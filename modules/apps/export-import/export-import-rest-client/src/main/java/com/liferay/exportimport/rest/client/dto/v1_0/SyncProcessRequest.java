/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.exportimport.rest.client.dto.v1_0;

import com.liferay.exportimport.rest.client.function.UnsafeSupplier;
import com.liferay.exportimport.rest.client.serdes.v1_0.SyncProcessRequestSerDes;

import jakarta.annotation.Generated;

import java.io.Serializable;

import java.util.Objects;

/**
 * @author Petteri Karttunen
 * @generated
 */
@Generated("")
public class SyncProcessRequest implements Cloneable, Serializable {

	public static SyncProcessRequest toDTO(String json) {
		return SyncProcessRequestSerDes.toDTO(json);
	}

	public Boolean getComments() {
		return comments;
	}

	public void setComments(Boolean comments) {
		this.comments = comments;
	}

	public void setComments(
		UnsafeSupplier<Boolean, Exception> commentsUnsafeSupplier) {

		try {
			comments = commentsUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Boolean comments;

	public PortletDataHandler[] getPortletDataHandlers() {
		return portletDataHandlers;
	}

	public void setPortletDataHandlers(
		PortletDataHandler[] portletDataHandlers) {

		this.portletDataHandlers = portletDataHandlers;
	}

	public void setPortletDataHandlers(
		UnsafeSupplier<PortletDataHandler[], Exception>
			portletDataHandlersUnsafeSupplier) {

		try {
			portletDataHandlers = portletDataHandlersUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected PortletDataHandler[] portletDataHandlers;

	public Boolean getRatings() {
		return ratings;
	}

	public void setRatings(Boolean ratings) {
		this.ratings = ratings;
	}

	public void setRatings(
		UnsafeSupplier<Boolean, Exception> ratingsUnsafeSupplier) {

		try {
			ratings = ratingsUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Boolean ratings;

	@Override
	public SyncProcessRequest clone() throws CloneNotSupportedException {
		return (SyncProcessRequest)super.clone();
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof SyncProcessRequest)) {
			return false;
		}

		SyncProcessRequest syncProcessRequest = (SyncProcessRequest)object;

		return Objects.equals(toString(), syncProcessRequest.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		return SyncProcessRequestSerDes.toJSON(this);
	}

}
// LIFERAY-REST-BUILDER-HASH:1348553884