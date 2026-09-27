/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.dao.db;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author Jorge Díaz
 */
public class IndexMetadataFactoryUtilTest {

	@Test
	public void testCreateIndexMetadata() {
		IndexMetadata indexMetadata =
			IndexMetadataFactoryUtil.createIndexMetadata(
				"\tcreate unique index IX_1 on Table1 (column1);");

		Assert.assertTrue(indexMetadata.isUnique());

		indexMetadata = IndexMetadataFactoryUtil.createIndexMetadata(
			"create index IX_1 on Table1 (column1);");

		Assert.assertFalse(indexMetadata.isUnique());

		indexMetadata = IndexMetadataFactoryUtil.createIndexMetadata(
			"create index IX_1 on Table1 (column1, uniqueColumn2);");

		Assert.assertFalse(indexMetadata.isUnique());

		indexMetadata = IndexMetadataFactoryUtil.createIndexMetadata(
			"create unique index IX_1 on Table1 (column1);");

		Assert.assertTrue(indexMetadata.isUnique());
	}

}