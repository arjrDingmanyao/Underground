package com.creditcloud.payment.utils;

import com.creditcloud.payment.model.chinapnr.base.BaseRequest;
import java.util.HashMap;
import java.util.Map;
import javax.ws.rs.core.Form;
import org.apache.commons.beanutils.BeanMap;

public class FormUtils {

    public static Form getForm(BaseRequest request) {
        Form form = new Form();
        for (Map.Entry<String, String> entry : getRequestValues(request).entrySet()) {
            form.param((String) entry.getKey(), (String) entry.getValue());
        }
        return form;
    }

    public static Map<String, String> getRequestValues(BaseRequest request) {
        Map<String, String> result = new HashMap();
        for (Object entryObj : new BeanMap(request).entrySet()) {
            Map.Entry entry = (Map.Entry) entryObj;
            String key = entry.getKey().toString();
            String value = entry.getValue() == null ? "" : entry.getValue().toString();
            if (!key.equalsIgnoreCase("class")) {
                String newKey = key.substring(0, 1).toUpperCase().concat(key.substring(1));
                result.put(newKey, value);
            }
        }
        return result;
    }
}
