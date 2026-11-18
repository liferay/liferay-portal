/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.exportimport.internal.lar;

import com.liferay.depot.constants.DepotConstants;
import com.liferay.depot.model.DepotEntry;
import com.liferay.depot.service.DepotEntryLocalService;
import com.liferay.exportimport.kernel.lar.ExportImportThreadLocal;
import com.liferay.exportimport.portlet.preferences.processor.ExportImportPortletPreferencesProcessorHelper;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.xml.Element;

/**
 * @author Adolfo Pérez Álvarez
 */
public class RemoteStagingGroupExporter {

	public RemoteStagingGroupExporter(
		DepotEntryLocalService depotEntryLocalService,
		ExportImportPortletPreferencesProcessorHelper
			exportImportPortletPreferencesProcessorHelper) {

		_depotEntryLocalService = depotEntryLocalService;
		_exportImportPortletPreferencesProcessorHelper =
			exportImportPortletPreferencesProcessorHelper;
	}

	public void addGroupExternalReferenceCodesElement(
			Group group, Element headerElement)
		throws PortalException {

		if (!ExportImportThreadLocal.isStagingInProcess() ||
			!group.isStagedRemotely()) {

			return;
		}

		Element groupExternalReferenceCodesElement = headerElement.addElement(
			"group-external-reference-codes");

		for (DepotEntry depotEntry :
				_depotEntryLocalService.getGroupConnectedDepotEntries(
					group.getGroupId(), DepotConstants.TYPE_ANY,
					QueryUtil.ALL_POS, QueryUtil.ALL_POS)) {

			Element groupExternalReferenceCodeElement =
				groupExternalReferenceCodesElement.addElement(
					"group-external-reference-code");

			Group depotGroup = depotEntry.getGroup();

			groupExternalReferenceCodeElement.addAttribute(
				"source", depotGroup.getExternalReferenceCode());
			groupExternalReferenceCodeElement.addAttribute(
				"target",
				_exportImportPortletPreferencesProcessorHelper.
					getGroupExportPortletPreferencesExternalReferenceCode(
						depotGroup.getCompanyId(),
						depotGroup.getExternalReferenceCode()));
		}
	}

	private final DepotEntryLocalService _depotEntryLocalService;
	private final ExportImportPortletPreferencesProcessorHelper
		_exportImportPortletPreferencesProcessorHelper;

}