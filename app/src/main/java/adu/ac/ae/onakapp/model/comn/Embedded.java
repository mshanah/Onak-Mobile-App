
package adu.ac.ae.onakapp.model.comn;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Embedded<T> {

    @SerializedName("annountmentses")
    @Expose
    private List<T> annountmentses = null;

    public List<T> getAnnountmentses() {
        return annountmentses;
    }

    public void setAnnountmentses(List<T> annountmentses) {
        this.annountmentses = annountmentses;
    }

}
