/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.vulcan.internal.template.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.portal.configuration.test.util.CompanyConfigurationTemporarySwapper;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.HTTPTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.ZipFileTestUtil;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.kernel.util.Http;
import com.liferay.portal.kernel.util.PropsValues;
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
			String friendlyUrlPath = HTTPTestUtil.invokeToJSONObject(
				JSONUtil.put(
					"name", RandomTestUtil.randomString()
				).put(
					"templateKey",
					"com.liferay.portal.vulcan.test.site.initializer"
				).put(
					"templateType", "site-initializer"
				).toString(),
				"headless-admin-site/v1.0/sites", Http.Method.POST
			).getString(
				"friendlyUrlPath"
			);

			boolean termsOfUseRequired = PropsValues.TERMS_OF_USE_REQUIRED;

			PropsValues.TERMS_OF_USE_REQUIRED = false;

			try {
				HTTPTestUtil.customize(
				).withoutModulePath(
				).apply(
					() -> _test(friendlyUrlPath, TestPropsValues.getUser())
				);
			}
			finally {
				PropsValues.TERMS_OF_USE_REQUIRED = termsOfUseRequired;
			}

			HTTPTestUtil.customize(
			).withoutModulePath(
			).apply(
				() -> HTTPTestUtil.invokeToString(
					null, "c/portal/logout", Http.Method.GET)
			);

			HTTPTestUtil.customize(
			).withoutModulePath(
			).withGuest(
			).apply(
				() -> _test(
					friendlyUrlPath,
					_userLocalService.getGuestUser(
						TestPropsValues.getCompanyId()))
			);
		}
		finally {
			bundle.uninstall();
		}
	}

	private void _test(String friendlyUrlPath, User user) throws Exception {
		try (CompanyConfigurationTemporarySwapper
				companyConfigurationTemporarySwapper =
					new CompanyConfigurationTemporarySwapper(
						TestPropsValues.getCompanyId(),
						"com.liferay.portal.security.auto.login.basic.auth." +
							"header",
						HashMapDictionaryBuilder.<String, Object>put(
							"enabled", true
						).build())) {

			Assert.assertThat(
				HTTPTestUtil.invokeToString(
					null,
					"web" + friendlyUrlPath + "/portal-vulcan-test?pageSize=2",
					Http.Method.GET),
				CoreMatchers.allOf(
					CoreMatchers.containsString("Name: spain."),
					CoreMatchers.containsString("Page Size (default): 20."),
					CoreMatchers.containsString("Page Size (query): 1."),
					CoreMatchers.containsString(
						"User: " + user.getScreenName() + ".")));
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
	private UserLocalService _userLocalService;

	@Inject
	private ZipWriterFactory _zipWriterFactory;

}