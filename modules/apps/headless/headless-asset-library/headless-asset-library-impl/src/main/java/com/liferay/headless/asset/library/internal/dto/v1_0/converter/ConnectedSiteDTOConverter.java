/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.asset.library.internal.dto.v1_0.converter;

import com.liferay.depot.constants.DepotConstants;
import com.liferay.depot.model.DepotEntry;
import com.liferay.depot.model.DepotEntryGroupRel;
import com.liferay.depot.service.DepotEntryGroupRelLocalService;
import com.liferay.depot.service.DepotEntryLocalService;
import com.liferay.headless.asset.library.dto.v1_0.ConnectedSite;
import com.liferay.headless.asset.library.internal.resource.v1_0.BaseConnectedSiteResourceImpl;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.vulcan.dto.converter.DTOConverter;
import com.liferay.portal.vulcan.dto.converter.DTOConverterContext;
import com.liferay.portal.vulcan.util.JaxRsLinkUtil;
import com.liferay.portal.vulcan.util.LocalizedMapUtil;
import com.liferay.staging.StagingGroupHelper;

import jakarta.ws.rs.core.UriInfo;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Roberto Díaz
 */
@Component(service = DTOConverter.class)
public class ConnectedSiteDTOConverter
	implements DTOConverter<DepotEntryGroupRel, ConnectedSite> {

	@Override
	public String getContentType() {
		return ConnectedSite.class.getSimpleName();
	}

	@Override
	public String getJaxRsLink(long classPK, UriInfo uriInfo) {
		return JaxRsLinkUtil.getJaxRsLink(
			"headless-asset-library", BaseConnectedSiteResourceImpl.class,
			"getAssetLibrarySite", uriInfo, classPK);
	}

	@Override
	public ConnectedSite toDTO(DTOConverterContext dtoConverterContext)
		throws Exception {

		DepotEntryGroupRel depotEntryGroupRel =
			_depotEntryGroupRelLocalService.getDepotEntryGroupRel(
				(Long)dtoConverterContext.getId());

		Group group = _groupLocalService.getGroup(
			depotEntryGroupRel.getToGroupId());

		return new ConnectedSite() {
			{
				setDescriptiveName(
					() -> group.getDescriptiveName(
						dtoConverterContext.getLocale()));
				setDescriptiveName_i18n(
					() -> LocalizedMapUtil.getI18nMap(
						dtoConverterContext.isAcceptAllLanguages(),
						group.getDescriptiveNameMap()));
				setExternalReferenceCode(group::getExternalReferenceCode);
				setId(group::getGroupId);
				setLogo(
					() -> {
						ThemeDisplay themeDisplay = new ThemeDisplay() {
							{
								setCompany(
									_companyLocalService.getCompany(
										group.getCompanyId()));
								setPathImage(_portal.getPathImage());
							}
						};

						return group.getLogoURL(themeDisplay, true);
					});
				setName(() -> group.getName(dtoConverterContext.getLocale()));
				setName_i18n(
					() -> LocalizedMapUtil.getI18nMap(
						dtoConverterContext.isAcceptAllLanguages(),
						group.getNameMap()));
				setSearchable(depotEntryGroupRel::isSearchable);
				setStagingType(
					() -> {
						if (_stagingGroupHelper.isLiveGroup(group)) {
							return ConnectedSite.StagingType.LIVE;
						}

						if (_stagingGroupHelper.isStagingGroup(group)) {
							return ConnectedSite.StagingType.STAGING;
						}

						return null;
					});
				setType(
					() -> {
						DepotEntry groupDepotEntry =
							_depotEntryLocalService.fetchGroupDepotEntry(
								group.getGroupId());

						if ((groupDepotEntry != null) &&
							(groupDepotEntry.getType() ==
								DepotConstants.TYPE_DESIGN_LIBRARY)) {

							return ConnectedSite.Type.DESIGN_LIBRARY;
						}

						if (group.isLayoutSetPrototype()) {
							return ConnectedSite.Type.SITE_TEMPLATE;
						}

						return ConnectedSite.Type.SITE;
					});
			}
		};
	}

	@Reference
	private CompanyLocalService _companyLocalService;

	@Reference
	private DepotEntryGroupRelLocalService _depotEntryGroupRelLocalService;

	@Reference
	private DepotEntryLocalService _depotEntryLocalService;

	@Reference
	private GroupLocalService _groupLocalService;

	@Reference
	private Portal _portal;

	@Reference
	private StagingGroupHelper _stagingGroupHelper;

}