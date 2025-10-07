/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.cms.internal.graphql.mutation.v1_0;

import com.liferay.headless.cms.dto.v1_0.BulkAction;
import com.liferay.headless.cms.dto.v1_0.BulkActionItem;
import com.liferay.headless.cms.dto.v1_0.BulkActionTask;
import com.liferay.headless.cms.resource.v1_0.BulkActionResource;
import com.liferay.petra.function.UnsafeConsumer;
import com.liferay.petra.function.UnsafeFunction;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.vulcan.accept.language.AcceptLanguage;
import com.liferay.portal.vulcan.graphql.annotation.GraphQLField;
import com.liferay.portal.vulcan.graphql.annotation.GraphQLName;
import com.liferay.portal.vulcan.pagination.Page;
import com.liferay.portal.vulcan.pagination.Pagination;

import jakarta.annotation.Generated;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import jakarta.ws.rs.core.UriInfo;

import java.util.function.BiFunction;

import org.osgi.service.component.ComponentServiceObjects;

/**
 * @author Crescenzo Rega
 * @generated
 */
@Generated("")
public class Mutation {

	public static void setBulkActionResourceComponentServiceObjects(
		ComponentServiceObjects<BulkActionResource>
			bulkActionResourceComponentServiceObjects) {

		_bulkActionResourceComponentServiceObjects =
			bulkActionResourceComponentServiceObjects;
	}

	@GraphQLField(description = "Execute a bulk action")
	public BulkActionTask createBulkAction(
			@GraphQLName("search") String search,
			@GraphQLName("filter") String filterString,
			@GraphQLName("bulkAction") BulkAction bulkAction)
		throws Exception {

		return _applyComponentServiceObjects(
			_bulkActionResourceComponentServiceObjects,
			this::_populateResourceContext,
			bulkActionResource -> bulkActionResource.postBulkAction(
				search,
				_filterBiFunction.apply(bulkActionResource, filterString),
				bulkAction));
	}

	@GraphQLField(
		description = "Creates a preview for each item based on the bulk action type"
	)
	public java.util.Collection<BulkActionItem> createBulkActionItemPreviewPage(
			@GraphQLName("fetchChildren") Boolean fetchChildren,
			@GraphQLName("search") String search,
			@GraphQLName("filter") String filterString,
			@GraphQLName("pageSize") int pageSize,
			@GraphQLName("page") int page,
			@GraphQLName("sort") String sortsString,
			@GraphQLName("bulkAction") BulkAction bulkAction)
		throws Exception {

		return _applyComponentServiceObjects(
			_bulkActionResourceComponentServiceObjects,
			this::_populateResourceContext,
			bulkActionResource -> {
				Page paginationPage =
					bulkActionResource.postBulkActionItemPreviewPage(
						fetchChildren, search,
						_filterBiFunction.apply(
							bulkActionResource, filterString),
						Pagination.of(page, pageSize),
						_sortsBiFunction.apply(bulkActionResource, sortsString),
						bulkAction);

				return paginationPage.getItems();
			});
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

	private <T, E1 extends Throwable, E2 extends Throwable> void
			_applyVoidComponentServiceObjects(
				ComponentServiceObjects<T> componentServiceObjects,
				UnsafeConsumer<T, E1> unsafeConsumer,
				UnsafeConsumer<T, E2> unsafeFunction)
		throws E1, E2 {

		T resource = componentServiceObjects.getService();

		try {
			unsafeConsumer.accept(resource);

			unsafeFunction.accept(resource);
		}
		finally {
			componentServiceObjects.ungetService(resource);
		}
	}

	private void _populateResourceContext(BulkActionResource bulkActionResource)
		throws Exception {

		bulkActionResource.setContextAcceptLanguage(_acceptLanguage);
		bulkActionResource.setContextCompany(_company);
		bulkActionResource.setContextHttpServletRequest(_httpServletRequest);
		bulkActionResource.setContextHttpServletResponse(_httpServletResponse);
		bulkActionResource.setContextUriInfo(_uriInfo);
		bulkActionResource.setContextUser(_user);
		bulkActionResource.setGroupLocalService(_groupLocalService);
		bulkActionResource.setRoleLocalService(_roleLocalService);
	}

	private static ComponentServiceObjects<BulkActionResource>
		_bulkActionResourceComponentServiceObjects;

	private AcceptLanguage _acceptLanguage;
	private com.liferay.portal.kernel.model.Company _company;
	private BiFunction
		<Object, String, com.liferay.portal.kernel.search.filter.Filter>
			_filterBiFunction;
	private GroupLocalService _groupLocalService;
	private HttpServletRequest _httpServletRequest;
	private HttpServletResponse _httpServletResponse;
	private RoleLocalService _roleLocalService;
	private BiFunction<Object, String, com.liferay.portal.kernel.search.Sort[]>
		_sortsBiFunction;
	private UriInfo _uriInfo;
	private com.liferay.portal.kernel.model.User _user;

}