/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.analytics.layout.page.template.web.internal.servlet.taglib.test;

import com.liferay.analytics.layout.page.template.web.internal.MockObject;
import com.liferay.analytics.layout.page.template.web.internal.layout.display.page.MockObjectLayoutDisplayPageObjectProvider;
import com.liferay.analytics.settings.configuration.AnalyticsConfiguration;
import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.blogs.model.BlogsEntry;
import com.liferay.blogs.service.BlogsEntryLocalService;
import com.liferay.document.library.kernel.model.DLFolderConstants;
import com.liferay.document.library.kernel.service.DLAppLocalService;
import com.liferay.info.item.InfoItemReference;
import com.liferay.journal.constants.JournalFolderConstants;
import com.liferay.journal.model.JournalArticle;
import com.liferay.journal.test.util.JournalTestUtil;
import com.liferay.layout.display.page.LayoutDisplayPageProvider;
import com.liferay.layout.display.page.constants.LayoutDisplayPageWebKeys;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.configuration.test.util.CompanyConfigurationTemporarySwapper;
import com.liferay.portal.kernel.model.ClassName;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.service.ClassNameLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.servlet.taglib.DynamicInclude;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import java.util.Collections;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

/**
 * @author Cristina González
 */
@RunWith(Arquillian.class)
public class AnalyticsRenderFragmentLayoutPreDynamicIncludeTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup(
			TestPropsValues.getCompanyId(), TestPropsValues.getUserId(), 0);
	}

	@Test
	public void testIncludeWithBlog() throws Exception {
		BlogsEntry blogsEntry = _blogsEntryLocalService.addEntry(
			TestPropsValues.getUserId(), RandomTestUtil.randomString(),
			RandomTestUtil.randomString(),
			ServiceContextTestUtil.getServiceContext(_group.getGroupId()));

		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest();

		mockHttpServletRequest.setAttribute(
			LayoutDisplayPageWebKeys.LAYOUT_DISPLAY_PAGE_OBJECT_PROVIDER,
			_blogsLayoutDisplayPageProvider.getLayoutDisplayPageObjectProvider(
				_group.getGroupId(), blogsEntry.getUrlTitle()));

		MockHttpServletResponse mockHttpServletResponse =
			new MockHttpServletResponse();

		_include(mockHttpServletRequest, mockHttpServletResponse);

		Assert.assertEquals(
			StringBundler.concat(
				"<div data-analytics-asset-id=\"", blogsEntry.getEntryId(),
				"\" data-analytics-asset-title=\"", blogsEntry.getTitle(),
				"\" data-analytics-asset-type=\"blog\" ",
				"data-analytics-external-reference-code=\"",
				blogsEntry.getExternalReferenceCode(), "\">"),
			mockHttpServletResponse.getContentAsString());
	}

	@Test
	public void testIncludeWithFileEntry() throws Exception {
		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest();

		FileEntry fileEntry = _dlAppLocalService.addFileEntry(
			null, TestPropsValues.getUserId(), _group.getGroupId(),
			DLFolderConstants.DEFAULT_PARENT_FOLDER_ID, "image.jpg",
			ContentTypes.IMAGE_JPEG,
			FileUtil.getBytes(
				AnalyticsRenderFragmentLayoutPreDynamicIncludeTest.class,
				"dependencies/image.jpg"),
			null, null, null, new ServiceContext());

		mockHttpServletRequest.setAttribute(
			LayoutDisplayPageWebKeys.LAYOUT_DISPLAY_PAGE_OBJECT_PROVIDER,
			_fileEntryLayoutDisplayPageProvider.
				getLayoutDisplayPageObjectProvider(
					new InfoItemReference(
						FileEntry.class.getName(),
						fileEntry.getFileEntryId())));

		MockHttpServletResponse mockHttpServletResponse =
			new MockHttpServletResponse();

		_include(mockHttpServletRequest, mockHttpServletResponse);

		Assert.assertEquals(
			StringBundler.concat(
				"<div data-analytics-asset-action=\"preview\" ",
				"data-analytics-asset-id=\"", fileEntry.getFileEntryId(),
				"\" data-analytics-asset-title=\"", fileEntry.getTitle(),
				"\" data-analytics-asset-type=\"document\" ",
				"data-analytics-asset-version=\"", fileEntry.getVersion(),
				"\" data-analytics-external-reference-code=\"",
				fileEntry.getExternalReferenceCode(), "\">"),
			mockHttpServletResponse.getContentAsString());
	}

	@Test
	public void testIncludeWithJournalArticle() throws Exception {
		JournalArticle journalArticle = JournalTestUtil.addArticle(
			_group.getGroupId(),
			JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			Collections.emptyMap());

		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest();

		mockHttpServletRequest.setAttribute(
			LayoutDisplayPageWebKeys.LAYOUT_DISPLAY_PAGE_OBJECT_PROVIDER,
			_journalArticlesLayoutDisplayPageProvider.
				getLayoutDisplayPageObjectProvider(
					new InfoItemReference(
						JournalArticle.class.getName(),
						journalArticle.getResourcePrimKey())));

		MockHttpServletResponse mockHttpServletResponse =
			new MockHttpServletResponse();

		_include(mockHttpServletRequest, mockHttpServletResponse);

		Assert.assertEquals(
			StringBundler.concat(
				"<div data-analytics-asset-id=\"",
				journalArticle.getResourcePrimKey(),
				"\" data-analytics-asset-title=\"", journalArticle.getTitle(),
				"\" data-analytics-asset-type=\"web-content\" ",
				"data-analytics-external-reference-code=\"",
				journalArticle.getExternalReferenceCode(),
				"\" data-analytics-web-content-resource-pk=\"",
				journalArticle.getResourcePrimKey(), "\">"),
			mockHttpServletResponse.getContentAsString());
	}

	@Test
	public void testIncludeWithoutLayoutDisplayPageObjectProvider()
		throws Exception {

		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest();

		MockHttpServletResponse mockHttpServletResponse =
			new MockHttpServletResponse();

		_include(mockHttpServletRequest, mockHttpServletResponse);

		Assert.assertEquals(
			StringPool.BLANK, mockHttpServletResponse.getContentAsString());
	}

	@Test
	public void testIncludeWithUnregisteredClass() throws Exception {
		ClassName className = _classNameLocalService.addClassName(
			MockObject.class.getName());

		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest();

		mockHttpServletRequest.setAttribute(
			LayoutDisplayPageWebKeys.LAYOUT_DISPLAY_PAGE_OBJECT_PROVIDER,
			new MockObjectLayoutDisplayPageObjectProvider(
				className.getClassNameId()));

		MockHttpServletResponse mockHttpServletResponse =
			new MockHttpServletResponse();

		_include(mockHttpServletRequest, mockHttpServletResponse);

		Assert.assertEquals(
			StringPool.BLANK, mockHttpServletResponse.getContentAsString());
	}

	private void _include(
			MockHttpServletRequest mockHttpServletRequest,
			MockHttpServletResponse mockHttpServletResponse)
		throws Exception {

		try (CompanyConfigurationTemporarySwapper
				companyConfigurationTemporarySwapper =
					new CompanyConfigurationTemporarySwapper(
						TestPropsValues.getCompanyId(),
						AnalyticsConfiguration.class.getName(),
						HashMapDictionaryBuilder.<String, Object>put(
							"liferayAnalyticsDataSourceId",
							RandomTestUtil.nextLong()
						).put(
							"liferayAnalyticsEnableAllGroupIds", true
						).put(
							"liferayAnalyticsFaroBackendSecuritySignature",
							RandomTestUtil.randomString()
						).put(
							"liferayAnalyticsFaroBackendURL",
							RandomTestUtil.randomString()
						).build())) {

			_dynamicInclude.include(
				mockHttpServletRequest, mockHttpServletResponse,
				RandomTestUtil.randomString());
		}
	}

	@Inject
	private BlogsEntryLocalService _blogsEntryLocalService;

	@Inject(
		filter = "component.name=com.liferay.blogs.web.internal.layout.display.page.BlogsLayoutDisplayPageProvider"
	)
	private LayoutDisplayPageProvider _blogsLayoutDisplayPageProvider;

	@Inject
	private ClassNameLocalService _classNameLocalService;

	@Inject
	private DLAppLocalService _dlAppLocalService;

	@Inject(
		filter = "component.name=com.liferay.analytics.layout.page.template.web.internal.servlet.taglib.AnalyticsRenderFragmentLayoutPreDynamicInclude"
	)
	private DynamicInclude _dynamicInclude;

	@Inject(
		filter = "component.name=com.liferay.document.library.web.internal.layout.display.page.FileEntryLayoutDisplayPageProvider"
	)
	private LayoutDisplayPageProvider _fileEntryLayoutDisplayPageProvider;

	@DeleteAfterTestRun
	private Group _group;

	@Inject(
		filter = "component.name=com.liferay.journal.web.internal.layout.display.page.JournalArticleLayoutDisplayPageProvider"
	)
	private LayoutDisplayPageProvider _journalArticlesLayoutDisplayPageProvider;

}