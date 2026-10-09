/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.source.formatter.check;

import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.source.formatter.check.util.GradleSourceUtil;
import com.liferay.source.formatter.check.util.SourceUtil;

import java.util.List;

/**
 * @author Alan Huang
 */
public class GradlePetraModuleDependenciesCheck extends BaseFileCheck {

	@Override
	public boolean isModuleSourceCheck() {
		return true;
	}

	@Override
	protected String doProcess(
		String fileName, String absolutePath, String content) {

		if (!absolutePath.contains("/modules/core/petra/")) {
			return content;
		}

		List<String> dependenciesBlocks =
			GradleSourceUtil.getDependenciesBlocks(content);

		for (String dependenciesBlock : dependenciesBlocks) {
			int x = dependenciesBlock.indexOf("\n");
			int y = dependenciesBlock.lastIndexOf("\n");

			if (x == y) {
				continue;
			}

			String dependencies = dependenciesBlock.substring(x, y + 1);

			for (String line : StringUtil.splitLines(dependencies)) {
				if (Validator.isNull(line) || line.contains("petra")) {
					continue;
				}

				addMessage(
					fileName,
					"Only modules/core/petra dependencies are allowed",
					SourceUtil.getLineNumber(content, content.indexOf(line)));
			}
		}

		return content;
	}

}