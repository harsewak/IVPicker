package com.ivpicker;

/**
 * Created by harsewaksingh on 21/01/16.
 */
public enum IVType {
    IMAGES(0), VIDEOS(1), BOTH(2);
    int type;

    IVType(int type) {
        this.type = type;
    }

    /**
     * @return IMAGES - if type not matched
     */
    public static IVType toIVType(int type) {
        if (type == 0) {
            return IMAGES;
        } else if (type == 1) {
            return VIDEOS;
        } else if (type == 2) {
            return BOTH;
        }
        return IMAGES;
    }

    public int toInteger() {
        return type;
    }
}
