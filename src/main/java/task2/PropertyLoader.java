package task2;

import utils.PropertyReader;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Properties;

public class PropertyLoader {
    //main method to load class instance from properties
    public static <T> T loadFromProperties(Class<T> cls, Path propertiesPath) throws IllegalArgumentException{
        try {
            Properties properties = PropertyReader.readProperties(propertiesPath.toString());
            //instance which fields will be filled with values from properties
            T currentInstance = cls.getConstructor().newInstance();
            //fields with all modifiers
            Field[] fields = cls.getDeclaredFields();
            for (Field field : fields) {
                //checking if annotation is not present or annotation does not have name attribute
                if (!field.isAnnotationPresent(Property.class) || field.getAnnotation(Property.class).name().equals("")){
                    //if property keys contains field name
                    if (properties.containsKey(field.getName()))
                        setFieldValue(field,currentInstance,properties.getProperty(field.getName()));
                    else
                        throw new IllegalArgumentException("Wrong field name: " + field.getName());

                } else {
                    //if annotation is present and has name attribute
                    if (properties.containsKey(field.getAnnotation(Property.class).name()))
                        setFieldValue(field,currentInstance,properties.getProperty(field.getAnnotation(Property.class).name()));
                    else
                        throw new IllegalArgumentException("Wrong annotation name: " + field.getAnnotation(Property.class).name());
                }
               }
            return currentInstance;
        } catch ( NoSuchMethodException | IllegalAccessException | InvocationTargetException | InstantiationException exception){
            throw new RuntimeException(exception);
        }
    }
    //method for setting value into field
    private static <T> void setFieldValue(Field field, T instance, String value) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException{
        //string with setter name for concrete field
        String setterName = "set" + field.getName().substring(0, 1).toUpperCase() + field.getName().substring(1);
        switch (field.getType().getSimpleName()) {
            case "String" -> field.getDeclaringClass().getDeclaredMethod(setterName, field.getType()).invoke(instance, value);
            case "int" -> field.getDeclaringClass().getDeclaredMethod(setterName, field.getType()).invoke(instance, Integer.parseInt(value));
            case "Integer" -> field.getDeclaringClass().getDeclaredMethod(setterName, field.getType()).invoke(instance, Integer.valueOf(value));
            case "Instant" -> {
                //Instant field can be loaded only when annotation is present, and it has format attribute
                if (!field.isAnnotationPresent(Property.class) || (field.getAnnotation(Property.class).format().equals(""))){
                    throw new IllegalArgumentException("Instant field has no format.");}

                    field.getDeclaringClass().getDeclaredMethod(setterName, field.getType())
                            .invoke(instance, LocalDateTime.parse(value, DateTimeFormatter.ofPattern(field.getAnnotation(Property.class).format()))
                                    .toInstant(ZoneOffset.UTC));
            }
            default -> throw new IllegalArgumentException("Wrong field type: " + field.getType().getSimpleName());
        }
    }
}

