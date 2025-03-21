/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.admin.payment.internal.graphql.mutation.v1_0;

import com.liferay.headless.commerce.admin.payment.dto.v1_0.Payment;
import com.liferay.headless.commerce.admin.payment.resource.v1_0.PaymentResource;
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
 * @author Alessio Antonio Rendina
 * @generated
 */
@Generated("")
public class Mutation {

	public static void setPaymentResourceComponentServiceObjects(
		ComponentServiceObjects<PaymentResource>
			paymentResourceComponentServiceObjects) {

		_paymentResourceComponentServiceObjects =
			paymentResourceComponentServiceObjects;
	}

	@GraphQLField
	public Response createPaymentsPageExportBatch(
			@GraphQLName("search") String search,
			@GraphQLName("filter") String filterString,
			@GraphQLName("sort") String sortsString,
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("contentType") String contentType,
			@GraphQLName("fieldNames") String fieldNames)
		throws Exception {

		return _applyComponentServiceObjects(
			_paymentResourceComponentServiceObjects,
			this::_populateResourceContext,
			paymentResource -> paymentResource.postPaymentsPageExportBatch(
				search, _filterBiFunction.apply(paymentResource, filterString),
				_sortsBiFunction.apply(paymentResource, sortsString),
				callbackURL, contentType, fieldNames));
	}

	@GraphQLField
	public Payment createPayment(@GraphQLName("payment") Payment payment)
		throws Exception {

		return _applyComponentServiceObjects(
			_paymentResourceComponentServiceObjects,
			this::_populateResourceContext,
			paymentResource -> paymentResource.postPayment(payment));
	}

	@GraphQLField
	public Response createPaymentBatch(
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("object") Object object)
		throws Exception {

		return _applyComponentServiceObjects(
			_paymentResourceComponentServiceObjects,
			this::_populateResourceContext,
			paymentResource -> paymentResource.postPaymentBatch(
				callbackURL, object));
	}

	@GraphQLField
	public Response deletePaymentByExternalReferenceCode(
			@GraphQLName("externalReferenceCode") String externalReferenceCode)
		throws Exception {

		return _applyComponentServiceObjects(
			_paymentResourceComponentServiceObjects,
			this::_populateResourceContext,
			paymentResource ->
				paymentResource.deletePaymentByExternalReferenceCode(
					externalReferenceCode));
	}

	@GraphQLField
	public Payment patchPaymentByExternalReferenceCode(
			@GraphQLName("externalReferenceCode") String externalReferenceCode,
			@GraphQLName("payment") Payment payment)
		throws Exception {

		return _applyComponentServiceObjects(
			_paymentResourceComponentServiceObjects,
			this::_populateResourceContext,
			paymentResource ->
				paymentResource.patchPaymentByExternalReferenceCode(
					externalReferenceCode, payment));
	}

	@GraphQLField
	public Payment updatePaymentByExternalReferenceCode(
			@GraphQLName("externalReferenceCode") String externalReferenceCode,
			@GraphQLName("payment") Payment payment)
		throws Exception {

		return _applyComponentServiceObjects(
			_paymentResourceComponentServiceObjects,
			this::_populateResourceContext,
			paymentResource ->
				paymentResource.putPaymentByExternalReferenceCode(
					externalReferenceCode, payment));
	}

	@GraphQLField
	public Payment createPaymentByExternalReferenceCodeRefund(
			@GraphQLName("externalReferenceCode") String externalReferenceCode)
		throws Exception {

		return _applyComponentServiceObjects(
			_paymentResourceComponentServiceObjects,
			this::_populateResourceContext,
			paymentResource ->
				paymentResource.postPaymentByExternalReferenceCodeRefund(
					externalReferenceCode));
	}

	@GraphQLField
	public Response deletePayment(@GraphQLName("id") Long id) throws Exception {
		return _applyComponentServiceObjects(
			_paymentResourceComponentServiceObjects,
			this::_populateResourceContext,
			paymentResource -> paymentResource.deletePayment(id));
	}

	@GraphQLField
	public Response deletePaymentBatch(
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("object") Object object)
		throws Exception {

		return _applyComponentServiceObjects(
			_paymentResourceComponentServiceObjects,
			this::_populateResourceContext,
			paymentResource -> paymentResource.deletePaymentBatch(
				callbackURL, object));
	}

	@GraphQLField
	public Payment patchPayment(
			@GraphQLName("id") Long id, @GraphQLName("payment") Payment payment)
		throws Exception {

		return _applyComponentServiceObjects(
			_paymentResourceComponentServiceObjects,
			this::_populateResourceContext,
			paymentResource -> paymentResource.patchPayment(id, payment));
	}

	@GraphQLField
	public Payment createPaymentRefund(@GraphQLName("id") Long id)
		throws Exception {

		return _applyComponentServiceObjects(
			_paymentResourceComponentServiceObjects,
			this::_populateResourceContext,
			paymentResource -> paymentResource.postPaymentRefund(id));
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

	private void _populateResourceContext(PaymentResource paymentResource)
		throws Exception {

		paymentResource.setContextAcceptLanguage(_acceptLanguage);
		paymentResource.setContextCompany(_company);
		paymentResource.setContextHttpServletRequest(_httpServletRequest);
		paymentResource.setContextHttpServletResponse(_httpServletResponse);
		paymentResource.setContextUriInfo(_uriInfo);
		paymentResource.setContextUser(_user);
		paymentResource.setGroupLocalService(_groupLocalService);
		paymentResource.setRoleLocalService(_roleLocalService);

		paymentResource.setVulcanBatchEngineExportTaskResource(
			_vulcanBatchEngineExportTaskResource);

		paymentResource.setVulcanBatchEngineImportTaskResource(
			_vulcanBatchEngineImportTaskResource);
	}

	private static ComponentServiceObjects<PaymentResource>
		_paymentResourceComponentServiceObjects;

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
	private VulcanBatchEngineExportTaskResource
		_vulcanBatchEngineExportTaskResource;
	private VulcanBatchEngineImportTaskResource
		_vulcanBatchEngineImportTaskResource;

}