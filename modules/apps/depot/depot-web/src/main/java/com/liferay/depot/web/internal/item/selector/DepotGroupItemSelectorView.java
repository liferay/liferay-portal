/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.depot.web.internal.item.selector;

import com.liferay.depot.web.internal.util.DepotAdminGroupSearchProvider;
import com.liferay.item.selector.ItemSelectorReturnType;
import com.liferay.item.selector.ItemSelectorView;
import com.liferay.item.selector.ItemSelectorViewDescriptor;
import com.liferay.item.selector.ItemSelectorViewDescriptorRenderer;
import com.liferay.item.selector.criteria.GroupItemSelectorReturnType;
import com.liferay.petra.reflect.ReflectionUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.search.SearchContainer;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.util.JavaConstants;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.ResourceBundleUtil;

import jakarta.portlet.PortletRequest;
import jakarta.portlet.PortletURL;

import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;

import java.io.IOException;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Eudaldo Alonso
 */
@Component(
	property = "item.selector.view.order:Integer=200",
	service = ItemSelectorView.class
)
public class DepotGroupItemSelectorView
	implements ItemSelectorView<DepotGroupItemSelectorCriterion> {

	@Override
	public Class<DepotGroupItemSelectorCriterion>
		getItemSelectorCriterionClass() {

		return DepotGroupItemSelectorCriterion.class;
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
	public void renderHTML(
			ServletRequest servletRequest, ServletResponse servletResponse,
			DepotGroupItemSelectorCriterion depotGroupItemSelectorCriterion,
			PortletURL portletURL, String itemSelectedEventName, boolean search)
		throws IOException, ServletException {

		_itemSelectorViewDescriptorRenderer.renderHTML(
			servletRequest, servletResponse, depotGroupItemSelectorCriterion,
			portletURL, itemSelectedEventName, search,
			new DepotGroupSelectorViewDescriptor(
				depotGroupItemSelectorCriterion,
				(HttpServletRequest)servletRequest, portletURL));
	}

	private static final Log _log = LogFactoryUtil.getLog(
		DepotGroupItemSelectorView.class);

	private static final List<ItemSelectorReturnType>
		_supportedItemSelectorReturnTypes = Collections.singletonList(
			new GroupItemSelectorReturnType());

	@Reference
	private DepotAdminGroupSearchProvider _depotAdminGroupSearchProvider;

	@Reference
	private ItemSelectorViewDescriptorRenderer<DepotGroupItemSelectorCriterion>
		_itemSelectorViewDescriptorRenderer;

	@Reference
	private Portal _portal;

	@Reference
	private UserLocalService _userLocalService;

	private class DepotGroupItemDescriptor
		implements ItemSelectorViewDescriptor.ItemDescriptor {

		public DepotGroupItemDescriptor(
			Group group, HttpServletRequest httpServletRequest) {

			_group = group;
			_httpServletRequest = httpServletRequest;

			_resourceBundle = ResourceBundleUtil.getBundle(
				httpServletRequest.getLocale(), getClass());
		}

		@Override
		public String getIcon() {
			return "books";
		}

		@Override
		public String getImageURL() {
			return null;
		}

		@Override
		public Date getModifiedDate() {
			return null;
		}

		@Override
		public String getPayload() {
			return JSONUtil.put(
				"className", Group.class.getName()
			).put(
				"classNameId", _portal.getClassNameId(Group.class.getName())
			).put(
				"classPK", _group.getGroupId()
			).put(
				"title", _getTitle(_resourceBundle.getLocale())
			).toString();
		}

		@Override
		public String getSubtitle(Locale locale) {
			return StringPool.BLANK;
		}

		@Override
		public String getTitle(Locale locale) {
			return _getTitle(locale);
		}

		@Override
		public long getUserId() {
			return _group.getCreatorUserId();
		}

		@Override
		public String getUserName() {
			try {
				User user = _userLocalService.getUser(
					_group.getCreatorUserId());

				return user.getFullName();
			}
			catch (PortalException portalException) {
				if (_log.isWarnEnabled()) {
					_log.warn(portalException);
				}
			}

			return StringPool.BLANK;
		}

		private String _getTitle(Locale locale) {
			try {
				return _group.getDescriptiveName(locale);
			}
			catch (Exception exception) {
				if (_log.isDebugEnabled()) {
					_log.debug(exception);
				}
			}

			return _group.getName(locale);
		}

		private final Group _group;
		private HttpServletRequest _httpServletRequest;
		private final ResourceBundle _resourceBundle;

	}

	private class DepotGroupSelectorViewDescriptor
		implements ItemSelectorViewDescriptor<Group> {

		public DepotGroupSelectorViewDescriptor(
			DepotGroupItemSelectorCriterion depotGroupItemSelectorCriterion,
			HttpServletRequest httpServletRequest, PortletURL portletURL) {

			_depotGroupItemSelectorCriterion = depotGroupItemSelectorCriterion;
			_httpServletRequest = httpServletRequest;
			_portletURL = portletURL;
		}

		@Override
		public ItemDescriptor getItemDescriptor(Group group) {
			return new DepotGroupItemDescriptor(group, _httpServletRequest);
		}

		@Override
		public ItemSelectorReturnType getItemSelectorReturnType() {
			return new GroupItemSelectorReturnType();
		}

		@Override
		public String[] getOrderByKeys() {
			return new String[] {"title", "display-date"};
		}

		@Override
		public SearchContainer<Group> getSearchContainer() {
			try {
				PortletRequest portletRequest =
					(PortletRequest)_httpServletRequest.getAttribute(
						JavaConstants.JAKARTA_PORTLET_REQUEST);

				return _depotAdminGroupSearchProvider.getGroupSearch(
					_depotGroupItemSelectorCriterion, portletRequest,
					_portletURL);
			}
			catch (PortalException portalException) {
				return ReflectionUtil.throwException(portalException);
			}
		}

		@Override
		public boolean isShowBreadcrumb() {
			return false;
		}

		@Override
		public boolean isShowSearch() {
			return true;
		}

		private final DepotGroupItemSelectorCriterion
			_depotGroupItemSelectorCriterion;
		private HttpServletRequest _httpServletRequest;
		private final PortletURL _portletURL;

	}

}