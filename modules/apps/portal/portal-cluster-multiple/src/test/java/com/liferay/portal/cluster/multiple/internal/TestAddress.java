/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.cluster.multiple.internal;

import com.liferay.portal.kernel.cluster.Address;

/**
 * @author Tina Tian
 */
public class TestAddress implements Address, Comparable<TestAddress> {

	public TestAddress(int address) {
		_address = address;
	}

	@Override
	public int compareTo(TestAddress testAddress) {
		return Integer.compare(_address, testAddress._address);
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof TestAddress)) {
			return false;
		}

		TestAddress testAddress = (TestAddress)object;

		if (_address == testAddress._address) {
			return true;
		}

		return false;
	}

	@Override
	public String getDescription() {
		return String.valueOf(_address);
	}

	@Override
	public Object getRealAddress() {
		return _address;
	}

	@Override
	public int hashCode() {
		return _address;
	}

	@Override
	public String toString() {
		return String.valueOf(_address);
	}

	private final int _address;

}