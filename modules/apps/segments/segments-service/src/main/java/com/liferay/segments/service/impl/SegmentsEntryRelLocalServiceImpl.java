/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.segments.service.impl;

import com.liferay.portal.aop.AopService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.segments.model.SegmentsEntryRel;
import com.liferay.segments.service.base.SegmentsEntryRelLocalServiceBaseImpl;

import java.util.Date;
import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Eduardo García
 */
@Component(
	property = "model.class.name=com.liferay.segments.model.SegmentsEntryRel",
	service = AopService.class
)
public class SegmentsEntryRelLocalServiceImpl
	extends SegmentsEntryRelLocalServiceBaseImpl {

	@Override
	public SegmentsEntryRel addSegmentsEntryRel(
			long segmentsEntryId, long classNameId, long classPK,
			ServiceContext serviceContext)
		throws PortalException {

		User user = _userLocalService.getUser(serviceContext.getUserId());

		long segmentsEntryRelId = counterLocalService.increment();

		SegmentsEntryRel segmentsEntryRel = segmentsEntryRelPersistence.create(
			segmentsEntryRelId);

		segmentsEntryRel.setGroupId(serviceContext.getScopeGroupId());
		segmentsEntryRel.setCompanyId(user.getCompanyId());
		segmentsEntryRel.setUserId(user.getUserId());
		segmentsEntryRel.setUserName(user.getFullName());
		segmentsEntryRel.setCreateDate(
			serviceContext.getCreateDate(new Date()));
		segmentsEntryRel.setModifiedDate(
			serviceContext.getModifiedDate(new Date()));
		segmentsEntryRel.setSegmentsEntryId(segmentsEntryId);
		segmentsEntryRel.setClassNameId(classNameId);
		segmentsEntryRel.setClassPK(classPK);

		return segmentsEntryRelPersistence.update(segmentsEntryRel);
	}

	@Override
	public void addSegmentsEntryRels(
			long segmentsEntryId, long classNameId, long[] classPKs,
			ServiceContext serviceContext)
		throws PortalException {

		for (long classPK : classPKs) {
			if (hasSegmentsEntryRel(segmentsEntryId, classNameId, classPK)) {
				continue;
			}

			addSegmentsEntryRel(
				segmentsEntryId, classNameId, classPK, serviceContext);
		}
	}

	@Override
	public void deleteSegmentsEntryRel(
			long segmentsEntryId, long classNameId, long classPK)
		throws PortalException {

		segmentsEntryRelPersistence.removeByS_CN_CPK(
			segmentsEntryId, classNameId, classPK);
	}

	@Override
	public void deleteSegmentsEntryRels(long segmentsEntryId) {
		segmentsEntryRelPersistence.removeBySegmentsEntryId(segmentsEntryId);
	}

	@Override
	public void deleteSegmentsEntryRels(long classNameId, long classPK) {
		segmentsEntryRelPersistence.removeByCN_CPK(classNameId, classPK);
	}

	@Override
	public void deleteSegmentsEntryRels(
			long segmentsEntryId, long classNameId, long[] classPKs)
		throws PortalException {

		for (long classPK : classPKs) {
			deleteSegmentsEntryRel(segmentsEntryId, classNameId, classPK);
		}
	}

	@Override
	public SegmentsEntryRel fetchSegmentsEntryRel(
		long segmentsEntryId, long classNameId, long classPK) {

		return segmentsEntryRelPersistence.fetchByS_CN_CPK(
			segmentsEntryId, classNameId, classPK);
	}

	@Override
	public List<SegmentsEntryRel> getSegmentsEntryRels(long segmentsEntryId) {
		return segmentsEntryRelPersistence.findBySegmentsEntryId(
			segmentsEntryId);
	}

	@Override
	public List<SegmentsEntryRel> getSegmentsEntryRels(
		long segmentsEntryId, int start, int end,
		OrderByComparator<SegmentsEntryRel> orderByComparator) {

		return segmentsEntryRelPersistence.findBySegmentsEntryId(
			segmentsEntryId, start, end, orderByComparator);
	}

	@Override
	public List<SegmentsEntryRel> getSegmentsEntryRels(
		long classNameId, long classPK) {

		return segmentsEntryRelPersistence.findByCN_CPK(classNameId, classPK);
	}

	@Override
	public List<SegmentsEntryRel> getSegmentsEntryRels(
		long groupId, long classNameId, long classPK) {

		return segmentsEntryRelPersistence.findByG_CN_CPK(
			groupId, classNameId, classPK);
	}

	@Override
	public int getSegmentsEntryRelsCount(long segmentsEntryId) {
		return segmentsEntryRelPersistence.countBySegmentsEntryId(
			segmentsEntryId);
	}

	@Override
	public int getSegmentsEntryRelsCount(long classNameId, long classPK) {
		return segmentsEntryRelPersistence.countByCN_CPK(classNameId, classPK);
	}

	@Override
	public int getSegmentsEntryRelsCount(
		long groupId, long classNameId, long classPK) {

		return segmentsEntryRelPersistence.countByG_CN_CPK(
			groupId, classNameId, classPK);
	}

	@Override
	public boolean hasSegmentsEntryRel(
		long segmentsEntryId, long classNameId, long classPK) {

		SegmentsEntryRel segmentsEntryRel = fetchSegmentsEntryRel(
			segmentsEntryId, classNameId, classPK);

		if (segmentsEntryRel == null) {
			return false;
		}

		return true;
	}

	@Reference
	private UserLocalService _userLocalService;

}