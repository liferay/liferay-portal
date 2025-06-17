/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.content.page.editor.web.internal.portlet.action.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.asset.kernel.model.AssetEntry;
import com.liferay.asset.kernel.service.AssetEntryLocalService;
import com.liferay.data.engine.rest.resource.v2_0.DataDefinitionResource;
import com.liferay.dynamic.data.mapping.form.field.type.constants.DDMFormFieldTypeConstants;
import com.liferay.dynamic.data.mapping.model.DDMFormField;
import com.liferay.dynamic.data.mapping.model.LocalizedValue;
import com.liferay.dynamic.data.mapping.util.DDMFormValuesToFieldsConverter;
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
import com.liferay.journal.constants.JournalContentPortletKeys;
import com.liferay.journal.model.JournalArticle;
import com.liferay.journal.test.util.JournalTestUtil;
import com.liferay.journal.util.JournalConverter;
import com.liferay.layout.content.page.editor.web.internal.portlet.constants.LayoutContentPageEditorWebPortletKeys;
import com.liferay.layout.page.template.model.LayoutPageTemplateStructure;
import com.liferay.layout.page.template.service.LayoutPageTemplateStructureLocalService;
import com.liferay.layout.provider.LayoutStructureProvider;
import com.liferay.layout.service.LayoutClassedModelUsageLocalService;
import com.liferay.layout.test.util.ContentLayoutTestUtil;
import com.liferay.layout.test.util.LayoutTestUtil;
import com.liferay.layout.util.BulkLayoutConverter;
import com.liferay.layout.util.LayoutServiceContextHelper;
import com.liferay.layout.util.structure.DeletedLayoutStructureItem;
import com.liferay.layout.util.structure.FragmentDropZoneLayoutStructureItem;
import com.liferay.layout.util.structure.FragmentStyledLayoutStructureItem;
import com.liferay.layout.util.structure.LayoutStructure;
import com.liferay.layout.util.structure.LayoutStructureItem;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.LayoutConstants;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.portlet.PortletIdCodec;
import com.liferay.portal.kernel.portlet.PortletPreferencesFactory;
import com.liferay.portal.kernel.search.Document;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.service.PortletPreferencesLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.PortletKeys;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.search.test.util.IndexerFixture;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.segments.service.SegmentsExperienceLocalService;

import jakarta.portlet.PortletPreferences;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.BiFunction;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Víctor Galán
 */
@RunWith(Arquillian.class)
public class PublishLayoutMVCActionCommandTest {

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

		_draftLayout = _layout.fetchDraftLayout();

		_segmentsExperienceId =
			_segmentsExperienceLocalService.fetchDefaultSegmentsExperienceId(
				_draftLayout.getPlid());
	}

	@Test
	@TestInfo({"LPD-51205", "LPD-53620", "LPD-56595"})
	public void testDeletedFragmentEntryLinksAreRemovedWhenLayoutIsPublished()
		throws Exception {

		int count =
			_layoutClassedModelUsageLocalService.
				getLayoutClassedModelUsagesCount();

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(
				_group, TestPropsValues.getUserId());

		FragmentCollection fragmentCollection =
			_fragmentCollectionLocalService.addFragmentCollection(
				null, TestPropsValues.getUserId(), _group.getGroupId(),
				StringUtil.randomString(), StringPool.BLANK, serviceContext);

		FragmentEntryLink dropzoneFragmentEntryLink = _addFragmentEntryLink(
			"{}", fragmentCollection.getFragmentCollectionId(),
			"<lfr-drop-zone></lfr-drop-zone>", null, serviceContext);

		LayoutStructure layoutStructure =
			_layoutStructureProvider.getLayoutStructure(
				_draftLayout.getPlid(), _segmentsExperienceId);

		FragmentStyledLayoutStructureItem
			dropZoneFragmentStyledLayoutStructureItem =
				(FragmentStyledLayoutStructureItem)
					layoutStructure.getLayoutStructureItemByFragmentEntryLinkId(
						dropzoneFragmentEntryLink.getFragmentEntryLinkId());

		List<String> childrenItemIds =
			dropZoneFragmentStyledLayoutStructureItem.getChildrenItemIds();

		Assert.assertEquals(
			childrenItemIds.toString(), 1, childrenItemIds.size());

		FragmentDropZoneLayoutStructureItem
			fragmentDropZoneLayoutStructureItem =
				(FragmentDropZoneLayoutStructureItem)
					layoutStructure.getLayoutStructureItem(
						childrenItemIds.get(0));

		JournalArticle journalArticle = JournalTestUtil.addArticle(
			_group.getGroupId(), 0);

		FragmentEntryLink fragmentEntryLink = _addFragmentEntryLink(
			JSONUtil.put(
				FragmentEntryProcessorConstants.
					KEY_EDITABLE_FRAGMENT_ENTRY_PROCESSOR,
				JSONUtil.put(
					"element-text",
					JSONUtil.put(
						"className", JournalArticle.class.getName()
					).put(
						"classNameId",
						_portal.getClassNameId(JournalArticle.class)
					).put(
						"classPK", journalArticle.getResourcePrimKey()
					).put(
						"fieldId", "JournalArticle_title"
					))
			).toString(),
			fragmentCollection.getFragmentCollectionId(),
			"<h1 data-lfr-editable-id=\"element-text\" " +
				"data-lfr-editable-type=\"text\">Heading Example</h1>",
			fragmentDropZoneLayoutStructureItem.getItemId(), serviceContext);

		layoutStructure = _layoutStructureProvider.getLayoutStructure(
			_draftLayout.getPlid(), _segmentsExperienceId);

		FragmentStyledLayoutStructureItem fragmentStyledLayoutStructureItem =
			(FragmentStyledLayoutStructureItem)
				layoutStructure.getLayoutStructureItemByFragmentEntryLinkId(
					fragmentEntryLink.getFragmentEntryLinkId());

		Assert.assertEquals(
			fragmentDropZoneLayoutStructureItem.getItemId(),
			fragmentStyledLayoutStructureItem.getParentItemId());

		ContentLayoutTestUtil.publishLayout(_draftLayout, _layout);

		FragmentEntryLink publishedLayoutDropzoneFragmentEntryLink =
			_fragmentEntryLinkLocalService.getFragmentEntryLink(
				_group.getGroupId(),
				dropzoneFragmentEntryLink.getFragmentEntryLinkId(),
				_layout.getPlid());

		Assert.assertNotNull(publishedLayoutDropzoneFragmentEntryLink);

		FragmentEntryLink publishedLayoutFragmentEntryLink =
			_fragmentEntryLinkLocalService.getFragmentEntryLink(
				_group.getGroupId(), fragmentEntryLink.getFragmentEntryLinkId(),
				_layout.getPlid());

		Assert.assertNotNull(publishedLayoutFragmentEntryLink);

		Assert.assertEquals(
			count + 2,
			_layoutClassedModelUsageLocalService.
				getLayoutClassedModelUsagesCount());

		ContentLayoutTestUtil.markItemForDeletionFromLayout(
			dropZoneFragmentStyledLayoutStructureItem.getItemId(), _draftLayout,
			StringPool.BLANK);

		dropzoneFragmentEntryLink =
			_fragmentEntryLinkLocalService.getFragmentEntryLink(
				dropzoneFragmentEntryLink.getFragmentEntryLinkId());

		Assert.assertTrue(dropzoneFragmentEntryLink.isDeleted());

		fragmentEntryLink = _fragmentEntryLinkLocalService.getFragmentEntryLink(
			fragmentEntryLink.getFragmentEntryLinkId());

		Assert.assertTrue(fragmentEntryLink.isDeleted());

		ContentLayoutTestUtil.publishLayout(_draftLayout, _layout);

		Assert.assertNull(
			_fragmentEntryLinkLocalService.fetchFragmentEntryLink(
				dropzoneFragmentEntryLink.getFragmentEntryLinkId()));
		Assert.assertNull(
			_fragmentEntryLinkLocalService.fetchFragmentEntryLink(
				fragmentEntryLink.getFragmentEntryLinkId()));
		Assert.assertNull(
			_fragmentEntryLinkLocalService.fetchFragmentEntryLink(
				publishedLayoutDropzoneFragmentEntryLink.
					getFragmentEntryLinkId()));
		Assert.assertNull(
			_fragmentEntryLinkLocalService.fetchFragmentEntryLink(
				publishedLayoutFragmentEntryLink.getFragmentEntryLinkId()));

		Assert.assertEquals(
			count,
			_layoutClassedModelUsageLocalService.
				getLayoutClassedModelUsagesCount());

		layoutStructure = _layoutStructureProvider.getLayoutStructure(
			_layout.getPlid(),
			_segmentsExperienceLocalService.fetchDefaultSegmentsExperienceId(
				_layout.getPlid()));

		List<DeletedLayoutStructureItem> deletedLayoutStructureItems =
			layoutStructure.getDeletedLayoutStructureItems();

		Assert.assertTrue(
			deletedLayoutStructureItems.toString(),
			ListUtil.isEmpty(deletedLayoutStructureItems));
	}

	@Test
	public void testDeletedItemPortletPreferencesAreRemovedWhenLayoutIsPublished()
		throws Exception {

		FragmentEntryLink fragmentEntryLink = _addPortletToLayout();

		JSONObject editableValuesJSONObject = JSONFactoryUtil.createJSONObject(
			fragmentEntryLink.getEditableValues());

		String encodePortletId = PortletIdCodec.encode(
			LayoutContentPageEditorWebPortletKeys.
				LAYOUT_CONTENT_PAGE_EDITOR_WEB_TEST_PORTLET,
			editableValuesJSONObject.getString("instanceId"));

		_assertNotNullPortletPreferences(
			_draftLayout.getPlid(), encodePortletId);

		_assertNullPortletPreferences(_layout.getPlid(), encodePortletId);

		ContentLayoutTestUtil.publishLayout(_draftLayout, _layout);

		_assertNotNullPortletPreferences(
			_draftLayout.getPlid(), encodePortletId);

		_assertNotNullPortletPreferences(_layout.getPlid(), encodePortletId);

		LayoutStructure layoutStructure = _getLayoutStructure(_draftLayout);

		LayoutStructureItem portletLayoutStructureItem =
			layoutStructure.getLayoutStructureItemByFragmentEntryLinkId(
				fragmentEntryLink.getFragmentEntryLinkId());

		layoutStructure.markLayoutStructureItemForDeletion(
			Collections.singletonList(portletLayoutStructureItem.getItemId()),
			Collections.emptyList());

		_layoutPageTemplateStructureLocalService.
			updateLayoutPageTemplateStructureData(
				_group.getGroupId(), _draftLayout.getPlid(),
				_segmentsExperienceId, layoutStructure.toString());

		ContentLayoutTestUtil.publishLayout(_draftLayout, _layout);

		_assertNullPortletPreferences(_draftLayout.getPlid(), encodePortletId);

		_assertNullPortletPreferences(_layout.getPlid(), encodePortletId);
	}

	@Test
	public void testDeletedItemsAreRemovedWhenLayoutIsPublished()
		throws Exception {

		LayoutStructure layoutStructure = _getLayoutStructure(_draftLayout);

		LayoutStructureItem layoutStructureItem1 =
			layoutStructure.addContainerStyledLayoutStructureItem(
				layoutStructure.getMainItemId(), 0);

		LayoutStructureItem layoutStructureItem2 =
			layoutStructure.addRowStyledLayoutStructureItem(
				layoutStructure.getMainItemId(), 0, 3);

		LayoutStructureItem layoutStructureItem3 =
			layoutStructure.addRowStyledLayoutStructureItem(
				layoutStructure.getMainItemId(), 0, 3);

		layoutStructure.markLayoutStructureItemForDeletion(
			Collections.singletonList(layoutStructureItem1.getItemId()),
			Collections.emptyList());

		layoutStructure.markLayoutStructureItemForDeletion(
			Collections.singletonList(layoutStructureItem2.getItemId()),
			Collections.emptyList());

		_layoutPageTemplateStructureLocalService.
			updateLayoutPageTemplateStructureData(
				_group.getGroupId(), _draftLayout.getPlid(),
				_segmentsExperienceId, layoutStructure.toString());

		ContentLayoutTestUtil.publishLayout(_draftLayout, _layout);

		layoutStructure = _getLayoutStructure(_draftLayout);

		List<DeletedLayoutStructureItem> deletedLayoutStructureItems =
			layoutStructure.getDeletedLayoutStructureItems();

		Assert.assertEquals(
			deletedLayoutStructureItems.toString(), 0,
			deletedLayoutStructureItems.size());

		Assert.assertNull(
			layoutStructure.getLayoutStructureItem(
				layoutStructureItem1.getItemId()));
		Assert.assertNull(
			layoutStructure.getLayoutStructureItem(
				layoutStructureItem2.getItemId()));
		Assert.assertNotNull(
			layoutStructure.getLayoutStructureItem(
				layoutStructureItem3.getItemId()));
	}

	@Test
	public void testLayoutContentIsIndexedAfterPublishing() throws Exception {
		Assert.assertFalse(_layout.isPublished());

		FragmentEntryLink fragmentEntryLink =
			ContentLayoutTestUtil.addFragmentEntryLinkToLayout(
				"{}", _draftLayout, _segmentsExperienceId);

		String keywords = fragmentEntryLink.getHtml();

		Assert.assertTrue(keywords, Validator.isNotNull(keywords));

		IndexerFixture<Layout> layoutIndexerFixture = new IndexerFixture<>(
			Layout.class);

		layoutIndexerFixture.searchNoOne(keywords);

		ContentLayoutTestUtil.publishLayout(_draftLayout, _layout);

		_layout = _layoutLocalService.getLayout(_layout.getPlid());

		Assert.assertTrue(_layout.isPublished());

		Locale locale = LocaleUtil.getSiteDefault();

		Document document = layoutIndexerFixture.searchOnlyOne(
			keywords, locale);

		Assert.assertNotNull(document);

		String content = document.get(
			Field.getLocalizedName(locale, Field.CONTENT));

		Assert.assertTrue(
			content, StringUtil.contains(content, keywords, StringPool.BLANK));

		Assert.assertEquals(
			document.get(Field.ENTRY_CLASS_PK),
			String.valueOf(_layout.getPlid()));
	}

	@Test
	public void testPublishConversionDraftCreatedByDeletedUser()
		throws Exception {

		User user = UserTestUtil.addCompanyAdminUser(
			_companyLocalService.getCompany(_group.getCompanyId()));

		try {
			ServiceContext serviceContext =
				ServiceContextTestUtil.getServiceContext(
					_group.getGroupId(), user.getUserId());

			ServiceContextThreadLocal.pushServiceContext(serviceContext);

			Layout originalLayout = _layoutLocalService.addLayout(
				null, user.getUserId(), _group.getGroupId(), false,
				LayoutConstants.DEFAULT_PARENT_LAYOUT_ID,
				RandomTestUtil.randomString(), StringPool.BLANK,
				StringPool.BLANK, LayoutConstants.TYPE_PORTLET, false,
				StringPool.BLANK, serviceContext);

			Assert.assertEquals(user.getUserId(), originalLayout.getUserId());
			Assert.assertTrue(originalLayout.isTypePortlet());

			serviceContext = ServiceContextTestUtil.getServiceContext(
				_group.getGroupId(), TestPropsValues.getUserId());

			ServiceContextThreadLocal.pushServiceContext(serviceContext);

			_deleteUser(user, serviceContext);

			_bulkLayoutConverter.convertLayout(originalLayout.getPlid());

			originalLayout = _layoutLocalService.getLayout(
				originalLayout.getPlid());

			Assert.assertTrue(originalLayout.isTypePortlet());

			Layout conversionDraftLayout = originalLayout.fetchDraftLayout();

			Assert.assertNotNull(conversionDraftLayout);
			Assert.assertEquals(
				TestPropsValues.getUserId(), conversionDraftLayout.getUserId());
			Assert.assertTrue(conversionDraftLayout.isTypeContent());

			ContentLayoutTestUtil.publishLayout(
				conversionDraftLayout, originalLayout);

			originalLayout = _layoutLocalService.getLayout(
				originalLayout.getPlid());

			Assert.assertTrue(originalLayout.isPublished());
			Assert.assertTrue(originalLayout.isTypeContent());

			Layout draftLayout = originalLayout.fetchDraftLayout();

			Assert.assertNotNull(draftLayout);
			Assert.assertEquals(
				conversionDraftLayout.getPlid(), draftLayout.getPlid());

			Assert.assertTrue(draftLayout.isApproved());
		}
		finally {
			ServiceContextThreadLocal.popServiceContext();
		}
	}

	@Test
	@TestInfo({"LPD-39213", "LPS-202932"})
	public void testPublishedLayoutFragmentEntryLinkWithFreeMarkerEmbeddedPortlet()
		throws Exception {

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(
				_group.getGroupId(), TestPropsValues.getUserId());

		try {
			ServiceContextThreadLocal.pushServiceContext(serviceContext);

			_assertPublishedLayoutFragmentEntryLinkWithFreeMarkerEmbeddedPortlet(
				new String[] {
					StringBundler.concat(
						"<div class=\"fragment_1\">[@liferay_portlet.runtime ",
						"portletName=\"com_liferay_journal_content_web_",
						"portlet_JournalContentPortlet\" ",
						"instanceId=\"myInstanceId0\" persistSettings=false /]",
						"</div>"),
					StringBundler.concat(
						"<div class=\"fragment_1\">[@liferay_portlet",
						"[\"runtime\"] portletName=\"com_liferay_journal_",
						"content_web_portlet_JournalContentPortlet\" ",
						"instanceId=\"myInstanceId1\" persistSettings=false /]",
						"</div>")
				},
				(index, namespace) -> PortletIdCodec.encode(
					JournalContentPortletKeys.JOURNAL_CONTENT,
					"myInstanceId" + index));
		}
		finally {
			ServiceContextThreadLocal.popServiceContext();
		}
	}

	@Test
	@TestInfo({"LPD-39213", "LPS-202932"})
	public void testPublishedLayoutFragmentEntryLinkWithFreeMarkerEmbeddedPortletAndDynamicInstanceId()
		throws Exception {

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(
				_group.getGroupId(), TestPropsValues.getUserId());

		try {
			ServiceContextThreadLocal.pushServiceContext(serviceContext);

			_assertPublishedLayoutFragmentEntryLinkWithFreeMarkerEmbeddedPortlet(
				new String[] {
					StringBundler.concat(
						"<div class=\"fragment_1\">[@liferay_portlet.runtime ",
						"portletName=\"com_liferay_journal_content_web_",
						"portlet_JournalContentPortlet\" ",
						"instanceId=\"fragmentEntryLinkNamespace\" ",
						"persistSettings=false /]</div>"),
					StringBundler.concat(
						"<div class=\"fragment_2\">[@liferay_portlet",
						"[\"runtime\"] portletName=\"com_liferay_journal_",
						"content_web_portlet_JournalContentPortlet\" ",
						"instanceId=\"fragmentEntryLinkNamespace\" ",
						"persistSettings=false /]</div>")
				},
				(index, namespace) -> PortletIdCodec.encode(
					JournalContentPortletKeys.JOURNAL_CONTENT, namespace));
		}
		finally {
			ServiceContextThreadLocal.popServiceContext();
		}
	}

	@Test
	public void testPublishedLayoutWithNoninstanciablePortlet()
		throws Exception {

		_testPublishedLayoutWithNoninstanciablePortlet();
	}

	@Test
	@TestInfo("LPD-40170")
	public void testPublishedLayoutWithNoninstanciablePortletMarkedForDeletion()
		throws Exception {

		ContentLayoutTestUtil.addPortletToLayout(
			_draftLayout,
			LayoutContentPageEditorWebPortletKeys.
				LAYOUT_CONTENT_PAGE_EDITOR_WEB_NONINSTANCEABLE_TEST_PORTLET);

		ContentLayoutTestUtil.markItemForDeletionFromLayout(
			_getDraftLayoutFragmentStyledLayoutStructureItemId(), _draftLayout,
			LayoutContentPageEditorWebPortletKeys.
				LAYOUT_CONTENT_PAGE_EDITOR_WEB_NONINSTANCEABLE_TEST_PORTLET);

		_testPublishedLayoutWithNoninstanciablePortlet();

		ContentLayoutTestUtil.markItemForDeletionFromLayout(
			_getDraftLayoutFragmentStyledLayoutStructureItemId(), _draftLayout,
			LayoutContentPageEditorWebPortletKeys.
				LAYOUT_CONTENT_PAGE_EDITOR_WEB_NONINSTANCEABLE_TEST_PORTLET);

		ContentLayoutTestUtil.publishLayout(_draftLayout, _layout);

		Assert.assertTrue(
			ListUtil.isEmpty(
				_portletPreferencesLocalService.getPortletPreferences(
					_draftLayout.getPlid(),
					LayoutContentPageEditorWebPortletKeys.
						LAYOUT_CONTENT_PAGE_EDITOR_WEB_NONINSTANCEABLE_TEST_PORTLET)));
		Assert.assertTrue(
			ListUtil.isEmpty(
				_portletPreferencesLocalService.getPortletPreferences(
					_layout.getPlid(),
					LayoutContentPageEditorWebPortletKeys.
						LAYOUT_CONTENT_PAGE_EDITOR_WEB_NONINSTANCEABLE_TEST_PORTLET)));
	}

	@Test
	@TestInfo("LPS-148426")
	public void testPublishedLayoutWithNoninstanciablePortlets()
		throws Exception {

		String[] portletIds = {
			"com_liferay_commerce_checkout_web_internal_portlet_" +
				"CommerceCheckoutPortlet",
			"com_liferay_commerce_order_content_web_internal_portlet_" +
				"CommerceOpenOrderContentPortlet",
			"com_liferay_commerce_order_content_web_internal_portlet_" +
				"CommerceOrderContentPortlet",
			"com_liferay_commerce_wish_list_web_internal_portlet_" +
				"CommerceWishListContentPortlet"
		};

		ContentLayoutTestUtil.publishLayout(_draftLayout, _layout);

		String html = ContentLayoutTestUtil.getRenderLayoutHTML(
			_layout, _layoutServiceContextHelper, _layoutStructureProvider,
			_segmentsExperienceId);

		for (String portletId : portletIds) {
			Assert.assertFalse(html.contains(portletId));
		}

		for (String portletId : portletIds) {
			ContentLayoutTestUtil.addPortletToLayout(_draftLayout, portletId);
		}

		ContentLayoutTestUtil.publishLayout(_draftLayout, _layout);

		html = ContentLayoutTestUtil.getRenderLayoutHTML(
			_layout, _layoutServiceContextHelper, _layoutStructureProvider,
			_segmentsExperienceId);

		for (String portletId : portletIds) {
			Assert.assertTrue(html.contains(portletId));
		}
	}

	@Test
	@TestInfo("LPD-39258")
	public void testPublishedLayoutWithNoninstanciablePortletWithZeroInstanceId()
		throws Exception {

		JSONObject jsonObject = ContentLayoutTestUtil.addPortletToLayout(
			_draftLayout,
			LayoutContentPageEditorWebPortletKeys.
				LAYOUT_CONTENT_PAGE_EDITOR_WEB_NONINSTANCEABLE_TEST_PORTLET);

		JSONObject fragmentEntryLinkJSONObject = jsonObject.getJSONObject(
			"fragmentEntryLink");

		JSONObject editableValuesJSONObject =
			fragmentEntryLinkJSONObject.getJSONObject("editableValues");

		Assert.assertEquals(
			StringPool.BLANK, editableValuesJSONObject.getString("instanceId"));

		editableValuesJSONObject.put("instanceId", "0");

		_fragmentEntryLinkLocalService.updateFragmentEntryLink(
			TestPropsValues.getUserId(),
			GetterUtil.getLong(
				fragmentEntryLinkJSONObject.getString("fragmentEntryLinkId")),
			editableValuesJSONObject.toString());

		Map<String, String> map = HashMapBuilder.put(
			RandomTestUtil.randomString(), RandomTestUtil.randomString()
		).build();

		_setUpPortletPreferences(
			_draftLayout, map,
			LayoutContentPageEditorWebPortletKeys.
				LAYOUT_CONTENT_PAGE_EDITOR_WEB_NONINSTANCEABLE_TEST_PORTLET);

		ContentLayoutTestUtil.publishLayout(_draftLayout, _layout);

		_assertPortletPreferences(
			_layout, map,
			LayoutContentPageEditorWebPortletKeys.
				LAYOUT_CONTENT_PAGE_EDITOR_WEB_NONINSTANCEABLE_TEST_PORTLET);
	}

	private FragmentEntryLink _addFragmentEntryLink(
			String editableValues, long fragmentCollectionId, String html,
			String parentItemId, ServiceContext serviceContext)
		throws Exception {

		FragmentEntry fragmentEntry =
			_fragmentEntryLocalService.addFragmentEntry(
				null, TestPropsValues.getUserId(), _group.getGroupId(),
				fragmentCollectionId, RandomTestUtil.randomString(),
				RandomTestUtil.randomString(), StringPool.BLANK, html,
				StringPool.BLANK, false, StringPool.BLANK, null, 0, false,
				false, FragmentConstants.TYPE_COMPONENT, null,
				WorkflowConstants.STATUS_APPROVED, serviceContext);

		FragmentEntryLink fragmentEntryLink =
			ContentLayoutTestUtil.addFragmentEntryLinkToLayout(
				editableValues, fragmentEntry.getCss(),
				fragmentEntry.getConfiguration(),
				fragmentEntry.getFragmentEntryId(), fragmentEntry.getHtml(),
				fragmentEntry.getJs(), _draftLayout,
				fragmentEntry.getFragmentEntryKey(), fragmentEntry.getType(),
				parentItemId, 0, _segmentsExperienceId);

		ServiceContextThreadLocal.pushServiceContext(serviceContext);

		try {
			for (FragmentEntryLinkListener fragmentEntryLinkListener :
					_fragmentEntryLinkListenerRegistry.
						getFragmentEntryLinkListeners()) {

				fragmentEntryLinkListener.onAddFragmentEntryLink(
					fragmentEntryLink);
			}
		}
		finally {
			ServiceContextThreadLocal.popServiceContext();
		}

		return fragmentEntryLink;
	}

	private FragmentEntryLink _addFragmentEntryLinkToLayout(String html)
		throws Exception {

		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		FragmentCollection fragmentCollection =
			_fragmentCollectionLocalService.addFragmentCollection(
				null, TestPropsValues.getUserId(), _group.getGroupId(),
				RandomTestUtil.randomString(), null, serviceContext);

		FragmentEntry fragmentEntry =
			_fragmentEntryLocalService.addFragmentEntry(
				null, TestPropsValues.getUserId(), _group.getGroupId(),
				fragmentCollection.getFragmentCollectionId(), null,
				RandomTestUtil.randomString(), null, html, null, false, null,
				null, 0, false, false, FragmentConstants.TYPE_COMPONENT, null,
				WorkflowConstants.STATUS_APPROVED, serviceContext);

		return ContentLayoutTestUtil.addFragmentEntryLinkToLayout(
			"{}", fragmentEntry.getCss(), fragmentEntry.getConfiguration(),
			fragmentEntry.getFragmentEntryId(), fragmentEntry.getHtml(),
			fragmentEntry.getJs(), _draftLayout,
			fragmentEntry.getFragmentEntryKey(), fragmentEntry.getType(), null,
			0, _segmentsExperienceId);
	}

	private FragmentEntryLink _addPortletToLayout() throws Exception {
		List<FragmentEntryLink> originalFragmentEntryLinks =
			_fragmentEntryLinkLocalService.getFragmentEntryLinksByPlid(
				_group.getGroupId(), _draftLayout.getPlid());

		JSONObject processAddPortletJSONObject =
			ContentLayoutTestUtil.addPortletToLayout(
				_draftLayout,
				LayoutContentPageEditorWebPortletKeys.
					LAYOUT_CONTENT_PAGE_EDITOR_WEB_TEST_PORTLET);

		Assert.assertNotNull(processAddPortletJSONObject);

		JSONObject fragmentEntryLinkJSONObject =
			processAddPortletJSONObject.getJSONObject("fragmentEntryLink");

		Assert.assertNotNull(fragmentEntryLinkJSONObject);

		FragmentEntryLink fragmentEntryLink =
			_fragmentEntryLinkLocalService.fetchFragmentEntryLink(
				fragmentEntryLinkJSONObject.getLong("fragmentEntryLinkId"));

		Assert.assertNotNull(fragmentEntryLink);

		List<FragmentEntryLink> actualFragmentEntryLinks =
			_fragmentEntryLinkLocalService.getFragmentEntryLinksByPlid(
				_group.getGroupId(), _draftLayout.getPlid());

		Assert.assertEquals(
			actualFragmentEntryLinks.toString(),
			originalFragmentEntryLinks.size() + 1,
			actualFragmentEntryLinks.size());

		return fragmentEntryLink;
	}

	private void _assertNotNullPortletPreferences(
		long plid, String encodePortletId) {

		Assert.assertNotNull(
			_portletPreferencesLocalService.fetchPortletPreferences(
				PortletKeys.PREFS_OWNER_ID_DEFAULT,
				PortletKeys.PREFS_OWNER_TYPE_LAYOUT, plid, encodePortletId));
	}

	private void _assertNullPortletPreferences(
		long plid, String encodePortletId) {

		Assert.assertNull(
			_portletPreferencesLocalService.fetchPortletPreferences(
				PortletKeys.PREFS_OWNER_ID_DEFAULT,
				PortletKeys.PREFS_OWNER_TYPE_LAYOUT, plid, encodePortletId));
	}

	private void _assertPortletPreferences(
		Layout layout, Map<String, String> map, String portletId) {

		PortletPreferences portletPreferences =
			_portletPreferencesFactory.getPortletSetup(layout, portletId, null);

		for (Map.Entry<String, String> entry : map.entrySet()) {
			Assert.assertEquals(
				entry.getValue(),
				portletPreferences.getValue(entry.getKey(), null));
		}
	}

	private void
			_assertPublishedLayoutFragmentEntryLinkWithFreeMarkerEmbeddedPortlet(
				String[] htmls,
				BiFunction<Integer, String, String> portletIdFunction)
		throws Exception {

		Map<String, Map<String, String>> portletIdsMap = new HashMap<>();

		for (int i = 0; i < htmls.length; i++) {
			JournalArticle journalArticle = JournalTestUtil.addJournalArticle(
				_dataDefinitionResourceFactory, _createDDMFormField(),
				_ddmFormValuesToFieldsConverter, RandomTestUtil.randomString(),
				_group.getGroupId(), _journalConverter);

			AssetEntry assetEntry = _assetEntryLocalService.getEntry(
				JournalArticle.class.getName(),
				journalArticle.getResourcePrimKey());

			Map<String, String> map = HashMapBuilder.put(
				"articleId", String.valueOf(journalArticle.getArticleId())
			).put(
				"assetEntryId", String.valueOf(assetEntry.getEntryId())
			).put(
				"groupId", String.valueOf(journalArticle.getGroupId())
			).build();

			FragmentEntryLink fragmentEntryLink = _addFragmentEntryLinkToLayout(
				htmls[i]);

			String portletId = portletIdFunction.apply(
				i, fragmentEntryLink.getNamespace());

			_setUpPortletPreferences(_draftLayout, map, portletId);

			portletIdsMap.put(portletId, map);
		}

		ContentLayoutTestUtil.publishLayout(_draftLayout, _layout);

		for (Map.Entry<String, Map<String, String>> entry :
				portletIdsMap.entrySet()) {

			_assertPortletPreferences(
				_layout, entry.getValue(), entry.getKey());
		}
	}

	private DDMFormField _createDDMFormField() {
		DDMFormField ddmFormField = new DDMFormField(
			"name", DDMFormFieldTypeConstants.TEXT);

		ddmFormField.setDataType("text");
		ddmFormField.setIndexType("text");

		LocalizedValue localizedValue = new LocalizedValue(LocaleUtil.US);

		localizedValue.addString(
			LocaleUtil.US, RandomTestUtil.randomString(10));

		ddmFormField.setLabel(localizedValue);

		ddmFormField.setLocalizable(true);

		return ddmFormField;
	}

	private void _deleteUser(User user, ServiceContext serviceContext)
		throws PortalException {

		_userLocalService.updateStatus(
			user, WorkflowConstants.STATUS_INACTIVE, serviceContext);

		_userLocalService.deleteUser(user.getUserId());

		Assert.assertNull(_userLocalService.fetchUser(user.getUserId()));
	}

	private String _getDraftLayoutFragmentStyledLayoutStructureItemId() {
		LayoutStructure layoutStructure =
			_layoutStructureProvider.getLayoutStructure(
				_draftLayout.getPlid(), _segmentsExperienceId);

		Map<Long, LayoutStructureItem> fragmentLayoutStructureItems =
			layoutStructure.getFragmentLayoutStructureItems();

		List<FragmentEntryLink> fragmentEntryLinks =
			_fragmentEntryLinkLocalService.
				getFragmentEntryLinksBySegmentsExperienceId(
					_draftLayout.getGroupId(), _segmentsExperienceId,
					_draftLayout.getPlid(), false);

		Assert.assertEquals(
			fragmentEntryLinks.toString(), 1, fragmentEntryLinks.size());

		FragmentEntryLink fragmentEntryLink = fragmentEntryLinks.get(0);

		LayoutStructureItem layoutStructureItem =
			fragmentLayoutStructureItems.get(
				fragmentEntryLink.getFragmentEntryLinkId());

		return layoutStructureItem.getItemId();
	}

	private LayoutStructure _getLayoutStructure(Layout layout)
		throws Exception {

		LayoutPageTemplateStructure layoutPageTemplateStructure =
			_layoutPageTemplateStructureLocalService.
				fetchLayoutPageTemplateStructure(
					_group.getGroupId(), layout.getPlid());

		return LayoutStructure.of(
			layoutPageTemplateStructure.getDefaultSegmentsExperienceData());
	}

	private void _setUpPortletPreferences(
			Layout layout, Map<String, String> map, String portletId)
		throws Exception {

		PortletPreferences portletPreferences =
			_portletPreferencesFactory.getPortletSetup(layout, portletId, null);

		for (Map.Entry<String, String> entry : map.entrySet()) {
			portletPreferences.setValue(entry.getKey(), entry.getValue());
		}

		portletPreferences.store();

		_assertPortletPreferences(layout, map, portletId);
	}

	private void _testPublishedLayoutWithNoninstanciablePortlet()
		throws Exception {

		ContentLayoutTestUtil.addPortletToLayout(
			_draftLayout,
			LayoutContentPageEditorWebPortletKeys.
				LAYOUT_CONTENT_PAGE_EDITOR_WEB_NONINSTANCEABLE_TEST_PORTLET);

		Map<String, String> map = HashMapBuilder.put(
			RandomTestUtil.randomString(), RandomTestUtil.randomString()
		).build();

		_setUpPortletPreferences(
			_draftLayout, map,
			LayoutContentPageEditorWebPortletKeys.
				LAYOUT_CONTENT_PAGE_EDITOR_WEB_NONINSTANCEABLE_TEST_PORTLET);

		ContentLayoutTestUtil.publishLayout(_draftLayout, _layout);

		_assertPortletPreferences(
			_draftLayout, map,
			LayoutContentPageEditorWebPortletKeys.
				LAYOUT_CONTENT_PAGE_EDITOR_WEB_NONINSTANCEABLE_TEST_PORTLET);
		_assertPortletPreferences(
			_layout, map,
			LayoutContentPageEditorWebPortletKeys.
				LAYOUT_CONTENT_PAGE_EDITOR_WEB_NONINSTANCEABLE_TEST_PORTLET);
	}

	@Inject
	private AssetEntryLocalService _assetEntryLocalService;

	@Inject
	private BulkLayoutConverter _bulkLayoutConverter;

	@Inject
	private CompanyLocalService _companyLocalService;

	@Inject
	private DataDefinitionResource.Factory _dataDefinitionResourceFactory;

	@Inject
	private DDMFormValuesToFieldsConverter _ddmFormValuesToFieldsConverter;

	private Layout _draftLayout;

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
	private JournalConverter _journalConverter;

	private Layout _layout;

	@Inject
	private LayoutClassedModelUsageLocalService
		_layoutClassedModelUsageLocalService;

	@Inject
	private LayoutLocalService _layoutLocalService;

	@Inject
	private LayoutPageTemplateStructureLocalService
		_layoutPageTemplateStructureLocalService;

	@Inject
	private LayoutServiceContextHelper _layoutServiceContextHelper;

	@Inject
	private LayoutStructureProvider _layoutStructureProvider;

	@Inject
	private Portal _portal;

	@Inject
	private PortletPreferencesFactory _portletPreferencesFactory;

	@Inject
	private PortletPreferencesLocalService _portletPreferencesLocalService;

	private long _segmentsExperienceId;

	@Inject
	private SegmentsExperienceLocalService _segmentsExperienceLocalService;

	@Inject
	private UserLocalService _userLocalService;

}