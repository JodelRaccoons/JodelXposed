
package com.jodelXposed.retrofit.Response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class HooksResponse {

    @SerializedName("methods")
    @Expose
    private Methods methods;
    @SerializedName("classes")
    @Expose
    private Classes classes;
    @SerializedName("updatemessage")
    @Expose
    private String updatemessage;

    /**
     *
     * @return
     *     The methods
     */
    public Methods getMethods() {
        return methods;
    }

    /**
     *
     * @param methods
     *     The methods
     */
    public void setMethods(Methods methods) {
        this.methods = methods;
    }

    /**
     *
     * @return
     *     The classes
     */
    public Classes getClasses() {
        return classes;
    }

    /**
     *
     * @param classes
     *     The classes
     */
    public void setClasses(Classes classes) {
        this.classes = classes;
    }

    /**
     *
     * @return
     *     The updatemessage
     */
    public String getUpdatemessage() {
        return updatemessage;
    }

    /**
     *
     * @param updatemessage
     *     The updatemessage
     */
    public void setUpdatemessage(String updatemessage) {
        this.updatemessage = updatemessage;
    }

}
