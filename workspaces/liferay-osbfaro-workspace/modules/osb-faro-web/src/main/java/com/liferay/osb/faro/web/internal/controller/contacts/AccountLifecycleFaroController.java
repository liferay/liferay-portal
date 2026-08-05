/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.web.internal.controller.contacts;

import com.liferay.osb.faro.engine.client.model.Account;
import com.liferay.osb.faro.engine.client.model.AccountLifecycle;
import com.liferay.osb.faro.engine.client.model.AccountLifecycleMetric;
import com.liferay.osb.faro.engine.client.model.AccountLifecycleStageMetric;
import com.liferay.osb.faro.engine.client.model.Results;
import com.liferay.osb.faro.web.internal.controller.BaseFaroController;
import com.liferay.osb.faro.web.internal.exception.FaroException;
import com.liferay.osb.faro.web.internal.model.display.FaroFDSResultsDisplay;
import com.liferay.osb.faro.web.internal.model.display.contacts.AccountDisplay;
import com.liferay.osb.faro.web.internal.param.FaroParam;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.model.RoleConstants;
import com.liferay.portal.kernel.util.Validator;

import jakarta.annotation.security.RolesAllowed;

import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.FormParam;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;

import java.util.List;
import java.util.function.Function;

import org.osgi.service.component.annotations.Component;

/**
 * @author Riccardo Ferrari
 */
@Component(service = AccountLifecycleFaroController.class)
@Path("/{groupId}/account-lifecycle")
@Produces(MediaType.APPLICATION_JSON)
public class AccountLifecycleFaroController extends BaseFaroController {

	@POST
	@RolesAllowed(RoleConstants.SITE_MEMBER)
	public AccountLifecycle createAccountLifecycle(
			@PathParam("groupId") long groupId,
			@FormParam("accountLifecycle") FaroParam<AccountLifecycle>
				accountLifecycleFaroParam,
			@QueryParam("channelId") String channelId)
		throws Exception {

		if (Validator.isNull(channelId)) {
			throw new FaroException("Invalid channel ID: " + channelId);
		}

		return contactsEngineClient.addAccountLifecycle(
			faroProjectLocalService.getFaroProjectByGroupId(groupId),
			accountLifecycleFaroParam.getValue(), channelId);
	}

	@GET
	@Path("/{id}")
	@RolesAllowed(RoleConstants.SITE_MEMBER)
	public AccountLifecycle getAccountLifecycle(
			@PathParam("groupId") long groupId, @PathParam("id") String id)
		throws Exception {

		return contactsEngineClient.getAccountLifecycle(
			faroProjectLocalService.getFaroProjectByGroupId(groupId), id);
	}

	@GET
	@Path("/{id}/overview")
	@RolesAllowed(RoleConstants.SITE_MEMBER)
	public List<AccountLifecycleMetric> getAccountLifecycleMetrics(
			@PathParam("groupId") long groupId, @PathParam("id") String id,
			@QueryParam("country") String country,
			@QueryParam("industry") String industry)
		throws Exception {

		return contactsEngineClient.getAccountLifecycleMetrics(
			faroProjectLocalService.getFaroProjectByGroupId(groupId), country,
			id, industry);
	}

	@GET
	@RolesAllowed(RoleConstants.SITE_MEMBER)
	public List<AccountLifecycle> getAccountLifecycles(
			@PathParam("groupId") long groupId)
		throws Exception {

		return contactsEngineClient.getAccountLifecycles(
			faroProjectLocalService.getFaroProjectByGroupId(groupId));
	}

	@GET
	@Path("/{id}/stages")
	@RolesAllowed(RoleConstants.SITE_MEMBER)
	public List<AccountLifecycleStageMetric> getAccountLifecycleStageMetrics(
			@PathParam("groupId") long groupId, @PathParam("id") String id,
			@QueryParam("country") String country,
			@QueryParam("industry") String industry,
			@QueryParam("segmentId") Long segmentId)
		throws Exception {

		return contactsEngineClient.getAccountLifecycleStageMetrics(
			faroProjectLocalService.getFaroProjectByGroupId(groupId), country,
			id, industry, segmentId);
	}

	@GET
	@Path("/{id}/accounts")
	@RolesAllowed(RoleConstants.SITE_MEMBER)
	public FaroFDSResultsDisplay getAccountsFaroFDSResultsDisplay(
			@PathParam("groupId") long groupId, @PathParam("id") String id,
			@QueryParam("filter") String filterString,
			@QueryParam("page") int page, @QueryParam("pageSize") int pageSize,
			@QueryParam("search") String search,
			@DefaultValue(StringPool.BLANK) @QueryParam("sort") String
				sortString)
		throws Exception {

		Results<Account> results =
			contactsEngineClient.getAccountLifecycleAccounts(
				faroProjectLocalService.getFaroProjectByGroupId(groupId),
				filterString, id, search, page, pageSize, sortString);

		Function<Account, AccountDisplay> function = AccountDisplay::new;

		return new FaroFDSResultsDisplay(results, function, page, pageSize);
	}

	@Path("/{id}")
	@PUT
	@RolesAllowed(RoleConstants.SITE_MEMBER)
	public AccountLifecycle updateAccountLifecycle(
			@PathParam("groupId") long groupId, @PathParam("id") String id,
			@FormParam("accountLifecycle") FaroParam<AccountLifecycle>
				accountLifecycleFaroParam)
		throws Exception {

		AccountLifecycle accountLifecycle =
			accountLifecycleFaroParam.getValue();

		accountLifecycle.setId(id);

		return contactsEngineClient.updateAccountLifecycle(
			faroProjectLocalService.getFaroProjectByGroupId(groupId),
			accountLifecycle);
	}

	@Path("/{id}/stages/{stageId}/rules")
	@PUT
	@RolesAllowed(RoleConstants.SITE_MEMBER)
	public void updateAccountLifecycleStageRule(
			@PathParam("groupId") long groupId, @PathParam("id") String id,
			@PathParam("stageId") String stageId,
			@FormParam("filter") String filterString,
			@FormParam("filterMetadata") String filterMetadata,
			@FormParam("name") String name)
		throws Exception {

		contactsEngineClient.updateAccountLifecycleStageRule(
			faroProjectLocalService.getFaroProjectByGroupId(groupId),
			filterMetadata, filterString, id, name, stageId);
	}

}