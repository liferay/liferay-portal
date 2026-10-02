/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.changeset.service.impl;

import com.liferay.changeset.model.ChangesetCollection;
import com.liferay.changeset.model.ChangesetEntry;
import com.liferay.changeset.service.base.ChangesetEntryLocalServiceBaseImpl;
import com.liferay.changeset.service.persistence.ChangesetCollectionPersistence;
import com.liferay.portal.aop.AopService;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.dao.orm.RestrictionsFactoryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.util.SetUtil;
import com.liferay.portal.kernel.util.Validator;

import java.util.List;
import java.util.Set;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Brian Wing Shun Chan
 */
@Component(
	property = "model.class.name=com.liferay.changeset.model.ChangesetEntry",
	service = AopService.class
)
public class ChangesetEntryLocalServiceImpl
	extends ChangesetEntryLocalServiceBaseImpl {

	@Override
	public ChangesetEntry addChangesetEntry(
			long userId, long changesetCollectionId,
			String classExternalReferenceCode, long classNameId, long classPK)
		throws PortalException {

		User user = _userLocalService.getUser(userId);
		ChangesetCollection changesetCollection =
			_changesetCollectionPersistence.fetchByPrimaryKey(
				changesetCollectionId);

		long changesetEntryId = counterLocalService.increment();

		ChangesetEntry changesetEntry = changesetEntryPersistence.create(
			changesetEntryId);

		changesetEntry.setGroupId(changesetCollection.getGroupId());
		changesetEntry.setCompanyId(user.getCompanyId());
		changesetEntry.setUserId(user.getUserId());
		changesetEntry.setUserName(user.getFullName());
		changesetEntry.setChangesetCollectionId(changesetCollectionId);
		changesetEntry.setClassExternalReferenceCode(
			classExternalReferenceCode);
		changesetEntry.setClassNameId(classNameId);
		changesetEntry.setClassPK(classPK);

		return changesetEntryPersistence.update(changesetEntry);
	}

	@Override
	public void deleteChangesetEntries(long changesetCollectionId) {
		changesetEntryPersistence.removeByChangesetCollectionId(
			changesetCollectionId);
	}

	@Override
	public void deleteChangesetEntries(Set<Long> changesetEntryIds) {
		if (SetUtil.isEmpty(changesetEntryIds)) {
			return;
		}

		for (long changesetEntryId : changesetEntryIds) {
			ChangesetEntry changesetEntry =
				changesetEntryPersistence.fetchByPrimaryKey(changesetEntryId);

			if (changesetEntry != null) {
				changesetEntryPersistence.remove(changesetEntry);
			}
		}
	}

	@Override
	public void deleteEntry(long changesetId, long classNameId, long classPK) {
		ChangesetEntry changesetEntry =
			changesetEntryLocalService.fetchChangesetEntry(
				changesetId, classNameId, classPK);

		if (changesetEntry != null) {
			changesetEntryLocalService.deleteChangesetEntry(changesetEntry);
		}
	}

	@Override
	public ChangesetEntry fetchChangesetEntry(
		long changesetCollectionId, long classNameId, long classPK) {

		return changesetEntryPersistence.fetchByC_C_C(
			changesetCollectionId, classNameId, classPK);
	}

	@Override
	public ChangesetEntry fetchChangesetEntry(
		long changesetCollectionId, String classExternalReferenceCode,
		long classNameId) {

		return changesetEntryPersistence.fetchByC_CERC_C(
			changesetCollectionId, classExternalReferenceCode, classNameId);
	}

	@Override
	public ChangesetEntry fetchOrAddChangesetEntry(
			long changesetCollectionId, long classNameId, long classPK)
		throws PortalException {

		return fetchOrAddChangesetEntry(
			changesetCollectionId, null, classNameId, classPK);
	}

	@Override
	public ChangesetEntry fetchOrAddChangesetEntry(
			long changesetCollectionId, String classExternalReferenceCode,
			long classNameId, long classPK)
		throws PortalException {

		ChangesetEntry changesetEntry = null;

		if (!Validator.isBlank(classExternalReferenceCode)) {
			changesetEntry = changesetEntryLocalService.fetchChangesetEntry(
				changesetCollectionId, classExternalReferenceCode, classNameId);
		}

		if (changesetEntry != null) {
			return changesetEntry;
		}

		changesetEntry = changesetEntryLocalService.fetchChangesetEntry(
			changesetCollectionId, classNameId, classPK);

		if (changesetEntry != null) {
			return changesetEntry;
		}

		ChangesetCollection changesetCollection =
			_changesetCollectionPersistence.findByPrimaryKey(
				changesetCollectionId);

		User user = _userLocalService.getGuestUser(
			changesetCollection.getCompanyId());

		return changesetEntryLocalService.addChangesetEntry(
			user.getUserId(), changesetCollectionId, classExternalReferenceCode,
			classNameId, classPK);
	}

	@Override
	public List<ChangesetEntry> getChangesetEntries(
		long changesetCollectionId, long classNameId) {

		return changesetEntryPersistence.findByC_C(
			changesetCollectionId, classNameId);
	}

	@Override
	public long getChangesetEntriesCount(
		long changesetCollectionId, long classNameId) {

		return changesetEntryPersistence.countByC_C(
			changesetCollectionId, classNameId);
	}

	@Override
	public long getChangesetEntriesCount(
		long changesetCollectionId, long classNameId, Set<Long> classPKs) {

		DynamicQuery dynamicQuery = dynamicQuery();

		dynamicQuery.add(
			RestrictionsFactoryUtil.eq(
				"changesetCollectionId", changesetCollectionId));
		dynamicQuery.add(
			RestrictionsFactoryUtil.eq("classNameId", classNameId));

		if (SetUtil.isNotEmpty(classPKs)) {
			dynamicQuery.add(RestrictionsFactoryUtil.in("classPK", classPKs));
		}
		else {
			return 0;
		}

		return dynamicQueryCount(dynamicQuery);
	}

	@Reference
	private ChangesetCollectionPersistence _changesetCollectionPersistence;

	@Reference
	private UserLocalService _userLocalService;

}