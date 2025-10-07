/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.learn;

import com.liferay.client.extension.util.spring.boot3.BaseRestController;
import com.liferay.client.extension.util.spring.boot3.client.LiferayOAuth2AccessTokenManager;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

import org.json.JSONArray;
import org.json.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * @author Lucas Emanuel
 */
@RequestMapping("/course-progress/download")
@RestController
public class CourseProgressDownloadRestController extends BaseRestController {

	@GetMapping
	@ResponseBody
	public ResponseEntity<StreamingResponseBody> get(
			@AuthenticationPrincipal Jwt jwt,
			@RequestParam(required = false, value = "endDate") String endDate,
			@RequestParam(required = false, value = "startDate") String
				startDate)
		throws IOException {

		return ResponseEntity.ok(
		).header(
			"Content-Disposition",
			"attachment; filename=\"course_progress.csv\""
		).body(
			new StreamingResponseBody() {

				@Override
				public void writeTo(OutputStream outputStream)
					throws IOException {

					_write(endDate, outputStream, startDate);
				}

			}
		);
	}

	private String _getAuthorization() {
		return _liferayOAuth2AccessTokenManager.getAuthorization(
			"liferay-learn-etc-spring-boot-oahs");
	}

	private boolean _isBetween(
		String dateString, String endDateString, String startDateString) {

		if ((dateString == null) ||
			((startDateString == null) && (endDateString == null))) {

			return true;
		}

		LocalDate localDate = LocalDate.parse(
			dateString, DateTimeFormatter.ISO_DATE_TIME);

		if (startDateString != null) {
			LocalDate startLocalDate = LocalDate.parse(startDateString);

			if (localDate.isBefore(startLocalDate)) {
				return false;
			}
		}

		if (endDateString != null) {
			LocalDate endLocalDate = LocalDate.parse(endDateString);

			return !localDate.isAfter(endLocalDate);
		}

		return true;
	}

	private void _loadCourseQuizzes() {
		_courseQuizzes = new HashMap<>();

		int lastPage = 1;

		for (int i = 1; i <= lastPage; i++) {
			JSONObject jsonObject = new JSONObject(
				get(
					_getAuthorization(),
					UriComponentsBuilder.fromUriString(
						"/o/c/quizes/"
					).queryParam(
						"fields",
						"id,r_quiz_c_module,r_quiz_c_module.r_module_c_courseId"
					).queryParam(
						"filter", "isKnowledgeCheck eq false"
					).queryParam(
						"nestedFields", "module"
					).queryParam(
						"page", i
					).queryParam(
						"pageSize", 500
					).build(
					).toUri()));

			lastPage = jsonObject.optInt("lastPage", 1);

			JSONArray jsonArray = jsonObject.getJSONArray("items");

			for (int j = 0; j < jsonArray.length(); j++) {
				JSONObject quizJSONObject = jsonArray.getJSONObject(j);

				JSONObject moduleJSONObject = quizJSONObject.optJSONObject(
					"r_quiz_c_module", null);

				if (moduleJSONObject == null) {
					continue;
				}

				_courseQuizzes.put(
					moduleJSONObject.getLong("r_module_c_courseId"),
					quizJSONObject.getLong("id"));
			}
		}
	}

	private void _write(
			String endDate, OutputStream outputStream, String startDate)
		throws IOException {

		_loadCourseQuizzes();

		try (CSVPrinter csvPrinter = new CSVPrinter(
				new BufferedWriter(new OutputStreamWriter(outputStream)),
				CSVFormat.DEFAULT.builder(
				).setHeader(
					"First Name", "Last Name", "Work Email", "Course Name",
					"Completion Status", "% Complete", "User Group"
				).build())) {

			int lastPage = 1;

			for (int i = 1; i <= lastPage; i++) {
				JSONObject jsonObject = new JSONObject(
					get(
						_getAuthorization(),
						UriComponentsBuilder.fromUriString(
							"/o/c/enrollments/"
						).queryParam(
							"nestedFields", "course,user"
						).queryParam(
							"page", i
						).queryParam(
							"pageSize", 500
						).build(
						).toUri()));

				lastPage = jsonObject.optInt("lastPage", 1);

				JSONArray jsonArray = jsonObject.getJSONArray("items");

				for (int j = 0; j < jsonArray.length(); j++) {
					JSONObject enrollmentJSONObject = jsonArray.getJSONObject(
						j);

					JSONObject courseJSONObject =
						enrollmentJSONObject.optJSONObject(
							"r_courseEnrollment_c_course");
					JSONObject userJSONObject =
						enrollmentJSONObject.optJSONObject(
							"r_userenrollments_user");

					if ((courseJSONObject == null) ||
						(userJSONObject == null)) {

						continue;
					}

					String modifiedDate = enrollmentJSONObject.optString(
						"dateModified", null);

					if (!_isBetween(modifiedDate, endDate, startDate)) {
						continue;
					}

					float totalAssets = courseJSONObject.optInt(
						"totalAssets", 0);

					if (totalAssets == 0) {
						continue;
					}

					List<String> userGroupNames = new ArrayList<>();

					JSONArray userGroupBriefsJSONArray =
						userJSONObject.optJSONArray("userGroupBriefs");

					if (userGroupBriefsJSONArray != null) {
						for (int k = 0; k < userGroupBriefsJSONArray.length();
							 k++) {

							userGroupNames.add(
								userGroupBriefsJSONArray.getJSONObject(
									k
								).optString(
									"name", ""
								));
						}
					}

					String completedAssetIds = enrollmentJSONObject.optString(
						"completedAssetIds", ""
					).replaceFirst(
						"^,", ""
					);

					Long quizId = _courseQuizzes.get(
						enrollmentJSONObject.optLong(
							"r_courseEnrollment_c_courseId", 0));

					float progress = 0;

					if (completedAssetIds.contains(String.valueOf(quizId))) {
						progress = 100;
					}
					else {
						List<String> completedAssets =
							completedAssetIds.isBlank() ?
								Collections.emptyList() :
									Arrays.asList(completedAssetIds.split(","));

						progress =
							((float)completedAssets.size() / totalAssets) * 100;
					}

					String[] fullName = userJSONObject.optString(
						"name", ""
					).split(
						" ", 2
					);

					csvPrinter.printRecord(
						(fullName.length > 0) ? fullName[0] : "",
						(fullName.length > 1) ? fullName[1] : "",
						userJSONObject.optString("emailAddress", ""),
						courseJSONObject.optString("title", ""),
						(progress >= 100) ? "completed" : "in progress",
						String.format("%.2f", progress),
						String.join(" | ", userGroupNames));
				}
			}

			csvPrinter.flush();
		}
		catch (Exception exception) {
			throw new IOException(exception);
		}
	}

	private Map<Long, Long> _courseQuizzes;

	@Autowired
	private LiferayOAuth2AccessTokenManager _liferayOAuth2AccessTokenManager;

}