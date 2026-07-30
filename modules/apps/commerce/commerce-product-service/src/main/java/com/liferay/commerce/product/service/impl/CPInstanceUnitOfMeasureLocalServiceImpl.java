/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.service.impl;

import com.liferay.commerce.product.exception.CPInstanceUnitOfMeasureIncrementalOrderQuantityException;
import com.liferay.commerce.product.exception.CPInstanceUnitOfMeasureKeyException;
import com.liferay.commerce.product.exception.CPInstanceUnitOfMeasureNameException;
import com.liferay.commerce.product.exception.CPInstanceUnitOfMeasureRateException;
import com.liferay.commerce.product.exception.CPInstanceUnitOfMeasureSkuException;
import com.liferay.commerce.product.exception.DuplicateCPInstanceUnitOfMeasureKeyException;
import com.liferay.commerce.product.model.CPInstanceUnitOfMeasure;
import com.liferay.commerce.product.service.base.CPInstanceUnitOfMeasureLocalServiceBaseImpl;
import com.liferay.commerce.product.util.comparator.CPInstanceUnitOfMeasurePriorityComparator;
import com.liferay.portal.aop.AopService;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.sanitizer.Sanitizer;
import com.liferay.portal.kernel.sanitizer.SanitizerUtil;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.Validator;

import java.math.BigDecimal;
import java.math.RoundingMode;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Marco Leo
 */
@Component(
	property = "model.class.name=com.liferay.commerce.product.model.CPInstanceUnitOfMeasure",
	service = AopService.class
)
public class CPInstanceUnitOfMeasureLocalServiceImpl
	extends CPInstanceUnitOfMeasureLocalServiceBaseImpl {

	@Override
	public CPInstanceUnitOfMeasure addCPInstanceUnitOfMeasure(
			long userId, long cpInstanceId, boolean active,
			BigDecimal incrementalOrderQuantity, String key,
			Map<Locale, String> nameMap, int precision,
			BigDecimal pricingQuantity, boolean primary, double priority,
			BigDecimal rate, String sku)
		throws PortalException {

		_validateCPInstanceUnitOfMeasureIncrementalOrderQuantity(
			incrementalOrderQuantity, precision);
		_validateCPInstanceUnitOfMeasureKey(cpInstanceId, 0, key);
		_validateCPInstanceUnitOfMeasureName(nameMap);
		_validateCPInstanceUnitOfMeasureRate(rate);
		_validateCPInstanceUnitOfMeasureSKU(sku);

		CPInstanceUnitOfMeasure cpInstanceUnitOfMeasure =
			cpInstanceUnitOfMeasurePersistence.create(
				counterLocalService.increment());

		User user = _userLocalService.getUser(userId);

		cpInstanceUnitOfMeasure.setCompanyId(user.getCompanyId());
		cpInstanceUnitOfMeasure.setUserId(user.getUserId());
		cpInstanceUnitOfMeasure.setUserName(user.getFullName());

		cpInstanceUnitOfMeasure.setCPInstanceId(cpInstanceId);
		cpInstanceUnitOfMeasure.setActive(active);
		cpInstanceUnitOfMeasure.setIncrementalOrderQuantity(
			_normalizeCPInstanceUnitOfMeasureBaseDecimalQuantity(
				incrementalOrderQuantity, precision));
		cpInstanceUnitOfMeasure.setKey(key);
		cpInstanceUnitOfMeasure.setNameMap(
			_sanitizeCPInstanceUnitOfMeasureNameMap(
				cpInstanceUnitOfMeasure.getCPInstanceUnitOfMeasureId(), nameMap,
				user, 0));
		cpInstanceUnitOfMeasure.setPrecision(precision);
		cpInstanceUnitOfMeasure.setPricingQuantity(pricingQuantity);
		cpInstanceUnitOfMeasure.setPrimary(primary);
		cpInstanceUnitOfMeasure.setPriority(priority);
		cpInstanceUnitOfMeasure.setRate(rate);
		cpInstanceUnitOfMeasure.setSku(sku);

		if (cpInstanceUnitOfMeasure.isPrimary()) {
			_updatePrimary(
				cpInstanceUnitOfMeasure.getCPInstanceUnitOfMeasureId(),
				cpInstanceId);
		}

		return cpInstanceUnitOfMeasurePersistence.update(
			cpInstanceUnitOfMeasure);
	}

	@Override
	public CPInstanceUnitOfMeasure addOrUpdateCPInstanceUnitOfMeasure(
			long userId, long cpInstanceId, boolean active,
			BigDecimal incrementalOrderQuantity, String key,
			Map<Locale, String> nameMap, int precision,
			BigDecimal pricingQuantity, boolean primary, double priority,
			BigDecimal rate, String sku)
		throws PortalException {

		CPInstanceUnitOfMeasure cpInstanceUnitOfMeasure =
			cpInstanceUnitOfMeasurePersistence.fetchByC_K(cpInstanceId, key);

		if (cpInstanceUnitOfMeasure == null) {
			return cpInstanceUnitOfMeasureLocalService.
				addCPInstanceUnitOfMeasure(
					userId, cpInstanceId, active, incrementalOrderQuantity, key,
					nameMap, precision, pricingQuantity, primary, priority,
					rate, sku);
		}

		return cpInstanceUnitOfMeasureLocalService.
			updateCPInstanceUnitOfMeasure(
				cpInstanceUnitOfMeasure.getCPInstanceUnitOfMeasureId(),
				cpInstanceId, active, incrementalOrderQuantity, key, nameMap,
				precision, pricingQuantity, primary, priority, rate, sku);
	}

	@Override
	public CPInstanceUnitOfMeasure fetchCPInstanceUnitOfMeasure(
		long cpInstanceId, String key) {

		return cpInstanceUnitOfMeasurePersistence.fetchByC_K(cpInstanceId, key);
	}

	@Override
	public CPInstanceUnitOfMeasure fetchCPInstanceUnitOfMeasure(
		long companyId, String key, String sku) {

		return cpInstanceUnitOfMeasurePersistence.fetchByC_K_S_First(
			companyId, key, sku, null);
	}

	@Override
	public CPInstanceUnitOfMeasure fetchPrimaryCPInstanceUnitOfMeasure(
		long cpInstanceId) {

		return cpInstanceUnitOfMeasurePersistence.fetchByC_P_First(
			cpInstanceId, true, null);
	}

	@Override
	public List<CPInstanceUnitOfMeasure> getActiveCPInstanceUnitOfMeasures(
		long cpInstanceId) {

		return cpInstanceUnitOfMeasurePersistence.findByC_A(
			cpInstanceId, true, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
			CPInstanceUnitOfMeasurePriorityComparator.getInstance(true));
	}

	@Override
	public int getActiveCPInstanceUnitOfMeasuresCount(long cpInstanceId) {
		return cpInstanceUnitOfMeasurePersistence.countByC_A(
			cpInstanceId, true);
	}

	@Override
	public CPInstanceUnitOfMeasure getCPInstanceUnitOfMeasure(
			long cpInstanceId, String key)
		throws PortalException {

		return cpInstanceUnitOfMeasurePersistence.findByC_K(cpInstanceId, key);
	}

	@Override
	public List<CPInstanceUnitOfMeasure> getCPInstanceUnitOfMeasures(
		long cpInstanceId, int start, int end,
		OrderByComparator<CPInstanceUnitOfMeasure> orderByComparator) {

		return cpInstanceUnitOfMeasurePersistence.findByCPInstanceId(
			cpInstanceId, start, end, orderByComparator);
	}

	@Override
	public List<CPInstanceUnitOfMeasure> getCPInstanceUnitOfMeasures(
		long companyId, String sku) {

		return cpInstanceUnitOfMeasurePersistence.findByC_S(companyId, sku);
	}

	@Override
	public int getCPInstanceUnitOfMeasuresCount(long cpInstanceId) {
		return cpInstanceUnitOfMeasurePersistence.countByCPInstanceId(
			cpInstanceId);
	}

	@Override
	public int getCPInstanceUnitOfMeasuresCount(long companyId, String sku) {
		return cpInstanceUnitOfMeasurePersistence.countByC_S(companyId, sku);
	}

	@Override
	public CPInstanceUnitOfMeasure updateCPInstanceUnitOfMeasure(
			long cpInstanceUnitOfMeasureId, long cpInstanceId, boolean active,
			BigDecimal incrementalOrderQuantity, String key,
			Map<Locale, String> nameMap, int precision,
			BigDecimal pricingQuantity, boolean primary, double priority,
			BigDecimal rate, String sku)
		throws PortalException {

		CPInstanceUnitOfMeasure cpInstanceUnitOfMeasure =
			cpInstanceUnitOfMeasurePersistence.findByPrimaryKey(
				cpInstanceUnitOfMeasureId);

		_validateCPInstanceUnitOfMeasureIncrementalOrderQuantity(
			incrementalOrderQuantity, precision);
		_validateCPInstanceUnitOfMeasureKey(
			cpInstanceId, cpInstanceUnitOfMeasureId, key);
		_validateCPInstanceUnitOfMeasureName(nameMap);
		_validateCPInstanceUnitOfMeasureRate(rate);
		_validateCPInstanceUnitOfMeasureSKU(sku);

		cpInstanceUnitOfMeasure.setActive(active);
		cpInstanceUnitOfMeasure.setIncrementalOrderQuantity(
			_normalizeCPInstanceUnitOfMeasureBaseDecimalQuantity(
				incrementalOrderQuantity, precision));
		cpInstanceUnitOfMeasure.setKey(key);
		cpInstanceUnitOfMeasure.setNameMap(
			_sanitizeCPInstanceUnitOfMeasureNameMap(
				cpInstanceUnitOfMeasureId, nameMap, null,
				cpInstanceUnitOfMeasure.getUserId()));
		cpInstanceUnitOfMeasure.setPrecision(precision);
		cpInstanceUnitOfMeasure.setPricingQuantity(pricingQuantity);
		cpInstanceUnitOfMeasure.setPrimary(primary);
		cpInstanceUnitOfMeasure.setPriority(priority);
		cpInstanceUnitOfMeasure.setRate(rate);
		cpInstanceUnitOfMeasure.setSku(sku);

		if (cpInstanceUnitOfMeasure.isPrimary()) {
			_updatePrimary(
				cpInstanceUnitOfMeasure.getCPInstanceUnitOfMeasureId(),
				cpInstanceId);
		}

		return cpInstanceUnitOfMeasurePersistence.update(
			cpInstanceUnitOfMeasure);
	}

	private BigDecimal _normalizeCPInstanceUnitOfMeasureBaseDecimalQuantity(
		BigDecimal baseDecimalQuantity, int precision) {

		return baseDecimalQuantity.setScale(precision, RoundingMode.HALF_UP);
	}

	private Map<Locale, String> _sanitizeCPInstanceUnitOfMeasureNameMap(
			long cpInstanceUnitOfMeasureId, Map<Locale, String> nameMap,
			User user, long userId)
		throws PortalException {

		if (user == null) {
			user = _userLocalService.getUser(userId);
		}

		Map<Locale, String> updatedNameMap = new HashMap<>();

		for (Map.Entry<Locale, String> nameMapEntry : nameMap.entrySet()) {
			if (nameMapEntry.getValue() == null) {
				continue;
			}

			updatedNameMap.put(
				nameMapEntry.getKey(),
				SanitizerUtil.sanitize(
					user.getCompanyId(), user.getGroupId(), user.getUserId(),
					CPInstanceUnitOfMeasure.class.getName(),
					cpInstanceUnitOfMeasureId, ContentTypes.TEXT_HTML,
					Sanitizer.MODE_ALL, nameMapEntry.getValue(), null));
		}

		return updatedNameMap;
	}

	private void _updatePrimary(
		long cpInstanceUnitOfMeasureId, long cpInstanceId) {

		CPInstanceUnitOfMeasure cpInstanceUnitOfMeasure =
			cpInstanceUnitOfMeasurePersistence.fetchByC_P_First(
				cpInstanceId, true, null);

		if ((cpInstanceUnitOfMeasure != null) &&
			(cpInstanceUnitOfMeasure.getCPInstanceUnitOfMeasureId() !=
				cpInstanceUnitOfMeasureId)) {

			cpInstanceUnitOfMeasure.setPrimary(false);

			cpInstanceUnitOfMeasurePersistence.update(cpInstanceUnitOfMeasure);
		}
	}

	private void _validateCPInstanceUnitOfMeasureIncrementalOrderQuantity(
			BigDecimal incrementalOrderQuantity, int precision)
		throws PortalException {

		if (incrementalOrderQuantity == null) {
			throw new CPInstanceUnitOfMeasureIncrementalOrderQuantityException(
				"Incremental order quantity is mandatory");
		}

		if (incrementalOrderQuantity.compareTo(BigDecimal.ZERO) < 1) {
			throw new CPInstanceUnitOfMeasureIncrementalOrderQuantityException(
				"Incremental order quantity must be greater than 0");
		}

		if (incrementalOrderQuantity.scale() > precision) {
			throw new CPInstanceUnitOfMeasureIncrementalOrderQuantityException(
				"Incremental order quantity scale is invalid");
		}
	}

	private void _validateCPInstanceUnitOfMeasureKey(
			long cpInstanceId, long cpInstanceUnitOfMeasureId, String key)
		throws PortalException {

		if (Validator.isNull(key)) {
			throw new CPInstanceUnitOfMeasureKeyException("Key is mandatory");
		}

		CPInstanceUnitOfMeasure cpInstanceUnitOfMeasure =
			cpInstanceUnitOfMeasurePersistence.fetchByC_K(cpInstanceId, key);

		if ((cpInstanceUnitOfMeasure != null) &&
			(cpInstanceUnitOfMeasure.getCPInstanceUnitOfMeasureId() !=
				cpInstanceUnitOfMeasureId)) {

			throw new DuplicateCPInstanceUnitOfMeasureKeyException(
				"There is another commerce product instance unit of measure " +
					"with key " + key);
		}
	}

	private void _validateCPInstanceUnitOfMeasureName(
			Map<Locale, String> nameMap)
		throws PortalException {

		if ((nameMap == null) || nameMap.isEmpty()) {
			throw new CPInstanceUnitOfMeasureNameException("Name is mandatory");
		}
	}

	private void _validateCPInstanceUnitOfMeasureRate(BigDecimal rate)
		throws PortalException {

		if (rate == null) {
			throw new CPInstanceUnitOfMeasureRateException("Rate is mandatory");
		}

		if (rate.compareTo(BigDecimal.ZERO) < 1) {
			throw new CPInstanceUnitOfMeasureRateException(
				"Rate quantity must be greater than 0");
		}
	}

	private void _validateCPInstanceUnitOfMeasureSKU(String sku)
		throws PortalException {

		if (Validator.isNull(sku)) {
			throw new CPInstanceUnitOfMeasureSkuException("SKU is mandatory");
		}
	}

	@Reference
	private UserLocalService _userLocalService;

}