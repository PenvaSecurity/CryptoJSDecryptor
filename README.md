# CryptoJSDecryptor
A Burp Suite extension for seamless encryption and decryption with CryptoJS — the most widely used JavaScript crypto library.
<img src="https://i.postimg.cc/Gt6ktD9Y/cryptojsdecryptor.png">

---

### About
- CryptoJSDecryptor is built using the Montoya API and leverages handlers like `HttpHandler` and `ProxyRequestHandler` to perform encryption and decryption of HTTP requests and responses.
- Note: *Decryption of responses in the Proxy tool is not supported yet, but support for this feature is currently in development.*

---

### Setting up
```
git clone https://github.com/PenvaSecurity/CryptoJSDecryptor
cd CryptoJSDecryptor
gradle clean build
```
You will get a `CryptoJSDecryptor.jar` file in `build\libs` directory which you can then load into BurpSuite.

---

### Important Note
This extension currently supports a specific usage pattern of CryptoJS encryption/decryption - where only a passphrase and data are provided:
```
CryptoJS.AES.encrypt(data, passphrase);
CryptoJS.AES.decrypt(data, passphrase);
```
When we perform encryption in CryptoJS this way, we will get a base64 encoded cipher with a `U2FsdGVkX1` prefix in starting which decodes to `Salted__` string.
In simple words, when using a passphrase, the encrypted output follows this structure:
```
[8 bytes: "Salted__"] + [8 bytes: salt] + [ciphertext]
```
Then, this entire binary blob is Base64-encoded to get a nice string.

So, if you encounter a CryptoJS implementation that uses a string-based passphrase (without explicitly supplying a key and IV), this extension can be especially useful for decoding and inspecting such traffic.

---

### How to use
- You will be required to give AES Passphrase in user interface of the extension tab. AES Passphrase can be obtained by reversing the application.
- One of the three options can also be selected from the Configuration Settings which will tell the position to extension where to perform encryption/decryption in the request.
- When you have inserted the passphrase and selected desired options, you can simply click on `Save Configuration` button.
<img src="https://i.postimg.cc/vmGbvX03/config-saved.png">

