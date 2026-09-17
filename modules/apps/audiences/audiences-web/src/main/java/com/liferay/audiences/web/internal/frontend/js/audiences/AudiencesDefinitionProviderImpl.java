/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.audiences.web.internal.frontend.js.audiences;

import com.liferay.audiences.cache.AudiencesDefinitionCache;
import com.liferay.audiences.criteria.AudiencesCriteriaProvider;
import com.liferay.audiences.model.AudiencesEntry;
import com.liferay.audiences.service.AudiencesEntryGroupRelLocalService;
import com.liferay.audiences.service.AudiencesEntryLocalService;
import com.liferay.frontend.js.audiences.AudiencesDefinition;
import com.liferay.frontend.js.audiences.AudiencesDefinitionProvider;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.frontend.hashed.files.HashedFilesUtil;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.util.OrderByComparatorFactoryUtil;

import java.util.List;
import java.util.Set;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Eudaldo Alonso
 */
@Component(service = AudiencesDefinitionProvider.class)
public class AudiencesDefinitionProviderImpl
	implements AudiencesDefinitionProvider {

	@Override
	public AudiencesDefinition getAudiencesDefinition(long companyId) {
		if (!FeatureFlagManagerUtil.isEnabled(companyId, "LPD-85746")) {
			return null;
		}

		AudiencesDefinition audiencesDefinition =
			_audiencesDefinitionCache.getAudiencesDefinition(companyId);

		if (audiencesDefinition != null) {
			return audiencesDefinition;
		}

		JSONArray audiencesJSONArray = _jsonFactory.createJSONArray();

		List<AudiencesEntry> audiencesEntries =
			_audiencesEntryLocalService.getAudiencesEntries(
				companyId, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
				OrderByComparatorFactoryUtil.create(
					"AudiencesEntry", "createDate", true));

		Set<String> customAudiencesCriteriaKeys =
			_audiencesCriteriaProvider.getCustomAudiencesCriteriaKeys(
				companyId);

		for (AudiencesEntry audiencesEntry : audiencesEntries) {
			JSONObject jsonObject = _getAudiencesEntryJSONObject(
				audiencesEntry);

			if (!_hasValidAttributes(jsonObject, customAudiencesCriteriaKeys)) {
				if (_log.isWarnEnabled()) {
					_log.warn(
						StringBundler.concat(
							"Skipping audiences entry ",
							audiencesEntry.getAudiencesEntryId(),
							" because it has an unregistered custom ",
							"attribute"));
				}

				continue;
			}

			JSONArray scopeJSONArray = _getScopeJSONArray(audiencesEntry);

			if (scopeJSONArray.length() > 0) {
				jsonObject.put("scope", scopeJSONArray);
			}

			audiencesJSONArray.put(
				jsonObject.put(
					"id", audiencesEntry.getExternalReferenceCode()));
		}

		String json = JSONUtil.put(
			"audiences", audiencesJSONArray
		).toString();

		audiencesDefinition = new AudiencesDefinition(
			json, HashedFilesUtil.computeHash(json));

		_audiencesDefinitionCache.putAudiencesDefinition(
			companyId, audiencesDefinition);

		return audiencesDefinition;
	}

	private JSONObject _getAudiencesEntryJSONObject(
		AudiencesEntry audiencesEntry) {

		try {
			return _jsonFactory.createJSONObject(audiencesEntry.getJSON());
		}
		catch (PortalException portalException) {
			if (_log.isDebugEnabled()) {
				_log.debug(portalException);
			}
		}

		return _jsonFactory.createJSONObject();
	}

	private JSONArray _getScopeJSONArray(AudiencesEntry audiencesEntry) {
		return JSONUtil.toJSONArray(
			_audiencesEntryGroupRelLocalService.
				getAudiencesEntryGroupRelsByAudienceEntryERC(
					audiencesEntry.getCompanyId(),
					audiencesEntry.getExternalReferenceCode()),
			audiencesEntryGroupRel -> {
				Group group =
					_groupLocalService.fetchGroupByExternalReferenceCode(
						audiencesEntryGroupRel.getGroupERC(),
						audiencesEntryGroupRel.getCompanyId());

				if (group == null) {
					return null;
				}

				return group.getGroupId();
			},
			_log);
	}

	private boolean _hasValidAttributes(
		JSONObject jsonObject, Set<String> customAudiencesCriteriaKeys) {

		JSONArray rulesJSONArray = jsonObject.getJSONArray("rules");

		if (rulesJSONArray == null) {
			String attribute = jsonObject.getString("attribute");

			if (attribute.startsWith("custom:") &&
				!customAudiencesCriteriaKeys.contains(attribute)) {

				return false;
			}

			return true;
		}

		for (int i = 0; i < rulesJSONArray.length(); i++) {
			if (!_hasValidAttributes(
					rulesJSONArray.getJSONObject(i),
					customAudiencesCriteriaKeys)) {

				return false;
			}
		}

		return true;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		AudiencesDefinitionProviderImpl.class);

	@Reference
	private AudiencesCriteriaProvider _audiencesCriteriaProvider;

	@Reference
	private AudiencesDefinitionCache _audiencesDefinitionCache;

	@Reference
	private AudiencesEntryGroupRelLocalService
		_audiencesEntryGroupRelLocalService;

	@Reference
	private AudiencesEntryLocalService _audiencesEntryLocalService;

	@Reference
	private GroupLocalService _groupLocalService;

	@Reference
	private JSONFactory _jsonFactory;

}