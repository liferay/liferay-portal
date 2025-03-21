/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.admin.catalog.client.dto.v1_0;

import com.liferay.headless.commerce.admin.catalog.client.function.UnsafeSupplier;
import com.liferay.headless.commerce.admin.catalog.client.serdes.v1_0.ProductSubscriptionConfigurationSerDes;

import jakarta.annotation.Generated;

import java.io.Serializable;

import java.util.Map;
import java.util.Objects;

/**
 * @author Zoltán Takács
 * @generated
 */
@Generated("")
public class ProductSubscriptionConfiguration
	implements Cloneable, Serializable {

	public static ProductSubscriptionConfiguration toDTO(String json) {
		return ProductSubscriptionConfigurationSerDes.toDTO(json);
	}

	public Boolean getDeliverySubscriptionEnable() {
		return deliverySubscriptionEnable;
	}

	public void setDeliverySubscriptionEnable(
		Boolean deliverySubscriptionEnable) {

		this.deliverySubscriptionEnable = deliverySubscriptionEnable;
	}

	public void setDeliverySubscriptionEnable(
		UnsafeSupplier<Boolean, Exception>
			deliverySubscriptionEnableUnsafeSupplier) {

		try {
			deliverySubscriptionEnable =
				deliverySubscriptionEnableUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Boolean deliverySubscriptionEnable;

	public Integer getDeliverySubscriptionLength() {
		return deliverySubscriptionLength;
	}

	public void setDeliverySubscriptionLength(
		Integer deliverySubscriptionLength) {

		this.deliverySubscriptionLength = deliverySubscriptionLength;
	}

	public void setDeliverySubscriptionLength(
		UnsafeSupplier<Integer, Exception>
			deliverySubscriptionLengthUnsafeSupplier) {

		try {
			deliverySubscriptionLength =
				deliverySubscriptionLengthUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Integer deliverySubscriptionLength;

	public Long getDeliverySubscriptionNumberOfLength() {
		return deliverySubscriptionNumberOfLength;
	}

	public void setDeliverySubscriptionNumberOfLength(
		Long deliverySubscriptionNumberOfLength) {

		this.deliverySubscriptionNumberOfLength =
			deliverySubscriptionNumberOfLength;
	}

	public void setDeliverySubscriptionNumberOfLength(
		UnsafeSupplier<Long, Exception>
			deliverySubscriptionNumberOfLengthUnsafeSupplier) {

		try {
			deliverySubscriptionNumberOfLength =
				deliverySubscriptionNumberOfLengthUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Long deliverySubscriptionNumberOfLength;

	public DeliverySubscriptionType getDeliverySubscriptionType() {
		return deliverySubscriptionType;
	}

	public String getDeliverySubscriptionTypeAsString() {
		if (deliverySubscriptionType == null) {
			return null;
		}

		return deliverySubscriptionType.toString();
	}

	public void setDeliverySubscriptionType(
		DeliverySubscriptionType deliverySubscriptionType) {

		this.deliverySubscriptionType = deliverySubscriptionType;
	}

	public void setDeliverySubscriptionType(
		UnsafeSupplier<DeliverySubscriptionType, Exception>
			deliverySubscriptionTypeUnsafeSupplier) {

		try {
			deliverySubscriptionType =
				deliverySubscriptionTypeUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected DeliverySubscriptionType deliverySubscriptionType;

	public Map<String, String> getDeliverySubscriptionTypeSettings() {
		return deliverySubscriptionTypeSettings;
	}

	public void setDeliverySubscriptionTypeSettings(
		Map<String, String> deliverySubscriptionTypeSettings) {

		this.deliverySubscriptionTypeSettings =
			deliverySubscriptionTypeSettings;
	}

	public void setDeliverySubscriptionTypeSettings(
		UnsafeSupplier<Map<String, String>, Exception>
			deliverySubscriptionTypeSettingsUnsafeSupplier) {

		try {
			deliverySubscriptionTypeSettings =
				deliverySubscriptionTypeSettingsUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Map<String, String> deliverySubscriptionTypeSettings;

	public Boolean getEnable() {
		return enable;
	}

	public void setEnable(Boolean enable) {
		this.enable = enable;
	}

	public void setEnable(
		UnsafeSupplier<Boolean, Exception> enableUnsafeSupplier) {

		try {
			enable = enableUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Boolean enable;

	public Integer getLength() {
		return length;
	}

	public void setLength(Integer length) {
		this.length = length;
	}

	public void setLength(
		UnsafeSupplier<Integer, Exception> lengthUnsafeSupplier) {

		try {
			length = lengthUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Integer length;

	public Long getNumberOfLength() {
		return numberOfLength;
	}

	public void setNumberOfLength(Long numberOfLength) {
		this.numberOfLength = numberOfLength;
	}

	public void setNumberOfLength(
		UnsafeSupplier<Long, Exception> numberOfLengthUnsafeSupplier) {

		try {
			numberOfLength = numberOfLengthUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Long numberOfLength;

	public SubscriptionType getSubscriptionType() {
		return subscriptionType;
	}

	public String getSubscriptionTypeAsString() {
		if (subscriptionType == null) {
			return null;
		}

		return subscriptionType.toString();
	}

	public void setSubscriptionType(SubscriptionType subscriptionType) {
		this.subscriptionType = subscriptionType;
	}

	public void setSubscriptionType(
		UnsafeSupplier<SubscriptionType, Exception>
			subscriptionTypeUnsafeSupplier) {

		try {
			subscriptionType = subscriptionTypeUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected SubscriptionType subscriptionType;

	public Map<String, String> getSubscriptionTypeSettings() {
		return subscriptionTypeSettings;
	}

	public void setSubscriptionTypeSettings(
		Map<String, String> subscriptionTypeSettings) {

		this.subscriptionTypeSettings = subscriptionTypeSettings;
	}

	public void setSubscriptionTypeSettings(
		UnsafeSupplier<Map<String, String>, Exception>
			subscriptionTypeSettingsUnsafeSupplier) {

		try {
			subscriptionTypeSettings =
				subscriptionTypeSettingsUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Map<String, String> subscriptionTypeSettings;

	@Override
	public ProductSubscriptionConfiguration clone()
		throws CloneNotSupportedException {

		return (ProductSubscriptionConfiguration)super.clone();
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof ProductSubscriptionConfiguration)) {
			return false;
		}

		ProductSubscriptionConfiguration productSubscriptionConfiguration =
			(ProductSubscriptionConfiguration)object;

		return Objects.equals(
			toString(), productSubscriptionConfiguration.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		return ProductSubscriptionConfigurationSerDes.toJSON(this);
	}

	public static enum DeliverySubscriptionType {

		DAILY("daily"), MONTHLY("monthly"), WEEKLY("weekly"), YEARLY("yearly");

		public static DeliverySubscriptionType create(String value) {
			for (DeliverySubscriptionType deliverySubscriptionType : values()) {
				if (Objects.equals(
						deliverySubscriptionType.getValue(), value) ||
					Objects.equals(deliverySubscriptionType.name(), value)) {

					return deliverySubscriptionType;
				}
			}

			return null;
		}

		public String getValue() {
			return _value;
		}

		@Override
		public String toString() {
			return _value;
		}

		private DeliverySubscriptionType(String value) {
			_value = value;
		}

		private final String _value;

	}

	public static enum SubscriptionType {

		DAILY("daily"), MONTHLY("monthly"), WEEKLY("weekly"), YEARLY("yearly");

		public static SubscriptionType create(String value) {
			for (SubscriptionType subscriptionType : values()) {
				if (Objects.equals(subscriptionType.getValue(), value) ||
					Objects.equals(subscriptionType.name(), value)) {

					return subscriptionType;
				}
			}

			return null;
		}

		public String getValue() {
			return _value;
		}

		@Override
		public String toString() {
			return _value;
		}

		private SubscriptionType(String value) {
			_value = value;
		}

		private final String _value;

	}

}