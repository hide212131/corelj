package hide212131.corelj.json;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import org.objectweb.asm.Type;

@JsonAutoDetect
public class MethodInsnEntry {
    public int index;
    public String owner;
    public String name;
    public String desc;
    public String signature;

    public MethodInsnEntry(String owner, String name, String desc) {
        this.owner = Type.getObjectType(owner).getClassName();
        this.name = name;
        this.desc = desc;
        this.signature = this.owner + "#" + name;
    }
}
