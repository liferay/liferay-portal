/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser;

import java.io.IOException;
import java.io.PrintStream;

import java.util.Arrays;

/**
 * @author Peter Yoo
 */
public class SecurePrintStream extends PrintStream {

	public SecurePrintStream(PrintStream printStream) {
		super(printStream, true);

		_printStream = printStream;
	}

	@Override
	public PrintStream append(char c) {
		_printStream.append(c);

		return this;
	}

	@Override
	public PrintStream append(CharSequence charSequence) {
		String redactedString = _redact(charSequence.toString());

		if (redactedString != null) {
			_printStream.append(redactedString);
		}
		else {
			_printStream.append(charSequence);
		}

		return this;
	}

	@Override
	public PrintStream append(CharSequence charSequence, int start, int end) {
		return append(charSequence.subSequence(start, end));
	}

	@Override
	public void flush() {
		_printStream.flush();
	}

	@Override
	public void print(boolean b) {
		_printStream.print(b);
	}

	@Override
	public void print(char c) {
		_printStream.print(c);
	}

	@Override
	public void print(char[] chars) {
		String redactedString = _redact(new String(chars));

		if (redactedString != null) {
			_print(redactedString, false);

			return;
		}

		_printStream.print(chars);
	}

	@Override
	public void print(double d) {
		String redactedString = _redact(String.valueOf(d));

		if (redactedString != null) {
			_printStream.print(redactedString);

			return;
		}

		_printStream.print(d);
	}

	@Override
	public void print(float f) {
		String redactedString = _redact(String.valueOf(f));

		if (redactedString != null) {
			_printStream.print(redactedString);

			return;
		}

		_printStream.print(f);
	}

	@Override
	public void print(int i) {
		String redactedString = _redact(String.valueOf(i));

		if (redactedString != null) {
			_printStream.print(redactedString);

			return;
		}

		_printStream.print(i);
	}

	@Override
	public void print(long l) {
		String redactedString = _redact(String.valueOf(l));

		if (redactedString != null) {
			_printStream.println(redactedString);

			return;
		}

		_printStream.print(l);
	}

	@Override
	public void print(Object object) {
		if (object == null) {
			_printStream.print("null");
		}

		String redactedString = _redact(object.toString());

		if (redactedString != null) {
			_print(redactedString, false);

			return;
		}

		_printStream.print(object);
	}

	@Override
	public void print(String string) {
		String redactedString = _redact(string);

		if (redactedString != null) {
			_print(redactedString, false);

			return;
		}

		_print(string, false);
	}

	@Override
	public void println() {
		_printStream.println();
	}

	@Override
	public void println(boolean b) {
		_printStream.println(b);
	}

	@Override
	public void println(char c) {
		_printStream.println(c);
	}

	@Override
	public void println(char[] chars) {
		String redactedString = _redact(new String(chars));

		if (redactedString != null) {
			_print(redactedString, true);

			return;
		}

		_printStream.println(chars);
	}

	@Override
	public void println(double d) {
		String redactedString = _redact(String.valueOf(d));

		if (redactedString != null) {
			_printStream.println(redactedString);

			return;
		}

		_printStream.println(d);
	}

	@Override
	public void println(float f) {
		String redactedString = _redact(String.valueOf(f));

		if (redactedString != null) {
			_printStream.println(redactedString);

			return;
		}

		_printStream.println(f);
	}

	@Override
	public void println(int i) {
		String redactedString = _redact(String.valueOf(i));

		if (redactedString != null) {
			_printStream.println(redactedString);

			return;
		}

		_printStream.println(i);
	}

	@Override
	public void println(long l) {
		String redactedString = _redact(String.valueOf(l));

		if (redactedString != null) {
			_printStream.println(redactedString);

			return;
		}

		_printStream.println(l);
	}

	@Override
	public void println(Object object) {
		if (object == null) {
			_printStream.println("null");

			return;
		}

		String redactedString = _redact(object.toString());

		if (redactedString != null) {
			println(redactedString);

			return;
		}

		_printStream.println(object);
	}

	@Override
	public void println(String string) {
		String redactedString = _redact(string);

		if (redactedString != null) {
			_print(redactedString, true);
		}
		else {
			_print(string, true);
		}
	}

	@Override
	public void write(byte[] bytes) throws IOException {
		String redactedString = _redact(new String(bytes));

		if (redactedString != null) {
			_print(redactedString, false);

			return;
		}

		_printStream.write(bytes);
	}

	@Override
	public void write(byte[] buffer, int offset, int length) {
		String redactedString = _redact(
			new String(Arrays.copyOfRange(buffer, offset, offset + length)));

		if (redactedString != null) {
			_print(redactedString, false);

			return;
		}

		_printStream.write(buffer, offset, length);
	}

	@Override
	public void write(int b) {
		String redactedString = _redact(String.valueOf(b));

		if (redactedString != null) {
			_print(redactedString, false);

			return;
		}

		_printStream.write(b);
	}

	private void _print(String string, boolean appendNewLine) {
		if ((string == null) && appendNewLine) {
			_printStream.println();

			return;
		}

		String[] lines = string.split("\n");

		if (lines.length == 0) {
			if (!string.isEmpty()) {
				_printStream.print(string);
			}

			if (appendNewLine) {
				_printStream.println();
			}

			return;
		}

		String lastLine = lines[lines.length - 1];

		for (String line : lines) {
			if (line.length() > _MAX_PRINT_LINE_LENGTH) {
				_printStream.print(line.substring(0, _MAX_PRINT_LINE_LENGTH));

				_printStream.print(
					JenkinsResultsParserUtil.combine(
						"[TRUNCATED ",
						String.valueOf(line.length() - _MAX_PRINT_LINE_LENGTH),
						" CHARACTERS]"));

				_printStream.flush();
			}
			else {
				_printStream.print(line);
			}

			if ((line == lastLine) && ((line != lastLine) || !appendNewLine)) {
				continue;
			}

			_printStream.println();
		}
	}

	private String _redact(String string) {
		if (string == null) {
			return null;
		}

		String redactedString = JenkinsResultsParserUtil.redact(string);

		if (string.equals(redactedString)) {
			return null;
		}

		return redactedString;
	}

	private static final int _MAX_PRINT_LINE_LENGTH = 25000;

	private final PrintStream _printStream;

}