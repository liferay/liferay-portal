/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.document.library.internal.expiration;

import com.liferay.document.library.configuration.DLConfiguration;
import com.liferay.document.library.kernel.expiration.ExpireAllVersions;
import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;

import java.util.Map;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;

/**
 * @author Roberto Díaz
 */
@Component(
	configurationPid = "com.liferay.document.library.configuration.DLConfiguration",
	service = ExpireAllVersions.class
)
public class ExpireAllVersionsImpl implements ExpireAllVersions {

	@Override
	public boolean isEnabled() {
		return _dlConfiguration.expireAllVersionsEnabled();
	}

	@Activate
	@Modified
	protected void activate(Map<String, Object> properties) {
		_dlConfiguration = ConfigurableUtil.createConfigurable(
			DLConfiguration.class, properties);
	}

	private volatile DLConfiguration _dlConfiguration;

}