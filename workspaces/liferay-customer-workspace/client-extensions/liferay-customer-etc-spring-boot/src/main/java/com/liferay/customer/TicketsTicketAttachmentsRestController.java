/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.customer;

import com.liferay.customer.constants.RoleConstants;
import com.liferay.customer.exception.JiraOrganizationNotFoundException;
import com.liferay.customer.model.JiraSupportIssue;
import com.liferay.customer.service.JiraService;
import com.liferay.headless.admin.user.client.dto.v1_0.Account;
import com.liferay.headless.admin.user.client.dto.v1_0.AccountBrief;
import com.liferay.headless.admin.user.client.dto.v1_0.OrganizationBrief;
import com.liferay.headless.admin.user.client.dto.v1_0.RoleBrief;
import com.liferay.headless.admin.user.client.dto.v1_0.UserAccount;
import com.liferay.headless.admin.user.client.resource.v1_0.AccountResource;
import com.liferay.headless.admin.user.client.resource.v1_0.UserAccountResource;
import com.liferay.portal.kernel.util.ArrayUtil;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Karoline Silva
 */
@RequestMapping("/tickets/{ticketId}/ticket-attachments")
@RestController
public class TicketsTicketAttachmentsRestController extends BaseRestController {

	@GetMapping("/download-access-check")
	public ResponseEntity<String> getDownloadAccessCheck(
		@AuthenticationPrincipal Jwt jwt,
		@PathVariable("ticketId") String ticketId) {

		return _getResponseEntity(jwt, ticketId, true);
	}

	@GetMapping("/upload-access-check")
	public ResponseEntity<String> getUploadAccessCheck(
		@AuthenticationPrincipal Jwt jwt,
		@PathVariable("ticketId") String ticketId) {

		return _getResponseEntity(jwt, ticketId, false);
	}

	private ResponseEntity<String> _getResponseEntity(
		Jwt jwt, String ticketId, boolean allowClosedTicket) {

		try {
			JiraSupportIssue jiraSupportIssue =
				_jiraService.getJiraSupportIssue(ticketId);

			if (jiraSupportIssue == null) {
				return new ResponseEntity<>(
					"INVALID_TICKET_NUMBER", HttpStatus.NOT_FOUND);
			}

			if (jiraSupportIssue.isClosed() && !allowClosedTicket) {
				return new ResponseEntity<>(
					"TICKET_IS_CLOSED", HttpStatus.BAD_REQUEST);
			}

			if (!_hasViewPermission(
					jwt,
					getAccountKey(
						jiraSupportIssue.getOrganizationId(),
						jiraSupportIssue.getWorkspaceId()))) {

				return new ResponseEntity<>(
					"FORBIDDEN_ACCESS", HttpStatus.FORBIDDEN);
			}

			return new ResponseEntity<>("", HttpStatus.OK);
		}
		catch (JiraOrganizationNotFoundException
					jiraOrganizationNotFoundException) {

			_log.error(
				jiraOrganizationNotFoundException,
				jiraOrganizationNotFoundException);

			return new ResponseEntity<>(
				"JIRA_ORGANIZATION_ERROR", HttpStatus.INTERNAL_SERVER_ERROR);
		}
		catch (Exception exception) {
			_log.error(exception, exception);

			return new ResponseEntity<>(
				"UNEXPECTED_ERROR", HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	private boolean _hasViewPermission(
			Jwt jwt, String accountExternalReferenceCode)
		throws Exception {

		UserAccountResource userAccountResource = UserAccountResource.builder(
		).header(
			HttpHeaders.AUTHORIZATION, "Bearer " + jwt.getTokenValue()
		).endpoint(
			lxcDXPMainDomain, lxcDXPServerProtocol
		).build();

		UserAccount userAccount = userAccountResource.getMyUserAccount();

		if (userAccount == null) {
			return false;
		}

		for (RoleBrief roleBrief : userAccount.getRoleBriefs()) {
			String name = roleBrief.getName();

			if (name.equals(RoleConstants.NAME_PROVISIONING_MEMBER)) {
				return true;
			}
		}

		for (AccountBrief accountBrief : userAccount.getAccountBriefs()) {
			if (!accountExternalReferenceCode.equals(
					accountBrief.getExternalReferenceCode())) {

				continue;
			}

			for (RoleBrief roleBrief : accountBrief.getRoleBriefs()) {
				if (ArrayUtil.contains(
						RoleConstants.SUPPORT_ACCOUNT_TICKET_ROLES,
						roleBrief.getName())) {

					return true;
				}
			}
		}

		AccountResource accountResource = AccountResource.builder(
		).header(
			HttpHeaders.AUTHORIZATION, "Bearer " + jwt.getTokenValue()
		).endpoint(
			lxcDXPMainDomain, lxcDXPServerProtocol
		).build();

		Account account = accountResource.getAccountByExternalReferenceCode(
			accountExternalReferenceCode);

		if ((account == null) || (account.getOrganizationIds() == null)) {
			return false;
		}

		Long[] organizationIds = account.getOrganizationIds();

		for (OrganizationBrief organizationBrief :
				userAccount.getOrganizationBriefs()) {

			for (long organizationId : organizationIds) {
				if (organizationBrief.getId() == organizationId) {
					return true;
				}
			}
		}

		return false;
	}

	private static final Log _log = LogFactory.getLog(
		TicketsTicketAttachmentsRestController.class);

	@Autowired
	private JiraService _jiraService;

}