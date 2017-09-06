package com.telekom.m2m.cot.restsdk.realtime;

import java.util.Arrays;

import com.google.gson.Gson;
import com.telekom.m2m.cot.restsdk.CloudOfThingsRestClient;
import com.telekom.m2m.cot.restsdk.util.GsonUtils;

/**
 * The class that defines the CepApi. CEP stands for Complex-Event-Processing.
 * CepApi returns a URL to a collection of modules.
 * 
 * Created by Ozan Arslan on 14.08.2017. TODO: we might want to rename this, to
 * avoid confusion with the CoT-entity "CepApi".
 */
public class CepApi {

    private final CloudOfThingsRestClient cloudOfThingsRestClient;

    private final Gson gson = GsonUtils.createGson();


    public CepApi(CloudOfThingsRestClient cloudOfThingsRestClient) {
        this.cloudOfThingsRestClient = cloudOfThingsRestClient;
    }


    /**
     * Returns the connector that establishes the communications with the
     * notifications service.
     * 
     * @return CepConnector
     */
    public CepConnector getCepConnector() {
        return new CepConnector(cloudOfThingsRestClient);
    }


    public ModuleCollection getModules() {
        return new ModuleCollection(cloudOfThingsRestClient, "cep/modules", gson, null);

    }


    public Module createModule(Module module) {
        String data = "module " + module.getName() + ";" + String.join("\n\n", module.getStatements());

        String response = cloudOfThingsRestClient.doFormUpload(data, "file", "cep/modules");

        Module responseModule = gson.fromJson(response, Module.class);

        module.copyFrom(responseModule);

        return module;
    }


    public Module getModule(String id) {

        String responseJson = cloudOfThingsRestClient.getResponse(id,
                                                              "cep/modules",
                                                              "application/vnd.com.nsn.cumulocity.cepModule+json");
        String statementsFile = cloudOfThingsRestClient.getResponse(id,
                "cep/modules",
                "text/plain");

        Module module = gson.fromJson(responseJson, Module.class);
        String[] statements = statementsFile.split("\n\n");
        module.setStatements(Arrays.asList(statements)); // TODO: does this already work?

        return module;

    }

    // TODO: to be tested with the new userName.
    public void updateModule(Module module) {
        String CONTENT = "application/vnd.com.nsn.cumulocity.cepModule+json";

        String json = "{\"name\" : \"" + module.getName() + "\", \"status\" : \"" + module.getStatus() + "\"}";

        // the status can only be updated if it has changed from the default:
        // DEPLOYED. TODO: investigate why this is the case.
        if (module.getStatus().equals("DEPLOYED")) {
            json = "{\"name\" : \"" + module.getName() + "\"}";
        }

        cloudOfThingsRestClient.doPutRequest(json, "cep/modules/" + module.getId(), CONTENT);

    }

    public void deleteModule(Module module) {

        // TODO: 405 Method not supported!?
        cloudOfThingsRestClient.delete(module.getId(), "cep/modules");

    }

    public void deleteModule(String id) {

        cloudOfThingsRestClient.delete(id, "cep/modules");

    }
    // TODO check if we really need a representation of the CepApi such as
    // below:
    /*
     * public CepApiRepresentation getCepApi() {
     * 
     * String result = cloudOfThingsRestClient.getResponse("cep", null); return
     * new CepApiRepresentation(gson.fromJson(result, ExtensibleObject.class));
     * }
     */

}
