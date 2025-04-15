/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.account.admin.web.internal.display.context;

import com.liferay.account.admin.web.internal.display.AccountUserDisplay;
import com.liferay.account.configuration.AccountEntryEmailDomainsConfiguration;
import com.liferay.frontend.taglib.clay.servlet.taglib.display.context.SearchContainerManagementToolbarDisplayContext;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.CreationMenu;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.CreationMenuBuilder;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.LabelItem;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.LabelItemListBuilder;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.configuration.module.configuration.ConfigurationProviderUtil;
import com.liferay.portal.kernel.dao.search.SearchContainer;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.module.configuration.ConfigurationException;
import com.liferay.portal.kernel.portlet.LiferayPortletRequest;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.PortalUtil;

import jakarta.servlet.http.HttpServletRequest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * @author Pei-Jung Lan
 */
public class SelectAccountUsersManagementToolbarDisplayContext
	extends SearchContainerManagementToolbarDisplayContext {

	public SelectAccountUsersManagementToolbarDisplayContext(
		HttpServletRequest httpServletRequest,
		LiferayPortletRequest liferayPortletRequest,
		LiferayPortletResponse liferayPortletResponse,
		SearchContainer<AccountUserDisplay> searchContainer,
		SelectAccountUsersDisplayContext selectAccountUsersDisplayContext) {

		super(
			httpServletRequest, liferayPortletRequest, liferayPortletResponse,
			searchContainer);

		_selectAccountUsersDisplayContext = selectAccountUsersDisplayContext;
	}

	@Override
	public Map<String, Object> getAdditionalProps() {
		return HashMapBuilder.<String, Object>put(
			"addAccountEntryUserURL",
			PortletURLBuilder.createRenderURL(
				liferayPortletResponse
			).setMVCRenderCommandName(
				"/account_admin/add_account_user"
			).setRedirect(
				ParamUtil.getString(httpServletRequest, "redirect")
			).setParameter(
				"accountEntryId",
				_selectAccountUsersDisplayContext.getAccountEntryId()
			).buildString()
		).build();
	}

	@Override
	public String getClearResultsURL() {
		return PortletURLBuilder.create(
			getPortletURL()
		).setKeywords(
			StringPool.BLANK
		).setNavigation(
			(String)null
		).buildString();
	}

	@Override
	public CreationMenu getCreationMenu() {
		if (!_selectAccountUsersDisplayContext.isShowCreateButton()) {
			return null;
		}

		return CreationMenuBuilder.addPrimaryDropdownItem(
			dropdownItem -> {
				dropdownItem.putData("action", "addAccountEntryUser");
				dropdownItem.setLabel(
					LanguageUtil.get(httpServletRequest, "new-user"));
			}
		).build();
	}

	@Override
	public List<LabelItem> getFilterLabelItems() {
		return LabelItemListBuilder.add(
			() -> {
				String navigation = getNavigation();

				if (navigation.equals("account-users") ||
					navigation.equals("no-assigned-account")) {

					return true;
				}

				return false;
			},
			labelItem -> {
				labelItem.putData(
					"removeLabelURL",
					PortletURLBuilder.create(
						getPortletURL()
					).setNavigation(
						(String)null
					).buildString());

				labelItem.setCloseable(true);
				labelItem.setLabel(
					String.format(
						"%s: %s",
						LanguageUtil.get(httpServletRequest, "filter-by"),
						LanguageUtil.get(httpServletRequest, getNavigation())));
			}
		).build();
	}

	@Override
	public String getNavigation() {
		return ParamUtil.getString(
			liferayPortletRequest, getNavigationParam(),
			ArrayUtil.isEmpty(getNavigationKeys()) ? "all-users" :
				getNavigationKeys()[0]);
	}

	@Override
	public Boolean isSelectable() {
		return !_selectAccountUsersDisplayContext.isSingleSelect();
	}

	@Override
	public Boolean isShowCreationMenu() {
		return _selectAccountUsersDisplayContext.isShowCreateButton();
	}

	@Override
	protected String[] getNavigationKeys() {
		if (!_selectAccountUsersDisplayContext.isShowFilter()) {
			return new String[0];
		}

		List<String> navigationKeys = new ArrayList<>(
			Arrays.asList("all-users", "account-users", "no-assigned-account"));

		try {
			AccountEntryEmailDomainsConfiguration
				accountEntryEmailDomainsConfiguration =
					ConfigurationProviderUtil.getCompanyConfiguration(
						AccountEntryEmailDomainsConfiguration.class,
						PortalUtil.getCompanyId(liferayPortletRequest));

			if (accountEntryEmailDomainsConfiguration.
					enableEmailDomainValidation()) {

				navigationKeys.add(0, "valid-domain-users");
			}
		}
		catch (ConfigurationException configurationException) {
			if (_log.isDebugEnabled()) {
				_log.debug(configurationException);
			}
		}

		return ArrayUtil.toStringArray(navigationKeys);
	}

	@Override
	protected String getOrderByCol() {
		return ParamUtil.getString(
			liferayPortletRequest, getOrderByColParam(), "last-name");
	}

	@Override
	protected String[] getOrderByKeys() {
		return new String[] {"first-name", "last-name", "email-address"};
	}

	private static final Log _log = LogFactoryUtil.getLog(
		SelectAccountUsersManagementToolbarDisplayContext.class);

	private final SelectAccountUsersDisplayContext
		_selectAccountUsersDisplayContext;

}