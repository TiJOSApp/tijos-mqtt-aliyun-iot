package net.tijos.aliyun;


public class HmacSHA1 {
	public static String getHmacSHA1(String key, String data) {
		System.err.println(data);
		
		byte[] ipadArray = new byte[64];
		byte[] opadArray = new byte[64];
		byte[] keyArray = new byte[64];
		int ex = key.length();
		SHA1 sha1 = new SHA1();
		if (key.length() > 64) {
			byte[] temp = sha1.getDigestOfBytes(key.getBytes());
			ex = temp.length;
			for (int i = 0; i < ex; i++) {
				keyArray[i] = temp[i];
			}
		} else {
			byte[] temp = key.getBytes();
			for (int i = 0; i < temp.length; i++) {
				keyArray[i] = temp[i];
			}
		}
		for (int i = ex; i < 64; i++) {
			keyArray[i] = 0;
		}
		for (int j = 0; j < 64; j++) {
			ipadArray[j] = (byte) (keyArray[j] ^ 0x36);
			opadArray[j] = (byte) (keyArray[j] ^ 0x5C);
		}
		byte[] tempResult = sha1.getDigestOfBytes(join(ipadArray, data.getBytes()));

		return sha1.getDigestOfString(join(opadArray, tempResult));
	}

	private static byte[] join(byte[] b1, byte[] b2) {
		int length = b1.length + b2.length;
		byte[] newer = new byte[length];
		for (int i = 0; i < b1.length; i++) {
			newer[i] = b1[i];
		}
		for (int i = 0; i < b2.length; i++) {
			newer[i + b1.length] = b2[i];
		}
		return newer;
	}
	
	
	
	public static void main(String[] args) {
		System.err.println((getHmacSHA1("t0nBZxxxjXso6FymkagnLBemzp58N2GJ", "clientIdAdmindeviceNameAdminproductKeyre0cvn30Chqtimestamp5087")));
	}
	
	static String Byte2HexStr(byte[] buf) {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < buf.length; i++) {
			String hex = Integer.toHexString(buf[i] & 0xFF);
			if (hex.length() == 1) {
				hex = '0' + hex;
			}
			sb.append(hex.toUpperCase());
		}
		return sb.toString();
	}
}
