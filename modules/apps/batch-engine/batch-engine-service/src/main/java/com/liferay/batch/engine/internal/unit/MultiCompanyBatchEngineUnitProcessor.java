/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.batch.engine.internal.unit;

import com.liferay.batch.engine.internal.bundle.CompanyBatchEngineUnitWrapper;
import com.liferay.batch.engine.unit.BatchEngineUnit;
import com.liferay.batch.engine.unit.BatchEngineUnitProcessor;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.service.CompanyLocalService;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
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

	public CompletableFuture<Void> processBatchEngineUnits(Company company) {
		List<CompletableFuture<Void>> completableFutures = new ArrayList<>();

		for (Bundle bundle : _bundleBatchEngineUnits.keySet()) {
			completableFutures.add(_processBatchEngineUnits(bundle, company));
		}

		return CompletableFuture.allOf(
			completableFutures.toArray(new CompletableFuture[0]));
	}

	public void registerBatchEngineUnits(
		Bundle bundle, List<BatchEngineUnit> batchEngineUnits) {

		_bundleBatchEngineUnits.put(bundle, batchEngineUnits);

		_companyLocalService.forEachCompany(
			company -> _processBatchEngineUnits(bundle, company));
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

	private CompletableFuture<Void> _processBatchEngineUnits(
		Bundle bundle, Company company) {

		Set<Long> companyIds = _bundleProcessedCompanies.computeIfAbsent(
			bundle, key -> ConcurrentHashMap.newKeySet());

		if (!companyIds.add(company.getCompanyId())) {
			return CompletableFuture.completedFuture(null);
		}

		return _batchEngineUnitProcessor.processBatchEngineUnits(
			TransformUtil.transform(
				_bundleBatchEngineUnits.get(bundle),
				batchEngineUnit -> new CompanyBatchEngineUnitWrapper(
					batchEngineUnit, company)));
	}

	@Reference
	private BatchEngineUnitProcessor _batchEngineUnitProcessor;

	private final Map<Bundle, List<BatchEngineUnit>> _bundleBatchEngineUnits =
		new ConcurrentHashMap<>();
	private final Map<Bundle, Set<Long>> _bundleProcessedCompanies =
		new ConcurrentHashMap<>();

	@Reference
	private CompanyLocalService _companyLocalService;

}