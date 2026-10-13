/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.launch.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.portal.kernel.launch.LaunchPreviewThreadLocal;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.HttpComponentsUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Alejandro Tardín
 */
@RunWith(Arquillian.class)
public class LaunchPreviewThreadLocalTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Test
	public void testAddPreservedParameters() {
		String url = "/web/guest/" + RandomTestUtil.randomString();

		Assert.assertEquals(
			url,
			PortalUtil.addPreservedParameters(
				new ThemeDisplay(), url, false, false));

		long launchSetId = RandomTestUtil.randomLong();

		try (SafeCloseable safeCloseable =
				LaunchPreviewThreadLocal.setLaunchSetIdWithSafeCloseable(
					launchSetId)) {

			_assertPreservedParameters(String.valueOf(launchSetId), url);
		}

		try (SafeCloseable safeCloseable =
				LaunchPreviewThreadLocal.setLaunchSetIdWithSafeCloseable(0)) {

			_assertPreservedParameters("0", url);
		}

		Assert.assertEquals(
			url,
			PortalUtil.addPreservedParameters(
				new ThemeDisplay(), url, false, false));
	}

	private void _assertPreservedParameters(
		String expectedLaunchSetId, String url) {

		String preservedURL = PortalUtil.addPreservedParameters(
			new ThemeDisplay(), url, false, false);

		Assert.assertEquals(
			Constants.PREVIEW,
			HttpComponentsUtil.getParameter(preservedURL, "p_l_mode", false));
		Assert.assertEquals(
			expectedLaunchSetId,
			HttpComponentsUtil.getParameter(
				preservedURL, "previewLaunchSetId", false));
	}

}