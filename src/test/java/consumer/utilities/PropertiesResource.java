/*
 * Copyright (c) 2018 Thermo Fisher Scientific
 * All rights reserved.
 */


package consumer.utilities;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;


/**
 * Utilities methods
 */
public class PropertiesResource
{
    private static final Logger LOG = LoggerFactory.getLogger(PropertiesResource.class);

    /**
     * Obtain the resource file, convert content to string
     *
     * @param bodyReference - resource reference for the json body
     *
     * @return the string representation of the resource json body
     */
    @Nullable
    public static String getBody(String bodyReference)
    {
        try
        {
            return Resources.toString(Resources.getResource(bodyReference), Charsets.UTF_8);
        }
        catch (IOException e)
        {
            LOG.error(e.getMessage(), e);
        }
        return null;

    }

    /**
     * Obtain the property key in the property resource
     *
     * @param propertyResource - property resource reference
     * @param propertyKey - property key
     *
     * @return a string representing the value pointed to by the property key
     */
    @Nullable
    public static String getProperty(String propertyResource, String propertyKey)
    {
        URL propsResource = PropertiesResource.class.getClassLoader().getResource(propertyResource);

        if (propsResource == null)
        {
            LOG.error("Could not find property resource in classpath: {}", propertyResource);
            return null;
        }

        try (InputStream stream = propsResource.openStream())
        {
            Properties properties = new Properties();
            properties.load(stream);

            if (properties.getProperty(propertyKey) == null)
            {
                LOG.error("Could not find constant: {}", propertyKey);
            }
            return properties.getProperty(propertyKey);
        }
        catch (IOException e)
        {
            LOG.error("Could not read properties resource {} for property {} \nStacktrace: {}", propertyResource, propertyKey, e.getMessage());
        }
        return null;
    }

    /**
     * Obtain the property key in the property resource
     *
     * @param propertyResource - property resource reference
     * @param propertyKey - property key
     * @param defaultValue
     *
     * @return a string representing the value pointed to by the property key
     */
    @Nullable
    public static String getProperty(String propertyResource, String propertyKey, String defaultValue)
    {
        URL propsResource = PropertiesResource.class.getClassLoader().getResource(propertyResource);

        if (propsResource == null)
        {
            LOG.error("Could not find property resource in classpath: {}", propertyResource);
            return null;
        }

        try (InputStream stream = propsResource.openStream())
        {
            Properties properties = new Properties();
            properties.load(stream);
            return properties.getProperty(propertyKey, defaultValue);
        }
        catch (IOException e)
        {
            LOG.error("Could not read properties resource {} for property {} \nStacktrace: {}", propertyResource, propertyKey, e.getMessage());
        }
        return null;
    }

    /**
     * Obtain the properties in the property resource
     *
     * @param propertyResource - property resource reference
     *
     * @return a map representing the keys/values in property resource
     */
    @Nullable
    public static Map<String, String> getProperties(String propertyResource)
    {
        URL propsResource = PropertiesResource.class.getClassLoader().getResource(propertyResource);
        Map<String, String> result = new HashMap<>();

        if (propsResource == null)
        {
            LOG.error("Could not find property resource in classpath: {}", propertyResource);
            return null;
        }

        try (InputStream stream = propsResource.openStream())
        {
            Properties properties = new Properties();
            properties.load(stream);

            Enumeration<?> e = properties.propertyNames();
            while (e.hasMoreElements())
            {
                String key = (String)e.nextElement();
                String value = properties.getProperty(key);
                result.put(key, value);
            }
        }
        catch (IOException e)
        {
            LOG.error("Could not read properties resource {}\nStacktrace: {}", propertyResource, e.getMessage());
            return null;
        }
        return result;
    }
}

