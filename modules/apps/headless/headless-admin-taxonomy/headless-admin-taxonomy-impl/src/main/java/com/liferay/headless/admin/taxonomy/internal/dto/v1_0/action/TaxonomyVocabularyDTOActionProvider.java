/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.taxonomy.internal.dto.v1_0.action;

import com.liferay.headless.admin.taxonomy.internal.dto.v1_0.action.metadata.TaxonomyVocabularyDTOActionMetadataProvider;
import com.liferay.oauth2.provider.scope.ScopeChecker;
import com.liferay.portal.vulcan.dto.action.ActionInfo;
import com.liferay.portal.vulcan.dto.action.DTOActionProvider;
import com.liferay.portal.vulcan.util.ActionUtil;
import com.liferay.portal.vulcan.util.UriInfoUtil;

import jakarta.annotation.Generated;

import jakarta.ws.rs.core.UriInfo;

import java.util.HashMap;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Javier Gamarra
 * @generated
 */
@Component(
	property = {
		"dto.class.name=com.liferay.headless.admin.taxonomy.dto.v1_0.TaxonomyVocabulary"
	},
	service = DTOActionProvider.class
)
@Generated("")
public class TaxonomyVocabularyDTOActionProvider implements DTOActionProvider {

	@Override
	public Map<String, ActionInfo> getActionInfos() throws Exception {
		Map<String, ActionInfo> actionInfos = new HashMap<>();

		TaxonomyVocabularyDTOActionMetadataProvider
			taxonomyVocabularyDTOActionMetadataProvider =
				new TaxonomyVocabularyDTOActionMetadataProvider();

		for (String actionName :
				taxonomyVocabularyDTOActionMetadataProvider.getActionNames()) {

			actionInfos.put(
				actionName,
				taxonomyVocabularyDTOActionMetadataProvider.getActionInfo(
					actionName));
		}

		return actionInfos;
	}

	@Override
	public Map<String, Map<String, String>> getActions(
		long groupId, long primaryKey, UriInfo uriInfo, long userId) {

		Map<String, Map<String, String>> actions = new HashMap<>();

		TaxonomyVocabularyDTOActionMetadataProvider
			taxonomyVocabularyDTOActionMetadataProvider =
				new TaxonomyVocabularyDTOActionMetadataProvider();

		for (String actionName :
				taxonomyVocabularyDTOActionMetadataProvider.getActionNames()) {

			ActionInfo actionInfo =
				taxonomyVocabularyDTOActionMetadataProvider.getActionInfo(
					actionName);

			if ((actionInfo == null) || (actionInfo.getActionName() == null) ||
				(actionInfo.getResourceMethodName() == null)) {

				continue;
			}

			actions.put(
				actionName,
				ActionUtil.addAction(
					actionInfo.getActionName(), actionInfo.getResourceClass(),
					primaryKey, actionInfo.getResourceMethodName(),
					_scopeChecker, userId,
					taxonomyVocabularyDTOActionMetadataProvider.
						getPermissionName(),
					groupId,
					() -> UriInfoUtil.getBaseUriBuilder(
						"headless-admin-taxonomy", uriInfo),
					uriInfo));
		}

		return actions;
	}

	@Reference
	private ScopeChecker _scopeChecker;

}