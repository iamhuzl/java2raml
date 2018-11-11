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
package cn.herofrog.maven.plugin.raml;

import org.raml.builder.BooleanNode;
import org.raml.yagi.framework.nodes.*;

/**
 * springmvc-raml: ObjectValueNodeBuilder
 *
 * @author huzhenglin (Employee ID: 17031596)
 * @version 1.0.0, 2018-11-09 17:49
 * @since 1.0.0, 2018-11-09 17:49
 */
public class ObjectNodeBuilder {
    private ObjectNode container = new ObjectNodeImpl();

    private ObjectNodeBuilder() {

    }

    public static ObjectNodeBuilder builder() {
        return new ObjectNodeBuilder();
    }

    public ObjectNodeBuilder field(String name, String value) {
        if (value != null)
            container.addChild(createKeyValue(name, value));
        return this;
    }

    public ObjectNodeBuilder field(String name, boolean value) {
        container.addChild(createKeyValue(name, value));
        return this;
    }

    public ObjectNodeBuilder field(String name, Node value) {
        if (value != null)
            container.addChild(createKeyValue(name, value));
        return this;
    }

    public ObjectNodeBuilder addChild(Node value) {
        if (value != null)
            container.addChild(value);
        return this;
    }

    public ObjectNode create() {
        return this.container;
    }

    public static KeyValueNode createKeyValue(String name, String value) {
        return new KeyValueNodeImpl(new StringNodeImpl(name), new StringNodeImpl(value));
    }


    public static KeyValueNode createKeyValue(String name, boolean value) {
        return new KeyValueNodeImpl(new StringNodeImpl(name), new BooleanNode(value));
    }

    public static KeyValueNode createKeyValue(String name, Node value) {
        return new KeyValueNodeImpl(new StringNodeImpl(name), value);
    }


    public static ObjectNode createObjectNodee(Node ... values) {
        ObjectNode result = new ObjectNodeImpl();
        for (Node value : values) {
            result.addChild(value);
        }
        return result;
    }


    public static KeyValueNode createKeyValue(Node value, String... tree) {
        Node result = value;
        for (String name : tree) {
            result = createKeyValue(name, result);
        }
        return (KeyValueNode) result;
    }
}
