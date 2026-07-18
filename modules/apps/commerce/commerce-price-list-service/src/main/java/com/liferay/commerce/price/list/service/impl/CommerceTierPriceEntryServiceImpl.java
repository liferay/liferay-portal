/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.price.list.service.impl;

import com.liferay.commerce.price.list.model.CommercePriceEntry;
import com.liferay.commerce.price.list.model.CommercePriceList;
import com.liferay.commerce.price.list.model.CommerceTierPriceEntry;
import com.liferay.commerce.price.list.service.CommercePriceEntryLocalService;
import com.liferay.commerce.price.list.service.base.CommerceTierPriceEntryServiceBaseImpl;
import com.liferay.commerce.price.list.service.persistence.CommercePriceEntryPersistence;
import com.liferay.portal.aop.AopService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.search.BaseModelSearchResult;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.Validator;

import java.math.BigDecimal;

import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Alessio Antonio Rendina
 * @author Zoltán Takács
 */
@Component(
	property = {
		"json.web.service.context.name=commerce",
		"json.web.service.context.path=CommerceTierPriceEntry"
	},
	service = AopService.class
)
public class CommerceTierPriceEntryServiceImpl
	extends CommerceTierPriceEntryServiceBaseImpl {

	@Override
	public CommerceTierPriceEntry addCommerceTierPriceEntry(
			long commercePriceEntryId, BigDecimal price, BigDecimal promoPrice,
			BigDecimal minQuantity, ServiceContext serviceContext)
		throws PortalException {

		return addCommerceTierPriceEntry(
			null, commercePriceEntryId, price, promoPrice, minQuantity,
			serviceContext);
	}

	@Override
	public CommerceTierPriceEntry addCommerceTierPriceEntry(
			String externalReferenceCode, long commercePriceEntryId,
			BigDecimal price, BigDecimal promoPrice, BigDecimal minQuantity,
			ServiceContext serviceContext)
		throws PortalException {

		CommercePriceEntry commercePriceEntry =
			_commercePriceEntryPersistence.findByPrimaryKey(
				commercePriceEntryId);

		if (commercePriceEntry != null) {
			_commercePriceListModelResourcePermission.check(
				getPermissionChecker(),
				commercePriceEntry.getCommercePriceListId(), ActionKeys.UPDATE);
		}

		return commerceTierPriceEntryLocalService.addCommerceTierPriceEntry(
			externalReferenceCode, commercePriceEntryId, price, promoPrice,
			commercePriceEntry.isBulkPricing(), minQuantity, serviceContext);
	}

	@Override
	public CommerceTierPriceEntry addCommerceTierPriceEntry(
			String externalReferenceCode, long commercePriceEntryId,
			BigDecimal price, BigDecimal minQuantity, boolean bulkPricing,
			boolean discountDiscovery, BigDecimal discountLevel1,
			BigDecimal discountLevel2, BigDecimal discountLevel3,
			BigDecimal discountLevel4, int displayDateMonth, int displayDateDay,
			int displayDateYear, int displayDateHour, int displayDateMinute,
			int expirationDateMonth, int expirationDateDay,
			int expirationDateYear, int expirationDateHour,
			int expirationDateMinute, boolean neverExpire,
			ServiceContext serviceContext)
		throws PortalException {

		CommercePriceEntry commercePriceEntry =
			_commercePriceEntryPersistence.findByPrimaryKey(
				commercePriceEntryId);

		_commercePriceListModelResourcePermission.check(
			getPermissionChecker(), commercePriceEntry.getCommercePriceListId(),
			ActionKeys.UPDATE);

		return commerceTierPriceEntryLocalService.addCommerceTierPriceEntry(
			externalReferenceCode, commercePriceEntryId, price, minQuantity,
			bulkPricing, discountDiscovery, discountLevel1, discountLevel2,
			discountLevel3, discountLevel4, displayDateMonth, displayDateDay,
			displayDateYear, displayDateHour, displayDateMinute,
			expirationDateMonth, expirationDateDay, expirationDateYear,
			expirationDateHour, expirationDateMinute, neverExpire,
			serviceContext);
	}

	@Override
	public CommerceTierPriceEntry addOrUpdateCommerceTierPriceEntry(
			String externalReferenceCode, long commerceTierPriceEntryId,
			long commercePriceEntryId, BigDecimal price, BigDecimal promoPrice,
			BigDecimal minQuantity, String priceEntryExternalReferenceCode,
			ServiceContext serviceContext)
		throws PortalException {

		CommercePriceEntry commercePriceEntry =
			_commercePriceEntryPersistence.fetchByPrimaryKey(
				commercePriceEntryId);

		if ((commercePriceEntry == null) &&
			Validator.isNotNull(priceEntryExternalReferenceCode)) {

			commercePriceEntry =
				_commercePriceEntryLocalService.
					fetchCommercePriceEntryByExternalReferenceCode(
						priceEntryExternalReferenceCode,
						serviceContext.getCompanyId());
		}

		if (commercePriceEntry != null) {
			_commercePriceListModelResourcePermission.check(
				getPermissionChecker(),
				commercePriceEntry.getCommercePriceListId(), ActionKeys.UPDATE);
		}

		return commerceTierPriceEntryLocalService.
			addOrUpdateCommerceTierPriceEntry(
				externalReferenceCode, commerceTierPriceEntryId,
				commercePriceEntryId, price, promoPrice, minQuantity,
				priceEntryExternalReferenceCode, serviceContext);
	}

	@Override
	public CommerceTierPriceEntry addOrUpdateCommerceTierPriceEntry(
			String externalReferenceCode, long commerceTierPriceEntryId,
			long commercePriceEntryId, BigDecimal price, BigDecimal minQuantity,
			boolean bulkPricing, boolean discountDiscovery,
			BigDecimal discountLevel1, BigDecimal discountLevel2,
			BigDecimal discountLevel3, BigDecimal discountLevel4,
			int displayDateMonth, int displayDateDay, int displayDateYear,
			int displayDateHour, int displayDateMinute, int expirationDateMonth,
			int expirationDateDay, int expirationDateYear,
			int expirationDateHour, int expirationDateMinute,
			boolean neverExpire, String priceEntryExternalReferenceCode,
			ServiceContext serviceContext)
		throws PortalException {

		CommercePriceEntry commercePriceEntry =
			_commercePriceEntryPersistence.fetchByPrimaryKey(
				commercePriceEntryId);

		if ((commercePriceEntry == null) &&
			Validator.isNotNull(priceEntryExternalReferenceCode)) {

			commercePriceEntry =
				_commercePriceEntryLocalService.
					fetchCommercePriceEntryByExternalReferenceCode(
						priceEntryExternalReferenceCode,
						serviceContext.getCompanyId());
		}

		if (commercePriceEntry != null) {
			_commercePriceListModelResourcePermission.check(
				getPermissionChecker(),
				commercePriceEntry.getCommercePriceListId(), ActionKeys.UPDATE);
		}

		return commerceTierPriceEntryLocalService.
			addOrUpdateCommerceTierPriceEntry(
				externalReferenceCode, commerceTierPriceEntryId,
				commercePriceEntryId, price, minQuantity, bulkPricing,
				discountDiscovery, discountLevel1, discountLevel2,
				discountLevel3, discountLevel4, displayDateMonth,
				displayDateDay, displayDateYear, displayDateHour,
				displayDateMinute, expirationDateMonth, expirationDateDay,
				expirationDateYear, expirationDateHour, expirationDateMinute,
				neverExpire, priceEntryExternalReferenceCode, serviceContext);
	}

	@Override
	public void deleteCommerceTierPriceEntry(long commerceTierPriceEntryId)
		throws PortalException {

		CommerceTierPriceEntry commerceTierPriceEntry =
			commerceTierPriceEntryPersistence.findByPrimaryKey(
				commerceTierPriceEntryId);

		CommercePriceEntry commercePriceEntry =
			commerceTierPriceEntry.getCommercePriceEntry();

		_commercePriceListModelResourcePermission.check(
			getPermissionChecker(), commercePriceEntry.getCommercePriceListId(),
			ActionKeys.UPDATE);

		commerceTierPriceEntryLocalService.deleteCommerceTierPriceEntry(
			commerceTierPriceEntryId);
	}

	@Override
	public CommerceTierPriceEntry fetchCommerceTierPriceEntry(
			long commerceTierPriceEntryId)
		throws PortalException {

		CommerceTierPriceEntry commerceTierPriceEntry =
			commerceTierPriceEntryPersistence.fetchByPrimaryKey(
				commerceTierPriceEntryId);

		if (commerceTierPriceEntry != null) {
			CommercePriceEntry commercePriceEntry =
				commerceTierPriceEntry.getCommercePriceEntry();

			_commercePriceListModelResourcePermission.check(
				getPermissionChecker(),
				commercePriceEntry.getCommercePriceListId(), ActionKeys.VIEW);
		}

		return commerceTierPriceEntry;
	}

	@Override
	public CommerceTierPriceEntry
			fetchCommerceTierPriceEntryByExternalReferenceCode(
				String externalReferenceCode, long companyId)
		throws PortalException {

		CommerceTierPriceEntry commerceTierPriceEntry =
			commerceTierPriceEntryLocalService.
				fetchCommerceTierPriceEntryByExternalReferenceCode(
					externalReferenceCode, companyId);

		if (commerceTierPriceEntry != null) {
			CommercePriceEntry commercePriceEntry =
				commerceTierPriceEntry.getCommercePriceEntry();

			_commercePriceListModelResourcePermission.check(
				getPermissionChecker(),
				commercePriceEntry.getCommercePriceListId(), ActionKeys.VIEW);
		}

		return commerceTierPriceEntry;
	}

	@Override
	public List<CommerceTierPriceEntry> getCommerceTierPriceEntries(
			long commercePriceEntryId, int start, int end)
		throws PortalException {

		CommercePriceEntry commercePriceEntry =
			_commercePriceEntryPersistence.findByPrimaryKey(
				commercePriceEntryId);

		_commercePriceListModelResourcePermission.check(
			getPermissionChecker(), commercePriceEntry.getCommercePriceListId(),
			ActionKeys.VIEW);

		return commerceTierPriceEntryPersistence.findByCommercePriceEntryId(
			commercePriceEntryId, start, end);
	}

	@Override
	public List<CommerceTierPriceEntry> getCommerceTierPriceEntries(
			long commercePriceEntryId, int start, int end,
			OrderByComparator<CommerceTierPriceEntry> orderByComparator)
		throws PortalException {

		CommercePriceEntry commercePriceEntry =
			_commercePriceEntryPersistence.findByPrimaryKey(
				commercePriceEntryId);

		_commercePriceListModelResourcePermission.check(
			getPermissionChecker(), commercePriceEntry.getCommercePriceListId(),
			ActionKeys.VIEW);

		return commerceTierPriceEntryPersistence.findByCommercePriceEntryId(
			commercePriceEntryId, start, end, orderByComparator);
	}

	@Override
	public int getCommerceTierPriceEntriesCount(long commercePriceEntryId)
		throws PortalException {

		CommercePriceEntry commercePriceEntry =
			_commercePriceEntryPersistence.findByPrimaryKey(
				commercePriceEntryId);

		_commercePriceListModelResourcePermission.check(
			getPermissionChecker(), commercePriceEntry.getCommercePriceListId(),
			ActionKeys.VIEW);

		return commerceTierPriceEntryLocalService.
			getCommerceTierPriceEntriesCount(commercePriceEntryId);
	}

	/**
	 * @deprecated As of Athanasius (7.3.x)
	 */
	@Deprecated
	@Override
	public int getCommerceTierPriceEntriesCountByCompanyId(long companyId)
		throws PortalException {

		throw new UnsupportedOperationException();
	}

	@Override
	public CommerceTierPriceEntry getCommerceTierPriceEntry(
			long commerceTierPriceEntryId)
		throws PortalException {

		CommerceTierPriceEntry commerceTierPriceEntry =
			commerceTierPriceEntryPersistence.findByPrimaryKey(
				commerceTierPriceEntryId);

		CommercePriceEntry commercePriceEntry =
			commerceTierPriceEntry.getCommercePriceEntry();

		_commercePriceListModelResourcePermission.check(
			getPermissionChecker(), commercePriceEntry.getCommercePriceListId(),
			ActionKeys.VIEW);

		return commerceTierPriceEntry;
	}

	@Override
	public BaseModelSearchResult<CommerceTierPriceEntry>
			searchCommerceTierPriceEntries(
				long companyId, long commercePriceEntryId, String keywords,
				int start, int end, Sort sort)
		throws PortalException {

		CommercePriceEntry commercePriceEntry =
			_commercePriceEntryPersistence.fetchByPrimaryKey(
				commercePriceEntryId);

		if (commercePriceEntry != null) {
			_commercePriceListModelResourcePermission.check(
				getPermissionChecker(),
				commercePriceEntry.getCommercePriceListId(), ActionKeys.VIEW);
		}

		return commerceTierPriceEntryLocalService.
			searchCommerceTierPriceEntries(
				companyId, commercePriceEntryId, keywords, start, end, sort);
	}

	@Override
	public int searchCommerceTierPriceEntriesCount(
			long companyId, long commercePriceEntryId, String keywords)
		throws PortalException {

		CommercePriceEntry commercePriceEntry =
			_commercePriceEntryPersistence.findByPrimaryKey(
				commercePriceEntryId);

		if (commercePriceEntry != null) {
			_commercePriceListModelResourcePermission.check(
				getPermissionChecker(),
				commercePriceEntry.getCommercePriceListId(), ActionKeys.UPDATE);
		}

		return _commercePriceEntryLocalService.searchCommercePriceEntriesCount(
			companyId, commercePriceEntryId, keywords);
	}

	@Override
	public CommerceTierPriceEntry updateCommerceTierPriceEntry(
			long commerceTierPriceEntryId, BigDecimal price,
			BigDecimal promoPrice, BigDecimal minQuantity,
			ServiceContext serviceContext)
		throws PortalException {

		CommerceTierPriceEntry commerceTierPriceEntry =
			commerceTierPriceEntryPersistence.findByPrimaryKey(
				commerceTierPriceEntryId);

		CommercePriceEntry commercePriceEntry =
			commerceTierPriceEntry.getCommercePriceEntry();

		_commercePriceListModelResourcePermission.check(
			getPermissionChecker(), commercePriceEntry.getCommercePriceListId(),
			ActionKeys.UPDATE);

		return commerceTierPriceEntryLocalService.updateCommerceTierPriceEntry(
			commerceTierPriceEntryId, price, promoPrice, minQuantity,
			commercePriceEntry.isBulkPricing(), serviceContext);
	}

	@Override
	public CommerceTierPriceEntry updateCommerceTierPriceEntry(
			long commerceTierPriceEntryId, BigDecimal price,
			BigDecimal minQuantity, boolean bulkPricing,
			boolean discountDiscovery, BigDecimal discountLevel1,
			BigDecimal discountLevel2, BigDecimal discountLevel3,
			BigDecimal discountLevel4, int displayDateMonth, int displayDateDay,
			int displayDateYear, int displayDateHour, int displayDateMinute,
			int expirationDateMonth, int expirationDateDay,
			int expirationDateYear, int expirationDateHour,
			int expirationDateMinute, boolean neverExpire,
			ServiceContext serviceContext)
		throws PortalException {

		CommerceTierPriceEntry commerceTierPriceEntry =
			commerceTierPriceEntryPersistence.findByPrimaryKey(
				commerceTierPriceEntryId);

		CommercePriceEntry commercePriceEntry =
			commerceTierPriceEntry.getCommercePriceEntry();

		_commercePriceListModelResourcePermission.check(
			getPermissionChecker(), commercePriceEntry.getCommercePriceListId(),
			ActionKeys.UPDATE);

		return commerceTierPriceEntryLocalService.updateCommerceTierPriceEntry(
			commerceTierPriceEntryId, price, minQuantity, bulkPricing,
			discountDiscovery, discountLevel1, discountLevel2, discountLevel3,
			discountLevel4, displayDateMonth, displayDateDay, displayDateYear,
			displayDateHour, displayDateMinute, expirationDateMonth,
			expirationDateDay, expirationDateYear, expirationDateHour,
			expirationDateMinute, neverExpire, serviceContext);
	}

	@Override
	public CommerceTierPriceEntry updateExternalReferenceCode(
			CommerceTierPriceEntry commerceTierPriceEntry,
			String externalReferenceCode)
		throws PortalException {

		CommercePriceEntry commercePriceEntry =
			commerceTierPriceEntry.getCommercePriceEntry();

		_commercePriceListModelResourcePermission.check(
			getPermissionChecker(), commercePriceEntry.getCommercePriceListId(),
			ActionKeys.UPDATE);

		return commerceTierPriceEntryLocalService.updateExternalReferenceCode(
			commerceTierPriceEntry, externalReferenceCode);
	}

	@Reference
	private CommercePriceEntryLocalService _commercePriceEntryLocalService;

	@Reference
	private CommercePriceEntryPersistence _commercePriceEntryPersistence;

	@Reference(
		target = "(model.class.name=com.liferay.commerce.price.list.model.CommercePriceList)"
	)
	private ModelResourcePermission<CommercePriceList>
		_commercePriceListModelResourcePermission;

}