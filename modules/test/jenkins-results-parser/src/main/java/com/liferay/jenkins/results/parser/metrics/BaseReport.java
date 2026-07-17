/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser.metrics;

import com.liferay.jenkins.results.parser.JenkinsResultsParserUtil;

import java.io.File;
import java.io.IOException;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.FileUtils;

/**
 * @author Brittney Nguyen
 */
public abstract class BaseReport {

	public void addFile(String fileContent, String fileName) {
		_fileMap.put(new File(_outputDir, fileName), fileContent);
	}

	public void addFilesFromResource(
		String resourceDirPath, String... fileNames) {

		for (String fileName : fileNames) {
			try {
				addFile(
					JenkinsResultsParserUtil.getResourceFileContent(
						resourceDirPath + fileName),
					fileName);
			}
			catch (IOException ioException) {
				System.out.println(
					"Unable to get file content from resource: " +
						resourceDirPath + fileName);
			}
		}
	}

	public void write() throws IOException {
		FileUtils.deleteDirectory(_outputDir);

		for (Map.Entry<File, String> entry : _fileMap.entrySet()) {
			File file = entry.getKey();

			String filePath = file.getCanonicalPath();

			if (filePath.contains(".html")) {
				System.out.println("Report created at: file://" + filePath);
			}

			JenkinsResultsParserUtil.write(file, entry.getValue());
		}
	}

	protected BaseReport(File outputDir) {
		_outputDir = outputDir;
	}

	private final Map<File, String> _fileMap = new HashMap<>();
	private final File _outputDir;

}