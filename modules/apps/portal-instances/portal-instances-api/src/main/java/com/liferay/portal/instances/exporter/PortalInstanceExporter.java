/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.instances.exporter;

import org.osgi.annotation.versioning.ProviderType;

/**
 * @author Jorge Avalos
 */
@ProviderType
public interface PortalInstanceExporter {

	public String exportPortalInstance(long companyId) throws Exception;

}