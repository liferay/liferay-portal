/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.asset.library.internal.resource.v1_0;

import com.liferay.depot.model.DepotEntry;
import com.liferay.depot.model.DepotEntryGroupRel;
import com.liferay.depot.service.DepotEntryGroupRelService;
import com.liferay.depot.service.DepotEntryService;
import com.liferay.headless.asset.library.dto.v1_0.AssetLibrary;
import com.liferay.headless.asset.library.dto.v1_0.Site;
import com.liferay.headless.asset.library.resource.v1_0.SiteResource;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.vulcan.dto.converter.DTOConverter;
import com.liferay.portal.vulcan.dto.converter.DTOConverterRegistry;
import com.liferay.portal.vulcan.dto.converter.DefaultDTOConverterContext;
import com.liferay.portal.vulcan.fields.NestedField;
import com.liferay.portal.vulcan.fields.NestedFieldId;
import com.liferay.portal.vulcan.pagination.Page;
import com.liferay.portal.vulcan.pagination.Pagination;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ServiceScope;

/**
 * @author Roberto Díaz
 */
@Component(
	properties = "OSGI-INF/liferay/rest/v1_0/site.properties",
	property = "nested.field.support=true", scope = ServiceScope.PROTOTYPE,
	service = SiteResource.class
)
public class SiteResourceImpl extends BaseSiteResourceImpl {

	@Override
	public void
			deleteAssetLibraryByExternalReferenceCodeAssetLibraryExternalReferenceCodeSiteByExternalReferenceCodeSiteExternalReferenceCode(
				String assetLibraryExternalReferenceCode,
				String siteExternalReferenceCode)
		throws Exception {

		if (!FeatureFlagManagerUtil.isEnabled("LPD-17564")) {
			throw new UnsupportedOperationException();
		}

		Group assetLibraryGroup =
			_groupLocalService.getGroupByExternalReferenceCode(
				assetLibraryExternalReferenceCode,
				contextCompany.getCompanyId());
		Group siteGroup = _groupLocalService.getGroupByExternalReferenceCode(
			siteExternalReferenceCode, contextCompany.getCompanyId());

		deleteAssetLibrarySite(
			assetLibraryGroup.getGroupId(), siteGroup.getGroupId());
	}

	@Override
	public void deleteAssetLibrarySite(Long assetLibraryId, Long siteId)
		throws Exception {

		if (!FeatureFlagManagerUtil.isEnabled("LPD-17564")) {
			throw new UnsupportedOperationException();
		}

		DepotEntry depotEntry = _depotEntryService.getGroupDepotEntry(
			assetLibraryId);

		DepotEntryGroupRel depotEntryGroupRel =
			_depotEntryGroupRelService.
				getDepotEntryGroupRelByDepotEntryIdToGroupId(
					depotEntry.getDepotEntryId(), siteId);

		_depotEntryGroupRelService.deleteDepotEntryGroupRel(
			depotEntryGroupRel.getDepotEntryGroupRelId());
	}

	@Override
	public Site
			getAssetLibraryByExternalReferenceCodeAssetLibraryExternalReferenceCodeSiteByExternalReferenceCodeSiteExternalReferenceCode(
				String assetLibraryExternalReferenceCode,
				String siteExternalReferenceCode)
		throws Exception {

		if (!FeatureFlagManagerUtil.isEnabled("LPD-17564")) {
			throw new UnsupportedOperationException();
		}

		Group assetLibraryGroup =
			_groupLocalService.getGroupByExternalReferenceCode(
				assetLibraryExternalReferenceCode,
				contextCompany.getCompanyId());
		Group siteGroup = _groupLocalService.getGroupByExternalReferenceCode(
			siteExternalReferenceCode, contextCompany.getCompanyId());

		return getAssetLibrarySite(
			assetLibraryGroup.getGroupId(), siteGroup.getGroupId());
	}

	@Override
	public Page<Site> getAssetLibraryByExternalReferenceCodeSitesPage(
			String externalReferenceCode, Pagination pagination)
		throws Exception {

		if (!FeatureFlagManagerUtil.isEnabled("LPD-17564")) {
			throw new UnsupportedOperationException();
		}

		Group group = _groupLocalService.getGroupByExternalReferenceCode(
			externalReferenceCode, contextCompany.getCompanyId());

		return getAssetLibrarySitesPage(group.getGroupId(), pagination);
	}

	@Override
	public Site getAssetLibrarySite(Long assetLibraryId, Long siteId)
		throws Exception {

		if (!FeatureFlagManagerUtil.isEnabled("LPD-17564")) {
			throw new UnsupportedOperationException();
		}

		DepotEntry depotEntry = _depotEntryService.getGroupDepotEntry(
			assetLibraryId);

		DepotEntryGroupRel depotEntryGroupRel =
			_depotEntryGroupRelService.
				getDepotEntryGroupRelByDepotEntryIdToGroupId(
					depotEntry.getDepotEntryId(), siteId);

		return _toSite(depotEntry, depotEntryGroupRel);
	}

	@NestedField(parentClass = AssetLibrary.class, value = "sites")
	@Override
	public Page<Site> getAssetLibrarySitesPage(
			@NestedFieldId("siteId") Long assetLibraryId, Pagination pagination)
		throws Exception {

		if (!FeatureFlagManagerUtil.isEnabled("LPD-17564")) {
			throw new UnsupportedOperationException();
		}

		DepotEntry depotEntry = _depotEntryService.getGroupDepotEntry(
			assetLibraryId);

		return Page.of(
			transform(
				_depotEntryGroupRelService.getDepotEntryGroupRels(
					depotEntry, pagination.getStartPosition(),
					pagination.getEndPosition()),
				depotEntryGroupRel -> _toSite(depotEntry, depotEntryGroupRel)),
			pagination,
			_depotEntryGroupRelService.getDepotEntryGroupRelsCount(depotEntry));
	}

	@Override
	public Site
			putAssetLibraryByExternalReferenceCodeAssetLibraryExternalReferenceCodeSiteByExternalReferenceCodeSiteExternalReferenceCode(
				String assetLibraryExternalReferenceCode,
				String siteExternalReferenceCode)
		throws Exception {

		if (!FeatureFlagManagerUtil.isEnabled("LPD-17564")) {
			throw new UnsupportedOperationException();
		}

		Group assetLibraryGroup =
			_groupLocalService.getGroupByExternalReferenceCode(
				assetLibraryExternalReferenceCode,
				contextCompany.getCompanyId());
		Group siteGroup = _groupLocalService.getGroupByExternalReferenceCode(
			siteExternalReferenceCode, contextCompany.getCompanyId());

		return putAssetLibrarySite(
			assetLibraryGroup.getGroupId(), siteGroup.getGroupId());
	}

	@Override
	public Site putAssetLibrarySite(Long assetLibraryId, Long siteId)
		throws Exception {

		if (!FeatureFlagManagerUtil.isEnabled("LPD-17564")) {
			throw new UnsupportedOperationException();
		}

		DepotEntry depotEntry = _depotEntryService.getGroupDepotEntry(
			assetLibraryId);

		DepotEntryGroupRel depotEntryGroupRel =
			_depotEntryGroupRelService.addDepotEntryGroupRel(
				depotEntry.getDepotEntryId(), siteId);

		return _toSite(depotEntry, depotEntryGroupRel);
	}

	private Site _toSite(
			DepotEntry depotEntry, DepotEntryGroupRel depotEntryGroupRel)
		throws Exception {

		return _siteDTOConverter.toDTO(
			new DefaultDTOConverterContext(
				contextAcceptLanguage.isAcceptAllLanguages(),
				HashMapBuilder.put(
					"delete",
					addAction(
						ActionKeys.DELETE, depotEntry, "deleteAssetLibrarySite")
				).put(
					"get",
					addAction(
						ActionKeys.VIEW, depotEntry, "getAssetLibrarySite")
				).put(
					"update",
					addAction(
						ActionKeys.UPDATE, depotEntry, "putAssetLibrarySite")
				).build(),
				_dtoConverterRegistry, contextHttpServletRequest,
				depotEntryGroupRel.getDepotEntryGroupRelId(),
				contextAcceptLanguage.getPreferredLocale(), contextUriInfo,
				contextUser));
	}

	@Reference
	private DepotEntryGroupRelService _depotEntryGroupRelService;

	@Reference
	private DepotEntryService _depotEntryService;

	@Reference
	private DTOConverterRegistry _dtoConverterRegistry;

	@Reference
	private GroupLocalService _groupLocalService;

	@Reference(
		target = "(component.name=com.liferay.headless.asset.library.internal.dto.v1_0.converter.SiteDTOConverter)"
	)
	private DTOConverter<DepotEntryGroupRel, Site> _siteDTOConverter;

}