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
package cn.herofrog.maven.plugin.support;

import com.google.common.collect.Lists;
import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;


/**
 * springmvc-raml: ResourceResolver
 *
 * @author huzhenglin (Employee ID: 17031596)
 * @version 1.0.0, 2018-11-02 16:56
 * @since 1.0.0, 2018-11-02 16:56
 */
public class ClassResourceResolver {
    private final ClassLoader classLoader;

    public ClassResourceResolver(String compileDir, List<File> dependencyJars) {
        classLoader = buildClassLoader(compileDir,dependencyJars);
    }

    public List<String> scanClass(String... basePackages) {
        return Stream.of(basePackages).flatMap(this::scanClass0).collect(toList());
    }

    public InputStream getInputStream(String className){
        String resource = className.replace('.','/').concat(".class");
        return classLoader.getResourceAsStream(resource);
    }

    public Class loadClass(String className) throws ClassNotFoundException {
        return classLoader.loadClass(className);
    }

    private ClassLoader buildClassLoader(String compileDir, List<File> dependencyJars ){
        List<URL> dependencies = dependencyJars.stream()
                .map(ClassResourceResolver::getFileURL)
                .collect(Collectors.toList());
        dependencies.add(getFileURL(new File(compileDir)));
        return new URLClassLoader(dependencies.toArray(new URL[]{}));
    }

    @SneakyThrows
    private static URL getFileURL(File file) {
        return file.toURI().toURL();
    }


    @SneakyThrows
    private Stream<String> scanClass0(String backPackage)  {
        String packagePath = backPackage.replace('.','/').concat("/");
        Enumeration<URL> rootResources = classLoader.getResources(packagePath);
        List<String> result = Lists.newLinkedList();
        while (rootResources.hasMoreElements()) {
            URL url = rootResources.nextElement();
            result.addAll(findAllClass(url,packagePath));
        }
        return result.stream();
    }

    private List<String> findAllClass(URL url, String packagePath) throws IOException {
        File file = new File(url.getFile());
        if("jar".equals(url.getProtocol())){
            JarURLConnection connection = (JarURLConnection) url.openConnection();
            return doFindJarClass(connection.getJarFile(),packagePath);
        }else if("file".equals(url.getProtocol())){
            return doFindDirectoryClass(file,packagePath);
        }else {
            return Collections.emptyList();
        }

    }

    @SneakyThrows
    private List<String> doFindJarClass(JarFile jarFile, String packagePath) {
        Enumeration<JarEntry> entries = jarFile.entries();
        List<String> result = Lists.newLinkedList();
        while (entries.hasMoreElements()) {
            JarEntry jarEntry = entries.nextElement();
            String entryName = jarEntry.getName();
            if(entryName.startsWith(packagePath) && entryName.endsWith(".class"))
                result.add(getClassName(entryName,packagePath));
        }
        return result;
    }


    private List<String> doFindDirectoryClass(File directory, String packagePath) {
        List<String> result = Lists.newLinkedList();
        doFindDirectoryClass(directory,packagePath,result);
        return result;
    }

    private void doFindDirectoryClass(File file, String packagePath, List<String> result) {
        if(file.isDirectory()){
            File[] files = file.listFiles();
            if (files == null) return;
            for (File subFile : files) {
                doFindDirectoryClass(subFile,packagePath,result);
            }
        }else if(isClassFile(file)){
            result.add(getClassName(file.getAbsolutePath(),packagePath));
        }
    }


    private boolean isClassFile(File file) {
        return file.getName().endsWith(".class");
    }

    private String getClassName(String filePath, String packagePath) {
        String normalizePath = filePath.replace('\\','/');
        normalizePath = StringUtils.stripEnd(normalizePath,".class");
        String relativePath = StringUtils.substringAfterLast(normalizePath,packagePath);
        return packagePath.concat(relativePath).replace('/','.');
    }




}
