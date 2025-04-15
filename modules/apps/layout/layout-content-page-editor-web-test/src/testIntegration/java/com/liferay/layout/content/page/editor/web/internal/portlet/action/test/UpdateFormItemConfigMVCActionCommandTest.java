/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.content.page.editor.web.internal.portlet.action.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.fragment.contributor.FragmentCollectionContributorRegistry;
import com.liferay.fragment.entry.processor.constants.FragmentEntryProcessorConstants;
import com.liferay.fragment.model.FragmentEntry;
import com.liferay.fragment.model.FragmentEntryLink;
import com.liferay.fragment.service.FragmentEntryLinkLocalService;
import com.liferay.fragment.util.configuration.FragmentConfigurationField;
import com.liferay.fragment.util.configuration.FragmentEntryConfigurationParser;
import com.liferay.info.field.InfoField;
import com.liferay.info.field.InfoFieldSet;
import com.liferay.info.field.type.BooleanInfoFieldType;
import com.liferay.info.field.type.DateInfoFieldType;
import com.liferay.info.field.type.FileInfoFieldType;
import com.liferay.info.field.type.InfoFieldType;
import com.liferay.info.field.type.LongTextInfoFieldType;
import com.liferay.info.field.type.MultiselectInfoFieldType;
import com.liferay.info.field.type.NumberInfoFieldType;
import com.liferay.info.field.type.RelationshipInfoFieldType;
import com.liferay.info.field.type.SelectInfoFieldType;
import com.liferay.info.field.type.TextInfoFieldType;
import com.liferay.info.item.capability.InfoItemCapability;
import com.liferay.info.localized.InfoLocalizedValue;
import com.liferay.info.test.util.MockInfoServiceRegistrationHolder;
import com.liferay.info.test.util.model.MockObject;
import com.liferay.layout.page.template.info.item.capability.EditPageInfoItemCapability;
import com.liferay.layout.page.template.model.LayoutPageTemplateStructure;
import com.liferay.layout.page.template.service.LayoutPageTemplateStructureLocalService;
import com.liferay.layout.provider.LayoutStructureProvider;
import com.liferay.layout.test.util.ContentLayoutTestUtil;
import com.liferay.layout.test.util.LayoutTestUtil;
import com.liferay.layout.util.constants.LayoutDataItemTypeConstants;
import com.liferay.layout.util.structure.ContainerStyledLayoutStructureItem;
import com.liferay.layout.util.structure.FormStyledLayoutStructureItem;
import com.liferay.layout.util.structure.FragmentStyledLayoutStructureItem;
import com.liferay.layout.util.structure.LayoutStructure;
import com.liferay.layout.util.structure.LayoutStructureItem;
import com.liferay.layout.util.structure.LayoutStructureItemUtil;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.module.util.SystemBundleUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.servlet.PortletServlet;
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
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.segments.service.SegmentsExperienceLocalService;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.TreeSet;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.service.component.runtime.ServiceComponentRuntime;
import org.osgi.service.component.runtime.dto.ComponentDescriptionDTO;
import org.osgi.util.promise.Promise;

import org.springframework.mock.web.MockHttpServletRequest;

/**
 * @author Lourdes Fernández Besada
 */
@RunWith(Arquillian.class)
public class UpdateFormItemConfigMVCActionCommandTest {

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

		_draftLayout = layout.fetchDraftLayout();

		_segmentsExperienceId =
			_segmentsExperienceLocalService.fetchDefaultSegmentsExperienceId(
				_draftLayout.getPlid());

		_prepareServiceContext();
	}

	@After
	public void tearDown() {
		ServiceContextThreadLocal.popServiceContext();
	}

	@Test
	@TestInfo("LPD-31772")
	public void testUpdateFormItemConfigMVCActionCommandDecreasingNumberOfSteps()
		throws Exception {

		LayoutStructure layoutStructure =
			_addLayoutStructureWithFormStyledLayoutStructureItem();

		List<FormStyledLayoutStructureItem> formStyledLayoutStructureItems =
			layoutStructure.getFormStyledLayoutStructureItems();

		FormStyledLayoutStructureItem formStyledLayoutStructureItem =
			formStyledLayoutStructureItems.get(0);

		ReflectionTestUtil.invoke(
			_mvcActionCommand, "_updateFormStyledLayoutStructureItemFormType",
			new Class<?>[] {
				List.class, FormStyledLayoutStructureItem.class,
				HttpServletRequest.class, HttpServletResponse.class,
				String.class, Layout.class, LayoutStructure.class, int.class,
				String.class, int.class, long.class, ServiceContext.class,
				long.class
			},
			new ArrayList<>(), formStyledLayoutStructureItem, null, null,
			"multistep", _draftLayout, layoutStructure, 2, "multistep", 3,
			_segmentsExperienceId,
			ServiceContextTestUtil.getServiceContext(_group.getGroupId()), 0);

		LayoutStructureItem formStepContainerStyledLayoutStructureItem =
			layoutStructure.getLayoutStructureItem(
				formStyledLayoutStructureItem.getChildrenItemId(0));

		for (String childrenItemId :
				LayoutStructureItemUtil.getChildrenItemIds(
					formStepContainerStyledLayoutStructureItem.
						getChildrenItemId(1),
					layoutStructure)) {

			LayoutStructureItem layoutStructureItem =
				layoutStructure.getLayoutStructureItem(childrenItemId);

			if (!(layoutStructureItem instanceof
					FragmentStyledLayoutStructureItem)) {

				continue;
			}

			FragmentStyledLayoutStructureItem
				fragmentStyledLayoutStructureItem =
					(FragmentStyledLayoutStructureItem)
						layoutStructure.getLayoutStructureItem(childrenItemId);

			FragmentEntryLink fragmentEntryLink =
				_fragmentEntryLinkLocalService.fetchFragmentEntryLink(
					fragmentStyledLayoutStructureItem.getFragmentEntryLinkId());

			Object value = _fragmentEntryConfigurationParser.getFieldValue(
				fragmentEntryLink.getConfiguration(),
				fragmentEntryLink.getEditableValues(),
				LocaleUtil.getMostRelevantLocale(), "type");

			Assert.assertTrue(
				ArrayUtil.contains(new String[] {"previous", "submit"}, value));
		}

		List<FragmentEntryLink> fragmentEntryLinks =
			_fragmentEntryLinkLocalService.
				getFragmentEntryLinksBySegmentsExperienceId(
					_group.getGroupId(), _segmentsExperienceId,
					_draftLayout.getPlid(), true);

		Assert.assertEquals(
			fragmentEntryLinks.toString(), 2, fragmentEntryLinks.size());

		for (FragmentEntryLink fragmentEntryLink : fragmentEntryLinks) {
			Assert.assertEquals(
				"next",
				_fragmentEntryConfigurationParser.getFieldValue(
					fragmentEntryLink.getConfiguration(),
					fragmentEntryLink.getEditableValues(),
					LocaleUtil.getMostRelevantLocale(), "type"));
		}
	}

	@Test
	public void testUpdateFormItemConfigMVCActionCommandFragmentEntryNotAvailable()
		throws Exception {

		String expectedFieldTypeLabel = RandomTestUtil.randomString();

		InfoFieldType infoFieldType = new InfoFieldType() {

			@Override
			public String getLabel(Locale locale) {
				return expectedFieldTypeLabel;
			}

			@Override
			public String getName() {
				return RandomTestUtil.randomString();
			}

		};

		InfoField<?>[] allInfoFields = ArrayUtil.append(
			_INFO_FIELDS, _getInfoField(infoFieldType));

		try (ComponentEnablerTemporarySwapper componentEnablerTemporarySwapper =
				new ComponentEnablerTemporarySwapper(
					_BUNDLE_SYMBOLIC_NAME, _COMPONENT_CLASS_NAME, true);
			MockInfoServiceRegistrationHolder
				mockInfoServiceRegistrationHolder =
					new MockInfoServiceRegistrationHolder(
						InfoFieldSet.builder(
						).infoFieldSetEntries(
							ListUtil.fromArray(allInfoFields)
						).build(),
						_portal, _editPageInfoItemCapability)) {

			long classNameId = _portal.getClassNameId(
				MockObject.class.getName());

			JSONObject addItemJSONObject =
				ContentLayoutTestUtil.addItemToLayout(
					"{}", LayoutDataItemTypeConstants.TYPE_FORM, _draftLayout,
					_layoutStructureProvider, _segmentsExperienceId);

			String formItemId = addItemJSONObject.getString("addedItemId");

			List<String> uniqueInfoFieldIds = TransformUtil.transform(
				ListUtil.fromArray(allInfoFields), InfoField::getUniqueId);

			JSONObject updateFormJSONObject = ReflectionTestUtil.invoke(
				_mvcActionCommand, "_updateFormStyledLayoutStructureItemConfig",
				new Class<?>[] {ActionRequest.class, ActionResponse.class},
				_getMockLiferayPortletActionRequest(
					StringUtil.merge(uniqueInfoFieldIds),
					JSONUtil.put(
						"classNameId", classNameId
					).put(
						"classTypeId", "0"
					).toString(),
					formItemId, _draftLayout),
				new MockLiferayPortletActionResponse());

			_assertUpdateFormStyledLayoutStructureItemConfigJSONObject(
				updateFormJSONObject, _INFO_FIELDS.length + 1, StringPool.BLANK,
				_language.format(
					_portal.getSiteDefaultLocale(_group),
					"some-fragments-are-missing.-x-fields-cannot-have-an-" +
						"associated-fragment-or-cannot-be-available-in-master",
					expectedFieldTypeLabel),
				0);

			_assertFormStyledLayoutStructureItem(
				classNameId, _INFO_FIELDS.length + 1, formItemId, _INFO_FIELDS,
				true, true);
		}
	}

	@Test
	@TestInfo("LPD-31772")
	public void testUpdateFormItemConfigMVCActionCommandIncreasingNumberOfSteps()
		throws Exception {

		LayoutStructure layoutStructure = new LayoutStructure();

		LayoutStructureItem rootLayoutStructureItem =
			layoutStructure.addRootLayoutStructureItem();

		FormStyledLayoutStructureItem formStyledLayoutStructureItem =
			(FormStyledLayoutStructureItem)
				layoutStructure.addFormStyledLayoutStructureItem(
					rootLayoutStructureItem.getItemId(), 0);

		formStyledLayoutStructureItem.setFormType("multiple");
		formStyledLayoutStructureItem.setNumberOfSteps(2);

		LayoutStructureItem formStepContainerStyledLayoutStructureItem =
			layoutStructure.addFormStepContainerStyledLayoutStructureItem(
				formStyledLayoutStructureItem.getItemId(), 0);

		layoutStructure.addFormStepLayoutStructureItem(
			formStepContainerStyledLayoutStructureItem.getItemId(), -1);

		LayoutStructureItem lastFormStepLayoutStructureItem =
			layoutStructure.addFormStepLayoutStructureItem(
				formStepContainerStyledLayoutStructureItem.getItemId(), -1);

		FragmentEntry fragmentEntry =
			_fragmentCollectionContributorRegistry.getFragmentEntry(
				"INPUTS-submit-button");

		FragmentEntryLink fragmentEntryLink =
			_fragmentEntryLinkLocalService.addFragmentEntryLink(
				null, TestPropsValues.getUserId(), _draftLayout.getGroupId(), 0,
				fragmentEntry.getFragmentEntryId(), _segmentsExperienceId,
				_draftLayout.getPlid(), fragmentEntry.getCss(),
				fragmentEntry.getHtml(), fragmentEntry.getJs(),
				fragmentEntry.getConfiguration(), null, StringPool.BLANK, 0,
				fragmentEntry.getFragmentEntryKey(), fragmentEntry.getType(),
				ServiceContextTestUtil.getServiceContext(_group.getGroupId()));

		layoutStructure.addFragmentStyledLayoutStructureItem(
			fragmentEntryLink.getFragmentEntryLinkId(),
			lastFormStepLayoutStructureItem.getItemId(), -1);

		ReflectionTestUtil.invoke(
			_mvcActionCommand, "_updateFormStyledLayoutStructureItemFormType",
			new Class<?>[] {
				List.class, FormStyledLayoutStructureItem.class,
				HttpServletRequest.class, HttpServletResponse.class,
				String.class, Layout.class, LayoutStructure.class, int.class,
				String.class, int.class, long.class, ServiceContext.class,
				long.class
			},
			new ArrayList<>(), formStyledLayoutStructureItem, null, null,
			"multistep", _draftLayout, layoutStructure, 4, "multistep", 2,
			_segmentsExperienceId,
			ServiceContextTestUtil.getServiceContext(_group.getGroupId()), 0);

		LayoutStructureItem firstFormStepStyledLayoutStructureItem =
			layoutStructure.getLayoutStructureItem(
				formStepContainerStyledLayoutStructureItem.getChildrenItemId(
					1));

		List<String> childrenItemIds =
			firstFormStepStyledLayoutStructureItem.getChildrenItemIds();

		Assert.assertEquals(
			childrenItemIds.toString(), 1, childrenItemIds.size());

		_assertFormButtonType(
			"next", firstFormStepStyledLayoutStructureItem.getChildrenItemId(0),
			layoutStructure);

		LayoutStructureItem secondFormStepStyledLayoutStructureItem =
			layoutStructure.getLayoutStructureItem(
				formStepContainerStyledLayoutStructureItem.getChildrenItemId(
					2));

		childrenItemIds =
			secondFormStepStyledLayoutStructureItem.getChildrenItemIds();

		Assert.assertEquals(
			childrenItemIds.toString(), 2, childrenItemIds.size());

		_assertFormButtonType(
			"previous",
			secondFormStepStyledLayoutStructureItem.getChildrenItemId(0),
			layoutStructure);
		_assertFormButtonType(
			"next",
			secondFormStepStyledLayoutStructureItem.getChildrenItemId(1),
			layoutStructure);

		LayoutStructureItem thirdFormStepStyledLayoutStructureItem =
			layoutStructure.getLayoutStructureItem(
				formStepContainerStyledLayoutStructureItem.getChildrenItemId(
					3));

		childrenItemIds =
			thirdFormStepStyledLayoutStructureItem.getChildrenItemIds();

		Assert.assertEquals(
			childrenItemIds.toString(), 2, childrenItemIds.size());

		_assertFormButtonType(
			"previous",
			thirdFormStepStyledLayoutStructureItem.getChildrenItemId(0),
			layoutStructure);
		_assertFormButtonType(
			"submit",
			thirdFormStepStyledLayoutStructureItem.getChildrenItemId(1),
			layoutStructure);
	}

	@Test
	public void testUpdateFormItemConfigMVCActionCommandInputsFragmentCollectionProviderNotAvailable()
		throws Exception {

		try (ComponentEnablerTemporarySwapper componentEnablerTemporarySwapper =
				new ComponentEnablerTemporarySwapper(
					_BUNDLE_SYMBOLIC_NAME, _COMPONENT_CLASS_NAME, false);
			MockInfoServiceRegistrationHolder
				mockInfoServiceRegistrationHolder =
					new MockInfoServiceRegistrationHolder(
						InfoFieldSet.builder(
						).infoFieldSetEntries(
							ListUtil.fromArray(_INFO_FIELDS)
						).build(),
						_portal, _editPageInfoItemCapability)) {

			List<String> uniqueInfoFieldIds = TransformUtil.transform(
				ListUtil.fromArray(_INFO_FIELDS), InfoField::getUniqueId);

			long classNameId = _portal.getClassNameId(
				MockObject.class.getName());

			JSONObject addItemJSONObject =
				ContentLayoutTestUtil.addItemToLayout(
					"{}", LayoutDataItemTypeConstants.TYPE_FORM, _draftLayout,
					_layoutStructureProvider, _segmentsExperienceId);

			String formItemId = addItemJSONObject.getString("addedItemId");

			JSONObject updateFormJSONObject = ReflectionTestUtil.invoke(
				_mvcActionCommand, "_updateFormStyledLayoutStructureItemConfig",
				new Class<?>[] {ActionRequest.class, ActionResponse.class},
				_getMockLiferayPortletActionRequest(
					StringUtil.merge(uniqueInfoFieldIds),
					JSONUtil.put(
						"classNameId", classNameId
					).put(
						"classTypeId", "0"
					).toString(),
					formItemId, _draftLayout),
				new MockLiferayPortletActionResponse());

			_assertUpdateFormStyledLayoutStructureItemConfigJSONObject(
				updateFormJSONObject, 0, StringPool.BLANK,
				_language.get(
					_portal.getSiteDefaultLocale(_group),
					"your-form-could-not-be-loaded-because-fragments-are-not-" +
						"available"),
				0);

			_assertFormStyledLayoutStructureItem(
				classNameId, 0, formItemId, new InfoField<?>[0], true, false);
		}
	}

	@Test
	public void testUpdateFormItemConfigMVCActionCommandMappingForm()
		throws Exception {

		try (ComponentEnablerTemporarySwapper componentEnablerTemporarySwapper =
				new ComponentEnablerTemporarySwapper(
					_BUNDLE_SYMBOLIC_NAME, _COMPONENT_CLASS_NAME, true);
			MockInfoServiceRegistrationHolder
				mockInfoServiceRegistrationHolder =
					new MockInfoServiceRegistrationHolder(
						InfoFieldSet.builder(
						).infoFieldSetEntries(
							ListUtil.fromArray(_INFO_FIELDS)
						).build(),
						_portal, _editPageInfoItemCapability)) {

			List<String> uniqueInfoFieldIds = TransformUtil.transform(
				ListUtil.fromArray(_INFO_FIELDS), InfoField::getUniqueId);

			long classNameId = _portal.getClassNameId(
				MockObject.class.getName());

			JSONObject addItemJSONObject =
				ContentLayoutTestUtil.addItemToLayout(
					"{}", LayoutDataItemTypeConstants.TYPE_FORM, _draftLayout,
					_layoutStructureProvider, _segmentsExperienceId);

			String formItemId = addItemJSONObject.getString("addedItemId");

			JSONObject updateFormJSONObject = ReflectionTestUtil.invoke(
				_mvcActionCommand, "_updateFormStyledLayoutStructureItemConfig",
				new Class<?>[] {ActionRequest.class, ActionResponse.class},
				_getMockLiferayPortletActionRequest(
					StringUtil.merge(uniqueInfoFieldIds),
					JSONUtil.put(
						"classNameId", classNameId
					).put(
						"classTypeId", "0"
					).toString(),
					formItemId, _draftLayout),
				new MockLiferayPortletActionResponse());

			_assertUpdateFormStyledLayoutStructureItemConfigJSONObject(
				updateFormJSONObject, _INFO_FIELDS.length + 1, StringPool.BLANK,
				StringPool.BLANK, 0);

			_assertFormStyledLayoutStructureItem(
				classNameId, _INFO_FIELDS.length + 1, formItemId, _INFO_FIELDS,
				true, true);
		}
	}

	@Test
	@TestInfo("LPD-31772")
	public void testUpdateFormItemConfigMVCActionCommandMappingFormChangingFormTypeMultistep()
		throws Exception {

		LayoutStructure layoutStructure = new LayoutStructure();

		LayoutStructureItem rootLayoutStructureItem =
			layoutStructure.addRootLayoutStructureItem();

		FormStyledLayoutStructureItem formStyledLayoutStructureItem =
			(FormStyledLayoutStructureItem)
				layoutStructure.addFormStyledLayoutStructureItem(
					rootLayoutStructureItem.getItemId(), 0);

		FragmentEntry fragmentEntry =
			_fragmentCollectionContributorRegistry.getFragmentEntry(
				"INPUTS-submit-button");

		FragmentEntryLink fragmentEntryLink =
			_fragmentEntryLinkLocalService.addFragmentEntryLink(
				null, TestPropsValues.getUserId(), _draftLayout.getGroupId(), 0,
				fragmentEntry.getFragmentEntryId(), _segmentsExperienceId,
				_draftLayout.getPlid(), fragmentEntry.getCss(),
				fragmentEntry.getHtml(), fragmentEntry.getJs(),
				fragmentEntry.getConfiguration(), null, StringPool.BLANK, 0,
				fragmentEntry.getFragmentEntryKey(), fragmentEntry.getType(),
				ServiceContextTestUtil.getServiceContext(_group.getGroupId()));

		layoutStructure.addFragmentStyledLayoutStructureItem(
			fragmentEntryLink.getFragmentEntryLinkId(),
			formStyledLayoutStructureItem.getItemId(), -1);

		ReflectionTestUtil.invoke(
			_mvcActionCommand, "_updateFormStyledLayoutStructureItemFormType",
			new Class<?>[] {
				List.class, FormStyledLayoutStructureItem.class,
				HttpServletRequest.class, HttpServletResponse.class,
				String.class, Layout.class, LayoutStructure.class, int.class,
				String.class, int.class, long.class, ServiceContext.class,
				long.class
			},
			new ArrayList<>(), formStyledLayoutStructureItem, null, null,
			"multistep", _draftLayout, layoutStructure, 2, "simple", 2,
			_segmentsExperienceId,
			ServiceContextTestUtil.getServiceContext(_group.getGroupId()), 0);

		LayoutStructureItem formStepContainerStyledLayoutStructureItem =
			layoutStructure.getLayoutStructureItem(
				formStyledLayoutStructureItem.getChildrenItemId(0));

		LayoutStructureItem firstFormStepStyledLayoutStructureItem =
			layoutStructure.getLayoutStructureItem(
				formStepContainerStyledLayoutStructureItem.getChildrenItemId(
					0));

		List<String> childrenItemIds =
			firstFormStepStyledLayoutStructureItem.getChildrenItemIds();

		Assert.assertEquals(
			childrenItemIds.toString(), 1, childrenItemIds.size());

		_assertFormButtonType(
			"next", firstFormStepStyledLayoutStructureItem.getChildrenItemId(0),
			layoutStructure);

		LayoutStructureItem secondFormStepStyledLayoutStructureItem =
			layoutStructure.getLayoutStructureItem(
				formStepContainerStyledLayoutStructureItem.getChildrenItemId(
					1));

		childrenItemIds =
			secondFormStepStyledLayoutStructureItem.getChildrenItemIds();

		Assert.assertEquals(
			childrenItemIds.toString(), 2, childrenItemIds.size());

		_assertFormButtonType(
			"previous",
			secondFormStepStyledLayoutStructureItem.getChildrenItemId(0),
			layoutStructure);
		_assertFormButtonType(
			"submit",
			secondFormStepStyledLayoutStructureItem.getChildrenItemId(1),
			layoutStructure);
	}

	@Test
	public void testUpdateFormItemConfigMVCActionCommandMappingFormChangingFormTypeSimple1()
		throws Exception {

		LayoutStructure layoutStructure = LayoutStructure.of(_readLayoutData());

		FormStyledLayoutStructureItem formStyledLayoutStructureItem =
			(FormStyledLayoutStructureItem)
				layoutStructure.getLayoutStructureItem("formId");

		ReflectionTestUtil.invoke(
			_mvcActionCommand, "_updateFormStyledLayoutStructureItemFormType",
			new Class<?>[] {
				List.class, FormStyledLayoutStructureItem.class,
				HttpServletRequest.class, HttpServletResponse.class,
				String.class, Layout.class, LayoutStructure.class, int.class,
				String.class, int.class, long.class, ServiceContext.class,
				long.class
			},
			new ArrayList<>(), formStyledLayoutStructureItem, null, null,
			"multistep", _draftLayout, layoutStructure, 2, "simple", 2,
			_segmentsExperienceId,
			ServiceContextTestUtil.getServiceContext(_group.getGroupId()), 0);

		ReflectionTestUtil.invoke(
			_mvcActionCommand, "_updateFormStyledLayoutStructureItemFormType",
			new Class<?>[] {
				List.class, FormStyledLayoutStructureItem.class,
				HttpServletRequest.class, HttpServletResponse.class,
				String.class, Layout.class, LayoutStructure.class, int.class,
				String.class, int.class, long.class, ServiceContext.class,
				long.class
			},
			new ArrayList<>(), formStyledLayoutStructureItem, null, null,
			"simple", _draftLayout, layoutStructure, 2, "multistep", 2,
			_segmentsExperienceId,
			ServiceContextTestUtil.getServiceContext(_group.getGroupId()), 0);

		List<String> childrenItemIds =
			formStyledLayoutStructureItem.getChildrenItemIds();

		Assert.assertEquals(
			childrenItemIds.toString(), 1, childrenItemIds.size());

		LayoutStructureItem layoutStructureItem =
			layoutStructure.getLayoutStructureItem(childrenItemIds.get(0));

		Assert.assertTrue(
			layoutStructureItem instanceof ContainerStyledLayoutStructureItem);
	}

	@Test
	@TestInfo("LPD-31772")
	public void testUpdateFormItemConfigMVCActionCommandMappingFormChangingFormTypeSimple2()
		throws Exception {

		LayoutStructure layoutStructure =
			_addLayoutStructureWithFormStyledLayoutStructureItem();

		List<FormStyledLayoutStructureItem> formStyledLayoutStructureItems =
			layoutStructure.getFormStyledLayoutStructureItems();

		FormStyledLayoutStructureItem formStyledLayoutStructureItem =
			formStyledLayoutStructureItems.get(0);

		ReflectionTestUtil.invoke(
			_mvcActionCommand, "_updateFormStyledLayoutStructureItemFormType",
			new Class<?>[] {
				List.class, FormStyledLayoutStructureItem.class,
				HttpServletRequest.class, HttpServletResponse.class,
				String.class, Layout.class, LayoutStructure.class, int.class,
				String.class, int.class, long.class, ServiceContext.class,
				long.class
			},
			new ArrayList<>(), formStyledLayoutStructureItem, null, null,
			"simple", _draftLayout, layoutStructure, 0, "multistep", 0,
			_segmentsExperienceId,
			ServiceContextTestUtil.getServiceContext(_group.getGroupId()), 0);

		List<String> childrenItemIds =
			formStyledLayoutStructureItem.getChildrenItemIds();

		Assert.assertEquals(
			childrenItemIds.toString(), 3, childrenItemIds.size());

		for (String childrenItemId : childrenItemIds) {
			ContainerStyledLayoutStructureItem
				containerStyledLayoutStructureItem =
					(ContainerStyledLayoutStructureItem)
						layoutStructure.getLayoutStructureItem(childrenItemId);

			FragmentStyledLayoutStructureItem
				fragmentStyledLayoutStructureItem =
					(FragmentStyledLayoutStructureItem)
						layoutStructure.getLayoutStructureItem(
							containerStyledLayoutStructureItem.
								getChildrenItemId(0));

			FragmentEntryLink fragmentEntryLink =
				_fragmentEntryLinkLocalService.fetchFragmentEntryLink(
					fragmentStyledLayoutStructureItem.getFragmentEntryLinkId());

			Assert.assertEquals(
				"submit",
				_fragmentEntryConfigurationParser.getFieldValue(
					fragmentEntryLink.getConfiguration(),
					fragmentEntryLink.getEditableValues(),
					LocaleUtil.getMostRelevantLocale(), "type"));
		}

		Map<Long, LayoutStructureItem> fragmentLayoutStructureItems =
			layoutStructure.getFragmentLayoutStructureItems();

		for (LayoutStructureItem layoutStructureItem :
				fragmentLayoutStructureItems.values()) {

			FragmentStyledLayoutStructureItem
				fragmentStyledLayoutStructureItem =
					(FragmentStyledLayoutStructureItem)layoutStructureItem;

			FragmentEntryLink fragmentEntryLink =
				_fragmentEntryLinkLocalService.fetchFragmentEntryLink(
					fragmentStyledLayoutStructureItem.getFragmentEntryLinkId());

			Object value = _fragmentEntryConfigurationParser.getFieldValue(
				fragmentEntryLink.getConfiguration(),
				fragmentEntryLink.getEditableValues(),
				LocaleUtil.getMostRelevantLocale(), "type");

			if (Objects.equals(value, "previous") ||
				Objects.equals(value, "next")) {

				Assert.assertTrue(fragmentEntryLink.isDeleted());
				Assert.assertTrue(
					layoutStructure.isItemMarkedForDeletion(
						fragmentStyledLayoutStructureItem.getItemId()));
			}
			else {
				Assert.assertFalse(fragmentEntryLink.isDeleted());
				Assert.assertFalse(
					layoutStructure.isItemMarkedForDeletion(
						fragmentStyledLayoutStructureItem.getItemId()));
			}
		}
	}

	@Test
	public void testUpdateFormItemConfigMVCActionCommandMappingFormDeletingInfoFields()
		throws Exception {

		InfoField<?>[] allInfoFields = ArrayUtil.append(
			_INFO_FIELDS,
			new InfoField<?>[] {
				_getInfoField(BooleanInfoFieldType.INSTANCE),
				_getInfoField(TextInfoFieldType.INSTANCE)
			});

		try (ComponentEnablerTemporarySwapper componentEnablerTemporarySwapper =
				new ComponentEnablerTemporarySwapper(
					_BUNDLE_SYMBOLIC_NAME, _COMPONENT_CLASS_NAME, true);
			MockInfoServiceRegistrationHolder
				mockInfoServiceRegistrationHolder =
					new MockInfoServiceRegistrationHolder(
						InfoFieldSet.builder(
						).infoFieldSetEntries(
							ListUtil.fromArray(allInfoFields)
						).build(),
						_portal, _editPageInfoItemCapability)) {

			JSONObject addItemJSONObject =
				ContentLayoutTestUtil.addItemToLayout(
					"{}", LayoutDataItemTypeConstants.TYPE_FORM, _draftLayout,
					_layoutStructureProvider, _segmentsExperienceId);

			long classNameId = _portal.getClassNameId(
				MockObject.class.getName());

			String formItemId = addItemJSONObject.getString("addedItemId");

			List<String> uniqueInfoFieldIds = TransformUtil.transform(
				ListUtil.fromArray(allInfoFields), InfoField::getUniqueId);

			ReflectionTestUtil.invoke(
				_mvcActionCommand, "doTransactionalCommand",
				new Class<?>[] {ActionRequest.class, ActionResponse.class},
				_getMockLiferayPortletActionRequest(
					StringUtil.merge(uniqueInfoFieldIds),
					JSONUtil.put(
						"classNameId", classNameId
					).put(
						"classTypeId", "0"
					).toString(),
					formItemId, _draftLayout),
				new MockLiferayPortletActionResponse());

			uniqueInfoFieldIds = TransformUtil.transform(
				ListUtil.fromArray(_INFO_FIELDS), InfoField::getUniqueId);

			JSONObject updateFormJSONObject = ReflectionTestUtil.invoke(
				_mvcActionCommand, "doTransactionalCommand",
				new Class<?>[] {ActionRequest.class, ActionResponse.class},
				_getMockLiferayPortletActionRequest(
					StringUtil.merge(uniqueInfoFieldIds),
					JSONUtil.put(
						"classNameId", classNameId
					).put(
						"classTypeId", "0"
					).toString(),
					formItemId, _draftLayout),
				new MockLiferayPortletActionResponse());

			_assertUpdateFormStyledLayoutStructureItemConfigJSONObject(
				updateFormJSONObject, 0, StringPool.BLANK, StringPool.BLANK, 2);

			_assertFormStyledLayoutStructureItem(
				classNameId, _INFO_FIELDS.length + 1, formItemId, allInfoFields,
				true, false);
		}
	}

	@Test
	public void testUpdateFormItemConfigMVCActionCommandMappingFormWithEmptySpecificInfoField()
		throws Exception {

		try (ComponentEnablerTemporarySwapper componentEnablerTemporarySwapper =
				new ComponentEnablerTemporarySwapper(
					_BUNDLE_SYMBOLIC_NAME, _COMPONENT_CLASS_NAME, true);
			MockInfoServiceRegistrationHolder
				mockInfoServiceRegistrationHolder =
					new MockInfoServiceRegistrationHolder(
						InfoFieldSet.builder(
						).infoFieldSetEntries(
							ListUtil.fromArray(_INFO_FIELDS)
						).build(),
						_portal, _editPageInfoItemCapability)) {

			JSONObject addItemJSONObject =
				ContentLayoutTestUtil.addItemToLayout(
					"{}", LayoutDataItemTypeConstants.TYPE_FORM, _draftLayout,
					_layoutStructureProvider, _segmentsExperienceId);

			long classNameId = _portal.getClassNameId(
				MockObject.class.getName());

			String formItemId = addItemJSONObject.getString("addedItemId");

			JSONObject updateFormJSONObject = ReflectionTestUtil.invoke(
				_mvcActionCommand, "_updateFormStyledLayoutStructureItemConfig",
				new Class<?>[] {ActionRequest.class, ActionResponse.class},
				_getMockLiferayPortletActionRequest(
					null,
					JSONUtil.put(
						"classNameId", classNameId
					).put(
						"classTypeId", "0"
					).toString(),
					formItemId, _draftLayout),
				new MockLiferayPortletActionResponse());

			_assertUpdateFormStyledLayoutStructureItemConfigJSONObject(
				updateFormJSONObject, 0, StringPool.BLANK, StringPool.BLANK, 0);

			_assertFormStyledLayoutStructureItem(
				classNameId, 0, formItemId, new InfoField<?>[0], true, false);
		}
	}

	@Test
	public void testUpdateFormItemConfigMVCActionCommandMappingFormWithNewInfoFields()
		throws Exception {

		InfoField<?>[] allInfoFields = ArrayUtil.append(
			_INFO_FIELDS,
			new InfoField<?>[] {
				_getInfoField(BooleanInfoFieldType.INSTANCE),
				_getInfoField(TextInfoFieldType.INSTANCE)
			});

		try (ComponentEnablerTemporarySwapper componentEnablerTemporarySwapper =
				new ComponentEnablerTemporarySwapper(
					_BUNDLE_SYMBOLIC_NAME, _COMPONENT_CLASS_NAME, true);
			MockInfoServiceRegistrationHolder
				mockInfoServiceRegistrationHolder =
					new MockInfoServiceRegistrationHolder(
						InfoFieldSet.builder(
						).infoFieldSetEntries(
							ListUtil.fromArray(allInfoFields)
						).build(),
						_portal, _editPageInfoItemCapability)) {

			JSONObject addItemJSONObject =
				ContentLayoutTestUtil.addItemToLayout(
					"{}", LayoutDataItemTypeConstants.TYPE_FORM, _draftLayout,
					_layoutStructureProvider, _segmentsExperienceId);

			long classNameId = _portal.getClassNameId(
				MockObject.class.getName());

			String formItemId = addItemJSONObject.getString("addedItemId");

			List<String> uniqueInfoFieldIds = TransformUtil.transform(
				ListUtil.fromArray(_INFO_FIELDS), InfoField::getUniqueId);

			ReflectionTestUtil.invoke(
				_mvcActionCommand, "_updateFormStyledLayoutStructureItemConfig",
				new Class<?>[] {ActionRequest.class, ActionResponse.class},
				_getMockLiferayPortletActionRequest(
					StringUtil.merge(uniqueInfoFieldIds),
					JSONUtil.put(
						"classNameId", classNameId
					).put(
						"classTypeId", "0"
					).toString(),
					formItemId, _draftLayout),
				new MockLiferayPortletActionResponse());

			uniqueInfoFieldIds = TransformUtil.transform(
				ListUtil.fromArray(allInfoFields), InfoField::getUniqueId);

			JSONObject updateFormJSONObject = ReflectionTestUtil.invoke(
				_mvcActionCommand, "_updateFormStyledLayoutStructureItemConfig",
				new Class<?>[] {ActionRequest.class, ActionResponse.class},
				_getMockLiferayPortletActionRequest(
					StringUtil.merge(uniqueInfoFieldIds),
					JSONUtil.put(
						"classNameId", classNameId
					).put(
						"classTypeId", "0"
					).toString(),
					formItemId, _draftLayout),
				new MockLiferayPortletActionResponse());

			_assertUpdateFormStyledLayoutStructureItemConfigJSONObject(
				updateFormJSONObject, 2, StringPool.BLANK, StringPool.BLANK, 0);

			_assertFormStyledLayoutStructureItem(
				classNameId, allInfoFields.length + 1, formItemId,
				allInfoFields, true, false);
		}
	}

	@Test
	public void testUpdateFormItemConfigMVCActionCommandMappingFormWithNonexistingSpecificInfoField()
		throws Exception {

		try (ComponentEnablerTemporarySwapper componentEnablerTemporarySwapper =
				new ComponentEnablerTemporarySwapper(
					_BUNDLE_SYMBOLIC_NAME, _COMPONENT_CLASS_NAME, true);
			MockInfoServiceRegistrationHolder
				mockInfoServiceRegistrationHolder =
					new MockInfoServiceRegistrationHolder(
						InfoFieldSet.builder(
						).infoFieldSetEntries(
							ListUtil.fromArray(_INFO_FIELDS)
						).build(),
						_portal, _editPageInfoItemCapability)) {

			JSONObject addItemJSONObject =
				ContentLayoutTestUtil.addItemToLayout(
					"{}", LayoutDataItemTypeConstants.TYPE_FORM, _draftLayout,
					_layoutStructureProvider, _segmentsExperienceId);

			long classNameId = _portal.getClassNameId(
				MockObject.class.getName());

			String formItemId = addItemJSONObject.getString("addedItemId");

			JSONObject updateFormJSONObject = ReflectionTestUtil.invoke(
				_mvcActionCommand, "_updateFormStyledLayoutStructureItemConfig",
				new Class<?>[] {ActionRequest.class, ActionResponse.class},
				_getMockLiferayPortletActionRequest(
					RandomTestUtil.randomString(),
					JSONUtil.put(
						"classNameId", classNameId
					).put(
						"classTypeId", "0"
					).toString(),
					formItemId, _draftLayout),
				new MockLiferayPortletActionResponse());

			_assertUpdateFormStyledLayoutStructureItemConfigJSONObject(
				updateFormJSONObject, 1, StringPool.BLANK, StringPool.BLANK, 0);

			_assertFormStyledLayoutStructureItem(
				classNameId, 1, formItemId, new InfoField<?>[0], true, true);
		}
	}

	@Test
	public void testUpdateFormItemConfigMVCActionCommandMappingFormWithSpecificInfoFields()
		throws Exception {

		InfoField<?> booleanInfoField = _getInfoField(
			BooleanInfoFieldType.INSTANCE);
		InfoField<?> textInfoField = _getInfoField(TextInfoFieldType.INSTANCE);

		InfoField<?>[] customInfoFields = new InfoField<?>[] {
			booleanInfoField, textInfoField
		};

		InfoField<?>[] allInfoFields = ArrayUtil.append(
			_INFO_FIELDS, customInfoFields);

		try (ComponentEnablerTemporarySwapper componentEnablerTemporarySwapper =
				new ComponentEnablerTemporarySwapper(
					_BUNDLE_SYMBOLIC_NAME, _COMPONENT_CLASS_NAME, true);
			MockInfoServiceRegistrationHolder
				mockInfoServiceRegistrationHolder =
					new MockInfoServiceRegistrationHolder(
						InfoFieldSet.builder(
						).infoFieldSetEntries(
							ListUtil.fromArray(allInfoFields)
						).build(),
						_portal, _editPageInfoItemCapability)) {

			JSONObject addItemJSONObject =
				ContentLayoutTestUtil.addItemToLayout(
					"{}", LayoutDataItemTypeConstants.TYPE_FORM, _draftLayout,
					_layoutStructureProvider, _segmentsExperienceId);

			long classNameId = _portal.getClassNameId(
				MockObject.class.getName());

			String formItemId = addItemJSONObject.getString("addedItemId");

			JSONObject updateFormJSONObject = ReflectionTestUtil.invoke(
				_mvcActionCommand, "_updateFormStyledLayoutStructureItemConfig",
				new Class<?>[] {ActionRequest.class, ActionResponse.class},
				_getMockLiferayPortletActionRequest(
					StringUtil.merge(
						new String[] {
							booleanInfoField.getUniqueId(),
							textInfoField.getUniqueId()
						}),
					JSONUtil.put(
						"classNameId", classNameId
					).put(
						"classTypeId", "0"
					).toString(),
					formItemId, _draftLayout),
				new MockLiferayPortletActionResponse());

			_assertUpdateFormStyledLayoutStructureItemConfigJSONObject(
				updateFormJSONObject, customInfoFields.length + 1,
				StringPool.BLANK, StringPool.BLANK, 0);

			_assertFormStyledLayoutStructureItem(
				classNameId, customInfoFields.length + 1, formItemId,
				customInfoFields, true, true);
		}
	}

	@Test
	public void testUpdateFormItemConfigMVCActionCommandMoreThanOneFragmentEntryNotAvailable()
		throws Exception {

		TreeSet<String> expectedFieldTypeLabels = new TreeSet<>();

		expectedFieldTypeLabels.add(RandomTestUtil.randomString());
		expectedFieldTypeLabels.add(RandomTestUtil.randomString());

		InfoFieldType infoFieldType1 = new InfoFieldType() {

			@Override
			public String getLabel(Locale locale) {
				return expectedFieldTypeLabels.first();
			}

			@Override
			public String getName() {
				return RandomTestUtil.randomString();
			}

		};

		InfoFieldType infoFieldType2 = new InfoFieldType() {

			@Override
			public String getLabel(Locale locale) {
				return expectedFieldTypeLabels.last();
			}

			@Override
			public String getName() {
				return RandomTestUtil.randomString();
			}

		};

		InfoField<?>[] allInfoFields = ArrayUtil.append(
			_INFO_FIELDS,
			new InfoField<?>[] {
				_getInfoField(infoFieldType1), _getInfoField(infoFieldType2)
			});

		try (ComponentEnablerTemporarySwapper componentEnablerTemporarySwapper =
				new ComponentEnablerTemporarySwapper(
					_BUNDLE_SYMBOLIC_NAME, _COMPONENT_CLASS_NAME, true);
			MockInfoServiceRegistrationHolder
				mockInfoServiceRegistrationHolder =
					new MockInfoServiceRegistrationHolder(
						InfoFieldSet.builder(
						).infoFieldSetEntries(
							ListUtil.fromArray(allInfoFields)
						).build(),
						_portal, _editPageInfoItemCapability)) {

			List<String> uniqueInfoFieldIds = TransformUtil.transform(
				ListUtil.fromArray(allInfoFields), InfoField::getUniqueId);

			long classNameId = _portal.getClassNameId(
				MockObject.class.getName());

			JSONObject addItemJSONObject =
				ContentLayoutTestUtil.addItemToLayout(
					"{}", LayoutDataItemTypeConstants.TYPE_FORM, _draftLayout,
					_layoutStructureProvider, _segmentsExperienceId);

			String formItemId = addItemJSONObject.getString("addedItemId");

			JSONObject updateFormJSONObject = ReflectionTestUtil.invoke(
				_mvcActionCommand, "_updateFormStyledLayoutStructureItemConfig",
				new Class<?>[] {ActionRequest.class, ActionResponse.class},
				_getMockLiferayPortletActionRequest(
					StringUtil.merge(uniqueInfoFieldIds),
					JSONUtil.put(
						"classNameId", classNameId
					).put(
						"classTypeId", "0"
					).toString(),
					formItemId, _draftLayout),
				new MockLiferayPortletActionResponse());

			_assertUpdateFormStyledLayoutStructureItemConfigJSONObject(
				updateFormJSONObject, _INFO_FIELDS.length + 1, StringPool.BLANK,
				_language.format(
					_portal.getSiteDefaultLocale(_group),
					"some-fragments-are-missing.-x-and-x-fields-cannot-have-" +
						"an-associated-fragment-or-cannot-be-available-in-" +
							"master",
					ArrayUtil.sortedUnique(
						new String[] {
							expectedFieldTypeLabels.first(),
							expectedFieldTypeLabels.last()
						})),
				0);

			_assertFormStyledLayoutStructureItem(
				classNameId, _INFO_FIELDS.length + 1, formItemId, _INFO_FIELDS,
				true, true);
		}
	}

	@Test
	public void testUpdateFormItemConfigMVCActionCommandNoMappingChange()
		throws Exception {

		try (ComponentEnablerTemporarySwapper componentEnablerTemporarySwapper =
				new ComponentEnablerTemporarySwapper(
					_BUNDLE_SYMBOLIC_NAME, _COMPONENT_CLASS_NAME, true);
			MockInfoServiceRegistrationHolder
				mockInfoServiceRegistrationHolder =
					new MockInfoServiceRegistrationHolder(
						InfoFieldSet.builder(
						).infoFieldSetEntries(
							ListUtil.fromArray(_INFO_FIELDS)
						).build(),
						_portal, _editPageInfoItemCapability)) {

			List<String> uniqueInfoFieldIds = TransformUtil.transform(
				ListUtil.fromArray(_INFO_FIELDS), InfoField::getUniqueId);

			long classNameId = _portal.getClassNameId(
				MockObject.class.getName());

			JSONObject jsonObject = ContentLayoutTestUtil.addFormToLayout(
				false, String.valueOf(classNameId), "0", _draftLayout,
				_layoutStructureProvider, _segmentsExperienceId, _INFO_FIELDS);

			String formItemId = jsonObject.getString("addedItemId");

			_assertFormStyledLayoutStructureItem(
				classNameId, _INFO_FIELDS.length, formItemId, _INFO_FIELDS,
				false, false);

			JSONObject updateFormJSONObject = ReflectionTestUtil.invoke(
				_mvcActionCommand, "_updateFormStyledLayoutStructureItemConfig",
				new Class<?>[] {ActionRequest.class, ActionResponse.class},
				_getMockLiferayPortletActionRequest(
					StringUtil.merge(uniqueInfoFieldIds),
					JSONUtil.put(
						"successMessage",
						JSONUtil.put(
							"message",
							JSONUtil.put(
								LocaleUtil.toLanguageId(
									LocaleUtil.getMostRelevantLocale()),
								RandomTestUtil.randomString()))
					).toString(),
					formItemId, _draftLayout),
				new MockLiferayPortletActionResponse());

			_assertUpdateFormStyledLayoutStructureItemConfigJSONObject(
				updateFormJSONObject, 0, StringPool.BLANK, StringPool.BLANK, 0);

			_assertFormStyledLayoutStructureItem(
				classNameId, _INFO_FIELDS.length, formItemId, _INFO_FIELDS,
				false, false);
		}
	}

	@Test
	public void testUpdateFormItemConfigMVCActionCommandResetMapping()
		throws Exception {

		try (ComponentEnablerTemporarySwapper componentEnablerTemporarySwapper =
				new ComponentEnablerTemporarySwapper(
					_BUNDLE_SYMBOLIC_NAME, _COMPONENT_CLASS_NAME, true);
			MockInfoServiceRegistrationHolder
				mockInfoServiceRegistrationHolder =
					new MockInfoServiceRegistrationHolder(
						InfoFieldSet.builder(
						).infoFieldSetEntries(
							ListUtil.fromArray(_INFO_FIELDS)
						).build(),
						_portal, _editPageInfoItemCapability)) {

			long classNameId = _portal.getClassNameId(
				MockObject.class.getName());

			JSONObject jsonObject = ContentLayoutTestUtil.addFormToLayout(
				false, String.valueOf(classNameId), "0", _draftLayout,
				_layoutStructureProvider, _segmentsExperienceId, _INFO_FIELDS);

			String formItemId = jsonObject.getString("addedItemId");

			_assertFormStyledLayoutStructureItem(
				classNameId, _INFO_FIELDS.length, formItemId, _INFO_FIELDS,
				false, false);

			JSONObject updateFormJSONObject = ReflectionTestUtil.invoke(
				_mvcActionCommand, "_updateFormStyledLayoutStructureItemConfig",
				new Class<?>[] {ActionRequest.class, ActionResponse.class},
				_getMockLiferayPortletActionRequest(
					null,
					JSONUtil.put(
						"classNameId", "0"
					).put(
						"classTypeId", "0"
					).toString(),
					formItemId, _draftLayout),
				new MockLiferayPortletActionResponse());

			_assertUpdateFormStyledLayoutStructureItemConfigJSONObject(
				updateFormJSONObject, 0, StringPool.BLANK, StringPool.BLANK,
				_INFO_FIELDS.length);

			_assertFormStyledLayoutStructureItem(
				0, 0, formItemId, new InfoField<?>[0], false, false);
		}
	}

	private static <T extends InfoFieldType> InfoField<T> _getInfoField(
		T infoFieldTypeInstance) {

		return InfoField.builder(
		).infoFieldType(
			infoFieldTypeInstance
		).uniqueId(
			RandomTestUtil.randomString()
		).name(
			RandomTestUtil.randomString()
		).editable(
			true
		).labelInfoLocalizedValue(
			InfoLocalizedValue.singleValue(RandomTestUtil.randomString())
		).localizable(
			true
		).build();
	}

	private LayoutStructure
			_addLayoutStructureWithFormStyledLayoutStructureItem()
		throws Exception {

		LayoutStructure layoutStructure = new LayoutStructure();

		LayoutStructureItem rootLayoutStructureItem =
			layoutStructure.addRootLayoutStructureItem();

		FormStyledLayoutStructureItem formStyledLayoutStructureItem =
			(FormStyledLayoutStructureItem)
				layoutStructure.addFormStyledLayoutStructureItem(
					rootLayoutStructureItem.getItemId(), 0);

		formStyledLayoutStructureItem.setFormType("multiple");
		formStyledLayoutStructureItem.setNumberOfSteps(3);

		LayoutStructureItem formStepContainerStyledLayoutStructureItem =
			layoutStructure.addFormStepContainerStyledLayoutStructureItem(
				formStyledLayoutStructureItem.getItemId(), 0);

		for (int i = 0; i < 3; i++) {
			LayoutStructureItem formStepLayoutStructureItem =
				layoutStructure.addFormStepLayoutStructureItem(
					formStepContainerStyledLayoutStructureItem.getItemId(), 0);

			LayoutStructureItem containerStyledLayoutStructureItem =
				layoutStructure.addContainerStyledLayoutStructureItem(
					formStepLayoutStructureItem.getItemId(), 0);

			layoutStructure.addFragmentStyledLayoutStructureItem(
				_addSubmitButtonFragmentEntryLink("submit"),
				containerStyledLayoutStructureItem.getItemId(), 0);
			layoutStructure.addFragmentStyledLayoutStructureItem(
				_addSubmitButtonFragmentEntryLink("previous"),
				containerStyledLayoutStructureItem.getItemId(), 0);
			layoutStructure.addFragmentStyledLayoutStructureItem(
				_addSubmitButtonFragmentEntryLink("next"),
				containerStyledLayoutStructureItem.getItemId(), 0);
		}

		return layoutStructure;
	}

	private long _addSubmitButtonFragmentEntryLink(String type)
		throws Exception {

		FragmentEntry fragmentEntry =
			_fragmentCollectionContributorRegistry.getFragmentEntry(
				"INPUTS-submit-button");

		JSONObject editableValuesJSONObject = JSONUtil.put(
			FragmentEntryProcessorConstants.
				KEY_FREEMARKER_FRAGMENT_ENTRY_PROCESSOR,
			JSONUtil.put("type", type));

		FragmentEntryLink fragmentEntryLink =
			_fragmentEntryLinkLocalService.addFragmentEntryLink(
				null, TestPropsValues.getUserId(), _group.getGroupId(), 0,
				fragmentEntry.getFragmentEntryId(), _segmentsExperienceId,
				_draftLayout.getPlid(), fragmentEntry.getCss(),
				fragmentEntry.getHtml(), fragmentEntry.getJs(),
				fragmentEntry.getConfiguration(),
				editableValuesJSONObject.toString(), StringPool.BLANK, 0,
				fragmentEntry.getFragmentEntryKey(), fragmentEntry.getType(),
				ServiceContextTestUtil.getServiceContext(_group.getGroupId()));

		return fragmentEntryLink.getFragmentEntryLinkId();
	}

	private void _assertFormButtonType(
		String expectedType, String itemId, LayoutStructure layoutStructure) {

		FragmentStyledLayoutStructureItem fragmentStyledLayoutStructureItem =
			(FragmentStyledLayoutStructureItem)
				layoutStructure.getLayoutStructureItem(itemId);

		FragmentEntryLink fragmentEntryLink =
			_fragmentEntryLinkLocalService.fetchFragmentEntryLink(
				fragmentStyledLayoutStructureItem.getFragmentEntryLinkId());

		Assert.assertEquals(
			expectedType,
			_fragmentEntryConfigurationParser.getFieldValue(
				fragmentEntryLink.getConfiguration(),
				fragmentEntryLink.getEditableValues(),
				LocaleUtil.getMostRelevantLocale(), "type"));
	}

	private void _assertFormStyledLayoutStructureItem(
			long expectedClassNameId, int expectedChildrenSize,
			String formItemId, InfoField<?>[] infoFields,
			boolean assertRendererKey, boolean submitButton)
		throws PortalException {

		LayoutPageTemplateStructure layoutPageTemplateStructure =
			_layoutPageTemplateStructureLocalService.
				fetchLayoutPageTemplateStructure(
					_group.getGroupId(), _draftLayout.getPlid());

		LayoutStructure layoutStructure = LayoutStructure.of(
			layoutPageTemplateStructure.getDefaultSegmentsExperienceData());

		FormStyledLayoutStructureItem formStyledLayoutStructureItem =
			(FormStyledLayoutStructureItem)
				layoutStructure.getLayoutStructureItem(formItemId);

		Assert.assertEquals(
			expectedClassNameId,
			formStyledLayoutStructureItem.getClassNameId());
		Assert.assertEquals(0, formStyledLayoutStructureItem.getClassTypeId());

		List<String> childrenItemIds =
			formStyledLayoutStructureItem.getChildrenItemIds();

		Assert.assertEquals(
			childrenItemIds.toString(), expectedChildrenSize,
			childrenItemIds.size());

		int i = 0;

		for (String itemId : childrenItemIds) {
			FragmentStyledLayoutStructureItem
				fragmentStyledLayoutStructureItem =
					(FragmentStyledLayoutStructureItem)
						layoutStructure.getLayoutStructureItem(itemId);

			FragmentEntryLink fragmentEntryLink =
				_fragmentEntryLinkLocalService.getFragmentEntryLink(
					fragmentStyledLayoutStructureItem.getFragmentEntryLinkId());

			if (Objects.equals(
					fragmentEntryLink.getRendererKey(),
					"INPUTS-submit-button")) {

				_assertFragmentEntry(
					StringPool.BLANK, "INPUTS-submit-button", fragmentEntryLink,
					assertRendererKey);
			}
			else {
				InfoField<?> infoField = infoFields[i++];

				_assertFragmentEntry(
					infoField.getUniqueId(), _getExpectedRendererKey(infoField),
					fragmentEntryLink, assertRendererKey);
			}
		}

		if (!submitButton) {
			return;
		}

		FragmentStyledLayoutStructureItem fragmentStyledLayoutStructureItem =
			(FragmentStyledLayoutStructureItem)
				layoutStructure.getLayoutStructureItem(
					childrenItemIds.get(infoFields.length));

		_assertFragmentEntry(
			StringPool.BLANK, "INPUTS-submit-button",
			_fragmentEntryLinkLocalService.getFragmentEntryLink(
				fragmentStyledLayoutStructureItem.getFragmentEntryLinkId()),
			assertRendererKey);
	}

	private void _assertFragmentEntry(
		String expectedInputFieldId, String expectedRendererKey,
		FragmentEntryLink fragmentEntryLink, boolean assertRendererKey) {

		String inputFieldId = GetterUtil.getString(
			_fragmentEntryConfigurationParser.getFieldValue(
				fragmentEntryLink.getEditableValues(),
				new FragmentConfigurationField(
					"inputFieldId", "string", "", false, "text"),
				LocaleUtil.getMostRelevantLocale()));

		Assert.assertEquals(expectedInputFieldId, inputFieldId);

		if (!assertRendererKey) {
			return;
		}

		Assert.assertEquals(
			expectedRendererKey, fragmentEntryLink.getRendererKey());
	}

	private void _assertUpdateFormStyledLayoutStructureItemConfigJSONObject(
		JSONObject jsonObject, int expectedFragmentEntryLinks,
		String expectedError, String expectedErrorMessage,
		int expectedRemovedLayoutStructureItems) {

		Assert.assertEquals(expectedError, jsonObject.getString("error"));

		if (Validator.isNotNull(expectedError)) {
			return;
		}

		Assert.assertTrue(jsonObject.has("layoutData"));

		JSONObject fragmentEntryLinksJSONObject = jsonObject.getJSONObject(
			"fragmentEntryLinks");

		Assert.assertEquals(
			expectedFragmentEntryLinks, fragmentEntryLinksJSONObject.length());

		JSONArray removedLayoutStructureItemsJSONArray =
			jsonObject.getJSONArray("removedItemIds");

		Assert.assertEquals(
			expectedRemovedLayoutStructureItems,
			removedLayoutStructureItemsJSONArray.length());

		Assert.assertEquals(
			expectedErrorMessage, jsonObject.getString("errorMessage"));
	}

	private String _getExpectedRendererKey(InfoField infoField) {
		InfoFieldType infoFieldType = infoField.getInfoFieldType();

		if (infoFieldType instanceof BooleanInfoFieldType) {
			return "INPUTS-checkbox";
		}

		if (infoFieldType instanceof DateInfoFieldType) {
			return "INPUTS-date-input";
		}

		if (infoFieldType instanceof FileInfoFieldType) {
			return "INPUTS-file-upload";
		}

		if (infoFieldType instanceof LongTextInfoFieldType) {
			return "INPUTS-textarea";
		}

		if (infoFieldType instanceof MultiselectInfoFieldType) {
			return "INPUTS-multiselect-list";
		}

		if (infoFieldType instanceof NumberInfoFieldType) {
			return "INPUTS-numeric-input";
		}

		if (infoFieldType instanceof RelationshipInfoFieldType ||
			infoFieldType instanceof SelectInfoFieldType) {

			return "INPUTS-select-from-list";
		}

		if (infoFieldType instanceof TextInfoFieldType) {
			return "INPUTS-text-input";
		}

		return null;
	}

	private MockLiferayPortletActionRequest _getMockLiferayPortletActionRequest(
			String itemConfig, String formItemId, Layout layout)
		throws Exception {

		MockLiferayPortletActionRequest mockLiferayPortletActionRequest =
			ContentLayoutTestUtil.getMockLiferayPortletActionRequest(
				_company, _group, layout);

		mockLiferayPortletActionRequest.addParameter("itemConfig", itemConfig);
		mockLiferayPortletActionRequest.addParameter("itemId", formItemId);
		mockLiferayPortletActionRequest.addParameter(
			"segmentsExperienceId", String.valueOf(_segmentsExperienceId));

		mockLiferayPortletActionRequest.setAttribute(
			PortletServlet.PORTLET_SERVLET_REQUEST,
			_serviceContext.getRequest());

		return mockLiferayPortletActionRequest;
	}

	private MockLiferayPortletActionRequest _getMockLiferayPortletActionRequest(
			String uniqueInfoFieldIds, String itemConfig, String formItemId,
			Layout layout)
		throws Exception {

		MockLiferayPortletActionRequest mockLiferayPortletActionRequest =
			_getMockLiferayPortletActionRequest(itemConfig, formItemId, layout);

		mockLiferayPortletActionRequest.addParameter(
			"fields", uniqueInfoFieldIds);

		return mockLiferayPortletActionRequest;
	}

	private void _prepareServiceContext() throws Exception {
		_serviceContext = ServiceContextTestUtil.getServiceContext(
			_group.getGroupId(), TestPropsValues.getUserId());

		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest();

		mockHttpServletRequest.setAttribute(WebKeys.LAYOUT, _draftLayout);

		ThemeDisplay themeDisplay = ContentLayoutTestUtil.getThemeDisplay(
			_company, _group, _draftLayout);

		themeDisplay.setRequest(mockHttpServletRequest);

		mockHttpServletRequest.setAttribute(
			WebKeys.THEME_DISPLAY, themeDisplay);

		mockHttpServletRequest.setRequestURI(
			StringPool.SLASH + RandomTestUtil.randomString());

		_serviceContext.setRequest(mockHttpServletRequest);

		ServiceContextThreadLocal.pushServiceContext(_serviceContext);
	}

	private String _readLayoutData() throws Exception {
		return StringUtil.read(
			UpdateFormItemConfigMVCActionCommandTest.class.getResourceAsStream(
				"dependencies/layout_data_with_form.json"));
	}

	private static final String _BUNDLE_SYMBOLIC_NAME =
		"com.liferay.fragment.collection.contributor.inputs";

	private static final String _COMPONENT_CLASS_NAME =
		"com.liferay.fragment.collection.contributor.inputs." +
			"InputsFragmentCollectionContributor";

	private static final InfoField<?>[] _INFO_FIELDS = new InfoField<?>[] {
		_getInfoField(BooleanInfoFieldType.INSTANCE),
		_getInfoField(DateInfoFieldType.INSTANCE),
		_getInfoField(FileInfoFieldType.INSTANCE),
		_getInfoField(NumberInfoFieldType.INSTANCE),
		_getInfoField(RelationshipInfoFieldType.INSTANCE),
		_getInfoField(SelectInfoFieldType.INSTANCE),
		_getInfoField(TextInfoFieldType.INSTANCE)
	};

	private Company _company;

	@Inject
	private CompanyLocalService _companyLocalService;

	private Layout _draftLayout;

	@Inject(
		filter = "info.item.capability.key=" + EditPageInfoItemCapability.KEY
	)
	private InfoItemCapability _editPageInfoItemCapability;

	@Inject
	private FragmentCollectionContributorRegistry
		_fragmentCollectionContributorRegistry;

	@Inject
	private FragmentEntryConfigurationParser _fragmentEntryConfigurationParser;

	@Inject
	private FragmentEntryLinkLocalService _fragmentEntryLinkLocalService;

	@DeleteAfterTestRun
	private Group _group;

	@Inject
	private Language _language;

	@Inject
	private LayoutPageTemplateStructureLocalService
		_layoutPageTemplateStructureLocalService;

	@Inject
	private LayoutStructureProvider _layoutStructureProvider;

	@Inject(
		filter = "mvc.command.name=/layout_content_page_editor/update_form_item_config"
	)
	private MVCActionCommand _mvcActionCommand;

	@Inject
	private Portal _portal;

	private long _segmentsExperienceId;

	@Inject
	private SegmentsExperienceLocalService _segmentsExperienceLocalService;

	@Inject
	private ServiceComponentRuntime _serviceComponentRuntime;

	private ServiceContext _serviceContext;

	private class ComponentEnablerTemporarySwapper implements AutoCloseable {

		public ComponentEnablerTemporarySwapper(
				String bundleSymbolicName, String componentClassName,
				boolean enabled)
			throws Exception {

			BundleContext bundleContext = SystemBundleUtil.getBundleContext();

			ComponentDescriptionDTO componentDescriptionDTO = null;

			for (Bundle bundle : bundleContext.getBundles()) {
				String symbolicName = bundle.getSymbolicName();

				if (symbolicName.startsWith(bundleSymbolicName)) {
					componentDescriptionDTO =
						_serviceComponentRuntime.getComponentDescriptionDTO(
							bundle, componentClassName);

					break;
				}
			}

			Assert.assertNotNull(componentDescriptionDTO);

			_componentDescriptionDTO = componentDescriptionDTO;

			_componentEnabled = _serviceComponentRuntime.isComponentEnabled(
				_componentDescriptionDTO);

			if (enabled) {
				Promise<?> promise = _serviceComponentRuntime.enableComponent(
					_componentDescriptionDTO);

				promise.getValue();
			}
			else {
				Promise<?> promise = _serviceComponentRuntime.disableComponent(
					_componentDescriptionDTO);

				promise.getValue();
			}
		}

		@Override
		public void close() throws Exception {
			if (_componentDescriptionDTO == null) {
				return;
			}

			if (_componentEnabled) {
				Promise<?> promise = _serviceComponentRuntime.enableComponent(
					_componentDescriptionDTO);

				promise.getValue();
			}
			else {
				Promise<?> promise = _serviceComponentRuntime.disableComponent(
					_componentDescriptionDTO);

				promise.getValue();
			}
		}

		private final ComponentDescriptionDTO _componentDescriptionDTO;
		private final boolean _componentEnabled;

	}

}