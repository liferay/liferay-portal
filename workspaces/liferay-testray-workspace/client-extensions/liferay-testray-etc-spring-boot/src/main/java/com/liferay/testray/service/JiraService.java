/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.testray.service;

import com.liferay.client.extension.util.spring.boot3.client.LiferayOAuth2AccessTokenManager;
import com.liferay.client.extension.util.spring.boot3.service.BaseService;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;

import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.json.JSONArray;
import org.json.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * @author Nilton Vieira
 */
@Component
@EnableScheduling
public class JiraService extends BaseService {

	@Scheduled(cron = "${liferay.testray.jira.sync.cron}")
	public void scheduledSyncJiraIssues() {
		if (_log.isInfoEnabled()) {
			_log.info("Syncing Jira issues");
		}

		syncJiraIssues();
	}

	public void syncJiraIssues() {
		for (int page = 1;; page++) {
			JSONObject jsonObject = new JSONObject(
				get(
					_getLiferayAuthorization(),
					UriComponentsBuilder.fromPath(
						"/o/c/jiraissues"
					).queryParam(
						"fields", "externalReferenceCode"
					).queryParam(
						"filter", "issueType eq null or issueType eq ''"
					).queryParam(
						"page", "{page}"
					).queryParam(
						"pageSize", 50
					).build(
						page
					)));

			JSONArray jsonArray = jsonObject.getJSONArray("items");

			if (jsonArray.isEmpty()) {
				return;
			}

			JSONObject jiraQueryJSONObject = new JSONObject(
				get(
					_getJiraAuthorization(),
					UriComponentsBuilder.fromHttpUrl(
						"https://api.atlassian.com/ex/jira/{cloudId}/rest/api" +
							"/3/search"
					).queryParam(
						"expand", "renderedFields"
					).queryParam(
						"fields", "description,issuetype,parent,project,summary"
					).queryParam(
						"jql", "issuekey in ({issueKeys})"
					).build(
						_liferayTestrayJiraCloudId,
						StringUtil.merge(
							TransformUtil.transform(
								jsonArray.toList(),
								item -> {
									Map<String, Object> map =
										(Map<String, Object>)item;

									return map.get("externalReferenceCode");
								}))
					)));

			JSONArray issuesJSONArray = jiraQueryJSONObject.getJSONArray(
				"issues");

			for (int i = 0; i < issuesJSONArray.length(); i++) {
				JSONObject issueJSONObject = issuesJSONArray.getJSONObject(i);

				_importTestrayJiraIssue(issueJSONObject);
			}

			if (page == jsonObject.getInt("lastPage")) {
				break;
			}
		}
	}

	public JSONObject updateJiraIssue(String issueKey, String json) {
		JSONObject jsonObject = new JSONObject(json);

		return new JSONObject(
			put(
				_getJiraAuthorization(),
				new JSONObject(
				).put(
					"fields",
					new JSONObject(
					).put(
						"customfield_10202",
						jsonObject.getJSONArray("testrayCaseNames")
					)
				).toString(),
				UriComponentsBuilder.fromHttpUrl(
					"https://api.atlassian.com/ex/jira/{cloudId}/rest/api/3" +
						"/issue/{issueKey}"
				).build(
					_liferayTestrayJiraCloudId, issueKey
				)));
	}

	private String _getJiraAuthorization() {
		return _jiraOAuthService.getAuthorization();
	}

	private String _getLiferayAuthorization() {
		return _liferayOAuth2AccessTokenManager.getAuthorization(
			"liferay-testray-etc-spring-boot-oahs");
	}

	private String _getNestedKey(JSONObject jsonObject, String... keys) {
		if (!jsonObject.has(keys[0])) {
			return StringPool.BLANK;
		}

		if (keys.length == 1) {
			return jsonObject.getString(keys[0]);
		}

		return _getNestedKey(
			jsonObject.getJSONObject(keys[0]),
			ArrayUtil.subset(keys, 1, keys.length));
	}

	private Map<String, String> _getParentIssuesMap(String parentKey) {
		if (Validator.isNull(parentKey)) {
			return HashMapBuilder.put(
				"r_epic_c_jiraIssueERC", ""
			).put(
				"r_initiative_c_jiraIssueERC", ""
			).put(
				"r_parentIssue_c_jiraIssueERC", ""
			).put(
				"r_story_c_jiraIssueERC", ""
			).put(
				"r_task_c_jiraIssueERC", ""
			).build();
		}

		try {
			JSONObject jsonObject = new JSONObject(
				get(
					_getLiferayAuthorization(),
					UriComponentsBuilder.fromPath(
						"/o/c/jiraissues/by-external-reference-code/{parentKey}"
					).build(
						parentKey
					)));

			return HashMapBuilder.put(
				"r_epic_c_jiraIssueERC", jsonObject.getString("epicERC")
			).put(
				"r_initiative_c_jiraIssueERC",
				jsonObject.getString("initiativeERC")
			).put(
				"r_parentIssue_c_jiraIssueERC",
				jsonObject.getString("externalReferenceCode")
			).put(
				"r_story_c_jiraIssueERC", jsonObject.getString("storyERC")
			).put(
				"r_task_c_jiraIssueERC", jsonObject.getString("taskERC")
			).put(
				_getRelationshipName(
					_getNestedKey(jsonObject, "issueType", "name")),
				jsonObject.getString("externalReferenceCode")
			).build();
		}
		catch (WebClientResponseException webClientResponseException) {
			if (_log.isDebugEnabled()) {
				_log.debug(webClientResponseException);
			}

			JSONObject jsonObject = new JSONObject(
				get(
					_getJiraAuthorization(),
					UriComponentsBuilder.fromHttpUrl(
						"https://api.atlassian.com/ex/jira/{cloudId}/rest/api" +
							"/3/issue/{parentKey}"
					).queryParam(
						"expand", "renderedFields"
					).queryParam(
						"fields", "description,issuetype,parent,project,summary"
					).build(
						_liferayTestrayJiraCloudId, parentKey
					)));

			return _importTestrayJiraIssue(jsonObject);
		}
	}

	private String _getRelationshipName(String issueType) {
		return "r_" + StringUtil.lowerCase(issueType) + "_c_jiraIssueERC";
	}

	private Map<String, String> _importTestrayJiraIssue(JSONObject jsonObject) {
		Map<String, String> map = _getParentIssuesMap(
			_getNestedKey(jsonObject, "fields", "parent", "key"));

		String issueType = StringUtil.removeSubstring(
			StringUtil.upperCase(
				_getNestedKey(jsonObject, "fields", "issuetype", "name")),
			" ");

		if (!ArrayUtil.contains(_ALLOWED_ISSUE_TYPES, issueType)) {
			issueType = "UNDEFINED";
		}

		put(
			_getLiferayAuthorization(),
			new JSONObject(
			).put(
				"description",
				_getNestedKey(jsonObject, "renderedFields", "description")
			).put(
				"issueType", issueType
			).put(
				"r_epic_c_jiraIssueERC", map.get("r_epic_c_jiraIssueERC")
			).put(
				"r_initiative_c_jiraIssueERC",
				map.get("r_initiative_c_jiraIssueERC")
			).put(
				"r_jiraProjectToJiraIssue_c_jiraProjectERC",
				_getNestedKey(jsonObject, "fields", "project", "key")
			).put(
				"r_parentIssue_c_jiraIssueERC",
				map.get("r_parentIssue_c_jiraIssueERC")
			).put(
				"r_story_c_jiraIssueERC", map.get("r_story_c_jiraIssueERC")
			).put(
				"r_task_c_jiraIssueERC", map.get("r_task_c_jiraIssueERC")
			).put(
				"title", _getNestedKey(jsonObject, "fields", "summary")
			).toString(),
			UriComponentsBuilder.fromPath(
				"/o/c/jiraissues/by-external-reference-code/{issueKey}"
			).build(
				jsonObject.getString("key")
			));

		map.put("r_parentIssue_c_jiraIssueERC", jsonObject.getString("key"));
		map.put(
			_getRelationshipName(
				_getNestedKey(jsonObject, "fields", "issuetype", "name")),
			jsonObject.getString("key"));

		return map;
	}

	private static final String[] _ALLOWED_ISSUE_TYPES = {
		"BUG", "EPIC", "IMPEDIBUG", "INITIATIVE", "STORY", "TASK",
		"TECHNICALTASK"
	};

	private static final Log _log = LogFactory.getLog(JiraService.class);

	@Autowired
	private JiraOAuthService _jiraOAuthService;

	@Autowired
	private LiferayOAuth2AccessTokenManager _liferayOAuth2AccessTokenManager;

	@Value("${liferay.testray.jira.cloud.id}")
	private String _liferayTestrayJiraCloudId;

}