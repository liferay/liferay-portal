/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.sitemap.web.internal.portlet.action.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.test.util.ObjectDefinitionTestUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.petra.string.StringUtil;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.GroupConstants;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.LayoutTypePortlet;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.security.auth.PrincipalThreadLocal;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactoryUtil;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.servlet.SessionMessages;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletActionRequest;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletActionResponse;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.JavaConstants;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.site.configuration.manager.SitemapConfigurationManager;

import jakarta.portlet.PortletException;

import java.util.Dictionary;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;

/**
 * @author Lourdes Fernández Besada
 */
@RunWith(Arquillian.class)
public class SaveCompanyConfigurationMVCActionCommandTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@BeforeClass
	public static void setUpClass() throws Exception {
		_adminUser = TestPropsValues.getUser();

		_company = _companyLocalService.getCompany(
			TestPropsValues.getCompanyId());

		Group group = _groupLocalService.fetchGroup(
			_company.getCompanyId(), GroupConstants.CONTROL_PANEL);

		_layout = _layoutLocalService.fetchDefaultLayout(
			group.getGroupId(), true);

		_originalCompanySitemapGroupIds =
			_sitemapConfigurationManager.getCompanySitemapGroupIds(
				_company.getCompanyId());
		_originalCompanySitemapObjectDefinitionIds =
			_sitemapConfigurationManager.getCompanySitemapObjectDefinitionIds(
				_company.getCompanyId());
		_originalIncludeCategories =
			_sitemapConfigurationManager.includeCategoriesCompanyEnabled(
				_company.getCompanyId());
		_originalIncludePages =
			_sitemapConfigurationManager.includePagesCompanyEnabled(
				_company.getCompanyId());
		_originalIncludeWebContent =
			_sitemapConfigurationManager.includeWebContentCompanyEnabled(
				_company.getCompanyId());
		_originalXMLSitemapIndexEnabled =
			_sitemapConfigurationManager.xmlSitemapIndexCompanyEnabled(
				_company.getCompanyId());

		_originalName = PrincipalThreadLocal.getName();

		PrincipalThreadLocal.setName(_adminUser.getUserId());
	}

	@AfterClass
	public static void tearDownClass() throws Exception {
		_sitemapConfigurationManager.saveSitemapCompanyConfiguration(
			_company.getCompanyId(),
			ArrayUtil.toArray(_originalCompanySitemapGroupIds),
			ArrayUtil.toArray(_originalCompanySitemapObjectDefinitionIds),
			_originalIncludeCategories, _originalIncludePages,
			_originalIncludeWebContent, _originalXMLSitemapIndexEnabled);

		PrincipalThreadLocal.setName(_originalName);
	}

	@Test
	public void testSaveCompanyConfiguration() throws Exception {
		_assertSaveCompanyConfiguration(
			new long[0], new long[0], new long[0], new long[0], true, true,
			true, true, _adminUser);
	}

	@Test
	public void testSaveCompanyConfigurationCompanySitemapGroupIds()
		throws Exception {

		Group group = GroupTestUtil.addGroup(
			_company.getCompanyId(), _adminUser.getUserId(),
			GroupConstants.DEFAULT_PARENT_GROUP_ID);

		_assertSaveCompanyConfiguration(
			new long[] {group.getGroupId()}, new long[0],
			new long[] {group.getGroupId()}, new long[0], true, true, false,
			true, _adminUser);
	}

	@Test
	public void testSaveCompanyConfigurationCompanySitemapGroupIdsGuestGroupSelected()
		throws Exception {

		Group group = GroupTestUtil.addGroup(
			_company.getCompanyId(), _adminUser.getUserId(),
			GroupConstants.DEFAULT_PARENT_GROUP_ID);

		Group guestGroup = _groupLocalService.fetchGroup(
			_company.getCompanyId(), GroupConstants.GUEST);

		_assertSaveCompanyConfiguration(
			new long[] {group.getGroupId()}, new long[0],
			new long[] {guestGroup.getGroupId(), group.getGroupId()},
			new long[0], true, true, false, true, _adminUser);
	}

	@Test
	public void testSaveCompanyConfigurationCompanySitemapGroupIdsNonexistentGroup()
		throws Exception {

		Group group = GroupTestUtil.addGroup(
			_company.getCompanyId(), _adminUser.getUserId(),
			GroupConstants.DEFAULT_PARENT_GROUP_ID);

		_assertSaveCompanyConfiguration(
			new long[] {group.getGroupId()}, new long[0],
			new long[] {RandomTestUtil.randomLong(), group.getGroupId()},
			new long[0], true, true, false, true, _adminUser);
	}

	@Test
	public void testSaveCompanyConfigurationCompanySitemapObjectDefinitionIds()
		throws Exception {

		ObjectDefinition objectDefinition =
			ObjectDefinitionTestUtil.publishObjectDefinition();

		_assertSaveCompanyConfiguration(
			new long[0], new long[] {objectDefinition.getObjectDefinitionId()},
			new long[0], new long[] {objectDefinition.getObjectDefinitionId()},
			true, true, false, true, _adminUser);
	}

	@Test
	public void testSaveCompanyConfigurationCompanySitemapObjectDefinitionIdsInactiveObjectDefinitionSelected()
		throws Exception {

		ObjectDefinition objectDefinition =
			ObjectDefinitionTestUtil.publishObjectDefinition();

		objectDefinition.setActive(false);

		objectDefinition = _objectDefinitionLocalService.updateObjectDefinition(
			objectDefinition);

		_assertSaveCompanyConfiguration(
			new long[0], new long[0], new long[0],
			new long[] {objectDefinition.getObjectDefinitionId()}, true, true,
			false, true, _adminUser);
	}

	@Test
	public void testSaveCompanyConfigurationCompanySitemapObjectDefinitionIdsNonexistentObjectDefinition()
		throws Exception {

		_assertSaveCompanyConfiguration(
			new long[0], new long[0], new long[0],
			new long[] {RandomTestUtil.randomLong()}, true, true, false, true,
			_adminUser);
	}

	@Test
	public void testSaveCompanyConfigurationCompanySitemapObjectDefinitionIdsSystemObjectDefinitionSelected()
		throws Exception {

		ObjectDefinition objectDefinition =
			ObjectDefinitionTestUtil.publishSystemObjectDefinition();

		_assertSaveCompanyConfiguration(
			new long[0], new long[0], new long[0],
			new long[] {objectDefinition.getObjectDefinitionId()}, true, true,
			false, true, _adminUser);
	}

	@Test
	public void testSaveCompanyConfigurationDisablingIncludeCategories()
		throws Exception {

		_assertSaveCompanyConfiguration(
			new long[0], new long[0], new long[0], new long[0], false, true,
			true, true, _adminUser);
	}

	@Test
	public void testSaveCompanyConfigurationDisablingIncludePages()
		throws Exception {

		_assertSaveCompanyConfiguration(
			new long[0], new long[0], new long[0], new long[0], true, false,
			true, true, _adminUser);
	}

	@Test
	public void testSaveCompanyConfigurationDisablingIncludeWebContent()
		throws Exception {

		_assertSaveCompanyConfiguration(
			new long[0], new long[0], new long[0], new long[0], true, true,
			false, true, _adminUser);
	}

	@Test
	public void testSaveCompanyConfigurationDisablingXMLSitemapIndexEnabled()
		throws Exception {

		_assertSaveCompanyConfiguration(
			new long[0], new long[0], new long[0], new long[0], true, true,
			true, false, _adminUser);
	}

	@Test
	public void testSaveCompanyConfigurationNotCompanyAdminUser()
		throws Exception {

		Group group = GroupTestUtil.addGroup(
			_company.getCompanyId(), _adminUser.getUserId(),
			GroupConstants.DEFAULT_PARENT_GROUP_ID);

		boolean portletExceptionThrown = false;

		try {
			_assertSaveCompanyConfiguration(
				new long[0], new long[0], new long[0], new long[0], true, true,
				true, true, UserTestUtil.addGroupAdminUser(group));
		}
		catch (PortletException portletException) {
			portletExceptionThrown = true;

			Throwable throwable = portletException.getCause();

			Assert.assertNotNull(throwable);
			Assert.assertTrue(
				throwable instanceof PrincipalException.MustBeCompanyAdmin);
		}

		Assert.assertTrue(portletExceptionThrown);
	}

	private void _assertCompanyConfiguration(
			long[] companySitemapGroupIds,
			long[] companySitemapObjectDefinitionIds, boolean includeCategories,
			boolean includePages, boolean includeWebContent,
			boolean xmlSitemapIndexEnabled)
		throws Exception {

		Configuration[] configurations = _configurationAdmin.listConfigurations(
			StringBundler.concat(
				"(&(companyId=", _company.getCompanyId(),
				")(service.factoryPid=", _PID_SITEMAP_COMPANY_CONFIGURATION,
				".scoped))"));

		Assert.assertTrue(ArrayUtil.isNotEmpty(configurations));

		Configuration configuration = configurations[0];

		Dictionary<String, Object> properties = configuration.getProperties();

		Assert.assertArrayEquals(
			companySitemapGroupIds,
			GetterUtil.getLongValues(properties.get("companySitemapGroupIds")));
		Assert.assertArrayEquals(
			companySitemapObjectDefinitionIds,
			GetterUtil.getLongValues(
				properties.get("companySitemapObjectDefinitionIds")));
		Assert.assertEquals(
			includeCategories,
			GetterUtil.getBoolean(properties.get("includeCategories")));
		Assert.assertEquals(
			includePages,
			GetterUtil.getBoolean(properties.get("includePages")));
		Assert.assertEquals(
			includeWebContent,
			GetterUtil.getBoolean(properties.get("includeWebContent")));
		Assert.assertEquals(
			xmlSitemapIndexEnabled,
			GetterUtil.getBoolean(properties.get("xmlSitemapIndexEnabled")));
	}

	private void _assertSaveCompanyConfiguration(
			long[] expectedGroupIds, long[] expectedObjectDefinitionIds,
			long[] groupIds, long[] objectDefinitionIds,
			boolean includeCategories, boolean includePages,
			boolean includeWebContent, boolean xmlSitemapIndexEnabled,
			User user)
		throws Exception {

		MockLiferayPortletActionRequest mockLiferayPortletActionRequest =
			_getMockLiferayPortletActionRequest(
				groupIds, objectDefinitionIds, includeCategories, includePages,
				includeWebContent, xmlSitemapIndexEnabled, user);

		Assert.assertFalse(
			SessionMessages.contains(
				mockLiferayPortletActionRequest, "requestProcessed"));

		_mvcActionCommand.processAction(
			mockLiferayPortletActionRequest,
			new MockLiferayPortletActionResponse());

		Assert.assertTrue(
			SessionMessages.contains(
				mockLiferayPortletActionRequest, "requestProcessed"));

		_assertCompanyConfiguration(
			expectedGroupIds, expectedObjectDefinitionIds, includeCategories,
			includePages, includeWebContent, xmlSitemapIndexEnabled);
	}

	private MockLiferayPortletActionRequest _getMockLiferayPortletActionRequest(
			long[] groupIds, long[] objectDefinitionIds,
			boolean includeCategories, boolean includePages,
			boolean includeWebContent, boolean xmlSitemapIndexEnabled,
			User user)
		throws Exception {

		MockLiferayPortletActionRequest mockLiferayPortletActionRequest =
			new MockLiferayPortletActionRequest();

		mockLiferayPortletActionRequest.addParameter(
			"groupsSearchContainerPrimaryKeys",
			StringUtil.merge(groupIds, StringPool.COMMA));
		mockLiferayPortletActionRequest.addParameter(
			"includeCategories", String.valueOf(includeCategories));
		mockLiferayPortletActionRequest.addParameter(
			"includePages", String.valueOf(includePages));
		mockLiferayPortletActionRequest.addParameter(
			"includeWebContent", String.valueOf(includeWebContent));
		mockLiferayPortletActionRequest.addParameter(
			"objectDefinitionsSearchContainerPrimaryKeys",
			StringUtil.merge(objectDefinitionIds, StringPool.COMMA));
		mockLiferayPortletActionRequest.addParameter(
			"xmlSitemapIndexEnabled", String.valueOf(xmlSitemapIndexEnabled));
		mockLiferayPortletActionRequest.setAttribute(
			JavaConstants.JAKARTA_PORTLET_RESPONSE,
			new MockLiferayPortletActionResponse());
		mockLiferayPortletActionRequest.setAttribute(
			WebKeys.THEME_DISPLAY, _getThemeDisplay(user));

		return mockLiferayPortletActionRequest;
	}

	private ThemeDisplay _getThemeDisplay(User user) throws Exception {
		ThemeDisplay themeDisplay = new ThemeDisplay();

		themeDisplay.setCompany(_company);
		themeDisplay.setLayout(_layout);
		themeDisplay.setLayoutSet(_layout.getLayoutSet());
		themeDisplay.setLayoutTypePortlet(
			(LayoutTypePortlet)_layout.getLayoutType());
		themeDisplay.setLocale(LocaleUtil.getDefault());
		themeDisplay.setPermissionChecker(
			PermissionCheckerFactoryUtil.create(user));
		themeDisplay.setUser(user);

		return themeDisplay;
	}

	private static final String _PID_SITEMAP_COMPANY_CONFIGURATION =
		"com.liferay.site.internal.configuration.SitemapCompanyConfiguration";

	private static User _adminUser;
	private static Company _company;

	@Inject
	private static CompanyLocalService _companyLocalService;

	@Inject
	private static ConfigurationAdmin _configurationAdmin;

	@Inject
	private static GroupLocalService _groupLocalService;

	private static Layout _layout;

	@Inject
	private static LayoutLocalService _layoutLocalService;

	@Inject
	private static ObjectDefinitionLocalService _objectDefinitionLocalService;

	private static Long[] _originalCompanySitemapGroupIds;
	private static Long[] _originalCompanySitemapObjectDefinitionIds;
	private static boolean _originalIncludeCategories;
	private static boolean _originalIncludePages;
	private static boolean _originalIncludeWebContent;
	private static String _originalName;
	private static boolean _originalXMLSitemapIndexEnabled;

	@Inject
	private static SitemapConfigurationManager _sitemapConfigurationManager;

	@Inject(
		filter = "mvc.command.name=/site_sitemap/save_company_configuration"
	)
	private MVCActionCommand _mvcActionCommand;

}