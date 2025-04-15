/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.admin.catalog.internal.resource.v1_0;

import com.liferay.commerce.product.exception.NoSuchCPSpecificationOptionException;
import com.liferay.commerce.product.model.CPSpecificationOption;
import com.liferay.commerce.product.service.CPSpecificationOptionService;
import com.liferay.headless.commerce.admin.catalog.dto.v1_0.OptionCategory;
import com.liferay.headless.commerce.admin.catalog.dto.v1_0.Specification;
import com.liferay.headless.commerce.admin.catalog.internal.odata.entity.v1_0.SpecificationEntityModel;
import com.liferay.headless.commerce.admin.catalog.resource.v1_0.SpecificationResource;
import com.liferay.headless.commerce.core.util.LanguageUtils;
import com.liferay.headless.commerce.core.util.ServiceContextHelper;
import com.liferay.list.type.model.ListTypeDefinition;
import com.liferay.portal.kernel.change.tracking.CTAware;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.search.filter.Filter;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.odata.entity.EntityModel;
import com.liferay.portal.vulcan.dto.converter.DTOConverter;
import com.liferay.portal.vulcan.dto.converter.DefaultDTOConverterContext;
import com.liferay.portal.vulcan.pagination.Page;
import com.liferay.portal.vulcan.pagination.Pagination;
import com.liferay.portal.vulcan.util.SearchUtil;

import jakarta.ws.rs.core.MultivaluedMap;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ServiceScope;

/**
 * @author Zoltán Takács
 * @author Alessio Antonio Rendina
 */
@Component(
	properties = "OSGI-INF/liferay/rest/v1_0/specification.properties",
	scope = ServiceScope.PROTOTYPE, service = SpecificationResource.class
)
@CTAware
public class SpecificationResourceImpl extends BaseSpecificationResourceImpl {

	@Override
	public void deleteSpecification(Long id) throws Exception {
		_cpSpecificationOptionService.deleteCPSpecificationOption(id);
	}

	@Override
	public void deleteSpecificationByExternalReferenceCode(
			String externalReferenceCode)
		throws Exception {

		CPSpecificationOption cpSpecificationOption =
			_cpSpecificationOptionService.
				fetchCPSpecificationOptionByExternalReferenceCode(
					externalReferenceCode, contextCompany.getCompanyId());

		if (cpSpecificationOption == null) {
			throw new NoSuchCPSpecificationOptionException();
		}

		deleteSpecification(cpSpecificationOption.getCPSpecificationOptionId());
	}

	@Override
	public EntityModel getEntityModel(MultivaluedMap multivaluedMap)
		throws Exception {

		return _entityModel;
	}

	@Override
	public Specification getSpecification(Long id) throws Exception {
		return _toSpecification(GetterUtil.getLong(id));
	}

	@Override
	public Specification getSpecificationByExternalReferenceCode(
			String externalReferenceCode)
		throws Exception {

		CPSpecificationOption cpSpecificationOption =
			_cpSpecificationOptionService.
				fetchCPSpecificationOptionByExternalReferenceCode(
					externalReferenceCode, contextCompany.getCompanyId());

		if (cpSpecificationOption == null) {
			throw new NoSuchCPSpecificationOptionException();
		}

		return getSpecification(
			cpSpecificationOption.getCPSpecificationOptionId());
	}

	@Override
	public Page<Specification> getSpecificationsPage(
			String search, Filter filter, Pagination pagination, Sort[] sorts)
		throws Exception {

		return SearchUtil.search(
			Collections.emptyMap(),
			booleanQuery -> booleanQuery.getPreBooleanFilter(), filter,
			CPSpecificationOption.class.getName(), search, pagination,
			queryConfig -> queryConfig.setSelectedFieldNames(
				Field.ENTRY_CLASS_PK),
			searchContext -> searchContext.setCompanyId(
				contextCompany.getCompanyId()),
			sorts,
			document -> _toSpecification(
				GetterUtil.getLong(document.get(Field.ENTRY_CLASS_PK))));
	}

	@Override
	public Specification patchSpecification(
			Long id, Specification specification)
		throws Exception {

		CPSpecificationOption cpSpecificationOption = _updateSpecification(
			_cpSpecificationOptionService.getCPSpecificationOption(id),
			specification);

		return _toSpecification(
			cpSpecificationOption.getCPSpecificationOptionId());
	}

	@Override
	public Specification patchSpecificationByExternalReferenceCode(
			String externalReferenceCode, Specification specification)
		throws Exception {

		CPSpecificationOption cpSpecificationOption =
			_cpSpecificationOptionService.
				fetchCPSpecificationOptionByExternalReferenceCode(
					externalReferenceCode, contextCompany.getCompanyId());

		if (cpSpecificationOption == null) {
			throw new NoSuchCPSpecificationOptionException();
		}

		return patchSpecification(
			cpSpecificationOption.getCPSpecificationOptionId(), specification);
	}

	@Override
	public Specification postSpecification(Specification specification)
		throws Exception {

		return _addOrUpdateSpecification(specification);
	}

	@Override
	public Specification putSpecificationByExternalReferenceCode(
			String externalReferenceCode, Specification specification)
		throws Exception {

		CPSpecificationOption cpSpecificationOption =
			_cpSpecificationOptionService.
				fetchCPSpecificationOptionByExternalReferenceCode(
					externalReferenceCode, contextCompany.getCompanyId());

		long[] listTypeDefinitionIds = GetterUtil.getLongValues(
			specification.getListTypeDefinitionIds(),
			new long[] {
				GetterUtil.getLong(specification.getListTypeDefinitionId())
			});

		if (cpSpecificationOption == null) {
			cpSpecificationOption =
				_cpSpecificationOptionService.addCPSpecificationOption(
					specification.getExternalReferenceCode(),
					_getCPOptionCategoryId(specification),
					listTypeDefinitionIds,
					LanguageUtils.getLocalizedMap(specification.getTitle()),
					LanguageUtils.getLocalizedMap(
						specification.getDescription()),
					GetterUtil.getBoolean(specification.getFacetable()),
					GetterUtil.getString(specification.getKey()),
					GetterUtil.getDouble(specification.getPriority()),
					GetterUtil.getBoolean(specification.getVisible(), true),
					_serviceContextHelper.getServiceContext());

			return _toSpecification(
				cpSpecificationOption.getCPSpecificationOptionId());
		}

		Map<String, String> descriptionMap = specification.getDescription();
		Map<String, String> titleMap = specification.getTitle();

		_cpSpecificationOptionService.updateCPSpecificationOption(
			GetterUtil.getString(specification.getExternalReferenceCode()),
			cpSpecificationOption.getCPSpecificationOptionId(),
			GetterUtil.getLong(_getCPOptionCategoryId(specification)),
			listTypeDefinitionIds, LanguageUtils.getLocalizedMap(titleMap),
			LanguageUtils.getLocalizedMap(descriptionMap),
			GetterUtil.getBoolean(specification.getFacetable()),
			GetterUtil.getString(specification.getKey()),
			GetterUtil.getDouble(specification.getPriority()),
			GetterUtil.getBoolean(
				specification.getVisible(), cpSpecificationOption.isVisible()),
			_serviceContextHelper.getServiceContext());

		return _toSpecification(
			cpSpecificationOption.getCPSpecificationOptionId());
	}

	private Specification _addOrUpdateSpecification(Specification specification)
		throws Exception {

		Long specificationId = specification.getId();

		if (specificationId != null) {
			try {
				CPSpecificationOption cpSpecificationOption =
					_updateSpecification(
						_cpSpecificationOptionService.getCPSpecificationOption(
							specificationId),
						specification);

				return _toSpecification(
					cpSpecificationOption.getCPSpecificationOptionId());
			}
			catch (NoSuchCPSpecificationOptionException
						noSuchCPSpecificationOptionException) {

				if (_log.isDebugEnabled()) {
					_log.debug(
						"Unable to find specification with ID: " +
							specificationId,
						noSuchCPSpecificationOptionException);
				}
			}
		}

		String specificationExternalReferenceCode =
			specification.getExternalReferenceCode();

		if (Validator.isNotNull(specificationExternalReferenceCode)) {
			try {
				CPSpecificationOption cpSpecificationOption =
					_cpSpecificationOptionService.
						fetchCPSpecificationOptionByExternalReferenceCode(
							specificationExternalReferenceCode,
							contextCompany.getCompanyId());

				if (cpSpecificationOption == null) {
					throw new NoSuchCPSpecificationOptionException();
				}

				cpSpecificationOption = _updateSpecification(
					cpSpecificationOption, specification);

				return _toSpecification(
					cpSpecificationOption.getCPSpecificationOptionId());
			}
			catch (NoSuchCPSpecificationOptionException
						noSuchCPSpecificationOptionException) {

				if (_log.isDebugEnabled()) {
					_log.debug(
						"Unable to find specification with external " +
							"reference code: " + specificationId,
						noSuchCPSpecificationOptionException);
				}
			}
		}

		String specificationKey = specification.getKey();

		if (Validator.isNotNull(specificationKey)) {
			try {
				CPSpecificationOption cpSpecificationOption =
					_updateSpecification(
						_cpSpecificationOptionService.getCPSpecificationOption(
							contextCompany.getCompanyId(), specificationKey),
						specification);

				return _toSpecification(
					cpSpecificationOption.getCPSpecificationOptionId());
			}
			catch (NoSuchCPSpecificationOptionException
						noSuchCPSpecificationOptionException) {

				if (_log.isDebugEnabled()) {
					_log.debug(
						"Unable to find specification with key: " +
							specificationKey,
						noSuchCPSpecificationOptionException);
				}
			}
		}

		CPSpecificationOption cpSpecificationOption =
			_cpSpecificationOptionService.addCPSpecificationOption(
				specification.getExternalReferenceCode(),
				_getCPOptionCategoryId(specification),
				GetterUtil.getLongValues(
					specification.getListTypeDefinitionIds(),
					new long[] {
						GetterUtil.getLong(
							specification.getListTypeDefinitionId())
					}),
				LanguageUtils.getLocalizedMap(specification.getTitle()),
				LanguageUtils.getLocalizedMap(specification.getDescription()),
				GetterUtil.getBoolean(specification.getFacetable()),
				specificationKey,
				GetterUtil.getDouble(specification.getPriority()), true,
				_serviceContextHelper.getServiceContext());

		return _toSpecification(
			cpSpecificationOption.getCPSpecificationOptionId());
	}

	private long _getCPOptionCategoryId(Specification specification) {
		OptionCategory optionCategory = specification.getOptionCategory();

		if (optionCategory == null) {
			return 0;
		}

		return optionCategory.getId();
	}

	private Specification _toSpecification(Long cpSpecificationOptionId)
		throws Exception {

		return _specificationDTOConverter.toDTO(
			new DefaultDTOConverterContext(
				cpSpecificationOptionId,
				contextAcceptLanguage.getPreferredLocale()));
	}

	private CPSpecificationOption _updateSpecification(
			CPSpecificationOption cpSpecificationOption,
			Specification specification)
		throws PortalException {

		Map<String, String> descriptionMap = specification.getDescription();

		if (descriptionMap == null) {
			descriptionMap = LanguageUtils.getLanguageIdMap(
				cpSpecificationOption.getDescriptionMap());
		}

		long[] listTypeDefinitionIds = GetterUtil.getLongValues(
			specification.getListTypeDefinitionIds(),
			transformToLongArray(
				cpSpecificationOption.getListTypeDefinitions(),
				ListTypeDefinition::getListTypeDefinitionId));

		if (specification.getListTypeDefinitionIds() == null) {
			long listTypeDefinitionId = GetterUtil.getLong(
				specification.getListTypeDefinitionId());

			List<ListTypeDefinition> listTypeDefinitions =
				cpSpecificationOption.getListTypeDefinitions();

			if (!listTypeDefinitions.isEmpty()) {
				ListTypeDefinition listTypeDefinition = listTypeDefinitions.get(
					0);

				listTypeDefinitionId = GetterUtil.getLong(
					specification.getListTypeDefinitionId(),
					listTypeDefinition.getListTypeDefinitionId());
			}

			listTypeDefinitionIds = new long[] {listTypeDefinitionId};
		}

		Map<String, String> titleMap = specification.getTitle();

		if (titleMap == null) {
			titleMap = LanguageUtils.getLanguageIdMap(
				cpSpecificationOption.getTitleMap());
		}

		return _cpSpecificationOptionService.updateCPSpecificationOption(
			GetterUtil.getString(
				specification.getExternalReferenceCode(),
				cpSpecificationOption.getExternalReferenceCode()),
			cpSpecificationOption.getCPSpecificationOptionId(),
			GetterUtil.getLong(
				cpSpecificationOption.getCPOptionCategoryId(),
				_getCPOptionCategoryId(specification)),
			listTypeDefinitionIds, LanguageUtils.getLocalizedMap(titleMap),
			LanguageUtils.getLocalizedMap(descriptionMap),
			GetterUtil.getBoolean(
				specification.getFacetable(),
				cpSpecificationOption.isFacetable()),
			GetterUtil.getString(
				specification.getKey(), cpSpecificationOption.getKey()),
			GetterUtil.getDouble(
				specification.getPriority(),
				cpSpecificationOption.getPriority()),
			GetterUtil.getBoolean(
				specification.getVisible(), cpSpecificationOption.isVisible()),
			_serviceContextHelper.getServiceContext());
	}

	private static final Log _log = LogFactoryUtil.getLog(
		SpecificationResourceImpl.class);

	private static final EntityModel _entityModel =
		new SpecificationEntityModel();

	@Reference
	private CPSpecificationOptionService _cpSpecificationOptionService;

	@Reference
	private ServiceContextHelper _serviceContextHelper;

	@Reference(
		target = "(component.name=com.liferay.headless.commerce.admin.catalog.internal.dto.v1_0.converter.SpecificationDTOConverter)"
	)
	private DTOConverter<CPSpecificationOption, Specification>
		_specificationDTOConverter;

}