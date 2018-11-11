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
package cn.herofrog.maven.plugin.spring;

import com.google.common.collect.Lists;
import cn.herofrog.maven.plugin.raml.resource.Parameter;
import cn.herofrog.maven.plugin.raml.resource.RestfulResource;
import cn.herofrog.maven.plugin.spring.annotation.*;
import cn.herofrog.maven.plugin.visitor.ApiVisitContext;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AnnotationNode;
import org.objectweb.asm.tree.LocalVariableNode;
import org.objectweb.asm.tree.MethodNode;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

import static org.apache.commons.lang3.StringUtils.removeEnd;
import static org.apache.commons.lang3.StringUtils.removeFirst;

/**
 * Withdraw {@link RestfulResource} by visiting spring mvc controller with <code>RequestMapping</code>
 * and method with <code>RequestMapping</code>
 *
 * @author huzhenglin (Employee ID: 17031596)
 * @version 1.0.0, 2018-11-04 16:06
 * @since 1.0.0, 2018-11-04 16:06
 */
public class SpringApiVisitContext implements ApiVisitContext {
    private static final String SEPARATOR = "/";

    private boolean isController = false;

    private boolean isRestController = false;

    private String baseUri;

    @Getter
    private List<RestfulResource> apis = Lists.newLinkedList();

    @Setter
    private Consumer<RestfulResource> apiConsumer;

    @Override
    public void beginVisit() {
        isController = false;
        isRestController = false;
        baseUri = null;
    }


    @Override
    @SuppressWarnings("unchecked")
    public void visitMethod(MethodNode methodNode) {
        if (!isController) return;
        List<AnnotationNode> methodAnnotations = methodNode.visibleAnnotations;
        Optional<RequestMapping> requestMapping = getAnnotation(methodAnnotations, RequestMapping.class);
        if (!requestMapping.isPresent()) return;
        boolean hasResponseBody = isRestController || getAnnotation(methodAnnotations, ResponseBody.class).isPresent();
        if (!hasResponseBody) {
            return;
        }
        String apiPath = removeEnd(baseUri, SEPARATOR) + SEPARATOR + removeFirst(requestMapping.get().getPath(), SEPARATOR);
        RestfulResource restfulApi = new RestfulResource(apiPath);
        List<LocalVariableNode> localVariables = methodNode.localVariables;

        //get parameter name through localVariables,ignore the first param [this]
        int parameterLength = localVariables.size() - 1;
        for (int i = 0; i < parameterLength; i++) {
            LocalVariableNode localVariable = localVariables.get(i + 1);
            List<AnnotationNode> parameterAnnotations = methodNode.visibleParameterAnnotations[i];
            Optional<Parameter> parameter = parseParameter(localVariable, parameterAnnotations);
            parameter.ifPresent(restfulApi::addParameter);
        }
        restfulApi.setResponseBody(Type.getReturnType(methodNode.desc));
        apiConsumer.accept(restfulApi);
    }

    private Optional<Parameter> parseParameter(LocalVariableNode localVariable, List<AnnotationNode> parameterAnnotations) {
        Optional<RequestBody> requestBody = getAnnotation(parameterAnnotations, RequestBody.class);
        Type valueType = Type.getType(localVariable.desc);
        if (requestBody.isPresent()) {
            return Optional.of(Parameter.ofBody(valueType));
        }
        Optional<RequestParam> requestParam = getAnnotation(parameterAnnotations, RequestParam.class);
        if (requestParam.isPresent()) {
            return Optional.of(Parameter.ofRequest(valueType, localVariable.name, requestParam.get().getName()));
        }

        Optional<RequestHeader> requestHeader = getAnnotation(parameterAnnotations, RequestHeader.class);
        return requestHeader.map(requestHeader1 -> Parameter.ofHeader(valueType, localVariable.name, requestHeader1.getName()));
    }


    @Override
    public void visitClassAnnotation(AnnotationNode annotationNode) {
        switch (annotationNode.desc) {
            case ApiType.CONTROLLER:
            case ApiType.REST_CONTROLLER:
                isController = true;
                isRestController = annotationNode.desc.equals(ApiType.REST_CONTROLLER);
                break;
            case ApiType.REQUEST_MAPPING:
                RequestMapping requestMapping = new RequestMapping();
                requestMapping.parse(annotationNode);
                this.baseUri = requestMapping.getPath();
        }
    }

    @Override
    public void endVisit() {
    }


    @SneakyThrows
    private static <T extends MvcAnnotation<T>> Optional<T> getAnnotation(List<AnnotationNode> annotations, Class<T> annotationClass) {
        if (annotations == null) return Optional.empty();
        T annotation = annotationClass.newInstance();
        Optional<AnnotationNode> node = annotations.stream()
                .filter(annotation::match)
                .findFirst();
        return node.map(annotation::parse);
    }

}
