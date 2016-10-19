package com.jodelXposed.retrofit.Response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class VersionResponse {

    @SerializedName("versioncode")
    @Expose
    private Integer versioncode;

    /**
     *
     * @return
     * The versioncode
     */
    public Integer getVersioncode() {
        return versioncode;
    }

    /**
     *
     * @param versioncode
     * The versioncode
     */
    public void setVersioncode(Integer versioncode) {
        this.versioncode = versioncode;
    }

}
