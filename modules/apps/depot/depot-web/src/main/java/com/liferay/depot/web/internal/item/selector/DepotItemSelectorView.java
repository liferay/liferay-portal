/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.depot.web.internal.item.selector;

import com.liferay.depot.web.internal.application.list.DepotPanelAppController;
import com.liferay.depot.web.internal.util.DepotAdminGroupSearchProvider;
import com.liferay.item.selector.ItemSelectorReturnType;
import com.liferay.item.selector.ItemSelectorView;
import com.liferay.item.selector.criteria.GroupItemSelectorReturnType;
import com.liferay.item.selector.criteria.URLItemSelectorReturnType;
import com.liferay.item.selector.criteria.UUIDItemSelectorReturnType;
import com.liferay.item.selector.criteria.group.criterion.GroupItemSelectorCriterion;
import com.liferay.petra.reflect.ReflectionUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.JavaConstants;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.ResourceBundleUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.site.item.selector.display.context.SitesItemSelectorViewDisplayContext;
import com.liferay.site.item.selector.renderer.SiteItemSelectorViewRenderer;
import com.liferay.site.search.GroupSearch;

import jakarta.portlet.PortletRequest;
import jakarta.portlet.PortletResponse;
import jakarta.portlet.PortletURL;

import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;

import java.io.IOException;

import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Alejandro Tardín
 */
@Component(
	property = "item.selector.view.order:Integer=400",
	service = ItemSelectorView.class
)
public class DepotItemSelectorView
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
		ResourceBundle resourceBundle = ResourceBundleUtil.getBundle(
			locale, getClass());

		return ResourceBundleUtil.getString(resourceBundle, "asset-libraries");
	}

	@Override
	public boolean isVisible(
		GroupItemSelectorCriterion groupItemSelectorCriterion,
		ThemeDisplay themeDisplay) {

		if (groupItemSelectorCriterion == null) {
			return true;
		}

		String portletId = groupItemSelectorCriterion.getPortletId();

		if (Validator.isNull(portletId)) {
			return true;
		}

		return _depotPanelAppController.isShow(portletId);
	}

	@Override
	public void renderHTML(
			ServletRequest servletRequest, ServletResponse servletResponse,
			GroupItemSelectorCriterion groupItemSelectorCriterion,
			PortletURL portletURL, String itemSelectedEventName, boolean search)
		throws IOException, ServletException {

		SitesItemSelectorViewDisplayContext
			depotSitesItemSelectorViewDisplayContext =
				new DepotSitesItemSelectorViewDisplayContext(
					(HttpServletRequest)servletRequest, itemSelectedEventName,
					portletURL, groupItemSelectorCriterion);

		_siteItemSelectorViewRenderer.renderHTML(
			depotSitesItemSelectorViewDisplayContext);
	}

	private static final List<ItemSelectorReturnType>
		_supportedItemSelectorReturnTypes = Collections.unmodifiableList(
			ListUtil.fromArray(
				new GroupItemSelectorReturnType(),
				new URLItemSelectorReturnType(),
				new UUIDItemSelectorReturnType()));

	@Reference
	private DepotAdminGroupSearchProvider _depotAdminGroupSearchProvider;

	@Reference
	private DepotPanelAppController _depotPanelAppController;

	@Reference
	private SiteItemSelectorViewRenderer _siteItemSelectorViewRenderer;

	private class DepotSitesItemSelectorViewDisplayContext
		implements SitesItemSelectorViewDisplayContext {

		public DepotSitesItemSelectorViewDisplayContext(
			HttpServletRequest httpServletRequest, String itemSelectedEventName,
			PortletURL portletURL,
			GroupItemSelectorCriterion groupItemSelectorCriterion) {

			_httpServletRequest = httpServletRequest;
			_itemSelectedEventName = itemSelectedEventName;
			_portletURL = portletURL;
			_groupItemSelectorCriterion = groupItemSelectorCriterion;
		}

		@Override
		public String getDisplayStyle() {
			return ParamUtil.getString(
				_httpServletRequest, "displayStyle", "icon");
		}

		@Override
		public GroupItemSelectorCriterion getGroupItemSelectorCriterion() {
			return _groupItemSelectorCriterion;
		}

		@Override
		public String getGroupName(Group group) throws PortalException {
			ThemeDisplay themeDisplay =
				(ThemeDisplay)_httpServletRequest.getAttribute(
					WebKeys.THEME_DISPLAY);

			return group.getDescriptiveName(themeDisplay.getLocale());
		}

		@Override
		public GroupSearch getGroupSearch() {
			try {
				return _depotAdminGroupSearchProvider.getGroupSearch(
					_groupItemSelectorCriterion, getPortletRequest(),
					getPortletURL());
			}
			catch (PortalException portalException) {
				return ReflectionUtil.throwException(portalException);
			}
		}

		@Override
		public String getItemSelectedEventName() {
			return _itemSelectedEventName;
		}

		@Override
		public PortletRequest getPortletRequest() {
			return (PortletRequest)_httpServletRequest.getAttribute(
				JavaConstants.JAVAX_PORTLET_REQUEST);
		}

		@Override
		public PortletResponse getPortletResponse() {
			return (PortletResponse)_httpServletRequest.getAttribute(
				JavaConstants.JAVAX_PORTLET_RESPONSE);
		}

		@Override
		public PortletURL getPortletURL() {
			return _portletURL;
		}

		@Override
		public boolean isShowChildSitesLink() {
			return false;
		}

		@Override
		public boolean isShowSearch() {
			return true;
		}

		@Override
		public boolean isShowSortFilter() {
			return true;
		}

		private final GroupItemSelectorCriterion _groupItemSelectorCriterion;
		private final HttpServletRequest _httpServletRequest;
		private final String _itemSelectedEventName;
		private final PortletURL _portletURL;

	}

}