/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.partner;

import com.liferay.client.extension.util.spring.boot3.BaseRestController;
import com.liferay.client.extension.util.spring.boot3.client.LiferayOAuth2AccessTokenManager;

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
 * @author Elias Santos
 */
@RequestMapping("/object/action/mdf/claim/status/management")
@RestController
public class ObjectActionMDFClaimStatusManagementRestController
	extends BaseRestController {

	@PostMapping
	public ResponseEntity<String> post(@RequestBody String json) {
		JSONObject jsonObject = new JSONObject(json);

		JSONObject mdfClaimJSONObject = jsonObject.getJSONObject(
			"objectEntryDTOMDFClaim");

		JSONObject mdfClaimPropertiesJSONObject =
			mdfClaimJSONObject.getJSONObject("properties");

		String mdfClaimStatus = mdfClaimPropertiesJSONObject.getJSONObject(
			"mdfClaimStatus"
		).getString(
			"key"
		);

		if (mdfClaimStatus.equals("claimPaid")) {
			String mdfRequestExternalReferenceCode =
				mdfClaimPropertiesJSONObject.getString("mdfReqToMDFClmsERC");

			if (mdfClaimPropertiesJSONObject.getDouble("claimPaid") >=
					mdfClaimPropertiesJSONObject.getDouble(
						"totalMDFRequestedAmount")) {

				_completeMDFRequestStatus(mdfRequestExternalReferenceCode);
			}
			else {
				JSONObject responseJSONObject = new JSONObject(
					get(
						_getAuthorization(),
						UriComponentsBuilder.fromPath(
							"/o/c/mdfrequests/by-external-reference-code/" +
								mdfRequestExternalReferenceCode
						).build(
						).toUri()));

				if (responseJSONObject.getDouble("totalPaidAmount") >=
						responseJSONObject.getDouble("totalMDFRequestAmount")) {

					_completeMDFRequestStatus(mdfRequestExternalReferenceCode);
				}
			}
		}

		return new ResponseEntity<>(json, HttpStatus.OK);
	}

	private void _completeMDFRequestStatus(
		String mdfRequestExternalReferenceCode) {

		JSONObject jsonObject = new JSONObject();

		JSONObject mdfRequestStatusJSONObject = new JSONObject();

		mdfRequestStatusJSONObject.put(
			"key", "completed"
		).put(
			"name", "Completed"
		);

		jsonObject.put("mdfRequestStatus", mdfRequestStatusJSONObject);

		patch(
			_getAuthorization(), jsonObject.toString(),
			UriComponentsBuilder.fromPath(
				"/o/c/mdfrequests/by-external-reference-code/" +
					mdfRequestExternalReferenceCode
			).build(
			).toUri());
	}

	private String _getAuthorization() {
		return _liferayOAuth2AccessTokenManager.getAuthorization(
			"liferay-partner-etc-spring-boot-oahs");
	}

	@Autowired
	private LiferayOAuth2AccessTokenManager _liferayOAuth2AccessTokenManager;

}