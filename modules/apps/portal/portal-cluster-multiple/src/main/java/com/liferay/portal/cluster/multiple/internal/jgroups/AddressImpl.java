/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.cluster.multiple.internal.jgroups;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import org.jgroups.Address;
import org.jgroups.util.Util;

/**
 * @author Shuyang Zhou
 */
public class AddressImpl
	implements com.liferay.portal.kernel.cluster.Address, Externalizable {

	public AddressImpl() {
	}

	public AddressImpl(Address address) {
		_address = address;
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof AddressImpl)) {
			return false;
		}

		AddressImpl addressImpl = (AddressImpl)object;

		return _address.equals(addressImpl._address);
	}

	@Override
	public String getDescription() {
		return Util.addressToString(_address);
	}

	@Override
	public Address getRealAddress() {
		return _address;
	}

	@Override
	public int hashCode() {
		return _address.hashCode();
	}

	@Override
	public void readExternal(ObjectInput objectInput)
		throws ClassNotFoundException, IOException {

		_address = Util.readAddress(objectInput);
	}

	@Override
	public String toString() {
		return Util.addressToString(_address);
	}

	@Override
	public void writeExternal(ObjectOutput objectOutput) throws IOException {
		Util.writeAddress(_address, objectOutput);
	}

	private static final long serialVersionUID = 7969878022424426497L;

	private Address _address;

}