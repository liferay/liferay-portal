/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.workflow.kaleo.runtime;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.workflow.kaleo.model.KaleoNode;

import org.osgi.annotation.versioning.ProviderType;

/**
 * @author Michael C. Han
 */
@ProviderType
public interface KaleoSignaler {

	public void signalEntry(
			String transitionName, ExecutionContext executionContext)
		throws PortalException;

	public default void signalEntry(
			String transitionName, ExecutionContext executionContext,
			boolean waitForCompletion)
		throws PortalException {

		if (waitForCompletion) {
			throw new UnsupportedOperationException();
		}

		signalEntry(transitionName, executionContext);
	}

	public void signalExecute(
			KaleoNode currentKaleoNode, ExecutionContext executionContext)
		throws PortalException;

	public void signalExit(
			String transitionName, ExecutionContext executionContext)
		throws PortalException;

	public default void signalExit(
			String transitionName, ExecutionContext executionContext,
			boolean waitForCompletion)
		throws PortalException {

		if (waitForCompletion) {
			throw new UnsupportedOperationException();
		}

		signalExit(transitionName, executionContext);
	}

}