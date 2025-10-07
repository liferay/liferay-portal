/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.customer.service;

import com.liferay.client.extension.util.spring.boot3.service.BaseService;
import com.liferay.customer.model.JiraSupportIssue;
import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.Base64;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.json.JSONArray;
import org.json.JSONObject;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * @author Jenny Chen
 */
@Component
public class JiraService extends BaseService {

	public void addComment(String issueKey, String body) {
		post(
			body,
			HashMapBuilder.put(
				HttpHeaders.AUTHORIZATION, _getCredentials()
			).put(
				HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE
			).build(),
			UriComponentsBuilder.fromUriString(
				StringBundler.concat(
					_jiraURL, "/rest/api/3/issue/", issueKey, "/comment")
			).build(
			).toUri());
	}

	public int calculateStartAt(int page, int pageSize) {
		return (page - 1) * pageSize;
	}

	@Cacheable("affectedVersions")
	public JSONArray getAffectedVersionsJSONArray() throws Exception {
		try {
			Set<String> affectedVersions = new TreeSet<>();

			String[] issueFields = {_FIELD_VERSIONS};

			StringBundler sb = new StringBundler(7);

			sb.append("project = '");
			sb.append(_jiraSecurityVulnerabilityProject);
			sb.append("' AND ");
			sb.append(
				_getJQLCustomField(
					_jiraSecurityVulnerabilityFieldPublishingStatus));
			sb.append(" = 'Ready for Publishing' AND ");
			sb.append(
				_getJQLCustomField(
					_jiraSecurityVulnerabilityFieldPartnerPublishingDate));
			sb.append(" <= now()");

			String nextPageToken = StringPool.BLANK;

			while (true) {
				JSONObject jsonObject = _search(
					sb.toString(), 100, nextPageToken, issueFields);

				if (jsonObject == null) {
					break;
				}

				JSONArray issuesJSONArray = jsonObject.getJSONArray("issues");

				for (int i = 0; i < issuesJSONArray.length(); i++) {
					JSONObject issueJSONObject = issuesJSONArray.getJSONObject(
						i);

					JSONObject fieldsJSONObject = issueJSONObject.getJSONObject(
						"fields");

					JSONArray versionsJSONArray = fieldsJSONObject.getJSONArray(
						"versions");

					for (int j = 0; j < versionsJSONArray.length(); j++) {
						JSONObject versionJSONObject =
							versionsJSONArray.getJSONObject(j);

						affectedVersions.add(
							versionJSONObject.optString("name"));
					}
				}

				nextPageToken = jsonObject.optString("nextPageToken");

				if (Validator.isNull(nextPageToken)) {
					break;
				}
			}

			return new JSONArray(affectedVersions);
		}
		catch (Exception exception) {
			_log.error("Unable to get affected versions", exception);
		}

		return null;
	}

	public JSONObject getAssetObject(String workspaceId, String objectId) {
		JSONObject jsonObject = new JSONObject(
			get(
				_getCredentials(),
				UriComponentsBuilder.fromUriString(
					StringBundler.concat(
						_JIRA_CLOUD_API_URL, "/jsm/assets/workspace/",
						workspaceId, "/v1/object/", objectId)
				).build(
				).toUri()));

		if (jsonObject.has("errorMessages")) {
			if (_log.isWarnEnabled()) {
				_log.warn(
					"Unable to get asset object " +
						jsonObject.getJSONArray("errorMessages"));
			}

			return null;
		}

		return jsonObject;
	}

	@Cacheable("issue")
	public JSONObject getIssueJSONObject(String issueKey) throws Exception {
		try {
			JSONObject jsonObject = new JSONObject(
				get(
					_getCredentials(),
					UriComponentsBuilder.fromUriString(
						StringBundler.concat(
							_jiraURL, _URL_REST_API_3, "/issue/", issueKey)
					).queryParam(
						"expand", "renderedFields"
					).build(
					).toUri()));

			return _transformIssue(jsonObject);
		}
		catch (Exception exception) {
			if (_log.isWarnEnabled()) {
				_log.warn(
					"Unable to get Jira issue with key " + issueKey, exception);
			}
		}

		return null;
	}

	public JiraSupportIssue getJiraSupportIssue(String issueKey)
		throws Exception {

		try {
			JSONObject jsonObject = new JSONObject(
				get(
					_getCredentials(),
					UriComponentsBuilder.fromUriString(
						StringBundler.concat(
							_jiraURL, _URL_REST_API_3, "/issue/", issueKey)
					).queryParam(
						"expand", "renderedFields"
					).build(
					).toUri()));

			JSONObject issueFieldsJSONObject = jsonObject.optJSONObject(
				"fields");

			String organizationObjectFieldId = _getAssetObjectFieldId(
				issueFieldsJSONObject.optJSONArray(
					_jiraSupportHCFieldOrganization));

			if (Validator.isNotNull(organizationObjectFieldId)) {
				String[] parts = StringUtil.split(
					organizationObjectFieldId, CharPool.COLON);

				return new JiraSupportIssue(jsonObject, parts[1], parts[0]);
			}

			return new JiraSupportIssue(jsonObject);
		}
		catch (Exception exception) {
			if (_log.isWarnEnabled()) {
				_log.warn(
					"Unable to get Jira issue with key " + issueKey, exception);
			}
		}

		return null;
	}

	@CacheEvict(allEntries = true, value = "affectedVersions")
	@Scheduled(
		cron = "${liferay.customer.jira.service.affected.versions.cache.eviction.cron}"
	)
	public void scheduledAffectedVersionsCacheEviction() throws Exception {
	}

	@CacheEvict(allEntries = true, value = {"issue", "issues"})
	@Scheduled(
		cron = "${liferay.customer.jira.service.issues.cache.eviction.cron}"
	)
	public void scheduledIssuesCacheEviction() throws Exception {
	}

	public List<JiraSupportIssue> search(String jql, String[] returnFields)
		throws Exception {

		List<JiraSupportIssue> jiraSupportIssues = new ArrayList<>();

		String nextPageToken = StringPool.BLANK;

		while (true) {
			JSONObject jsonObject = _search(
				jql, 100, nextPageToken, returnFields);

			if (jsonObject == null) {
				break;
			}

			JSONArray issuesJSONArray = jsonObject.getJSONArray("issues");

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

			nextPageToken = jsonObject.optString("nextPageToken");

			if (Validator.isNull(nextPageToken)) {
				break;
			}
		}

		return jiraSupportIssues;
	}

	@Cacheable("issues")
	public List<JSONObject> search(
			String[] filterAffectedVersions, String[] filterCategories,
			String[] filterClassifications, String[] filterFixVersions,
			String[] filterSeverities, String keywords, String sortOrder,
			boolean hasEarlyPublishAccess)
		throws Exception {

		List<JSONObject> jsonObjects = new ArrayList<>();

		String nextPageToken = StringPool.BLANK;

		StringBundler sb = new StringBundler(49);

		sb.append("project = '");
		sb.append(_jiraSecurityVulnerabilityProject);
		sb.append("' AND ");
		sb.append(
			_getJQLCustomField(
				_jiraSecurityVulnerabilityFieldPublishingStatus));
		sb.append(" = 'Ready for Publishing'");

		if (hasEarlyPublishAccess) {
			sb.append(" AND ");
			sb.append(
				_getJQLCustomField(
					_jiraSecurityVulnerabilityFieldPartnerPublishingDate));
			sb.append(" <= now()");
		}
		else {
			sb.append(" AND ");
			sb.append(
				_getJQLCustomField(
					_jiraSecurityVulnerabilityFieldCustomerPublishingDate));
			sb.append(" <= now()");
		}

		if (ArrayUtil.isNotEmpty(filterAffectedVersions)) {
			sb.append(" AND ");
			sb.append(_FIELD_AFFECTED_VERSION);
			sb.append(" in ('");
			sb.append(StringUtil.merge(filterAffectedVersions, "','"));
			sb.append("')");
		}

		if (ArrayUtil.isNotEmpty(filterCategories)) {
			sb.append(" AND ");
			sb.append(
				_getJQLCustomField(_jiraSecurityVulnerabilityFieldCategories));
			sb.append(" in ('");
			sb.append(StringUtil.merge(filterCategories, "','"));
			sb.append("')");
		}

		if (ArrayUtil.isNotEmpty(filterClassifications)) {
			sb.append(" AND ");
			sb.append(
				_getJQLCustomField(
					_jiraSecurityVulnerabilityFieldIssueClassification));
			sb.append(" in ('");
			sb.append(StringUtil.merge(filterClassifications, "','"));
			sb.append("')");
		}

		if (ArrayUtil.isNotEmpty(filterFixVersions)) {
			sb.append(" AND ");
			sb.append(
				_getJQLCustomField(_jiraSecurityVulnerabilityFieldFixVersions));
			sb.append(" in ('");
			sb.append(StringUtil.merge(filterFixVersions, "','"));
			sb.append("')");
		}

		if (ArrayUtil.isNotEmpty(filterSeverities)) {
			sb.append(" AND ");
			sb.append(
				_getJQLCustomField(_jiraSecurityVulnerabilityFieldSeverity));
			sb.append(" in ('");
			sb.append(StringUtil.merge(filterSeverities, "','"));
			sb.append("')");
		}

		if (Validator.isNotNull(keywords)) {
			sb.append(" AND (");
			sb.append(
				_getJQLCustomField(
					_jiraSecurityVulnerabilityFieldCustomerPortalSummary));
			sb.append(" ~ ");
			sb.append(StringUtil.quote(keywords));
			sb.append(" OR ");
			sb.append(
				_getJQLCustomField(_jiraSecurityVulnerabilityFieldCVEIds));
			sb.append(" ~ ");
			sb.append(StringUtil.quote(keywords));
			sb.append(")");
		}

		sb.append(" ORDER BY ");

		if (hasEarlyPublishAccess) {
			sb.append(
				_getJQLCustomField(
					_jiraSecurityVulnerabilityFieldPartnerPublishingDate));
		}
		else {
			sb.append(
				_getJQLCustomField(
					_jiraSecurityVulnerabilityFieldCustomerPublishingDate));
		}

		sb.append(" ");
		sb.append(sortOrder);
		sb.append(", ");
		sb.append(_getJQLCustomField(_jiraSecurityVulnerabilityFieldSeverity));
		sb.append(" ASC");

		String[] securityVulnerabilitiesIssueFields = {
			_FIELD_COMPONENTS, _FIELD_ISSUE_KEY, _FIELD_VERSIONS,
			_jiraSecurityVulnerabilityFieldAffectedVersionsDetails,
			_jiraSecurityVulnerabilityFieldAffects,
			_jiraSecurityVulnerabilityFieldCategories,
			_jiraSecurityVulnerabilityFieldCustomerPortalDescription,
			_jiraSecurityVulnerabilityFieldCustomerPortalSummary,
			_jiraSecurityVulnerabilityFieldCustomerPublishingDate,
			_jiraSecurityVulnerabilityFieldCVEIds,
			_jiraSecurityVulnerabilityFieldCVSSBaseScore,
			_jiraSecurityVulnerabilityFieldCVSSVectorString,
			_jiraSecurityVulnerabilityFieldCWEIds,
			_jiraSecurityVulnerabilityFieldFixVersions,
			_jiraSecurityVulnerabilityFieldIssueClassification,
			_jiraSecurityVulnerabilityFieldPartnerPublishingDate,
			_jiraSecurityVulnerabilityFieldPublishingStatus,
			_jiraSecurityVulnerabilityFieldSeverity
		};

		while (true) {
			JSONObject jsonObject = _search(
				sb.toString(), 100, nextPageToken,
				securityVulnerabilitiesIssueFields);

			if (jsonObject == null) {
				break;
			}

			JSONArray issuesJSONArray = jsonObject.getJSONArray("issues");

			for (int i = 0; i < issuesJSONArray.length(); i++) {
				jsonObjects.add(
					_transformIssue(issuesJSONArray.getJSONObject(i)));
			}

			nextPageToken = jsonObject.optString("nextPageToken");

			if (Validator.isNull(nextPageToken)) {
				break;
			}
		}

		return jsonObjects;
	}

	public void updateAccountObject(
			String koroneikiAccountKey, String businessEvents)
		throws Exception {

		JSONObject accountResponseJSONObject = _searchAccountByExternalKey(
			koroneikiAccountKey);

		JSONArray valuesJSONArray = accountResponseJSONObject.getJSONArray(
			"values");

		if (valuesJSONArray == null) {
			throw new Exception(
				"Unable to find account with key " + koroneikiAccountKey);
		}

		String businessEventsAttributeId = _getObjectTypeAttributeId(
			accountResponseJSONObject.getJSONArray("objectTypeAttributes"),
			"Business Events");

		JSONObject jsonObject = new JSONObject(
		).put(
			"attributes",
			new JSONArray(
			).put(
				new JSONObject(
				).put(
					"objectTypeAttributeId", businessEventsAttributeId
				).put(
					"objectAttributeValues",
					new JSONArray(
					).put(
						new JSONObject(
						).put(
							"value", businessEvents
						)
					)
				)
			)
		);

		JSONObject accountJSONObject = valuesJSONArray.getJSONObject(0);

		put(
			jsonObject.toString(),
			HashMapBuilder.put(
				HttpHeaders.AUTHORIZATION, _getCredentials()
			).put(
				HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE
			).build(),
			UriComponentsBuilder.fromUriString(
				StringBundler.concat(
					_JIRA_CLOUD_API_URL, "/jsm/assets/workspace/",
					_jiraWorkspaceId, "/v1/object/",
					accountJSONObject.getString("id"))
			).build(
			).toUri());
	}

	public void updateIssue(
		String issueKey, String businessEvents, String[] addLabels,
		String[] removeLabels) {

		JSONArray labelsJSONArray = new JSONArray();

		for (String label : addLabels) {
			JSONObject addLabelJSONObject = new JSONObject();

			addLabelJSONObject.put("add", label);

			labelsJSONArray.put(addLabelJSONObject);
		}

		for (String label : removeLabels) {
			JSONObject removeLabelJSONObject = new JSONObject();

			removeLabelJSONObject.put("remove", label);

			labelsJSONArray.put(removeLabelJSONObject);
		}

		JSONObject updateJSONObject = new JSONObject();

		updateJSONObject.put("labels", labelsJSONArray);

		if (Validator.isNotNull(businessEvents)) {
			updateJSONObject.put(
				_jiraSupportHCFieldBusinessEvent,
				_transformADFTextArea(businessEvents));
		}

		JSONObject jsonObject = new JSONObject(
		).put(
			"update", updateJSONObject
		);

		put(
			jsonObject.toString(),
			HashMapBuilder.put(
				HttpHeaders.AUTHORIZATION, _getCredentials()
			).put(
				HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE
			).build(),
			UriComponentsBuilder.fromUriString(
				StringBundler.concat(
					_jiraURL, _URL_REST_API_3, "/issue/", issueKey)
			).build(
			).toUri());
	}

	private JSONArray _flattenJSONArray(JSONArray jsonArray) {
		if (jsonArray == null) {
			return new JSONArray();
		}

		JSONArray flattenedJSONArray = new JSONArray();

		for (int i = 0; i < jsonArray.length(); i++) {
			JSONObject jsonObject = jsonArray.getJSONObject(i);

			String name = jsonObject.optString("name");

			if (Validator.isNotNull(name)) {
				flattenedJSONArray.put(name);
			}

			String value = jsonObject.optString("value");

			if (Validator.isNotNull(value)) {
				flattenedJSONArray.put(value);
			}
		}

		return flattenedJSONArray;
	}

	private String _getAssetObjectFieldId(JSONArray jsonArray) {
		if ((jsonArray != null) && (jsonArray.length() > 0)) {
			JSONObject jsonObject = jsonArray.getJSONObject(0);

			return jsonObject.getString("id");
		}

		return null;
	}

	private String _getCredentials() {
		String jiraUserNameAndJiraApiToken =
			_jiraAPIEmailAddress + StringPool.COLON + _jiraAPIToken;

		return "Basic " + Base64.encode(jiraUserNameAndJiraApiToken.getBytes());
	}

	private String _getJQLCustomField(String customField) {
		int pos = customField.indexOf(StringPool.UNDERLINE);

		return "cf[" + customField.substring(pos + 1) + "]";
	}

	private String _getJSONObjectFieldValue(JSONObject jsonObject, String key) {
		if (jsonObject != null) {
			return jsonObject.optString(key);
		}

		return null;
	}

	private String _getObjectTypeAttributeId(
		JSONArray objectTypeAttributesJSONArray, String attributeName) {

		for (int i = 0; i < objectTypeAttributesJSONArray.length(); i++) {
			JSONObject objectTypeAttributeJSONObject =
				objectTypeAttributesJSONArray.getJSONObject(i);

			String name = objectTypeAttributeJSONObject.getString("name");

			if (name.equals(attributeName)) {
				return objectTypeAttributeJSONObject.getString("id");
			}
		}

		return StringPool.BLANK;
	}

	private JSONObject _search(
			String jql, int maxResults, String nextPageToken,
			String[] returnFields)
		throws Exception {

		try {
			return new JSONObject(
				get(
					_getCredentials(),
					UriComponentsBuilder.fromUriString(
						_jiraURL + _URL_REST_API_3 + "/search/jql"
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

	private JSONObject _searchAccountByExternalKey(String externalKey) {
		StringBundler sb = new StringBundler(4);

		sb.append("objectSchema = \"Koroneiki\" and objectType = \"Account\" ");
		sb.append("and \"External Key\" = \"");
		sb.append(externalKey);
		sb.append("\"");

		JSONObject jsonObject = new JSONObject(
		).put(
			"qlQuery", sb.toString()
		);

		return new JSONObject(
			post(
				jsonObject.toString(),
				HashMapBuilder.put(
					HttpHeaders.AUTHORIZATION, _getCredentials()
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

	private JSONArray _transformADFTextArea(String text) {
		return new JSONArray(
		).put(
			new JSONObject(
			).put(
				"set",
				new JSONObject(
				).put(
					"type", "doc"
				).put(
					"version", 1
				).put(
					"content",
					new JSONArray(
					).put(
						new JSONObject(
						).put(
							"type", "paragraph"
						).put(
							"content",
							new JSONArray(
							).put(
								new JSONObject(
								).put(
									"text", text
								).put(
									"type", "text"
								)
							)
						)
					)
				)
			)
		);
	}

	private JSONObject _transformIssue(JSONObject issueJSONObject) {
		return new JSONObject(
		).put(
			"fields",
			_transformIssueFields(
				issueJSONObject.optJSONObject("fields"),
				issueJSONObject.optJSONObject("renderedFields"))
		).put(
			"key", issueJSONObject.getString(_FIELD_ISSUE_KEY)
		);
	}

	private JSONObject _transformIssueFields(
		JSONObject issueFieldsJSONObject,
		JSONObject issueRenderedFieldsJSONObject) {

		JSONObject jsonObject = new JSONObject();

		if (issueFieldsJSONObject != null) {
			jsonObject.put(
				"affectedVersions",
				_flattenJSONArray(
					issueFieldsJSONObject.optJSONArray(_FIELD_VERSIONS))
			).put(
				"affectedVersionsDetails",
				issueFieldsJSONObject.optString(
					_jiraSecurityVulnerabilityFieldAffectedVersionsDetails)
			).put(
				"affects",
				issueFieldsJSONObject.optString(
					_jiraSecurityVulnerabilityFieldAffects)
			).put(
				"categories",
				_flattenJSONArray(
					issueFieldsJSONObject.optJSONArray(
						_jiraSecurityVulnerabilityFieldCategories))
			).put(
				"components",
				_flattenJSONArray(
					issueFieldsJSONObject.optJSONArray(_FIELD_COMPONENTS))
			).put(
				"customerPortalSummary",
				issueFieldsJSONObject.optString(
					_jiraSecurityVulnerabilityFieldCustomerPortalSummary)
			).put(
				"customerPublishingDate",
				issueFieldsJSONObject.optString(
					_jiraSecurityVulnerabilityFieldCustomerPublishingDate)
			).put(
				"cveIds",
				issueFieldsJSONObject.optString(
					_jiraSecurityVulnerabilityFieldCVEIds)
			).put(
				"cvssBaseScore",
				issueFieldsJSONObject.optString(
					_jiraSecurityVulnerabilityFieldCVSSBaseScore)
			).put(
				"cvssVectorString",
				issueFieldsJSONObject.optString(
					_jiraSecurityVulnerabilityFieldCVSSVectorString)
			).put(
				"cweIds",
				issueFieldsJSONObject.optString(
					_jiraSecurityVulnerabilityFieldCWEIds)
			).put(
				"fixVersions",
				_flattenJSONArray(
					issueFieldsJSONObject.optJSONArray(
						_jiraSecurityVulnerabilityFieldFixVersions))
			).put(
				"issueClassification",
				_getJSONObjectFieldValue(
					issueFieldsJSONObject.optJSONObject(
						_jiraSecurityVulnerabilityFieldIssueClassification),
					"value")
			).put(
				"organization",
				_getAssetObjectFieldId(
					issueFieldsJSONObject.optJSONArray(
						_jiraSupportHCFieldOrganization))
			).put(
				"partnerPublishingDate",
				issueFieldsJSONObject.optString(
					_jiraSecurityVulnerabilityFieldPartnerPublishingDate)
			).put(
				"publishingStatus",
				_getJSONObjectFieldValue(
					issueFieldsJSONObject.optJSONObject(
						_jiraSecurityVulnerabilityFieldPublishingStatus),
					"value")
			).put(
				"severity",
				_getJSONObjectFieldValue(
					issueFieldsJSONObject.optJSONObject(
						_jiraSecurityVulnerabilityFieldSeverity),
					"value")
			).put(
				"status",
				_getJSONObjectFieldValue(
					issueFieldsJSONObject.optJSONObject(_FIELD_STATUS), "name")
			);
		}

		if (issueRenderedFieldsJSONObject != null) {
			jsonObject.put(
				"customerPortalDescription",
				issueRenderedFieldsJSONObject.optString(
					_jiraSecurityVulnerabilityFieldCustomerPortalDescription));
		}

		return jsonObject;
	}

	private static final String _FIELD_AFFECTED_VERSION = "affectedVersion";

	private static final String _FIELD_COMPONENTS = "components";

	private static final String _FIELD_ISSUE_KEY = "key";

	private static final String _FIELD_STATUS = "status";

	private static final String _FIELD_VERSIONS = "versions";

	private static final String _JIRA_CLOUD_API_URL =
		"https://api.atlassian.com";

	private static final String _URL_REST_API_3 = "/rest/api/3";

	private static final Log _log = LogFactory.getLog(JiraService.class);

	@Value("${liferay.customer.jira.api.email.address}")
	private String _jiraAPIEmailAddress;

	@Value("${liferay.customer.jira.api.token}")
	private String _jiraAPIToken;

	@Value(
		"${liferay.customer.jira.security.vulnerability.field.affected.versions.details}"
	)
	private String _jiraSecurityVulnerabilityFieldAffectedVersionsDetails;

	@Value("${liferay.customer.jira.security.vulnerability.field.affects}")
	private String _jiraSecurityVulnerabilityFieldAffects;

	@Value("${liferay.customer.jira.security.vulnerability.field.categories}")
	private String _jiraSecurityVulnerabilityFieldCategories;

	@Value(
		"${liferay.customer.jira.security.vulnerability.field.customer.portal.description}"
	)
	private String _jiraSecurityVulnerabilityFieldCustomerPortalDescription;

	@Value(
		"${liferay.customer.jira.security.vulnerability.field.customer.portal.summary}"
	)
	private String _jiraSecurityVulnerabilityFieldCustomerPortalSummary;

	@Value(
		"${liferay.customer.jira.security.vulnerability.field.customer.publishing.date}"
	)
	private String _jiraSecurityVulnerabilityFieldCustomerPublishingDate;

	@Value("${liferay.customer.jira.security.vulnerability.field.cve.ids}")
	private String _jiraSecurityVulnerabilityFieldCVEIds;

	@Value(
		"${liferay.customer.jira.security.vulnerability.field.cvss.base.score}"
	)
	private String _jiraSecurityVulnerabilityFieldCVSSBaseScore;

	@Value(
		"${liferay.customer.jira.security.vulnerability.field.cvss.vector.string}"
	)
	private String _jiraSecurityVulnerabilityFieldCVSSVectorString;

	@Value("${liferay.customer.jira.security.vulnerability.field.cwe.ids}")
	private String _jiraSecurityVulnerabilityFieldCWEIds;

	@Value("${liferay.customer.jira.security.vulnerability.field.fix.versions}")
	private String _jiraSecurityVulnerabilityFieldFixVersions;

	@Value(
		"${liferay.customer.jira.security.vulnerability.field.issue.classification}"
	)
	private String _jiraSecurityVulnerabilityFieldIssueClassification;

	@Value(
		"${liferay.customer.jira.security.vulnerability.field.partner.publishing.date}"
	)
	private String _jiraSecurityVulnerabilityFieldPartnerPublishingDate;

	@Value(
		"${liferay.customer.jira.security.vulnerability.field.publishing.status}"
	)
	private String _jiraSecurityVulnerabilityFieldPublishingStatus;

	@Value("${liferay.customer.jira.security.vulnerability.field.severity}")
	private String _jiraSecurityVulnerabilityFieldSeverity;

	@Value("${liferay.customer.jira.security.vulnerability.project}")
	private String _jiraSecurityVulnerabilityProject;

	@Value("${liferay.customer.jira.support.fls.portal.url}")
	private String _jiraSupportFLSPortalURL;

	@Value("${liferay.customer.jira.support.fls.project}")
	private String _jiraSupportFLSProject;

	@Value("${liferay.customer.jira.support.hc.field.business.event}")
	private String _jiraSupportHCFieldBusinessEvent;

	@Value("${liferay.customer.jira.support.hc.field.organization}")
	private String _jiraSupportHCFieldOrganization;

	@Value("${liferay.customer.jira.support.hc.portal.url}")
	private String _jiraSupportHCPortalURL;

	@Value("${liferay.customer.jira.url}")
	private String _jiraURL;

	@Value("${liferay.customer.jira.workspace.id}")
	private String _jiraWorkspaceId;

}