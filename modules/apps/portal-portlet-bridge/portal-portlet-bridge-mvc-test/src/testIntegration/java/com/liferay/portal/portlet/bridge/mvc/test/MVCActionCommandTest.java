/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.portlet.bridge.mvc.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.model.Portlet;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCPortlet;
import com.liferay.portal.kernel.test.portlet.MockActionResponse;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletActionRequest;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import jakarta.portlet.ActionRequest;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceRegistration;

/**
 * @author Manuel de la Peña
 */
@RunWith(Arquillian.class)
public class MVCActionCommandTest {

	@ClassRule
	@Rule
	public static final LiferayIntegrationTestRule liferayIntegrationTestRule =
		new LiferayIntegrationTestRule();

	@BeforeClass
	public static void setUpClass() {
		Bundle bundle = FrameworkUtil.getBundle(MVCActionCommandTest.class);

		BundleContext bundleContext = bundle.getBundleContext();

		_portlet = new MVCPortlet();

		_portletServiceRegistration = bundleContext.registerService(
			jakarta.portlet.Portlet.class, _portlet,
			HashMapDictionaryBuilder.<String, Object>put(
				"jakarta.portlet.init-param.copy-request-parameters", "false"
			).put(
				"jakarta.portlet.name", _PORTLET_NAME
			).build());

		_mvcActionCommandServiceRegistration1 = bundleContext.registerService(
			MVCActionCommand.class,
			(actionRequest, actionResponse) -> {
				actionRequest.setAttribute(
					_TEST_MVC_ACTION_COMMAND_ATTRIBUTE_1,
					_TEST_MVC_ACTION_COMMAND_ATTRIBUTE_1);

				return true;
			},
			HashMapDictionaryBuilder.<String, Object>put(
				"jakarta.portlet.name", _PORTLET_NAME
			).put(
				"mvc.command.name", _TEST_MVC_ACTION_COMMAND_NAME_1
			).build());

		_mvcActionCommandServiceRegistration2 = bundleContext.registerService(
			MVCActionCommand.class,
			(actionRequest, actionResponse) -> {
				actionRequest.setAttribute(
					_TEST_MVC_ACTION_COMMAND_ATTRIBUTE_2,
					_TEST_MVC_ACTION_COMMAND_ATTRIBUTE_2);

				return true;
			},
			HashMapDictionaryBuilder.<String, Object>put(
				"jakarta.portlet.name", _PORTLET_NAME
			).put(
				"mvc.command.name", _TEST_MVC_ACTION_COMMAND_NAME_2
			).build());
	}

	@AfterClass
	public static void tearDownClass() {
		_portletServiceRegistration.unregister();
		_mvcActionCommandServiceRegistration1.unregister();
		_mvcActionCommandServiceRegistration2.unregister();
	}

	@Test
	public void testMultipleMVCActionCommandsWithMultipleParameters()
		throws Exception {

		MockLiferayPortletActionRequest mockLiferayPortletActionRequest =
			new MockLiferayPortletActionRequest();

		mockLiferayPortletActionRequest.addParameter(
			ActionRequest.ACTION_NAME, _TEST_MVC_ACTION_COMMAND_NAME_1);
		mockLiferayPortletActionRequest.addParameter(
			ActionRequest.ACTION_NAME, _TEST_MVC_ACTION_COMMAND_NAME_2);

		_portlet.processAction(
			mockLiferayPortletActionRequest, new MockActionResponse());

		Assert.assertNotNull(
			mockLiferayPortletActionRequest.getAttribute(
				_TEST_MVC_ACTION_COMMAND_ATTRIBUTE_1));
		Assert.assertEquals(
			_TEST_MVC_ACTION_COMMAND_ATTRIBUTE_1,
			mockLiferayPortletActionRequest.getAttribute(
				_TEST_MVC_ACTION_COMMAND_ATTRIBUTE_1));
		Assert.assertNotNull(
			mockLiferayPortletActionRequest.getAttribute(
				_TEST_MVC_ACTION_COMMAND_ATTRIBUTE_2));
		Assert.assertEquals(
			_TEST_MVC_ACTION_COMMAND_ATTRIBUTE_2,
			mockLiferayPortletActionRequest.getAttribute(
				_TEST_MVC_ACTION_COMMAND_ATTRIBUTE_2));
	}

	@Test
	public void testMultipleMVCActionCommandsWithSingleParameter()
		throws Exception {

		MockLiferayPortletActionRequest mockLiferayPortletActionRequest =
			new MockLiferayPortletActionRequest();

		mockLiferayPortletActionRequest.addParameter(
			ActionRequest.ACTION_NAME,
			_TEST_MVC_ACTION_COMMAND_NAME_1 + StringPool.COMMA +
				_TEST_MVC_ACTION_COMMAND_NAME_2);

		_portlet.processAction(
			mockLiferayPortletActionRequest, new MockActionResponse());

		Assert.assertNotNull(
			mockLiferayPortletActionRequest.getAttribute(
				_TEST_MVC_ACTION_COMMAND_ATTRIBUTE_1));
		Assert.assertEquals(
			_TEST_MVC_ACTION_COMMAND_ATTRIBUTE_1,
			mockLiferayPortletActionRequest.getAttribute(
				_TEST_MVC_ACTION_COMMAND_ATTRIBUTE_1));
		Assert.assertNotNull(
			mockLiferayPortletActionRequest.getAttribute(
				_TEST_MVC_ACTION_COMMAND_ATTRIBUTE_2));
		Assert.assertEquals(
			_TEST_MVC_ACTION_COMMAND_ATTRIBUTE_2,
			mockLiferayPortletActionRequest.getAttribute(
				_TEST_MVC_ACTION_COMMAND_ATTRIBUTE_2));
	}

	@Test
	public void testSingleMVCActionCommand() throws Exception {
		MockLiferayPortletActionRequest mockLiferayPortletActionRequest =
			new MockLiferayPortletActionRequest();

		mockLiferayPortletActionRequest.addParameter(
			ActionRequest.ACTION_NAME, _TEST_MVC_ACTION_COMMAND_NAME_1);

		_portlet.processAction(
			mockLiferayPortletActionRequest, new MockActionResponse());

		Assert.assertNotNull(
			mockLiferayPortletActionRequest.getAttribute(
				_TEST_MVC_ACTION_COMMAND_ATTRIBUTE_1));
		Assert.assertEquals(
			_TEST_MVC_ACTION_COMMAND_ATTRIBUTE_1,
			mockLiferayPortletActionRequest.getAttribute(
				_TEST_MVC_ACTION_COMMAND_ATTRIBUTE_1));
	}

	private static final String _PORTLET_NAME =
		"com_liferay_portal_kernel_portlet_bridges_mvc_test_" +
			"MVCActionCommandTest_Test-Portlet";

	private static final String _TEST_MVC_ACTION_COMMAND_ATTRIBUTE_1 =
		"TEST_MVC_ACTION_COMMAND_ATTRIBUTE_1";

	private static final String _TEST_MVC_ACTION_COMMAND_ATTRIBUTE_2 =
		"TEST_MVC_ACTION_COMMAND_ATTRIBUTE_2";

	private static final String _TEST_MVC_ACTION_COMMAND_NAME_1 =
		"TEST_MVC_ACTION_COMMAND_NAME_1";

	private static final String _TEST_MVC_ACTION_COMMAND_NAME_2 =
		"TEST_MVC_ACTION_COMMAND_NAME_2";

	private static ServiceRegistration<MVCActionCommand>
		_mvcActionCommandServiceRegistration1;
	private static ServiceRegistration<MVCActionCommand>
		_mvcActionCommandServiceRegistration2;
	private static jakarta.portlet.Portlet _portlet;
	private static ServiceRegistration<jakarta.portlet.Portlet>
		_portletServiceRegistration;

}