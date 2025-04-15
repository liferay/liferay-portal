/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.asset.library.internal.dto.v1_0.converter;

import com.liferay.depot.model.DepotEntryGroupRel;
import com.liferay.depot.service.DepotEntryGroupRelLocalService;
import com.liferay.headless.asset.library.dto.v1_0.Site;
import com.liferay.headless.asset.library.internal.resource.v1_0.BaseSiteResourceImpl;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.vulcan.dto.converter.DTOConverter;
import com.liferay.portal.vulcan.dto.converter.DTOConverterContext;
import com.liferay.portal.vulcan.util.JaxRsLinkUtil;
import com.liferay.portal.vulcan.util.LocalizedMapUtil;

import jakarta.ws.rs.core.UriInfo;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Roberto Díaz
 */
@Component(service = DTOConverter.class)
public class SiteDTOConverter
	implements DTOConverter<DepotEntryGroupRel, Site> {

	@Override
	public String getContentType() {
		return Site.class.getSimpleName();
	}

	@Override
	public String getJaxRsLink(long classPK, UriInfo uriInfo) {
		return JaxRsLinkUtil.getJaxRsLink(
			"headless-asset-library", BaseSiteResourceImpl.class,
			"getAssetLibrarySite", uriInfo, classPK);
	}

	@Override
	public Site toDTO(DTOConverterContext dtoConverterContext)
		throws Exception {

		DepotEntryGroupRel depotEntryGroupRel =
			_depotEntryGroupRelLocalService.getDepotEntryGroupRel(
				(Long)dtoConverterContext.getId());

		Group group = _groupLocalService.getGroup(
			depotEntryGroupRel.getToGroupId());

		return new Site() {
			{
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
			}
		};
	}

	@Reference
	private CompanyLocalService _companyLocalService;

	@Reference
	private DepotEntryGroupRelLocalService _depotEntryGroupRelLocalService;

	@Reference
	private GroupLocalService _groupLocalService;

	@Reference
	private Portal _portal;

}