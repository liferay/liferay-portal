/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.customer;

import com.liferay.customer.exception.JiraIssueClosedException;
import com.liferay.customer.exception.JiraIssueNotFoundException;
import com.liferay.customer.exception.JiraOrganizationNotFoundException;
import com.liferay.customer.model.JiraSupportIssue;
import com.liferay.customer.service.JiraService;
import com.liferay.portal.kernel.util.Validator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.json.JSONArray;
import org.json.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author Amos Fong
 */
public class BaseRestController
	extends com.liferay.client.extension.util.spring.boot3.BaseRestController {

	protected String getAccountKey(String jiraIssueKey) throws Exception {
		try {
			return _getAccountKey(jiraIssueKey);
		}
		catch (JiraIssueClosedException jiraIssueClosedException) {
			_log.error(jiraIssueClosedException, jiraIssueClosedException);

			throw jiraIssueClosedException;
		}
		catch (JiraIssueNotFoundException jiraIssueNotFoundException) {
			_log.error(jiraIssueNotFoundException, jiraIssueNotFoundException);

			throw jiraIssueNotFoundException;
		}
		catch (Exception exception) {
			_log.error(exception, exception);

			throw new JiraOrganizationNotFoundException(exception);
		}
	}

	private String _getAccountKey(String jiraIssueKey) throws Exception {
		JiraSupportIssue jiraSupportIssue = _jiraService.getJiraSupportIssue(
			jiraIssueKey);

		if (jiraSupportIssue == null) {
			throw new JiraIssueNotFoundException();
		}

		if (jiraSupportIssue.isClosed()) {
			throw new JiraIssueClosedException();
		}

		String organizationId = jiraSupportIssue.getOrganizationId();
		String workspaceId = jiraSupportIssue.getWorkspaceId();

		if (Validator.isNull(organizationId) || Validator.isNull(workspaceId)) {
			throw new JiraOrganizationNotFoundException();
		}

		JSONObject assetObjectJSONObject = _jiraService.getAssetObject(
			workspaceId, organizationId);

		JSONArray jsonArray = assetObjectJSONObject.getJSONArray("attributes");

		for (int i = 0; i < jsonArray.length(); i++) {
			JSONObject attributeJSONObject = jsonArray.getJSONObject(i);

			JSONObject objectTypeAttributeJSONObject =
				attributeJSONObject.getJSONObject("objectTypeAttribute");

			String name = objectTypeAttributeJSONObject.getString("name");

			if (!name.equals("External Key")) {
				continue;
			}

			JSONArray objectAttributeValuesJSONArray =
				attributeJSONObject.getJSONArray("objectAttributeValues");

			JSONObject objectAttributeValuesJSONObject =
				objectAttributeValuesJSONArray.getJSONObject(0);

			return objectAttributeValuesJSONObject.getString("value");
		}

		throw new JiraOrganizationNotFoundException();
	}

	private static final Log _log = LogFactory.getLog(BaseRestController.class);

	@Autowired
	private JiraService _jiraService;

}