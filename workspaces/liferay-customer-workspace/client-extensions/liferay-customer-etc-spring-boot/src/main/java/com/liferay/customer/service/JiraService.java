/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.customer.service;

import com.liferay.client.extension.util.spring.boot3.service.BaseService;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.Base64;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;

import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.json.JSONArray;
import org.json.JSONObject;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * @author Jenny Chen
 */
@Component
public class JiraService extends BaseService {

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

			String jql = sb.toString();

			for (int i = 0; true; i += 100) {
				JSONObject jsonObject = _search(jql, 100, issueFields, i);

				JSONArray issuesJSONArray = jsonObject.getJSONArray("issues");

				if (issuesJSONArray.length() <= 0) {
					break;
				}

				for (int j = 0; j < issuesJSONArray.length(); j++) {
					JSONObject issueJSONObject = issuesJSONArray.getJSONObject(
						j);

					JSONObject fieldsJSONObject = issueJSONObject.getJSONObject(
						"fields");

					JSONArray versionsJSONArray = fieldsJSONObject.getJSONArray(
						"versions");

					for (int k = 0; k < versionsJSONArray.length(); k++) {
						JSONObject versionJSONObject =
							versionsJSONArray.getJSONObject(k);

						affectedVersions.add(
							versionJSONObject.optString("name"));
					}
				}
			}

			return new JSONArray(affectedVersions);
		}
		catch (Exception exception) {
			_log.error("Unable to get affected versions", exception);
		}

		return null;
	}

	@Cacheable("issue")
	public JSONObject getIssueJSONObject(String issueKey) throws Exception {
		try {
			JSONObject jsonObject = new JSONObject(
				get(
					_getCredentials(),
					UriComponentsBuilder.fromUriString(
						StringBundler.concat(
							_jiraURL, _URL_REST_API_2, "/issue/", issueKey)
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

	@Cacheable("issues")
	public JSONObject search(
			String[] filterAffectedVersions, String[] filterCategories,
			String[] filterClassifications, String[] filterFixVersions,
			String[] filterSeverities, String keywords, int page, int pageSize,
			String sortOrder, boolean hasEarlyPublishAccess)
		throws Exception {

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

		JSONObject jsonObject = _search(
			sb.toString(), pageSize, securityVulnerabilitiesIssueFields,
			_calculateStartAt(page, pageSize));

		return _transformSearchResults(jsonObject);
	}

	private int _calculatePage(int startAt, int maxResults) {
		return (startAt / maxResults) + 1;
	}

	private int _calculateStartAt(int page, int pageSize) {
		return (page - 1) * pageSize;
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

	private String _getCredentials() {
		String jiraUserNameAndJiraApiToken =
			_jiraAPIEmailAddress + StringPool.COLON + _jiraAPIToken;

		return "Basic " + Base64.encode(jiraUserNameAndJiraApiToken.getBytes());
	}

	private String _getJQLCustomField(String customField) {
		int pos = customField.indexOf(StringPool.UNDERLINE);

		return "cf[" + customField.substring(pos + 1) + "]";
	}

	private String _getJSONObjectFieldValue(JSONObject jsonObject) {
		if (jsonObject != null) {
			return jsonObject.optString("value");
		}

		return null;
	}

	private JSONObject _search(
			String jql, int maxResults, String[] returnFields, int startAt)
		throws Exception {

		try {
			return new JSONObject(
				get(
					_getCredentials(),
					UriComponentsBuilder.fromUriString(
						_jiraURL + _URL_REST_API_2 + "/search"
					).queryParam(
						"expand", "renderedFields"
					).queryParam(
						"fields", StringUtil.merge(returnFields)
					).queryParam(
						"jql", jql
					).queryParam(
						"maxResults", maxResults
					).queryParam(
						"startAt", startAt
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

	private JSONObject _transformIssue(JSONObject issueJSONObject) {
		return new JSONObject(
		).put(
			"fields",
			_transformIssueFields(
				issueJSONObject.getJSONObject("fields"),
				issueJSONObject.getJSONObject("renderedFields"))
		).put(
			"key", issueJSONObject.getString(_FIELD_ISSUE_KEY)
		);
	}

	private JSONObject _transformIssueFields(
		JSONObject issueFieldsJSONObject,
		JSONObject issueRenderedFieldsJSONObject) {

		return new JSONObject(
		).put(
			"affectedVersionsDetails",
			issueFieldsJSONObject.optString(
				_jiraSecurityVulnerabilityFieldAffectedVersionsDetails)
		).put(
			"affectedVersions",
			_flattenJSONArray(
				issueFieldsJSONObject.getJSONArray(_FIELD_VERSIONS))
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
				issueFieldsJSONObject.getJSONArray(_FIELD_COMPONENTS))
		).put(
			"customerPortalDescription",
			issueRenderedFieldsJSONObject.optString(
				_jiraSecurityVulnerabilityFieldCustomerPortalDescription)
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
					_jiraSecurityVulnerabilityFieldIssueClassification))
		).put(
			"partnerPublishingDate",
			issueFieldsJSONObject.optString(
				_jiraSecurityVulnerabilityFieldPartnerPublishingDate)
		).put(
			"publishingStatus",
			_getJSONObjectFieldValue(
				issueFieldsJSONObject.optJSONObject(
					_jiraSecurityVulnerabilityFieldPublishingStatus))
		).put(
			"severity",
			_getJSONObjectFieldValue(
				issueFieldsJSONObject.optJSONObject(
					_jiraSecurityVulnerabilityFieldSeverity))
		);
	}

	private JSONObject _transformSearchResults(JSONObject resultsJSONObject) {
		JSONArray jsonArray = new JSONArray();

		JSONArray issuesJSONArray = resultsJSONObject.getJSONArray("issues");

		for (int i = 0; i < issuesJSONArray.length(); i++) {
			JSONObject issueJSONObject = issuesJSONArray.getJSONObject(i);

			jsonArray.put(_transformIssue(issueJSONObject));
		}

		return new JSONObject(
		).put(
			"issues", jsonArray
		).put(
			"page",
			_calculatePage(
				resultsJSONObject.getInt("startAt"),
				resultsJSONObject.getInt("maxResults"))
		).put(
			"pageSize", resultsJSONObject.getInt("maxResults")
		).put(
			"total", resultsJSONObject.getInt("total")
		);
	}

	private static final String _FIELD_AFFECTED_VERSION = "affectedVersion";

	private static final String _FIELD_COMPONENTS = "components";

	private static final String _FIELD_ISSUE_KEY = "key";

	private static final String _FIELD_VERSIONS = "versions";

	private static final String _URL_REST_API_2 = "/rest/api/2";

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

	@Value("${liferay.customer.jira.url}")
	private String _jiraURL;

}