/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.taglib.util;

import jakarta.servlet.jsp.tagext.TagData;
import jakarta.servlet.jsp.tagext.TagExtraInfo;
import jakarta.servlet.jsp.tagext.VariableInfo;

/**
 * @author Brian Wing Shun Chan
 */
public class BufferTei extends TagExtraInfo {

	@Override
	public VariableInfo[] getVariableInfo(TagData tagData) {
		String var = tagData.getAttributeString("var");

		return new VariableInfo[] {
			new VariableInfo(
				var, String.class.getName(), true, VariableInfo.AT_END)
		};
	}

}