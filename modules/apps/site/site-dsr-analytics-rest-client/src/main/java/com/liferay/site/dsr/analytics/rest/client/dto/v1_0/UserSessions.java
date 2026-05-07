/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.dsr.analytics.rest.client.dto.v1_0;

import com.liferay.site.dsr.analytics.rest.client.function.UnsafeSupplier;
import com.liferay.site.dsr.analytics.rest.client.serdes.v1_0.UserSessionsSerDes;

import jakarta.annotation.Generated;

import java.io.Serializable;

import java.util.Objects;

/**
 * @author Gianmarco Brunialti
 * @generated
 */
@Generated("")
public class UserSessions implements Cloneable, Serializable {

	public static UserSessions toDTO(String json) {
		return UserSessionsSerDes.toDTO(json);
	}

	public Long getTotalEvents() {
		return totalEvents;
	}

	public void setTotalEvents(Long totalEvents) {
		this.totalEvents = totalEvents;
	}

	public void setTotalEvents(
		UnsafeSupplier<Long, Exception> totalEventsUnsafeSupplier) {

		try {
			totalEvents = totalEventsUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Long totalEvents;

	public UserSession[] getUserSessions() {
		return userSessions;
	}

	public void setUserSessions(UserSession[] userSessions) {
		this.userSessions = userSessions;
	}

	public void setUserSessions(
		UnsafeSupplier<UserSession[], Exception> userSessionsUnsafeSupplier) {

		try {
			userSessions = userSessionsUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected UserSession[] userSessions;

	@Override
	public UserSessions clone() throws CloneNotSupportedException {
		return (UserSessions)super.clone();
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof UserSessions)) {
			return false;
		}

		UserSessions userSessions = (UserSessions)object;

		return Objects.equals(toString(), userSessions.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		return UserSessionsSerDes.toJSON(this);
	}

}
// LIFERAY-REST-BUILDER-HASH:568255478