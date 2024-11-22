/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.style.book.web.internal.portlet.action.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.document.library.kernel.exception.NoSuchFileEntryException;
import com.liferay.layout.test.util.LayoutTestUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.LayoutTypePortlet;
import com.liferay.portal.kernel.model.Repository;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.portletfilerepository.PortletFileRepositoryUtil;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletActionRequest;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.portletmvc4spring.test.mock.web.portlet.MockActionResponse;
import com.liferay.style.book.constants.StyleBookPortletKeys;
import com.liferay.style.book.model.StyleBookEntry;
import com.liferay.style.book.service.StyleBookEntryLocalService;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Eudaldo Alonso
 */
@RunWith(Arquillian.class)
public class UpdateStyleBookEntryPreviewMVCActionCommandTest {

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

		_repository = PortletFileRepositoryUtil.addPortletRepository(
			_group.getGroupId(), StyleBookPortletKeys.STYLE_BOOK,
			_serviceContext);

		_themeDisplay = new ThemeDisplay();

		_themeDisplay.setCompany(
			_companyLocalService.getCompany(TestPropsValues.getCompanyId()));
		_themeDisplay.setLanguageId(
			LanguageUtil.getLanguageId(LocaleUtil.getDefault()));

		Layout layout = LayoutTestUtil.addTypePortletLayout(_group);

		_themeDisplay.setLayout(layout);
		_themeDisplay.setLayoutTypePortlet(
			(LayoutTypePortlet)layout.getLayoutType());

		_themeDisplay.setPermissionChecker(
			PermissionThreadLocal.getPermissionChecker());
		_themeDisplay.setRealUser(TestPropsValues.getUser());
		_themeDisplay.setScopeGroupId(_group.getGroupId());
		_themeDisplay.setSiteGroupId(_group.getGroupId());
		_themeDisplay.setUser(TestPropsValues.getUser());
	}

	@Test(expected = NoSuchFileEntryException.class)
	public void testInvalidStyleBookEntryPreview() throws Exception {
		MockLiferayPortletActionRequest mockLiferayPortletActionRequest =
			new MockLiferayPortletActionRequest();

		StyleBookEntry styleBookEntry =
			_styleBookEntryLocalService.addStyleBookEntry(
				null, TestPropsValues.getUserId(), _group.getGroupId(), false,
				StringPool.BLANK, RandomTestUtil.randomString(),
				StringPool.BLANK, RandomTestUtil.randomString(),
				_serviceContext);

		Class<?> clazz = getClass();

		FileEntry fileEntry = PortletFileRepositoryUtil.addPortletFileEntry(
			null, _group.getGroupId(), TestPropsValues.getUserId(),
			StyleBookEntry.class.getName(),
			styleBookEntry.getStyleBookEntryId(), RandomTestUtil.randomString(),
			_repository.getDlFolderId(),
			clazz.getResourceAsStream(
				"dependencies/frontend-tokens-values.json"),
			"frontend-tokens-values.json", ContentTypes.APPLICATION_JSON,
			false);

		mockLiferayPortletActionRequest.addParameter(
			"fileEntryId", String.valueOf(fileEntry.getFileEntryId()));

		mockLiferayPortletActionRequest.addParameter(
			"styleBookEntryId",
			String.valueOf(styleBookEntry.getStyleBookEntryId()));
		mockLiferayPortletActionRequest.setAttribute(
			WebKeys.PORTLET_ID, StyleBookPortletKeys.STYLE_BOOK);
		mockLiferayPortletActionRequest.setAttribute(
			WebKeys.THEME_DISPLAY, _themeDisplay);

		_updateStyleBookEntryPreviewMVCActionCommandTest.processAction(
			mockLiferayPortletActionRequest, new MockActionResponse());

		styleBookEntry = _styleBookEntryLocalService.fetchStyleBookEntry(
			styleBookEntry.getStyleBookEntryId());

		Assert.assertEquals(0, styleBookEntry.getPreviewFileEntryId());

		Assert.assertTrue(
			SessionErrors.contains(
				mockLiferayPortletActionRequest, "invalidThumbnailFormat"));

		PortletFileRepositoryUtil.getPortletFileEntry(
			_group.getGroupId(), _repository.getDlFolderId(),
			_getFileName(
				fileEntry.getExtension(),
				styleBookEntry.getStyleBookEntryId()));
	}

	@Test(expected = NoSuchFileEntryException.class)
	public void testReplaceStyleBookEntryPreview() throws Exception {
		StyleBookEntry styleBookEntry =
			_styleBookEntryLocalService.addStyleBookEntry(
				null, TestPropsValues.getUserId(), _group.getGroupId(), false,
				StringPool.BLANK, RandomTestUtil.randomString(),
				StringPool.BLANK, RandomTestUtil.randomString(),
				_serviceContext);

		MockLiferayPortletActionRequest mockLiferayPortletActionRequest =
			new MockLiferayPortletActionRequest();

		FileEntry tempFileEntry1 = _addFileEntry(
			"thumbnail1.png", styleBookEntry);

		mockLiferayPortletActionRequest.addParameter(
			"fileEntryId", String.valueOf(tempFileEntry1.getFileEntryId()));

		mockLiferayPortletActionRequest.addParameter(
			"styleBookEntryId",
			String.valueOf(styleBookEntry.getStyleBookEntryId()));
		mockLiferayPortletActionRequest.setAttribute(
			WebKeys.PORTLET_ID, StyleBookPortletKeys.STYLE_BOOK);
		mockLiferayPortletActionRequest.setAttribute(
			WebKeys.THEME_DISPLAY, _themeDisplay);

		_updateStyleBookEntryPreviewMVCActionCommandTest.processAction(
			mockLiferayPortletActionRequest, new MockActionResponse());

		Assert.assertNotNull(
			PortletFileRepositoryUtil.getPortletFileEntry(
				styleBookEntry.getPreviewFileEntryId()));

		FileEntry fileEntry = PortletFileRepositoryUtil.getPortletFileEntry(
			_group.getGroupId(), _repository.getDlFolderId(),
			_getFileName(
				tempFileEntry1.getExtension(),
				styleBookEntry.getStyleBookEntryId()));

		Assert.assertNotNull(fileEntry);
		Assert.assertEquals(
			fileEntry.getFileEntryId(), styleBookEntry.getPreviewFileEntryId());

		mockLiferayPortletActionRequest = new MockLiferayPortletActionRequest();

		FileEntry tempFileEntry2 = _addFileEntry(
			"thumbnail2.png", styleBookEntry);

		mockLiferayPortletActionRequest.addParameter(
			"fileEntryId", String.valueOf(tempFileEntry2.getFileEntryId()));

		mockLiferayPortletActionRequest.addParameter(
			"styleBookEntryId",
			String.valueOf(styleBookEntry.getStyleBookEntryId()));
		mockLiferayPortletActionRequest.setAttribute(
			WebKeys.PORTLET_ID, StyleBookPortletKeys.STYLE_BOOK);
		mockLiferayPortletActionRequest.setAttribute(
			WebKeys.THEME_DISPLAY, _themeDisplay);

		_updateStyleBookEntryPreviewMVCActionCommandTest.processAction(
			mockLiferayPortletActionRequest, new MockActionResponse());

		StyleBookEntry updateStyleBookEntry =
			_styleBookEntryLocalService.fetchStyleBookEntry(
				styleBookEntry.getStyleBookEntryId());

		Assert.assertNotEquals(
			styleBookEntry.getPreviewFileEntryId(),
			updateStyleBookEntry.getPreviewFileEntryId());

		fileEntry = PortletFileRepositoryUtil.getPortletFileEntry(
			_group.getGroupId(), _repository.getDlFolderId(),
			_getFileName(
				tempFileEntry2.getExtension(),
				styleBookEntry.getStyleBookEntryId()));

		Assert.assertNotNull(fileEntry);
		Assert.assertEquals(
			fileEntry.getFileEntryId(), styleBookEntry.getPreviewFileEntryId());

		PortletFileRepositoryUtil.getPortletFileEntry(
			styleBookEntry.getPreviewFileEntryId());
	}

	@Test
	public void testUpdateStyleBookEntryPreview() throws Exception {
		MockLiferayPortletActionRequest mockLiferayPortletActionRequest =
			new MockLiferayPortletActionRequest();

		StyleBookEntry styleBookEntry =
			_styleBookEntryLocalService.addStyleBookEntry(
				null, TestPropsValues.getUserId(), _group.getGroupId(), false,
				StringPool.BLANK, RandomTestUtil.randomString(),
				StringPool.BLANK, RandomTestUtil.randomString(),
				_serviceContext);

		FileEntry tempFileEntry = _addFileEntry(
			"thumbnail.png", styleBookEntry);

		mockLiferayPortletActionRequest.addParameter(
			"fileEntryId", String.valueOf(tempFileEntry.getFileEntryId()));

		mockLiferayPortletActionRequest.addParameter(
			"styleBookEntryId",
			String.valueOf(styleBookEntry.getStyleBookEntryId()));
		mockLiferayPortletActionRequest.setAttribute(
			WebKeys.PORTLET_ID, StyleBookPortletKeys.STYLE_BOOK);
		mockLiferayPortletActionRequest.setAttribute(
			WebKeys.THEME_DISPLAY, _themeDisplay);

		_updateStyleBookEntryPreviewMVCActionCommandTest.processAction(
			mockLiferayPortletActionRequest, new MockActionResponse());

		styleBookEntry = _styleBookEntryLocalService.fetchStyleBookEntry(
			styleBookEntry.getStyleBookEntryId());

		Assert.assertNotNull(
			PortletFileRepositoryUtil.getPortletFileEntry(
				styleBookEntry.getPreviewFileEntryId()));

		FileEntry fileEntry = PortletFileRepositoryUtil.getPortletFileEntry(
			_group.getGroupId(), _repository.getDlFolderId(),
			_getFileName(
				tempFileEntry.getExtension(),
				styleBookEntry.getStyleBookEntryId()));

		Assert.assertNotNull(fileEntry);
		Assert.assertEquals(
			fileEntry.getFileEntryId(), styleBookEntry.getPreviewFileEntryId());
	}

	private FileEntry _addFileEntry(
			String fileName, StyleBookEntry styleBookEntry)
		throws Exception {

		Class<?> clazz = getClass();

		return PortletFileRepositoryUtil.addPortletFileEntry(
			null, _group.getGroupId(), TestPropsValues.getUserId(),
			StyleBookEntry.class.getName(),
			styleBookEntry.getStyleBookEntryId(), RandomTestUtil.randomString(),
			_repository.getDlFolderId(),
			clazz.getResourceAsStream("dependencies/thumbnail.png"), fileName,
			ContentTypes.IMAGE_PNG, false);
	}

	private String _getFileName(String extension, long styleBookEntryId) {
		return styleBookEntryId + "_preview." + extension;
	}

	@Inject
	private CompanyLocalService _companyLocalService;

	@DeleteAfterTestRun
	private Group _group;

	private Repository _repository;
	private ServiceContext _serviceContext;

	@Inject
	private StyleBookEntryLocalService _styleBookEntryLocalService;

	private ThemeDisplay _themeDisplay;

	@Inject(
		filter = "mvc.command.name=/style_book/update_style_book_entry_preview"
	)
	private MVCActionCommand _updateStyleBookEntryPreviewMVCActionCommandTest;

}