/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.configuration.admin.web.internal.omni.search;

import com.liferay.configuration.admin.constants.ConfigurationAdminPortletKeys;
import com.liferay.configuration.admin.display.ConfigurationScreen;
import com.liferay.configuration.admin.web.internal.model.ConfigurationModel;
import com.liferay.configuration.admin.web.internal.util.ConfigurationEntryRetriever;
import com.liferay.configuration.admin.web.internal.util.ConfigurationModelRetriever;
import com.liferay.configuration.admin.web.internal.util.ResourceBundleLoaderProviderUtil;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.portal.configuration.metatype.annotations.ExtendedObjectClassDefinition;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.portlet.LiferayPortletRequest;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.portlet.LiferayPortletURL;
import com.liferay.portal.kernel.portlet.PortletURLFactory;
import com.liferay.portal.kernel.portlet.PortletURLFactoryUtil;
import com.liferay.portal.kernel.resource.bundle.ResourceBundleLoader;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.test.rule.LiferayUnitTestRule;
import com.liferay.product.navigation.omni.search.OmniSearchResult;
import com.liferay.product.navigation.omni.search.constants.OmniSearchConstants;

import jakarta.portlet.PortletRequest;

import java.io.Serializable;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.MockedStatic;
import org.mockito.Mockito;

/**
 * @author Thiago Buarque
 */
public class ConfigurationOmniSearchResultProviderTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() throws Exception {
		LanguageUtil languageUtil = new LanguageUtil();

		Language language = Mockito.mock(Language.class);

		Mockito.when(
			language.get(Mockito.any(Locale.class), Mockito.anyString())
		).thenAnswer(
			invocationOnMock -> invocationOnMock.getArgument(1)
		);

		Mockito.when(
			language.get(
				Mockito.any(Locale.class), Mockito.anyString(),
				Mockito.anyString())
		).thenAnswer(
			invocationOnMock -> invocationOnMock.getArgument(2)
		);

		languageUtil.setLanguage(language);

		PortletURLFactoryUtil portletURLFactoryUtil =
			new PortletURLFactoryUtil();

		Mockito.when(
			_portletURLFactory.create(
				Mockito.any(PortletRequest.class), Mockito.anyString(),
				Mockito.anyLong(), Mockito.eq(PortletRequest.RENDER_PHASE))
		).thenReturn(
			_liferayPortletURL
		);

		portletURLFactoryUtil.setPortletURLFactory(_portletURLFactory);

		ResourceBundleLoader resourceBundleLoader = Mockito.mock(
			ResourceBundleLoader.class);

		_resourceBundleLoaderProviderUtilMockedStatic.when(
			() -> ResourceBundleLoaderProviderUtil.getResourceBundleLoader(
				Mockito.anyString())
		).thenReturn(
			resourceBundleLoader
		);

		ReflectionTestUtil.setFieldValue(
			_configurationOmniSearchResultProvider,
			"_configurationEntryRetriever", _configurationEntryRetriever);
		ReflectionTestUtil.setFieldValue(
			_configurationOmniSearchResultProvider,
			"_configurationModelRetriever", _configurationModelRetriever);
		ReflectionTestUtil.setFieldValue(
			_configurationOmniSearchResultProvider, "_portal", _portal);

		Mockito.when(
			_portal.getControlPanelPlid(_COMPANY_ID)
		).thenReturn(
			_CONTROL_PANEL_PLID
		);

		Mockito.when(
			_themeDisplay.getCompanyId()
		).thenReturn(
			_COMPANY_ID
		);

		Mockito.when(
			_themeDisplay.getLocale()
		).thenReturn(
			LocaleUtil.US
		);

		Mockito.when(
			_themeDisplay.getPermissionChecker()
		).thenReturn(
			_permissionChecker
		);
	}

	@After
	public void tearDown() {
		_resourceBundleLoaderProviderUtilMockedStatic.close();
	}

	@Test
	public void testGetOmniSearchResults() throws Exception {
		_setUpOmniadmin();

		_setUpConfigurationModels(
			ExtendedObjectClassDefinition.Scope.SYSTEM, null,
			_setUpConfigurationModel("elasticsearch", true, "Elasticsearch 7"),
			_setUpConfigurationModel("foundation", true, "Virtual Instances"));

		List<OmniSearchResult> omniSearchResults = _getOmniSearchResults(
			"elasticsearch");

		Assert.assertEquals(
			omniSearchResults.toString(), 1, omniSearchResults.size());

		OmniSearchResult sectionOmniSearchResult = omniSearchResults.get(0);

		Assert.assertEquals("cog", sectionOmniSearchResult.getIcon());
		Assert.assertEquals(
			"system-settings", sectionOmniSearchResult.getTitle());
		Assert.assertEquals(
			OmniSearchResult.Type.SECTION, sectionOmniSearchResult.getType());

		List<OmniSearchResult> entryOmniSearchResults =
			sectionOmniSearchResult.getOmniSearchResults();

		Assert.assertEquals(
			entryOmniSearchResults.toString(), 1,
			entryOmniSearchResults.size());

		OmniSearchResult entryOmniSearchResult = entryOmniSearchResults.get(0);

		Assert.assertEquals(
			"elasticsearch", entryOmniSearchResult.getDescription());
		Assert.assertEquals("cog", entryOmniSearchResult.getIcon());
		Assert.assertEquals(
			"Elasticsearch 7", entryOmniSearchResult.getTitle());
		Assert.assertEquals(
			OmniSearchResult.Type.ENTRY, entryOmniSearchResult.getType());
		Assert.assertEquals(
			String.valueOf(_liferayPortletURL), entryOmniSearchResult.getURL());
	}

	@Test
	public void testGetOmniSearchResultsIncludesConfigurationScreens()
		throws Exception {

		_setUpCompanyAdmin();

		_setUpConfigurationScreens(
			_setUpConfigurationScreen(
				"analytics", "analytics-cloud-connection",
				"Analytics Cloud Connection",
				ExtendedObjectClassDefinition.Scope.COMPANY, true));

		List<OmniSearchResult> omniSearchResults = _getOmniSearchResults(
			"analytics cloud");

		Assert.assertEquals(
			omniSearchResults.toString(), 1, omniSearchResults.size());

		OmniSearchResult sectionOmniSearchResult = omniSearchResults.get(0);

		Assert.assertEquals(
			"instance-settings", sectionOmniSearchResult.getTitle());

		List<OmniSearchResult> entryOmniSearchResults =
			sectionOmniSearchResult.getOmniSearchResults();

		Assert.assertEquals(
			entryOmniSearchResults.toString(), 1,
			entryOmniSearchResults.size());

		OmniSearchResult entryOmniSearchResult = entryOmniSearchResults.get(0);

		Assert.assertEquals(
			"analytics", entryOmniSearchResult.getDescription());
		Assert.assertEquals(
			"Analytics Cloud Connection", entryOmniSearchResult.getTitle());

		Mockito.verify(
			_liferayPortletURL
		).setParameter(
			"mvcRenderCommandName",
			"/configuration_admin/view_configuration_screen"
		);

		Mockito.verify(
			_liferayPortletURL
		).setParameter(
			"configurationScreenKey", "analytics-cloud-connection"
		);
	}

	@Test
	public void testGetOmniSearchResultsIsEmptyWithoutMatches()
		throws Exception {

		_setUpOmniadmin();

		_setUpConfigurationModels(
			ExtendedObjectClassDefinition.Scope.SYSTEM, null,
			_setUpConfigurationModel("foundation", true, "Virtual Instances"));

		List<OmniSearchResult> omniSearchResults = _getOmniSearchResults(
			RandomTestUtil.randomString());

		Assert.assertTrue(
			omniSearchResults.toString(), omniSearchResults.isEmpty());
	}

	@Test
	public void testGetOmniSearchResultsIsEmptyWithoutPermission()
		throws Exception {

		_setUpConfigurationModels(
			ExtendedObjectClassDefinition.Scope.COMPANY, _COMPANY_ID,
			_setUpConfigurationModel("foundation", true, "Virtual Instances"));
		_setUpConfigurationModels(
			ExtendedObjectClassDefinition.Scope.SYSTEM, null,
			_setUpConfigurationModel("foundation", true, "Virtual Instances"));
		_setUpConfigurationScreens(
			_setUpConfigurationScreen(
				"analytics", "analytics-cloud-connection", "Virtual Screen",
				ExtendedObjectClassDefinition.Scope.COMPANY, true));

		List<OmniSearchResult> omniSearchResults = _getOmniSearchResults(
			"virtual");

		Assert.assertTrue(
			omniSearchResults.toString(), omniSearchResults.isEmpty());
	}

	@Test
	public void testGetOmniSearchResultsLimitsEntriesPerSectionIndependently()
		throws Exception {

		_setUpCompanyAdmin();
		_setUpOmniadmin();

		_setUpConfigurationModels(
			ExtendedObjectClassDefinition.Scope.COMPANY, _COMPANY_ID,
			_setUpConfigurationModels("Company Virtual Instances "));
		_setUpConfigurationModels(
			ExtendedObjectClassDefinition.Scope.SYSTEM, null,
			_setUpConfigurationModels("System Virtual Instances "));

		List<OmniSearchResult> omniSearchResults = _getOmniSearchResults(
			"virtual");

		Assert.assertEquals(
			omniSearchResults.toString(), 2, omniSearchResults.size());

		for (OmniSearchResult sectionOmniSearchResult : omniSearchResults) {
			List<OmniSearchResult> entryOmniSearchResults =
				sectionOmniSearchResult.getOmniSearchResults();

			Assert.assertEquals(
				entryOmniSearchResults.toString(),
				OmniSearchConstants.MAX_ENTRIES_PER_SECTION,
				entryOmniSearchResults.size());
		}
	}

	@Test
	public void testGetOmniSearchResultsMatchesTheNameOnly() throws Exception {
		_setUpOmniadmin();

		_setUpConfigurationModels(
			ExtendedObjectClassDefinition.Scope.SYSTEM, null,
			_setUpConfigurationModel("elasticsearch", true, "Search Tuning"));
		_setUpConfigurationScreens(
			_setUpConfigurationScreen(
				"elasticsearch", "screen-key", "Screen Name",
				ExtendedObjectClassDefinition.Scope.SYSTEM, true));

		List<OmniSearchResult> omniSearchResults = _getOmniSearchResults(
			"elasticsearch");

		Assert.assertTrue(
			omniSearchResults.toString(), omniSearchResults.isEmpty());
	}

	@Test
	public void testGetOmniSearchResultsReturnsASectionPerScope()
		throws Exception {

		_setUpCompanyAdmin();
		_setUpOmniadmin();

		_setUpConfigurationModels(
			ExtendedObjectClassDefinition.Scope.COMPANY, _COMPANY_ID,
			_setUpConfigurationModel("foundation", true, "Virtual Instances"));
		_setUpConfigurationModels(
			ExtendedObjectClassDefinition.Scope.SYSTEM, null,
			_setUpConfigurationModel("foundation", true, "Virtual Hosts"));

		Assert.assertEquals(
			ListUtil.fromArray("system-settings", "instance-settings"),
			TransformUtil.transform(
				_getOmniSearchResults("virtual"), OmniSearchResult::getTitle));
	}

	@Test
	public void testGetOmniSearchResultsSkipsEntriesWhenTheNameIsBlank()
		throws Exception {

		_setUpOmniadmin();

		_setUpConfigurationModels(
			ExtendedObjectClassDefinition.Scope.SYSTEM, null,
			_setUpConfigurationModel("foundation", true, ""));
		_setUpConfigurationScreens(
			_setUpConfigurationScreen(
				"analytics", "screen-key", "",
				ExtendedObjectClassDefinition.Scope.SYSTEM, true));

		List<OmniSearchResult> omniSearchResults = _getOmniSearchResults("");

		Assert.assertTrue(
			omniSearchResults.toString(), omniSearchResults.isEmpty());
	}

	@Test
	public void testGetOmniSearchResultsSkipsInvisibleConfigurationScreens()
		throws Exception {

		_setUpOmniadmin();

		_setUpConfigurationScreens(
			_setUpConfigurationScreen(
				"analytics", "screen-key", "Virtual Screen",
				ExtendedObjectClassDefinition.Scope.SYSTEM, false));

		List<OmniSearchResult> omniSearchResults = _getOmniSearchResults(
			"virtual");

		Assert.assertTrue(
			omniSearchResults.toString(), omniSearchResults.isEmpty());
	}

	@Test
	public void testGetOmniSearchResultsSkipsModelsWithoutGeneratedUI()
		throws Exception {

		_setUpCompanyAdmin();

		_setUpConfigurationModels(
			ExtendedObjectClassDefinition.Scope.COMPANY, _COMPANY_ID,
			_setUpConfigurationModel("foundation", false, "Virtual Instances"));

		List<OmniSearchResult> omniSearchResults = _getOmniSearchResults(
			"virtual");

		Assert.assertTrue(
			omniSearchResults.toString(), omniSearchResults.isEmpty());
	}

	@Test
	public void testGetOmniSearchResultsSkipsScreensOutOfScope()
		throws Exception {

		_setUpCompanyAdmin();

		_setUpConfigurationScreens(
			_setUpConfigurationScreen(
				"analytics", "screen-key", "Virtual Screen",
				ExtendedObjectClassDefinition.Scope.SYSTEM, true));

		List<OmniSearchResult> omniSearchResults = _getOmniSearchResults(
			"virtual");

		Assert.assertTrue(
			omniSearchResults.toString(), omniSearchResults.isEmpty());
	}

	@Test
	public void testGetOmniSearchResultsSortsEntriesByName() throws Exception {
		_setUpOmniadmin();

		_setUpConfigurationModels(
			ExtendedObjectClassDefinition.Scope.SYSTEM, null,
			_setUpConfigurationModel("foundation", true, "Virtual Instances"),
			_setUpConfigurationModel("foundation", true, "Virtual Hosts"));
		_setUpConfigurationScreens(
			_setUpConfigurationScreen(
				"analytics", "screen-key", "Virtual Assets",
				ExtendedObjectClassDefinition.Scope.SYSTEM, true));

		List<OmniSearchResult> omniSearchResults = _getOmniSearchResults(
			"virtual");

		OmniSearchResult sectionOmniSearchResult = omniSearchResults.get(0);

		Assert.assertEquals(
			ListUtil.fromArray(
				"Virtual Assets", "Virtual Hosts", "Virtual Instances"),
			TransformUtil.transform(
				sectionOmniSearchResult.getOmniSearchResults(),
				OmniSearchResult::getTitle));
	}

	@Test
	public void testGetOmniSearchResultsUsesTheInstanceSettingsScope()
		throws Exception {

		_setUpCompanyAdmin();

		_setUpConfigurationModels(
			ExtendedObjectClassDefinition.Scope.COMPANY, _COMPANY_ID,
			_setUpConfigurationModel("foundation", true, "Virtual Instances"));

		List<OmniSearchResult> omniSearchResults = _getOmniSearchResults(
			"virtual");

		OmniSearchResult sectionOmniSearchResult = omniSearchResults.get(0);

		List<OmniSearchResult> entryOmniSearchResults =
			sectionOmniSearchResult.getOmniSearchResults();

		OmniSearchResult entryOmniSearchResult = entryOmniSearchResults.get(0);

		Assert.assertEquals(
			"foundation", entryOmniSearchResult.getDescription());

		Mockito.verify(
			_portletURLFactory
		).create(
			_liferayPortletRequest,
			ConfigurationAdminPortletKeys.INSTANCE_SETTINGS,
			_CONTROL_PANEL_PLID, PortletRequest.RENDER_PHASE
		);
	}

	private List<OmniSearchResult> _getOmniSearchResults(String keywords)
		throws Exception {

		return _configurationOmniSearchResultProvider.getOmniSearchResults(
			keywords, _liferayPortletRequest, _liferayPortletResponse,
			_themeDisplay);
	}

	private void _setUpCompanyAdmin() {
		Mockito.when(
			_permissionChecker.isCompanyAdmin()
		).thenReturn(
			true
		);
	}

	private ConfigurationModel _setUpConfigurationModel(
		String category, boolean generateUI, String name) {

		ConfigurationModel configurationModel = Mockito.mock(
			ConfigurationModel.class);

		Mockito.when(
			configurationModel.getBundleSymbolicName()
		).thenReturn(
			RandomTestUtil.randomString()
		);

		Mockito.when(
			configurationModel.getCategory()
		).thenReturn(
			category
		);

		Mockito.when(
			configurationModel.getID()
		).thenReturn(
			RandomTestUtil.randomString()
		);

		Mockito.when(
			configurationModel.getName()
		).thenReturn(
			name
		);

		Mockito.when(
			configurationModel.isGenerateUI()
		).thenReturn(
			generateUI
		);

		return configurationModel;
	}

	private void _setUpConfigurationModels(
		ExtendedObjectClassDefinition.Scope scope, Serializable scopePK,
		ConfigurationModel... configurationModels) {

		Map<String, ConfigurationModel> configurationModelsMap =
			new LinkedHashMap<>();

		for (ConfigurationModel configurationModel : configurationModels) {
			configurationModelsMap.put(
				configurationModel.getID(), configurationModel);
		}

		Mockito.when(
			_configurationModelRetriever.getConfigurationModels(
				LocaleUtil.toLanguageId(LocaleUtil.US), scope, scopePK)
		).thenReturn(
			configurationModelsMap
		);
	}

	private ConfigurationModel[] _setUpConfigurationModels(String namePrefix) {
		ConfigurationModel[] configurationModels =
			new ConfigurationModel
				[OmniSearchConstants.MAX_ENTRIES_PER_SECTION + 3];

		for (int i = 0; i < configurationModels.length; i++) {
			configurationModels[i] = _setUpConfigurationModel(
				"foundation", true, namePrefix + i);
		}

		return configurationModels;
	}

	private ConfigurationScreen _setUpConfigurationScreen(
		String categoryKey, String key, String name,
		ExtendedObjectClassDefinition.Scope scope, boolean visible) {

		ConfigurationScreen configurationScreen = Mockito.mock(
			ConfigurationScreen.class);

		Mockito.when(
			configurationScreen.getCategoryKey()
		).thenReturn(
			categoryKey
		);

		Mockito.when(
			configurationScreen.getKey()
		).thenReturn(
			key
		);

		Mockito.when(
			configurationScreen.getName(LocaleUtil.US)
		).thenReturn(
			name
		);

		Mockito.when(
			configurationScreen.getScope()
		).thenReturn(
			scope.getValue()
		);

		Mockito.when(
			configurationScreen.isVisible()
		).thenReturn(
			visible
		);

		return configurationScreen;
	}

	private void _setUpConfigurationScreens(
		ConfigurationScreen... configurationScreens) {

		Mockito.when(
			_configurationEntryRetriever.getAllConfigurationScreens()
		).thenReturn(
			ListUtil.fromArray(configurationScreens)
		);
	}

	private void _setUpOmniadmin() {
		Mockito.when(
			_permissionChecker.isOmniadmin()
		).thenReturn(
			true
		);
	}

	private static final long _COMPANY_ID = RandomTestUtil.randomLong();

	private static final long _CONTROL_PANEL_PLID = RandomTestUtil.randomLong();

	private final ConfigurationEntryRetriever _configurationEntryRetriever =
		Mockito.mock(ConfigurationEntryRetriever.class);
	private final ConfigurationModelRetriever _configurationModelRetriever =
		Mockito.mock(ConfigurationModelRetriever.class);
	private final ConfigurationOmniSearchResultProvider
		_configurationOmniSearchResultProvider =
			new ConfigurationOmniSearchResultProvider();
	private final LiferayPortletRequest _liferayPortletRequest = Mockito.mock(
		LiferayPortletRequest.class);
	private final LiferayPortletResponse _liferayPortletResponse = Mockito.mock(
		LiferayPortletResponse.class);
	private final LiferayPortletURL _liferayPortletURL = Mockito.mock(
		LiferayPortletURL.class);
	private final PermissionChecker _permissionChecker = Mockito.mock(
		PermissionChecker.class);
	private final Portal _portal = Mockito.mock(Portal.class);
	private final PortletURLFactory _portletURLFactory = Mockito.mock(
		PortletURLFactory.class);
	private final MockedStatic<ResourceBundleLoaderProviderUtil>
		_resourceBundleLoaderProviderUtilMockedStatic = Mockito.mockStatic(
			ResourceBundleLoaderProviderUtil.class);
	private final ThemeDisplay _themeDisplay = Mockito.mock(ThemeDisplay.class);

}