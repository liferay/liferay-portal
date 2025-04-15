/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.web.internal.servlet;

import com.liferay.mail.kernel.model.MailMessage;
import com.liferay.mail.kernel.service.MailService;
import com.liferay.osb.faro.model.FaroUser;
import com.liferay.osb.faro.service.FaroEmailLocalService;
import com.liferay.osb.faro.service.FaroUserLocalService;
import com.liferay.osb.faro.util.EmailUtil;
import com.liferay.osb.faro.util.FaroPropsValues;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.util.DateUtil;
import com.liferay.portal.kernel.util.HttpComponentsUtil;
import com.liferay.portal.kernel.util.StringUtil;

import jakarta.mail.internet.InternetAddress;

import jakarta.servlet.Servlet;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

import java.util.Date;
import java.util.ResourceBundle;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Matthew Kong
 */
@Component(
	property = {
		"osgi.http.whiteboard.context.path=/email",
		"osgi.http.whiteboard.servlet.name=com.liferay.osb.faro.web.internal.servlet.EmailServlet",
		"osgi.http.whiteboard.servlet.pattern=/email/*"
	},
	service = Servlet.class
)
public class EmailServlet extends BaseAsahServlet {

	@Override
	protected void doPost(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws IOException, ServletException {

		try {
			_sendEmail(
				_jsonFactory.createJSONObject(
					StringUtil.read(httpServletRequest.getInputStream())));
		}
		catch (Exception exception) {
			throw new ServletException(exception);
		}
	}

	private String _getDownloadURL(String batchId, long groupId) {
		String url =
			FaroPropsValues.FARO_URL + "/o/proxy/download/data-control-tasks";

		url = HttpComponentsUtil.addParameter(url, "projectGroupId", groupId);

		return HttpComponentsUtil.addParameter(url, "batchId", batchId);
	}

	private void _sendEmail(JSONObject jsonObject) throws Exception {
		FaroUser faroUser = _faroUserLocalService.fetchFaroUser(
			jsonObject.getLong("ownerId"));

		if (faroUser == null) {
			return;
		}

		InternetAddress from = new InternetAddress(
			"ac@liferay.com", "Analytics Cloud");

		User user = _userLocalService.getUser(faroUser.getLiveUserId());

		InternetAddress to = new InternetAddress(
			user.getEmailAddress(), user.getFullName());

		ResourceBundle resourceBundle =
			_faroEmailLocalService.getResourceBundle(user.getLocale());

		String subject = _language.get(
			resourceBundle, "your-request-is-complete");

		String body = StringUtil.replace(
			_faroEmailLocalService.getTemplate(
				"com/liferay/osb/faro/dependencies" +
					"/data-control-task-complete.html"),
			new String[] {
				"[$BUTTON_TEXT$]", "[$BUTTON_URL$]", "[$DOWNLOAD_URL$]",
				"[$EMAIL_HEADER_URL$]", "[$EMAIL_TITLE$]", "[$FOOTER_MENU_1$]",
				"[$FOOTER_MENU_2$]", "[$FOOTER_MENU_3$]", "[$FOOTER_MSG_1$]",
				"[$FOOTER_MSG_2$]", "[$FOOTER_MSG_3$]", "[$FOOTER_MSG_4$]",
				"[$LIFERAY_LOGO_URL$]", "[$NOTIFICATION_MSG_1$]",
				"[$NOTIFICATION_MSG_2$]", "[$TITLE_MSG$]", "[$YEAR$]"
			},
			new String[] {
				_language.get(resourceBundle, "download"),
				EmailUtil.getShareIconURL(),
				_getDownloadURL(
					jsonObject.getString("batchId"), faroUser.getGroupId()),
				EmailUtil.getEmailHeaderURL(), subject,
				_language.get(resourceBundle, "contact-support"),
				_language.get(resourceBundle, "documentation"),
				_language.get(resourceBundle, "announcements"),
				_language.format(
					resourceBundle, "this-email-was-sent-by-x",
					new String[] {
						"<a style=\"color: #0b5fff; text-decoration: none;\" " +
							"href=\"https://liferay.com\" target=\"_blank\">",
						"</a>"
					}),
				_language.get(resourceBundle, "need-help"),
				_language.get(
					resourceBundle, "let-our-team-do-the-work-for-you"),
				_language.get(
					resourceBundle,
					"liferay-experts-are-available-to-answer-your-questions-" +
						"anytime"),
				EmailUtil.getLiferayIconURL(),
				_language.format(
					resourceBundle,
					"the-request-x-has-been-processed-and-is-ready-to-be-" +
						"downloaded",
					jsonObject.getString("batchId")),
				_language.get(
					resourceBundle, "download-files-are-available-for-30-days"),
				subject, String.valueOf(DateUtil.getYear(new Date()))
			});

		_mailService.sendEmail(new MailMessage(from, to, subject, body, true));
	}

	@Reference
	private FaroEmailLocalService _faroEmailLocalService;

	@Reference
	private FaroUserLocalService _faroUserLocalService;

	@Reference
	private JSONFactory _jsonFactory;

	@Reference
	private Language _language;

	@Reference
	private MailService _mailService;

	@Reference
	private UserLocalService _userLocalService;

}