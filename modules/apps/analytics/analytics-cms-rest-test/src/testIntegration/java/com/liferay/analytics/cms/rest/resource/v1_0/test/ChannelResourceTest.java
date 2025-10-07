/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.analytics.cms.rest.resource.v1_0.test;

import com.liferay.analytics.cms.rest.client.dto.v1_0.Channel;
import com.liferay.analytics.cms.rest.client.pagination.Page;
import com.liferay.analytics.cms.rest.client.resource.v1_0.ChannelResource;
import com.liferay.analytics.settings.configuration.AnalyticsConfiguration;
import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.portal.configuration.test.util.CompanyConfigurationTemporarySwapper;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.test.rule.Inject;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Rachael Koestartyo
 */
@RunWith(Arquillian.class)
public class ChannelResourceTest extends BaseChannelResourceTestCase {

	@Override
	@Test
	public void testGetChannelsPage() throws Exception {
		User user = UserTestUtil.addGroupUser(
			testGroup, RoleConstants.SITE_MEMBER);
		String password = RandomTestUtil.randomString();

		_userLocalService.updatePassword(
			user.getUserId(), password, password, false, true);

		ChannelResource channelResource = ChannelResource.builder(
		).authentication(
			user.getEmailAddress(), password
		).endpoint(
			testCompany.getVirtualHostname(), 8080, "http"
		).locale(
			LocaleUtil.getDefault()
		).build();

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
						).put(
							"syncedGroupIds",
							new String[] {
								String.valueOf(testGroup.getGroupId())
							}
						).build())) {

			Page<Channel> channelsPage = channelResource.getChannelsPage(null);

			Assert.assertEquals(1, channelsPage.getTotalCount());

			List<Channel> channels = (List<Channel>)channelsPage.getItems();

			Channel channel = channels.get(0);

			Assert.assertEquals(
				String.valueOf(testGroup.getGroupId()),
				String.valueOf(channel.getGroupId()));
			Assert.assertEquals(
				testGroup.getName(LocaleUtil.getDefault()), channel.getName());
		}
	}

	@Inject
	private UserLocalService _userLocalService;

}