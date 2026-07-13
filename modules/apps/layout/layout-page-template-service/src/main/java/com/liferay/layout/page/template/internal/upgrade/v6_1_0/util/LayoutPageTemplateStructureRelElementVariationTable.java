/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.page.template.internal.upgrade.v6_1_0.util;

import com.liferay.portal.kernel.upgrade.UpgradeProcess;

/**
 * @author Victor Galan
 * @generated
 * @see com.liferay.portal.tools.upgrade.table.builder.UpgradeTableBuilder
 */
public class LayoutPageTemplateStructureRelElementVariationTable {

	public static UpgradeProcess create() {
		return new UpgradeProcess() {

			@Override
			protected void doUpgrade() throws Exception {
				if (!hasTable(_TABLE_NAME)) {
					runSQL(_TABLE_SQL_CREATE);
				}
			}

		};
	}

	private static final String _TABLE_NAME = "LPTSRelElementVariation";

	private static final String _TABLE_SQL_CREATE =
		"create table LPTSRelElementVariation (mvccVersion LONG default 0 not null,ctCollectionId LONG default 0 not null,uuid_ VARCHAR(75) null,externalReferenceCode VARCHAR(75) null,lptsRelElementVariationId LONG not null,groupId LONG,companyId LONG,userId LONG,userName VARCHAR(75) null,createDate DATE null,modifiedDate DATE null,hide STRING null,html STRING null,js STRING null,name VARCHAR(75) null,plid LONG,segmentsExperienceERC VARCHAR(75) null,targetElement VARCHAR(1000) null,active_ BOOLEAN,primary key (lptsRelElementVariationId, ctCollectionId))";

}