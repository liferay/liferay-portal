/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.content.dashboard.document.library.internal.item.filter.provider.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.content.dashboard.item.filter.ContentDashboardItemFilter;
import com.liferay.content.dashboard.item.filter.provider.ContentDashboardItemFilterProvider;
import com.liferay.document.library.kernel.model.DLFileEntry;
import com.liferay.document.library.kernel.model.DLFolderConstants;
import com.liferay.document.library.kernel.service.DLAppLocalService;
import com.liferay.portal.kernel.io.unsync.UnsyncByteArrayInputStream;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.search.BooleanClause;
import com.liferay.portal.kernel.search.BooleanClauseFactoryUtil;
import com.liferay.portal.kernel.search.BooleanClauseOccur;
import com.liferay.portal.kernel.search.Document;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.SearchContext;
import com.liferay.portal.kernel.search.SearchContextFactory;
import com.liferay.portal.kernel.search.filter.BooleanFilter;
import com.liferay.portal.kernel.search.filter.Filter;
import com.liferay.portal.kernel.search.generic.BooleanQueryImpl;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.kernel.util.MimeTypesUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.search.legacy.searcher.SearchRequestBuilderFactory;
import com.liferay.portal.search.searcher.SearchResponse;
import com.liferay.portal.search.searcher.Searcher;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.mock.web.MockHttpServletRequest;

/**
 * @author Mikel Lorza
 */
@RunWith(Arquillian.class)
public class FileResolutionContentDashboardItemFilterProviderTest {

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

	@Test
	public void testGetContentDashboardItemFilter() throws Exception {
		FileEntry largeResolutionFileEntry = _addDLFileEntry(
			"dependencies/large_image.jpg");
		FileEntry mediumResolutionFileEntry = _addDLFileEntry(
			"dependencies/medium_image.jpg");
		FileEntry smallResolutionFileEntry = _addDLFileEntry(
			"dependencies/small_image.jpg");
		FileEntry smallMediumResolutionFileEntry = _addDLFileEntry(
			"dependencies/small_medium_image.jpg");

		List<Document> documents = _getDocuments(_getMockHttpServletRequest());

		Assert.assertEquals(documents.toString(), 4, documents.size());

		_assertGetDocuments(
			new long[] {largeResolutionFileEntry.getFileEntryId()}, "large");
		_assertGetDocuments(
			new long[] {
				mediumResolutionFileEntry.getFileEntryId(),
				smallMediumResolutionFileEntry.getFileEntryId()
			},
			"medium");
		_assertGetDocuments(
			new long[] {smallResolutionFileEntry.getFileEntryId()}, "small");
	}

	private FileEntry _addDLFileEntry(String fileName) throws Exception {
		byte[] bytes = FileUtil.getBytes(getClass(), fileName);

		return _dlAppLocalService.addFileEntry(
			null, TestPropsValues.getUserId(), _group.getGroupId(),
			DLFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			RandomTestUtil.randomString(),
			MimeTypesUtil.getExtensionContentType(fileName),
			RandomTestUtil.randomString(), RandomTestUtil.randomString(), null,
			null, new UnsyncByteArrayInputStream(bytes), bytes.length, null,
			null, null,
			ServiceContextTestUtil.getServiceContext(_group.getGroupId()));
	}

	private void _assertGetDocuments(long[] fileEntryIds, String resolution)
		throws Exception {

		List<Document> documents = _getDocuments(
			_getMockHttpServletRequest(resolution));

		Assert.assertEquals(
			documents.toString(), fileEntryIds.length, documents.size());

		for (Document document : documents) {
			Assert.assertEquals(
				DLFileEntry.class.getName(),
				document.get(Field.ENTRY_CLASS_NAME));
			Assert.assertTrue(
				ArrayUtil.contains(
					fileEntryIds,
					Long.valueOf(document.get(Field.ENTRY_CLASS_PK))));
		}
	}

	private List<Document> _getDocuments(HttpServletRequest httpServletRequest)
		throws Exception {

		SearchContext searchContext = SearchContextFactory.getInstance(
			httpServletRequest);

		searchContext.setAttribute("status", WorkflowConstants.STATUS_ANY);
		searchContext.setCompanyId(_group.getCompanyId());
		searchContext.setGroupIds(new long[] {_group.getGroupId()});

		BooleanQueryImpl booleanQueryImpl = new BooleanQueryImpl();

		BooleanFilter booleanFilter = new BooleanFilter();

		ContentDashboardItemFilter contentDashboardItemFilter =
			_contentDashboardItemFilterProvider.getContentDashboardItemFilter(
				httpServletRequest);

		Filter filter = contentDashboardItemFilter.getFilter();

		if (filter != null) {
			booleanFilter.add(filter, BooleanClauseOccur.MUST);
		}

		booleanQueryImpl.setPreBooleanFilter(booleanFilter);

		BooleanClause[] booleanClauses = {
			BooleanClauseFactoryUtil.create(
				booleanQueryImpl, BooleanClauseOccur.MUST.getName())
		};

		searchContext.setBooleanClauses(booleanClauses);

		SearchResponse searchResponse = _getSearchResponse(searchContext);

		return searchResponse.getDocuments71();
	}

	private MockHttpServletRequest _getMockHttpServletRequest()
		throws Exception {

		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest();

		mockHttpServletRequest.setAttribute(
			WebKeys.THEME_DISPLAY, _getThemeDisplay());

		return mockHttpServletRequest;
	}

	private MockHttpServletRequest _getMockHttpServletRequest(String resolution)
		throws Exception {

		MockHttpServletRequest mockHttpServletRequest =
			_getMockHttpServletRequest();

		mockHttpServletRequest.addParameter("resolution", resolution);

		return mockHttpServletRequest;
	}

	private SearchResponse _getSearchResponse(SearchContext searchContext) {
		return _searcher.search(
			_searchRequestBuilderFactory.builder(
				searchContext
			).emptySearchEnabled(
				true
			).entryClassNames(
				DLFileEntry.class.getName()
			).fields(
				Field.ENTRY_CLASS_NAME, Field.ENTRY_CLASS_PK,
				Field.ROOT_ENTRY_CLASS_PK, Field.UID
			).highlightEnabled(
				false
			).build());
	}

	private ThemeDisplay _getThemeDisplay() throws Exception {
		ThemeDisplay themeDisplay = new ThemeDisplay();

		themeDisplay.setCompany(
			_companyLocalService.fetchCompany(TestPropsValues.getCompanyId()));
		themeDisplay.setScopeGroupId(_group.getGroupId());
		themeDisplay.setUser(TestPropsValues.getUser());

		return themeDisplay;
	}

	@Inject
	private CompanyLocalService _companyLocalService;

	@Inject(
		filter = "component.name=com.liferay.content.dashboard.document.library.internal.item.filter.provider.FileResolutionContentDashboardItemFilterProvider"
	)
	private ContentDashboardItemFilterProvider
		_contentDashboardItemFilterProvider;

	@Inject
	private DLAppLocalService _dlAppLocalService;

	@DeleteAfterTestRun
	private Group _group;

	@Inject
	private Searcher _searcher;

	@Inject
	private SearchRequestBuilderFactory _searchRequestBuilderFactory;

}