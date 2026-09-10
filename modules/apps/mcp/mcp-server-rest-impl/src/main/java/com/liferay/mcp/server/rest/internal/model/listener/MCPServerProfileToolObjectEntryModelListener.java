/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.mcp.server.rest.internal.model.listener;

import com.liferay.mcp.server.rest.internal.constants.MCPServerConstants;
import com.liferay.mcp.server.rest.internal.servlet.MCPServerServlet;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.model.ObjectRelationship;
import com.liferay.object.model.listener.RelevantObjectEntryModelListener;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.object.service.ObjectRelationshipLocalService;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.ModelListenerException;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.BaseModelListener;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;

import jakarta.servlet.Servlet;

import jakarta.validation.ValidationException;

import java.io.Serializable;

import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Alberto Javier Moreno Lage
 */
@Component(service = RelevantObjectEntryModelListener.class)
public class MCPServerProfileToolObjectEntryModelListener
	extends BaseModelListener<ObjectEntry>
	implements RelevantObjectEntryModelListener {

	@Override
	public String getObjectDefinitionExternalReferenceCode() {
		return MCPServerConstants.
			EXTERNAL_REFERENCE_CODE_MCP_SERVER_PROFILE_TOOL;
	}

	@Override
	public void onAfterCreate(ObjectEntry objectEntry)
		throws ModelListenerException {

		_invalidateServlet(objectEntry);
	}

	@Override
	public void onAfterUpdate(
			ObjectEntry originalObjectEntry, ObjectEntry objectEntry)
		throws ModelListenerException {

		_invalidateServlet(objectEntry);
	}

	@Override
	public void onBeforeCreate(ObjectEntry objectEntry)
		throws ModelListenerException {

		_validateRestrictFields(objectEntry);
		_validateTool(objectEntry);
	}

	@Override
	public void onBeforeRemove(ObjectEntry objectEntry)
		throws ModelListenerException {

		_invalidateServlet(objectEntry);
	}

	@Override
	public void onBeforeUpdate(
			ObjectEntry originalObjectEntry, ObjectEntry objectEntry)
		throws ModelListenerException {

		_validateRestrictFields(objectEntry);
		_validateTool(objectEntry);
	}

	private void _invalidateServlet(ObjectEntry objectEntry) {
		ObjectDefinition objectDefinition =
			_objectDefinitionLocalService.
				fetchObjectDefinitionByExternalReferenceCode(
					MCPServerConstants.
						EXTERNAL_REFERENCE_CODE_MCP_SERVER_PROFILE,
					objectEntry.getCompanyId());

		if (objectDefinition == null) {
			return;
		}

		ObjectEntry mcpServerProfileObjectEntry =
			_objectEntryLocalService.fetchObjectEntry(
				MapUtil.getString(
					objectEntry.getValues(),
					"r_mcpServerProfileToTools_l_mcpServerProfileERC"),
				0, objectDefinition.getObjectDefinitionId());

		if (mcpServerProfileObjectEntry == null) {
			return;
		}

		MCPServerServlet mcpServerServlet = (MCPServerServlet)_servlet;

		mcpServerServlet.invalidate(
			objectEntry.getCompanyId(),
			MapUtil.getString(mcpServerProfileObjectEntry.getValues(), "name"));
	}

	private void _validateRestrictFields(ObjectEntry objectEntry)
		throws ModelListenerException {

		String restrictFields = MapUtil.getString(
			objectEntry.getValues(), "restrictFields");

		if (Validator.isNull(restrictFields)) {
			return;
		}

		String[] restrictFieldNames = StringUtil.split(restrictFields);

		Set<String> uniqueRestrictFieldNames = new HashSet<>();

		for (String restrictFieldName : restrictFieldNames) {
			if (restrictFieldName.isEmpty() ||
				!Objects.equals(restrictFieldName, restrictFieldName.trim())) {

				throw new ModelListenerException(
					new ValidationException(
						StringBundler.concat(
							"Unable to restrict field \"", restrictFieldName,
							"\" because the name is blank or has surrounding ",
							"whitespace")));
			}

			Matcher matcher = _restrictFieldNamePattern.matcher(
				restrictFieldName);

			if (!matcher.matches()) {
				throw new ModelListenerException(
					new ValidationException(
						StringBundler.concat(
							"Unable to restrict field \"", restrictFieldName,
							"\" because the name is not a dotted path of ",
							"letters, digits, and underscores")));
			}

			if (!uniqueRestrictFieldNames.add(restrictFieldName)) {
				throw new ModelListenerException(
					new ValidationException(
						StringBundler.concat(
							"Unable to restrict field \"", restrictFieldName,
							"\" more than once")));
			}

			for (String ancestorFieldName : restrictFieldNames) {
				if (restrictFieldName.startsWith(
						ancestorFieldName + StringPool.PERIOD)) {

					throw new ModelListenerException(
						new ValidationException(
							StringBundler.concat(
								"Unable to restrict field \"",
								restrictFieldName,
								"\" because restricted field \"",
								ancestorFieldName, "\" already hides it")));
				}
			}
		}
	}

	private void _validateTool(ObjectEntry objectEntry)
		throws ModelListenerException {

		try {
			ObjectDefinition mcpServerProfileObjectDefinition =
				_objectDefinitionLocalService.
					fetchObjectDefinitionByExternalReferenceCode(
						MCPServerConstants.
							EXTERNAL_REFERENCE_CODE_MCP_SERVER_PROFILE,
						objectEntry.getCompanyId());

			ObjectRelationship objectRelationship =
				_objectRelationshipLocalService.getObjectRelationship(
					mcpServerProfileObjectDefinition.getObjectDefinitionId(),
					"mcpServerProfileToTools");

			Map<String, Serializable> values = objectEntry.getValues();

			String toolName = MapUtil.getString(values, "toolName");
			String toolSetName = MapUtil.getString(values, "toolSetName");

			for (ObjectEntry mcpServerProfileToolObjectEntry :
					_objectEntryLocalService.getOneToManyObjectEntries(
						0, objectRelationship.getObjectRelationshipId(), null,
						false,
						MapUtil.getLong(
							values,
							"r_mcpServerProfileToTools_l_mcpServerProfileId"),
						true, null, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
						null)) {

				if (mcpServerProfileToolObjectEntry.getObjectEntryId() ==
						objectEntry.getObjectEntryId()) {

					continue;
				}

				Map<String, Serializable> mcpServerProfileToolValues =
					mcpServerProfileToolObjectEntry.getValues();

				if (!Objects.equals(
						MapUtil.getString(
							mcpServerProfileToolValues, "toolName"),
						toolName) ||
					!Objects.equals(
						MapUtil.getString(
							mcpServerProfileToolValues, "toolSetName"),
						toolSetName)) {

					continue;
				}

				String mcpServerProfileExternalReferenceCode =
					MapUtil.getString(
						values,
						"r_mcpServerProfileToTools_l_mcpServerProfileERC");

				throw new ModelListenerException(
					new ValidationException(
						StringBundler.concat(
							"Unable to add tool \"", toolName,
							"\" from tool set \"", toolSetName,
							"\" to MCP server profile \"",
							mcpServerProfileExternalReferenceCode,
							"\" more than once")));
			}
		}
		catch (PortalException portalException) {
			throw new ModelListenerException(portalException);
		}
	}

	private static final Pattern _restrictFieldNamePattern = Pattern.compile(
		"[A-Za-z0-9_]+(\\.[A-Za-z0-9_]+)*");

	@Reference
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	@Reference
	private ObjectEntryLocalService _objectEntryLocalService;

	@Reference
	private ObjectRelationshipLocalService _objectRelationshipLocalService;

	@Reference(
		target = "(osgi.http.whiteboard.servlet.name=com.liferay.mcp.server.rest.internal.servlet.MCPServerServlet)"
	)
	private Servlet _servlet;

}