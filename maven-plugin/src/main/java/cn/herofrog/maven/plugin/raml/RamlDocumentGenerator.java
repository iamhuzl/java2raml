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

import cn.herofrog.maven.plugin.raml.resource.RestfulResource;
import com.google.common.collect.Sets;
import lombok.SneakyThrows;
import org.raml.builder.TypeDeclarationBuilder;
import org.raml.pojotoraml.PojoToRaml;
import org.raml.pojotoraml.PojoToRamlBuilder;
import org.raml.pojotoraml.Result;
import org.raml.simpleemitter.HandlerList;
import org.raml.simpleemitter.YamlEmitter;
import org.raml.yagi.framework.nodes.ObjectNode;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Set;

/**
 * Create main api raml and pojo types raml files
 *
 * @author huzhenglin (Employee ID: 17031596)
 * @version 1.0.0, 2018-11-06 16:33
 * @since 1.0.0, 2018-11-06 16:33
 */
public class RamlDocumentGenerator {
    private static final String LINE_SEPARATOR = System.lineSeparator();
    private HandlerList handlerList = new HandlerList();
    private PojoToRaml pojoToRaml = PojoToRamlBuilder.create();
    private Set<String> writtenTypes = Sets.newHashSet();

    @SneakyThrows
    public void writeResources(File file, RamlApi api, List<RestfulResource> resources) {
        try (Writer writer = openWriter(file)) {
            writer.write("#%RAML 1.0" + LINE_SEPARATOR);
            ObjectNode apiNode = api.toNode();
            resources.stream().map(RestfulResource::raml).forEach(apiNode::addChild);
            handlerList.handle(apiNode, new YamlEmitter(writer, 0));
        }
    }

    @SneakyThrows
    public void beginTypes(Writer writer) {
        writer.write("#%RAML 1.0 Library" + LINE_SEPARATOR);
        writer.write("types:");
    }

    @SneakyThrows
    public void writeType(Class typeClass, Writer writer) {
        //TODO: write pojo example using jackson
        //TODO: support pojo wrap
        Result result = pojoToRaml.classToRaml(typeClass);
        YamlEmitter emitter = new YamlEmitter(writer, 1);
        for (TypeDeclarationBuilder builder : result.allTypes()) {
            if (writtenTypes.contains(builder.id())) continue;
            handlerList.handle(builder.buildNode(), emitter);
            writtenTypes.add(builder.id());
        }
    }


    public Writer openWriter(File file) throws IOException {
        file.createNewFile();
        return new OutputStreamWriter(new FileOutputStream(file, false), StandardCharsets.UTF_8);
    }

}
