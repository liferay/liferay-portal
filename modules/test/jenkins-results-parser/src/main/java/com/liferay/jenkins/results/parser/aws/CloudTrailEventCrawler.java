/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser.aws;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.retry.RetryMode;
import com.amazonaws.services.cloudtrail.AWSCloudTrail;
import com.amazonaws.services.cloudtrail.AWSCloudTrailClientBuilder;
import com.amazonaws.services.cloudtrail.model.Event;
import com.amazonaws.services.cloudtrail.model.LookupAttribute;
import com.amazonaws.services.cloudtrail.model.LookupAttributeKey;
import com.amazonaws.services.cloudtrail.model.LookupEventsRequest;
import com.amazonaws.services.cloudtrail.model.LookupEventsResult;

import com.liferay.jenkins.results.parser.JenkinsResultsParserUtil;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.json.JSONObject;

/**
 * @author Brittney Nguyen
 */
public class CloudTrailEventCrawler {

	public CloudTrailEventCrawler(String regionName) {
		ClientConfiguration clientConfiguration = new ClientConfiguration();

		clientConfiguration.setMaxErrorRetry(9);
		clientConfiguration.setRetryMode(RetryMode.ADAPTIVE);

		AWSCloudTrailClientBuilder awsCloudTrailClientBuilder =
			AWSCloudTrailClientBuilder.standard();

		awsCloudTrailClientBuilder.withClientConfiguration(clientConfiguration);
		awsCloudTrailClientBuilder.withRegion(regionName);

		_awsCloudTrail = awsCloudTrailClientBuilder.build();
	}

	public List<JSONObject> getEventJSONObjects(
		Date endDate, String eventName, Date startDate) {

		List<JSONObject> eventJSONObjects = new ArrayList<>();

		LookupAttribute lookupAttribute = new LookupAttribute();

		lookupAttribute.withAttributeKey(LookupAttributeKey.EventName);
		lookupAttribute.withAttributeValue(eventName);

		LookupEventsRequest lookupEventsRequest = new LookupEventsRequest();

		lookupEventsRequest.withEndTime(endDate);
		lookupEventsRequest.withLookupAttributes(lookupAttribute);
		lookupEventsRequest.withMaxResults(50);
		lookupEventsRequest.withStartTime(startDate);

		int lookupCount = 0;

		while (true) {
			LookupEventsResult lookupEventsResult = _awsCloudTrail.lookupEvents(
				lookupEventsRequest);

			for (Event event : lookupEventsResult.getEvents()) {
				eventJSONObjects.add(
					new JSONObject(event.getCloudTrailEvent()));
			}

			lookupCount++;

			if ((lookupCount % 10) == 0) {
				System.out.println(
					JenkinsResultsParserUtil.combine(
						"Retrieved ", String.valueOf(eventJSONObjects.size()),
						" ", eventName, " events"));
			}

			String nextToken = lookupEventsResult.getNextToken();

			if (JenkinsResultsParserUtil.isNullOrEmpty(nextToken)) {
				break;
			}

			lookupEventsRequest.withNextToken(nextToken);

			JenkinsResultsParserUtil.sleep(300);
		}

		return eventJSONObjects;
	}

	private final AWSCloudTrail _awsCloudTrail;

}