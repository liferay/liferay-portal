/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.machine.learning.forecast.alert.web.internal.display.context;

import com.liferay.account.model.AccountEntry;
import com.liferay.account.service.AccountEntryLocalService;
import com.liferay.commerce.machine.learning.forecast.alert.constants.CommerceMLForecastAlertActionKeys;
import com.liferay.commerce.machine.learning.forecast.alert.constants.CommerceMLForecastAlertConstants;
import com.liferay.commerce.machine.learning.forecast.alert.model.CommerceMLForecastAlertEntry;
import com.liferay.commerce.machine.learning.forecast.alert.service.CommerceMLForecastAlertEntryService;
import com.liferay.commerce.machine.learning.forecast.alert.web.internal.display.context.helper.CommerceMLForecastAlertEntryRequestHelper;
import com.liferay.portal.kernel.dao.search.SearchContainer;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.GroupConstants;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.security.permission.resource.PortletResourcePermission;

import jakarta.portlet.PortletURL;
import jakarta.portlet.RenderRequest;

/**
 * @author Riccardo Ferrari
 */
public class CommerceMLForecastAlertEntryListDisplayContext {

	public CommerceMLForecastAlertEntryListDisplayContext(
		AccountEntryLocalService accountEntryLocalService,
		CommerceMLForecastAlertEntryService commerceMLForecastAlertEntryService,
		PortletResourcePermission portletResourcePermission,
		RenderRequest renderRequest) {

		_accountEntryLocalService = accountEntryLocalService;
		_commerceMLForecastAlertEntryService =
			commerceMLForecastAlertEntryService;
		_portletResourcePermission = portletResourcePermission;

		_commerceMLForecastAlertEntryRequestHelper =
			new CommerceMLForecastAlertEntryRequestHelper(renderRequest);
	}

	public AccountEntry getAccountEntry(long accountEntryId) {
		try {
			return _accountEntryLocalService.getAccountEntry(accountEntryId);
		}
		catch (PortalException portalException) {
			_log.error(portalException);

			return null;
		}
	}

	public PortletURL getPortletURL() {
		LiferayPortletResponse liferayPortletResponse =
			_commerceMLForecastAlertEntryRequestHelper.
				getLiferayPortletResponse();

		return liferayPortletResponse.createRenderURL();
	}

	public SearchContainer<CommerceMLForecastAlertEntry> getSearchContainer()
		throws PortalException {

		if (_searchContainer != null) {
			return _searchContainer;
		}

		_searchContainer = new SearchContainer<>(
			_commerceMLForecastAlertEntryRequestHelper.
				getLiferayPortletRequest(),
			getPortletURL(), null,
			"there-are-no-forecast-alert-entries-to-display");

		_searchContainer.setResultsAndTotal(
			() ->
				_commerceMLForecastAlertEntryService.
					getBelowThresholdCommerceMLForecastAlertEntries(
						_commerceMLForecastAlertEntryRequestHelper.
							getCompanyId(),
						_commerceMLForecastAlertEntryRequestHelper.getUserId(),
						CommerceMLForecastAlertConstants.STATUS_NEW, 0.0,
						_searchContainer.getStart(), _searchContainer.getEnd()),
			_commerceMLForecastAlertEntryService.
				getBelowThresholdCommerceMLForecastAlertEntriesCount(
					_commerceMLForecastAlertEntryRequestHelper.getCompanyId(),
					_commerceMLForecastAlertEntryRequestHelper.getUserId(),
					CommerceMLForecastAlertConstants.STATUS_NEW, 0.0));

		return _searchContainer;
	}

	public boolean hasUpdatePermission() {
		return _portletResourcePermission.contains(
			_commerceMLForecastAlertEntryRequestHelper.getPermissionChecker(),
			GroupConstants.DEFAULT_LIVE_GROUP_ID,
			CommerceMLForecastAlertActionKeys.MANAGE_ALERT_STATUS);
	}

	public boolean hasViewPermission() {
		return _portletResourcePermission.contains(
			_commerceMLForecastAlertEntryRequestHelper.getPermissionChecker(),
			GroupConstants.DEFAULT_LIVE_GROUP_ID,
			CommerceMLForecastAlertActionKeys.VIEW_ALERTS);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		CommerceMLForecastAlertEntryListDisplayContext.class);

	private final AccountEntryLocalService _accountEntryLocalService;
	private final CommerceMLForecastAlertEntryRequestHelper
		_commerceMLForecastAlertEntryRequestHelper;
	private final CommerceMLForecastAlertEntryService
		_commerceMLForecastAlertEntryService;
	private final PortletResourcePermission _portletResourcePermission;
	private SearchContainer<CommerceMLForecastAlertEntry> _searchContainer;

}