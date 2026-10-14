/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.pricing.service.impl;

import com.liferay.asset.kernel.model.AssetCategory;
import com.liferay.asset.kernel.model.AssetEntry;
import com.liferay.asset.kernel.service.AssetEntryLocalService;
import com.liferay.commerce.pricing.constants.CommercePriceModifierConstants;
import com.liferay.commerce.pricing.exception.CommercePriceModifierAmountException;
import com.liferay.commerce.pricing.exception.CommercePriceModifierDisplayDateException;
import com.liferay.commerce.pricing.exception.CommercePriceModifierExpirationDateException;
import com.liferay.commerce.pricing.exception.CommercePriceModifierTargetException;
import com.liferay.commerce.pricing.exception.CommercePriceModifierTitleException;
import com.liferay.commerce.pricing.exception.CommercePriceModifierTypeException;
import com.liferay.commerce.pricing.exception.NoSuchPriceModifierException;
import com.liferay.commerce.pricing.model.CommercePriceModifier;
import com.liferay.commerce.pricing.service.CommercePriceModifierRelLocalService;
import com.liferay.commerce.pricing.service.CommercePricingClassLocalService;
import com.liferay.commerce.pricing.service.base.CommercePriceModifierLocalServiceBaseImpl;
import com.liferay.commerce.pricing.type.CommercePriceModifierType;
import com.liferay.commerce.pricing.type.CommercePriceModifierTypeRegistry;
import com.liferay.commerce.product.model.CPDefinition;
import com.liferay.expando.kernel.service.ExpandoRowLocalService;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.portal.aop.AopService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.SystemEventConstants;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.service.WorkflowInstanceLinkLocalService;
import com.liferay.portal.kernel.systemevent.SystemEvent;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.kernel.workflow.WorkflowHandlerRegistryUtil;

import java.io.Serializable;

import java.math.BigDecimal;

import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Riccardo Alberti
 */
@Component(
	property = "model.class.name=com.liferay.commerce.pricing.model.CommercePriceModifier",
	service = AopService.class
)
public class CommercePriceModifierLocalServiceImpl
	extends CommercePriceModifierLocalServiceBaseImpl {

	@Override
	public CommercePriceModifier addCommercePriceModifier(
			String externalReferenceCode, long groupId,
			long commercePriceListId, String title, String target,
			BigDecimal modifierAmount, String modifierType, double priority,
			boolean active, int displayDateMonth, int displayDateDay,
			int displayDateYear, int displayDateHour, int displayDateMinute,
			int expirationDateMonth, int expirationDateDay,
			int expirationDateYear, int expirationDateHour,
			int expirationDateMinute, boolean neverExpire,
			ServiceContext serviceContext)
		throws PortalException {

		// Commerce price modifier

		User user = _userLocalService.getUser(serviceContext.getUserId());

		Date date = new Date();

		Date displayDate = _portal.getDate(
			displayDateMonth, displayDateDay, displayDateYear, displayDateHour,
			displayDateMinute, user.getTimeZone(),
			CommercePriceModifierDisplayDateException.class);

		Date expirationDate = null;

		if (!neverExpire) {
			expirationDate = _portal.getDate(
				expirationDateMonth, expirationDateDay, expirationDateYear,
				expirationDateHour, expirationDateMinute, user.getTimeZone(),
				CommercePriceModifierExpirationDateException.class);
		}

		long commercePriceModifierId = counterLocalService.increment();

		_validate(title, target, modifierType, modifierAmount);

		CommercePriceModifier commercePriceModifier =
			commercePriceModifierPersistence.create(commercePriceModifierId);

		commercePriceModifier.setExternalReferenceCode(externalReferenceCode);
		commercePriceModifier.setGroupId(groupId);
		commercePriceModifier.setCompanyId(user.getCompanyId());
		commercePriceModifier.setUserId(user.getUserId());
		commercePriceModifier.setUserName(user.getFullName());
		commercePriceModifier.setCommercePriceListId(commercePriceListId);
		commercePriceModifier.setTitle(title);
		commercePriceModifier.setTarget(target);
		commercePriceModifier.setModifierAmount(modifierAmount);
		commercePriceModifier.setModifierType(modifierType);
		commercePriceModifier.setPriority(priority);
		commercePriceModifier.setActive(active);
		commercePriceModifier.setDisplayDate(displayDate);
		commercePriceModifier.setExpirationDate(expirationDate);

		if ((expirationDate == null) || expirationDate.after(date)) {
			commercePriceModifier.setStatus(WorkflowConstants.STATUS_DRAFT);
		}
		else {
			commercePriceModifier.setStatus(WorkflowConstants.STATUS_EXPIRED);
		}

		commercePriceModifier.setStatusByUserId(user.getUserId());
		commercePriceModifier.setStatusDate(
			serviceContext.getModifiedDate(date));
		commercePriceModifier.setExpandoBridgeAttributes(serviceContext);

		commercePriceModifier = commercePriceModifierPersistence.update(
			commercePriceModifier);

		// Workflow

		return _startWorkflowInstance(
			user.getUserId(), commercePriceModifier, serviceContext);
	}

	@Override
	public CommercePriceModifier addOrUpdateCommercePriceModifier(
			String externalReferenceCode, long commercePriceModifierId,
			long groupId, long commercePriceListId, String title, String target,
			BigDecimal modifierAmount, String modifierType, double priority,
			boolean active, int displayDateMonth, int displayDateDay,
			int displayDateYear, int displayDateHour, int displayDateMinute,
			int expirationDateMonth, int expirationDateDay,
			int expirationDateYear, int expirationDateHour,
			int expirationDateMinute, boolean neverExpire,
			ServiceContext serviceContext)
		throws PortalException {

		// Update

		if (commercePriceModifierId > 0) {
			try {
				return commercePriceModifierLocalService.
					updateCommercePriceModifier(
						commercePriceModifierId, groupId, commercePriceListId,
						title, target, modifierAmount, modifierType, priority,
						active, displayDateMonth, displayDateDay,
						displayDateYear, displayDateHour, displayDateMinute,
						expirationDateMonth, expirationDateDay,
						expirationDateYear, expirationDateHour,
						expirationDateMinute, neverExpire, serviceContext);
			}
			catch (NoSuchPriceModifierException noSuchPriceModifierException) {
				if (_log.isDebugEnabled()) {
					_log.debug(
						"Unable to find price modifier with ID: " +
							commercePriceModifierId,
						noSuchPriceModifierException);
				}
			}
		}

		if (!Validator.isBlank(externalReferenceCode)) {
			CommercePriceModifier commercePriceModifier =
				commercePriceModifierPersistence.fetchByERC_C(
					externalReferenceCode, serviceContext.getCompanyId());

			if (commercePriceModifier != null) {
				return commercePriceModifierLocalService.
					updateCommercePriceModifier(
						commercePriceModifier.getCommercePriceModifierId(),
						groupId, commercePriceListId, title, target,
						modifierAmount, modifierType, priority, active,
						displayDateMonth, displayDateDay, displayDateYear,
						displayDateHour, displayDateMinute, expirationDateMonth,
						expirationDateDay, expirationDateYear,
						expirationDateHour, expirationDateMinute, neverExpire,
						serviceContext);
			}
		}

		// Add

		return commercePriceModifierLocalService.addCommercePriceModifier(
			externalReferenceCode, groupId, commercePriceListId, title, target,
			modifierAmount, modifierType, priority, active, displayDateMonth,
			displayDateDay, displayDateYear, displayDateHour, displayDateMinute,
			expirationDateMonth, expirationDateDay, expirationDateYear,
			expirationDateHour, expirationDateMinute, neverExpire,
			serviceContext);
	}

	@Override
	public void checkCommercePriceModifiers() throws PortalException {
		_checkCommercePriceModifiersByDisplayDate();
		_checkCommercePriceModifiersByExpirationDate();
	}

	@Override
	@SystemEvent(type = SystemEventConstants.TYPE_DELETE)
	public CommercePriceModifier deleteCommercePriceModifier(
			CommercePriceModifier commercePriceModifier)
		throws PortalException {

		// Commerce price modifier rels

		_commercePriceModifierRelLocalService.deleteCommercePriceModifierRels(
			commercePriceModifier.getCommercePriceModifierId());

		// Commerce price modifier

		commercePriceModifierPersistence.remove(commercePriceModifier);

		// Expando

		_expandoRowLocalService.deleteRows(
			commercePriceModifier.getCommercePriceModifierId());

		// Workflow

		_workflowInstanceLinkLocalService.deleteWorkflowInstanceLinks(
			commercePriceModifier.getCompanyId(), 0L,
			CommercePriceModifier.class.getName(),
			commercePriceModifier.getCommercePriceModifierId());

		return commercePriceModifier;
	}

	@Override
	public CommercePriceModifier deleteCommercePriceModifier(
			long commercePriceModifierId)
		throws PortalException {

		CommercePriceModifier commercePriceModifier =
			commercePriceModifierPersistence.findByPrimaryKey(
				commercePriceModifierId);

		return commercePriceModifierLocalService.deleteCommercePriceModifier(
			commercePriceModifier);
	}

	@Override
	public void deleteCommercePriceModifiers(long companyId)
		throws PortalException {

		List<CommercePriceModifier> commercePriceModifiers =
			commercePriceModifierPersistence.findByCompanyId(companyId);

		for (CommercePriceModifier commercePriceModifier :
				commercePriceModifiers) {

			commercePriceModifierLocalService.deleteCommercePriceModifier(
				commercePriceModifier);
		}
	}

	@Override
	public void deleteCommercePriceModifiersByCommercePriceListId(
			long commercePriceListId)
		throws PortalException {

		List<CommercePriceModifier> commercePriceModifiers =
			commercePriceModifierPersistence.findByCommercePriceListId(
				commercePriceListId);

		for (CommercePriceModifier commercePriceModifier :
				commercePriceModifiers) {

			commercePriceModifierLocalService.deleteCommercePriceModifier(
				commercePriceModifier);
		}
	}

	@Override
	public List<CommercePriceModifier> getCommercePriceModifiers(
		long commercePriceListId) {

		return commercePriceModifierPersistence.findByCommercePriceListId(
			commercePriceListId);
	}

	@Override
	public List<CommercePriceModifier> getCommercePriceModifiers(
		long commercePriceListId, int start, int end,
		OrderByComparator<CommercePriceModifier> orderByComparator) {

		return commercePriceModifierPersistence.findByCommercePriceListId(
			commercePriceListId, start, end, orderByComparator);
	}

	@Override
	public int getCommercePriceModifiersCount(long commercePriceListId) {
		return commercePriceModifierPersistence.countByCommercePriceListId(
			commercePriceListId);
	}

	@Override
	public List<CommercePriceModifier> getQualifiedCommercePriceModifiers(
		long commercePriceListId, long cpDefinitionId) {

		return commercePriceModifierFinder.findByC_C_C_P(
			commercePriceListId, cpDefinitionId,
			_getAssetCategoryIds(cpDefinitionId),
			_commercePricingClassLocalService.
				getCommercePricingClassByCPDefinition(cpDefinitionId));
	}

	@Override
	public CommercePriceModifier updateCommercePriceModifier(
			long commercePriceModifierId, long groupId,
			long commercePriceListId, String title, String target,
			BigDecimal modifierAmount, String modifierType, double priority,
			boolean active, int displayDateMonth, int displayDateDay,
			int displayDateYear, int displayDateHour, int displayDateMinute,
			int expirationDateMonth, int expirationDateDay,
			int expirationDateYear, int expirationDateHour,
			int expirationDateMinute, boolean neverExpire,
			ServiceContext serviceContext)
		throws PortalException {

		User user = _userLocalService.getUser(serviceContext.getUserId());

		CommercePriceModifier commercePriceModifier =
			commercePriceModifierPersistence.findByPrimaryKey(
				commercePriceModifierId);

		_validate(title, target, modifierType, modifierAmount);

		String currentTarget = commercePriceModifier.getTarget();

		if (!currentTarget.equals(target)) {
			_commercePriceModifierRelLocalService.
				deleteCommercePriceModifierRels(
					commercePriceModifier.getCommercePriceModifierId());
		}

		Date date = new Date();

		Date displayDate = _portal.getDate(
			displayDateMonth, displayDateDay, displayDateYear, displayDateHour,
			displayDateMinute, user.getTimeZone(),
			CommercePriceModifierDisplayDateException.class);

		Date expirationDate = null;

		if (!neverExpire) {
			expirationDate = _portal.getDate(
				expirationDateMonth, expirationDateDay, expirationDateYear,
				expirationDateHour, expirationDateMinute, user.getTimeZone(),
				CommercePriceModifierExpirationDateException.class);
		}

		commercePriceModifier.setGroupId(groupId);
		commercePriceModifier.setCommercePriceListId(commercePriceListId);
		commercePriceModifier.setTitle(title);
		commercePriceModifier.setTarget(target);
		commercePriceModifier.setModifierAmount(modifierAmount);
		commercePriceModifier.setModifierType(modifierType);
		commercePriceModifier.setPriority(priority);
		commercePriceModifier.setActive(active);
		commercePriceModifier.setDisplayDate(displayDate);
		commercePriceModifier.setExpirationDate(expirationDate);

		if ((expirationDate == null) || expirationDate.after(date)) {
			commercePriceModifier.setStatus(WorkflowConstants.STATUS_DRAFT);
		}
		else {
			commercePriceModifier.setStatus(WorkflowConstants.STATUS_EXPIRED);
		}

		commercePriceModifier.setStatusByUserId(user.getUserId());
		commercePriceModifier.setStatusDate(
			serviceContext.getModifiedDate(date));
		commercePriceModifier.setExpandoBridgeAttributes(serviceContext);

		commercePriceModifier = commercePriceModifierPersistence.update(
			commercePriceModifier);

		return _startWorkflowInstance(
			user.getUserId(), commercePriceModifier, serviceContext);
	}

	@Override
	public CommercePriceModifier updateStatus(
			long userId, long commercePriceModifierId, int status,
			ServiceContext serviceContext,
			Map<String, Serializable> workflowContext)
		throws PortalException {

		User user = _userLocalService.getUser(userId);
		Date date = new Date();

		CommercePriceModifier commercePriceModifier =
			commercePriceModifierPersistence.findByPrimaryKey(
				commercePriceModifierId);

		if ((status == WorkflowConstants.STATUS_APPROVED) &&
			(commercePriceModifier.getDisplayDate() != null) &&
			date.before(commercePriceModifier.getDisplayDate())) {

			commercePriceModifier.setActive(false);

			status = WorkflowConstants.STATUS_SCHEDULED;
		}

		if (status == WorkflowConstants.STATUS_APPROVED) {
			Date expirationDate = commercePriceModifier.getExpirationDate();

			if ((expirationDate != null) && expirationDate.before(date)) {
				commercePriceModifier.setExpirationDate(null);
			}

			if (commercePriceModifier.getStatus() ==
					WorkflowConstants.STATUS_SCHEDULED) {

				commercePriceModifier.setActive(true);
			}
		}

		if (status == WorkflowConstants.STATUS_EXPIRED) {
			commercePriceModifier.setActive(false);
			commercePriceModifier.setExpirationDate(date);
		}

		commercePriceModifier.setStatus(status);
		commercePriceModifier.setStatusByUserId(user.getUserId());
		commercePriceModifier.setStatusByUserName(user.getFullName());
		commercePriceModifier.setStatusDate(
			serviceContext.getModifiedDate(date));

		return commercePriceModifierPersistence.update(commercePriceModifier);
	}

	private void _checkCommercePriceModifiersByDisplayDate()
		throws PortalException {

		List<CommercePriceModifier> commercePriceModifiers =
			commercePriceModifierPersistence.findByLtD_S(
				new Date(), WorkflowConstants.STATUS_SCHEDULED);

		for (CommercePriceModifier commercePriceModifier :
				commercePriceModifiers) {

			long userId = _portal.getValidUserId(
				commercePriceModifier.getCompanyId(),
				commercePriceModifier.getUserId());

			ServiceContext serviceContext = new ServiceContext();

			serviceContext.setCommand(Constants.UPDATE);
			serviceContext.setScopeGroupId(commercePriceModifier.getGroupId());

			commercePriceModifierLocalService.updateStatus(
				userId, commercePriceModifier.getCommercePriceModifierId(),
				WorkflowConstants.STATUS_APPROVED, serviceContext,
				new HashMap<String, Serializable>());
		}
	}

	private void _checkCommercePriceModifiersByExpirationDate()
		throws PortalException {

		List<CommercePriceModifier> commercePriceModifiers =
			commercePriceModifierPersistence.findByLtE_S(
				new Date(), WorkflowConstants.STATUS_APPROVED);

		if (_log.isDebugEnabled()) {
			_log.debug(
				"Expiring " + commercePriceModifiers.size() +
					" commerce price modifiers");
		}

		if ((commercePriceModifiers != null) &&
			!commercePriceModifiers.isEmpty()) {

			for (CommercePriceModifier commercePriceModifier :
					commercePriceModifiers) {

				long userId = _portal.getValidUserId(
					commercePriceModifier.getCompanyId(),
					commercePriceModifier.getUserId());

				ServiceContext serviceContext = new ServiceContext();

				serviceContext.setCommand(Constants.UPDATE);
				serviceContext.setScopeGroupId(
					commercePriceModifier.getGroupId());

				commercePriceModifierLocalService.updateStatus(
					userId, commercePriceModifier.getCommercePriceModifierId(),
					WorkflowConstants.STATUS_EXPIRED, serviceContext,
					new HashMap<String, Serializable>());
			}
		}
	}

	private long[] _getAssetCategoryIds(long cpDefinitionId) {
		try {
			AssetEntry assetEntry = _assetEntryLocalService.getEntry(
				CPDefinition.class.getName(), cpDefinitionId);

			Set<AssetCategory> assetCategories = new HashSet<>();

			for (AssetCategory assetCategory : assetEntry.getCategories()) {
				assetCategories.add(assetCategory);
				assetCategories.addAll(assetCategory.getAncestors());
			}

			return TransformUtil.transformToLongArray(
				assetCategories, AssetCategory::getCategoryId);
		}
		catch (PortalException portalException) {
			_log.error(portalException);
		}

		return new long[0];
	}

	private CommercePriceModifier _startWorkflowInstance(
			long userId, CommercePriceModifier commercePriceModifier,
			ServiceContext serviceContext)
		throws PortalException {

		Map<String, Serializable> workflowContext = new HashMap<>();

		return WorkflowHandlerRegistryUtil.startWorkflowInstance(
			commercePriceModifier.getCompanyId(), 0L, userId,
			CommercePriceModifier.class.getName(),
			commercePriceModifier.getCommercePriceModifierId(),
			commercePriceModifier, serviceContext, workflowContext);
	}

	private void _validate(
			String title, String target, String modifierType,
			BigDecimal modifierAmount)
		throws PortalException {

		if (Validator.isNull(title)) {
			throw new CommercePriceModifierTitleException();
		}

		if (!CommercePriceModifierConstants.TARGET_CATALOG.equals(target) &&
			!CommercePriceModifierConstants.TARGET_CATEGORIES.equals(target) &&
			!CommercePriceModifierConstants.TARGET_PRODUCT_GROUPS.equals(
				target) &&
			!CommercePriceModifierConstants.TARGET_PRODUCTS.equals(target)) {

			throw new CommercePriceModifierTargetException();
		}

		CommercePriceModifierType commercePriceModifierType =
			_commercePriceModifierTypeRegistry.getCommercePriceModifierType(
				modifierType);

		if (commercePriceModifierType == null) {
			throw new CommercePriceModifierTypeException();
		}

		if (modifierAmount == null) {
			throw new CommercePriceModifierAmountException();
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		CommercePriceModifierLocalServiceImpl.class);

	@Reference
	private AssetEntryLocalService _assetEntryLocalService;

	@Reference
	private CommercePriceModifierRelLocalService
		_commercePriceModifierRelLocalService;

	@Reference
	private CommercePriceModifierTypeRegistry
		_commercePriceModifierTypeRegistry;

	@Reference
	private CommercePricingClassLocalService _commercePricingClassLocalService;

	@Reference
	private ExpandoRowLocalService _expandoRowLocalService;

	@Reference
	private Portal _portal;

	@Reference
	private UserLocalService _userLocalService;

	@Reference
	private WorkflowInstanceLinkLocalService _workflowInstanceLinkLocalService;

}