package com.epherical.eights.data.serializer;

import com.epherical.eights.currency.BasicCurrency;
import com.epherical.eights.user.PlayerUser;
import com.epherical.octoecon.api.Currency;
import com.google.common.collect.Maps;
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
import java.util.Map;
import java.util.UUID;

public class PlayerUserSerializer implements JsonSerializer<PlayerUser>, JsonDeserializer<PlayerUser> {

    @Override
    public PlayerUser deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject object = json.getAsJsonObject();
        String uniqueID = object.getAsJsonPrimitive("uuid").getAsString();
        String username = object.getAsJsonPrimitive("name").getAsString();
        JsonArray array = object.getAsJsonArray("currencies");
        Map<Currency, Double> moneyMap = Maps.newHashMap();
        for (JsonElement jsonElement : array) {
            JsonObject cast = jsonElement.getAsJsonObject();
            for (Map.Entry<String, JsonElement> entry : cast.entrySet()) {
                BasicCurrency currency = new BasicCurrency(new ResourceLocation(entry.getKey()));
                moneyMap.put(currency, entry.getValue().getAsDouble());
            }
        }
        return new PlayerUser(UUID.fromString(uniqueID), username, moneyMap);
    }

    @Override
    public JsonElement serialize(PlayerUser src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject object = new JsonObject();
        object.addProperty("uuid", src.getUserID().toString());
        object.addProperty("name", src.getIdentity());
        JsonArray array = new JsonArray();
        for (Map.Entry<Currency, Double> entry : src.getAllBalances().entrySet()) {
            JsonObject object1 = new JsonObject();
            object1.add(entry.getKey().getIdentity(), new JsonPrimitive(entry.getValue()));
            array.add(object1);
        }
        object.add("currencies", array);
        return object;
    }
}
