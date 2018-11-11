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
package cn.herofrog.maven.plugin.visitor;

import com.google.common.collect.Lists;
import cn.herofrog.maven.plugin.ClassVisitException;
import cn.herofrog.maven.plugin.raml.resource.RestfulResource;
import cn.herofrog.maven.plugin.support.ClassResourceResolver;
import lombok.RequiredArgsConstructor;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.Opcodes;

import java.io.IOException;
import java.util.List;

/**
 * springmvc-raml: SpringControllerScan
 *
 * @author huzhenglin (Employee ID: 17031596)
 * @version 1.0.0, 2018-11-02 15:14
 * @since 1.0.0, 2018-11-02 15:14
 */
@RequiredArgsConstructor
public class WebComponentScan {

    private final ClassResourceResolver resourceResolver;

    public List<RestfulResource> scan(String[] basePackages, ApiVisitContext apiVisitContext) {
        List<String> resources = resourceResolver.scanClass(basePackages);
        WebClassVisitor classVisitor = new WebClassVisitor(apiVisitContext);
        List<RestfulResource> result = Lists.newLinkedList();
        apiVisitContext.setApiConsumer(result::add);
        resources.forEach(resource -> {
            try {
                ClassReader classReader = new ClassReader(resourceResolver.getInputStream(resource));
                classReader.accept(classVisitor, Opcodes.ACC_MANDATED | Opcodes.ACC_VARARGS);
            } catch (IOException e) {
                throw new ClassVisitException("Can't read class " + resource,e);
            }
        });
        return result;
    }


}
