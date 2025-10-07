/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.cms.site.initializer.internal.struts.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.depot.constants.DepotConstants;
import com.liferay.depot.model.DepotEntry;
import com.liferay.depot.service.DepotEntryLocalService;
import com.liferay.layout.constants.LayoutTypeSettingsConstants;
import com.liferay.layout.page.template.model.LayoutPageTemplateEntry;
import com.liferay.layout.page.template.service.LayoutPageTemplateEntryLocalService;
import com.liferay.layout.page.template.test.util.DisplayPageTemplateTestUtil;
import com.liferay.layout.test.util.ContentLayoutTestUtil;
import com.liferay.layout.test.util.LayoutTestUtil;
import com.liferay.object.constants.ObjectDefinitionConstants;
import com.liferay.object.constants.ObjectDefinitionSettingConstants;
import com.liferay.object.constants.ObjectFieldConstants;
import com.liferay.object.field.util.ObjectFieldUtil;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.service.ObjectDefinitionSettingLocalService;
import com.liferay.object.test.util.ObjectDefinitionTestUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.GroupConstants;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.struts.StrutsAction;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.CompanyTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.UnicodeProperties;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import jakarta.servlet.http.HttpServletRequest;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

/**
 * @author Lourdes Fernández Besada
 */
@FeatureFlag("LPD-17564")
@RunWith(Arquillian.class)
public class ResetStructureDisplayPageStrutsActionTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@BeforeClass
	public static void setUpClass() throws Exception {
		_company = CompanyTestUtil.addCompany();

		_user = UserTestUtil.addCompanyAdminUser(_company);

		_groupLocalService.checkSystemGroups(_company.getCompanyId());
	}

	@AfterClass
	public static void tearDownClass() throws Exception {
		_companyLocalService.deleteCompany(_company);
	}

	@Before
	public void setUp() throws Exception {
		_depotEntry = _depotEntryLocalService.addDepotEntry(
			HashMapBuilder.put(
				LocaleUtil.getDefault(), RandomTestUtil.randomString()
			).build(),
			HashMapBuilder.put(
				LocaleUtil.getDefault(), RandomTestUtil.randomString()
			).build(),
			DepotConstants.TYPE_ASSET_LIBRARY,
			ServiceContextTestUtil.getServiceContext());

		_group = _groupLocalService.getGroup(
			_company.getCompanyId(), GroupConstants.CMS);

		_layout = LayoutTestUtil.addTypeContentLayout(_group);

		_objectDefinition = ObjectDefinitionTestUtil.publishObjectDefinition(
			"A" + RandomTestUtil.randomString(),
			ListUtil.fromArray(
				ObjectFieldUtil.createObjectField(
					ObjectFieldConstants.BUSINESS_TYPE_TEXT,
					ObjectFieldConstants.DB_TYPE_STRING,
					RandomTestUtil.randomString(), "text")),
			ObjectDefinitionConstants.SCOPE_DEPOT, _user.getUserId());

		_objectDefinitionSettingLocalService.addObjectDefinitionSetting(
			_objectDefinition.getUserId(),
			_objectDefinition.getObjectDefinitionId(),
			ObjectDefinitionSettingConstants.NAME_ACCEPT_ALL_GROUPS,
			StringPool.TRUE);
	}

	@Test
	@TestInfo("LPD-54517")
	public void testExecute() throws Exception {
		long classNameId = _portal.getClassNameId(
			_objectDefinition.getClassName());
		HttpServletRequest httpServletRequest = _getMockHttpServletRequest();

		_testExecute(classNameId, 0, httpServletRequest);

		LayoutPageTemplateEntry layoutPageTemplateEntry =
			DisplayPageTemplateTestUtil.addDisplayPageTemplate(
				_group.getGroupId(), classNameId, 0, true,
				WorkflowConstants.STATUS_APPROVED);

		_testExecute(
			classNameId, layoutPageTemplateEntry.getLayoutPageTemplateEntryId(),
			httpServletRequest);

		Layout draftLayout = _layoutLocalService.fetchDraftLayout(
			layoutPageTemplateEntry.getPlid());

		UnicodeProperties typeSettingsUnicodeProperties =
			draftLayout.getTypeSettingsProperties();

		typeSettingsUnicodeProperties.put(
			LayoutTypeSettingsConstants.KEY_AUTOGENERATED,
			Boolean.TRUE.toString());

		_layoutLocalService.updateLayout(
			draftLayout.getGroupId(), draftLayout.isPrivateLayout(),
			draftLayout.getLayoutId(),
			typeSettingsUnicodeProperties.toString());

		_testExecute(classNameId, 0, httpServletRequest);
	}

	private HttpServletRequest _getMockHttpServletRequest() throws Exception {
		MockHttpServletRequest mockHttpServletRequest =
			ContentLayoutTestUtil.getMockHttpServletRequest(
				_companyLocalService.getCompany(_company.getCompanyId()),
				_group, _layout);

		mockHttpServletRequest.setParameter(
			"objectDefinitionExternalReferenceCode",
			String.valueOf(_objectDefinition.getExternalReferenceCode()));
		mockHttpServletRequest.setParameter(
			"redirect", RandomTestUtil.randomString());
		mockHttpServletRequest.setRequestURI(_layout.getFriendlyURL());

		return mockHttpServletRequest;
	}

	private void _testExecute(
			long classNameId, long layoutPageTemplateEntryId,
			HttpServletRequest httpServletRequest)
		throws Exception {

		_resetStructureDisplayPageStrutsAction.execute(
			httpServletRequest, new MockHttpServletResponse());

		LayoutPageTemplateEntry layoutPageTemplateEntry =
			_layoutPageTemplateEntryLocalService.
				fetchDefaultLayoutPageTemplateEntry(
					_group.getGroupId(), classNameId, 0);

		if (layoutPageTemplateEntryId > 0) {
			Assert.assertEquals(
				layoutPageTemplateEntryId,
				layoutPageTemplateEntry.getLayoutPageTemplateEntryId());
		}
		else {
			Assert.assertNull(layoutPageTemplateEntry);
		}
	}

	private static Company _company;

	@Inject
	private static CompanyLocalService _companyLocalService;

	@Inject
	private static GroupLocalService _groupLocalService;

	private static User _user;

	private DepotEntry _depotEntry;

	@Inject
	private DepotEntryLocalService _depotEntryLocalService;

	private Group _group;
	private Layout _layout;

	@Inject
	private LayoutLocalService _layoutLocalService;

	@Inject
	private LayoutPageTemplateEntryLocalService
		_layoutPageTemplateEntryLocalService;

	private ObjectDefinition _objectDefinition;

	@Inject
	private ObjectDefinitionSettingLocalService
		_objectDefinitionSettingLocalService;

	@Inject
	private Portal _portal;

	@Inject(filter = "path=/cms/reset_structure_display_page")
	private StrutsAction _resetStructureDisplayPageStrutsAction;

}