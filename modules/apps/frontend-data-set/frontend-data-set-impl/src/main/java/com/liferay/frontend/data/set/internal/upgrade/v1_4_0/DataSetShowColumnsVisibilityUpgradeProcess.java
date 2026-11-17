/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.frontend.data.set.internal.upgrade.v1_4_0;

import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;

import java.io.Serializable;

import java.util.Map;

/**
 * @author Miguel Arroyo
 */
public class DataSetShowColumnsVisibilityUpgradeProcess extends UpgradeProcess {

	public DataSetShowColumnsVisibilityUpgradeProcess(
		CompanyLocalService companyLocalService,
		ObjectDefinitionLocalService objectDefinitionLocalService,
		ObjectEntryLocalService objectEntryLocalService) {

		_companyLocalService = companyLocalService;
		_objectDefinitionLocalService = objectDefinitionLocalService;
		_objectEntryLocalService = objectEntryLocalService;
	}

	@Override
	protected void doUpgrade() throws Exception {
		_companyLocalService.forEachCompanyId(
			this::_updateDataSetShowColumnsVisibility);
	}

	private void _updateDataSetShowColumnsVisibility(long companyId)
		throws PortalException {

		ObjectDefinition objectDefinition =
			_objectDefinitionLocalService.
				fetchObjectDefinitionByExternalReferenceCode(
					"L_DATA_SET", companyId);

		if (objectDefinition == null) {
			if (_log.isWarnEnabled()) {
				_log.warn(
					"\"L_DATA_SET\" object definition was not found for " +
						"company " + companyId);
			}

			return;
		}

		for (ObjectEntry objectEntry :
				_objectEntryLocalService.getObjectEntries(
					0, objectDefinition.getObjectDefinitionId(),
					QueryUtil.ALL_POS, QueryUtil.ALL_POS)) {

			Map<String, Serializable> values = objectEntry.getValues();

			if (Boolean.TRUE.equals(values.get("showColumnsVisibility"))) {
				continue;
			}

			values.put("showColumnsVisibility", Boolean.TRUE);

			_objectEntryLocalService.updateObjectEntry(
				objectEntry.getUserId(), objectEntry.getObjectEntryId(),
				objectEntry.getObjectEntryFolderId(), values,
				new ServiceContext());
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		DataSetShowColumnsVisibilityUpgradeProcess.class);

	private final CompanyLocalService _companyLocalService;
	private final ObjectDefinitionLocalService _objectDefinitionLocalService;
	private final ObjectEntryLocalService _objectEntryLocalService;

}