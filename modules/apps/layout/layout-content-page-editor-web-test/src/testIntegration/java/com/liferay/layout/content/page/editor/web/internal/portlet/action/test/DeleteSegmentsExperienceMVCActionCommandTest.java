/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.content.page.editor.web.internal.portlet.action.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.fragment.model.FragmentEntryLink;
import com.liferay.fragment.service.FragmentEntryLinkLocalService;
import com.liferay.journal.constants.JournalContentPortletKeys;
import com.liferay.journal.constants.JournalFolderConstants;
import com.liferay.journal.model.JournalArticle;
import com.liferay.journal.test.util.JournalTestUtil;
import com.liferay.layout.content.page.editor.constants.ContentPageEditorPortletKeys;
import com.liferay.layout.test.util.ContentLayoutTestUtil;
import com.liferay.layout.test.util.LayoutTestUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.portlet.PortletIdCodec;
import com.liferay.portal.kernel.portlet.PortletPreferencesFactory;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.PortletPreferencesLocalService;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.portlet.MockActionRequest;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletActionResponse;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import jakarta.portlet.PortletPreferences;

import java.util.List;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.mock.web.MockHttpServletResponse;

/**
 * @author Eudaldo Alonso
 */
@RunWith(Arquillian.class)
public class DeleteSegmentsExperienceMVCActionCommandTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();

		ServiceContextThreadLocal.pushServiceContext(
			ServiceContextTestUtil.getServiceContext(_group.getGroupId()));
	}

	@After
	public void tearDown() {
		ServiceContextThreadLocal.popServiceContext();
	}

	@Test
	@TestInfo({"LPD-50133", "LPS-187444"})
	public void testProcessAction() throws Exception {
		Layout layout = LayoutTestUtil.addTypeContentLayout(_group);

		Layout draftLayout = layout.fetchDraftLayout();

		String portletId = _addJournalContentPortletToLayout(draftLayout);

		PortletPreferences portletPreferences =
			_portletPreferencesFactory.getPortletSetup(
				draftLayout, portletId, null);

		JournalArticle journalArticle = JournalTestUtil.addArticle(
			_group.getGroupId(),
			JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID);

		portletPreferences.setValue(
			"articleExternalReferenceCode",
			journalArticle.getExternalReferenceCode());

		portletPreferences.setValue(
			"groupExternalReferenceCode", _group.getExternalReferenceCode());

		portletPreferences.store();

		long segmentsExperienceId = _addSegmentsExperience(draftLayout);

		List<FragmentEntryLink> fragmentEntryLinks =
			_fragmentEntryLinkLocalService.
				getFragmentEntryLinksBySegmentsExperienceId(
					draftLayout.getGroupId(), segmentsExperienceId,
					draftLayout.getPlid());

		Assert.assertEquals(
			fragmentEntryLinks.toString(), 1, fragmentEntryLinks.size());

		FragmentEntryLink fragmentEntryLink = fragmentEntryLinks.get(0);

		JSONObject editableValuesJSONObject = _jsonFactory.createJSONObject(
			fragmentEntryLink.getEditableValues());

		String segmentsExperiencePortletId = PortletIdCodec.encode(
			editableValuesJSONObject.getString("portletId"),
			editableValuesJSONObject.getString("instanceId"));

		Assert.assertNotEquals(portletId, segmentsExperiencePortletId);

		portletPreferences = _portletPreferencesFactory.getPortletSetup(
			draftLayout, segmentsExperiencePortletId, null);

		Assert.assertEquals(
			journalArticle.getExternalReferenceCode(),
			portletPreferences.getValue(
				"articleExternalReferenceCode", StringPool.BLANK));

		List<com.liferay.portal.kernel.model.PortletPreferences>
			segmentsExperiencePortletPreferences =
				_portletPreferencesLocalService.
					getPortletPreferencesByPortletId(
						segmentsExperiencePortletId);

		Assert.assertEquals(
			segmentsExperiencePortletPreferences.toString(), 1,
			segmentsExperiencePortletPreferences.size());

		MockActionRequest mockActionRequest =
			ContentLayoutTestUtil.getMockLiferayPortletActionRequest(
				_companyLocalService.getCompany(TestPropsValues.getCompanyId()),
				_group, draftLayout);

		mockActionRequest.setParameter(
			"segmentsExperienceId", String.valueOf(segmentsExperienceId));

		_deleteSegmentsExperieceMVCActionCommand.processAction(
			mockActionRequest, new MockLiferayPortletActionResponse());

		portletPreferences = _portletPreferencesFactory.getPortletSetup(
			draftLayout, portletId, null);

		Assert.assertEquals(
			journalArticle.getExternalReferenceCode(),
			portletPreferences.getValue(
				"articleExternalReferenceCode", StringPool.BLANK));

		fragmentEntryLinks =
			_fragmentEntryLinkLocalService.
				getFragmentEntryLinksBySegmentsExperienceId(
					draftLayout.getGroupId(), segmentsExperienceId,
					draftLayout.getPlid());

		Assert.assertEquals(
			fragmentEntryLinks.toString(), 0, fragmentEntryLinks.size());

		segmentsExperiencePortletPreferences =
			_portletPreferencesLocalService.getPortletPreferencesByPortletId(
				segmentsExperiencePortletId);

		Assert.assertEquals(
			segmentsExperiencePortletPreferences.toString(), 0,
			segmentsExperiencePortletPreferences.size());
	}

	private String _addJournalContentPortletToLayout(Layout layout)
		throws Exception {

		JSONObject processAddPortletJSONObject =
			ContentLayoutTestUtil.addPortletToLayout(
				layout, JournalContentPortletKeys.JOURNAL_CONTENT);

		JSONObject fragmentEntryLinkJSONObject =
			processAddPortletJSONObject.getJSONObject("fragmentEntryLink");

		JSONObject editableValuesJSONObject =
			fragmentEntryLinkJSONObject.getJSONObject("editableValues");

		return PortletIdCodec.encode(
			editableValuesJSONObject.getString("portletId"),
			editableValuesJSONObject.getString("instanceId"));
	}

	private long _addSegmentsExperience(Layout draftLayout) throws Exception {
		MockActionRequest mockActionRequest =
			ContentLayoutTestUtil.getMockLiferayPortletActionRequest(
				_companyLocalService.getCompany(TestPropsValues.getCompanyId()),
				_group, draftLayout);

		mockActionRequest.setAttribute(
			WebKeys.PORTLET_ID,
			ContentPageEditorPortletKeys.CONTENT_PAGE_EDITOR_PORTLET);
		mockActionRequest.setParameter(
			"groupId", String.valueOf(_group.getGroupId()));
		mockActionRequest.setParameter("name", RandomTestUtil.randomString());
		mockActionRequest.setParameter(
			"plid", String.valueOf(draftLayout.getPlid()));

		MockLiferayPortletActionResponse mockLiferayPortletActionResponse =
			new MockLiferayPortletActionResponse();

		_addSegmentsExperienceMVCActionCommand.processAction(
			mockActionRequest, mockLiferayPortletActionResponse);

		MockHttpServletResponse mockHttpServletResponse =
			(MockHttpServletResponse)
				mockLiferayPortletActionResponse.getHttpServletResponse();

		JSONObject jsonObject = _jsonFactory.createJSONObject(
			mockHttpServletResponse.getContentAsString());

		JSONObject segmentsExperienceJSONObject = jsonObject.getJSONObject(
			"segmentsExperience");

		return segmentsExperienceJSONObject.getLong("segmentsExperienceId");
	}

	@Inject(
		filter = "mvc.command.name=/layout_content_page_editor/add_segments_experience"
	)
	private MVCActionCommand _addSegmentsExperienceMVCActionCommand;

	@Inject
	private CompanyLocalService _companyLocalService;

	@Inject(
		filter = "mvc.command.name=/layout_content_page_editor/delete_segments_experience"
	)
	private MVCActionCommand _deleteSegmentsExperieceMVCActionCommand;

	@Inject
	private FragmentEntryLinkLocalService _fragmentEntryLinkLocalService;

	@DeleteAfterTestRun
	private Group _group;

	@Inject
	private JSONFactory _jsonFactory;

	@Inject
	private PortletPreferencesFactory _portletPreferencesFactory;

	@Inject
	private PortletPreferencesLocalService _portletPreferencesLocalService;

}