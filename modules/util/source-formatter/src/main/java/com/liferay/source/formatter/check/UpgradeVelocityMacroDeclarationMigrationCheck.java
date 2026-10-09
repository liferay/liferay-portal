/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.source.formatter.check;

import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.source.formatter.check.constants.VelocityMigrationConstants;
import com.liferay.source.formatter.check.util.VelocityMigrationUtil;

/**
 * @author Nícolas Moura
 */
public class UpgradeVelocityMacroDeclarationMigrationCheck
	extends BaseUpgradeVelocityMigrationCheck {

	@Override
	protected String migrateContent(String content) {
		String[] lines = StringUtil.splitLines(content);

		for (int i = 0; i < lines.length; i++) {
			String newLine = lines[i];

			if (!newLine.contains(
					VelocityMigrationConstants.VELOCITY_MACRO_START) ||
				!VelocityMigrationUtil.isVelocityStatement(
					newLine, VelocityMigrationConstants.VELOCITY_MACRO_START)) {

				continue;
			}

			newLine = VelocityMigrationUtil.removeFirstParenthesis(newLine);
			newLine = StringUtil.replace(
				newLine, VelocityMigrationConstants.VELOCITY_MACRO_START,
				VelocityMigrationConstants.FREEMARKER_MACRO_START);
			newLine = StringUtil.replaceLast(
				newLine, CharPool.CLOSE_PARENTHESIS, CharPool.GREATER_THAN);

			VelocityMigrationUtil.replaceStatementEnd(
				i, lines, VelocityMigrationConstants.VELOCITY_MACRO_START);

			lines[i] = newLine;
		}

		return com.liferay.petra.string.StringUtil.merge(
			lines, StringPool.NEW_LINE);
	}

}