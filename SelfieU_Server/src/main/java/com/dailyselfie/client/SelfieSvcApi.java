package com.dailyselfie.client;

import com.dailyselfie.repository.SelfieRecord;
import com.dailyselfie.repository.SelfieStatus;

import java.util.Collection;
import java.util.List;

import retrofit.client.Response;
import retrofit.http.Body;
import retrofit.http.DELETE;
import retrofit.http.GET;
import retrofit.http.Multipart;
import retrofit.http.POST;
import retrofit.http.Part;
import retrofit.http.Path;
import retrofit.http.Query;
import retrofit.http.Streaming;
import retrofit.mime.TypedFile;

/**
 * DO NOT MODIFY THIS INTERFACE
 * ___                    ___           ___
 * _____         /\  \                  /\  \         /\  \
 * /::\  \       /::\  \                 \:\  \       /::\  \         ___
 * /:/\:\  \     /:/\:\  \                 \:\  \     /:/\:\  \       /\__\
 * /:/  \:\__\   /:/  \:\  \            _____\:\  \   /:/  \:\  \     /:/  /
 * /:/__/ \:|__| /:/__/ \:\__\          /::::::::\__\ /:/__/ \:\__\   /:/__/
 * \:\  \ /:/  / \:\  \ /:/  /          \:\~~\~~\/__/ \:\  \ /:/  /  /::\  \
 * \:\  /:/  /   \:\  /:/  /            \:\  \        \:\  /:/  /  /:/\:\  \
 * \:\/:/  /     \:\/:/  /              \:\  \        \:\/:/  /   \/__\:\  \
 * \::/  /       \::/  /                \:\__\        \::/  /         \:\__\
 * \/__/         \/__/                  \/__/         \/__/           \/__/
 * ___           ___                                     ___
 * /\  \         /\  \         _____                     /\__\
 * |::\  \       /::\  \       /::\  \       ___         /:/ _/_         ___
 * |:|:\  \     /:/\:\  \     /:/\:\  \     /\__\       /:/ /\__\       /|  |
 * __|:|\:\  \   /:/  \:\  \   /:/  \:\__\   /:/__/      /:/ /:/  /      |:|  |
 * /::::|_\:\__\ /:/__/ \:\__\ /:/__/ \:|__| /::\  \     /:/_/:/  /       |:|  |
 * \:\~~\  \/__/ \:\  \ /:/  / \:\  \ /:/  / \/\:\  \__  \:\/:/  /      __|:|__|
 * \:\  \        \:\  /:/  /   \:\  /:/  /   ~~\:\/\__\  \::/__/      /::::\  \
 * \:\  \        \:\/:/  /     \:\/:/  /       \::/  /   \:\  \      ~~~~\:\  \
 * \:\__\        \::/  /       \::/  /        /:/  /     \:\__\          \:\__\
 * \/__/         \/__/         \/__/         \/__/       \/__/           \/__/
 * <p/>
 * <p/>
 * /**
 * This interface defines an API for a SelfieSvc. The
 * interface is used to provide a contract for client/server
 * interactions. The interface is annotated with Retrofit
 * annotations so that clients can automatically convert the
 * interface into a client capable of sending the appropriate
 * HTTP requests.
 * <p/>
 * The HTTP API that you must implement so that this interface
 * will work:
 * <p/>
 * POST /oauth/token
 * - The access point for the OAuth 2.0 Password Grant flow.
 * - Clients should be able to submit a request with their username, password,
 * client ID, and client secret, encoded as described in the OAuth lecture
 * selfies.
 * - The client ID for the Retrofit adapter is "mobile" with an empty password.
 * - There must be 2 users, whose usernames are "user0" and "admin". All passwords
 * should simply be "pass".
 * - Rather than implementing this from scratch, we suggest reusing the example
 * configuration from the OAuth 2.0 example in GitHub by copying these classes over:
 * https://github.com/juleswhite/mobilecloud-14/tree/master/examples/9-SelfieServiceWithOauth2/src/main/java/org/magnum/mobilecloud/selfie/auth
 * You will need to @Import the OAuth2SecurityConfiguration into your Application or
 * other configuration class to enable OAuth 2.0. You will also need to remove one
 * of the containerCustomizer() methods in either OAuth2SecurityConfiguration or
 * Application (they are the exact same code). You may need to customize the users
 * in the OAuth2Config constructor or the security applied by the ResourceServer.configure(...)
 * method. You should determine what (if any) adaptations are needed by comparing this
 * and the test specification against the code in that class.
 * <p/>
 * GET /selfie
 * - Returns the list of selfies that have been added to the
 * server as JSON. The list of selfies should be persisted
 * using Spring Data. The list of Selfie objects should be able
 * to be unmarshalled by the client into a Collection<Selfie>.
 * - The return content-type should be application/json, which
 * will be the default if you use @ResponseBody
 * <p/>
 * <p/>
 * POST /selfie
 * - The selfie metadata is provided as an application/json request
 * body. The JSON should generate a valid instance of the
 * Selfie class when deserialized by Spring's default
 * Jackson library.
 * - Returns the JSON representation of the Selfie object that
 * was stored along with any updates to that object made by the server.
 * - **_The server should store the Selfie in a Spring Data JPA repository.
 * If done properly, the repository should handle generating ID's._**
 * - A selfie should not have any likes when it is initially created.
 * - You will need to add one or more annotations to the Selfie object
 * in order for it to be persisted with JPA.
 * <p/>
 * GET /selfie/{id}
 * - Returns the selfie with the given id or 404 if the selfie is not found.
 * <p/>
 * POST /selfie/{id}/like
 * - Allows a user to like a selfie. Returns 200 Ok on success, 404 if the
 * selfie is not found, or 400 if the user has already liked the selfie.
 * - The service should should keep track of which users have liked a selfie and
 * prevent a user from liking a selfie twice. A POJO Selfie object is provided for
 * you and you will need to annotate and/or add to it in order to make it persistable.
 * - A user is only allowed to like a selfie once. If a user tries to like a selfie
 * a second time, the operation should fail and return 400 Bad Request.
 * <p/>
 * POST /selfie/{id}/unlike
 * - Allows a user to unlike a selfie that he/she previously liked. Returns 200 OK
 * on success, 404 if the selfie is not found, and a 400 if the user has not
 * previously liked the specified selfie.
 * <p/>
 * GET /selfie/{id}/likedby
 * - Returns a list of the string usernames of the users that have liked the specified
 * selfie. If the selfie is not found, a 404 error should be generated.
 * <p/>
 * GET /selfie/search/findByName?title={title}
 * - Returns a list of selfies whose titles match the given parameter or an empty
 * list if none are found.
 * <p/>
 * GET /selfie/search/findByDurationLessThan?duration={duration}
 * - Returns a list of selfies whose durations are less than the given parameter or
 * an empty list if none are found.
 * <p/>
 * <p/>
 * The SelfieSvcApi interface described below should be used as the ultimate ground
 * truth for what should be implemented in the assignment. If there are any details
 * in the description above that conflict with the SelfieSvcApi interface below, use
 * the details in the SelfieSvcApi interface and report the discrepancy on the course
 * forums.
 * <p/>
 * For the ultimate ground truth of how the assignment will be graded, please see
 * AutoGradingTest, which shows the specific tests that will be run to grade your
 * solution.
 *
 * @author jules
 */
public interface SelfieSvcApi {


    String TOKEN_PATH = "/oauth/token";

    String DATA_PARAMETER = "data";

    String ID_PARAMETER = "id";

    String NAME_PARAMETER = "name";

    String FILTER_PARAMETER = "filters";

    String SELFIE_SVC_PATH = "/selfie";

    String SELFIE_DATA_PATH = SELFIE_SVC_PATH + "/{id}/data";

    String SELFIE_FILTER_PATH = SELFIE_SVC_PATH + "/filter";

    String SELFIE_DELETE_PATH = SELFIE_SVC_PATH + "/{id}/";
    /**
     * This endpoint in the API returns a list of the selfies that have
     * been added to the server. The SelfieRecord objects should be returned as
     * JSON.
     * <p/>
     * To manually test this endpoint, run your server and open this URL in a browser:
     * http://localhost:8080/selfie
     *
     * @return
     */
    @GET(SELFIE_SVC_PATH)
    Collection<SelfieRecord> getSelfieList();

    /**
     * This endpoint allows clients to add SelfieRecord objects by sending POST requests
     * that have an application/json body containing the SelfieRecord object information.
     *
     * @return
     */
    @POST(SELFIE_SVC_PATH)
    SelfieRecord addSelfie(@Body SelfieRecord selfie);

    /**
     * This endpoint allows clients to set the mpeg selfie data for previously
     * added SelfieRecord objects by sending multipart POST requests to the server.
     * The URL that the POST requests should be sent to includes the ID of the
     * SelfieRecord that the data should be associated with (e.g., replace {id} in
     * the url /selfie/{id}/data with a valid ID of a selfie, such as /selfie/1/data
     * -- assuming that "1" is a valid ID of a selfie).
     *
     * @return
     */
    @Multipart
    @POST(SELFIE_DATA_PATH)
    SelfieStatus setSelfieData(@Path(ID_PARAMETER) long id, @Part(DATA_PARAMETER) TypedFile selfieData);

    /**
     * This endpoint should return the selfie data that has been associated with
     * a SelfieRecord object or a 404 if no selfie data has been set yet. The URL scheme
     * is the same as in the method above and assumes that the client knows the ID
     * of the SelfieRecord object that it would like to retrieve selfie data for.
     * <p/>
     * This method uses Retrofit's @Streaming annotation to indicate that the
     * method is going to access a large stream of data (e.g., the mpeg selfie
     * data on the server). The client can access this stream of data by obtaining
     * an InputStream from the Response as shown below:
     * <p/>
     * SelfieSvcApi client = ... // use retrofit to create the client
     * Response response = client.getData(someSelfieId);
     * InputStream selfieDataStream = response.getBody().in();
     *
     * @param id
     * @return
     */
    @Streaming
    @GET(SELFIE_DATA_PATH)
    Response getData(@Path(ID_PARAMETER) long id);

    @Multipart
    @POST(SELFIE_FILTER_PATH)
    Response applyFilters(@Query(FILTER_PARAMETER) List<String> filterCommands,
                          @Part(DATA_PARAMETER) TypedFile selfieData);


    @DELETE(SELFIE_DELETE_PATH)
    Response delete(@Path(ID_PARAMETER) long id);
}

