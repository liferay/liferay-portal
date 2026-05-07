/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.oauth.client.persistence.service.impl;

import com.liferay.oauth.client.persistence.service.base.OAuthClientPRLocalMetadataLocalServiceBaseImpl;
import com.liferay.portal.aop.AopService;

import org.osgi.service.component.annotations.Component;

/**
 * @author Brian Wing Shun Chan
 */
@Component(
	property = "model.class.name=com.liferay.oauth.client.persistence.model.OAuthClientPRLocalMetadata",
	service = AopService.class
)
public class OAuthClientPRLocalMetadataLocalServiceImpl
	extends OAuthClientPRLocalMetadataLocalServiceBaseImpl {
}

// LIFERAY-SERVICE-BUILDER-HASH:-2102663966