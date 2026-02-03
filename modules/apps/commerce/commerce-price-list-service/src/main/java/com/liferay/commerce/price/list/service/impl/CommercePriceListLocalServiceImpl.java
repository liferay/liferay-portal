/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.price.list.service.impl;

import com.liferay.commerce.currency.exception.NoSuchCurrencyException;
import com.liferay.commerce.currency.model.CommerceCurrency;
import com.liferay.commerce.currency.model.CommerceCurrencyTable;
import com.liferay.commerce.currency.service.CommerceCurrencyLocalService;
import com.liferay.commerce.price.list.constants.CommercePriceListConstants;
import com.liferay.commerce.price.list.exception.CommercePriceListCurrencyException;
import com.liferay.commerce.price.list.exception.CommercePriceListDisplayDateException;
import com.liferay.commerce.price.list.exception.CommercePriceListExpirationDateException;
import com.liferay.commerce.price.list.exception.CommercePriceListParentPriceListGroupIdException;
import com.liferay.commerce.price.list.exception.DuplicateCommerceBasePriceListException;
import com.liferay.commerce.price.list.exception.NoSuchPriceListException;
import com.liferay.commerce.price.list.exception.RequiredCommerceBasePriceListException;
import com.liferay.commerce.price.list.model.CommercePriceEntry;
import com.liferay.commerce.price.list.model.CommercePriceEntryTable;
import com.liferay.commerce.price.list.model.CommercePriceList;
import com.liferay.commerce.price.list.model.CommercePriceListAccountRelTable;
import com.liferay.commerce.price.list.model.CommercePriceListChannelRelTable;
import com.liferay.commerce.price.list.model.CommercePriceListCommerceAccountGroupRelTable;
import com.liferay.commerce.price.list.model.CommercePriceListOrderTypeRelTable;
import com.liferay.commerce.price.list.model.CommercePriceListTable;
import com.liferay.commerce.price.list.service.CommercePriceEntryLocalService;
import com.liferay.commerce.price.list.service.CommercePriceListAccountRelLocalService;
import com.liferay.commerce.price.list.service.CommercePriceListChannelRelLocalService;
import com.liferay.commerce.price.list.service.CommercePriceListCommerceAccountGroupRelLocalService;
import com.liferay.commerce.price.list.service.CommercePriceListDiscountRelLocalService;
import com.liferay.commerce.price.list.service.CommercePriceListOrderTypeRelLocalService;
import com.liferay.commerce.price.list.service.base.CommercePriceListLocalServiceBaseImpl;
import com.liferay.commerce.price.list.service.persistence.CommercePriceEntryPersistence;
import com.liferay.commerce.pricing.exception.CommerceUndefinedBasePriceListException;
import com.liferay.commerce.pricing.model.CommercePriceModifier;
import com.liferay.commerce.pricing.service.CommercePriceModifierLocalService;
import com.liferay.commerce.pricing.type.CommercePriceModifierType;
import com.liferay.commerce.pricing.type.CommercePriceModifierTypeRegistry;
import com.liferay.commerce.product.model.CPDefinition;
import com.liferay.commerce.product.model.CommerceChannelRelTable;
import com.liferay.commerce.product.service.CPDefinitionLocalService;
import com.liferay.commerce.product.service.CommerceChannelAccountEntryRelLocalService;
import com.liferay.expando.kernel.service.ExpandoRowLocalService;
import com.liferay.petra.sql.dsl.DSLFunctionFactoryUtil;
import com.liferay.petra.sql.dsl.DSLQueryFactoryUtil;
import com.liferay.petra.sql.dsl.expression.Expression;
import com.liferay.petra.sql.dsl.expression.Predicate;
import com.liferay.petra.sql.dsl.query.FromStep;
import com.liferay.petra.sql.dsl.query.GroupByStep;
import com.liferay.petra.sql.dsl.query.JoinStep;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.aop.AopService;
import com.liferay.portal.kernel.cache.MultiVMPool;
import com.liferay.portal.kernel.cache.PortalCache;
import com.liferay.portal.kernel.dao.orm.QueryDefinition;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.ResourceConstants;
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
import com.liferay.portal.kernel.search.SortFactoryUtil;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.service.ClassNameLocalService;
import com.liferay.portal.kernel.service.ResourceLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.service.WorkflowInstanceLinkLocalService;
import com.liferay.portal.kernel.systemevent.SystemEvent;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.BigDecimalUtil;
import com.liferay.portal.kernel.util.CalendarFactoryUtil;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.LinkedHashMapBuilder;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.ProxyFactory;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.kernel.workflow.WorkflowHandlerRegistryUtil;

import java.io.Serializable;

import java.math.BigDecimal;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Marco Leo
 * @author Alessio Antonio Rendina
 * @author Zoltán Takács
 */
@Component(
	property = "model.class.name=com.liferay.commerce.price.list.model.CommercePriceList",
	service = AopService.class
)
public class CommercePriceListLocalServiceImpl
	extends CommercePriceListLocalServiceBaseImpl {

	@Override
	public CommercePriceList addCatalogBaseCommercePriceList(
			long groupId, long userId, String commerceCurrencyCode, String type,
			String name, ServiceContext serviceContext)
		throws PortalException {

		Date date = new Date();

		Calendar calendar = CalendarFactoryUtil.getCalendar(date.getTime());

		int displayDateHour = calendar.get(Calendar.HOUR);

		if (calendar.get(Calendar.AM_PM) == Calendar.PM) {
			displayDateHour += 12;
		}

		return commercePriceListLocalService.addCommercePriceList(
			null, userId, groupId, commerceCurrencyCode, true, type, 0L, true,
			name, 0D, calendar.get(Calendar.MONTH),
			calendar.get(Calendar.DAY_OF_MONTH), calendar.get(Calendar.YEAR),
			displayDateHour, calendar.get(Calendar.MINUTE), 0, 0, 0, 0, 0, true,
			serviceContext);
	}

	@Indexable(type = IndexableType.REINDEX)
	@Override
	public CommercePriceList addCommercePriceList(
			String externalReferenceCode, long userId, long groupId,
			String commerceCurrencyCode, boolean netPrice, String type,
			long parentCommercePriceListId, boolean catalogBasePriceList,
			String name, double priority, int displayDateMonth,
			int displayDateDay, int displayDateYear, int displayDateHour,
			int displayDateMinute, int expirationDateMonth,
			int expirationDateDay, int expirationDateYear,
			int expirationDateHour, int expirationDateMinute,
			boolean neverExpire, ServiceContext serviceContext)
		throws PortalException {

		// Commerce price list

		User user = _userLocalService.getUser(userId);

		_validate(
			catalogBasePriceList, commerceCurrencyCode, 0, user.getCompanyId(),
			groupId, neverExpire, parentCommercePriceListId, type);

		Date expirationDate = null;
		Date date = new Date();

		Date displayDate = _portal.getDate(
			displayDateMonth, displayDateDay, displayDateYear, displayDateHour,
			displayDateMinute, user.getTimeZone(),
			CommercePriceListDisplayDateException.class);

		if (!neverExpire) {
			expirationDate = _portal.getDate(
				expirationDateMonth, expirationDateDay, expirationDateYear,
				expirationDateHour, expirationDateMinute, user.getTimeZone(),
				CommercePriceListExpirationDateException.class);
		}

		long commercePriceListId = counterLocalService.increment();

		CommercePriceList commercePriceList =
			commercePriceListPersistence.create(commercePriceListId);

		commercePriceList.setExternalReferenceCode(externalReferenceCode);
		commercePriceList.setGroupId(groupId);
		commercePriceList.setCompanyId(user.getCompanyId());
		commercePriceList.setUserId(user.getUserId());
		commercePriceList.setUserName(user.getFullName());
		commercePriceList.setCommerceCurrencyCode(commerceCurrencyCode);
		commercePriceList.setParentCommercePriceListId(
			parentCommercePriceListId);
		commercePriceList.setCatalogBasePriceList(catalogBasePriceList);
		commercePriceList.setNetPrice(netPrice);
		commercePriceList.setType(type);
		commercePriceList.setName(name);
		commercePriceList.setPriority(priority);
		commercePriceList.setDisplayDate(displayDate);
		commercePriceList.setExpirationDate(expirationDate);

		if ((expirationDate == null) || expirationDate.after(date)) {
			commercePriceList.setStatus(WorkflowConstants.STATUS_DRAFT);
		}
		else {
			commercePriceList.setStatus(WorkflowConstants.STATUS_EXPIRED);
		}

		if (catalogBasePriceList) {
			commercePriceList.setStatus(WorkflowConstants.STATUS_APPROVED);
		}

		commercePriceList.setStatusByUserId(user.getUserId());
		commercePriceList.setStatusDate(serviceContext.getModifiedDate(date));
		commercePriceList.setExpandoBridgeAttributes(serviceContext);

		commercePriceList = commercePriceListPersistence.update(
			commercePriceList);

		// Workflow

		commercePriceList = _startWorkflowInstance(
			user.getUserId(), commercePriceList, serviceContext);

		// Resources

		_resourceLocalService.addModelResources(
			commercePriceList, serviceContext);

		return commercePriceList;
	}

	@Indexable(type = IndexableType.REINDEX)
	@Override
	public CommercePriceList addOrUpdateCommercePriceList(
			String externalReferenceCode, long userId, long groupId,
			long commercePriceListId, String commerceCurrencyCode,
			boolean netPrice, String type, long parentCommercePriceListId,
			boolean catalogBasePriceList, String name, double priority,
			int displayDateMonth, int displayDateDay, int displayDateYear,
			int displayDateHour, int displayDateMinute, int expirationDateMonth,
			int expirationDateDay, int expirationDateYear,
			int expirationDateHour, int expirationDateMinute,
			boolean neverExpire, ServiceContext serviceContext)
		throws PortalException {

		// Update

		if (commercePriceListId > 0) {
			try {
				return updateCommercePriceList(
					commercePriceListId, commerceCurrencyCode, netPrice, type,
					parentCommercePriceListId, catalogBasePriceList, name,
					priority, displayDateMonth, displayDateDay, displayDateYear,
					displayDateHour, displayDateMinute, expirationDateMonth,
					expirationDateDay, expirationDateYear, expirationDateHour,
					expirationDateMinute, neverExpire, serviceContext);
			}
			catch (NoSuchPriceListException noSuchPriceListException) {
				if (_log.isDebugEnabled()) {
					_log.debug(
						"Unable to find price list with ID: " +
							commercePriceListId,
						noSuchPriceListException);
				}
			}
		}

		if (Validator.isNotNull(externalReferenceCode)) {
			CommercePriceList commercePriceList =
				commercePriceListPersistence.fetchByERC_C(
					externalReferenceCode, serviceContext.getCompanyId());

			if (commercePriceList != null) {
				return commercePriceListLocalService.updateCommercePriceList(
					commercePriceList.getCommercePriceListId(),
					commerceCurrencyCode, netPrice, type,
					parentCommercePriceListId, catalogBasePriceList, name,
					priority, displayDateMonth, displayDateDay, displayDateYear,
					displayDateHour, displayDateMinute, expirationDateMonth,
					expirationDateDay, expirationDateYear, expirationDateHour,
					expirationDateMinute, neverExpire, serviceContext);
			}
		}

		// Add

		return commercePriceListLocalService.addCommercePriceList(
			externalReferenceCode, userId, groupId, commerceCurrencyCode,
			netPrice, type, parentCommercePriceListId, catalogBasePriceList,
			name, priority, displayDateMonth, displayDateDay, displayDateYear,
			displayDateHour, displayDateMinute, expirationDateMonth,
			expirationDateDay, expirationDateYear, expirationDateHour,
			expirationDateMinute, neverExpire, serviceContext);
	}

	@Override
	public void checkCommercePriceLists() throws PortalException {
		_checkCommercePriceListsByDisplayDate();
		_checkCommercePriceListsByExpirationDate();
	}

	@Override
	public void cleanPriceListCache() {
		_portalCache.removeAll();
	}

	@Indexable(type = IndexableType.DELETE)
	@Override
	@SystemEvent(type = SystemEventConstants.TYPE_DELETE)
	public CommercePriceList deleteCommercePriceList(
			CommercePriceList commercePriceList)
		throws PortalException {

		if (commercePriceList.isCatalogBasePriceList()) {
			throw new RequiredCommerceBasePriceListException();
		}

		return commercePriceListLocalService.forceDeleteCommercePriceList(
			commercePriceList);
	}

	@Override
	public CommercePriceList deleteCommercePriceList(long commercePriceListId)
		throws PortalException {

		CommercePriceList commercePriceList =
			commercePriceListPersistence.findByPrimaryKey(commercePriceListId);

		return commercePriceListLocalService.deleteCommercePriceList(
			commercePriceList);
	}

	@Override
	public void deleteCommercePriceLists(long companyId)
		throws PortalException {

		List<CommercePriceList> commercePriceLists =
			commercePriceListLocalService.getCommercePriceLists(
				companyId, QueryUtil.ALL_POS, QueryUtil.ALL_POS);

		for (CommercePriceList commercePriceList : commercePriceLists) {
			commercePriceListLocalService.forceDeleteCommercePriceList(
				commercePriceList);
		}
	}

	@Override
	public CommercePriceList fetchCatalogBaseCommercePriceList(long groupId) {
		return commercePriceListPersistence.fetchByG_CBPL_T_First(
			groupId, true, CommercePriceListConstants.TYPE_PRICE_LIST, null);
	}

	@Override
	public CommercePriceList fetchCatalogBaseCommercePriceListByType(
		long groupId, String type) {

		return commercePriceListPersistence.fetchByG_CBPL_T_First(
			groupId, true, type, null);
	}

	@Indexable(type = IndexableType.DELETE)
	@Override
	@SystemEvent(type = SystemEventConstants.TYPE_DELETE)
	public CommercePriceList forceDeleteCommercePriceList(
			CommercePriceList commercePriceList)
		throws PortalException {

		commercePriceListPersistence.remove(commercePriceList);

		_resourceLocalService.deleteResource(
			commercePriceList, ResourceConstants.SCOPE_INDIVIDUAL);

		_commerceChannelAccountEntryRelLocalService.
			deleteCommerceChannelAccountEntryRels(
				CommercePriceList.class.getName(),
				commercePriceList.getCommercePriceListId());

		_commercePriceEntryLocalService.deleteCommercePriceEntries(
			commercePriceList.getCommercePriceListId());

		_commercePriceListAccountRelLocalService.
			deleteCommercePriceListAccountRels(
				commercePriceList.getCommercePriceListId());

		_commercePriceListChannelRelLocalService.
			deleteCommercePriceListChannelRels(
				commercePriceList.getCommercePriceListId());

		_commercePriceListCommerceAccountGroupRelLocalService.
			deleteCommercePriceListCommerceAccountGroupRels(
				commercePriceList.getCommercePriceListId());

		_commercePriceListDiscountRelLocalService.
			deleteCommercePriceListDiscountRels(
				commercePriceList.getCommercePriceListId());

		_commercePriceListOrderTypeRelLocalService.
			deleteCommercePriceListOrderTypeRels(
				commercePriceList.getCommercePriceListId());

		_commercePriceModifierLocalService.
			deleteCommercePriceModifiersByCommercePriceListId(
				commercePriceList.getCommercePriceListId());

		_expandoRowLocalService.deleteRows(
			commercePriceList.getCommercePriceListId());

		_workflowInstanceLinkLocalService.deleteWorkflowInstanceLinks(
			commercePriceList.getCompanyId(), commercePriceList.getGroupId(),
			CommercePriceList.class.getName(),
			commercePriceList.getCommercePriceListId());

		return commercePriceList;
	}

	@Override
	public CommercePriceList getCatalogBaseCommercePriceList(long groupId)
		throws PortalException {

		CommercePriceList commercePriceList =
			commercePriceListPersistence.fetchByG_CBPL_T_First(
				groupId, true, CommercePriceListConstants.TYPE_PRICE_LIST,
				null);

		if (commercePriceList == null) {
			throw new CommerceUndefinedBasePriceListException();
		}

		return commercePriceList;
	}

	@Override
	public CommercePriceList getCatalogBaseCommercePriceListByType(
			long groupId, String type)
		throws PortalException {

		CommercePriceList commercePriceList =
			commercePriceListPersistence.fetchByG_CBPL_T_First(
				groupId, true, type, null);

		if (commercePriceList == null) {
			throw new CommerceUndefinedBasePriceListException();
		}

		return commercePriceList;
	}

	@Override
	public CommercePriceList getCommercePriceList(
			long groupId, long commerceAccountId,
			long[] commerceAccountGroupIds)
		throws PortalException {

		if (commerceAccountGroupIds == null) {
			commerceAccountGroupIds = new long[0];
		}
		else if (commerceAccountGroupIds.length > 1) {
			commerceAccountGroupIds = ArrayUtil.unique(commerceAccountGroupIds);

			Arrays.sort(commerceAccountGroupIds);
		}

		String cacheKey = StringBundler.concat(
			groupId, StringPool.POUND, commerceAccountId, StringPool.POUND,
			StringUtil.merge(commerceAccountGroupIds));

		CommercePriceList commercePriceList = _portalCache.get(cacheKey);

		if (commercePriceList == _dummyCommercePriceList) {
			return null;
		}
		else if (commercePriceList != null) {
			return commercePriceList;
		}

		SearchContext searchContext = _buildSearchContext(
			CompanyThreadLocal.getCompanyId(), groupId, commerceAccountId,
			commerceAccountGroupIds);

		Indexer<CommercePriceList> indexer =
			IndexerRegistryUtil.nullSafeGetIndexer(CommercePriceList.class);

		Hits hits = indexer.search(searchContext, Field.ENTRY_CLASS_PK);

		List<Document> documents = hits.toList();

		if (documents.isEmpty()) {
			_portalCache.put(cacheKey, _dummyCommercePriceList);

			return null;
		}

		Document document = documents.get(0);

		long commercePriceListId = GetterUtil.getLong(
			document.get(Field.ENTRY_CLASS_PK));

		commercePriceList = fetchCommercePriceList(commercePriceListId);

		if (commercePriceList == null) {
			_portalCache.put(cacheKey, _dummyCommercePriceList);

			return null;
		}

		_portalCache.put(cacheKey, commercePriceList);

		return commercePriceList;
	}

	/**
	 * @deprecated As of Cavanaugh (7.4.x)
	 */
	@Deprecated
	@Override
	public CommercePriceList getCommercePriceListByAccountAndChannelId(
		long groupId, long commerceAccountId, long commerceChannelId,
		String type) {

		List<CommercePriceList> commercePriceLists =
			commercePriceListLocalService.
				getCommercePriceListsByAccountAndChannelId(
					groupId, commerceAccountId, commerceChannelId, null, type);

		if (commercePriceLists.isEmpty()) {
			return null;
		}

		return commercePriceLists.get(0);
	}

	/**
	 * @deprecated As of Cavanaugh (7.4.x)
	 */
	@Deprecated
	@Override
	public CommercePriceList getCommercePriceListByAccountGroupIds(
		long groupId, long[] commerceAccountGroupIds, String type) {

		List<CommercePriceList> commercePriceLists =
			commercePriceListLocalService.
				getCommercePriceListsByAccountGroupIds(
					groupId, commerceAccountGroupIds, null, type);

		if (commercePriceLists.isEmpty()) {
			return null;
		}

		return commercePriceLists.get(0);
	}

	/**
	 * @deprecated As of Cavanaugh (7.4.x)
	 */
	@Deprecated
	@Override
	public CommercePriceList getCommercePriceListByAccountGroupsAndChannelId(
		long groupId, long[] commerceAccountGroupIds, long commerceChannelId,
		String type) {

		List<CommercePriceList> commercePriceLists =
			commercePriceListLocalService.
				getCommercePriceListsByAccountGroupsAndChannelId(
					groupId, commerceAccountGroupIds, commerceChannelId, null,
					type);

		if (commercePriceLists.isEmpty()) {
			return null;
		}

		return commercePriceLists.get(0);
	}

	/**
	 * @deprecated As of Cavanaugh (7.4.x)
	 */
	@Deprecated
	@Override
	public CommercePriceList getCommercePriceListByAccountId(
		long groupId, long commerceAccountId, String type) {

		List<CommercePriceList> commercePriceLists =
			commercePriceListLocalService.getCommercePriceListsByAccountId(
				groupId, commerceAccountId, null, type);

		if (commercePriceLists.isEmpty()) {
			return null;
		}

		return commercePriceLists.get(0);
	}

	/**
	 * @deprecated As of Cavanaugh (7.4.x)
	 */
	@Deprecated
	@Override
	public CommercePriceList getCommercePriceListByChannelId(
		long groupId, long commerceChannelId, String type) {

		List<CommercePriceList> commercePriceLists =
			commercePriceListLocalService.getCommercePriceListsByChannelId(
				groupId, commerceChannelId, null, type);

		if (commercePriceLists.isEmpty()) {
			return null;
		}

		return commercePriceLists.get(0);
	}

	@Override
	public CommercePriceList getCommercePriceListByLowestPrice(
			long groupId, long commerceAccountId,
			long[] commerceAccountGroupIds, long commerceChannelId,
			long commerceOrderTypeId, String cpInstanceUuid,
			String currencyCode, String type, String unitOfMeasureKey)
		throws PortalException {

		Expression<Float> expression = DSLFunctionFactoryUtil.floatDivide(
			CommercePriceEntryTable.INSTANCE.price,
			CommerceCurrencyTable.INSTANCE.rate
		).as(
			"convertedPrice"
		);

		List<Object[]> results = _commercePriceEntryPersistence.dslQuery(
			_getGroupByStep(
				DSLQueryFactoryUtil.select(
					CommercePriceListTable.INSTANCE.commercePriceListId,
					expression,
					CommercePriceEntryTable.INSTANCE.priceOnApplication,
					CommercePriceEntryTable.INSTANCE.CProductId),
				groupId, commerceAccountId, commerceAccountGroupIds,
				commerceChannelId, commerceOrderTypeId, cpInstanceUuid,
				currencyCode, type, unitOfMeasureKey
			).orderBy(
				CommercePriceEntryTable.INSTANCE.priceOnApplication.ascending(),
				expression.ascending()
			).limit(
				0, 1
			));

		if (results.isEmpty()) {
			return null;
		}

		Object[] result = results.get(0);

		if (result[0] == null) {
			return null;
		}

		long commercePriceListId = (Long)result[0];

		if (type.equals(CommercePriceListConstants.TYPE_PROMOTION)) {
			List<CommercePriceList> commercePromoPriceLists =
				commercePriceListLocalService.
					getCommercePriceListsByUnqualified(
						groupId, currencyCode, type);

			if (commercePromoPriceLists.size() <= 1) {
				return commercePriceListLocalService.getCommercePriceList(
					commercePriceListId);
			}

			CommercePriceList actualCommercePriceList =
				commercePriceListLocalService.getCatalogBaseCommercePriceList(
					groupId);

			CommercePriceEntry commercePriceEntry = null;

			if (actualCommercePriceList != null) {
				commercePriceEntry =
					_commercePriceEntryLocalService.fetchCommercePriceEntry(
						actualCommercePriceList.getCommercePriceListId(),
						cpInstanceUuid, unitOfMeasureKey, true);
			}

			if (commercePriceEntry == null) {
				List<CommercePriceList> commercePriceLists =
					commercePriceListLocalService.
						getCommercePriceListsByUnqualified(
							groupId, currencyCode,
							CommercePriceListConstants.TYPE_PRICE_LIST);

				if (commercePriceLists.isEmpty()) {
					return commercePriceListLocalService.getCommercePriceList(
						commercePriceListId);
				}

				actualCommercePriceList = commercePriceLists.get(0);

				commercePriceEntry =
					_commercePriceEntryLocalService.fetchCommercePriceEntry(
						actualCommercePriceList.getCommercePriceListId(),
						cpInstanceUuid, unitOfMeasureKey, true);
			}

			if (commercePriceEntry == null) {
				return commercePriceListLocalService.getCommercePriceList(
					commercePriceListId);
			}

			if (result[3] == null) {
				return null;
			}

			BigDecimal originalPrice = commercePriceEntry.getPrice();

			BigDecimal lowestPrice = originalPrice;

			if (result[1] != null) {
				BigDecimal price = new BigDecimal(String.valueOf(result[1]));

				if (!BigDecimalUtil.eq(BigDecimal.ZERO, price)) {
					lowestPrice = price;
				}
			}

			CPDefinition cpDefinition =
				_cpDefinitionLocalService.getCPDefinitionByCProductId(
					(Long)result[3]);

			for (CommercePriceList commercePriceList :
					commercePromoPriceLists) {

				if (commercePriceList.getCommercePriceListId() ==
						commercePriceListId) {

					continue;
				}

				for (CommercePriceModifier commercePriceModifier :
						_commercePriceModifierLocalService.
							getQualifiedCommercePriceModifiers(
								commercePriceList.getCommercePriceListId(),
								cpDefinition.getCPDefinitionId())) {

					CommercePriceModifierType commercePriceModifierType =
						_commercePriceModifierTypeRegistry.
							getCommercePriceModifierType(
								commercePriceModifier.getModifierType());

					BigDecimal actualPrice = commercePriceModifierType.evaluate(
						originalPrice, commercePriceModifier);

					if (BigDecimalUtil.lt(actualPrice, lowestPrice)) {
						commercePriceListId =
							commercePriceList.getCommercePriceListId();
						lowestPrice = actualPrice;
					}
				}
			}
		}

		return commercePriceListLocalService.getCommercePriceList(
			commercePriceListId);
	}

	/**
	 * @deprecated As of Cavanaugh (7.4.x)
	 */
	@Deprecated
	@Override
	public CommercePriceList getCommercePriceListByUnqualified(
		long groupId, String type) {

		List<CommercePriceList> commercePriceLists =
			commercePriceListLocalService.getCommercePriceListsByUnqualified(
				groupId, null, type);

		if (commercePriceLists.isEmpty()) {
			return null;
		}

		return commercePriceLists.get(0);
	}

	@Override
	public List<CommercePriceList> getCommercePriceLists(
		long companyId, int start, int end) {

		return commercePriceListPersistence.findByCompanyId(
			companyId, start, end);
	}

	@Override
	public List<CommercePriceList> getCommercePriceLists(
		long[] groupIds, long companyId, int start, int end) {

		return commercePriceListPersistence.findByG_C(
			groupIds, companyId, start, end);
	}

	@Override
	public List<CommercePriceList> getCommercePriceLists(
		long[] groupIds, long companyId, int status, int start, int end,
		OrderByComparator<CommercePriceList> orderByComparator) {

		if (status == WorkflowConstants.STATUS_ANY) {
			return commercePriceListPersistence.findByG_C_NotS(
				groupIds, companyId, WorkflowConstants.STATUS_IN_TRASH, start,
				end, orderByComparator);
		}

		return commercePriceListPersistence.findByG_C_S(
			groupIds, companyId, status, start, end, orderByComparator);
	}

	@Override
	public List<CommercePriceList>
		getCommercePriceListsByAccountAndChannelAndOrderTypeId(
			long groupId, long commerceAccountId, long commerceChannelId,
			long commerceOrderTypeId, String currencyCode, String type) {

		return dslQuery(
			_getGroupByStep(
				DSLQueryFactoryUtil.selectDistinct(
					CommercePriceListTable.INSTANCE),
				groupId, commerceAccountId, null, commerceChannelId,
				commerceOrderTypeId, currencyCode, type
			).orderBy(
				CommercePriceListTable.INSTANCE.priority.descending()
			));
	}

	@Override
	public List<CommercePriceList> getCommercePriceListsByAccountAndChannelId(
		long groupId, long commerceAccountId, long commerceChannelId,
		String currencyCode, String type) {

		return dslQuery(
			_getGroupByStep(
				DSLQueryFactoryUtil.selectDistinct(
					CommercePriceListTable.INSTANCE),
				groupId, commerceAccountId, null, commerceChannelId, null,
				currencyCode, type
			).orderBy(
				CommercePriceListTable.INSTANCE.priority.descending()
			));
	}

	@Override
	public List<CommercePriceList> getCommercePriceListsByAccountAndOrderTypeId(
		long groupId, long commerceAccountId, long commerceOrderTypeId,
		String currencyCode, String type) {

		return dslQuery(
			_getGroupByStep(
				DSLQueryFactoryUtil.selectDistinct(
					CommercePriceListTable.INSTANCE),
				groupId, commerceAccountId, null, null, commerceOrderTypeId,
				currencyCode, type
			).orderBy(
				CommercePriceListTable.INSTANCE.priority.descending()
			));
	}

	@Override
	public List<CommercePriceList> getCommercePriceListsByAccountGroupIds(
		long groupId, long[] commerceAccountGroupIds, String currencyCode,
		String type) {

		return dslQuery(
			_getGroupByStep(
				DSLQueryFactoryUtil.selectDistinct(
					CommercePriceListTable.INSTANCE),
				groupId, null, commerceAccountGroupIds, null, null,
				currencyCode, type
			).orderBy(
				CommercePriceListTable.INSTANCE.priority.descending()
			));
	}

	@Override
	public List<CommercePriceList>
		getCommercePriceListsByAccountGroupsAndChannelAndOrderTypeId(
			long groupId, long[] commerceAccountGroupIds,
			long commerceChannelId, long commerceOrderTypeId,
			String currencyCode, String type) {

		return dslQuery(
			_getGroupByStep(
				DSLQueryFactoryUtil.selectDistinct(
					CommercePriceListTable.INSTANCE),
				groupId, null, commerceAccountGroupIds, commerceChannelId,
				commerceOrderTypeId, currencyCode, type
			).orderBy(
				CommercePriceListTable.INSTANCE.priority.descending()
			));
	}

	@Override
	public List<CommercePriceList>
		getCommercePriceListsByAccountGroupsAndChannelId(
			long groupId, long[] commerceAccountGroupIds,
			long commerceChannelId, String currencyCode, String type) {

		return dslQuery(
			_getGroupByStep(
				DSLQueryFactoryUtil.selectDistinct(
					CommercePriceListTable.INSTANCE),
				groupId, null, commerceAccountGroupIds, commerceChannelId, null,
				currencyCode, type
			).orderBy(
				CommercePriceListTable.INSTANCE.priority.descending()
			));
	}

	@Override
	public List<CommercePriceList>
		getCommercePriceListsByAccountGroupsAndOrderTypeId(
			long groupId, long[] commerceAccountGroupIds,
			long commerceOrderTypeId, String currencyCode, String type) {

		return dslQuery(
			_getGroupByStep(
				DSLQueryFactoryUtil.selectDistinct(
					CommercePriceListTable.INSTANCE),
				groupId, null, commerceAccountGroupIds, null,
				commerceOrderTypeId, currencyCode, type
			).orderBy(
				CommercePriceListTable.INSTANCE.priority.descending()
			));
	}

	@Override
	public List<CommercePriceList> getCommercePriceListsByAccountId(
		long groupId, long commerceAccountId, String currencyCode,
		String type) {

		return dslQuery(
			_getGroupByStep(
				DSLQueryFactoryUtil.selectDistinct(
					CommercePriceListTable.INSTANCE),
				groupId, commerceAccountId, null, null, null, currencyCode, type
			).orderBy(
				CommercePriceListTable.INSTANCE.priority.descending()
			));
	}

	@Override
	public List<CommercePriceList> getCommercePriceListsByChannelAndOrderTypeId(
		long groupId, long commerceChannelId, long commerceOrderTypeId,
		String currencyCode, String type) {

		return dslQuery(
			_getGroupByStep(
				DSLQueryFactoryUtil.selectDistinct(
					CommercePriceListTable.INSTANCE),
				groupId, null, null, commerceChannelId, commerceOrderTypeId,
				currencyCode, type
			).orderBy(
				CommercePriceListTable.INSTANCE.priority.descending()
			));
	}

	@Override
	public List<CommercePriceList> getCommercePriceListsByChannelId(
		long groupId, long commerceChannelId, String currencyCode,
		String type) {

		return dslQuery(
			_getGroupByStep(
				DSLQueryFactoryUtil.selectDistinct(
					CommercePriceListTable.INSTANCE),
				groupId, null, null, commerceChannelId, null, currencyCode, type
			).orderBy(
				CommercePriceListTable.INSTANCE.priority.descending()
			));
	}

	@Override
	public List<CommercePriceList> getCommercePriceListsByOrderTypeId(
		long groupId, long commerceOrderTypeId, String currencyCode,
		String type) {

		return dslQuery(
			_getGroupByStep(
				DSLQueryFactoryUtil.selectDistinct(
					CommercePriceListTable.INSTANCE),
				groupId, null, null, null, commerceOrderTypeId, currencyCode,
				type
			).orderBy(
				CommercePriceListTable.INSTANCE.priority.descending()
			));
	}

	@Override
	public List<CommercePriceList> getCommercePriceListsByUnqualified(
		long groupId, String currencyCode, String type) {

		return dslQuery(
			_getGroupByStep(
				DSLQueryFactoryUtil.selectDistinct(
					CommercePriceListTable.INSTANCE),
				groupId, null, null, null, null, currencyCode, type
			).orderBy(
				CommercePriceListTable.INSTANCE.priority.descending(),
				CommercePriceListTable.INSTANCE.catalogBasePriceList.ascending()
			));
	}

	@Override
	public int getCommercePriceListsCount(
		long commercePricingClassId, String name) {

		return commercePriceListFinder.countByCommercePricingClassId(
			commercePricingClassId, name);
	}

	@Override
	public int getCommercePriceListsCount(
		long[] groupIds, long companyId, int status) {

		if (status == WorkflowConstants.STATUS_ANY) {
			return commercePriceListPersistence.countByG_C_NotS(
				groupIds, companyId, WorkflowConstants.STATUS_IN_TRASH);
		}

		return commercePriceListPersistence.countByG_C_S(
			groupIds, companyId, status);
	}

	@Override
	public Hits search(SearchContext searchContext) {
		try {
			Indexer<CommercePriceList> indexer =
				IndexerRegistryUtil.nullSafeGetIndexer(CommercePriceList.class);

			return indexer.search(searchContext);
		}
		catch (Exception exception) {
			throw new SystemException(exception);
		}
	}

	@Override
	public List<CommercePriceList> searchByCommercePricingClassId(
		long commercePricingClassId, String name, int start, int end) {

		return commercePriceListFinder.findByCommercePricingClassId(
			commercePricingClassId, name, start, end);
	}

	@Override
	public BaseModelSearchResult<CommercePriceList> searchCommercePriceLists(
			long companyId, long[] groupIds, String keywords, int status,
			int start, int end, Sort sort)
		throws PortalException {

		SearchContext searchContext = _buildSearchContext(
			companyId, groupIds, keywords, status, start, end, sort);

		return _searchCommercePriceLists(searchContext);
	}

	@Override
	public int searchCommercePriceListsCount(
			long companyId, long[] groupIds, String keywords, int status)
		throws PortalException {

		SearchContext searchContext = _buildSearchContext(
			companyId, groupIds, keywords, status, QueryUtil.ALL_POS,
			QueryUtil.ALL_POS, null);

		return _searchCommercePriceListsCount(searchContext);
	}

	@Indexable(type = IndexableType.REINDEX)
	@Override
	public CommercePriceList setCatalogBasePriceList(
			long commercePriceListId, boolean catalogBasePriceList)
		throws PortalException {

		CommercePriceList commercePriceList =
			commercePriceListPersistence.findByPrimaryKey(commercePriceListId);

		commercePriceList.setCatalogBasePriceList(catalogBasePriceList);

		return commercePriceListPersistence.update(commercePriceList);
	}

	@Override
	public void setCatalogBasePriceList(
			long groupId, long commercePriceListId, String type)
		throws PortalException {

		CommercePriceList baseCommercePriceList =
			commercePriceListPersistence.fetchByG_CBPL_T_First(
				groupId, true, type, null);

		if (baseCommercePriceList != null) {
			commercePriceListLocalService.setCatalogBasePriceList(
				baseCommercePriceList.getCommercePriceListId(), false);
		}

		commercePriceListLocalService.setCatalogBasePriceList(
			commercePriceListId, true);
	}

	@Indexable(type = IndexableType.REINDEX)
	@Override
	public CommercePriceList updateCommercePriceList(
			long commercePriceListId, String commerceCurrencyCode,
			boolean netPrice, long parentCommercePriceListId, String name,
			double priority, int displayDateMonth, int displayDateDay,
			int displayDateYear, int displayDateHour, int displayDateMinute,
			int expirationDateMonth, int expirationDateDay,
			int expirationDateYear, int expirationDateHour,
			int expirationDateMinute, boolean neverExpire,
			ServiceContext serviceContext)
		throws PortalException {

		CommercePriceList commercePriceList =
			commercePriceListPersistence.findByPrimaryKey(commercePriceListId);

		return commercePriceListLocalService.updateCommercePriceList(
			commercePriceListId, commerceCurrencyCode, netPrice,
			commercePriceList.getType(), parentCommercePriceListId,
			commercePriceList.isCatalogBasePriceList(), name, priority,
			displayDateMonth, displayDateDay, displayDateYear, displayDateHour,
			displayDateMinute, expirationDateMonth, expirationDateDay,
			expirationDateYear, expirationDateHour, expirationDateMinute,
			neverExpire, serviceContext);
	}

	@Indexable(type = IndexableType.REINDEX)
	@Override
	public CommercePriceList updateCommercePriceList(
			long commercePriceListId, String commerceCurrencyCode,
			boolean netPrice, String type, long parentCommercePriceListId,
			boolean catalogBasePriceList, String name, double priority,
			int displayDateMonth, int displayDateDay, int displayDateYear,
			int displayDateHour, int displayDateMinute, int expirationDateMonth,
			int expirationDateDay, int expirationDateYear,
			int expirationDateHour, int expirationDateMinute,
			boolean neverExpire, ServiceContext serviceContext)
		throws PortalException {

		// Commerce price list

		User user = _userLocalService.getUser(serviceContext.getUserId());

		CommercePriceList commercePriceList =
			commercePriceListPersistence.findByPrimaryKey(commercePriceListId);

		_validate(
			catalogBasePriceList, commerceCurrencyCode, commercePriceListId,
			commercePriceList.getCompanyId(), commercePriceList.getGroupId(),
			neverExpire, parentCommercePriceListId, type);

		Date expirationDate = null;
		Date date = new Date();

		Date displayDate = _portal.getDate(
			displayDateMonth, displayDateDay, displayDateYear, displayDateHour,
			displayDateMinute, user.getTimeZone(),
			CommercePriceListDisplayDateException.class);

		if (!neverExpire) {
			expirationDate = _portal.getDate(
				expirationDateMonth, expirationDateDay, expirationDateYear,
				expirationDateHour, expirationDateMinute, user.getTimeZone(),
				CommercePriceListExpirationDateException.class);
		}

		commercePriceList.setCommerceCurrencyCode(commerceCurrencyCode);
		commercePriceList.setParentCommercePriceListId(
			parentCommercePriceListId);
		commercePriceList.setCatalogBasePriceList(catalogBasePriceList);
		commercePriceList.setNetPrice(netPrice);
		commercePriceList.setType(type);
		commercePriceList.setName(name);
		commercePriceList.setPriority(priority);
		commercePriceList.setDisplayDate(displayDate);
		commercePriceList.setExpirationDate(expirationDate);

		if ((expirationDate == null) || expirationDate.after(date)) {
			commercePriceList.setStatus(WorkflowConstants.STATUS_DRAFT);
		}
		else {
			commercePriceList.setStatus(WorkflowConstants.STATUS_EXPIRED);
		}

		commercePriceList.setStatusByUserId(user.getUserId());
		commercePriceList.setStatusDate(serviceContext.getModifiedDate(date));
		commercePriceList.setExpandoBridgeAttributes(serviceContext);

		commercePriceList = commercePriceListPersistence.update(
			commercePriceList);

		// Workflow

		commercePriceList = _startWorkflowInstance(
			user.getUserId(), commercePriceList, serviceContext);

		return commercePriceList;
	}

	@Override
	public void updateCommercePriceListCurrencies(
			long companyId, String oldCommerceCurrencyCode,
			String newCommerceCurrencyCode)
		throws PortalException {

		List<CommercePriceList> commercePriceLists =
			commercePriceListPersistence.findByC_C(
				companyId, oldCommerceCurrencyCode);

		for (CommercePriceList commercePriceList : commercePriceLists) {
			commercePriceList.setCommerceCurrencyCode(newCommerceCurrencyCode);

			commercePriceList = commercePriceListPersistence.update(
				commercePriceList);

			_reindex(commercePriceList.getCommercePriceListId());
		}
	}

	@Indexable(type = IndexableType.REINDEX)
	@Override
	public CommercePriceList updateExternalReferenceCode(
			CommercePriceList commercePriceList, String externalReferenceCode)
		throws PortalException {

		commercePriceList.setExternalReferenceCode(externalReferenceCode);

		return commercePriceListPersistence.update(commercePriceList);
	}

	@Indexable(type = IndexableType.REINDEX)
	@Override
	public CommercePriceList updateStatus(
			long userId, long commercePriceListId, int status,
			ServiceContext serviceContext,
			Map<String, Serializable> workflowContext)
		throws PortalException {

		User user = _userLocalService.getUser(userId);
		Date date = new Date();

		CommercePriceList commercePriceList =
			commercePriceListPersistence.findByPrimaryKey(commercePriceListId);

		if ((status == WorkflowConstants.STATUS_APPROVED) &&
			(commercePriceList.getDisplayDate() != null) &&
			date.before(commercePriceList.getDisplayDate())) {

			status = WorkflowConstants.STATUS_SCHEDULED;
		}

		Date modifiedDate = serviceContext.getModifiedDate(date);

		if (status == WorkflowConstants.STATUS_APPROVED) {
			Date expirationDate = commercePriceList.getExpirationDate();

			if ((expirationDate != null) && expirationDate.before(date)) {
				commercePriceList.setExpirationDate(null);
			}
		}

		if (status == WorkflowConstants.STATUS_EXPIRED) {
			commercePriceList.setExpirationDate(date);
		}

		commercePriceList.setStatus(status);
		commercePriceList.setStatusByUserId(user.getUserId());
		commercePriceList.setStatusByUserName(user.getFullName());
		commercePriceList.setStatusDate(modifiedDate);

		return commercePriceListPersistence.update(commercePriceList);
	}

	@Activate
	protected void activate() {
		_portalCache =
			(PortalCache<String, CommercePriceList>)_multiVMPool.getPortalCache(
				"PRICE_LISTS", false, true);
	}

	@Deactivate
	@Override
	protected void deactivate() {
		_multiVMPool.removePortalCache(_portalCache.getPortalCacheName());
	}

	private SearchContext _buildSearchContext(
		long companyId, long groupId, long commerceAccountId,
		long[] commerceAccountGroupIds) {

		SearchContext searchContext = new SearchContext();

		searchContext.setAttributes(
			HashMapBuilder.<String, Serializable>put(
				Field.STATUS, WorkflowConstants.STATUS_APPROVED
			).put(
				"commerceAccountGroupIds", commerceAccountGroupIds
			).put(
				"commerceAccountId", commerceAccountId
			).build());
		searchContext.setCompanyId(companyId);
		searchContext.setEnd(1);
		searchContext.setGroupIds(new long[] {groupId});
		searchContext.setStart(0);

		QueryConfig queryConfig = searchContext.getQueryConfig();

		queryConfig.setHighlightEnabled(false);
		queryConfig.setScoreEnabled(false);

		Sort sort = SortFactoryUtil.create(
			Field.PRIORITY + "_Number_sortable", true);

		searchContext.setSorts(sort);

		return searchContext;
	}

	private SearchContext _buildSearchContext(
		long companyId, long[] groupIds, String keywords, int status, int start,
		int end, Sort sort) {

		SearchContext searchContext = new SearchContext();

		searchContext.setAttributes(
			HashMapBuilder.<String, Serializable>put(
				Field.ENTRY_CLASS_PK, keywords
			).put(
				Field.NAME, keywords
			).put(
				Field.STATUS, status
			).put(
				Field.USER_NAME, keywords
			).put(
				"params",
				LinkedHashMapBuilder.<String, Object>put(
					"keywords", keywords
				).build()
			).build());
		searchContext.setCompanyId(companyId);
		searchContext.setEnd(end);
		searchContext.setGroupIds(groupIds);

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

	private void _checkCommercePriceListsByDisplayDate()
		throws PortalException {

		List<CommercePriceList> commercePriceLists =
			commercePriceListPersistence.findByLtD_S(
				new Date(), WorkflowConstants.STATUS_SCHEDULED);

		for (CommercePriceList commercePriceList : commercePriceLists) {
			long userId = _portal.getValidUserId(
				commercePriceList.getCompanyId(),
				commercePriceList.getUserId());

			ServiceContext serviceContext = new ServiceContext();

			serviceContext.setCommand(Constants.UPDATE);
			serviceContext.setScopeGroupId(commercePriceList.getGroupId());

			commercePriceListLocalService.updateStatus(
				userId, commercePriceList.getCommercePriceListId(),
				WorkflowConstants.STATUS_APPROVED, serviceContext,
				new HashMap<String, Serializable>());
		}
	}

	private void _checkCommercePriceListsByExpirationDate()
		throws PortalException {

		List<CommercePriceList> commercePriceLists =
			commercePriceListFinder.findByExpirationDate(
				new Date(),
				new QueryDefinition<CommercePriceList>(
					WorkflowConstants.STATUS_APPROVED));

		if (_log.isDebugEnabled()) {
			_log.debug(
				"Expiring " + commercePriceLists.size() +
					" commerce price lists");
		}

		if ((commercePriceLists != null) && !commercePriceLists.isEmpty()) {
			for (CommercePriceList commercePriceList : commercePriceLists) {
				long userId = _portal.getValidUserId(
					commercePriceList.getCompanyId(),
					commercePriceList.getUserId());

				ServiceContext serviceContext = new ServiceContext();

				serviceContext.setCommand(Constants.UPDATE);
				serviceContext.setScopeGroupId(commercePriceList.getGroupId());

				commercePriceListLocalService.updateStatus(
					userId, commercePriceList.getCommercePriceListId(),
					WorkflowConstants.STATUS_EXPIRED, serviceContext,
					new HashMap<String, Serializable>());
			}
		}
	}

	private List<CommercePriceList> _getCommercePriceLists(Hits hits)
		throws PortalException {

		List<Document> documents = hits.toList();

		List<CommercePriceList> commercePriceLists = new ArrayList<>(
			documents.size());

		for (Document document : documents) {
			long commercePriceListId = GetterUtil.getLong(
				document.get(Field.ENTRY_CLASS_PK));

			CommercePriceList commercePriceList = fetchCommercePriceList(
				commercePriceListId);

			if (commercePriceList == null) {
				commercePriceLists = null;

				Indexer<CommercePriceList> indexer =
					IndexerRegistryUtil.getIndexer(CommercePriceList.class);

				long companyId = GetterUtil.getLong(
					document.get(Field.COMPANY_ID));

				indexer.delete(companyId, document.getUID());
			}
			else if (commercePriceLists != null) {
				commercePriceLists.add(commercePriceList);
			}
		}

		return commercePriceLists;
	}

	private GroupByStep _getGroupByStep(
		FromStep fromStep, Long groupId, Long commerceAccountId,
		long[] commerceAccountGroupIds, Long commerceChannelId,
		Long commerceOrderTypeId, String currencyCode, String type) {

		JoinStep joinStep = fromStep.from(CommercePriceListTable.INSTANCE);
		Predicate predicate = CommercePriceListTable.INSTANCE.status.eq(
			WorkflowConstants.STATUS_APPROVED
		).and(
			CommercePriceListTable.INSTANCE.groupId.eq(groupId)
		).and(
			CommercePriceListTable.INSTANCE.type.eq(type)
		);

		if (commerceAccountId != null) {
			joinStep = joinStep.innerJoinON(
				CommercePriceListAccountRelTable.INSTANCE,
				CommercePriceListAccountRelTable.INSTANCE.commercePriceListId.
					eq(CommercePriceListTable.INSTANCE.commercePriceListId));
			predicate = predicate.and(
				CommercePriceListAccountRelTable.INSTANCE.commerceAccountId.eq(
					commerceAccountId));
		}
		else {
			joinStep = joinStep.leftJoinOn(
				CommercePriceListAccountRelTable.INSTANCE,
				CommercePriceListAccountRelTable.INSTANCE.commercePriceListId.
					eq(CommercePriceListTable.INSTANCE.commercePriceListId));
			predicate = predicate.and(
				CommercePriceListAccountRelTable.INSTANCE.
					commercePriceListAccountRelId.isNull());
		}

		if (commerceAccountGroupIds != null) {
			if (commerceAccountGroupIds.length == 0) {
				commerceAccountGroupIds = new long[] {0};
			}

			joinStep = joinStep.innerJoinON(
				CommercePriceListCommerceAccountGroupRelTable.INSTANCE,
				CommercePriceListCommerceAccountGroupRelTable.INSTANCE.
					commercePriceListId.eq(
						CommercePriceListTable.INSTANCE.commercePriceListId));

			predicate = predicate.and(
				CommercePriceListCommerceAccountGroupRelTable.INSTANCE.
					commerceAccountGroupId.in(
						ArrayUtil.toLongArray(commerceAccountGroupIds)));
		}
		else {
			joinStep = joinStep.leftJoinOn(
				CommercePriceListCommerceAccountGroupRelTable.INSTANCE,
				CommercePriceListCommerceAccountGroupRelTable.INSTANCE.
					commercePriceListId.eq(
						CommercePriceListTable.INSTANCE.commercePriceListId));
			predicate = predicate.and(
				CommercePriceListCommerceAccountGroupRelTable.INSTANCE.
					commercePriceListCommerceAccountGroupRelId.isNull());
		}

		if (commerceChannelId != null) {
			joinStep = joinStep.innerJoinON(
				CommercePriceListChannelRelTable.INSTANCE,
				CommercePriceListChannelRelTable.INSTANCE.commercePriceListId.
					eq(CommercePriceListTable.INSTANCE.commercePriceListId));
			predicate = predicate.and(
				CommercePriceListChannelRelTable.INSTANCE.commerceChannelId.eq(
					commerceChannelId));
		}
		else {
			joinStep = joinStep.leftJoinOn(
				CommercePriceListChannelRelTable.INSTANCE,
				CommercePriceListChannelRelTable.INSTANCE.commercePriceListId.
					eq(CommercePriceListTable.INSTANCE.commercePriceListId));
			predicate = predicate.and(
				CommercePriceListChannelRelTable.INSTANCE.
					CommercePriceListChannelRelId.isNull());
		}

		if (commerceChannelId != null) {
			joinStep = joinStep.leftJoinOn(
				CommerceChannelRelTable.INSTANCE,
				CommerceChannelRelTable.INSTANCE.commerceChannelId.eq(
					commerceChannelId
				).and(
					CommerceChannelRelTable.INSTANCE.classNameId.eq(
						_classNameLocalService.getClassNameId(
							CommerceCurrency.class.getName()))
				));

			joinStep = joinStep.leftJoinOn(
				CommerceCurrencyTable.INSTANCE,
				CommerceCurrencyTable.INSTANCE.commerceCurrencyId.eq(
					CommerceChannelRelTable.INSTANCE.classPK));

			predicate = predicate.and(
				CommerceCurrencyTable.INSTANCE.code.eq(
					CommercePriceListTable.INSTANCE.commerceCurrencyCode
				).or(
					CommerceChannelRelTable.INSTANCE.commerceChannelRelId.
						isNull()
				).withParentheses());
		}

		if (!Validator.isBlank(currencyCode)) {
			predicate = predicate.and(
				CommercePriceListTable.INSTANCE.commerceCurrencyCode.eq(
					currencyCode));
		}

		if (commerceOrderTypeId != null) {
			joinStep = joinStep.innerJoinON(
				CommercePriceListOrderTypeRelTable.INSTANCE,
				CommercePriceListOrderTypeRelTable.INSTANCE.commercePriceListId.
					eq(CommercePriceListTable.INSTANCE.commercePriceListId));
			predicate = predicate.and(
				CommercePriceListOrderTypeRelTable.INSTANCE.commerceOrderTypeId.
					eq(commerceOrderTypeId));
		}
		else {
			joinStep = joinStep.leftJoinOn(
				CommercePriceListOrderTypeRelTable.INSTANCE,
				CommercePriceListOrderTypeRelTable.INSTANCE.commercePriceListId.
					eq(CommercePriceListTable.INSTANCE.commercePriceListId));
			predicate = predicate.and(
				CommercePriceListOrderTypeRelTable.INSTANCE.
					commercePriceListOrderTypeRelId.isNull());
		}

		return joinStep.where(predicate);
	}

	private GroupByStep _getGroupByStep(
		FromStep fromStep, Long groupId, Long commerceAccountId,
		long[] commerceAccountGroupIds, Long commerceChannelId,
		Long commerceOrderTypeId, String cpInstanceUuid, String currencyCode,
		String type, String unitOfMeasureKey) {

		CommerceCurrencyTable commerceChannelRelCommerceCurrencyTable =
			CommerceCurrencyTable.INSTANCE.as("commerceChannelRelCurrency");

		JoinStep joinStep = fromStep.from(
			CommercePriceEntryTable.INSTANCE
		).innerJoinON(
			CommercePriceListTable.INSTANCE,
			CommercePriceListTable.INSTANCE.commercePriceListId.eq(
				CommercePriceEntryTable.INSTANCE.commercePriceListId)
		).innerJoinON(
			CommerceCurrencyTable.INSTANCE,
			CommerceCurrencyTable.INSTANCE.code.eq(
				CommercePriceListTable.INSTANCE.commerceCurrencyCode)
		).leftJoinOn(
			CommercePriceListAccountRelTable.INSTANCE,
			CommercePriceListAccountRelTable.INSTANCE.commercePriceListId.eq(
				CommercePriceListTable.INSTANCE.commercePriceListId)
		).leftJoinOn(
			CommercePriceListCommerceAccountGroupRelTable.INSTANCE,
			CommercePriceListCommerceAccountGroupRelTable.INSTANCE.
				commercePriceListId.eq(
					CommercePriceListTable.INSTANCE.commercePriceListId)
		).leftJoinOn(
			CommercePriceListChannelRelTable.INSTANCE,
			CommercePriceListChannelRelTable.INSTANCE.commercePriceListId.eq(
				CommercePriceListTable.INSTANCE.commercePriceListId)
		).leftJoinOn(
			CommerceChannelRelTable.INSTANCE,
			CommerceChannelRelTable.INSTANCE.commerceChannelId.eq(
				commerceChannelId
			).and(
				CommerceChannelRelTable.INSTANCE.classNameId.eq(
					_classNameLocalService.getClassNameId(
						CommerceCurrency.class.getName()))
			)
		).leftJoinOn(
			commerceChannelRelCommerceCurrencyTable,
			commerceChannelRelCommerceCurrencyTable.commerceCurrencyId.eq(
				CommerceChannelRelTable.INSTANCE.classPK)
		).leftJoinOn(
			CommercePriceListOrderTypeRelTable.INSTANCE,
			CommercePriceListOrderTypeRelTable.INSTANCE.commercePriceListId.eq(
				CommercePriceListTable.INSTANCE.commercePriceListId)
		);

		Long[] commerceAccountGroupObjs = {0L};

		if ((commerceAccountGroupIds != null) &&
			(commerceAccountGroupIds.length > 0)) {

			commerceAccountGroupObjs = ArrayUtil.toLongArray(
				commerceAccountGroupIds);
		}

		Predicate predicate = CommercePriceListTable.INSTANCE.status.eq(
			WorkflowConstants.STATUS_APPROVED
		).and(
			CommercePriceListTable.INSTANCE.groupId.eq(groupId)
		).and(
			CommercePriceListTable.INSTANCE.type.eq(type)
		).and(
			CommercePriceListAccountRelTable.INSTANCE.commerceAccountId.eq(
				commerceAccountId
			).or(
				CommercePriceListAccountRelTable.INSTANCE.
					commercePriceListAccountRelId.isNull()
			).withParentheses()
		).and(
			CommercePriceListCommerceAccountGroupRelTable.INSTANCE.
				commerceAccountGroupId.in(
					commerceAccountGroupObjs
				).or(
					CommercePriceListCommerceAccountGroupRelTable.INSTANCE.
						commercePriceListCommerceAccountGroupRelId.isNull()
				).withParentheses()
		).and(
			CommercePriceListChannelRelTable.INSTANCE.commerceChannelId.eq(
				commerceChannelId
			).or(
				CommercePriceListChannelRelTable.INSTANCE.
					CommercePriceListChannelRelId.isNull()
			).withParentheses()
		).and(
			commerceChannelRelCommerceCurrencyTable.code.eq(
				CommercePriceListTable.INSTANCE.commerceCurrencyCode
			).or(
				CommerceChannelRelTable.INSTANCE.commerceChannelRelId.isNull()
			).withParentheses()
		).and(
			CommercePriceListOrderTypeRelTable.INSTANCE.commerceOrderTypeId.eq(
				commerceOrderTypeId
			).or(
				CommercePriceListOrderTypeRelTable.INSTANCE.
					commercePriceListOrderTypeRelId.isNull()
			).withParentheses()
		);

		if (!Validator.isBlank(cpInstanceUuid)) {
			predicate = predicate.and(
				CommercePriceEntryTable.INSTANCE.CPInstanceUuid.eq(
					cpInstanceUuid)
			).and(
				CommercePriceEntryTable.INSTANCE.status.eq(
					WorkflowConstants.STATUS_APPROVED)
			);
		}

		if (!Validator.isBlank(currencyCode)) {
			predicate = predicate.and(
				CommercePriceListTable.INSTANCE.commerceCurrencyCode.eq(
					currencyCode));
		}

		if (Validator.isNotNull(unitOfMeasureKey)) {
			predicate = predicate.and(
				CommercePriceEntryTable.INSTANCE.unitOfMeasureKey.eq(
					unitOfMeasureKey)
			).and(
				CommercePriceEntryTable.INSTANCE.status.eq(
					WorkflowConstants.STATUS_APPROVED)
			);
		}

		return joinStep.where(predicate);
	}

	private void _reindex(long commercePriceListId) throws PortalException {
		Indexer<CommercePriceList> indexer =
			IndexerRegistryUtil.nullSafeGetIndexer(CommercePriceList.class);

		indexer.reindex(CommercePriceList.class.getName(), commercePriceListId);
	}

	private BaseModelSearchResult<CommercePriceList> _searchCommercePriceLists(
			SearchContext searchContext)
		throws PortalException {

		Indexer<CommercePriceList> indexer =
			IndexerRegistryUtil.nullSafeGetIndexer(CommercePriceList.class);

		for (int i = 0; i < 10; i++) {
			Hits hits = indexer.search(searchContext, _SELECTED_FIELD_NAMES);

			List<CommercePriceList> commercePriceLists = _getCommercePriceLists(
				hits);

			if (commercePriceLists != null) {
				return new BaseModelSearchResult<>(
					commercePriceLists, hits.getLength());
			}
		}

		throw new SearchException(
			"Unable to fix the search index after 10 attempts");
	}

	private int _searchCommercePriceListsCount(SearchContext searchContext)
		throws PortalException {

		Indexer<CommercePriceList> indexer =
			IndexerRegistryUtil.nullSafeGetIndexer(CommercePriceList.class);

		return GetterUtil.getInteger(indexer.searchCount(searchContext));
	}

	private CommercePriceList _startWorkflowInstance(
			long userId, CommercePriceList commercePriceList,
			ServiceContext serviceContext)
		throws PortalException {

		Map<String, Serializable> workflowContext = new HashMap<>();

		return WorkflowHandlerRegistryUtil.startWorkflowInstance(
			commercePriceList.getCompanyId(), commercePriceList.getGroupId(),
			userId, CommercePriceList.class.getName(),
			commercePriceList.getCommercePriceListId(), commercePriceList,
			serviceContext, workflowContext);
	}

	private void _validate(
			boolean catalogBasePriceList, String commerceCurrencyCode,
			long commercePriceListId, long companyId, long groupId,
			boolean neverExpire, long parentCommercePriceListId, String type)
		throws PortalException {

		if (catalogBasePriceList) {
			CommercePriceList basePriceList =
				commercePriceListPersistence.fetchByG_CBPL_T_First(
					groupId, true, type, null);

			if ((basePriceList != null) &&
				(basePriceList.getCommercePriceListId() !=
					commercePriceListId)) {

				throw new DuplicateCommerceBasePriceListException();
			}

			if (!neverExpire &&
				Objects.equals(
					type, CommercePriceListConstants.TYPE_PRICE_LIST)) {

				throw new CommercePriceListExpirationDateException();
			}
		}

		if (parentCommercePriceListId > 0) {
			if (parentCommercePriceListId == commercePriceListId) {
				throw new CommercePriceListParentPriceListGroupIdException();
			}

			CommercePriceList commercePriceList =
				commercePriceListLocalService.fetchCommercePriceList(
					parentCommercePriceListId);

			if ((commercePriceList != null) &&
				(commercePriceList.getGroupId() != groupId)) {

				throw new CommercePriceListParentPriceListGroupIdException();
			}
		}

		try {
			_commerceCurrencyLocalService.getCommerceCurrency(
				companyId, commerceCurrencyCode);
		}
		catch (NoSuchCurrencyException noSuchCurrencyException) {
			throw new CommercePriceListCurrencyException(
				noSuchCurrencyException);
		}
	}

	private static final String[] _SELECTED_FIELD_NAMES = {
		Field.ENTRY_CLASS_PK, Field.COMPANY_ID, Field.GROUP_ID, Field.UID
	};

	private static final Log _log = LogFactoryUtil.getLog(
		CommercePriceListLocalServiceImpl.class);

	private static final CommercePriceList _dummyCommercePriceList =
		ProxyFactory.newDummyInstance(CommercePriceList.class);

	@Reference
	private ClassNameLocalService _classNameLocalService;

	@Reference
	private CommerceChannelAccountEntryRelLocalService
		_commerceChannelAccountEntryRelLocalService;

	@Reference
	private CommerceCurrencyLocalService _commerceCurrencyLocalService;

	@Reference
	private CommercePriceEntryLocalService _commercePriceEntryLocalService;

	@Reference
	private CommercePriceEntryPersistence _commercePriceEntryPersistence;

	@Reference
	private CommercePriceListAccountRelLocalService
		_commercePriceListAccountRelLocalService;

	@Reference
	private CommercePriceListChannelRelLocalService
		_commercePriceListChannelRelLocalService;

	@Reference
	private CommercePriceListCommerceAccountGroupRelLocalService
		_commercePriceListCommerceAccountGroupRelLocalService;

	@Reference
	private CommercePriceListDiscountRelLocalService
		_commercePriceListDiscountRelLocalService;

	@Reference
	private CommercePriceListOrderTypeRelLocalService
		_commercePriceListOrderTypeRelLocalService;

	@Reference
	private CommercePriceModifierLocalService
		_commercePriceModifierLocalService;

	@Reference
	private CommercePriceModifierTypeRegistry
		_commercePriceModifierTypeRegistry;

	@Reference
	private CPDefinitionLocalService _cpDefinitionLocalService;

	@Reference
	private ExpandoRowLocalService _expandoRowLocalService;

	@Reference
	private MultiVMPool _multiVMPool;

	@Reference
	private Portal _portal;

	private PortalCache<String, CommercePriceList> _portalCache;

	@Reference
	private ResourceLocalService _resourceLocalService;

	@Reference
	private UserLocalService _userLocalService;

	@Reference
	private WorkflowInstanceLinkLocalService _workflowInstanceLinkLocalService;

}