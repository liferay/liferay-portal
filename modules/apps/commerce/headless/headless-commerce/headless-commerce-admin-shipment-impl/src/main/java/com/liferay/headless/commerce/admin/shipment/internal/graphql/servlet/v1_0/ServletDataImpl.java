/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.admin.shipment.internal.graphql.servlet.v1_0;

import com.liferay.headless.commerce.admin.shipment.internal.graphql.mutation.v1_0.Mutation;
import com.liferay.headless.commerce.admin.shipment.internal.graphql.query.v1_0.Query;
import com.liferay.headless.commerce.admin.shipment.internal.resource.v1_0.ShipmentItemResourceImpl;
import com.liferay.headless.commerce.admin.shipment.internal.resource.v1_0.ShipmentResourceImpl;
import com.liferay.headless.commerce.admin.shipment.internal.resource.v1_0.ShippingAddressResourceImpl;
import com.liferay.headless.commerce.admin.shipment.resource.v1_0.ShipmentItemResource;
import com.liferay.headless.commerce.admin.shipment.resource.v1_0.ShipmentResource;
import com.liferay.headless.commerce.admin.shipment.resource.v1_0.ShippingAddressResource;
import com.liferay.portal.kernel.util.ObjectValuePair;
import com.liferay.portal.vulcan.graphql.servlet.ServletData;

import java.util.HashMap;
import java.util.Map;

import jakarta.annotation.Generated;

import org.osgi.framework.BundleContext;
import org.osgi.service.component.ComponentServiceObjects;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceScope;

/**
 * @author Andrea Sbarra
 * @generated
 */
@Component(service = ServletData.class)
@Generated("")
public class ServletDataImpl implements ServletData {

	@Activate
	public void activate(BundleContext bundleContext) {
		Mutation.setShipmentResourceComponentServiceObjects(
			_shipmentResourceComponentServiceObjects);
		Mutation.setShipmentItemResourceComponentServiceObjects(
			_shipmentItemResourceComponentServiceObjects);
		Mutation.setShippingAddressResourceComponentServiceObjects(
			_shippingAddressResourceComponentServiceObjects);

		Query.setShipmentResourceComponentServiceObjects(
			_shipmentResourceComponentServiceObjects);
		Query.setShipmentItemResourceComponentServiceObjects(
			_shipmentItemResourceComponentServiceObjects);
		Query.setShippingAddressResourceComponentServiceObjects(
			_shippingAddressResourceComponentServiceObjects);
	}

	public String getApplicationName() {
		return "Liferay.Headless.Commerce.Admin.Shipment";
	}

	@Override
	public Mutation getMutation() {
		return new Mutation();
	}

	@Override
	public String getPath() {
		return "/headless-commerce-admin-shipment-graphql/v1_0";
	}

	@Override
	public Query getQuery() {
		return new Query();
	}

	public ObjectValuePair<Class<?>, String> getResourceMethodObjectValuePair(
		String methodName, boolean mutation) {

		if (mutation) {
			return _resourceMethodObjectValuePairs.get(
				"mutation#" + methodName);
		}

		return _resourceMethodObjectValuePairs.get("query#" + methodName);
	}

	private static final Map<String, ObjectValuePair<Class<?>, String>>
		_resourceMethodObjectValuePairs =
			new HashMap<String, ObjectValuePair<Class<?>, String>>() {
				{
					put(
						"mutation#createShipmentsPageExportBatch",
						new ObjectValuePair<>(
							ShipmentResourceImpl.class,
							"postShipmentsPageExportBatch"));
					put(
						"mutation#createShipment",
						new ObjectValuePair<>(
							ShipmentResourceImpl.class, "postShipment"));
					put(
						"mutation#createShipmentBatch",
						new ObjectValuePair<>(
							ShipmentResourceImpl.class, "postShipmentBatch"));
					put(
						"mutation#deleteShipmentByExternalReferenceCode",
						new ObjectValuePair<>(
							ShipmentResourceImpl.class,
							"deleteShipmentByExternalReferenceCode"));
					put(
						"mutation#patchShipmentByExternalReferenceCode",
						new ObjectValuePair<>(
							ShipmentResourceImpl.class,
							"patchShipmentByExternalReferenceCode"));
					put(
						"mutation#updateShipmentByExternalReferenceCode",
						new ObjectValuePair<>(
							ShipmentResourceImpl.class,
							"putShipmentByExternalReferenceCode"));
					put(
						"mutation#createShipmentByExternalReferenceCodeStatusDelivered",
						new ObjectValuePair<>(
							ShipmentResourceImpl.class,
							"postShipmentByExternalReferenceCodeStatusDelivered"));
					put(
						"mutation#createShipmentByExternalReferenceCodeStatusFinishProcessing",
						new ObjectValuePair<>(
							ShipmentResourceImpl.class,
							"postShipmentByExternalReferenceCodeStatusFinishProcessing"));
					put(
						"mutation#createShipmentByExternalReferenceCodeStatusShipped",
						new ObjectValuePair<>(
							ShipmentResourceImpl.class,
							"postShipmentByExternalReferenceCodeStatusShipped"));
					put(
						"mutation#deleteShipment",
						new ObjectValuePair<>(
							ShipmentResourceImpl.class, "deleteShipment"));
					put(
						"mutation#deleteShipmentBatch",
						new ObjectValuePair<>(
							ShipmentResourceImpl.class, "deleteShipmentBatch"));
					put(
						"mutation#patchShipment",
						new ObjectValuePair<>(
							ShipmentResourceImpl.class, "patchShipment"));
					put(
						"mutation#createShipmentStatusDelivered",
						new ObjectValuePair<>(
							ShipmentResourceImpl.class,
							"postShipmentStatusDelivered"));
					put(
						"mutation#createShipmentStatusFinishProcessing",
						new ObjectValuePair<>(
							ShipmentResourceImpl.class,
							"postShipmentStatusFinishProcessing"));
					put(
						"mutation#createShipmentStatusShipped",
						new ObjectValuePair<>(
							ShipmentResourceImpl.class,
							"postShipmentStatusShipped"));
					put(
						"mutation#deleteShipmentItemByExternalReferenceCode",
						new ObjectValuePair<>(
							ShipmentItemResourceImpl.class,
							"deleteShipmentItemByExternalReferenceCode"));
					put(
						"mutation#patchShipmentItemByExternalReferenceCode",
						new ObjectValuePair<>(
							ShipmentItemResourceImpl.class,
							"patchShipmentItemByExternalReferenceCode"));
					put(
						"mutation#deleteShipmentItem",
						new ObjectValuePair<>(
							ShipmentItemResourceImpl.class,
							"deleteShipmentItem"));
					put(
						"mutation#deleteShipmentItemBatch",
						new ObjectValuePair<>(
							ShipmentItemResourceImpl.class,
							"deleteShipmentItemBatch"));
					put(
						"mutation#patchShipmentItem",
						new ObjectValuePair<>(
							ShipmentItemResourceImpl.class,
							"patchShipmentItem"));
					put(
						"mutation#createShipmentItemByExternalReferenceCode",
						new ObjectValuePair<>(
							ShipmentItemResourceImpl.class,
							"postShipmentItemByExternalReferenceCode"));
					put(
						"mutation#updateShipmentByExternalReferenceCodeItem",
						new ObjectValuePair<>(
							ShipmentItemResourceImpl.class,
							"putShipmentByExternalReferenceCodeItem"));
					put(
						"mutation#createShipmentItem",
						new ObjectValuePair<>(
							ShipmentItemResourceImpl.class,
							"postShipmentItem"));
					put(
						"mutation#patchShipmentByExternalReferenceCodeShippingAddress",
						new ObjectValuePair<>(
							ShippingAddressResourceImpl.class,
							"patchShipmentByExternalReferenceCodeShippingAddress"));
					put(
						"mutation#patchShipmentShippingAddress",
						new ObjectValuePair<>(
							ShippingAddressResourceImpl.class,
							"patchShipmentShippingAddress"));

					put(
						"query#shipments",
						new ObjectValuePair<>(
							ShipmentResourceImpl.class, "getShipmentsPage"));
					put(
						"query#shipmentByExternalReferenceCode",
						new ObjectValuePair<>(
							ShipmentResourceImpl.class,
							"getShipmentByExternalReferenceCode"));
					put(
						"query#shipment",
						new ObjectValuePair<>(
							ShipmentResourceImpl.class, "getShipment"));
					put(
						"query#shipmentByExternalReferenceCodeItem",
						new ObjectValuePair<>(
							ShipmentItemResourceImpl.class,
							"getShipmentByExternalReferenceCodeItem"));
					put(
						"query#shipmentItem",
						new ObjectValuePair<>(
							ShipmentItemResourceImpl.class, "getShipmentItem"));
					put(
						"query#shipmentByExternalReferenceCodeItems",
						new ObjectValuePair<>(
							ShipmentItemResourceImpl.class,
							"getShipmentByExternalReferenceCodeItemsPage"));
					put(
						"query#shipmentItems",
						new ObjectValuePair<>(
							ShipmentItemResourceImpl.class,
							"getShipmentItemsPage"));
					put(
						"query#shipmentByExternalReferenceCodeShippingAddress",
						new ObjectValuePair<>(
							ShippingAddressResourceImpl.class,
							"getShipmentByExternalReferenceCodeShippingAddress"));
					put(
						"query#shipmentShippingAddress",
						new ObjectValuePair<>(
							ShippingAddressResourceImpl.class,
							"getShipmentShippingAddress"));

					put(
						"query#Shipment.byExternalReferenceCodeItem",
						new ObjectValuePair<>(
							ShipmentItemResourceImpl.class,
							"getShipmentByExternalReferenceCodeItem"));
					put(
						"query#Shipment.byExternalReferenceCodeItems",
						new ObjectValuePair<>(
							ShipmentItemResourceImpl.class,
							"getShipmentByExternalReferenceCodeItemsPage"));
					put(
						"query#Shipment.byExternalReferenceCodeShippingAddress",
						new ObjectValuePair<>(
							ShippingAddressResourceImpl.class,
							"getShipmentByExternalReferenceCodeShippingAddress"));
					put(
						"query#ShipmentItem.shipment",
						new ObjectValuePair<>(
							ShipmentResourceImpl.class, "getShipment"));
					put(
						"query#ShipmentItem.shipmentByExternalReferenceCode",
						new ObjectValuePair<>(
							ShipmentResourceImpl.class,
							"getShipmentByExternalReferenceCode"));
					put(
						"query#Shipment.items",
						new ObjectValuePair<>(
							ShipmentItemResourceImpl.class,
							"getShipmentItemsPage"));
				}
			};

	@Reference(scope = ReferenceScope.PROTOTYPE_REQUIRED)
	private ComponentServiceObjects<ShipmentResource>
		_shipmentResourceComponentServiceObjects;

	@Reference(scope = ReferenceScope.PROTOTYPE_REQUIRED)
	private ComponentServiceObjects<ShipmentItemResource>
		_shipmentItemResourceComponentServiceObjects;

	@Reference(scope = ReferenceScope.PROTOTYPE_REQUIRED)
	private ComponentServiceObjects<ShippingAddressResource>
		_shippingAddressResourceComponentServiceObjects;

}