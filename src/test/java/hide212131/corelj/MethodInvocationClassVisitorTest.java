package hide212131.corelj;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import hide212131.corelj.invokeTest.Call;
import hide212131.corelj.visitor.MyClassVisitor;
import hide212131.corelj.visitor.SignatureFilter;
import org.junit.jupiter.api.Test;
import org.objectweb.asm.ClassReader;

import java.io.InputStream;

class MethodInvocationClassVisitorTest {
    @Test
    void testMain() throws Exception {
        InputStream is = Call.class.getResourceAsStream("/hide212131/corelj/invokeTest/Call.class");
        ClassReader reader = new ClassReader(is);
        MyClassVisitor cv = new MyClassVisitor(new SignatureFilter(), new SignatureFilter());
        reader.accept(cv, 0);

        var objectMapper = new ObjectMapper();
        objectMapper.configure(SerializationFeature.INDENT_OUTPUT, true);
        String classJson = objectMapper.writeValueAsString(cv.getClassEntry());
        System.out.println(classJson);
    }
}


