/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.exportimport.kernel.lar;

import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.PortalUtil;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import org.mockito.MockedStatic;
import org.mockito.Mockito;

/**
 * @author Lourdes Fernández Besada
 */
public class StagedModelTypeTest {

	@BeforeClass
	public static void setUpClass() {
		_portalUtilMockedStatic.when(
			() -> PortalUtil.getClassName(Mockito.anyLong())
		).thenAnswer(
			invocationOnMock -> String.valueOf(
				invocationOnMock.getArgument(0, Long.class))
		);

		_portalUtilMockedStatic.when(
			() -> PortalUtil.getClassNameId(Mockito.anyString())
		).thenAnswer(
			invocationOnMock -> GetterUtil.getLong(
				invocationOnMock.getArgument(0, String.class))
		);
	}

	@AfterClass
	public static void tearDownClass() {
		_portalUtilMockedStatic.close();
	}

	@Test
	@TestInfo("LPD-68492")
	public void testParse() {
		long classNameId = RandomTestUtil.randomLong();

		String className = String.valueOf(classNameId);

		_testParse(new StagedModelType(className));
		_testParse(new StagedModelType(className, null));

		_testParse(new StagedModelType(classNameId));
		_testParse(new StagedModelType(classNameId, 0));
		_testParse(
			new StagedModelType(
				className, StagedModelType.REFERRER_CLASS_NAME_ALL));
		_testParse(
			new StagedModelType(
				classNameId, StagedModelType.REFERRER_CLASS_NAME_ID_ALL));
		_testParse(
			new StagedModelType(
				className, StagedModelType.REFERRER_CLASS_NAME_ANY));
		_testParse(
			new StagedModelType(
				classNameId, StagedModelType.REFERRER_CLASS_NAME_ID_ANY));

		long referrerClassNameId = RandomTestUtil.randomLong();

		_testParse(new StagedModelType(classNameId, referrerClassNameId));

		String referrerClassName = String.valueOf(referrerClassNameId);

		_testParse(new StagedModelType(className, referrerClassName));
	}

	@Test
	@TestInfo("LPD-68492")
	public void testToString() {
		long classNameId = RandomTestUtil.randomLong();

		String className = String.valueOf(classNameId);

		_testToString(className, new StagedModelType(className));
		_testToString(className, new StagedModelType(className, null));
		_testToString(className, new StagedModelType(classNameId));
		_testToString(className, new StagedModelType(classNameId, 0));

		String expected = StringBundler.concat(
			className, StringPool.POUND,
			StagedModelType.REFERRER_CLASS_NAME_ALL);

		_testToString(
			expected,
			new StagedModelType(
				className, StagedModelType.REFERRER_CLASS_NAME_ALL));
		_testToString(
			expected,
			new StagedModelType(
				classNameId, StagedModelType.REFERRER_CLASS_NAME_ID_ALL));

		expected = StringBundler.concat(
			className, StringPool.POUND,
			StagedModelType.REFERRER_CLASS_NAME_ANY);

		_testToString(
			expected,
			new StagedModelType(
				className, StagedModelType.REFERRER_CLASS_NAME_ANY));
		_testToString(
			expected,
			new StagedModelType(
				classNameId, StagedModelType.REFERRER_CLASS_NAME_ID_ANY));

		long referrerClassNameId = RandomTestUtil.randomLong();

		String referrerClassName = String.valueOf(referrerClassNameId);

		expected = StringBundler.concat(
			className, StringPool.POUND, referrerClassName);

		_testToString(
			expected, new StagedModelType(classNameId, referrerClassNameId));

		_testToString(
			expected, new StagedModelType(className, referrerClassName));
	}

	private void _testParse(StagedModelType stagedModelType) {
		Assert.assertEquals(
			stagedModelType, StagedModelType.parse(stagedModelType.toString()));
	}

	private void _testToString(
		String expected, StagedModelType stagedModelType) {

		Assert.assertEquals(expected, stagedModelType.toString());
	}

	private static final MockedStatic<PortalUtil> _portalUtilMockedStatic =
		Mockito.mockStatic(PortalUtil.class);

}