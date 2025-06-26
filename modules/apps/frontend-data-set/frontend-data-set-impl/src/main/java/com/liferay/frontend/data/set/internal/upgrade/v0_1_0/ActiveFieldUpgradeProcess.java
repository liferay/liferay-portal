/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */


package com.liferay.frontend.data.set.internal.upgrade.v0_1_0;

import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;

import java.util.List;
import java.util.Map;

/**
 * @author Daniel Sanz
 */
public class ActiveFieldUpgradeProcess extends UpgradeProcess {

	public ActiveFieldUpgradeProcess(
		CompanyLocalService companyLocalService,
	 	ObjectDefinitionLocalService objectDefinitionLocalService,
		ObjectEntryLocalService objectEntryLocalService) {

		_companyLocalService = companyLocalService;
		_objectDefinitionLocalService = objectDefinitionLocalService;
		_objectEntryLocalService = objectEntryLocalService;
	}

	@Override
	protected void doUpgrade() throws Exception {
		System.out.println("1%$In doUpgrade");

		_companyLocalService.forEachCompanyId(
			companyId -> {
				for (String objectDefinitionERC : _OBJECT_DEFINITION_EXTERNAL_REFERENCE_CODES) {
					ObjectDefinition objectDefinition =
						_objectDefinitionLocalService.
							fetchObjectDefinitionByExternalReferenceCode(
								objectDefinitionERC, companyId);

					if (objectDefinition == null) {
						return;
					}

					List<ObjectEntry> objectEntries = _objectEntryLocalService.getObjectEntries(0, objectDefinition.getObjectDefinitionId(), -1, -1);

					objectEntries.forEach(objectEntry -> {

						String tableName = "L_" + companyId + "_DataSet_x";

						try {
							if (!hasColumn(tableName, "active_")) {
								runSQL("ALTER TABLE " + tableName +" ADD COLUMN active_ BOOLEAN");
							}

							runSQL("UPDATE " + tableName + " SET active_ = TRUE WHERE l_dataSetId_ = " + objectEntry.getObjectEntryId());

						} catch (Exception e) {
							_log.error("Error upgrading table " + tableName + ": " + e.getMessage(), e);
						}

					});



				}
			});
	}

	private static final String[] _OBJECT_DEFINITION_EXTERNAL_REFERENCE_CODES = {
		"L_DATA_SET"
	};

	private static final Log _log = LogFactoryUtil.getLog(
		ActiveFieldUpgradeProcess.class);
	private final CompanyLocalService _companyLocalService;
	private final ObjectDefinitionLocalService _objectDefinitionLocalService;
	private final ObjectEntryLocalService _objectEntryLocalService;
}
