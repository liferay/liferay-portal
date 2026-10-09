/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.source.formatter.check;

import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.source.formatter.parser.GradleFile;

import java.util.Set;
import java.util.TreeSet;

/**
 * @author Peter Shin
 */
public class GradleBodyCheck extends BaseGradleFileCheck {

	@Override
	protected String doProcess(
		String fileName, String absolutePath, GradleFile gradleFile,
		String content) {

		if (absolutePath.contains("/project-templates-")) {
			return content;
		}

		String bodyBlock = gradleFile.getBodyBlock();

		if (Validator.isNull(bodyBlock)) {
			return content;
		}

		String newBlock = StringPool.BLANK;
		Set<String> newBlocks = new TreeSet<>();
		String oldBlocks = StringPool.BLANK;

		for (String line : StringUtil.splitLines(bodyBlock)) {
			if (line.matches(
					"(allprojects|project|subprojects|(else|for|if|while)\\s)" +
						".*\\{")) {

				return content;
			}

			if (Validator.isNull(newBlock) && line.matches("\\w+\\s*\\{")) {
				newBlock = line;
				oldBlocks = oldBlocks + "\n" + line;

				continue;
			}

			if (Validator.isNotNull(newBlock) && !newBlock.endsWith("\n}")) {
				newBlock = newBlock + "\n" + line;
				oldBlocks = oldBlocks + "\n" + line;

				if (newBlock.endsWith("\n}")) {
					newBlocks.add(newBlock);

					newBlock = StringPool.BLANK;
				}

				continue;
			}

			if (Validator.isNull(line)) {
				oldBlocks = oldBlocks + "\n" + line;

				continue;
			}

			newBlocks.clear();
			oldBlocks = StringPool.BLANK;
		}

		if (newBlocks.isEmpty()) {
			return content;
		}

		StringBundler sb = new StringBundler(2 * newBlocks.size());

		for (String s : newBlocks) {
			sb.append(s);
			sb.append("\n\n");
		}

		sb.setIndex(sb.index() - 1);

		return StringUtil.replaceLast(
			content, StringUtil.trim(oldBlocks), sb.toString());
	}

}