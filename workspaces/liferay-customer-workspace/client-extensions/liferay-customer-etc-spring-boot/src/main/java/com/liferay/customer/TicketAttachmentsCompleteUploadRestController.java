/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.customer;

import com.liferay.client.extension.util.spring.boot3.BaseRestController;
import com.liferay.client.extension.util.spring.boot3.client.LiferayOAuth2AccessTokenManager;
import com.liferay.customer.model.TicketAttachment;
import com.liferay.customer.service.NotificationQueueEntryService;
import com.liferay.customer.service.TicketAttachmentService;
import com.liferay.osb.spring.boot.client.zendesk.model.ZendeskUser;
import com.liferay.osb.spring.boot.client.zendesk.service.ZendeskService;
import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.util.StackTraceUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.json.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * @author Amos Fong
 */
@ComponentScan(basePackages = "com.liferay.osb")
@RequestMapping("/ticket-attachments/{ticketAttachmentId}/complete-upload")
@RestController
public class TicketAttachmentsCompleteUploadRestController
	extends BaseRestController {

	@PostMapping
	public ResponseEntity<String> post(
		@AuthenticationPrincipal Jwt jwt, @RequestBody String json,
		@PathVariable("ticketAttachmentId") long ticketAttachmentId) {

		try {
			TicketAttachment ticketAttachment =
				_ticketAttachmentService.approveTicketAttachment(
					"Bearer " + jwt.getTokenValue(), ticketAttachmentId);
			JSONObject jsonObject = new JSONObject(json);

			String zendeskTicketCommentBody = _buildZendeskTicketCommentBody(
				ticketAttachment,
				jsonObject.optString("zendeskTicketCommentBody"));

			try {
				_postZendeskComment(
					jwt.getClaimAsString("username"),
					ticketAttachment.getZendeskTicketId(),
					zendeskTicketCommentBody);

				return new ResponseEntity<>(HttpStatus.OK);
			}
			catch (Exception exception) {
				_log.error(exception, exception);

				_ticketAttachmentService.updateTicketAttachmentDraftCommentBody(
					"Bearer " + jwt.getTokenValue(), ticketAttachmentId,
					zendeskTicketCommentBody);

				return new ResponseEntity<>(
					"COMMENT_POST_FAILED_RETRYING", HttpStatus.ACCEPTED);
			}
		}
		catch (HttpServerErrorException.ServiceUnavailable
					httpServerErrorException) {

			_log.error(httpServerErrorException, httpServerErrorException);

			return new ResponseEntity<>(
				"FILE_SERVER_UNAVAILABLE", HttpStatus.SERVICE_UNAVAILABLE);
		}
		catch (Exception exception) {
			_log.error(exception, exception);

			return new ResponseEntity<>(
				"UNEXPECTED_ERROR", HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@Scheduled(cron = "0 0 */1 * * ?")
	public void scheduledUpdateTicketAttachmentDraftCommentBody()
		throws Exception {

		List<TicketAttachment> ticketAttachments =
			_ticketAttachmentService.searchTicketAttachments(
				_liferayOAuth2AccessTokenManager.getAuthorization(
					"liferay-customer-etc-spring-boot-oahs"),
				"draftCommentBody ne null and draftCommentBody ne '' and " +
					"(state eq 0 or state eq null) and status/any(s:s eq 0)");

		for (TicketAttachment ticketAttachment : ticketAttachments) {
			try {
				JSONObject jsonObject = new JSONObject(
					get(
						_liferayOAuth2AccessTokenManager.getAuthorization(
							"liferay-customer-etc-spring-boot-oahs"),
						UriComponentsBuilder.fromPath(
							"/o/headless-admin-user/v1.0/user-accounts/" +
								ticketAttachment.getUserId()
						).build(
						).toUri()));

				_postZendeskComment(
					jsonObject.getString("emailAddress"),
					ticketAttachment.getZendeskTicketId(),
					ticketAttachment.getDraftCommentBody());

				_ticketAttachmentService.updateTicketAttachmentDraftCommentBody(
					_liferayOAuth2AccessTokenManager.getAuthorization(
						"liferay-customer-etc-spring-boot-oahs"),
					ticketAttachment.getTicketAttachmentId(), "");
			}
			catch (Exception exception) {
				_log.error(exception, exception);

				_notificationQueueEntryService.addNotificationQueueEntry(
					"solutions@liferay.com", "Customer Portal",
					"is-support@liferay.com",
					"Customer Portal Error Notification",
					StringBundler.concat(
						"<p>There was an error posting a large file uploader ",
						"comment to Zendesk.</p>",
						StackTraceUtil.getStackTrace(exception)));
			}
		}
	}

	private String _buildZendeskTicketCommentBody(
			TicketAttachment ticketAttachment, String zendeskTicketCommentBody)
		throws Exception {

		StringBundler sb = new StringBundler(11);

		if (Validator.isNotNull(zendeskTicketCommentBody)) {
			sb.append(
				StringUtil.replace(
					zendeskTicketCommentBody, CharPool.NEW_LINE, "<br />"));
			sb.append("<br /><br />");
		}

		sb.append("<a href=\"");
		sb.append(lxcDXPServerProtocol);
		sb.append("://");
		sb.append(lxcDXPMainDomain);
		sb.append("/placeholder/");
		sb.append(ticketAttachment.getTicketAttachmentId());
		sb.append("\">");
		sb.append(ticketAttachment.getFileName());
		sb.append("</a>");

		return sb.toString();
	}

	private void _postZendeskComment(
			String emailAddress, long zendeskTicketId,
			String zendeskTicketCommentBody)
		throws Exception {

		ZendeskUser zendeskUser = _zendeskService.fetchZendeskUser(
			emailAddress);

		if (zendeskUser == null) {
			throw new Exception(
				"Zendesk user " + emailAddress + " does not exist");
		}

		if (zendeskUser.isEndUser()) {
			_zendeskService.addEndUserZendeskTicketComment(
				zendeskUser.getEmailAddress(), zendeskTicketCommentBody,
				zendeskTicketId);
		}
		else {
			_zendeskService.addAgentZendeskTicketComment(
				zendeskTicketCommentBody, zendeskTicketId,
				zendeskUser.getZendeskUserId());
		}
	}

	private static final Log _log = LogFactory.getLog(
		TicketAttachmentsCompleteUploadRestController.class);

	@Autowired
	private LiferayOAuth2AccessTokenManager _liferayOAuth2AccessTokenManager;

	@Autowired
	private NotificationQueueEntryService _notificationQueueEntryService;

	@Autowired
	private TicketAttachmentService _ticketAttachmentService;

	@Value("${liferay.osb.spring.boot.client.zendesk.api.email.address}")
	private String _zendeskAPIEmailAddress;

	@Autowired
	private ZendeskService _zendeskService;

}