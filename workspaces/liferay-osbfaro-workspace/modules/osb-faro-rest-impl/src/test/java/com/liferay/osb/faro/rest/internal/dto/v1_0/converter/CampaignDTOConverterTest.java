/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.rest.internal.dto.v1_0.converter;

import com.liferay.osb.faro.rest.dto.v1_0.Campaign;
import com.liferay.portal.kernel.test.util.RandomTestUtil;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author Nilton Vieira
 */
public class CampaignDTOConverterTest {

	@Test
	public void testToDTO() {
		Assert.assertNull(_campaignDTOConverter.toDTO(null, null));

		com.liferay.osb.faro.engine.client.model.Campaign engineClientCampaign =
			new com.liferay.osb.faro.engine.client.model.Campaign();

		engineClientCampaign.setAccountsTouched(RandomTestUtil.randomLong());
		engineClientCampaign.setCampaignName(RandomTestUtil.randomString());
		engineClientCampaign.setCampaignType(RandomTestUtil.randomString());
		engineClientCampaign.setEndDate(RandomTestUtil.nextDate());
		engineClientCampaign.setId(RandomTestUtil.randomString());
		engineClientCampaign.setIndividualsTouched(RandomTestUtil.randomLong());
		engineClientCampaign.setLastTouchDate(RandomTestUtil.nextDate());
		engineClientCampaign.setOrigin(RandomTestUtil.randomString());
		engineClientCampaign.setStartDate(RandomTestUtil.nextDate());
		engineClientCampaign.setStatus(RandomTestUtil.randomString());

		Campaign campaign = _campaignDTOConverter.toDTO(
			null, engineClientCampaign);

		Assert.assertEquals(
			engineClientCampaign.getAccountsTouched(),
			campaign.getAccountsTouched());
		Assert.assertEquals(
			engineClientCampaign.getCampaignType(), campaign.getCampaignType());
		Assert.assertEquals(
			engineClientCampaign.getEndDate(), campaign.getEndDate());
		Assert.assertEquals(engineClientCampaign.getId(), campaign.getId());
		Assert.assertEquals(
			engineClientCampaign.getIndividualsTouched(),
			campaign.getIndividualsTouched());
		Assert.assertEquals(
			engineClientCampaign.getLastTouchDate(),
			campaign.getLastTouchDate());
		Assert.assertEquals(
			engineClientCampaign.getCampaignName(), campaign.getName());
		Assert.assertEquals(
			engineClientCampaign.getOrigin(), campaign.getOrigin());
		Assert.assertEquals(
			engineClientCampaign.getStartDate(), campaign.getStartDate());
		Assert.assertEquals(
			engineClientCampaign.getStatus(), campaign.getStatus());
	}

	private final CampaignDTOConverter _campaignDTOConverter =
		new CampaignDTOConverter();

}