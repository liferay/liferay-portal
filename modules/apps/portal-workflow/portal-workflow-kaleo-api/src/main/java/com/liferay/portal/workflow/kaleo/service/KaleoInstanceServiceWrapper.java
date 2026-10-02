/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.workflow.kaleo.service;

import com.liferay.portal.kernel.service.ServiceWrapper;
import com.liferay.portal.workflow.kaleo.model.KaleoInstance;

/**
 * Provides a wrapper for {@link KaleoInstanceService}.
 *
 * @author Brian Wing Shun Chan
 * @see KaleoInstanceService
 * @generated
 */
public class KaleoInstanceServiceWrapper
	implements KaleoInstanceService, ServiceWrapper<KaleoInstanceService> {

	public KaleoInstanceServiceWrapper() {
		this(null);
	}

	public KaleoInstanceServiceWrapper(
		KaleoInstanceService kaleoInstanceService) {

		_kaleoInstanceService = kaleoInstanceService;
	}

	@Override
	public KaleoInstance addKaleoInstance(
			String kaleoDefinitionName, Integer kaleoDefinitionVersion,
			String transitionName,
			java.util.Map<String, java.io.Serializable> workflowContext,
			com.liferay.portal.kernel.service.ServiceContext serviceContext,
			boolean waitForCompletion)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _kaleoInstanceService.addKaleoInstance(
			kaleoDefinitionName, kaleoDefinitionVersion, transitionName,
			workflowContext, serviceContext, waitForCompletion);
	}

	@Override
	public KaleoInstance getKaleoInstance(long kaleoInstanceId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _kaleoInstanceService.getKaleoInstance(kaleoInstanceId);
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	@Override
	public String getOSGiServiceIdentifier() {
		return _kaleoInstanceService.getOSGiServiceIdentifier();
	}

	@Override
	public KaleoInstance updateKaleoInstance(
			long kaleoInstanceId,
			java.util.Map<String, java.io.Serializable> workflowContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _kaleoInstanceService.updateKaleoInstance(
			kaleoInstanceId, workflowContext);
	}

	@Override
	public KaleoInstanceService getWrappedService() {
		return _kaleoInstanceService;
	}

	@Override
	public void setWrappedService(KaleoInstanceService kaleoInstanceService) {
		_kaleoInstanceService = kaleoInstanceService;
	}

	private KaleoInstanceService _kaleoInstanceService;

}
// LIFERAY-SERVICE-BUILDER-HASH:608162959