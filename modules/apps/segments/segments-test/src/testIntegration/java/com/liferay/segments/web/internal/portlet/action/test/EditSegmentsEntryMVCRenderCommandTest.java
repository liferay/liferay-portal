/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.segments.web.internal.portlet.action.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.portal.configuration.test.util.CompanyConfigurationTemporarySwapper;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONSerializer;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCRenderCommand;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletRenderRequest;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletRenderResponse;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.LocalizationUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.segments.configuration.SegmentsCompanyConfiguration;
import com.liferay.segments.criteria.Criteria;
import com.liferay.segments.criteria.CriteriaSerializer;
import com.liferay.segments.criteria.contributor.SegmentsCriteriaContributor;
import com.liferay.segments.model.SegmentsEntry;
import com.liferay.segments.test.util.SegmentsTestUtil;

import java.util.Map;
import java.util.Objects;

import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Cristina González
 */
@RunWith(Arquillian.class)
public class EditSegmentsEntryMVCRenderCommandTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@BeforeClass
	public static void setUpClass() throws Exception {
		_company = _companyLocalService.fetchCompany(
			TestPropsValues.getCompanyId());
	}

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();
	}

	@Test
	public void testGetPropsWithSegmentsEntryId() throws Exception {
		MockLiferayPortletRenderRequest mockLiferayPortletRenderRequest =
			_getMockLiferayPortletRenderRequests();

		User user = TestPropsValues.getUser();

		mockLiferayPortletRenderRequest.setAttribute(WebKeys.USER, user);

		SegmentsEntry segmentsEntry = _addSegmentEntry(
			String.format("(firstName eq '%s')", user.getFirstName()));

		mockLiferayPortletRenderRequest.setParameter(
			"segmentsEntryId",
			String.valueOf(segmentsEntry.getSegmentsEntryId()));

		_mvcRenderCommand.render(
			mockLiferayPortletRenderRequest,
			new MockLiferayPortletRenderResponse());

		Map<String, Object> data = ReflectionTestUtil.invoke(
			mockLiferayPortletRenderRequest.getAttribute(_CLASS_NAME),
			"getData", new Class<?>[0]);

		Map<String, Object> props = (Map<String, Object>)data.get("props");

		JSONArray contributorsJSONArray = (JSONArray)props.get("contributors");

		boolean findUserContributor = false;

		for (Object object : contributorsJSONArray) {
			JSONObject jsonObject = (JSONObject)object;

			if (Objects.equals(jsonObject.getString("propertyKey"), "user")) {
				Assert.assertEquals(
					JSONUtil.put(
						"conjunctionName", "and"
					).put(
						"groupId", "group_0"
					).put(
						"items",
						JSONUtil.putAll(
							JSONUtil.put(
								"operatorName", "eq"
							).put(
								"propertyName", "firstName"
							).put(
								"value", "Test"
							))
					).toString(),
					String.valueOf(jsonObject.getJSONObject("initialQuery")));

				findUserContributor = true;

				break;
			}
		}

		Assert.assertTrue(findUserContributor);

		Assert.assertEquals(
			LocalizationUtil.getDefaultLanguageId(segmentsEntry.getName()),
			props.get("defaultLanguageId"));
		Assert.assertEquals(1, (int)props.get("initialMembersCount"));
		Assert.assertTrue((boolean)props.get("initialSegmentActive"));

		JSONSerializer jsonSerializer = JSONFactoryUtil.createJSONSerializer();

		Assert.assertEquals(
			String.valueOf(
				JSONFactoryUtil.createJSONObject(
					jsonSerializer.serializeDeep(segmentsEntry.getNameMap()))),
			String.valueOf(props.get("initialSegmentName")));

		Assert.assertNull(props.get("siteItemSelectorURL"));
	}

	@Test
	public void testGetPropsWithoutSegmentsEntryId() throws Exception {
		try (CompanyConfigurationTemporarySwapper
				companyConfigurationTemporarySwapper =
					new CompanyConfigurationTemporarySwapper(
						TestPropsValues.getCompanyId(),
						SegmentsCompanyConfiguration.class.getName(),
						HashMapDictionaryBuilder.<String, Object>put(
							"segmentationEnabled", true
						).build())) {

			MockLiferayPortletRenderRequest mockLiferayPortletRenderRequest =
				_getMockLiferayPortletRenderRequests();

			mockLiferayPortletRenderRequest.setAttribute(
				WebKeys.USER, TestPropsValues.getUser());
			mockLiferayPortletRenderRequest.setParameter(
				"groupId", String.valueOf(_group.getGroupId()));

			_mvcRenderCommand.render(
				mockLiferayPortletRenderRequest,
				new MockLiferayPortletRenderResponse());

			Map<String, Object> data = ReflectionTestUtil.invoke(
				mockLiferayPortletRenderRequest.getAttribute(_CLASS_NAME),
				"getData", new Class<?>[0]);

			Map<String, Object> props = (Map<String, Object>)data.get("props");

			JSONArray jsonArray = (JSONArray)props.get("contributors");

			for (Object object : jsonArray) {
				JSONObject jsonObject = (JSONObject)object;

				Assert.assertNull(jsonObject.getJSONObject("initialQuery"));
			}

			Assert.assertEquals(
				LocaleUtil.toLanguageId(
					_portal.getSiteDefaultLocale(_group.getGroupId())),
				props.get("defaultLanguageId"));

			String formId = String.valueOf(props.get("formId"));

			Assert.assertTrue(formId.endsWith("editSegmentFm"));

			Assert.assertTrue((boolean)props.get("hasUpdatePermission"));
			Assert.assertEquals(0, (int)props.get("initialMembersCount"));
			Assert.assertFalse((boolean)props.get("initialSegmentActive"));
			Assert.assertNull(props.get("initialSegmentName"));
			Assert.assertTrue((boolean)props.get("isSegmentationEnabled"));
			Assert.assertEquals(
				String.valueOf(
					_portal.getLocale(mockLiferayPortletRenderRequest)),
				props.get("locale"));
			Assert.assertNotNull(props.get("previewMembersURL"));
			Assert.assertNotNull(props.get("redirect"));
			Assert.assertNotNull(props.get("requestMembersCountURL"));
			Assert.assertNotNull(props.get("scopeName"));
			Assert.assertEquals(
				_group.getDescriptiveName(), props.get("scopeName"));
			Assert.assertNotNull(props.get("segmentsConfigurationURL"));
			Assert.assertTrue((boolean)props.get("showInEditMode"));
		}
	}

	private SegmentsEntry _addSegmentEntry(String filterString)
		throws Exception {

		Criteria criteria = new Criteria();

		_userSegmentsCriteriaContributor.contribute(
			criteria, filterString, Criteria.Conjunction.AND);

		return SegmentsTestUtil.addSegmentsEntry(
			_group.getGroupId(), CriteriaSerializer.serialize(criteria));
	}

	private MockLiferayPortletRenderRequest
			_getMockLiferayPortletRenderRequests()
		throws Exception {

		MockLiferayPortletRenderRequest mockLiferayPortletRenderRequest =
			new MockLiferayPortletRenderRequest();

		mockLiferayPortletRenderRequest.setAttribute(
			WebKeys.THEME_DISPLAY, _getThemeDisplay());

		return mockLiferayPortletRenderRequest;
	}

	private ThemeDisplay _getThemeDisplay() throws Exception {
		ThemeDisplay themeDisplay = new ThemeDisplay();

		themeDisplay.setCompany(_company);
		themeDisplay.setLanguageId(LanguageUtil.getLanguageId(LocaleUtil.US));
		themeDisplay.setLocale(LocaleUtil.US);
		themeDisplay.setPermissionChecker(
			PermissionThreadLocal.getPermissionChecker());
		themeDisplay.setScopeGroupId(_group.getGroupId());
		themeDisplay.setSiteGroupId(_group.getGroupId());
		themeDisplay.setUser(TestPropsValues.getUser());

		return themeDisplay;
	}

	private static final String _CLASS_NAME =
		"com.liferay.segments.web.internal.display.context." +
			"EditSegmentsEntryDisplayContext";

	private static Company _company;

	@Inject
	private static CompanyLocalService _companyLocalService;

	@DeleteAfterTestRun
	private Group _group;

	@Inject(
		filter = "mvc.command.name=/segments/edit_segments_entry",
		type = MVCRenderCommand.class
	)
	private MVCRenderCommand _mvcRenderCommand;

	@Inject
	private Portal _portal;

	@Inject(
		filter = "segments.criteria.contributor.key=user",
		type = SegmentsCriteriaContributor.class
	)
	private SegmentsCriteriaContributor _userSegmentsCriteriaContributor;

}