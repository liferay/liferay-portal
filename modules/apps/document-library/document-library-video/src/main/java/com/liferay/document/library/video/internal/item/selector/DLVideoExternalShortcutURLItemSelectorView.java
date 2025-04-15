/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.document.library.video.internal.item.selector;

import com.liferay.document.library.video.internal.constants.DLVideoWebKeys;
import com.liferay.item.selector.ItemSelectorReturnType;
import com.liferay.item.selector.ItemSelectorView;
import com.liferay.item.selector.criteria.VideoEmbeddableHTMLItemSelectorReturnType;
import com.liferay.item.selector.criteria.video.criterion.VideoItemSelectorCriterion;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.util.ListUtil;

import jakarta.portlet.PortletURL;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;

import java.io.IOException;

import java.util.Collections;
import java.util.List;
import java.util.Locale;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Ambrín Chaudhary
 */
@Component(service = ItemSelectorView.class)
public class DLVideoExternalShortcutURLItemSelectorView
	implements ItemSelectorView<VideoItemSelectorCriterion> {

	@Override
	public Class<VideoItemSelectorCriterion> getItemSelectorCriterionClass() {
		return VideoItemSelectorCriterion.class;
	}

	@Override
	public List<ItemSelectorReturnType> getSupportedItemSelectorReturnTypes() {
		return _supportedItemSelectorReturnTypes;
	}

	@Override
	public String getTitle(Locale locale) {
		return _language.get(locale, "url");
	}

	@Override
	public void renderHTML(
			ServletRequest servletRequest, ServletResponse servletResponse,
			VideoItemSelectorCriterion videoItemSelectorCriterion,
			PortletURL portletURL, String itemSelectedEventName, boolean search)
		throws IOException, ServletException {

		RequestDispatcher requestDispatcher =
			_servletContext.getRequestDispatcher("/url.jsp");

		servletRequest.setAttribute(
			DLVideoWebKeys.EVENT_NAME, itemSelectedEventName);

		requestDispatcher.include(servletRequest, servletResponse);
	}

	private static final List<ItemSelectorReturnType>
		_supportedItemSelectorReturnTypes = Collections.unmodifiableList(
			ListUtil.fromArray(
				new VideoEmbeddableHTMLItemSelectorReturnType()));

	@Reference
	private Language _language;

	@Reference(
		target = "(osgi.web.symbolicname=com.liferay.document.library.video)"
	)
	private ServletContext _servletContext;

}