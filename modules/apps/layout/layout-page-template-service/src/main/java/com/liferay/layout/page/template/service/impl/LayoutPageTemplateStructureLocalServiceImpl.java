/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.page.template.service.impl;

import com.liferay.exportimport.kernel.lar.ExportImportThreadLocal;
import com.liferay.fragment.service.FragmentEntryLinkLocalService;
import com.liferay.layout.page.template.model.LayoutPageTemplateStructure;
import com.liferay.layout.page.template.model.LayoutPageTemplateStructureRel;
import com.liferay.layout.page.template.service.LayoutPageTemplateStructureRelLocalService;
import com.liferay.layout.page.template.service.base.LayoutPageTemplateStructureLocalServiceBaseImpl;
import com.liferay.layout.page.template.util.CheckUnlockedLayoutThreadLocal;
import com.liferay.layout.util.UpdateLayoutStatusThreadLocal;
import com.liferay.portal.aop.AopService;
import com.liferay.portal.kernel.exception.LockedLayoutException;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.SystemEventConstants;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.systemevent.SystemEvent;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.segments.service.SegmentsExperienceLocalService;

import java.util.Date;
import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Jürgen Kappler
 */
@Component(
	property = "model.class.name=com.liferay.layout.page.template.model.LayoutPageTemplateStructure",
	service = AopService.class
)
public class LayoutPageTemplateStructureLocalServiceImpl
	extends LayoutPageTemplateStructureLocalServiceBaseImpl {

	@Override
	public LayoutPageTemplateStructure addLayoutPageTemplateStructure(
			long userId, long groupId, long plid, long segmentsExperienceId,
			String data, ServiceContext serviceContext)
		throws PortalException {

		// Layout page template structure

		User user = _userLocalService.getUser(userId);

		long layoutPageTemplateStructureId = counterLocalService.increment();

		LayoutPageTemplateStructure layoutPageTemplateStructure =
			layoutPageTemplateStructurePersistence.create(
				layoutPageTemplateStructureId);

		layoutPageTemplateStructure.setUuid(serviceContext.getUuid());
		layoutPageTemplateStructure.setGroupId(groupId);
		layoutPageTemplateStructure.setCompanyId(user.getCompanyId());
		layoutPageTemplateStructure.setUserId(user.getUserId());
		layoutPageTemplateStructure.setUserName(user.getFullName());
		layoutPageTemplateStructure.setCreateDate(
			serviceContext.getCreateDate(new Date()));
		layoutPageTemplateStructure.setModifiedDate(
			serviceContext.getModifiedDate(new Date()));
		layoutPageTemplateStructure.setPlid(plid);

		layoutPageTemplateStructure =
			layoutPageTemplateStructurePersistence.update(
				layoutPageTemplateStructure);

		int count =
			_fragmentEntryLinkLocalService.getFragmentEntryLinksCountByPlid(
				groupId, plid);

		if (count > 0) {
			_updateLayoutStatus(userId, plid);
		}

		// Layout page template structure rel

		if (!ExportImportThreadLocal.isImportInProcess()) {
			_layoutPageTemplateStructureRelLocalService.
				addLayoutPageTemplateStructureRel(
					userId, groupId, layoutPageTemplateStructureId,
					segmentsExperienceId, data, serviceContext);
		}

		return layoutPageTemplateStructure;
	}

	@Override
	@SystemEvent(type = SystemEventConstants.TYPE_DELETE)
	public LayoutPageTemplateStructure deleteLayoutPageTemplateStructure(
		LayoutPageTemplateStructure layoutPageTemplateStructure) {

		// Layout page template structure

		layoutPageTemplateStructurePersistence.remove(
			layoutPageTemplateStructure);

		// Layout page template structure rels

		List<LayoutPageTemplateStructureRel> layoutPageTemplateStructureRels =
			_layoutPageTemplateStructureRelLocalService.
				getLayoutPageTemplateStructureRels(
					layoutPageTemplateStructure.
						getLayoutPageTemplateStructureId());

		for (LayoutPageTemplateStructureRel layoutPageTemplateStructureRel :
				layoutPageTemplateStructureRels) {

			_layoutPageTemplateStructureRelLocalService.
				deleteLayoutPageTemplateStructureRel(
					layoutPageTemplateStructureRel);
		}

		return layoutPageTemplateStructure;
	}

	@Override
	public LayoutPageTemplateStructure deleteLayoutPageTemplateStructure(
			long groupId, long plid)
		throws PortalException {

		LayoutPageTemplateStructure layoutPageTemplateStructure =
			layoutPageTemplateStructurePersistence.findByG_P(groupId, plid);

		layoutPageTemplateStructureLocalService.
			deleteLayoutPageTemplateStructure(layoutPageTemplateStructure);

		return layoutPageTemplateStructure;
	}

	@Override
	public LayoutPageTemplateStructure fetchLayoutPageTemplateStructure(
		long groupId, long plid) {

		return layoutPageTemplateStructurePersistence.fetchByG_P(groupId, plid);
	}

	@Override
	public LayoutPageTemplateStructure updateLayoutPageTemplateStructureData(
			long userId, long groupId, long plid, long segmentsExperienceId,
			String data)
		throws PortalException {

		if (CheckUnlockedLayoutThreadLocal.isCheckUnlockedLayout()) {
			_checkUnlockedLayout(userId, plid);
		}

		// Layout page template structure

		LayoutPageTemplateStructure layoutPageTemplateStructure =
			layoutPageTemplateStructurePersistence.findByG_P(groupId, plid);

		layoutPageTemplateStructure.setModifiedDate(new Date());

		layoutPageTemplateStructure =
			layoutPageTemplateStructurePersistence.update(
				layoutPageTemplateStructure);

		// Layout page template structure rel

		LayoutPageTemplateStructureRel layoutPageTemplateStructureRel =
			_layoutPageTemplateStructureRelLocalService.
				fetchLayoutPageTemplateStructureRel(
					layoutPageTemplateStructure.
						getLayoutPageTemplateStructureId(),
					segmentsExperienceId);

		if (layoutPageTemplateStructureRel == null) {
			_layoutPageTemplateStructureRelLocalService.
				addLayoutPageTemplateStructureRel(
					userId, groupId,
					layoutPageTemplateStructure.
						getLayoutPageTemplateStructureId(),
					segmentsExperienceId, data,
					ServiceContextThreadLocal.getServiceContext());
		}
		else {
			_layoutPageTemplateStructureRelLocalService.
				updateLayoutPageTemplateStructureRel(
					layoutPageTemplateStructure.
						getLayoutPageTemplateStructureId(),
					segmentsExperienceId, data);
		}

		_updateLayoutStatus(userId, plid);

		return layoutPageTemplateStructure;
	}

	@Override
	public LayoutPageTemplateStructure updateLayoutPageTemplateStructureData(
			long userId, long groupId, long plid, String data)
		throws PortalException {

		_checkUnlockedLayout(userId, plid);

		long defaultSegmentsExperienceId =
			_segmentsExperienceLocalService.fetchDefaultSegmentsExperienceId(
				plid);

		return layoutPageTemplateStructureLocalService.
			updateLayoutPageTemplateStructureData(
				userId, groupId, plid, defaultSegmentsExperienceId, data);
	}

	private void _checkUnlockedLayout(long userId, long plid)
		throws PortalException {

		Layout layout = _layoutLocalService.fetchLayout(plid);

		if ((layout != null) && !layout.isUnlocked(Constants.EDIT, userId)) {
			throw new LockedLayoutException();
		}
	}

	private void _updateLayoutStatus(long userId, long plid)
		throws PortalException {

		if (UpdateLayoutStatusThreadLocal.isUpdateLayoutStatus()) {
			_layoutLocalService.updateStatus(
				userId, plid, WorkflowConstants.STATUS_DRAFT,
				ServiceContextThreadLocal.getServiceContext());
		}
	}

	@Reference
	private FragmentEntryLinkLocalService _fragmentEntryLinkLocalService;

	@Reference
	private LayoutLocalService _layoutLocalService;

	@Reference
	private LayoutPageTemplateStructureRelLocalService
		_layoutPageTemplateStructureRelLocalService;

	@Reference
	private SegmentsExperienceLocalService _segmentsExperienceLocalService;

	@Reference
	private UserLocalService _userLocalService;

}