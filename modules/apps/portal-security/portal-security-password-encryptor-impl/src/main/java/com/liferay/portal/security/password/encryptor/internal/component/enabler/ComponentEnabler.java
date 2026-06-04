/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.password.encryptor.internal.component.enabler;

import com.liferay.portal.kernel.util.PropsValues;
import com.liferay.portal.security.password.encryptor.internal.BCryptPasswordEncryptor;
import com.liferay.portal.security.password.encryptor.internal.CryptPasswordEncryptor;

import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;

/**
 * @author Caio Farias
 */
@Component(service = {})
public class ComponentEnabler {

	@Activate
	protected void activate(ComponentContext componentContext) {
		if (!PropsValues.FIPS_ENABLED) {
			componentContext.enableComponent(
				BCryptPasswordEncryptor.class.getName());
			componentContext.enableComponent(
				CryptPasswordEncryptor.class.getName());
		}
	}

}