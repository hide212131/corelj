/*
 * This source is a modified version of the following source.
 * https://github.com/zolyfarkas/spf4j/blob/master/spf4j-asm/src/main/java/org/spf4j/base/asm/MethodInvocationClassVisitor.java
 */
/*
 * Copyright (c) 2001-2017, Zoltan Farkas All Rights Reserved.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 *
 * Additionally licensed with:
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package hide212131.corelj.visitor.vmsim;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.objectweb.asm.*;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Stack;

public class MethodInvocationVisitor extends MethodVisitor {

    private final String methodDesc;
    private final String methodName;
    private final Stack<Object> stack;
    private final VariableMap variableMap = new VariableMap();
    protected Object peek;
    private int lineNumber;

    public MethodInvocationVisitor(final int api, final String methodDesc, final String methodName) {
        super(api);
        this.methodDesc = methodDesc;
        this.methodName = methodName;
        this.stack = new Stack<>();
        Type[] argumentTypes = Type.getArgumentTypes(methodDesc);
        for (int i = 0; i < argumentTypes.length; i++) {
            stack.add(new ObjectValue(-1));
        }
    }

    @Override
    public void visitMethodInsn(final int opcode, final String owner, final String name, final String desc,
                                final boolean itf) {
        Type returnType = Type.getReturnType(desc);
        Type[] parameterTypes = Type.getArgumentTypes(desc);
        Object[] parameters = new Object[parameterTypes.length];
        for (int i = parameterTypes.length - 1; i >= 0; i--) {
            if (stack.isEmpty()) {
                throw new IllegalStateException("Not enough params in stack for invocation of " + desc + " at "
                        + methodName + '.' + lineNumber);
            } else {
                parameters[i] = stack.pop();
            }
        }
        if (opcode != Opcodes.INVOKESTATIC && !stack.isEmpty()) {
            peek = stack.pop();
        } else {
            peek = null;
        }
        if (returnType != Type.VOID_TYPE) {
            stack.push(new ObjectValue(opcode, returnType.getClassName())); // return value
        }
    }

    @Override
    public void visitLdcInsn(final Object o) {
        stack.push(o); // push the constant to the stack.
    }

    @Override
    public void visitTypeInsn(final int opcode, final String type) {
        stack.push(new ObjectValue(opcode, type));
    }

    @Override
    @SuppressFBWarnings({"PRMC_POSSIBLY_REDUNDANT_METHOD_CALLS", "CC_CYCLOMATIC_COMPLEXITY"})
    public void visitInsn(final int opcode) {
        Object pop1 = null;
        Object pop2 = null;
        switch (opcode) {
            case Opcodes.ICONST_0:
                stack.push(0);
                break;
            case Opcodes.ICONST_1:
                stack.push(1);
                break;
            case Opcodes.ICONST_2:
                stack.push(2);
                break;
            case Opcodes.ICONST_3:
                stack.push(3);
                break;
            case Opcodes.ICONST_4:
                stack.push(4);
                break;
            case Opcodes.ICONST_5:
                stack.push(5);
                break;
            case Opcodes.ICONST_M1:
                stack.push(-1);
                break;
            case Opcodes.LCONST_0:
                stack.push(0L);
                break;
            case Opcodes.LCONST_1:
                stack.push(1L);
                break;
            case Opcodes.FCONST_0:
                stack.push(0f);
                break;
            case Opcodes.FCONST_1:
                stack.push(1f);
                break;
            case Opcodes.FCONST_2:
                stack.push(2f);
                break;
            case Opcodes.DCONST_0:
                stack.push(0d);
                break;
            case Opcodes.DCONST_1:
                stack.push(1d);
                break;
            case Opcodes.ACONST_NULL:
                stack.push(null);
                break;
            case Opcodes.DUP:
            case Opcodes.DUP2: // we do not differentiate betwen types with mutiple words.
                Object pop = stack.peek();
                stack.push(pop);
                break;
            case Opcodes.DUP2_X1:
            case Opcodes.DUP_X1:
                Object a1 = stack.peek();
                Object b1 = stack.peek();
                stack.push(a1);
                stack.push(b1);
                stack.push(a1);
                break;
            case Opcodes.DUP2_X2:
            case Opcodes.DUP_X2:
                Object a2 = stack.peek();
                Object b2 = stack.peek();
                Object c2 = stack.peek();
                stack.push(a2);
                stack.push(c2);
                stack.push(b2);
                stack.push(a2);
                break;
            case Opcodes.IDIV:
                pop1 = stack.pop();
                pop2 = stack.pop();
                try {
                    stack.push((int) pop2 / (int) pop1);
                } catch (RuntimeException ex) {
                    stack.push(new ObjectValue(opcode));
                }
                break;
            case Opcodes.IMUL:
                pop1 = stack.pop();
                pop2 = stack.pop();
                try {
                    stack.push((int) pop2 * (int) pop1);
                } catch (RuntimeException ex) {
                    stack.push(new ObjectValue(opcode));
                }
                break;
            case Opcodes.IADD:
                pop1 = stack.pop();
                pop2 = stack.pop();
                try {
                    stack.push((int) pop2 + (int) pop1);
                } catch (RuntimeException ex) {
                    stack.push(new ObjectValue(opcode));
                }
                break;
            case Opcodes.ISUB:
                pop1 = stack.pop();
                pop2 = stack.pop();
                try {
                    stack.push((int) pop2 - (int) pop1);
                } catch (RuntimeException ex) {
                    stack.push(new ObjectValue(opcode));
                }
                break;
            case Opcodes.LDIV:
                pop1 = stack.pop();
                pop2 = stack.pop();
                try {
                    stack.push((long) pop2 / (long) pop1);
                } catch (RuntimeException ex) {
                    stack.push(new ObjectValue(opcode));
                }
                break;
            case Opcodes.LMUL:
                pop1 = stack.pop();
                pop2 = stack.pop();
                try {
                    stack.push((long) pop2 * (long) pop1);
                } catch (RuntimeException ex) {
                    stack.push(new ObjectValue(opcode));
                }
                break;
            case Opcodes.LADD:
                pop1 = stack.pop();
                pop2 = stack.pop();
                try {
                    stack.push((long) pop2 + (long) pop1);
                } catch (RuntimeException ex) {
                    stack.push(new ObjectValue(opcode));
                }
                break;
            case Opcodes.LSUB:
                pop1 = stack.pop();
                pop2 = stack.pop();
                try {
                    stack.push((long) pop2 - (long) pop1);
                } catch (RuntimeException ex) {
                    stack.push(new ObjectValue(opcode));
                }
                break;
            default:
                stack.push(new ObjectValue(opcode)); // assume something will end up on the stack
        }
    }

    @Override
    public void visitFieldInsn(final int opcode, final String owner, final String name, final String desc) {
        switch (opcode) {
            case Opcodes.GETSTATIC:
                Class<?> clasz;
                try {
                    clasz = Class.forName(owner.replace('/', '.'));
                } catch (ClassNotFoundException ex) {
                    stack.push(new ObjectValue(opcode)); // assume something will end up on the stack
                    break;
                }
                Field declaredField;
                try {
                    declaredField = clasz.getDeclaredField(name);
                } catch (NoSuchFieldException | SecurityException ex) {
                    stack.push(new ObjectValue(opcode)); // assume something will end up on the stack
                    break;
                }
                if ((declaredField.getModifiers() & Modifier.FINAL) == Modifier.FINAL) {
                    AccessController.doPrivileged(new PrivilegedAction<Void>() {
                        @Override
                        public Void run() {
                            declaredField.setAccessible(true);
                            return null;
                        }
                    });
                    Object value;
                    try {
                        value = declaredField.get(null);
                    } catch (IllegalArgumentException | IllegalAccessException ex) {
                        stack.push(new ObjectValue(opcode)); // assume something will end up on the stack
                        break;
                    }
                    stack.push(value);
                } else {
                    stack.push(new ObjectValue(opcode)); // assume something will end up on the stack
                }
                break;
            case Opcodes.GETFIELD:
                stack.push(new ObjectValue(opcode)); // assume something will end up on the stack
                break;
            case Opcodes.PUTSTATIC:
            case Opcodes.PUTFIELD:
                if (!stack.isEmpty()) {
                    peek = stack.pop();
                }
                break;
            default:
                throw new IllegalStateException(" Illegal opcode = " + opcode + " in context");
        }

    }

    @Override
    public void visitIntInsn(final int opcode, final int operand) {
        switch (opcode) {
            case Opcodes.BIPUSH:
            case Opcodes.SIPUSH:
                stack.push(operand);
                break;
            default:
                stack.push(new ObjectValue(opcode)); // assume something will end up on the stack
        }
    }

    @Override
    public void visitVarInsn(final int opcode, final int localVarIdx) {
        if (opcode >= 21 && opcode <= 45) { // *LOAD* from local var instructions
            stack.push(variableMap.load(opcode));
        } else if (opcode >= 54 && opcode <= 78) { // *STORE* to local var instructions
            if (!stack.isEmpty()) {
                variableMap.store(opcode, stack.pop());
            }
//        else {
//          throw new RuntimeException("Not enough params in stack for instr " + opcode + " at "
//                  + className + '.' + methodName + '.' + lineNumber);
//        }
        } else {
            stack.push(new ObjectValue(opcode)); // assume something will end up on the stack
        }
    }

    @Override
    public void visitMultiANewArrayInsn(final String desc, final int dims) {
        stack.push(new ObjectValue(Opcodes.MULTIANEWARRAY)); // assume something will end up on the stack
    }

    @Override
    public void visitInvokeDynamicInsn(final String name, final String desc, final Handle bsm,
                                       final Object... bsmArgs) {
        // I am not sure this classic method handling is correct
        Type[] argumentTypes = Type.getArgumentTypes(desc);
        for (int i = 0; i < argumentTypes.length; i++) {
            peek = stack.pop();
        }
        Type returnType = Type.getReturnType(desc);
        if (returnType != Type.VOID_TYPE) {
            stack.push(new ObjectValue(Opcodes.INVOKEDYNAMIC)); // return value
        }
    }

    @Override
    public void visitLineNumber(final int line, final Label start) {
        this.lineNumber = line;
    }
}


