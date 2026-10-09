/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.learn;

import com.liferay.client.extension.util.spring.boot3.BaseRestController;
import com.liferay.petra.string.StringBundler;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.json.JSONArray;
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
 * @author José Abelenda
 */
@RequestMapping("/object/action/course")
@RestController
public class ObjectActionCourseRestController extends BaseRestController {

	@PostMapping
	public ResponseEntity<String> post(
		@AuthenticationPrincipal Jwt jwt, @RequestBody String json) {

		JSONObject responseJSONObject = new JSONObject(
			get(
				"Bearer " + jwt.getTokenValue(),
				UriComponentsBuilder.fromPath(
					"/o/c/p2s3courses"
				).queryParam(
					"fields",
					StringBundler.concat(
						"id,p2s3CourseToP2S3Modules.lessonDurationMinutes,",
						"p2s3CourseToP2S3Modules.lessons,",
						"p2s3CourseToP2S3Modules.quizDurationMinutes,",
						"p2s3CourseToP2S3Modules.quizzes")
				).queryParam(
					"filter",
					"p2s3CourseToP2S3Modules/id eq '" + _getModuleId(json) + "'"
				).queryParam(
					"nestedFields", "p2s3CourseToP2S3Modules"
				).build(
				).toUri()));

		JSONArray itemsJSONArray = responseJSONObject.getJSONArray("items");

		JSONObject itemJSONObject = itemsJSONArray.getJSONObject(0);

		patch(
			"Bearer " + jwt.getTokenValue(),
			_getPayloadJSONObject(
				itemJSONObject.getJSONArray("p2s3CourseToP2S3Modules")
			).toString(),
			UriComponentsBuilder.fromPath(
				"/o/c/p2s3courses/" + itemJSONObject.getLong("id")
			).build(
			).toUri());

		if (_log.isInfoEnabled()) {
			_log.info("Updated course " + itemJSONObject.getLong("id"));
		}

		return new ResponseEntity<>(json, HttpStatus.OK);
	}

	private long _getModuleId(String json) {
		JSONObject jsonObject = new JSONObject(json);

		JSONObject objectEntryJSONObject = jsonObject.getJSONObject(
			"objectEntry");

		JSONObject valuesJSONObject = objectEntryJSONObject.getJSONObject(
			"values");

		if (valuesJSONObject.has("r_p2s3ModuleToP2S3Lessons_c_p2s3ModuleId")) {
			return valuesJSONObject.getLong(
				"r_p2s3ModuleToP2S3Lessons_c_p2s3ModuleId");
		}

		return valuesJSONObject.getLong(
			"r_p2s3ModuleToP2S3Quizzes_c_p2s3ModuleId");
	}

	private JSONObject _getPayloadJSONObject(JSONArray moduleJSONArray) {
		long durationMinutes = 0;
		long totalAssets = 0;

		for (int i = 0; i < moduleJSONArray.length(); i++) {
			JSONObject moduleJSONObject = moduleJSONArray.getJSONObject(i);

			durationMinutes += moduleJSONObject.getLong(
				"lessonDurationMinutes");
			durationMinutes += moduleJSONObject.getLong("quizDurationMinutes");

			if (moduleJSONObject.has("lessons")) {
				totalAssets += moduleJSONObject.getInt("lessons");
			}

			if (!moduleJSONObject.has("quizzes")) {
				continue;
			}

			totalAssets += moduleJSONObject.getInt("quizzes");
		}

		return new JSONObject(
		).put(
			"durationMinutes", durationMinutes
		).put(
			"totalAssets", totalAssets
		);
	}

	private static final Log _log = LogFactory.getLog(
		ObjectActionCourseRestController.class);

}