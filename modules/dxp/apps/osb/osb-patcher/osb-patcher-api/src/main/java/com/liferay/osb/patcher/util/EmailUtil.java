/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.patcher.util;

import com.liferay.osb.patcher.constants.PatcherPortletKeys;
import com.liferay.osb.patcher.constants.WorkflowConstants;
import com.liferay.osb.patcher.model.PatcherBuild;
import com.liferay.osb.patcher.model.PatcherFix;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.BaseModel;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.Portlet;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.WorkflowedModel;
import com.liferay.portal.kernel.service.PortletLocalServiceUtil;
import com.liferay.portal.kernel.service.UserLocalServiceUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.DateFormatFactoryUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HtmlUtil;
import com.liferay.portal.kernel.util.Http;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.PrefsPropsUtil;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.StringBundler;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.SubscriptionSender;
import com.liferay.portal.kernel.util.TextFormatter;
import com.liferay.portal.kernel.util.Validator;

import java.io.IOException;

import java.net.URL;

import java.text.DateFormat;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Zsolt Balogh
 */
public class EmailUtil {

	public static void sendEmail(
			ThemeDisplay themeDisplay, String emailAddress, String message,
			String subject, Map<String, String> contextAttributes)
		throws Exception {

		if (Validator.isNull(emailAddress) ||
			(Validator.isNull(message) && Validator.isNull(subject))) {

			return;
		}

		SubscriptionSender subscriptionSender = new SubscriptionSender();

		subscriptionSender.setBody(message);

		subscriptionSender.setCompanyId(themeDisplay.getCompanyId());

		if ((contextAttributes != null) && !contextAttributes.isEmpty()) {
			for (Map.Entry<String, String> entry :
					contextAttributes.entrySet()) {

				boolean escape = true;

				if (StringUtil.endsWith(entry.getKey(), "TEMPLATE$]")) {
					escape = false;
				}

				subscriptionSender.setContextAttribute(
					entry.getKey(), entry.getValue(), escape);
			}
		}

		String fromAddress = PrefsPropsUtil.getString(
			themeDisplay.getCompanyId(), PropsKeys.ADMIN_EMAIL_FROM_ADDRESS);
		String fromName = PrefsPropsUtil.getString(
			themeDisplay.getCompanyId(), PropsKeys.ADMIN_EMAIL_FROM_NAME);

		subscriptionSender.setFrom(fromAddress, fromName);

		subscriptionSender.setGroupId(themeDisplay.getScopeGroupId());
		subscriptionSender.setHtmlFormat(true);

		DateFormat mailIdDateFormat = DateFormatFactoryUtil.getSimpleDateFormat(
			"yyyyMMddHHmmss");

		subscriptionSender.setMailId(
			"osb_patcher_entry", mailIdDateFormat.format(new Date()));

		subscriptionSender.setReplyToAddress(fromAddress);

		subscriptionSender.setSubject(subject);

		if (Validator.isNull(subject)) {
			subscriptionSender.setSubject(message);
		}

		subscriptionSender.setCurrentUserId(themeDisplay.getDefaultUserId());

		subscriptionSender.addRuntimeSubscribers(emailAddress, emailAddress);

		subscriptionSender.flushNotificationsAsync();
	}

	public static void sendPatcherStatusEmail(
			BaseModel<?> baseModel, String emailAddress,
			ThemeDisplay themeDisplay)
		throws Exception {

		Integer baseModelStatus = BaseModelUtil.fetchBaseModelStatus(baseModel);

		if (baseModelStatus == null) {
			return;
		}

		sendPatcherEmail(
			baseModel, emailAddress,
			WorkflowConstants.getStatusLabel(baseModelStatus), themeDisplay);
	}

	public static void sendPatcherTimeoutEmail(
			BaseModel<?> baseModel, String emailAddress,
			ThemeDisplay themeDisplay)
		throws Exception {

		sendPatcherEmail(baseModel, emailAddress, "timeout", themeDisplay);
	}

	protected static String applyTextFormatterFormats(
		String string, int... formats) {

		for (int format : formats) {
			string = TextFormatter.format(string, format);
		}

		return string;
	}

	protected static String getBodyEmailTemplate(String templateName)
		throws Exception {

		return getEmailTemplate(templateName + "_body.tmpl");
	}

	protected static String getDownloadHotfixURL(PatcherBuild patcherBuild)
		throws Exception {

		if (PatcherBuildUtil.isCompleteOrReady(patcherBuild) &&
			Validator.isNotNull(patcherBuild.getFileName())) {

			StringBundler sb = new StringBundler(3);

			String fileName = patcherBuild.getFileName();

			if (fileName.contains("/liferay-dxp-")) {
				sb.append("https://releases-cdn.liferay.com/dxp/hotfix");
			}
			else {
				sb.append(PortletPropsValues.OSB_PATCHER_BUILD_DOWNLOAD_URL);
			}

			sb.append(StringPool.SLASH);
			sb.append(fileName);

			return sb.toString();
		}

		return StringPool.BLANK;
	}

	protected static String getEmailTemplate(String templateName)
		throws Exception {

		ClassLoader portletClassLoader = EmailUtil.class.getClassLoader();

		URL url = portletClassLoader.getResource(
			_TEMPLATE_DIRECTORY + templateName);

		if (url == null) {
			return StringPool.BLANK;
		}

		try {
			return com.liferay.petra.string.StringUtil.read(
				portletClassLoader, _TEMPLATE_DIRECTORY + templateName);
		}
		catch (IOException ioException) {
			_log.error(
				"Unable to read the content for: " + _TEMPLATE_DIRECTORY +
					templateName);
		}

		return StringPool.BLANK;
	}

	protected static Map<String, String> getPatcherContextAttributes(
			BaseModel<?> baseModel, ThemeDisplay themeDisplay)
		throws Exception {

		Map<String, String> contextAttributes = new HashMap<>();

		DateFormat dateFormat = DateFormatFactoryUtil.getSimpleDateFormat(
			"yyyy-MM-dd HH:mm:ss");

		if (baseModel instanceof PatcherBuild) {
			PatcherBuild patcherBuild = (PatcherBuild)baseModel;

			contextAttributes.put(
				"[$CREATE_DATE$]",
				dateFormat.format(patcherBuild.getCreateDate()));
			contextAttributes.put(
				"[$DOWNLOAD_HOTFIX_URL$]", getDownloadHotfixURL(patcherBuild));
			contextAttributes.put(
				"[$PATCHER_BUILD_ID$]",
				String.valueOf(patcherBuild.getPatcherBuildId()));

			if (Validator.isNotNull(patcherBuild.getQaComments())) {
				String qaCommentsParagraphTemplate = getEmailTemplate(
					"email_qa_comments.tmpl");

				String qaComments = HtmlUtil.escape(
					patcherBuild.getQaComments());

				contextAttributes.put(
					"[$QA_COMMENTS_TEMPLATE$]",
					StringUtil.replace(
						qaCommentsParagraphTemplate, "[$QA_COMMENTS$]",
						qaComments));
			}
			else {
				contextAttributes.put(
					"[$QA_COMMENTS_TEMPLATE$]", StringPool.BLANK);
			}

			contextAttributes.put(
				"[$QA_STATUS$]",
				TextFormatter.format(
					WorkflowConstants.getStatusLabel(
						patcherBuild.getQaStatus()),
					TextFormatter.J));
			contextAttributes.put(
				"[$STATUS$]",
				TextFormatter.format(
					WorkflowConstants.getStatusLabel(patcherBuild.getStatus()),
					TextFormatter.J));
			contextAttributes.put(
				"[$STATUS_BY_USER_NAME$]", patcherBuild.getStatusByUserName());
			contextAttributes.put(
				"[$STATUS_DATE$]",
				dateFormat.format(patcherBuild.getStatusDate()));
			contextAttributes.put(
				"[$SUPPORT_TICKET$]", patcherBuild.getSupportTicket());
			contextAttributes.put("[$USER_NAME$]", patcherBuild.getUserName());
			contextAttributes.put(
				"[$VIEW_BUILD_URL$]",
				_getDisplayURL(
					_BUILDS_CONTROLLER_PATH, patcherBuild.getPatcherBuildId(),
					themeDisplay));
			contextAttributes.put(
				"[$VIEW_CONFLICT_FIX_URL$]",
				getPatcherFixesURLsByBuildStatus(
					patcherBuild, WorkflowConstants.STATUS_BUILD_CONFLICT,
					themeDisplay));
			contextAttributes.put(
				"[$VIEW_REBASE_CONFLICT_FIX_URL$]",
				getPatcherFixesURLsByBuildStatus(
					patcherBuild,
					WorkflowConstants.STATUS_BUILD_REBASE_CONFLICT,
					themeDisplay));
			contextAttributes.put(
				"[$VIEW_TROUBLESHOOTING_URL$]",
				TextFormatter.format(
					PortletPropsValues.TROUBLESHOOTING_URL, TextFormatter.J));
		}

		if (baseModel instanceof PatcherFix) {
			PatcherFix patcherFix = (PatcherFix)baseModel;

			contextAttributes.put(
				"[$CREATE_DATE$]",
				dateFormat.format(patcherFix.getCreateDate()));
			contextAttributes.put(
				"[$PATCHER_FIX_ID$]",
				String.valueOf(patcherFix.getPatcherFixId()));
			contextAttributes.put("[$PATCHER_FIX_NAME$]", patcherFix.getName());
			contextAttributes.put(
				"[$STATUS$]",
				WorkflowConstants.getStatusLabel(patcherFix.getStatus()));
			contextAttributes.put(
				"[$STATUS_BY_USER_NAME$]", patcherFix.getStatusByUserName());
			contextAttributes.put(
				"[$STATUS_DATE$]",
				dateFormat.format(patcherFix.getStatusDate()));
			contextAttributes.put("[$USER_NAME$]", patcherFix.getUserName());
			contextAttributes.put(
				"[$VIEW_FIX_URL$]",
				_getDisplayURL(
					_FIXES_CONTROLLER_PATH, patcherFix.getPatcherFixId(),
					themeDisplay));
		}

		return contextAttributes;
	}

	protected static String getPatcherFixesURLsByBuildStatus(
			PatcherBuild patcherBuild, long patcherBuildStatus,
			ThemeDisplay themeDisplay)
		throws Exception {

		if (patcherBuild.getStatus() != patcherBuildStatus) {
			return StringPool.BLANK;
		}

		List<Long> patcherFixIds = new ArrayList<>();

		if (patcherBuildStatus == WorkflowConstants.STATUS_BUILD_CONFLICT) {
			patcherFixIds = PatcherFixUtil.getPatcherBuildFixIdsByFixStatus(
				patcherBuild, WorkflowConstants.STATUS_FIX_CONFLICT);
		}
		else if (patcherBuildStatus ==
					WorkflowConstants.STATUS_BUILD_REBASE_CONFLICT) {

			patcherFixIds = PatcherFixUtil.getPatcherBuildFixIdsByFixStatus(
				patcherBuild, WorkflowConstants.STATUS_FIX_REBASE_CONFLICT);
		}

		if (!patcherFixIds.isEmpty()) {
			StringBundler sb = new StringBundler(2 * patcherFixIds.size());

			for (long patcherFixId : patcherFixIds) {
				if (sb.length() > 0) {
					sb.append(", ");
				}

				sb.append(
					_getDisplayURL(
						_FIXES_CONTROLLER_PATH, patcherFixId, themeDisplay));
			}

			return sb.toString();
		}

		return StringPool.BLANK;
	}

	protected static String getSubjectEmailTemplate(String templateName)
		throws Exception {

		return getEmailTemplate(templateName + "_subject.tmpl");
	}

	protected static void sendPatcherEmail(
			BaseModel<?> baseModel, String emailAddress,
			String templateNameSuffix, ThemeDisplay themeDisplay)
		throws Exception {

		if (Validator.isNull(templateNameSuffix)) {
			return;
		}

		StringBundler sb = new StringBundler(4);

		sb.append("email_");

		Class<?> modelClass = baseModel.getModelClass();

		sb.append(
			applyTextFormatterFormats(
				modelClass.getSimpleName(), TextFormatter.L, TextFormatter.K,
				TextFormatter.N));
		sb.append(StringPool.UNDERLINE);
		sb.append(
			applyTextFormatterFormats(
				templateNameSuffix, TextFormatter.B, TextFormatter.N));

		String body = getBodyEmailTemplate(sb.toString());

		String subject = getSubjectEmailTemplate(sb.toString());

		Map<String, String> contextAttributes = getPatcherContextAttributes(
			baseModel, themeDisplay);

		sendEmail(themeDisplay, emailAddress, body, subject, contextAttributes);

		if (baseModel instanceof WorkflowedModel) {
			Long statusByUserId = BaseModelUtil.fetchBaseModelStatusByUserId(
				baseModel);
			Long userId = BaseModelUtil.fetchBaseModelUserId(baseModel);

			if (statusByUserId != userId) {
				User user = UserLocalServiceUtil.getUser(statusByUserId);

				sendEmail(
					themeDisplay, user.getEmailAddress(), body, subject,
					contextAttributes);
			}
		}
	}

	private static String _getDisplayURL(
			String controllerPath, long classPK, ThemeDisplay themeDisplay)
		throws Exception {

		String layoutFriendlyURL = StringPool.BLANK;

		Layout layout = themeDisplay.getLayout();

		if (layout != null) {
			layoutFriendlyURL = GetterUtil.getString(
				PortalUtil.getLayoutFriendlyURL(layout, themeDisplay));
		}

		StringBundler sb = new StringBundler(8);

		if (!layoutFriendlyURL.startsWith(Http.HTTP_WITH_SLASH) &&
			!layoutFriendlyURL.startsWith(Http.HTTPS_WITH_SLASH)) {

			sb.append(
				PortalUtil.getPortalURL(
					themeDisplay.getRequest(), themeDisplay.isSecure()));
		}

		sb.append(layoutFriendlyURL);
		sb.append(Portal.FRIENDLY_URL_SEPARATOR);

		Portlet portlet = PortletLocalServiceUtil.getPortletById(
			PatcherPortletKeys.PATCHER);

		sb.append(portlet.getFriendlyURLMapping());

		sb.append(StringPool.SLASH);
		sb.append(controllerPath);
		sb.append(StringPool.SLASH);
		sb.append(classPK);

		return sb.toString();
	}

	private static final String _BUILDS_CONTROLLER_PATH = "builds";

	private static final String _FIXES_CONTROLLER_PATH = "fixes";

	private static final String _TEMPLATE_DIRECTORY =
		"com/liferay/osb/patcher/dependencies/";

	private static final Log _log = LogFactoryUtil.getLog(EmailUtil.class);

}