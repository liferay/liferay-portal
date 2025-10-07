/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.util.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.layout.test.util.LayoutTestUtil;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.CompanyConstants;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.GroupConstants;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.LayoutSet;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.service.VirtualHostLocalService;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.CompanyTestUtil;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.PropsValues;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.TreeMapBuilder;
import com.liferay.portal.kernel.util.UnicodeProperties;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.util.PortalInstances;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.mock.web.MockHttpServletRequest;

/**
 * @author Lourdes Fernández Besada
 */
@RunWith(Arquillian.class)
public class PortalInstancesTest {

	@ClassRule
	@Rule
	public static final LiferayIntegrationTestRule liferayIntegrationTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() throws Exception {
		_virtualHostsDefaultSiteName = ReflectionTestUtil.getAndSetFieldValue(
			PropsValues.class, "VIRTUAL_HOSTS_DEFAULT_SITE_NAME", "Guest");

		_company = CompanyTestUtil.addCompany();

		Group defaultGroup = _groupLocalService.getGroup(
			_company.getCompanyId(), GroupConstants.GUEST);

		_defaultGroupPublicLayout = _layoutLocalService.fetchDefaultLayout(
			defaultGroup.getGroupId(), false);

		Group nondefaultGroup = GroupTestUtil.addGroup(
			_company.getCompanyId(), TestPropsValues.getUserId(),
			GroupConstants.DEFAULT_PARENT_GROUP_ID);

		LayoutTestUtil.addTypePortletLayout(nondefaultGroup, false);

		_nondefaultGroupPublicLayout = _layoutLocalService.fetchDefaultLayout(
			nondefaultGroup.getGroupId(), false);

		_nondefaultGroupPublicLayoutHostname =
			RandomTestUtil.randomString(6) + "." +
				RandomTestUtil.randomString(3);

		_updateLayoutSetVirtualHostname(
			_nondefaultGroupPublicLayout, _nondefaultGroupPublicLayoutHostname);
	}

	@After
	public void tearDown() throws PortalException {
		ReflectionTestUtil.setFieldValue(
			PropsValues.class, "VIRTUAL_HOSTS_DEFAULT_SITE_NAME",
			_virtualHostsDefaultSiteName);
	}

	@Test
	public void testGetCompanyId() {
		_updateLayoutSetVirtualHostname(
			_defaultGroupPublicLayout, StringPool.BLANK);

		_testGetCompanyId(
			_company.getVirtualHostname(),
			_defaultGroupPublicLayout.getLayoutSet());
		_testGetCompanyId(
			_nondefaultGroupPublicLayoutHostname,
			_nondefaultGroupPublicLayout.getLayoutSet());

		String defaultGroupPublicLayoutHostname =
			RandomTestUtil.randomString(6) + "." +
				RandomTestUtil.randomString(3);

		_updateLayoutSetVirtualHostname(
			_defaultGroupPublicLayout, defaultGroupPublicLayoutHostname);

		_testGetCompanyId(
			_company.getVirtualHostname(),
			_defaultGroupPublicLayout.getLayoutSet());
		_testGetCompanyId(
			defaultGroupPublicLayoutHostname,
			_defaultGroupPublicLayout.getLayoutSet());
		_testGetCompanyId(
			_nondefaultGroupPublicLayoutHostname,
			_nondefaultGroupPublicLayout.getLayoutSet());
	}

	@Test
	public void testGetCompanyIdFromHttpServletRequestAttribute() {
		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest();

		mockHttpServletRequest.setAttribute(
			WebKeys.COMPANY_ID, _company.getCompanyId());

		try (SafeCloseable safeCloseable =
				CompanyThreadLocal.setCompanyIdWithSafeCloseable(
					CompanyConstants.SYSTEM)) {

			_assertGetCompanyId(true, mockHttpServletRequest);
		}

		try (SafeCloseable safeCloseable =
				CompanyThreadLocal.setCompanyIdWithSafeCloseable(
					RandomTestUtil.randomLong())) {

			_assertGetCompanyId(false, mockHttpServletRequest);
		}
	}

	@Test
	public void testGetVirtualHostLanguageId() throws Exception {
		Group group = GroupTestUtil.addGroupToCompany(_company.getCompanyId());

		UnicodeProperties typeSettingsUnicodeProperties =
			group.getTypeSettingsProperties();

		String languageId = LanguageUtil.getLanguageId(LocaleUtil.SPAIN);

		typeSettingsUnicodeProperties.setProperty(
			PropsKeys.LOCALES, languageId);
		typeSettingsUnicodeProperties.setProperty("languageId", languageId);

		group.setTypeSettingsProperties(typeSettingsUnicodeProperties);

		group = _groupLocalService.updateGroup(group);

		String hostname =
			RandomTestUtil.randomString(6) + "." +
				RandomTestUtil.randomString(3);

		// Blank virtual host language

		_updateLayoutSetVirtualHostname(
			StringPool.BLANK, group.getPublicLayoutSet(), hostname);

		_testGetVirtualHostLanguageId(null, hostname);

		// Spanish virtual host language

		_updateLayoutSetVirtualHostname(
			languageId, group.getPublicLayoutSet(), hostname);

		_testGetVirtualHostLanguageId(languageId, hostname);
	}

	private void _assertGetCompanyId(
		boolean equals, MockHttpServletRequest mockHttpServletRequest) {

		// PortalInstances#getCompanyId must be invoked before
		// CompanyThreadLocal#getCompanyId

		Assert.assertEquals(
			_company.getCompanyId(),
			PortalInstances.getCompanyId(mockHttpServletRequest));

		if (equals) {
			Assert.assertEquals(
				_company.getCompanyId(),
				(long)CompanyThreadLocal.getCompanyId());
		}
		else {
			Assert.assertNotEquals(
				_company.getCompanyId(),
				(long)CompanyThreadLocal.getCompanyId());
		}
	}

	private void _testGetCompanyId(String hostname, LayoutSet layoutSet) {
		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest();

		mockHttpServletRequest.addHeader("Host", hostname);
		mockHttpServletRequest.setServerName(hostname);

		_assertGetCompanyId(true, mockHttpServletRequest);

		Assert.assertEquals(
			_company.getCompanyId(),
			mockHttpServletRequest.getAttribute(WebKeys.COMPANY_ID));
		Assert.assertEquals(
			layoutSet,
			mockHttpServletRequest.getAttribute(
				WebKeys.VIRTUAL_HOST_LAYOUT_SET));
	}

	private void _testGetVirtualHostLanguageId(
		String expectedLanguageId, String hostname) {

		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest();

		mockHttpServletRequest.addHeader("Host", hostname);
		mockHttpServletRequest.setServerName(hostname);

		Assert.assertEquals(
			_company.getCompanyId(),
			PortalInstances.getCompanyId(mockHttpServletRequest));
		Assert.assertEquals(
			expectedLanguageId,
			mockHttpServletRequest.getAttribute(
				WebKeys.VIRTUAL_HOST_LANGUAGE_ID));
	}

	private void _updateLayoutSetVirtualHostname(
		Layout layout, String layoutHostname) {

		_updateLayoutSetVirtualHostname(
			StringPool.BLANK, layout.getLayoutSet(), layoutHostname);

		layout.setLayoutSet(null);
	}

	private void _updateLayoutSetVirtualHostname(
		String languageId, LayoutSet layoutSet, String layoutHostname) {

		_virtualHostLocalService.updateVirtualHosts(
			_company.getCompanyId(), layoutSet.getLayoutSetId(),
			TreeMapBuilder.put(
				StringUtil.toLowerCase(layoutHostname), languageId
			).build());
	}

	@DeleteAfterTestRun
	private Company _company;

	@Inject
	private CompanyLocalService _companyLocalService;

	private Layout _defaultGroupPublicLayout;

	@Inject
	private GroupLocalService _groupLocalService;

	@Inject
	private LayoutLocalService _layoutLocalService;

	private Layout _nondefaultGroupPublicLayout;
	private String _nondefaultGroupPublicLayoutHostname;

	@Inject
	private VirtualHostLocalService _virtualHostLocalService;

	private String _virtualHostsDefaultSiteName;

}