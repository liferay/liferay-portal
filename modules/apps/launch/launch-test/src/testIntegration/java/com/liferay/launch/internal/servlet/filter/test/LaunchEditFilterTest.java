/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.launch.internal.servlet.filter.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.journal.constants.JournalFolderConstants;
import com.liferay.journal.model.JournalArticle;
import com.liferay.journal.service.JournalArticleLocalService;
import com.liferay.journal.test.util.JournalTestUtil;
import com.liferay.layout.test.util.LayoutTestUtil;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.launch.LaunchThreadLocal;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.preview.PreviewableResolverUtil;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;

import java.io.Serializable;

import java.util.Collections;
import java.util.Map;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

/**
 * @author Carlos Correa
 */
@FeatureFlag("LPD-72278")
@RunWith(Arquillian.class)
public class LaunchEditFilterTest {

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
			ServiceContextTestUtil.getServiceContext(
				_group.getGroupId(), TestPropsValues.getUserId()));

		_launchSetObjectEntry = _addObjectEntry(
			"LaunchSet",
			HashMapBuilder.<String, Serializable>put(
				"description", RandomTestUtil.randomString()
			).put(
				"name", RandomTestUtil.randomString()
			).build());

		_journalArticle = JournalTestUtil.addArticle(
			_group.getGroupId(),
			JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID);

		_launchDraftTitle = RandomTestUtil.randomString();

		_updateLaunchDraftJournalArticle(
			_addLaunchEntryDraftId(
				JournalArticle.class.getName(),
				_journalArticle.getResourcePrimKey(),
				String.valueOf(_journalArticle.getVersion())),
			_launchDraftTitle);

		_layout1 = LayoutTestUtil.addTypeContentPublishedLayout(
			_group, RandomTestUtil.randomString(),
			WorkflowConstants.STATUS_APPROVED);

		_launchDraftLayout1 = _layoutLocalService.getLayout(
			_addLaunchEntryDraftId(
				Layout.class.getName(), _layout1.getPlid(),
				String.valueOf(_layout1.getPlid())));

		_layout2 = LayoutTestUtil.addTypeContentPublishedLayout(
			_group, RandomTestUtil.randomString(),
			WorkflowConstants.STATUS_APPROVED);

		_launchDraftLayout2 = _layoutLocalService.getLayout(
			_addLaunchEntryDraftId(
				Layout.class.getName(), _layout2.getPlid(),
				String.valueOf(_layout2.getPlid())));
	}

	@After
	public void tearDown() throws Exception {
		_objectEntryLocalService.deleteObjectEntry(_launchSetObjectEntry);

		ServiceContextThreadLocal.popServiceContext();
	}

	@Test
	public void testProcessFilter() throws Exception {

		// Edit launch set ID header

		long launchSetId = _launchSetObjectEntry.getObjectEntryId();

		_assertProcessFilter(
			_launchDraftTitle, launchSetId, _launchDraftLayout1.getPlid(),
			_launchDraftLayout2.getPlid(),
			HashMapBuilder.put(
				"X-Liferay-Launch-Set-Id", String.valueOf(launchSetId)
			).build(),
			Collections.emptyMap(), null, TestPropsValues.getUser());

		// Edit launch set ID parameter

		_assertProcessFilter(
			_launchDraftTitle, launchSetId, _launchDraftLayout1.getPlid(),
			_launchDraftLayout2.getPlid(), Collections.emptyMap(),
			HashMapBuilder.put(
				"editLaunchSetId", String.valueOf(launchSetId)
			).build(),
			null, TestPropsValues.getUser());

		// Edit launch set ID parameter forwarded

		_assertProcessFilter(
			_launchDraftTitle, launchSetId, _launchDraftLayout1.getPlid(),
			_launchDraftLayout2.getPlid(), Collections.emptyMap(),
			HashMapBuilder.put(
				"editLaunchSetId", String.valueOf(launchSetId)
			).build(),
			Collections.emptyMap(), TestPropsValues.getUser());

		// Edit launch set ID parameter of a missing launch set

		String title = _journalArticle.getTitle(LocaleUtil.getSiteDefault());

		_assertProcessFilter(
			title, 0, _layout1.getPlid(), _layout2.getPlid(),
			Collections.emptyMap(),
			HashMapBuilder.put(
				"editLaunchSetId", String.valueOf(RandomTestUtil.randomLong())
			).build(),
			null, TestPropsValues.getUser());

		// Edit launch set ID parameter of an approved launch set

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(
				_group.getGroupId(), TestPropsValues.getUserId());

		_objectEntryLocalService.updateStatus(
			TestPropsValues.getUserId(), launchSetId,
			WorkflowConstants.STATUS_APPROVED, serviceContext);

		_assertProcessFilter(
			title, 0, _layout1.getPlid(), _layout2.getPlid(),
			Collections.emptyMap(),
			HashMapBuilder.put(
				"editLaunchSetId", String.valueOf(launchSetId)
			).build(),
			null, TestPropsValues.getUser());

		_objectEntryLocalService.updateStatus(
			TestPropsValues.getUserId(), launchSetId,
			WorkflowConstants.STATUS_DRAFT, serviceContext);

		// Edit launch set ID parameter with user without view permission

		_user = UserTestUtil.addUser();

		_assertProcessFilter(
			title, 0, _layout1.getPlid(), _layout2.getPlid(),
			Collections.emptyMap(),
			HashMapBuilder.put(
				"editLaunchSetId", String.valueOf(launchSetId)
			).build(),
			null, _user);

		// Launch preview

		boolean[] edit = {true};

		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest();

		mockHttpServletRequest.setAttribute(
			WebKeys.USER, TestPropsValues.getUser());
		mockHttpServletRequest.setParameter(
			"editLaunchSetId", String.valueOf(launchSetId));

		try (SafeCloseable safeCloseable =
				LaunchThreadLocal.setLaunchSetIdWithSafeCloseable(
					launchSetId, LaunchThreadLocal.Mode.PREVIEW)) {

			_filter.doFilter(
				mockHttpServletRequest, new MockHttpServletResponse(),
				(servletRequest, servletResponse) ->
					edit[0] = LaunchThreadLocal.isEdit());
		}

		Assert.assertFalse(edit[0]);

		// Portlet namespaced edit launch set ID parameter

		String portletId = RandomTestUtil.randomString();

		_assertProcessFilter(
			_launchDraftTitle, launchSetId, _launchDraftLayout1.getPlid(),
			_launchDraftLayout2.getPlid(), Collections.emptyMap(),
			HashMapBuilder.put(
				"_" + portletId + "_editLaunchSetId",
				String.valueOf(launchSetId)
			).put(
				"p_p_id", portletId
			).build(),
			null, TestPropsValues.getUser());
	}

	private long _addLaunchEntryDraftId(
			String className, long classPK, String baseVersionKey)
		throws Exception {

		ObjectEntry launchEntryObjectEntry = _addObjectEntry(
			"LaunchEntry",
			HashMapBuilder.<String, Serializable>put(
				"baseVersionKey", baseVersionKey
			).put(
				"className", className
			).put(
				"classPK", classPK
			).put(
				"r_launchSetToLaunchEntries_c_launchSetId",
				_launchSetObjectEntry.getObjectEntryId()
			).build());

		ObjectEntry reloadedObjectEntry =
			_objectEntryLocalService.getObjectEntry(
				launchEntryObjectEntry.getObjectEntryId());

		long draftId = MapUtil.getLong(
			reloadedObjectEntry.getValues(), "draftId");

		Assert.assertNotEquals(0, draftId);

		return draftId;
	}

	private ObjectEntry _addObjectEntry(
			String objectDefinitionName, Map<String, Serializable> values)
		throws Exception {

		ObjectDefinition objectDefinition =
			_objectDefinitionLocalService.getObjectDefinition(
				TestPropsValues.getCompanyId(), objectDefinitionName);

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(
				TestPropsValues.getGroupId(), TestPropsValues.getUserId());

		serviceContext.setWorkflowAction(WorkflowConstants.ACTION_SAVE_DRAFT);

		return _objectEntryLocalService.addObjectEntry(
			0, TestPropsValues.getUserId(),
			objectDefinition.getObjectDefinitionId(), 0,
			LocaleUtil.toLanguageId(LocaleUtil.getSiteDefault()), values,
			serviceContext);
	}

	private void _assertProcessFilter(
			String expectedJournalArticleTitle, long expectedLaunchSetId,
			long expectedLayout1Plid, long expectedLayout2Plid,
			Map<String, String> headers, Map<String, String> parameters,
			Map<String, String> forwardParameters, User user)
		throws Exception {

		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest();

		mockHttpServletRequest.setAttribute(WebKeys.USER, user);

		for (Map.Entry<String, String> entry : headers.entrySet()) {
			mockHttpServletRequest.addHeader(entry.getKey(), entry.getValue());
		}

		mockHttpServletRequest.setParameters(parameters);

		Object[] values = new Object[5];

		FilterChain filterChain = (servletRequest, servletResponse) -> {
			JournalArticle journalArticle =
				_journalArticleLocalService.fetchLatestArticle(
					_journalArticle.getResourcePrimKey());

			values[0] = journalArticle.getTitle(LocaleUtil.getSiteDefault());

			try {
				Layout layout1 = _layoutLocalService.getLayout(
					_layout1.getPlid());

				values[1] = layout1.getPlid();

				Layout layout2 = _layoutLocalService.getLayout(
					_layout2.getPlid());

				values[2] = layout2.getPlid();
			}
			catch (PortalException portalException) {
				throw new ServletException(portalException);
			}

			values[3] = LaunchThreadLocal.getLaunchSetId();
			values[4] = LaunchThreadLocal.getMode();
		};

		if (forwardParameters == null) {
			_filter.doFilter(
				mockHttpServletRequest, new MockHttpServletResponse(),
				filterChain);
		}
		else {
			_filter.doFilter(
				mockHttpServletRequest, new MockHttpServletResponse(),
				(servletRequest, servletResponse) -> {
					mockHttpServletRequest.addParameters(forwardParameters);

					_filter.doFilter(
						mockHttpServletRequest, servletResponse, filterChain);
				});
		}

		Assert.assertEquals(expectedJournalArticleTitle, values[0]);
		Assert.assertEquals(expectedLayout1Plid, values[1]);
		Assert.assertEquals(expectedLayout2Plid, values[2]);

		if (expectedLaunchSetId > 0) {
			Assert.assertEquals(expectedLaunchSetId, values[3]);
			Assert.assertEquals(LaunchThreadLocal.Mode.EDIT, values[4]);
		}
		else {
			Assert.assertEquals(-1L, values[3]);
			Assert.assertNull(values[4]);
		}

		Assert.assertNull(PreviewableResolverUtil.getPreviewId());
	}

	private void _updateLaunchDraftJournalArticle(long draftId, String title)
		throws Exception {

		JournalArticle launchDraftJournalArticle =
			_journalArticleLocalService.getLatestArticle(
				draftId, WorkflowConstants.STATUS_ANY, false);

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(
				_group.getGroupId(), TestPropsValues.getUserId());

		serviceContext.setWorkflowAction(WorkflowConstants.ACTION_SAVE_DRAFT);

		_journalArticleLocalService.updateArticle(
			TestPropsValues.getUserId(), launchDraftJournalArticle.getGroupId(),
			launchDraftJournalArticle.getFolderId(),
			launchDraftJournalArticle.getArticleId(),
			launchDraftJournalArticle.getVersion(),
			HashMapBuilder.put(
				LocaleUtil.getSiteDefault(), title
			).build(),
			launchDraftJournalArticle.getDescriptionMap(),
			launchDraftJournalArticle.getContent(),
			launchDraftJournalArticle.getLayoutUuid(), serviceContext);
	}

	@Inject(filter = "servlet-filter-name=Launch Edit Filter")
	private Filter _filter;

	@DeleteAfterTestRun
	private Group _group;

	private JournalArticle _journalArticle;

	@Inject
	private JournalArticleLocalService _journalArticleLocalService;

	private Layout _launchDraftLayout1;
	private Layout _launchDraftLayout2;
	private String _launchDraftTitle;
	private ObjectEntry _launchSetObjectEntry;
	private Layout _layout1;
	private Layout _layout2;

	@Inject
	private LayoutLocalService _layoutLocalService;

	@Inject
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	@Inject
	private ObjectEntryLocalService _objectEntryLocalService;

	@DeleteAfterTestRun
	private User _user;

}