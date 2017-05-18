package com.telekom.m2m.cot.restsdk.devicecontrol;

import com.google.gson.Gson;
import com.telekom.m2m.cot.restsdk.CloudOfThingsRestClient;
import com.telekom.m2m.cot.restsdk.util.ExtensibleObject;
import com.telekom.m2m.cot.restsdk.util.GsonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Device credentials is used to work with device credentials and new device requests.
 * <p>
 * Created by Patrick Steinert on 31.01.16.
 */
public class DeviceCredentialsApi {

    private static final Logger LOGGER = LoggerFactory.getLogger(DeviceCredentialsApi.class);

    private final CloudOfThingsRestClient cloudOfThingsRestClient;
    protected Gson gson = GsonUtils.createGson();
    private static final String CONTENT_TYPE = "application/vnd.com.nsn.cumulocity.deviceCredentials+json;charset=UTF-8;ver=0.9";

    /**
     * Constructor for this API - just used internal.
     *
     * @param cloudOfThingsRestClient the REST client to access CoT.
     */
    public DeviceCredentialsApi(CloudOfThingsRestClient cloudOfThingsRestClient) {
        this.cloudOfThingsRestClient = cloudOfThingsRestClient;
    }

    /**
     * Retrieve the credentials of a certain device.
     *
     * @param deviceId the unique identifier of the device.
     * @return a DeviceCredential object with the device credentials or null if not found.
     */
    public DeviceCredentials getCredentials(String deviceId) {
        DeviceCredentials deviceCredentials = new DeviceCredentials();
        deviceCredentials.setId(deviceId);
        String response = cloudOfThingsRestClient.doPostRequest(gson.toJson(deviceCredentials), "devicecontrol/deviceCredentials", CONTENT_TYPE);

        DeviceCredentials retrievedDeviceCredentials = gson.fromJson(response, DeviceCredentials.class);
        return retrievedDeviceCredentials;
    }

    /**
     * Retrieves a collection for all new device requests.
     *
     * @param resultSize size of the results (Max. 2000)
     * @return an object to retrieve NewDeviceRequests.
     */
    public NewDeviceRequestCollection getNewDeviceRequests(int resultSize) {
        return new NewDeviceRequestCollection(resultSize, cloudOfThingsRestClient);
    }

    /**
     * Retrieves a NewDeviceRequest by its id.
     *
     * @param newDeviceRequestId id of the newDeviceRequest
     * @return the NewDeviceRequest.
     */
    public NewDeviceRequest getNewDeviceRequest(final String newDeviceRequestId) {

        final String api = "/devicecontrol/newDeviceRequests/" + newDeviceRequestId;
        LOGGER.debug("getNewDeviceRequest with api {}", api);

        final String response = cloudOfThingsRestClient.getResponse(api, CONTENT_TYPE);

        LOGGER.debug("response for getNewDeviceRequest: {}", response);

        final ExtensibleObject extensibleObject = gson.fromJson(response, ExtensibleObject.class);

        return extensibleObject != null ?
                new NewDeviceRequest(extensibleObject) : null;
    }

    /**
     * Deletes a certain new device requests.
     *
     * @param deviceId the unique identifier.
     */
    public void deleteNewDeviceRequests(String deviceId) {
        cloudOfThingsRestClient.delete(deviceId, "devicecontrol/newDeviceRequests");
    }
}
