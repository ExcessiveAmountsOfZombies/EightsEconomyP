package com.epherical.eights.data.serializer;

import com.epherical.eights.currency.BasicCurrency;
import com.epherical.eights.user.PlayerUser;
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
import java.util.UUID;

public class PlayerUserSerializer implements JsonSerializer<PlayerUser>, JsonDeserializer<PlayerUser> {

    @Override
    public PlayerUser deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject object = json.getAsJsonObject();
        String uniqueId = object.getAsJsonPrimitive("uuid").getAsString();
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
        return new PlayerUser(UUID.fromString(uniqueId), username, moneyMap);
    }

    @Override
    public JsonElement serialize(PlayerUser src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject object = new JsonObject();
        object.addProperty("uuid", src.getUserID().toString());
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
