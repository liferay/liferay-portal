/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.cms.site.initializer.internal.struts.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.layout.page.template.model.LayoutPageTemplateEntry;
import com.liferay.layout.page.template.model.LayoutPageTemplateStructure;
import com.liferay.layout.page.template.service.LayoutPageTemplateEntryLocalService;
import com.liferay.layout.page.template.service.LayoutPageTemplateStructureLocalService;
import com.liferay.layout.test.util.ContentLayoutTestUtil;
import com.liferay.layout.test.util.LayoutTestUtil;
import com.liferay.layout.util.structure.FormStyledLayoutStructureItem;
import com.liferay.layout.util.structure.LayoutStructure;
import com.liferay.object.constants.ObjectDefinitionConstants;
import com.liferay.object.constants.ObjectFieldConstants;
import com.liferay.object.field.util.ObjectFieldUtil;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.object.test.util.ObjectDefinitionTestUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.portlet.constants.FriendlyURLResolverConstants;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.struts.StrutsAction;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.test.rule.FeatureFlags;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.portal.util.PropsValues;

import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

/**
 * @author Lourdes Fernández Besada
 */
@FeatureFlags("LPD-17564")
@RunWith(Arquillian.class)
public class AddStructuredContentItemStrutsActionTest {

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

		_objectDefinition = ObjectDefinitionTestUtil.publishObjectDefinition(
			ListUtil.fromArray(
				ObjectFieldUtil.createObjectField(
					ObjectFieldConstants.BUSINESS_TYPE_TEXT,
					ObjectFieldConstants.DB_TYPE_STRING,
					RandomTestUtil.randomString(), "text")),
			ObjectDefinitionConstants.SCOPE_SITE);

		_objectDefinition.setEnableObjectEntryDraft(true);

		_objectDefinition =
			_objectDefinitionLocalService.updateObjectDefinition(
				_objectDefinition);
	}

	@Test
	@TestInfo({"LPD-50664", "LPD-50665"})
	public void testExecute() throws Exception {
		List<ObjectEntry> objectEntries =
			_objectEntryLocalService.getObjectEntries(
				_group.getGroupId(), _objectDefinition.getObjectDefinitionId(),
				QueryUtil.ALL_POS, QueryUtil.ALL_POS);

		Assert.assertTrue(objectEntries.isEmpty());

		long classNameId = _portal.getClassNameId(
			_objectDefinition.getClassName());
		HttpServletRequest httpServletRequest = _getMockHttpServletRequest();

		_testExecute(classNameId, 1, null, httpServletRequest);
		_testExecute(
			classNameId, 2,
			_layoutPageTemplateEntryLocalService.
				fetchDefaultLayoutPageTemplateEntry(
					_group.getGroupId(), classNameId, 0),
			httpServletRequest);
	}

	private HttpServletRequest _getMockHttpServletRequest() throws Exception {
		MockHttpServletRequest mockHttpServletRequest =
			ContentLayoutTestUtil.getMockHttpServletRequest(
				_companyLocalService.getCompany(TestPropsValues.getCompanyId()),
				_group, _layout);

		mockHttpServletRequest.setParameter(
			"groupId", String.valueOf(_group.getGroupId()));
		mockHttpServletRequest.setParameter(
			"objectDefinitionId",
			String.valueOf(_objectDefinition.getObjectDefinitionId()));
		mockHttpServletRequest.setParameter(
			"plid", String.valueOf(_layout.getPlid()));
		mockHttpServletRequest.setRequestURI(_layout.getFriendlyURL());

		return mockHttpServletRequest;
	}

	private void _testExecute(
			long classNameId, int count,
			LayoutPageTemplateEntry expectedLayoutPageTemplateEntry,
			HttpServletRequest httpServletRequest)
		throws Exception {

		MockHttpServletResponse mockHttpServletResponse =
			new MockHttpServletResponse();

		_addStructuredContentItemStrutsAction.execute(
			httpServletRequest, mockHttpServletResponse);

		String urlSeparator =
			FriendlyURLResolverConstants.URL_SEPARATOR_CUSTOM_ASSET;

		LayoutPageTemplateEntry layoutPageTemplateEntry =
			_layoutPageTemplateEntryLocalService.
				fetchDefaultLayoutPageTemplateEntry(
					_group.getGroupId(), classNameId, 0);

		if (expectedLayoutPageTemplateEntry != null) {
			Assert.assertEquals(
				expectedLayoutPageTemplateEntry.getLayoutPageTemplateEntryId(),
				layoutPageTemplateEntry.getLayoutPageTemplateEntryId());
		}
		else {
			Assert.assertNotNull(layoutPageTemplateEntry);

			LayoutPageTemplateStructure layoutPageTemplateStructure =
				_layoutPageTemplateStructureLocalService.
					fetchLayoutPageTemplateStructure(
						layoutPageTemplateEntry.getGroupId(),
						layoutPageTemplateEntry.getPlid());

			LayoutStructure layoutStructure = LayoutStructure.of(
				layoutPageTemplateStructure.getDefaultSegmentsExperienceData());

			List<FormStyledLayoutStructureItem> formStyledLayoutStructureItems =
				layoutStructure.getFormStyledLayoutStructureItems();

			Assert.assertEquals(
				formStyledLayoutStructureItems.toString(), 1,
				formStyledLayoutStructureItems.size());

			FormStyledLayoutStructureItem formStyledLayoutStructureItem =
				formStyledLayoutStructureItems.get(0);

			Assert.assertEquals(
				_objectDefinition.getClassName(),
				formStyledLayoutStructureItem.getClassName());

			JSONObject itemConfigJSONObject =
				formStyledLayoutStructureItem.getItemConfigJSONObject();

			Assert.assertEquals(
				JSONUtil.put(
					"layout",
					JSONUtil.put(
						"groupId", _layout.getGroupId()
					).put(
						"layoutId", _layout.getLayoutId()
					).put(
						"layoutUuid", _layout.getUuid()
					).put(
						"private", _layout.isPrivateLayout()
					).put(
						"title", _layout.getTitle()
					)
				).put(
					"showNotification", true
				).put(
					"type", "page"
				).toString(),
				String.valueOf(itemConfigJSONObject.get("successMessage")));
		}

		Layout layout = _layoutLocalService.getLayout(
			layoutPageTemplateEntry.getPlid());

		List<ObjectEntry> objectEntries =
			_objectEntryLocalService.getObjectEntries(
				_group.getGroupId(), _objectDefinition.getObjectDefinitionId(),
				QueryUtil.ALL_POS, QueryUtil.ALL_POS);

		Assert.assertEquals(
			objectEntries.toString(), count, objectEntries.size());

		ObjectEntry objectEntry = objectEntries.get(objectEntries.size() - 1);

		Assert.assertTrue(objectEntry.isDraft());

		Assert.assertEquals(
			StringBundler.concat(
				PropsValues.LAYOUT_FRIENDLY_URL_PUBLIC_SERVLET_MAPPING,
				_group.getFriendlyURL(),
				urlSeparator.substring(0, urlSeparator.length() - 1),
				layout.getFriendlyURL(_portal.getSiteDefaultLocale(_group)),
				StringPool.SLASH, classNameId, StringPool.SLASH,
				objectEntry.getObjectEntryId()),
			mockHttpServletResponse.getRedirectedUrl());
	}

	@Inject(filter = "path=/cms/add_structured_content_item")
	private StrutsAction _addStructuredContentItemStrutsAction;

	@Inject
	private CompanyLocalService _companyLocalService;

	@DeleteAfterTestRun
	private Group _group;

	private Layout _layout;

	@Inject
	private LayoutLocalService _layoutLocalService;

	@Inject
	private LayoutPageTemplateEntryLocalService
		_layoutPageTemplateEntryLocalService;

	@Inject
	private LayoutPageTemplateStructureLocalService
		_layoutPageTemplateStructureLocalService;

	@DeleteAfterTestRun
	private ObjectDefinition _objectDefinition;

	@Inject
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	@Inject
	private ObjectEntryLocalService _objectEntryLocalService;

	@Inject
	private Portal _portal;

}