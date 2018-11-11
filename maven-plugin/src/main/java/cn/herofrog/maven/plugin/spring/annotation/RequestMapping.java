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
 * springmvc-raml: RequestMapping
 *
 * @author huzhenglin (Employee ID: 17031596)
 * @version 1.0.0, 2018-11-05 11:03
 * @since 1.0.0, 2018-11-05 11:03
 */
public class RequestMapping extends MvcAnnotation<RequestMapping> {
    private static final Type TYPE = Type.getObjectType("org/springframework/web/bind/annotation/RequestMapping");

    @Getter
    private String path = "";

    @Getter
    private String method = "";

    @Override
    public Type getType() {
        return TYPE;
    }

    @Override
    public RequestMapping parse(AnnotationNode annotationNode) {
        this.path = (String) getAnnotationValue(annotationNode, "value", "path");
        return this;
    }

}
