/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.admin.web.internal.portlet.action;

import com.liferay.asset.kernel.exception.AssetCategoryException;
import com.liferay.asset.kernel.model.AssetVocabulary;
import com.liferay.configuration.admin.constants.ConfigurationAdminPortletKeys;
import com.liferay.layout.seo.service.LayoutSEOSiteLocalService;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.change.tracking.CTTransactionException;
import com.liferay.portal.kernel.exception.DataLimitExceededException;
import com.liferay.portal.kernel.exception.DuplicateGroupException;
import com.liferay.portal.kernel.exception.GroupInheritContentException;
import com.liferay.portal.kernel.exception.GroupKeyException;
import com.liferay.portal.kernel.exception.GroupParentException;
import com.liferay.portal.kernel.exception.LocaleException;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.GroupConstants;
import com.liferay.portal.kernel.model.ModelHintsUtil;
import com.liferay.portal.kernel.model.SiteConstants;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.WorkflowDefinitionLink;
import com.liferay.portal.kernel.portlet.JSONPortletResponseUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCActionCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.GroupService;
import com.liferay.portal.kernel.service.LayoutSetService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextFactory;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.service.WorkflowDefinitionLinkLocalService;
import com.liferay.portal.kernel.servlet.MultiSessionMessages;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.transaction.Propagation;
import com.liferay.portal.kernel.transaction.TransactionConfig;
import com.liferay.portal.kernel.transaction.TransactionInvokerUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HttpComponentsUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.Localization;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.PrefsPropsUtil;
import com.liferay.portal.kernel.util.PropertiesParamUtil;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.UnicodeProperties;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.liveusers.LiveUsers;
import com.liferay.portal.security.permission.PermissionCacheUtil;
import com.liferay.ratings.kernel.RatingsType;
import com.liferay.site.admin.web.internal.constants.SiteAdminConstants;
import com.liferay.site.admin.web.internal.constants.SiteAdminPortletKeys;
import com.liferay.site.initializer.SiteInitializer;
import com.liferay.site.initializer.SiteInitializerRegistry;
import com.liferay.sites.kernel.util.Sites;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;
import jakarta.portlet.PortletRequest;
import jakarta.portlet.PortletURL;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Eudaldo Alonso
 */
@Component(
	property = {
		"jakarta.portlet.name=" + SiteAdminPortletKeys.SITE_ADMIN,
		"mvc.command.name=/site_admin/add_group"
	},
	service = MVCActionCommand.class
)
public class AddGroupMVCActionCommand extends BaseMVCActionCommand {

	@Override
	protected void doProcessAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		JSONObject jsonObject = _jsonFactory.createJSONObject();

		try {
			Callable<Group> callable = new GroupCallable(actionRequest);

			Group group = TransactionInvokerUtil.invoke(
				_transactionConfig, callable);

			long liveGroupId = ParamUtil.getLong(actionRequest, "liveGroupId");

			if (liveGroupId <= 0) {
				hideDefaultSuccessMessage(actionRequest);

				MultiSessionMessages.add(
					actionRequest,
					ConfigurationAdminPortletKeys.SITE_SETTINGS +
						"requestProcessed");
			}

			PortletURL siteAdministrationURL = _getSiteAdministrationURL(
				actionRequest, group);

			siteAdministrationURL.setParameter(
				"redirect", siteAdministrationURL.toString());
			siteAdministrationURL.setParameter(
				"historyKey",
				HttpComponentsUtil.getParameter(
					ParamUtil.getString(actionRequest, "redirect"),
					actionResponse.getNamespace() + "historyKey", false));

			jsonObject.put("redirectURL", siteAdministrationURL.toString());

			JSONPortletResponseUtil.writeJSON(
				actionRequest, actionResponse, jsonObject);
		}
		catch (CTTransactionException ctTransactionException) {
			JSONPortletResponseUtil.writeJSON(
				actionRequest, actionResponse,
				JSONUtil.put(
					"redirectURL",
					() -> {
						PortletURL redirectURL =
							_portal.getControlPanelPortletURL(
								actionRequest, SiteAdminPortletKeys.SITE_ADMIN,
								PortletRequest.RENDER_PHASE);

						return redirectURL.toString();
					}));

			throw ctTransactionException;
		}
		catch (Exception exception) {
			hideDefaultErrorMessage(actionRequest);
			hideDefaultSuccessMessage(actionRequest);

			_handlePortalException(actionRequest, actionResponse, exception);
		}
		catch (Throwable throwable) {
			throw new Exception(throwable);
		}
	}

	private PortletURL _getSiteAdministrationURL(
		ActionRequest actionRequest, Group group) {

		String portletId = SiteAdminPortletKeys.SITE_ADMIN;

		long liveGroupId = ParamUtil.getLong(actionRequest, "liveGroupId");

		if (liveGroupId <= 0) {
			portletId = ConfigurationAdminPortletKeys.SITE_SETTINGS;
		}

		return _portal.getControlPanelPortletURL(
			actionRequest, group, portletId, 0, 0, PortletRequest.RENDER_PHASE);
	}

	private String _handleGroupKeyException(ActionRequest actionRequest) {
		ThemeDisplay themeDisplay = (ThemeDisplay)actionRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		StringBundler sb = new StringBundler(5);

		sb.append(
			_language.format(
				themeDisplay.getRequest(),
				"the-x-cannot-be-x-or-a-reserved-word-such-as-x",
				new String[] {
					SiteConstants.NAME_LABEL,
					SiteConstants.getNameGeneralRestrictions(
						themeDisplay.getLocale()),
					SiteConstants.NAME_RESERVED_WORDS
				}));

		sb.append(StringPool.SPACE);

		sb.append(
			_language.format(
				themeDisplay.getRequest(),
				"the-x-cannot-contain-the-following-invalid-characters-x",
				new String[] {
					SiteConstants.NAME_LABEL,
					SiteConstants.NAME_INVALID_CHARACTERS
				}));

		sb.append(StringPool.SPACE);

		int groupKeyMaxLength = ModelHintsUtil.getMaxLength(
			Group.class.getName(), "groupKey");

		sb.append(
			_language.format(
				themeDisplay.getRequest(),
				"the-x-cannot-contain-more-than-x-characters",
				new String[] {
					SiteConstants.NAME_LABEL, String.valueOf(groupKeyMaxLength)
				}));

		return sb.toString();
	}

	private void _handlePortalException(
			ActionRequest actionRequest, ActionResponse actionResponse,
			Exception exception)
		throws Exception {

		if (_log.isDebugEnabled()) {
			_log.debug(exception);
		}

		ThemeDisplay themeDisplay = (ThemeDisplay)actionRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		String errorMessage = null;

		if (exception instanceof AssetCategoryException) {
			AssetCategoryException assetCategoryException =
				(AssetCategoryException)exception;

			AssetVocabulary assetVocabulary =
				assetCategoryException.getVocabulary();

			String assetVocabularyTitle = StringPool.BLANK;

			if (assetVocabulary != null) {
				assetVocabularyTitle = assetVocabulary.getTitle(
					themeDisplay.getLocale());
			}

			if (assetCategoryException.getType() ==
					AssetCategoryException.AT_LEAST_ONE_CATEGORY) {

				errorMessage = _language.format(
					themeDisplay.getRequest(),
					"please-select-at-least-one-category-for-x",
					assetVocabularyTitle);
			}
			else if (assetCategoryException.getType() ==
						AssetCategoryException.TOO_MANY_CATEGORIES) {

				errorMessage = _language.format(
					themeDisplay.getRequest(),
					"you-cannot-select-more-than-one-category-for-x",
					assetVocabularyTitle);
			}
		}
		else if (exception instanceof DataLimitExceededException) {
			errorMessage = _language.get(
				themeDisplay.getRequest(),
				"unable-to-exceed-maximum-number-of-allowed-sites");
		}
		else if (exception instanceof DuplicateGroupException) {
			errorMessage = _language.get(
				themeDisplay.getRequest(), "please-enter-a-unique-name");
		}
		else if (exception instanceof GroupInheritContentException) {
			errorMessage = _language.get(
				themeDisplay.getRequest(),
				"this-site-cannot-inherit-content-from-its-parent-site");
		}
		else if (exception instanceof GroupKeyException) {
			errorMessage = _handleGroupKeyException(actionRequest);
		}
		else if (exception instanceof GroupParentException.MustNotBeOwnParent) {
			errorMessage = _language.get(
				themeDisplay.getRequest(),
				"this-site-cannot-inherit-content-from-its-parent-site");
		}

		if (Validator.isNull(errorMessage)) {
			errorMessage = _language.get(
				themeDisplay.getRequest(), "an-unexpected-error-occurred");

			_log.error(exception);
		}

		JSONObject jsonObject = JSONUtil.put("error", errorMessage);

		JSONPortletResponseUtil.writeJSON(
			actionRequest, actionResponse, jsonObject);
	}

	private Group _updateGroup(ActionRequest actionRequest) throws Exception {
		ThemeDisplay themeDisplay = (ThemeDisplay)actionRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		long defaultParentGroupId = ParamUtil.getLong(
			actionRequest, "parentGroupId",
			GroupConstants.DEFAULT_PARENT_GROUP_ID);

		long parentGroupId = ParamUtil.getLong(
			actionRequest, "parentGroupSearchContainerPrimaryKeys",
			defaultParentGroupId);

		int membershipRestriction =
			GroupConstants.DEFAULT_MEMBERSHIP_RESTRICTION;

		boolean actionRequestMembershipRestriction = ParamUtil.getBoolean(
			actionRequest, "membershipRestriction");

		if (actionRequestMembershipRestriction &&
			(parentGroupId != GroupConstants.DEFAULT_PARENT_GROUP_ID)) {

			membershipRestriction =
				GroupConstants.MEMBERSHIP_RESTRICTION_TO_PARENT_SITE_MEMBERS;
		}

		ServiceContext serviceContext = ServiceContextFactory.getInstance(
			Group.class.getName(), actionRequest);

		ServiceContextThreadLocal.pushServiceContext(serviceContext);

		String name = ParamUtil.getString(actionRequest, "name");
		Map<Locale, String> nameMap = _localization.getLocalizationMap(
			actionRequest, "name");
		Map<Locale, String> descriptionMap = _localization.getLocalizationMap(
			actionRequest, "description");
		int type = ParamUtil.getInteger(
			actionRequest, "type", GroupConstants.TYPE_SITE_RESTRICTED);
		String friendlyURL = ParamUtil.getString(
			actionRequest, "groupFriendlyURL");
		boolean manualMembership = ParamUtil.getBoolean(
			actionRequest, "manualMembership", true);
		boolean inheritContent = ParamUtil.getBoolean(
			actionRequest, "inheritContent");
		boolean active = ParamUtil.getBoolean(actionRequest, "active", true);
		long userId = _portal.getUserId(actionRequest);

		if (Validator.isNotNull(name)) {
			nameMap.put(LocaleUtil.getDefault(), name);
		}

		Group liveGroup = _groupService.addGroup(
			StringPool.BLANK, parentGroupId,
			GroupConstants.DEFAULT_LIVE_GROUP_ID, nameMap, descriptionMap, type,
			null, manualMembership, membershipRestriction, friendlyURL, true,
			inheritContent, active, serviceContext);

		LiveUsers.joinGroup(
			themeDisplay.getCompanyId(), liveGroup.getGroupId(), userId);

		boolean openGraphEnabled = ParamUtil.getBoolean(
			actionRequest, "openGraphEnabled", true);
		Map<Locale, String> openGraphImageAltMap =
			_localization.getLocalizationMap(
				actionRequest, "openGraphImageAlt");
		long openGraphImageFileEntryId = ParamUtil.getLong(
			actionRequest, "openGraphImageFileEntryId");

		_layoutSEOSiteLocalService.updateLayoutSEOSite(
			_portal.getUserId(actionRequest), liveGroup.getGroupId(),
			openGraphEnabled, openGraphImageAltMap, openGraphImageFileEntryId,
			serviceContext);

		// Settings

		UnicodeProperties typeSettingsUnicodeProperties =
			liveGroup.getTypeSettingsProperties();

		String customJspServletContextName = ParamUtil.getString(
			actionRequest, "customJspServletContextName",
			typeSettingsUnicodeProperties.getProperty(
				"customJspServletContextName"));

		typeSettingsUnicodeProperties.setProperty(
			"customJspServletContextName", customJspServletContextName);

		typeSettingsUnicodeProperties.setProperty(
			"defaultSiteRoleIds",
			ListUtil.toString(
				ActionUtil.getRoleIds(actionRequest), StringPool.BLANK));
		typeSettingsUnicodeProperties.setProperty(
			"defaultTeamIds",
			ListUtil.toString(
				ActionUtil.getTeamIds(actionRequest), StringPool.BLANK));

		String[] analyticsTypes = PrefsPropsUtil.getStringArray(
			themeDisplay.getCompanyId(), PropsKeys.ADMIN_ANALYTICS_TYPES,
			StringPool.NEW_LINE);

		for (String analyticsType : analyticsTypes) {
			if (StringUtil.equalsIgnoreCase(analyticsType, "google")) {
				String googleAnalyticsCreateCustomConfiguration =
					ParamUtil.getString(
						actionRequest,
						"googleAnalyticsCreateCustomConfiguration",
						typeSettingsUnicodeProperties.getProperty(
							"googleAnalyticsCreateCustomConfiguration"));

				typeSettingsUnicodeProperties.setProperty(
					"googleAnalyticsCreateCustomConfiguration",
					googleAnalyticsCreateCustomConfiguration);

				String googleAnalyticsCustomConfiguration = ParamUtil.getString(
					actionRequest, "googleAnalyticsCustomConfiguration",
					typeSettingsUnicodeProperties.getProperty(
						"googleAnalyticsCustomConfiguration"));

				typeSettingsUnicodeProperties.setProperty(
					"googleAnalyticsCustomConfiguration",
					googleAnalyticsCustomConfiguration);

				String googleAnalyticsId = ParamUtil.getString(
					actionRequest, "googleAnalyticsId",
					typeSettingsUnicodeProperties.getProperty(
						"googleAnalyticsId"));

				typeSettingsUnicodeProperties.setProperty(
					"googleAnalyticsId", googleAnalyticsId);
			}
			else if (StringUtil.equalsIgnoreCase(
						analyticsType, "googleAnalytics4")) {

				String googleAnalytics4CustomConfiguration =
					ParamUtil.getString(
						actionRequest, "googleAnalytics4CustomConfiguration",
						typeSettingsUnicodeProperties.getProperty(
							"googleAnalytics4CustomConfiguration"));

				typeSettingsUnicodeProperties.setProperty(
					"googleAnalytics4CustomConfiguration",
					googleAnalytics4CustomConfiguration);

				String googleAnalytics4Id = ParamUtil.getString(
					actionRequest, "googleAnalytics4Id",
					typeSettingsUnicodeProperties.getProperty(
						"googleAnalytics4Id"));

				typeSettingsUnicodeProperties.setProperty(
					"googleAnalytics4Id", googleAnalytics4Id);
			}
			else {
				String analyticsScript = ParamUtil.getString(
					actionRequest, Sites.ANALYTICS_PREFIX + analyticsType,
					typeSettingsUnicodeProperties.getProperty(analyticsType));

				typeSettingsUnicodeProperties.setProperty(
					Sites.ANALYTICS_PREFIX + analyticsType, analyticsScript);
			}
		}

		boolean trashEnabled = ParamUtil.getBoolean(
			actionRequest, "trashEnabled",
			GetterUtil.getBoolean(
				typeSettingsUnicodeProperties.getProperty("trashEnabled"),
				true));

		typeSettingsUnicodeProperties.setProperty(
			"trashEnabled", String.valueOf(trashEnabled));

		int trashEntriesMaxAgeCompany = PrefsPropsUtil.getInteger(
			themeDisplay.getCompanyId(), PropsKeys.TRASH_ENTRIES_MAX_AGE);

		int trashEntriesMaxAgeGroup = ParamUtil.getInteger(
			actionRequest, "trashEntriesMaxAge");

		if (trashEntriesMaxAgeGroup <= 0) {
			trashEntriesMaxAgeGroup = GetterUtil.getInteger(
				typeSettingsUnicodeProperties.getProperty("trashEntriesMaxAge"),
				trashEntriesMaxAgeCompany);
		}

		if (trashEntriesMaxAgeGroup != trashEntriesMaxAgeCompany) {
			typeSettingsUnicodeProperties.setProperty(
				"trashEntriesMaxAge",
				String.valueOf(GetterUtil.getInteger(trashEntriesMaxAgeGroup)));
		}
		else {
			typeSettingsUnicodeProperties.remove("trashEntriesMaxAge");
		}

		int contentSharingWithChildrenEnabled = ParamUtil.getInteger(
			actionRequest, "contentSharingWithChildrenEnabled",
			GetterUtil.getInteger(
				typeSettingsUnicodeProperties.getProperty(
					"contentSharingWithChildrenEnabled"),
				Sites.CONTENT_SHARING_WITH_CHILDREN_DEFAULT_VALUE));

		typeSettingsUnicodeProperties.setProperty(
			"contentSharingWithChildrenEnabled",
			String.valueOf(contentSharingWithChildrenEnabled));

		UnicodeProperties formTypeSettingsUnicodeProperties =
			PropertiesParamUtil.getProperties(
				actionRequest, "TypeSettingsProperties--");

		boolean inheritLocales = GetterUtil.getBoolean(
			typeSettingsUnicodeProperties.getProperty("inheritLocales"));

		if (formTypeSettingsUnicodeProperties.containsKey("inheritLocales")) {
			inheritLocales = GetterUtil.getBoolean(
				formTypeSettingsUnicodeProperties.getProperty(
					"inheritLocales"));
		}

		if (inheritLocales) {
			formTypeSettingsUnicodeProperties.setProperty(
				PropsKeys.LOCALES,
				StringUtil.merge(
					LocaleUtil.toLanguageIds(_language.getAvailableLocales())));

			User user = themeDisplay.getGuestUser();

			formTypeSettingsUnicodeProperties.setProperty(
				"languageId", user.getLanguageId());
		}

		if (formTypeSettingsUnicodeProperties.containsKey(PropsKeys.LOCALES) &&
			Validator.isNull(
				formTypeSettingsUnicodeProperties.getProperty(
					PropsKeys.LOCALES))) {

			long liveGroupId = ParamUtil.getLong(actionRequest, "liveGroupId");

			throw new LocaleException(
				LocaleException.TYPE_DEFAULT,
				"Must have at least one valid locale for site " + liveGroupId);
		}

		typeSettingsUnicodeProperties.putAll(formTypeSettingsUnicodeProperties);

		UnicodeProperties ratingsTypeUnicodeProperties =
			PropertiesParamUtil.getProperties(actionRequest, "RatingsType--");

		for (String propertyKey : ratingsTypeUnicodeProperties.keySet()) {
			String newRatingsType = ratingsTypeUnicodeProperties.getProperty(
				propertyKey);

			String oldRatingsType = typeSettingsUnicodeProperties.getProperty(
				propertyKey);

			if (newRatingsType.equals(oldRatingsType)) {
				continue;
			}

			if (RatingsType.isValid(newRatingsType)) {
				typeSettingsUnicodeProperties.put(propertyKey, newRatingsType);
			}
			else {
				typeSettingsUnicodeProperties.remove(propertyKey);
			}
		}

		// Virtual hosts

		Set<Locale> availableLocales = _language.getAvailableLocales(
			liveGroup.getGroupId());

		_layoutSetService.updateVirtualHosts(
			liveGroup.getGroupId(), false,
			ActionUtil.toTreeMap(
				actionRequest, "publicVirtualHost", availableLocales));

		_layoutSetService.updateVirtualHosts(
			liveGroup.getGroupId(), true,
			ActionUtil.toTreeMap(
				actionRequest, "privateVirtualHost", availableLocales));

		// Staging

		if (liveGroup.hasStagingGroup()) {
			Group stagingGroup = liveGroup.getStagingGroup();

			friendlyURL = ParamUtil.getString(
				actionRequest, "stagingFriendlyURL",
				stagingGroup.getFriendlyURL());

			_groupService.updateFriendlyURL(
				stagingGroup.getGroupId(), friendlyURL);

			_layoutSetService.updateVirtualHosts(
				stagingGroup.getGroupId(), false,
				ActionUtil.toTreeMap(
					actionRequest, "stagingPublicVirtualHost",
					availableLocales));

			_layoutSetService.updateVirtualHosts(
				stagingGroup.getGroupId(), true,
				ActionUtil.toTreeMap(
					actionRequest, "stagingPrivateVirtualHost",
					availableLocales));

			UnicodeProperties stagedGroupTypeSettingsUnicodeProperties =
				stagingGroup.getTypeSettingsProperties();

			stagedGroupTypeSettingsUnicodeProperties.putAll(
				formTypeSettingsUnicodeProperties);

			_groupService.updateGroup(
				stagingGroup.getGroupId(),
				stagedGroupTypeSettingsUnicodeProperties.toString());
		}

		liveGroup = _groupService.updateGroup(
			liveGroup.getGroupId(), typeSettingsUnicodeProperties.toString());

		String creationType = ParamUtil.getString(
			actionRequest, "creationType");

		if (Validator.isNull(creationType) ||
			creationType.equals(
				SiteAdminConstants.CREATION_TYPE_SITE_TEMPLATE)) {

			_updateGroupFromSiteTemplate(actionRequest, liveGroup);
		}
		else if (creationType.equals(
					SiteAdminConstants.CREATION_TYPE_INITIALIZER)) {

			String siteInitializerKey = ParamUtil.getString(
				actionRequest, "siteInitializerKey");

			SiteInitializer siteInitializer =
				_siteInitializerRegistry.getSiteInitializer(siteInitializerKey);

			if (!liveGroup.isStaged() || liveGroup.isStagedRemotely()) {
				siteInitializer.initialize(liveGroup.getGroupId());
			}
			else {
				Group stagingGroup = liveGroup.getStagingGroup();

				siteInitializer.initialize(stagingGroup.getGroupId());
			}
		}

		themeDisplay.setSiteGroupId(liveGroup.getGroupId());

		return liveGroup;
	}

	private void _updateGroupFromSiteTemplate(
			ActionRequest actionRequest, Group group)
		throws Exception {

		ActionUtil.updateLayoutSetPrototypesLinks(actionRequest, group, _sites);

		long layoutSetPrototypeId = ParamUtil.getLong(
			actionRequest, "layoutSetPrototypeId");

		Group layoutSetPrototypeGroup =
			_groupLocalService.getLayoutSetPrototypeGroup(
				group.getCompanyId(), layoutSetPrototypeId);

		List<WorkflowDefinitionLink> workflowDefinitionLinks =
			_workflowDefinitionLinkLocalService.getWorkflowDefinitionLinks(
				group.getCompanyId(), layoutSetPrototypeGroup.getGroupId(), 0);

		for (WorkflowDefinitionLink workflowDefinitionLink :
				workflowDefinitionLinks) {

			_workflowDefinitionLinkLocalService.addWorkflowDefinitionLink(
				null, group.getCreatorUserId(), group.getCompanyId(),
				group.getGroupId(), workflowDefinitionLink.getClassName(),
				workflowDefinitionLink.getClassPK(),
				workflowDefinitionLink.getTypePK(),
				workflowDefinitionLink.getWorkflowDefinitionName(),
				workflowDefinitionLink.getWorkflowDefinitionVersion());
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		AddGroupMVCActionCommand.class);

	private static final TransactionConfig _transactionConfig =
		TransactionConfig.Factory.create(
			Propagation.REQUIRED, new Class<?>[] {Exception.class});

	@Reference
	private GroupLocalService _groupLocalService;

	@Reference
	private GroupService _groupService;

	@Reference
	private JSONFactory _jsonFactory;

	@Reference
	private Language _language;

	@Reference
	private LayoutSEOSiteLocalService _layoutSEOSiteLocalService;

	@Reference
	private LayoutSetService _layoutSetService;

	@Reference
	private Localization _localization;

	@Reference
	private Portal _portal;

	@Reference
	private SiteInitializerRegistry _siteInitializerRegistry;

	@Reference
	private Sites _sites;

	@Reference
	private WorkflowDefinitionLinkLocalService
		_workflowDefinitionLinkLocalService;

	private class GroupCallable implements Callable<Group> {

		@Override
		public Group call() throws Exception {
			try {
				return _updateGroup(_actionRequest);
			}
			catch (Exception exception) {

				// LPS-169057

				PermissionCacheUtil.clearCache(
					_portal.getUserId(_actionRequest));

				throw exception;
			}
		}

		private GroupCallable(ActionRequest actionRequest) {
			_actionRequest = actionRequest;
		}

		private final ActionRequest _actionRequest;

	}

}