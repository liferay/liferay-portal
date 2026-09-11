/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.friendly.url.internal.exportimport.staged.model.repository;

import com.liferay.exportimport.kernel.lar.PortletDataContext;
import com.liferay.exportimport.staged.model.repository.StagedModelRepository;
import com.liferay.exportimport.staged.model.repository.StagedModelRepositoryHelper;
import com.liferay.friendly.url.internal.exportimport.lar.FriendlyURLExportImportPathUtil;
import com.liferay.friendly.url.model.FriendlyURLEntry;
import com.liferay.friendly.url.model.FriendlyURLEntryLocalization;
import com.liferay.friendly.url.service.FriendlyURLEntryLocalService;
import com.liferay.portal.kernel.dao.orm.ExportActionableDynamicQuery;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.util.Localization;
import com.liferay.portal.kernel.util.MapUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Adolfo Pérez
 */
@Component(
	property = "model.class.name=com.liferay.friendly.url.model.FriendlyURLEntry",
	service = StagedModelRepository.class
)
public class FriendlyURLEntryStagedModelRepository
	implements StagedModelRepository<FriendlyURLEntry> {

	@Override
	public FriendlyURLEntry addStagedModel(
			PortletDataContext portletDataContext,
			FriendlyURLEntry friendlyURLEntry)
		throws PortalException {

		ServiceContext serviceContext = portletDataContext.createServiceContext(
			friendlyURLEntry);

		if (portletDataContext.isDataStrategyMirror()) {
			serviceContext.setUuid(friendlyURLEntry.getUuid());
		}

		return _friendlyURLEntryLocalService.addFriendlyURLEntry(
			friendlyURLEntry.getGroupId(), friendlyURLEntry.getClassNameId(),
			friendlyURLEntry.getClassPK(),
			_getLocalizationMap(portletDataContext, friendlyURLEntry),
			serviceContext);
	}

	@Override
	public void deleteStagedModel(FriendlyURLEntry friendlyURLEntry) {
		_friendlyURLEntryLocalService.deleteFriendlyURLEntry(friendlyURLEntry);
	}

	@Override
	public void deleteStagedModel(
		String uuid, long groupId, String className, String extraData) {

		FriendlyURLEntry friendlyURLEntry = fetchStagedModelByUuidAndGroupId(
			uuid, groupId);

		if (friendlyURLEntry != null) {
			deleteStagedModel(friendlyURLEntry);
		}
	}

	@Override
	public void deleteStagedModels(PortletDataContext portletDataContext) {
		long classNameId = MapUtil.getLong(
			portletDataContext.getParameterMap(),
			"parentStagedModelClassNameId");

		_friendlyURLEntryLocalService.deleteGroupFriendlyURLEntries(
			portletDataContext.getGroupId(), classNameId);
	}

	@Override
	public FriendlyURLEntry fetchMissingReference(String uuid, long groupId) {
		return _stagedModelRepositoryHelper.fetchMissingReference(
			uuid, groupId, this);
	}

	@Override
	public FriendlyURLEntry fetchStagedModelByUuidAndGroupId(
		String uuid, long groupId) {

		return _friendlyURLEntryLocalService.
			fetchFriendlyURLEntryByUuidAndGroupId(uuid, groupId);
	}

	@Override
	public List<FriendlyURLEntry> fetchStagedModelsByUuidAndCompanyId(
		String uuid, long companyId) {

		return _friendlyURLEntryLocalService.
			getFriendlyURLEntriesByUuidAndCompanyId(uuid, companyId);
	}

	@Override
	public ExportActionableDynamicQuery getExportActionableDynamicQuery(
		PortletDataContext portletDataContext) {

		return _friendlyURLEntryLocalService.getExportActionableDynamicQuery(
			portletDataContext);
	}

	@Override
	public FriendlyURLEntry getStagedModel(long friendlyURLEntryId)
		throws PortalException {

		return _friendlyURLEntryLocalService.getFriendlyURLEntry(
			friendlyURLEntryId);
	}

	@Override
	public void restoreStagedModel(
		PortletDataContext portletDataContext, FriendlyURLEntry stagedModel) {
	}

	@Override
	public FriendlyURLEntry saveStagedModel(FriendlyURLEntry friendlyURLEntry) {
		List<FriendlyURLEntryLocalization> friendlyURLEntryLocalizations =
			_friendlyURLEntryLocalService.getFriendlyURLEntryLocalizations(
				friendlyURLEntry.getFriendlyURLEntryId());

		for (FriendlyURLEntryLocalization friendlyURLEntryLocalization :
				friendlyURLEntryLocalizations) {

			friendlyURLEntryLocalization.setUrlTitle(
				_friendlyURLEntryLocalService.getUniqueUrlTitle(
					friendlyURLEntry.getGroupId(),
					friendlyURLEntry.getClassNameId(),
					friendlyURLEntry.getParentClassPK(),
					friendlyURLEntry.getClassPK(),
					friendlyURLEntryLocalization.getUrlTitle()));

			_friendlyURLEntryLocalService.updateFriendlyURLLocalization(
				friendlyURLEntryLocalization);
		}

		return _friendlyURLEntryLocalService.updateFriendlyURLEntry(
			friendlyURLEntry);
	}

	@Override
	public FriendlyURLEntry updateStagedModel(
			PortletDataContext portletDataContext,
			FriendlyURLEntry friendlyURLEntry)
		throws PortalException {

		FriendlyURLEntry existingFriendlyURLEntry =
			fetchStagedModelByUuidAndGroupId(
				friendlyURLEntry.getUuid(),
				portletDataContext.getScopeGroupId());

		if (existingFriendlyURLEntry == null) {
			return null;
		}

		return _friendlyURLEntryLocalService.updateFriendlyURLEntry(
			existingFriendlyURLEntry.getFriendlyURLEntryId(),
			existingFriendlyURLEntry.getClassNameId(),
			existingFriendlyURLEntry.getClassPK(),
			existingFriendlyURLEntry.getDefaultLanguageId(),
			_getLocalizationMap(portletDataContext, friendlyURLEntry),
			portletDataContext.createServiceContext(friendlyURLEntry));
	}

	private Map<String, String> _getLocalizationMap(
		PortletDataContext portletDataContext,
		FriendlyURLEntry friendlyURLEntry) {

		String modelPath = FriendlyURLExportImportPathUtil.getModelPath(
			portletDataContext, friendlyURLEntry);

		Map<Locale, String> localeLocalizationMap =
			_localization.getLocalizationMap(
				portletDataContext.getZipEntryAsString(modelPath));

		Map<String, String> languageIdLocalizationMap = new HashMap<>();

		for (Map.Entry<Locale, String> entry :
				localeLocalizationMap.entrySet()) {

			String urlTitle = entry.getValue();

			FriendlyURLEntry existingFriendlyURLEntry =
				_friendlyURLEntryLocalService.fetchFriendlyURLEntry(
					friendlyURLEntry.getGroupId(),
					friendlyURLEntry.getClassNameId(),
					friendlyURLEntry.getParentClassPK(), urlTitle);

			if (existingFriendlyURLEntry != null) {
				urlTitle = _friendlyURLEntryLocalService.getUniqueUrlTitle(
					friendlyURLEntry.getGroupId(),
					friendlyURLEntry.getClassNameId(),
					friendlyURLEntry.getParentClassPK(),
					friendlyURLEntry.getClassPK(), urlTitle);
			}

			languageIdLocalizationMap.put(
				_language.getLanguageId(entry.getKey()), urlTitle);
		}

		return languageIdLocalizationMap;
	}

	@Reference
	private FriendlyURLEntryLocalService _friendlyURLEntryLocalService;

	@Reference
	private Language _language;

	@Reference
	private Localization _localization;

	@Reference
	private StagedModelRepositoryHelper _stagedModelRepositoryHelper;

}