package com.github.rexliu88.constant;

public enum LeiSuResType {
    job("调度"),
    category("分类"),
    competition("赛事"),
    country("国家"),
    manager("教练"),
    match("比赛"),
    player("球员"),
    season("赛季"),
    stage("阶段"),
    group("分组"),
    team("球队"),
    venue("场地"),
    teamSquad("球队阵容"),
    competitionRule("赛事规则"),
    matchDetailLive("比赛实时"),
    referee("裁判"),
    teamInjury("球队伤停"),
    teamHonor("球队荣誉"),
    playerHonor("球员荣誉"),
    matchUp("比赛补偿")
    ;
    LeiSuResType(String text) {
        this.text = text;
    }
    private String text;
    public String getText() {
        return text;
    }
}
