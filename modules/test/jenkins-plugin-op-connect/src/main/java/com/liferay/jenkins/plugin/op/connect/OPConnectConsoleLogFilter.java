/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.plugin.op.connect;

import hudson.Extension;

import hudson.console.ConsoleLogFilter;
import hudson.console.LineTransformationOutputStream;

import hudson.model.AbstractBuild;
import hudson.model.Run;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;

import java.nio.charset.StandardCharsets;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Michael Hashimoto
 */
@Extension
public class OPConnectConsoleLogFilter
	extends ConsoleLogFilter implements Serializable {

	@Override
	public OutputStream decorateLogger(
		AbstractBuild abstractBuild, OutputStream outputStream) {

		return _decorateLogger(outputStream);
	}

	@Override
	public OutputStream decorateLogger(Run run, OutputStream outputStream) {
		return _decorateLogger(outputStream);
	}

	private OutputStream _decorateLogger(OutputStream outputStream) {
		Pattern pattern = _getSecretValuesPattern();

		if (pattern == null) {
			return outputStream;
		}

		return new MaskingOutputStream(outputStream, pattern);
	}

	private Pattern _getSecretValuesPattern() {
		List<String> ignoredValues = OPConnectUtil.getIgnoredValues();

		List<String> secretValues = new ArrayList<>();

		for (String secretValue : OPConnectUtil.getSecretValues()) {
			if (secretValue == null) {
				continue;
			}

			for (String line : secretValue.split("\\r?\\n")) {
				if (line == null) {
					continue;
				}

				line = line.trim();

				if (line.isEmpty() || ignoredValues.contains(line)) {
					continue;
				}

				secretValues.add(line);
			}
		}

		if (secretValues.isEmpty()) {
			return null;
		}

		Comparator<String> comparator = Comparator.comparingInt(String::length);

		secretValues.sort(comparator.reversed());

		StringBuilder sb = new StringBuilder();

		for (String secretValue : secretValues) {
			if (sb.length() > 0) {
				sb.append('|');
			}

			sb.append(Pattern.quote(secretValue));
		}

		return Pattern.compile(sb.toString());
	}

	private static final long serialVersionUID = 1L;

	private static class MaskingOutputStream
		extends LineTransformationOutputStream {

		@Override
		public void close() throws IOException {
			super.close();

			_outputStream.close();
		}

		@Override
		public void flush() throws IOException {
			super.flush();

			_outputStream.flush();
		}

		protected MaskingOutputStream(
			OutputStream outputStream, Pattern pattern) {

			_outputStream = outputStream;
			_pattern = pattern;
		}

		@Override
		protected void eol(byte[] bytes, int length) throws IOException {
			String line = new String(bytes, 0, length, StandardCharsets.UTF_8);

			Matcher matcher = _pattern.matcher(line);

			line = matcher.replaceAll("[REDACTED]");

			_outputStream.write(line.getBytes(StandardCharsets.UTF_8));
		}

		private final OutputStream _outputStream;
		private final Pattern _pattern;

	}

}