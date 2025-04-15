/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.content.page.editor.web.internal.portlet.action.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.fragment.constants.FragmentConstants;
import com.liferay.fragment.entry.processor.constants.FragmentEntryProcessorConstants;
import com.liferay.fragment.listener.FragmentEntryLinkListener;
import com.liferay.fragment.listener.FragmentEntryLinkListenerRegistry;
import com.liferay.fragment.model.FragmentCollection;
import com.liferay.fragment.model.FragmentEntry;
import com.liferay.fragment.model.FragmentEntryLink;
import com.liferay.fragment.service.FragmentCollectionLocalService;
import com.liferay.fragment.service.FragmentEntryLinkLocalService;
import com.liferay.fragment.service.FragmentEntryLocalService;
import com.liferay.info.field.InfoField;
import com.liferay.info.form.InfoForm;
import com.liferay.info.item.InfoItemServiceRegistry;
import com.liferay.info.item.provider.InfoItemFormProvider;
import com.liferay.layout.content.page.editor.web.internal.portlet.constants.LayoutContentPageEditorWebPortletKeys;
import com.liferay.layout.page.template.service.LayoutPageTemplateStructureLocalService;
import com.liferay.layout.provider.LayoutStructureProvider;
import com.liferay.layout.test.util.ContentLayoutTestUtil;
import com.liferay.layout.test.util.LayoutTestUtil;
import com.liferay.layout.util.constants.LayoutDataItemTypeConstants;
import com.liferay.layout.util.structure.FormStyledLayoutStructureItem;
import com.liferay.layout.util.structure.FragmentDropZoneLayoutStructureItem;
import com.liferay.layout.util.structure.FragmentStyledLayoutStructureItem;
import com.liferay.layout.util.structure.LayoutStructure;
import com.liferay.layout.util.structure.LayoutStructureItem;
import com.liferay.object.constants.ObjectFieldConstants;
import com.liferay.object.field.util.ObjectFieldUtil;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.test.util.ObjectDefinitionTestUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
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
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.JavaConstants;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
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
import java.util.Objects;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.mock.web.MockHttpServletRequest;

/**
 * @author Georgel Pop
 */
@RunWith(Arquillian.class)
public class CopyItemsMVCActionCommandTest {

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
	public void testCopyDropZoneFragmentEntryLink() throws Exception {
		FragmentEntryLink dropzoneFragmentEntryLink = _addFragmentEntryLink(
			"{}",
			"<lfr-drop-zone " +
				"data-lfr-drop-zone-id=${fragmentEntryLinkNamespace}>" +
					"</lfr-drop-zone>",
			null);

		LayoutStructure layoutStructure =
			_layoutStructureProvider.getLayoutStructure(
				_layout.getPlid(), _segmentsExperienceId);

		FragmentStyledLayoutStructureItem
			dropZoneFragmentStyledLayoutStructureItem =
				_assertFragmentStyledLayoutStructureItem(
					layoutStructure.getLayoutStructureItemByFragmentEntryLinkId(
						dropzoneFragmentEntryLink.getFragmentEntryLinkId()));

		FragmentDropZoneLayoutStructureItem
			fragmentDropZoneLayoutStructureItem =
				_assertFragmentDropZoneLayoutStructureItem(
					layoutStructure, dropZoneFragmentStyledLayoutStructureItem);

		FragmentEntryLink headingFragmentEntryLink = _addFragmentEntryLink(
			JSONUtil.put(
				FragmentEntryProcessorConstants.
					KEY_EDITABLE_FRAGMENT_ENTRY_PROCESSOR,
				JSONUtil.put(
					"element-text",
					JSONUtil.put(
						LocaleUtil.toLanguageId(
							_portal.getSiteDefaultLocale(_group)),
						RandomTestUtil.randomString()))
			).toString(),
			"<h1 data-lfr-editable-id=\"element-text\" " +
				"data-lfr-editable-type=\"text\">Heading Example</h1>",
			fragmentDropZoneLayoutStructureItem.getItemId());

		layoutStructure = _layoutStructureProvider.getLayoutStructure(
			_layout.getPlid(), _segmentsExperienceId);

		FragmentStyledLayoutStructureItem
			headingFragmentStyledLayoutStructureItem =
				_assertFragmentStyledLayoutStructureItem(
					layoutStructure.getLayoutStructureItemByFragmentEntryLinkId(
						headingFragmentEntryLink.getFragmentEntryLinkId()));

		Assert.assertEquals(
			fragmentDropZoneLayoutStructureItem.getItemId(),
			headingFragmentStyledLayoutStructureItem.getParentItemId());

		JSONObject jsonObject = ReflectionTestUtil.invoke(
			_mvcActionCommand, "doTransactionalCommand",
			new Class<?>[] {ActionRequest.class, ActionResponse.class},
			_getMockLiferayPortletActionRequest(
				new String[] {
					dropZoneFragmentStyledLayoutStructureItem.getItemId()
				},
				dropZoneFragmentStyledLayoutStructureItem.getItemId()),
			new MockLiferayPortletActionResponse());

		List<String> copiedItemIds = (List<String>)jsonObject.get(
			"copiedItemIds");

		String copiedItemId = copiedItemIds.get(0);

		Assert.assertNotNull(copiedItemId);

		layoutStructure = _layoutStructureProvider.getLayoutStructure(
			_layout.getPlid(), _segmentsExperienceId);

		FragmentStyledLayoutStructureItem
			copiedDropZoneFragmentStyledLayoutStructureItem =
				_assertFragmentStyledLayoutStructureItem(
					layoutStructure.getLayoutStructureItem(copiedItemId));

		FragmentEntryLink copiedDropzoneFragmentEntryLink =
			_fragmentEntryLinkLocalService.getFragmentEntryLink(
				copiedDropZoneFragmentStyledLayoutStructureItem.
					getFragmentEntryLinkId());

		_assertCopiedFragmentEntryLink(
			copiedDropzoneFragmentEntryLink, dropzoneFragmentEntryLink);

		FragmentDropZoneLayoutStructureItem
			copiedFragmentDropZoneLayoutStructureItem =
				_assertFragmentDropZoneLayoutStructureItem(
					layoutStructure,
					copiedDropZoneFragmentStyledLayoutStructureItem);

		Assert.assertEquals(
			copiedDropzoneFragmentEntryLink.getNamespace(),
			copiedFragmentDropZoneLayoutStructureItem.getFragmentDropZoneId());

		FragmentStyledLayoutStructureItem
			copiedHeadingFragmentStyledLayoutStructureItem =
				_assertFragmentStyledLayoutStructureItem(
					_assertChildrenItems(
						layoutStructure,
						copiedFragmentDropZoneLayoutStructureItem));

		_assertCopiedFragmentEntryLink(
			_fragmentEntryLinkLocalService.getFragmentEntryLink(
				copiedHeadingFragmentStyledLayoutStructureItem.
					getFragmentEntryLinkId()),
			headingFragmentEntryLink);
	}

	@Test
	@TestInfo("LPD-50957")
	public void testCopyFormStyledLayoutStructureItem() throws Exception {
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

		String formStyledLayoutStructureItemId = jsonObject.getString(
			"addedItemId");

		jsonObject = ContentLayoutTestUtil.addItemToLayout(
			"{}", LayoutDataItemTypeConstants.TYPE_CONTAINER, _layout,
			_layoutStructureProvider, _segmentsExperienceId);

		LayoutStructure layoutStructure =
			_layoutStructureProvider.getLayoutStructure(
				_layout.getPlid(), _segmentsExperienceId);

		List<LayoutStructureItem> layoutStructureItems =
			layoutStructure.getLayoutStructureItems();

		String parentItemId = jsonObject.getString("addedItemId");

		ReflectionTestUtil.invoke(
			_mvcActionCommand, "doTransactionalCommand",
			new Class<?>[] {ActionRequest.class, ActionResponse.class},
			_getMockLiferayPortletActionRequest(
				new String[] {formStyledLayoutStructureItemId}, parentItemId),
			new MockLiferayPortletActionResponse());

		layoutStructure = _layoutStructureProvider.getLayoutStructure(
			_layout.getPlid(), _segmentsExperienceId);

		List<LayoutStructureItem> curLayoutStructureItems =
			layoutStructure.getLayoutStructureItems();

		Assert.assertEquals(
			curLayoutStructureItems.toString(), layoutStructureItems.size() + 2,
			curLayoutStructureItems.size());

		List<FormStyledLayoutStructureItem> curFormStyledLayoutStructureItems =
			layoutStructure.getFormStyledLayoutStructureItems();

		Assert.assertEquals(
			curFormStyledLayoutStructureItems.toString(), 2,
			curFormStyledLayoutStructureItems.size());

		jsonObject = ContentLayoutTestUtil.addItemToLayout(
			"{}", LayoutDataItemTypeConstants.TYPE_CONTAINER, _layout,
			_layoutStructureProvider, _segmentsExperienceId);

		ReflectionTestUtil.invoke(
			_mvcActionCommand, "doTransactionalCommand",
			new Class<?>[] {ActionRequest.class, ActionResponse.class},
			_getMockLiferayPortletActionRequest(
				new String[] {parentItemId},
				jsonObject.getString("addedItemId")),
			new MockLiferayPortletActionResponse());

		layoutStructure = _layoutStructureProvider.getLayoutStructure(
			_layout.getPlid(), _segmentsExperienceId);

		curLayoutStructureItems = layoutStructure.getLayoutStructureItems();

		Assert.assertEquals(
			curLayoutStructureItems.toString(), layoutStructureItems.size() + 6,
			curLayoutStructureItems.size());

		curFormStyledLayoutStructureItems =
			layoutStructure.getFormStyledLayoutStructureItems();

		Assert.assertEquals(
			curFormStyledLayoutStructureItems.toString(), 3,
			curFormStyledLayoutStructureItems.size());
	}

	@Test
	@TestInfo("LPD-45944")
	public void testCopyInputFragmentEntryLink() throws Exception {
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

		String[] itemIds = {fragmentStyledLayoutStructureItem.getItemId()};

		_testErrorMessage(
			_language.get(
				_portal.getSiteDefaultLocale(_group),
				"this-form-component-can-only-be-placed-inside-a-mapped-form-" +
					"container"),
			itemIds, jsonObject.getString("addedItemId"));

		jsonObject = ContentLayoutTestUtil.addFormToLayout(
			false, classNameId, "0", _layout, _layoutStructureProvider,
			_segmentsExperienceId);

		LayoutStructure layoutStructure =
			_layoutStructureProvider.getLayoutStructure(
				_layout.getPlid(), _segmentsExperienceId);

		List<LayoutStructureItem> layoutStructureItems =
			layoutStructure.getLayoutStructureItems();

		ReflectionTestUtil.invoke(
			_mvcActionCommand, "doTransactionalCommand",
			new Class<?>[] {ActionRequest.class, ActionResponse.class},
			_getMockLiferayPortletActionRequest(
				itemIds, jsonObject.getString("addedItemId")),
			new MockLiferayPortletActionResponse());

		layoutStructure = _layoutStructureProvider.getLayoutStructure(
			_layout.getPlid(), _segmentsExperienceId);

		List<LayoutStructureItem> curLayoutStructureItems =
			layoutStructure.getLayoutStructureItems();

		Assert.assertEquals(
			curLayoutStructureItems.toString(),
			layoutStructureItems.size() + itemIds.length,
			curLayoutStructureItems.size());
	}

	@Test
	public void testCopyMultipleItems() throws Exception {
		LayoutStructure layoutStructure =
			_layoutStructureProvider.getLayoutStructure(
				_layout.getPlid(), _segmentsExperienceId);

		LayoutStructureItem rowStyledLayoutStructureItem1 =
			layoutStructure.addRowStyledLayoutStructureItem(
				layoutStructure.getMainItemId(), 0, 1);

		LayoutStructureItem rowStyledLayoutStructureItem2 =
			layoutStructure.addRowStyledLayoutStructureItem(
				layoutStructure.getMainItemId(), 0, 1);

		LayoutStructureItem rowStyledLayoutStructureItem3 =
			layoutStructure.addRowStyledLayoutStructureItem(
				layoutStructure.getMainItemId(), 0, 1);

		_layoutPageTemplateStructureLocalService.
			updateLayoutPageTemplateStructureData(
				_layout.getGroupId(), _layout.getPlid(),
				layoutStructure.toString());

		JSONObject jsonObject = ReflectionTestUtil.invoke(
			_mvcActionCommand, "doTransactionalCommand",
			new Class<?>[] {ActionRequest.class, ActionResponse.class},
			_getMockLiferayPortletActionRequest(
				new String[] {
					rowStyledLayoutStructureItem1.getItemId(),
					rowStyledLayoutStructureItem2.getItemId()
				},
				rowStyledLayoutStructureItem3.getItemId()),
			new MockLiferayPortletActionResponse());

		List<String> copiedItemIds = (List<String>)jsonObject.get(
			"copiedItemIds");

		Assert.assertEquals(copiedItemIds.toString(), 2, copiedItemIds.size());

		JSONObject layoutDataJSONObject = jsonObject.getJSONObject(
			"layoutData");

		layoutStructure = LayoutStructure.of(layoutDataJSONObject.toString());

		LayoutStructureItem mainLayoutStructureItem =
			layoutStructure.getLayoutStructureItem(
				layoutStructure.getMainItemId());

		List<String> childrenItemIds =
			mainLayoutStructureItem.getChildrenItemIds();

		Assert.assertEquals(
			childrenItemIds.toString(), 5, childrenItemIds.size());
	}

	@Test
	@TestInfo("LPD-37704")
	public void testCopyPortletFragmentEntryLink() throws Exception {
		_testCopyPortletFragmentEntryLink(
			3,
			_addFragmentStyledLayoutStructureItem(
				LayoutContentPageEditorWebPortletKeys.
					LAYOUT_CONTENT_PAGE_EDITOR_WEB_TEST_PORTLET));

		FragmentStyledLayoutStructureItem fragmentStyledLayoutStructureItem =
			_addFragmentStyledLayoutStructureItem(
				LayoutContentPageEditorWebPortletKeys.
					LAYOUT_CONTENT_PAGE_EDITOR_WEB_NONINSTANCEABLE_TEST_PORTLET);

		_testErrorMessage(
			_language.format(
				_portal.getSiteDefaultLocale(_group),
				"the-layout-could-not-be-copied-because-it-contains-a-widget-" +
					"x-that-can-only-appear-once-on-the-page",
				"Noninstanciable Test"),
			new String[] {fragmentStyledLayoutStructureItem.getItemId()},
			_addRowStyledLayoutStructureItem());

		ContentLayoutTestUtil.markItemForDeletionFromLayout(
			fragmentStyledLayoutStructureItem.getItemId(), _layout,
			LayoutContentPageEditorWebPortletKeys.
				LAYOUT_CONTENT_PAGE_EDITOR_WEB_NONINSTANCEABLE_TEST_PORTLET);

		_testCopyPortletFragmentEntryLink(6, fragmentStyledLayoutStructureItem);
	}

	private FragmentEntryLink _addFragmentEntryLink(
			String editableValues, String html, String parentItemId)
		throws Exception {

		FragmentCollection fragmentCollection =
			_fragmentCollectionLocalService.addFragmentCollection(
				null, TestPropsValues.getUserId(), _group.getGroupId(),
				StringUtil.randomString(), StringPool.BLANK, _serviceContext);

		FragmentEntry fragmentEntry =
			_fragmentEntryLocalService.addFragmentEntry(
				null, TestPropsValues.getUserId(), _group.getGroupId(),
				fragmentCollection.getFragmentCollectionId(),
				RandomTestUtil.randomString(), RandomTestUtil.randomString(),
				StringPool.BLANK, html, StringPool.BLANK, false,
				StringPool.BLANK, null, 0, false, false,
				FragmentConstants.TYPE_COMPONENT, null,
				WorkflowConstants.STATUS_APPROVED, _serviceContext);

		FragmentEntryLink fragmentEntryLink =
			ContentLayoutTestUtil.addFragmentEntryLinkToLayout(
				editableValues, fragmentEntry.getCss(),
				fragmentEntry.getConfiguration(),
				fragmentEntry.getFragmentEntryId(), fragmentEntry.getHtml(),
				fragmentEntry.getJs(), _layout,
				fragmentEntry.getFragmentEntryKey(), fragmentEntry.getType(),
				parentItemId, 0, _segmentsExperienceId);

		for (FragmentEntryLinkListener fragmentEntryLinkListener :
				_fragmentEntryLinkListenerRegistry.
					getFragmentEntryLinkListeners()) {

			fragmentEntryLinkListener.onAddFragmentEntryLink(fragmentEntryLink);
		}

		return fragmentEntryLink;
	}

	private FragmentStyledLayoutStructureItem
			_addFragmentStyledLayoutStructureItem(String portletId)
		throws Exception {

		JSONObject jsonObject = ContentLayoutTestUtil.addPortletToLayout(
			_layout, portletId);

		LayoutStructure layoutStructure =
			_layoutStructureProvider.getLayoutStructure(
				_layout.getPlid(), _segmentsExperienceId);

		Map<Long, LayoutStructureItem> fragmentLayoutStructureItems =
			layoutStructure.getFragmentLayoutStructureItems();

		JSONObject fragmentEntryLinkJSONObject = jsonObject.getJSONObject(
			"fragmentEntryLink");

		return (FragmentStyledLayoutStructureItem)
			fragmentLayoutStructureItems.get(
				GetterUtil.getLong(
					fragmentEntryLinkJSONObject.getString(
						"fragmentEntryLinkId")));
	}

	private String _addRowStyledLayoutStructureItem() throws Exception {
		JSONObject jsonObject = ContentLayoutTestUtil.addItemToLayout(
			"{}", LayoutDataItemTypeConstants.TYPE_ROW, _layout,
			_layoutStructureProvider, _segmentsExperienceId);

		return jsonObject.getString("addedItemId");
	}

	private LayoutStructureItem _assertChildrenItems(
		LayoutStructure layoutStructure,
		LayoutStructureItem layoutStructureItem) {

		List<String> childrenItemIds = layoutStructureItem.getChildrenItemIds();

		Assert.assertEquals(
			childrenItemIds.toString(), 1, childrenItemIds.size());

		String childrenItemId = childrenItemIds.get(0);

		LayoutStructureItem childLayoutStructureItem =
			layoutStructure.getLayoutStructureItem(childrenItemId);

		Assert.assertNotNull(childLayoutStructureItem);

		return childLayoutStructureItem;
	}

	private void _assertCopiedEditableValues(
			FragmentEntryLink copiedFragmentEntryLink,
			FragmentEntryLink fragmentEntryLink)
		throws Exception {

		JSONObject jsonObject = _jsonFactory.createJSONObject(
			fragmentEntryLink.getEditableValues());
		JSONObject copiedJSONObject = _jsonFactory.createJSONObject(
			copiedFragmentEntryLink.getEditableValues());

		Assert.assertEquals(jsonObject.length(), copiedJSONObject.length());

		for (String key : jsonObject.keySet()) {
			if (Objects.equals(key, "instanceId") &&
				Objects.equals(
					jsonObject.get(key), fragmentEntryLink.getNamespace())) {

				Assert.assertEquals(
					copiedFragmentEntryLink.getNamespace(),
					copiedJSONObject.get(key));
			}
			else if (jsonObject.get(key) instanceof JSONObject) {
				Assert.assertTrue(
					JSONUtil.toString(copiedJSONObject.getJSONObject(key)),
					JSONUtil.equals(
						jsonObject.getJSONObject(key),
						copiedJSONObject.getJSONObject(key)));
			}
			else {
				Assert.assertEquals(
					jsonObject.get(key), copiedJSONObject.get(key));
			}
		}
	}

	private void _assertCopiedFragmentEntryLink(
			FragmentEntryLink copiedFragmentEntryLink,
			FragmentEntryLink fragmentEntryLink)
		throws Exception {

		Assert.assertEquals(
			fragmentEntryLink.getFragmentEntryId(),
			copiedFragmentEntryLink.getFragmentEntryId());
		Assert.assertNotEquals(
			copiedFragmentEntryLink.getFragmentEntryLinkId(),
			fragmentEntryLink.getFragmentEntryLinkId());
		Assert.assertEquals(
			fragmentEntryLink.getHtml(), copiedFragmentEntryLink.getHtml());
		Assert.assertNotEquals(
			fragmentEntryLink.getNamespace(),
			copiedFragmentEntryLink.getNamespace());
		Assert.assertEquals(
			0, copiedFragmentEntryLink.getOriginalFragmentEntryLinkId());
		Assert.assertEquals(
			copiedFragmentEntryLink.getType(),
			copiedFragmentEntryLink.getType());

		_assertCopiedEditableValues(copiedFragmentEntryLink, fragmentEntryLink);
	}

	private FragmentDropZoneLayoutStructureItem
			_assertFragmentDropZoneLayoutStructureItem(
				LayoutStructure layoutStructure,
				FragmentStyledLayoutStructureItem
					fragmentStyledLayoutStructureItem)
		throws Exception {

		FragmentEntryLink fragmentEntryLink =
			_fragmentEntryLinkLocalService.getFragmentEntryLink(
				fragmentStyledLayoutStructureItem.getFragmentEntryLinkId());

		Assert.assertNotNull(fragmentEntryLink);

		LayoutStructureItem childLayoutStructureItem = _assertChildrenItems(
			layoutStructure, fragmentStyledLayoutStructureItem);

		Assert.assertTrue(
			childLayoutStructureItem instanceof
				FragmentDropZoneLayoutStructureItem);

		FragmentDropZoneLayoutStructureItem
			fragmentDropZoneLayoutStructureItem =
				(FragmentDropZoneLayoutStructureItem)childLayoutStructureItem;

		Assert.assertEquals(
			fragmentEntryLink.getNamespace(),
			fragmentDropZoneLayoutStructureItem.getFragmentDropZoneId());

		return fragmentDropZoneLayoutStructureItem;
	}

	private FragmentStyledLayoutStructureItem
		_assertFragmentStyledLayoutStructureItem(
			LayoutStructureItem layoutStructureItem) {

		Assert.assertNotNull(layoutStructureItem);
		Assert.assertTrue(
			layoutStructureItem instanceof FragmentStyledLayoutStructureItem);

		return (FragmentStyledLayoutStructureItem)layoutStructureItem;
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
			String[] itemIds, String parentItemId)
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
			"parentItemId", parentItemId);
		mockLiferayPortletActionRequest.setParameter(
			"segmentsExperienceId", String.valueOf(_segmentsExperienceId));

		return mockLiferayPortletActionRequest;
	}

	private void _testCopyPortletFragmentEntryLink(
			int count,
			FragmentStyledLayoutStructureItem fragmentStyledLayoutStructureItem)
		throws Exception {

		JSONObject jsonObject = ReflectionTestUtil.invoke(
			_mvcActionCommand, "doTransactionalCommand",
			new Class<?>[] {ActionRequest.class, ActionResponse.class},
			_getMockLiferayPortletActionRequest(
				new String[] {fragmentStyledLayoutStructureItem.getItemId()},
				_addRowStyledLayoutStructureItem()),
			new MockLiferayPortletActionResponse());

		List<String> copiedItemIds = (List<String>)jsonObject.get(
			"copiedItemIds");

		Assert.assertEquals(copiedItemIds.toString(), 1, copiedItemIds.size());

		JSONObject layoutDataJSONObject = jsonObject.getJSONObject(
			"layoutData");

		LayoutStructure layoutStructure = LayoutStructure.of(
			layoutDataJSONObject.toString());

		FragmentStyledLayoutStructureItem
			copiedFragmentStyledLayoutStructureItem =
				(FragmentStyledLayoutStructureItem)
					layoutStructure.getLayoutStructureItem(
						copiedItemIds.get(0));

		FragmentEntryLink fragmentEntryLink =
			_fragmentEntryLinkLocalService.getFragmentEntryLink(
				fragmentStyledLayoutStructureItem.getFragmentEntryLinkId());

		Assert.assertEquals(
			FragmentConstants.TYPE_PORTLET, fragmentEntryLink.getType());

		_assertCopiedFragmentEntryLink(
			_fragmentEntryLinkLocalService.getFragmentEntryLink(
				copiedFragmentStyledLayoutStructureItem.
					getFragmentEntryLinkId()),
			fragmentEntryLink);

		LayoutStructureItem mainLayoutStructureItem =
			layoutStructure.getLayoutStructureItem(
				layoutStructure.getMainItemId());

		List<String> childrenItemIds =
			mainLayoutStructureItem.getChildrenItemIds();

		Assert.assertEquals(
			childrenItemIds.toString(), count, childrenItemIds.size());
	}

	private void _testErrorMessage(
			String error, String[] itemIds, String parentItemId)
		throws Exception {

		LayoutStructure layoutStructure =
			_layoutStructureProvider.getLayoutStructure(
				_layout.getPlid(), _segmentsExperienceId);

		List<LayoutStructureItem> layoutStructureItems =
			layoutStructure.getLayoutStructureItems();

		MockLiferayPortletActionRequest mockLiferayPortletActionRequest =
			_getMockLiferayPortletActionRequest(itemIds, parentItemId);

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
			Assert.assertEquals(error, jsonObject.getString("error"));
		}

		layoutStructure = _layoutStructureProvider.getLayoutStructure(
			_layout.getPlid(), _segmentsExperienceId);

		List<LayoutStructureItem> curLayoutStructureItems =
			layoutStructure.getLayoutStructureItems();

		Assert.assertEquals(
			curLayoutStructureItems.toString(), layoutStructureItems.size(),
			curLayoutStructureItems.size());
	}

	private Company _company;

	@Inject
	private CompanyLocalService _companyLocalService;

	@Inject
	private FragmentCollectionLocalService _fragmentCollectionLocalService;

	@Inject
	private FragmentEntryLinkListenerRegistry
		_fragmentEntryLinkListenerRegistry;

	@Inject
	private FragmentEntryLinkLocalService _fragmentEntryLinkLocalService;

	@Inject
	private FragmentEntryLocalService _fragmentEntryLocalService;

	@DeleteAfterTestRun
	private Group _group;

	@Inject
	private InfoItemServiceRegistry _infoItemServiceRegistry;

	@Inject
	private JSONFactory _jsonFactory;

	@Inject
	private Language _language;

	private Layout _layout;

	@Inject
	private LayoutPageTemplateStructureLocalService
		_layoutPageTemplateStructureLocalService;

	@Inject
	private LayoutStructureProvider _layoutStructureProvider;

	@Inject(filter = "mvc.command.name=/layout_content_page_editor/copy_items")
	private MVCActionCommand _mvcActionCommand;

	@Inject
	private Portal _portal;

	private long _segmentsExperienceId;

	@Inject
	private SegmentsExperienceLocalService _segmentsExperienceLocalService;

	private ServiceContext _serviceContext;
	private ThemeDisplay _themeDisplay;

}