/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.change.tracking.internal.portlet.action.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.change.tracking.configuration.CTCollectionEmailConfiguration;
import com.liferay.change.tracking.constants.CTPortletKeys;
import com.liferay.change.tracking.model.CTCollection;
import com.liferay.change.tracking.service.CTCollectionLocalService;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.configuration.test.util.CompanyConfigurationTemporarySwapper;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCResourceCommand;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.test.portlet.MockLiferayResourceRequest;
import com.liferay.portal.kernel.test.portlet.MockLiferayResourceResponse;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.kernel.util.JavaConstants;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.test.mail.MailMessage;
import com.liferay.portal.test.mail.MailServiceTestUtil;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.portal.test.rule.SynchronousMailTestRule;

import jakarta.portlet.PortletRequest;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Brooke Dalton
 */
@RunWith(Arquillian.class)
public class InviteUsersMVCResourceCommandTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE,
			SynchronousMailTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_ctCollection = _ctCollectionLocalService.addCTCollection(
			null, TestPropsValues.getCompanyId(), TestPropsValues.getUserId(),
			0, RandomTestUtil.randomString(), null);
	}

	@FeatureFlag("LPD-11212")
	@Test
	public void testGetInviteUsersEmailNotificationBody() throws Exception {
		_testGetInviteUsers(_ctCollection);

		Assert.assertEquals(1, MailServiceTestUtil.getInboxSize());

		MailMessage mailMessage = MailServiceTestUtil.getLastMailMessage();

		String mailMessageBody = mailMessage.getBody();

		String url = PortletURLBuilder.create(
			_portal.getControlPanelPortletURL(
				_serviceContext.getRequest(), _serviceContext.getScopeGroup(),
				CTPortletKeys.PUBLICATIONS, 0, 0, PortletRequest.RENDER_PHASE)
		).setMVCRenderCommandName(
			"/change_tracking/view_changes"
		).setParameter(
			"ctCollectionId", _ctCollection.getCtCollectionId()
		).buildString();

		Assert.assertFalse(url.isEmpty());

		Assert.assertTrue(
			mailMessageBody.contains(
				StringBundler.concat(
					"You have been invited to work on a publication. For ",
					"further information, please visit:<br /><br />\n<a href=",
					"\"", url, "\">", _ctCollection.getName(), "</a><br />",
					"<br />")));
	}

	@FeatureFlag("LPD-11212")
	@Test
	public void testGetInviteUsersWithCustomEmailFromAddressAndEmailFromName()
		throws Exception {

		String emailFromAddress =
			RandomTestUtil.randomString() + "@liferay.com";
		String emailFromName = RandomTestUtil.randomString();

		try (CompanyConfigurationTemporarySwapper
				companyConfigurationTemporarySwapper =
					new CompanyConfigurationTemporarySwapper(
						TestPropsValues.getCompanyId(),
						CTCollectionEmailConfiguration.class.getName(),
						HashMapDictionaryBuilder.<String, Object>put(
							"emailFromAddress", emailFromAddress
						).put(
							"emailFromName", emailFromName
						).build())) {

			_testGetInviteUsers(_ctCollection);

			MailMessage mailMessage = MailServiceTestUtil.getLastMailMessage();

			String from = mailMessage.getFirstHeaderValue("From");

			Assert.assertEquals(
				from,
				StringBundler.concat(
					emailFromName, " <", emailFromAddress, ">"));
		}
	}

	@FeatureFlag("LPD-11212")
	@Test
	public void testGetInviteUsersWithCustomInvitationEmailBodyAndInvitationEmailSubject()
		throws Exception {

		String invitationEmailBody = RandomTestUtil.randomString();
		String invitationEmailSubject = RandomTestUtil.randomString();

		try (CompanyConfigurationTemporarySwapper
				companyConfigurationTemporarySwapper =
					new CompanyConfigurationTemporarySwapper(
						TestPropsValues.getCompanyId(),
						CTCollectionEmailConfiguration.class.getName(),
						HashMapDictionaryBuilder.<String, Object>put(
							"invitationEmailBody", invitationEmailBody
						).put(
							"invitationEmailSubject", invitationEmailSubject
						).build())) {

			_testGetInviteUsers(_ctCollection);

			MailMessage mailMessage = MailServiceTestUtil.getLastMailMessage();

			Assert.assertEquals(
				invitationEmailSubject,
				mailMessage.getFirstHeaderValue("Subject"));

			String mailMessageBody = mailMessage.getBody();

			Assert.assertTrue(mailMessageBody.contains(invitationEmailBody));
		}
	}

	private MockLiferayResourceRequest _getMockLiferayResourceRequest(
			long ctCollectionId, long userId)
		throws Exception {

		MockLiferayResourceRequest mockLiferayResourceRequest =
			new MockLiferayResourceRequest();

		mockLiferayResourceRequest.setAttribute(
			JavaConstants.JAKARTA_PORTLET_CONFIG, null);
		mockLiferayResourceRequest.setAttribute(
			WebKeys.THEME_DISPLAY, _getThemeDisplay());
		mockLiferayResourceRequest.setParameter(
			"ctCollectionId", String.valueOf(ctCollectionId));
		mockLiferayResourceRequest.setParameter(
			"roleValues", String.valueOf(0));
		mockLiferayResourceRequest.setParameter(
			"userIds", String.valueOf(userId));

		return mockLiferayResourceRequest;
	}

	private ThemeDisplay _getThemeDisplay() throws Exception {
		ThemeDisplay themeDisplay = new ThemeDisplay();

		themeDisplay.setCompany(
			_companyLocalService.fetchCompany(TestPropsValues.getCompanyId()));
		themeDisplay.setPermissionChecker(
			PermissionThreadLocal.getPermissionChecker());
		themeDisplay.setSiteGroupId(TestPropsValues.getGroupId());
		themeDisplay.setUser(TestPropsValues.getUser());

		return themeDisplay;
	}

	private void _testGetInviteUsers(CTCollection ctCollection)
		throws Exception {

		_serviceContext = ServiceContextTestUtil.getServiceContext(
			TestPropsValues.getGroupId(), TestPropsValues.getUserId());

		User user = UserTestUtil.addUser();

		MockLiferayResourceRequest mockLiferayResourceRequest =
			_getMockLiferayResourceRequest(
				ctCollection.getCtCollectionId(), user.getUserId());

		_serviceContext.setRequest(
			mockLiferayResourceRequest.getHttpServletRequest());

		ServiceContextThreadLocal.pushServiceContext(_serviceContext);

		try {
			_mvcResourceCommand.serveResource(
				mockLiferayResourceRequest, new MockLiferayResourceResponse());
		}
		finally {
			ServiceContextThreadLocal.popServiceContext();
		}
	}

	private static CTCollection _ctCollection;

	@Inject
	private static CTCollectionLocalService _ctCollectionLocalService;

	@Inject
	private CompanyLocalService _companyLocalService;

	@Inject(filter = "mvc.command.name=/change_tracking/invite_users")
	private MVCResourceCommand _mvcResourceCommand;

	@Inject
	private Portal _portal;

	private ServiceContext _serviceContext;

}