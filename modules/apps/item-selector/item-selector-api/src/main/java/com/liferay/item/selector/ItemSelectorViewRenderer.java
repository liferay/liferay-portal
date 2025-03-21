/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.item.selector;

import jakarta.portlet.PortletURL;

import jakarta.servlet.ServletException;
import jakarta.servlet.jsp.PageContext;

import java.io.IOException;

/**
 * @author Iván Zaera
 */
public interface ItemSelectorViewRenderer {

	public String getItemSelectedEventName();

	public ItemSelectorCriterion getItemSelectorCriterion();

	public ItemSelectorView<ItemSelectorCriterion> getItemSelectorView();

	public PortletURL getPortletURL();

	public void renderHTML(PageContext pageContext)
		throws IOException, ServletException;

}