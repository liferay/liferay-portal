/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.hcaptcha;

import com.liferay.client.extension.util.spring.boot3.BaseRestController;

import org.json.JSONObject;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

/**
 * @author Manuele Castro
 * @author Pedro Victor Silvestre
 */
@RequestMapping("/validation")
@RestController
public class ValidationRestController extends BaseRestController {

	public ValidationRestController(RestTemplateBuilder restTemplateBuilder) {
		_restTemplate = restTemplateBuilder.build();
	}

	@PostMapping
	public ResponseEntity<String> post(
		@AuthenticationPrincipal Jwt jwt, @RequestBody String json) {

		JSONObject responseJSONObject = new JSONObject();

		MultiValueMap<String, String> body = new LinkedMultiValueMap<>();

		JSONObject jsonObject = new JSONObject(json);

		body.add("remoteip", jsonObject.getString("remoteip"));
		body.add("response", jsonObject.getString("response"));

		body.add("secret", _secret);

		HttpHeaders httpHeaders = new HttpHeaders();

		httpHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

		ResponseEntity<String> responseEntity = _restTemplate.postForEntity(
			"https://api.hcaptcha.com/siteverify",
			new HttpEntity<>(body, httpHeaders), String.class);

		JSONObject siteVerifyJSONObject = new JSONObject(
			responseEntity.getBody());

		if (!siteVerifyJSONObject.getBoolean("success")) {
			responseJSONObject.put(
				"error-codes",
				siteVerifyJSONObject.getJSONArray("error-codes"));
		}

		responseJSONObject.put(
			"success", siteVerifyJSONObject.getBoolean("success"));

		return new ResponseEntity<>(
			responseJSONObject.toString(), HttpStatus.OK);
	}

	private final RestTemplate _restTemplate;

	@Value("${liferay.hcaptcha.secret}")
	private String _secret;

}