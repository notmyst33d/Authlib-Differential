package com.mojang.authlib.properties;

import com.google.common.collect.ForwardingMultimap;
import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import java.lang.reflect.Type;
import java.util.Iterator;
import java.util.Map;

/* loaded from: authlib-3.3.39.jar:com/mojang/authlib/properties/PropertyMap.class */
public class PropertyMap extends ForwardingMultimap<String, Property> {
    private final Multimap<String, Property> properties = LinkedHashMultimap.create();

    /* JADX INFO: Access modifiers changed from: protected */
    public Multimap<String, Property> delegate() {
        return this.properties;
    }

    /* loaded from: authlib-3.3.39.jar:com/mojang/authlib/properties/PropertyMap$Serializer.class */
    public static class Serializer implements JsonSerializer<PropertyMap>, JsonDeserializer<PropertyMap> {
        public PropertyMap deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            PropertyMap result = new PropertyMap();
            if (json instanceof JsonObject) {
                for (Map.Entry<String, JsonElement> entry : ((JsonObject) json).entrySet()) {
                    if (entry.getValue() instanceof JsonArray) {
                        Iterator it = ((JsonArray) entry.getValue()).iterator();
                        while (it.hasNext()) {
                            JsonElement element = (JsonElement) it.next();
                            result.put(entry.getKey(), new Property(entry.getKey(), element.getAsString()));
                        }
                    }
                }
            } else if (json instanceof JsonArray) {
                Iterator it2 = ((JsonArray) json).iterator();
                while (it2.hasNext()) {
                    JsonObject jsonObject = (JsonObject) it2.next();
                    if (jsonObject instanceof JsonObject) {
                        JsonObject object = jsonObject;
                        String name = object.getAsJsonPrimitive("name").getAsString();
                        String value = object.getAsJsonPrimitive("value").getAsString();
                        if (object.has("signature")) {
                            result.put(name, new Property(name, value, object.getAsJsonPrimitive("signature").getAsString()));
                        } else {
                            result.put(name, new Property(name, value));
                        }
                    }
                }
            }
            return result;
        }

        public JsonElement serialize(PropertyMap src, Type typeOfSrc, JsonSerializationContext context) {
            JsonArray result = new JsonArray();
            for (Property property : src.values()) {
                JsonObject object = new JsonObject();
                object.addProperty("name", property.getName());
                object.addProperty("value", property.getValue());
                if (property.hasSignature()) {
                    object.addProperty("signature", property.getSignature());
                }
                result.add(object);
            }
            return result;
        }
    }
}
