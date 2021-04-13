package llproj.llpv.util;

import java.util.Base64;
import java.util.Base64.Decoder;
import java.util.Base64.Encoder;

public class CmnUt {
	public static String secToTime(int sec) {
		int hour, min;
		int origin = sec;
		min = sec / 60;
		hour = min / 60;
		sec = sec % 60;
		min = min % 60;

		String result = "";
		if (hour != 0) {
			result = result + hour + "시간 ";
		}

		if (min != 0) {
			result = result + min + "분 ";
		}

		if (sec != 0) {
			result = result + sec + "초";
		}

		if (origin == 0) {
			result = "0초";
		}
		
		return result;
	}



	public static String decode(String str) {
		 Decoder decoder = Base64.getDecoder();
		 byte[] decodedBytes = decoder.decode(str.getBytes());
		 String decodeData = new String(decodedBytes);
		 return decodeData;
	}
	
	public static String encode(String str) {
		Encoder encoder = Base64.getEncoder();
		byte[] encodedBytes = encoder.encode(str.getBytes());
		String encodeData = new String(encodedBytes);
		return encodeData;
	}
}
