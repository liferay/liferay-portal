/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.mcp.server.rest.internal.upgrade;

import com.liferay.mcp.server.rest.internal.constants.MCPServerConstants;
import com.liferay.mcp.server.rest.internal.util.MCPServerProfileUtil;
import com.liferay.object.definition.util.ObjectDefinitionThreadLocal;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.model.ObjectRelationship;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.object.service.ObjectRelationshipLocalService;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.kernel.util.MapUtil;

import java.io.Serializable;

import java.util.Map;

/**
 * @author Jose Luis Navarro
 */
public class MCPProfileStatusUpgradeProcess extends UpgradeProcess {

	public MCPProfileStatusUpgradeProcess(
		CompanyLocalService companyLocalService,
		ObjectDefinitionLocalService objectDefinitionLocalService,
		ObjectEntryLocalService objectEntryLocalService,
		ObjectRelationshipLocalService objectRelationshipLocalService) {

		_companyLocalService = companyLocalService;
		_objectDefinitionLocalService = objectDefinitionLocalService;
		_objectEntryLocalService = objectEntryLocalService;
		_objectRelationshipLocalService = objectRelationshipLocalService;
	}

	@Override
	protected void doUpgrade() throws Exception {
		_companyLocalService.forEachCompanyId(this::_upgradeCompany);
	}

	private void _upgradeCompany(long companyId) throws Exception {
		ObjectDefinition objectDefinition =
			_objectDefinitionLocalService.
				fetchObjectDefinitionByExternalReferenceCode(
					MCPServerConstants.
						EXTERNAL_REFERENCE_CODE_MCP_SERVER_PROFILE,
					companyId);

		if (objectDefinition == null) {
			return;
		}

		ObjectRelationship objectRelationship =
			_objectRelationshipLocalService.fetchObjectRelationship(
				objectDefinition.getObjectDefinitionId(),
				"mcpServerProfileToTools");

		if (objectRelationship == null) {
			return;
		}

		try (SafeCloseable safeCloseable =
				ObjectDefinitionThreadLocal.
					setSkipBundleAllowedCheckWithSafeCloseable(true)) {

			for (ObjectEntry objectEntry :
					_objectEntryLocalService.getObjectEntries(
						0, objectDefinition.getObjectDefinitionId(),
						QueryUtil.ALL_POS, QueryUtil.ALL_POS)) {

				if (!MCPServerProfileUtil.isActive(objectEntry)) {
					continue;
				}

				int toolsCount =
					_objectEntryLocalService.getOneToManyObjectEntriesCount(
						0, objectRelationship.getObjectRelationshipId(), null,
						objectEntry.getObjectEntryId(), true, null);

				if (toolsCount > 0) {
					continue;
				}

				Map<String, Serializable> values =
					_objectEntryLocalService.getValues(objectEntry);

				if (_log.isInfoEnabled()) {
					_log.info(
						StringBundler.concat(
							"Deactivating MCP server profile \"",
							MapUtil.getString(values, "name"),
							"\" because it has no associated tools"));
				}

				values.put("profileStatus", "inactive");

				_objectEntryLocalService.updateObjectEntry(
					objectEntry.getUserId(), objectEntry.getObjectEntryId(),
					objectEntry.getObjectEntryFolderId(), values,
					new ServiceContext());
			}
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		MCPProfileStatusUpgradeProcess.class);

	private final CompanyLocalService _companyLocalService;
	private final ObjectDefinitionLocalService _objectDefinitionLocalService;
	private final ObjectEntryLocalService _objectEntryLocalService;
	private final ObjectRelationshipLocalService
		_objectRelationshipLocalService;

}