/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.saml.opensaml.integration.internal.util;

import com.liferay.expando.kernel.model.ExpandoColumn;
import com.liferay.expando.kernel.model.ExpandoColumnConstants;
import com.liferay.expando.kernel.model.ExpandoTable;
import com.liferay.expando.kernel.model.ExpandoTableConstants;
import com.liferay.expando.kernel.service.ExpandoColumnLocalServiceUtil;
import com.liferay.expando.kernel.service.ExpandoTableLocalServiceUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.service.ClassNameLocalServiceUtil;
import com.liferay.portal.kernel.util.UnicodeProperties;

/**
 * @author Christopher Kian
 */
public class SamlProvisioningUtil {

	public static ExpandoColumn getOrAddExpandoColumn(
			long companyId, String className, String name)
		throws PortalException {

		long classNameId = ClassNameLocalServiceUtil.getClassNameId(className);

		ExpandoTable expandoTable = ExpandoTableLocalServiceUtil.fetchTable(
			companyId, classNameId, ExpandoTableConstants.DEFAULT_TABLE_NAME);

		if (expandoTable == null) {
			expandoTable = ExpandoTableLocalServiceUtil.addTable(
				companyId, classNameId,
				ExpandoTableConstants.DEFAULT_TABLE_NAME);
		}

		ExpandoColumn expandoColumn = ExpandoColumnLocalServiceUtil.fetchColumn(
			expandoTable.getTableId(), name);

		if (expandoColumn != null) {
			return expandoColumn;
		}

		expandoColumn = ExpandoColumnLocalServiceUtil.addColumn(
			expandoTable.getTableId(), name, ExpandoColumnConstants.STRING);

		UnicodeProperties unicodeProperties =
			expandoColumn.getTypeSettingsProperties();

		unicodeProperties.setProperty(
			ExpandoColumnConstants.INDEX_TYPE,
			String.valueOf(ExpandoColumnConstants.INDEX_TYPE_KEYWORD));
		unicodeProperties.setProperty(
			ExpandoColumnConstants.PROPERTY_HIDDEN, Boolean.TRUE.toString());

		expandoColumn.setTypeSettingsProperties(unicodeProperties);

		return ExpandoColumnLocalServiceUtil.updateExpandoColumn(expandoColumn);
	}

}