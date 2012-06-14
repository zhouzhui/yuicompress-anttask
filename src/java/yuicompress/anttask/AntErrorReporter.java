package yuicompress.anttask;

import org.apache.tools.ant.Task;
import org.apache.tools.ant.types.LogLevel;
import org.mozilla.javascript.ErrorReporter;
import org.mozilla.javascript.EvaluatorException;

public class AntErrorReporter implements ErrorReporter {

    private Task task = null;

    public AntErrorReporter(Task task) {
        if (null == task) {
            throw new IllegalArgumentException("task could not be null");
        }
        this.task = task;
    }

    @Override
    public void warning(String message, String sourceName, int line,
            String lineSource, int lineOffset) {
        if (line < 0) {
            this.task.log(message, LogLevel.WARN.getLevel());
        } else {
            this.task.log("line: " + line + ", offset: " + lineOffset + ", "
                    + message, LogLevel.WARN.getLevel());
        }
    }

    public void error(String message, String sourceName, int line,
            String lineSource, int lineOffset) {
        if (line < 0) {
            this.task.log(message, LogLevel.ERR.getLevel());
        } else {
            this.task.log("line: " + line + ", offset: " + lineOffset + ", "
                    + message, LogLevel.ERR.getLevel());
        }
    }

    public EvaluatorException runtimeError(String message, String sourceName,
            int line, String lineSource, int lineOffset) {
        error(message, sourceName, line, lineSource, lineOffset);
        return new EvaluatorException(message);
    }

}
