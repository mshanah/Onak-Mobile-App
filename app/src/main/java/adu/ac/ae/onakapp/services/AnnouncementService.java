package adu.ac.ae.onakapp.services;

import adu.ac.ae.onakapp.model.Annountmentse;
import adu.ac.ae.onakapp.model.comn.SpringRestResponse;
import retrofit2.Call;
import retrofit2.http.GET;

public interface AnnouncementService {
    @GET("/api/public/anns")
    Call<SpringRestResponse<Annountmentse>> listRepos();
}
