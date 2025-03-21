/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.admin.payment.internal.graphql.servlet.v1_0;

import com.liferay.headless.commerce.admin.payment.internal.graphql.mutation.v1_0.Mutation;
import com.liferay.headless.commerce.admin.payment.internal.graphql.query.v1_0.Query;
import com.liferay.headless.commerce.admin.payment.internal.resource.v1_0.PaymentResourceImpl;
import com.liferay.headless.commerce.admin.payment.resource.v1_0.PaymentResource;
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
 * @author Alessio Antonio Rendina
 * @generated
 */
@Component(service = ServletData.class)
@Generated("")
public class ServletDataImpl implements ServletData {

	@Activate
	public void activate(BundleContext bundleContext) {
		Mutation.setPaymentResourceComponentServiceObjects(
			_paymentResourceComponentServiceObjects);

		Query.setPaymentResourceComponentServiceObjects(
			_paymentResourceComponentServiceObjects);
	}

	public String getApplicationName() {
		return "Liferay.Headless.Commerce.Admin.Payment";
	}

	@Override
	public Mutation getMutation() {
		return new Mutation();
	}

	@Override
	public String getPath() {
		return "/headless-commerce-admin-payment-graphql/v1_0";
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
						"mutation#createPaymentsPageExportBatch",
						new ObjectValuePair<>(
							PaymentResourceImpl.class,
							"postPaymentsPageExportBatch"));
					put(
						"mutation#createPayment",
						new ObjectValuePair<>(
							PaymentResourceImpl.class, "postPayment"));
					put(
						"mutation#createPaymentBatch",
						new ObjectValuePair<>(
							PaymentResourceImpl.class, "postPaymentBatch"));
					put(
						"mutation#deletePaymentByExternalReferenceCode",
						new ObjectValuePair<>(
							PaymentResourceImpl.class,
							"deletePaymentByExternalReferenceCode"));
					put(
						"mutation#patchPaymentByExternalReferenceCode",
						new ObjectValuePair<>(
							PaymentResourceImpl.class,
							"patchPaymentByExternalReferenceCode"));
					put(
						"mutation#updatePaymentByExternalReferenceCode",
						new ObjectValuePair<>(
							PaymentResourceImpl.class,
							"putPaymentByExternalReferenceCode"));
					put(
						"mutation#createPaymentByExternalReferenceCodeRefund",
						new ObjectValuePair<>(
							PaymentResourceImpl.class,
							"postPaymentByExternalReferenceCodeRefund"));
					put(
						"mutation#deletePayment",
						new ObjectValuePair<>(
							PaymentResourceImpl.class, "deletePayment"));
					put(
						"mutation#deletePaymentBatch",
						new ObjectValuePair<>(
							PaymentResourceImpl.class, "deletePaymentBatch"));
					put(
						"mutation#patchPayment",
						new ObjectValuePair<>(
							PaymentResourceImpl.class, "patchPayment"));
					put(
						"mutation#createPaymentRefund",
						new ObjectValuePair<>(
							PaymentResourceImpl.class, "postPaymentRefund"));

					put(
						"query#payments",
						new ObjectValuePair<>(
							PaymentResourceImpl.class, "getPaymentsPage"));
					put(
						"query#paymentByExternalReferenceCode",
						new ObjectValuePair<>(
							PaymentResourceImpl.class,
							"getPaymentByExternalReferenceCode"));
					put(
						"query#payment",
						new ObjectValuePair<>(
							PaymentResourceImpl.class, "getPayment"));
				}
			};

	@Reference(scope = ReferenceScope.PROTOTYPE_REQUIRED)
	private ComponentServiceObjects<PaymentResource>
		_paymentResourceComponentServiceObjects;

}