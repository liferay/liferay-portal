/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.translation.web.internal.portlet.action.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.dynamic.data.mapping.model.Value;
import com.liferay.dynamic.data.mapping.storage.DDMFormFieldValue;
import com.liferay.dynamic.data.mapping.storage.DDMFormValues;
import com.liferay.fragment.constants.FragmentConstants;
import com.liferay.fragment.model.FragmentCollection;
import com.liferay.fragment.model.FragmentEntry;
import com.liferay.fragment.model.FragmentEntryLink;
import com.liferay.fragment.service.FragmentCollectionService;
import com.liferay.fragment.service.FragmentEntryLinkLocalService;
import com.liferay.fragment.service.FragmentEntryService;
import com.liferay.journal.constants.JournalFolderConstants;
import com.liferay.journal.model.JournalArticle;
import com.liferay.journal.service.JournalArticleLocalService;
import com.liferay.journal.test.util.JournalTestUtil;
import com.liferay.layout.test.util.ContentLayoutTestUtil;
import com.liferay.layout.test.util.LayoutTestUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.service.ClassNameLocalService;
import com.liferay.portal.kernel.service.CompanyLocalServiceUtil;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.portlet.MockActionResponse;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletActionRequest;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.rule.Sync;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.URLCodec;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.segments.service.SegmentsExperienceLocalService;
import com.liferay.translation.constants.TranslationPortletKeys;
import com.liferay.translation.model.TranslationEntry;
import com.liferay.translation.service.TranslationEntryLocalService;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Mikel Lorza
 */
@RunWith(Arquillian.class)
@Sync
public class UpdateTranslationMVCActionCommandTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();
	}

	@Test
	public void testDoProcessAction() throws Exception {
		JournalArticle journalArticle = _addArticle();

		TranslationEntry translationEntry =
			_translationEntryLocalService.fetchTranslationEntry(
				JournalArticle.class.getName(),
				journalArticle.getResourcePrimKey(),
				LocaleUtil.SPAIN.toString());

		Assert.assertNull(translationEntry);

		_mvcActionCommand.processAction(
			_getMockActionRequest(
				_classNameLocalService.getClassNameId(JournalArticle.class),
				journalArticle.getResourcePrimKey(),
				HashMapBuilder.put(
					"infoField--DDMStructure_name--", "name spanish"
				).put(
					"infoField--JournalArticle_description--",
					"description spanish"
				).put(
					"infoField--JournalArticle_title--", "title spanish"
				).build(),
				journalArticle.getGroupId()),
			new MockActionResponse());

		journalArticle = _journalArticleLocalService.fetchLatestArticle(
			journalArticle.getResourcePrimKey());

		Assert.assertNotNull(journalArticle);
		Assert.assertEquals(
			"title spanish", journalArticle.getTitle(LocaleUtil.SPAIN));
		Assert.assertEquals(
			"description spanish",
			journalArticle.getDescription(LocaleUtil.SPAIN));

		DDMFormValues ddmFormValues = journalArticle.getDDMFormValues();

		List<DDMFormFieldValue> ddmFormFieldValues =
			ddmFormValues.getDDMFormFieldValues();

		Assert.assertEquals(
			ddmFormFieldValues.toString(), 1, ddmFormFieldValues.size());

		DDMFormFieldValue ddmFormFieldValue = ddmFormFieldValues.get(0);

		Assert.assertNotNull(ddmFormFieldValue);

		Value value = ddmFormFieldValue.getValue();

		Assert.assertNotNull(value);
		Assert.assertEquals("name spanish", value.getString(LocaleUtil.SPAIN));

		translationEntry = _translationEntryLocalService.fetchTranslationEntry(
			JournalArticle.class.getName(), journalArticle.getResourcePrimKey(),
			LocaleUtil.SPAIN.toString());

		try {
			Assert.assertNotNull(translationEntry);
		}
		finally {
			if (translationEntry != null) {
				_translationEntryLocalService.deleteTranslationEntry(
					translationEntry);
			}
		}
	}

	@Test
	public void testDoProcessActionWithConcurrentUsers() throws Exception {
		JournalArticle expectedJournalArticle = _addArticle();

		MockActionRequest mockActionRequest = _getMockActionRequest(
			_classNameLocalService.getClassNameId(JournalArticle.class),
			expectedJournalArticle.getResourcePrimKey(),
			HashMapBuilder.put(
				"infoField--DDMStructure_name--", "name spanish"
			).put(
				"infoField--JournalArticle_description--", "description spanish"
			).put(
				"infoField--JournalArticle_title--", "title spanish"
			).build(),
			expectedJournalArticle.getGroupId());

		Date modifiedDate = expectedJournalArticle.getModifiedDate();

		mockActionRequest.setParameter(
			"modifiedDateTime", String.valueOf(modifiedDate.getTime() - 1));

		MockActionResponse mockActionResponse = new MockActionResponse();

		_mvcActionCommand.processAction(mockActionRequest, mockActionResponse);

		Assert.assertTrue(
			SessionErrors.contains(mockActionRequest, "duplicateChanges"));

		String redirectURL = mockActionResponse.getRedirectedUrl();

		Assert.assertNotNull(redirectURL);
		Assert.assertTrue(
			redirectURL.contains(
				"classNameId=" + _portal.getClassNameId(JournalArticle.class)));
		Assert.assertTrue(
			redirectURL.contains(
				"classPK=" + expectedJournalArticle.getResourcePrimKey()));
		Assert.assertTrue(
			redirectURL.contains(
				"DDMStructure_name=" + URLCodec.encodeURL("name spanish")));
		Assert.assertTrue(
			redirectURL.contains(
				"JournalArticle_description=" +
					URLCodec.encodeURL("description spanish")));
		Assert.assertTrue(
			redirectURL.contains(
				"JournalArticle_title=" + URLCodec.encodeURL("title spanish")));

		JournalArticle actualJournalArticle =
			_journalArticleLocalService.fetchLatestArticle(
				expectedJournalArticle.getResourcePrimKey());

		Assert.assertNotNull(actualJournalArticle);
		Assert.assertEquals(
			expectedJournalArticle.getArticleId(),
			actualJournalArticle.getArticleId());

		Assert.assertNull(
			_translationEntryLocalService.fetchTranslationEntry(
				JournalArticle.class.getName(),
				expectedJournalArticle.getResourcePrimKey(),
				LocaleUtil.SPAIN.toString()));
	}

	@Test
	@TestInfo("LPD-87379")
	public void testDoProcessActionWithLayout() throws Exception {
		Layout layout = LayoutTestUtil.addTypeContentLayout(_group);

		ContentLayoutTestUtil.publishLayout(layout.fetchDraftLayout(), layout);

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(
				TestPropsValues.getGroupId(), TestPropsValues.getUserId());

		FragmentCollection fragmentCollection =
			_fragmentCollectionService.addFragmentCollection(
				null, _group.getGroupId(), RandomTestUtil.randomString(),
				StringPool.BLANK, serviceContext);

		FragmentEntry fragmentEntry = _fragmentEntryService.addFragmentEntry(
			null, _group.getGroupId(),
			fragmentCollection.getFragmentCollectionId(), null,
			RandomTestUtil.randomString(), null, _read("index.html"), null,
			false, _read("configuration.json"), null, 0, false, false,
			FragmentConstants.TYPE_COMPONENT, null,
			WorkflowConstants.STATUS_APPROVED, serviceContext);

		long segmentExperienceId =
			_segmentsExperienceLocalService.fetchDefaultSegmentsExperienceId(
				layout.getPlid());

		ContentLayoutTestUtil.addFragmentEntryLinkToLayout(
			JSONUtil.put(
				"com.liferay.fragment.entry.processor.editable." +
					"EditableFragmentEntryProcessor",
				JSONUtil.put(
					"paragraph",
					JSONUtil.put(
						"defaultValue", RandomTestUtil.randomString()
					).put(
						"en_US", RandomTestUtil.randomString()
					))
			).put(
				"com.liferay.fragment.entry.processor.freemarker." +
					"FreeMarkerFragmentEntryProcessor",
				JSONUtil.put("paragraph", true)
			).toString(),
			fragmentEntry.getCss(), fragmentEntry.getConfiguration(),
			fragmentEntry.getExternalReferenceCode(), null,
			fragmentEntry.getHtml(), fragmentEntry.getJs(), layout, null,
			FragmentConstants.TYPE_COMPONENT, null, 0, segmentExperienceId);

		List<FragmentEntryLink> fragmentEntryLinks =
			_fragmentEntryLinkLocalService.getFragmentEntryLinksByPlid(
				layout.getGroupId(), layout.getPlid());

		FragmentEntryLink fragmentEntryLink = fragmentEntryLinks.get(0);

		_mvcActionCommand.processAction(
			_getMockActionRequest(
				_classNameLocalService.getClassNameId(Layout.class),
				layout.getPlid(),
				HashMapBuilder.put(
					"infoField--Layout_name--", "name spanish"
				).put(
					"infoField--FragmentEntryLink_" +
						fragmentEntryLink.getFragmentEntryLinkId() +
							":paragraph--",
					"<p>paragraph spanish</p>"
				).put(
					"segmentsExperienceId", String.valueOf(segmentExperienceId)
				).build(),
				layout.getGroupId()),
			new MockActionResponse());

		layout = _layoutLocalService.fetchLayout(layout.getPlid());

		Assert.assertEquals("name spanish", layout.getName(LocaleUtil.SPAIN));

		fragmentEntryLinks =
			_fragmentEntryLinkLocalService.getFragmentEntryLinksByPlid(
				layout.getGroupId(), layout.getPlid());

		fragmentEntryLink = fragmentEntryLinks.get(0);

		JSONObject editableValueJSONObject = JSONFactoryUtil.createJSONObject(
			fragmentEntryLink.getEditableValues());

		JSONObject editableFragmentJSONObject =
			editableValueJSONObject.getJSONObject(
				"com.liferay.fragment.entry.processor.editable." +
					"EditableFragmentEntryProcessor");

		JSONObject paragraphJSONObject =
			editableFragmentJSONObject.getJSONObject("paragraph");

		String spanishValue = paragraphJSONObject.getString(
			LocaleUtil.SPAIN.toString());

		Assert.assertEquals("<p>paragraph spanish</p>", spanishValue);
	}

	private JournalArticle _addArticle() throws Exception {
		JournalArticle journalArticle = JournalTestUtil.addArticle(
			_group.getGroupId(),
			JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID);

		journalArticle.setStatus(WorkflowConstants.STATUS_APPROVED);

		return JournalTestUtil.updateArticle(journalArticle);
	}

	private MockActionRequest _getMockActionRequest(
			long classNameId, long classPK, Map<String, String> fields,
			long groupId)
		throws Exception {

		MockActionRequest mockActionRequest = new MockActionRequest();

		mockActionRequest.setAttribute(
			WebKeys.THEME_DISPLAY, _getThemeDisplay());

		mockActionRequest.setParameter(
			"classNameId", String.valueOf(classNameId));
		mockActionRequest.setParameter("classPK", String.valueOf(classPK));
		mockActionRequest.setParameter("groupId", String.valueOf(groupId));
		mockActionRequest.setParameter(
			"redirect", "http://localhost:8080/content-list");
		mockActionRequest.setParameter(
			"sourceLanguageId", LocaleUtil.US.toString());
		mockActionRequest.setParameter(
			"targetLanguageId", LocaleUtil.SPAIN.toString());

		for (Map.Entry<String, String> field : fields.entrySet()) {
			mockActionRequest.setParameter(field.getKey(), field.getValue());
		}

		return mockActionRequest;
	}

	private ThemeDisplay _getThemeDisplay() throws Exception {
		ThemeDisplay themeDisplay = new ThemeDisplay();

		themeDisplay.setCompany(
			CompanyLocalServiceUtil.getCompany(TestPropsValues.getCompanyId()));
		themeDisplay.setScopeGroupId(_group.getGroupId());
		themeDisplay.setSiteGroupId(_group.getGroupId());

		Layout layout = LayoutTestUtil.addTypeContentLayout(_group);

		themeDisplay.setLayout(layout);
		themeDisplay.setLayoutSet(layout.getLayoutSet());

		themeDisplay.setLocale(LocaleUtil.US);
		themeDisplay.setPpid(TranslationPortletKeys.TRANSLATION);
		themeDisplay.setUser(TestPropsValues.getUser());

		return themeDisplay;
	}

	private String _read(String fileName) throws Exception {
		Class<?> clazz = getClass();

		return StringUtil.read(
			clazz.getResourceAsStream("dependencies/" + fileName));
	}

	@Inject
	private ClassNameLocalService _classNameLocalService;

	@Inject
	private FragmentCollectionService _fragmentCollectionService;

	@Inject
	private FragmentEntryLinkLocalService _fragmentEntryLinkLocalService;

	@Inject
	private FragmentEntryService _fragmentEntryService;

	@DeleteAfterTestRun
	private Group _group;

	@Inject
	private JournalArticleLocalService _journalArticleLocalService;

	@Inject
	private LayoutLocalService _layoutLocalService;

	@Inject(filter = "mvc.command.name=/translation/update_translation")
	private MVCActionCommand _mvcActionCommand;

	@Inject
	private Portal _portal;

	@Inject
	private SegmentsExperienceLocalService _segmentsExperienceLocalService;

	@Inject
	private TranslationEntryLocalService _translationEntryLocalService;

	private static class MockActionRequest
		extends MockLiferayPortletActionRequest {

		@Override
		public String[] getParameterValues(String name) {
			Map<String, String[]> parameters = getParameterMap();

			return parameters.get(name);
		}

	}

}