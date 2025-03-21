/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.seo.internal.portlet.action.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.layout.seo.model.LayoutSEOEntry;
import com.liferay.layout.seo.model.LayoutSEOEntryCustomMetaTag;
import com.liferay.layout.seo.model.LayoutSEOEntryCustomMetaTagProperty;
import com.liferay.layout.seo.service.LayoutSEOEntryLocalService;
import com.liferay.layout.test.util.ContentLayoutTestUtil;
import com.liferay.layout.test.util.LayoutTestUtil;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.service.ClassNameLocalService;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletActionRequest;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletActionResponse;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;

import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Lourdes Fernández Besada
 */
@RunWith(Arquillian.class)
public class EditOpenGraphMVCActionCommandTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();

		_company = _companyLocalService.getCompany(_group.getCompanyId());
		_layout = LayoutTestUtil.addTypePortletLayout(_group);
	}

	@Test
	@TestInfo("LPD-42898")
	public void testEditOpenGraphWithExistingLayoutSEOEntry() throws Exception {
		LayoutSEOEntry layoutSEOEntry =
			_layoutSEOEntryLocalService.updateLayoutSEOEntry(
				TestPropsValues.getUserId(), _group.getGroupId(), false,
				_layout.getLayoutId(), true,
				HashMapBuilder.put(
					LocaleUtil.getSiteDefault(), RandomTestUtil.randomString()
				).build(),
				false, Collections.emptyMap(), Collections.emptyMap(), 0, false,
				Collections.emptyMap(),
				ServiceContextTestUtil.getServiceContext(
					_group, TestPropsValues.getUserId()));

		Map<Locale, String> contentMap = Collections.singletonMap(
			LocaleUtil.getSiteDefault(), RandomTestUtil.randomString());
		String property = RandomTestUtil.randomString();

		_layoutSEOEntryLocalService.updateCustomMetaTags(
			TestPropsValues.getUserId(), _layout.getGroupId(), false,
			_layout.getLayoutId(),
			Collections.singletonList(
				new LayoutSEOEntryCustomMetaTagProperty(contentMap, property)),
			ServiceContextTestUtil.getServiceContext(
				_layout.getGroupId(), TestPropsValues.getUserId()));

		_assertCustomMetaTags(contentMap, layoutSEOEntry, property);

		String languageId = _language.getLanguageId(
			LocaleUtil.getSiteDefault());
		String description = RandomTestUtil.randomString();
		String imageAlt = RandomTestUtil.randomString();
		long fileEntryId = RandomTestUtil.randomLong();
		String title = RandomTestUtil.randomString();

		ReflectionTestUtil.invoke(
			_mvcActionCommand, "doProcessAction",
			new Class<?>[] {ActionRequest.class, ActionResponse.class},
			_getMockLiferayPortletActionRequest(
				HashMapBuilder.put(
					"openGraphDescription_" + languageId, description
				).put(
					"openGraphDescriptionEnabled", "true"
				).put(
					"openGraphImageAlt_" + languageId, imageAlt
				).put(
					"openGraphImageFileEntryId", String.valueOf(fileEntryId)
				).put(
					"openGraphTitle_" + languageId, title
				).put(
					"openGraphTitleEnabled", "true"
				).build()),
			new MockLiferayPortletActionResponse());

		LayoutSEOEntry curLayoutSEOEntry =
			_layoutSEOEntryLocalService.fetchLayoutSEOEntry(
				_layout.getGroupId(), _layout.isPrivateLayout(),
				_layout.getLayoutId());

		Assert.assertEquals(
			layoutSEOEntry.getCanonicalURL(),
			curLayoutSEOEntry.getCanonicalURL());

		_assertCustomMetaTags(contentMap, curLayoutSEOEntry, property);

		Assert.assertTrue(curLayoutSEOEntry.isOpenGraphDescriptionEnabled());
		Assert.assertEquals(
			description, curLayoutSEOEntry.getOpenGraphDescription(languageId));
		Assert.assertTrue(curLayoutSEOEntry.isOpenGraphTitleEnabled());
		Assert.assertEquals(
			title, curLayoutSEOEntry.getOpenGraphTitle(languageId));
		Assert.assertEquals(
			imageAlt, curLayoutSEOEntry.getOpenGraphImageAlt(languageId));
		Assert.assertEquals(
			fileEntryId, curLayoutSEOEntry.getOpenGraphImageFileEntryId());
	}

	private void _assertCustomMetaTags(
		Map<Locale, String> contentMap, LayoutSEOEntry layoutSEOEntry,
		String property) {

		List<LayoutSEOEntryCustomMetaTag> layoutSEOEntryCustomMetaTags =
			_layoutSEOEntryLocalService.getLayoutSEOEntryCustomMetaTags(
				layoutSEOEntry.getGroupId(),
				layoutSEOEntry.getLayoutSEOEntryId());

		Assert.assertFalse(layoutSEOEntryCustomMetaTags.isEmpty());
		Assert.assertEquals(
			layoutSEOEntryCustomMetaTags.toString(), 1,
			layoutSEOEntryCustomMetaTags.size());

		LayoutSEOEntryCustomMetaTag layoutSEOEntryCustomMetaTag =
			layoutSEOEntryCustomMetaTags.get(0);

		Assert.assertEquals(
			property, layoutSEOEntryCustomMetaTag.getProperty());
		Assert.assertEquals(
			contentMap.get(LocaleUtil.getSiteDefault()),
			layoutSEOEntryCustomMetaTag.getContent(
				LocaleUtil.getSiteDefault()));
	}

	private MockLiferayPortletActionRequest _getMockLiferayPortletActionRequest(
			Map<String, String> parameterMap)
		throws Exception {

		MockLiferayPortletActionRequest mockLiferayPortletActionRequest =
			new MockLiferayPortletActionRequest();

		mockLiferayPortletActionRequest.setAttribute(
			WebKeys.THEME_DISPLAY,
			ContentLayoutTestUtil.getThemeDisplay(_company, _group, _layout));
		mockLiferayPortletActionRequest.setParameter(
			"groupId", String.valueOf(_group.getGroupId()));
		mockLiferayPortletActionRequest.setParameter(
			"layoutId", String.valueOf(_layout.getLayoutId()));
		mockLiferayPortletActionRequest.setParameter(
			"privateLayout", String.valueOf(_layout.isPrivateLayout()));

		for (Map.Entry<String, String> entry : parameterMap.entrySet()) {
			mockLiferayPortletActionRequest.setParameter(
				entry.getKey(), entry.getValue());
		}

		return mockLiferayPortletActionRequest;
	}

	@Inject
	private ClassNameLocalService _classNameLocalService;

	private Company _company;

	@Inject
	private CompanyLocalService _companyLocalService;

	@DeleteAfterTestRun
	private Group _group;

	@Inject
	private Language _language;

	private Layout _layout;

	@Inject
	private LayoutSEOEntryLocalService _layoutSEOEntryLocalService;

	@Inject(filter = "mvc.command.name=/layout/edit_open_graph")
	private MVCActionCommand _mvcActionCommand;

}