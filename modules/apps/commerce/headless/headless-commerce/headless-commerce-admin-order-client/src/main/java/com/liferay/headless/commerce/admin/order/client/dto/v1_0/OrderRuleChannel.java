/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.admin.order.client.dto.v1_0;

import com.liferay.headless.commerce.admin.order.client.function.UnsafeSupplier;
import com.liferay.headless.commerce.admin.order.client.serdes.v1_0.OrderRuleChannelSerDes;

import java.io.Serializable;

import java.util.Map;
import java.util.Objects;

import jakarta.annotation.Generated;

/**
 * @author Alessio Antonio Rendina
 * @generated
 */
@Generated("")
public class OrderRuleChannel implements Cloneable, Serializable {

	public static OrderRuleChannel toDTO(String json) {
		return OrderRuleChannelSerDes.toDTO(json);
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

	public Long getOrderRuleChannelId() {
		return orderRuleChannelId;
	}

	public void setOrderRuleChannelId(Long orderRuleChannelId) {
		this.orderRuleChannelId = orderRuleChannelId;
	}

	public void setOrderRuleChannelId(
		UnsafeSupplier<Long, Exception> orderRuleChannelIdUnsafeSupplier) {

		try {
			orderRuleChannelId = orderRuleChannelIdUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Long orderRuleChannelId;

	public String getOrderRuleExternalReferenceCode() {
		return orderRuleExternalReferenceCode;
	}

	public void setOrderRuleExternalReferenceCode(
		String orderRuleExternalReferenceCode) {

		this.orderRuleExternalReferenceCode = orderRuleExternalReferenceCode;
	}

	public void setOrderRuleExternalReferenceCode(
		UnsafeSupplier<String, Exception>
			orderRuleExternalReferenceCodeUnsafeSupplier) {

		try {
			orderRuleExternalReferenceCode =
				orderRuleExternalReferenceCodeUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String orderRuleExternalReferenceCode;

	public Long getOrderRuleId() {
		return orderRuleId;
	}

	public void setOrderRuleId(Long orderRuleId) {
		this.orderRuleId = orderRuleId;
	}

	public void setOrderRuleId(
		UnsafeSupplier<Long, Exception> orderRuleIdUnsafeSupplier) {

		try {
			orderRuleId = orderRuleIdUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Long orderRuleId;

	@Override
	public OrderRuleChannel clone() throws CloneNotSupportedException {
		return (OrderRuleChannel)super.clone();
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof OrderRuleChannel)) {
			return false;
		}

		OrderRuleChannel orderRuleChannel = (OrderRuleChannel)object;

		return Objects.equals(toString(), orderRuleChannel.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		return OrderRuleChannelSerDes.toJSON(this);
	}

}