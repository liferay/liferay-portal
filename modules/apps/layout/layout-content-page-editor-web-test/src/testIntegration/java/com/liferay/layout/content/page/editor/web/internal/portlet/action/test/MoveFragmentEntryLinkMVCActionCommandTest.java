/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.content.page.editor.web.internal.portlet.action.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.info.field.InfoField;
import com.liferay.info.form.InfoForm;
import com.liferay.info.item.InfoItemServiceRegistry;
import com.liferay.info.item.provider.InfoItemFormProvider;
import com.liferay.layout.provider.LayoutStructureProvider;
import com.liferay.layout.test.util.ContentLayoutTestUtil;
import com.liferay.layout.test.util.LayoutTestUtil;
import com.liferay.layout.util.constants.LayoutDataItemTypeConstants;
import com.liferay.layout.util.structure.ContainerStyledLayoutStructureItem;
import com.liferay.layout.util.structure.FormStyledLayoutStructureItem;
import com.liferay.layout.util.structure.FragmentStyledLayoutStructureItem;
import com.liferay.layout.util.structure.LayoutStructure;
import com.liferay.layout.util.structure.LayoutStructureItem;
import com.liferay.object.constants.ObjectFieldConstants;
import com.liferay.object.field.util.ObjectFieldUtil;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.test.util.ObjectDefinitionTestUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletActionRequest;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletActionResponse;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.JavaConstants;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.segments.service.SegmentsExperienceLocalService;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.mock.web.MockHttpServletRequest;

/**
 * @author Lourdes Fernández Besada
 */
@RunWith(Arquillian.class)
public class MoveFragmentEntryLinkMVCActionCommandTest {

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

		Layout layout = LayoutTestUtil.addTypeContentLayout(_group);

		_layout = layout.fetchDraftLayout();

		_segmentsExperienceId =
			_segmentsExperienceLocalService.fetchDefaultSegmentsExperienceId(
				_layout.getPlid());

		_serviceContext = ServiceContextTestUtil.getServiceContext(
			_group.getGroupId(), TestPropsValues.getUserId());

		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest();

		mockHttpServletRequest.setAttribute(
			JavaConstants.JAVAX_PORTLET_RESPONSE,
			new MockLiferayPortletActionResponse());
		mockHttpServletRequest.setAttribute(WebKeys.LAYOUT, _layout);

		_themeDisplay = ContentLayoutTestUtil.getThemeDisplay(
			_company, _group, _layout);

		_themeDisplay.setRequest(mockHttpServletRequest);

		mockHttpServletRequest.setAttribute(
			WebKeys.THEME_DISPLAY, _themeDisplay);

		_serviceContext.setRequest(mockHttpServletRequest);

		ServiceContextThreadLocal.pushServiceContext(_serviceContext);
	}

	@After
	public void tearDown() {
		ServiceContextThreadLocal.popServiceContext();
	}

	@Test
	@TestInfo("LPD-50957")
	public void testMoveFormStyledLayoutStructureItem() throws Exception {
		ObjectDefinition objectDefinition =
			ObjectDefinitionTestUtil.publishObjectDefinition(
				Collections.singletonList(
					ObjectFieldUtil.createObjectField(
						ObjectFieldConstants.BUSINESS_TYPE_TEXT,
						ObjectFieldConstants.DB_TYPE_STRING, "First Name",
						"firstName")));

		InfoItemFormProvider<?> infoItemFormProvider =
			_infoItemServiceRegistry.getFirstInfoItemService(
				InfoItemFormProvider.class, objectDefinition.getClassName());

		InfoForm infoForm = infoItemFormProvider.getInfoForm(
			StringPool.BLANK, _group.getGroupId());

		List<InfoField<?>> allInfoFields = ListUtil.filter(
			infoForm.getAllInfoFields(), InfoField::isEditable);

		String classNameId = String.valueOf(
			_portal.getClassNameId(objectDefinition.getClassName()));

		JSONObject jsonObject = ContentLayoutTestUtil.addFormToLayout(
			false, classNameId, "0", _layout, _layoutStructureProvider,
			_segmentsExperienceId, allInfoFields.toArray(new InfoField<?>[0]));

		LayoutStructure layoutStructure = (LayoutStructure)jsonObject.get(
			"layoutData");

		FormStyledLayoutStructureItem formStyledLayoutStructureItem =
			(FormStyledLayoutStructureItem)
				layoutStructure.getLayoutStructureItem(
					jsonObject.getString("addedItemId"));

		jsonObject = ContentLayoutTestUtil.addItemToLayout(
			"{}", LayoutDataItemTypeConstants.TYPE_CONTAINER, _layout,
			_layoutStructureProvider, _segmentsExperienceId);

		_testMoveLayoutStructureItem(
			formStyledLayoutStructureItem, jsonObject.getString("addedItemId"));

		layoutStructure = _layoutStructureProvider.getLayoutStructure(
			_layout.getPlid(), _segmentsExperienceId);

		ContainerStyledLayoutStructureItem containerStyledLayoutStructureItem =
			(ContainerStyledLayoutStructureItem)
				layoutStructure.getLayoutStructureItem(
					jsonObject.getString("addedItemId"));

		jsonObject = ContentLayoutTestUtil.addItemToLayout(
			"{}", LayoutDataItemTypeConstants.TYPE_CONTAINER, _layout,
			_layoutStructureProvider, _segmentsExperienceId);

		_testMoveLayoutStructureItem(
			containerStyledLayoutStructureItem,
			jsonObject.getString("addedItemId"));
	}

	@Test
	@TestInfo("LPD-46069")
	public void testMoveInputFragmentEntryLink() throws Exception {
		ObjectDefinition objectDefinition =
			ObjectDefinitionTestUtil.publishObjectDefinition(
				Collections.singletonList(
					ObjectFieldUtil.createObjectField(
						ObjectFieldConstants.BUSINESS_TYPE_TEXT,
						ObjectFieldConstants.DB_TYPE_STRING, "First Name",
						"firstName")));

		InfoItemFormProvider<?> infoItemFormProvider =
			_infoItemServiceRegistry.getFirstInfoItemService(
				InfoItemFormProvider.class, objectDefinition.getClassName());

		InfoForm infoForm = infoItemFormProvider.getInfoForm(
			StringPool.BLANK, _group.getGroupId());

		List<InfoField<?>> allInfoFields = ListUtil.filter(
			infoForm.getAllInfoFields(), InfoField::isEditable);

		String classNameId = String.valueOf(
			_portal.getClassNameId(objectDefinition.getClassName()));

		JSONObject jsonObject = ContentLayoutTestUtil.addFormToLayout(
			false, classNameId, "0", _layout, _layoutStructureProvider,
			_segmentsExperienceId, allInfoFields.toArray(new InfoField<?>[0]));

		FragmentStyledLayoutStructureItem fragmentStyledLayoutStructureItem =
			_getFragmentStyledLayoutStructureItem(
				(LayoutStructure)jsonObject.get("layoutData"));

		jsonObject = ContentLayoutTestUtil.addItemToLayout(
			"{}", LayoutDataItemTypeConstants.TYPE_ROW, _layout,
			_layoutStructureProvider, _segmentsExperienceId);

		_testErrorMessage(
			new String[] {fragmentStyledLayoutStructureItem.getItemId()},
			new String[] {jsonObject.getString("addedItemId")});

		jsonObject = ContentLayoutTestUtil.addFormToLayout(
			false, classNameId, "0", _layout, _layoutStructureProvider,
			_segmentsExperienceId);

		_testMoveLayoutStructureItem(
			fragmentStyledLayoutStructureItem,
			jsonObject.getString("addedItemId"));
	}

	private FragmentStyledLayoutStructureItem
		_getFragmentStyledLayoutStructureItem(LayoutStructure layoutStructure) {

		Map<Long, LayoutStructureItem> fragmentLayoutStructureItems =
			layoutStructure.getFragmentLayoutStructureItems();

		Collection<LayoutStructureItem> layoutStructureItems =
			fragmentLayoutStructureItems.values();

		Iterator<LayoutStructureItem> iterator =
			layoutStructureItems.iterator();

		return (FragmentStyledLayoutStructureItem)iterator.next();
	}

	private MockLiferayPortletActionRequest _getMockLiferayPortletActionRequest(
			String[] itemIds, String[] parentItemIds)
		throws Exception {

		MockLiferayPortletActionRequest mockLiferayPortletActionRequest =
			new MockLiferayPortletActionRequest();

		mockLiferayPortletActionRequest.setAttribute(
			JavaConstants.JAVAX_PORTLET_CONFIG, null);
		mockLiferayPortletActionRequest.setAttribute(WebKeys.LAYOUT, _layout);
		mockLiferayPortletActionRequest.setAttribute(
			WebKeys.THEME_DISPLAY, _themeDisplay);
		mockLiferayPortletActionRequest.setParameter("itemIds", itemIds);
		mockLiferayPortletActionRequest.setParameter(
			"parentItemIds", parentItemIds);
		mockLiferayPortletActionRequest.setParameter(
			"positions", new String[] {"0"});
		mockLiferayPortletActionRequest.setParameter(
			"segmentsExperienceId", String.valueOf(_segmentsExperienceId));

		return mockLiferayPortletActionRequest;
	}

	private void _testErrorMessage(String[] itemIds, String[] parentItemIds)
		throws Exception {

		LayoutStructure layoutStructure =
			_layoutStructureProvider.getLayoutStructure(
				_layout.getPlid(), _segmentsExperienceId);

		List<LayoutStructureItem> layoutStructureItems =
			layoutStructure.getLayoutStructureItems();

		MockLiferayPortletActionRequest mockLiferayPortletActionRequest =
			_getMockLiferayPortletActionRequest(itemIds, parentItemIds);

		try {
			ReflectionTestUtil.invoke(
				_mvcActionCommand, "doTransactionalCommand",
				new Class<?>[] {ActionRequest.class, ActionResponse.class},
				mockLiferayPortletActionRequest,
				new MockLiferayPortletActionResponse());

			Assert.fail();
		}
		catch (Exception exception) {
			JSONObject jsonObject = ReflectionTestUtil.invoke(
				_mvcActionCommand, "processException",
				new Class<?>[] {ActionRequest.class, Exception.class},
				mockLiferayPortletActionRequest, exception);

			Assert.assertEquals(jsonObject.toString(), 1, jsonObject.length());
			Assert.assertEquals(
				_language.get(
					_portal.getSiteDefaultLocale(_group),
					"this-form-component-can-only-be-placed-inside-a-mapped-" +
						"form-container"),
				jsonObject.getString("error"));
		}

		layoutStructure = _layoutStructureProvider.getLayoutStructure(
			_layout.getPlid(), _segmentsExperienceId);

		List<LayoutStructureItem> curLayoutStructureItems =
			layoutStructure.getLayoutStructureItems();

		Assert.assertEquals(
			curLayoutStructureItems.toString(), layoutStructureItems.size(),
			curLayoutStructureItems.size());
	}

	private void _testMoveLayoutStructureItem(
			LayoutStructureItem layoutStructureItem, String parentItemId)
		throws Exception {

		LayoutStructure layoutStructure =
			_layoutStructureProvider.getLayoutStructure(
				_layout.getPlid(), _segmentsExperienceId);

		LayoutStructureItem originalParentLayoutStructureItem =
			layoutStructure.getLayoutStructureItem(
				layoutStructureItem.getParentItemId());

		List<String> originalChildrenItemIds =
			originalParentLayoutStructureItem.getChildrenItemIds();

		LayoutStructureItem parentLayoutStructureItem =
			layoutStructure.getLayoutStructureItem(parentItemId);

		List<String> childrenItemIds =
			parentLayoutStructureItem.getChildrenItemIds();

		ReflectionTestUtil.invoke(
			_mvcActionCommand, "doTransactionalCommand",
			new Class<?>[] {ActionRequest.class, ActionResponse.class},
			_getMockLiferayPortletActionRequest(
				new String[] {layoutStructureItem.getItemId()},
				new String[] {parentLayoutStructureItem.getItemId()}),
			new MockLiferayPortletActionResponse());

		layoutStructure = _layoutStructureProvider.getLayoutStructure(
			_layout.getPlid(), _segmentsExperienceId);

		LayoutStructureItem curLayoutStructureItem =
			layoutStructure.getLayoutStructureItem(
				layoutStructureItem.getItemId());

		Assert.assertEquals(
			parentLayoutStructureItem.getItemId(),
			curLayoutStructureItem.getParentItemId());

		LayoutStructureItem curParentLayoutStructureItem =
			layoutStructure.getLayoutStructureItem(
				parentLayoutStructureItem.getItemId());

		List<String> curChildrenItemIds =
			curParentLayoutStructureItem.getChildrenItemIds();

		Assert.assertEquals(
			curChildrenItemIds.toString(), childrenItemIds.size() + 1,
			curChildrenItemIds.size());
		Assert.assertTrue(
			curChildrenItemIds.toString(),
			curChildrenItemIds.contains(layoutStructureItem.getItemId()));

		originalParentLayoutStructureItem =
			layoutStructure.getLayoutStructureItem(
				originalParentLayoutStructureItem.getItemId());

		curChildrenItemIds =
			originalParentLayoutStructureItem.getChildrenItemIds();

		Assert.assertEquals(
			curChildrenItemIds.toString(), originalChildrenItemIds.size() - 1,
			curChildrenItemIds.size());
		Assert.assertFalse(
			curChildrenItemIds.toString(),
			curChildrenItemIds.contains(layoutStructureItem.getItemId()));
	}

	private Company _company;

	@Inject
	private CompanyLocalService _companyLocalService;

	@DeleteAfterTestRun
	private Group _group;

	@Inject
	private InfoItemServiceRegistry _infoItemServiceRegistry;

	@Inject
	private Language _language;

	private Layout _layout;

	@Inject
	private LayoutStructureProvider _layoutStructureProvider;

	@Inject(
		filter = "mvc.command.name=/layout_content_page_editor/move_fragment_entry_link"
	)
	private MVCActionCommand _mvcActionCommand;

	@Inject
	private Portal _portal;

	private long _segmentsExperienceId;

	@Inject
	private SegmentsExperienceLocalService _segmentsExperienceLocalService;

	private ServiceContext _serviceContext;
	private ThemeDisplay _themeDisplay;

}