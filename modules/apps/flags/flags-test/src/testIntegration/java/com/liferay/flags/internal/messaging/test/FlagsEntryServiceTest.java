/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.flags.internal.messaging.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.flags.service.FlagsEntryService;
import com.liferay.portal.kernel.messaging.BaseMessageListener;
import com.liferay.portal.kernel.messaging.Destination;
import com.liferay.portal.kernel.messaging.DestinationConfiguration;
import com.liferay.portal.kernel.messaging.DestinationFactory;
import com.liferay.portal.kernel.messaging.DestinationNames;
import com.liferay.portal.kernel.messaging.Message;
import com.liferay.portal.kernel.messaging.MessageListener;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.CompanyLocalServiceUtil;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.HtmlEscapableObject;
import com.liferay.portal.kernel.util.HtmlUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.SubscriptionSender;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceRegistration;

import org.springframework.mock.web.MockHttpServletRequest;

/**
 * @author Pei-Jung Lan
 */
@RunWith(Arquillian.class)
public class FlagsEntryServiceTest {

	@ClassRule
	@Rule
	public static final LiferayIntegrationTestRule liferayIntegrationTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() {
		Bundle bundle = FrameworkUtil.getBundle(FlagsEntryServiceTest.class);

		_bundleContext = bundle.getBundleContext();
	}

	@After
	public void tearDown() {
		for (ServiceRegistration<?> serviceRegistration :
				_serviceRegistrations) {

			serviceRegistration.unregister();
		}
	}

	@Test
	public void testEscapeContentAttributes() throws Exception {
		MockSubscriptionSenderMessageListener
			mockSubscriptionSenderMessageListener =
				new MockSubscriptionSenderMessageListener();

		_registerDestination(mockSubscriptionSenderMessageListener);

		Map<String, String> contentAttributes = HashMapBuilder.put(
			"[$CONTENT_TITLE$]", "<img src=x onerror=alert(1)>"
		).put(
			"[$CONTENT_URL$]", "<img src=x onerror=alert(2)>"
		).put(
			"[$REASON$]", "<img src=x onerror=alert(3)>"
		).build();

		_flagsEntryService.addEntry(
			RandomTestUtil.randomString(), 0L,
			RandomTestUtil.randomString() + "@liferay.com",
			TestPropsValues.getUserId(),
			contentAttributes.get("[$CONTENT_TITLE$]"),
			contentAttributes.get("[$CONTENT_URL$]"),
			contentAttributes.get("[$REASON$]"),
			ServiceContextTestUtil.getServiceContext());

		mockSubscriptionSenderMessageListener.block();

		_assertContentAttributesEscaped(
			contentAttributes,
			mockSubscriptionSenderMessageListener.getSubscriptionSender());
	}

	@Test
	public void testNotifyAfterRequestIsRecycled() throws Exception {
		MockSubscriptionSenderMessageListener
			mockSubscriptionSenderMessageListener =
				new MockSubscriptionSenderMessageListener();

		_registerDestination(mockSubscriptionSenderMessageListener);

		User user = TestPropsValues.getUser();

		int portalServerPort = PortalUtil.getPortalServerPort(false);

		ThemeDisplay themeDisplay = new ThemeDisplay();

		themeDisplay.setCompany(
			CompanyLocalServiceUtil.getCompany(TestPropsValues.getCompanyId()));
		themeDisplay.setLocale(LocaleUtil.getDefault());
		themeDisplay.setPortalURL(
			PortalUtil.getPortalURL("localhost", portalServerPort, false));
		themeDisplay.setScopeGroupId(TestPropsValues.getGroupId());
		themeDisplay.setServerName("localhost");
		themeDisplay.setServerPort(portalServerPort);
		themeDisplay.setSiteGroupId(TestPropsValues.getGroupId());
		themeDisplay.setUser(user);

		Thread thread = Thread.currentThread();

		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest() {

				@Override
				public Object getAttribute(String name) {
					if (thread != Thread.currentThread()) {
						throw new IllegalStateException(
							"The request object has been recycled");
					}

					return super.getAttribute(name);
				}

			};

		mockHttpServletRequest.setAttribute(
			WebKeys.THEME_DISPLAY, themeDisplay);

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext();

		serviceContext.setRequest(mockHttpServletRequest);

		_flagsEntryService.addEntry(
			RandomTestUtil.randomString(), 0L,
			RandomTestUtil.randomString() + "@liferay.com", user.getUserId(),
			RandomTestUtil.randomString(), RandomTestUtil.randomString(),
			RandomTestUtil.randomString(), serviceContext);

		mockSubscriptionSenderMessageListener.block();

		SubscriptionSender subscriptionSender =
			mockSubscriptionSenderMessageListener.getSubscriptionSender();

		HtmlEscapableObject htmlEscapableObject =
			(HtmlEscapableObject)subscriptionSender.getContextAttribute(
				"[$REPORTED_USER_URL$]");

		Assert.assertEquals(
			user.getDisplayURL(themeDisplay),
			htmlEscapableObject.getOriginalValue());
	}

	private void _assertContentAttributesEscaped(
		Map<String, String> contentAttributes,
		SubscriptionSender subscriptionSender) {

		for (Map.Entry<String, String> entry : contentAttributes.entrySet()) {
			HtmlEscapableObject htmlEscapableObject =
				(HtmlEscapableObject)subscriptionSender.getContextAttribute(
					entry.getKey());

			Assert.assertTrue(htmlEscapableObject.isEscape());
			Assert.assertEquals(
				HtmlUtil.escape(
					String.valueOf(htmlEscapableObject.getOriginalValue())),
				htmlEscapableObject.getEscapedValue());
		}
	}

	private void _registerDestination(MessageListener messageListener) {
		Destination destination = _destinationFactory.createDestination(
			new DestinationConfiguration(
				DestinationConfiguration.DESTINATION_TYPE_SYNCHRONOUS,
				DestinationNames.SUBSCRIPTION_SENDER));

		Dictionary<String, Object> dictionary = MapUtil.singletonDictionary(
			"destination.name", destination.getName());

		_serviceRegistrations.add(
			_bundleContext.registerService(
				Destination.class, destination, dictionary));
		_serviceRegistrations.add(
			_bundleContext.registerService(
				MessageListener.class, messageListener, dictionary));
	}

	private BundleContext _bundleContext;

	@Inject
	private DestinationFactory _destinationFactory;

	@Inject
	private FlagsEntryService _flagsEntryService;

	private final List<ServiceRegistration<?>> _serviceRegistrations =
		new ArrayList<>();

	private class MockSubscriptionSenderMessageListener
		extends BaseMessageListener {

		public void block() throws Exception {
			Assert.assertTrue(_countDownLatch.await(30, TimeUnit.SECONDS));
		}

		public SubscriptionSender getSubscriptionSender() {
			return _subscriptionSender.get();
		}

		@Override
		protected void doReceive(Message message) {
			_subscriptionSender.set((SubscriptionSender)message.getPayload());

			_countDownLatch.countDown();
		}

		private final CountDownLatch _countDownLatch = new CountDownLatch(1);
		private final AtomicReference<SubscriptionSender> _subscriptionSender =
			new AtomicReference<>();

	}

}