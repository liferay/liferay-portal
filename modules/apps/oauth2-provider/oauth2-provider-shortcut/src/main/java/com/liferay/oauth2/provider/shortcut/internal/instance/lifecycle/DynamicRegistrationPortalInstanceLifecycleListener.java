/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.oauth2.provider.shortcut.internal.instance.lifecycle;

import com.liferay.oauth2.provider.service.OAuth2ApplicationLocalService;
import com.liferay.portal.instance.lifecycle.BasePortalInstanceLifecycleListener;
import com.liferay.portal.instance.lifecycle.PortalInstanceLifecycleListener;
import com.liferay.portal.kernel.model.Company;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Jorge García Jiménez
 */
@Component(service = PortalInstanceLifecycleListener.class)
public class DynamicRegistrationPortalInstanceLifecycleListener
	extends BasePortalInstanceLifecycleListener {

	@Override
	public void portalInstanceRegistered(Company company) throws Exception {
		_oAuth2ApplicationLocalService.checkDynamicRegistratorOAuth2Application(
			company.getCompanyId());
	}

	@Reference
	private OAuth2ApplicationLocalService _oAuth2ApplicationLocalService;

}