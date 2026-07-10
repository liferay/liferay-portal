/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.batch.engine.internal.unit;

import com.liferay.batch.engine.internal.bundle.CompanyBatchEngineUnitWrapper;
import com.liferay.batch.engine.unit.BatchEngineUnit;
import com.liferay.batch.engine.unit.BatchEngineUnitProcessor;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.service.CompanyLocalService;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.osgi.framework.Bundle;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Alejandro Tardín
 */
@Component(service = MultiCompanyBatchEngineUnitProcessor.class)
public class MultiCompanyBatchEngineUnitProcessor {

	public void processBatchEngineUnits(Company company) throws Exception {
		Exception exception1 = null;

		for (Bundle bundle : _bundleBatchEngineUnits.keySet()) {
			try {
				_processBatchEngineUnits(bundle, company);
			}
			catch (Exception exception2) {
				if (exception1 == null) {
					exception1 = new Exception(
						"Unable to process batch engine units for company " +
							company.getCompanyId());
				}

				exception1.addSuppressed(exception2);
			}
		}

		if (exception1 != null) {
			throw exception1;
		}
	}

	public void registerBatchEngineUnits(
		Bundle bundle, List<BatchEngineUnit> batchEngineUnits) {

		_bundleBatchEngineUnits.put(bundle, batchEngineUnits);

		_companyLocalService.forEachCompany(
			company -> {
				try {
					_processBatchEngineUnits(bundle, company);
				}
				catch (Exception exception) {
					_log.error(
						StringBundler.concat(
							"Unable to process batch engine units of bundle ",
							bundle.getSymbolicName(), " for company ",
							company.getCompanyId()),
						exception);
				}
			});
	}

	public void unregister(Bundle bundle) {
		_bundleBatchEngineUnits.remove(bundle);
		_bundleProcessedCompanies.remove(bundle);
	}

	public void unregister(Company company) {
		for (Set<Long> companyIds : _bundleProcessedCompanies.values()) {
			companyIds.remove(company.getCompanyId());
		}
	}

	@Deactivate
	protected void deactivate() {
		_bundleBatchEngineUnits.clear();
		_bundleProcessedCompanies.clear();
	}

	private void _processBatchEngineUnits(Bundle bundle, Company company)
		throws Exception {

		Set<Long> companyIds = _bundleProcessedCompanies.computeIfAbsent(
			bundle, key -> ConcurrentHashMap.newKeySet());

		if (!companyIds.add(company.getCompanyId())) {
			return;
		}

		_batchEngineUnitProcessor.processBatchEngineUnits(
			TransformUtil.transform(
				_bundleBatchEngineUnits.get(bundle),
				batchEngineUnit -> new CompanyBatchEngineUnitWrapper(
					batchEngineUnit, company)));
	}

	private static final Log _log = LogFactoryUtil.getLog(
		MultiCompanyBatchEngineUnitProcessor.class);

	@Reference
	private BatchEngineUnitProcessor _batchEngineUnitProcessor;

	private final Map<Bundle, List<BatchEngineUnit>> _bundleBatchEngineUnits =
		new ConcurrentHashMap<>();
	private final Map<Bundle, Set<Long>> _bundleProcessedCompanies =
		new ConcurrentHashMap<>();

	@Reference
	private CompanyLocalService _companyLocalService;

}