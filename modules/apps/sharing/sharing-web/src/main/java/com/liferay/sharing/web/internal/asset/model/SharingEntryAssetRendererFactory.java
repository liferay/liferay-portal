/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.sharing.web.internal.asset.model;

import com.liferay.asset.kernel.model.AssetRenderer;
import com.liferay.asset.kernel.model.AssetRendererFactory;
import com.liferay.asset.kernel.model.BaseAssetRendererFactory;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.sharing.constants.SharingPortletKeys;
import com.liferay.sharing.interpreter.SharingEntryInterpreterProvider;
import com.liferay.sharing.model.SharingEntry;
import com.liferay.sharing.service.SharingEntryLocalService;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Mikel Lorza
 */
@Component(
	property = "jakarta.portlet.name=" + SharingPortletKeys.SHARING,
	service = AssetRendererFactory.class
)
public class SharingEntryAssetRendererFactory
	extends BaseAssetRendererFactory<SharingEntry> {

	public static final String TYPE = "sharing_entry";

	public SharingEntryAssetRendererFactory() {
		setClassName(SharingEntry.class.getName());
		setLinkable(true);
		setPortletId(SharingPortletKeys.SHARING);
	}

	@Override
	public AssetRenderer<SharingEntry> getAssetRenderer(long classPK, int type)
		throws PortalException {

		SharingEntry sharingEntry = _sharingEntryLocalService.getSharingEntry(
			classPK);

		if (!FeatureFlagManagerUtil.isEnabled(
				sharingEntry.getCompanyId(), "LPD-17564")) {

			return null;
		}

		return new SharingEntryAssetRenderer(
			sharingEntry, _sharingEntryInterpreterProvider);
	}

	@Override
	public String getClassName() {
		return SharingEntry.class.getName();
	}

	@Override
	public String getIconCssClass() {
		return "sharingEntry";
	}

	@Override
	public String getType() {
		return TYPE;
	}

	@Override
	public boolean isSearchable() {
		if (FeatureFlagManagerUtil.isEnabled(
				CompanyThreadLocal.getCompanyId(), "LPD-17564")) {

			return true;
		}

		return false;
	}

	@Reference
	private SharingEntryInterpreterProvider _sharingEntryInterpreterProvider;

	@Reference
	private SharingEntryLocalService _sharingEntryLocalService;

}