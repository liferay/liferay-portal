/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.customer;

import com.liferay.client.extension.util.spring.boot3.BaseRestController;
import com.liferay.client.extension.util.spring.boot3.client.LiferayOAuth2AccessTokenManager;
import com.liferay.customer.constants.JiraIssueConstants;
import com.liferay.customer.exception.TicketAttachmentNotFoundException;
import com.liferay.customer.model.JiraSupportIssue;
import com.liferay.customer.model.TicketAttachment;
import com.liferay.customer.service.GoogleCloudStorageService;
import com.liferay.customer.service.JiraService;
import com.liferay.customer.service.NotificationQueueEntryService;
import com.liferay.customer.service.TicketAttachmentService;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.util.StackTraceUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.workflow.WorkflowConstants;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Amos Fong
 */
@RequestMapping("/ticket-attachments/{ticketAttachmentId}")
@RestController
public class TicketAttachmentsRestController extends BaseRestController {

	@DeleteMapping
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

	private static final Log _log = LogFactory.getLog(
		TicketAttachmentsRestController.class);

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