/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.one.service;

import com.liferay.client.extension.util.spring.boot3.service.BaseService;
import com.liferay.one.constants.JiraIssueConstants;
import com.liferay.one.converter.BusinessEventConverter;
import com.liferay.one.converter.BusinessEventVersionConverter;
import com.liferay.one.model.BusinessEvent;
import com.liferay.one.model.BusinessEventVersion;
import com.liferay.one.model.JiraSupportIssue;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;

import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.json.JSONArray;
import org.json.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * @author Jenny Chen
 */
@Component
public class JiraService extends BaseService {

	public void createBusinessEvent(BusinessEvent businessEvent)
		throws Exception {

		_syncBusinessEvent(businessEvent, null);
	}

	public void deleteBusinessEvent(String id) throws Exception {
		_deleteAssetObjectJSONObject(id, _jiraWorkspaceId);
	}

	public String getAccountObjectKey(String externalKey) throws Exception {
		JSONObject accountResponseJSONObject =
			_searchAccountByExternalKeyJSONObject(externalKey);

		JSONArray valuesJSONArray = accountResponseJSONObject.getJSONArray(
			"values");

		JSONObject accountJSONObject = valuesJSONArray.getJSONObject(0);

		return accountJSONObject.getString("objectKey");
	}

	public BusinessEvent getBusinessEvent(String id) throws Exception {
		return _businessEventConverter.toBusinessEvent(
			StringPool.BLANK, _getAssetObjectJSONObject(id, _jiraWorkspaceId));
	}

	public List<BusinessEventVersion> getBusinessEventVersions(
			String businessEventId)
		throws Exception {

		List<BusinessEventVersion> businessEventVersions = new ArrayList<>();

		String aql = StringBundler.concat(
			"objectSchema = \"Business Events\" AND objectType = \"Business ",
			"Event Version\" AND \"Business Event\" = ", businessEventId,
			" ORDER BY Updated DESC");

		JSONArray assetsObjectsJSONArray = _searchAssetsObjectsJSONArray(
			aql, _jiraWorkspaceId);

		if (assetsObjectsJSONArray != null) {
			for (int i = 0; i < assetsObjectsJSONArray.length(); i++) {
				businessEventVersions.add(
					_businessEventVersionConverter.toBusinessEventVersion(
						assetsObjectsJSONArray.getJSONObject(i)));
			}
		}

		return businessEventVersions;
	}

	public List<BusinessEvent> getBusinessEvents(
			String accountExternalReferenceCode)
		throws Exception {

		List<BusinessEvent> businessEvents = new ArrayList<>();

		String aql = StringBundler.concat(
			"objectSchema = \"Business Events\" AND objectType = \"Business ",
			"Event\" AND \"Account\".\"External Key\" = \"",
			accountExternalReferenceCode, "\"");

		JSONArray assetsObjectsJSONArray = _searchAssetsObjectsJSONArray(
			aql, _jiraWorkspaceId);

		if (assetsObjectsJSONArray != null) {
			for (int i = 0; i < assetsObjectsJSONArray.length(); i++) {
				businessEvents.add(
					_businessEventConverter.toBusinessEvent(
						accountExternalReferenceCode,
						assetsObjectsJSONArray.getJSONObject(i)));
			}
		}

		return businessEvents;
	}

	public List<JiraSupportIssue> getJSMJiraSupportIssues(
			String externalReferenceCode, String[] issueKeys)
		throws Exception {

		StringBundler sb = new StringBundler(12);

		sb.append("Organization in aqlFunction('\"External Key\" = \"");
		sb.append(externalReferenceCode);
		sb.append("\"') and (status not in ('");
		sb.append(
			StringUtil.merge(
				JiraIssueConstants.STATUSES_SOLVED_AND_CLOSED, "','"));
		sb.append("')) and ");
		sb.append(
			JiraIssueConstants.toJQLCustomField(
				_jiraSupportHCFieldRequestType));
		sb.append(" = '");
		sb.append(JiraIssueConstants.TYPE_GENERAL_REQUEST);
		sb.append("'");

		if (ArrayUtil.isNotEmpty(issueKeys)) {
			sb.append(" or key in ('");
			sb.append(StringUtil.merge(issueKeys, "','"));
			sb.append("')");
		}

		return search(
			sb.toString(), new String[] {"key", "labels", "status", "summary"});
	}

	public List<JiraSupportIssue> search(String jql, String[] returnFields)
		throws Exception {

		List<JiraSupportIssue> jiraSupportIssues = new ArrayList<>();

		String nextPageToken = StringPool.BLANK;

		while (true) {
			JSONObject searchResponseJSONObject = _searchJSONObject(
				jql, _MAX_RESULTS, nextPageToken, returnFields);

			if (searchResponseJSONObject == null) {
				break;
			}

			JSONArray issuesJSONArray = searchResponseJSONObject.getJSONArray(
				"issues");

			for (int i = 0; i < issuesJSONArray.length(); i++) {
				JSONObject issueJSONObject = issuesJSONArray.getJSONObject(i);

				String issueKey = issueJSONObject.getString("key");

				String ticketURL =
					_jiraSupportHCPortalURL + StringPool.SLASH + issueKey;

				if (issueKey.startsWith(_jiraSupportFLSProject)) {
					ticketURL =
						_jiraSupportFLSPortalURL + StringPool.SLASH + issueKey;
				}

				JiraSupportIssue jiraSupportIssue = new JiraSupportIssue(
					issueJSONObject, ticketURL);

				jiraSupportIssues.add(jiraSupportIssue);
			}

			nextPageToken = searchResponseJSONObject.optString("nextPageToken");

			if (Validator.isNull(nextPageToken)) {
				break;
			}
		}

		return jiraSupportIssues;
	}

	public BusinessEvent updateBusinessEvent(
			BusinessEvent businessEvent, String id)
		throws Exception {

		_syncBusinessEvent(businessEvent, id);

		return _businessEventConverter.toBusinessEvent(
			StringPool.BLANK, _getAssetObjectJSONObject(id, _jiraWorkspaceId));
	}

	private JSONObject _createAssetObjectJSONObject(
			JSONObject attributesJSONObject, String objectTypeId,
			String workspaceId)
		throws Exception {

		JSONObject bodyJSONObject = new JSONObject(
		).put(
			"attributes", _transformAttributes(attributesJSONObject)
		).put(
			"objectTypeId", objectTypeId
		);

		String response = post(
			bodyJSONObject.toString(),
			HashMapBuilder.put(
				HttpHeaders.AUTHORIZATION, _getAuthorization()
			).put(
				HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE
			).build(),
			UriComponentsBuilder.fromUriString(
				StringBundler.concat(
					_JIRA_CLOUD_API_URL, "/jsm/assets/workspace/", workspaceId,
					"/v1/object/create")
			).build(
			).toUri());

		return new JSONObject(response);
	}

	private JSONObject _deleteAssetObjectJSONObject(
			String objectId, String workspaceId)
		throws Exception {

		String response = delete(
			_getAuthorization(), StringPool.BLANK,
			UriComponentsBuilder.fromUriString(
				StringBundler.concat(
					_JIRA_CLOUD_API_URL, "/jsm/assets/workspace/", workspaceId,
					"/v1/object/", objectId)
			).build(
			).toUri());

		if (Validator.isNull(response)) {
			return new JSONObject();
		}

		return new JSONObject(response);
	}

	private JSONObject _getAssetObjectJSONObject(
			String objectId, String workspaceId)
		throws Exception {

		return new JSONObject(
			get(
				_getAuthorization(),
				UriComponentsBuilder.fromUriString(
					StringBundler.concat(
						_JIRA_CLOUD_API_URL, "/jsm/assets/workspace/",
						workspaceId, "/v1/object/", objectId)
				).build(
				).toUri()));
	}

	private String _getAuthorization() {
		Base64.Encoder encoder = Base64.getEncoder();

		String credentials =
			_jiraAPIEmailAddress + StringPool.COLON + _jiraAPIToken;

		return "Basic " + encoder.encodeToString(credentials.getBytes());
	}

	private JSONObject _searchAccountByExternalKeyJSONObject(
		String externalKey) {

		StringBundler sb = new StringBundler(4);

		sb.append("objectSchema = \"Koroneiki\" AND objectType = \"Account\" ");
		sb.append("AND \"External Key\" = \"");
		sb.append(externalKey);
		sb.append("\"");

		return new JSONObject(
			post(
				new JSONObject(
				).put(
					"qlQuery", sb.toString()
				).toString(),
				HashMapBuilder.put(
					HttpHeaders.AUTHORIZATION, _getAuthorization()
				).put(
					HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE
				).build(),
				UriComponentsBuilder.fromUriString(
					StringBundler.concat(
						_JIRA_CLOUD_API_URL, "/jsm/assets/workspace/",
						_jiraWorkspaceId, "/v1/object/aql")
				).build(
				).toUri()));
	}

	private JSONArray _searchAssetsObjectsJSONArray(
			String aql, String workspaceId)
		throws Exception {

		JSONArray itemsJSONArray = new JSONArray();

		boolean last = false;
		int startAt = 0;

		while (!last) {
			JSONObject resultsJSONObject = _searchAssetsObjectsPageJSONObject(
				aql, _MAX_RESULTS, startAt, workspaceId);

			JSONArray valuesJSONArray = resultsJSONObject.optJSONArray(
				"values");

			if ((valuesJSONArray == null) || valuesJSONArray.isEmpty()) {
				break;
			}

			itemsJSONArray.putAll(valuesJSONArray);

			last = resultsJSONObject.optBoolean("last");

			startAt += _MAX_RESULTS;
		}

		return itemsJSONArray;
	}

	private JSONObject _searchAssetsObjectsPageJSONObject(
			String aql, int maxResults, int startAt, String workspaceId)
		throws Exception {

		String response = post(
			new JSONObject(
			).put(
				"qlQuery", aql
			).toString(),
			HashMapBuilder.put(
				HttpHeaders.AUTHORIZATION, _getAuthorization()
			).put(
				HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE
			).build(),
			UriComponentsBuilder.fromUriString(
				StringBundler.concat(
					_JIRA_CLOUD_API_URL, "/jsm/assets/workspace/", workspaceId,
					"/v1/object/aql")
			).queryParam(
				"maxResults", maxResults
			).queryParam(
				"startAt", startAt
			).build(
			).toUri());

		if (Validator.isNull(response)) {
			return new JSONObject();
		}

		return new JSONObject(response);
	}

	private JSONObject _searchJSONObject(
			String jql, int maxResults, String nextPageToken,
			String[] returnFields)
		throws Exception {

		try {
			return new JSONObject(
				get(
					_getAuthorization(),
					UriComponentsBuilder.fromUriString(
						StringBundler.concat(
							_jiraURL, _URL_REST_API_3, "/search/jql")
					).queryParam(
						"expand", "renderedFields"
					).queryParam(
						"fields", StringUtil.merge(returnFields)
					).queryParam(
						"jql", jql
					).queryParam(
						"maxResults", maxResults
					).queryParam(
						"nextPageToken", nextPageToken
					).build(
					).toUri()));
		}
		catch (Exception exception) {
			if (_log.isWarnEnabled()) {
				_log.warn(
					"Unable to get Jira issues with JQL " + jql, exception);
			}
		}

		return null;
	}

	private void _syncBusinessEvent(BusinessEvent businessEvent, String id)
		throws Exception {

		if (Validator.isNull(id)) {
			_createAssetObjectJSONObject(
				_businessEventConverter.toAttributesJSONObject(
					getAccountObjectKey(
						businessEvent.getAccountExternalReferenceCode()),
					businessEvent),
				_jiraBusinessEventAssetObjectTypeId, _jiraWorkspaceId);
		}
		else {
			_updateAssetObjectJSONObject(
				_businessEventConverter.toAttributesJSONObject(
					null, businessEvent),
				id, _jiraWorkspaceId);
		}
	}

	private JSONArray _transformAttributes(JSONObject attributesJSONObject) {
		JSONArray attributesJSONArray = new JSONArray();

		for (String key : attributesJSONObject.keySet()) {
			if (Validator.isNull(key)) {
				continue;
			}

			attributesJSONArray.put(
				new JSONObject(
				).put(
					"objectAttributeValues",
					new JSONArray(
					).put(
						new JSONObject(
						).put(
							"value", attributesJSONObject.get(key)
						)
					)
				).put(
					"objectTypeAttributeId", key
				));
		}

		return attributesJSONArray;
	}

	private JSONObject _updateAssetObjectJSONObject(
			JSONObject attributesJSONObject, String objectId,
			String workspaceId)
		throws Exception {

		return new JSONObject(
			put(
				new JSONObject(
				).put(
					"attributes", _transformAttributes(attributesJSONObject)
				).toString(),
				HashMapBuilder.put(
					HttpHeaders.AUTHORIZATION, _getAuthorization()
				).put(
					HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE
				).build(),
				UriComponentsBuilder.fromUriString(
					StringBundler.concat(
						_JIRA_CLOUD_API_URL, "/jsm/assets/workspace/",
						workspaceId, "/v1/object/", objectId)
				).build(
				).toUri()));
	}

	private static final String _JIRA_CLOUD_API_URL =
		"https://api.atlassian.com";

	private static final int _MAX_RESULTS = 100;

	private static final String _URL_REST_API_3 = "/rest/api/3";

	private static final Log _log = LogFactory.getLog(JiraService.class);

	@Autowired
	private BusinessEventConverter _businessEventConverter;

	@Autowired
	private BusinessEventVersionConverter _businessEventVersionConverter;

	@Value("${liferay.one.jira.api.email.address}")
	private String _jiraAPIEmailAddress;

	@Value("${liferay.one.jira.api.token}")
	private String _jiraAPIToken;

	@Value("${liferay.one.jira.business.event.asset.object.type.id}")
	private String _jiraBusinessEventAssetObjectTypeId;

	@Value("${liferay.one.jira.support.fls.portal.url}")
	private String _jiraSupportFLSPortalURL;

	@Value("${liferay.one.jira.support.fls.project}")
	private String _jiraSupportFLSProject;

	@Value("${liferay.one.jira.support.hc.field.request.type}")
	private String _jiraSupportHCFieldRequestType;

	@Value("${liferay.one.jira.support.hc.portal.url}")
	private String _jiraSupportHCPortalURL;

	@Value("${liferay.one.jira.url}")
	private String _jiraURL;

	@Value("${liferay.one.jira.workspace.id}")
	private String _jiraWorkspaceId;

}