/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.admin.catalog.internal.resource.v1_0;

import com.liferay.commerce.product.exception.NoSuchCPOptionException;
import com.liferay.commerce.product.exception.NoSuchCPOptionValueException;
import com.liferay.commerce.product.model.CPOption;
import com.liferay.commerce.product.model.CPOptionValue;
import com.liferay.commerce.product.service.CPOptionService;
import com.liferay.commerce.product.service.CPOptionValueService;
import com.liferay.headless.commerce.admin.catalog.dto.v1_0.Option;
import com.liferay.headless.commerce.admin.catalog.dto.v1_0.OptionValue;
import com.liferay.headless.commerce.admin.catalog.resource.v1_0.OptionValueResource;
import com.liferay.headless.commerce.core.util.ActionUtil;
import com.liferay.headless.commerce.core.util.LanguageUtils;
import com.liferay.headless.commerce.core.util.ServiceContextHelper;
import com.liferay.portal.kernel.change.tracking.CTAware;
import com.liferay.portal.kernel.search.BaseModelSearchResult;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.vulcan.custom.field.CustomFieldsUtil;
import com.liferay.portal.vulcan.dto.converter.DTOConverter;
import com.liferay.portal.vulcan.dto.converter.DTOConverterRegistry;
import com.liferay.portal.vulcan.dto.converter.DefaultDTOConverterContext;
import com.liferay.portal.vulcan.fields.NestedField;
import com.liferay.portal.vulcan.pagination.Page;
import com.liferay.portal.vulcan.pagination.Pagination;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriBuilder;
import jakarta.ws.rs.core.UriInfo;

import java.io.Serializable;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ServiceScope;

/**
 * @author Alessio Antonio Rendina
 */
@Component(
	properties = "OSGI-INF/liferay/rest/v1_0/option-value.properties",
	property = "nested.field.support=true", scope = ServiceScope.PROTOTYPE,
	service = OptionValueResource.class
)
@CTAware
public class OptionValueResourceImpl extends BaseOptionValueResourceImpl {

	@Override
	public Response deleteOptionValue(Long id) throws Exception {
		_cpOptionValueService.deleteCPOptionValue(id);

		Response.ResponseBuilder responseBuilder = Response.ok();

		return responseBuilder.build();
	}

	@Override
	public Response deleteOptionValueByExternalReferenceCode(
			String externalReferenceCode)
		throws Exception {

		CPOptionValue cpOptionValue =
			_cpOptionValueService.fetchCPOptionValueByExternalReferenceCode(
				externalReferenceCode, contextCompany.getCompanyId());

		if (cpOptionValue == null) {
			throw new NoSuchCPOptionValueException(
				"Unable to find option value with external reference code " +
					externalReferenceCode);
		}

		_cpOptionValueService.deleteCPOptionValue(
			cpOptionValue.getCPOptionValueId());

		Response.ResponseBuilder responseBuilder = Response.ok();

		return responseBuilder.build();
	}

	@Override
	public Page<OptionValue> getOptionByExternalReferenceCodeOptionValuesPage(
			String externalReferenceCode, String search, Pagination pagination,
			Sort[] sorts)
		throws Exception {

		CPOption cpOption =
			_cpOptionService.fetchCPOptionByExternalReferenceCode(
				externalReferenceCode, contextCompany.getCompanyId());

		if (cpOption == null) {
			throw new NoSuchCPOptionException(
				"Unable to find option with external reference code " +
					externalReferenceCode);
		}

		BaseModelSearchResult<CPOptionValue>
			cpOptionValueBaseModelSearchResult =
				_cpOptionValueService.searchCPOptionValues(
					cpOption.getCompanyId(), cpOption.getCPOptionId(), search,
					pagination.getStartPosition(), pagination.getEndPosition(),
					sorts);

		int totalCount = _cpOptionValueService.searchCPOptionValuesCount(
			cpOption.getCompanyId(), cpOption.getCPOptionId(), search);

		return Page.of(
			_toOptionValues(cpOptionValueBaseModelSearchResult.getBaseModels()),
			pagination, totalCount);
	}

	@NestedField(parentClass = Option.class, value = "optionValues")
	@Override
	public Page<OptionValue> getOptionIdOptionValuesPage(
			Long id, String search, Pagination pagination, Sort[] sorts)
		throws Exception {

		CPOption cpOption = _cpOptionService.getCPOption(id);

		BaseModelSearchResult<CPOptionValue>
			cpOptionValueBaseModelSearchResult =
				_cpOptionValueService.searchCPOptionValues(
					cpOption.getCompanyId(), cpOption.getCPOptionId(), search,
					pagination.getStartPosition(), pagination.getEndPosition(),
					sorts);

		int totalCount = _cpOptionValueService.searchCPOptionValuesCount(
			cpOption.getCompanyId(), cpOption.getCPOptionId(), search);

		return Page.of(
			_toOptionValues(cpOptionValueBaseModelSearchResult.getBaseModels()),
			pagination, totalCount);
	}

	@Override
	public OptionValue getOptionValue(Long id) throws Exception {
		return _toOptionValue(id);
	}

	@Override
	public OptionValue getOptionValueByExternalReferenceCode(
			String externalReferenceCode)
		throws Exception {

		CPOptionValue cpOptionValue =
			_cpOptionValueService.fetchCPOptionValueByExternalReferenceCode(
				externalReferenceCode, contextCompany.getCompanyId());

		if (cpOptionValue == null) {
			throw new NoSuchCPOptionValueException(
				"Unable to find option value with external reference code " +
					externalReferenceCode);
		}

		return _toOptionValue(cpOptionValue.getCPOptionValueId());
	}

	@Override
	public Response patchOptionValue(Long id, OptionValue optionValue)
		throws Exception {

		_updateOptionValue(
			_cpOptionValueService.getCPOptionValue(id), optionValue);

		Response.ResponseBuilder responseBuilder = Response.ok();

		return responseBuilder.build();
	}

	@Override
	public Response patchOptionValueByExternalReferenceCode(
			String externalReferenceCode, OptionValue optionValue)
		throws Exception {

		CPOptionValue cpOptionValue =
			_cpOptionValueService.fetchCPOptionValueByExternalReferenceCode(
				externalReferenceCode, contextCompany.getCompanyId());

		if (cpOptionValue == null) {
			throw new NoSuchCPOptionValueException(
				"Unable to find option value with external reference code " +
					externalReferenceCode);
		}

		_updateOptionValue(cpOptionValue, optionValue);

		Response.ResponseBuilder responseBuilder = Response.ok();

		return responseBuilder.build();
	}

	@Override
	public OptionValue postOptionByExternalReferenceCodeOptionValue(
			String externalReferenceCode, OptionValue optionValue)
		throws Exception {

		CPOption cpOption =
			_cpOptionService.fetchCPOptionByExternalReferenceCode(
				externalReferenceCode, contextCompany.getCompanyId());

		if (cpOption == null) {
			throw new NoSuchCPOptionException(
				"Unable to find option with external reference code " +
					externalReferenceCode);
		}

		return _addOrUpdateOptionValue(cpOption, optionValue);
	}

	@Override
	public OptionValue postOptionIdOptionValue(Long id, OptionValue optionValue)
		throws Exception {

		return _addOrUpdateOptionValue(
			_cpOptionService.getCPOption(id), optionValue);
	}

	private Map<String, String> _addAction(
			String actionId, long cpOptionValueId, UriInfo uriInfo,
			String methodName, Class<?> clazz)
		throws Exception {

		CPOptionValue cpOptionValue = _cpOptionValueService.getCPOptionValue(
			cpOptionValueId);

		if (!_cpOptionValueModelResourcePermission.contains(
				PermissionThreadLocal.getPermissionChecker(),
				cpOptionValue.getCPOptionId(), actionId)) {

			return null;
		}

		return HashMapBuilder.put(
			"href",
			() -> {
				UriBuilder uriBuilder = uriInfo.getBaseUriBuilder();

				return uriBuilder.path(
					ActionUtil.getVersion(uriInfo)
				).path(
					clazz.getSuperclass(), methodName
				).toTemplate();
			}
		).put(
			"method",
			ActionUtil.getHttpMethodName(
				clazz, ActionUtil.getMethod(clazz, methodName))
		).build();
	}

	private OptionValue _addOrUpdateOptionValue(
			CPOption cpOption, OptionValue optionValue)
		throws Exception {

		ServiceContext serviceContext =
			_serviceContextHelper.getServiceContext();

		serviceContext.setExpandoBridgeAttributes(
			_getExpandoBridgeAttributes(optionValue));

		CPOptionValue cpOptionValue =
			_cpOptionValueService.addOrUpdateCPOptionValue(
				optionValue.getExternalReferenceCode(),
				cpOption.getCPOptionId(),
				LanguageUtils.getLocalizedMap(optionValue.getName()),
				GetterUtil.get(optionValue.getPriority(), 0D),
				optionValue.getKey(), serviceContext);

		return _toOptionValue(cpOptionValue.getCPOptionValueId());
	}

	private Map<String, Map<String, String>> _getActions(long cpOptionValueId)
		throws Exception {

		return HashMapBuilder.<String, Map<String, String>>put(
			"delete",
			_addAction(
				ActionKeys.DELETE, cpOptionValueId, contextUriInfo,
				"deleteOptionValue", getClass())
		).put(
			"get",
			_addAction(
				ActionKeys.VIEW, cpOptionValueId, contextUriInfo,
				"getOptionValue", getClass())
		).put(
			"update",
			_addAction(
				ActionKeys.UPDATE, cpOptionValueId, contextUriInfo,
				"patchOptionValue", getClass())
		).build();
	}

	private Map<String, Serializable> _getExpandoBridgeAttributes(
		OptionValue optionValue) {

		Map<String, Serializable> expandoBridgeAttributes =
			CustomFieldsUtil.toMap(
				CPOptionValue.class.getName(), contextCompany.getCompanyId(),
				optionValue.getCustomFields(),
				contextAcceptLanguage.getPreferredLocale());

		if (expandoBridgeAttributes == null) {
			expandoBridgeAttributes = new HashMap<>();
		}

		return expandoBridgeAttributes;
	}

	private OptionValue _toOptionValue(Long cpOptionValueId) throws Exception {
		return _optionValueDTOConverter.toDTO(
			new DefaultDTOConverterContext(
				contextAcceptLanguage.isAcceptAllLanguages(),
				_getActions(cpOptionValueId), _dtoConverterRegistry,
				cpOptionValueId, contextAcceptLanguage.getPreferredLocale(),
				contextUriInfo, contextUser));
	}

	private List<OptionValue> _toOptionValues(
			List<CPOptionValue> cpOptionValues)
		throws Exception {

		return transform(
			cpOptionValues,
			cpOptionValue -> _toOptionValue(
				cpOptionValue.getCPOptionValueId()));
	}

	private OptionValue _updateOptionValue(
			CPOptionValue cpOptionValue, OptionValue optionValue)
		throws Exception {

		Map<String, String> name = optionValue.getName();
		Map<Locale, String> nameMap = null;

		if (MapUtil.isEmpty(name)) {
			nameMap = cpOptionValue.getNameMap();
		}
		else {
			nameMap = LanguageUtils.getLocalizedMap(name);
		}

		ServiceContext serviceContext =
			_serviceContextHelper.getServiceContext();

		serviceContext.setExpandoBridgeAttributes(
			_getExpandoBridgeAttributes(optionValue));

		cpOptionValue = _cpOptionValueService.updateCPOptionValue(
			cpOptionValue.getCPOptionValueId(), nameMap,
			GetterUtil.get(
				optionValue.getPriority(), cpOptionValue.getPriority()),
			GetterUtil.get(optionValue.getKey(), cpOptionValue.getKey()),
			serviceContext);

		return _toOptionValue(cpOptionValue.getCPOptionValueId());
	}

	@Reference
	private CPOptionService _cpOptionService;

	@Reference(
		target = "(model.class.name=com.liferay.commerce.product.model.CPOption)"
	)
	private ModelResourcePermission<CPOption>
		_cpOptionValueModelResourcePermission;

	@Reference
	private CPOptionValueService _cpOptionValueService;

	@Reference
	private DTOConverterRegistry _dtoConverterRegistry;

	@Reference(
		target = "(component.name=com.liferay.headless.commerce.admin.catalog.internal.dto.v1_0.converter.OptionValueDTOConverter)"
	)
	private DTOConverter<CPOptionValue, OptionValue> _optionValueDTOConverter;

	@Reference
	private ServiceContextHelper _serviceContextHelper;

}