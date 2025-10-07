/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.content.page.editor.web.internal.portlet.action.test;

import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.document.library.kernel.model.DLFolderConstants;
import com.liferay.document.library.kernel.service.DLAppLocalService;
import com.liferay.fragment.constants.FragmentConstants;
import com.liferay.fragment.entry.processor.constants.FragmentEntryProcessorConstants;
import com.liferay.fragment.model.FragmentCollection;
import com.liferay.fragment.model.FragmentComposition;
import com.liferay.fragment.model.FragmentEntry;
import com.liferay.fragment.model.FragmentEntryLink;
import com.liferay.fragment.service.FragmentCollectionLocalService;
import com.liferay.fragment.service.FragmentCompositionLocalService;
import com.liferay.fragment.service.FragmentEntryLinkLocalService;
import com.liferay.fragment.service.FragmentEntryLocalService;
import com.liferay.item.selector.criteria.InfoItemItemSelectorReturnType;
import com.liferay.journal.model.JournalArticle;
import com.liferay.journal.test.util.JournalTestUtil;
import com.liferay.layout.page.template.model.LayoutPageTemplateStructure;
import com.liferay.layout.page.template.service.LayoutPageTemplateStructureLocalService;
import com.liferay.layout.provider.LayoutStructureProvider;
import com.liferay.layout.test.util.ContentLayoutTestUtil;
import com.liferay.layout.test.util.LayoutTestUtil;
import com.liferay.layout.util.constants.LayoutDataItemTypeConstants;
import com.liferay.layout.util.structure.LayoutStructure;
import com.liferay.layout.util.structure.LayoutStructureItem;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.portletfilerepository.PortletFileRepository;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletActionRequest;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletActionResponse;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.rule.Sync;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.segments.service.SegmentsExperienceLocalService;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;

import java.util.HashMap;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Pavel Savinov
 */
@RunWith(Arquillian.class)
@Sync
public class AddFragmentCompositionMVCActionCommandTest {

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

		_layout = LayoutTestUtil.addTypeContentLayout(_group);

		_serviceContext = ServiceContextTestUtil.getServiceContext(
			_group.getGroupId(), TestPropsValues.getUserId());

		_serviceContext.setCompanyId(TestPropsValues.getCompanyId());

		ServiceContextThreadLocal.pushServiceContext(_serviceContext);

		_objectMapper = new ObjectMapper() {
			{
				configure(MapperFeature.SORT_PROPERTIES_ALPHABETICALLY, true);
			}
		};
	}

	@Test
	public void testAddFragmentCompositionDefaultCollection() throws Exception {
		MockLiferayPortletActionRequest mockLiferayPortletActionRequest =
			_getMockLiferayPortletActionRequest();

		LayoutPageTemplateStructure layoutPageTemplateStructure =
			_layoutPageTemplateStructureLocalService.
				fetchLayoutPageTemplateStructure(
					_group.getGroupId(), _layout.getPlid());

		LayoutStructure layoutStructure = LayoutStructure.of(
			layoutPageTemplateStructure.getDefaultSegmentsExperienceData());

		mockLiferayPortletActionRequest.addParameter(
			"fragmentCollectionId", String.valueOf(0));
		mockLiferayPortletActionRequest.addParameter(
			"name", RandomTestUtil.randomString());
		mockLiferayPortletActionRequest.addParameter(
			"itemId", layoutStructure.getMainItemId());

		JSONObject jsonObject = ReflectionTestUtil.invoke(
			_mvcActionCommand, "doTransactionalCommand",
			new Class<?>[] {ActionRequest.class, ActionResponse.class},
			mockLiferayPortletActionRequest,
			new MockLiferayPortletActionResponse());

		Assert.assertNotNull(jsonObject);

		JSONObject fragmentCompositionJSONObject = jsonObject.getJSONObject(
			"fragmentComposition");

		FragmentComposition fragmentComposition =
			_fragmentCompositionLocalService.fetchFragmentComposition(
				_group.getGroupId(),
				fragmentCompositionJSONObject.getString("fragmentEntryKey"));

		Assert.assertNotNull(fragmentComposition);

		FragmentCollection fragmentCollection =
			_fragmentCollectionLocalService.getFragmentCollection(
				fragmentComposition.getFragmentCollectionId());

		Assert.assertNotNull(fragmentCollection);

		Assert.assertEquals(
			"saved-fragments", fragmentCollection.getFragmentCollectionKey());
	}

	@Test
	public void testAddFragmentCompositionExistingCollection()
		throws Exception {

		MockLiferayPortletActionRequest mockLiferayPortletActionRequest =
			_getMockLiferayPortletActionRequest();

		LayoutPageTemplateStructure layoutPageTemplateStructure =
			_layoutPageTemplateStructureLocalService.
				fetchLayoutPageTemplateStructure(
					_group.getGroupId(), _layout.getPlid());

		LayoutStructure layoutStructure = LayoutStructure.of(
			layoutPageTemplateStructure.getDefaultSegmentsExperienceData());

		FragmentCollection newFragmentCollection =
			_fragmentCollectionLocalService.addFragmentCollection(
				null, TestPropsValues.getUserId(), _group.getGroupId(),
				StringUtil.randomString(), StringPool.BLANK,
				ServiceContextThreadLocal.getServiceContext());

		mockLiferayPortletActionRequest.addParameter(
			"fragmentCollectionId",
			String.valueOf(newFragmentCollection.getFragmentCollectionId()));

		mockLiferayPortletActionRequest.addParameter(
			"name", RandomTestUtil.randomString());
		mockLiferayPortletActionRequest.addParameter(
			"itemId", layoutStructure.getMainItemId());

		JSONObject jsonObject = ReflectionTestUtil.invoke(
			_mvcActionCommand, "doTransactionalCommand",
			new Class<?>[] {ActionRequest.class, ActionResponse.class},
			mockLiferayPortletActionRequest,
			new MockLiferayPortletActionResponse());

		Assert.assertNotNull(jsonObject);

		JSONObject fragmentCompositionJSONObject = jsonObject.getJSONObject(
			"fragmentComposition");

		FragmentComposition fragmentComposition =
			_fragmentCompositionLocalService.fetchFragmentComposition(
				_group.getGroupId(),
				fragmentCompositionJSONObject.getString("fragmentEntryKey"));

		Assert.assertNotNull(fragmentComposition);

		FragmentCollection fragmentCollection =
			_fragmentCollectionLocalService.getFragmentCollection(
				fragmentComposition.getFragmentCollectionId());

		Assert.assertNotNull(fragmentCollection);

		Assert.assertEquals(
			newFragmentCollection.getFragmentCollectionKey(),
			fragmentCollection.getFragmentCollectionKey());
	}

	@Test
	public void testAddFragmentCompositionSaveMappingConfigurationEditableLink()
		throws Exception {

		_layout = LayoutTestUtil.addTypeContentLayout(_group);

		FragmentCollection fragmentCollection =
			_fragmentCollectionLocalService.addFragmentCollection(
				null, TestPropsValues.getUserId(), _group.getGroupId(),
				RandomTestUtil.randomString(), StringPool.BLANK,
				_serviceContext);

		String html =
			"<div><a data-lfr-editable-id=\"my-link-editable-id\" " +
				"data-lfr-editable-type=\"link\" href=\"\" id=\"my-link-id\">" +
					"Example</a></div>";

		FragmentEntry fragmentEntry =
			_fragmentEntryLocalService.addFragmentEntry(
				null, TestPropsValues.getUserId(), _group.getGroupId(),
				fragmentCollection.getFragmentCollectionId(),
				"example-fragment-entry-key", RandomTestUtil.randomString(),
				StringPool.BLANK, html, StringPool.BLANK, false,
				StringPool.BLANK, null, 0, false, false,
				FragmentConstants.TYPE_COMPONENT, null,
				WorkflowConstants.STATUS_APPROVED, _serviceContext);

		long defaultSegmentsExperienceId =
			_segmentsExperienceLocalService.fetchDefaultSegmentsExperienceId(
				_layout.getPlid());

		JournalArticle journalArticle1 = _addJournalArticle(
			RandomTestUtil.randomString());

		JournalArticle journalArticle2 = _addJournalArticle(
			RandomTestUtil.randomString());

		HashMap<String, String> valuesMap = HashMapBuilder.put(
			"FRAGMENT_COLLECTION_NAME",
			StringUtil.quote(fragmentCollection.getName(), StringPool.QUOTE)
		).put(
			"FRAGMENT_ENTRY_KEY",
			StringUtil.quote(
				fragmentEntry.getFragmentEntryKey(), StringPool.QUOTE)
		).put(
			"FRAGMENT_ENTRY_NAME",
			StringUtil.quote(fragmentEntry.getName(), StringPool.QUOTE)
		).put(
			"MAPPED_LINK_CLASS_NAME",
			StringUtil.quote(
				"com.liferay.journal.model.JournalArticle", StringPool.QUOTE)
		).put(
			"MAPPED_LINK_CLASS_NAME_ID",
			StringUtil.quote(
				String.valueOf(
					_portal.getClassNameId(
						"com.liferay.journal.model.JournalArticle")))
		).put(
			"MAPPED_LINK_CLASS_PK",
			String.valueOf(journalArticle1.getResourcePrimKey())
		).put(
			"MAPPED_LINK_DEFAULT_VALUE",
			StringUtil.quote(journalArticle1.getTitle())
		).put(
			"MAPPED_TEXT_CLASS_NAME",
			StringUtil.quote(
				"com.liferay.journal.model.JournalArticle", StringPool.QUOTE)
		).put(
			"MAPPED_TEXT_CLASS_NAME_ID",
			StringUtil.quote(
				String.valueOf(
					_portal.getClassNameId(
						"com.liferay.journal.model.JournalArticle")),
				StringPool.QUOTE)
		).put(
			"MAPPED_TEXT_CLASS_PK",
			String.valueOf(journalArticle2.getResourcePrimKey())
		).put(
			"MAPPED_TEXT_DEFAULT_VALUE",
			StringUtil.quote(journalArticle2.getTitle(), StringPool.QUOTE)
		).put(
			"SITE_KEY", _group.getGroupKey()
		).build();

		String editableValues = StringUtil.replace(
			_read("editable_values_with_mapping.json"), "\"${", "}\"",
			valuesMap);

		FragmentEntryLink fragmentEntryLink =
			_fragmentEntryLinkLocalService.addFragmentEntryLink(
				null, TestPropsValues.getUserId(), _group.getGroupId(), 0,
				fragmentEntry.getFragmentEntryId(), defaultSegmentsExperienceId,
				_layout.getPlid(), StringPool.BLANK, html, StringPool.BLANK,
				_read("fragment_configuration.json"), editableValues,
				StringPool.BLANK, 0, null, fragmentEntry.getType(),
				_serviceContext);

		LayoutStructure layoutStructure = new LayoutStructure();

		LayoutStructureItem rootLayoutStructureItem =
			layoutStructure.addRootLayoutStructureItem();

		LayoutStructureItem containerStyledLayoutStructureItem =
			layoutStructure.addContainerStyledLayoutStructureItem(
				"item1", rootLayoutStructureItem.getItemId(), 0);

		layoutStructure.addFragmentStyledLayoutStructureItem(
			fragmentEntryLink.getFragmentEntryLinkId(), "item2",
			containerStyledLayoutStructureItem.getItemId(), 0);

		_layoutPageTemplateStructureLocalService.
			updateLayoutPageTemplateStructureData(
				TestPropsValues.getUserId(), _group.getGroupId(),
				_layout.getPlid(), defaultSegmentsExperienceId,
				layoutStructure.toString());

		FragmentComposition fragmentComposition = _testAddFragmentComposition(
			fragmentCollection, containerStyledLayoutStructureItem.getItemId(),
			_getMockLiferayPortletActionRequest());

		String expectedFragmentCompositionData = StringUtil.replace(
			_read("expected_fragment_composition_data.json"), "\"${", "}\"",
			valuesMap);

		JSONObject expectedFragmentCompositionDataJSONObject =
			JSONFactoryUtil.createJSONObject(expectedFragmentCompositionData);

		JSONObject fragmentCompositionDataJSONObject =
			fragmentComposition.getDataJSONObject();

		Assert.assertEquals(
			_objectMapper.readTree(
				expectedFragmentCompositionDataJSONObject.toString()),
			_objectMapper.readTree(
				fragmentCompositionDataJSONObject.toString()));
	}

	@Test
	@TestInfo("LPD-53905")
	public void testAddFragmentCompositionWithItemSelectorTypeFragmentConfigurationField()
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
				StringPool.BLANK,
				"<div class=\"fragment_1\"><a href=${configuration.myURL}>" +
					RandomTestUtil.randomString() + "</a></div>",
				StringPool.BLANK, false,
				JSONUtil.put(
					"fieldSets",
					JSONUtil.put(
						JSONUtil.put(
							"fields",
							JSONUtil.put(
								JSONUtil.put(
									"label", RandomTestUtil.randomString()
								).put(
									"name", "itemSelector"
								).put(
									"type", "itemSelector"
								).put(
									"typeOptions",
									JSONUtil.put(
										"enableSelectTemplate", Boolean.FALSE)
								))))
				).toString(),
				null, 0, false, false, FragmentConstants.TYPE_COMPONENT, null,
				WorkflowConstants.STATUS_APPROVED, _serviceContext);

		_testAddFragmentCompositionWithItemSelectorTypeFragmentConfigurationField(
			fragmentCollection, fragmentEntry,
			JSONUtil.put(
				"className", FileEntry.class.getName()
			).put(
				"classNameId", _portal.getClassNameId(FileEntry.class.getName())
			).put(
				"classTypeId", "0"
			).put(
				"itemSubtype", "Basic Document"
			).put(
				"itemType", "Document"
			).put(
				"title", RandomTestUtil.randomString()
			).put(
				"type", InfoItemItemSelectorReturnType.class.getName()
			));

		_testAddFragmentCompositionWithItemSelectorTypeFragmentConfigurationField(
			fragmentCollection, fragmentEntry, _jsonFactory.createJSONObject());
	}

	@Test
	@TestInfo("LPD-61879")
	public void testAddFragmentCompositionWithNamespaceInEditableID()
		throws Exception {

		_layout = LayoutTestUtil.addTypeContentLayout(_group);

		FragmentCollection fragmentCollection =
			_fragmentCollectionLocalService.addFragmentCollection(
				null, TestPropsValues.getUserId(), _group.getGroupId(),
				RandomTestUtil.randomString(), StringPool.BLANK,
				_serviceContext);

		String html =
			"<div> data-lfr-editable-id=\"${fragmentEntryLinkNamespace}-" +
				"element-text\"\n\tdata-lfr-editable-type=\"text\">\n" +
					"\tHeading Example</div>";

		FragmentEntry fragmentEntry =
			_fragmentEntryLocalService.addFragmentEntry(
				null, TestPropsValues.getUserId(), _group.getGroupId(),
				fragmentCollection.getFragmentCollectionId(),
				"example-fragment-entry-key", RandomTestUtil.randomString(),
				StringPool.BLANK, html, StringPool.BLANK, false,
				StringPool.BLANK, null, 0, false, false,
				FragmentConstants.TYPE_COMPONENT, null,
				WorkflowConstants.STATUS_APPROVED, _serviceContext);

		long defaultSegmentsExperienceId =
			_segmentsExperienceLocalService.fetchDefaultSegmentsExperienceId(
				_layout.getPlid());

		FragmentEntryLink fragmentEntryLink =
			_fragmentEntryLinkLocalService.addFragmentEntryLink(
				null, TestPropsValues.getUserId(), _group.getGroupId(), 0,
				fragmentEntry.getFragmentEntryId(), defaultSegmentsExperienceId,
				_layout.getPlid(), StringPool.BLANK, html, StringPool.BLANK,
				StringPool.BLANK, StringPool.BLANK, StringPool.BLANK, 0, null,
				fragmentEntry.getType(), _serviceContext);

		_fragmentEntryLinkLocalService.updateFragmentEntryLink(
			TestPropsValues.getUserId(),
			fragmentEntryLink.getFragmentEntryLinkId(),
			JSONUtil.put(
				FragmentEntryProcessorConstants.
					KEY_EDITABLE_FRAGMENT_ENTRY_PROCESSOR,
				JSONUtil.put(
					fragmentEntryLink.getNamespace() + "-element-text",
					JSONUtil.put(
						LocaleUtil.toLanguageId(
							_portal.getSiteDefaultLocale(_group)),
						RandomTestUtil.randomString()))
			).toString());

		LayoutStructure layoutStructure = new LayoutStructure();

		LayoutStructureItem rootLayoutStructureItem =
			layoutStructure.addRootLayoutStructureItem();

		LayoutStructureItem containerStyledLayoutStructureItem =
			layoutStructure.addContainerStyledLayoutStructureItem(
				"item1", rootLayoutStructureItem.getItemId(), 0);

		layoutStructure.addFragmentStyledLayoutStructureItem(
			fragmentEntryLink.getFragmentEntryLinkId(), "item2",
			containerStyledLayoutStructureItem.getItemId(), 0);

		_layoutPageTemplateStructureLocalService.
			updateLayoutPageTemplateStructureData(
				TestPropsValues.getUserId(), _group.getGroupId(),
				_layout.getPlid(), defaultSegmentsExperienceId,
				layoutStructure.toString());

		FragmentComposition fragmentComposition = _testAddFragmentComposition(
			fragmentCollection, containerStyledLayoutStructureItem.getItemId(),
			_getMockLiferayPortletActionRequest());

		String data = fragmentComposition.getData();

		Assert.assertTrue(data.contains("[$NAMESPACE$]"));
	}

	@Test
	public void testAddFragmentCompositionWithThumbnail() throws Exception {
		MockLiferayPortletActionRequest mockLiferayPortletActionRequest =
			_getMockLiferayPortletActionRequest();

		LayoutPageTemplateStructure layoutPageTemplateStructure =
			_layoutPageTemplateStructureLocalService.
				fetchLayoutPageTemplateStructure(
					_group.getGroupId(), _layout.getPlid());

		LayoutStructure layoutStructure = LayoutStructure.of(
			layoutPageTemplateStructure.getDefaultSegmentsExperienceData());

		FragmentCollection newFragmentCollection =
			_fragmentCollectionLocalService.addFragmentCollection(
				null, TestPropsValues.getUserId(), _group.getGroupId(),
				StringUtil.randomString(), StringPool.BLANK,
				ServiceContextThreadLocal.getServiceContext());

		mockLiferayPortletActionRequest.addParameter(
			"fragmentCollectionId",
			String.valueOf(newFragmentCollection.getFragmentCollectionId()));

		mockLiferayPortletActionRequest.addParameter(
			"name", RandomTestUtil.randomString());
		mockLiferayPortletActionRequest.addParameter(
			"itemId", layoutStructure.getMainItemId());

		FileEntry fileEntry = _dlAppLocalService.addFileEntry(
			null, TestPropsValues.getUserId(), _group.getGroupId(),
			DLFolderConstants.DEFAULT_PARENT_FOLDER_ID, "liferay.jpg",
			ContentTypes.IMAGE_JPEG,
			FileUtil.getBytes(getClass(), "dependencies/liferay.jpg"), null,
			null, null, ServiceContextThreadLocal.getServiceContext());

		mockLiferayPortletActionRequest.addParameter(
			"fileEntryId", String.valueOf(fileEntry.getFileEntryId()));

		JSONObject jsonObject = ReflectionTestUtil.invoke(
			_mvcActionCommand, "doTransactionalCommand",
			new Class<?>[] {ActionRequest.class, ActionResponse.class},
			mockLiferayPortletActionRequest,
			new MockLiferayPortletActionResponse());

		Assert.assertNotNull(jsonObject);

		JSONObject fragmentCompositionJSONObject = jsonObject.getJSONObject(
			"fragmentComposition");

		FragmentComposition fragmentComposition =
			_fragmentCompositionLocalService.fetchFragmentComposition(
				_group.getGroupId(),
				fragmentCompositionJSONObject.getString("fragmentEntryKey"));

		Assert.assertNotNull(fragmentComposition);

		long previewFileEntryId = fragmentComposition.getPreviewFileEntryId();

		Assert.assertTrue(previewFileEntryId > 0);

		FileEntry previewFileEntry = _portletFileRepository.getPortletFileEntry(
			previewFileEntryId);

		Assert.assertTrue(Validator.isNotNull(previewFileEntry.getExtension()));
	}

	private JournalArticle _addJournalArticle(String title) throws Exception {
		return JournalTestUtil.addArticle(
			_group.getGroupId(), 0,
			PortalUtil.getClassNameId(JournalArticle.class),
			HashMapBuilder.put(
				LocaleUtil.US, title
			).build(),
			null,
			HashMapBuilder.put(
				LocaleUtil.US, RandomTestUtil.randomString()
			).build(),
			LocaleUtil.getSiteDefault(), false, true, _serviceContext);
	}

	private MockLiferayPortletActionRequest
			_getMockLiferayPortletActionRequest()
		throws Exception {

		MockLiferayPortletActionRequest mockLiferayPortletActionRequest =
			new MockLiferayPortletActionRequest();

		mockLiferayPortletActionRequest.setAttribute(
			WebKeys.THEME_DISPLAY, _getThemeDisplay());

		mockLiferayPortletActionRequest.addParameter(
			"groupId", String.valueOf(_group.getGroupId()));
		mockLiferayPortletActionRequest.addParameter(
			"segmentsExperienceId",
			String.valueOf(
				_segmentsExperienceLocalService.
					fetchDefaultSegmentsExperienceId(_layout.getPlid())));

		return mockLiferayPortletActionRequest;
	}

	private ThemeDisplay _getThemeDisplay() throws Exception {
		ThemeDisplay themeDisplay = new ThemeDisplay();

		themeDisplay.setCompany(_company);
		themeDisplay.setLayout(_layout);
		themeDisplay.setLayoutSet(_layout.getLayoutSet());
		themeDisplay.setPermissionChecker(
			PermissionThreadLocal.getPermissionChecker());
		themeDisplay.setPlid(_layout.getPlid());
		themeDisplay.setScopeGroupId(_group.getGroupId());
		themeDisplay.setSiteGroupId(_group.getGroupId());
		themeDisplay.setUser(TestPropsValues.getUser());

		return themeDisplay;
	}

	private String _read(String fileName) throws Exception {
		return new String(
			FileUtil.getBytes(getClass(), "dependencies/" + fileName));
	}

	private FragmentComposition _testAddFragmentComposition(
		FragmentCollection fragmentCollection, String itemId,
		MockLiferayPortletActionRequest mockLiferayPortletActionRequest) {

		mockLiferayPortletActionRequest.addParameter(
			"description", RandomTestUtil.randomString());
		mockLiferayPortletActionRequest.addParameter(
			"fragmentCollectionId",
			String.valueOf(fragmentCollection.getFragmentCollectionId()));
		mockLiferayPortletActionRequest.addParameter("itemId", itemId);
		mockLiferayPortletActionRequest.addParameter(
			"name", RandomTestUtil.randomString());
		mockLiferayPortletActionRequest.addParameter(
			"saveInlineContent", Boolean.TRUE.toString());
		mockLiferayPortletActionRequest.addParameter(
			"saveMappingConfiguration", Boolean.TRUE.toString());

		JSONObject jsonObject = ReflectionTestUtil.invoke(
			_mvcActionCommand, "doTransactionalCommand",
			new Class<?>[] {ActionRequest.class, ActionResponse.class},
			mockLiferayPortletActionRequest,
			new MockLiferayPortletActionResponse());

		JSONObject fragmentCompositionJSONObject = jsonObject.getJSONObject(
			"fragmentComposition");

		Assert.assertEquals(
			String.valueOf(fragmentCollection.getFragmentCollectionId()),
			fragmentCompositionJSONObject.getString("fragmentCollectionId"));
		Assert.assertEquals(
			fragmentCollection.getName(),
			fragmentCompositionJSONObject.getString("fragmentCollectionName"));
		Assert.assertTrue(
			Validator.isNotNull(
				fragmentCompositionJSONObject.getString("fragmentEntryKey")));
		Assert.assertEquals(
			String.valueOf(_group.getGroupId()),
			fragmentCompositionJSONObject.getString("groupId"));
		Assert.assertEquals(
			mockLiferayPortletActionRequest.getParameter("name"),
			fragmentCompositionJSONObject.getString("name"));
		Assert.assertEquals(
			"composition", fragmentCompositionJSONObject.getString("type"));

		FragmentComposition fragmentComposition =
			_fragmentCompositionLocalService.fetchFragmentComposition(
				_group.getGroupId(),
				fragmentCompositionJSONObject.getString("fragmentEntryKey"));

		Assert.assertNotNull(fragmentComposition);
		Assert.assertEquals(
			mockLiferayPortletActionRequest.getParameter("description"),
			fragmentComposition.getDescription());
		Assert.assertEquals(
			mockLiferayPortletActionRequest.getParameter("name"),
			fragmentComposition.getName());

		return fragmentComposition;
	}

	private void
			_testAddFragmentCompositionWithItemSelectorTypeFragmentConfigurationField(
				FragmentCollection fragmentCollection,
				FragmentEntry fragmentEntry, JSONObject jsonObject)
		throws Exception {

		Layout draftLayout = _layout.fetchDraftLayout();

		long segmentsExperienceId =
			_segmentsExperienceLocalService.fetchDefaultSegmentsExperienceId(
				draftLayout.getPlid());

		FragmentEntryLink fragmentEntryLink =
			_fragmentEntryLinkLocalService.addFragmentEntryLink(
				null, TestPropsValues.getUserId(), _group.getGroupId(), 0,
				fragmentEntry.getFragmentEntryId(), segmentsExperienceId,
				draftLayout.getPlid(), fragmentEntry.getCss(),
				fragmentEntry.getHtml(), fragmentEntry.getConfiguration(),
				fragmentEntry.getConfiguration(),
				JSONUtil.put(
					FragmentEntryProcessorConstants.
						KEY_FREEMARKER_FRAGMENT_ENTRY_PROCESSOR,
					JSONUtil.put("itemSelector", jsonObject)
				).toString(),
				StringPool.BLANK, 0, fragmentEntry.getFragmentEntryKey(),
				fragmentEntry.getType(), _serviceContext);

		JSONObject addItemJSONObject = ContentLayoutTestUtil.addItemToLayout(
			"{}", LayoutDataItemTypeConstants.TYPE_CONTAINER, draftLayout,
			_layoutStructureProvider, segmentsExperienceId);

		String containerItemId = addItemJSONObject.getString("addedItemId");

		ContentLayoutTestUtil.addFragmentEntryLinkToLayout(
			fragmentEntryLink, draftLayout, containerItemId, 0,
			segmentsExperienceId);

		FragmentComposition fragmentComposition = _testAddFragmentComposition(
			fragmentCollection, containerItemId,
			ContentLayoutTestUtil.getMockLiferayPortletActionRequest(
				_company, _group, draftLayout));

		JSONObject fragmentCompositionDataJSONObject =
			fragmentComposition.getDataJSONObject();

		JSONArray pageElementsJSONArray =
			fragmentCompositionDataJSONObject.getJSONArray("pageElements");

		Assert.assertEquals(
			pageElementsJSONArray.toString(), 1,
			pageElementsJSONArray.length());

		JSONObject pageElementJSONObject = pageElementsJSONArray.getJSONObject(
			0);

		Assert.assertEquals(
			"Fragment", pageElementJSONObject.getString("type"));

		JSONObject definitionJSONObject = pageElementJSONObject.getJSONObject(
			"definition");

		JSONObject fragmentJSONObject = definitionJSONObject.getJSONObject(
			"fragment");

		Assert.assertEquals(
			_group.getGroupKey(), fragmentJSONObject.getString("siteKey"));
		Assert.assertEquals(
			fragmentEntry.getFragmentEntryKey(),
			fragmentJSONObject.getString("key"));

		JSONObject fragmentConfigJSONObject =
			definitionJSONObject.getJSONObject("fragmentConfig");

		JSONObject itemSelectorJSONObject =
			fragmentConfigJSONObject.getJSONObject("itemSelector");

		Assert.assertEquals(
			jsonObject.toString(), itemSelectorJSONObject.toString());
	}

	private Company _company;

	@Inject
	private CompanyLocalService _companyLocalService;

	@Inject
	private DLAppLocalService _dlAppLocalService;

	@Inject
	private FragmentCollectionLocalService _fragmentCollectionLocalService;

	@Inject
	private FragmentCompositionLocalService _fragmentCompositionLocalService;

	@Inject
	private FragmentEntryLinkLocalService _fragmentEntryLinkLocalService;

	@Inject
	private FragmentEntryLocalService _fragmentEntryLocalService;

	@DeleteAfterTestRun
	private Group _group;

	@Inject
	private JSONFactory _jsonFactory;

	private Layout _layout;

	@Inject
	private LayoutPageTemplateStructureLocalService
		_layoutPageTemplateStructureLocalService;

	@Inject
	private LayoutStructureProvider _layoutStructureProvider;

	@Inject(
		filter = "mvc.command.name=/layout_content_page_editor/add_fragment_composition"
	)
	private MVCActionCommand _mvcActionCommand;

	private ObjectMapper _objectMapper;

	@Inject
	private Portal _portal;

	@Inject
	private PortletFileRepository _portletFileRepository;

	@Inject
	private SegmentsExperienceLocalService _segmentsExperienceLocalService;

	private ServiceContext _serviceContext;

}