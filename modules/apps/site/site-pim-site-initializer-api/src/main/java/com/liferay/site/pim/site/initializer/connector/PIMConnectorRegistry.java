/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.pim.site.initializer.connector;

import java.util.List;

import org.osgi.annotation.versioning.ProviderType;

/**
 * @author Andrea Sbarra
 */
@ProviderType
public interface PIMConnectorRegistry {

	public PIMConnector getConnector(String key);

	public List<PIMConnector> getConnectors(long companyId);

	public List<PIMConnector> getConnectors(long companyId, boolean activeOnly);

}