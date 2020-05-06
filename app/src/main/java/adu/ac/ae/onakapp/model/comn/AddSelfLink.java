
package adu.ac.ae.onakapp.model.comn;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class AddSelfLink {

    @SerializedName("href")
    @Expose
    private String href;

    public String getHref() {
        return href;
    }

    public void setHref(String href) {
        this.href = href;
    }

}
