/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.web.internal.layout.display.page;

import com.liferay.friendly.url.info.item.provider.InfoItemFriendlyURLProvider;
import com.liferay.info.item.ClassPKInfoItemIdentifier;
import com.liferay.info.item.InfoItemIdentifier;
import com.liferay.info.item.InfoItemReference;
import com.liferay.layout.display.page.BaseLayoutDisplayPageProvider;
import com.liferay.layout.display.page.LayoutDisplayPageObjectProvider;
import com.liferay.layout.display.page.LayoutDisplayPageProvider;
import com.liferay.object.model.ObjectEntryFolder;
import com.liferay.object.service.ObjectEntryFolderLocalService;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.portlet.constants.FriendlyURLResolverConstants;
import com.liferay.portal.kernel.util.GetterUtil;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Marco Leo
 */
@Component(service = LayoutDisplayPageProvider.class)
public class ObjectEntryFolderLayoutDisplayPageProvider
	extends BaseLayoutDisplayPageProvider<ObjectEntryFolder> {

	@Override
	public LayoutDisplayPageObjectProvider<ObjectEntryFolder>
		doGetLayoutDisplayPageObjectProvider(
			long groupId, InfoItemReference infoItemReference) {

		InfoItemIdentifier infoItemIdentifier =
			infoItemReference.getInfoItemIdentifier();

		if (!(infoItemIdentifier instanceof ClassPKInfoItemIdentifier)) {
			return null;
		}

		ClassPKInfoItemIdentifier classPKInfoItemIdentifier =
			(ClassPKInfoItemIdentifier)
				infoItemReference.getInfoItemIdentifier();

		ObjectEntryFolder objectEntryFolder =
			_objectEntryFolderLocalService.fetchObjectEntryFolder(
				classPKInfoItemIdentifier.getClassPK());

		if (objectEntryFolder == null) {
			return null;
		}

		return new ObjectEntryFolderLayoutDisplayPageObjectProvider(
			_infoItemFriendlyURLProvider, _language, objectEntryFolder);
	}

	@Override
	public String getClassName() {
		return ObjectEntryFolder.class.getName();
	}

	@Override
	public String getDefaultURLSeparator() {
		return FriendlyURLResolverConstants.URL_SEPARATOR_OBJECT_ENTRY_FOLDER;
	}

	@Override
	public LayoutDisplayPageObjectProvider<ObjectEntryFolder>
		getLayoutDisplayPageObjectProvider(long groupId, String urlTitle) {

		ObjectEntryFolder objectEntryFolder =
			_objectEntryFolderLocalService.fetchObjectEntryFolder(
				GetterUtil.getLong(urlTitle));

		return new ObjectEntryFolderLayoutDisplayPageObjectProvider(
			_infoItemFriendlyURLProvider, _language, objectEntryFolder);
	}

	@Override
	public LayoutDisplayPageObjectProvider<ObjectEntryFolder>
		getLayoutDisplayPageObjectProvider(
			ObjectEntryFolder objectEntryFolder) {

		if (objectEntryFolder == null) {
			return null;
		}

		return new ObjectEntryFolderLayoutDisplayPageObjectProvider(
			_infoItemFriendlyURLProvider, _language, objectEntryFolder);
	}

	@Reference(
		target = "(item.class.name=com.liferay.object.model.ObjectEntryFolder)"
	)
	private InfoItemFriendlyURLProvider<ObjectEntryFolder>
		_infoItemFriendlyURLProvider;

	@Reference
	private Language _language;

	@Reference
	private ObjectEntryFolderLocalService _objectEntryFolderLocalService;

}