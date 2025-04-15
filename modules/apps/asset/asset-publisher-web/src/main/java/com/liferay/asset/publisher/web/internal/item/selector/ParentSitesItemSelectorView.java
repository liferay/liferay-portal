/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.asset.publisher.web.internal.item.selector;

import com.liferay.asset.publisher.util.AssetPublisherHelper;
import com.liferay.asset.publisher.web.internal.display.context.ParentSitesItemSelectorViewDisplayContext;
import com.liferay.item.selector.ItemSelectorReturnType;
import com.liferay.item.selector.ItemSelectorView;
import com.liferay.item.selector.ItemSelectorViewDescriptorRenderer;
import com.liferay.item.selector.criteria.GroupItemSelectorReturnType;
import com.liferay.item.selector.criteria.group.criterion.GroupItemSelectorCriterion;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.ResourceBundleUtil;

import jakarta.portlet.PortletURL;

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
 * @author Eudaldo Alonso
 */
@Component(
	property = "item.selector.view.order:Integer=100",
	service = ItemSelectorView.class
)
public class ParentSitesItemSelectorView
	implements ItemSelectorView<GroupItemSelectorCriterion> {

	@Override
	public Class<GroupItemSelectorCriterion> getItemSelectorCriterionClass() {
		return GroupItemSelectorCriterion.class;
	}

	@Override
	public List<ItemSelectorReturnType> getSupportedItemSelectorReturnTypes() {
		return _supportedItemSelectorReturnTypes;
	}

	@Override
	public String getTitle(Locale locale) {
		return ResourceBundleUtil.getString(
			_portal.getResourceBundle(locale), "parent-sites");
	}

	@Override
	public boolean isVisible(
		GroupItemSelectorCriterion groupItemSelectorCriterion,
		ThemeDisplay themeDisplay) {

		if (!groupItemSelectorCriterion.isIncludeParentSites()) {
			return false;
		}

		Group siteGroup = themeDisplay.getSiteGroup();

		if (siteGroup.isLayoutPrototype() || siteGroup.isLayoutSetPrototype() ||
			siteGroup.isRoot()) {

			return false;
		}

		return true;
	}

	@Override
	public void renderHTML(
			ServletRequest servletRequest, ServletResponse servletResponse,
			GroupItemSelectorCriterion groupItemSelectorCriterion,
			PortletURL portletURL, String itemSelectedEventName, boolean search)
		throws IOException, ServletException {

		ParentSitesItemSelectorViewDisplayContext
			parentSitesItemSelectorViewDisplayContext =
				new ParentSitesItemSelectorViewDisplayContext(
					groupItemSelectorCriterion,
					(HttpServletRequest)servletRequest, _assetPublisherHelper,
					portletURL);

		_itemSelectorViewDescriptorRenderer.renderHTML(
			servletRequest, servletResponse, groupItemSelectorCriterion,
			portletURL, itemSelectedEventName, search,
			new SitesItemSelectorViewDescriptor(
				(HttpServletRequest)servletRequest,
				parentSitesItemSelectorViewDisplayContext));
	}

	private static final List<ItemSelectorReturnType>
		_supportedItemSelectorReturnTypes = Collections.singletonList(
			new GroupItemSelectorReturnType());

	@Reference
	private AssetPublisherHelper _assetPublisherHelper;

	@Reference
	private ItemSelectorViewDescriptorRenderer<GroupItemSelectorCriterion>
		_itemSelectorViewDescriptorRenderer;

	@Reference
	private Portal _portal;

}