/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.item.selector.web.internal;

import com.liferay.item.selector.ItemSelectorReturnType;
import com.liferay.item.selector.ItemSelectorView;
import com.liferay.item.selector.criteria.GroupItemSelectorReturnType;
import com.liferay.item.selector.criteria.URLItemSelectorReturnType;
import com.liferay.item.selector.criteria.UUIDItemSelectorReturnType;
import com.liferay.item.selector.criteria.group.criterion.GroupItemSelectorCriterion;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.ResourceBundleUtil;
import com.liferay.site.item.selector.web.internal.renderer.MyGroupItemSelectorViewRenderer;
import com.liferay.site.provider.GroupURLProvider;

import jakarta.portlet.PortletURL;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;

import java.io.IOException;

import java.util.Collections;
import java.util.List;
import java.util.Locale;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Cristina González
 */
@Component(
	property = "item.selector.view.order:Integer=300",
	service = ItemSelectorView.class
)
public class MyGroupItemSelectorView
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
			_portal.getResourceBundle(locale), "my-sites");
	}

	@Override
	public boolean isVisible(
		GroupItemSelectorCriterion groupItemSelectorCriterion,
		ThemeDisplay themeDisplay) {

		return groupItemSelectorCriterion.isIncludeRecentSites();
	}

	@Override
	public void renderHTML(
			ServletRequest servletRequest, ServletResponse servletResponse,
			GroupItemSelectorCriterion groupItemSelectorCriterion,
			PortletURL portletURL, String itemSelectedEventName, boolean search)
		throws IOException, ServletException {

		_myGroupItemSelectorViewRenderer.renderHTML(
			servletRequest, servletResponse, groupItemSelectorCriterion,
			portletURL, itemSelectedEventName, search);
	}

	@Activate
	protected void activate() {
		_myGroupItemSelectorViewRenderer = new MyGroupItemSelectorViewRenderer(
			_groupURLProvider, _servletContext);
	}

	@Deactivate
	protected void deactivate() {
		_myGroupItemSelectorViewRenderer = null;
	}

	private static final List<ItemSelectorReturnType>
		_supportedItemSelectorReturnTypes = Collections.unmodifiableList(
			ListUtil.fromArray(
				new GroupItemSelectorReturnType(),
				new URLItemSelectorReturnType(),
				new UUIDItemSelectorReturnType()));

	@Reference
	private GroupURLProvider _groupURLProvider;

	private MyGroupItemSelectorViewRenderer _myGroupItemSelectorViewRenderer;

	@Reference
	private Portal _portal;

	@Reference(
		target = "(osgi.web.symbolicname=com.liferay.site.item.selector.web)"
	)
	private ServletContext _servletContext;

}