/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.style.book.internal.exportimport.staged.model.repository;

import com.liferay.exportimport.kernel.lar.PortletDataContext;
import com.liferay.exportimport.staged.model.repository.StagedModelRepository;
import com.liferay.exportimport.staged.model.repository.StagedModelRepositoryHelper;
import com.liferay.portal.kernel.dao.orm.ExportActionableDynamicQuery;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.style.book.model.StyleBookEntry;
import com.liferay.style.book.service.StyleBookEntryLocalService;

import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Pavel Savinov
 */
@Component(
	property = "model.class.name=com.liferay.style.book.model.StyleBookEntry",
	service = StagedModelRepository.class
)
public class StylebookEntryStagedModelRepository
	implements StagedModelRepository<StyleBookEntry> {

	@Override
	public StyleBookEntry addStagedModel(
			PortletDataContext portletDataContext,
			StyleBookEntry styleBookEntry)
		throws PortalException {

		long userId = portletDataContext.getUserId(
			styleBookEntry.getUserUuid());

		ServiceContext serviceContext = portletDataContext.createServiceContext(
			styleBookEntry);

		if (portletDataContext.isDataStrategyMirror()) {
			serviceContext.setUuid(styleBookEntry.getUuid());
		}

		return _styleBookEntryLocalService.addStyleBookEntry(
			styleBookEntry.getExternalReferenceCode(), userId,
			styleBookEntry.getGroupId(),
			styleBookEntry.isDefaultStyleBookEntry(),
			styleBookEntry.getFrontendTokenDefinition(),
			styleBookEntry.getFrontendTokensValues(), styleBookEntry.getName(),
			styleBookEntry.getStyleBookEntryKey(), styleBookEntry.getThemeId(),
			serviceContext);
	}

	@Override
	public void deleteStagedModel(
			String uuid, long groupId, String className, String extraData)
		throws PortalException {

		StyleBookEntry styleBookEntry = fetchStagedModelByUuidAndGroupId(
			uuid, groupId);

		if (styleBookEntry != null) {
			deleteStagedModel(styleBookEntry);
		}
	}

	@Override
	public void deleteStagedModel(StyleBookEntry styleBookEntry)
		throws PortalException {

		_styleBookEntryLocalService.deleteStyleBookEntry(styleBookEntry);
	}

	@Override
	public void deleteStagedModels(PortletDataContext portletDataContext)
		throws PortalException {
	}

	@Override
	public StyleBookEntry fetchMissingReference(String uuid, long groupId) {
		return (StyleBookEntry)
			_stagedModelRepositoryHelper.fetchMissingReference(
				uuid, groupId, this);
	}

	@Override
	public StyleBookEntry fetchStagedModelByUuidAndGroupId(
		String uuid, long groupId) {

		return _styleBookEntryLocalService.fetchStyleBookEntryByUuidAndGroupId(
			uuid, groupId);
	}

	@Override
	public List<StyleBookEntry> fetchStagedModelsByUuidAndCompanyId(
		String uuid, long companyId) {

		return _styleBookEntryLocalService.
			getStyleBookEntriesByUuidAndCompanyId(uuid, companyId);
	}

	@Override
	public ExportActionableDynamicQuery getExportActionableDynamicQuery(
		PortletDataContext portletDataContext) {

		return _styleBookEntryLocalService.getExportActionableDynamicQuery(
			portletDataContext);
	}

	@Override
	public StyleBookEntry getStagedModel(long classPK) throws PortalException {
		return _styleBookEntryLocalService.getStyleBookEntry(classPK);
	}

	@Override
	public StyleBookEntry saveStagedModel(StyleBookEntry styleBookEntry)
		throws PortalException {

		return _styleBookEntryLocalService.updateStyleBookEntry(styleBookEntry);
	}

	@Override
	public StyleBookEntry updateStagedModel(
			PortletDataContext portletDataContext,
			StyleBookEntry styleBookEntry)
		throws PortalException {

		return _styleBookEntryLocalService.updateStyleBookEntry(
			portletDataContext.getUserId(styleBookEntry.getUserUuid()),
			styleBookEntry.getStyleBookEntryId(),
			styleBookEntry.isDefaultStyleBookEntry(),
			styleBookEntry.getFrontendTokenDefinition(),
			styleBookEntry.getFrontendTokensValues(), styleBookEntry.getName(),
			styleBookEntry.getStyleBookEntryKey(),
			styleBookEntry.getPreviewFileEntryId(),
			portletDataContext.createServiceContext(styleBookEntry));
	}

	@Reference
	private StagedModelRepositoryHelper _stagedModelRepositoryHelper;

	@Reference
	private StyleBookEntryLocalService _styleBookEntryLocalService;

}