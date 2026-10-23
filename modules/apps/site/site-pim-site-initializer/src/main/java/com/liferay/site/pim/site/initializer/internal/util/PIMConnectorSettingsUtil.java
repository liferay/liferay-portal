/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.pim.site.initializer.internal.util;

import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.service.ObjectDefinitionLocalServiceUtil;
import com.liferay.object.service.ObjectEntryLocalServiceUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.site.pim.site.initializer.connector.PIMConnector;

/**
 * @author Tancredi Covioli
 */
public class PIMConnectorSettingsUtil {

	public static ObjectDefinition fetchObjectDefinition(
		long companyId, PIMConnector pimConnector) {

		if ((pimConnector == null) ||
			Validator.isNull(
				pimConnector.
					getSettingsObjectDefinitionExternalReferenceCode())) {

			return null;
		}

		return ObjectDefinitionLocalServiceUtil.
			fetchObjectDefinitionByExternalReferenceCode(
				pimConnector.getSettingsObjectDefinitionExternalReferenceCode(),
				companyId);
	}

	public static ObjectEntry fetchObjectEntry(
		PIMConnector pimConnector, ObjectEntry pimConnectorObjectEntry) {

		ObjectDefinition objectDefinition = fetchObjectDefinition(
			pimConnectorObjectEntry.getCompanyId(), pimConnector);

		if (objectDefinition == null) {
			return null;
		}

		return ObjectEntryLocalServiceUtil.fetchObjectEntry(
			pimConnectorObjectEntry.getExternalReferenceCode(), 0,
			objectDefinition.getObjectDefinitionId());
	}

}