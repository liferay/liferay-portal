/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.mcp.server.rest.internal.upgrade.registry;

import com.liferay.list.type.service.ListTypeDefinitionLocalService;
import com.liferay.mcp.server.rest.internal.constants.MCPServerConstants;
import com.liferay.mcp.server.rest.internal.upgrade.MCPServerProfileDataMaskUpgradeProcess;
import com.liferay.mcp.server.rest.internal.upgrade.MCPServerProfilePathUpgradeProcess;
import com.liferay.mcp.server.rest.internal.upgrade.MCPServerProfileStatusUpgradeProcess;
import com.liferay.mcp.server.rest.internal.upgrade.MCPServerProfileToolUpgradeProcess;
import com.liferay.mcp.server.rest.internal.upgrade.MCPServerProfileUpgradeProcess;
import com.liferay.mcp.server.rest.internal.upgrade.MCPServerPromptUpgradeProcess;
import com.liferay.object.constants.ObjectDefinitionConstants;
import com.liferay.object.rest.filter.factory.FilterFactory;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.object.service.ObjectFieldLocalService;
import com.liferay.object.service.ObjectFieldSettingLocalService;
import com.liferay.object.service.ObjectFolderLocalService;
import com.liferay.object.service.ObjectRelationshipLocalService;
import com.liferay.object.service.ObjectValidationRuleLocalService;
import com.liferay.petra.sql.dsl.expression.Predicate;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.upgrade.UpgradeProcessFactory;
import com.liferay.portal.upgrade.registry.UpgradeStepRegistrator;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Jaime León
 */
@Component(service = UpgradeStepRegistrator.class)
public class MCPServerRestUpgradeStepRegistrator
	implements UpgradeStepRegistrator {

	@Override
	public void register(Registry registry) {
		registry.registerInitialization();

		registry.register(
			"0.0.1", "1.0.0",
			new MCPServerPromptUpgradeProcess(
				_companyLocalService, _listTypeDefinitionLocalService,
				_objectDefinitionLocalService, _objectEntryLocalService,
				_objectFieldLocalService, _objectFieldSettingLocalService,
				_objectValidationRuleLocalService));

		registry.register(
			"1.0.0", "1.1.0",
			new MCPServerProfileDataMaskUpgradeProcess(
				_companyLocalService, _filterFactory,
				_objectDefinitionLocalService, _objectEntryLocalService));

		registry.register(
			"1.1.0", "1.2.0",
			new MCPServerProfileUpgradeProcess(
				_companyLocalService, _listTypeDefinitionLocalService,
				_objectDefinitionLocalService, _objectEntryLocalService,
				_objectFieldLocalService, _objectFieldSettingLocalService,
				_objectFolderLocalService, _objectRelationshipLocalService));

		registry.register(
			"1.2.0", "1.3.0",
			new MCPServerProfileToolUpgradeProcess(
				_companyLocalService, _objectDefinitionLocalService,
				_objectFieldLocalService));

		registry.register(
			"1.3.0", "1.4.0",
			new MCPServerProfileStatusUpgradeProcess(
				_companyLocalService, _objectDefinitionLocalService,
				_objectEntryLocalService, _objectFieldLocalService,
				_objectRelationshipLocalService));

		registry.register(
			"1.4.0", "1.5.0",
			UpgradeProcessFactory.runSQL(
				StringBundler.concat(
					"update ObjectDefinition set panelCategoryKey = null ",
					"where externalReferenceCode in ('",
					MCPServerConstants.
						EXTERNAL_REFERENCE_CODE_MCP_SERVER_PROFILE,
					"', '",
					MCPServerConstants.
						EXTERNAL_REFERENCE_CODE_MCP_SERVER_PROFILE_DATA_MASK,
					"', '",
					MCPServerConstants.
						EXTERNAL_REFERENCE_CODE_MCP_SERVER_PROFILE_TOOL,
					"', '",
					MCPServerConstants.
						EXTERNAL_REFERENCE_CODE_MCP_SERVER_PROMPT,
					"')")));

		registry.register(
			"1.5.0", "1.6.0",
			new MCPServerProfilePathUpgradeProcess(
				_companyLocalService, _objectDefinitionLocalService,
				_objectEntryLocalService, _objectFieldLocalService,
				_objectFieldSettingLocalService,
				_objectValidationRuleLocalService));
	}

	@Reference
	private CompanyLocalService _companyLocalService;

	@Reference(
		target = "(filter.factory.key=" + ObjectDefinitionConstants.STORAGE_TYPE_DEFAULT + ")"
	)
	private FilterFactory<Predicate> _filterFactory;

	@Reference
	private ListTypeDefinitionLocalService _listTypeDefinitionLocalService;

	@Reference
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	@Reference
	private ObjectEntryLocalService _objectEntryLocalService;

	@Reference
	private ObjectFieldLocalService _objectFieldLocalService;

	@Reference
	private ObjectFieldSettingLocalService _objectFieldSettingLocalService;

	@Reference
	private ObjectFolderLocalService _objectFolderLocalService;

	@Reference
	private ObjectRelationshipLocalService _objectRelationshipLocalService;

	@Reference
	private ObjectValidationRuleLocalService _objectValidationRuleLocalService;

}