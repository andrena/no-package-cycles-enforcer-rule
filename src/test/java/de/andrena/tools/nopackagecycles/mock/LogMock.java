package de.andrena.tools.nopackagecycles.mock;

import java.util.ArrayList;
import java.util.List;

import org.apache.maven.plugin.logging.Log;

public class LogMock implements Log {
	private final List<String> info = new ArrayList<String>();

	public List<String> getInfo() {
		return info;
	}

	public void debug(CharSequence arg0) {
	}

	public void debug(Throwable arg0) {
	}

	public void debug(CharSequence arg0, Throwable arg1) {
	}

	public void error(CharSequence arg0) {
	}

	public void error(Throwable arg0) {
	}

	public void error(CharSequence arg0, Throwable arg1) {
	}

	public void info(CharSequence arg0) {
		info.add(arg0.toString());
	}

	public void info(Throwable arg0) {
	}

	public void info(CharSequence arg0, Throwable arg1) {
	}

	public boolean isDebugEnabled() {
		return false;
	}

	public boolean isErrorEnabled() {
		return false;
	}

	public boolean isInfoEnabled() {
		return false;
	}

	public boolean isWarnEnabled() {
		return false;
	}

	public void warn(CharSequence arg0) {
	}

	public void warn(Throwable arg0) {
	}

	public void warn(CharSequence arg0, Throwable arg1) {
	}

}
