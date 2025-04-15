/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.item.selector.web.internal;

import com.liferay.item.selector.ItemSelectorReturnType;
import com.liferay.item.selector.ItemSelectorView;

import jakarta.portlet.PortletURL;

import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;

import java.io.IOException;
import java.io.PrintWriter;

import java.util.Collections;
import java.util.List;
import java.util.Locale;

/**
 * @author Iván Zaera
 */
public class MediaItemSelectorView
	implements ItemSelectorView<MediaItemSelectorCriterion> {

	@Override
	public Class<MediaItemSelectorCriterion> getItemSelectorCriterionClass() {
		return MediaItemSelectorCriterion.class;
	}

	@Override
	public List<ItemSelectorReturnType> getSupportedItemSelectorReturnTypes() {
		return _supportedItemSelectorReturnTypes;
	}

	@Override
	public String getTitle(Locale locale) {
		return MediaItemSelectorView.class.getName();
	}

	@Override
	public void renderHTML(
			ServletRequest servletRequest, ServletResponse servletResponse,
			MediaItemSelectorCriterion mediaItemSelectorCriterion,
			PortletURL portletURL, String itemSelectedEventName, boolean search)
		throws IOException {

		PrintWriter printWriter = servletResponse.getWriter();

		printWriter.print(
			"<html>" + MediaItemSelectorView.class.getName() + "</html>");
	}

	private static final List<ItemSelectorReturnType>
		_supportedItemSelectorReturnTypes = Collections.singletonList(
			new TestURLItemSelectorReturnType());

}