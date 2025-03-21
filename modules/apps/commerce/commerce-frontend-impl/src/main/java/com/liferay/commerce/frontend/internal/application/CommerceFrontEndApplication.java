/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.frontend.internal.application;

import com.liferay.commerce.frontend.internal.account.CommerceAccountResource;
import com.liferay.commerce.frontend.internal.address.AddressResource;
import com.liferay.commerce.frontend.internal.application.context.provider.ThemeDisplayContextProvider;
import com.liferay.commerce.frontend.internal.cart.CommerceCartResource;
import com.liferay.commerce.frontend.internal.search.CommerceSearchResource;
import com.liferay.commerce.frontend.internal.wishlist.CommerceWishListResource;
import com.liferay.portal.kernel.theme.ThemeDisplay;

import jakarta.ws.rs.core.Application;

import java.util.HashSet;
import java.util.Set;

import org.apache.cxf.jaxrs.ext.ContextProvider;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.jaxrs.whiteboard.JaxrsWhiteboardConstants;

/**
 * @author Marco Leo
 */
@Component(
	property = {
		JaxrsWhiteboardConstants.JAX_RS_APPLICATION_BASE + "=/commerce-ui",
		JaxrsWhiteboardConstants.JAX_RS_NAME + "=CommerceUi.Application",
		"auth.verifier.auth.verifier.PortalSessionAuthVerifier.urls.includes=/*",
		"auth.verifier.guest.allowed=true", "liferay.oauth2=false"
	},
	service = Application.class
)
public class CommerceFrontEndApplication extends Application {

	public Set<Object> getSingletons() {
		Set<Object> singletons = new HashSet<>();

		singletons.add(_addressResource);
		singletons.add(_commerceAccountResource);
		singletons.add(_commerceCartResource);
		singletons.add(_commerceSearchResource);
		singletons.add(_commerceWishListResource);
		singletons.add(_themeDisplayContextProvider);

		return singletons;
	}

	@Reference
	private AddressResource _addressResource;

	@Reference
	private CommerceAccountResource _commerceAccountResource;

	@Reference
	private CommerceCartResource _commerceCartResource;

	@Reference
	private CommerceSearchResource _commerceSearchResource;

	@Reference
	private CommerceWishListResource _commerceWishListResource;

	private final ContextProvider<ThemeDisplay> _themeDisplayContextProvider =
		new ThemeDisplayContextProvider();

}