/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.price.list.service.impl;

import com.liferay.commerce.price.list.model.CommercePriceEntry;
import com.liferay.commerce.price.list.model.CommercePriceList;
import com.liferay.commerce.price.list.service.base.CommercePriceEntryServiceBaseImpl;
import com.liferay.commerce.price.list.util.comparator.CommercePriceEntryUOMCreateDateComparator;
import com.liferay.commerce.product.model.CPDefinition;
import com.liferay.commerce.product.model.CPInstance;
import com.liferay.commerce.product.service.CPInstanceService;
import com.liferay.portal.aop.AopService;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.search.BaseModelSearchResult;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.math.BigDecimal;

import java.util.Calendar;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Marco Leo
 * @author Alessio Antonio Rendina
 * @author Zoltán Takács
 */
@Component(
	property = {
		"json.web.service.context.name=commerce",
		"json.web.service.context.path=CommercePriceEntry"
	},
	service = AopService.class
)
public class CommercePriceEntryServiceImpl
	extends CommercePriceEntryServiceBaseImpl {

	@Override
	public CommercePriceEntry addCommercePriceEntry(
			String externalReferenceCode, long cpInstanceId,
			long commercePriceListId, BigDecimal price,
			boolean priceOnApplication, BigDecimal promoPrice,
			String unitOfMeasureKey, ServiceContext serviceContext)
		throws PortalException {

		_commercePriceListModelResourcePermission.check(
			getPermissionChecker(), commercePriceListId, ActionKeys.UPDATE);

		CPInstance cpInstance = _cpInstanceService.getCPInstance(cpInstanceId);

		CPDefinition cpDefinition = cpInstance.getCPDefinition();

		return commercePriceEntryLocalService.addCommercePriceEntry(
			externalReferenceCode, cpDefinition.getCProductId(),
			cpInstance.getCPInstanceUuid(), commercePriceListId, price,
			priceOnApplication, promoPrice, unitOfMeasureKey, serviceContext);
	}

	@Override
	public CommercePriceEntry addCommercePriceEntry(
			String externalReferenceCode, long cProductId,
			String cpInstanceUuid, long commercePriceListId, BigDecimal price,
			boolean discountDiscovery, BigDecimal discountLevel1,
			BigDecimal discountLevel2, BigDecimal discountLevel3,
			BigDecimal discountLevel4, int displayDateMonth, int displayDateDay,
			int displayDateYear, int displayDateHour, int displayDateMinute,
			int expirationDateMonth, int expirationDateDay,
			int expirationDateYear, int expirationDateHour,
			int expirationDateMinute, boolean neverExpire,
			String unitOfMeasureKey, ServiceContext serviceContext)
		throws PortalException {

		_commercePriceListModelResourcePermission.check(
			getPermissionChecker(), commercePriceListId, ActionKeys.UPDATE);

		return commercePriceEntryLocalService.addCommercePriceEntry(
			externalReferenceCode, cProductId, cpInstanceUuid,
			commercePriceListId, discountDiscovery, discountLevel1,
			discountLevel2, discountLevel3, discountLevel4, displayDateMonth,
			displayDateDay, displayDateYear, displayDateHour, displayDateMinute,
			expirationDateMonth, expirationDateDay, expirationDateYear,
			expirationDateHour, expirationDateMinute, neverExpire, price, false,
			null, unitOfMeasureKey, serviceContext);
	}

	@Override
	public CommercePriceEntry addOrUpdateCommercePriceEntry(
			String externalReferenceCode, long commercePriceEntryId,
			long cProductId, String cpInstanceUuid, long commercePriceListId,
			BigDecimal price, BigDecimal promoPrice,
			String skuExternalReferenceCode, String unitOfMeasureKey,
			ServiceContext serviceContext)
		throws PortalException {

		_commercePriceListModelResourcePermission.check(
			getPermissionChecker(), commercePriceListId, ActionKeys.UPDATE);

		Calendar calendar = new GregorianCalendar();

		return commercePriceEntryLocalService.addOrUpdateCommercePriceEntry(
			externalReferenceCode, commercePriceEntryId, cProductId,
			cpInstanceUuid, commercePriceListId, true, null, null, null, null,
			calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH),
			calendar.get(Calendar.YEAR), calendar.get(Calendar.HOUR),
			calendar.get(Calendar.MINUTE), 0, 0, 0, 0, 0, true, price, false,
			promoPrice, skuExternalReferenceCode, unitOfMeasureKey,
			serviceContext);
	}

	@Override
	public CommercePriceEntry addOrUpdateCommercePriceEntry(
			String externalReferenceCode, long commercePriceEntryId,
			long cProductId, String cpInstanceUuid, long commercePriceListId,
			boolean discountDiscovery, BigDecimal discountLevel1,
			BigDecimal discountLevel2, BigDecimal discountLevel3,
			BigDecimal discountLevel4, int displayDateMonth, int displayDateDay,
			int displayDateYear, int displayDateHour, int displayDateMinute,
			int expirationDateMonth, int expirationDateDay,
			int expirationDateYear, int expirationDateHour,
			int expirationDateMinute, boolean neverExpire, BigDecimal price,
			boolean priceOnApplication, String skuExternalReferenceCode,
			String unitOfMeasureKey, ServiceContext serviceContext)
		throws PortalException {

		_commercePriceListModelResourcePermission.check(
			getPermissionChecker(), commercePriceListId, ActionKeys.UPDATE);

		return commercePriceEntryLocalService.addOrUpdateCommercePriceEntry(
			externalReferenceCode, commercePriceEntryId, cProductId,
			cpInstanceUuid, commercePriceListId, discountDiscovery,
			discountLevel1, discountLevel2, discountLevel3, discountLevel4,
			displayDateMonth, displayDateDay, displayDateYear, displayDateHour,
			displayDateMinute, expirationDateMonth, expirationDateDay,
			expirationDateYear, expirationDateHour, expirationDateMinute,
			neverExpire, price, priceOnApplication, null,
			skuExternalReferenceCode, unitOfMeasureKey, serviceContext);
	}

	@Override
	public void deleteCommercePriceEntry(long commercePriceEntryId)
		throws PortalException {

		CommercePriceEntry commercePriceEntry =
			commercePriceEntryPersistence.findByPrimaryKey(
				commercePriceEntryId);

		_commercePriceListModelResourcePermission.check(
			getPermissionChecker(), commercePriceEntry.getCommercePriceListId(),
			ActionKeys.UPDATE);

		commercePriceEntryLocalService.deleteCommercePriceEntry(
			commercePriceEntry);
	}

	@Override
	public CommercePriceEntry fetchCommercePriceEntry(long commercePriceEntryId)
		throws PortalException {

		CommercePriceEntry commercePriceEntry =
			commercePriceEntryPersistence.fetchByPrimaryKey(
				commercePriceEntryId);

		if (commercePriceEntry != null) {
			_commercePriceListModelResourcePermission.check(
				getPermissionChecker(),
				commercePriceEntry.getCommercePriceListId(), ActionKeys.VIEW);
		}

		return commercePriceEntry;
	}

	@Override
	public CommercePriceEntry fetchCommercePriceEntryByExternalReferenceCode(
			String externalReferenceCode, long companyId)
		throws PortalException {

		CommercePriceEntry commercePriceEntry =
			commercePriceEntryLocalService.
				fetchCommercePriceEntryByExternalReferenceCode(
					externalReferenceCode, companyId);

		if (commercePriceEntry != null) {
			_commercePriceListModelResourcePermission.check(
				getPermissionChecker(),
				commercePriceEntry.getCommercePriceListId(), ActionKeys.VIEW);
		}

		return commercePriceEntry;
	}

	@Override
	public List<CommercePriceEntry> getCommercePriceEntries(
			long commercePriceListId, int start, int end)
		throws PortalException {

		_commercePriceListModelResourcePermission.check(
			getPermissionChecker(), commercePriceListId, ActionKeys.VIEW);

		return commercePriceEntryLocalService.getCommercePriceEntries(
			commercePriceListId, start, end);
	}

	@Override
	public List<CommercePriceEntry> getCommercePriceEntries(
			long commercePriceListId, int start, int end,
			OrderByComparator<CommercePriceEntry> orderByComparator)
		throws PortalException {

		_commercePriceListModelResourcePermission.check(
			getPermissionChecker(), commercePriceListId, ActionKeys.VIEW);

		return commercePriceEntryPersistence.findByCommercePriceListId(
			commercePriceListId, start, end, orderByComparator);
	}

	@Override
	public int getCommercePriceEntriesCount(long commercePriceListId)
		throws PortalException {

		_commercePriceListModelResourcePermission.check(
			getPermissionChecker(), commercePriceListId, ActionKeys.VIEW);

		return commercePriceEntryPersistence.countByCommercePriceListId(
			commercePriceListId);
	}

	@Override
	public CommercePriceEntry getCommercePriceEntry(long commercePriceEntryId)
		throws PortalException {

		CommercePriceEntry commercePriceEntry =
			commercePriceEntryPersistence.findByPrimaryKey(
				commercePriceEntryId);

		_commercePriceListModelResourcePermission.check(
			getPermissionChecker(), commercePriceEntry.getCommercePriceListId(),
			ActionKeys.VIEW);

		return commercePriceEntry;
	}

	@Override
	public CommercePriceEntry getInstanceBaseCommercePriceEntry(
		String cpInstanceUuid, String priceListType, String unitOfMeasureKey) {

		return commercePriceEntryLocalService.getInstanceBaseCommercePriceEntry(
			cpInstanceUuid, priceListType, unitOfMeasureKey);
	}

	@Override
	public List<CommercePriceEntry> getInstanceCommercePriceEntries(
			long cpInstanceId, int start, int end)
		throws PortalException {

		CPInstance cpInstance = _cpInstanceService.fetchCPInstance(
			cpInstanceId);

		if (cpInstance == null) {
			return Collections.emptyList();
		}

		List<CommercePriceEntry> commercePriceEntries =
			commercePriceEntryPersistence.findByCPInstanceUuid(
				cpInstance.getCPInstanceUuid(), start, end,
				CommercePriceEntryUOMCreateDateComparator.getInstance(true));

		Iterator<CommercePriceEntry> iterator = commercePriceEntries.iterator();

		while (iterator.hasNext()) {
			CommercePriceEntry commercePriceEntry = iterator.next();

			if (_commercePriceListModelResourcePermission.contains(
					getPermissionChecker(),
					commercePriceEntry.getCommercePriceListId(),
					ActionKeys.VIEW)) {

				continue;
			}

			iterator.remove();
		}

		return commercePriceEntries;
	}

	@Override
	public int getInstanceCommercePriceEntriesCount(long cpInstanceId)
		throws PortalException {

		CPInstance cpInstance = _cpInstanceService.fetchCPInstance(
			cpInstanceId);

		if (cpInstance == null) {
			return 0;
		}

		int count = 0;

		List<CommercePriceEntry> commercePriceEntries =
			commercePriceEntryPersistence.findByCPInstanceUuid(
				cpInstance.getCPInstanceUuid(), QueryUtil.ALL_POS,
				QueryUtil.ALL_POS,
				CommercePriceEntryUOMCreateDateComparator.getInstance(true));

		for (CommercePriceEntry commercePriceEntry : commercePriceEntries) {
			if (_commercePriceListModelResourcePermission.contains(
					getPermissionChecker(),
					commercePriceEntry.getCommercePriceListId(),
					ActionKeys.VIEW)) {

				count++;
			}
		}

		return count;
	}

	@Override
	public BaseModelSearchResult<CommercePriceEntry> searchCommercePriceEntries(
			long companyId, long commercePriceListId, String keywords,
			int start, int end, Sort sort)
		throws PortalException {

		_commercePriceListModelResourcePermission.check(
			getPermissionChecker(), commercePriceListId, ActionKeys.VIEW);

		return commercePriceEntryLocalService.searchCommercePriceEntries(
			companyId, commercePriceListId, keywords, start, end, sort);
	}

	@Override
	public int searchCommercePriceEntriesCount(
			long companyId, long commercePriceListId, String keywords)
		throws PortalException {

		_commercePriceListModelResourcePermission.check(
			getPermissionChecker(), commercePriceListId, ActionKeys.VIEW);

		return commercePriceEntryLocalService.searchCommercePriceEntriesCount(
			companyId, commercePriceListId, keywords);
	}

	@Override
	public CommercePriceEntry updateCommercePriceEntry(
			long commercePriceEntryId, boolean bulkPricing,
			boolean discountDiscovery, BigDecimal discountLevel1,
			BigDecimal discountLevel2, BigDecimal discountLevel3,
			BigDecimal discountLevel4, int displayDateMonth, int displayDateDay,
			int displayDateYear, int displayDateHour, int displayDateMinute,
			int expirationDateMonth, int expirationDateDay,
			int expirationDateYear, int expirationDateHour,
			int expirationDateMinute, boolean neverExpire, BigDecimal price,
			boolean priceOnApplication, String unitOfMeasureKey,
			ServiceContext serviceContext)
		throws PortalException {

		CommercePriceEntry commercePriceEntry =
			commercePriceEntryPersistence.findByPrimaryKey(
				commercePriceEntryId);

		_commercePriceListModelResourcePermission.check(
			getPermissionChecker(), commercePriceEntry.getCommercePriceListId(),
			ActionKeys.UPDATE);

		return commercePriceEntryLocalService.updateCommercePriceEntry(
			commercePriceEntryId, bulkPricing, discountDiscovery,
			discountLevel1, discountLevel2, discountLevel3, discountLevel4,
			displayDateMonth, displayDateDay, displayDateYear, displayDateHour,
			displayDateMinute, expirationDateMonth, expirationDateDay,
			expirationDateYear, expirationDateHour, expirationDateMinute,
			neverExpire, price, priceOnApplication, null, unitOfMeasureKey,
			serviceContext);
	}

	@Override
	public CommercePriceEntry updateExternalReferenceCode(
			String externalReferenceCode, CommercePriceEntry commercePriceEntry)
		throws PortalException {

		_commercePriceListModelResourcePermission.check(
			getPermissionChecker(), commercePriceEntry.getCommercePriceListId(),
			ActionKeys.UPDATE);

		return commercePriceEntryLocalService.updateExternalReferenceCode(
			externalReferenceCode, commercePriceEntry);
	}

	@Override
	public CommercePriceEntry updatePricingInfo(
			long commercePriceEntryId, boolean bulkPricing, BigDecimal price,
			boolean priceOnApplication, BigDecimal promoPrice,
			String unitOfMeasureKey, ServiceContext serviceContext)
		throws PortalException {

		CommercePriceEntry commercePriceEntry =
			commercePriceEntryPersistence.findByPrimaryKey(
				commercePriceEntryId);

		_commercePriceListModelResourcePermission.check(
			getPermissionChecker(), commercePriceEntry.getCommercePriceListId(),
			ActionKeys.UPDATE);

		return commercePriceEntryLocalService.updatePricingInfo(
			commercePriceEntryId, bulkPricing, price, priceOnApplication,
			promoPrice, unitOfMeasureKey, serviceContext);
	}

	@Reference(
		target = "(model.class.name=com.liferay.commerce.price.list.model.CommercePriceList)"
	)
	private ModelResourcePermission<CommercePriceList>
		_commercePriceListModelResourcePermission;

	@Reference
	private CPInstanceService _cpInstanceService;

}