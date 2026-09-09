/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.ai.hub.cell.authorization;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONObject;

/**
 * @author Rafael Uen
 */
public interface AIHubCellAuthorizationTokenProvider {

	public JSONObject getAuthorizationTokenJSONObject(
			long companyId, long userId)
		throws PortalException;

}
