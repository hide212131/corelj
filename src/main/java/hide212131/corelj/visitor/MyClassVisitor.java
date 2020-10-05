package hide212131.corelj.visitor;

import hide212131.corelj.json.ClassEntry;
import hide212131.corelj.json.MethodEntry;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.Opcodes;

public class MyClassVisitor extends ClassVisitor {

    ClassEntry classEntry;
    SignatureFilter methodFilter;
    SignatureFilter methodInsnFilter;

    public MyClassVisitor(SignatureFilter methodFilter, SignatureFilter methodInsnFilter) {
        super(Opcodes.ASM9);
        this.methodFilter = methodFilter;
        this.methodInsnFilter = methodInsnFilter;
    }

    @Override
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        super.visit(version, access, name, signature, superName, interfaces);
        var classEntry = new ClassEntry(name);
        this.classEntry = classEntry;
    }

    @Override
    public MyMethodVisitor visitMethod(int access, String name,
                                       String desc, String signature, String[] exceptions) {

        //System.out.println("Method: " + name + " -- " + desc);
        var methodEntry = new MethodEntry(classEntry, name, desc);
        if (methodFilter.match(methodEntry.signature)) {
            classEntry.add(methodEntry);
            return new MyMethodVisitor(methodInsnFilter, classEntry, methodEntry);
        }
        return null;
    }

    public ClassEntry getClassEntry() {
        return classEntry;
    }

}
