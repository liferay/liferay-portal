/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.change.tracking.rest.internal.dto.v1_0.converter;

import com.liferay.change.tracking.rest.dto.v1_0.CTRemote;
import com.liferay.portal.vulcan.dto.converter.DTOConverter;
import com.liferay.portal.vulcan.dto.converter.DTOConverterContext;

import org.osgi.service.component.annotations.Component;

/**
 * @author Pei-Jung Lan
 */
@Component(
	property = {
		"default=true",
		"dto.class.name=com.liferay.change.tracking.model.CTRemote"
	},
	service = DTOConverter.class
)
public class CTRemoteDTOConverter
	implements DTOConverter
		<com.liferay.change.tracking.model.CTRemote, CTRemote> {

	@Override
	public String getContentType() {
		return CTRemote.class.getSimpleName();
	}

	@Override
	public CTRemote toDTO(
			DTOConverterContext dtoConverterContext,
			com.liferay.change.tracking.model.CTRemote ctRemote)
		throws Exception {

		if (ctRemote == null) {
			return null;
		}

		return new CTRemote() {
			{
				setActions(dtoConverterContext::getActions);
				setClientId(ctRemote::getClientId);
				setClientSecret(ctRemote::getClientSecret);
				setDateCreated(ctRemote::getCreateDate);
				setDateModified(ctRemote::getModifiedDate);
				setDescription(ctRemote::getDescription);
				setId(ctRemote::getCtRemoteId);
				setName(ctRemote::getName);
				setOwnerName(ctRemote::getUserName);
				setUrl(ctRemote::getUrl);
			}
		};
	}

}