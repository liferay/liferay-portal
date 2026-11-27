/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.launch.internal.servlet.filter.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.layout.test.util.LayoutTestUtil;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.launch.LaunchSetMode;
import com.liferay.portal.kernel.launch.LaunchSetThreadLocal;
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
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.HttpComponentsUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.Portal;
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
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

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
public class LaunchSetFilterTest {

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

		_layout1 = LayoutTestUtil.addTypeContentPublishedLayout(
			_group, RandomTestUtil.randomString(),
			WorkflowConstants.STATUS_APPROVED);

		_launchDraftLayout1 = _layoutLocalService.getLayout(
			_addLaunchEntryDraftId(
				String.valueOf(_layout1.getPlid()), Layout.class.getName(),
				_layout1.getPlid()));

		_layout2 = LayoutTestUtil.addTypeContentPublishedLayout(
			_group, RandomTestUtil.randomString(),
			WorkflowConstants.STATUS_APPROVED);

		_launchDraftLayout2 = _layoutLocalService.getLayout(
			_addLaunchEntryDraftId(
				String.valueOf(_layout2.getPlid()), Layout.class.getName(),
				_layout2.getPlid()));
	}

	@After
	public void tearDown() throws Exception {
		_objectEntryLocalService.deleteObjectEntry(_launchSetObjectEntry);

		ServiceContextThreadLocal.popServiceContext();
	}

	@Test
	public void testProcessFilterWithLaunchSetModeEdit() throws Exception {

		// Launch set ID header

		long launchSetId = _launchSetObjectEntry.getObjectEntryId();

		_assertProcessFilterWithLaunchSetModeEdit(
			launchSetId, _launchDraftLayout1.getPlid(),
			_launchDraftLayout2.getPlid(), null,
			HashMapBuilder.put(
				"X-Liferay-Launch-Set-Id", String.valueOf(launchSetId)
			).put(
				"X-Liferay-Launch-Set-Mode", LaunchSetMode.EDIT.getValue()
			).build(),
			Collections.emptyMap(), TestPropsValues.getUser());

		// Launch set ID parameter

		_assertProcessFilterWithLaunchSetModeEdit(
			launchSetId, _launchDraftLayout1.getPlid(),
			_launchDraftLayout2.getPlid(), null, Collections.emptyMap(),
			HashMapBuilder.put(
				"p_launch_set_id", String.valueOf(launchSetId)
			).put(
				"p_launch_set_mode", LaunchSetMode.EDIT.getValue()
			).build(),
			TestPropsValues.getUser());

		// Launch set ID parameter forwarded

		_assertProcessFilterWithLaunchSetModeEdit(
			launchSetId, _launchDraftLayout1.getPlid(),
			_launchDraftLayout2.getPlid(), Collections.emptyMap(),
			Collections.emptyMap(),
			HashMapBuilder.put(
				"p_launch_set_id", String.valueOf(launchSetId)
			).put(
				"p_launch_set_mode", LaunchSetMode.EDIT.getValue()
			).build(),
			TestPropsValues.getUser());

		// Launch set ID parameter of a missing launch set

		_assertProcessFilterWithLaunchSetModeEdit(
			-1, _layout1.getPlid(), _layout2.getPlid(), null,
			Collections.emptyMap(),
			HashMapBuilder.put(
				"p_launch_set_id", String.valueOf(RandomTestUtil.randomLong())
			).put(
				"p_launch_set_mode", LaunchSetMode.EDIT.getValue()
			).build(),
			TestPropsValues.getUser());

		// Launch set ID parameter of an approved launch set

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(
				_group.getGroupId(), TestPropsValues.getUserId());

		_objectEntryLocalService.updateStatus(
			TestPropsValues.getUserId(), launchSetId,
			WorkflowConstants.STATUS_APPROVED, serviceContext);

		_assertProcessFilterWithLaunchSetModeEdit(
			-1, _layout1.getPlid(), _layout2.getPlid(), null,
			Collections.emptyMap(),
			HashMapBuilder.put(
				"p_launch_set_id", String.valueOf(launchSetId)
			).put(
				"p_launch_set_mode", LaunchSetMode.EDIT.getValue()
			).build(),
			TestPropsValues.getUser());

		_objectEntryLocalService.updateStatus(
			TestPropsValues.getUserId(), launchSetId,
			WorkflowConstants.STATUS_DRAFT, serviceContext);

		// Launch set ID parameter with user without view permission

		_user = UserTestUtil.addUser();

		_assertProcessFilterWithLaunchSetModeEdit(
			-1, _layout1.getPlid(), _layout2.getPlid(), null,
			Collections.emptyMap(),
			HashMapBuilder.put(
				"p_launch_set_id", String.valueOf(launchSetId)
			).put(
				"p_launch_set_mode", LaunchSetMode.EDIT.getValue()
			).build(),
			_user);

		// Launch set already in preview mode

		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest();

		mockHttpServletRequest.setAttribute(
			WebKeys.USER, TestPropsValues.getUser());
		mockHttpServletRequest.setParameter(
			"p_launch_set_id", String.valueOf(launchSetId));
		mockHttpServletRequest.setParameter(
			"p_launch_set_mode", LaunchSetMode.EDIT.getValue());

		AtomicReference<LaunchSetMode> launchSetMode = new AtomicReference<>();

		try (SafeCloseable safeCloseable =
				LaunchSetThreadLocal.setLaunchSetIdWithSafeCloseable(
					launchSetId, LaunchSetMode.PREVIEW)) {

			_filter.doFilter(
				mockHttpServletRequest, new MockHttpServletResponse(),
				(servletRequest, servletResponse) -> launchSetMode.set(
					LaunchSetThreadLocal.getLaunchSetMode()));
		}

		Assert.assertEquals(LaunchSetMode.PREVIEW, launchSetMode.get());

		// Launch set mode header without a launch set ID

		_assertProcessFilterWithLaunchSetModeEdit(
			-1, _layout1.getPlid(), _layout2.getPlid(), null,
			HashMapBuilder.put(
				"X-Liferay-Launch-Set-Mode", LaunchSetMode.EDIT.getValue()
			).build(),
			Collections.emptyMap(), TestPropsValues.getUser());

		// Portlet namespaced launch set ID parameter

		String portletId = RandomTestUtil.randomString();

		_assertProcessFilterWithLaunchSetModeEdit(
			-1, _layout1.getPlid(), _layout2.getPlid(), null,
			Collections.emptyMap(),
			HashMapBuilder.put(
				"_" + portletId + "_p_launch_set_id",
				String.valueOf(launchSetId)
			).put(
				"_" + portletId + "_p_launch_set_mode",
				LaunchSetMode.EDIT.getValue()
			).put(
				"p_p_id", portletId
			).build(),
			TestPropsValues.getUser());
	}

	@Test
	public void testProcessFilterWithLaunchSetModePreview() throws Exception {

		// Launch set ID header

		long launchSetId = _launchSetObjectEntry.getObjectEntryId();

		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest();

		mockHttpServletRequest.addHeader(
			"X-Liferay-Launch-Set-Id", String.valueOf(launchSetId));
		mockHttpServletRequest.addHeader(
			"X-Liferay-Launch-Set-Mode", LaunchSetMode.PREVIEW.getValue());

		_assertProcessFilterWithLaunchSetModePreview(
			launchSetId, null, mockHttpServletRequest,
			TestPropsValues.getUser());

		// Launch set ID header with guest user

		_assertProcessFilterWithLaunchSetModePreview(
			-1, null, mockHttpServletRequest, null);

		// Launch set ID header without a launch set mode

		mockHttpServletRequest = new MockHttpServletRequest();

		mockHttpServletRequest.addHeader(
			"X-Liferay-Launch-Set-Id", String.valueOf(launchSetId));

		_assertProcessFilterWithLaunchSetModePreview(
			launchSetId, null, mockHttpServletRequest,
			TestPropsValues.getUser());

		// Launch set ID parameter

		mockHttpServletRequest = new MockHttpServletRequest();

		mockHttpServletRequest.setParameter("p_l_mode", Constants.PREVIEW);
		mockHttpServletRequest.setParameter(
			"p_launch_set_id", String.valueOf(launchSetId));
		mockHttpServletRequest.setParameter(
			"p_launch_set_mode", LaunchSetMode.PREVIEW.getValue());
		mockHttpServletRequest.setQueryString(
			StringBundler.concat(
				"p_launch_set_id=", launchSetId,
				"&p_launch_set_mode=preview&p_l_mode=preview"));
		mockHttpServletRequest.setRequestURI("/web/guest/home");

		_assertProcessFilterWithLaunchSetModePreview(
			launchSetId, null, mockHttpServletRequest,
			TestPropsValues.getUser());

		// Launch set ID parameter with guest user

		_assertProcessFilterWithLaunchSetModePreview(
			-1,
			HttpComponentsUtil.addParameter(
				_portal.getPathMain() + "/portal/login", "redirect",
				StringBundler.concat(
					"http://localhost/web/guest/home?p_launch_set_id=",
					launchSetId,
					"&p_launch_set_mode=preview&p_l_mode=preview")),
			mockHttpServletRequest, null);

		// Launch set ID parameter without a launch set mode

		mockHttpServletRequest = new MockHttpServletRequest();

		mockHttpServletRequest.setParameter("p_l_mode", Constants.PREVIEW);
		mockHttpServletRequest.setParameter(
			"p_launch_set_id", String.valueOf(launchSetId));

		_assertProcessFilterWithLaunchSetModePreview(
			launchSetId, null, mockHttpServletRequest,
			TestPropsValues.getUser());

		// Launch set ID parameter without layout preview mode

		mockHttpServletRequest = new MockHttpServletRequest();

		mockHttpServletRequest.setParameter(
			"p_launch_set_id", String.valueOf(launchSetId));
		mockHttpServletRequest.setParameter(
			"p_launch_set_mode", LaunchSetMode.PREVIEW.getValue());
		mockHttpServletRequest.setQueryString(
			StringBundler.concat(
				"p_launch_set_id=", launchSetId, "&p_launch_set_mode=preview"));
		mockHttpServletRequest.setRequestURI("/web/guest/home");

		_assertProcessFilterWithLaunchSetModePreview(
			-1,
			StringBundler.concat(
				"/web/guest/home?p_launch_set_id=", launchSetId,
				"&p_launch_set_mode=preview&p_l_mode=preview"),
			mockHttpServletRequest, TestPropsValues.getUser());
	}

	private long _addLaunchEntryDraftId(
			String baseVersionKey, String className, long classPK)
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

	private void _assertProcessFilterWithLaunchSetModeEdit(
			long expectedLaunchSetId, long expectedLayout1Plid,
			long expectedLayout2Plid, Map<String, String> forwardParameters,
			Map<String, String> headers, Map<String, String> parameters,
			User user)
		throws Exception {

		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest();

		for (Map.Entry<String, String> entry : headers.entrySet()) {
			mockHttpServletRequest.addHeader(entry.getKey(), entry.getValue());
		}

		mockHttpServletRequest.setAttribute(WebKeys.USER, user);
		mockHttpServletRequest.setParameters(parameters);

		AtomicLong launchSetId = new AtomicLong();
		AtomicReference<LaunchSetMode> launchSetMode = new AtomicReference<>();
		AtomicLong layout1Plid = new AtomicLong();
		AtomicLong layout2Plid = new AtomicLong();

		FilterChain filterChain = (servletRequest, servletResponse) -> {
			launchSetId.set(LaunchSetThreadLocal.getLaunchSetId());
			launchSetMode.set(LaunchSetThreadLocal.getLaunchSetMode());

			try {
				Layout layout1 = _layoutLocalService.getLayout(
					_layout1.getPlid());

				layout1Plid.set(layout1.getPlid());

				Layout layout2 = _layoutLocalService.getLayout(
					_layout2.getPlid());

				layout2Plid.set(layout2.getPlid());
			}
			catch (PortalException portalException) {
				throw new ServletException(portalException);
			}
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

		Assert.assertEquals(expectedLaunchSetId, launchSetId.get());
		Assert.assertEquals(expectedLayout1Plid, layout1Plid.get());
		Assert.assertEquals(expectedLayout2Plid, layout2Plid.get());

		if (expectedLaunchSetId > 0) {
			Assert.assertEquals(LaunchSetMode.EDIT, launchSetMode.get());
		}
		else {
			Assert.assertNull(launchSetMode.get());
		}

		Assert.assertNull(PreviewableResolverUtil.getPreviewId());
	}

	private void _assertProcessFilterWithLaunchSetModePreview(
			long expectedLaunchSetId, String expectedRedirectedURL,
			MockHttpServletRequest mockHttpServletRequest, User user)
		throws Exception {

		mockHttpServletRequest.removeAttribute(WebKeys.USER);

		if (user != null) {
			mockHttpServletRequest.setAttribute(WebKeys.USER, user);
		}

		AtomicLong launchSetId = new AtomicLong(-1);
		AtomicReference<LaunchSetMode> launchSetMode = new AtomicReference<>();
		MockHttpServletResponse mockHttpServletResponse =
			new MockHttpServletResponse();

		_filter.doFilter(
			mockHttpServletRequest, mockHttpServletResponse,
			(servletRequest, servletResponse) -> {
				launchSetId.set(LaunchSetThreadLocal.getLaunchSetId());
				launchSetMode.set(LaunchSetThreadLocal.getLaunchSetMode());
			});

		Assert.assertEquals(expectedLaunchSetId, launchSetId.get());

		if (expectedLaunchSetId > 0) {
			Assert.assertEquals(LaunchSetMode.PREVIEW, launchSetMode.get());
		}
		else {
			Assert.assertNull(launchSetMode.get());
		}

		Assert.assertEquals(
			expectedRedirectedURL, mockHttpServletResponse.getRedirectedUrl());
		Assert.assertNull(PreviewableResolverUtil.getPreviewId());
	}

	@Inject(filter = "servlet-filter-name=Launch Set Filter")
	private Filter _filter;

	@DeleteAfterTestRun
	private Group _group;

	private Layout _launchDraftLayout1;
	private Layout _launchDraftLayout2;
	private ObjectEntry _launchSetObjectEntry;
	private Layout _layout1;
	private Layout _layout2;

	@Inject
	private LayoutLocalService _layoutLocalService;

	@Inject
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	@Inject
	private ObjectEntryLocalService _objectEntryLocalService;

	@Inject
	private Portal _portal;

	@DeleteAfterTestRun
	private User _user;

}