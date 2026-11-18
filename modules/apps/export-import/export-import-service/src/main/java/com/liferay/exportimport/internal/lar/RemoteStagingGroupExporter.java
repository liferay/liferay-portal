/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.exportimport.internal.lar;

import com.liferay.exportimport.kernel.lar.ExportImportThreadLocal;
import com.liferay.exportimport.portlet.preferences.processor.ExportImportPortletPreferencesProcessorHelper;
import com.liferay.petra.sql.dsl.DSLQueryFactoryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.GroupTable;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.util.UnicodeProperties;
import com.liferay.portal.kernel.xml.Element;

import java.util.List;
import java.util.Objects;

/**
 * @author Adolfo Pérez Álvarez
 */
public class RemoteStagingGroupExporter {

	public RemoteStagingGroupExporter(
		ExportImportPortletPreferencesProcessorHelper
			exportImportPortletPreferencesProcessorHelper,
		GroupLocalService groupLocalService) {

		_exportImportPortletPreferencesProcessorHelper =
			exportImportPortletPreferencesProcessorHelper;
		_groupLocalService = groupLocalService;
	}

	public void addGroupExternalReferenceCodesElement(
		Group group, Element headerElement) {

		if (!ExportImportThreadLocal.isStagingInProcess() ||
			!group.isStagedRemotely()) {

			return;
		}

		UnicodeProperties typeSettingsUnicodeProperties =
			group.getTypeSettingsProperties();

		Element groupExternalReferenceCodesElement = headerElement.addElement(
			"group-external-reference-codes");

		for (Group stagedRemotelyGroup :
				_getStagedRemotelyGroups(group.getCompanyId())) {

			if (!stagedRemotelyGroup.isStagedRemotely() ||
				!_isSameRemoteConnection(
					typeSettingsUnicodeProperties,
					stagedRemotelyGroup.getTypeSettingsProperties())) {

				continue;
			}

			String externalReferenceCode =
				stagedRemotelyGroup.getExternalReferenceCode();

			String remoteExternalReferenceCode =
				_exportImportPortletPreferencesProcessorHelper.
					getGroupExportPortletPreferencesExternalReferenceCode(
						stagedRemotelyGroup.getCompanyId(),
						externalReferenceCode);

			if (Objects.equals(
					externalReferenceCode, remoteExternalReferenceCode)) {

				continue;
			}

			Element groupExternalReferenceCodeElement =
				groupExternalReferenceCodesElement.addElement(
					"group-external-reference-code");

			groupExternalReferenceCodeElement.addAttribute(
				"source", externalReferenceCode);
			groupExternalReferenceCodeElement.addAttribute(
				"target", remoteExternalReferenceCode);
		}
	}

	private List<Group> _getStagedRemotelyGroups(long companyId) {
		return _groupLocalService.dslQuery(
			DSLQueryFactoryUtil.select(
				GroupTable.INSTANCE
			).from(
				GroupTable.INSTANCE
			).where(
				GroupTable.INSTANCE.companyId.eq(
					companyId
				).and(
					GroupTable.INSTANCE.typeSettings.like(
						"%stagedRemotely=true%")
				)
			));
	}

	private boolean _isSameRemoteConnection(
		UnicodeProperties typeSettingsUnicodeProperties1,
		UnicodeProperties typeSettingsUnicodeProperties2) {

		for (String key : _REMOTE_CONNECTION_KEYS) {
			if (!Objects.equals(
					typeSettingsUnicodeProperties1.getProperty(key),
					typeSettingsUnicodeProperties2.getProperty(key))) {

				return false;
			}
		}

		return true;
	}

	private static final String[] _REMOTE_CONNECTION_KEYS = {
		"remoteAddress", "remotePathContext", "remotePort", "secureConnection"
	};

	private final ExportImportPortletPreferencesProcessorHelper
		_exportImportPortletPreferencesProcessorHelper;
	private final GroupLocalService _groupLocalService;

}