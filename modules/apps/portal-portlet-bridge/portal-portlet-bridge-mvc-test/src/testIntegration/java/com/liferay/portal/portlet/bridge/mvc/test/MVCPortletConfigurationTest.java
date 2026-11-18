/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.portlet.bridge.mvc.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.portlet.ConfigurationAction;
import com.liferay.portal.kernel.portlet.DefaultConfigurationAction;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCPortlet;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletRenderRequest;
import com.liferay.portal.kernel.test.portlet.MockLiferayResourceRequest;
import com.liferay.portal.kernel.test.portlet.MockLiferayResourceResponse;
import com.liferay.portal.kernel.test.portlet.MockRenderResponse;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import jakarta.portlet.Portlet;
import jakarta.portlet.PortletException;
import jakarta.portlet.PortletRequest;
import jakarta.portlet.PortletResponse;
import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;

import java.io.IOException;

import java.util.Dictionary;

import org.junit.After;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceRegistration;

/**
 * @author Antonio Ortega
 */
@RunWith(Arquillian.class)
public class MVCPortletConfigurationTest {

	@After
	public void tearDown() {
		_unregister();
	}

	@Test
	public void testDoDispatchDoesNotServeConfigurationJSPUnderViewMode()
		throws Exception {

		// Config JSP

		String configJSP = "/" + RandomTestUtil.randomString() + ".jsp";

		_testDoDispatch(
			configJSP,
			HashMapDictionaryBuilder.<String, Object>put(
				"jakarta.portlet.init-param.config-jsp", configJSP
			).build());

		// Config template

		String configTemplate = "/" + RandomTestUtil.randomString() + ".jsp";

		_testDoDispatch(
			configTemplate,
			HashMapDictionaryBuilder.<String, Object>put(
				"jakarta.portlet.init-param.config-template", configTemplate
			).build());

		// Meta-INF resources config template

		_testDoDispatch(
			"/META-INF/resources" + configTemplate,
			HashMapDictionaryBuilder.<String, Object>put(
				"jakarta.portlet.init-param.config-template", configTemplate
			).build());

		// Nested configuration JSP

		_testDoDispatch(
			StringBundler.concat(
				"/", RandomTestUtil.randomString(), "/configuration/",
				RandomTestUtil.randomString(), ".jsp"),
			HashMapDictionaryBuilder.<String, Object>put(
				"jakarta.portlet.init-param.config-template",
				"/" + RandomTestUtil.randomString() + ".jsp"
			).build());

		// Sibling configuration JSP

		_testDoDispatch(
			"/" + RandomTestUtil.randomString() + "/configuration.jsp",
			HashMapDictionaryBuilder.<String, Object>put(
				"jakarta.portlet.init-param.config-template",
				"/" + RandomTestUtil.randomString() + ".jsp"
			).build());
	}

	@Test
	public void testServeResourceDoesNotServeConfigurationJSPUnderViewMode()
		throws Exception {

		String configTemplate = "/" + RandomTestUtil.randomString() + ".jsp";

		for (String mvcPath :
				new String[] {
					configTemplate,
					StringBundler.concat(
						"/", RandomTestUtil.randomString(), "/configuration/",
						RandomTestUtil.randomString(), ".jsp")
				}) {

			TestMVCPortlet testMVCPortlet = _registerMVCPortlet(
				HashMapDictionaryBuilder.<String, Object>put(
					"jakarta.portlet.init-param.config-template", configTemplate
				).build());

			MockLiferayResourceRequest mockLiferayResourceRequest =
				new MockLiferayResourceRequest();

			mockLiferayResourceRequest.setParameter("mvcPath", mvcPath);

			testMVCPortlet.serveResource(
				mockLiferayResourceRequest, new MockLiferayResourceResponse());

			Assert.assertEquals(0, testMVCPortlet.getIncludesCount());

			_unregister();
		}
	}

	@Rule
	public final LiferayIntegrationTestRule liferayIntegrationTestRule =
		new LiferayIntegrationTestRule();

	private TestMVCPortlet _registerMVCPortlet(
		Dictionary<String, Object> properties) {

		TestMVCPortlet testMVCPortlet = new TestMVCPortlet();

		String portletName = "testPortlet" + RandomTestUtil.randomString();

		properties.put("jakarta.portlet.name", portletName);

		_configurationActionServiceRegistration =
			_bundleContext.registerService(
				ConfigurationAction.class, new DefaultConfigurationAction(),
				HashMapDictionaryBuilder.<String, Object>put(
					"jakarta.portlet.name", portletName
				).build());

		_portletServiceRegistration = _bundleContext.registerService(
			Portlet.class, testMVCPortlet, properties);

		return testMVCPortlet;
	}

	private void _testDoDispatch(
			String mvcPath, Dictionary<String, Object> properties)
		throws Exception {

		String viewTemplate = "/" + RandomTestUtil.randomString() + ".jsp";

		properties.put(
			"jakarta.portlet.init-param.view-template", viewTemplate);

		TestMVCPortlet testMVCPortlet = _registerMVCPortlet(properties);

		MockLiferayPortletRenderRequest mockLiferayPortletRenderRequest =
			new MockLiferayPortletRenderRequest();

		mockLiferayPortletRenderRequest.setAttribute(
			WebKeys.COMPANY_ID, TestPropsValues.getCompanyId());
		mockLiferayPortletRenderRequest.setParameter("mvcPath", mvcPath);

		testMVCPortlet.dispatch(
			mockLiferayPortletRenderRequest, new MockRenderResponse());

		Assert.assertEquals(1, testMVCPortlet.getIncludesCount());
		Assert.assertEquals(viewTemplate, testMVCPortlet.getIncludedPath());

		_unregister();
	}

	private void _unregister() {
		if (_configurationActionServiceRegistration != null) {
			_configurationActionServiceRegistration.unregister();

			_configurationActionServiceRegistration = null;
		}

		if (_portletServiceRegistration != null) {
			_portletServiceRegistration.unregister();

			_portletServiceRegistration = null;
		}
	}

	private final BundleContext _bundleContext;

	{
		Bundle bundle = FrameworkUtil.getBundle(
			MVCPortletConfigurationTest.class);

		_bundleContext = bundle.getBundleContext();
	}

	private ServiceRegistration<ConfigurationAction>
		_configurationActionServiceRegistration;
	private ServiceRegistration<Portlet> _portletServiceRegistration;

	private static class TestMVCPortlet extends MVCPortlet {

		public void dispatch(
				RenderRequest renderRequest, RenderResponse renderResponse)
			throws IOException, PortletException {

			doDispatch(renderRequest, renderResponse);
		}

		public String getIncludedPath() {
			return _includedPath;
		}

		public int getIncludesCount() {
			return _includesCount;
		}

		@Override
		protected void include(
			String path, PortletRequest portletRequest,
			PortletResponse portletResponse, String lifecycle) {

			_includesCount++;
			_includedPath = path;
		}

		private String _includedPath;
		private int _includesCount;

	}

}