/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.customer;

import com.google.cloud.storage.StorageException;

import com.liferay.client.extension.util.spring.boot3.client.LiferayOAuth2AccessTokenManager;
import com.liferay.customer.constants.JiraIssueConstants;
import com.liferay.customer.exception.FileServerUnavailableException;
import com.liferay.customer.exception.JiraOrganizationNotFoundException;
import com.liferay.customer.exception.TicketAttachmentAlreadyApprovedException;
import com.liferay.customer.exception.TicketAttachmentNotFoundException;
import com.liferay.customer.model.JiraSupportIssue;
import com.liferay.customer.model.TicketAttachment;
import com.liferay.customer.service.GoogleCloudStorageService;
import com.liferay.customer.service.JiraService;
import com.liferay.customer.service.NotificationQueueEntryService;
import com.liferay.customer.service.TicketAttachmentService;
import com.liferay.petra.function.UnsafeFunction;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.util.StackTraceUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.workflow.WorkflowConstants;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.json.JSONArray;
import org.json.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * @author Amos Fong
 */
@RequestMapping("/ticket-attachments")
@RestController
public class TicketAttachmentsRestController extends BaseRestController {

	@DeleteMapping("/{ticketAttachmentId}")
	public ResponseEntity<String> delete(
		@AuthenticationPrincipal Jwt jwt,
		@PathVariable("ticketAttachmentId") long ticketAttachmentId) {

		try {
			TicketAttachment ticketAttachment =
				_ticketAttachmentService.getTicketAttachment(
					"Bearer " + jwt.getTokenValue(), ticketAttachmentId);

			_ticketAttachmentService.updateTicketAttachmentState(
				"Bearer " + jwt.getTokenValue(), ticketAttachmentId,
				WorkflowConstants.STATUS_IN_TRASH);

			try {
				_googleCloudStorageService.deleteObject(
					ticketAttachment.getGCSBucketName(),
					ticketAttachment.getGCSObjectName());

				_ticketAttachmentService.deleteTicketAttachment(
					"Bearer " + jwt.getTokenValue(), ticketAttachmentId);

				return new ResponseEntity<>(HttpStatus.OK);
			}
			catch (Exception exception) {
				_log.error(exception, exception);

				return new ResponseEntity<>("", HttpStatus.ACCEPTED);
			}
		}
		catch (TicketAttachmentNotFoundException
					ticketAttachmentNotFoundException) {

			_log.error(
				ticketAttachmentNotFoundException,
				ticketAttachmentNotFoundException);

			return new ResponseEntity<>(
				"ATTACHMENT_NOT_FOUND", HttpStatus.NOT_FOUND);
		}
		catch (Exception exception) {
			_log.error(exception, exception);

			return new ResponseEntity<>(
				"UNEXPECTED_ERROR", HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@GetMapping("/by-external-reference-code/{externalReferenceCode}/download")
	public ResponseEntity<String> getByExternalReferenceCodeDownload(
		@AuthenticationPrincipal Jwt jwt,
		@PathVariable("externalReferenceCode") String externalReferenceCode) {

		return _getResponseEntity(
			jwt,
			authorization -> _ticketAttachmentService.getTicketAttachment(
				authorization, externalReferenceCode));
	}

	@GetMapping("/by-id/{id}/download")
	public ResponseEntity<String> getByIdDownload(
		@AuthenticationPrincipal Jwt jwt, @PathVariable("id") long id) {

		return _getResponseEntity(
			jwt,
			authorization -> _ticketAttachmentService.getTicketAttachment(
				authorization, id));
	}

	@PostMapping("/{ticketAttachmentId}/complete-upload")
	public ResponseEntity<String> postCompleteUpload(
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
		catch (TicketAttachmentAlreadyApprovedException
					ticketAttachmentAlreadyApprovedException) {

			_log.error(
				ticketAttachmentAlreadyApprovedException,
				ticketAttachmentAlreadyApprovedException);

			return new ResponseEntity<>(
				"ATTACHMENT_ALREADY_EXISTS", HttpStatus.CONFLICT);
		}
		catch (Exception exception) {
			_log.error(exception, exception);

			return new ResponseEntity<>(
				"UNEXPECTED_ERROR", HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@PostMapping("/initiate-upload")
	public ResponseEntity<String> postInitiateUpload(
		@AuthenticationPrincipal Jwt jwt, @RequestBody String json,
		@RequestHeader(name = HttpHeaders.ORIGIN) String origin) {

		try {
			JSONObject jsonObject = new JSONObject(json);

			String fileName = jsonObject.getString("fileName");
			String fileSize = jsonObject.getString("fileSize");
			String gcsSessionURL = jsonObject.optString("gcsSessionURL");
			String md5Checksum = jsonObject.optString("md5Checksum");

			String ticketId = jsonObject.getString("ticketId");

			JiraSupportIssue jiraSupportIssue =
				_jiraService.getJiraSupportIssue(ticketId);

			if (jiraSupportIssue == null) {
				return new ResponseEntity<>(
					"INVALID_TICKET_NUMBER", HttpStatus.NOT_FOUND);
			}

			if (jiraSupportIssue.isClosed()) {
				return new ResponseEntity<>(
					"TICKET_IS_CLOSED", HttpStatus.BAD_REQUEST);
			}

			String accountKey = getAccountKey(
				jiraSupportIssue.getOrganizationId(),
				jiraSupportIssue.getWorkspaceId());

			TicketAttachment ticketAttachment =
				_ticketAttachmentService.fetchTicketAttachment(
					"Bearer " + jwt.getTokenValue(), fileName, ticketId,
					md5Checksum);

			if (ticketAttachment != null) {
				if (ticketAttachment.isApproved()) {
					return new ResponseEntity<>(
						"ATTACHMENT_ALREADY_EXISTS", HttpStatus.CONFLICT);
				}
			}
			else {
				String externalReferenceCode = jsonObject.optString(
					"externalReferenceCode");
				String type = jsonObject.optString("type");

				ticketAttachment = _ticketAttachmentService.addTicketAttachment(
					"Bearer " + jwt.getTokenValue(), accountKey,
					externalReferenceCode, fileName, fileSize, ticketId,
					md5Checksum, TicketAttachment.STATUS_DRAFT, type);
			}

			JSONObject responseJSONObject = new JSONObject();

			responseJSONObject.put(
				"accountKey", accountKey
			).put(
				"ticketAttachmentId", ticketAttachment.getTicketAttachmentId()
			);

			if (!gcsSessionURL.equals("")) {
				responseJSONObject.put("gcsSessionURL", gcsSessionURL);
			}
			else {
				responseJSONObject.put(
					"gcsSessionURL",
					_googleCloudStorageService.getUploadSessionURL(
						origin, ticketAttachment.getGCSBucketName(),
						ticketAttachment.getGCSObjectName(), fileSize));
			}

			return new ResponseEntity<>(
				responseJSONObject.toString(), HttpStatus.OK);
		}
		catch (FileServerUnavailableException fileServerUnavailableException) {
			_log.error(
				fileServerUnavailableException, fileServerUnavailableException);

			return new ResponseEntity<>(
				"FILE_SERVER_UNAVAILABLE", HttpStatus.SERVICE_UNAVAILABLE);
		}
		catch (HttpClientErrorException.Forbidden httpClientErrorException) {
			_log.error(httpClientErrorException, httpClientErrorException);

			return new ResponseEntity<>(
				"FORBIDDEN_ACCESS", HttpStatus.FORBIDDEN);
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

	@Scheduled(cron = "0 0 0,12 * * *")
	public void scheduledCleanUp() throws Exception {
		if (_log.isInfoEnabled()) {
			_log.info("Cleaning up JSM large file attachments");
		}

		StringBundler sb = new StringBundler(9);

		sb.append("(project in (");
		sb.append(_jiraSupportFLSProject);
		sb.append(StringPool.COMMA);
		sb.append(_jiraSupportHCProject);
		sb.append(")) and (status in ('");
		sb.append(StringUtil.merge(JiraIssueConstants.STATUSES_CLOSED, "', '"));
		sb.append("')) and (status changed to ('");
		sb.append(StringUtil.merge(JiraIssueConstants.STATUSES_CLOSED, "', '"));
		sb.append("') after -8d before -7d)");

		List<JiraSupportIssue> jiraSupportIssues = _jiraService.search(
			sb.toString(), new String[] {"key"});

		for (JiraSupportIssue jiraSupportIssue : jiraSupportIssues) {
			_deleteTicketAttachments(jiraSupportIssue.getKey());
		}
	}

	@Scheduled(cron = "0 0 * * * *")
	public void scheduledDeleteTicketAttachment() throws Exception {
		List<TicketAttachment> ticketAttachments =
			_ticketAttachmentService.search(
				_getAuthorization(),
				"state eq " + WorkflowConstants.STATUS_IN_TRASH, 1, 500);

		for (TicketAttachment ticketAttachment : ticketAttachments) {
			try {
				_googleCloudStorageService.deleteObject(
					ticketAttachment.getGCSBucketName(),
					ticketAttachment.getGCSObjectName());

				_ticketAttachmentService.deleteTicketAttachment(
					_getAuthorization(),
					ticketAttachment.getTicketAttachmentId());
			}
			catch (Exception exception) {
				_log.error(exception, exception);

				_notificationQueueEntryService.addNotificationQueueEntry(
					"solutions@liferay.com", "Customer Portal",
					"is-support@liferay.com",
					"Customer Portal Error Notification",
					StringBundler.concat(
						"<p>There was an error deleting a large file from ",
						"Google Cloud Storage.</p>",
						StackTraceUtil.getStackTrace(exception)));
			}
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
						"comment to JSM.</p>",
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
						"content",
						new JSONArray(
						).put(
							new JSONObject(
							).put(
								"text",
								_getCommentAuthorInfo(
									jwt, ticketAttachment.getUserId())
							).put(
								"type", "text"
							)
						)
					).put(
						"type", "paragraph"
					)
				).put(
					new JSONObject(
					).put(
						"content",
						new JSONArray(
						).put(
							new JSONObject(
							).put(
								"content",
								new JSONArray(
								).putAll(
									_getCommentBodyJSONArray(commentBody)
								)
							).put(
								"type", "paragraph"
							)
						)
					).put(
						"type", "blockquote"
					)
				).put(
					new JSONObject(
					).put(
						"content",
						new JSONArray(
						).put(
							new JSONObject(
							).put(
								"marks",
								new JSONArray(
								).put(
									new JSONObject(
									).put(
										"attrs",
										new JSONObject(
										).put(
											"href", sb.toString()
										)
									).put(
										"type", "link"
									)
								)
							).put(
								"text", ticketAttachment.getFileName()
							).put(
								"type", "text"
							)
						)
					).put(
						"type", "paragraph"
					)
				)
			).put(
				"type", "doc"
			).put(
				"version", 1
			)
		).toString();
	}

	private void _deleteTicketAttachments(String jiraIssueKey)
		throws Exception {

		List<TicketAttachment> ticketAttachments =
			_ticketAttachmentService.search(
				_getAuthorization(), "jiraIssueKey eq '" + jiraIssueKey + "'",
				1, 500);

		for (TicketAttachment ticketAttachment : ticketAttachments) {
			if (_log.isDebugEnabled()) {
				_log.debug(
					"Deleting ticket attachment " +
						ticketAttachment.getTicketAttachmentId());
			}

			_ticketAttachmentService.deleteTicketAttachment(
				_getAuthorization(), ticketAttachment.getTicketAttachmentId());

			_googleCloudStorageService.deleteObject(
				ticketAttachment.getGCSBucketName(),
				ticketAttachment.getGCSObjectName());
		}
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

			if (!matcher.find()) {
				continue;
			}

			String link = matcher.group(1);

			jsonArray.put(
				new JSONObject(
				).put(
					"marks",
					new JSONArray(
					).put(
						new JSONObject(
						).put(
							"attrs",
							new JSONObject(
							).put(
								"href", link
							)
						).put(
							"type", "link"
						)
					)
				).put(
					"text", link
				).put(
					"type", "text"
				));
		}

		return jsonArray;
	}

	private ResponseEntity<String> _getResponseEntity(
		Jwt jwt,
		UnsafeFunction<String, TicketAttachment, Exception> unsafeFunction) {

		try {
			String authorization = "Bearer " + jwt.getTokenValue();

			TicketAttachment ticketAttachment = unsafeFunction.apply(
				authorization);

			return new ResponseEntity<>(
				_googleCloudStorageService.getDownloadURL(
					ticketAttachment.getGCSBucketName(),
					ticketAttachment.getGCSObjectName()),
				HttpStatus.OK);
		}
		catch (FileServerUnavailableException fileServerUnavailableException) {
			_log.error(
				fileServerUnavailableException, fileServerUnavailableException);

			return new ResponseEntity<>(
				"FILE_SERVER_UNAVAILABLE", HttpStatus.SERVICE_UNAVAILABLE);
		}
		catch (StorageException storageException) {
			_log.error(storageException, storageException);

			if (storageException.getCode() == 404) {
				return new ResponseEntity<>(
					"FILE_NOT_FOUND_IN_STORAGE", HttpStatus.NOT_FOUND);
			}

			return new ResponseEntity<>(
				"FILE_SERVER_UNAVAILABLE", HttpStatus.SERVICE_UNAVAILABLE);
		}
		catch (TicketAttachmentNotFoundException
					ticketAttachmentNotFoundException) {

			_log.error(
				ticketAttachmentNotFoundException,
				ticketAttachmentNotFoundException);

			return new ResponseEntity<>(
				"ATTACHMENT_NOT_FOUND", HttpStatus.NOT_FOUND);
		}
		catch (Exception exception) {
			_log.error(exception, exception);

			return new ResponseEntity<>(
				"UNEXPECTED_ERROR", HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	private static final String _URL_REGEX =
		"((?:https?:\\/\\/|www\\.)[^\\s()]+\\b)";

	private static final Log _log = LogFactory.getLog(
		TicketAttachmentsRestController.class);

	private static final Pattern _pattern = Pattern.compile(_URL_REGEX);

	@Value("${liferay.customer.portal.url}")
	private String _customerPortalURL;

	@Autowired
	private GoogleCloudStorageService _googleCloudStorageService;

	@Autowired
	private JiraService _jiraService;

	@Value("${liferay.customer.jira.support.fls.project}")
	private String _jiraSupportFLSProject;

	@Value("${liferay.customer.jira.support.hc.project}")
	private String _jiraSupportHCProject;

	@Autowired
	private LiferayOAuth2AccessTokenManager _liferayOAuth2AccessTokenManager;

	@Autowired
	private NotificationQueueEntryService _notificationQueueEntryService;

	@Autowired
	private TicketAttachmentService _ticketAttachmentService;

}