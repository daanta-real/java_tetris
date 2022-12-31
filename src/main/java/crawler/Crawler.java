package crawler;

import lombok.extern.slf4j.Slf4j;
import utils.CommonUtils;

import java.util.*;

// 깃헙 긁어오기 관련 모든 소스코드. 크롤링 및 데이터 획득
@Slf4j
public class Crawler {

	// Return the GitHub profile page of someone
	public static String getHTML(String id) throws Exception {
		return CommonUtils.httpRequestUrl_GET("https://github.com/" + id);
	}

	// 수신된 웹 데이터 1차 trim (불필요 태그들 제거)
	static String trim(String str) {
		int st = str.indexOf("js-calendar-graph-svg") + 26;
		int ed = str.indexOf("<text ");
		str = str.substring(st, ed);
		return str.replaceAll("^(<rect).*(data-)$", "")
		;
	}

	// 수신된 웹 데이터 2차 trim (내부 태그 정돈)
	static String makeDataCSV(String str) {

		return str

		// 1차 내부 트림
		.replaceAll("^.*(<g.*>).*\n|.*</?g.*\n|(.*data-count=\"\\d\"\\s)|(></).*(rect>)|(\\s.*<rect).*count=\"\\d\\d\"\\s", "") // 무관문자열 all삭제
		.replaceAll("\"d", "\"\nd")             // 엔터키 안쳐진거 엔터치기

		// 데이터 좌우 트림
		.replaceAll("data-date=\"", "")    // 왼쪽부분
		.replaceAll("\" data-level=", ",") // 오른쪽부분

		// 잔디심은 결과에 따른 트림
		.replaceAll("\"[1-9]\"\n", "1\n")  // true의 경우
		.replaceAll("\"0\"\n", "0\n")      // false의 경우

		.replaceAll("\\n\\s.*", "")
		;

	}

	// 완성된 CSV를 배열로 변환 후 리턴
	static Map<String, Boolean> CSVtoHashMap(String csv) {

		String[] csvArr = csv.split("\n");
		List<String> list = new ArrayList<>(Arrays.asList(csvArr));
		Map<String, Boolean> map = new TreeMap<>();
		for(String lis: list) {
			String[] keyValStr = lis.split(",");
			// 날짜 별 커밋 농도를 콘솔에 표시
			// log.info(Arrays.toString(keyValStr));
			// 날짜 찾기
			String dateStr = keyValStr[0];
			// 잔디여부 찾기
			Boolean val = !"0".equals(keyValStr[1]);
			map.put(dateStr, val);
		}
		return map;
	}

	// ID를 넘기면 일일 잔디현황을 Map으로 리턴
	public static Map<String, Boolean> getGithubMap(String githubId) throws Exception {
		String str               = getHTML     (githubId);
		String trimmed           = trim        (str)     ;
		String csv               = makeDataCSV (trimmed) ;
		return CSVtoHashMap(csv);
	}
}