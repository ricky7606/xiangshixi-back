package thu.declan.xi.server.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author declan
 */
public class LinuxCmdUtils {
    
    
    public static class LinuxCmdResult {

        private List<String> output = new ArrayList<>();
        private List<String> error = new ArrayList<>();
        private int exitValue;

        public List<String> getOutput() {
            return output;
        }

        public void setOutput(List<String> output) {
            this.output = output;
        }

        public void addOutput(String output) {
            this.output.add(output);
        }

        public List<String> getError() {
            return error;
        }

        public void setError(List<String> error) {
            this.error = error;
        }

        public void addError(String error) {
            this.error.add(error);
        }

        public int getExitValue() {
            return exitValue;
        }

        public void setExitValue(int exitValue) {
            this.exitValue = exitValue;
        }

    }

    public static LinuxCmdResult processLinuxCmd(String input, PrintWriter redirectOut, String ... cmd) {
        String s;
        Process p;
        LinuxCmdResult result = new LinuxCmdResult();
        try {
            p = Runtime.getRuntime().exec(cmd);
            BufferedReader stderr;
            try (BufferedReader stdout = new BufferedReader(new InputStreamReader(p.getInputStream()))) {
                stderr = new BufferedReader(new InputStreamReader(p.getErrorStream()));
				if (null != input) {
                    try (BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(p.getOutputStream()))) {
						bw.write(input);
                        bw.flush();
                    }
                }
                while ((s = stdout.readLine()) != null) {
					if (redirectOut != null) {
						redirectOut.println(s);
					} else {
						result.addOutput(s);
					}
                }
                while ((s = stderr.readLine()) != null) {
                    result.addError(s);
                }
            }
            stderr.close();
            p.waitFor();
            result.setExitValue(p.exitValue());
            p.destroy();
        } catch (IOException | InterruptedException e) {
        }
        return result;
    }
	
	public static LinuxCmdResult processLinuxCmd(PrintWriter redirectOut, String ... cmd) {
		return processLinuxCmd((String)null, redirectOut, cmd);
	}
	
	public static LinuxCmdResult processLinuxCmd(String ... cmd) {
		return processLinuxCmd((String)null, (PrintWriter)null, cmd);
	}

}
