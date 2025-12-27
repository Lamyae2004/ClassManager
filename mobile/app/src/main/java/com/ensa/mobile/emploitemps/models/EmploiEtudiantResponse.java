package com.ensa.mobile.emploitemps.models;
import java.util.List;
import com.google.gson.annotations.SerializedName;

public class EmploiEtudiantResponse {

    @SerializedName("emploi")
    private List<EmploiWrapperDTO> emplois;

    public List<EmploiWrapperDTO> getEmplois() {
        return emplois;
    }
}
