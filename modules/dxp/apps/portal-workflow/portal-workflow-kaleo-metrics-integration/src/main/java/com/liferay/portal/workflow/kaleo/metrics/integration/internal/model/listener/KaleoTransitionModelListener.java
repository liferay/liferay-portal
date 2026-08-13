/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.workflow.kaleo.metrics.integration.internal.model.listener;

import com.liferay.portal.kernel.exception.ModelListenerException;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.ModelListener;
import com.liferay.portal.workflow.kaleo.metrics.integration.helper.IndexerHelper;
import com.liferay.portal.workflow.kaleo.model.KaleoDefinitionVersion;
import com.liferay.portal.workflow.kaleo.model.KaleoTransition;
import com.liferay.portal.workflow.metrics.search.index.TransitionWorkflowMetricsIndexer;

import java.util.Objects;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Inácio Nery
 */
@Component(service = ModelListener.class)
public class KaleoTransitionModelListener
	extends BaseKaleoModelListener<KaleoTransition> {

	@Override
	public void onAfterCreate(KaleoTransition kaleoTransition) {
		KaleoDefinitionVersion kaleoDefinitionVersion =
			getKaleoDefinitionVersion(
				kaleoTransition.getKaleoDefinitionVersionId());

		if (Objects.isNull(kaleoDefinitionVersion)) {
			return;
		}

		try {
			_transitionWorkflowMetricsIndexer.addTransition(
				_indexerHelper.createAddTransitionRequest(
					kaleoTransition, kaleoDefinitionVersion.getVersion()));
		}
		catch (PortalException portalException) {
			throw new ModelListenerException(portalException);
		}
	}

	@Override
	public void onAfterRemove(KaleoTransition kaleoTransition) {
		_transitionWorkflowMetricsIndexer.deleteTransition(
			_indexerHelper.createDeleteTransitionRequest(kaleoTransition));
	}

	@Reference
	private IndexerHelper _indexerHelper;

	@Reference
	private TransitionWorkflowMetricsIndexer _transitionWorkflowMetricsIndexer;

}