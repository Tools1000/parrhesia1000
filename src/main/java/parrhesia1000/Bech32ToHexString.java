package parrhesia1000;

import org.bitcoinj.core.Bech32;

import java.util.ArrayList;

public class Bech32ToHexString {

    public static String decode(String bech32) {
        Bech32.Bech32Data decode = Bech32.decode(bech32);
        int[] bytes2 = fromWords(decode.data);
       return hex_encode(bytes2);
    }

    public static int[] convertbits(byte[] data, byte inBits, byte outBits, boolean pad) {
        int value = 0;
        int bits = 0;
        int maxV = (1 << outBits) - 1;
        ArrayList<Integer> result = new ArrayList<>();
        for (int i = 0; i < data.length; ++i) {
            value = (value << inBits) | data[i];
            bits += inBits;
            while (bits >= outBits) {
                bits -= outBits;
                result.add((value >> bits) & maxV);
            }
        }
        if (pad) {
            if (bits > 0) {
                result.add((value << (outBits - bits)) & maxV);
            }
        }
        else {
            if (bits >= inBits)
                throw new IllegalArgumentException("Excess padding");
//            if ((value << (outBits - bits)) & maxV)
//                throw new IllegalArgumentException("Non-zero padding");
        }
        return result.stream().mapToInt(Integer::intValue).toArray();
    }

    public static int[] fromWords(byte[] words) {
        int[] res = convertbits(words, (byte) 5, (byte) 8, false);
        if (res != null)
            return res;
        throw new IllegalArgumentException("Conversion failed");
    }

    public static char hex_char(int val) {
        if (val < 10)
            return (char) (48 + val);
        if (val < 16)
            return (char) (97 + val - 10);
        throw new IllegalArgumentException("Invalid input");
    }

    public static String hex_encode(int[] buf) {
        StringBuilder str = new StringBuilder();
        for (int i = 0; i < buf.length; i++) {
            int c = buf[i];
            str.append(hex_char(c >> 4));
            str.append(hex_char(c & 0xF));
        }
        return str.toString();
    }
}






