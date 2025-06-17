/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.verify;

import com.liferay.portal.kernel.util.StringBundler;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.MockedConstruction;
import org.mockito.Mockito;

/**
 * @author Mariano Álvaro Sáiz
 */
public class PreupgradeVerifyProcessSuiteTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Test
	public void testVerifyExceptionMessages() {
		try (MockedConstruction<PreupgradeVerifyCompanyUsers>
				mockedConstruction1 = _mockConstruction(
					PreupgradeVerifyCompanyUsers.class);
			MockedConstruction<PreupgradeVerifyDatabaseCharacterSet>
				mockedConstruction2 = _mockConstruction(
					PreupgradeVerifyDatabaseCharacterSet.class);
			MockedConstruction<PreupgradeVerifyProperties> mockedConstruction3 =
				_mockConstruction(PreupgradeVerifyProperties.class)) {

			VerifyProcess verifyProcess = new PreupgradeVerifyProcessSuite();

			verifyProcess.verify();

			Assert.fail();
		}
		catch (VerifyException verifyException) {
			Assert.assertEquals(
				StringBundler.concat(
					"Exception in PreupgradeVerifyCompanyUsers\n",
					"Exception in PreupgradeVerifyDatabaseCharacterSet\n",
					"Exception in PreupgradeVerifyProperties"),
				verifyException.getMessage());
		}
	}

	private <T extends VerifyProcess> MockedConstruction<T> _mockConstruction(
		Class<T> clazz) {

		return Mockito.mockConstruction(
			clazz,
			(mock, context) -> Mockito.doThrow(
				new VerifyException("Exception in " + clazz.getSimpleName())
			).when(
				mock
			).verify());
	}

}