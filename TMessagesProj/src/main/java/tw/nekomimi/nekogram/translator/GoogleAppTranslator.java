package tw.nekomimi.nekogram.translator;

import android.text.TextUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.List;

public class GoogleAppTranslator extends BaseTranslator {

    private static GoogleAppTranslator instance;
    private final List<String> targetLanguages = Arrays.asList(
            "sq", "ar", "am", "az", "ga", "et", "or", "eu", "be", "bg", "is", "pl", "bs",
            "fa", "af", "tt", "da", "de", "ru", "fr", "tl", "fi", "fy", "km", "ka", "gu",
            "kk", "ht", "ko", "ha", "nl", "ky", "gl", "ca", "cs", "kn", "co", "hr", "ku",
            "la", "lv", "lo", "lt", "lb", "rw", "ro", "mg", "mt", "mr", "ml", "ms", "mk",
            "mi", "mn", "bn", "my", "hmn", "xh", "zu", "ne", "no", "pa", "pt", "ps", "ny",
            "ja", "sv", "sm", "sr", "st", "si", "eo", "sk", "sl", "sw", "gd", "ceb", "so",
            "tg", "te", "ta", "th", "tr", "tk", "cy", "ug", "ur", "uk", "uz", "es", "iw",
            "el", "haw", "sd", "hu", "sn", "hy", "ig", "it", "yi", "hi", "su", "id", "jw",
            "en", "yo", "vi", "zh-TW", "zh-CN", "zh");

    static GoogleAppTranslator getInstance() {
        if (instance == null) {
            synchronized (GoogleAppTranslator.class) {
                if (instance == null) {
                    instance = new GoogleAppTranslator();
                }
            }
        }
        return instance;
    }

    @Override
    protected String translate(String query, String tl) throws IOException, JSONException {
        String url = "https://translate.google.com/translate_a/single?dj=1" +
                "&q=" + URLEncoder.encode(query, "UTF-8") +
                "&sl=auto" +
                "&tl=" + tl +
                "&ie=UTF-8&oe=UTF-8&client=at&dt=t&otf=2";
        String response = new Http(url)
                .header("User-Agent", "GoogleTranslate/6.25.0.02.404801591 (Linux; U; Android 11; Redmi K20 Pro)")
                .request();
        if (TextUtils.isEmpty(response)) {
            return null;
        }
        return getResult(response);
    }

    @Override
    public List<String> getTargetLanguages() {
        return targetLanguages;
    }

    @Override
    public String convertLanguageCode(String language, String country) {
        String languageLowerCase = language.toLowerCase();
        String code;
        if (!TextUtils.isEmpty(country)) {
            String countryUpperCase = country.toUpperCase();
            if (targetLanguages.contains(languageLowerCase + "-" + countryUpperCase)) {
                code = languageLowerCase + "-" + countryUpperCase;
            } else if (languageLowerCase.equals("zh")) {
                if (countryUpperCase.equals("DG")) {
                    code = "zh-CN";
                } else if (countryUpperCase.equals("HK")) {
                    code = "zh-TW";
                } else {
                    code = languageLowerCase;
                }
            } else {
                code = languageLowerCase;
            }
        } else {
            code = languageLowerCase;
        }
        return code;
    }

    private String getResult(String string) throws JSONException {
        StringBuilder sb = new StringBuilder();
        JSONArray array = new JSONObject(string).getJSONArray("sentences");
        for (int i = 0; i < array.length(); i++) {
            sb.append(array.getJSONObject(i).getString("trans"));
        }
        return sb.toString();
    }
}
