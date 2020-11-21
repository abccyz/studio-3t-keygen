import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.security.*;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Arrays;
import java.util.Base64;

public class Main {
    public static String addLinebreaks(String input) {
        return input.replaceAll("(.{70})", "$1\n");
    }

    // openssl req -newkey rsa:2048 -sha256 -new -nodes -x509 -days 3650 -keyout key2.pem -out cert2.pem
    // openssl x509 -inform PEM -outform der -in cert2.pem -out licensing_public.cer
    public static String publicCertString = "MIIDvDCCAqQCCQDhHZ5dA65Y2jANBgkqhkiG9w0BAQsFADCBnzELMAkGA1UEBhMCVUExETAPBgNVBAgMCFRlcm5vcGlsMREwDwYDVQQHDAhUZXJub3BpbDEVMBMGA1UECgwMeXJhbWJsZXIyMDAxMRUwEwYDVQQLDAx5cmFtYmxlcjIwMDExFTATBgNVBAMMDHlyYW1ibGVyMjAwMTElMCMGCSqGSIb3DQEJARYWeXJhbWJsZXIyMDAxQGdtYWlsLmNvbTAeFw0yMDExMjEyMTU5MzdaFw0zMDExMTkyMTU5MzdaMIGfMQswCQYDVQQGEwJVQTERMA8GA1UECAwIVGVybm9waWwxETAPBgNVBAcMCFRlcm5vcGlsMRUwEwYDVQQKDAx5cmFtYmxlcjIwMDExFTATBgNVBAsMDHlyYW1ibGVyMjAwMTEVMBMGA1UEAwwMeXJhbWJsZXIyMDAxMSUwIwYJKoZIhvcNAQkBFhZ5cmFtYmxlcjIwMDFAZ21haWwuY29tMIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA7DaUHuhKVp871zQy39Ybj+/f29xH1SuGHIjtNkRMZ7aXWXOsLCpJbarmNWnxThIa3bpg0zU7DOGTJET535c/7/yj3v6C/+AcBrbNXpO/gmVYgNxjAZ9iTgueodGdN2z0ihT3KCP+jbcssiGDd4RAjvuJNGNjyxKEUcHfE2cEjztZAIjVbw+wRmH/UT2GwUFxgk7ztN8vb5/CHrxruHjNsCIUd6raXMHQ0Z1mUR72beIW3A82fZwY6+704XdHjl9InkrrSxa7XiEpklkPFSygBIRiID2bHtUUroVINKD94gB2thcLdADMRzgmJcVdLSvHBEWlKx92Sn237P2/gFOolwIDAQABMA0GCSqGSIb3DQEBCwUAA4IBAQBywBWK0SV+agVl1evskXsK6neFDMTKoGSpy7F2zS2iLKQxg9/Rf7SFsvAQ1oJjJZRp91x731KBHcO/olpDk4SiV28fKuMKrHIp7yWeJ0aHj9nJdEoJhegZlzxr9pvd71J7VlK/vSZHVFOBOsKcEXZgfiIbEvGi6W5LrcAe6jz+qLT4MWieN1yhgXrlYgQrpDkjFhP4fvjf+cH6Di502iUUC42ETWFKUQePL2XkEzRJfIvMztGRqeYfn4+5kbAzDhzIN0qja3K9A32qxyiRyqW+NxR1Ooota0zP1cyvI5N5CMXEGcD38J57OXe4NvJa779G+Q19MRRqF0xYpW0YlduC";
    public static String privateKey = "MIIEvAIBADANBgkqhkiG9w0BAQEFAASCBKYwggSiAgEAAoIBAQDsNpQe6EpWnzvXNDLf1huP79/b3EfVK4YciO02RExntpdZc6wsKkltquY1afFOEhrdumDTNTsM4ZMkRPnflz/v/KPe/oL/4BwGts1ek7+CZViA3GMBn2JOC56h0Z03bPSKFPcoI/6NtyyyIYN3hECO+4k0Y2PLEoRRwd8TZwSPO1kAiNVvD7BGYf9RPYbBQXGCTvO03y9vn8IevGu4eM2wIhR3qtpcwdDRnWZRHvZt4hbcDzZ9nBjr7vThd0eOX0ieSutLFrteISmSWQ8VLKAEhGIgPZse1RSuhUg0oP3iAHa2Fwt0AMxHOCYlxV0tK8cERaUrH3ZKfbfs/b+AU6iXAgMBAAECggEAZP+5ObXtAzi2p3/EOP0db+ALAcEgXb9saosJkmiFmtrxv+e7uG6kUuX95NzVtqH7Js9oUyaxhwJ0nc94X+gWCJEnqFN+w9WZZi2Hhmb6PdEDlKWnF6yEpgZF37720BogPWJVg65QS1wFGm1F/zZnPDvFIMdWhh/2v9Z7gZpM0silI8fw+KCdnm0cIY4DxOYyIqxGSnHlCX/pYj6+g9pSLvfoFTWvA4ofNco9h4GR4jr9VTrvEayREB9o0xiUmd5w9Bv9Bj2LWD/UkIvQOZ8p94T5r+pkIX0KsjeimXqB/drNm4OHo5wA6SnroqEzumwk4LoTZUMhU/4EI6tJn62dsQKBgQD6gtCjg+8vhDfblsgn+TasvRSJk0+87t+W7t26dxyvQgs79OVAPB1P79jG1roZXc4RTwhqxSJH/ZD1Zs84AvnvWug07TKTQ6itP7IrQ8ZZ0LHVp85C+SPQBOaCNx8g4a3g+vHloAh9DiBNBQRxjG9ZsyzhlHh8ORA0JOmNYBYnOwKBgQDxY5A13CFEotvu3P1xdvQ/teBh/meQzWiasha0l/Cu1cdDri/PuXjdVfXs4L13h9t1Ic7KlQ0O62ckrMxsaenKTZc2wev2WjVsHNuKeedPcQXcp8HNc1EWCnSTzpOJWgcXsTtM5mi1yaq0vqD15lngsUS5ShvVxVoSpe9ZU9PGVQKBgGMLC2BKzmtT9B2ujK45OuOq0g7enj0NmHr9L88EPuQP0Y8/8M+ntNMg+e0LgoJ68vkm7SYBkN8zondT0YPMsXJv17TtPvy4WF0+/LtcTXS/LUBS2xrhk8Yv2c8L6hO/BlcYAEGQNyryRdUNsccPBgtJ6um5ILXEfeKP4YMSxU93AoGAa5slq0zhK7ap8lZjSNRtV4VdbHG7e0qGKgffpUviSB4WgS5CNAiLP6fVRG5/J6zRgOq8Q4SkWJVJ2oiJbH7ecIecNBXIeX6HOBW5LIbhPYG1ykKiW1Lfv8KTa1x4Wo9egApK1CIWZYtr5LziBhxUzpxXdX364JXkbFG6XAjJbC0CgYBCD/RFMZR019WuTiCrhcXHOKQEbbwrrnsF2vhv8r6F/YCfJooCD+nIkjI9cJtR5rAuf+O5N68kiD8+aFNyxyzWI6EtQdCLrweVtpAxTNlppQXleLENmMcvp3aQSp6ITTbaH7O/vyv5PjtR9GhHkWBLkI6nd3ZHvvWsyLOAa69CTg==";

    public static InputStream getStreamFromB64(String b64) {
        byte[] arrCert = Base64.getDecoder().decode(b64);
        return new ByteArrayInputStream(arrCert);
    }

    public static X509Certificate getCertFromString(String certString) throws CertificateException {
        CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
        return (X509Certificate) certificateFactory.generateCertificate(getStreamFromB64(certString));
    }

    public static void printCertDigest(String certString) {
        try {
            X509Certificate cert = getCertFromString(certString);
            MessageDigest messageDigest = MessageDigest.getInstance("SHA-1");
            messageDigest.update(cert.getEncoded());
            byte[] arrayOfByte = messageDigest.digest();
            System.out.println(Arrays.toString(arrayOfByte));
            // JavaScript => Recaf: [...].map((e,index)=>`DUP\nBIPUSH ${index}\nBIPUSH ${e}\nBASTORE`).join('\n')
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public static String signString(String string) {
        try {
            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(Base64.getDecoder().decode(privateKey));
            KeyFactory kf = KeyFactory.getInstance("RSA");
            PrivateKey privKey = kf.generatePrivate(keySpec);
            Signature signature = Signature.getInstance("SHA256withRSA");
            signature.initSign(privKey);
            signature.update(string.getBytes("UTF-8"));
            String returnStr = Base64.getEncoder().encodeToString(signature.sign());
            // System.out.println("signed:" + returnStr);
            return returnStr;
        } catch (Exception e) {
            System.out.println(e);
            return "";
        }
    }

    public static void isSignString(String signed, String originalString) {
        try {
            X509Certificate cert = getCertFromString(publicCertString);
            PublicKey publicKey = cert.getPublicKey();
            Signature signature = Signature.getInstance("SHA256withRSA");
            signature.initVerify(publicKey);
            signature.update(originalString.getBytes("UTF-8"));
            boolean match = signature.verify(Base64.getDecoder().decode(signed));
            System.out.println("match:" + match);
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public static String getLicName(String type) {
        if (type.equals("1")) return "Pro";
        if (type.equals("2")) return "Core";
        if (type.equals("5")) return "Enterprise";
        return "";
    }

    public static String genLicKey(String endDate, String startDate, String type, String customerName, String companyName) throws UnsupportedEncodingException {
        String s = "v1|" + type + "|sl|true|yrambler2001|" + startDate + "|" + endDate + "|made by yrambler2001|" + customerName + "|" + companyName + "|_";
        String b64s = Base64.getEncoder().encodeToString(s.getBytes("UTF-8"));
        String enc = signString(s);
        String key = addLinebreaks("Aw==\\" + b64s + "\\" + enc + "\n");
        return "---\n" +
                "Software License Details\n" +
                "\n" +
                " Customer Name: " + customerName + "\n" +
                " Company: " + companyName + "\n" +
                " Edition: Studio 3T " + getLicName(type) + "\n" +
                " Number of users: Volume License\n" +
                " License Period: " + startDate + " - " + endDate + "\n" +
                "\n" +
                "#\n" +
                key +
                "#\n" +
                "---";
    }

    public static void main(String[] args) {
        printCertDigest(publicCertString);
        try {
            // String signed = signString("123");
            // isSignString(signed, "123");
            String key = genLicKey("01.01.2025", "01.01.2018", "5", "yrambler2001", "yrambler2001");
            System.out.println(key);
        } catch (Exception e) {
            System.out.println(e);
        }
    }

}
