/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.asset.kernel.model;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.BaseModel;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.ResourcedModel;
import com.liferay.portal.kernel.portlet.LiferayPortletRequest;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.security.permission.PermissionChecker;

import jakarta.portlet.PortletURL;
import jakarta.portlet.WindowState;

import java.util.Locale;

import org.osgi.annotation.versioning.ProviderType;

/**
 * @author Jorge Ferrer
 * @author Juan Fernández
 * @author Raymond Augé
 * @author Sergio González
 */
@ProviderType
public interface AssetRendererFactory<T> {

	public static final int TYPE_LATEST = 0;

	public static final int TYPE_LATEST_APPROVED = 1;

	public AssetEntry getAssetEntry(long assetEntryId) throws PortalException;

	public AssetEntry getAssetEntry(String classNameId, long classPK)
		throws PortalException;

	public default AssetEntry getAssetEntry(T entry) throws PortalException {
		if (entry instanceof ResourcedModel) {
			ResourcedModel resourcedModel = (ResourcedModel)entry;

			return getAssetEntry(
				getClassName(), resourcedModel.getResourcePrimKey());
		}

		if (entry instanceof BaseModel<?>) {
			BaseModel<?> baseModel = (BaseModel<?>)entry;

			return getAssetEntry(
				getClassName(), (Long)baseModel.getPrimaryKeyObj());
		}

		return null;
	}

	public AssetRenderer<T> getAssetRenderer(long classPK)
		throws PortalException;

	public AssetRenderer<T> getAssetRenderer(long classPK, int type)
		throws PortalException;

	public AssetRenderer<T> getAssetRenderer(long groupId, String urlTitle)
		throws PortalException;

	public AssetRenderer<T> getAssetRenderer(T entry, int type)
		throws PortalException;

	public String getClassName();

	public long getClassNameId();

	public ClassTypeReader getClassTypeReader();

	public String getIconCssClass();

	public default PortletURL getItemSelectorURL(
		LiferayPortletRequest liferayPortletRequest,
		LiferayPortletResponse liferayPortletResponse, long classTypeId,
		String eventName, Group group, boolean multiSelection,
		long refererAssetEntryId) {

		return null;
	}

	public String getPortletId();

	public String getSubtypeTitle(Locale locale);

	public String getType();

	public String getTypeName(Locale locale);

	public String getTypeName(Locale locale, long subtypeId);

	public PortletURL getURLAdd(
			LiferayPortletRequest liferayPortletRequest,
			LiferayPortletResponse liferayPortletResponse, long classTypeId)
		throws PortalException;

	public PortletURL getURLView(
			LiferayPortletResponse liferayPortletResponse,
			WindowState windowState)
		throws PortalException;

	public boolean hasAddPermission(
			PermissionChecker permissionChecker, long groupId, long classTypeId)
		throws Exception;

	public boolean hasPermission(
			PermissionChecker permissionChecker, long entryClassPK,
			String actionId)
		throws Exception;

	public boolean isActive(long companyId);

	public boolean isCategorizable();

	public boolean isLinkable();

	public boolean isSearchable();

	public boolean isSelectable();

	public boolean isSupportsClassTypes();

	public void setClassName(String className);

	public void setPortletId(String portletId);

}