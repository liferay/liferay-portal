/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.user.internal.resource.v1_0;

import com.liferay.exportimport.kernel.lar.ExportImportThreadLocal;
import com.liferay.headless.admin.user.dto.v1_0.UserAccount;
import com.liferay.headless.admin.user.resource.v1_0.UserAccountResource;
import com.liferay.petra.function.UnsafeBiConsumer;
import com.liferay.petra.function.UnsafeConsumer;
import com.liferay.petra.function.UnsafeFunction;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.portal.kernel.exception.NoSuchModelException;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.ResourceActionLocalService;
import com.liferay.portal.kernel.service.ResourcePermissionLocalService;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.SetUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.odata.entity.EntityModel;
import com.liferay.portal.odata.filter.ExpressionConvert;
import com.liferay.portal.odata.filter.FilterParser;
import com.liferay.portal.odata.filter.FilterParserProvider;
import com.liferay.portal.odata.sort.SortField;
import com.liferay.portal.odata.sort.SortParser;
import com.liferay.portal.odata.sort.SortParserProvider;
import com.liferay.portal.vulcan.accept.language.AcceptLanguage;
import com.liferay.portal.vulcan.batch.engine.VulcanBatchEngineTaskItemDelegate;
import com.liferay.portal.vulcan.batch.engine.resource.VulcanBatchEngineExportTaskResource;
import com.liferay.portal.vulcan.batch.engine.resource.VulcanBatchEngineImportTaskResource;
import com.liferay.portal.vulcan.crud.VulcanCRUDItemDelegate;
import com.liferay.portal.vulcan.multipart.MultipartBody;
import com.liferay.portal.vulcan.pagination.Page;
import com.liferay.portal.vulcan.pagination.Pagination;
import com.liferay.portal.vulcan.resource.EntityModelResource;
import com.liferay.portal.vulcan.util.ActionUtil;
import com.liferay.portal.vulcan.util.UriInfoUtil;

import jakarta.annotation.Generated;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import jakarta.ws.rs.NotSupportedException;
import jakarta.ws.rs.core.MultivaluedHashMap;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;

import java.io.Serializable;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

/**
 * @author Javier Gamarra
 * @generated
 */
@Generated("")
@jakarta.ws.rs.Path("/v1.0")
public abstract class BaseUserAccountResourceImpl
	implements EntityModelResource, UserAccountResource,
			   VulcanBatchEngineTaskItemDelegate<UserAccount>,
			   VulcanCRUDItemDelegate<UserAccount> {

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -X 'DELETE' 'http://localhost:8080/o/headless-admin-user/v1.0/accounts/by-external-reference-code/{accountExternalReferenceCode}/user-accounts/by-external-reference-code/{externalReferenceCode}'  -u 'test@liferay.com:test'
	 */
	@io.swagger.v3.oas.annotations.Operation(
		description = "Removes a user by their external reference code from an account by external reference code"
	)
	@io.swagger.v3.oas.annotations.Parameters(
		value = {
			@io.swagger.v3.oas.annotations.Parameter(
				in = io.swagger.v3.oas.annotations.enums.ParameterIn.PATH,
				name = "accountExternalReferenceCode"
			),
			@io.swagger.v3.oas.annotations.Parameter(
				in = io.swagger.v3.oas.annotations.enums.ParameterIn.PATH,
				name = "externalReferenceCode"
			)
		}
	)
	@io.swagger.v3.oas.annotations.tags.Tags(
		value = {@io.swagger.v3.oas.annotations.tags.Tag(name = "UserAccount")}
	)
	@jakarta.ws.rs.DELETE
	@jakarta.ws.rs.Path(
		"/accounts/by-external-reference-code/{accountExternalReferenceCode}/user-accounts/by-external-reference-code/{externalReferenceCode}"
	)
	@jakarta.ws.rs.Produces({"application/json", "application/xml"})
	@Override
	public void
			deleteAccountByExternalReferenceCodeUserAccountByExternalReferenceCode(
				@io.swagger.v3.oas.annotations.Parameter(hidden = true)
				@jakarta.validation.constraints.NotNull
				@jakarta.ws.rs.PathParam("accountExternalReferenceCode")
				String accountExternalReferenceCode,
				@io.swagger.v3.oas.annotations.Parameter(hidden = true)
				@jakarta.validation.constraints.NotNull
				@jakarta.ws.rs.PathParam("externalReferenceCode")
				String externalReferenceCode)
		throws Exception {
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -X 'DELETE' 'http://localhost:8080/o/headless-admin-user/v1.0/accounts/{accountId}/user-accounts/{userAccountId}'  -u 'test@liferay.com:test'
	 */
	@io.swagger.v3.oas.annotations.Operation(
		description = "Removes a user assigned to an account"
	)
	@io.swagger.v3.oas.annotations.Parameters(
		value = {
			@io.swagger.v3.oas.annotations.Parameter(
				in = io.swagger.v3.oas.annotations.enums.ParameterIn.PATH,
				name = "accountId"
			),
			@io.swagger.v3.oas.annotations.Parameter(
				in = io.swagger.v3.oas.annotations.enums.ParameterIn.PATH,
				name = "userAccountId"
			)
		}
	)
	@io.swagger.v3.oas.annotations.tags.Tags(
		value = {@io.swagger.v3.oas.annotations.tags.Tag(name = "UserAccount")}
	)
	@jakarta.ws.rs.DELETE
	@jakarta.ws.rs.Path("/accounts/{accountId}/user-accounts/{userAccountId}")
	@jakarta.ws.rs.Produces({"application/json", "application/xml"})
	@Override
	public void deleteAccountUserAccount(
			@io.swagger.v3.oas.annotations.Parameter(hidden = true)
			@jakarta.validation.constraints.NotNull
			@jakarta.ws.rs.PathParam("accountId")
			Long accountId,
			@io.swagger.v3.oas.annotations.Parameter(hidden = true)
			@jakarta.validation.constraints.NotNull
			@jakarta.ws.rs.PathParam("userAccountId")
			Long userAccountId)
		throws Exception {
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -X 'DELETE' 'http://localhost:8080/o/headless-admin-user/v1.0/accounts/{accountId}/user-accounts/by-email-address/{emailAddress}'  -u 'test@liferay.com:test'
	 */
	@io.swagger.v3.oas.annotations.Operation(
		description = "Removes a user from an account by their email address"
	)
	@io.swagger.v3.oas.annotations.Parameters(
		value = {
			@io.swagger.v3.oas.annotations.Parameter(
				in = io.swagger.v3.oas.annotations.enums.ParameterIn.PATH,
				name = "accountId"
			),
			@io.swagger.v3.oas.annotations.Parameter(
				in = io.swagger.v3.oas.annotations.enums.ParameterIn.PATH,
				name = "emailAddress"
			)
		}
	)
	@io.swagger.v3.oas.annotations.tags.Tags(
		value = {@io.swagger.v3.oas.annotations.tags.Tag(name = "UserAccount")}
	)
	@jakarta.ws.rs.DELETE
	@jakarta.ws.rs.Path(
		"/accounts/{accountId}/user-accounts/by-email-address/{emailAddress}"
	)
	@jakarta.ws.rs.Produces({"application/json", "application/xml"})
	@Override
	public void deleteAccountUserAccountByEmailAddress(
			@io.swagger.v3.oas.annotations.Parameter(hidden = true)
			@jakarta.validation.constraints.NotNull
			@jakarta.ws.rs.PathParam("accountId")
			Long accountId,
			@io.swagger.v3.oas.annotations.Parameter(hidden = true)
			@jakarta.validation.constraints.NotNull
			@jakarta.ws.rs.PathParam("emailAddress")
			String emailAddress)
		throws Exception {
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -X 'DELETE' 'http://localhost:8080/o/headless-admin-user/v1.0/accounts/by-external-reference-code/{externalReferenceCode}/user-accounts/by-email-address/{emailAddress}'  -u 'test@liferay.com:test'
	 */
	@io.swagger.v3.oas.annotations.Operation(
		description = "Removes a user from an account by external reference code by their email address"
	)
	@io.swagger.v3.oas.annotations.Parameters(
		value = {
			@io.swagger.v3.oas.annotations.Parameter(
				in = io.swagger.v3.oas.annotations.enums.ParameterIn.PATH,
				name = "externalReferenceCode"
			),
			@io.swagger.v3.oas.annotations.Parameter(
				in = io.swagger.v3.oas.annotations.enums.ParameterIn.PATH,
				name = "emailAddress"
			)
		}
	)
	@io.swagger.v3.oas.annotations.tags.Tags(
		value = {@io.swagger.v3.oas.annotations.tags.Tag(name = "UserAccount")}
	)
	@jakarta.ws.rs.DELETE
	@jakarta.ws.rs.Path(
		"/accounts/by-external-reference-code/{externalReferenceCode}/user-accounts/by-email-address/{emailAddress}"
	)
	@jakarta.ws.rs.Produces({"application/json", "application/xml"})
	@Override
	public void deleteAccountUserAccountByExternalReferenceCodeByEmailAddress(
			@io.swagger.v3.oas.annotations.Parameter(hidden = true)
			@jakarta.validation.constraints.NotNull
			@jakarta.ws.rs.PathParam("externalReferenceCode")
			String externalReferenceCode,
			@io.swagger.v3.oas.annotations.Parameter(hidden = true)
			@jakarta.validation.constraints.NotNull
			@jakarta.ws.rs.PathParam("emailAddress")
			String emailAddress)
		throws Exception {
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -X 'DELETE' 'http://localhost:8080/o/headless-admin-user/v1.0/accounts/{accountId}/user-accounts/by-email-address'  -u 'test@liferay.com:test'
	 */
	@io.swagger.v3.oas.annotations.Operation(
		description = "Removes users from an account by their email addresses",
		requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "A list of user email addresses to remove from the account")
	)
	@io.swagger.v3.oas.annotations.Parameters(
		value = {
			@io.swagger.v3.oas.annotations.Parameter(
				in = io.swagger.v3.oas.annotations.enums.ParameterIn.PATH,
				name = "accountId"
			)
		}
	)
	@io.swagger.v3.oas.annotations.tags.Tags(
		value = {@io.swagger.v3.oas.annotations.tags.Tag(name = "UserAccount")}
	)
	@jakarta.ws.rs.Consumes({"application/json", "application/xml"})
	@jakarta.ws.rs.DELETE
	@jakarta.ws.rs.Path("/accounts/{accountId}/user-accounts/by-email-address")
	@jakarta.ws.rs.Produces({"application/json", "application/xml"})
	@Override
	public void deleteAccountUserAccountsByEmailAddress(
			@io.swagger.v3.oas.annotations.Parameter(hidden = true)
			@jakarta.validation.constraints.NotNull
			@jakarta.ws.rs.PathParam("accountId")
			Long accountId,
			String[] strings)
		throws Exception {
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -X 'DELETE' 'http://localhost:8080/o/headless-admin-user/v1.0/accounts/by-external-reference-code/{externalReferenceCode}/user-accounts/by-email-address'  -u 'test@liferay.com:test'
	 */
	@io.swagger.v3.oas.annotations.Operation(
		description = "Removes users from an account by their email addresses",
		requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "A list of user email addresses to remove from the account")
	)
	@io.swagger.v3.oas.annotations.Parameters(
		value = {
			@io.swagger.v3.oas.annotations.Parameter(
				in = io.swagger.v3.oas.annotations.enums.ParameterIn.PATH,
				name = "externalReferenceCode"
			)
		}
	)
	@io.swagger.v3.oas.annotations.tags.Tags(
		value = {@io.swagger.v3.oas.annotations.tags.Tag(name = "UserAccount")}
	)
	@jakarta.ws.rs.Consumes({"application/json", "application/xml"})
	@jakarta.ws.rs.DELETE
	@jakarta.ws.rs.Path(
		"/accounts/by-external-reference-code/{externalReferenceCode}/user-accounts/by-email-address"
	)
	@jakarta.ws.rs.Produces({"application/json", "application/xml"})
	@Override
	public void deleteAccountUserAccountsByExternalReferenceCodeByEmailAddress(
			@io.swagger.v3.oas.annotations.Parameter(hidden = true)
			@jakarta.validation.constraints.NotNull
			@jakarta.ws.rs.PathParam("externalReferenceCode")
			String externalReferenceCode,
			String[] strings)
		throws Exception {
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -X 'DELETE' 'http://localhost:8080/o/headless-admin-user/v1.0/user-accounts/{userAccountId}'  -u 'test@liferay.com:test'
	 */
	@io.swagger.v3.oas.annotations.Operation(
		description = "Deletes the user account"
	)
	@io.swagger.v3.oas.annotations.Parameters(
		value = {
			@io.swagger.v3.oas.annotations.Parameter(
				in = io.swagger.v3.oas.annotations.enums.ParameterIn.PATH,
				name = "userAccountId"
			)
		}
	)
	@io.swagger.v3.oas.annotations.tags.Tags(
		value = {@io.swagger.v3.oas.annotations.tags.Tag(name = "UserAccount")}
	)
	@jakarta.ws.rs.DELETE
	@jakarta.ws.rs.Path("/user-accounts/{userAccountId}")
	@jakarta.ws.rs.Produces({"application/json", "application/xml"})
	@Override
	public void deleteUserAccount(
			@io.swagger.v3.oas.annotations.Parameter(hidden = true)
			@jakarta.validation.constraints.NotNull
			@jakarta.ws.rs.PathParam("userAccountId")
			Long userAccountId)
		throws Exception {
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -X 'DELETE' 'http://localhost:8080/o/headless-admin-user/v1.0/user-accounts/batch'  -u 'test@liferay.com:test'
	 */
	@io.swagger.v3.oas.annotations.Parameters(
		value = {
			@io.swagger.v3.oas.annotations.Parameter(
				in = io.swagger.v3.oas.annotations.enums.ParameterIn.QUERY,
				name = "callbackURL"
			)
		}
	)
	@io.swagger.v3.oas.annotations.tags.Tags(
		value = {@io.swagger.v3.oas.annotations.tags.Tag(name = "UserAccount")}
	)
	@jakarta.ws.rs.Consumes("application/json")
	@jakarta.ws.rs.DELETE
	@jakarta.ws.rs.Path("/user-accounts/batch")
	@jakarta.ws.rs.Produces("application/json")
	@Override
	public Response deleteUserAccountBatch(
			@io.swagger.v3.oas.annotations.Parameter(hidden = true)
			@jakarta.ws.rs.QueryParam("callbackURL")
			String callbackURL,
			Object object)
		throws Exception {

		vulcanBatchEngineImportTaskResource.setContextAcceptLanguage(
			contextAcceptLanguage);
		vulcanBatchEngineImportTaskResource.setContextCompany(contextCompany);
		vulcanBatchEngineImportTaskResource.setContextHttpServletRequest(
			contextHttpServletRequest);
		vulcanBatchEngineImportTaskResource.setContextUriInfo(contextUriInfo);
		vulcanBatchEngineImportTaskResource.setContextUser(contextUser);

		Response.ResponseBuilder responseBuilder = Response.accepted();

		return responseBuilder.entity(
			vulcanBatchEngineImportTaskResource.deleteImportTask(
				UserAccount.class.getName(), callbackURL, object)
		).build();
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -X 'DELETE' 'http://localhost:8080/o/headless-admin-user/v1.0/user-accounts/by-external-reference-code/{externalReferenceCode}'  -u 'test@liferay.com:test'
	 */
	@io.swagger.v3.oas.annotations.Parameters(
		value = {
			@io.swagger.v3.oas.annotations.Parameter(
				in = io.swagger.v3.oas.annotations.enums.ParameterIn.PATH,
				name = "externalReferenceCode"
			)
		}
	)
	@io.swagger.v3.oas.annotations.tags.Tags(
		value = {@io.swagger.v3.oas.annotations.tags.Tag(name = "UserAccount")}
	)
	@jakarta.ws.rs.DELETE
	@jakarta.ws.rs.Path(
		"/user-accounts/by-external-reference-code/{externalReferenceCode}"
	)
	@jakarta.ws.rs.Produces({"application/json", "application/xml"})
	@Override
	public void deleteUserAccountByExternalReferenceCode(
			@io.swagger.v3.oas.annotations.Parameter(hidden = true)
			@jakarta.validation.constraints.NotNull
			@jakarta.ws.rs.PathParam("externalReferenceCode")
			String externalReferenceCode)
		throws Exception {
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -X 'GET' 'http://localhost:8080/o/headless-admin-user/v1.0/accounts/by-external-reference-code/{accountExternalReferenceCode}/user-accounts/by-external-reference-code/{externalReferenceCode}'  -u 'test@liferay.com:test'
	 */
	@io.swagger.v3.oas.annotations.Operation(
		description = "Gets a user by their external reference code to an account by external reference code"
	)
	@io.swagger.v3.oas.annotations.Parameters(
		value = {
			@io.swagger.v3.oas.annotations.Parameter(
				in = io.swagger.v3.oas.annotations.enums.ParameterIn.PATH,
				name = "accountExternalReferenceCode"
			),
			@io.swagger.v3.oas.annotations.Parameter(
				in = io.swagger.v3.oas.annotations.enums.ParameterIn.PATH,
				name = "externalReferenceCode"
			)
		}
	)
	@io.swagger.v3.oas.annotations.tags.Tags(
		value = {@io.swagger.v3.oas.annotations.tags.Tag(name = "UserAccount")}
	)
	@jakarta.ws.rs.GET
	@jakarta.ws.rs.Path(
		"/accounts/by-external-reference-code/{accountExternalReferenceCode}/user-accounts/by-external-reference-code/{externalReferenceCode}"
	)
	@jakarta.ws.rs.Produces({"application/json", "application/xml"})
	@Override
	public UserAccount
			getAccountByExternalReferenceCodeUserAccountByExternalReferenceCode(
				@io.swagger.v3.oas.annotations.Parameter(hidden = true)
				@jakarta.validation.constraints.NotNull
				@jakarta.ws.rs.PathParam("accountExternalReferenceCode")
				String accountExternalReferenceCode,
				@io.swagger.v3.oas.annotations.Parameter(hidden = true)
				@jakarta.validation.constraints.NotNull
				@jakarta.ws.rs.PathParam("externalReferenceCode")
				String externalReferenceCode)
		throws Exception {

		return new UserAccount();
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -X 'GET' 'http://localhost:8080/o/headless-admin-user/v1.0/accounts/{accountId}/user-accounts/{userAccountId}'  -u 'test@liferay.com:test'
	 */
	@io.swagger.v3.oas.annotations.Operation(
		description = "Gets a user assigned to an account"
	)
	@io.swagger.v3.oas.annotations.Parameters(
		value = {
			@io.swagger.v3.oas.annotations.Parameter(
				in = io.swagger.v3.oas.annotations.enums.ParameterIn.PATH,
				name = "accountId"
			),
			@io.swagger.v3.oas.annotations.Parameter(
				in = io.swagger.v3.oas.annotations.enums.ParameterIn.PATH,
				name = "userAccountId"
			)
		}
	)
	@io.swagger.v3.oas.annotations.tags.Tags(
		value = {@io.swagger.v3.oas.annotations.tags.Tag(name = "UserAccount")}
	)
	@jakarta.ws.rs.GET
	@jakarta.ws.rs.Path("/accounts/{accountId}/user-accounts/{userAccountId}")
	@jakarta.ws.rs.Produces({"application/json", "application/xml"})
	@Override
	public UserAccount getAccountUserAccount(
			@io.swagger.v3.oas.annotations.Parameter(hidden = true)
			@jakarta.validation.constraints.NotNull
			@jakarta.ws.rs.PathParam("accountId")
			Long accountId,
			@io.swagger.v3.oas.annotations.Parameter(hidden = true)
			@jakarta.validation.constraints.NotNull
			@jakarta.ws.rs.PathParam("userAccountId")
			Long userAccountId)
		throws Exception {

		return new UserAccount();
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -X 'GET' 'http://localhost:8080/o/headless-admin-user/v1.0/accounts/by-external-reference-code/{externalReferenceCode}/user-accounts'  -u 'test@liferay.com:test'
	 */
	@io.swagger.v3.oas.annotations.Operation(
		description = "Gets the users assigned to an account"
	)
	@io.swagger.v3.oas.annotations.Parameters(
		value = {
			@io.swagger.v3.oas.annotations.Parameter(
				in = io.swagger.v3.oas.annotations.enums.ParameterIn.PATH,
				name = "externalReferenceCode"
			),
			@io.swagger.v3.oas.annotations.Parameter(
				in = io.swagger.v3.oas.annotations.enums.ParameterIn.QUERY,
				name = "filter"
			),
			@io.swagger.v3.oas.annotations.Parameter(
				in = io.swagger.v3.oas.annotations.enums.ParameterIn.QUERY,
				name = "page"
			),
			@io.swagger.v3.oas.annotations.Parameter(
				in = io.swagger.v3.oas.annotations.enums.ParameterIn.QUERY,
				name = "pageSize"
			),
			@io.swagger.v3.oas.annotations.Parameter(
				in = io.swagger.v3.oas.annotations.enums.ParameterIn.QUERY,
				name = "search"
			),
			@io.swagger.v3.oas.annotations.Parameter(
				in = io.swagger.v3.oas.annotations.enums.ParameterIn.QUERY,
				name = "sort"
			)
		}
	)
	@io.swagger.v3.oas.annotations.tags.Tags(
		value = {@io.swagger.v3.oas.annotations.tags.Tag(name = "UserAccount")}
	)
	@jakarta.ws.rs.GET
	@jakarta.ws.rs.Path(
		"/accounts/by-external-reference-code/{externalReferenceCode}/user-accounts"
	)
	@jakarta.ws.rs.Produces({"application/json", "application/xml"})
	@Override
	public Page<UserAccount> getAccountUserAccountsByExternalReferenceCodePage(
			@io.swagger.v3.oas.annotations.Parameter(hidden = true)
			@jakarta.validation.constraints.NotNull
			@jakarta.ws.rs.PathParam("externalReferenceCode")
			String externalReferenceCode,
			@io.swagger.v3.oas.annotations.Parameter(hidden = true)
			@jakarta.ws.rs.QueryParam("search")
			String search,
			@jakarta.ws.rs.core.Context
				com.liferay.portal.kernel.search.filter.Filter filter,
			@jakarta.ws.rs.core.Context Pagination pagination,
			@jakarta.ws.rs.core.Context com.liferay.portal.kernel.search.Sort[]
				sorts)
		throws Exception {

		return Page.of(Collections.emptyList());
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -X 'GET' 'http://localhost:8080/o/headless-admin-user/v1.0/accounts/{accountId}/user-accounts'  -u 'test@liferay.com:test'
	 */
	@io.swagger.v3.oas.annotations.Operation(
		description = "Gets the users assigned to an account"
	)
	@io.swagger.v3.oas.annotations.Parameters(
		value = {
			@io.swagger.v3.oas.annotations.Parameter(
				in = io.swagger.v3.oas.annotations.enums.ParameterIn.PATH,
				name = "accountId"
			),
			@io.swagger.v3.oas.annotations.Parameter(
				in = io.swagger.v3.oas.annotations.enums.ParameterIn.QUERY,
				name = "filter"
			),
			@io.swagger.v3.oas.annotations.Parameter(
				in = io.swagger.v3.oas.annotations.enums.ParameterIn.QUERY,
				name = "page"
			),
			@io.swagger.v3.oas.annotations.Parameter(
				in = io.swagger.v3.oas.annotations.enums.ParameterIn.QUERY,
				name = "pageSize"
			),
			@io.swagger.v3.oas.annotations.Parameter(
				in = io.swagger.v3.oas.annotations.enums.ParameterIn.QUERY,
				name = "search"
			),
			@io.swagger.v3.oas.annotations.Parameter(
				in = io.swagger.v3.oas.annotations.enums.ParameterIn.QUERY,
				name = "sort"
			)
		}
	)
	@io.swagger.v3.oas.annotations.tags.Tags(
		value = {@io.swagger.v3.oas.annotations.tags.Tag(name = "UserAccount")}
	)
	@jakarta.ws.rs.GET
	@jakarta.ws.rs.Path("/accounts/{accountId}/user-accounts")
	@jakarta.ws.rs.Produces({"application/json", "application/xml"})
	@Override
	public Page<UserAccount> getAccountUserAccountsPage(
			@io.swagger.v3.oas.annotations.Parameter(hidden = true)
			@jakarta.validation.constraints.NotNull
			@jakarta.ws.rs.PathParam("accountId")
			Long accountId,
			@io.swagger.v3.oas.annotations.Parameter(hidden = true)
			@jakarta.ws.rs.QueryParam("search")
			String search,
			@jakarta.ws.rs.core.Context
				com.liferay.portal.kernel.search.filter.Filter filter,
			@jakarta.ws.rs.core.Context Pagination pagination,
			@jakarta.ws.rs.core.Context com.liferay.portal.kernel.search.Sort[]
				sorts)
		throws Exception {

		return Page.of(Collections.emptyList());
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -X 'GET' 'http://localhost:8080/o/headless-admin-user/v1.0/my-user-account'  -u 'test@liferay.com:test'
	 */
	@io.swagger.v3.oas.annotations.Operation(
		description = "Retrieves information about the user who made the request."
	)
	@io.swagger.v3.oas.annotations.tags.Tags(
		value = {@io.swagger.v3.oas.annotations.tags.Tag(name = "UserAccount")}
	)
	@jakarta.ws.rs.GET
	@jakarta.ws.rs.Path("/my-user-account")
	@jakarta.ws.rs.Produces({"application/json", "application/xml"})
	@Override
	public UserAccount getMyUserAccount() throws Exception {
		return new UserAccount();
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -X 'GET' 'http://localhost:8080/o/headless-admin-user/v1.0/organizations/by-external-reference-code/{externalReferenceCode}/user-accounts'  -u 'test@liferay.com:test'
	 */
	@io.swagger.v3.oas.annotations.Operation(
		description = "Retrieves the organization's members (users). Results can be paginated, filtered, searched, and sorted."
	)
	@io.swagger.v3.oas.annotations.Parameters(
		value = {
			@io.swagger.v3.oas.annotations.Parameter(
				in = io.swagger.v3.oas.annotations.enums.ParameterIn.PATH,
				name = "externalReferenceCode"
			),
			@io.swagger.v3.oas.annotations.Parameter(
				in = io.swagger.v3.oas.annotations.enums.ParameterIn.QUERY,
				name = "filter"
			),
			@io.swagger.v3.oas.annotations.Parameter(
				in = io.swagger.v3.oas.annotations.enums.ParameterIn.QUERY,
				name = "page"
			),
			@io.swagger.v3.oas.annotations.Parameter(
				in = io.swagger.v3.oas.annotations.enums.ParameterIn.QUERY,
				name = "pageSize"
			),
			@io.swagger.v3.oas.annotations.Parameter(
				in = io.swagger.v3.oas.annotations.enums.ParameterIn.QUERY,
				name = "search"
			),
			@io.swagger.v3.oas.annotations.Parameter(
				in = io.swagger.v3.oas.annotations.enums.ParameterIn.QUERY,
				name = "sort"
			)
		}
	)
	@io.swagger.v3.oas.annotations.tags.Tags(
		value = {@io.swagger.v3.oas.annotations.tags.Tag(name = "UserAccount")}
	)
	@jakarta.ws.rs.GET
	@jakarta.ws.rs.Path(
		"/organizations/by-external-reference-code/{externalReferenceCode}/user-accounts"
	)
	@jakarta.ws.rs.Produces({"application/json", "application/xml"})
	@Override
	public Page<UserAccount>
			getOrganizationByExternalReferenceCodeUserAccountsPage(
				@io.swagger.v3.oas.annotations.Parameter(hidden = true)
				@jakarta.validation.constraints.NotNull
				@jakarta.ws.rs.PathParam("externalReferenceCode")
				String externalReferenceCode,
				@io.swagger.v3.oas.annotations.Parameter(hidden = true)
				@jakarta.ws.rs.QueryParam("search")
				String search,
				@jakarta.ws.rs.core.Context
					com.liferay.portal.kernel.search.filter.Filter filter,
				@jakarta.ws.rs.core.Context Pagination pagination,
				@jakarta.ws.rs.core.Context
					com.liferay.portal.kernel.search.Sort[] sorts)
		throws Exception {

		return Page.of(Collections.emptyList());
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -X 'GET' 'http://localhost:8080/o/headless-admin-user/v1.0/organizations/{organizationId}/user-accounts'  -u 'test@liferay.com:test'
	 */
	@io.swagger.v3.oas.annotations.Operation(
		description = "Retrieves the organization's members (users). Results can be paginated, filtered, searched, and sorted."
	)
	@io.swagger.v3.oas.annotations.Parameters(
		value = {
			@io.swagger.v3.oas.annotations.Parameter(
				in = io.swagger.v3.oas.annotations.enums.ParameterIn.PATH,
				name = "organizationId"
			),
			@io.swagger.v3.oas.annotations.Parameter(
				in = io.swagger.v3.oas.annotations.enums.ParameterIn.QUERY,
				name = "filter"
			),
			@io.swagger.v3.oas.annotations.Parameter(
				in = io.swagger.v3.oas.annotations.enums.ParameterIn.QUERY,
				name = "page"
			),
			@io.swagger.v3.oas.annotations.Parameter(
				in = io.swagger.v3.oas.annotations.enums.ParameterIn.QUERY,
				name = "pageSize"
			),
			@io.swagger.v3.oas.annotations.Parameter(
				in = io.swagger.v3.oas.annotations.enums.ParameterIn.QUERY,
				name = "search"
			),
			@io.swagger.v3.oas.annotations.Parameter(
				in = io.swagger.v3.oas.annotations.enums.ParameterIn.QUERY,
				name = "sort"
			)
		}
	)
	@io.swagger.v3.oas.annotations.tags.Tags(
		value = {@io.swagger.v3.oas.annotations.tags.Tag(name = "UserAccount")}
	)
	@jakarta.ws.rs.GET
	@jakarta.ws.rs.Path("/organizations/{organizationId}/user-accounts")
	@jakarta.ws.rs.Produces({"application/json", "application/xml"})
	@Override
	public Page<UserAccount> getOrganizationUserAccountsPage(
			@io.swagger.v3.oas.annotations.Parameter(hidden = true)
			@jakarta.validation.constraints.NotNull
			@jakarta.ws.rs.PathParam("organizationId")
			String organizationId,
			@io.swagger.v3.oas.annotations.Parameter(hidden = true)
			@jakarta.ws.rs.QueryParam("search")
			String search,
			@jakarta.ws.rs.core.Context
				com.liferay.portal.kernel.search.filter.Filter filter,
			@jakarta.ws.rs.core.Context Pagination pagination,
			@jakarta.ws.rs.core.Context com.liferay.portal.kernel.search.Sort[]
				sorts)
		throws Exception {

		return Page.of(Collections.emptyList());
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -X 'GET' 'http://localhost:8080/o/headless-admin-user/v1.0/sites/{siteId}/accounts/{accountId}/user-accounts/{userAccountId}/selected'  -u 'test@liferay.com:test'
	 */
	@io.swagger.v3.oas.annotations.Operation(
		description = "Gets the selected property of a user assigned to an account for a specific site"
	)
	@io.swagger.v3.oas.annotations.Parameters(
		value = {
			@io.swagger.v3.oas.annotations.Parameter(
				in = io.swagger.v3.oas.annotations.enums.ParameterIn.PATH,
				name = "siteId"
			),
			@io.swagger.v3.oas.annotations.Parameter(
				in = io.swagger.v3.oas.annotations.enums.ParameterIn.PATH,
				name = "accountId"
			),
			@io.swagger.v3.oas.annotations.Parameter(
				in = io.swagger.v3.oas.annotations.enums.ParameterIn.PATH,
				name = "userAccountId"
			)
		}
	)
	@io.swagger.v3.oas.annotations.tags.Tags(
		value = {@io.swagger.v3.oas.annotations.tags.Tag(name = "UserAccount")}
	)
	@jakarta.ws.rs.GET
	@jakarta.ws.rs.Path(
		"/sites/{siteId}/accounts/{accountId}/user-accounts/{userAccountId}/selected"
	)
	@jakarta.ws.rs.Produces({"application/json", "application/xml"})
	@Override
	public Boolean getSiteAccountUserAccountSelected(
			@io.swagger.v3.oas.annotations.Parameter(hidden = true)
			@jakarta.validation.constraints.NotNull
			@jakarta.ws.rs.PathParam("siteId")
			Long siteId,
			@io.swagger.v3.oas.annotations.Parameter(hidden = true)
			@jakarta.validation.constraints.NotNull
			@jakarta.ws.rs.PathParam("accountId")
			Long accountId,
			@io.swagger.v3.oas.annotations.Parameter(hidden = true)
			@jakarta.validation.constraints.NotNull
			@jakarta.ws.rs.PathParam("userAccountId")
			Long userAccountId)
		throws Exception {

		return false;
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -X 'GET' 'http://localhost:8080/o/headless-admin-user/v1.0/sites/by-friendly-url-path/{friendlyUrlPath}/accounts/by-external-reference-code/{accountExternalReferenceCode}/user-accounts/by-external-reference-code/{userAccountExternalReferenceCode}/selected'  -u 'test@liferay.com:test'
	 */
	@io.swagger.v3.oas.annotations.Operation(
		description = "Gets the selected property of a user assigned to an account for a specific site"
	)
	@io.swagger.v3.oas.annotations.Parameters(
		value = {
			@io.swagger.v3.oas.annotations.Parameter(
				in = io.swagger.v3.oas.annotations.enums.ParameterIn.PATH,
				name = "friendlyUrlPath"
			),
			@io.swagger.v3.oas.annotations.Parameter(
				in = io.swagger.v3.oas.annotations.enums.ParameterIn.PATH,
				name = "accountExternalReferenceCode"
			),
			@io.swagger.v3.oas.annotations.Parameter(
				in = io.swagger.v3.oas.annotations.enums.ParameterIn.PATH,
				name = "userAccountExternalReferenceCode"
			)
		}
	)
	@io.swagger.v3.oas.annotations.tags.Tags(
		value = {@io.swagger.v3.oas.annotations.tags.Tag(name = "UserAccount")}
	)
	@jakarta.ws.rs.GET
	@jakarta.ws.rs.Path(
		"/sites/by-friendly-url-path/{friendlyUrlPath: .+}/accounts/by-external-reference-code/{accountExternalReferenceCode}/user-accounts/by-external-reference-code/{userAccountExternalReferenceCode}/selected"
	)
	@jakarta.ws.rs.Produces({"application/json", "application/xml"})
	@Override
	public Boolean
			getSiteByFriendlyUrlPathAccountByExternalReferenceCodeAccountExternalReferenceCodeUserAccountByExternalReferenceCodeUserAccountExternalReferenceCodeSelected(
				@io.swagger.v3.oas.annotations.Parameter(hidden = true)
				@jakarta.validation.constraints.NotNull
				@jakarta.ws.rs.PathParam("friendlyUrlPath")
				String friendlyUrlPath,
				@io.swagger.v3.oas.annotations.Parameter(hidden = true)
				@jakarta.validation.constraints.NotNull
				@jakarta.ws.rs.PathParam("accountExternalReferenceCode")
				String accountExternalReferenceCode,
				@io.swagger.v3.oas.annotations.Parameter(hidden = true)
				@jakarta.validation.constraints.NotNull
				@jakarta.ws.rs.PathParam("userAccountExternalReferenceCode")
				String userAccountExternalReferenceCode)
		throws Exception {

		return false;
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -X 'GET' 'http://localhost:8080/o/headless-admin-user/v1.0/sites/{siteId}/user-accounts'  -u 'test@liferay.com:test'
	 */
	@io.swagger.v3.oas.annotations.Operation(
		description = "Retrieves the site members' user accounts. Results can be paginated, filtered, searched, and sorted."
	)
	@io.swagger.v3.oas.annotations.Parameters(
		value = {
			@io.swagger.v3.oas.annotations.Parameter(
				in = io.swagger.v3.oas.annotations.enums.ParameterIn.PATH,
				name = "siteId"
			),
			@io.swagger.v3.oas.annotations.Parameter(
				in = io.swagger.v3.oas.annotations.enums.ParameterIn.QUERY,
				name = "filter"
			),
			@io.swagger.v3.oas.annotations.Parameter(
				in = io.swagger.v3.oas.annotations.enums.ParameterIn.QUERY,
				name = "page"
			),
			@io.swagger.v3.oas.annotations.Parameter(
				in = io.swagger.v3.oas.annotations.enums.ParameterIn.QUERY,
				name = "pageSize"
			),
			@io.swagger.v3.oas.annotations.Parameter(
				in = io.swagger.v3.oas.annotations.enums.ParameterIn.QUERY,
				name = "search"
			),
			@io.swagger.v3.oas.annotations.Parameter(
				in = io.swagger.v3.oas.annotations.enums.ParameterIn.QUERY,
				name = "sort"
			)
		}
	)
	@io.swagger.v3.oas.annotations.tags.Tags(
		value = {@io.swagger.v3.oas.annotations.tags.Tag(name = "UserAccount")}
	)
	@jakarta.ws.rs.GET
	@jakarta.ws.rs.Path("/sites/{siteId}/user-accounts")
	@jakarta.ws.rs.Produces({"application/json", "application/xml"})
	@Override
	public Page<UserAccount> getSiteUserAccountsPage(
			@io.swagger.v3.oas.annotations.Parameter(hidden = true)
			@jakarta.validation.constraints.NotNull
			@jakarta.ws.rs.PathParam("siteId")
			Long siteId,
			@io.swagger.v3.oas.annotations.Parameter(hidden = true)
			@jakarta.ws.rs.QueryParam("search")
			String search,
			@jakarta.ws.rs.core.Context
				com.liferay.portal.kernel.search.filter.Filter filter,
			@jakarta.ws.rs.core.Context Pagination pagination,
			@jakarta.ws.rs.core.Context com.liferay.portal.kernel.search.Sort[]
				sorts)
		throws Exception {

		return Page.of(Collections.emptyList());
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -X 'GET' 'http://localhost:8080/o/headless-admin-user/v1.0/user-accounts/{userAccountId}'  -u 'test@liferay.com:test'
	 */
	@io.swagger.v3.oas.annotations.Operation(
		description = "Retrieves the user account."
	)
	@io.swagger.v3.oas.annotations.Parameters(
		value = {
			@io.swagger.v3.oas.annotations.Parameter(
				in = io.swagger.v3.oas.annotations.enums.ParameterIn.PATH,
				name = "userAccountId"
			)
		}
	)
	@io.swagger.v3.oas.annotations.tags.Tags(
		value = {@io.swagger.v3.oas.annotations.tags.Tag(name = "UserAccount")}
	)
	@jakarta.ws.rs.GET
	@jakarta.ws.rs.Path("/user-accounts/{userAccountId}")
	@jakarta.ws.rs.Produces({"application/json", "application/xml"})
	@Override
	public UserAccount getUserAccount(
			@io.swagger.v3.oas.annotations.Parameter(hidden = true)
			@jakarta.validation.constraints.NotNull
			@jakarta.ws.rs.PathParam("userAccountId")
			Long userAccountId)
		throws Exception {

		return new UserAccount();
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -X 'GET' 'http://localhost:8080/o/headless-admin-user/v1.0/user-accounts/by-email-address/{emailAddress}'  -u 'test@liferay.com:test'
	 */
	@io.swagger.v3.oas.annotations.Parameters(
		value = {
			@io.swagger.v3.oas.annotations.Parameter(
				in = io.swagger.v3.oas.annotations.enums.ParameterIn.PATH,
				name = "emailAddress"
			)
		}
	)
	@io.swagger.v3.oas.annotations.tags.Tags(
		value = {@io.swagger.v3.oas.annotations.tags.Tag(name = "UserAccount")}
	)
	@jakarta.ws.rs.GET
	@jakarta.ws.rs.Path("/user-accounts/by-email-address/{emailAddress}")
	@jakarta.ws.rs.Produces({"application/json", "application/xml"})
	@Override
	public UserAccount getUserAccountByEmailAddress(
			@io.swagger.v3.oas.annotations.Parameter(hidden = true)
			@jakarta.validation.constraints.NotNull
			@jakarta.ws.rs.PathParam("emailAddress")
			String emailAddress)
		throws Exception {

		return new UserAccount();
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -X 'GET' 'http://localhost:8080/o/headless-admin-user/v1.0/user-accounts/by-external-reference-code/{externalReferenceCode}'  -u 'test@liferay.com:test'
	 */
	@io.swagger.v3.oas.annotations.Parameters(
		value = {
			@io.swagger.v3.oas.annotations.Parameter(
				in = io.swagger.v3.oas.annotations.enums.ParameterIn.PATH,
				name = "externalReferenceCode"
			)
		}
	)
	@io.swagger.v3.oas.annotations.tags.Tags(
		value = {@io.swagger.v3.oas.annotations.tags.Tag(name = "UserAccount")}
	)
	@jakarta.ws.rs.GET
	@jakarta.ws.rs.Path(
		"/user-accounts/by-external-reference-code/{externalReferenceCode}"
	)
	@jakarta.ws.rs.Produces({"application/json", "application/xml"})
	@Override
	public UserAccount getUserAccountByExternalReferenceCode(
			@io.swagger.v3.oas.annotations.Parameter(hidden = true)
			@jakarta.validation.constraints.NotNull
			@jakarta.ws.rs.PathParam("externalReferenceCode")
			String externalReferenceCode)
		throws Exception {

		return new UserAccount();
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -X 'GET' 'http://localhost:8080/o/headless-admin-user/v1.0/user-accounts/by-status/{status}'  -u 'test@liferay.com:test'
	 */
	@io.swagger.v3.oas.annotations.Parameters(
		value = {
			@io.swagger.v3.oas.annotations.Parameter(
				in = io.swagger.v3.oas.annotations.enums.ParameterIn.PATH,
				name = "status"
			),
			@io.swagger.v3.oas.annotations.Parameter(
				in = io.swagger.v3.oas.annotations.enums.ParameterIn.QUERY,
				name = "filter"
			),
			@io.swagger.v3.oas.annotations.Parameter(
				in = io.swagger.v3.oas.annotations.enums.ParameterIn.QUERY,
				name = "page"
			),
			@io.swagger.v3.oas.annotations.Parameter(
				in = io.swagger.v3.oas.annotations.enums.ParameterIn.QUERY,
				name = "pageSize"
			),
			@io.swagger.v3.oas.annotations.Parameter(
				in = io.swagger.v3.oas.annotations.enums.ParameterIn.QUERY,
				name = "search"
			),
			@io.swagger.v3.oas.annotations.Parameter(
				in = io.swagger.v3.oas.annotations.enums.ParameterIn.QUERY,
				name = "sort"
			)
		}
	)
	@io.swagger.v3.oas.annotations.tags.Tags(
		value = {@io.swagger.v3.oas.annotations.tags.Tag(name = "UserAccount")}
	)
	@jakarta.ws.rs.GET
	@jakarta.ws.rs.Path("/user-accounts/by-status/{status}")
	@jakarta.ws.rs.Produces({"application/json", "application/xml"})
	@Override
	public Page<UserAccount> getUserAccountsByStatusPage(
			@io.swagger.v3.oas.annotations.Parameter(hidden = true)
			@jakarta.validation.constraints.NotNull
			@jakarta.ws.rs.PathParam("status")
			String status,
			@io.swagger.v3.oas.annotations.Parameter(hidden = true)
			@jakarta.ws.rs.QueryParam("search")
			String search,
			@jakarta.ws.rs.core.Context
				com.liferay.portal.kernel.search.filter.Filter filter,
			@jakarta.ws.rs.core.Context Pagination pagination,
			@jakarta.ws.rs.core.Context com.liferay.portal.kernel.search.Sort[]
				sorts)
		throws Exception {

		return Page.of(Collections.emptyList());
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -X 'GET' 'http://localhost:8080/o/headless-admin-user/v1.0/user-accounts'  -u 'test@liferay.com:test'
	 */
	@io.swagger.v3.oas.annotations.Operation(
		description = "Retrieves the user accounts. Results can be paginated, filtered, searched, and sorted."
	)
	@io.swagger.v3.oas.annotations.Parameters(
		value = {
			@io.swagger.v3.oas.annotations.Parameter(
				in = io.swagger.v3.oas.annotations.enums.ParameterIn.QUERY,
				name = "filter"
			),
			@io.swagger.v3.oas.annotations.Parameter(
				in = io.swagger.v3.oas.annotations.enums.ParameterIn.QUERY,
				name = "page"
			),
			@io.swagger.v3.oas.annotations.Parameter(
				in = io.swagger.v3.oas.annotations.enums.ParameterIn.QUERY,
				name = "pageSize"
			),
			@io.swagger.v3.oas.annotations.Parameter(
				in = io.swagger.v3.oas.annotations.enums.ParameterIn.QUERY,
				name = "search"
			),
			@io.swagger.v3.oas.annotations.Parameter(
				in = io.swagger.v3.oas.annotations.enums.ParameterIn.QUERY,
				name = "sort"
			)
		}
	)
	@io.swagger.v3.oas.annotations.tags.Tags(
		value = {@io.swagger.v3.oas.annotations.tags.Tag(name = "UserAccount")}
	)
	@jakarta.ws.rs.GET
	@jakarta.ws.rs.Path("/user-accounts")
	@jakarta.ws.rs.Produces({"application/json", "application/xml"})
	@Override
	public Page<UserAccount> getUserAccountsPage(
			@io.swagger.v3.oas.annotations.Parameter(hidden = true)
			@jakarta.ws.rs.QueryParam("search")
			String search,
			@jakarta.ws.rs.core.Context
				com.liferay.portal.kernel.search.filter.Filter filter,
			@jakarta.ws.rs.core.Context Pagination pagination,
			@jakarta.ws.rs.core.Context com.liferay.portal.kernel.search.Sort[]
				sorts)
		throws Exception {

		return Page.of(Collections.emptyList());
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -X 'GET' 'http://localhost:8080/o/headless-admin-user/v1.0/user-groups/by-external-reference-code/{externalReferenceCode}/user-group-users'  -u 'test@liferay.com:test'
	 */
	@io.swagger.v3.oas.annotations.Operation(
		description = "Retrieves the list of users in a user group."
	)
	@io.swagger.v3.oas.annotations.Parameters(
		value = {
			@io.swagger.v3.oas.annotations.Parameter(
				in = io.swagger.v3.oas.annotations.enums.ParameterIn.PATH,
				name = "externalReferenceCode"
			),
			@io.swagger.v3.oas.annotations.Parameter(
				in = io.swagger.v3.oas.annotations.enums.ParameterIn.QUERY,
				name = "filter"
			),
			@io.swagger.v3.oas.annotations.Parameter(
				in = io.swagger.v3.oas.annotations.enums.ParameterIn.QUERY,
				name = "page"
			),
			@io.swagger.v3.oas.annotations.Parameter(
				in = io.swagger.v3.oas.annotations.enums.ParameterIn.QUERY,
				name = "pageSize"
			),
			@io.swagger.v3.oas.annotations.Parameter(
				in = io.swagger.v3.oas.annotations.enums.ParameterIn.QUERY,
				name = "search"
			),
			@io.swagger.v3.oas.annotations.Parameter(
				in = io.swagger.v3.oas.annotations.enums.ParameterIn.QUERY,
				name = "sort"
			)
		}
	)
	@io.swagger.v3.oas.annotations.tags.Tags(
		value = {@io.swagger.v3.oas.annotations.tags.Tag(name = "UserAccount")}
	)
	@jakarta.ws.rs.GET
	@jakarta.ws.rs.Path(
		"/user-groups/by-external-reference-code/{externalReferenceCode}/user-group-users"
	)
	@jakarta.ws.rs.Produces({"application/json", "application/xml"})
	@Override
	public Page<UserAccount> getUserGroupByExternalReferenceCodeUsersPage(
			@io.swagger.v3.oas.annotations.Parameter(hidden = true)
			@jakarta.validation.constraints.NotNull
			@jakarta.ws.rs.PathParam("externalReferenceCode")
			String externalReferenceCode,
			@io.swagger.v3.oas.annotations.Parameter(hidden = true)
			@jakarta.ws.rs.QueryParam("search")
			String search,
			@jakarta.ws.rs.core.Context
				com.liferay.portal.kernel.search.filter.Filter filter,
			@jakarta.ws.rs.core.Context Pagination pagination,
			@jakarta.ws.rs.core.Context com.liferay.portal.kernel.search.Sort[]
				sorts)
		throws Exception {

		return Page.of(Collections.emptyList());
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -X 'GET' 'http://localhost:8080/o/headless-admin-user/v1.0/user-groups/{userGroupId}/user-group-users'  -u 'test@liferay.com:test'
	 */
	@io.swagger.v3.oas.annotations.Operation(
		description = "Retrieves the list of users in a user group."
	)
	@io.swagger.v3.oas.annotations.Parameters(
		value = {
			@io.swagger.v3.oas.annotations.Parameter(
				in = io.swagger.v3.oas.annotations.enums.ParameterIn.PATH,
				name = "userGroupId"
			),
			@io.swagger.v3.oas.annotations.Parameter(
				in = io.swagger.v3.oas.annotations.enums.ParameterIn.QUERY,
				name = "filter"
			),
			@io.swagger.v3.oas.annotations.Parameter(
				in = io.swagger.v3.oas.annotations.enums.ParameterIn.QUERY,
				name = "page"
			),
			@io.swagger.v3.oas.annotations.Parameter(
				in = io.swagger.v3.oas.annotations.enums.ParameterIn.QUERY,
				name = "pageSize"
			),
			@io.swagger.v3.oas.annotations.Parameter(
				in = io.swagger.v3.oas.annotations.enums.ParameterIn.QUERY,
				name = "search"
			),
			@io.swagger.v3.oas.annotations.Parameter(
				in = io.swagger.v3.oas.annotations.enums.ParameterIn.QUERY,
				name = "sort"
			)
		}
	)
	@io.swagger.v3.oas.annotations.tags.Tags(
		value = {@io.swagger.v3.oas.annotations.tags.Tag(name = "UserAccount")}
	)
	@jakarta.ws.rs.GET
	@jakarta.ws.rs.Path("/user-groups/{userGroupId}/user-group-users")
	@jakarta.ws.rs.Produces({"application/json", "application/xml"})
	@Override
	public Page<UserAccount> getUserGroupUsersPage(
			@io.swagger.v3.oas.annotations.Parameter(hidden = true)
			@jakarta.validation.constraints.NotNull
			@jakarta.ws.rs.PathParam("userGroupId")
			Long userGroupId,
			@io.swagger.v3.oas.annotations.Parameter(hidden = true)
			@jakarta.ws.rs.QueryParam("search")
			String search,
			@jakarta.ws.rs.core.Context
				com.liferay.portal.kernel.search.filter.Filter filter,
			@jakarta.ws.rs.core.Context Pagination pagination,
			@jakarta.ws.rs.core.Context com.liferay.portal.kernel.search.Sort[]
				sorts)
		throws Exception {

		return Page.of(Collections.emptyList());
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -X 'PATCH' 'http://localhost:8080/o/headless-admin-user/v1.0/sites/{siteId}/accounts/{accountId}/user-accounts/{userAccountId}/selected'  -u 'test@liferay.com:test'
	 */
	@io.swagger.v3.oas.annotations.Operation(
		description = "Updates the selected property of a user assigned to an account for a specific site"
	)
	@io.swagger.v3.oas.annotations.Parameters(
		value = {
			@io.swagger.v3.oas.annotations.Parameter(
				in = io.swagger.v3.oas.annotations.enums.ParameterIn.PATH,
				name = "siteId"
			),
			@io.swagger.v3.oas.annotations.Parameter(
				in = io.swagger.v3.oas.annotations.enums.ParameterIn.PATH,
				name = "accountId"
			),
			@io.swagger.v3.oas.annotations.Parameter(
				in = io.swagger.v3.oas.annotations.enums.ParameterIn.PATH,
				name = "userAccountId"
			)
		}
	)
	@io.swagger.v3.oas.annotations.tags.Tags(
		value = {@io.swagger.v3.oas.annotations.tags.Tag(name = "UserAccount")}
	)
	@jakarta.ws.rs.PATCH
	@jakarta.ws.rs.Path(
		"/sites/{siteId}/accounts/{accountId}/user-accounts/{userAccountId}/selected"
	)
	@jakarta.ws.rs.Produces({"application/json", "application/xml"})
	@Override
	public void patchSiteAccountUserAccountSelected(
			@io.swagger.v3.oas.annotations.Parameter(hidden = true)
			@jakarta.validation.constraints.NotNull
			@jakarta.ws.rs.PathParam("siteId")
			Long siteId,
			@io.swagger.v3.oas.annotations.Parameter(hidden = true)
			@jakarta.validation.constraints.NotNull
			@jakarta.ws.rs.PathParam("accountId")
			Long accountId,
			@io.swagger.v3.oas.annotations.Parameter(hidden = true)
			@jakarta.validation.constraints.NotNull
			@jakarta.ws.rs.PathParam("userAccountId")
			Long userAccountId)
		throws Exception {
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -X 'PATCH' 'http://localhost:8080/o/headless-admin-user/v1.0/sites/by-friendly-url-path/{friendlyUrlPath}/accounts/by-external-reference-code/{accountExternalReferenceCode}/user-accounts/by-external-reference-code/{userAccountExternalReferenceCode}/selected'  -u 'test@liferay.com:test'
	 */
	@io.swagger.v3.oas.annotations.Operation(
		description = "Updates the selected property of a user assigned to an account for a specific site"
	)
	@io.swagger.v3.oas.annotations.Parameters(
		value = {
			@io.swagger.v3.oas.annotations.Parameter(
				in = io.swagger.v3.oas.annotations.enums.ParameterIn.PATH,
				name = "friendlyUrlPath"
			),
			@io.swagger.v3.oas.annotations.Parameter(
				in = io.swagger.v3.oas.annotations.enums.ParameterIn.PATH,
				name = "accountExternalReferenceCode"
			),
			@io.swagger.v3.oas.annotations.Parameter(
				in = io.swagger.v3.oas.annotations.enums.ParameterIn.PATH,
				name = "userAccountExternalReferenceCode"
			)
		}
	)
	@io.swagger.v3.oas.annotations.tags.Tags(
		value = {@io.swagger.v3.oas.annotations.tags.Tag(name = "UserAccount")}
	)
	@jakarta.ws.rs.PATCH
	@jakarta.ws.rs.Path(
		"/sites/by-friendly-url-path/{friendlyUrlPath: .+}/accounts/by-external-reference-code/{accountExternalReferenceCode}/user-accounts/by-external-reference-code/{userAccountExternalReferenceCode}/selected"
	)
	@jakarta.ws.rs.Produces({"application/json", "application/xml"})
	@Override
	public void
			patchSiteByFriendlyUrlPathAccountByExternalReferenceCodeAccountExternalReferenceCodeUserAccountByExternalReferenceCodeUserAccountExternalReferenceCodeSelected(
				@io.swagger.v3.oas.annotations.Parameter(hidden = true)
				@jakarta.validation.constraints.NotNull
				@jakarta.ws.rs.PathParam("friendlyUrlPath")
				String friendlyUrlPath,
				@io.swagger.v3.oas.annotations.Parameter(hidden = true)
				@jakarta.validation.constraints.NotNull
				@jakarta.ws.rs.PathParam("accountExternalReferenceCode")
				String accountExternalReferenceCode,
				@io.swagger.v3.oas.annotations.Parameter(hidden = true)
				@jakarta.validation.constraints.NotNull
				@jakarta.ws.rs.PathParam("userAccountExternalReferenceCode")
				String userAccountExternalReferenceCode)
		throws Exception {
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -X 'PATCH' 'http://localhost:8080/o/headless-admin-user/v1.0/user-accounts/{userAccountId}' -d $'{"additionalName": ___, "alternateName": ___, "birthDate": ___, "currentPassword": ___, "customFields": ___, "emailAddress": ___, "externalReferenceCode": ___, "familyName": ___, "gender": ___, "givenName": ___, "honorificPrefix": ___, "honorificSuffix": ___, "id": ___, "imageExternalReferenceCode": ___, "imageId": ___, "jobTitle": ___, "languageId": ___, "password": ___, "permissions": ___, "status": ___, "userAccountContactInformation": ___}' --header 'Content-Type: application/json' -u 'test@liferay.com:test'
	 */
	@io.swagger.v3.oas.annotations.Operation(
		description = "Updates the user account with information sent in the request body. Only the provided fields are updated."
	)
	@io.swagger.v3.oas.annotations.Parameters(
		value = {
			@io.swagger.v3.oas.annotations.Parameter(
				in = io.swagger.v3.oas.annotations.enums.ParameterIn.PATH,
				name = "userAccountId"
			)
		}
	)
	@io.swagger.v3.oas.annotations.tags.Tags(
		value = {@io.swagger.v3.oas.annotations.tags.Tag(name = "UserAccount")}
	)
	@jakarta.ws.rs.Consumes({"application/json", "application/xml"})
	@jakarta.ws.rs.PATCH
	@jakarta.ws.rs.Path("/user-accounts/{userAccountId}")
	@jakarta.ws.rs.Produces({"application/json", "application/xml"})
	@Override
	public UserAccount patchUserAccount(
			@io.swagger.v3.oas.annotations.Parameter(hidden = true)
			@jakarta.validation.constraints.NotNull
			@jakarta.ws.rs.PathParam("userAccountId")
			Long userAccountId,
			UserAccount userAccount)
		throws Exception {

		UserAccount existingUserAccount = getUserAccount(userAccountId);

		if (userAccount.getAdditionalName() != null) {
			existingUserAccount.setAdditionalName(
				userAccount.getAdditionalName());
		}

		if (userAccount.getAlternateName() != null) {
			existingUserAccount.setAlternateName(
				userAccount.getAlternateName());
		}

		if (userAccount.getBirthDate() != null) {
			existingUserAccount.setBirthDate(userAccount.getBirthDate());
		}

		if (userAccount.getCurrentPassword() != null) {
			existingUserAccount.setCurrentPassword(
				userAccount.getCurrentPassword());
		}

		if (userAccount.getCustomFields() != null) {
			existingUserAccount.setCustomFields(userAccount.getCustomFields());
		}

		if (userAccount.getEmailAddress() != null) {
			existingUserAccount.setEmailAddress(userAccount.getEmailAddress());
		}

		if (userAccount.getExternalReferenceCode() != null) {
			existingUserAccount.setExternalReferenceCode(
				userAccount.getExternalReferenceCode());
		}

		if (userAccount.getFamilyName() != null) {
			existingUserAccount.setFamilyName(userAccount.getFamilyName());
		}

		if (userAccount.getGender() != null) {
			existingUserAccount.setGender(userAccount.getGender());
		}

		if (userAccount.getGivenName() != null) {
			existingUserAccount.setGivenName(userAccount.getGivenName());
		}

		if (userAccount.getHonorificPrefix() != null) {
			existingUserAccount.setHonorificPrefix(
				userAccount.getHonorificPrefix());
		}

		if (userAccount.getHonorificSuffix() != null) {
			existingUserAccount.setHonorificSuffix(
				userAccount.getHonorificSuffix());
		}

		if (userAccount.getImageExternalReferenceCode() != null) {
			existingUserAccount.setImageExternalReferenceCode(
				userAccount.getImageExternalReferenceCode());
		}

		if (userAccount.getImageId() != null) {
			existingUserAccount.setImageId(userAccount.getImageId());
		}

		if (userAccount.getJobTitle() != null) {
			existingUserAccount.setJobTitle(userAccount.getJobTitle());
		}

		if (userAccount.getLanguageId() != null) {
			existingUserAccount.setLanguageId(userAccount.getLanguageId());
		}

		if (userAccount.getPassword() != null) {
			existingUserAccount.setPassword(userAccount.getPassword());
		}

		if (userAccount.getPermissions() != null) {
			existingUserAccount.setPermissions(userAccount.getPermissions());
		}

		if (userAccount.getStatus() != null) {
			existingUserAccount.setStatus(userAccount.getStatus());
		}

		preparePatch(userAccount, existingUserAccount);

		return putUserAccount(userAccountId, existingUserAccount);
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -X 'PATCH' 'http://localhost:8080/o/headless-admin-user/v1.0/user-accounts/by-external-reference-code/{externalReferenceCode}' -d $'{"additionalName": ___, "alternateName": ___, "birthDate": ___, "currentPassword": ___, "customFields": ___, "emailAddress": ___, "externalReferenceCode": ___, "familyName": ___, "gender": ___, "givenName": ___, "honorificPrefix": ___, "honorificSuffix": ___, "id": ___, "imageExternalReferenceCode": ___, "imageId": ___, "jobTitle": ___, "languageId": ___, "password": ___, "permissions": ___, "status": ___, "userAccountContactInformation": ___}' --header 'Content-Type: application/json' -u 'test@liferay.com:test'
	 */
	@io.swagger.v3.oas.annotations.Operation(
		description = "Updates the user account with information sent in the request body. Only the provided fields are updated."
	)
	@io.swagger.v3.oas.annotations.Parameters(
		value = {
			@io.swagger.v3.oas.annotations.Parameter(
				in = io.swagger.v3.oas.annotations.enums.ParameterIn.PATH,
				name = "externalReferenceCode"
			)
		}
	)
	@io.swagger.v3.oas.annotations.tags.Tags(
		value = {@io.swagger.v3.oas.annotations.tags.Tag(name = "UserAccount")}
	)
	@jakarta.ws.rs.Consumes({"application/json", "application/xml"})
	@jakarta.ws.rs.PATCH
	@jakarta.ws.rs.Path(
		"/user-accounts/by-external-reference-code/{externalReferenceCode}"
	)
	@jakarta.ws.rs.Produces({"application/json", "application/xml"})
	@Override
	public UserAccount patchUserAccountByExternalReferenceCode(
			@io.swagger.v3.oas.annotations.Parameter(hidden = true)
			@jakarta.validation.constraints.NotNull
			@jakarta.ws.rs.PathParam("externalReferenceCode")
			String externalReferenceCode,
			UserAccount userAccount)
		throws Exception {

		UserAccount existingUserAccount = getUserAccountByExternalReferenceCode(
			externalReferenceCode);

		if (userAccount.getAdditionalName() != null) {
			existingUserAccount.setAdditionalName(
				userAccount.getAdditionalName());
		}

		if (userAccount.getAlternateName() != null) {
			existingUserAccount.setAlternateName(
				userAccount.getAlternateName());
		}

		if (userAccount.getBirthDate() != null) {
			existingUserAccount.setBirthDate(userAccount.getBirthDate());
		}

		if (userAccount.getCurrentPassword() != null) {
			existingUserAccount.setCurrentPassword(
				userAccount.getCurrentPassword());
		}

		if (userAccount.getCustomFields() != null) {
			existingUserAccount.setCustomFields(userAccount.getCustomFields());
		}

		if (userAccount.getEmailAddress() != null) {
			existingUserAccount.setEmailAddress(userAccount.getEmailAddress());
		}

		if (userAccount.getExternalReferenceCode() != null) {
			existingUserAccount.setExternalReferenceCode(
				userAccount.getExternalReferenceCode());
		}

		if (userAccount.getFamilyName() != null) {
			existingUserAccount.setFamilyName(userAccount.getFamilyName());
		}

		if (userAccount.getGender() != null) {
			existingUserAccount.setGender(userAccount.getGender());
		}

		if (userAccount.getGivenName() != null) {
			existingUserAccount.setGivenName(userAccount.getGivenName());
		}

		if (userAccount.getHonorificPrefix() != null) {
			existingUserAccount.setHonorificPrefix(
				userAccount.getHonorificPrefix());
		}

		if (userAccount.getHonorificSuffix() != null) {
			existingUserAccount.setHonorificSuffix(
				userAccount.getHonorificSuffix());
		}

		if (userAccount.getImageExternalReferenceCode() != null) {
			existingUserAccount.setImageExternalReferenceCode(
				userAccount.getImageExternalReferenceCode());
		}

		if (userAccount.getImageId() != null) {
			existingUserAccount.setImageId(userAccount.getImageId());
		}

		if (userAccount.getJobTitle() != null) {
			existingUserAccount.setJobTitle(userAccount.getJobTitle());
		}

		if (userAccount.getLanguageId() != null) {
			existingUserAccount.setLanguageId(userAccount.getLanguageId());
		}

		if (userAccount.getPassword() != null) {
			existingUserAccount.setPassword(userAccount.getPassword());
		}

		if (userAccount.getPermissions() != null) {
			existingUserAccount.setPermissions(userAccount.getPermissions());
		}

		if (userAccount.getStatus() != null) {
			existingUserAccount.setStatus(userAccount.getStatus());
		}

		preparePatch(userAccount, existingUserAccount);

		return putUserAccountByExternalReferenceCode(
			externalReferenceCode, existingUserAccount);
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -X 'POST' 'http://localhost:8080/o/headless-admin-user/v1.0/accounts/by-external-reference-code/{accountExternalReferenceCode}/user-accounts/by-external-reference-code/{externalReferenceCode}'  -u 'test@liferay.com:test'
	 */
	@io.swagger.v3.oas.annotations.Operation(
		description = "Assigns a user by their external reference code to an account by external reference code"
	)
	@io.swagger.v3.oas.annotations.Parameters(
		value = {
			@io.swagger.v3.oas.annotations.Parameter(
				in = io.swagger.v3.oas.annotations.enums.ParameterIn.PATH,
				name = "accountExternalReferenceCode"
			),
			@io.swagger.v3.oas.annotations.Parameter(
				in = io.swagger.v3.oas.annotations.enums.ParameterIn.PATH,
				name = "externalReferenceCode"
			)
		}
	)
	@io.swagger.v3.oas.annotations.tags.Tags(
		value = {@io.swagger.v3.oas.annotations.tags.Tag(name = "UserAccount")}
	)
	@jakarta.ws.rs.Path(
		"/accounts/by-external-reference-code/{accountExternalReferenceCode}/user-accounts/by-external-reference-code/{externalReferenceCode}"
	)
	@jakarta.ws.rs.POST
	@jakarta.ws.rs.Produces({"application/json", "application/xml"})
	@Override
	public void
			postAccountByExternalReferenceCodeUserAccountByExternalReferenceCode(
				@io.swagger.v3.oas.annotations.Parameter(hidden = true)
				@jakarta.validation.constraints.NotNull
				@jakarta.ws.rs.PathParam("accountExternalReferenceCode")
				String accountExternalReferenceCode,
				@io.swagger.v3.oas.annotations.Parameter(hidden = true)
				@jakarta.validation.constraints.NotNull
				@jakarta.ws.rs.PathParam("externalReferenceCode")
				String externalReferenceCode)
		throws Exception {
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -X 'POST' 'http://localhost:8080/o/headless-admin-user/v1.0/accounts/{accountId}/user-accounts' -d $'{"additionalName": ___, "alternateName": ___, "birthDate": ___, "currentPassword": ___, "customFields": ___, "emailAddress": ___, "externalReferenceCode": ___, "familyName": ___, "gender": ___, "givenName": ___, "honorificPrefix": ___, "honorificSuffix": ___, "id": ___, "imageExternalReferenceCode": ___, "imageId": ___, "jobTitle": ___, "languageId": ___, "password": ___, "permissions": ___, "status": ___, "userAccountContactInformation": ___}' --header 'Content-Type: application/json' -u 'test@liferay.com:test'
	 */
	@io.swagger.v3.oas.annotations.Operation(
		description = "Creates a user and assigns them to the account"
	)
	@io.swagger.v3.oas.annotations.Parameters(
		value = {
			@io.swagger.v3.oas.annotations.Parameter(
				in = io.swagger.v3.oas.annotations.enums.ParameterIn.PATH,
				name = "accountId"
			)
		}
	)
	@io.swagger.v3.oas.annotations.tags.Tags(
		value = {@io.swagger.v3.oas.annotations.tags.Tag(name = "UserAccount")}
	)
	@jakarta.ws.rs.Consumes({"application/json", "application/xml"})
	@jakarta.ws.rs.Path("/accounts/{accountId}/user-accounts")
	@jakarta.ws.rs.POST
	@jakarta.ws.rs.Produces({"application/json", "application/xml"})
	@Override
	public UserAccount postAccountUserAccount(
			@io.swagger.v3.oas.annotations.Parameter(hidden = true)
			@jakarta.validation.constraints.NotNull
			@jakarta.ws.rs.PathParam("accountId")
			Long accountId,
			UserAccount userAccount)
		throws Exception {

		return new UserAccount();
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -X 'POST' 'http://localhost:8080/o/headless-admin-user/v1.0/accounts/{accountId}/user-accounts/batch'  -u 'test@liferay.com:test'
	 */
	@io.swagger.v3.oas.annotations.Parameters(
		value = {
			@io.swagger.v3.oas.annotations.Parameter(
				in = io.swagger.v3.oas.annotations.enums.ParameterIn.PATH,
				name = "accountId"
			),
			@io.swagger.v3.oas.annotations.Parameter(
				in = io.swagger.v3.oas.annotations.enums.ParameterIn.QUERY,
				name = "callbackURL"
			)
		}
	)
	@io.swagger.v3.oas.annotations.tags.Tags(
		value = {@io.swagger.v3.oas.annotations.tags.Tag(name = "UserAccount")}
	)
	@jakarta.ws.rs.Consumes("application/json")
	@jakarta.ws.rs.Path("/accounts/{accountId}/user-accounts/batch")
	@jakarta.ws.rs.POST
	@jakarta.ws.rs.Produces("application/json")
	@Override
	public Response postAccountUserAccountBatch(
			@io.swagger.v3.oas.annotations.Parameter(hidden = true)
			@jakarta.validation.constraints.NotNull
			@jakarta.ws.rs.PathParam("accountId")
			Long accountId,
			@io.swagger.v3.oas.annotations.Parameter(hidden = true)
			@jakarta.ws.rs.QueryParam("callbackURL")
			String callbackURL,
			Object object)
		throws Exception {

		vulcanBatchEngineImportTaskResource.setContextAcceptLanguage(
			contextAcceptLanguage);
		vulcanBatchEngineImportTaskResource.setContextCompany(contextCompany);
		vulcanBatchEngineImportTaskResource.setContextHttpServletRequest(
			contextHttpServletRequest);
		vulcanBatchEngineImportTaskResource.setContextUriInfo(contextUriInfo);
		vulcanBatchEngineImportTaskResource.setContextUser(contextUser);

		Response.ResponseBuilder responseBuilder = Response.accepted();

		return responseBuilder.entity(
			vulcanBatchEngineImportTaskResource.postImportTask(
				UserAccount.class.getName(), callbackURL, null, object)
		).build();
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -X 'POST' 'http://localhost:8080/o/headless-admin-user/v1.0/accounts/{accountId}/user-accounts/by-email-address/{emailAddress}'  -u 'test@liferay.com:test'
	 */
	@io.swagger.v3.oas.annotations.Operation(
		description = "Assigns a user to an account by their email address"
	)
	@io.swagger.v3.oas.annotations.Parameters(
		value = {
			@io.swagger.v3.oas.annotations.Parameter(
				in = io.swagger.v3.oas.annotations.enums.ParameterIn.PATH,
				name = "accountId"
			),
			@io.swagger.v3.oas.annotations.Parameter(
				in = io.swagger.v3.oas.annotations.enums.ParameterIn.PATH,
				name = "emailAddress"
			)
		}
	)
	@io.swagger.v3.oas.annotations.tags.Tags(
		value = {@io.swagger.v3.oas.annotations.tags.Tag(name = "UserAccount")}
	)
	@jakarta.ws.rs.Path(
		"/accounts/{accountId}/user-accounts/by-email-address/{emailAddress}"
	)
	@jakarta.ws.rs.POST
	@jakarta.ws.rs.Produces({"application/json", "application/xml"})
	@Override
	public UserAccount postAccountUserAccountByEmailAddress(
			@io.swagger.v3.oas.annotations.Parameter(hidden = true)
			@jakarta.validation.constraints.NotNull
			@jakarta.ws.rs.PathParam("accountId")
			Long accountId,
			@io.swagger.v3.oas.annotations.Parameter(hidden = true)
			@jakarta.validation.constraints.NotNull
			@jakarta.ws.rs.PathParam("emailAddress")
			String emailAddress)
		throws Exception {

		return new UserAccount();
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -X 'POST' 'http://localhost:8080/o/headless-admin-user/v1.0/accounts/by-external-reference-code/{externalReferenceCode}/user-accounts' -d $'{"additionalName": ___, "alternateName": ___, "birthDate": ___, "currentPassword": ___, "customFields": ___, "emailAddress": ___, "externalReferenceCode": ___, "familyName": ___, "gender": ___, "givenName": ___, "honorificPrefix": ___, "honorificSuffix": ___, "id": ___, "imageExternalReferenceCode": ___, "imageId": ___, "jobTitle": ___, "languageId": ___, "password": ___, "permissions": ___, "status": ___, "userAccountContactInformation": ___}' --header 'Content-Type: application/json' -u 'test@liferay.com:test'
	 */
	@io.swagger.v3.oas.annotations.Operation(
		description = "Creates a user and assigns them to the account"
	)
	@io.swagger.v3.oas.annotations.Parameters(
		value = {
			@io.swagger.v3.oas.annotations.Parameter(
				in = io.swagger.v3.oas.annotations.enums.ParameterIn.PATH,
				name = "externalReferenceCode"
			)
		}
	)
	@io.swagger.v3.oas.annotations.tags.Tags(
		value = {@io.swagger.v3.oas.annotations.tags.Tag(name = "UserAccount")}
	)
	@jakarta.ws.rs.Consumes({"application/json", "application/xml"})
	@jakarta.ws.rs.Path(
		"/accounts/by-external-reference-code/{externalReferenceCode}/user-accounts"
	)
	@jakarta.ws.rs.POST
	@jakarta.ws.rs.Produces({"application/json", "application/xml"})
	@Override
	public UserAccount postAccountUserAccountByExternalReferenceCode(
			@io.swagger.v3.oas.annotations.Parameter(hidden = true)
			@jakarta.validation.constraints.NotNull
			@jakarta.ws.rs.PathParam("externalReferenceCode")
			String externalReferenceCode,
			UserAccount userAccount)
		throws Exception {

		return new UserAccount();
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -X 'POST' 'http://localhost:8080/o/headless-admin-user/v1.0/accounts/by-external-reference-code/{externalReferenceCode}/user-accounts/by-email-address/{emailAddress}'  -u 'test@liferay.com:test'
	 */
	@io.swagger.v3.oas.annotations.Operation(
		description = "Assigns a user to an account by external reference code by their email address"
	)
	@io.swagger.v3.oas.annotations.Parameters(
		value = {
			@io.swagger.v3.oas.annotations.Parameter(
				in = io.swagger.v3.oas.annotations.enums.ParameterIn.PATH,
				name = "externalReferenceCode"
			),
			@io.swagger.v3.oas.annotations.Parameter(
				in = io.swagger.v3.oas.annotations.enums.ParameterIn.PATH,
				name = "emailAddress"
			)
		}
	)
	@io.swagger.v3.oas.annotations.tags.Tags(
		value = {@io.swagger.v3.oas.annotations.tags.Tag(name = "UserAccount")}
	)
	@jakarta.ws.rs.Path(
		"/accounts/by-external-reference-code/{externalReferenceCode}/user-accounts/by-email-address/{emailAddress}"
	)
	@jakarta.ws.rs.POST
	@jakarta.ws.rs.Produces({"application/json", "application/xml"})
	@Override
	public UserAccount
			postAccountUserAccountByExternalReferenceCodeByEmailAddress(
				@io.swagger.v3.oas.annotations.Parameter(hidden = true)
				@jakarta.validation.constraints.NotNull
				@jakarta.ws.rs.PathParam("externalReferenceCode")
				String externalReferenceCode,
				@io.swagger.v3.oas.annotations.Parameter(hidden = true)
				@jakarta.validation.constraints.NotNull
				@jakarta.ws.rs.PathParam("emailAddress")
				String emailAddress)
		throws Exception {

		return new UserAccount();
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -X 'POST' 'http://localhost:8080/o/headless-admin-user/v1.0/accounts/{accountId}/user-accounts/by-email-address'  -u 'test@liferay.com:test'
	 */
	@io.swagger.v3.oas.annotations.Operation(
		description = "Assigns users to an account by their email addresses",
		requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "A list of user email addresses to assign to the account")
	)
	@io.swagger.v3.oas.annotations.Parameters(
		value = {
			@io.swagger.v3.oas.annotations.Parameter(
				in = io.swagger.v3.oas.annotations.enums.ParameterIn.PATH,
				name = "accountId"
			),
			@io.swagger.v3.oas.annotations.Parameter(
				in = io.swagger.v3.oas.annotations.enums.ParameterIn.QUERY,
				name = "accountRoleIds"
			)
		}
	)
	@io.swagger.v3.oas.annotations.tags.Tags(
		value = {@io.swagger.v3.oas.annotations.tags.Tag(name = "UserAccount")}
	)
	@jakarta.ws.rs.Consumes({"application/json", "application/xml"})
	@jakarta.ws.rs.Path("/accounts/{accountId}/user-accounts/by-email-address")
	@jakarta.ws.rs.POST
	@jakarta.ws.rs.Produces({"application/json", "application/xml"})
	@Override
	public Page<UserAccount> postAccountUserAccountsByEmailAddress(
			@io.swagger.v3.oas.annotations.Parameter(hidden = true)
			@jakarta.validation.constraints.NotNull
			@jakarta.ws.rs.PathParam("accountId")
			Long accountId,
			@io.swagger.v3.oas.annotations.Parameter(hidden = true)
			@jakarta.ws.rs.QueryParam("accountRoleIds")
			String accountRoleIds,
			String[] strings)
		throws Exception {

		return Page.of(Collections.emptyList());
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -X 'POST' 'http://localhost:8080/o/headless-admin-user/v1.0/accounts/by-external-reference-code/{externalReferenceCode}/user-accounts/by-email-address'  -u 'test@liferay.com:test'
	 */
	@io.swagger.v3.oas.annotations.Operation(
		description = "Assigns users to an account by their email addresses",
		requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "A list of user email addresses to assign to the account")
	)
	@io.swagger.v3.oas.annotations.Parameters(
		value = {
			@io.swagger.v3.oas.annotations.Parameter(
				in = io.swagger.v3.oas.annotations.enums.ParameterIn.PATH,
				name = "externalReferenceCode"
			),
			@io.swagger.v3.oas.annotations.Parameter(
				in = io.swagger.v3.oas.annotations.enums.ParameterIn.QUERY,
				name = "accountRoleIds"
			)
		}
	)
	@io.swagger.v3.oas.annotations.tags.Tags(
		value = {@io.swagger.v3.oas.annotations.tags.Tag(name = "UserAccount")}
	)
	@jakarta.ws.rs.Consumes({"application/json", "application/xml"})
	@jakarta.ws.rs.Path(
		"/accounts/by-external-reference-code/{externalReferenceCode}/user-accounts/by-email-address"
	)
	@jakarta.ws.rs.POST
	@jakarta.ws.rs.Produces({"application/json", "application/xml"})
	@Override
	public Page<UserAccount>
			postAccountUserAccountsByExternalReferenceCodeByEmailAddress(
				@io.swagger.v3.oas.annotations.Parameter(hidden = true)
				@jakarta.validation.constraints.NotNull
				@jakarta.ws.rs.PathParam("externalReferenceCode")
				String externalReferenceCode,
				@io.swagger.v3.oas.annotations.Parameter(hidden = true)
				@jakarta.ws.rs.QueryParam("accountRoleIds")
				String accountRoleIds,
				String[] strings)
		throws Exception {

		return Page.of(Collections.emptyList());
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -X 'POST' 'http://localhost:8080/o/headless-admin-user/v1.0/accounts/{accountId}/user-accounts/export-batch'  -u 'test@liferay.com:test'
	 */
	@io.swagger.v3.oas.annotations.Parameters(
		value = {
			@io.swagger.v3.oas.annotations.Parameter(
				in = io.swagger.v3.oas.annotations.enums.ParameterIn.PATH,
				name = "accountId"
			),
			@io.swagger.v3.oas.annotations.Parameter(
				in = io.swagger.v3.oas.annotations.enums.ParameterIn.QUERY,
				name = "filter"
			),
			@io.swagger.v3.oas.annotations.Parameter(
				in = io.swagger.v3.oas.annotations.enums.ParameterIn.QUERY,
				name = "search"
			),
			@io.swagger.v3.oas.annotations.Parameter(
				in = io.swagger.v3.oas.annotations.enums.ParameterIn.QUERY,
				name = "sort"
			),
			@io.swagger.v3.oas.annotations.Parameter(
				in = io.swagger.v3.oas.annotations.enums.ParameterIn.QUERY,
				name = "callbackURL"
			),
			@io.swagger.v3.oas.annotations.Parameter(
				in = io.swagger.v3.oas.annotations.enums.ParameterIn.QUERY,
				name = "contentType"
			),
			@io.swagger.v3.oas.annotations.Parameter(
				in = io.swagger.v3.oas.annotations.enums.ParameterIn.QUERY,
				name = "fieldNames"
			)
		}
	)
	@io.swagger.v3.oas.annotations.tags.Tags(
		value = {@io.swagger.v3.oas.annotations.tags.Tag(name = "UserAccount")}
	)
	@jakarta.ws.rs.Consumes("application/json")
	@jakarta.ws.rs.Path("/accounts/{accountId}/user-accounts/export-batch")
	@jakarta.ws.rs.POST
	@jakarta.ws.rs.Produces("application/json")
	@Override
	public Response postAccountUserAccountsPageExportBatch(
			@io.swagger.v3.oas.annotations.Parameter(hidden = true)
			@jakarta.validation.constraints.NotNull
			@jakarta.ws.rs.PathParam("accountId")
			Long accountId,
			@io.swagger.v3.oas.annotations.Parameter(hidden = true)
			@jakarta.ws.rs.QueryParam("search")
			String search,
			@jakarta.ws.rs.core.Context
				com.liferay.portal.kernel.search.filter.Filter filter,
			@jakarta.ws.rs.core.Context com.liferay.portal.kernel.search.Sort[]
				sorts,
			@io.swagger.v3.oas.annotations.Parameter(hidden = true)
			@jakarta.ws.rs.QueryParam("callbackURL")
			String callbackURL,
			@io.swagger.v3.oas.annotations.Parameter(hidden = true)
			@jakarta.ws.rs.DefaultValue("JSON")
			@jakarta.ws.rs.QueryParam("contentType")
			String contentType,
			@io.swagger.v3.oas.annotations.Parameter(hidden = true)
			@jakarta.ws.rs.QueryParam("fieldNames")
			String fieldNames)
		throws Exception {

		vulcanBatchEngineExportTaskResource.setContextAcceptLanguage(
			contextAcceptLanguage);
		vulcanBatchEngineExportTaskResource.setContextCompany(contextCompany);
		vulcanBatchEngineExportTaskResource.setContextHttpServletRequest(
			contextHttpServletRequest);
		vulcanBatchEngineExportTaskResource.setContextUriInfo(contextUriInfo);
		vulcanBatchEngineExportTaskResource.setContextUser(contextUser);
		vulcanBatchEngineExportTaskResource.setGroupLocalService(
			groupLocalService);

		Response.ResponseBuilder responseBuilder = Response.accepted();

		return responseBuilder.entity(
			vulcanBatchEngineExportTaskResource.postExportTask(
				UserAccount.class.getName(), callbackURL, contentType,
				fieldNames)
		).build();
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -X 'POST' 'http://localhost:8080/o/headless-admin-user/v1.0/organizations/{organizationId}/user-accounts/export-batch'  -u 'test@liferay.com:test'
	 */
	@io.swagger.v3.oas.annotations.Parameters(
		value = {
			@io.swagger.v3.oas.annotations.Parameter(
				in = io.swagger.v3.oas.annotations.enums.ParameterIn.PATH,
				name = "organizationId"
			),
			@io.swagger.v3.oas.annotations.Parameter(
				in = io.swagger.v3.oas.annotations.enums.ParameterIn.QUERY,
				name = "filter"
			),
			@io.swagger.v3.oas.annotations.Parameter(
				in = io.swagger.v3.oas.annotations.enums.ParameterIn.QUERY,
				name = "search"
			),
			@io.swagger.v3.oas.annotations.Parameter(
				in = io.swagger.v3.oas.annotations.enums.ParameterIn.QUERY,
				name = "sort"
			),
			@io.swagger.v3.oas.annotations.Parameter(
				in = io.swagger.v3.oas.annotations.enums.ParameterIn.QUERY,
				name = "callbackURL"
			),
			@io.swagger.v3.oas.annotations.Parameter(
				in = io.swagger.v3.oas.annotations.enums.ParameterIn.QUERY,
				name = "contentType"
			),
			@io.swagger.v3.oas.annotations.Parameter(
				in = io.swagger.v3.oas.annotations.enums.ParameterIn.QUERY,
				name = "fieldNames"
			)
		}
	)
	@io.swagger.v3.oas.annotations.tags.Tags(
		value = {@io.swagger.v3.oas.annotations.tags.Tag(name = "UserAccount")}
	)
	@jakarta.ws.rs.Consumes("application/json")
	@jakarta.ws.rs.Path(
		"/organizations/{organizationId}/user-accounts/export-batch"
	)
	@jakarta.ws.rs.POST
	@jakarta.ws.rs.Produces("application/json")
	@Override
	public Response postOrganizationUserAccountsPageExportBatch(
			@io.swagger.v3.oas.annotations.Parameter(hidden = true)
			@jakarta.validation.constraints.NotNull
			@jakarta.ws.rs.PathParam("organizationId")
			String organizationId,
			@io.swagger.v3.oas.annotations.Parameter(hidden = true)
			@jakarta.ws.rs.QueryParam("search")
			String search,
			@jakarta.ws.rs.core.Context
				com.liferay.portal.kernel.search.filter.Filter filter,
			@jakarta.ws.rs.core.Context com.liferay.portal.kernel.search.Sort[]
				sorts,
			@io.swagger.v3.oas.annotations.Parameter(hidden = true)
			@jakarta.ws.rs.QueryParam("callbackURL")
			String callbackURL,
			@io.swagger.v3.oas.annotations.Parameter(hidden = true)
			@jakarta.ws.rs.DefaultValue("JSON")
			@jakarta.ws.rs.QueryParam("contentType")
			String contentType,
			@io.swagger.v3.oas.annotations.Parameter(hidden = true)
			@jakarta.ws.rs.QueryParam("fieldNames")
			String fieldNames)
		throws Exception {

		vulcanBatchEngineExportTaskResource.setContextAcceptLanguage(
			contextAcceptLanguage);
		vulcanBatchEngineExportTaskResource.setContextCompany(contextCompany);
		vulcanBatchEngineExportTaskResource.setContextHttpServletRequest(
			contextHttpServletRequest);
		vulcanBatchEngineExportTaskResource.setContextUriInfo(contextUriInfo);
		vulcanBatchEngineExportTaskResource.setContextUser(contextUser);
		vulcanBatchEngineExportTaskResource.setGroupLocalService(
			groupLocalService);

		Response.ResponseBuilder responseBuilder = Response.accepted();

		return responseBuilder.entity(
			vulcanBatchEngineExportTaskResource.postExportTask(
				UserAccount.class.getName(), callbackURL, contentType,
				fieldNames)
		).build();
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -X 'POST' 'http://localhost:8080/o/headless-admin-user/v1.0/sites/{siteId}/user-accounts/export-batch'  -u 'test@liferay.com:test'
	 */
	@io.swagger.v3.oas.annotations.Parameters(
		value = {
			@io.swagger.v3.oas.annotations.Parameter(
				in = io.swagger.v3.oas.annotations.enums.ParameterIn.PATH,
				name = "siteId"
			),
			@io.swagger.v3.oas.annotations.Parameter(
				in = io.swagger.v3.oas.annotations.enums.ParameterIn.QUERY,
				name = "filter"
			),
			@io.swagger.v3.oas.annotations.Parameter(
				in = io.swagger.v3.oas.annotations.enums.ParameterIn.QUERY,
				name = "search"
			),
			@io.swagger.v3.oas.annotations.Parameter(
				in = io.swagger.v3.oas.annotations.enums.ParameterIn.QUERY,
				name = "sort"
			),
			@io.swagger.v3.oas.annotations.Parameter(
				in = io.swagger.v3.oas.annotations.enums.ParameterIn.QUERY,
				name = "callbackURL"
			),
			@io.swagger.v3.oas.annotations.Parameter(
				in = io.swagger.v3.oas.annotations.enums.ParameterIn.QUERY,
				name = "contentType"
			),
			@io.swagger.v3.oas.annotations.Parameter(
				in = io.swagger.v3.oas.annotations.enums.ParameterIn.QUERY,
				name = "fieldNames"
			)
		}
	)
	@io.swagger.v3.oas.annotations.tags.Tags(
		value = {@io.swagger.v3.oas.annotations.tags.Tag(name = "UserAccount")}
	)
	@jakarta.ws.rs.Consumes("application/json")
	@jakarta.ws.rs.Path("/sites/{siteId}/user-accounts/export-batch")
	@jakarta.ws.rs.POST
	@jakarta.ws.rs.Produces("application/json")
	@Override
	public Response postSiteUserAccountsPageExportBatch(
			@io.swagger.v3.oas.annotations.Parameter(hidden = true)
			@jakarta.validation.constraints.NotNull
			@jakarta.ws.rs.PathParam("siteId")
			Long siteId,
			@io.swagger.v3.oas.annotations.Parameter(hidden = true)
			@jakarta.ws.rs.QueryParam("search")
			String search,
			@jakarta.ws.rs.core.Context
				com.liferay.portal.kernel.search.filter.Filter filter,
			@jakarta.ws.rs.core.Context com.liferay.portal.kernel.search.Sort[]
				sorts,
			@io.swagger.v3.oas.annotations.Parameter(hidden = true)
			@jakarta.ws.rs.QueryParam("callbackURL")
			String callbackURL,
			@io.swagger.v3.oas.annotations.Parameter(hidden = true)
			@jakarta.ws.rs.DefaultValue("JSON")
			@jakarta.ws.rs.QueryParam("contentType")
			String contentType,
			@io.swagger.v3.oas.annotations.Parameter(hidden = true)
			@jakarta.ws.rs.QueryParam("fieldNames")
			String fieldNames)
		throws Exception {

		vulcanBatchEngineExportTaskResource.setContextAcceptLanguage(
			contextAcceptLanguage);
		vulcanBatchEngineExportTaskResource.setContextCompany(contextCompany);
		vulcanBatchEngineExportTaskResource.setContextHttpServletRequest(
			contextHttpServletRequest);
		vulcanBatchEngineExportTaskResource.setContextUriInfo(contextUriInfo);
		vulcanBatchEngineExportTaskResource.setContextUser(contextUser);
		vulcanBatchEngineExportTaskResource.setGroupLocalService(
			groupLocalService);

		Response.ResponseBuilder responseBuilder = Response.accepted();

		return responseBuilder.entity(
			vulcanBatchEngineExportTaskResource.postExportTask(
				UserAccount.class.getName(), callbackURL, contentType,
				fieldNames)
		).build();
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -X 'POST' 'http://localhost:8080/o/headless-admin-user/v1.0/user-accounts' -d $'{"additionalName": ___, "alternateName": ___, "birthDate": ___, "currentPassword": ___, "customFields": ___, "emailAddress": ___, "externalReferenceCode": ___, "familyName": ___, "gender": ___, "givenName": ___, "honorificPrefix": ___, "honorificSuffix": ___, "id": ___, "imageExternalReferenceCode": ___, "imageId": ___, "jobTitle": ___, "languageId": ___, "password": ___, "permissions": ___, "status": ___, "userAccountContactInformation": ___}' --header 'Content-Type: application/json' -u 'test@liferay.com:test'
	 */
	@io.swagger.v3.oas.annotations.Operation(
		description = "Creates a new user account"
	)
	@io.swagger.v3.oas.annotations.Parameters(
		value = {
			@io.swagger.v3.oas.annotations.Parameter(
				in = io.swagger.v3.oas.annotations.enums.ParameterIn.QUERY,
				name = "captchaAnswer"
			),
			@io.swagger.v3.oas.annotations.Parameter(
				in = io.swagger.v3.oas.annotations.enums.ParameterIn.QUERY,
				name = "captchaToken"
			)
		}
	)
	@io.swagger.v3.oas.annotations.tags.Tags(
		value = {@io.swagger.v3.oas.annotations.tags.Tag(name = "UserAccount")}
	)
	@jakarta.ws.rs.Consumes({"application/json", "application/xml"})
	@jakarta.ws.rs.Path("/user-accounts")
	@jakarta.ws.rs.POST
	@jakarta.ws.rs.Produces({"application/json", "application/xml"})
	@Override
	public UserAccount postUserAccount(
			@io.swagger.v3.oas.annotations.Parameter(hidden = true)
			@jakarta.ws.rs.QueryParam("captchaAnswer")
			String captchaAnswer,
			@io.swagger.v3.oas.annotations.Parameter(hidden = true)
			@jakarta.ws.rs.QueryParam("captchaToken")
			String captchaToken,
			UserAccount userAccount)
		throws Exception {

		return new UserAccount();
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -X 'POST' 'http://localhost:8080/o/headless-admin-user/v1.0/user-accounts/batch'  -u 'test@liferay.com:test'
	 */
	@io.swagger.v3.oas.annotations.Parameters(
		value = {
			@io.swagger.v3.oas.annotations.Parameter(
				in = io.swagger.v3.oas.annotations.enums.ParameterIn.QUERY,
				name = "captchaAnswer"
			),
			@io.swagger.v3.oas.annotations.Parameter(
				in = io.swagger.v3.oas.annotations.enums.ParameterIn.QUERY,
				name = "captchaToken"
			),
			@io.swagger.v3.oas.annotations.Parameter(
				in = io.swagger.v3.oas.annotations.enums.ParameterIn.QUERY,
				name = "callbackURL"
			)
		}
	)
	@io.swagger.v3.oas.annotations.tags.Tags(
		value = {@io.swagger.v3.oas.annotations.tags.Tag(name = "UserAccount")}
	)
	@jakarta.ws.rs.Consumes("application/json")
	@jakarta.ws.rs.Path("/user-accounts/batch")
	@jakarta.ws.rs.POST
	@jakarta.ws.rs.Produces("application/json")
	@Override
	public Response postUserAccountBatch(
			@io.swagger.v3.oas.annotations.Parameter(hidden = true)
			@jakarta.ws.rs.QueryParam("captchaAnswer")
			String captchaAnswer,
			@io.swagger.v3.oas.annotations.Parameter(hidden = true)
			@jakarta.ws.rs.QueryParam("captchaToken")
			String captchaToken,
			@io.swagger.v3.oas.annotations.Parameter(hidden = true)
			@jakarta.ws.rs.QueryParam("callbackURL")
			String callbackURL,
			Object object)
		throws Exception {

		vulcanBatchEngineImportTaskResource.setContextAcceptLanguage(
			contextAcceptLanguage);
		vulcanBatchEngineImportTaskResource.setContextCompany(contextCompany);
		vulcanBatchEngineImportTaskResource.setContextHttpServletRequest(
			contextHttpServletRequest);
		vulcanBatchEngineImportTaskResource.setContextUriInfo(contextUriInfo);
		vulcanBatchEngineImportTaskResource.setContextUser(contextUser);

		Response.ResponseBuilder responseBuilder = Response.accepted();

		return responseBuilder.entity(
			vulcanBatchEngineImportTaskResource.postImportTask(
				UserAccount.class.getName(), callbackURL, null, object)
		).build();
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -X 'POST' 'http://localhost:8080/o/headless-admin-user/v1.0/user-accounts/{userAccountId}/image'  -u 'test@liferay.com:test'
	 */
	@io.swagger.v3.oas.annotations.Parameters(
		value = {
			@io.swagger.v3.oas.annotations.Parameter(
				in = io.swagger.v3.oas.annotations.enums.ParameterIn.PATH,
				name = "userAccountId"
			)
		}
	)
	@io.swagger.v3.oas.annotations.tags.Tags(
		value = {@io.swagger.v3.oas.annotations.tags.Tag(name = "UserAccount")}
	)
	@jakarta.ws.rs.Consumes("multipart/form-data")
	@jakarta.ws.rs.Path("/user-accounts/{userAccountId}/image")
	@jakarta.ws.rs.POST
	@Override
	public Response postUserAccountImage(
			@io.swagger.v3.oas.annotations.Parameter(hidden = true)
			@jakarta.validation.constraints.NotNull
			@jakarta.ws.rs.PathParam("userAccountId")
			Long userAccountId,
			MultipartBody multipartBody)
		throws Exception {

		Response.ResponseBuilder responseBuilder = Response.ok();

		return responseBuilder.build();
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -X 'POST' 'http://localhost:8080/o/headless-admin-user/v1.0/user-accounts/export-batch'  -u 'test@liferay.com:test'
	 */
	@io.swagger.v3.oas.annotations.Parameters(
		value = {
			@io.swagger.v3.oas.annotations.Parameter(
				in = io.swagger.v3.oas.annotations.enums.ParameterIn.QUERY,
				name = "filter"
			),
			@io.swagger.v3.oas.annotations.Parameter(
				in = io.swagger.v3.oas.annotations.enums.ParameterIn.QUERY,
				name = "search"
			),
			@io.swagger.v3.oas.annotations.Parameter(
				in = io.swagger.v3.oas.annotations.enums.ParameterIn.QUERY,
				name = "sort"
			),
			@io.swagger.v3.oas.annotations.Parameter(
				in = io.swagger.v3.oas.annotations.enums.ParameterIn.QUERY,
				name = "callbackURL"
			),
			@io.swagger.v3.oas.annotations.Parameter(
				in = io.swagger.v3.oas.annotations.enums.ParameterIn.QUERY,
				name = "contentType"
			),
			@io.swagger.v3.oas.annotations.Parameter(
				in = io.swagger.v3.oas.annotations.enums.ParameterIn.QUERY,
				name = "fieldNames"
			)
		}
	)
	@io.swagger.v3.oas.annotations.tags.Tags(
		value = {@io.swagger.v3.oas.annotations.tags.Tag(name = "UserAccount")}
	)
	@jakarta.ws.rs.Consumes("application/json")
	@jakarta.ws.rs.Path("/user-accounts/export-batch")
	@jakarta.ws.rs.POST
	@jakarta.ws.rs.Produces("application/json")
	@Override
	public Response postUserAccountsPageExportBatch(
			@io.swagger.v3.oas.annotations.Parameter(hidden = true)
			@jakarta.ws.rs.QueryParam("search")
			String search,
			@jakarta.ws.rs.core.Context
				com.liferay.portal.kernel.search.filter.Filter filter,
			@jakarta.ws.rs.core.Context com.liferay.portal.kernel.search.Sort[]
				sorts,
			@io.swagger.v3.oas.annotations.Parameter(hidden = true)
			@jakarta.ws.rs.QueryParam("callbackURL")
			String callbackURL,
			@io.swagger.v3.oas.annotations.Parameter(hidden = true)
			@jakarta.ws.rs.DefaultValue("JSON")
			@jakarta.ws.rs.QueryParam("contentType")
			String contentType,
			@io.swagger.v3.oas.annotations.Parameter(hidden = true)
			@jakarta.ws.rs.QueryParam("fieldNames")
			String fieldNames)
		throws Exception {

		vulcanBatchEngineExportTaskResource.setContextAcceptLanguage(
			contextAcceptLanguage);
		vulcanBatchEngineExportTaskResource.setContextCompany(contextCompany);
		vulcanBatchEngineExportTaskResource.setContextHttpServletRequest(
			contextHttpServletRequest);
		vulcanBatchEngineExportTaskResource.setContextUriInfo(contextUriInfo);
		vulcanBatchEngineExportTaskResource.setContextUser(contextUser);
		vulcanBatchEngineExportTaskResource.setGroupLocalService(
			groupLocalService);

		Response.ResponseBuilder responseBuilder = Response.accepted();

		return responseBuilder.entity(
			vulcanBatchEngineExportTaskResource.postExportTask(
				UserAccount.class.getName(), callbackURL, contentType,
				fieldNames)
		).build();
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -X 'PUT' 'http://localhost:8080/o/headless-admin-user/v1.0/user-accounts/{userAccountId}' -d $'{"additionalName": ___, "alternateName": ___, "birthDate": ___, "currentPassword": ___, "customFields": ___, "emailAddress": ___, "externalReferenceCode": ___, "familyName": ___, "gender": ___, "givenName": ___, "honorificPrefix": ___, "honorificSuffix": ___, "id": ___, "imageExternalReferenceCode": ___, "imageId": ___, "jobTitle": ___, "languageId": ___, "password": ___, "permissions": ___, "status": ___, "userAccountContactInformation": ___}' --header 'Content-Type: application/json' -u 'test@liferay.com:test'
	 */
	@io.swagger.v3.oas.annotations.Operation(
		description = "Replaces the user account with information sent in the request body. Any missing fields are deleted unless they are required."
	)
	@io.swagger.v3.oas.annotations.Parameters(
		value = {
			@io.swagger.v3.oas.annotations.Parameter(
				in = io.swagger.v3.oas.annotations.enums.ParameterIn.PATH,
				name = "userAccountId"
			)
		}
	)
	@io.swagger.v3.oas.annotations.tags.Tags(
		value = {@io.swagger.v3.oas.annotations.tags.Tag(name = "UserAccount")}
	)
	@jakarta.ws.rs.Consumes({"application/json", "application/xml"})
	@jakarta.ws.rs.Path("/user-accounts/{userAccountId}")
	@jakarta.ws.rs.Produces({"application/json", "application/xml"})
	@jakarta.ws.rs.PUT
	@Override
	public UserAccount putUserAccount(
			@io.swagger.v3.oas.annotations.Parameter(hidden = true)
			@jakarta.validation.constraints.NotNull
			@jakarta.ws.rs.PathParam("userAccountId")
			Long userAccountId,
			UserAccount userAccount)
		throws Exception {

		return new UserAccount();
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -X 'PUT' 'http://localhost:8080/o/headless-admin-user/v1.0/user-accounts/batch'  -u 'test@liferay.com:test'
	 */
	@io.swagger.v3.oas.annotations.Parameters(
		value = {
			@io.swagger.v3.oas.annotations.Parameter(
				in = io.swagger.v3.oas.annotations.enums.ParameterIn.QUERY,
				name = "callbackURL"
			)
		}
	)
	@io.swagger.v3.oas.annotations.tags.Tags(
		value = {@io.swagger.v3.oas.annotations.tags.Tag(name = "UserAccount")}
	)
	@jakarta.ws.rs.Consumes("application/json")
	@jakarta.ws.rs.Path("/user-accounts/batch")
	@jakarta.ws.rs.Produces("application/json")
	@jakarta.ws.rs.PUT
	@Override
	public Response putUserAccountBatch(
			@io.swagger.v3.oas.annotations.Parameter(hidden = true)
			@jakarta.ws.rs.QueryParam("callbackURL")
			String callbackURL,
			Object object)
		throws Exception {

		vulcanBatchEngineImportTaskResource.setContextAcceptLanguage(
			contextAcceptLanguage);
		vulcanBatchEngineImportTaskResource.setContextCompany(contextCompany);
		vulcanBatchEngineImportTaskResource.setContextHttpServletRequest(
			contextHttpServletRequest);
		vulcanBatchEngineImportTaskResource.setContextUriInfo(contextUriInfo);
		vulcanBatchEngineImportTaskResource.setContextUser(contextUser);

		Response.ResponseBuilder responseBuilder = Response.accepted();

		return responseBuilder.entity(
			vulcanBatchEngineImportTaskResource.putImportTask(
				UserAccount.class.getName(), callbackURL, object)
		).build();
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -X 'PUT' 'http://localhost:8080/o/headless-admin-user/v1.0/user-accounts/by-external-reference-code/{externalReferenceCode}' -d $'{"additionalName": ___, "alternateName": ___, "birthDate": ___, "currentPassword": ___, "customFields": ___, "emailAddress": ___, "externalReferenceCode": ___, "familyName": ___, "gender": ___, "givenName": ___, "honorificPrefix": ___, "honorificSuffix": ___, "id": ___, "imageExternalReferenceCode": ___, "imageId": ___, "jobTitle": ___, "languageId": ___, "password": ___, "permissions": ___, "status": ___, "userAccountContactInformation": ___}' --header 'Content-Type: application/json' -u 'test@liferay.com:test'
	 */
	@io.swagger.v3.oas.annotations.Parameters(
		value = {
			@io.swagger.v3.oas.annotations.Parameter(
				in = io.swagger.v3.oas.annotations.enums.ParameterIn.PATH,
				name = "externalReferenceCode"
			)
		}
	)
	@io.swagger.v3.oas.annotations.tags.Tags(
		value = {@io.swagger.v3.oas.annotations.tags.Tag(name = "UserAccount")}
	)
	@jakarta.ws.rs.Consumes({"application/json", "application/xml"})
	@jakarta.ws.rs.Path(
		"/user-accounts/by-external-reference-code/{externalReferenceCode}"
	)
	@jakarta.ws.rs.Produces({"application/json", "application/xml"})
	@jakarta.ws.rs.PUT
	@Override
	public UserAccount putUserAccountByExternalReferenceCode(
			@io.swagger.v3.oas.annotations.Parameter(hidden = true)
			@jakarta.validation.constraints.NotNull
			@jakarta.ws.rs.PathParam("externalReferenceCode")
			String externalReferenceCode,
			UserAccount userAccount)
		throws Exception {

		return new UserAccount();
	}

	@Override
	@SuppressWarnings("PMD.UnusedLocalVariable")
	public void create(
			Collection<UserAccount> userAccounts,
			Map<String, Serializable> parameters)
		throws Exception {

		UnsafeFunction<UserAccount, UserAccount, Exception>
			userAccountUnsafeFunction = null;

		String createStrategy = (String)parameters.getOrDefault(
			"createStrategy", "INSERT");

		if (StringUtil.equalsIgnoreCase(createStrategy, "INSERT")) {
			if (parameters.containsKey("accountId")) {
				userAccountUnsafeFunction =
					userAccount -> postAccountUserAccount(
						_parseLong((String)parameters.get("accountId")),
						userAccount);
			}
			else if (parameters.containsKey("externalReferenceCode")) {
				userAccountUnsafeFunction =
					userAccount ->
						postAccountUserAccountByExternalReferenceCode(
							(String)parameters.get("externalReferenceCode"),
							userAccount);
			}
			else {
				userAccountUnsafeFunction = userAccount -> postUserAccount(
					(String)parameters.get("captchaAnswer"),
					(String)parameters.get("captchaToken"), userAccount);
			}
		}

		if (StringUtil.equalsIgnoreCase(createStrategy, "UPSERT")) {
			String updateStrategy = (String)parameters.getOrDefault(
				"updateStrategy", "UPDATE");

			if (StringUtil.equalsIgnoreCase(updateStrategy, "PARTIAL_UPDATE")) {
				userAccountUnsafeFunction = userAccount -> {
					UserAccount getUserAccount = null;
					UserAccount persistedUserAccount = null;

					try {
						getUserAccount = getUserAccountByExternalReferenceCode(
							userAccount.getExternalReferenceCode());

						persistedUserAccount = patchUserAccount(
							getUserAccount.getId(), userAccount);
					}
					catch (NoSuchModelException noSuchModelException) {
						if (parameters.containsKey("accountId")) {
							persistedUserAccount = postAccountUserAccount(
								_parseLong((String)parameters.get("accountId")),
								userAccount);
						}
						else if (parameters.containsKey(
									"externalReferenceCode")) {

							persistedUserAccount =
								postAccountUserAccountByExternalReferenceCode(
									(String)parameters.get(
										"externalReferenceCode"),
									userAccount);
						}
						else {
							persistedUserAccount = postUserAccount(
								(String)parameters.get("captchaAnswer"),
								(String)parameters.get("captchaToken"),
								userAccount);
						}
					}

					return persistedUserAccount;
				};
			}

			if (StringUtil.equalsIgnoreCase(updateStrategy, "UPDATE")) {
				userAccountUnsafeFunction = userAccount -> {
					UserAccount persistedUserAccount = null;

					persistedUserAccount =
						putUserAccountByExternalReferenceCode(
							userAccount.getExternalReferenceCode(),
							userAccount);

					return persistedUserAccount;
				};
			}
		}

		if (userAccountUnsafeFunction == null) {
			throw new NotSupportedException(
				"Create strategy \"" + createStrategy +
					"\" is not supported for UserAccount");
		}

		if (contextBatchUnsafeBiConsumer != null) {
			contextBatchUnsafeBiConsumer.accept(
				userAccounts, userAccountUnsafeFunction);
		}
		else if (contextBatchUnsafeConsumer != null) {
			contextBatchUnsafeConsumer.accept(
				userAccounts, userAccountUnsafeFunction::apply);
		}
		else {
			for (UserAccount userAccount : userAccounts) {
				userAccountUnsafeFunction.apply(userAccount);
			}
		}
	}

	@Override
	public void delete(
			Collection<UserAccount> userAccounts,
			Map<String, Serializable> parameters)
		throws Exception {

		UnsafeFunction<UserAccount, UserAccount, Exception>
			userAccountUnsafeFunction = userAccount -> {
				if (userAccount.getId() != null) {
					try {
						deleteUserAccount(userAccount.getId());

						return userAccount;
					}
					catch (Exception exception) {
						if (userAccount.getExternalReferenceCode() != null) {
							deleteUserAccountByExternalReferenceCode(
								userAccount.getExternalReferenceCode());

							return userAccount;
						}
					}
				}
				else if (userAccount.getExternalReferenceCode() != null) {
					deleteUserAccountByExternalReferenceCode(
						userAccount.getExternalReferenceCode());

					return userAccount;
				}

				throw new UnsupportedOperationException(
					"Unable to delete by external reference code or ID");
			};

		if (contextBatchUnsafeBiConsumer != null) {
			contextBatchUnsafeBiConsumer.accept(
				userAccounts, userAccountUnsafeFunction);
		}
		else if (contextBatchUnsafeConsumer != null) {
			contextBatchUnsafeConsumer.accept(
				userAccounts, userAccountUnsafeFunction::apply);
		}
		else {
			for (UserAccount userAccount : userAccounts) {
				userAccountUnsafeFunction.apply(userAccount);
			}
		}
	}

	public Set<String> getAvailableCreateStrategies() {
		return SetUtil.fromArray("INSERT", "UPSERT");
	}

	public Set<String> getAvailableUpdateStrategies() {
		return SetUtil.fromArray("PARTIAL_UPDATE", "UPDATE");
	}

	@Override
	public EntityModel getEntityModel(Map<String, List<String>> multivaluedMap)
		throws Exception {

		return getEntityModel(
			new MultivaluedHashMap<String, Object>(multivaluedMap));
	}

	public String getResourceName() {
		return "UserAccount";
	}

	public String getVersion() {
		return "v1.0";
	}

	@Override
	public Page<UserAccount> read(
			com.liferay.portal.kernel.search.filter.Filter filter,
			Pagination pagination,
			com.liferay.portal.kernel.search.Sort[] sorts,
			Map<String, Serializable> parameters, String search)
		throws Exception {

		if (parameters.containsKey("accountId")) {
			return getAccountUserAccountsPage(
				_parseLong((String)parameters.get("accountId")), search, filter,
				pagination, sorts);
		}
		else if (parameters.containsKey("organizationId")) {
			return getOrganizationUserAccountsPage(
				(String)parameters.get("organizationId"), search, filter,
				pagination, sorts);
		}
		else if (parameters.containsKey("siteId")) {
			return getSiteUserAccountsPage(
				(Long)parameters.get("siteId"), search, filter, pagination,
				sorts);
		}
		else {
			return getUserAccountsPage(search, filter, pagination, sorts);
		}
	}

	@Override
	public void setLanguageId(String languageId) {
		this.contextAcceptLanguage = new AcceptLanguage() {

			@Override
			public List<Locale> getLocales() {
				return null;
			}

			@Override
			public String getPreferredLanguageId() {
				return languageId;
			}

			@Override
			public Locale getPreferredLocale() {
				return LocaleUtil.fromLanguageId(languageId);
			}

			@Override
			public boolean isAcceptAllLanguages() {
				if (ExportImportThreadLocal.isExportInProcess()) {
					return true;
				}

				return AcceptLanguage.super.isAcceptAllLanguages();
			}

		};
	}

	@Override
	public void update(
			Collection<UserAccount> userAccounts,
			Map<String, Serializable> parameters)
		throws Exception {

		UnsafeFunction<UserAccount, UserAccount, Exception>
			userAccountUnsafeFunction = null;

		String updateStrategy = (String)parameters.getOrDefault(
			"updateStrategy", "UPDATE");

		if (StringUtil.equalsIgnoreCase(updateStrategy, "PARTIAL_UPDATE")) {
			userAccountUnsafeFunction = userAccount -> patchUserAccount(
				userAccount.getId(), userAccount);
		}

		if (StringUtil.equalsIgnoreCase(updateStrategy, "UPDATE")) {
			userAccountUnsafeFunction = userAccount -> putUserAccount(
				userAccount.getId(), userAccount);
		}

		if (userAccountUnsafeFunction == null) {
			throw new NotSupportedException(
				"Update strategy \"" + updateStrategy +
					"\" is not supported for UserAccount");
		}

		if (contextBatchUnsafeBiConsumer != null) {
			contextBatchUnsafeBiConsumer.accept(
				userAccounts, userAccountUnsafeFunction);
		}
		else if (contextBatchUnsafeConsumer != null) {
			contextBatchUnsafeConsumer.accept(
				userAccounts, userAccountUnsafeFunction::apply);
		}
		else {
			for (UserAccount userAccount : userAccounts) {
				userAccountUnsafeFunction.apply(userAccount);
			}
		}
	}

	private Long _parseLong(String value) {
		if (value != null) {
			return Long.parseLong(value);
		}

		return null;
	}

	@Override
	public EntityModel getEntityModel(MultivaluedMap multivaluedMap)
		throws Exception {

		return null;
	}

	@Override
	public UserAccount getItem(Long id) throws Exception {
		return getUserAccount(id);
	}

	public void setContextAcceptLanguage(AcceptLanguage contextAcceptLanguage) {
		this.contextAcceptLanguage = contextAcceptLanguage;
	}

	public void setContextBatchUnsafeBiConsumer(
		UnsafeBiConsumer
			<Collection<UserAccount>,
			 UnsafeFunction<UserAccount, UserAccount, Exception>, Exception>
				contextBatchUnsafeBiConsumer) {

		this.contextBatchUnsafeBiConsumer = contextBatchUnsafeBiConsumer;
	}

	public void setContextBatchUnsafeConsumer(
		UnsafeBiConsumer
			<Collection<UserAccount>, UnsafeConsumer<UserAccount, Exception>,
			 Exception> contextBatchUnsafeConsumer) {

		this.contextBatchUnsafeConsumer = contextBatchUnsafeConsumer;
	}

	public void setContextCompany(
		com.liferay.portal.kernel.model.Company contextCompany) {

		this.contextCompany = contextCompany;
	}

	public void setContextHttpServletRequest(
		HttpServletRequest contextHttpServletRequest) {

		this.contextHttpServletRequest = contextHttpServletRequest;
	}

	public void setContextHttpServletResponse(
		HttpServletResponse contextHttpServletResponse) {

		this.contextHttpServletResponse = contextHttpServletResponse;
	}

	public void setContextUriInfo(UriInfo contextUriInfo) {
		this.contextUriInfo = UriInfoUtil.getVulcanUriInfo(
			getApplicationPath(), contextUriInfo);
	}

	public void setContextUser(
		com.liferay.portal.kernel.model.User contextUser) {

		this.contextUser = contextUser;
	}

	public void setExpressionConvert(
		ExpressionConvert<com.liferay.portal.kernel.search.filter.Filter>
			expressionConvert) {

		this.expressionConvert = expressionConvert;
	}

	public void setFilterParserProvider(
		FilterParserProvider filterParserProvider) {

		this.filterParserProvider = filterParserProvider;
	}

	public void setGroupLocalService(GroupLocalService groupLocalService) {
		this.groupLocalService = groupLocalService;
	}

	public void setResourceActionLocalService(
		ResourceActionLocalService resourceActionLocalService) {

		this.resourceActionLocalService = resourceActionLocalService;
	}

	public void setResourcePermissionLocalService(
		ResourcePermissionLocalService resourcePermissionLocalService) {

		this.resourcePermissionLocalService = resourcePermissionLocalService;
	}

	public void setRoleLocalService(RoleLocalService roleLocalService) {
		this.roleLocalService = roleLocalService;
	}

	public void setSortParserProvider(SortParserProvider sortParserProvider) {
		this.sortParserProvider = sortParserProvider;
	}

	protected String getApplicationPath() {
		return "headless-admin-user";
	}

	public void setVulcanBatchEngineExportTaskResource(
		VulcanBatchEngineExportTaskResource
			vulcanBatchEngineExportTaskResource) {

		this.vulcanBatchEngineExportTaskResource =
			vulcanBatchEngineExportTaskResource;
	}

	public void setVulcanBatchEngineImportTaskResource(
		VulcanBatchEngineImportTaskResource
			vulcanBatchEngineImportTaskResource) {

		this.vulcanBatchEngineImportTaskResource =
			vulcanBatchEngineImportTaskResource;
	}

	@Override
	public com.liferay.portal.kernel.search.filter.Filter toFilter(
		String filterString, Map<String, List<String>> multivaluedMap) {

		try {
			EntityModel entityModel = getEntityModel(multivaluedMap);

			FilterParser filterParser = filterParserProvider.provide(
				entityModel);

			com.liferay.portal.odata.filter.Filter oDataFilter =
				new com.liferay.portal.odata.filter.Filter(
					filterParser.parse(filterString));

			return expressionConvert.convert(
				oDataFilter.getExpression(),
				contextAcceptLanguage.getPreferredLocale(), entityModel);
		}
		catch (Exception exception) {
			_log.error("Invalid filter " + filterString, exception);

			return null;
		}
	}

	@Override
	public com.liferay.portal.kernel.search.Sort[] toSorts(String sortString) {
		if (Validator.isNull(sortString)) {
			return null;
		}

		try {
			SortParser sortParser = sortParserProvider.provide(
				getEntityModel(Collections.emptyMap()));

			if (sortParser == null) {
				return null;
			}

			com.liferay.portal.odata.sort.Sort oDataSort =
				new com.liferay.portal.odata.sort.Sort(
					sortParser.parse(sortString));

			List<SortField> sortFields = oDataSort.getSortFields();
			com.liferay.portal.kernel.search.Sort[] sorts =
				new com.liferay.portal.kernel.search.Sort[sortFields.size()];

			for (int i = 0; i < sortFields.size(); i++) {
				SortField sortField = sortFields.get(i);

				sorts[i] = new com.liferay.portal.kernel.search.Sort(
					sortField.getSortableFieldName(
						contextAcceptLanguage.getPreferredLocale()),
					!sortField.isAscending());
			}

			return sorts;
		}
		catch (Exception exception) {
			_log.error("Invalid sort " + sortString, exception);

			return new com.liferay.portal.kernel.search.Sort[0];
		}
	}

	protected Map<String, String> addAction(
		String actionName,
		com.liferay.portal.kernel.model.GroupedModel groupedModel,
		String methodName) {

		return ActionUtil.addAction(
			actionName, getClass(), groupedModel, methodName,
			contextScopeChecker, contextUriInfo);
	}

	protected Map<String, String> addAction(
		String actionName, Long id, String methodName, Long ownerId,
		String permissionName, Long siteId) {

		return ActionUtil.addAction(
			actionName, getClass(), id, methodName, contextScopeChecker,
			ownerId, permissionName, siteId, contextUriInfo);
	}

	protected Map<String, String> addAction(
		String actionName, Long id, String methodName,
		ModelResourcePermission modelResourcePermission) {

		return ActionUtil.addAction(
			actionName, getClass(), id, methodName, contextScopeChecker,
			modelResourcePermission, contextUriInfo);
	}

	protected Map<String, String> addAction(
		String actionName, String methodName, String permissionName,
		Long siteId) {

		return addAction(
			actionName, siteId, methodName, null, permissionName, siteId);
	}

	protected void preparePatch(
		UserAccount userAccount, UserAccount existingUserAccount) {
	}

	protected <T, R, E extends Throwable> List<R> transform(
		Collection<T> collection, UnsafeFunction<T, R, E> unsafeFunction) {

		return TransformUtil.transform(collection, unsafeFunction);
	}

	public static <R, E extends Throwable> R[] transform(
		int[] array, UnsafeFunction<Integer, R, E> unsafeFunction,
		Class<? extends R> clazz) {

		return TransformUtil.transform(array, unsafeFunction, clazz);
	}

	public static <R, E extends Throwable> R[] transform(
		long[] array, UnsafeFunction<Long, R, E> unsafeFunction,
		Class<? extends R> clazz) {

		return TransformUtil.transform(array, unsafeFunction, clazz);
	}

	protected <T, R, E extends Throwable> R[] transform(
		T[] array, UnsafeFunction<T, R, E> unsafeFunction,
		Class<? extends R> clazz) {

		return TransformUtil.transform(array, unsafeFunction, clazz);
	}

	protected <T, R, E extends Throwable> R[] transformToArray(
		Collection<T> collection, UnsafeFunction<T, R, E> unsafeFunction,
		Class<? extends R> clazz) {

		return TransformUtil.transformToArray(
			collection, unsafeFunction, clazz);
	}

	public static <T, E extends Throwable> boolean[] transformToBooleanArray(
		Collection<T> collection,
		UnsafeFunction<T, Boolean, E> unsafeFunction) {

		return TransformUtil.transformToBooleanArray(
			collection, unsafeFunction);
	}

	public static <T, E extends Throwable> boolean[] transformToBooleanArray(
		T[] array, UnsafeFunction<T, Boolean, E> unsafeFunction) {

		return TransformUtil.transformToBooleanArray(array, unsafeFunction);
	}

	public static <T, E extends Throwable> byte[] transformToByteArray(
		Collection<T> collection, UnsafeFunction<T, Byte, E> unsafeFunction) {

		return TransformUtil.transformToByteArray(collection, unsafeFunction);
	}

	public static <T, E extends Throwable> byte[] transformToByteArray(
		T[] array, UnsafeFunction<T, Byte, E> unsafeFunction) {

		return TransformUtil.transformToByteArray(array, unsafeFunction);
	}

	public static <T, E extends Throwable> double[] transformToDoubleArray(
		Collection<T> collection, UnsafeFunction<T, Double, E> unsafeFunction) {

		return TransformUtil.transformToDoubleArray(collection, unsafeFunction);
	}

	public static <T, E extends Throwable> double[] transformToDoubleArray(
		T[] array, UnsafeFunction<T, Double, E> unsafeFunction) {

		return TransformUtil.transformToDoubleArray(array, unsafeFunction);
	}

	public static <T, E extends Throwable> float[] transformToFloatArray(
		Collection<T> collection, UnsafeFunction<T, Float, E> unsafeFunction) {

		return TransformUtil.transformToFloatArray(collection, unsafeFunction);
	}

	public static <T, E extends Throwable> float[] transformToFloatArray(
		T[] array, UnsafeFunction<T, Float, E> unsafeFunction) {

		return TransformUtil.transformToFloatArray(array, unsafeFunction);
	}

	public static <T, R, E extends Throwable> int[] transformToIntArray(
		Collection<T> collection, UnsafeFunction<T, R, E> unsafeFunction) {

		return TransformUtil.transformToIntArray(collection, unsafeFunction);
	}

	public static <T, E extends Throwable> int[] transformToIntArray(
		T[] array, UnsafeFunction<T, Integer, E> unsafeFunction) {

		return TransformUtil.transformToIntArray(array, unsafeFunction);
	}

	public static <R, E extends Throwable> List<R> transformToList(
		int[] array, UnsafeFunction<Integer, R, E> unsafeFunction) {

		return TransformUtil.transformToList(array, unsafeFunction);
	}

	public static <R, E extends Throwable> List<R> transformToList(
		long[] array, UnsafeFunction<Long, R, E> unsafeFunction) {

		return TransformUtil.transformToList(array, unsafeFunction);
	}

	protected <T, R, E extends Throwable> List<R> transformToList(
		T[] array, UnsafeFunction<T, R, E> unsafeFunction) {

		return TransformUtil.transformToList(array, unsafeFunction);
	}

	protected <T, R, E extends Throwable> long[] transformToLongArray(
		Collection<T> collection, UnsafeFunction<T, R, E> unsafeFunction) {

		return TransformUtil.transformToLongArray(collection, unsafeFunction);
	}

	public static <T, E extends Throwable> long[] transformToLongArray(
		T[] array, UnsafeFunction<T, Long, E> unsafeFunction) {

		return TransformUtil.transformToLongArray(array, unsafeFunction);
	}

	public static <T, E extends Throwable> short[] transformToShortArray(
		Collection<T> collection, UnsafeFunction<T, Short, E> unsafeFunction) {

		return TransformUtil.transformToShortArray(collection, unsafeFunction);
	}

	public static <T, E extends Throwable> short[] transformToShortArray(
		T[] array, UnsafeFunction<T, Short, E> unsafeFunction) {

		return TransformUtil.transformToShortArray(array, unsafeFunction);
	}

	protected <T, R, E extends Throwable> List<R> unsafeTransform(
			Collection<T> collection, UnsafeFunction<T, R, E> unsafeFunction)
		throws E {

		return TransformUtil.unsafeTransform(collection, unsafeFunction);
	}

	public static <R, E extends Throwable> R[] unsafeTransform(
			int[] array, UnsafeFunction<Integer, R, E> unsafeFunction,
			Class<? extends R> clazz)
		throws E {

		return TransformUtil.unsafeTransform(array, unsafeFunction, clazz);
	}

	public static <R, E extends Throwable> R[] unsafeTransform(
			long[] array, UnsafeFunction<Long, R, E> unsafeFunction,
			Class<? extends R> clazz)
		throws E {

		return TransformUtil.unsafeTransform(array, unsafeFunction, clazz);
	}

	protected <T, R, E extends Throwable> R[] unsafeTransform(
			T[] array, UnsafeFunction<T, R, E> unsafeFunction,
			Class<? extends R> clazz)
		throws E {

		return TransformUtil.unsafeTransform(array, unsafeFunction, clazz);
	}

	protected <T, R, E extends Throwable> R[] unsafeTransformToArray(
			Collection<T> collection, UnsafeFunction<T, R, E> unsafeFunction,
			Class<? extends R> clazz)
		throws E {

		return TransformUtil.unsafeTransformToArray(
			collection, unsafeFunction, clazz);
	}

	public static <T, E extends Throwable> boolean[]
			unsafeTransformToBooleanArray(
				Collection<T> collection,
				UnsafeFunction<T, Boolean, E> unsafeFunction)
		throws E {

		return TransformUtil.unsafeTransformToBooleanArray(
			collection, unsafeFunction);
	}

	public static <T, E extends Throwable> boolean[]
			unsafeTransformToBooleanArray(
				T[] array, UnsafeFunction<T, Boolean, E> unsafeFunction)
		throws E {

		return TransformUtil.unsafeTransformToBooleanArray(
			array, unsafeFunction);
	}

	public static <T, E extends Throwable> byte[] unsafeTransformToByteArray(
			Collection<T> collection, UnsafeFunction<T, Byte, E> unsafeFunction)
		throws E {

		return TransformUtil.unsafeTransformToByteArray(
			collection, unsafeFunction);
	}

	public static <T, E extends Throwable> byte[] unsafeTransformToByteArray(
			T[] array, UnsafeFunction<T, Byte, E> unsafeFunction)
		throws E {

		return TransformUtil.unsafeTransformToByteArray(array, unsafeFunction);
	}

	public static <T, E extends Throwable> double[]
			unsafeTransformToDoubleArray(
				Collection<T> collection,
				UnsafeFunction<T, Double, E> unsafeFunction)
		throws E {

		return TransformUtil.unsafeTransformToDoubleArray(
			collection, unsafeFunction);
	}

	public static <T, E extends Throwable> double[]
			unsafeTransformToDoubleArray(
				T[] array, UnsafeFunction<T, Double, E> unsafeFunction)
		throws E {

		return TransformUtil.unsafeTransformToDoubleArray(
			array, unsafeFunction);
	}

	public static <T, E extends Throwable> float[] unsafeTransformToFloatArray(
			Collection<T> collection,
			UnsafeFunction<T, Float, E> unsafeFunction)
		throws E {

		return TransformUtil.unsafeTransformToFloatArray(
			collection, unsafeFunction);
	}

	public static <T, E extends Throwable> float[] unsafeTransformToFloatArray(
			T[] array, UnsafeFunction<T, Float, E> unsafeFunction)
		throws E {

		return TransformUtil.unsafeTransformToFloatArray(array, unsafeFunction);
	}

	public static <T, R, E extends Throwable> int[] unsafeTransformToIntArray(
			Collection<T> collection, UnsafeFunction<T, R, E> unsafeFunction)
		throws E {

		return TransformUtil.unsafeTransformToIntArray(
			collection, unsafeFunction);
	}

	public static <T, E extends Throwable> int[] unsafeTransformToIntArray(
			T[] array, UnsafeFunction<T, Integer, E> unsafeFunction)
		throws E {

		return TransformUtil.unsafeTransformToIntArray(array, unsafeFunction);
	}

	public static <R, E extends Throwable> List<R> unsafeTransformToList(
			int[] array, UnsafeFunction<Integer, R, E> unsafeFunction)
		throws E {

		return TransformUtil.unsafeTransformToList(array, unsafeFunction);
	}

	public static <R, E extends Throwable> List<R> unsafeTransformToList(
			long[] array, UnsafeFunction<Long, R, E> unsafeFunction)
		throws E {

		return TransformUtil.unsafeTransformToList(array, unsafeFunction);
	}

	protected <T, R, E extends Throwable> List<R> unsafeTransformToList(
			T[] array, UnsafeFunction<T, R, E> unsafeFunction)
		throws E {

		return TransformUtil.unsafeTransformToList(array, unsafeFunction);
	}

	protected <T, R, E extends Throwable> long[] unsafeTransformToLongArray(
			Collection<T> collection, UnsafeFunction<T, R, E> unsafeFunction)
		throws E {

		return TransformUtil.unsafeTransformToLongArray(
			collection, unsafeFunction);
	}

	public static <T, E extends Throwable> long[] unsafeTransformToLongArray(
			T[] array, UnsafeFunction<T, Long, E> unsafeFunction)
		throws E {

		return TransformUtil.unsafeTransformToLongArray(array, unsafeFunction);
	}

	public static <T, E extends Throwable> short[] unsafeTransformToShortArray(
			Collection<T> collection,
			UnsafeFunction<T, Short, E> unsafeFunction)
		throws E {

		return TransformUtil.unsafeTransformToShortArray(
			collection, unsafeFunction);
	}

	public static <T, E extends Throwable> short[] unsafeTransformToShortArray(
			T[] array, UnsafeFunction<T, Short, E> unsafeFunction)
		throws E {

		return TransformUtil.unsafeTransformToShortArray(array, unsafeFunction);
	}

	protected AcceptLanguage contextAcceptLanguage;
	protected UnsafeBiConsumer
		<Collection<UserAccount>,
		 UnsafeFunction<UserAccount, UserAccount, Exception>, Exception>
			contextBatchUnsafeBiConsumer;
	protected UnsafeBiConsumer
		<Collection<UserAccount>, UnsafeConsumer<UserAccount, Exception>,
		 Exception> contextBatchUnsafeConsumer;
	protected com.liferay.portal.kernel.model.Company contextCompany;
	protected HttpServletRequest contextHttpServletRequest;
	protected HttpServletResponse contextHttpServletResponse;
	protected Object contextScopeChecker;
	protected UriInfo contextUriInfo;
	protected com.liferay.portal.kernel.model.User contextUser;
	protected ExpressionConvert<com.liferay.portal.kernel.search.filter.Filter>
		expressionConvert;
	protected FilterParserProvider filterParserProvider;
	protected GroupLocalService groupLocalService;
	protected ResourceActionLocalService resourceActionLocalService;
	protected ResourcePermissionLocalService resourcePermissionLocalService;
	protected RoleLocalService roleLocalService;
	protected SortParserProvider sortParserProvider;
	protected VulcanBatchEngineExportTaskResource
		vulcanBatchEngineExportTaskResource;
	protected VulcanBatchEngineImportTaskResource
		vulcanBatchEngineImportTaskResource;

	private static final com.liferay.portal.kernel.log.Log _log =
		LogFactoryUtil.getLog(BaseUserAccountResourceImpl.class);

}
// LIFERAY-REST-BUILDER-HASH:-440087298