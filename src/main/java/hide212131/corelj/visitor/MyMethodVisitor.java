package hide212131.corelj.visitor;

import hide212131.corelj.json.ClassEntry;
import hide212131.corelj.json.MethodEntry;
import hide212131.corelj.json.MethodInsnEntry;
import hide212131.corelj.visitor.vmsim.MethodInvocationVisitor;
import hide212131.corelj.visitor.vmsim.ObjectValue;
import org.objectweb.asm.Opcodes;

public class MyMethodVisitor extends MethodInvocationVisitor {

    ClassEntry classEntry;
    MethodEntry methodEntry;
    SignatureFilter methodInsnFilter;

    public MyMethodVisitor(SignatureFilter filter, ClassEntry classEntry, MethodEntry methodEntry) {
        super(Opcodes.ASM9, methodEntry.desc, methodEntry.name);
        this.methodInsnFilter = filter;
        this.classEntry = classEntry;
        this.methodEntry = methodEntry;
    }

    @Override
    public void visitMethodInsn(int opcode, String owner,
                                String name, String desc, boolean arg4) {
        super.visitMethodInsn(opcode, owner, name, desc, arg4);
        if (peek != null && peek instanceof ObjectValue) {
            owner = ((ObjectValue) peek).getType();
        }
        var methodInsnEntry = new MethodInsnEntry(owner, name, desc);
        if (methodInsnFilter.match(methodInsnEntry.signature)) {
            //System.out.println("--  opcode  --  " + opcode + " --  owner  --  " + owner + "name  --  " + name + "desc  --  " + desc);
            methodEntry.add(methodInsnEntry);
        }
    }

}
