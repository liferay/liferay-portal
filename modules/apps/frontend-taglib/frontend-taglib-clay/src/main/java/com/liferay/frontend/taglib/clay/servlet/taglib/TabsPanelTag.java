/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.frontend.taglib.clay.servlet.taglib;

import com.liferay.frontend.taglib.clay.internal.servlet.taglib.BaseContainerTag;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.TabsItem;
import com.liferay.petra.string.StringPool;

import jakarta.servlet.jsp.JspException;
import jakarta.servlet.jsp.tagext.BodyTag;

import java.util.List;
import java.util.Set;

/**
 * @author Carlos Lancha
 */
public class TabsPanelTag extends BaseContainerTag implements BodyTag {

	@Override
	public int doStartTag() throws JspException {
		_tabsTag = (TabsTag)findAncestorWithClass(this, TabsTag.class);

		if (_tabsTag == null) {
			throw new JspException();
		}

		_tabsTag.setPanelsCount(_tabsTag.getPanelsCount() + 1);

		setAttributeNamespace(_ATTRIBUTE_NAMESPACE);

		setDynamicAttribute(StringPool.BLANK, "role", "tabpanel");
		setDynamicAttribute(StringPool.BLANK, "tabindex", "0");

		return super.doStartTag();
	}

	@Override
	protected String processCssClasses(Set<String> cssClasses) {
		cssClasses.add("tab-pane");

		if (_isActive()) {
			cssClasses.add("active");
			cssClasses.add("show");
		}

		if (_tabsTag.isFade()) {
			cssClasses.add("fade");
		}

		return super.processCssClasses(cssClasses);
	}

	private boolean _isActive() {
		List<TabsItem> tabsItems = _tabsTag.getTabsItems();

		TabsItem tabsItem = tabsItems.get(_tabsTag.getPanelsCount() - 1);

		Boolean active = (Boolean)tabsItem.get("active");

		if ((active != null) && active) {
			return true;
		}

		return false;
	}

	private static final String _ATTRIBUTE_NAMESPACE = "clay:tabs:panel:";

	private TabsTag _tabsTag;

}