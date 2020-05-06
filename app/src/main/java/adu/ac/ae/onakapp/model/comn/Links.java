
package adu.ac.ae.onakapp.model.comn;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Links {

    @SerializedName("self")
    @Expose
    private Self self;
    @SerializedName("annountments")
    @Expose
    private AddSelfLink annountments;

    public Self getSelf() {
        return self;
    }

    public void setSelf(Self self) {
        this.self = self;
    }

    public AddSelfLink getAnnountments() {
        return annountments;
    }

    public void setAnnountments(AddSelfLink annountments) {
        this.annountments = annountments;
    }

}
