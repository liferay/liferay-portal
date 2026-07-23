/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.asset.library.internal.resource.v1_0;

import com.liferay.depot.model.DepotEntry;
import com.liferay.depot.model.DepotEntryGroupRel;
import com.liferay.depot.service.DepotEntryGroupRelService;
import com.liferay.depot.service.DepotEntryService;
import com.liferay.headless.asset.library.dto.v1_0.ConnectedSite;
import com.liferay.headless.asset.library.resource.v1_0.ConnectedSiteResource;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.vulcan.dto.converter.DTOConverter;
import com.liferay.portal.vulcan.dto.converter.DTOConverterRegistry;
import com.liferay.portal.vulcan.dto.converter.DefaultDTOConverterContext;
import com.liferay.portal.vulcan.pagination.Page;
import com.liferay.portal.vulcan.pagination.Pagination;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ServiceScope;

/**
 * @author Roberto Díaz
 */
@Component(
	properties = "OSGI-INF/liferay/rest/v1_0/connected-site.properties",
	scope = ServiceScope.PROTOTYPE, service = ConnectedSiteResource.class
)
public class ConnectedSiteResourceImpl extends BaseConnectedSiteResourceImpl {

	@Override
	public void deleteAssetLibraryConnectedSite(
			String assetLibraryExternalReferenceCode,
			String connectedSiteExternalReferenceCode)
		throws Exception {

		Group assetLibraryGroup =
			_groupLocalService.getGroupByExternalReferenceCode(
				assetLibraryExternalReferenceCode,
				contextCompany.getCompanyId());
		Group connectedSiteGroup =
			_groupLocalService.getGroupByExternalReferenceCode(
				connectedSiteExternalReferenceCode,
				contextCompany.getCompanyId());

		DepotEntry depotEntry = _depotEntryService.getGroupDepotEntry(
			assetLibraryGroup.getGroupId());

		DepotEntryGroupRel depotEntryGroupRel =
			_depotEntryGroupRelService.
				getDepotEntryGroupRelByDepotEntryIdToGroupId(
					depotEntry.getDepotEntryId(),
					connectedSiteGroup.getGroupId());

		_depotEntryGroupRelService.deleteDepotEntryGroupRel(
			depotEntryGroupRel.getDepotEntryGroupRelId());
	}

	@Override
	public ConnectedSite getAssetLibraryConnectedSite(
			String assetLibraryExternalReferenceCode,
			String connectedSiteExternalReferenceCode)
		throws Exception {

		Group assetLibraryGroup =
			_groupLocalService.getGroupByExternalReferenceCode(
				assetLibraryExternalReferenceCode,
				contextCompany.getCompanyId());
		Group connectedSiteGroup =
			_groupLocalService.getGroupByExternalReferenceCode(
				connectedSiteExternalReferenceCode,
				contextCompany.getCompanyId());

		DepotEntry depotEntry = _depotEntryService.getGroupDepotEntry(
			assetLibraryGroup.getGroupId());

		DepotEntryGroupRel depotEntryGroupRel =
			_depotEntryGroupRelService.
				getDepotEntryGroupRelByDepotEntryIdToGroupId(
					depotEntry.getDepotEntryId(),
					connectedSiteGroup.getGroupId());

		return _toConnectedSite(depotEntry, depotEntryGroupRel);
	}

	@Override
	public Page<ConnectedSite> getAssetLibraryConnectedSitesPage(
			String assetLibraryExternalReferenceCode, Pagination pagination)
		throws Exception {

		Group group = _groupLocalService.getGroupByExternalReferenceCode(
			assetLibraryExternalReferenceCode, contextCompany.getCompanyId());

		DepotEntry depotEntry = _depotEntryService.getGroupDepotEntry(
			group.getGroupId());

		return Page.of(
			transform(
				_depotEntryGroupRelService.getDepotEntryGroupRels(
					depotEntry, pagination.getStartPosition(),
					pagination.getEndPosition()),
				depotEntryGroupRel -> _toConnectedSite(
					depotEntry, depotEntryGroupRel)),
			pagination,
			_depotEntryGroupRelService.getDepotEntryGroupRelsCount(depotEntry));
	}

	@Override
	public ConnectedSite putAssetLibraryConnectedSite(
			String assetLibraryExternalReferenceCode,
			String connectedSiteExternalReferenceCode,
			ConnectedSite connectedSite)
		throws Exception {

		Group assetLibraryGroup =
			_groupLocalService.getGroupByExternalReferenceCode(
				assetLibraryExternalReferenceCode,
				contextCompany.getCompanyId());
		Group connectedSiteGroup =
			_groupLocalService.getGroupByExternalReferenceCode(
				connectedSiteExternalReferenceCode,
				contextCompany.getCompanyId());

		DepotEntry depotEntry = _depotEntryService.getGroupDepotEntry(
			assetLibraryGroup.getGroupId());

		DepotEntryGroupRel depotEntryGroupRel =
			_depotEntryGroupRelService.addDepotEntryGroupRel(
				depotEntry.getDepotEntryId(), connectedSiteGroup.getGroupId());

		if (connectedSite.getSearchable() != null) {
			_depotEntryGroupRelService.updateSearchable(
				depotEntryGroupRel.getDepotEntryGroupRelId(),
				connectedSite.getSearchable());
		}

		return _toConnectedSite(depotEntry, depotEntryGroupRel);
	}

	private ConnectedSite _toConnectedSite(
			DepotEntry depotEntry, DepotEntryGroupRel depotEntryGroupRel)
		throws Exception {

		return _connectedSiteDTOConverter.toDTO(
			new DefaultDTOConverterContext(
				contextAcceptLanguage.isAcceptAllLanguages(),
				HashMapBuilder.put(
					"delete",
					addAction(
						ActionKeys.DELETE, depotEntry,
						"deleteAssetLibraryConnectedSite")
				).put(
					"get",
					addAction(
						ActionKeys.VIEW, depotEntry,
						"getAssetLibraryConnectedSite")
				).put(
					"update",
					addAction(
						ActionKeys.UPDATE, depotEntry,
						"putAssetLibraryConnectedSite")
				).build(),
				_dtoConverterRegistry, contextHttpServletRequest,
				depotEntryGroupRel.getDepotEntryGroupRelId(),
				contextAcceptLanguage.getPreferredLocale(), contextUriInfo,
				contextUser));
	}

	@Reference(
		target = "(component.name=com.liferay.headless.asset.library.internal.dto.v1_0.converter.ConnectedSiteDTOConverter)"
	)
	private DTOConverter<DepotEntryGroupRel, ConnectedSite>
		_connectedSiteDTOConverter;

	@Reference
	private DepotEntryGroupRelService _depotEntryGroupRelService;

	@Reference
	private DepotEntryService _depotEntryService;

	@Reference
	private DTOConverterRegistry _dtoConverterRegistry;

	@Reference
	private GroupLocalService _groupLocalService;

}