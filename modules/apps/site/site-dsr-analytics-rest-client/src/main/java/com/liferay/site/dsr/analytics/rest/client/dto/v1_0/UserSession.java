/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.dsr.analytics.rest.client.dto.v1_0;

import com.liferay.site.dsr.analytics.rest.client.function.UnsafeSupplier;
import com.liferay.site.dsr.analytics.rest.client.serdes.v1_0.UserSessionSerDes;

import jakarta.annotation.Generated;

import java.io.Serializable;

import java.util.Objects;

/**
 * @author Gianmarco Brunialti
 * @generated
 */
@Generated("")
public class UserSession implements Cloneable, Serializable {

	public static UserSession toDTO(String json) {
		return UserSessionSerDes.toDTO(json);
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public void setUserName(
		UnsafeSupplier<String, Exception> userNameUnsafeSupplier) {

		try {
			userName = userNameUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String userName;

	public UserSessionEvent[] getUserSessionEvents() {
		return userSessionEvents;
	}

	public void setUserSessionEvents(UserSessionEvent[] userSessionEvents) {
		this.userSessionEvents = userSessionEvents;
	}

	public void setUserSessionEvents(
		UnsafeSupplier<UserSessionEvent[], Exception>
			userSessionEventsUnsafeSupplier) {

		try {
			userSessionEvents = userSessionEventsUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected UserSessionEvent[] userSessionEvents;

	@Override
	public UserSession clone() throws CloneNotSupportedException {
		return (UserSession)super.clone();
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof UserSession)) {
			return false;
		}

		UserSession userSession = (UserSession)object;

		return Objects.equals(toString(), userSession.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		return UserSessionSerDes.toJSON(this);
	}

}
// LIFERAY-REST-BUILDER-HASH:-460684754