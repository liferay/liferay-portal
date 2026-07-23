/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.service.impl;

import com.liferay.osb.faro.model.FaroDataSourceUsage;
import com.liferay.osb.faro.service.base.FaroDataSourceUsageLocalServiceBaseImpl;
import com.liferay.portal.aop.AopService;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQueryFactoryUtil;
import com.liferay.portal.kernel.dao.orm.OrderFactoryUtil;
import com.liferay.portal.kernel.dao.orm.RestrictionsFactoryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.UserLocalService;

import java.util.Date;
import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Caio Pinheiro
 */
@Component(
	property = "model.class.name=com.liferay.osb.faro.model.FaroDataSourceUsage",
	service = AopService.class
)
public class FaroDataSourceUsageLocalServiceImpl
	extends FaroDataSourceUsageLocalServiceBaseImpl {

	@Override
	public FaroDataSourceUsage addFaroDataSourceUsage(
			long userId, long billableEventsCount, long dataSourceId,
			String dataSourceName, String dataSourceStatus, long faroProjectId,
			long knownIndividualsCount, Date usageDate)
		throws PortalException {

		FaroDataSourceUsage faroDataSourceUsage =
			faroDataSourceUsagePersistence.create(
				counterLocalService.increment());

		User user = _userLocalService.getUser(userId);

		faroDataSourceUsage.setCompanyId(user.getCompanyId());
		faroDataSourceUsage.setUserId(user.getUserId());

		long now = System.currentTimeMillis();

		faroDataSourceUsage.setCreateTime(now);
		faroDataSourceUsage.setModifiedTime(now);

		faroDataSourceUsage.setBillableEventsCount(billableEventsCount);
		faroDataSourceUsage.setDataSourceId(dataSourceId);
		faroDataSourceUsage.setDataSourceName(dataSourceName);
		faroDataSourceUsage.setDataSourceStatus(dataSourceStatus);
		faroDataSourceUsage.setFaroProjectId(faroProjectId);
		faroDataSourceUsage.setKnownIndividualsCount(knownIndividualsCount);
		faroDataSourceUsage.setUsageTime(usageDate.getTime());

		return faroDataSourceUsagePersistence.update(faroDataSourceUsage);
	}

	@Override
	public FaroDataSourceUsage addOrUpdateFaroDataSourceUsage(
			long userId, long billableEventsCount, long dataSourceId,
			String dataSourceName, String dataSourceStatus, long faroProjectId,
			long knownIndividualsCount, Date usageDate)
		throws PortalException {

		FaroDataSourceUsage faroDataSourceUsage = fetchFaroDataSourceUsage(
			dataSourceId, faroProjectId, usageDate);

		if (faroDataSourceUsage == null) {
			return addFaroDataSourceUsage(
				userId, billableEventsCount, dataSourceId, dataSourceName,
				dataSourceStatus, faroProjectId, knownIndividualsCount,
				usageDate);
		}

		return updateFaroDataSourceUsage(
			billableEventsCount, dataSourceName, dataSourceStatus,
			faroDataSourceUsage.getFaroDataSourceUsageId(),
			knownIndividualsCount);
	}

	@Override
	public FaroDataSourceUsage fetchFaroDataSourceUsage(
		long dataSourceId, long faroProjectId, Date usageDate) {

		return faroDataSourceUsagePersistence.fetchByF_D_U(
			dataSourceId, faroProjectId, usageDate.getTime());
	}

	@Override
	public List<FaroDataSourceUsage> getFaroDataSourceUsages(
		long faroProjectId, Date startDate, Date endDate) {

		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			FaroDataSourceUsage.class, getClassLoader());

		dynamicQuery.add(
			RestrictionsFactoryUtil.eq("faroProjectId", faroProjectId));
		dynamicQuery.add(
			RestrictionsFactoryUtil.between(
				"usageTime", startDate.getTime(), endDate.getTime()));

		dynamicQuery.addOrder(OrderFactoryUtil.asc("dataSourceId"));
		dynamicQuery.addOrder(OrderFactoryUtil.asc("usageTime"));

		return dynamicQuery(dynamicQuery);
	}

	@Override
	public FaroDataSourceUsage updateFaroDataSourceUsage(
			long billableEventsCount, String dataSourceName,
			String dataSourceStatus, long faroDataSourceUsageId,
			long knownIndividualsCount)
		throws PortalException {

		FaroDataSourceUsage faroDataSourceUsage = getFaroDataSourceUsage(
			faroDataSourceUsageId);

		faroDataSourceUsage.setBillableEventsCount(billableEventsCount);
		faroDataSourceUsage.setDataSourceName(dataSourceName);
		faroDataSourceUsage.setDataSourceStatus(dataSourceStatus);
		faroDataSourceUsage.setKnownIndividualsCount(knownIndividualsCount);
		faroDataSourceUsage.setModifiedTime(System.currentTimeMillis());

		return faroDataSourceUsagePersistence.update(faroDataSourceUsage);
	}

	@Reference
	private UserLocalService _userLocalService;

}