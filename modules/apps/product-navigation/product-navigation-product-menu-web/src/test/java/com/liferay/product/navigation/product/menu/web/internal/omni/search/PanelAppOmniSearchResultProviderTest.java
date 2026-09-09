/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.product.navigation.product.menu.web.internal.omni.search;

import com.liferay.application.list.PanelApp;
import com.liferay.application.list.PanelAppRegistry;
import com.liferay.application.list.PanelCategory;
import com.liferay.application.list.constants.PanelCategoryKeys;
import com.liferay.application.list.display.context.logic.PanelCategoryHelper;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.portlet.LiferayPortletRequest;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
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

import jakarta.portlet.PortletURL;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.Mockito;

/**
 * @author Thiago Buarque
 */
public class PanelAppOmniSearchResultProviderTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() {
		LanguageUtil languageUtil = new LanguageUtil();

		Language language = Mockito.mock(Language.class);

		Mockito.when(
			language.get(Mockito.any(Locale.class), Mockito.anyString())
		).thenAnswer(
			invocationOnMock -> invocationOnMock.getArgument(1)
		);

		languageUtil.setLanguage(language);

		ReflectionTestUtil.setFieldValue(
			_panelAppOmniSearchResultProvider, "_panelAppRegistry",
			_panelAppRegistry);
		ReflectionTestUtil.setFieldValue(
			_panelAppOmniSearchResultProvider, "_panelCategoryHelper",
			_panelCategoryHelper);
		ReflectionTestUtil.setFieldValue(
			_panelAppOmniSearchResultProvider, "_portal", _portal);

		Mockito.when(
			_portal.getHttpServletRequest(_liferayPortletRequest)
		).thenReturn(
			_httpServletRequest
		);

		Mockito.when(
			_portal.getOriginalServletRequest(_httpServletRequest)
		).thenReturn(
			_httpServletRequest
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

		Mockito.when(
			_themeDisplay.getScopeGroup()
		).thenReturn(
			_group
		);
	}

	@Test
	public void testGetOmniSearchResults() throws Exception {
		PanelCategory panelCategory = _setUpPanelCategory(
			"Users", PanelCategoryKeys.APPLICATIONS_MENU);

		PortletURL portletURL = Mockito.mock(PortletURL.class);

		_setUpPanelApps(
			panelCategory,
			_setUpPanelApp("Users and Organizations", portletURL),
			_setUpPanelApp("Password Policies", portletURL));

		List<OmniSearchResult> omniSearchResults = _getOmniSearchResults(
			"organizations");

		Assert.assertEquals(
			omniSearchResults.toString(), 1, omniSearchResults.size());

		OmniSearchResult sectionOmniSearchResult = omniSearchResults.get(0);

		Assert.assertEquals("grid", sectionOmniSearchResult.getIcon());
		Assert.assertEquals(
			"applications-menu", sectionOmniSearchResult.getTitle());
		Assert.assertEquals(
			OmniSearchResult.Type.SECTION, sectionOmniSearchResult.getType());

		List<OmniSearchResult> entryOmniSearchResults =
			sectionOmniSearchResult.getOmniSearchResults();

		Assert.assertEquals(
			entryOmniSearchResults.toString(), 1,
			entryOmniSearchResults.size());

		OmniSearchResult entryOmniSearchResult = entryOmniSearchResults.get(0);

		Assert.assertEquals("Users", entryOmniSearchResult.getDescription());
		Assert.assertEquals("grid", entryOmniSearchResult.getIcon());
		Assert.assertEquals(
			"Users and Organizations", entryOmniSearchResult.getTitle());
		Assert.assertEquals(
			OmniSearchResult.Type.ENTRY, entryOmniSearchResult.getType());
		Assert.assertEquals(
			String.valueOf(portletURL), entryOmniSearchResult.getURL());
	}

	@Test
	public void testGetOmniSearchResultsIncludesGrandchildPanelCategories()
		throws Exception {

		PanelCategory childPanelCategory = _setUpPanelCategory(
			"Users", PanelCategoryKeys.APPLICATIONS_MENU);

		_setUpPanelApps(
			_setUpChildPanelCategory("Memberships", childPanelCategory),
			_setUpPanelApp("Organizations", Mockito.mock(PortletURL.class)));

		List<OmniSearchResult> omniSearchResults = _getOmniSearchResults(
			"organizations");

		OmniSearchResult sectionOmniSearchResult = omniSearchResults.get(0);

		Assert.assertEquals(
			"applications-menu", sectionOmniSearchResult.getTitle());

		List<OmniSearchResult> entryOmniSearchResults =
			sectionOmniSearchResult.getOmniSearchResults();

		Assert.assertEquals(
			entryOmniSearchResults.toString(), 1,
			entryOmniSearchResults.size());

		OmniSearchResult entryOmniSearchResult = entryOmniSearchResults.get(0);

		Assert.assertEquals(
			"Users \u203a Memberships", entryOmniSearchResult.getDescription());
		Assert.assertEquals("Organizations", entryOmniSearchResult.getTitle());
	}

	@Test
	public void testGetOmniSearchResultsIsEmptyWithoutMatches()
		throws Exception {

		_setUpPanelApps(
			_setUpPanelCategory("Users", PanelCategoryKeys.APPLICATIONS_MENU),
			_setUpPanelApp(
				"Password Policies", Mockito.mock(PortletURL.class)));

		List<OmniSearchResult> omniSearchResults = _getOmniSearchResults(
			RandomTestUtil.randomString());

		Assert.assertTrue(
			omniSearchResults.toString(), omniSearchResults.isEmpty());
	}

	@Test
	public void testGetOmniSearchResultsLimitsEntriesPerSectionIndependently()
		throws Exception {

		PanelApp[] applicationsMenuPanelApps = _setUpPanelApps(
			"Applications Menu Users ");
		PanelApp[] siteAdministrationPanelApps = _setUpPanelApps(
			"Site Administration Users ");

		_setUpPanelApps(
			_setUpPanelCategory("Users", PanelCategoryKeys.APPLICATIONS_MENU),
			applicationsMenuPanelApps);
		_setUpPanelApps(
			_setUpPanelCategory("Users", PanelCategoryKeys.SITE_ADMINISTRATION),
			siteAdministrationPanelApps);

		List<OmniSearchResult> omniSearchResults = _getOmniSearchResults(
			"users");

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

		Mockito.verify(
			applicationsMenuPanelApps[applicationsMenuPanelApps.length - 1],
			Mockito.never()
		).getPortletURL(
			Mockito.any(HttpServletRequest.class)
		);
	}

	@Test
	public void testGetOmniSearchResultsMatchesTheNameOnly() throws Exception {
		_setUpPanelApps(
			_setUpPanelCategory(
				"Configuration", PanelCategoryKeys.SITE_ADMINISTRATION),
			_setUpPanelApp(
				"Password Policies", Mockito.mock(PortletURL.class)));

		List<OmniSearchResult> omniSearchResults = _getOmniSearchResults(
			"configuration");

		Assert.assertTrue(
			omniSearchResults.toString(), omniSearchResults.isEmpty());
	}

	@Test
	public void testGetOmniSearchResultsReturnsASectionPerRootPanelCategory()
		throws Exception {

		_setUpPanelApps(
			_setUpPanelCategory("Users", PanelCategoryKeys.APPLICATIONS_MENU),
			_setUpPanelApp(
				"Users and Organizations", Mockito.mock(PortletURL.class)));
		_setUpPanelApps(
			_setUpPanelCategory(
				"People", PanelCategoryKeys.SITE_ADMINISTRATION),
			_setUpPanelApp("User Memberships", Mockito.mock(PortletURL.class)));

		Assert.assertEquals(
			ListUtil.fromArray("applications-menu", "site-administration"),
			TransformUtil.transform(
				_getOmniSearchResults("user"), OmniSearchResult::getTitle));
	}

	private List<OmniSearchResult> _getOmniSearchResults(String keywords)
		throws Exception {

		return _panelAppOmniSearchResultProvider.getOmniSearchResults(
			keywords, _liferayPortletRequest, _liferayPortletResponse,
			_themeDisplay);
	}

	private PanelCategory _setUpChildPanelCategory(
		String label, PanelCategory parentPanelCategory) {

		PanelCategory panelCategory = Mockito.mock(PanelCategory.class);

		Mockito.when(
			panelCategory.getKey()
		).thenReturn(
			RandomTestUtil.randomString()
		);

		Mockito.when(
			panelCategory.getLabel(LocaleUtil.US)
		).thenReturn(
			label
		);

		Mockito.when(
			_panelCategoryHelper.getChildPanelCategories(
				parentPanelCategory.getKey(), _themeDisplay)
		).thenReturn(
			Collections.singletonList(panelCategory)
		);

		return panelCategory;
	}

	private PanelApp _setUpPanelApp(String label, PortletURL portletURL)
		throws Exception {

		PanelApp panelApp = Mockito.mock(PanelApp.class);

		Mockito.when(
			panelApp.getLabel(LocaleUtil.US)
		).thenReturn(
			label
		);

		Mockito.when(
			panelApp.getPortletURL(_httpServletRequest)
		).thenReturn(
			portletURL
		);

		return panelApp;
	}

	private void _setUpPanelApps(
		PanelCategory panelCategory, PanelApp... panelApps) {

		Mockito.when(
			_panelAppRegistry.getPanelApps(
				panelCategory.getKey(), _permissionChecker, _group)
		).thenReturn(
			Arrays.asList(panelApps)
		);
	}

	private PanelApp[] _setUpPanelApps(String labelPrefix) throws Exception {
		PanelApp[] panelApps =
			new PanelApp[OmniSearchConstants.MAX_ENTRIES_PER_SECTION + 3];

		for (int i = 0; i < panelApps.length; i++) {
			panelApps[i] = _setUpPanelApp(
				labelPrefix + i, Mockito.mock(PortletURL.class));
		}

		return panelApps;
	}

	private PanelCategory _setUpPanelCategory(
		String label, String rootPanelCategoryKey) {

		PanelCategory panelCategory = Mockito.mock(PanelCategory.class);

		Mockito.when(
			panelCategory.getKey()
		).thenReturn(
			RandomTestUtil.randomString()
		);

		Mockito.when(
			panelCategory.getLabel(LocaleUtil.US)
		).thenReturn(
			label
		);

		Mockito.when(
			_panelCategoryHelper.getChildPanelCategories(
				rootPanelCategoryKey, _themeDisplay)
		).thenReturn(
			Collections.singletonList(panelCategory)
		);

		return panelCategory;
	}

	private final Group _group = Mockito.mock(Group.class);
	private final HttpServletRequest _httpServletRequest = Mockito.mock(
		HttpServletRequest.class);
	private final LiferayPortletRequest _liferayPortletRequest = Mockito.mock(
		LiferayPortletRequest.class);
	private final LiferayPortletResponse _liferayPortletResponse = Mockito.mock(
		LiferayPortletResponse.class);
	private final PanelAppOmniSearchResultProvider
		_panelAppOmniSearchResultProvider =
			new PanelAppOmniSearchResultProvider();
	private final PanelAppRegistry _panelAppRegistry = Mockito.mock(
		PanelAppRegistry.class);
	private final PanelCategoryHelper _panelCategoryHelper = Mockito.mock(
		PanelCategoryHelper.class);
	private final PermissionChecker _permissionChecker = Mockito.mock(
		PermissionChecker.class);
	private final Portal _portal = Mockito.mock(Portal.class);
	private final ThemeDisplay _themeDisplay = Mockito.mock(ThemeDisplay.class);

}