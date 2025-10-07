/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.item.selector.web.internal;

import com.liferay.item.selector.ItemSelectorReturnType;
import com.liferay.item.selector.ItemSelectorView;
import com.liferay.item.selector.ItemSelectorViewDescriptorRenderer;
import com.liferay.object.item.selector.ObjectDefinitionItemSelectorCriterion;
import com.liferay.object.item.selector.ObjectDefinitionItemSelectorReturnType;
import com.liferay.object.item.selector.web.internal.display.context.ObjectDefinitionDisplayContext;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.util.JavaConstants;
import com.liferay.portal.kernel.util.Portal;

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
 * @author Jonathan McCann
 */
@Component(service = ItemSelectorView.class)
public class ObjectDefinitionItemSelectorView
	implements ItemSelectorView<ObjectDefinitionItemSelectorCriterion> {

	@Override
	public Class<? extends ObjectDefinitionItemSelectorCriterion>
		getItemSelectorCriterionClass() {

		return ObjectDefinitionItemSelectorCriterion.class;
	}

	@Override
	public List<ItemSelectorReturnType> getSupportedItemSelectorReturnTypes() {
		return _supportedItemSelectorReturnTypes;
	}

	@Override
	public String getTitle(Locale locale) {
		return _language.get(_portal.getResourceBundle(locale), "objects");
	}

	@Override
	public void renderHTML(
			ServletRequest servletRequest, ServletResponse servletResponse,
			ObjectDefinitionItemSelectorCriterion
				objectDefinitionItemSelectorCriterion,
			PortletURL portletURL, String itemSelectedEventName, boolean search)
		throws IOException, ServletException {

		HttpServletRequest httpServletRequest =
			(HttpServletRequest)servletRequest;

		RenderRequest renderRequest =
			(RenderRequest)httpServletRequest.getAttribute(
				JavaConstants.JAKARTA_PORTLET_REQUEST);

		ObjectDefinitionDisplayContext objectDefinitionDisplayContext =
			new ObjectDefinitionDisplayContext(
				httpServletRequest, _objectDefinitionLocalService, portletURL,
				renderRequest);

		_itemSelectorViewDescriptorRenderer.renderHTML(
			httpServletRequest, servletResponse,
			objectDefinitionItemSelectorCriterion, portletURL,
			itemSelectedEventName, search,
			new ObjectDefinitionItemSelectorViewDescriptor(
				httpServletRequest, objectDefinitionDisplayContext));
	}

	private static final List<ItemSelectorReturnType>
		_supportedItemSelectorReturnTypes = Collections.singletonList(
			new ObjectDefinitionItemSelectorReturnType());

	@Reference
	private ItemSelectorViewDescriptorRenderer
		<ObjectDefinitionItemSelectorCriterion>
			_itemSelectorViewDescriptorRenderer;

	@Reference
	private Language _language;

	@Reference
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	@Reference
	private Portal _portal;

}