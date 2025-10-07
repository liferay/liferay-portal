/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.admin.shipment.internal.graphql.query.v1_0;

import com.liferay.headless.commerce.admin.shipment.dto.v1_0.Shipment;
import com.liferay.headless.commerce.admin.shipment.dto.v1_0.ShipmentItem;
import com.liferay.headless.commerce.admin.shipment.dto.v1_0.ShippingAddress;
import com.liferay.headless.commerce.admin.shipment.resource.v1_0.ShipmentItemResource;
import com.liferay.headless.commerce.admin.shipment.resource.v1_0.ShipmentResource;
import com.liferay.headless.commerce.admin.shipment.resource.v1_0.ShippingAddressResource;
import com.liferay.petra.function.UnsafeConsumer;
import com.liferay.petra.function.UnsafeFunction;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.ResourceActionLocalService;
import com.liferay.portal.kernel.service.ResourcePermissionLocalService;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.vulcan.accept.language.AcceptLanguage;
import com.liferay.portal.vulcan.graphql.annotation.GraphQLField;
import com.liferay.portal.vulcan.graphql.annotation.GraphQLName;
import com.liferay.portal.vulcan.graphql.annotation.GraphQLTypeExtension;
import com.liferay.portal.vulcan.pagination.Page;
import com.liferay.portal.vulcan.pagination.Pagination;

import jakarta.annotation.Generated;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import jakarta.ws.rs.core.UriInfo;

import java.util.Map;
import java.util.function.BiFunction;

import org.osgi.service.component.ComponentServiceObjects;

/**
 * @author Andrea Sbarra
 * @generated
 */
@Generated("")
public class Query {

	public static void setShipmentResourceComponentServiceObjects(
		ComponentServiceObjects<ShipmentResource>
			shipmentResourceComponentServiceObjects) {

		_shipmentResourceComponentServiceObjects =
			shipmentResourceComponentServiceObjects;
	}

	public static void setShipmentItemResourceComponentServiceObjects(
		ComponentServiceObjects<ShipmentItemResource>
			shipmentItemResourceComponentServiceObjects) {

		_shipmentItemResourceComponentServiceObjects =
			shipmentItemResourceComponentServiceObjects;
	}

	public static void setShippingAddressResourceComponentServiceObjects(
		ComponentServiceObjects<ShippingAddressResource>
			shippingAddressResourceComponentServiceObjects) {

		_shippingAddressResourceComponentServiceObjects =
			shippingAddressResourceComponentServiceObjects;
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {shipment(shipmentId: ___){accountId, actions, carrier, createDate, customFields, expectedDate, externalReferenceCode, id, modifiedDate, orderExternalReferenceCode, orderId, shipmentItems, shippingAddress, shippingAddressId, shippingDate, shippingMethodId, shippingOptionName, status, trackingNumber, trackingURL, userName}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public Shipment shipment(@GraphQLName("shipmentId") Long shipmentId)
		throws Exception {

		return _applyComponentServiceObjects(
			_shipmentResourceComponentServiceObjects,
			this::_populateResourceContext,
			shipmentResource -> shipmentResource.getShipment(shipmentId));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {shipmentByExternalReferenceCode(externalReferenceCode: ___){accountId, actions, carrier, createDate, customFields, expectedDate, externalReferenceCode, id, modifiedDate, orderExternalReferenceCode, orderId, shipmentItems, shippingAddress, shippingAddressId, shippingDate, shippingMethodId, shippingOptionName, status, trackingNumber, trackingURL, userName}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField(description = "Retrive information of the given Shipment.")
	public Shipment shipmentByExternalReferenceCode(
			@GraphQLName("externalReferenceCode") String externalReferenceCode)
		throws Exception {

		return _applyComponentServiceObjects(
			_shipmentResourceComponentServiceObjects,
			this::_populateResourceContext,
			shipmentResource ->
				shipmentResource.getShipmentByExternalReferenceCode(
					externalReferenceCode));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {shipments(filter: ___, page: ___, pageSize: ___, search: ___, sorts: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public ShipmentPage shipments(
			@GraphQLName("search") String search,
			@GraphQLName("filter") String filterString,
			@GraphQLName("pageSize") int pageSize,
			@GraphQLName("page") int page,
			@GraphQLName("sort") String sortsString)
		throws Exception {

		return _applyComponentServiceObjects(
			_shipmentResourceComponentServiceObjects,
			this::_populateResourceContext,
			shipmentResource -> new ShipmentPage(
				shipmentResource.getShipmentsPage(
					search,
					_filterBiFunction.apply(shipmentResource, filterString),
					Pagination.of(page, pageSize),
					_sortsBiFunction.apply(shipmentResource, sortsString))));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {shipmentByExternalReferenceCodeItem(externalReferenceCode: ___){actions, createDate, externalReferenceCode, id, modifiedDate, orderItemExternalReferenceCode, orderItemId, quantity, shipmentExternalReferenceCode, shipmentId, unitOfMeasureKey, userName, validateInventory, warehouseExternalReferenceCode, warehouseId}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public ShipmentItem shipmentByExternalReferenceCodeItem(
			@GraphQLName("externalReferenceCode") String externalReferenceCode)
		throws Exception {

		return _applyComponentServiceObjects(
			_shipmentItemResourceComponentServiceObjects,
			this::_populateResourceContext,
			shipmentItemResource ->
				shipmentItemResource.getShipmentByExternalReferenceCodeItem(
					externalReferenceCode));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {shipmentByExternalReferenceCodeItems(externalReferenceCode: ___, page: ___, pageSize: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public ShipmentItemPage shipmentByExternalReferenceCodeItems(
			@GraphQLName("externalReferenceCode") String externalReferenceCode,
			@GraphQLName("pageSize") int pageSize,
			@GraphQLName("page") int page)
		throws Exception {

		return _applyComponentServiceObjects(
			_shipmentItemResourceComponentServiceObjects,
			this::_populateResourceContext,
			shipmentItemResource -> new ShipmentItemPage(
				shipmentItemResource.
					getShipmentByExternalReferenceCodeItemsPage(
						externalReferenceCode, Pagination.of(page, pageSize))));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {shipmentItem(shipmentItemId: ___){actions, createDate, externalReferenceCode, id, modifiedDate, orderItemExternalReferenceCode, orderItemId, quantity, shipmentExternalReferenceCode, shipmentId, unitOfMeasureKey, userName, validateInventory, warehouseExternalReferenceCode, warehouseId}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public ShipmentItem shipmentItem(
			@GraphQLName("shipmentItemId") Long shipmentItemId)
		throws Exception {

		return _applyComponentServiceObjects(
			_shipmentItemResourceComponentServiceObjects,
			this::_populateResourceContext,
			shipmentItemResource -> shipmentItemResource.getShipmentItem(
				shipmentItemId));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {shipmentItems(page: ___, pageSize: ___, shipmentId: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public ShipmentItemPage shipmentItems(
			@GraphQLName("shipmentId") Long shipmentId,
			@GraphQLName("pageSize") int pageSize,
			@GraphQLName("page") int page)
		throws Exception {

		return _applyComponentServiceObjects(
			_shipmentItemResourceComponentServiceObjects,
			this::_populateResourceContext,
			shipmentItemResource -> new ShipmentItemPage(
				shipmentItemResource.getShipmentItemsPage(
					shipmentId, Pagination.of(page, pageSize))));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {shipmentByExternalReferenceCodeShippingAddress(externalReferenceCode: ___){city, countryISOCode, description, externalReferenceCode, id, latitude, longitude, name, phoneNumber, regionISOCode, street1, street2, street3, zip}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public ShippingAddress shipmentByExternalReferenceCodeShippingAddress(
			@GraphQLName("externalReferenceCode") String externalReferenceCode)
		throws Exception {

		return _applyComponentServiceObjects(
			_shippingAddressResourceComponentServiceObjects,
			this::_populateResourceContext,
			shippingAddressResource ->
				shippingAddressResource.
					getShipmentByExternalReferenceCodeShippingAddress(
						externalReferenceCode));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {shipmentShippingAddress(shipmentId: ___){city, countryISOCode, description, externalReferenceCode, id, latitude, longitude, name, phoneNumber, regionISOCode, street1, street2, street3, zip}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public ShippingAddress shipmentShippingAddress(
			@GraphQLName("shipmentId") Long shipmentId)
		throws Exception {

		return _applyComponentServiceObjects(
			_shippingAddressResourceComponentServiceObjects,
			this::_populateResourceContext,
			shippingAddressResource ->
				shippingAddressResource.getShipmentShippingAddress(shipmentId));
	}

	@GraphQLTypeExtension(Shipment.class)
	public class GetShipmentByExternalReferenceCodeItemTypeExtension {

		public GetShipmentByExternalReferenceCodeItemTypeExtension(
			Shipment shipment) {

			_shipment = shipment;
		}

		@GraphQLField
		public ShipmentItem byExternalReferenceCodeItem() throws Exception {
			return _applyComponentServiceObjects(
				_shipmentItemResourceComponentServiceObjects,
				Query.this::_populateResourceContext,
				shipmentItemResource ->
					shipmentItemResource.getShipmentByExternalReferenceCodeItem(
						_shipment.getExternalReferenceCode()));
		}

		private Shipment _shipment;

	}

	@GraphQLTypeExtension(Shipment.class)
	public class GetShipmentByExternalReferenceCodeItemsPageTypeExtension {

		public GetShipmentByExternalReferenceCodeItemsPageTypeExtension(
			Shipment shipment) {

			_shipment = shipment;
		}

		@GraphQLField
		public ShipmentItemPage byExternalReferenceCodeItems(
				@GraphQLName("pageSize") int pageSize,
				@GraphQLName("page") int page)
			throws Exception {

			return _applyComponentServiceObjects(
				_shipmentItemResourceComponentServiceObjects,
				Query.this::_populateResourceContext,
				shipmentItemResource -> new ShipmentItemPage(
					shipmentItemResource.
						getShipmentByExternalReferenceCodeItemsPage(
							_shipment.getExternalReferenceCode(),
							Pagination.of(page, pageSize))));
		}

		private Shipment _shipment;

	}

	@GraphQLTypeExtension(Shipment.class)
	public class
		GetShipmentByExternalReferenceCodeShippingAddressTypeExtension {

		public GetShipmentByExternalReferenceCodeShippingAddressTypeExtension(
			Shipment shipment) {

			_shipment = shipment;
		}

		@GraphQLField
		public ShippingAddress byExternalReferenceCodeShippingAddress()
			throws Exception {

			return _applyComponentServiceObjects(
				_shippingAddressResourceComponentServiceObjects,
				Query.this::_populateResourceContext,
				shippingAddressResource ->
					shippingAddressResource.
						getShipmentByExternalReferenceCodeShippingAddress(
							_shipment.getExternalReferenceCode()));
		}

		private Shipment _shipment;

	}

	@GraphQLTypeExtension(ShipmentItem.class)
	public class GetShipmentTypeExtension {

		public GetShipmentTypeExtension(ShipmentItem shipmentItem) {
			_shipmentItem = shipmentItem;
		}

		@GraphQLField
		public Shipment shipment() throws Exception {
			return _applyComponentServiceObjects(
				_shipmentResourceComponentServiceObjects,
				Query.this::_populateResourceContext,
				shipmentResource -> shipmentResource.getShipment(
					_shipmentItem.getShipmentId()));
		}

		private ShipmentItem _shipmentItem;

	}

	@GraphQLTypeExtension(ShipmentItem.class)
	public class GetShipmentByExternalReferenceCodeTypeExtension {

		public GetShipmentByExternalReferenceCodeTypeExtension(
			ShipmentItem shipmentItem) {

			_shipmentItem = shipmentItem;
		}

		@GraphQLField(
			description = "Retrive information of the given Shipment."
		)
		public Shipment shipmentByExternalReferenceCode() throws Exception {
			return _applyComponentServiceObjects(
				_shipmentResourceComponentServiceObjects,
				Query.this::_populateResourceContext,
				shipmentResource ->
					shipmentResource.getShipmentByExternalReferenceCode(
						_shipmentItem.getExternalReferenceCode()));
		}

		private ShipmentItem _shipmentItem;

	}

	@GraphQLTypeExtension(Shipment.class)
	public class GetShipmentItemsPageTypeExtension {

		public GetShipmentItemsPageTypeExtension(Shipment shipment) {
			_shipment = shipment;
		}

		@GraphQLField
		public ShipmentItemPage items(
				@GraphQLName("pageSize") int pageSize,
				@GraphQLName("page") int page)
			throws Exception {

			return _applyComponentServiceObjects(
				_shipmentItemResourceComponentServiceObjects,
				Query.this::_populateResourceContext,
				shipmentItemResource -> new ShipmentItemPage(
					shipmentItemResource.getShipmentItemsPage(
						_shipment.getId(), Pagination.of(page, pageSize))));
		}

		private Shipment _shipment;

	}

	@GraphQLName("ShipmentPage")
	public class ShipmentPage {

		public ShipmentPage(Page shipmentPage) {
			actions = shipmentPage.getActions();

			items = shipmentPage.getItems();
			lastPage = shipmentPage.getLastPage();
			page = shipmentPage.getPage();
			pageSize = shipmentPage.getPageSize();
			totalCount = shipmentPage.getTotalCount();
		}

		@GraphQLField
		protected Map<String, Map<String, String>> actions;

		@GraphQLField
		protected java.util.Collection<Shipment> items;

		@GraphQLField
		protected long lastPage;

		@GraphQLField
		protected long page;

		@GraphQLField
		protected long pageSize;

		@GraphQLField
		protected long totalCount;

	}

	@GraphQLName("ShipmentItemPage")
	public class ShipmentItemPage {

		public ShipmentItemPage(Page shipmentItemPage) {
			actions = shipmentItemPage.getActions();

			items = shipmentItemPage.getItems();
			lastPage = shipmentItemPage.getLastPage();
			page = shipmentItemPage.getPage();
			pageSize = shipmentItemPage.getPageSize();
			totalCount = shipmentItemPage.getTotalCount();
		}

		@GraphQLField
		protected Map<String, Map<String, String>> actions;

		@GraphQLField
		protected java.util.Collection<ShipmentItem> items;

		@GraphQLField
		protected long lastPage;

		@GraphQLField
		protected long page;

		@GraphQLField
		protected long pageSize;

		@GraphQLField
		protected long totalCount;

	}

	@GraphQLName("ShippingAddressPage")
	public class ShippingAddressPage {

		public ShippingAddressPage(Page shippingAddressPage) {
			actions = shippingAddressPage.getActions();

			items = shippingAddressPage.getItems();
			lastPage = shippingAddressPage.getLastPage();
			page = shippingAddressPage.getPage();
			pageSize = shippingAddressPage.getPageSize();
			totalCount = shippingAddressPage.getTotalCount();
		}

		@GraphQLField
		protected Map<String, Map<String, String>> actions;

		@GraphQLField
		protected java.util.Collection<ShippingAddress> items;

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

	private void _populateResourceContext(ShipmentResource shipmentResource)
		throws Exception {

		shipmentResource.setContextAcceptLanguage(_acceptLanguage);
		shipmentResource.setContextCompany(_company);
		shipmentResource.setContextHttpServletRequest(_httpServletRequest);
		shipmentResource.setContextHttpServletResponse(_httpServletResponse);
		shipmentResource.setContextUriInfo(_uriInfo);
		shipmentResource.setContextUser(_user);
		shipmentResource.setGroupLocalService(_groupLocalService);
		shipmentResource.setResourceActionLocalService(
			_resourceActionLocalService);
		shipmentResource.setResourcePermissionLocalService(
			_resourcePermissionLocalService);
		shipmentResource.setRoleLocalService(_roleLocalService);
	}

	private void _populateResourceContext(
			ShipmentItemResource shipmentItemResource)
		throws Exception {

		shipmentItemResource.setContextAcceptLanguage(_acceptLanguage);
		shipmentItemResource.setContextCompany(_company);
		shipmentItemResource.setContextHttpServletRequest(_httpServletRequest);
		shipmentItemResource.setContextHttpServletResponse(
			_httpServletResponse);
		shipmentItemResource.setContextUriInfo(_uriInfo);
		shipmentItemResource.setContextUser(_user);
		shipmentItemResource.setGroupLocalService(_groupLocalService);
		shipmentItemResource.setResourceActionLocalService(
			_resourceActionLocalService);
		shipmentItemResource.setResourcePermissionLocalService(
			_resourcePermissionLocalService);
		shipmentItemResource.setRoleLocalService(_roleLocalService);
	}

	private void _populateResourceContext(
			ShippingAddressResource shippingAddressResource)
		throws Exception {

		shippingAddressResource.setContextAcceptLanguage(_acceptLanguage);
		shippingAddressResource.setContextCompany(_company);
		shippingAddressResource.setContextHttpServletRequest(
			_httpServletRequest);
		shippingAddressResource.setContextHttpServletResponse(
			_httpServletResponse);
		shippingAddressResource.setContextUriInfo(_uriInfo);
		shippingAddressResource.setContextUser(_user);
		shippingAddressResource.setGroupLocalService(_groupLocalService);
		shippingAddressResource.setResourceActionLocalService(
			_resourceActionLocalService);
		shippingAddressResource.setResourcePermissionLocalService(
			_resourcePermissionLocalService);
		shippingAddressResource.setRoleLocalService(_roleLocalService);
	}

	private static ComponentServiceObjects<ShipmentResource>
		_shipmentResourceComponentServiceObjects;
	private static ComponentServiceObjects<ShipmentItemResource>
		_shipmentItemResourceComponentServiceObjects;
	private static ComponentServiceObjects<ShippingAddressResource>
		_shippingAddressResourceComponentServiceObjects;

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