/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.pim.site.initializer.internal.contributor.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.depot.model.DepotEntry;
import com.liferay.fragment.renderer.FragmentRenderer;
import com.liferay.frontend.data.set.SystemFDSEntry;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.rest.manager.v1_0.DefaultObjectEntryManager;
import com.liferay.object.rest.manager.v1_0.DefaultObjectEntryManagerProvider;
import com.liferay.object.rest.manager.v1_0.ObjectEntryManagerRegistry;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.UnicodeProperties;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.search.rest.dto.v1_0.SearchResult;
import com.liferay.portal.search.rest.resource.v1_0.SearchResultResource;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.portal.vulcan.pagination.Page;
import com.liferay.portal.vulcan.pagination.Pagination;
import com.liferay.site.pim.site.initializer.constants.PIMObjectEntryFolderConstants;
import com.liferay.site.pim.site.initializer.test.util.PIMBasicProductTestUtil;
import com.liferay.site.pim.site.initializer.test.util.PIMTestUtil;

import jakarta.servlet.http.HttpServletRequest;

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
 * @author Stefano Motta
 */
@FeatureFlag("LPD-96666")
@RunWith(Arquillian.class)
public class ProductsCMSSectionTypeContributorTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_group = PIMTestUtil.getOrAddGroup();

		_company = _companyLocalService.getCompany(_group.getCompanyId());
	}

	@Test
	public void testGetAdditionalAPIURLParameters() throws Exception {
		String additionalAPIURLParameters =
			_allSectionSystemFDSEntry.getAdditionalAPIURLParameters(
				_getMockHttpServletRequest());

		Assert.assertTrue(
			additionalAPIURLParameters.contains("cmsSection eq 'products'"));
	}

	@Test
	public void testGetCMSSectionFilterString() throws Exception {
		DepotEntry depotEntry = PIMTestUtil.addSpaceDepotEntry();

		Group depotGroup = depotEntry.getGroup();

		UnicodeProperties unicodeProperties =
			depotGroup.getTypeSettingsProperties();

		unicodeProperties.setProperty("trashEnabled", Boolean.TRUE.toString());

		_groupLocalService.updateGroup(
			depotGroup.getGroupId(), unicodeProperties.toString());

		String title = RandomTestUtil.randomString();

		ObjectEntry objectEntry =
			PIMBasicProductTestUtil.addPIMBasicProductObjectEntry(
				depotEntry.getGroupId(), null,
				PIMObjectEntryFolderConstants.EXTERNAL_REFERENCE_CODE_PRODUCTS,
				RandomTestUtil.randomString(), title);

		ObjectDefinition objectDefinition =
			_objectDefinitionLocalService.getObjectDefinition(
				objectEntry.getObjectDefinitionId());

		DefaultObjectEntryManager defaultObjectEntryManager =
			DefaultObjectEntryManagerProvider.provide(
				_objectEntryManagerRegistry.getObjectEntryManager(
					objectDefinition.getCompanyId(),
					objectDefinition.getStorageType()));

		defaultObjectEntryManager.deleteObjectEntry(
			objectDefinition, objectEntry.getObjectEntryId());

		objectEntry = _objectEntryLocalService.getObjectEntry(
			objectEntry.getObjectEntryId());

		Assert.assertEquals(
			WorkflowConstants.STATUS_IN_TRASH, objectEntry.getStatus());

		MockHttpServletRequest mockHttpServletRequest =
			_getMockHttpServletRequest();

		Object displayContext = ReflectionTestUtil.invoke(
			_recycleBinFragmentRenderer, "getDisplayContext",
			new Class<?>[] {HttpServletRequest.class}, mockHttpServletRequest);

		String filterString = ReflectionTestUtil.invoke(
			displayContext, "getCMSSectionFilterString", new Class<?>[0]);

		Assert.assertTrue(filterString.contains("cmsSection eq 'products'"));

		Map<String, Object> breadcrumbProps = ReflectionTestUtil.invoke(
			displayContext, "getBreadcrumbProps", new Class<?>[0]);

		Assert.assertEquals(
			filterString, breadcrumbProps.get("emptyRecycleBinFilterString"));

		SearchResultResource searchResultResource =
			_searchResultResourceFactory.create(
			).httpServletRequest(
				mockHttpServletRequest
			).preferredLocale(
				LocaleUtil.getDefault()
			).user(
				TestPropsValues.getUser()
			).build();

		Page<SearchResult> searchPage = searchResultResource.getSearchPage(
			null, true, null, null, null,
			searchResultResource.toFilter(filterString), Pagination.of(1, 100),
			null);

		List<String> titles = TransformUtil.transform(
			searchPage.getItems(), SearchResult::getTitle);

		Assert.assertTrue(titles.contains(title));
	}

	private MockHttpServletRequest _getMockHttpServletRequest()
		throws Exception {

		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest();

		ThemeDisplay themeDisplay = new ThemeDisplay();

		themeDisplay.setCompany(_company);
		themeDisplay.setLocale(LocaleUtil.getDefault());
		themeDisplay.setScopeGroupId(_group.getGroupId());
		themeDisplay.setSiteGroupId(_group.getGroupId());
		themeDisplay.setUser(TestPropsValues.getUser());

		mockHttpServletRequest.setAttribute(
			WebKeys.THEME_DISPLAY, themeDisplay);

		return mockHttpServletRequest;
	}

	@Inject(
		filter = "frontend.data.set.name=com.liferay.site.cms.site.initializer-allSection"
	)
	private SystemFDSEntry _allSectionSystemFDSEntry;

	private Company _company;

	@Inject
	private CompanyLocalService _companyLocalService;

	private Group _group;

	@Inject
	private GroupLocalService _groupLocalService;

	@Inject
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	@Inject
	private ObjectEntryLocalService _objectEntryLocalService;

	@Inject
	private ObjectEntryManagerRegistry _objectEntryManagerRegistry;

	@Inject(
		filter = "component.name=com.liferay.site.cms.site.initializer.internal.fragment.renderer.ViewRecycleBinJSPSectionFragmentRenderer"
	)
	private FragmentRenderer _recycleBinFragmentRenderer;

	@Inject
	private SearchResultResource.Factory _searchResultResourceFactory;

}