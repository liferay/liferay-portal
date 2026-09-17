/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.cms.site.initializer.internal.struts.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.depot.constants.DepotConstants;
import com.liferay.depot.model.DepotEntry;
import com.liferay.depot.service.DepotEntryLocalService;
import com.liferay.fragment.entry.processor.constants.FragmentEntryProcessorConstants;
import com.liferay.fragment.model.FragmentEntryLink;
import com.liferay.fragment.service.FragmentEntryLinkLocalService;
import com.liferay.layout.constants.LayoutTypeSettingsConstants;
import com.liferay.layout.page.template.model.LayoutPageTemplateEntry;
import com.liferay.layout.page.template.test.util.DisplayPageTemplateTestUtil;
import com.liferay.layout.test.util.ContentLayoutTestUtil;
import com.liferay.layout.test.util.LayoutTestUtil;
import com.liferay.object.constants.ObjectDefinitionConstants;
import com.liferay.object.constants.ObjectDefinitionSettingConstants;
import com.liferay.object.constants.ObjectFieldConstants;
import com.liferay.object.constants.ObjectLayoutBoxConstants;
import com.liferay.object.field.util.ObjectFieldUtil;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectField;
import com.liferay.object.model.ObjectLayoutBox;
import com.liferay.object.model.ObjectLayoutColumn;
import com.liferay.object.model.ObjectLayoutRow;
import com.liferay.object.model.ObjectLayoutTab;
import com.liferay.object.service.ObjectDefinitionSettingLocalService;
import com.liferay.object.service.ObjectFieldLocalService;
import com.liferay.object.service.ObjectLayoutLocalService;
import com.liferay.object.service.persistence.ObjectLayoutBoxPersistence;
import com.liferay.object.service.persistence.ObjectLayoutColumnPersistence;
import com.liferay.object.service.persistence.ObjectLayoutRowPersistence;
import com.liferay.object.service.persistence.ObjectLayoutTabPersistence;
import com.liferay.object.test.util.ObjectDefinitionTestUtil;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
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
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextFactory;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.struts.StrutsAction;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.context.ContextUserReplace;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.CompanyTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

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

	@FeatureFlag("LPD-96666")
	@Test
	@TestInfo({"LPD-99448", "LPD-102262"})
	public void testExecute() throws Exception {
		_testExecuteWithObjectLayout();
		_testExecuteWithoutUpdatePermission();
	}

	private ObjectLayoutBox _createObjectLayoutBox(
		boolean collapsable, ObjectField objectField) {

		ObjectLayoutBox objectLayoutBox = _objectLayoutBoxPersistence.create(0);

		objectLayoutBox.setCollapsable(collapsable);
		objectLayoutBox.setNameMap(RandomTestUtil.randomLocaleStringMap());
		objectLayoutBox.setPriority(0);
		objectLayoutBox.setType(ObjectLayoutBoxConstants.TYPE_REGULAR);

		ObjectLayoutColumn objectLayoutColumn =
			_objectLayoutColumnPersistence.create(0);

		objectLayoutColumn.setObjectFieldId(objectField.getObjectFieldId());
		objectLayoutColumn.setPriority(0);
		objectLayoutColumn.setSize(12);

		ObjectLayoutRow objectLayoutRow = _objectLayoutRowPersistence.create(0);

		objectLayoutRow.setPriority(0);
		objectLayoutRow.setObjectLayoutColumns(
			Collections.singletonList(objectLayoutColumn));

		objectLayoutBox.setObjectLayoutRows(
			Collections.singletonList(objectLayoutRow));

		return objectLayoutBox;
	}

	private ObjectLayoutTab _createObjectLayoutTab(
		ObjectLayoutBox... objectLayoutBoxes) {

		for (int i = 0; i < objectLayoutBoxes.length; i++) {
			ObjectLayoutBox objectLayoutBox = objectLayoutBoxes[i];

			objectLayoutBox.setPriority(i);
		}

		ObjectLayoutTab objectLayoutTab = _objectLayoutTabPersistence.create(0);

		objectLayoutTab.setNameMap(RandomTestUtil.randomLocaleStringMap());
		objectLayoutTab.setPriority(0);
		objectLayoutTab.setObjectLayoutBoxes(
			ListUtil.fromArray(objectLayoutBoxes));

		return objectLayoutTab;
	}

	private void _execute(Group group, Layout layout) throws Exception {
		MockHttpServletRequest mockHttpServletRequest =
			_getMockHttpServletRequest(group, layout, layout.getPlid());
		MockHttpServletResponse mockHttpServletResponse =
			new MockHttpServletResponse();

		ThemeDisplay themeDisplay =
			(ThemeDisplay)mockHttpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		themeDisplay.setRequest(mockHttpServletRequest);
		themeDisplay.setResponse(mockHttpServletResponse);

		ServiceContext serviceContext = ServiceContextFactory.getInstance(
			mockHttpServletRequest);

		serviceContext.setRequest(mockHttpServletRequest);

		ServiceContextThreadLocal.pushServiceContext(serviceContext);

		_regenerateStructureDisplayPageStrutsAction.execute(
			mockHttpServletRequest, mockHttpServletResponse);

		ServiceContextThreadLocal.popServiceContext();
	}

	private List<FragmentEntryLink> _getFragmentEntryLinks(
		Group group, Layout layout, String rendererKey) {

		return TransformUtil.transform(
			_fragmentEntryLinkLocalService.getFragmentEntryLinksByPlid(
				group.getGroupId(), layout.getPlid()),
			fragmentEntryLink -> {
				if (Objects.equals(
						rendererKey, fragmentEntryLink.getRendererKey())) {

					return fragmentEntryLink;
				}

				return null;
			});
	}

	private MockHttpServletRequest _getMockHttpServletRequest(
			Group group, Layout layout, long plid)
		throws Exception {

		MockHttpServletRequest mockHttpServletRequest =
			ContentLayoutTestUtil.getMockHttpServletRequest(
				_companyLocalService.getCompany(group.getCompanyId()), group,
				layout);

		mockHttpServletRequest.setParameter("plid", String.valueOf(plid));
		mockHttpServletRequest.setRequestURI(layout.getFriendlyURL());

		return mockHttpServletRequest;
	}

	private void _testExecuteWithObjectLayout() throws Exception {
		Group group = _groupLocalService.getGroup(
			TestPropsValues.getCompanyId(), GroupConstants.CMS);

		ObjectDefinition objectDefinition =
			ObjectDefinitionTestUtil.publishObjectDefinition(
				"A" + RandomTestUtil.randomString(),
				ListUtil.fromArray(
					ObjectFieldUtil.createObjectField(
						ObjectFieldConstants.BUSINESS_TYPE_TEXT,
						ObjectFieldConstants.DB_TYPE_STRING,
						RandomTestUtil.randomString(), "depth"),
					ObjectFieldUtil.createObjectField(
						ObjectFieldConstants.BUSINESS_TYPE_TEXT,
						ObjectFieldConstants.DB_TYPE_STRING,
						RandomTestUtil.randomString(), "height"),
					ObjectFieldUtil.createObjectField(
						ObjectFieldConstants.BUSINESS_TYPE_TEXT,
						ObjectFieldConstants.DB_TYPE_STRING,
						RandomTestUtil.randomString(), "weight"),
					ObjectFieldUtil.createObjectField(
						ObjectFieldConstants.BUSINESS_TYPE_TEXT,
						ObjectFieldConstants.DB_TYPE_STRING,
						RandomTestUtil.randomString(), "width")),
				ObjectDefinitionConstants.SCOPE_DEPOT,
				TestPropsValues.getUserId());

		_objectDefinitionSettingLocalService.addObjectDefinitionSetting(
			objectDefinition.getUserId(),
			objectDefinition.getObjectDefinitionId(),
			ObjectDefinitionSettingConstants.NAME_ACCEPT_ALL_GROUPS,
			StringPool.TRUE);

		LayoutPageTemplateEntry layoutPageTemplateEntry =
			DisplayPageTemplateTestUtil.addDisplayPageTemplate(
				group.getGroupId(),
				_portal.getClassNameId(objectDefinition.getClassName()), null,
				true, WorkflowConstants.STATUS_APPROVED);

		Layout layout = _layoutLocalService.fetchDraftLayout(
			layoutPageTemplateEntry.getPlid());

		_execute(group, layout);

		List<FragmentEntryLink> fragmentEntryLinks = _getFragmentEntryLinks(
			group, layout, _RENDERER_KEY_ACCORDION);

		Assert.assertEquals(
			fragmentEntryLinks.toString(), 0, fragmentEntryLinks.size());

		fragmentEntryLinks = _getFragmentEntryLinks(
			group, layout, _RENDERER_KEY_TABS);

		Assert.assertEquals(
			fragmentEntryLinks.toString(), 0, fragmentEntryLinks.size());

		_objectLayoutLocalService.addObjectLayout(
			TestPropsValues.getUserId(),
			objectDefinition.getObjectDefinitionId(), false,
			RandomTestUtil.randomLocaleStringMap(),
			ListUtil.fromArray(
				_createObjectLayoutTab(
					_createObjectLayoutBox(
						false,
						_objectFieldLocalService.fetchObjectField(
							objectDefinition.getObjectDefinitionId(),
							"depth"))),
				_createObjectLayoutTab(
					_createObjectLayoutBox(
						true,
						_objectFieldLocalService.fetchObjectField(
							objectDefinition.getObjectDefinitionId(),
							"height"))),
				_createObjectLayoutTab(
					_createObjectLayoutBox(
						true,
						_objectFieldLocalService.fetchObjectField(
							objectDefinition.getObjectDefinitionId(),
							"weight")),
					_createObjectLayoutBox(
						true,
						_objectFieldLocalService.fetchObjectField(
							objectDefinition.getObjectDefinitionId(),
							"width")))));

		_execute(group, layout);

		fragmentEntryLinks = _getFragmentEntryLinks(
			group, layout, _RENDERER_KEY_ACCORDION);

		Assert.assertEquals(
			fragmentEntryLinks.toString(), 6, fragmentEntryLinks.size());

		fragmentEntryLinks = _getFragmentEntryLinks(
			group, layout, _RENDERER_KEY_TABS);

		Assert.assertEquals(
			fragmentEntryLinks.toString(), 1, fragmentEntryLinks.size());

		FragmentEntryLink fragmentEntryLink = fragmentEntryLinks.get(0);

		JSONObject jsonObject1 = JSONFactoryUtil.createJSONObject(
			fragmentEntryLink.getEditableValues());

		JSONObject jsonObject2 = jsonObject1.getJSONObject(
			FragmentEntryProcessorConstants.
				KEY_EDITABLE_FRAGMENT_ENTRY_PROCESSOR);

		Assert.assertEquals(3, jsonObject2.length());

		jsonObject2 = jsonObject1.getJSONObject(
			FragmentEntryProcessorConstants.
				KEY_FREEMARKER_FRAGMENT_ENTRY_PROCESSOR);

		Assert.assertEquals("3", jsonObject2.getString("numberOfTabs"));
	}

	private void _testExecuteWithoutUpdatePermission() throws Exception {
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
				_getMockHttpServletRequest(
					_group, _layout, draftLayout.getPlid()),
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

	private static final String _RENDERER_KEY_ACCORDION =
		"BASIC_COMPONENT-accordion";

	private static final String _RENDERER_KEY_TABS = "BASIC_COMPONENT-tabs";

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
	private ObjectFieldLocalService _objectFieldLocalService;

	@Inject
	private ObjectLayoutBoxPersistence _objectLayoutBoxPersistence;

	@Inject
	private ObjectLayoutColumnPersistence _objectLayoutColumnPersistence;

	@Inject
	private ObjectLayoutLocalService _objectLayoutLocalService;

	@Inject
	private ObjectLayoutRowPersistence _objectLayoutRowPersistence;

	@Inject
	private ObjectLayoutTabPersistence _objectLayoutTabPersistence;

	@Inject
	private Portal _portal;

	@Inject(filter = "path=/cms/regenerate_structure_display_page")
	private StrutsAction _regenerateStructureDisplayPageStrutsAction;

}