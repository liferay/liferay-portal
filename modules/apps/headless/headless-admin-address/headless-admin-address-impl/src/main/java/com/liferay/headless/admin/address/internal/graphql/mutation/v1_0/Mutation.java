/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.address.internal.graphql.mutation.v1_0;

import com.liferay.headless.admin.address.dto.v1_0.Country;
import com.liferay.headless.admin.address.dto.v1_0.Region;
import com.liferay.headless.admin.address.resource.v1_0.CountryResource;
import com.liferay.headless.admin.address.resource.v1_0.RegionResource;
import com.liferay.petra.function.UnsafeConsumer;
import com.liferay.petra.function.UnsafeFunction;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.vulcan.accept.language.AcceptLanguage;
import com.liferay.portal.vulcan.batch.engine.resource.VulcanBatchEngineExportTaskResource;
import com.liferay.portal.vulcan.batch.engine.resource.VulcanBatchEngineImportTaskResource;
import com.liferay.portal.vulcan.graphql.annotation.GraphQLField;
import com.liferay.portal.vulcan.graphql.annotation.GraphQLName;

import jakarta.annotation.Generated;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;

import java.util.function.BiFunction;

import org.osgi.service.component.ComponentServiceObjects;

/**
 * @author Drew Brokke
 * @generated
 */
@Generated("")
public class Mutation {

	public static void setCountryResourceComponentServiceObjects(
		ComponentServiceObjects<CountryResource>
			countryResourceComponentServiceObjects) {

		_countryResourceComponentServiceObjects =
			countryResourceComponentServiceObjects;
	}

	public static void setRegionResourceComponentServiceObjects(
		ComponentServiceObjects<RegionResource>
			regionResourceComponentServiceObjects) {

		_regionResourceComponentServiceObjects =
			regionResourceComponentServiceObjects;
	}

	@GraphQLField
	public Response createCountriesPageExportBatch(
			@GraphQLName("active") Boolean active,
			@GraphQLName("search") String search,
			@GraphQLName("sort") String sortsString,
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("contentType") String contentType,
			@GraphQLName("fieldNames") String fieldNames)
		throws Exception {

		return _applyComponentServiceObjects(
			_countryResourceComponentServiceObjects,
			this::_populateResourceContext,
			countryResource -> countryResource.postCountriesPageExportBatch(
				active, search,
				_sortsBiFunction.apply(countryResource, sortsString),
				callbackURL, contentType, fieldNames));
	}

	@GraphQLField
	public Country createCountry(@GraphQLName("country") Country country)
		throws Exception {

		return _applyComponentServiceObjects(
			_countryResourceComponentServiceObjects,
			this::_populateResourceContext,
			countryResource -> countryResource.postCountry(country));
	}

	@GraphQLField
	public Response createCountryBatch(
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("object") Object object)
		throws Exception {

		return _applyComponentServiceObjects(
			_countryResourceComponentServiceObjects,
			this::_populateResourceContext,
			countryResource -> countryResource.postCountryBatch(
				callbackURL, object));
	}

	@GraphQLField
	public boolean deleteCountry(@GraphQLName("countryId") Long countryId)
		throws Exception {

		_applyVoidComponentServiceObjects(
			_countryResourceComponentServiceObjects,
			this::_populateResourceContext,
			countryResource -> countryResource.deleteCountry(countryId));

		return true;
	}

	@GraphQLField
	public Response deleteCountryBatch(
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("object") Object object)
		throws Exception {

		return _applyComponentServiceObjects(
			_countryResourceComponentServiceObjects,
			this::_populateResourceContext,
			countryResource -> countryResource.deleteCountryBatch(
				callbackURL, object));
	}

	@GraphQLField
	public Country patchCountry(
			@GraphQLName("countryId") Long countryId,
			@GraphQLName("country") Country country)
		throws Exception {

		return _applyComponentServiceObjects(
			_countryResourceComponentServiceObjects,
			this::_populateResourceContext,
			countryResource -> countryResource.patchCountry(
				countryId, country));
	}

	@GraphQLField
	public Country updateCountry(
			@GraphQLName("countryId") Long countryId,
			@GraphQLName("country") Country country)
		throws Exception {

		return _applyComponentServiceObjects(
			_countryResourceComponentServiceObjects,
			this::_populateResourceContext,
			countryResource -> countryResource.putCountry(countryId, country));
	}

	@GraphQLField
	public Response updateCountryBatch(
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("object") Object object)
		throws Exception {

		return _applyComponentServiceObjects(
			_countryResourceComponentServiceObjects,
			this::_populateResourceContext,
			countryResource -> countryResource.putCountryBatch(
				callbackURL, object));
	}

	@GraphQLField
	public Response createCountryRegionsPageExportBatch(
			@GraphQLName("countryId") Long countryId,
			@GraphQLName("active") Boolean active,
			@GraphQLName("search") String search,
			@GraphQLName("sort") String sortsString,
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("contentType") String contentType,
			@GraphQLName("fieldNames") String fieldNames)
		throws Exception {

		return _applyComponentServiceObjects(
			_regionResourceComponentServiceObjects,
			this::_populateResourceContext,
			regionResource -> regionResource.postCountryRegionsPageExportBatch(
				countryId, active, search,
				_sortsBiFunction.apply(regionResource, sortsString),
				callbackURL, contentType, fieldNames));
	}

	@GraphQLField
	public Region createCountryRegion(
			@GraphQLName("countryId") Long countryId,
			@GraphQLName("region") Region region)
		throws Exception {

		return _applyComponentServiceObjects(
			_regionResourceComponentServiceObjects,
			this::_populateResourceContext,
			regionResource -> regionResource.postCountryRegion(
				countryId, region));
	}

	@GraphQLField
	public Response createCountryRegionBatch(
			@GraphQLName("countryId") Long countryId,
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("object") Object object)
		throws Exception {

		return _applyComponentServiceObjects(
			_regionResourceComponentServiceObjects,
			this::_populateResourceContext,
			regionResource -> regionResource.postCountryRegionBatch(
				countryId, callbackURL, object));
	}

	@GraphQLField
	public Response createRegionsPageExportBatch(
			@GraphQLName("active") Boolean active,
			@GraphQLName("search") String search,
			@GraphQLName("sort") String sortsString,
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("contentType") String contentType,
			@GraphQLName("fieldNames") String fieldNames)
		throws Exception {

		return _applyComponentServiceObjects(
			_regionResourceComponentServiceObjects,
			this::_populateResourceContext,
			regionResource -> regionResource.postRegionsPageExportBatch(
				active, search,
				_sortsBiFunction.apply(regionResource, sortsString),
				callbackURL, contentType, fieldNames));
	}

	@GraphQLField
	public boolean deleteRegion(@GraphQLName("regionId") Long regionId)
		throws Exception {

		_applyVoidComponentServiceObjects(
			_regionResourceComponentServiceObjects,
			this::_populateResourceContext,
			regionResource -> regionResource.deleteRegion(regionId));

		return true;
	}

	@GraphQLField
	public Response deleteRegionBatch(
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("object") Object object)
		throws Exception {

		return _applyComponentServiceObjects(
			_regionResourceComponentServiceObjects,
			this::_populateResourceContext,
			regionResource -> regionResource.deleteRegionBatch(
				callbackURL, object));
	}

	@GraphQLField
	public Region patchRegion(
			@GraphQLName("regionId") Long regionId,
			@GraphQLName("region") Region region)
		throws Exception {

		return _applyComponentServiceObjects(
			_regionResourceComponentServiceObjects,
			this::_populateResourceContext,
			regionResource -> regionResource.patchRegion(regionId, region));
	}

	@GraphQLField
	public Region updateRegion(
			@GraphQLName("regionId") Long regionId,
			@GraphQLName("region") Region region)
		throws Exception {

		return _applyComponentServiceObjects(
			_regionResourceComponentServiceObjects,
			this::_populateResourceContext,
			regionResource -> regionResource.putRegion(regionId, region));
	}

	@GraphQLField
	public Response updateRegionBatch(
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("object") Object object)
		throws Exception {

		return _applyComponentServiceObjects(
			_regionResourceComponentServiceObjects,
			this::_populateResourceContext,
			regionResource -> regionResource.putRegionBatch(
				callbackURL, object));
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

	private void _populateResourceContext(CountryResource countryResource)
		throws Exception {

		countryResource.setContextAcceptLanguage(_acceptLanguage);
		countryResource.setContextCompany(_company);
		countryResource.setContextHttpServletRequest(_httpServletRequest);
		countryResource.setContextHttpServletResponse(_httpServletResponse);
		countryResource.setContextUriInfo(_uriInfo);
		countryResource.setContextUser(_user);
		countryResource.setGroupLocalService(_groupLocalService);
		countryResource.setRoleLocalService(_roleLocalService);

		countryResource.setVulcanBatchEngineExportTaskResource(
			_vulcanBatchEngineExportTaskResource);

		countryResource.setVulcanBatchEngineImportTaskResource(
			_vulcanBatchEngineImportTaskResource);
	}

	private void _populateResourceContext(RegionResource regionResource)
		throws Exception {

		regionResource.setContextAcceptLanguage(_acceptLanguage);
		regionResource.setContextCompany(_company);
		regionResource.setContextHttpServletRequest(_httpServletRequest);
		regionResource.setContextHttpServletResponse(_httpServletResponse);
		regionResource.setContextUriInfo(_uriInfo);
		regionResource.setContextUser(_user);
		regionResource.setGroupLocalService(_groupLocalService);
		regionResource.setRoleLocalService(_roleLocalService);

		regionResource.setVulcanBatchEngineExportTaskResource(
			_vulcanBatchEngineExportTaskResource);

		regionResource.setVulcanBatchEngineImportTaskResource(
			_vulcanBatchEngineImportTaskResource);
	}

	private static ComponentServiceObjects<CountryResource>
		_countryResourceComponentServiceObjects;
	private static ComponentServiceObjects<RegionResource>
		_regionResourceComponentServiceObjects;

	private AcceptLanguage _acceptLanguage;
	private com.liferay.portal.kernel.model.Company _company;
	private GroupLocalService _groupLocalService;
	private HttpServletRequest _httpServletRequest;
	private HttpServletResponse _httpServletResponse;
	private RoleLocalService _roleLocalService;
	private BiFunction<Object, String, com.liferay.portal.kernel.search.Sort[]>
		_sortsBiFunction;
	private UriInfo _uriInfo;
	private com.liferay.portal.kernel.model.User _user;
	private VulcanBatchEngineExportTaskResource
		_vulcanBatchEngineExportTaskResource;
	private VulcanBatchEngineImportTaskResource
		_vulcanBatchEngineImportTaskResource;

}