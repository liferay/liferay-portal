/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.learn;

import com.liferay.client.extension.util.spring.boot3.BaseRestController;
import com.liferay.learn.service.P2S3FriendlyURLService;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.json.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Ana Beatriz Alves
 */
@RequestMapping("/object/action/p2s3-friendly-url")
@RestController
public class ObjectActionP2S3FriendlyURLRestController
	extends BaseRestController {

	@PostMapping
	public ResponseEntity<String> post(
		@AuthenticationPrincipal Jwt jwt, @RequestBody String json) {

		JSONObject entryJSONObject = new JSONObject(json);

		String objectERC = entryJSONObject.getString(
			"objectDefinitionExternalReferenceCode");
		JSONObject entryPayloadJSONObject = entryJSONObject.getJSONObject(
			"objectEntry");

		String token = "Bearer " + jwt.getTokenValue();

		if (objectERC.equals("P2S3_COURSE")) {
			_p2s3FriendlyURLService.handleCourseUpdate(
				token, entryPayloadJSONObject);
		}
		else if (objectERC.equals("P2S3_MODULE")) {
			_p2s3FriendlyURLService.handleModuleUpdate(
				token, entryPayloadJSONObject);
		}
		else if (objectERC.equals("P2S3_LESSON")) {
			_p2s3FriendlyURLService.handleLessonUpdate(
				token, entryPayloadJSONObject);
		}
		else {
			if (_log.isWarnEnabled()) {
				_log.warn("Unsupported object ERC: " + objectERC);
			}

			return new ResponseEntity<>(
				"Unsupported Object Type", HttpStatus.BAD_REQUEST);
		}

		return new ResponseEntity<>(json, HttpStatus.OK);
	}

	private static final Log _log = LogFactory.getLog(
		ObjectActionP2S3FriendlyURLRestController.class);

	@Autowired
	private P2S3FriendlyURLService _p2s3FriendlyURLService;

}