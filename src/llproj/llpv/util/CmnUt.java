package llproj.llpv.util;

import java.text.MessageFormat;
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
			result = result + hour + MessageUt.getMessage("hour") + " ";
		}

		if (min != 0) {
			result = result + min + MessageUt.getMessage("min") + " ";
		}

		if (sec != 0) {
			result = result + sec + MessageUt.getMessage("sec");
		}

		if (origin == 0) {
			result = "0" + MessageUt.getMessage("sec");
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
