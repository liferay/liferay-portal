/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.machine.learning.forecast.alert.service.impl;

import com.liferay.commerce.machine.learning.forecast.alert.constants.CommerceMLForecastAlertConstants;
import com.liferay.commerce.machine.learning.forecast.alert.model.CommerceMLForecastAlertEntry;
import com.liferay.commerce.machine.learning.forecast.alert.service.base.CommerceMLForecastAlertEntryLocalServiceBaseImpl;
import com.liferay.portal.aop.AopService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.GroupConstants;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.search.Indexable;
import com.liferay.portal.kernel.search.IndexableType;
import com.liferay.portal.kernel.service.ResourceLocalService;
import com.liferay.portal.kernel.service.UserLocalService;

import java.util.Date;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Riccardo Ferrari
 */
@Component(
	property = "model.class.name=com.liferay.commerce.machine.learning.forecast.alert.model.CommerceMLForecastAlertEntry",
	service = AopService.class
)
public class CommerceMLForecastAlertEntryLocalServiceImpl
	extends CommerceMLForecastAlertEntryLocalServiceBaseImpl {

	@Indexable(type = IndexableType.REINDEX)
	@Override
	public CommerceMLForecastAlertEntry addOrUpdateCommerceMLForecastAlertEntry(
			long companyId, long userId, long commerceAccountId, Date timestamp,
			float actual, float forecast, float relativeChange)
		throws PortalException {

		CommerceMLForecastAlertEntry commerceMLForecastAlertEntry =
			commerceMLForecastAlertEntryPersistence.findByC_C_T(
				companyId, commerceAccountId, timestamp);

		User user = _userLocalService.getUser(userId);

		if (commerceMLForecastAlertEntry == null) {
			long commerceMLForecastAlertEntryId =
				counterLocalService.increment();

			commerceMLForecastAlertEntry =
				commerceMLForecastAlertEntryPersistence.create(
					commerceMLForecastAlertEntryId);

			commerceMLForecastAlertEntry.setCompanyId(companyId);
			commerceMLForecastAlertEntry.setUserId(userId);
			commerceMLForecastAlertEntry.setUserName(user.getFullName());
			commerceMLForecastAlertEntry.setCreateDate(new Date());
			commerceMLForecastAlertEntry.setCommerceAccountId(
				commerceAccountId);
			commerceMLForecastAlertEntry.setTimestamp(timestamp);
		}

		commerceMLForecastAlertEntry.setModifiedDate(new Date());
		commerceMLForecastAlertEntry.setActual(actual);
		commerceMLForecastAlertEntry.setForecast(forecast);
		commerceMLForecastAlertEntry.setRelativeChange(relativeChange);
		commerceMLForecastAlertEntry.setStatus(
			CommerceMLForecastAlertConstants.STATUS_NEW);

		commerceMLForecastAlertEntry =
			commerceMLForecastAlertEntryPersistence.update(
				commerceMLForecastAlertEntry);

		// Resources

		_resourceLocalService.addResources(
			user.getCompanyId(), GroupConstants.DEFAULT_LIVE_GROUP_ID,
			user.getUserId(), CommerceMLForecastAlertEntry.class.getName(),
			commerceMLForecastAlertEntry.getCommerceAccountId(), false, false,
			false);

		return commerceMLForecastAlertEntry;
	}

	@Override
	public CommerceMLForecastAlertEntry updateStatus(
			long userId, long commerceMLForecastAlertEntryId, int status)
		throws PortalException {

		User user = _userLocalService.getUser(userId);

		CommerceMLForecastAlertEntry commerceMLForecastAlertEntry =
			commerceMLForecastAlertEntryPersistence.findByPrimaryKey(
				commerceMLForecastAlertEntryId);

		commerceMLForecastAlertEntry.setUserId(userId);
		commerceMLForecastAlertEntry.setUserName(user.getFullName());
		commerceMLForecastAlertEntry.setModifiedDate(new Date());
		commerceMLForecastAlertEntry.setStatus(status);

		return commerceMLForecastAlertEntryPersistence.update(
			commerceMLForecastAlertEntry);
	}

	@Reference
	private ResourceLocalService _resourceLocalService;

	@Reference
	private UserLocalService _userLocalService;

}