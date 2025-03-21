/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.social.kernel.model;

import com.liferay.asset.kernel.AssetRendererFactoryRegistryUtil;
import com.liferay.asset.kernel.model.AssetRenderer;
import com.liferay.asset.kernel.model.AssetRendererFactory;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONException;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.module.service.Snapshot;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.resource.bundle.ResourceBundleLoader;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.service.GroupLocalServiceUtil;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.UserLocalServiceUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.trash.TrashHandler;
import com.liferay.portal.kernel.trash.TrashHandlerRegistryUtil;
import com.liferay.portal.kernel.trash.helper.TrashHelper;
import com.liferay.portal.kernel.util.HtmlUtil;
import com.liferay.portal.kernel.util.HttpComponentsUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.ResourceBundleUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.social.kernel.service.SocialActivityLocalServiceUtil;
import com.liferay.social.kernel.service.SocialActivitySetLocalServiceUtil;
import com.liferay.social.kernel.service.persistence.SocialActivityUtil;

import jakarta.portlet.PortletURL;
import jakarta.portlet.WindowState;

import java.util.List;
import java.util.ResourceBundle;

/**
 * @author Brian Wing Shun Chan
 * @author Ryan Park
 */
public abstract class BaseSocialActivityInterpreter
	implements SocialActivityInterpreter {

	@Override
	public String getSelector() {
		return StringPool.BLANK;
	}

	@Override
	public boolean hasPermission(
			PermissionChecker permissionChecker, SocialActivity activity,
			String actionId, ServiceContext serviceContext)
		throws Exception {

		return hasPermissions(
			permissionChecker, activity, actionId, serviceContext);
	}

	@Override
	public SocialActivityFeedEntry interpret(
		SocialActivity activity, ServiceContext serviceContext) {

		try {
			return doInterpret(activity, serviceContext);
		}
		catch (Exception exception) {
			_log.error("Unable to interpret activity", exception);
		}

		return null;
	}

	@Override
	public SocialActivityFeedEntry interpret(
		SocialActivitySet activitySet, ServiceContext serviceContext) {

		try {
			return doInterpret(activitySet, serviceContext);
		}
		catch (Exception exception) {
			_log.error("Unable to interpret activity set", exception);
		}

		return null;
	}

	@Override
	public void updateActivitySet(long activityId) throws PortalException {
		SocialActivity activity = SocialActivityUtil.fetchByPrimaryKey(
			activityId);

		if ((activity == null) || (activity.getActivitySetId() > 0)) {
			return;
		}

		long activitySetId = getActivitySetId(activityId);

		if (activitySetId > 0) {
			SocialActivitySetLocalServiceUtil.incrementActivityCount(
				activitySetId, activityId);
		}
		else {
			SocialActivitySetLocalServiceUtil.addActivitySet(activityId);
		}
	}

	protected ResourceBundleLoader acquireResourceBundleLoader() {
		return locale -> ResourceBundleUtil.getBundle(locale, getClass());
	}

	protected String addNoSuchEntryRedirect(
			String url, String className, long classPK,
			ServiceContext serviceContext)
		throws Exception {

		String viewEntryURL = getViewEntryURL(
			className, classPK, serviceContext);

		if (Validator.isNull(viewEntryURL)) {
			return url;
		}

		return HttpComponentsUtil.setParameter(
			url, "noSuchEntryRedirect", viewEntryURL);
	}

	protected String buildLink(String link, String text) {
		return StringBundler.concat(
			"<a class=\"text-decoration-underline\" href=\"", link, "\">", text,
			"</a>");
	}

	protected SocialActivityFeedEntry doInterpret(
			SocialActivity activity, ServiceContext serviceContext)
		throws Exception {

		ThemeDisplay themeDisplay = serviceContext.getThemeDisplay();

		if (!hasPermissions(
				themeDisplay.getPermissionChecker(), activity, ActionKeys.VIEW,
				serviceContext)) {

			return null;
		}

		String link = getLink(activity, serviceContext);

		String title = getTitle(activity, serviceContext);

		if (Validator.isNull(title)) {
			return null;
		}

		return new SocialActivityFeedEntry(
			link, title, getBody(activity, serviceContext));
	}

	protected SocialActivityFeedEntry doInterpret(
			SocialActivitySet activitySet, ServiceContext serviceContext)
		throws Exception {

		List<SocialActivity> activities =
			SocialActivityLocalServiceUtil.getActivitySetActivities(
				activitySet.getActivitySetId(), 0, 1);

		if (!activities.isEmpty()) {
			SocialActivity activity = activities.get(0);

			return doInterpret(activity, serviceContext);
		}

		return null;
	}

	protected long getActivitySetId(long activityId) {
		return 0;
	}

	protected String getBody(
			SocialActivity activity, ServiceContext serviceContext)
		throws Exception {

		return StringPool.BLANK;
	}

	protected String getEntryTitle(
			SocialActivity activity, ServiceContext serviceContext)
		throws Exception {

		return activity.getExtraDataValue("title", serviceContext.getLocale());
	}

	protected String getGroupName(long groupId, ServiceContext serviceContext) {
		try {
			if (groupId <= 0) {
				return StringPool.BLANK;
			}

			Group group = GroupLocalServiceUtil.getGroup(groupId);

			String groupName = group.getDescriptiveName();

			if (group.getGroupId() == serviceContext.getScopeGroupId()) {
				return HtmlUtil.escape(groupName);
			}

			String groupDisplayURL = StringPool.BLANK;

			if (group.hasPublicLayouts()) {
				groupDisplayURL = group.getDisplayURL(
					serviceContext.getThemeDisplay(), false);
			}
			else if (group.hasPrivateLayouts()) {
				groupDisplayURL = group.getDisplayURL(
					serviceContext.getThemeDisplay(), true);
			}
			else {
				return HtmlUtil.escape(groupName);
			}

			return StringBundler.concat(
				"<a class=\"group text-decoration-underline\" href=\"",
				groupDisplayURL, "\">", HtmlUtil.escape(groupName), "</a>");
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(exception);
			}

			return StringPool.BLANK;
		}
	}

	protected String getJSONValue(String json, String key) {
		return getJSONValue(json, key, StringPool.BLANK);
	}

	protected String getJSONValue(
		String json, String key, String defaultValue) {

		if (Validator.isNull(json)) {
			return defaultValue;
		}

		try {
			JSONObject extraDataJSONObject = JSONFactoryUtil.createJSONObject(
				json);

			String value = extraDataJSONObject.getString(key);

			if (Validator.isNotNull(value)) {
				return value;
			}
		}
		catch (JSONException jsonException) {
			_log.error(
				"Unable to create a JSON object from " + json, jsonException);
		}

		return defaultValue;
	}

	protected String getLink(
			SocialActivity activity, ServiceContext serviceContext)
		throws Exception {

		try {
			String className = activity.getClassName();
			long classPK = activity.getClassPK();

			String viewEntryInTrashURL = getViewEntryInTrashURL(
				className, classPK, serviceContext);

			if (viewEntryInTrashURL != null) {
				return viewEntryInTrashURL;
			}

			String path = getPath(activity, serviceContext);

			if (Validator.isNull(path)) {
				return null;
			}

			path = addNoSuchEntryRedirect(
				path, className, classPK, serviceContext);

			if (!path.startsWith(StringPool.SLASH)) {
				return path;
			}

			StringBundler sb = new StringBundler(4);

			sb.append(serviceContext.getPortalURL());

			if (!path.startsWith(PortalUtil.getPathContext())) {
				sb.append(PortalUtil.getPathContext());
			}

			if (!path.startsWith(serviceContext.getPathMain())) {
				sb.append(serviceContext.getPathMain());
			}

			sb.append(path);

			return sb.toString();
		}
		catch (PortalException portalException) {
			_log.error(portalException);

			return null;
		}
	}

	protected String getPath(
			SocialActivity activity, ServiceContext serviceContext)
		throws Exception {

		return StringPool.BLANK;
	}

	protected String getTitle(
			SocialActivity activity, ServiceContext serviceContext)
		throws Exception {

		String groupName = StringPool.BLANK;

		if (activity.getGroupId() != serviceContext.getScopeGroupId()) {
			groupName = getGroupName(activity.getGroupId(), serviceContext);
		}

		String titlePattern = getTitlePattern(groupName, activity);

		if (Validator.isNull(titlePattern)) {
			return null;
		}

		Object[] titleArguments = getTitleArguments(
			groupName, activity, getLink(activity, serviceContext),
			getEntryTitle(activity, serviceContext), serviceContext);

		ResourceBundleLoader resourceBundleLoader =
			acquireResourceBundleLoader();

		if (resourceBundleLoader == null) {
			return serviceContext.translate(titlePattern, titleArguments);
		}

		ResourceBundle resourceBundle = resourceBundleLoader.loadResourceBundle(
			serviceContext.getLocale());

		return LanguageUtil.format(
			resourceBundle, titlePattern, titleArguments);
	}

	protected Object[] getTitleArguments(
			String groupName, SocialActivity activity, String link,
			String title, ServiceContext serviceContext)
		throws Exception {

		return new Object[] {
			groupName, getUserName(activity.getUserId(), serviceContext),
			wrapLink(link, title)
		};
	}

	protected String getTitlePattern(String groupName, SocialActivity activity)
		throws Exception {

		return StringPool.BLANK;
	}

	protected String getUserName(long userId, ServiceContext serviceContext) {
		try {
			if (userId <= 0) {
				return StringPool.BLANK;
			}

			User user = UserLocalServiceUtil.getUserById(userId);

			if (user.getUserId() == serviceContext.getUserId()) {
				return HtmlUtil.escape(user.getFirstName());
			}

			String userName = user.getFullName();

			Group group = user.getGroup();

			if (group.getGroupId() == serviceContext.getScopeGroupId()) {
				return HtmlUtil.escape(userName);
			}

			String userDisplayURL = user.getDisplayURL(
				serviceContext.getThemeDisplay());

			return StringBundler.concat(
				"<a class=\"user\" href=\"", userDisplayURL, "\">",
				HtmlUtil.escape(userName), "</a>");
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(exception);
			}

			return StringPool.BLANK;
		}
	}

	protected String getViewEntryInTrashURL(
			String className, long classPK, ServiceContext serviceContext)
		throws Exception {

		TrashHandler trashHandler = TrashHandlerRegistryUtil.getTrashHandler(
			className);

		if ((trashHandler != null) && trashHandler.isInTrash(classPK)) {
			TrashHelper trashHelper = _trashHelperSnapshot.get();

			PortletURL portletURL = trashHelper.getViewContentURL(
				serviceContext.getRequest(), className, classPK);

			if (portletURL == null) {
				return null;
			}

			return portletURL.toString();
		}

		return null;
	}

	protected String getViewEntryURL(
			String className, long classPK, ServiceContext serviceContext)
		throws Exception {

		AssetRendererFactory<?> assetRendererFactory =
			AssetRendererFactoryRegistryUtil.getAssetRendererFactoryByClassName(
				className);

		if (assetRendererFactory == null) {
			return null;
		}

		LiferayPortletResponse liferayPortletResponse =
			serviceContext.getLiferayPortletResponse();

		if (liferayPortletResponse == null) {
			return null;
		}

		if (classPK == 0) {
			PortletURL portletURL = assetRendererFactory.getURLView(
				liferayPortletResponse, WindowState.MAXIMIZED);

			return portletURL.toString();
		}

		AssetRenderer<?> assetRenderer = assetRendererFactory.getAssetRenderer(
			classPK);

		if (assetRenderer == null) {
			return null;
		}

		return assetRenderer.getURLView(
			liferayPortletResponse, WindowState.MAXIMIZED);
	}

	protected boolean hasPermissions(
			PermissionChecker permissionChecker, SocialActivity activity,
			String actionId, ServiceContext serviceContext)
		throws Exception {

		return false;
	}

	protected String wrapLink(String link, String title) {
		title = HtmlUtil.escape(title);

		if (link == null) {
			return title;
		}

		return buildLink(link, title);
	}

	protected String wrapLink(
		String link, String key, ServiceContext serviceContext) {

		ResourceBundleLoader resourceBundleLoader =
			acquireResourceBundleLoader();

		ResourceBundle resourceBundle = resourceBundleLoader.loadResourceBundle(
			serviceContext.getLocale());

		String title = LanguageUtil.get(resourceBundle, key);

		return wrapLink(link, title);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		BaseSocialActivityInterpreter.class);

	private static final Snapshot<TrashHelper> _trashHelperSnapshot =
		new Snapshot<>(BaseSocialActivityInterpreter.class, TrashHelper.class);

}