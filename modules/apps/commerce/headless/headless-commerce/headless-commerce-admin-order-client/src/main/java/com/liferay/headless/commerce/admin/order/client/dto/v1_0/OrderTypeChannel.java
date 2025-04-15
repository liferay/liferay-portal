/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.admin.order.client.dto.v1_0;

import com.liferay.headless.commerce.admin.order.client.function.UnsafeSupplier;
import com.liferay.headless.commerce.admin.order.client.serdes.v1_0.OrderTypeChannelSerDes;

import java.io.Serializable;

import java.util.Map;
import java.util.Objects;

import jakarta.annotation.Generated;

/**
 * @author Alessio Antonio Rendina
 * @generated
 */
@Generated("")
public class OrderTypeChannel implements Cloneable, Serializable {

	public static OrderTypeChannel toDTO(String json) {
		return OrderTypeChannelSerDes.toDTO(json);
	}

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

	public Long getOrderTypeChannelId() {
		return orderTypeChannelId;
	}

	public void setOrderTypeChannelId(Long orderTypeChannelId) {
		this.orderTypeChannelId = orderTypeChannelId;
	}

	public void setOrderTypeChannelId(
		UnsafeSupplier<Long, Exception> orderTypeChannelIdUnsafeSupplier) {

		try {
			orderTypeChannelId = orderTypeChannelIdUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Long orderTypeChannelId;

	public String getOrderTypeExternalReferenceCode() {
		return orderTypeExternalReferenceCode;
	}

	public void setOrderTypeExternalReferenceCode(
		String orderTypeExternalReferenceCode) {

		this.orderTypeExternalReferenceCode = orderTypeExternalReferenceCode;
	}

	public void setOrderTypeExternalReferenceCode(
		UnsafeSupplier<String, Exception>
			orderTypeExternalReferenceCodeUnsafeSupplier) {

		try {
			orderTypeExternalReferenceCode =
				orderTypeExternalReferenceCodeUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String orderTypeExternalReferenceCode;

	public Long getOrderTypeId() {
		return orderTypeId;
	}

	public void setOrderTypeId(Long orderTypeId) {
		this.orderTypeId = orderTypeId;
	}

	public void setOrderTypeId(
		UnsafeSupplier<Long, Exception> orderTypeIdUnsafeSupplier) {

		try {
			orderTypeId = orderTypeIdUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Long orderTypeId;

	@Override
	public OrderTypeChannel clone() throws CloneNotSupportedException {
		return (OrderTypeChannel)super.clone();
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof OrderTypeChannel)) {
			return false;
		}

		OrderTypeChannel orderTypeChannel = (OrderTypeChannel)object;

		return Objects.equals(toString(), orderTypeChannel.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		return OrderTypeChannelSerDes.toJSON(this);
	}

}