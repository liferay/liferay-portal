/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.depot.internal.model.listener;

import com.liferay.depot.model.DepotAppCustomization;
import com.liferay.depot.model.DepotEntry;
import com.liferay.depot.model.DepotEntryGroupRel;
import com.liferay.depot.service.DepotAppCustomizationLocalService;
import com.liferay.depot.service.DepotEntryGroupRelLocalService;
import com.liferay.depot.service.DepotEntryLocalService;
import com.liferay.portal.kernel.exception.ModelListenerException;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.BaseModelListener;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.ModelListener;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.transaction.TransactionCallbackUtil;
import com.liferay.portal.kernel.util.ParamUtil;

import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Adolfo Pérez
 */
@Component(service = ModelListener.class)
public class GroupModelListener extends BaseModelListener<Group> {

	@Override
	public void onAfterCreate(Group group) throws ModelListenerException {
		if ((group != null) && group.isDepot() &&
			_isStaging(ServiceContextThreadLocal.getServiceContext())) {

			TransactionCallbackUtil.registerCommitCallback(
				() -> {
					_copyLiveDepotEntryGroupRelsToStaging(group);

					return null;
				});
		}
	}

	@Override
	public void onAfterRemove(Group group) throws ModelListenerException {
		if ((group != null) && group.isDepot()) {
			TransactionCallbackUtil.registerCommitCallback(
				() -> {
					DepotEntry depotEntry =
						_depotEntryLocalService.fetchGroupDepotEntry(
							group.getGroupId());

					if (depotEntry != null) {
						_depotEntryLocalService.deleteDepotEntry(
							depotEntry.getDepotEntryId());
					}

					return null;
				});
		}
	}

	@Override
	public void onBeforeCreate(Group group) throws ModelListenerException {
		try {
			ServiceContext serviceContext =
				ServiceContextThreadLocal.getServiceContext();

			if (group.isDepot() && _isStaging(serviceContext)) {
				DepotEntry depotEntry = _depotEntryLocalService.addDepotEntry(
					group, serviceContext);

				group.setClassPK(depotEntry.getDepotEntryId());

				Group liveGroup = group.getLiveGroup();

				_copyDepotAppCustomizations(
					depotEntry.getDepotEntryId(), liveGroup.getClassPK());
			}
		}
		catch (PortalException portalException) {
			throw new ModelListenerException(portalException);
		}
	}

	@Override
	public void onBeforeRemove(Group group) throws ModelListenerException {
		if (!group.isSite()) {
			return;
		}

		_depotEntryGroupRelLocalService.deleteToGroupDepotEntryGroupRels(
			group.getGroupId());
	}

	private void _copyDepotAppCustomizations(
			long newDepotEntryId, long oldDepotEntryId)
		throws PortalException {

		List<DepotAppCustomization> depotAppCustomizations =
			_depotAppCustomizationLocalService.getDepotAppCustomizations(
				oldDepotEntryId);

		for (DepotAppCustomization depotAppCustomization :
				depotAppCustomizations) {

			_depotAppCustomizationLocalService.updateDepotAppCustomization(
				newDepotEntryId, depotAppCustomization.isEnabled(),
				depotAppCustomization.getPortletId());
		}
	}

	private void _copyLiveDepotEntryGroupRelsToStaging(Group group)
		throws PortalException {

		Group liveGroup = group.getLiveGroup();

		if (liveGroup == null) {
			return;
		}

		DepotEntry liveDepotEntry =
			_depotEntryLocalService.fetchGroupDepotEntry(
				liveGroup.getGroupId());

		if (liveDepotEntry == null) {
			return;
		}

		List<DepotEntryGroupRel> depotEntryGroupRels =
			_depotEntryGroupRelLocalService.getDepotEntryGroupRels(
				liveDepotEntry);

		for (DepotEntryGroupRel depotEntryGroupRel : depotEntryGroupRels) {
			Group groupRel = _groupLocalService.getGroup(
				depotEntryGroupRel.getGroupId());

			if (groupRel.isStagingGroup()) {
				DepotEntry depotEntry =
					_depotEntryLocalService.fetchGroupDepotEntry(
						group.getGroupId());

				DepotEntryGroupRel stagedDepotEntryGroupRel =
					_depotEntryGroupRelLocalService.addDepotEntryGroupRel(
						depotEntry.getDepotEntryId(),
						depotEntryGroupRel.getGroupId());

				stagedDepotEntryGroupRel.setDdmStructuresAvailable(
					depotEntryGroupRel.isDdmStructuresAvailable());
				stagedDepotEntryGroupRel.setSearchable(
					depotEntryGroupRel.isSearchable());

				_depotEntryGroupRelLocalService.updateDepotEntryGroupRel(
					stagedDepotEntryGroupRel);
			}
		}
	}

	private boolean _isStaging(ServiceContext serviceContext) {
		if (serviceContext == null) {
			return false;
		}

		return ParamUtil.getBoolean(serviceContext, "staging");
	}

	@Reference
	private DepotAppCustomizationLocalService
		_depotAppCustomizationLocalService;

	@Reference
	private DepotEntryGroupRelLocalService _depotEntryGroupRelLocalService;

	@Reference
	private DepotEntryLocalService _depotEntryLocalService;

	@Reference
	private GroupLocalService _groupLocalService;

}