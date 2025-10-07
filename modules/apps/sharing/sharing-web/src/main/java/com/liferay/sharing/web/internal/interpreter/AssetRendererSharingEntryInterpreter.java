/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.sharing.web.internal.interpreter;

import com.liferay.asset.kernel.model.AssetEntry;
import com.liferay.asset.kernel.model.AssetRenderer;
import com.liferay.asset.kernel.model.AssetRendererFactory;
import com.liferay.asset.kernel.service.AssetEntryLocalService;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.sharing.interpreter.SharingEntryInterpreter;
import com.liferay.sharing.model.SharingEntry;
import com.liferay.sharing.renderer.SharingEntryEditRenderer;
import com.liferay.sharing.renderer.SharingEntryViewRenderer;
import com.liferay.sharing.web.internal.renderer.AssetRendererSharingEntryEditRenderer;
import com.liferay.sharing.web.internal.renderer.AssetRendererSharingEntryViewRenderer;
import com.liferay.sharing.web.internal.util.AssetRendererSharingUtil;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * @author Alejandro Tardín
 */
public class AssetRendererSharingEntryInterpreter
	implements SharingEntryInterpreter {

	public AssetRendererSharingEntryInterpreter(
		AssetEntryLocalService assetEntryLocalService,
		AssetRendererSharingEntryEditRenderer
			assetRendererSharingEntryEditRenderer,
		AssetRendererSharingEntryViewRenderer
			assetRendererSharingEntryViewRenderer) {

		_assetEntryLocalService = assetEntryLocalService;
		_assetRendererSharingEntryEditRenderer =
			assetRendererSharingEntryEditRenderer;
		_assetRendererSharingEntryViewRenderer =
			assetRendererSharingEntryViewRenderer;
	}

	@Override
	public String getAssetTypeTitle(SharingEntry sharingEntry, Locale locale)
		throws PortalException {

		AssetRenderer<?> assetRenderer =
			AssetRendererSharingUtil.getAssetRenderer(sharingEntry);

		if (assetRenderer == null) {
			return StringPool.BLANK;
		}

		AssetRendererFactory<?> assetRendererFactory =
			assetRenderer.getAssetRendererFactory();

		return assetRendererFactory.getTypeName(locale);
	}

	@Override
	public SharingEntryEditRenderer getSharingEntryEditRenderer() {
		return _assetRendererSharingEntryEditRenderer;
	}

	@Override
	public SharingEntryViewRenderer getSharingEntryViewRenderer() {
		return _assetRendererSharingEntryViewRenderer;
	}

	@Override
	public String getTitle(SharingEntry sharingEntry, Locale locale) {
		AssetEntry assetEntry = _getAssetEntry(sharingEntry);

		if (assetEntry == null) {
			return StringPool.BLANK;
		}

		return assetEntry.getTitle(locale);
	}

	@Override
	public Map<Locale, String> getTitleMap(SharingEntry sharingEntry) {
		AssetEntry assetEntry = _getAssetEntry(sharingEntry);

		if (assetEntry == null) {
			return new HashMap<>();
		}

		return assetEntry.getTitleMap();
	}

	@Override
	public boolean isVisible(SharingEntry sharingEntry) throws PortalException {
		AssetRenderer<?> assetRenderer =
			AssetRendererSharingUtil.getAssetRenderer(sharingEntry);

		if ((assetRenderer == null) || !assetRenderer.isDisplayable()) {
			return false;
		}

		AssetEntry assetEntry = _assetEntryLocalService.fetchEntry(
			sharingEntry.getClassNameId(), sharingEntry.getClassPK());

		if ((assetEntry == null) || !assetEntry.isVisible()) {
			return false;
		}

		return true;
	}

	private AssetEntry _getAssetEntry(SharingEntry sharingEntry) {
		try {
			AssetRenderer<?> assetRenderer =
				AssetRendererSharingUtil.getAssetRenderer(sharingEntry);

			if (assetRenderer == null) {
				return null;
			}

			AssetRendererFactory<?> assetRendererFactory =
				assetRenderer.getAssetRendererFactory();

			return assetRendererFactory.getAssetEntry(
				assetRendererFactory.getClassName(),
				assetRenderer.getClassPK());
		}
		catch (PortalException portalException) {
			_log.error(portalException);
		}

		return null;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		AssetRendererSharingEntryInterpreter.class);

	private final AssetEntryLocalService _assetEntryLocalService;
	private final AssetRendererSharingEntryEditRenderer
		_assetRendererSharingEntryEditRenderer;
	private final AssetRendererSharingEntryViewRenderer
		_assetRendererSharingEntryViewRenderer;

}