package com.dailyselfie.model.mediator.webdata;

import java.util.Collection;
import java.util.List;

import retrofit.client.Response;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.Multipart;
import retrofit.http.POST;
import retrofit.http.Part;
import retrofit.http.Path;
import retrofit.http.Query;
import retrofit.http.Streaming;
import retrofit.mime.TypedFile;

/**
 * This interface defines an API for a Selfie Service web service.  The
 * interface is used to provide a contract for client/server
 * interactions.  The interface is annotated with Retrofit annotations
 * to send Requests and automatically convert the Selfie.
 */
public interface SelfieServiceProxy {

    String TOKEN_PATH = "/oauth/token";
    /**
     * Used as Request Parameter for Selfie data.
     */
    String DATA_PARAMETER = "data";

    /**
     * Used as Request Parameter for SelfieId.
     */
    String ID_PARAMETER = "id";

    /**
     * The path where we expect the SelfieSvc to live.
     */
    String SELFIE_SVC_PATH = "/selfie";

    /**
     * The path where we expect the SelfieSvc to live.
     */
    String SELFIE_DATA_PATH =
            SELFIE_SVC_PATH
                    + "/{"
                    + SelfieServiceProxy.ID_PARAMETER
                    + "}/data";

    String SELFIE_FILTER_PATH = SELFIE_SVC_PATH + "/filter";

    String SELFIE_DELETE_PATH = SELFIE_SVC_PATH  + "/{"
            + SelfieServiceProxy.ID_PARAMETER
            + "}/";

    String FILTER_PARAMETER = "filters";

    /**
     * Sends a GET request to get the List of Selfies from Selfie
     * Web service using a two-way Retrofit RPC call.
     */
    @GET(SELFIE_SVC_PATH)
    Collection<SelfieRecord> getSelfieList();

    /**
     * Sends a POST request to add the Selfie metadata to the Selfie
     * Web service using a two-way Retrofit RPC call.
     *
     * @param selfie meta-data
     * @return Updated selfie meta-data returned from the Selfie Service.
     */
    @POST(SELFIE_SVC_PATH)
    SelfieRecord addSelfie(@Body SelfieRecord selfie);

    /**
     * Sends a POST request to Upload the Selfie data to the Selfie Web
     * service using a two-way Retrofit RPC call.  @Multipart is used
     * to transfer multiple content (i.e. several files in case of a
     * file upload to a server) within one request entity.  When doing
     * so, a REST client can save the overhead of sending a sequence
     * of single requests to the server, thereby reducing network
     * latency.
     *
     * @param id
     * @param selfieData
     * @return selfieStatus indicating status of the uploaded selfie.
     */
    @Multipart
    @POST(SELFIE_DATA_PATH)
    SelfieStatus setSelfieData(@Path(ID_PARAMETER) long id,
                               @Part(DATA_PARAMETER) TypedFile selfieData);

    /**
     * This method uses Retrofit's @Streaming annotation to indicate
     * that the method is going to access a large stream of data
     * (e.g., the mpeg selfie data on the server).  The client can
     * access this stream of data by obtaining an InputStream from the
     * Response as shown below:
     * <p/>
     * SelfieServiceProxy client = ... // use retrofit to create the client
     * Response response = client.getData(someSelfieId);
     * InputStream selfieDataStream = response.getBody().in();
     *
     * @param id
     * @return Response which contains the actual Selfie data.
     */
    @Streaming
    @GET(SELFIE_DATA_PATH)
    Response getData(@Path(ID_PARAMETER) long id);

    @Multipart
    @POST(SELFIE_FILTER_PATH)
    Response applyFilters(@Query(FILTER_PARAMETER) List<String> filterCommands,
                          @Part(DATA_PARAMETER) TypedFile selfieData);


    @POST(SELFIE_DELETE_PATH)
    Response delete(@Path(ID_PARAMETER) long id);

}
