/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.source.formatter.check;

import com.liferay.petra.io.unsync.UnsyncBufferedReader;
import com.liferay.petra.io.unsync.UnsyncStringReader;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;

import java.io.IOException;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Alan Huang
 */
public class MarkdownBreakingChangesAmendmentsFileCheck
	extends BaseBreakingChangesCheck {

	@Override
	protected String doProcess(
			String fileName, String absolutePath, String content)
		throws IOException {

		if (!absolutePath.endsWith("readme/BREAKING_CHANGES_AMENDMENTS.md")) {
			return content;
		}

		_checkBreakingChangesAmendments(
			fileName, absolutePath, _splitBreakingChangesAmendments(content));

		return content;
	}

	private void _checkBreakingChangesAmendments(
			String fileName, String absolutePath,
			List<String> breakingChangesAmendments)
		throws IOException {

		for (String breakingChangesAmendment : breakingChangesAmendments) {
			breakingChangesAmendment = StringUtil.trimLeading(
				breakingChangesAmendment);

			int x = breakingChangesAmendment.indexOf("\n");

			if (x == -1) {
				return;
			}

			String firstLine = breakingChangesAmendment.substring(0, x);

			if (!firstLine.matches("# [0-9a-f]{40}")) {
				addMessage(
					fileName,
					"The first line in each amendment should be # SHA");

				continue;
			}

			x = breakingChangesAmendment.indexOf("```");

			if (x == -1) {
				continue;
			}

			int y = breakingChangesAmendment.lastIndexOf("```");

			if (y == -1) {
				continue;
			}

			String breakingChanges = breakingChangesAmendment.substring(
				x + 3, y);

			String message =
				"Incorrect breaking change content in " + firstLine + ": ";

			checkBreakingChanges(
				fileName, absolutePath, breakingChanges.split("\n----"),
				message, false);
		}
	}

	private List<String> _splitBreakingChangesAmendments(String content)
		throws IOException {

		StringBundler sb = new StringBundler();

		List<String> breakingChangesAmendments = new ArrayList<>();

		try (UnsyncBufferedReader unsyncBufferedReader =
				new UnsyncBufferedReader(new UnsyncStringReader(content))) {

			boolean codeBlock = false;
			String line = StringPool.BLANK;

			while ((line = unsyncBufferedReader.readLine()) != null) {
				if (Validator.isBlank(line)) {
					sb.append("\n");

					continue;
				}

				if (line.equals("----") && !codeBlock && (sb.index() > 0)) {
					sb.append(line);
					sb.append("\n");

					breakingChangesAmendments.add(sb.toString());

					sb.setIndex(0);

					continue;
				}

				sb.append(line);
				sb.append("\n");

				if (!line.startsWith("```") ||
					(StringUtil.count(line, "```") != 1)) {

					continue;
				}

				codeBlock = !codeBlock;
			}

			if (sb.index() > 0) {
				breakingChangesAmendments.add(sb.toString());
			}
		}

		return breakingChangesAmendments;
	}

}