/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.whip.coveragedata;

import java.io.Serializable;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author Shuyang Zhou
 */
public abstract class CoverageDataContainer
	<K, V extends CoverageData<V>, T extends CoverageDataContainer<K, V, T>>
		implements CoverageData<T>, Serializable {

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof CoverageDataContainer)) {
			return false;
		}

		CoverageDataContainer<K, V, T> coverageDataContainer =
			(CoverageDataContainer<K, V, T>)object;

		return children.equals(coverageDataContainer.children);
	}

	@Override
	public double getBranchCoverageRate() {
		int numberOfCoveredBranches = 0;
		int numberOfValidBranches = 0;

		for (CoverageData<V> coverageData : children.values()) {
			numberOfCoveredBranches +=
				coverageData.getNumberOfCoveredBranches();
			numberOfValidBranches += coverageData.getNumberOfValidBranches();
		}

		if (numberOfValidBranches == 0) {
			return 1D;
		}

		return (double)numberOfCoveredBranches / numberOfValidBranches;
	}

	@Override
	public double getLineCoverageRate() {
		int numberOfCoveredLines = 0;
		int numberOfValidLines = 0;

		for (CoverageData<V> coverageData : children.values()) {
			numberOfCoveredLines += coverageData.getNumberOfCoveredLines();
			numberOfValidLines += coverageData.getNumberOfValidLines();
		}

		if (numberOfValidLines == 0) {
			return 1D;
		}

		return (double)numberOfCoveredLines / numberOfValidLines;
	}

	@Override
	public int getNumberOfCoveredBranches() {
		int numberOfCoveredBranches = 0;

		for (CoverageData<V> coverageData : children.values()) {
			numberOfCoveredBranches +=
				coverageData.getNumberOfCoveredBranches();
		}

		return numberOfCoveredBranches;
	}

	@Override
	public int getNumberOfCoveredLines() {
		int numberOfCoveredLines = 0;

		for (CoverageData<V> coverageData : children.values()) {
			numberOfCoveredLines += coverageData.getNumberOfCoveredLines();
		}

		return numberOfCoveredLines;
	}

	@Override
	public int getNumberOfValidBranches() {
		int numberOfValidBranches = 0;

		for (CoverageData<V> coverageData : children.values()) {
			numberOfValidBranches += coverageData.getNumberOfValidBranches();
		}

		return numberOfValidBranches;
	}

	@Override
	public int getNumberOfValidLines() {
		int numberOfValidLines = 0;

		for (CoverageData<V> coverageData : children.values()) {
			numberOfValidLines += coverageData.getNumberOfValidLines();
		}

		return numberOfValidLines;
	}

	@Override
	public int hashCode() {
		return children.hashCode();
	}

	@Override
	public void merge(T otherCoverageDataContainer) {
		Map<K, V> otherChildren = otherCoverageDataContainer.children;

		for (Map.Entry<K, V> entry : otherChildren.entrySet()) {
			V otherChildCoverageData = entry.getValue();

			V myChildCoverageData = children.putIfAbsent(
				entry.getKey(), otherChildCoverageData);

			if (myChildCoverageData == null) {
				continue;
			}

			myChildCoverageData.merge(otherChildCoverageData);
		}
	}

	protected final ConcurrentMap<K, V> children = new ConcurrentHashMap<>();

	private static final long serialVersionUID = 1;

}