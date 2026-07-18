/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.utility.page.service.impl;

import com.liferay.layout.utility.page.constants.LayoutUtilityPageActionKeys;
import com.liferay.layout.utility.page.model.LayoutUtilityPageEntry;
import com.liferay.layout.utility.page.service.base.LayoutUtilityPageEntryServiceBaseImpl;
import com.liferay.portal.aop.AopService;
import com.liferay.portal.dao.orm.custom.sql.CustomSQL;
import com.liferay.portal.kernel.dao.orm.WildcardMode;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.permission.GroupPermissionUtil;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Brian Wing Shun Chan
 */
@Component(
	property = {
		"json.web.service.context.name=layoututilitypage",
		"json.web.service.context.path=LayoutUtilityPageEntry"
	},
	service = AopService.class
)
public class LayoutUtilityPageEntryServiceImpl
	extends LayoutUtilityPageEntryServiceBaseImpl {

	@Override
	public LayoutUtilityPageEntry addLayoutUtilityPageEntry(
			String externalReferenceCode, long groupId, long plid,
			long previewFileEntryId, boolean defaultLayoutUtilityPageEntry,
			String name, String type, String masterLayoutPageTemplateEntryERC,
			ServiceContext serviceContext)
		throws PortalException {

		GroupPermissionUtil.check(
			getPermissionChecker(), groupId,
			LayoutUtilityPageActionKeys.ADD_LAYOUT_UTILITY_PAGE_ENTRY);

		return layoutUtilityPageEntryLocalService.addLayoutUtilityPageEntry(
			externalReferenceCode, getUserId(), groupId, plid,
			previewFileEntryId, defaultLayoutUtilityPageEntry, name, type,
			masterLayoutPageTemplateEntryERC, serviceContext);
	}

	@Override
	public LayoutUtilityPageEntry copyLayoutUtilityPageEntry(
			long groupId, long sourceLayoutUtilityPageEntryId,
			ServiceContext serviceContext)
		throws Exception {

		GroupPermissionUtil.check(
			getPermissionChecker(), groupId,
			LayoutUtilityPageActionKeys.ADD_LAYOUT_UTILITY_PAGE_ENTRY);

		_layoutUtilityPageEntryModelResourcePermission.check(
			getPermissionChecker(), sourceLayoutUtilityPageEntryId,
			ActionKeys.VIEW);

		return layoutUtilityPageEntryLocalService.copyLayoutUtilityPageEntry(
			getUserId(), groupId, sourceLayoutUtilityPageEntryId,
			serviceContext);
	}

	@Override
	public LayoutUtilityPageEntry deleteLayoutUtilityPageEntry(
			long layoutUtilityPageEntryId)
		throws PortalException {

		LayoutUtilityPageEntry layoutUtilityPageEntry =
			layoutUtilityPageEntryPersistence.findByPrimaryKey(
				layoutUtilityPageEntryId);

		_layoutUtilityPageEntryModelResourcePermission.check(
			getPermissionChecker(), layoutUtilityPageEntryId,
			ActionKeys.DELETE);

		if (layoutUtilityPageEntry.isDefaultLayoutUtilityPageEntry()) {
			GroupPermissionUtil.check(
				getPermissionChecker(), layoutUtilityPageEntry.getGroupId(),
				LayoutUtilityPageActionKeys.
					ASSIGN_DEFAULT_LAYOUT_UTILITY_PAGE_ENTRY);
		}

		return layoutUtilityPageEntryLocalService.deleteLayoutUtilityPageEntry(
			layoutUtilityPageEntryId);
	}

	@Override
	public LayoutUtilityPageEntry deleteLayoutUtilityPageEntry(
			String externalReferenceCode, long groupId)
		throws PortalException {

		LayoutUtilityPageEntry layoutUtilityPageEntry =
			layoutUtilityPageEntryLocalService.
				getLayoutUtilityPageEntryByExternalReferenceCode(
					externalReferenceCode, groupId);

		_layoutUtilityPageEntryModelResourcePermission.check(
			getPermissionChecker(), layoutUtilityPageEntry, ActionKeys.DELETE);

		return layoutUtilityPageEntryLocalService.deleteLayoutUtilityPageEntry(
			layoutUtilityPageEntry);
	}

	@Override
	public LayoutUtilityPageEntry fetchLayoutUtilityPageEntry(
		long layoutUtilityPageEntryId) {

		return layoutUtilityPageEntryPersistence.fetchByPrimaryKey(
			layoutUtilityPageEntryId);
	}

	@Override
	public LayoutUtilityPageEntry
			fetchLayoutUtilityPageEntryByExternalReferenceCode(
				String externalReferenceCode, long groupId)
		throws PortalException {

		return layoutUtilityPageEntryLocalService.
			fetchLayoutUtilityPageEntryByExternalReferenceCode(
				externalReferenceCode, groupId);
	}

	@Override
	public LayoutUtilityPageEntry getDefaultLayoutUtilityPageEntry(
			long groupId, String type)
		throws PortalException {

		return layoutUtilityPageEntryLocalService.
			getDefaultLayoutUtilityPageEntry(groupId, type);
	}

	@Override
	public List<LayoutUtilityPageEntry> getLayoutUtilityPageEntries(
		long groupId) {

		return layoutUtilityPageEntryPersistence.filterFindByGroupId(groupId);
	}

	@Override
	public List<LayoutUtilityPageEntry> getLayoutUtilityPageEntries(
		long groupId, int start, int end,
		OrderByComparator<LayoutUtilityPageEntry> orderByComparator) {

		return layoutUtilityPageEntryPersistence.filterFindByGroupId(
			groupId, start, end, orderByComparator);
	}

	@Override
	public List<LayoutUtilityPageEntry> getLayoutUtilityPageEntries(
		long groupId, String type, int start, int end,
		OrderByComparator<LayoutUtilityPageEntry> orderByComparator) {

		return layoutUtilityPageEntryPersistence.filterFindByG_T(
			groupId, type, start, end, orderByComparator);
	}

	@Override
	public List<LayoutUtilityPageEntry> getLayoutUtilityPageEntries(
		long groupId, String keyword, String[] types, int start, int end,
		OrderByComparator<LayoutUtilityPageEntry> orderByComparator) {

		return layoutUtilityPageEntryPersistence.filterFindByG_LikeN_T(
			groupId,
			_customSQL.keywords(keyword, false, WildcardMode.SURROUND)[0],
			types, start, end, orderByComparator);
	}

	@Override
	public List<LayoutUtilityPageEntry> getLayoutUtilityPageEntries(
		long groupId, String[] types, int start, int end,
		OrderByComparator<LayoutUtilityPageEntry> orderByComparator) {

		return layoutUtilityPageEntryPersistence.filterFindByG_T(
			groupId, types, start, end, orderByComparator);
	}

	@Override
	public int getLayoutUtilityPageEntriesCount(long groupId) {
		return layoutUtilityPageEntryPersistence.filterCountByGroupId(groupId);
	}

	@Override
	public int getLayoutUtilityPageEntriesCount(
		long groupId, String keyword, String[] types) {

		return layoutUtilityPageEntryPersistence.filterCountByG_LikeN_T(
			groupId,
			_customSQL.keywords(keyword, false, WildcardMode.SURROUND)[0],
			types);
	}

	@Override
	public int getLayoutUtilityPageEntriesCount(long groupId, String[] types) {
		return layoutUtilityPageEntryPersistence.filterCountByG_T(
			groupId, types);
	}

	@Override
	public LayoutUtilityPageEntry
			getLayoutUtilityPageEntryByExternalReferenceCode(
				String externalReferenceCode, long groupId)
		throws PortalException {

		return layoutUtilityPageEntryLocalService.
			getLayoutUtilityPageEntryByExternalReferenceCode(
				externalReferenceCode, groupId);
	}

	@Override
	public LayoutUtilityPageEntry setDefaultLayoutUtilityPageEntry(
			long layoutUtilityPageEntryId)
		throws PortalException {

		LayoutUtilityPageEntry layoutUtilityPageEntry =
			layoutUtilityPageEntryPersistence.findByPrimaryKey(
				layoutUtilityPageEntryId);

		GroupPermissionUtil.check(
			getPermissionChecker(), layoutUtilityPageEntry.getGroupId(),
			LayoutUtilityPageActionKeys.
				ASSIGN_DEFAULT_LAYOUT_UTILITY_PAGE_ENTRY);

		_layoutUtilityPageEntryModelResourcePermission.check(
			getPermissionChecker(), layoutUtilityPageEntryId,
			ActionKeys.UPDATE);

		return layoutUtilityPageEntryLocalService.
			setDefaultLayoutUtilityPageEntry(layoutUtilityPageEntryId);
	}

	@Override
	public LayoutUtilityPageEntry unsetDefaultLayoutUtilityPageEntry(
			long layoutUtilityPageEntryId)
		throws PortalException {

		LayoutUtilityPageEntry layoutUtilityPageEntry =
			layoutUtilityPageEntryPersistence.findByPrimaryKey(
				layoutUtilityPageEntryId);

		GroupPermissionUtil.check(
			getPermissionChecker(), layoutUtilityPageEntry.getGroupId(),
			LayoutUtilityPageActionKeys.
				ASSIGN_DEFAULT_LAYOUT_UTILITY_PAGE_ENTRY);

		_layoutUtilityPageEntryModelResourcePermission.check(
			getPermissionChecker(), layoutUtilityPageEntryId,
			ActionKeys.UPDATE);

		if (!layoutUtilityPageEntry.isDefaultLayoutUtilityPageEntry()) {
			return layoutUtilityPageEntry;
		}

		layoutUtilityPageEntry.setDefaultLayoutUtilityPageEntry(false);

		return layoutUtilityPageEntryLocalService.updateLayoutUtilityPageEntry(
			layoutUtilityPageEntry);
	}

	@Override
	public LayoutUtilityPageEntry updateLayoutUtilityPageEntry(
			long layoutUtilityPageEntryId, long previewFileEntryId,
			ServiceContext serviceContext)
		throws PortalException {

		_layoutUtilityPageEntryModelResourcePermission.check(
			getPermissionChecker(), layoutUtilityPageEntryId,
			ActionKeys.UPDATE);

		return layoutUtilityPageEntryLocalService.updateLayoutUtilityPageEntry(
			layoutUtilityPageEntryId, previewFileEntryId, serviceContext);
	}

	@Override
	public LayoutUtilityPageEntry updateLayoutUtilityPageEntry(
			long layoutUtilityPageEntryId, String name,
			ServiceContext serviceContext)
		throws PortalException {

		_layoutUtilityPageEntryModelResourcePermission.check(
			getPermissionChecker(), layoutUtilityPageEntryId,
			ActionKeys.UPDATE);

		return layoutUtilityPageEntryLocalService.updateLayoutUtilityPageEntry(
			layoutUtilityPageEntryId, name, serviceContext);
	}

	@Reference
	private CustomSQL _customSQL;

	@Reference(
		target = "(model.class.name=com.liferay.layout.utility.page.model.LayoutUtilityPageEntry)"
	)
	private ModelResourcePermission<LayoutUtilityPageEntry>
		_layoutUtilityPageEntryModelResourcePermission;

}