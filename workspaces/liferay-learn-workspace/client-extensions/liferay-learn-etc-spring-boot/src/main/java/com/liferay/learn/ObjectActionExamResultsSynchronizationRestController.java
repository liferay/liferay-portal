/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.learn;

import com.liferay.client.extension.util.spring.boot3.BaseRestController;
import com.liferay.client.extension.util.spring.boot3.client.LiferayOAuth2AccessTokenManager;
import com.liferay.portal.kernel.util.GetterUtil;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.json.JSONArray;
import org.json.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * @author Nilton Vieira
 */
@RequestMapping("/object/action/exam/results/synchronization")
@RestController
public class ObjectActionExamResultsSynchronizationRestController
	extends BaseRestController {

	@PostMapping
	public ResponseEntity<String> post(
		@AuthenticationPrincipal Jwt jwt, @RequestBody String json) {

		if (_log.isInfoEnabled()) {
			_log.info("Started exam results synchronization");
		}

		int examResultAmount = 0;
		long startTime = System.currentTimeMillis();
		String status = "Failed";

		try {
			OffsetDateTime offsetDateTime =
				_getLatestSuccessfulExecutionOffsetDateTime();

			while (offsetDateTime.isBefore(
						OffsetDateTime.now(ZoneOffset.UTC))) {

				examResultAmount += _importExamResults(offsetDateTime);

				offsetDateTime = offsetDateTime.plusDays(7);
			}

			status = "Successful";
		}
		catch (WebClientResponseException webClientResponseException) {
			_log.error(webClientResponseException.getResponseBodyAsString());
		}
		finally {
			_updateExamResultsSynchronization(
				new JSONObject(
					json
				).getLong(
					"classPK"
				),
				examResultAmount, System.currentTimeMillis() - startTime,
				status);
		}

		if (_log.isInfoEnabled()) {
			_log.info("Finished exam results synchronization");
		}

		return new ResponseEntity<>(json, HttpStatus.OK);
	}

	private String _getAuthorization() {
		return _liferayOAuth2AccessTokenManager.getAuthorization(
			"liferay-learn-etc-spring-boot-oahs");
	}

	private OffsetDateTime _getLatestSuccessfulExecutionOffsetDateTime() {
		JSONObject jsonObject = new JSONObject(
			get(
				_getAuthorization(),
				UriComponentsBuilder.fromPath(
					"/o/c/p2s3examresultssynchronizations"
				).queryParam(
					"fields", "dateCreated"
				).queryParam(
					"filter", "synchronizationStatus eq 'Successful'"
				).queryParam(
					"pageSize", 1
				).queryParam(
					"sort", "dateCreated:desc"
				).build(
				).toUri()));

		JSONArray itemsJSONArray = jsonObject.getJSONArray("items");

		if (itemsJSONArray.isEmpty()) {
			return OffsetDateTime.of(
				GetterUtil.getInteger(
					System.getenv("LIFERAY_LEARN_ETC_SPRING_BOOT_START_YEAR")),
				1, 1, 0, 0, 0, 0, ZoneOffset.UTC);
		}

		JSONObject itemJSONObject = itemsJSONArray.getJSONObject(0);

		return OffsetDateTime.parse(itemJSONObject.getString("dateCreated"));
	}

	private String _getPayload(JSONObject jsonObject) {
		return new JSONObject(
		).put(
			"date",
			OffsetDateTime.parse(
				jsonObject.getString("date")
			).atZoneSameInstant(
				ZoneOffset.UTC
			).format(
				DateTimeFormatter.ofPattern("uuuu-MM-dd'T'HH:mm:ssX")
			)
		).put(
			"email",
			jsonObject.getJSONObject(
				"simpleRegistration"
			).getJSONObject(
				"candidate"
			).getString(
				"email"
			)
		).put(
			"examName", jsonObject.getString("examName")
		).put(
			"externalReferenceCode", jsonObject.getLong("id")
		).put(
			"firstName",
			jsonObject.getJSONObject(
				"simpleRegistration"
			).getJSONObject(
				"candidate"
			).getString(
				"firstName"
			)
		).put(
			"lastName",
			jsonObject.getJSONObject(
				"simpleRegistration"
			).getJSONObject(
				"candidate"
			).getString(
				"lastName"
			)
		).put(
			"result", jsonObject.getString("passFail")
		).put(
			"score", jsonObject.getDouble("score")
		).put(
			"testName",
			jsonObject.getJSONObject(
				"simpleRegistration"
			).getString(
				"testName"
			)
		).toString();
	}

	private int _importExamResults(OffsetDateTime offsetDateTime) {
		JSONArray jsonArray = new JSONArray(
			post(
				null,
				new JSONObject(
				).put(
					"endDate",
					offsetDateTime.plusDays(
						7
					).format(
						DateTimeFormatter.ofPattern("uuuu-MM-dd HH:mm:ss")
					)
				).put(
					"requestType", "GET TRANSCRIPTS BY DATE RANGE"
				).put(
					"returnFormat", "JSON"
				).put(
					"securityToken", _webassessorSecurityToken
				).put(
					"startDate",
					offsetDateTime.format(
						DateTimeFormatter.ofPattern("uuuu-MM-dd HH:mm:ss"))
				).toString(),
				UriComponentsBuilder.fromPath(
					"/WebAssessorWebServices/jaxrs/wawebservices/processRequest"
				).host(
					"webassessor.com"
				).scheme(
					"https"
				).build(
				).toUri()));

		if (jsonArray.get(0) instanceof String) {
			return 0;
		}

		for (int i = 0; i < jsonArray.length(); i++) {
			JSONObject jsonObject1 = jsonArray.getJSONObject(i);

			JSONObject jsonObject2 = new JSONObject(
				put(
					_getAuthorization(), _getPayload(jsonObject1),
					UriComponentsBuilder.fromPath(
						"/o/c/p2s3examresults/by-external-reference-code/" +
							jsonObject1.getLong("id")
					).build(
					).toUri()));

			put(
				_getAuthorization(),
				new JSONArray(
				).put(
					new JSONObject(
					).put(
						"actionIds",
						new JSONArray(
						).put(
							"VIEW"
						)
					).put(
						"roleName", "Guest"
					)
				).toString(),
				UriComponentsBuilder.fromPath(
					"/o/c/p2s3examresults/" + jsonObject2.getLong("id") +
						"/permissions"
				).build(
				).toUri());
		}

		return jsonArray.length();
	}

	private void _updateExamResultsSynchronization(
		Long classPK, int examResultAmount, long executionTime,
		String synchronizationStatus) {

		patch(
			_getAuthorization(),
			new JSONObject(
			).put(
				"examResultAmount", examResultAmount
			).put(
				"executionTime", executionTime
			).put(
				"synchronizationStatus", synchronizationStatus
			).toString(),
			UriComponentsBuilder.fromPath(
				"/o/c/p2s3examresultssynchronizations/" + classPK
			).build(
			).toUri());
	}

	private static final Log _log = LogFactory.getLog(
		ObjectActionExamResultsSynchronizationRestController.class);

	@Autowired
	private LiferayOAuth2AccessTokenManager _liferayOAuth2AccessTokenManager;

	@Value("${liferay.oauth.application.external.reference.codes}")
	private String _liferayOAuthApplicationExternalReferenceCodes;

	@Value("${liferay.learn.webassessor.security.token}")
	private String _webassessorSecurityToken;

}