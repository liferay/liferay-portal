/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.document.library.web.internal.layout.display.page;

import com.liferay.document.library.kernel.service.DLAppLocalService;
import com.liferay.friendly.url.info.item.provider.InfoItemFriendlyURLProvider;
import com.liferay.friendly.url.model.FriendlyURLEntry;
import com.liferay.friendly.url.service.FriendlyURLEntryLocalService;
import com.liferay.info.item.ClassPKInfoItemIdentifier;
import com.liferay.info.item.ERCInfoItemIdentifier;
import com.liferay.info.item.InfoItemIdentifier;
import com.liferay.info.item.InfoItemReference;
import com.liferay.layout.display.page.BaseLayoutDisplayPageProvider;
import com.liferay.layout.display.page.LayoutDisplayPageObjectProvider;
import com.liferay.layout.display.page.LayoutDisplayPageProvider;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.portlet.constants.FriendlyURLResolverConstants;
import com.liferay.portal.kernel.repository.LocalRepository;
import com.liferay.portal.kernel.repository.RepositoryProvider;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.Validator;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Jürgen Kappler
 */
@Component(service = LayoutDisplayPageProvider.class)
public class FileEntryLayoutDisplayPageProvider
	extends BaseLayoutDisplayPageProvider<FileEntry> {

	@Override
	public String getClassName() {
		return FileEntry.class.getName();
	}

	@Override
	public String getDefaultURLSeparator() {
		return FriendlyURLResolverConstants.URL_SEPARATOR_FILE_ENTRY;
	}

	@Override
	public LayoutDisplayPageObjectProvider<FileEntry>
		getLayoutDisplayPageObjectProvider(FileEntry fileEntry) {

		if (fileEntry.isInTrash()) {
			return null;
		}

		return new FileEntryLayoutDisplayPageObjectProvider(
			fileEntry, _infoItemFriendlyURLProvider, _language);
	}

	@Override
	public LayoutDisplayPageObjectProvider<FileEntry>
		getLayoutDisplayPageObjectProvider(long groupId, String urlTitle) {

		if (urlTitle.contains(StringPool.SLASH)) {
			String[] urlNames = urlTitle.split(StringPool.SLASH);

			if (urlNames.length > 1) {
				Group group = _groupLocalService.fetchFriendlyURLGroup(
					CompanyThreadLocal.getCompanyId(),
					StringPool.SLASH + urlNames[0]);

				if (group != null) {
					return getLayoutDisplayPageObjectProvider(
						group.getGroupId(), urlNames[1]);
				}
			}
		}

		FriendlyURLEntry friendlyURLEntry =
			_friendlyURLEntryLocalService.fetchFriendlyURLEntry(
				groupId, FileEntry.class, urlTitle);

		if (friendlyURLEntry != null) {
			return getLayoutDisplayPageObjectProvider(
				new InfoItemReference(
					FileEntry.class.getName(), friendlyURLEntry.getClassPK()));
		}

		if (!Validator.isNumber(urlTitle)) {
			return null;
		}

		return getLayoutDisplayPageObjectProvider(
			new InfoItemReference(
				FileEntry.class.getName(), GetterUtil.getLong(urlTitle)));
	}

	@Override
	protected FileEntryLayoutDisplayPageObjectProvider
		doGetLayoutDisplayPageObjectProvider(
			long groupId, InfoItemReference infoItemReference) {

		try {
			InfoItemIdentifier infoItemIdentifier =
				infoItemReference.getInfoItemIdentifier();

			if (!(infoItemIdentifier instanceof ClassPKInfoItemIdentifier) &&
				!(infoItemIdentifier instanceof ERCInfoItemIdentifier)) {

				return null;
			}

			FileEntry fileEntry = null;

			if (infoItemIdentifier instanceof ClassPKInfoItemIdentifier) {
				ClassPKInfoItemIdentifier classPKInfoItemIdentifier =
					(ClassPKInfoItemIdentifier)
						infoItemReference.getInfoItemIdentifier();

				LocalRepository localRepository =
					_repositoryProvider.fetchFileEntryLocalRepository(
						classPKInfoItemIdentifier.getClassPK());

				if (localRepository == null) {
					return null;
				}

				fileEntry = localRepository.getFileEntry(
					classPKInfoItemIdentifier.getClassPK());
			}
			else {
				ERCInfoItemIdentifier ercInfoItemIdentifier =
					(ERCInfoItemIdentifier)infoItemIdentifier;

				fileEntry =
					_dlAppLocalService.fetchFileEntryByExternalReferenceCode(
						groupId,
						ercInfoItemIdentifier.getExternalReferenceCode());
			}

			if ((fileEntry == null) || fileEntry.isInTrash()) {
				return null;
			}

			return new FileEntryLayoutDisplayPageObjectProvider(
				fileEntry, _infoItemFriendlyURLProvider, _language);
		}
		catch (PortalException portalException) {
			throw new RuntimeException(portalException);
		}
	}

	@Reference
	private DLAppLocalService _dlAppLocalService;

	@Reference
	private FriendlyURLEntryLocalService _friendlyURLEntryLocalService;

	@Reference
	private GroupLocalService _groupLocalService;

	@Reference(
		target = "(item.class.name=com.liferay.portal.kernel.repository.model.FileEntry)"
	)
	private InfoItemFriendlyURLProvider<FileEntry> _infoItemFriendlyURLProvider;

	@Reference
	private Language _language;

	@Reference
	private RepositoryProvider _repositoryProvider;

}