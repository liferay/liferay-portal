/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.address.internal.resource.v1_0;

import com.liferay.headless.admin.address.dto.v1_0.Region;
import com.liferay.headless.admin.address.internal.dto.v1_0.converter.constants.DTOConverterConstants;
import com.liferay.headless.admin.address.resource.v1_0.RegionResource;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.model.RegionTable;
import com.liferay.portal.kernel.search.BaseModelSearchResult;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.service.CountryService;
import com.liferay.portal.kernel.service.RegionLocalService;
import com.liferay.portal.kernel.service.RegionService;
import com.liferay.portal.kernel.service.ServiceContextFactory;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.LinkedHashMapBuilder;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.OrderByComparatorFactoryUtil;
import com.liferay.portal.odata.entity.DoubleEntityField;
import com.liferay.portal.odata.entity.EntityModel;
import com.liferay.portal.odata.entity.StringEntityField;
import com.liferay.portal.vulcan.dto.converter.DTOConverter;
import com.liferay.portal.vulcan.pagination.Page;
import com.liferay.portal.vulcan.pagination.Pagination;

import jakarta.ws.rs.core.MultivaluedMap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ServiceScope;

/**
 * @author Drew Brokke
 */
@Component(
	properties = "OSGI-INF/liferay/rest/v1_0/region.properties",
	scope = ServiceScope.PROTOTYPE, service = RegionResource.class
)
public class RegionResourceImpl extends BaseRegionResourceImpl {

	@Override
	public void deleteRegion(Long regionId) throws Exception {
		_regionService.deleteRegion(regionId);
	}

	@Override
	public Region getCountryRegionByRegionCode(
			Long countryId, String regionCode)
		throws Exception {

		return _toRegion(_regionService.getRegion(countryId, regionCode));
	}

	@Override
	public Page<Region> getCountryRegionsPage(
			Long countryId, Boolean active, String search,
			Pagination pagination, Sort[] sorts)
		throws Exception {

		_countryService.getCountry(countryId);

		BaseModelSearchResult<com.liferay.portal.kernel.model.Region>
			baseModelSearchResult = _regionService.searchRegions(
				contextCompany.getCompanyId(), active, search,
				LinkedHashMapBuilder.<String, Object>put(
					"countryId", countryId
				).build(),
				pagination.getStartPosition(), pagination.getEndPosition(),
				_toOrderByComparator(sorts));

		return Page.of(
			transform(baseModelSearchResult.getBaseModels(), this::_toRegion),
			pagination, baseModelSearchResult.getLength());
	}

	@Override
	public EntityModel getEntityModel(MultivaluedMap multivaluedMap)
		throws Exception {

		return _entityModel;
	}

	@Override
	public Region getRegion(Long regionId) throws Exception {
		return _toRegion(_regionService.getRegion(regionId));
	}

	@Override
	public Page<Region> getRegionsPage(
			Boolean active, String search, Pagination pagination, Sort[] sorts)
		throws Exception {

		BaseModelSearchResult<com.liferay.portal.kernel.model.Region>
			baseModelSearchResult = _regionService.searchRegions(
				contextCompany.getCompanyId(), active, search, null,
				pagination.getStartPosition(), pagination.getEndPosition(),
				_toOrderByComparator(sorts));

		return Page.of(
			transform(baseModelSearchResult.getBaseModels(), this::_toRegion),
			pagination, baseModelSearchResult.getLength());
	}

	@Override
	public Region postCountryRegion(Long countryId, Region region)
		throws Exception {

		_setTitleMap(region);

		com.liferay.portal.kernel.model.Region serviceBuilderRegion =
			_regionService.addRegion(
				countryId, GetterUtil.get(region.getActive(), true),
				region.getName(), GetterUtil.getDouble(region.getPosition()),
				region.getRegionCode(),
				ServiceContextFactory.getInstance(
					Region.class.getName(), contextHttpServletRequest));

		_regionLocalService.updateRegionLocalizations(
			serviceBuilderRegion, region.getTitle_i18n());

		return _toRegion(serviceBuilderRegion);
	}

	@Override
	public Region putRegion(Long regionId, Region region) throws Exception {
		_setTitleMap(region);

		com.liferay.portal.kernel.model.Region serviceBuilderRegion =
			_regionService.updateRegion(
				regionId, GetterUtil.get(region.getActive(), true),
				region.getName(), GetterUtil.getDouble(region.getPosition()),
				region.getRegionCode());

		_regionLocalService.updateRegionLocalizations(
			serviceBuilderRegion, region.getTitle_i18n());

		return _toRegion(serviceBuilderRegion);
	}

	private void _setTitleMap(Region region) {
		if (region.getTitle_i18n() == null) {
			Map<String, String> titleMap = new HashMap<>();

			for (Locale locale : _language.getAvailableLocales()) {
				titleMap.put(_language.getLanguageId(locale), null);
			}

			region.setTitle_i18n(() -> titleMap);
		}
	}

	private OrderByComparator<com.liferay.portal.kernel.model.Region>
		_toOrderByComparator(Sort[] sorts) {

		List<Object> objects = new ArrayList<>();

		if (ArrayUtil.isEmpty(sorts)) {
			objects.add(RegionTable.INSTANCE.regionId.getName());
			objects.add(true);
		}
		else {
			for (Sort sort : sorts) {
				objects.add(sort.getFieldName());
				objects.add(!sort.isReverse());
			}
		}

		return OrderByComparatorFactoryUtil.create(
			RegionTable.INSTANCE.getTableName(),
			objects.toArray(new Object[0]));
	}

	private Region _toRegion(
			com.liferay.portal.kernel.model.Region serviceBuilderRegion)
		throws Exception {

		return _regionResourceDTOConverter.toDTO(serviceBuilderRegion);
	}

	private static final EntityModel _entityModel =
		() -> EntityModel.toEntityFieldsMap(
			new DoubleEntityField("position", locale -> "position"),
			new StringEntityField("name", locale -> "name"));

	@Reference
	private CountryService _countryService;

	@Reference
	private Language _language;

	@Reference
	private RegionLocalService _regionLocalService;

	@Reference(target = DTOConverterConstants.REGION_RESOURCE_DTO_CONVERTER)
	private DTOConverter<com.liferay.portal.kernel.model.Region, Region>
		_regionResourceDTOConverter;

	@Reference
	private RegionService _regionService;

}