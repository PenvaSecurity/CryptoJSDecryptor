import burp.api.montoya.BurpExtension;
import burp.api.montoya.MontoyaApi;
import burp.api.montoya.extension.ExtensionUnloadingHandler;
import burp.api.montoya.ui.UserInterface;

public class BurpCryptoJSDecryptor implements BurpExtension, ExtensionUnloadingHandler {
    private MontoyaApi api;

    @Override
    public void initialize(MontoyaApi api) {
        this.api = api;
        api.extension().setName("CryptoJSDecryptor");
        api.extension().registerUnloadingHandler(this);
        api.http().registerHttpHandler(new CryptoJSDecryptor(api));
        api.proxy().registerRequestHandler(new CryptoJSDecryptor(api));
//        api.proxy().registerResponseHandler(new EncryptedDataLogger(api));
        UserInterface ui = api.userInterface();
        ui.registerSuiteTab("CryptoJSDecryptor", CustomCryptoJS.tab(api));
        api.logging().logToOutput("CryptoJSDecryptor v1.0 loaded!");
    }

    @Override
    public void extensionUnloaded(){
        // Extension does not create background threads, database connections etc. so just logging for now.
        api.logging().logToOutput("Extension unloaded cleanly");
    }
}
