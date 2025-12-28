package com.ensa.mobile.emploitemps.models;

import com.google.gson.annotations.SerializedName;

public class EmploiWrapperDTO {

    @SerializedName("emploi")
    private EmploiDuTempsDTO emploi;

    @SerializedName("profNom")
    private String profNom;

    public EmploiDuTempsDTO toEmploiDTO() {
        emploi.setProfNom(profNom);
        return emploi;
    }
}
