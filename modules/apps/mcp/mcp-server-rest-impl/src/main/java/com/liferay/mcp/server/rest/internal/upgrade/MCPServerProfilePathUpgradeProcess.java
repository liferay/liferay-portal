/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.mcp.server.rest.internal.upgrade;

import com.liferay.mcp.server.rest.internal.constants.MCPServerConstants;
import com.liferay.mcp.server.rest.internal.upgrade.util.MCPServerProfileUpgradeProcessUtil;
import com.liferay.object.constants.ObjectFieldConstants;
import com.liferay.object.constants.ObjectFieldSettingConstants;
import com.liferay.object.constants.ObjectValidationRuleConstants;
import com.liferay.object.definition.util.ObjectDefinitionThreadLocal;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.model.ObjectField;
import com.liferay.object.model.ObjectFieldSetting;
import com.liferay.object.model.ObjectValidationRule;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.object.service.ObjectFieldLocalService;
import com.liferay.object.service.ObjectFieldSettingLocalService;
import com.liferay.object.service.ObjectValidationRuleLocalService;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.Validator;

import java.io.Serializable;

import java.util.Collections;
import java.util.Map;

/**
 * @author Alejandro Tardín
 */
public class MCPServerProfilePathUpgradeProcess extends UpgradeProcess {

	public MCPServerProfilePathUpgradeProcess(
		CompanyLocalService companyLocalService,
		ObjectDefinitionLocalService objectDefinitionLocalService,
		ObjectEntryLocalService objectEntryLocalService,
		ObjectFieldLocalService objectFieldLocalService,
		ObjectFieldSettingLocalService objectFieldSettingLocalService,
		ObjectValidationRuleLocalService objectValidationRuleLocalService) {

		_companyLocalService = companyLocalService;
		_objectDefinitionLocalService = objectDefinitionLocalService;
		_objectEntryLocalService = objectEntryLocalService;
		_objectFieldLocalService = objectFieldLocalService;
		_objectFieldSettingLocalService = objectFieldSettingLocalService;
		_objectValidationRuleLocalService = objectValidationRuleLocalService;
	}

	@Override
	protected void doUpgrade() throws Exception {
		_companyLocalService.forEachCompanyId(this::_upgradeCompany);
	}

	private void _addObjectField(ObjectDefinition objectDefinition)
		throws PortalException {

		ObjectField objectField = _objectFieldLocalService.fetchObjectField(
			objectDefinition.getObjectDefinitionId(), "profilePath");

		if (objectField != null) {
			return;
		}

		ObjectFieldSetting objectFieldSetting =
			_objectFieldSettingLocalService.createObjectFieldSetting(0);

		objectFieldSetting.setName(
			ObjectFieldSettingConstants.NAME_UNIQUE_VALUES);
		objectFieldSetting.setValue("true");

		_objectFieldLocalService.addSystemObjectField(
			null, objectDefinition.getUserId(), 0,
			objectDefinition.getObjectDefinitionId(),
			ObjectFieldConstants.BUSINESS_TYPE_TEXT, null, null,
			ObjectFieldConstants.DB_TYPE_STRING, null, true, false, "en_US",
			Collections.singletonMap(LocaleUtil.US, "Profile Path"), false,
			"profilePath", ObjectFieldConstants.READ_ONLY_FALSE, null, false,
			false, Collections.singletonList(objectFieldSetting));
	}

	private void _addObjectValidationRule(ObjectDefinition objectDefinition)
		throws PortalException {

		ObjectValidationRule objectValidationRule =
			_objectValidationRuleLocalService.fetchObjectValidationRule(
				"L_MCP_SERVER_PROFILE_VALID_PROFILE_PATH",
				objectDefinition.getObjectDefinitionId());

		if (objectValidationRule != null) {
			return;
		}

		_objectValidationRuleLocalService.addObjectValidationRule(
			"L_MCP_SERVER_PROFILE_VALID_PROFILE_PATH",
			objectDefinition.getUserId(),
			objectDefinition.getObjectDefinitionId(), true,
			ObjectValidationRuleConstants.ENGINE_TYPE_DDM,
			Collections.singletonMap(
				LocaleUtil.US,
				"Please enter a valid profile path (lowercase letters and " +
					"numbers separated by single hyphens)."),
			Collections.singletonMap(LocaleUtil.US, "Valid Profile Path"),
			ObjectValidationRuleConstants.OUTPUT_TYPE_FULL_VALIDATION,
			"isEmpty(profilePath) OR match(profilePath, " +
				"'^[a-z0-9]+(-[a-z0-9]+)*$')",
			true, Collections.emptyList());
	}

	private void _updateObjectEntries(ObjectDefinition objectDefinition)
		throws PortalException {

		for (ObjectEntry objectEntry :
				_objectEntryLocalService.getObjectEntries(
					0, objectDefinition.getObjectDefinitionId(),
					QueryUtil.ALL_POS, QueryUtil.ALL_POS)) {

			Map<String, Serializable> values =
				_objectEntryLocalService.getValues(objectEntry);

			if (Validator.isNotNull(values.get("profilePath"))) {
				continue;
			}

			MCPServerProfileUpgradeProcessUtil.putProfilePath(
				objectEntry.getExternalReferenceCode(),
				objectDefinition.getObjectDefinitionId(),
				_objectFieldLocalService, values);

			_objectEntryLocalService.updateObjectEntry(
				objectEntry.getUserId(), objectEntry.getObjectEntryId(),
				objectEntry.getObjectEntryFolderId(), values,
				new ServiceContext());
		}
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

		try (SafeCloseable safeCloseable =
				ObjectDefinitionThreadLocal.
					setSkipBundleAllowedCheckWithSafeCloseable(true)) {

			_addObjectField(objectDefinition);
			_updateObjectEntries(objectDefinition);
			_addObjectValidationRule(objectDefinition);
		}
	}

	private final CompanyLocalService _companyLocalService;
	private final ObjectDefinitionLocalService _objectDefinitionLocalService;
	private final ObjectEntryLocalService _objectEntryLocalService;
	private final ObjectFieldLocalService _objectFieldLocalService;
	private final ObjectFieldSettingLocalService
		_objectFieldSettingLocalService;
	private final ObjectValidationRuleLocalService
		_objectValidationRuleLocalService;

}