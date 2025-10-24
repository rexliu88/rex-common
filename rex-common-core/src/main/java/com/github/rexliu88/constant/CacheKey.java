package com.github.rexliu88.constant;

/**
 * 缓存key枚举
 */
public enum CacheKey {
    name("名称"),
    relationId("关系id"),
    parentId("父id"),
    parentCacheType("父类型"),
    categoryId("分类id"),
    categoryName("分类名称"),

    categoryContinent("分类大洲"),
    competitionId("赛事"),
    competitionName("赛事名称"),
    competitionRule("赛事规则"),
    countryId("国家"),
    countryName("国家名称"),
    managerId("教练"),
    managerName("教练姓名"),
    matchId("比赛"),
    matchName("比赛名称"),
    matchHasLineup("比赛是否有阵容"),
    playerId("球员"),
    playerName("球员姓名"),
    seasonId("赛季"),
    seasonName("赛季名称"),
    stageId("阶段"),
    stageName("阶段名称"),
    groupId("分组"),
    groupName("分组名称"),
    teamId("球队"),
    teamName("球队名称"),
    venueId("场地"),
    venueName("场地名称"),
    homeTeamId("主队球队"),
    homeTeamName("主队名称"),
    awayTeamId("客队球队"),
    awayTeamName("客队名称")
            ;
    CacheKey(String text) {
        this.text = text;
    }
    private String text;
    public String getText() {
        return text;
    }
}
