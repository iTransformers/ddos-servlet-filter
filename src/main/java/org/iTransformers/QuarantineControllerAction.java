package org.iTransformers;

import java.util.Map;

/**
 * Created by vasko on 9/25/14.
 */
public interface QuarantineControllerAction {
    void enterQuarantine(String prefix, Map<String, Object> params);
    void exitQuarantine(String prefix, Map<String, Object> params);
}
