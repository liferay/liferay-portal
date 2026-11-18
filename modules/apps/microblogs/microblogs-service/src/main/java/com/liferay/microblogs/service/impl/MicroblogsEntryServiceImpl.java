/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.microblogs.service.impl;

import com.liferay.microblogs.constants.MicroblogsConstants;
import com.liferay.microblogs.model.MicroblogsEntry;
import com.liferay.microblogs.service.base.MicroblogsEntryServiceBaseImpl;
import com.liferay.portal.aop.AopService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.security.permission.resource.PortletResourcePermission;
import com.liferay.portal.kernel.service.ServiceContext;

import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Jonathan Lee
 */
@Component(
	property = {
		"json.web.service.context.name=microblogs",
		"json.web.service.context.path=MicroblogsEntry"
	},
	service = AopService.class
)
public class MicroblogsEntryServiceImpl extends MicroblogsEntryServiceBaseImpl {

	@Override
	public MicroblogsEntry addMicroblogsEntry(
			long userId, String content, int type, long parentMicroblogsEntryId,
			int socialRelationType, ServiceContext serviceContext)
		throws PortalException {

		_portletResourcePermission.check(
			getPermissionChecker(), serviceContext.getScopeGroupId(),
			ActionKeys.ADD_ENTRY);

		if (userId != getUserId()) {
			throw new PrincipalException();
		}

		return microblogsEntryLocalService.addMicroblogsEntry(
			userId, content, type, parentMicroblogsEntryId, socialRelationType,
			serviceContext);
	}

	@Override
	public MicroblogsEntry deleteMicroblogsEntry(long microblogsEntryId)
		throws PortalException {

		_microblogsEntryModelResourcePermission.check(
			getPermissionChecker(), microblogsEntryId, ActionKeys.DELETE);

		return microblogsEntryLocalService.deleteMicroblogsEntry(
			microblogsEntryId);
	}

	@Override
	public List<MicroblogsEntry> getMicroblogsEntries(int start, int end)
		throws PortalException {

		User user = getGuestOrUser();

		return microblogsEntryFinder.findByC_U(
			user.getCompanyId(), user.getUserId(), start, end);
	}

	@Override
	public List<MicroblogsEntry> getMicroblogsEntries(
			String assetTagName, int start, int end)
		throws PortalException {

		User user = getGuestOrUser();

		return microblogsEntryFinder.findByC_U_ATN(
			user.getCompanyId(), user.getUserId(), assetTagName, start, end);
	}

	@Override
	public int getMicroblogsEntriesCount() throws PortalException {
		User user = getGuestOrUser();

		return microblogsEntryFinder.countByC_U(
			user.getCompanyId(), user.getUserId());
	}

	@Override
	public int getMicroblogsEntriesCount(String assetTagName)
		throws PortalException {

		User user = getGuestOrUser();

		return microblogsEntryFinder.countByC_U_ATN(
			user.getCompanyId(), user.getUserId(), assetTagName);
	}

	@Override
	public MicroblogsEntry getMicroblogsEntry(long microblogsEntryId)
		throws PortalException {

		_microblogsEntryModelResourcePermission.check(
			getPermissionChecker(), microblogsEntryId, ActionKeys.VIEW);

		return microblogsEntryLocalService.getMicroblogsEntry(
			microblogsEntryId);
	}

	@Override
	public List<MicroblogsEntry> getUserMicroblogsEntries(
			long microblogsEntryUserId, int start, int end)
		throws PortalException {

		return microblogsEntryFinder.findByU_MU(
			getGuestOrUserId(), microblogsEntryUserId, start, end);
	}

	@Override
	public List<MicroblogsEntry> getUserMicroblogsEntries(
			long microblogsEntryUserId, int type, int start, int end)
		throws PortalException {

		return microblogsEntryFinder.findByU_T_MU(
			getGuestOrUserId(), type, microblogsEntryUserId, start, end);
	}

	@Override
	public int getUserMicroblogsEntriesCount(long microblogsEntryUserId)
		throws PortalException {

		return microblogsEntryFinder.countByU_MU(
			getGuestOrUserId(), microblogsEntryUserId);
	}

	@Override
	public int getUserMicroblogsEntriesCount(
			long microblogsEntryUserId, int type)
		throws PortalException {

		return microblogsEntryFinder.countByU_T_MU(
			getGuestOrUserId(), type, microblogsEntryUserId);
	}

	@Override
	public MicroblogsEntry updateMicroblogsEntry(
			long microblogsEntryId, String content, int socialRelationType,
			ServiceContext serviceContext)
		throws PortalException {

		_microblogsEntryModelResourcePermission.check(
			getPermissionChecker(), microblogsEntryId, ActionKeys.UPDATE);

		return microblogsEntryLocalService.updateMicroblogsEntry(
			microblogsEntryId, content, socialRelationType, serviceContext);
	}

	@Reference(
		target = "(model.class.name=com.liferay.microblogs.model.MicroblogsEntry)"
	)
	private ModelResourcePermission<MicroblogsEntry>
		_microblogsEntryModelResourcePermission;

	@Reference(
		target = "(resource.name=" + MicroblogsConstants.RESOURCE_NAME + ")"
	)
	private PortletResourcePermission _portletResourcePermission;

}