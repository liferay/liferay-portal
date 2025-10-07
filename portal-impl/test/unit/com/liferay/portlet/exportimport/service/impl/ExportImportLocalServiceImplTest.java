/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portlet.exportimport.service.impl;

import com.liferay.exportimport.kernel.controller.ExportImportControllerRegistryUtil;
import com.liferay.exportimport.kernel.controller.ImportController;
import com.liferay.exportimport.kernel.lar.MissingReferences;
import com.liferay.exportimport.kernel.model.ExportImportConfiguration;
import com.liferay.exportimport.kernel.service.ExportImportLocalService;
import com.liferay.portal.kernel.lazy.referencing.LazyReferencingThreadLocal;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.util.IntegerWrapper;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import java.io.File;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.MockedStatic;
import org.mockito.Mockito;

/**
 * @author Carlos Correa
 */
public class ExportImportLocalServiceImplTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Test
	@TestInfo("LPD-61067")
	public void testImportLayouts() throws Exception {
		IntegerWrapper count = new IntegerWrapper();

		_mockedStatic.when(
			() -> ExportImportControllerRegistryUtil.getImportController(
				Mockito.any())
		).thenReturn(
			new TestImportController(
				() -> {
					Assert.assertTrue(LazyReferencingThreadLocal.isEnabled());

					count.increment();
				})
		);

		Assert.assertEquals(0, count.getValue());

		Assert.assertFalse(LazyReferencingThreadLocal.isEnabled());

		ExportImportLocalService exportImportLocalService =
			new ExportImportLocalServiceImpl();

		exportImportLocalService.importLayouts(
			Mockito.mock(ExportImportConfiguration.class),
			Mockito.mock(File.class));

		Assert.assertEquals(1, count.getValue());
		Assert.assertFalse(LazyReferencingThreadLocal.isEnabled());
	}

	@Test
	@TestInfo("LPD-61067")
	public void testImportPortletInfo() throws Exception {
		IntegerWrapper count = new IntegerWrapper();

		_mockedStatic.when(
			() -> ExportImportControllerRegistryUtil.getImportController(
				Mockito.any())
		).thenReturn(
			new TestImportController(
				() -> {
					Assert.assertTrue(LazyReferencingThreadLocal.isEnabled());

					count.increment();
				})
		);

		Assert.assertEquals(0, count.getValue());

		Assert.assertFalse(LazyReferencingThreadLocal.isEnabled());

		ExportImportLocalService exportImportLocalService =
			new ExportImportLocalServiceImpl();

		exportImportLocalService.importPortletInfo(
			Mockito.mock(ExportImportConfiguration.class),
			Mockito.mock(File.class));

		Assert.assertEquals(1, count.getValue());
		Assert.assertFalse(LazyReferencingThreadLocal.isEnabled());
	}

	private static final MockedStatic<ExportImportControllerRegistryUtil>
		_mockedStatic = Mockito.mockStatic(
			ExportImportControllerRegistryUtil.class);

	private static class TestImportController implements ImportController {

		public TestImportController(Runnable runnable) {
			_runnable = runnable;
		}

		@Override
		public void importDataDeletions(
				ExportImportConfiguration exportImportConfiguration, File file)
			throws Exception {

			throw new UnsupportedOperationException();
		}

		@Override
		public void importFile(
				ExportImportConfiguration exportImportConfiguration, File file)
			throws Exception {

			_runnable.run();
		}

		@Override
		public MissingReferences validateFile(
				ExportImportConfiguration exportImportConfiguration, File file)
			throws Exception {

			throw new UnsupportedOperationException();
		}

		private final Runnable _runnable;

	}

}