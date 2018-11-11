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
package cn.herofrog.maven.plugin;

import com.google.common.collect.Lists;
import cn.herofrog.maven.plugin.raml.RamlDocumentGenerator;
import cn.herofrog.maven.plugin.support.ClassResourceResolver;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apache.maven.project.MavenProject;

import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.util.List;
import java.util.stream.Collectors;

/**
 * The maven plugin mojo of transform Java Pojo class to raml 1.0 specification file
 *
 * @author huzhenglin (Employee ID: 17031596)
 * @version 1.0.0, 2018-11-08 15:14
 * @since 1.0.0, 2018-11-08 15:14
 */
@Mojo(name = "pojo2raml", requiresDependencyResolution = ResolutionScope.COMPILE)
public class PojoRamlMojo extends AbstractMojo {
    @Parameter(defaultValue = "${project}", readonly = true)
    protected MavenProject project;

    /**
     * The directory for the generated WAR.
     */
    @Parameter(defaultValue = "${project.build.directory}", required = true)
    private String outputDirectory;

    /**
     * The directory containing compiled classes.
     */
    @Parameter(defaultValue = "${project.build.outputDirectory}", required = true, readonly = true)
    private String classesDirectory;

    /**
     * The pojo class name
     */
    @Parameter(property = "targetClass")
    private String targetClass;

    /**
     * The package include pojo class
     */
    @Parameter(property = "targetPackage")
    private String targetPackage;

    @Override
    public void execute() throws MojoExecutionException {
        ClassResourceResolver classResourceResolver = buildClassResourceResolver();
        List<String> pojoClassList = Lists.newLinkedList();
        if (targetClass != null)
            pojoClassList.add(targetClass);
        if (targetPackage != null)
            pojoClassList.addAll(classResourceResolver.scanClass(targetPackage));
        if(pojoClassList.isEmpty())return;
        try {
            generateSchema(pojoClassList, classResourceResolver);
        } catch (Exception e) {
            throw new MojoExecutionException(e.getMessage(), e);
        }
    }

    @SuppressWarnings("unchecked")
    private void generateSchema(List<String> classes, ClassResourceResolver classResourceResolver) throws IOException, ClassNotFoundException {
        File outFile = new File(outputDirectory, "project-api-types.yaml");
        RamlDocumentGenerator documentGenerator = new RamlDocumentGenerator();
        Writer writer = documentGenerator.openWriter(outFile);
        documentGenerator.beginTypes(writer);
        for (String className : classes) {
            Class clazz = classResourceResolver.loadClass(className);
            documentGenerator.writeType(clazz, writer);
            getLog().info("Generator RAML json schema for class " + className);
        }
        writer.close();
        getLog().info("Generator RAML json schema file " + outFile.getPath());
    }

    @SuppressWarnings("unchecked")
    private ClassResourceResolver buildClassResourceResolver() {
        List<Artifact> artifacts = project.getCompileArtifacts();
        List<File> dependencies = artifacts.stream()
                .filter(artifact -> "jar".equals(artifact.getType()))
                .map(Artifact::getFile)
                .collect(Collectors.toList());
        return new ClassResourceResolver(classesDirectory, dependencies);
    }


}
