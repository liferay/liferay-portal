/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.fragment.entry.processor.background.image.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.document.library.helper.DLURLHelper;
import com.liferay.document.library.kernel.model.DLFolderConstants;
import com.liferay.document.library.kernel.service.DLAppLocalService;
import com.liferay.fragment.entry.processor.constants.FragmentEntryProcessorConstants;
import com.liferay.fragment.model.FragmentEntryLink;
import com.liferay.fragment.processor.DefaultFragmentEntryProcessorContext;
import com.liferay.fragment.processor.FragmentEntryProcessorRegistry;
import com.liferay.fragment.service.FragmentCollectionService;
import com.liferay.fragment.service.FragmentEntryLinkLocalService;
import com.liferay.fragment.service.FragmentEntryService;
import com.liferay.layout.test.util.LayoutTestUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.LayoutSet;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.LayoutSetLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.service.ThemeLocalService;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.kernel.util.HtmlUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

/**
 * @author Eudaldo Alonso
 */
@RunWith(Arquillian.class)
public class BackgroundImageDocumentFragmentEntryProcessorTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();

		_layout = LayoutTestUtil.addTypeContentLayout(_group);

		_serviceContext = ServiceContextTestUtil.getServiceContext(
			_group.getGroupId(), TestPropsValues.getUserId());

		ServiceContextThreadLocal.pushServiceContext(_serviceContext);
	}

	@After
	public void tearDown() throws Exception {
		ServiceContextThreadLocal.popServiceContext();
	}

	@Test
	public void testAddFragmentEntryWithFreeMarkerVariable() throws Exception {
		FragmentEntryLink fragmentEntryLink =
			_fragmentEntryLinkLocalService.createFragmentEntryLink(0);

		FileEntry fileEntry = _dlAppLocalService.addFileEntry(
			null, TestPropsValues.getUserId(), _group.getGroupId(),
			DLFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			StringUtil.randomString(), ContentTypes.IMAGE_JPEG,
			FileUtil.getBytes(getClass(), "dependencies/image.jpg"), null, null,
			null, _serviceContext);

		fragmentEntryLink.setEditableValues(
			JSONUtil.put(
				FragmentEntryProcessorConstants.
					KEY_BACKGROUND_IMAGE_FRAGMENT_ENTRY_PROCESSOR,
				JSONUtil.put(
					"bannerSimpleImage",
					JSONUtil.put(
						LocaleUtil.toLanguageId(LocaleUtil.getSiteDefault()),
						JSONUtil.put(
							"classNameId",
							_portal.getClassNameId(FileEntry.class)
						).put(
							"classPK", fileEntry.getFileEntryId()
						).put(
							"fileEntryId", fileEntry.getFileEntryId()
						).put(
							"url",
							_dlURLHelper.getImagePreviewURL(
								fileEntry, fileEntry.getFileVersion(), null,
								StringPool.BLANK, false, false)
						)))
			).toString());

		fragmentEntryLink.setHtml(_readFileToString("fragment_entry.html"));

		_testProcessFragmentEntryLinkHTML(
			Collections.emptyList(), fragmentEntryLink,
			Arrays.asList("imagePreview=1"));
	}

	@Test
	@TestInfo("LPD-73556")
	public void testProcessFragmentEntryLinkHTMLWithMissingReference()
		throws Exception {

		FragmentEntryLink fragmentEntryLink =
			_fragmentEntryLinkLocalService.createFragmentEntryLink(0);

		String externalReferenceCode = RandomTestUtil.randomString();

		JSONObject jsonObject = JSONUtil.put(
			"className", FileEntry.class.getName()
		).put(
			"externalReferenceCode", externalReferenceCode
		);

		fragmentEntryLink.setEditableValues(
			JSONUtil.put(
				FragmentEntryProcessorConstants.
					KEY_BACKGROUND_IMAGE_FRAGMENT_ENTRY_PROCESSOR,
				JSONUtil.put(
					"bannerSimpleImage",
					JSONUtil.put(
						LocaleUtil.toLanguageId(LocaleUtil.getSiteDefault()),
						jsonObject))
			).toString());

		fragmentEntryLink.setHtml(_readFileToString("fragment_entry.html"));

		_testProcessFragmentEntryLinkHTML(
			Arrays.asList(
				StringBundler.concat(
					"background-image: url(", jsonObject,
					"); background-size: cover;")),
			fragmentEntryLink, Collections.emptyList());

		FileEntry fileEntry = _dlAppLocalService.addFileEntry(
			externalReferenceCode, TestPropsValues.getUserId(),
			_group.getGroupId(), DLFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			StringUtil.randomString(), ContentTypes.IMAGE_JPEG,
			FileUtil.getBytes(getClass(), "dependencies/image.jpg"), null, null,
			null, _serviceContext);

		String previewURL = _dlURLHelper.getPreviewURL(
			fileEntry, fileEntry.getFileVersion(), null, StringPool.BLANK);

		_testProcessFragmentEntryLinkHTML(
			Arrays.asList("background-image: url(" + previewURL),
			fragmentEntryLink, Arrays.asList(jsonObject.toString()));

		externalReferenceCode = RandomTestUtil.randomString();

		jsonObject = JSONUtil.put(
			"classNameId", _portal.getClassNameId(FileEntry.class.getName())
		).put(
			"externalReferenceCode", externalReferenceCode
		).put(
			"fieldId", "FileEntry_fileURL"
		);

		fragmentEntryLink.setEditableValues(
			JSONUtil.put(
				FragmentEntryProcessorConstants.
					KEY_BACKGROUND_IMAGE_FRAGMENT_ENTRY_PROCESSOR,
				JSONUtil.put("bannerSimpleImage", jsonObject)
			).toString());

		_testProcessFragmentEntryLinkHTML(
			Collections.emptyList(), fragmentEntryLink,
			Arrays.asList(jsonObject.toString()));

		fileEntry = _dlAppLocalService.addFileEntry(
			externalReferenceCode, TestPropsValues.getUserId(),
			_group.getGroupId(), DLFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			StringUtil.randomString(), ContentTypes.IMAGE_JPEG,
			FileUtil.getBytes(getClass(), "dependencies/image.jpg"), null, null,
			null, _serviceContext);

		previewURL = _dlURLHelper.getPreviewURL(
			fileEntry, fileEntry.getFileVersion(), null, StringPool.BLANK);

		_testProcessFragmentEntryLinkHTML(
			Arrays.asList("background-image: url(" + previewURL),
			fragmentEntryLink, Arrays.asList(jsonObject.toString()));

		Group group = GroupTestUtil.addGroup();

		jsonObject = JSONUtil.put(
			"className", FileEntry.class.getName()
		).put(
			"externalReferenceCode", externalReferenceCode
		).put(
			"scopeExternalReferenceCode", group.getExternalReferenceCode()
		);

		fragmentEntryLink.setEditableValues(
			JSONUtil.put(
				FragmentEntryProcessorConstants.
					KEY_BACKGROUND_IMAGE_FRAGMENT_ENTRY_PROCESSOR,
				JSONUtil.put(
					"bannerSimpleImage",
					JSONUtil.put(
						LocaleUtil.toLanguageId(LocaleUtil.getSiteDefault()),
						jsonObject))
			).toString());

		_testProcessFragmentEntryLinkHTML(
			Arrays.asList(jsonObject.toString()), fragmentEntryLink,
			Collections.emptyList());

		fileEntry = _dlAppLocalService.addFileEntry(
			externalReferenceCode, TestPropsValues.getUserId(),
			group.getGroupId(), DLFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			StringUtil.randomString(), ContentTypes.IMAGE_JPEG,
			FileUtil.getBytes(getClass(), "dependencies/image.jpg"), null, null,
			null,
			ServiceContextTestUtil.getServiceContext(
				group, TestPropsValues.getUserId()));

		previewURL = _dlURLHelper.getPreviewURL(
			fileEntry, fileEntry.getFileVersion(), null, StringPool.BLANK);

		_testProcessFragmentEntryLinkHTML(
			Arrays.asList("background-image: url(" + previewURL),
			fragmentEntryLink, Arrays.asList(jsonObject.toString()));
	}

	private MockHttpServletRequest _getMockHttpServletRequest()
		throws Exception {

		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest();

		mockHttpServletRequest.setAttribute(
			WebKeys.THEME_DISPLAY, _getThemeDisplay(mockHttpServletRequest));

		return mockHttpServletRequest;
	}

	private ThemeDisplay _getThemeDisplay(HttpServletRequest httpServletRequest)
		throws Exception {

		ThemeDisplay themeDisplay = new ThemeDisplay();

		themeDisplay.setCompany(
			_companyLocalService.fetchCompany(_group.getCompanyId()));
		themeDisplay.setLayout(_layout);

		LayoutSet layoutSet = _layoutSetLocalService.getLayoutSet(
			_group.getGroupId(), false);

		themeDisplay.setLookAndFeel(
			_themeLocalService.getTheme(
				_group.getCompanyId(), layoutSet.getThemeId()),
			null);

		themeDisplay.setRealUser(TestPropsValues.getUser());
		themeDisplay.setRequest(httpServletRequest);
		themeDisplay.setUser(TestPropsValues.getUser());

		return themeDisplay;
	}

	private String _readFileToString(String fileName) throws Exception {
		Class<?> clazz = getClass();

		return StringUtil.read(
			clazz.getClassLoader(),
			"com/liferay/fragment/entry/processor/background/image/test" +
				"/dependencies/" + fileName);
	}

	private void _testProcessFragmentEntryLinkHTML(
			List<String> expected, FragmentEntryLink fragmentEntryLink,
			List<String> notExpected)
		throws Exception {

		DefaultFragmentEntryProcessorContext
			defaultFragmentEntryProcessorContext =
				new DefaultFragmentEntryProcessorContext(
					_group.getCompanyId(), _getMockHttpServletRequest(),
					new MockHttpServletResponse(), LocaleUtil.getDefault(),
					null, _group.getGroupId());

		String processFragmentEntryLinkHTML =
			_fragmentEntryProcessorRegistry.processFragmentEntryLinkHTML(
				fragmentEntryLink, defaultFragmentEntryProcessorContext);

		for (String string : expected) {
			Assert.assertTrue(
				processFragmentEntryLinkHTML,
				processFragmentEntryLinkHTML.contains(
					HtmlUtil.toInputSafe(string)));
		}

		for (String string : notExpected) {
			Assert.assertFalse(
				processFragmentEntryLinkHTML,
				processFragmentEntryLinkHTML.contains(
					HtmlUtil.toInputSafe(string)));
		}
	}

	@Inject
	private CompanyLocalService _companyLocalService;

	@Inject
	private DLAppLocalService _dlAppLocalService;

	@Inject
	private DLURLHelper _dlURLHelper;

	@Inject
	private FragmentCollectionService _fragmentCollectionService;

	@Inject
	private FragmentEntryLinkLocalService _fragmentEntryLinkLocalService;

	@Inject
	private FragmentEntryProcessorRegistry _fragmentEntryProcessorRegistry;

	@Inject
	private FragmentEntryService _fragmentEntryService;

	@DeleteAfterTestRun
	private Group _group;

	private Layout _layout;

	@Inject
	private LayoutSetLocalService _layoutSetLocalService;

	@Inject
	private Portal _portal;

	private ServiceContext _serviceContext;

	@Inject
	private ThemeLocalService _themeLocalService;

}