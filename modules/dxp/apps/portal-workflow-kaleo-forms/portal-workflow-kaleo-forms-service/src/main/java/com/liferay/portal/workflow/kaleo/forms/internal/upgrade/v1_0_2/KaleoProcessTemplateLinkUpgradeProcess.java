/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.workflow.kaleo.forms.internal.upgrade.v1_0_2;

import com.liferay.dynamic.data.mapping.service.DDMTemplateLinkLocalService;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.service.ClassNameLocalService;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.workflow.kaleo.forms.model.KaleoProcess;
import com.liferay.portal.workflow.kaleo.forms.model.KaleoProcessLink;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * @author Rafael Praxedes
 */
public class KaleoProcessTemplateLinkUpgradeProcess extends UpgradeProcess {

	public KaleoProcessTemplateLinkUpgradeProcess(
		ClassNameLocalService classNameLocalService,
		DDMTemplateLinkLocalService ddmTemplateLinkLocalService) {

		_classNameLocalService = classNameLocalService;
		_ddmTemplateLinkLocalService = ddmTemplateLinkLocalService;
	}

	@Override
	protected void doUpgrade() throws Exception {
		_updateKaleoProcess();
		_updateKaleoProcessLink();
	}

	private void _updateKaleoProcess() throws Exception {
		long kaleoProcessClassNameId = _classNameLocalService.getClassNameId(
			KaleoProcess.class.getName());

		try (PreparedStatement preparedStatement = connection.prepareStatement(
				StringBundler.concat(
					"select KaleoProcess.kaleoProcessId, KaleoProcess.",
					"DDMTemplateId from KaleoProcess where (KaleoProcess.",
					"DDMTemplateId > 0) and not exists (select 1 from ",
					"DDMTemplateLink where (DDMTemplateLink.classPK = ",
					"KaleoProcess.kaleoProcessId) and (DDMTemplateLink.",
					"classNameId = ?))"))) {

			preparedStatement.setLong(1, kaleoProcessClassNameId);

			try (ResultSet resultSet = preparedStatement.executeQuery()) {
				while (resultSet.next()) {
					long kaleoProcessLinkId = resultSet.getLong(
						"kaleoProcessId");
					long ddmTemplateId = resultSet.getLong("DDMTemplateId");

					_ddmTemplateLinkLocalService.addTemplateLink(
						kaleoProcessClassNameId, kaleoProcessLinkId,
						ddmTemplateId);
				}
			}
		}
	}

	private void _updateKaleoProcessLink() throws Exception {
		long kaleoProcessLinkClassNameId =
			_classNameLocalService.getClassNameId(
				KaleoProcessLink.class.getName());

		try (PreparedStatement preparedStatement = connection.prepareStatement(
				StringBundler.concat(
					"select KaleoProcessLink.kaleoProcessLinkId, ",
					"KaleoProcessLink.DDMTemplateId from KaleoProcessLink ",
					"where (KaleoProcessLink.DDMTemplateId > 0) and not ",
					"exists (select 1 from DDMTemplateLink where (",
					"DDMTemplateLink.classPK = KaleoProcessLink.",
					"kaleoProcessLinkId) and (DDMTemplateLink.classNameId = ?)",
					")"))) {

			preparedStatement.setLong(1, kaleoProcessLinkClassNameId);

			try (ResultSet resultSet = preparedStatement.executeQuery()) {
				while (resultSet.next()) {
					long kaleoProcessLinkId = resultSet.getLong(
						"kaleoProcessLinkId");
					long ddmTemplateId = resultSet.getLong("DDMTemplateId");

					_ddmTemplateLinkLocalService.addTemplateLink(
						kaleoProcessLinkClassNameId, kaleoProcessLinkId,
						ddmTemplateId);
				}
			}
		}
	}

	private final ClassNameLocalService _classNameLocalService;
	private final DDMTemplateLinkLocalService _ddmTemplateLinkLocalService;

}