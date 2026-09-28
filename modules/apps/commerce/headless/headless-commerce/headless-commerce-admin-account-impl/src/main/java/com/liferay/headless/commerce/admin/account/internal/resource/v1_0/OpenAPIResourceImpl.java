/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.admin.account.internal.resource.v1_0;

import com.liferay.portal.vulcan.resource.OpenAPIResource;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;

import jakarta.annotation.Generated;

import jakarta.servlet.http.HttpServletRequest;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;

import java.lang.reflect.Method;

import java.util.HashSet;
import java.util.Set;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Alessio Antonio Rendina
 * @generated
 */
@Component(
	properties = "OSGI-INF/liferay/rest/v1_0/openapi.properties",
	service = OpenAPIResourceImpl.class
)
@Generated("")
@OpenAPIDefinition(
	info = @Info(description = "Liferay Commerce Admin Account API. Manages B2B and B2C Commerce accounts and their relationships from the admin perspective. -- Primary entities -- Account (the commerce buyer organization), AccountGroup (collections of accounts that share rules), AccountMember (links users to accounts with account-scoped roles), AccountOrganization (links accounts to Liferay Organizations), AccountAddress (billing and shipping addresses), AccountChannelEntry (deprecated per-account overrides of channel-level rules including currency, delivery and payment terms, discounts, payment methods, price lists, and reserved shipping or billing addresses), and AccountChannelShippingOption (per-account overrides of available shipping methods on a channel). -- Perspective -- administrators with the CommerceOpenApiAdmin.read or CommerceOpenApiAdmin.write scope. Endpoints expose the full account graph, including HATEOAS action descriptors and custom fields, regardless of the requesting user's account membership. -- Related modules -- buyer-perspective endpoints live in com.liferay.headless.commerce.delivery.cart and com.liferay.headless.commerce.delivery.catalog. Channel and pricing configuration lives in com.liferay.headless.commerce.admin.channel and com.liferay.headless.commerce.admin.pricing. -- Common workflows: Provision an account, addresses, and members from the back-office -- POST /accounts with externalReferenceCode upserts the account, and nested addresses and members are persisted in the same call; add further memberships through POST /accounts/by-externalReferenceCode/<externalReferenceCode>/accountMembers (existing user) or .../accountMembers/createUser (new user). -- Link an existing user as an account member -- POST /accounts/by-externalReferenceCode/<externalReferenceCode>/accountMembers with userId or userExternalReferenceCode and the desired AccountRoles, and PATCH on the member endpoint replaces the user's role set on the account. -- Override channel-level rules for one account -- POST /accounts/<id>/account-channel-* (or the by-externalReferenceCode variant) creates an override that takes precedence over the channel default; the top-level /account-channel-* endpoints are deprecated, so prefer the nested forms scoped under an account. -- Group accounts for bulk rule application -- POST /accountGroups creates the group (upsert by externalReferenceCode), and POST /accountGroups/by-externalReferenceCode/<externalReferenceCode>/accounts links an account into the group while the DELETE counterpart unlinks it. A Java client JAR is available for use with the group ID 'com.liferay', artifact ID 'com.liferay.headless.commerce.admin.account.client', and version '4.0.45'.", license = @License(name = "Apache 2.0", url = "http://www.apache.org/licenses/LICENSE-2.0.html"), title = "Liferay Commerce Admin Account API", version = "v1.0")
)
@Path("/v1.0")
public class OpenAPIResourceImpl {

	@GET
	@Path("/openapi.{type:json|yaml}")
	@Produces({MediaType.APPLICATION_JSON, "application/yaml"})
	public Response getOpenAPI(
			@Context HttpServletRequest httpServletRequest,
			@PathParam("type") String type, @Context UriInfo uriInfo)
		throws Exception {

		Class<? extends OpenAPIResource> clazz = _openAPIResource.getClass();

		try {
			Method method = clazz.getMethod(
				"getOpenAPI", HttpServletRequest.class, Set.class, String.class,
				UriInfo.class);

			return (Response)method.invoke(
				_openAPIResource, httpServletRequest, _resourceClasses, type,
				uriInfo);
		}
		catch (NoSuchMethodException noSuchMethodException1) {
			try {
				Method method = clazz.getMethod(
					"getOpenAPI", Set.class, String.class, UriInfo.class);

				return (Response)method.invoke(
					_openAPIResource, _resourceClasses, type, uriInfo);
			}
			catch (NoSuchMethodException noSuchMethodException2) {
				return _openAPIResource.getOpenAPI(_resourceClasses, type);
			}
		}
	}

	@Reference
	private OpenAPIResource _openAPIResource;

	private final Set<Class<?>> _resourceClasses = new HashSet<Class<?>>() {
		{
			add(AccountResourceImpl.class);

			add(AccountAddressResourceImpl.class);

			add(AccountChannelEntryResourceImpl.class);

			add(AccountChannelShippingOptionResourceImpl.class);

			add(AccountMemberResourceImpl.class);

			add(AccountOrganizationResourceImpl.class);

			add(AdminAccountGroupResourceImpl.class);

			add(UserResourceImpl.class);

			add(OpenAPIResourceImpl.class);
		}
	};

}
// LIFERAY-REST-BUILDER-HASH:-1435385916