/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.delivery.cart.resource.v1_0;

import com.liferay.headless.commerce.delivery.cart.dto.v1_0.Cart;
import com.liferay.headless.commerce.delivery.cart.dto.v1_0.CouponCode;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.ResourceActionLocalService;
import com.liferay.portal.kernel.service.ResourcePermissionLocalService;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.odata.filter.ExpressionConvert;
import com.liferay.portal.odata.filter.FilterParserProvider;
import com.liferay.portal.odata.sort.SortParserProvider;
import com.liferay.portal.vulcan.accept.language.AcceptLanguage;
import com.liferay.portal.vulcan.batch.engine.resource.VulcanBatchEngineExportTaskResource;
import com.liferay.portal.vulcan.batch.engine.resource.VulcanBatchEngineImportTaskResource;
import com.liferay.portal.vulcan.pagination.Page;
import com.liferay.portal.vulcan.pagination.Pagination;

import jakarta.annotation.Generated;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;

import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.osgi.annotation.versioning.ProviderType;

/**
 * To access this resource, run:
 *
 *     curl -u your@email.com:yourpassword -D - http://localhost:8080/o/headless-commerce-delivery-cart/v1.0
 *
 * @author Andrea Sbarra
 * @generated
 */
@Generated("")
@ProviderType
public interface CartResource {

	public Response deleteCartByExternalReferenceCode(
			String externalReferenceCode)
		throws Exception;

	public Cart getCartByExternalReferenceCode(String externalReferenceCode)
		throws Exception;

	public Cart patchCartByExternalReferenceCode(
			String externalReferenceCode, Cart cart)
		throws Exception;

	public Cart putCartByExternalReferenceCode(
			String externalReferenceCode, Cart cart)
		throws Exception;

	public Cart postCartByExternalReferenceCodeCheckout(
			String externalReferenceCode)
		throws Exception;

	public Cart postCartByExternalReferenceCodeCouponCode(
			String externalReferenceCode, CouponCode couponCode)
		throws Exception;

	public String getCartByExternalReferenceCodePaymentUrl(
			String externalReferenceCode, String callbackURL)
		throws Exception;

	public Response deleteCart(Long cartId) throws Exception;

	public Response deleteCartBatch(String callbackURL, Object object)
		throws Exception;

	public Cart getCart(Long cartId) throws Exception;

	public Cart patchCart(Long cartId, Cart cart) throws Exception;

	public Cart putCart(Long cartId, Cart cart) throws Exception;

	public Response putCartBatch(String callbackURL, Object object)
		throws Exception;

	public Cart postCartCheckout(Long cartId) throws Exception;

	public Cart postCartCouponCode(Long cartId, CouponCode couponCode)
		throws Exception;

	public String getCartPaymentURL(Long cartId, String callbackURL)
		throws Exception;

	public Page<Cart>
			getChannelByExternalReferenceCodeChannelExternalReferenceCodeAccountByExternalReferenceCodeAccountExternalReferenceCodeCartsPage(
				String accountExternalReferenceCode,
				String channelExternalReferenceCode, String search,
				Pagination pagination)
		throws Exception;

	public Cart postChannelCartByExternalReferenceCode(
			String externalReferenceCode, Cart cart)
		throws Exception;

	public Page<Cart> getChannelAccountCartsPage(
			Long accountId, Long channelId, String search,
			Pagination pagination)
		throws Exception;

	public Page<Cart> getChannelCartsPage(
			Long channelId, String search,
			com.liferay.portal.kernel.search.filter.Filter filter,
			Pagination pagination,
			com.liferay.portal.kernel.search.Sort[] sorts)
		throws Exception;

	public Cart postChannelCart(Long channelId, Cart cart) throws Exception;

	public default void setContextAcceptLanguage(
		AcceptLanguage contextAcceptLanguage) {
	}

	public void setContextCompany(
		com.liferay.portal.kernel.model.Company contextCompany);

	public default void setContextHttpServletRequest(
		HttpServletRequest contextHttpServletRequest) {
	}

	public default void setContextHttpServletResponse(
		HttpServletResponse contextHttpServletResponse) {
	}

	public default void setContextUriInfo(UriInfo contextUriInfo) {
	}

	public void setContextUser(
		com.liferay.portal.kernel.model.User contextUser);

	public void setExpressionConvert(
		ExpressionConvert<com.liferay.portal.kernel.search.filter.Filter>
			expressionConvert);

	public void setFilterParserProvider(
		FilterParserProvider filterParserProvider);

	public void setGroupLocalService(GroupLocalService groupLocalService);

	public void setResourceActionLocalService(
		ResourceActionLocalService resourceActionLocalService);

	public void setResourcePermissionLocalService(
		ResourcePermissionLocalService resourcePermissionLocalService);

	public void setRoleLocalService(RoleLocalService roleLocalService);

	public void setSortParserProvider(SortParserProvider sortParserProvider);

	public void setVulcanBatchEngineExportTaskResource(
		VulcanBatchEngineExportTaskResource
			vulcanBatchEngineExportTaskResource);

	public void setVulcanBatchEngineImportTaskResource(
		VulcanBatchEngineImportTaskResource
			vulcanBatchEngineImportTaskResource);

	public default com.liferay.portal.kernel.search.filter.Filter toFilter(
		String filterString) {

		return toFilter(
			filterString, Collections.<String, List<String>>emptyMap());
	}

	public default com.liferay.portal.kernel.search.filter.Filter toFilter(
		String filterString, Map<String, List<String>> multivaluedMap) {

		return null;
	}

	public default com.liferay.portal.kernel.search.Sort[] toSorts(
		String sortsString) {

		return new com.liferay.portal.kernel.search.Sort[0];
	}

	@ProviderType
	public interface Builder {

		public CartResource build();

		public Builder checkPermissions(boolean checkPermissions);

		public Builder httpServletRequest(
			HttpServletRequest httpServletRequest);

		public Builder httpServletResponse(
			HttpServletResponse httpServletResponse);

		public Builder preferredLocale(Locale preferredLocale);

		public Builder uriInfo(UriInfo uriInfo);

		public Builder user(com.liferay.portal.kernel.model.User user);

	}

	@ProviderType
	public interface Factory {

		public Builder create();

	}

}