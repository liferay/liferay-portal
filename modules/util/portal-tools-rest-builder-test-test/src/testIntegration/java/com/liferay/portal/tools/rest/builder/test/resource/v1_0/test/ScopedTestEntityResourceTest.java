/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.tools.rest.builder.test.resource.v1_0.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.portal.tools.rest.builder.test.client.dto.v1_0.ScopedTestEntity;

import java.util.Objects;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Alejandro Tardín
 */
@RunWith(Arquillian.class)
public class ScopedTestEntityResourceTest
	extends BaseScopedTestEntityResourceTestCase {

	@Ignore
	@Override
	@Test
	public void testBatchEngineDeleteImportTask() throws Exception {
		super.testBatchEngineDeleteImportTask();
	}

	@Override
	protected void assertValid(ScopedTestEntity scopedTestEntity)
		throws Exception {

		boolean valid = true;

		if ((scopedTestEntity.getDateCreated() == null) ||
			(scopedTestEntity.getDateModified() == null) ||
			(scopedTestEntity.getExternalReferenceCode() == null)) {

			valid = false;
		}

		if ((scopedTestEntity.getAssetLibraryKey() != null) &&
			(!Objects.equals(
				scopedTestEntity.getAssetLibraryKey(),
				String.valueOf(testDepotEntry.getGroupId())) ||
			 !Objects.equals(scopedTestEntity.getSiteId(), 0L))) {

			valid = false;
		}

		Assert.assertTrue(valid);
	}

}