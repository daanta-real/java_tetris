package jda.interaction;

import cmd.CmdService;
import init.Initializer;
import jda.JDAController;
import jda.menu.ModalMenu;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import org.apache.commons.lang3.StringUtils;

@Slf4j
public class ButtonInteraction {

    public static void run(ButtonInteractionEvent event) {

        // Get command and options
        String cmd = event.getComponentId(); // Command
        log.debug("[[[ 버튼 누르기로 명령을 접수하였습니다. ]]] [{}]", cmd);

        String result = null;
        try {

            // Run each command
            switch(cmd) {
                case JDAController.CMD_ME -> {
                    // Interaction을 보내준 객체를 spinner로 바꾼다.
                    // 여기에서 deferReply()로 하면, 답신이 메세지가 아닌 답글 형태로만 달리게 되고
                    // "원본 메시지가 삭제되었어요"라는 꼬리표가 붙고 또 spinner도 없어지지 않는다.
                    // deferEdit()를 사용해야 되는 것 같다.
                    event.deferEdit().queue();
                    result = CmdService.getJandiMapStringOfMe(event.getUser()); // 내 잔디정보를 획득
                }
                case JDAController.CMD_JANDIYA -> ModalMenu.getChatAnswer(event); // 일반적인 질문에 답하는 AI
                case JDAController.CMD_LIST_YESTERDAY_SUCCESS -> {
                    event.deferEdit().queue(); // Set defer
                    result = CmdService.getDidCommitStringYesterday(); // 어제 잔디심기 한 그룹원 목록 출력
                }
                case JDAController.CMD_LIST_TODAY_SUCCESS -> {
                    event.deferEdit().queue(); // Set defer
                    result = CmdService.getDidCommitStringToday(); // 오늘 잔디심기 한 그룹원 목록 출력
                }
                case JDAController.CMD_NAME -> ModalMenu.showJandiMapByName(event); // 특정 이름의 그룹원의 종합 잔디정보 출력
                case JDAController.CMD_ID -> ModalMenu.showJandiMapById(event); // 특정 Github ID의 종합 잔디정보 출력
                case JDAController.CMD_LIST_YESTERDAY_FAIL -> {
                    event.deferEdit().queue(); // Set defer
                    result = CmdService.getNotCommittedStringYesterday(); // 어제 잔디심기 안 한 그룹원 목록 출력
                }
                case JDAController.CMD_LIST_BY_DATE -> ModalMenu.showDidCommitSomeday(event); // 특정 날짜에 잔디를 심은 그룹원 목록 출력
                case JDAController.CMD_TRANSLATE_EN_TO_KR -> ModalMenu.showTranslate_EN_to_KR(event); // 영한 번역
                case JDAController.CMD_TRANSLATE_KR_TO_EN -> ModalMenu.showTranslate_KR_to_EN(event); // 한영 번역
                case JDAController.CMD_ABOUT -> {
                    event.deferEdit().queue(); // Set defer
                    result = Initializer.INFO_STRING; // 소개말
                }
                case JDAController.CMD_CLOSE -> {
                    event.getMessage().delete().queue();
                    return;
                }
                default -> throw new Exception();
            }

        } catch(Exception e) {
            result = "정보 획득에 실패하였습니다.";
        }

        // 위의 switch문에서 모달 호출 없이 단순 String만 가져왔다면 여기가 실행된다.
        // 반대로 모달 입력 등이 있다면 여기를 실행하지 않는다(result가 초기값인 null이므로).
        if(!StringUtils.isEmpty(result)) {

            // Show result
            // event.reply(result).queue();
            event.getChannel().sendMessage(result).queue();

            // Remove defer message and its button menu panel
            // event.getHook().sendMessage(result).queue();
            // event.getHook().editOriginal(result).queue();
            event.getMessage().delete().queue(); // 메뉴+오리지널 메세지 지우기

        }

    }

}
