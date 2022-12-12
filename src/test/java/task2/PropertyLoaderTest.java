package task2;

import entity.*;
import org.junit.Assert;
import org.junit.Test;

import java.nio.file.Path;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class PropertyLoaderTest {
        private static final String PROPERTY_PATH = "src/test/resources/classLoader.properties";

        @Test
        public void testPositiveLoadEveryTypeOfLoading() throws NoSuchMethodException {
                TestClassSuccess actual  = PropertyLoader.loadFromProperties(TestClassSuccess.class, Path.of(PROPERTY_PATH));
                TestClassSuccess expected = new TestClassSuccess();
                expected.setStringProperty("value1");
                expected.setWrongName("customStringValue");
                expected.setNumberProperty(10);
                expected.setMyNumber(10);
                Instant instant = LocalDateTime.parse("29.11.2022 18:30", DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"))
                        .toInstant(ZoneOffset.UTC);
                expected.setTime(instant);
                expected.setTimeProperty(instant);
                Assert.assertEquals(actual, expected);
        }

        @Test(expected = IllegalArgumentException.class)
        public void testShouldThrowIllegalArgumentException_WhenFieldNamesDontMatch()  {
                WrongFieldNameClass wrongFieldNameClass = PropertyLoader.loadFromProperties(WrongFieldNameClass.class, Path.of(PROPERTY_PATH));
        }

        @Test(expected = IllegalArgumentException.class)
        public void testShouldThrowIllegalArgumentException_WhenAnnotationNamesDontMatch()  {
                WrongAnnotationNameClass wrongAnnotationNameClass = PropertyLoader.loadFromProperties(WrongAnnotationNameClass.class, Path.of(PROPERTY_PATH));
        }

        @Test(expected = IllegalArgumentException.class)
        public void testShouldThrowIllegalArgumentException_whenNoDateFormat(){
                NoDateFormat noDateFormat = PropertyLoader.loadFromProperties(NoDateFormat.class, Path.of(PROPERTY_PATH));
        }

        @Test(expected = DateTimeParseException.class)
        public void testShouldThrowIllegalArgumentException_whenWrongDateFormat(){
                WrongDateFormat dateFormat = PropertyLoader.loadFromProperties(WrongDateFormat.class, Path.of(PROPERTY_PATH));
        }

        @Test(expected = IllegalArgumentException.class)
        public void testShouldThrowIllegalArgumentException_whenWrongFieldType(){
                WrongFieldTypeClass wrongFieldTypeClass = PropertyLoader.loadFromProperties(WrongFieldTypeClass.class, Path.of(PROPERTY_PATH));
        }

        @Test(expected = RuntimeException.class)
        public void testShouldThrowRuntimeException_whenNoSetter(){
                NoSetterClass noSetterClass = PropertyLoader.loadFromProperties(NoSetterClass.class, Path.of(PROPERTY_PATH));
        }
}