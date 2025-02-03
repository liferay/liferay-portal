/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.asset.library.internal.resource.v1_0;

import com.liferay.depot.model.DepotEntry;
import com.liferay.depot.service.DepotEntryService;
import com.liferay.headless.asset.library.dto.v1_0.AssetLibrary;
import com.liferay.headless.asset.library.resource.v1_0.AssetLibraryResource;
import com.liferay.portal.kernel.service.ServiceContextFactory;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.vulcan.dto.converter.DTOConverter;
import com.liferay.portal.vulcan.dto.converter.DTOConverterRegistry;
import com.liferay.portal.vulcan.dto.converter.DefaultDTOConverterContext;
import com.liferay.portal.vulcan.util.LocalizedMapUtil;

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
	public AssetLibrary postAssetLibrary(AssetLibrary assetLibrary) throws Exception {
		return _toAssetLibrary(
			_depotEntryService.addDepotEntry(
				LocalizedMapUtil.getLocalizedMap(
					contextAcceptLanguage.getPreferredLocale(), assetLibrary.getName(),
					assetLibrary.getName_i18n()),
				LocalizedMapUtil.getLocalizedMap(
					contextAcceptLanguage.getPreferredLocale(),
					assetLibrary.getDescription(), assetLibrary.getDescription_i18n()),
				ServiceContextFactory.getInstance(
					DepotEntry.class.getName(), contextHttpServletRequest)));
	}

	private AssetLibrary _toAssetLibrary(DepotEntry depotEntry) throws Exception {
		return _assetLibraryDTOConverter.toDTO(
			new DefaultDTOConverterContext(
				contextAcceptLanguage.isAcceptAllLanguages(),
				HashMapBuilder.put(
					"create", addAction("add", depotEntry, "postAssetLibrary")
				).build(),
				_dtoConverterRegistry, depotEntry.getDepotEntryId(),
				contextAcceptLanguage.getPreferredLocale(), contextUriInfo,
				contextUser));
	}

	@Reference
	private DepotEntryService _depotEntryService;

	@Reference
	private DTOConverterRegistry _dtoConverterRegistry;

	@Reference(
		target = "(component.name=com.liferay.headless.asset.library.internal.dto.v1_0.converter.AssetLibraryDTOConverter)"
	)
	private DTOConverter<DepotEntry, AssetLibrary> _assetLibraryDTOConverter;

}