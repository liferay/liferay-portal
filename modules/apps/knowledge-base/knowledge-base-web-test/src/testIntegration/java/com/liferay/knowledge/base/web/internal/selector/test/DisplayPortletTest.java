/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.knowledge.base.web.internal.selector.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.knowledge.base.constants.KBPortletKeys;
import com.liferay.knowledge.base.model.KBArticle;
import com.liferay.knowledge.base.model.KBFolder;
import com.liferay.knowledge.base.service.KBArticleLocalService;
import com.liferay.knowledge.base.service.KBFolderLocalService;
import com.liferay.layout.test.util.LayoutTestUtil;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.portlet.bridges.mvc.constants.MVCRenderConstants;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletRenderRequest;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletRenderResponse;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.portlet.test.MockLiferayPortletContext;

import jakarta.portlet.Portlet;

import java.util.Arrays;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Jonathan McCann
 */
@RunWith(Arquillian.class)
public class DisplayPortletTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();

		_company = _companyLocalService.fetchCompany(_group.getCompanyId());
		_layout = LayoutTestUtil.addTypePortletLayout(_group);
		_serviceContext = ServiceContextTestUtil.getServiceContext(
			_group.getGroupId());
	}

	@Test
	public void testDoRenderWithResourcePrimKey() throws Exception {
		MockLiferayPortletRenderRequest mockLiferayPortletRenderRequest =
			_getMockLiferayPortletRenderRequest();

		KBArticle kbArticle = _addKBArticle(0);

		mockLiferayPortletRenderRequest.setParameter(
			"resourcePrimKey", String.valueOf(kbArticle.getResourcePrimKey()));

		_displayPortlet.render(
			mockLiferayPortletRenderRequest,
			new MockLiferayPortletRenderResponse());

		assertResponse(mockLiferayPortletRenderRequest, kbArticle);
	}

	@Test
	public void testDoRenderWithResourcePrimKeyAndParentFolder()
		throws Exception {

		KBFolder kbFolder = _addKBFolder();

		KBArticle kbArticle = _addKBArticle(kbFolder.getKbFolderId());

		MockLiferayPortletRenderRequest mockLiferayPortletRenderRequest =
			_getMockLiferayPortletRenderRequest();

		mockLiferayPortletRenderRequest.setParameter(
			"kbFolderId", String.valueOf(kbFolder.getKbFolderId()));
		mockLiferayPortletRenderRequest.setParameter(
			"resourcePrimKey", String.valueOf(kbArticle.getResourcePrimKey()));

		_displayPortlet.render(
			mockLiferayPortletRenderRequest,
			new MockLiferayPortletRenderResponse());

		assertResponse(mockLiferayPortletRenderRequest, kbArticle);
	}

	@Test
	public void testDoRenderWithURLTitle() throws Exception {
		MockLiferayPortletRenderRequest mockLiferayPortletRenderRequest =
			_getMockLiferayPortletRenderRequest();

		KBArticle kbArticle = _addKBArticle(0);

		mockLiferayPortletRenderRequest.setParameter(
			"urlTitle", kbArticle.getUrlTitle());

		_displayPortlet.render(
			mockLiferayPortletRenderRequest,
			new MockLiferayPortletRenderResponse());

		assertResponse(mockLiferayPortletRenderRequest, kbArticle);
	}

	@Test
	public void testDoRenderWithURLTitleAndParentKBFolder() throws Exception {
		MockLiferayPortletRenderRequest mockLiferayPortletRenderRequest =
			_getMockLiferayPortletRenderRequest();

		KBFolder kbFolder = _addKBFolder();

		mockLiferayPortletRenderRequest.setParameter(
			"kbFolderId", String.valueOf(kbFolder.getKbFolderId()));
		mockLiferayPortletRenderRequest.setParameter(
			"kbFolderUrlTitle", kbFolder.getUrlTitle());

		KBArticle kbArticle = _addKBArticle(kbFolder.getKbFolderId());

		mockLiferayPortletRenderRequest.setParameter(
			"urlTitle", kbArticle.getUrlTitle());

		_displayPortlet.render(
			mockLiferayPortletRenderRequest,
			new MockLiferayPortletRenderResponse());

		assertResponse(mockLiferayPortletRenderRequest, kbArticle);
	}

	protected void assertResponse(
		MockLiferayPortletRenderRequest mockLiferayPortletRenderRequest,
		KBArticle kbArticle) {

		Assert.assertTrue(
			GetterUtil.getBoolean(
				mockLiferayPortletRenderRequest.getAttribute(
					_KNOWLEDGE_BASE_EXACT_MATCH)));
		Assert.assertEquals(
			kbArticle,
			mockLiferayPortletRenderRequest.getAttribute(
				_KNOWLEDGE_BASE_KB_ARTICLE));

		String[] keywords = GetterUtil.getStringValues(
			mockLiferayPortletRenderRequest.getAttribute(
				_KNOWLEDGE_BASE_SEARCH_KEYWORDS));

		Assert.assertEquals(Arrays.toString(keywords), 0, keywords.length);
	}

	private KBArticle _addKBArticle(long kbFolderId) throws Exception {
		return _kbArticleLocalService.addKBArticle(
			null, TestPropsValues.getUserId(),
			PortalUtil.getClassNameId(KBFolder.class.getName()), kbFolderId,
			RandomTestUtil.randomString(), RandomTestUtil.randomString(),
			RandomTestUtil.randomString(), RandomTestUtil.randomString(), null,
			null, RandomTestUtil.nextDate(), null, null, null,
			ServiceContextTestUtil.getServiceContext(_group.getGroupId()));
	}

	private KBFolder _addKBFolder() throws Exception {
		return _kbFolderLocalService.addKBFolder(
			null, TestPropsValues.getUserId(), _group.getGroupId(),
			PortalUtil.getClassNameId(KBFolder.class.getName()), 0,
			RandomTestUtil.randomString(), RandomTestUtil.randomString(),
			_serviceContext);
	}

	private MockLiferayPortletRenderRequest
			_getMockLiferayPortletRenderRequest()
		throws Exception {

		MockLiferayPortletRenderRequest mockLiferayPortletRenderRequest =
			new MockLiferayPortletRenderRequest();

		String path = "/admin/view.jsp";

		mockLiferayPortletRenderRequest.setAttribute(
			MVCRenderConstants.
				PORTLET_CONTEXT_OVERRIDE_REQUEST_ATTIBUTE_NAME_PREFIX + path,
			new MockLiferayPortletContext(path));

		mockLiferayPortletRenderRequest.setAttribute(WebKeys.LAYOUT, _layout);

		ThemeDisplay themeDisplay = new ThemeDisplay();

		themeDisplay.setUser(_company.getGuestUser());

		mockLiferayPortletRenderRequest.setAttribute(
			WebKeys.THEME_DISPLAY, themeDisplay);

		mockLiferayPortletRenderRequest.setParameter(
			"doAsGroupId", String.valueOf(_group.getGroupId()));
		mockLiferayPortletRenderRequest.setParameter("mvcPath", path);

		return mockLiferayPortletRenderRequest;
	}

	private static final String _KNOWLEDGE_BASE_EXACT_MATCH =
		"KNOWLEDGE_BASE_EXACT_MATCH";

	private static final String _KNOWLEDGE_BASE_KB_ARTICLE =
		"KNOWLEDGE_BASE_KB_ARTICLE";

	private static final String _KNOWLEDGE_BASE_SEARCH_KEYWORDS =
		"KNOWLEDGE_BASE_SEARCH_KEYWORDS";

	@Inject
	private static CompanyLocalService _companyLocalService;

	private Company _company;

	@Inject(
		filter = "jakarta.portlet.name=" + KBPortletKeys.KNOWLEDGE_BASE_DISPLAY
	)
	private Portlet _displayPortlet;

	@DeleteAfterTestRun
	private Group _group;

	@Inject
	private KBArticleLocalService _kbArticleLocalService;

	@Inject
	private KBFolderLocalService _kbFolderLocalService;

	private Layout _layout;
	private ServiceContext _serviceContext;

}