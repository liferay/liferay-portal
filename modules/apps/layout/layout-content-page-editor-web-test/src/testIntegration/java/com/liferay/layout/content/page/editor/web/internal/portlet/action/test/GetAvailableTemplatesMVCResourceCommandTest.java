/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.content.page.editor.web.internal.portlet.action.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.info.item.renderer.InfoItemRenderer;
import com.liferay.info.item.renderer.InfoItemRendererRegistry;
import com.liferay.info.item.renderer.InfoItemTemplatedRenderer;
import com.liferay.journal.constants.JournalFolderConstants;
import com.liferay.journal.model.JournalArticle;
import com.liferay.journal.service.JournalArticleLocalService;
import com.liferay.journal.test.util.JournalTestUtil;
import com.liferay.layout.test.util.ContentLayoutTestUtil;
import com.liferay.layout.test.util.LayoutTestUtil;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCResourceCommand;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.portlet.MockLiferayResourceRequest;
import com.liferay.portal.kernel.test.portlet.MockLiferayResourceResponse;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import java.io.ByteArrayOutputStream;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Georgel Pop
 */
@RunWith(Arquillian.class)
public class GetAvailableTemplatesMVCResourceCommandTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() throws Exception {
		_group = _groupLocalService.getGroup(TestPropsValues.getGroupId());

		_layout = LayoutTestUtil.addTypeContentLayout(_group);

		ServiceContext serviceContext = new ServiceContext();

		serviceContext.setScopeGroupId(_group.getGroupId());
		serviceContext.setUserId(TestPropsValues.getUserId());

		ServiceContextThreadLocal.pushServiceContext(serviceContext);
	}

	@After
	public void tearDown() {
		ServiceContextThreadLocal.popServiceContext();
	}

	@Test
	@TestInfo("LPD-59838")
	public void testDoServeResourceWhereInfoItemObjectIsNull()
		throws Exception {

		JournalArticle journalArticle = JournalTestUtil.addArticle(
			_group.getGroupId(),
			JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID);

		_journalArticleLocalService.moveArticleToTrash(
			TestPropsValues.getUserId(), journalArticle);

		MockLiferayResourceResponse mockLiferayResourceResponse =
			new MockLiferayResourceResponse();

		_mvcResourceCommand.serveResource(
			_getMockLiferayResourceRequest(journalArticle.getResourcePrimKey()),
			mockLiferayResourceResponse);

		ByteArrayOutputStream byteArrayOutputStream =
			(ByteArrayOutputStream)
				mockLiferayResourceResponse.getPortletOutputStream();

		JSONArray responseJSONArray = JSONFactoryUtil.createJSONArray(
			byteArrayOutputStream.toString());

		int noninfoItemTemplatedRendererCount = 0;

		for (InfoItemRenderer<?> infoItemRenderer :
				_infoItemRendererRegistry.getInfoItemRenderers(
					_CLASS_NAME_JOURNAL_ARTICLE)) {

			if (!infoItemRenderer.isAvailable()) {
				continue;
			}

			if (!(infoItemRenderer instanceof InfoItemTemplatedRenderer)) {
				noninfoItemTemplatedRendererCount++;
			}
		}

		Assert.assertEquals(
			noninfoItemTemplatedRendererCount, responseJSONArray.length());
	}

	private MockLiferayResourceRequest _getMockLiferayResourceRequest(
			long classPK)
		throws Exception {

		MockLiferayResourceRequest mockLiferayResourceRequest =
			new MockLiferayResourceRequest();

		mockLiferayResourceRequest.setAttribute(
			WebKeys.THEME_DISPLAY,
			ContentLayoutTestUtil.getThemeDisplay(
				_companyLocalService.fetchCompany(
					TestPropsValues.getCompanyId()),
				_group, _layout));

		mockLiferayResourceRequest.setParameter(
			"className", _CLASS_NAME_JOURNAL_ARTICLE);
		mockLiferayResourceRequest.setParameter(
			"classPK", String.valueOf(classPK));
		mockLiferayResourceRequest.setParameter(
			"externalReferenceCode", RandomTestUtil.randomString());

		return mockLiferayResourceRequest;
	}

	private static final String _CLASS_NAME_JOURNAL_ARTICLE =
		JournalArticle.class.getName();

	@Inject
	private CompanyLocalService _companyLocalService;

	private Group _group;

	@Inject
	private GroupLocalService _groupLocalService;

	@Inject
	private InfoItemRendererRegistry _infoItemRendererRegistry;

	@Inject
	private JournalArticleLocalService _journalArticleLocalService;

	private Layout _layout;

	@Inject(
		filter = "mvc.command.name=/layout_content_page_editor/get_available_templates"
	)
	private MVCResourceCommand _mvcResourceCommand;

}