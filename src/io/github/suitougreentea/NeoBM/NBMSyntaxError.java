package io.github.suitougreentea.NeoBM;

public class NBMSyntaxError extends Throwable {
    public NBMSyntaxError(String string, int lineNumber) {
        super(String.format("SyntaxError: %s at line %d", string, lineNumber));
    }

    private static final long serialVersionUID = 324958723890457L;

}
