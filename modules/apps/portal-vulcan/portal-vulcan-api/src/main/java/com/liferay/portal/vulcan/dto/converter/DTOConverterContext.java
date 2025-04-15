/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.vulcan.dto.converter;

import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.util.LocaleThreadLocal;

import jakarta.servlet.http.HttpServletRequest;

import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.core.UriInfo;

import java.io.Serializable;

import java.util.Collections;
import java.util.Locale;
import java.util.Map;

/**
 * @author Rubén Pulido
 * @author Víctor Galán
 */
public interface DTOConverterContext {

	public default boolean containsNestedFieldsValue(String key) {
		UriInfo uriInfo = getUriInfo();

		if (uriInfo == null) {
			return false;
		}

		MultivaluedMap<String, String> parameters =
			uriInfo.getQueryParameters();

		if ((parameters == null) || parameters.isEmpty()) {
			return false;
		}

		String fields = parameters.getFirst("nestedFields");

		if (fields == null) {
			return false;
		}

		return fields.contains(key);
	}

	public default Map<String, Map<String, String>> getActions() {
		return Collections.emptyMap();
	}

	public default Object getAttribute(String name) {
		return null;
	}

	public default Map<String, Object> getAttributes() {
		return Collections.emptyMap();
	}

	public default DTOConverterRegistry getDTOConverterRegistry() {
		return null;
	}

	public default HttpServletRequest getHttpServletRequest() {
		return null;
	}

	public default Object getId() {
		return null;
	}

	public default Locale getLocale() {
		return LocaleThreadLocal.getDefaultLocale();
	}

	public default UriInfo getUriInfo() {
		return null;
	}

	public default User getUser() {
		return (User)PermissionThreadLocal.getPermissionChecker();
	}

	public default long getUserId() {
		User user = getUser();

		return user.getUserId();
	}

	public default boolean isAcceptAllLanguages() {
		return true;
	}

	public default Object removeAttribute(String name) {
		return null;
	}

	public default void setAttribute(String name, Object value) {
	}

	public default void setAttributes(Map<String, Serializable> attributes) {
	}

}