package com.epherical.eights.data.serializer;

import com.epherical.eights.currency.BasicCurrency;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import net.minecraft.resources.ResourceLocation;

import java.lang.reflect.Type;

public class CurrencySerializer implements JsonSerializer<BasicCurrency>, JsonDeserializer<BasicCurrency> {

    @Override
    public BasicCurrency deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject object = json.getAsJsonObject();
        String name = object.getAsJsonPrimitive("cName").getAsString();
        return new BasicCurrency(new ResourceLocation(name));
    }

    @Override
    public JsonElement serialize(BasicCurrency src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject object = new JsonObject();
        object.addProperty("cName", src.getIdentity());
        return object;
    }
}
