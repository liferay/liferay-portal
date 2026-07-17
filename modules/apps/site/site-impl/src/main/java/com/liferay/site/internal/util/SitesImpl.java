/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.internal.util;

import com.liferay.asset.kernel.model.AssetEntry;
import com.liferay.asset.kernel.service.AssetEntryLocalService;
import com.liferay.change.tracking.configuration.helper.CTSettingsConfigurationHelper;
import com.liferay.exportimport.kernel.configuration.ExportImportConfigurationSettingsMapFactoryUtil;
import com.liferay.exportimport.kernel.configuration.constants.ExportImportConfigurationConstants;
import com.liferay.exportimport.kernel.lar.ExportImportHelper;
import com.liferay.exportimport.kernel.lar.ExportImportThreadLocal;
import com.liferay.exportimport.kernel.lar.PortletDataHandlerKeys;
import com.liferay.exportimport.kernel.lar.UserIdStrategy;
import com.liferay.exportimport.kernel.model.ExportImportConfiguration;
import com.liferay.exportimport.kernel.service.ExportImportConfigurationLocalService;
import com.liferay.exportimport.kernel.service.ExportImportLocalService;
import com.liferay.exportimport.kernel.staging.MergeLayoutPrototypesThreadLocal;
import com.liferay.layout.page.template.model.LayoutPageTemplateEntry;
import com.liferay.layout.page.template.service.LayoutPageTemplateEntryLocalService;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.change.tracking.CTTransactionException;
import com.liferay.portal.kernel.dao.orm.EntityCacheUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.lock.Lock;
import com.liferay.portal.kernel.lock.LockManagerUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.GroupConstants;
import com.liferay.portal.kernel.model.Image;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.LayoutPrototype;
import com.liferay.portal.kernel.model.LayoutSet;
import com.liferay.portal.kernel.model.LayoutSetPrototype;
import com.liferay.portal.kernel.model.LayoutTypePortlet;
import com.liferay.portal.kernel.model.Portlet;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.portlet.PortletIdCodec;
import com.liferay.portal.kernel.portlet.PortletPreferencesFactoryUtil;
import com.liferay.portal.kernel.security.permission.ResourceActionsUtil;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.ImageLocalService;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.service.LayoutPrototypeLocalService;
import com.liferay.portal.kernel.service.LayoutSetLocalService;
import com.liferay.portal.kernel.service.LayoutSetPrototypeLocalService;
import com.liferay.portal.kernel.service.LayoutSetService;
import com.liferay.portal.kernel.service.PortletLocalService;
import com.liferay.portal.kernel.service.PortletPreferencesLocalService;
import com.liferay.portal.kernel.service.ResourcePermissionLocalService;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.service.permission.PortletPermissionUtil;
import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.LinkedHashMapBuilder;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.LocaleThreadLocal;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.PortletKeys;
import com.liferay.portal.kernel.util.PropsValues;
import com.liferay.portal.kernel.util.ScopeUtil;
import com.liferay.portal.kernel.util.SystemProperties;
import com.liferay.portal.kernel.util.UnicodeProperties;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.uuid.PortalUUIDUtil;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portlet.PortletPreferencesImpl;
import com.liferay.site.internal.exportimport.internal.notifications.LayoutSetPrototypeNotificationUtil;
import com.liferay.sites.kernel.util.Sites;

import jakarta.portlet.PortletPreferences;

import java.io.File;
import java.io.Serializable;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Raymond Augé
 * @author Ryan Park
 * @author Zsolt Berentey
 */
@Component(service = Sites.class)
public class SitesImpl implements Sites {

	@Override
	public void applyLayoutPrototype(
			LayoutPrototype layoutPrototype, Layout targetLayout,
			boolean linkEnabled)
		throws Exception {

		Locale siteDefaultLocale = LocaleThreadLocal.getSiteDefaultLocale();

		LayoutTypePortlet targetLayoutType =
			(LayoutTypePortlet)targetLayout.getLayoutType();

		List<String> targetLayoutPortletIds = targetLayoutType.getPortletIds();

		Layout layoutPrototypeLayout = layoutPrototype.getLayout();

		byte[] iconBytes = null;

		if (layoutPrototypeLayout.isIconImage()) {
			Image image = _imageLocalService.getImage(
				layoutPrototypeLayout.getIconImageId());

			iconBytes = image.getTextObj();
		}

		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		long[] originalAssetCategoryIds = serviceContext.getAssetCategoryIds();
		String[] originalAssetTagNames = serviceContext.getAssetTagNames();
		Serializable originalPortletLayoutPageTemplateEntryERC =
			serviceContext.getAttribute("portletLayoutPageTemplateEntryERC");
		Serializable originalPortletLayoutPageTemplateEntryLinkEnabled =
			serviceContext.getAttribute(
				"portletLayoutPageTemplateEntryLinkEnabled");
		Serializable originalPortletLayoutPageTemplateEntryScopeERC =
			serviceContext.getAttribute(
				"portletLayoutPageTemplateEntryScopeERC");

		try {
			AssetEntry assetEntry = _assetEntryLocalService.fetchEntry(
				Layout.class.getName(), layoutPrototypeLayout.getPlid());

			serviceContext.setAssetCategoryIds(assetEntry.getCategoryIds());
			serviceContext.setAssetTagNames(assetEntry.getTagNames());

			LayoutPageTemplateEntry layoutPageTemplateEntry =
				_layoutPageTemplateEntryLocalService.
					getFirstLayoutPageTemplateEntry(
						layoutPrototype.getLayoutPrototypeId());

			serviceContext.setAttribute(
				"portletLayoutPageTemplateEntryERC",
				layoutPageTemplateEntry.getExternalReferenceCode());

			serviceContext.setAttribute(
				"portletLayoutPageTemplateEntryLinkEnabled", linkEnabled);
			serviceContext.setAttribute(
				"portletLayoutPageTemplateEntryScopeERC",
				ScopeUtil.getItemScopeExternalReferenceCode(
					layoutPageTemplateEntry.getGroupId(),
					targetLayout.getGroupId()));

			Locale targetSiteDefaultLocale = _portal.getSiteDefaultLocale(
				targetLayout.getGroupId());

			LocaleThreadLocal.setSiteDefaultLocale(targetSiteDefaultLocale);

			targetLayout = _layoutLocalService.updateLayout(
				targetLayout.getGroupId(), targetLayout.isPrivateLayout(),
				targetLayout.getLayoutId(), targetLayout.getParentLayoutId(),
				targetLayout.getNameMap(), targetLayout.getTitleMap(),
				targetLayout.getDescriptionMap(), targetLayout.getKeywordsMap(),
				targetLayout.getRobotsMap(), layoutPrototypeLayout.getType(),
				targetLayout.isHidden(), targetLayout.getFriendlyURLMap(),
				layoutPrototypeLayout.isIconImage(), iconBytes, null, null,
				null, null,
				layoutPrototypeLayout.getMasterLayoutPageTemplateEntryERC(),
				serviceContext);
		}
		finally {
			serviceContext.setAssetCategoryIds(originalAssetCategoryIds);
			serviceContext.setAssetTagNames(originalAssetTagNames);

			if (originalPortletLayoutPageTemplateEntryERC == null) {
				serviceContext.removeAttribute(
					"portletLayoutPageTemplateEntryERC");
			}
			else {
				serviceContext.setAttribute(
					"portletLayoutPageTemplateEntryERC",
					originalPortletLayoutPageTemplateEntryERC);
			}

			if (originalPortletLayoutPageTemplateEntryLinkEnabled == null) {
				serviceContext.removeAttribute(
					"portletLayoutPageTemplateEntryLinkEnabled");
			}
			else {
				serviceContext.setAttribute(
					"portletLayoutPageTemplateEntryLinkEnabled",
					originalPortletLayoutPageTemplateEntryLinkEnabled);
			}

			if (originalPortletLayoutPageTemplateEntryScopeERC == null) {
				serviceContext.removeAttribute(
					"portletLayoutPageTemplateEntryScopeERC");
			}
			else {
				serviceContext.setAttribute(
					"portletLayoutPageTemplateEntryScopeERC",
					originalPortletLayoutPageTemplateEntryScopeERC);
			}

			LocaleThreadLocal.setSiteDefaultLocale(siteDefaultLocale);
		}

		targetLayout = _layoutLocalService.updateTypeSettings(
			targetLayout, layoutPrototypeLayout.getTypeSettings());

		copyExpandoBridgeAttributes(layoutPrototypeLayout, targetLayout);

		copyPortletPermissions(targetLayout, layoutPrototypeLayout);

		copyPortletSetups(layoutPrototypeLayout, targetLayout);

		_layoutLocalService.updateLookAndFeel(
			targetLayout.getGroupId(), targetLayout.isPrivateLayout(),
			targetLayout.getLayoutId(), layoutPrototypeLayout.getThemeId(),
			layoutPrototypeLayout.getColorSchemeId(),
			layoutPrototypeLayout.getCss());

		deleteUnreferencedPortlets(
			targetLayoutPortletIds, targetLayout, layoutPrototypeLayout);

		targetLayout = _layoutLocalService.getLayout(targetLayout.getPlid());

		UnicodeProperties typeSettingsUnicodeProperties =
			targetLayout.getTypeSettingsProperties();

		Date modifiedDate = targetLayout.getModifiedDate();

		typeSettingsUnicodeProperties.setProperty(
			LAST_MERGE_TIME, String.valueOf(modifiedDate.getTime()));

		_layoutLocalService.updateTypeSettings(
			targetLayout, targetLayout.getTypeSettings());

		UnicodeProperties prototypeTypeSettingsUnicodeProperties =
			layoutPrototypeLayout.getTypeSettingsProperties();

		if (prototypeTypeSettingsUnicodeProperties.containsKey(
				MERGE_FAIL_COUNT)) {

			prototypeTypeSettingsUnicodeProperties.remove(MERGE_FAIL_COUNT);

			_layoutLocalService.updateLayout(layoutPrototypeLayout);
		}
	}

	@Override
	public void copyPortletPermissions(Layout targetLayout, Layout sourceLayout)
		throws Exception {

		List<Role> roles = _roleLocalService.getGroupRelatedRoles(
			targetLayout.getGroupId());
		Group targetGroup = targetLayout.getGroup();

		LayoutTypePortlet sourceLayoutTypePortlet =
			(LayoutTypePortlet)sourceLayout.getLayoutType();

		List<String> sourcePortletIds = sourceLayoutTypePortlet.getPortletIds();

		for (String sourcePortletId : sourcePortletIds) {
			String resourceName = PortletIdCodec.decodePortletName(
				sourcePortletId);

			String sourceResourcePrimKey = PortletPermissionUtil.getPrimaryKey(
				sourceLayout.getPlid(), sourcePortletId);

			String targetResourcePrimKey = PortletPermissionUtil.getPrimaryKey(
				targetLayout.getPlid(), sourcePortletId);

			List<String> actionIds =
				ResourceActionsUtil.getPortletResourceActions(resourceName);

			for (Role role : roles) {
				String roleName = role.getName();

				if (roleName.equals(RoleConstants.ADMINISTRATOR) ||
					(!targetGroup.isLayoutSetPrototype() &&
					 targetLayout.isPrivateLayout() &&
					 roleName.equals(RoleConstants.GUEST))) {

					continue;
				}

				List<String> actions =
					_resourcePermissionLocalService.
						getAvailableResourcePermissionActionIds(
							targetLayout.getCompanyId(), resourceName,
							ResourceConstants.SCOPE_INDIVIDUAL,
							sourceResourcePrimKey, role.getRoleId(), actionIds);

				_resourcePermissionLocalService.setResourcePermissions(
					targetLayout.getCompanyId(), resourceName,
					ResourceConstants.SCOPE_INDIVIDUAL, targetResourcePrimKey,
					role.getRoleId(), actions.toArray(new String[0]));
			}
		}
	}

	@Override
	public void copyPortletSetups(Layout sourceLayout, Layout targetLayout)
		throws Exception {

		LayoutTypePortlet sourceLayoutTypePortlet =
			(LayoutTypePortlet)sourceLayout.getLayoutType();

		List<String> sourcePortletIds = ListUtil.toList(
			sourceLayoutTypePortlet.getAllPortlets(),
			Portlet.PORTLET_ID_ACCESSOR);

		for (String sourcePortletId : sourcePortletIds) {
			PortletPreferences sourcePortletPreferences =
				PortletPreferencesFactoryUtil.getPortletSetup(
					sourceLayout, sourcePortletId, null);

			PortletPreferencesImpl sourcePortletPreferencesImpl =
				(PortletPreferencesImpl)sourcePortletPreferences;

			PortletPreferences targetPortletPreferences =
				PortletPreferencesFactoryUtil.getPortletSetup(
					targetLayout, sourcePortletId, null);

			PortletPreferencesImpl targetPortletPreferencesImpl =
				(PortletPreferencesImpl)targetPortletPreferences;

			_portletPreferencesLocalService.updatePreferences(
				targetPortletPreferencesImpl.getOwnerId(),
				targetPortletPreferencesImpl.getOwnerType(),
				targetPortletPreferencesImpl.getPlid(), sourcePortletId,
				sourcePortletPreferences);

			if ((sourcePortletPreferencesImpl.getOwnerId() !=
					PortletKeys.PREFS_OWNER_ID_DEFAULT) &&
				(sourcePortletPreferencesImpl.getOwnerType() !=
					PortletKeys.PREFS_OWNER_TYPE_LAYOUT)) {

				sourcePortletPreferences =
					PortletPreferencesFactoryUtil.getLayoutPortletSetup(
						sourceLayout, sourcePortletId);

				targetPortletPreferences =
					PortletPreferencesFactoryUtil.getLayoutPortletSetup(
						targetLayout, sourcePortletId);

				targetPortletPreferencesImpl =
					(PortletPreferencesImpl)targetPortletPreferences;

				_portletPreferencesLocalService.updatePreferences(
					targetPortletPreferencesImpl.getOwnerId(),
					targetPortletPreferencesImpl.getOwnerType(),
					targetPortletPreferencesImpl.getPlid(), sourcePortletId,
					sourcePortletPreferences);
			}

			ServiceContext serviceContext =
				ServiceContextThreadLocal.getServiceContext();

			_updateLayoutScopes(
				serviceContext.getUserId(), sourceLayout, targetLayout,
				sourcePortletPreferences, targetPortletPreferences,
				sourcePortletId, serviceContext.getLanguageId());
		}
	}

	@Override
	public boolean isLayoutSetMergeable(LayoutSet layoutSet)
		throws PortalException {

		if (!layoutSet.isLayoutSetPrototypeLinkActive()) {
			return false;
		}

		Group group = layoutSet.getGroup();

		if (group.isLayoutPrototype() || group.isLayoutSetPrototype()) {
			return false;
		}

		return true;
	}

	@Override
	public void mergeLayoutPrototypeLayout(Layout layout) throws Exception {
		if (!layout.isPortletLayoutPageTemplateEntryLinkActive()) {
			return;
		}

		if (Validator.isNotNull(layout.getLayoutSetPrototypeLayoutERC())) {
			if (_log.isDebugEnabled()) {
				_log.debug(
					"Merge not performed because the layout is linked to a " +
						"layout set prototype with external reference code " +
							layout.getLayoutSetPrototypeLayoutERC());
			}

			return;
		}

		Group group = layout.getGroup();

		if (group.isLayoutPrototype() || group.hasStagingGroup()) {
			return;
		}

		long lastMergeTime = GetterUtil.getLong(
			layout.getTypeSettingsProperty(LAST_MERGE_TIME));

		if (lastMergeTime == 0) {
			try {
				MergeLayoutPrototypesThreadLocal.setInProgress(true);

				Layout targetLayout = _layoutLocalService.getLayout(
					layout.getPlid());

				if (targetLayout != null) {
					lastMergeTime = GetterUtil.getLong(
						targetLayout.getTypeSettingsProperty(LAST_MERGE_TIME));
				}
			}
			finally {
				MergeLayoutPrototypesThreadLocal.setInProgress(false);
			}
		}

		if (Validator.isNull(layout.getLayoutPrototypeUuid())) {
			if (_log.isWarnEnabled()) {
				_log.warn(
					"Merge not performed because layout prototype does not " +
						"exist for layout PLID " + layout.getPlid());
			}

			return;
		}

		LayoutPrototype layoutPrototype =
			_layoutPrototypeLocalService.getLayoutPrototypeByUuidAndCompanyId(
				layout.getLayoutPrototypeUuid(), layout.getCompanyId());

		Layout layoutPrototypeLayout = layoutPrototype.getLayout();

		Date modifiedDate = layoutPrototypeLayout.getModifiedDate();

		if (lastMergeTime >= modifiedDate.getTime()) {
			return;
		}

		UnicodeProperties prototypeTypeSettingsUnicodeProperties =
			layoutPrototypeLayout.getTypeSettingsProperties();

		int mergeFailCount = GetterUtil.getInteger(
			prototypeTypeSettingsUnicodeProperties.getProperty(
				MERGE_FAIL_COUNT));

		if (mergeFailCount >
				PropsValues.LAYOUT_PROTOTYPE_MERGE_FAIL_THRESHOLD) {

			if (_log.isWarnEnabled()) {
				_log.warn(
					StringBundler.concat(
						"Merge not performed because the fail threshold was ",
						"reached for layoutPrototypeId ",
						layoutPrototype.getLayoutPrototypeId(),
						" and layoutId ", layoutPrototypeLayout.getLayoutId(),
						". Update the count in the database to try again."));
			}

			return;
		}

		String owner = _acquireLock(
			Layout.class.getName(), layout.getPlid(),
			PropsValues.LAYOUT_PROTOTYPE_MERGE_LOCK_MAX_TIME);

		if (owner == null) {
			return;
		}

		EntityCacheUtil.clearLocalCache();

		layout = _layoutLocalService.fetchLayout(layout.getPlid());

		try {
			MergeLayoutPrototypesThreadLocal.setInProgress(true);

			if (_log.isDebugEnabled()) {
				_log.debug(
					StringBundler.concat(
						"Applying layout prototype ", layoutPrototype.getUuid(),
						" (mvccVersion ", layoutPrototype.getMvccVersion(),
						") to layout ", layout.getPlid(), " (mvccVersion ",
						layout.getMvccVersion(), ")"));
			}

			applyLayoutPrototype(layoutPrototype, layout, true);
		}
		catch (CTTransactionException ctTransactionException) {
			throw ctTransactionException;
		}
		catch (Exception exception) {
			_log.error(exception);

			prototypeTypeSettingsUnicodeProperties.setProperty(
				MERGE_FAIL_COUNT, String.valueOf(++mergeFailCount));

			// Invoke updateImpl so that we do not trigger the listeners

			_layoutLocalService.updateLayout(layoutPrototypeLayout);
		}
		finally {
			MergeLayoutPrototypesThreadLocal.setInProgress(false);

			_releaseLock(Layout.class.getName(), layout.getPlid(), owner);
		}
	}

	@Override
	public void mergeLayoutSetPrototypeLayouts(LayoutSet layoutSet)
		throws Exception {

		if (ExportImportThreadLocal.isExportInProcess() ||
			ExportImportThreadLocal.isImportInProcess() ||
			ExportImportThreadLocal.isStagingInProcess()) {

			throw new IllegalStateException(
				"The site template merge cannot start while an export, " +
					"import, or staging process is in progress");
		}

		layoutSet = _layoutSetLocalService.fetchLayoutSet(
			layoutSet.getLayoutSetId());

		if (!isLayoutSetMergeable(layoutSet)) {
			return;
		}

		LayoutSetPrototype layoutSetPrototype =
			_layoutSetPrototypeLocalService.
				getLayoutSetPrototypeByUuidAndCompanyId(
					layoutSet.getLayoutSetPrototypeUuid(),
					layoutSet.getCompanyId());

		User user = _userLocalService.getDefaultUser(layoutSet.getCompanyId());

		ExportImportConfiguration exportImportConfiguration =
			_buildExportImportConfiguration(
				true, layoutSet, layoutSetPrototype, user);

		_exportImportLocalService.mergeLayoutSetPrototypeInBackground(
			user.getUserId(), layoutSet.getGroupId(),
			exportImportConfiguration);
	}

	@Override
	public void mergeLayoutSetPrototypeLayouts(
			LayoutSetPrototype layoutSetPrototype, long userId)
		throws Exception {

		if (!FeatureFlagManagerUtil.isEnabled(
				layoutSetPrototype.getCompanyId(), "LPD-82107")) {

			throw new UnsupportedOperationException(
				"The site template merge is not available");
		}

		if (ExportImportThreadLocal.isExportInProcess() ||
			ExportImportThreadLocal.isImportInProcess() ||
			ExportImportThreadLocal.isStagingInProcess()) {

			throw new IllegalStateException(
				"The site template merge cannot start while an export, " +
					"import, or staging process is in progress");
		}

		if (_ctSettingsConfigurationHelper.isEnabled(
				layoutSetPrototype.getCompanyId())) {

			throw new IllegalStateException(
				"The site template merge cannot start while publications is " +
					"enabled");
		}

		List<LayoutSet> mergeableLayoutSets = new ArrayList<>();

		for (LayoutSet layoutSet :
				_layoutSetLocalService.getLayoutSetsByLayoutSetPrototypeUuid(
					layoutSetPrototype.getUuid())) {

			if (isLayoutSetMergeable(layoutSet)) {
				mergeableLayoutSets.add(layoutSet);
			}
		}

		Map<Long, ExportImportConfiguration> exportImportConfigurations =
			new HashMap<>();

		boolean preValidationErrors = false;

		User user = _userLocalService.getUser(userId);

		for (Iterator<LayoutSet> iterator = mergeableLayoutSets.iterator();
			 iterator.hasNext();) {

			LayoutSet layoutSet = iterator.next();

			try {
				exportImportConfigurations.put(
					layoutSet.getLayoutSetId(),
					_buildExportImportConfiguration(
						false, layoutSet, layoutSetPrototype, user));
			}
			catch (PortalException portalException) {
				_log.error(
					"Unable to add draft export/import configuration for " +
						"layout set " + layoutSet.getLayoutSetId(),
					portalException);

				iterator.remove();

				preValidationErrors = true;
			}
		}

		if (mergeableLayoutSets.isEmpty()) {
			if (preValidationErrors) {
				throw new PortalException(
					"Unable to start site template merge");
			}

			LayoutSetPrototypeNotificationUtil.sendMergeCompletedNotification(
				null, layoutSetPrototype, userId);

			return;
		}

		Map<String, Serializable> taskContextMap =
			LayoutSetPrototypeNotificationUtil.buildTaskContextMap(
				mergeableLayoutSets, layoutSetPrototype, preValidationErrors,
				userId);

		for (LayoutSet layoutSet : mergeableLayoutSets) {
			try {
				_exportImportLocalService.mergeLayoutSetPrototypeInBackground(
					userId, layoutSet.getGroupId(),
					exportImportConfigurations.get(layoutSet.getLayoutSetId()),
					taskContextMap);
			}
			catch (Exception exception) {
				_log.error(
					"Unable to start site template merge for layout set " +
						layoutSet.getLayoutSetId(),
					exception);
			}
		}
	}

	@Override
	public void updateLayoutSetPrototypesLinks(
			Group group, boolean mergeLayoutSetPrototype,
			long publicLayoutSetPrototypeId, long privateLayoutSetPrototypeId,
			boolean publicLayoutSetPrototypeLinkEnabled,
			boolean privateLayoutSetPrototypeLinkEnabled)
		throws Exception {

		updateLayoutSetPrototypeLink(
			group.getGroupId(), mergeLayoutSetPrototype, true,
			privateLayoutSetPrototypeId, privateLayoutSetPrototypeLinkEnabled);
		updateLayoutSetPrototypeLink(
			group.getGroupId(), mergeLayoutSetPrototype, false,
			publicLayoutSetPrototypeId, publicLayoutSetPrototypeLinkEnabled);
	}

	@Override
	public void updateLayoutSetPrototypesLinks(
			Group group, long publicLayoutSetPrototypeId,
			long privateLayoutSetPrototypeId,
			boolean publicLayoutSetPrototypeLinkEnabled,
			boolean privateLayoutSetPrototypeLinkEnabled)
		throws Exception {

		updateLayoutSetPrototypesLinks(
			group, true, publicLayoutSetPrototypeId,
			privateLayoutSetPrototypeId, publicLayoutSetPrototypeLinkEnabled,
			privateLayoutSetPrototypeLinkEnabled);
	}

	protected void deleteUnreferencedPortlets(
			List<String> targetLayoutPortletIds, Layout targetLayout,
			Layout sourceLayout)
		throws Exception {

		List<String> unreferencedPortletIds = new ArrayList<>(
			targetLayoutPortletIds);

		LayoutTypePortlet sourceLayoutType =
			(LayoutTypePortlet)sourceLayout.getLayoutType();

		unreferencedPortletIds.removeAll(sourceLayoutType.getPortletIds());

		_portletLocalService.deletePortlets(
			targetLayout.getCompanyId(),
			unreferencedPortletIds.toArray(new String[0]),
			targetLayout.getPlid());
	}

	protected File exportLayoutSetPrototype(
		User user, LayoutSetPrototype layoutSetPrototype,
		Map<String, String[]> parameterMap, String cacheFileName) {

		File cacheFile = null;

		if (cacheFileName != null) {
			cacheFile = new File(cacheFileName);

			if (cacheFile.exists()) {
				if (_log.isDebugEnabled()) {
					_log.debug(
						"Using cached layout set prototype LAR file " +
							cacheFile.getAbsolutePath());
				}

				return cacheFile;
			}
		}

		long layoutSetPrototypeGroupId = 0;

		try {
			layoutSetPrototypeGroupId = layoutSetPrototype.getGroupId();
		}
		catch (PortalException portalException) {
			_log.error(
				"Unable to get groupId for layout set prototype " +
					layoutSetPrototype.getLayoutSetPrototypeId(),
				portalException);

			return null;
		}

		List<Layout> layoutSetPrototypeLayouts = _layoutLocalService.getLayouts(
			layoutSetPrototypeGroupId, true);

		Map<String, Serializable> exportLayoutSettingsMap =
			ExportImportConfigurationSettingsMapFactoryUtil.
				buildExportLayoutSettingsMap(
					user, layoutSetPrototypeGroupId, true,
					_exportImportHelper.getLayoutIds(layoutSetPrototypeLayouts),
					parameterMap);

		ExportImportConfiguration exportImportConfiguration = null;

		try {
			exportImportConfiguration =
				_exportImportConfigurationLocalService.
					addDraftExportImportConfiguration(
						user.getUserId(),
						ExportImportConfigurationConstants.TYPE_EXPORT_LAYOUT,
						exportLayoutSettingsMap);
		}
		catch (PortalException portalException) {
			_log.error(
				"Unable to add draft export-import configuration",
				portalException);

			return null;
		}

		File file = null;

		try {
			file = _exportImportLocalService.exportLayoutsAsFile(
				exportImportConfiguration);
		}
		catch (PortalException portalException) {
			_log.error(
				"Unable to export layout set prototype " +
					layoutSetPrototype.getLayoutSetPrototypeId(),
				portalException);

			return null;
		}

		if (cacheFile == null) {
			return file;
		}

		try {
			FileUtil.copyFile(file, cacheFile);

			if (_log.isDebugEnabled()) {
				_log.debug(
					StringBundler.concat(
						"Copied ", file.getAbsolutePath(), " to ",
						cacheFile.getAbsolutePath()));
			}
		}
		catch (Exception exception) {
			_log.error(
				StringBundler.concat(
					"Unable to copy file ", file.getAbsolutePath(), " to ",
					cacheFile.getAbsolutePath()),
				exception);
		}

		return cacheFile;
	}

	protected Map<String, String[]> getLayoutSetPrototypesParameters(
		boolean firstMerge) {

		return LinkedHashMapBuilder.put(
			PortletDataHandlerKeys.DATA_STRATEGY,
			new String[] {PortletDataHandlerKeys.DATA_STRATEGY_MIRROR}
		).put(
			PortletDataHandlerKeys.DELETE_MISSING_LAYOUTS,
			new String[] {Boolean.FALSE.toString()}
		).put(
			PortletDataHandlerKeys.DELETE_PORTLET_DATA,
			new String[] {Boolean.FALSE.toString()}
		).put(
			PortletDataHandlerKeys.FAVICON,
			new String[] {Boolean.TRUE.toString()}
		).put(
			PortletDataHandlerKeys.IGNORE_LAST_PUBLISH_DATE,
			new String[] {Boolean.TRUE.toString()}
		).put(
			PortletDataHandlerKeys.LAYOUT_SET_PROTOTYPE_LINK_ENABLED,
			new String[] {Boolean.TRUE.toString()}
		).put(
			PortletDataHandlerKeys.LAYOUT_SET_PROTOTYPE_SETTINGS,
			new String[] {Boolean.TRUE.toString()}
		).put(
			PortletDataHandlerKeys.LAYOUT_SET_SETTINGS,
			new String[] {Boolean.TRUE.toString()}
		).put(
			PortletDataHandlerKeys.LAYOUTS_IMPORT_MODE,
			new String[] {
				PortletDataHandlerKeys.
					LAYOUTS_IMPORT_MODE_CREATED_FROM_PROTOTYPE
			}
		).put(
			PortletDataHandlerKeys.LOGO, new String[] {Boolean.TRUE.toString()}
		).put(
			PortletDataHandlerKeys.PERMISSIONS,
			new String[] {String.valueOf(firstMerge)}
		).put(
			PortletDataHandlerKeys.PORTLET_CONFIGURATION,
			new String[] {Boolean.TRUE.toString()}
		).put(
			PortletDataHandlerKeys.PORTLET_CONFIGURATION_ALL,
			new String[] {Boolean.TRUE.toString()}
		).put(
			PortletDataHandlerKeys.PORTLET_DATA,
			new String[] {Boolean.TRUE.toString()}
		).put(
			PortletDataHandlerKeys.PORTLET_DATA_ALL,
			new String[] {Boolean.TRUE.toString()}
		).put(
			PortletDataHandlerKeys.PORTLET_SETUP_ALL,
			new String[] {Boolean.TRUE.toString()}
		).put(
			PortletDataHandlerKeys.THEME_REFERENCE,
			new String[] {Boolean.TRUE.toString()}
		).put(
			PortletDataHandlerKeys.UPDATE_LAST_PUBLISH_DATE,
			new String[] {Boolean.FALSE.toString()}
		).put(
			PortletDataHandlerKeys.USER_ID_STRATEGY,
			new String[] {UserIdStrategy.CURRENT_USER_ID}
		).build();
	}

	protected void importLayoutSetPrototype(
			LayoutSetPrototype layoutSetPrototype, long groupId,
			boolean privateLayout, Map<String, String[]> parameterMap,
			boolean importData)
		throws PortalException {

		File file = null;

		User user = _userLocalService.getGuestUser(
			layoutSetPrototype.getCompanyId());

		long lastMergeVersion = layoutSetPrototype.getMvccVersion();

		parameterMap.put(
			"lastMergeVersion",
			new String[] {String.valueOf(lastMergeVersion)});

		parameterMap.put(
			"layoutSetPrototypeId",
			new String[] {
				String.valueOf(layoutSetPrototype.getLayoutSetPrototypeId())
			});

		if (importData) {
			file = exportLayoutSetPrototype(
				user, layoutSetPrototype, parameterMap, null);
		}
		else {
			String cacheFileName = StringBundler.concat(
				_TEMP_DIR, layoutSetPrototype.getUuid(), ".v", lastMergeVersion,
				".lar");

			file = _exportInProgressMap.computeIfAbsent(
				cacheFileName,
				fileName -> exportLayoutSetPrototype(
					user, layoutSetPrototype, parameterMap, fileName));

			_exportInProgressMap.remove(cacheFileName);
		}

		LayoutSet layoutSet = _layoutSetLocalService.getLayoutSet(
			groupId, privateLayout);

		if (file == null) {
			if (_log.isDebugEnabled()) {
				_log.debug(
					StringBundler.concat(
						"Skipping import of layout set prototype ",
						layoutSetPrototype.getUuid(), " (mvccVersion ",
						layoutSetPrototype.getMvccVersion(), ") to layout set ",
						layoutSet.getLayoutSetId(), " (mvccVersion ",
						layoutSet.getMvccVersion(), ")"));
			}

			return;
		}

		Map<String, Serializable> importLayoutSettingsMap =
			ExportImportConfigurationSettingsMapFactoryUtil.
				buildImportLayoutSettingsMap(
					user.getUserId(), groupId, privateLayout, null,
					parameterMap, user.getLocale(), user.getTimeZone());

		ExportImportConfiguration exportImportConfiguration =
			_exportImportConfigurationLocalService.addExportImportConfiguration(
				user.getUserId(), groupId, StringPool.BLANK, StringPool.BLANK,
				ExportImportConfigurationConstants.TYPE_IMPORT_LAYOUT,
				importLayoutSettingsMap, WorkflowConstants.STATUS_DRAFT,
				new ServiceContext());

		_exportImportLocalService.importLayoutSetPrototypeInBackground(
			user.getUserId(), exportImportConfiguration, file);
	}

	protected void updateLayoutSetPrototypeLink(
			long groupId, boolean mergeLayoutSetPrototype,
			boolean privateLayout, long layoutSetPrototypeId,
			boolean layoutSetPrototypeLinkEnabled)
		throws Exception {

		String layoutSetPrototypeUuid = null;

		if (layoutSetPrototypeId > 0) {
			LayoutSetPrototype layoutSetPrototype =
				_layoutSetPrototypeLocalService.fetchLayoutSetPrototype(
					layoutSetPrototypeId);

			if (layoutSetPrototype != null) {
				layoutSetPrototypeUuid = layoutSetPrototype.getUuid();

				// Merge without enabling the link

				if (!layoutSetPrototypeLinkEnabled &&
					(layoutSetPrototypeId > 0) && mergeLayoutSetPrototype) {

					boolean mergeLayoutPrototypesThreadLocalInProgress =
						MergeLayoutPrototypesThreadLocal.isInProgress();

					try {
						MergeLayoutPrototypesThreadLocal.setInProgress(true);

						importLayoutSetPrototype(
							layoutSetPrototype, groupId, privateLayout,
							getLayoutSetPrototypesParameters(true), true);
					}
					finally {
						MergeLayoutPrototypesThreadLocal.setInProgress(
							mergeLayoutPrototypesThreadLocalInProgress);
					}
				}
			}
		}

		_layoutSetService.updateLayoutSetPrototypeLinkEnabled(
			groupId, mergeLayoutSetPrototype, privateLayout,
			layoutSetPrototypeLinkEnabled, layoutSetPrototypeUuid);

		_layoutLocalService.updatePriorities(groupId, privateLayout);
	}

	private String _acquireLock(
		String className, long classPK, long mergeLockMaxTime) {

		String owner = PortalUUIDUtil.generate();

		try {
			Lock lock = LockManagerUtil.lock(
				SitesImpl.class.getName(), String.valueOf(classPK), owner);

			// Double deep check

			if (!owner.equals(lock.getOwner())) {
				Date createDate = lock.getCreateDate();

				if ((System.currentTimeMillis() - createDate.getTime()) >=
						mergeLockMaxTime) {

					// Acquire lock if the lock is older than the lock max time

					lock = LockManagerUtil.lock(
						SitesImpl.class.getName(), String.valueOf(classPK),
						lock.getOwner(), owner);

					// Check if acquiring the lock succeeded or if another
					// process has the lock

					if (!owner.equals(lock.getOwner())) {
						return null;
					}
				}
				else {
					return null;
				}
			}
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(exception);
			}

			return null;
		}

		if (_log.isDebugEnabled()) {
			_log.debug(
				StringBundler.concat(
					"Acquired lock for ", SitesImpl.class.getName(),
					" to update ", className, StringPool.POUND, classPK));
		}

		return owner;
	}

	private ExportImportConfiguration _buildExportImportConfiguration(
			boolean firstMerge, LayoutSet layoutSet,
			LayoutSetPrototype layoutSetPrototype, User user)
		throws PortalException {

		Map<String, String[]> parameterMap = getLayoutSetPrototypesParameters(
			firstMerge);

		parameterMap.put(
			PortletDataHandlerKeys.LAYOUT_SET_PRIVATE_LAYOUT,
			new String[] {String.valueOf(layoutSet.isPrivateLayout())});
		parameterMap.put(
			"layoutSetId",
			new String[] {String.valueOf(layoutSet.getLayoutSetId())});
		parameterMap.put(
			"layoutSetPrototypeId",
			new String[] {
				String.valueOf(layoutSetPrototype.getLayoutSetPrototypeId())
			});

		List<Layout> layoutSetPrototypeLayouts = _layoutLocalService.getLayouts(
			layoutSetPrototype.getGroupId(), true);

		Map<String, Serializable> exportLayoutSettingsMap =
			ExportImportConfigurationSettingsMapFactoryUtil.
				buildExportLayoutSettingsMap(
					user, layoutSetPrototype.getGroupId(), true,
					_exportImportHelper.getLayoutIds(layoutSetPrototypeLayouts),
					parameterMap);

		return _exportImportConfigurationLocalService.
			addDraftExportImportConfiguration(
				user.getUserId(),
				ExportImportConfigurationConstants.TYPE_EXPORT_LAYOUT,
				exportLayoutSettingsMap);
	}

	private void _releaseLock(String className, long classPK, String owner) {
		LockManagerUtil.unlock(
			SitesImpl.class.getName(), String.valueOf(classPK), owner);

		if (_log.isDebugEnabled()) {
			_log.debug(
				StringBundler.concat(
					"Released lock for ", SitesImpl.class.getName(),
					" to update ", className, StringPool.POUND, classPK));
		}
	}

	private void _updateLayoutScopes(
			long userId, Layout sourceLayout, Layout targetLayout,
			PortletPreferences sourcePortletPreferences,
			PortletPreferences targetPortletPreferences, String sourcePortletId,
			String languageId)
		throws Exception {

		String scopeType = GetterUtil.getString(
			sourcePortletPreferences.getValue("lfrScopeType", null));

		if (Validator.isNull(scopeType) || !scopeType.equals("layout")) {
			return;
		}

		Layout targetScopeLayout =
			_layoutLocalService.getLayoutByUuidAndGroupId(
				targetLayout.getUuid(), targetLayout.getGroupId(),
				targetLayout.isPrivateLayout());

		if (!targetScopeLayout.hasScopeGroup()) {
			_groupLocalService.addGroup(
				StringPool.BLANK, userId,
				GroupConstants.DEFAULT_PARENT_GROUP_ID, Layout.class.getName(),
				targetLayout.getPlid(), GroupConstants.DEFAULT_LIVE_GROUP_ID,
				targetLayout.getNameMap(), null, 0, null, true,
				GroupConstants.DEFAULT_MEMBERSHIP_RESTRICTION, null, false,
				false, true, null);
		}

		String newPortletTitle = _portal.getNewPortletTitle(
			_portal.getPortletTitle(
				PortletIdCodec.decodePortletName(sourcePortletId), languageId),
			String.valueOf(sourceLayout.getLayoutId()),
			targetLayout.getName(languageId));

		targetPortletPreferences.setValue(
			"groupId", String.valueOf(targetLayout.getGroupId()));
		targetPortletPreferences.setValue("lfrScopeType", "layout");
		targetPortletPreferences.setValue(
			"lfrScopeLayoutUuid", targetLayout.getUuid());
		targetPortletPreferences.setValue(
			"portletSetupTitle_" + languageId, newPortletTitle);
		targetPortletPreferences.setValue(
			"portletSetupUseCustomTitle", Boolean.TRUE.toString());

		targetPortletPreferences.store();
	}

	private static final String _TEMP_DIR =
		SystemProperties.get(SystemProperties.TMP_DIR) +
			"/liferay/layout_set_prototype/";

	private static final Log _log = LogFactoryUtil.getLog(SitesImpl.class);

	@Reference
	private AssetEntryLocalService _assetEntryLocalService;

	@Reference
	private CTSettingsConfigurationHelper _ctSettingsConfigurationHelper;

	@Reference
	private ExportImportConfigurationLocalService
		_exportImportConfigurationLocalService;

	@Reference
	private ExportImportHelper _exportImportHelper;

	@Reference
	private ExportImportLocalService _exportImportLocalService;

	private final ConcurrentHashMap<String, File> _exportInProgressMap =
		new ConcurrentHashMap<>();

	@Reference
	private GroupLocalService _groupLocalService;

	@Reference
	private ImageLocalService _imageLocalService;

	@Reference
	private LayoutLocalService _layoutLocalService;

	@Reference
	private LayoutPageTemplateEntryLocalService
		_layoutPageTemplateEntryLocalService;

	@Reference
	private LayoutPrototypeLocalService _layoutPrototypeLocalService;

	@Reference
	private LayoutSetLocalService _layoutSetLocalService;

	@Reference
	private LayoutSetPrototypeLocalService _layoutSetPrototypeLocalService;

	@Reference
	private LayoutSetService _layoutSetService;

	@Reference
	private Portal _portal;

	@Reference
	private PortletLocalService _portletLocalService;

	@Reference
	private PortletPreferencesLocalService _portletPreferencesLocalService;

	@Reference
	private ResourcePermissionLocalService _resourcePermissionLocalService;

	@Reference
	private RoleLocalService _roleLocalService;

	@Reference
	private UserLocalService _userLocalService;

}