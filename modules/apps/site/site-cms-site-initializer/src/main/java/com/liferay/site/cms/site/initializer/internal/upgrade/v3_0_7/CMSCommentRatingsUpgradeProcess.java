/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.cms.site.initializer.internal.upgrade.v3_0_7;

import com.liferay.object.constants.ObjectFolderConstants;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectFolder;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectFolderLocalService;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.service.ClassNameLocalService;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;

/**
 * @author Jürgen Kappler
 */
public class CMSCommentRatingsUpgradeProcess extends UpgradeProcess {

	public CMSCommentRatingsUpgradeProcess(
		ClassNameLocalService classNameLocalService,
		CompanyLocalService companyLocalService,
		ObjectDefinitionLocalService objectDefinitionLocalService,
		ObjectFolderLocalService objectFolderLocalService) {

		_classNameLocalService = classNameLocalService;
		_companyLocalService = companyLocalService;
		_objectDefinitionLocalService = objectDefinitionLocalService;
		_objectFolderLocalService = objectFolderLocalService;
	}

	@Override
	protected void doUpgrade() throws Exception {
		_companyLocalService.forEachCompanyId(this::_upgradeCompany);
	}

	private void _upgradeCompany(long companyId) throws Exception {
		_upgradeObjectFolder(
			companyId,
			ObjectFolderConstants.EXTERNAL_REFERENCE_CODE_CONTENT_STRUCTURES);
		_upgradeObjectFolder(
			companyId,
			ObjectFolderConstants.EXTERNAL_REFERENCE_CODE_FILE_TYPES);
	}

	private void _upgradeObjectFolder(
			long companyId, String externalReferenceCode)
		throws Exception {

		ObjectFolder objectFolder =
			_objectFolderLocalService.fetchObjectFolderByExternalReferenceCode(
				externalReferenceCode, companyId);

		if (objectFolder == null) {
			return;
		}

		long mbDiscussionClassNameId = _classNameLocalService.getClassNameId(
			"com.liferay.message.boards.model.MBDiscussion");

		for (ObjectDefinition objectDefinition :
				_objectDefinitionLocalService.getObjectFolderObjectDefinitions(
					objectFolder.getObjectFolderId())) {

			long classNameId = _classNameLocalService.getClassNameId(
				objectDefinition.getClassName());

			_upgradeTable(classNameId, mbDiscussionClassNameId, "RatingsEntry");
			_upgradeTable(classNameId, mbDiscussionClassNameId, "RatingsStats");
		}
	}

	private void _upgradeTable(
			long classNameId, long mbDiscussionClassNameId, String tableName)
		throws Exception {

		runSQL(
			StringBundler.concat(
				"update ", tableName, " set classNameId = ",
				mbDiscussionClassNameId, " where classNameId = ", classNameId,
				" and classPK in (select messageId from MBMessage where ",
				"classNameId = ", classNameId, ")"));
	}

	private final ClassNameLocalService _classNameLocalService;
	private final CompanyLocalService _companyLocalService;
	private final ObjectDefinitionLocalService _objectDefinitionLocalService;
	private final ObjectFolderLocalService _objectFolderLocalService;

}