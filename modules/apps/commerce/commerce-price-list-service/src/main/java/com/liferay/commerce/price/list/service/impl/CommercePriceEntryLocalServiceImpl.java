/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.price.list.service.impl;

import com.liferay.commerce.constants.CommercePriceConstants;
import com.liferay.commerce.price.list.exception.CommercePriceEntryDisplayDateException;
import com.liferay.commerce.price.list.exception.CommercePriceEntryExpirationDateException;
import com.liferay.commerce.price.list.exception.CommercePriceEntryUnitOfMeasureKeyException;
import com.liferay.commerce.price.list.exception.CommercePriceListMaxPriceValueException;
import com.liferay.commerce.price.list.exception.CommercePriceListMinPriceValueException;
import com.liferay.commerce.price.list.exception.NoSuchPriceEntryException;
import com.liferay.commerce.price.list.model.CommercePriceEntry;
import com.liferay.commerce.price.list.model.CommercePriceEntryTable;
import com.liferay.commerce.price.list.model.CommercePriceList;
import com.liferay.commerce.price.list.model.CommercePriceListTable;
import com.liferay.commerce.price.list.service.base.CommercePriceEntryLocalServiceBaseImpl;
import com.liferay.commerce.price.list.service.persistence.CommercePriceListPersistence;
import com.liferay.commerce.product.exception.NoSuchCPInstanceException;
import com.liferay.commerce.product.model.CPDefinition;
import com.liferay.commerce.product.model.CPInstance;
import com.liferay.commerce.product.model.CPInstanceUnitOfMeasure;
import com.liferay.commerce.product.service.CPDefinitionLocalService;
import com.liferay.commerce.product.service.CPInstanceLocalService;
import com.liferay.commerce.product.service.CPInstanceUnitOfMeasureLocalService;
import com.liferay.expando.kernel.service.ExpandoRowLocalService;
import com.liferay.petra.sql.dsl.DSLQueryFactoryUtil;
import com.liferay.petra.sql.dsl.query.FromStep;
import com.liferay.petra.sql.dsl.query.GroupByStep;
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
 * @author Marco Leo
 * @author Alessio Antonio Rendina
 * @author Zoltán Takács
 */
@Component(
	property = "model.class.name=com.liferay.commerce.price.list.model.CommercePriceEntry",
	service = AopService.class
)
public class CommercePriceEntryLocalServiceImpl
	extends CommercePriceEntryLocalServiceBaseImpl {

	@Override
	public CommercePriceEntry addCommercePriceEntry(
			String externalReferenceCode, long cProductId,
			String cpInstanceUuid, long commercePriceListId, BigDecimal price,
			boolean priceOnApplication, BigDecimal promoPrice,
			String unitOfMeasureKey, ServiceContext serviceContext)
		throws PortalException {

		Calendar calendar = new GregorianCalendar();

		return commercePriceEntryLocalService.addCommercePriceEntry(
			externalReferenceCode, cProductId, cpInstanceUuid,
			commercePriceListId, true, null, null, null, null,
			calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH),
			calendar.get(Calendar.YEAR), calendar.get(Calendar.HOUR),
			calendar.get(Calendar.MINUTE), 0, 0, 0, 0, 0, true, price,
			priceOnApplication, promoPrice, unitOfMeasureKey, serviceContext);
	}

	@Indexable(type = IndexableType.REINDEX)
	@Override
	public CommercePriceEntry addCommercePriceEntry(
			String externalReferenceCode, long cProductId,
			String cpInstanceUuid, long commercePriceListId,
			boolean discountDiscovery, BigDecimal discountLevel1,
			BigDecimal discountLevel2, BigDecimal discountLevel3,
			BigDecimal discountLevel4, int displayDateMonth, int displayDateDay,
			int displayDateYear, int displayDateHour, int displayDateMinute,
			int expirationDateMonth, int expirationDateDay,
			int expirationDateYear, int expirationDateHour,
			int expirationDateMinute, boolean neverExpire, BigDecimal price,
			boolean priceOnApplication, BigDecimal promoPrice,
			String unitOfMeasureKey, ServiceContext serviceContext)
		throws PortalException {

		User user = _userLocalService.getUser(serviceContext.getUserId());

		CPInstance cpInstance = _cpInstanceLocalService.fetchCPInstance(
			cProductId, cpInstanceUuid);

		long cpInstanceId = 0;

		if (cpInstance != null) {
			cpInstanceId = cpInstance.getCPInstanceId();
		}

		_validateUnitOfMeasureKey(cpInstanceId, unitOfMeasureKey);

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

		long commercePriceEntryId = counterLocalService.increment();

		CommercePriceEntry commercePriceEntry =
			commercePriceEntryPersistence.create(commercePriceEntryId);

		commercePriceEntry.setExternalReferenceCode(externalReferenceCode);
		commercePriceEntry.setCompanyId(user.getCompanyId());
		commercePriceEntry.setUserId(user.getUserId());
		commercePriceEntry.setUserName(user.getFullName());
		commercePriceEntry.setCommercePriceListId(commercePriceListId);
		commercePriceEntry.setCPInstanceUuid(cpInstanceUuid);
		commercePriceEntry.setCProductId(cProductId);
		commercePriceEntry.setDiscountDiscovery(discountDiscovery);
		commercePriceEntry.setDiscountLevel1(discountLevel1);
		commercePriceEntry.setDiscountLevel2(discountLevel2);
		commercePriceEntry.setDiscountLevel3(discountLevel3);
		commercePriceEntry.setDiscountLevel4(discountLevel4);
		commercePriceEntry.setDisplayDate(displayDate);
		commercePriceEntry.setExpirationDate(expirationDate);
		commercePriceEntry.setPrice(price);
		commercePriceEntry.setPriceOnApplication(priceOnApplication);

		CPInstanceUnitOfMeasure cpInstanceUnitOfMeasure =
			_getCPInstanceUnitOfMeasure(cpInstanceId, unitOfMeasureKey);

		if (cpInstanceUnitOfMeasure != null) {
			commercePriceEntry.setPricingQuantity(
				cpInstanceUnitOfMeasure.getPricingQuantity());

			BigDecimal incrementalOrderQuantity =
				cpInstanceUnitOfMeasure.getIncrementalOrderQuantity();

			commercePriceEntry.setQuantity(
				incrementalOrderQuantity.setScale(
					cpInstanceUnitOfMeasure.getPrecision(),
					RoundingMode.HALF_UP));

			commercePriceEntry.setUnitOfMeasureKey(
				cpInstanceUnitOfMeasure.getKey());
		}
		else {
			commercePriceEntry.setPricingQuantity(null);
			commercePriceEntry.setQuantity(null);
			commercePriceEntry.setUnitOfMeasureKey(null);
		}

		commercePriceEntry.setPromoPrice(promoPrice);

		if ((expirationDate == null) || expirationDate.after(date)) {
			commercePriceEntry.setStatus(WorkflowConstants.STATUS_DRAFT);
		}
		else {
			commercePriceEntry.setStatus(WorkflowConstants.STATUS_EXPIRED);
		}

		commercePriceEntry.setStatusByUserId(user.getUserId());
		commercePriceEntry.setStatusDate(serviceContext.getModifiedDate(date));
		commercePriceEntry.setExpandoBridgeAttributes(serviceContext);

		commercePriceEntry = commercePriceEntryPersistence.update(
			commercePriceEntry);

		commercePriceEntry = _startWorkflowInstance(
			user.getUserId(), commercePriceEntry, serviceContext);

		if (cpInstance != null) {
			_reindexCPDefinition(cpInstance.getCPDefinitionId());
		}

		return commercePriceEntry;
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
			boolean priceOnApplication, BigDecimal promoPrice,
			String skuExternalReferenceCode, String unitOfMeasureKey,
			ServiceContext serviceContext)
		throws PortalException {

		// Update

		if (commercePriceEntryId > 0) {
			try {
				return commercePriceEntryLocalService.updateCommercePriceEntry(
					commercePriceEntryId, true, discountDiscovery,
					discountLevel1, discountLevel2, discountLevel3,
					discountLevel4, displayDateMonth, displayDateDay,
					displayDateYear, displayDateHour, displayDateMinute,
					expirationDateMonth, expirationDateDay, expirationDateYear,
					expirationDateHour, expirationDateMinute, neverExpire,
					price, priceOnApplication, promoPrice, unitOfMeasureKey,
					serviceContext);
			}
			catch (NoSuchPriceEntryException noSuchPriceEntryException) {
				if (_log.isDebugEnabled()) {
					_log.debug(
						"Unable to find price entry with ID: " +
							commercePriceEntryId,
						noSuchPriceEntryException);
				}
			}
		}

		CommercePriceEntry commercePriceEntry = null;

		if (Validator.isNotNull(externalReferenceCode)) {
			commercePriceEntry = commercePriceEntryPersistence.fetchByERC_C(
				externalReferenceCode, serviceContext.getCompanyId());
		}

		if (commercePriceEntry != null) {
			return commercePriceEntryLocalService.updateCommercePriceEntry(
				commercePriceEntry.getCommercePriceEntryId(), true,
				discountDiscovery, discountLevel1, discountLevel2,
				discountLevel3, discountLevel4, displayDateMonth,
				displayDateDay, displayDateYear, displayDateHour,
				displayDateMinute, expirationDateMonth, expirationDateDay,
				expirationDateYear, expirationDateHour, expirationDateMinute,
				neverExpire, price, priceOnApplication, promoPrice,
				unitOfMeasureKey, serviceContext);
		}

		// Add

		if ((cProductId > 0) && (cpInstanceUuid != null)) {
			return commercePriceEntryLocalService.addCommercePriceEntry(
				externalReferenceCode, cProductId, cpInstanceUuid,
				commercePriceListId, discountDiscovery, discountLevel1,
				discountLevel2, discountLevel3, discountLevel4,
				displayDateMonth, displayDateDay, displayDateYear,
				displayDateHour, displayDateMinute, expirationDateMonth,
				expirationDateDay, expirationDateYear, expirationDateHour,
				expirationDateMinute, neverExpire, price, priceOnApplication,
				promoPrice, unitOfMeasureKey, serviceContext);
		}

		if (Validator.isNotNull(skuExternalReferenceCode)) {
			CPInstance cpInstance =
				_cpInstanceLocalService.getCPInstanceByExternalReferenceCode(
					skuExternalReferenceCode, serviceContext.getCompanyId());

			CPDefinition cpDefinition =
				_cpDefinitionLocalService.getCPDefinition(
					cpInstance.getCPDefinitionId());

			return commercePriceEntryLocalService.addCommercePriceEntry(
				externalReferenceCode, cpDefinition.getCProductId(),
				cpInstance.getCPInstanceUuid(), commercePriceListId,
				discountDiscovery, discountLevel1, discountLevel2,
				discountLevel3, discountLevel4, displayDateMonth,
				displayDateDay, displayDateYear, displayDateHour,
				displayDateMinute, expirationDateMonth, expirationDateDay,
				expirationDateYear, expirationDateHour, expirationDateMinute,
				neverExpire, price, priceOnApplication, promoPrice,
				unitOfMeasureKey, serviceContext);
		}

		throw new NoSuchCPInstanceException(
			StringBundler.concat(
				"{cProductId=", cProductId, ", cpInstanceUuid=", cpInstanceUuid,
				", skuExternalReferenceCode=", skuExternalReferenceCode,
				CharPool.CLOSE_CURLY_BRACE));
	}

	@Override
	public void checkCommercePriceEntries() throws PortalException {
		_checkCommercePriceEntriesByDisplayDate();
		_checkCommercePriceEntriesByExpirationDate();
	}

	@Override
	public void deleteCommercePriceEntries(long commercePriceListId)
		throws PortalException {

		List<CommercePriceEntry> commercePriceEntries =
			commercePriceEntryLocalService.getCommercePriceEntries(
				commercePriceListId, QueryUtil.ALL_POS, QueryUtil.ALL_POS);

		for (CommercePriceEntry commercePriceEntry : commercePriceEntries) {
			commercePriceEntryLocalService.deleteCommercePriceEntry(
				commercePriceEntry);
		}
	}

	@Override
	public void deleteCommercePriceEntries(String cpInstanceUuid)
		throws PortalException {

		List<CommercePriceEntry> commercePriceEntries =
			commercePriceEntryPersistence.findByCPInstanceUuid(cpInstanceUuid);

		for (CommercePriceEntry commercePriceEntry : commercePriceEntries) {
			commercePriceEntryLocalService.deleteCommercePriceEntry(
				commercePriceEntry);
		}
	}

	@Override
	public void deleteCommercePriceEntries(
			String cpInstanceUuid, BigDecimal quantity, String unitOfMeasureKey)
		throws PortalException {

		List<CommercePriceEntry> commercePriceEntries =
			commercePriceEntryPersistence.findByC_Q_U(
				cpInstanceUuid, quantity, unitOfMeasureKey);

		for (CommercePriceEntry commercePriceEntry : commercePriceEntries) {
			commercePriceEntryLocalService.deleteCommercePriceEntry(
				commercePriceEntry);
		}
	}

	@Indexable(type = IndexableType.DELETE)
	@Override
	@SystemEvent(type = SystemEventConstants.TYPE_DELETE)
	public CommercePriceEntry deleteCommercePriceEntry(
			CommercePriceEntry commercePriceEntry)
		throws PortalException {

		// Commerce price entry

		commercePriceEntryPersistence.remove(commercePriceEntry);

		// Expando

		_expandoRowLocalService.deleteRows(
			commercePriceEntry.getCommercePriceEntryId());

		CPInstance cpInstance = _cpInstanceLocalService.fetchCPInstance(
			commercePriceEntry.getCProductId(),
			commercePriceEntry.getCPInstanceUuid());

		if (cpInstance != null) {
			_reindexCPDefinition(cpInstance.getCPDefinitionId());
		}

		return commercePriceEntry;
	}

	@Override
	public CommercePriceEntry deleteCommercePriceEntry(
			long commercePriceEntryId)
		throws PortalException {

		CommercePriceEntry commercePriceEntry =
			commercePriceEntryPersistence.findByPrimaryKey(
				commercePriceEntryId);

		return commercePriceEntryLocalService.deleteCommercePriceEntry(
			commercePriceEntry);
	}

	@Override
	public CommercePriceEntry fetchCommercePriceEntry(
		long commercePriceListId, String cpInstanceUuid, int status,
		String unitOfMeasureKey) {

		List<CommercePriceEntry> commercePriceEntries = dslQuery(
			_getGroupByStep(
				DSLQueryFactoryUtil.selectDistinct(
					CommercePriceEntryTable.INSTANCE),
				commercePriceListId, cpInstanceUuid, status, unitOfMeasureKey
			).orderBy(
				CommercePriceEntryTable.INSTANCE.displayDate.descending(),
				CommercePriceEntryTable.INSTANCE.createDate.descending()
			).limit(
				0, 1
			));

		if (commercePriceEntries.isEmpty()) {
			return null;
		}

		return commercePriceEntries.get(0);
	}

	@Override
	public CommercePriceEntry fetchCommercePriceEntry(
		long commercePriceListId, String cpInstanceUuid,
		String unitOfMeasureKey) {

		List<CommercePriceEntry> commercePriceEntries = dslQuery(
			_getGroupByStep(
				DSLQueryFactoryUtil.selectDistinct(
					CommercePriceEntryTable.INSTANCE),
				commercePriceListId, cpInstanceUuid,
				WorkflowConstants.STATUS_ANY, unitOfMeasureKey
			).orderBy(
				CommercePriceEntryTable.INSTANCE.displayDate.descending(),
				CommercePriceEntryTable.INSTANCE.createDate.descending()
			).limit(
				0, 1
			));

		if (commercePriceEntries.isEmpty()) {
			return null;
		}

		return commercePriceEntries.get(0);
	}

	@Override
	public CommercePriceEntry fetchCommercePriceEntry(
		long commercePriceListId, String cpInstanceUuid,
		String unitOfMeasureKey, boolean useAncestor) {

		CommercePriceEntry commercePriceEntry =
			commercePriceEntryLocalService.fetchCommercePriceEntry(
				commercePriceListId, cpInstanceUuid,
				WorkflowConstants.STATUS_APPROVED, unitOfMeasureKey);

		if (!useAncestor || (commercePriceEntry != null)) {
			return commercePriceEntry;
		}

		CommercePriceList commercePriceList =
			_commercePriceListPersistence.fetchByPrimaryKey(
				commercePriceListId);

		if ((commercePriceList == null) ||
			(commercePriceList.getParentCommercePriceListId() == 0)) {

			return null;
		}

		return commercePriceEntryLocalService.fetchCommercePriceEntry(
			commercePriceList.getParentCommercePriceListId(), cpInstanceUuid,
			unitOfMeasureKey, useAncestor);
	}

	@Override
	public List<CommercePriceEntry> getCommercePriceEntries(
		long commercePriceListId, int start, int end) {

		return commercePriceEntryPersistence.findByCommercePriceListId(
			commercePriceListId, start, end);
	}

	@Override
	public List<CommercePriceEntry> getCommercePriceEntries(
		long commercePriceListId, int start, int end,
		OrderByComparator<CommercePriceEntry> orderByComparator) {

		return commercePriceEntryPersistence.findByCommercePriceListId(
			commercePriceListId, start, end, orderByComparator);
	}

	@Override
	public List<CommercePriceEntry> getCommercePriceEntries(
		String cpInstanceUuid, BigDecimal quantity, String unitOfMeasureKey) {

		return commercePriceEntryPersistence.findByC_Q_U(
			cpInstanceUuid, quantity, unitOfMeasureKey);
	}

	@Override
	public List<CommercePriceEntry> getCommercePriceEntriesByCompanyId(
		long companyId, int start, int end) {

		return commercePriceEntryPersistence.findByCompanyId(
			companyId, start, end);
	}

	@Override
	public int getCommercePriceEntriesCount(long commercePriceListId) {
		return commercePriceEntryPersistence.countByCommercePriceListId(
			commercePriceListId);
	}

	@Override
	public int getCommercePriceEntriesCountByCompanyId(long companyId) {
		return commercePriceEntryPersistence.countByCompanyId(companyId);
	}

	@Override
	public CommercePriceEntry getInstanceBaseCommercePriceEntry(
		String cpInstanceUuid, String priceListType, String unitOfMeasureKey) {

		List<CommercePriceEntry> commercePriceEntries = dslQuery(
			DSLQueryFactoryUtil.select(
				CommercePriceEntryTable.INSTANCE
			).from(
				CommercePriceEntryTable.INSTANCE
			).innerJoinON(
				CommercePriceListTable.INSTANCE,
				CommercePriceListTable.INSTANCE.commercePriceListId.eq(
					CommercePriceEntryTable.INSTANCE.commercePriceListId)
			).where(
				CommercePriceEntryTable.INSTANCE.CPInstanceUuid.eq(
					cpInstanceUuid
				).and(
					() -> {
						if (Validator.isNull(unitOfMeasureKey)) {
							return null;
						}

						return CommercePriceEntryTable.INSTANCE.
							unitOfMeasureKey.eq(unitOfMeasureKey);
					}
				).and(
					CommercePriceListTable.INSTANCE.type.eq(priceListType)
				).and(
					CommercePriceListTable.INSTANCE.catalogBasePriceList.eq(
						true)
				)
			));

		if (commercePriceEntries.isEmpty()) {
			return null;
		}

		return commercePriceEntries.get(0);
	}

	@Override
	public List<CommercePriceEntry> getInstanceCommercePriceEntries(
		String cpInstanceUuid, int start, int end,
		OrderByComparator<CommercePriceEntry> orderByComparator) {

		return commercePriceEntryPersistence.findByCPInstanceUuid(
			cpInstanceUuid, start, end, orderByComparator);
	}

	@Override
	public int getInstanceCommercePriceEntriesCount(String cpInstanceUuid) {
		return commercePriceEntryPersistence.countByCPInstanceUuid(
			cpInstanceUuid);
	}

	@Override
	public Hits search(SearchContext searchContext) {
		try {
			Indexer<CommercePriceEntry> indexer =
				IndexerRegistryUtil.nullSafeGetIndexer(
					CommercePriceEntry.class);

			return indexer.search(searchContext);
		}
		catch (Exception exception) {
			throw new SystemException(exception);
		}
	}

	@Override
	public BaseModelSearchResult<CommercePriceEntry> searchCommercePriceEntries(
			long companyId, long commercePriceListId, String keywords,
			int start, int end, Sort sort)
		throws PortalException {

		SearchContext searchContext = _buildSearchContext(
			companyId, commercePriceListId, keywords, start, end, sort);

		return _searchCommercePriceEntries(searchContext);
	}

	@Override
	public int searchCommercePriceEntriesCount(
			long companyId, long commercePriceListId, String keywords)
		throws PortalException {

		SearchContext searchContext = _buildSearchContext(
			companyId, commercePriceListId, keywords, QueryUtil.ALL_POS,
			QueryUtil.ALL_POS, null);

		return _searchCommercePriceEntriesCount(searchContext);
	}

	@Indexable(type = IndexableType.REINDEX)
	@Override
	public CommercePriceEntry setHasTierPrice(
			long commercePriceEntryId, boolean hasTierPrice)
		throws PortalException {

		CommercePriceEntry commercePriceEntry =
			commercePriceEntryPersistence.findByPrimaryKey(
				commercePriceEntryId);

		commercePriceEntry.setBulkPricing(true);
		commercePriceEntry.setHasTierPrice(hasTierPrice);

		return commercePriceEntryPersistence.update(commercePriceEntry);
	}

	@Override
	public CommercePriceEntry setHasTierPrice(
			long commercePriceEntryId, boolean hasTierPrice,
			boolean bulkPricing)
		throws PortalException {

		CommercePriceEntry commercePriceEntry =
			commercePriceEntryPersistence.findByPrimaryKey(
				commercePriceEntryId);

		commercePriceEntry.setBulkPricing(bulkPricing);
		commercePriceEntry.setHasTierPrice(hasTierPrice);

		return commercePriceEntryPersistence.update(commercePriceEntry);
	}

	@Indexable(type = IndexableType.REINDEX)
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
			boolean priceOnApplication, BigDecimal promoPrice,
			String unitOfMeasureKey, ServiceContext serviceContext)
		throws PortalException {

		User user = _userLocalService.getUser(serviceContext.getUserId());

		CommercePriceEntry commercePriceEntry =
			commercePriceEntryPersistence.findByPrimaryKey(
				commercePriceEntryId);

		Date expirationDate = null;
		Date date = new Date();

		Date displayDate = _portal.getDate(
			displayDateMonth, displayDateDay, displayDateYear, displayDateHour,
			displayDateMinute, user.getTimeZone(),
			CommercePriceEntryDisplayDateException.class);

		_validatePrice(
			price, discountLevel1, discountLevel2, discountLevel3,
			discountLevel4);

		CPInstance cpInstance = _cpInstanceLocalService.fetchCPInstance(
			commercePriceEntry.getCProductId(),
			commercePriceEntry.getCPInstanceUuid());

		long cpInstanceId = 0;

		if (cpInstance != null) {
			cpInstanceId = cpInstance.getCPInstanceId();
		}

		_validateUnitOfMeasureKey(cpInstanceId, unitOfMeasureKey);

		if (!neverExpire) {
			expirationDate = _portal.getDate(
				expirationDateMonth, expirationDateDay, expirationDateYear,
				expirationDateHour, expirationDateMinute, user.getTimeZone(),
				CommercePriceEntryExpirationDateException.class);
		}

		commercePriceEntry.setBulkPricing(bulkPricing);
		commercePriceEntry.setDiscountDiscovery(discountDiscovery);
		commercePriceEntry.setDiscountLevel1(discountLevel1);
		commercePriceEntry.setDiscountLevel2(discountLevel2);
		commercePriceEntry.setDiscountLevel3(discountLevel3);
		commercePriceEntry.setDiscountLevel4(discountLevel4);
		commercePriceEntry.setDisplayDate(displayDate);
		commercePriceEntry.setExpirationDate(expirationDate);
		commercePriceEntry.setPrice(price);
		commercePriceEntry.setPriceOnApplication(priceOnApplication);

		CPInstanceUnitOfMeasure cpInstanceUnitOfMeasure =
			_getCPInstanceUnitOfMeasure(cpInstanceId, unitOfMeasureKey);

		if (cpInstanceUnitOfMeasure != null) {
			commercePriceEntry.setPricingQuantity(
				cpInstanceUnitOfMeasure.getPricingQuantity());

			BigDecimal incrementalOrderQuantity =
				cpInstanceUnitOfMeasure.getIncrementalOrderQuantity();

			commercePriceEntry.setQuantity(
				incrementalOrderQuantity.setScale(
					cpInstanceUnitOfMeasure.getPrecision(),
					RoundingMode.HALF_UP));

			commercePriceEntry.setUnitOfMeasureKey(
				cpInstanceUnitOfMeasure.getKey());
		}
		else {
			commercePriceEntry.setPricingQuantity(null);
			commercePriceEntry.setQuantity(null);
			commercePriceEntry.setUnitOfMeasureKey(null);
		}

		commercePriceEntry.setPromoPrice(promoPrice);

		if ((expirationDate == null) || expirationDate.after(date)) {
			commercePriceEntry.setStatus(WorkflowConstants.STATUS_DRAFT);
		}
		else {
			commercePriceEntry.setStatus(WorkflowConstants.STATUS_EXPIRED);
		}

		commercePriceEntry.setStatusByUserId(user.getUserId());
		commercePriceEntry.setStatusDate(serviceContext.getModifiedDate(date));
		commercePriceEntry.setExpandoBridgeAttributes(serviceContext);

		commercePriceEntry = commercePriceEntryPersistence.update(
			commercePriceEntry);

		commercePriceEntry = _startWorkflowInstance(
			user.getUserId(), commercePriceEntry, serviceContext);

		if (cpInstance != null) {
			_reindexCPDefinition(cpInstance.getCPDefinitionId());
		}

		return commercePriceEntry;
	}

	@Indexable(type = IndexableType.REINDEX)
	@Override
	public CommercePriceEntry updateExternalReferenceCode(
			String externalReferenceCode, CommercePriceEntry commercePriceEntry)
		throws PortalException {

		commercePriceEntry.setExternalReferenceCode(externalReferenceCode);

		return commercePriceEntryPersistence.update(commercePriceEntry);
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

		commercePriceEntry.setBulkPricing(bulkPricing);
		commercePriceEntry.setPrice(price);
		commercePriceEntry.setPriceOnApplication(priceOnApplication);
		commercePriceEntry.setPromoPrice(promoPrice);
		commercePriceEntry.setUnitOfMeasureKey(unitOfMeasureKey);
		commercePriceEntry.setExpandoBridgeAttributes(serviceContext);

		return commercePriceEntryPersistence.update(commercePriceEntry);
	}

	@Indexable(type = IndexableType.REINDEX)
	@Override
	public CommercePriceEntry updateStatus(
			long userId, long commercePriceEntryId, int status,
			ServiceContext serviceContext,
			Map<String, Serializable> workflowContext)
		throws PortalException {

		User user = _userLocalService.getUser(userId);
		Date date = new Date();

		CommercePriceEntry commercePriceEntry =
			commercePriceEntryPersistence.findByPrimaryKey(
				commercePriceEntryId);

		if ((status == WorkflowConstants.STATUS_APPROVED) &&
			(commercePriceEntry.getDisplayDate() != null) &&
			date.before(commercePriceEntry.getDisplayDate())) {

			status = WorkflowConstants.STATUS_SCHEDULED;
		}

		Date modifiedDate = serviceContext.getModifiedDate(date);

		if (status == WorkflowConstants.STATUS_APPROVED) {
			Date expirationDate = commercePriceEntry.getExpirationDate();

			if ((expirationDate != null) && expirationDate.before(date)) {
				commercePriceEntry.setExpirationDate(null);
			}
		}

		if (status == WorkflowConstants.STATUS_EXPIRED) {
			commercePriceEntry.setExpirationDate(date);
		}

		commercePriceEntry.setStatus(status);
		commercePriceEntry.setStatusByUserId(user.getUserId());
		commercePriceEntry.setStatusByUserName(user.getFullName());
		commercePriceEntry.setStatusDate(modifiedDate);

		return commercePriceEntryPersistence.update(commercePriceEntry);
	}

	private SearchContext _buildSearchContext(
		long companyId, long commercePriceListId, String keywords, int start,
		int end, Sort sort) {

		SearchContext searchContext = new SearchContext();

		searchContext.setAttributes(
			HashMapBuilder.<String, Serializable>put(
				Field.ENTRY_CLASS_PK, keywords
			).put(
				"commercePriceListId", commercePriceListId
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

	private void _checkCommercePriceEntriesByDisplayDate()
		throws PortalException {

		List<CommercePriceEntry> commercePriceEntries =
			commercePriceEntryPersistence.findByLtD_S(
				new Date(), WorkflowConstants.STATUS_SCHEDULED);

		for (CommercePriceEntry commercePriceEntry : commercePriceEntries) {
			long userId = _portal.getValidUserId(
				commercePriceEntry.getCompanyId(),
				commercePriceEntry.getUserId());

			ServiceContext serviceContext = new ServiceContext();

			serviceContext.setCommand(Constants.UPDATE);

			CommercePriceList commercePriceList =
				commercePriceEntry.getCommercePriceList();

			serviceContext.setScopeGroupId(commercePriceList.getGroupId());

			commercePriceEntryLocalService.updateStatus(
				userId, commercePriceEntry.getCommercePriceEntryId(),
				WorkflowConstants.STATUS_APPROVED, serviceContext,
				new HashMap<String, Serializable>());
		}
	}

	private void _checkCommercePriceEntriesByExpirationDate()
		throws PortalException {

		List<CommercePriceEntry> commercePriceEntries =
			commercePriceEntryPersistence.findByLtE_S(
				new Date(), WorkflowConstants.STATUS_APPROVED);

		if (_log.isDebugEnabled()) {
			_log.debug(
				"Expiring " + commercePriceEntries.size() +
					" commerce price entries");
		}

		if ((commercePriceEntries != null) && !commercePriceEntries.isEmpty()) {
			for (CommercePriceEntry commercePriceEntry : commercePriceEntries) {
				long userId = _portal.getValidUserId(
					commercePriceEntry.getCompanyId(),
					commercePriceEntry.getUserId());

				ServiceContext serviceContext = new ServiceContext();

				serviceContext.setCommand(Constants.UPDATE);

				CommercePriceList commercePriceList =
					commercePriceEntry.getCommercePriceList();

				serviceContext.setScopeGroupId(commercePriceList.getGroupId());

				commercePriceEntryLocalService.updateStatus(
					userId, commercePriceEntry.getCommercePriceEntryId(),
					WorkflowConstants.STATUS_EXPIRED, serviceContext,
					new HashMap<String, Serializable>());
			}
		}
	}

	private List<CommercePriceEntry> _getCommercePriceEntries(Hits hits)
		throws PortalException {

		List<Document> documents = hits.toList();

		List<CommercePriceEntry> commercePriceEntries = new ArrayList<>(
			documents.size());

		for (Document document : documents) {
			long commercePriceEntryId = GetterUtil.getLong(
				document.get(Field.ENTRY_CLASS_PK));

			CommercePriceEntry commercePriceEntry = fetchCommercePriceEntry(
				commercePriceEntryId);

			if (commercePriceEntry == null) {
				commercePriceEntries = null;

				Indexer<CommercePriceEntry> indexer =
					IndexerRegistryUtil.getIndexer(CommercePriceEntry.class);

				long companyId = GetterUtil.getLong(
					document.get(Field.COMPANY_ID));

				indexer.delete(companyId, document.getUID());
			}
			else if (commercePriceEntries != null) {
				commercePriceEntries.add(commercePriceEntry);
			}
		}

		return commercePriceEntries;
	}

	private CPInstanceUnitOfMeasure _getCPInstanceUnitOfMeasure(
		long cpInstanceId, String unitOfMeasureKey) {

		if (!Validator.isBlank(unitOfMeasureKey)) {
			return _cpInstanceUnitOfMeasureLocalService.
				fetchCPInstanceUnitOfMeasure(cpInstanceId, unitOfMeasureKey);
		}

		int count =
			_cpInstanceUnitOfMeasureLocalService.
				getCPInstanceUnitOfMeasuresCount(cpInstanceId);

		if (count == 1) {
			return _cpInstanceUnitOfMeasureLocalService.
				fetchPrimaryCPInstanceUnitOfMeasure(cpInstanceId);
		}

		return null;
	}

	private GroupByStep _getGroupByStep(
		FromStep fromStep, long commercePriceListId, String cpInstanceUuid,
		int status, String unitOfMeasureKey) {

		return fromStep.from(
			CommercePriceEntryTable.INSTANCE
		).where(
			CommercePriceEntryTable.INSTANCE.commercePriceListId.eq(
				commercePriceListId
			).and(
				CommercePriceEntryTable.INSTANCE.CPInstanceUuid.eq(
					cpInstanceUuid)
			).and(
				() -> {
					if (status == WorkflowConstants.STATUS_ANY) {
						return CommercePriceEntryTable.INSTANCE.status.neq(
							WorkflowConstants.STATUS_IN_TRASH);
					}

					return CommercePriceEntryTable.INSTANCE.status.eq(status);
				}
			).and(
				() -> {
					if (Validator.isNull(unitOfMeasureKey)) {
						return null;
					}

					return CommercePriceEntryTable.INSTANCE.unitOfMeasureKey.eq(
						unitOfMeasureKey);
				}
			)
		);
	}

	private void _reindexCPDefinition(long cpDefinitionId)
		throws PortalException {

		Indexer<CPDefinition> indexer = IndexerRegistryUtil.nullSafeGetIndexer(
			CPDefinition.class);

		indexer.reindex(CPDefinition.class.getName(), cpDefinitionId);
	}

	private BaseModelSearchResult<CommercePriceEntry>
			_searchCommercePriceEntries(SearchContext searchContext)
		throws PortalException {

		Indexer<CommercePriceEntry> indexer =
			IndexerRegistryUtil.nullSafeGetIndexer(CommercePriceEntry.class);

		for (int i = 0; i < 10; i++) {
			Hits hits = indexer.search(searchContext, _SELECTED_FIELD_NAMES);

			List<CommercePriceEntry> commercePriceEntries =
				_getCommercePriceEntries(hits);

			if (commercePriceEntries != null) {
				return new BaseModelSearchResult<>(
					commercePriceEntries, hits.getLength());
			}
		}

		throw new SearchException(
			"Unable to fix the search index after 10 attempts");
	}

	private int _searchCommercePriceEntriesCount(SearchContext searchContext)
		throws PortalException {

		Indexer<CommercePriceEntry> indexer =
			IndexerRegistryUtil.nullSafeGetIndexer(CommercePriceEntry.class);

		return GetterUtil.getInteger(indexer.searchCount(searchContext));
	}

	private CommercePriceEntry _startWorkflowInstance(
			long userId, CommercePriceEntry commercePriceEntry,
			ServiceContext serviceContext)
		throws PortalException {

		Map<String, Serializable> workflowContext = new HashMap<>();

		return WorkflowHandlerRegistryUtil.startWorkflowInstance(
			commercePriceEntry.getCompanyId(), 0L, userId,
			CommercePriceEntry.class.getName(),
			commercePriceEntry.getCommercePriceEntryId(), commercePriceEntry,
			serviceContext, workflowContext);
	}

	private void _validatePrice(
			BigDecimal price, BigDecimal discountLevel1,
			BigDecimal discountLevel2, BigDecimal discountLevel3,
			BigDecimal discountLevel4)
		throws PortalException {

		BigDecimal maxValue = BigDecimal.valueOf(
			GetterUtil.getDouble(CommercePriceConstants.PRICE_VALUE_MAX));

		if (((price != null) && (price.compareTo(maxValue) > 0)) ||
			((discountLevel1 != null) &&
			 (discountLevel1.compareTo(maxValue) > 0)) ||
			((discountLevel2 != null) &&
			 (discountLevel2.compareTo(maxValue) > 0)) ||
			((discountLevel3 != null) &&
			 (discountLevel3.compareTo(maxValue) > 0)) ||
			((discountLevel4 != null) &&
			 (discountLevel4.compareTo(maxValue) > 0))) {

			throw new CommercePriceListMaxPriceValueException();
		}

		BigDecimal minValue = BigDecimal.valueOf(
			GetterUtil.getDouble(CommercePriceConstants.PRICE_VALUE_MIN));

		if (((price != null) && (price.compareTo(minValue) < 0)) ||
			((discountLevel1 != null) &&
			 (discountLevel1.compareTo(minValue) < 0)) ||
			((discountLevel2 != null) &&
			 (discountLevel2.compareTo(minValue) < 0)) ||
			((discountLevel3 != null) &&
			 (discountLevel3.compareTo(minValue) < 0)) ||
			((discountLevel4 != null) &&
			 (discountLevel4.compareTo(minValue) < 0))) {

			throw new CommercePriceListMinPriceValueException();
		}
	}

	private void _validateUnitOfMeasureKey(
			long cpInstanceId, String unitOfMeasureKey)
		throws PortalException {

		int cpInstanceUnitOfMeasuresCount =
			_cpInstanceUnitOfMeasureLocalService.
				getCPInstanceUnitOfMeasuresCount(cpInstanceId);

		if ((cpInstanceUnitOfMeasuresCount > 1) &&
			Validator.isBlank(unitOfMeasureKey)) {

			throw new CommercePriceEntryUnitOfMeasureKeyException(
				"You must specify unit of measure key in order to price this " +
					"SKU");
		}

		if (!Validator.isBlank(unitOfMeasureKey)) {
			CPInstanceUnitOfMeasure cpInstanceUnitOfMeasure =
				_cpInstanceUnitOfMeasureLocalService.
					fetchCPInstanceUnitOfMeasure(
						cpInstanceId, unitOfMeasureKey);

			if (cpInstanceUnitOfMeasure == null) {
				throw new CommercePriceEntryUnitOfMeasureKeyException(
					"No unit of measure found with key: " + unitOfMeasureKey);
			}
		}
	}

	private static final String[] _SELECTED_FIELD_NAMES = {
		Field.ENTRY_CLASS_PK, Field.COMPANY_ID, Field.GROUP_ID, Field.UID
	};

	private static final Log _log = LogFactoryUtil.getLog(
		CommercePriceEntryLocalServiceImpl.class);

	@Reference
	private CommercePriceListPersistence _commercePriceListPersistence;

	@Reference
	private CPDefinitionLocalService _cpDefinitionLocalService;

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