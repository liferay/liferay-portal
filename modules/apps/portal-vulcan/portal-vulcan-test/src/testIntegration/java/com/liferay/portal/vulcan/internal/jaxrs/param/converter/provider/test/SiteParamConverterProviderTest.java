/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.vulcan.internal.jaxrs.param.converter.provider.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.GroupConstants;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.GroupLocalServiceUtil;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.test.log.LogCapture;
import com.liferay.portal.test.log.LoggerTestUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.vulcan.internal.test.util.URLConnectionUtil;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.core.Application;

import java.io.FileNotFoundException;

import java.util.Collections;
import java.util.Set;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceRegistration;

/**
 * @author Cristina González
 */
@RunWith(Arquillian.class)
public class SiteParamConverterProviderTest {

	@ClassRule
	@Rule
	public static final LiferayIntegrationTestRule liferayIntegrationTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() {
		Bundle bundle = FrameworkUtil.getBundle(
			SiteParamConverterProviderTest.class);

		BundleContext bundleContext = bundle.getBundleContext();

		_serviceRegistration = bundleContext.registerService(
			Application.class,
			new SiteParamConverterProviderTest.TestApplication(),
			HashMapDictionaryBuilder.<String, Object>put(
				"liferay.auth.verifier", true
			).put(
				"liferay.oauth2", false
			).put(
				"osgi.jaxrs.application.base", "/test-vulcan"
			).put(
				"osgi.jaxrs.extension.select",
				"(osgi.jaxrs.name=Liferay.Vulcan)"
			).build());
	}

	@After
	public void tearDown() {
		_serviceRegistration.unregister();
	}

	@Test(expected = FileNotFoundException.class)
	public void testInValidGroup() throws Exception {
		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				_CLASS_NAME_WEB_APPLICATION_EXCEPTION_MAPPER,
				LoggerTestUtil.ERROR)) {

			URLConnectionUtil.read(_TEST_BASE_URL + "0/name");
		}
	}

	@Test
	@TestInfo("LPD-53838")
	public void testValidGroup() throws Exception {
		long defaultCompanyId = _portal.getDefaultCompanyId();

		User user = UserTestUtil.getAdminUser(defaultCompanyId);

		Group group = GroupTestUtil.addGroup(
			defaultCompanyId, user.getUserId(),
			GroupConstants.DEFAULT_PARENT_GROUP_ID);

		String expectedGroupName = group.getName(LocaleUtil.getDefault());

		Assert.assertEquals(
			expectedGroupName,
			URLConnectionUtil.read(
				_TEST_BASE_URL + group.getExternalReferenceCode() + "/name"));
		Assert.assertEquals(
			expectedGroupName,
			URLConnectionUtil.read(
				_TEST_BASE_URL + group.getGroupId() + "/name"));
		Assert.assertEquals(
			expectedGroupName,
			URLConnectionUtil.read(
				_TEST_BASE_URL + group.getGroupKey() + "/name"));
	}

	public static class TestApplication extends Application {

		@Override
		public Set<Object> getSingletons() {
			return Collections.singleton(this);
		}

		@GET
		@Path("/{siteId}/name")
		public String testClass(@PathParam("siteId") Long siteId)
			throws Exception {

			Group group = GroupLocalServiceUtil.getGroup(siteId);

			return group.getName(LocaleUtil.getDefault());
		}

	}

	private static final String _CLASS_NAME_WEB_APPLICATION_EXCEPTION_MAPPER =
		"com.liferay.portal.vulcan.internal.jaxrs.exception.mapper." +
			"WebApplicationExceptionMapper";

	private static final String _TEST_BASE_URL =
		"http://localhost:8080/o/test-vulcan/";

	@Inject
	private Portal _portal;

	private ServiceRegistration<Application> _serviceRegistration;

}