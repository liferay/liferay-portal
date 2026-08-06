/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.mcp.server.rest.internal.model.listener;

import com.liferay.mcp.server.rest.internal.constants.MCPServerConstants;
import com.liferay.object.definition.util.ObjectDefinitionThreadLocal;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.model.ObjectField;
import com.liferay.object.model.ObjectRelationship;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.object.service.ObjectRelationshipLocalService;
import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.ModelListenerException;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.BaseModelListener;
import com.liferay.portal.kernel.model.ModelListener;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;

import java.io.Serializable;

import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Alberto Javier Moreno Lage
 */
@Component(service = ModelListener.class)
public class MCPServerProfileToolsObjectFieldModelListener
	extends BaseModelListener<ObjectField> {

	@Override
	public void onBeforeRemove(ObjectField objectField)
		throws ModelListenerException {

		if (!Objects.equals(objectField.getName(), "tools") ||
			ObjectDefinitionThreadLocal.isDeleteObjectDefinitionId(
				objectField.getObjectDefinitionId())) {

			return;
		}

		ObjectDefinition mcpServerProfileObjectDefinition =
			_objectDefinitionLocalService.fetchObjectDefinition(
				objectField.getObjectDefinitionId());

		if ((mcpServerProfileObjectDefinition == null) ||
			!Objects.equals(
				mcpServerProfileObjectDefinition.getExternalReferenceCode(),
				MCPServerConstants.
					EXTERNAL_REFERENCE_CODE_MCP_SERVER_PROFILE)) {

			return;
		}

		try {
			_verifyMCPServerProfileToolObjectEntries(
				mcpServerProfileObjectDefinition);
		}
		catch (PortalException portalException) {
			throw new ModelListenerException(portalException);
		}
	}

	private Set<String> _getExistingTools(
			ObjectEntry mcpServerProfileObjectEntry,
			ObjectRelationship objectRelationship)
		throws PortalException {

		Set<String> existingTools = new HashSet<>();

		if (objectRelationship == null) {
			return existingTools;
		}

		for (ObjectEntry mcpServerProfileToolObjectEntry :
				_objectEntryLocalService.getOneToManyObjectEntries(
					0, objectRelationship.getObjectRelationshipId(), null,
					false, mcpServerProfileObjectEntry.getObjectEntryId(), true,
					null, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null)) {

			Map<String, Serializable> values =
				mcpServerProfileToolObjectEntry.getValues();

			existingTools.add(
				StringBundler.concat(
					values.get("toolSetName"), StringPool.SPACE,
					values.get("toolName")));
		}

		return existingTools;
	}

	private void _verifyMCPServerProfileToolObjectEntries(
			ObjectDefinition mcpServerProfileObjectDefinition)
		throws PortalException {

		ObjectRelationship objectRelationship =
			_objectRelationshipLocalService.
				fetchObjectRelationshipByExternalReferenceCode(
					"L_MCP_SERVER_PROFILE_TO_L_MCP_SERVER_PROFILE_TOOL",
					mcpServerProfileObjectDefinition.getCompanyId(),
					mcpServerProfileObjectDefinition.getObjectDefinitionId());

		for (ObjectEntry mcpServerProfileObjectEntry :
				_objectEntryLocalService.getObjectEntries(
					0, mcpServerProfileObjectDefinition.getObjectDefinitionId(),
					QueryUtil.ALL_POS, QueryUtil.ALL_POS)) {

			Map<String, Serializable> mcpServerProfileValues =
				_objectEntryLocalService.getValues(mcpServerProfileObjectEntry);

			String tools = GetterUtil.getString(
				mcpServerProfileValues.get("tools"));

			if (Validator.isNull(tools)) {
				continue;
			}

			Set<String> existingTools = _getExistingTools(
				mcpServerProfileObjectEntry, objectRelationship);

			for (String tool : StringUtil.splitLines(tools)) {
				String[] tokens = StringUtil.split(tool, CharPool.SPACE);

				if ((tokens.length != 2) ||
					existingTools.contains(
						tokens[0] + StringPool.SPACE + tokens[1])) {

					continue;
				}

				throw new ModelListenerException(
					StringBundler.concat(
						"The tools object field cannot be deleted because the ",
						"profile tool \"", tool, "\" of profile ",
						mcpServerProfileObjectEntry.getObjectEntryId(),
						" was not migrated to an MCPServerProfileTool object ",
						"entry"));
			}
		}
	}

	@Reference
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	@Reference
	private ObjectEntryLocalService _objectEntryLocalService;

	@Reference
	private ObjectRelationshipLocalService _objectRelationshipLocalService;

}