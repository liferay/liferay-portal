/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.web.internal.application;

import com.fasterxml.jackson.jakarta.rs.json.JacksonJsonProvider;

import com.liferay.osb.faro.web.internal.exception.FaroEngineClientExceptionMapper;
import com.liferay.osb.faro.web.internal.exception.NoSuchModelExceptionMapper;
import com.liferay.osb.faro.web.internal.exception.OAuthExceptionMapper;
import com.liferay.osb.faro.web.internal.param.converter.FaroParamConverterProvider;
import com.liferay.osb.faro.web.internal.request.filter.FaroContainerRequestFilter;
import com.liferay.osb.faro.web.internal.request.filter.FaroContainerResponseFilter;
import com.liferay.osb.faro.web.internal.request.filter.SecurityFilter;
import com.liferay.osb.faro.web.internal.request.filter.TokenAuthenticationFilter;
import com.liferay.osb.faro.web.internal.util.JSONUtil;

import jakarta.ws.rs.core.Application;

import java.util.HashSet;
import java.util.Set;

/**
 * @author Matthew Kong
 */
public abstract class BaseApplication extends Application {

	@Override
	public Set<Class<?>> getClasses() {
		Set<Class<?>> classes = new HashSet<>();

		classes.add(FaroContainerRequestFilter.class);
		classes.add(FaroContainerResponseFilter.class);
		classes.add(FaroParamConverterProvider.class);

		return classes;
	}

	public abstract Set<Object> getControllers();

	@Override
	public Set<Object> getSingletons() {
		Set<Object> singletons = new HashSet<>();

		singletons.add(new JacksonJsonProvider(JSONUtil.getObjectMapper()));
		singletons.add(new FaroEngineClientExceptionMapper());
		singletons.add(new NoSuchModelExceptionMapper());
		singletons.add(new OAuthExceptionMapper());
		singletons.add(new SecurityFilter());
		singletons.add(new TokenAuthenticationFilter());
		singletons.addAll(getControllers());

		return singletons;
	}

}