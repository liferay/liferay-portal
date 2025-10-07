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
import com.liferay.document.library.configuration.DLSizeLimitConfigurationProvider;
import com.liferay.expando.kernel.model.ExpandoBridge;
import com.liferay.expando.kernel.model.ExpandoColumnConstants;
import com.liferay.headless.asset.library.dto.v1_0.AssetLibrary;
import com.liferay.headless.asset.library.dto.v1_0.MimeTypeLimit;
import com.liferay.headless.asset.library.dto.v1_0.Settings;
import com.liferay.headless.asset.library.internal.odata.entity.v1_0.AssetLibraryEntityModel;
import com.liferay.headless.asset.library.internal.util.AssetLibraryUtil;
import com.liferay.headless.asset.library.resource.v1_0.AssetLibraryResource;
import com.liferay.petra.function.UnsafeSupplier;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.search.filter.Filter;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextFactory;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.UnicodeProperties;
import com.liferay.portal.kernel.util.UnicodePropertiesBuilder;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.odata.entity.EntityModel;
import com.liferay.portal.vulcan.dto.converter.DTOConverter;
import com.liferay.portal.vulcan.dto.converter.DTOConverterRegistry;
import com.liferay.portal.vulcan.dto.converter.DefaultDTOConverterContext;
import com.liferay.portal.vulcan.fields.NestedFieldsSupplier;
import com.liferay.portal.vulcan.pagination.Page;
import com.liferay.portal.vulcan.pagination.Pagination;
import com.liferay.portal.vulcan.permission.Permission;
import com.liferay.portal.vulcan.util.LocalizedMapUtil;
import com.liferay.portal.vulcan.util.SearchUtil;
import com.liferay.sharing.constants.SharingConfigurationConstants;

import jakarta.ws.rs.core.MultivaluedMap;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
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

		DepotEntry depotEntry = _getGroupDepotEntry(assetLibraryId);

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

		DepotEntry depotEntry = _getGroupDepotEntry(assetLibraryId);

		_depotEntryPinService.deleteDepotEntryPin(
			contextUser.getUserId(), depotEntry.getDepotEntryId());
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
	public Page<Permission>
			getAssetLibraryByExternalReferenceCodePermissionsPage(
				String externalReferenceCode, String roleNames)
		throws Exception {

		if (!FeatureFlagManagerUtil.isEnabled("LPD-17564")) {
			throw new UnsupportedOperationException();
		}

		return getAssetLibraryPermissionsPage(
			_getGroupIdByExternalReferenceCode(externalReferenceCode),
			roleNames);
	}

	@Override
	public EntityModel getEntityModel(MultivaluedMap multivaluedMap) {
		return _assetLibraryEntityModel;
	}

	@Override
	public AssetLibrary patchAssetLibrary(
			Long assetLibraryId, AssetLibrary assetLibrary)
		throws Exception {

		if (!FeatureFlagManagerUtil.isEnabled("LPD-17564")) {
			throw new UnsupportedOperationException();
		}

		DepotEntry depotEntry = _getGroupDepotEntry(assetLibraryId);

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

		if (assetLibrary.getSettings() == null) {
			assetLibrary.setSettings(Settings::new);
		}

		UnicodeProperties unicodeProperties = _patchUnicodeProperties(
			assetLibrary.getSettings(),
			_getUnicodeProperties(
				contextCompany.getCompanyId(),
				group.getExternalReferenceCode()));

		AssetLibrary updatedAssetLibrary = _toAssetLibrary(
			_addOrUpdateDepotEntry(
				assetLibrary,
				LocalizedMapUtil.getLocalizedMap(
					contextAcceptLanguage.getPreferredLocale(), description,
					descriptionMap),
				group.getExternalReferenceCode(),
				LocalizedMapUtil.getLocalizedMap(
					contextAcceptLanguage.getPreferredLocale(), name, nameMap),
				_getServiceContext(), unicodeProperties,
				_dlSizeLimitConfigurationProvider.getGroupMimeTypeSizeLimit(
					group.getGroupId())));

		Permission[] permissions = assetLibrary.getPermissions();

		if (permissions != null) {
			Page<Permission> permissionsPage = putAssetLibraryPermissionsPage(
				updatedAssetLibrary.getId(), permissions);

			updatedAssetLibrary.setPermissions(
				() -> NestedFieldsSupplier.supply(
					"permissions",
					nestedField -> {
						Collection<Permission> collection =
							permissionsPage.getItems();

						return collection.toArray(new Permission[0]);
					}));
		}

		return updatedAssetLibrary;
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
	public Page<Permission>
			putAssetLibraryByExternalReferenceCodePermissionsPage(
				String externalReferenceCode, Permission[] permissions)
		throws Exception {

		if (!FeatureFlagManagerUtil.isEnabled("LPD-17564")) {
			throw new UnsupportedOperationException();
		}

		return putAssetLibraryPermissionsPage(
			_getGroupIdByExternalReferenceCode(externalReferenceCode),
			permissions);
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

		DepotEntry depotEntry = _getGroupDepotEntry(assetLibraryId);

		_depotEntryPinService.addDepotEntryPin(
			contextUser.getUserId(), depotEntry.getDepotEntryId());

		return _toAssetLibrary(depotEntry);
	}

	@Override
	protected Page<AssetLibrary> doGetAssetLibrariesPage(
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
	protected AssetLibrary doGetAssetLibrary(Long assetLibraryId)
		throws Exception {

		if (!FeatureFlagManagerUtil.isEnabled("LPD-17564")) {
			throw new UnsupportedOperationException();
		}

		return _toAssetLibrary(_getGroupDepotEntry(assetLibraryId));
	}

	@Override
	protected AssetLibrary doGetAssetLibraryByExternalReferenceCode(
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
	protected AssetLibrary doPostAssetLibrary(AssetLibrary assetLibrary)
		throws Exception {

		if (!FeatureFlagManagerUtil.isEnabled("LPD-17564")) {
			throw new UnsupportedOperationException();
		}

		return _toAssetLibrary(
			_addOrUpdateDepotEntry(
				assetLibrary,
				LocalizedMapUtil.getLocalizedMap(
					contextAcceptLanguage.getPreferredLocale(),
					assetLibrary.getDescription(),
					assetLibrary.getDescription_i18n()),
				StringPool.BLANK,
				LocalizedMapUtil.getLocalizedMap(
					contextAcceptLanguage.getPreferredLocale(),
					assetLibrary.getName(), assetLibrary.getName_i18n()),
				_getServiceContext(),
				_putUnicodeProperties(assetLibrary.getSettings()),
				new LinkedHashMap<>()));
	}

	@Override
	protected AssetLibrary doPutAssetLibraryByExternalReferenceCode(
			String externalReferenceCode, AssetLibrary assetLibrary)
		throws Exception {

		if (!FeatureFlagManagerUtil.isEnabled("LPD-17564")) {
			throw new UnsupportedOperationException();
		}

		return _toAssetLibrary(
			_addOrUpdateDepotEntry(
				assetLibrary,
				LocalizedMapUtil.getLocalizedMap(
					contextAcceptLanguage.getPreferredLocale(),
					assetLibrary.getDescription(),
					assetLibrary.getDescription_i18n()),
				externalReferenceCode,
				LocalizedMapUtil.getLocalizedMap(
					contextAcceptLanguage.getPreferredLocale(),
					assetLibrary.getName(), assetLibrary.getName_i18n()),
				_getServiceContext(),
				_putUnicodeProperties(assetLibrary.getSettings()),
				new LinkedHashMap<>()));
	}

	@Override
	protected Long getPermissionCheckerGroupId(Object id) throws Exception {
		DepotEntry depotEntry = _getGroupDepotEntry(GetterUtil.getLong(id));

		return depotEntry.getGroupId();
	}

	@Override
	protected Long getPermissionCheckerResourceId(Object id) throws Exception {
		DepotEntry depotEntry = _getGroupDepotEntry(GetterUtil.getLong(id));

		return depotEntry.getDepotEntryId();
	}

	@Override
	protected String getPermissionCheckerResourceName(Object id) {
		return DepotEntry.class.getName();
	}

	private DepotEntry _addOrUpdateDepotEntry(
			AssetLibrary assetLibrary, Map<Locale, String> descriptionMap,
			String externalReferenceCode, Map<Locale, String> nameMap,
			ServiceContext serviceContext, UnicodeProperties unicodeProperties,
			Map<String, Long> mimeTypeSizeLimits)
		throws Exception {

		Group group = null;

		if (Validator.isNotNull(externalReferenceCode)) {
			group = _groupLocalService.fetchGroupByExternalReferenceCode(
				externalReferenceCode, serviceContext.getCompanyId());
		}

		if (group != null) {
			DepotEntry depotEntry = _depotEntryService.getGroupDepotEntry(
				group.getGroupId());

			if (!externalReferenceCode.equals(
					assetLibrary.getExternalReferenceCode())) {

				group = depotEntry.getGroup();

				group.setExternalReferenceCode(
					assetLibrary.getExternalReferenceCode());

				group = _groupLocalService.updateGroup(group);
			}

			_updateDLSizeLimitConfiguration(
				assetLibrary, group.getGroupId(), mimeTypeSizeLimits);

			return _depotEntryService.updateDepotEntry(
				depotEntry.getDepotEntryId(), nameMap, descriptionMap,
				_getDepotAppCustomizationMap(
					depotEntry.getCompanyId(), externalReferenceCode),
				unicodeProperties, serviceContext);
		}

		DepotEntry depotEntry = _depotEntryService.addDepotEntry(
			nameMap, descriptionMap,
			AssetLibraryUtil.getDepotEntryType(
				GetterUtil.getObject(
					assetLibrary.getType(),
					() -> AssetLibrary.Type.ASSET_LIBRARY)),
			serviceContext);

		group = depotEntry.getGroup();

		if (Validator.isNotNull(externalReferenceCode) ||
			((unicodeProperties != null) && !unicodeProperties.isEmpty())) {

			if (Validator.isNotNull(externalReferenceCode)) {
				group.setExternalReferenceCode(externalReferenceCode);
			}

			if ((unicodeProperties != null) && !unicodeProperties.isEmpty()) {
				group.setTypeSettingsProperties(unicodeProperties);
			}

			group = _groupLocalService.updateGroup(group);
		}

		_updateDLSizeLimitConfiguration(
			assetLibrary, group.getGroupId(), mimeTypeSizeLimits);

		if (assetLibrary.getType() == AssetLibrary.Type.SPACE) {
			Company company = _companyLocalService.getCompanyById(
				serviceContext.getCompanyId());

			ExpandoBridge expandoBridge = company.getExpandoBridge();

			if (!expandoBridge.hasAttribute("cmsFirstTimeAccess")) {
				expandoBridge.addAttribute(
					"cmsFirstTimeAccess", ExpandoColumnConstants.BOOLEAN,
					false);

				expandoBridge.setAttribute("cmsFirstTimeAccess", Boolean.FALSE);
			}
		}

		return depotEntry;
	}

	private Boolean _getBooleanValue(Object defaultValue, Boolean value) {
		if (value == null) {
			return GetterUtil.getBoolean(defaultValue);
		}

		return value;
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

	private DepotEntry _getGroupDepotEntry(Long assetLibraryId)
		throws Exception {

		DepotEntry depotEntry = _depotEntryService.fetchGroupDepotEntry(
			assetLibraryId);

		if (depotEntry != null) {
			return depotEntry;
		}

		return _depotEntryService.getDepotEntry(assetLibraryId);
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

		serviceContext.setCompanyId(contextCompany.getCompanyId());
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

	private UnicodeProperties _patchUnicodeProperties(
		Settings settings, UnicodeProperties unicodeProperties) {

		return UnicodePropertiesBuilder.create(
			true
		).put(
			"autoTaggingEnabled",
			_getBooleanValue(
				unicodeProperties.getProperty("autoTaggingEnabled"),
				settings.getAutoTaggingEnabled())
		).put(
			"inheritLocales",
			() -> {
				Boolean inheritLocales = _getBooleanValue(
					unicodeProperties.getProperty("inheritLocales"),
					settings.getUseCustomLanguages());

				if (settings.getUseCustomLanguages() == null) {
					return inheritLocales.toString();
				}

				return String.valueOf(!inheritLocales.booleanValue());
			}
		).put(
			"languageId",
			GetterUtil.getString(
				settings.getDefaultLanguageId(),
				unicodeProperties.getProperty("languageId"))
		).put(
			"locales",
			GetterUtil.getString(
				StringUtil.merge(settings.getAvailableLanguageIds()),
				unicodeProperties.getProperty("locales"))
		).put(
			"logoColor",
			GetterUtil.getString(
				settings.getLogoColor(),
				unicodeProperties.getProperty("logoColor"))
		).put(
			"sharingEnabled",
			_getBooleanValue(
				unicodeProperties.getProperty("sharingEnabled"),
				settings.getSharingEnabled())
		).put(
			"trashEnabled",
			_getBooleanValue(
				unicodeProperties.getProperty("trashEnabled"),
				settings.getTrashEnabled())
		).put(
			"trashEntriesMaxAge",
			GetterUtil.getInteger(
				settings.getTrashEntriesMaxAge(),
				GetterUtil.getInteger(
					unicodeProperties.getProperty("trashEntriesMaxAge")))
		).build();
	}

	private UnicodeProperties _putUnicodeProperties(Settings settings) {
		if (settings == null) {
			return null;
		}

		return UnicodePropertiesBuilder.create(
			true
		).put(
			"autoTaggingEnabled",
			GetterUtil.getBoolean(settings.getAutoTaggingEnabled())
		).put(
			"inheritLocales",
			!GetterUtil.getBoolean(settings.getUseCustomLanguages())
		).put(
			"languageId", settings.getDefaultLanguageId()
		).put(
			"locales", StringUtil.merge(settings.getAvailableLanguageIds())
		).put(
			"logoColor",
			GetterUtil.getString(settings.getLogoColor(), "outline-0")
		).put(
			"sharingEnabled",
			GetterUtil.getBoolean(
				settings.getSharingEnabled(),
				SharingConfigurationConstants.SHARING_ENABLED_DEFAULT)
		).put(
			"trashEnabled",
			GetterUtil.getBoolean(settings.getTrashEnabled(), true)
		).put(
			"trashEntriesMaxAge",
			GetterUtil.getInteger(settings.getTrashEntriesMaxAge())
		).build();
	}

	private AssetLibrary _toAssetLibrary(DepotEntry depotEntry)
		throws Exception {

		return _assetLibraryDTOConverter.toDTO(
			new DefaultDTOConverterContext(
				contextAcceptLanguage.isAcceptAllLanguages(),
				HashMapBuilder.put(
					"assign-members",
					() -> {
						if (!_groupModelResourcePermission.contains(
								PermissionThreadLocal.getPermissionChecker(),
								depotEntry.getGroupId(),
								ActionKeys.ASSIGN_MEMBERS)) {

							return null;
						}

						return addAction(
							ActionKeys.VIEW, depotEntry, "getAssetLibrary");
					}
				).put(
					"connect-sites",
					() -> {
						if (!_depotEntryModelResourcePermission.contains(
								PermissionThreadLocal.getPermissionChecker(),
								depotEntry.getDepotEntryId(),
								ActionKeys.UPDATE)) {

							return null;
						}

						return addAction(
							ActionKeys.VIEW, depotEntry, "getAssetLibrary");
					}
				).put(
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
					"pin",
					addAction(
						ActionKeys.UPDATE, depotEntry, "putAssetLibraryPin")
				).put(
					"unpin",
					addAction(
						ActionKeys.UPDATE, depotEntry, "deleteAssetLibraryPin")
				).put(
					"update",
					addAction(
						ActionKeys.UPDATE, depotEntry, "patchAssetLibrary")
				).put(
					"view-connected-sites",
					() -> {
						if (_depotEntryModelResourcePermission.contains(
								PermissionThreadLocal.getPermissionChecker(),
								depotEntry.getDepotEntryId(),
								ActionKeys.UPDATE)) {

							return null;
						}

						return addAction(
							ActionKeys.VIEW, depotEntry, "getAssetLibrary");
					}
				).put(
					"view-members",
					() -> {
						if (_groupModelResourcePermission.contains(
								PermissionThreadLocal.getPermissionChecker(),
								depotEntry.getGroupId(),
								ActionKeys.ASSIGN_MEMBERS)) {

							return null;
						}

						return addAction(
							ActionKeys.VIEW, depotEntry, "getAssetLibrary");
					}
				).build(),
				_dtoConverterRegistry, depotEntry.getDepotEntryId(),
				contextAcceptLanguage.getPreferredLocale(), contextUriInfo,
				contextUser));
	}

	private void _updateDLSizeLimitConfiguration(
			AssetLibrary assetLibrary, long groupId,
			Map<String, Long> mimeTypeSizeLimits)
		throws Exception {

		Settings settings = assetLibrary.getSettings();

		MimeTypeLimit[] mimeTypeLimits = settings.getMimeTypeLimits();

		if (mimeTypeLimits == null) {
			_dlSizeLimitConfigurationProvider.updateGroupSizeLimit(
				groupId, 0L, 0L, mimeTypeSizeLimits);

			return;
		}

		mimeTypeSizeLimits = new LinkedHashMap<>();

		for (MimeTypeLimit mimeTypeLimit : mimeTypeLimits) {
			String mimeType = mimeTypeLimit.getMimeType();

			if (Validator.isNotNull(mimeType)) {
				mimeTypeSizeLimits.put(
					mimeType,
					GetterUtil.getLong(mimeTypeLimit.getMaximumSize()));
			}
		}

		_dlSizeLimitConfigurationProvider.updateGroupSizeLimit(
			groupId, 0L, 0L, mimeTypeSizeLimits);
	}

	private static final AssetLibraryEntityModel _assetLibraryEntityModel =
		new AssetLibraryEntityModel();

	@Reference(
		target = "(component.name=com.liferay.headless.asset.library.internal.dto.v1_0.converter.AssetLibraryDTOConverter)"
	)
	private DTOConverter<DepotEntry, AssetLibrary> _assetLibraryDTOConverter;

	@Reference
	private CompanyLocalService _companyLocalService;

	@Reference
	private DepotAppCustomizationLocalService
		_depotAppCustomizationLocalService;

	@Reference(target = "(model.class.name=com.liferay.depot.model.DepotEntry)")
	private ModelResourcePermission<DepotEntry>
		_depotEntryModelResourcePermission;

	@Reference
	private DepotEntryPinLocalService _depotEntryPinLocalService;

	@Reference
	private DepotEntryPinService _depotEntryPinService;

	@Reference
	private DepotEntryService _depotEntryService;

	@Reference
	private DLSizeLimitConfigurationProvider _dlSizeLimitConfigurationProvider;

	@Reference
	private DTOConverterRegistry _dtoConverterRegistry;

	@Reference
	private GroupLocalService _groupLocalService;

	@Reference(
		target = "(model.class.name=com.liferay.portal.kernel.model.Group)"
	)
	private ModelResourcePermission<Group> _groupModelResourcePermission;

}