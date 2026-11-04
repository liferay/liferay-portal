/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.inventory.service.impl;

import com.liferay.commerce.inventory.constants.CommerceInventoryConstants;
import com.liferay.commerce.inventory.exception.MVCCException;
import com.liferay.commerce.inventory.exception.NoSuchInventoryBookedQuantityException;
import com.liferay.commerce.inventory.model.CommerceInventoryBookedQuantity;
import com.liferay.commerce.inventory.model.CommerceInventoryBookedQuantityTable;
import com.liferay.commerce.inventory.service.CommerceInventoryAuditLocalService;
import com.liferay.commerce.inventory.service.base.CommerceInventoryBookedQuantityLocalServiceBaseImpl;
import com.liferay.commerce.inventory.type.CommerceInventoryAuditType;
import com.liferay.commerce.inventory.type.CommerceInventoryAuditTypeRegistry;
import com.liferay.commerce.model.CommerceOrderItemTable;
import com.liferay.commerce.product.model.CommerceChannel;
import com.liferay.petra.sql.dsl.DSLFunctionFactoryUtil;
import com.liferay.petra.sql.dsl.DSLQueryFactoryUtil;
import com.liferay.portal.aop.AopService;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.GroupTable;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.search.BaseModelSearchResult;
import com.liferay.portal.kernel.search.Document;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.Hits;
import com.liferay.portal.kernel.search.Indexable;
import com.liferay.portal.kernel.search.IndexableType;
import com.liferay.portal.kernel.search.Indexer;
import com.liferay.portal.kernel.search.IndexerRegistry;
import com.liferay.portal.kernel.search.QueryConfig;
import com.liferay.portal.kernel.search.SearchContext;
import com.liferay.portal.kernel.search.SearchException;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.util.BigDecimalUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.Validator;

import java.math.BigDecimal;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Luca Pellizzon
 * @author Alessio Antonio Rendina
 */
@Component(
	property = "model.class.name=com.liferay.commerce.inventory.model.CommerceInventoryBookedQuantity",
	service = AopService.class
)
public class CommerceInventoryBookedQuantityLocalServiceImpl
	extends CommerceInventoryBookedQuantityLocalServiceBaseImpl {

	@Indexable(type = IndexableType.REINDEX)
	@Override
	public CommerceInventoryBookedQuantity addCommerceInventoryBookedQuantity(
			long userId, Date expirationDate, BigDecimal quantity, String sku,
			String unitOfMeasureKey, Map<String, String> context)
		throws PortalException {

		User user = _userLocalService.getUser(userId);

		long commerceInventoryBookedQuantityId =
			counterLocalService.increment();

		CommerceInventoryBookedQuantity commerceInventoryBookedQuantity =
			commerceInventoryBookedQuantityPersistence.create(
				commerceInventoryBookedQuantityId);

		commerceInventoryBookedQuantity.setCompanyId(user.getCompanyId());
		commerceInventoryBookedQuantity.setUserId(user.getUserId());
		commerceInventoryBookedQuantity.setUserName(user.getFullName());
		commerceInventoryBookedQuantity.setExpirationDate(expirationDate);
		commerceInventoryBookedQuantity.setQuantity(quantity);
		commerceInventoryBookedQuantity.setSku(sku);
		commerceInventoryBookedQuantity.setUnitOfMeasureKey(unitOfMeasureKey);

		CommerceInventoryAuditType commerceInventoryAuditType =
			_commerceInventoryAuditTypeRegistry.getCommerceInventoryAuditType(
				CommerceInventoryConstants.AUDIT_TYPE_BOOKED_QUANTITY);

		_commerceInventoryAuditLocalService.addCommerceInventoryAudit(
			userId, commerceInventoryAuditType.getType(),
			commerceInventoryAuditType.getLog(context), quantity, sku,
			unitOfMeasureKey);

		return commerceInventoryBookedQuantityPersistence.update(
			commerceInventoryBookedQuantity);
	}

	@Override
	public void checkCommerceInventoryBookedQuantities() {
		for (CommerceInventoryBookedQuantity commerceInventoryBookedQuantity :
				commerceInventoryBookedQuantityPersistence.
					findByLtExpirationDate(new Date())) {

			commerceInventoryBookedQuantityLocalService.
				deleteCommerceInventoryBookedQuantity(
					commerceInventoryBookedQuantity);
		}
	}

	@Override
	public CommerceInventoryBookedQuantity
			consumeCommerceInventoryBookedQuantity(
				long commerceInventoryBookedQuantityId, BigDecimal quantity)
		throws NoSuchInventoryBookedQuantityException {

		CommerceInventoryBookedQuantity commerceInventoryBookedQuantity =
			commerceInventoryBookedQuantityPersistence.findByPrimaryKey(
				commerceInventoryBookedQuantityId);

		if (quantity == null) {
			quantity = BigDecimal.ZERO;
		}

		BigDecimal commerceInventoryWarehouseItemQuantity =
			commerceInventoryBookedQuantity.getQuantity();

		if (BigDecimalUtil.lt(
				quantity, commerceInventoryWarehouseItemQuantity)) {

			BigDecimal newQuantity =
				commerceInventoryWarehouseItemQuantity.subtract(quantity);

			commerceInventoryBookedQuantity.setQuantity(newQuantity);

			return commerceInventoryBookedQuantityLocalService.
				updateCommerceInventoryBookedQuantity(
					commerceInventoryBookedQuantity);
		}

		return commerceInventoryBookedQuantityLocalService.
			deleteCommerceInventoryBookedQuantity(
				commerceInventoryBookedQuantity);
	}

	@Override
	public CommerceInventoryBookedQuantity
			deleteCommerceInventoryBookedQuantity(
				long userId, long commerceInventoryBookedQuantityId,
				Map<String, String> context,
				CommerceInventoryAuditType commerceInventoryAuditType)
		throws PortalException {

		CommerceInventoryBookedQuantity commerceInventoryBookedQuantity =
			commerceInventoryBookedQuantityLocalService.
				deleteCommerceInventoryBookedQuantity(
					commerceInventoryBookedQuantityId);

		_commerceInventoryAuditLocalService.addCommerceInventoryAudit(
			userId, commerceInventoryAuditType.getType(),
			commerceInventoryAuditType.getLog(context),
			commerceInventoryBookedQuantity.getQuantity(),
			commerceInventoryBookedQuantity.getSku(),
			commerceInventoryBookedQuantity.getUnitOfMeasureKey());

		return commerceInventoryBookedQuantity;
	}

	@Override
	public List<CommerceInventoryBookedQuantity>
		getCommerceInventoryBookedQuantities(
			long companyId, String sku, String unitOfMeasureKey, int start,
			int end) {

		return commerceInventoryBookedQuantityPersistence.findByC_S_U(
			companyId, sku, unitOfMeasureKey, start, end);
	}

	@Override
	public List<CommerceInventoryBookedQuantity>
			getCommerceInventoryBookedQuantities(
				long companyId, String keywords, String sku,
				String unitOfMeasureKey, int start, int end)
		throws PortalException {

		SearchContext searchContext = _buildSearchContext(
			companyId, keywords, sku, unitOfMeasureKey, start, end);

		BaseModelSearchResult<CommerceInventoryBookedQuantity>
			baseModelSearchResult =
				commerceInventoryBookedQuantityLocalService.
					searchCommerceInventoryBookedQuantities(searchContext);

		return baseModelSearchResult.getBaseModels();
	}

	@Override
	public int getCommerceInventoryBookedQuantitiesCount(
		long companyId, String sku, String unitOfMeasureKey) {

		return commerceInventoryBookedQuantityPersistence.countByC_S_U(
			companyId, sku, unitOfMeasureKey);
	}

	@Override
	public int getCommerceInventoryBookedQuantitiesCount(
			long companyId, String keywords, String sku,
			String unitOfMeasureKey)
		throws PortalException {

		SearchContext searchContext = _buildSearchContext(
			companyId, keywords, sku, unitOfMeasureKey, QueryUtil.ALL_POS,
			QueryUtil.ALL_POS);

		return commerceInventoryBookedQuantityLocalService.
			searchCommerceInventoryBookedQuantitiesCount(searchContext);
	}

	@Override
	public BigDecimal getCommerceInventoryBookedQuantity(
		long companyId, long commerceChannelGroupId, String sku,
		String unitOfMeasureKey) {

		Iterable<BigDecimal> iterable = dslQuery(
			DSLQueryFactoryUtil.select(
				DSLFunctionFactoryUtil.sum(
					CommerceInventoryBookedQuantityTable.INSTANCE.quantity
				).as(
					"SUM_VALUE"
				)
			).from(
				CommerceInventoryBookedQuantityTable.INSTANCE
			).innerJoinON(
				CommerceOrderItemTable.INSTANCE,
				CommerceInventoryBookedQuantityTable.INSTANCE.
					commerceInventoryBookedQuantityId.eq(
						CommerceOrderItemTable.INSTANCE.
							commerceInventoryBookedQuantityId)
			).innerJoinON(
				GroupTable.INSTANCE,
				CommerceOrderItemTable.INSTANCE.groupId.eq(
					GroupTable.INSTANCE.groupId
				).and(
					GroupTable.INSTANCE.classNameId.eq(
						_portal.getClassNameId(CommerceChannel.class.getName()))
				)
			).where(
				CommerceInventoryBookedQuantityTable.INSTANCE.companyId.eq(
					companyId
				).and(
					CommerceInventoryBookedQuantityTable.INSTANCE.sku.eq(sku)
				).and(
					() -> {
						if (Validator.isNull(unitOfMeasureKey)) {
							return null;
						}

						return CommerceInventoryBookedQuantityTable.INSTANCE.
							unitOfMeasureKey.eq(unitOfMeasureKey);
					}
				).and(
					GroupTable.INSTANCE.groupId.eq(commerceChannelGroupId)
				)
			));

		Iterator<BigDecimal> iterator = iterable.iterator();

		BigDecimal bookedQuantity = iterator.next();

		if (bookedQuantity == null) {
			return BigDecimal.ZERO;
		}

		return bookedQuantity;
	}

	@Override
	public BigDecimal getCommerceInventoryBookedQuantity(
		long companyId, String sku, String unitOfMeasureKey) {

		Iterable<BigDecimal> iterable = dslQuery(
			DSLQueryFactoryUtil.select(
				DSLFunctionFactoryUtil.sum(
					CommerceInventoryBookedQuantityTable.INSTANCE.quantity
				).as(
					"SUM_VALUE"
				)
			).from(
				CommerceInventoryBookedQuantityTable.INSTANCE
			).where(
				CommerceInventoryBookedQuantityTable.INSTANCE.companyId.eq(
					companyId
				).and(
					CommerceInventoryBookedQuantityTable.INSTANCE.sku.eq(sku)
				).and(
					() -> {
						if (Validator.isNull(unitOfMeasureKey)) {
							return null;
						}

						return CommerceInventoryBookedQuantityTable.INSTANCE.
							unitOfMeasureKey.eq(unitOfMeasureKey);
					}
				)
			));

		Iterator<BigDecimal> iterator = iterable.iterator();

		BigDecimal bookedQuantity = iterator.next();

		if (bookedQuantity == null) {
			return BigDecimal.ZERO;
		}

		return bookedQuantity;
	}

	@Override
	public CommerceInventoryBookedQuantity resetCommerceInventoryBookedQuantity(
			long commerceInventoryBookedQuantityId, long userId,
			Date expirationDate, BigDecimal quantity, String sku,
			String unitOfMeasureKey, Map<String, String> context)
		throws PortalException {

		if (commerceInventoryBookedQuantityId < 1) {
			return null;
		}

		CommerceInventoryBookedQuantity commerceInventoryBookedQuantity =
			commerceInventoryBookedQuantityPersistence.fetchByPrimaryKey(
				commerceInventoryBookedQuantityId);

		if (commerceInventoryBookedQuantity == null) {
			User user = _userLocalService.getUser(userId);

			commerceInventoryBookedQuantity =
				commerceInventoryBookedQuantityPersistence.create(
					commerceInventoryBookedQuantityId);

			commerceInventoryBookedQuantity.setCompanyId(user.getCompanyId());
			commerceInventoryBookedQuantity.setUserId(userId);
			commerceInventoryBookedQuantity.setUserName(user.getFullName());
			commerceInventoryBookedQuantity.setExpirationDate(expirationDate);
			commerceInventoryBookedQuantity.setSku(sku);
			commerceInventoryBookedQuantity.setUnitOfMeasureKey(
				unitOfMeasureKey);
		}
		else {
			quantity = quantity.add(
				commerceInventoryBookedQuantity.getQuantity());

			if (BigDecimalUtil.lt(quantity, BigDecimal.ZERO)) {
				quantity = BigDecimal.ZERO;
			}
		}

		commerceInventoryBookedQuantity.setQuantity(quantity);

		CommerceInventoryAuditType commerceInventoryAuditType =
			_commerceInventoryAuditTypeRegistry.getCommerceInventoryAuditType(
				CommerceInventoryConstants.AUDIT_TYPE_RESTORE_QUANTITY);

		_commerceInventoryAuditLocalService.addCommerceInventoryAudit(
			userId, commerceInventoryAuditType.getType(),
			commerceInventoryAuditType.getLog(context), quantity, sku,
			unitOfMeasureKey);

		return commerceInventoryBookedQuantityLocalService.
			updateCommerceInventoryBookedQuantity(
				commerceInventoryBookedQuantity);
	}

	@Override
	public CommerceInventoryBookedQuantity
			restockCommerceInventoryBookedQuantity(
				long userId, long commerceInventoryBookedQuantityId,
				Map<String, String> context)
		throws PortalException {

		CommerceInventoryBookedQuantity commerceInventoryBookedQuantity =
			commerceInventoryBookedQuantityLocalService.
				deleteCommerceInventoryBookedQuantity(
					commerceInventoryBookedQuantityId);

		CommerceInventoryAuditType commerceInventoryAuditType =
			_commerceInventoryAuditTypeRegistry.getCommerceInventoryAuditType(
				CommerceInventoryConstants.AUDIT_TYPE_RESTOCK_QUANTITY);

		_commerceInventoryAuditLocalService.addCommerceInventoryAudit(
			userId, commerceInventoryAuditType.getType(),
			commerceInventoryAuditType.getLog(context),
			commerceInventoryBookedQuantity.getQuantity(),
			commerceInventoryBookedQuantity.getSku(),
			commerceInventoryBookedQuantity.getUnitOfMeasureKey());

		return commerceInventoryBookedQuantity;
	}

	@Override
	public BaseModelSearchResult<CommerceInventoryBookedQuantity>
			searchCommerceInventoryBookedQuantities(SearchContext searchContext)
		throws PortalException {

		Indexer<CommerceInventoryBookedQuantity> indexer =
			_indexerRegistry.nullSafeGetIndexer(
				CommerceInventoryBookedQuantity.class);

		for (int i = 0; i < 10; i++) {
			Hits hits = indexer.search(searchContext);

			List<CommerceInventoryBookedQuantity>
				commerceInventoryBookedQuantities =
					_getCommerceInventoryBookedQuantities(hits);

			if (commerceInventoryBookedQuantities != null) {
				return new BaseModelSearchResult<>(
					commerceInventoryBookedQuantities, hits.getLength());
			}
		}

		throw new SearchException(
			"Unable to fix the search index after 10 attempts");
	}

	@Override
	public int searchCommerceInventoryBookedQuantitiesCount(
			SearchContext searchContext)
		throws PortalException {

		Indexer<CommerceInventoryBookedQuantity> indexer =
			_indexerRegistry.nullSafeGetIndexer(
				CommerceInventoryBookedQuantity.class);

		return GetterUtil.getInteger(indexer.searchCount(searchContext));
	}

	@Indexable(type = IndexableType.REINDEX)
	@Override
	public CommerceInventoryBookedQuantity
			updateCommerceInventoryBookedQuantity(
				long userId, long commerceInventoryBookedQuantityId,
				BigDecimal quantity, Map<String, String> context,
				long mvccVersion)
		throws PortalException {

		CommerceInventoryBookedQuantity commerceInventoryBookedQuantity =
			commerceInventoryBookedQuantityPersistence.findByPrimaryKey(
				commerceInventoryBookedQuantityId);

		if (commerceInventoryBookedQuantity.getMvccVersion() != mvccVersion) {
			throw new MVCCException();
		}

		commerceInventoryBookedQuantity.setQuantity(quantity);

		CommerceInventoryAuditType commerceInventoryAuditType =
			_commerceInventoryAuditTypeRegistry.getCommerceInventoryAuditType(
				CommerceInventoryConstants.AUDIT_TYPE_UPDATE_BOOKED_QUANTITY);

		_commerceInventoryAuditLocalService.addCommerceInventoryAudit(
			userId, commerceInventoryAuditType.getType(),
			commerceInventoryAuditType.getLog(context), quantity,
			commerceInventoryBookedQuantity.getSku(),
			commerceInventoryBookedQuantity.getUnitOfMeasureKey());

		return commerceInventoryBookedQuantityLocalService.
			updateCommerceInventoryBookedQuantity(
				commerceInventoryBookedQuantity);
	}

	private SearchContext _buildSearchContext(
		long companyId, String keywords, String sku, String unitOfMeasureKey,
		int start, int end) {

		SearchContext searchContext = new SearchContext();

		searchContext.setCompanyId(companyId);
		searchContext.setEnd(end);
		searchContext.setKeywords(keywords);

		if (Validator.isNotNull(sku)) {
			searchContext.setAttribute("sku", sku);
		}

		if (Validator.isNotNull(unitOfMeasureKey)) {
			searchContext.setAttribute("unitOfMeasureKey", unitOfMeasureKey);
		}

		searchContext.setStart(start);

		QueryConfig queryConfig = searchContext.getQueryConfig();

		queryConfig.setHighlightEnabled(false);
		queryConfig.setScoreEnabled(false);

		return searchContext;
	}

	private List<CommerceInventoryBookedQuantity>
			_getCommerceInventoryBookedQuantities(Hits hits)
		throws PortalException {

		List<Document> documents = hits.toList();

		List<CommerceInventoryBookedQuantity>
			commerceInventoryBookedQuantities = new ArrayList<>(
				documents.size());

		for (Document document : documents) {
			long commerceInventoryBookedQuantityId = GetterUtil.getLong(
				document.get(Field.ENTRY_CLASS_PK));

			CommerceInventoryBookedQuantity commerceInventoryBookedQuantity =
				fetchCommerceInventoryBookedQuantity(
					commerceInventoryBookedQuantityId);

			if (commerceInventoryBookedQuantity == null) {
				commerceInventoryBookedQuantities = null;

				Indexer<CommerceInventoryBookedQuantity> indexer =
					_indexerRegistry.getIndexer(
						CommerceInventoryBookedQuantity.class);

				long companyId = GetterUtil.getLong(
					document.get(Field.COMPANY_ID));

				indexer.delete(companyId, document.getUID());
			}
			else if (commerceInventoryBookedQuantities != null) {
				commerceInventoryBookedQuantities.add(
					commerceInventoryBookedQuantity);
			}
		}

		return commerceInventoryBookedQuantities;
	}

	@Reference
	private CommerceInventoryAuditLocalService
		_commerceInventoryAuditLocalService;

	@Reference
	private CommerceInventoryAuditTypeRegistry
		_commerceInventoryAuditTypeRegistry;

	@Reference
	private IndexerRegistry _indexerRegistry;

	@Reference
	private Portal _portal;

	@Reference
	private UserLocalService _userLocalService;

}