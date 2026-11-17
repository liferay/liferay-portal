/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.exportimport.test.util.lar;

import com.liferay.exportimport.kernel.lar.ManifestSummary;
import com.liferay.exportimport.kernel.lar.PortletDataException;
import com.liferay.exportimport.kernel.lar.StagedModelDataHandlerUtil;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.portal.kernel.model.GroupConstants;
import com.liferay.portal.kernel.model.StagedModel;
import com.liferay.portal.kernel.service.GroupLocalServiceUtil;
import com.liferay.portal.kernel.test.AssertUtils;
import com.liferay.portal.test.rule.Inject;
import com.liferay.staging.StagingGroupHelper;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author Adolfo Pérez
 */
public abstract class BaseCompanyStagedModelDataHandlerTestCase
	extends BaseStagedModelDataHandlerTestCase {

	@Test
	public void testImportStagedModel() throws Exception {
		initExport();

		StagedModel stagedModel = addStagedModel(
			stagingGroup, addDependentStagedModelsMap(stagingGroup));

		StagedModelDataHandlerUtil.exportStagedModel(
			portletDataContext, stagedModel);

		// Company group

		try (SafeCloseable safeCloseable = initImportWithSafeCloseable()) {
			StagedModelDataHandlerUtil.importStagedModel(
				portletDataContext, readExportedStagedModel(stagedModel));

			ManifestSummary manifestSummary =
				portletDataContext.getManifestSummary();

			Assert.assertEquals(
				1, manifestSummary.getModelAdditionCount(stagedModel));
		}

		// Control panel group

		try (SafeCloseable safeCloseable = initImportWithSafeCloseable(
				stagingGroup,
				GroupLocalServiceUtil.getGroup(
					liveGroup.getCompanyId(), GroupConstants.CONTROL_PANEL))) {

			StagedModelDataHandlerUtil.importStagedModel(
				portletDataContext, readExportedStagedModel(stagedModel));

			ManifestSummary manifestSummary =
				portletDataContext.getManifestSummary();

			Assert.assertEquals(
				1, manifestSummary.getModelAdditionCount(stagedModel));
		}

		// Site group

		try (SafeCloseable safeCloseable = initImportWithSafeCloseable(
				stagingGroup, liveGroup)) {

			AssertUtils.assertFailure(
				PortletDataException.class,
				"Unable to import a company scoped element into a site group",
				() -> StagedModelDataHandlerUtil.importStagedModel(
					portletDataContext, readExportedStagedModel(stagedModel)));
		}
	}

	@Override
	protected SafeCloseable initImportWithSafeCloseable() throws Exception {
		return initImportWithSafeCloseable(
			stagingGroup,
			_stagingGroupHelper.fetchCompanyGroup(liveGroup.getCompanyId()));
	}

	@Inject
	private StagingGroupHelper _stagingGroupHelper;

}