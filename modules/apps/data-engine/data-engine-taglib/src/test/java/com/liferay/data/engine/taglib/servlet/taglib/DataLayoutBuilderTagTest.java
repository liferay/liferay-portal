/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.data.engine.taglib.servlet.taglib;

import com.liferay.data.engine.taglib.servlet.taglib.DataLayoutBuilderTag.DataLayoutDDMFormAdapter;
import com.liferay.dynamic.data.mapping.model.DDMFormField;
import com.liferay.dynamic.data.mapping.model.UnlocalizedValue;
import com.liferay.dynamic.data.mapping.model.Value;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.module.util.SystemBundleUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import java.util.Collections;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.MockedStatic;
import org.mockito.Mockito;

import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;

/**
 * @author Igor Franca
 */
public class DataLayoutBuilderTagTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@BeforeClass
	public static void setUpClass() throws Exception {
		BundleContext bundleContext = SystemBundleUtil.getBundleContext();

		_frameworkUtilMockedStatic.when(
			() -> FrameworkUtil.getBundle(Mockito.any())
		).thenReturn(
			bundleContext.getBundle()
		);
	}

	@AfterClass
	public static void tearDownClass() {
		_frameworkUtilMockedStatic.close();
	}

	@Before
	public void setUp() {
		DataLayoutBuilderTag dataLayoutBuilderTag = new DataLayoutBuilderTag();

		_dataLayoutDDMFormAdapter =
			dataLayoutBuilderTag.new DataLayoutDDMFormAdapter(
				Collections.singleton(LocaleUtil.US), null, null, null, null);
	}

	@Test
	public void testDataLayoutDDMFormAdapterCreateDDMFormFieldValue()
		throws Exception {

		_testDataLayoutDDMFormAdapterCreateDDMFormFieldValue(
			null, StringPool.BLANK);

		String propertyValue = RandomTestUtil.randomString();

		_testDataLayoutDDMFormAdapterCreateDDMFormFieldValue(
			propertyValue, propertyValue);
	}

	private void _testDataLayoutDDMFormAdapterCreateDDMFormFieldValue(
			String actualPropertyValue, String expectedPropertyValue)
		throws Exception {

		Value value = _dataLayoutDDMFormAdapter.createDDMFormFieldValue(
			Collections.singleton(LocaleUtil.US), new DDMFormField(),
			actualPropertyValue);

		Assert.assertTrue(value instanceof UnlocalizedValue);
		Assert.assertEquals(
			expectedPropertyValue, value.getString(LocaleUtil.US));
	}

	private static final MockedStatic<FrameworkUtil>
		_frameworkUtilMockedStatic = Mockito.mockStatic(FrameworkUtil.class);

	private DataLayoutDDMFormAdapter _dataLayoutDDMFormAdapter;

}