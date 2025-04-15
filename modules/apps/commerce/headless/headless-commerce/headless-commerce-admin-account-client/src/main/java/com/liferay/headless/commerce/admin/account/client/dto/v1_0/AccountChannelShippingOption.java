/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.admin.account.client.dto.v1_0;

import com.liferay.headless.commerce.admin.account.client.function.UnsafeSupplier;
import com.liferay.headless.commerce.admin.account.client.serdes.v1_0.AccountChannelShippingOptionSerDes;

import java.io.Serializable;

import java.util.Map;
import java.util.Objects;

import jakarta.annotation.Generated;

/**
 * @author Alessio Antonio Rendina
 * @generated
 */
@Generated("")
public class AccountChannelShippingOption implements Cloneable, Serializable {

	public static AccountChannelShippingOption toDTO(String json) {
		return AccountChannelShippingOptionSerDes.toDTO(json);
	}

	public String getAccountExternalReferenceCode() {
		return accountExternalReferenceCode;
	}

	public void setAccountExternalReferenceCode(
		String accountExternalReferenceCode) {

		this.accountExternalReferenceCode = accountExternalReferenceCode;
	}

	public void setAccountExternalReferenceCode(
		UnsafeSupplier<String, Exception>
			accountExternalReferenceCodeUnsafeSupplier) {

		try {
			accountExternalReferenceCode =
				accountExternalReferenceCodeUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String accountExternalReferenceCode;

	public Long getAccountId() {
		return accountId;
	}

	public void setAccountId(Long accountId) {
		this.accountId = accountId;
	}

	public void setAccountId(
		UnsafeSupplier<Long, Exception> accountIdUnsafeSupplier) {

		try {
			accountId = accountIdUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Long accountId;

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

	public String getChannelExternalReferenceCode() {
		return channelExternalReferenceCode;
	}

	public void setChannelExternalReferenceCode(
		String channelExternalReferenceCode) {

		this.channelExternalReferenceCode = channelExternalReferenceCode;
	}

	public void setChannelExternalReferenceCode(
		UnsafeSupplier<String, Exception>
			channelExternalReferenceCodeUnsafeSupplier) {

		try {
			channelExternalReferenceCode =
				channelExternalReferenceCodeUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String channelExternalReferenceCode;

	public Long getChannelId() {
		return channelId;
	}

	public void setChannelId(Long channelId) {
		this.channelId = channelId;
	}

	public void setChannelId(
		UnsafeSupplier<Long, Exception> channelIdUnsafeSupplier) {

		try {
			channelId = channelIdUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Long channelId;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public void setId(UnsafeSupplier<Long, Exception> idUnsafeSupplier) {
		try {
			id = idUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Long id;

	public Long getShippingMethodId() {
		return shippingMethodId;
	}

	public void setShippingMethodId(Long shippingMethodId) {
		this.shippingMethodId = shippingMethodId;
	}

	public void setShippingMethodId(
		UnsafeSupplier<Long, Exception> shippingMethodIdUnsafeSupplier) {

		try {
			shippingMethodId = shippingMethodIdUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Long shippingMethodId;

	public String getShippingMethodKey() {
		return shippingMethodKey;
	}

	public void setShippingMethodKey(String shippingMethodKey) {
		this.shippingMethodKey = shippingMethodKey;
	}

	public void setShippingMethodKey(
		UnsafeSupplier<String, Exception> shippingMethodKeyUnsafeSupplier) {

		try {
			shippingMethodKey = shippingMethodKeyUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String shippingMethodKey;

	public Long getShippingOptionId() {
		return shippingOptionId;
	}

	public void setShippingOptionId(Long shippingOptionId) {
		this.shippingOptionId = shippingOptionId;
	}

	public void setShippingOptionId(
		UnsafeSupplier<Long, Exception> shippingOptionIdUnsafeSupplier) {

		try {
			shippingOptionId = shippingOptionIdUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Long shippingOptionId;

	public String getShippingOptionKey() {
		return shippingOptionKey;
	}

	public void setShippingOptionKey(String shippingOptionKey) {
		this.shippingOptionKey = shippingOptionKey;
	}

	public void setShippingOptionKey(
		UnsafeSupplier<String, Exception> shippingOptionKeyUnsafeSupplier) {

		try {
			shippingOptionKey = shippingOptionKeyUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String shippingOptionKey;

	@Override
	public AccountChannelShippingOption clone()
		throws CloneNotSupportedException {

		return (AccountChannelShippingOption)super.clone();
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof AccountChannelShippingOption)) {
			return false;
		}

		AccountChannelShippingOption accountChannelShippingOption =
			(AccountChannelShippingOption)object;

		return Objects.equals(
			toString(), accountChannelShippingOption.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		return AccountChannelShippingOptionSerDes.toJSON(this);
	}

}