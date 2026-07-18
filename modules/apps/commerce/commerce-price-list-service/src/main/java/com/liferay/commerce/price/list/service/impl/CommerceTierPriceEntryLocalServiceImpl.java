/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.price.list.service.impl;

import com.liferay.commerce.price.list.exception.CommercePriceEntryDisplayDateException;
import com.liferay.commerce.price.list.exception.CommercePriceEntryExpirationDateException;
import com.liferay.commerce.price.list.exception.CommerceTierPriceEntryDisplayDateException;
import com.liferay.commerce.price.list.exception.CommerceTierPriceEntryExpirationDateException;
import com.liferay.commerce.price.list.exception.CommerceTierPriceEntryMinQuantityException;
import com.liferay.commerce.price.list.exception.DuplicateCommerceTierPriceEntryException;
import com.liferay.commerce.price.list.exception.NoSuchPriceEntryException;
import com.liferay.commerce.price.list.exception.NoSuchTierPriceEntryException;
import com.liferay.commerce.price.list.model.CommercePriceEntry;
import com.liferay.commerce.price.list.model.CommercePriceList;
import com.liferay.commerce.price.list.model.CommerceTierPriceEntry;
import com.liferay.commerce.price.list.service.CommercePriceEntryLocalService;
import com.liferay.commerce.price.list.service.base.CommerceTierPriceEntryLocalServiceBaseImpl;
import com.liferay.commerce.price.list.service.persistence.CommercePriceEntryPersistence;
import com.liferay.commerce.price.list.util.comparator.CommerceTierPriceEntryMinQuantityComparator;
import com.liferay.commerce.product.model.CPInstance;
import com.liferay.commerce.product.model.CPInstanceUnitOfMeasure;
import com.liferay.commerce.product.service.CPInstanceLocalService;
import com.liferay.commerce.product.service.CPInstanceUnitOfMeasureLocalService;
import com.liferay.expando.kernel.service.ExpandoRowLocalService;
import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.aop.AopService;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.SystemEventConstants;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.search.BaseModelSearchResult;
import com.liferay.portal.kernel.search.Document;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.Hits;
import com.liferay.portal.kernel.search.Indexable;
import com.liferay.portal.kernel.search.IndexableType;
import com.liferay.portal.kernel.search.Indexer;
import com.liferay.portal.kernel.search.IndexerRegistryUtil;
import com.liferay.portal.kernel.search.QueryConfig;
import com.liferay.portal.kernel.search.SearchContext;
import com.liferay.portal.kernel.search.SearchException;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.systemevent.SystemEvent;
import com.liferay.portal.kernel.util.BigDecimalUtil;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.LinkedHashMapBuilder;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.kernel.workflow.WorkflowHandlerRegistryUtil;

import java.io.Serializable;

import java.math.BigDecimal;
import java.math.RoundingMode;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Alessio Antonio Rendina
 * @author Zoltán Takács
 */
@Component(
	property = "model.class.name=com.liferay.commerce.price.list.model.CommerceTierPriceEntry",
	service = AopService.class
)
public class CommerceTierPriceEntryLocalServiceImpl
	extends CommerceTierPriceEntryLocalServiceBaseImpl {

	@Override
	public CommerceTierPriceEntry addCommerceTierPriceEntry(
			long commercePriceEntryId, BigDecimal price, BigDecimal promoPrice,
			BigDecimal minQuantity, ServiceContext serviceContext)
		throws PortalException {

		return commerceTierPriceEntryLocalService.addCommerceTierPriceEntry(
			null, commercePriceEntryId, price, promoPrice, minQuantity,
			serviceContext);
	}

	@Override
	public CommerceTierPriceEntry addCommerceTierPriceEntry(
			long commercePriceEntryId, BigDecimal price, BigDecimal promoPrice,
			boolean bulkPricing, BigDecimal minQuantity,
			ServiceContext serviceContext)
		throws PortalException {

		return commerceTierPriceEntryLocalService.addCommerceTierPriceEntry(
			null, commercePriceEntryId, price, promoPrice, bulkPricing,
			minQuantity, serviceContext);
	}

	@Indexable(type = IndexableType.REINDEX)
	@Override
	public CommerceTierPriceEntry addCommerceTierPriceEntry(
			String externalReferenceCode, long commercePriceEntryId,
			BigDecimal price, BigDecimal promoPrice, BigDecimal minQuantity,
			boolean bulkPricing, boolean discountDiscovery,
			BigDecimal discountLevel1, BigDecimal discountLevel2,
			BigDecimal discountLevel3, BigDecimal discountLevel4,
			int displayDateMonth, int displayDateDay, int displayDateYear,
			int displayDateHour, int displayDateMinute, int expirationDateMonth,
			int expirationDateDay, int expirationDateYear,
			int expirationDateHour, int expirationDateMinute,
			boolean neverExpire, ServiceContext serviceContext)
		throws PortalException {

		// Commerce tier price entry

		User user = _userLocalService.getUser(serviceContext.getUserId());

		CommercePriceEntry commercePriceEntry =
			_commercePriceEntryPersistence.findByPrimaryKey(
				commercePriceEntryId);

		_validateMinQuantity(commercePriceEntry, minQuantity);
		_validateCommercePriceEntryId(
			0, commercePriceEntry.getCommercePriceEntryId(), minQuantity);

		Date expirationDate = null;
		Date date = new Date();

		Date displayDate = _portal.getDate(
			displayDateMonth, displayDateDay, displayDateYear, displayDateHour,
			displayDateMinute, user.getTimeZone(),
			CommerceTierPriceEntryDisplayDateException.class);

		if (!neverExpire) {
			expirationDate = _portal.getDate(
				expirationDateMonth, expirationDateDay, expirationDateYear,
				expirationDateHour, expirationDateMinute, user.getTimeZone(),
				CommerceTierPriceEntryExpirationDateException.class);
		}

		long commerceTierPriceEntryId = counterLocalService.increment();

		CommerceTierPriceEntry commerceTierPriceEntry =
			commerceTierPriceEntryPersistence.create(commerceTierPriceEntryId);

		commerceTierPriceEntry.setExternalReferenceCode(externalReferenceCode);
		commerceTierPriceEntry.setCompanyId(user.getCompanyId());
		commerceTierPriceEntry.setUserId(user.getUserId());
		commerceTierPriceEntry.setUserName(user.getFullName());
		commerceTierPriceEntry.setCommercePriceEntryId(commercePriceEntryId);
		commerceTierPriceEntry.setPrice(price);
		commerceTierPriceEntry.setPromoPrice(promoPrice);
		commerceTierPriceEntry.setDiscountDiscovery(discountDiscovery);
		commerceTierPriceEntry.setDiscountLevel1(discountLevel1);
		commerceTierPriceEntry.setDiscountLevel2(discountLevel2);
		commerceTierPriceEntry.setDiscountLevel3(discountLevel3);
		commerceTierPriceEntry.setDiscountLevel4(discountLevel4);
		commerceTierPriceEntry.setMinQuantity(
			_normalizeMinQuantity(commercePriceEntry, minQuantity));
		commerceTierPriceEntry.setDisplayDate(displayDate);

		if ((expirationDate == null) || expirationDate.after(date)) {
			commerceTierPriceEntry.setStatus(WorkflowConstants.STATUS_DRAFT);
		}
		else {
			commerceTierPriceEntry.setStatus(WorkflowConstants.STATUS_EXPIRED);
		}

		commerceTierPriceEntry.setExpirationDate(expirationDate);
		commerceTierPriceEntry.setStatusByUserId(user.getUserId());
		commerceTierPriceEntry.setStatusDate(
			serviceContext.getModifiedDate(date));
		commerceTierPriceEntry.setExpandoBridgeAttributes(serviceContext);

		commerceTierPriceEntry = commerceTierPriceEntryPersistence.update(
			commerceTierPriceEntry);

		// Commerce price entry

		_commercePriceEntryLocalService.setHasTierPrice(
			commercePriceEntryId, true, bulkPricing);

		return _startWorkflowInstance(
			user.getUserId(), commerceTierPriceEntry, serviceContext);
	}

	@Override
	public CommerceTierPriceEntry addCommerceTierPriceEntry(
			String externalReferenceCode, long commercePriceEntryId,
			BigDecimal price, BigDecimal promoPrice, BigDecimal minQuantity,
			ServiceContext serviceContext)
		throws PortalException {

		Calendar now = new GregorianCalendar();

		return commerceTierPriceEntryLocalService.addCommerceTierPriceEntry(
			externalReferenceCode, commercePriceEntryId, price, promoPrice,
			minQuantity, true, true, null, null, null, null,
			now.get(Calendar.MONTH), now.get(Calendar.DAY_OF_MONTH),
			now.get(Calendar.YEAR), now.get(Calendar.HOUR),
			now.get(Calendar.MINUTE), 0, 0, 0, 0, 0, true, serviceContext);
	}

	@Override
	public CommerceTierPriceEntry addCommerceTierPriceEntry(
			String externalReferenceCode, long commercePriceEntryId,
			BigDecimal price, BigDecimal promoPrice, boolean bulkPricing,
			BigDecimal minQuantity, ServiceContext serviceContext)
		throws PortalException {

		Calendar now = new GregorianCalendar();

		return commerceTierPriceEntryLocalService.addCommerceTierPriceEntry(
			externalReferenceCode, commercePriceEntryId, price, promoPrice,
			minQuantity, bulkPricing, true, null, null, null, null,
			now.get(Calendar.MONTH), now.get(Calendar.DAY_OF_MONTH),
			now.get(Calendar.YEAR), now.get(Calendar.HOUR),
			now.get(Calendar.MINUTE), 0, 0, 0, 0, 0, true, serviceContext);
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

		return commerceTierPriceEntryLocalService.addCommerceTierPriceEntry(
			externalReferenceCode, commercePriceEntryId, price, null,
			minQuantity, bulkPricing, discountDiscovery, discountLevel1,
			discountLevel2, discountLevel3, discountLevel4, displayDateMonth,
			displayDateDay, displayDateYear, displayDateHour, displayDateMinute,
			expirationDateMonth, expirationDateDay, expirationDateYear,
			expirationDateHour, expirationDateMinute, neverExpire,
			serviceContext);
	}

	@Override
	public CommerceTierPriceEntry addOrUpdateCommerceTierPriceEntry(
			String externalReferenceCode, long commerceTierPriceEntryId,
			long commercePriceEntryId, BigDecimal price, BigDecimal promoPrice,
			BigDecimal minQuantity, boolean bulkPricing,
			boolean discountDiscovery, BigDecimal discountLevel1,
			BigDecimal discountLevel2, BigDecimal discountLevel3,
			BigDecimal discountLevel4, int displayDateMonth, int displayDateDay,
			int displayDateYear, int displayDateHour, int displayDateMinute,
			int expirationDateMonth, int expirationDateDay,
			int expirationDateYear, int expirationDateHour,
			int expirationDateMinute, boolean neverExpire,
			String priceEntryExternalReferenceCode,
			ServiceContext serviceContext)
		throws PortalException {

		// Update

		if (commerceTierPriceEntryId > 0) {
			try {
				return commerceTierPriceEntryLocalService.
					updateCommerceTierPriceEntry(
						commerceTierPriceEntryId, price, promoPrice,
						minQuantity, bulkPricing, discountDiscovery,
						discountLevel1, discountLevel2, discountLevel3,
						discountLevel4, displayDateMonth, displayDateDay,
						displayDateYear, displayDateHour, displayDateMinute,
						expirationDateMonth, expirationDateDay,
						expirationDateYear, expirationDateHour,
						expirationDateMinute, neverExpire, serviceContext);
			}
			catch (NoSuchTierPriceEntryException
						noSuchTierPriceEntryException) {

				if (_log.isDebugEnabled()) {
					_log.debug(
						"Unable to find tier price entry with ID: " +
							commerceTierPriceEntryId,
						noSuchTierPriceEntryException);
				}
			}
		}

		if (Validator.isNotNull(externalReferenceCode)) {
			CommerceTierPriceEntry commerceTierPriceEntry =
				commerceTierPriceEntryPersistence.fetchByERC_C(
					externalReferenceCode, serviceContext.getCompanyId());

			if (commerceTierPriceEntry != null) {
				return commerceTierPriceEntryLocalService.
					updateCommerceTierPriceEntry(
						commerceTierPriceEntry.getCommerceTierPriceEntryId(),
						price, promoPrice, minQuantity, bulkPricing,
						discountDiscovery, discountLevel1, discountLevel2,
						discountLevel3, discountLevel4, displayDateMonth,
						displayDateDay, displayDateYear, displayDateHour,
						displayDateMinute, expirationDateMonth,
						expirationDateDay, expirationDateYear,
						expirationDateHour, expirationDateMinute, neverExpire,
						serviceContext);
			}
		}

		// Add

		if (commercePriceEntryId > 0) {
			CommercePriceEntry commercePriceEntry =
				_commercePriceEntryPersistence.findByPrimaryKey(
					commercePriceEntryId);

			_validateMinQuantity(commercePriceEntry, minQuantity);
			_validateCommercePriceEntryId(
				0L, commercePriceEntry.getCommercePriceEntryId(), minQuantity);

			return commerceTierPriceEntryLocalService.addCommerceTierPriceEntry(
				externalReferenceCode,
				commercePriceEntry.getCommercePriceEntryId(), price, promoPrice,
				minQuantity, bulkPricing, discountDiscovery, discountLevel1,
				discountLevel2, discountLevel3, discountLevel4,
				displayDateMonth, displayDateDay, displayDateYear,
				displayDateHour, displayDateMinute, expirationDateMonth,
				expirationDateDay, expirationDateYear, expirationDateHour,
				expirationDateMinute, neverExpire, serviceContext);
		}

		if (Validator.isNotNull(priceEntryExternalReferenceCode)) {
			CommercePriceEntry commercePriceEntry =
				_commercePriceEntryPersistence.findByERC_C(
					priceEntryExternalReferenceCode,
					serviceContext.getCompanyId());

			_validateMinQuantity(commercePriceEntry, minQuantity);
			_validateCommercePriceEntryId(
				0L, commercePriceEntry.getCommercePriceEntryId(), minQuantity);

			return commerceTierPriceEntryLocalService.addCommerceTierPriceEntry(
				externalReferenceCode,
				commercePriceEntry.getCommercePriceEntryId(), price, promoPrice,
				minQuantity, bulkPricing, discountDiscovery, discountLevel1,
				discountLevel2, discountLevel3, discountLevel4,
				displayDateMonth, displayDateDay, displayDateYear,
				displayDateHour, displayDateMinute, expirationDateMonth,
				expirationDateDay, expirationDateYear, expirationDateHour,
				expirationDateMinute, neverExpire, serviceContext);
		}

		throw new NoSuchPriceEntryException(
			StringBundler.concat(
				"{commercePriceEntryId=", commercePriceEntryId,
				", priceEntryExternalReferenceCode=",
				priceEntryExternalReferenceCode, CharPool.CLOSE_CURLY_BRACE));
	}

	/**
	 * This method is used to insert a new CommerceTierPriceEntry or update an
	 * existing one
	 *
	 * @param  externalReferenceCode - The external identifier code from a 3rd
	 *         party system to be able to locate the same entity in the portal
	 *         <b>Only</b> used when updating an entity; the first entity with a
	 *         matching reference code one will be updated
	 * @param  commerceTierPriceEntryId - <b>Only</b> used when updating an
	 *         entity; the matching one will be updated
	 * @param  commercePriceEntryId - <b>Only</b> used when adding a new entity
	 * @param  price
	 * @param  promoPrice
	 * @param  minQuantity
	 * @param  priceEntryExternalReferenceCode - <b>Only</b> used when adding a
	 *         new entity, similar as <code>commercePriceEntryId</code> but the
	 *         external identifier code from a 3rd party system. If
	 *         commercePriceEntryId is used, it doesn't have any effect,
	 *         otherwise it tries to fetch the CommercePriceEntry against the
	 *         external code reference
	 * @param  serviceContext
	 * @return CommerceTierPriceEntry
	 * @throws PortalException
	 */
	@Override
	public CommerceTierPriceEntry addOrUpdateCommerceTierPriceEntry(
			String externalReferenceCode, long commerceTierPriceEntryId,
			long commercePriceEntryId, BigDecimal price, BigDecimal promoPrice,
			BigDecimal minQuantity, String priceEntryExternalReferenceCode,
			ServiceContext serviceContext)
		throws PortalException {

		Calendar now = new GregorianCalendar();

		return commerceTierPriceEntryLocalService.
			addOrUpdateCommerceTierPriceEntry(
				externalReferenceCode, commerceTierPriceEntryId,
				commercePriceEntryId, price, promoPrice, minQuantity, true,
				true, null, null, null, null, now.get(Calendar.MONTH),
				now.get(Calendar.DAY_OF_MONTH), now.get(Calendar.YEAR),
				now.get(Calendar.HOUR), now.get(Calendar.MINUTE), 0, 0, 0, 0, 0,
				true, priceEntryExternalReferenceCode, serviceContext);
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

		return commerceTierPriceEntryLocalService.
			addOrUpdateCommerceTierPriceEntry(
				externalReferenceCode, commerceTierPriceEntryId,
				commercePriceEntryId, price, null, minQuantity, bulkPricing,
				discountDiscovery, discountLevel1, discountLevel2,
				discountLevel3, discountLevel4, displayDateMonth,
				displayDateDay, displayDateYear, displayDateHour,
				displayDateMinute, expirationDateMonth, expirationDateDay,
				expirationDateYear, expirationDateHour, expirationDateMinute,
				neverExpire, priceEntryExternalReferenceCode, serviceContext);
	}

	@Override
	public void checkCommerceTierPriceEntries() throws PortalException {
		_checkCommerceTierPriceEntriesByDisplayDate();
		_checkCommerceTierPriceEntriesByExpirationDate();
	}

	@Override
	public void deleteCommerceTierPriceEntries(long commercePriceEntryId)
		throws PortalException {

		List<CommerceTierPriceEntry> commerceTierPriceEntries =
			commerceTierPriceEntryPersistence.findByCommercePriceEntryId(
				commercePriceEntryId, QueryUtil.ALL_POS, QueryUtil.ALL_POS);

		for (CommerceTierPriceEntry commerceTierPriceEntry :
				commerceTierPriceEntries) {

			commerceTierPriceEntryLocalService.deleteCommerceTierPriceEntry(
				commerceTierPriceEntry);
		}
	}

	@Indexable(type = IndexableType.DELETE)
	@Override
	@SystemEvent(type = SystemEventConstants.TYPE_DELETE)
	public CommerceTierPriceEntry deleteCommerceTierPriceEntry(
			CommerceTierPriceEntry commerceTierPriceEntry)
		throws PortalException {

		// Commerce tier price entry

		commerceTierPriceEntryPersistence.remove(commerceTierPriceEntry);

		// Commerce price entries

		List<CommerceTierPriceEntry> commerceTierPriceEntries =
			commerceTierPriceEntryPersistence.findByCommercePriceEntryId(
				commerceTierPriceEntry.getCommercePriceEntryId(),
				QueryUtil.ALL_POS, QueryUtil.ALL_POS);

		if (commerceTierPriceEntries.isEmpty()) {
			_commercePriceEntryLocalService.setHasTierPrice(
				commerceTierPriceEntry.getCommercePriceEntryId(), false);
		}

		// Expando

		_expandoRowLocalService.deleteRows(
			commerceTierPriceEntry.getCommerceTierPriceEntryId());

		return commerceTierPriceEntry;
	}

	@Override
	public CommerceTierPriceEntry deleteCommerceTierPriceEntry(
			long commerceTierPriceEntryId)
		throws PortalException {

		CommerceTierPriceEntry commerceTierPriceEntry =
			commerceTierPriceEntryPersistence.findByPrimaryKey(
				commerceTierPriceEntryId);

		return commerceTierPriceEntryLocalService.deleteCommerceTierPriceEntry(
			commerceTierPriceEntry);
	}

	@Override
	public CommerceTierPriceEntry fetchClosestCommerceTierPriceEntry(
		long commercePriceEntryId, BigDecimal minQuantity) {

		return commerceTierPriceEntryPersistence.fetchByC_LteM_S_First(
			commercePriceEntryId, minQuantity,
			WorkflowConstants.STATUS_APPROVED,
			CommerceTierPriceEntryMinQuantityComparator.getInstance(false));
	}

	@Override
	public List<CommerceTierPriceEntry> getCommerceTierPriceEntries(
		long commercePriceEntryId, BigDecimal minQuantity) {

		return commerceTierPriceEntryPersistence.findByC_LteM_S(
			commercePriceEntryId, minQuantity,
			WorkflowConstants.STATUS_APPROVED, QueryUtil.ALL_POS,
			QueryUtil.ALL_POS,
			CommerceTierPriceEntryMinQuantityComparator.getInstance(true));
	}

	@Override
	public List<CommerceTierPriceEntry> getCommerceTierPriceEntries(
		long commercePriceEntryId, int status) {

		return commerceTierPriceEntryPersistence.findByC_S(
			commercePriceEntryId, status, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
			CommerceTierPriceEntryMinQuantityComparator.getInstance(true));
	}

	@Override
	public List<CommerceTierPriceEntry> getCommerceTierPriceEntries(
		long commercePriceEntryId, int start, int end) {

		return commerceTierPriceEntryPersistence.findByCommercePriceEntryId(
			commercePriceEntryId, start, end);
	}

	@Override
	public List<CommerceTierPriceEntry> getCommerceTierPriceEntries(
		long commercePriceEntryId, int start, int end,
		OrderByComparator<CommerceTierPriceEntry> orderByComparator) {

		return commerceTierPriceEntryPersistence.findByCommercePriceEntryId(
			commercePriceEntryId, start, end, orderByComparator);
	}

	@Override
	public int getCommerceTierPriceEntriesCount(long commercePriceEntryId) {
		return commerceTierPriceEntryPersistence.countByCommercePriceEntryId(
			commercePriceEntryId);
	}

	@Override
	public int getCommerceTierPriceEntriesCountByCompanyId(long companyId) {
		return commerceTierPriceEntryPersistence.countByCompanyId(companyId);
	}

	@Override
	public Hits search(SearchContext searchContext) {
		try {
			Indexer<CommerceTierPriceEntry> indexer =
				IndexerRegistryUtil.nullSafeGetIndexer(
					CommerceTierPriceEntry.class);

			return indexer.search(searchContext);
		}
		catch (Exception exception) {
			throw new SystemException(exception);
		}
	}

	@Override
	public BaseModelSearchResult<CommerceTierPriceEntry>
			searchCommerceTierPriceEntries(
				long companyId, long commercePriceEntryId, String keywords,
				int start, int end, Sort sort)
		throws PortalException {

		SearchContext searchContext = _buildSearchContext(
			companyId, commercePriceEntryId, keywords, start, end, sort);

		return _searchCommerceTierPriceEntries(searchContext);
	}

	@Override
	public int searchCommerceTierPriceEntriesCount(
			long companyId, long commercePriceEntryId, String keywords)
		throws PortalException {

		SearchContext searchContext = _buildSearchContext(
			companyId, commercePriceEntryId, keywords, QueryUtil.ALL_POS,
			QueryUtil.ALL_POS, null);

		return _searchCommerceTierPriceEntriesCount(searchContext);
	}

	@Indexable(type = IndexableType.REINDEX)
	@Override
	public CommerceTierPriceEntry updateCommerceTierPriceEntry(
			long commerceTierPriceEntryId, BigDecimal price,
			BigDecimal promoPrice, BigDecimal minQuantity, boolean bulkPricing,
			boolean discountDiscovery, BigDecimal discountLevel1,
			BigDecimal discountLevel2, BigDecimal discountLevel3,
			BigDecimal discountLevel4, int displayDateMonth, int displayDateDay,
			int displayDateYear, int displayDateHour, int displayDateMinute,
			int expirationDateMonth, int expirationDateDay,
			int expirationDateYear, int expirationDateHour,
			int expirationDateMinute, boolean neverExpire,
			ServiceContext serviceContext)
		throws PortalException {

		User user = _userLocalService.getUser(serviceContext.getUserId());

		CommerceTierPriceEntry commerceTierPriceEntry =
			commerceTierPriceEntryPersistence.findByPrimaryKey(
				commerceTierPriceEntryId);

		CommercePriceEntry commercePriceEntry =
			_commercePriceEntryPersistence.findByPrimaryKey(
				commerceTierPriceEntry.getCommercePriceEntryId());

		_validateMinQuantity(commercePriceEntry, minQuantity);
		_validateCommercePriceEntryId(
			commerceTierPriceEntryId,
			commercePriceEntry.getCommercePriceEntryId(), minQuantity);

		Date expirationDate = null;
		Date date = new Date();

		Date displayDate = _portal.getDate(
			displayDateMonth, displayDateDay, displayDateYear, displayDateHour,
			displayDateMinute, user.getTimeZone(),
			CommercePriceEntryDisplayDateException.class);

		if (!neverExpire) {
			expirationDate = _portal.getDate(
				expirationDateMonth, expirationDateDay, expirationDateYear,
				expirationDateHour, expirationDateMinute, user.getTimeZone(),
				CommercePriceEntryExpirationDateException.class);
		}

		commerceTierPriceEntry.setPrice(price);
		commerceTierPriceEntry.setPromoPrice(promoPrice);
		commerceTierPriceEntry.setDiscountDiscovery(discountDiscovery);
		commerceTierPriceEntry.setDiscountLevel1(discountLevel1);
		commerceTierPriceEntry.setDiscountLevel2(discountLevel2);
		commerceTierPriceEntry.setDiscountLevel3(discountLevel3);
		commerceTierPriceEntry.setDiscountLevel4(discountLevel4);
		commerceTierPriceEntry.setMinQuantity(
			_normalizeMinQuantity(commercePriceEntry, minQuantity));
		commerceTierPriceEntry.setDisplayDate(displayDate);

		if ((expirationDate == null) || expirationDate.after(date)) {
			commerceTierPriceEntry.setStatus(WorkflowConstants.STATUS_DRAFT);
		}
		else {
			commerceTierPriceEntry.setStatus(WorkflowConstants.STATUS_EXPIRED);
		}

		commerceTierPriceEntry.setExpirationDate(expirationDate);
		commerceTierPriceEntry.setStatusByUserId(user.getUserId());
		commerceTierPriceEntry.setStatusDate(
			serviceContext.getModifiedDate(date));
		commerceTierPriceEntry.setExpandoBridgeAttributes(serviceContext);

		// Commerce price entry

		_commercePriceEntryLocalService.setHasTierPrice(
			commerceTierPriceEntry.getCommercePriceEntryId(), true,
			bulkPricing);

		commerceTierPriceEntry = commerceTierPriceEntryPersistence.update(
			commerceTierPriceEntry);

		commerceTierPriceEntry = _startWorkflowInstance(
			user.getUserId(), commerceTierPriceEntry, serviceContext);

		return commerceTierPriceEntry;
	}

	@Override
	public CommerceTierPriceEntry updateCommerceTierPriceEntry(
			long commerceTierPriceEntryId, BigDecimal price,
			BigDecimal promoPrice, BigDecimal minQuantity, boolean bulkPricing,
			ServiceContext serviceContext)
		throws PortalException {

		Calendar now = new GregorianCalendar();

		return commerceTierPriceEntryLocalService.updateCommerceTierPriceEntry(
			commerceTierPriceEntryId, price, promoPrice, minQuantity,
			bulkPricing, true, null, null, null, null, now.get(Calendar.MONTH),
			now.get(Calendar.DAY_OF_MONTH), now.get(Calendar.YEAR),
			now.get(Calendar.HOUR), now.get(Calendar.MINUTE), 0, 0, 0, 0, 0,
			true, serviceContext);
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

		return commerceTierPriceEntryLocalService.updateCommerceTierPriceEntry(
			commerceTierPriceEntryId, price, null, minQuantity, bulkPricing,
			discountDiscovery, discountLevel1, discountLevel2, discountLevel3,
			discountLevel4, displayDateMonth, displayDateDay, displayDateYear,
			displayDateHour, displayDateMinute, expirationDateMonth,
			expirationDateDay, expirationDateYear, expirationDateHour,
			expirationDateMinute, neverExpire, serviceContext);
	}

	@Indexable(type = IndexableType.REINDEX)
	@Override
	public CommerceTierPriceEntry updateExternalReferenceCode(
			CommerceTierPriceEntry commerceTierPriceEntry,
			String externalReferenceCode)
		throws PortalException {

		commerceTierPriceEntry.setExternalReferenceCode(externalReferenceCode);

		return commerceTierPriceEntryPersistence.update(commerceTierPriceEntry);
	}

	@Indexable(type = IndexableType.REINDEX)
	@Override
	public CommerceTierPriceEntry updateStatus(
			long userId, long commerceTierPriceEntryId, int status,
			ServiceContext serviceContext,
			Map<String, Serializable> workflowContext)
		throws PortalException {

		User user = _userLocalService.getUser(userId);
		Date date = new Date();

		CommerceTierPriceEntry commerceTierPriceEntry =
			commerceTierPriceEntryPersistence.findByPrimaryKey(
				commerceTierPriceEntryId);

		if ((status == WorkflowConstants.STATUS_APPROVED) &&
			(commerceTierPriceEntry.getDisplayDate() != null) &&
			date.before(commerceTierPriceEntry.getDisplayDate())) {

			status = WorkflowConstants.STATUS_SCHEDULED;
		}

		Date modifiedDate = serviceContext.getModifiedDate(date);

		if (status == WorkflowConstants.STATUS_APPROVED) {
			Date expirationDate = commerceTierPriceEntry.getExpirationDate();

			if ((expirationDate != null) && expirationDate.before(date)) {
				commerceTierPriceEntry.setExpirationDate(null);
			}
		}

		if (status == WorkflowConstants.STATUS_EXPIRED) {
			commerceTierPriceEntry.setExpirationDate(date);
		}

		commerceTierPriceEntry.setStatus(status);
		commerceTierPriceEntry.setStatusByUserId(user.getUserId());
		commerceTierPriceEntry.setStatusByUserName(user.getFullName());
		commerceTierPriceEntry.setStatusDate(modifiedDate);

		return commerceTierPriceEntryPersistence.update(commerceTierPriceEntry);
	}

	private SearchContext _buildSearchContext(
		long companyId, long commercePriceEntryId, String keywords, int start,
		int end, Sort sort) {

		SearchContext searchContext = new SearchContext();

		searchContext.setAttributes(
			HashMapBuilder.<String, Serializable>put(
				Field.ENTRY_CLASS_PK, keywords
			).put(
				"commercePriceEntryId", commercePriceEntryId
			).put(
				"params",
				LinkedHashMapBuilder.<String, Object>put(
					"keywords", keywords
				).build()
			).build());
		searchContext.setCompanyId(companyId);
		searchContext.setEnd(end);

		if (Validator.isNotNull(keywords)) {
			searchContext.setKeywords(keywords);
		}

		if (sort != null) {
			searchContext.setSorts(sort);
		}

		searchContext.setStart(start);

		QueryConfig queryConfig = searchContext.getQueryConfig();

		queryConfig.setHighlightEnabled(false);
		queryConfig.setScoreEnabled(false);

		return searchContext;
	}

	private void _checkCommerceTierPriceEntriesByDisplayDate()
		throws PortalException {

		List<CommerceTierPriceEntry> commerceTierPriceEntries =
			commerceTierPriceEntryPersistence.findByLtD_S(
				new Date(), WorkflowConstants.STATUS_SCHEDULED);

		for (CommerceTierPriceEntry commerceTierPriceEntry :
				commerceTierPriceEntries) {

			long userId = _portal.getValidUserId(
				commerceTierPriceEntry.getCompanyId(),
				commerceTierPriceEntry.getUserId());

			ServiceContext serviceContext = new ServiceContext();

			serviceContext.setCommand(Constants.UPDATE);

			CommercePriceEntry commercePriceEntry =
				commerceTierPriceEntry.getCommercePriceEntry();

			CommercePriceList commercePriceList =
				commercePriceEntry.getCommercePriceList();

			serviceContext.setScopeGroupId(commercePriceList.getGroupId());

			commerceTierPriceEntryLocalService.updateStatus(
				userId, commerceTierPriceEntry.getCommerceTierPriceEntryId(),
				WorkflowConstants.STATUS_APPROVED, serviceContext,
				new HashMap<String, Serializable>());
		}
	}

	private void _checkCommerceTierPriceEntriesByExpirationDate()
		throws PortalException {

		List<CommerceTierPriceEntry> commerceTierPriceEntries =
			commerceTierPriceEntryPersistence.findByLtE_S(
				new Date(), WorkflowConstants.STATUS_APPROVED);

		if (_log.isDebugEnabled()) {
			_log.debug(
				"Expiring " + commerceTierPriceEntries.size() +
					" commerce tier price entries");
		}

		if ((commerceTierPriceEntries != null) &&
			!commerceTierPriceEntries.isEmpty()) {

			for (CommerceTierPriceEntry commerceTierPriceEntry :
					commerceTierPriceEntries) {

				long userId = _portal.getValidUserId(
					commerceTierPriceEntry.getCompanyId(),
					commerceTierPriceEntry.getUserId());

				ServiceContext serviceContext = new ServiceContext();

				serviceContext.setCommand(Constants.UPDATE);

				CommercePriceEntry commercePriceEntry =
					commerceTierPriceEntry.getCommercePriceEntry();

				CommercePriceList commercePriceList =
					commercePriceEntry.getCommercePriceList();

				serviceContext.setScopeGroupId(commercePriceList.getGroupId());

				commerceTierPriceEntryLocalService.updateStatus(
					userId,
					commerceTierPriceEntry.getCommerceTierPriceEntryId(),
					WorkflowConstants.STATUS_EXPIRED, serviceContext,
					new HashMap<String, Serializable>());
			}
		}
	}

	private List<CommerceTierPriceEntry> _getCommerceTierPriceEntries(Hits hits)
		throws PortalException {

		List<Document> documents = hits.toList();

		List<CommerceTierPriceEntry> commerceTierPriceEntries = new ArrayList<>(
			documents.size());

		for (Document document : documents) {
			long commerceTierPriceEntryId = GetterUtil.getLong(
				document.get(Field.ENTRY_CLASS_PK));

			CommerceTierPriceEntry commerceTierPriceEntry =
				fetchCommerceTierPriceEntry(commerceTierPriceEntryId);

			if (commerceTierPriceEntry == null) {
				commerceTierPriceEntries = null;

				Indexer<CommerceTierPriceEntry> indexer =
					IndexerRegistryUtil.getIndexer(
						CommerceTierPriceEntry.class);

				long companyId = GetterUtil.getLong(
					document.get(Field.COMPANY_ID));

				indexer.delete(companyId, document.getUID());
			}
			else if (commerceTierPriceEntries != null) {
				commerceTierPriceEntries.add(commerceTierPriceEntry);
			}
		}

		return commerceTierPriceEntries;
	}

	private BigDecimal _normalizeMinQuantity(
			CommercePriceEntry commercePriceEntry, BigDecimal minQuantity)
		throws PortalException {

		int unitOfMeasurePrecision = 0;

		CPInstance cpInstance = _cpInstanceLocalService.fetchCProductInstance(
			commercePriceEntry.getCProductId(),
			commercePriceEntry.getCPInstanceUuid());

		if (cpInstance != null) {
			CPInstanceUnitOfMeasure cpInstanceUnitOfMeasure =
				_cpInstanceUnitOfMeasureLocalService.
					fetchCPInstanceUnitOfMeasure(
						cpInstance.getCPInstanceId(),
						commercePriceEntry.getUnitOfMeasureKey());

			if (cpInstanceUnitOfMeasure != null) {
				unitOfMeasurePrecision = cpInstanceUnitOfMeasure.getPrecision();
			}
		}

		return minQuantity.setScale(
			unitOfMeasurePrecision, RoundingMode.HALF_UP);
	}

	private BaseModelSearchResult<CommerceTierPriceEntry>
			_searchCommerceTierPriceEntries(SearchContext searchContext)
		throws PortalException {

		Indexer<CommerceTierPriceEntry> indexer =
			IndexerRegistryUtil.nullSafeGetIndexer(
				CommerceTierPriceEntry.class);

		for (int i = 0; i < 10; i++) {
			Hits hits = indexer.search(searchContext, _SELECTED_FIELD_NAMES);

			List<CommerceTierPriceEntry> commerceTierPriceEntries =
				_getCommerceTierPriceEntries(hits);

			if (commerceTierPriceEntries != null) {
				return new BaseModelSearchResult<>(
					commerceTierPriceEntries, hits.getLength());
			}
		}

		throw new SearchException(
			"Unable to fix the search index after 10 attempts");
	}

	private int _searchCommerceTierPriceEntriesCount(
			SearchContext searchContext)
		throws PortalException {

		Indexer<CommerceTierPriceEntry> indexer =
			IndexerRegistryUtil.nullSafeGetIndexer(
				CommerceTierPriceEntry.class);

		return GetterUtil.getInteger(indexer.searchCount(searchContext));
	}

	private CommerceTierPriceEntry _startWorkflowInstance(
			long userId, CommerceTierPriceEntry commerceTierPriceEntry,
			ServiceContext serviceContext)
		throws PortalException {

		Map<String, Serializable> workflowContext = new HashMap<>();

		return WorkflowHandlerRegistryUtil.startWorkflowInstance(
			commerceTierPriceEntry.getCompanyId(), 0L, userId,
			CommerceTierPriceEntry.class.getName(),
			commerceTierPriceEntry.getCommerceTierPriceEntryId(),
			commerceTierPriceEntry, serviceContext, workflowContext);
	}

	private void _validateCommercePriceEntryId(
			long commerceTierPriceEntryId, long commercePriceEntryId,
			BigDecimal minQuantity)
		throws PortalException {

		CommercePriceEntry commercePriceEntry =
			_commercePriceEntryPersistence.findByPrimaryKey(
				commercePriceEntryId);

		CommerceTierPriceEntry commerceTierPriceEntry =
			commerceTierPriceEntryPersistence.fetchByC_M(
				commercePriceEntryId,
				_normalizeMinQuantity(commercePriceEntry, minQuantity));

		if ((commerceTierPriceEntry != null) &&
			!(commerceTierPriceEntry.getCommerceTierPriceEntryId() ==
				commerceTierPriceEntryId)) {

			throw new DuplicateCommerceTierPriceEntryException();
		}
	}

	private void _validateMinQuantity(
			CommercePriceEntry commercePriceEntry, BigDecimal minQuantity)
		throws PortalException {

		if (minQuantity == null) {
			return;
		}

		if (BigDecimalUtil.lte(minQuantity, BigDecimal.ZERO)) {
			throw new CommerceTierPriceEntryMinQuantityException(
				"The min quantity must be greater than zero");
		}

		CPInstance cpInstance = _cpInstanceLocalService.fetchCProductInstance(
			commercePriceEntry.getCProductId(),
			commercePriceEntry.getCPInstanceUuid());

		if (cpInstance != null) {
			CPInstanceUnitOfMeasure cpInstanceUnitOfMeasure =
				_cpInstanceUnitOfMeasureLocalService.
					fetchCPInstanceUnitOfMeasure(
						cpInstance.getCPInstanceId(),
						commercePriceEntry.getUnitOfMeasureKey());

			if (cpInstanceUnitOfMeasure != null) {
				int unitOfMeasurePrecision =
					cpInstanceUnitOfMeasure.getPrecision();

				if (minQuantity.scale() > unitOfMeasurePrecision) {
					throw new CommerceTierPriceEntryMinQuantityException(
						"It does not have the same precision as the unit of " +
							"measure quantity");
				}

				BigDecimal remainder = minQuantity.remainder(
					cpInstanceUnitOfMeasure.getIncrementalOrderQuantity());

				if (remainder.compareTo(BigDecimal.ZERO) != 0) {
					throw new CommerceTierPriceEntryMinQuantityException(
						"It is not a multiple of the incremental order " +
							"quantity of the unit of measure");
				}
			}
		}
	}

	private static final String[] _SELECTED_FIELD_NAMES = {
		Field.ENTRY_CLASS_PK, Field.COMPANY_ID, Field.GROUP_ID, Field.UID
	};

	private static final Log _log = LogFactoryUtil.getLog(
		CommerceTierPriceEntryLocalServiceImpl.class);

	@Reference
	private CommercePriceEntryLocalService _commercePriceEntryLocalService;

	@Reference
	private CommercePriceEntryPersistence _commercePriceEntryPersistence;

	@Reference
	private CPInstanceLocalService _cpInstanceLocalService;

	@Reference
	private CPInstanceUnitOfMeasureLocalService
		_cpInstanceUnitOfMeasureLocalService;

	@Reference
	private ExpandoRowLocalService _expandoRowLocalService;

	@Reference
	private Portal _portal;

	@Reference
	private UserLocalService _userLocalService;

}