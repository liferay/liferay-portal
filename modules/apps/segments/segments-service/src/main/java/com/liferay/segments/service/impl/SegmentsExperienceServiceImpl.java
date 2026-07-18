/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.segments.service.impl;

import com.liferay.portal.aop.AopService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.security.permission.resource.PortletResourcePermission;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.permission.LayoutPermission;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.UnicodeProperties;
import com.liferay.segments.constants.SegmentsActionKeys;
import com.liferay.segments.constants.SegmentsConstants;
import com.liferay.segments.model.SegmentsExperience;
import com.liferay.segments.service.base.SegmentsExperienceServiceBaseImpl;

import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author David Arques
 */
@Component(
	property = {
		"json.web.service.context.name=segments",
		"json.web.service.context.path=SegmentsExperience"
	},
	service = AopService.class
)
public class SegmentsExperienceServiceImpl
	extends SegmentsExperienceServiceBaseImpl {

	@Override
	public SegmentsExperience addSegmentsExperience(
			String externalReferenceCode, long groupId, String segmentsEntryERC,
			String segmentsEntryScopeERC, long plid,
			Map<Locale, String> nameMap, boolean active,
			UnicodeProperties typeSettingsUnicodeProperties,
			ServiceContext serviceContext)
		throws PortalException {

		if (!_hasUpdateLayoutPermission(plid)) {
			_portletResourcePermission.check(
				getPermissionChecker(), serviceContext.getScopeGroupId(),
				SegmentsActionKeys.MANAGE_SEGMENTS_ENTRIES);
		}

		return segmentsExperienceLocalService.addSegmentsExperience(
			externalReferenceCode, getUserId(), groupId, segmentsEntryERC,
			segmentsEntryScopeERC, plid, nameMap, active,
			typeSettingsUnicodeProperties, serviceContext);
	}

	@Override
	public SegmentsExperience addSegmentsExperience(
			String externalReferenceCode, long groupId, String segmentsEntryERC,
			String segmentsEntryScopeERC, String segmentsExperienceKey,
			long plid, Map<Locale, String> nameMap, int priority,
			boolean active, UnicodeProperties typeSettingsUnicodeProperties,
			ServiceContext serviceContext)
		throws PortalException {

		if (!_hasUpdateLayoutPermission(plid)) {
			_portletResourcePermission.check(
				getPermissionChecker(), serviceContext.getScopeGroupId(),
				SegmentsActionKeys.MANAGE_SEGMENTS_ENTRIES);
		}

		return segmentsExperienceLocalService.addSegmentsExperience(
			externalReferenceCode, getUserId(), groupId, segmentsEntryERC,
			segmentsEntryScopeERC, segmentsExperienceKey, plid, nameMap,
			priority, active, typeSettingsUnicodeProperties, serviceContext);
	}

	@Override
	public SegmentsExperience appendSegmentsExperience(
			long groupId, String segmentsEntryERC, String segmentsEntryScopeERC,
			long plid, Map<Locale, String> nameMap, boolean active,
			ServiceContext serviceContext)
		throws PortalException {

		return appendSegmentsExperience(
			groupId, segmentsEntryERC, segmentsEntryScopeERC, plid, nameMap,
			active, new UnicodeProperties(true), serviceContext);
	}

	@Override
	public SegmentsExperience appendSegmentsExperience(
			long groupId, String segmentsEntryERC, String segmentsEntryScopeERC,
			long plid, Map<Locale, String> nameMap, boolean active,
			UnicodeProperties typeSettingsUnicodeProperties,
			ServiceContext serviceContext)
		throws PortalException {

		if (!_hasUpdateLayoutPermission(plid)) {
			_portletResourcePermission.check(
				getPermissionChecker(), serviceContext.getScopeGroupId(),
				SegmentsActionKeys.MANAGE_SEGMENTS_ENTRIES);
		}

		return segmentsExperienceLocalService.appendSegmentsExperience(
			getUserId(), groupId, segmentsEntryERC, segmentsEntryScopeERC, plid,
			nameMap, active, typeSettingsUnicodeProperties, serviceContext);
	}

	@Override
	public SegmentsExperience deleteSegmentsExperience(
			long segmentsExperienceId)
		throws PortalException {

		_segmentsExperienceResourcePermission.check(
			getPermissionChecker(),
			segmentsExperienceLocalService.getSegmentsExperience(
				segmentsExperienceId),
			ActionKeys.DELETE);

		return segmentsExperienceLocalService.deleteSegmentsExperience(
			segmentsExperienceId);
	}

	@Override
	public SegmentsExperience deleteSegmentsExperience(
			String externalReferenceCode, long groupId)
		throws PortalException {

		SegmentsExperience segmentsExperience =
			segmentsExperienceLocalService.
				getSegmentsExperienceByExternalReferenceCode(
					externalReferenceCode, groupId);

		_segmentsExperienceResourcePermission.check(
			getPermissionChecker(), segmentsExperience, ActionKeys.DELETE);

		return segmentsExperienceLocalService.deleteSegmentsExperience(
			segmentsExperience);
	}

	@Override
	public SegmentsExperience fetchSegmentsExperience(
			long groupId, String segmentsExperienceKey, long plid)
		throws PortalException {

		SegmentsExperience segmentsExperience =
			segmentsExperienceLocalService.fetchSegmentsExperience(
				groupId, segmentsExperienceKey, plid);

		if (segmentsExperience != null) {
			_segmentsExperienceResourcePermission.check(
				getPermissionChecker(), segmentsExperience, ActionKeys.VIEW);
		}

		return segmentsExperience;
	}

	@Override
	public SegmentsExperience fetchSegmentsExperienceByExternalReferenceCode(
			String externalReferenceCode, long groupId)
		throws PortalException {

		SegmentsExperience segmentsExperience =
			segmentsExperienceLocalService.
				fetchSegmentsExperienceByExternalReferenceCode(
					externalReferenceCode, groupId);

		if (segmentsExperience != null) {
			_segmentsExperienceResourcePermission.check(
				getPermissionChecker(), segmentsExperience, ActionKeys.VIEW);
		}

		return segmentsExperience;
	}

	@Override
	public SegmentsExperience getSegmentsExperience(long segmentsExperienceId)
		throws PortalException {

		SegmentsExperience segmentsExperience =
			segmentsExperienceLocalService.getSegmentsExperience(
				segmentsExperienceId);

		_segmentsExperienceResourcePermission.check(
			getPermissionChecker(), segmentsExperience, ActionKeys.VIEW);

		return segmentsExperience;
	}

	@Override
	public SegmentsExperience getSegmentsExperience(
			long groupId, String segmentsExperienceKey, long plid)
		throws PortalException {

		SegmentsExperience segmentsExperience =
			segmentsExperiencePersistence.findByG_SEK_P(
				groupId, segmentsExperienceKey, plid);

		_segmentsExperienceResourcePermission.check(
			getPermissionChecker(), segmentsExperience, ActionKeys.VIEW);

		return segmentsExperience;
	}

	@Override
	public SegmentsExperience getSegmentsExperienceByExternalReferenceCode(
			String externalReferenceCode, long groupId)
		throws PortalException {

		SegmentsExperience segmentsExperience =
			segmentsExperienceLocalService.
				getSegmentsExperienceByExternalReferenceCode(
					externalReferenceCode, groupId);

		_segmentsExperienceResourcePermission.check(
			getPermissionChecker(), segmentsExperience, ActionKeys.VIEW);

		return segmentsExperience;
	}

	@Override
	public List<SegmentsExperience> getSegmentsExperiences(
			long groupId, long plid, boolean active)
		throws PortalException {

		if (_hasUpdateLayoutPermission(plid)) {
			return segmentsExperiencePersistence.findByG_P_A(
				groupId, plid, active);
		}

		return segmentsExperiencePersistence.filterFindByG_P_A(
			groupId, plid, active);
	}

	@Override
	public List<SegmentsExperience> getSegmentsExperiences(
			long groupId, long plid, boolean active, int start, int end,
			OrderByComparator<SegmentsExperience> orderByComparator)
		throws PortalException {

		if (_hasUpdateLayoutPermission(plid)) {
			return segmentsExperiencePersistence.findByG_P_A(
				groupId, plid, active, start, end, orderByComparator);
		}

		return segmentsExperiencePersistence.filterFindByG_P_A(
			groupId, plid, active, start, end, orderByComparator);
	}

	@Override
	public int getSegmentsExperiencesCount(
			long groupId, long plid, boolean active)
		throws PortalException {

		if (_hasUpdateLayoutPermission(plid)) {
			return segmentsExperiencePersistence.countByG_P_A(
				groupId, plid, active);
		}

		return segmentsExperiencePersistence.filterCountByG_P_A(
			groupId, plid, active);
	}

	@Override
	public SegmentsExperience updateSegmentsExperience(
			long segmentsExperienceId, String segmentsEntryERC,
			String segmentsEntryScopeERC, Map<Locale, String> nameMap,
			boolean active)
		throws PortalException {

		SegmentsExperience segmentsExperience = getSegmentsExperience(
			segmentsExperienceId);

		return updateSegmentsExperience(
			segmentsExperienceId, segmentsEntryERC, segmentsEntryScopeERC,
			nameMap, active,
			segmentsExperience.getTypeSettingsUnicodeProperties());
	}

	@Override
	public SegmentsExperience updateSegmentsExperience(
			long segmentsExperienceId, String segmentsEntryERC,
			String segmentsEntryScopeERC, Map<Locale, String> nameMap,
			boolean active, UnicodeProperties typeSettingsUnicodeProperties)
		throws PortalException {

		_segmentsExperienceResourcePermission.check(
			getPermissionChecker(),
			segmentsExperienceLocalService.getSegmentsExperience(
				segmentsExperienceId),
			ActionKeys.UPDATE);

		return segmentsExperienceLocalService.updateSegmentsExperience(
			getUserId(), segmentsExperienceId, segmentsEntryERC,
			segmentsEntryScopeERC, nameMap, active,
			typeSettingsUnicodeProperties);
	}

	@Override
	public SegmentsExperience updateSegmentsExperiencePriority(
			long segmentsExperienceId, int newPriority)
		throws PortalException {

		SegmentsExperience segmentsExperience =
			segmentsExperiencePersistence.findByPrimaryKey(
				segmentsExperienceId);

		_segmentsExperienceResourcePermission.check(
			getPermissionChecker(), segmentsExperience, ActionKeys.UPDATE);

		SegmentsExperience swapSegmentsExperience =
			segmentsExperiencePersistence.fetchByG_P_P(
				segmentsExperience.getGroupId(), segmentsExperience.getPlid(),
				newPriority);

		if (swapSegmentsExperience != null) {
			_segmentsExperienceResourcePermission.check(
				getPermissionChecker(), swapSegmentsExperience,
				ActionKeys.UPDATE);
		}

		return segmentsExperienceLocalService.updateSegmentsExperiencePriority(
			getUserId(), segmentsExperienceId, newPriority);
	}

	private boolean _hasUpdateLayoutPermission(long plid)
		throws PortalException {

		Layout layout = _layoutLocalService.fetchLayout(plid);

		if (layout == null) {
			return false;
		}

		return _layoutPermission.containsLayoutRestrictedUpdatePermission(
			getPermissionChecker(), layout);
	}

	@Reference
	private LayoutLocalService _layoutLocalService;

	@Reference
	private LayoutPermission _layoutPermission;

	@Reference(
		target = "(resource.name=" + SegmentsConstants.RESOURCE_NAME + ")"
	)
	private PortletResourcePermission _portletResourcePermission;

	@Reference(
		target = "(model.class.name=com.liferay.segments.model.SegmentsExperience)"
	)
	private ModelResourcePermission<SegmentsExperience>
		_segmentsExperienceResourcePermission;

}