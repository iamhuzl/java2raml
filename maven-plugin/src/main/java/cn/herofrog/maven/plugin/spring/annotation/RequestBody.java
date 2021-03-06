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

import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AnnotationNode;

/**
 * springmvc-raml: RequestMapping
 *
 * @author huzhenglin (Employee ID: 17031596)
 * @version 1.0.0, 2018-11-05 11:03
 * @since 1.0.0, 2018-11-05 11:03
 */
public class RequestBody extends MvcAnnotation<RequestBody> {
    private static final Type TYPE = Type.getObjectType("org/springframework/web/bind/annotation/RequestBody");

    @Override
    public Type getType() {
        return TYPE;
    }

    @Override
    public RequestBody parse(AnnotationNode annotationNode) {
        return this;
    }

}
