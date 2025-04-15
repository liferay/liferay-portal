/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.delivery.cart.internal.graphql.servlet.v1_0;

import com.liferay.headless.commerce.delivery.cart.internal.graphql.mutation.v1_0.Mutation;
import com.liferay.headless.commerce.delivery.cart.internal.graphql.query.v1_0.Query;
import com.liferay.headless.commerce.delivery.cart.internal.resource.v1_0.AddressResourceImpl;
import com.liferay.headless.commerce.delivery.cart.internal.resource.v1_0.AttachmentResourceImpl;
import com.liferay.headless.commerce.delivery.cart.internal.resource.v1_0.CartCommentResourceImpl;
import com.liferay.headless.commerce.delivery.cart.internal.resource.v1_0.CartItemResourceImpl;
import com.liferay.headless.commerce.delivery.cart.internal.resource.v1_0.CartResourceImpl;
import com.liferay.headless.commerce.delivery.cart.internal.resource.v1_0.CartTransitionResourceImpl;
import com.liferay.headless.commerce.delivery.cart.internal.resource.v1_0.PaymentMethodResourceImpl;
import com.liferay.headless.commerce.delivery.cart.internal.resource.v1_0.ShippingMethodResourceImpl;
import com.liferay.headless.commerce.delivery.cart.internal.resource.v1_0.TermResourceImpl;
import com.liferay.headless.commerce.delivery.cart.resource.v1_0.AddressResource;
import com.liferay.headless.commerce.delivery.cart.resource.v1_0.AttachmentResource;
import com.liferay.headless.commerce.delivery.cart.resource.v1_0.CartCommentResource;
import com.liferay.headless.commerce.delivery.cart.resource.v1_0.CartItemResource;
import com.liferay.headless.commerce.delivery.cart.resource.v1_0.CartResource;
import com.liferay.headless.commerce.delivery.cart.resource.v1_0.CartTransitionResource;
import com.liferay.headless.commerce.delivery.cart.resource.v1_0.PaymentMethodResource;
import com.liferay.headless.commerce.delivery.cart.resource.v1_0.ShippingMethodResource;
import com.liferay.headless.commerce.delivery.cart.resource.v1_0.TermResource;
import com.liferay.portal.kernel.util.ObjectValuePair;
import com.liferay.portal.vulcan.graphql.servlet.ServletData;

import jakarta.annotation.Generated;

import java.util.HashMap;
import java.util.Map;

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
		Mutation.setAttachmentResourceComponentServiceObjects(
			_attachmentResourceComponentServiceObjects);
		Mutation.setCartResourceComponentServiceObjects(
			_cartResourceComponentServiceObjects);
		Mutation.setCartCommentResourceComponentServiceObjects(
			_cartCommentResourceComponentServiceObjects);
		Mutation.setCartItemResourceComponentServiceObjects(
			_cartItemResourceComponentServiceObjects);
		Mutation.setCartTransitionResourceComponentServiceObjects(
			_cartTransitionResourceComponentServiceObjects);
		Mutation.setPaymentMethodResourceComponentServiceObjects(
			_paymentMethodResourceComponentServiceObjects);
		Mutation.setShippingMethodResourceComponentServiceObjects(
			_shippingMethodResourceComponentServiceObjects);

		Query.setAddressResourceComponentServiceObjects(
			_addressResourceComponentServiceObjects);
		Query.setAttachmentResourceComponentServiceObjects(
			_attachmentResourceComponentServiceObjects);
		Query.setCartResourceComponentServiceObjects(
			_cartResourceComponentServiceObjects);
		Query.setCartCommentResourceComponentServiceObjects(
			_cartCommentResourceComponentServiceObjects);
		Query.setCartItemResourceComponentServiceObjects(
			_cartItemResourceComponentServiceObjects);
		Query.setCartTransitionResourceComponentServiceObjects(
			_cartTransitionResourceComponentServiceObjects);
		Query.setPaymentMethodResourceComponentServiceObjects(
			_paymentMethodResourceComponentServiceObjects);
		Query.setShippingMethodResourceComponentServiceObjects(
			_shippingMethodResourceComponentServiceObjects);
		Query.setTermResourceComponentServiceObjects(
			_termResourceComponentServiceObjects);
	}

	public String getApplicationName() {
		return "Liferay.Headless.Commerce.Delivery.Cart";
	}

	@Override
	public Mutation getMutation() {
		return new Mutation();
	}

	@Override
	public String getPath() {
		return "/headless-commerce-delivery-cart-graphql/v1_0";
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
						"mutation#createCartByExternalReferenceCodeAttachmentByBase64",
						new ObjectValuePair<>(
							AttachmentResourceImpl.class,
							"postCartByExternalReferenceCodeAttachmentByBase64"));
					put(
						"mutation#deleteCartByExternalReferenceCodeAttachmentByExternalReferenceCodeAttachmentExternalReferenceCode",
						new ObjectValuePair<>(
							AttachmentResourceImpl.class,
							"deleteCartByExternalReferenceCodeAttachmentByExternalReferenceCodeAttachmentExternalReferenceCode"));
					put(
						"mutation#createCartAttachmentsPageExportBatch",
						new ObjectValuePair<>(
							AttachmentResourceImpl.class,
							"postCartAttachmentsPageExportBatch"));
					put(
						"mutation#createCartAttachmentByBase64",
						new ObjectValuePair<>(
							AttachmentResourceImpl.class,
							"postCartAttachmentByBase64"));
					put(
						"mutation#deleteCartAttachment",
						new ObjectValuePair<>(
							AttachmentResourceImpl.class,
							"deleteCartAttachment"));
					put(
						"mutation#deleteCartByExternalReferenceCode",
						new ObjectValuePair<>(
							CartResourceImpl.class,
							"deleteCartByExternalReferenceCode"));
					put(
						"mutation#patchCartByExternalReferenceCode",
						new ObjectValuePair<>(
							CartResourceImpl.class,
							"patchCartByExternalReferenceCode"));
					put(
						"mutation#updateCartByExternalReferenceCode",
						new ObjectValuePair<>(
							CartResourceImpl.class,
							"putCartByExternalReferenceCode"));
					put(
						"mutation#createCartByExternalReferenceCodeCheckout",
						new ObjectValuePair<>(
							CartResourceImpl.class,
							"postCartByExternalReferenceCodeCheckout"));
					put(
						"mutation#createCartByExternalReferenceCodeCouponCode",
						new ObjectValuePair<>(
							CartResourceImpl.class,
							"postCartByExternalReferenceCodeCouponCode"));
					put(
						"mutation#deleteCart",
						new ObjectValuePair<>(
							CartResourceImpl.class, "deleteCart"));
					put(
						"mutation#deleteCartBatch",
						new ObjectValuePair<>(
							CartResourceImpl.class, "deleteCartBatch"));
					put(
						"mutation#patchCart",
						new ObjectValuePair<>(
							CartResourceImpl.class, "patchCart"));
					put(
						"mutation#updateCart",
						new ObjectValuePair<>(
							CartResourceImpl.class, "putCart"));
					put(
						"mutation#updateCartBatch",
						new ObjectValuePair<>(
							CartResourceImpl.class, "putCartBatch"));
					put(
						"mutation#createCartCheckout",
						new ObjectValuePair<>(
							CartResourceImpl.class, "postCartCheckout"));
					put(
						"mutation#createCartCouponCode",
						new ObjectValuePair<>(
							CartResourceImpl.class, "postCartCouponCode"));
					put(
						"mutation#createChannelCartByExternalReferenceCode",
						new ObjectValuePair<>(
							CartResourceImpl.class,
							"postChannelCartByExternalReferenceCode"));
					put(
						"mutation#createChannelCart",
						new ObjectValuePair<>(
							CartResourceImpl.class, "postChannelCart"));
					put(
						"mutation#deleteCartCommentByExternalReferenceCode",
						new ObjectValuePair<>(
							CartCommentResourceImpl.class,
							"deleteCartCommentByExternalReferenceCode"));
					put(
						"mutation#patchCartCommentByExternalReferenceCode",
						new ObjectValuePair<>(
							CartCommentResourceImpl.class,
							"patchCartCommentByExternalReferenceCode"));
					put(
						"mutation#updateCartCommentByExternalReferenceCode",
						new ObjectValuePair<>(
							CartCommentResourceImpl.class,
							"putCartCommentByExternalReferenceCode"));
					put(
						"mutation#deleteCartComment",
						new ObjectValuePair<>(
							CartCommentResourceImpl.class,
							"deleteCartComment"));
					put(
						"mutation#deleteCartCommentBatch",
						new ObjectValuePair<>(
							CartCommentResourceImpl.class,
							"deleteCartCommentBatch"));
					put(
						"mutation#patchCartComment",
						new ObjectValuePair<>(
							CartCommentResourceImpl.class, "patchCartComment"));
					put(
						"mutation#updateCartComment",
						new ObjectValuePair<>(
							CartCommentResourceImpl.class, "putCartComment"));
					put(
						"mutation#updateCartCommentBatch",
						new ObjectValuePair<>(
							CartCommentResourceImpl.class,
							"putCartCommentBatch"));
					put(
						"mutation#createCartByExternalReferenceCodeComment",
						new ObjectValuePair<>(
							CartCommentResourceImpl.class,
							"postCartByExternalReferenceCodeComment"));
					put(
						"mutation#createCartComment",
						new ObjectValuePair<>(
							CartCommentResourceImpl.class, "postCartComment"));
					put(
						"mutation#deleteCartItemByExternalReferenceCode",
						new ObjectValuePair<>(
							CartItemResourceImpl.class,
							"deleteCartItemByExternalReferenceCode"));
					put(
						"mutation#patchCartItemByExternalReferenceCode",
						new ObjectValuePair<>(
							CartItemResourceImpl.class,
							"patchCartItemByExternalReferenceCode"));
					put(
						"mutation#updateCartItemByExternalReferenceCode",
						new ObjectValuePair<>(
							CartItemResourceImpl.class,
							"putCartItemByExternalReferenceCode"));
					put(
						"mutation#deleteCartItem",
						new ObjectValuePair<>(
							CartItemResourceImpl.class, "deleteCartItem"));
					put(
						"mutation#deleteCartItemBatch",
						new ObjectValuePair<>(
							CartItemResourceImpl.class, "deleteCartItemBatch"));
					put(
						"mutation#patchCartItem",
						new ObjectValuePair<>(
							CartItemResourceImpl.class, "patchCartItem"));
					put(
						"mutation#updateCartItem",
						new ObjectValuePair<>(
							CartItemResourceImpl.class, "putCartItem"));
					put(
						"mutation#updateCartItemBatch",
						new ObjectValuePair<>(
							CartItemResourceImpl.class, "putCartItemBatch"));
					put(
						"mutation#createCartByExternalReferenceCodeItem",
						new ObjectValuePair<>(
							CartItemResourceImpl.class,
							"postCartByExternalReferenceCodeItem"));
					put(
						"mutation#createCartItem",
						new ObjectValuePair<>(
							CartItemResourceImpl.class, "postCartItem"));
					put(
						"mutation#createCartCartTransitionsPageExportBatch",
						new ObjectValuePair<>(
							CartTransitionResourceImpl.class,
							"postCartCartTransitionsPageExportBatch"));
					put(
						"mutation#createCartCartTransition",
						new ObjectValuePair<>(
							CartTransitionResourceImpl.class,
							"postCartCartTransition"));
					put(
						"mutation#createCartCartTransitionBatch",
						new ObjectValuePair<>(
							CartTransitionResourceImpl.class,
							"postCartCartTransitionBatch"));
					put(
						"mutation#createCartPaymentMethodsPageExportBatch",
						new ObjectValuePair<>(
							PaymentMethodResourceImpl.class,
							"postCartPaymentMethodsPageExportBatch"));
					put(
						"mutation#createCartShippingMethodsPageExportBatch",
						new ObjectValuePair<>(
							ShippingMethodResourceImpl.class,
							"postCartShippingMethodsPageExportBatch"));

					put(
						"query#cartByExternalReferenceCodeBillingAddress",
						new ObjectValuePair<>(
							AddressResourceImpl.class,
							"getCartByExternalReferenceCodeBillingAddress"));
					put(
						"query#cartByExternalReferenceCodeShippingAddress",
						new ObjectValuePair<>(
							AddressResourceImpl.class,
							"getCartByExternalReferenceCodeShippingAddress"));
					put(
						"query#cartBillingAddres",
						new ObjectValuePair<>(
							AddressResourceImpl.class, "getCartBillingAddres"));
					put(
						"query#cartShippingAddres",
						new ObjectValuePair<>(
							AddressResourceImpl.class,
							"getCartShippingAddres"));
					put(
						"query#cartByExternalReferenceCodeAttachments",
						new ObjectValuePair<>(
							AttachmentResourceImpl.class,
							"getCartByExternalReferenceCodeAttachmentsPage"));
					put(
						"query#cartAttachments",
						new ObjectValuePair<>(
							AttachmentResourceImpl.class,
							"getCartAttachmentsPage"));
					put(
						"query#cartByExternalReferenceCode",
						new ObjectValuePair<>(
							CartResourceImpl.class,
							"getCartByExternalReferenceCode"));
					put(
						"query#cartByExternalReferenceCodePaymentUrl",
						new ObjectValuePair<>(
							CartResourceImpl.class,
							"getCartByExternalReferenceCodePaymentUrl"));
					put(
						"query#cart",
						new ObjectValuePair<>(
							CartResourceImpl.class, "getCart"));
					put(
						"query#cartPaymentURL",
						new ObjectValuePair<>(
							CartResourceImpl.class, "getCartPaymentURL"));
					put(
						"query#channelByExternalReferenceCodeChannelExternalReferenceCodeAccountByExternalReferenceCodeAccountExternalReferenceCodeCarts",
						new ObjectValuePair<>(
							CartResourceImpl.class,
							"getChannelByExternalReferenceCodeChannelExternalReferenceCodeAccountByExternalReferenceCodeAccountExternalReferenceCodeCartsPage"));
					put(
						"query#channelAccountCarts",
						new ObjectValuePair<>(
							CartResourceImpl.class,
							"getChannelAccountCartsPage"));
					put(
						"query#channelCarts",
						new ObjectValuePair<>(
							CartResourceImpl.class, "getChannelCartsPage"));
					put(
						"query#cartCommentByExternalReferenceCode",
						new ObjectValuePair<>(
							CartCommentResourceImpl.class,
							"getCartCommentByExternalReferenceCode"));
					put(
						"query#cartComment",
						new ObjectValuePair<>(
							CartCommentResourceImpl.class, "getCartComment"));
					put(
						"query#cartByExternalReferenceCodeComments",
						new ObjectValuePair<>(
							CartCommentResourceImpl.class,
							"getCartByExternalReferenceCodeCommentsPage"));
					put(
						"query#cartComments",
						new ObjectValuePair<>(
							CartCommentResourceImpl.class,
							"getCartCommentsPage"));
					put(
						"query#cartItemByExternalReferenceCode",
						new ObjectValuePair<>(
							CartItemResourceImpl.class,
							"getCartItemByExternalReferenceCode"));
					put(
						"query#cartItem",
						new ObjectValuePair<>(
							CartItemResourceImpl.class, "getCartItem"));
					put(
						"query#cartByExternalReferenceCodeItems",
						new ObjectValuePair<>(
							CartItemResourceImpl.class,
							"getCartByExternalReferenceCodeItemsPage"));
					put(
						"query#cartItems",
						new ObjectValuePair<>(
							CartItemResourceImpl.class, "getCartItemsPage"));
					put(
						"query#cartCartTransitions",
						new ObjectValuePair<>(
							CartTransitionResourceImpl.class,
							"getCartCartTransitionsPage"));
					put(
						"query#cartByExternalReferenceCodePaymentMethods",
						new ObjectValuePair<>(
							PaymentMethodResourceImpl.class,
							"getCartByExternalReferenceCodePaymentMethodsPage"));
					put(
						"query#cartPaymentMethods",
						new ObjectValuePair<>(
							PaymentMethodResourceImpl.class,
							"getCartPaymentMethodsPage"));
					put(
						"query#cartByExternalReferenceCodeShippingMethods",
						new ObjectValuePair<>(
							ShippingMethodResourceImpl.class,
							"getCartByExternalReferenceCodeShippingMethodsPage"));
					put(
						"query#cartShippingMethods",
						new ObjectValuePair<>(
							ShippingMethodResourceImpl.class,
							"getCartShippingMethodsPage"));
					put(
						"query#cartByExternalReferenceCodeDeliveryTerms",
						new ObjectValuePair<>(
							TermResourceImpl.class,
							"getCartByExternalReferenceCodeDeliveryTermsPage"));
					put(
						"query#cartByExternalReferenceCodePaymentTerms",
						new ObjectValuePair<>(
							TermResourceImpl.class,
							"getCartByExternalReferenceCodePaymentTermsPage"));
					put(
						"query#cartDeliveryTerms",
						new ObjectValuePair<>(
							TermResourceImpl.class,
							"getCartDeliveryTermsPage"));
					put(
						"query#cartPaymentTerms",
						new ObjectValuePair<>(
							TermResourceImpl.class, "getCartPaymentTermsPage"));

					put(
						"query#Cart.paymentMethods",
						new ObjectValuePair<>(
							PaymentMethodResourceImpl.class,
							"getCartPaymentMethodsPage"));
					put(
						"query#Cart.paymentTerms",
						new ObjectValuePair<>(
							TermResourceImpl.class, "getCartPaymentTermsPage"));
					put(
						"query#Cart.deliveryTerms",
						new ObjectValuePair<>(
							TermResourceImpl.class,
							"getCartDeliveryTermsPage"));
					put(
						"query#Cart.shippingAddres",
						new ObjectValuePair<>(
							AddressResourceImpl.class,
							"getCartShippingAddres"));
					put(
						"query#Cart.shippingMethods",
						new ObjectValuePair<>(
							ShippingMethodResourceImpl.class,
							"getCartShippingMethodsPage"));
					put(
						"query#Cart.byExternalReferenceCodePaymentUrl",
						new ObjectValuePair<>(
							CartResourceImpl.class,
							"getCartByExternalReferenceCodePaymentUrl"));
					put(
						"query#CartTransition.cart",
						new ObjectValuePair<>(
							CartResourceImpl.class, "getCart"));
					put(
						"query#Cart.paymentURL",
						new ObjectValuePair<>(
							CartResourceImpl.class, "getCartPaymentURL"));
					put(
						"query#Cart.items",
						new ObjectValuePair<>(
							CartItemResourceImpl.class, "getCartItemsPage"));
					put(
						"query#Cart.billingAddres",
						new ObjectValuePair<>(
							AddressResourceImpl.class, "getCartBillingAddres"));
					put(
						"query#Cart.commentByExternalReferenceCode",
						new ObjectValuePair<>(
							CartCommentResourceImpl.class,
							"getCartCommentByExternalReferenceCode"));
					put(
						"query#Cart.byExternalReferenceCodeBillingAddress",
						new ObjectValuePair<>(
							AddressResourceImpl.class,
							"getCartByExternalReferenceCodeBillingAddress"));
					put(
						"query#Cart.byExternalReferenceCodeShippingAddress",
						new ObjectValuePair<>(
							AddressResourceImpl.class,
							"getCartByExternalReferenceCodeShippingAddress"));
					put(
						"query#Cart.comments",
						new ObjectValuePair<>(
							CartCommentResourceImpl.class,
							"getCartCommentsPage"));
					put(
						"query#Cart.byExternalReferenceCodePaymentTerms",
						new ObjectValuePair<>(
							TermResourceImpl.class,
							"getCartByExternalReferenceCodePaymentTermsPage"));
					put(
						"query#Cart.byExternalReferenceCodeDeliveryTerms",
						new ObjectValuePair<>(
							TermResourceImpl.class,
							"getCartByExternalReferenceCodeDeliveryTermsPage"));
					put(
						"query#Cart.itemByExternalReferenceCode",
						new ObjectValuePair<>(
							CartItemResourceImpl.class,
							"getCartItemByExternalReferenceCode"));
					put(
						"query#Cart.cartTransitions",
						new ObjectValuePair<>(
							CartTransitionResourceImpl.class,
							"getCartCartTransitionsPage"));
					put(
						"query#Cart.byExternalReferenceCodePaymentMethods",
						new ObjectValuePair<>(
							PaymentMethodResourceImpl.class,
							"getCartByExternalReferenceCodePaymentMethodsPage"));
					put(
						"query#Cart.byExternalReferenceCodeComments",
						new ObjectValuePair<>(
							CartCommentResourceImpl.class,
							"getCartByExternalReferenceCodeCommentsPage"));
					put(
						"query#Cart.byExternalReferenceCodeAttachments",
						new ObjectValuePair<>(
							AttachmentResourceImpl.class,
							"getCartByExternalReferenceCodeAttachmentsPage"));
					put(
						"query#Cart.byExternalReferenceCodeShippingMethods",
						new ObjectValuePair<>(
							ShippingMethodResourceImpl.class,
							"getCartByExternalReferenceCodeShippingMethodsPage"));
					put(
						"query#CartComment.cartByExternalReferenceCode",
						new ObjectValuePair<>(
							CartResourceImpl.class,
							"getCartByExternalReferenceCode"));
					put(
						"query#Cart.byExternalReferenceCodeItems",
						new ObjectValuePair<>(
							CartItemResourceImpl.class,
							"getCartByExternalReferenceCodeItemsPage"));

					put(
						"query#CartItem.parentCartItem",
						new ObjectValuePair<>(
							CartItemResourceImpl.class, "getCartItem"));
				}
			};

	@Reference(scope = ReferenceScope.PROTOTYPE_REQUIRED)
	private ComponentServiceObjects<AttachmentResource>
		_attachmentResourceComponentServiceObjects;

	@Reference(scope = ReferenceScope.PROTOTYPE_REQUIRED)
	private ComponentServiceObjects<CartResource>
		_cartResourceComponentServiceObjects;

	@Reference(scope = ReferenceScope.PROTOTYPE_REQUIRED)
	private ComponentServiceObjects<CartCommentResource>
		_cartCommentResourceComponentServiceObjects;

	@Reference(scope = ReferenceScope.PROTOTYPE_REQUIRED)
	private ComponentServiceObjects<CartItemResource>
		_cartItemResourceComponentServiceObjects;

	@Reference(scope = ReferenceScope.PROTOTYPE_REQUIRED)
	private ComponentServiceObjects<CartTransitionResource>
		_cartTransitionResourceComponentServiceObjects;

	@Reference(scope = ReferenceScope.PROTOTYPE_REQUIRED)
	private ComponentServiceObjects<PaymentMethodResource>
		_paymentMethodResourceComponentServiceObjects;

	@Reference(scope = ReferenceScope.PROTOTYPE_REQUIRED)
	private ComponentServiceObjects<ShippingMethodResource>
		_shippingMethodResourceComponentServiceObjects;

	@Reference(scope = ReferenceScope.PROTOTYPE_REQUIRED)
	private ComponentServiceObjects<AddressResource>
		_addressResourceComponentServiceObjects;

	@Reference(scope = ReferenceScope.PROTOTYPE_REQUIRED)
	private ComponentServiceObjects<TermResource>
		_termResourceComponentServiceObjects;

}