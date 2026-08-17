/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.internal.upgrade.v13_3_0;

import com.liferay.object.constants.ObjectFieldConstants;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.jdbc.AutoBatchPreparedStatementUtil;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.model.ResourceAction;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.service.ResourceActionLocalService;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.Localization;
import com.liferay.portal.kernel.util.TextFormatter;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.language.override.service.PLOEntryLocalService;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

import java.util.Collections;
import java.util.Locale;

/**
 * @author Manuele Castro
 */
public class AttachmentObjectFieldDownloadPermissionUpgradeProcess
	extends UpgradeProcess {

	public AttachmentObjectFieldDownloadPermissionUpgradeProcess(
		Language language, Localization localization,
		PLOEntryLocalService ploEntryLocalService,
		ResourceActionLocalService resourceActionLocalService) {

		_language = language;
		_localization = localization;
		_ploEntryLocalService = ploEntryLocalService;
		_resourceActionLocalService = resourceActionLocalService;
	}

	@Override
	protected void doUpgrade() throws Exception {
		try (PreparedStatement preparedStatement = connection.prepareStatement(
				StringBundler.concat(
					"select ObjectDefinition.className, ",
					"ObjectField.companyId, ObjectField.userId, ",
					"ObjectField.label, ObjectField.name from ObjectField ",
					"inner join ObjectDefinition on ",
					"ObjectDefinition.objectDefinitionId = ",
					"ObjectField.objectDefinitionId where ",
					"ObjectField.businessType = ? and ObjectDefinition.status ",
					"= ? and not (ObjectDefinition.system_ = ? and ",
					"ObjectDefinition.modifiable = ?)"))) {

			preparedStatement.setString(
				1, ObjectFieldConstants.BUSINESS_TYPE_ATTACHMENT);
			preparedStatement.setInt(2, WorkflowConstants.STATUS_APPROVED);
			preparedStatement.setBoolean(3, true);
			preparedStatement.setBoolean(4, false);

			try (ResultSet resultSet = preparedStatement.executeQuery()) {
				while (resultSet.next()) {
					_upgradeAttachmentObjectField(
						resultSet.getString("className"),
						resultSet.getLong("companyId"),
						resultSet.getString("label"),
						resultSet.getString("name"),
						resultSet.getLong("userId"));
				}
			}
		}
	}

	private void _addOrUpdatePLOEntries(
			String actionId, long companyId, String label, long userId)
		throws Exception {

		for (Locale locale : _language.getCompanyAvailableLocales(companyId)) {
			String languageId = LocaleUtil.toLanguageId(locale);

			_ploEntryLocalService.addOrUpdatePLOEntry(
				null, companyId, userId, "action." + actionId, languageId,
				_language.format(
					locale, "download-x",
					_localization.getLocalization(label, languageId)));
		}
	}

	private String _getAttachmentDownloadActionKey(String name) {
		return StringBundler.concat(
			ActionKeys.DOWNLOAD, StringPool.UNDERLINE,
			TextFormatter.format(name, TextFormatter.R));
	}

	private void _updateResourcePermissions(
			long bitwiseValue, String className, long companyId,
			long viewBitwiseValue)
		throws Exception {

		try (PreparedStatement preparedStatement1 = connection.prepareStatement(
				StringBundler.concat(
					"select resourcePermissionId, actionIds from ",
					"ResourcePermission where companyId = ? and ",
					"ctCollectionId = 0 and name = ? and scope = ?"));
			PreparedStatement preparedStatement2 =
				AutoBatchPreparedStatementUtil.concurrentAutoBatch(
					connection,
					"update ResourcePermission set actionIds = ? where " +
						"resourcePermissionId = ? and ctCollectionId = 0")) {

			preparedStatement1.setLong(1, companyId);
			preparedStatement1.setString(2, className);
			preparedStatement1.setInt(3, ResourceConstants.SCOPE_INDIVIDUAL);

			try (ResultSet resultSet = preparedStatement1.executeQuery()) {
				while (resultSet.next()) {
					long actionIds = resultSet.getLong("actionIds");

					if (((actionIds & bitwiseValue) != 0) ||
						((actionIds & viewBitwiseValue) == 0)) {

						continue;
					}

					preparedStatement2.setLong(1, actionIds | bitwiseValue);
					preparedStatement2.setLong(
						2, resultSet.getLong("resourcePermissionId"));

					preparedStatement2.addBatch();
				}
			}

			preparedStatement2.executeBatch();
		}
	}

	private void _upgradeAttachmentObjectField(
			String className, long companyId, String label, String name,
			long userId)
		throws Exception {

		String actionId = _getAttachmentDownloadActionKey(name);

		_resourceActionLocalService.checkResourceActions(
			className, Collections.singletonList(actionId));

		ResourceAction resourceAction =
			_resourceActionLocalService.fetchResourceAction(
				className, actionId);

		ResourceAction viewResourceAction =
			_resourceActionLocalService.fetchResourceAction(
				className, ActionKeys.VIEW);

		if ((resourceAction == null) || (viewResourceAction == null)) {
			return;
		}

		_updateResourcePermissions(
			resourceAction.getBitwiseValue(), className, companyId,
			viewResourceAction.getBitwiseValue());

		_addOrUpdatePLOEntries(actionId, companyId, label, userId);
	}

	private final Language _language;
	private final Localization _localization;
	private final PLOEntryLocalService _ploEntryLocalService;
	private final ResourceActionLocalService _resourceActionLocalService;

}