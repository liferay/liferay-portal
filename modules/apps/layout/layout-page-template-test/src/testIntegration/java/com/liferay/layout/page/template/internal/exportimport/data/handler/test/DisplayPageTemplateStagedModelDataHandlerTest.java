/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.page.template.internal.exportimport.data.handler.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.dynamic.data.mapping.model.DDMStructure;
import com.liferay.dynamic.data.mapping.service.DDMStructureLocalService;
import com.liferay.exportimport.kernel.lar.StagedModelDataHandlerUtil;
import com.liferay.exportimport.kernel.service.StagingLocalService;
import com.liferay.exportimport.test.util.lar.BaseStagedModelDataHandlerTestCase;
import com.liferay.fragment.contributor.FragmentCollectionContributorRegistry;
import com.liferay.fragment.entry.processor.constants.FragmentEntryProcessorConstants;
import com.liferay.fragment.model.FragmentEntry;
import com.liferay.fragment.model.FragmentEntryLink;
import com.liferay.fragment.service.FragmentEntryLinkLocalService;
import com.liferay.layout.page.template.model.LayoutPageTemplateEntry;
import com.liferay.layout.page.template.service.LayoutPageTemplateEntryLocalService;
import com.liferay.layout.page.template.test.util.DisplayPageTemplateTestUtil;
import com.liferay.layout.test.util.ContentLayoutTestUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.StagedModel;
import com.liferay.portal.kernel.service.GroupLocalServiceUtil;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.DateTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.segments.service.SegmentsExperienceLocalService;

import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Kyle Miho
 */
@RunWith(Arquillian.class)
public class DisplayPageTemplateStagedModelDataHandlerTest
	extends BaseStagedModelDataHandlerTestCase {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Before
	@Override
	public void setUp() throws Exception, PortalException {
		super.setUp();

		_classNameId = PortalUtil.getClassNameId(
			"com.liferay.journal.model.JournalArticle");

		Group group = GroupLocalServiceUtil.getCompanyGroup(
			TestPropsValues.getCompanyId());

		DDMStructure ddmStructure = _ddmStructureLocalService.getStructure(
			group.getGroupId(), _classNameId, "BASIC-WEB-CONTENT");

		_classTypeId = ddmStructure.getStructureId();
	}

	@Test
	public void testPropagateChangesOfFragmentEntryToLiveGroup()
		throws Exception {

		initExport();

		LayoutPageTemplateEntry layoutPageTemplateEntry =
			DisplayPageTemplateTestUtil.addDisplayPageTemplate(
				stagingGroup.getGroupId(), _classNameId, _classTypeId, true,
				WorkflowConstants.STATUS_APPROVED);

		Layout layout = _layoutLocalService.fetchLayout(
			layoutPageTemplateEntry.getPlid());

		Layout draftLayout = layout.fetchDraftLayout();

		FragmentEntry fragmentEntry =
			_fragmentCollectionContributorRegistry.getFragmentEntry(
				"BASIC_COMPONENT-heading");

		Locale locale = _portal.getSiteDefaultLocale(stagingGroup);

		String languageId = LocaleUtil.toLanguageId(locale);

		String originalText = RandomTestUtil.randomString();

		FragmentEntryLink fragmentEntryLink =
			ContentLayoutTestUtil.addFragmentEntryLinkToLayout(
				JSONUtil.put(
					FragmentEntryProcessorConstants.
						KEY_EDITABLE_FRAGMENT_ENTRY_PROCESSOR,
					JSONUtil.put(
						"element-text", JSONUtil.put(languageId, originalText))
				).toString(),
				fragmentEntry.getCss(), fragmentEntry.getConfiguration(),
				fragmentEntry.getFragmentEntryId(), fragmentEntry.getHtml(),
				fragmentEntry.getJs(), draftLayout,
				fragmentEntry.getFragmentEntryKey(), fragmentEntry.getType(),
				null, 0,
				_segmentsExperienceLocalService.
					fetchDefaultSegmentsExperienceId(draftLayout.getPlid()));

		ContentLayoutTestUtil.publishLayout(draftLayout, layout);

		StagedModelDataHandlerUtil.exportStagedModel(
			portletDataContext, layoutPageTemplateEntry);

		initImport();

		StagedModelDataHandlerUtil.importStagedModel(
			portletDataContext,
			(LayoutPageTemplateEntry)readExportedStagedModel(
				layoutPageTemplateEntry));

		initExport();

		FragmentEntryLink importedFragmentEntryLink =
			_fragmentEntryLinkLocalService.
				fetchFragmentEntryLinkByUuidAndGroupId(
					fragmentEntryLink.getUuid(), liveGroup.getGroupId());

		Assert.assertEquals(
			originalText,
			_getEditableValue(importedFragmentEntryLink, languageId));

		String updatedText = RandomTestUtil.randomString();

		_fragmentEntryLinkLocalService.updateFragmentEntryLink(
			TestPropsValues.getUserId(),
			fragmentEntryLink.getFragmentEntryLinkId(),
			JSONUtil.put(
				FragmentEntryProcessorConstants.
					KEY_EDITABLE_FRAGMENT_ENTRY_PROCESSOR,
				JSONUtil.put(
					"element-text", JSONUtil.put(languageId, updatedText))
			).toString());

		importedFragmentEntryLink =
			_fragmentEntryLinkLocalService.
				fetchFragmentEntryLinkByUuidAndGroupId(
					fragmentEntryLink.getUuid(), liveGroup.getGroupId());

		Assert.assertEquals(
			originalText,
			_getEditableValue(importedFragmentEntryLink, languageId));

		StagedModelDataHandlerUtil.exportStagedModel(
			portletDataContext, layoutPageTemplateEntry);

		initImport();

		StagedModelDataHandlerUtil.importStagedModel(
			portletDataContext,
			(LayoutPageTemplateEntry)readExportedStagedModel(
				layoutPageTemplateEntry));

		importedFragmentEntryLink =
			_fragmentEntryLinkLocalService.
				fetchFragmentEntryLinkByUuidAndGroupId(
					fragmentEntryLink.getUuid(), liveGroup.getGroupId());

		Assert.assertEquals(
			updatedText,
			_getEditableValue(importedFragmentEntryLink, languageId));

		_stagingLocalService.disableStaging(
			liveGroup,
			ServiceContextTestUtil.getServiceContext(liveGroup.getGroupId()));

		importedFragmentEntryLink =
			_fragmentEntryLinkLocalService.
				fetchFragmentEntryLinkByUuidAndGroupId(
					fragmentEntryLink.getUuid(), liveGroup.getGroupId());

		Assert.assertEquals(
			updatedText,
			_getEditableValue(importedFragmentEntryLink, languageId));
	}

	@Override
	protected StagedModel addStagedModel(
			Group group,
			Map<String, List<StagedModel>> dependentStagedModelsMap)
		throws Exception {

		return DisplayPageTemplateTestUtil.addDisplayPageTemplate(
			group.getGroupId(), _classNameId, _classTypeId, false,
			WorkflowConstants.STATUS_APPROVED);
	}

	@Override
	protected StagedModel getStagedModel(String uuid, Group group) {
		return _layoutPageTemplateEntryLocalService.
			fetchLayoutPageTemplateEntryByUuidAndGroupId(
				uuid, group.getGroupId());
	}

	@Override
	protected Class<? extends StagedModel> getStagedModelClass() {
		return LayoutPageTemplateEntry.class;
	}

	@Override
	protected void validateImportedStagedModel(
			StagedModel stagedModel, StagedModel importedStagedModel)
		throws Exception {

		DateTestUtil.assertEquals(
			stagedModel.getCreateDate(), importedStagedModel.getCreateDate());

		Assert.assertEquals(
			stagedModel.getUuid(), importedStagedModel.getUuid());

		LayoutPageTemplateEntry displayPageTemplate =
			(LayoutPageTemplateEntry)stagedModel;
		LayoutPageTemplateEntry importDisplayPageTemplate =
			(LayoutPageTemplateEntry)importedStagedModel;

		Assert.assertEquals(
			displayPageTemplate.getClassNameId(),
			importDisplayPageTemplate.getClassNameId());
		Assert.assertEquals(
			displayPageTemplate.getClassTypeId(),
			importDisplayPageTemplate.getClassTypeId());
		Assert.assertEquals(
			displayPageTemplate.getName(), importDisplayPageTemplate.getName());
		Assert.assertEquals(
			displayPageTemplate.getType(), importDisplayPageTemplate.getType());
	}

	private String _getEditableValue(
			FragmentEntryLink fragmentEntryLink, String languageId)
		throws Exception {

		JSONObject jsonObject = fragmentEntryLink.getEditableValuesJSONObject();

		JSONObject editableJSONObject = jsonObject.getJSONObject(
			FragmentEntryProcessorConstants.
				KEY_EDITABLE_FRAGMENT_ENTRY_PROCESSOR);

		Assert.assertNotNull(editableJSONObject);

		JSONObject textJSONObject = editableJSONObject.getJSONObject(
			"element-text");

		return textJSONObject.getString(languageId);
	}

	private long _classNameId;
	private long _classTypeId;

	@Inject
	private DDMStructureLocalService _ddmStructureLocalService;

	@Inject
	private FragmentCollectionContributorRegistry
		_fragmentCollectionContributorRegistry;

	@Inject
	private FragmentEntryLinkLocalService _fragmentEntryLinkLocalService;

	@Inject
	private LayoutLocalService _layoutLocalService;

	@Inject
	private LayoutPageTemplateEntryLocalService
		_layoutPageTemplateEntryLocalService;

	@Inject
	private Portal _portal;

	@Inject
	private SegmentsExperienceLocalService _segmentsExperienceLocalService;

	@Inject
	private StagingLocalService _stagingLocalService;

}