/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.depot.web.internal.asset.model;

import com.liferay.asset.kernel.model.AssetEntry;
import com.liferay.asset.kernel.model.AssetRenderer;
import com.liferay.asset.kernel.model.AssetRendererFactory;
import com.liferay.asset.kernel.model.ClassTypeReader;
import com.liferay.asset.util.AssetRendererFactoryWrapper;
import com.liferay.depot.constants.DepotConstants;
import com.liferay.depot.group.provider.SiteConnectedGroupGroupProvider;
import com.liferay.depot.model.DepotEntry;
import com.liferay.depot.service.DepotEntryLocalService;
import com.liferay.depot.web.internal.application.controller.DepotApplicationController;
import com.liferay.layout.page.template.model.LayoutPageTemplateEntry;
import com.liferay.layout.page.template.service.LayoutPageTemplateEntryLocalService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.LayoutPrototype;
import com.liferay.portal.kernel.portlet.LiferayPortletRequest;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.LayoutPrototypeLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.GroupThreadLocal;

import jakarta.portlet.PortletURL;
import jakarta.portlet.WindowState;

import java.util.Locale;

/**
 * @author Adolfo Pérez
 */
public class DepotAssetRendererFactoryWrapper<T>
	implements AssetRendererFactoryWrapper<T> {

	public DepotAssetRendererFactoryWrapper(
		AssetRendererFactory<T> assetRendererFactory,
		DepotApplicationController depotApplicationController,
		DepotEntryLocalService depotEntryLocalService,
		GroupLocalService groupLocalService,
		LayoutPageTemplateEntryLocalService layoutPageTemplateEntryLocalService,
		LayoutPrototypeLocalService layoutPrototypeLocalService,
		SiteConnectedGroupGroupProvider siteConnectedGroupGroupProvider) {

		_assetRendererFactory = assetRendererFactory;
		_depotApplicationController = depotApplicationController;
		_depotEntryLocalService = depotEntryLocalService;
		_groupLocalService = groupLocalService;
		_layoutPageTemplateEntryLocalService =
			layoutPageTemplateEntryLocalService;
		_layoutPrototypeLocalService = layoutPrototypeLocalService;
		_siteConnectedGroupGroupProvider = siteConnectedGroupGroupProvider;
	}

	@Override
	public AssetEntry getAssetEntry(long assetEntryId) throws PortalException {
		return _assetRendererFactory.getAssetEntry(assetEntryId);
	}

	@Override
	public AssetEntry getAssetEntry(String classNameId, long classPK)
		throws PortalException {

		return _assetRendererFactory.getAssetEntry(classNameId, classPK);
	}

	@Override
	public AssetEntry getAssetEntry(T entry) throws PortalException {
		return _assetRendererFactory.getAssetEntry(entry);
	}

	@Override
	public AssetRenderer<T> getAssetRenderer(long classPK)
		throws PortalException {

		AssetRenderer<T> assetRenderer = _assetRendererFactory.getAssetRenderer(
			classPK);

		if (assetRenderer == null) {
			return null;
		}

		long groupId = assetRenderer.getGroupId();

		Group assetRendererGroup = _groupLocalService.fetchGroup(groupId);

		if ((assetRendererGroup == null) || !assetRendererGroup.isDepot()) {
			return assetRenderer;
		}

		Group group = _getGroup(assetRendererGroup);

		if (group == null) {
			return null;
		}

		if (group.isControlPanel() ||
			ArrayUtil.contains(
				_siteConnectedGroupGroupProvider.
					getCurrentAndAncestorSiteAndDepotGroupIds(
						_getGroupId(group.getGroupId())),
				groupId)) {

			return assetRenderer;
		}

		if (!FeatureFlagManagerUtil.isEnabled(
				group.getCompanyId(), "LPD-17564")) {

			return null;
		}

		DepotEntry depotEntry = _depotEntryLocalService.getGroupDepotEntry(
			groupId);

		if (depotEntry.getType() == DepotConstants.TYPE_SPACE) {
			return assetRenderer;
		}

		return null;
	}

	@Override
	public AssetRenderer<T> getAssetRenderer(long classPK, int type)
		throws PortalException {

		return _assetRendererFactory.getAssetRenderer(classPK, type);
	}

	@Override
	public AssetRenderer<T> getAssetRenderer(long groupId, String urlTitle)
		throws PortalException {

		return _assetRendererFactory.getAssetRenderer(groupId, urlTitle);
	}

	@Override
	public AssetRenderer<T> getAssetRenderer(T entry, int type)
		throws PortalException {

		return _assetRendererFactory.getAssetRenderer(entry, type);
	}

	@Override
	public String getClassName() {
		return _assetRendererFactory.getClassName();
	}

	@Override
	public long getClassNameId() {
		return _assetRendererFactory.getClassNameId();
	}

	@Override
	public ClassTypeReader getClassTypeReader() {
		if (isSelectable()) {
			return new DepotClassTypeReader(
				_assetRendererFactory.getClassTypeReader(),
				_depotEntryLocalService);
		}

		return _assetRendererFactory.getClassTypeReader();
	}

	@Override
	public String getIconCssClass() {
		return _assetRendererFactory.getIconCssClass();
	}

	@Override
	public PortletURL getItemSelectorURL(
		LiferayPortletRequest liferayPortletRequest,
		LiferayPortletResponse liferayPortletResponse, long classTypeId,
		String eventName, Group group, boolean multiSelection,
		long refererAssetEntryId) {

		return _assetRendererFactory.getItemSelectorURL(
			liferayPortletRequest, liferayPortletResponse, classTypeId,
			eventName, group, multiSelection, refererAssetEntryId);
	}

	@Override
	public String getPortletId() {
		return _assetRendererFactory.getPortletId();
	}

	@Override
	public String getSubtypeTitle(Locale locale) {
		return _assetRendererFactory.getSubtypeTitle(locale);
	}

	@Override
	public String getType() {
		return _assetRendererFactory.getType();
	}

	@Override
	public String getTypeName(Locale locale) {
		return _assetRendererFactory.getTypeName(locale);
	}

	@Override
	public String getTypeName(Locale locale, long subtypeId) {
		return _assetRendererFactory.getTypeName(locale, subtypeId);
	}

	@Override
	public PortletURL getURLAdd(
			LiferayPortletRequest liferayPortletRequest,
			LiferayPortletResponse liferayPortletResponse, long classTypeId)
		throws PortalException {

		return _assetRendererFactory.getURLAdd(
			liferayPortletRequest, liferayPortletResponse, classTypeId);
	}

	@Override
	public PortletURL getURLView(
			LiferayPortletResponse liferayPortletResponse,
			WindowState windowState)
		throws PortalException {

		return _assetRendererFactory.getURLView(
			liferayPortletResponse, windowState);
	}

	@Override
	public Class<? extends AssetRendererFactory> getWrappedClass() {
		return _assetRendererFactory.getClass();
	}

	@Override
	public boolean hasAddPermission(
			PermissionChecker permissionChecker, long groupId, long classTypeId)
		throws Exception {

		return _assetRendererFactory.hasAddPermission(
			permissionChecker, groupId, classTypeId);
	}

	@Override
	public boolean hasPermission(
			PermissionChecker permissionChecker, long entryClassPK,
			String actionId)
		throws Exception {

		return _assetRendererFactory.hasPermission(
			permissionChecker, entryClassPK, actionId);
	}

	@Override
	public boolean isActive(long companyId) {
		return _assetRendererFactory.isActive(companyId);
	}

	@Override
	public boolean isCategorizable() {
		return _assetRendererFactory.isCategorizable();
	}

	@Override
	public boolean isLinkable() {
		return _assetRendererFactory.isLinkable();
	}

	@Override
	public boolean isSearchable() {
		return _assetRendererFactory.isSearchable();
	}

	@Override
	public boolean isSelectable() {
		Group group = _getGroup(null);

		if ((group != null) && group.isDepot() &&
			!_depotApplicationController.isClassNameEnabled(
				getClassName(), group.getGroupId())) {

			return false;
		}

		return _assetRendererFactory.isSelectable();
	}

	@Override
	public boolean isSupportsClassTypes() {
		return _assetRendererFactory.isSupportsClassTypes();
	}

	@Override
	public void setClassName(String className) {
		_assetRendererFactory.setClassName(className);
	}

	@Override
	public void setPortletId(String portletId) {
		_assetRendererFactory.setPortletId(portletId);
	}

	private Group _getGroup(Group fallbackGroup) {
		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		if (serviceContext != null) {
			long scopeGroupId = GetterUtil.getLong(
				serviceContext.getAttribute("scopeGroupId"));

			if (scopeGroupId != 0) {
				return _groupLocalService.fetchGroup(scopeGroupId);
			}

			if (serviceContext.getScopeGroupId() != 0) {
				return _groupLocalService.fetchGroup(
					serviceContext.getScopeGroupId());
			}
		}

		Group group = _groupLocalService.fetchGroup(
			GroupThreadLocal.getGroupId());

		if (group != null) {
			return group;
		}

		return fallbackGroup;
	}

	private long _getGroupId(long groupId) throws PortalException {
		Group group = _groupLocalService.getGroup(groupId);

		if (group.isLayoutPrototype()) {
			LayoutPrototype layoutPrototype =
				_layoutPrototypeLocalService.getLayoutPrototype(
					group.getClassPK());

			LayoutPageTemplateEntry layoutPageTemplateEntry =
				_layoutPageTemplateEntryLocalService.
					fetchFirstLayoutPageTemplateEntry(
						layoutPrototype.getLayoutPrototypeId());

			if ((layoutPageTemplateEntry != null) &&
				(layoutPageTemplateEntry.getGroupId() > 0)) {

				group = _groupLocalService.getGroup(
					layoutPageTemplateEntry.getGroupId());
			}
		}

		return group.getGroupId();
	}

	private final AssetRendererFactory<T> _assetRendererFactory;
	private final DepotApplicationController _depotApplicationController;
	private final DepotEntryLocalService _depotEntryLocalService;
	private final GroupLocalService _groupLocalService;
	private final LayoutPageTemplateEntryLocalService
		_layoutPageTemplateEntryLocalService;
	private final LayoutPrototypeLocalService _layoutPrototypeLocalService;
	private final SiteConnectedGroupGroupProvider
		_siteConnectedGroupGroupProvider;

}