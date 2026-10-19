/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.fragment.web.internal.display.context.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.configuration.admin.constants.ConfigurationAdminPortletKeys;
import com.liferay.configuration.admin.display.ConfigurationFormRenderer;
import com.liferay.fragment.configuration.FragmentServiceConfiguration;
import com.liferay.layout.test.util.ContentLayoutTestUtil;
import com.liferay.layout.test.util.LayoutTestUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.configuration.module.configuration.ConfigurationProvider;
import com.liferay.portal.configuration.test.util.ConfigurationTestUtil;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.portlet.LiferayPortletConfig;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletRenderRequest;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletRenderResponse;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.kernel.util.JavaConstants;
import com.liferay.portal.kernel.util.ProxyUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import java.util.Objects;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

/**
 * @author Javier Moral
 */
@RunWith(Arquillian.class)
public class FragmentServiceConfigurationDisplayContextTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();

		_company = _companyLocalService.getCompany(_group.getCompanyId());
		_layout = LayoutTestUtil.addTypePortletLayout(_group);

		_companyPropagateChanges = RandomTestUtil.randomBoolean();
		_companyPropagateContributedFragmentChanges =
			RandomTestUtil.randomBoolean();

		_configurationProvider.saveCompanyConfiguration(
			FragmentServiceConfiguration.class, _company.getCompanyId(),
			HashMapDictionaryBuilder.<String, Object>put(
				"propagateChanges", _companyPropagateChanges
			).put(
				"propagateContributedFragmentChanges",
				_companyPropagateContributedFragmentChanges
			).build());

		_systemPropagateChanges = RandomTestUtil.randomBoolean();
		_systemPropagateContributedFragmentChanges =
			RandomTestUtil.randomBoolean();

		ConfigurationTestUtil.saveConfiguration(
			FragmentServiceConfiguration.class.getName(),
			HashMapDictionaryBuilder.<String, Object>put(
				"propagateChanges", _systemPropagateChanges
			).put(
				"propagateContributedFragmentChanges",
				_systemPropagateContributedFragmentChanges
			).build());
	}

	@After
	public void tearDown() throws Exception {
		ConfigurationTestUtil.deleteConfiguration(
			FragmentServiceConfiguration.class.getName());
		_configurationProvider.deleteCompanyConfiguration(
			FragmentServiceConfiguration.class, _company.getCompanyId());
	}

	@Test
	@TestInfo("LPD-82989")
	public void testGetFragmentServiceConfigurationWithCompanyScope()
		throws Exception {

		MockHttpServletRequest mockHttpServletRequest =
			_getMockHttpServletRequest(
				ConfigurationAdminPortletKeys.INSTANCE_SETTINGS);

		_configurationFormRenderer.render(
			mockHttpServletRequest, new MockHttpServletResponse());

		Object displayContext = mockHttpServletRequest.getAttribute(
			_DISPLAY_CONTEXT_CLASS_NAME);

		Assert.assertEquals(
			_companyPropagateChanges,
			ReflectionTestUtil.invoke(
				displayContext, "isPropagateChangesEnabled", new Class<?>[0]));

		Assert.assertEquals(
			_companyPropagateContributedFragmentChanges,
			ReflectionTestUtil.invoke(
				displayContext, "isPropagateContributedFragmentChangesEnabled",
				new Class<?>[0]));
	}

	@Test
	@TestInfo("LPD-82989")
	public void testGetFragmentServiceConfigurationWithSystemScope()
		throws Exception {

		MockHttpServletRequest mockHttpServletRequest =
			_getMockHttpServletRequest(
				ConfigurationAdminPortletKeys.SYSTEM_SETTINGS);

		_configurationFormRenderer.render(
			mockHttpServletRequest, new MockHttpServletResponse());

		Object displayContext = mockHttpServletRequest.getAttribute(
			_DISPLAY_CONTEXT_CLASS_NAME);

		Assert.assertEquals(
			_systemPropagateChanges,
			ReflectionTestUtil.invoke(
				displayContext, "isPropagateChangesEnabled", new Class<?>[0]));

		Assert.assertEquals(
			_systemPropagateContributedFragmentChanges,
			ReflectionTestUtil.invoke(
				displayContext, "isPropagateContributedFragmentChangesEnabled",
				new Class<?>[0]));
	}

	private MockHttpServletRequest _getMockHttpServletRequest(String portletId)
		throws Exception {

		MockHttpServletRequest mockHttpServletRequest =
			ContentLayoutTestUtil.getMockHttpServletRequest(
				_company, _group, _layout);

		mockHttpServletRequest.setAttribute(
			JavaConstants.JAKARTA_PORTLET_REQUEST,
			new MockLiferayPortletRenderRequest(mockHttpServletRequest));

		mockHttpServletRequest.setAttribute(
			JavaConstants.JAKARTA_PORTLET_CONFIG,
			ProxyUtil.newProxyInstance(
				LiferayPortletConfig.class.getClassLoader(),
				new Class<?>[] {LiferayPortletConfig.class},
				(proxy, method, args) -> {
					if (Objects.equals(method.getName(), "getPortletId")) {
						return portletId;
					}

					return null;
				}));
		mockHttpServletRequest.setAttribute(
			JavaConstants.JAKARTA_PORTLET_RESPONSE,
			new MockLiferayPortletRenderResponse());
		mockHttpServletRequest.setRequestURI(StringPool.SLASH);

		return mockHttpServletRequest;
	}

	private static final String _DISPLAY_CONTEXT_CLASS_NAME =
		"com.liferay.fragment.web.internal.display.context." +
			"FragmentServiceConfigurationDisplayContext";

	private Company _company;

	@Inject
	private CompanyLocalService _companyLocalService;

	private boolean _companyPropagateChanges;
	private boolean _companyPropagateContributedFragmentChanges;

	@Inject(
		filter = "component.name=com.liferay.fragment.web.internal.configuration.admin.display.FragmentServiceConfigurationFormRenderer"
	)
	private ConfigurationFormRenderer _configurationFormRenderer;

	@Inject
	private ConfigurationProvider _configurationProvider;

	@DeleteAfterTestRun
	private Group _group;

	private Layout _layout;
	private boolean _systemPropagateChanges;
	private boolean _systemPropagateContributedFragmentChanges;

}