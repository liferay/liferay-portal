/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.change.tracking.service.impl;

import com.liferay.change.tracking.internal.background.task.CTPublishBackgroundTaskExecutor;
import com.liferay.change.tracking.model.CTCollection;
import com.liferay.change.tracking.model.CTProcess;
import com.liferay.change.tracking.service.CTCollectionLocalService;
import com.liferay.change.tracking.service.CTPreferencesLocalService;
import com.liferay.change.tracking.service.base.CTProcessLocalServiceBaseImpl;
import com.liferay.change.tracking.service.persistence.CTCollectionPersistence;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.portal.aop.AopService;
import com.liferay.portal.background.task.model.BackgroundTask;
import com.liferay.portal.background.task.service.BackgroundTaskLocalService;
import com.liferay.portal.kernel.backgroundtask.constants.BackgroundTaskConstants;
import com.liferay.portal.kernel.change.tracking.CTAware;
import com.liferay.portal.kernel.change.tracking.CTCollectionThreadLocal;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.search.Indexable;
import com.liferay.portal.kernel.search.IndexableType;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.ResourceLocalService;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.workflow.WorkflowConstants;

import java.io.Serializable;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Daniel Kocsis
 * @author Preston Crary
 */
@Component(
	property = "model.class.name=com.liferay.change.tracking.model.CTProcess",
	service = AopService.class
)
@CTAware(onProduction = true)
public class CTProcessLocalServiceImpl extends CTProcessLocalServiceBaseImpl {

	@Indexable(type = IndexableType.REINDEX)
	@Override
	public CTProcess addCTProcess(long userId, long ctCollectionId)
		throws PortalException {

		CTCollection ctCollection = _ctCollectionPersistence.findByPrimaryKey(
			ctCollectionId);

		if (ctCollection.getStatus() == WorkflowConstants.STATUS_APPROVED) {
			throw new IllegalStateException(
				"Change tracking collection is already published " +
					ctCollection);
		}

		if (ctCollection.isEmpty()) {
			throw new IllegalStateException(
				"Change tracking collection is empty " + ctCollection);
		}

		ctCollection.setStatus(WorkflowConstants.STATUS_PENDING);

		ctCollection = _ctCollectionLocalService.updateCTCollection(
			ctCollection);

		if (!FeatureFlagManagerUtil.isEnabled(
				ctCollection.getCompanyId(), "LPD-39203")) {

			_ctPreferencesLocalService.resetCTPreferences(
				ctCollection.getCtCollectionId());
		}

		long ctProcessId = counterLocalService.increment(
			CTProcess.class.getName());

		CTProcess ctProcess = ctProcessPersistence.create(ctProcessId);

		ctProcess.setCompanyId(ctCollection.getCompanyId());
		ctProcess.setUserId(userId);
		ctProcess.setCreateDate(new Date());
		ctProcess.setCtCollectionId(ctCollectionId);

		Company company = _companyLocalService.getCompany(
			ctCollection.getCompanyId());

		Map<String, Serializable> taskContextMap =
			HashMapBuilder.<String, Serializable>put(
				"ctCollectionId", ctCollectionId
			).put(
				"ctProcessId", ctProcessId
			).build();

		try (SafeCloseable safeCloseable =
				CTCollectionThreadLocal.setProductionModeWithSafeCloseable()) {

			BackgroundTask backgroundTask =
				_backgroundTaskLocalService.addBackgroundTask(
					userId, company.getGroupId(),
					String.valueOf(ctCollectionId), null,
					CTPublishBackgroundTaskExecutor.class, taskContextMap,
					null);

			ctProcess.setBackgroundTaskId(backgroundTask.getBackgroundTaskId());
		}

		ctProcess = ctProcessPersistence.update(ctProcess);

		_resourceLocalService.addResources(
			ctProcess.getCompanyId(), 0, ctProcess.getUserId(),
			CTProcess.class.getName(), ctProcess.getCtProcessId(), false, false,
			false);

		return ctProcess;
	}

	@Indexable(type = IndexableType.DELETE)
	@Override
	public CTProcess deleteCTProcess(CTProcess ctProcess)
		throws PortalException {

		ctProcessPersistence.remove(ctProcess);

		_resourceLocalService.deleteResource(
			ctProcess.getCompanyId(), CTProcess.class.getName(),
			ResourceConstants.SCOPE_INDIVIDUAL, ctProcess.getCtProcessId());

		BackgroundTask backgroundTask =
			_backgroundTaskLocalService.fetchBackgroundTask(
				ctProcess.getBackgroundTaskId());

		if (backgroundTask != null) {
			if (backgroundTask.getStatus() ==
					BackgroundTaskConstants.STATUS_SUCCESSFUL) {

				CTCollection ctCollection =
					_ctCollectionPersistence.fetchByPrimaryKey(
						ctProcess.getCtCollectionId());

				if (ctCollection != null) {
					_ctCollectionLocalService.deleteCTCollection(ctCollection);
				}
			}

			_backgroundTaskLocalService.deleteBackgroundTask(backgroundTask);
		}

		return ctProcess;
	}

	@Indexable(type = IndexableType.DELETE)
	@Override
	public CTProcess deleteCTProcess(long ctProcessId) throws PortalException {
		CTProcess ctProcess = ctProcessPersistence.findByPrimaryKey(
			ctProcessId);

		return deleteCTProcess(ctProcess);
	}

	@Override
	public CTProcess fetchLatestCTProcess(long companyId) {
		return ctProcessPersistence.fetchByCompanyId_First(companyId, null);
	}

	@Override
	public List<CTProcess> getCTProcesses(long ctCollectionId) {
		return ctProcessPersistence.findByCtCollectionId(ctCollectionId);
	}

	@Reference
	private BackgroundTaskLocalService _backgroundTaskLocalService;

	@Reference
	private CompanyLocalService _companyLocalService;

	@Reference
	private CTCollectionLocalService _ctCollectionLocalService;

	@Reference
	private CTCollectionPersistence _ctCollectionPersistence;

	@Reference
	private CTPreferencesLocalService _ctPreferencesLocalService;

	@Reference
	private ResourceLocalService _resourceLocalService;

}