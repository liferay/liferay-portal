/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.taglib.servlet;

import com.liferay.petra.string.StringPool;

import jakarta.servlet.jsp.JspWriter;

import java.io.IOException;
import java.io.Writer;

/**
 * @author Shuyang Zhou
 */
public class PipingJspWriter extends JspWriter {

	public PipingJspWriter(Writer writer) {
		super(NO_BUFFER, false);

		_writer = writer;
	}

	@Override
	public void clear() throws IOException {
		throw new IOException();
	}

	@Override
	public void clearBuffer() {
	}

	@Override
	public void close() throws IOException {
		_writer.close();
	}

	@Override
	public void flush() throws IOException {
		_writer.flush();
	}

	@Override
	public int getRemaining() {
		return 0;
	}

	@Override
	public void newLine() throws IOException {
		_writer.write(_LINE_SEPARATOR);
	}

	@Override
	public void print(boolean b) throws IOException {
		if (b) {
			_writer.write(StringPool.TRUE);
		}
		else {
			_writer.write(StringPool.FALSE);
		}
	}

	@Override
	public void print(char c) throws IOException {
		_writer.write(c);
	}

	@Override
	public void print(char[] chars) throws IOException {
		_writer.write(chars);
	}

	@Override
	public void print(double d) throws IOException {
		_writer.write(String.valueOf(d));
	}

	@Override
	public void print(float f) throws IOException {
		_writer.write(String.valueOf(f));
	}

	@Override
	public void print(int i) throws IOException {
		_writer.write(String.valueOf(i));
	}

	@Override
	public void print(long l) throws IOException {
		_writer.write(String.valueOf(l));
	}

	@Override
	public void print(Object object) throws IOException {
		_writer.write(String.valueOf(object));
	}

	@Override
	public void print(String string) throws IOException {
		if (string == null) {
			string = StringPool.NULL;
		}

		_writer.write(string);
	}

	@Override
	public void println() throws IOException {
		_writer.write(_LINE_SEPARATOR);
	}

	@Override
	public void println(boolean b) throws IOException {
		if (b) {
			_writer.write(StringPool.TRUE);
		}
		else {
			_writer.write(StringPool.FALSE);
		}

		_writer.write(_LINE_SEPARATOR);
	}

	@Override
	public void println(char c) throws IOException {
		_writer.write(c);
		_writer.write(_LINE_SEPARATOR);
	}

	@Override
	public void println(char[] chars) throws IOException {
		_writer.write(chars);
		_writer.write(_LINE_SEPARATOR);
	}

	@Override
	public void println(double d) throws IOException {
		_writer.write(String.valueOf(d));
		_writer.write(_LINE_SEPARATOR);
	}

	@Override
	public void println(float f) throws IOException {
		_writer.write(String.valueOf(f));
		_writer.write(_LINE_SEPARATOR);
	}

	@Override
	public void println(int i) throws IOException {
		_writer.write(String.valueOf(i));
		_writer.write(_LINE_SEPARATOR);
	}

	@Override
	public void println(long l) throws IOException {
		_writer.write(String.valueOf(l));
		_writer.write(_LINE_SEPARATOR);
	}

	@Override
	public void println(Object object) throws IOException {
		_writer.write(String.valueOf(object));
		_writer.write(_LINE_SEPARATOR);
	}

	@Override
	public void println(String string) throws IOException {
		if (string == null) {
			string = StringPool.NULL;
		}

		_writer.write(string);
		_writer.write(_LINE_SEPARATOR);
	}

	@Override
	public void write(char[] chars) throws IOException {
		_writer.write(chars);
	}

	@Override
	public void write(char[] chars, int offset, int length) throws IOException {
		_writer.write(chars, offset, length);
	}

	@Override
	public void write(int c) throws IOException {
		_writer.write(c);
	}

	@Override
	public void write(String string) throws IOException {
		_writer.write(string);
	}

	@Override
	public void write(String string, int offset, int length)
		throws IOException {

		_writer.write(string, offset, length);
	}

	private static final String _LINE_SEPARATOR = System.getProperty(
		"line.separator");

	private final Writer _writer;

}