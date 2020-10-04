package hide212131.corelj.json;

import com.fasterxml.jackson.annotation.JsonAutoDetect;

import java.util.ArrayList;
import java.util.List;

@JsonAutoDetect
public class MethodEntry {
    public String name;
    public String desc;
    public String signature;
    public String simpleName;
    public String className;
    public List<MethodInsnEntry> methodInsnList = new ArrayList<>();


    public MethodEntry(ClassEntry classEntry, String name, String desc) {
        this.name = name;
        this.desc = desc;
        this.signature = classEntry.name + "#" + name;
        this.simpleName = this.signature.substring(this.signature.lastIndexOf(".") + 1);
        this.className = classEntry.name;
    }

    public void add(MethodInsnEntry methodInsnEntry) {
        methodInsnList.add(methodInsnEntry);
        methodInsnEntry.index = methodInsnList.size();
    }
}
