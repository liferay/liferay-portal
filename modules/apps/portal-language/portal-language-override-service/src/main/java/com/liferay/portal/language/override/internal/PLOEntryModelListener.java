/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.language.override.internal;

import com.liferay.portal.kernel.cluster.ClusterExecutor;
import com.liferay.portal.kernel.cluster.ClusterInvokeThreadLocal;
import com.liferay.portal.kernel.cluster.ClusterRequest;
import com.liferay.portal.kernel.model.BaseModelListener;
import com.liferay.portal.kernel.model.ModelListener;
import com.liferay.portal.kernel.transaction.TransactionCallbackUtil;
import com.liferay.portal.kernel.util.MethodHandler;
import com.liferay.portal.kernel.util.MethodKey;
import com.liferay.portal.language.override.model.PLOEntry;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Drew Brokke
 */
@Component(service = ModelListener.class)
public class PLOEntryModelListener extends BaseModelListener<PLOEntry> {

	public static void clearCache(ClusterExecutor clusterExecutor) {
		PLOOverrideResourceBundleManager.clearCache();

		if (!clusterExecutor.isEnabled() ||
			!ClusterInvokeThreadLocal.isEnabled()) {

			return;
		}

		ClusterRequest clusterRequest = ClusterRequest.createMulticastRequest(
			_clearCacheMethodHandle, true);

		clusterRequest.setFireAndForget(true);

		TransactionCallbackUtil.registerCommitCallback(
			() -> clusterExecutor.execute(clusterRequest));
	}

	@Override
	public void onAfterCreate(PLOEntry ploEntry) {
		clearCache(_clusterExecutor);
	}

	@Override
	public void onAfterRemove(PLOEntry ploEntry) {
		clearCache(_clusterExecutor);
	}

	@Override
	public void onAfterUpdate(PLOEntry originalPLOEntry, PLOEntry ploEntry) {
		clearCache(_clusterExecutor);
	}

	private static final MethodHandler _clearCacheMethodHandle =
		new MethodHandler(
			new MethodKey(
				PLOOverrideResourceBundleManager.class, "clearCache"));

	@Reference
	private ClusterExecutor _clusterExecutor;

}