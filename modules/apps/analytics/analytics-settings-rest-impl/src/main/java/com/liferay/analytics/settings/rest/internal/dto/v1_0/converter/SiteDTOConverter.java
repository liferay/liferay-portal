/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.analytics.settings.rest.internal.dto.v1_0.converter;

import com.liferay.analytics.settings.rest.dto.v1_0.Site;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.UnicodeProperties;
import com.liferay.portal.vulcan.dto.converter.DTOConverter;
import com.liferay.portal.vulcan.dto.converter.DTOConverterContext;

import org.osgi.service.component.annotations.Component;

/**
 * @author Riccardo Ferrari
 */
@Component(
	property = {
		"default=true", "dto.class.name=com.liferay.portal.kernel.model.Group"
	},
	service = DTOConverter.class
)
public class SiteDTOConverter implements DTOConverter<Group, Site> {

	@Override
	public String getContentType() {
		return Site.class.getSimpleName();
	}

	@Override
	public Site toDTO(DTOConverterContext dtoConverterContext, Group group)
		throws Exception {

		SiteDTOConverterContext siteDTOConverterContext =
			(SiteDTOConverterContext)dtoConverterContext;

		return new Site() {
			{
				setChannelName(
					() -> {
						UnicodeProperties typeSettingsUnicodeProperties =
							group.getTypeSettingsProperties();

						String analyticsChannelId =
							typeSettingsUnicodeProperties.getProperty(
								"analyticsChannelId", null);

						return siteDTOConverterContext.getChannelName(
							GetterUtil.getLong(analyticsChannelId));
					});
				setFriendlyURL(group::getFriendlyURL);
				setId(group::getGroupId);
				setName(group::getDescriptiveName);
			}
		};
	}

}