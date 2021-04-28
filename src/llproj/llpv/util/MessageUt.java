package llproj.llpv.util;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.ResourceBundle;

public class MessageUt {
  static ResourceBundle bundle;

  public static String getMessage(String code) {
    return bundle.getString(code);

  }

  public static String getMessage(String code, String... strings) {
    return MessageFormat.format(bundle.getString(code), strings);
  }

  public static void setLocale(String lang) {
    String messagePath = "resources/messages/message";
    String langSplit[] = lang.split("_");
    Locale locale = new Locale(langSplit[0], langSplit[1]);
    bundle = ResourceBundle.getBundle(messagePath, locale);
  }

}
