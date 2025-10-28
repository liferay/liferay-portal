/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.web.internal.search.bar.portlet.display.context.factory;

import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.configuration.module.configuration.ConfigurationProviderUtil;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.UserGroup;
import com.liferay.portal.kernel.module.configuration.ConfigurationException;
import com.liferay.portal.kernel.portlet.LiferayPortletRequest;
import com.liferay.portal.kernel.service.GroupLocalServiceUtil;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.theme.PortletDisplay;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.Http;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.search.capabilities.SearchCapabilities;
import com.liferay.portal.search.rest.configuration.SearchSuggestionsCompanyConfiguration;
import com.liferay.portal.search.searcher.SearchRequest;
import com.liferay.portal.search.searcher.SearchResponse;
import com.liferay.portal.search.web.internal.portlet.preferences.PortletPreferencesLookup;
import com.liferay.portal.search.web.internal.search.bar.portlet.SearchBarPortletPreferences;
import com.liferay.portal.search.web.internal.search.bar.portlet.configuration.SearchBarPortletInstanceConfiguration;
import com.liferay.portal.search.web.internal.search.bar.portlet.display.context.SearchBarPortletDisplayContext;
import com.liferay.portal.search.web.internal.search.bar.portlet.helper.SearchBarPrecedenceHelper;
import com.liferay.portal.search.web.portlet.shared.search.PortletSharedSearchRequest;
import com.liferay.portal.search.web.portlet.shared.search.PortletSharedSearchResponse;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.LiferayUnitTestRule;
import com.liferay.portlet.PortletPreferencesImpl;

import jakarta.portlet.PortletPreferences;
import jakarta.portlet.RenderRequest;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Collections;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import org.mockito.MockedStatic;
import org.mockito.Mockito;

/**
 * @author Adam Brandizzi
 * @author Petteri Karttunne
 */
@FeatureFlag("LPD-35128")
public class SearchBarPortletDisplayContextFactoryTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@BeforeClass
	public static void setUpClass() {
		_configurationProviderUtilMockedStatic = Mockito.mockStatic(
			ConfigurationProviderUtil.class);
	}

	@AfterClass
	public static void tearDownClass() {
		_configurationProviderUtilMockedStatic.close();
	}

	@Before
	public void setUp() throws ConfigurationException {
		_setUpLanguageUtil();
		_setUpPortal();
		_setUpThemeDisplay();
	}

	@Test
	public void testDestinationBlank() throws Exception {
		SearchBarPortletDisplayContextFactory
			searchBarPortletDisplayContextFactory =
				_createSearchBarPortletDisplayContextFactory(StringPool.BLANK);

		SearchBarPortletDisplayContext searchBarPortletDisplayContext =
			searchBarPortletDisplayContextFactory.create(
				_portletPreferencesLookup, _portletSharedSearchRequest,
				_searchBarPrecedenceHelper, _searchCapabilities);

		Assert.assertFalse(
			searchBarPortletDisplayContext.isDestinationUnreachable());
	}

	@Test
	public void testDestinationFromUserGroupLayout() throws Exception {
		String destination = RandomTestUtil.randomString();

		Layout layout = Mockito.mock(Layout.class);

		_whenLayoutLocalServiceFetchLayoutByFriendlyURL(
			destination, _USER_GROUP_ID, layout);

		String layoutFriendlyURL = RandomTestUtil.randomString();

		_whenPortalGetLayoutFriendlyURL(layout, layoutFriendlyURL);

		_whenUserLocalServiceFetchUserById();

		SearchBarPortletDisplayContextFactory
			searchBarPortletDisplayContextFactory =
				_createSearchBarPortletDisplayContextFactory(destination);

		SearchBarPortletDisplayContext searchBarPortletDisplayContext =
			searchBarPortletDisplayContextFactory.create(
				_portletPreferencesLookup, _portletSharedSearchRequest,
				_searchBarPrecedenceHelper, _searchCapabilities);

		Assert.assertEquals(
			layoutFriendlyURL, searchBarPortletDisplayContext.getSearchURL());

		Assert.assertFalse(
			searchBarPortletDisplayContext.isDestinationUnreachable());
	}

	@Test
	public void testDestinationNull() throws Exception {
		SearchBarPortletDisplayContextFactory
			searchBarPortletDisplayContextFactory =
				_createSearchBarPortletDisplayContextFactory(null);

		SearchBarPortletDisplayContext searchBarPortletDisplayContext =
			searchBarPortletDisplayContextFactory.create(
				_portletPreferencesLookup, _portletSharedSearchRequest,
				_searchBarPrecedenceHelper, _searchCapabilities);

		Assert.assertFalse(
			searchBarPortletDisplayContext.isDestinationUnreachable());
	}

	@Test
	public void testDestinationUnreachable() throws Exception {
		String destination = RandomTestUtil.randomString();

		_whenLayoutLocalServiceFetchLayoutByFriendlyURL(destination, null);

		SearchBarPortletDisplayContextFactory
			searchBarPortletDisplayContextFactory =
				_createSearchBarPortletDisplayContextFactory(destination);

		SearchBarPortletDisplayContext searchBarPortletDisplayContext =
			searchBarPortletDisplayContextFactory.create(
				_portletPreferencesLookup, _portletSharedSearchRequest,
				_searchBarPrecedenceHelper, _searchCapabilities);

		Assert.assertTrue(
			searchBarPortletDisplayContext.isDestinationUnreachable());
	}

	@Test
	public void testDestinationWithLeadingSlash() throws Exception {
		String destination = RandomTestUtil.randomString();

		Layout layout = Mockito.mock(Layout.class);

		_whenLayoutLocalServiceFetchLayoutByFriendlyURL(destination, layout);

		String layoutFriendlyURL = RandomTestUtil.randomString();

		_whenPortalGetLayoutFriendlyURL(layout, layoutFriendlyURL);

		SearchBarPortletDisplayContextFactory
			searchBarPortletDisplayContextFactory =
				_createSearchBarPortletDisplayContextFactory(
					StringPool.SLASH.concat(destination));

		SearchBarPortletDisplayContext searchBarPortletDisplayContext =
			searchBarPortletDisplayContextFactory.create(
				_portletPreferencesLookup, _portletSharedSearchRequest,
				_searchBarPrecedenceHelper, _searchCapabilities);

		Assert.assertEquals(
			layoutFriendlyURL, searchBarPortletDisplayContext.getSearchURL());

		Assert.assertFalse(
			searchBarPortletDisplayContext.isDestinationUnreachable());
	}

	@Test
	public void testDestinationWithoutLeadingSlash() throws Exception {
		String destination = RandomTestUtil.randomString();

		Layout layout = Mockito.mock(Layout.class);

		_whenLayoutLocalServiceFetchLayoutByFriendlyURL(destination, layout);

		String layoutFriendlyURL = RandomTestUtil.randomString();

		_whenPortalGetLayoutFriendlyURL(layout, layoutFriendlyURL);

		SearchBarPortletDisplayContextFactory
			searchBarPortletDisplayContextFactory =
				_createSearchBarPortletDisplayContextFactory(destination);

		SearchBarPortletDisplayContext searchBarPortletDisplayContext =
			searchBarPortletDisplayContextFactory.create(
				_portletPreferencesLookup, _portletSharedSearchRequest,
				_searchBarPrecedenceHelper, _searchCapabilities);

		Assert.assertEquals(
			layoutFriendlyURL, searchBarPortletDisplayContext.getSearchURL());

		Assert.assertFalse(
			searchBarPortletDisplayContext.isDestinationUnreachable());
	}

	@Test
	public void testGetDisplayStyleGroup() throws Exception {
		_setUpGroupLocalServiceUtil(_getGroup());
		_setUpPortletDisplayStyleGroupExternalReferenceCode(null);

		_assertDisplayContext(_group);

		_groupLocalServiceUtilMockedStatic.verifyNoInteractions();
	}

	@Test
	public void testGetDisplayStyleGroupWithConfiguration() throws Exception {
		Group group = _getGroup();

		_setUpGroupLocalServiceUtil(group);
		_setUpPortletDisplayStyleGroupExternalReferenceCode(
			group.getExternalReferenceCode());

		_assertDisplayContext(group);

		_groupLocalServiceUtilMockedStatic.verify(
			() -> GroupLocalServiceUtil.fetchGroupByExternalReferenceCode(
				group.getExternalReferenceCode(), 0L),
			Mockito.times(1));
	}

	@Test
	public void testSamePageNoDestination() throws Exception {
		Layout layout = Mockito.mock(Layout.class);

		_whenPortalGetLayoutFriendlyURL(layout, "/web/guest/home");

		Mockito.doReturn(
			layout
		).when(
			_themeDisplay
		).getLayout();

		SearchBarPortletDisplayContextFactory
			searchBarPortletDisplayContextFactory =
				_createSearchBarPortletDisplayContextFactory(null);

		SearchBarPortletDisplayContext searchBarPortletDisplayContext =
			searchBarPortletDisplayContextFactory.create(
				_portletPreferencesLookup, _portletSharedSearchRequest,
				_searchBarPrecedenceHelper, _searchCapabilities);

		Assert.assertFalse(
			searchBarPortletDisplayContext.isDestinationUnreachable());

		Assert.assertEquals(
			"/web/guest/home", searchBarPortletDisplayContext.getSearchURL());
	}

	@Test
	public void testScopeParameterName() throws Exception {
		SearchBarPortletDisplayContextFactory
			searchBarPortletDisplayContextFactory =
				_createSearchBarPortletDisplayContextFactory(null, "sp", null);

		SearchBarPortletDisplayContext searchBarPortletDisplayContext =
			searchBarPortletDisplayContextFactory.create(
				_portletPreferencesLookup, _portletSharedSearchRequest,
				_searchBarPrecedenceHelper, _searchCapabilities);

		Assert.assertEquals(
			"sp", searchBarPortletDisplayContext.getScopeParameterName());
	}

	@Test
	public void testScopeParameterNameDefault() throws Exception {
		SearchBarPortletDisplayContextFactory
			searchBarPortletDisplayContextFactory =
				_createSearchBarPortletDisplayContextFactory(null, null, null);

		SearchBarPortletDisplayContext searchBarPortletDisplayContext =
			searchBarPortletDisplayContextFactory.create(
				_portletPreferencesLookup, _portletSharedSearchRequest,
				_searchBarPrecedenceHelper, _searchCapabilities);

		Assert.assertEquals(
			_DEFAULT_SCOPE_PARAMETER_NAME,
			searchBarPortletDisplayContext.getScopeParameterName());
	}

	@Test
	public void testSearchScopePreferenceDefault() throws Exception {
		SearchBarPortletDisplayContextFactory
			searchBarPortletDisplayContextFactory =
				_createSearchBarPortletDisplayContextFactory(null, null, null);

		_assertScope(searchBarPortletDisplayContextFactory, false, true, false);
	}

	@Test
	public void testSearchScopePreferenceEverything() throws Exception {
		SearchBarPortletDisplayContextFactory
			searchBarPortletDisplayContextFactory =
				_createSearchBarPortletDisplayContextFactory(
					"everything", null, null);

		_assertScope(searchBarPortletDisplayContextFactory, false, false, true);
	}

	@Test
	public void testSearchScopePreferenceLetTheUserChooseEverything()
		throws Exception {

		SearchBarPortletDisplayContextFactory
			searchBarPortletDisplayContextFactory =
				_createSearchBarPortletDisplayContextFactory(
					"let-the-user-choose", null, "everything");

		_assertScope(searchBarPortletDisplayContextFactory, true, false, true);
	}

	@Test
	public void testSearchScopePreferenceLetTheUserChooseInvalidScopeParam()
		throws Exception {

		expectedException.expect(IllegalArgumentException.class);
		expectedException.expectMessage(
			"The string invalid-scope does not correspond to a valid search " +
				"scope");

		SearchBarPortletDisplayContextFactory
			searchBarPortletDisplayContextFactory =
				_createSearchBarPortletDisplayContextFactory(
					"let-the-user-choose", null, "invalid-scope");

		_assertScope(searchBarPortletDisplayContextFactory, true, false, false);
	}

	@Test
	public void testSearchScopePreferenceLetTheUserChooseNoScopeParam()
		throws Exception {

		SearchBarPortletDisplayContextFactory
			searchBarPortletDisplayContextFactory =
				_createSearchBarPortletDisplayContextFactory(
					"let-the-user-choose", null, null);

		_assertScope(searchBarPortletDisplayContextFactory, true, false, false);
	}

	@Test
	public void testSearchScopePreferenceLetTheUserChooseThisSite()
		throws Exception {

		SearchBarPortletDisplayContextFactory
			searchBarPortletDisplayContextFactory =
				_createSearchBarPortletDisplayContextFactory(
					"let-the-user-choose", null, "this-site");

		_assertScope(searchBarPortletDisplayContextFactory, true, true, false);
	}

	@Test
	public void testSearchScopePreferenceThisSite() throws Exception {
		SearchBarPortletDisplayContextFactory
			searchBarPortletDisplayContextFactory =
				_createSearchBarPortletDisplayContextFactory(
					"this-site", null, null);

		_assertScope(searchBarPortletDisplayContextFactory, false, true, false);
	}

	@Test
	public void testsIncludeAttachmentsInSuggestionsContributorConfiguration()
		throws Exception {

		_testIncludeAttachmentsInSuggestionsContributorConfiguration(false);
		_testIncludeAttachmentsInSuggestionsContributorConfiguration(true);
	}

	@Rule
	public ExpectedException expectedException = ExpectedException.none();

	protected HttpServletRequest getHttpServletRequest() {
		HttpServletRequest httpServletRequest = Mockito.mock(
			HttpServletRequest.class);

		Mockito.when(
			(ThemeDisplay)httpServletRequest.getAttribute(WebKeys.THEME_DISPLAY)
		).thenReturn(
			_themeDisplay
		);

		return httpServletRequest;
	}

	protected String getPath(String url) {
		if (Validator.isNull(url)) {
			return url;
		}

		if (url.startsWith(Http.HTTP)) {
			int pos = url.indexOf(
				CharPool.SLASH, Http.HTTPS_WITH_SLASH.length());

			url = url.substring(pos);
		}

		int pos = url.indexOf(CharPool.QUESTION);

		if (pos == -1) {
			return url;
		}

		return url.substring(0, pos);
	}

	private void _assertDisplayContext(Group group) throws Exception {
		SearchBarPortletDisplayContextFactory
			searchBarPortletDisplayContextFactory =
				_createSearchBarPortletDisplayContextFactory(null);

		SearchBarPortletDisplayContext searchBarPortletDisplayContext =
			searchBarPortletDisplayContextFactory.create(
				_portletPreferencesLookup, _portletSharedSearchRequest,
				_searchBarPrecedenceHelper, _searchCapabilities);

		Assert.assertEquals(
			group.getGroupId(),
			searchBarPortletDisplayContext.getDisplayStyleGroupId());
	}

	private void _assertScope(
		SearchBarPortletDisplayContextFactory
			searchBarPortletDisplayContextFactory,
		boolean expectedLetTheUserChoose, boolean expectedSelectedCurrentSite,
		boolean expectedSelectedEverything) {

		SearchBarPortletDisplayContext searchBarPortletDisplayContext =
			searchBarPortletDisplayContextFactory.create(
				_portletPreferencesLookup, _portletSharedSearchRequest,
				_searchBarPrecedenceHelper, _searchCapabilities);

		Assert.assertEquals(
			expectedLetTheUserChoose,
			searchBarPortletDisplayContext.isLetTheUserChooseTheSearchScope());
		Assert.assertEquals(
			expectedSelectedCurrentSite,
			searchBarPortletDisplayContext.isSelectedCurrentSiteSearchScope());
		Assert.assertEquals(
			expectedSelectedEverything,
			searchBarPortletDisplayContext.isSelectedEverythingSearchScope());
	}

	private LiferayPortletRequest _createLiferayPortletRequest() {
		LiferayPortletRequest liferayPortletRequest = Mockito.mock(
			LiferayPortletRequest.class);

		Mockito.doReturn(
			getHttpServletRequest()
		).when(
			liferayPortletRequest
		).getHttpServletRequest();

		return liferayPortletRequest;
	}

	private SearchBarPortletDisplayContextFactory
			_createSearchBarPortletDisplayContextFactory(String destination)
		throws Exception {

		return _createSearchBarPortletDisplayContextFactory(
			destination, false, false, null, null, null);
	}

	private SearchBarPortletDisplayContextFactory
			_createSearchBarPortletDisplayContextFactory(
				String destination, boolean enableSuggestionsPoint,
				boolean includeAttachments, String scope,
				String scopeParameterName, String scopeParameterValue)
		throws Exception {

		RenderRequest renderRequest = Mockito.mock(RenderRequest.class);

		Mockito.when(
			renderRequest.getAttribute(WebKeys.THEME_DISPLAY)
		).thenReturn(
			_themeDisplay
		);

		SearchBarPortletDisplayContextFactory
			searchBarPortletDisplayContextFactory =
				new SearchBarPortletDisplayContextFactory(
					_layoutLocalService, _portal, renderRequest,
					_userLocalService);

		PortletPreferences portletPreferences = new PortletPreferencesImpl();

		portletPreferences.setValue(
			SearchBarPortletPreferences.PREFERENCE_KEY_DESTINATION,
			destination);
		portletPreferences.setValue(
			SearchBarPortletPreferences.PREFERENCE_KEY_INCLUDE_ATTACHMENTS,
			Boolean.toString(includeAttachments));
		portletPreferences.setValue(
			SearchBarPortletPreferences.PREFERENCE_KEY_SEARCH_SCOPE, scope);

		if (scopeParameterName != null) {
			portletPreferences.setValue(
				SearchBarPortletPreferences.PREFERENCE_KEY_SCOPE_PARAMETER_NAME,
				scopeParameterName);
		}
		else {
			scopeParameterName = _DEFAULT_SCOPE_PARAMETER_NAME;
		}

		if (scopeParameterValue != null) {
			portletPreferences.setValue(
				scopeParameterName, scopeParameterValue);
		}

		Mockito.when(
			renderRequest.getPreferences()
		).thenReturn(
			portletPreferences
		);

		PortletSharedSearchResponse portletSharedSearchResponse = Mockito.mock(
			PortletSharedSearchResponse.class);

		Mockito.when(
			_portletSharedSearchRequest.search(renderRequest)
		).thenReturn(
			portletSharedSearchResponse
		);

		Mockito.when(
			_searchBarPortletInstanceConfiguration.destination()
		).thenReturn(
			destination
		);

		Mockito.when(
			_searchBarPortletInstanceConfiguration.
				suggestionsContributorConfigurations()
		).thenReturn(
			new String[] {"{}"}
		);

		Mockito.when(
			_searchBarPrecedenceHelper.findHeaderSearchBarPortlet(_themeDisplay)
		).thenReturn(
			null
		);

		Mockito.when(
			portletSharedSearchResponse.getParameter(
				Mockito.eq(scopeParameterName), Mockito.any())
		).thenReturn(
			scopeParameterValue
		);

		SearchResponse searchResponse = Mockito.mock(SearchResponse.class);

		Mockito.when(
			portletSharedSearchResponse.getFederatedSearchResponse(
				Mockito.any())
		).thenReturn(
			searchResponse
		);

		Mockito.when(
			searchResponse.getRequest()
		).thenReturn(
			Mockito.mock(SearchRequest.class)
		);

		Mockito.when(
			portletSharedSearchResponse.getSearchResponse()
		).thenReturn(
			searchResponse
		);

		searchBarPortletDisplayContextFactory = Mockito.spy(
			searchBarPortletDisplayContextFactory);

		Mockito.doReturn(
			_searchSuggestionsCompanyConfiguration
		).when(
			searchBarPortletDisplayContextFactory
		).getSearchSuggestionsCompanyConfiguration(
			0
		);

		Mockito.doReturn(
			enableSuggestionsPoint
		).when(
			_searchSuggestionsCompanyConfiguration
		).enableSuggestionsEndpoint();

		return searchBarPortletDisplayContextFactory;
	}

	private SearchBarPortletDisplayContextFactory
			_createSearchBarPortletDisplayContextFactory(
				String scope, String scopeParameterName,
				String scopeParameterValue)
		throws Exception {

		return _createSearchBarPortletDisplayContextFactory(
			null, false, false, scope, scopeParameterName, scopeParameterValue);
	}

	private Group _getGroup() {
		Group group = Mockito.mock(Group.class);

		Mockito.when(
			group.getExternalReferenceCode()
		).thenReturn(
			RandomTestUtil.randomString()
		);

		Mockito.when(
			group.getGroupId()
		).thenReturn(
			RandomTestUtil.randomLong()
		);

		return group;
	}

	private void _setUpGroupLocalServiceUtil(Group group) throws Exception {
		_groupLocalServiceUtilMockedStatic.reset();

		Mockito.when(
			GroupLocalServiceUtil.fetchGroupByExternalReferenceCode(
				group.getExternalReferenceCode(), 0L)
		).thenReturn(
			group
		);
	}

	private void _setUpLanguageUtil() {
		LanguageUtil languageUtil = new LanguageUtil();

		languageUtil.setLanguage(Mockito.mock(Language.class));
	}

	private void _setUpPortal() {
		Mockito.doReturn(
			_createLiferayPortletRequest()
		).when(
			_portal
		).getLiferayPortletRequest(
			Mockito.any()
		);
	}

	private void _setUpPortletDisplayStyleGroupExternalReferenceCode(
		String externalReferenceCode) {

		SearchBarPortletInstanceConfiguration
			searchBarPortletInstanceConfiguration = Mockito.mock(
				SearchBarPortletInstanceConfiguration.class);

		Mockito.when(
			searchBarPortletInstanceConfiguration.
				displayStyleGroupExternalReferenceCode()
		).thenReturn(
			externalReferenceCode
		);

		_configurationProviderUtilMockedStatic.reset();

		_configurationProviderUtilMockedStatic.when(
			() -> ConfigurationProviderUtil.getPortletInstanceConfiguration(
				Mockito.any(), Mockito.any())
		).thenReturn(
			searchBarPortletInstanceConfiguration
		);
	}

	private void _setUpThemeDisplay() throws ConfigurationException {
		Mockito.when(
			_themeDisplay.getScopeGroup()
		).thenReturn(
			_group
		);

		Mockito.when(
			_group.getClassPK()
		).thenReturn(
			_CLASS_PK
		);

		Mockito.when(
			_group.getExternalReferenceCode()
		).thenReturn(
			RandomTestUtil.randomString()
		);

		Mockito.when(
			_group.getGroupId()
		).thenReturn(
			RandomTestUtil.randomLong()
		);

		Mockito.when(
			_portletDisplay.getPortletResource()
		).thenReturn(
			"test"
		);

		Mockito.when(
			_portletDisplay.getThemeDisplay()
		).thenReturn(
			_themeDisplay
		);

		Mockito.when(
			_themeDisplay.getPortletDisplay()
		).thenReturn(
			_portletDisplay
		);

		Mockito.when(
			_themeDisplay.getScopeGroupId()
		).thenReturn(
			_SCOPE_GROUP_ID
		);

		Mockito.when(
			_themeDisplay.getUser()
		).thenReturn(
			_user
		);

		_configurationProviderUtilMockedStatic.when(
			() -> ConfigurationProviderUtil.getPortletInstanceConfiguration(
				Mockito.any(), Mockito.any())
		).thenReturn(
			_searchBarPortletInstanceConfiguration
		);
	}

	private void _testIncludeAttachmentsInSuggestionsContributorConfiguration(
			boolean includeAttachments)
		throws Exception {

		SearchBarPortletDisplayContextFactory
			searchBarPortletDisplayContextFactory =
				_createSearchBarPortletDisplayContextFactory(
					null, true, includeAttachments, null, null, null);

		SearchBarPortletDisplayContext searchBarPortletDisplayContext =
			searchBarPortletDisplayContextFactory.create(
				_portletPreferencesLookup, _portletSharedSearchRequest,
				_searchBarPrecedenceHelper, _searchCapabilities);

		String suggestionsContributorConfiguration =
			searchBarPortletDisplayContext.
				getSuggestionsContributorConfiguration();

		Assert.assertTrue(
			suggestionsContributorConfiguration,
			suggestionsContributorConfiguration.contains(
				"\"includeAttachments\":" + includeAttachments));
	}

	private void _whenLayoutLocalServiceFetchLayoutByFriendlyURL(
		String friendlyURL, Layout layout) {

		_whenLayoutLocalServiceFetchLayoutByFriendlyURL(
			friendlyURL, _SCOPE_GROUP_ID, layout);
	}

	private void _whenLayoutLocalServiceFetchLayoutByFriendlyURL(
		String friendlyURL, long groupId, Layout layout) {

		if (!StringUtil.startsWith(friendlyURL, CharPool.SLASH)) {
			friendlyURL = StringPool.SLASH.concat(friendlyURL);
		}

		Mockito.doReturn(
			layout
		).when(
			_layoutLocalService
		).fetchLayoutByFriendlyURL(
			groupId, false, friendlyURL
		);
	}

	private void _whenPortalGetLayoutFriendlyURL(
			Layout layout, String layoutFriendlyURL)
		throws Exception {

		Mockito.doReturn(
			layoutFriendlyURL
		).when(
			_portal
		).getLayoutFriendlyURL(
			Mockito.eq(layout), Mockito.any()
		);
	}

	private void _whenUserLocalServiceFetchUserById() throws Exception {
		Mockito.doReturn(
			Collections.singletonList(_userGroup)
		).when(
			_user
		).getUserGroups();

		Mockito.doReturn(
			_USER_GROUP_ID
		).when(
			_userGroup
		).getGroupId();

		Mockito.doReturn(
			_user
		).when(
			_userLocalService
		).fetchUserById(
			_CLASS_PK
		);
	}

	private static final long _CLASS_PK = RandomTestUtil.randomLong();

	private static final String _DEFAULT_SCOPE_PARAMETER_NAME = "scope";

	private static final long _SCOPE_GROUP_ID = 0L;

	private static final long _USER_GROUP_ID = RandomTestUtil.randomLong();

	private static MockedStatic<ConfigurationProviderUtil>
		_configurationProviderUtilMockedStatic;
	private static final MockedStatic<GroupLocalServiceUtil>
		_groupLocalServiceUtilMockedStatic = Mockito.mockStatic(
			GroupLocalServiceUtil.class);

	private final Group _group = Mockito.mock(Group.class);
	private final LayoutLocalService _layoutLocalService = Mockito.mock(
		LayoutLocalService.class);
	private final Portal _portal = Mockito.mock(Portal.class);
	private final PortletDisplay _portletDisplay = Mockito.mock(
		PortletDisplay.class);
	private final PortletPreferencesLookup _portletPreferencesLookup =
		Mockito.mock(PortletPreferencesLookup.class);
	private final PortletSharedSearchRequest _portletSharedSearchRequest =
		Mockito.mock(PortletSharedSearchRequest.class);
	private final SearchBarPortletInstanceConfiguration
		_searchBarPortletInstanceConfiguration = Mockito.mock(
			SearchBarPortletInstanceConfiguration.class);
	private final SearchBarPrecedenceHelper _searchBarPrecedenceHelper =
		Mockito.mock(SearchBarPrecedenceHelper.class);
	private final SearchCapabilities _searchCapabilities = Mockito.mock(
		SearchCapabilities.class);
	private final SearchSuggestionsCompanyConfiguration
		_searchSuggestionsCompanyConfiguration = Mockito.mock(
			SearchSuggestionsCompanyConfiguration.class);
	private final ThemeDisplay _themeDisplay = Mockito.mock(ThemeDisplay.class);
	private final User _user = Mockito.mock(User.class);
	private final UserGroup _userGroup = Mockito.mock(UserGroup.class);
	private final UserLocalService _userLocalService = Mockito.mock(
		UserLocalService.class);

}