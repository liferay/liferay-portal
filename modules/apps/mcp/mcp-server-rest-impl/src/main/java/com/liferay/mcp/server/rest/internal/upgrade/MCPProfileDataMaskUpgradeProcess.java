/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.mcp.server.rest.internal.upgrade;

import com.liferay.mcp.server.rest.internal.constants.MCPServerConstants;
import com.liferay.object.constants.ObjectEntryFolderConstants;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.rest.filter.factory.FilterFactory;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.sql.dsl.expression.Predicate;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.MapUtil;

import java.io.Serializable;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Alberto Javier Moreno Lage
 */
public class MCPProfileDataMaskUpgradeProcess extends UpgradeProcess {

	public MCPProfileDataMaskUpgradeProcess(
		CompanyLocalService companyLocalService,
		FilterFactory<Predicate> filterFactory,
		ObjectDefinitionLocalService objectDefinitionLocalService,
		ObjectEntryLocalService objectEntryLocalService) {

		_companyLocalService = companyLocalService;
		_filterFactory = filterFactory;
		_objectDefinitionLocalService = objectDefinitionLocalService;
		_objectEntryLocalService = objectEntryLocalService;
	}

	@Override
	protected void doUpgrade() throws Exception {
		_companyLocalService.forEachCompanyId(this::_upgradeCompany);
	}

	private List<ObjectEntry> _getSystemDataMaskObjectEntries(
			ObjectDefinition dataMaskObjectDefinition)
		throws Exception {

		return TransformUtil.transform(
			_objectEntryLocalService.getPrimaryKeys(
				new Long[] {0L}, dataMaskObjectDefinition.getCompanyId(),
				dataMaskObjectDefinition.getUserId(),
				dataMaskObjectDefinition.getObjectDefinitionId(),
				_filterFactory.create(
					"maskType eq 'system'", dataMaskObjectDefinition),
				false, null, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
				new Sort[] {new Sort("id", Sort.LONG_TYPE, false)}),
			_objectEntryLocalService::getObjectEntry);
	}

	private void _upgradeCompany(long companyId) throws Exception {
		ObjectDefinition dataMaskObjectDefinition =
			_objectDefinitionLocalService.
				fetchObjectDefinitionByExternalReferenceCode(
					MCPServerConstants.EXTERNAL_REFERENCE_CODE_DATA_MASK,
					companyId);

		if (dataMaskObjectDefinition == null) {
			return;
		}

		List<ObjectEntry> systemDataMaskObjectEntries =
			_getSystemDataMaskObjectEntries(dataMaskObjectDefinition);

		if (systemDataMaskObjectEntries.isEmpty()) {
			return;
		}

		ObjectDefinition mcpServerProfileDataMaskObjectDefinition =
			_objectDefinitionLocalService.
				fetchObjectDefinitionByExternalReferenceCode(
					MCPServerConstants.
						EXTERNAL_REFERENCE_CODE_MCP_SERVER_PROFILE_DATA_MASK,
					companyId);

		if (mcpServerProfileDataMaskObjectDefinition == null) {
			return;
		}

		Map<String, Set<String>> dataMaskExternalReferenceCodesMap =
			new HashMap<>();
		Map<String, Integer> maxExecutionOrders = new HashMap<>();

		for (Map<String, Serializable> values :
				_objectEntryLocalService.getValuesList(
					0, companyId,
					mcpServerProfileDataMaskObjectDefinition.getUserId(),
					mcpServerProfileDataMaskObjectDefinition.
						getObjectDefinitionId(),
					null, null, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null)) {

			String mcpServerProfileExternalReferenceCode = GetterUtil.getString(
				values.get("mcpServerProfileExternalReferenceCode"));

			Set<String> dataMaskExternalReferenceCodes =
				dataMaskExternalReferenceCodesMap.computeIfAbsent(
					mcpServerProfileExternalReferenceCode,
					externalReferenceCode -> new HashSet<>());

			dataMaskExternalReferenceCodes.add(
				GetterUtil.getString(
					values.get("dataMaskExternalReferenceCode")));

			maxExecutionOrders.merge(
				mcpServerProfileExternalReferenceCode,
				MapUtil.getInteger(values, "executionOrder"), Math::max);
		}

		ObjectDefinition mcpServerProfileObjectDefinition =
			_objectDefinitionLocalService.
				fetchObjectDefinitionByExternalReferenceCode(
					MCPServerConstants.
						EXTERNAL_REFERENCE_CODE_MCP_SERVER_PROFILE,
					companyId);

		if (mcpServerProfileObjectDefinition == null) {
			return;
		}

		for (ObjectEntry mcpServerProfileObjectEntry :
				_objectEntryLocalService.getObjectEntries(
					0, mcpServerProfileObjectDefinition.getObjectDefinitionId(),
					QueryUtil.ALL_POS, QueryUtil.ALL_POS)) {

			String mcpServerProfileExternalReferenceCode =
				mcpServerProfileObjectEntry.getExternalReferenceCode();

			Set<String> dataMaskExternalReferenceCodes =
				dataMaskExternalReferenceCodesMap.getOrDefault(
					mcpServerProfileExternalReferenceCode,
					Collections.emptySet());

			int executionOrder = maxExecutionOrders.getOrDefault(
				mcpServerProfileExternalReferenceCode, 0);

			for (ObjectEntry systemDataMaskObjectEntry :
					systemDataMaskObjectEntries) {

				String dataMaskExternalReferenceCode =
					systemDataMaskObjectEntry.getExternalReferenceCode();

				if (dataMaskExternalReferenceCodes.contains(
						dataMaskExternalReferenceCode)) {

					continue;
				}

				executionOrder++;

				_objectEntryLocalService.addObjectEntry(
					0, systemDataMaskObjectEntry.getUserId(),
					mcpServerProfileDataMaskObjectDefinition.
						getObjectDefinitionId(),
					ObjectEntryFolderConstants.
						PARENT_OBJECT_ENTRY_FOLDER_ID_DEFAULT,
					null,
					HashMapBuilder.<String, Serializable>put(
						"dataMaskExternalReferenceCode",
						dataMaskExternalReferenceCode
					).put(
						"executionOrder", executionOrder
					).put(
						"mcpServerProfileExternalReferenceCode",
						mcpServerProfileExternalReferenceCode
					).build(),
					new ServiceContext());
			}
		}
	}

	private final CompanyLocalService _companyLocalService;
	private final FilterFactory<Predicate> _filterFactory;
	private final ObjectDefinitionLocalService _objectDefinitionLocalService;
	private final ObjectEntryLocalService _objectEntryLocalService;

}