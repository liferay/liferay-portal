/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.seo.studio.pagespeed.internal;

import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

/**
 * @author Kiana Suetani
 */
public class PageSpeedScoreProviderTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Test
	public void testIsValidConnection() {
		PageSpeedScoreProvider pageSpeedScoreProvider =
			new PageSpeedScoreProvider("apiKey", "DESKTOP");

		Assert.assertTrue(pageSpeedScoreProvider.isValidConnection());
	}

	@Test
	public void testIsValidConnectionWithEmptyAPIKey() {
		PageSpeedScoreProvider pageSpeedScoreProvider =
			new PageSpeedScoreProvider("", "DESKTOP");

		Assert.assertFalse(pageSpeedScoreProvider.isValidConnection());
	}

	@Test
	public void testIsValidConnectionWithNullAPIKey() {
		PageSpeedScoreProvider pageSpeedScoreProvider =
			new PageSpeedScoreProvider(null, "DESKTOP");

		Assert.assertFalse(pageSpeedScoreProvider.isValidConnection());
	}

	@Test
	public void testQuotaExceededExceptionWithErrorJSON() throws Exception {
		JSONObject errorJSONObject = JSONUtil.put(
			"error",
			JSONUtil.put(
				"code", 429
			).put(
				"message", "Quota exceeded"
			));

		PageSpeedScoreProvider.PageSpeedScoreProviderException
			pageSpeedScoreProviderException =
				new PageSpeedScoreProvider.PageSpeedScoreProviderException(
					errorJSONObject, "Quota exceeded");

		Assert.assertTrue(pageSpeedScoreProviderException.isQuotaExceeded());
		Assert.assertEquals(
			errorJSONObject,
			pageSpeedScoreProviderException.
				getGooglePageSpeedErrorJSONObject());
	}

	@Test
	public void testQuotaExceededExceptionWithNonquotaError() throws Exception {
		JSONObject errorJSONObject = JSONUtil.put(
			"error",
			JSONUtil.put(
				"code", 403
			).put(
				"message", "Forbidden"
			));

		PageSpeedScoreProvider.PageSpeedScoreProviderException
			pageSpeedScoreProviderException =
				new PageSpeedScoreProvider.PageSpeedScoreProviderException(
					errorJSONObject, "Forbidden");

		Assert.assertFalse(pageSpeedScoreProviderException.isQuotaExceeded());
	}

	@Test
	public void testQuotaExceededExceptionWithNullJSON() {
		PageSpeedScoreProvider.PageSpeedScoreProviderException
			pageSpeedScoreProviderException =
				new PageSpeedScoreProvider.PageSpeedScoreProviderException(
					"Some error");

		Assert.assertFalse(pageSpeedScoreProviderException.isQuotaExceeded());
	}

}