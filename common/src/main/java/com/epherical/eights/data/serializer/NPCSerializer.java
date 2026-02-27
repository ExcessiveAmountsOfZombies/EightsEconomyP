package com.epherical.eights.data.serializer;

import com.epherical.eights.currency.BasicCurrency;
import com.epherical.eights.user.NPCUser;
import com.epherical.octoecon.api.Currency;
import com.epherical.octoecon.api.VirtualCurrency;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import net.minecraft.resources.ResourceLocation;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

public class NPCSerializer implements JsonSerializer<NPCUser>, JsonDeserializer<NPCUser> {

    @Override
    public NPCUser deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject object = json.getAsJsonObject();
        String username = object.getAsJsonPrimitive("name").getAsString();
        JsonArray array = object.getAsJsonArray("currencies");
        Map<Currency, Double> moneyMap = new HashMap<>();
        for (JsonElement jsonElement : array) {
            JsonObject currencyObject = jsonElement.getAsJsonObject();
            for (Map.Entry<String, JsonElement> entry : currencyObject.entrySet()) {
                BasicCurrency currency = new BasicCurrency(ResourceLocation.parse(entry.getKey()));
                moneyMap.put(currency, entry.getValue().getAsDouble());
            }
        }
        return new NPCUser(ResourceLocation.parse(username), moneyMap);
    }

    @Override
    public JsonElement serialize(NPCUser src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject object = new JsonObject();
        object.addProperty("name", src.getIdentity());
        JsonArray array = new JsonArray();
        for (Map.Entry<Currency, Double> entry : src.getAllBalances().entrySet()) {
            if (entry.getKey() instanceof VirtualCurrency) {
                continue;
            }
            JsonObject valueObject = new JsonObject();
            valueObject.add(entry.getKey().getIdentity(), new JsonPrimitive(entry.getValue()));
            array.add(valueObject);
        }
        object.add("currencies", array);
        return object;
    }
}
