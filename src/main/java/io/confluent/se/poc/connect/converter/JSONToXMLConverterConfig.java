package io.confluent.se.poc.connect.converter;

import org.apache.kafka.common.config.ConfigDef;
import org.apache.kafka.common.config.ConfigDef.Importance;
import org.apache.kafka.common.config.ConfigDef.Type;
import org.apache.kafka.common.config.ConfigDef.Width;
import org.apache.kafka.connect.storage.*;

import java.util.Map;

public class JSONToXMLConverterConfig extends ConverterConfig {

  public static final String ENCODING_CONFIG = "converter.encoding";
  public static final String ENCODING_DEFAULT = "UTF8";
  private static final String ENCODING_DOC = "The name of the Java character set to use for encoding strings as byte arrays.";
  private static final String ENCODING_DISPLAY = "Encoding";

  private final static ConfigDef CONFIG;

  static {
    CONFIG = ConverterConfig.newConfigDef();
    CONFIG.define(ENCODING_CONFIG, Type.STRING, ENCODING_DEFAULT, Importance.HIGH, ENCODING_DOC, null, -1, Width.MEDIUM,ENCODING_DISPLAY);
  }

  public static ConfigDef configDef() {
    return CONFIG;
  }

  public JSONToXMLConverterConfig(Map<String, ?> props) {
    super(CONFIG, props);
  }

  public String encoding() {
    return getString(ENCODING_CONFIG);
  }
}
