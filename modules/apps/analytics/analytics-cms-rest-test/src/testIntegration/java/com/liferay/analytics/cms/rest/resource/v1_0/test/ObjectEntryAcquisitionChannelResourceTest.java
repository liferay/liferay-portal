/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.analytics.cms.rest.resource.v1_0.test;

import com.liferay.analytics.cms.rest.dto.v1_0.ObjectEntryAcquisitionChannel;
import com.liferay.analytics.cms.rest.resource.v1_0.ObjectEntryAcquisitionChannelResource;
import com.liferay.analytics.test.util.AnalyticsCompanyConfigurationTemporarySwapper;
import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.depot.constants.DepotConstants;
import com.liferay.depot.model.DepotEntry;
import com.liferay.depot.service.DepotEntryLocalService;
import com.liferay.object.exception.NoSuchObjectEntryException;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.rest.test.util.ObjectEntryTestUtil;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactoryUtil;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.MockHttp;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.Http;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.portal.vulcan.pagination.Page;

import jakarta.ws.rs.BadRequestException;

import java.io.Serializable;

import java.util.Collections;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
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

	@Before
	@Override
	public void setUp() throws Exception {
		super.setUp();

		_depotEntry = _depotEntryLocalService.addDepotEntry(
			Collections.singletonMap(
				LocaleUtil.getDefault(), RandomTestUtil.randomString()),
			Collections.singletonMap(
				LocaleUtil.getDefault(), RandomTestUtil.randomString()),
			DepotConstants.TYPE_SPACE,
			ServiceContextTestUtil.getServiceContext(
				testGroup.getGroupId(), TestPropsValues.getUserId()));

		_objectEntry = ObjectEntryTestUtil.addObjectEntry(
			_depotEntry.getGroupId(),
			_objectDefinitionLocalService.
				getObjectDefinitionByExternalReferenceCode(
					"L_CMS_BASIC_WEB_CONTENT", testCompany.getCompanyId()),
			HashMapBuilder.<String, Serializable>put(
				"title_i18n",
				HashMapBuilder.put(
					"en_US", RandomTestUtil.randomString()
				).build()
			).build());
	}

	@Override
	@Test
	public void testGetObjectEntryAcquisitionChannelsPage() throws Exception {
		_testGetObjectEntryAcquisitionChannelsPage();
		_testGetObjectEntryAcquisitionChannelsPageWithInvalidObjectEntryId();
		_testGetObjectEntryAcquisitionChannelsPageWithoutViewPermission();
	}

	@Test
	public void testGetObjectEntryAcquisitionChannelsPageWithUnsyncedGroup()
		throws Exception {

		try (AnalyticsCompanyConfigurationTemporarySwapper
				analyticsCompanyConfigurationTemporarySwapper =
					new AnalyticsCompanyConfigurationTemporarySwapper(
						testCompany.getCompanyId(),
						RandomTestUtil.randomString(), false)) {

			Assert.assertThrows(
				BadRequestException.class,
				() ->
					_objectEntryAcquisitionChannelResource.
						getObjectEntryAcquisitionChannelsPage(
							testGroup.getGroupId(),
							_objectEntry.getObjectEntryId(),
							RandomTestUtil.randomInt()));
		}
	}

	private void _testGetObjectEntryAcquisitionChannelsPage() throws Exception {
		try (AnalyticsCompanyConfigurationTemporarySwapper
				analyticsCompanyConfigurationTemporarySwapper =
					new AnalyticsCompanyConfigurationTemporarySwapper(
						testCompany.getCompanyId())) {

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
							null, _objectEntry.getObjectEntryId(),
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

	private void _testGetObjectEntryAcquisitionChannelsPageWithInvalidObjectEntryId()
		throws Exception {

		try (AnalyticsCompanyConfigurationTemporarySwapper
				analyticsCompanyConfigurationTemporarySwapper =
					new AnalyticsCompanyConfigurationTemporarySwapper(
						testCompany.getCompanyId(),
						RandomTestUtil.randomString(), false)) {

			Assert.assertThrows(
				NoSuchObjectEntryException.class,
				() ->
					_objectEntryAcquisitionChannelResource.
						getObjectEntryAcquisitionChannelsPage(
							null, RandomTestUtil.nextLong(),
							RandomTestUtil.randomInt()));
		}
	}

	private void _testGetObjectEntryAcquisitionChannelsPageWithoutViewPermission()
		throws Exception {

		PermissionChecker permissionChecker =
			PermissionThreadLocal.getPermissionChecker();

		try (AnalyticsCompanyConfigurationTemporarySwapper
				analyticsCompanyConfigurationTemporarySwapper =
					new AnalyticsCompanyConfigurationTemporarySwapper(
						testCompany.getCompanyId(),
						RandomTestUtil.randomString(), false)) {

			_user = UserTestUtil.addUser();

			PermissionThreadLocal.setPermissionChecker(
				PermissionCheckerFactoryUtil.create(_user));

			Assert.assertThrows(
				PrincipalException.MustHavePermission.class,
				() ->
					_objectEntryAcquisitionChannelResource.
						getObjectEntryAcquisitionChannelsPage(
							null, _objectEntry.getObjectEntryId(),
							RandomTestUtil.randomInt()));
		}
		finally {
			PermissionThreadLocal.setPermissionChecker(permissionChecker);
		}
	}

	@DeleteAfterTestRun
	private DepotEntry _depotEntry;

	@Inject
	private DepotEntryLocalService _depotEntryLocalService;

	@Inject
	private Http _http;

	@Inject
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	@DeleteAfterTestRun
	private ObjectEntry _objectEntry;

	@Inject
	private ObjectEntryAcquisitionChannelResource
		_objectEntryAcquisitionChannelResource;

	@DeleteAfterTestRun
	private User _user;

}