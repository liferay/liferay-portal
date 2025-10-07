/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.taglib.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.asset.kernel.model.AssetEntry;
import com.liferay.asset.kernel.service.AssetEntryLocalService;
import com.liferay.asset.list.constants.AssetListEntryTypeConstants;
import com.liferay.asset.list.model.AssetListEntry;
import com.liferay.asset.list.model.AssetListEntrySegmentsEntryRel;
import com.liferay.asset.list.service.AssetListEntryLocalService;
import com.liferay.asset.list.service.AssetListEntrySegmentsEntryRelLocalService;
import com.liferay.document.library.kernel.model.DLFolderConstants;
import com.liferay.document.library.kernel.service.DLAppLocalService;
import com.liferay.document.library.util.DLURLHelper;
import com.liferay.dynamic.data.mapping.constants.DDMStructureConstants;
import com.liferay.dynamic.data.mapping.io.DDMFormDeserializer;
import com.liferay.dynamic.data.mapping.io.DDMFormDeserializerDeserializeRequest;
import com.liferay.dynamic.data.mapping.io.DDMFormDeserializerDeserializeResponse;
import com.liferay.dynamic.data.mapping.model.DDMForm;
import com.liferay.dynamic.data.mapping.model.DDMStructure;
import com.liferay.dynamic.data.mapping.storage.StorageType;
import com.liferay.dynamic.data.mapping.test.util.DDMStructureTestHelper;
import com.liferay.dynamic.data.mapping.test.util.DDMStructureTestUtil;
import com.liferay.fragment.constants.FragmentConstants;
import com.liferay.fragment.contributor.FragmentCollectionContributorRegistry;
import com.liferay.fragment.entry.processor.constants.FragmentEntryProcessorConstants;
import com.liferay.fragment.helper.DefaultInputFragmentEntryConfigurationProvider;
import com.liferay.fragment.listener.FragmentEntryLinkListener;
import com.liferay.fragment.listener.FragmentEntryLinkListenerRegistry;
import com.liferay.fragment.model.FragmentCollection;
import com.liferay.fragment.model.FragmentEntry;
import com.liferay.fragment.model.FragmentEntryLink;
import com.liferay.fragment.renderer.FragmentRendererRegistry;
import com.liferay.fragment.service.FragmentCollectionLocalService;
import com.liferay.fragment.service.FragmentEntryLinkLocalService;
import com.liferay.fragment.service.FragmentEntryLocalService;
import com.liferay.headless.delivery.dto.v1_0.MessageBoardMessage;
import com.liferay.info.collection.provider.CollectionQuery;
import com.liferay.info.collection.provider.InfoCollectionProvider;
import com.liferay.info.collection.provider.RepeatableFieldInfoItemCollectionProvider;
import com.liferay.info.constants.InfoDisplayWebKeys;
import com.liferay.info.exception.InfoFormValidationException;
import com.liferay.info.field.InfoField;
import com.liferay.info.field.InfoFieldSet;
import com.liferay.info.field.type.BooleanInfoFieldType;
import com.liferay.info.field.type.InfoFieldType;
import com.liferay.info.field.type.TextInfoFieldType;
import com.liferay.info.form.InfoForm;
import com.liferay.info.item.InfoItemReference;
import com.liferay.info.item.InfoItemServiceRegistry;
import com.liferay.info.item.capability.InfoItemCapability;
import com.liferay.info.item.provider.InfoItemDetailsProvider;
import com.liferay.info.item.provider.InfoItemFormProvider;
import com.liferay.info.list.provider.item.selector.criterion.InfoListProviderItemSelectorReturnType;
import com.liferay.info.list.renderer.InfoListRenderer;
import com.liferay.info.list.renderer.InfoListRendererRegistry;
import com.liferay.info.localized.InfoLocalizedValue;
import com.liferay.info.pagination.InfoPage;
import com.liferay.info.test.util.MockInfoServiceRegistrationHolder;
import com.liferay.info.test.util.model.MockObject;
import com.liferay.item.selector.criteria.InfoListItemSelectorReturnType;
import com.liferay.journal.constants.JournalArticleConstants;
import com.liferay.journal.constants.JournalFolderConstants;
import com.liferay.journal.model.JournalArticle;
import com.liferay.journal.service.JournalArticleLocalService;
import com.liferay.journal.test.util.JournalTestUtil;
import com.liferay.layout.display.page.LayoutDisplayPageObjectProvider;
import com.liferay.layout.display.page.LayoutDisplayPageProvider;
import com.liferay.layout.display.page.LayoutDisplayPageProviderRegistry;
import com.liferay.layout.display.page.constants.LayoutDisplayPageWebKeys;
import com.liferay.layout.manager.FormManager;
import com.liferay.layout.page.template.info.item.capability.DisplayPageInfoItemCapability;
import com.liferay.layout.page.template.info.item.capability.EditPageInfoItemCapability;
import com.liferay.layout.page.template.model.LayoutPageTemplateEntry;
import com.liferay.layout.page.template.model.LayoutPageTemplateStructure;
import com.liferay.layout.page.template.service.LayoutPageTemplateEntryService;
import com.liferay.layout.page.template.service.LayoutPageTemplateStructureLocalService;
import com.liferay.layout.page.template.service.LayoutPageTemplateStructureLocalServiceUtil;
import com.liferay.layout.page.template.test.util.DisplayPageTemplateTestUtil;
import com.liferay.layout.provider.LayoutStructureProvider;
import com.liferay.layout.taglib.servlet.taglib.RenderLayoutStructureTag;
import com.liferay.layout.test.util.ContentLayoutTestUtil;
import com.liferay.layout.test.util.LayoutFriendlyURLRandomizerBumper;
import com.liferay.layout.test.util.LayoutTestUtil;
import com.liferay.layout.util.constants.LayoutDataItemTypeConstants;
import com.liferay.layout.util.structure.ContainerStyledLayoutStructureItem;
import com.liferay.layout.util.structure.FormStyledLayoutStructureItem;
import com.liferay.layout.util.structure.FragmentStyledLayoutStructureItem;
import com.liferay.layout.util.structure.LayoutStructure;
import com.liferay.layout.util.structure.LayoutStructureItem;
import com.liferay.layout.util.structure.LayoutStructureRule;
import com.liferay.object.constants.ObjectDefinitionConstants;
import com.liferay.object.constants.ObjectEntryFolderConstants;
import com.liferay.object.constants.ObjectFieldConstants;
import com.liferay.object.field.builder.TextObjectFieldBuilder;
import com.liferay.object.field.util.ObjectFieldUtil;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.model.ObjectRelationship;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.object.service.ObjectRelationshipLocalService;
import com.liferay.object.test.util.ObjectDefinitionTestUtil;
import com.liferay.object.test.util.ObjectRelationshipTestUtil;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.cache.MultiVMPool;
import com.liferay.portal.kernel.dao.orm.EntityCache;
import com.liferay.portal.kernel.exception.InfoFormException;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.LayoutConstants;
import com.liferay.portal.kernel.model.LayoutTypePortlet;
import com.liferay.portal.kernel.model.LayoutTypePortletConstants;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.security.auth.PrincipalThreadLocal;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactoryUtil;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.service.LayoutLocalServiceUtil;
import com.liferay.portal.kernel.service.ResourcePermissionLocalServiceUtil;
import com.liferay.portal.kernel.service.RoleLocalServiceUtil;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.servlet.HttpMethods;
import com.liferay.portal.kernel.servlet.PortletServlet;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.servlet.SessionMessages;
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
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.HtmlUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.LoggingTimer;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.PropsValues;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.UnicodeProperties;
import com.liferay.portal.kernel.util.UnicodePropertiesBuilder;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.portal.util.PortalImpl;
import com.liferay.portal.vulcan.util.LocalizedMapUtil;
import com.liferay.segments.constants.SegmentsEntryConstants;
import com.liferay.segments.criteria.Criteria;
import com.liferay.segments.criteria.CriteriaSerializer;
import com.liferay.segments.criteria.contributor.SegmentsCriteriaContributor;
import com.liferay.segments.model.SegmentsEntry;
import com.liferay.segments.model.SegmentsExperience;
import com.liferay.segments.service.SegmentsExperienceLocalService;
import com.liferay.segments.test.util.SegmentsTestUtil;
import com.liferay.template.model.TemplateEntry;
import com.liferay.template.test.util.TemplateTestUtil;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;

import jakarta.servlet.http.HttpServletRequest;

import java.io.Serializable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceRegistration;

import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockPageContext;

/**
 * @author Eudaldo Alonso
 */
@RunWith(Arquillian.class)
public class RenderLayoutStructureTagTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();

		_originalName = PrincipalThreadLocal.getName();

		PrincipalThreadLocal.setName(TestPropsValues.getUserId());

		_serviceContext = ServiceContextTestUtil.getServiceContext(
			TestPropsValues.getGroupId(), TestPropsValues.getUserId());

		ServiceContextThreadLocal.pushServiceContext(_serviceContext);
	}

	@After
	public void tearDown() {
		PrincipalThreadLocal.setName(_originalName);
		ServiceContextThreadLocal.popServiceContext();
	}

	@FeatureFlag("LPD-21926")
	@Test
	@TestInfo({"LPD-50584", "LPD-52416"})
	public void testDisplayPageTemplateWithMappedFriendlyURLInfoField()
		throws Exception {

		ObjectDefinition objectDefinition =
			_objectDefinitionLocalService.addCustomObjectDefinition(
				TestPropsValues.getUserId(), 0, null, false, true, true, true,
				true, false, false, false, false, null,
				RandomTestUtil.randomLocaleStringMap(),
				ObjectDefinitionTestUtil.getRandomName(), null, null,
				RandomTestUtil.randomLocaleStringMap(), true,
				ObjectDefinitionConstants.SCOPE_COMPANY,
				ObjectDefinitionConstants.STORAGE_TYPE_DEFAULT,
				Collections.emptyList(),
				ListUtil.fromArray(
					ObjectFieldUtil.createObjectField(
						ObjectFieldConstants.BUSINESS_TYPE_TEXT,
						ObjectFieldConstants.DB_TYPE_STRING,
						RandomTestUtil.randomString(), "text")),
				Collections.emptyList());

		_objectDefinitionLocalService.publishCustomObjectDefinition(
			TestPropsValues.getUserId(),
			objectDefinition.getObjectDefinitionId());

		objectDefinition.setEnableFriendlyURLCustomization(true);

		ObjectDefinition relationshipObjectDefinition =
			_objectDefinitionLocalService.addCustomObjectDefinition(
				TestPropsValues.getUserId(), 0, null, false, true, true, true,
				true, false, false, false, false, null,
				RandomTestUtil.randomLocaleStringMap(),
				ObjectDefinitionTestUtil.getRandomName(), null, null,
				RandomTestUtil.randomLocaleStringMap(), true,
				ObjectDefinitionConstants.SCOPE_COMPANY,
				ObjectDefinitionConstants.STORAGE_TYPE_DEFAULT,
				Collections.emptyList(),
				ListUtil.fromArray(
					ObjectFieldUtil.createObjectField(
						ObjectFieldConstants.BUSINESS_TYPE_TEXT,
						ObjectFieldConstants.DB_TYPE_STRING,
						RandomTestUtil.randomString(), "text")),
				Collections.emptyList());

		_objectDefinitionLocalService.publishCustomObjectDefinition(
			TestPropsValues.getUserId(),
			relationshipObjectDefinition.getObjectDefinitionId());

		relationshipObjectDefinition.setEnableFriendlyURLCustomization(true);

		ObjectRelationship objectRelationship =
			ObjectRelationshipTestUtil.addObjectRelationship(
				_objectRelationshipLocalService, objectDefinition,
				relationshipObjectDefinition);

		long classNameId = _portal.getClassNameId(
			objectDefinition.getClassName());

		LayoutPageTemplateEntry layoutPageTemplateEntry =
			DisplayPageTemplateTestUtil.addDisplayPageTemplate(
				_group.getGroupId(), classNameId, 0, true,
				WorkflowConstants.STATUS_APPROVED);

		Layout layout = _layoutLocalService.getLayout(
			layoutPageTemplateEntry.getPlid());

		Layout draftLayout = layout.fetchDraftLayout();

		long segmentsExperienceId =
			_segmentsExperienceLocalService.fetchDefaultSegmentsExperienceId(
				draftLayout.getPlid());

		JSONObject jsonObject = ContentLayoutTestUtil.addFormToLayout(
			false, String.valueOf(classNameId), "0", draftLayout,
			_layoutStructureProvider, segmentsExperienceId);

		LayoutStructure layoutStructure =
			_layoutStructureProvider.getLayoutStructure(
				draftLayout.getPlid(), segmentsExperienceId);

		List<FragmentEntryLink> addedFragmentEntryLinks = new ArrayList<>();

		_formManager.addFragmentEntryLinksLayoutStructureItems(
			addedFragmentEntryLinks, _jsonFactory.createJSONObject(),
			(FormStyledLayoutStructureItem)
				layoutStructure.getLayoutStructureItem(
					jsonObject.getString("addedItemId")),
			true, draftLayout, layoutStructure,
			LocaleUtil.getMostRelevantLocale(), false, segmentsExperienceId,
			_serviceContext, null);

		_layoutPageTemplateStructureLocalService.
			updateLayoutPageTemplateStructureData(
				TestPropsValues.getUserId(), draftLayout.getGroupId(),
				draftLayout.getPlid(), segmentsExperienceId,
				layoutStructure.toString());

		for (FragmentEntryLink addedFragmentEntryLink :
				addedFragmentEntryLinks) {

			for (FragmentEntryLinkListener fragmentEntryLinkListener :
					_fragmentEntryLinkListenerRegistry.
						getFragmentEntryLinkListeners()) {

				fragmentEntryLinkListener.onAddFragmentEntryLink(
					addedFragmentEntryLink);
			}
		}

		ContentLayoutTestUtil.publishLayout(draftLayout, layout);

		layoutStructure = _layoutStructureProvider.getLayoutStructure(
			layout.getPlid(),
			_segmentsExperienceLocalService.fetchDefaultSegmentsExperienceId(
				layout.getPlid()));

		Map<Long, LayoutStructureItem> fragmentLayoutStructureItems =
			layoutStructure.getFragmentLayoutStructureItems();

		Assert.assertEquals(
			MapUtil.toString(fragmentLayoutStructureItems), 4,
			fragmentLayoutStructureItems.size());

		String content = _getRenderLayoutHTML(layout);

		List<String> expectedList = new ArrayList<>();
		String externalReferenceCode = RandomTestUtil.randomString();
		String friendlyURLValue = StringUtil.toLowerCase(
			StringUtil.removeSubstring(
				RandomTestUtil.randomString(
					LayoutFriendlyURLRandomizerBumper.INSTANCE),
				StringPool.SLASH));
		String relationshipFriendlyURLValue = StringUtil.toLowerCase(
			StringUtil.removeSubstring(
				RandomTestUtil.randomString(
					LayoutFriendlyURLRandomizerBumper.INSTANCE),
				StringPool.SLASH));
		String relationshipTextValue = RandomTestUtil.randomString();
		String textValue = RandomTestUtil.randomString();

		for (Map.Entry<Long, LayoutStructureItem> entry :
				fragmentLayoutStructureItems.entrySet()) {

			FragmentEntryLink fragmentEntryLink =
				_fragmentEntryLinkLocalService.getFragmentEntryLink(
					entry.getKey());

			if (Objects.equals(
					fragmentEntryLink.getRendererKey(),
					"INPUTS-submit-button")) {

				continue;
			}

			String expectedContent = StringBundler.concat(
				"id=\"", fragmentEntryLink.getNamespace(),
				"-text-input\" name=\"text\" placeholder=\"\" type=\"text\" ",
				"value=\"");

			JSONObject editableValueJSONObject =
				fragmentEntryLink.getEditableValuesJSONObject();

			JSONObject fremarkerFragmentEntryProcessorJSONObject =
				editableValueJSONObject.getJSONObject(
					FragmentEntryProcessorConstants.
						KEY_FREEMARKER_FRAGMENT_ENTRY_PROCESSOR);

			String namespace = StringBundler.concat(
				ObjectRelationship.class.getName(), StringPool.POUND,
				relationshipObjectDefinition.getName(), StringPool.POUND,
				objectRelationship.getName());

			if (Objects.equals(
					fremarkerFragmentEntryProcessorJSONObject.getString(
						"inputFieldId"),
					"ObjectEntry_externalReferenceCode")) {

				expectedContent = StringBundler.concat(
					"id=\"", fragmentEntryLink.getNamespace(),
					"-text-input\" name=\"externalReferenceCode\" ",
					"placeholder=\"\" type=\"text\" value=\"");

				expectedList.add(
					expectedContent + externalReferenceCode + StringPool.QUOTE);
			}
			else if (Objects.equals(
						fremarkerFragmentEntryProcessorJSONObject.getString(
							"inputFieldId"),
						"ObjectEntry_objectEntryFriendlyURL")) {

				expectedContent = StringBundler.concat(
					"id=\"", fragmentEntryLink.getNamespace(),
					"-friendly-url-input\" name=\"objectEntryFriendlyURL\" ",
					"type=\"text\" value=\"");

				expectedList.add(
					expectedContent + friendlyURLValue + StringPool.QUOTE);
			}
			else if (Objects.equals(
						fremarkerFragmentEntryProcessorJSONObject.getString(
							"inputFieldId"),
						StringBundler.concat(
							namespace, StringPool.UNDERLINE,
							objectRelationship.getName(),
							"_objectEntryFriendlyURL"))) {

				expectedContent = StringBundler.concat(
					"id=\"", fragmentEntryLink.getNamespace(),
					"-friendly-url-input\" name=\"",
					objectRelationship.getName(),
					"_objectEntryFriendlyURL\"\" type=\"text\" value=\"");

				expectedList.add(
					expectedContent + relationshipFriendlyURLValue +
						StringPool.QUOTE);
			}
			else if (Objects.equals(
						fremarkerFragmentEntryProcessorJSONObject.getString(
							"inputFieldId"),
						StringBundler.concat(
							namespace, StringPool.UNDERLINE,
							objectRelationship.getName(), "_text"))) {

				expectedList.add(
					expectedContent + relationshipTextValue + StringPool.QUOTE);
			}
			else {
				expectedList.add(
					expectedContent + textValue + StringPool.QUOTE);
			}

			Assert.assertTrue(
				content, content.contains(expectedContent + StringPool.QUOTE));
		}

		_serviceContext.setAttribute(
			"friendlyUrlMap",
			HashMapBuilder.put(
				relationshipObjectDefinition.getDefaultLanguageId(),
				relationshipFriendlyURLValue
			).build());

		ObjectEntry relationshipObjectEntry =
			_objectEntryLocalService.addObjectEntry(
				0, TestPropsValues.getUserId(),
				relationshipObjectDefinition.getObjectDefinitionId(),
				ObjectEntryFolderConstants.
					PARENT_OBJECT_ENTRY_FOLDER_ID_DEFAULT,
				null,
				HashMapBuilder.<String, Serializable>put(
					"text", relationshipTextValue
				).build(),
				_serviceContext);

		Assert.assertEquals(
			relationshipFriendlyURLValue,
			relationshipObjectEntry.getURLTitle(
				objectDefinition.getDefaultLocale()));

		_serviceContext.setAttribute(
			"friendlyUrlMap",
			HashMapBuilder.put(
				objectDefinition.getDefaultLanguageId(), friendlyURLValue
			).build());

		ObjectEntry objectEntry = _objectEntryLocalService.addObjectEntry(
			0, TestPropsValues.getUserId(),
			objectDefinition.getObjectDefinitionId(),
			ObjectEntryFolderConstants.PARENT_OBJECT_ENTRY_FOLDER_ID_DEFAULT,
			null,
			HashMapBuilder.<String, Serializable>put(
				objectRelationship.getName(),
				relationshipObjectEntry.getObjectEntryId()
			).put(
				"externalReferenceCode", externalReferenceCode
			).put(
				"text", textValue
			).build(),
			_serviceContext);

		Assert.assertEquals(
			friendlyURLValue,
			objectEntry.getURLTitle(objectDefinition.getDefaultLocale()));

		LayoutDisplayPageProvider<?> layoutDisplayPageProvider =
			_layoutDisplayPageProviderRegistry.
				getLayoutDisplayPageProviderByClassName(
					objectDefinition.getClassName());

		MockHttpServletRequest mockHttpServletRequest =
			_getMockHttpServletRequest(
				layout,
				layoutDisplayPageProvider.getLayoutDisplayPageObjectProvider(
					new InfoItemReference(
						objectDefinition.getClassName(),
						objectEntry.getObjectEntryId())),
				Collections.emptyMap(), null);

		mockHttpServletRequest.setAttribute(
			InfoDisplayWebKeys.INFO_ITEM, objectEntry);

		InfoItemDetailsProvider infoItemDetailsProvider =
			_infoItemServiceRegistry.getFirstInfoItemService(
				InfoItemDetailsProvider.class, objectDefinition.getClassName());

		mockHttpServletRequest.setAttribute(
			InfoDisplayWebKeys.INFO_ITEM_DETAILS,
			infoItemDetailsProvider.getInfoItemDetails(objectEntry));

		_serviceContext.setRequest(mockHttpServletRequest);

		ServiceContextThreadLocal.pushServiceContext(_serviceContext);

		try {
			MockHttpServletResponse mockHttpServletResponse = _renderLayout(
				layout, mockHttpServletRequest);

			content = mockHttpServletResponse.getContentAsString();
		}
		finally {
			ServiceContextThreadLocal.popServiceContext();
		}

		for (String value : expectedList) {
			Assert.assertTrue(content, content.contains(value));
		}

		_objectDefinitionLocalService.deleteObjectDefinition(objectDefinition);
		_objectDefinitionLocalService.deleteObjectDefinition(
			relationshipObjectDefinition);
	}

	@Test
	@TestInfo("LPD-62906")
	public void testEmptyListStyleSetsCssClassBasedOnColumns()
		throws Exception {

		AssetListEntry assetListEntry =
			_assetListEntryLocalService.addAssetListEntry(
				null, TestPropsValues.getUserId(), _group.getGroupId(),
				RandomTestUtil.randomString(),
				AssetListEntryTypeConstants.TYPE_DYNAMIC, _serviceContext);

		JournalTestUtil.addArticle(
			_group.getGroupId(),
			JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID);

		Layout layout = LayoutTestUtil.addTypeContentLayout(_group);

		Layout draftLayout = layout.fetchDraftLayout();

		long segmentsExperienceId =
			_segmentsExperienceLocalService.fetchDefaultSegmentsExperienceId(
				draftLayout.getPlid());

		_addCollectionStyledLayoutStructureItem(
			JSONUtil.put(
				"classNameId", _portal.getClassNameId(AssetListEntry.class)
			).put(
				"classPK", assetListEntry.getAssetListEntryId()
			).put(
				"itemType", AssetEntry.class.getName()
			).put(
				"type", InfoListItemSelectorReturnType.class.getName()
			),
			JSONUtil.put(
				"align", "align-items-center"
			).put(
				"flex-wrap", "wrap"
			).put(
				"justify", "justify-content-center"
			),
			layout, "flex-row", segmentsExperienceId,
			_addFragmentEntryLinks(
				1, null, layout.fetchDraftLayout(), segmentsExperienceId));

		MockHttpServletResponse mockHttpServletResponse = _renderLayout(
			layout, _getMockHttpServletRequest(layout));

		String content = mockHttpServletResponse.getContentAsString();

		Assert.assertFalse(content, content.contains("col-"));
	}

	@Test
	@TestInfo({"LPD-33573", "LPD-55091"})
	public void testEnsureFileURLWhenChangingGroupFriendlyURL()
		throws Exception {

		Layout layout = LayoutTestUtil.addTypeContentLayout(_group);

		FileEntry fileEntry = _addFileEntry();

		String url = _dlURLHelper.getPreviewURL(
			fileEntry, fileEntry.getFileVersion(), null, StringPool.BLANK,
			false, false);

		Assert.assertTrue(url.contains(_group.getFriendlyURL()));

		Layout draftLayout = layout.fetchDraftLayout();

		long segmentExperienceId =
			_segmentsExperienceLocalService.fetchDefaultSegmentsExperienceId(
				draftLayout.getPlid());

		ContentLayoutTestUtil.addItemToLayout(
			"{}", LayoutDataItemTypeConstants.TYPE_CONTAINER,
			layout.fetchDraftLayout(), _layoutStructureProvider,
			segmentExperienceId);

		ContentLayoutTestUtil.addItemToLayout(
			JSONUtil.put(
				"styles",
				JSONUtil.put(
					"backgroundImage",
					JSONUtil.put(
						"classNameId", _portal.getClassNameId(FileEntry.class)
					).put(
						"classPK", fileEntry.getFileEntryId()
					).put(
						"fileEntryId", fileEntry.getFileEntryId()
					).put(
						"url", url
					))
			).toString(),
			LayoutDataItemTypeConstants.TYPE_CONTAINER,
			layout.fetchDraftLayout(), _layoutStructureProvider,
			segmentExperienceId);

		ContentLayoutTestUtil.publishLayout(layout.fetchDraftLayout(), layout);

		String content = _getRenderLayoutHTML(layout);

		Assert.assertFalse(content, content.contains("style=\"\""));
		Assert.assertTrue(content, content.contains(url));
		Assert.assertTrue(content, content.contains("style=\""));

		_groupLocalService.updateFriendlyURL(
			_group.getGroupId(), "/new-friendly-url");

		url = _dlURLHelper.getPreviewURL(
			fileEntry, fileEntry.getFileVersion(), null, StringPool.BLANK,
			false, false);

		Assert.assertTrue(url.contains("/new-friendly-url"));

		content = _getRenderLayoutHTML(layout);

		Assert.assertTrue(content, content.contains(url));
	}

	@Test
	public void testLayoutStructureRules() throws Exception {
		ObjectDefinition objectDefinition =
			ObjectDefinitionTestUtil.publishObjectDefinition(
				ListUtil.fromArray(
					ObjectFieldUtil.createObjectField(
						ObjectFieldConstants.BUSINESS_TYPE_TEXT,
						ObjectFieldConstants.DB_TYPE_STRING,
						RandomTestUtil.randomString(), "text"),
					ObjectFieldUtil.createObjectField(
						ObjectFieldConstants.BUSINESS_TYPE_BOOLEAN,
						ObjectFieldConstants.DB_TYPE_BOOLEAN,
						RandomTestUtil.randomString(), "boolean")));

		Layout layout = LayoutTestUtil.addTypeContentLayout(_group);

		Layout draftLayout = layout.fetchDraftLayout();

		Map<String, String> inputTypesMap = _addFormToLayout(
			objectDefinition.getClassName(), draftLayout);

		_addLayoutStructureRule(inputTypesMap, draftLayout);

		ContentLayoutTestUtil.publishLayout(draftLayout, layout);

		String content = _getRenderLayoutHTML(layout);

		LayoutStructure layoutStructure =
			_layoutStructureProvider.getLayoutStructure(
				layout.getPlid(),
				_segmentsExperienceLocalService.
					fetchDefaultSegmentsExperienceId(layout.getPlid()));

		FragmentStyledLayoutStructureItem
			submitButtonFragmentStyledLayoutStructureItem =
				(FragmentStyledLayoutStructureItem)
					layoutStructure.getLayoutStructureItem(
						inputTypesMap.get(
							DefaultInputFragmentEntryConfigurationProvider.
								FORM_INPUT_SUBMIT_BUTTON));

		FragmentEntryLink submitButtonFragmentEntryLink =
			_fragmentEntryLinkLocalService.getFragmentEntryLink(
				submitButtonFragmentStyledLayoutStructureItem.
					getFragmentEntryLinkId());

		String submitButtonIdAttribute =
			"id=\"fragment-" + submitButtonFragmentEntryLink.getNamespace() +
				"-submit-button\"";

		Assert.assertTrue(
			content, content.contains("disabled " + submitButtonIdAttribute));

		FragmentStyledLayoutStructureItem
			textInputFragmentStyledLayoutStructureItem =
				(FragmentStyledLayoutStructureItem)
					layoutStructure.getLayoutStructureItem(
						inputTypesMap.get(
							TextInfoFieldType.INSTANCE.getName()));

		FragmentEntryLink textInputFragmentEntryLink =
			_fragmentEntryLinkLocalService.getFragmentEntryLink(
				textInputFragmentStyledLayoutStructureItem.
					getFragmentEntryLinkId());

		String textInputIdAttribute =
			"id=\"" + textInputFragmentEntryLink.getNamespace() +
				"-text-input\"";

		Assert.assertFalse(content, content.contains(textInputIdAttribute));

		content = _getRenderLayoutHTML(
			layout, Collections.emptyMap(),
			UserTestUtil.addCompanyAdminUser(
				_companyLocalService.getCompany(_group.getCompanyId())));

		Assert.assertFalse(
			content, content.contains("disabled " + submitButtonIdAttribute));
		Assert.assertTrue(content, content.contains(submitButtonIdAttribute));
		Assert.assertTrue(content, content.contains(textInputIdAttribute));
	}

	@Test
	public void testRemovedLayoutTemplateId() throws Exception {
		Layout layout = _layoutLocalService.addLayout(
			null, TestPropsValues.getUserId(), _group.getGroupId(), false,
			LayoutConstants.DEFAULT_PARENT_LAYOUT_ID,
			RandomTestUtil.randomString(), RandomTestUtil.randomString(),
			StringPool.BLANK, LayoutConstants.TYPE_PORTLET, false,
			StringPool.BLANK, _serviceContext);

		UnicodeProperties typeSettingsUnicodeProperties =
			layout.getTypeSettingsProperties();

		typeSettingsUnicodeProperties.setProperty(
			LayoutTypePortletConstants.LAYOUT_TEMPLATE_ID,
			"removed-template-id");

		layout = LayoutLocalServiceUtil.updateLayout(
			layout.getGroupId(), layout.isPrivateLayout(), layout.getLayoutId(),
			typeSettingsUnicodeProperties.toString());

		LayoutTypePortlet layoutTypePortlet =
			(LayoutTypePortlet)layout.getLayoutType();

		Assert.assertEquals(
			"removed-template-id", layoutTypePortlet.getLayoutTemplateId());

		RenderLayoutStructureTag renderLayoutStructureTag =
			new RenderLayoutStructureTag();

		renderLayoutStructureTag.setLayoutStructure(
			_getDefaultMasterLayoutStructure());

		renderLayoutStructureTag.doTag(
			_getMockHttpServletRequest(layout), new MockHttpServletResponse());

		Assert.assertEquals(
			PropsValues.DEFAULT_LAYOUT_TEMPLATE_ID,
			layoutTypePortlet.getLayoutTemplateId());

		layout = _layoutLocalService.fetchLayout(layout.getPlid());

		layoutTypePortlet = (LayoutTypePortlet)layout.getLayoutType();

		Assert.assertEquals(
			"removed-template-id", layoutTypePortlet.getLayoutTemplateId());
	}

	@Test
	public void testRenderCollectionStyledLayoutStructureItem()
		throws Exception {

		AssetListEntry assetListEntry =
			_assetListEntryLocalService.addAssetListEntry(
				null, TestPropsValues.getUserId(), _group.getGroupId(),
				RandomTestUtil.randomString(),
				AssetListEntryTypeConstants.TYPE_MANUAL, _serviceContext);

		Layout layout = LayoutTestUtil.addTypeContentLayout(_group);

		Layout draftLayout = layout.fetchDraftLayout();

		long segmentsExperienceId =
			_segmentsExperienceLocalService.fetchDefaultSegmentsExperienceId(
				draftLayout.getPlid());

		_addCollectionStyledLayoutStructureItem(
			assetListEntry, layout, _COUNT_INFO_LIST_ITEMS, "none",
			segmentsExperienceId,
			_addFragmentEntryLinks(
				_COUNT_FRAGMENT_ENTRY_LINKS,
				JSONUtil.put("collectionFieldId", "JournalArticle_title"),
				layout.fetchDraftLayout(), segmentsExperienceId));

		List<AssetEntry> assetEntries = _addAssetEntries(assetListEntry);

		MockHttpServletRequest mockHttpServletRequest =
			_getMockHttpServletRequest(layout);
		MockHttpServletResponse mockHttpServletResponse =
			new MockHttpServletResponse();

		RenderLayoutStructureTag renderLayoutStructureTag =
			_getRenderLayoutStructureTag(
				layout, mockHttpServletRequest, mockHttpServletResponse,
				segmentsExperienceId);

		_entityCache.clearCache();
		_multiVMPool.clear();

		try (LoggingTimer loggingTimer = new LoggingTimer()) {
			renderLayoutStructureTag.doTag(
				mockHttpServletRequest, mockHttpServletResponse);
		}

		String content = mockHttpServletResponse.getContentAsString();

		for (AssetEntry assetEntry : assetEntries) {
			int count = StringUtil.count(
				content,
				assetEntry.getTitle(assetEntry.getDefaultLanguageId()));

			Assert.assertTrue(
				String.valueOf(count), count >= _COUNT_FRAGMENT_ENTRY_LINKS);
		}
	}

	@Test
	@TestInfo("LPS-163440")
	public void testRenderCollectionStyledLayoutStructureItemForFlexRowListStyle()
		throws Exception {

		AssetListEntry assetListEntry =
			_assetListEntryLocalService.addAssetListEntry(
				null, TestPropsValues.getUserId(), _group.getGroupId(),
				RandomTestUtil.randomString(),
				AssetListEntryTypeConstants.TYPE_DYNAMIC, _serviceContext);

		JournalTestUtil.addArticle(
			_group.getGroupId(),
			JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID);

		Layout layout = LayoutTestUtil.addTypeContentLayout(_group);

		Layout draftLayout = layout.fetchDraftLayout();

		long segmentsExperienceId =
			_segmentsExperienceLocalService.fetchDefaultSegmentsExperienceId(
				draftLayout.getPlid());

		_addCollectionStyledLayoutStructureItem(
			JSONUtil.put(
				"classNameId", _portal.getClassNameId(AssetListEntry.class)
			).put(
				"classPK", assetListEntry.getAssetListEntryId()
			).put(
				"itemType", AssetEntry.class.getName()
			).put(
				"type", InfoListItemSelectorReturnType.class.getName()
			),
			JSONUtil.put(
				"align", "align-items-end"
			).put(
				"justify", "justify-content-center"
			),
			layout, "flex-row", segmentsExperienceId,
			_addFragmentEntryLinks(
				1, JSONUtil.put("collectionFieldId", "JournalArticle_title"),
				layout.fetchDraftLayout(), segmentsExperienceId));

		MockHttpServletResponse mockHttpServletResponse = _renderLayout(
			layout, _getMockHttpServletRequest(layout));

		String content = mockHttpServletResponse.getContentAsString();

		Assert.assertTrue(
			content.contains(
				"d-flex flex-row align-items-end justify-content-center"));
	}

	@Test
	public void testRenderCollectionStyledLayoutStructureItemForRepeatableFields()
		throws Exception {

		DDMStructureTestHelper ddmStructureTestHelper =
			new DDMStructureTestHelper(
				_portal.getClassNameId(JournalArticle.class), _group);

		DDMStructure ddmStructure = ddmStructureTestHelper.addStructure(
			_portal.getClassNameId(JournalArticle.class),
			RandomTestUtil.randomString(), RandomTestUtil.randomString(),
			_deserialize(_read("structure_with_repeatable_fieldset.json")),
			StorageType.DEFAULT.getValue(), DDMStructureConstants.TYPE_DEFAULT);

		JournalArticle journalArticle =
			JournalTestUtil.addArticleWithXMLContent(
				_group.getGroupId(),
				JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID,
				PortalUtil.getClassNameId(DDMStructure.class),
				ddmStructure.getStructureId(),
				_read("repeatable_fieldset_content.xml"),
				ddmStructure.getStructureKey(), null,
				LocaleUtil.getSiteDefault());

		LayoutPageTemplateEntry layoutPageTemplateEntry =
			_layoutPageTemplateEntryService.addLayoutPageTemplateEntry(
				null, _group.getGroupId(), 0, null,
				_portal.getClassNameId(JournalArticle.class.getName()),
				ddmStructure.getStructureId(), RandomTestUtil.randomString(), 0,
				WorkflowConstants.STATUS_DRAFT, _serviceContext);

		Layout layout = _layoutLocalService.getLayout(
			layoutPageTemplateEntry.getPlid());

		MockHttpServletRequest mockHttpServletRequest =
			_getMockHttpServletRequest(
				layout,
				_journalArticleLayoutDisplayPageProvider.
					getLayoutDisplayPageObjectProvider(journalArticle));

		mockHttpServletRequest.setAttribute(
			InfoDisplayWebKeys.INFO_ITEM, journalArticle);

		Layout draftLayout = layout.fetchDraftLayout();

		long segmentsExperienceId =
			_segmentsExperienceLocalService.fetchDefaultSegmentsExperienceId(
				draftLayout.getPlid());

		_addCollectionStyledLayoutStructureItem(
			JSONUtil.put(
				"fieldName", "Fieldset"
			).put(
				"itemSubType", String.valueOf(ddmStructure.getStructureId())
			).put(
				"itemType", JournalArticle.class.getName()
			).put(
				"key", RepeatableFieldInfoItemCollectionProvider.class.getName()
			).put(
				"type", InfoListProviderItemSelectorReturnType.class.getName()
			),
			JSONUtil.put(
				"displayAllPages", true
			).put(
				"numberOfItems", 3
			).put(
				"numberOfItemsPerPage", 3
			).put(
				"paginationType", "none"
			).put(
				"showAllItems", true
			),
			layout, null, segmentsExperienceId,
			_addFragmentEntryLinks(
				1, JSONUtil.put("collectionFieldId", "DDMStructure_Text1"),
				layout.fetchDraftLayout(), segmentsExperienceId));

		MockHttpServletResponse mockHttpServletResponse = _renderLayout(
			layout, mockHttpServletRequest);

		String content = mockHttpServletResponse.getContentAsString();

		Assert.assertTrue(content.contains("text1one"));
		Assert.assertTrue(content.contains("text1two"));
		Assert.assertTrue(content.contains("text1three"));
	}

	@Test
	public void testRenderCollectionStyledLayoutStructureItemSelectingSegmentsExperienceWithDifferentSegmentsEntry()
		throws Exception {

		AssetListEntry assetListEntry =
			_assetListEntryLocalService.addAssetListEntry(
				null, TestPropsValues.getUserId(), _group.getGroupId(),
				RandomTestUtil.randomString(),
				AssetListEntryTypeConstants.TYPE_MANUAL, _serviceContext);

		DDMStructure ddmStructure = DDMStructureTestUtil.addStructure(
			_group.getGroupId(), JournalArticle.class.getName());

		JournalArticle expectedJournalArticle1 = _addJournalArticle(
			ddmStructure);

		AssetEntry assetEntry1 = _assetEntryLocalService.fetchEntry(
			JournalArticle.class.getName(),
			expectedJournalArticle1.getResourcePrimKey());

		_assetListEntryLocalService.addAssetEntrySelections(
			assetListEntry.getAssetListEntryId(),
			new long[] {assetEntry1.getEntryId()},
			SegmentsEntryConstants.ID_DEFAULT, _serviceContext);

		JournalArticle expectedJournalArticle2 = _addJournalArticle(
			ddmStructure);

		AssetEntry assetEntry2 = _assetEntryLocalService.fetchEntry(
			JournalArticle.class.getName(),
			expectedJournalArticle2.getResourcePrimKey());

		SegmentsEntry segmentsEntry1 = _addSegmentsEntryByFirstName("Test");

		_assetListEntryLocalService.addAssetEntrySelections(
			assetListEntry.getAssetListEntryId(),
			new long[] {assetEntry2.getEntryId()},
			segmentsEntry1.getSegmentsEntryId(), _serviceContext);

		AssetListEntrySegmentsEntryRel assetListEntrySegmentsEntryRel1 =
			_assetListEntrySegmentsEntryRelLocalService.
				getAssetListEntrySegmentsEntryRel(
					assetListEntry.getAssetListEntryId(),
					SegmentsEntryConstants.ID_DEFAULT);

		AssetListEntrySegmentsEntryRel assetListEntrySegmentsEntryRel2 =
			_assetListEntrySegmentsEntryRelLocalService.
				getAssetListEntrySegmentsEntryRel(
					assetListEntry.getAssetListEntryId(),
					segmentsEntry1.getSegmentsEntryId());

		_assetListEntrySegmentsEntryRelLocalService.updateVariationsPriority(
			new long[] {
				assetListEntrySegmentsEntryRel2.
					getAssetListEntrySegmentsEntryRelId(),
				assetListEntrySegmentsEntryRel1.
					getAssetListEntrySegmentsEntryRelId()
			});

		SegmentsEntry segmentsEntry2 = _addSegmentsEntryByFirstName("User");

		Layout layout = LayoutTestUtil.addTypeContentLayout(_group);

		Layout draftLayout = layout.fetchDraftLayout();

		SegmentsExperience draftSegmentsExperience = _addSegmentsExperience(
			draftLayout, segmentsEntry2.getSegmentsEntryId());

		_addCollectionStyledLayoutStructureItem(
			assetListEntry.getAssetListEntryId(), layout,
			draftSegmentsExperience.getSegmentsExperienceId());

		MockHttpServletRequest mockHttpServletRequest =
			_getMockHttpServletRequest(layout);

		SegmentsExperience publishedSegmentsExperience =
			_segmentsExperienceLocalService.fetchSegmentsExperience(
				_group.getGroupId(),
				draftSegmentsExperience.getSegmentsExperienceKey(),
				layout.getPlid());

		mockHttpServletRequest.addParameter(
			"segmentsExperienceId",
			String.valueOf(
				publishedSegmentsExperience.getSegmentsExperienceId()));

		MockHttpServletResponse mockHttpServletResponse =
			new MockHttpServletResponse();

		RenderLayoutStructureTag renderLayoutStructureTag =
			_getRenderLayoutStructureTag(
				layout, mockHttpServletRequest, mockHttpServletResponse,
				publishedSegmentsExperience.getSegmentsExperienceId());

		renderLayoutStructureTag.doTag(
			mockHttpServletRequest, mockHttpServletResponse);

		List<JournalArticle> actualJournalArticles =
			(List<JournalArticle>)mockHttpServletRequest.getAttribute(
				"liferay-info:info-list-grid:infoListObjects");

		Assert.assertNotNull(actualJournalArticles);
		Assert.assertEquals(
			actualJournalArticles.toString(), 1, actualJournalArticles.size());

		JournalArticle actualJournalArticle1 = actualJournalArticles.get(0);

		Assert.assertEquals(
			expectedJournalArticle1.getArticleId(),
			actualJournalArticle1.getArticleId());
	}

	@Test
	public void testRenderCollectionStyledLayoutStructureItemSelectingSegmentsExperienceWithSameSegmentsEntry()
		throws Exception {

		AssetListEntry assetListEntry =
			_assetListEntryLocalService.addAssetListEntry(
				null, TestPropsValues.getUserId(), _group.getGroupId(),
				RandomTestUtil.randomString(),
				AssetListEntryTypeConstants.TYPE_MANUAL, _serviceContext);

		DDMStructure ddmStructure = DDMStructureTestUtil.addStructure(
			_group.getGroupId(), JournalArticle.class.getName());

		JournalArticle expectedJournalArticle1 = _addJournalArticle(
			ddmStructure);

		AssetEntry assetEntry1 = _assetEntryLocalService.fetchEntry(
			JournalArticle.class.getName(),
			expectedJournalArticle1.getResourcePrimKey());

		_assetListEntryLocalService.addAssetEntrySelections(
			assetListEntry.getAssetListEntryId(),
			new long[] {assetEntry1.getEntryId()},
			SegmentsEntryConstants.ID_DEFAULT, _serviceContext);

		JournalArticle expectedJournalArticle2 = _addJournalArticle(
			ddmStructure);

		AssetEntry assetEntry2 = _assetEntryLocalService.fetchEntry(
			JournalArticle.class.getName(),
			expectedJournalArticle2.getResourcePrimKey());

		SegmentsEntry segmentsEntry = _addSegmentsEntryByFirstName("Test");

		_assetListEntryLocalService.addAssetEntrySelections(
			assetListEntry.getAssetListEntryId(),
			new long[] {assetEntry2.getEntryId()},
			segmentsEntry.getSegmentsEntryId(), _serviceContext);

		Layout layout = LayoutTestUtil.addTypeContentLayout(_group);

		Layout draftLayout = layout.fetchDraftLayout();

		SegmentsExperience draftSegmentsExperience = _addSegmentsExperience(
			draftLayout, segmentsEntry.getSegmentsEntryId());

		_addCollectionStyledLayoutStructureItem(
			assetListEntry.getAssetListEntryId(), layout,
			draftSegmentsExperience.getSegmentsExperienceId());

		MockHttpServletRequest mockHttpServletRequest =
			_getMockHttpServletRequest(layout);

		SegmentsExperience publishedSegmentsExperience =
			_segmentsExperienceLocalService.fetchSegmentsExperience(
				_group.getGroupId(),
				draftSegmentsExperience.getSegmentsExperienceKey(),
				layout.getPlid());

		mockHttpServletRequest.addParameter(
			"segmentsExperienceId",
			String.valueOf(
				publishedSegmentsExperience.getSegmentsExperienceId()));

		MockHttpServletResponse mockHttpServletResponse =
			new MockHttpServletResponse();

		RenderLayoutStructureTag renderLayoutStructureTag =
			_getRenderLayoutStructureTag(
				layout, mockHttpServletRequest, mockHttpServletResponse,
				publishedSegmentsExperience.getSegmentsExperienceId());

		renderLayoutStructureTag.doTag(
			mockHttpServletRequest, mockHttpServletResponse);

		List<JournalArticle> actualJournalArticles =
			(List<JournalArticle>)mockHttpServletRequest.getAttribute(
				"liferay-info:info-list-grid:infoListObjects");

		Assert.assertNotNull(actualJournalArticles);
		Assert.assertEquals(
			actualJournalArticles.toString(), 1, actualJournalArticles.size());

		JournalArticle actualJournalArticle = actualJournalArticles.get(0);

		Assert.assertEquals(
			expectedJournalArticle2.getArticleId(),
			actualJournalArticle.getArticleId());
	}

	@Test
	@TestInfo("LPD-60936")
	public void testRenderCollectionStyledLayoutStructureItemWithAssetListEntryItemClassChanged()
		throws Exception {

		AssetListEntry assetListEntry =
			_assetListEntryLocalService.addAssetListEntry(
				null, TestPropsValues.getUserId(), _group.getGroupId(),
				RandomTestUtil.randomString(),
				AssetListEntryTypeConstants.TYPE_DYNAMIC,
				UnicodePropertiesBuilder.create(
					true
				).put(
					"anyAssetType",
					String.valueOf(_portal.getClassNameId(JournalArticle.class))
				).buildString(),
				_serviceContext);

		Layout layout = LayoutTestUtil.addTypeContentLayout(_group);

		Layout draftLayout = layout.fetchDraftLayout();

		long segmentsExperienceId =
			_segmentsExperienceLocalService.fetchDefaultSegmentsExperienceId(
				draftLayout.getPlid());

		FragmentEntry fragmentEntry = _addFragmentEntry();

		_addCollectionStyledLayoutStructureItem(
			assetListEntry, layout, 10, "none", segmentsExperienceId,
			_fragmentEntryLinkLocalService.addFragmentEntryLink(
				null, TestPropsValues.getUserId(), _group.getGroupId(), 0, 0,
				segmentsExperienceId, draftLayout.getPlid(),
				fragmentEntry.getCss(), fragmentEntry.getHtml(),
				fragmentEntry.getJs(), fragmentEntry.getConfiguration(),
				JSONUtil.put(
					FragmentEntryProcessorConstants.
						KEY_EDITABLE_FRAGMENT_ENTRY_PROCESSOR,
					JSONUtil.put(
						"element-text",
						JSONUtil.put(
							"collectionFieldId", "JournalArticle_title"
						).put(
							"defaultValue", "Heading Example"
						))
				).toString(),
				StringPool.BLANK, 0, fragmentEntry.getFragmentEntryKey(),
				fragmentEntry.getType(), _serviceContext));

		MockHttpServletRequest mockHttpServletRequest =
			_getMockHttpServletRequest(layout);

		String content = _getContent(
			layout, mockHttpServletRequest, segmentsExperienceId);

		Assert.assertFalse(content, content.contains(">Heading Example</h1>"));

		String title = RandomTestUtil.randomString();

		JournalTestUtil.addArticle(
			_group.getGroupId(),
			JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			JournalArticleConstants.CLASS_NAME_ID_DEFAULT, title,
			RandomTestUtil.randomString(), RandomTestUtil.randomString(),
			_portal.getSiteDefaultLocale(_group), true, true, _serviceContext);

		content = _getContent(
			layout, mockHttpServletRequest, segmentsExperienceId);

		Assert.assertFalse(content, content.contains(">Heading Example</h1>"));
		Assert.assertTrue(content, content.contains(">" + title + "</h1>"));

		_assetListEntryLocalService.updateAssetListEntryTypeSettings(
			assetListEntry.getAssetListEntryId(), 0,
			UnicodePropertiesBuilder.create(
				true
			).put(
				"anyAssetType", true
			).buildString());

		content = _getContent(
			layout, mockHttpServletRequest, segmentsExperienceId);

		Assert.assertFalse(content, content.contains(">" + title + "</h1>"));
		Assert.assertTrue(content, content.contains(">Heading Example</h1>"));

		_assetListEntryLocalService.updateAssetListEntryTypeSettings(
			assetListEntry.getAssetListEntryId(), 0,
			UnicodePropertiesBuilder.create(
				true
			).put(
				"anyAssetType",
				String.valueOf(
					_portal.getClassNameId(MessageBoardMessage.class))
			).buildString());

		content = _getContent(
			layout, mockHttpServletRequest, segmentsExperienceId);

		Assert.assertFalse(content, content.contains(">" + title + "</h1>"));
		Assert.assertFalse(content, content.contains(">Heading Example</h1>"));
		Assert.assertTrue(
			content,
			content.contains(
				_language.get(mockHttpServletRequest, "no-results-found")));
	}

	@Test
	@TestInfo("LPD-58700")
	public void testRenderCollectionStyledLayoutStructureItemWithCollectionProviderItemClassChanged()
		throws Exception {

		Bundle bundle = FrameworkUtil.getBundle(
			RenderLayoutStructureTagTest.class);

		BundleContext bundleContext = bundle.getBundleContext();

		ServiceRegistration<InfoCollectionProvider<?>> serviceRegistration =
			null;

		String key = RandomTestUtil.randomString();

		JournalArticleInfoCollectionProvider
			journalArticleInfoCollectionProvider =
				new JournalArticleInfoCollectionProvider(key);

		try {
			serviceRegistration = bundleContext.registerService(
				(Class<InfoCollectionProvider<?>>)
					(Class<?>)InfoCollectionProvider.class,
				journalArticleInfoCollectionProvider, null);

			Layout layout = LayoutTestUtil.addTypeContentLayout(_group);

			Layout draftLayout = layout.fetchDraftLayout();

			long segmentsExperienceId =
				_segmentsExperienceLocalService.
					fetchDefaultSegmentsExperienceId(draftLayout.getPlid());

			FragmentEntry fragmentEntry = _addFragmentEntry();

			_addCollectionStyledLayoutStructureItem(
				JSONUtil.put(
					"itemType", JournalArticle.class.getName()
				).put(
					"key", key
				).put(
					"type",
					InfoListProviderItemSelectorReturnType.class.getName()
				),
				JSONUtil.put(
					"displayAllPages", true
				).put(
					"paginationType", "none"
				).put(
					"showAllItems", true
				),
				layout, null, segmentsExperienceId,
				_fragmentEntryLinkLocalService.addFragmentEntryLink(
					null, TestPropsValues.getUserId(), _group.getGroupId(), 0,
					0, segmentsExperienceId, draftLayout.getPlid(),
					fragmentEntry.getCss(), fragmentEntry.getHtml(),
					fragmentEntry.getJs(), fragmentEntry.getConfiguration(),
					JSONUtil.put(
						FragmentEntryProcessorConstants.
							KEY_EDITABLE_FRAGMENT_ENTRY_PROCESSOR,
						JSONUtil.put(
							"element-text",
							JSONUtil.put(
								"collectionFieldId", "JournalArticle_title"
							).put(
								"defaultValue", "Heading Example"
							))
					).toString(),
					StringPool.BLANK, 0, fragmentEntry.getFragmentEntryKey(),
					fragmentEntry.getType(), _serviceContext));

			MockHttpServletRequest mockHttpServletRequest =
				_getMockHttpServletRequest(layout);

			String content = _getContent(
				layout, mockHttpServletRequest, segmentsExperienceId);

			Assert.assertFalse(
				content, content.contains(">Heading Example</h1>"));

			String title = RandomTestUtil.randomString();

			JournalArticle journalArticle = JournalTestUtil.addArticle(
				_group.getGroupId(),
				JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID,
				JournalArticleConstants.CLASS_NAME_ID_DEFAULT, title,
				RandomTestUtil.randomString(), RandomTestUtil.randomString(),
				_portal.getSiteDefaultLocale(_group), true, true,
				_serviceContext);

			journalArticleInfoCollectionProvider.addJournalArticle(
				journalArticle);

			content = _getContent(
				layout, mockHttpServletRequest, segmentsExperienceId);

			Assert.assertFalse(
				content, content.contains(">Heading Example</h1>"));
			Assert.assertTrue(content, content.contains(">" + title + "</h1>"));

			serviceRegistration.unregister();

			AssetEntryInfoCollectionProvider assetEntryInfoCollectionProvider =
				new AssetEntryInfoCollectionProvider(key);

			serviceRegistration = bundleContext.registerService(
				(Class<InfoCollectionProvider<?>>)
					(Class<?>)InfoCollectionProvider.class,
				assetEntryInfoCollectionProvider, null);

			content = _getContent(
				layout, mockHttpServletRequest, segmentsExperienceId);

			Assert.assertFalse(
				content, content.contains(">" + title + "</h1>"));
			Assert.assertFalse(
				content, content.contains(">Heading Example</h1>"));

			assetEntryInfoCollectionProvider.addAssetEntry(
				_assetEntryLocalService.getEntry(
					JournalArticle.class.getName(),
					journalArticle.getResourcePrimKey()));

			content = _getContent(
				layout, mockHttpServletRequest, segmentsExperienceId);

			Assert.assertFalse(
				content, content.contains(">" + title + "</h1>"));
			Assert.assertTrue(
				content, content.contains(">Heading Example</h1>"));
		}
		finally {
			if (serviceRegistration != null) {
				serviceRegistration.unregister();
			}
		}
	}

	@Test
	@TestInfo("LPD-61211")
	public void testRenderCollectionStyledLayoutStructureItemWithInfoListRenderer()
		throws Exception {

		ObjectDefinition objectDefinition =
			ObjectDefinitionTestUtil.publishObjectDefinition(
				ListUtil.fromArray(
					new TextObjectFieldBuilder(
					).labelMap(
						LocalizedMapUtil.getLocalizedMap(
							RandomTestUtil.randomString())
					).name(
						"myText"
					).objectFieldSettings(
						Collections.emptyList()
					).build()),
				ObjectDefinitionConstants.SCOPE_SITE);

		String myTextValue = RandomTestUtil.randomString();

		_objectEntryLocalService.addObjectEntry(
			_group.getGroupId(), TestPropsValues.getUserId(),
			objectDefinition.getObjectDefinitionId(),
			ObjectEntryFolderConstants.PARENT_OBJECT_ENTRY_FOLDER_ID_DEFAULT,
			null,
			HashMapBuilder.<String, Serializable>put(
				"myText", myTextValue
			).build(),
			ServiceContextTestUtil.getServiceContext());

		Layout layout = LayoutTestUtil.addTypeContentLayout(_group);

		List<InfoListRenderer<?>> infoListRenderers =
			_infoListRendererRegistry.getInfoListRenderers(
				objectDefinition.getClassName());

		InfoListRenderer<?> infoListRenderer = infoListRenderers.get(0);

		Layout draftLayout = layout.fetchDraftLayout();

		_addCollectionStyledLayoutStructureItem(
			JSONUtil.put(
				"itemSubtype", objectDefinition.getObjectDefinitionId()
			).put(
				"itemType", objectDefinition.getClassName()
			).put(
				"key", objectDefinition.getClassName()
			).put(
				"type", InfoListProviderItemSelectorReturnType.class.getName()
			),
			JSONUtil.put(
				"displayAllPages", true
			).put(
				"paginationType", "none"
			).put(
				"showAllItems", true
			),
			layout, infoListRenderer.getKey(),
			_segmentsExperienceLocalService.fetchDefaultSegmentsExperienceId(
				draftLayout.getPlid()));

		_testRenderLayoutWithLocale(layout, LocaleUtil.US, myTextValue);
	}

	@FeatureFlag("LPD-32050")
	@Test
	@TestInfo("LPD-48715")
	public void testRenderCollectionStyledLayoutStructureItemWithLocalizedObjectField()
		throws Exception {

		ObjectDefinition objectDefinition =
			ObjectDefinitionTestUtil.publishObjectDefinition(
				ListUtil.fromArray(
					new TextObjectFieldBuilder(
					).labelMap(
						LocalizedMapUtil.getLocalizedMap(
							RandomTestUtil.randomString())
					).name(
						"myLocalizedText"
					).localized(
						true
					).objectFieldSettings(
						Collections.emptyList()
					).build(),
					new TextObjectFieldBuilder(
					).labelMap(
						LocalizedMapUtil.getLocalizedMap(
							RandomTestUtil.randomString())
					).name(
						"myText"
					).objectFieldSettings(
						Collections.emptyList()
					).build()),
				ObjectDefinitionConstants.SCOPE_SITE);

		String myLocalizedTextENValue = RandomTestUtil.randomString();
		String myLocalizedTextESValue = RandomTestUtil.randomString();
		String myTextValue = RandomTestUtil.randomString();

		_objectEntryLocalService.addObjectEntry(
			_group.getGroupId(), TestPropsValues.getUserId(),
			objectDefinition.getObjectDefinitionId(),
			ObjectEntryFolderConstants.PARENT_OBJECT_ENTRY_FOLDER_ID_DEFAULT,
			null,
			HashMapBuilder.<String, Serializable>put(
				"myLocalizedText", myLocalizedTextENValue
			).put(
				"myLocalizedText_i18n",
				HashMapBuilder.put(
					LocaleUtil.toLanguageId(LocaleUtil.SPAIN),
					myLocalizedTextESValue
				).put(
					LocaleUtil.toLanguageId(LocaleUtil.US),
					myLocalizedTextENValue
				).build()
			).put(
				"myText", myTextValue
			).build(),
			ServiceContextTestUtil.getServiceContext());

		Layout layout = LayoutTestUtil.addTypeContentLayout(_group);

		Layout draftLayout = layout.fetchDraftLayout();

		long segmentsExperienceId =
			_segmentsExperienceLocalService.fetchDefaultSegmentsExperienceId(
				draftLayout.getPlid());

		_addCollectionStyledLayoutStructureItem(
			JSONUtil.put(
				"itemSubtype", objectDefinition.getObjectDefinitionId()
			).put(
				"itemType", objectDefinition.getClassName()
			).put(
				"key", objectDefinition.getClassName()
			).put(
				"type", InfoListProviderItemSelectorReturnType.class.getName()
			),
			JSONUtil.put(
				"displayAllPages", true
			).put(
				"paginationType", "none"
			).put(
				"showAllItems", true
			),
			layout, null, segmentsExperienceId,
			ArrayUtil.append(
				_addFragmentEntryLinks(
					1,
					JSONUtil.put(
						"collectionFieldId", "ObjectField_myLocalizedText"),
					draftLayout, segmentsExperienceId),
				_addFragmentEntryLinks(
					1, JSONUtil.put("collectionFieldId", "ObjectField_myText"),
					draftLayout, segmentsExperienceId)));

		_testRenderLayoutWithLocale(
			layout, LocaleUtil.CHINESE, "<h1", myLocalizedTextENValue, "</h1>",
			"<h1", myTextValue, "</h1>");
		_testRenderLayoutWithLocale(
			layout, LocaleUtil.SPAIN, "<h1", myLocalizedTextESValue, "</h1>",
			"<h1", myTextValue, "</h1>");
		_testRenderLayoutWithLocale(
			layout, LocaleUtil.US, "<h1", myLocalizedTextENValue, "</h1>",
			"<h1", myTextValue, "</h1>");
	}

	@Test
	@TestInfo("LPS-173440")
	public void testRenderCollectionStyledLayoutStructureItemWithoutPermissions()
		throws Exception {

		AssetListEntry assetListEntry =
			_assetListEntryLocalService.addAssetListEntry(
				null, TestPropsValues.getUserId(), _group.getGroupId(),
				RandomTestUtil.randomString(),
				AssetListEntryTypeConstants.TYPE_MANUAL, _serviceContext);

		Role guestRole = RoleLocalServiceUtil.getRole(
			TestPropsValues.getCompanyId(), RoleConstants.GUEST);

		ResourcePermissionLocalServiceUtil.removeResourcePermission(
			TestPropsValues.getCompanyId(), AssetListEntry.class.getName(),
			ResourceConstants.SCOPE_INDIVIDUAL,
			String.valueOf(assetListEntry.getAssetListEntryId()),
			guestRole.getRoleId(), ActionKeys.VIEW);

		Layout layout = LayoutTestUtil.addTypeContentLayout(_group);

		Layout draftLayout = layout.fetchDraftLayout();

		long segmentsExperienceId =
			_segmentsExperienceLocalService.fetchDefaultSegmentsExperienceId(
				draftLayout.getPlid());

		_addCollectionStyledLayoutStructureItem(
			assetListEntry, layout, _COUNT_INFO_LIST_ITEMS, "none",
			segmentsExperienceId,
			_addFragmentEntryLinks(
				_COUNT_FRAGMENT_ENTRY_LINKS,
				JSONUtil.put("collectionFieldId", "JournalArticle_title"),
				layout.fetchDraftLayout(), segmentsExperienceId));

		_addAssetEntries(assetListEntry);

		PermissionChecker permissionChecker =
			PermissionThreadLocal.getPermissionChecker();

		try {
			Company company = _companyLocalService.fetchCompany(
				TestPropsValues.getCompanyId());

			PermissionThreadLocal.setPermissionChecker(
				PermissionCheckerFactoryUtil.create(company.getGuestUser()));

			MockHttpServletRequest mockHttpServletRequest =
				_getMockHttpServletRequest(layout);
			MockHttpServletResponse mockHttpServletResponse =
				new MockHttpServletResponse();

			RenderLayoutStructureTag renderLayoutStructureTag =
				_getRenderLayoutStructureTag(
					layout, mockHttpServletRequest, mockHttpServletResponse,
					segmentsExperienceId);

			renderLayoutStructureTag.doTag(
				mockHttpServletRequest, mockHttpServletResponse);

			String content = mockHttpServletResponse.getContentAsString();

			Assert.assertTrue(content.isEmpty());
		}
		finally {
			PermissionThreadLocal.setPermissionChecker(permissionChecker);
		}
	}

	@Test
	public void testRenderCollectionStyledLayoutStructureItemWithoutSelectingSegmentsExperience()
		throws Exception {

		AssetListEntry assetListEntry =
			_assetListEntryLocalService.addAssetListEntry(
				null, TestPropsValues.getUserId(), _group.getGroupId(),
				RandomTestUtil.randomString(),
				AssetListEntryTypeConstants.TYPE_MANUAL, _serviceContext);

		DDMStructure ddmStructure = DDMStructureTestUtil.addStructure(
			_group.getGroupId(), JournalArticle.class.getName());

		JournalArticle expectedJournalArticle1 = _addJournalArticle(
			ddmStructure);

		AssetEntry assetEntry1 = _assetEntryLocalService.fetchEntry(
			JournalArticle.class.getName(),
			expectedJournalArticle1.getResourcePrimKey());

		_assetListEntryLocalService.addAssetEntrySelections(
			assetListEntry.getAssetListEntryId(),
			new long[] {assetEntry1.getEntryId()},
			SegmentsEntryConstants.ID_DEFAULT, _serviceContext);

		JournalArticle expectedJournalArticle2 = _addJournalArticle(
			ddmStructure);

		AssetEntry assetEntry2 = _assetEntryLocalService.fetchEntry(
			JournalArticle.class.getName(),
			expectedJournalArticle2.getResourcePrimKey());

		SegmentsEntry segmentsEntry = _addSegmentsEntryByFirstName("Test");

		_assetListEntryLocalService.addAssetEntrySelections(
			assetListEntry.getAssetListEntryId(),
			new long[] {assetEntry2.getEntryId()},
			segmentsEntry.getSegmentsEntryId(), _serviceContext);

		Layout layout = LayoutTestUtil.addTypeContentLayout(_group);

		Layout draftLayout = layout.fetchDraftLayout();

		_addCollectionStyledLayoutStructureItem(
			JSONUtil.put(
				"classNameId", _portal.getClassNameId(AssetListEntry.class)
			).put(
				"classPK", assetListEntry.getAssetListEntryId()
			).put(
				"itemType", JournalArticle.class.getName()
			).put(
				"type", InfoListItemSelectorReturnType.class.getName()
			),
			null, layout,
			"com.liferay.journal.web.internal.info.list.renderer." +
				"BulletedJournalArticleBasicInfoListRenderer",
			_segmentsExperienceLocalService.fetchDefaultSegmentsExperienceId(
				draftLayout.getPlid()));

		MockHttpServletRequest mockHttpServletRequest =
			_getMockHttpServletRequest(layout);

		_renderLayout(layout, mockHttpServletRequest);

		List<JournalArticle> actualJournalArticles =
			(List<JournalArticle>)mockHttpServletRequest.getAttribute(
				"liferay-info:info-list-grid:infoListObjects");

		Assert.assertNotNull(actualJournalArticles);
		Assert.assertEquals(
			actualJournalArticles.toString(), 1, actualJournalArticles.size());

		JournalArticle actualJournalArticle = actualJournalArticles.get(0);

		Assert.assertEquals(
			expectedJournalArticle1.getArticleId(),
			actualJournalArticle.getArticleId());
	}

	@Test
	@TestInfo({"LPS-123825", "LPS-149178"})
	public void testRenderCollectionStyledLayoutStructureItemWithPagination()
		throws Exception {

		AssetListEntry assetListEntry =
			_assetListEntryLocalService.addAssetListEntry(
				null, TestPropsValues.getUserId(), _group.getGroupId(),
				RandomTestUtil.randomString(),
				AssetListEntryTypeConstants.TYPE_DYNAMIC,
				UnicodePropertiesBuilder.create(
					true
				).put(
					"anyAssetType",
					String.valueOf(_portal.getClassNameId(JournalArticle.class))
				).buildString(),
				_serviceContext);

		Layout layout = LayoutTestUtil.addTypeContentLayout(_group);

		Layout draftLayout = layout.fetchDraftLayout();

		long segmentsExperienceId =
			_segmentsExperienceLocalService.fetchDefaultSegmentsExperienceId(
				draftLayout.getPlid());

		String itemId = _addCollectionStyledLayoutStructureItem(
			JSONUtil.put(
				"classNameId", _portal.getClassNameId(AssetListEntry.class)
			).put(
				"classPK", assetListEntry.getAssetListEntryId()
			).put(
				"itemType", JournalArticle.class.getName()
			).put(
				"type", InfoListItemSelectorReturnType.class.getName()
			),
			null, layout, null, segmentsExperienceId,
			_addFragmentEntryLinks(
				1, JSONUtil.put("collectionFieldId", "JournalArticle_title"),
				layout.fetchDraftLayout(), segmentsExperienceId));

		Locale locale = _portal.getSiteDefaultLocale(_group);

		List<String> titles = _addJournalArticlesAndGetTitles(7, locale);

		_testPagination(
			itemId,
			JSONUtil.put(
				"displayAllItems", false
			).put(
				"displayAllPages", true
			).put(
				"numberOfItemsPerPage", 2
			),
			layout, 2, 4, segmentsExperienceId, titles);

		_testPagination(
			itemId,
			JSONUtil.put(
				"displayAllPages", false
			).put(
				"numberOfItemsPerPage", 6
			).put(
				"numberOfPages", 1
			),
			layout, 6, 1, segmentsExperienceId, titles);

		_testPagination(
			itemId,
			JSONUtil.put(
				"displayAllPages", true
			).put(
				"numberOfItemsPerPage", 7
			).put(
				"numberOfPages", 1
			),
			layout, 7, 1, segmentsExperienceId, titles);

		_testPagination(
			itemId,
			JSONUtil.put(
				"displayAllItems", false
			).put(
				"displayAllPages", false
			).put(
				"numberOfItemsPerPage", 2
			).put(
				"numberOfPages", 2
			),
			layout, 2, 2, segmentsExperienceId, titles);
	}

	@Test
	public void testRenderContainerWithBackgroundImageAndCustomPathContext()
		throws Exception {

		String pathContext = "/de";

		PortalImpl portalImpl = new PortalImpl() {

			@Override
			public String getPathContext() {
				return pathContext;
			}

		};

		PortalUtil portalUtil = new PortalUtil();

		portalUtil.setPortal(portalImpl);

		try {
			Layout layout = LayoutTestUtil.addTypeContentLayout(_group);

			Layout draftLayout = layout.fetchDraftLayout();

			LayoutPageTemplateStructure layoutPageTemplateStructure =
				LayoutPageTemplateStructureLocalServiceUtil.
					fetchLayoutPageTemplateStructure(
						_group.getGroupId(), draftLayout.getPlid());

			LayoutStructure layoutStructure = LayoutStructure.of(
				layoutPageTemplateStructure.getDefaultSegmentsExperienceData());

			ContainerStyledLayoutStructureItem
				containerStyledLayoutStructureItem =
					(ContainerStyledLayoutStructureItem)
						layoutStructure.addContainerStyledLayoutStructureItem(
							layoutStructure.getMainItemId(), 0);

			FileEntry fileEntry = _addFileEntry();

			String url = _dlURLHelper.getPreviewURL(
				fileEntry, fileEntry.getFileVersion(), null, StringPool.BLANK,
				false, false);

			containerStyledLayoutStructureItem.updateItemConfig(
				JSONUtil.put(
					"styles",
					JSONUtil.put(
						"backgroundImage",
						JSONUtil.put(
							"classNameId",
							_portal.getClassNameId(FileEntry.class)
						).put(
							"classPK", fileEntry.getFileEntryId()
						).put(
							"url", url
						))));

			_layoutPageTemplateStructureLocalService.
				updateLayoutPageTemplateStructureData(
					TestPropsValues.getUserId(), _group.getGroupId(),
					draftLayout.getPlid(),
					_segmentsExperienceLocalService.
						fetchDefaultSegmentsExperienceId(draftLayout.getPlid()),
					layoutStructure.toString());

			ContentLayoutTestUtil.publishLayout(draftLayout, layout);

			String content = _getRenderLayoutHTML(layout);

			Assert.assertTrue(content.contains(url));
			Assert.assertFalse(content.contains(pathContext + url));
		}
		finally {
			portalUtil.setPortal(new PortalImpl());
		}
	}

	@Test
	@TestInfo("LPD-59838")
	public void testRenderContainerWithBackgroundImageWhereInfoItemObjectIsNull()
		throws Exception {

		Layout layout = LayoutTestUtil.addTypeContentLayout(_group);

		Layout draftLayout = layout.fetchDraftLayout();

		ContentLayoutTestUtil.addItemToLayout(
			JSONUtil.put(
				"styles",
				JSONUtil.put(
					"backgroundImage",
					JSONUtil.put(
						"className", FileEntry.class.getName()
					).put(
						"classNameId", _portal.getClassNameId(FileEntry.class)
					).put(
						"classPK", RandomTestUtil.nextLong()
					).put(
						"fieldId", "FileEntry_authorProfileImage"
					))
			).toString(),
			LayoutDataItemTypeConstants.TYPE_CONTAINER, draftLayout,
			_layoutStructureProvider,
			_segmentsExperienceLocalService.fetchDefaultSegmentsExperienceId(
				draftLayout.getPlid()));

		ContentLayoutTestUtil.publishLayout(layout.fetchDraftLayout(), layout);

		String content = _getRenderLayoutHTML(layout);

		Assert.assertFalse(content.contains("--lfr-background-image"));
		Assert.assertFalse(content.contains(": url("));
		Assert.assertFalse(content.contains("style="));
		Assert.assertTrue(content.contains("data-layout-structure-item-id="));
		Assert.assertTrue(
			content.contains("lfr-layout-structure-item-container"));
	}

	@Test
	@TestInfo("LPS-119817")
	public void testRenderContainerWithLinkToURL() throws Exception {
		String languageId = LocaleUtil.toLanguageId(
			_portal.getSiteDefaultLocale(_group));

		Layout layout = LayoutTestUtil.addTypeContentLayout(_group);

		Layout draftLayout = layout.fetchDraftLayout();

		long segmentsExperienceId =
			_segmentsExperienceLocalService.fetchDefaultSegmentsExperienceId(
				draftLayout.getPlid());

		JSONObject jsonObject = ContentLayoutTestUtil.addItemToLayout(
			JSONUtil.put(
				"link",
				JSONUtil.put(
					"href", JSONUtil.put(languageId, "https://www.liferay.com/")
				).put(
					"target", "_blank"
				)
			).toString(),
			LayoutDataItemTypeConstants.TYPE_CONTAINER,
			layout.fetchDraftLayout(), _layoutStructureProvider,
			segmentsExperienceId);

		String expectedContent = RandomTestUtil.randomString();

		_addFragmentEntryLinkToLayout(
			JSONUtil.put(languageId, expectedContent),
			layout.fetchDraftLayout(), jsonObject.getString("addedItemId"),
			segmentsExperienceId);

		ContentLayoutTestUtil.publishLayout(layout.fetchDraftLayout(), layout);

		String content = _getRenderLayoutHTML(layout);

		Assert.assertTrue(
			content,
			StringUtil.startsWith(
				content, "<a href=\"https://www.liferay.com/\""));
		Assert.assertTrue(content, content.contains("target=\"_blank\"><div"));
		Assert.assertTrue(
			content,
			content.contains(
				StringBundler.concat(
					"<h1 data-lfr-editable-id=\"element-text\" data-lfr-",
					"editable-type=\"text\">", expectedContent, "</h1>")));
		Assert.assertTrue(content, StringUtil.endsWith(content, "</div></a>"));
	}

	@Test
	public void testRenderEditionForm() throws Exception {
		MockObject mockObject = new MockObject(RandomTestUtil.randomLong());

		InfoField<TextInfoFieldType> infoField1 = _getInfoField(false);

		String infoField1Value = RandomTestUtil.randomString();

		mockObject.addInfoField(infoField1, infoField1Value);

		InfoField<TextInfoFieldType> infoField2 = _getInfoField(false);

		String infoField2Value = RandomTestUtil.randomString();

		mockObject.addInfoField(infoField2, infoField2Value);

		try (MockInfoServiceRegistrationHolder
				mockInfoServiceRegistrationHolder =
					new MockInfoServiceRegistrationHolder(
						InfoFieldSet.builder(
						).infoFieldSetEntries(
							ListUtil.fromArray(infoField1, infoField2)
						).build(),
						mockObject, _portal, _displayPageInfoItemCapability,
						_editPageInfoItemCapability)) {

			Layout layout = _addDisplayPageWithFormAndGetLayout(
				infoField1, infoField2);

			MockHttpServletResponse mockHttpServletResponse = _renderLayout(
				layout,
				_getMockHttpServletRequest(
					layout,
					mockInfoServiceRegistrationHolder.
						getMockObjectLayoutDisplayPageObjectProvider()));

			String content = mockHttpServletResponse.getContentAsString();

			Assert.assertFalse(
				content.contains("<fieldset disabled=\"disabled\">"));

			_assertInfoFieldInput(infoField1, content, infoField1Value);
			_assertInfoFieldInput(infoField2, content, infoField2Value);

			_assertInputJSONObject(content, infoField1, infoField2);
		}
	}

	@Test
	@TestInfo("LPS-169924")
	public void testRenderEditionFormWithAddPermissionAndWithoutViewPermission()
		throws Exception {

		MockObject mockObject = new MockObject(
			true, RandomTestUtil.randomLong(), false, false);

		InfoField<TextInfoFieldType> infoField1 = _getInfoField(false);

		String infoField1Value = RandomTestUtil.randomString();

		mockObject.addInfoField(infoField1, infoField1Value);

		InfoField<TextInfoFieldType> infoField2 = _getInfoField(false);

		String infoField2Value = RandomTestUtil.randomString();

		mockObject.addInfoField(infoField2, infoField2Value);

		try (MockInfoServiceRegistrationHolder
				mockInfoServiceRegistrationHolder =
					new MockInfoServiceRegistrationHolder(
						InfoFieldSet.builder(
						).infoFieldSetEntries(
							ListUtil.fromArray(infoField1, infoField2)
						).build(),
						mockObject, _portal, _displayPageInfoItemCapability,
						_editPageInfoItemCapability)) {

			Layout layout = _addDisplayPageWithFormAndGetLayout(
				infoField1, infoField2);

			MockHttpServletResponse mockHttpServletResponse = _renderLayout(
				layout,
				_getMockHttpServletRequest(
					layout,
					mockInfoServiceRegistrationHolder.
						getMockObjectLayoutDisplayPageObjectProvider()));

			String content = mockHttpServletResponse.getContentAsString();

			Assert.assertTrue(
				content.contains("<fieldset disabled=\"disabled\">"));

			_assertInfoFieldInput(infoField1, content, infoField1Value);
			_assertInfoFieldInput(infoField2, content, infoField2Value);

			_assertInputJSONObject(content, infoField1, infoField2);
		}
	}

	@Test
	@TestInfo("LPS-169924")
	public void testRenderEditionFormWithoutAddPermission() throws Exception {
		MockObject mockObject = new MockObject(
			false, RandomTestUtil.randomLong(), false, false);

		InfoField<TextInfoFieldType> infoField1 = _getInfoField(false);

		String infoField1Value = RandomTestUtil.randomString();

		mockObject.addInfoField(infoField1, infoField1Value);

		InfoField<TextInfoFieldType> infoField2 = _getInfoField(false);

		String infoField2Value = RandomTestUtil.randomString();

		mockObject.addInfoField(infoField2, infoField2Value);

		try (MockInfoServiceRegistrationHolder
				mockInfoServiceRegistrationHolder =
					new MockInfoServiceRegistrationHolder(
						InfoFieldSet.builder(
						).infoFieldSetEntries(
							ListUtil.fromArray(infoField1, infoField2)
						).build(),
						mockObject, _portal, _displayPageInfoItemCapability,
						_editPageInfoItemCapability)) {

			Layout layout = _addDisplayPageWithFormAndGetLayout(
				infoField1, infoField2);

			MockHttpServletResponse mockHttpServletResponse = _renderLayout(
				layout,
				_getMockHttpServletRequest(
					layout,
					mockInfoServiceRegistrationHolder.
						getMockObjectLayoutDisplayPageObjectProvider()));

			String content = mockHttpServletResponse.getContentAsString();

			Assert.assertTrue(content.isEmpty());
		}
	}

	@Test
	public void testRenderEditionFormWithoutUpdatePermission()
		throws Exception {

		MockObject mockObject = new MockObject(
			RandomTestUtil.randomLong(), false, true);

		InfoField<TextInfoFieldType> infoField1 = _getInfoField(false);

		String infoField1Value = RandomTestUtil.randomString();

		mockObject.addInfoField(infoField1, infoField1Value);

		InfoField<TextInfoFieldType> infoField2 = _getInfoField(false);

		String infoField2Value = RandomTestUtil.randomString();

		mockObject.addInfoField(infoField2, infoField2Value);

		try (MockInfoServiceRegistrationHolder
				mockInfoServiceRegistrationHolder =
					new MockInfoServiceRegistrationHolder(
						InfoFieldSet.builder(
						).infoFieldSetEntries(
							ListUtil.fromArray(infoField1, infoField2)
						).build(),
						mockObject, _portal, _displayPageInfoItemCapability,
						_editPageInfoItemCapability)) {

			Layout layout = _addDisplayPageWithFormAndGetLayout(
				infoField1, infoField2);

			MockHttpServletResponse mockHttpServletResponse = _renderLayout(
				layout,
				_getMockHttpServletRequest(
					layout,
					mockInfoServiceRegistrationHolder.
						getMockObjectLayoutDisplayPageObjectProvider()));

			String content = mockHttpServletResponse.getContentAsString();

			Assert.assertTrue(
				content.contains("<fieldset disabled=\"disabled\">"));

			_assertInfoFieldInput(infoField1, content, infoField1Value);
			_assertInfoFieldInput(infoField2, content, infoField2Value);

			_assertInputJSONObject(content, infoField1, infoField2);
		}
	}

	@Test
	public void testRenderFormWithInfoFormException() throws Exception {
		InfoField<TextInfoFieldType> infoField = _getInfoField(false);

		try (MockInfoServiceRegistrationHolder
				mockInfoServiceRegistrationHolder =
					new MockInfoServiceRegistrationHolder(
						InfoFieldSet.builder(
						).infoFieldSetEntries(
							ListUtil.fromArray(infoField)
						).build(),
						_portal, _editPageInfoItemCapability)) {

			Layout layout = LayoutTestUtil.addTypeContentLayout(_group);

			MockHttpServletRequest mockHttpServletRequest =
				_getMockHttpServletRequest(layout);

			String formItemId = ContentLayoutTestUtil.addFormToPublishedLayout(
				false,
				String.valueOf(
					_portal.getClassNameId(MockObject.class.getName())),
				"0", layout, _layoutStructureProvider, infoField);

			InfoFormException infoFormException = new InfoFormException();

			SessionErrors.add(
				mockHttpServletRequest, formItemId, infoFormException);

			MockHttpServletResponse mockHttpServletResponse = _renderLayout(
				layout, mockHttpServletRequest);

			Assert.assertFalse(
				SessionErrors.contains(mockHttpServletRequest, formItemId));

			String content = mockHttpServletResponse.getContentAsString();

			_assertErrorMessage(
				content,
				infoFormException.getLocalizedMessage(
					_portal.getSiteDefaultLocale(_group)));

			_assertInfoFieldInput(infoField, content);
		}
	}

	@Test
	public void testRenderFormWithInfoFormValidationException()
		throws Exception {

		InfoField<TextInfoFieldType> infoField = _getInfoField(false);

		try (MockInfoServiceRegistrationHolder
				mockInfoServiceRegistrationHolder =
					new MockInfoServiceRegistrationHolder(
						InfoFieldSet.builder(
						).infoFieldSetEntries(
							ListUtil.fromArray(infoField)
						).build(),
						_portal, _editPageInfoItemCapability)) {

			Layout layout = LayoutTestUtil.addTypeContentLayout(_group);

			MockHttpServletRequest mockHttpServletRequest =
				_getMockHttpServletRequest(layout);

			String formItemId = ContentLayoutTestUtil.addFormToPublishedLayout(
				false,
				String.valueOf(
					_portal.getClassNameId(MockObject.class.getName())),
				"0", layout, _layoutStructureProvider, infoField);

			InfoFormValidationException infoFormValidationException =
				new InfoFormValidationException(infoField.getUniqueId());

			SessionErrors.add(
				mockHttpServletRequest, formItemId,
				infoFormValidationException);
			SessionErrors.add(
				mockHttpServletRequest, InfoFormException.class,
				infoFormValidationException);

			MockHttpServletResponse mockHttpServletResponse = _renderLayout(
				layout, mockHttpServletRequest);

			Assert.assertFalse(
				SessionErrors.contains(mockHttpServletRequest, formItemId));
			Assert.assertFalse(
				SessionErrors.contains(
					mockHttpServletRequest, InfoFormException.class));

			String content = mockHttpServletResponse.getContentAsString();

			Locale locale = _portal.getSiteDefaultLocale(_group);

			_assertErrorMessage(
				content,
				infoFormValidationException.getLocalizedMessage(
					infoField.getLabel(locale), locale));

			_assertInfoFieldInput(infoField, content);
		}
	}

	@Test
	public void testRenderFormWithoutErrors() throws Exception {
		InfoField<TextInfoFieldType> infoField = _getInfoField(false);
		InfoField<TextInfoFieldType> readOnlyInfoField = _getInfoField(true);

		try (MockInfoServiceRegistrationHolder
				mockInfoServiceRegistrationHolder =
					new MockInfoServiceRegistrationHolder(
						InfoFieldSet.builder(
						).infoFieldSetEntries(
							ListUtil.fromArray(infoField, readOnlyInfoField)
						).build(),
						_portal, _editPageInfoItemCapability)) {

			Layout layout = LayoutTestUtil.addTypeContentLayout(_group);

			ContentLayoutTestUtil.addFormToPublishedLayout(
				false,
				String.valueOf(
					_portal.getClassNameId(MockObject.class.getName())),
				"0", layout, _layoutStructureProvider, infoField,
				readOnlyInfoField);

			String content = _getRenderLayoutHTML(layout);

			String errorHTML = "<div class=\"alert alert-danger\">";

			Assert.assertFalse(content.contains(errorHTML));

			_assertInfoFieldInput(infoField, content);
			_assertInfoFieldInput(readOnlyInfoField, content);

			_assertInputJSONObject(content, infoField, readOnlyInfoField);
		}
	}

	@Test
	@TestInfo("LPD-52923")
	public void testRenderFormWithSuccessMessage() throws Exception {
		InfoField<TextInfoFieldType> infoField = _getInfoField(false);

		try (MockInfoServiceRegistrationHolder
				mockInfoServiceRegistrationHolder =
					new MockInfoServiceRegistrationHolder(
						InfoFieldSet.builder(
						).infoFieldSetEntries(
							ListUtil.fromArray(infoField)
						).build(),
						_portal, _editPageInfoItemCapability)) {

			Layout layout = LayoutTestUtil.addTypeContentLayout(_group);

			String formItemId = ContentLayoutTestUtil.addFormToPublishedLayout(
				false,
				String.valueOf(
					_portal.getClassNameId(MockObject.class.getName())),
				"0", layout, _layoutStructureProvider, infoField);

			Locale locale = _portal.getSiteDefaultLocale(_group);

			_testRenderFormWithSuccessMessage(
				StringBundler.concat(
					"<div class=\"bg-white font-weight-semi-bold p-5 text-3 ",
					"text-center text-secondary\">",
					LanguageUtil.get(
						locale,
						"thank-you.-your-information-was-successfully-" +
							"received"),
					"</div>"),
				formItemId, infoField, layout);

			Layout draftLayout = layout.fetchDraftLayout();

			long segmentsExperienceId =
				_segmentsExperienceLocalService.
					fetchDefaultSegmentsExperienceId(draftLayout.getPlid());

			LayoutStructure layoutStructure =
				_layoutStructureProvider.getLayoutStructure(
					draftLayout.getPlid(), segmentsExperienceId);

			List<FormStyledLayoutStructureItem> formStyledLayoutStructureItems =
				layoutStructure.getFormStyledLayoutStructureItems();

			FormStyledLayoutStructureItem formStyledLayoutStructureItem =
				formStyledLayoutStructureItems.get(0);

			String message =
				"<script>alert('" + RandomTestUtil.randomString() +
					"')</script>";

			formStyledLayoutStructureItem.setSuccessMessageJSONObject(
				JSONUtil.put(
					"message",
					JSONUtil.put(LocaleUtil.toLanguageId(locale), message)
				).put(
					"type", "embedded"
				));

			_layoutPageTemplateStructureLocalService.
				updateLayoutPageTemplateStructureData(
					TestPropsValues.getUserId(), _group.getGroupId(),
					draftLayout.getPlid(), segmentsExperienceId,
					layoutStructure.toString());

			ContentLayoutTestUtil.publishLayout(draftLayout, layout);

			_testRenderFormWithSuccessMessage(
				HtmlUtil.escape(message), formItemId, infoField, layout);
		}
	}

	@Test
	@TestInfo("LPS-120094")
	public void testRenderFragmentEntryLinkWithImageLinkToURL()
		throws Exception {

		String languageId = LocaleUtil.toLanguageId(
			_portal.getSiteDefaultLocale(_group));

		FileEntry fileEntry = _addFileEntry();

		Layout layout = LayoutTestUtil.addTypeContentLayout(_group);

		Layout draftLayout = layout.fetchDraftLayout();

		_addFragmentEntryLinkToLayout(
			JSONUtil.put(
				"image-square",
				JSONUtil.put(
					languageId, RandomTestUtil.randomString()
				).put(
					languageId,
					JSONUtil.put(
						"classNameId", _portal.getClassNameId(FileEntry.class)
					).put(
						"classPK", fileEntry.getFileEntryId()
					).put(
						"fileEntryId", fileEntry.getFileEntryId()
					).put(
						"url",
						_dlURLHelper.getPreviewURL(
							fileEntry, fileEntry.getFileVersion(), null,
							StringPool.BLANK, false, false)
					)
				).put(
					"config",
					JSONUtil.put(
						"href",
						JSONUtil.put(languageId, "https://www.liferay.com/")
					).put(
						"mapperType", "link"
					)
				)),
			"BASIC_COMPONENT-image", layout.fetchDraftLayout(),
			_segmentsExperienceLocalService.fetchDefaultSegmentsExperienceId(
				draftLayout.getPlid()));

		ContentLayoutTestUtil.publishLayout(layout.fetchDraftLayout(), layout);

		String content = _getRenderLayoutHTML(layout);

		Assert.assertTrue(
			content,
			content.contains(
				StringBundler.concat(
					"<a href=\"https://www.liferay.com/\"><img alt=\"\" class=",
					"\"w-100\" data-lfr-editable-id=\"image-square\" data-lfr-",
					"editable-type=\"image\" src=\"",
					HtmlUtil.escape(
						_dlURLHelper.getPreviewURL(
							fileEntry, fileEntry.getFileVersion(), null,
							StringPool.BLANK)),
					"\" data-fileentryid=\"", fileEntry.getFileEntryId(),
					"\"></a>")));
	}

	@Test
	@TestInfo("LPD-61012")
	public void testRenderFragmentEntryLinkWithItemSelectorWithERCInfoItemIdentifier()
		throws Exception {

		MockObject mockObject = new MockObject(
			RandomTestUtil.randomLong(), false, true);

		InfoField<TextInfoFieldType> infoField = InfoField.builder(
		).infoFieldType(
			TextInfoFieldType.INSTANCE
		).namespace(
			RandomTestUtil.randomString()
		).name(
			"fieldName"
		).labelInfoLocalizedValue(
			InfoLocalizedValue.singleValue(RandomTestUtil.randomString())
		).localizable(
			true
		).build();

		String value = RandomTestUtil.randomString();

		mockObject.addInfoField(infoField, value);

		try (MockInfoServiceRegistrationHolder
				mockInfoServiceRegistrationHolder =
					new MockInfoServiceRegistrationHolder(
						InfoFieldSet.builder(
						).infoFieldSetEntries(
							ListUtil.fromArray(infoField)
						).build(),
						mockObject, _portal)) {

			Layout layout = LayoutTestUtil.addTypeContentLayout(_group);

			Layout draftLayout = layout.fetchDraftLayout();

			_addFragmentEntryLinkToLayout(
				JSONUtil.put(
					"element-text",
					JSONUtil.put(
						"classNameId", _portal.getClassNameId(MockObject.class)
					).put(
						"classPK", mockObject.getClassPK()
					).put(
						"externalReferenceCode", RandomTestUtil.randomString()
					).put(
						"fieldId", "fieldName"
					)),
				"BASIC_COMPONENT-heading", draftLayout,
				_segmentsExperienceLocalService.
					fetchDefaultSegmentsExperienceId(draftLayout.getPlid()));

			TemplateEntry templateEntry = TemplateTestUtil.addTemplateEntry(
				MockObject.class.getName(), "0", RandomTestUtil.randomString(),
				RandomTestUtil.randomString(),
				"<#if (fieldName.getData())??><p>${fieldName.getData()}</p>" +
					"</#if>",
				_serviceContext);

			ContentLayoutTestUtil.addFragmentEntryLinkToLayout(
				JSONUtil.put(
					FragmentEntryProcessorConstants.
						KEY_FREEMARKER_FRAGMENT_ENTRY_PROCESSOR,
					JSONUtil.put(
						"itemSelector",
						JSONUtil.put(
							"className", MockObject.class.getName()
						).put(
							"classNameId",
							_portal.getClassNameId(MockObject.class)
						).put(
							"classPK", mockObject.getClassPK()
						).put(
							"classTypeId", "0"
						).put(
							"externalReferenceCode",
							RandomTestUtil.randomString()
						).put(
							"template",
							JSONUtil.put(
								"infoItemRendererKey",
								"com.liferay.template.internal.info.item." +
									"renderer.TemplateInfoItemTemplatedRenderer"
							).put(
								"templateKey",
								String.valueOf(
									templateEntry.getTemplateEntryId())
							)
						))
				).toString(),
				_fragmentRendererRegistry.getFragmentRenderer(
					"com.liferay.fragment.internal.renderer." +
						"ContentObjectFragmentRenderer"),
				draftLayout, null, 0,
				_segmentsExperienceLocalService.
					fetchDefaultSegmentsExperienceId(draftLayout.getPlid()));

			ContentLayoutTestUtil.publishLayout(draftLayout, layout);

			String content = _getRenderLayoutHTML(layout);

			Assert.assertFalse(
				content, content.contains(">Heading Example</h1>"));
			Assert.assertTrue(content, content.contains(">" + value + "</h1>"));
			Assert.assertTrue(
				content, content.contains("<p>" + value + "</p>"));
		}
	}

	@Test
	@TestInfo("LPS-120348")
	public void testRenderFragmentEntryLinkWithLinkToURL() throws Exception {
		String languageId = LocaleUtil.toLanguageId(
			_portal.getSiteDefaultLocale(_group));

		String expectedContent = RandomTestUtil.randomString();

		Layout layout = LayoutTestUtil.addTypeContentLayout(_group);

		Layout draftLayout = layout.fetchDraftLayout();

		_addFragmentEntryLinkToLayout(
			JSONUtil.put(
				"element-text",
				JSONUtil.put(
					languageId, expectedContent
				).put(
					"config",
					JSONUtil.put(
						"href",
						JSONUtil.put(languageId, "https://www.liferay.com/")
					).put(
						"mapperType", "link"
					).put(
						"target", "_blank"
					)
				).put(
					"defaultValue", "Heading Example"
				)),
			"BASIC_COMPONENT-heading", layout.fetchDraftLayout(),
			_segmentsExperienceLocalService.fetchDefaultSegmentsExperienceId(
				draftLayout.getPlid()));

		ContentLayoutTestUtil.publishLayout(layout.fetchDraftLayout(), layout);

		String content = _getRenderLayoutHTML(layout);

		Assert.assertTrue(
			content,
			content.contains(
				StringBundler.concat(
					"data-lfr-editable-id=\"element-text\" data-lfr-editable-",
					"type=\"text\"><a rel=\"noopener noreferrer\" ",
					"target=\"_blank\" href=\"https://www.liferay.com/\">",
					expectedContent, "</a></h1></div>")));
	}

	@Test
	@TestInfo("LPD-41653")
	public void testViewAssertAnalyticsTargetableCollectionIdForCollectionStyledLayoutStructureItem()
		throws Exception {

		AssetListEntry assetListEntry =
			_assetListEntryLocalService.addAssetListEntry(
				null, TestPropsValues.getUserId(), _group.getGroupId(),
				RandomTestUtil.randomString(),
				AssetListEntryTypeConstants.TYPE_MANUAL, _serviceContext);

		Layout layout = LayoutTestUtil.addTypeContentLayout(_group);

		Layout draftLayout = layout.fetchDraftLayout();

		long segmentsExperienceId =
			_segmentsExperienceLocalService.fetchDefaultSegmentsExperienceId(
				draftLayout.getPlid());

		String itemId = _addCollectionStyledLayoutStructureItem(
			assetListEntry, layout, _COUNT_INFO_LIST_ITEMS, "none",
			segmentsExperienceId);

		String content = _getRenderLayoutHTML(layout);

		Assert.assertTrue(
			content.contains(
				"id=\"analytics-targetable-collection-" + itemId + "\""));
	}

	@Test
	@TestInfo("LPS-151738")
	public void testViewCommonStylesClassesGeneratedInOuterDivForFragmentEntryWithoutStylingAttribute()
		throws Exception {

		Layout layout = LayoutTestUtil.addTypeContentLayout(_group);

		Layout draftLayout = layout.fetchDraftLayout();

		ContentLayoutTestUtil.addFragmentEntryLinkToLayout(
			"{}", draftLayout,
			_segmentsExperienceLocalService.fetchDefaultSegmentsExperienceId(
				draftLayout.getPlid()));

		ContentLayoutTestUtil.publishLayout(draftLayout, layout);

		String content = _getRenderLayoutHTML(layout);

		Assert.assertTrue(
			content.startsWith("<div class=\"lfr-layout-structure-item-"));
	}

	private List<AssetEntry> _addAssetEntries(AssetListEntry assetListEntry)
		throws Exception {

		List<AssetEntry> assetEntries = new ArrayList<>();

		DDMStructure ddmStructure = DDMStructureTestUtil.addStructure(
			_group.getGroupId(), JournalArticle.class.getName());

		for (int i = 0; i < _COUNT_INFO_LIST_ITEMS; i++) {
			JournalArticle journalArticle = _addJournalArticle(ddmStructure);

			assetEntries.add(
				_assetEntryLocalService.fetchEntry(
					JournalArticle.class.getName(),
					journalArticle.getResourcePrimKey()));
		}

		_assetListEntryLocalService.addAssetEntrySelections(
			assetListEntry.getAssetListEntryId(),
			TransformUtil.transformToLongArray(
				assetEntries, assetEntry -> assetEntry.getEntryId()),
			SegmentsEntryConstants.ID_DEFAULT, _serviceContext);

		return assetEntries;
	}

	private String _addCollectionStyledLayoutStructureItem(
			AssetListEntry assetListEntry, Layout layout,
			int numberOfItemsPerPage, String paginationType,
			long segmentsExperienceId, FragmentEntryLink... fragmentEntryLinks)
		throws Exception {

		return _addCollectionStyledLayoutStructureItem(
			JSONUtil.put(
				"classNameId", _portal.getClassNameId(AssetListEntry.class)
			).put(
				"classPK", assetListEntry.getAssetListEntryId()
			).put(
				"itemType", JournalArticle.class.getName()
			).put(
				"type", InfoListItemSelectorReturnType.class.getName()
			),
			JSONUtil.put(
				"displayAllPages", true
			).put(
				"numberOfItemsPerPage", numberOfItemsPerPage
			).put(
				"paginationType", paginationType
			).put(
				"showAllItems", true
			),
			layout, null, segmentsExperienceId, fragmentEntryLinks);
	}

	private String _addCollectionStyledLayoutStructureItem(
			JSONObject collectionJSONObject, JSONObject displayConfigJSONObject,
			Layout layout, String listStyle, long segmentsExperienceId,
			FragmentEntryLink... fragmentEntryLinks)
		throws Exception {

		Layout draftLayout = layout.fetchDraftLayout();

		String itemId = ContentLayoutTestUtil.addCollectionDisplayToLayout(
			collectionJSONObject, draftLayout, _layoutStructureProvider,
			listStyle, null, 0, segmentsExperienceId, fragmentEntryLinks);

		if (displayConfigJSONObject != null) {
			_updateItemConfig(
				itemId, displayConfigJSONObject, draftLayout,
				segmentsExperienceId);
		}

		ContentLayoutTestUtil.publishLayout(draftLayout, layout);

		return itemId;
	}

	private void _addCollectionStyledLayoutStructureItem(
			long assetListEntryId, Layout layout, long segmentsExperienceId)
		throws Exception {

		_addCollectionStyledLayoutStructureItem(
			JSONUtil.put(
				"classNameId", _portal.getClassNameId(AssetListEntry.class)
			).put(
				"classPK", assetListEntryId
			).put(
				"itemType", JournalArticle.class.getName()
			).put(
				"type", InfoListItemSelectorReturnType.class.getName()
			),
			null, layout,
			"com.liferay.journal.web.internal.info.list.renderer." +
				"BulletedJournalArticleBasicInfoListRenderer",
			segmentsExperienceId);
	}

	private Layout _addDisplayPageWithFormAndGetLayout(InfoField... infoFields)
		throws Exception {

		LayoutPageTemplateEntry layoutPageTemplateEntry =
			_layoutPageTemplateEntryService.addLayoutPageTemplateEntry(
				null, _group.getGroupId(), 0, null,
				_portal.getClassNameId(MockObject.class.getName()), 0,
				RandomTestUtil.randomString(), 0,
				WorkflowConstants.STATUS_DRAFT, _serviceContext);

		Layout layout = _layoutLocalService.getLayout(
			layoutPageTemplateEntry.getPlid());

		ContentLayoutTestUtil.addFormToPublishedLayout(
			false,
			String.valueOf(_portal.getClassNameId(MockObject.class.getName())),
			"0", layout, _layoutStructureProvider, infoFields);

		return _layoutLocalService.getLayout(layout.getPlid());
	}

	private FileEntry _addFileEntry() throws Exception {
		return _dlAppLocalService.addFileEntry(
			null, TestPropsValues.getUserId(), _group.getGroupId(),
			DLFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			RandomTestUtil.randomString() + ".jpg", ContentTypes.IMAGE_JPEG,
			FileUtil.getBytes(
				RenderLayoutStructureTagTest.class, "dependencies/liferay.jpg"),
			null, null, null,
			ServiceContextTestUtil.getServiceContext(
				_group, TestPropsValues.getUserId()));
	}

	private Map<String, String> _addFormToLayout(
			String className, Layout layout)
		throws Exception {

		long segmentsExperienceId =
			_segmentsExperienceLocalService.fetchDefaultSegmentsExperienceId(
				layout.getPlid());

		JSONObject jsonObject = ContentLayoutTestUtil.addFormToLayout(
			false, String.valueOf(_portal.getClassNameId(className)), "0",
			layout, _layoutStructureProvider, segmentsExperienceId);

		String parentItemId = jsonObject.getString("addedItemId");

		int position = 0;

		Map<String, Long> map = new HashMap<>();

		for (InfoField<?> infoField : _getEditableInfoFields(className)) {
			InfoFieldType infoFieldType = infoField.getInfoFieldType();

			FragmentEntry fragmentEntry =
				_fragmentCollectionContributorRegistry.getFragmentEntry(
					_getInputFragmentEntryKey(infoFieldType.getName()));

			FragmentEntryLink fragmentEntryLink =
				ContentLayoutTestUtil.addFragmentEntryLinkToLayout(
					JSONUtil.put(
						FragmentEntryProcessorConstants.
							KEY_FREEMARKER_FRAGMENT_ENTRY_PROCESSOR,
						JSONUtil.put("inputFieldId", infoField.getUniqueId())
					).toString(),
					fragmentEntry.getCss(), fragmentEntry.getConfiguration(),
					fragmentEntry.getFragmentEntryId(), fragmentEntry.getHtml(),
					fragmentEntry.getJs(), layout,
					fragmentEntry.getFragmentEntryKey(),
					fragmentEntry.getType(), parentItemId, position,
					segmentsExperienceId);

			map.put(
				infoFieldType.getName(),
				fragmentEntryLink.getFragmentEntryLinkId());

			position++;
		}

		FragmentEntry fragmentEntry =
			_fragmentCollectionContributorRegistry.getFragmentEntry(
				_getInputFragmentEntryKey(
					DefaultInputFragmentEntryConfigurationProvider.
						FORM_INPUT_SUBMIT_BUTTON));

		FragmentEntryLink fragmentEntryLink =
			ContentLayoutTestUtil.addFragmentEntryLinkToLayout(
				"{}", fragmentEntry.getCss(), fragmentEntry.getConfiguration(),
				fragmentEntry.getFragmentEntryId(), fragmentEntry.getHtml(),
				fragmentEntry.getJs(), layout,
				fragmentEntry.getFragmentEntryKey(), fragmentEntry.getType(),
				parentItemId, position, segmentsExperienceId);

		map.put(
			DefaultInputFragmentEntryConfigurationProvider.
				FORM_INPUT_SUBMIT_BUTTON,
			fragmentEntryLink.getFragmentEntryLinkId());

		LayoutStructure layoutStructure =
			_layoutStructureProvider.getLayoutStructure(
				layout.getPlid(), segmentsExperienceId);

		Map<Long, LayoutStructureItem> fragmentLayoutStructureItems =
			layoutStructure.getFragmentLayoutStructureItems();

		Assert.assertEquals(
			MapUtil.toString(fragmentLayoutStructureItems), map.size(),
			fragmentLayoutStructureItems.size());

		Map<String, String> inputTypesMap = new HashMap<>();

		for (Map.Entry<String, Long> entry : map.entrySet()) {
			LayoutStructureItem layoutStructureItem =
				fragmentLayoutStructureItems.get(entry.getValue());

			inputTypesMap.put(entry.getKey(), layoutStructureItem.getItemId());
		}

		return inputTypesMap;
	}

	private FragmentEntry _addFragmentEntry() throws Exception {
		FragmentCollection fragmentCollection =
			_fragmentCollectionLocalService.addFragmentCollection(
				null, TestPropsValues.getUserId(), _group.getGroupId(),
				StringUtil.randomString(), StringPool.BLANK, _serviceContext);

		return _fragmentEntryLocalService.addFragmentEntry(
			null, TestPropsValues.getUserId(), _group.getGroupId(),
			fragmentCollection.getFragmentCollectionId(),
			RandomTestUtil.randomString(), RandomTestUtil.randomString(),
			StringPool.BLANK,
			"<h1 data-lfr-editable-id=\"element-text\" " +
				"data-lfr-editable-type=\"text\">Heading Example</h1>",
			StringPool.BLANK, false, StringPool.BLANK, null, 0, false, false,
			FragmentConstants.TYPE_COMPONENT, null,
			WorkflowConstants.STATUS_APPROVED, _serviceContext);
	}

	private FragmentEntryLink[] _addFragmentEntryLinks(
			int count, JSONObject jsonObject, Layout layout,
			long segmentsExperienceId)
		throws Exception {

		FragmentEntryLink[] fragmentEntryLinks = new FragmentEntryLink[count];

		FragmentEntry fragmentEntry = _addFragmentEntry();

		for (int i = 0; i < count; i++) {
			fragmentEntryLinks[i] =
				_fragmentEntryLinkLocalService.addFragmentEntryLink(
					null, TestPropsValues.getUserId(), _group.getGroupId(), 0,
					0, segmentsExperienceId, layout.getPlid(),
					fragmentEntry.getCss(), fragmentEntry.getHtml(),
					fragmentEntry.getJs(), fragmentEntry.getConfiguration(),
					JSONUtil.put(
						FragmentEntryProcessorConstants.
							KEY_EDITABLE_FRAGMENT_ENTRY_PROCESSOR,
						JSONUtil.put("element-text", jsonObject)
					).toString(),
					StringPool.BLANK, 0, fragmentEntry.getFragmentEntryKey(),
					fragmentEntry.getType(), _serviceContext);
		}

		return fragmentEntryLinks;
	}

	private FragmentEntryLink _addFragmentEntryLinkToLayout(
			JSONObject editableFragmentEntryProcessorJSONObject,
			FragmentEntry fragmentEntry, Layout layout, String parentItemId,
			int position, long segmentsExperienceId)
		throws Exception {

		return ContentLayoutTestUtil.addFragmentEntryLinkToLayout(
			JSONUtil.put(
				FragmentEntryProcessorConstants.
					KEY_EDITABLE_FRAGMENT_ENTRY_PROCESSOR,
				editableFragmentEntryProcessorJSONObject
			).toString(),
			fragmentEntry.getCss(), fragmentEntry.getConfiguration(),
			fragmentEntry.getFragmentEntryId(), fragmentEntry.getHtml(),
			fragmentEntry.getJs(), layout, fragmentEntry.getFragmentEntryKey(),
			fragmentEntry.getType(), parentItemId, position,
			segmentsExperienceId);
	}

	private FragmentEntryLink _addFragmentEntryLinkToLayout(
			JSONObject elemenTextJSONObject, Layout layout, String parentItemId,
			long segmentsExperienceId)
		throws Exception {

		FragmentEntry fragmentEntry = _addFragmentEntry();

		return _addFragmentEntryLinkToLayout(
			JSONUtil.put("element-text", elemenTextJSONObject), fragmentEntry,
			layout, parentItemId, 0, segmentsExperienceId);
	}

	private FragmentEntryLink _addFragmentEntryLinkToLayout(
			JSONObject editableFragmentEntryProcessorJSONObject,
			String fragmentEntryKey, Layout layout, long segmentsExperienceId)
		throws Exception {

		FragmentEntry fragmentEntry =
			_fragmentCollectionContributorRegistry.getFragmentEntry(
				fragmentEntryKey);

		return ContentLayoutTestUtil.addFragmentEntryLinkToLayout(
			JSONUtil.put(
				FragmentEntryProcessorConstants.
					KEY_EDITABLE_FRAGMENT_ENTRY_PROCESSOR,
				editableFragmentEntryProcessorJSONObject
			).toString(),
			fragmentEntry.getCss(), fragmentEntry.getConfiguration(),
			fragmentEntry.getFragmentEntryId(), fragmentEntry.getHtml(),
			fragmentEntry.getJs(), layout, fragmentEntry.getFragmentEntryKey(),
			fragmentEntry.getType(), null, 0, segmentsExperienceId);
	}

	private JournalArticle _addJournalArticle(DDMStructure ddmStructure)
		throws Exception {

		return _journalArticleLocalService.addArticle(
			null, TestPropsValues.getUserId(), _group.getGroupId(),
			JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			JournalArticleConstants.CLASS_NAME_ID_DEFAULT, 0, StringPool.BLANK,
			true, JournalArticleConstants.VERSION_DEFAULT,
			RandomTestUtil.randomLocaleStringMap(),
			RandomTestUtil.randomLocaleStringMap(),
			RandomTestUtil.randomLocaleStringMap(),
			DDMStructureTestUtil.getSampleStructuredContent(),
			ddmStructure.getStructureId(), StringPool.BLANK, null, 0, 0, 0, 0,
			0, 0, 0, 0, 0, 0, true, 0, 0, 0, 0, 0, true, true, false, 0, 0,
			null, null, null, null,
			ServiceContextTestUtil.getServiceContext(_group.getGroupId()));
	}

	private List<String> _addJournalArticlesAndGetTitles(
			int count, Locale locale)
		throws Exception {

		List<String> titles = new ArrayList<>();

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext();

		serviceContext.setCommand(Constants.ADD);
		serviceContext.setLayoutFullURL("http://localhost");

		for (int i = 0; i < count; i++) {
			serviceContext.setAssetPriority(i);

			JournalArticle journalArticle = JournalTestUtil.addArticle(
				_group.getGroupId(),
				JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID,
				JournalArticleConstants.CLASS_NAME_ID_DEFAULT,
				RandomTestUtil.randomString(), RandomTestUtil.randomString(),
				RandomTestUtil.randomString(), locale, true, true,
				serviceContext);

			titles.add(journalArticle.getTitle(locale));
		}

		return titles;
	}

	private void _addLayoutStructureRule(
			Map<String, String> inputTypesMap, Layout layout)
		throws Exception {

		long segmentsExperienceId =
			_segmentsExperienceLocalService.fetchDefaultSegmentsExperienceId(
				layout.getPlid());

		LayoutStructure layoutStructure =
			_layoutStructureProvider.getLayoutStructure(
				layout.getPlid(), segmentsExperienceId);

		LayoutStructureRule layoutStructureRule =
			layoutStructure.addLayoutStructureRule(
				RandomTestUtil.randomString());

		layoutStructureRule.setActionsJSONArray(
			JSONUtil.putAll(
				JSONUtil.put(
					"id", RandomTestUtil.randomString()
				).put(
					"itemId",
					inputTypesMap.get(TextInfoFieldType.INSTANCE.getName())
				).put(
					"type", "hide"
				),
				JSONUtil.put(
					"id", RandomTestUtil.randomString()
				).put(
					"itemId",
					inputTypesMap.get(
						DefaultInputFragmentEntryConfigurationProvider.
							FORM_INPUT_SUBMIT_BUTTON)
				).put(
					"type", "disable"
				)));
		layoutStructureRule.setConditionsJSONArray(
			JSONUtil.put(
				JSONUtil.put(
					"field", "user"
				).put(
					"id", RandomTestUtil.randomString()
				).put(
					"options",
					JSONUtil.put(
						"type", "equal"
					).put(
						"value", String.valueOf(TestPropsValues.getUserId())
					)
				).put(
					"type", "user"
				)));
		layoutStructureRule.setConditionType("all");

		_layoutPageTemplateStructureLocalService.
			updateLayoutPageTemplateStructureData(
				TestPropsValues.getUserId(), _group.getGroupId(),
				layout.getPlid(), segmentsExperienceId,
				layoutStructure.toString());
	}

	private SegmentsEntry _addSegmentsEntryByFirstName(String firstName)
		throws Exception {

		Criteria criteria = new Criteria();

		_segmentsCriteriaContributor.contribute(
			criteria, String.format("(firstName eq '%s')", firstName),
			Criteria.Conjunction.AND);

		return SegmentsTestUtil.addSegmentsEntry(
			_group.getGroupId(), CriteriaSerializer.serialize(criteria));
	}

	private SegmentsExperience _addSegmentsExperience(
			Layout layout, long segmentsEntryId)
		throws Exception {

		MVCActionCommand addSegmentsExperienceMVCActionCommand =
			ContentLayoutTestUtil.getMVCActionCommand(
				"/layout_content_page_editor/add_segments_experience");

		Company company = _companyLocalService.getCompany(
			_group.getCompanyId());

		MockLiferayPortletActionRequest mockLiferayPortletActionRequest =
			ContentLayoutTestUtil.getMockLiferayPortletActionRequest(
				company, _group, layout);

		HttpServletRequest httpServletRequest = new MockHttpServletRequest();

		httpServletRequest.setAttribute(
			WebKeys.THEME_DISPLAY,
			ContentLayoutTestUtil.getThemeDisplay(company, _group, layout));
		httpServletRequest.setAttribute(
			WebKeys.USER_ID, TestPropsValues.getUserId());

		mockLiferayPortletActionRequest.setAttribute(
			PortletServlet.PORTLET_SERVLET_REQUEST, httpServletRequest);

		mockLiferayPortletActionRequest.setParameter(
			"groupId", String.valueOf(layout.getGroupId()));
		mockLiferayPortletActionRequest.setParameter(
			"name", RandomTestUtil.randomString());
		mockLiferayPortletActionRequest.setParameter(
			"plid", String.valueOf(layout.getPlid()));
		mockLiferayPortletActionRequest.setParameter(
			"segmentsEntryId", String.valueOf(segmentsEntryId));

		JSONObject jsonObject = ReflectionTestUtil.invoke(
			addSegmentsExperienceMVCActionCommand, "doTransactionalCommand",
			new Class<?>[] {ActionRequest.class, ActionResponse.class},
			mockLiferayPortletActionRequest,
			new MockLiferayPortletActionResponse());

		JSONObject segmentsExperienceJSONObject = jsonObject.getJSONObject(
			"segmentsExperience");

		return _segmentsExperienceLocalService.getSegmentsExperience(
			segmentsExperienceJSONObject.getLong("segmentsExperienceId"));
	}

	private void _assertErrorMessage(
		String content, String expectedErrorMessage) {

		String expectedErrorHTML =
			"<div class=\"alert alert-danger\">" + expectedErrorMessage +
				"</div>";

		Assert.assertTrue(content.contains(expectedErrorHTML));
	}

	private void _assertInfoFieldInput(
		InfoField<TextInfoFieldType> infoField, String content) {

		String expectedInfoFieldInput =
			"<p>InputName:" + infoField.getName() + "</p>";

		Assert.assertTrue(content.contains(expectedInfoFieldInput));
	}

	private void _assertInfoFieldInput(
		InfoField<TextInfoFieldType> infoField, String content, String value) {

		_assertInfoFieldInput(infoField, content);

		Assert.assertTrue(content.contains(value));
	}

	private void _assertInfoFieldInputJSONObject(
			InfoField<TextInfoFieldType> infoField, String jsonString,
			Locale locale)
		throws Exception {

		JSONObject jsonObject = JSONFactoryUtil.createJSONObject(jsonString);

		Assert.assertEquals(
			infoField.getLabel(locale), jsonObject.getString("label"));
		Assert.assertEquals(
			infoField.isReadOnly(), jsonObject.getBoolean("readOnly"));
		Assert.assertEquals("text", jsonObject.getString("type"));
	}

	private void _assertInputJSONObject(String content, InfoField... infoFields)
		throws Exception {

		Matcher matcher = _inputJSONObjectPattern.matcher(content);
		Locale locale = _portal.getSiteDefaultLocale(_group);

		for (InfoField infoField : infoFields) {
			Assert.assertTrue(matcher.find());

			_assertInfoFieldInputJSONObject(
				infoField, matcher.group(1), locale);
		}
	}

	private void _assertNumericPagination(
		String html, int pageNumber, int numberOfPages) {

		Assert.assertEquals(
			html, numberOfPages + 2, StringUtil.count(html, "page-item"));

		String[] pageItems = html.split("page-item");

		Assert.assertEquals(
			pageItems.toString(), numberOfPages + 3, pageItems.length);

		Assert.assertTrue(
			pageItems[pageNumber],
			StringUtil.endsWith(pageItems[pageNumber], "\"active "));

		if (pageNumber != 1) {
			Assert.assertFalse(
				pageItems[0], StringUtil.endsWith(pageItems[0], "\"disabled "));
		}
		else {
			Assert.assertTrue(
				pageItems[0], StringUtil.endsWith(pageItems[0], "\"disabled "));
		}

		if (pageNumber != numberOfPages) {
			Assert.assertFalse(
				pageItems[pageNumber + 1],
				StringUtil.endsWith(pageItems[pageNumber + 1], "\"disabled "));
		}
		else {
			Assert.assertTrue(
				pageItems[pageNumber + 1],
				StringUtil.endsWith(pageItems[pageNumber + 1], "\"disabled "));
		}
	}

	private void _assertPagination(
			String itemId, Layout layout, int pageNumber,
			int numberOfItemsPerPage, int numberOfPages, String paginationType,
			List<String> strings)
		throws Exception {

		String html = _getRenderLayoutHTML(
			layout,
			HashMapBuilder.put(
				"page_number_" + itemId,
				() -> {
					if (pageNumber > 1) {
						return String.valueOf(pageNumber);
					}

					return null;
				}
			).build(),
			null);

		int endIndex = pageNumber * numberOfItemsPerPage;
		int startIndex =
			(pageNumber * numberOfItemsPerPage) - numberOfItemsPerPage;

		for (int i = 0; i < strings.size(); i++) {
			String string = strings.get(i);

			if ((i >= startIndex) && (i < endIndex)) {
				Assert.assertTrue(
					html + " does not contain " + string,
					html.contains(string));
			}
			else {
				Assert.assertFalse(
					html + " contains " + string, html.contains(string));
			}
		}

		if (paginationType.equals("numeric")) {
			_assertNumericPagination(html, pageNumber, numberOfPages);
		}
		else {
			_assertSimplePagination(html, itemId, pageNumber, numberOfPages);
		}
	}

	private void _assertSimplePagination(
		String html, String itemId, int pageNumber, int numberOfPages) {

		Assert.assertTrue(
			html, html.contains("paginationNextButton_" + itemId));
		Assert.assertTrue(
			html, html.contains("paginationPreviousButton_" + itemId));

		if (pageNumber != 1) {
			Assert.assertFalse(
				html,
				html.contains(
					"paginationPreviousButton_" + itemId +
						"\" disabled=\"disabled\""));
		}
		else {
			Assert.assertTrue(
				html,
				html.contains(
					"paginationPreviousButton_" + itemId +
						"\" disabled=\"disabled\""));
		}

		if (pageNumber != numberOfPages) {
			Assert.assertFalse(
				html,
				html.contains(
					"paginationNextButton_" + itemId +
						"\" disabled=\"disabled\""));
		}
		else {
			Assert.assertTrue(
				html,
				html.contains(
					"paginationNextButton_" + itemId +
						"\" disabled=\"disabled\""));
		}
	}

	private DDMForm _deserialize(String content) {
		DDMFormDeserializerDeserializeRequest.Builder builder =
			DDMFormDeserializerDeserializeRequest.Builder.newBuilder(content);

		DDMFormDeserializerDeserializeResponse
			ddmFormDeserializerDeserializeResponse =
				_jsonDDMFormDeserializer.deserialize(builder.build());

		return ddmFormDeserializerDeserializeResponse.getDDMForm();
	}

	private String _getContent(
			Layout layout, MockHttpServletRequest mockHttpServletRequest,
			long segmentsExperienceId)
		throws Exception {

		MockHttpServletResponse mockHttpServletResponse =
			new MockHttpServletResponse();

		RenderLayoutStructureTag renderLayoutStructureTag =
			_getRenderLayoutStructureTag(
				layout, mockHttpServletRequest, mockHttpServletResponse,
				segmentsExperienceId);

		try (LoggingTimer loggingTimer = new LoggingTimer()) {
			renderLayoutStructureTag.doTag(
				mockHttpServletRequest, mockHttpServletResponse);
		}

		return mockHttpServletResponse.getContentAsString();
	}

	private LayoutStructure _getDefaultMasterLayoutStructure() {
		LayoutStructure layoutStructure = new LayoutStructure();

		LayoutStructureItem rootLayoutStructureItem =
			layoutStructure.addRootLayoutStructureItem();

		layoutStructure.addDropZoneLayoutStructureItem(
			rootLayoutStructureItem.getItemId(), 0);

		return layoutStructure;
	}

	private List<InfoField<?>> _getEditableInfoFields(String className)
		throws Exception {

		InfoItemFormProvider<?> infoItemFormProvider =
			_infoItemServiceRegistry.getFirstInfoItemService(
				InfoItemFormProvider.class, className);

		InfoForm infoForm = infoItemFormProvider.getInfoForm(
			StringPool.BLANK, _group.getGroupId());

		return ListUtil.filter(
			infoForm.getAllInfoFields(),
			infoField ->
				infoField.isEditable() &&
				!StringUtil.equals(
					infoField.getName(), "externalReferenceCode"));
	}

	private InfoField<TextInfoFieldType> _getInfoField(boolean readOnly) {
		return InfoField.builder(
		).infoFieldType(
			TextInfoFieldType.INSTANCE
		).namespace(
			RandomTestUtil.randomString()
		).name(
			RandomTestUtil.randomString()
		).labelInfoLocalizedValue(
			InfoLocalizedValue.singleValue(RandomTestUtil.randomString())
		).localizable(
			true
		).readOnly(
			readOnly
		).build();
	}

	private String _getInputFragmentEntryKey(String infoFieldTypeName) {
		if (Objects.equals(
				infoFieldTypeName, BooleanInfoFieldType.INSTANCE.getName())) {

			return "INPUTS-checkbox";
		}

		if (Objects.equals(
				infoFieldTypeName,
				DefaultInputFragmentEntryConfigurationProvider.
					FORM_INPUT_SUBMIT_BUTTON)) {

			return "INPUTS-submit-button";
		}

		if (Objects.equals(
				infoFieldTypeName, TextInfoFieldType.INSTANCE.getName())) {

			return "INPUTS-text-input";
		}

		return null;
	}

	private MockHttpServletRequest _getMockHttpServletRequest(Layout layout)
		throws Exception {

		return _getMockHttpServletRequest(
			layout, null, Collections.emptyMap(), null);
	}

	private MockHttpServletRequest _getMockHttpServletRequest(
			Layout layout,
			LayoutDisplayPageObjectProvider layoutDisplayPageObjectProvider)
		throws Exception {

		return _getMockHttpServletRequest(
			layout, layoutDisplayPageObjectProvider, Collections.emptyMap(),
			null);
	}

	private MockHttpServletRequest _getMockHttpServletRequest(
			Layout layout,
			LayoutDisplayPageObjectProvider layoutDisplayPageObjectProvider,
			Map<String, String> map, User user)
		throws Exception {

		MockHttpServletRequest mockHttpServletRequest =
			ContentLayoutTestUtil.getMockHttpServletRequest(
				_companyLocalService.getCompany(layout.getCompanyId()), _group,
				layout);

		mockHttpServletRequest.setAttribute(
			LayoutDisplayPageWebKeys.LAYOUT_DISPLAY_PAGE_OBJECT_PROVIDER,
			layoutDisplayPageObjectProvider);
		mockHttpServletRequest.setAttribute(
			"ORIGINAL_HTTP_SERVLET_REQUEST", mockHttpServletRequest);
		mockHttpServletRequest.setMethod(HttpMethods.GET);

		for (Map.Entry<String, String> entry : map.entrySet()) {
			mockHttpServletRequest.setParameter(
				entry.getKey(), entry.getValue());
		}

		ThemeDisplay themeDisplay =
			(ThemeDisplay)mockHttpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		if (user != null) {
			themeDisplay.setPermissionChecker(
				PermissionCheckerFactoryUtil.create(user));
			themeDisplay.setUser(user);
		}

		themeDisplay.setRequest(mockHttpServletRequest);

		return mockHttpServletRequest;
	}

	private String _getRenderLayoutHTML(Layout layout) throws Exception {
		return _getRenderLayoutHTML(layout, Collections.emptyMap(), null);
	}

	private String _getRenderLayoutHTML(
			Layout layout, Map<String, String> map, User user)
		throws Exception {

		MockHttpServletResponse mockHttpServletResponse = _renderLayout(
			layout, _getMockHttpServletRequest(layout, null, map, user));

		return mockHttpServletResponse.getContentAsString();
	}

	private RenderLayoutStructureTag _getRenderLayoutStructureTag(
		Layout layout, MockHttpServletRequest mockHttpServletRequest,
		MockHttpServletResponse mockHttpServletResponse,
		long selectedSegmentsExperienceId) {

		RenderLayoutStructureTag renderLayoutStructureTag =
			new RenderLayoutStructureTag();

		renderLayoutStructureTag.setLayoutStructure(
			_layoutStructureProvider.getLayoutStructure(
				layout.getPlid(), selectedSegmentsExperienceId));
		renderLayoutStructureTag.setPageContext(
			new MockPageContext(
				null, mockHttpServletRequest, mockHttpServletResponse));

		return renderLayoutStructureTag;
	}

	private RenderLayoutStructureTag
		_getRenderLayoutStructureTagDefaultSegmentsExperience(
			Layout layout, MockHttpServletRequest mockHttpServletRequest,
			MockHttpServletResponse mockHttpServletResponse) {

		return _getRenderLayoutStructureTag(
			layout, mockHttpServletRequest, mockHttpServletResponse,
			_segmentsExperienceLocalService.fetchDefaultSegmentsExperienceId(
				layout.getPlid()));
	}

	private String _read(String fileName) throws Exception {
		Class<?> clazz = getClass();

		return StringUtil.read(
			clazz.getResourceAsStream("dependencies/" + fileName));
	}

	private MockHttpServletResponse _renderLayout(
			Layout layout, MockHttpServletRequest mockHttpServletRequest)
		throws Exception {

		MockHttpServletResponse mockHttpServletResponse =
			new MockHttpServletResponse();

		RenderLayoutStructureTag renderLayoutStructureTag =
			_getRenderLayoutStructureTagDefaultSegmentsExperience(
				layout, mockHttpServletRequest, mockHttpServletResponse);

		renderLayoutStructureTag.doTag(
			mockHttpServletRequest, mockHttpServletResponse);

		return mockHttpServletResponse;
	}

	private void _testPagination(
			String itemId, JSONObject jsonObject, Layout layout,
			int numberOfItemsPerPage, int numberOfPages,
			long segmentsExperienceId, List<String> strings)
		throws Exception {

		jsonObject.put("paginationType", "numeric");

		_updateItemConfig(
			itemId, jsonObject, layout.fetchDraftLayout(),
			segmentsExperienceId);

		ContentLayoutTestUtil.publishLayout(layout.fetchDraftLayout(), layout);

		for (int i = 1; i <= numberOfPages; i++) {
			_assertPagination(
				itemId, layout, i, numberOfItemsPerPage, numberOfPages,
				"numeric", strings);
		}

		jsonObject.put("paginationType", "simple");

		_updateItemConfig(
			itemId, jsonObject, layout.fetchDraftLayout(),
			segmentsExperienceId);

		ContentLayoutTestUtil.publishLayout(layout.fetchDraftLayout(), layout);

		for (int i = 1; i <= numberOfPages; i++) {
			_assertPagination(
				itemId, layout, i, numberOfItemsPerPage, numberOfPages,
				"simple", strings);
		}
	}

	private void _testRenderFormWithSuccessMessage(
			String expectedSuccessHTML, String formItemId,
			InfoField<TextInfoFieldType> infoField, Layout layout)
		throws Exception {

		MockHttpServletRequest mockHttpServletRequest =
			_getMockHttpServletRequest(layout);

		SessionMessages.add(mockHttpServletRequest, formItemId);

		MockHttpServletResponse mockHttpServletResponse = _renderLayout(
			layout, mockHttpServletRequest);

		String content = mockHttpServletResponse.getContentAsString();

		Assert.assertFalse(content.contains("<form action=\""));
		Assert.assertFalse(
			content.contains("<p>InputName:" + infoField.getName() + "</p>"));
		Assert.assertTrue(content, content.contains(expectedSuccessHTML));
	}

	private void _testRenderLayoutWithLocale(
			Layout layout, Locale locale, String... strings)
		throws Exception {

		MockHttpServletRequest mockHttpServletRequest =
			ContentLayoutTestUtil.getMockHttpServletRequest(
				_companyLocalService.getCompany(layout.getCompanyId()), _group,
				layout);

		mockHttpServletRequest.setAttribute(
			"ORIGINAL_HTTP_SERVLET_REQUEST", mockHttpServletRequest);
		mockHttpServletRequest.setMethod(HttpMethods.GET);

		ThemeDisplay themeDisplay =
			(ThemeDisplay)mockHttpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		themeDisplay.setLanguageId(LocaleUtil.toLanguageId(locale));
		themeDisplay.setLocale(locale);

		themeDisplay.setRequest(mockHttpServletRequest);

		MockHttpServletResponse mockHttpServletResponse = _renderLayout(
			layout, mockHttpServletRequest);

		String content = mockHttpServletResponse.getContentAsString();

		for (String string : strings) {
			int index = content.indexOf(string);

			Assert.assertTrue(index >= 0);

			content = content.substring(index);
		}
	}

	private void _updateItemConfig(
			String itemId, JSONObject jsonObject, Layout layout,
			long segmentsExperienceId)
		throws Exception {

		LayoutStructure layoutStructure =
			_layoutStructureProvider.getLayoutStructure(
				layout.getPlid(), segmentsExperienceId);

		LayoutStructureItem layoutStructureItem =
			layoutStructure.getLayoutStructureItem(itemId);

		JSONObject itemConfigJSONObject =
			layoutStructureItem.getItemConfigJSONObject();

		for (String key : jsonObject.keySet()) {
			itemConfigJSONObject.put(key, jsonObject.get(key));
		}

		layoutStructureItem.updateItemConfig(itemConfigJSONObject);

		_layoutPageTemplateStructureLocalService.
			updateLayoutPageTemplateStructureData(
				TestPropsValues.getUserId(), _group.getGroupId(),
				layout.getPlid(), segmentsExperienceId,
				layoutStructure.toString());
	}

	private static final int _COUNT_FRAGMENT_ENTRY_LINKS = 5;

	private static final int _COUNT_INFO_LIST_ITEMS = 5;

	private static final Pattern _inputJSONObjectPattern = Pattern.compile(
		"<p>InputJSONObject:(.*?)<\\/p>");

	@Inject(filter = "ddm.form.deserializer.type=json")
	private static DDMFormDeserializer _jsonDDMFormDeserializer;

	@Inject
	private AssetEntryLocalService _assetEntryLocalService;

	@Inject
	private AssetListEntryLocalService _assetListEntryLocalService;

	@Inject
	private AssetListEntrySegmentsEntryRelLocalService
		_assetListEntrySegmentsEntryRelLocalService;

	@Inject
	private CompanyLocalService _companyLocalService;

	@Inject(
		filter = "info.item.capability.key=" + DisplayPageInfoItemCapability.KEY
	)
	private InfoItemCapability _displayPageInfoItemCapability;

	@Inject
	private DLAppLocalService _dlAppLocalService;

	@Inject
	private DLURLHelper _dlURLHelper;

	@Inject(
		filter = "info.item.capability.key=" + EditPageInfoItemCapability.KEY
	)
	private InfoItemCapability _editPageInfoItemCapability;

	@Inject
	private EntityCache _entityCache;

	@Inject
	private FormManager _formManager;

	@Inject
	private FragmentCollectionContributorRegistry
		_fragmentCollectionContributorRegistry;

	@Inject
	private FragmentCollectionLocalService _fragmentCollectionLocalService;

	@Inject
	private FragmentEntryLinkListenerRegistry
		_fragmentEntryLinkListenerRegistry;

	@Inject
	private FragmentEntryLinkLocalService _fragmentEntryLinkLocalService;

	@Inject
	private FragmentEntryLocalService _fragmentEntryLocalService;

	@Inject
	private FragmentRendererRegistry _fragmentRendererRegistry;

	@DeleteAfterTestRun
	private Group _group;

	@Inject
	private GroupLocalService _groupLocalService;

	@Inject
	private InfoItemServiceRegistry _infoItemServiceRegistry;

	@Inject
	private InfoListRendererRegistry _infoListRendererRegistry;

	@Inject(
		filter = "component.name=com.liferay.journal.web.internal.layout.display.page.JournalArticleLayoutDisplayPageProvider"
	)
	private LayoutDisplayPageProvider<JournalArticle>
		_journalArticleLayoutDisplayPageProvider;

	@Inject
	private JournalArticleLocalService _journalArticleLocalService;

	@Inject
	private JSONFactory _jsonFactory;

	@Inject
	private Language _language;

	@Inject
	private LayoutDisplayPageProviderRegistry
		_layoutDisplayPageProviderRegistry;

	@Inject
	private LayoutLocalService _layoutLocalService;

	@Inject
	private LayoutPageTemplateEntryService _layoutPageTemplateEntryService;

	@Inject
	private LayoutPageTemplateStructureLocalService
		_layoutPageTemplateStructureLocalService;

	@Inject
	private LayoutStructureProvider _layoutStructureProvider;

	@Inject
	private MultiVMPool _multiVMPool;

	@Inject
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	@Inject
	private ObjectEntryLocalService _objectEntryLocalService;

	@Inject
	private ObjectRelationshipLocalService _objectRelationshipLocalService;

	private String _originalName;

	@Inject
	private Portal _portal;

	@Inject(
		filter = "segments.criteria.contributor.key=user",
		type = SegmentsCriteriaContributor.class
	)
	private SegmentsCriteriaContributor _segmentsCriteriaContributor;

	@Inject
	private SegmentsExperienceLocalService _segmentsExperienceLocalService;

	private ServiceContext _serviceContext;

	private static class AssetEntryInfoCollectionProvider
		implements InfoCollectionProvider<AssetEntry> {

		public void addAssetEntry(AssetEntry assetEntry) {
			_assetEntries.add(assetEntry);
		}

		@Override
		public InfoPage<AssetEntry> getCollectionInfoPage(
			CollectionQuery collectionQuery) {

			return InfoPage.of(_assetEntries);
		}

		@Override
		public String getKey() {
			return _key;
		}

		@Override
		public String getLabel(Locale locale) {
			return RandomTestUtil.randomString();
		}

		private AssetEntryInfoCollectionProvider(String key) {
			_key = key;
		}

		private List<AssetEntry> _assetEntries = new ArrayList<>();
		private final String _key;

	}

	private static class JournalArticleInfoCollectionProvider
		implements InfoCollectionProvider<JournalArticle> {

		public void addJournalArticle(JournalArticle journalArticle) {
			_journalArticles.add(journalArticle);
		}

		@Override
		public InfoPage<JournalArticle> getCollectionInfoPage(
			CollectionQuery collectionQuery) {

			return InfoPage.of(_journalArticles);
		}

		@Override
		public String getKey() {
			return _key;
		}

		@Override
		public String getLabel(Locale locale) {
			return RandomTestUtil.randomString();
		}

		private JournalArticleInfoCollectionProvider(String key) {
			_key = key;
		}

		private List<JournalArticle> _journalArticles = new ArrayList<>();
		private final String _key;

	}

}