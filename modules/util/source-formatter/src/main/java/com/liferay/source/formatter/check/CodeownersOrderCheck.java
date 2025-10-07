/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.source.formatter.check;

import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.source.formatter.check.comparator.PropertyValueComparator;

import java.util.Collections;
import java.util.List;

/**
 * @author Alan Huang
 */
public class CodeownersOrderCheck extends BaseFileCheck {

	@Override
	protected String doProcess(
		String fileName, String absolutePath, String content) {

		List<String> lines = ListUtil.fromString(content);

		Collections.sort(lines, new PropertyValueComparator());

		return StringUtil.merge(lines, "\n");
	}

}