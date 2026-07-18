/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.saved.content.service.impl;

import com.liferay.portal.aop.AopService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.security.permission.resource.PortletResourcePermission;
import com.liferay.portal.kernel.service.ClassNameLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.saved.content.constants.SavedContentConstants;
import com.liferay.saved.content.model.SavedContentEntry;
import com.liferay.saved.content.service.base.SavedContentEntryServiceBaseImpl;
import com.liferay.saved.content.util.comparator.SavedContentEntryClassNameIdComparator;

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
		"json.web.service.context.name=savedcontententry",
		"json.web.service.context.path=SavedContentEntry"
	},
	service = AopService.class
)
public class SavedContentEntryServiceImpl
	extends SavedContentEntryServiceBaseImpl {

	@Override
	public SavedContentEntry addSavedContentEntry(
			long groupId, String className, long classPK,
			ServiceContext serviceContext)
		throws PortalException {

		_portletResourcePermission.check(
			getPermissionChecker(), groupId, ActionKeys.ADD_ENTRY);

		return savedContentEntryLocalService.addSavedContentEntry(
			getUserId(), groupId, className, classPK, serviceContext);
	}

	@Override
	public void deleteSavedContentEntry(SavedContentEntry savedContentEntry)
		throws PortalException {

		_savedContentEntryModelResourcePermission.check(
			getPermissionChecker(), savedContentEntry, ActionKeys.DELETE);

		savedContentEntryLocalService.deleteSavedContentEntry(
			savedContentEntry);
	}

	@Override
	public SavedContentEntry fetchSavedContentEntry(
			long groupId, String className, long classPK)
		throws PortalException {

		SavedContentEntry savedContentEntry =
			savedContentEntryPersistence.fetchByG_U_C_C(
				groupId, getUserId(),
				_classNameLocalService.getClassNameId(className), classPK);

		if (savedContentEntry != null) {
			_savedContentEntryModelResourcePermission.check(
				getPermissionChecker(), savedContentEntry, ActionKeys.VIEW);
		}

		return savedContentEntry;
	}

	@Override
	public List<SavedContentEntry> getGroupUserSavedContentEntries(
			long groupId, int start, int end)
		throws PortalException {

		return savedContentEntryPersistence.findByG_U(
			groupId, getUserId(), start, end,
			SavedContentEntryClassNameIdComparator.getInstance(false));
	}

	@Override
	public List<SavedContentEntry> getGroupUserSavedContentEntries(
			long groupId, int start, int end,
			OrderByComparator<SavedContentEntry> orderByComparator)
		throws PortalException {

		return savedContentEntryPersistence.findByG_U(
			groupId, getUserId(), start, end, orderByComparator);
	}

	@Override
	public int getGroupUserSavedContentEntriesCount(long groupId)
		throws PortalException {

		return savedContentEntryPersistence.filterCountByG_U(
			groupId, getUserId());
	}

	@Override
	public SavedContentEntry getSavedContentEntry(
			long groupId, String className, long classPK)
		throws PortalException {

		SavedContentEntry savedContentEntry =
			savedContentEntryPersistence.findByG_U_C_C(
				groupId, getUserId(),
				_classNameLocalService.getClassNameId(className), classPK);

		_savedContentEntryModelResourcePermission.check(
			getPermissionChecker(), savedContentEntry, ActionKeys.VIEW);

		return savedContentEntry;
	}

	@Reference
	private ClassNameLocalService _classNameLocalService;

	@Reference(
		target = "(resource.name=" + SavedContentConstants.RESOURCE_NAME + ")"
	)
	private PortletResourcePermission _portletResourcePermission;

	@Reference(
		policy = ReferencePolicy.DYNAMIC,
		policyOption = ReferencePolicyOption.GREEDY,
		target = "(model.class.name=com.liferay.saved.content.model.SavedContentEntry)"
	)
	private volatile ModelResourcePermission<SavedContentEntry>
		_savedContentEntryModelResourcePermission;

}