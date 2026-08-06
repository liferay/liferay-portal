/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.mcp.server.rest.internal.model.listener;

import com.liferay.mcp.server.rest.internal.constants.MCPServerConstants;
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
import com.liferay.portal.kernel.util.MapUtil;

import java.io.Serializable;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Jose Luis Navarro
 */
@Component(service = RelevantObjectEntryModelListener.class)
public class DataMaskObjectEntryModelListener
	extends BaseModelListener<ObjectEntry>
	implements RelevantObjectEntryModelListener {

	@Override
	public String getObjectDefinitionExternalReferenceCode() {
		return MCPServerConstants.EXTERNAL_REFERENCE_CODE_DATA_MASK;
	}

	@Override
	public void onAfterCreate(ObjectEntry objectEntry)
		throws ModelListenerException {

		Map<String, Serializable> values = objectEntry.getValues();

		if (!Objects.equals(values.get("maskType"), "system")) {
			return;
		}

		long companyId = objectEntry.getCompanyId();

		ObjectDefinition mcpServerProfileObjectDefinition =
			_objectDefinitionLocalService.
				fetchObjectDefinitionByExternalReferenceCode(
					MCPServerConstants.
						EXTERNAL_REFERENCE_CODE_MCP_SERVER_PROFILE,
					companyId);

		ObjectDefinition mcpServerProfileDataMaskObjectDefinition =
			_objectDefinitionLocalService.
				fetchObjectDefinitionByExternalReferenceCode(
					MCPServerConstants.
						EXTERNAL_REFERENCE_CODE_MCP_SERVER_PROFILE_DATA_MASK,
					companyId);

		if ((mcpServerProfileObjectDefinition == null) ||
			(mcpServerProfileDataMaskObjectDefinition == null)) {

			return;
		}

		String externalReferenceCode = objectEntry.getExternalReferenceCode();

		List<ObjectEntry> mcpServerProfileDataMaskObjectEntries =
			_objectEntryLocalService.getObjectEntries(
				0,
				mcpServerProfileDataMaskObjectDefinition.
					getObjectDefinitionId(),
				QueryUtil.ALL_POS, QueryUtil.ALL_POS);

		for (ObjectEntry mcpServerProfileObjectEntry :
				_objectEntryLocalService.getObjectEntries(
					0, mcpServerProfileObjectDefinition.getObjectDefinitionId(),
					QueryUtil.ALL_POS, QueryUtil.ALL_POS)) {

			String mcpServerProfileExternalReferenceCode =
				mcpServerProfileObjectEntry.getExternalReferenceCode();

			boolean linked = false;
			int maxExecutionOrder = 0;

			for (ObjectEntry mcpServerProfileDataMaskObjectEntry :
					mcpServerProfileDataMaskObjectEntries) {

				Map<String, Serializable> mcpServerProfileDataMaskValues =
					mcpServerProfileDataMaskObjectEntry.getValues();

				if (!Objects.equals(
						mcpServerProfileDataMaskValues.get(
							"mcpServerProfileExternalReferenceCode"),
						mcpServerProfileExternalReferenceCode)) {

					continue;
				}

				if (Objects.equals(
						mcpServerProfileDataMaskValues.get(
							"dataMaskExternalReferenceCode"),
						externalReferenceCode)) {

					linked = true;

					break;
				}

				maxExecutionOrder = Math.max(
					maxExecutionOrder,
					MapUtil.getInteger(
						mcpServerProfileDataMaskValues, "executionOrder"));
			}

			if (linked) {
				continue;
			}

			try {
				_objectEntryLocalService.addObjectEntry(
					0, objectEntry.getUserId(),
					mcpServerProfileDataMaskObjectDefinition.
						getObjectDefinitionId(),
					ObjectEntryFolderConstants.
						PARENT_OBJECT_ENTRY_FOLDER_ID_DEFAULT,
					null,
					HashMapBuilder.<String, Serializable>put(
						"dataMaskExternalReferenceCode", externalReferenceCode
					).put(
						"executionOrder", maxExecutionOrder + 1
					).put(
						"mcpServerProfileExternalReferenceCode",
						mcpServerProfileExternalReferenceCode
					).build(),
					new ServiceContext());
			}
			catch (PortalException portalException) {
				if (_log.isWarnEnabled()) {
					_log.warn(
						StringBundler.concat(
							"Unable to attach system mask \"",
							values.get("name"), "\" to profile ",
							mcpServerProfileObjectEntry.getObjectEntryId()),
						portalException);
				}
			}
		}
	}

	@Override
	public void onBeforeRemove(ObjectEntry objectEntry)
		throws ModelListenerException {

		ObjectDefinition objectDefinition =
			_objectDefinitionLocalService.
				fetchObjectDefinitionByExternalReferenceCode(
					MCPServerConstants.
						EXTERNAL_REFERENCE_CODE_MCP_SERVER_PROFILE_DATA_MASK,
					objectEntry.getCompanyId());

		if (objectDefinition == null) {
			return;
		}

		String externalReferenceCode = objectEntry.getExternalReferenceCode();

		for (ObjectEntry mcpServerProfileDataMaskObjectEntry :
				_objectEntryLocalService.getObjectEntries(
					0, objectDefinition.getObjectDefinitionId(),
					QueryUtil.ALL_POS, QueryUtil.ALL_POS)) {

			Map<String, Serializable> values =
				mcpServerProfileDataMaskObjectEntry.getValues();

			if (!Objects.equals(
					values.get("dataMaskExternalReferenceCode"),
					externalReferenceCode)) {

				continue;
			}

			try {
				Map<String, Serializable> newValues =
					HashMapBuilder.<String, Serializable>putAll(
						values
					).put(
						"deleteReason", "Data mask was deleted."
					).build();

				_objectEntryLocalService.updateObjectEntry(
					mcpServerProfileDataMaskObjectEntry.getUserId(),
					mcpServerProfileDataMaskObjectEntry.getObjectEntryId(),
					mcpServerProfileDataMaskObjectEntry.
						getObjectEntryFolderId(),
					newValues, new ServiceContext());

				mcpServerProfileDataMaskObjectEntry.setValues(newValues);

				_objectEntryLocalService.deleteObjectEntry(
					mcpServerProfileDataMaskObjectEntry);
			}
			catch (PortalException portalException) {
				if (_log.isWarnEnabled()) {
					_log.warn(
						StringBundler.concat(
							"Unable to delete profile data mask ",
							mcpServerProfileDataMaskObjectEntry.
								getObjectEntryId(),
							" for data mask ", externalReferenceCode),
						portalException);
				}
			}
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		DataMaskObjectEntryModelListener.class);

	@Reference
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	@Reference
	private ObjectEntryLocalService _objectEntryLocalService;

}