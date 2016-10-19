
package com.jodelXposed.retrofit.Response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Classes {

    @SerializedName("Class_Storage")
    @Expose
    private String classStorage;
    @SerializedName("Class_PhotoEditFragment")
    @Expose
    private String classPhotoEditFragment;
    @SerializedName("Class_PostDetailRecyclerAdapter")
    @Expose
    private String classPostDetailRecyclerAdapter;
    @SerializedName("Class_CreateTextPostFragment")
    @Expose
    private String classCreateTextPostFragment;
    @SerializedName("Class_MyMenuPresenter")
    @Expose
    private String classMyMenuPresenter;
    @SerializedName("Class_MyGcmListenerService")
    @Expose
    private String classMyGcmListenerService;
    @SerializedName("Class_UniqueDeviceIdentifier")
    @Expose
    private String classUniqueDeviceIdentifier;

    /**
     *
     * @return
     *     The classStorage
     */
    public String getClassStorage() {
        return classStorage;
    }

    /**
     *
     * @param classStorage
     *     The Class_Storage
     */
    public void setClassStorage(String classStorage) {
        this.classStorage = classStorage;
    }

    /**
     *
     * @return
     *     The classPhotoEditFragment
     */
    public String getClassPhotoEditFragment() {
        return classPhotoEditFragment;
    }

    /**
     *
     * @param classPhotoEditFragment
     *     The Class_PhotoEditFragment
     */
    public void setClassPhotoEditFragment(String classPhotoEditFragment) {
        this.classPhotoEditFragment = classPhotoEditFragment;
    }

    /**
     *
     * @return
     *     The classPostDetailRecyclerAdapter
     */
    public String getClassPostDetailRecyclerAdapter() {
        return classPostDetailRecyclerAdapter;
    }

    /**
     *
     * @param classPostDetailRecyclerAdapter
     *     The Class_PostDetailRecyclerAdapter
     */
    public void setClassPostDetailRecyclerAdapter(String classPostDetailRecyclerAdapter) {
        this.classPostDetailRecyclerAdapter = classPostDetailRecyclerAdapter;
    }

    /**
     *
     * @return
     *     The classCreateTextPostFragment
     */
    public String getClassCreateTextPostFragment() {
        return classCreateTextPostFragment;
    }

    /**
     *
     * @param classCreateTextPostFragment
     *     The Class_CreateTextPostFragment
     */
    public void setClassCreateTextPostFragment(String classCreateTextPostFragment) {
        this.classCreateTextPostFragment = classCreateTextPostFragment;
    }

    /**
     *
     * @return
     *     The classMyMenuPresenter
     */
    public String getClassMyMenuPresenter() {
        return classMyMenuPresenter;
    }

    /**
     *
     * @param classMyMenuPresenter
     *     The Class_MyMenuPresenter
     */
    public void setClassMyMenuPresenter(String classMyMenuPresenter) {
        this.classMyMenuPresenter = classMyMenuPresenter;
    }

    /**
     *
     * @return
     *     The classMyGcmListenerService
     */
    public String getClassMyGcmListenerService() {
        return classMyGcmListenerService;
    }

    /**
     *
     * @param classMyGcmListenerService
     *     The Class_MyGcmListenerService
     */
    public void setClassMyGcmListenerService(String classMyGcmListenerService) {
        this.classMyGcmListenerService = classMyGcmListenerService;
    }

    /**
     *
     * @return
     *     The classUniqueDeviceIdentifier
     */
    public String getClassUniqueDeviceIdentifier() {
        return classUniqueDeviceIdentifier;
    }

    /**
     *
     * @param classUniqueDeviceIdentifier
     *     The Class_UniqueDeviceIdentifier
     */
    public void setClassUniqueDeviceIdentifier(String classUniqueDeviceIdentifier) {
        this.classUniqueDeviceIdentifier = classUniqueDeviceIdentifier;
    }

}
