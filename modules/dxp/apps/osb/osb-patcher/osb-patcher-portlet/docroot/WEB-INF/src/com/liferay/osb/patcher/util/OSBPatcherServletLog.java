/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */

package com.liferay.osb.patcher.util;

/**
 * @author Zsolt Balogh
 */
public class OSBPatcherServletLog {

	public String getCommandLine() {
		return commandLine;
	}

	public int getExitValue() {
		return exitValue;
	}

	public String getStdErr() {
		return stdErr;
	}

	public String getStdOut() {
		return stdOut;
	}

	public void setCommandLine(String commandLine) {
		this.commandLine = commandLine;
	}

	public void setExitValue(int exitValue) {
		this.exitValue = exitValue;
	}

	public void setStdErr(String stdError) {
		this.stdErr = stdError;
	}

	public void setStdOut(String stdOut) {
		this.stdOut = stdOut;
	}

	protected String commandLine;
	protected int exitValue;
	protected String stdErr;
	protected String stdOut;

}