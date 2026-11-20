/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.memberships.web.internal.portlet.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.GroupConstants;
import com.liferay.portal.kernel.model.MembershipRequest;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.portlet.bridges.mvc.constants.MVCRenderConstants;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactory;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.service.MembershipRequestLocalService;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletRenderRequest;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletRenderResponse;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.portlet.test.MockLiferayPortletContext;

import jakarta.portlet.Portlet;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Brooke Dalton
 */
@RunWith(Arquillian.class)
public class SiteMembershipsPortletTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Test
	@TestInfo("LPD-116128")
	public void testRenderWithoutPermissions() throws Exception {
		Group group = GroupTestUtil.addGroupWithType(
			GroupConstants.TYPE_SITE_RESTRICTED);
		User user = UserTestUtil.addUser();

		MembershipRequest membershipRequest =
			_membershipRequestLocalService.addMembershipRequest(
				user.getUserId(), group.getGroupId(),
				RandomTestUtil.randomString(),
				ServiceContextTestUtil.getServiceContext(group.getGroupId()));

		PermissionThreadLocal.setPermissionChecker(
			_permissionCheckerFactory.create(
				UserTestUtil.addGroupAdminUser(GroupTestUtil.addGroup())));

		for (String mvcPath :
				new String[] {
					"/preview_membership_request.jsp",
					"/reply_membership_request.jsp"
				}) {

			MockLiferayPortletRenderRequest mockLiferayPortletRenderRequest =
				new MockLiferayPortletRenderRequest();

			mockLiferayPortletRenderRequest.setAttribute(
				MVCRenderConstants.
					PORTLET_CONTEXT_OVERRIDE_REQUEST_ATTIBUTE_NAME_PREFIX +
						"/error.jsp",
				new MockLiferayPortletContext("/error.jsp"));
			mockLiferayPortletRenderRequest.setParameter(
				"membershipRequestId",
				String.valueOf(membershipRequest.getMembershipRequestId()));
			mockLiferayPortletRenderRequest.setParameter("mvcPath", mvcPath);

			_portlet.render(
				mockLiferayPortletRenderRequest,
				new MockLiferayPortletRenderResponse());

			Assert.assertTrue(
				SessionErrors.contains(
					mockLiferayPortletRenderRequest,
					PrincipalException.getNestedClasses()));
			Assert.assertNull(
				mvcPath,
				mockLiferayPortletRenderRequest.getAttribute(
					WebKeys.MEMBERSHIP_REQUEST));
		}
	}

	@Inject
	private MembershipRequestLocalService _membershipRequestLocalService;

	@Inject
	private PermissionCheckerFactory _permissionCheckerFactory;

	@Inject(
		filter = "component.name=com.liferay.site.memberships.web.internal.portlet.SiteMembershipsPortlet"
	)
	private Portlet _portlet;

}