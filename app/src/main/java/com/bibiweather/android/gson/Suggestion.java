package com.bibiweather.android.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by 门一 on 2018/5/26.
 */

public class Suggestion {

    @SerializedName("drsg")
    public Drsg drsg;//穿衣指数

    public class Drsg{
        @SerializedName("txt")
        public String info;
    }

    @SerializedName("flu")
    public Flu flu;//感冒指数

    public class Flu{

        @SerializedName("txt")
        public String info;
    }

    @SerializedName("trav")
    public Trav trav;//旅游指数

    public class Trav{

        @SerializedName("txt")
        public String info;
    }

    @SerializedName("uv")
    public Uv uv;//紫外线指数

    public class Uv{

        @SerializedName("txt")
        public String info;
    }


    @SerializedName("comf")
    public Comfort comfort;

    @SerializedName("cw")
    public CarWash carWash;

    public Sport sport;

    public class Comfort{
        @SerializedName("txt")
        public String info;
    }

    public class CarWash{
        @SerializedName("txt")
        public String info;
    }
    public class Sport{
        @SerializedName("txt")
        public String info;
    }
}
