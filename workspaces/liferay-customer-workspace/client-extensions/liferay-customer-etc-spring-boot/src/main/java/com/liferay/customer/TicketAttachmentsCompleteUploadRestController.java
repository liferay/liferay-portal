/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.customer;

import com.liferay.client.extension.util.spring.boot3.BaseRestController;
import com.liferay.client.extension.util.spring.boot3.client.LiferayOAuth2AccessTokenManager;
import com.liferay.customer.model.TicketAttachment;
import com.liferay.customer.service.JiraService;
import com.liferay.customer.service.NotificationQueueEntryService;
import com.liferay.customer.service.TicketAttachmentService;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.util.StackTraceUtil;
import com.liferay.portal.kernel.util.Validator;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.json.JSONArray;
import org.json.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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

			String jiraIssueCommentBody = _buildJiraIssueCommentBody(
				jwt, ticketAttachment, jsonObject.optString("commentBody"));

			try {
				_jiraService.addComment(
					ticketAttachment.getJiraIssueKey(), jiraIssueCommentBody);

				return new ResponseEntity<>(HttpStatus.OK);
			}
			catch (Exception exception) {
				_log.error(exception, exception);

				_ticketAttachmentService.updateTicketAttachmentDraftCommentBody(
					"Bearer " + jwt.getTokenValue(), ticketAttachmentId,
					jiraIssueCommentBody);

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
			_ticketAttachmentService.search(
				_getAuthorization(),
				"draftCommentBody ne null and draftCommentBody ne '' and " +
					"(state eq 0 or state eq null) and status/any(s:s eq 0)",
				1, 500);

		for (TicketAttachment ticketAttachment : ticketAttachments) {
			try {
				_jiraService.addComment(
					ticketAttachment.getJiraIssueKey(),
					ticketAttachment.getDraftCommentBody());

				_ticketAttachmentService.updateTicketAttachmentDraftCommentBody(
					_getAuthorization(),
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

	private String _buildJiraIssueCommentBody(
			Jwt jwt, TicketAttachment ticketAttachment, String commentBody)
		throws Exception {

		StringBundler sb = new StringBundler(3);

		sb.append(_customerPortalURL);
		sb.append("/ticket-attachments/#/id/");
		sb.append(ticketAttachment.getTicketAttachmentId());

		return new JSONObject(
		).put(
			"body",
			new JSONObject(
			).put(
				"content",
				new JSONArray(
				).put(
					new JSONObject(
					).put(
						"type", "paragraph"
					).put(
						"content",
						new JSONArray(
						).put(
							new JSONObject(
							).put(
								"type", "text"
							).put(
								"text",
								_getCommentAuthorInfo(
									jwt, ticketAttachment.getUserId())
							)
						)
					)
				).put(
					new JSONObject(
					).put(
						"content",
						new JSONArray(
						).put(
							new JSONObject(
							).put(
								"type", "paragraph"
							).put(
								"content",
								new JSONArray(
								).putAll(
									_getCommentBodyJSONArray(commentBody)
								)
							)
						)
					).put(
						"type", "blockquote"
					)
				).put(
					new JSONObject(
					).put(
						"type", "paragraph"
					).put(
						"content",
						new JSONArray(
						).put(
							new JSONObject(
							).put(
								"type", "text"
							).put(
								"text", ticketAttachment.getFileName()
							).put(
								"marks",
								new JSONArray(
								).put(
									new JSONObject(
									).put(
										"type", "link"
									).put(
										"attrs",
										new JSONObject(
										).put(
											"href", sb.toString()
										)
									)
								)
							)
						)
					)
				)
			).put(
				"type", "doc"
			).put(
				"version", 1
			)
		).toString();
	}

	private String _getAuthorization() {
		return _liferayOAuth2AccessTokenManager.getAuthorization(
			"liferay-customer-etc-spring-boot-oahs");
	}

	private String _getCommentAuthorInfo(Jwt jwt, long userId) {
		StringBundler sb = new StringBundler(6);

		JSONObject jsonObject = new JSONObject(
			get(
				"Bearer " + jwt.getTokenValue(),
				UriComponentsBuilder.fromPath(
					"/o/headless-admin-user/v1.0/user-accounts/" + userId
				).build(
				).toUri()));

		sb.append(jsonObject.getString("name"));

		sb.append(" (");
		sb.append(jsonObject.getString("emailAddress"));
		sb.append(") ");

		String languageId = jsonObject.optString("languageId");

		if (languageId.equals("es_ES")) {
			sb.append("escribió");
		}
		else if (languageId.equals("ja_JP")) {
			sb.append("_さんが書きました");
		}
		else if (languageId.equals("pt_BR")) {
			sb.append("escreveu");
		}
		else {
			sb.append("wrote");
		}

		sb.append(":");

		return sb.toString();
	}

	private JSONArray _getCommentBodyJSONArray(String commentBody) {
		JSONArray jsonArray = new JSONArray();

		Matcher matcher = _pattern.matcher(commentBody);

		for (String part : commentBody.split(_URL_REGEX)) {
			if (Validator.isNotNull(part)) {
				jsonArray.put(
					new JSONObject(
					).put(
						"text", part
					).put(
						"type", "text"
					));
			}

			if (matcher.find()) {
				String link = matcher.group(1);

				jsonArray.put(
					new JSONObject(
					).put(
						"type", "text"
					).put(
						"text", link
					).put(
						"marks",
						new JSONArray(
						).put(
							new JSONObject(
							).put(
								"type", "link"
							).put(
								"attrs",
								new JSONObject(
								).put(
									"href", link
								)
							)
						)
					));
			}
		}

		return jsonArray;
	}

	private static final String _URL_REGEX =
		"((?:https?:\\/\\/|www\\.)[^\\s()]+\\b)";

	private static final Log _log = LogFactory.getLog(
		TicketAttachmentsCompleteUploadRestController.class);

	private static final Pattern _pattern = Pattern.compile(_URL_REGEX);

	@Value("${liferay.customer.portal.url}")
	private String _customerPortalURL;

	@Autowired
	private JiraService _jiraService;

	@Autowired
	private LiferayOAuth2AccessTokenManager _liferayOAuth2AccessTokenManager;

	@Autowired
	private NotificationQueueEntryService _notificationQueueEntryService;

	@Autowired
	private TicketAttachmentService _ticketAttachmentService;

}