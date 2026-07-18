/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.segments.service.impl;

import com.liferay.portal.aop.AopService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.security.permission.resource.PortletResourcePermission;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.segments.constants.SegmentsActionKeys;
import com.liferay.segments.constants.SegmentsConstants;
import com.liferay.segments.model.SegmentsEntry;
import com.liferay.segments.model.SegmentsEntryRel;
import com.liferay.segments.service.base.SegmentsEntryRelServiceBaseImpl;

import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Eduardo García
 */
@Component(
	property = {
		"json.web.service.context.name=segments",
		"json.web.service.context.path=SegmentsEntryRel"
	},
	service = AopService.class
)
public class SegmentsEntryRelServiceImpl
	extends SegmentsEntryRelServiceBaseImpl {

	@Override
	public List<SegmentsEntryRel> getSegmentsEntryRels(long segmentsEntryId)
		throws PortalException {

		_segmentsEntryResourcePermission.check(
			getPermissionChecker(), segmentsEntryId, ActionKeys.VIEW);

		return segmentsEntryRelPersistence.findBySegmentsEntryId(
			segmentsEntryId);
	}

	@Override
	public List<SegmentsEntryRel> getSegmentsEntryRels(
			long segmentsEntryId, int start, int end,
			OrderByComparator<SegmentsEntryRel> orderByComparator)
		throws PortalException {

		_segmentsEntryResourcePermission.check(
			getPermissionChecker(), segmentsEntryId, ActionKeys.VIEW);

		return segmentsEntryRelPersistence.findBySegmentsEntryId(
			segmentsEntryId, start, end, orderByComparator);
	}

	@Override
	public List<SegmentsEntryRel> getSegmentsEntryRels(
			long groupId, long classNameId, long classPK)
		throws PortalException {

		_portletResourcePermission.check(
			getPermissionChecker(), groupId,
			SegmentsActionKeys.MANAGE_SEGMENTS_ENTRIES);

		return segmentsEntryRelPersistence.findByG_CN_CPK(
			groupId, classNameId, classPK);
	}

	@Override
	public int getSegmentsEntryRelsCount(long segmentsEntryId)
		throws PortalException {

		_segmentsEntryResourcePermission.check(
			getPermissionChecker(), segmentsEntryId, ActionKeys.VIEW);

		return segmentsEntryRelPersistence.countBySegmentsEntryId(
			segmentsEntryId);
	}

	@Override
	public int getSegmentsEntryRelsCount(
			long groupId, long classNameId, long classPK)
		throws PortalException {

		_portletResourcePermission.check(
			getPermissionChecker(), groupId,
			SegmentsActionKeys.MANAGE_SEGMENTS_ENTRIES);

		return segmentsEntryRelPersistence.countByG_CN_CPK(
			groupId, classNameId, classPK);
	}

	@Override
	public boolean hasSegmentsEntryRel(
		long segmentsEntryId, long classNameId, long classPK) {

		return segmentsEntryRelLocalService.hasSegmentsEntryRel(
			segmentsEntryId, classNameId, classPK);
	}

	@Reference(
		target = "(resource.name=" + SegmentsConstants.RESOURCE_NAME + ")"
	)
	private PortletResourcePermission _portletResourcePermission;

	@Reference(
		target = "(model.class.name=com.liferay.segments.model.SegmentsEntry)"
	)
	private ModelResourcePermission<SegmentsEntry>
		_segmentsEntryResourcePermission;

}