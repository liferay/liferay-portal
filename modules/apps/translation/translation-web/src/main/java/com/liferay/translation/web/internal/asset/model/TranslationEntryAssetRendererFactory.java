/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.translation.web.internal.asset.model;

import com.liferay.asset.kernel.model.AssetRenderer;
import com.liferay.asset.kernel.model.AssetRendererFactory;
import com.liferay.asset.kernel.model.BaseAssetRendererFactory;
import com.liferay.info.item.InfoItemServiceRegistry;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.translation.constants.TranslationPortletKeys;
import com.liferay.translation.info.field.TranslationInfoFieldChecker;
import com.liferay.translation.model.TranslationEntry;
import com.liferay.translation.service.TranslationEntryLocalService;
import com.liferay.translation.snapshot.TranslationSnapshotProvider;

import jakarta.servlet.ServletContext;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Adolfo Pérez
 */
@Component(
	property = "jakarta.portlet.name=" + TranslationPortletKeys.TRANSLATION,
	service = AssetRendererFactory.class
)
public class TranslationEntryAssetRendererFactory
	extends BaseAssetRendererFactory<TranslationEntry> {

	public TranslationEntryAssetRendererFactory() {
		setClassName(TranslationEntry.class.getName());
		setPortletId(TranslationPortletKeys.TRANSLATION);
	}

	@Override
	public AssetRenderer<TranslationEntry> getAssetRenderer(
			long classPK, int type)
		throws PortalException {

		TranslationEntry translationEntry =
			_translationEntryLocalService.fetchTranslationEntry(classPK);

		if (translationEntry != null) {
			return new TranslationEntryAssetRenderer(
				_infoItemServiceRegistry, _servletContext, translationEntry,
				_translationInfoFieldChecker, _translationSnapshotProvider);
		}

		return null;
	}

	@Override
	public String getIconCssClass() {
		return "automatic-translate";
	}

	@Override
	public String getType() {
		return "translation";
	}

	@Reference
	private InfoItemServiceRegistry _infoItemServiceRegistry;

	@Reference(target = "(osgi.web.symbolicname=com.liferay.translation.web)")
	private ServletContext _servletContext;

	@Reference
	private TranslationEntryLocalService _translationEntryLocalService;

	@Reference
	private TranslationInfoFieldChecker _translationInfoFieldChecker;

	@Reference
	private TranslationSnapshotProvider _translationSnapshotProvider;

}