/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.asset.library.internal.resource.v1_0;

import com.liferay.depot.constants.DepotActionKeys;
import com.liferay.depot.model.DepotAppCustomization;
import com.liferay.depot.model.DepotEntry;
import com.liferay.depot.model.DepotEntryPin;
import com.liferay.depot.service.DepotAppCustomizationLocalService;
import com.liferay.depot.service.DepotEntryPinLocalService;
import com.liferay.depot.service.DepotEntryPinService;
import com.liferay.depot.service.DepotEntryService;
import com.liferay.headless.asset.library.dto.v1_0.AssetLibrary;
import com.liferay.headless.asset.library.dto.v1_0.Settings;
import com.liferay.headless.asset.library.resource.v1_0.AssetLibraryResource;
import com.liferay.petra.function.UnsafeSupplier;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.search.filter.Filter;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextFactory;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.UnicodeProperties;
import com.liferay.portal.kernel.util.UnicodePropertiesBuilder;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.vulcan.dto.converter.DTOConverter;
import com.liferay.portal.vulcan.dto.converter.DTOConverterRegistry;
import com.liferay.portal.vulcan.dto.converter.DefaultDTOConverterContext;
import com.liferay.portal.vulcan.pagination.Page;
import com.liferay.portal.vulcan.pagination.Pagination;
import com.liferay.portal.vulcan.util.LocalizedMapUtil;
import com.liferay.portal.vulcan.util.SearchUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
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
	public void deleteAssetLibraryByExternalReferenceCodePin(
			String externalReferenceCode)
		throws Exception {

		if (!FeatureFlagManagerUtil.isEnabled("LPD-17564")) {
			throw new UnsupportedOperationException();
		}

		deleteAssetLibraryPin(
			_getGroupIdByExternalReferenceCode(externalReferenceCode));
	}

	@Override
	public void deleteAssetLibraryPin(Long assetLibraryId) throws Exception {
		if (!FeatureFlagManagerUtil.isEnabled("LPD-17564")) {
			throw new UnsupportedOperationException();
		}

		DepotEntry depotEntry = _depotEntryService.getGroupDepotEntry(
			assetLibraryId);

		_depotEntryPinService.deleteDepotEntryPin(
			contextUser.getUserId(), depotEntry.getDepotEntryId());
	}

	@Override
	public Page<AssetLibrary> getAssetLibrariesPage(
			String keywords, String search, Filter filter,
			Pagination pagination, Sort[] sorts)
		throws Exception {

		if (!FeatureFlagManagerUtil.isEnabled("LPD-17564")) {
			throw new UnsupportedOperationException();
		}

		return SearchUtil.search(
			Collections.emptyMap(),
			booleanQuery -> {
			},
			filter, DepotEntry.class.getName(), keywords, pagination,
			queryConfig -> {
			},
			searchContext -> {
				searchContext.setCompanyId(contextCompany.getCompanyId());

				if (Validator.isNotNull(search)) {
					searchContext.setKeywords(search);
				}
			},
			sorts,
			document -> _toAssetLibrary(
				_depotEntryService.getDepotEntry(
					GetterUtil.getLong(document.get(Field.ENTRY_CLASS_PK)))));
	}

	@Override
	public Page<AssetLibrary> getAssetLibrariesPinnedByMePage(
			Pagination pagination)
		throws Exception {

		if (!FeatureFlagManagerUtil.isEnabled("LPD-17564")) {
			throw new UnsupportedOperationException();
		}

		List<AssetLibrary> assetLibraries = new ArrayList<>(
			pagination.getPageSize());

		List<DepotEntryPin> depotEntryPins =
			_depotEntryPinLocalService.getUserDepotEntryPins(
				contextUser.getUserId(), pagination.getStartPosition(),
				pagination.getEndPosition());

		for (DepotEntryPin depotEntryPin : depotEntryPins) {
			assetLibraries.add(
				_toAssetLibrary(
					_depotEntryService.getDepotEntry(
						depotEntryPin.getDepotEntryId())));
		}

		return Page.of(
			assetLibraries, pagination,
			_depotEntryPinLocalService.getUserDepotEntryPinsCount(
				contextUser.getUserId()));
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

		Group group = depotEntry.getGroup();

		String name = _getValue(
			() -> group.getName(contextAcceptLanguage.getPreferredLocale()),
			assetLibrary::getName);
		Map<String, String> nameMap = _getValue(
			() -> LocalizedMapUtil.getI18nMap(group.getNameMap()),
			assetLibrary::getName_i18n);
		String description = _getValue(
			() -> group.getDescription(
				contextAcceptLanguage.getPreferredLocale()),
			assetLibrary::getDescription);
		Map<String, String> descriptionMap = _getValue(
			() -> LocalizedMapUtil.getI18nMap(group.getDescriptionMap()),
			assetLibrary::getDescription_i18n);
		UnicodeProperties unicodeProperties = _getValue(
			() -> _getUnicodeProperties(
				contextCompany.getCompanyId(),
				group.getExternalReferenceCode()),
			() -> _toUnicodeProperties(assetLibrary.getSettings()));

		return _toAssetLibrary(
			_depotEntryService.updateDepotEntry(
				depotEntry.getDepotEntryId(),
				LocalizedMapUtil.getLocalizedMap(
					contextAcceptLanguage.getPreferredLocale(), name, nameMap),
				LocalizedMapUtil.getLocalizedMap(
					contextAcceptLanguage.getPreferredLocale(), description,
					descriptionMap),
				_getDepotAppCustomizationMap(
					contextCompany.getCompanyId(),
					group.getExternalReferenceCode()),
				unicodeProperties, _getServiceContext()));
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
			_addOrUpdateDepotEntry(
				LocalizedMapUtil.getLocalizedMap(
					contextAcceptLanguage.getPreferredLocale(),
					assetLibrary.getDescription(),
					assetLibrary.getDescription_i18n()),
				StringPool.BLANK,
				LocalizedMapUtil.getLocalizedMap(
					contextAcceptLanguage.getPreferredLocale(),
					assetLibrary.getName(), assetLibrary.getName_i18n()),
				_getServiceContext(),
				_toUnicodeProperties(assetLibrary.getSettings())));
	}

	@Override
	public AssetLibrary putAssetLibraryByExternalReferenceCode(
			String externalReferenceCode, AssetLibrary assetLibrary)
		throws Exception {

		if (!FeatureFlagManagerUtil.isEnabled("LPD-17564")) {
			throw new UnsupportedOperationException();
		}

		return _toAssetLibrary(
			_addOrUpdateDepotEntry(
				LocalizedMapUtil.getLocalizedMap(
					contextAcceptLanguage.getPreferredLocale(),
					assetLibrary.getDescription(),
					assetLibrary.getDescription_i18n()),
				externalReferenceCode,
				LocalizedMapUtil.getLocalizedMap(
					contextAcceptLanguage.getPreferredLocale(),
					assetLibrary.getName(), assetLibrary.getName_i18n()),
				_getServiceContext(),
				_toUnicodeProperties(assetLibrary.getSettings())));
	}

	@Override
	public AssetLibrary putAssetLibraryByExternalReferenceCodePin(
			String externalReferenceCode)
		throws Exception {

		if (!FeatureFlagManagerUtil.isEnabled("LPD-17564")) {
			throw new UnsupportedOperationException();
		}

		return putAssetLibraryPin(
			_getGroupIdByExternalReferenceCode(externalReferenceCode));
	}

	@Override
	public AssetLibrary putAssetLibraryPin(Long assetLibraryId)
		throws Exception {

		if (!FeatureFlagManagerUtil.isEnabled("LPD-17564")) {
			throw new UnsupportedOperationException();
		}

		DepotEntry depotEntry = _depotEntryService.getGroupDepotEntry(
			assetLibraryId);

		_depotEntryPinService.addDepotEntryPin(
			contextUser.getUserId(), depotEntry.getDepotEntryId());

		return _toAssetLibrary(depotEntry);
	}

	private DepotEntry _addOrUpdateDepotEntry(
			Map<Locale, String> descriptionMap, String externalReferenceCode,
			Map<Locale, String> nameMap, ServiceContext serviceContext,
			UnicodeProperties unicodeProperties)
		throws Exception {

		DepotEntry depotEntry = null;

		Group group = null;

		if (Validator.isNotNull(externalReferenceCode)) {
			group = _groupLocalService.fetchGroupByExternalReferenceCode(
				externalReferenceCode, serviceContext.getCompanyId());
		}

		if (group != null) {
			depotEntry = _depotEntryService.getGroupDepotEntry(
				group.getGroupId());
		}
		else {
			depotEntry = _depotEntryService.addDepotEntry(
				nameMap, descriptionMap, serviceContext);

			if (Validator.isNotNull(externalReferenceCode)) {
				group = depotEntry.getGroup();

				group.setExternalReferenceCode(externalReferenceCode);

				_groupLocalService.updateGroup(group);
			}
		}

		return _depotEntryService.updateDepotEntry(
			depotEntry.getDepotEntryId(), nameMap, descriptionMap,
			_getDepotAppCustomizationMap(
				depotEntry.getCompanyId(), externalReferenceCode),
			unicodeProperties, serviceContext);
	}

	private Map<String, Boolean> _getDepotAppCustomizationMap(
			long companyId, String externalReferenceCode)
		throws Exception {

		Map<String, Boolean> depotAppCustomizationMap = new HashMap<>();

		Group group = _groupLocalService.fetchGroupByExternalReferenceCode(
			externalReferenceCode, companyId);

		if (group == null) {
			return depotAppCustomizationMap;
		}

		DepotEntry depotEntry = _depotEntryService.getGroupDepotEntry(
			group.getGroupId());

		for (DepotAppCustomization depotAppCustomization :
				_depotAppCustomizationLocalService.getDepotAppCustomizations(
					depotEntry.getDepotEntryId())) {

			depotAppCustomizationMap.put(
				depotAppCustomization.getPortletId(),
				depotAppCustomization.isEnabled());
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

	private ServiceContext _getServiceContext() throws Exception {
		ServiceContext serviceContext = ServiceContextFactory.getInstance(
			DepotEntry.class.getName(), contextHttpServletRequest);

		serviceContext.setModifiedDate(new Date());

		return serviceContext;
	}

	private UnicodeProperties _getUnicodeProperties(
		long companyId, String externalReferenceCode) {

		Group group = _groupLocalService.fetchGroupByExternalReferenceCode(
			externalReferenceCode, companyId);

		if (group != null) {
			return group.getTypeSettingsProperties();
		}

		return new UnicodeProperties(true);
	}

	private <T, E extends Exception> T _getValue(
			UnsafeSupplier<T, E> defaultValueUnsafeSupplier,
			UnsafeSupplier<T, E> valueUnsafeSupplier)
		throws Exception {

		T value = valueUnsafeSupplier.get();

		if (value == null) {
			return defaultValueUnsafeSupplier.get();
		}

		return value;
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
					"permissions",
					addAction(
						ActionKeys.PERMISSIONS, depotEntry, "patchAssetLibrary")
				).put(
					"update",
					addAction(
						ActionKeys.UPDATE, depotEntry, "patchAssetLibrary")
				).build(),
				_dtoConverterRegistry, depotEntry.getDepotEntryId(),
				contextAcceptLanguage.getPreferredLocale(), contextUriInfo,
				contextUser));
	}

	private UnicodeProperties _toUnicodeProperties(Settings settings) {
		if (settings == null) {
			return null;
		}

		return UnicodePropertiesBuilder.create(
			true
		).put(
			"autoTaggingEnabled",
			GetterUtil.getString(settings.getAutoTaggingEnabled(), "false")
		).put(
			"logoColor",
			GetterUtil.getString(settings.getLogoColor(), "outline-0")
		).put(
			"sharingEnabled",
			GetterUtil.getString(settings.getSharingEnabled(), "false")
		).put(
			"useCustomLanguages",
			GetterUtil.getString(settings.getUseCustomLanguages(), "false")
		).build();
	}

	@Reference(
		target = "(component.name=com.liferay.headless.asset.library.internal.dto.v1_0.converter.AssetLibraryDTOConverter)"
	)
	private DTOConverter<DepotEntry, AssetLibrary> _assetLibraryDTOConverter;

	@Reference
	private DepotAppCustomizationLocalService
		_depotAppCustomizationLocalService;

	@Reference
	private DepotEntryPinLocalService _depotEntryPinLocalService;

	@Reference
	private DepotEntryPinService _depotEntryPinService;

	@Reference
	private DepotEntryService _depotEntryService;

	@Reference
	private DTOConverterRegistry _dtoConverterRegistry;

	@Reference
	private GroupLocalService _groupLocalService;

}