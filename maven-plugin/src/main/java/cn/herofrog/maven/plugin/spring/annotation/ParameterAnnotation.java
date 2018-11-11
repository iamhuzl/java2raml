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
package cn.herofrog.maven.plugin.spring.annotation;

import lombok.Getter;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AnnotationNode;

import static cn.herofrog.maven.plugin.support.AnnotationUtils.getAnnotationValue;

/**
 * springmvc-raml: ParameterAnnotation
 *
 * @author huzhenglin (Employee ID: 17031596)
 * @version 1.0.0, 2018-11-05 17:57
 * @since 1.0.0, 2018-11-05 17:57
 */
public abstract class ParameterAnnotation<T extends ParameterAnnotation> extends MvcAnnotation<T> {

    private final Type type ;

    public ParameterAnnotation() {
        type = Type.getObjectType("org/springframework/web/bind/annotation/" + getClass().getSimpleName());
    }

    protected ParameterAnnotation(String typeDescriptor) {
        type = Type.getObjectType(typeDescriptor);
    }

    @Getter
    protected String name;

    @Getter
    protected boolean required;

    @Getter
    protected String defaultValue;

    @Override
    public Type getType() {
        return type;
    }

    @Override
    public T parse(AnnotationNode annotationNode) {
        this.name = (String) getAnnotationValue(annotationNode, "value", null);
        this.defaultValue = (String) getAnnotationValue(annotationNode, "defaultValue", null);
        return (T) this;
    }
}
