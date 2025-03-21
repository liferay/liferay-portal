/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.admin.catalog.client.dto.v1_0;

import com.liferay.headless.commerce.admin.catalog.client.function.UnsafeSupplier;
import com.liferay.headless.commerce.admin.catalog.client.serdes.v1_0.ProductConfigurationListChannelSerDes;

import jakarta.annotation.Generated;

import java.io.Serializable;

import java.util.Map;
import java.util.Objects;

/**
 * @author Zoltán Takács
 * @generated
 */
@Generated("")
public class ProductConfigurationListChannel
	implements Cloneable, Serializable {

	public static ProductConfigurationListChannel toDTO(String json) {
		return ProductConfigurationListChannelSerDes.toDTO(json);
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

	public Integer getOrder() {
		return order;
	}

	public void setOrder(Integer order) {
		this.order = order;
	}

	public void setOrder(
		UnsafeSupplier<Integer, Exception> orderUnsafeSupplier) {

		try {
			order = orderUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Integer order;

	public Long getProductConfigurationListChannelId() {
		return productConfigurationListChannelId;
	}

	public void setProductConfigurationListChannelId(
		Long productConfigurationListChannelId) {

		this.productConfigurationListChannelId =
			productConfigurationListChannelId;
	}

	public void setProductConfigurationListChannelId(
		UnsafeSupplier<Long, Exception>
			productConfigurationListChannelIdUnsafeSupplier) {

		try {
			productConfigurationListChannelId =
				productConfigurationListChannelIdUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Long productConfigurationListChannelId;

	public String getProductConfigurationListExternalReferenceCode() {
		return productConfigurationListExternalReferenceCode;
	}

	public void setProductConfigurationListExternalReferenceCode(
		String productConfigurationListExternalReferenceCode) {

		this.productConfigurationListExternalReferenceCode =
			productConfigurationListExternalReferenceCode;
	}

	public void setProductConfigurationListExternalReferenceCode(
		UnsafeSupplier<String, Exception>
			productConfigurationListExternalReferenceCodeUnsafeSupplier) {

		try {
			productConfigurationListExternalReferenceCode =
				productConfigurationListExternalReferenceCodeUnsafeSupplier.
					get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String productConfigurationListExternalReferenceCode;

	public Long getProductConfigurationListId() {
		return productConfigurationListId;
	}

	public void setProductConfigurationListId(Long productConfigurationListId) {
		this.productConfigurationListId = productConfigurationListId;
	}

	public void setProductConfigurationListId(
		UnsafeSupplier<Long, Exception>
			productConfigurationListIdUnsafeSupplier) {

		try {
			productConfigurationListId =
				productConfigurationListIdUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Long productConfigurationListId;

	@Override
	public ProductConfigurationListChannel clone()
		throws CloneNotSupportedException {

		return (ProductConfigurationListChannel)super.clone();
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof ProductConfigurationListChannel)) {
			return false;
		}

		ProductConfigurationListChannel productConfigurationListChannel =
			(ProductConfigurationListChannel)object;

		return Objects.equals(
			toString(), productConfigurationListChannel.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		return ProductConfigurationListChannelSerDes.toJSON(this);
	}

}