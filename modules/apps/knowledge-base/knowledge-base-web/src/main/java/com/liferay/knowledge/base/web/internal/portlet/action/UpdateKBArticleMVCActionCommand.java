/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.knowledge.base.web.internal.portlet.action;

import com.liferay.asset.display.page.portlet.AssetDisplayPageEntryFormProcessor;
import com.liferay.knowledge.base.constants.KBArticleConstants;
import com.liferay.knowledge.base.constants.KBFolderConstants;
import com.liferay.knowledge.base.constants.KBPortletKeys;
import com.liferay.knowledge.base.exception.KBArticleDisplayDateException;
import com.liferay.knowledge.base.exception.KBArticleExpirationDateException;
import com.liferay.knowledge.base.exception.KBArticleReviewDateException;
import com.liferay.knowledge.base.exception.LockedKBArticleException;
import com.liferay.knowledge.base.model.KBArticle;
import com.liferay.knowledge.base.service.KBArticleService;
import com.liferay.knowledge.base.util.KnowledgeBaseUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.portlet.PortletURLFactoryUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseTransactionalMVCActionCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextFactory;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.servlet.MultiSessionMessages;
import com.liferay.portal.kernel.theme.PortletDisplay;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.DateFormatFactoryUtil;
import com.liferay.portal.kernel.util.FastDateFormatFactoryUtil;
import com.liferay.portal.kernel.util.HtmlUtil;
import com.liferay.portal.kernel.util.HttpComponentsUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.util.PropsValues;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;
import jakarta.portlet.PortletRequest;

import java.text.Format;

import java.util.Calendar;
import java.util.Date;
import java.util.Objects;
import java.util.TimeZone;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Adolfo Pérez
 */
@Component(
	property = {
		"jakarta.portlet.name=" + KBPortletKeys.KNOWLEDGE_BASE_ADMIN,
		"jakarta.portlet.name=" + KBPortletKeys.KNOWLEDGE_BASE_ARTICLE,
		"jakarta.portlet.name=" + KBPortletKeys.KNOWLEDGE_BASE_DISPLAY,
		"jakarta.portlet.name=" + KBPortletKeys.KNOWLEDGE_BASE_SEARCH,
		"jakarta.portlet.name=" + KBPortletKeys.KNOWLEDGE_BASE_SECTION,
		"mvc.command.name=/knowledge_base/update_kb_article"
	},
	service = MVCActionCommand.class
)
public class UpdateKBArticleMVCActionCommand
	extends BaseTransactionalMVCActionCommand {

	@Override
	protected void doTransactionalCommand(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		String cmd = ParamUtil.getString(actionRequest, Constants.CMD);

		long resourcePrimKey = ParamUtil.getLong(
			actionRequest, "resourcePrimKey");

		ServiceContext serviceContext = ServiceContextFactory.getInstance(
			KBArticle.class.getName(), actionRequest);

		if (cmd.equals(Constants.ADD) || cmd.equals(Constants.UPDATE)) {
			_updateKBArticle(actionRequest, actionResponse, serviceContext);
		}
		else if (cmd.equals(Constants.CANCEL)) {
			_kbArticleService.unlockKBArticle(resourcePrimKey);

			hideDefaultSuccessMessage(actionRequest);
			sendRedirect(actionRequest, actionResponse);
		}
		else if (cmd.equals(Constants.REVERT)) {
			if (ParamUtil.getBoolean(actionRequest, "forceLock")) {
				ThemeDisplay themeDisplay =
					(ThemeDisplay)actionRequest.getAttribute(
						WebKeys.THEME_DISPLAY);

				_kbArticleService.forceLockKBArticle(
					themeDisplay.getScopeGroupId(), resourcePrimKey);
			}

			int version = ParamUtil.getInteger(
				actionRequest, "version", KBArticleConstants.DEFAULT_VERSION);

			try {
				_kbArticleService.revertKBArticle(
					resourcePrimKey, version, serviceContext);
			}
			catch (LockedKBArticleException lockedKBArticleException) {
				hideDefaultErrorMessage(actionRequest);

				lockedKBArticleException.setActionURL(
					KnowledgeBaseUtil.getKBArticleRevertURL(
						_portal.getLiferayPortletResponse(actionResponse), true,
						KnowledgeBaseUtil.getRedirect(actionRequest),
						resourcePrimKey, version));
				lockedKBArticleException.setCmd(Constants.REVERT);

				throw lockedKBArticleException;
			}
		}
	}

	private String _buildEditURL(
			ActionRequest actionRequest, ActionResponse actionResponse,
			KBArticle kbArticle)
		throws Exception {

		if (Objects.equals(
				_portal.getPortletId(actionRequest),
				KBPortletKeys.KNOWLEDGE_BASE_ADMIN)) {

			return PortletURLBuilder.create(
				PortletURLFactoryUtil.create(
					actionRequest, KBPortletKeys.KNOWLEDGE_BASE_ADMIN,
					PortletRequest.RENDER_PHASE)
			).setMVCRenderCommandName(
				"/knowledge_base/edit_kb_article"
			).setRedirect(
				_getRedirect(actionRequest)
			).setParameter(
				"resourcePrimKey", kbArticle.getResourcePrimKey()
			).setWindowState(
				actionRequest.getWindowState()
			).buildString();
		}

		ThemeDisplay themeDisplay = (ThemeDisplay)actionRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		PortletDisplay portletDisplay = themeDisplay.getPortletDisplay();

		String editURL = _portal.getLayoutFullURL(themeDisplay);

		editURL = HttpComponentsUtil.setParameter(
			editURL, "p_p_id", portletDisplay.getId());
		editURL = HttpComponentsUtil.setParameter(
			editURL, actionResponse.getNamespace() + "mvcRenderCommandName",
			"/knowledge_base/edit_kb_article");
		editURL = HttpComponentsUtil.setParameter(
			editURL, actionResponse.getNamespace() + "redirect",
			_getRedirect(actionRequest));
		editURL = HttpComponentsUtil.setParameter(
			editURL, actionResponse.getNamespace() + "resourcePrimKey",
			kbArticle.getResourcePrimKey());
		editURL = HttpComponentsUtil.setParameter(
			editURL, actionResponse.getNamespace() + "status",
			WorkflowConstants.STATUS_ANY);

		return editURL;
	}

	private String _getContentRedirect(
		Class<?> clazz, long classPK, String redirect) {

		String portletId = HttpComponentsUtil.getParameter(
			redirect, "portletResource", false);

		String namespace = _portal.getPortletNamespace(portletId);

		if (Validator.isNotNull(portletId)) {
			redirect = HttpComponentsUtil.addParameter(
				redirect, namespace + "className", clazz.getName());
			redirect = HttpComponentsUtil.addParameter(
				redirect, namespace + "classPK", classPK);
		}

		return redirect;
	}

	private Date _getDisplayDate(ActionRequest actionRequest, TimeZone timeZone)
		throws Exception {

		Date date = new Date();

		if (!PropsValues.SCHEDULER_ENABLED) {
			return date;
		}

		Date displayDate = ParamUtil.getDate(
			actionRequest, "displayDate",
			DateFormatFactoryUtil.getSimpleDateFormat(
				"yyyy-MM-dd HH:mm", timeZone));

		if ((displayDate == null) || displayDate.before(date)) {
			throw new KBArticleDisplayDateException(
				"Schedule date " + displayDate + " is in the past");
		}

		return displayDate;
	}

	private Date _getExpirationDate(
			ActionRequest actionRequest, boolean neverExpireDefaultValue,
			TimeZone timeZone)
		throws Exception {

		boolean neverExpire = ParamUtil.getBoolean(
			actionRequest, "neverExpire", neverExpireDefaultValue);

		if (!PropsValues.SCHEDULER_ENABLED || neverExpire) {
			return null;
		}

		int expirationDateMonth = ParamUtil.getInteger(
			actionRequest, "expirationDateMonth");
		int expirationDateDay = ParamUtil.getInteger(
			actionRequest, "expirationDateDay");
		int expirationDateYear = ParamUtil.getInteger(
			actionRequest, "expirationDateYear");
		int expirationDateHour = ParamUtil.getInteger(
			actionRequest, "expirationDateHour");
		int expirationDateMinute = ParamUtil.getInteger(
			actionRequest, "expirationDateMinute");
		int expirationDateAmPm = ParamUtil.getInteger(
			actionRequest, "expirationDateAmPm");

		if (expirationDateAmPm == Calendar.PM) {
			expirationDateHour += 12;
		}

		Date expirationDate = _portal.getDate(
			expirationDateMonth, expirationDateDay, expirationDateYear,
			expirationDateHour, expirationDateMinute, timeZone,
			KBArticleExpirationDateException.class);

		if ((expirationDate != null) && expirationDate.before(new Date())) {
			throw new KBArticleExpirationDateException(
				"Expiration date " + expirationDate + " is in the past");
		}

		return expirationDate;
	}

	private String _getRedirect(ActionRequest actionRequest) {
		String redirect = (String)actionRequest.getAttribute(WebKeys.REDIRECT);

		if (Validator.isBlank(redirect)) {
			redirect = ParamUtil.getString(actionRequest, "redirect");

			if (!Validator.isBlank(redirect)) {
				redirect = _portal.escapeRedirect(redirect);
			}
		}

		return redirect;
	}

	private Date _getReviewDate(
			ActionRequest actionRequest, boolean neverReviewDefaultValue,
			TimeZone timeZone)
		throws Exception {

		boolean neverReview = ParamUtil.getBoolean(
			actionRequest, "neverReview", neverReviewDefaultValue);

		if (!PropsValues.SCHEDULER_ENABLED || neverReview) {
			return null;
		}

		int reviewDateMonth = ParamUtil.getInteger(
			actionRequest, "reviewDateMonth");
		int reviewDateDay = ParamUtil.getInteger(
			actionRequest, "reviewDateDay");
		int reviewDateYear = ParamUtil.getInteger(
			actionRequest, "reviewDateYear");
		int reviewDateHour = ParamUtil.getInteger(
			actionRequest, "reviewDateHour");
		int reviewDateMinute = ParamUtil.getInteger(
			actionRequest, "reviewDateMinute");
		int reviewDateAmPm = ParamUtil.getInteger(
			actionRequest, "reviewDateAmPm");

		if (reviewDateAmPm == Calendar.PM) {
			reviewDateHour += 12;
		}

		return _portal.getDate(
			reviewDateMonth, reviewDateDay, reviewDateYear, reviewDateHour,
			reviewDateMinute, timeZone, KBArticleReviewDateException.class);
	}

	private void _updateKBArticle(
			ActionRequest actionRequest, ActionResponse actionResponse,
			ServiceContext serviceContext)
		throws Exception {

		KBArticle kbArticle = null;

		String title = ParamUtil.getString(actionRequest, "title");
		String content = ParamUtil.getString(actionRequest, "content");
		String description = ParamUtil.getString(actionRequest, "description");
		String sourceURL = ParamUtil.getString(actionRequest, "sourceURL");

		ThemeDisplay themeDisplay = (ThemeDisplay)actionRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		User user = _userLocalService.getUser(themeDisplay.getUserId());

		Date displayDate = _getDisplayDate(actionRequest, user.getTimeZone());
		Date expirationDate = _getExpirationDate(
			actionRequest, true, user.getTimeZone());
		Date reviewDate = _getReviewDate(
			actionRequest, true, user.getTimeZone());

		String[] sections = actionRequest.getParameterValues("sections");
		String[] selectedFileNames = ParamUtil.getParameterValues(
			actionRequest, "selectedFileName");

		String cmd = ParamUtil.getString(actionRequest, Constants.CMD);

		int workflowAction = ParamUtil.getInteger(
			actionRequest, "workflowAction");

		if (cmd.equals(Constants.ADD)) {
			long parentResourceClassNameId = ParamUtil.getLong(
				actionRequest, "parentResourceClassNameId",
				_portal.getClassNameId(KBFolderConstants.getClassName()));
			long parentResourcePrimKey = ParamUtil.getLong(
				actionRequest, "parentResourcePrimKey",
				KBFolderConstants.DEFAULT_PARENT_FOLDER_ID);
			String urlTitle = ParamUtil.getString(actionRequest, "urlTitle");

			kbArticle = _kbArticleService.addKBArticle(
				null, _portal.getPortletId(actionRequest),
				parentResourceClassNameId, parentResourcePrimKey, title,
				urlTitle, content, description, sections, sourceURL,
				displayDate, expirationDate, reviewDate, selectedFileNames,
				serviceContext);
		}
		else if (cmd.equals(Constants.UPDATE)) {
			long resourcePrimKey = ParamUtil.getLong(
				actionRequest, "resourcePrimKey");

			long[] removeFileEntryIds = ParamUtil.getLongValues(
				actionRequest, "removeFileEntryIds");

			try {
				if (workflowAction == WorkflowConstants.ACTION_SAVE_DRAFT) {
					kbArticle = _kbArticleService.updateKBArticle(
						resourcePrimKey, title, content, description, sections,
						sourceURL, displayDate, expirationDate, reviewDate,
						selectedFileNames, removeFileEntryIds, serviceContext);
				}
				else {
					kbArticle = _kbArticleService.updateAndUnlockKBArticle(
						resourcePrimKey, title, content, description, sections,
						sourceURL, displayDate, expirationDate, reviewDate,
						selectedFileNames, removeFileEntryIds, serviceContext);
				}
			}
			catch (LockedKBArticleException lockedKBArticleException) {
				hideDefaultErrorMessage(actionRequest);

				lockedKBArticleException.setCmd(Constants.SAVE);

				throw lockedKBArticleException;
			}
		}

		_assetDisplayPageEntryFormProcessor.process(
			KBArticle.class.getName(), kbArticle.getResourcePrimKey(),
			actionRequest);

		String successMessage = StringPool.BLANK;

		if (workflowAction == WorkflowConstants.ACTION_SAVE_DRAFT) {
			String editURL = _buildEditURL(
				actionRequest, actionResponse, kbArticle);

			actionRequest.setAttribute(WebKeys.REDIRECT, editURL);

			successMessage = _language.format(
				_portal.getHttpServletRequest(actionRequest),
				"x-was-successfully-saved-as-draft",
				new Object[] {
					"<strong>" + HtmlUtil.escape(title) + "</strong>"
				});
		}
		else {
			String redirect = _portal.escapeRedirect(
				ParamUtil.getString(actionRequest, "redirect"));

			if (cmd.equals(Constants.ADD) && Validator.isNotNull(redirect)) {
				actionRequest.setAttribute(
					WebKeys.REDIRECT,
					_getContentRedirect(
						KBArticle.class, kbArticle.getResourcePrimKey(),
						redirect));
			}

			if (kbArticle.isScheduled()) {
				Format dateTimeFormat = FastDateFormatFactoryUtil.getDateTime(
					themeDisplay.getLocale(), themeDisplay.getTimeZone());

				successMessage = _language.format(
					_portal.getHttpServletRequest(actionRequest),
					"x-will-be-published-on-x",
					new Object[] {
						"<strong>" + HtmlUtil.escape(kbArticle.getTitle()) +
							"</strong>",
						dateTimeFormat.format(kbArticle.getDisplayDate())
					});
			}
			else {
				successMessage = _language.format(
					_portal.getHttpServletRequest(actionRequest),
					"x-was-successfully-published",
					new Object[] {
						"<strong>" + HtmlUtil.escape(kbArticle.getTitle()) +
							"</strong>"
					});
			}
		}

		MultiSessionMessages.add(
			actionRequest, "kbArticleSuccessMessage", successMessage);

		hideDefaultSuccessMessage(actionRequest);
	}

	@Reference
	private AssetDisplayPageEntryFormProcessor
		_assetDisplayPageEntryFormProcessor;

	@Reference
	private KBArticleService _kbArticleService;

	@Reference
	private Language _language;

	@Reference
	private Portal _portal;

	@Reference
	private UserLocalService _userLocalService;

}