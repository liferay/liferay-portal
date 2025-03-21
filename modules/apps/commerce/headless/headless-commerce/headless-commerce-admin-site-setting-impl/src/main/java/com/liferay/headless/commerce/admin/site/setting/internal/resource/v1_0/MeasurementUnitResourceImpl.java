/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.admin.site.setting.internal.resource.v1_0;

import com.liferay.commerce.product.constants.CPMeasurementUnitConstants;
import com.liferay.commerce.product.exception.CPMeasurementUnitTypeException;
import com.liferay.commerce.product.exception.DuplicateCPMeasurementUnitExternalReferenceCodeException;
import com.liferay.commerce.product.exception.DuplicateCPMeasurementUnitKeyException;
import com.liferay.commerce.product.exception.NoSuchCPMeasurementUnitException;
import com.liferay.commerce.product.model.CPMeasurementUnit;
import com.liferay.commerce.product.service.CPMeasurementUnitService;
import com.liferay.headless.commerce.admin.site.setting.dto.v1_0.MeasurementUnit;
import com.liferay.headless.commerce.admin.site.setting.resource.v1_0.MeasurementUnitResource;
import com.liferay.headless.commerce.core.util.LanguageUtils;
import com.liferay.headless.commerce.core.util.ServiceContextHelper;
import com.liferay.petra.string.StringUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.search.filter.Filter;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.vulcan.dto.converter.DTOConverter;
import com.liferay.portal.vulcan.dto.converter.DTOConverterRegistry;
import com.liferay.portal.vulcan.dto.converter.DefaultDTOConverterContext;
import com.liferay.portal.vulcan.pagination.Page;
import com.liferay.portal.vulcan.pagination.Pagination;

import jakarta.ws.rs.core.Response;

import java.util.Collections;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ServiceScope;

/**
 * @author Zoltán Takács
 * @author Crescenzo Rega
 */
@Component(
	properties = "OSGI-INF/liferay/rest/v1_0/measurement-unit.properties",
	scope = ServiceScope.PROTOTYPE, service = MeasurementUnitResource.class
)
public class MeasurementUnitResourceImpl
	extends BaseMeasurementUnitResourceImpl {

	@Override
	public void deleteMeasurementUnit(Long id) throws Exception {
		_findById(id);

		_cpMeasurementUnitService.deleteCPMeasurementUnit(id);
	}

	@Override
	public void deleteMeasurementUnitByExternalReferenceCode(
			String externalReferenceCode)
		throws Exception {

		CPMeasurementUnit cpMeasurementUnit = _findByExternalReferenceCode(
			externalReferenceCode);

		_cpMeasurementUnitService.deleteCPMeasurementUnit(
			cpMeasurementUnit.getCPMeasurementUnitId());
	}

	@Override
	public void deleteMeasurementUnitByKey(String key) throws Exception {
		CPMeasurementUnit cpMeasurementUnit = _findByKey(key);

		_cpMeasurementUnitService.deleteCPMeasurementUnit(
			cpMeasurementUnit.getCPMeasurementUnitId());
	}

	@Override
	public MeasurementUnit getMeasurementUnit(Long id) throws Exception {
		return _toMeasurementUnit(_findById(id));
	}

	@Override
	public MeasurementUnit getMeasurementUnitByExternalReferenceCode(
			String externalReferenceCode)
		throws Exception {

		return _toMeasurementUnit(
			_findByExternalReferenceCode(externalReferenceCode));
	}

	@Override
	public MeasurementUnit getMeasurementUnitByKey(String key)
		throws Exception {

		return _toMeasurementUnit(_findByKey(key));
	}

	@Override
	public Page<MeasurementUnit> getMeasurementUnitsByType(
			String measurementUnitType, Pagination pagination, Sort[] sorts)
		throws Exception {

		try {
			int type = _getType(measurementUnitType);

			return Page.of(
				transform(
					_cpMeasurementUnitService.getCPMeasurementUnitsByType(
						contextCompany.getCompanyId(), type,
						pagination.getStartPosition(),
						pagination.getEndPosition(), null),
					this::_toMeasurementUnit),
				pagination,
				_cpMeasurementUnitService.getCPMeasurementUnitsCount(
					contextCompany.getCompanyId(), type));
		}
		catch (CPMeasurementUnitTypeException cpMeasurementUnitTypeException) {
			if (_log.isDebugEnabled()) {
				_log.debug(cpMeasurementUnitTypeException);
			}

			return Page.of(Collections.emptyList());
		}
	}

	@Override
	public Page<MeasurementUnit> getMeasurementUnitsPage(
			Filter filter, Pagination pagination, Sort[] sorts)
		throws Exception {

		return Page.of(
			transform(
				_cpMeasurementUnitService.getCPMeasurementUnits(
					contextCompany.getCompanyId(),
					pagination.getStartPosition(), pagination.getEndPosition(),
					null),
				this::_toMeasurementUnit),
			pagination,
			_cpMeasurementUnitService.getCPMeasurementUnitsCount(
				contextCompany.getCompanyId()));
	}

	@Override
	public Response patchMeasurementUnit(
			Long id, MeasurementUnit measurementUnit)
		throws Exception {

		_updateMeasurementUnit(measurementUnit, _findById(id));

		Response.ResponseBuilder responseBuilder = Response.ok();

		return responseBuilder.build();
	}

	@Override
	public Response patchMeasurementUnitByExternalReferenceCode(
			String externalReferenceCode, MeasurementUnit measurementUnit)
		throws Exception {

		_updateMeasurementUnit(
			measurementUnit,
			_findByExternalReferenceCode(externalReferenceCode));

		Response.ResponseBuilder responseBuilder = Response.ok();

		return responseBuilder.build();
	}

	@Override
	public Response patchMeasurementUnitByKey(
			String key, MeasurementUnit measurementUnit)
		throws Exception {

		_updateMeasurementUnit(measurementUnit, _findByKey(key));

		Response.ResponseBuilder responseBuilder = Response.ok();

		return responseBuilder.build();
	}

	@Override
	public MeasurementUnit postMeasurementUnit(MeasurementUnit measurementUnit)
		throws Exception {

		try {
			return _toMeasurementUnit(
				_cpMeasurementUnitService.addCPMeasurementUnit(
					measurementUnit.getExternalReferenceCode(),
					LanguageUtils.getLocalizedMap(measurementUnit.getName()),
					measurementUnit.getKey(), measurementUnit.getRate(),
					_getPrimary(
						measurementUnit.getPrimary(),
						_getType(measurementUnit.getType())),
					measurementUnit.getPriority(),
					_getType(measurementUnit.getType()),
					_serviceContextHelper.getServiceContext(contextUser)));
		}
		catch (Exception exception) {
			if (exception instanceof
					DuplicateCPMeasurementUnitExternalReferenceCodeException) {

				throw new DuplicateCPMeasurementUnitExternalReferenceCodeException(
					"There is another measurement unit with external " +
						"reference code " +
							measurementUnit.getExternalReferenceCode());
			}

			if (exception instanceof DuplicateCPMeasurementUnitKeyException) {
				throw new DuplicateCPMeasurementUnitKeyException(
					"There is another measurement unit with key " +
						measurementUnit.getKey());
			}

			throw new Exception(exception.getMessage(), exception.getCause());
		}
	}

	@Override
	public MeasurementUnit putMeasurementUnitByExternalReferenceCode(
			String externalReferenceCode, MeasurementUnit measurementUnit)
		throws Exception {

		CPMeasurementUnit cpMeasurementUnit =
			_cpMeasurementUnitService.
				fetchCPMeasurementUnitByExternalReferenceCode(
					contextCompany.getCompanyId(), externalReferenceCode);

		if (cpMeasurementUnit == null) {
			return postMeasurementUnit(measurementUnit);
		}

		return _toMeasurementUnit(
			_cpMeasurementUnitService.updateCPMeasurementUnit(
				GetterUtil.getString(
					measurementUnit.getExternalReferenceCode()),
				cpMeasurementUnit.getCPMeasurementUnitId(),
				LanguageUtils.getLocalizedMap(measurementUnit.getName()),
				GetterUtil.getString(measurementUnit.getKey()),
				GetterUtil.getDouble(measurementUnit.getRate()),
				GetterUtil.getBoolean(measurementUnit.getPrimary()),
				GetterUtil.getDouble(measurementUnit.getPriority()),
				_getType(measurementUnit.getType()),
				_serviceContextHelper.getServiceContext(contextUser)));
	}

	private CPMeasurementUnit _findByExternalReferenceCode(
			String externalReferenceCode)
		throws Exception {

		CPMeasurementUnit cpMeasurementUnit =
			_cpMeasurementUnitService.
				fetchCPMeasurementUnitByExternalReferenceCode(
					contextCompany.getCompanyId(), externalReferenceCode);

		if (cpMeasurementUnit == null) {
			throw new NoSuchCPMeasurementUnitException(
				"Unable to find measurement unit with external reference " +
					"code " + externalReferenceCode);
		}

		return cpMeasurementUnit;
	}

	private CPMeasurementUnit _findById(Long id) throws Exception {
		CPMeasurementUnit cpMeasurementUnit =
			_cpMeasurementUnitService.fetchCPMeasurementUnit(id);

		if (cpMeasurementUnit == null) {
			throw new NoSuchCPMeasurementUnitException(
				"Unable to find measurement unit with id " + id);
		}

		return cpMeasurementUnit;
	}

	private CPMeasurementUnit _findByKey(String key) throws Exception {
		CPMeasurementUnit cpMeasurementUnit =
			_cpMeasurementUnitService.fetchCPMeasurementUnitByKey(
				contextCompany.getCompanyId(), key);

		if (cpMeasurementUnit == null) {
			throw new NoSuchCPMeasurementUnitException(
				"Unable to find measurement unit with key " + key);
		}

		return cpMeasurementUnit;
	}

	private Boolean _getPrimary(Boolean primary, int type) throws Exception {
		CPMeasurementUnit cpMeasurementUnit =
			_cpMeasurementUnitService.fetchPrimaryCPMeasurementUnitByType(
				contextCompany.getCompanyId(), type);

		if (cpMeasurementUnit == null) {
			return Boolean.TRUE;
		}

		if (primary == null) {
			return Boolean.FALSE;
		}

		return primary;
	}

	private int _getType(String measurementUnitType) throws Exception {
		if (Validator.isNumber(measurementUnitType) &&
			CPMeasurementUnitConstants.typesMap.containsKey(
				GetterUtil.getInteger(measurementUnitType))) {

			return GetterUtil.getInteger(measurementUnitType);
		}

		if (!CPMeasurementUnitConstants.typesMap.containsValue(
				measurementUnitType)) {

			throw new CPMeasurementUnitTypeException(
				"Unable to find measurement unit with type " +
					measurementUnitType);
		}

		for (Map.Entry<Integer, String> entry :
				CPMeasurementUnitConstants.typesMap.entrySet()) {

			if (StringUtil.equalsIgnoreCase(
					measurementUnitType, entry.getValue())) {

				return entry.getKey();
			}
		}

		return -1;
	}

	private MeasurementUnit _toMeasurementUnit(
			CPMeasurementUnit cpMeasurementUnit)
		throws Exception {

		return _measurementUnitDTOConverter.toDTO(
			new DefaultDTOConverterContext(
				contextAcceptLanguage.isAcceptAllLanguages(),
				HashMapBuilder.put(
					"delete",
					addAction(
						ActionKeys.DELETE, cpMeasurementUnit,
						"deleteMeasurementUnit")
				).put(
					"get",
					addAction(
						ActionKeys.VIEW, cpMeasurementUnit,
						"getMeasurementUnit")
				).put(
					"update",
					addAction(
						ActionKeys.UPDATE, cpMeasurementUnit,
						"patchMeasurementUnit")
				).build(),
				_dtoConverterRegistry,
				cpMeasurementUnit.getCPMeasurementUnitId(),
				contextAcceptLanguage.getPreferredLocale(), contextUriInfo,
				contextUser));
	}

	private void _updateMeasurementUnit(
			MeasurementUnit measurementUnit,
			CPMeasurementUnit cpMeasurementUnit)
		throws Exception {

		_cpMeasurementUnitService.updateCPMeasurementUnit(
			GetterUtil.get(
				measurementUnit.getExternalReferenceCode(),
				cpMeasurementUnit.getExternalReferenceCode()),
			cpMeasurementUnit.getCPMeasurementUnitId(),
			LanguageUtils.getLocalizedMap(measurementUnit.getName()),
			GetterUtil.get(
				measurementUnit.getKey(), cpMeasurementUnit.getKey()),
			GetterUtil.get(
				measurementUnit.getRate(), cpMeasurementUnit.getRate()),
			GetterUtil.get(
				measurementUnit.getPrimary(), cpMeasurementUnit.isPrimary()),
			GetterUtil.get(
				measurementUnit.getPriority(), cpMeasurementUnit.getPriority()),
			_getType(measurementUnit.getType()),
			_serviceContextHelper.getServiceContext(contextUser));
	}

	private static final Log _log = LogFactoryUtil.getLog(
		MeasurementUnitResourceImpl.class);

	@Reference
	private CPMeasurementUnitService _cpMeasurementUnitService;

	@Reference
	private DTOConverterRegistry _dtoConverterRegistry;

	@Reference(
		target = "(component.name=com.liferay.headless.commerce.admin.site.setting.internal.dto.v1_0.converter.MeasurementUnitDTOConverter)"
	)
	private DTOConverter<CPMeasurementUnit, MeasurementUnit>
		_measurementUnitDTOConverter;

	@Reference
	private ServiceContextHelper _serviceContextHelper;

}