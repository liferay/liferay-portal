/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.journal.web.internal.portlet.action;

import com.liferay.asset.kernel.model.AssetEntry;
import com.liferay.asset.kernel.service.AssetEntryLocalService;
import com.liferay.dynamic.data.mapping.form.values.factory.DDMFormValuesFactory;
import com.liferay.dynamic.data.mapping.service.DDMStructureLocalService;
import com.liferay.dynamic.data.mapping.util.DDMFormValuesToFieldsConverter;
import com.liferay.journal.constants.JournalPortletKeys;
import com.liferay.journal.model.JournalArticle;
import com.liferay.journal.service.JournalArticleLocalService;
import com.liferay.journal.service.JournalArticleService;
import com.liferay.journal.util.JournalConverter;
import com.liferay.journal.util.JournalHelper;
import com.liferay.journal.web.internal.asset.model.JournalArticleAssetRenderer;
import com.liferay.journal.web.internal.util.JournalArticleUtil;
import com.liferay.layout.model.LayoutClassedModelUsage;
import com.liferay.layout.service.LayoutClassedModelUsageLocalService;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Portlet;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.portlet.PortletPreferencesFactoryUtil;
import com.liferay.portal.kernel.portlet.PortletURLFactoryUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCActionCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextFactory;
import com.liferay.portal.kernel.servlet.MultiSessionMessages;
import com.liferay.portal.kernel.servlet.SessionMessages;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.upload.UploadPortletRequest;
import com.liferay.portal.kernel.util.FriendlyURLNormalizer;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.HtmlUtil;
import com.liferay.portal.kernel.util.HttpComponentsUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.Localization;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.workflow.WorkflowConstants;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;
import jakarta.portlet.PortletPreferences;
import jakarta.portlet.PortletRequest;

import jakarta.servlet.http.HttpServletRequest;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Eudaldo Alonso
 */
@Component(
	property = {
		"jakarta.portlet.name=" + JournalPortletKeys.JOURNAL,
		"mvc.command.name=/journal/add_article",
		"mvc.command.name=/journal/update_article"
	},
	service = MVCActionCommand.class
)
public class UpdateArticleMVCActionCommand extends BaseMVCActionCommand {

	@Override
	protected void doProcessAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		String actionName = ParamUtil.getString(
			actionRequest, ActionRequest.ACTION_NAME);

		UploadPortletRequest uploadPortletRequest =
			_portal.getUploadPortletRequest(actionRequest);

		long groupId = ParamUtil.getLong(uploadPortletRequest, "groupId");

		String oldUrlTitle = StringPool.BLANK;

		String tempOldUrlTitle = StringPool.BLANK;

		if (!actionName.equals("/journal/add_article")) {
			JournalArticle article = _journalArticleService.getArticle(
				groupId, ParamUtil.getString(uploadPortletRequest, "articleId"),
				ParamUtil.getDouble(uploadPortletRequest, "version"));

			tempOldUrlTitle = article.getUrlTitle();
		}

		JournalArticle article = JournalArticleUtil.addOrUpdateArticle(
			actionName, _ddmFormValuesFactory, _ddmFormValuesToFieldsConverter,
			_ddmStructureLocalService, _journalArticleService,
			_journalConverter, _journalHelper, _localization, _portal,
			actionRequest);

		if (!tempOldUrlTitle.equals(article.getUrlTitle())) {
			oldUrlTitle = tempOldUrlTitle;
		}

		// Journal content

		String portletResource = ParamUtil.getString(
			actionRequest, "portletResource");
		long refererPlid = ParamUtil.getLong(actionRequest, "refererPlid");

		if (Validator.isNotNull(portletResource) && (refererPlid > 0)) {
			AssetEntry assetEntry = _assetEntryLocalService.fetchEntry(
				JournalArticle.class.getName(), article.getResourcePrimKey());

			PortletPreferences portletPreferences =
				PortletPreferencesFactoryUtil.getStrictPortletSetup(
					_layoutLocalService.getLayout(refererPlid),
					portletResource);

			if (portletPreferences != null) {
				Group group = _groupLocalService.fetchGroup(
					article.getGroupId());

				if (group != null) {
					portletPreferences.setValue(
						"groupExternalReferenceCode",
						group.getExternalReferenceCode());
				}

				portletPreferences.setValue(
					"articleExternalReferenceCode",
					article.getExternalReferenceCode());

				portletPreferences.store();
			}

			if (assetEntry != null) {
				ServiceContext serviceContext =
					ServiceContextFactory.getInstance(
						JournalArticle.class.getName(), uploadPortletRequest);

				serviceContext.setAttribute(
					"updateAutoTags",
					ParamUtil.getBoolean(actionRequest, "updateAutoTags"));

				_updateLayoutClassedModelUsage(
					groupId,
					_portal.getClassNameId(JournalArticle.class.getName()),
					article.getResourcePrimKey(), portletResource, refererPlid,
					serviceContext);
			}
		}

		// Asset display page

		int workflowAction = ParamUtil.getInteger(
			actionRequest, "workflowAction", WorkflowConstants.ACTION_PUBLISH);

		if (workflowAction != WorkflowConstants.ACTION_SAVE_DRAFT) {
			String referringPortletResource = ParamUtil.getString(
				actionRequest, "referringPortletResource");

			if (Validator.isNotNull(referringPortletResource)) {
				MultiSessionMessages.add(
					actionRequest,
					referringPortletResource + "requestProcessed");
			}
			else if (Validator.isNotNull(portletResource)) {
				MultiSessionMessages.add(
					actionRequest, portletResource + "requestProcessed");
			}

			if (article.isPending()) {
				ThemeDisplay themeDisplay =
					(ThemeDisplay)actionRequest.getAttribute(
						WebKeys.THEME_DISPLAY);

				User user = themeDisplay.getUser();

				Date displayDate = _portal.getDate(
					ParamUtil.getInteger(
						uploadPortletRequest, "displayDateMonth"),
					ParamUtil.getInteger(
						uploadPortletRequest, "displayDateDay"),
					ParamUtil.getInteger(
						uploadPortletRequest, "displayDateYear"),
					ParamUtil.getInteger(
						uploadPortletRequest, "displayDateHour"),
					ParamUtil.getInteger(
						uploadPortletRequest, "displayDateMinute"),
					user.getTimeZone(), null);

				if (displayDate != null) {
					MultiSessionMessages.add(
						actionRequest, "articlePendingScheduled",
						article.getId());
				}
				else {
					MultiSessionMessages.add(
						actionRequest, "articlePending", article.getId());
				}
			}
			else if (article.isScheduled()) {
				MultiSessionMessages.add(
					actionRequest, "articleScheduled", article.getId());
			}
			else {
				if (actionName.equals("/journal/add_article")) {
					MultiSessionMessages.add(
						actionRequest, "articleCreated", article.getId());
				}
				else {
					MultiSessionMessages.add(
						actionRequest, "articleUpdated", article.getId());
				}
			}
		}
		else {
			MultiSessionMessages.add(
				actionRequest, "articleSavedAsDraft", article.getId());
		}

		HttpServletRequest httpServletRequest = _portal.getHttpServletRequest(
			actionRequest);

		Map<Locale, String> friendlyURLMap = _localization.getLocalizationMap(
			actionRequest, "friendlyURL");

		Map<String, String> friendlyURLWarningMessages =
			_getFriendlyURLWarningMessages(
				actionRequest, article.getFriendlyURLMap(), friendlyURLMap);

		for (Map.Entry<String, String> entry :
				friendlyURLWarningMessages.entrySet()) {

			SessionMessages.add(
				httpServletRequest, entry.getKey(), entry.getValue());
		}

		_sendEditArticleRedirect(actionRequest, article, oldUrlTitle);

		boolean hideDefaultSuccessMessage = ParamUtil.getBoolean(
			actionRequest, "hideDefaultSuccessMessage");

		if (hideDefaultSuccessMessage) {
			hideDefaultSuccessMessage(actionRequest);
		}
		else {
			SessionMessages.remove(
				httpServletRequest,
				_portal.getPortletId(actionRequest) +
					SessionMessages.KEY_SUFFIX_HIDE_DEFAULT_SUCCESS_MESSAGE);
		}

		hideDefaultSuccessMessage(actionRequest);
	}

	private Map<String, String> _getFriendlyURLWarningMessages(
		ActionRequest actionRequest, Map<Locale, String> currentFriendlyURLMap,
		Map<Locale, String> originalFriendlyURLMap) {

		List<Long> excludedGroupIds = new ArrayList<>();

		ThemeDisplay themeDisplay = (ThemeDisplay)actionRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		Group group = themeDisplay.getScopeGroup();

		excludedGroupIds.add(group.getGroupId());

		if (group.isStagingGroup()) {
			excludedGroupIds.add(group.getLiveGroupId());
		}
		else if (group.hasStagingGroup()) {
			Group stagingGroup = group.getStagingGroup();

			excludedGroupIds.add(stagingGroup.getGroupId());
		}

		List<String> friendlyURLChangedMessages = new ArrayList<>();
		List<Locale> friendlyURLDuplicatedLocales = new ArrayList<>();
		Map<String, List<Long>> friendlyURLGroupIdsMap = new HashMap<>();
		HttpServletRequest httpServletRequest = _portal.getHttpServletRequest(
			actionRequest);

		for (Map.Entry<Locale, String> entry :
				currentFriendlyURLMap.entrySet()) {

			Locale locale = entry.getKey();

			String originalFriendlyURL = originalFriendlyURLMap.get(locale);

			String normalizedOriginalFriendlyURL =
				_friendlyURLNormalizer.normalizeWithEncoding(
					originalFriendlyURL);

			String currentFriendlyURL = entry.getValue();

			if (Validator.isNotNull(originalFriendlyURL) &&
				!currentFriendlyURL.equals(normalizedOriginalFriendlyURL)) {

				friendlyURLChangedMessages.add(
					_language.format(
						httpServletRequest, "for-locale-x-x-was-changed-to-x",
						new Object[] {
							"<strong>" + locale.getLanguage() + "</strong>",
							"<strong>" +
								HtmlUtil.escapeURL(originalFriendlyURL) +
									"</strong>",
							"<strong>" + currentFriendlyURL + "</strong>"
						}));
			}

			List<Long> groupIds = friendlyURLGroupIdsMap.computeIfAbsent(
				currentFriendlyURL,
				key -> ListUtil.remove(
					_journalArticleLocalService.getGroupIdsByUrlTitle(
						themeDisplay.getCompanyId(), key),
					excludedGroupIds));

			if (!groupIds.isEmpty() &&
				((groupIds.size() > 1) ||
				 !Objects.equals(
					 groupIds.get(0), themeDisplay.getScopeGroupId()))) {

				friendlyURLDuplicatedLocales.add(locale);
			}
		}

		if (friendlyURLChangedMessages.isEmpty() &&
			friendlyURLDuplicatedLocales.isEmpty()) {

			return Collections.emptyMap();
		}

		return HashMapBuilder.put(
			"friendlyURLChanged_requestProcessedWarning",
			() -> {
				if (friendlyURLChangedMessages.isEmpty()) {
					return null;
				}

				friendlyURLChangedMessages.add(
					0,
					_language.get(
						httpServletRequest,
						"the-following-friendly-urls-were-changed-to-ensure-" +
							"uniqueness"));

				return StringUtil.merge(friendlyURLChangedMessages, "<br />");
			}
		).put(
			"friendlyURLDuplicated_requestProcessedWarning",
			() -> {
				if (friendlyURLDuplicatedLocales.isEmpty()) {
					return null;
				}

				Locale siteDefaultLocale = _portal.getSiteDefaultLocale(group);

				if ((friendlyURLDuplicatedLocales.size() > 1) &&
					friendlyURLDuplicatedLocales.remove(siteDefaultLocale)) {

					friendlyURLDuplicatedLocales.add(0, siteDefaultLocale);
				}

				if (friendlyURLDuplicatedLocales.size() > 3) {
					return _language.format(
						themeDisplay.getLocale(),
						StringBundler.concat(
							"the-content-has-been-published-but-might-cause-",
							"errors.-the-url-used-in-x-and-x-more-",
							"translations-already-exists-in-other-sites-or-",
							"asset-libraries"),
						new String[] {
							_getLocaleDisplayNames(
								themeDisplay.getLocale(),
								friendlyURLDuplicatedLocales.get(0),
								friendlyURLDuplicatedLocales.get(1),
								friendlyURLDuplicatedLocales.get(2)),
							String.valueOf(
								friendlyURLDuplicatedLocales.size() - 3)
						},
						false);
				}

				if (friendlyURLDuplicatedLocales.size() == 1) {
					return _language.format(
						themeDisplay.getLocale(),
						"the-content-has-been-published-but-might-cause-" +
							"errors.-the-url-used-in-x-already-exists-in-" +
								"other-sites-or-asset-libraries",
						new String[] {
							_getLocaleDisplayNames(
								themeDisplay.getLocale(),
								friendlyURLDuplicatedLocales.get(0))
						},
						false);
				}

				int lastElementIndex = friendlyURLDuplicatedLocales.size() - 1;

				List<Locale> locales = ListUtil.subList(
					friendlyURLDuplicatedLocales, 0, lastElementIndex);

				return _language.format(
					themeDisplay.getLocale(),
					"the-content-has-been-published-but-might-cause-errors.-" +
						"the-url-used-in-x-and-x-already-exists-in-other-" +
							"sites-or-asset-libraries",
					new String[] {
						_getLocaleDisplayNames(
							themeDisplay.getLocale(),
							locales.toArray(new Locale[0])),
						_getLocaleDisplayNames(
							themeDisplay.getLocale(),
							friendlyURLDuplicatedLocales.get(lastElementIndex))
					},
					false);
			}
		).build();
	}

	private String _getLocaleDisplayNames(Locale locale, Locale... locales) {
		List<String> displayLocaleNames = new ArrayList<>();

		for (Locale currentLocale : locales) {
			displayLocaleNames.add(
				LocaleUtil.getLocaleDisplayName(currentLocale, locale));
		}

		return StringUtil.merge(displayLocaleNames, StringPool.COMMA_AND_SPACE);
	}

	private String _getSaveAndContinueRedirect(
		ActionRequest actionRequest, JournalArticle article, String redirect) {

		return PortletURLBuilder.create(
			PortletURLFactoryUtil.create(
				actionRequest, JournalPortletKeys.JOURNAL,
				PortletRequest.RENDER_PHASE)
		).setMVCRenderCommandName(
			"/journal/edit_article"
		).setRedirect(
			redirect
		).setPortletResource(
			ParamUtil.getString(actionRequest, "portletResource")
		).setParameter(
			"articleId", article.getArticleId()
		).setParameter(
			"folderId", article.getFolderId()
		).setParameter(
			"groupId", article.getGroupId()
		).setParameter(
			"languageId",
			() -> {
				String languageId = ParamUtil.getString(
					actionRequest, "languageId");

				if (Validator.isNull(languageId)) {
					return null;
				}

				Locale locale = LocaleUtil.fromLanguageId(
					languageId, true, false);

				if (locale == null) {
					return null;
				}

				return languageId;
			}
		).setParameter(
			"referringPortletResource",
			ParamUtil.getString(actionRequest, "referringPortletResource")
		).setParameter(
			"resourcePrimKey", article.getResourcePrimKey()
		).setParameter(
			"version", article.getVersion()
		).setWindowState(
			actionRequest.getWindowState()
		).buildString();
	}

	private void _sendEditArticleRedirect(
			ActionRequest actionRequest, JournalArticle article,
			String oldUrlTitle)
		throws Exception {

		String actionName = ParamUtil.getString(
			actionRequest, ActionRequest.ACTION_NAME);

		String redirect = ParamUtil.getString(actionRequest, "redirect");

		int workflowAction = ParamUtil.getInteger(
			actionRequest, "workflowAction", WorkflowConstants.ACTION_PUBLISH);

		String portletId = HttpComponentsUtil.getParameter(
			redirect, "portletResource", false);

		String namespace = _portal.getPortletNamespace(portletId);

		if (Validator.isNotNull(oldUrlTitle) &&
			Validator.isNotNull(portletId)) {

			String oldRedirectParam = namespace + "redirect";

			String oldRedirect = HttpComponentsUtil.getParameter(
				redirect, oldRedirectParam, false);

			if (Validator.isNotNull(oldRedirect)) {
				String newRedirect = HttpComponentsUtil.decodeURL(oldRedirect);

				newRedirect = StringUtil.replace(
					newRedirect, oldUrlTitle, article.getUrlTitle());
				newRedirect = StringUtil.replace(
					newRedirect, oldRedirectParam, "redirect");

				redirect = StringUtil.replace(
					redirect, oldRedirect, newRedirect);
			}
		}

		if ((article != null) &&
			(workflowAction == WorkflowConstants.ACTION_SAVE_DRAFT)) {

			redirect = _getSaveAndContinueRedirect(
				actionRequest, article, redirect);
		}
		else {
			redirect = _portal.escapeRedirect(redirect);

			if (Validator.isNotNull(redirect) &&
				Validator.isNotNull(portletId) &&
				actionName.equals("/journal/add_article") &&
				(article != null) && Validator.isNotNull(namespace)) {

				redirect = HttpComponentsUtil.addParameter(
					redirect, namespace + "className",
					JournalArticle.class.getName());
				redirect = HttpComponentsUtil.addParameter(
					redirect, namespace + "classPK",
					JournalArticleAssetRenderer.getClassPK(article));
			}
		}

		actionRequest.setAttribute(WebKeys.REDIRECT, redirect);
	}

	private void _updateLayoutClassedModelUsage(
		long groupId, long classNameId, long classPK, String portletResource,
		long plid, ServiceContext serviceContext) {

		LayoutClassedModelUsage layoutClassedModelUsage =
			_layoutClassedModelUsageLocalService.fetchLayoutClassedModelUsage(
				groupId, StringPool.BLANK, classNameId, classPK,
				portletResource, _portal.getClassNameId(Portlet.class), plid);

		if (layoutClassedModelUsage != null) {
			return;
		}

		_layoutClassedModelUsageLocalService.addLayoutClassedModelUsage(
			groupId, StringPool.BLANK, classNameId, classPK, portletResource,
			_portal.getClassNameId(Portlet.class), plid, serviceContext);
	}

	@Reference
	private AssetEntryLocalService _assetEntryLocalService;

	@Reference
	private DDMFormValuesFactory _ddmFormValuesFactory;

	@Reference
	private DDMFormValuesToFieldsConverter _ddmFormValuesToFieldsConverter;

	@Reference
	private DDMStructureLocalService _ddmStructureLocalService;

	@Reference
	private FriendlyURLNormalizer _friendlyURLNormalizer;

	@Reference
	private GroupLocalService _groupLocalService;

	@Reference
	private JournalArticleLocalService _journalArticleLocalService;

	@Reference
	private JournalArticleService _journalArticleService;

	@Reference
	private JournalConverter _journalConverter;

	@Reference
	private JournalHelper _journalHelper;

	@Reference
	private Language _language;

	@Reference
	private LayoutClassedModelUsageLocalService
		_layoutClassedModelUsageLocalService;

	@Reference
	private LayoutLocalService _layoutLocalService;

	@Reference
	private Localization _localization;

	@Reference
	private Portal _portal;

}