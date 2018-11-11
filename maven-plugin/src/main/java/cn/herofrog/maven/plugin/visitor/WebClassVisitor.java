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

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AnnotationNode;
import org.objectweb.asm.tree.MethodNode;

/**
 * springmvc-raml: ControllerClassVisitor
 *
 * @author huzhenglin (Employee ID: 17031596)
 * @version 1.0.0, 2018-11-04 14:42
 * @since 1.0.0, 2018-11-04 14:42
 */
class WebClassVisitor extends ClassVisitor {

    private final ApiVisitContext apiVisitContext;

    public WebClassVisitor(ApiVisitContext apiVisitContext) {
        super(Opcodes.ASM5);
        this.apiVisitContext = apiVisitContext;
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
        return new MethodNode(api, access, name, desc, signature, exceptions) {
            @Override
            public void visitEnd() {
                apiVisitContext.visitMethod(this);
            }
        };
    }

    @Override
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        super.visit(version, access, name, signature, superName, interfaces);
        apiVisitContext.beginVisit();
    }

    @Override
    public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
        return new AnnotationNode(Opcodes.ASM5, desc) {
            @Override
            public void visitEnd() {
                apiVisitContext.visitClassAnnotation(this);
            }
        };
    }

    @Override
    public void visitEnd() {
        apiVisitContext.endVisit();
    }


}
