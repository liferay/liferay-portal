/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.document.library.web.internal.display.context.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.asset.kernel.model.AssetCategory;
import com.liferay.asset.kernel.model.AssetVocabulary;
import com.liferay.asset.kernel.service.AssetCategoryLocalService;
import com.liferay.asset.kernel.service.AssetTagLocalService;
import com.liferay.asset.kernel.service.AssetVocabularyLocalService;
import com.liferay.document.library.constants.DLPortletKeys;
import com.liferay.document.library.kernel.model.DLFolderConstants;
import com.liferay.document.library.kernel.service.DLAppLocalService;
import com.liferay.document.library.kernel.service.DLAppService;
import com.liferay.document.library.test.util.BaseDLAppTestCase;
import com.liferay.document.library.test.util.DLAppTestUtil;
import com.liferay.layout.test.util.LayoutTestUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.search.SearchContainer;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.portlet.PortletPreferencesFactoryUtil;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.repository.model.Folder;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletActionRequest;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletActionResponse;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.rule.Sync;
import com.liferay.portal.kernel.test.rule.SynchronousDestinationTestRule;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.theme.PortletDisplay;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.JavaConstants;
import com.liferay.portal.kernel.util.PortletKeys;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import jakarta.portlet.MutableRenderParameters;
import jakarta.portlet.PortletMode;
import jakarta.portlet.PortletPreferences;
import jakarta.portlet.PortletURL;
import jakarta.portlet.RenderURL;
import jakarta.portlet.WindowState;
import jakarta.portlet.annotations.PortletSerializable;

import jakarta.servlet.http.HttpServletRequest;

import java.io.Writer;

import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpSession;

/**
 * @author Cristina González
 */
@RunWith(Arquillian.class)
@Sync
public class DLAdminDisplayContextTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE,
			SynchronousDestinationTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();

		_company = _companyLocalService.getCompany(_group.getCompanyId());
		_layout = LayoutTestUtil.addTypePortletLayout(_group);
	}

	@Test
	public void testGetOrderByCol() throws Exception {
		Assert.assertEquals("modifiedDate", _getOrderByCol(null, null));

		String keywords = RandomTestUtil.randomString();

		Assert.assertEquals("relevance", _getOrderByCol(keywords, null));

		Assert.assertEquals("size", _getOrderByCol(null, "size"));
		Assert.assertEquals("size", _getOrderByCol(keywords, null));
		Assert.assertEquals("relevance", _getOrderByCol(keywords, "relevance"));
		Assert.assertEquals(
			"relevance",
			_mockHttpSession.getAttribute(
				DLPortletKeys.DOCUMENT_LIBRARY_ADMIN + "_order-by-col"));
		Assert.assertEquals("modifiedDate", _getOrderByCol(null, null));
		Assert.assertNull(
			_mockHttpSession.getAttribute(
				DLPortletKeys.DOCUMENT_LIBRARY_ADMIN + "_order-by-col"));
		Assert.assertEquals("relevance", _getOrderByCol(keywords, null));
		Assert.assertEquals("title", _getOrderByCol(keywords, "title"));
		Assert.assertEquals("title", _getOrderByCol(null, null));
	}

	@Test
	public void testGetOrderByColWithSignedInUser() throws Exception {
		_signedIn = true;

		Assert.assertEquals("modifiedDate", _getOrderByCol(null, null));

		String keywords = RandomTestUtil.randomString();

		Assert.assertEquals("relevance", _getOrderByCol(keywords, null));

		Assert.assertEquals("size", _getOrderByCol(null, "size"));
		Assert.assertEquals("size", _getOrderByColPortletPreference());
		Assert.assertEquals("size", _getOrderByCol(keywords, null));
		Assert.assertEquals("relevance", _getOrderByCol(keywords, "relevance"));
		Assert.assertEquals("relevance", _getOrderByColPortletPreference());
		Assert.assertEquals("modifiedDate", _getOrderByCol(null, null));
		Assert.assertNull(_getOrderByColPortletPreference());
		Assert.assertEquals("relevance", _getOrderByCol(keywords, null));
	}

	@Test
	public void testGetSearchContainer() throws Exception {
		for (int i = 0; i < 25; i++) {
			_addDLFileEntry("alpha_" + i + ".txt", "alpha");
		}

		SearchContainer<Object> searchContainer = _getSearchContainer(
			_getMockLiferayPortletActionRequest());

		Assert.assertEquals(25, searchContainer.getTotal());
	}

	@Test
	public void testGetSearchContainerWithFilterByAssetCategoryId()
		throws Exception {

		Folder parentFolder1 = DLAppTestUtil.addFolder(_group.getGroupId());
		Folder parentFolder2 = DLAppTestUtil.addFolder(_group.getGroupId());

		AssetVocabulary assetVocabulary =
			_assetVocabularyLocalService.addVocabulary(
				TestPropsValues.getUserId(), _group.getGroupId(), "Vocabulary",
				new ServiceContext());

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(_group.getGroupId());

		AssetCategory assetCategory = _assetCategoryLocalService.addCategory(
			TestPropsValues.getUserId(), _group.getGroupId(),
			RandomTestUtil.randomString(), assetVocabulary.getVocabularyId(),
			serviceContext);

		serviceContext.setAssetCategoryIds(
			new long[] {assetCategory.getCategoryId()});

		_addFileEntry(parentFolder1, serviceContext);
		_addFileEntry(parentFolder2, serviceContext);

		SearchContainer<Object> searchContainer = _getSearchContainer(
			_getMockLiferayPortletActionRequest(
				assetCategory.getCategoryId(), parentFolder1.getFolderId(),
				null, null));

		Assert.assertEquals(1, searchContainer.getTotal());
	}

	@Test
	public void testGetSearchContainerWithFilterByAssetTagName()
		throws Exception {

		Folder parentFolder1 = DLAppTestUtil.addFolder(_group.getGroupId());
		Folder parentFolder2 = DLAppTestUtil.addFolder(_group.getGroupId());

		String assetTagName = RandomTestUtil.randomString();

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(_group.getGroupId());

		_assetTagLocalService.addTag(
			null, TestPropsValues.getUserId(), _group.getGroupId(),
			assetTagName, serviceContext);

		serviceContext.setAssetTagNames(new String[] {assetTagName});

		_addFileEntry(parentFolder1, serviceContext);
		_addFileEntry(parentFolder2, serviceContext);

		SearchContainer<Object> searchContainer = _getSearchContainer(
			_getMockLiferayPortletActionRequest(
				-1, parentFolder1.getFolderId(), null, assetTagName));

		Assert.assertEquals(1, searchContainer.getTotal());
	}

	@Test
	public void testGetSearchContainerWithSearch() throws Exception {
		for (int i = 0; i < 25; i++) {
			_addDLFileEntry("alpha_" + i + ".txt", "alpha");
		}

		SearchContainer<Object> searchContainer = _getSearchContainer(
			_getMockLiferayPortletActionRequest(-1, -1, "alpha", null));

		Assert.assertEquals(25, searchContainer.getTotal());
	}

	@Test
	public void testSearchContainerContainsFolderWithNavigationMine()
		throws Exception {

		DLAppTestUtil.addFolder(_group.getGroupId());

		MockLiferayPortletActionRequest mockLiferayPortletActionRequest =
			_getMockLiferayPortletActionRequest();

		mockLiferayPortletActionRequest.setParameter("navigation", "mine");

		SearchContainer<Object> searchContainer = _getSearchContainer(
			mockLiferayPortletActionRequest);

		List<Object> results = searchContainer.getResults();

		Assert.assertEquals(results.toString(), 1, results.size());
	}

	private FileEntry _addDLFileEntry(String fileName, String content)
		throws Exception {

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(_group.getGroupId());

		return _dlAppLocalService.addFileEntry(
			null, TestPropsValues.getUserId(), serviceContext.getScopeGroupId(),
			DLFolderConstants.DEFAULT_PARENT_FOLDER_ID, fileName,
			ContentTypes.TEXT_PLAIN, RandomTestUtil.randomString(),
			StringPool.BLANK, StringPool.BLANK, StringPool.BLANK,
			content.getBytes(), null, null, null, serviceContext);
	}

	private void _addFileEntry(
			Folder parentFolder, ServiceContext serviceContext)
		throws Exception {

		_dlAppService.addFileEntry(
			null, _group.getGroupId(), parentFolder.getFolderId(),
			RandomTestUtil.randomString(), ContentTypes.TEXT_PLAIN,
			RandomTestUtil.randomString(), StringPool.BLANK, StringPool.BLANK,
			StringPool.BLANK, BaseDLAppTestCase.CONTENT.getBytes(), null, null,
			null, serviceContext);
	}

	private Object _getDLAdminDisplayContext(
		MockLiferayPortletActionRequest mockLiferayPortletActionRequest) {

		return ReflectionTestUtil.invoke(
			_dlAdminDisplayContextProvider, "getDLAdminDisplayContext",
			new Class<?>[] {HttpServletRequest.class},
			mockLiferayPortletActionRequest.getHttpServletRequest());
	}

	private MockLiferayPortletActionRequest
			_getMockLiferayPortletActionRequest()
		throws Exception {

		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest();

		mockHttpServletRequest.setSession(_mockHttpSession);

		MockLiferayPortletActionRequest mockLiferayPortletActionRequest =
			new MockLiferayPortletActionRequest(mockHttpServletRequest);

		mockLiferayPortletActionRequest.setAttribute(
			JavaConstants.JAKARTA_PORTLET_CONFIG, null);
		mockLiferayPortletActionRequest.setAttribute(
			JavaConstants.JAKARTA_PORTLET_REQUEST,
			mockLiferayPortletActionRequest);
		mockLiferayPortletActionRequest.setAttribute(
			JavaConstants.JAKARTA_PORTLET_RESPONSE, new MockActionResponse());
		mockLiferayPortletActionRequest.setAttribute(
			WebKeys.THEME_DISPLAY, _getThemeDisplay());

		return mockLiferayPortletActionRequest;
	}

	private MockLiferayPortletActionRequest _getMockLiferayPortletActionRequest(
			long categoryId, long folderId, String keywords, String tagName)
		throws Exception {

		MockLiferayPortletActionRequest mockLiferayPortletActionRequest =
			_getMockLiferayPortletActionRequest();

		if (categoryId > 0) {
			mockLiferayPortletActionRequest.setParameter(
				"categoryId", String.valueOf(categoryId));
		}

		if (folderId > 0) {
			mockLiferayPortletActionRequest.setParameter(
				"folderId", String.valueOf(folderId));
		}

		if (Validator.isNotNull(keywords)) {
			mockLiferayPortletActionRequest.setParameter(
				"mvcRenderCommandName", "/document_library/search");
			mockLiferayPortletActionRequest.setParameter("keywords", keywords);
		}

		if (Validator.isNotNull(tagName)) {
			mockLiferayPortletActionRequest.setParameter("tag", tagName);
		}

		return mockLiferayPortletActionRequest;
	}

	private String _getOrderByCol(String keywords, String orderByCol)
		throws Exception {

		MockLiferayPortletActionRequest mockLiferayPortletActionRequest =
			_getMockLiferayPortletActionRequest(-1, -1, keywords, null);

		if (Validator.isNotNull(orderByCol)) {
			mockLiferayPortletActionRequest.setParameter(
				"orderByCol", orderByCol);
		}

		return ReflectionTestUtil.invoke(
			_getDLAdminDisplayContext(mockLiferayPortletActionRequest),
			"getOrderByCol", new Class<?>[0], null);
	}

	private String _getOrderByColPortletPreference() throws Exception {
		PortletPreferences portletPreferences =
			PortletPreferencesFactoryUtil.getLayoutPortletSetup(
				_company.getCompanyId(), TestPropsValues.getUserId(),
				PortletKeys.PREFS_OWNER_TYPE_USER, _layout.getPlid(),
				DLPortletKeys.DOCUMENT_LIBRARY_ADMIN, StringPool.BLANK);

		return portletPreferences.getValue("order-by-col", null);
	}

	private SearchContainer<Object> _getSearchContainer(
		MockLiferayPortletActionRequest mockLiferayPortletActionRequest) {

		return ReflectionTestUtil.invoke(
			_getDLAdminDisplayContext(mockLiferayPortletActionRequest),
			"getSearchContainer", new Class<?>[0], null);
	}

	private ThemeDisplay _getThemeDisplay() throws Exception {
		ThemeDisplay themeDisplay = new ThemeDisplay();

		themeDisplay.setCompany(_company);
		themeDisplay.setLayout(_layout);
		themeDisplay.setPermissionChecker(
			PermissionThreadLocal.getPermissionChecker());
		themeDisplay.setPlid(_layout.getPlid());

		PortletDisplay portletDisplay = themeDisplay.getPortletDisplay();

		portletDisplay.setId(DLPortletKeys.DOCUMENT_LIBRARY_ADMIN);

		themeDisplay.setRealUser(TestPropsValues.getUser());
		themeDisplay.setScopeGroupId(_layout.getGroupId());
		themeDisplay.setSignedIn(_signedIn);
		themeDisplay.setSiteGroupId(_layout.getGroupId());
		themeDisplay.setUser(TestPropsValues.getUser());

		return themeDisplay;
	}

	@Inject
	private AssetCategoryLocalService _assetCategoryLocalService;

	@Inject
	private AssetTagLocalService _assetTagLocalService;

	@Inject
	private AssetVocabularyLocalService _assetVocabularyLocalService;

	private Company _company;

	@Inject
	private CompanyLocalService _companyLocalService;

	@Inject(
		filter = "component.name=com.liferay.document.library.web.internal.display.context.DLAdminDisplayContextProvider",
		type = Inject.NoType.class
	)
	private Object _dlAdminDisplayContextProvider;

	@Inject
	private DLAppLocalService _dlAppLocalService;

	@Inject
	private DLAppService _dlAppService;

	@DeleteAfterTestRun
	private Group _group;

	private Layout _layout;
	private final MockHttpSession _mockHttpSession = new MockHttpSession();
	private boolean _signedIn;

	private static class MockActionResponse
		extends MockLiferayPortletActionResponse {

		@Override
		public MockPortletURL createRenderURL() {
			return new MockPortletURL();
		}

	}

	private static class MockPortletURL implements PortletURL, RenderURL {

		@Override
		public void addProperty(String key, String value) {
		}

		@Override
		public Appendable append(Appendable appendable) {
			return null;
		}

		@Override
		public Appendable append(Appendable appendable, boolean escapeXML) {
			return null;
		}

		@Override
		public String getFragmentIdentifier() {
			return null;
		}

		@Override
		public Map<String, String[]> getParameterMap() {
			return null;
		}

		@Override
		public PortletMode getPortletMode() {
			return null;
		}

		@Override
		public MutableRenderParameters getRenderParameters() {
			return null;
		}

		@Override
		public WindowState getWindowState() {
			return null;
		}

		@Override
		public void removePublicRenderParameter(String name) {
		}

		@Override
		public void setBeanParameter(PortletSerializable portletSerializable) {
		}

		@Override
		public void setFragmentIdentifier(String fragment) {
		}

		@Override
		public void setParameter(String name, String value) {
		}

		@Override
		public void setParameter(String name, String... values) {
		}

		@Override
		public void setParameters(Map<String, String[]> map) {
		}

		@Override
		public void setPortletMode(PortletMode portletMode) {
		}

		@Override
		public void setProperty(String key, String value) {
		}

		@Override
		public void setSecure(boolean secure) {
		}

		@Override
		public void setWindowState(WindowState windowState) {
		}

		@Override
		public void write(Writer writer) {
		}

		@Override
		public void write(Writer writer, boolean escapeXML) {
		}

	}

}