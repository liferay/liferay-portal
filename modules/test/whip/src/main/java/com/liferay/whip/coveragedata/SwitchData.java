/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.whip.coveragedata;

import java.io.Serializable;

import java.util.concurrent.atomic.AtomicLongArray;

/**
 * @author Shuyang Zhou
 */
public class SwitchData
	implements BranchCoverageData<SwitchData>, Serializable {

	public SwitchData(
		String className, int lineNumber, int switchNumber, int caseNumber) {

		_className = className;
		_lineNumber = lineNumber;
		_switchNumber = switchNumber;

		_hitCounterArray = new AtomicLongArray(caseNumber + 1);
	}

	@Override
	public double getBranchCoverageRate() {
		return (double)getNumberOfCoveredBranches() /
			getNumberOfValidBranches();
	}

	@Override
	public int getNumberOfCoveredBranches() {
		int numberOfCoveredBranches = 0;

		for (int i = 0; i < _hitCounterArray.length(); i++) {
			if (_hitCounterArray.get(i) <= 0) {
				continue;
			}

			numberOfCoveredBranches++;
		}

		return numberOfCoveredBranches;
	}

	@Override
	public int getNumberOfValidBranches() {
		return _hitCounterArray.length();
	}

	public int getSwitchNumber() {
		return _switchNumber;
	}

	@Override
	public void merge(SwitchData switchData) {
		AtomicLongArray hitCounterArray = switchData._hitCounterArray;

		if (!_className.equals(switchData._className) ||
			(_lineNumber != switchData._lineNumber) ||
			(_switchNumber != switchData._switchNumber) ||
			(_hitCounterArray.length() != hitCounterArray.length())) {

			throw new IllegalArgumentException(
				"Switch data mismatch, left : " + toString() + ", right : " +
					switchData);
		}

		for (int i = 0; i < _hitCounterArray.length(); i++) {
			_hitCounterArray.addAndGet(i, hitCounterArray.get(i));
		}
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();

		sb.append("{className=");
		sb.append(_className);
		sb.append(", lineNumber=");
		sb.append(_lineNumber);
		sb.append(", switchNumber=");
		sb.append(_switchNumber);
		sb.append(", caseNumber=");
		sb.append(_hitCounterArray.length());
		sb.append("}");

		return sb.toString();
	}

	public void touchBranch(int branch) {
		if (branch >= _hitCounterArray.length()) {
			throw new IllegalStateException(
				"No instrument data for class " + _className + " line " +
					_lineNumber + " switch " + _switchNumber + " branch " +
						branch);
		}

		if (branch == -1) {
			branch = _hitCounterArray.length() - 1;
		}

		_hitCounterArray.incrementAndGet(branch);
	}

	private static final long serialVersionUID = 1;

	private final String _className;
	private final AtomicLongArray _hitCounterArray;
	private final int _lineNumber;
	private final int _switchNumber;

}