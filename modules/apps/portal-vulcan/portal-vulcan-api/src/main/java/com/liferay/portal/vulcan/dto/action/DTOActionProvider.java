/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.vulcan.dto.action;

import jakarta.ws.rs.core.UriInfo;

import java.util.Map;

/**
 * @author Carlos Correa
 */
public interface DTOActionProvider {

	public Map<String, ActionInfo> getActionInfos() throws Exception;

	public Map<String, Map<String, String>> getActions(
		long groupId, long primaryKey, UriInfo uriInfo, long userId);

}