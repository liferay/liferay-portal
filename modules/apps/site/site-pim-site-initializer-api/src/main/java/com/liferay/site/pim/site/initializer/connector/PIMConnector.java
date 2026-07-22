/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.pim.site.initializer.connector;

import java.util.Locale;

import org.osgi.annotation.versioning.ProviderType;

/**
 * @author Andrea Sbarra
 */
@ProviderType
public interface PIMConnector {

	public String getKey();

	public String getName(Locale locale);

	public boolean isActive(long companyId);

}