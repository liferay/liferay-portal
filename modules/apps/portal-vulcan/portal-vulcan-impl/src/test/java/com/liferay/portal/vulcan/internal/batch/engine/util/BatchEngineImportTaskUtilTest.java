/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.vulcan.internal.batch.engine.util;

import com.liferay.batch.engine.BatchEngineTaskOperation;
import com.liferay.batch.engine.constants.CreateStrategy;
import com.liferay.batch.engine.constants.UpdateStrategy;
import com.liferay.batch.engine.model.BatchEngineImportTask;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.Mockito;

/**
 * @author Jhosseph Gonzalez
 */
public class BatchEngineImportTaskUtilTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Test
	public void testIsPartialUpdateWithCreateOperation() {
		Assert.assertTrue(
			_isPartialUpdate(
				BatchEngineTaskOperation.CREATE, CreateStrategy.UPSERT,
				UpdateStrategy.PARTIAL_UPDATE));
		Assert.assertFalse(
			_isPartialUpdate(
				BatchEngineTaskOperation.CREATE, CreateStrategy.INSERT,
				UpdateStrategy.PARTIAL_UPDATE));
		Assert.assertFalse(
			_isPartialUpdate(
				BatchEngineTaskOperation.CREATE, CreateStrategy.UPSERT,
				UpdateStrategy.UPDATE));
	}

	@Test
	public void testIsPartialUpdateWithUpdateOperation() {
		Assert.assertTrue(
			_isPartialUpdate(
				BatchEngineTaskOperation.UPDATE, null,
				UpdateStrategy.PARTIAL_UPDATE));
		Assert.assertFalse(
			_isPartialUpdate(
				BatchEngineTaskOperation.UPDATE, null, UpdateStrategy.UPDATE));
	}

	private boolean _isPartialUpdate(
		BatchEngineTaskOperation batchEngineTaskOperation,
		CreateStrategy createStrategy, UpdateStrategy updateStrategy) {

		BatchEngineImportTask batchEngineImportTask = Mockito.mock(
			BatchEngineImportTask.class);

		Mockito.when(
			batchEngineImportTask.getOperation()
		).thenReturn(
			batchEngineTaskOperation.name()
		);

		if (createStrategy != null) {
			Mockito.when(
				batchEngineImportTask.getParameterValue("createStrategy")
			).thenReturn(
				createStrategy.getDBOperation()
			);
		}

		Mockito.when(
			batchEngineImportTask.getParameterValue("updateStrategy")
		).thenReturn(
			updateStrategy.getDBOperation()
		);

		return BatchEngineImportTaskUtil.isPartialUpdate(batchEngineImportTask);
	}

}