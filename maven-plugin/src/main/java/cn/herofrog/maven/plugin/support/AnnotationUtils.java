/*
 * Copyright [2018] [zhenglin Hu]
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package cn.herofrog.maven.plugin.support;

import org.objectweb.asm.tree.AnnotationNode;

import java.lang.reflect.Array;
import java.util.List;

/**
 * springmvc-raml: AnnotationUtils
 *
 * @author huzhenglin (Employee ID: 17031596)
 * @version 1.0.0, 2018-11-05 10:46
 * @since 1.0.0, 2018-11-05 10:46
 */
public class AnnotationUtils {

    public static Object getAnnotationValue(AnnotationNode annotation, String name, String alias) {
        List values = annotation.values;
        if (values == null) return null;
        for (int i = 0; i < values.size(); i = i + 2) {
            Object attrName = values.get(i);
            Object value = getFirstValue(values.get(i + 1));
            if (value == null) continue;
            if (attrName.equals(name) || attrName.equals(alias)) return value;
        }
        return null;
    }

    private static Object getFirstValue(Object array) {
        if (array == null) return null;
        if (array instanceof List && !((List) array).isEmpty()) return ((List) array).get(0);
        if (array.getClass().isArray() && Array.getLength(array) != 0) Array.get(array, 0);
        return null;
    }
}
