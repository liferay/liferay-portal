/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.vulcan.internal.jaxrs.context.provider;

import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.odata.entity.CollectionEntityField;
import com.liferay.portal.odata.entity.ComplexEntityField;
import com.liferay.portal.odata.entity.EntityField;
import com.liferay.portal.odata.entity.EntityModel;
import com.liferay.portal.vulcan.accept.language.AcceptLanguage;
import com.liferay.portal.vulcan.aggregation.Aggregation;
import com.liferay.portal.vulcan.internal.accept.language.AcceptLanguageImpl;

import jakarta.servlet.http.HttpServletRequest;

import jakarta.ws.rs.ServerErrorException;
import jakarta.ws.rs.ext.Provider;

import java.util.Map;

import org.apache.cxf.jaxrs.ext.ContextProvider;
import org.apache.cxf.message.Message;

/**
 * @author Javier Gamarra
 */
@Provider
public class AggregationContextProvider
	implements ContextProvider<Aggregation> {

	public AggregationContextProvider(Language language, Portal portal) {
		_language = language;
		_portal = portal;
	}

	public Aggregation createContext(
		AcceptLanguage acceptLanguage, String[] aggregationTermsArray,
		EntityModel entityModel) {

		if ((aggregationTermsArray == null) || (entityModel == null)) {
			return null;
		}

		Map<String, EntityField> entityFieldsMap =
			entityModel.getEntityFieldsMap();

		Aggregation aggregation = new Aggregation();

		Map<String, String> aggregationTerms =
			aggregation.getAggregationTerms();

		for (String aggregationTerm : aggregationTermsArray) {
			String[] aggregationTermParts = aggregationTerm.split("/");

			if (!entityFieldsMap.containsKey(aggregationTermParts[0])) {
				aggregationTerms.put(aggregationTerm, aggregationTerm);

				continue;
			}

			EntityField entityField = entityFieldsMap.get(
				aggregationTermParts[0]);

			if (EntityField.Type.COLLECTION.equals(entityField.getType())) {
				CollectionEntityField collectionEntityField =
					(CollectionEntityField)entityField;

				entityField = collectionEntityField.getEntityField();
			}
			else if (EntityField.Type.COMPLEX.equals(entityField.getType())) {
				ComplexEntityField complexEntityField =
					(ComplexEntityField)entityField;

				Map<String, EntityField> complexEntityFieldsMap =
					complexEntityField.getEntityFieldsMap();

				entityField = complexEntityFieldsMap.get(
					aggregationTermParts[1]);
			}

			if (entityField != null) {
				aggregationTerms.put(
					aggregationTerm,
					_getAggregationTermValue(acceptLanguage, entityField));
			}
		}

		return aggregation;
	}

	@Override
	public Aggregation createContext(Message message) {
		try {
			HttpServletRequest httpServletRequest =
				ContextProviderUtil.getHttpServletRequest(message);

			return createContext(
				new AcceptLanguageImpl(httpServletRequest, _language, _portal),
				ParamUtil.getStringValues(
					httpServletRequest, "aggregationTerms"),
				ContextProviderUtil.getEntityModel(message));
		}
		catch (Exception exception) {
			throw new ServerErrorException(500, exception);
		}
	}

	private String _getAggregationTermValue(
		AcceptLanguage acceptLanguage, EntityField entityField) {

		String aggregationTermValue = entityField.getFilterableName(
			acceptLanguage.getPreferredLocale());

		if (aggregationTermValue.startsWith("expando")) {
			aggregationTermValue += ".raw";
		}

		return aggregationTermValue;
	}

	private final Language _language;
	private final Portal _portal;

}