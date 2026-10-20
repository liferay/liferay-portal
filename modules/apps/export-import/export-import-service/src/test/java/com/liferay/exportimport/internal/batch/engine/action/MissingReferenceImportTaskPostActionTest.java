/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.exportimport.internal.batch.engine.action;

import com.liferay.exportimport.internal.lar.ExportImportDescriptorThreadLocal;
import com.liferay.exportimport.internal.lar.PortletDataContextThreadLocal;
import com.liferay.exportimport.kernel.empty.model.EmptyModelManager;
import com.liferay.exportimport.kernel.lar.ExportImportThreadLocal;
import com.liferay.exportimport.kernel.lar.PortletDataContext;
import com.liferay.exportimport.vulcan.batch.engine.ExportImportVulcanBatchEngineTaskItemDelegate;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.portal.kernel.bean.BeanProperties;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;
import com.liferay.staging.StagingGroupHelper;

import org.junit.After;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.MockedStatic;
import org.mockito.Mockito;

/**
 * @author Carlos Correa
 */
public class MissingReferenceImportTaskPostActionTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() {
		ReflectionTestUtil.setFieldValue(
			_missingReferenceImportTaskPostAction, "_beanProperties",
			_beanProperties);
		ReflectionTestUtil.setFieldValue(
			_missingReferenceImportTaskPostAction, "_emptyModelManager",
			_emptyModelManager);
		ReflectionTestUtil.setFieldValue(
			_missingReferenceImportTaskPostAction, "_stagingGroupHelper",
			_stagingGroupHelper);

		_exportImportThreadLocalMockedStatic = Mockito.mockStatic(
			ExportImportThreadLocal.class);
	}

	@After
	public void tearDown() {
		Mockito.verifyNoMoreInteractions(_emptyModelManager);

		_exportImportThreadLocalMockedStatic.close();
	}

	@Test
	public void testRun() throws Exception {
		_testRun(false);
		_testRun(true);
	}

	@Test
	public void testRunOutsideImportProcess() throws Exception {
		_exportImportThreadLocalMockedStatic.when(
			ExportImportThreadLocal::isImportInProcess
		).thenReturn(
			false
		);

		_missingReferenceImportTaskPostAction.run(
			null, null, null, new Object(), new Object());
	}

	@Test
	public void testRunWithoutExportImportDescriptor() throws Exception {
		_exportImportThreadLocalMockedStatic.when(
			ExportImportThreadLocal::isImportInProcess
		).thenReturn(
			true
		);

		_missingReferenceImportTaskPostAction.run(
			null, null, null, new Object(), new Object());
	}

	@Test
	public void testRunWithoutExternalReferenceCode() throws Exception {
		_exportImportThreadLocalMockedStatic.when(
			ExportImportThreadLocal::isImportInProcess
		).thenReturn(
			true
		);

		try (SafeCloseable safeCloseable1 =
				ExportImportDescriptorThreadLocal.
					setExportImportDescriptorWithSafeCloseable(
						Mockito.mock(
							ExportImportVulcanBatchEngineTaskItemDelegate.
								ExportImportDescriptor.class));
			SafeCloseable safeCloseable2 =
				PortletDataContextThreadLocal.
					setPortletDataContextWithSafeCloseable(
						Mockito.mock(PortletDataContext.class))) {

			_missingReferenceImportTaskPostAction.run(
				null, null, null, new Object(), new Object());
		}
	}

	private void _testRun(boolean companyGroup) throws Exception {
		_exportImportThreadLocalMockedStatic.when(
			ExportImportThreadLocal::isImportInProcess
		).thenReturn(
			true
		);

		Object persistedItem = new Object();

		String externalReferenceCode = RandomTestUtil.randomString();

		Mockito.when(
			_beanProperties.getStringSilent(
				persistedItem, "externalReferenceCode")
		).thenReturn(
			externalReferenceCode
		);

		ExportImportVulcanBatchEngineTaskItemDelegate.ExportImportDescriptor<?>
			exportImportDescriptor = Mockito.mock(
				ExportImportVulcanBatchEngineTaskItemDelegate.
					ExportImportDescriptor.class);

		String modelClassName = RandomTestUtil.randomString();

		Mockito.when(
			exportImportDescriptor.getModelClassName()
		).thenReturn(
			modelClassName
		);

		PortletDataContext portletDataContext = Mockito.mock(
			PortletDataContext.class);

		long companyId = RandomTestUtil.randomLong();

		Mockito.when(
			portletDataContext.getCompanyId()
		).thenReturn(
			companyId
		);

		long scopeGroupId = RandomTestUtil.randomLong();

		Mockito.when(
			portletDataContext.getScopeGroupId()
		).thenReturn(
			scopeGroupId
		);

		Mockito.when(
			_stagingGroupHelper.isCompanyGroup(companyId, scopeGroupId)
		).thenReturn(
			companyGroup
		);

		long groupId = 0;

		if (!companyGroup) {
			groupId = scopeGroupId;
		}

		try (SafeCloseable safeCloseable1 =
				ExportImportDescriptorThreadLocal.
					setExportImportDescriptorWithSafeCloseable(
						exportImportDescriptor);
			SafeCloseable safeCloseable2 =
				PortletDataContextThreadLocal.
					setPortletDataContextWithSafeCloseable(
						portletDataContext)) {

			_missingReferenceImportTaskPostAction.run(
				null, null, null, new Object(), persistedItem);
		}

		Mockito.verify(
			_emptyModelManager
		).resolveMissingReference(
			modelClassName, companyId, externalReferenceCode, groupId
		);
	}

	private final BeanProperties _beanProperties = Mockito.mock(
		BeanProperties.class);
	private final EmptyModelManager _emptyModelManager = Mockito.mock(
		EmptyModelManager.class);
	private MockedStatic<ExportImportThreadLocal>
		_exportImportThreadLocalMockedStatic;
	private final MissingReferenceImportTaskPostAction
		_missingReferenceImportTaskPostAction =
			new MissingReferenceImportTaskPostAction();
	private final StagingGroupHelper _stagingGroupHelper = Mockito.mock(
		StagingGroupHelper.class);

}