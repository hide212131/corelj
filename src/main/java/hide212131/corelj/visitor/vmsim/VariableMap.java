package hide212131.corelj.visitor.vmsim;

import org.objectweb.asm.Opcodes;

import java.util.HashMap;
import java.util.Map;

public class VariableMap {
    private final Map<Integer, Object> variableMap = new HashMap<>();
    private Map<Integer, Integer> v = new HashMap();

    public VariableMap() {
        v.put(Opcodes.ILOAD, Opcodes.ISTORE);
        v.put(Opcodes.LLOAD, Opcodes.LSTORE);
        v.put(Opcodes.FLOAD, Opcodes.FSTORE);
        v.put(Opcodes.DLOAD, Opcodes.DSTORE);
        v.put(Opcodes.ALOAD, Opcodes.ASTORE);
        v.put(Opcodes.IALOAD, Opcodes.IASTORE);
        v.put(Opcodes.LALOAD, Opcodes.LASTORE);
        v.put(Opcodes.FALOAD, Opcodes.FASTORE);
        v.put(Opcodes.DALOAD, Opcodes.DASTORE);
        v.put(Opcodes.AALOAD, Opcodes.AASTORE);
        v.put(Opcodes.BALOAD, Opcodes.BASTORE);
        v.put(Opcodes.CALOAD, Opcodes.CASTORE);
        v.put(Opcodes.SALOAD, Opcodes.SASTORE);
    }

    public void store(int opcode, Object v) {
        variableMap.put(opcode, v);
    }

    public Object load(int opcode) {
        return variableMap.get(v.get(opcode));
    }
}
