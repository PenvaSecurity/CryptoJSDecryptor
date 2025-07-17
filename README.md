# CryptoJSDecryptor
A simple BurpSuite extension to help in encryption/decryption of CryptoJS, the most used crypto library in JS.
<img src="https://i.postimg.cc/Gt6ktD9Y/cryptojsdecryptor.png">

### About
- CryptoJSDecryptor is built with Montoya API and uses handlers like HttpHandler and ProxyRequestHandler for encrypting/decrypting of requests and responses.
- Note: *This extension does not decrypt response in Proxy tool right now but we are working on supporting this feature as well.*

### Important Note
This extension currently works on this specific implementation of CryptoJS encryption/decryption where you pass only passphrase and data to encrypt/decrypt.
```
CryptoJS.AES.encrypt(data, passphrase);
CryptoJS.AES.decrypt(data, passphrase);
```
When we perform encryption in CryptoJS this way, we will get a base64 encoded cipher with a `U2FsdGVkX1` prefix in starting which decodes to `Salted__` string.
In simple words, when using a passphrase, CryptoJS structures the output like this:
```
[8 bytes: "Salted__"] + [8 bytes: salt] + [ciphertext]
```
Then, this entire binary blob is Base64-encoded to get a nice string.

So, if you ever see a string-based passphrase encryption/decryption implementation (using just a string, not a key/IV) in CryptoJS, then this extension can help greatly.

### How to use
- You will be required to give AES Passphrase in user interface of the extension tab. AES Passphrase can be obtained by reversing the application.
- One of the three options can also be selected from the Configuration Settings which will tell the position to extension where to perform encryption/decryption in the request.
- When you have inserted the passphrase and selected desired options, you can simply click on `Save Configuration` button.
<img src="https://i.postimg.cc/vmGbvX03/config-saved.png">

### Setting up
```
git clone https://github.com/PenvaSecurity/CryptoJSDecryptor
cd CryptoJSDecryptor
gradle clean build
```
You will get a `CryptoJSDecryptor.jar` file in `build\libs` directory which you can then load into BurpSuite.
