/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.asset.test.util.asset.renderer.factory;

import com.liferay.asset.kernel.model.AssetEntry;
import com.liferay.asset.kernel.model.AssetRenderer;
import com.liferay.asset.kernel.model.AssetRendererFactory;
import com.liferay.asset.kernel.model.ClassTypeReader;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.portlet.LiferayPortletRequest;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.security.permission.PermissionChecker;

import jakarta.portlet.PortletURL;
import jakarta.portlet.WindowState;

import java.util.Locale;

import org.osgi.service.component.annotations.Component;

/**
 * @author Peter Fellwock
 */
@Component(
	property = "service.ranking:Integer=" + Integer.MAX_VALUE,
	service = AssetRendererFactory.class
)
public class TestAssetRendererFactory
	implements AssetRendererFactory<TestAssetRendererModel> {

	@Override
	public AssetEntry getAssetEntry(long assetEntryId) {
		return null;
	}

	@Override
	public AssetEntry getAssetEntry(String classNameId, long classPK) {
		return null;
	}

	@Override
	public AssetRenderer<TestAssetRendererModel> getAssetRenderer(
		long classPK) {

		return null;
	}

	@Override
	public AssetRenderer<TestAssetRendererModel> getAssetRenderer(
		long classPK, int type) {

		return null;
	}

	@Override
	public AssetRenderer<TestAssetRendererModel> getAssetRenderer(
		long groupId, String urlTitle) {

		return null;
	}

	@Override
	public AssetRenderer<TestAssetRendererModel> getAssetRenderer(
			TestAssetRendererModel entry, int type)
		throws PortalException {

		return null;
	}

	@Override
	public String getClassName() {
		return TestAssetRendererFactory.class.getName();
	}

	@Override
	public long getClassNameId() {
		return 1234567890;
	}

	@Override
	public ClassTypeReader getClassTypeReader() {
		return null;
	}

	@Override
	public String getIconCssClass() {
		return null;
	}

	@Override
	public String getPortletId() {
		return null;
	}

	@Override
	public String getSubtypeTitle(Locale locale) {
		return StringPool.BLANK;
	}

	@Override
	public String getType() {
		return TestAssetRendererFactory.class.getName();
	}

	@Override
	public String getTypeName(Locale locale) {
		return null;
	}

	@Override
	public String getTypeName(Locale locale, long subtypeId) {
		return null;
	}

	@Override
	public PortletURL getURLAdd(
		LiferayPortletRequest liferayPortletRequest,
		LiferayPortletResponse liferayPortletResponse, long classTypeId) {

		return null;
	}

	@Override
	public PortletURL getURLView(
		LiferayPortletResponse liferayPortletResponse,
		WindowState windowState) {

		return null;
	}

	@Override
	public boolean hasAddPermission(
		PermissionChecker permissionChecker, long groupId, long classTypeId) {

		return false;
	}

	@Override
	public boolean hasPermission(
		PermissionChecker permissionChecker, long entryClassPK,
		String actionId) {

		return false;
	}

	@Override
	public boolean isActive(long companyId) {
		if (companyId == 1) {
			return true;
		}

		return false;
	}

	@Override
	public boolean isCategorizable() {
		return false;
	}

	@Override
	public boolean isLinkable() {
		return false;
	}

	@Override
	public boolean isSearchable() {
		return false;
	}

	@Override
	public boolean isSelectable() {
		return true;
	}

	@Override
	public boolean isSupportsClassTypes() {
		return false;
	}

	@Override
	public void setClassName(String className) {
	}

	@Override
	public void setPortletId(String portletId) {
	}

}