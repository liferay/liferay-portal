/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.cms.standalone.site.initializer.internal.dto.v1_0.converter;

import com.liferay.depot.model.DepotEntry;
import com.liferay.headless.asset.library.dto.v1_0.AssetLibrary;
import com.liferay.portal.vulcan.dto.converter.DTOConverter;
import com.liferay.portal.vulcan.dto.converter.DTOConverterContext;

import jakarta.ws.rs.core.UriInfo;

import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Adolfo Pérez
 */
@Component(
	property = {
		"dto.class.name=com.liferay.depot.model.DepotEntry",
		"service.ranking:Integer=100"
	},
	service = DTOConverter.class
)
public class CMSAssetLibraryDTOConverter
	implements DTOConverter<DepotEntry, AssetLibrary> {

	@Override
	public String getContentType() {
		return _dtoConverter.getContentType();
	}

	@Override
	public String getJaxRsLink(long classPK, UriInfo uriInfo) {
		return _dtoConverter.getJaxRsLink(classPK, uriInfo);
	}

	@Override
	public AssetLibrary toDTO(DTOConverterContext dtoConverterContext)
		throws Exception {

		_removeConnectedSitesActions(dtoConverterContext);

		return _dtoConverter.toDTO(dtoConverterContext);
	}

	@Override
	public AssetLibrary toDTO(
			DTOConverterContext dtoConverterContext, DepotEntry depotEntry)
		throws Exception {

		_removeConnectedSitesActions(dtoConverterContext);

		return _dtoConverter.toDTO(dtoConverterContext, depotEntry);
	}

	private void _removeConnectedSitesActions(
		DTOConverterContext dtoConverterContext) {

		Map<String, Map<String, String>> actions =
			dtoConverterContext.getActions();

		if (actions == null) {
			return;
		}

		actions.remove("connect-sites");
		actions.remove("view-connected-sites");
	}

	@Reference(
		target = "(component.name=com.liferay.headless.asset.library.internal.dto.v1_0.converter.AssetLibraryDTOConverter)"
	)
	private DTOConverter<DepotEntry, AssetLibrary> _dtoConverter;

}