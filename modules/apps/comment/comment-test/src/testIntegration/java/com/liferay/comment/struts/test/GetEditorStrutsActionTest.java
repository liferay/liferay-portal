/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.comment.struts.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.util.HttpComponentsUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import java.net.HttpURLConnection;
import java.net.URL;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Jürgen Kappler
 */
@RunWith(Arquillian.class)
public class GetEditorStrutsActionTest {

	@ClassRule
	@Rule
	public static final LiferayIntegrationTestRule liferayIntegrationTestRule =
		new LiferayIntegrationTestRule();

	@Test
	@TestInfo("LPD-109768")
	public void testExecute() throws Exception {
		HttpURLConnection httpURLConnection = _openHttpURLConnection(
			"_namespace_");

		Assert.assertEquals(200, httpURLConnection.getResponseCode());

		String content = StringUtil.read(httpURLConnection.getInputStream());

		Assert.assertTrue(content, content.contains("_namespace_body"));

		httpURLConnection.disconnect();

		_testExecuteWithInvalidNamespace();
	}

	private HttpURLConnection _openHttpURLConnection(String namespace)
		throws Exception {

		String urlString =
			"http://localhost:" + PortalUtil.getPortalServerPort(false) +
				"/c/portal/comment/discussion/get_editor";

		urlString = HttpComponentsUtil.addParameter(urlString, "name", "body");
		urlString = HttpComponentsUtil.addParameter(
			urlString, "namespace", namespace);

		URL url = new URL(urlString);

		HttpURLConnection httpURLConnection =
			(HttpURLConnection)url.openConnection();

		httpURLConnection.setRequestMethod("GET");

		return httpURLConnection;
	}

	private void _testExecuteWithInvalidNamespace() throws Exception {
		HttpURLConnection httpURLConnection = _openHttpURLConnection(
			"_name\"space_");

		Assert.assertEquals(400, httpURLConnection.getResponseCode());

		String content = StringUtil.read(httpURLConnection.getErrorStream());

		Assert.assertFalse(content, content.contains("name\"space"));

		httpURLConnection.disconnect();
	}

}