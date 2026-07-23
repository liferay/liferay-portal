/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.cmp.site.initializer.internal.model.listener.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.layout.test.util.ContentLayoutTestUtil;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.object.util.HttpServletRequestThreadLocal;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.comment.CommentManager;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.GroupConstants;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.IdentityServiceContextFunction;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.test.mail.MailServiceTestUtil;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.FeatureFlags;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.portal.test.rule.SynchronousMailTestRule;
import com.liferay.site.cmp.site.initializer.test.util.CMPTestUtil;

import jakarta.servlet.http.HttpServletRequest;

import java.io.Serializable;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.mock.web.MockHttpServletRequest;

/**
 * @author Pedro Leite
 */
@FeatureFlags(featureFlags = @FeatureFlag("LPD-58677"))
@RunWith(Arquillian.class)
public class MBMessageModelListenerTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE,
			SynchronousMailTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		HttpServletRequest httpServletRequest = new MockHttpServletRequest();

		Group group = CMPTestUtil.getOrAddGroup(
			MBMessageModelListenerTest.class);

		httpServletRequest.setAttribute(
			WebKeys.THEME_DISPLAY,
			ContentLayoutTestUtil.getThemeDisplay(
				_companyLocalService.fetchCompany(
					TestPropsValues.getCompanyId()),
				group,
				_layoutLocalService.fetchLayoutByFriendlyURL(
					group.getGroupId(), false, "/projects")));

		HttpServletRequestThreadLocal.setHttpServletRequest(httpServletRequest);
	}

	@After
	public void tearDown() throws Exception {
		HttpServletRequestThreadLocal.setHttpServletRequest(null);
	}

	@Test
	public void testOnAfterCreate() throws Exception {
		ObjectEntry cmpProjectObjectEntry =
			CMPTestUtil.addCMPProjectObjectEntry();

		_objectEntryLocalService.subscribeObjectEntry(
			TestPropsValues.getUserId(), cmpProjectObjectEntry.getGroupId(),
			cmpProjectObjectEntry.getObjectEntryId());

		cmpProjectObjectEntry = _objectEntryLocalService.updateObjectEntry(
			TestPropsValues.getUserId(),
			cmpProjectObjectEntry.getObjectEntryId(),
			cmpProjectObjectEntry.getObjectEntryFolderId(),
			HashMapBuilder.<String, Serializable>put(
				"title", RandomTestUtil.randomString()
			).build(),
			ServiceContextTestUtil.getServiceContext());

		User user = TestPropsValues.getUser();

		_commentManager.addComment(
			null, TestPropsValues.getUserId(),
			cmpProjectObjectEntry.getGroupId(),
			cmpProjectObjectEntry.getModelClassName(),
			cmpProjectObjectEntry.getObjectEntryId(), user.getFullName(), null,
			RandomTestUtil.randomString(),
			new IdentityServiceContextFunction(
				ServiceContextTestUtil.getServiceContext()));

		Assert.assertTrue(
			MailServiceTestUtil.lastMailMessageContains(
				"There is a new comment on the project " +
					cmpProjectObjectEntry.getTitleValue()));
		Assert.assertTrue(
			MailServiceTestUtil.lastMailMessageContains(
				StringBundler.concat(
					GroupConstants.CMS_FRIENDLY_URL, "/e/project/",
					_portal.getClassNameId(
						cmpProjectObjectEntry.getModelClassName()),
					"/", cmpProjectObjectEntry.getObjectEntryId())));
	}

	@Inject
	private CommentManager _commentManager;

	@Inject
	private CompanyLocalService _companyLocalService;

	@Inject
	private GroupLocalService _groupLocalService;

	@Inject
	private LayoutLocalService _layoutLocalService;

	@Inject
	private ObjectEntryLocalService _objectEntryLocalService;

	@Inject
	private Portal _portal;

}