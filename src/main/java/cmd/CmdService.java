package cmd;

import crawler.Checker;
import crawler.GithubMap;
import init.Initializer;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.apache.commons.lang3.StringUtils;

// 정보 출력 메소드 모음
@Slf4j
public class CmdService {

	/*
	 * 0. Libraries
	 */

	// 이름을 입력하면 깃헙 ID를 리턴
	public static String getGithubID(String name) {
		for(String[] s: Initializer.getMembers())
			if(s[0].equals(name) || s[0].substring(1).equals(name)) return s[1];
		return null;
	}



	/*
	 * 1. Single jandi map services
	 */

	// 커맨드를 요청한 사람의 잔디정보를 리턴
	public static String showJandiMapOfMe(SlashCommandInteractionEvent event) {

		// ID 구하기
		String myId = "daanta-real";

		// 종합잔디정보 리턴
		return showJandiMapById(myId);

	}

	// 특정 ID의 종합 잔디정보를 리턴
	public static String showJandiMapById(String id) { // id로만

		// 미입력 걸러내기
		if (StringUtils.isEmpty(id)) return "정확히 입력해 주세요.";

		// 종합잔디정보 리턴
		log.info("ID '{}'의 종합 잔디정보 호출을 명령받았습니다.", id);
		return GithubMap.getGithubInfoString(id, id);

	}

	// 특정 그룹원 이름으로 종합 잔디정보를 리턴
	public static String showJandiMapByName(String name) { // id로만

		// 미입력 걸러내기
		if (StringUtils.isEmpty(name)) return "정확히 입력해 주세요.";

		// ID 구하기
		String id = getGithubID(name);
		
		// 종합잔디정보 리턴
		log.info("그룹원 '{}' (ID '{}')의 종합 잔디정보 호출을 명령받았습니다.", name, id);
		return GithubMap.getGithubInfoString(name, id);

	}



	/*
	 * 2. Jandi map list services
	 */

	// 어제 커밋 안 한 스터디원 목록을 리턴
	public static String showNotCommittedYesterday() throws Exception {
		return Checker.getNotCommittedYesterday();
	}

	// 어제 커밋 한 스터디원 목록을 리턴
	public static String showDidCommitYesterday() throws Exception {
		return Checker.getDidCommitYesterday();
	}

	// 현시각 기준 오늘 아직 커밋 한 스터디원 목록을 리턴
	public static String showDidCommitToday() throws Exception {
		return Checker.getDidCommittedToday();
	}

	// 특정일에 커밋 한 스터디원 목록을 리턴
	public static String showDidCommitSomeday(String date) throws Exception {
		return Checker.getDidCommittedSomeday(date);
	}

}
