/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.source.formatter.check;

import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.tools.GitUtil;
import com.liferay.source.formatter.SourceFormatterArgs;
import com.liferay.source.formatter.check.util.SourceUtil;
import com.liferay.source.formatter.processor.SourceProcessor;

import java.io.IOException;

import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Alan Huang
 */
public abstract class BaseBreakingChangesCheck extends BaseFileCheck {

	protected void checkBreakingChanges(
			String fileName, String absolutePath, String[] breakingChanges,
			String message, boolean bndFile)
		throws IOException {

		for (String breakingChange : breakingChanges) {
			int alternativesCount = StringUtil.count(
				breakingChange, "## Alternatives");
			int breakingCount = StringUtil.count(
				breakingChange, "# breaking\n");
			int whatCount = StringUtil.count(breakingChange, "## What");
			int whyCount = StringUtil.count(breakingChange, "## Why");

			if ((alternativesCount > 1) || (breakingCount != 1) ||
				(whatCount != 1) || (whyCount != 1)) {

				addMessage(
					fileName,
					StringBundler.concat(
						message,
						"Each breaking change should have one, and only one ",
						"\"# breaking\", \"## What\", \"## Why\" and ## ",
						"(Optional). Use \"----\" to split each breaking ",
						"change."));

				return;
			}

			int alternativesPosition = breakingChange.indexOf(
				"## Alternatives");
			int whatPosition = breakingChange.indexOf("## What");
			int whyPosition = breakingChange.indexOf("## Why");

			if ((whatPosition > whyPosition) ||
				((alternativesPosition != -1) &&
				 (whyPosition > alternativesPosition))) {

				addMessage(
					fileName,
					message +
						"The correct order of headers should be \"## What\" " +
							"| \"## Why\" | \"## Alternatives\"");

				return;
			}

			_checkMissingExplanation(
				fileName, breakingChange, message, alternativesPosition,
				whatPosition, whyPosition);

			int lineNumber = SourceUtil.getLineNumber(
				breakingChange, whatPosition);

			String trimmedLine = StringUtil.trimLeading(
				SourceUtil.getLine(breakingChange, lineNumber));

			if (trimmedLine.length() == 7) {
				addMessage(
					fileName,
					message +
						"There should be one file path after \"## What\"");

				return;
			}

			if (!bndFile) {
				continue;
			}

			String filePath = StringUtil.trim(trimmedLine.substring(7));

			if (getPortalContent(filePath, absolutePath, true) == null) {
				addMessage(
					fileName,
					StringBundler.concat(
						message, StringUtil.quote(filePath),
						" points to nonexistent file. \"## What\" should be ",
						"followed by only one path, which is from ",
						_LIFERAY_PORTAL_MASTER_URL, "."));

				return;
			}
		}
	}

	protected void checkMajorVersionBump(
			String fileName, String absolutePath, String content,
			String additionalMessage)
		throws Exception {

		String oldVersion = _getMajorVersion(
			getPortalContent(fileName, absolutePath, true));

		if (Validator.isBlank(oldVersion)) {
			return;
		}

		String version = _getMajorVersion(content);

		if (Validator.isBlank(version)) {
			return;
		}

		if (Integer.valueOf(version) > Integer.valueOf(oldVersion)) {
			_checkCommitMessages(fileName, absolutePath, additionalMessage);
		}
	}

	protected void checkMissingEmptyLinesAroundHeaders(
		String fileName, String breakingChanges, String message) {

		if (!breakingChanges.endsWith("\n\n----")) {
			addMessage(
				fileName,
				message + "The commit message contains \"# breaking\" should " +
					"end with \"\\n\\n----\"");
		}

		for (String header : _BREAKING_CHANGE_HEADER_NAMES) {
			int x = breakingChanges.indexOf(header);

			if (x == -1) {
				continue;
			}

			if (header.equals("## Alternatives") || header.equals("## Why")) {
				char c = breakingChanges.charAt(x + header.length());

				if (c != CharPool.NEW_LINE) {
					addMessage(
						fileName,
						StringBundler.concat(
							message, "There should be a line break after \" ",
							header, "\""));
				}
			}

			int lineNumber = SourceUtil.getLineNumber(breakingChanges, x);

			String nextLine = SourceUtil.getLine(
				breakingChanges, lineNumber + 1);
			String previousLine = SourceUtil.getLine(
				breakingChanges, lineNumber - 1);

			if (Validator.isNotNull(nextLine) ||
				Validator.isNotNull(previousLine)) {

				addMessage(
					fileName,
					StringBundler.concat(
						message,
						"There should be an empty line after/before \"----\", ",
						"\"# breaking\", \"## What\", \"## Why\" and \"## ",
						"Alternatives\""));
			}
		}
	}

	protected synchronized List<String> getCurrentBranchCommitMessages()
		throws Exception {

		if (_currentBranchCommitMessages != null) {
			return _currentBranchCommitMessages;
		}

		SourceProcessor sourceProcessor = getSourceProcessor();

		SourceFormatterArgs sourceFormatterArgs =
			sourceProcessor.getSourceFormatterArgs();

		_currentBranchCommitMessages = GitUtil.getCurrentBranchCommitMessages(
			sourceFormatterArgs.getBaseDirName(),
			sourceFormatterArgs.getGitWorkingBranchName());

		return _currentBranchCommitMessages;
	}

	protected Pattern getVersionPattern() {
		return null;
	}

	private void _checkCommitMessages(
			String fileName, String absolutePath, String additionalMessage)
		throws Exception {

		List<String> commitMessages = getCurrentBranchCommitMessages();

		Iterator<String> iterator = commitMessages.iterator();

		while (iterator.hasNext()) {
			String commitMessage = iterator.next();

			String[] parts = commitMessage.split(":", 2);

			if (!parts[1].contains("# breaking")) {
				iterator.remove();
			}
		}

		if (commitMessages.isEmpty()) {
			addMessage(
				fileName,
				"Incorrect commit message: Missing breaking change in commit " +
					"messages when " + additionalMessage);

			return;
		}

		for (String commitMessage : commitMessages) {
			String[] parts = commitMessage.split(":", 2);

			if (!parts[1].contains("# breaking")) {
				continue;
			}

			String message =
				"Incorrect commit message in SHA " + parts[0] + ": ";

			checkMissingEmptyLinesAroundHeaders(fileName, parts[1], message);

			checkBreakingChanges(
				fileName, absolutePath, parts[1].split("\n----"), message,
				true);
		}
	}

	private void _checkMissingExplanation(
		String fileName, String breakingChange, String message,
		int... headerPositions) {

		for (int headerPosition : headerPositions) {
			if (headerPosition == -1) {
				continue;
			}

			int lineNumber = SourceUtil.getLineNumber(
				breakingChange, headerPosition);

			String explanationLine = SourceUtil.getLine(
				breakingChange, lineNumber + 2);

			if (Validator.isNull(explanationLine) ||
				ArrayUtil.contains(
					_BREAKING_CHANGE_HEADER_NAMES, explanationLine)) {

				addMessage(
					fileName,
					StringBundler.concat(
						message,
						"There should be at least a line containing an ",
						"explanation after \"## What\", \"## Why\" and \"## ",
						"Alternatives\""));
			}
		}
	}

	private String _getMajorVersion(String content) {
		if (Validator.isBlank(content)) {
			return null;
		}

		Pattern versionPattern = getVersionPattern();

		Matcher matcher = versionPattern.matcher(content);

		if (!matcher.find()) {
			return null;
		}

		String version = matcher.group(1);

		int index = version.indexOf(".");

		if (index == -1) {
			return version;
		}

		return version.substring(0, index);
	}

	private static final String[] _BREAKING_CHANGE_HEADER_NAMES = {
		"----", "## Alternatives", "# breaking", "## What", "## Why"
	};

	private static final String _LIFERAY_PORTAL_MASTER_URL =
		"https://github.com/liferay/liferay-portal/blob/master/";

	private static List<String> _currentBranchCommitMessages;

}