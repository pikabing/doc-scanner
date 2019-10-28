package com.technology.singularium.api;

public class APIUtils {

    private APIUtils(){
    }
    public static final String API_URL = "http://e90cvm.southeastasia.cloudapp.azure.com:8000/";
    public static FileService getFileService(){
        return  RetroFitClient.getClient(API_URL).create(FileService.class);
    }
}
