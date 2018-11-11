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
import lombok.Data;
import lombok.experimental.Accessors;
import org.apache.commons.lang3.ObjectUtils;
import org.objectweb.asm.Type;
import org.raml.yagi.framework.nodes.*;


/**
 * Query or header parameter definition
 *
 * @author huzhenglin (Employee ID: 17031596)
 * @version 1.0.0, 2018-11-02 15:05
 * @since 1.0.0, 2018-11-02 15:05
 */
@Data
@Accessors(chain = true)
public class Parameter {
    private String name;
    private Type valueType;
    private String description;
    private boolean required;
    private ParameterType parameterType;

    public static Parameter ofBody(Type valueType) {
        return new Parameter().setValueType(valueType).setParameterType(ParameterType.BODY);
    }

    public static Parameter ofRequest(Type valueType, String name, String alias) {
        return new Parameter().setValueType(valueType)
                .setParameterType(ParameterType.REQUEST)
                .setName(ObjectUtils.defaultIfNull(alias, name));
    }

    public static Parameter ofHeader(Type valueType , String name, String alias) {
        return new Parameter().setValueType(valueType)
                .setParameterType(ParameterType.HEADER)
                .setName(ObjectUtils.defaultIfNull(alias, name));
    }

    public Node raml(){
        Node node = ObjectNodeBuilder.builder()
                .field("description",description)
                .field("required",required)
                .field("example","<<example>>")
                .field("type",getRamlType())
                .create();
        return ObjectNodeBuilder.createKeyValue(name,node);
    }

    private String getRamlType(){
        return "string";//TODO convert Type to real raml type
    }
}
