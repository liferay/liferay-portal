/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.admin.catalog.internal.resource.v1_0;

import com.liferay.commerce.product.exception.NoSuchCPOptionException;
import com.liferay.commerce.product.model.CPOption;
import com.liferay.commerce.product.service.CPOptionService;
import com.liferay.headless.commerce.admin.catalog.dto.v1_0.Option;
import com.liferay.headless.commerce.admin.catalog.dto.v1_0.OptionValue;
import com.liferay.headless.commerce.admin.catalog.internal.odata.entity.v1_0.OptionEntityModel;
import com.liferay.headless.commerce.admin.catalog.resource.v1_0.OptionResource;
import com.liferay.headless.commerce.admin.catalog.resource.v1_0.OptionValueResource;
import com.liferay.headless.commerce.core.util.LanguageUtils;
import com.liferay.headless.commerce.core.util.ServiceContextHelper;
import com.liferay.portal.kernel.change.tracking.CTAware;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.search.filter.Filter;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.odata.entity.EntityModel;
import com.liferay.portal.vulcan.custom.field.CustomFieldsUtil;
import com.liferay.portal.vulcan.dto.converter.DTOConverter;
import com.liferay.portal.vulcan.dto.converter.DTOConverterRegistry;
import com.liferay.portal.vulcan.dto.converter.DefaultDTOConverterContext;
import com.liferay.portal.vulcan.pagination.Page;
import com.liferay.portal.vulcan.pagination.Pagination;
import com.liferay.portal.vulcan.util.SearchUtil;

import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.core.Response;

import java.io.Serializable;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ServiceScope;

/**
 * @author Alessio Antonio Rendina
 */
@Component(
	properties = "OSGI-INF/liferay/rest/v1_0/option.properties",
	scope = ServiceScope.PROTOTYPE, service = OptionResource.class
)
@CTAware
public class OptionResourceImpl extends BaseOptionResourceImpl {

	@Override
	public Response deleteOption(Long id) throws Exception {
		CPOption cpOption = _cpOptionService.getCPOption(id);

		_cpOptionService.deleteCPOption(cpOption.getCPOptionId());

		Response.ResponseBuilder responseBuilder = Response.ok();

		return responseBuilder.build();
	}

	@Override
	public Response deleteOptionByExternalReferenceCode(
			String externalReferenceCode)
		throws Exception {

		CPOption cpOption =
			_cpOptionService.fetchCPOptionByExternalReferenceCode(
				externalReferenceCode, contextCompany.getCompanyId());

		if (cpOption == null) {
			throw new NoSuchCPOptionException(
				"Unable to find option with external reference code " +
					externalReferenceCode);
		}

		_cpOptionService.deleteCPOption(cpOption.getCPOptionId());

		Response.ResponseBuilder responseBuilder = Response.ok();

		return responseBuilder.build();
	}

	@Override
	public EntityModel getEntityModel(MultivaluedMap multivaluedMap)
		throws Exception {

		return _entityModel;
	}

	@Override
	public Option getOption(Long id) throws Exception {
		return _toOption(GetterUtil.getLong(id));
	}

	@Override
	public Option getOptionByExternalReferenceCode(String externalReferenceCode)
		throws Exception {

		CPOption cpOption =
			_cpOptionService.fetchCPOptionByExternalReferenceCode(
				externalReferenceCode, contextCompany.getCompanyId());

		return _toOption(cpOption.getCPOptionId());
	}

	@Override
	public Page<Option> getOptionsPage(
			String search, Filter filter, Pagination pagination, Sort[] sorts)
		throws Exception {

		return SearchUtil.search(
			Collections.emptyMap(),
			booleanQuery -> booleanQuery.getPreBooleanFilter(), filter,
			CPOption.class.getName(), search, pagination,
			queryConfig -> queryConfig.setSelectedFieldNames(
				Field.ENTRY_CLASS_PK),
			searchContext -> searchContext.setCompanyId(
				contextCompany.getCompanyId()),
			sorts,
			document -> _toOption(
				GetterUtil.getLong(document.get(Field.ENTRY_CLASS_PK))));
	}

	@Override
	public Response patchOption(Long id, Option option) throws Exception {
		_updateOption(_cpOptionService.getCPOption(id), option);

		Response.ResponseBuilder responseBuilder = Response.ok();

		return responseBuilder.build();
	}

	@Override
	public Response patchOptionByExternalReferenceCode(
			String externalReferenceCode, Option option)
		throws Exception {

		CPOption cpOption =
			_cpOptionService.fetchCPOptionByExternalReferenceCode(
				externalReferenceCode, contextCompany.getCompanyId());

		if (cpOption == null) {
			throw new NoSuchCPOptionException(
				"Unable to find option with external reference code " +
					externalReferenceCode);
		}

		_updateOption(cpOption, option);

		Response.ResponseBuilder responseBuilder = Response.noContent();

		return responseBuilder.build();
	}

	@Override
	public Option postOption(Option option) throws Exception {
		return _addOrUpdateOption(option.getExternalReferenceCode(), option);
	}

	@Override
	public Option putOptionByExternalReferenceCode(
			String externalReferenceCode, Option option)
		throws Exception {

		return _addOrUpdateOption(externalReferenceCode, option);
	}

	private Option _addOrUpdateOption(
			String externalReferenceCode, Option option)
		throws Exception {

		Option.FieldType fieldType = option.getFieldType();

		ServiceContext serviceContext =
			_serviceContextHelper.getServiceContext();

		serviceContext.setExpandoBridgeAttributes(
			_getExpandoBridgeAttributes(option));

		CPOption cpOption = _cpOptionService.addOrUpdateCPOption(
			externalReferenceCode,
			LanguageUtils.getLocalizedMap(option.getName()),
			LanguageUtils.getLocalizedMap(option.getDescription()),
			fieldType.getValue(), GetterUtil.get(option.getFacetable(), false),
			GetterUtil.get(option.getRequired(), false),
			GetterUtil.get(option.getSkuContributor(), false), option.getKey(),
			serviceContext);

		_addOrUpdateOptionValues(cpOption, option.getOptionValues());

		return _toOption(cpOption.getCPOptionId());
	}

	private void _addOrUpdateOptionValues(
			CPOption cpOption, OptionValue[] optionValues)
		throws Exception {

		if (ArrayUtil.isEmpty(optionValues)) {
			return;
		}

		_optionValueResource.setContextAcceptLanguage(contextAcceptLanguage);
		_optionValueResource.setContextCompany(contextCompany);
		_optionValueResource.setContextUriInfo(contextUriInfo);

		for (OptionValue optionValue : optionValues) {
			_optionValueResource.postOptionIdOptionValue(
				cpOption.getCPOptionId(), optionValue);
		}
	}

	private Map<String, Map<String, String>> _getActions(long cpOptionId) {
		return HashMapBuilder.<String, Map<String, String>>put(
			"delete",
			addAction(
				ActionKeys.DELETE, cpOptionId, "deleteOption",
				_cpOptionModelResourcePermission)
		).put(
			"get",
			addAction(
				ActionKeys.VIEW, cpOptionId, "getOption",
				_cpOptionModelResourcePermission)
		).put(
			"update",
			addAction(
				ActionKeys.UPDATE, cpOptionId, "patchOption",
				_cpOptionModelResourcePermission)
		).build();
	}

	private Map<String, Serializable> _getExpandoBridgeAttributes(
		Option option) {

		Map<String, Serializable> expandoBridgeAttributes =
			CustomFieldsUtil.toMap(
				CPOption.class.getName(), contextCompany.getCompanyId(),
				option.getCustomFields(),
				contextAcceptLanguage.getPreferredLocale());

		if (expandoBridgeAttributes == null) {
			expandoBridgeAttributes = new HashMap<>();
		}

		return expandoBridgeAttributes;
	}

	private Option _toOption(Long cpOptionId) throws Exception {
		return _optionDTOConverter.toDTO(
			new DefaultDTOConverterContext(
				contextAcceptLanguage.isAcceptAllLanguages(),
				_getActions(cpOptionId), _dtoConverterRegistry, cpOptionId,
				contextAcceptLanguage.getPreferredLocale(), contextUriInfo,
				contextUser));
	}

	private Option _updateOption(CPOption cpOption, Option option)
		throws Exception {

		Option.FieldType fieldType = option.getFieldType();

		ServiceContext serviceContext =
			_serviceContextHelper.getServiceContext();

		serviceContext.setExpandoBridgeAttributes(
			_getExpandoBridgeAttributes(option));

		cpOption = _cpOptionService.updateCPOption(
			cpOption.getCPOptionId(),
			LanguageUtils.getLocalizedMap(option.getName()),
			LanguageUtils.getLocalizedMap(option.getDescription()),
			fieldType.getValue(),
			GetterUtil.get(option.getFacetable(), cpOption.isFacetable()),
			GetterUtil.get(option.getRequired(), cpOption.isRequired()),
			GetterUtil.get(
				option.getSkuContributor(), cpOption.isSkuContributor()),
			option.getKey(), serviceContext);

		return _toOption(cpOption.getCPOptionId());
	}

	private static final EntityModel _entityModel = new OptionEntityModel();

	@Reference(
		target = "(model.class.name=com.liferay.commerce.product.model.CPOption)"
	)
	private ModelResourcePermission<CPOption> _cpOptionModelResourcePermission;

	@Reference
	private CPOptionService _cpOptionService;

	@Reference
	private DTOConverterRegistry _dtoConverterRegistry;

	@Reference(
		target = "(component.name=com.liferay.headless.commerce.admin.catalog.internal.dto.v1_0.converter.OptionDTOConverter)"
	)
	private DTOConverter<CPOption, Option> _optionDTOConverter;

	@Reference
	private OptionValueResource _optionValueResource;

	@Reference
	private ServiceContextHelper _serviceContextHelper;

}