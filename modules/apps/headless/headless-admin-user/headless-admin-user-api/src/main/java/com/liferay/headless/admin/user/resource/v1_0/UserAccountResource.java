/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.user.resource.v1_0;

import com.liferay.headless.admin.user.dto.v1_0.UserAccount;
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
import com.liferay.portal.vulcan.multipart.MultipartBody;
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
 *     curl -u your@email.com:yourpassword -D - http://localhost:8080/o/headless-admin-user/v1.0
 *
 * @author Javier Gamarra
 * @generated
 */
@Generated("")
@ProviderType
public interface UserAccountResource {

	public void
			deleteAccountByExternalReferenceCodeUserAccountByExternalReferenceCode(
				String accountExternalReferenceCode,
				String externalReferenceCode)
		throws Exception;

	public void deleteAccountUserAccount(Long accountId, Long userAccountId)
		throws Exception;

	public void deleteAccountUserAccountByEmailAddress(
			Long accountId, String emailAddress)
		throws Exception;

	public void deleteAccountUserAccountByExternalReferenceCodeByEmailAddress(
			String externalReferenceCode, String emailAddress)
		throws Exception;

	public void deleteAccountUserAccountsByEmailAddress(
			Long accountId, String[] strings)
		throws Exception;

	public void deleteAccountUserAccountsByExternalReferenceCodeByEmailAddress(
			String externalReferenceCode, String[] strings)
		throws Exception;

	public void deleteUserAccount(Long userAccountId) throws Exception;

	public Response deleteUserAccountBatch(String callbackURL, Object object)
		throws Exception;

	public void deleteUserAccountByExternalReferenceCode(
			String externalReferenceCode)
		throws Exception;

	public UserAccount
			getAccountByExternalReferenceCodeUserAccountByExternalReferenceCode(
				String accountExternalReferenceCode,
				String externalReferenceCode)
		throws Exception;

	public UserAccount getAccountUserAccount(Long accountId, Long userAccountId)
		throws Exception;

	public Page<UserAccount> getAccountUserAccountsByExternalReferenceCodePage(
			String externalReferenceCode, String search,
			com.liferay.portal.kernel.search.filter.Filter filter,
			Pagination pagination,
			com.liferay.portal.kernel.search.Sort[] sorts)
		throws Exception;

	public Page<UserAccount> getAccountUserAccountsPage(
			Long accountId, String search,
			com.liferay.portal.kernel.search.filter.Filter filter,
			Pagination pagination,
			com.liferay.portal.kernel.search.Sort[] sorts)
		throws Exception;

	public UserAccount getMyUserAccount() throws Exception;

	public Page<UserAccount>
			getOrganizationByExternalReferenceCodeUserAccountsPage(
				String externalReferenceCode, String search,
				com.liferay.portal.kernel.search.filter.Filter filter,
				Pagination pagination,
				com.liferay.portal.kernel.search.Sort[] sorts)
		throws Exception;

	public Page<UserAccount> getOrganizationUserAccountsPage(
			String organizationId, String search,
			com.liferay.portal.kernel.search.filter.Filter filter,
			Pagination pagination,
			com.liferay.portal.kernel.search.Sort[] sorts)
		throws Exception;

	public Boolean getSiteAccountUserAccountSelected(
			Long siteId, Long accountId, Long userAccountId)
		throws Exception;

	public Boolean
			getSiteByFriendlyUrlPathAccountByExternalReferenceCodeAccountExternalReferenceCodeUserAccountByExternalReferenceCodeUserAccountExternalReferenceCodeSelected(
				String friendlyUrlPath, String accountExternalReferenceCode,
				String userAccountExternalReferenceCode)
		throws Exception;

	public Page<UserAccount> getSiteUserAccountsPage(
			Long siteId, String search,
			com.liferay.portal.kernel.search.filter.Filter filter,
			Pagination pagination,
			com.liferay.portal.kernel.search.Sort[] sorts)
		throws Exception;

	public UserAccount getUserAccount(Long userAccountId) throws Exception;

	public UserAccount getUserAccountByEmailAddress(String emailAddress)
		throws Exception;

	public UserAccount getUserAccountByExternalReferenceCode(
			String externalReferenceCode)
		throws Exception;

	public Page<UserAccount> getUserAccountsByStatusPage(
			String status, String search,
			com.liferay.portal.kernel.search.filter.Filter filter,
			Pagination pagination,
			com.liferay.portal.kernel.search.Sort[] sorts)
		throws Exception;

	public Page<UserAccount> getUserAccountsPage(
			String search,
			com.liferay.portal.kernel.search.filter.Filter filter,
			Pagination pagination,
			com.liferay.portal.kernel.search.Sort[] sorts)
		throws Exception;

	public Page<UserAccount> getUserGroupByExternalReferenceCodeUsersPage(
			String externalReferenceCode, String search,
			com.liferay.portal.kernel.search.filter.Filter filter,
			Pagination pagination,
			com.liferay.portal.kernel.search.Sort[] sorts)
		throws Exception;

	public Page<UserAccount> getUserGroupUsersPage(
			Long userGroupId, String search,
			com.liferay.portal.kernel.search.filter.Filter filter,
			Pagination pagination,
			com.liferay.portal.kernel.search.Sort[] sorts)
		throws Exception;

	public void patchSiteAccountUserAccountSelected(
			Long siteId, Long accountId, Long userAccountId)
		throws Exception;

	public void
			patchSiteByFriendlyUrlPathAccountByExternalReferenceCodeAccountExternalReferenceCodeUserAccountByExternalReferenceCodeUserAccountExternalReferenceCodeSelected(
				String friendlyUrlPath, String accountExternalReferenceCode,
				String userAccountExternalReferenceCode)
		throws Exception;

	public UserAccount patchUserAccount(
			Long userAccountId, UserAccount userAccount)
		throws Exception;

	public UserAccount patchUserAccountByExternalReferenceCode(
			String externalReferenceCode, UserAccount userAccount)
		throws Exception;

	public void
			postAccountByExternalReferenceCodeUserAccountByExternalReferenceCode(
				String accountExternalReferenceCode,
				String externalReferenceCode)
		throws Exception;

	public UserAccount postAccountUserAccount(
			Long accountId, UserAccount userAccount)
		throws Exception;

	public Response postAccountUserAccountBatch(
			Long accountId, String callbackURL, Object object)
		throws Exception;

	public UserAccount postAccountUserAccountByEmailAddress(
			Long accountId, String emailAddress)
		throws Exception;

	public UserAccount postAccountUserAccountByExternalReferenceCode(
			String externalReferenceCode, UserAccount userAccount)
		throws Exception;

	public UserAccount
			postAccountUserAccountByExternalReferenceCodeByEmailAddress(
				String externalReferenceCode, String emailAddress)
		throws Exception;

	public Page<UserAccount> postAccountUserAccountsByEmailAddress(
			Long accountId, String accountRoleIds, String[] strings)
		throws Exception;

	public Page<UserAccount>
			postAccountUserAccountsByExternalReferenceCodeByEmailAddress(
				String externalReferenceCode, String accountRoleIds,
				String[] strings)
		throws Exception;

	public Response postAccountUserAccountsPageExportBatch(
			Long accountId, String search,
			com.liferay.portal.kernel.search.filter.Filter filter,
			com.liferay.portal.kernel.search.Sort[] sorts, String callbackURL,
			String contentType, String fieldNames)
		throws Exception;

	public Response postOrganizationUserAccountsPageExportBatch(
			String organizationId, String search,
			com.liferay.portal.kernel.search.filter.Filter filter,
			com.liferay.portal.kernel.search.Sort[] sorts, String callbackURL,
			String contentType, String fieldNames)
		throws Exception;

	public Response postSiteUserAccountsPageExportBatch(
			Long siteId, String search,
			com.liferay.portal.kernel.search.filter.Filter filter,
			com.liferay.portal.kernel.search.Sort[] sorts, String callbackURL,
			String contentType, String fieldNames)
		throws Exception;

	public UserAccount postUserAccount(
			String captchaAnswer, String captchaToken, UserAccount userAccount)
		throws Exception;

	public Response postUserAccountBatch(
			String captchaAnswer, String captchaToken, String callbackURL,
			Object object)
		throws Exception;

	public Response postUserAccountImage(
			Long userAccountId, MultipartBody multipartBody)
		throws Exception;

	public Response postUserAccountsPageExportBatch(
			String search,
			com.liferay.portal.kernel.search.filter.Filter filter,
			com.liferay.portal.kernel.search.Sort[] sorts, String callbackURL,
			String contentType, String fieldNames)
		throws Exception;

	public UserAccount putUserAccount(
			Long userAccountId, UserAccount userAccount)
		throws Exception;

	public Response putUserAccountBatch(String callbackURL, Object object)
		throws Exception;

	public UserAccount putUserAccountByExternalReferenceCode(
			String externalReferenceCode, UserAccount userAccount)
		throws Exception;

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

		public UserAccountResource build();

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