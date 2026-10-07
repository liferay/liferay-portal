/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.internal.upgrade.v15_1_6;

import com.liferay.change.tracking.model.CTCollection;
import com.liferay.change.tracking.service.CTCollectionLocalService;
import com.liferay.commerce.product.constants.CPConfigurationEntrySettingConstants;
import com.liferay.commerce.product.model.CPConfigurationEntry;
import com.liferay.commerce.product.model.CPConfigurationEntrySetting;
import com.liferay.commerce.product.model.CPConfigurationList;
import com.liferay.commerce.product.model.CPDefinition;
import com.liferay.commerce.product.model.CPInstance;
import com.liferay.commerce.product.service.CPConfigurationEntryLocalService;
import com.liferay.commerce.product.service.CPConfigurationEntrySettingLocalService;
import com.liferay.commerce.product.service.CPConfigurationListLocalService;
import com.liferay.commerce.product.service.CPInstanceLocalService;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.petra.string.StringUtil;
import com.liferay.portal.kernel.change.tracking.CTCollectionThreadLocal;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.service.ClassNameLocalService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.workflow.WorkflowConstants;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Lianne Louie
 */
public class CPConfigurationEntryUpgradeProcess extends UpgradeProcess {

	public CPConfigurationEntryUpgradeProcess(
		ClassNameLocalService classNameLocalService,
		CPConfigurationEntryLocalService cpConfigurationEntryLocalService,
		CPConfigurationEntrySettingLocalService
			cpConfigurationEntrySettingLocalService,
		CPConfigurationListLocalService cpConfigurationListLocalService,
		CPInstanceLocalService cpInstanceLocalService,
		CTCollectionLocalService ctCollectionLocalService,
		UserLocalService userLocalService) {

		_classNameLocalService = classNameLocalService;
		_cpConfigurationEntryLocalService = cpConfigurationEntryLocalService;
		_cpConfigurationEntrySettingLocalService =
			cpConfigurationEntrySettingLocalService;
		_cpConfigurationListLocalService = cpConfigurationListLocalService;
		_cpInstanceLocalService = cpInstanceLocalService;
		_ctCollectionLocalService = ctCollectionLocalService;
		_userLocalService = userLocalService;
	}

	@Override
	protected void doUpgrade() throws Exception {
		try (PreparedStatement preparedStatement = connection.prepareStatement(
				StringBundler.concat(
					"select CPConfigurationList.CPConfigurationListId, ",
					"CPConfigurationList.companyId, ",
					"CPDAvailabilityEstimate.commerceAvailabilityEstimateId, ",
					"CPDefinition.CPDefinitionId, CPDefinition.CProductId, ",
					"CPDefinition.CPTaxCategoryId, ",
					"CPDefinition.ctCollectionId, CPDefinition.depth, ",
					"CPDefinition.freeShipping, CPDefinition.groupId, ",
					"CPDefinition.height, CPDefinition.shippable, ",
					"CPDefinition.shippingExtraPrice, ",
					"CPDefinition.shipSeparately, CPDefinition.taxExempt, ",
					"CPDefinition.userId, CPDefinition.version, ",
					"CPDefinition.weight, CPDefinition.width, ",
					"CPDefinitionInventory.allowedOrderQuantities, ",
					"CPDefinitionInventory.backOrders, ",
					"CPDefinitionInventory.CPDefinitionInventoryEngine, ",
					"CPDefinitionInventory.displayAvailability, ",
					"CPDefinitionInventory.displayStockQuantity, ",
					"CPDefinitionInventory.lowStockActivity, ",
					"CPDefinitionInventory.maxOrderQuantity, ",
					"CPDefinitionInventory.minOrderQuantity, ",
					"CPDefinitionInventory.minStockQuantity, ",
					"CPDefinitionInventory.multipleOrderQuantity from ",
					"CPDefinition join CPConfigurationList on ",
					"CPConfigurationList.groupId = CPDefinition.groupId and ",
					"CPConfigurationList.master = ? join ",
					"CPDefinitionInventory on CPDefinition.CPDefinitionId = ",
					"CPDefinitionInventory.CPDefinitionId left join ",
					"CPConfigurationEntry on CPConfigurationEntry.classNameId ",
					"= ? and CPConfigurationEntry.classPK = ",
					"CPDefinition.CPDefinitionId and ",
					"CPConfigurationEntry.CPConfigurationListId = ",
					"CPConfigurationList.CPConfigurationListId left join ",
					"CPDAvailabilityEstimate on CPDefinition.CProductId = ",
					"CPDAvailabilityEstimate.CProductId where ",
					"CPConfigurationEntry.CPConfigurationEntryId is null ",
					"order by CPDefinition.CPDefinitionId, ",
					"CPDefinition.ctCollectionId, ",
					"CPDefinitionInventory.ctCollectionId"))) {

			long classNameId = _classNameLocalService.getClassNameId(
				CPDefinition.class);

			preparedStatement.setBoolean(1, true);
			preparedStatement.setLong(2, classNameId);

			Set<Long> cpDefinitionIds = new HashSet<>();
			Map<Long, Boolean> ctCollectionIdsMap = new HashMap<>();
			Map<Long, String> indexIdsMap = new HashMap<>();

			try (ResultSet resultSet = preparedStatement.executeQuery()) {
				while (resultSet.next()) {
					if (!cpDefinitionIds.add(
							resultSet.getLong("CPDefinitionId"))) {

						continue;
					}

					long cpConfigurationListId = resultSet.getLong(
						"CPConfigurationListId");

					String indexIds = indexIdsMap.get(cpConfigurationListId);

					if (indexIds == null) {
						indexIds = StringUtil.merge(
							ArrayUtil.filter(
								TransformUtil.transformToLongArray(
									_cpConfigurationListLocalService.
										getCPConfigurationLists(
											resultSet.getLong("groupId"),
											resultSet.getLong("companyId")),
									CPConfigurationList::
										getCPConfigurationListId),
								curCPConfigurationListId ->
									curCPConfigurationListId !=
										cpConfigurationListId),
							StringPool.COMMA);

						indexIdsMap.put(cpConfigurationListId, indexIds);
					}

					_addCPConfigurationEntry(
						classNameId, ctCollectionIdsMap, indexIds, resultSet);
				}
			}
		}
	}

	private void _addCPConfigurationEntry(
			long classNameId, Map<Long, Boolean> ctCollectionIdsMap,
			String indexIds, ResultSet resultSet)
		throws Exception {

		long ctCollectionId = resultSet.getLong("ctCollectionId");

		if (ctCollectionId > 0) {
			Boolean readOnly = ctCollectionIdsMap.get(ctCollectionId);

			if (readOnly == null) {
				CTCollection ctCollection =
					_ctCollectionLocalService.fetchCTCollection(ctCollectionId);

				readOnly = (ctCollection != null) && ctCollection.isReadOnly();

				ctCollectionIdsMap.put(ctCollectionId, readOnly);
			}

			if (readOnly) {
				return;
			}
		}

		try (SafeCloseable safeCloseable =
				CTCollectionThreadLocal.setCTCollectionIdWithSafeCloseable(
					ctCollectionId)) {

			long userId = resultSet.getLong("userId");

			if (_userLocalService.fetchUser(userId) == null) {
				userId = _userLocalService.getGuestUserId(
					resultSet.getLong("companyId"));
			}

			CPConfigurationEntry previousCPConfigurationEntry =
				_fetchPreviousCPConfigurationEntry(classNameId, resultSet);

			CPConfigurationEntry cpConfigurationEntry = null;

			if (previousCPConfigurationEntry == null) {
				cpConfigurationEntry =
					_cpConfigurationEntryLocalService.addCPConfigurationEntry(
						null, userId, resultSet.getLong("groupId"), classNameId,
						resultSet.getLong("CPDefinitionId"),
						resultSet.getLong("CPConfigurationListId"),
						resultSet.getLong("CPTaxCategoryId"),
						resultSet.getString("allowedOrderQuantities"),
						resultSet.getBoolean("backOrders"),
						resultSet.getLong("commerceAvailabilityEstimateId"),
						resultSet.getString("CPDefinitionInventoryEngine"),
						resultSet.getDouble("depth"),
						resultSet.getBoolean("displayAvailability"),
						resultSet.getBoolean("displayStockQuantity"),
						resultSet.getBoolean("freeShipping"),
						resultSet.getDouble("height"),
						resultSet.getString("lowStockActivity"),
						resultSet.getBigDecimal("maxOrderQuantity"),
						resultSet.getBigDecimal("minOrderQuantity"),
						resultSet.getBigDecimal("minStockQuantity"),
						resultSet.getBigDecimal("multipleOrderQuantity"),
						_isPurchasable(
							resultSet.getLong("CPConfigurationListId"),
							resultSet.getLong("CPDefinitionId")),
						resultSet.getBoolean("shippable"),
						resultSet.getDouble("shippingExtraPrice"),
						resultSet.getBoolean("shipSeparately"),
						resultSet.getBoolean("taxExempt"),
						resultSet.getDouble("weight"),
						resultSet.getDouble("width"));
			}
			else {
				cpConfigurationEntry =
					_cpConfigurationEntryLocalService.addCPConfigurationEntry(
						null, userId, resultSet.getLong("groupId"), classNameId,
						resultSet.getLong("CPDefinitionId"),
						resultSet.getLong("CPConfigurationListId"),
						previousCPConfigurationEntry.getCPTaxCategoryId(),
						previousCPConfigurationEntry.
							getAllowedOrderQuantities(),
						previousCPConfigurationEntry.isBackOrders(),
						previousCPConfigurationEntry.
							getCommerceAvailabilityEstimateId(),
						previousCPConfigurationEntry.
							getCPDefinitionInventoryEngine(),
						previousCPConfigurationEntry.getDepth(),
						previousCPConfigurationEntry.isDisplayAvailability(),
						previousCPConfigurationEntry.isDisplayStockQuantity(),
						previousCPConfigurationEntry.isFreeShipping(),
						previousCPConfigurationEntry.getHeight(),
						previousCPConfigurationEntry.getLowStockActivity(),
						previousCPConfigurationEntry.getMaxOrderQuantity(),
						previousCPConfigurationEntry.getMinOrderQuantity(),
						previousCPConfigurationEntry.getMinStockQuantity(),
						previousCPConfigurationEntry.getMultipleOrderQuantity(),
						previousCPConfigurationEntry.isPurchasable(),
						previousCPConfigurationEntry.isShippable(),
						previousCPConfigurationEntry.getShippingExtraPrice(),
						previousCPConfigurationEntry.isShipSeparately(),
						previousCPConfigurationEntry.isTaxExempt(),
						previousCPConfigurationEntry.getWeight(),
						previousCPConfigurationEntry.getWidth());
			}

			if (Validator.isNull(indexIds)) {
				return;
			}

			CPConfigurationEntrySetting cpConfigurationEntrySetting =
				_cpConfigurationEntrySettingLocalService.
					fetchCPConfigurationEntrySetting(
						cpConfigurationEntry.getCPConfigurationEntryId(),
						CPConfigurationEntrySettingConstants.TYPE_INDEX_IDS);

			cpConfigurationEntrySetting.setValue(indexIds);

			_cpConfigurationEntrySettingLocalService.
				updateCPConfigurationEntrySetting(cpConfigurationEntrySetting);
		}
		catch (Exception exception) {
			_log.error(exception);
		}
	}

	private CPConfigurationEntry _fetchPreviousCPConfigurationEntry(
			long classNameId, ResultSet resultSet)
		throws Exception {

		try (PreparedStatement preparedStatement = connection.prepareStatement(
				StringBundler.concat(
					"select CPConfigurationEntry.CPConfigurationEntryId from ",
					"CPConfigurationEntry join CPDefinition on ",
					"CPDefinition.CPDefinitionId = ",
					"CPConfigurationEntry.classPK where ",
					"CPConfigurationEntry.classNameId = ? and ",
					"CPConfigurationEntry.CPConfigurationListId = ? and ",
					"CPDefinition.CProductId = ? and CPDefinition.version < ? ",
					"order by CPDefinition.version desc"))) {

			preparedStatement.setLong(1, classNameId);
			preparedStatement.setLong(
				2, resultSet.getLong("CPConfigurationListId"));
			preparedStatement.setLong(3, resultSet.getLong("CProductId"));
			preparedStatement.setInt(4, resultSet.getInt("version"));

			try (ResultSet previousResultSet =
					preparedStatement.executeQuery()) {

				while (previousResultSet.next()) {
					CPConfigurationEntry cpConfigurationEntry =
						_cpConfigurationEntryLocalService.
							fetchCPConfigurationEntry(
								previousResultSet.getLong(
									"CPConfigurationEntryId"));

					if (cpConfigurationEntry != null) {
						return cpConfigurationEntry;
					}
				}
			}
		}

		return null;
	}

	private boolean _isPurchasable(
		long cpConfigurationListId, long cpDefinitionId) {

		List<CPInstance> cpInstances =
			_cpInstanceLocalService.getCPDefinitionInstances(
				cpDefinitionId, WorkflowConstants.STATUS_ANY, QueryUtil.ALL_POS,
				QueryUtil.ALL_POS, null);

		if (!cpInstances.isEmpty()) {
			return ListUtil.exists(cpInstances, CPInstance::isPurchasable);
		}

		CPConfigurationEntry templateCPConfigurationEntry =
			_cpConfigurationEntryLocalService.fetchCPConfigurationEntry(
				_classNameLocalService.getClassNameId(
					CPConfigurationList.class),
				cpConfigurationListId, cpConfigurationListId);

		if (templateCPConfigurationEntry == null) {
			return true;
		}

		return templateCPConfigurationEntry.isPurchasable();
	}

	private static final Log _log = LogFactoryUtil.getLog(
		CPConfigurationEntryUpgradeProcess.class);

	private final ClassNameLocalService _classNameLocalService;
	private final CPConfigurationEntryLocalService
		_cpConfigurationEntryLocalService;
	private final CPConfigurationEntrySettingLocalService
		_cpConfigurationEntrySettingLocalService;
	private final CPConfigurationListLocalService
		_cpConfigurationListLocalService;
	private final CPInstanceLocalService _cpInstanceLocalService;
	private final CTCollectionLocalService _ctCollectionLocalService;
	private final UserLocalService _userLocalService;

}