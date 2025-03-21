/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.taglib.ui;

import jakarta.servlet.jsp.tagext.TagData;
import jakarta.servlet.jsp.tagext.TagExtraInfo;
import jakarta.servlet.jsp.tagext.VariableInfo;

/**
 * @author Brian Wing Shun Chan
 * @deprecated As of Cavanaugh (7.4.x), replaced by {@link
 *             com.liferay.frontend.taglib.clay.servlet.taglib.TabsPanelTag}
 */
@Deprecated
public class SectionTei extends TagExtraInfo {

	@Override
	public VariableInfo[] getVariableInfo(TagData tagData) {
		return Concealer._variableInfo;
	}

	private static class Concealer {

		private static final VariableInfo[] _variableInfo = {
			new VariableInfo(
				"sectionParam", String.class.getName(), true,
				VariableInfo.NESTED),
			new VariableInfo(
				"sectionName", String.class.getName(), true,
				VariableInfo.NESTED),
			new VariableInfo(
				"sectionSelected", Boolean.class.getName(), true,
				VariableInfo.NESTED),
			new VariableInfo(
				"sectionScroll", String.class.getName(), true,
				VariableInfo.NESTED),
			new VariableInfo(
				"sectionRedirectParams", String.class.getName(), true,
				VariableInfo.NESTED)
		};

	}

}