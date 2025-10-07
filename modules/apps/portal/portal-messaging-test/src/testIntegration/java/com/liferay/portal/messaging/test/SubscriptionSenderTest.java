/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.messaging.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.layout.test.util.LayoutTestUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.service.CompanyLocalServiceUtil;
import com.liferay.portal.kernel.service.LayoutSetLocalService;
import com.liferay.portal.kernel.test.rule.DataGuard;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.PropsValues;
import com.liferay.portal.kernel.util.SubscriptionSender;
import com.liferay.portal.kernel.util.TreeMapBuilder;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.subscription.model.Subscription;
import com.liferay.subscription.service.SubscriptionLocalService;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Mika Koivisto
 * @author Christopher Kian
 */
@DataGuard(scope = DataGuard.Scope.METHOD)
@RunWith(Arquillian.class)
public class SubscriptionSenderTest {

	@ClassRule
	@Rule
	public static final LiferayIntegrationTestRule liferayIntegrationTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();

		Company company = CompanyLocalServiceUtil.getCompany(
			TestPropsValues.getCompanyId());

		_portalURL = _portal.getPortalURL(
			company.getVirtualHostname(), _portal.getPortalServerPort(false),
			false);

		_virtualURL = "http://virtual";

		int portalServerPort = _portal.getPortalServerPort(false);

		if (portalServerPort > 0) {
			_virtualURL = StringBundler.concat(
				_virtualURL, StringPool.COLON, portalServerPort);
		}
	}

	@Test
	public void testGetPortalURLFromPrivateLayoutSetWithEntryURL()
		throws Exception {

		LayoutTestUtil.addTypePortletLayout(_group, true);
		LayoutTestUtil.addTypePortletLayout(_group, false);

		SubscriptionSender subscriptionSender = new SubscriptionSender();

		subscriptionSender.setEntryURL(
			_portalURL +
				PropsValues.LAYOUT_FRIENDLY_URL_PRIVATE_GROUP_SERVLET_MAPPING);
		subscriptionSender.setGroupId(_group.getGroupId());
		subscriptionSender.setMailId("test-mail-id");

		subscriptionSender.initialize();

		String portalURL = String.valueOf(
			subscriptionSender.getContextAttribute("[$PORTAL_URL$]"));

		Assert.assertEquals(_portalURL, portalURL);
	}

	@Test
	public void testGetPortalURLFromPrivateLayoutSetWithVirtualHost()
		throws Exception {

		LayoutTestUtil.addTypePortletLayout(_group, true);

		_layoutSetLocalService.updateVirtualHosts(
			_group.getGroupId(), true,
			TreeMapBuilder.put(
				"virtual", StringPool.BLANK
			).build());

		SubscriptionSender subscriptionSender = new SubscriptionSender();

		subscriptionSender.setGroupId(_group.getGroupId());
		subscriptionSender.setMailId("test-mail-id");

		subscriptionSender.initialize();

		String portalURL = String.valueOf(
			subscriptionSender.getContextAttribute("[$PORTAL_URL$]"));

		Assert.assertEquals(_virtualURL, portalURL);
	}

	@Test
	public void testGetPortalURLFromPrivateLayoutSetWithVirtualHostAndEntryURL()
		throws Exception {

		LayoutTestUtil.addTypePortletLayout(_group, true);

		_layoutSetLocalService.updateVirtualHosts(
			_group.getGroupId(), true,
			TreeMapBuilder.put(
				"virtual", StringPool.BLANK
			).build());

		// Include a public layout set to test for LPS-113218

		LayoutTestUtil.addTypePortletLayout(_group, false);

		_layoutSetLocalService.updateVirtualHosts(
			_group.getGroupId(), false,
			TreeMapBuilder.put(
				"publicVirtualHost", StringPool.BLANK
			).build());

		SubscriptionSender subscriptionSender = new SubscriptionSender();

		subscriptionSender.setEntryURL(
			_portalURL +
				PropsValues.LAYOUT_FRIENDLY_URL_PRIVATE_GROUP_SERVLET_MAPPING);
		subscriptionSender.setGroupId(_group.getGroupId());
		subscriptionSender.setMailId("test-mail-id");

		subscriptionSender.initialize();

		String portalURL = String.valueOf(
			subscriptionSender.getContextAttribute("[$PORTAL_URL$]"));

		Assert.assertEquals(_virtualURL, portalURL);
	}

	@Test
	public void testGetPortalURLFromPublicLayoutSetWithVirtualHost()
		throws Exception {

		LayoutTestUtil.addTypePortletLayout(_group, false);

		_layoutSetLocalService.updateVirtualHosts(
			_group.getGroupId(), false,
			TreeMapBuilder.put(
				"virtual", StringPool.BLANK
			).build());

		SubscriptionSender subscriptionSender = new SubscriptionSender();

		subscriptionSender.setGroupId(_group.getGroupId());
		subscriptionSender.setMailId("test-mail-id");

		subscriptionSender.initialize();

		String portalURL = String.valueOf(
			subscriptionSender.getContextAttribute("[$PORTAL_URL$]"));

		Assert.assertEquals(_virtualURL, portalURL);
	}

	@Test
	public void testHasSubscriptionsReturnsFalseWhenNoSubscribers()
		throws PortalException {

		SubscriptionSender subscriptionSender = new SubscriptionSender();

		Assert.assertFalse(subscriptionSender.hasSubscribers());
	}

	@Test
	public void testHasSubscriptionsReturnsTrueWhenSubscriptionsInDB()
		throws PortalException {

		Subscription subscription = _subscriptionLocalService.addSubscription(
			TestPropsValues.getUserId(), _group.getGroupId(),
			Group.class.getName(), _group.getGroupId());

		SubscriptionSender subscriptionSender = new SubscriptionSender();

		subscriptionSender.addPersistedSubscribers(
			Group.class.getName(), _group.getGroupId(), false);

		Assert.assertTrue(subscriptionSender.hasSubscribers());

		_subscriptionLocalService.deleteSubscription(subscription);
	}

	@Test
	public void testPortalURLIsReturned() throws Exception {
		SubscriptionSender subscriptionSender = new SubscriptionSender();

		subscriptionSender.setMailId("test-mail-id");

		subscriptionSender.initialize();

		String portalURL = String.valueOf(
			subscriptionSender.getContextAttribute("[$PORTAL_URL$]"));

		Assert.assertEquals(_portalURL, portalURL);
	}

	@Test
	public void testPortalURLParamsAreRemoved() throws Exception {
		SubscriptionSender subscriptionSender = new SubscriptionSender();

		subscriptionSender.setEntryURL(_portalURL + "/test/page?with&params");
		subscriptionSender.setGroupId(_group.getGroupId());
		subscriptionSender.setMailId("test-mail-id");

		subscriptionSender.initialize();

		String portalURL = String.valueOf(
			subscriptionSender.getContextAttribute("[$PORTAL_URL$]"));

		Assert.assertEquals(_portalURL, portalURL);
	}

	@Test
	public void testVirtualURLIsReturned() throws Exception {
		SubscriptionSender subscriptionSender = new SubscriptionSender();

		subscriptionSender.setEntryURL(_virtualURL);
		subscriptionSender.setMailId("test-mail-id");

		subscriptionSender.initialize();

		String portalURL = String.valueOf(
			subscriptionSender.getContextAttribute("[$PORTAL_URL$]"));

		Assert.assertEquals(_virtualURL, portalURL);
	}

	private Group _group;

	@Inject
	private LayoutSetLocalService _layoutSetLocalService;

	@Inject
	private Portal _portal;

	private String _portalURL;

	@Inject
	private SubscriptionLocalService _subscriptionLocalService;

	private String _virtualURL;

}