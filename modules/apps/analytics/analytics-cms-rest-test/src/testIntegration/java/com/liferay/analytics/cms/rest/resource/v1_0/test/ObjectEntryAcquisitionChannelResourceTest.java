/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.analytics.cms.rest.resource.v1_0.test;

import com.liferay.analytics.cms.rest.dto.v1_0.ObjectEntryAcquisitionChannel;
import com.liferay.analytics.cms.rest.resource.v1_0.ObjectEntryAcquisitionChannelResource;
import com.liferay.analytics.settings.configuration.AnalyticsConfiguration;
import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.portal.configuration.test.util.CompanyConfigurationTemporarySwapper;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.MockHttp;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.kernel.util.Http;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.portal.vulcan.pagination.Page;

import java.util.Collections;
import java.util.List;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Rachael Koestartyo
 */
@RunWith(Arquillian.class)
public class ObjectEntryAcquisitionChannelResourceTest
	extends BaseObjectEntryAcquisitionChannelResourceTestCase {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Override
	@Test
	public void testGetObjectEntryAcquisitionChannelsPage() throws Exception {
		try (CompanyConfigurationTemporarySwapper
				companyConfigurationTemporarySwapper =
					new CompanyConfigurationTemporarySwapper(
						testCompany.getCompanyId(),
						AnalyticsConfiguration.class.getName(),
						HashMapDictionaryBuilder.<String, Object>put(
							"liferayAnalyticsDataSourceId",
							RandomTestUtil.nextLong()
						).put(
							"liferayAnalyticsEnableAllGroupIds", true
						).put(
							"liferayAnalyticsFaroBackendSecuritySignature",
							RandomTestUtil.randomString()
						).put(
							"liferayAnalyticsFaroBackendURL",
							"http://" + RandomTestUtil.randomString()
						).build())) {

			Double value1 = 1.0;
			Double value2 = 2.0;
			Double value3 = 5.0;

			Double totalCount = value1 + value2 + value3;

			Double percentage1 = value1 / totalCount;
			Double percentage2 = value2 / totalCount;
			Double percentage3 = value3 / totalCount;

			ReflectionTestUtil.setFieldValue(
				_objectEntryAcquisitionChannelResource, "_http",
				new MockHttp(
					Collections.singletonMap(
						"/api/1.0/asset-metric/objectEntry" +
							"/acquisition-channels",
						() -> JSONUtil.putAll(
							JSONUtil.put(
								"name", "direct"
							).put(
								"percentage", percentage1
							).put(
								"value", value1
							),
							JSONUtil.put(
								"name", "social"
							).put(
								"percentage", percentage2
							).put(
								"value", value2
							),
							JSONUtil.put(
								"name", "others"
							).put(
								"percentage", percentage3
							).put(
								"value", value3
							)
						).toString())));

			Page<ObjectEntryAcquisitionChannel>
				objectEntryAcquisitionChannelsPage =
					_objectEntryAcquisitionChannelResource.
						getObjectEntryAcquisitionChannelsPage(
							RandomTestUtil.randomString(), null,
							RandomTestUtil.randomInt());

			Assert.assertEquals(
				3, objectEntryAcquisitionChannelsPage.getTotalCount());

			List<ObjectEntryAcquisitionChannel> objectEntryAcquisitionChannels =
				ListUtil.fromCollection(
					objectEntryAcquisitionChannelsPage.getItems());

			ObjectEntryAcquisitionChannel objectEntryAcquisitionChannel1 =
				objectEntryAcquisitionChannels.get(0);

			Assert.assertEquals(
				"direct", objectEntryAcquisitionChannel1.getName());
			Assert.assertEquals(
				percentage1, objectEntryAcquisitionChannel1.getPercentage());
			Assert.assertEquals(
				value1, objectEntryAcquisitionChannel1.getValue());

			ObjectEntryAcquisitionChannel objectEntryAcquisitionChannel2 =
				objectEntryAcquisitionChannels.get(1);

			Assert.assertEquals(
				"social", objectEntryAcquisitionChannel2.getName());
			Assert.assertEquals(
				percentage2, objectEntryAcquisitionChannel2.getPercentage());
			Assert.assertEquals(
				value2, objectEntryAcquisitionChannel2.getValue());

			ObjectEntryAcquisitionChannel objectEntryAcquisitionChannel3 =
				objectEntryAcquisitionChannels.get(2);

			Assert.assertEquals(
				"others", objectEntryAcquisitionChannel3.getName());
			Assert.assertEquals(
				percentage3, objectEntryAcquisitionChannel3.getPercentage());
			Assert.assertEquals(
				value3, objectEntryAcquisitionChannel3.getValue());
		}
		finally {
			ReflectionTestUtil.setFieldValue(
				_objectEntryAcquisitionChannelResource, "_http", _http);
		}
	}

	@Inject
	private Http _http;

	@Inject
	private ObjectEntryAcquisitionChannelResource
		_objectEntryAcquisitionChannelResource;

}