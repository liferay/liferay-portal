/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.exportimport.kernel.lar;

import com.liferay.portal.kernel.model.StagedModel;
import com.liferay.portal.kernel.model.TrashedModel;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.util.RandomTestUtil;

import org.junit.Test;

import org.mockito.ArgumentMatchers;
import org.mockito.MockSettings;
import org.mockito.Mockito;

/**
 * @author Brian Wulbern
 */
public class BaseStagedModelDataHandlerTest {

	@Test
	@TestInfo("LPD-108159")
	public void testImportStagedModelWithStagedModelFromAnotherCompany()
		throws Exception {

		long sourceCompanyId = RandomTestUtil.randomLong();
		long targetCompanyId = RandomTestUtil.randomLong();

		BaseStagedModelDataHandler<StagedModel> baseStagedModelDataHandler =
			_getBaseStagedModelDataHandler(sourceCompanyId, targetCompanyId);

		PortletDataContext portletDataContext = _getPortletDataContext(
			targetCompanyId);

		baseStagedModelDataHandler.importStagedModel(
			portletDataContext, _getStagedModel(sourceCompanyId, false));

		Mockito.verify(
			portletDataContext
		).isPathProcessed(
			ArgumentMatchers.anyString()
		);
	}

	@Test
	@TestInfo("LPD-108159")
	public void testImportStagedModelWithTargetCompanyDisabled()
		throws Exception {

		long sourceCompanyId = RandomTestUtil.randomLong();
		long targetCompanyId = RandomTestUtil.randomLong();

		BaseStagedModelDataHandler<StagedModel> baseStagedModelDataHandler =
			_getBaseStagedModelDataHandler(targetCompanyId, sourceCompanyId);

		PortletDataContext portletDataContext = _getPortletDataContext(
			targetCompanyId);

		baseStagedModelDataHandler.importStagedModel(
			portletDataContext, _getStagedModel(sourceCompanyId, false));

		Mockito.verify(
			portletDataContext, Mockito.never()
		).isPathProcessed(
			ArgumentMatchers.anyString()
		);
	}

	@Test
	@TestInfo("LPD-108159")
	public void testRestoreStagedModelWithStagedModelFromAnotherCompany()
		throws Exception {

		long sourceCompanyId = RandomTestUtil.randomLong();
		long targetCompanyId = RandomTestUtil.randomLong();

		BaseStagedModelDataHandler<StagedModel> baseStagedModelDataHandler =
			_getBaseStagedModelDataHandler(sourceCompanyId, targetCompanyId);

		Mockito.doNothing(
		).when(
			baseStagedModelDataHandler
		).doRestoreStagedModel(
			ArgumentMatchers.any(), ArgumentMatchers.any()
		);

		PortletDataContext portletDataContext = _getPortletDataContext(
			targetCompanyId);

		StagedModel stagedModel = _getStagedModel(sourceCompanyId, true);

		baseStagedModelDataHandler.restoreStagedModel(
			portletDataContext, stagedModel);

		Mockito.verify(
			baseStagedModelDataHandler
		).doRestoreStagedModel(
			portletDataContext, stagedModel
		);
	}

	private BaseStagedModelDataHandler<StagedModel>
		_getBaseStagedModelDataHandler(
			long disabledCompanyId, long enabledCompanyId) {

		BaseStagedModelDataHandler<StagedModel> baseStagedModelDataHandler =
			Mockito.mock(
				BaseStagedModelDataHandler.class, Mockito.CALLS_REAL_METHODS);

		Mockito.doReturn(
			false
		).when(
			baseStagedModelDataHandler
		).isEnabled(
			disabledCompanyId
		);

		Mockito.doReturn(
			true
		).when(
			baseStagedModelDataHandler
		).isEnabled(
			enabledCompanyId
		);

		return baseStagedModelDataHandler;
	}

	private PortletDataContext _getPortletDataContext(long companyId) {
		PortletDataContext portletDataContext = Mockito.mock(
			PortletDataContext.class);

		Mockito.when(
			portletDataContext.getCompanyId()
		).thenReturn(
			companyId
		);

		Mockito.when(
			portletDataContext.isPathProcessed(ArgumentMatchers.anyString())
		).thenReturn(
			true
		);

		return portletDataContext;
	}

	private StagedModel _getStagedModel(long companyId, boolean trashed) {
		MockSettings mockSettings = Mockito.withSettings();

		if (trashed) {
			mockSettings.extraInterfaces(TrashedModel.class);
		}

		StagedModel stagedModel = Mockito.mock(StagedModel.class, mockSettings);

		StagedModelType stagedModelType = Mockito.mock(StagedModelType.class);

		Mockito.when(
			stagedModelType.getClassName()
		).thenReturn(
			RandomTestUtil.randomString()
		);

		Mockito.when(
			stagedModel.getCompanyId()
		).thenReturn(
			companyId
		);

		Mockito.when(
			stagedModel.getPrimaryKeyObj()
		).thenReturn(
			RandomTestUtil.randomLong()
		);

		Mockito.when(
			stagedModel.getStagedModelType()
		).thenReturn(
			stagedModelType
		);

		return stagedModel;
	}

}