/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.page.template.item.selector.web.internal;

import com.liferay.item.selector.ItemSelectorReturnType;
import com.liferay.item.selector.ItemSelectorView;
import com.liferay.item.selector.criteria.UUIDItemSelectorReturnType;
import com.liferay.layout.page.template.item.selector.LayoutPageTemplateCollectionTreeNodeItemSelectorCriterion;
import com.liferay.layout.page.template.item.selector.web.internal.display.context.LayoutPageTemplateCollectionsTreeNodeDisplayContext;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.template.react.renderer.ComponentDescriptor;
import com.liferay.portal.template.react.renderer.ReactRenderer;

import jakarta.portlet.PortletURL;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;

import java.io.IOException;
import java.io.PrintWriter;

import java.util.Collections;
import java.util.List;
import java.util.Locale;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Bárbara Cabrera
 */
@Component(service = ItemSelectorView.class)
public class LayoutPageTemplateCollectionsTreeNodeItemSelectorView
	implements ItemSelectorView
		<LayoutPageTemplateCollectionTreeNodeItemSelectorCriterion> {

	@Override
	public Class<LayoutPageTemplateCollectionTreeNodeItemSelectorCriterion>
		getItemSelectorCriterionClass() {

		return LayoutPageTemplateCollectionTreeNodeItemSelectorCriterion.class;
	}

	public ServletContext getServletContext() {
		return _servletContext;
	}

	@Override
	public List<ItemSelectorReturnType> getSupportedItemSelectorReturnTypes() {
		return _supportedItemSelectorReturnTypes;
	}

	@Override
	public String getTitle(Locale locale) {
		return _language.get(locale, "source");
	}

	@Override
	public void renderHTML(
			ServletRequest servletRequest, ServletResponse servletResponse,
			LayoutPageTemplateCollectionTreeNodeItemSelectorCriterion
				layoutPageTemplateCollectionTreeNodeItemSelectorCriterion,
			PortletURL portletURL, String itemSelectedEventName, boolean search)
		throws IOException, ServletException {

		PrintWriter printWriter = servletResponse.getWriter();

		printWriter.write("<div>");

		_reactRenderer.renderReact(
			new ComponentDescriptor(
				"{SelectLayoutPageTemplateCollection} from " +
					"layout-page-template-item-selector-web"),
			HashMapBuilder.<String, Object>put(
				"itemSelectorSaveEvent", "selectFolder"
			).put(
				"layoutPageTemplateCollections",
				() -> {
					LayoutPageTemplateCollectionsTreeNodeDisplayContext
						layoutPageTemplateCollectionsTreeNodeDisplayContext =
							new LayoutPageTemplateCollectionsTreeNodeDisplayContext(
								layoutPageTemplateCollectionTreeNodeItemSelectorCriterion,
								(ThemeDisplay)servletRequest.getAttribute(
									WebKeys.THEME_DISPLAY));

					return layoutPageTemplateCollectionsTreeNodeDisplayContext.
						getLayoutPageTemplateCollectionJSONArray();
				}
			).build(),
			(HttpServletRequest)servletRequest, printWriter);

		printWriter.write("</div>");
	}

	private static final List<ItemSelectorReturnType>
		_supportedItemSelectorReturnTypes = Collections.singletonList(
			new UUIDItemSelectorReturnType());

	@Reference
	private Language _language;

	@Reference
	private ReactRenderer _reactRenderer;

	@Reference(
		target = "(osgi.web.symbolicname=com.liferay.layout.page.template.item.selector.web)"
	)
	private ServletContext _servletContext;

}