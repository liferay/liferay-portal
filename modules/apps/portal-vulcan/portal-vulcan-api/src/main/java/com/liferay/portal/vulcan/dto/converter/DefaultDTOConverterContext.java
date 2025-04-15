/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.vulcan.dto.converter;

import com.liferay.portal.kernel.model.User;

import jakarta.servlet.http.HttpServletRequest;

import jakarta.ws.rs.core.UriInfo;

import java.io.Serializable;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * @author Rubén Pulido
 * @author Víctor Galán
 */
public class DefaultDTOConverterContext implements DTOConverterContext {

	/**
	 * @deprecated As of Athanasius (7.3.x)
	 */
	@Deprecated
	public DefaultDTOConverterContext(
		boolean acceptAllLanguages, DTOConverterRegistry dtoConverterRegistry,
		Object id, Locale locale, UriInfo uriInfo, User user) {

		this(
			acceptAllLanguages, new HashMap<>(), dtoConverterRegistry, id,
			locale, uriInfo, user);
	}

	public DefaultDTOConverterContext(
		boolean acceptAllLanguages, Map<String, Map<String, String>> actions,
		DTOConverterRegistry dtoConverterRegistry,
		HttpServletRequest httpServletRequest, Object id, Locale locale,
		UriInfo uriInfo, User user) {

		_acceptAllLanguages = acceptAllLanguages;
		_actions = actions;
		_dtoConverterRegistry = dtoConverterRegistry;
		_httpServletRequest = httpServletRequest;
		_id = id;
		_locale = locale;
		_uriInfo = uriInfo;
		_user = user;

		_attributes = new HashMap<>();
	}

	public DefaultDTOConverterContext(
		boolean acceptAllLanguages, Map<String, Map<String, String>> actions,
		DTOConverterRegistry dtoConverterRegistry, Object id, Locale locale,
		UriInfo uriInfo, User user) {

		this(
			acceptAllLanguages, actions, dtoConverterRegistry, null, id, locale,
			uriInfo, user);
	}

	public DefaultDTOConverterContext(
		DTOConverterRegistry dtoConverterRegistry, Object id, Locale locale,
		UriInfo uriInfo, User user) {

		this(
			false, new HashMap<>(), dtoConverterRegistry, id, locale, uriInfo,
			user);
	}

	/**
	 * @deprecated As of Athanasius (7.3.x)
	 */
	@Deprecated
	public DefaultDTOConverterContext(Object id, Locale locale) {
		this(false, new HashMap<>(), null, id, locale, null, null);
	}

	/**
	 * @deprecated As of Athanasius (7.3.x)
	 */
	@Deprecated
	public DefaultDTOConverterContext(
		Object id, Locale locale, UriInfo uriInfo) {

		this(false, new HashMap<>(), null, id, locale, uriInfo, null);
	}

	/**
	 * @deprecated As of Athanasius (7.3.x)
	 */
	@Deprecated
	public DefaultDTOConverterContext(
		Object id, Locale locale, UriInfo uriInfo, User user) {

		this(false, new HashMap<>(), null, id, locale, uriInfo, user);
	}

	@Override
	public Map<String, Map<String, String>> getActions() {
		return _actions;
	}

	@Override
	public Object getAttribute(String name) {
		return _attributes.get(name);
	}

	@Override
	public Map<String, Object> getAttributes() {
		return _attributes;
	}

	@Override
	public DTOConverterRegistry getDTOConverterRegistry() {
		return _dtoConverterRegistry;
	}

	@Override
	public HttpServletRequest getHttpServletRequest() {
		return _httpServletRequest;
	}

	@Override
	public Object getId() {
		return _id;
	}

	@Override
	public Locale getLocale() {
		return _locale;
	}

	@Override
	public UriInfo getUriInfo() {
		return _uriInfo;
	}

	@Override
	public User getUser() {
		return _user;
	}

	@Override
	public long getUserId() {
		if (_user != null) {
			return _user.getUserId();
		}

		return 0;
	}

	@Override
	public boolean isAcceptAllLanguages() {
		return _acceptAllLanguages;
	}

	@Override
	public Object removeAttribute(String name) {
		return _attributes.remove(name);
	}

	@Override
	public void setAttribute(String name, Object value) {
		_attributes.put(name, value);
	}

	@Override
	public void setAttributes(Map<String, Serializable> attributes) {
		_attributes.putAll(attributes);
	}

	private final boolean _acceptAllLanguages;
	private final Map<String, Map<String, String>> _actions;
	private final Map<String, Object> _attributes;
	private final DTOConverterRegistry _dtoConverterRegistry;
	private final HttpServletRequest _httpServletRequest;
	private final Object _id;
	private final Locale _locale;
	private final UriInfo _uriInfo;
	private final User _user;

}