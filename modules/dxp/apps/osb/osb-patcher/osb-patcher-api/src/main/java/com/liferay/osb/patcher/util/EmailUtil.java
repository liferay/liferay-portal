/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.patcher.util;

import com.liferay.osb.patcher.configuration.PatcherConfiguration;
import com.liferay.osb.patcher.configuration.PatcherEmailConfiguration;
import com.liferay.osb.patcher.constants.PatcherPortletKeys;
import com.liferay.osb.patcher.constants.WorkflowConstants;
import com.liferay.osb.patcher.model.PatcherBuild;
import com.liferay.osb.patcher.model.PatcherFix;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.configuration.module.configuration.ConfigurationProviderUtil;
import com.liferay.portal.kernel.model.BaseModel;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.Portlet;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.WorkflowedModel;
import com.liferay.portal.kernel.service.PortletLocalServiceUtil;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.service.UserLocalServiceUtil;
import com.liferay.portal.kernel.settings.LocalizedValuesMap;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.DateFormatFactoryUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.GroupThreadLocal;
import com.liferay.portal.kernel.util.HtmlUtil;
import com.liferay.portal.kernel.util.Http;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.PrefsPropsUtil;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.SubscriptionSender;
import com.liferay.portal.kernel.util.TextFormatter;
import com.liferay.portal.kernel.util.Validator;

import java.text.DateFormat;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * @author Zsolt Balogh
 */
public class EmailUtil {

	public static void sendEmail(
			long companyId, String emailAddress, String message, String subject,
			Map<String, String> contextAttributes)
		throws Exception {

		if (Validator.isNull(emailAddress) ||
			(Validator.isNull(message) && Validator.isNull(subject))) {

			return;
		}

		SubscriptionSender subscriptionSender = new SubscriptionSender();

		subscriptionSender.setBody(message);

		subscriptionSender.setCompanyId(companyId);

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
			companyId, PropsKeys.ADMIN_EMAIL_FROM_ADDRESS);
		String fromName = PrefsPropsUtil.getString(
			companyId, PropsKeys.ADMIN_EMAIL_FROM_NAME);

		subscriptionSender.setFrom(fromAddress, fromName);

		subscriptionSender.setGroupId(GroupThreadLocal.getGroupId());
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

		subscriptionSender.setCurrentUserId(
			UserLocalServiceUtil.getDefaultUserId(companyId));

		subscriptionSender.addRuntimeSubscribers(emailAddress, emailAddress);

		subscriptionSender.flushNotificationsAsync();
	}

	public static void sendPatcherEmail(
			BaseModel<?> baseModel, int status, User user)
		throws Exception {

		PatcherEmailConfiguration patcherEmailConfiguration =
			ConfigurationProviderUtil.getCompanyConfiguration(
				PatcherEmailConfiguration.class, user.getCompanyId());

		Map<String, String> contextAttributes = getPatcherContextAttributes(
			baseModel, user.getCompanyId(), user.getLocale());

		sendEmail(
			user.getCompanyId(), user.getEmailAddress(),
			_getBodyEmailTemplate(
				user.getLocale(), patcherEmailConfiguration, status),
			_getSubjectEmailTemplate(
				user.getLocale(), patcherEmailConfiguration, status),
			contextAttributes);

		if ((baseModel instanceof WorkflowedModel workflowedModel) &&
			(workflowedModel.getStatusByUserId() != user.getUserId())) {

			user = UserLocalServiceUtil.getUser(
				workflowedModel.getStatusByUserId());

			sendEmail(
				user.getCompanyId(), user.getEmailAddress(),
				_getBodyEmailTemplate(
					user.getLocale(), patcherEmailConfiguration, status),
				_getSubjectEmailTemplate(
					user.getLocale(), patcherEmailConfiguration, status),
				contextAttributes);
		}
	}

	public static void sendPatcherTimeoutEmail(
			BaseModel<?> baseModel, User user)
		throws Exception {

		if (baseModel instanceof PatcherBuild) {
			sendPatcherEmail(
				baseModel, WorkflowConstants.STATUS_BUILD_TIMEOUT, user);
		}
		else {
			sendPatcherEmail(
				baseModel, WorkflowConstants.STATUS_FIX_TIMEOUT, user);
		}
	}

	protected static String getDownloadHotfixURL(PatcherBuild patcherBuild)
		throws Exception {

		if (PatcherBuildUtil.isCompleteOrReady(patcherBuild) &&
			Validator.isNotNull(patcherBuild.getFileName())) {

			StringBundler sb = new StringBundler(3);

			PatcherConfiguration patcherConfiguration =
				ConfigurationProviderUtil.getCompanyConfiguration(
					PatcherConfiguration.class, patcherBuild.getCompanyId());

			sb.append(patcherConfiguration.patcherBuildDownloadURL());
			sb.append(StringPool.SLASH);
			sb.append(patcherBuild.getFileName());

			return sb.toString();
		}

		return StringPool.BLANK;
	}

	protected static Map<String, String> getPatcherContextAttributes(
			BaseModel<?> baseModel, long companyId, Locale locale)
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
				PatcherEmailConfiguration patcherEmailConfiguration =
					ConfigurationProviderUtil.getCompanyConfiguration(
						PatcherEmailConfiguration.class, companyId);

				LocalizedValuesMap emailQACommentsMap =
					patcherEmailConfiguration.emailQAComments();

				String qaComments = HtmlUtil.escape(
					patcherBuild.getQaComments());

				contextAttributes.put(
					"[$QA_COMMENTS_TEMPLATE$]",
					StringUtil.replace(
						emailQACommentsMap.get(locale), "[$QA_COMMENTS$]",
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
					_BUILDS_CONTROLLER_PATH, patcherBuild.getPatcherBuildId()));
			contextAttributes.put(
				"[$VIEW_CONFLICT_FIX_URL$]",
				getPatcherFixesURLsByBuildStatus(
					patcherBuild, WorkflowConstants.STATUS_BUILD_CONFLICT));
			contextAttributes.put(
				"[$VIEW_REBASE_CONFLICT_FIX_URL$]",
				getPatcherFixesURLsByBuildStatus(
					patcherBuild,
					WorkflowConstants.STATUS_BUILD_REBASE_CONFLICT));

			PatcherConfiguration patcherConfiguration =
				ConfigurationProviderUtil.getCompanyConfiguration(
					PatcherConfiguration.class, companyId);

			contextAttributes.put(
				"[$VIEW_TROUBLESHOOTING_URL$]",
				TextFormatter.format(
					patcherConfiguration.troubleshootingURL(),
					TextFormatter.J));
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
					_FIXES_CONTROLLER_PATH, patcherFix.getPatcherFixId()));
		}

		return contextAttributes;
	}

	protected static String getPatcherFixesURLsByBuildStatus(
			PatcherBuild patcherBuild, long patcherBuildStatus)
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

				sb.append(_getDisplayURL(_FIXES_CONTROLLER_PATH, patcherFixId));
			}

			return sb.toString();
		}

		return StringPool.BLANK;
	}

	private static String _getBodyEmailTemplate(
		Locale locale, PatcherEmailConfiguration patcherEmailConfiguration,
		int status) {

		if (status == WorkflowConstants.STATUS_BUILD_COMPLETE) {
			LocalizedValuesMap emailPatcherBuildCompleteBodyMap =
				patcherEmailConfiguration.emailPatcherBuildCompleteBody();

			return emailPatcherBuildCompleteBodyMap.get(locale);
		}
		else if (status == WorkflowConstants.STATUS_BUILD_CONFLICT) {
			LocalizedValuesMap emailPatcherBuildConflictBodyMap =
				patcherEmailConfiguration.emailPatcherBuildConflictBody();

			return emailPatcherBuildConflictBodyMap.get(locale);
		}
		else if (status == WorkflowConstants.STATUS_BUILD_FAILED) {
			LocalizedValuesMap emailPatcherBuildFailedBodyMap =
				patcherEmailConfiguration.emailPatcherBuildFailedBody();

			return emailPatcherBuildFailedBodyMap.get(locale);
		}
		else if (status == WorkflowConstants.STATUS_BUILD_QA_ANALYSIS_NEEDED) {
			LocalizedValuesMap emailPatcherBuildQAAnalysisNeededBodyMap =
				patcherEmailConfiguration.
					emailPatcherBuildQAAnalysisNeededBody();

			return emailPatcherBuildQAAnalysisNeededBodyMap.get(locale);
		}
		else if (status ==
					WorkflowConstants.STATUS_BUILD_QA_AUTOMATION_PASSED) {

			LocalizedValuesMap emailPatcherBuildQAAutomationPassedBodyMap =
				patcherEmailConfiguration.
					emailPatcherBuildQAAutomationPassedBody();

			return emailPatcherBuildQAAutomationPassedBodyMap.get(locale);
		}
		else if (status == WorkflowConstants.STATUS_BUILD_QA_FAILED_MANUALLY) {
			LocalizedValuesMap emailPatcherBuildQAFailedManuallyBodyMap =
				patcherEmailConfiguration.
					emailPatcherBuildQAFailedManuallyBody();

			return emailPatcherBuildQAFailedManuallyBodyMap.get(locale);
		}
		else if (status == WorkflowConstants.STATUS_BUILD_QA_PASSED_MANUALLY) {
			LocalizedValuesMap emailPatcherBuildQAPassedManuallyBodyMap =
				patcherEmailConfiguration.
					emailPatcherBuildQAPassedManuallyBody();

			return emailPatcherBuildQAPassedManuallyBodyMap.get(locale);
		}
		else if (status == WorkflowConstants.STATUS_BUILD_TIMEOUT) {
			LocalizedValuesMap emailPatcherBuildTimeoutBodyMap =
				patcherEmailConfiguration.emailPatcherBuildTimeoutBody();

			return emailPatcherBuildTimeoutBodyMap.get(locale);
		}
		else if (status == WorkflowConstants.STATUS_FIX_COMPLETE) {
			LocalizedValuesMap emailPatcherFixCompleteBodyMap =
				patcherEmailConfiguration.emailPatcherFixCompleteBody();

			return emailPatcherFixCompleteBodyMap.get(locale);
		}
		else if (status == WorkflowConstants.STATUS_FIX_FAILED) {
			LocalizedValuesMap emailPatcherFixFailedBodyMap =
				patcherEmailConfiguration.emailPatcherFixFailedBody();

			return emailPatcherFixFailedBodyMap.get(locale);
		}
		else if (status == WorkflowConstants.STATUS_FIX_TIMEOUT) {
			LocalizedValuesMap emailPatcherFixTimeoutBodyMap =
				patcherEmailConfiguration.emailPatcherFixTimeoutBody();

			return emailPatcherFixTimeoutBodyMap.get(locale);
		}

		return StringPool.BLANK;
	}

	private static String _getDisplayURL(String controllerPath, long classPK)
		throws Exception {

		ThemeDisplay themeDisplay = null;

		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		if (serviceContext != null) {
			themeDisplay = serviceContext.getThemeDisplay();
		}

		if (themeDisplay == null) {
			return StringPool.BLANK;
		}

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

	private static String _getSubjectEmailTemplate(
		Locale locale, PatcherEmailConfiguration patcherEmailConfiguration,
		int status) {

		if (status == WorkflowConstants.STATUS_BUILD_COMPLETE) {
			LocalizedValuesMap emailPatcherBuildCompleteSubjectMap =
				patcherEmailConfiguration.emailPatcherBuildCompleteSubject();

			return emailPatcherBuildCompleteSubjectMap.get(locale);
		}
		else if (status == WorkflowConstants.STATUS_BUILD_CONFLICT) {
			LocalizedValuesMap emailPatcherBuildConflictSubjectMap =
				patcherEmailConfiguration.emailPatcherBuildConflictSubject();

			return emailPatcherBuildConflictSubjectMap.get(locale);
		}
		else if (status == WorkflowConstants.STATUS_BUILD_FAILED) {
			LocalizedValuesMap emailPatcherBuildFailedSubjectMap =
				patcherEmailConfiguration.emailPatcherBuildFailedSubject();

			return emailPatcherBuildFailedSubjectMap.get(locale);
		}
		else if (status == WorkflowConstants.STATUS_BUILD_QA_ANALYSIS_NEEDED) {
			LocalizedValuesMap emailPatcherBuildQAAnalysisNeededSubjectMap =
				patcherEmailConfiguration.
					emailPatcherBuildQAAnalysisNeededSubject();

			return emailPatcherBuildQAAnalysisNeededSubjectMap.get(locale);
		}
		else if (status ==
					WorkflowConstants.STATUS_BUILD_QA_AUTOMATION_PASSED) {

			LocalizedValuesMap emailPatcherBuildQAAutomationPassedSubjectMap =
				patcherEmailConfiguration.
					emailPatcherBuildQAAutomationPassedSubject();

			return emailPatcherBuildQAAutomationPassedSubjectMap.get(locale);
		}
		else if (status == WorkflowConstants.STATUS_BUILD_QA_FAILED_MANUALLY) {
			LocalizedValuesMap emailPatcherBuildQAFailedManuallySubjectMap =
				patcherEmailConfiguration.
					emailPatcherBuildQAFailedManuallySubject();

			return emailPatcherBuildQAFailedManuallySubjectMap.get(locale);
		}
		else if (status == WorkflowConstants.STATUS_BUILD_QA_PASSED_MANUALLY) {
			LocalizedValuesMap emailPatcherBuildQAPassedManuallySubjectMap =
				patcherEmailConfiguration.
					emailPatcherBuildQAPassedManuallySubject();

			return emailPatcherBuildQAPassedManuallySubjectMap.get(locale);
		}
		else if (status == WorkflowConstants.STATUS_BUILD_TIMEOUT) {
			LocalizedValuesMap emailPatcherBuildTimeoutSubjectMap =
				patcherEmailConfiguration.emailPatcherBuildTimeoutSubject();

			return emailPatcherBuildTimeoutSubjectMap.get(locale);
		}
		else if (status == WorkflowConstants.STATUS_FIX_COMPLETE) {
			LocalizedValuesMap emailPatcherFixCompleteSubjectMap =
				patcherEmailConfiguration.emailPatcherFixCompleteSubject();

			return emailPatcherFixCompleteSubjectMap.get(locale);
		}
		else if (status == WorkflowConstants.STATUS_FIX_FAILED) {
			LocalizedValuesMap emailPatcherFixFailedSubjectMap =
				patcherEmailConfiguration.emailPatcherFixFailedSubject();

			return emailPatcherFixFailedSubjectMap.get(locale);
		}
		else if (status == WorkflowConstants.STATUS_FIX_TIMEOUT) {
			LocalizedValuesMap emailPatcherFixTimeoutSubjectMap =
				patcherEmailConfiguration.emailPatcherFixTimeoutSubject();

			return emailPatcherFixTimeoutSubjectMap.get(locale);
		}

		return StringPool.BLANK;
	}

	private static final String _BUILDS_CONTROLLER_PATH = "builds";

	private static final String _FIXES_CONTROLLER_PATH = "fixes";

}