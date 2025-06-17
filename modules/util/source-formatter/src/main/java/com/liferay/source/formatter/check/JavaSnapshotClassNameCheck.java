/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.source.formatter.check;

import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.source.formatter.check.util.JavaSourceUtil;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Alan Huang
 */
public class JavaSnapshotClassNameCheck extends BaseFileCheck {

	@Override
	public boolean isLiferaySourceCheck() {
		return true;
	}

	@Override
	protected String doProcess(
		String fileName, String absolutePath, String content) {

		StringBuffer sb = new StringBuffer();

		Matcher matcher = _snapshotPattern.matcher(content);

		while (matcher.find()) {
			String className = JavaSourceUtil.getClassName(fileName);
			String holderClassName = matcher.group(1);

			if (holderClassName.equals(className)) {
				continue;
			}

			String replacement = StringUtil.replaceFirst(
				matcher.group(), holderClassName + ".class",
				className + ".class");

			matcher.appendReplacement(sb, replacement);
		}

		if (sb.length() > 0) {
			matcher.appendTail(sb);

			return sb.toString();
		}

		return content;
	}

	private static final Pattern _snapshotPattern = Pattern.compile(
		"\n\tprivate static final Snapshot<.+>\\s+\\w+\\s+=\\s+new Snapshot<>" +
			"\\(\\s*([\\w\\s.]+)\\.class");

}