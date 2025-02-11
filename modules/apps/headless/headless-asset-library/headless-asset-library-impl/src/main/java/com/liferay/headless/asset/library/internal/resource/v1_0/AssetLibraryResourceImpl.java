/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.asset.library.internal.resource.v1_0;

import com.liferay.depot.constants.DepotActionKeys;
import com.liferay.depot.model.DepotAppCustomization;
import com.liferay.depot.model.DepotEntry;
import com.liferay.depot.model.DepotEntryGroupRel;
import com.liferay.depot.service.DepotAppCustomizationLocalService;
import com.liferay.depot.service.DepotEntryGroupRelService;
import com.liferay.depot.service.DepotEntryService;
import com.liferay.headless.asset.library.dto.v1_0.AssetLibrary;
import com.liferay.headless.asset.library.resource.v1_0.AssetLibraryResource;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextFactory;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.vulcan.dto.converter.DTOConverter;
import com.liferay.portal.vulcan.dto.converter.DTOConverterRegistry;
import com.liferay.portal.vulcan.dto.converter.DefaultDTOConverterContext;
import com.liferay.portal.vulcan.util.LocalizedMapUtil;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
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
		if (!FeatureFlagManagerUtil.isEnabled("LPD-32649")) {
			throw new UnsupportedOperationException();
		}

		DepotEntry depotEntry = _depotEntryService.getGroupDepotEntry(
			assetLibraryId);

		_depotEntryService.deleteDepotEntry(depotEntry.getDepotEntryId());
	}

	@Override
	public AssetLibrary deleteAssetLibraryLinkToSite(
			Long assetLibraryId, Long toSiteId)
		throws Exception {

		if (!FeatureFlagManagerUtil.isEnabled("LPD-32649")) {
			throw new UnsupportedOperationException();
		}

		DepotEntry depotEntry = _depotEntryService.getGroupDepotEntry(
			assetLibraryId);

		DepotEntryGroupRel depotEntryGroupRel =
			_depotEntryGroupRelService.
				getDepotEntryGroupRelByDepotEntryIdToGroupId(
					depotEntry.getDepotEntryId(), toSiteId);

		_depotEntryGroupRelService.deleteDepotEntryGroupRel(
			depotEntryGroupRel.getDepotEntryGroupRelId());

		return getAssetLibrary(assetLibraryId);
	}

	@Override
	public AssetLibrary getAssetLibrary(Long assetLibraryId) throws Exception {
		if (!FeatureFlagManagerUtil.isEnabled("LPD-32649")) {
			throw new UnsupportedOperationException();
		}

		return _toAssetLibrary(
			_depotEntryService.getGroupDepotEntry(assetLibraryId));
	}

	@Override
	public AssetLibrary patchAssetLibrary(
			Long assetLibraryId, AssetLibrary assetLibrary)
		throws Exception {

		if (!FeatureFlagManagerUtil.isEnabled("LPD-32649")) {
			throw new UnsupportedOperationException();
		}

		DepotEntry depotEntry = _depotEntryService.getGroupDepotEntry(
			assetLibraryId);

		Group group = depotEntry.getGroup();

		List<DepotAppCustomization> depotAppCustomizations =
			_depotAppCustomizationLocalService.getDepotAppCustomizations(
				depotEntry.getDepotEntryId());

		Map<String, Boolean> depotAppCustomizationMap = new HashMap<>();

		for (DepotAppCustomization depotAppCustomization :
				depotAppCustomizations) {

			depotAppCustomizationMap.put(
				depotAppCustomization.getPortletId(),
				depotAppCustomization.isEnabled());
		}

		return _toAssetLibrary(
			_depotEntryService.updateDepotEntry(
				depotEntry.getDepotEntryId(),
				LocalizedMapUtil.getLocalizedMap(
					contextAcceptLanguage.getPreferredLocale(),
					assetLibrary.getName(), assetLibrary.getName_i18n()),
				LocalizedMapUtil.getLocalizedMap(
					contextAcceptLanguage.getPreferredLocale(),
					assetLibrary.getDescription(),
					assetLibrary.getDescription_i18n()),
				depotAppCustomizationMap, group.getTypeSettingsProperties(),
				_getServiceContext()));
	}

	@Override
	public AssetLibrary postAssetLibrary(AssetLibrary assetLibrary)
		throws Exception {

		if (!FeatureFlagManagerUtil.isEnabled("LPD-32649")) {
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
	public AssetLibrary postAssetLibraryLinkToSite(
			Long assetLibraryId, Long toSiteId)
		throws Exception {

		if (!FeatureFlagManagerUtil.isEnabled("LPD-32649")) {
			throw new UnsupportedOperationException();
		}

		DepotEntry depotEntry = _depotEntryService.getGroupDepotEntry(
			assetLibraryId);

		_depotEntryGroupRelService.addDepotEntryGroupRel(
			depotEntry.getDepotEntryId(), toSiteId);

		return getAssetLibrary(assetLibraryId);
	}

	private ServiceContext _getServiceContext() throws Exception {
		ServiceContext serviceContext = ServiceContextFactory.getInstance(
			DepotEntry.class.getName(), contextHttpServletRequest);

		serviceContext.setModifiedDate(new Date());

		return serviceContext;
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
	private DepotEntryGroupRelService _depotEntryGroupRelService;

	@Reference
	private DepotEntryService _depotEntryService;

	@Reference
	private DTOConverterRegistry _dtoConverterRegistry;

}