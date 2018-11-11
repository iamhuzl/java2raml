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

import lombok.Data;
import lombok.experimental.Accessors;
import org.raml.builder.SimpleArrayNode;
import org.raml.yagi.framework.nodes.ArrayNode;
import org.raml.yagi.framework.nodes.ObjectNode;

/**
 * Base raml api info defined in maven plugin configurations
 *
 * @author huzhenglin (Employee ID: 17031596)
 * @version 1.0.0, 2018-11-09 14:13
 * @since 1.0.0, 2018-11-09 14:13
 */
@Data
@Accessors(chain = true)
public class RamlApi {
    private String title;
    private String version;
    private String baseUri;

    public RamlApi() {
    }

    public RamlApi(String title, String version, String baseUri) {
        this.title = title;
        this.version = version;
        this.baseUri = baseUri;
    }

    public ObjectNode toNode(){
        ObjectNode root = ObjectNodeBuilder.builder()
                .field("title",title)
                .field("baseUri",baseUri)
                .field("version",version)
                .create();
        ArrayNode documentationItems = new SimpleArrayNode();
        ObjectNode apiDescription = ObjectNodeBuilder.builder()
                .field("title", "API Base Rule - 基本规范")
                .field("content", "## API基本规范介绍1\r\n\t\t* 请求\r\n\t\t* 响应")
                .create();
        ObjectNode errorDescription = ObjectNodeBuilder.builder()
                .field("title", "Error Dispose - 错误处理")
                .field("content", "## Error Code Definition - 错误码定义\r\n\t\t* 200 成功 \r\n\t\t* 400 客户端请求错误\r\n\t\t* 500 服务端错误")
                .create();

        documentationItems.addChild(apiDescription);
        documentationItems.addChild(errorDescription);
        root.addChild(ObjectNodeBuilder.createKeyValue("documentation",documentationItems));
        return root;
    }
}
