/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.discount.service.impl;

import com.liferay.asset.kernel.model.AssetCategory;
import com.liferay.asset.kernel.model.AssetEntry;
import com.liferay.asset.kernel.service.AssetEntryLocalService;
import com.liferay.commerce.constants.CommercePriceConstants;
import com.liferay.commerce.discount.constants.CommerceDiscountConstants;
import com.liferay.commerce.discount.exception.CommerceDiscountCouponCodeException;
import com.liferay.commerce.discount.exception.CommerceDiscountDisplayDateException;
import com.liferay.commerce.discount.exception.CommerceDiscountExpirationDateException;
import com.liferay.commerce.discount.exception.CommerceDiscountLimitationTypeException;
import com.liferay.commerce.discount.exception.CommerceDiscountMaxPriceValueException;
import com.liferay.commerce.discount.exception.CommerceDiscountMinPriceValueException;
import com.liferay.commerce.discount.exception.CommerceDiscountRuleTypeSettingsException;
import com.liferay.commerce.discount.exception.CommerceDiscountTargetException;
import com.liferay.commerce.discount.exception.CommerceDiscountTitleException;
import com.liferay.commerce.discount.exception.DuplicateCommerceDiscountException;
import com.liferay.commerce.discount.exception.NoSuchDiscountException;
import com.liferay.commerce.discount.internal.search.CommerceDiscountIndexer;
import com.liferay.commerce.discount.model.CommerceDiscount;
import com.liferay.commerce.discount.model.CommerceDiscountAccountRelTable;
import com.liferay.commerce.discount.model.CommerceDiscountCommerceAccountGroupRelTable;
import com.liferay.commerce.discount.model.CommerceDiscountOrderTypeRelTable;
import com.liferay.commerce.discount.model.CommerceDiscountRelTable;
import com.liferay.commerce.discount.model.CommerceDiscountRule;
import com.liferay.commerce.discount.model.CommerceDiscountTable;
import com.liferay.commerce.discount.rule.type.CommerceDiscountRuleType;
import com.liferay.commerce.discount.rule.type.CommerceDiscountRuleTypeRegistry;
import com.liferay.commerce.discount.service.CommerceDiscountCommerceAccountGroupRelLocalService;
import com.liferay.commerce.discount.service.CommerceDiscountOrderTypeRelLocalService;
import com.liferay.commerce.discount.service.CommerceDiscountRelLocalService;
import com.liferay.commerce.discount.service.CommerceDiscountRuleLocalService;
import com.liferay.commerce.discount.service.CommerceDiscountUsageEntryLocalService;
import com.liferay.commerce.discount.service.base.CommerceDiscountLocalServiceBaseImpl;
import com.liferay.commerce.discount.target.CommerceDiscountTarget;
import com.liferay.commerce.discount.target.CommerceDiscountTargetRegistry;
import com.liferay.commerce.discount.util.comparator.CommerceDiscountCreateDateComparator;
import com.liferay.commerce.pricing.model.CommercePricingClass;
import com.liferay.commerce.pricing.service.CommercePricingClassLocalService;
import com.liferay.commerce.product.model.CPDefinition;
import com.liferay.commerce.product.model.CPInstance;
import com.liferay.commerce.product.model.CommerceChannelAccountEntryRelTable;
import com.liferay.commerce.product.model.CommerceChannelRelTable;
import com.liferay.commerce.product.service.CommerceChannelAccountEntryRelLocalService;
import com.liferay.expando.kernel.service.ExpandoRowLocalService;
import com.liferay.exportimport.kernel.empty.model.EmptyModelManager;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.sql.dsl.DSLQueryFactoryUtil;
import com.liferay.petra.sql.dsl.expression.Predicate;
import com.liferay.petra.sql.dsl.query.FromStep;
import com.liferay.petra.sql.dsl.query.GroupByStep;
import com.liferay.petra.sql.dsl.query.JoinStep;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.aop.AopService;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.PortalException;
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
import com.liferay.portal.kernel.service.ClassNameLocalService;
import com.liferay.portal.kernel.service.ResourceLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.service.WorkflowDefinitionLinkLocalService;
import com.liferay.portal.kernel.service.WorkflowInstanceLinkLocalService;
import com.liferay.portal.kernel.systemevent.SystemEvent;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.CalendarFactoryUtil;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.LinkedHashMapBuilder;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.kernel.workflow.WorkflowHandlerRegistryUtil;

import java.io.Serializable;

import java.math.BigDecimal;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Marco Leo
 * @author Alessio Antonio Rendina
 */
@Component(
	property = "model.class.name=com.liferay.commerce.discount.model.CommerceDiscount",
	service = AopService.class
)
public class CommerceDiscountLocalServiceImpl
	extends CommerceDiscountLocalServiceBaseImpl {

	@Indexable(type = IndexableType.REINDEX)
	@Override
	public CommerceDiscount addCommerceDiscount(
			long userId, String title, String target, boolean useCouponCode,
			String couponCode, boolean usePercentage,
			BigDecimal maximumDiscountAmount, BigDecimal level1,
			BigDecimal level2, BigDecimal level3, BigDecimal level4,
			String limitationType, int limitationTimes, boolean active,
			int displayDateMonth, int displayDateDay, int displayDateYear,
			int displayDateHour, int displayDateMinute, int expirationDateMonth,
			int expirationDateDay, int expirationDateYear,
			int expirationDateHour, int expirationDateMinute,
			boolean neverExpire, ServiceContext serviceContext)
		throws PortalException {

		return commerceDiscountLocalService.addCommerceDiscount(
			userId, title, target, useCouponCode, couponCode, usePercentage,
			maximumDiscountAmount, StringPool.BLANK, level1, level2, level3,
			level4, limitationType, limitationTimes, true, active,
			displayDateMonth, displayDateDay, displayDateYear, displayDateHour,
			displayDateMinute, expirationDateMonth, expirationDateDay,
			expirationDateYear, expirationDateHour, expirationDateMinute,
			neverExpire, serviceContext);
	}

	@Indexable(type = IndexableType.REINDEX)
	@Override
	public CommerceDiscount addCommerceDiscount(
			long userId, String title, String target, boolean useCouponCode,
			String couponCode, boolean usePercentage,
			BigDecimal maximumDiscountAmount, String level, BigDecimal level1,
			BigDecimal level2, BigDecimal level3, BigDecimal level4,
			String limitationType, int limitationTimes,
			boolean rulesConjunction, boolean active, int displayDateMonth,
			int displayDateDay, int displayDateYear, int displayDateHour,
			int displayDateMinute, int expirationDateMonth,
			int expirationDateDay, int expirationDateYear,
			int expirationDateHour, int expirationDateMinute,
			boolean neverExpire, ServiceContext serviceContext)
		throws PortalException {

		return addCommerceDiscount(
			null, userId, title, target, useCouponCode, couponCode,
			usePercentage, maximumDiscountAmount, level, level1, level2, level3,
			level4, limitationType, limitationTimes, rulesConjunction, active,
			displayDateMonth, displayDateDay, displayDateYear, displayDateHour,
			displayDateMinute, expirationDateMonth, expirationDateDay,
			expirationDateYear, expirationDateHour, expirationDateMinute,
			neverExpire, serviceContext);
	}

	/**
	 * @deprecated As of Cavanaugh (7.4.x), replaced by {@link
	 *             #addCommerceDiscount(String, long, String, String, boolean,
	 *             String, boolean, BigDecimal, String, BigDecimal, BigDecimal,
	 *             BigDecimal, BigDecimal, String, int, boolean, boolean, int,
	 *             int, int, int, int, int, int, int, int, int, boolean,
	 *             ServiceContext)}
	 */
	@Deprecated
	@Indexable(type = IndexableType.REINDEX)
	@Override
	public CommerceDiscount addCommerceDiscount(
			long userId, String title, String target, boolean useCouponCode,
			String couponCode, boolean usePercentage,
			BigDecimal maximumDiscountAmount, String level, BigDecimal level1,
			BigDecimal level2, BigDecimal level3, BigDecimal level4,
			String limitationType, int limitationTimes,
			boolean rulesConjunction, boolean active, int displayDateMonth,
			int displayDateDay, int displayDateYear, int displayDateHour,
			int displayDateMinute, int expirationDateMonth,
			int expirationDateDay, int expirationDateYear,
			int expirationDateHour, int expirationDateMinute,
			String externalReferenceCode, boolean neverExpire,
			ServiceContext serviceContext)
		throws PortalException {

		return addCommerceDiscount(
			externalReferenceCode, userId, title, target, useCouponCode,
			couponCode, usePercentage, maximumDiscountAmount, level, level1,
			level2, level3, level4, limitationType, limitationTimes,
			rulesConjunction, active, displayDateMonth, displayDateDay,
			displayDateYear, displayDateHour, displayDateMinute,
			expirationDateMonth, expirationDateDay, expirationDateYear,
			expirationDateHour, expirationDateMinute, neverExpire,
			serviceContext);
	}

	/**
	 * @deprecated As of Cavanaugh (7.4.x), replaced by {@link
	 *             #addCommerceDiscount(String, long, String, String, boolean, String,
	 *             boolean, BigDecimal, String, BigDecimal, BigDecimal,
	 *             BigDecimal, BigDecimal, BigDecimal, String, int, int,
	 *             boolean, boolean, int, int, int, int, int, int, int, int,
	 *             int, int, boolean, ServiceContext)}
	 */
	@Deprecated
	@Indexable(type = IndexableType.REINDEX)
	@Override
	public CommerceDiscount addCommerceDiscount(
			long userId, String title, String target, boolean useCouponCode,
			String couponCode, boolean usePercentage,
			BigDecimal maximumDiscountAmount, String level, BigDecimal level1,
			BigDecimal level2, BigDecimal level3, BigDecimal level4,
			String limitationType, int limitationTimes,
			int limitationTimesPerAccount, boolean rulesConjunction,
			boolean active, int displayDateMonth, int displayDateDay,
			int displayDateYear, int displayDateHour, int displayDateMinute,
			int expirationDateMonth, int expirationDateDay,
			int expirationDateYear, int expirationDateHour,
			int expirationDateMinute, String externalReferenceCode,
			boolean neverExpire, ServiceContext serviceContext)
		throws PortalException {

		return addCommerceDiscount(
			externalReferenceCode, userId, title, target, useCouponCode,
			couponCode, usePercentage, maximumDiscountAmount, level, level1,
			level2, level3, level4, limitationType, limitationTimes,
			limitationTimesPerAccount, rulesConjunction, active,
			displayDateMonth, displayDateDay, displayDateYear, displayDateHour,
			displayDateMinute, expirationDateMonth, expirationDateDay,
			expirationDateYear, expirationDateHour, expirationDateMinute,
			neverExpire, serviceContext);
	}

	@Indexable(type = IndexableType.REINDEX)
	@Override
	public CommerceDiscount addCommerceDiscount(
			String externalReferenceCode, long userId, String title,
			String target, boolean useCouponCode, String couponCode,
			boolean usePercentage, BigDecimal maximumDiscountAmount,
			String level, BigDecimal level1, BigDecimal level2,
			BigDecimal level3, BigDecimal level4, String limitationType,
			int limitationTimes, boolean rulesConjunction, boolean active,
			int displayDateMonth, int displayDateDay, int displayDateYear,
			int displayDateHour, int displayDateMinute, int expirationDateMonth,
			int expirationDateDay, int expirationDateYear,
			int expirationDateHour, int expirationDateMinute,
			boolean neverExpire, ServiceContext serviceContext)
		throws PortalException {

		return addCommerceDiscount(
			externalReferenceCode, userId, title, target, useCouponCode,
			couponCode, usePercentage, maximumDiscountAmount, level, level1,
			level2, level3, level4, limitationType, limitationTimes, 0,
			rulesConjunction, active, displayDateMonth, displayDateDay,
			displayDateYear, displayDateHour, displayDateMinute,
			expirationDateMonth, expirationDateDay, expirationDateYear,
			expirationDateHour, expirationDateMinute, neverExpire,
			serviceContext);
	}

	@Indexable(type = IndexableType.REINDEX)
	@Override
	public CommerceDiscount addCommerceDiscount(
			String externalReferenceCode, long userId, String title,
			String target, boolean useCouponCode, String couponCode,
			boolean usePercentage, BigDecimal maximumDiscountAmount,
			String level, BigDecimal level1, BigDecimal level2,
			BigDecimal level3, BigDecimal level4, String limitationType,
			int limitationTimes, int limitationTimesPerAccount,
			boolean rulesConjunction, boolean active, int displayDateMonth,
			int displayDateDay, int displayDateYear, int displayDateHour,
			int displayDateMinute, int expirationDateMonth,
			int expirationDateDay, int expirationDateYear,
			int expirationDateHour, int expirationDateMinute,
			boolean neverExpire, ServiceContext serviceContext)
		throws PortalException {

		// Commerce discount

		User user = _userLocalService.getUser(userId);

		_validate(
			0, serviceContext.getCompanyId(), title, target, useCouponCode,
			couponCode, maximumDiscountAmount, level1, level2, level3, level4,
			limitationType);

		Date date = new Date();

		Date displayDate = _portal.getDate(
			displayDateMonth, displayDateDay, displayDateYear, displayDateHour,
			displayDateMinute, user.getTimeZone(),
			CommerceDiscountDisplayDateException.class);

		Date expirationDate = null;

		if (!neverExpire) {
			expirationDate = _portal.getDate(
				expirationDateMonth, expirationDateDay, expirationDateYear,
				expirationDateHour, expirationDateMinute, user.getTimeZone(),
				CommerceDiscountExpirationDateException.class);
		}

		long commerceDiscountId = counterLocalService.increment();

		CommerceDiscount commerceDiscount = commerceDiscountPersistence.create(
			commerceDiscountId);

		commerceDiscount.setExternalReferenceCode(externalReferenceCode);
		commerceDiscount.setCompanyId(user.getCompanyId());
		commerceDiscount.setUserId(user.getUserId());
		commerceDiscount.setUserName(user.getFullName());
		commerceDiscount.setTitle(title);
		commerceDiscount.setTarget(target);
		commerceDiscount.setUseCouponCode(useCouponCode);
		commerceDiscount.setCouponCode(couponCode);
		commerceDiscount.setUsePercentage(usePercentage);
		commerceDiscount.setMaximumDiscountAmount(maximumDiscountAmount);
		commerceDiscount.setLevel(level);

		if (!level.isEmpty()) {
			if (level.equals(CommerceDiscountConstants.LEVEL_L1)) {
				commerceDiscount.setLevel1(level1);
			}
			else if (level.equals(CommerceDiscountConstants.LEVEL_L2)) {
				commerceDiscount.setLevel2(level1);
			}
			else if (level.equals(CommerceDiscountConstants.LEVEL_L3)) {
				commerceDiscount.setLevel3(level1);
			}
			else if (level.equals(CommerceDiscountConstants.LEVEL_L4)) {
				commerceDiscount.setLevel4(level1);
			}
		}
		else {
			commerceDiscount.setLevel1(level1);
			commerceDiscount.setLevel2(level2);
			commerceDiscount.setLevel3(level3);
			commerceDiscount.setLevel4(level4);
		}

		commerceDiscount.setLimitationType(limitationType);
		commerceDiscount.setLimitationTimes(limitationTimes);
		commerceDiscount.setLimitationTimesPerAccount(
			limitationTimesPerAccount);
		commerceDiscount.setRulesConjunction(rulesConjunction);
		commerceDiscount.setActive(active);
		commerceDiscount.setDisplayDate(displayDate);
		commerceDiscount.setExpirationDate(expirationDate);

		if (_emptyModelManager.isEmptyModel()) {
			commerceDiscount.setStatus(WorkflowConstants.STATUS_EMPTY);
		}
		else if ((expirationDate == null) || expirationDate.after(date)) {
			commerceDiscount.setStatus(WorkflowConstants.STATUS_DRAFT);
		}
		else {
			commerceDiscount.setStatus(WorkflowConstants.STATUS_EXPIRED);
		}

		commerceDiscount.setStatusByUserId(user.getUserId());
		commerceDiscount.setStatusDate(serviceContext.getModifiedDate(date));
		commerceDiscount.setExpandoBridgeAttributes(serviceContext);

		commerceDiscount = commerceDiscountPersistence.update(commerceDiscount);

		// Resources

		_resourceLocalService.addModelResources(
			commerceDiscount, serviceContext);

		if (_emptyModelManager.isEmptyModel()) {
			return commerceDiscount;
		}

		// Workflow

		if (_isWorkflowEnabled(
				serviceContext.getCompanyId(), serviceContext.getScopeGroupId(),
				CommerceDiscount.class.getName())) {

			serviceContext.setWorkflowAction(
				WorkflowConstants.ACTION_SAVE_DRAFT);
		}

		return _startWorkflowInstance(
			user.getUserId(), commerceDiscount, serviceContext);
	}

	@Indexable(type = IndexableType.REINDEX)
	@Override
	public CommerceDiscount addOrUpdateCommerceDiscount(
			String externalReferenceCode, long userId, long commerceDiscountId,
			String title, String target, boolean useCouponCode,
			String couponCode, boolean usePercentage,
			BigDecimal maximumDiscountAmount, BigDecimal level1,
			BigDecimal level2, BigDecimal level3, BigDecimal level4,
			String limitationType, int limitationTimes, boolean active,
			int displayDateMonth, int displayDateDay, int displayDateYear,
			int displayDateHour, int displayDateMinute, int expirationDateMonth,
			int expirationDateDay, int expirationDateYear,
			int expirationDateHour, int expirationDateMinute,
			boolean neverExpire, ServiceContext serviceContext)
		throws PortalException {

		return addOrUpdateCommerceDiscount(
			externalReferenceCode, userId, commerceDiscountId, title, target,
			useCouponCode, couponCode, usePercentage, maximumDiscountAmount,
			StringPool.BLANK, level1, level2, level3, level4, limitationType,
			limitationTimes, 0, true, active, displayDateMonth, displayDateDay,
			displayDateYear, displayDateHour, displayDateMinute,
			expirationDateMonth, expirationDateDay, expirationDateYear,
			expirationDateHour, expirationDateMinute, neverExpire,
			serviceContext);
	}

	@Indexable(type = IndexableType.REINDEX)
	@Override
	public CommerceDiscount addOrUpdateCommerceDiscount(
			String externalReferenceCode, long userId, long commerceDiscountId,
			String title, String target, boolean useCouponCode,
			String couponCode, boolean usePercentage,
			BigDecimal maximumDiscountAmount, String level, BigDecimal level1,
			BigDecimal level2, BigDecimal level3, BigDecimal level4,
			String limitationType, int limitationTimes,
			boolean rulesConjunction, boolean active, int displayDateMonth,
			int displayDateDay, int displayDateYear, int displayDateHour,
			int displayDateMinute, int expirationDateMonth,
			int expirationDateDay, int expirationDateYear,
			int expirationDateHour, int expirationDateMinute,
			boolean neverExpire, ServiceContext serviceContext)
		throws PortalException {

		// Update

		if (commerceDiscountId > 0) {
			try {
				return commerceDiscountLocalService.updateCommerceDiscount(
					commerceDiscountId, title, target, useCouponCode,
					couponCode, usePercentage, maximumDiscountAmount, level,
					level1, level2, level3, level4, limitationType,
					limitationTimes, rulesConjunction, active, displayDateMonth,
					displayDateDay, displayDateYear, displayDateHour,
					displayDateMinute, expirationDateMonth, expirationDateDay,
					expirationDateYear, expirationDateHour,
					expirationDateMinute, neverExpire, serviceContext);
			}
			catch (NoSuchDiscountException noSuchDiscountException) {
				if (_log.isDebugEnabled()) {
					_log.debug(
						"Unable to find discount with ID: " +
							commerceDiscountId,
						noSuchDiscountException);
				}
			}
		}

		if (!Validator.isBlank(externalReferenceCode)) {
			CommerceDiscount commerceDiscount =
				commerceDiscountPersistence.fetchByERC_C(
					externalReferenceCode, serviceContext.getCompanyId());

			if (commerceDiscount != null) {
				return commerceDiscountLocalService.updateCommerceDiscount(
					commerceDiscountId, title, target, useCouponCode,
					couponCode, usePercentage, maximumDiscountAmount, level,
					level1, level2, level3, level4, limitationType,
					limitationTimes, rulesConjunction, active, displayDateMonth,
					displayDateDay, displayDateYear, displayDateHour,
					displayDateMinute, expirationDateMonth, expirationDateDay,
					expirationDateYear, expirationDateHour,
					expirationDateMinute, neverExpire, serviceContext);
			}
		}

		// Add

		return commerceDiscountLocalService.addCommerceDiscount(
			externalReferenceCode, userId, title, target, useCouponCode,
			couponCode, usePercentage, maximumDiscountAmount, level, level1,
			level2, level3, level4, limitationType, limitationTimes,
			rulesConjunction, active, displayDateMonth, displayDateDay,
			displayDateYear, displayDateHour, displayDateMinute,
			expirationDateMonth, expirationDateDay, expirationDateYear,
			expirationDateHour, expirationDateMinute, neverExpire,
			serviceContext);
	}

	@Indexable(type = IndexableType.REINDEX)
	@Override
	public CommerceDiscount addOrUpdateCommerceDiscount(
			String externalReferenceCode, long userId, long commerceDiscountId,
			String title, String target, boolean useCouponCode,
			String couponCode, boolean usePercentage,
			BigDecimal maximumDiscountAmount, String level, BigDecimal level1,
			BigDecimal level2, BigDecimal level3, BigDecimal level4,
			String limitationType, int limitationTimes,
			int limitationTimesPerAccount, boolean rulesConjunction,
			boolean active, int displayDateMonth, int displayDateDay,
			int displayDateYear, int displayDateHour, int displayDateMinute,
			int expirationDateMonth, int expirationDateDay,
			int expirationDateYear, int expirationDateHour,
			int expirationDateMinute, boolean neverExpire,
			ServiceContext serviceContext)
		throws PortalException {

		// Update

		if (commerceDiscountId > 0) {
			try {
				return commerceDiscountLocalService.updateCommerceDiscount(
					commerceDiscountId, title, target, useCouponCode,
					couponCode, usePercentage, maximumDiscountAmount, level,
					level1, level2, level3, level4, limitationType,
					limitationTimes, limitationTimesPerAccount,
					rulesConjunction, active, displayDateMonth, displayDateDay,
					displayDateYear, displayDateHour, displayDateMinute,
					expirationDateMonth, expirationDateDay, expirationDateYear,
					expirationDateHour, expirationDateMinute, neverExpire,
					serviceContext);
			}
			catch (NoSuchDiscountException noSuchDiscountException) {
				if (_log.isDebugEnabled()) {
					_log.debug(
						"Unable to find discount with ID: " +
							commerceDiscountId,
						noSuchDiscountException);
				}
			}
		}

		if (!Validator.isBlank(externalReferenceCode)) {
			CommerceDiscount commerceDiscount =
				commerceDiscountPersistence.fetchByERC_C(
					externalReferenceCode, serviceContext.getCompanyId());

			if (commerceDiscount != null) {
				return commerceDiscountLocalService.updateCommerceDiscount(
					commerceDiscountId, title, target, useCouponCode,
					couponCode, usePercentage, maximumDiscountAmount, level,
					level1, level2, level3, level4, limitationType,
					limitationTimes, limitationTimesPerAccount,
					rulesConjunction, active, displayDateMonth, displayDateDay,
					displayDateYear, displayDateHour, displayDateMinute,
					expirationDateMonth, expirationDateDay, expirationDateYear,
					expirationDateHour, expirationDateMinute, neverExpire,
					serviceContext);
			}
		}

		// Add

		return commerceDiscountLocalService.addCommerceDiscount(
			externalReferenceCode, userId, title, target, useCouponCode,
			couponCode, usePercentage, maximumDiscountAmount, level, level1,
			level2, level3, level4, limitationType, limitationTimes,
			limitationTimesPerAccount, rulesConjunction, active,
			displayDateMonth, displayDateDay, displayDateYear, displayDateHour,
			displayDateMinute, expirationDateMonth, expirationDateDay,
			expirationDateYear, expirationDateHour, expirationDateMinute,
			neverExpire, serviceContext);
	}

	@Override
	public void checkCommerceDiscounts() throws PortalException {
		_checkCommerceDiscountsByDisplayDate();
		_checkCommerceDiscountsByExpirationDate();
	}

	@Indexable(type = IndexableType.DELETE)
	@Override
	@SystemEvent(type = SystemEventConstants.TYPE_DELETE)
	public CommerceDiscount deleteCommerceDiscount(
			CommerceDiscount commerceDiscount)
		throws PortalException {

		commerceDiscountPersistence.remove(commerceDiscount);

		_resourceLocalService.deleteResource(
			commerceDiscount.getCompanyId(), CommerceDiscount.class.getName(),
			ResourceConstants.SCOPE_INDIVIDUAL,
			commerceDiscount.getCommerceDiscountId());

		_commerceChannelAccountEntryRelLocalService.
			deleteCommerceChannelAccountEntryRels(
				CommerceDiscount.class.getName(),
				commerceDiscount.getCommerceDiscountId());

		_commerceDiscountCommerceAccountGroupRelLocalService.
			deleteCommerceDiscountCommerceAccountGroupRelsByCommerceDiscountId(
				commerceDiscount.getCommerceDiscountId());

		_commerceDiscountOrderTypeRelLocalService.
			deleteCommerceDiscountOrderTypeRels(
				commerceDiscount.getCommerceDiscountId());

		_commerceDiscountRelLocalService.deleteCommerceDiscountRels(
			commerceDiscount);

		_commerceDiscountRuleLocalService.deleteCommerceDiscountRules(
			commerceDiscount.getCommerceDiscountId());

		_commerceDiscountUsageEntryLocalService.
			deleteCommerceUsageEntryByDiscountId(
				commerceDiscount.getCommerceDiscountId());

		_expandoRowLocalService.deleteRows(
			commerceDiscount.getCommerceDiscountId());

		_workflowInstanceLinkLocalService.deleteWorkflowInstanceLinks(
			commerceDiscount.getCompanyId(), 0L,
			CommerceDiscount.class.getName(),
			commerceDiscount.getCommerceDiscountId());

		return commerceDiscount;
	}

	@Override
	public CommerceDiscount deleteCommerceDiscount(long commerceDiscountId)
		throws PortalException {

		CommerceDiscount commerceDiscount =
			commerceDiscountPersistence.findByPrimaryKey(commerceDiscountId);

		return commerceDiscountLocalService.deleteCommerceDiscount(
			commerceDiscount);
	}

	@Override
	public void deleteCommerceDiscounts(long companyId) throws PortalException {
		List<CommerceDiscount> commerceDiscounts =
			commerceDiscountPersistence.findByCompanyId(companyId);

		for (CommerceDiscount commerceDiscount : commerceDiscounts) {
			commerceDiscountLocalService.deleteCommerceDiscount(
				commerceDiscount);
		}
	}

	@Override
	public CommerceDiscount fetchDefaultCommerceDiscount(
		long commerceChannelAccountEntryRelId, long cpDefinitionId,
		long cpInstanceId, String unitOfMeasureKey) {

		List<CommerceDiscount> commerceDiscounts = dslQuery(
			_getGroupByStep(
				DSLQueryFactoryUtil.selectDistinct(
					CommerceDiscountTable.INSTANCE),
				commerceChannelAccountEntryRelId, cpDefinitionId, cpInstanceId,
				unitOfMeasureKey
			).limit(
				0, 1
			));

		if (commerceDiscounts.isEmpty()) {
			return null;
		}

		return commerceDiscounts.get(0);
	}

	@Override
	public List<CommerceDiscount>
		getAccountAndChannelAndOrderTypeCommerceDiscounts(
			long commerceAccountId, long commerceChannelId,
			long commerceOrderTypeId, long cpDefinitionId, long cpInstanceId,
			String unitOfMeasureKey) {

		return dslQuery(
			_getGroupByStep(
				DSLQueryFactoryUtil.selectDistinct(
					CommerceDiscountTable.INSTANCE),
				null, commerceAccountId, null, commerceChannelId,
				commerceOrderTypeId, cpDefinitionId, cpInstanceId, null,
				unitOfMeasureKey));
	}

	@Override
	public List<CommerceDiscount>
		getAccountAndChannelAndOrderTypeCommerceDiscounts(
			long commerceAccountId, long commerceChannelId,
			long commerceOrderTypeId, String target) {

		return dslQuery(
			_getGroupByStep(
				DSLQueryFactoryUtil.selectDistinct(
					CommerceDiscountTable.INSTANCE),
				null, commerceAccountId, null, commerceChannelId,
				commerceOrderTypeId, null, null, target, null));
	}

	@Override
	public List<CommerceDiscount> getAccountAndChannelCommerceDiscounts(
		long commerceAccountId, long commerceChannelId, long cpDefinitionId,
		long cpInstanceId, String unitOfMeasureKey) {

		return dslQuery(
			_getGroupByStep(
				DSLQueryFactoryUtil.selectDistinct(
					CommerceDiscountTable.INSTANCE),
				null, commerceAccountId, null, commerceChannelId, null,
				cpDefinitionId, cpInstanceId, null, unitOfMeasureKey));
	}

	@Override
	public List<CommerceDiscount> getAccountAndChannelCommerceDiscounts(
		long commerceAccountId, long commerceChannelId, String target) {

		return dslQuery(
			_getGroupByStep(
				DSLQueryFactoryUtil.selectDistinct(
					CommerceDiscountTable.INSTANCE),
				null, commerceAccountId, null, commerceChannelId, null, null,
				null, target, null));
	}

	@Override
	public List<CommerceDiscount> getAccountAndOrderTypeCommerceDiscounts(
		long commerceAccountId, long commerceOrderTypeId, String target) {

		return dslQuery(
			_getGroupByStep(
				DSLQueryFactoryUtil.selectDistinct(
					CommerceDiscountTable.INSTANCE),
				null, commerceAccountId, null, null, commerceOrderTypeId, null,
				null, target, null));
	}

	@Override
	public List<CommerceDiscount> getAccountCommerceAndOrderTypeDiscounts(
		long commerceAccountId, long commerceOrderTypeId, long cpDefinitionId,
		long cpInstanceId, String unitOfMeasureKey) {

		return dslQuery(
			_getGroupByStep(
				DSLQueryFactoryUtil.selectDistinct(
					CommerceDiscountTable.INSTANCE),
				null, commerceAccountId, null, null, commerceOrderTypeId,
				cpDefinitionId, cpInstanceId, null, unitOfMeasureKey));
	}

	@Override
	public List<CommerceDiscount> getAccountCommerceDiscounts(
		long commerceAccountId, long cpDefinitionId, long cpInstanceId,
		String unitOfMeasureKey) {

		return dslQuery(
			_getGroupByStep(
				DSLQueryFactoryUtil.selectDistinct(
					CommerceDiscountTable.INSTANCE),
				null, commerceAccountId, null, null, null, cpDefinitionId,
				cpInstanceId, null, unitOfMeasureKey));
	}

	@Override
	public List<CommerceDiscount> getAccountCommerceDiscounts(
		long commerceAccountId, String target) {

		return dslQuery(
			_getGroupByStep(
				DSLQueryFactoryUtil.selectDistinct(
					CommerceDiscountTable.INSTANCE),
				null, commerceAccountId, null, null, null, null, null, target,
				null));
	}

	@Override
	public List<CommerceDiscount>
		getAccountGroupAndChannelAndOrderTypeCommerceDiscount(
			long[] commerceAccountGroupIds, long commerceChannelId,
			long commerceOrderTypeId, long cpDefinitionId, long cpInstanceId,
			String unitOfMeasureKey) {

		return dslQuery(
			_getGroupByStep(
				DSLQueryFactoryUtil.selectDistinct(
					CommerceDiscountTable.INSTANCE),
				null, null, commerceAccountGroupIds, commerceChannelId,
				commerceOrderTypeId, cpDefinitionId, cpInstanceId, null,
				unitOfMeasureKey));
	}

	@Override
	public List<CommerceDiscount>
		getAccountGroupAndChannelAndOrderTypeCommerceDiscount(
			long[] commerceAccountGroupIds, long commerceChannelId,
			long commerceOrderTypeId, String target) {

		return dslQuery(
			_getGroupByStep(
				DSLQueryFactoryUtil.selectDistinct(
					CommerceDiscountTable.INSTANCE),
				null, null, commerceAccountGroupIds, commerceChannelId,
				commerceOrderTypeId, null, null, target, null));
	}

	@Override
	public List<CommerceDiscount> getAccountGroupAndChannelCommerceDiscount(
		long[] commerceAccountGroupIds, long commerceChannelId,
		long cpDefinitionId, long cpInstanceId, String unitOfMeasureKey) {

		return dslQuery(
			_getGroupByStep(
				DSLQueryFactoryUtil.selectDistinct(
					CommerceDiscountTable.INSTANCE),
				null, null, commerceAccountGroupIds, commerceChannelId, null,
				cpDefinitionId, cpInstanceId, null, unitOfMeasureKey));
	}

	@Override
	public List<CommerceDiscount> getAccountGroupAndChannelCommerceDiscount(
		long[] commerceAccountGroupIds, long commerceChannelId, String target) {

		return dslQuery(
			_getGroupByStep(
				DSLQueryFactoryUtil.selectDistinct(
					CommerceDiscountTable.INSTANCE),
				null, null, commerceAccountGroupIds, commerceChannelId, null,
				null, null, target, null));
	}

	@Override
	public List<CommerceDiscount> getAccountGroupAndOrderTypeCommerceDiscount(
		long[] commerceAccountGroupIds, long commerceOrderTypeId,
		long cpDefinitionId, long cpInstanceId, String unitOfMeasureKey) {

		return dslQuery(
			_getGroupByStep(
				DSLQueryFactoryUtil.selectDistinct(
					CommerceDiscountTable.INSTANCE),
				null, null, commerceAccountGroupIds, null, commerceOrderTypeId,
				cpDefinitionId, cpInstanceId, null, unitOfMeasureKey));
	}

	@Override
	public List<CommerceDiscount> getAccountGroupAndOrderTypeCommerceDiscount(
		long[] commerceAccountGroupIds, long commerceOrderTypeId,
		String target) {

		return dslQuery(
			_getGroupByStep(
				DSLQueryFactoryUtil.selectDistinct(
					CommerceDiscountTable.INSTANCE),
				null, null, commerceAccountGroupIds, null, commerceOrderTypeId,
				null, null, target, null));
	}

	@Override
	public List<CommerceDiscount> getAccountGroupCommerceDiscount(
		long[] commerceAccountGroupIds, long cpDefinitionId, long cpInstanceId,
		String unitOfMeasureKey) {

		return dslQuery(
			_getGroupByStep(
				DSLQueryFactoryUtil.selectDistinct(
					CommerceDiscountTable.INSTANCE),
				null, null, commerceAccountGroupIds, null, null, cpDefinitionId,
				cpInstanceId, null, unitOfMeasureKey));
	}

	@Override
	public List<CommerceDiscount> getAccountGroupCommerceDiscount(
		long[] commerceAccountGroupIds, String target) {

		return dslQuery(
			_getGroupByStep(
				DSLQueryFactoryUtil.selectDistinct(
					CommerceDiscountTable.INSTANCE),
				null, null, commerceAccountGroupIds, null, null, null, null,
				target, null));
	}

	@Override
	public CommerceDiscount getActiveCommerceDiscount(
			long companyId, String couponCode, boolean active)
		throws PortalException {

		return commerceDiscountPersistence.findByC_C_A(
			companyId, couponCode, active);
	}

	@Override
	public int getActiveCommerceDiscountsCount(
		long companyId, String couponCode, boolean active) {

		return commerceDiscountPersistence.countByC_C_A(
			companyId, couponCode, active);
	}

	@Override
	public List<CommerceDiscount> getChannelAndOrderTypeCommerceDiscounts(
		long commerceChannelId, long commerceOrderTypeId, long cpDefinitionId,
		long cpInstanceId, String unitOfMeasureKey) {

		return dslQuery(
			_getGroupByStep(
				DSLQueryFactoryUtil.selectDistinct(
					CommerceDiscountTable.INSTANCE),
				null, null, null, commerceChannelId, commerceOrderTypeId,
				cpDefinitionId, cpInstanceId, null, unitOfMeasureKey));
	}

	@Override
	public List<CommerceDiscount> getChannelAndOrderTypeCommerceDiscounts(
		long commerceChannelId, long commerceOrderTypeId, String target) {

		return dslQuery(
			_getGroupByStep(
				DSLQueryFactoryUtil.selectDistinct(
					CommerceDiscountTable.INSTANCE),
				null, null, null, commerceChannelId, commerceOrderTypeId, null,
				null, target, null));
	}

	@Override
	public List<CommerceDiscount> getChannelCommerceDiscounts(
		long commerceChannelId, long cpDefinitionId, long cpInstanceId,
		String unitOfMeasureKey) {

		return dslQuery(
			_getGroupByStep(
				DSLQueryFactoryUtil.selectDistinct(
					CommerceDiscountTable.INSTANCE),
				null, null, null, commerceChannelId, null, cpDefinitionId,
				cpInstanceId, null, unitOfMeasureKey));
	}

	@Override
	public List<CommerceDiscount> getChannelCommerceDiscounts(
		long commerceChannelId, String target) {

		return dslQuery(
			_getGroupByStep(
				DSLQueryFactoryUtil.selectDistinct(
					CommerceDiscountTable.INSTANCE),
				null, null, null, commerceChannelId, null, null, null, target,
				null));
	}

	/**
	 * @deprecated As of Athanasius (7.3.x)
	 */
	@Deprecated
	@Override
	public List<CommerceDiscount> getCommerceDiscounts(
		long companyId, String couponCode) {

		return commerceDiscountPersistence.findByC_C(companyId, couponCode);
	}

	@Override
	public List<CommerceDiscount> getCommerceDiscounts(
		long companyId, String level, boolean active, int status) {

		return commerceDiscountPersistence.findByC_L_A_S(
			companyId, level, active, status);
	}

	/**
	 * @deprecated As of Athanasius (7.3.x)
	 */
	@Deprecated
	@Override
	public int getCommerceDiscountsCount(long companyId, String couponCode) {
		return commerceDiscountPersistence.countByC_C(companyId, couponCode);
	}

	@Override
	public int getCommerceDiscountsCountByPricingClassId(
		long commercePricingClassId, String title) {

		return commerceDiscountFinder.countByCommercePricingClassId(
			commercePricingClassId, title);
	}

	@Override
	public CommerceDiscount getOrAddEmptyCommerceDiscount(
			String externalReferenceCode, long companyId, long userId)
		throws PortalException {

		Calendar calendar = CalendarFactoryUtil.getCalendar();

		ServiceContext serviceContext = new ServiceContext();

		serviceContext.setCompanyId(companyId);
		serviceContext.setUserId(userId);

		return _emptyModelManager.getOrAddEmptyModel(
			CommerceDiscount.class, companyId,
			() -> commerceDiscountLocalService.addCommerceDiscount(
				externalReferenceCode, userId, externalReferenceCode,
				CommerceDiscountConstants.TARGET_TOTAL, false, null, false,
				null, StringPool.BLANK, null, null, null, null,
				CommerceDiscountConstants.LIMITATION_TYPE_UNLIMITED, 0, 0,
				false, false, calendar.get(Calendar.MONTH),
				calendar.get(Calendar.DATE), calendar.get(Calendar.YEAR),
				calendar.get(Calendar.HOUR_OF_DAY),
				calendar.get(Calendar.MINUTE), 0, 0, 0, 0, 0, true,
				serviceContext),
			externalReferenceCode,
			this::fetchCommerceDiscountByExternalReferenceCode,
			this::getCommerceDiscountByExternalReferenceCode,
			CommerceDiscount.class.getName());
	}

	@Override
	public List<CommerceDiscount> getOrderTypeCommerceDiscounts(
		long commerceOrderTypeId, long cpDefinitionId, long cpInstanceId,
		String unitOfMeasureKey) {

		return dslQuery(
			_getGroupByStep(
				DSLQueryFactoryUtil.selectDistinct(
					CommerceDiscountTable.INSTANCE),
				null, null, null, null, commerceOrderTypeId, cpDefinitionId,
				cpInstanceId, null, unitOfMeasureKey));
	}

	@Override
	public List<CommerceDiscount> getOrderTypeCommerceDiscounts(
		long commerceOrderTypeId, String target) {

		return dslQuery(
			_getGroupByStep(
				DSLQueryFactoryUtil.selectDistinct(
					CommerceDiscountTable.INSTANCE),
				null, null, null, null, commerceOrderTypeId, null, null, target,
				null));
	}

	@Override
	public List<CommerceDiscount> getPriceListCommerceDiscounts(
		long[] commerceDiscountIds, long cpDefinitionId) {

		return commerceDiscountFinder.findPriceListDiscountProduct(
			commerceDiscountIds, cpDefinitionId,
			_getAssetCategoryIds(cpDefinitionId),
			_commercePricingClassLocalService.
				getCommercePricingClassByCPDefinition(cpDefinitionId));
	}

	@Override
	public List<CommerceDiscount> getUnqualifiedCommerceDiscounts(
		long companyId, long cpDefinitionId, long cpInstanceId,
		String unitOfMeasureKey) {

		return dslQuery(
			_getGroupByStep(
				DSLQueryFactoryUtil.selectDistinct(
					CommerceDiscountTable.INSTANCE),
				companyId, null, null, null, null, cpDefinitionId, cpInstanceId,
				null, unitOfMeasureKey));
	}

	@Override
	public List<CommerceDiscount> getUnqualifiedCommerceDiscounts(
		long companyId, String target) {

		return dslQuery(
			_getGroupByStep(
				DSLQueryFactoryUtil.selectDistinct(
					CommerceDiscountTable.INSTANCE),
				companyId, null, null, null, null, null, null, target, null));
	}

	@Override
	public int getValidCommerceDiscountsCount(
		long commerceDiscountId, long cpDefinitionId, long cpInstanceId,
		String unitOfMeasureKey) {

		return dslQueryCount(
			DSLQueryFactoryUtil.countDistinct(
				CommerceDiscountTable.INSTANCE.commerceDiscountId
			).from(
				CommerceDiscountTable.INSTANCE
			).innerJoinON(
				CommerceDiscountRelTable.INSTANCE,
				CommerceDiscountRelTable.INSTANCE.commerceDiscountId.eq(
					CommerceDiscountTable.INSTANCE.commerceDiscountId)
			).where(
				CommerceDiscountTable.INSTANCE.commerceDiscountId.eq(
					commerceDiscountId
				).and(
					_toTargetPredicate(
						cpDefinitionId, cpInstanceId, unitOfMeasureKey)
				)
			));
	}

	@Override
	public int getValidCommerceDiscountsCount(
		long commerceAccountId, long[] commerceAccountGroupIds,
		long commerceChannelId, long commerceDiscountId) {

		return commerceDiscountFinder.countByValidCommerceDiscount(
			commerceAccountId, commerceAccountGroupIds, commerceChannelId,
			commerceDiscountId);
	}

	@Indexable(type = IndexableType.REINDEX)
	@Override
	public CommerceDiscount incrementCommerceDiscountNumberOfUse(
			long commerceDiscountId)
		throws PortalException {

		CommerceDiscount commerceDiscount =
			commerceDiscountPersistence.findByPrimaryKey(commerceDiscountId);

		commerceDiscount.setNumberOfUse(commerceDiscount.getNumberOfUse() + 1);

		return commerceDiscountPersistence.update(commerceDiscount);
	}

	@Override
	public List<CommerceDiscount> searchByCommercePricingClassId(
		long commercePricingClassId, String title, int start, int end) {

		return commerceDiscountFinder.findByCommercePricingClassId(
			commercePricingClassId, title, start, end);
	}

	@Override
	public BaseModelSearchResult<CommerceDiscount> searchCommerceDiscounts(
			long companyId, long[] groupIds, String keywords, int status,
			int start, int end, Sort sort)
		throws PortalException {

		SearchContext searchContext = _buildSearchContext(
			companyId, groupIds, keywords, status, start, end, sort);

		return searchCommerceDiscounts(searchContext);
	}

	@Override
	public BaseModelSearchResult<CommerceDiscount> searchCommerceDiscounts(
			SearchContext searchContext)
		throws PortalException {

		Indexer<CommerceDiscount> indexer =
			IndexerRegistryUtil.nullSafeGetIndexer(CommerceDiscount.class);

		for (int i = 0; i < 10; i++) {
			Hits hits = indexer.search(searchContext, _SELECTED_FIELD_NAMES);

			List<CommerceDiscount> commerceDiscounts = _getCommerceDiscounts(
				hits);

			if (commerceDiscounts != null) {
				return new BaseModelSearchResult<>(
					commerceDiscounts, hits.getLength());
			}
		}

		throw new SearchException(
			"Unable to fix the search index after 10 attempts");
	}

	@Indexable(type = IndexableType.REINDEX)
	@Override
	public CommerceDiscount updateCommerceDiscount(
			long commerceDiscountId, String title, String target,
			boolean useCouponCode, String couponCode, boolean usePercentage,
			BigDecimal maximumDiscountAmount, BigDecimal level1,
			BigDecimal level2, BigDecimal level3, BigDecimal level4,
			String limitationType, int limitationTimes, boolean active,
			int displayDateMonth, int displayDateDay, int displayDateYear,
			int displayDateHour, int displayDateMinute, int expirationDateMonth,
			int expirationDateDay, int expirationDateYear,
			int expirationDateHour, int expirationDateMinute,
			boolean neverExpire, ServiceContext serviceContext)
		throws PortalException {

		return updateCommerceDiscount(
			commerceDiscountId, title, target, useCouponCode, couponCode,
			usePercentage, maximumDiscountAmount, StringPool.BLANK, level1,
			level2, level3, level4, limitationType, limitationTimes, 0, true,
			active, displayDateMonth, displayDateDay, displayDateYear,
			displayDateHour, displayDateMinute, expirationDateMonth,
			expirationDateDay, expirationDateYear, expirationDateHour,
			expirationDateMinute, neverExpire, serviceContext);
	}

	@Indexable(type = IndexableType.REINDEX)
	@Override
	public CommerceDiscount updateCommerceDiscount(
			long commerceDiscountId, String title, String target,
			boolean useCouponCode, String couponCode, boolean usePercentage,
			BigDecimal maximumDiscountAmount, String level, BigDecimal level1,
			BigDecimal level2, BigDecimal level3, BigDecimal level4,
			String limitationType, int limitationTimes,
			boolean rulesConjunction, boolean active, int displayDateMonth,
			int displayDateDay, int displayDateYear, int displayDateHour,
			int displayDateMinute, int expirationDateMonth,
			int expirationDateDay, int expirationDateYear,
			int expirationDateHour, int expirationDateMinute,
			boolean neverExpire, ServiceContext serviceContext)
		throws PortalException {

		User user = _userLocalService.getUser(serviceContext.getUserId());

		CommerceDiscount commerceDiscount =
			commerceDiscountPersistence.findByPrimaryKey(commerceDiscountId);

		_validate(
			commerceDiscountId, serviceContext.getCompanyId(), title, target,
			useCouponCode, couponCode, maximumDiscountAmount, level1, level2,
			level3, level4, limitationType);

		Date date = new Date();

		Date displayDate = _portal.getDate(
			displayDateMonth, displayDateDay, displayDateYear, displayDateHour,
			displayDateMinute, user.getTimeZone(),
			CommerceDiscountDisplayDateException.class);

		Date expirationDate = null;

		if (!neverExpire) {
			expirationDate = _portal.getDate(
				expirationDateMonth, expirationDateDay, expirationDateYear,
				expirationDateHour, expirationDateMinute, user.getTimeZone(),
				CommerceDiscountExpirationDateException.class);
		}

		commerceDiscount.setTitle(title);
		commerceDiscount.setTarget(target);
		commerceDiscount.setUseCouponCode(useCouponCode);
		commerceDiscount.setCouponCode(couponCode);
		commerceDiscount.setUsePercentage(usePercentage);
		commerceDiscount.setMaximumDiscountAmount(maximumDiscountAmount);
		commerceDiscount.setLevel(level);
		commerceDiscount.setLevel1(level1);
		commerceDiscount.setLevel2(level2);
		commerceDiscount.setLevel3(level3);
		commerceDiscount.setLevel4(level4);
		commerceDiscount.setLimitationType(limitationType);
		commerceDiscount.setLimitationTimes(limitationTimes);
		commerceDiscount.setRulesConjunction(rulesConjunction);
		commerceDiscount.setActive(active);
		commerceDiscount.setDisplayDate(displayDate);
		commerceDiscount.setExpirationDate(expirationDate);

		if (commerceDiscount.getStatus() == WorkflowConstants.STATUS_EMPTY) {
			commerceDiscount.setStatus(
				_emptyModelManager.solveEmptyModel(
					commerceDiscount.getExternalReferenceCode(),
					commerceDiscount.getModelClassName(),
					commerceDiscount.getCompanyId(), 0,
					commerceDiscount.getStatus(),
					() -> WorkflowConstants.STATUS_DRAFT));
		}
		else if ((expirationDate == null) || expirationDate.after(date)) {
			commerceDiscount.setStatus(WorkflowConstants.STATUS_DRAFT);
		}
		else {
			commerceDiscount.setStatus(WorkflowConstants.STATUS_EXPIRED);
		}

		commerceDiscount.setStatusByUserId(user.getUserId());
		commerceDiscount.setStatusDate(serviceContext.getModifiedDate(date));
		commerceDiscount.setExpandoBridgeAttributes(serviceContext);

		commerceDiscount = commerceDiscountPersistence.update(commerceDiscount);

		return _startWorkflowInstance(
			user.getUserId(), commerceDiscount, serviceContext);
	}

	@Indexable(type = IndexableType.REINDEX)
	@Override
	public CommerceDiscount updateCommerceDiscount(
			long commerceDiscountId, String title, String target,
			boolean useCouponCode, String couponCode, boolean usePercentage,
			BigDecimal maximumDiscountAmount, String level, BigDecimal level1,
			BigDecimal level2, BigDecimal level3, BigDecimal level4,
			String limitationType, int limitationTimes,
			int limitationTimesPerAccount, boolean rulesConjunction,
			boolean active, int displayDateMonth, int displayDateDay,
			int displayDateYear, int displayDateHour, int displayDateMinute,
			int expirationDateMonth, int expirationDateDay,
			int expirationDateYear, int expirationDateHour,
			int expirationDateMinute, boolean neverExpire,
			ServiceContext serviceContext)
		throws PortalException {

		User user = _userLocalService.getUser(serviceContext.getUserId());

		CommerceDiscount commerceDiscount =
			commerceDiscountPersistence.findByPrimaryKey(commerceDiscountId);

		_validate(
			commerceDiscountId, serviceContext.getCompanyId(), title, target,
			useCouponCode, couponCode, maximumDiscountAmount, level1, level2,
			level3, level4, limitationType);

		Date date = new Date();

		Date displayDate = _portal.getDate(
			displayDateMonth, displayDateDay, displayDateYear, displayDateHour,
			displayDateMinute, user.getTimeZone(),
			CommerceDiscountDisplayDateException.class);

		Date expirationDate = null;

		if (!neverExpire) {
			expirationDate = _portal.getDate(
				expirationDateMonth, expirationDateDay, expirationDateYear,
				expirationDateHour, expirationDateMinute, user.getTimeZone(),
				CommerceDiscountExpirationDateException.class);
		}

		commerceDiscount.setTitle(title);
		commerceDiscount.setTarget(target);
		commerceDiscount.setUseCouponCode(useCouponCode);
		commerceDiscount.setCouponCode(couponCode);
		commerceDiscount.setUsePercentage(usePercentage);
		commerceDiscount.setMaximumDiscountAmount(maximumDiscountAmount);
		commerceDiscount.setLevel(level);
		commerceDiscount.setLevel1(level1);
		commerceDiscount.setLevel2(level2);
		commerceDiscount.setLevel3(level3);
		commerceDiscount.setLevel4(level4);
		commerceDiscount.setLimitationType(limitationType);
		commerceDiscount.setLimitationTimes(limitationTimes);
		commerceDiscount.setLimitationTimesPerAccount(
			limitationTimesPerAccount);
		commerceDiscount.setRulesConjunction(rulesConjunction);
		commerceDiscount.setActive(active);
		commerceDiscount.setDisplayDate(displayDate);
		commerceDiscount.setExpirationDate(expirationDate);

		if (commerceDiscount.getStatus() == WorkflowConstants.STATUS_EMPTY) {
			commerceDiscount.setStatus(
				_emptyModelManager.solveEmptyModel(
					commerceDiscount.getExternalReferenceCode(),
					commerceDiscount.getModelClassName(),
					commerceDiscount.getCompanyId(), 0,
					commerceDiscount.getStatus(),
					() -> WorkflowConstants.STATUS_DRAFT));
		}
		else if ((expirationDate == null) || expirationDate.after(date)) {
			commerceDiscount.setStatus(WorkflowConstants.STATUS_DRAFT);
		}
		else {
			commerceDiscount.setStatus(WorkflowConstants.STATUS_EXPIRED);
		}

		commerceDiscount.setStatusByUserId(user.getUserId());
		commerceDiscount.setStatusDate(serviceContext.getModifiedDate(date));
		commerceDiscount.setExpandoBridgeAttributes(serviceContext);

		commerceDiscount = commerceDiscountPersistence.update(commerceDiscount);

		return _startWorkflowInstance(
			user.getUserId(), commerceDiscount, serviceContext);
	}

	/**
	 * @deprecated As of Cavanaugh (7.4.x), replaced by {@link
	 *             #updateCommerceDiscountExternalReferenceCode(String, long)}
	 */
	@Deprecated
	@Indexable(type = IndexableType.REINDEX)
	@Override
	public CommerceDiscount updateCommerceDiscountExternalReferenceCode(
			long commerceDiscountId, String externalReferenceCode)
		throws PortalException {

		return updateCommerceDiscountExternalReferenceCode(
			externalReferenceCode, commerceDiscountId);
	}

	@Indexable(type = IndexableType.REINDEX)
	@Override
	public CommerceDiscount updateCommerceDiscountExternalReferenceCode(
			String externalReferenceCode, long commerceDiscountId)
		throws PortalException {

		CommerceDiscount commerceDiscount =
			commerceDiscountPersistence.findByPrimaryKey(commerceDiscountId);

		commerceDiscount.setExternalReferenceCode(externalReferenceCode);

		return commerceDiscountLocalService.updateCommerceDiscount(
			commerceDiscount);
	}

	@Indexable(type = IndexableType.REINDEX)
	@Override
	public CommerceDiscount updateStatus(
			long userId, long commerceDiscountId, int status,
			ServiceContext serviceContext,
			Map<String, Serializable> workflowContext)
		throws PortalException {

		User user = _userLocalService.getUser(userId);
		Date date = new Date();

		CommerceDiscount commerceDiscount =
			commerceDiscountPersistence.findByPrimaryKey(commerceDiscountId);

		if ((status == WorkflowConstants.STATUS_APPROVED) &&
			(commerceDiscount.getDisplayDate() != null) &&
			date.before(commerceDiscount.getDisplayDate())) {

			commerceDiscount.setActive(false);

			status = WorkflowConstants.STATUS_SCHEDULED;
		}

		if (status == WorkflowConstants.STATUS_APPROVED) {
			Date expirationDate = commerceDiscount.getExpirationDate();

			if ((expirationDate != null) && expirationDate.before(date)) {
				commerceDiscount.setExpirationDate(null);
			}

			if (commerceDiscount.getStatus() ==
					WorkflowConstants.STATUS_SCHEDULED) {

				commerceDiscount.setActive(true);
			}
		}

		if (status == WorkflowConstants.STATUS_EXPIRED) {
			commerceDiscount.setActive(false);
			commerceDiscount.setExpirationDate(date);
		}

		commerceDiscount.setStatus(status);
		commerceDiscount.setStatusByUserId(user.getUserId());
		commerceDiscount.setStatusByUserName(user.getFullName());
		commerceDiscount.setStatusDate(serviceContext.getModifiedDate(date));

		return commerceDiscountPersistence.update(commerceDiscount);
	}

	/**
	 * @deprecated As of Cavanaugh (7.4.x), replaced by {@link
	 *             #addOrUpdateCommerceDiscount(String, long, long, String, String,
	 *             boolean, String, boolean, BigDecimal, BigDecimal,
	 *             BigDecimal, BigDecimal, BigDecimal, String, int, boolean,
	 *             int, int, int, int, int, int, int, int, int, int, boolean,
	 *             ServiceContext)}
	 */
	@Deprecated
	@Indexable(type = IndexableType.REINDEX)
	@Override
	public CommerceDiscount upsertCommerceDiscount(
			long userId, long commerceDiscountId, String title, String target,
			boolean useCouponCode, String couponCode, boolean usePercentage,
			BigDecimal maximumDiscountAmount, BigDecimal level1,
			BigDecimal level2, BigDecimal level3, BigDecimal level4,
			String limitationType, int limitationTimes, boolean active,
			int displayDateMonth, int displayDateDay, int displayDateYear,
			int displayDateHour, int displayDateMinute, int expirationDateMonth,
			int expirationDateDay, int expirationDateYear,
			int expirationDateHour, int expirationDateMinute,
			String externalReferenceCode, boolean neverExpire,
			ServiceContext serviceContext)
		throws PortalException {

		return addOrUpdateCommerceDiscount(
			externalReferenceCode, userId, commerceDiscountId, title, target,
			useCouponCode, couponCode, usePercentage, maximumDiscountAmount,
			StringPool.BLANK, level1, level2, level3, level4, limitationType,
			limitationTimes, 0, true, active, displayDateMonth, displayDateDay,
			displayDateYear, displayDateHour, displayDateMinute,
			expirationDateMonth, expirationDateDay, expirationDateYear,
			expirationDateHour, expirationDateMinute, neverExpire,
			serviceContext);
	}

	/**
	 * @deprecated As of Cavanaugh (7.4.x), replaced by {@link
	 *             #addOrUpdateCommerceDiscount(String, long, long, String, String,
	 *             boolean, String, boolean, BigDecimal, String, BigDecimal,
	 *             BigDecimal, BigDecimal, BigDecimal, String, int, boolean,
	 *             boolean, int, int, int, int, int, int, int, int, int, int,
	 *             boolean, ServiceContext)}
	 */
	@Deprecated
	@Indexable(type = IndexableType.REINDEX)
	@Override
	public CommerceDiscount upsertCommerceDiscount(
			long userId, long commerceDiscountId, String title, String target,
			boolean useCouponCode, String couponCode, boolean usePercentage,
			BigDecimal maximumDiscountAmount, String level, BigDecimal level1,
			BigDecimal level2, BigDecimal level3, BigDecimal level4,
			String limitationType, int limitationTimes,
			boolean rulesConjunction, boolean active, int displayDateMonth,
			int displayDateDay, int displayDateYear, int displayDateHour,
			int displayDateMinute, int expirationDateMonth,
			int expirationDateDay, int expirationDateYear,
			int expirationDateHour, int expirationDateMinute,
			String externalReferenceCode, boolean neverExpire,
			ServiceContext serviceContext)
		throws PortalException {

		return addOrUpdateCommerceDiscount(
			externalReferenceCode, userId, commerceDiscountId, title, target,
			useCouponCode, couponCode, usePercentage, maximumDiscountAmount,
			level, level1, level2, level3, level4, limitationType,
			limitationTimes, rulesConjunction, active, displayDateMonth,
			displayDateDay, displayDateYear, displayDateHour, displayDateMinute,
			expirationDateMonth, expirationDateDay, expirationDateYear,
			expirationDateHour, expirationDateMinute, neverExpire,
			serviceContext);
	}

	/**
	 * @deprecated As of Cavanaugh (7.4.x), replaced by {@link
	 *             #addOrUpdateCommerceDiscount(String, long, long, String, String,
	 *             boolean, String, boolean, BigDecimal, String, BigDecimal,
	 *             BigDecimal, BigDecimal, BigDecimal, String, int, int,
	 *             boolean, boolean, int, int, int, int, int, int, int, int,
	 *             int, int, boolean, ServiceContext)}
	 */
	@Deprecated
	@Indexable(type = IndexableType.REINDEX)
	@Override
	public CommerceDiscount upsertCommerceDiscount(
			long userId, long commerceDiscountId, String title, String target,
			boolean useCouponCode, String couponCode, boolean usePercentage,
			BigDecimal maximumDiscountAmount, String level, BigDecimal level1,
			BigDecimal level2, BigDecimal level3, BigDecimal level4,
			String limitationType, int limitationTimes,
			int limitationTimesPerAccount, boolean rulesConjunction,
			boolean active, int displayDateMonth, int displayDateDay,
			int displayDateYear, int displayDateHour, int displayDateMinute,
			int expirationDateMonth, int expirationDateDay,
			int expirationDateYear, int expirationDateHour,
			int expirationDateMinute, String externalReferenceCode,
			boolean neverExpire, ServiceContext serviceContext)
		throws PortalException {

		return addOrUpdateCommerceDiscount(
			externalReferenceCode, userId, commerceDiscountId, title, target,
			useCouponCode, couponCode, usePercentage, maximumDiscountAmount,
			level, level1, level2, level3, level4, limitationType,
			limitationTimes, limitationTimesPerAccount, rulesConjunction,
			active, displayDateMonth, displayDateDay, displayDateYear,
			displayDateHour, displayDateMinute, expirationDateMonth,
			expirationDateDay, expirationDateYear, expirationDateHour,
			expirationDateMinute, neverExpire, serviceContext);
	}

	private SearchContext _buildSearchContext(
		long companyId, long[] groupIds, String keywords, int status, int start,
		int end, Sort sort) {

		SearchContext searchContext = new SearchContext();

		searchContext.setAttributes(
			HashMapBuilder.<String, Serializable>put(
				CommerceDiscountIndexer.FIELD_GROUP_IDS, groupIds
			).put(
				Field.ENTRY_CLASS_PK, keywords
			).put(
				Field.STATUS, status
			).put(
				Field.TITLE, keywords
			).put(
				"params",
				LinkedHashMapBuilder.<String, Object>put(
					"keywords", keywords
				).build()
			).put(
				"skipCommerceAccountGroupValidation", true
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

	private void _checkCommerceDiscountsByDisplayDate() throws PortalException {
		List<CommerceDiscount> commerceDiscounts =
			commerceDiscountPersistence.findByLtD_S(
				new Date(), WorkflowConstants.STATUS_SCHEDULED);

		for (CommerceDiscount commerceDiscount : commerceDiscounts) {
			long userId = _portal.getValidUserId(
				commerceDiscount.getCompanyId(), commerceDiscount.getUserId());

			ServiceContext serviceContext = new ServiceContext();

			serviceContext.setCommand(Constants.UPDATE);

			commerceDiscountLocalService.updateStatus(
				userId, commerceDiscount.getCommerceDiscountId(),
				WorkflowConstants.STATUS_APPROVED, serviceContext,
				new HashMap<String, Serializable>());
		}
	}

	private void _checkCommerceDiscountsByExpirationDate()
		throws PortalException {

		List<CommerceDiscount> commerceDiscounts =
			commerceDiscountPersistence.findByLtE_S(
				new Date(), WorkflowConstants.STATUS_APPROVED);

		if (_log.isDebugEnabled()) {
			_log.debug(
				"Expiring " + commerceDiscounts.size() + " commerce discounts");
		}

		if ((commerceDiscounts != null) && !commerceDiscounts.isEmpty()) {
			for (CommerceDiscount commerceDiscount : commerceDiscounts) {
				long userId = _portal.getValidUserId(
					commerceDiscount.getCompanyId(),
					commerceDiscount.getUserId());

				ServiceContext serviceContext = new ServiceContext();

				serviceContext.setCommand(Constants.UPDATE);

				commerceDiscountLocalService.updateStatus(
					userId, commerceDiscount.getCommerceDiscountId(),
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

	private List<CommerceDiscount> _getCommerceDiscounts(Hits hits)
		throws PortalException {

		List<Document> documents = hits.toList();

		List<CommerceDiscount> commerceDiscounts = new ArrayList<>(
			documents.size());

		for (Document document : documents) {
			long commerceDiscountId = GetterUtil.getLong(
				document.get(Field.ENTRY_CLASS_PK));

			CommerceDiscount commerceDiscount = fetchCommerceDiscount(
				commerceDiscountId);

			if (commerceDiscount == null) {
				commerceDiscounts = null;

				Indexer<CommerceDiscount> indexer =
					IndexerRegistryUtil.getIndexer(CommerceDiscount.class);

				long companyId = GetterUtil.getLong(
					document.get(Field.COMPANY_ID));

				indexer.delete(companyId, document.getUID());
			}
			else if (commerceDiscounts != null) {
				commerceDiscounts.add(commerceDiscount);
			}
		}

		return commerceDiscounts;
	}

	private GroupByStep _getGroupByStep(
		FromStep fromStep, long commerceChannelAccountEntryRelId,
		long cpDefinitionId, long cpInstanceId, String unitOfMeasureKey) {

		JoinStep joinStep = fromStep.from(
			CommerceDiscountTable.INSTANCE
		).innerJoinON(
			CommerceChannelAccountEntryRelTable.INSTANCE,
			CommerceChannelAccountEntryRelTable.INSTANCE.classPK.eq(
				CommerceDiscountTable.INSTANCE.commerceDiscountId)
		).innerJoinON(
			CommerceDiscountRelTable.INSTANCE,
			CommerceDiscountRelTable.INSTANCE.commerceDiscountId.eq(
				CommerceDiscountTable.INSTANCE.commerceDiscountId)
		);

		Predicate predicate =
			CommerceChannelAccountEntryRelTable.INSTANCE.
				commerceChannelAccountEntryRelId.eq(
					commerceChannelAccountEntryRelId
				).and(
					CommerceDiscountTable.INSTANCE.active.eq(true)
				).and(
					CommerceDiscountTable.INSTANCE.status.eq(
						WorkflowConstants.STATUS_APPROVED)
				);

		return joinStep.where(
			predicate.and(
				_toTargetPredicate(
					cpDefinitionId, cpInstanceId, unitOfMeasureKey)));
	}

	private GroupByStep _getGroupByStep(
		FromStep fromStep, Long companyId, Long commerceAccountId,
		long[] commerceAccountGroupIds, Long commerceChannelId,
		Long commerceOrderTypeId, Long cpDefinitionId, Long cpInstanceId,
		String target, String unitOfMeasureKey) {

		JoinStep joinStep = fromStep.from(CommerceDiscountTable.INSTANCE);

		Predicate predicate = CommerceDiscountTable.INSTANCE.active.eq(
			true
		).and(
			() -> {
				if (companyId == null) {
					return null;
				}

				return CommerceDiscountTable.INSTANCE.companyId.eq(companyId);
			}
		).and(
			CommerceDiscountTable.INSTANCE.status.eq(
				WorkflowConstants.STATUS_APPROVED)
		);

		if (commerceAccountId != null) {
			joinStep = joinStep.innerJoinON(
				CommerceDiscountAccountRelTable.INSTANCE,
				CommerceDiscountAccountRelTable.INSTANCE.commerceDiscountId.eq(
					CommerceDiscountTable.INSTANCE.commerceDiscountId));
			predicate = predicate.and(
				CommerceDiscountAccountRelTable.INSTANCE.commerceAccountId.eq(
					commerceAccountId));
		}
		else {
			joinStep = joinStep.leftJoinOn(
				CommerceDiscountAccountRelTable.INSTANCE,
				CommerceDiscountAccountRelTable.INSTANCE.commerceDiscountId.eq(
					CommerceDiscountTable.INSTANCE.commerceDiscountId));
			predicate = predicate.and(
				CommerceDiscountAccountRelTable.INSTANCE.
					commerceDiscountAccountRelId.isNull());
		}

		if (commerceAccountGroupIds != null) {
			if (commerceAccountGroupIds.length == 0) {
				commerceAccountGroupIds = new long[] {0};
			}

			joinStep = joinStep.innerJoinON(
				CommerceDiscountCommerceAccountGroupRelTable.INSTANCE,
				CommerceDiscountCommerceAccountGroupRelTable.INSTANCE.
					commerceDiscountId.eq(
						CommerceDiscountTable.INSTANCE.commerceDiscountId));

			predicate = predicate.and(
				CommerceDiscountCommerceAccountGroupRelTable.INSTANCE.
					commerceAccountGroupId.in(
						ArrayUtil.toArray(commerceAccountGroupIds)));
		}
		else {
			joinStep = joinStep.leftJoinOn(
				CommerceDiscountCommerceAccountGroupRelTable.INSTANCE,
				CommerceDiscountCommerceAccountGroupRelTable.INSTANCE.
					commerceDiscountId.eq(
						CommerceDiscountTable.INSTANCE.commerceDiscountId));
			predicate = predicate.and(
				CommerceDiscountCommerceAccountGroupRelTable.INSTANCE.
					commerceDiscountCommerceAccountGroupRelId.isNull());
		}

		if (commerceChannelId != null) {
			joinStep = joinStep.innerJoinON(
				CommerceChannelRelTable.INSTANCE,
				CommerceChannelRelTable.INSTANCE.classPK.eq(
					CommerceDiscountTable.INSTANCE.commerceDiscountId
				).and(
					CommerceChannelRelTable.INSTANCE.classNameId.eq(
						_classNameLocalService.getClassNameId(
							CommerceDiscount.class.getName()))
				));
			predicate = predicate.and(
				CommerceChannelRelTable.INSTANCE.commerceChannelId.eq(
					commerceChannelId));
		}
		else {
			joinStep = joinStep.leftJoinOn(
				CommerceChannelRelTable.INSTANCE,
				CommerceChannelRelTable.INSTANCE.classPK.eq(
					CommerceDiscountTable.INSTANCE.commerceDiscountId
				).and(
					CommerceChannelRelTable.INSTANCE.classNameId.eq(
						_classNameLocalService.getClassNameId(
							CommerceDiscount.class.getName()))
				));
			predicate = predicate.and(
				CommerceChannelRelTable.INSTANCE.commerceChannelRelId.isNull());
		}

		if (commerceOrderTypeId != null) {
			joinStep = joinStep.innerJoinON(
				CommerceDiscountOrderTypeRelTable.INSTANCE,
				CommerceDiscountOrderTypeRelTable.INSTANCE.commerceDiscountId.
					eq(CommerceDiscountTable.INSTANCE.commerceDiscountId));
			predicate = predicate.and(
				CommerceDiscountOrderTypeRelTable.INSTANCE.commerceOrderTypeId.
					eq(commerceOrderTypeId));
		}
		else {
			joinStep = joinStep.leftJoinOn(
				CommerceDiscountOrderTypeRelTable.INSTANCE,
				CommerceDiscountOrderTypeRelTable.INSTANCE.commerceDiscountId.
					eq(CommerceDiscountTable.INSTANCE.commerceDiscountId));
			predicate = predicate.and(
				CommerceDiscountOrderTypeRelTable.INSTANCE.
					commerceDiscountOrderTypeRelId.isNull());
		}

		if (!Validator.isBlank(target)) {
			return joinStep.where(
				predicate.and(
					CommerceDiscountTable.INSTANCE.target.eq(target)));
		}

		joinStep = joinStep.innerJoinON(
			CommerceDiscountRelTable.INSTANCE,
			CommerceDiscountRelTable.INSTANCE.commerceDiscountId.eq(
				CommerceDiscountTable.INSTANCE.commerceDiscountId));

		return joinStep.where(
			predicate.and(
				_toTargetPredicate(
					cpDefinitionId, cpInstanceId, unitOfMeasureKey)));
	}

	private boolean _isWorkflowEnabled(
		long companyId, long groupId, String className) {

		return _workflowDefinitionLinkLocalService.hasWorkflowDefinitionLink(
			companyId, groupId, className, 0);
	}

	private CommerceDiscount _startWorkflowInstance(
			long userId, CommerceDiscount commerceDiscount,
			ServiceContext serviceContext)
		throws PortalException {

		Map<String, Serializable> workflowContext = new HashMap<>();

		return WorkflowHandlerRegistryUtil.startWorkflowInstance(
			commerceDiscount.getCompanyId(), 0L, userId,
			CommerceDiscount.class.getName(),
			commerceDiscount.getCommerceDiscountId(), commerceDiscount,
			serviceContext, workflowContext);
	}

	private Predicate _toTargetPredicate(
		long cpDefinitionId, long cpInstanceId, String unitOfMeasureKey) {

		Predicate predicate = CommerceDiscountTable.INSTANCE.target.eq(
			CommerceDiscountConstants.TARGET_PRODUCTS
		).and(
			CommerceDiscountRelTable.INSTANCE.classPK.eq(cpDefinitionId)
		).and(
			CommerceDiscountRelTable.INSTANCE.classNameId.eq(
				_classNameLocalService.getClassNameId(
					CPDefinition.class.getName()))
		);

		Predicate andPredicate = CommerceDiscountTable.INSTANCE.target.eq(
			CommerceDiscountConstants.TARGET_SKUS
		).and(
			CommerceDiscountRelTable.INSTANCE.classPK.eq(cpInstanceId)
		).and(
			CommerceDiscountRelTable.INSTANCE.classNameId.eq(
				_classNameLocalService.getClassNameId(
					CPInstance.class.getName()))
		);

		if (!Validator.isBlank(unitOfMeasureKey)) {
			andPredicate = andPredicate.and(
				CommerceDiscountRelTable.INSTANCE.typeSettings.like(
					StringBundler.concat(
						"%unitOfMeasureKey=", unitOfMeasureKey,
						StringPool.PERCENT)));
		}

		predicate = predicate.or(andPredicate);

		long[] assetCategoryIds = _getAssetCategoryIds(cpDefinitionId);

		if (assetCategoryIds != null) {
			if (assetCategoryIds.length == 0) {
				assetCategoryIds = new long[] {0};
			}

			predicate = predicate.or(
				CommerceDiscountTable.INSTANCE.target.eq(
					CommerceDiscountConstants.TARGET_CATEGORIES
				).and(
					CommerceDiscountRelTable.INSTANCE.classPK.in(
						ArrayUtil.toArray(assetCategoryIds))
				).and(
					CommerceDiscountRelTable.INSTANCE.classNameId.eq(
						_classNameLocalService.getClassNameId(
							AssetCategory.class.getName()))
				));
		}

		long[] commercePricingClasses =
			_commercePricingClassLocalService.
				getCommercePricingClassByCPDefinition(cpDefinitionId);

		if (commercePricingClasses != null) {
			if (commercePricingClasses.length == 0) {
				commercePricingClasses = new long[] {0};
			}

			predicate = predicate.or(
				CommerceDiscountTable.INSTANCE.target.eq(
					CommerceDiscountConstants.TARGET_PRODUCT_GROUPS
				).and(
					CommerceDiscountRelTable.INSTANCE.classPK.in(
						ArrayUtil.toArray(commercePricingClasses))
				).and(
					CommerceDiscountRelTable.INSTANCE.classNameId.eq(
						_classNameLocalService.getClassNameId(
							CommercePricingClass.class.getName()))
				));
		}

		return predicate.withParentheses();
	}

	private void _validate(
			long commerceDiscountId, long companyId, String title,
			String target, boolean useCouponCode, String couponCode,
			BigDecimal maxDiscountAmount, BigDecimal level1, BigDecimal level2,
			BigDecimal level3, BigDecimal level4, String limitationType)
		throws PortalException {

		if (Validator.isNull(title)) {
			throw new CommerceDiscountTitleException();
		}

		CommerceDiscountTarget commerceDiscountTarget =
			_commerceDiscountTargetRegistry.getCommerceDiscountTarget(target);

		if (commerceDiscountTarget == null) {
			throw new CommerceDiscountTargetException();
		}

		if (useCouponCode) {
			if (Validator.isNull(couponCode)) {
				throw new CommerceDiscountCouponCodeException();
			}

			CommerceDiscount commerceDiscount =
				commerceDiscountPersistence.fetchByC_C_First(
					companyId, couponCode,
					CommerceDiscountCreateDateComparator.getInstance(true));

			if (((commerceDiscountId <= 0) && (commerceDiscount != null)) ||
				((commerceDiscount != null) &&
				 (commerceDiscountId !=
					 commerceDiscount.getCommerceDiscountId()))) {

				throw new DuplicateCommerceDiscountException();
			}
		}

		if (Validator.isNull(limitationType)) {
			throw new CommerceDiscountLimitationTypeException();
		}

		BigDecimal maxValue = BigDecimal.valueOf(
			GetterUtil.getDouble(CommercePriceConstants.PRICE_VALUE_MAX));

		if (((maxDiscountAmount != null) &&
			 (maxDiscountAmount.compareTo(maxValue) > 0)) ||
			((level1 != null) && (level1.compareTo(maxValue) > 0)) ||
			((level2 != null) && (level2.compareTo(maxValue) > 0)) ||
			((level3 != null) && (level3.compareTo(maxValue) > 0)) ||
			((level4 != null) && (level4.compareTo(maxValue) > 0))) {

			throw new CommerceDiscountMaxPriceValueException();
		}

		BigDecimal minValue = BigDecimal.valueOf(
			GetterUtil.getDouble(CommercePriceConstants.PRICE_VALUE_MIN));

		if (((maxDiscountAmount != null) &&
			 (maxDiscountAmount.compareTo(minValue) < 0)) ||
			((level1 != null) && (level1.compareTo(minValue) < 0)) ||
			((level2 != null) && (level2.compareTo(minValue) < 0)) ||
			((level3 != null) && (level3.compareTo(minValue) < 0)) ||
			((level4 != null) && (level4.compareTo(minValue) < 0))) {

			throw new CommerceDiscountMinPriceValueException();
		}

		if (commerceDiscountId > 0) {
			List<CommerceDiscountRule> commerceDiscountRules =
				_commerceDiscountRuleLocalService.getCommerceDiscountRules(
					commerceDiscountId, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
					null);

			for (CommerceDiscountRule commerceDiscountRule :
					commerceDiscountRules) {

				CommerceDiscountRuleType commerceDiscountRuleType =
					_commerceDiscountRuleTypeRegistry.
						getCommerceDiscountRuleType(
							commerceDiscountRule.getType());

				if (!commerceDiscountRuleType.validate(
						commerceDiscountRule.getSettingsProperty(
							commerceDiscountRule.getType()))) {

					throw new CommerceDiscountRuleTypeSettingsException();
				}
			}
		}
	}

	private static final String[] _SELECTED_FIELD_NAMES = {
		Field.ENTRY_CLASS_PK, Field.COMPANY_ID, Field.UID
	};

	private static final Log _log = LogFactoryUtil.getLog(
		CommerceDiscountLocalServiceImpl.class);

	@Reference
	private AssetEntryLocalService _assetEntryLocalService;

	@Reference
	private ClassNameLocalService _classNameLocalService;

	@Reference
	private CommerceChannelAccountEntryRelLocalService
		_commerceChannelAccountEntryRelLocalService;

	@Reference
	private CommerceDiscountCommerceAccountGroupRelLocalService
		_commerceDiscountCommerceAccountGroupRelLocalService;

	@Reference
	private CommerceDiscountOrderTypeRelLocalService
		_commerceDiscountOrderTypeRelLocalService;

	@Reference
	private CommerceDiscountRelLocalService _commerceDiscountRelLocalService;

	@Reference
	private CommerceDiscountRuleLocalService _commerceDiscountRuleLocalService;

	@Reference
	private CommerceDiscountRuleTypeRegistry _commerceDiscountRuleTypeRegistry;

	@Reference
	private CommerceDiscountTargetRegistry _commerceDiscountTargetRegistry;

	@Reference
	private CommerceDiscountUsageEntryLocalService
		_commerceDiscountUsageEntryLocalService;

	@Reference
	private CommercePricingClassLocalService _commercePricingClassLocalService;

	@Reference
	private EmptyModelManager _emptyModelManager;

	@Reference
	private ExpandoRowLocalService _expandoRowLocalService;

	@Reference
	private Portal _portal;

	@Reference
	private ResourceLocalService _resourceLocalService;

	@Reference
	private UserLocalService _userLocalService;

	@Reference
	private WorkflowDefinitionLinkLocalService
		_workflowDefinitionLinkLocalService;

	@Reference
	private WorkflowInstanceLinkLocalService _workflowInstanceLinkLocalService;

}