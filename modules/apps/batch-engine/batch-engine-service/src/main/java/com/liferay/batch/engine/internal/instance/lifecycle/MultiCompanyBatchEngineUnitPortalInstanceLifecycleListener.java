/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.batch.engine.internal.instance.lifecycle;

import com.liferay.batch.engine.internal.unit.MultiCompanyBatchEngineUnitProcessor;
import com.liferay.portal.instance.lifecycle.BasePortalInstanceLifecycleListener;
import com.liferay.portal.instance.lifecycle.PortalInstanceLifecycleListener;
import com.liferay.portal.kernel.dao.db.DB;
import com.liferay.portal.kernel.dao.db.DBManagerUtil;
import com.liferay.portal.kernel.dao.db.DBType;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.transaction.TransactionCallbackUtil;

import java.util.concurrent.CompletableFuture;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Alejandro Tardín
 */
@Component(
	property = "service.ranking:Integer=" + Integer.MIN_VALUE,
	service = PortalInstanceLifecycleListener.class
)
public class MultiCompanyBatchEngineUnitPortalInstanceLifecycleListener
	extends BasePortalInstanceLifecycleListener {

	@Override
	public void portalInstanceRegistered(Company company) throws Exception {
		DB db = DBManagerUtil.getDB();

		if (db.getDBType() == DBType.HYPERSONIC) {
			TransactionCallbackUtil.registerCommitCallback(
				() -> _processBatchEngineUnits(company));
		}
		else {
			_processBatchEngineUnits(company);
		}
	}

	@Override
	public void portalInstanceUnregistered(Company company) {
		_multiCompanyBatchEngineUnitProcessor.unregister(company);
	}

	private Void _processBatchEngineUnits(Company company) throws Exception {
		CompletableFuture<Void> completableFuture =
			_multiCompanyBatchEngineUnitProcessor.processBatchEngineUnits(
				company);

		return completableFuture.get();
	}

	@Reference
	private MultiCompanyBatchEngineUnitProcessor
		_multiCompanyBatchEngineUnitProcessor;

}