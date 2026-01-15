/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.learn;

import com.liferay.client.extension.util.spring.boot3.BaseRestController;
import com.liferay.petra.string.StringBundler;

import org.json.JSONArray;
import org.json.JSONObject;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * @author Ana Beatriz Alves
 */
@RequestMapping("/object/action/lesson-friendly-url-update")
@RestController
public class ObjectActionLessonFriendlyUrlUpdateRestController
	extends BaseRestController {

	@Async
	public void handleCourseModuleUpdate(
		String jwt, String objectDefinitionExternalReferenceCode,
		long objectEntryId, String objectEntryNewTitle) {

		JSONArray objectEntryChildrenJSONArray = new JSONObject(
			get(
				jwt,
				UriComponentsBuilder.fromPath(
					"/o/c/p2s3modules"
				).queryParam(
					"filter",
					StringBundler.concat(
						objectDefinitionExternalReferenceCode.equals(
							"P2S3_COURSE") ?
								"r_p2s3CourseToP2S3Modules_c_p2s3CourseId" :
									"id",
						" eq '", objectEntryId, "'")
				).queryParam(
					"nestedFields",
					"p2s3ModuleToP2S3Lessons,p2s3ModuleToP2S3Quizzes"
				).queryParam(
					"pageSize", -1
				).build(
				).toUri())
		).getJSONArray(
			"items"
		);

		int index =
			objectDefinitionExternalReferenceCode.equals("P2S3_COURSE") ? 0 : 1;

		for (int i = 0; i < objectEntryChildrenJSONArray.length(); i++) {
			JSONObject childModuleJSONObject =
				objectEntryChildrenJSONArray.getJSONObject(i);

			if (childModuleJSONObject.has("p2s3ModuleToP2S3Lessons")) {
				JSONArray childLessonsJSONArray =
					childModuleJSONObject.getJSONArray(
						"p2s3ModuleToP2S3Lessons");

				for (int j = 0; j < childLessonsJSONArray.length(); j++) {
					_putObjectEntry(
						jwt, childLessonsJSONArray.getJSONObject(j),
						objectEntryNewTitle, index, "/o/c/p2s3lessons/");
				}
			}

			if (childModuleJSONObject.has("p2s3ModuleToP2S3Quizzes")) {
				JSONArray childQuizzesJSONArray =
					childModuleJSONObject.getJSONArray(
						"p2s3ModuleToP2S3Quizzes");

				for (int j = 0; j < childQuizzesJSONArray.length(); j++) {
					_putObjectEntry(
						jwt, childQuizzesJSONArray.getJSONObject(j),
						objectEntryNewTitle, index, "/o/c/p2s3quizzes/");
				}
			}
		}
	}

	@PostMapping
	public ResponseEntity<String> post(
		@AuthenticationPrincipal Jwt jwt, @RequestBody String json) {

		JSONObject jsonObject = new JSONObject(json);

		String objectDefinitionExternalReferenceCode = jsonObject.getString(
			"objectDefinitionExternalReferenceCode");
		JSONObject objectEntryJSONObject = jsonObject.getJSONObject(
			"objectEntry");

		String token = "Bearer " + jwt.getTokenValue();

		if (objectDefinitionExternalReferenceCode.equals("P2S3_COURSE") ||
			objectDefinitionExternalReferenceCode.equals("P2S3_MODULE")) {

			handleCourseModuleUpdate(
				token, objectDefinitionExternalReferenceCode,
				objectEntryJSONObject.getLong("id"),
				objectEntryJSONObject.getJSONObject(
					"title_i18n"
				).getString(
					"en_US"
				));
		}
		else if (objectDefinitionExternalReferenceCode.equals("P2S3_LESSON") ||
				 objectDefinitionExternalReferenceCode.equals("P2S3_QUIZZES")) {

			_putObjectEntry(
				token, objectEntryJSONObject,
				objectEntryJSONObject.getJSONObject(
					"title_i18n"
				).getString(
					"en_US"
				),
				2,
				objectDefinitionExternalReferenceCode.equals("P2S3_LESSON") ?
					"/o/c/p2s3lessons/" : "/o/c/p2s3quizzes/");
		}
		else {
			return new ResponseEntity<>(
				"Unsupported Object Type", HttpStatus.BAD_REQUEST);
		}

		return new ResponseEntity<>(json, HttpStatus.ACCEPTED);
	}

	private void _putObjectEntry(
		String jwt, JSONObject objectEntryJSONObject,
		String objectEntryNewTitle, int index, String apiEndpoint) {

		String currentObjectEntryFriendlyUrlPath =
			objectEntryJSONObject.optString("friendlyUrlPath", "");

		String[] friendlyUrlPathSegments =
			currentObjectEntryFriendlyUrlPath.split("/");

		if (friendlyUrlPathSegments.length == 3) {
			friendlyUrlPathSegments[index] = objectEntryNewTitle;

			String newObjectEntryFriendlyUrlPath = String.join(
				"/", friendlyUrlPathSegments);

			if (!newObjectEntryFriendlyUrlPath.equals(
					currentObjectEntryFriendlyUrlPath)) {

				objectEntryJSONObject.put(
					"friendlyUrlPath", newObjectEntryFriendlyUrlPath);

				put(
					jwt, objectEntryJSONObject.toString(),
					UriComponentsBuilder.fromPath(
						apiEndpoint + objectEntryJSONObject.getLong("id")
					).build(
					).toUri());
			}
		}
	}

}