/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.cms.site.initializer.internal.struts.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.depot.constants.DepotConstants;
import com.liferay.depot.model.DepotEntry;
import com.liferay.depot.service.DepotEntryLocalService;
import com.liferay.fragment.service.FragmentEntryLinkLocalService;
import com.liferay.layout.constants.LayoutTypeSettingsConstants;
import com.liferay.layout.page.template.model.LayoutPageTemplateEntry;
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
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactoryUtil;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.struts.StrutsAction;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.context.ContextUserReplace;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.CompanyTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

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
 * @author Mikel Lorza
 */
@RunWith(Arquillian.class)
public class RegenerateStructureDisplayPageStrutsActionTest {

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
	@TestInfo("LPD-102262")
	public void testExecuteWithoutUpdatePermission() throws Exception {
		LayoutPageTemplateEntry layoutPageTemplateEntry =
			DisplayPageTemplateTestUtil.addDisplayPageTemplate(
				_group.getGroupId(),
				_portal.getClassNameId(_objectDefinition.getClassName()), null,
				true, WorkflowConstants.STATUS_APPROVED);

		Layout draftLayout = _layoutLocalService.fetchDraftLayout(
			layoutPageTemplateEntry.getPlid());

		int fragmentEntryLinksCount =
			_fragmentEntryLinkLocalService.getFragmentEntryLinksCountByPlid(
				_group.getGroupId(), draftLayout.getPlid());

		User user = UserTestUtil.addUser(_company);

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				user, PermissionCheckerFactoryUtil.create(user))) {

			_regenerateStructureDisplayPageStrutsAction.execute(
				_getMockHttpServletRequest(draftLayout.getPlid()),
				new MockHttpServletResponse());

			Assert.fail();
		}
		catch (Exception exception) {
			Assert.assertTrue(
				exception instanceof PrincipalException.MustHavePermission);

			Assert.assertTrue(
				StringUtil.startsWith(
					exception.getMessage(),
					"User " + user.getUserId() +
						" must have UPDATE permission for"));
		}

		Assert.assertEquals(
			fragmentEntryLinksCount,
			_fragmentEntryLinkLocalService.getFragmentEntryLinksCountByPlid(
				_group.getGroupId(), draftLayout.getPlid()));

		draftLayout = _layoutLocalService.fetchLayout(draftLayout.getPlid());

		Assert.assertFalse(
			GetterUtil.getBoolean(
				draftLayout.getTypeSettingsProperty(
					LayoutTypeSettingsConstants.KEY_AUTOGENERATED)));
	}

	private MockHttpServletRequest _getMockHttpServletRequest(long plid)
		throws Exception {

		MockHttpServletRequest mockHttpServletRequest =
			ContentLayoutTestUtil.getMockHttpServletRequest(
				_companyLocalService.getCompany(_company.getCompanyId()),
				_group, _layout);

		mockHttpServletRequest.setParameter("plid", String.valueOf(plid));
		mockHttpServletRequest.setRequestURI(_layout.getFriendlyURL());

		return mockHttpServletRequest;
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

	@Inject
	private FragmentEntryLinkLocalService _fragmentEntryLinkLocalService;

	private Group _group;
	private Layout _layout;

	@Inject
	private LayoutLocalService _layoutLocalService;

	private ObjectDefinition _objectDefinition;

	@Inject
	private ObjectDefinitionSettingLocalService
		_objectDefinitionSettingLocalService;

	@Inject
	private Portal _portal;

	@Inject(filter = "path=/cms/regenerate_structure_display_page")
	private StrutsAction _regenerateStructureDisplayPageStrutsAction;

}