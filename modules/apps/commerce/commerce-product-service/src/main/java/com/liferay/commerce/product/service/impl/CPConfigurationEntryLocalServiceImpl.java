/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.service.impl;

import com.liferay.commerce.product.constants.CPConfigurationEntrySettingConstants;
import com.liferay.commerce.product.exception.CPConfigurationEntryAllowedOrderQuantitiesException;
import com.liferay.commerce.product.exception.RequiredCPConfigurationEntryException;
import com.liferay.commerce.product.model.CPConfigurationEntry;
import com.liferay.commerce.product.model.CPConfigurationEntrySetting;
import com.liferay.commerce.product.model.CPConfigurationList;
import com.liferay.commerce.product.model.CPDefinition;
import com.liferay.commerce.product.service.CPConfigurationEntrySettingLocalService;
import com.liferay.commerce.product.service.base.CPConfigurationEntryLocalServiceBaseImpl;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.petra.string.StringUtil;
import com.liferay.portal.aop.AopService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONSerializer;
import com.liferay.portal.kernel.model.SystemEventConstants;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.search.Indexable;
import com.liferay.portal.kernel.search.IndexableType;
import com.liferay.portal.kernel.search.Indexer;
import com.liferay.portal.kernel.search.IndexerRegistryUtil;
import com.liferay.portal.kernel.service.ClassNameLocalService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.systemevent.SystemEvent;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.Validator;

import java.math.BigDecimal;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Marco Leo
 */
@Component(
	property = "model.class.name=com.liferay.commerce.product.model.CPConfigurationEntry",
	service = AopService.class
)
public class CPConfigurationEntryLocalServiceImpl
	extends CPConfigurationEntryLocalServiceBaseImpl {

	@Indexable(type = IndexableType.REINDEX)
	@Override
	public CPConfigurationEntry addCPConfigurationEntry(
			String externalReferenceCode, long userId, long groupId,
			long classNameId, long classPK, long cpConfigurationListId,
			long cpTaxCategoryId, String allowedOrderQuantities,
			boolean backOrders, long commerceAvailabilityEstimateId,
			String cpDefinitionInventoryEngine, double depth,
			boolean displayAvailability, boolean displayStockQuantity,
			boolean freeShipping, double height, String lowStockActivity,
			BigDecimal maxOrderQuantity, BigDecimal minOrderQuantity,
			BigDecimal minStockQuantity, BigDecimal multipleOrderQuantity,
			boolean purchasable, boolean shippable, double shippingExtraPrice,
			boolean shipSeparately, boolean taxExempt, double weight,
			double width)
		throws PortalException {

		_validateAllowedOrderQuantities(allowedOrderQuantities);

		User user = _userLocalService.getUser(userId);

		CPConfigurationEntry cpConfigurationEntry =
			cpConfigurationEntryPersistence.create(
				counterLocalService.increment());

		cpConfigurationEntry.setExternalReferenceCode(externalReferenceCode);
		cpConfigurationEntry.setGroupId(groupId);
		cpConfigurationEntry.setCompanyId(user.getCompanyId());
		cpConfigurationEntry.setUserId(user.getUserId());
		cpConfigurationEntry.setUserName(user.getFullName());
		cpConfigurationEntry.setClassNameId(classNameId);
		cpConfigurationEntry.setClassPK(classPK);
		cpConfigurationEntry.setCPConfigurationListId(cpConfigurationListId);
		cpConfigurationEntry.setCPTaxCategoryId(cpTaxCategoryId);
		cpConfigurationEntry.setAllowedOrderQuantities(allowedOrderQuantities);
		cpConfigurationEntry.setBackOrders(backOrders);
		cpConfigurationEntry.setCommerceAvailabilityEstimateId(
			commerceAvailabilityEstimateId);
		cpConfigurationEntry.setCPDefinitionInventoryEngine(
			cpDefinitionInventoryEngine);
		cpConfigurationEntry.setDepth(depth);
		cpConfigurationEntry.setDisplayAvailability(displayAvailability);
		cpConfigurationEntry.setDisplayStockQuantity(displayStockQuantity);
		cpConfigurationEntry.setFreeShipping(freeShipping);
		cpConfigurationEntry.setHeight(height);
		cpConfigurationEntry.setLowStockActivity(lowStockActivity);
		cpConfigurationEntry.setMaxOrderQuantity(maxOrderQuantity);
		cpConfigurationEntry.setMinOrderQuantity(minOrderQuantity);
		cpConfigurationEntry.setMinStockQuantity(minStockQuantity);
		cpConfigurationEntry.setMultipleOrderQuantity(multipleOrderQuantity);
		cpConfigurationEntry.setPurchasable(purchasable);
		cpConfigurationEntry.setShippable(shippable);
		cpConfigurationEntry.setShippingExtraPrice(shippingExtraPrice);
		cpConfigurationEntry.setShipSeparately(shipSeparately);
		cpConfigurationEntry.setTaxExempt(taxExempt);
		cpConfigurationEntry.setWeight(weight);
		cpConfigurationEntry.setWidth(width);

		cpConfigurationEntry = cpConfigurationEntryPersistence.update(
			cpConfigurationEntry);

		if (classNameId == _classNameLocalService.getClassNameId(
				CPDefinition.class)) {

			_reindexCPDefinition(classPK);
		}

		_cpConfigurationEntrySettingLocalService.addCPConfigurationEntrySetting(
			userId, groupId, cpConfigurationEntry.getCPConfigurationEntryId(),
			CPConfigurationEntrySettingConstants.TYPE_INDEX_IDS,
			StringPool.BLANK);

		JSONSerializer jsonSerializer = _jsonFactory.createJSONSerializer();

		jsonSerializer.exclude(
			"CPConfigurationEntryId", "CPConfigurationListId", "companyId",
			"ctCollectionId", "externalReferenceCode", "mvccVersion", "userId",
			"userName", "uuid");

		_cpConfigurationEntrySettingLocalService.addCPConfigurationEntrySetting(
			user.getUserId(), groupId,
			cpConfigurationEntry.getCPConfigurationEntryId(),
			CPConfigurationEntrySettingConstants.TYPE_CHANGE_LOG,
			jsonSerializer.serializeDeep(cpConfigurationEntry));

		if (cpConfigurationEntry.getParentCPConfigurationList() == null) {
			return cpConfigurationEntry;
		}

		CPConfigurationEntrySetting cpConfigurationEntrySetting =
			_fetchCPConfigurationEntrySetting(cpConfigurationEntry);

		cpConfigurationEntrySetting.setValue(
			StringUtil.merge(
				ArrayUtil.filter(
					TransformUtil.transformToLongArray(
						StringUtil.split(
							cpConfigurationEntrySetting.getValue()),
						Long::valueOf),
					curCPConfigurationListId ->
						curCPConfigurationListId != cpConfigurationListId),
				StringPool.COMMA));

		cpConfigurationEntrySetting =
			_cpConfigurationEntrySettingLocalService.
				updateCPConfigurationEntrySetting(cpConfigurationEntrySetting);

		_reindexCPConfigurationEntry(
			cpConfigurationEntrySetting.getCPConfigurationEntryId());

		return cpConfigurationEntry;
	}

	@Override
	public void deleteCPConfigurationEntries(long cpConfigurationListId)
		throws PortalException {

		for (CPConfigurationEntry cpConfigurationEntry :
				cpConfigurationEntryLocalService.getCPConfigurationEntries(
					cpConfigurationListId)) {

			cpConfigurationEntryLocalService.deleteCPConfigurationEntry(
				cpConfigurationEntry, true);
		}
	}

	@Override
	public void deleteCPConfigurationEntries(
			long classNameId, long classPK, boolean force)
		throws PortalException {

		List<CPConfigurationEntry> cpConfigurationEntries =
			cpConfigurationEntryPersistence.findByC_C(classNameId, classPK);

		for (CPConfigurationEntry cpConfigurationEntry :
				cpConfigurationEntries) {

			cpConfigurationEntryLocalService.deleteCPConfigurationEntry(
				cpConfigurationEntry, force);
		}
	}

	@Override
	public CPConfigurationEntry deleteCPConfigurationEntry(
			CPConfigurationEntry cpConfigurationEntry)
		throws PortalException {

		return cpConfigurationEntryLocalService.deleteCPConfigurationEntry(
			cpConfigurationEntry, false);
	}

	@Override
	@SystemEvent(type = SystemEventConstants.TYPE_DELETE)
	public CPConfigurationEntry deleteCPConfigurationEntry(
			CPConfigurationEntry cpConfigurationEntry, boolean force)
		throws PortalException {

		if (cpConfigurationEntry.isMaster() && !force) {
			throw new RequiredCPConfigurationEntryException();
		}

		CPConfigurationEntrySetting cpConfigurationEntrySetting =
			_cpConfigurationEntrySettingLocalService.
				fetchCPConfigurationEntrySetting(
					cpConfigurationEntry.getCPConfigurationEntryId(),
					CPConfigurationEntrySettingConstants.TYPE_CHANGE_LOG);

		if (cpConfigurationEntrySetting != null) {
			_cpConfigurationEntrySettingLocalService.
				deleteCPConfigurationEntrySetting(cpConfigurationEntrySetting);
		}

		cpConfigurationEntrySetting =
			_cpConfigurationEntrySettingLocalService.
				fetchCPConfigurationEntrySetting(
					cpConfigurationEntry.getCPConfigurationEntryId(),
					CPConfigurationEntrySettingConstants.TYPE_INDEX_IDS);

		if (cpConfigurationEntrySetting != null) {
			_cpConfigurationEntrySettingLocalService.
				deleteCPConfigurationEntrySetting(cpConfigurationEntrySetting);
		}

		if (force) {
			return super.deleteCPConfigurationEntry(cpConfigurationEntry);
		}

		CPConfigurationEntrySetting parentCPConfigurationEntrySetting =
			_fetchCPConfigurationEntrySetting(cpConfigurationEntry);

		cpConfigurationEntry = super.deleteCPConfigurationEntry(
			cpConfigurationEntry);

		if (parentCPConfigurationEntrySetting == null) {
			return cpConfigurationEntry;
		}

		String value = String.valueOf(
			cpConfigurationEntry.getCPConfigurationListId());

		if ((cpConfigurationEntrySetting != null) &&
			Validator.isNotNull(cpConfigurationEntrySetting.getValue())) {

			value = StringBundler.concat(
				value, StringPool.COMMA,
				cpConfigurationEntrySetting.getValue());
		}

		String parentCPConfigurationEntrySettingValue =
			parentCPConfigurationEntrySetting.getValue();

		parentCPConfigurationEntrySetting.setValue(
			StringBundler.concat(
				parentCPConfigurationEntrySettingValue, StringPool.COMMA,
				value));

		_cpConfigurationEntrySettingLocalService.
			updateCPConfigurationEntrySetting(
				parentCPConfigurationEntrySetting);

		return cpConfigurationEntry;
	}

	@Override
	public CPConfigurationEntry deleteCPConfigurationEntry(
			long cpConfigurationEntryId)
		throws PortalException {

		return cpConfigurationEntryLocalService.deleteCPConfigurationEntry(
			cpConfigurationEntryId, false);
	}

	@Override
	public CPConfigurationEntry deleteCPConfigurationEntry(
			long cpConfigurationEntryId, boolean force)
		throws PortalException {

		return cpConfigurationEntryLocalService.deleteCPConfigurationEntry(
			cpConfigurationEntryPersistence.findByPrimaryKey(
				cpConfigurationEntryId),
			force);
	}

	@Override
	public CPConfigurationEntry fetchCPConfigurationEntry(
		long classNameId, long classPK, long cpConfigurationListId) {

		return cpConfigurationEntryPersistence.fetchByC_C_C(
			classNameId, classPK, cpConfigurationListId);
	}

	@Override
	public List<CPConfigurationEntry> getCPConfigurationEntries(
		long cpConfigurationListId) {

		return cpConfigurationEntryPersistence.findByCPConfigurationListId(
			cpConfigurationListId);
	}

	@Override
	public List<CPConfigurationEntry> getCPConfigurationEntries(
		long classNameId, long classPK) {

		return cpConfigurationEntryPersistence.findByC_C(classNameId, classPK);
	}

	@Override
	public CPConfigurationEntry getCPConfigurationEntry(
			long classNameId, long classPK, long cpConfigurationListId)
		throws PortalException {

		return cpConfigurationEntryPersistence.findByC_C_C(
			classNameId, classPK, cpConfigurationListId);
	}

	@Indexable(type = IndexableType.REINDEX)
	@Override
	public CPConfigurationEntry updateCPConfigurationEntry(
			String externalReferenceCode, long cpConfigurationEntryId,
			long cpTaxCategoryId, String allowedOrderQuantities,
			boolean backOrders, long commerceAvailabilityEstimateId,
			String cpDefinitionInventoryEngine, double depth,
			boolean displayAvailability, boolean displayStockQuantity,
			boolean freeShipping, double height, String lowStockActivity,
			BigDecimal maxOrderQuantity, BigDecimal minOrderQuantity,
			BigDecimal minStockQuantity, BigDecimal multipleOrderQuantity,
			boolean purchasable, boolean shippable, double shippingExtraPrice,
			boolean shipSeparately, boolean taxExempt, double weight,
			double width)
		throws PortalException {

		_validateAllowedOrderQuantities(allowedOrderQuantities);

		CPConfigurationEntry cpConfigurationEntry =
			cpConfigurationEntryPersistence.findByPrimaryKey(
				cpConfigurationEntryId);

		cpConfigurationEntry.setExternalReferenceCode(externalReferenceCode);
		cpConfigurationEntry.setCPTaxCategoryId(cpTaxCategoryId);
		cpConfigurationEntry.setAllowedOrderQuantities(allowedOrderQuantities);
		cpConfigurationEntry.setBackOrders(backOrders);
		cpConfigurationEntry.setCommerceAvailabilityEstimateId(
			commerceAvailabilityEstimateId);
		cpConfigurationEntry.setCPDefinitionInventoryEngine(
			cpDefinitionInventoryEngine);
		cpConfigurationEntry.setDepth(depth);
		cpConfigurationEntry.setDisplayAvailability(displayAvailability);
		cpConfigurationEntry.setDisplayStockQuantity(displayStockQuantity);
		cpConfigurationEntry.setFreeShipping(freeShipping);
		cpConfigurationEntry.setHeight(height);
		cpConfigurationEntry.setLowStockActivity(lowStockActivity);
		cpConfigurationEntry.setMaxOrderQuantity(maxOrderQuantity);
		cpConfigurationEntry.setMinOrderQuantity(minOrderQuantity);
		cpConfigurationEntry.setMinStockQuantity(minStockQuantity);
		cpConfigurationEntry.setMultipleOrderQuantity(multipleOrderQuantity);
		cpConfigurationEntry.setPurchasable(purchasable);
		cpConfigurationEntry.setShippable(shippable);
		cpConfigurationEntry.setShippingExtraPrice(shippingExtraPrice);
		cpConfigurationEntry.setShipSeparately(shipSeparately);
		cpConfigurationEntry.setTaxExempt(taxExempt);
		cpConfigurationEntry.setWeight(weight);
		cpConfigurationEntry.setWidth(width);

		cpConfigurationEntry = cpConfigurationEntryPersistence.update(
			cpConfigurationEntry);

		if (cpConfigurationEntry.getClassNameId() ==
				_classNameLocalService.getClassNameId(CPDefinition.class)) {

			_reindexCPDefinition(cpConfigurationEntry.getClassPK());
		}

		return cpConfigurationEntry;
	}

	private CPConfigurationEntrySetting _fetchCPConfigurationEntrySetting(
			CPConfigurationEntry cpConfigurationEntry)
		throws PortalException {

		CPConfigurationEntrySetting cpConfigurationEntrySetting =
			_cpConfigurationEntrySettingLocalService.
				fetchCPConfigurationEntrySetting(
					cpConfigurationEntry.getCPConfigurationEntryId(),
					CPConfigurationEntrySettingConstants.TYPE_INDEX_IDS);

		CPConfigurationList parentCPConfigurationList =
			cpConfigurationEntry.getParentCPConfigurationList();

		if ((cpConfigurationEntrySetting != null) &&
			(parentCPConfigurationList == null)) {

			return cpConfigurationEntrySetting;
		}

		long cpConfigurationListId =
			parentCPConfigurationList.getCPConfigurationListId();

		CPConfigurationEntry parentCPConfigurationEntry = null;

		while (parentCPConfigurationEntry == null) {
			parentCPConfigurationEntry =
				cpConfigurationEntryLocalService.fetchCPConfigurationEntry(
					cpConfigurationEntry.getClassNameId(),
					cpConfigurationEntry.getClassPK(), cpConfigurationListId);

			parentCPConfigurationList =
				parentCPConfigurationList.getParentCPConfigurationList();

			if (parentCPConfigurationList == null) {
				break;
			}

			cpConfigurationListId =
				parentCPConfigurationList.getCPConfigurationListId();
		}

		return _cpConfigurationEntrySettingLocalService.
			fetchCPConfigurationEntrySetting(
				parentCPConfigurationEntry.getCPConfigurationEntryId(),
				CPConfigurationEntrySettingConstants.TYPE_INDEX_IDS);
	}

	private void _reindexCPConfigurationEntry(long cpConfigurationEntryId)
		throws PortalException {

		Indexer<CPConfigurationEntry> indexer =
			IndexerRegistryUtil.nullSafeGetIndexer(CPConfigurationEntry.class);

		indexer.reindex(
			CPConfigurationEntry.class.getName(), cpConfigurationEntryId);
	}

	private void _reindexCPDefinition(long cpDefinitionId)
		throws PortalException {

		Indexer<CPDefinition> indexer = IndexerRegistryUtil.nullSafeGetIndexer(
			CPDefinition.class);

		indexer.reindex(CPDefinition.class.getName(), cpDefinitionId);
	}

	private void _validateAllowedOrderQuantities(String allowedOrderQuantities)
		throws PortalException {

		if (Validator.isNull(allowedOrderQuantities)) {
			return;
		}

		Matcher matcher = _pattern.matcher(allowedOrderQuantities);

		if (!matcher.matches()) {
			throw new CPConfigurationEntryAllowedOrderQuantitiesException();
		}
	}

	private static final Pattern _pattern = Pattern.compile(
		"^(\\d{1,3}(,\\d{3})*(\\.\\d{1,2})?)" +
			"(\\s\\d{1,3}(,\\d{3})*(\\.\\d{1,2})?)*$");

	@Reference
	private ClassNameLocalService _classNameLocalService;

	@Reference
	private CPConfigurationEntrySettingLocalService
		_cpConfigurationEntrySettingLocalService;

	@Reference
	private JSONFactory _jsonFactory;

	@Reference
	private UserLocalService _userLocalService;

}