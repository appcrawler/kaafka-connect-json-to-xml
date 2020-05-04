package io.confluent.se.poc.connect.converter;;

import org.apache.kafka.connect.storage.*;
import org.apache.kafka.common.config.ConfigDef;
import org.apache.kafka.common.errors.SerializationException;
import io.confluent.kafka.serializers.*;
import org.apache.kafka.connect.data.Schema;
import org.apache.kafka.connect.data.SchemaAndValue;
import org.apache.kafka.connect.errors.DataException;
import io.confluent.kafka.serializers.KafkaAvroSerializer;
import io.confluent.kafka.serializers.KafkaAvroDeserializer;

import java.util.HashMap;
import java.util.Map;

import org.json.*;

public class JSONToXMLConverter implements Converter, HeaderConverter {

  private final KafkaAvroSerializer serializer = new KafkaAvroSerializer();
  private final KafkaAvroDeserializer deserializer = new KafkaAvroDeserializer();

  public JSONToXMLConverter() {
  }

  @Override
  public ConfigDef config() {
    return JSONToXMLConverterConfig.configDef();
  }

  @Override
  public void configure(Map<String, ?> configs) {
    JSONToXMLConverterConfig conf = new JSONToXMLConverterConfig(configs);
    String encoding = conf.encoding();

    Map<String, Object> serializerConfigs = new HashMap<>(configs);
    Map<String, Object> deserializerConfigs = new HashMap<>(configs);
    serializerConfigs.put("serializer.encoding", encoding);
    deserializerConfigs.put("deserializer.encoding", encoding);

    boolean isKey = conf.type() == ConverterType.KEY;
    serializer.configure(serializerConfigs, isKey);
    deserializer.configure(deserializerConfigs, isKey);
  }

  @Override
  public void configure(Map<String, ?> configs, boolean isKey) {
    Map<String, Object> conf = new HashMap<>(configs);
    conf.put(JSONToXMLConverterConfig.TYPE_CONFIG, isKey ? ConverterType.KEY.getName() : ConverterType.VALUE.getName());
    configure(conf);
  }

  @Override
  public byte[] fromConnectData(String topic, Schema schema, Object value) {
    try {
      return serializer.serialize(topic, value == null ? null : value.toString());
    } 
    catch (SerializationException e) {
      throw new DataException("Failed to serialize to a string: ", e);
    }
  }

  @Override
  public SchemaAndValue toConnectData(String topic, byte[] value) {
    try {
      Object o  = deserializer.deserialize(topic, value);
      JSONObject j = new JSONObject(o.toString());
      System.out.println(j);
      return new SchemaAndValue(Schema.OPTIONAL_STRING_SCHEMA, XML.toString(j));
    } 
    catch (SerializationException e) {
      throw new DataException("Failed to deserialize string: ", e);
    }
  }

  @Override
  public byte[] fromConnectHeader(String topic, String headerKey, Schema schema, Object value) {
    return fromConnectData(topic, schema, value);
  }

  @Override
  public SchemaAndValue toConnectHeader(String topic, String headerKey, byte[] value) {
    return toConnectData(topic, value);
  }

  @Override
  public void close() {
    // do nothing
  }
}
