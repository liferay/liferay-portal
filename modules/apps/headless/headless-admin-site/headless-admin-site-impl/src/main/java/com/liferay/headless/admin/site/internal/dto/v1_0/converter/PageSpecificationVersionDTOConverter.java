/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.site.internal.dto.v1_0.converter;

import com.liferay.headless.admin.site.dto.v1_0.PageSpecificationVersion;
import com.liferay.headless.admin.user.dto.v1_0.Creator;
import com.liferay.layout.content.model.LayoutContentVersion;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.vulcan.dto.converter.DTOConverter;
import com.liferay.portal.vulcan.dto.converter.DTOConverterContext;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Lourdes Fernández Besada
 */
@Component(service = DTOConverter.class)
public class PageSpecificationVersionDTOConverter
	implements DTOConverter<LayoutContentVersion, PageSpecificationVersion> {

	@Override
	public String getContentType() {
		return PageSpecificationVersion.class.getSimpleName();
	}

	@Override
	public PageSpecificationVersion toDTO(
		DTOConverterContext dtoConverterContext,
		LayoutContentVersion layoutContentVersion) {

		return new PageSpecificationVersion() {
			{
				setCreator(
					() -> {
						User user = _userLocalService.fetchUser(
							layoutContentVersion.getUserId());

						if (user == null) {
							return null;
						}

						return new Creator() {
							{
								setExternalReferenceCode(
									user::getExternalReferenceCode);
								setImage(
									() -> user.getPortraitURL(
										new ThemeDisplay() {
											{
												setPathImage(
													_portal.getPathImage());
											}
										}));
								setName(user::getFullName);
							}
						};
					});
				setDateCreated(layoutContentVersion::getCreateDate);
				setDateModified(layoutContentVersion::getModifiedDate);
				setExternalReferenceCode(
					layoutContentVersion::getExternalReferenceCode);
				setName(
					() -> layoutContentVersion.getName(
						LocaleUtil.getSiteDefault()));
				setStatus(
					() -> {
						if (layoutContentVersion.getStatus() ==
								WorkflowConstants.STATUS_APPROVED) {

							return PageSpecificationVersion.Status.APPROVED;
						}

						return PageSpecificationVersion.Status.DRAFT;
					});
				setStatusDate(layoutContentVersion::getStatusDate);
				setVersion(layoutContentVersion::getVersion);
			}
		};
	}

	@Reference
	private Portal _portal;

	@Reference
	private UserLocalService _userLocalService;

}