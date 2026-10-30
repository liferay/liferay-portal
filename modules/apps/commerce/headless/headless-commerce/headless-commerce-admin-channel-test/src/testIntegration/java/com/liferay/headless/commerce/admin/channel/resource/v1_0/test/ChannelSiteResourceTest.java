/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.admin.channel.resource.v1_0.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.commerce.currency.model.CommerceCurrency;
import com.liferay.commerce.currency.test.util.CommerceCurrencyTestUtil;
import com.liferay.commerce.product.model.CommerceChannel;
import com.liferay.commerce.test.util.CommerceTestUtil;
import com.liferay.headless.commerce.admin.channel.client.dto.v1_0.ChannelSite;
import com.liferay.headless.commerce.admin.channel.client.pagination.Page;
import com.liferay.headless.commerce.admin.channel.client.pagination.Pagination;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Organization;
import com.liferay.portal.kernel.service.GroupLocalServiceUtil;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.OrganizationTestUtil;
import com.liferay.portal.kernel.util.LocaleUtil;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Michele Vigilante
 */
@RunWith(Arquillian.class)
public class ChannelSiteResourceTest extends BaseChannelSiteResourceTestCase {

	@Before
	@Override
	public void setUp() throws Exception {
		super.setUp();

		_commerceCurrency = CommerceCurrencyTestUtil.addCommerceCurrency(
			testGroup.getCompanyId());

		_commerceChannel = CommerceTestUtil.addCommerceChannel(
			testGroup.getGroupId(), _commerceCurrency.getCode());
	}

	@Override
	@Test
	public void testGetChannelAvailableSitesPage() throws Exception {
		super.testGetChannelAvailableSitesPage();

		_testGetChannelAvailableSitesPageWithGlobalSite();
		_testGetChannelAvailableSitesPageWithOrganizationSite();
		_testGetChannelAvailableSitesPageWithOtherChannelSite();
		_testGetChannelAvailableSitesPageWithSearch();
	}

	@Override
	@Test
	public void testGetChannelByExternalReferenceCodeAvailableSitesPage()
		throws Exception {

		super.testGetChannelByExternalReferenceCodeAvailableSitesPage();
	}

	@Override
	protected String[] getAdditionalAssertFieldNames() {
		return new String[] {
			"externalReferenceCode", "friendlyUrlPath", "id", "name"
		};
	}

	@Override
	protected ChannelSite testGetChannelAvailableSitesPage_addChannelSite(
			Long channelId, ChannelSite channelSite)
		throws Exception {

		Group group = GroupTestUtil.addGroup();

		_groups.add(group);

		return new ChannelSite() {
			{
				externalReferenceCode = group.getExternalReferenceCode();
				friendlyUrlPath = group.getFriendlyURL();
				id = group.getGroupId();
				name = group.getName(LocaleUtil.US);
			}
		};
	}

	@Override
	protected Long testGetChannelAvailableSitesPage_getChannelId()
		throws Exception {

		return _commerceChannel.getCommerceChannelId();
	}

	@Override
	protected ChannelSite
			testGetChannelByExternalReferenceCodeAvailableSitesPage_addChannelSite(
				String externalReferenceCode, ChannelSite channelSite)
		throws Exception {

		return testGetChannelAvailableSitesPage_addChannelSite(
			_commerceChannel.getCommerceChannelId(), channelSite);
	}

	@Override
	protected String
			testGetChannelByExternalReferenceCodeAvailableSitesPage_getExternalReferenceCode()
		throws Exception {

		return _commerceChannel.getExternalReferenceCode();
	}

	private void _testGetChannelAvailableSitesPageWithGlobalSite()
		throws Exception {

		Page<ChannelSite> page =
			channelSiteResource.getChannelAvailableSitesPage(
				_commerceChannel.getCommerceChannelId(), null,
				Pagination.of(1, 100));

		List<Long> groupIds = TransformUtil.transform(
			page.getItems(), ChannelSite::getId);

		Group companyGroup = GroupLocalServiceUtil.getCompanyGroup(
			testGroup.getCompanyId());

		Assert.assertFalse(groupIds.contains(companyGroup.getGroupId()));
	}

	private void _testGetChannelAvailableSitesPageWithOrganizationSite()
		throws Exception {

		_organization = OrganizationTestUtil.addOrganization(true);

		Page<ChannelSite> page =
			channelSiteResource.getChannelAvailableSitesPage(
				_commerceChannel.getCommerceChannelId(), null,
				Pagination.of(1, 100));

		List<Long> groupIds = TransformUtil.transform(
			page.getItems(), ChannelSite::getId);

		Assert.assertTrue(groupIds.contains(_organization.getGroupId()));
	}

	private void _testGetChannelAvailableSitesPageWithOtherChannelSite()
		throws Exception {

		Group group = GroupTestUtil.addGroup();

		_groups.add(group);

		_commerceChannels.add(
			CommerceTestUtil.addCommerceChannel(
				group.getGroupId(), _commerceCurrency.getCode()));

		Page<ChannelSite> page =
			channelSiteResource.getChannelAvailableSitesPage(
				_commerceChannel.getCommerceChannelId(), null,
				Pagination.of(1, 100));

		List<Long> groupIds = TransformUtil.transform(
			page.getItems(), ChannelSite::getId);

		Assert.assertFalse(groupIds.contains(group.getGroupId()));
		Assert.assertTrue(groupIds.contains(testGroup.getGroupId()));
	}

	private void _testGetChannelAvailableSitesPageWithSearch()
		throws Exception {

		ChannelSite channelSite =
			testGetChannelAvailableSitesPage_addChannelSite(
				_commerceChannel.getCommerceChannelId(), null);

		Page<ChannelSite> page =
			channelSiteResource.getChannelAvailableSitesPage(
				_commerceChannel.getCommerceChannelId(), channelSite.getName(),
				Pagination.of(1, 100));

		Assert.assertEquals(1, page.getTotalCount());
		assertContains(channelSite, (List<ChannelSite>)page.getItems());
	}

	@DeleteAfterTestRun
	private CommerceChannel _commerceChannel;

	@DeleteAfterTestRun
	private final List<CommerceChannel> _commerceChannels = new ArrayList<>();

	@DeleteAfterTestRun
	private CommerceCurrency _commerceCurrency;

	@DeleteAfterTestRun
	private final List<Group> _groups = new ArrayList<>();

	@DeleteAfterTestRun
	private Organization _organization;

}