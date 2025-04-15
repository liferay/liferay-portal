/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.application.list.taglib.servlet.taglib;

import com.liferay.application.list.GroupProvider;
import com.liferay.application.list.constants.ApplicationListWebKeys;
import com.liferay.application.list.taglib.internal.servlet.ServletContextUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.taglib.util.IncludeTag;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.jsp.PageContext;

/**
 * @author Adolfo Pérez
 */
public class BasePanelTag extends IncludeTag {

	public Group getGroup() {
		HttpServletRequest httpServletRequest = getRequest();

		GroupProvider groupProvider =
			(GroupProvider)httpServletRequest.getAttribute(
				ApplicationListWebKeys.GROUP_PROVIDER);

		if (groupProvider != null) {
			Group group = groupProvider.getGroup(httpServletRequest);

			if (group != null) {
				return group;
			}
		}

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		return themeDisplay.getScopeGroup();
	}

	@Override
	public void setPageContext(PageContext pageContext) {
		super.setPageContext(pageContext);

		setServletContext(ServletContextUtil.getServletContext());
	}

}