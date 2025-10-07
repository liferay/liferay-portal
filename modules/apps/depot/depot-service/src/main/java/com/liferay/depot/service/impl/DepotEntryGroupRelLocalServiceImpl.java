/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.depot.service.impl;

import com.liferay.depot.constants.DepotConstants;
import com.liferay.depot.model.DepotEntry;
import com.liferay.depot.model.DepotEntryGroupRel;
import com.liferay.depot.service.base.DepotEntryGroupRelLocalServiceBaseImpl;
import com.liferay.depot.service.persistence.DepotEntryPersistence;
import com.liferay.portal.aop.AopService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.LayoutSet;
import com.liferay.portal.kernel.model.LayoutSetPrototype;
import com.liferay.portal.kernel.model.SystemEventConstants;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.LayoutSetLocalService;
import com.liferay.portal.kernel.service.LayoutSetPrototypeLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.systemevent.SystemEvent;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.UnicodeProperties;

import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Brian Wing Shun Chan
 */
@Component(
	property = "model.class.name=com.liferay.depot.model.DepotEntryGroupRel",
	service = AopService.class
)
public class DepotEntryGroupRelLocalServiceImpl
	extends DepotEntryGroupRelLocalServiceBaseImpl {

	@Override
	public DepotEntryGroupRel addDepotEntryGroupRel(
			boolean ddmStructuresAvailable, long depotEntryId, long toGroupId,
			boolean searchable)
		throws PortalException {

		DepotEntryGroupRel depotEntryGroupRel =
			depotEntryGroupRelPersistence.fetchByD_TGI(depotEntryId, toGroupId);

		if (depotEntryGroupRel != null) {
			return depotEntryGroupRel;
		}

		Group toGroup = _groupLocalService.getGroup(toGroupId);

		if (toGroup.isLayoutSetPrototype()) {
			_connectLayoutSetPrototypeGroups(depotEntryId, toGroup);
		}

		depotEntryGroupRel = depotEntryGroupRelPersistence.create(
			counterLocalService.increment());

		depotEntryGroupRel.setGroupId(toGroupId);
		depotEntryGroupRel.setDdmStructuresAvailable(ddmStructuresAvailable);
		depotEntryGroupRel.setDepotEntryId(depotEntryId);
		depotEntryGroupRel.setSearchable(searchable);
		depotEntryGroupRel.setToGroupId(toGroupId);

		DepotEntry depotEntry = _depotEntryPersistence.findByPrimaryKey(
			depotEntryId);

		depotEntryGroupRel.setType(depotEntry.getType());

		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		if (serviceContext != null) {
			depotEntryGroupRel.setUuid(serviceContext.getUuid());
		}

		return depotEntryGroupRelPersistence.update(depotEntryGroupRel);
	}

	@Override
	public DepotEntryGroupRel addDepotEntryGroupRel(
			long depotEntryId, long toGroupId)
		throws PortalException {

		return addDepotEntryGroupRel(depotEntryId, toGroupId, true);
	}

	@Override
	public DepotEntryGroupRel addDepotEntryGroupRel(
			long depotEntryId, long toGroupId, boolean searchable)
		throws PortalException {

		return addDepotEntryGroupRel(
			false, depotEntryId, toGroupId, searchable);
	}

	@Override
	@SystemEvent(type = SystemEventConstants.TYPE_DELETE)
	public DepotEntryGroupRel deleteDepotEntryGroupRel(
			DepotEntryGroupRel depotEntryGroupRel)
		throws PortalException {

		Group toGroup = _groupLocalService.getGroup(
			depotEntryGroupRel.getToGroupId());

		if (toGroup.isLayoutSetPrototype()) {
			_disconnectLayoutSetPrototypeGroups(
				depotEntryGroupRel.getDepotEntryId(), toGroup);
		}

		return super.deleteDepotEntryGroupRel(depotEntryGroupRel);
	}

	@Override
	public DepotEntryGroupRel deleteDepotEntryGroupRel(
			long depotEntryGroupRelId)
		throws PortalException {

		return depotEntryGroupRelLocalService.deleteDepotEntryGroupRel(
			getDepotEntryGroupRel(depotEntryGroupRelId));
	}

	@Override
	public void deleteToGroupDepotEntryGroupRels(long toGroupId) {
		depotEntryGroupRelPersistence.removeByToGroupId(toGroupId);
	}

	@Override
	public DepotEntryGroupRel fetchDepotEntryGroupRelByDepotEntryIdToGroupId(
		long depotEntryId, long toGroupId) {

		return depotEntryGroupRelPersistence.fetchByD_TGI(
			depotEntryId, toGroupId);
	}

	@Override
	public DepotEntryGroupRel getDepotEntryGroupRelByDepotEntryIdToGroupId(
			long depotEntryId, long toGroupId)
		throws PortalException {

		return depotEntryGroupRelPersistence.findByD_TGI(
			depotEntryId, toGroupId);
	}

	@Override
	public List<DepotEntryGroupRel> getDepotEntryGroupRels(
		DepotEntry depotEntry) {

		return depotEntryGroupRelPersistence.findByDepotEntryId(
			depotEntry.getDepotEntryId());
	}

	@Override
	public List<DepotEntryGroupRel> getDepotEntryGroupRels(
		DepotEntry depotEntry, int start, int end) {

		return depotEntryGroupRelPersistence.findByDepotEntryId(
			depotEntry.getDepotEntryId(), start, end);
	}

	@Override
	public List<DepotEntryGroupRel> getDepotEntryGroupRels(
		long groupId, int type, int start, int end) {

		if (type == DepotConstants.TYPE_ANY) {
			return depotEntryGroupRelPersistence.findByToGroupId(groupId);
		}

		return depotEntryGroupRelPersistence.findByTGI_T(
			groupId, type, start, end);
	}

	@Override
	public int getDepotEntryGroupRelsCount(DepotEntry depotEntry) {
		return depotEntryGroupRelPersistence.countByDepotEntryId(
			depotEntry.getDepotEntryId());
	}

	@Override
	public int getDepotEntryGroupRelsCount(long groupId, int type) {
		if (type == DepotConstants.TYPE_ANY) {
			return depotEntryGroupRelPersistence.countByToGroupId(groupId);
		}

		return depotEntryGroupRelPersistence.countByTGI_T(groupId, type);
	}

	@Override
	public List<DepotEntryGroupRel> getSearchableDepotEntryGroupRels(
		long groupId, int start, int end) {

		return depotEntryGroupRelPersistence.findByS_TGI(
			true, groupId, start, end);
	}

	@Override
	public int getSearchableDepotEntryGroupRelsCount(long groupId) {
		return depotEntryGroupRelPersistence.countByS_TGI(true, groupId);
	}

	@Override
	public DepotEntryGroupRel updateDDMStructuresAvailable(
			long depotEntryGroupRelId, boolean ddmStructuresAvailable)
		throws PortalException {

		DepotEntryGroupRel depotEntryGroupRel = getDepotEntryGroupRel(
			depotEntryGroupRelId);

		depotEntryGroupRel.setDdmStructuresAvailable(ddmStructuresAvailable);

		return depotEntryGroupRelPersistence.update(depotEntryGroupRel);
	}

	@Override
	public DepotEntryGroupRel updateSearchable(
			long depotEntryGroupRelId, boolean searchable)
		throws PortalException {

		DepotEntryGroupRel depotEntryGroupRel = getDepotEntryGroupRel(
			depotEntryGroupRelId);

		depotEntryGroupRel.setSearchable(searchable);

		return depotEntryGroupRelPersistence.update(depotEntryGroupRel);
	}

	private void _connectLayoutSetPrototypeGroups(
			long depotEntryId, Group toGroup)
		throws PortalException {

		LayoutSetPrototype layoutSetPrototype =
			_layoutSetPrototypeLocalService.getLayoutSetPrototype(
				toGroup.getClassPK());

		UnicodeProperties settingsUnicodeProperties =
			layoutSetPrototype.getSettingsProperties();

		boolean readyForPropagation = GetterUtil.getBoolean(
			settingsUnicodeProperties.getProperty("readyForPropagation"));

		if (!readyForPropagation) {
			return;
		}

		List<LayoutSet> layoutSets =
			_layoutSetLocalService.getLayoutSetsByLayoutSetPrototypeUuid(
				layoutSetPrototype.getUuid());

		for (LayoutSet layoutSet : layoutSets) {
			addDepotEntryGroupRel(depotEntryId, layoutSet.getGroupId());
		}
	}

	private void _disconnectLayoutSetPrototypeGroups(
			long depotEntryId, Group group)
		throws PortalException {

		LayoutSetPrototype layoutSetPrototype =
			_layoutSetPrototypeLocalService.getLayoutSetPrototype(
				group.getClassPK());

		UnicodeProperties settingsUnicodeProperties =
			layoutSetPrototype.getSettingsProperties();

		boolean readyForPropagation = GetterUtil.getBoolean(
			settingsUnicodeProperties.getProperty("readyForPropagation"));

		if (!readyForPropagation) {
			return;
		}

		List<LayoutSet> layoutSets =
			_layoutSetLocalService.getLayoutSetsByLayoutSetPrototypeUuid(
				layoutSetPrototype.getUuid());

		for (LayoutSet layoutSet : layoutSets) {
			DepotEntryGroupRel depotEntryGroupRel =
				fetchDepotEntryGroupRelByDepotEntryIdToGroupId(
					depotEntryId, layoutSet.getGroupId());

			if (depotEntryGroupRel == null) {
				continue;
			}

			if (depotEntryGroupRel.isDdmStructuresAvailable()) {
				if (_log.isWarnEnabled()) {
					_log.warn(
						"Unable to disconnect " +
							depotEntryGroupRel.getToGroupId());
				}

				continue;
			}

			depotEntryGroupRelLocalService.deleteDepotEntryGroupRel(
				depotEntryGroupRel);
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		DepotEntryGroupRelLocalServiceImpl.class);

	@Reference
	private DepotEntryPersistence _depotEntryPersistence;

	@Reference
	private GroupLocalService _groupLocalService;

	@Reference
	private LayoutSetLocalService _layoutSetLocalService;

	@Reference
	private LayoutSetPrototypeLocalService _layoutSetPrototypeLocalService;

}