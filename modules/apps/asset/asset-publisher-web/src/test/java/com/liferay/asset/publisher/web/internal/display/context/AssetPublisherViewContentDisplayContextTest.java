/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.asset.publisher.web.internal.display.context;

import com.liferay.asset.kernel.model.AssetEntry;
import com.liferay.asset.kernel.model.AssetRenderer;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletRenderRequest;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.test.rule.LiferayUnitTestRule;
import com.liferay.portal.util.PortalImpl;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.Mockito;

/**
 * @author Jesus Antonio
 */
public class AssetPublisherViewContentDisplayContextTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() {
		PortalUtil portalUtil = new PortalUtil();

		portalUtil.setPortal(new PortalImpl());
	}

	@Test
	public void testIsAssetEntryVisible() {
		Assert.assertFalse(_isAssetEntryVisible("1", false));
		Assert.assertFalse(_isAssetEntryVisible("2", true));
		Assert.assertTrue(_isAssetEntryVisible("1", true));
	}

	private boolean _isAssetEntryVisible(
		String assetEntryId, boolean workflowAssetPreview) {

		MockLiferayPortletRenderRequest mockLiferayPortletRenderRequest =
			new MockLiferayPortletRenderRequest();

		if (workflowAssetPreview) {
			mockLiferayPortletRenderRequest.setAttribute(
				WebKeys.WORKFLOW_ASSET_PREVIEW, Boolean.TRUE);
		}

		mockLiferayPortletRenderRequest.setParameter(
			"assetEntryId", assetEntryId);

		AssetPublisherViewContentDisplayContext
			assetPublisherViewContentDisplayContext =
				new AssetPublisherViewContentDisplayContext(
					mockLiferayPortletRenderRequest, false);

		AssetEntry assetEntry = Mockito.mock(AssetEntry.class);

		Mockito.when(
			assetEntry.getEntryId()
		).thenReturn(
			1L
		);

		Mockito.when(
			assetEntry.isVisible()
		).thenReturn(
			false
		);

		ReflectionTestUtil.setFieldValue(
			assetPublisherViewContentDisplayContext, "_assetEntry", assetEntry);

		ReflectionTestUtil.setFieldValue(
			assetPublisherViewContentDisplayContext, "_assetRenderer",
			Mockito.mock(AssetRenderer.class));

		return assetPublisherViewContentDisplayContext.isAssetEntryVisible();
	}

}