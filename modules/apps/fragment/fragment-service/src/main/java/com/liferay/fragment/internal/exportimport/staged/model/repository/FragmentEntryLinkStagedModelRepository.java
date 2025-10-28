/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.fragment.internal.exportimport.staged.model.repository;

import com.liferay.exportimport.kernel.lar.PortletDataContext;
import com.liferay.exportimport.kernel.lar.StagedModelModifiedDateComparator;
import com.liferay.exportimport.staged.model.repository.StagedModelRepository;
import com.liferay.exportimport.staged.model.repository.StagedModelRepositoryHelper;
import com.liferay.fragment.model.FragmentEntryLink;
import com.liferay.fragment.service.FragmentEntryLinkLocalService;
import com.liferay.portal.kernel.dao.orm.ExportActionableDynamicQuery;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.service.ServiceContext;

import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Pavel Savinov
 */
@Component(
	property = "model.class.name=com.liferay.fragment.model.FragmentEntryLink",
	service = StagedModelRepository.class
)
public class FragmentEntryLinkStagedModelRepository
	implements StagedModelRepository<FragmentEntryLink> {

	@Override
	public FragmentEntryLink addStagedModel(
			PortletDataContext portletDataContext,
			FragmentEntryLink fragmentEntryLink)
		throws PortalException {

		long userId = portletDataContext.getUserId(
			fragmentEntryLink.getUserUuid());

		ServiceContext serviceContext = portletDataContext.createServiceContext(
			fragmentEntryLink);

		if (portletDataContext.isDataStrategyMirror()) {
			serviceContext.setUuid(fragmentEntryLink.getUuid());
		}

		return _fragmentEntryLinkLocalService.addFragmentEntryLink(
			fragmentEntryLink.getExternalReferenceCode(), userId,
			fragmentEntryLink.getGroupId(),
			fragmentEntryLink.getOriginalFragmentEntryLinkERC(),
			fragmentEntryLink.getFragmentEntryERC(),
			fragmentEntryLink.getFragmentEntryScopeERC(),
			fragmentEntryLink.getSegmentsExperienceId(),
			fragmentEntryLink.getPlid(), fragmentEntryLink.getCss(),
			fragmentEntryLink.getHtml(), fragmentEntryLink.getJs(),
			fragmentEntryLink.getConfiguration(),
			fragmentEntryLink.getEditableValues(),
			fragmentEntryLink.getNamespace(), fragmentEntryLink.getPosition(),
			fragmentEntryLink.getRendererKey(), fragmentEntryLink.getType(),
			serviceContext);
	}

	@Override
	public void deleteStagedModel(FragmentEntryLink fragmentEntryLink) {
		_fragmentEntryLinkLocalService.deleteFragmentEntryLink(
			fragmentEntryLink);
	}

	@Override
	public void deleteStagedModel(
		String uuid, long groupId, String className, String extraData) {

		FragmentEntryLink fragmentEntry = fetchStagedModelByUuidAndGroupId(
			uuid, groupId);

		if (fragmentEntry != null) {
			deleteStagedModel(fragmentEntry);
		}
	}

	@Override
	public void deleteStagedModels(PortletDataContext portletDataContext) {
		_fragmentEntryLinkLocalService.deleteFragmentEntryLinks(
			portletDataContext.getScopeGroupId());
	}

	@Override
	public FragmentEntryLink fetchMissingReference(String uuid, long groupId) {
		return _stagedModelRepositoryHelper.fetchMissingReference(
			uuid, groupId, this);
	}

	@Override
	public FragmentEntryLink fetchStagedModelByUuidAndGroupId(
		String uuid, long groupId) {

		return _fragmentEntryLinkLocalService.
			fetchFragmentEntryLinkByUuidAndGroupId(uuid, groupId);
	}

	@Override
	public List<FragmentEntryLink> fetchStagedModelsByUuidAndCompanyId(
		String uuid, long companyId) {

		return _fragmentEntryLinkLocalService.
			getFragmentEntryLinksByUuidAndCompanyId(
				uuid, companyId, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
				new StagedModelModifiedDateComparator<>());
	}

	@Override
	public ExportActionableDynamicQuery getExportActionableDynamicQuery(
		PortletDataContext portletDataContext) {

		return _fragmentEntryLinkLocalService.getExportActionableDynamicQuery(
			portletDataContext);
	}

	@Override
	public FragmentEntryLink getStagedModel(long fragmentEntryLinkId)
		throws PortalException {

		return _fragmentEntryLinkLocalService.getFragmentEntryLink(
			fragmentEntryLinkId);
	}

	@Override
	public FragmentEntryLink saveStagedModel(
		FragmentEntryLink fragmentEntryLink) {

		return _fragmentEntryLinkLocalService.updateFragmentEntryLink(
			fragmentEntryLink);
	}

	@Override
	public FragmentEntryLink updateStagedModel(
			PortletDataContext portletDataContext,
			FragmentEntryLink fragmentEntryLink)
		throws PortalException {

		long userId = portletDataContext.getUserId(
			fragmentEntryLink.getUserUuid());

		ServiceContext serviceContext = portletDataContext.createServiceContext(
			fragmentEntryLink);

		return _fragmentEntryLinkLocalService.updateFragmentEntryLink(
			userId, fragmentEntryLink.getFragmentEntryLinkId(),
			fragmentEntryLink.getOriginalFragmentEntryLinkERC(),
			fragmentEntryLink.getFragmentEntryERC(),
			fragmentEntryLink.getFragmentEntryScopeERC(),
			fragmentEntryLink.getPlid(), fragmentEntryLink.getCss(),
			fragmentEntryLink.getHtml(), fragmentEntryLink.getJs(),
			fragmentEntryLink.getConfiguration(),
			fragmentEntryLink.getEditableValues(),
			fragmentEntryLink.getNamespace(), fragmentEntryLink.getPosition(),
			fragmentEntryLink.getType(), serviceContext);
	}

	@Reference
	private FragmentEntryLinkLocalService _fragmentEntryLinkLocalService;

	@Reference
	private StagedModelRepositoryHelper _stagedModelRepositoryHelper;

}