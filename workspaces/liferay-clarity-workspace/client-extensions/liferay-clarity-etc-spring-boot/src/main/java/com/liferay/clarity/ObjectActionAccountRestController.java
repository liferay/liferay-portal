/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.clarity;

import com.liferay.client.extension.util.spring.boot3.BaseRestController;
import com.liferay.petra.string.StringBundler;

import java.util.Collections;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.json.JSONArray;
import org.json.JSONObject;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Raymond Augé
 * @author Gregory Amerson
 * @author Brian Wing Shun Chan
 */
@RequestMapping("/object/action/account")
@RestController
public class ObjectActionAccountRestController extends BaseRestController {

	@PostMapping
	public ResponseEntity<String> post(
			@AuthenticationPrincipal Jwt jwt, @RequestBody String json)
		throws Exception {

		log(jwt, _log, json);

		JSONObject jsonObject = new JSONObject(json);

		JSONObject objectEntryDTODistributorApplicationJSONObject =
			jsonObject.getJSONObject("objectEntryDTODistributorApplication");

		JSONObject propertiesJSONObject =
			objectEntryDTODistributorApplicationJSONObject.getJSONObject(
				"properties");

		String accountEmailAddress = propertiesJSONObject.getString(
			"applicantEmail");

		String accountName = propertiesJSONObject.getString("businessName");

		String accountExternalReferenceCode = "ACCOUNT_".concat(
			accountName.toUpperCase(
			).replace(
				' ', '_'
			));

		String response = post(
			StringBundler.concat(
				"{\"externalReferenceCode\": \"", accountExternalReferenceCode,
				"\", \"name\": \"", accountName, "\", \"type\": \"business\"}"),
			Collections.singletonMap(
				HttpHeaders.AUTHORIZATION, "Bearer " + jwt.getTokenValue()),
			"o/headless-admin-user/v1.0/accounts");

		_log("Created account: " + response);

		response = post(
			null,
			Collections.singletonMap(
				HttpHeaders.AUTHORIZATION, "Bearer " + jwt.getTokenValue()),
			StringBundler.concat(
				"o/headless-admin-user/v1.0/accounts",
				"/by-external-reference-code/", accountExternalReferenceCode,
				"/user-accounts/by-email-address/", accountEmailAddress));

		_log("Assigned user: " + response);

		JSONArray jsonArray = new JSONObject(
			get(
				Collections.singletonMap(
					HttpHeaders.AUTHORIZATION, "Bearer " + jwt.getTokenValue()),
				StringBundler.concat(
					"o/headless-admin-user/v1.0/accounts",
					"/by-external-reference-code/",
					accountExternalReferenceCode,
					"/account-roles?filter=name eq 'Account Administrator'"))
		).getJSONArray(
			"items"
		);

		response = post(
			null,
			Collections.singletonMap(
				HttpHeaders.AUTHORIZATION, "Bearer " + jwt.getTokenValue()),
			StringBundler.concat(
				"o/headless-admin-user/v1.0/accounts",
				"/by-external-reference-code/", accountExternalReferenceCode,
				"/account-roles/",
				jsonArray.getJSONObject(
					0
				).getInt(
					"id"
				),
				"/user-accounts/by-email-address/", accountEmailAddress));

		_log("Assigned role: " + response);

		return new ResponseEntity<>(json, HttpStatus.OK);
	}

	private void _log(String message) {
		if (_log.isInfoEnabled()) {
			_log.info(message);
		}
	}

	private static final Log _log = LogFactory.getLog(
		ObjectActionAccountRestController.class);

}