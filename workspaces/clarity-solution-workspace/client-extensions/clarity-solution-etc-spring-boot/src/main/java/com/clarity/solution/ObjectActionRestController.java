/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.clarity.solution;

import com.liferay.client.extension.util.spring.boot3.BaseRestController;
import com.liferay.petra.string.StringBundler;

import org.apache.commons.lang3.StringUtils;

import org.json.JSONObject;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * @author David Nebinger
 * @author Mumen Tayyem
 */
@RequestMapping("/object/action")
@RestController
public class ObjectActionRestController extends BaseRestController {

	@PostMapping
	public ResponseEntity<String> post(
		@AuthenticationPrincipal Jwt jwt, @RequestBody String json) {

		JSONObject jsonObject = new JSONObject(json);

		JSONObject propertiesJSONObject = jsonObject.getJSONObject(
			"objectEntryDTOU3A2DistributorApplication"
		).getJSONObject(
			"properties"
		);

		String accountEmailAddress = propertiesJSONObject.getString(
			"applicantEmailAddress");

		String businessName = propertiesJSONObject.getString("businessName");

		String externalReferenceCode = "ACCOUNT_";

		externalReferenceCode += StringUtils.replace(
			StringUtils.upperCase(businessName), " ", "_");

		post(
			jwt.toString(),
			new JSONObject(
			).put(
				"externalReferenceCode", externalReferenceCode
			).put(
				"name", businessName
			).put(
				"type", "business"
			).toString(),
			UriComponentsBuilder.fromUriString(
				"/o/headless-admin-user/v1.0/accounts"
			).build(
			).toUri());

		post(
			jwt.toString(), "",
			UriComponentsBuilder.fromUriString(
				StringBundler.concat(
					"/o/headless-admin-user/v1.0/accounts",
					"/by-external-reference-code/", externalReferenceCode,
					"/user-accounts/by-email-address/", accountEmailAddress)
			).build(
			).toUri());

		long id = new JSONObject(
			get(
				jwt.toString(),
				UriComponentsBuilder.fromUriString(
					StringBundler.concat(
						"/o/headless-admin-user/v1.0/accounts",
						"/by-external-reference-code/", externalReferenceCode,
						"/account-roles",
						"?filter=name eq 'Account Administrator'")
				).build(
				).toUri())
		).getJSONArray(
			"items"
		).getJSONObject(
			0
		).getLong(
			"id"
		);

		post(
			jwt.toString(), "",
			UriComponentsBuilder.fromUriString(
				StringBundler.concat(
					"/o/headless-admin-user/v1.0/accounts",
					"/by-external-reference-code/", externalReferenceCode,
					"/account-roles/", id, "/user-accounts/by-email-address/",
					accountEmailAddress)
			).build(
			).toUri());

		return new ResponseEntity<>(HttpStatus.OK);
	}

}