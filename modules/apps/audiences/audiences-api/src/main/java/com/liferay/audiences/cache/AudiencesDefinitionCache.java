/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.audiences.cache;

import com.liferay.frontend.js.audiences.AudiencesDefinition;

/**
 * @author Víctor Galán
 */
public interface AudiencesDefinitionCache {

	public AudiencesDefinition getAudiencesDefinition(long companyId);

	public void putAudiencesDefinition(
		long companyId, AudiencesDefinition audiencesDefinition);

	public void removeAudiencesDefinition(long companyId);

}