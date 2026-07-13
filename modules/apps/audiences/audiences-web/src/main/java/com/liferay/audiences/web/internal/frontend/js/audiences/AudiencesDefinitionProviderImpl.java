/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.audiences.web.internal.frontend.js.audiences;

import com.liferay.audiences.model.AudiencesEntry;
import com.liferay.audiences.service.AudiencesEntryLocalService;
import com.liferay.frontend.js.audiences.AudiencesDefinition;
import com.liferay.frontend.js.audiences.AudiencesDefinitionProvider;
import com.liferay.portal.kernel.cache.MultiVMPool;
import com.liferay.portal.kernel.cache.PortalCache;
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

import java.util.List;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
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

		AudiencesDefinition audiencesDefinition = _portalCache.get(companyId);

		if (audiencesDefinition != null) {
			return audiencesDefinition;
		}

		JSONArray audiencesJSONArray = _jsonFactory.createJSONArray();

		List<AudiencesEntry> audiencesEntries =
			_audiencesEntryLocalService.getAudiencesEntries(
				companyId, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);

		for (AudiencesEntry audiencesEntry : audiencesEntries) {
			JSONObject jsonObject = _getAudiencesEntryJSONObject(
				audiencesEntry);

			audiencesJSONArray.put(
				jsonObject.put(
					"id", audiencesEntry.getExternalReferenceCode()));
		}

		String json = JSONUtil.put(
			"audiences", audiencesJSONArray
		).toString();

		audiencesDefinition = new AudiencesDefinition(
			json, HashedFilesUtil.computeHash(json));

		_portalCache.put(companyId, audiencesDefinition);

		return audiencesDefinition;
	}

	@Activate
	protected void activate() {
		_portalCache =
			(PortalCache<Long, AudiencesDefinition>)_multiVMPool.getPortalCache(
				AudiencesEntry.class.getName());
	}

	@Deactivate
	protected void deactivate() {
		_multiVMPool.removePortalCache(AudiencesEntry.class.getName());
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

	private static final Log _log = LogFactoryUtil.getLog(
		AudiencesDefinitionProviderImpl.class);

	@Reference
	private AudiencesEntryLocalService _audiencesEntryLocalService;

	@Reference
	private JSONFactory _jsonFactory;

	@Reference
	private MultiVMPool _multiVMPool;

	private PortalCache<Long, AudiencesDefinition> _portalCache;

}