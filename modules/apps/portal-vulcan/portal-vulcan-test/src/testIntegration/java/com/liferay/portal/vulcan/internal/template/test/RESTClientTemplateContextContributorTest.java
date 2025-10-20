/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.vulcan.internal.template.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.HTTPTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ZipFileTestUtil;
import com.liferay.portal.kernel.util.Http;
import com.liferay.portal.kernel.zip.ZipWriterFactory;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import java.io.InputStream;

import org.hamcrest.CoreMatchers;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;

/**
 * @author Alejandro Tardín
 */
@RunWith(Arquillian.class)
public class RESTClientTemplateContextContributorTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Test
	public void test() throws Exception {
		Bundle bundle = FrameworkUtil.getBundle(
			RESTClientTemplateContextContributorTest.class);

		BundleContext bundleContext = bundle.getBundleContext();

		bundle = bundleContext.installBundle(
			RandomTestUtil.randomString(), _toInputStream());

		bundle.start();

		try {
			JSONObject jsonObject = HTTPTestUtil.invokeToJSONObject(
				JSONUtil.put(
					"name", RandomTestUtil.randomString()
				).put(
					"templateKey",
					"com.liferay.portal.vulcan.test.site.initializer"
				).put(
					"templateType", "site-initializer"
				).toString(),
				"headless-site/v1.0/sites", Http.Method.POST);

			HTTPTestUtil.customize(
			).withoutModulePath(
			).apply(
				() -> Assert.assertThat(
					HTTPTestUtil.invokeToString(
						null,
						"web" + jsonObject.getString("friendlyUrlPath") +
							"/portal-vulcan-test",
						Http.Method.GET),
					CoreMatchers.containsString("Countries: spain"))
			);
		}
		finally {
			bundle.uninstall();
		}
	}

	private InputStream _toInputStream() throws Exception {
		String basePath =
			"com/liferay/portal/vulcan/internal/template/test/dependencies" +
				"/site-initializer/";

		return ZipFileTestUtil.toInputStream(
			basePath,
			FrameworkUtil.getBundle(
				RESTClientTemplateContextContributorTest.class),
			_zipWriterFactory.getZipWriter());
	}

	@Inject
	private ZipWriterFactory _zipWriterFactory;

}