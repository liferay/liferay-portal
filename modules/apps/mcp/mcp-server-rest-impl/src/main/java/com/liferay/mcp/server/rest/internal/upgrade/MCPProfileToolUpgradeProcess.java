/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.mcp.server.rest.internal.upgrade;

import com.liferay.mcp.server.rest.internal.constants.MCPServerConstants;
import com.liferay.object.constants.ObjectFieldConstants;
import com.liferay.object.definition.util.ObjectDefinitionThreadLocal;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectField;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectFieldLocalService;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.kernel.util.LocaleUtil;

import java.util.Collections;

/**
 * @author Alberto Javier Moreno Lage
 */
public class MCPProfileToolUpgradeProcess extends UpgradeProcess {

	public MCPProfileToolUpgradeProcess(
		CompanyLocalService companyLocalService,
		ObjectDefinitionLocalService objectDefinitionLocalService,
		ObjectFieldLocalService objectFieldLocalService) {

		_companyLocalService = companyLocalService;
		_objectDefinitionLocalService = objectDefinitionLocalService;
		_objectFieldLocalService = objectFieldLocalService;
	}

	@Override
	protected void doUpgrade() throws Exception {
		_companyLocalService.forEachCompanyId(this::_upgradeCompany);
	}

	private void _addRestrictFieldsObjectField(
			ObjectDefinition objectDefinition)
		throws PortalException {

		ObjectField restrictFieldsObjectField =
			_objectFieldLocalService.fetchObjectField(
				objectDefinition.getObjectDefinitionId(), "restrictFields");

		if (restrictFieldsObjectField != null) {
			return;
		}

		try {
			_objectFieldLocalService.addSystemObjectField(
				null, objectDefinition.getUserId(), 0,
				objectDefinition.getObjectDefinitionId(),
				ObjectFieldConstants.BUSINESS_TYPE_LONG_TEXT, null, null,
				ObjectFieldConstants.DB_TYPE_CLOB, true, false, "en_US",
				Collections.singletonMap(LocaleUtil.US, "Restrict Fields"),
				false, "restrictFields", ObjectFieldConstants.READ_ONLY_FALSE,
				null, false, false, Collections.emptyList());
		}
		catch (PortalException portalException) {
			restrictFieldsObjectField =
				_objectFieldLocalService.fetchObjectField(
					objectDefinition.getObjectDefinitionId(), "restrictFields");

			if (restrictFieldsObjectField == null) {
				throw portalException;
			}

			if (_log.isDebugEnabled()) {
				_log.debug(portalException);
			}
		}
	}

	private void _upgradeCompany(long companyId) throws Exception {
		ObjectDefinition objectDefinition =
			_objectDefinitionLocalService.
				fetchObjectDefinitionByExternalReferenceCode(
					MCPServerConstants.
						EXTERNAL_REFERENCE_CODE_MCP_SERVER_PROFILE_TOOL,
					companyId);

		if (objectDefinition == null) {
			return;
		}

		try (SafeCloseable safeCloseable =
				ObjectDefinitionThreadLocal.
					setSkipBundleAllowedCheckWithSafeCloseable(true)) {

			_addRestrictFieldsObjectField(objectDefinition);
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		MCPProfileToolUpgradeProcess.class);

	private final CompanyLocalService _companyLocalService;
	private final ObjectDefinitionLocalService _objectDefinitionLocalService;
	private final ObjectFieldLocalService _objectFieldLocalService;

}