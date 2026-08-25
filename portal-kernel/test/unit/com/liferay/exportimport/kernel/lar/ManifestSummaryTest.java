/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.exportimport.kernel.lar;

import com.liferay.portal.kernel.model.Portlet;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.util.RandomTestUtil;

import java.util.Date;

import org.junit.Assert;
import org.junit.Test;

import org.mockito.Mockito;

/**
 * @author Adolfo Pérez
 */
public class ManifestSummaryTest {

	@Test
	@TestInfo("LPD-103412")
	public void testClone() {
		ManifestSummary manifestSummary = new ManifestSummary();

		String className = RandomTestUtil.randomString();
		String assetTitle = RandomTestUtil.randomString();

		manifestSummary.addAssetTitle(className, assetTitle);

		String dataPortletRootPortletId = RandomTestUtil.randomString();
		String[] dataPortletConfigurationPortletOptions = {
			RandomTestUtil.randomString()
		};

		manifestSummary.addDataPortlet(
			_createPortlet(dataPortletRootPortletId),
			dataPortletConfigurationPortletOptions);

		String layoutPortletRootPortletId = RandomTestUtil.randomString();
		String[] layoutPortletConfigurationPortletOptions = {
			RandomTestUtil.randomString()
		};

		manifestSummary.addLayoutPortlet(
			_createPortlet(layoutPortletRootPortletId),
			layoutPortletConfigurationPortletOptions);

		String additionManifestSummaryKey = RandomTestUtil.randomString();
		long modelAdditionCount = RandomTestUtil.randomLong();

		manifestSummary.addModelAdditionCount(
			additionManifestSummaryKey, modelAdditionCount);

		String deletionManifestSummaryKey = RandomTestUtil.randomString();
		long modelDeletionCount = RandomTestUtil.randomLong();

		manifestSummary.addModelDeletionCount(
			deletionManifestSummaryKey, modelDeletionCount);

		Date exportDate = new Date();

		manifestSummary.setExportDate(exportDate);

		ManifestSummary clonedManifestSummary =
			(ManifestSummary)manifestSummary.clone();

		Assert.assertEquals(
			assetTitle,
			clonedManifestSummary.getStagedModelAssetTitle(className));
		Assert.assertArrayEquals(
			dataPortletConfigurationPortletOptions,
			clonedManifestSummary.getConfigurationPortletOptions(
				dataPortletRootPortletId));
		Assert.assertEquals(exportDate, clonedManifestSummary.getExportDate());
		Assert.assertNotSame(exportDate, clonedManifestSummary.getExportDate());
		Assert.assertArrayEquals(
			layoutPortletConfigurationPortletOptions,
			clonedManifestSummary.getConfigurationPortletOptions(
				layoutPortletRootPortletId));
		Assert.assertEquals(
			manifestSummary.getDataPortlets(),
			clonedManifestSummary.getDataPortlets());
		Assert.assertEquals(
			manifestSummary.getLayoutPortlets(),
			clonedManifestSummary.getLayoutPortlets());
		Assert.assertEquals(
			manifestSummary.getManifestSummaryKeys(),
			clonedManifestSummary.getManifestSummaryKeys());
		Assert.assertEquals(
			modelAdditionCount,
			clonedManifestSummary.getModelAdditionCount(
				additionManifestSummaryKey));
		Assert.assertEquals(
			modelDeletionCount,
			clonedManifestSummary.getModelDeletionCount(
				deletionManifestSummaryKey));
	}

	private Portlet _createPortlet(String rootPortletId) {
		Portlet portlet = Mockito.mock(Portlet.class);

		Mockito.when(
			portlet.getRootPortletId()
		).thenReturn(
			rootPortletId
		);

		return portlet;
	}

}