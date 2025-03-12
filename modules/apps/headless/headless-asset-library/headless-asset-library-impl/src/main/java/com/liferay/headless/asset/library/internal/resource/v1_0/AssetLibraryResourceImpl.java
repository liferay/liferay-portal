/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.asset.library.internal.resource.v1_0;

import com.liferay.depot.constants.DepotActionKeys;
import com.liferay.depot.model.DepotAppCustomization;
import com.liferay.depot.model.DepotEntry;
import com.liferay.depot.service.DepotAppCustomizationLocalService;
import com.liferay.depot.service.DepotEntryService;
import com.liferay.headless.asset.library.dto.v1_0.AssetLibrary;
import com.liferay.headless.asset.library.internal.util.comparator.AssetLibraryOrderByComparator;
import com.liferay.headless.asset.library.resource.v1_0.AssetLibraryResource;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.search.filter.Filter;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.GroupService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextFactory;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.LinkedHashMapBuilder;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.UnicodeProperties;
import com.liferay.portal.vulcan.dto.converter.DTOConverter;
import com.liferay.portal.vulcan.dto.converter.DTOConverterRegistry;
import com.liferay.portal.vulcan.dto.converter.DefaultDTOConverterContext;
import com.liferay.portal.vulcan.pagination.Page;
import com.liferay.portal.vulcan.pagination.Pagination;
import com.liferay.portal.vulcan.util.LocalizedMapUtil;

import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ServiceScope;

/**
 * @author Roberto Díaz
 */
@Component(
	properties = "OSGI-INF/liferay/rest/v1_0/asset-library.properties",
	scope = ServiceScope.PROTOTYPE, service = AssetLibraryResource.class
)
public class AssetLibraryResourceImpl extends BaseAssetLibraryResourceImpl {

	@Override
	public void deleteAssetLibrary(Long assetLibraryId) throws Exception {
		if (!FeatureFlagManagerUtil.isEnabled("LPD-17564")) {
			throw new UnsupportedOperationException();
		}

		DepotEntry depotEntry = _depotEntryService.getGroupDepotEntry(
			assetLibraryId);

		_depotEntryService.deleteDepotEntry(depotEntry.getDepotEntryId());
	}

	@Override
	public void deleteAssetLibraryByExternalReferenceCode(
			String externalReferenceCode)
		throws Exception {

		if (!FeatureFlagManagerUtil.isEnabled("LPD-17564")) {
			throw new UnsupportedOperationException();
		}

		deleteAssetLibrary(
			_getGroupIdByExternalReferenceCode(externalReferenceCode));
	}

	@Override
	public Page<AssetLibrary> getAssetLibrariesPage(
			String keywords, String search, Filter filter,
			Pagination pagination, Sort[] sorts)
		throws Exception {

		if (!FeatureFlagManagerUtil.isEnabled("LPD-17564")) {
			throw new UnsupportedOperationException();
		}

		OrderByComparator<Group> assetLibraryOrderByComparator = null;

		if (sorts != null) {
			assetLibraryOrderByComparator = new AssetLibraryOrderByComparator<>(
				contextAcceptLanguage.getPreferredLocale(), sorts);
		}

		return Page.of(
			transform(
				_groupService.search(
					contextCompany.getCompanyId(),
					new long[] {
						_portal.getClassNameId(DepotEntry.class.getName())
					},
					keywords, _getParams(), pagination.getStartPosition(),
					pagination.getEndPosition(), assetLibraryOrderByComparator),
				group -> _assetLibraryDTOConverter.toDTO(
					new DefaultDTOConverterContext(
						contextAcceptLanguage.isAcceptAllLanguages(),
						new HashMap<>(), _dtoConverterRegistry,
						group.getGroupId(),
						contextAcceptLanguage.getPreferredLocale(),
						contextUriInfo, contextUser),
					_depotEntryService.getGroupDepotEntry(group.getGroupId()))),
			pagination,
			_groupService.searchCount(
				contextCompany.getCompanyId(),
				new long[] {_portal.getClassNameId(DepotEntry.class.getName())},
				keywords, _getParams()));
	}

	@Override
	public AssetLibrary getAssetLibrary(Long assetLibraryId) throws Exception {
		if (!FeatureFlagManagerUtil.isEnabled("LPD-17564")) {
			throw new UnsupportedOperationException();
		}

		return _toAssetLibrary(
			_depotEntryService.getGroupDepotEntry(assetLibraryId));
	}

	@Override
	public AssetLibrary getAssetLibraryByExternalReferenceCode(
			String externalReferenceCode)
		throws Exception {

		if (!FeatureFlagManagerUtil.isEnabled("LPD-17564")) {
			throw new UnsupportedOperationException();
		}

		return _toAssetLibrary(
			_depotEntryService.getGroupDepotEntry(
				_getGroupIdByExternalReferenceCode(externalReferenceCode)));
	}

	@Override
	public AssetLibrary patchAssetLibrary(
			Long assetLibraryId, AssetLibrary assetLibrary)
		throws Exception {

		if (!FeatureFlagManagerUtil.isEnabled("LPD-17564")) {
			throw new UnsupportedOperationException();
		}

		DepotEntry depotEntry = _depotEntryService.getGroupDepotEntry(
			assetLibraryId);

		String name = assetLibrary.getName();

		Group group = depotEntry.getGroup();

		if (name == null) {
			name = group.getName(contextAcceptLanguage.getPreferredLocale());
		}

		Map<String, String> nameMap = assetLibrary.getName_i18n();

		if (nameMap == null) {
			nameMap = LocalizedMapUtil.getI18nMap(group.getNameMap());
		}

		String description = assetLibrary.getDescription();

		if (description == null) {
			description = group.getDescription(
				contextAcceptLanguage.getPreferredLocale());
		}

		Map<String, String> descriptionMap = assetLibrary.getDescription_i18n();

		if (descriptionMap == null) {
			descriptionMap = LocalizedMapUtil.getI18nMap(
				group.getDescriptionMap());
		}

		// TODO transform the assetLibrary::settings in unicodeProperties where
		//  needed

		return _toAssetLibrary(
			_depotEntryService.updateDepotEntry(
				depotEntry.getDepotEntryId(),
				LocalizedMapUtil.getLocalizedMap(
					contextAcceptLanguage.getPreferredLocale(), name, nameMap),
				LocalizedMapUtil.getLocalizedMap(
					contextAcceptLanguage.getPreferredLocale(), description,
					descriptionMap),
				_getDepotCustomizationMap(group.getExternalReferenceCode()),
				_getUnicodeProperties(group.getExternalReferenceCode()),
				_getServiceContext()));
	}

	@Override
	public AssetLibrary patchAssetLibraryByExternalReferenceCode(
			String externalReferenceCode, AssetLibrary assetLibrary)
		throws Exception {

		if (!FeatureFlagManagerUtil.isEnabled("LPD-17564")) {
			throw new UnsupportedOperationException();
		}

		return patchAssetLibrary(
			_getGroupIdByExternalReferenceCode(externalReferenceCode),
			assetLibrary);
	}

	@Override
	public AssetLibrary postAssetLibrary(AssetLibrary assetLibrary)
		throws Exception {

		if (!FeatureFlagManagerUtil.isEnabled("LPD-17564")) {
			throw new UnsupportedOperationException();
		}

		return _toAssetLibrary(
			_depotEntryService.addDepotEntry(
				LocalizedMapUtil.getLocalizedMap(
					contextAcceptLanguage.getPreferredLocale(),
					assetLibrary.getName(), assetLibrary.getName_i18n()),
				LocalizedMapUtil.getLocalizedMap(
					contextAcceptLanguage.getPreferredLocale(),
					assetLibrary.getDescription(),
					assetLibrary.getDescription_i18n()),
				_getServiceContext()));
	}

	@Override
	public AssetLibrary putAssetLibraryByExternalReferenceCode(
			String externalReferenceCode, AssetLibrary assetLibrary)
		throws Exception {

		if (!FeatureFlagManagerUtil.isEnabled("LPD-17564")) {
			throw new UnsupportedOperationException();
		}

		return _toAssetLibrary(
			_depotEntryService.addOrUpdateDepotEntry(
				externalReferenceCode,
				LocalizedMapUtil.getLocalizedMap(
					contextAcceptLanguage.getPreferredLocale(),
					assetLibrary.getName(), assetLibrary.getName_i18n()),
				LocalizedMapUtil.getLocalizedMap(
					contextAcceptLanguage.getPreferredLocale(),
					assetLibrary.getDescription(),
					assetLibrary.getDescription_i18n()),
				_getDepotCustomizationMap(externalReferenceCode),
				_getUnicodeProperties(externalReferenceCode),
				_getServiceContext()));
	}

	private Map<String, Boolean> _getDepotCustomizationMap(
			String externalReferenceCode)
		throws Exception {

		Map<String, Boolean> depotAppCustomizationMap = new HashMap<>();

		Group group = _groupLocalService.fetchGroupByExternalReferenceCode(
			externalReferenceCode, contextCompany.getCompanyId());

		if (group != null) {
			DepotEntry depotEntry = _depotEntryService.getGroupDepotEntry(
				group.getGroupId());

			for (DepotAppCustomization depotAppCustomization :
					_depotAppCustomizationLocalService.
						getDepotAppCustomizations(
							depotEntry.getDepotEntryId())) {

				depotAppCustomizationMap.put(
					depotAppCustomization.getPortletId(),
					depotAppCustomization.isEnabled());
			}
		}

		return depotAppCustomizationMap;
	}

	private long _getGroupIdByExternalReferenceCode(
			String externalReferenceCode)
		throws Exception {

		Group group = _groupLocalService.getGroupByExternalReferenceCode(
			externalReferenceCode, contextCompany.getCompanyId());

		return group.getGroupId();
	}

	private LinkedHashMap<String, Object> _getParams() {
		return LinkedHashMapBuilder.<String, Object>put(
			"actionId", ActionKeys.VIEW
		).put(
			"active", Boolean.TRUE
		).put(
			"site", Boolean.FALSE
		).build();
	}

	private ServiceContext _getServiceContext() throws Exception {
		ServiceContext serviceContext = ServiceContextFactory.getInstance(
			DepotEntry.class.getName(), contextHttpServletRequest);

		serviceContext.setModifiedDate(new Date());

		return serviceContext;
	}

	private UnicodeProperties _getUnicodeProperties(
		String externalReferenceCode) {

		UnicodeProperties unicodeProperties = new UnicodeProperties(true);

		Group group = _groupLocalService.fetchGroupByExternalReferenceCode(
			externalReferenceCode, contextCompany.getCompanyId());

		if (group != null) {
			unicodeProperties = group.getTypeSettingsProperties();
		}

		return unicodeProperties;
	}

	private AssetLibrary _toAssetLibrary(DepotEntry depotEntry)
		throws Exception {

		return _assetLibraryDTOConverter.toDTO(
			new DefaultDTOConverterContext(
				contextAcceptLanguage.isAcceptAllLanguages(),
				HashMapBuilder.put(
					"create",
					addAction(
						DepotActionKeys.ADD_DEPOT_ENTRY, depotEntry,
						"postAssetLibrary")
				).put(
					"delete",
					addAction(
						ActionKeys.DELETE, depotEntry, "deleteAssetLibrary")
				).put(
					"get",
					addAction(ActionKeys.VIEW, depotEntry, "getAssetLibrary")
				).put(
					"update",
					addAction(
						ActionKeys.UPDATE, depotEntry, "patchAssetLibrary")
				).build(),
				_dtoConverterRegistry, depotEntry.getGroupId(),
				contextAcceptLanguage.getPreferredLocale(), contextUriInfo,
				contextUser));
	}

	@Reference(
		target = "(component.name=com.liferay.headless.asset.library.internal.dto.v1_0.converter.AssetLibraryDTOConverter)"
	)
	private DTOConverter<DepotEntry, AssetLibrary> _assetLibraryDTOConverter;

	@Reference
	private DepotAppCustomizationLocalService
		_depotAppCustomizationLocalService;

	@Reference
	private DepotEntryService _depotEntryService;

	@Reference
	private DTOConverterRegistry _dtoConverterRegistry;

	@Reference
	private GroupLocalService _groupLocalService;

	@Reference
	private GroupService _groupService;

	@Reference
	private Portal _portal;

}