/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.knowledge.base.web.internal.portlet;

import com.liferay.knowledge.base.constants.KBFolderConstants;
import com.liferay.knowledge.base.constants.KBPortletKeys;
import com.liferay.knowledge.base.exception.NoSuchArticleException;
import com.liferay.knowledge.base.model.KBArticle;
import com.liferay.knowledge.base.service.KBArticleLocalService;
import com.liferay.knowledge.base.service.KBArticleService;
import com.liferay.knowledge.base.web.internal.constants.KBWebKeys;
import com.liferay.portal.kernel.portlet.LiferayPortletRequest;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.portlet.MockLiferayResourceResponse;
import com.liferay.portal.kernel.test.portlet.MockRenderRequest;
import com.liferay.portal.kernel.test.portlet.MockRenderResponse;
import com.liferay.portal.kernel.test.portlet.MockResourceRequest;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import jakarta.portlet.PortletException;
import jakarta.portlet.PortletRequest;

import jakarta.servlet.http.HttpServletRequest;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;

import org.mockito.Mockito;

import org.springframework.mock.web.MockHttpServletRequest;

/**
 * @author Jürgen Kappler
 */
public class ArticlePortletTest {

	@ClassRule
	public static LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() throws Exception {
		ReflectionTestUtil.setFieldValue(
			_articlePortlet, "_kbArticleLocalService", _kbArticleLocalService);
		ReflectionTestUtil.setFieldValue(
			_articlePortlet, "kbArticleService", _kbArticleService);
		ReflectionTestUtil.setFieldValue(_articlePortlet, "portal", _portal);
		ReflectionTestUtil.setFieldValue(_articlePortlet, "_portal", _portal);

		Mockito.when(
			_kbArticleLocalService.fetchLatestKBArticle(
				_RESOURCE_PRIM_KEY, WorkflowConstants.STATUS_ANY)
		).thenReturn(
			_kbArticle
		);

		Mockito.when(
			_kbArticleService.getLatestKBArticle(
				_RESOURCE_PRIM_KEY, WorkflowConstants.STATUS_APPROVED)
		).thenReturn(
			_kbArticle
		);

		Mockito.when(
			_themeDisplay.getScopeGroupId()
		).thenReturn(
			_GROUP_ID_SCOPE
		);

		PortalUtil portalUtil = new PortalUtil();

		portalUtil.setPortal(_portal);

		Mockito.when(
			_portal.getCurrentAndAncestorSiteGroupIds(_GROUP_ID_SCOPE)
		).thenReturn(
			new long[] {_GROUP_ID_SCOPE, _GROUP_ID_ANCESTOR}
		);

		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest();

		Mockito.when(
			_portal.getHttpServletRequest(Mockito.any(PortletRequest.class))
		).thenReturn(
			mockHttpServletRequest
		);

		Mockito.when(
			_portal.getLiferayPortletRequest(Mockito.any(PortletRequest.class))
		).thenReturn(
			Mockito.mock(LiferayPortletRequest.class)
		);

		Mockito.when(
			_portal.getOriginalServletRequest(
				Mockito.any(HttpServletRequest.class))
		).thenReturn(
			mockHttpServletRequest
		);
	}

	@Test
	@TestInfo("LPD-114594")
	public void testDoRender() throws Exception {
		Assert.assertEquals(
			_kbArticle,
			_getKBArticle(
				RandomTestUtil.randomLong(),
				KBPortletKeys.KNOWLEDGE_BASE_ARTICLE + "_INSTANCE_" +
					RandomTestUtil.randomString()));
		Assert.assertEquals(
			_kbArticle,
			_getKBArticle(
				_GROUP_ID_ANCESTOR,
				KBPortletKeys.KNOWLEDGE_BASE_ARTICLE_DEFAULT_INSTANCE));
		Assert.assertEquals(
			_kbArticle,
			_getKBArticle(
				_GROUP_ID_SCOPE,
				KBPortletKeys.KNOWLEDGE_BASE_ARTICLE_DEFAULT_INSTANCE));

		MockRenderRequest mockRenderRequest = _render(
			RandomTestUtil.randomLong(),
			KBPortletKeys.KNOWLEDGE_BASE_ARTICLE_DEFAULT_INSTANCE);

		Assert.assertNull(
			mockRenderRequest.getAttribute(
				KBWebKeys.KNOWLEDGE_BASE_KB_ARTICLE));
		Assert.assertTrue(
			SessionErrors.contains(
				mockRenderRequest, NoSuchArticleException.class));
	}

	@Test
	@TestInfo("LPD-114594")
	public void testDoRenderWithCompareVersions() throws Exception {
		Assert.assertFalse(
			SessionErrors.contains(
				_renderCompareVersions(_GROUP_ID_ANCESTOR),
				NoSuchArticleException.class));
		Assert.assertTrue(
			SessionErrors.contains(
				_renderCompareVersions(RandomTestUtil.randomLong()),
				NoSuchArticleException.class));
	}

	@Test
	@TestInfo("LPD-114594")
	public void testServeResource() throws Exception {
		_serveResource(
			RandomTestUtil.randomLong(),
			KBPortletKeys.KNOWLEDGE_BASE_ARTICLE + "_INSTANCE_" +
				RandomTestUtil.randomString());
		_serveResource(
			_GROUP_ID_ANCESTOR,
			KBPortletKeys.KNOWLEDGE_BASE_ARTICLE_DEFAULT_INSTANCE);
		_serveResource(
			_GROUP_ID_SCOPE,
			KBPortletKeys.KNOWLEDGE_BASE_ARTICLE_DEFAULT_INSTANCE);

		PortletException portletException = Assert.assertThrows(
			PortletException.class,
			() -> _serveResource(
				RandomTestUtil.randomLong(),
				KBPortletKeys.KNOWLEDGE_BASE_ARTICLE_DEFAULT_INSTANCE));

		Assert.assertTrue(
			portletException.getCause() instanceof NoSuchArticleException);
	}

	private Object _getKBArticle(long groupId, String portletId)
		throws Exception {

		MockRenderRequest mockRenderRequest = _render(groupId, portletId);

		return mockRenderRequest.getAttribute(
			KBWebKeys.KNOWLEDGE_BASE_KB_ARTICLE);
	}

	private void _mockGroupId(long groupId) {
		Mockito.when(
			_kbArticle.getGroupId()
		).thenReturn(
			groupId
		);
	}

	private void _mockPortletId(String portletId) {
		Mockito.when(
			_portal.getPortletId(Mockito.any(PortletRequest.class))
		).thenReturn(
			portletId
		);
	}

	private MockRenderRequest _render(long groupId, String portletId)
		throws Exception {

		MockRenderRequest mockRenderRequest = new MockRenderRequest();

		_mockGroupId(groupId);
		_mockPortletId(portletId);

		mockRenderRequest.addParameter(
			"resourcePrimKey", String.valueOf(_RESOURCE_PRIM_KEY));
		mockRenderRequest.setAttribute(KBWebKeys.THEME_DISPLAY, _themeDisplay);

		_articlePortlet.doRender(mockRenderRequest, new MockRenderResponse());

		return mockRenderRequest;
	}

	private MockRenderRequest _renderCompareVersions(long groupId)
		throws Exception {

		_mockGroupId(groupId);
		_mockPortletId(KBPortletKeys.KNOWLEDGE_BASE_ARTICLE_DEFAULT_INSTANCE);

		KBArticle kbArticle = Mockito.mock(KBArticle.class);

		Mockito.when(
			kbArticle.getGroupId()
		).thenReturn(
			_GROUP_ID_SCOPE
		);

		long resourcePrimKey = RandomTestUtil.randomLong();

		Mockito.when(
			kbArticle.getResourcePrimKey()
		).thenReturn(
			resourcePrimKey
		);

		Mockito.when(
			_kbArticleService.getLatestKBArticle(
				resourcePrimKey, WorkflowConstants.STATUS_APPROVED)
		).thenReturn(
			kbArticle
		);

		Mockito.when(
			_portal.getScopeGroupId(Mockito.any(PortletRequest.class))
		).thenReturn(
			_GROUP_ID_SCOPE
		);

		String urlTitle = RandomTestUtil.randomString();

		Mockito.when(
			_kbArticleLocalService.fetchKBArticleByUrlTitle(
				_GROUP_ID_SCOPE, KBFolderConstants.DEFAULT_PARENT_FOLDER_ID,
				urlTitle)
		).thenReturn(
			kbArticle
		);

		MockRenderRequest mockRenderRequest = new MockRenderRequest();

		mockRenderRequest.addParameter(Constants.CMD, "compareVersions");
		mockRenderRequest.addParameter(
			"resourcePrimKey", String.valueOf(_RESOURCE_PRIM_KEY));
		mockRenderRequest.addParameter("urlTitle", urlTitle);
		mockRenderRequest.setAttribute(KBWebKeys.THEME_DISPLAY, _themeDisplay);

		_articlePortlet.doRender(mockRenderRequest, new MockRenderResponse());

		return mockRenderRequest;
	}

	private void _serveResource(long groupId, String portletId)
		throws Exception {

		_mockGroupId(groupId);
		_mockPortletId(portletId);

		MockResourceRequest mockResourceRequest = new MockResourceRequest(
			RandomTestUtil.randomString());

		mockResourceRequest.addParameter(
			"resourcePrimKey", String.valueOf(_RESOURCE_PRIM_KEY));
		mockResourceRequest.setAttribute(
			KBWebKeys.THEME_DISPLAY, _themeDisplay);

		_articlePortlet.serveResource(
			mockResourceRequest, new MockLiferayResourceResponse());
	}

	private static final long _GROUP_ID_ANCESTOR = RandomTestUtil.randomLong();

	private static final long _GROUP_ID_SCOPE = RandomTestUtil.randomLong();

	private static final long _RESOURCE_PRIM_KEY = RandomTestUtil.randomLong();

	private final ArticlePortlet _articlePortlet = new ArticlePortlet();
	private final KBArticle _kbArticle = Mockito.mock(KBArticle.class);
	private final KBArticleLocalService _kbArticleLocalService = Mockito.mock(
		KBArticleLocalService.class);
	private final KBArticleService _kbArticleService = Mockito.mock(
		KBArticleService.class);
	private final Portal _portal = Mockito.mock(Portal.class);
	private final ThemeDisplay _themeDisplay = Mockito.mock(ThemeDisplay.class);

}