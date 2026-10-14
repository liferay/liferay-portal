/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.web.internal.upgrade.v2_1_2;

import com.liferay.dynamic.data.mapping.model.DDMTemplate;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;

/**
 * @author Selena Aungst
 */
public class DateFacetDDMTemplateUpgradeProcess extends UpgradeProcess {

	@Override
	protected void doUpgrade() throws Exception {
		String ddmTemplateIdsSQL =
			"select templateId from DDMTemplate where " + _WHERE_CLAUSE;

		runSQL(
			"delete from DDMTemplateVersion where templateId in (" +
				ddmTemplateIdsSQL + ")");
		runSQL(
			StringBundler.concat(
				"delete from ResourcePermission where name = '",
				DDMTemplate.class.getName(), "' and primKeyId in (",
				ddmTemplateIdsSQL, ")"));

		runSQL("delete from DDMTemplate where " + _WHERE_CLAUSE);
	}

	private static final String _WHERE_CLAUSE = StringBundler.concat(
		"classNameId in (select classNameId from ClassName_ where value = '",
		"com.liferay.portal.search.web.internal.date.facet.portlet.",
		"DateFacetPortlet') and templateKey in ('DATE-FACET-CHECKBOX-FTL', ",
		"'DATE-FACET-LINK-FTL', 'DATE-FACET-RADIO-FTL')");

}