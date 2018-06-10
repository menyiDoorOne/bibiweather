package com.bibiweather.android.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by 门一 on 2018/5/26.
 */

public class Now {
    @SerializedName("tmp")
    public String temperature;

    @SerializedName("cond")
    public More more;

    public class More{

        @SerializedName("txt")
        public String info;
    }
}
