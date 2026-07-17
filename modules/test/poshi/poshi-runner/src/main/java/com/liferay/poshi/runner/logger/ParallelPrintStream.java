/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.poshi.runner.logger;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;

import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * @author Kenji Heigel
 */
public class ParallelPrintStream extends PrintStream {

	public static File getLogFile() {
		Thread currentThread = Thread.currentThread();

		return _logFiles.get(currentThread.getName());
	}

	public static PrintStream getPrintStream(String name) {
		return _printStreams.get(name);
	}

	public static void resetPrintStream() {
		Thread thread = Thread.currentThread();

		PrintStream printStream = _printStreams.get(thread.getName());

		printStream.close();

		_printStreams.remove(thread.getName());

		File logFile = _logFiles.get(thread.getName());

		logFile.delete();

		_logFiles.remove(thread.getName());

		try {
			_addPrintStream(thread.getName());
		}
		catch (IOException ioException) {
			throw new RuntimeException(
				"Unable to reset log file: " + logFile, ioException);
		}
	}

	public ParallelPrintStream(PrintStream printStream) {
		super(printStream);

		_originalPrintStream = printStream;
	}

	@Override
	public PrintStream append(char c) {
		PrintStream printStream = _getPrintStream();

		return printStream.append(c);
	}

	@Override
	public PrintStream append(CharSequence charSequence) {
		PrintStream printStream = _getPrintStream();

		return printStream.append(charSequence);
	}

	@Override
	public PrintStream append(CharSequence charSequence, int start, int end) {
		PrintStream printStream = _getPrintStream();

		return printStream.append(charSequence, start, end);
	}

	@Override
	public boolean checkError() {
		PrintStream printStream = _getPrintStream();

		return printStream.checkError();
	}

	@Override
	public void close() {
		PrintStream printStream = _getPrintStream();

		printStream.close();
	}

	@Override
	public void flush() {
		PrintStream printStream = _getPrintStream();

		printStream.flush();
	}

	@Override
	public PrintStream format(Locale locale, String format, Object... args) {
		PrintStream printStream = _getPrintStream();

		return printStream.format(locale, format, args);
	}

	@Override
	public PrintStream format(String format, Object... args) {
		PrintStream printStream = _getPrintStream();

		return printStream.format(format, args);
	}

	public PrintStream getOriginalPrintStream() {
		return _originalPrintStream;
	}

	@Override
	public void print(boolean b) {
		PrintStream printStream = _getPrintStream();

		printStream.print(b);
	}

	@Override
	public void print(char c) {
		PrintStream printStream = _getPrintStream();

		printStream.print(c);
	}

	@Override
	public void print(char[] chars) {
		PrintStream printStream = _getPrintStream();

		printStream.print(chars);
	}

	@Override
	public void print(double d) {
		PrintStream printStream = _getPrintStream();

		printStream.print(d);
	}

	@Override
	public void print(float f) {
		PrintStream printStream = _getPrintStream();

		printStream.print(f);
	}

	@Override
	public void print(int i) {
		PrintStream printStream = _getPrintStream();

		printStream.print(i);
	}

	@Override
	public void print(long l) {
		PrintStream printStream = _getPrintStream();

		printStream.print(l);
	}

	@Override
	public void print(Object object) {
		PrintStream printStream = _getPrintStream();

		printStream.print(object);
	}

	@Override
	public void print(String s) {
		PrintStream printStream = _getPrintStream();

		printStream.print(s);
	}

	@Override
	public PrintStream printf(Locale locale, String format, Object... args) {
		PrintStream printStream = _getPrintStream();

		return printStream.printf(locale, format, args);
	}

	@Override
	public PrintStream printf(String format, Object... args) {
		PrintStream printStream = _getPrintStream();

		return printStream.printf(format, args);
	}

	@Override
	public void println() {
		PrintStream printStream = _getPrintStream();

		printStream.println();
	}

	@Override
	public void println(boolean b) {
		PrintStream printStream = _getPrintStream();

		printStream.println(b);
	}

	@Override
	public void println(char c) {
		PrintStream printStream = _getPrintStream();

		printStream.println(c);
	}

	@Override
	public void println(char[] chars) {
		PrintStream printStream = _getPrintStream();

		printStream.println(chars);
	}

	@Override
	public void println(double d) {
		PrintStream printStream = _getPrintStream();

		printStream.println(d);
	}

	@Override
	public void println(float f) {
		PrintStream printStream = _getPrintStream();

		printStream.println(f);
	}

	@Override
	public void println(int i) {
		PrintStream printStream = _getPrintStream();

		printStream.println(i);
	}

	@Override
	public void println(long l) {
		PrintStream printStream = _getPrintStream();

		printStream.println(l);
	}

	@Override
	public void println(Object object) {
		PrintStream printStream = _getPrintStream();

		printStream.println(object);
	}

	@Override
	public void println(String s) {
		PrintStream printStream = _getPrintStream();

		printStream.println(s);
	}

	@Override
	public void write(byte[] bytes) throws IOException {
		PrintStream printStream = _getPrintStream();

		printStream.write(bytes);
	}

	@Override
	public void write(byte[] buffer, int offset, int length) {
		PrintStream printStream = _getPrintStream();

		printStream.write(buffer, offset, length);
	}

	@Override
	public void write(int i) {
		PrintStream printStream = _getPrintStream();

		printStream.write(i);
	}

	private static PrintStream _addPrintStream(String threadName)
		throws IOException {

		File file = new File("test-results/" + threadName + ".log");

		_logFiles.put(threadName, file.getCanonicalFile());

		PrintStream printStream = new PrintStream(file.getCanonicalPath());

		_printStreams.put(threadName, printStream);

		return printStream;
	}

	private PrintStream _getPrintStream() {
		Thread currentThread = Thread.currentThread();

		String currentThreadName = currentThread.getName();

		PrintStream printStream = _printStreams.get(currentThreadName);

		if (printStream != null) {
			return printStream;
		}

		try {
			if (currentThreadName.equalsIgnoreCase("main") ||
				currentThreadName.equalsIgnoreCase("Test worker")) {

				return _originalPrintStream;
			}

			return _addPrintStream(currentThreadName);
		}
		catch (IOException ioException) {
			return _originalPrintStream;
		}
	}

	private static final Map<String, File> _logFiles =
		Collections.synchronizedMap(new HashMap<>());
	private static final Map<String, PrintStream> _printStreams =
		Collections.synchronizedMap(new HashMap<>());

	private final PrintStream _originalPrintStream;

}