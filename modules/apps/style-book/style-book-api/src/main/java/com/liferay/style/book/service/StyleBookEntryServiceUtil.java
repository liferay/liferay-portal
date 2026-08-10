/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.style.book.service;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.module.service.Snapshot;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.style.book.model.StyleBookEntry;

import java.util.List;

/**
 * Provides the remote service utility for StyleBookEntry. This utility wraps
 * <code>com.liferay.style.book.service.impl.StyleBookEntryServiceImpl</code> and is an
 * access point for service operations in application layer code running on a
 * remote server. Methods of this service are expected to have security checks
 * based on the propagated JAAS credentials because this service can be
 * accessed remotely.
 *
 * @author Brian Wing Shun Chan
 * @see StyleBookEntryService
 * @generated
 */
public class StyleBookEntryServiceUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Add custom service methods to <code>com.liferay.style.book.service.impl.StyleBookEntryServiceImpl</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static StyleBookEntry addStyleBookEntry(
			String externalReferenceCode, long groupId,
			boolean defaultStyleBookEntry, String frontendTokensValues,
			String name, String styleBookEntryKey, String themeId,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().addStyleBookEntry(
			externalReferenceCode, groupId, defaultStyleBookEntry,
			frontendTokensValues, name, styleBookEntryKey, themeId,
			serviceContext);
	}

	public static StyleBookEntry addStyleBookEntry(
			String externalReferenceCode, long groupId, String name,
			String styleBookEntryKey, String themeId,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().addStyleBookEntry(
			externalReferenceCode, groupId, name, styleBookEntryKey, themeId,
			serviceContext);
	}

	public static StyleBookEntry addStyleBookEntry(
			String externalReferenceCode, long groupId,
			String frontendTokensValues, String name, String styleBookEntryKey,
			String themeId,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().addStyleBookEntry(
			externalReferenceCode, groupId, frontendTokensValues, name,
			styleBookEntryKey, themeId, serviceContext);
	}

	public static StyleBookEntry copyStyleBookEntry(
			long groupId, long sourceStyleBookEntryId,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().copyStyleBookEntry(
			groupId, sourceStyleBookEntryId, serviceContext);
	}

	public static StyleBookEntry deleteStyleBookEntry(long styleBookEntryId)
		throws PortalException {

		return getService().deleteStyleBookEntry(styleBookEntryId);
	}

	public static StyleBookEntry deleteStyleBookEntry(
			String externalReferenceCode, long groupId)
		throws PortalException {

		return getService().deleteStyleBookEntry(
			externalReferenceCode, groupId);
	}

	public static StyleBookEntry deleteStyleBookEntry(
			StyleBookEntry styleBookEntry)
		throws PortalException {

		return getService().deleteStyleBookEntry(styleBookEntry);
	}

	public static StyleBookEntry discardDraftStyleBookEntry(
			long styleBookEntryId)
		throws PortalException {

		return getService().discardDraftStyleBookEntry(styleBookEntryId);
	}

	public static StyleBookEntry fetchStyleBookEntryByExternalReferenceCode(
			String externalReferenceCode, long groupId)
		throws PortalException {

		return getService().fetchStyleBookEntryByExternalReferenceCode(
			externalReferenceCode, groupId);
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	public static String getOSGiServiceIdentifier() {
		return getService().getOSGiServiceIdentifier();
	}

	public static List<StyleBookEntry> getStyleBookEntries(
			long groupId, int start, int end,
			OrderByComparator<StyleBookEntry> orderByComparator)
		throws com.liferay.portal.kernel.security.auth.PrincipalException {

		return getService().getStyleBookEntries(
			groupId, start, end, orderByComparator);
	}

	public static List<StyleBookEntry> getStyleBookEntries(
			long groupId, String name, int start, int end,
			OrderByComparator<StyleBookEntry> orderByComparator)
		throws com.liferay.portal.kernel.security.auth.PrincipalException {

		return getService().getStyleBookEntries(
			groupId, name, start, end, orderByComparator);
	}

	public static int getStyleBookEntriesCount(long groupId)
		throws com.liferay.portal.kernel.security.auth.PrincipalException {

		return getService().getStyleBookEntriesCount(groupId);
	}

	public static int getStyleBookEntriesCount(long groupId, String name)
		throws com.liferay.portal.kernel.security.auth.PrincipalException {

		return getService().getStyleBookEntriesCount(groupId, name);
	}

	public static StyleBookEntry getStyleBookEntry(long styleBookEntryId)
		throws PortalException {

		return getService().getStyleBookEntry(styleBookEntryId);
	}

	public static StyleBookEntry getStyleBookEntryByExternalReferenceCode(
			String externalReferenceCode, long groupId)
		throws PortalException {

		return getService().getStyleBookEntryByExternalReferenceCode(
			externalReferenceCode, groupId);
	}

	public static StyleBookEntry publishDraft(long styleBookEntryId)
		throws PortalException {

		return getService().publishDraft(styleBookEntryId);
	}

	public static StyleBookEntry updateDefaultStyleBookEntry(
			long styleBookEntryId, boolean defaultStyleBookEntry)
		throws PortalException {

		return getService().updateDefaultStyleBookEntry(
			styleBookEntryId, defaultStyleBookEntry);
	}

	public static StyleBookEntry updateFrontendTokenDefinition(
			long styleBookEntryId, String frontendTokenDefinition)
		throws PortalException {

		return getService().updateFrontendTokenDefinition(
			styleBookEntryId, frontendTokenDefinition);
	}

	public static StyleBookEntry updateFrontendTokensValues(
			long styleBookEntryId, String frontendTokensValues)
		throws PortalException {

		return getService().updateFrontendTokensValues(
			styleBookEntryId, frontendTokensValues);
	}

	public static StyleBookEntry updateName(long styleBookEntryId, String name)
		throws PortalException {

		return getService().updateName(styleBookEntryId, name);
	}

	public static StyleBookEntry updatePreviewFileEntryId(
			long styleBookEntryId, long previewFileEntryId,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().updatePreviewFileEntryId(
			styleBookEntryId, previewFileEntryId, serviceContext);
	}

	public static StyleBookEntry updateStyleBookEntry(
			long styleBookEntryId, boolean defaultStylebookEntry,
			String frontendTokensValues, String name, String styleBookEntryKey,
			long previewFileEntryId,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().updateStyleBookEntry(
			styleBookEntryId, defaultStylebookEntry, frontendTokensValues, name,
			styleBookEntryKey, previewFileEntryId, serviceContext);
	}

	public static StyleBookEntry updateStyleBookEntry(
			long styleBookEntryId, String frontendTokensValues, String name,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().updateStyleBookEntry(
			styleBookEntryId, frontendTokensValues, name, serviceContext);
	}

	public static StyleBookEntryService getService() {
		return _serviceSnapshot.get();
	}

	private static final Snapshot<StyleBookEntryService> _serviceSnapshot =
		new Snapshot<>(
			StyleBookEntryServiceUtil.class, StyleBookEntryService.class);

}
// LIFERAY-SERVICE-BUILDER-HASH:1883450480