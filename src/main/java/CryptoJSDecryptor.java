import burp.api.montoya.MontoyaApi;
import burp.api.montoya.core.ToolType;
import burp.api.montoya.http.handler.*;
import burp.api.montoya.http.message.HttpHeader;
import burp.api.montoya.http.message.params.HttpParameter;
import burp.api.montoya.http.message.params.HttpParameterType;
import burp.api.montoya.proxy.http.*;


public class CryptoJSDecryptor implements HttpHandler, ProxyRequestHandler {
    private final MontoyaApi api;

    public CryptoJSDecryptor(MontoyaApi api) {
        this.api = api;
    }

    //ENCRYPTING PROXY/REPEATER REQUEST
    @Override
    public RequestToBeSentAction handleHttpRequestToBeSent(HttpRequestToBeSent request) {
        api.logging().logToOutput("handleHttpRequestToBeSent CALLED!");
        if (CustomCryptoJS.checkbox1_selected || CustomCryptoJS.checkbox2_selected || CustomCryptoJS.checkbox3_selected){
            if (CustomCryptoJS.checkbox1_selected){
                String encrypted;
                if(request.parameter(CustomCryptoJS.url_parameter, HttpParameterType.URL).value()!=null){
                    try {
                        encrypted = CustomCryptoJS.encrypt(request.parameter(CustomCryptoJS.url_parameter, HttpParameterType.URL).value(),CustomCryptoJS.aes_passphrase);
                    } catch (Exception e) {
                        return RequestToBeSentAction.continueWith(request);
                    }
                    HttpParameter url_param = HttpParameter.urlParameter(CustomCryptoJS.url_parameter,encrypted);
                    return RequestToBeSentAction.continueWith(request.withUpdatedParameters(url_param));
                }
                return RequestToBeSentAction.continueWith(request);
            }
            if (CustomCryptoJS.checkbox2_selected) {
                String encrypted;
                if(request.parameter(CustomCryptoJS.body_parameter, HttpParameterType.BODY).value()!=null){
                    try {
                        encrypted = CustomCryptoJS.encrypt(request.parameter(CustomCryptoJS.body_parameter, HttpParameterType.BODY).value(),CustomCryptoJS.aes_passphrase);
                    } catch (Exception e) {
                        return RequestToBeSentAction.continueWith(request);
                    }
                    HttpParameter body_param = HttpParameter.bodyParameter(CustomCryptoJS.body_parameter,encrypted);
                    return RequestToBeSentAction.continueWith(request.withUpdatedParameters(body_param));
                }
                return RequestToBeSentAction.continueWith(request);
            }
            if (CustomCryptoJS.checkbox3_selected) {
                String encrypted;
                if(request.header(CustomCryptoJS.header).value()!=null){
                    try {
                        encrypted = CustomCryptoJS.encrypt(request.header(CustomCryptoJS.header).value(),CustomCryptoJS.aes_passphrase);
                    } catch (Exception e) {
                        return RequestToBeSentAction.continueWith(request);
                    }
                    HttpHeader header = HttpHeader.httpHeader(CustomCryptoJS.header,encrypted);
                    return RequestToBeSentAction.continueWith(request.withUpdatedHeader(header));
                }
                return RequestToBeSentAction.continueWith(request);
            }
        }
        return RequestToBeSentAction.continueWith(request);
    }

    //DECRYPTING PROXY/REPEATER RESPONSE
    @Override
    public ResponseReceivedAction handleHttpResponseReceived(HttpResponseReceived response) {
        api.logging().logToOutput("handleHttpResponseReceived CALLED!");
        // We don't want to modify response in PROXY with this function as this function does not make Edited response separately and as a result, it breaks the functionality of the application.
        if (!response.toolSource().isFromTool(ToolType.PROXY)) {
            String body = response.bodyToString();
            StringBuilder modifiedBody = new StringBuilder(body);
            int index = 0;
            while ((index = modifiedBody.indexOf("U2FsdGVk", index)) != -1) {
                int endIndex = findEncryptedStringEnd(modifiedBody, index);
                String decrypted = null;
                try {
                    decrypted = CustomCryptoJS.decrypt(modifiedBody.substring(index, endIndex).replace("\\", ""), CustomCryptoJS.aes_passphrase);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
                if (endIndex != -1) {
                    modifiedBody.replace(
                            index,
                            endIndex,
                            decrypted
                    );
                    index += decrypted.length();
                } else {
                    index += decrypted.length();
                }
            }
            if (!body.contentEquals(modifiedBody)) {
                api.logging().logToOutput(response.withBody(modifiedBody.toString()));
                return ResponseReceivedAction.continueWith(
                        response.withBody(modifiedBody.toString())
                );
            }
        }
        return ResponseReceivedAction.continueWith(response);
    }


    //DECRYPTING PROXY REQUEST
    @Override
    public ProxyRequestReceivedAction handleRequestReceived(InterceptedRequest interceptedRequest) {
        api.logging().logToOutput("handleRequestReceived CALLED!");
        if (CustomCryptoJS.checkbox1_selected || CustomCryptoJS.checkbox2_selected || CustomCryptoJS.checkbox3_selected){
            if (CustomCryptoJS.checkbox1_selected){
                String decrypted;
                if(interceptedRequest.parameter(CustomCryptoJS.url_parameter, HttpParameterType.URL).value()!=null){
                    try {
                        decrypted = CustomCryptoJS.decrypt(interceptedRequest.parameter(CustomCryptoJS.url_parameter, HttpParameterType.URL).value(),CustomCryptoJS.aes_passphrase);
                    } catch (Exception e) {
                        return ProxyRequestReceivedAction.continueWith(interceptedRequest);
                    }
                    HttpParameter url_param = HttpParameter.urlParameter(CustomCryptoJS.url_parameter,decrypted);
                    return ProxyRequestReceivedAction.continueWith(interceptedRequest.withUpdatedParameters(url_param));
                }
                return ProxyRequestReceivedAction.continueWith(interceptedRequest);
            }
            if (CustomCryptoJS.checkbox2_selected) {
                String decrypted;
                if(interceptedRequest.parameter(CustomCryptoJS.body_parameter, HttpParameterType.BODY).value()!=null){
                    try {
                        decrypted = CustomCryptoJS.decrypt(interceptedRequest.parameter(CustomCryptoJS.body_parameter, HttpParameterType.BODY).value(),CustomCryptoJS.aes_passphrase);
                    } catch (Exception e) {
                        return ProxyRequestReceivedAction.continueWith(interceptedRequest);
                    }
                    HttpParameter body_param = HttpParameter.bodyParameter(CustomCryptoJS.body_parameter,decrypted);
//                    return ProxyRequestReceivedAction.continueWith(interceptedRequest.withUpdatedParameters(body_param));
                    return ProxyRequestReceivedAction.continueWith(interceptedRequest.withUpdatedParameters(body_param));
                }
                return ProxyRequestReceivedAction.continueWith(interceptedRequest);
            }
            if (CustomCryptoJS.checkbox3_selected) {
                String decrypted;
                if(interceptedRequest.header(CustomCryptoJS.header).value()!=null){
                    try {
                        decrypted = CustomCryptoJS.decrypt(interceptedRequest.header(CustomCryptoJS.header).value(),CustomCryptoJS.aes_passphrase);
                    } catch (Exception e) {
                        return ProxyRequestReceivedAction.continueWith(interceptedRequest);
                    }
                    HttpHeader header = HttpHeader.httpHeader(CustomCryptoJS.header,decrypted);
                    return ProxyRequestReceivedAction.continueWith(interceptedRequest.withUpdatedHeader(header));
                }
                return ProxyRequestReceivedAction.continueWith(interceptedRequest);
            }
        }
        return ProxyRequestReceivedAction.continueWith(interceptedRequest);
    }

    @Override
    public ProxyRequestToBeSentAction handleRequestToBeSent(InterceptedRequest interceptedRequest) {
        return null;
    }


    private int findEncryptedStringEnd(CharSequence text, int startIndex) {
        for (int i = startIndex + "U2FsdGVk".length(); i < text.length(); i++) {
            char c = text.charAt(i);
            // Ends at quote, comma, space, newline, or bracket
            if (c == '"' || c == '\'' || c == ' ' || c == '\n' || c == '\r' || c == '}' || c == ',') {
                return i;
            }
        }
        return -1;
    }
}
