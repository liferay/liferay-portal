/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.analytics.cms.rest.resource.v1_0.test;

import com.liferay.analytics.cms.rest.client.dto.v1_0.ConnectionInfo;
import com.liferay.analytics.settings.configuration.AnalyticsConfiguration;
import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.depot.constants.DepotConstants;
import com.liferay.depot.model.DepotEntry;
import com.liferay.depot.service.DepotEntryGroupRelLocalService;
import com.liferay.depot.service.DepotEntryLocalService;
import com.liferay.portal.configuration.test.util.CompanyConfigurationTemporarySwapper;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.FeatureFlags;
import com.liferay.portal.test.rule.Inject;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Leslie Wong
 */
@FeatureFlags(
	featureFlags = {
		@FeatureFlag(value = "LPD-34594"), @FeatureFlag(value = "LPS-179669"),
		@FeatureFlag(value = "LPD-17564"), @FeatureFlag(value = "LPD-21926"),
		@FeatureFlag("LPD-11232")
	}
)
@RunWith(Arquillian.class)
public class ConnectionInfoResourceTest
	extends BaseConnectionInfoResourceTestCase {

	@Before
	@Override
	public void setUp() throws Exception {
		super.setUp();

		_serviceContext = ServiceContextTestUtil.getServiceContext(
			testCompany.getCompanyId(), testGroup.getGroupId(),
			TestPropsValues.getUserId());

		_depotEntry1 = _depotEntryLocalService.addDepotEntry(
			HashMapBuilder.put(
				LocaleUtil.getDefault(), RandomTestUtil.randomString()
			).build(),
			HashMapBuilder.put(
				LocaleUtil.getDefault(), RandomTestUtil.randomString()
			).build(),
			DepotConstants.TYPE_ASSET_LIBRARY, _serviceContext);
		_depotEntry2 = _depotEntryLocalService.addDepotEntry(
			HashMapBuilder.put(
				LocaleUtil.getDefault(), RandomTestUtil.randomString()
			).build(),
			HashMapBuilder.put(
				LocaleUtil.getDefault(), RandomTestUtil.randomString()
			).build(),
			DepotConstants.TYPE_ASSET_LIBRARY, _serviceContext);

		_group = GroupTestUtil.addGroup();

		_depotEntryGroupRelLocalService.addDepotEntryGroupRel(
			_depotEntry2.getDepotEntryId(), _group.getGroupId());
	}

	@Override
	@Test
	public void testGetConnectionInfo() throws Exception {
		Assert.assertEquals(
			new ConnectionInfo() {
				{
					admin = true;
					connectedToAnalyticsCloud = false;
					connectedToSpace = false;
					siteSyncedToAnalyticsCloud = false;
				}
			},
			connectionInfoResource.getConnectionInfo(
				_depotEntry1.getGroupId()));

		try (CompanyConfigurationTemporarySwapper
				companyConfigurationTemporarySwapper =
					new CompanyConfigurationTemporarySwapper(
						TestPropsValues.getCompanyId(),
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
							RandomTestUtil.randomString()
						).put(
							"token", RandomTestUtil.randomString()
						).build())) {

			Assert.assertEquals(
				new ConnectionInfo() {
					{
						admin = true;
						connectedToAnalyticsCloud = true;
						connectedToSpace = false;
						siteSyncedToAnalyticsCloud = false;
					}
				},
				connectionInfoResource.getConnectionInfo(
					_depotEntry1.getGroupId()));
		}

		Assert.assertEquals(
			new ConnectionInfo() {
				{
					admin = true;
					connectedToAnalyticsCloud = false;
					connectedToSpace = true;
					siteSyncedToAnalyticsCloud = false;
				}
			},
			connectionInfoResource.getConnectionInfo(
				_depotEntry2.getGroupId()));

		try (CompanyConfigurationTemporarySwapper
				companyConfigurationTemporarySwapper =
					new CompanyConfigurationTemporarySwapper(
						TestPropsValues.getCompanyId(),
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
							RandomTestUtil.randomString()
						).put(
							"syncedGroupIds",
							new String[] {String.valueOf(_group.getGroupId())}
						).put(
							"token", RandomTestUtil.randomString()
						).build())) {

			Assert.assertEquals(
				new ConnectionInfo() {
					{
						admin = true;
						connectedToAnalyticsCloud = true;
						connectedToSpace = true;
						siteSyncedToAnalyticsCloud = true;
					}
				},
				connectionInfoResource.getConnectionInfo(
					_depotEntry2.getGroupId()));
		}
	}

	@DeleteAfterTestRun
	private DepotEntry _depotEntry1;

	@DeleteAfterTestRun
	private DepotEntry _depotEntry2;

	@Inject
	private DepotEntryGroupRelLocalService _depotEntryGroupRelLocalService;

	@Inject
	private DepotEntryLocalService _depotEntryLocalService;

	@DeleteAfterTestRun
	private Group _group;

	private ServiceContext _serviceContext;

}