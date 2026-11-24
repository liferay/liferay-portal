/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.web.internal.model.display.contacts;

import com.liferay.osb.faro.engine.client.model.Author;
import com.liferay.osb.faro.engine.client.model.IndividualSegment;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author Eudaldo Alonso
 */
public class IndividualSegmentDisplayTest {

	@Test
	public void testGetReferencedObjects() {
		Map<String, CampaignDisplay> campaignDisplays = _getCampaignDisplays(
			HashMapBuilder.<String, Object>put(
				"field-mappings", Collections.emptyList()
			).build());

		Assert.assertTrue(campaignDisplays.isEmpty());

		campaignDisplays = _getCampaignDisplays(
			HashMapBuilder.<String, Object>put(
				"campaigns",
				List.of(
					Map.of("campaignName", "Alpha", "id", "C1"),
					Map.of("campaignName", "Bravo", "id", "C2"))
			).put(
				"field-mappings", Collections.emptyList()
			).build());

		Assert.assertEquals(
			campaignDisplays.toString(), 2, campaignDisplays.size());

		CampaignDisplay campaignDisplay = campaignDisplays.get("C1");

		Assert.assertEquals(
			"Alpha",
			ReflectionTestUtil.getFieldValue(campaignDisplay, "_campaignName"));
		Assert.assertEquals(
			"C1", ReflectionTestUtil.getFieldValue(campaignDisplay, "_id"));

		campaignDisplay = campaignDisplays.get("C2");

		Assert.assertEquals(
			"Bravo",
			ReflectionTestUtil.getFieldValue(campaignDisplay, "_campaignName"));
		Assert.assertEquals(
			"C2", ReflectionTestUtil.getFieldValue(campaignDisplay, "_id"));
	}

	private Map<String, CampaignDisplay> _getCampaignDisplays(
		Map<String, Object> referencedObjects) {

		IndividualSegment individualSegment = new IndividualSegment();

		individualSegment.setAuthor(new Author());
		individualSegment.setEmbeddedResources(
			Map.of("referenced-objects", referencedObjects));

		Map<String, Object> displayReferencedObjects =
			ReflectionTestUtil.getFieldValue(
				new IndividualSegmentDisplay(individualSegment),
				"_referencedObjects");

		return (Map<String, CampaignDisplay>)displayReferencedObjects.get(
			"campaigns");
	}

}