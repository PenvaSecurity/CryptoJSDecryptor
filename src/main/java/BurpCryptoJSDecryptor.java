import burp.api.montoya.BurpExtension;
import burp.api.montoya.MontoyaApi;
import burp.api.montoya.ui.UserInterface;

public class BurpCryptoJSDecryptor implements BurpExtension {
    @Override
    public void initialize(MontoyaApi api) {
        api.extension().setName("CryptoJSDecryptor");
        api.http().registerHttpHandler(new CryptoJSDecryptor(api));
        api.proxy().registerRequestHandler(new CryptoJSDecryptor(api));
//        api.proxy().registerResponseHandler(new EncryptedDataLogger(api));
        UserInterface ui = api.userInterface();
        ui.registerSuiteTab("CryptoJSDecryptor", CustomCryptoJS.tab());
        api.logging().logToOutput("CryptoJSDecryptor v1.0 loaded!");
    }
}
