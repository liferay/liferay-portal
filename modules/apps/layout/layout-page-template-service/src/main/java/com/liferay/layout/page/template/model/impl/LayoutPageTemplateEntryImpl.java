/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.page.template.model.impl;

import com.liferay.document.library.util.DLURLHelperUtil;
import com.liferay.info.item.InfoItemFormVariation;
import com.liferay.info.item.InfoItemServiceRegistry;
import com.liferay.info.item.provider.InfoItemFormVariationsProvider;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.module.service.Snapshot;
import com.liferay.portal.kernel.portletfilerepository.PortletFileRepositoryUtil;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.Validator;

/**
 * @author Jürgen Kappler
 */
public class LayoutPageTemplateEntryImpl
	extends LayoutPageTemplateEntryBaseImpl {

	@Override
	public String getClassName() {
		if (getClassNameId() <= 0) {
			return StringPool.BLANK;
		}

		return PortalUtil.fetchClassName(getClassNameId());
	}

	@Override
	public long getClassTypeId() {
		if (super.getClassTypeId() > 0) {
			return super.getClassTypeId();
		}

		String classTypeKey = getClassTypeKey();

		if (Validator.isNull(classTypeKey)) {
			return 0;
		}

		InfoItemServiceRegistry infoItemServiceRegistry =
			_infoItemServiceRegistrySnapshot.get();

		if (infoItemServiceRegistry == null) {
			return 0;
		}

		InfoItemFormVariationsProvider<?> infoItemFormVariationsProvider =
			infoItemServiceRegistry.getFirstInfoItemService(
				InfoItemFormVariationsProvider.class, getClassName());

		if (infoItemFormVariationsProvider == null) {
			return 0;
		}

		InfoItemFormVariation infoItemFormVariation =
			infoItemFormVariationsProvider.
				getInfoItemFormVariationByExternalReferenceCode(
					classTypeKey, getGroupId());

		if (infoItemFormVariation == null) {
			return 0;
		}

		return GetterUtil.getLong(infoItemFormVariation.getKey());
	}

	@Override
	public String getImagePreviewURL(ThemeDisplay themeDisplay) {
		if (getPreviewFileEntryId() <= 0) {
			return StringPool.BLANK;
		}

		try {
			FileEntry fileEntry = PortletFileRepositoryUtil.getPortletFileEntry(
				getPreviewFileEntryId());

			if (fileEntry == null) {
				return StringPool.BLANK;
			}

			return DLURLHelperUtil.getImagePreviewURL(fileEntry, themeDisplay);
		}
		catch (Exception exception) {
			_log.error("Unable to get image preview URL", exception);
		}

		return StringPool.BLANK;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		LayoutPageTemplateEntryImpl.class);

	private static final Snapshot<InfoItemServiceRegistry>
		_infoItemServiceRegistrySnapshot = new Snapshot<>(
			LayoutPageTemplateEntryImpl.class, InfoItemServiceRegistry.class);

}