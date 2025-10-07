/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.workflow.kaleo.service.impl;

import com.liferay.exportimport.kernel.staging.Staging;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.aop.AopService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.search.Indexable;
import com.liferay.portal.kernel.search.IndexableType;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.kernel.workflow.WorkflowException;
import com.liferay.portal.workflow.kaleo.definition.util.WorkflowDefinitionContentUtil;
import com.liferay.portal.workflow.kaleo.model.KaleoDefinition;
import com.liferay.portal.workflow.kaleo.model.KaleoDefinitionVersion;
import com.liferay.portal.workflow.kaleo.service.KaleoConditionLocalService;
import com.liferay.portal.workflow.kaleo.service.KaleoDefinitionVersionLocalService;
import com.liferay.portal.workflow.kaleo.service.KaleoInstanceLocalService;
import com.liferay.portal.workflow.kaleo.service.KaleoNodeLocalService;
import com.liferay.portal.workflow.kaleo.service.KaleoTaskLocalService;
import com.liferay.portal.workflow.kaleo.service.KaleoTransitionLocalService;
import com.liferay.portal.workflow.kaleo.service.base.KaleoDefinitionLocalServiceBaseImpl;
import com.liferay.portal.workflow.kaleo.service.persistence.KaleoDefinitionVersionPersistence;

import java.util.Date;
import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Brian Wing Shun Chan
 */
@Component(
	property = "model.class.name=com.liferay.portal.workflow.kaleo.model.KaleoDefinition",
	service = AopService.class
)
public class KaleoDefinitionLocalServiceImpl
	extends KaleoDefinitionLocalServiceBaseImpl {

	@Indexable(type = IndexableType.REINDEX)
	@Override
	public KaleoDefinition activateKaleoDefinition(
			long kaleoDefinitionId, long kaleoDefinitionVersionId,
			long startKaleoNodeId, ServiceContext serviceContext)
		throws PortalException {

		// Kaleo definition

		KaleoDefinition kaleoDefinition =
			kaleoDefinitionPersistence.findByPrimaryKey(kaleoDefinitionId);

		kaleoDefinition.setModifiedDate(new Date());
		kaleoDefinition.setActive(true);
		kaleoDefinition.setStatus(WorkflowConstants.STATUS_APPROVED);

		kaleoDefinition = kaleoDefinitionPersistence.update(kaleoDefinition);

		// Kaleo definition version

		KaleoDefinitionVersion kaleoDefinitionVersion =
			_kaleoDefinitionVersionPersistence.findByPrimaryKey(
				kaleoDefinitionVersionId);

		kaleoDefinitionVersion.setModifiedDate(new Date());
		kaleoDefinitionVersion.setStartKaleoNodeId(startKaleoNodeId);

		_kaleoDefinitionVersionPersistence.update(kaleoDefinitionVersion);

		return kaleoDefinition;
	}

	@Indexable(type = IndexableType.REINDEX)
	@Override
	public KaleoDefinition activateKaleoDefinition(
			long kaleoDefinitionId, ServiceContext serviceContext)
		throws PortalException {

		// Kaleo definition

		KaleoDefinition kaleoDefinition =
			kaleoDefinitionPersistence.findByPrimaryKey(kaleoDefinitionId);

		kaleoDefinition.setModifiedDate(new Date());
		kaleoDefinition.setActive(true);
		kaleoDefinition.setStatus(WorkflowConstants.STATUS_APPROVED);

		return kaleoDefinitionPersistence.update(kaleoDefinition);
	}

	@Indexable(type = IndexableType.REINDEX)
	@Override
	public KaleoDefinition activateKaleoDefinition(
			String name, int version, ServiceContext serviceContext)
		throws PortalException {

		// Kaleo definition

		KaleoDefinition kaleoDefinition =
			kaleoDefinitionPersistence.findByC_N_V(
				serviceContext.getCompanyId(), name, version);

		kaleoDefinition.setModifiedDate(new Date());
		kaleoDefinition.setActive(true);
		kaleoDefinition.setStatus(WorkflowConstants.STATUS_APPROVED);

		return kaleoDefinitionPersistence.update(kaleoDefinition);
	}

	@Override
	public KaleoDefinition addKaleoDefinition(
			String externalReferenceCode, String name, String title,
			String description, String content, String scope, int version,
			ServiceContext serviceContext)
		throws PortalException {

		// Kaleo definition

		long kaleoDefinitionId = counterLocalService.increment();

		KaleoDefinition kaleoDefinition = kaleoDefinitionPersistence.create(
			kaleoDefinitionId);

		kaleoDefinition.setExternalReferenceCode(externalReferenceCode);
		kaleoDefinition.setGroupId(
			_staging.getLiveGroupId(serviceContext.getScopeGroupId()));

		User user = _userLocalService.getUser(
			serviceContext.getGuestOrUserId());

		kaleoDefinition.setCompanyId(user.getCompanyId());
		kaleoDefinition.setUserId(user.getUserId());
		kaleoDefinition.setUserName(user.getFullName());

		Date date = new Date();

		kaleoDefinition.setCreateDate(date);
		kaleoDefinition.setModifiedDate(date);

		kaleoDefinition.setName(name);
		kaleoDefinition.setTitle(title);
		kaleoDefinition.setDescription(description);

		content = WorkflowDefinitionContentUtil.toJSON(content);

		kaleoDefinition.setContent(content);

		kaleoDefinition.setScope(scope);
		kaleoDefinition.setVersion(version);
		kaleoDefinition.setActive(false);
		kaleoDefinition.setStatus(WorkflowConstants.STATUS_DRAFT);

		kaleoDefinition = kaleoDefinitionPersistence.update(kaleoDefinition);

		// Kaleo definition version

		_kaleoDefinitionVersionLocalService.addKaleoDefinitionVersion(
			kaleoDefinitionId, name, title, description, content,
			_getVersion(version), serviceContext);

		return kaleoDefinition;
	}

	@Indexable(type = IndexableType.REINDEX)
	@Override
	public KaleoDefinition deactivateKaleoDefinition(
			String name, int version, ServiceContext serviceContext)
		throws PortalException {

		KaleoDefinition kaleoDefinition =
			kaleoDefinitionPersistence.findByC_N_V(
				serviceContext.getCompanyId(), name, version);

		kaleoDefinition.setModifiedDate(new Date());
		kaleoDefinition.setActive(false);
		kaleoDefinition.setStatus(WorkflowConstants.STATUS_DRAFT);

		return kaleoDefinitionPersistence.update(kaleoDefinition);
	}

	@Override
	public void deleteCompanyKaleoDefinitions(long companyId) {

		// Kaleo definitions

		kaleoDefinitionPersistence.removeByCompanyId(companyId);

		// Kaleo definition version

		_kaleoDefinitionVersionPersistence.removeByCompanyId(companyId);

		// Kaleo condition

		_kaleoConditionLocalService.deleteCompanyKaleoConditions(companyId);

		// Kaleo instances

		_kaleoInstanceLocalService.deleteCompanyKaleoInstances(companyId);

		// Kaleo nodes

		_kaleoNodeLocalService.deleteCompanyKaleoNodes(companyId);

		// Kaleo tasks

		_kaleoTaskLocalService.deleteCompanyKaleoTasks(companyId);

		// Kaleo transitions

		_kaleoTransitionLocalService.deleteCompanyKaleoTransitions(companyId);
	}

	@Override
	public void deleteKaleoDefinition(
			String name, ServiceContext serviceContext)
		throws PortalException {

		// Kaleo definition

		KaleoDefinition kaleoDefinition = getKaleoDefinition(
			name, serviceContext);

		if (kaleoDefinition.isActive()) {
			throw new WorkflowException(
				"Cannot delete active workflow definition " +
					kaleoDefinition.getKaleoDefinitionId());
		}

		kaleoDefinitionPersistence.remove(kaleoDefinition);

		// Kaleo definition version

		_kaleoDefinitionVersionLocalService.deleteKaleoDefinitionVersions(
			kaleoDefinition);
	}

	@Override
	public KaleoDefinition fetchKaleoDefinition(
		String name, ServiceContext serviceContext) {

		return kaleoDefinitionPersistence.fetchByC_N(
			serviceContext.getCompanyId(), name);
	}

	@Override
	public KaleoDefinition getKaleoDefinition(
			String name, ServiceContext serviceContext)
		throws PortalException {

		return kaleoDefinitionPersistence.findByC_N(
			serviceContext.getCompanyId(), name);
	}

	@Override
	public List<KaleoDefinition> getKaleoDefinitions(
		boolean active, int start, int end) {

		return kaleoDefinitionPersistence.findByActive(true, start, end);
	}

	@Override
	public List<KaleoDefinition> getKaleoDefinitions(
		boolean active, int start, int end,
		OrderByComparator<KaleoDefinition> orderByComparator,
		ServiceContext serviceContext) {

		return kaleoDefinitionPersistence.findByC_A(
			serviceContext.getCompanyId(), active, start, end,
			orderByComparator);
	}

	@Override
	public List<KaleoDefinition> getKaleoDefinitions(
		int start, int end,
		OrderByComparator<KaleoDefinition> orderByComparator,
		ServiceContext serviceContext) {

		return kaleoDefinitionPersistence.findByCompanyId(
			serviceContext.getCompanyId(), start, end, orderByComparator);
	}

	@Override
	public int getKaleoDefinitionsCount(
		boolean active, ServiceContext serviceContext) {

		return kaleoDefinitionPersistence.countByC_A(
			serviceContext.getCompanyId(), active);
	}

	@Override
	public int getKaleoDefinitionsCount(ServiceContext serviceContext) {
		return kaleoDefinitionPersistence.countByCompanyId(
			serviceContext.getCompanyId());
	}

	@Override
	public int getKaleoDefinitionsCount(
		String name, boolean active, ServiceContext serviceContext) {

		return kaleoDefinitionPersistence.countByC_N_A(
			serviceContext.getCompanyId(), name, active);
	}

	@Override
	public int getKaleoDefinitionsCount(
		String name, ServiceContext serviceContext) {

		return kaleoDefinitionPersistence.countByC_N(
			serviceContext.getCompanyId(), name);
	}

	@Override
	public List<KaleoDefinition> getScopeKaleoDefinitions(
		String scope, boolean active, int start, int end,
		OrderByComparator<KaleoDefinition> orderByComparator,
		ServiceContext serviceContext) {

		return kaleoDefinitionPersistence.findByC_S_A(
			serviceContext.getCompanyId(), scope, active, start, end,
			orderByComparator);
	}

	@Override
	public List<KaleoDefinition> getScopeKaleoDefinitions(
		String scope, int start, int end,
		OrderByComparator<KaleoDefinition> orderByComparator,
		ServiceContext serviceContext) {

		return kaleoDefinitionPersistence.findByC_S(
			serviceContext.getCompanyId(), scope, start, end,
			orderByComparator);
	}

	@Override
	public int getScopeKaleoDefinitionsCount(
		String scope, boolean active, ServiceContext serviceContext) {

		return kaleoDefinitionPersistence.countByC_S_A(
			serviceContext.getCompanyId(), scope, active);
	}

	@Override
	public int getScopeKaleoDefinitionsCount(
		String scope, ServiceContext serviceContext) {

		return kaleoDefinitionPersistence.countByC_S(
			serviceContext.getCompanyId(), scope);
	}

	@Override
	public KaleoDefinition updatedKaleoDefinition(
			String externalReferenceCode, long kaleoDefinitionId, String title,
			String description, String content, ServiceContext serviceContext)
		throws PortalException {

		// Kaleo definition

		User user = _userLocalService.getUser(
			serviceContext.getGuestOrUserId());
		Date date = new Date();

		KaleoDefinition kaleoDefinition =
			kaleoDefinitionPersistence.findByPrimaryKey(kaleoDefinitionId);

		kaleoDefinition.setExternalReferenceCode(externalReferenceCode);
		kaleoDefinition.setGroupId(
			_staging.getLiveGroupId(serviceContext.getScopeGroupId()));
		kaleoDefinition.setUserId(user.getUserId());
		kaleoDefinition.setUserName(user.getFullName());
		kaleoDefinition.setCreateDate(date);
		kaleoDefinition.setModifiedDate(date);
		kaleoDefinition.setTitle(title);
		kaleoDefinition.setDescription(description);

		content = WorkflowDefinitionContentUtil.toJSON(content);

		kaleoDefinition.setContent(content);

		int nextVersion = kaleoDefinition.getVersion() + 1;

		kaleoDefinition.setVersion(nextVersion);

		kaleoDefinition.setActive(false);

		kaleoDefinition = kaleoDefinitionPersistence.update(kaleoDefinition);

		// Kaleo definition version

		_kaleoDefinitionVersionLocalService.addKaleoDefinitionVersion(
			kaleoDefinitionId, kaleoDefinition.getName(), title, description,
			content, _getVersion(nextVersion), serviceContext);

		return kaleoDefinition;
	}

	private String _getVersion(int version) {
		return version + StringPool.PERIOD + 0;
	}

	@Reference
	private KaleoConditionLocalService _kaleoConditionLocalService;

	@Reference
	private KaleoDefinitionVersionLocalService
		_kaleoDefinitionVersionLocalService;

	@Reference
	private KaleoDefinitionVersionPersistence
		_kaleoDefinitionVersionPersistence;

	@Reference
	private KaleoInstanceLocalService _kaleoInstanceLocalService;

	@Reference
	private KaleoNodeLocalService _kaleoNodeLocalService;

	@Reference
	private KaleoTaskLocalService _kaleoTaskLocalService;

	@Reference
	private KaleoTransitionLocalService _kaleoTransitionLocalService;

	@Reference
	private Staging _staging;

	@Reference
	private UserLocalService _userLocalService;

}