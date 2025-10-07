/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.service.impl;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.bean.BeanReference;
import com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery;
import com.liferay.portal.kernel.dao.orm.RestrictionsFactoryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.SystemEvent;
import com.liferay.portal.kernel.model.SystemEventConstants;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.security.auth.PrincipalThreadLocal;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.persistence.CompanyPersistence;
import com.liferay.portal.kernel.service.persistence.GroupPersistence;
import com.liferay.portal.kernel.service.persistence.UserPersistence;
import com.liferay.portal.kernel.systemevent.SystemEventHierarchyEntry;
import com.liferay.portal.kernel.systemevent.SystemEventHierarchyEntryThreadLocal;
import com.liferay.portal.kernel.util.PropsValues;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.service.base.SystemEventLocalServiceBaseImpl;
import com.liferay.portal.util.PortalInstances;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * @author Zsolt Berentey
 */
public class SystemEventLocalServiceImpl
	extends SystemEventLocalServiceBaseImpl {

	@Override
	public SystemEvent addSystemEvent(
			long userId, long groupId, String classExternalReferenceCode,
			String className, long classPK, String classUuid,
			String referrerClassName, int type, String extraData)
		throws PortalException {

		if (userId == 0) {
			userId = PrincipalThreadLocal.getUserId();
		}

		long companyId = 0;
		String userName = StringPool.BLANK;

		if (userId > 0) {
			User user = _userPersistence.findByPrimaryKey(userId);

			companyId = user.getCompanyId();
			userName = user.getFullName();
		}
		else if (groupId > 0) {
			Group group = _groupPersistence.findByPrimaryKey(groupId);

			companyId = group.getCompanyId();
		}

		return addSystemEvent(
			userId, companyId, groupId, classExternalReferenceCode, className,
			classPK, classUuid, referrerClassName, type, extraData, userName);
	}

	@Override
	public SystemEvent addSystemEvent(
			long companyId, String classExternalReferenceCode, String className,
			long classPK, String classUuid, String referrerClassName, int type,
			String extraData)
		throws PortalException {

		return addSystemEvent(
			0, companyId, 0, classExternalReferenceCode, className, classPK,
			classUuid, referrerClassName, type, extraData, StringPool.BLANK);
	}

	@Override
	public void checkSystemEvents() throws PortalException {
		if (PropsValues.STAGING_SYSTEM_EVENT_MAX_AGE <= 0) {
			return;
		}

		ActionableDynamicQuery actionableDynamicQuery =
			systemEventLocalService.getActionableDynamicQuery();

		actionableDynamicQuery.setAddCriteriaMethod(
			dynamicQuery -> {
				Calendar calendar = Calendar.getInstance();

				calendar.add(
					Calendar.HOUR, -PropsValues.STAGING_SYSTEM_EVENT_MAX_AGE);

				dynamicQuery.add(
					RestrictionsFactoryUtil.lt(
						"createDate", calendar.getTime()));
			});
		actionableDynamicQuery.setPerformActionMethod(
			systemEvent -> deleteSystemEvent((SystemEvent)systemEvent));

		actionableDynamicQuery.performActions();
	}

	@Override
	public void deleteSystemEvents(long groupId) {
		systemEventPersistence.removeByGroupId(groupId);
	}

	@Override
	public void deleteSystemEvents(long groupId, long systemEventSetKey) {
		systemEventPersistence.removeByG_S(groupId, systemEventSetKey);
	}

	@Override
	public SystemEvent fetchSystemEvent(
		long groupId, long classNameId, long classPK, int type) {

		return systemEventPersistence.fetchByG_C_C_T_First(
			groupId, classNameId, classPK, type, null);
	}

	@Override
	public List<SystemEvent> getSystemEvents(
		long groupId, long classNameId, long classPK) {

		return systemEventPersistence.findByG_C_C(
			groupId, classNameId, classPK);
	}

	@Override
	public List<SystemEvent> getSystemEvents(
		long groupId, long classNameId, long classPK, int type) {

		return systemEventPersistence.findByG_C_C_T(
			groupId, classNameId, classPK, type);
	}

	@Override
	public boolean validateGroup(long groupId) throws PortalException {
		if (groupId > 0) {
			Group group = _groupLocalService.getGroup(groupId);

			if (group.hasStagingGroup() && !group.isStagedRemotely()) {
				return false;
			}

			if (group.hasRemoteStagingGroup() &&
				!PropsValues.STAGING_LIVE_GROUP_REMOTE_STAGING_ENABLED) {

				return false;
			}
		}

		return true;
	}

	protected SystemEvent addSystemEvent(
			long userId, long companyId, long groupId,
			String classExternalReferenceCode, String className, long classPK,
			String classUuid, String referrerClassName, int type,
			String extraData, String userName)
		throws PortalException {

		SystemEventHierarchyEntry systemEventHierarchyEntry =
			SystemEventHierarchyEntryThreadLocal.peek();

		int action = SystemEventConstants.ACTION_NONE;

		if (systemEventHierarchyEntry != null) {
			action = systemEventHierarchyEntry.getAction();

			if ((action == SystemEventConstants.ACTION_SKIP) &&
				!systemEventHierarchyEntry.hasTypedModel(className, classPK)) {

				return null;
			}
		}

		if (!PortalInstances.isCurrentCompanyInDeletionProcess()) {
			Company company = _companyPersistence.findByPrimaryKey(companyId);

			Group companyGroup = company.getGroup();

			if (companyGroup.getGroupId() == groupId) {
				groupId = 0;
			}
		}

		if (!validateGroup(groupId)) {
			return null;
		}

		if (Validator.isNotNull(referrerClassName) &&
			referrerClassName.equals(className)) {

			referrerClassName = null;
		}

		long systemEventId = 0;

		if ((systemEventHierarchyEntry != null) &&
			systemEventHierarchyEntry.hasTypedModel(className, classPK)) {

			systemEventId = systemEventHierarchyEntry.getSystemEventId();
		}
		else {
			systemEventId = counterLocalService.increment();
		}

		SystemEvent systemEvent = systemEventPersistence.create(systemEventId);

		systemEvent.setGroupId(groupId);
		systemEvent.setCompanyId(companyId);
		systemEvent.setUserId(userId);
		systemEvent.setUserName(userName);
		systemEvent.setCreateDate(new Date());
		systemEvent.setClassExternalReferenceCode(classExternalReferenceCode);
		systemEvent.setClassName(className);
		systemEvent.setClassPK(classPK);
		systemEvent.setClassUuid(classUuid);
		systemEvent.setReferrerClassName(referrerClassName);

		long parentSystemEventId = 0;

		if (action == SystemEventConstants.ACTION_HIERARCHY) {
			if (systemEventHierarchyEntry.hasTypedModel(className, classPK)) {
				parentSystemEventId =
					systemEventHierarchyEntry.getParentSystemEventId();
			}
			else {
				parentSystemEventId =
					systemEventHierarchyEntry.getSystemEventId();
			}
		}

		systemEvent.setParentSystemEventId(parentSystemEventId);

		long systemEventSetKey = 0;

		if ((action == SystemEventConstants.ACTION_GROUP) ||
			(action == SystemEventConstants.ACTION_HIERARCHY)) {

			systemEventSetKey =
				systemEventHierarchyEntry.getSystemEventSetKey();
		}
		else {
			systemEventSetKey = counterLocalService.increment();
		}

		systemEvent.setSystemEventSetKey(systemEventSetKey);

		systemEvent.setType(type);
		systemEvent.setExtraData(extraData);

		return systemEventPersistence.update(systemEvent);
	}

	@BeanReference(type = CompanyPersistence.class)
	private CompanyPersistence _companyPersistence;

	@BeanReference(type = GroupLocalService.class)
	private GroupLocalService _groupLocalService;

	@BeanReference(type = GroupPersistence.class)
	private GroupPersistence _groupPersistence;

	@BeanReference(type = UserPersistence.class)
	private UserPersistence _userPersistence;

}