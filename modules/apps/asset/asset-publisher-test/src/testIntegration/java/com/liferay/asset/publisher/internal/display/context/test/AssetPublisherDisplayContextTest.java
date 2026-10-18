/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.asset.publisher.internal.display.context.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.asset.kernel.model.AssetCategory;
import com.liferay.asset.kernel.model.AssetEntry;
import com.liferay.asset.kernel.model.AssetVocabulary;
import com.liferay.asset.kernel.service.AssetCategoryLocalService;
import com.liferay.asset.kernel.service.AssetEntryLocalService;
import com.liferay.asset.kernel.service.AssetVocabularyLocalService;
import com.liferay.asset.list.constants.AssetListPortletKeys;
import com.liferay.asset.publisher.constants.AssetPublisherPortletKeys;
import com.liferay.asset.publisher.constants.AssetPublisherWebKeys;
import com.liferay.asset.publisher.util.AssetEntryResult;
import com.liferay.blogs.constants.BlogsPortletKeys;
import com.liferay.blogs.model.BlogsEntry;
import com.liferay.depot.constants.DepotConstants;
import com.liferay.depot.model.DepotEntry;
import com.liferay.depot.service.DepotEntryGroupRelLocalService;
import com.liferay.depot.service.DepotEntryLocalService;
import com.liferay.exportimport.kernel.staging.StagingUtil;
import com.liferay.journal.constants.JournalFolderConstants;
import com.liferay.journal.constants.JournalPortletKeys;
import com.liferay.journal.model.JournalArticle;
import com.liferay.journal.test.util.JournalTestUtil;
import com.liferay.layout.test.util.LayoutTestUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.configuration.test.util.ConfigurationTestUtil;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.LayoutConstants;
import com.liferay.portal.kernel.portlet.LiferayPortletConfig;
import com.liferay.portal.kernel.portlet.LiferayWindowState;
import com.liferay.portal.kernel.portlet.PortletConfigFactoryUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.constants.MVCRenderConstants;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.service.PortletLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.settings.LocalizedValuesMap;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletRenderRequest;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletRenderResponse;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.rule.SynchronousDestinationTestRule;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.theme.PortletDisplay;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.kernel.util.JavaConstants;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.Localization;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.UnicodeProperties;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.portlet.PortletPreferencesImpl;
import com.liferay.portlet.test.MockLiferayPortletContext;

import jakarta.portlet.Portlet;
import jakarta.portlet.PortletPreferences;
import jakarta.portlet.PortletRequest;
import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;

import org.springframework.mock.web.MockHttpServletRequest;

/**
 * @author Jürgen Kappler
 */
@RunWith(Arquillian.class)
public class AssetPublisherDisplayContextTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE,
			SynchronousDestinationTestRule.INSTANCE);

	@AfterClass
	public static void tearDownClass() throws Exception {
		ConfigurationTestUtil.deleteConfiguration(
			_assetPublisherWebConfiguration);
	}

	@Before
	public void setUp() throws Exception {
		_assetPublisherWebConfiguration = _configurationAdmin.getConfiguration(
			"com.liferay.asset.publisher.web.internal.configuration." +
				"AssetPublisherWebConfiguration",
			StringPool.QUESTION);

		ConfigurationTestUtil.saveConfiguration(
			_assetPublisherWebConfiguration,
			HashMapDictionaryBuilder.<String, Object>put(
				"searchWithIndex", false
			).build());

		_group = GroupTestUtil.addGroup();

		_company = _companyLocalService.getCompany(_group.getCompanyId());
	}

	@Test
	public void testGetAssetEntryResultsFilterByAssetCategory()
		throws Exception {

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext();

		AssetVocabulary assetVocabulary =
			_assetVocabularyLocalService.addVocabulary(
				TestPropsValues.getUserId(), _group.getGroupId(),
				RandomTestUtil.randomString(), serviceContext);

		AssetCategory assetCategory = _assetCategoryLocalService.addCategory(
			TestPropsValues.getUserId(), _group.getGroupId(),
			RandomTestUtil.randomString(), assetVocabulary.getVocabularyId(),
			serviceContext);

		JournalArticle journalArticle = _addJournalArticle(
			new long[] {assetCategory.getCategoryId()}, null);

		AssetEntry expectedAssetEntry = _assetEntryLocalService.getEntry(
			JournalArticle.class.getName(),
			journalArticle.getResourcePrimKey());

		JournalTestUtil.addArticle(
			_group.getGroupId(),
			JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID);

		PortletPreferences portletPreferences = new PortletPreferencesImpl();

		portletPreferences.setValue("selectionStyle", "dynamic");

		_testGetAssetEntryResults(_getAssetEntryResults(portletPreferences), 2);

		portletPreferences.setValue("queryContains0", "true");
		portletPreferences.setValue("queryName0", "assetCategories");
		portletPreferences.setValue(
			"queryValues0", String.valueOf(assetCategory.getCategoryId()));

		List<AssetEntry> assetEntries = _testGetAssetEntryResults(
			_getAssetEntryResults(portletPreferences), 1);

		Assert.assertEquals(expectedAssetEntry, assetEntries.get(0));
	}

	@Test
	public void testGetAssetEntryResultsFilterByAssetTags() throws Exception {
		String assetTagName = RandomTestUtil.randomString();

		JournalArticle journalArticle = _addJournalArticle(
			null, new String[] {assetTagName});

		AssetEntry expectedAssetEntry = _assetEntryLocalService.getEntry(
			JournalArticle.class.getName(),
			journalArticle.getResourcePrimKey());

		JournalTestUtil.addArticle(
			_group.getGroupId(),
			JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID);

		PortletPreferences portletPreferences = new PortletPreferencesImpl();

		portletPreferences.setValue("selectionStyle", "dynamic");

		_testGetAssetEntryResults(_getAssetEntryResults(portletPreferences), 2);

		portletPreferences.setValue("queryContains0", "true");
		portletPreferences.setValue("queryName0", "assetTags");
		portletPreferences.setValue("queryValues0", assetTagName);

		List<AssetEntry> assetEntries = _testGetAssetEntryResults(
			_getAssetEntryResults(portletPreferences), 1);

		Assert.assertEquals(expectedAssetEntry, assetEntries.get(0));
	}

	@Test
	public void testGetAssetEntryResultsOrderByColumn() throws Exception {
		Date date = new Date();

		AssetEntry assetEntry1 = _addAssetEntry(date, date, date, 0.9);

		date = new Date(date.getYear(), date.getMonth(), date.getDate() - 10);

		AssetEntry assetEntry2 = _addAssetEntry(date, date, date, 0.5);

		date = new Date(date.getYear(), date.getMonth(), date.getDate() + 5);

		AssetEntry assetEntry3 = _addAssetEntry(date, date, date, 0.3);

		_testGetAssetEntryResultsOrderByColumn(
			"createDate", Arrays.asList(assetEntry2, assetEntry3, assetEntry1));
		_testGetAssetEntryResultsOrderByColumn(
			"modifiedDate",
			Arrays.asList(assetEntry2, assetEntry3, assetEntry1));
		_testGetAssetEntryResultsOrderByColumn(
			"publishedDate",
			Arrays.asList(assetEntry2, assetEntry3, assetEntry1));
	}

	@Test
	public void testGetAssetEntryTypesJSONArrayWithUnstagedPortlet()
		throws Exception {

		Layout layout = LayoutTestUtil.addTypePortletLayout(_group);

		GroupTestUtil.enableLocalStaging(_group);

		_setStagedPortlet(BlogsPortletKeys.BLOGS, true);
		_setStagedPortlet(JournalPortletKeys.JOURNAL, false);

		_depotEntry = _depotEntryLocalService.addDepotEntry(
			HashMapBuilder.put(
				LocaleUtil.getDefault(), RandomTestUtil.randomString()
			).build(),
			null, DepotConstants.TYPE_ASSET_LIBRARY,
			ServiceContextTestUtil.getServiceContext());

		Group stagingGroup = _group.getStagingGroup();

		_depotEntryGroupRelLocalService.addDepotEntryGroupRel(
			_depotEntry.getDepotEntryId(), stagingGroup.getGroupId());

		JSONArray jsonArray = ReflectionTestUtil.invoke(
			_getAssetPublisherDisplayContext(
				layout, new PortletPreferencesImpl()),
			"getAssetEntryTypesJSONArray", new Class<?>[] {long.class},
			stagingGroup.getGroupId());

		Assert.assertArrayEquals(
			new long[] {
				stagingGroup.getGroupId(), _company.getGroupId(),
				_depotEntry.getGroupId()
			},
			_getGroupIds(BlogsEntry.class.getName(), jsonArray));
		Assert.assertArrayEquals(
			new long[] {_group.getGroupId(), _company.getGroupId()},
			_getGroupIds(JournalArticle.class.getName(), jsonArray));
	}

	@Test
	public void testGetEmailAssetEntryAddedBody() throws Exception {
		PortletPreferences portletPreferences = new PortletPreferencesImpl();

		LocalizedValuesMap localizedValuesMap = ReflectionTestUtil.invoke(
			_getAssetPublisherDisplayContext(
				LayoutTestUtil.addTypePortletLayout(_group),
				portletPreferences),
			"getEmailAssetEntryAddedBody", new Class<?>[0]);

		Assert.assertNotNull(
			localizedValuesMap.get(LocaleUtil.getSiteDefault()));

		portletPreferences.setValue(
			_localization.getLocalizedName(
				"emailAssetEntryAddedBody",
				LocaleUtil.toLanguageId(LocaleUtil.getSiteDefault())),
			"Test Body");

		localizedValuesMap = ReflectionTestUtil.invoke(
			_getAssetPublisherDisplayContext(
				LayoutTestUtil.addTypePortletLayout(_group),
				portletPreferences),
			"getEmailAssetEntryAddedBody", new Class<?>[0]);

		Assert.assertEquals(
			"Test Body", localizedValuesMap.get(LocaleUtil.getSiteDefault()));
	}

	@Test
	public void testGetEmailAssetEntryAddedSubject() throws Exception {
		PortletPreferences portletPreferences = new PortletPreferencesImpl();

		LocalizedValuesMap localizedValuesMap = ReflectionTestUtil.invoke(
			_getAssetPublisherDisplayContext(
				LayoutTestUtil.addTypePortletLayout(_group),
				portletPreferences),
			"getEmailAssetEntryAddedSubject", new Class<?>[0]);

		Assert.assertNotNull(
			localizedValuesMap.get(LocaleUtil.getSiteDefault()));

		portletPreferences.setValue(
			_localization.getLocalizedName(
				"emailAssetEntryAddedSubject",
				LocaleUtil.toLanguageId(LocaleUtil.getSiteDefault())),
			"Test Subject");

		localizedValuesMap = ReflectionTestUtil.invoke(
			_getAssetPublisherDisplayContext(
				LayoutTestUtil.addTypePortletLayout(_group),
				portletPreferences),
			"getEmailAssetEntryAddedSubject", new Class<?>[0]);

		Assert.assertEquals(
			"Test Subject",
			localizedValuesMap.get(LocaleUtil.getSiteDefault()));
	}

	@FeatureFlag(enable = false, value = "LPD-89171")
	@Test
	public void testGetPortletURLWhenRedirectParameterIsDisabled()
		throws Exception {

		String portletURLString = _getPortletURLString();

		Assert.assertFalse(portletURLString.contains("redirect"));
	}

	@FeatureFlag("LPD-89171")
	@Test
	public void testGetPortletURLWhenRedirectParameterIsEnabled()
		throws Exception {

		String portletURLString = _getPortletURLString();

		Assert.assertTrue(portletURLString.contains("redirect"));
	}

	@Test
	public void testGetSelectCollectionProps() throws Exception {
		Map<String, Object> selectCollectionProps = ReflectionTestUtil.invoke(
			_getAssetPublisherDisplayContext(
				LayoutTestUtil.addTypePortletLayout(_group),
				new PortletPreferencesImpl()),
			"getSelectCollectionProps", new Class<?>[0]);

		Assert.assertEquals(
			selectCollectionProps.get("addAssetListEntryURL"),
			PortletURLBuilder.create(
				_portal.getControlPanelPortletURL(
					_mockLiferayPortletRenderRequest,
					_themeDisplay.getScopeGroup(),
					AssetListPortletKeys.ASSET_LIST, 0, 0,
					PortletRequest.RENDER_PHASE)
			).setWindowState(
				LiferayWindowState.POP_UP
			).buildString());
	}

	@Test
	public void testIsEnableSetAsDefaultAssetPublisher() throws Exception {
		Assert.assertFalse(
			ReflectionTestUtil.invoke(
				_getAssetPublisherDisplayContext(
					_addLayout(_group, LayoutConstants.TYPE_ASSET_DISPLAY),
					new PortletPreferencesImpl()),
				"isEnableSetAsDefaultAssetPublisher", new Class<?>[0]));
		Assert.assertTrue(
			ReflectionTestUtil.invoke(
				_getAssetPublisherDisplayContext(
					LayoutTestUtil.addTypeContentLayout(_group),
					new PortletPreferencesImpl()),
				"isEnableSetAsDefaultAssetPublisher", new Class<?>[0]));
		Assert.assertTrue(
			ReflectionTestUtil.invoke(
				_getAssetPublisherDisplayContext(
					LayoutTestUtil.addTypePortletLayout(_group),
					new PortletPreferencesImpl()),
				"isEnableSetAsDefaultAssetPublisher", new Class<?>[0]));
	}

	private AssetEntry _addAssetEntry(
			Date createDate, Date modifiedDate, Date publishedDate,
			double priority)
		throws Exception {

		JournalArticle journalArticle = JournalTestUtil.addArticle(
			_group.getGroupId(),
			JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID);

		AssetEntry assetEntry = _assetEntryLocalService.getEntry(
			JournalArticle.class.getName(),
			journalArticle.getResourcePrimKey());

		assetEntry.setCreateDate(createDate);
		assetEntry.setModifiedDate(modifiedDate);
		assetEntry.setPublishDate(publishedDate);
		assetEntry.setPriority(priority);

		return _assetEntryLocalService.updateAssetEntry(assetEntry);
	}

	private JournalArticle _addJournalArticle(
			long[] assetCategoryIds, String[] assetTagNames)
		throws Exception {

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(_group.getGroupId());

		serviceContext.setAssetCategoryIds(assetCategoryIds);
		serviceContext.setAssetTagNames(assetTagNames);

		return JournalTestUtil.addArticle(
			_group.getGroupId(),
			JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID, serviceContext);
	}

	private Layout _addLayout(Group group, String type) throws Exception {
		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(
				group, TestPropsValues.getUserId());

		serviceContext.setAttribute(
			"layout.instanceable.allowed", Boolean.TRUE);

		return _layoutLocalService.addLayout(
			null, TestPropsValues.getUserId(), group.getGroupId(), false,
			LayoutConstants.DEFAULT_PARENT_LAYOUT_ID,
			RandomTestUtil.randomString(), RandomTestUtil.randomString(),
			StringPool.BLANK, type, false, false, StringPool.BLANK,
			serviceContext);
	}

	private List<AssetEntryResult> _getAssetEntryResults(
			PortletPreferences portletPreferences)
		throws Exception {

		return ReflectionTestUtil.invoke(
			_getAssetPublisherDisplayContext(
				LayoutTestUtil.addTypePortletLayout(_group),
				portletPreferences),
			"getAssetEntryResults", new Class<?>[0]);
	}

	private Object _getAssetPublisherDisplayContext(
			Layout layout, PortletPreferences portletPreferences)
		throws Exception {

		_mockLiferayPortletRenderRequest =
			new TestMockLiferayPortletRenderRequest(
				new MockHttpServletRequest());

		com.liferay.portal.kernel.model.Portlet portlet =
			_portletLocalService.getPortletById(
				AssetPublisherPortletKeys.ASSET_PUBLISHER);

		LiferayPortletConfig liferayPortletConfig =
			(LiferayPortletConfig)PortletConfigFactoryUtil.create(
				portlet, null);

		_mockLiferayPortletRenderRequest.setAttribute(
			JavaConstants.JAKARTA_PORTLET_CONFIG, liferayPortletConfig);

		String path = "/view.jsp";

		_mockLiferayPortletRenderRequest.setAttribute(
			MVCRenderConstants.
				PORTLET_CONTEXT_OVERRIDE_REQUEST_ATTIBUTE_NAME_PREFIX + path,
			new MockLiferayPortletContext(path));

		_mockLiferayPortletRenderRequest.setAttribute(
			WebKeys.PORTLET_ID, AssetPublisherPortletKeys.ASSET_PUBLISHER);

		_themeDisplay = _getThemeDisplay(layout, portletPreferences);

		_mockLiferayPortletRenderRequest.setAttribute(
			WebKeys.THEME_DISPLAY, _themeDisplay);

		_mockLiferayPortletRenderRequest.setParameter("mvcPath", path);
		_mockLiferayPortletRenderRequest.setParameter(
			"portletResource", AssetPublisherPortletKeys.ASSET_PUBLISHER);

		ReflectionTestUtil.invoke(
			_portlet, "doDispatch",
			new Class<?>[] {RenderRequest.class, RenderResponse.class},
			_mockLiferayPortletRenderRequest,
			new TestMockLiferayPortletRenderResponse());

		return _mockLiferayPortletRenderRequest.getAttribute(
			AssetPublisherWebKeys.ASSET_PUBLISHER_DISPLAY_CONTEXT);
	}

	private long[] _getGroupIds(String className, JSONArray jsonArray) {
		long classNameId = _portal.getClassNameId(className);

		for (int i = 0; i < jsonArray.length(); i++) {
			JSONObject jsonObject = jsonArray.getJSONObject(i);

			if (jsonObject.getLong("classNameId") == classNameId) {
				return JSONUtil.toLongArray(
					jsonObject.getJSONArray("groupIds"));
			}
		}

		return null;
	}

	private String _getPortletURLString() throws Exception {
		PortletPreferences portletPreferences = new PortletPreferencesImpl();

		portletPreferences.setValue("paginationType", "regular");

		Object assetPublisherDisplayContext = _getAssetPublisherDisplayContext(
			LayoutTestUtil.addTypePortletLayout(_group), portletPreferences);

		_mockLiferayPortletRenderRequest.setParameter(
			"redirect", RandomTestUtil.randomString());

		Object portletURL = ReflectionTestUtil.invoke(
			assetPublisherDisplayContext, "getPortletURL", new Class<?>[0]);

		return String.valueOf(portletURL);
	}

	private ThemeDisplay _getThemeDisplay(
			Layout layout, PortletPreferences portletPreferences)
		throws Exception {

		ThemeDisplay themeDisplay = new ThemeDisplay();

		PortletDisplay portletDisplay = themeDisplay.getPortletDisplay();

		portletDisplay.setPortletName(
			AssetPublisherPortletKeys.ASSET_PUBLISHER);
		portletDisplay.setPortletPreferences(portletPreferences);

		themeDisplay.setCompany(_company);
		themeDisplay.setLayout(layout);
		themeDisplay.setLocale(LocaleUtil.getSiteDefault());
		themeDisplay.setPermissionChecker(
			PermissionThreadLocal.getPermissionChecker());
		themeDisplay.setRealUser(TestPropsValues.getUser());
		themeDisplay.setScopeGroupId(_group.getGroupId());
		themeDisplay.setSiteGroupId(_group.getGroupId());
		themeDisplay.setUser(TestPropsValues.getUser());

		return themeDisplay;
	}

	private void _setStagedPortlet(String portletId, boolean staged)
		throws Exception {

		_group = _groupLocalService.getGroup(_group.getGroupId());

		UnicodeProperties typeSettingsUnicodeProperties =
			_group.getTypeSettingsProperties();

		typeSettingsUnicodeProperties.setProperty(
			StagingUtil.getStagedPortletId(portletId), String.valueOf(staged));

		_group = _groupLocalService.updateGroup(
			_group.getGroupId(), typeSettingsUnicodeProperties.toString());
	}

	private List<AssetEntry> _testGetAssetEntryResults(
		List<AssetEntryResult> assetEntryResults, int expectedAssetEntries) {

		Assert.assertEquals(
			assetEntryResults.toString(), 1, assetEntryResults.size());

		AssetEntryResult assetEntryResult = assetEntryResults.get(0);

		List<AssetEntry> assetEntries = assetEntryResult.getAssetEntries();

		Assert.assertEquals(
			assetEntries.toString(), expectedAssetEntries, assetEntries.size());

		return assetEntries;
	}

	private void _testGetAssetEntryResultsOrderByColumn(
			String orderByColumn, List<AssetEntry> expectedAssetEntries)
		throws Exception {

		PortletPreferences portletPreferences = new PortletPreferencesImpl();

		portletPreferences.setValue(orderByColumn, "createDate");
		portletPreferences.setValue("orderByType1", "ASC");
		portletPreferences.setValue("selectionStyle", "dynamic");

		List<AssetEntry> assetEntries = _testGetAssetEntryResults(
			_getAssetEntryResults(portletPreferences),
			expectedAssetEntries.size());

		for (int i = 0; i < assetEntries.size(); i++) {
			Assert.assertEquals(
				expectedAssetEntries.get(i), assetEntries.get(i));
		}

		portletPreferences.setValue("orderByType1", "DESC");

		assetEntries = _testGetAssetEntryResults(
			_getAssetEntryResults(portletPreferences),
			expectedAssetEntries.size());

		for (int i = 0; i < assetEntries.size(); i++) {
			Assert.assertEquals(
				expectedAssetEntries.get(assetEntries.size() - 1 - i),
				assetEntries.get(i));
		}
	}

	private static Configuration _assetPublisherWebConfiguration;

	@Inject
	private AssetCategoryLocalService _assetCategoryLocalService;

	@Inject
	private AssetEntryLocalService _assetEntryLocalService;

	@Inject
	private AssetVocabularyLocalService _assetVocabularyLocalService;

	private Company _company;

	@Inject
	private CompanyLocalService _companyLocalService;

	@Inject
	private ConfigurationAdmin _configurationAdmin;

	@DeleteAfterTestRun
	private DepotEntry _depotEntry;

	@Inject
	private DepotEntryGroupRelLocalService _depotEntryGroupRelLocalService;

	@Inject
	private DepotEntryLocalService _depotEntryLocalService;

	@DeleteAfterTestRun
	private Group _group;

	@Inject
	private GroupLocalService _groupLocalService;

	@Inject
	private LayoutLocalService _layoutLocalService;

	@Inject
	private Localization _localization;

	private MockLiferayPortletRenderRequest _mockLiferayPortletRenderRequest;

	@Inject
	private Portal _portal;

	@Inject(
		filter = "component.name=com.liferay.asset.publisher.web.internal.portlet.AssetPublisherPortlet"
	)
	private Portlet _portlet;

	@Inject
	private PortletLocalService _portletLocalService;

	private ThemeDisplay _themeDisplay;

	private static class TestMockLiferayPortletRenderRequest
		extends MockLiferayPortletRenderRequest {

		public TestMockLiferayPortletRenderRequest(
			HttpServletRequest httpServletRequest) {

			_httpServletRequest = httpServletRequest;
		}

		@Override
		public void setAttribute(String name, Object value) {
			super.setAttribute(name, value);

			_httpServletRequest.setAttribute(name, value);
		}

		private final HttpServletRequest _httpServletRequest;

	}

	private static class TestMockLiferayPortletRenderResponse
		extends MockLiferayPortletRenderResponse {

		@Override
		public String getNamespace() {
			return AssetPublisherPortletKeys.ASSET_PUBLISHER;
		}

	}

}