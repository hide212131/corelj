package hide212131.corelj.visitor.invocation;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MethodInvocationClassVisitorTest {
    @Test
    void testMain() throws Exception {

    }

    static class Super {
        public void foo() {
        }
    }

    static class Sub extends Super {
    }

    static class Ope {
        public void exec() {
            Super su = new Sub();
            su.foo();
        }
    }

}


