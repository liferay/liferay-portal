/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.mcp.server.rest.internal.util;

import com.liferay.object.model.ObjectEntry;
import com.liferay.object.model.ObjectRelationship;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.object.service.ObjectRelationshipLocalService;
import com.liferay.portal.kernel.exception.PortalException;

import java.io.Serializable;

import java.util.Map;
import java.util.Objects;

/**
 * @author Jose Luis Navarro
 */
public class MCPServerProfileUtil {

	public static int getToolsCount(
			ObjectEntry mcpServerProfileObjectEntry,
			ObjectEntryLocalService objectEntryLocalService,
			ObjectRelationshipLocalService objectRelationshipLocalService)
		throws PortalException {

		ObjectRelationship objectRelationship =
			objectRelationshipLocalService.getObjectRelationship(
				mcpServerProfileObjectEntry.getObjectDefinitionId(),
				"mcpServerProfileToTools");

		return objectEntryLocalService.getOneToManyObjectEntriesCount(
			0, objectRelationship.getObjectRelationshipId(), null,
			mcpServerProfileObjectEntry.getObjectEntryId(), true, null);
	}

	public static boolean isActive(ObjectEntry mcpServerProfileObjectEntry) {
		Map<String, Serializable> values =
			mcpServerProfileObjectEntry.getValues();

		return Objects.equals(values.get("profileStatus"), "active");
	}

}