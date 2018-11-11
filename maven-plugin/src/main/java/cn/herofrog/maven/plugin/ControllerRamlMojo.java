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

import cn.herofrog.maven.plugin.raml.resource.RestfulResource;
import cn.herofrog.maven.plugin.raml.RamlApi;
import cn.herofrog.maven.plugin.raml.RamlDocumentGenerator;
import cn.herofrog.maven.plugin.spring.SpringApiVisitContext;
import cn.herofrog.maven.plugin.support.ClassResourceResolver;
import cn.herofrog.maven.plugin.visitor.ApiVisitContext;
import cn.herofrog.maven.plugin.visitor.WebComponentScan;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apache.maven.project.MavenProject;

import java.io.File;
import java.util.List;
import java.util.stream.Collectors;

/**
 * The maven plugin mojo of transform spring restful controller/resource to raml 1.0 specification file
 **/
@Mojo(name = "controller2raml", requiresDependencyResolution = ResolutionScope.COMPILE)
public class ControllerRamlMojo extends AbstractMojo {

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
    private File classesDirectory;

    @Parameter(property = "basePackages",defaultValue = "${project.groupId}")
    private String basePackages;

    @Parameter(property = "api.title")
    private String title = "Project Spring Restful Api Definition";

    @Parameter(property = "api.baseUri")
    private String baseUri = "http://{host}:8080";

    @Parameter(property = "api.version")
    private String version = "1.0";


    public void execute() throws MojoExecutionException {
        ClassResourceResolver resourceResolver = buildResourceResolver();
        WebComponentScan webComponentScan = new WebComponentScan(resourceResolver);
        ApiVisitContext apiVisitContext = new SpringApiVisitContext();
        List<RestfulResource> resources = webComponentScan.scan(basePackages.split(","), apiVisitContext);
        RamlApi api = new RamlApi(title, version, baseUri);
        String fileName = "project-api-definition.yaml";
        File outFile = new File(outputDirectory, fileName);
        RamlDocumentGenerator documentGenerator = new RamlDocumentGenerator();
        documentGenerator.writeResources(outFile, api, resources);
        getLog().info("Create api yaml successfully [" + outFile.getPath() + "]");
    }

    @SuppressWarnings("unchecked")
    private ClassResourceResolver buildResourceResolver() {
        List<Artifact> artifacts = project.getCompileArtifacts();
        List<File> dependencies = artifacts.stream()
                .filter(artifact -> "jar".equals(artifact.getType()))
                .map(Artifact::getFile)
                .collect(Collectors.toList());
        return new ClassResourceResolver(classesDirectory.getPath(), dependencies);
    }


}
