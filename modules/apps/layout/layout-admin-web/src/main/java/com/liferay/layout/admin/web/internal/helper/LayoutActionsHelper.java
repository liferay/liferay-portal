/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.admin.web.internal.helper;

import com.liferay.exportimport.kernel.staging.StagingUtil;
import com.liferay.layout.util.template.LayoutConverter;
import com.liferay.layout.util.template.LayoutConverterRegistry;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.LayoutConstants;
import com.liferay.portal.kernel.model.LayoutType;
import com.liferay.portal.kernel.model.LayoutTypePortlet;
import com.liferay.portal.kernel.model.Portlet;
import com.liferay.portal.kernel.model.PortletPreferences;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.service.LayoutLocalServiceUtil;
import com.liferay.portal.kernel.service.PortletLocalServiceUtil;
import com.liferay.portal.kernel.service.PortletPreferencesLocalServiceUtil;
import com.liferay.portal.kernel.service.permission.GroupPermissionUtil;
import com.liferay.portal.kernel.service.permission.LayoutPermissionUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.PortletKeys;
import com.liferay.staging.StagingGroupHelper;
import com.liferay.staging.StagingGroupHelperUtil;
import com.liferay.translation.constants.TranslationActionKeys;
import com.liferay.translation.security.permission.TranslationPermission;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;

/**
 * @author Eudaldo Alonso
 */
public class LayoutActionsHelper {

	public LayoutActionsHelper(
		LayoutConverterRegistry layoutConverterRegistry,
		ThemeDisplay themeDisplay,
		TranslationPermission translationPermission) {

		_layoutConverterRegistry = layoutConverterRegistry;
		_themeDisplay = themeDisplay;
		_translationPermission = translationPermission;
	}

	public boolean isShowAddChildPageAction(Layout layout)
		throws PortalException {

		if (layout == null) {
			return true;
		}

		return LayoutPermissionUtil.contains(
			_themeDisplay.getPermissionChecker(), layout,
			ActionKeys.ADD_LAYOUT);
	}

	public boolean isShowAddRootLayoutButton(Group selGroup)
		throws PortalException {

		return GroupPermissionUtil.contains(
			_themeDisplay.getPermissionChecker(), selGroup,
			ActionKeys.ADD_LAYOUT);
	}

	public boolean isShowConfigureAction(Layout layout) throws PortalException {
		if (layout.isTypeEmpty()) {
			return false;
		}

		return LayoutPermissionUtil.containsLayoutRestrictedUpdatePermission(
			_themeDisplay.getPermissionChecker(), layout);
	}

	public boolean isShowConvertLayoutAction(Layout layout)
		throws PortalException {

		if (!LayoutPermissionUtil.containsLayoutUpdatePermission(
				_themeDisplay.getPermissionChecker(), layout) ||
			_isLiveGroup() ||
			!Objects.equals(layout.getType(), LayoutConstants.TYPE_PORTLET)) {

			return false;
		}

		LayoutType layoutType = layout.getLayoutType();

		if (!(layoutType instanceof LayoutTypePortlet)) {
			return false;
		}

		LayoutTypePortlet layoutTypePortlet = (LayoutTypePortlet)layoutType;

		LayoutConverter layoutConverter =
			_layoutConverterRegistry.getLayoutConverter(
				layoutTypePortlet.getLayoutTemplateId());

		if ((layoutConverter == null) ||
			!layoutConverter.isConvertible(layout)) {

			return false;
		}

		return true;
	}

	public boolean isShowCopyLayoutAction(Layout layout, Group selGroup)
		throws PortalException {

		if (!isShowAddRootLayoutButton(selGroup) || !layout.isTypePortlet()) {
			return false;
		}

		return layout.isPublished();
	}

	public boolean isShowDeleteAction(Layout layout) throws PortalException {
		if (StagingUtil.isIncomplete(layout) ||
			!LayoutPermissionUtil.contains(
				_themeDisplay.getPermissionChecker(), layout,
				ActionKeys.DELETE)) {

			return false;
		}

		Group group = layout.getGroup();

		int layoutsCount = LayoutLocalServiceUtil.getLayoutsCount(
			group, false, LayoutConstants.DEFAULT_PARENT_LAYOUT_ID);

		if (group.isGuest() && !layout.isPrivateLayout() &&
			layout.isRootLayout() && (layoutsCount == 1)) {

			return false;
		}

		return true;
	}

	public boolean isShowDiscardDraftActions(Layout layout)
		throws PortalException {

		if (!layout.isTypeContent() ||
			!LayoutPermissionUtil.contains(
				_themeDisplay.getPermissionChecker(), layout,
				ActionKeys.UPDATE)) {

			return false;
		}

		Layout draftLayout = layout.fetchDraftLayout();

		if (draftLayout == null) {
			return false;
		}

		return draftLayout.isDraft();
	}

	public boolean isShowExportTranslationAction(Layout layout) {
		if (layout.isTypeContent() && !_isSingleLanguageSite(layout)) {
			return true;
		}

		return false;
	}

	public boolean isShowImportTranslationAction(Layout layout) {
		try {
			if (layout.isTypeContent() && !_isSingleLanguageSite(layout) &&
				LayoutPermissionUtil.contains(
					_themeDisplay.getPermissionChecker(), layout,
					ActionKeys.UPDATE)) {

				return true;
			}

			return false;
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(exception);
			}

			return false;
		}
	}

	public boolean isShowOrphanPortletsAction(Layout layout, Group selGroup)
		throws PortalException {

		if (StagingUtil.isIncomplete(layout) ||
			!layout.isSupportsEmbeddedPortlets() ||
			!isShowAddRootLayoutButton(selGroup) ||
			!_hasOrphanPortlets(layout)) {

			return false;
		}

		return true;
	}

	public boolean isShowPermissionsAction(Layout layout, Group selGroup)
		throws PortalException {

		if (StagingUtil.isIncomplete(layout) || layout.isTypeEmpty() ||
			selGroup.isLayoutPrototype()) {

			return false;
		}

		return LayoutPermissionUtil.contains(
			_themeDisplay.getPermissionChecker(), layout,
			ActionKeys.PERMISSIONS);
	}

	public boolean isShowPreviewDraftActions(Layout layout)
		throws PortalException {

		if (!LayoutPermissionUtil.containsLayoutPreviewDraftPermission(
				_themeDisplay.getPermissionChecker(), layout)) {

			return false;
		}

		Layout draftLayout = layout.fetchDraftLayout();

		if (draftLayout == null) {
			return false;
		}

		if (draftLayout.isDraft() || !layout.isPublished()) {
			return true;
		}

		return false;
	}

	public boolean isShowTranslateAction(Layout layout) {
		if (layout.isTypeContent() && _hasTranslatePermission() &&
			!_isSingleLanguageSite(layout)) {

			return true;
		}

		return false;
	}

	public boolean isShowViewLayoutAction(Layout layout) {
		if (layout.isDenied() || layout.isPending() || layout.isPublished()) {
			return true;
		}

		return false;
	}

	private boolean _hasOrphanPortlets(Layout layout) {
		if (!layout.isSupportsEmbeddedPortlets()) {
			return false;
		}

		LayoutTypePortlet selLayoutTypePortlet =
			(LayoutTypePortlet)layout.getLayoutType();

		List<Portlet> explicitlyAddedPortlets =
			selLayoutTypePortlet.getExplicitlyAddedPortlets();

		List<String> explicitlyAddedPortletIds = new ArrayList<>();

		for (Portlet explicitlyAddedPortlet : explicitlyAddedPortlets) {
			explicitlyAddedPortletIds.add(
				explicitlyAddedPortlet.getPortletId());
		}

		List<PortletPreferences> portletPreferencesList =
			PortletPreferencesLocalServiceUtil.getPortletPreferences(
				PortletKeys.PREFS_OWNER_ID_DEFAULT,
				PortletKeys.PREFS_OWNER_TYPE_LAYOUT, layout.getPlid());

		for (PortletPreferences portletPreferences : portletPreferencesList) {
			String portletId = portletPreferences.getPortletId();

			Portlet portlet = PortletLocalServiceUtil.getPortletById(
				layout.getCompanyId(), portletId);

			if (portlet.isSystem() ||
				explicitlyAddedPortletIds.contains(portletId)) {

				continue;
			}

			return true;
		}

		return false;
	}

	private boolean _hasTranslatePermission() {
		long scopeGroupId = _themeDisplay.getScopeGroupId();

		for (Locale locale : LanguageUtil.getAvailableLocales(scopeGroupId)) {
			if (_translationPermission.contains(
					_themeDisplay.getPermissionChecker(), scopeGroupId,
					LanguageUtil.getLanguageId(locale),
					TranslationActionKeys.TRANSLATE)) {

				return true;
			}
		}

		return false;
	}

	private boolean _isLiveGroup() {
		StagingGroupHelper stagingGroupHelper =
			StagingGroupHelperUtil.getStagingGroupHelper();

		Group group = _themeDisplay.getScopeGroup();

		if (stagingGroupHelper.isLocalLiveGroup(group) ||
			stagingGroupHelper.isRemoteLiveGroup(group)) {

			return true;
		}

		return false;
	}

	private boolean _isSingleLanguageSite(Layout layout) {
		Set<Locale> availableLocales = LanguageUtil.getAvailableLocales(
			layout.getGroupId());

		if (availableLocales.size() == 1) {
			return true;
		}

		return false;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		LayoutActionsHelper.class);

	private final LayoutConverterRegistry _layoutConverterRegistry;
	private final ThemeDisplay _themeDisplay;
	private final TranslationPermission _translationPermission;

}