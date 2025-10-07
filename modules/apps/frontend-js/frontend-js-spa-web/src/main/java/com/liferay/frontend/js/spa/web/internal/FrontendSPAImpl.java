/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.frontend.js.spa.web.internal;

import com.liferay.frontend.js.spa.web.internal.configuration.SPAConfiguration;
import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.configuration.module.configuration.ConfigurationProvider;
import com.liferay.portal.kernel.frontend.spa.FrontendSPA;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.module.configuration.ConfigurationException;

import java.util.Collections;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Bryce Osterhaus
 */
@Component(
	configurationPid = "com.liferay.frontend.js.spa.web.internal.configuration.SPAConfiguration",
	service = FrontendSPA.class
)
public class FrontendSPAImpl implements FrontendSPA {

	@Override
	public boolean isEnabled(long companyId) {
		SPAConfiguration spaConfiguration = _getSPAConfiguration(companyId);

		return spaConfiguration.enabled();
	}

	private SPAConfiguration _getSPAConfiguration(long companyId) {
		try {
			return _configurationProvider.getCompanyConfiguration(
				SPAConfiguration.class, companyId);
		}
		catch (ConfigurationException configurationException) {
			if (_log.isDebugEnabled()) {
				_log.debug(configurationException);
			}

			return _spaConfiguration;
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		FrontendSPAImpl.class);

	private static final SPAConfiguration _spaConfiguration =
		ConfigurableUtil.createConfigurable(
			SPAConfiguration.class, Collections.emptyMap());

	@Reference
	private ConfigurationProvider _configurationProvider;

}