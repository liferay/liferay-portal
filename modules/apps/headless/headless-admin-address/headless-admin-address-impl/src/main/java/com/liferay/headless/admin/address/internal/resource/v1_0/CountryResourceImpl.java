/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.address.internal.resource.v1_0;

import com.liferay.exportimport.kernel.lar.PortletDataContext;
import com.liferay.exportimport.vulcan.batch.engine.ExportImportVulcanBatchEngineTaskItemDelegate;
import com.liferay.headless.admin.address.dto.v1_0.Country;
import com.liferay.headless.admin.address.dto.v1_0.Region;
import com.liferay.headless.admin.address.internal.odata.entity.v1_0.CountryEntityModel;
import com.liferay.headless.admin.address.resource.v1_0.CountryResource;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.model.BaseModel;
import com.liferay.portal.kernel.search.BooleanClauseOccur;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.search.filter.BooleanFilter;
import com.liferay.portal.kernel.search.filter.Filter;
import com.liferay.portal.kernel.search.filter.TermFilter;
import com.liferay.portal.kernel.service.CountryLocalService;
import com.liferay.portal.kernel.service.CountryService;
import com.liferay.portal.kernel.service.RegionLocalService;
import com.liferay.portal.kernel.service.RegionService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextFactory;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.odata.entity.EntityModel;
import com.liferay.portal.vulcan.dto.converter.DTOConverter;
import com.liferay.portal.vulcan.pagination.Page;
import com.liferay.portal.vulcan.pagination.Pagination;
import com.liferay.portal.vulcan.util.SearchUtil;

import jakarta.ws.rs.core.MultivaluedMap;

import java.io.Serializable;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ServiceScope;

/**
 * @author Javier Gamarra
 * @author Drew Brokke
 */
@Component(
	properties = "OSGI-INF/liferay/rest/v1_0/country.properties",
	property = "export.import.vulcan.batch.engine.task.item.delegate=true",
	scope = ServiceScope.PROTOTYPE, service = CountryResource.class
)
public class CountryResourceImpl
	extends BaseCountryResourceImpl
	implements ExportImportVulcanBatchEngineTaskItemDelegate<Country> {

	@Override
	public void deleteCountry(Long countryId) throws Exception {
		_countryService.deleteCountry(countryId);
	}

	@Override
	public void deleteCountryByExternalReferenceCode(
			String externalReferenceCode)
		throws Exception {

		com.liferay.portal.kernel.model.Country serviceBuilderCountry =
			_countryService.getCountryByExternalReferenceCode(
				externalReferenceCode, contextCompany.getCompanyId());

		_countryService.deleteCountry(serviceBuilderCountry.getCountryId());
	}

	@Override
	public Page<Country> getCountriesPage(
			Boolean active, String search, Filter filter, Pagination pagination,
			Sort[] sorts)
		throws Exception {

		return SearchUtil.search(
			Collections.emptyMap(),
			booleanQuery -> {
				if (active != null) {
					BooleanFilter booleanFilter =
						booleanQuery.getPreBooleanFilter();

					booleanFilter.add(
						new TermFilter("active", String.valueOf(active)),
						BooleanClauseOccur.MUST);
				}
			},
			filter, com.liferay.portal.kernel.model.Country.class.getName(),
			search, pagination,
			queryConfig -> queryConfig.setSelectedFieldNames(
				Field.ENTRY_CLASS_PK),
			searchContext -> searchContext.setCompanyId(
				contextCompany.getCompanyId()),
			sorts,
			document -> {
				com.liferay.portal.kernel.model.Country serviceBuilderCountry =
					_countryLocalService.fetchCountry(
						GetterUtil.getLong(document.get(Field.ENTRY_CLASS_PK)));

				if (serviceBuilderCountry == null) {
					return null;
				}

				return _toCountry(serviceBuilderCountry);
			});
	}

	@Override
	public Country getCountry(Long countryId) throws Exception {
		return _toCountry(_countryService.getCountry(countryId));
	}

	@Override
	public Country getCountryByA2(String a2) throws Exception {
		return _toCountry(
			_countryService.getCountryByA2(contextCompany.getCompanyId(), a2));
	}

	@Override
	public Country getCountryByA3(String a3) throws Exception {
		return _toCountry(
			_countryService.getCountryByA3(contextCompany.getCompanyId(), a3));
	}

	@Override
	public Country getCountryByExternalReferenceCode(
			String externalReferenceCode)
		throws Exception {

		return _toCountry(
			_countryService.getCountryByExternalReferenceCode(
				externalReferenceCode, contextCompany.getCompanyId()));
	}

	@Override
	public Country getCountryByName(String name) throws Exception {
		return _toCountry(
			_countryService.getCountryByName(
				contextCompany.getCompanyId(), name));
	}

	@Override
	public Country getCountryByNumber(Integer number) throws Exception {
		return _toCountry(
			_countryService.getCountryByNumber(
				contextCompany.getCompanyId(), String.valueOf(number)));
	}

	@Override
	public EntityModel getEntityModel(MultivaluedMap multivaluedMap)
		throws Exception {

		return _entityModel;
	}

	@Override
	public ExportImportDescriptor<? extends BaseModel<?>>
		getExportImportDescriptor() {

		return new ExportImportDescriptor
			<com.liferay.portal.kernel.model.Country>() {

			@Override
			public String getKey() {
				return CountryResourceImpl.class.getName();
			}

			@Override
			public String getLabelLanguageKey() {
				return "countries";
			}

			@Override
			public Class<com.liferay.portal.kernel.model.Country>
				getModelClass() {

				return com.liferay.portal.kernel.model.Country.class;
			}

			@Override
			public List<String> getNestedFields() {
				return List.of("creator", "regions");
			}

			@Override
			public Map<String, Serializable> getParameters(
				PortletDataContext portletDataContext) {

				return HashMapBuilder.<String, Serializable>put(
					"sort", "name:asc"
				).build();
			}

			@Override
			public String getPortletId() {
				return "com_liferay_address_web_internal_portlet_" +
					"CountriesManagementAdminPortlet";
			}

			@Override
			public Scope getScope() {
				return Scope.COMPANY;
			}

		};
	}

	@Override
	public Country patchCountry(Long countryId, Country country)
		throws Exception {

		com.liferay.portal.kernel.model.Country serviceBuilderCountry =
			_countryService.getCountry(countryId);

		serviceBuilderCountry = _countryService.updateCountry(
			GetterUtil.getString(
				country.getExternalReferenceCode(),
				serviceBuilderCountry.getExternalReferenceCode()),
			countryId,
			GetterUtil.getString(
				country.getA2(), serviceBuilderCountry.getA2()),
			GetterUtil.getString(
				country.getA3(), serviceBuilderCountry.getA3()),
			GetterUtil.getBoolean(
				country.getActive(), serviceBuilderCountry.isActive()),
			GetterUtil.getBoolean(
				country.getBillingAllowed(),
				serviceBuilderCountry.isBillingAllowed()),
			(country.getIdd() != null) ? String.valueOf(country.getIdd()) :
				serviceBuilderCountry.getIdd(),
			GetterUtil.getString(
				country.getName(), serviceBuilderCountry.getName()),
			(country.getNumber() != null) ?
				String.valueOf(country.getNumber()) :
					serviceBuilderCountry.getNumber(),
			GetterUtil.getDouble(
				country.getPosition(), serviceBuilderCountry.getPosition()),
			GetterUtil.getBoolean(
				country.getShippingAllowed(),
				serviceBuilderCountry.isShippingAllowed()),
			GetterUtil.getBoolean(
				country.getSubjectToVAT(),
				serviceBuilderCountry.isSubjectToVAT()));

		return _toCountry(
			_updateNestedResources(country, serviceBuilderCountry, false));
	}

	@Override
	public Country patchCountryByExternalReferenceCode(
			String externalReferenceCode, Country country)
		throws Exception {

		com.liferay.portal.kernel.model.Country serviceBuilderCountry =
			_countryService.getCountryByExternalReferenceCode(
				externalReferenceCode, contextCompany.getCompanyId());

		return patchCountry(serviceBuilderCountry.getCountryId(), country);
	}

	@Override
	public Country postCountry(Country country) throws Exception {
		com.liferay.portal.kernel.model.Country serviceBuilderCountry =
			_countryService.addCountry(
				GetterUtil.getString(country.getExternalReferenceCode()),
				country.getA2(), country.getA3(),
				GetterUtil.getBoolean(country.getActive(), true),
				GetterUtil.getBoolean(country.getBillingAllowed(), true),
				String.valueOf(country.getIdd()), country.getName(),
				String.valueOf(country.getNumber()),
				GetterUtil.getDouble(country.getPosition()),
				GetterUtil.getBoolean(country.getShippingAllowed(), true),
				GetterUtil.getBoolean(country.getSubjectToVAT()),
				GetterUtil.getBoolean(country.getZipRequired(), true),
				_getServiceContext());

		_setTitleMap(country);

		_countryLocalService.updateCountryLocalizations(
			serviceBuilderCountry, country.getTitle_i18n());

		return _toCountry(
			_updateNestedResources(
				country,
				_countryLocalService.updateGroupFilterEnabled(
					serviceBuilderCountry.getCountryId(),
					GetterUtil.getBoolean(country.getGroupFilterEnabled())),
				false));
	}

	@Override
	public Country putCountry(Long countryId, Country country)
		throws Exception {

		com.liferay.portal.kernel.model.Country serviceBuilderCountry =
			_countryService.updateCountry(
				GetterUtil.getString(country.getExternalReferenceCode()),
				countryId, country.getA2(), country.getA3(),
				GetterUtil.getBoolean(country.getActive(), true),
				GetterUtil.getBoolean(country.getBillingAllowed(), true),
				String.valueOf(country.getIdd()), country.getName(),
				String.valueOf(country.getNumber()),
				GetterUtil.getDouble(country.getPosition()),
				GetterUtil.getBoolean(country.getShippingAllowed(), true),
				GetterUtil.getBoolean(country.getSubjectToVAT()));

		_setTitleMap(country);

		_countryLocalService.updateCountryLocalizations(
			serviceBuilderCountry, country.getTitle_i18n());

		return _toCountry(
			_updateNestedResources(
				country,
				_countryService.updateGroupFilterEnabled(
					serviceBuilderCountry.getCountryId(),
					GetterUtil.getBoolean(country.getGroupFilterEnabled())),
				true));
	}

	@Override
	public Country putCountryByExternalReferenceCode(
			String externalReferenceCode, Country country)
		throws Exception {

		com.liferay.portal.kernel.model.Country serviceBuilderCountry =
			_countryLocalService.fetchCountryByExternalReferenceCode(
				externalReferenceCode, contextCompany.getCompanyId());

		if (serviceBuilderCountry == null) {
			country.setExternalReferenceCode(() -> externalReferenceCode);

			return postCountry(country);
		}

		return putCountry(serviceBuilderCountry.getCountryId(), country);
	}

	private com.liferay.portal.kernel.model.Region _addOrUpdateRegion(
			com.liferay.portal.kernel.model.Country serviceBuilderCountry,
			Region region)
		throws Exception {

		com.liferay.portal.kernel.model.Region serviceBuilderRegion =
			_regionLocalService.fetchRegionByExternalReferenceCode(
				region.getExternalReferenceCode(),
				contextCompany.getCompanyId());

		if (serviceBuilderRegion != null) {
			serviceBuilderRegion = _regionService.updateRegion(
				region.getExternalReferenceCode(),
				serviceBuilderRegion.getRegionId(), region.getActive(),
				region.getName(), region.getPosition(), region.getRegionCode());
		}
		else {
			serviceBuilderRegion = _regionService.addRegion(
				GetterUtil.getString(region.getExternalReferenceCode()),
				serviceBuilderCountry.getCountryId(),
				GetterUtil.get(region.getActive(), true), region.getName(),
				GetterUtil.getDouble(region.getPosition()),
				region.getRegionCode(), _getServiceContext());
		}

		if (region.getTitle_i18n() != null) {
			_regionLocalService.updateRegionLocalizations(
				serviceBuilderRegion, region.getTitle_i18n());
		}

		return serviceBuilderRegion;
	}

	private ServiceContext _getServiceContext() throws Exception {
		if (contextHttpServletRequest != null) {
			ServiceContext serviceContext = ServiceContextFactory.getInstance(
				com.liferay.portal.kernel.model.Country.class.getName(),
				contextHttpServletRequest);

			if (serviceContext.getUserId() != 0) {
				return serviceContext;
			}
		}

		ServiceContext serviceContext = new ServiceContext();

		serviceContext.setCompanyId(contextCompany.getCompanyId());
		serviceContext.setUserId(contextUser.getUserId());

		return serviceContext;
	}

	private void _setTitleMap(Country country) {
		if (country.getTitle_i18n() == null) {
			Map<String, String> titleMap = new HashMap<>();

			for (Locale locale : _language.getAvailableLocales()) {
				titleMap.put(_language.getLanguageId(locale), null);
			}

			country.setTitle_i18n(() -> titleMap);
		}
	}

	private Country _toCountry(
			com.liferay.portal.kernel.model.Country serviceBuilderCountry)
		throws Exception {

		return _countryResourceDTOConverter.toDTO(serviceBuilderCountry);
	}

	private com.liferay.portal.kernel.model.Country _updateNestedResources(
			Country country,
			com.liferay.portal.kernel.model.Country serviceBuilderCountry,
			boolean replaceRegions)
		throws Exception {

		if (country.getRegions() == null) {
			return serviceBuilderCountry;
		}

		if (replaceRegions) {
			for (com.liferay.portal.kernel.model.Region region :
					_regionService.getRegions(
						serviceBuilderCountry.getCountryId())) {

				_regionService.deleteRegion(region.getRegionId());
			}
		}

		for (Region region : country.getRegions()) {
			_addOrUpdateRegion(serviceBuilderCountry, region);
		}

		return serviceBuilderCountry;
	}

	private static final EntityModel _entityModel = new CountryEntityModel();

	@Reference
	private CountryLocalService _countryLocalService;

	@Reference(
		target = "(component.name=com.liferay.headless.admin.address.internal.dto.v1_0.converter.CountryResourceDTOConverter)"
	)
	private DTOConverter<com.liferay.portal.kernel.model.Country, Country>
		_countryResourceDTOConverter;

	@Reference
	private CountryService _countryService;

	@Reference
	private Language _language;

	@Reference
	private RegionLocalService _regionLocalService;

	@Reference
	private RegionService _regionService;

}