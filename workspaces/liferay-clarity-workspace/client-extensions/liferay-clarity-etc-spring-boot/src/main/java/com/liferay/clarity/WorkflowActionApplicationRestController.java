/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.clarity;

import com.liferay.client.extension.util.spring.boot3.BaseRestController;

import java.util.Objects;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.json.JSONObject;

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
@RequestMapping("/workflow/action/application")
@RestController
public class WorkflowActionApplicationRestController
	extends BaseRestController {

	@PostMapping
	public ResponseEntity<String> post(
			@AuthenticationPrincipal Jwt jwt, @RequestBody String json)
		throws Exception {

		log(jwt, _log, json);

		JSONObject jsonObject = new JSONObject(json);

		String response = post(
			"Bearer " + jwt.getTokenValue(),
			"{\"transitionName\": \"" + _getTransitionName(jsonObject) + "\"}",
			jsonObject.getString("transitionURL"));

		if (_log.isInfoEnabled()) {
			_log.info("Output: " + response);
		}

		return new ResponseEntity<>(json, HttpStatus.OK);
	}

	private String _getTransitionName(JSONObject jsonObject) {
		JSONObject entryDTOJSONObject = jsonObject.getJSONObject("entryDTO");

		JSONObject applicationStateJSONObject =
			entryDTOJSONObject.getJSONObject("applicationState");

		String applicationStateKey = applicationStateJSONObject.getString(
			"key");

		if (Objects.equals(applicationStateKey, "approved") ||
			Objects.equals(applicationStateKey, "denied")) {

			return "review";
		}

		return "auto-approve";
	}

	private static final Log _log = LogFactory.getLog(
		WorkflowActionApplicationRestController.class);

}