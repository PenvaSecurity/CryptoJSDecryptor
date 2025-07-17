# CryptoJSDecryptor
A simple BurpSuite extension to help in CryptoJS encryption/decryption.

<img src="https://i.postimg.cc/Gt6ktD9Y/cryptojsdecryptor.png">

### About
- CryptoJSDecryptor is built with Montoya API and uses handlers like HttpHandler and ProxyRequestHandler for encrypting/decrypting of requests and responses.
- Note: *This extension does not decrypt response in Proxy tool right now but we are working on supporting this feature as well.*

### How to use
- You will be required to give AES Passphrase in user inferface of the extension tab. AES Passphrase can be obtained by reversing the application.
- One of the three options can also be selected from the Configuration Settings which will tell the position to extension where to perform encryption/decryption in the request.
- When you have inserted the passphrase and selected desired options, you can simply click on `Save Configuration` button.

### Setting up
```
git clone https://github.com/PenvaSecurity/CryptoJSDecryptor
cd CryptoJSDecryptor
gradle clean build
```
You will get a `CryptoJSDecryptor.jar` file in `build\libs` directory which you can then load into BurpSuite.
