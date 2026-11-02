/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.price.list.test.util;

import com.liferay.commerce.price.list.model.CommercePriceEntry;
import com.liferay.commerce.price.list.model.CommercePriceList;
import com.liferay.commerce.price.list.model.CommerceTierPriceEntry;
import com.liferay.commerce.price.list.service.CommercePriceEntryLocalServiceUtil;
import com.liferay.commerce.price.list.service.CommercePriceListLocalServiceUtil;
import com.liferay.commerce.price.list.service.CommerceTierPriceEntryLocalServiceUtil;
import com.liferay.commerce.test.util.price.list.CommerceTierPriceEntryTestUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.UserLocalServiceUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.util.CalendarFactoryUtil;
import com.liferay.portal.kernel.workflow.WorkflowConstants;

import java.math.BigDecimal;

import java.util.Calendar;

/**
 * @author Riccardo Alberti
 */
public class CommercePriceEntryTestUtil {

	public static CommercePriceEntry addCommercePriceEntry(
			String externalReferenceCode, long cpProductId,
			String cpInstanceUuid, long commercePriceListId, BigDecimal price)
		throws PortalException {

		CommercePriceList commercePriceList =
			CommercePriceListLocalServiceUtil.getCommercePriceList(
				commercePriceListId);

		return CommercePriceEntryLocalServiceUtil.addCommercePriceEntry(
			externalReferenceCode, cpProductId, cpInstanceUuid,
			commercePriceListId, price, false, BigDecimal.ZERO, null,
			ServiceContextTestUtil.getServiceContext(
				commercePriceList.getGroupId()));
	}

	public static CommercePriceEntry addCommercePriceEntry(
			String externalReferenceCode, long cpProductId,
			String cpInstanceUuid, long commercePriceListId, BigDecimal price,
			boolean discountDiscovery, BigDecimal discountLevel1,
			BigDecimal discountLevel2, BigDecimal discountLevel3,
			BigDecimal discountLevel4, boolean publish, boolean neverExpire)
		throws PortalException {

		CommercePriceList commercePriceList =
			CommercePriceListLocalServiceUtil.getCommercePriceList(
				commercePriceListId);

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(
				commercePriceList.getGroupId());

		User user = UserLocalServiceUtil.getGuestUser(
			serviceContext.getCompanyId());

		Calendar calendar = CalendarFactoryUtil.getCalendar(user.getTimeZone());

		if (publish) {
			serviceContext.setWorkflowAction(WorkflowConstants.ACTION_PUBLISH);
		}

		return CommercePriceEntryLocalServiceUtil.addCommercePriceEntry(
			externalReferenceCode, cpProductId, cpInstanceUuid,
			commercePriceListId, discountDiscovery, discountLevel1,
			discountLevel2, discountLevel3, discountLevel4,
			calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH),
			calendar.get(Calendar.YEAR), calendar.get(Calendar.HOUR_OF_DAY),
			calendar.get(Calendar.MINUTE), calendar.get(Calendar.MONTH),
			calendar.get(Calendar.DAY_OF_MONTH), calendar.get(Calendar.YEAR),
			calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE),
			neverExpire, price, false, null, null, serviceContext);
	}

	public static CommercePriceEntry addCommercePriceEntry(
			String externalReferenceCode, long cpProductId,
			String cpInstanceUuid, long commercePriceListId, BigDecimal price,
			String unitOfMeasureKey)
		throws PortalException {

		CommercePriceList commercePriceList =
			CommercePriceListLocalServiceUtil.getCommercePriceList(
				commercePriceListId);

		return CommercePriceEntryLocalServiceUtil.addCommercePriceEntry(
			externalReferenceCode, cpProductId, cpInstanceUuid,
			commercePriceListId, price, false, BigDecimal.ZERO,
			unitOfMeasureKey,
			ServiceContextTestUtil.getServiceContext(
				commercePriceList.getGroupId()));
	}

	public static CommerceTierPriceEntry addCommerceTierPriceEntry(
			long commercePriceEntryId, BigDecimal price, BigDecimal minQuantity,
			boolean bulkPricing)
		throws PortalException {

		CommercePriceEntry commercePriceEntry =
			CommercePriceEntryLocalServiceUtil.getCommercePriceEntry(
				commercePriceEntryId);

		CommercePriceList commercePriceList =
			commercePriceEntry.getCommercePriceList();

		return CommerceTierPriceEntryTestUtil.addCommerceTierPriceEntry(
			commercePriceEntryId, price, null, bulkPricing, minQuantity,
			ServiceContextTestUtil.getServiceContext(
				commercePriceList.getGroupId()));
	}

	public static CommerceTierPriceEntry addCommerceTierPriceEntry(
			long commercePriceEntryId, String externalReferenceCode,
			BigDecimal price, BigDecimal minQuantity, boolean bulkPricing,
			boolean discountDiscovery, BigDecimal discountLevel1,
			BigDecimal discountLevel2, BigDecimal discountLevel3,
			BigDecimal discountLevel4, boolean publish, boolean neverExpire)
		throws PortalException {

		CommercePriceEntry commercePriceEntry =
			CommercePriceEntryLocalServiceUtil.getCommercePriceEntry(
				commercePriceEntryId);

		CommercePriceList commercePriceList =
			commercePriceEntry.getCommercePriceList();

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(
				commercePriceList.getGroupId());

		User user = UserLocalServiceUtil.getGuestUser(
			serviceContext.getCompanyId());

		Calendar calendar = CalendarFactoryUtil.getCalendar(user.getTimeZone());

		if (publish) {
			serviceContext.setWorkflowAction(WorkflowConstants.ACTION_PUBLISH);
		}

		return CommerceTierPriceEntryLocalServiceUtil.addCommerceTierPriceEntry(
			externalReferenceCode, commercePriceEntryId, price, minQuantity,
			bulkPricing, discountDiscovery, discountLevel1, discountLevel2,
			discountLevel3, discountLevel4, calendar.get(Calendar.MONTH),
			calendar.get(Calendar.DAY_OF_MONTH), calendar.get(Calendar.YEAR),
			calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE),
			calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH),
			calendar.get(Calendar.YEAR), calendar.get(Calendar.HOUR_OF_DAY),
			calendar.get(Calendar.MINUTE), neverExpire, serviceContext);
	}

}