/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.capabilities;

import org.osgi.annotation.versioning.ProviderType;

/**
 * @author Rodrigo Guedes de Souza
 */
@ProviderType
public interface ElasticsearchLicenseInformation {

	public boolean isInferenceLicenseAvailable();

}