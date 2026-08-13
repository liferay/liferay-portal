/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.mcp.server.rest.internal.model.listener;

import com.liferay.mcp.server.rest.internal.constants.MCPServerConstants;
import com.liferay.object.constants.ObjectDefinitionConstants;
import com.liferay.object.constants.ObjectEntryFolderConstants;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.model.listener.RelevantObjectEntryModelListener;
import com.liferay.object.rest.filter.factory.FilterFactory;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.petra.sql.dsl.expression.Predicate;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.ModelListenerException;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.BaseModelListener;
import com.liferay.portal.kernel.search.Sort;
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

		for (ObjectEntry mcpServerProfileObjectEntry :
				_objectEntryLocalService.getObjectEntries(
					0, mcpServerProfileObjectDefinition.getObjectDefinitionId(),
					QueryUtil.ALL_POS, QueryUtil.ALL_POS)) {

			try {
				_addMCPServerProfileDataMaskObjectEntry(
					objectEntry, mcpServerProfileDataMaskObjectDefinition,
					mcpServerProfileObjectEntry);
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

		List<Long> primaryKeys = null;

		try {
			primaryKeys = _objectEntryLocalService.getPrimaryKeys(
				new Long[] {0L}, objectEntry.getCompanyId(),
				objectEntry.getUserId(),
				objectDefinition.getObjectDefinitionId(),
				_filterFactory.create(
					StringBundler.concat(
						"dataMaskExternalReferenceCode eq '",
						externalReferenceCode, "'"),
					objectDefinition),
				false, null, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
		}
		catch (PortalException portalException) {
			throw new ModelListenerException(portalException);
		}

		for (long primaryKey : primaryKeys) {
			try {
				ObjectEntry mcpServerProfileDataMaskObjectEntry =
					_objectEntryLocalService.getObjectEntry(primaryKey);

				Map<String, Serializable> newValues =
					HashMapBuilder.<String, Serializable>putAll(
						mcpServerProfileDataMaskObjectEntry.getValues()
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
							"Unable to delete profile data mask ", primaryKey,
							" for data mask ", externalReferenceCode),
						portalException);
				}
			}
		}
	}

	private void _addMCPServerProfileDataMaskObjectEntry(
			ObjectEntry dataMaskObjectEntry,
			ObjectDefinition mcpServerProfileDataMaskObjectDefinition,
			ObjectEntry mcpServerProfileObjectEntry)
		throws PortalException {

		long companyId = dataMaskObjectEntry.getCompanyId();
		long userId = dataMaskObjectEntry.getUserId();
		long objectDefinitionId =
			mcpServerProfileDataMaskObjectDefinition.getObjectDefinitionId();
		String dataMaskExternalReferenceCode =
			dataMaskObjectEntry.getExternalReferenceCode();
		String mcpServerProfileExternalReferenceCode =
			mcpServerProfileObjectEntry.getExternalReferenceCode();

		int count = _objectEntryLocalService.getValuesListCount(
			new Long[] {0L}, companyId, userId, objectDefinitionId,
			_filterFactory.create(
				StringBundler.concat(
					"(dataMaskExternalReferenceCode eq '",
					dataMaskExternalReferenceCode,
					"') and (mcpServerProfileExternalReferenceCode eq '",
					mcpServerProfileExternalReferenceCode, "')"),
				mcpServerProfileDataMaskObjectDefinition),
			false, null);

		if (count > 0) {
			return;
		}

		List<Map<String, Serializable>> valuesList =
			_objectEntryLocalService.getValuesList(
				0, companyId, userId, objectDefinitionId,
				_filterFactory.create(
					StringBundler.concat(
						"mcpServerProfileExternalReferenceCode eq '",
						mcpServerProfileExternalReferenceCode, "'"),
					mcpServerProfileDataMaskObjectDefinition),
				null, 0, 1,
				new Sort[] {new Sort("executionOrder", Sort.INT_TYPE, true)});

		int maxExecutionOrder = 0;

		if (!valuesList.isEmpty()) {
			maxExecutionOrder = MapUtil.getInteger(
				valuesList.get(0), "executionOrder");
		}

		_objectEntryLocalService.addObjectEntry(
			0, userId, objectDefinitionId,
			ObjectEntryFolderConstants.PARENT_OBJECT_ENTRY_FOLDER_ID_DEFAULT,
			null,
			HashMapBuilder.<String, Serializable>put(
				"dataMaskExternalReferenceCode", dataMaskExternalReferenceCode
			).put(
				"executionOrder", maxExecutionOrder + 1
			).put(
				"mcpServerProfileExternalReferenceCode",
				mcpServerProfileExternalReferenceCode
			).build(),
			new ServiceContext());
	}

	private static final Log _log = LogFactoryUtil.getLog(
		DataMaskObjectEntryModelListener.class);

	@Reference(
		target = "(filter.factory.key=" + ObjectDefinitionConstants.STORAGE_TYPE_DEFAULT + ")"
	)
	private FilterFactory<Predicate> _filterFactory;

	@Reference
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	@Reference
	private ObjectEntryLocalService _objectEntryLocalService;

}