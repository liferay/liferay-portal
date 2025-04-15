/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.fragment.internal.portlet.action.test;

import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.fragment.helper.DefaultInputFragmentEntryConfigurationProvider;
import com.liferay.info.field.type.BooleanInfoFieldType;
import com.liferay.info.field.type.TextInfoFieldType;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.LayoutSet;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletActionRequest;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletActionResponse;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;

import java.util.Dictionary;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;

/**
 * @author Víctor Galán
 */
@RunWith(Arquillian.class)
public class UpdateDefaultInputFragmentsMVCActionCommandMVCActionCommandTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();

		_company = _companyLocalService.getCompany(_group.getCompanyId());
	}

	@After
	public void tearDown() throws Exception {
		_group = GroupTestUtil.addGroup();

		_company = _companyLocalService.getCompany(_group.getCompanyId());
	}

	@Test
	public void testUpdateDefaultInputFragments() throws Exception {
		JSONObject valuesJSONObject = JSONUtil.put(
			BooleanInfoFieldType.INSTANCE.getName(),
			JSONUtil.put(
				"groupKey", _group.getGroupKey()
			).put(
				"key", RandomTestUtil.randomString()
			)
		).put(
			TextInfoFieldType.INSTANCE.getName(),
			JSONUtil.put(
				"groupKey", _group.getGroupKey()
			).put(
				"key", RandomTestUtil.randomString()
			)
		);

		ReflectionTestUtil.invoke(
			_mvcActionCommand, "doProcessAction",
			new Class<?>[] {ActionRequest.class, ActionResponse.class},
			_getMockLiferayPortletActionRequest(valuesJSONObject.toString()),
			new MockLiferayPortletActionResponse());

		Configuration[] configurations = _getConfigurations();

		Configuration configuration = configurations[0];

		Dictionary<String, Object> properties = configuration.getProperties();

		JSONObject defaultInputFragmentEntryKeysJSONObject =
			_jsonFactory.createJSONObject(
				(String)properties.get("defaultInputFragmentEntryKeys"));

		_assertEqualJSONObject(
			valuesJSONObject.getJSONObject(
				BooleanInfoFieldType.INSTANCE.getName()),
			defaultInputFragmentEntryKeysJSONObject.getJSONObject(
				BooleanInfoFieldType.INSTANCE.getName()));

		_assertEqualJSONObject(
			valuesJSONObject.getJSONObject(
				TextInfoFieldType.INSTANCE.getName()),
			defaultInputFragmentEntryKeysJSONObject.getJSONObject(
				TextInfoFieldType.INSTANCE.getName()));

		configuration.delete();
	}

	private void _assertEqualJSONObject(
			JSONObject expectedJSONObject, JSONObject actualJSONObject)
		throws Exception {

		Assert.assertEquals(
			_objectMapper.readTree(expectedJSONObject.toString()),
			_objectMapper.readTree(actualJSONObject.toString()));
	}

	private Configuration[] _getConfigurations() throws Exception {
		String pidFilter = StringBundler.concat(
			"(service.factoryPid=", _PID, StringPool.CLOSE_PARENTHESIS);

		return _configurationAdmin.listConfigurations(pidFilter);
	}

	private MockLiferayPortletActionRequest _getMockLiferayPortletActionRequest(
			String values)
		throws Exception {

		MockLiferayPortletActionRequest mockLiferayPortletActionRequest =
			new MockLiferayPortletActionRequest();

		mockLiferayPortletActionRequest.setAttribute(
			WebKeys.THEME_DISPLAY, _getThemeDisplay());

		mockLiferayPortletActionRequest.setParameter("values", values);

		return mockLiferayPortletActionRequest;
	}

	private ThemeDisplay _getThemeDisplay() throws Exception {
		ThemeDisplay themeDisplay = new ThemeDisplay();

		themeDisplay.setCompany(_company);

		LayoutSet layoutSet = _group.getPublicLayoutSet();

		themeDisplay.setLookAndFeel(layoutSet.getTheme(), null);

		themeDisplay.setPermissionChecker(
			PermissionThreadLocal.getPermissionChecker());
		themeDisplay.setRealUser(TestPropsValues.getUser());
		themeDisplay.setScopeGroupId(_group.getGroupId());
		themeDisplay.setSiteGroupId(_group.getGroupId());
		themeDisplay.setUser(TestPropsValues.getUser());

		return themeDisplay;
	}

	private static final String _PID =
		"com.liferay.fragment.configuration." +
			"DefaultInputFragmentEntryConfiguration.scoped";

	private Company _company;

	@Inject
	private CompanyLocalService _companyLocalService;

	@Inject
	private ConfigurationAdmin _configurationAdmin;

	@Inject
	private DefaultInputFragmentEntryConfigurationProvider
		_defaultInputFragmentEntryConfigurationProvider;

	@DeleteAfterTestRun
	private Group _group;

	@Inject
	private JSONFactory _jsonFactory;

	@Inject(
		filter = "mvc.command.name=/fragment/update_default_input_fragment_entries"
	)
	private MVCActionCommand _mvcActionCommand;

	private final ObjectMapper _objectMapper = new ObjectMapper() {
		{
			configure(MapperFeature.SORT_PROPERTIES_ALPHABETICALLY, true);
		}
	};

}