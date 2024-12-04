/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.service.impl;

import com.liferay.exportimport.kernel.staging.StagingUtil;
import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.bean.BeanReference;
import com.liferay.portal.kernel.exception.NoSuchWorkflowDefinitionLinkException;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.WorkflowDefinitionLink;
import com.liferay.portal.kernel.service.ClassNameLocalService;
import com.liferay.portal.kernel.service.persistence.UserPersistence;
import com.liferay.portal.kernel.transaction.Transactional;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.ObjectValuePair;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.kernel.workflow.WorkflowException;
import com.liferay.portal.service.base.WorkflowDefinitionLinkLocalServiceBaseImpl;

import java.util.List;

/**
 * @author Jorge Ferrer
 * @author Bruno Farache
 * @author Brian Wing Shun Chan
 * @author Juan Fernández
 * @author Marcellus Tavares
 */
public class WorkflowDefinitionLinkLocalServiceImpl
	extends WorkflowDefinitionLinkLocalServiceBaseImpl {

	@Override
	public WorkflowDefinitionLink addWorkflowDefinitionLink(
			long userId, long companyId, long groupId, String className,
			long classPK, long typePK, String workflowDefinitionName,
			int workflowDefinitionVersion)
		throws PortalException {

		WorkflowDefinitionLink workflowDefinitionLink =
			workflowDefinitionLinkPersistence.fetchByG_C_C_W(
				groupId, companyId,
				_classNameLocalService.getClassNameId(className),
				workflowDefinitionName);

		if (workflowDefinitionLink != null) {
			throw new WorkflowException(
				StringBundler.concat(
					"WorkflowDefinitionLink ",
					workflowDefinitionLink.getWorkflowDefinitionName(), " and ",
					workflowDefinitionLink.getClassName(), " already exists"));
		}

		User user = _userPersistence.findByPrimaryKey(userId);

		long workflowDefinitionLinkId = counterLocalService.increment();

		workflowDefinitionLink = workflowDefinitionLinkPersistence.create(
			workflowDefinitionLinkId);

		workflowDefinitionLink.setGroupId(StagingUtil.getLiveGroupId(groupId));
		workflowDefinitionLink.setCompanyId(companyId);
		workflowDefinitionLink.setUserId(userId);
		workflowDefinitionLink.setUserName(user.getFullName());
		workflowDefinitionLink.setClassNameId(
			_classNameLocalService.getClassNameId(className));
		workflowDefinitionLink.setClassPK(classPK);
		workflowDefinitionLink.setTypePK(typePK);
		workflowDefinitionLink.setWorkflowDefinitionName(
			workflowDefinitionName);
		workflowDefinitionLink.setWorkflowDefinitionVersion(
			workflowDefinitionVersion);

		return workflowDefinitionLinkPersistence.update(workflowDefinitionLink);
	}

	@Override
	public void deleteWorkflowDefinitionLink(
		long companyId, long groupId, String className, long classPK,
		long typePK) {

		WorkflowDefinitionLink workflowDefinitionLink =
			fetchWorkflowDefinitionLink(
				companyId, groupId, className, classPK, typePK, true);

		if (workflowDefinitionLink != null) {
			deleteWorkflowDefinitionLink(workflowDefinitionLink);
		}
	}

	@Override
	public WorkflowDefinitionLink fetchDefaultWorkflowDefinitionLink(
		long companyId, String className, long classPK, long typePK) {

		return workflowDefinitionLinkPersistence.fetchByG_C_C_C_T(
			WorkflowConstants.DEFAULT_GROUP_ID, companyId,
			_classNameLocalService.getClassNameId(className), classPK, typePK);
	}

	@Override
	public WorkflowDefinitionLink fetchWorkflowDefinitionLink(
		long companyId, long groupId, String className, long classPK,
		long typePK) {

		return fetchWorkflowDefinitionLink(
			companyId, groupId, className, classPK, typePK, false);
	}

	@Override
	public WorkflowDefinitionLink fetchWorkflowDefinitionLink(
		long companyId, long groupId, String className, long classPK,
		long typePK, boolean strict) {

		WorkflowDefinitionLink workflowDefinitionLink =
			workflowDefinitionLinkPersistence.fetchByG_C_C_C_T(
				StagingUtil.getLiveGroupId(groupId), companyId,
				_classNameLocalService.getClassNameId(className), classPK,
				typePK);

		if (!strict && (workflowDefinitionLink == null)) {
			workflowDefinitionLink =
				workflowDefinitionLinkPersistence.fetchByG_C_C_C_T(
					PortalUtil.getSiteGroupId(groupId), companyId,
					_classNameLocalService.getClassNameId(className), classPK,
					typePK);

			if (workflowDefinitionLink == null) {
				workflowDefinitionLink =
					workflowDefinitionLinkPersistence.fetchByG_C_C_C_T(
						WorkflowConstants.DEFAULT_GROUP_ID, companyId,
						_classNameLocalService.getClassNameId(className),
						classPK, typePK);
			}
		}

		return workflowDefinitionLink;
	}

	@Override
	public List<WorkflowDefinitionLink> fetchWorkflowDefinitionLinks(
		long companyId, long groupId, String className, long classPK) {

		return workflowDefinitionLinkPersistence.findByG_C_C_C(
			StagingUtil.getLiveGroupId(groupId), companyId,
			_classNameLocalService.getClassNameId(className), classPK);
	}

	@Override
	public WorkflowDefinitionLink getDefaultWorkflowDefinitionLink(
			long companyId, String className, long classPK, long typePK)
		throws PortalException {

		return workflowDefinitionLinkPersistence.findByG_C_C_C_T(
			WorkflowConstants.DEFAULT_GROUP_ID, companyId,
			_classNameLocalService.getClassNameId(className), classPK, typePK);
	}

	@Override
	public WorkflowDefinitionLink getWorkflowDefinitionLink(
			long companyId, long groupId, String className, long classPK,
			long typePK)
		throws PortalException {

		return getWorkflowDefinitionLink(
			companyId, groupId, className, classPK, typePK, false);
	}

	@Override
	public WorkflowDefinitionLink getWorkflowDefinitionLink(
			long companyId, long groupId, String className, long classPK,
			long typePK, boolean strict)
		throws PortalException {

		WorkflowDefinitionLink workflowDefinitionLink =
			fetchWorkflowDefinitionLink(
				companyId, groupId, className, classPK, typePK, strict);

		if (workflowDefinitionLink == null) {
			throw new NoSuchWorkflowDefinitionLinkException(
				StringBundler.concat(
					"No workflow exists with the key {groupId=",
					StagingUtil.getLiveGroupId(groupId), ", companyId=",
					companyId, ", and className=", className, "}"));
		}

		return workflowDefinitionLink;
	}

	@Override
	public List<WorkflowDefinitionLink> getWorkflowDefinitionLinks(
			long companyId, long groupId, long classPK)
		throws PortalException {

		return workflowDefinitionLinkPersistence.findByG_C_CPK(
			groupId, companyId, classPK);
	}

	@Override
	public List<WorkflowDefinitionLink> getWorkflowDefinitionLinks(
			long companyId, long groupId, String className, long classPK)
		throws PortalException {

		return workflowDefinitionLinkPersistence.findByG_C_C_C(
			companyId, StagingUtil.getLiveGroupId(groupId),
			_classNameLocalService.getClassNameId(className), classPK);
	}

	@Override
	public List<WorkflowDefinitionLink> getWorkflowDefinitionLinks(
			long companyId, String workflowDefinitionName,
			int workflowDefinitionVersion)
		throws PortalException {

		return workflowDefinitionLinkPersistence.findByC_W_W(
			companyId, workflowDefinitionName, workflowDefinitionVersion);
	}

	@Override
	public int getWorkflowDefinitionLinksCount(
		long companyId, long groupId, String className) {

		return workflowDefinitionLinkPersistence.countByG_C_C(
			StagingUtil.getLiveGroupId(groupId), companyId,
			_classNameLocalService.getClassNameId(className));
	}

	@Override
	public int getWorkflowDefinitionLinksCount(
		long companyId, String workflowDefinitionName,
		int workflowDefinitionVersion) {

		return workflowDefinitionLinkPersistence.countByC_W_W(
			companyId, workflowDefinitionName, workflowDefinitionVersion);
	}

	@Override
	@Transactional(enabled = false)
	public boolean hasWorkflowDefinitionLink(
		long companyId, long groupId, String className) {

		int count =
			workflowDefinitionLinkLocalService.getWorkflowDefinitionLinksCount(
				companyId, StagingUtil.getLiveGroupId(groupId), className);

		if (count > 0) {
			return true;
		}

		count =
			workflowDefinitionLinkLocalService.getWorkflowDefinitionLinksCount(
				companyId, PortalUtil.getSiteGroupId(groupId), className);

		if (count > 0) {
			return true;
		}

		count =
			workflowDefinitionLinkLocalService.getWorkflowDefinitionLinksCount(
				companyId, WorkflowConstants.DEFAULT_GROUP_ID, className);

		if (count > 0) {
			return true;
		}

		return false;
	}

	@Override
	public boolean hasWorkflowDefinitionLink(
		long companyId, long groupId, String className, long classPK) {

		int count = workflowDefinitionLinkPersistence.countByG_C_C_C(
			StagingUtil.getLiveGroupId(groupId), companyId,
			_classNameLocalService.getClassNameId(className), classPK);

		if (count > 0) {
			return true;
		}

		count = workflowDefinitionLinkPersistence.countByG_C_C_C(
			PortalUtil.getSiteGroupId(groupId), companyId,
			_classNameLocalService.getClassNameId(className), classPK);

		if (count > 0) {
			return true;
		}

		count = workflowDefinitionLinkPersistence.countByG_C_C_C(
			WorkflowConstants.DEFAULT_GROUP_ID, companyId,
			_classNameLocalService.getClassNameId(className), classPK);

		if (count > 0) {
			return true;
		}

		return false;
	}

	@Override
	public boolean hasWorkflowDefinitionLink(
		long companyId, long groupId, String className, long classPK,
		long typePK) {

		int count = workflowDefinitionLinkPersistence.countByG_C_C_C_T(
			StagingUtil.getLiveGroupId(groupId), companyId,
			_classNameLocalService.getClassNameId(className), classPK, typePK);

		if (count > 0) {
			return true;
		}

		count = workflowDefinitionLinkPersistence.countByG_C_C_C_T(
			PortalUtil.getSiteGroupId(groupId), companyId,
			_classNameLocalService.getClassNameId(className), classPK, typePK);

		if (count > 0) {
			return true;
		}

		count = workflowDefinitionLinkPersistence.countByG_C_C_C_T(
			WorkflowConstants.DEFAULT_GROUP_ID, companyId,
			_classNameLocalService.getClassNameId(className), classPK, typePK);

		if (count > 0) {
			return true;
		}

		return false;
	}

	@Override
	public void updateWorkflowDefinitionLink(
			long userId, long companyId, long groupId, String className,
			long classPK, long typePK, String workflowDefinition)
		throws PortalException {

		if (Validator.isNull(workflowDefinition)) {
			deleteWorkflowDefinitionLink(
				companyId, groupId, className, classPK, typePK);
		}
		else {
			String[] workflowDefinitionParts = StringUtil.split(
				workflowDefinition, CharPool.AT);

			String workflowDefinitionName = workflowDefinitionParts[0];
			int workflowDefinitionVersion = GetterUtil.getInteger(
				workflowDefinitionParts[1]);

			updateWorkflowDefinitionLink(
				userId, companyId, groupId, className, classPK, typePK,
				workflowDefinitionName, workflowDefinitionVersion);
		}
	}

	@Override
	public WorkflowDefinitionLink updateWorkflowDefinitionLink(
			long userId, long companyId, long groupId, String className,
			long classPK, long typePK, String workflowDefinitionName,
			int workflowDefinitionVersion)
		throws PortalException {

		User user = _userPersistence.findByPrimaryKey(userId);

		WorkflowDefinitionLink workflowDefinitionLink =
			workflowDefinitionLinkPersistence.fetchByG_C_C_C_T(
				StagingUtil.getLiveGroupId(groupId), companyId,
				_classNameLocalService.getClassNameId(className), classPK,
				typePK);

		if (workflowDefinitionLink == null) {
			workflowDefinitionLink = addWorkflowDefinitionLink(
				userId, companyId, StagingUtil.getLiveGroupId(groupId),
				className, classPK, typePK, workflowDefinitionName,
				workflowDefinitionVersion);
		}

		workflowDefinitionLink.setGroupId(StagingUtil.getLiveGroupId(groupId));
		workflowDefinitionLink.setCompanyId(companyId);
		workflowDefinitionLink.setUserId(userId);
		workflowDefinitionLink.setUserName(user.getFullName());
		workflowDefinitionLink.setClassNameId(
			_classNameLocalService.getClassNameId(className));
		workflowDefinitionLink.setClassPK(classPK);
		workflowDefinitionLink.setTypePK(typePK);
		workflowDefinitionLink.setWorkflowDefinitionName(
			workflowDefinitionName);
		workflowDefinitionLink.setWorkflowDefinitionVersion(
			workflowDefinitionVersion);

		return workflowDefinitionLinkPersistence.update(workflowDefinitionLink);
	}

	@Override
	public WorkflowDefinitionLink updateWorkflowDefinitionLink(
			String externalReferenceCode, long userId, long companyId,
			long groupId, String className, long classPK, long typePK,
			String workflowDefinitionName, int workflowDefinitionVersion)
		throws PortalException {

		WorkflowDefinitionLink serviceBuilderWorkflowDefinitionLink =
			workflowDefinitionLinkPersistence.fetchByERC_G(
				externalReferenceCode, groupId);

		if (serviceBuilderWorkflowDefinitionLink == null) {
			return addWorkflowDefinitionLink(
				userId, companyId, StagingUtil.getLiveGroupId(groupId),
				className, classPK, typePK, workflowDefinitionName,
				workflowDefinitionVersion);
		}

		serviceBuilderWorkflowDefinitionLink.setClassName(className);
		serviceBuilderWorkflowDefinitionLink.setWorkflowDefinitionName(
			workflowDefinitionName);

		return workflowDefinitionLinkPersistence.update(
			serviceBuilderWorkflowDefinitionLink);
	}

	@Override
	public void updateWorkflowDefinitionLinks(
			long userId, long companyId, long groupId, String className,
			long classPK,
			List<ObjectValuePair<Long, String>> workflowDefinitionOVPs)
		throws PortalException {

		for (ObjectValuePair<Long, String> workflowDefinitionOVP :
				workflowDefinitionOVPs) {

			long typePK = workflowDefinitionOVP.getKey();
			String workflowDefinitionName = workflowDefinitionOVP.getValue();

			if (Validator.isNull(workflowDefinitionName)) {
				deleteWorkflowDefinitionLink(
					companyId, groupId, className, classPK, typePK);
			}
			else {
				updateWorkflowDefinitionLink(
					userId, companyId, groupId, className, classPK, typePK,
					workflowDefinitionName);
			}
		}
	}

	@BeanReference(type = ClassNameLocalService.class)
	private ClassNameLocalService _classNameLocalService;

	@BeanReference(type = UserPersistence.class)
	private UserPersistence _userPersistence;

}