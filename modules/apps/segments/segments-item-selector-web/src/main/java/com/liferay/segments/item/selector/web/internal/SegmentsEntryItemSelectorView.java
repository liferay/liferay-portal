/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.segments.item.selector.web.internal;

import com.liferay.item.selector.ItemSelectorReturnType;
import com.liferay.item.selector.ItemSelectorView;
import com.liferay.item.selector.ItemSelectorViewDescriptorRenderer;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.util.JavaConstants;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.segments.item.selector.SegmentsEntryItemSelectorCriterion;
import com.liferay.segments.item.selector.SegmentsEntryItemSelectorReturnType;
import com.liferay.segments.item.selector.web.internal.display.context.SegmentsEntryDisplayContext;
import com.liferay.segments.service.SegmentsEntryLocalService;

import jakarta.portlet.PortletURL;
import jakarta.portlet.RenderRequest;

import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;

import java.io.IOException;

import java.util.Collections;
import java.util.List;
import java.util.Locale;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Stefan Tanasie
 */
@Component(service = ItemSelectorView.class)
public class SegmentsEntryItemSelectorView
	implements ItemSelectorView<SegmentsEntryItemSelectorCriterion> {

	@Override
	public Class<? extends SegmentsEntryItemSelectorCriterion>
		getItemSelectorCriterionClass() {

		return SegmentsEntryItemSelectorCriterion.class;
	}

	@Override
	public List<ItemSelectorReturnType> getSupportedItemSelectorReturnTypes() {
		return _supportedItemSelectorReturnTypes;
	}

	@Override
	public String getTitle(Locale locale) {
		return _language.get(_portal.getResourceBundle(locale), "segments");
	}

	@Override
	public void renderHTML(
			ServletRequest servletRequest, ServletResponse servletResponse,
			SegmentsEntryItemSelectorCriterion
				segmentsEntryItemSelectorCriterion,
			PortletURL portletURL, String itemSelectedEventName, boolean search)
		throws IOException, ServletException {

		HttpServletRequest httpServletRequest =
			(HttpServletRequest)servletRequest;

		RenderRequest renderRequest =
			(RenderRequest)httpServletRequest.getAttribute(
				JavaConstants.JAKARTA_PORTLET_REQUEST);

		SegmentsEntryDisplayContext segmentsEntryDisplayContext =
			new SegmentsEntryDisplayContext(
				httpServletRequest, portletURL, renderRequest,
				segmentsEntryItemSelectorCriterion, _segmentsEntryLocalService);

		_itemSelectorViewDescriptorRenderer.renderHTML(
			httpServletRequest, servletResponse,
			segmentsEntryItemSelectorCriterion, portletURL,
			itemSelectedEventName, search,
			new SegmentsEntryItemSelectorViewDescriptor(
				httpServletRequest, segmentsEntryDisplayContext));
	}

	private static final List<ItemSelectorReturnType>
		_supportedItemSelectorReturnTypes = Collections.singletonList(
			new SegmentsEntryItemSelectorReturnType());

	@Reference
	private ItemSelectorViewDescriptorRenderer
		<SegmentsEntryItemSelectorCriterion>
			_itemSelectorViewDescriptorRenderer;

	@Reference
	private Language _language;

	@Reference
	private Portal _portal;

	@Reference
	private SegmentsEntryLocalService _segmentsEntryLocalService;

}