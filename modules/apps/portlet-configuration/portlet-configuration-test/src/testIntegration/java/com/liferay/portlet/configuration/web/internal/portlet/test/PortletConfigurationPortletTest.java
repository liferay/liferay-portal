/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portlet.configuration.web.internal.portlet.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.layout.test.util.LayoutTestUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.NoSuchPortletItemException;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.LayoutTypePortlet;
import com.liferay.portal.kernel.model.PortletItem;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCPortlet;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactoryUtil;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.PortletItemLocalService;
import com.liferay.portal.kernel.service.PortletLocalService;
import com.liferay.portal.kernel.service.PortletPreferencesLocalService;
import com.liferay.portal.kernel.service.ResourcePermissionLocalService;
import com.liferay.portal.kernel.servlet.PortletServlet;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.portlet.MockActionResponse;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletActionRequest;
import com.liferay.portal.kernel.test.portlet.MockLiferayResourceRequest;
import com.liferay.portal.kernel.test.portlet.MockLiferayResourceResponse;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.RoleTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.PortletKeys;
import com.liferay.portal.kernel.util.SetUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LanguageIds;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import jakarta.portlet.ActionParameters;
import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;
import jakarta.portlet.MutableActionParameters;
import jakarta.portlet.Portlet;
import jakarta.portlet.PortletPreferences;
import jakarta.portlet.PortletRequest;
import jakarta.portlet.PortletResponse;
import jakarta.portlet.ResourceRequest;
import jakarta.portlet.ResourceResponse;

import jakarta.servlet.http.HttpServletResponse;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceRegistration;

import org.springframework.mock.web.MockHttpServletRequest;

/**
 * @author Mikel Lorza
 */
@LanguageIds(
	availableLanguageIds = {"en_US", "ja_JP"}, defaultLanguageId = "en_US"
)
@RunWith(Arquillian.class)
public class PortletConfigurationPortletTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@BeforeClass
	public static void setUpClass() throws PortalException {
		Bundle bundle = FrameworkUtil.getBundle(
			PortletConfigurationPortletTest.class);

		BundleContext bundleContext = bundle.getBundleContext();

		String portletId = "TEST_PORTLET_" + RandomTestUtil.randomString();

		_serviceRegistration = bundleContext.registerService(
			Portlet.class, new MVCPortlet(),
			HashMapDictionaryBuilder.put(
				"jakarta.portlet.name", portletId
			).build());

		_serviceBuilderPortlet = _portletLocalService.getPortletById(
			TestPropsValues.getCompanyId(), portletId);
	}

	@AfterClass
	public static void tearDownClass() throws Exception {
		_serviceRegistration.unregister();
	}

	@Before
	public void setUp() throws Exception {
		_company = _companyLocalService.getCompany(
			TestPropsValues.getCompanyId());

		_group = GroupTestUtil.addGroup();

		_locale = _portal.getSiteDefaultLocale(_group);
	}

	@After
	public void tearDown() throws Exception {
		for (PortletItem portletItem :
				_portletItemLocalService.getPortletItems(
					_group.getGroupId(), _CLASS_NAME_PORTLET_PREFERENCES)) {

			_portletPreferencesLocalService.deletePortletPreferences(
				portletItem.getPortletItemId(),
				PortletKeys.PREFS_OWNER_TYPE_ARCHIVED, 0);

			_portletItemLocalService.deletePortletItem(portletItem);
		}
	}

	@Test
	@TestInfo("LPD-106855")
	public void testCheckPermissions() throws Exception {
		_testCheckPermissionsIsNotOverridden();
		_testCheckPermissionsWithConfigurationPermission();
		_testCheckPermissionsWithoutConfigurationPermission();
		_testCheckPermissionsWithoutPermissionsPermission();
	}

	@Test
	public void testDeleteArchivedSetups() throws Exception {
		PortletItem portletItem1 = _addPortletItem(
			HashMapBuilder.put(
				"key1", new String[] {RandomTestUtil.randomString()}
			).build());
		PortletItem portletItem2 = _addPortletItem(
			HashMapBuilder.put(
				"key2", new String[] {RandomTestUtil.randomString()}
			).build());
		PortletItem portletItem3 = _addPortletItem(
			HashMapBuilder.put(
				"key3", new String[] {RandomTestUtil.randomString()}
			).build());

		Layout layout = _addLayout(
			HashMapBuilder.put(
				"key1", new String[] {RandomTestUtil.randomString()}
			).build());

		_invoke(layout, "deleteArchivedSetups", "name", portletItem1.getName());

		_assertArchivedSetupDeleted(portletItem1);
		_assertPortletItemNames(portletItem2.getName(), portletItem3.getName());

		_invoke(
			layout, "deleteArchivedSetups", "rowIds", portletItem2.getName(),
			portletItem3.getName());

		_assertArchivedSetupDeleted(portletItem2);
		_assertArchivedSetupDeleted(portletItem3);
		_assertPortletItemNames();
	}

	@Test
	public void testEditScopeForLayoutPortlet() throws Exception {
		Layout layout = LayoutTestUtil.addTypePortletLayout(_group);

		LayoutTestUtil.addPortletToLayout(
			layout, _serviceBuilderPortlet.getPortletId(),
			HashMapBuilder.put(
				"portletSetupTitle_en_US",
				new String[] {RandomTestUtil.randomString()}
			).put(
				"portletSetupUseCustomTitle",
				new String[] {Boolean.TRUE.toString()}
			).build());

		_assertUpdateScope(
			layout, PortletKeys.PREFS_OWNER_ID_DEFAULT, layout.getPlid());
	}

	@Test
	public void testEditScopeForSharedPortlet() throws Exception {
		Layout layout = LayoutTestUtil.addTypePortletLayout(_group);

		String defaultPreferences = _getDefaultPreferences(
			HashMapBuilder.put(
				"portletSetupTitle_en_US", RandomTestUtil.randomString()
			).put(
				"portletSetupUseCustomTitle", Boolean.TRUE.toString()
			).build());

		_portletPreferencesLocalService.addPortletPreferences(
			_company.getCompanyId(), _group.getGroupId(),
			PortletKeys.PREFS_OWNER_TYPE_LAYOUT, PortletKeys.PREFS_PLID_SHARED,
			_serviceBuilderPortlet.getPortletId(), _serviceBuilderPortlet,
			defaultPreferences);

		_portletPreferencesLocalService.addPortletPreferences(
			_company.getCompanyId(), PortletKeys.PREFS_OWNER_ID_DEFAULT,
			PortletKeys.PREFS_OWNER_TYPE_LAYOUT, layout.getPlid(),
			_serviceBuilderPortlet.getPortletId(), _serviceBuilderPortlet,
			defaultPreferences);

		_assertUpdateScope(
			layout, _group.getGroupId(), PortletKeys.PREFS_PLID_SHARED);
	}

	@Test
	public void testRestoreArchivedSetup() throws Exception {
		_testRestoreArchivedSetupExistingName();
		_testRestoreArchivedSetupMissingName();
	}

	@Test
	@TestInfo("LPD-106855")
	public void testServeResource() throws Exception {
		MockLiferayResourceResponse mockLiferayResourceResponse =
			new TestMockLiferayResourceResponse();

		ReflectionTestUtil.invoke(
			_portlet, "serveResource",
			new Class<?>[] {ResourceRequest.class, ResourceResponse.class},
			_getMockResourceRequest(
				"/edit_scope.jsp", _getThemeDisplay(_addUser())),
			mockLiferayResourceResponse);

		Assert.assertEquals(
			String.valueOf(HttpServletResponse.SC_FORBIDDEN),
			mockLiferayResourceResponse.getProperty(
				ResourceResponse.HTTP_STATUS_CODE));
	}

	@Test
	public void testUpdateArchivedSetup() throws Exception {
		_testUpdateArchivedSetupExistingName();
		_testUpdateArchivedSetupNewName();
	}

	@Test
	public void testUpdateRolePermissions() throws Exception {
		List<String> plids = _addLayouts(1);

		List<Long> roleIds = _addRoles();

		ReflectionTestUtil.invoke(
			_portlet, "updateRolePermissions",
			new Class<?>[] {ActionRequest.class, ActionResponse.class},
			_getMockActionRequest(plids, roleIds), new MockActionResponse());

		_assertResourcePermissions(plids, roleIds);
	}

	@Test
	public void testUpdateRolePermissionsInBulk() throws Exception {
		List<String> plids = _addLayouts(100);
		List<Long> roleIds = _addRoles();

		ReflectionTestUtil.invoke(
			_portlet, "updateRolePermissions",
			new Class<?>[] {ActionRequest.class, ActionResponse.class},
			_getMockActionRequest(plids, roleIds), new MockActionResponse());

		_assertResourcePermissions(plids, roleIds);
	}

	private Layout _addLayout(Map<String, String[]> preferenceMap)
		throws Exception {

		Layout layout = LayoutTestUtil.addTypePortletLayout(_group);

		LayoutTestUtil.addPortletToLayout(
			layout, _serviceBuilderPortlet.getPortletId(), preferenceMap);

		return layout;
	}

	private List<String> _addLayouts(int numberOfItems) throws Exception {
		List<String> plids = new ArrayList<>();

		for (int i = 0; i < numberOfItems; i++) {
			Layout layout = LayoutTestUtil.addTypeContentLayout(_group);

			plids.add(String.valueOf(layout.getPlid()));
		}

		return plids;
	}

	private PortletItem _addPortletItem(Map<String, String[]> preferenceMap)
		throws Exception {

		PortletItem portletItem = _portletItemLocalService.addPortletItem(
			TestPropsValues.getUserId(), _group.getGroupId(),
			RandomTestUtil.randomString(),
			_serviceBuilderPortlet.getRootPortletId(),
			_CLASS_NAME_PORTLET_PREFERENCES);

		PortletPreferences portletPreferences = _getArchivedPortletPreferences(
			portletItem);

		for (Map.Entry<String, String[]> entry : preferenceMap.entrySet()) {
			portletPreferences.setValues(entry.getKey(), entry.getValue());
		}

		portletPreferences.store();

		return portletItem;
	}

	private List<Long> _addRoles() throws Exception {
		List<Long> roleIds = new ArrayList<>();

		for (int i = 0; i < 10; i++) {
			Role role = RoleTestUtil.addRole(RoleConstants.TYPE_REGULAR);

			roleIds.add(role.getRoleId());
		}

		return roleIds;
	}

	private User _addUser() throws Exception {
		User user = UserTestUtil.addUser(_group.getGroupId());

		_users.add(user);

		return user;
	}

	private void _assertArchivedSetupDeleted(PortletItem portletItem) {
		Assert.assertNull(
			_portletItemLocalService.fetchPortletItem(
				portletItem.getPortletItemId()));
		Assert.assertNull(
			_portletPreferencesLocalService.fetchPortletPreferences(
				portletItem.getPortletItemId(),
				PortletKeys.PREFS_OWNER_TYPE_ARCHIVED, 0,
				_serviceBuilderPortlet.getRootPortletId()));
	}

	private void _assertPortletItemNames(String... names) {
		Assert.assertEquals(SetUtil.fromArray(names), _getPortletItemNames());
	}

	private void _assertPreferences(
		Map<String, String[]> expectedPreferenceMap,
		PortletPreferences portletPreferences) {

		Map<String, String[]> preferenceMap = portletPreferences.getMap();

		Assert.assertEquals(
			expectedPreferenceMap.keySet(), preferenceMap.keySet());

		for (Map.Entry<String, String[]> entry :
				expectedPreferenceMap.entrySet()) {

			Assert.assertArrayEquals(
				entry.getKey(), entry.getValue(),
				preferenceMap.get(entry.getKey()));
		}
	}

	private void _assertResourcePermissions(
		List<String> plids, List<Long> roleIds) {

		for (String plid : plids) {
			for (long roleId : roleIds) {
				Assert.assertNotNull(
					_resourcePermissionLocalService.fetchResourcePermission(
						_company.getCompanyId(), Layout.class.getName(),
						ResourceConstants.SCOPE_INDIVIDUAL, plid, roleId));
			}
		}
	}

	private void _assertUpdateScope(
			Layout layout, long preferencesOwnerId, long preferencesPlid)
		throws Exception {

		ReflectionTestUtil.invoke(
			_portlet, "_updateScope", new Class<?>[] {ActionRequest.class},
			_getMockActionRequest(layout));

		PortletPreferences portletPreferences =
			_portletPreferencesLocalService.getPreferences(
				_company.getCompanyId(), preferencesOwnerId,
				PortletKeys.PREFS_OWNER_TYPE_LAYOUT, preferencesPlid,
				_serviceBuilderPortlet.getPortletId());

		Assert.assertEquals(
			"company",
			portletPreferences.getValue("lfrScopeType", StringPool.BLANK));
	}

	private PortletPreferences _getArchivedPortletPreferences(
		PortletItem portletItem) {

		return _portletPreferencesLocalService.getPreferences(
			portletItem.getCompanyId(), portletItem.getPortletItemId(),
			PortletKeys.PREFS_OWNER_TYPE_ARCHIVED, 0,
			_serviceBuilderPortlet.getRootPortletId());
	}

	private PortletItem _getArchivedSetup(String name) throws Exception {
		return _portletItemLocalService.getPortletItem(
			_group.getGroupId(), name,
			_serviceBuilderPortlet.getRootPortletId(),
			_CLASS_NAME_PORTLET_PREFERENCES);
	}

	private String _getDefaultPreferences(Map<String, String> preferences) {
		StringBundler sb = new StringBundler();

		sb.append("<portlet-preferences>");

		for (Map.Entry<String, String> entry : preferences.entrySet()) {
			sb.append("<preference><name>");
			sb.append(entry.getKey());
			sb.append("</name><value>");
			sb.append(entry.getValue());
			sb.append("</value></preference>");
		}

		sb.append("</portlet-preferences>");

		return sb.toString();
	}

	private MockActionRequest _getMockActionRequest(Layout layout)
		throws Exception {

		MockActionRequest mockActionRequest = new MockActionRequest();

		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest();

		mockHttpServletRequest.setParameter(
			"portletResource", _serviceBuilderPortlet.getPortletId());
		mockHttpServletRequest.setParameter("scope", "company");

		mockActionRequest.setAttribute(
			PortletServlet.PORTLET_SERVLET_REQUEST, mockHttpServletRequest);

		mockActionRequest.setAttribute(
			WebKeys.THEME_DISPLAY, _getThemeDisplay(layout));
		mockActionRequest.setParameter(
			"portletResource", _serviceBuilderPortlet.getPortletId());
		mockActionRequest.setParameter("scope", "company");

		return mockActionRequest;
	}

	private MockActionRequest _getMockActionRequest(
			List<String> plids, List<Long> roleIds)
		throws Exception {

		MockActionRequest mockActionRequest = new MockActionRequest();

		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest();

		mockHttpServletRequest.setParameter(
			"resourcePrimKey", StringUtil.merge(plids, StringPool.COMMA));

		mockActionRequest.setAttribute(
			PortletServlet.PORTLET_SERVLET_REQUEST, mockHttpServletRequest);

		mockActionRequest.setAttribute(
			WebKeys.THEME_DISPLAY, _getThemeDisplay());

		mockActionRequest.setParameter("modelResource", Layout.class.getName());
		mockActionRequest.setParameter(
			"resourceGroupId", String.valueOf(_group.getGroupId()));

		String roleSearchContainerPrimaryKeys = StringPool.BLANK;

		for (long roleId : roleIds) {
			mockActionRequest.setParameter(roleId + "_ACTION_DELETE", "");
			mockActionRequest.setParameter(roleId + "_ACTION_UPDATE", "");
			mockActionRequest.setParameter(roleId + "_ACTION_VIEW", "");

			roleSearchContainerPrimaryKeys = StringBundler.concat(
				roleSearchContainerPrimaryKeys, roleId, StringPool.COMMA);
		}

		mockActionRequest.setParameter(
			"rolesSearchContainerPrimaryKeys", roleSearchContainerPrimaryKeys);

		return mockActionRequest;
	}

	private MockLiferayResourceRequest _getMockResourceRequest(
		String mvcPath, ThemeDisplay themeDisplay) {

		MockLiferayResourceRequest mockLiferayResourceRequest =
			new MockLiferayResourceRequest();

		mockLiferayResourceRequest.setAttribute(
			WebKeys.THEME_DISPLAY, themeDisplay);
		mockLiferayResourceRequest.setParameter("mvcPath", mvcPath);
		mockLiferayResourceRequest.setParameter(
			"portletResource", _serviceBuilderPortlet.getPortletId());

		return mockLiferayResourceRequest;
	}

	private PortletPreferences _getPortletInstancePortletPreferences(
		Layout layout) {

		return _portletPreferencesLocalService.getPreferences(
			_company.getCompanyId(), PortletKeys.PREFS_OWNER_ID_DEFAULT,
			PortletKeys.PREFS_OWNER_TYPE_LAYOUT, layout.getPlid(),
			_serviceBuilderPortlet.getPortletId());
	}

	private Set<String> _getPortletItemNames() {
		Set<String> names = new HashSet<>();

		for (PortletItem portletItem :
				_portletItemLocalService.getPortletItems(
					_group.getGroupId(),
					_serviceBuilderPortlet.getRootPortletId(),
					_CLASS_NAME_PORTLET_PREFERENCES)) {

			names.add(portletItem.getName());
		}

		return names;
	}

	private ThemeDisplay _getThemeDisplay() throws Exception {
		return _getThemeDisplay(LayoutTestUtil.addTypeContentLayout(_group));
	}

	private ThemeDisplay _getThemeDisplay(Layout layout) throws Exception {
		return _getThemeDisplay(layout, TestPropsValues.getUser());
	}

	private ThemeDisplay _getThemeDisplay(Layout layout, User user)
		throws Exception {

		ThemeDisplay themeDisplay = new ThemeDisplay();

		themeDisplay.setCompany(_company);
		themeDisplay.setLanguageId(LocaleUtil.toLanguageId(_locale));
		themeDisplay.setLayout(layout);
		themeDisplay.setLayoutSet(layout.getLayoutSet());
		themeDisplay.setLayoutTypePortlet(
			(LayoutTypePortlet)layout.getLayoutType());
		themeDisplay.setLocale(_locale);
		themeDisplay.setPermissionChecker(
			PermissionCheckerFactoryUtil.create(user));
		themeDisplay.setScopeGroupId(_group.getGroupId());
		themeDisplay.setSiteGroupId(_group.getGroupId());
		themeDisplay.setUser(user);

		return themeDisplay;
	}

	private ThemeDisplay _getThemeDisplay(User user) throws Exception {
		return _getThemeDisplay(
			LayoutTestUtil.addTypeContentLayout(_group), user);
	}

	private void _invoke(
			Layout layout, String methodName, String parameterName,
			String... parameterValues)
		throws Exception {

		MockActionRequest mockActionRequest = new MockActionRequest();

		mockActionRequest.setAttribute(
			WebKeys.THEME_DISPLAY, _getThemeDisplay(layout));
		mockActionRequest.setParameter(parameterName, parameterValues);
		mockActionRequest.setParameter(
			"portletResource", _serviceBuilderPortlet.getPortletId());

		ReflectionTestUtil.invoke(
			_portlet, methodName,
			new Class<?>[] {ActionRequest.class, ActionResponse.class},
			mockActionRequest, new MockActionResponse());
	}

	private void _testCheckPermissionsIsNotOverridden() throws Exception {
		Class<?> clazz = _portlet.getClass();

		try {
			clazz.getDeclaredMethod("checkPermissions", PortletRequest.class);

			Assert.fail();
		}
		catch (NoSuchMethodException noSuchMethodException) {
			Assert.assertNotNull(noSuchMethodException);
		}
	}

	private void _testCheckPermissionsWithConfigurationPermission()
		throws Exception {

		ReflectionTestUtil.invoke(
			_portlet, "_checkPermissions",
			new Class<?>[] {PortletRequest.class, PortletResponse.class},
			_getMockResourceRequest(
				"/edit_scope.jsp", _getThemeDisplay(TestPropsValues.getUser())),
			new MockLiferayResourceResponse());
	}

	private void _testCheckPermissionsWithoutConfigurationPermission()
		throws Exception {

		try {
			ReflectionTestUtil.invoke(
				_portlet, "_checkPermissions",
				new Class<?>[] {PortletRequest.class, PortletResponse.class},
				_getMockResourceRequest(
					"/edit_scope.jsp", _getThemeDisplay(_addUser())),
				new MockLiferayResourceResponse());

			Assert.fail();
		}
		catch (PrincipalException.MustHavePermission principalException) {
			Assert.assertNotNull(principalException);
		}
	}

	private void _testCheckPermissionsWithoutPermissionsPermission()
		throws Exception {

		try {
			ReflectionTestUtil.invoke(
				_portlet, "_checkPermissions",
				new Class<?>[] {PortletRequest.class, PortletResponse.class},
				_getMockResourceRequest(
					"/edit_permissions.jsp", _getThemeDisplay(_addUser())),
				new MockLiferayResourceResponse());

			Assert.fail();
		}
		catch (PrincipalException.MustHavePermission principalException) {
			Assert.assertNotNull(principalException);
		}
	}

	private void _testRestoreArchivedSetupExistingName() throws Exception {
		Map<String, String[]> preferenceMap = HashMapBuilder.put(
			"key1", new String[] {RandomTestUtil.randomString()}
		).put(
			"key2",
			new String[] {
				RandomTestUtil.randomString(), RandomTestUtil.randomString()
			}
		).build();

		PortletItem portletItem = _addPortletItem(preferenceMap);

		Layout layout = _addLayout(
			HashMapBuilder.put(
				"key1", new String[] {RandomTestUtil.randomString()}
			).put(
				"key3", new String[] {RandomTestUtil.randomString()}
			).build());

		_invoke(layout, "restoreArchivedSetup", "name", portletItem.getName());

		_assertPreferences(
			preferenceMap, _getPortletInstancePortletPreferences(layout));
	}

	private void _testRestoreArchivedSetupMissingName() throws Exception {
		Map<String, String[]> preferenceMap = HashMapBuilder.put(
			"key1", new String[] {RandomTestUtil.randomString()}
		).build();

		Layout layout = _addLayout(preferenceMap);

		Set<String> names = _getPortletItemNames();

		try {
			_invoke(
				layout, "restoreArchivedSetup", "name",
				RandomTestUtil.randomString());

			Assert.fail();
		}
		catch (NoSuchPortletItemException noSuchPortletItemException) {
			Assert.assertNotNull(noSuchPortletItemException);
		}

		Assert.assertEquals(names, _getPortletItemNames());

		_assertPreferences(
			preferenceMap, _getPortletInstancePortletPreferences(layout));
	}

	private void _testUpdateArchivedSetupExistingName() throws Exception {
		Layout layout = _addLayout(
			HashMapBuilder.put(
				"key1", new String[] {RandomTestUtil.randomString()}
			).put(
				"key2", new String[] {RandomTestUtil.randomString()}
			).build());

		String name = RandomTestUtil.randomString();

		_invoke(layout, "updateArchivedSetup", "name", name);

		PortletPreferences portletPreferences =
			_getPortletInstancePortletPreferences(layout);

		String value = RandomTestUtil.randomString();

		portletPreferences.reset("key2");
		portletPreferences.setValue("key1", value);

		portletPreferences.store();

		_invoke(layout, "updateArchivedSetup", "name", name);

		_assertPreferences(
			HashMapBuilder.put(
				"key1", new String[] {value}
			).build(),
			_getArchivedPortletPreferences(_getArchivedSetup(name)));
	}

	private void _testUpdateArchivedSetupNewName() throws Exception {
		Map<String, String[]> preferenceMap = HashMapBuilder.put(
			"key1", new String[] {RandomTestUtil.randomString()}
		).put(
			"key2",
			new String[] {
				RandomTestUtil.randomString(), RandomTestUtil.randomString()
			}
		).put(
			"key3", new String[] {StringPool.BLANK}
		).build();

		Layout layout = _addLayout(preferenceMap);

		String name = RandomTestUtil.randomString();

		_invoke(layout, "updateArchivedSetup", "name", name);

		PortletItem portletItem = _getArchivedSetup(name);

		Assert.assertEquals(
			TestPropsValues.getUserId(), portletItem.getUserId());

		_assertPreferences(
			preferenceMap, _getArchivedPortletPreferences(portletItem));
	}

	private static final String _CLASS_NAME_PORTLET_PREFERENCES =
		com.liferay.portal.kernel.model.PortletPreferences.class.getName();

	@Inject
	private static PortletLocalService _portletLocalService;

	private static com.liferay.portal.kernel.model.Portlet
		_serviceBuilderPortlet;
	private static ServiceRegistration<?> _serviceRegistration;

	private Company _company;

	@Inject
	private CompanyLocalService _companyLocalService;

	@DeleteAfterTestRun
	private Group _group;

	private Locale _locale;

	@Inject
	private Portal _portal;

	@Inject(
		filter = "component.name=com.liferay.portlet.configuration.web.internal.portlet.PortletConfigurationPortlet"
	)
	private Portlet _portlet;

	@Inject
	private PortletItemLocalService _portletItemLocalService;

	@Inject
	private PortletPreferencesLocalService _portletPreferencesLocalService;

	@Inject
	private ResourcePermissionLocalService _resourcePermissionLocalService;

	@DeleteAfterTestRun
	private final List<User> _users = new ArrayList<>();

	private static class MockActionRequest
		extends MockLiferayPortletActionRequest {

		@Override
		public ActionParameters getActionParameters() {
			return new MockActionParameters();
		}

		private class MockActionParameters implements ActionParameters {

			@Override
			public MutableActionParameters clone() {
				return null;
			}

			@Override
			public Set<String> getNames() {
				return getParameterMap().keySet();
			}

			@Override
			public String getValue(String name) {
				return getParameter(name);
			}

			@Override
			public String[] getValues(String name) {
				return getParameterValues(name);
			}

			@Override
			public boolean isEmpty() {
				return getParameterMap().isEmpty();
			}

			@Override
			public int size() {
				return getParameterMap().size();
			}

		}

	}

	private static class TestMockLiferayResourceResponse
		extends MockLiferayResourceResponse {

		@Override
		public String getProperty(String name) {
			return _properties.get(name);
		}

		@Override
		public void setProperty(String property, String value) {
			_properties.put(property, value);
		}

		private final Map<String, String> _properties = new HashMap<>();

	}

}