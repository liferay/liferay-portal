/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.admin.web.internal.portlet.action;

import com.liferay.asset.kernel.model.AssetEntry;
import com.liferay.asset.kernel.service.AssetEntryLocalServiceUtil;
import com.liferay.asset.link.model.AssetLink;
import com.liferay.asset.link.service.AssetLinkLocalServiceUtil;
import com.liferay.portal.kernel.exception.AvailableLocaleException;
import com.liferay.portal.kernel.exception.NoSuchLayoutException;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.LayoutSet;
import com.liferay.portal.kernel.service.LayoutLocalServiceUtil;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextFactory;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.sites.kernel.util.Sites;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.PortletRequest;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.TreeMap;

/**
 * @author Jürgen Kappler
 */
public class ActionUtil {

	public static long getRefererGroupId(ThemeDisplay themeDisplay)
		throws Exception {

		long refererGroupId = 0;

		try {
			Layout refererLayout = LayoutLocalServiceUtil.getLayout(
				themeDisplay.getRefererPlid());

			refererGroupId = refererLayout.getGroupId();
		}
		catch (NoSuchLayoutException noSuchLayoutException) {

			// LPS-52675

			if (_log.isDebugEnabled()) {
				_log.debug(noSuchLayoutException);
			}
		}

		return refererGroupId;
	}

	public static List<Long> getRoleIds(PortletRequest portletRequest) {
		List<Long> roleIds = new ArrayList<>();

		long[] siteRolesRoleIds = ArrayUtil.unique(
			ParamUtil.getLongValues(
				portletRequest, "siteRolesSearchContainerPrimaryKeys"));

		for (long siteRolesRoleId : siteRolesRoleIds) {
			if (siteRolesRoleId == 0) {
				continue;
			}

			roleIds.add(siteRolesRoleId);
		}

		return roleIds;
	}

	public static ServiceContext getServiceContext(
			ActionRequest actionRequest, long groupId)
		throws PortalException {

		ServiceContext serviceContext = ServiceContextFactory.getInstance(
			Group.class.getName(), actionRequest);

		AssetEntry assetEntry = AssetEntryLocalServiceUtil.fetchEntry(
			Group.class.getName(), groupId);

		if (assetEntry == null) {
			return serviceContext;
		}

		serviceContext.setAssetCategoryIds(assetEntry.getCategoryIds());
		serviceContext.setAssetLinkEntryIds(
			ListUtil.toLongArray(
				AssetLinkLocalServiceUtil.getDirectLinks(
					assetEntry.getEntryId()),
				AssetLink.ENTRY_ID2_ACCESSOR));
		serviceContext.setAssetPriority(assetEntry.getPriority());
		serviceContext.setAssetTagNames(assetEntry.getTagNames());

		return serviceContext;
	}

	public static List<Long> getTeamIds(PortletRequest portletRequest) {
		List<Long> teamIds = new ArrayList<>();

		long[] teamsTeamIds = ArrayUtil.unique(
			ParamUtil.getLongValues(
				portletRequest, "teamsSearchContainerPrimaryKeys"));

		for (long teamsTeamId : teamsTeamIds) {
			if (teamsTeamId == 0) {
				continue;
			}

			teamIds.add(teamsTeamId);
		}

		return teamIds;
	}

	public static TreeMap<String, String> toTreeMap(
			ActionRequest actionRequest, String parameterPrefix,
			Set<Locale> availableLocales)
		throws AvailableLocaleException {

		TreeMap<String, String> treeMap = new TreeMap<>();

		String[] virtualHostnames = ParamUtil.getStringValues(
			actionRequest, parameterPrefix + "name[]");
		String[] virtualHostLanguageIds = ParamUtil.getStringValues(
			actionRequest, parameterPrefix + "LanguageId[]");

		for (int i = 0; i < virtualHostnames.length; i++) {
			String virtualHostname = virtualHostnames[i];

			String virtualHostLanguageId = (String)ArrayUtil.getValue(
				virtualHostLanguageIds, i);

			if (Validator.isNotNull(virtualHostLanguageId)) {
				Locale locale = LocaleUtil.fromLanguageId(
					virtualHostLanguageId);

				if (!availableLocales.contains(locale)) {
					throw new AvailableLocaleException(virtualHostLanguageId);
				}
			}

			treeMap.put(virtualHostname, virtualHostLanguageId);
		}

		return treeMap;
	}

	public static void updateLayoutSetPrototypesLinks(
			ActionRequest actionRequest, Group liveGroup, Sites sites)
		throws Exception {

		long privateLayoutSetPrototypeId = ParamUtil.getLong(
			actionRequest, "privateLayoutSetPrototypeId");
		long publicLayoutSetPrototypeId = ParamUtil.getLong(
			actionRequest, "publicLayoutSetPrototypeId");
		boolean privateLayoutSetPrototypeLinkEnabled = ParamUtil.getBoolean(
			actionRequest, "privateLayoutSetPrototypeLinkEnabled");
		boolean publicLayoutSetPrototypeLinkEnabled = ParamUtil.getBoolean(
			actionRequest, "publicLayoutSetPrototypeLinkEnabled");

		if ((privateLayoutSetPrototypeId == 0) &&
			(publicLayoutSetPrototypeId == 0) &&
			!privateLayoutSetPrototypeLinkEnabled &&
			!publicLayoutSetPrototypeLinkEnabled) {

			long layoutSetPrototypeId = ParamUtil.getLong(
				actionRequest, "layoutSetPrototypeId");
			int layoutSetVisibility = ParamUtil.getInteger(
				actionRequest, "layoutSetVisibility");
			boolean layoutSetPrototypeLinkEnabled = ParamUtil.getBoolean(
				actionRequest, "layoutSetPrototypeLinkEnabled",
				layoutSetPrototypeId > 0);
			boolean layoutSetVisibilityPrivate = ParamUtil.getBoolean(
				actionRequest, "layoutSetVisibilityPrivate");

			if ((layoutSetVisibility == _LAYOUT_SET_VISIBILITY_PRIVATE) ||
				layoutSetVisibilityPrivate) {

				privateLayoutSetPrototypeId = layoutSetPrototypeId;
				privateLayoutSetPrototypeLinkEnabled =
					layoutSetPrototypeLinkEnabled;
			}
			else {
				publicLayoutSetPrototypeId = layoutSetPrototypeId;
				publicLayoutSetPrototypeLinkEnabled =
					layoutSetPrototypeLinkEnabled;
			}
		}

		LayoutSet privateLayoutSet = liveGroup.getPrivateLayoutSet();
		LayoutSet publicLayoutSet = liveGroup.getPublicLayoutSet();

		if ((privateLayoutSetPrototypeId ==
				privateLayoutSet.getLayoutSetPrototypeId()) &&
			(privateLayoutSetPrototypeLinkEnabled ==
				privateLayoutSet.isLayoutSetPrototypeLinkEnabled()) &&
			(publicLayoutSetPrototypeId ==
				publicLayoutSet.getLayoutSetPrototypeId()) &&
			(publicLayoutSetPrototypeLinkEnabled ==
				publicLayoutSet.isLayoutSetPrototypeLinkEnabled())) {

			return;
		}

		Group group = liveGroup.getStagingGroup();

		if (!liveGroup.isStaged() || liveGroup.isStagedRemotely()) {
			group = liveGroup;
		}

		sites.updateLayoutSetPrototypesLinks(
			group, publicLayoutSetPrototypeId, privateLayoutSetPrototypeId,
			publicLayoutSetPrototypeLinkEnabled,
			privateLayoutSetPrototypeLinkEnabled);
	}

	private static final int _LAYOUT_SET_VISIBILITY_PRIVATE = 1;

	private static final Log _log = LogFactoryUtil.getLog(ActionUtil.class);

}