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
package cn.herofrog.maven.plugin.raml.resource;

import cn.herofrog.maven.plugin.raml.ObjectNodeBuilder;
import com.google.common.collect.Lists;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.objectweb.asm.Type;
import org.raml.yagi.framework.nodes.KeyValueNode;
import org.raml.yagi.framework.nodes.Node;
import org.raml.yagi.framework.nodes.ObjectNode;
import org.raml.yagi.framework.nodes.ObjectNodeImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collector;

import static cn.herofrog.maven.plugin.raml.ObjectNodeBuilder.createKeyValue;
import static cn.herofrog.maven.plugin.raml.ObjectNodeBuilder.createObjectNodee;

/**
 * Spring restful controller method
 *
 * @author huzhenglin (Employee ID: 17031596)
 * @version 1.0.0, 2018-11-02 15:01
 * @since 1.0.0, 2018-11-02 15:01
 */
@Getter
@Accessors(chain = true)
@ToString
public class RestfulResource {
    private final String path;
    private String method = "get";
    private List<Parameter> headerParameters = Lists.newLinkedList();
    private List<Parameter> pathParameters = Lists.newLinkedList();
    private List<Parameter> requestParameters = Lists.newLinkedList();
    private Type requestBody;

    @Setter
    private Type responseBody;
    private static final Pattern PATH_VARIABLE_PATTERN = Pattern.compile("\\{(.+?)}");

    public RestfulResource(String path) {
        this.path = path;
        parseUriParameter();
    }

    public void addParameter(Parameter parameter) {
        switch (parameter.getParameterType()) {
            case HEADER:
                headerParameters.add(parameter);
                break;
            case PATH:
                pathParameters.add(parameter);
                break;
            case REQUEST:
                requestParameters.add(parameter);
                break;
            case BODY:
                requestBody = parameter.getValueType();
                break;
        }
    }

    public Node raml() {
        Node responseType = ObjectNodeBuilder.builder()
                .field("type", "typeName")
                .field("example", "{\"field\":\"value\"}")
                .create();
        Node responses = ObjectNodeBuilder.createKeyValue(responseType, "application/json", "body", "200");
        ObjectNode methodValueNode = ObjectNodeBuilder.builder()
                .field("headers", parameterNode(headerParameters))
                .field("queryParameters", parameterNode(requestParameters))
                .field("responses", responses)
                .create();
        Node descriptionNode = createKeyValue("description", "method description here!!");
        KeyValueNode uriParametersNode = ObjectNodeBuilder.createKeyValue("uriParameters", parameterNode(pathParameters));
        KeyValueNode methodNode = ObjectNodeBuilder.createKeyValue(method, methodValueNode);
        return createKeyValue(path, createObjectNodee(descriptionNode, uriParametersNode, methodNode));
    }

    private void parseUriParameter() {
        Matcher matcher = PATH_VARIABLE_PATTERN.matcher(path);
        while (matcher.find()) {
            String name = matcher.group(1);
            Parameter parameter = new Parameter();
            parameter
                    .setParameterType(ParameterType.PATH)
                    .setName(name)
                    .setValueType(Type.getType(String.class))
                    .setRequired(true);
            addParameter(parameter);
        }
    }

    private static ObjectNode parameterNode(List<Parameter> parameters) {
        return parameters.stream().map(Parameter::raml).collect(objectNodeCollector());
    }

    private static Collector<Node, List<Node>, ObjectNode> objectNodeCollector() {
        return Collector.of(ArrayList::new, List::add,
                (left, right) -> {
                    left.addAll(right);
                    return left;
                }, nodes -> {
                    ObjectNode result = new ObjectNodeImpl();
                    nodes.forEach(result::addChild);
                    return result;
                });
    }

}
