/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.content.page.editor.web.internal.adaptive.media.test;

import com.liferay.adaptive.media.image.configuration.AMImageConfigurationEntry;
import com.liferay.adaptive.media.image.configuration.AMImageConfigurationHelper;
import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.document.library.kernel.model.DLFolderConstants;
import com.liferay.document.library.kernel.service.DLAppLocalService;
import com.liferay.fragment.contributor.FragmentCollectionContributorRegistry;
import com.liferay.fragment.entry.processor.constants.FragmentEntryProcessorConstants;
import com.liferay.fragment.model.FragmentEntry;
import com.liferay.fragment.model.FragmentEntryLink;
import com.liferay.fragment.service.FragmentEntryLinkService;
import com.liferay.layout.page.template.service.LayoutPageTemplateStructureLocalService;
import com.liferay.layout.responsive.ViewportSize;
import com.liferay.layout.taglib.servlet.taglib.RenderFragmentLayoutTag;
import com.liferay.layout.test.util.LayoutTestUtil;
import com.liferay.layout.util.structure.LayoutStructure;
import com.liferay.layout.util.structure.LayoutStructureItem;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.LayoutTypePortlet;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.servlet.HttpMethods;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.rule.Sync;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.segments.service.SegmentsExperienceLocalService;

import java.util.Collection;
import java.util.Iterator;

import org.hamcrest.CoreMatchers;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

/**
 * @author Pavel Savinov
 */
@RunWith(Arquillian.class)
@Sync
public class LayoutAdaptiveMediaProcessorTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();

		_serviceContext = new ServiceContext();

		_serviceContext.setScopeGroupId(_group.getGroupId());
		_serviceContext.setUserId(TestPropsValues.getUserId());

		ServiceContextThreadLocal.pushServiceContext(_serviceContext);

		_themeDisplay = new ThemeDisplay();

		_themeDisplay.setCompany(
			_companyLocalService.getCompany(TestPropsValues.getCompanyId()));
		_themeDisplay.setLanguageId(
			LanguageUtil.getLanguageId(LocaleUtil.getDefault()));
		_themeDisplay.setPermissionChecker(
			PermissionThreadLocal.getPermissionChecker());
		_themeDisplay.setRealUser(TestPropsValues.getUser());
		_themeDisplay.setScopeGroupId(_group.getGroupId());
		_themeDisplay.setSiteGroupId(_group.getGroupId());
		_themeDisplay.setUser(TestPropsValues.getUser());

		_addLayout();
	}

	@Test
	public void testContentPageAdaptiveMediaProcessModeAuto() throws Exception {
		RenderFragmentLayoutTag renderFragmentLayoutTag =
			new RenderFragmentLayoutTag();

		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest();

		mockHttpServletRequest.setAttribute(
			WebKeys.CTX, mockHttpServletRequest.getServletContext());
		mockHttpServletRequest.setAttribute(
			WebKeys.THEME_DISPLAY, _themeDisplay);
		mockHttpServletRequest.setMethod(HttpMethods.GET);

		_themeDisplay.setRequest(mockHttpServletRequest);

		MockHttpServletResponse mockHttpServletResponse =
			new MockHttpServletResponse();

		renderFragmentLayoutTag.doTag(
			mockHttpServletRequest, mockHttpServletResponse);

		String content = mockHttpServletResponse.getContentAsString();

		Assert.assertThat(
			content, CoreMatchers.containsString("(max-width:300px)"));
		Assert.assertThat(
			content,
			CoreMatchers.containsString(
				"(max-width:1000px) and (min-width:300px)"));
	}

	@Test
	public void testContentPageAdaptiveMediaProcessModeManualTablet()
		throws Exception {

		Collection<AMImageConfigurationEntry> amImageConfigurationEntries =
			_amImageConfigurationHelper.getAMImageConfigurationEntries(
				_group.getCompanyId());

		Assert.assertFalse(amImageConfigurationEntries.isEmpty());

		Iterator<AMImageConfigurationEntry> iterator =
			amImageConfigurationEntries.iterator();

		AMImageConfigurationEntry amImageConfigurationEntry = iterator.next();

		JSONObject editableValuesJSONObject =
			_fragmentEntryLink.getEditableValuesJSONObject();

		JSONObject processorJSONObject = editableValuesJSONObject.getJSONObject(
			FragmentEntryProcessorConstants.
				KEY_EDITABLE_FRAGMENT_ENTRY_PROCESSOR);

		JSONObject imageJSONObject = processorJSONObject.getJSONObject(
			"image-square");

		imageJSONObject.put(
			"config",
			JSONUtil.put(
				"imageConfiguration",
				JSONUtil.put("tablet", amImageConfigurationEntry.getUUID())));

		_fragmentEntryLinkService.updateFragmentEntryLink(
			_fragmentEntryLink.getFragmentEntryLinkId(),
			editableValuesJSONObject.toString());

		RenderFragmentLayoutTag renderFragmentLayoutTag =
			new RenderFragmentLayoutTag();

		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest();

		mockHttpServletRequest.setAttribute(
			WebKeys.CTX, mockHttpServletRequest.getServletContext());
		mockHttpServletRequest.setAttribute(
			WebKeys.THEME_DISPLAY, _themeDisplay);
		mockHttpServletRequest.setMethod(HttpMethods.GET);

		_themeDisplay.setRequest(mockHttpServletRequest);

		MockHttpServletResponse mockHttpServletResponse =
			new MockHttpServletResponse();

		renderFragmentLayoutTag.doTag(
			mockHttpServletRequest, mockHttpServletResponse);

		String content = mockHttpServletResponse.getContentAsString();

		Assert.assertThat(
			content,
			CoreMatchers.containsString(amImageConfigurationEntry.getUUID()));

		StringBundler sb = new StringBundler(5);

		ViewportSize viewportSize = ViewportSize.TABLET;

		sb.append("(min-width:");
		sb.append(viewportSize.getMinWidth());
		sb.append("px) and (max-width:");
		sb.append(viewportSize.getMaxWidth());
		sb.append("px)");

		Assert.assertThat(content, CoreMatchers.containsString(sb.toString()));
	}

	private void _addLayout() throws Exception {
		Layout layout = LayoutTestUtil.addTypeContentLayout(_group);

		FragmentEntry fragmentEntry =
			_fragmentCollectionContributorRegistry.getFragmentEntry(
				"BASIC_COMPONENT-image");

		long defaultSegmentsExperienceId =
			_segmentsExperienceLocalService.fetchDefaultSegmentsExperienceId(
				layout.getPlid());

		FileEntry fileEntry = _dlAppLocalService.addFileEntry(
			null, TestPropsValues.getUserId(), _group.getGroupId(),
			DLFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			StringUtil.randomString(), ContentTypes.IMAGE_JPEG,
			FileUtil.getBytes(getClass(), "dependencies/image.jpg"), null, null,
			null, _serviceContext);

		JSONObject editableValuesJSONObject = JSONUtil.put(
			FragmentEntryProcessorConstants.
				KEY_EDITABLE_FRAGMENT_ENTRY_PROCESSOR,
			JSONUtil.put(
				"image-square",
				JSONUtil.put(
					LocaleUtil.toLanguageId(LocaleUtil.getDefault()),
					JSONUtil.put(
						"fileEntryId", fileEntry.getFileEntryId()
					).put(
						"url", "test"
					))));

		_fragmentEntryLink = _fragmentEntryLinkService.addFragmentEntryLink(
			null, _group.getGroupId(), 0, fragmentEntry.getFragmentEntryId(),
			defaultSegmentsExperienceId, layout.getPlid(),
			fragmentEntry.getCss(), fragmentEntry.getHtml(),
			fragmentEntry.getJs(), fragmentEntry.getConfiguration(),
			editableValuesJSONObject.toString(), StringPool.BLANK, 0, null,
			fragmentEntry.getType(), _serviceContext);

		LayoutStructure layoutStructure = new LayoutStructure();

		LayoutStructureItem rootLayoutStructureItem =
			layoutStructure.addRootLayoutStructureItem();

		layoutStructure.setMainItemId(rootLayoutStructureItem.getItemId());

		LayoutStructureItem containerStyledLayoutStructureItem =
			layoutStructure.addContainerStyledLayoutStructureItem(
				rootLayoutStructureItem.getItemId(), 0);

		layoutStructure.addFragmentStyledLayoutStructureItem(
			_fragmentEntryLink.getFragmentEntryLinkId(),
			containerStyledLayoutStructureItem.getItemId(), 0);

		_layoutPageTemplateStructureLocalService.
			updateLayoutPageTemplateStructureData(
				TestPropsValues.getUserId(), _group.getGroupId(),
				layout.getPlid(), defaultSegmentsExperienceId,
				layoutStructure.toString());

		_themeDisplay.setLayout(layout);
		_themeDisplay.setLayoutSet(layout.getLayoutSet());
		_themeDisplay.setLayoutTypePortlet(
			(LayoutTypePortlet)layout.getLayoutType());
		_themeDisplay.setLookAndFeel(
			layout.getTheme(), layout.getColorScheme());
		_themeDisplay.setPlid(layout.getPlid());
	}

	@Inject
	private AMImageConfigurationHelper _amImageConfigurationHelper;

	@Inject
	private CompanyLocalService _companyLocalService;

	@Inject
	private DLAppLocalService _dlAppLocalService;

	@Inject
	private FragmentCollectionContributorRegistry
		_fragmentCollectionContributorRegistry;

	private FragmentEntryLink _fragmentEntryLink;

	@Inject
	private FragmentEntryLinkService _fragmentEntryLinkService;

	@DeleteAfterTestRun
	private Group _group;

	@Inject
	private LayoutLocalService _layoutLocalService;

	@Inject
	private LayoutPageTemplateStructureLocalService
		_layoutPageTemplateStructureLocalService;

	@Inject
	private SegmentsExperienceLocalService _segmentsExperienceLocalService;

	private ServiceContext _serviceContext;
	private ThemeDisplay _themeDisplay;

}