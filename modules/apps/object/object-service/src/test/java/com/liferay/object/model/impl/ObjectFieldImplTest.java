/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.model.impl;

import com.liferay.object.constants.ObjectFieldConstants;
import com.liferay.object.model.ObjectField;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

/**
 * @author Carolina Barbosa
 */
public class ObjectFieldImplTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Test
	public void testGetReadOnly() {
		_testGetReadOnly(
			ObjectFieldConstants.READ_ONLY_CONDITIONAL,
			ObjectFieldConstants.READ_ONLY_CONDITIONAL);
		_testGetReadOnly(
			ObjectFieldConstants.READ_ONLY_FALSE,
			ObjectFieldConstants.READ_ONLY_FALSE);
		_testGetReadOnly(
			StringPool.BLANK, ObjectFieldConstants.READ_ONLY_FALSE);
		_testGetReadOnly(null, ObjectFieldConstants.READ_ONLY_FALSE);
		_testGetReadOnly(
			ObjectFieldConstants.READ_ONLY_TRUE,
			ObjectFieldConstants.READ_ONLY_TRUE);
	}

	private void _testGetReadOnly(
		String actualReadOnly, String expectedReadOnly) {

		ObjectField objectField = new ObjectFieldImpl();

		objectField.setReadOnly(actualReadOnly);

		Assert.assertEquals(expectedReadOnly, objectField.getReadOnly());
	}

}