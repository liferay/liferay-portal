/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.oauth.client.persistence.internal.upgrade.v1_6_0.util;

import com.liferay.portal.kernel.upgrade.UpgradeProcess;

/**
 * @author Jorge García Jiménez
 * @generated
 * @see com.liferay.portal.tools.upgrade.table.builder.UpgradeTableBuilder
 */
public class OAuthClientPRLocalMetadataTable {

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

	private static final String _TABLE_NAME = "OAuthClientPRLocalMetadata";

	private static final String _TABLE_SQL_CREATE =
		"create table OAuthClientPRLocalMetadata (mvccVersion LONG default 0 not null,uuid_ VARCHAR(75) null,externalReferenceCode VARCHAR(75) null,oAuthClientPRLocalMetadataId LONG not null primary key,companyId LONG,userId LONG,userName VARCHAR(75) null,createDate DATE null,modifiedDate DATE null,localWellKnownEnabled BOOLEAN,localWellKnownURI VARCHAR(256) null,metadataJSON TEXT null,resource VARCHAR(256) null)";

}