package hide212131.corelj.json;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import org.objectweb.asm.Type;

import java.util.ArrayList;
import java.util.List;

@JsonAutoDetect
public class ClassEntry {
    public String name;
    public String simpleName;
    public List<MethodEntry> methodEntryList = new ArrayList<>();

    public ClassEntry(String name) {
        this.name = Type.getObjectType(name).getClassName();
        this.simpleName = this.name.substring(this.name.lastIndexOf('.') + 1);
    }

    public void add(MethodEntry methodEntry) {
        methodEntryList.add(methodEntry);
    }
}
