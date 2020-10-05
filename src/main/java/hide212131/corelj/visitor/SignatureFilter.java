package hide212131.corelj.visitor;

public class SignatureFilter {

    SignatureFilter next = null;

    public boolean match(String signature) {
        SignatureFilter filter = this.next;
        while (filter != null) {
            if (!filter.match(signature)) {
                return false;
            }
        }
        return true;
    }

    public SignatureFilter include(String pattern) {
        if (pattern == null) {
            return this;
        } else {
            next = new SignatureFilter() {
                @Override
                public boolean match(String signature) {
                    return signature.matches("^.*" + pattern + ".*$");
                }
            };
            return next;
        }
    }

    public SignatureFilter exclude(String pattern) {
        if (pattern == null) {
            return this;
        } else {
            next = new SignatureFilter() {
                @Override
                public boolean match(String signature) {
                    return !signature.matches("^.*" + pattern + ".*$");
                }
            };
            return next;
        }
    }

}
