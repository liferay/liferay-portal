/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.exportimport.kernel.lar;

import com.liferay.portal.kernel.model.StagedModel;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.util.RandomTestUtil;

import org.junit.Test;

import org.mockito.Answers;
import org.mockito.ArgumentMatchers;
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
			Mockito.mock(
				BaseStagedModelDataHandler.class, Answers.CALLS_REAL_METHODS);

		Mockito.doReturn(
			false
		).when(
			baseStagedModelDataHandler
		).isEnabled(
			sourceCompanyId
		);

		Mockito.doReturn(
			true
		).when(
			baseStagedModelDataHandler
		).isEnabled(
			targetCompanyId
		);

		PortletDataContext portletDataContext = Mockito.mock(
			PortletDataContext.class);

		Mockito.when(
			portletDataContext.getCompanyId()
		).thenReturn(
			targetCompanyId
		);

		Mockito.when(
			portletDataContext.isPathProcessed(ArgumentMatchers.anyString())
		).thenReturn(
			true
		);

		StagedModel stagedModel = Mockito.mock(StagedModel.class);

		StagedModelType stagedModelType = Mockito.mock(StagedModelType.class);

		Mockito.when(
			stagedModelType.getClassName()
		).thenReturn(
			RandomTestUtil.randomString()
		);

		Mockito.when(
			stagedModel.getStagedModelType()
		).thenReturn(
			stagedModelType
		);

		Mockito.when(
			stagedModel.getCompanyId()
		).thenReturn(
			sourceCompanyId
		);

		Mockito.when(
			stagedModel.getPrimaryKeyObj()
		).thenReturn(
			RandomTestUtil.randomLong()
		);

		baseStagedModelDataHandler.importStagedModel(
			portletDataContext, stagedModel);

		Mockito.verify(
			portletDataContext
		).isPathProcessed(
			ArgumentMatchers.anyString()
		);
	}

}