/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.frontend.internal.account;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import com.liferay.account.constants.AccountConstants;
import com.liferay.account.service.AccountEntryLocalService;
import com.liferay.commerce.constants.CommerceCheckoutWebKeys;
import com.liferay.commerce.constants.CommercePortletKeys;
import com.liferay.commerce.context.CommerceContext;
import com.liferay.commerce.context.CommerceContextFactory;
import com.liferay.commerce.frontend.internal.account.model.Account;
import com.liferay.commerce.frontend.internal.account.model.AccountList;
import com.liferay.commerce.frontend.internal.account.model.AccountOrganization;
import com.liferay.commerce.frontend.internal.account.model.AccountOrganizationList;
import com.liferay.commerce.frontend.internal.account.model.AccountUser;
import com.liferay.commerce.frontend.internal.account.model.AccountUserList;
import com.liferay.commerce.frontend.internal.account.model.Order;
import com.liferay.commerce.frontend.internal.account.model.OrderList;
import com.liferay.commerce.model.CommerceOrder;
import com.liferay.commerce.product.service.CommerceChannelLocalService;
import com.liferay.commerce.service.CommerceOrderService;
import com.liferay.commerce.util.CommerceAccountHelper;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Organization;
import com.liferay.portal.kernel.model.OrganizationConstants;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.portlet.LiferayPortletURL;
import com.liferay.portal.kernel.portlet.PortletURLFactoryUtil;
import com.liferay.portal.kernel.search.BaseModelSearchResult;
import com.liferay.portal.kernel.search.SortFactoryUtil;
import com.liferay.portal.kernel.service.OrganizationLocalService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.webserver.WebServerServletToken;
import com.liferay.portal.kernel.workflow.WorkflowConstants;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.PortletRequest;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import jakarta.ws.rs.FormParam;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Alessio Antonio Rendina
 * @author Gianmarco Brunialti Masera
 */
@Component(service = CommerceAccountResource.class)
public class CommerceAccountResource {

	public AccountList getAccountList(
			long userId, long parentAccountId, int commerceSiteType,
			String keywords, int page, int pageSize, String imagePath)
		throws PortalException {

		List<Account> accounts = _getAccounts(
			userId, parentAccountId, commerceSiteType, keywords, page, pageSize,
			imagePath);

		return new AccountList(
			accounts,
			_getAccountsCount(
				userId, parentAccountId, commerceSiteType, keywords));
	}

	public AccountOrganizationList getAccountOrganizationList(
			long companyId, String keywords, String imagePath)
		throws PortalException {

		List<AccountOrganization> accountOrganizations = _searchOrganizations(
			companyId, keywords, imagePath);

		return new AccountOrganizationList(
			accountOrganizations, accountOrganizations.size());
	}

	public AccountUserList getAccountUserList(
			long companyId, String keywords, String imagePath)
		throws PortalException {

		List<AccountUser> accountUsers = _searchUsers(
			companyId, keywords, imagePath);

		return new AccountUserList(accountUsers, accountUsers.size());
	}

	@GET
	@Path("/search-accounts")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getCommerceAccounts(
		@QueryParam("groupId") long groupId,
		@QueryParam("q") String queryString, @QueryParam("page") int page,
		@QueryParam("pageSize") int pageSize, @Context UriInfo uriInfo,
		@Context ThemeDisplay themeDisplay) {

		AccountList accountList = null;

		themeDisplay.setScopeGroupId(groupId);

		HttpServletRequest httpServletRequest = themeDisplay.getRequest();

		try {
			CommerceContext commerceContext = _commerceContextFactory.create(
				0,
				_commerceChannelLocalService.
					getCommerceChannelGroupIdBySiteGroupId(groupId),
				null, 0, _portal.getCompanyId(httpServletRequest));

			accountList = getAccountList(
				themeDisplay.getUserId(),
				AccountConstants.PARENT_ACCOUNT_ENTRY_ID_DEFAULT,
				commerceContext.getCommerceSiteType(), queryString, page,
				pageSize, themeDisplay.getPathImage());
		}
		catch (Exception exception) {
			_log.error(exception);
			accountList = new AccountList(
				StringUtil.split(exception.getLocalizedMessage()));
		}

		return _getResponse(accountList);
	}

	@GET
	@Path("/search-accounts/{accountId}/orders/")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getCommerceOrders(
		@QueryParam("groupId") long groupId,
		@PathParam("accountId") long accountId,
		@QueryParam("q") String queryString, @QueryParam("page") int page,
		@QueryParam("pageSize") int pageSize,
		@Context HttpServletRequest httpServletRequest,
		@Context ThemeDisplay themeDisplay) {

		themeDisplay.setScopeGroupId(groupId);

		OrderList orderList = null;

		try {
			orderList = getOrderList(
				groupId, accountId, page, pageSize, httpServletRequest);
		}
		catch (Exception exception) {
			orderList = new OrderList(
				StringUtil.split(exception.getLocalizedMessage()));
		}

		return _getResponse(orderList);
	}

	public OrderList getOrderList(
			long groupId, long accountId, int page, int pageSize,
			HttpServletRequest httpServletRequest)
		throws PortalException {

		List<Order> orders = _getOrders(
			groupId, accountId, page, pageSize, httpServletRequest);

		return new OrderList(orders, orders.size());
	}

	@GET
	@Path("/search-organizations")
	@Produces(MediaType.APPLICATION_JSON)
	public Response searchOrganizations(
		@QueryParam("q") String queryString,
		@Context ThemeDisplay themeDisplay) {

		AccountOrganizationList accountOrganizationList = null;

		try {
			accountOrganizationList = getAccountOrganizationList(
				themeDisplay.getCompanyId(), queryString,
				themeDisplay.getPathImage());
		}
		catch (Exception exception) {
			accountOrganizationList = new AccountOrganizationList(
				StringUtil.split(exception.getLocalizedMessage()));
		}

		return _getResponse(accountOrganizationList);
	}

	@GET
	@Path("/search-users")
	@Produces(MediaType.APPLICATION_JSON)
	public Response searchUsers(
		@QueryParam("q") String queryString,
		@Context ThemeDisplay themeDisplay) {

		AccountUserList accountUserList = null;

		try {
			accountUserList = getAccountUserList(
				themeDisplay.getCompanyId(), queryString,
				themeDisplay.getPathImage());
		}
		catch (Exception exception) {
			accountUserList = new AccountUserList(
				StringUtil.split(exception.getLocalizedMessage()));
		}

		return _getResponse(accountUserList);
	}

	@Path("/set-current-account")
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	public Response setCurrentAccount(
		@QueryParam("groupId") long groupId,
		@FormParam("accountId") long accountId,
		@Context HttpServletRequest httpServletRequest) {

		try {
			long channelGroupId =
				_commerceChannelLocalService.
					getCommerceChannelGroupIdBySiteGroupId(groupId);

			_commerceAccountHelper.setCurrentCommerceAccount(
				httpServletRequest, channelGroupId, accountId);

			HttpServletRequest originalHttpServletRequest =
				_portal.getOriginalServletRequest(httpServletRequest);

			HttpSession httpSession = originalHttpServletRequest.getSession();

			CommerceOrder commerceOrder =
				(CommerceOrder)httpSession.getAttribute(
					CommerceCheckoutWebKeys.
						COMMERCE_ORDER_ON_ACCOUNT_SELECTION);

			if (commerceOrder != null) {
				httpSession.setAttribute(
					CommerceOrder.class.getName() + StringPool.POUND +
						channelGroupId,
					commerceOrder.getUuid());

				httpSession.removeAttribute(
					CommerceCheckoutWebKeys.
						COMMERCE_ORDER_ON_ACCOUNT_SELECTION);
			}
			else {
				httpSession.removeAttribute(
					CommerceOrder.class.getName() + StringPool.POUND +
						channelGroupId);
			}
		}
		catch (Exception exception) {
			_log.error(exception);

			Response.ResponseBuilder responseBuilder = Response.serverError();

			return responseBuilder.build();
		}

		return Response.ok(
		).build();
	}

	private List<Account> _getAccounts(
			long userId, long parentAccountId, int commerceSiteType,
			String keywords, int page, int pageSize, String imagePath)
		throws PortalException {

		int start = (page - 1) * pageSize;
		int end = page * pageSize;

		return TransformUtil.transform(
			_accountEntryLocalService.getUserAccountEntries(
				userId, parentAccountId, keywords,
				_commerceAccountHelper.toAccountEntryTypes(commerceSiteType),
				_commerceAccountHelper.toAccountEntryStatus(true), start, end),
			accountEntry -> new Account(
				String.valueOf(accountEntry.getAccountEntryId()),
				accountEntry.getName(),
				_getLogoThumbnailSrc(accountEntry.getLogoId(), imagePath)));
	}

	private int _getAccountsCount(
			long userId, Long parentAccountId, int commerceSiteType,
			String keywords)
		throws PortalException {

		return _accountEntryLocalService.getUserAccountEntriesCount(
			userId, parentAccountId, keywords,
			_commerceAccountHelper.toAccountEntryTypes(commerceSiteType),
			_commerceAccountHelper.toAccountEntryStatus(true));
	}

	private String _getLogoThumbnailSrc(long logoId, String imagePath) {
		return StringBundler.concat(
			imagePath, "/organization_logo?img_id=", logoId, "&t=",
			_webServerServletToken.getToken(logoId));
	}

	private String _getOrderLinkURL(
			long groupId, long commerceOrderId,
			HttpServletRequest httpServletRequest)
		throws PortalException {

		long plid = _portal.getPlidFromPortletId(
			groupId, CommercePortletKeys.COMMERCE_OPEN_ORDER_CONTENT);

		LiferayPortletURL editURL = PortletURLFactoryUtil.create(
			_portal.getOriginalServletRequest(httpServletRequest),
			CommercePortletKeys.COMMERCE_OPEN_ORDER_CONTENT, plid,
			PortletRequest.ACTION_PHASE);

		editURL.setParameter(
			ActionRequest.ACTION_NAME,
			"/commerce_open_order_content/edit_commerce_order");
		editURL.setParameter(Constants.CMD, "setCurrent");
		editURL.setParameter(
			"commerceOrderId", String.valueOf(commerceOrderId));

		return editURL.toString();
	}

	private List<Order> _getOrders(
			long groupId, long commerceAccountId, int page, int pageSize,
			HttpServletRequest httpServletRequest)
		throws PortalException {

		int start = (page - 1) * pageSize;
		int end = page * pageSize;

		long commerceChannelGroupId =
			_commerceChannelLocalService.getCommerceChannelGroupIdBySiteGroupId(
				groupId);

		List<CommerceOrder> userCommerceOrders =
			_commerceOrderService.getPendingCommerceOrders(
				commerceChannelGroupId, commerceAccountId, StringPool.BLANK,
				start, end);

		return TransformUtil.transform(
			userCommerceOrders,
			commerceOrder -> {
				Date modifiedDate = commerceOrder.getModifiedDate();

				String modifiedDateTimeDescription =
					_language.getTimeDescription(
						httpServletRequest,
						System.currentTimeMillis() - modifiedDate.getTime(),
						true);

				return new Order(
					commerceOrder.getCommerceOrderId(),
					commerceOrder.getCommerceAccountId(),
					commerceOrder.getCommerceAccountName(),
					commerceOrder.getPurchaseOrderNumber(),
					_language.format(
						httpServletRequest, "x-ago",
						modifiedDateTimeDescription),
					WorkflowConstants.getStatusLabel(commerceOrder.getStatus()),
					_getOrderLinkURL(
						groupId, commerceOrder.getCommerceOrderId(),
						httpServletRequest));
			});
	}

	private Response _getResponse(Object object) {
		if (object == null) {
			return Response.status(
				Response.Status.NOT_FOUND
			).build();
		}

		try {
			String json = _OBJECT_MAPPER.writeValueAsString(object);

			return Response.ok(
				json, MediaType.APPLICATION_JSON
			).build();
		}
		catch (JsonProcessingException jsonProcessingException) {
			_log.error(jsonProcessingException);
		}

		return Response.status(
			Response.Status.NOT_FOUND
		).build();
	}

	private String _getUserPortraitSrc(User user, String imagePath) {
		return StringBundler.concat(
			imagePath, "/user_portrait?screenName=", user.getScreenName(),
			"&amp;companyId=", user.getCompanyId());
	}

	private List<AccountOrganization> _searchOrganizations(
			long companyId, String keywords, String imagePath)
		throws PortalException {

		List<AccountOrganization> accountOrganizations = new ArrayList<>();

		BaseModelSearchResult<Organization> baseModelSearchResult =
			_organizationLocalService.searchOrganizations(
				companyId, OrganizationConstants.ANY_PARENT_ORGANIZATION_ID,
				keywords, null, 0, 10, SortFactoryUtil.create("name", false));

		for (Organization organization :
				baseModelSearchResult.getBaseModels()) {

			accountOrganizations.add(
				new AccountOrganization(
					organization.getOrganizationId(), organization.getName(),
					StringPool.BLANK,
					_getLogoThumbnailSrc(organization.getLogoId(), imagePath)));
		}

		return accountOrganizations;
	}

	private List<AccountUser> _searchUsers(
		long companyId, String keywords, String imagePath) {

		List<User> users = _userLocalService.search(
			companyId, keywords, WorkflowConstants.STATUS_APPROVED, null, 0, 10,
			(OrderByComparator<User>)null);

		return TransformUtil.transform(
			users,
			user -> new AccountUser(
				user.getUserId(), user.getFullName(), user.getEmailAddress(),
				_getUserPortraitSrc(user, imagePath)));
	}

	private static final ObjectMapper _OBJECT_MAPPER = new ObjectMapper() {
		{
			configure(MapperFeature.SORT_PROPERTIES_ALPHABETICALLY, true);
			disable(SerializationFeature.INDENT_OUTPUT);
		}
	};

	private static final Log _log = LogFactoryUtil.getLog(
		CommerceAccountResource.class);

	@Reference
	private AccountEntryLocalService _accountEntryLocalService;

	@Reference
	private CommerceAccountHelper _commerceAccountHelper;

	@Reference
	private CommerceChannelLocalService _commerceChannelLocalService;

	@Reference
	private CommerceContextFactory _commerceContextFactory;

	@Reference
	private CommerceOrderService _commerceOrderService;

	@Reference
	private Language _language;

	@Reference
	private OrganizationLocalService _organizationLocalService;

	@Reference
	private Portal _portal;

	@Reference
	private UserLocalService _userLocalService;

	@Reference
	private WebServerServletToken _webServerServletToken;

}