/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.delivery.order.client.dto.v1_0;

import com.liferay.headless.commerce.delivery.order.client.function.UnsafeSupplier;
import com.liferay.headless.commerce.delivery.order.client.serdes.v1_0.PlacedOrderCommentSerDes;

import java.io.Serializable;

import java.util.Objects;

import jakarta.annotation.Generated;

/**
 * @author Andrea Sbarra
 * @generated
 */
@Generated("")
public class PlacedOrderComment implements Cloneable, Serializable {

	public static PlacedOrderComment toDTO(String json) {
		return PlacedOrderCommentSerDes.toDTO(json);
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public void setAuthor(
		UnsafeSupplier<String, Exception> authorUnsafeSupplier) {

		try {
			author = authorUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String author;

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public void setContent(
		UnsafeSupplier<String, Exception> contentUnsafeSupplier) {

		try {
			content = contentUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String content;

	public String getExternalReferenceCode() {
		return externalReferenceCode;
	}

	public void setExternalReferenceCode(String externalReferenceCode) {
		this.externalReferenceCode = externalReferenceCode;
	}

	public void setExternalReferenceCode(
		UnsafeSupplier<String, Exception> externalReferenceCodeUnsafeSupplier) {

		try {
			externalReferenceCode = externalReferenceCodeUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String externalReferenceCode;

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

	public Long getOrderId() {
		return orderId;
	}

	public void setOrderId(Long orderId) {
		this.orderId = orderId;
	}

	public void setOrderId(
		UnsafeSupplier<Long, Exception> orderIdUnsafeSupplier) {

		try {
			orderId = orderIdUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Long orderId;

	public Boolean getRestricted() {
		return restricted;
	}

	public void setRestricted(Boolean restricted) {
		this.restricted = restricted;
	}

	public void setRestricted(
		UnsafeSupplier<Boolean, Exception> restrictedUnsafeSupplier) {

		try {
			restricted = restrictedUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Boolean restricted;

	@Override
	public PlacedOrderComment clone() throws CloneNotSupportedException {
		return (PlacedOrderComment)super.clone();
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof PlacedOrderComment)) {
			return false;
		}

		PlacedOrderComment placedOrderComment = (PlacedOrderComment)object;

		return Objects.equals(toString(), placedOrderComment.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		return PlacedOrderCommentSerDes.toJSON(this);
	}

}