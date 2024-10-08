package com.github.rexliu88.constant;

/**
 * 比赛结果类型
 */
public enum MatchResultsType {

    extraTimeFirstHalfScore("加时赛上半场比分"),
    extraTimeSecondHalfScore("加时赛下半场比分"),
    extraTimeScore("加时赛总比分"),

    firstHalfScore("常规赛上半场比分"),
    secondHalfScore("常规赛下半场比分"),
    matchScore("常规赛总比分"),
    ohtMinutesScore("120分钟比分"),
    penaltyScore("点球大战比分"),

    totalScore("总比分"), score("得分"), halfScore("半场得分"),

    red("红牌"), yellow("黄牌"),corner("角球"),
    rank("排名"),

    firstKickTeamCode("先开球队"),
    winCode("获胜方"),
    other("其他"),
    firstSection("第一节"),
    secondSection("第二节"),
    thirdSection("第三节"),
    fourthSection("第四节"),
    extraTime1("加时赛1"),
    extraTime2("加时赛2"),
    extraTime3("加时赛3"),
    extraTime4("加时赛4"),
    extraTime5("加时赛5"),
    extraTime6("加时赛6"),
    extraTime7("加时赛7"),
    extraTime8("加时赛8"),
    extraTime9("加时赛9"),
    extraTime10("加时赛10"),
    ;

    private String text;

    MatchResultsType(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

    public static String getMatchResultsNameByType(MatchResultsType type) {
        for (MatchResultsType a : MatchResultsType.values()) {
            if (a.getText().equals(type.getText())) {
                return a.getText();
            }
        }
        return "";
    }
}
