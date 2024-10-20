package org.example.io;

import lombok.extern.slf4j.Slf4j;

import java.time.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.function.Function;

@Slf4j
public class PropertyResolver {

    Map<String, String> properties = new HashMap<>();

    Map<Class<?>, Function<String, Object>> converters = new HashMap<>();


    /**
     * @param props use Properties for "props.load(fileInput); // 文件输入流"
     */
    public PropertyResolver(Properties props) {

        // 1. from env
        this.properties.putAll(System.getenv());
        // 2. from input
        Set<String> names = props.stringPropertyNames();
        for (String name : names) {
            this.properties.put(name, props.getProperty(name));
        }

        // String类型:
        converters.put(String.class, s -> s);
        // boolean类型:
        converters.put(boolean.class, Boolean::parseBoolean);
        converters.put(Boolean.class, Boolean::valueOf);
        // int类型:
        converters.put(int.class, Integer::parseInt);
        converters.put(Integer.class, Integer::valueOf);
        // 其他基本类型...
        // Date/Time类型:
        converters.put(LocalDate.class, LocalDate::parse);
        converters.put(LocalTime.class, LocalTime::parse);
        converters.put(LocalDateTime.class, LocalDateTime::parse);
        converters.put(ZonedDateTime.class, ZonedDateTime::parse);
        converters.put(Duration.class, Duration::parse);
        converters.put(ZoneId.class, ZoneId::of);
    }

    /**
     * get property and convert type
     * @param key example: "app.name", "${app.name}", "${app.name:default}"
     * @param targetType
     * @return
     * @param <T>
     */
    public <T> T getProperty(String key, Class<T> targetType) {
        String value = getProperty(key);
        if (value == null) {
            return null;
        }
        // 转换为指定类型:
        return convert(targetType, value);
    }

    /**
     * get property
     * @param key example: "app.name", "${app.name}", "${app.name:default}"
     * @return
     */
    public String getProperty(String key) {
        PropertyExpr expr = parsePropertyExpr(key);
        if (expr != null) {
            if (expr.defaultValue != null) {
                return this.properties.getOrDefault(expr.key, expr.defaultValue);
            } else {
                return this.properties.getOrDefault(expr.key, null);
            }
        }
        String value = this.properties.get(key);
        if (value != null) {
            // recursive depth = 1
            return parseValue(value);
        }
        return value;
    }

    String parseValue(String value) {
        PropertyExpr expr = parsePropertyExpr(value);
        if (expr == null) {
            return value;
        }
        if (expr.defaultValue != null) {
            return this.properties.getOrDefault(expr.key, expr.defaultValue);
        } else {
            return this.properties.getOrDefault(expr.key, null);
        }
    }

    @SuppressWarnings("unchecked")
    <T> T convert(Class<?> clazz, String value) {
        Function<String, Object> fn = this.converters.get(clazz);
        if (fn == null) {
            throw new IllegalArgumentException("Unsupported value type: " + clazz.getName());
        }
        return (T) fn.apply(value);
    }

    PropertyExpr parsePropertyExpr(String config) {
        if (config.startsWith("${") && config.endsWith("}")) {
            // 是否存在defaultValue?
            int n = config.indexOf(':');
            if (n == (-1)) {
                // 没有defaultValue: ${key}
                String k = config.substring(2, config.length() - 1);
                return new PropertyExpr(k, null);
            } else {
                // 有defaultValue: ${key:default}
                String k = config.substring(2, n);
                return new PropertyExpr(k, config.substring(n + 1, config.length() - 1));
            }
        }
        return null;
    }

    record PropertyExpr(String key, String defaultValue) {
        // just for return two param
    }
}
