/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.vulcan.internal.jaxrs.context.provider;

import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.search.filter.Filter;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.odata.entity.EntityModel;
import com.liferay.portal.odata.filter.ExpressionConvert;
import com.liferay.portal.odata.filter.FilterParser;
import com.liferay.portal.odata.filter.FilterParserProvider;
import com.liferay.portal.odata.filter.InvalidFilterException;
import com.liferay.portal.odata.filter.expression.ExpressionVisitException;
import com.liferay.portal.vulcan.accept.language.AcceptLanguage;
import com.liferay.portal.vulcan.internal.accept.language.AcceptLanguageImpl;

import jakarta.servlet.http.HttpServletRequest;

import jakarta.ws.rs.ServerErrorException;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.ext.Provider;

import org.apache.cxf.jaxrs.ext.ContextProvider;
import org.apache.cxf.message.Message;

/**
 * @author Brian Wing Shun Chan
 */
@Provider
public class FilterContextProvider implements ContextProvider<Filter> {

	public FilterContextProvider(
		ExpressionConvert<Filter> expressionConvert,
		FilterParserProvider filterParserProvider, Language language,
		Portal portal) {

		_expressionConvert = expressionConvert;
		_filterParserProvider = filterParserProvider;
		_language = language;
		_portal = portal;
	}

	public Filter createContext(
			AcceptLanguage acceptLanguage, EntityModel entityModel,
			String filterString)
		throws Exception {

		if (_log.isDebugEnabled()) {
			_log.debug("Filter parameter value: " + filterString);
		}

		if (Validator.isNull(filterString) || (entityModel == null)) {
			return null;
		}

		if (_log.isDebugEnabled()) {
			_log.debug("OData entity model: " + entityModel);
		}

		FilterParser filterParser = _filterParserProvider.provide(entityModel);

		if (_log.isDebugEnabled()) {
			_log.debug("OData filter parser: " + filterParser);
		}

		com.liferay.portal.odata.filter.Filter oDataFilter =
			new com.liferay.portal.odata.filter.Filter(
				filterParser.parse(filterString));

		if (_log.isDebugEnabled()) {
			_log.debug("OData filter: " + oDataFilter);
		}

		Filter filter = _expressionConvert.convert(
			oDataFilter.getExpression(), acceptLanguage.getPreferredLocale(),
			entityModel);

		if (_log.isDebugEnabled()) {
			_log.debug("Search filter: " + filter);
		}

		return filter;
	}

	@Override
	public Filter createContext(Message message) {
		try {
			HttpServletRequest httpServletRequest =
				ContextProviderUtil.getHttpServletRequest(message);

			return createContext(
				new AcceptLanguageImpl(httpServletRequest, _language, _portal),
				ContextProviderUtil.getEntityModel(message),
				ParamUtil.getString(httpServletRequest, "filter"));
		}
		catch (ExpressionVisitException expressionVisitException) {
			throw new InvalidFilterException(
				expressionVisitException.getMessage(),
				expressionVisitException);
		}
		catch (WebApplicationException webApplicationException) {
			throw webApplicationException;
		}
		catch (Exception exception) {
			throw new ServerErrorException(500, exception);
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		FilterContextProvider.class);

	private final ExpressionConvert<Filter> _expressionConvert;
	private final FilterParserProvider _filterParserProvider;
	private final Language _language;
	private final Portal _portal;

}