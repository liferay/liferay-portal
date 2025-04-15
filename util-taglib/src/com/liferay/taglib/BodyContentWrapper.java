/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.taglib;

import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.io.unsync.UnsyncStringReader;
import com.liferay.portal.kernel.io.unsync.UnsyncStringWriter;

import jakarta.servlet.jsp.JspWriter;
import jakarta.servlet.jsp.tagext.BodyContent;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;

/**
 * @author Shuyang Zhou
 */
public class BodyContentWrapper
	extends BodyContent
	implements com.liferay.portal.kernel.servlet.taglib.BodyContentWrapper {

	public BodyContentWrapper(
		BodyContent bodyContent, UnsyncStringWriter unsyncStringWriter) {

		super(bodyContent.getEnclosingWriter());

		_sb = unsyncStringWriter.getStringBundler();
	}

	@Override
	public Writer append(char c) throws IOException {
		write(c);

		return this;
	}

	@Override
	public Writer append(CharSequence charSequence) throws IOException {
		if (charSequence == null) {
			_sb.append(StringPool.NULL);
		}
		else {
			_sb.append(charSequence.toString());
		}

		return this;
	}

	@Override
	public Writer append(CharSequence charSequence, int start, int end)
		throws IOException {

		if (charSequence == null) {
			_sb.append(StringPool.NULL);
		}
		else {
			charSequence = charSequence.subSequence(start, end);

			_sb.append(charSequence.toString());
		}

		return this;
	}

	@Override
	public void clear() throws IOException {
		_sb.setIndex(0);
	}

	@Override
	public void clearBody() {
		_sb.setIndex(0);
	}

	@Override
	public void clearBuffer() {
		_sb.setIndex(0);
	}

	@Override
	public void close() throws IOException {
		_sb.setIndex(0);
	}

	@Override
	public void flush() throws IOException {
		throw new IOException("Illegal to flush within a custom tag");
	}

	@Override
	public int getBufferSize() {
		return 0;
	}

	@Override
	public JspWriter getEnclosingWriter() {
		return super.getEnclosingWriter();
	}

	@Override
	public Reader getReader() {
		return new UnsyncStringReader(_sb.toString());
	}

	@Override
	public int getRemaining() {
		return 0;
	}

	@Override
	public String getString() {
		return _sb.toString();
	}

	@Override
	public StringBundler getStringBundler() {
		return _sb;
	}

	@Override
	public boolean isAutoFlush() {
		return false;
	}

	@Override
	public void newLine() throws IOException {
		_sb.append(_LINE_SEPARATOR);
	}

	@Override
	public void print(boolean b) throws IOException {
		_sb.append(String.valueOf(b));
	}

	@Override
	public void print(char c) throws IOException {
		_sb.append(String.valueOf(c));
	}

	@Override
	public void print(char[] chars) throws IOException {
		_sb.append(new String(chars));
	}

	@Override
	public void print(double d) throws IOException {
		_sb.append(String.valueOf(d));
	}

	@Override
	public void print(float f) throws IOException {
		_sb.append(String.valueOf(f));
	}

	@Override
	public void print(int i) throws IOException {
		_sb.append(String.valueOf(i));
	}

	@Override
	public void print(long l) throws IOException {
		_sb.append(String.valueOf(l));
	}

	@Override
	public void print(Object object) throws IOException {
		_sb.append(String.valueOf(object));
	}

	@Override
	public void print(String string) throws IOException {
		if (string == null) {
			string = StringPool.NULL;
		}

		_sb.append(string);
	}

	@Override
	public void println() throws IOException {
		newLine();
	}

	@Override
	public void println(boolean b) throws IOException {
		print(b);
		newLine();
	}

	@Override
	public void println(char c) throws IOException {
		print(c);
		newLine();
	}

	@Override
	public void println(char[] chars) throws IOException {
		write(chars);
		newLine();
	}

	@Override
	public void println(double d) throws IOException {
		print(d);
		newLine();
	}

	@Override
	public void println(float f) throws IOException {
		print(f);
		newLine();
	}

	@Override
	public void println(int i) throws IOException {
		print(i);
		newLine();
	}

	@Override
	public void println(long l) throws IOException {
		print(l);
		newLine();
	}

	@Override
	public void println(Object object) throws IOException {
		print(object);
		newLine();
	}

	@Override
	public void println(String string) throws IOException {
		print(string);
		newLine();
	}

	@Override
	public void write(char[] chars) throws IOException {
		_sb.append(new String(chars));
	}

	@Override
	public void write(char[] chars, int offset, int length) throws IOException {
		_sb.append(new String(chars, offset, length));
	}

	@Override
	public void write(int c) throws IOException {
		if (c <= 127) {
			_sb.append(StringPool.ASCII_TABLE[c]);
		}
		else {
			_sb.append(String.valueOf(c));
		}
	}

	@Override
	public void write(String string) throws IOException {
		_sb.append(string);
	}

	@Override
	public void write(String string, int offset, int length)
		throws IOException {

		_sb.append(string.substring(offset, offset + length));
	}

	@Override
	public void writeOut(Writer writer) throws IOException {
		_sb.writeTo(writer);
	}

	private static final String _LINE_SEPARATOR = System.getProperty(
		"line.separator");

	private final StringBundler _sb;

}