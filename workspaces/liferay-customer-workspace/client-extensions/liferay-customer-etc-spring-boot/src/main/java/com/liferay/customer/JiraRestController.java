/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.customer;

import com.liferay.client.extension.util.spring.boot3.BaseRestController;
import com.liferay.customer.constants.RoleConstants;
import com.liferay.customer.service.JiraService;
import com.liferay.portal.kernel.security.auth.PrincipalException;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import java.util.ArrayList;
import java.util.List;

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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * @author Jenny Chen
 */
@RestController
public class JiraRestController extends BaseRestController {

	@RequestMapping(method = RequestMethod.DELETE, path = "/jira/cache")
	public ResponseEntity<String> delete(@AuthenticationPrincipal Jwt jwt) {
		try {
			if (!_hasAdministrator(jwt)) {
				throw new PrincipalException();
			}

			_jiraService.scheduledAffectedVersionsCacheEviction();
			_jiraService.scheduledIssuesCacheEviction();

			return new ResponseEntity<>(HttpStatus.OK);
		}
		catch (Exception exception) {
			_log.error(exception, exception);

			return new ResponseEntity(
				exception.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@RequestMapping(
		method = RequestMethod.GET,
		path = "/jira/security-vulnerabilities/affected-versions"
	)
	public ResponseEntity<String> get() throws Exception {
		try {
			JSONArray affectedVersionsJSONArray =
				_jiraService.getAffectedVersionsJSONArray();

			return new ResponseEntity<>(
				affectedVersionsJSONArray.toString(), HttpStatus.OK);
		}
		catch (Exception exception) {
			_log.error(exception, exception);

			return new ResponseEntity<>(
				exception.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@RequestMapping(method = RequestMethod.GET, path = "/jira/issue/{issueKey}")
	public ResponseEntity<String> get(
			@AuthenticationPrincipal Jwt jwt,
			@PathVariable("issueKey") String issueKey)
		throws Exception {

		try {
			if (!issueKey.startsWith(_jiraSecurityVulnerabilityProject)) {
				throw new PrincipalException();
			}

			JSONObject jsonObject = _jiraService.getIssueJSONObject(issueKey);

			if (_hasIssuePermission(jwt, jsonObject)) {
				return new ResponseEntity<>(
					jsonObject.toString(), HttpStatus.OK);
			}

			return new ResponseEntity<>(
				"No issue found with key " + issueKey, HttpStatus.NOT_FOUND);
		}
		catch (Exception exception) {
			_log.error(exception, exception);

			return new ResponseEntity<>(
				exception.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@RequestMapping(
		method = RequestMethod.GET,
		path = "/jira/security-vulnerabilities/search"
	)
	public ResponseEntity<String> search(
			@AuthenticationPrincipal Jwt jwt,
			@RequestParam(defaultValue = "", required = false) String[]
				filterAffectedVersions,
			@RequestParam(defaultValue = "", required = false) String[]
				filterCategories,
			@RequestParam(defaultValue = "", required = false) String[]
				filterClassifications,
			@RequestParam(defaultValue = "", required = false) String[]
				filterFixVersions,
			@RequestParam(defaultValue = "", required = false) String[]
				filterSeverities,
			@RequestParam(defaultValue = "", required = false) String keywords,
			@RequestParam(defaultValue = "1", required = false) int page,
			@RequestParam(defaultValue = "15", required = false) int pageSize,
			@RequestParam(defaultValue = "DESC", required = false) String
				sortOrder)
		throws Exception {

		try {
			List<JSONObject> jsonObjects = _jiraService.search(
				filterAffectedVersions, filterCategories, filterClassifications,
				filterFixVersions, filterSeverities, keywords, sortOrder,
				_hasEarlyPublishAccess(jwt));

			JSONObject jsonObject = _toJSONObject(jsonObjects, page, pageSize);

			return new ResponseEntity<>(jsonObject.toString(), HttpStatus.OK);
		}
		catch (Exception exception) {
			_log.error(exception, exception);

			return new ResponseEntity<>(
				exception.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	private JSONObject _getMyUserAccountJSONObject(Jwt jwt) throws Exception {
		try {
			return new JSONObject(
				get(
					"Bearer " + jwt.getTokenValue(),
					UriComponentsBuilder.fromPath(
						"/o/headless-admin-user/v1.0/my-user-account"
					).build(
					).toUri()));
		}
		catch (Exception exception) {
			if (_log.isWarnEnabled()) {
				_log.warn("Unable to get user account", exception);
			}

			throw new PrincipalException();
		}
	}

	private List<String> _getUserRoleNames(Jwt jwt) throws Exception {
		List<String> rolesNames = new ArrayList<>();

		JSONObject userJSONObject = _getMyUserAccountJSONObject(jwt);

		JSONArray roleBriefsJSONArray = userJSONObject.getJSONArray(
			"roleBriefs");

		for (int i = 0; i < roleBriefsJSONArray.length(); i++) {
			JSONObject roleBriefJSONObject = roleBriefsJSONArray.getJSONObject(
				i);

			String roleName = roleBriefJSONObject.getString("name");

			rolesNames.add(roleName);
		}

		return rolesNames;
	}

	private boolean _hasAdministrator(Jwt jwt) throws Exception {
		List<String> userRoleNames = _getUserRoleNames(jwt);

		return userRoleNames.contains(RoleConstants.NAME_ADMINISTRATOR);
	}

	private boolean _hasEarlyPublishAccess(Jwt jwt) throws Exception {
		List<String> userRoleNames = _getUserRoleNames(jwt);

		if (userRoleNames.contains(RoleConstants.NAME_ADMINISTRATOR) ||
			userRoleNames.contains(RoleConstants.NAME_LIFERAY_STAFF) ||
			userRoleNames.contains(RoleConstants.NAME_PARTNER)) {

			return true;
		}

		return false;
	}

	private boolean _hasIssuePermission(Jwt jwt, JSONObject issueJSONObject)
		throws Exception {

		JSONObject fieldsJSONObject = issueJSONObject.getJSONObject("fields");

		String publishingStatus = fieldsJSONObject.optString(
			"publishingStatus");

		if (publishingStatus.equals("Ready for Publishing")) {
			LocalDateTime localDateTime = _parseLocalDateTime(
				jwt, issueJSONObject);

			if (localDateTime.isBefore(LocalDateTime.now())) {
				return true;
			}
		}

		return false;
	}

	private LocalDateTime _parseLocalDateTime(
			Jwt jwt, JSONObject issueJSONObject)
		throws Exception {

		JSONObject fieldsJSONObject = issueJSONObject.getJSONObject("fields");

		DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(
			"yyyy-MM-dd'T'HH:mm:ss.SSSx");

		if (_hasEarlyPublishAccess(jwt)) {
			return LocalDateTime.parse(
				fieldsJSONObject.optString("partnerPublishingDate"),
				dateTimeFormatter);
		}

		return LocalDateTime.parse(
			fieldsJSONObject.optString("customerPublishingDate"),
			dateTimeFormatter);
	}

	private JSONObject _toJSONObject(
		List<JSONObject> jsonObjects, int page, int pageSize) {

		return new JSONObject(
		).put(
			"issues",
			jsonObjects.subList(
				_jiraService.calculateStartAt(page, pageSize),
				Math.min(
					_jiraService.calculateStartAt(page, pageSize) + pageSize,
					jsonObjects.size()))
		).put(
			"page", page
		).put(
			"pageSize", pageSize
		).put(
			"total", jsonObjects.size()
		);
	}

	private static final Log _log = LogFactory.getLog(JiraRestController.class);

	@Value("${liferay.customer.jira.security.vulnerability.project}")
	private String _jiraSecurityVulnerabilityProject;

	@Autowired
	private JiraService _jiraService;

}