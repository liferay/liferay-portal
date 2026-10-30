/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.admin.channel.internal.dto.v1_0.converter;

import com.liferay.headless.commerce.admin.channel.dto.v1_0.ChannelSite;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.vulcan.dto.converter.DTOConverter;
import com.liferay.portal.vulcan.dto.converter.DTOConverterContext;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Michele Vigilante
 */
@Component(
	property = "dto.class.name=com.liferay.portal.kernel.model.Group",
	service = DTOConverter.class
)
public class ChannelSiteDTOConverter
	implements DTOConverter<Group, ChannelSite> {

	@Override
	public String getContentType() {
		return ChannelSite.class.getSimpleName();
	}

	@Override
	public ChannelSite toDTO(DTOConverterContext dtoConverterContext)
		throws Exception {

		Group group = _groupLocalService.getGroup(
			(Long)dtoConverterContext.getId());

		return new ChannelSite() {
			{
				setExternalReferenceCode(group::getExternalReferenceCode);
				setFriendlyUrlPath(group::getFriendlyURL);
				setId(group::getGroupId);
				setName(() -> group.getName(dtoConverterContext.getLocale()));
			}
		};
	}

	@Reference
	private GroupLocalService _groupLocalService;

}