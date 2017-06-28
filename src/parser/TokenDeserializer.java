package parser;

import com.google.gson.*;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by gracenote on 13-Dec-16.
 */
public class TokenDeserializer implements JsonDeserializer<Token> {
    //TODO: wenn json-Datei fehler enthaelt, eine passende Exception ausgeben - Mila
    //TODO: schauen wie unerlaubte Datentypen abgecatscht werden koennen -> unterscheiden zw. Token fuer Rule, Feld usw. - Mila

    private static final String JSON_NAME = "token";
    private static final String JSON_FLAGS = "flags";

    @Override
    public Token deserialize(JsonElement json, Type type, JsonDeserializationContext context) throws JsonParseException {
        Token token = new Token();

        if (json.isJsonPrimitive()) { // Token ist ein String
            token = parsePrimitive(json.getAsString());
        } else if (json.isJsonObject()) { // Token ist ein JsonObject
            JsonObject jsonObject = json.getAsJsonObject();
            JsonElement jsonToken = jsonObject.get(JSON_NAME);
            if (jsonToken.isJsonPrimitive()) { // name in der Form "token":"String"
                token = parsePrimitive(jsonToken.getAsString());
            } else if (jsonToken.isJsonArray()) { // Name in der Form "token":["wall", "me", "exit", "stone"]
                JsonArray jsonArray = jsonToken.getAsJsonArray();
                String[] names = new String[jsonArray.size()];

                for (int i = 0; i < names.length; i++) {
                    names[i] = jsonArray.get(i).getAsString();
                }

                token.setNames(names);
            }

            if (jsonObject.has(JSON_FLAGS)) { // wenn "flags" auftreten
                Map<String, Boolean> flags = new HashMap<>();
                JsonElement jsonElement = jsonObject.get(JSON_FLAGS);
                for (Map.Entry<String, JsonElement> entry : jsonElement.getAsJsonObject().entrySet()) {
                    flags.put(entry.getKey(), context.deserialize(entry.getValue(), Boolean.class));
                }
                token.setFlags(flags);
            }
        }

        return token;
    }

    private Token parsePrimitive(String name) {
        Token token = new Token();
        try {
            int ref = Integer.parseInt(name); // Token ist ein Integer
            token.setReference(ref);
        } catch (NumberFormatException e) {
            token.setToken(name); // Token ist ein String
        }

        return token;
    }
}
