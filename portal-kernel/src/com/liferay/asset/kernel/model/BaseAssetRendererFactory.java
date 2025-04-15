/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.asset.kernel.model;

import com.liferay.asset.kernel.service.AssetEntryLocalServiceUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Portlet;
import com.liferay.portal.kernel.portlet.LiferayPortletRequest;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.portlet.PortletBag;
import com.liferay.portal.kernel.portlet.PortletBagPool;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.ResourceActionsUtil;
import com.liferay.portal.kernel.service.PortletLocalServiceUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.ResourceBundleUtil;
import com.liferay.portal.kernel.util.Tuple;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.PortletURL;
import jakarta.portlet.WindowState;

import java.util.Locale;
import java.util.ResourceBundle;

/**
 * @author Jorge Ferrer
 * @author Juan Fernández
 * @author Raymond Augé
 * @author Sergio González
 */
public abstract class BaseAssetRendererFactory<T>
	implements AssetRendererFactory<T> {

	@Override
	public AssetEntry getAssetEntry(long assetEntryId) throws PortalException {
		return AssetEntryLocalServiceUtil.getEntry(assetEntryId);
	}

	@Override
	public AssetEntry getAssetEntry(String className, long classPK)
		throws PortalException {

		return AssetEntryLocalServiceUtil.getEntry(className, classPK);
	}

	@Override
	public AssetRenderer<T> getAssetRenderer(long classPK)
		throws PortalException {

		return getAssetRenderer(classPK, TYPE_LATEST_APPROVED);
	}

	@Override
	public AssetRenderer<T> getAssetRenderer(long groupId, String urlTitle)
		throws PortalException {

		return null;
	}

	@Override
	public AssetRenderer<T> getAssetRenderer(T entry, int type)
		throws PortalException {

		return null;
	}

	@Override
	public String getClassName() {
		return _className;
	}

	@Override
	public long getClassNameId() {
		return PortalUtil.getClassNameId(getClassName());
	}

	@Override
	public ClassTypeReader getClassTypeReader() {
		return new NullClassTypeReader();
	}

	@Override
	public String getIconCssClass() {
		return null;
	}

	@Override
	public String getPortletId() {
		return _portletId;
	}

	@Override
	public String getSubtypeTitle(Locale locale) {
		return LanguageUtil.get(locale, "subtype");
	}

	@Override
	public String getTypeName(Locale locale) {
		String modelResourceNamePrefix =
			ResourceActionsUtil.getModelResourceNamePrefix();

		String key = modelResourceNamePrefix.concat(getClassName());

		String value = LanguageUtil.get(locale, key, null);

		String portletId = getPortletId();

		if ((value == null) && (portletId != null)) {
			PortletBag portletBag = PortletBagPool.get(portletId);

			ResourceBundle resourceBundle = portletBag.getResourceBundle(
				locale);

			if (resourceBundle != null) {
				value = ResourceBundleUtil.getString(resourceBundle, key);
			}
		}

		if (value == null) {
			value = getClassName();
		}

		return value;
	}

	@Override
	public String getTypeName(Locale locale, long subtypeId) {
		return getTypeName(locale);
	}

	@Override
	public PortletURL getURLAdd(
			LiferayPortletRequest liferayPortletRequest,
			LiferayPortletResponse liferayPortletResponse, long classTypeId)
		throws PortalException {

		return null;
	}

	@Override
	public PortletURL getURLView(
			LiferayPortletResponse liferayPortletResponse,
			WindowState windowState)
		throws PortalException {

		return null;
	}

	@Override
	public boolean hasAddPermission(
			PermissionChecker permissionChecker, long groupId, long classTypeId)
		throws Exception {

		return false;
	}

	@Override
	public boolean hasPermission(
			PermissionChecker permissionChecker, long classPK, String actionId)
		throws Exception {

		return _PERMISSION;
	}

	@Override
	public boolean isActive(long companyId) {
		if (Validator.isNull(getPortletId())) {
			return true;
		}

		Portlet portlet = PortletLocalServiceUtil.fetchPortletById(
			companyId, getPortletId());

		if (portlet == null) {
			return true;
		}

		return portlet.isActive();
	}

	@Override
	public boolean isCategorizable() {
		return _categorizable;
	}

	@Override
	public boolean isLinkable() {
		return _linkable;
	}

	@Override
	public boolean isSearchable() {
		return _searchable;
	}

	@Override
	public boolean isSelectable() {
		return _selectable;
	}

	@Override
	public boolean isSupportsClassTypes() {
		return _supportsClassTypes;
	}

	@Override
	public void setClassName(String className) {
		_className = className;
	}

	@Override
	public void setPortletId(String portletId) {
		_portletId = portletId;
	}

	protected long getControlPanelPlid(ThemeDisplay themeDisplay)
		throws PortalException {

		return PortalUtil.getControlPanelPlid(themeDisplay.getCompanyId());
	}

	protected Group getGroup(LiferayPortletRequest liferayPortletRequest) {
		return (Group)liferayPortletRequest.getAttribute(
			WebKeys.ASSET_RENDERER_FACTORY_GROUP);
	}

	protected void setCategorizable(boolean categorizable) {
		_categorizable = categorizable;
	}

	protected void setLinkable(boolean linkable) {
		_linkable = linkable;
	}

	protected void setSearchable(boolean searchable) {
		_searchable = searchable;
	}

	protected void setSelectable(boolean selectable) {
		_selectable = selectable;
	}

	protected void setSupportsClassTypes(boolean supportsClassTypes) {
		_supportsClassTypes = supportsClassTypes;
	}

	protected Tuple toTuple(ClassTypeField classTypeField) {
		return new Tuple(
			classTypeField.getLabel(), classTypeField.getName(),
			classTypeField.getType(), classTypeField.getClassTypeId());
	}

	private static final boolean _PERMISSION = true;

	private boolean _categorizable = true;
	private String _className;
	private boolean _linkable;
	private String _portletId;
	private boolean _searchable;
	private boolean _selectable = true;
	private boolean _supportsClassTypes;

}