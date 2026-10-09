/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.inventory.service.impl;

import com.liferay.account.model.AccountEntry;
import com.liferay.account.model.AccountGroup;
import com.liferay.account.service.AccountGroupLocalService;
import com.liferay.commerce.inventory.constants.CommerceInventoryConstants;
import com.liferay.commerce.inventory.exception.CommerceInventoryWarehouseItemSkuException;
import com.liferay.commerce.inventory.exception.DuplicateCommerceInventoryWarehouseItemException;
import com.liferay.commerce.inventory.exception.MVCCException;
import com.liferay.commerce.inventory.model.CIWarehouseItem;
import com.liferay.commerce.inventory.model.CommerceInventoryBookedQuantityTable;
import com.liferay.commerce.inventory.model.CommerceInventoryReplenishmentItemTable;
import com.liferay.commerce.inventory.model.CommerceInventoryWarehouse;
import com.liferay.commerce.inventory.model.CommerceInventoryWarehouseItem;
import com.liferay.commerce.inventory.model.CommerceInventoryWarehouseItemTable;
import com.liferay.commerce.inventory.model.CommerceInventoryWarehouseRelTable;
import com.liferay.commerce.inventory.model.CommerceInventoryWarehouseTable;
import com.liferay.commerce.inventory.service.CommerceInventoryAuditLocalService;
import com.liferay.commerce.inventory.service.base.CommerceInventoryWarehouseItemLocalServiceBaseImpl;
import com.liferay.commerce.inventory.type.CommerceInventoryAuditType;
import com.liferay.commerce.inventory.type.CommerceInventoryAuditTypeRegistry;
import com.liferay.commerce.inventory.type.constants.CommerceInventoryAuditTypeConstants;
import com.liferay.commerce.product.exception.CPInstanceUnitOfMeasureKeyException;
import com.liferay.commerce.product.exception.NoSuchCPInstanceUnitOfMeasureException;
import com.liferay.commerce.product.model.CPInstance;
import com.liferay.commerce.product.model.CPInstanceUnitOfMeasure;
import com.liferay.commerce.product.model.CommerceChannel;
import com.liferay.commerce.product.model.CommerceChannelRelTable;
import com.liferay.commerce.product.service.CPInstanceLocalService;
import com.liferay.commerce.product.service.CPInstanceUnitOfMeasureLocalService;
import com.liferay.petra.sql.dsl.Column;
import com.liferay.petra.sql.dsl.DSLFunctionFactoryUtil;
import com.liferay.petra.sql.dsl.DSLQueryFactoryUtil;
import com.liferay.petra.sql.dsl.Table;
import com.liferay.petra.sql.dsl.base.BaseTable;
import com.liferay.petra.sql.dsl.expression.Expression;
import com.liferay.petra.sql.dsl.expression.Predicate;
import com.liferay.petra.sql.dsl.query.DSLQuery;
import com.liferay.petra.sql.dsl.spi.expression.Scalar;
import com.liferay.petra.sql.dsl.spi.query.QueryTable;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.aop.AopService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.GroupTable;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.security.permission.InlineSQLHelperUtil;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.transaction.Propagation;
import com.liferay.portal.kernel.transaction.Transactional;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.BigDecimalUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;

import java.math.BigDecimal;

import java.sql.Types;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Luca Pellizzon
 * @author Alessio Antonio Rendina
 */
@Component(
	property = "model.class.name=com.liferay.commerce.inventory.model.CommerceInventoryWarehouseItem",
	service = AopService.class
)
public class CommerceInventoryWarehouseItemLocalServiceImpl
	extends CommerceInventoryWarehouseItemLocalServiceBaseImpl {

	@Override
	public CommerceInventoryWarehouseItem addCommerceInventoryWarehouseItem(
			String externalReferenceCode, long userId,
			long commerceInventoryWarehouseId, BigDecimal quantity,
			BigDecimal reservedQuantity, String sku, String unitOfMeasureKey)
		throws PortalException {

		User user = _userLocalService.getUser(userId);

		_validateSku(commerceInventoryWarehouseId, sku, unitOfMeasureKey);
		_validateUnitOfMeasureKey(user.getCompanyId(), sku, unitOfMeasureKey);

		long commerceInventoryWarehouseItemId = counterLocalService.increment();

		CommerceInventoryWarehouseItem commerceInventoryWarehouseItem =
			commerceInventoryWarehouseItemPersistence.create(
				commerceInventoryWarehouseItemId);

		commerceInventoryWarehouseItem.setExternalReferenceCode(
			externalReferenceCode);
		commerceInventoryWarehouseItem.setCompanyId(user.getCompanyId());
		commerceInventoryWarehouseItem.setUserId(user.getUserId());
		commerceInventoryWarehouseItem.setUserName(user.getFullName());
		commerceInventoryWarehouseItem.setCommerceInventoryWarehouseId(
			commerceInventoryWarehouseId);
		commerceInventoryWarehouseItem.setQuantity(quantity);
		commerceInventoryWarehouseItem.setReservedQuantity(reservedQuantity);
		commerceInventoryWarehouseItem.setSku(sku);
		commerceInventoryWarehouseItem.setUnitOfMeasureKey(
			_normalizeUnitOfMeasureKey(
				user.getCompanyId(), sku, unitOfMeasureKey));

		return commerceInventoryWarehouseItemPersistence.update(
			commerceInventoryWarehouseItem);
	}

	@Override
	public CommerceInventoryWarehouseItem
			addOrUpdateCommerceInventoryWarehouseItem(
				String externalReferenceCode, long companyId, long userId,
				long commerceInventoryWarehouseId, BigDecimal quantity,
				BigDecimal reservedQuantity, String sku,
				String unitOfMeasureKey)
		throws PortalException {

		CommerceInventoryWarehouseItem commerceInventoryWarehouseItem = null;

		if (Validator.isNull(externalReferenceCode)) {
			commerceInventoryWarehouseItem =
				commerceInventoryWarehouseItemPersistence.fetchByCIWI_S_U(
					commerceInventoryWarehouseId, sku, unitOfMeasureKey);
		}
		else {
			commerceInventoryWarehouseItem =
				commerceInventoryWarehouseItemPersistence.fetchByERC_C(
					externalReferenceCode, companyId);
		}

		if (commerceInventoryWarehouseItem != null) {
			return commerceInventoryWarehouseItemLocalService.
				updateCommerceInventoryWarehouseItem(
					userId,
					commerceInventoryWarehouseItem.
						getCommerceInventoryWarehouseItemId(),
					quantity, reservedQuantity, unitOfMeasureKey,
					commerceInventoryWarehouseItem.getMvccVersion());
		}

		return commerceInventoryWarehouseItemLocalService.
			addCommerceInventoryWarehouseItem(
				externalReferenceCode, userId, commerceInventoryWarehouseId,
				quantity, reservedQuantity, sku, unitOfMeasureKey);
	}

	@Override
	public int countItemsByCompanyId(
		long companyId, String sku, boolean replacePermissionCheck) {

		DSLQuery dslQuery = DSLQueryFactoryUtil.select(
			CommerceInventoryWarehouseItemTable.INSTANCE.sku,
			CommerceInventoryWarehouseItemTable.INSTANCE.unitOfMeasureKey
		).from(
			CommerceInventoryWarehouseItemTable.INSTANCE
		).leftJoinOn(
			CommerceInventoryWarehouseTable.INSTANCE,
			CommerceInventoryWarehouseItemTable.INSTANCE.
				commerceInventoryWarehouseId.eq(
					CommerceInventoryWarehouseTable.INSTANCE.
						commerceInventoryWarehouseId)
		).where(
			CommerceInventoryWarehouseItemTable.INSTANCE.companyId.eq(
				companyId
			).and(
				() -> {
					if (Validator.isNull(sku)) {
						return null;
					}

					return DSLFunctionFactoryUtil.lower(
						CommerceInventoryWarehouseItemTable.INSTANCE.sku
					).like(
						StringPool.PERCENT + StringUtil.toLowerCase(sku) +
							StringPool.PERCENT
					);
				}
			)
		);

		if (replacePermissionCheck) {
			Column<?, Long> commerceInventoryWarehouseIdColumn =
				CommerceInventoryWarehouseItemTable.INSTANCE.
					commerceInventoryWarehouseId;

			dslQuery = InlineSQLHelperUtil.replacePermissionCheck(
				dslQuery, CommerceInventoryWarehouse.class,
				commerceInventoryWarehouseIdColumn, 0);
		}

		Table<CommerceInventoryWarehouseItemTable> tempCIWarehouseItemTable =
			dslQuery.as(
				"tempCIWarehouseItem",
				CommerceInventoryWarehouseItemTable.INSTANCE);

		return dslQueryCount(
			DSLQueryFactoryUtil.count(
			).from(
				DSLQueryFactoryUtil.select(
					tempCIWarehouseItemTable.getColumn("sku", String.class),
					tempCIWarehouseItemTable.getColumn(
						"unitOfMeasureKey", String.class)
				).from(
					tempCIWarehouseItemTable
				).groupBy(
					tempCIWarehouseItemTable.getColumn("sku", String.class),
					tempCIWarehouseItemTable.getColumn(
						"unitOfMeasureKey", String.class)
				).as(
					"count_sku_uom"
				)
			));
	}

	@Override
	public void deleteCommerceInventoryWarehouseItems(
		long commerceInventoryWarehouseId) {

		commerceInventoryWarehouseItemPersistence.
			removeByCommerceInventoryWarehouseId(commerceInventoryWarehouseId);
	}

	@Override
	public void deleteCommerceInventoryWarehouseItems(
		long companyId, String sku, String unitOfMeasureKey) {

		commerceInventoryWarehouseItemPersistence.removeByC_S_U(
			companyId, sku, unitOfMeasureKey);
	}

	@Override
	public void deleteCommerceInventoryWarehouseItemsByCompanyId(
		long companyId) {

		commerceInventoryWarehouseItemPersistence.removeByCompanyId(companyId);
	}

	@Override
	public CommerceInventoryWarehouseItem fetchCommerceInventoryWarehouseItem(
		long commerceInventoryWarehouseId, String sku,
		String unitOfMeasureKey) {

		return commerceInventoryWarehouseItemPersistence.fetchByCIWI_S_U(
			commerceInventoryWarehouseId, sku, unitOfMeasureKey);
	}

	@Override
	public List<Long> getCommerceInventoryWarehouseIds(
		long companyId, String sku, String unitOfMeasureKey) {

		DSLQuery dslQuery = DSLQueryFactoryUtil.selectDistinct(
			CommerceInventoryWarehouseItemTable.INSTANCE.
				commerceInventoryWarehouseId
		).from(
			CommerceInventoryWarehouseItemTable.INSTANCE
		).leftJoinOn(
			CommerceInventoryWarehouseTable.INSTANCE,
			CommerceInventoryWarehouseItemTable.INSTANCE.
				commerceInventoryWarehouseId.eq(
					CommerceInventoryWarehouseTable.INSTANCE.
						commerceInventoryWarehouseId)
		).where(
			CommerceInventoryWarehouseItemTable.INSTANCE.companyId.eq(
				companyId
			).and(
				CommerceInventoryWarehouseItemTable.INSTANCE.sku.eq(sku)
			).and(
				() -> {
					if (Validator.isNull(unitOfMeasureKey)) {
						return null;
					}

					return CommerceInventoryWarehouseItemTable.INSTANCE.
						unitOfMeasureKey.eq(unitOfMeasureKey);
				}
			)
		);

		return dslQuery(dslQuery);
	}

	@Override
	public CommerceInventoryWarehouseItem getCommerceInventoryWarehouseItem(
			long commerceInventoryWarehouseId, String sku,
			String unitOfMeasureKey)
		throws PortalException {

		return commerceInventoryWarehouseItemPersistence.findByCIWI_S_U(
			commerceInventoryWarehouseId, sku, unitOfMeasureKey);
	}

	@Override
	public List<CommerceInventoryWarehouseItem>
		getCommerceInventoryWarehouseItems(
			long commerceInventoryWarehouseId, int start, int end) {

		return commerceInventoryWarehouseItemPersistence.
			findByCommerceInventoryWarehouseId(
				commerceInventoryWarehouseId, start, end);
	}

	@Override
	public List<CommerceInventoryWarehouseItem>
		getCommerceInventoryWarehouseItemsByCompanyId(
			long companyId, int start, int end) {

		return commerceInventoryWarehouseItemPersistence.findByCompanyId(
			companyId, start, end);
	}

	@Override
	public List<CommerceInventoryWarehouseItem>
		getCommerceInventoryWarehouseItemsByCompanyIdSkuAndUnitOfMeasureKey(
			long companyId, String sku, String unitOfMeasureKey, int start,
			int end, boolean replacePermissionCheck) {

		DSLQuery dslQuery = DSLQueryFactoryUtil.select(
			CommerceInventoryWarehouseItemTable.INSTANCE
		).from(
			CommerceInventoryWarehouseItemTable.INSTANCE
		).leftJoinOn(
			CommerceInventoryWarehouseTable.INSTANCE,
			CommerceInventoryWarehouseItemTable.INSTANCE.
				commerceInventoryWarehouseId.eq(
					CommerceInventoryWarehouseTable.INSTANCE.
						commerceInventoryWarehouseId)
		).where(
			CommerceInventoryWarehouseItemTable.INSTANCE.companyId.eq(
				companyId
			).and(
				CommerceInventoryWarehouseItemTable.INSTANCE.sku.eq(sku)
			).and(
				() -> {
					if (Validator.isNull(unitOfMeasureKey)) {
						return null;
					}

					return CommerceInventoryWarehouseItemTable.INSTANCE.
						unitOfMeasureKey.eq(unitOfMeasureKey);
				}
			)
		).limit(
			start, end
		);

		if (replacePermissionCheck) {
			Column<?, Long> commerceInventoryWarehouseIdColumn =
				CommerceInventoryWarehouseItemTable.INSTANCE.
					commerceInventoryWarehouseId;

			dslQuery = InlineSQLHelperUtil.replacePermissionCheck(
				dslQuery, CommerceInventoryWarehouse.class,
				commerceInventoryWarehouseIdColumn, 0);
		}

		return dslQuery(dslQuery);
	}

	@Override
	public List<CommerceInventoryWarehouseItem>
		getCommerceInventoryWarehouseItemsByModifiedDate(
			long companyId, Date startDate, Date endDate, int start, int end) {

		return dslQuery(
			DSLQueryFactoryUtil.select(
				CommerceInventoryWarehouseItemTable.INSTANCE
			).from(
				CommerceInventoryWarehouseItemTable.INSTANCE
			).where(
				CommerceInventoryWarehouseItemTable.INSTANCE.companyId.eq(
					companyId
				).and(
					CommerceInventoryWarehouseItemTable.INSTANCE.modifiedDate.
						gte(startDate)
				).and(
					CommerceInventoryWarehouseItemTable.INSTANCE.modifiedDate.
						lt(endDate)
				)
			).orderBy(
				CommerceInventoryWarehouseItemTable.INSTANCE.sku.ascending(),
				CommerceInventoryWarehouseItemTable.INSTANCE.unitOfMeasureKey.
					ascending()
			));
	}

	@Override
	public int getCommerceInventoryWarehouseItemsCount(
		long commerceInventoryWarehouseId) {

		return commerceInventoryWarehouseItemPersistence.
			countByCommerceInventoryWarehouseId(commerceInventoryWarehouseId);
	}

	@Override
	public int getCommerceInventoryWarehouseItemsCount(
		long companyId, long accountEntryId, long groupId, String sku,
		String unitOfMeasureKey) {

		return dslQueryCount(
			DSLQueryFactoryUtil.countDistinct(
				CommerceInventoryWarehouseItemTable.INSTANCE.
					commerceInventoryWarehouseItemId
			).from(
				CommerceInventoryWarehouseItemTable.INSTANCE
			).innerJoinON(
				CommerceChannelRelTable.INSTANCE,
				CommerceChannelRelTable.INSTANCE.classNameId.eq(
					_portal.getClassNameId(
						CommerceInventoryWarehouse.class.getName())
				).and(
					CommerceChannelRelTable.INSTANCE.classPK.eq(
						CommerceInventoryWarehouseItemTable.INSTANCE.
							commerceInventoryWarehouseId)
				)
			).innerJoinON(
				GroupTable.INSTANCE,
				GroupTable.INSTANCE.classNameId.eq(
					_portal.getClassNameId(CommerceChannel.class.getName())
				).and(
					GroupTable.INSTANCE.classPK.eq(
						CommerceChannelRelTable.INSTANCE.commerceChannelId)
				)
			).innerJoinON(
				CommerceInventoryWarehouseTable.INSTANCE,
				CommerceInventoryWarehouseTable.INSTANCE.
					commerceInventoryWarehouseId.eq(
						CommerceInventoryWarehouseItemTable.INSTANCE.
							commerceInventoryWarehouseId)
			).leftJoinOn(
				CommerceInventoryWarehouseRelTable.INSTANCE,
				CommerceInventoryWarehouseTable.INSTANCE.
					commerceInventoryWarehouseId.eq(
						CommerceInventoryWarehouseRelTable.INSTANCE.
							commerceInventoryWarehouseId)
			).where(
				CommerceInventoryWarehouseItemTable.INSTANCE.companyId.eq(
					companyId
				).and(
					CommerceInventoryWarehouseItemTable.INSTANCE.sku.eq(sku)
				).and(
					() -> {
						if (Validator.isNull(unitOfMeasureKey)) {
							return null;
						}

						return CommerceInventoryWarehouseItemTable.INSTANCE.
							unitOfMeasureKey.eq(unitOfMeasureKey);
					}
				).and(
					CommerceInventoryWarehouseTable.INSTANCE.active.eq(true)
				).and(
					GroupTable.INSTANCE.groupId.eq(groupId)
				).and(
					CommerceInventoryWarehouseRelTable.INSTANCE.classPK.eq(
						accountEntryId
					).and(
						CommerceInventoryWarehouseRelTable.INSTANCE.classNameId.
							eq(
								_portal.getClassNameId(
									AccountEntry.class.getName()))
					).or(
						() -> {
							Long[] accountGroupIds = ArrayUtil.toLongArray(
								_accountGroupLocalService.getAccountGroupIds(
									accountEntryId));

							if (accountGroupIds.length <= 0) {
								accountGroupIds = new Long[] {0L};
							}

							return CommerceInventoryWarehouseRelTable.INSTANCE.
								classPK.in(
									accountGroupIds
								).and(
									CommerceInventoryWarehouseRelTable.INSTANCE.
										classNameId.eq(
											_portal.getClassNameId(
												AccountGroup.class.getName()))
								).withParentheses();
						}
					).or(
						CommerceInventoryWarehouseRelTable.INSTANCE.classPK.
							isNull()
					).withParentheses()
				)
			));
	}

	@Override
	public int getCommerceInventoryWarehouseItemsCount(
		long companyId, String sku, String unitOfMeasureKey,
		boolean replacePermissionCheck) {

		DSLQuery dslQuery = DSLQueryFactoryUtil.countDistinct(
			CommerceInventoryWarehouseItemTable.INSTANCE.
				commerceInventoryWarehouseItemId
		).from(
			CommerceInventoryWarehouseItemTable.INSTANCE
		).leftJoinOn(
			CommerceInventoryWarehouseTable.INSTANCE,
			CommerceInventoryWarehouseItemTable.INSTANCE.
				commerceInventoryWarehouseId.eq(
					CommerceInventoryWarehouseTable.INSTANCE.
						commerceInventoryWarehouseId)
		).where(
			CommerceInventoryWarehouseItemTable.INSTANCE.companyId.eq(
				companyId
			).and(
				() -> {
					if (Validator.isNull(sku)) {
						return null;
					}

					return DSLFunctionFactoryUtil.lower(
						CommerceInventoryWarehouseItemTable.INSTANCE.sku
					).like(
						StringPool.PERCENT + StringUtil.toLowerCase(sku) +
							StringPool.PERCENT
					);
				}
			).and(
				() -> {
					if (Validator.isNull(unitOfMeasureKey)) {
						return null;
					}

					return DSLFunctionFactoryUtil.lower(
						CommerceInventoryWarehouseItemTable.INSTANCE.
							unitOfMeasureKey
					).like(
						StringPool.PERCENT +
							StringUtil.toLowerCase(unitOfMeasureKey) +
								StringPool.PERCENT
					);
				}
			)
		);

		if (replacePermissionCheck) {
			Column<?, Long> commerceInventoryWarehouseIdColumn =
				CommerceInventoryWarehouseItemTable.INSTANCE.
					commerceInventoryWarehouseId;

			dslQuery = InlineSQLHelperUtil.replacePermissionCheck(
				dslQuery, CommerceInventoryWarehouse.class,
				commerceInventoryWarehouseIdColumn, 0);
		}

		return dslQueryCount(dslQuery);
	}

	@Override
	public int getCommerceInventoryWarehouseItemsCountByCompanyId(
		long companyId) {

		return commerceInventoryWarehouseItemPersistence.countByCompanyId(
			companyId);
	}

	@Override
	public int getCommerceInventoryWarehouseItemsCountByModifiedDate(
		long companyId, Date startDate, Date endDate) {

		return dslQueryCount(
			DSLQueryFactoryUtil.countDistinct(
				CommerceInventoryWarehouseItemTable.INSTANCE.
					commerceInventoryWarehouseItemId
			).from(
				CommerceInventoryWarehouseItemTable.INSTANCE
			).where(
				CommerceInventoryWarehouseItemTable.INSTANCE.companyId.eq(
					companyId
				).and(
					CommerceInventoryWarehouseItemTable.INSTANCE.modifiedDate.
						gte(
							startDate
						).and(
							CommerceInventoryWarehouseItemTable.INSTANCE.
								modifiedDate.lt(endDate)
						)
				)
			));
	}

	@Override
	public List<CIWarehouseItem> getItemsByCompanyId(
		long companyId, String sku, int start, int end,
		boolean replacePermissionCheck) {

		DSLQuery dslQuery = DSLQueryFactoryUtil.select(
			CommerceInventoryWarehouseItemTable.INSTANCE.sku,
			CommerceInventoryWarehouseItemTable.INSTANCE.unitOfMeasureKey,
			DSLFunctionFactoryUtil.sum(
				CommerceInventoryWarehouseItemTable.INSTANCE.quantity
			).as(
				"SUM_STOCK"
			),
			DSLFunctionFactoryUtil.min(
				BookedQuantityTable.INSTANCE.sumBookedColumn
			).as(
				BookedQuantityTable.INSTANCE.sumBookedColumn.getName()
			),
			DSLFunctionFactoryUtil.min(
				ReplenishmentQuantityTable.INSTANCE.sumAwaitingColumn
			).as(
				ReplenishmentQuantityTable.INSTANCE.sumAwaitingColumn.getName()
			)
		).from(
			CommerceInventoryWarehouseItemTable.INSTANCE
		).leftJoinOn(
			CommerceInventoryWarehouseTable.INSTANCE,
			CommerceInventoryWarehouseItemTable.INSTANCE.
				commerceInventoryWarehouseId.eq(
					CommerceInventoryWarehouseTable.INSTANCE.
						commerceInventoryWarehouseId)
		).leftJoinOn(
			BookedQuantityTable.INSTANCE.getQueryTable(companyId),
			CommerceInventoryWarehouseItemTable.INSTANCE.sku.eq(
				BookedQuantityTable.INSTANCE.skuColumn
			).and(
				Predicate.withParentheses(
					CommerceInventoryWarehouseItemTable.INSTANCE.
						unitOfMeasureKey.eq(
							BookedQuantityTable.INSTANCE.unitOfMeasureKeyColumn
						).or(
							Predicate.withParentheses(
								CommerceInventoryWarehouseItemTable.INSTANCE.
									unitOfMeasureKey.isNull(
									).and(
										BookedQuantityTable.INSTANCE.
											unitOfMeasureKeyColumn.isNull()
									))
						))
			)
		).leftJoinOn(
			ReplenishmentQuantityTable.INSTANCE.getQueryTable(companyId),
			CommerceInventoryWarehouseItemTable.INSTANCE.sku.eq(
				ReplenishmentQuantityTable.INSTANCE.skuColumn
			).and(
				Predicate.withParentheses(
					CommerceInventoryWarehouseItemTable.INSTANCE.
						unitOfMeasureKey.eq(
							ReplenishmentQuantityTable.INSTANCE.
								unitOfMeasureKeyColumn
						).or(
							Predicate.withParentheses(
								CommerceInventoryWarehouseItemTable.INSTANCE.
									unitOfMeasureKey.isNull(
									).and(
										ReplenishmentQuantityTable.INSTANCE.
											unitOfMeasureKeyColumn.isNull()
									))
						))
			)
		).where(
			CommerceInventoryWarehouseItemTable.INSTANCE.companyId.eq(
				companyId
			).and(
				() -> {
					if (Validator.isNull(sku)) {
						return null;
					}

					return DSLFunctionFactoryUtil.lower(
						CommerceInventoryWarehouseItemTable.INSTANCE.sku
					).like(
						StringPool.PERCENT + StringUtil.toLowerCase(sku) +
							StringPool.PERCENT
					);
				}
			)
		).groupBy(
			CommerceInventoryWarehouseItemTable.INSTANCE.sku,
			CommerceInventoryWarehouseItemTable.INSTANCE.unitOfMeasureKey
		).orderBy(
			CommerceInventoryWarehouseItemTable.INSTANCE.sku.ascending(),
			CommerceInventoryWarehouseItemTable.INSTANCE.unitOfMeasureKey.
				ascending()
		).limit(
			start, end
		);

		if (replacePermissionCheck) {
			Column<?, Long> commerceInventoryWarehouseIdColumn =
				CommerceInventoryWarehouseItemTable.INSTANCE.
					commerceInventoryWarehouseId;

			dslQuery = InlineSQLHelperUtil.replacePermissionCheck(
				dslQuery, CommerceInventoryWarehouse.class,
				commerceInventoryWarehouseIdColumn, 0);
		}

		List<Object[]> sumStocks = dslQuery(dslQuery);

		List<CIWarehouseItem> ciWarehouseItems = new ArrayList<>();

		for (Object[] stock : sumStocks) {
			if (stock == null) {
				continue;
			}

			String skuCode = StringPool.BLANK;

			if ((stock.length > 0) && (stock[0] != null)) {
				skuCode = (String)stock[0];
			}

			String unitOfMeasureKey = StringPool.BLANK;

			if ((stock.length > 1) && (stock[1] != null)) {
				unitOfMeasureKey = (String)stock[1];
			}

			BigDecimal stockQuantity = BigDecimal.ZERO;

			if ((stock.length > 2) && (stock[2] != null)) {
				stockQuantity = (BigDecimal)stock[2];
			}

			BigDecimal bookedQuantity = BigDecimal.ZERO;

			if ((stock.length > 3) && (stock[3] != null)) {
				bookedQuantity = BigDecimalUtil.get(stock[3], BigDecimal.ZERO);
			}

			BigDecimal replenishmentQuantity = BigDecimal.ZERO;

			if ((stock.length > 4) && (stock[4] != null)) {
				replenishmentQuantity = BigDecimalUtil.get(
					stock[4], BigDecimal.ZERO);
			}

			ciWarehouseItems.add(
				new CIWarehouseItem(
					skuCode, unitOfMeasureKey, bookedQuantity,
					replenishmentQuantity, stockQuantity));
		}

		return ciWarehouseItems;
	}

	@Override
	public BigDecimal getStockQuantity(
		long companyId, long accountEntryId, long groupId, String sku,
		String unitOfMeasureKey) {

		Iterable<BigDecimal> iterable = dslQuery(
			DSLQueryFactoryUtil.select(
				DSLFunctionFactoryUtil.sum(
					DSLFunctionFactoryUtil.subtract(
						(Expression<Number>)_getExpression(
							CommerceInventoryWarehouseItemTable.INSTANCE.
								quantity),
						(Expression<Number>)_getExpression(
							CommerceInventoryWarehouseItemTable.INSTANCE.
								reservedQuantity))
				).as(
					"SUM_VALUE"
				)
			).from(
				CommerceInventoryWarehouseItemTable.INSTANCE
			).innerJoinON(
				CommerceChannelRelTable.INSTANCE,
				CommerceChannelRelTable.INSTANCE.classNameId.eq(
					_portal.getClassNameId(
						CommerceInventoryWarehouse.class.getName())
				).and(
					CommerceChannelRelTable.INSTANCE.classPK.eq(
						CommerceInventoryWarehouseItemTable.INSTANCE.
							commerceInventoryWarehouseId)
				)
			).innerJoinON(
				GroupTable.INSTANCE,
				GroupTable.INSTANCE.classNameId.eq(
					_portal.getClassNameId(CommerceChannel.class.getName())
				).and(
					GroupTable.INSTANCE.classPK.eq(
						CommerceChannelRelTable.INSTANCE.commerceChannelId)
				)
			).innerJoinON(
				CommerceInventoryWarehouseTable.INSTANCE,
				CommerceInventoryWarehouseTable.INSTANCE.
					commerceInventoryWarehouseId.eq(
						CommerceInventoryWarehouseItemTable.INSTANCE.
							commerceInventoryWarehouseId)
			).leftJoinOn(
				CommerceInventoryWarehouseRelTable.INSTANCE,
				CommerceInventoryWarehouseTable.INSTANCE.
					commerceInventoryWarehouseId.eq(
						CommerceInventoryWarehouseRelTable.INSTANCE.
							commerceInventoryWarehouseId)
			).where(
				CommerceInventoryWarehouseItemTable.INSTANCE.companyId.eq(
					companyId
				).and(
					CommerceInventoryWarehouseItemTable.INSTANCE.sku.eq(sku)
				).and(
					() -> {
						if (Validator.isNull(unitOfMeasureKey)) {
							return null;
						}

						return CommerceInventoryWarehouseItemTable.INSTANCE.
							unitOfMeasureKey.eq(unitOfMeasureKey);
					}
				).and(
					CommerceInventoryWarehouseTable.INSTANCE.active.eq(true)
				).and(
					GroupTable.INSTANCE.groupId.eq(groupId)
				).and(
					CommerceInventoryWarehouseRelTable.INSTANCE.classPK.eq(
						accountEntryId
					).and(
						CommerceInventoryWarehouseRelTable.INSTANCE.classNameId.
							eq(
								_portal.getClassNameId(
									AccountEntry.class.getName()))
					).or(
						() -> {
							Long[] accountGroupIds = ArrayUtil.toLongArray(
								_accountGroupLocalService.getAccountGroupIds(
									accountEntryId));

							if (accountGroupIds.length <= 0) {
								accountGroupIds = new Long[] {0L};
							}

							return CommerceInventoryWarehouseRelTable.INSTANCE.
								classPK.in(
									accountGroupIds
								).and(
									CommerceInventoryWarehouseRelTable.INSTANCE.
										classNameId.eq(
											_portal.getClassNameId(
												AccountGroup.class.getName()))
								).withParentheses();
						}
					).or(
						CommerceInventoryWarehouseRelTable.INSTANCE.classPK.
							isNull()
					).withParentheses()
				)
			));

		Iterator<BigDecimal> iterator = iterable.iterator();

		BigDecimal stockQuantity = iterator.next();

		if (stockQuantity == null) {
			return BigDecimal.ZERO;
		}

		return stockQuantity;
	}

	@Override
	public BigDecimal getStockQuantity(
		long companyId, String sku, String unitOfMeasureKey) {

		Iterable<BigDecimal> iterable = dslQuery(
			DSLQueryFactoryUtil.select(
				DSLFunctionFactoryUtil.sum(
					DSLFunctionFactoryUtil.subtract(
						(Expression<Number>)_getExpression(
							CommerceInventoryWarehouseItemTable.INSTANCE.
								quantity),
						(Expression<Number>)_getExpression(
							CommerceInventoryWarehouseItemTable.INSTANCE.
								reservedQuantity))
				).as(
					"SUM_VALUE"
				)
			).from(
				CommerceInventoryWarehouseItemTable.INSTANCE
			).innerJoinON(
				CommerceInventoryWarehouseTable.INSTANCE,
				CommerceInventoryWarehouseTable.INSTANCE.
					commerceInventoryWarehouseId.eq(
						CommerceInventoryWarehouseItemTable.INSTANCE.
							commerceInventoryWarehouseId)
			).where(
				CommerceInventoryWarehouseItemTable.INSTANCE.companyId.eq(
					companyId
				).and(
					CommerceInventoryWarehouseItemTable.INSTANCE.sku.eq(sku)
				).and(
					() -> {
						if (Validator.isNull(unitOfMeasureKey)) {
							return null;
						}

						return CommerceInventoryWarehouseItemTable.INSTANCE.
							unitOfMeasureKey.eq(unitOfMeasureKey);
					}
				).and(
					CommerceInventoryWarehouseTable.INSTANCE.active.eq(true)
				)
			));

		Iterator<BigDecimal> iterator = iterable.iterator();

		BigDecimal stockQuantity = iterator.next();

		if (stockQuantity == null) {
			return BigDecimal.ZERO;
		}

		return stockQuantity;
	}

	@Override
	public CommerceInventoryWarehouseItem
			increaseCommerceInventoryWarehouseItemQuantity(
				long userId, long commerceInventoryWarehouseItemId,
				BigDecimal quantity)
		throws PortalException {

		CommerceInventoryWarehouseItem commerceInventoryWarehouseItem =
			commerceInventoryWarehouseItemPersistence.findByPrimaryKey(
				commerceInventoryWarehouseItemId);

		quantity = quantity.add(commerceInventoryWarehouseItem.getQuantity());

		commerceInventoryWarehouseItem.setQuantity(quantity);

		commerceInventoryWarehouseItem =
			commerceInventoryWarehouseItemPersistence.update(
				commerceInventoryWarehouseItem);

		CommerceInventoryAuditType commerceInventoryAuditType =
			_commerceInventoryAuditTypeRegistry.getCommerceInventoryAuditType(
				CommerceInventoryConstants.AUDIT_TYPE_INCREASE_QUANTITY);

		_commerceInventoryAuditLocalService.addCommerceInventoryAudit(
			userId, commerceInventoryAuditType.getType(),
			commerceInventoryAuditType.getLog(null), quantity,
			commerceInventoryWarehouseItem.getSku(),
			commerceInventoryWarehouseItem.getUnitOfMeasureKey());

		return commerceInventoryWarehouseItem;
	}

	@Override
	@Transactional(
		propagation = Propagation.REQUIRED, readOnly = false,
		rollbackFor = Exception.class
	)
	public void moveQuantitiesBetweenWarehouses(
			long userId, long fromCommerceInventoryWarehouseId,
			long toCommerceInventoryWarehouseId, BigDecimal quantity,
			String sku, String unitOfMeasureKey)
		throws PortalException {

		CommerceInventoryWarehouseItem fromWarehouseItem =
			commerceInventoryWarehouseItemPersistence.findByCIWI_S_U(
				fromCommerceInventoryWarehouseId, sku, unitOfMeasureKey);

		BigDecimal fromWarehouseItemQuantity = fromWarehouseItem.getQuantity();

		if (quantity.compareTo(fromWarehouseItemQuantity) == 1) {
			throw new PortalException("Quantity to transfer unavailable");
		}

		commerceInventoryWarehouseItemLocalService.
			updateCommerceInventoryWarehouseItem(
				userId, fromWarehouseItem.getCommerceInventoryWarehouseItemId(),
				fromWarehouseItemQuantity.subtract(quantity),
				fromWarehouseItem.getReservedQuantity(),
				fromWarehouseItem.getUnitOfMeasureKey(),
				fromWarehouseItem.getMvccVersion());

		CommerceInventoryWarehouseItem toWarehouseItem =
			commerceInventoryWarehouseItemPersistence.findByCIWI_S_U(
				toCommerceInventoryWarehouseId, sku, unitOfMeasureKey);

		BigDecimal toWarehouseItemQuantity = toWarehouseItem.getQuantity();

		commerceInventoryWarehouseItemLocalService.
			updateCommerceInventoryWarehouseItem(
				userId, toWarehouseItem.getCommerceInventoryWarehouseItemId(),
				toWarehouseItemQuantity.add(quantity),
				toWarehouseItem.getReservedQuantity(),
				toWarehouseItem.getUnitOfMeasureKey(),
				toWarehouseItem.getMvccVersion());

		CommerceInventoryAuditType commerceInventoryAuditType =
			_commerceInventoryAuditTypeRegistry.getCommerceInventoryAuditType(
				CommerceInventoryConstants.AUDIT_TYPE_MOVE_QUANTITY);

		_commerceInventoryAuditLocalService.addCommerceInventoryAudit(
			userId, commerceInventoryAuditType.getType(),
			commerceInventoryAuditType.getLog(
				HashMapBuilder.put(
					CommerceInventoryAuditTypeConstants.FROM,
					() -> {
						CommerceInventoryWarehouse
							fromCommerceInventoryWarehouse =
								fromWarehouseItem.
									getCommerceInventoryWarehouse();

						return String.valueOf(
							fromCommerceInventoryWarehouse.getName());
					}
				).put(
					CommerceInventoryAuditTypeConstants.TO,
					() -> {
						CommerceInventoryWarehouse
							toCommerceInventoryWarehouse =
								toWarehouseItem.getCommerceInventoryWarehouse();

						return String.valueOf(
							toCommerceInventoryWarehouse.getName());
					}
				).build()),
			quantity, sku, unitOfMeasureKey);
	}

	@Override
	public CommerceInventoryWarehouseItem updateCommerceInventoryWarehouseItem(
			long userId, long commerceInventoryWarehouseItemId,
			BigDecimal quantity, BigDecimal reservedQuantity,
			String unitOfMeasureKey, long mvccVersion)
		throws PortalException {

		CommerceInventoryWarehouseItem commerceInventoryWarehouseItem =
			commerceInventoryWarehouseItemPersistence.findByPrimaryKey(
				commerceInventoryWarehouseItemId);

		if (commerceInventoryWarehouseItem.getMvccVersion() != mvccVersion) {
			throw new MVCCException();
		}

		_validateUnitOfMeasureKey(
			commerceInventoryWarehouseItem.getCompanyId(),
			commerceInventoryWarehouseItem.getSku(), unitOfMeasureKey);

		commerceInventoryWarehouseItem.setQuantity(quantity);
		commerceInventoryWarehouseItem.setReservedQuantity(reservedQuantity);

		commerceInventoryWarehouseItem.setUnitOfMeasureKey(
			_normalizeUnitOfMeasureKey(
				commerceInventoryWarehouseItem.getCompanyId(),
				commerceInventoryWarehouseItem.getSku(), unitOfMeasureKey));

		commerceInventoryWarehouseItem =
			commerceInventoryWarehouseItemPersistence.update(
				commerceInventoryWarehouseItem);

		CommerceInventoryAuditType commerceInventoryAuditType =
			_commerceInventoryAuditTypeRegistry.getCommerceInventoryAuditType(
				CommerceInventoryConstants.AUDIT_TYPE_UPDATE_WAREHOUSE_ITEM);

		CommerceInventoryWarehouse commerceInventoryWarehouse =
			commerceInventoryWarehouseItem.getCommerceInventoryWarehouse();

		_commerceInventoryAuditLocalService.addCommerceInventoryAudit(
			userId, commerceInventoryAuditType.getType(),
			commerceInventoryAuditType.getLog(
				HashMapBuilder.put(
					CommerceInventoryAuditTypeConstants.WAREHOUSE,
					String.valueOf(commerceInventoryWarehouse.getName())
				).build()),
			quantity, commerceInventoryWarehouseItem.getSku(),
			commerceInventoryWarehouseItem.getUnitOfMeasureKey());

		return commerceInventoryWarehouseItem;
	}

	private BigDecimal _getBigDecimal(Comparable<?> comparable) {
		if (comparable == null) {
			return BigDecimal.ZERO;
		}

		if (comparable instanceof BigDecimal) {
			return (BigDecimal)comparable;
		}

		String value = comparable.toString();

		if (Validator.isNull(value)) {
			return BigDecimal.ZERO;
		}

		return new BigDecimal(value);
	}

	private Expression<?> _getExpression(Object object) {
		if (object instanceof BigDecimal) {
			object = _getBigDecimal((Comparable<?>)object);
		}
		else if (object instanceof Expression) {
			return (Expression<?>)object;
		}

		return new Scalar<>(object);
	}

	private String _normalizeUnitOfMeasureKey(
			long companyId, String sku, String unitOfMeasureKey)
		throws PortalException {

		List<CPInstanceUnitOfMeasure> cpInstanceUnitOfMeasures =
			_cpInstanceUnitOfMeasureLocalService.getCPInstanceUnitOfMeasures(
				companyId, sku);

		if (Validator.isNull(unitOfMeasureKey) &&
			(cpInstanceUnitOfMeasures.size() == 1)) {

			CPInstanceUnitOfMeasure cpInstanceUnitOfMeasure =
				cpInstanceUnitOfMeasures.get(0);

			return cpInstanceUnitOfMeasure.getKey();
		}

		return unitOfMeasureKey;
	}

	private void _validateSku(
			long commerceInventoryWarehouseId, String sku,
			String unitOfMeasureKey)
		throws PortalException {

		if (Validator.isNull(sku)) {
			throw new CommerceInventoryWarehouseItemSkuException();
		}

		CommerceInventoryWarehouseItem commerceInventoryWarehouseItem =
			commerceInventoryWarehouseItemPersistence.fetchByCIWI_S_U(
				commerceInventoryWarehouseId, sku, unitOfMeasureKey);

		if (commerceInventoryWarehouseItem != null) {
			throw new DuplicateCommerceInventoryWarehouseItemException();
		}
	}

	private void _validateUnitOfMeasureKey(
			long companyId, String sku, String unitOfMeasureKey)
		throws PortalException {

		int cpInstanceUnitOfMeasuresCount =
			_cpInstanceUnitOfMeasureLocalService.
				getCPInstanceUnitOfMeasuresCount(companyId, sku);

		if (Validator.isNull(unitOfMeasureKey)) {
			if (cpInstanceUnitOfMeasuresCount == 1) {
				return;
			}

			if (cpInstanceUnitOfMeasuresCount > 0) {
				throw new CPInstanceUnitOfMeasureKeyException();
			}
		}
		else {
			if (cpInstanceUnitOfMeasuresCount == 0) {
				List<CPInstance> cpInstances =
					_cpInstanceLocalService.getCPInstances(companyId, sku);

				if (cpInstances.isEmpty()) {
					return;
				}

				throw new NoSuchCPInstanceUnitOfMeasureException();
			}

			List<CPInstanceUnitOfMeasure> cpInstanceUnitOfMeasures =
				_cpInstanceUnitOfMeasureLocalService.
					getCPInstanceUnitOfMeasures(companyId, sku);

			for (CPInstanceUnitOfMeasure cpInstanceUnitOfMeasure :
					cpInstanceUnitOfMeasures) {

				if (unitOfMeasureKey.equals(cpInstanceUnitOfMeasure.getKey())) {
					return;
				}
			}

			throw new NoSuchCPInstanceUnitOfMeasureException();
		}
	}

	@Reference
	private AccountGroupLocalService _accountGroupLocalService;

	@Reference
	private CommerceInventoryAuditLocalService
		_commerceInventoryAuditLocalService;

	@Reference
	private CommerceInventoryAuditTypeRegistry
		_commerceInventoryAuditTypeRegistry;

	@Reference
	private CPInstanceLocalService _cpInstanceLocalService;

	@Reference
	private CPInstanceUnitOfMeasureLocalService
		_cpInstanceUnitOfMeasureLocalService;

	@Reference
	private Portal _portal;

	@Reference
	private UserLocalService _userLocalService;

	private static class BookedQuantityTable
		extends BaseTable<BookedQuantityTable> {

		public static final BookedQuantityTable INSTANCE =
			new BookedQuantityTable();

		public QueryTable getQueryTable(long companyId) {
			return new QueryTable(
				BookedQuantityTable.INSTANCE.getTableName(),
				DSLQueryFactoryUtil.select(
					CommerceInventoryBookedQuantityTable.INSTANCE.sku.as(
						skuColumn.getName()),
					CommerceInventoryBookedQuantityTable.INSTANCE.
						unitOfMeasureKey.as(unitOfMeasureKeyColumn.getName()),
					DSLFunctionFactoryUtil.sum(
						CommerceInventoryBookedQuantityTable.INSTANCE.quantity
					).as(
						sumBookedColumn.getName()
					)
				).from(
					CommerceInventoryBookedQuantityTable.INSTANCE
				).where(
					CommerceInventoryBookedQuantityTable.INSTANCE.companyId.eq(
						companyId)
				).groupBy(
					CommerceInventoryBookedQuantityTable.INSTANCE.sku,
					CommerceInventoryBookedQuantityTable.INSTANCE.
						unitOfMeasureKey
				),
				Arrays.asList(
					skuColumn, unitOfMeasureKeyColumn, sumBookedColumn));
		}

		public final Column<BookedQuantityTable, String> skuColumn =
			createColumn(
				"SKU", String.class, Types.VARCHAR, Column.FLAG_DEFAULT);
		public final Column<BookedQuantityTable, BigDecimal> sumBookedColumn =
			createColumn(
				"SUM_BOOKED", BigDecimal.class, Types.DECIMAL,
				Column.FLAG_DEFAULT);
		public final Column<BookedQuantityTable, String>
			unitOfMeasureKeyColumn = createColumn(
				"UNIT_OF_MEASURE_KEY", String.class, Types.VARCHAR,
				Column.FLAG_DEFAULT);

		private BookedQuantityTable() {
			super("BookedQuantityTable", BookedQuantityTable::new);
		}

	}

	private static class ReplenishmentQuantityTable
		extends BaseTable<ReplenishmentQuantityTable> {

		public static final ReplenishmentQuantityTable INSTANCE =
			new ReplenishmentQuantityTable();

		public QueryTable getQueryTable(long companyId) {
			return new QueryTable(
				ReplenishmentQuantityTable.INSTANCE.getTableName(),
				DSLQueryFactoryUtil.select(
					CommerceInventoryReplenishmentItemTable.INSTANCE.sku.as(
						skuColumn.getName()),
					CommerceInventoryReplenishmentItemTable.INSTANCE.
						unitOfMeasureKey.as(unitOfMeasureKeyColumn.getName()),
					DSLFunctionFactoryUtil.sum(
						CommerceInventoryReplenishmentItemTable.INSTANCE.
							quantity
					).as(
						sumAwaitingColumn.getName()
					)
				).from(
					CommerceInventoryReplenishmentItemTable.INSTANCE
				).where(
					CommerceInventoryReplenishmentItemTable.INSTANCE.companyId.
						eq(companyId)
				).groupBy(
					CommerceInventoryReplenishmentItemTable.INSTANCE.sku,
					CommerceInventoryReplenishmentItemTable.INSTANCE.
						unitOfMeasureKey
				),
				Arrays.asList(
					skuColumn, unitOfMeasureKeyColumn, sumAwaitingColumn));
		}

		public final Column<ReplenishmentQuantityTable, String> skuColumn =
			createColumn(
				"SKU", String.class, Types.VARCHAR, Column.FLAG_DEFAULT);
		public final Column<ReplenishmentQuantityTable, BigDecimal>
			sumAwaitingColumn = createColumn(
				"SUM_AWAITING", BigDecimal.class, Types.DECIMAL,
				Column.FLAG_DEFAULT);
		public final Column<ReplenishmentQuantityTable, String>
			unitOfMeasureKeyColumn = createColumn(
				"UNIT_OF_MEASURE_KEY", String.class, Types.VARCHAR,
				Column.FLAG_DEFAULT);

		private ReplenishmentQuantityTable() {
			super(
				"ReplenishmentQuantityTable", ReplenishmentQuantityTable::new);
		}

	}

}