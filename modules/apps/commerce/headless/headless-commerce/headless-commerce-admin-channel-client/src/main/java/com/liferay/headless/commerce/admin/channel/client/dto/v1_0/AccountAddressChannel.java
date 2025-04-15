/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.admin.channel.client.dto.v1_0;

import com.liferay.headless.commerce.admin.channel.client.function.UnsafeSupplier;
import com.liferay.headless.commerce.admin.channel.client.serdes.v1_0.AccountAddressChannelSerDes;

import java.io.Serializable;

import java.util.Map;
import java.util.Objects;

import jakarta.annotation.Generated;

/**
 * @author Andrea Sbarra
 * @generated
 */
@Generated("")
public class AccountAddressChannel implements Cloneable, Serializable {

	public static AccountAddressChannel toDTO(String json) {
		return AccountAddressChannelSerDes.toDTO(json);
	}

	public Long getAccountAddressChannelId() {
		return accountAddressChannelId;
	}

	public void setAccountAddressChannelId(Long accountAddressChannelId) {
		this.accountAddressChannelId = accountAddressChannelId;
	}

	public void setAccountAddressChannelId(
		UnsafeSupplier<Long, Exception> accountAddressChannelIdUnsafeSupplier) {

		try {
			accountAddressChannelId =
				accountAddressChannelIdUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Long accountAddressChannelId;

	public Map<String, Map<String, String>> getActions() {
		return actions;
	}

	public void setActions(Map<String, Map<String, String>> actions) {
		this.actions = actions;
	}

	public void setActions(
		UnsafeSupplier<Map<String, Map<String, String>>, Exception>
			actionsUnsafeSupplier) {

		try {
			actions = actionsUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Map<String, Map<String, String>> actions;

	public String getAddressChannelExternalReferenceCode() {
		return addressChannelExternalReferenceCode;
	}

	public void setAddressChannelExternalReferenceCode(
		String addressChannelExternalReferenceCode) {

		this.addressChannelExternalReferenceCode =
			addressChannelExternalReferenceCode;
	}

	public void setAddressChannelExternalReferenceCode(
		UnsafeSupplier<String, Exception>
			addressChannelExternalReferenceCodeUnsafeSupplier) {

		try {
			addressChannelExternalReferenceCode =
				addressChannelExternalReferenceCodeUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String addressChannelExternalReferenceCode;

	public Long getAddressChannelId() {
		return addressChannelId;
	}

	public void setAddressChannelId(Long addressChannelId) {
		this.addressChannelId = addressChannelId;
	}

	public void setAddressChannelId(
		UnsafeSupplier<Long, Exception> addressChannelIdUnsafeSupplier) {

		try {
			addressChannelId = addressChannelIdUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Long addressChannelId;

	public String getAddressExternalReferenceCode() {
		return addressExternalReferenceCode;
	}

	public void setAddressExternalReferenceCode(
		String addressExternalReferenceCode) {

		this.addressExternalReferenceCode = addressExternalReferenceCode;
	}

	public void setAddressExternalReferenceCode(
		UnsafeSupplier<String, Exception>
			addressExternalReferenceCodeUnsafeSupplier) {

		try {
			addressExternalReferenceCode =
				addressExternalReferenceCodeUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String addressExternalReferenceCode;

	public Long getAddressId() {
		return addressId;
	}

	public void setAddressId(Long addressId) {
		this.addressId = addressId;
	}

	public void setAddressId(
		UnsafeSupplier<Long, Exception> addressIdUnsafeSupplier) {

		try {
			addressId = addressIdUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Long addressId;

	public Channel getChannel() {
		return channel;
	}

	public void setChannel(Channel channel) {
		this.channel = channel;
	}

	public void setChannel(
		UnsafeSupplier<Channel, Exception> channelUnsafeSupplier) {

		try {
			channel = channelUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Channel channel;

	@Override
	public AccountAddressChannel clone() throws CloneNotSupportedException {
		return (AccountAddressChannel)super.clone();
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof AccountAddressChannel)) {
			return false;
		}

		AccountAddressChannel accountAddressChannel =
			(AccountAddressChannel)object;

		return Objects.equals(toString(), accountAddressChannel.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		return AccountAddressChannelSerDes.toJSON(this);
	}

}