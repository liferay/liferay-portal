/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.trash.taglib.servlet.taglib;

import com.liferay.trash.TrashHelper;

import jakarta.servlet.jsp.tagext.TagData;
import jakarta.servlet.jsp.tagext.TagExtraInfo;
import jakarta.servlet.jsp.tagext.VariableInfo;

/**
 * @author Eudaldo Alonso
 */
public class DefineObjectsTei extends TagExtraInfo {

	@Override
	public VariableInfo[] getVariableInfo(TagData data) {
		return Concealer._variableInfo;
	}

	private static class Concealer {

		private static final VariableInfo[] _variableInfo = {
			new VariableInfo(
				"trashHelper", TrashHelper.class.getName(), true,
				VariableInfo.AT_END)
		};

	}

}