/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.learn.service;

import com.liferay.client.extension.util.spring.boot3.service.BaseService;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.Validator;

import java.net.URI;

import java.text.Normalizer;

import java.util.regex.Pattern;

import org.json.JSONArray;
import org.json.JSONObject;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * @author Ana Beatriz Alves
 */
@Service
public class P2S3FriendlyURLService extends BaseService {

	@Async
	public void handleCourseUpdate(String jwt, JSONObject payloadJSONObject) {
		long courseId = payloadJSONObject.getLong("id");
		String newSlug = _toSlug(
			payloadJSONObject.getJSONObject(
				"values"
			).getString(
				"title"
			));

		JSONArray modulesJSONArray = _fetchChildrenJSONArray(
			jwt, "/o/c/p2s3modules", "r_p2s3CourseToP2S3Modules_c_p2s3CourseId",
			courseId);

		for (int i = 0; i < modulesJSONArray.length(); i++) {
			long moduleId = modulesJSONArray.getJSONObject(
				i
			).getLong(
				"id"
			);

			_updateLessonsInModule(jwt, moduleId, newSlug, 0);
		}
	}

	@Async
	public void handleLessonUpdate(String jwt, JSONObject payloadJSONObject) {
		JSONObject valuesJSONObject = payloadJSONObject.getJSONObject("values");

		String lessonTitle = valuesJSONObject.getString("title");

		long lessonModuleId = valuesJSONObject.getLong(
			"r_p2s3ModuleToP2S3Lessons_c_p2s3ModuleId");

		JSONObject moduleJSONObject = _fetchSingleJSONObject(
			jwt, "/o/c/p2s3modules/", lessonModuleId);

		long lessonCourseId = moduleJSONObject.getLong(
			"r_p2s3CourseToP2S3Modules_c_p2s3CourseId");

		JSONObject courseJSONObject = _fetchSingleJSONObject(
			jwt, "/o/c/p2s3courses/", lessonCourseId);

		String newPath = String.format(
			"%s/%s/%s", _toSlug(courseJSONObject.getString("title")),
			_toSlug(moduleJSONObject.getString("title")), _toSlug(lessonTitle));

		_patchLesson(
			jwt, payloadJSONObject.getLong("id"), newPath,
			valuesJSONObject.optString("friendlyUrlPath", ""));
	}

	@Async
	public void handleModuleUpdate(String jwt, JSONObject payloadJSONObject) {
		long moduleId = payloadJSONObject.getLong("id");

		String newSlug = _toSlug(
			payloadJSONObject.getJSONObject(
				"values"
			).getString(
				"title"
			));

		_updateLessonsInModule(jwt, moduleId, newSlug, 1);
	}

	private JSONArray _fetchChildrenJSONArray(
		String jwt, String endpoint, String filterField, long parentId) {

		URI uri = UriComponentsBuilder.fromPath(
			endpoint
		).queryParam(
			"filter", StringBundler.concat(filterField, " eq '", parentId, "'")
		).queryParam(
			"pageSize", -1
		).build(
		).toUri();

		return new JSONObject(
			get(jwt, uri)
		).getJSONArray(
			"items"
		);
	}

	private JSONObject _fetchSingleJSONObject(
		String jwt, String apiPath, long id) {

		URI uri = UriComponentsBuilder.fromPath(
			apiPath
		).pathSegment(
			String.valueOf(id)
		).build(
		).toUri();

		return new JSONObject(get(jwt, uri));
	}

	private void _patchLesson(
		String jwt, long id, String newPath, String oldPath) {

		if (newPath.equals(oldPath)) {
			return;
		}

		JSONObject payloadJSONObject = new JSONObject(
		).put(
			"friendlyUrlPath", newPath
		);
		URI uri = UriComponentsBuilder.fromPath(
			"/o/c/p2s3lessons/" + id
		).build(
		).toUri();

		patch(jwt, payloadJSONObject.toString(), uri);
	}

	private String _toSlug(String input) {
		if (Validator.isNull(input)) {
			return "untitled";
		}

		String normalized = Normalizer.normalize(input, Normalizer.Form.NFD);

		String nowhitespace = _whitespacePattern.matcher(
			normalized
		).replaceAll(
			"-"
		);

		return _nonlatinPattern.matcher(
			nowhitespace
		).replaceAll(
			""
		).toLowerCase(
			LocaleUtil.ENGLISH
		);
	}

	private void _updateLessonsInModule(
		String jwt, long moduleId, String newSlug, int segmentIndex) {

		JSONArray lessonsJSONArray = _fetchChildrenJSONArray(
			jwt, "/o/c/p2s3lessons", "r_p2s3ModuleToP2S3Lessons_c_p2s3ModuleId",
			moduleId);

		for (int i = 0; i < lessonsJSONArray.length(); i++) {
			JSONObject lessonJSONObject = lessonsJSONArray.getJSONObject(i);

			String currentPath = lessonJSONObject.optString(
				"friendlyUrlPath", "");

			String[] segments = currentPath.split("/");

			if (segments.length >= 3) {
				segments[segmentIndex] = newSlug;
				_patchLesson(
					jwt, lessonJSONObject.getLong("id"),
					String.join("/", segments), currentPath);
			}
		}
	}

	private static final Pattern _nonlatinPattern = Pattern.compile("[^\\w-]");
	private static final Pattern _whitespacePattern = Pattern.compile("[\\s]");

}