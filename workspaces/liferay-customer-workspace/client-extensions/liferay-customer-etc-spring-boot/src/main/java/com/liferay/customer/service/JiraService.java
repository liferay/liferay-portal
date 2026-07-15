/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.customer.service;

import com.liferay.client.extension.util.spring.boot3.service.BaseService;
import com.liferay.customer.constants.BusinessEventConstants;
import com.liferay.customer.constants.BusinessEventVersionConstants;
import com.liferay.customer.constants.JiraIssueConstants;
import com.liferay.customer.model.BusinessEvent;
import com.liferay.customer.model.BusinessEventVersion;
import com.liferay.customer.model.JiraSupportIssue;
import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;

import java.util.ArrayList;
import java.util.Base64;
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

	public List<BusinessEvent> createBusinessEvent(BusinessEvent businessEvent)
		throws Exception {

		_syncBusinessEvent(null, businessEvent);

		return getBusinessEvents(businessEvent.getAccountExternalKey());
	}

	public void deleteBusinessEvent(String id) throws Exception {
		_deleteAssetObjectJSONObject(_jiraWorkspaceId, id);
	}

	public String getAccountObjectKey(String externalKey) throws Exception {
		JSONObject accountResponseJSONObject =
			_searchAccountByExternalKeyJSONObject(externalKey);

		JSONArray valuesJSONArray = accountResponseJSONObject.getJSONArray(
			"values");

		JSONObject accountJSONObject = valuesJSONArray.getJSONObject(0);

		return accountJSONObject.getString("objectKey");
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
				JiraIssueConstants.toJQLCustomField(
					_jiraSecurityVulnerabilityFieldPublishingStatus));
			sb.append(" = 'Ready for Publishing' AND ");
			sb.append(
				JiraIssueConstants.toJQLCustomField(
					_jiraSecurityVulnerabilityFieldPartnerPublishingDate));
			sb.append(" <= now()");

			String nextPageToken = StringPool.BLANK;

			while (true) {
				JSONObject jsonObject = _searchJSONObject(
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

	public JSONObject getAssetObjectJSONObject(
		String workspaceId, String objectId) {

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

	@Cacheable("assetObjects")
	public JSONArray getAssetObjects(
			String objectSchemaName, String objectTypeName)
		throws Exception {

		JSONArray jsonArray = new JSONArray();

		String aql = StringBundler.concat(
			"objectSchema = \"", objectSchemaName, "\" AND objectType = \"",
			objectTypeName, "\"");

		JSONArray assetsObjectsJSONArray = _searchAssetsObjectsJSONArray(
			_jiraWorkspaceId, aql);

		for (int i = 0; i < assetsObjectsJSONArray.length(); i++) {
			JSONObject assetObjectJSONObject =
				assetsObjectsJSONArray.getJSONObject(i);

			jsonArray.put(
				new JSONObject(
				).put(
					"key", assetObjectJSONObject.getString("id")
				).put(
					"name", assetObjectJSONObject.getString("name")
				));
		}

		return jsonArray;
	}

	public BusinessEvent getBusinessEvent(String id) throws Exception {
		JSONObject assetObjectJSONObject = _getAssetObjectJSONObject(
			_jiraWorkspaceId, id);

		_injectBusinessEventAttributeNames(assetObjectJSONObject);

		return new BusinessEvent(StringPool.BLANK, assetObjectJSONObject);
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
			_jiraWorkspaceId, aql);

		if ((assetsObjectsJSONArray != null) &&
			!assetsObjectsJSONArray.isEmpty()) {

			for (int i = 0; i < assetsObjectsJSONArray.length(); i++) {
				JSONObject assetObjectJSONObject =
					assetsObjectsJSONArray.getJSONObject(i);

				_injectBusinessEventAttributeNames(assetObjectJSONObject);

				businessEvents.add(
					new BusinessEvent(
						accountExternalReferenceCode, assetObjectJSONObject));
			}
		}

		return businessEvents;
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
			_jiraWorkspaceId, aql);

		if ((assetsObjectsJSONArray != null) &&
			!assetsObjectsJSONArray.isEmpty()) {

			for (int i = 0; i < assetsObjectsJSONArray.length(); i++) {
				JSONObject assetObjectJSONObject =
					assetsObjectsJSONArray.getJSONObject(i);

				_injectBusinessEventVersionAttributeNames(
					assetObjectJSONObject);

				businessEventVersions.add(
					new BusinessEventVersion(assetObjectJSONObject));
			}
		}

		return businessEventVersions;
	}

	@Cacheable("assetObjectFieldOptions")
	public JSONArray getFieldOptions(String fieldName) throws Exception {
		JSONArray objectTypeAttributesJSONArray =
			_getObjectTypeAttributesJSONArray(
				_jiraWorkspaceId, _jiraBusinessEventAssetObjectTypeId);

		JSONArray fieldOptionsJSONArray = new JSONArray();

		for (int i = 0; i < objectTypeAttributesJSONArray.length(); i++) {
			JSONObject objectTypeAttributeJSONObject =
				objectTypeAttributesJSONArray.getJSONObject(i);

			if (!fieldName.equals(
					objectTypeAttributeJSONObject.optString("name"))) {

				continue;
			}

			String options = objectTypeAttributeJSONObject.optString("options");

			if (Validator.isNotNull(options)) {
				for (String option : options.split(",")) {
					fieldOptionsJSONArray.put(
						new JSONObject(
						).put(
							"key", option
						).put(
							"name", option
						));
				}
			}

			break;
		}

		return fieldOptionsJSONArray;
	}

	@Cacheable("issue")
	public JSONObject getIssueJSONObject(String issueKey) throws Exception {
		try {
			JSONObject issueJSONObject = new JSONObject(
				get(
					_getCredentials(),
					UriComponentsBuilder.fromUriString(
						StringBundler.concat(
							_jiraURL, _URL_REST_API_3, "/issue/", issueKey)
					).queryParam(
						"expand", "renderedFields"
					).build(
					).toUri()));

			return _transformIssueJSONObject(issueJSONObject);
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
			JSONObject issueJSONObject = new JSONObject(
				get(
					_getCredentials(),
					UriComponentsBuilder.fromUriString(
						StringBundler.concat(
							_jiraURL, _URL_REST_API_3, "/issue/", issueKey)
					).queryParam(
						"expand", "renderedFields"
					).build(
					).toUri()));

			JSONObject issueFieldsJSONObject = issueJSONObject.optJSONObject(
				"fields");

			String organizationObjectFieldId = _getAssetObjectFieldId(
				issueFieldsJSONObject.optJSONArray(
					_jiraSupportHCFieldOrganization));

			if (Validator.isNotNull(organizationObjectFieldId)) {
				String[] parts = StringUtil.split(
					organizationObjectFieldId, CharPool.COLON);

				return new JiraSupportIssue(
					issueJSONObject, parts[1], parts[0]);
			}

			return new JiraSupportIssue(issueJSONObject);
		}
		catch (Exception exception) {
			if (_log.isWarnEnabled()) {
				_log.warn(
					"Unable to get Jira issue with key " + issueKey, exception);
			}
		}

		return null;
	}

	public List<JiraSupportIssue> getJSMJiraSupportIssues(
			String externalReferenceCode, String[] issueKeys)
		throws Exception {

		StringBundler sb = new StringBundler(12);

		sb.append("Organization in aqlFunction('\\\"External Key\\\" = \\\"");
		sb.append(externalReferenceCode);
		sb.append("\\\"') and (status not in ('");
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

	@CacheEvict(allEntries = true, value = "affectedVersions")
	@Scheduled(
		cron = "${liferay.customer.jira.service.affected.versions.cache.eviction.cron}"
	)
	public void scheduledAffectedVersionsCacheEviction() throws Exception {
	}

	@CacheEvict(
		allEntries = true, value = {"assetObjectFieldOptions", "assetObjects"}
	)
	@Scheduled(
		cron = "${liferay.customer.jira.service.jsm.objects.cache.eviction.cron}"
	)
	public void scheduledAssetObjectsCacheEviction() throws Exception {
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
			JSONObject searchResponseJSONObject = _searchJSONObject(
				jql, 100, nextPageToken, returnFields);

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
			JiraIssueConstants.toJQLCustomField(
				_jiraSecurityVulnerabilityFieldPublishingStatus));
		sb.append(" = 'Ready for Publishing'");

		if (hasEarlyPublishAccess) {
			sb.append(" AND ");
			sb.append(
				JiraIssueConstants.toJQLCustomField(
					_jiraSecurityVulnerabilityFieldPartnerPublishingDate));
			sb.append(" <= now()");
		}
		else {
			sb.append(" AND ");
			sb.append(
				JiraIssueConstants.toJQLCustomField(
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
				JiraIssueConstants.toJQLCustomField(
					_jiraSecurityVulnerabilityFieldCategories));
			sb.append(" in ('");
			sb.append(StringUtil.merge(filterCategories, "','"));
			sb.append("')");
		}

		if (ArrayUtil.isNotEmpty(filterClassifications)) {
			sb.append(" AND ");
			sb.append(
				JiraIssueConstants.toJQLCustomField(
					_jiraSecurityVulnerabilityFieldIssueClassification));
			sb.append(" in ('");
			sb.append(StringUtil.merge(filterClassifications, "','"));
			sb.append("')");
		}

		if (ArrayUtil.isNotEmpty(filterFixVersions)) {
			sb.append(" AND ");
			sb.append(
				JiraIssueConstants.toJQLCustomField(
					_jiraSecurityVulnerabilityFieldFixVersions));
			sb.append(" in ('");
			sb.append(StringUtil.merge(filterFixVersions, "','"));
			sb.append("')");
		}

		if (ArrayUtil.isNotEmpty(filterSeverities)) {
			sb.append(" AND ");
			sb.append(
				JiraIssueConstants.toJQLCustomField(
					_jiraSecurityVulnerabilityFieldSeverity));
			sb.append(" in ('");
			sb.append(StringUtil.merge(filterSeverities, "','"));
			sb.append("')");
		}

		if (Validator.isNotNull(keywords)) {
			sb.append(" AND (");
			sb.append(
				JiraIssueConstants.toJQLCustomField(
					_jiraSecurityVulnerabilityFieldCustomerPortalSummary));
			sb.append(" ~ ");
			sb.append(StringUtil.quote(keywords));
			sb.append(" OR ");
			sb.append(
				JiraIssueConstants.toJQLCustomField(
					_jiraSecurityVulnerabilityFieldCVEIds));
			sb.append(" ~ ");
			sb.append(StringUtil.quote(keywords));
			sb.append(")");
		}

		sb.append(" ORDER BY ");

		if (hasEarlyPublishAccess) {
			sb.append(
				JiraIssueConstants.toJQLCustomField(
					_jiraSecurityVulnerabilityFieldPartnerPublishingDate));
		}
		else {
			sb.append(
				JiraIssueConstants.toJQLCustomField(
					_jiraSecurityVulnerabilityFieldCustomerPublishingDate));
		}

		sb.append(" ");
		sb.append(sortOrder);
		sb.append(", ");
		sb.append(
			JiraIssueConstants.toJQLCustomField(
				_jiraSecurityVulnerabilityFieldSeverity));
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
			JSONObject searchResponseJSONObject = _searchJSONObject(
				sb.toString(), 100, nextPageToken,
				securityVulnerabilitiesIssueFields);

			if (searchResponseJSONObject == null) {
				break;
			}

			JSONArray issuesJSONArray = searchResponseJSONObject.getJSONArray(
				"issues");

			for (int i = 0; i < issuesJSONArray.length(); i++) {
				jsonObjects.add(
					_transformIssueJSONObject(
						issuesJSONArray.getJSONObject(i)));
			}

			nextPageToken = searchResponseJSONObject.optString("nextPageToken");

			if (Validator.isNull(nextPageToken)) {
				break;
			}
		}

		return jsonObjects;
	}

	public BusinessEvent updateBusinessEvent(
			String id, BusinessEvent businessEvent)
		throws Exception {

		_syncBusinessEvent(id, businessEvent);

		JSONObject assetObjectJSONObject = _getAssetObjectJSONObject(
			_jiraWorkspaceId, id);

		return new BusinessEvent(StringPool.BLANK, assetObjectJSONObject);
	}

	private JSONObject _createAssetObjectJSONObject(
			String workspaceId, String objectTypeId,
			JSONObject attributesJSONObject)
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
				HttpHeaders.AUTHORIZATION, _getCredentials()
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
			String workspaceId, String objectId)
		throws Exception {

		String response = delete(
			_getCredentials(), StringPool.BLANK,
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

	private JSONArray _flattenJSONArray(JSONArray jsonArray) {
		if (jsonArray == null) {
			return new JSONArray();
		}

		JSONArray flattenedJSONArray = new JSONArray();

		for (int i = 0; i < jsonArray.length(); i++) {
			JSONObject itemJSONObject = jsonArray.getJSONObject(i);

			String name = itemJSONObject.optString("name");

			if (Validator.isNotNull(name)) {
				flattenedJSONArray.put(name);
			}

			String value = itemJSONObject.optString("value");

			if (Validator.isNotNull(value)) {
				flattenedJSONArray.put(value);
			}
		}

		return flattenedJSONArray;
	}

	private String _getAssetObjectFieldId(JSONArray jsonArray) {
		if ((jsonArray != null) && (jsonArray.length() > 0)) {
			JSONObject assetJSONObject = jsonArray.getJSONObject(0);

			return assetJSONObject.getString("id");
		}

		return null;
	}

	private JSONObject _getAssetObjectJSONObject(
			String workspaceId, String objectId)
		throws Exception {

		return new JSONObject(
			get(
				_getCredentials(),
				UriComponentsBuilder.fromUriString(
					StringBundler.concat(
						_JIRA_CLOUD_API_URL, "/jsm/assets/workspace/",
						workspaceId, "/v1/object/", objectId)
				).build(
				).toUri()));
	}

	private String _getCredentials() {
		Base64.Encoder encoder = Base64.getEncoder();

		String jiraUserNameAndJiraApiToken =
			_jiraAPIEmailAddress + StringPool.COLON + _jiraAPIToken;

		return "Basic " +
			encoder.encodeToString(jiraUserNameAndJiraApiToken.getBytes());
	}

	private String _getJSONObjectFieldValue(JSONObject jsonObject, String key) {
		if (jsonObject != null) {
			return jsonObject.optString(key);
		}

		return null;
	}

	private JSONArray _getObjectTypeAttributesJSONArray(
			String workspaceId, String objectTypeId)
		throws Exception {

		return new JSONArray(
			get(
				_getCredentials(),
				UriComponentsBuilder.fromUriString(
					StringBundler.concat(
						_JIRA_CLOUD_API_URL, "/jsm/assets/workspace/",
						workspaceId, "/v1/objecttype/", objectTypeId,
						"/attributes")
				).build(
				).toUri()));
	}

	private void _injectBusinessEventAttributeNames(
			JSONObject assetObjectJSONObject)
		throws Exception {

		JSONArray attributesJSONArray = assetObjectJSONObject.optJSONArray(
			"attributes");

		if (attributesJSONArray == null) {
			return;
		}

		for (int i = 0; i < attributesJSONArray.length(); i++) {
			JSONObject attributeJSONObject = attributesJSONArray.getJSONObject(
				i);

			String objectTypeAttributeId = attributeJSONObject.optString(
				"objectTypeAttributeId");

			if (Validator.isNull(objectTypeAttributeId)) {
				continue;
			}

			if (objectTypeAttributeId.equals(
					_jiraBusinessEventAssetObjectTypeAttributeAccount)) {

				attributeJSONObject.put(
					"objectTypeAttributeName",
					BusinessEventConstants.ATTRIBUTE_ACCOUNT);
			}
			else if (objectTypeAttributeId.equals(
						_jiraBusinessEventAssetObjectTypeAttributeActualEventDate)) {

				attributeJSONObject.put(
					"objectTypeAttributeName",
					BusinessEventConstants.ATTRIBUTE_ACTUAL_EVENT_DATE);
			}
			else if (objectTypeAttributeId.equals(
						_jiraBusinessEventAssetObjectTypeAttributeAssociatedTickets)) {

				attributeJSONObject.put(
					"objectTypeAttributeName",
					BusinessEventConstants.ATTRIBUTE_ASSOCIATED_TICKETS);
			}
			else if (objectTypeAttributeId.equals(
						_jiraBusinessEventAssetObjectTypeAttributeAuthor)) {

				attributeJSONObject.put(
					"objectTypeAttributeName",
					BusinessEventConstants.ATTRIBUTE_AUTHOR);
			}
			else if (objectTypeAttributeId.equals(
						_jiraBusinessEventAssetObjectTypeAttributeCurrentVersion)) {

				attributeJSONObject.put(
					"objectTypeAttributeName",
					BusinessEventConstants.ATTRIBUTE_CURRENT_VERSION);
			}
			else if (objectTypeAttributeId.equals(
						_jiraBusinessEventAssetObjectTypeAttributeDescription)) {

				attributeJSONObject.put(
					"objectTypeAttributeName",
					BusinessEventConstants.ATTRIBUTE_DESCRIPTION);
			}
			else if (objectTypeAttributeId.equals(
						_jiraBusinessEventAssetObjectTypeAttributeEventStatus)) {

				attributeJSONObject.put(
					"objectTypeAttributeName",
					BusinessEventConstants.ATTRIBUTE_EVENT_STATUS);
			}
			else if (objectTypeAttributeId.equals(
						_jiraBusinessEventAssetObjectTypeAttributeEventType)) {

				attributeJSONObject.put(
					"objectTypeAttributeName",
					BusinessEventConstants.ATTRIBUTE_EVENT_TYPE);
			}
			else if (objectTypeAttributeId.equals(
						_jiraBusinessEventAssetObjectTypeAttributeLastComment)) {

				attributeJSONObject.put(
					"objectTypeAttributeName",
					BusinessEventConstants.ATTRIBUTE_LAST_COMMENT);
			}
			else if (objectTypeAttributeId.equals(
						_jiraBusinessEventAssetObjectTypeAttributeLastUpdatedAuthor)) {

				attributeJSONObject.put(
					"objectTypeAttributeName",
					BusinessEventConstants.ATTRIBUTE_LAST_UPDATED_AUTHOR);
			}
			else if (objectTypeAttributeId.equals(
						_jiraBusinessEventAssetObjectTypeAttributeName)) {

				attributeJSONObject.put(
					"objectTypeAttributeName",
					BusinessEventConstants.ATTRIBUTE_NAME);
			}
			else if (objectTypeAttributeId.equals(
						_jiraBusinessEventAssetObjectTypeAttributeNewVersion)) {

				attributeJSONObject.put(
					"objectTypeAttributeName",
					BusinessEventConstants.ATTRIBUTE_NEW_VERSION);
			}
			else if (objectTypeAttributeId.equals(
						_jiraBusinessEventAssetObjectTypeAttributePlannedEventDate)) {

				attributeJSONObject.put(
					"objectTypeAttributeName",
					BusinessEventConstants.ATTRIBUTE_PLANNED_EVENT_DATE);
			}
			else if (objectTypeAttributeId.equals(
						_jiraBusinessEventAssetObjectTypeAttributeTimeZone)) {

				attributeJSONObject.put(
					"objectTypeAttributeName",
					BusinessEventConstants.ATTRIBUTE_TIME_ZONE);
			}
		}
	}

	private void _injectBusinessEventVersionAttributeNames(
			JSONObject assetObjectJSONObject)
		throws Exception {

		JSONArray attributesJSONArray = assetObjectJSONObject.optJSONArray(
			"attributes");

		if (attributesJSONArray == null) {
			return;
		}

		for (int i = 0; i < attributesJSONArray.length(); i++) {
			JSONObject attributeJSONObject = attributesJSONArray.getJSONObject(
				i);

			String objectTypeAttributeId = attributeJSONObject.optString(
				"objectTypeAttributeId");

			if (Validator.isNull(objectTypeAttributeId)) {
				continue;
			}

			if (objectTypeAttributeId.equals(
					_jiraBusinessEventVersionAssetObjectTypeAttributeAuthor)) {

				attributeJSONObject.put(
					"objectTypeAttributeName",
					BusinessEventVersionConstants.ATTRIBUTE_AUTHOR);
			}
			else if (objectTypeAttributeId.equals(
						_jiraBusinessEventVersionAssetObjectTypeAttributeChange)) {

				attributeJSONObject.put(
					"objectTypeAttributeName",
					BusinessEventVersionConstants.ATTRIBUTE_CHANGE);
			}
			else if (objectTypeAttributeId.equals(
						_jiraBusinessEventVersionAssetObjectTypeAttributeComment)) {

				attributeJSONObject.put(
					"objectTypeAttributeName",
					BusinessEventVersionConstants.ATTRIBUTE_COMMENT);
			}
			else if (objectTypeAttributeId.equals(
						_jiraBusinessEventVersionAssetObjectTypeAttributeCreated)) {

				attributeJSONObject.put(
					"objectTypeAttributeName",
					BusinessEventVersionConstants.ATTRIBUTE_CREATED);
			}
		}
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

	private JSONArray _searchAssetsObjectsJSONArray(
			String workspaceId, String aql)
		throws Exception {

		JSONArray itemsJSONArray = new JSONArray();

		boolean last = false;
		int startAt = 0;

		while (!last) {
			JSONObject jsonObject = _searchAssetsObjectsPageJSONObject(
				workspaceId, aql, _ASSET_OBJECTS_MAX_RESULTS, startAt);

			JSONArray jsonArray = jsonObject.optJSONArray("values");

			if ((jsonArray == null) || jsonArray.isEmpty()) {
				break;
			}

			itemsJSONArray.putAll(jsonArray);

			last = jsonObject.optBoolean("last");

			startAt += _ASSET_OBJECTS_MAX_RESULTS;
		}

		return itemsJSONArray;
	}

	private JSONObject _searchAssetsObjectsPageJSONObject(
			String workspaceId, String aql, int maxResults, int startAt)
		throws Exception {

		String response = post(
			new JSONObject(
			).put(
				"qlQuery", aql
			).toString(),
			HashMapBuilder.put(
				HttpHeaders.AUTHORIZATION, _getCredentials()
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
					_getCredentials(),
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

	private void _syncBusinessEvent(String id, BusinessEvent businessEvent)
		throws Exception {

		JSONObject attributesJSONObject = new JSONObject();

		attributesJSONObject.put(
			_jiraBusinessEventAssetObjectTypeAttributeActualEventDate,
			businessEvent.getActualEventDate()
		).put(
			_jiraBusinessEventAssetObjectTypeAttributeAssociatedTickets,
			businessEvent.getAssociatedTickets()
		).put(
			_jiraBusinessEventAssetObjectTypeAttributeCurrentVersion,
			businessEvent.getCurrentLiferayVersionKey()
		).put(
			_jiraBusinessEventAssetObjectTypeAttributeDescription,
			businessEvent.getDescription()
		).put(
			_jiraBusinessEventAssetObjectTypeAttributeEventStatus,
			businessEvent.getEventStatusName()
		).put(
			_jiraBusinessEventAssetObjectTypeAttributeEventType,
			businessEvent.getEventTypeName()
		).put(
			_jiraBusinessEventAssetObjectTypeAttributeLastComment,
			businessEvent.getLastComment()
		).put(
			_jiraBusinessEventAssetObjectTypeAttributeLastUpdatedAuthor,
			businessEvent.getLastUpdatedAuthorEmailAddress()
		).put(
			_jiraBusinessEventAssetObjectTypeAttributeName,
			businessEvent.getName()
		).put(
			_jiraBusinessEventAssetObjectTypeAttributeNewVersion,
			businessEvent.getNewLiferayVersionKey()
		).put(
			_jiraBusinessEventAssetObjectTypeAttributePlannedEventDate,
			businessEvent.getPlannedEventDate()
		).put(
			_jiraBusinessEventAssetObjectTypeAttributeTimeZone,
			businessEvent.getTimeZoneName()
		);

		if (Validator.isNull(id)) {
			attributesJSONObject.put(
				_jiraBusinessEventAssetObjectTypeAttributeAccount,
				getAccountObjectKey(businessEvent.getAccountExternalKey())
			).put(
				_jiraBusinessEventAssetObjectTypeAttributeAuthor,
				businessEvent.getAuthorEmailAddress()
			);

			_createAssetObjectJSONObject(
				_jiraWorkspaceId, _jiraBusinessEventAssetObjectTypeId,
				attributesJSONObject);
		}
		else {
			_updateAssetObjectJSONObject(
				_jiraWorkspaceId, id, attributesJSONObject);
		}
	}

	private JSONArray _transformAttributes(JSONObject attributesJSONObject) {
		JSONArray jsonArray = new JSONArray();

		for (String key : attributesJSONObject.keySet()) {
			if (Validator.isNull(key)) {
				continue;
			}

			jsonArray.put(
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

		return jsonArray;
	}

	private JSONObject _transformIssueFieldsJSONObject(
		JSONObject issueFieldsJSONObject,
		JSONObject issueRenderedFieldsJSONObject) {

		JSONObject transformedFieldsJSONObject = new JSONObject();

		if (issueFieldsJSONObject != null) {
			transformedFieldsJSONObject.put(
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
			transformedFieldsJSONObject.put(
				"customerPortalDescription",
				issueRenderedFieldsJSONObject.optString(
					_jiraSecurityVulnerabilityFieldCustomerPortalDescription));
		}

		return transformedFieldsJSONObject;
	}

	private JSONObject _transformIssueJSONObject(JSONObject issueJSONObject) {
		return new JSONObject(
		).put(
			"fields",
			_transformIssueFieldsJSONObject(
				issueJSONObject.optJSONObject("fields"),
				issueJSONObject.optJSONObject("renderedFields"))
		).put(
			"key", issueJSONObject.getString(_FIELD_ISSUE_KEY)
		);
	}

	private JSONObject _updateAssetObjectJSONObject(
			String workspaceId, String objectId,
			JSONObject attributesJSONObject)
		throws Exception {

		return new JSONObject(
			put(
				new JSONObject(
				).put(
					"attributes", _transformAttributes(attributesJSONObject)
				).toString(),
				HashMapBuilder.put(
					HttpHeaders.AUTHORIZATION, _getCredentials()
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

	private static final int _ASSET_OBJECTS_MAX_RESULTS = 500;

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
		"${liferay.customer.jira.business.event.asset.object.type.attribute." +
			"account}"
	)
	private String _jiraBusinessEventAssetObjectTypeAttributeAccount;

	@Value(
		"${liferay.customer.jira.business.event.asset.object.type.attribute." +
			"actual.event.date}"
	)
	private String _jiraBusinessEventAssetObjectTypeAttributeActualEventDate;

	@Value(
		"${liferay.customer.jira.business.event.asset.object.type.attribute." +
			"associated.tickets}"
	)
	private String _jiraBusinessEventAssetObjectTypeAttributeAssociatedTickets;

	@Value(
		"${liferay.customer.jira.business.event.asset.object.type.attribute." +
			"author}"
	)
	private String _jiraBusinessEventAssetObjectTypeAttributeAuthor;

	@Value(
		"${liferay.customer.jira.business.event.asset.object.type.attribute." +
			"current.version}"
	)
	private String _jiraBusinessEventAssetObjectTypeAttributeCurrentVersion;

	@Value(
		"${liferay.customer.jira.business.event.asset.object.type.attribute." +
			"description}"
	)
	private String _jiraBusinessEventAssetObjectTypeAttributeDescription;

	@Value(
		"${liferay.customer.jira.business.event.asset.object.type.attribute." +
			"event.status}"
	)
	private String _jiraBusinessEventAssetObjectTypeAttributeEventStatus;

	@Value(
		"${liferay.customer.jira.business.event.asset.object.type.attribute." +
			"event.type}"
	)
	private String _jiraBusinessEventAssetObjectTypeAttributeEventType;

	@Value(
		"${liferay.customer.jira.business.event.asset.object.type.attribute." +
			"last.comment}"
	)
	private String _jiraBusinessEventAssetObjectTypeAttributeLastComment;

	@Value(
		"${liferay.customer.jira.business.event.asset.object.type.attribute." +
			"last.updated.author}"
	)
	private String _jiraBusinessEventAssetObjectTypeAttributeLastUpdatedAuthor;

	@Value(
		"${liferay.customer.jira.business.event.asset.object.type.attribute." +
			"name}"
	)
	private String _jiraBusinessEventAssetObjectTypeAttributeName;

	@Value(
		"${liferay.customer.jira.business.event.asset.object.type.attribute." +
			"new.version}"
	)
	private String _jiraBusinessEventAssetObjectTypeAttributeNewVersion;

	@Value(
		"${liferay.customer.jira.business.event.asset.object.type.attribute." +
			"planned.event.date}"
	)
	private String _jiraBusinessEventAssetObjectTypeAttributePlannedEventDate;

	@Value(
		"${liferay.customer.jira.business.event.asset.object.type.attribute." +
			"time.zone}"
	)
	private String _jiraBusinessEventAssetObjectTypeAttributeTimeZone;

	@Value("${liferay.customer.jira.business.event.asset.object.type.id}")
	private String _jiraBusinessEventAssetObjectTypeId;

	@Value(
		"${liferay.customer.jira.business.event.version.asset.object.type." +
			"attribute.author}"
	)
	private String _jiraBusinessEventVersionAssetObjectTypeAttributeAuthor;

	@Value(
		"${liferay.customer.jira.business.event.version.asset.object.type." +
			"attribute.change}"
	)
	private String _jiraBusinessEventVersionAssetObjectTypeAttributeChange;

	@Value(
		"${liferay.customer.jira.business.event.version.asset.object.type." +
			"attribute.comment}"
	)
	private String _jiraBusinessEventVersionAssetObjectTypeAttributeComment;

	@Value(
		"${liferay.customer.jira.business.event.version.asset.object.type." +
			"attribute.created}"
	)
	private String _jiraBusinessEventVersionAssetObjectTypeAttributeCreated;

	@Value(
		"${liferay.customer.jira.security.vulnerability.field.affected." +
			"versions.details}"
	)
	private String _jiraSecurityVulnerabilityFieldAffectedVersionsDetails;

	@Value("${liferay.customer.jira.security.vulnerability.field.affects}")
	private String _jiraSecurityVulnerabilityFieldAffects;

	@Value("${liferay.customer.jira.security.vulnerability.field.categories}")
	private String _jiraSecurityVulnerabilityFieldCategories;

	@Value(
		"${liferay.customer.jira.security.vulnerability.field.customer.portal." +
			"description}"
	)
	private String _jiraSecurityVulnerabilityFieldCustomerPortalDescription;

	@Value(
		"${liferay.customer.jira.security.vulnerability.field.customer.portal." +
			"summary}"
	)
	private String _jiraSecurityVulnerabilityFieldCustomerPortalSummary;

	@Value(
		"${liferay.customer.jira.security.vulnerability.field.customer." +
			"publishing.date}"
	)
	private String _jiraSecurityVulnerabilityFieldCustomerPublishingDate;

	@Value("${liferay.customer.jira.security.vulnerability.field.cve.ids}")
	private String _jiraSecurityVulnerabilityFieldCVEIds;

	@Value(
		"${liferay.customer.jira.security.vulnerability.field.cvss.base.score}"
	)
	private String _jiraSecurityVulnerabilityFieldCVSSBaseScore;

	@Value(
		"${liferay.customer.jira.security.vulnerability.field.cvss.vector." +
			"string}"
	)
	private String _jiraSecurityVulnerabilityFieldCVSSVectorString;

	@Value("${liferay.customer.jira.security.vulnerability.field.cwe.ids}")
	private String _jiraSecurityVulnerabilityFieldCWEIds;

	@Value("${liferay.customer.jira.security.vulnerability.field.fix.versions}")
	private String _jiraSecurityVulnerabilityFieldFixVersions;

	@Value(
		"${liferay.customer.jira.security.vulnerability.field.issue." +
			"classification}"
	)
	private String _jiraSecurityVulnerabilityFieldIssueClassification;

	@Value(
		"${liferay.customer.jira.security.vulnerability.field.partner." +
			"publishing.date}"
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

	@Value("${liferay.customer.jira.support.hc.field.organization}")
	private String _jiraSupportHCFieldOrganization;

	@Value("${liferay.customer.jira.support.hc.field.request.type}")
	private String _jiraSupportHCFieldRequestType;

	@Value("${liferay.customer.jira.support.hc.portal.url}")
	private String _jiraSupportHCPortalURL;

	@Value("${liferay.customer.jira.url}")
	private String _jiraURL;

	@Value("${liferay.customer.jira.workspace.id}")
	private String _jiraWorkspaceId;

}