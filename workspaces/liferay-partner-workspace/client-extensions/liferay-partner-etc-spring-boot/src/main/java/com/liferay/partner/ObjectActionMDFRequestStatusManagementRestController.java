/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.partner;

import com.liferay.client.extension.util.spring.boot3.BaseRestController;
import com.liferay.client.extension.util.spring.boot3.client.LiferayOAuth2AccessTokenManager;

import org.json.JSONArray;
import org.json.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * @author Felipe França
 */
@RequestMapping("/object/action/mdf/request/status/management")
@RestController
public class ObjectActionMDFRequestStatusManagementRestController
	extends BaseRestController {

	@PostMapping
	public ResponseEntity<String> post(@RequestBody String json) {
		JSONObject jsonObject = new JSONObject(json);

		JSONObject mdfRequestJSONObject = jsonObject.getJSONObject(
			"objectEntryDTOMDFRequest");

		JSONObject mdfRequestPropertiesJSONObject =
			mdfRequestJSONObject.getJSONObject("properties");

		String mdfRequestStatus = mdfRequestPropertiesJSONObject.getJSONObject(
			"mdfRequestStatus"
		).getString(
			"key"
		);

		JSONObject newActivityStatusJSONObject = new JSONObject();

		if (mdfRequestStatus.equals("approved")) {
			newActivityStatusJSONObject.put(
				"key", "active"
			).put(
				"name", "Active"
			);
		}
		else if (mdfRequestStatus.equals("completed")) {
			newActivityStatusJSONObject.put(
				"key", "completed"
			).put(
				"name", "Completed"
			);
		}
		else if (mdfRequestStatus.equals("marketingDirectorReview") ||
				 mdfRequestStatus.equals("pendingMarketingReview")) {

			newActivityStatusJSONObject.put(
				"key", "submitted"
			).put(
				"name", "Submitted"
			);
		}
		else if (mdfRequestStatus.equals("moreInfoRequested")) {
			newActivityStatusJSONObject.put(
				"key", "moreInfoRequested"
			).put(
				"name", "More Info Requested"
			);
		}
		else if (mdfRequestStatus.equals("rejected")) {
			newActivityStatusJSONObject.put(
				"key", "rejected"
			).put(
				"name", "Rejected"
			);
		}

		if (newActivityStatusJSONObject.isEmpty()) {
			return new ResponseEntity<>(json, HttpStatus.OK);
		}

		JSONObject responseJSONObject = new JSONObject(
			get(
				_getAuthorization(),
				UriComponentsBuilder.fromPath(
					"/o/c/activities"
				).queryParam(
					"filter",
					"r_mdfReqToActs_c_mdfRequestId eq '" +
						mdfRequestJSONObject.getString("id") + "'"
				).queryParam(
					"page", "1"
				).queryParam(
					"pageSize", "-1"
				).build(
				).toUri()));

		JSONArray itemsJSONArray = responseJSONObject.getJSONArray("items");

		for (int i = 0; i < itemsJSONArray.length(); i++) {
			JSONObject itemJSONObject = itemsJSONArray.getJSONObject(i);

			JSONObject activityStatusJSONObject = itemJSONObject.getJSONObject(
				"activityStatus");

			activityStatusJSONObject.put(
				"key", newActivityStatusJSONObject.getString("key")
			).put(
				"name", newActivityStatusJSONObject.getString("name")
			);
		}

		put(
			_getAuthorization(), itemsJSONArray.toString(),
			UriComponentsBuilder.fromPath(
				"/o/c/activities/batch"
			).build(
			).toUri());

		return new ResponseEntity<>(json, HttpStatus.OK);
	}

	private String _getAuthorization() {
		return _liferayOAuth2AccessTokenManager.getAuthorization(
			"liferay-partner-etc-spring-boot-oahs");
	}

	@Autowired
	private LiferayOAuth2AccessTokenManager _liferayOAuth2AccessTokenManager;

}