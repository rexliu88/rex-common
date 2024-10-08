package com.github.rexliu88.common;

import cn.hutool.core.util.StrUtil;
import com.github.rexliu88.constant.MatchResultsType;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

/**
 * 资源Id包装类
 */
@Slf4j
public class ResId {
    public static final String resIdPrefix = "res_";
    public static final String resIdSpiltChar = "_";

    public static String getResId(Integer sourceId, String id) {
        if (isResId(sourceId, id)) {
            return id;
        }
        if(StrUtil.isAllBlank(id)){
            return null;
        }
        return resIdPrefix + sourceId.toString() + resIdSpiltChar + id;
    }

    public static String getId(Integer sourceId, String resId) {
        if (!isResId(sourceId, resId)) {
            return resId;
        }
        return StrUtil.removePrefix(resId, resIdPrefix + sourceId.toString() + resIdSpiltChar);
    }

    public static Boolean isResId(Integer sourceId, String resId) {
        //log.info(" sourceId : {} ,resId : {} ", sourceId, resId);
        if (resId.indexOf(resIdPrefix + sourceId.toString() + resIdSpiltChar) > -1) {
            return true;
        }
        return false;
    }

    public static String getResPersonId(Integer sourceId, String id, Integer personType) {
        if (isResPersonId(sourceId, id, personType)) {
            return id;
        }
        return resIdPrefix + sourceId.toString() + resIdSpiltChar + personType.toString() + resIdSpiltChar + id;
    }

    public static String getPersonId(Integer sourceId, String resId, Integer personType) {
        if (!isResPersonId(sourceId, resId, personType)) {
            return resId;
        }
        return StrUtil.removePrefix(resId, resIdPrefix + sourceId.toString() + resIdSpiltChar + personType.toString() + resIdSpiltChar);
    }

    public static Boolean isResPersonId(Integer sourceId, String resId, Integer personType) {
        if (resId.indexOf(resIdPrefix + sourceId.toString() + resIdSpiltChar + personType.toString() + resIdSpiltChar) > -1) {
            return true;
        }
        return false;
    }
    /**
     * 获取 比赛成员Id
     * @param sourceId 来源
     * @param matchId  比赛Id
     * @param index    成员序号  从1开始
     * @return 比赛成员Id
     */
    public static String getResMatchMemberId(Integer sourceId, String matchId, Integer index) {
        if (isResMatchMemberId(sourceId, matchId)) {
            return matchId;
        }
        Integer match_id = 0;
        if (isResId(sourceId, matchId)) {
            match_id = Integer.valueOf(ResId.getId(sourceId, matchId));
        } else {
            match_id = Integer.valueOf(matchId);
        }
        return resIdPrefix + sourceId.toString() + resIdSpiltChar + match_id.toString() + resIdSpiltChar + index.toString();
    }

    /**
     * 获取 比赛成员序号
     * 序号 从1开始
     * @param sourceId 来源
     * @param resMatchMemberId 比赛成员Id
     * @return 比赛成员的序号
     */
    public static Integer getMatchMemberIdIndex(Integer sourceId, String resMatchMemberId) {
        if (!isResMatchMemberId(sourceId, resMatchMemberId)) {
            return null;
        }
        String[] matchIdArr = resMatchMemberId.split(resIdSpiltChar);
        if(matchIdArr.length >= 4){
            List<String> matchIdStrList = new ArrayList<>();
            int i = 0;
            for(String matchId : matchIdArr){
                i++;
                if(i < 5) {
                    matchIdStrList.add(matchId);
                }
            }
            String indexStr = matchIdStrList.get(3);
            Integer indexInt = Integer.valueOf(indexStr);
            if (indexInt != null && indexInt > 0) {
                return indexInt;
            }
        }
        return null;
    }
    /**
     * 是否成员序号
     * @param sourceId
     * @param resMatchMemberId
     * @return
     */
    public static Boolean isResMatchMemberId(Integer sourceId, String resMatchMemberId) {
        // MatchMemberId
        // res +"_"+ sourceId +"_"+ matchId+"_"+ index
        // 4
        // MatchResultsId
        // res +"_"+ sourceId +"_"+ matchId+"_"+ index +"_"+res.getResultsType()
        // 5
        if (!isResId(sourceId, resMatchMemberId)) {
            return false;
        }
        String[] matchIdArr = resMatchMemberId.split(resIdSpiltChar);
        if (matchIdArr.length == 4) {
            String match_id = resMatchMemberId.substring((resIdPrefix + sourceId.toString() + resIdSpiltChar).length());
            match_id = match_id.substring(0, match_id.indexOf(resIdSpiltChar));
            String indexStr = resMatchMemberId.substring((resIdPrefix + sourceId.toString() + resIdSpiltChar + match_id + resIdSpiltChar).length());
            Integer matchId = Integer.valueOf(match_id);
            Integer indexInt = Integer.valueOf(indexStr);
            if (matchId != null && matchId > 0 && indexInt > 0) {
                return true;
            }
        }
        return false;
    }
    /**
     * 获取 比赛成员Id
     * @param sourceId 来源
     * @param matchId  比赛Id
     * @param index    成员序号  从1开始
     * @return 比赛成员Id
     */
    public static String getResMatchResultsId(Integer sourceId, String matchId, Integer index, MatchResultsType matchResultsType) {
        if (isResMatchResultsId(sourceId, matchId)) {
            return matchId;
        }
        Integer match_id = 0;
        if (isResId(sourceId, matchId)) {
            match_id = Integer.valueOf(ResId.getId(sourceId, matchId));
        } else {
            match_id = Integer.valueOf(matchId);
        }
        return resIdPrefix + sourceId.toString() + resIdSpiltChar + match_id.toString() + resIdSpiltChar + index.toString() + resIdSpiltChar + matchResultsType.name();
    }

    /**
     * 获取 比赛结果中 结果类型
     * 序号 从1开始
     * @param sourceId 来源
     * @param resMatchResultsId 比赛结果Id
     * @return 比赛结果Id中结果类型
     */
    public static MatchResultsType getResMatchResultsIdMatchResultsType(Integer sourceId, String resMatchResultsId) {
        if (!isResMatchMemberId(sourceId, resMatchResultsId)) {
            return null;
        }
        String[] matchIdArr = resMatchResultsId.split(resIdSpiltChar);
        if (matchIdArr.length == 5) {
            List<String> matchIdStrList = new ArrayList<>();
            int i = 0;
            for(String matchId : matchIdArr){
                i++;
                if(i <= 5) {
                    matchIdStrList.add(matchId);
                }
            }
            String matchResultsTypeStr = matchIdStrList.get(4);
            if (StrUtil.isAllNotBlank(matchResultsTypeStr)) {
                try {
                    return MatchResultsType.valueOf(matchResultsTypeStr);
                } catch (Exception exception){
                    return null;
                }
            }
        }
        return null;
    }

    /**
     * 获取 比赛结果中成员序号
     * @param sourceId
     * @param resMatchResultsId
     * @return
     */
    public static Integer getResMatchResultsIdIndex(Integer sourceId, String resMatchResultsId) {
        return getMatchMemberIdIndex(sourceId,resMatchResultsId);
    }
    /**
     * 是否成员序号
     * @param sourceId
     * @param resMatchMemberId
     * @return
     */
    public static Boolean isResMatchResultsId(Integer sourceId, String resMatchMemberId) {
        // MatchMemberId
        // res +"_"+ sourceId +"_"+ matchId+"_"+ index
        // 4
        // MatchResultsId
        // res +"_"+ sourceId +"_"+ matchId+"_"+ index +"_"+res.getResultsType()
        // 5
        if (!isResId(sourceId, resMatchMemberId)) {
            return false;
        }
        String[] matchIdArr = resMatchMemberId.split(resIdSpiltChar);
        if (matchIdArr.length == 5) {
            List<String> matchIdStrList = new ArrayList<>();
            int i = 0;
            for(String matchId : matchIdArr){
                i++;
                if(i <= 5) {
                    matchIdStrList.add(matchId);
                }
            }
            String match_id = matchIdStrList.get(2);
            Integer matchId = Integer.valueOf(match_id);
            String indexStr = matchIdStrList.get(3);
            Integer indexInt = Integer.valueOf(indexStr);
            String matchResultsTypeStr = matchIdStrList.get(4);
            MatchResultsType matchResultsTypeValue = null;
            if (StrUtil.isAllNotBlank(matchResultsTypeStr)) {
                try {
                    matchResultsTypeValue = MatchResultsType.valueOf(matchResultsTypeStr);
                } catch (Exception exception){
                    matchResultsTypeValue = null;
                }
            }
            if (matchId != null && matchId > 0 && indexInt > 0 && matchResultsTypeValue != null) {
                return true;
            }
        }
        return false;
    }
}
