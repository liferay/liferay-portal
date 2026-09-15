/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser.test.clazz.file;

import com.liferay.jenkins.results.parser.JenkinsResultsParserUtil;

import java.io.File;
import java.io.IOException;

/**
 * @author Michael Hashimoto
 */
public abstract class BaseTestClassFile implements TestClassFile {

	@Override
	public File getFile() {
		return _file;
	}

	@Override
	public String getName() {
		return _file.getName();
	}

	@Override
	public String toString() {
		return JenkinsResultsParserUtil.getCanonicalPath(_file);
	}

	protected BaseTestClassFile(File file) throws IOException {
		_file = file;
	}

	protected String getContent() throws IOException {
		if (_content == null) {
			_content = JenkinsResultsParserUtil.read(_file);
		}

		return _content;
	}

	private String _content;
	private final File _file;

}