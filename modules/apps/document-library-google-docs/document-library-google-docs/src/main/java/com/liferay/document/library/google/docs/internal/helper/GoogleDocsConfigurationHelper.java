/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.document.library.google.docs.internal.helper;

import com.liferay.document.library.google.drive.configuration.DLGoogleDriveCompanyConfiguration;
import com.liferay.portal.configuration.module.configuration.ConfigurationProviderUtil;
import com.liferay.portal.kernel.module.configuration.ConfigurationException;
import com.liferay.portal.security.key.secret.SecretResolverUtil;

/**
 * @author Iván Zaera
 */
public class GoogleDocsConfigurationHelper {

	public GoogleDocsConfigurationHelper(long companyId)
		throws ConfigurationException {

		_companyId = companyId;

		_dlGoogleDriveCompanyConfiguration =
			ConfigurationProviderUtil.getCompanyConfiguration(
				DLGoogleDriveCompanyConfiguration.class, companyId);
	}

	public String getGoogleAppsAPIKey() {
		return SecretResolverUtil.resolve(
			_companyId, _dlGoogleDriveCompanyConfiguration.pickerAPIKey());
	}

	public String getGoogleClientId() {
		return _dlGoogleDriveCompanyConfiguration.clientId();
	}

	private final long _companyId;
	private final DLGoogleDriveCompanyConfiguration
		_dlGoogleDriveCompanyConfiguration;

}