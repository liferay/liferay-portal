/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.mcp.server.rest.internal.model.listener;

import com.liferay.mcp.server.rest.internal.constants.MCPServerConstants;
import com.liferay.mcp.server.rest.internal.servlet.MCPServerServlet;
import com.liferay.object.constants.ObjectEntryFolderConstants;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.model.listener.RelevantObjectEntryModelListener;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.ModelListenerException;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.BaseModelListener;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.util.HashMapBuilder;

import jakarta.servlet.Servlet;

import java.io.Serializable;

import java.util.Map;
import java.util.Objects;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Alejandro Tardín
 */
@Component(service = RelevantObjectEntryModelListener.class)
public class MCPServerProfileObjectEntryModelListener
	extends BaseModelListener<ObjectEntry>
	implements RelevantObjectEntryModelListener {

	@Override
	public String getObjectDefinitionExternalReferenceCode() {
		return MCPServerConstants.EXTERNAL_REFERENCE_CODE_MCP_SERVER_PROFILE;
	}

	@Override
	public void onAfterCreate(ObjectEntry objectEntry)
		throws ModelListenerException {

		_invalidateServlet(objectEntry, _getName(objectEntry));

		_addMCPServerProfileDataMasks(objectEntry);
	}

	@Override
	public void onAfterRemove(ObjectEntry objectEntry)
		throws ModelListenerException {

		_invalidateServlet(objectEntry, _getName(objectEntry));
	}

	@Override
	public void onAfterUpdate(
			ObjectEntry originalObjectEntry, ObjectEntry objectEntry)
		throws ModelListenerException {

		_invalidateServlet(objectEntry, _getName(originalObjectEntry));
	}

	@Override
	public void onBeforeRemove(ObjectEntry objectEntry)
		throws ModelListenerException {

		ObjectDefinition profileDataMaskObjectDefinition =
			_objectDefinitionLocalService.
				fetchObjectDefinitionByExternalReferenceCode(
					MCPServerConstants.
						EXTERNAL_REFERENCE_CODE_MCP_SERVER_PROFILE_DATA_MASK,
					objectEntry.getCompanyId());

		if (profileDataMaskObjectDefinition == null) {
			return;
		}

		String mcpServerProfileExternalReferenceCode =
			objectEntry.getExternalReferenceCode();

		for (ObjectEntry profileDataMaskObjectEntry :
				_objectEntryLocalService.getObjectEntries(
					0, profileDataMaskObjectDefinition.getObjectDefinitionId(),
					QueryUtil.ALL_POS, QueryUtil.ALL_POS)) {

			Map<String, Serializable> values =
				profileDataMaskObjectEntry.getValues();

			if (!Objects.equals(
					mcpServerProfileExternalReferenceCode,
					values.get("mcpServerProfileExternalReferenceCode"))) {

				continue;
			}

			try {
				Map<String, Serializable> newValues =
					HashMapBuilder.<String, Serializable>putAll(
						values
					).put(
						"deleteReason", "Profile deleted."
					).build();

				_objectEntryLocalService.updateObjectEntry(
					profileDataMaskObjectEntry.getUserId(),
					profileDataMaskObjectEntry.getObjectEntryId(),
					profileDataMaskObjectEntry.getObjectEntryFolderId(),
					newValues, new ServiceContext());

				profileDataMaskObjectEntry.setValues(newValues);

				_objectEntryLocalService.deleteObjectEntry(
					profileDataMaskObjectEntry);
			}
			catch (PortalException portalException) {
				if (_log.isWarnEnabled()) {
					_log.warn(
						StringBundler.concat(
							"Unable to delete profile data mask ",
							profileDataMaskObjectEntry.getObjectEntryId(),
							" for profile ",
							mcpServerProfileExternalReferenceCode),
						portalException);
				}
			}
		}
	}

	private void _addMCPServerProfileDataMasks(ObjectEntry profileObjectEntry) {
		long companyId = profileObjectEntry.getCompanyId();

		ObjectDefinition maskObjectDefinition =
			_objectDefinitionLocalService.
				fetchObjectDefinitionByExternalReferenceCode(
					MCPServerConstants.EXTERNAL_REFERENCE_CODE_DATA_MASK,
					companyId);

		ObjectDefinition profileDataMaskObjectDefinition =
			_objectDefinitionLocalService.
				fetchObjectDefinitionByExternalReferenceCode(
					MCPServerConstants.
						EXTERNAL_REFERENCE_CODE_MCP_SERVER_PROFILE_DATA_MASK,
					companyId);

		if ((maskObjectDefinition == null) ||
			(profileDataMaskObjectDefinition == null)) {

			return;
		}

		int executionOrder = 1;

		for (ObjectEntry maskObjectEntry :
				_objectEntryLocalService.getObjectEntries(
					0, maskObjectDefinition.getObjectDefinitionId(),
					QueryUtil.ALL_POS, QueryUtil.ALL_POS)) {

			Map<String, Serializable> maskValues = maskObjectEntry.getValues();

			if (!Objects.equals(maskValues.get("maskType"), "system")) {
				continue;
			}

			try {
				_objectEntryLocalService.addObjectEntry(
					0, profileObjectEntry.getUserId(),
					profileDataMaskObjectDefinition.getObjectDefinitionId(),
					ObjectEntryFolderConstants.
						PARENT_OBJECT_ENTRY_FOLDER_ID_DEFAULT,
					null,
					HashMapBuilder.<String, Serializable>put(
						"dataMaskExternalReferenceCode",
						maskObjectEntry.getExternalReferenceCode()
					).put(
						"executionOrder", executionOrder
					).put(
						"mcpServerProfileExternalReferenceCode",
						profileObjectEntry.getExternalReferenceCode()
					).build(),
					new ServiceContext());

				executionOrder++;
			}
			catch (PortalException portalException) {
				if (_log.isWarnEnabled()) {
					_log.warn(
						StringBundler.concat(
							"Unable to attach system mask \"",
							maskValues.get("name"), "\" to profile ",
							profileObjectEntry.getObjectEntryId()),
						portalException);
				}
			}
		}
	}

	private String _getName(ObjectEntry objectEntry) {
		Map<String, Serializable> values = objectEntry.getValues();

		return (String)values.get("name");
	}

	private void _invalidateServlet(
		ObjectEntry profileObjectEntry, String profileName) {

		if (_servlet == null) {
			return;
		}

		MCPServerServlet mcpServerServlet = (MCPServerServlet)_servlet;

		mcpServerServlet.invalidate(
			profileObjectEntry.getCompanyId(), profileName);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		MCPServerProfileObjectEntryModelListener.class);

	@Reference
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	@Reference
	private ObjectEntryLocalService _objectEntryLocalService;

	@Reference(
		target = "(osgi.http.whiteboard.servlet.name=com.liferay.mcp.server.rest.internal.servlet.MCPServerServlet)"
	)
	private Servlet _servlet;

}