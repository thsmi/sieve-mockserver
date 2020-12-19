# Sieve Mock Server

A mock implementation for a manage sieve server used to test the sieve clients

## Example Arguments
    
    SCRAMSHA256 SUPPORT_TLS KEYSTORE=d:\keystore.p12 TRUSTSTORE=d:\truststore.jks
    SCRAMSHA1 SUPPORT_TLS KEYSTORE=d:\keystore.p12 TRUSTSTORE=d:\truststore.jks
    CRAMMD5 SUPPORT_TLS KEYSTORE=d:\keystore.p12 TRUSTSTORE=d:\truststore.jks
    EXTERNAL SUPPORT_TLS KEYSTORE=d:\keystore.p12 TRUSTSTORE=d:\truststore.jks FRAGMENTED
    LOGIN SECURE_SASL SUPPORT_TLS KEYSTORE=d:\keystore.p12 TRUSTSTORE=d:\truststore.jks RESPAWN SERVER_SIGNATURE_INLINE
    
## How to create a keystore for TLS

Use the following commands to create a keystore and a truststore.

    set path="c:\Program Files\Java\jre6\bin";%path%
    
    keytool -genkey -alias server -keyalg RSA -keystore keystore.jks -storepass secret -keypass secret
    keytool -export -alias server -keystore keystore.jks -rfc -file server.cer -storepass secret
    keytool -import -alias ca -file server.cer -keystore truststore.jks -storepass secret
    keytool -importkeystore -srckeystore keystore.jks -destkeystore keystore.p12 -srcstoretype JKS -deststoretype PKCS12 -srcstorepass secret

    