/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.exportimport.report.internal.exportimport.lifecycle;

import com.liferay.exportimport.kernel.empty.model.EmptyModelManager;
import com.liferay.exportimport.kernel.lar.PortletDataContext;
import com.liferay.exportimport.kernel.lifecycle.ExportImportLifecycleEvent;
import com.liferay.exportimport.kernel.lifecycle.constants.ExportImportLifecycleConstants;
import com.liferay.portal.kernel.model.ExternalReferenceCodeModel;
import com.liferay.portal.kernel.model.StagedGroupedModel;
import com.liferay.portal.kernel.model.StagedModel;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.TransientValue;
import com.liferay.portal.test.rule.LiferayUnitTestRule;
import com.liferay.staging.StagingGroupHelper;

import java.io.Serializable;

import java.util.Arrays;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.Mockito;

/**
 * @author Carlos Correa
 */
public class MissingReferenceExportImportLifecycleListenerTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() {
		ReflectionTestUtil.setFieldValue(
			_missingReferenceExportImportLifecycleListener,
			"_emptyModelManager", _emptyModelManager);
		ReflectionTestUtil.setFieldValue(
			_missingReferenceExportImportLifecycleListener,
			"_stagingGroupHelper", _stagingGroupHelper);
	}

	@After
	public void tearDown() {
		Mockito.verifyNoMoreInteractions(_emptyModelManager);
	}

	@Test
	public void testIsParallel() {
		Assert.assertFalse(
			_missingReferenceExportImportLifecycleListener.isParallel());
	}

	@Test
	public void testOnExportImportLifecycleEventWithAnotherEvent()
		throws Exception {

		_missingReferenceExportImportLifecycleListener.
			onExportImportLifecycleEvent(
				_getExportImportLifecycleEvent(
					ExportImportLifecycleConstants.
						EVENT_STAGED_MODEL_IMPORT_STARTED,
					_getStagedModel(RandomTestUtil.randomString())));
	}

	@Test
	public void testOnExportImportLifecycleEventWithCompanyGroup()
		throws Exception {

		Mockito.when(
			_stagingGroupHelper.isCompanyGroup(_COMPANY_ID, _GROUP_ID)
		).thenReturn(
			true
		);

		String externalReferenceCode = RandomTestUtil.randomString();

		StagedModel stagedModel = _getStagedModel(externalReferenceCode);

		String modelClassName = RandomTestUtil.randomString();

		Mockito.when(
			stagedModel.getModelClassName()
		).thenReturn(
			modelClassName
		);

		_missingReferenceExportImportLifecycleListener.
			onExportImportLifecycleEvent(
				_getExportImportLifecycleEvent(
					ExportImportLifecycleConstants.
						EVENT_STAGED_MODEL_IMPORT_SUCCEEDED,
					stagedModel));

		Mockito.verify(
			_emptyModelManager
		).resolveMissingReference(
			modelClassName, _COMPANY_ID, externalReferenceCode, 0
		);
	}

	@Test
	public void testOnExportImportLifecycleEventWithFileEntry()
		throws Exception {

		FileEntry fileEntry = Mockito.mock(FileEntry.class);

		String externalReferenceCode = RandomTestUtil.randomString();

		Mockito.when(
			fileEntry.getExternalReferenceCode()
		).thenReturn(
			externalReferenceCode
		);

		Mockito.when(
			fileEntry.getCompanyId()
		).thenReturn(
			_COMPANY_ID
		);

		Mockito.when(
			fileEntry.getGroupId()
		).thenReturn(
			_GROUP_ID
		);

		Mockito.when(
			fileEntry.getModelClassName()
		).thenReturn(
			RandomTestUtil.randomString()
		);

		_missingReferenceExportImportLifecycleListener.
			onExportImportLifecycleEvent(
				_getExportImportLifecycleEvent(
					ExportImportLifecycleConstants.
						EVENT_STAGED_MODEL_IMPORT_SUCCEEDED,
					fileEntry));

		Mockito.verify(
			_emptyModelManager
		).resolveMissingReference(
			FileEntry.class.getName(), _COMPANY_ID, externalReferenceCode,
			_GROUP_ID
		);
	}

	@Test
	public void testOnExportImportLifecycleEventWithStagedModel()
		throws Exception {

		String externalReferenceCode = RandomTestUtil.randomString();

		StagedModel stagedModel = _getStagedModel(externalReferenceCode);

		String modelClassName = RandomTestUtil.randomString();

		Mockito.when(
			stagedModel.getModelClassName()
		).thenReturn(
			modelClassName
		);

		_missingReferenceExportImportLifecycleListener.
			onExportImportLifecycleEvent(
				_getExportImportLifecycleEvent(
					ExportImportLifecycleConstants.
						EVENT_STAGED_MODEL_IMPORT_SUCCEEDED,
					stagedModel));

		Mockito.verify(
			_emptyModelManager
		).resolveMissingReference(
			modelClassName, _COMPANY_ID, externalReferenceCode, _GROUP_ID
		);
	}

	@Test
	public void testOnExportImportLifecycleEventWithoutExternalReferenceCode()
		throws Exception {

		_missingReferenceExportImportLifecycleListener.
			onExportImportLifecycleEvent(
				_getExportImportLifecycleEvent(
					ExportImportLifecycleConstants.
						EVENT_STAGED_MODEL_IMPORT_SUCCEEDED,
					Mockito.mock(StagedGroupedModel.class)));
	}

	@Test
	public void testOnExportImportLifecycleEventWithoutGroup()
		throws Exception {

		StagedModel stagedModel = Mockito.mock(
			StagedModel.class,
			Mockito.withSettings(
			).extraInterfaces(
				ExternalReferenceCodeModel.class
			));

		_missingReferenceExportImportLifecycleListener.
			onExportImportLifecycleEvent(
				_getExportImportLifecycleEvent(
					ExportImportLifecycleConstants.
						EVENT_STAGED_MODEL_IMPORT_SUCCEEDED,
					stagedModel));
	}

	private ExportImportLifecycleEvent _getExportImportLifecycleEvent(
		int code, StagedModel stagedModel) {

		ExportImportLifecycleEvent exportImportLifecycleEvent = Mockito.mock(
			ExportImportLifecycleEvent.class);

		Mockito.when(
			exportImportLifecycleEvent.getCode()
		).thenReturn(
			code
		);

		Mockito.when(
			exportImportLifecycleEvent.getAttributes()
		).thenReturn(
			Arrays.<Serializable>asList(
				Mockito.mock(PortletDataContext.class),
				new TransientValue<>(stagedModel))
		);

		return exportImportLifecycleEvent;
	}

	private StagedModel _getStagedModel(String externalReferenceCode) {
		StagedGroupedModel stagedGroupedModel = Mockito.mock(
			StagedGroupedModel.class,
			Mockito.withSettings(
			).extraInterfaces(
				ExternalReferenceCodeModel.class
			));

		ExternalReferenceCodeModel externalReferenceCodeModel =
			(ExternalReferenceCodeModel)stagedGroupedModel;

		Mockito.when(
			externalReferenceCodeModel.getExternalReferenceCode()
		).thenReturn(
			externalReferenceCode
		);

		Mockito.when(
			stagedGroupedModel.getCompanyId()
		).thenReturn(
			_COMPANY_ID
		);

		Mockito.when(
			stagedGroupedModel.getGroupId()
		).thenReturn(
			_GROUP_ID
		);

		return stagedGroupedModel;
	}

	private static final long _COMPANY_ID = RandomTestUtil.randomLong();

	private static final long _GROUP_ID = RandomTestUtil.randomLong();

	private final EmptyModelManager _emptyModelManager = Mockito.mock(
		EmptyModelManager.class);
	private final MissingReferenceExportImportLifecycleListener
		_missingReferenceExportImportLifecycleListener =
			new MissingReferenceExportImportLifecycleListener();
	private final StagingGroupHelper _stagingGroupHelper = Mockito.mock(
		StagingGroupHelper.class);

}