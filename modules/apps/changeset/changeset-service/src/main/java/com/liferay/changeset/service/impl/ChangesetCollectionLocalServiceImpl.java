/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.changeset.service.impl;

import com.liferay.changeset.exception.NoSuchCollectionException;
import com.liferay.changeset.model.ChangesetCollection;
import com.liferay.changeset.service.ChangesetEntryLocalService;
import com.liferay.changeset.service.base.ChangesetCollectionLocalServiceBaseImpl;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.aop.AopService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.UserLocalService;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Brian Wing Shun Chan
 */
@Component(
	property = "model.class.name=com.liferay.changeset.model.ChangesetCollection",
	service = AopService.class
)
public class ChangesetCollectionLocalServiceImpl
	extends ChangesetCollectionLocalServiceBaseImpl {

	@Override
	public ChangesetCollection addChangesetCollection(
			long userId, long groupId, String name, String description)
		throws PortalException {

		User user = _userLocalService.getUser(userId);

		long changesetCollectionId = counterLocalService.increment();

		ChangesetCollection changesetCollection =
			changesetCollectionPersistence.create(changesetCollectionId);

		changesetCollection.setGroupId(groupId);
		changesetCollection.setCompanyId(user.getCompanyId());
		changesetCollection.setUserId(user.getUserId());
		changesetCollection.setUserName(user.getFullName());
		changesetCollection.setName(name);
		changesetCollection.setDescription(description);

		return changesetCollectionPersistence.update(changesetCollection);
	}

	@Override
	public ChangesetCollection deleteChangesetCollection(
			long changesetCollectionId)
		throws PortalException {

		_changesetEntryLocalService.deleteChangesetEntries(
			changesetCollectionId);

		return super.deleteChangesetCollection(changesetCollectionId);
	}

	@Override
	public ChangesetCollection fetchChangesetCollection(
		long groupId, String name) {

		return changesetCollectionPersistence.fetchByG_N(groupId, name);
	}

	@Override
	public ChangesetCollection fetchOrAddChangesetCollection(
			long groupId, String name)
		throws PortalException {

		ChangesetCollection changesetCollection =
			changesetCollectionPersistence.fetchByG_N(groupId, name);

		if (changesetCollection != null) {
			return changesetCollection;
		}

		Group group = _groupLocalService.getGroup(groupId);

		User user = _userLocalService.getGuestUser(group.getCompanyId());

		return changesetCollectionLocalService.addChangesetCollection(
			user.getUserId(), groupId, name, StringPool.BLANK);
	}

	@Override
	public ChangesetCollection getChangesetCollection(long groupId, String name)
		throws NoSuchCollectionException {

		return changesetCollectionPersistence.findByG_N(groupId, name);
	}

	@Reference
	private ChangesetEntryLocalService _changesetEntryLocalService;

	@Reference
	private GroupLocalService _groupLocalService;

	@Reference
	private UserLocalService _userLocalService;

}