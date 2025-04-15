/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.user.taglib.servlet.taglib;

import com.liferay.portal.kernel.model.User;

import jakarta.servlet.jsp.tagext.TagData;
import jakarta.servlet.jsp.tagext.TagExtraInfo;
import jakarta.servlet.jsp.tagext.VariableInfo;

/**
 * @author Brian Wing Shun Chan
 */
public class UserDisplayTei extends TagExtraInfo {

	@Override
	public VariableInfo[] getVariableInfo(TagData tagData) {
		return Concealer._variableInfo;
	}

	private static class Concealer {

		private static final VariableInfo[] _variableInfo = {
			new VariableInfo(
				"userDisplay", User.class.getName(), true,
				VariableInfo.AT_BEGIN)
		};

	}

}