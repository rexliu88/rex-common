package com.github.rexliu88.constant;

import java.util.Arrays;

/**
 * 赛事项目
 */
public enum Item {

    football("1", "足球"),
    basketball("2", "篮球"),
    combat("3", "格斗"),
    other("0", "其他");

    private String itemId;
    private String text;

    Item(String itemId, String text) {
        this.itemId = itemId;
        this.text = text;
    }

    public String getItemId() {
        return itemId;
    }

    public String getText() {
        return text;
    }

    /**
     * itemId 转换为item枚举
     *
     * @param itemId
     * @return
     */
    public static Item parse(final String itemId) {
        return Arrays.stream(Item.values())
                .filter(x -> x.itemId.equals(itemId)).findFirst()
                .orElse(null);
    }

}
