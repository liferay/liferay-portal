/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.service.impl;

import com.liferay.osb.faro.model.FaroProjectUsage;
import com.liferay.osb.faro.service.base.FaroProjectUsageLocalServiceBaseImpl;
import com.liferay.portal.aop.AopService;
import com.liferay.portal.kernel.exception.PortalException;

import java.util.Date;

import org.osgi.service.component.annotations.Component;

/**
 * @author Marcos Martins
 */
@Component(
	property = "model.class.name=com.liferay.osb.faro.model.FaroProjectUsage",
	service = AopService.class
)
public class FaroProjectUsageLocalServiceImpl
	extends FaroProjectUsageLocalServiceBaseImpl {

	@Override
	public FaroProjectUsage addFaroProjectUsage(
		long companyId, long userId, long activeLogicalBytes,
		long anonymousIndividualsCount, long eventsCount, long faroProjectId,
		long knownIndividualsCount, long longTermLogicalBytes,
		String monthDateKey, long onDemandBytesBilled, long pageViewsCount,
		long reservationSlotMs, Date usageDate) {

		long faroProjectUsageId = counterLocalService.increment();

		FaroProjectUsage faroProjectUsage = faroProjectUsagePersistence.create(
			faroProjectUsageId);

		faroProjectUsage.setCompanyId(companyId);
		faroProjectUsage.setUserId(userId);

		long now = System.currentTimeMillis();

		faroProjectUsage.setCreateTime(now);
		faroProjectUsage.setModifiedTime(now);

		faroProjectUsage.setActiveLogicalBytes(activeLogicalBytes);
		faroProjectUsage.setAnonymousIndividualsCount(
			anonymousIndividualsCount);
		faroProjectUsage.setEventsCount(eventsCount);
		faroProjectUsage.setFaroProjectId(faroProjectId);
		faroProjectUsage.setKnownIndividualsCount(knownIndividualsCount);
		faroProjectUsage.setLongTermLogicalBytes(longTermLogicalBytes);
		faroProjectUsage.setMonthDateKey(monthDateKey);
		faroProjectUsage.setOnDemandBytesBilled(onDemandBytesBilled);
		faroProjectUsage.setPageViewsCount(pageViewsCount);
		faroProjectUsage.setReservationSlotMs(reservationSlotMs);
		faroProjectUsage.setUsageTime(usageDate.getTime());

		return faroProjectUsagePersistence.update(faroProjectUsage);
	}

	@Override
	public void deleteFaroProjectUsages() {
		faroProjectUsagePersistence.removeAll();
	}

	@Override
	public FaroProjectUsage fetchFaroProjectUsage(
		long faroProjectId, Date usageDate) {

		return faroProjectUsagePersistence.fetchByF_U(
			faroProjectId, usageDate.getTime());
	}

	@Override
	public FaroProjectUsage updateFaroProjectUsage(
			long faroProjectUsageId, long activeLogicalBytes,
			long anonymousIndividualsCount, long eventsCount,
			long knownIndividualsCount, long longTermLogicalBytes,
			long onDemandBytesBilled, long pageViewsCount,
			long reservationSlotMs)
		throws PortalException {

		FaroProjectUsage faroProjectUsage = getFaroProjectUsage(
			faroProjectUsageId);

		faroProjectUsage.setModifiedTime(System.currentTimeMillis());

		faroProjectUsage.setActiveLogicalBytes(activeLogicalBytes);
		faroProjectUsage.setAnonymousIndividualsCount(
			anonymousIndividualsCount);
		faroProjectUsage.setEventsCount(eventsCount);
		faroProjectUsage.setKnownIndividualsCount(knownIndividualsCount);
		faroProjectUsage.setLongTermLogicalBytes(longTermLogicalBytes);
		faroProjectUsage.setOnDemandBytesBilled(onDemandBytesBilled);
		faroProjectUsage.setPageViewsCount(pageViewsCount);
		faroProjectUsage.setReservationSlotMs(reservationSlotMs);

		return faroProjectUsagePersistence.update(faroProjectUsage);
	}

}