/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.machine.learning.forecast.alert.service.impl;

import com.liferay.account.model.AccountEntry;
import com.liferay.account.service.AccountEntryLocalService;
import com.liferay.commerce.helper.CommerceAccountHelper;
import com.liferay.commerce.machine.learning.forecast.alert.constants.CommerceMLForecastAlertActionKeys;
import com.liferay.commerce.machine.learning.forecast.alert.constants.CommerceMLForecastAlertConstants;
import com.liferay.commerce.machine.learning.forecast.alert.model.CommerceMLForecastAlertEntry;
import com.liferay.commerce.machine.learning.forecast.alert.service.base.CommerceMLForecastAlertEntryServiceBaseImpl;
import com.liferay.commerce.product.constants.CommerceChannelConstants;
import com.liferay.portal.aop.AopService;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.GroupConstants;
import com.liferay.portal.kernel.security.permission.resource.PortletResourcePermission;
import com.liferay.portal.kernel.util.ListUtil;

import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Riccardo Ferrari
 */
@Component(
	property = {
		"json.web.service.context.name=commerce",
		"json.web.service.context.path=CommerceMLForecastAlertEntry"
	},
	service = AopService.class
)
public class CommerceMLForecastAlertEntryServiceImpl
	extends CommerceMLForecastAlertEntryServiceBaseImpl {

	@Override
	public List<CommerceMLForecastAlertEntry>
			getAboveThresholdCommerceMLForecastAlertEntries(
				long companyId, long userId, int status, double relativeChange,
				int start, int end)
		throws PortalException {

		_portletResourcePermission.check(
			getPermissionChecker(), GroupConstants.DEFAULT_LIVE_GROUP_ID,
			CommerceMLForecastAlertActionKeys.VIEW_ALERTS);

		long[] accountEntryIds = _getUserAccountEntryIds(userId);

		return commerceMLForecastAlertEntryPersistence.findByC_C_GtRc_S(
			companyId, accountEntryIds, relativeChange, status, start, end);
	}

	@Override
	public int getAboveThresholdCommerceMLForecastAlertEntriesCount(
			long companyId, long userId, int status, double relativeChange)
		throws PortalException {

		_portletResourcePermission.check(
			getPermissionChecker(), GroupConstants.DEFAULT_LIVE_GROUP_ID,
			CommerceMLForecastAlertActionKeys.VIEW_ALERTS);

		long[] accountEntryIds = _getUserAccountEntryIds(userId);

		return commerceMLForecastAlertEntryPersistence.countByC_C_GtRc_S(
			companyId, accountEntryIds, relativeChange, status);
	}

	@Override
	public List<CommerceMLForecastAlertEntry>
			getBelowThresholdCommerceMLForecastAlertEntries(
				long companyId, long userId, int status, double relativeChange,
				int start, int end)
		throws PortalException {

		_portletResourcePermission.check(
			getPermissionChecker(), GroupConstants.DEFAULT_LIVE_GROUP_ID,
			CommerceMLForecastAlertActionKeys.VIEW_ALERTS);

		long[] accountEntryIds = _getUserAccountEntryIds(userId);

		return commerceMLForecastAlertEntryPersistence.findByC_C_LtRc_S(
			companyId, accountEntryIds, relativeChange, status, start, end);
	}

	@Override
	public int getBelowThresholdCommerceMLForecastAlertEntriesCount(
			long companyId, long userId, int status, double relativeChange)
		throws PortalException {

		_portletResourcePermission.check(
			getPermissionChecker(), GroupConstants.DEFAULT_LIVE_GROUP_ID,
			CommerceMLForecastAlertActionKeys.VIEW_ALERTS);

		long[] accountEntryIds = _getUserAccountEntryIds(userId);

		return commerceMLForecastAlertEntryPersistence.countByC_C_LtRc_S(
			companyId, accountEntryIds, relativeChange, status);
	}

	@Override
	public List<CommerceMLForecastAlertEntry> getCommerceMLForecastAlertEntries(
			long companyId, long userId, int status, int start, int end)
		throws PortalException {

		_portletResourcePermission.check(
			getPermissionChecker(), GroupConstants.DEFAULT_LIVE_GROUP_ID,
			CommerceMLForecastAlertActionKeys.VIEW_ALERTS);

		long[] accountEntryIds = _getUserAccountEntryIds(userId);

		return commerceMLForecastAlertEntryPersistence.findByC_C_S(
			companyId, accountEntryIds, status, start, end);
	}

	@Override
	public int getCommerceMLForecastAlertEntriesCount(
			long companyId, long userId, int status)
		throws PortalException {

		_portletResourcePermission.check(
			getPermissionChecker(), GroupConstants.DEFAULT_LIVE_GROUP_ID,
			CommerceMLForecastAlertActionKeys.VIEW_ALERTS);

		long[] accountEntryIds = _getUserAccountEntryIds(userId);

		return commerceMLForecastAlertEntryPersistence.countByC_C_S(
			companyId, accountEntryIds, status);
	}

	@Override
	public CommerceMLForecastAlertEntry updateStatus(
			long commerceMLForecastAlertEntryId, int status)
		throws PortalException {

		_portletResourcePermission.check(
			getPermissionChecker(), GroupConstants.DEFAULT_LIVE_GROUP_ID,
			CommerceMLForecastAlertActionKeys.MANAGE_ALERT_STATUS);

		return commerceMLForecastAlertEntryLocalService.updateStatus(
			getUserId(), commerceMLForecastAlertEntryId, status);
	}

	private long[] _getUserAccountEntryIds(long userId) throws PortalException {
		List<AccountEntry> accountEntries =
			_accountEntryLocalService.getUserAccountEntries(
				userId, null, null,
				_commerceAccountHelper.toAccountEntryTypes(
					CommerceChannelConstants.SITE_TYPE_B2X),
				QueryUtil.ALL_POS, QueryUtil.ALL_POS);

		return ListUtil.toLongArray(
			accountEntries, AccountEntry::getAccountEntryId);
	}

	@Reference
	private AccountEntryLocalService _accountEntryLocalService;

	@Reference
	private CommerceAccountHelper _commerceAccountHelper;

	@Reference(
		target = "(resource.name=" + CommerceMLForecastAlertConstants.RESOURCE_NAME + ")"
	)
	private PortletResourcePermission _portletResourcePermission;

}