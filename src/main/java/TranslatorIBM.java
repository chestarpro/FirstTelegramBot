import com.ibm.cloud.sdk.core.security.IamAuthenticator;
import com.ibm.cloud.sdk.core.service.exception.NotFoundException;
import com.ibm.watson.language_translator.v3.LanguageTranslator;
import com.ibm.watson.language_translator.v3.model.TranslateOptions;
import com.ibm.watson.language_translator.v3.model.TranslationResult;
import org.json.JSONArray;
import org.json.JSONObject;

public class TranslatorIBM {

    public static String getTranslate(String message, String language) throws NotFoundException {
        IamAuthenticator authenticator = new IamAuthenticator("BK4p2fPVCKUbhWOJk4rNNhq_QaaXVN5hE4NeLdH323lA");
        LanguageTranslator languageTranslator = new LanguageTranslator("2018-05-01", authenticator);
        languageTranslator.setServiceUrl("https://api.eu-gb.language-translator.watson.cloud.ibm.com/instances/0a312422-532c-4a18-9c7b-81b1a6591620");

        TranslateOptions translateOptions = new TranslateOptions.Builder()
                .addText(message)
                .modelId(language)
                .build();

        TranslationResult result = languageTranslator.translate(translateOptions)
                .execute().getResult();

        String resText = result.toString();

        JSONObject object = new JSONObject(resText);

        String res2Text = "";

        JSONArray getArray = object.getJSONArray("translations");
        for (int i = 0; i < getArray.length() ; i++) {
            JSONObject obj = getArray.getJSONObject(i);
            res2Text += (String) obj.get("translation");
        }
        return res2Text;
    }
}
