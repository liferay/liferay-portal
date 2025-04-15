/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.staging.taglib.servlet.taglib;

import com.liferay.portal.kernel.model.Group;

import jakarta.servlet.jsp.tagext.TagData;
import jakarta.servlet.jsp.tagext.TagExtraInfo;
import jakarta.servlet.jsp.tagext.VariableInfo;

/**
 * @author Levente Hudák
 */
public class DefineObjectsTei extends TagExtraInfo {

	@Override
	public VariableInfo[] getVariableInfo(TagData tagData) {
		return Concealer._variableInfo;
	}

	private static class Concealer {

		private static final VariableInfo[] _variableInfo = {
			new VariableInfo(
				"group", Group.class.getName(), true, VariableInfo.AT_END),
			new VariableInfo(
				"groupId", Long.class.getName(), true, VariableInfo.AT_END),
			new VariableInfo(
				"liveGroup", Group.class.getName(), true, VariableInfo.AT_END),
			new VariableInfo(
				"liveGroupId", Long.class.getName(), true, VariableInfo.AT_END),
			new VariableInfo(
				"privateLayout", Boolean.class.getName(), true,
				VariableInfo.AT_END),
			new VariableInfo(
				"scopeGroup", Group.class.getName(), true, VariableInfo.AT_END),
			new VariableInfo(
				"scopeGroupId", Long.class.getName(), true,
				VariableInfo.AT_END),
			new VariableInfo(
				"stagingGroup", Group.class.getName(), true,
				VariableInfo.AT_END),
			new VariableInfo(
				"stagingGroupId", Long.class.getName(), true,
				VariableInfo.AT_END)
		};

	}

}