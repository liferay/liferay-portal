/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.users.admin.web.internal.frontend.taglib.servlet.taglib;

import com.liferay.frontend.taglib.servlet.taglib.ScreenNavigationEntry;
import com.liferay.frontend.taglib.servlet.taglib.util.JSPRenderer;
import com.liferay.item.selector.ItemSelector;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Organization;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.UserConstants;
import com.liferay.portal.kernel.portlet.PortletURLFactoryUtil;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.service.OrganizationService;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.users.admin.constants.UserScreenNavigationEntryConstants;
import com.liferay.users.admin.constants.UsersAdminPortletKeys;
import com.liferay.users.admin.web.internal.constants.UsersAdminWebKeys;
import com.liferay.users.admin.web.internal.display.context.OrganizationScreenNavigationDisplayContext;

import jakarta.portlet.PortletRequest;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

import java.util.Locale;
import java.util.function.BiFunction;

/**
 * @author Drew Brokke
 */
public class OrganizationScreenNavigationEntry
	implements ScreenNavigationEntry<Organization> {

	public static Builder builder() {
		return new Builder();
	}

	@Override
	public String getCategoryKey() {
		return _categoryKey;
	}

	@Override
	public String getEntryKey() {
		return _entryKey;
	}

	@Override
	public String getLabel(Locale locale) {
		return LanguageUtil.get(locale, _entryKey);
	}

	@Override
	public String getScreenNavigationKey() {
		return UserScreenNavigationEntryConstants.
			SCREEN_NAVIGATION_KEY_ORGANIZATIONS;
	}

	@Override
	public boolean isVisible(User user, Organization organization) {
		return _visibleBiFunction.apply(user, organization);
	}

	@Override
	public void render(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws IOException {

		httpServletRequest.setAttribute(
			ItemSelector.class.getName(), _itemSelector);

		OrganizationScreenNavigationDisplayContext
			organizationScreenNavigationDisplayContext =
				new OrganizationScreenNavigationDisplayContext();

		organizationScreenNavigationDisplayContext.setActionName(
			_mvcActionCommandName);

		String backURL = ParamUtil.getString(httpServletRequest, "backURL");

		if (Validator.isNull(backURL)) {
			backURL = PortletURLBuilder.create(
				PortletURLFactoryUtil.create(
					httpServletRequest, UsersAdminPortletKeys.USERS_ADMIN,
					PortletRequest.RENDER_PHASE)
			).setParameter(
				"screenNavigationCategoryKey",
				UserScreenNavigationEntryConstants.CATEGORY_KEY_ORGANIZATIONS
			).setParameter(
				"usersListView", UserConstants.LIST_VIEW_FLAT_ORGANIZATIONS
			).buildString();
		}

		organizationScreenNavigationDisplayContext.setBackURL(backURL);

		String redirect = ParamUtil.getString(httpServletRequest, "redirect");

		if (Validator.isNull(redirect)) {
			redirect = PortletURLBuilder.create(
				PortletURLFactoryUtil.create(
					httpServletRequest, UsersAdminPortletKeys.USERS_ADMIN,
					PortletRequest.RENDER_PHASE)
			).setMVCRenderCommandName(
				"/users_admin/edit_organization"
			).setBackURL(
				backURL
			).setParameter(
				"organizationId",
				ParamUtil.getString(httpServletRequest, "organizationId")
			).setParameter(
				"screenNavigationCategoryKey",
				ParamUtil.getString(
					httpServletRequest, "screenNavigationCategoryKey",
					UserScreenNavigationEntryConstants.CATEGORY_KEY_GENERAL)
			).setParameter(
				"screenNavigationEntryKey",
				ParamUtil.getString(
					httpServletRequest, "screenNavigationEntryKey")
			).buildString();
		}

		organizationScreenNavigationDisplayContext.setRedirect(redirect);

		organizationScreenNavigationDisplayContext.setFormLabel(
			getLabel(httpServletRequest.getLocale()));
		organizationScreenNavigationDisplayContext.setJspPath(_jspPath);

		long organizationId = ParamUtil.getLong(
			httpServletRequest, "organizationId");

		Organization organization = null;

		try {
			organization = _organizationService.fetchOrganization(
				organizationId);
		}
		catch (PortalException portalException) {
			if (_log.isDebugEnabled()) {
				_log.debug(portalException);
			}
		}

		organizationScreenNavigationDisplayContext.setOrganization(
			organization);
		organizationScreenNavigationDisplayContext.setOrganizationId(
			organizationId);
		organizationScreenNavigationDisplayContext.setShowControls(
			_showControls);
		organizationScreenNavigationDisplayContext.setShowTitle(_showTitle);

		httpServletRequest.setAttribute(
			UsersAdminWebKeys.ORGANIZATION_SCREEN_NAVIGATION_DISPLAY_CONTEXT,
			organizationScreenNavigationDisplayContext);

		_jspRenderer.renderJSP(
			httpServletRequest, httpServletResponse,
			"/edit_organization_navigation.jsp");
	}

	public static class Builder {

		public OrganizationScreenNavigationEntry build() {
			return new OrganizationScreenNavigationEntry(
				_jspRenderer, _organizationService, _entryKey, _categoryKey,
				_itemSelector, _jspPath, _mvcActionCommandName, _showControls,
				_showTitle, _visibleBiFunction);
		}

		public Builder categoryKey(String categoryKey) {
			_categoryKey = categoryKey;

			return this;
		}

		public Builder entryKey(String entryKey) {
			_entryKey = entryKey;

			return this;
		}

		public Builder itemSelector(ItemSelector itemSelector) {
			_itemSelector = itemSelector;

			return this;
		}

		public Builder jspPath(String jspPath) {
			_jspPath = jspPath;

			return this;
		}

		public Builder jspRenderer(JSPRenderer jspRenderer) {
			_jspRenderer = jspRenderer;

			return this;
		}

		public Builder mvcActionCommandName(String mvcActionCommandName) {
			_mvcActionCommandName = mvcActionCommandName;

			return this;
		}

		public Builder organizationService(
			OrganizationService organizationService) {

			_organizationService = organizationService;

			return this;
		}

		public Builder showControls(boolean showControls) {
			_showControls = showControls;

			return this;
		}

		public Builder showTitle(boolean showTitle) {
			_showTitle = showTitle;

			return this;
		}

		public Builder visibleBiFunction(
			BiFunction<User, Organization, Boolean> visibleBiFunction) {

			_visibleBiFunction = visibleBiFunction;

			return this;
		}

		private Builder() {
		}

		private String _categoryKey;
		private String _entryKey;
		private ItemSelector _itemSelector;
		private String _jspPath;
		private JSPRenderer _jspRenderer;
		private String _mvcActionCommandName;
		private OrganizationService _organizationService;
		private boolean _showControls = true;
		private boolean _showTitle = true;

		private BiFunction<User, Organization, Boolean> _visibleBiFunction =
			(user, organization) -> {
				if (organization == null) {
					return false;
				}

				return true;
			};

	}

	private OrganizationScreenNavigationEntry(
		JSPRenderer jspRenderer, OrganizationService organizationService,
		String entryKey, String categoryKey, ItemSelector itemSelector,
		String jspPath, String mvcActionCommandName, boolean showControls,
		boolean showTitle,
		BiFunction<User, Organization, Boolean> visibleBiFunction) {

		_jspRenderer = jspRenderer;
		_organizationService = organizationService;
		_entryKey = entryKey;
		_categoryKey = categoryKey;
		_itemSelector = itemSelector;
		_jspPath = jspPath;
		_mvcActionCommandName = mvcActionCommandName;
		_showControls = showControls;
		_showTitle = showTitle;
		_visibleBiFunction = visibleBiFunction;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		OrganizationScreenNavigationEntry.class);

	private final String _categoryKey;
	private final String _entryKey;
	private final ItemSelector _itemSelector;
	private final String _jspPath;
	private final JSPRenderer _jspRenderer;
	private final String _mvcActionCommandName;
	private final OrganizationService _organizationService;
	private final boolean _showControls;
	private final boolean _showTitle;
	private final BiFunction<User, Organization, Boolean> _visibleBiFunction;

}