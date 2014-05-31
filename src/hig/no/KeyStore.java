
package hig.no;

import javacard.framework.JCSystem;
import javacard.security.DESKey;
import javacard.security.ECPrivateKey;
import javacard.security.ECPublicKey;
import javacard.security.KeyBuilder;
import javacard.security.RSAPrivateKey;
import javacard.security.RSAPublicKey;

/**
 * Class that implements a Very Simple key store. Keys for Basic Access Control passive authentication,
 * active authentication, and Extended Access Control are stored here.
 * 
 *
 */
public class KeyStore {
    
    // static byte CA_EC_KEYTYPE_PRIVATE = KeyBuilder.TYPE_EC_F2M_PRIVATE;
    // static byte CA_EC_KEYTYPE_PUBLIC = KeyBuilder.TYPE_EC_F2M_PUBLIC;
    // static short CA_EC_KEYLENGTH = KeyBuilder.LENGTH_EC_F2M_163;    

    static byte CA_EC_KEYTYPE_PRIVATE = KeyBuilder.TYPE_EC_FP_PRIVATE;
    static byte CA_EC_KEYTYPE_PUBLIC = KeyBuilder.TYPE_EC_FP_PUBLIC;
    static short CA_EC_KEYLENGTH = KeyBuilder.LENGTH_EC_FP_192;
    
    private DESKey sm_kMac, sm_kEnc;
    private DESKey ma_kMac, ma_kEnc;
    byte[] tmpKeys;
    RSAPrivateKey rsaPrivateKey;
    RSAPublicKey rsaPublicKey;
    ECPrivateKey ecPrivateKey;
    ECPublicKey ecPublicKey;
    
    KeyStore() {
        sm_kEnc = (DESKey) KeyBuilder.buildKey(KeyBuilder.TYPE_DES_TRANSIENT_DESELECT,
                                              KeyBuilder.LENGTH_DES3_2KEY,
                                               false);
        sm_kMac = (DESKey) KeyBuilder.buildKey(KeyBuilder.TYPE_DES_TRANSIENT_DESELECT,
                KeyBuilder.LENGTH_DES3_2KEY,
                false);
        ma_kEnc = (DESKey) KeyBuilder.buildKey(KeyBuilder.TYPE_DES,
                                               KeyBuilder.LENGTH_DES3_2KEY,
                                               false);
        ma_kMac = (DESKey) KeyBuilder.buildKey(KeyBuilder.TYPE_DES,
               KeyBuilder.LENGTH_DES3_2KEY,
               false);

        rsaPrivateKey = (RSAPrivateKey)KeyBuilder.buildKey(KeyBuilder.TYPE_RSA_PRIVATE, KeyBuilder.LENGTH_RSA_1024,  false);
        rsaPublicKey =  (RSAPublicKey)KeyBuilder.buildKey(KeyBuilder.TYPE_RSA_PUBLIC, KeyBuilder.LENGTH_RSA_1024,  false);
        ecPrivateKey = (ECPrivateKey)KeyBuilder.buildKey(CA_EC_KEYTYPE_PRIVATE, CA_EC_KEYLENGTH, false);
        ecPublicKey = (ECPublicKey)KeyBuilder.buildKey(CA_EC_KEYTYPE_PUBLIC, CA_EC_KEYLENGTH, false);

        tmpKeys = JCSystem.makeTransientByteArray((short)32, JCSystem.CLEAR_ON_DESELECT);
    }
    

    DESKey getMacKey() {
        if(SmartIDApplet.hasMutuallyAuthenticated()) {
            return sm_kMac;
        }
        else {
            return ma_kMac;
        }
    }
    
    DESKey getCryptKey() {
        if(SmartIDApplet.hasMutuallyAuthenticated()) {
            return sm_kEnc;
        }
        else {
            return ma_kEnc;
        }
    }
    
    void setMutualAuthenticationKeys(byte[] kMac, short kMac_offset, byte[] kEnc, short kEnc_offset) {
        ma_kEnc.setKey(kEnc, kEnc_offset);
        ma_kMac.setKey(kMac, kMac_offset);
    }

    void setSecureMessagingKeys(byte[] kMac, short kMac_offset, byte[] kEnc, short kEnc_offset) {
        sm_kEnc.setKey(kEnc, kEnc_offset);
        sm_kMac.setKey(kMac, kMac_offset);
    }
}

