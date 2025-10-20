/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.internal.exportimport.staged.model.repository;

import com.liferay.exportimport.kernel.lar.PortletDataContext;
import com.liferay.exportimport.staged.model.repository.StagedModelRepository;
import com.liferay.portal.kernel.dao.orm.ExportActionableDynamicQuery;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.model.adapter.util.ModelAdapterUtil;
import com.liferay.site.model.adapter.StagedGroup;

import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Daniel Kocsis
 */
@Component(
	property = "model.class.name=com.liferay.site.model.adapter.StagedGroup",
	service = StagedModelRepository.class
)
public class StagedGroupStagedModelRepository
	implements StagedModelRepository<StagedGroup> {

	@Override
	public StagedGroup addStagedModel(
			PortletDataContext portletDataContext, StagedGroup stagedGroup)
		throws PortalException {

		throw new UnsupportedOperationException();
	}

	@Override
	public void deleteStagedModel(StagedGroup stagedGroup)
		throws PortalException {

		throw new UnsupportedOperationException();
	}

	@Override
	public void deleteStagedModel(
			String uuid, long groupId, String className, String extraData)
		throws PortalException {

		throw new UnsupportedOperationException();
	}

	@Override
	public void deleteStagedModels(PortletDataContext portletDataContext)
		throws PortalException {

		throw new UnsupportedOperationException();
	}

	@Override
	public StagedGroup fetchStagedModelByUuidAndGroupId(
		String uuid, long groupId) {

		return ModelAdapterUtil.adapt(
			_groupLocalService.fetchGroup(groupId), Group.class,
			StagedGroup.class);
	}

	@Override
	public List<StagedGroup> fetchStagedModelsByUuidAndCompanyId(
		String uuid, long companyId) {

		throw new UnsupportedOperationException();
	}

	@Override
	public ExportActionableDynamicQuery getExportActionableDynamicQuery(
		PortletDataContext portletDataContext) {

		throw new UnsupportedOperationException();
	}

	@Override
	public StagedGroup saveStagedModel(StagedGroup stagedGroup)
		throws PortalException {

		Group group = stagedGroup.getGroup();

		group = _groupLocalService.updateGroup(group);

		return ModelAdapterUtil.adapt(group, Group.class, StagedGroup.class);
	}

	@Override
	public StagedGroup updateStagedModel(
			PortletDataContext portletDataContext, StagedGroup stagedGroup)
		throws PortalException {

		ServiceContext serviceContext = portletDataContext.createServiceContext(
			stagedGroup);

		Group group = stagedGroup.getGroup();

		group = _groupLocalService.updateGroup(
			group.getGroupId(), group.getParentGroupId(), group.getNameMap(),
			group.getDescriptionMap(), group.getType(), null,
			group.isManualMembership(), group.getMembershipRestriction(),
			group.getFriendlyURL(), group.isInheritContent(), group.isActive(),
			serviceContext);

		return ModelAdapterUtil.adapt(group, Group.class, StagedGroup.class);
	}

	@Reference(unbind = "-")
	private GroupLocalService _groupLocalService;

}