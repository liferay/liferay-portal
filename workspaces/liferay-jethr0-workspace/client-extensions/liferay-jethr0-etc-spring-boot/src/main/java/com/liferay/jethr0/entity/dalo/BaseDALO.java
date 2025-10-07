/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jethr0.entity.dalo;

import com.liferay.client.extension.util.spring.boot3.BaseRestController;
import com.liferay.client.extension.util.spring.boot3.client.LiferayOAuth2AccessTokenManager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

/**
 * @author Michael Hashimoto
 */
@Configuration
public abstract class BaseDALO extends BaseRestController implements DALO {

	@Override
	public String getAuthorization() {
		return _liferayOAuth2AccessTokenManager.getAuthorization(
			"liferay-jethr0-etc-spring-boot-oahs");
	}

	@Override
	public void refresh() {
		_liferayOAuth2AccessTokenManager.refresh(
			"liferay-jethr0-etc-spring-boot-oahs");
	}

	@Autowired
	private LiferayOAuth2AccessTokenManager _liferayOAuth2AccessTokenManager;

}