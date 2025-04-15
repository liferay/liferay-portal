/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.taglib.ui;

import com.liferay.portal.kernel.servlet.taglib.ui.MenuItem;
import com.liferay.taglib.util.IncludeTag;

import jakarta.servlet.http.HttpServletRequest;

/**
 * @author Iván Zaera
 * @deprecated As of Cavanaugh (7.4.x), with no direct replacement
 */
@Deprecated
public class MenuItemTag extends IncludeTag {

	@Override
	public int doStartTag() {
		return EVAL_BODY_INCLUDE;
	}

	public MenuItem getMenuItem() {
		return _menuItem;
	}

	public void setMenuItem(MenuItem menuItem) {
		_menuItem = menuItem;
	}

	@Override
	protected void cleanUp() {
		super.cleanUp();

		_menuItem = null;
	}

	@Override
	protected String getPage() {
		return _PAGE;
	}

	@Override
	protected boolean isCleanUpSetAttributes() {
		return _CLEAN_UP_SET_ATTRIBUTES;
	}

	@Override
	protected void setAttributes(HttpServletRequest httpServletRequest) {
		httpServletRequest.setAttribute(
			"liferay-ui:menu_item:menuItem", _menuItem);
	}

	private static final boolean _CLEAN_UP_SET_ATTRIBUTES = true;

	private static final String _PAGE = "/html/taglib/ui/menu_item/page.jsp";

	private MenuItem _menuItem;

}