/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.learn;

import com.google.auth.oauth2.GoogleCredentials;

import com.liferay.client.extension.util.spring.boot3.BaseRestController;
import com.liferay.client.extension.util.spring.boot3.client.LiferayOAuth2AccessTokenManager;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.Validator;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.json.JSONArray;
import org.json.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * @author Nilton Vieira
 */
@RequestMapping("/learn")
@RestController
public class LearnRestController extends BaseRestController {

	@GetMapping("/lesson/{lessonId}/audio/base64")
	@ResponseBody
	public ResponseEntity<Object> getLessonAudioBase64(
		@AuthenticationPrincipal Jwt jwt, @PathVariable long lessonId,
		@RequestParam String languageCode, @RequestParam String voiceName) {

		try {
			JSONObject jsonObject = new JSONObject(
				get(
					_getAuthorization(),
					UriComponentsBuilder.fromPath(
						"/o/c/lessons/" + lessonId
					).queryParam(
						"fields", "contentRawText"
					).build(
					).toUri()));

			String contentRawText = jsonObject.getString("contentRawText");

			if (Validator.isNull(contentRawText)) {
				return ResponseEntity.status(
					HttpStatus.NOT_FOUND
				).body(
					"Lesson " + lessonId + " is missing readable text."
				);
			}

			ByteArrayOutputStream byteArrayOutputStream =
				new ByteArrayOutputStream();

			List<String> ssmls = _splitSsml(
				contentRawText.replaceAll("\\bLiferay\\b", "Life-ray"), 5000);

			for (String ssml : ssmls) {
				String response = post(
					_getGoogleAccessToken(),
					new JSONObject(
						HashMapBuilder.<String, Object>put(
							"audioConfig",
							HashMapBuilder.<String, Object>put(
								"audioEncoding", "MP3"
							).build()
						).put(
							"input",
							HashMapBuilder.<String, Object>put(
								"text", ssml
							).build()
						).put(
							"voice",
							HashMapBuilder.<String, Object>put(
								"languageCode", languageCode
							).put(
								"name", voiceName
							).build()
						).build()
					).toString(),
					UriComponentsBuilder.fromUriString(
						"https://texttospeech.googleapis.com/v1beta1/text:" +
							"synthesize"
					).build(
					).toUri());

				byteArrayOutputStream.write(
					Base64.getDecoder(
					).decode(
						new JSONObject(
							response
						).getString(
							"audioContent"
						)
					));
			}

			String audioContentBase64 = Base64.getEncoder(
			).encodeToString(
				byteArrayOutputStream.toByteArray()
			);

			return ResponseEntity.ok(audioContentBase64);
		}
		catch (Exception exception) {
			return ResponseEntity.status(
				500
			).body(
				"Error: " + exception.getMessage()
			);
		}
	}

	@GetMapping("/menu/items")
	@ResponseBody
	public ResponseEntity<Object> getMenuItems(
		@AuthenticationPrincipal Jwt jwt) {

		return new ResponseEntity<>(
			TransformUtil.transform(
				new JSONObject(
					get(
						_getAuthorization(),
						UriComponentsBuilder.fromPath(
							"/o/object-admin/v1.0/object-folders" +
								"/by-external-reference-code" +
									"/P2S3_LEARNING_MANAGEMENT_SYSTEM"
						).build(
						).toUri())
				).getJSONArray(
					"objectFolderItems"
				).toList(),
				this::_toMap),
			HttpStatus.OK);
	}

	@GetMapping("/{quizId}/questions")
	@ResponseBody
	public ResponseEntity<Object> getQuizQuestions(
			@AuthenticationPrincipal Jwt jwt, @PathVariable long quizId)
		throws Exception {

		return new ResponseEntity<>(
			new JSONObject(
				get(
					_getAuthorization(),
					UriComponentsBuilder.fromPath(
						"/o/c/quizquestions/scopes/" + _siteGroupId
					).queryParam(
						"filter", "quizId eq '" + quizId + "'"
					).queryParam(
						"fields",
						"id,position,question,questionType,quizAnswers," +
							"quizAnswers.answer,quizAnswers.id," +
								"quizAnswers.position"
					).queryParam(
						"nestedFields", "quizAnswers"
					).queryParam(
						"pageSize", "500"
					).queryParam(
						"sort", "position"
					).build(
					).toUri())
			).getJSONArray(
				"items"
			).toList(),
			HttpStatus.OK);
	}

	@PostMapping("/{quizId}/result")
	@ResponseBody
	public ResponseEntity<Object> postQuizResult(
			@AuthenticationPrincipal Jwt jwt, @PathVariable long quizId,
			@RequestBody String json)
		throws Exception {

		Map<String, Object> quizResultMap = _getQuizResult(
			new JSONObject(json),
			new JSONObject(
				get(
					_getAuthorization(),
					UriComponentsBuilder.fromPath(
						"/o/c/quizes/" + quizId
					).queryParam(
						"fields",
						StringBundler.concat(
							"id,r_quiz_c_moduleId,durationMinutes,passingScore",
							",isKnowledgeCheck,quizQuestions.id,quizQuestions.",
							"position,quizQuestions.question,quizQuestions.",
							"questionType,quizQuestions.questionTotalScore,",
							"quizQuestions.quizAnswers,quizQuestions.",
							"quizAnswers.id,quizQuestions.quizAnswers.position",
							",quizQuestions.quizAnswers.answer,quizQuestions.",
							"quizAnswers.score")
					).queryParam(
						"nestedFields", "quizQuestions,quizAnswers"
					).queryParam(
						"nestedFieldsDepth", "2"
					).queryParam(
						"pageSize", "500"
					).build(
					).toUri())));

		if (!GetterUtil.getBoolean(quizResultMap.get("isKnowledgeCheck")) &&
			GetterUtil.getBoolean(quizResultMap.get("passed")) &&
			(jwt != null)) {

			_postUserBadge(
				quizId,
				GetterUtil.getLong(
					jwt.getClaims(
					).get(
						"sub"
					)));
		}

		return ResponseEntity.ok(quizResultMap);
	}

	private String _getAuthorization() {
		return _liferayOAuth2AccessTokenManager.getAuthorization(
			"liferay-learn-etc-spring-boot-oauth-application-headless-server");
	}

	private String _getGoogleAccessToken() throws Exception {
		GoogleCredentials googleCredentials = GoogleCredentials.fromStream(
			new ByteArrayInputStream(_googleCredentials.getBytes())
		).createScoped(
			Collections.singletonList(
				"https://www.googleapis.com/auth/cloud-platform")
		);

		googleCredentials.refresh();

		String accessTokenValue = googleCredentials.getAccessToken(
		).getTokenValue();

		return "Bearer " + accessTokenValue;
	}

	private int _getQuizQuestionScore(
		Map<String, Object> answerMap, JSONObject quizQuestionJSONObject,
		JSONObject scoreSheetJSONObject) {

		JSONArray quizAnswersJSONArray = quizQuestionJSONObject.getJSONArray(
			"quizAnswers");

		scoreSheetJSONObject.put("questionsAnswers", quizAnswersJSONArray);

		boolean incorrectAnswer = false;

		for (int j = 0; j < quizAnswersJSONArray.length(); j++) {
			JSONObject quizAnswerJSONObject =
				quizAnswersJSONArray.getJSONObject(j);

			if (((quizAnswerJSONObject.getInt("score") > 0) &&
				 !GetterUtil.getBoolean(
					 answerMap.get(
						 String.valueOf(
							 quizAnswerJSONObject.getLong("id"))))) ||
				((quizAnswerJSONObject.getInt("score") <= 0) &&
				 GetterUtil.getBoolean(
					 answerMap.get(
						 String.valueOf(
							 quizAnswerJSONObject.getLong("id")))))) {

				incorrectAnswer = true;

				break;
			}
		}

		if (incorrectAnswer) {
			return 0;
		}

		return quizQuestionJSONObject.getInt("questionTotalScore");
	}

	private Map<String, Object> _getQuizResult(
		JSONObject quizAnswersJSONObject, JSONObject quizJSONObject) {

		JSONArray quizQuestionsJSONArray = quizJSONObject.getJSONArray(
			"quizQuestions");

		Map<String, Object> map = HashMapBuilder.<String, Object>put(
			"isKnowledgeCheck", false
		).put(
			"passingScore", quizJSONObject.getInt("passingScore")
		).put(
			"selectedAnswers", quizAnswersJSONObject.toMap()
		).put(
			"totalQuestions", quizQuestionsJSONArray.length()
		).build();

		float achievedQuizScore = 0;
		float totalQuizScore = 0;
		int totalPassedQuizQuestions = 0;

		JSONArray scoreSheetJSONArray = new JSONArray();

		for (int i = 0; i < quizQuestionsJSONArray.length(); i++) {
			JSONObject quizQuestionJSONObject =
				quizQuestionsJSONArray.getJSONObject(i);
			JSONObject scoreSheetJSONObject = new JSONObject();

			scoreSheetJSONObject.put(
				"questionId", quizQuestionJSONObject.getLong("id")
			).put(
				"questionTitle", quizQuestionJSONObject.getString("question")
			).put(
				"totalScore",
				quizQuestionJSONObject.getInt("questionTotalScore")
			).put(
				"type",
				quizQuestionJSONObject.getJSONObject(
					"questionType"
				).getString(
					"key"
				)
			);

			int quizQuestionScore = 0;

			if (Objects.equals(
					scoreSheetJSONObject.getString("type"),
					"selectMultipleChoice")) {

				JSONObject jsonObject = quizAnswersJSONObject.getJSONObject(
					String.valueOf(quizQuestionJSONObject.getLong("id")));

				scoreSheetJSONObject.put("selectedAnswer", jsonObject);

				quizQuestionScore = _getQuizQuestionScore(
					jsonObject.toMap(), quizQuestionJSONObject,
					scoreSheetJSONObject);
			}
			else {
				long id = quizAnswersJSONObject.getLong(
					String.valueOf(quizQuestionJSONObject.getLong("id")));

				scoreSheetJSONObject.put("selectedAnswer", id);

				quizQuestionScore = _getQuizQuestionScore(
					Collections.singletonMap(String.valueOf(id), true),
					quizQuestionJSONObject, scoreSheetJSONObject);
			}

			if (quizQuestionScore > 0) {
				totalPassedQuizQuestions++;
			}

			achievedQuizScore += quizQuestionScore;
			scoreSheetJSONObject.put("achievedScore", quizQuestionScore);
			totalQuizScore += quizQuestionJSONObject.getInt(
				"questionTotalScore");

			scoreSheetJSONArray.put(scoreSheetJSONObject);
		}

		if (quizJSONObject.getBoolean("isKnowledgeCheck")) {
			map.put("isKnowledgeCheck", true);
			map.put("scoreSheet", scoreSheetJSONArray.toList());
		}

		map.put(
			"passed",
			Math.round((achievedQuizScore / totalQuizScore) * 100) >=
				quizJSONObject.getInt("passingScore"));
		map.put("totalPassedQuestions", totalPassedQuizQuestions);
		map.put(
			"totalScore",
			Math.round((achievedQuizScore / totalQuizScore) * 100));

		return map;
	}

	private void _postUserBadge(long quizId, long userId) {
		JSONArray jsonArray = new JSONObject(
			get(
				_getAuthorization(),
				UriComponentsBuilder.fromPath(
					"/o/c/quizes/" + quizId + "/quizBadge"
				).queryParam(
					"fields", "id"
				).build(
				).toUri())
		).getJSONArray(
			"items"
		);

		if (jsonArray.isEmpty()) {
			return;
		}

		JSONObject badgeJSONObject = jsonArray.getJSONObject(0);

		JSONObject userBadgeJSONObject = new JSONObject(
			get(
				_getAuthorization(),
				UriComponentsBuilder.fromPath(
					"/o/c/userbadges/scopes/" + _siteGroupId
				).queryParam(
					"filter",
					StringBundler.concat(
						"userId eq '", userId, "' and badgeId eq ",
						badgeJSONObject.getLong("id"))
				).build(
				).toUri()));

		if (userBadgeJSONObject.getInt("totalCount") > 0) {
			return;
		}

		post(
			_getAuthorization(),
			new JSONObject(
			).put(
				"badgeId", badgeJSONObject.getLong("id")
			).put(
				"quizId", quizId
			).put(
				"r_userBadges_userId", userId
			).toString(),
			UriComponentsBuilder.fromPath(
				"/o/c/userbadges/scopes/" + _siteGroupId
			).build(
			).toUri());
	}

	private List<String> _splitSsml(String ssml, int maxLength) {
		List<String> parts = new ArrayList<>();

		StringBundler sb = new StringBundler();

		String ssmlContent = ssml.replaceFirst(
			"^<speak>", ""
		).replaceFirst(
			"</speak>$", ""
		).trim();

		String[] sentences = ssmlContent.split("(?<=[.!?])\\s+");

		for (String sentence : sentences) {
			if ((sb.length() + sentence.length()) > maxLength) {
				parts.add(
					sb.toString(
					).trim());
				sb = new StringBundler();
			}

			sb.append(
				sentence
			).append(
				" "
			);
		}

		if (sb.length() > 0) {
			parts.add(
				sb.toString(
				).trim());
		}

		return parts;
	}

	private Map<String, Object> _toMap(Object object) {
		Map<String, Object> map = (Map<String, Object>)object;

		if (!map.containsKey("objectDefinition")) {
			return null;
		}

		Map<String, Object> objectDefinitionMap = (Map<String, Object>)map.get(
			"objectDefinition");

		return HashMapBuilder.<String, Object>put(
			"externalReferenceCode",
			objectDefinitionMap.get("externalReferenceCode")
		).put(
			"id", objectDefinitionMap.get("id")
		).put(
			"title", objectDefinitionMap.get("pluralLabel")
		).build();
	}

	@Value("${liferay.learn.google.credentials}")
	private String _googleCredentials;

	@Autowired
	private LiferayOAuth2AccessTokenManager _liferayOAuth2AccessTokenManager;

	@Value("${liferay.learn.dxp.site.group.id}")
	private long _siteGroupId;

}