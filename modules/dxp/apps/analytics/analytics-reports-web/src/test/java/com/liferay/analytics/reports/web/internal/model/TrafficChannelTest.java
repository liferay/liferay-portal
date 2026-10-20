/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.analytics.reports.web.internal.model;

import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletActionRequest;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletRenderResponse;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.Tuple;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.ResourceBundle;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

/**
 * @author David Arques
 */
public class TrafficChannelTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Test
	public void testCreationWithDirectType() {
		TrafficChannel trafficChannel = TrafficChannel.newInstance(
			1, 7.0, TrafficChannel.Type.DIRECT);

		Assert.assertEquals(
			"this-is-the-number-of-page-views-generated-by-people-arriving-" +
				"directly-to-your-page",
			trafficChannel.getHelpMessageKey());

		Assert.assertEquals(1, trafficChannel.getTrafficAmount());
		Assert.assertEquals(7.0, trafficChannel.getTrafficShare(), 0);
		Assert.assertEquals(
			TrafficChannel.Type.DIRECT, trafficChannel.getType());

		Assert.assertNull(trafficChannel.getTuple());
	}

	@Test
	public void testCreationWithDirectTypeAcquisitionChannel() {
		AcquisitionChannel acquisitionChannel = new AcquisitionChannel(
			"DIRECT", RandomTestUtil.randomInt(),
			RandomTestUtil.randomDouble());

		TrafficChannel trafficChannel = TrafficChannel.newInstance(
			acquisitionChannel);

		Assert.assertEquals(
			acquisitionChannel.getTrafficAmount(),
			trafficChannel.getTrafficAmount());
		Assert.assertEquals(
			acquisitionChannel.getTrafficShare(),
			trafficChannel.getTrafficShare(), 0);
		Assert.assertEquals(
			TrafficChannel.Type.valueOf(acquisitionChannel.getName()),
			trafficChannel.getType());
	}

	@Test(expected = IllegalArgumentException.class)
	public void testCreationWithInvalidTypeAcquisitionChannel() {
		AcquisitionChannel acquisitionChannel = new AcquisitionChannel(
			"invalid", RandomTestUtil.randomInt(),
			RandomTestUtil.randomDouble());

		TrafficChannel.newInstance(acquisitionChannel);
	}

	@Test
	public void testToJSON() {
		TrafficChannel trafficChannel = TrafficChannel.newInstance(
			1, 7.0, TrafficChannel.Type.DIRECT);

		Assert.assertEquals(
			JSONUtil.put(
				"helpMessage", trafficChannel.getHelpMessageKey()
			).put(
				"name", String.valueOf(trafficChannel.getType())
			).put(
				"share", String.format("%.1f", trafficChannel.getTrafficShare())
			).put(
				"title", "direct[traffic-channel]"
			).put(
				"value", Math.toIntExact(trafficChannel.getTrafficAmount())
			).toString(),
			String.valueOf(
				trafficChannel.toJSONObject(
					new MockLiferayPortletActionRequest(),
					new MockLiferayPortletRenderResponse(),
					_getResourceBundle(trafficChannel))));
	}

	@Test
	public void testToJSONObjectWithError() {
		TrafficChannel trafficChannel = TrafficChannel.newInstance(
			new IllegalArgumentException(""), TrafficChannel.Type.DIRECT);

		Assert.assertEquals(
			JSONUtil.put(
				"helpMessage",
				"this-is-the-number-of-page-views-generated-by-people-" +
					"arriving-directly-to-your-page"
			).put(
				"name", TrafficChannel.Type.DIRECT.toString()
			).put(
				"title", "direct[traffic-channel]"
			).toString(),
			String.valueOf(
				trafficChannel.toJSONObject(
					new MockLiferayPortletActionRequest(),
					new MockLiferayPortletRenderResponse(),
					_getResourceBundle(trafficChannel))));
	}

	@Test
	public void testToJSONWithParameters() {
		TrafficChannel trafficChannel = TrafficChannel.newInstance(
			1, 7.0, TrafficChannel.Type.ORGANIC);

		MockLiferayPortletActionRequest mockLiferayPortletActionRequest =
			new MockLiferayPortletActionRequest();

		mockLiferayPortletActionRequest.setParameter("param1", "value1");

		Assert.assertEquals(
			JSONUtil.put(
				"helpMessage", trafficChannel.getHelpMessageKey()
			).put(
				"name", String.valueOf(trafficChannel.getType())
			).put(
				"share", String.format("%.1f", trafficChannel.getTrafficShare())
			).put(
				"title", String.valueOf(trafficChannel.getType())
			).put(
				"value", Math.toIntExact(trafficChannel.getTrafficAmount())
			).toString(),
			String.valueOf(
				trafficChannel.toJSONObject(
					mockLiferayPortletActionRequest,
					new MockLiferayPortletRenderResponse(),
					_getResourceBundle(trafficChannel))));
	}

	@Test
	public void testToTrafficChannelWithOrganicName() {
		AcquisitionChannel acquisitionChannel = new AcquisitionChannel(
			"ORGANIC", RandomTestUtil.randomInt(),
			RandomTestUtil.randomDouble());

		TrafficChannel trafficChannel = TrafficChannel.newInstance(
			acquisitionChannel);

		Assert.assertEquals(
			"this-is-the-number-of-page-views-generated-by-people-coming-" +
				"from-a-search-engine",
			trafficChannel.getHelpMessageKey());
		Assert.assertEquals(
			acquisitionChannel.getTrafficAmount(),
			trafficChannel.getTrafficAmount());
		Assert.assertEquals(
			acquisitionChannel.getTrafficShare(),
			trafficChannel.getTrafficShare(), 0);
		Assert.assertEquals(
			TrafficChannel.Type.valueOf(acquisitionChannel.getName()),
			trafficChannel.getType());
		Assert.assertNull(trafficChannel.getTuple());
	}

	@Test
	public void testToTrafficChannelWithPaidName() {
		AcquisitionChannel acquisitionChannel = new AcquisitionChannel(
			"PAID", RandomTestUtil.randomInt(), RandomTestUtil.randomDouble());

		TrafficChannel trafficChannel = TrafficChannel.newInstance(
			acquisitionChannel);

		Assert.assertEquals(
			"this-is-the-number-of-page-views-generated-by-people-that-find-" +
				"your-page-through-google-adwords",
			trafficChannel.getHelpMessageKey());
		Assert.assertEquals(
			acquisitionChannel.getTrafficAmount(),
			trafficChannel.getTrafficAmount());
		Assert.assertEquals(
			acquisitionChannel.getTrafficShare(),
			trafficChannel.getTrafficShare(), 0);
		Assert.assertEquals(
			TrafficChannel.Type.valueOf(acquisitionChannel.getName()),
			trafficChannel.getType());
		Assert.assertNull(trafficChannel.getTuple());
	}

	@Test
	public void testToTrafficChannelWithReferralName() {
		AcquisitionChannel acquisitionChannel = new AcquisitionChannel(
			"REFERRAL", RandomTestUtil.randomInt(),
			RandomTestUtil.randomDouble());

		TrafficChannel trafficChannel = TrafficChannel.newInstance(
			acquisitionChannel);

		Assert.assertEquals(
			"this-is-the-number-of-page-views-generated-by-people-coming-to-" +
				"your-page-from-other-sites-which-are-not-search-engine-" +
					"pages-or-social-sites",
			trafficChannel.getHelpMessageKey());
		Assert.assertEquals(
			acquisitionChannel.getTrafficAmount(),
			trafficChannel.getTrafficAmount());
		Assert.assertEquals(
			acquisitionChannel.getTrafficShare(),
			trafficChannel.getTrafficShare(), 0);
		Assert.assertEquals(
			TrafficChannel.Type.valueOf(acquisitionChannel.getName()),
			trafficChannel.getType());

		Tuple tuple = trafficChannel.getTuple();

		Assert.assertEquals(
			"/analytics_reports/get_referral_traffic_sources",
			tuple.getObject(0));
	}

	@Test
	public void testToTrafficChannelWithSocialName() {
		AcquisitionChannel acquisitionChannel = new AcquisitionChannel(
			"SOCIAL", RandomTestUtil.randomInt(),
			RandomTestUtil.randomDouble());

		TrafficChannel trafficChannel = TrafficChannel.newInstance(
			acquisitionChannel);

		Assert.assertEquals(
			"this-is-the-number-of-page-views-generated-by-people-coming-to-" +
				"your-page-from-social-sites",
			trafficChannel.getHelpMessageKey());
		Assert.assertEquals(
			acquisitionChannel.getTrafficAmount(),
			trafficChannel.getTrafficAmount());
		Assert.assertEquals(
			acquisitionChannel.getTrafficShare(),
			trafficChannel.getTrafficShare(), 0);
		Assert.assertEquals(
			TrafficChannel.Type.valueOf(acquisitionChannel.getName()),
			trafficChannel.getType());

		Tuple tuple = trafficChannel.getTuple();

		Assert.assertEquals(
			"/analytics_reports/get_social_traffic_sources",
			tuple.getObject(0));
	}

	private ResourceBundle _getResourceBundle(TrafficChannel trafficChannel) {
		return new ResourceBundle() {

			@Override
			public Enumeration<String> getKeys() {
				return Collections.enumeration(
					Arrays.asList(
						"direct[traffic-channel]",
						String.valueOf(trafficChannel.getType()),
						trafficChannel.getHelpMessageKey()));
			}

			@Override
			protected Object handleGetObject(String key) {
				return key;
			}

		};
	}

}