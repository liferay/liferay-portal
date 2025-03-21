/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.taglib.theme;

import com.liferay.portal.kernel.model.ColorScheme;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.Contact;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.LayoutTypePortlet;
import com.liferay.portal.kernel.model.Theme;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.theme.PortletDisplay;
import com.liferay.portal.kernel.theme.ThemeDisplay;

import jakarta.servlet.jsp.tagext.TagData;
import jakarta.servlet.jsp.tagext.TagExtraInfo;
import jakarta.servlet.jsp.tagext.VariableInfo;

import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

/**
 * @author Brian Wing Shun Chan
 */
public class DefineObjectsTei extends TagExtraInfo {

	@Override
	public VariableInfo[] getVariableInfo(TagData tagData) {
		return Concealer._variableInfo;
	}

	private static class Concealer {

		private static final VariableInfo[] _variableInfo = {
			new VariableInfo(
				"themeDisplay", ThemeDisplay.class.getName(), true,
				VariableInfo.AT_END),
			new VariableInfo(
				"company", Company.class.getName(), true, VariableInfo.AT_END),
			new VariableInfo(
				"user", User.class.getName(), true, VariableInfo.AT_END),
			new VariableInfo(
				"realUser", User.class.getName(), true, VariableInfo.AT_END),
			new VariableInfo(
				"contact", Contact.class.getName(), true, VariableInfo.AT_END),
			new VariableInfo(
				"layout", Layout.class.getName(), true, VariableInfo.AT_END),
			new VariableInfo(
				"layouts", List.class.getName(), true, VariableInfo.AT_END),
			new VariableInfo(
				"plid", Long.class.getName(), true, VariableInfo.AT_END),
			new VariableInfo(
				"layoutTypePortlet", LayoutTypePortlet.class.getName(), true,
				VariableInfo.AT_END),
			new VariableInfo(
				"scopeGroupId", Long.class.getName(), true,
				VariableInfo.AT_END),
			new VariableInfo(
				"permissionChecker", PermissionChecker.class.getName(), true,
				VariableInfo.AT_END),
			new VariableInfo(
				"locale", Locale.class.getName(), true, VariableInfo.AT_END),
			new VariableInfo(
				"timeZone", TimeZone.class.getName(), true,
				VariableInfo.AT_END),
			new VariableInfo(
				"theme", Theme.class.getName(), true, VariableInfo.AT_END),
			new VariableInfo(
				"colorScheme", ColorScheme.class.getName(), true,
				VariableInfo.AT_END),
			new VariableInfo(
				"portletDisplay", PortletDisplay.class.getName(), true,
				VariableInfo.AT_END),

			// Deprecated

			new VariableInfo(
				"portletGroupId", Long.class.getName(), true,
				VariableInfo.AT_END)
		};

	}

}