/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.redirect.service.impl;

import com.liferay.portal.aop.AopService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.security.permission.resource.PortletResourcePermission;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.redirect.constants.RedirectConstants;
import com.liferay.redirect.model.RedirectEntry;
import com.liferay.redirect.service.base.RedirectEntryServiceBaseImpl;

import java.util.Date;
import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferencePolicy;
import org.osgi.service.component.annotations.ReferencePolicyOption;

/**
 * @author Brian Wing Shun Chan
 */
@Component(
	property = {
		"json.web.service.context.name=redirect",
		"json.web.service.context.path=RedirectEntry"
	},
	service = AopService.class
)
public class RedirectEntryServiceImpl extends RedirectEntryServiceBaseImpl {

	@Override
	public RedirectEntry addRedirectEntry(
			long groupId, String destinationURL, Date expirationDate,
			boolean permanent, String sourceURL, ServiceContext serviceContext)
		throws PortalException {

		_portletResourcePermission.check(
			getPermissionChecker(), groupId, ActionKeys.ADD_ENTRY);

		return redirectEntryLocalService.addRedirectEntry(
			groupId, destinationURL, expirationDate, permanent, sourceURL,
			serviceContext);
	}

	@Override
	public RedirectEntry addRedirectEntry(
			long groupId, String destinationURL, Date expirationDate,
			String groupBaseURL, boolean permanent, String sourceURL,
			boolean updateChainedRedirectEntries, ServiceContext serviceContext)
		throws PortalException {

		_portletResourcePermission.check(
			getPermissionChecker(), groupId, ActionKeys.ADD_ENTRY);

		return redirectEntryLocalService.addRedirectEntry(
			groupId, destinationURL, expirationDate, groupBaseURL, permanent,
			sourceURL, updateChainedRedirectEntries, serviceContext);
	}

	@Override
	public RedirectEntry deleteRedirectEntry(long redirectEntryId)
		throws PortalException {

		_redirectEntryModelResourcePermission.check(
			getPermissionChecker(), redirectEntryId, ActionKeys.DELETE);

		return redirectEntryLocalService.deleteRedirectEntry(redirectEntryId);
	}

	@Override
	public RedirectEntry fetchRedirectEntry(long redirectEntryId)
		throws PortalException {

		RedirectEntry redirectEntry =
			redirectEntryPersistence.fetchByPrimaryKey(redirectEntryId);

		if (redirectEntry != null) {
			_redirectEntryModelResourcePermission.check(
				getPermissionChecker(), redirectEntry, ActionKeys.VIEW);
		}

		return redirectEntry;
	}

	@Override
	public List<RedirectEntry> getRedirectEntries(
			long groupId, int start, int end,
			OrderByComparator<RedirectEntry> orderByComparator)
		throws PortalException {

		PermissionChecker permissionChecker = getPermissionChecker();

		if (!permissionChecker.hasPermission(
				groupId, RedirectEntry.class.getName(),
				RedirectEntry.class.getName(), ActionKeys.VIEW)) {

			throw new PrincipalException.MustHavePermission(
				permissionChecker, RedirectEntry.class.getName(),
				RedirectEntry.class.getName(), ActionKeys.VIEW);
		}

		return redirectEntryPersistence.findByGroupId(
			groupId, start, end, orderByComparator);
	}

	@Override
	public int getRedirectEntriesCount(long groupId) throws PortalException {
		PermissionChecker permissionChecker = getPermissionChecker();

		if (!permissionChecker.hasPermission(
				groupId, RedirectEntry.class.getName(),
				RedirectEntry.class.getName(), ActionKeys.VIEW)) {

			throw new PrincipalException.MustHavePermission(
				permissionChecker, RedirectEntry.class.getName(),
				RedirectEntry.class.getName(), ActionKeys.VIEW);
		}

		return redirectEntryPersistence.countByGroupId(groupId);
	}

	@Override
	public RedirectEntry updateRedirectEntry(
			long redirectEntryId, String destinationURL, Date expirationDate,
			boolean permanent, String sourceURL)
		throws PortalException {

		_redirectEntryModelResourcePermission.check(
			getPermissionChecker(), redirectEntryId, ActionKeys.UPDATE);

		return redirectEntryLocalService.updateRedirectEntry(
			redirectEntryId, destinationURL, expirationDate, permanent,
			sourceURL);
	}

	@Override
	public RedirectEntry updateRedirectEntry(
			long redirectEntryId, String destinationURL, Date expirationDate,
			String groupBaseURL, boolean permanent, String sourceURL,
			boolean updateChainedRedirectEntries)
		throws PortalException {

		_redirectEntryModelResourcePermission.check(
			getPermissionChecker(), redirectEntryId, ActionKeys.UPDATE);

		return redirectEntryLocalService.updateRedirectEntry(
			redirectEntryId, destinationURL, expirationDate, groupBaseURL,
			permanent, sourceURL, updateChainedRedirectEntries);
	}

	@Reference(
		policy = ReferencePolicy.DYNAMIC,
		policyOption = ReferencePolicyOption.GREEDY,
		target = "(resource.name=" + RedirectConstants.RESOURCE_NAME + ")"
	)
	private volatile PortletResourcePermission _portletResourcePermission;

	@Reference(
		policy = ReferencePolicy.DYNAMIC,
		policyOption = ReferencePolicyOption.GREEDY,
		target = "(model.class.name=com.liferay.redirect.model.RedirectEntry)"
	)
	private volatile ModelResourcePermission<RedirectEntry>
		_redirectEntryModelResourcePermission;

}