/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.site.resource.v1_0.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.headless.admin.site.client.dto.v1_0.FriendlyUrlHistory;
import com.liferay.headless.admin.site.client.problem.Problem;
import com.liferay.headless.admin.site.resource.v1_0.test.util.LayoutPageTemplateEntryTestUtil;
import com.liferay.headless.admin.site.resource.v1_0.test.util.LayoutUtilityPageEntryTestUtil;
import com.liferay.layout.page.template.model.LayoutPageTemplateEntry;
import com.liferay.layout.test.util.ContentLayoutTestUtil;
import com.liferay.layout.test.util.LayoutTestUtil;
import com.liferay.layout.utility.page.model.LayoutUtilityPageEntry;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Rubén Pulido
 */
@FeatureFlag("LPD-35443")
@RunWith(Arquillian.class)
public class FriendlyUrlHistoryResourceTest
	extends BaseFriendlyUrlHistoryResourceTestCase {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Override
	@Test
	public void testGetSiteDisplayPageTemplateFriendlyUrlHistory()
		throws Exception {

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(
				testGroup.getGroupId(), TestPropsValues.getUserId());

		LayoutPageTemplateEntry layoutPageTemplateEntry =
			LayoutPageTemplateEntryTestUtil.
				getDisplayPageLayoutPageTemplateEntry(serviceContext);

		Layout layout = _layoutLocalService.getLayout(
			layoutPageTemplateEntry.getPlid());

		List<String> friendlyURLs = _updateLayout(layout);

		FriendlyUrlHistory friendlyUrlHistory =
			friendlyUrlHistoryResource.
				getSiteDisplayPageTemplateFriendlyUrlHistory(
					testGroup.getExternalReferenceCode(),
					layoutPageTemplateEntry.getExternalReferenceCode());

		JSONObject jsonObject = _jsonFactory.createJSONObject(
			GetterUtil.getString(friendlyUrlHistory.getFriendlyUrlPath_i18n()));

		Assert.assertEquals(0, jsonObject.length());

		ContentLayoutTestUtil.publishLayout(layout.fetchDraftLayout(), layout);

		friendlyUrlHistory =
			friendlyUrlHistoryResource.
				getSiteDisplayPageTemplateFriendlyUrlHistory(
					testGroup.getExternalReferenceCode(),
					layoutPageTemplateEntry.getExternalReferenceCode());

		_assertFriendlyUrlHistoryJSONObject(
			_jsonFactory.createJSONObject(
				GetterUtil.getString(
					friendlyUrlHistory.getFriendlyUrlPath_i18n())),
			friendlyURLs);

		_assertProblemException(
			LayoutPageTemplateEntryTestUtil.getBasicLayoutPageTemplateEntry(
				serviceContext));
		_assertProblemException(
			LayoutPageTemplateEntryTestUtil.getMasterLayoutPageTemplateEntry(
				serviceContext, WorkflowConstants.STATUS_DRAFT));
	}

	@Override
	@Test
	public void testGetSiteSitePageFriendlyUrlHistory() throws Exception {
		Layout layout = LayoutTestUtil.addTypePortletLayout(
			testGroup.getGroupId());

		_testGetSiteSitePageFriendlyUrlHistory(layout);

		layout = LayoutTestUtil.addTypeContentLayout(testGroup);

		_testGetSiteSitePageFriendlyUrlHistory(layout);

		_assertProblemException(layout.fetchDraftLayout());

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(
				testGroup.getGroupId(), TestPropsValues.getUserId());

		_assertProblemException(
			LayoutPageTemplateEntryTestUtil.
				getBasicLayoutPageTemplateEntryLayout(serviceContext));
		_assertProblemException(
			LayoutPageTemplateEntryTestUtil.
				getDisplayPageLayoutPageTemplateEntryLayout(serviceContext));
		_assertProblemException(
			LayoutPageTemplateEntryTestUtil.
				getMasterLayoutPageTemplateEntryLayout(serviceContext));
		_assertProblemException(
			LayoutUtilityPageEntryTestUtil.getLayoutUtilityPageEntryLayout(
				serviceContext));
	}

	@Override
	@Test
	public void testGetSiteUtilityPageFriendlyUrlHistory() throws Exception {
		LayoutUtilityPageEntry layoutUtilityPageEntry =
			LayoutUtilityPageEntryTestUtil.getLayoutUtilityPageEntry(
				ServiceContextTestUtil.getServiceContext(
					testGroup.getGroupId(), TestPropsValues.getUserId()));

		Layout layout = _layoutLocalService.getLayout(
			layoutUtilityPageEntry.getPlid());

		List<String> friendlyURLs = _updateLayout(layout);

		FriendlyUrlHistory friendlyUrlHistory =
			friendlyUrlHistoryResource.getSiteUtilityPageFriendlyUrlHistory(
				testGroup.getExternalReferenceCode(),
				layoutUtilityPageEntry.getExternalReferenceCode());

		JSONObject jsonObject = _jsonFactory.createJSONObject(
			GetterUtil.getString(friendlyUrlHistory.getFriendlyUrlPath_i18n()));

		Assert.assertEquals(0, jsonObject.length());

		ContentLayoutTestUtil.publishLayout(layout.fetchDraftLayout(), layout);

		friendlyUrlHistory =
			friendlyUrlHistoryResource.getSiteUtilityPageFriendlyUrlHistory(
				testGroup.getExternalReferenceCode(),
				layoutUtilityPageEntry.getExternalReferenceCode());

		_assertFriendlyUrlHistoryJSONObject(
			_jsonFactory.createJSONObject(
				GetterUtil.getString(
					friendlyUrlHistory.getFriendlyUrlPath_i18n())),
			friendlyURLs);

		_assertFriendlyUrlHistoryJSONObject(
			_jsonFactory.createJSONObject(
				GetterUtil.getString(
					friendlyUrlHistory.getFriendlyUrlPath_i18n())),
			friendlyURLs);
	}

	private void _assertFriendlyUrlHistoryJSONObject(
		JSONObject jsonObject, List<String> friendlyURLs) {

		Assert.assertEquals(1, jsonObject.length());

		JSONArray jsonArray = jsonObject.getJSONArray(
			LocaleUtil.toBCP47LanguageId(LocaleUtil.getSiteDefault()));

		Assert.assertEquals(4, jsonArray.length());

		for (int i = 0; i < friendlyURLs.size(); i++) {
			Assert.assertEquals(friendlyURLs.get(i), jsonArray.getString(i));
		}
	}

	private void _assertProblemException(Layout layout) throws Exception {
		try {
			friendlyUrlHistoryResource.getSiteSitePageFriendlyUrlHistory(
				testGroup.getExternalReferenceCode(),
				layout.getExternalReferenceCode());

			Assert.fail();
		}
		catch (Problem.ProblemException problemException) {
			Problem problem = problemException.getProblem();

			Assert.assertEquals("BAD_REQUEST", problem.getStatus());
			Assert.assertNull(problem.getTitle());
		}
	}

	private void _assertProblemException(
			LayoutPageTemplateEntry layoutPageTemplateEntry)
		throws Exception {

		try {
			friendlyUrlHistoryResource.
				getSiteDisplayPageTemplateFriendlyUrlHistory(
					testGroup.getExternalReferenceCode(),
					layoutPageTemplateEntry.getExternalReferenceCode());

			Assert.fail();
		}
		catch (Problem.ProblemException problemException) {
			Problem problem = problemException.getProblem();

			Assert.assertEquals("BAD_REQUEST", problem.getStatus());
			Assert.assertNull(problem.getTitle());
		}
	}

	private void _testGetSiteSitePageFriendlyUrlHistory(Layout layout)
		throws Exception {

		List<String> friendlyURLs = _updateLayout(layout);

		FriendlyUrlHistory friendlyUrlHistory =
			friendlyUrlHistoryResource.getSiteSitePageFriendlyUrlHistory(
				testGroup.getExternalReferenceCode(),
				layout.getExternalReferenceCode());

		if (!layout.isPublished()) {
			JSONObject jsonObject = _jsonFactory.createJSONObject(
				GetterUtil.getString(
					friendlyUrlHistory.getFriendlyUrlPath_i18n()));

			Assert.assertEquals(0, jsonObject.length());

			ContentLayoutTestUtil.publishLayout(
				layout.fetchDraftLayout(), layout);

			friendlyUrlHistory =
				friendlyUrlHistoryResource.getSiteSitePageFriendlyUrlHistory(
					testGroup.getExternalReferenceCode(),
					layout.getExternalReferenceCode());
		}

		_assertFriendlyUrlHistoryJSONObject(
			_jsonFactory.createJSONObject(
				GetterUtil.getString(
					friendlyUrlHistory.getFriendlyUrlPath_i18n())),
			friendlyURLs);
	}

	private List<String> _updateLayout(Layout layout) throws Exception {
		List<String> friendlyURLs = new ArrayList<>();

		String defaultLanguageId = LocaleUtil.toLanguageId(
			LocaleUtil.getSiteDefault());

		friendlyURLs.add(layout.getFriendlyURL(LocaleUtil.getSiteDefault()));

		for (int i = 0; i < 3; i++) {
			layout = _layoutLocalService.updateFriendlyURL(
				TestPropsValues.getUserId(), layout.getPlid(),
				"/" + RandomTestUtil.randomString(), defaultLanguageId);

			friendlyURLs.add(
				layout.getFriendlyURL(LocaleUtil.getSiteDefault()));
		}

		for (Locale locale :
				_language.getAvailableLocales(testGroup.getGroupId())) {

			layout = _layoutLocalService.updateName(
				layout, RandomTestUtil.randomString(),
				LocaleUtil.toLanguageId(locale));
		}

		Collections.reverse(friendlyURLs);

		return friendlyURLs;
	}

	@Inject
	private JSONFactory _jsonFactory;

	@Inject
	private Language _language;

	@Inject
	private LayoutLocalService _layoutLocalService;

}