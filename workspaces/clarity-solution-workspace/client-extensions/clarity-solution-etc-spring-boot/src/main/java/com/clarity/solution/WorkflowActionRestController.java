/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.clarity.solution;

import com.liferay.client.extension.util.spring.boot3.BaseRestController;

import java.util.Objects;

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
 * @author Brian Wing Shun Chan
 * @author David Nebinger
 * @author Mumen Tayyem
 */
@RequestMapping("/workflow/action")
@RestController
public class WorkflowActionRestController extends BaseRestController {

	@PostMapping
	public ResponseEntity<String> post(
		@AuthenticationPrincipal Jwt jwt, @RequestBody String json) {

		JSONObject jsonObject = new JSONObject(json);

		post(
			"Bearer " + jwt.getTokenValue(),
			new JSONObject(
			).put(
				"transitionName", _getTransitionName(jsonObject)
			).toString(),
			UriComponentsBuilder.fromPath(
				jsonObject.getString("transitionURL")
			).build(
			).toUri());

		return new ResponseEntity<>(json, HttpStatus.OK);
	}

	private String _getTransitionName(JSONObject jsonObject) {
		String key = jsonObject.getJSONObject(
			"entryDTO"
		).getJSONObject(
			"applicationState"
		).getString(
			"key"
		);

		if (Objects.equals(key, "approved") || Objects.equals(key, "denied")) {
			return "review";
		}

		return "auto-approve";
	}

}