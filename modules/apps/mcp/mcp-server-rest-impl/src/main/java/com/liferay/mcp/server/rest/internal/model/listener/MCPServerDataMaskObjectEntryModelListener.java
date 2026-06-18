/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.mcp.server.rest.internal.model.listener;

import com.liferay.mcp.server.rest.internal.constants.MCPServerConstants;
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

import java.io.Serializable;

import java.util.Map;
import java.util.Objects;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Jose Luis Navarro
 */
@Component(service = RelevantObjectEntryModelListener.class)
public class MCPServerDataMaskObjectEntryModelListener
	extends BaseModelListener<ObjectEntry>
	implements RelevantObjectEntryModelListener {

	@Override
	public String getObjectDefinitionExternalReferenceCode() {
		return MCPServerConstants.EXTERNAL_REFERENCE_CODE_DATA_MASK;
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

		String maskExternalReferenceCode =
			objectEntry.getExternalReferenceCode();

		for (ObjectEntry profileDataMaskObjectEntry :
				_objectEntryLocalService.getObjectEntries(
					0, profileDataMaskObjectDefinition.getObjectDefinitionId(),
					QueryUtil.ALL_POS, QueryUtil.ALL_POS)) {

			Map<String, Serializable> values =
				profileDataMaskObjectEntry.getValues();

			if (!Objects.equals(
					values.get("dataMaskExternalReferenceCode"),
					maskExternalReferenceCode)) {

				continue;
			}

			try {
				Map<String, Serializable> newValues =
					HashMapBuilder.<String, Serializable>putAll(
						values
					).put(
						"deleteReason", "Data mask deleted."
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
							" for data mask ", maskExternalReferenceCode),
						portalException);
				}
			}
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		MCPServerDataMaskObjectEntryModelListener.class);

	@Reference
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	@Reference
	private ObjectEntryLocalService _objectEntryLocalService;

}