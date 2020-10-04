package hide212131.corelj;

import org.junit.jupiter.api.Test;

class MainTest {
    @Test
    void testMain() throws Exception {
        Main.main(new String[]{ "-include", "hide212131",
                //new File(Main.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getPath()
                //"build/libs/corelj-1.0-SNAPSHOT-all.jar"
                "build/libs/corelj-1.0-SNAPSHOT-all.jar"
                //"/usr/local/Cellar/gradle/6.5/libexec/lib/minlog-1.2.jar"
        });
    }
}