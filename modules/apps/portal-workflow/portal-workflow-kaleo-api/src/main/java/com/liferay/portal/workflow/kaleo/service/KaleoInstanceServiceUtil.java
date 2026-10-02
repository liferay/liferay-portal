/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.workflow.kaleo.service;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.module.service.Snapshot;
import com.liferay.portal.workflow.kaleo.model.KaleoInstance;

import java.io.Serializable;

import java.util.Map;

/**
 * Provides the remote service utility for KaleoInstance. This utility wraps
 * <code>com.liferay.portal.workflow.kaleo.service.impl.KaleoInstanceServiceImpl</code> and is an
 * access point for service operations in application layer code running on a
 * remote server. Methods of this service are expected to have security checks
 * based on the propagated JAAS credentials because this service can be
 * accessed remotely.
 *
 * @author Brian Wing Shun Chan
 * @see KaleoInstanceService
 * @generated
 */
public class KaleoInstanceServiceUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Add custom service methods to <code>com.liferay.portal.workflow.kaleo.service.impl.KaleoInstanceServiceImpl</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static KaleoInstance addKaleoInstance(
			String kaleoDefinitionName, Integer kaleoDefinitionVersion,
			String transitionName, Map<String, Serializable> workflowContext,
			com.liferay.portal.kernel.service.ServiceContext serviceContext,
			boolean waitForCompletion)
		throws PortalException {

		return getService().addKaleoInstance(
			kaleoDefinitionName, kaleoDefinitionVersion, transitionName,
			workflowContext, serviceContext, waitForCompletion);
	}

	public static KaleoInstance getKaleoInstance(long kaleoInstanceId)
		throws PortalException {

		return getService().getKaleoInstance(kaleoInstanceId);
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	public static String getOSGiServiceIdentifier() {
		return getService().getOSGiServiceIdentifier();
	}

	public static KaleoInstance updateKaleoInstance(
			long kaleoInstanceId, Map<String, Serializable> workflowContext)
		throws PortalException {

		return getService().updateKaleoInstance(
			kaleoInstanceId, workflowContext);
	}

	public static KaleoInstanceService getService() {
		return _serviceSnapshot.get();
	}

	private static final Snapshot<KaleoInstanceService> _serviceSnapshot =
		new Snapshot<>(
			KaleoInstanceServiceUtil.class, KaleoInstanceService.class);

}
// LIFERAY-SERVICE-BUILDER-HASH:1225747297