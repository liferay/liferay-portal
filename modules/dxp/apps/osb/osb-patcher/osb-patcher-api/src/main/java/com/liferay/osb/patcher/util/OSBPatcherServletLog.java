/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
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