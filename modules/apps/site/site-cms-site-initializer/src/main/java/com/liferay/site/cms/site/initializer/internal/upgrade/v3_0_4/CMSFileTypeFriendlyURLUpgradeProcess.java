/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.cms.site.initializer.internal.upgrade.v3_0_4;

import com.liferay.object.constants.ObjectFolderConstants;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.model.ObjectFolder;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.object.service.ObjectFolderLocalService;
import com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery;
import com.liferay.portal.kernel.dao.orm.RestrictionsFactoryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.site.cms.site.initializer.internal.util.CMSFileTypeUtil;

/**
 * @author Mikel Lorza
 */
public class CMSFileTypeFriendlyURLUpgradeProcess extends UpgradeProcess {

	public CMSFileTypeFriendlyURLUpgradeProcess(
		CompanyLocalService companyLocalService,
		ObjectDefinitionLocalService objectDefinitionLocalService,
		ObjectEntryLocalService objectEntryLocalService,
		ObjectFolderLocalService objectFolderLocalService) {

		_companyLocalService = companyLocalService;
		_objectDefinitionLocalService = objectDefinitionLocalService;
		_objectEntryLocalService = objectEntryLocalService;
		_objectFolderLocalService = objectFolderLocalService;
	}

	@Override
	protected void doUpgrade() throws Exception {
		_companyLocalService.forEachCompanyId(this::_upgradeCompany);
	}

	private void _upgradeCompany(long companyId) throws PortalException {
		ObjectFolder objectFolder =
			_objectFolderLocalService.fetchObjectFolderByExternalReferenceCode(
				ObjectFolderConstants.EXTERNAL_REFERENCE_CODE_FILE_TYPES,
				companyId);

		if (objectFolder == null) {
			return;
		}

		for (ObjectDefinition objectDefinition :
				_objectDefinitionLocalService.getObjectFolderObjectDefinitions(
					objectFolder.getObjectFolderId())) {

			if (CMSFileTypeUtil.hasFileObjectField(objectDefinition)) {
				_upgradeObjectDefinition(objectDefinition);
			}
		}
	}

	private void _upgradeObjectDefinition(ObjectDefinition objectDefinition)
		throws PortalException {

		ActionableDynamicQuery actionableDynamicQuery =
			_objectEntryLocalService.getActionableDynamicQuery();

		actionableDynamicQuery.setAddCriteriaMethod(
			dynamicQuery -> dynamicQuery.add(
				RestrictionsFactoryUtil.eq(
					"objectDefinitionId",
					objectDefinition.getObjectDefinitionId())));
		actionableDynamicQuery.setPerformActionMethod(
			(ObjectEntry objectEntry) ->
				CMSFileTypeUtil.updateFileEntryFriendlyURL(
					objectDefinition, objectEntry));

		actionableDynamicQuery.performActions();
	}

	private final CompanyLocalService _companyLocalService;
	private final ObjectDefinitionLocalService _objectDefinitionLocalService;
	private final ObjectEntryLocalService _objectEntryLocalService;
	private final ObjectFolderLocalService _objectFolderLocalService;

}