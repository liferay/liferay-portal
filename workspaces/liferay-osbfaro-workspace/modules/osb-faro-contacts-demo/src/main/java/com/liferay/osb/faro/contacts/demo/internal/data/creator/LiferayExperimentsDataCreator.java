/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.contacts.demo.internal.data.creator;

import com.liferay.osb.faro.engine.client.ContactsEngineClient;
import com.liferay.osb.faro.model.FaroProject;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.Time;

import java.util.Arrays;
import java.util.Date;
import java.util.Map;

/**
 * @author Matthew Kong
 */
public class LiferayExperimentsDataCreator extends DataCreator {

	public LiferayExperimentsDataCreator(
		ContactsEngineClient contactsEngineClient, FaroProject faroProject,
		String channelId, String dataSourceId) {

		super(
			contactsEngineClient, faroProject, "osbasahfaroinfo",
			"experiments");

		_channelId = channelId;
		_dataSourceId = dataSourceId;
	}

	@Override
	protected Map<String, Object> doCreate(Object[] params) {
		String dateString = formatDate(
			new Date(
				System.currentTimeMillis() -
					(number.numberBetween(0, 100) * Time.DAY)));

		Map<String, Object> pageContext = (Map<String, Object>)params[0];

		return HashMapBuilder.<String, Object>put(
			"channelId", _channelId
		).put(
			"confidenceLevel", 95
		).put(
			"createDate", dateString
		).put(
			"dataSourceId", _dataSourceId
		).put(
			"dxpExperienceId", "DEFAULT"
		).put(
			"dxpExperienceName", "Default"
		).put(
			"dxpGroupId", number.randomNumber(8, false)
		).put(
			"dxpLayoutId", internet.uuid()
		).put(
			"dxpSegmentId", "DEFAULT"
		).put(
			"dxpSegmentName", "Anyone"
		).put(
			"dxpVariants",
			Arrays.asList(
				HashMapBuilder.<String, Object>put(
					"changes", 0
				).put(
					"control", true
				).put(
					"dxpVariantId", "DEFAULT"
				).put(
					"dxpVariantName", "Control"
				).put(
					"trafficSplit", 34
				).build(),
				HashMapBuilder.<String, Object>put(
					"changes", 0
				).put(
					"control", false
				).put(
					"dxpVariantId",
					String.valueOf(number.randomNumber(8, false))
				).put(
					"dxpVariantName", company.buzzword()
				).put(
					"trafficSplit", 33
				).build(),
				HashMapBuilder.<String, Object>put(
					"changes", 0
				).put(
					"control", false
				).put(
					"dxpVariantId",
					String.valueOf(number.randomNumber(8, false))
				).put(
					"dxpVariantName", company.buzzword()
				).put(
					"trafficSplit", 33
				).build())
		).put(
			"goal",
			HashMapBuilder.put(
				"metric", "BOUNCE_RATE"
			).put(
				"target", ""
			).build()
		).put(
			"modifiedDate", dateString
		).put(
			"name", pageContext.get("title") + " Testing"
		).put(
			"pageURL", pageContext.get("canonicalUrl")
		).put(
			"startedDate", dateString
		).put(
			"status", "RUNNING"
		).put(
			"type", "AB"
		).build();
	}

	private final String _channelId;
	private final String _dataSourceId;

}