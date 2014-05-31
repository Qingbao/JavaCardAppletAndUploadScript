package hig.no;

import javacard.framework.ISO7816;
import javacard.framework.ISOException;
import javacard.framework.JCSystem;
import javacard.framework.Util;

/**
 * Files system (with different authorization levels) according to LDS
 * version 1.7.
 * 
 * 
 */
public class FileSystem {
    static final short EF_DG1_FID = (short) 0x0001;

    static final short EF_DG2_FID = (short) 0x0002;

    static final short EF_DG3_FID = (short) 0x0003;

    static final short EF_DG4_FID = (short) 0x0004;

    static final short EF_DG5_FID = (short) 0x0005;

    static final short EF_DG6_FID = (short) 0x0006;

    static final short EF_DG7_FID = (short) 0x0007;

    static final short EF_DG8_FID = (short) 0x0008;

    static final short EF_DG9_FID = (short) 0x0009;

    static final short EF_DG10_FID = (short) 0x000A;

    static final short EF_DG11_FID = (short) 0x000B;

    static final short EF_DG12_FID = (short) 0x000C;

    static final short EF_DG13_FID = (short) 0x000D;

    static final short EF_DG14_FID = (short) 0x000E;

    static final short EF_DG15_FID = (short) 0x000F;
    
    static final short EF_DG16_FID = (short) 0x0010;

    static final short EF_SOD_FID = (short) 0x001D;

    static final short EF_COM_FID = (short) 0x001E;

    private static final short EF_DG1_INDEX = (short) 0;

    private static final short EF_DG2_INDEX = (short) 1;

    private static final short EF_DG3_INDEX = (short) 2;

    private static final short EF_DG4_INDEX = (short) 3;

    private static final short EF_DG5_INDEX = (short) 4;

    private static final short EF_DG6_INDEX = (short) 5;

    private static final short EF_DG7_INDEX = (short) 6;

    private static final short EF_DG8_INDEX = (short) 7;

    private static final short EF_DG9_INDEX = (short) 8;

    private static final short EF_DG10_INDEX = (short) 9;

    private static final short EF_DG11_INDEX = (short) 10;

    private static final short EF_DG12_INDEX = (short) 11;

    private static final short EF_DG13_INDEX = (short) 12;

    private static final short EF_DG14_INDEX = (short) 13;

    private static final short EF_DG15_INDEX = (short) 14;
    
    private static final short EF_DG16_INDEX = (short) 15;

    private static final short EF_SOD_INDEX = (short) 16;

    private static final short EF_COM_INDEX = (short) 17;


    private Object[] files;

    private short[] fileSizes;

    private byte[] filePerms;

    byte[] currentAuthorization;

    FileSystem() {
        files = new Object[18];
        fileSizes = new short[18];
        filePerms = new byte[18];
        currentAuthorization = JCSystem.makeTransientByteArray((short) 3,
                JCSystem.CLEAR_ON_DESELECT);
        // The default read authorization for all DGs is mutual authentication
        // required
        for (short i = 0; i < 18; i++) {
            filePerms[i] = SmartIDApplet.MUTUAL_AUTHENTICATED;
        }
        filePerms[EF_DG14_INDEX] = (byte) 0x00;
    }

    void createFile(short fid, short size, boolean eapProtection) {
        short idx = getFileIndex(fid);

        // first create determines maximum file size
        if (files[idx] == null)
            files[idx] = new byte[size];

        if (((byte[]) files[idx]).length < size)
            ISOException.throwIt(ISO7816.SW_FILE_FULL);

        fileSizes[idx] = size;
        if(eapProtection) {
          filePerms[idx] = SmartIDApplet.TERMINAL_AUTHENTICATED;
        }
    }

    void writeData(short fid, short file_offset, byte[] data,
            short data_offset, short length) {
        byte[] file = getFile(fid);
        short fileSize = getFileSize(fid);

        if (file == null) {
            ISOException.throwIt(ISO7816.SW_FILE_NOT_FOUND);
        }

        if (fileSize < (short) (file_offset + length))
            ISOException.throwIt(ISO7816.SW_FILE_FULL);
        Util.arrayCopyNonAtomic(data, data_offset, file, file_offset, length);
        // Extract the pointers to where the Root and Alternate root CVCA certificate
        // identifiers are stored. Properly this should be done with a BERTLVScanner or
        // similar.
        if(fid == EF_COM_FID) {
            short cvcaRootIndex = -1;
            short cvcaAltIndex = -1;
            for(short i = 0; i<length ; i++) {
                if(data[i] == 0x04 && data[(short)(i+1)] == 0x11) {
                    if(cvcaRootIndex == -1) {
                        cvcaRootIndex = (short)((short)(file_offset + i) + 2);                        
                    }else{
                        cvcaAltIndex = (short)((short)(file_offset + i) + 2);
                    }
                }
            }
            SmartIDApplet.certificate.setCOMFileData(file, cvcaRootIndex, cvcaAltIndex);
        }
    }

    byte[] getFile(short fid) {
        short idx = getFileIndex(fid);
        if (idx == -1) {
            return null;
        }
        return (byte[]) files[idx];
    }

    short getFileSize(short fid) {
        short idx = getFileIndex(fid);
        if (idx == -1) {
            return -1;
        }
        return fileSizes[idx];
    }

    private short getFileIndex(short fid) throws ISOException {
        short result = -1;
        switch (fid) {
        case EF_DG1_FID:
            result = EF_DG1_INDEX;
            break;
        case EF_DG2_FID:
            result = EF_DG2_INDEX;
            break;
        case EF_DG3_FID:
            result = EF_DG3_INDEX;
            break;
        case EF_DG4_FID:
            result = EF_DG4_INDEX;
            break;
        case EF_DG5_FID:
            result = EF_DG5_INDEX;
            break;
        case EF_DG6_FID:
            result = EF_DG6_INDEX;
            break;
        case EF_DG7_FID:
            result = EF_DG7_INDEX;
            break;
        case EF_DG8_FID:
            result = EF_DG8_INDEX;
            break;
        case EF_DG9_FID:
            result = EF_DG9_INDEX;
            break;
        case EF_DG10_FID:
            result = EF_DG10_INDEX;
            break;
        case EF_DG11_FID:
            result = EF_DG11_INDEX;
            break;
        case EF_DG12_FID:
            result = EF_DG12_INDEX;
            break;
        case EF_DG13_FID:
            result = EF_DG13_INDEX;
            break;
        case EF_DG14_FID:
            result = EF_DG14_INDEX;
            break;
        case EF_DG15_FID:
            result = EF_DG15_INDEX;
            break;
        case EF_DG16_FID:
            result = EF_DG16_INDEX;
            break;
        case EF_SOD_FID:
            result = EF_SOD_INDEX;
            break;
        case EF_COM_FID:
            result = EF_COM_INDEX;
            break;
        default:
            result = -1;
            break;
        }
        if (result != -1 && SmartIDApplet.isLocked() && SmartIDApplet.hasMutualAuthenticationKeys()) {
            // We are in the personalized state and BAC is active,
            // we need to control the access
            // a. check that the current authorization level is sufficient to
            // access
            // the given file
            // b. if we are passed the EAC protocol we also need to check
            // whether the current certificate authorization allows us to read the file.
            byte perm = filePerms[result];
            if ((byte) (perm & SmartIDApplet.volatileState[0]) != perm) {
                ISOException.throwIt(ISO7816.SW_SECURITY_STATUS_NOT_SATISFIED);
            }
            if (result <= EF_DG16_INDEX
                    && SmartIDApplet.hasTerminalAuthenticated() && perm == SmartIDApplet.TERMINAL_AUTHENTICATED) {
                short m = (short) (0x1 << result);
                if ((Util.getShort(currentAuthorization, (short) 1) & m) != m) {
                    ISOException
                            .throwIt(ISO7816.SW_SECURITY_STATUS_NOT_SATISFIED);
                }
            }
        }
        return result;
    }
}
