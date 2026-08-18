package com.webgis.map.finalmap;

public enum MapTag {
    DRY("dry"), WET("wet"), ALL_SEASON("all_season"), EBOLA("ebola"), RIFT_VALLEY_FEVER("rift_valley_fever"), OTHER("other");

    final private String value;

    MapTag(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static MapTag fromValue(String value) {
        for (MapTag tag : values()) {
            if (tag.value.equalsIgnoreCase(value)) {
                return tag;
            }
        }
        throw new IllegalArgumentException("Unknown tag: " + value);
    }
}