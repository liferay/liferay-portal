/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.source.formatter.check;

import com.liferay.petra.string.CharPool;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.source.formatter.check.constants.VelocityMigrationConstants;

/**
 * @author Nícolas Moura
 */
public class UpgradeVelocityCommentMigrationCheck
	extends BaseUpgradeVelocityMigrationCheck {

	@Override
	protected String migrateContent(String content) {
		String[] lines = StringUtil.splitLines(content);

		for (String line : lines) {
			if (!line.contains(
					VelocityMigrationConstants.VELOCITY_COMMENT_LINE) ||
				(line.length() == 2)) {

				continue;
			}

			String newLineStart = StringUtil.replace(
				line, VelocityMigrationConstants.VELOCITY_COMMENT_LINE,
				VelocityMigrationConstants.FREEMARKER_COMMENT_START);

			String newLine =
				newLineStart + CharPool.SPACE +
					VelocityMigrationConstants.FREEMARKER_COMMENT_END;

			if (newLine.contains(
					VelocityMigrationConstants.VELOCITY_TEMPLATE_DECLARATION)) {

				newLine = StringUtil.replace(
					newLine,
					VelocityMigrationConstants.VELOCITY_TEMPLATE_DECLARATION,
					"FreeMarker Template");
			}

			content = StringUtil.replace(content, line, newLine);
		}

		StringUtil.replace(
			content, "#*", VelocityMigrationConstants.FREEMARKER_COMMENT_START);
		StringUtil.replace(
			content, "*#", VelocityMigrationConstants.FREEMARKER_COMMENT_END);

		return StringUtil.removeSubstring(
			content, VelocityMigrationConstants.VELOCITY_COMMENT_LINE);
	}

}