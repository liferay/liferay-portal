/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.service.impl;

import com.liferay.exportimport.kernel.staging.LayoutStagingUtil;
import com.liferay.exportimport.kernel.staging.MergeLayoutPrototypesThreadLocal;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.bean.BeanReference;
import com.liferay.portal.kernel.dao.orm.EntityCacheUtil;
import com.liferay.portal.kernel.exception.LayoutSetJavaScriptException;
import com.liferay.portal.kernel.exception.LayoutSetVirtualHostException;
import com.liferay.portal.kernel.exception.NoSuchImageException;
import com.liferay.portal.kernel.exception.NoSuchVirtualHostException;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Image;
import com.liferay.portal.kernel.model.LayoutSet;
import com.liferay.portal.kernel.model.LayoutSetBranch;
import com.liferay.portal.kernel.model.LayoutSetStagingHandler;
import com.liferay.portal.kernel.model.VirtualHost;
import com.liferay.portal.kernel.module.service.Snapshot;
import com.liferay.portal.kernel.service.ImageLocalService;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.VirtualHostLocalService;
import com.liferay.portal.kernel.service.persistence.GroupPersistence;
import com.liferay.portal.kernel.service.persistence.LayoutPersistence;
import com.liferay.portal.kernel.service.persistence.LayoutSetBranchPersistence;
import com.liferay.portal.kernel.service.persistence.VirtualHostPersistence;
import com.liferay.portal.kernel.transaction.TransactionCommitCallbackUtil;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.ColorSchemeFactoryUtil;
import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.PrefsPropsUtil;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.PropsValues;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.UnicodeProperties;
import com.liferay.portal.kernel.util.UnicodePropertiesBuilder;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.model.impl.LayoutSetImpl;
import com.liferay.portal.service.base.LayoutSetLocalServiceBaseImpl;
import com.liferay.portal.util.ThemeFactoryUtil;
import com.liferay.sites.kernel.util.Sites;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import java.net.IDN;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeMap;

/**
 * @author Brian Wing Shun Chan
 * @author Julio Camarero
 * @author Ganesh Ram
 */
public class LayoutSetLocalServiceImpl extends LayoutSetLocalServiceBaseImpl {

	@Override
	public LayoutSet addLayoutSet(long groupId, boolean privateLayout)
		throws PortalException {

		Group group = _groupPersistence.findByPrimaryKey(groupId);

		Date date = new Date();

		long layoutSetId = counterLocalService.increment(
			LayoutSet.class.getName());

		LayoutSet layoutSet = layoutSetPersistence.create(layoutSetId);

		layoutSet.setGroupId(groupId);
		layoutSet.setCompanyId(group.getCompanyId());
		layoutSet.setCreateDate(date);
		layoutSet.setModifiedDate(date);
		layoutSet.setPrivateLayout(privateLayout);

		layoutSet = initLayoutSet(layoutSet);

		return layoutSetPersistence.update(layoutSet);
	}

	@Override
	public void deleteLayoutSet(
			long groupId, boolean privateLayout, ServiceContext serviceContext)
		throws PortalException {

		Group group = _groupPersistence.findByPrimaryKey(groupId);

		LayoutSet layoutSet = layoutSetPersistence.findByG_P(
			groupId, privateLayout);

		// Layouts

		serviceContext.setAttribute("updatePageCount", Boolean.FALSE);

		_layoutLocalService.deleteLayouts(
			groupId, privateLayout, serviceContext);

		// Logo

		if (group.isStagingGroup() || !group.isOrganization() ||
			!group.isSite()) {

			try {
				_imageLocalService.deleteImage(layoutSet.getLogoId());
			}
			catch (NoSuchImageException noSuchImageException) {
				if (_log.isWarnEnabled()) {
					_log.warn(
						"Unable to delete image " + layoutSet.getLogoId(),
						noSuchImageException);
				}
			}
		}

		// Layout set

		if (!group.isStagingGroup() && group.isOrganization() &&
			group.isSite()) {

			layoutSet = initLayoutSet(layoutSet);

			layoutSet.setLogoId(layoutSet.getLogoId());

			layoutSet = layoutSetPersistence.update(layoutSet);
		}
		else {
			layoutSetPersistence.removeByG_P(groupId, privateLayout);
		}

		// Virtual host

		_virtualHostPersistence.removeByC_L(
			layoutSet.getCompanyId(), layoutSet.getLayoutSetId());
	}

	@Override
	public LayoutSet fetchLayoutSet(long groupId, boolean privateLayout) {
		return layoutSetPersistence.fetchByG_P(groupId, privateLayout);
	}

	@Override
	public LayoutSet fetchLayoutSet(String virtualHostname) {
		virtualHostname = StringUtil.toLowerCase(
			StringUtil.trim(virtualHostname));

		VirtualHost virtualHost = _virtualHostPersistence.fetchByHostname(
			virtualHostname);

		if ((virtualHost == null) && virtualHostname.contains("xn--")) {
			virtualHost = _virtualHostPersistence.fetchByHostname(
				IDN.toUnicode(virtualHostname));
		}

		if ((virtualHost == null) || (virtualHost.getLayoutSetId() == 0)) {
			return null;
		}

		return layoutSetPersistence.fetchByPrimaryKey(
			virtualHost.getLayoutSetId());
	}

	@Override
	public LayoutSet fetchLayoutSetByLogoId(
		boolean privateLayout, long logoId) {

		if (logoId <= 0) {
			return null;
		}

		List<LayoutSet> layoutSets = layoutSetPersistence.findByP_L(
			privateLayout, logoId);

		if (layoutSets.isEmpty()) {
			return null;
		}

		if ((layoutSets.size() > 1) && _log.isWarnEnabled()) {
			_log.error("More than one layout set uses logo ID " + logoId);
		}

		return layoutSets.get(layoutSets.size() - 1);
	}

	@Override
	public LayoutSet getLayoutSet(long groupId, boolean privateLayout)
		throws PortalException {

		return layoutSetPersistence.findByG_P(groupId, privateLayout);
	}

	@Override
	public LayoutSet getLayoutSet(String virtualHostname)
		throws PortalException {

		virtualHostname = StringUtil.toLowerCase(
			StringUtil.trim(virtualHostname));

		VirtualHost virtualHost = null;

		try {
			virtualHost = _virtualHostPersistence.findByHostname(
				virtualHostname);
		}
		catch (NoSuchVirtualHostException noSuchVirtualHostException) {
			if (virtualHostname.contains("xn--")) {
				virtualHost = _virtualHostPersistence.findByHostname(
					IDN.toUnicode(virtualHostname));
			}
			else {
				throw noSuchVirtualHostException;
			}
		}

		if (virtualHost.getLayoutSetId() == 0) {
			throw new LayoutSetVirtualHostException(
				"Virtual host is associated with company " +
					virtualHost.getCompanyId());
		}

		return layoutSetPersistence.findByPrimaryKey(
			virtualHost.getLayoutSetId());
	}

	@Override
	public List<LayoutSet> getLayoutSetsByLayoutSetPrototypeUuid(
		String layoutSetPrototypeUuid) {

		return layoutSetPersistence.findByLayoutSetPrototypeUuid(
			layoutSetPrototypeUuid);
	}

	@Override
	public int getPageCount(long groupId, boolean privateLayout) {
		return _layoutPersistence.countByG_P(groupId, privateLayout);
	}

	@Override
	public LayoutSet updateFaviconFileEntryId(
			long groupId, boolean privateLayout, long faviconFileEntryId)
		throws PortalException {

		LayoutSet layoutSet = layoutSetPersistence.findByG_P(
			groupId, privateLayout);

		layoutSet.setModifiedDate(new Date());
		layoutSet.setFaviconFileEntryId(faviconFileEntryId);

		return layoutSetPersistence.update(layoutSet);
	}

	/**
	 * Updates the state of the layout set prototype link.
	 *
	 * @param groupId the primary key of the group
	 * @param privateLayout whether the layout set is private to the group
	 * @param layoutSetPrototypeLinkEnabled whether the layout set prototype is
	 *        link enabled
	 * @param layoutSetPrototypeUuid the uuid of the layout set prototype to
	 *        link with
	 */
	@Override
	public void updateLayoutSetPrototypeLinkEnabled(
			long groupId, boolean privateLayout,
			boolean layoutSetPrototypeLinkEnabled,
			String layoutSetPrototypeUuid)
		throws PortalException {

		LayoutSet layoutSet = layoutSetPersistence.findByG_P(
			groupId, privateLayout);

		LayoutSetBranch layoutSetBranch = _getLayoutSetBranch(layoutSet);

		if (layoutSetBranch == null) {
			if (Validator.isNull(layoutSetPrototypeUuid)) {
				layoutSetPrototypeUuid = layoutSet.getLayoutSetPrototypeUuid();
			}

			if (!layoutSetPrototypeUuid.equals(
					layoutSet.getLayoutSetPrototypeUuid())) {

				UnicodeProperties unicodeProperties =
					layoutSet.getSettingsProperties();

				unicodeProperties.remove(Sites.LAST_MERGE_TIME);
				unicodeProperties.remove(Sites.LAST_RESET_TIME);
				unicodeProperties.remove(Sites.LAST_MERGE_VERSION);

				layoutSet.setSettingsProperties(unicodeProperties);
			}

			layoutSet.setLayoutSetPrototypeUuid(layoutSetPrototypeUuid);

			if (Validator.isNull(layoutSetPrototypeUuid)) {
				layoutSetPrototypeLinkEnabled = false;
			}

			layoutSet.setLayoutSetPrototypeLinkEnabled(
				layoutSetPrototypeLinkEnabled);

			layoutSet = layoutSetPersistence.update(layoutSet);
		}
		else {
			if (Validator.isNull(layoutSetPrototypeUuid)) {
				layoutSetPrototypeUuid =
					layoutSetBranch.getLayoutSetPrototypeUuid();
			}

			if (Validator.isNull(layoutSetPrototypeUuid) &&
				layoutSetPrototypeLinkEnabled) {

				throw new IllegalStateException(
					"Cannot set layoutSetPrototypeLinkEnabled to true when " +
						"layoutSetPrototypeUuid is null");
			}

			layoutSetBranch.setLayoutSetPrototypeUuid(layoutSetPrototypeUuid);
			layoutSetBranch.setLayoutSetPrototypeLinkEnabled(
				layoutSetPrototypeLinkEnabled);

			_layoutSetBranchPersistence.update(layoutSetBranch);
		}

		try {
			MergeLayoutPrototypesThreadLocal.setSkipMerge(false);

			Sites sites = _sitesSnapshot.get();

			sites.mergeLayoutSetPrototypeLayouts(
				_groupPersistence.findByPrimaryKey(groupId), layoutSet);
		}
		catch (Exception exception) {
			if (_log.isWarnEnabled()) {
				_log.warn(
					"Unable to force propagation from site template to site",
					exception);
			}
		}
	}

	@Override
	public LayoutSet updateLogo(
			long groupId, boolean privateLayout, boolean hasLogo, byte[] bytes)
		throws PortalException {

		LayoutSet layoutSet = layoutSetPersistence.findByG_P(
			groupId, privateLayout);

		LayoutSetBranch layoutSetBranch = _getLayoutSetBranch(layoutSet);

		if (layoutSetBranch == null) {
			PortalUtil.updateImageId(
				layoutSet, hasLogo, bytes, "logoId", 0, 0, 0);

			long logoId = layoutSet.getLogoId();

			layoutSet = layoutSetPersistence.findByG_P(groupId, privateLayout);

			layoutSet.setModifiedDate(new Date());
			layoutSet.setLogoId(logoId);

			return layoutSetPersistence.update(layoutSet);
		}

		PortalUtil.updateImageId(
			layoutSetBranch, hasLogo, bytes, "logoId", 0, 0, 0);

		long logoId = layoutSetBranch.getLogoId();

		layoutSetBranch = _getLayoutSetBranch(layoutSet);

		layoutSetBranch.setModifiedDate(new Date());
		layoutSetBranch.setLogoId(logoId);

		_layoutSetBranchPersistence.update(layoutSetBranch);

		return layoutSet;
	}

	@Override
	public LayoutSet updateLogo(
			long groupId, boolean privateLayout, boolean hasLogo, File file)
		throws PortalException {

		byte[] bytes = null;

		try {
			bytes = FileUtil.getBytes(file);
		}
		catch (IOException ioException) {
			throw new SystemException(ioException);
		}

		return updateLogo(groupId, privateLayout, hasLogo, bytes);
	}

	@Override
	public LayoutSet updateLogo(
			long groupId, boolean privateLayout, boolean hasLogo,
			InputStream inputStream)
		throws PortalException {

		return updateLogo(groupId, privateLayout, hasLogo, inputStream, true);
	}

	@Override
	public LayoutSet updateLogo(
			long groupId, boolean privateLayout, boolean hasLogo,
			InputStream inputStream, boolean cleanUpStream)
		throws PortalException {

		byte[] bytes = null;

		try {
			bytes = FileUtil.getBytes(inputStream, -1, cleanUpStream);
		}
		catch (IOException ioException) {
			throw new SystemException(ioException);
		}

		return updateLogo(groupId, privateLayout, hasLogo, bytes);
	}

	@Override
	public LayoutSet updateLookAndFeel(
			long groupId, boolean privateLayout, String themeId,
			String colorSchemeId, String css)
		throws PortalException {

		LayoutSet layoutSet = layoutSetPersistence.findByG_P(
			groupId, privateLayout);

		if (Validator.isNull(themeId)) {
			themeId = ThemeFactoryUtil.getDefaultRegularThemeId(
				layoutSet.getCompanyId());
		}

		if (Validator.isNull(colorSchemeId)) {
			colorSchemeId =
				ColorSchemeFactoryUtil.getDefaultRegularColorSchemeId();
		}

		LayoutSetBranch layoutSetBranch = _getLayoutSetBranch(layoutSet);

		if (layoutSetBranch == null) {
			layoutSet.setModifiedDate(new Date());
			layoutSet.setThemeId(themeId);
			layoutSet.setColorSchemeId(colorSchemeId);
			layoutSet.setCss(css);

			layoutSet = layoutSetPersistence.update(layoutSet);

			if (PrefsPropsUtil.getBoolean(
					PropsKeys.THEME_SYNC_ON_GROUP,
					PropsValues.THEME_SYNC_ON_GROUP)) {

				LayoutSet otherLayoutSet = layoutSetPersistence.findByG_P(
					layoutSet.getGroupId(), layoutSet.isPrivateLayout());

				otherLayoutSet.setThemeId(themeId);
				otherLayoutSet.setColorSchemeId(colorSchemeId);

				layoutSetPersistence.update(otherLayoutSet);
			}

			return layoutSet;
		}

		layoutSetBranch.setModifiedDate(new Date());
		layoutSetBranch.setThemeId(themeId);
		layoutSetBranch.setColorSchemeId(colorSchemeId);
		layoutSetBranch.setCss(css);

		_layoutSetBranchPersistence.update(layoutSetBranch);

		return layoutSet;
	}

	@Override
	public void updateLookAndFeel(
			long groupId, String themeId, String colorSchemeId, String css)
		throws PortalException {

		updateLookAndFeel(groupId, false, themeId, colorSchemeId, css);
		updateLookAndFeel(groupId, true, themeId, colorSchemeId, css);
	}

	@Override
	public LayoutSet updateSettings(
			long groupId, boolean privateLayout, String settings)
		throws PortalException {

		UnicodeProperties settingsUnicodeProperties =
			UnicodePropertiesBuilder.fastLoad(
				settings
			).build();

		LayoutSet layoutSet = layoutSetPersistence.findByG_P(
			groupId, privateLayout);

		LayoutSetBranch layoutSetBranch = _getLayoutSetBranch(layoutSet);

		if (layoutSetBranch == null) {
			layoutSet.setModifiedDate(new Date());

			validateSettings(
				layoutSet.getSettingsProperties(), settingsUnicodeProperties);

			layoutSet.setSettingsProperties(settingsUnicodeProperties);

			return layoutSetPersistence.update(layoutSet);
		}

		layoutSetBranch.setModifiedDate(new Date());

		validateSettings(
			layoutSetBranch.getSettingsProperties(), settingsUnicodeProperties);

		layoutSetBranch.setSettingsProperties(settingsUnicodeProperties);

		_layoutSetBranchPersistence.update(layoutSetBranch);

		return layoutSet;
	}

	@Override
	public LayoutSet updateVirtualHosts(
			long groupId, boolean privateLayout,
			TreeMap<String, String> virtualHostnames)
		throws PortalException {

		Set<String> keySet = new HashSet<>(virtualHostnames.keySet());

		for (String curVirtualHostname : keySet) {
			if (!Validator.isDomain(curVirtualHostname)) {
				throw new LayoutSetVirtualHostException(
					"Invalid host name {" + curVirtualHostname + "}");
			}

			if (!StringUtil.isLowerCase(curVirtualHostname)) {
				virtualHostnames.putIfAbsent(
					StringUtil.toLowerCase(curVirtualHostname),
					virtualHostnames.remove(curVirtualHostname));
			}
		}

		LayoutSet layoutSet = layoutSetPersistence.findByG_P(
			groupId, privateLayout);

		if (!virtualHostnames.isEmpty()) {
			long virtualHostsCount =
				_virtualHostLocalService.getVirtualHostsCount(
					layoutSet.getLayoutSetId(),
					ArrayUtil.toStringArray(virtualHostnames.keySet()));

			if (virtualHostsCount > 0) {
				throw new LayoutSetVirtualHostException();
			}

			_virtualHostLocalService.updateVirtualHosts(
				layoutSet.getCompanyId(), layoutSet.getLayoutSetId(),
				virtualHostnames);
		}
		else {
			_virtualHostPersistence.removeByC_L(
				layoutSet.getCompanyId(), layoutSet.getLayoutSetId());

			layoutSetPersistence.clearCache(layoutSet);

			TransactionCommitCallbackUtil.registerCallback(
				() -> {
					EntityCacheUtil.removeResult(
						LayoutSetImpl.class, layoutSet.getLayoutSetId());

					return null;
				});
		}

		return layoutSet;
	}

	protected LayoutSet initLayoutSet(LayoutSet layoutSet)
		throws PortalException {

		Group group = layoutSet.getGroup();

		if (group.isStagingGroup()) {
			LayoutSet liveLayoutSet = null;

			Group liveGroup = group.getLiveGroup();

			if (layoutSet.isPrivateLayout()) {
				liveLayoutSet = liveGroup.getPrivateLayoutSet();
			}
			else {
				liveLayoutSet = liveGroup.getPublicLayoutSet();
			}

			layoutSet.setLogoId(liveLayoutSet.getLogoId());

			if (liveLayoutSet.isLogo()) {
				Image logoImage = _imageLocalService.getImage(
					liveLayoutSet.getLogoId());

				long logoId = counterLocalService.increment();

				_imageLocalService.updateImage(
					layoutSet.getCompanyId(), logoId, logoImage.getTextObj(),
					logoImage.getType(), logoImage.getHeight(),
					logoImage.getWidth(), logoImage.getSize());

				layoutSet.setLogoId(logoId);
			}

			layoutSet.setThemeId(liveLayoutSet.getThemeId());
			layoutSet.setColorSchemeId(liveLayoutSet.getColorSchemeId());
			layoutSet.setCss(liveLayoutSet.getCss());
			layoutSet.setSettings(liveLayoutSet.getSettings());
		}
		else {
			layoutSet.setThemeId(
				ThemeFactoryUtil.getDefaultRegularThemeId(
					group.getCompanyId()));
			layoutSet.setColorSchemeId(
				ColorSchemeFactoryUtil.getDefaultRegularColorSchemeId());
			layoutSet.setCss(StringPool.BLANK);
			layoutSet.setSettings(StringPool.BLANK);
		}

		return layoutSet;
	}

	protected void validateSettings(
			UnicodeProperties oldSettingsUnicodeProperties,
			UnicodeProperties newSettingsUnicodeProperties)
		throws PortalException {

		boolean enableJavaScript =
			PropsValues.
				FIELD_ENABLE_COM_LIFERAY_PORTAL_KERNEL_MODEL_LAYOUTSET_JAVASCRIPT;

		if (enableJavaScript) {
			String javaScript = newSettingsUnicodeProperties.getProperty(
				"javascript");

			if (Validator.isNotNull(javaScript) &&
				(javaScript.contains("<script") ||
				 javaScript.contains("</script>"))) {

				throw new LayoutSetJavaScriptException();
			}
		}
		else {
			String javaScript = oldSettingsUnicodeProperties.getProperty(
				"javascript");

			newSettingsUnicodeProperties.setProperty("javascript", javaScript);
		}
	}

	private LayoutSetBranch _getLayoutSetBranch(LayoutSet layoutSet)
		throws PortalException {

		LayoutSetStagingHandler layoutSetStagingHandler =
			LayoutStagingUtil.getLayoutSetStagingHandler(layoutSet);

		if (layoutSetStagingHandler != null) {
			return layoutSetStagingHandler.getLayoutSetBranch();
		}

		if (LayoutStagingUtil.isBranchingLayoutSet(
				layoutSet.getGroup(), layoutSet.isPrivateLayout())) {

			layoutSetStagingHandler = new LayoutSetStagingHandler(layoutSet);

			return layoutSetStagingHandler.getLayoutSetBranch();
		}

		return null;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		LayoutSetLocalServiceImpl.class);

	private static final Snapshot<Sites> _sitesSnapshot = new Snapshot<>(
		LayoutSetLocalServiceImpl.class, Sites.class);

	@BeanReference(type = GroupPersistence.class)
	private GroupPersistence _groupPersistence;

	@BeanReference(type = ImageLocalService.class)
	private ImageLocalService _imageLocalService;

	@BeanReference(type = LayoutLocalService.class)
	private LayoutLocalService _layoutLocalService;

	@BeanReference(type = LayoutPersistence.class)
	private LayoutPersistence _layoutPersistence;

	@BeanReference(type = LayoutSetBranchPersistence.class)
	private LayoutSetBranchPersistence _layoutSetBranchPersistence;

	@BeanReference(type = VirtualHostLocalService.class)
	private VirtualHostLocalService _virtualHostLocalService;

	@BeanReference(type = VirtualHostPersistence.class)
	private VirtualHostPersistence _virtualHostPersistence;

}