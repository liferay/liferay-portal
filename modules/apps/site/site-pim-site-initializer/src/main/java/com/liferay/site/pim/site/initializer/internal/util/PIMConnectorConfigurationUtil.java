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
public class PIMConnectorConfigurationUtil {

	public static ObjectDefinition
		fetchPIMConnectorConfigurationObjectDefinition(
			long companyId, PIMConnector pimConnector) {

		if ((pimConnector == null) ||
			Validator.isNull(
				pimConnector.
					getPIMConnectorConfigurationObjectDefinitionExternalReferenceCode())) {

			return null;
		}

		return ObjectDefinitionLocalServiceUtil.
			fetchObjectDefinitionByExternalReferenceCode(
				pimConnector.
					getPIMConnectorConfigurationObjectDefinitionExternalReferenceCode(),
				companyId);
	}

	public static ObjectEntry fetchPIMConnectorConfigurationObjectEntry(
		PIMConnector pimConnector, ObjectEntry pimConnectorObjectEntry) {

		ObjectDefinition objectDefinition =
			fetchPIMConnectorConfigurationObjectDefinition(
				pimConnectorObjectEntry.getCompanyId(), pimConnector);

		if (objectDefinition == null) {
			return null;
		}

		return ObjectEntryLocalServiceUtil.fetchObjectEntry(
			pimConnectorObjectEntry.getExternalReferenceCode(), 0,
			objectDefinition.getObjectDefinitionId());
	}

}