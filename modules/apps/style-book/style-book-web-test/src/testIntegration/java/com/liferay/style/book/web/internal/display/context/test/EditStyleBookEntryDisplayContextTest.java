/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.style.book.web.internal.display.context.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.depot.constants.DepotConstants;
import com.liferay.depot.model.DepotEntry;
import com.liferay.depot.service.DepotEntryGroupRelLocalService;
import com.liferay.depot.service.DepotEntryLocalService;
import com.liferay.fragment.model.FragmentCollection;
import com.liferay.fragment.service.FragmentCollectionLocalService;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.feature.flag.constants.FeatureFlagConstants;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCRenderCommand;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletRenderRequest;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletRenderResponse;
import com.liferay.portal.kernel.test.portlet.MockPortalContext;
import com.liferay.portal.kernel.test.portlet.MockResourceURL;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.FeatureFlagTestUtil;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.props.test.util.PropsTemporarySwapper;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.style.book.model.StyleBookEntry;
import com.liferay.style.book.service.StyleBookEntryLocalService;
import com.liferay.style.book.test.util.FrontendTokenDefinitionTestUtil;

import jakarta.portlet.ResourceURL;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.mock.web.MockHttpServletRequest;

/**
 * @author Gabriel Lima
 */
@RunWith(Arquillian.class)
public class EditStyleBookEntryDisplayContextTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();
	}

	@FeatureFlag("LPD-57283")
	@Test
	@TestInfo("LPD-101911")
	public void testGetFragmentCollectionOptionJSONObject() throws Exception {
		FeatureFlagTestUtil.invokeFeatureFlagListeners(
			TestPropsValues.getCompanyId(), true, "LPD-57283");

		DepotEntry connectedDepotEntry = _addDesignLibraryDepotEntry();

		FragmentCollection connectedFragmentCollection = _addFragmentCollection(
			connectedDepotEntry.getGroupId());

		DepotEntry disconnectedDepotEntry = _addDesignLibraryDepotEntry();

		FragmentCollection disconnectedFragmentCollection =
			_addFragmentCollection(disconnectedDepotEntry.getGroupId());

		FragmentCollection siteFragmentCollection = _addFragmentCollection(
			_group.getGroupId());

		Object editStyleBookEntryDisplayContext =
			_getEditStyleBookEntryDisplayContext(
				_addStyleBookEntry(
					FrontendTokenDefinitionTestUtil.getFrontendTokenDefinition(
						RandomTestUtil.randomString())));

		JSONObject disconnectedJSONObject =
			_getFragmentCollectionOptionJSONObject(
				editStyleBookEntryDisplayContext);

		List<String> fragmentCollectionNames = _getFragmentCollectionNames(
			disconnectedJSONObject);

		String connectedFragmentCollectionName = StringBundler.concat(
			"[", _getDescriptiveName(connectedDepotEntry), "] ",
			connectedFragmentCollection.getName());

		Assert.assertFalse(
			fragmentCollectionNames.toString(),
			fragmentCollectionNames.contains(connectedFragmentCollectionName));

		_depotEntryGroupRelLocalService.addDepotEntryGroupRel(
			connectedDepotEntry.getDepotEntryId(), _group.getGroupId());

		JSONObject connectedJSONObject = _getFragmentCollectionOptionJSONObject(
			editStyleBookEntryDisplayContext);

		fragmentCollectionNames = _getFragmentCollectionNames(
			connectedJSONObject);

		Assert.assertTrue(
			fragmentCollectionNames.toString(),
			fragmentCollectionNames.contains(connectedFragmentCollectionName));
		Assert.assertFalse(
			fragmentCollectionNames.toString(),
			fragmentCollectionNames.contains(
				disconnectedFragmentCollection.getName()));
		Assert.assertTrue(
			fragmentCollectionNames.toString(),
			fragmentCollectionNames.contains(siteFragmentCollection.getName()));

		Assert.assertEquals(
			disconnectedJSONObject.getInt("totalLayouts") + 1,
			connectedJSONObject.getInt("totalLayouts"));

		try (PropsTemporarySwapper propsTemporarySwapper =
				new PropsTemporarySwapper(
					FeatureFlagConstants.getKey("LPD-57283"),
					Boolean.FALSE.toString())) {

			JSONObject jsonObject = _getFragmentCollectionOptionJSONObject(
				editStyleBookEntryDisplayContext);

			fragmentCollectionNames = _getFragmentCollectionNames(jsonObject);

			Assert.assertFalse(
				fragmentCollectionNames.toString(),
				fragmentCollectionNames.contains(
					connectedFragmentCollectionName));

			Assert.assertEquals(
				disconnectedJSONObject.getInt("totalLayouts"),
				jsonObject.getInt("totalLayouts"));
		}
	}

	@Test
	public void testGetFrontendTokenDefinitionsJSONArray() throws Exception {
		StyleBookEntry styleBookEntry = _addStyleBookEntry(
			FrontendTokenDefinitionTestUtil.getFrontendTokenDefinition(
				RandomTestUtil.randomString()));

		JSONArray frontendTokenDefinitionsJSONArray = ReflectionTestUtil.invoke(
			_getEditStyleBookEntryDisplayContext(styleBookEntry),
			"_getFrontendTokenDefinitionsJSONArray", new Class<?>[0]);

		Map<String, JSONObject> frontendTokenDefinitionJSONObjects =
			JSONUtil.toJSONObjectMap(frontendTokenDefinitionsJSONArray, "id");

		Assert.assertTrue(
			frontendTokenDefinitionJSONObjects.containsKey(_THEME_ID_CLASSIC));
	}

	private DepotEntry _addDesignLibraryDepotEntry() throws Exception {
		return _depotEntryLocalService.addDepotEntry(
			Collections.singletonMap(
				LocaleUtil.getDefault(), RandomTestUtil.randomString()),
			Collections.emptyMap(), DepotConstants.TYPE_DESIGN_LIBRARY,
			ServiceContextTestUtil.getServiceContext());
	}

	private FragmentCollection _addFragmentCollection(long groupId)
		throws Exception {

		return _fragmentCollectionLocalService.addFragmentCollection(
			null, TestPropsValues.getUserId(), groupId,
			RandomTestUtil.randomString(), RandomTestUtil.randomString(),
			ServiceContextTestUtil.getServiceContext());
	}

	private StyleBookEntry _addStyleBookEntry(String frontendTokenDefinition)
		throws Exception {

		ServiceContext serviceContext = new ServiceContext();

		serviceContext.setScopeGroupId(_group.getGroupId());
		serviceContext.setUserId(TestPropsValues.getUserId());

		return _styleBookEntryLocalService.addStyleBookEntry(
			null, TestPropsValues.getUserId(), _group.getGroupId(), false,
			frontendTokenDefinition, StringPool.BLANK,
			RandomTestUtil.randomString(), RandomTestUtil.randomString(),
			_THEME_ID_CLASSIC, serviceContext);
	}

	private String _getDescriptiveName(DepotEntry depotEntry) throws Exception {
		Group group = depotEntry.getGroup();

		return group.getDescriptiveName(LocaleUtil.getDefault());
	}

	private Object _getEditStyleBookEntryDisplayContext(
			StyleBookEntry styleBookEntry)
		throws Exception {

		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest();

		mockHttpServletRequest.setParameter(
			"styleBookEntryId",
			String.valueOf(styleBookEntry.getStyleBookEntryId()));

		ThemeDisplay themeDisplay = new ThemeDisplay();

		themeDisplay.setCompany(
			_companyLocalService.getCompany(TestPropsValues.getCompanyId()));
		themeDisplay.setLanguageId(
			LanguageUtil.getLanguageId(LocaleUtil.getDefault()));
		themeDisplay.setLocale(LocaleUtil.getDefault());
		themeDisplay.setPermissionChecker(
			PermissionThreadLocal.getPermissionChecker());
		themeDisplay.setRealUser(TestPropsValues.getUser());
		themeDisplay.setScopeGroupId(_group.getGroupId());
		themeDisplay.setSiteGroupId(_group.getGroupId());
		themeDisplay.setUser(TestPropsValues.getUser());

		mockHttpServletRequest.setAttribute(
			WebKeys.THEME_DISPLAY, themeDisplay);

		MockLiferayPortletRenderRequest mockLiferayPortletRenderRequest =
			new MockLiferayPortletRenderRequest(mockHttpServletRequest);

		_mvcRenderCommand.render(
			mockLiferayPortletRenderRequest,
			new TestMockLiferayPortletRenderResponse());

		return mockLiferayPortletRenderRequest.getAttribute(
			"com.liferay.style.book.web.internal.display.context." +
				"EditStyleBookEntryDisplayContext");
	}

	private List<String> _getFragmentCollectionNames(
			JSONObject fragmentCollectionOptionJSONObject)
		throws Exception {

		return JSONUtil.toList(
			fragmentCollectionOptionJSONObject.getJSONArray("recentLayouts"),
			jsonObject -> jsonObject.getString("name"));
	}

	private JSONObject _getFragmentCollectionOptionJSONObject(
		Object editStyleBookEntryDisplayContext) {

		return ReflectionTestUtil.invoke(
			editStyleBookEntryDisplayContext,
			"_getFragmentCollectionOptionJSONObject", new Class<?>[0]);
	}

	private static final String _THEME_ID_CLASSIC = "classic_WAR_classictheme";

	@Inject
	private CompanyLocalService _companyLocalService;

	@Inject
	private DepotEntryGroupRelLocalService _depotEntryGroupRelLocalService;

	@Inject
	private DepotEntryLocalService _depotEntryLocalService;

	@Inject
	private FragmentCollectionLocalService _fragmentCollectionLocalService;

	@DeleteAfterTestRun
	private Group _group;

	@Inject(
		filter = "component.name=com.liferay.style.book.web.internal.portlet.action.EditStyleBookEntryMVCRenderCommand"
	)
	private MVCRenderCommand _mvcRenderCommand;

	@Inject
	private StyleBookEntryLocalService _styleBookEntryLocalService;

	private static class TestMockLiferayPortletRenderResponse
		extends MockLiferayPortletRenderResponse {

		@Override
		public ResourceURL createResourceURL() {
			return new MockResourceURL(
				new MockPortalContext(), RandomTestUtil.randomString());
		}

	}

}