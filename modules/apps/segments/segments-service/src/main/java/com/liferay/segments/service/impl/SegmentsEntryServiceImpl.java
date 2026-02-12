/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.segments.service.impl;

import com.liferay.portal.aop.AopService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.search.BaseModelSearchResult;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.security.permission.resource.PortletResourcePermission;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.segments.constants.SegmentsActionKeys;
import com.liferay.segments.constants.SegmentsConstants;
import com.liferay.segments.model.SegmentsEntry;
import com.liferay.segments.service.base.SegmentsEntryServiceBaseImpl;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Eduardo García
 */
@Component(
	property = {
		"json.web.service.context.name=segments",
		"json.web.service.context.path=SegmentsEntry"
	},
	service = AopService.class
)
public class SegmentsEntryServiceImpl extends SegmentsEntryServiceBaseImpl {

	@Override
	public SegmentsEntry addSegmentsEntry(
			String segmentsEntryKey, Map<Locale, String> nameMap,
			Map<Locale, String> descriptionMap, boolean active, String criteria,
			ServiceContext serviceContext)
		throws PortalException {

		_portletResourcePermission.check(
			getPermissionChecker(), serviceContext.getScopeGroupId(),
			SegmentsActionKeys.MANAGE_SEGMENTS_ENTRIES);

		return segmentsEntryLocalService.addSegmentsEntry(
			segmentsEntryKey, nameMap, descriptionMap, active, criteria,
			serviceContext);
	}

	@Override
	public SegmentsEntry addSegmentsEntry(
			String segmentsEntryKey, Map<Locale, String> nameMap,
			Map<Locale, String> descriptionMap, boolean active, String criteria,
			String source, ServiceContext serviceContext)
		throws PortalException {

		_portletResourcePermission.check(
			getPermissionChecker(), serviceContext.getScopeGroupId(),
			SegmentsActionKeys.MANAGE_SEGMENTS_ENTRIES);

		return segmentsEntryLocalService.addSegmentsEntry(
			segmentsEntryKey, nameMap, descriptionMap, active, criteria, source,
			serviceContext);
	}

	@Override
	public void addSegmentsEntryClassPKs(
			long segmentsEntryId, long[] classPKs,
			ServiceContext serviceContext)
		throws PortalException {

		_segmentsEntryResourcePermission.check(
			getPermissionChecker(), segmentsEntryId, ActionKeys.UPDATE);

		segmentsEntryLocalService.addSegmentsEntryClassPKs(
			segmentsEntryId, classPKs, serviceContext);
	}

	@Override
	public SegmentsEntry deleteSegmentsEntry(long segmentsEntryId)
		throws PortalException {

		_segmentsEntryResourcePermission.check(
			getPermissionChecker(), segmentsEntryId, ActionKeys.DELETE);

		return segmentsEntryLocalService.deleteSegmentsEntry(segmentsEntryId);
	}

	@Override
	public void deleteSegmentsEntryClassPKs(
			long segmentsEntryId, long[] classPKs)
		throws PortalException {

		_segmentsEntryResourcePermission.check(
			getPermissionChecker(), segmentsEntryId, ActionKeys.UPDATE);

		segmentsEntryLocalService.deleteSegmentsEntryClassPKs(
			segmentsEntryId, classPKs);
	}

	@Override
	public SegmentsEntry fetchSegmentsEntryByExternalReferenceCode(
			String segmentsEntryERC, long groupId)
		throws PortalException {

		SegmentsEntry segmentsEntry =
			segmentsEntryLocalService.fetchSegmentsEntryByExternalReferenceCode(
				segmentsEntryERC, groupId);

		_segmentsEntryResourcePermission.check(
			getPermissionChecker(), segmentsEntry.getSegmentsEntryId(),
			ActionKeys.VIEW);

		return segmentsEntry;
	}

	@Override
	public List<SegmentsEntry> getSegmentsEntries(long groupId) {
		return segmentsEntryPersistence.filterFindByGroupId(
			_portal.getCurrentAndAncestorSiteGroupIds(groupId));
	}

	@Override
	public List<SegmentsEntry> getSegmentsEntries(
		long groupId, int start, int end,
		OrderByComparator<SegmentsEntry> orderByComparator) {

		return segmentsEntryPersistence.filterFindByGroupId(
			_portal.getCurrentAndAncestorSiteGroupIds(groupId), start, end,
			orderByComparator);
	}

	@Override
	public List<SegmentsEntry> getSegmentsEntries(
		long groupId, String source, int start, int end,
		OrderByComparator<SegmentsEntry> orderByComparator) {

		return segmentsEntryLocalService.getSegmentsEntries(
			groupId, source, start, end, orderByComparator);
	}

	@Override
	public int getSegmentsEntriesCount(long groupId) {
		return segmentsEntryPersistence.filterCountByGroupId(
			_portal.getCurrentAndAncestorSiteGroupIds(groupId));
	}

	@Override
	public int getSegmentsEntriesCount(long groupId, String source) {
		return segmentsEntryPersistence.filterCountByG_SRC(
			_portal.getCurrentAndAncestorSiteGroupIds(groupId), source);
	}

	@Override
	public SegmentsEntry getSegmentsEntry(long segmentsEntryId)
		throws PortalException {

		SegmentsEntry segmentsEntry =
			segmentsEntryLocalService.getSegmentsEntry(segmentsEntryId);

		_segmentsEntryResourcePermission.check(
			getPermissionChecker(), segmentsEntryId, ActionKeys.VIEW);

		return segmentsEntry;
	}

	@Override
	public SegmentsEntry getSegmentsEntryByExternalReferenceCode(
			String segmentsEntryERC, long groupId)
		throws PortalException {

		SegmentsEntry segmentsEntry =
			segmentsEntryLocalService.getSegmentsEntryByExternalReferenceCode(
				segmentsEntryERC, groupId);

		_segmentsEntryResourcePermission.check(
			getPermissionChecker(), segmentsEntry.getSegmentsEntryId(),
			ActionKeys.VIEW);

		return segmentsEntry;
	}

	@Override
	public BaseModelSearchResult<SegmentsEntry> searchSegmentsEntries(
			long companyId, long groupId, String keywords, int start, int end,
			Sort sort)
		throws PortalException {

		_portletResourcePermission.check(
			getPermissionChecker(), groupId, ActionKeys.VIEW);

		return segmentsEntryLocalService.searchSegmentsEntries(
			companyId, groupId, keywords, new LinkedHashMap<>(), start, end,
			sort);
	}

	@Override
	public SegmentsEntry updateSegmentsEntry(
			long segmentsEntryId, String segmentsEntryKey,
			Map<Locale, String> nameMap, Map<Locale, String> descriptionMap,
			boolean active, String criteria, ServiceContext serviceContext)
		throws PortalException {

		_segmentsEntryResourcePermission.check(
			getPermissionChecker(), segmentsEntryId, ActionKeys.UPDATE);

		return segmentsEntryLocalService.updateSegmentsEntry(
			segmentsEntryId, segmentsEntryKey, nameMap, descriptionMap, active,
			criteria, serviceContext);
	}

	@Reference
	private Portal _portal;

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