/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.saml.runtime.configuration;

import com.liferay.osgi.util.ServiceTrackerFactory;

import org.osgi.framework.FrameworkUtil;
import org.osgi.util.tracker.ServiceTracker;

/**
 * @author Mika Koivisto
 */
public class SamlProviderConfigurationHelperUtil {

	public static SamlProviderConfiguration getSamlProviderConfiguration() {
		SamlProviderConfigurationHelper samlProviderConfigurationHelper =
			getSamlProviderConfigurationHelper();

		return samlProviderConfigurationHelper.getSamlProviderConfiguration();
	}

	public static SamlProviderConfigurationHelper
		getSamlProviderConfigurationHelper() {

		return _serviceTracker.getService();
	}

	public static boolean isEnabled() {
		SamlProviderConfigurationHelper samlProviderConfigurationHelper =
			getSamlProviderConfigurationHelper();

		return samlProviderConfigurationHelper.isEnabled();
	}

	public static boolean isLDAPImportEnabled() {
		SamlProviderConfigurationHelper samlProviderConfigurationHelper =
			getSamlProviderConfigurationHelper();

		return samlProviderConfigurationHelper.isLDAPImportEnabled();
	}

	public static boolean isRoleIdp() {
		SamlProviderConfigurationHelper samlProviderConfigurationHelper =
			getSamlProviderConfigurationHelper();

		return samlProviderConfigurationHelper.isRoleIdp();
	}

	public static boolean isRoleSp() {
		SamlProviderConfigurationHelper samlProviderConfigurationHelper =
			getSamlProviderConfigurationHelper();

		return samlProviderConfigurationHelper.isRoleSp();
	}

	//	public static boolean isRoleIdpAndSp() {

	// 		SamlProviderConfigurationHelper samlProviderConfigurationHelper =

	//			getSamlProviderConfigurationHelper();

	//
	//		return samlProviderConfigurationHelper.isRoleIdpAndSp();
	//	}

	private static final ServiceTracker
		<SamlProviderConfigurationHelper, SamlProviderConfigurationHelper>
			_serviceTracker = ServiceTrackerFactory.open(
				FrameworkUtil.getBundle(
					SamlProviderConfigurationHelperUtil.class),
				SamlProviderConfigurationHelper.class);

}