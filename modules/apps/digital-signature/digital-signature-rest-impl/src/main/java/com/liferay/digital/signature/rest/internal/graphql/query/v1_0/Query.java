/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.digital.signature.rest.internal.graphql.query.v1_0;

import com.liferay.digital.signature.rest.dto.v1_0.DSEnvelope;
import com.liferay.digital.signature.rest.resource.v1_0.DSEnvelopeResource;
import com.liferay.petra.function.UnsafeConsumer;
import com.liferay.petra.function.UnsafeFunction;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.ResourceActionLocalService;
import com.liferay.portal.kernel.service.ResourcePermissionLocalService;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.vulcan.accept.language.AcceptLanguage;
import com.liferay.portal.vulcan.graphql.annotation.GraphQLField;
import com.liferay.portal.vulcan.graphql.annotation.GraphQLName;
import com.liferay.portal.vulcan.pagination.Page;
import com.liferay.portal.vulcan.pagination.Pagination;

import jakarta.annotation.Generated;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import jakarta.validation.constraints.NotEmpty;

import jakarta.ws.rs.core.UriInfo;

import java.util.Map;
import java.util.function.BiFunction;

import org.osgi.service.component.ComponentServiceObjects;

/**
 * @author José Abelenda
 * @generated
 */
@Generated("")
public class Query {

	public static void setDSEnvelopeResourceComponentServiceObjects(
		ComponentServiceObjects<DSEnvelopeResource>
			dsEnvelopeResourceComponentServiceObjects) {

		_dsEnvelopeResourceComponentServiceObjects =
			dsEnvelopeResourceComponentServiceObjects;
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {dSEnvelope(dsEnvelopeId: ___, siteKey: ___){dateCreated, dateModified, dsDocument, dsRecipient, emailBlurb, emailSubject, id, name, senderEmailAddress, siteId, status}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public DSEnvelope dSEnvelope(
			@GraphQLName("siteKey") @NotEmpty String siteKey,
			@GraphQLName("dsEnvelopeId") String dsEnvelopeId)
		throws Exception {

		return _applyComponentServiceObjects(
			_dsEnvelopeResourceComponentServiceObjects,
			this::_populateResourceContext,
			dsEnvelopeResource -> dsEnvelopeResource.getSiteDSEnvelope(
				Long.valueOf(siteKey), dsEnvelopeId));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {dSEnvelopes(fromDate: ___, keywords: ___, order: ___, page: ___, pageSize: ___, siteKey: ___, status: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public DSEnvelopePage dSEnvelopes(
			@GraphQLName("siteKey") @NotEmpty String siteKey,
			@GraphQLName("fromDate") String fromDate,
			@GraphQLName("keywords") String keywords,
			@GraphQLName("order") String order,
			@GraphQLName("status") String status,
			@GraphQLName("pageSize") int pageSize,
			@GraphQLName("page") int page)
		throws Exception {

		return _applyComponentServiceObjects(
			_dsEnvelopeResourceComponentServiceObjects,
			this::_populateResourceContext,
			dsEnvelopeResource -> new DSEnvelopePage(
				dsEnvelopeResource.getSiteDSEnvelopesPage(
					Long.valueOf(siteKey), fromDate, keywords, order, status,
					Pagination.of(page, pageSize))));
	}

	@GraphQLName("DSEnvelopePage")
	public class DSEnvelopePage {

		public DSEnvelopePage(Page dsEnvelopePage) {
			actions = dsEnvelopePage.getActions();

			items = dsEnvelopePage.getItems();
			lastPage = dsEnvelopePage.getLastPage();
			page = dsEnvelopePage.getPage();
			pageSize = dsEnvelopePage.getPageSize();
			totalCount = dsEnvelopePage.getTotalCount();
		}

		@GraphQLField
		protected Map<String, Map<String, String>> actions;

		@GraphQLField
		protected java.util.Collection<DSEnvelope> items;

		@GraphQLField
		protected long lastPage;

		@GraphQLField
		protected long page;

		@GraphQLField
		protected long pageSize;

		@GraphQLField
		protected long totalCount;

	}

	private <T, R, E1 extends Throwable, E2 extends Throwable> R
			_applyComponentServiceObjects(
				ComponentServiceObjects<T> componentServiceObjects,
				UnsafeConsumer<T, E1> unsafeConsumer,
				UnsafeFunction<T, R, E2> unsafeFunction)
		throws E1, E2 {

		T resource = componentServiceObjects.getService();

		try {
			unsafeConsumer.accept(resource);

			return unsafeFunction.apply(resource);
		}
		finally {
			componentServiceObjects.ungetService(resource);
		}
	}

	private void _populateResourceContext(DSEnvelopeResource dsEnvelopeResource)
		throws Exception {

		dsEnvelopeResource.setContextAcceptLanguage(_acceptLanguage);
		dsEnvelopeResource.setContextCompany(_company);
		dsEnvelopeResource.setContextHttpServletRequest(_httpServletRequest);
		dsEnvelopeResource.setContextHttpServletResponse(_httpServletResponse);
		dsEnvelopeResource.setContextUriInfo(_uriInfo);
		dsEnvelopeResource.setContextUser(_user);
		dsEnvelopeResource.setGroupLocalService(_groupLocalService);
		dsEnvelopeResource.setResourceActionLocalService(
			_resourceActionLocalService);
		dsEnvelopeResource.setResourcePermissionLocalService(
			_resourcePermissionLocalService);
		dsEnvelopeResource.setRoleLocalService(_roleLocalService);
	}

	private static ComponentServiceObjects<DSEnvelopeResource>
		_dsEnvelopeResourceComponentServiceObjects;

	private AcceptLanguage _acceptLanguage;
	private com.liferay.portal.kernel.model.Company _company;
	private BiFunction
		<Object, String, com.liferay.portal.kernel.search.filter.Filter>
			_filterBiFunction;
	private GroupLocalService _groupLocalService;
	private HttpServletRequest _httpServletRequest;
	private HttpServletResponse _httpServletResponse;
	private ResourceActionLocalService _resourceActionLocalService;
	private ResourcePermissionLocalService _resourcePermissionLocalService;
	private RoleLocalService _roleLocalService;
	private BiFunction<Object, String, com.liferay.portal.kernel.search.Sort[]>
		_sortsBiFunction;
	private UriInfo _uriInfo;
	private com.liferay.portal.kernel.model.User _user;

}