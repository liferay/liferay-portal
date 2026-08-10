/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.style.book.service;

import com.liferay.portal.kernel.service.ServiceWrapper;
import com.liferay.style.book.model.StyleBookEntry;

/**
 * Provides a wrapper for {@link StyleBookEntryService}.
 *
 * @author Brian Wing Shun Chan
 * @see StyleBookEntryService
 * @generated
 */
public class StyleBookEntryServiceWrapper
	implements ServiceWrapper<StyleBookEntryService>, StyleBookEntryService {

	public StyleBookEntryServiceWrapper() {
		this(null);
	}

	public StyleBookEntryServiceWrapper(
		StyleBookEntryService styleBookEntryService) {

		_styleBookEntryService = styleBookEntryService;
	}

	@Override
	public StyleBookEntry addStyleBookEntry(
			String externalReferenceCode, long groupId,
			boolean defaultStyleBookEntry, String frontendTokensValues,
			String name, String styleBookEntryKey, String themeId,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _styleBookEntryService.addStyleBookEntry(
			externalReferenceCode, groupId, defaultStyleBookEntry,
			frontendTokensValues, name, styleBookEntryKey, themeId,
			serviceContext);
	}

	@Override
	public StyleBookEntry addStyleBookEntry(
			String externalReferenceCode, long groupId, String name,
			String styleBookEntryKey, String themeId,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _styleBookEntryService.addStyleBookEntry(
			externalReferenceCode, groupId, name, styleBookEntryKey, themeId,
			serviceContext);
	}

	@Override
	public StyleBookEntry addStyleBookEntry(
			String externalReferenceCode, long groupId,
			String frontendTokensValues, String name, String styleBookEntryKey,
			String themeId,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _styleBookEntryService.addStyleBookEntry(
			externalReferenceCode, groupId, frontendTokensValues, name,
			styleBookEntryKey, themeId, serviceContext);
	}

	@Override
	public StyleBookEntry copyStyleBookEntry(
			long groupId, long sourceStyleBookEntryId,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _styleBookEntryService.copyStyleBookEntry(
			groupId, sourceStyleBookEntryId, serviceContext);
	}

	@Override
	public StyleBookEntry deleteStyleBookEntry(long styleBookEntryId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _styleBookEntryService.deleteStyleBookEntry(styleBookEntryId);
	}

	@Override
	public StyleBookEntry deleteStyleBookEntry(
			String externalReferenceCode, long groupId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _styleBookEntryService.deleteStyleBookEntry(
			externalReferenceCode, groupId);
	}

	@Override
	public StyleBookEntry deleteStyleBookEntry(StyleBookEntry styleBookEntry)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _styleBookEntryService.deleteStyleBookEntry(styleBookEntry);
	}

	@Override
	public StyleBookEntry discardDraftStyleBookEntry(long styleBookEntryId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _styleBookEntryService.discardDraftStyleBookEntry(
			styleBookEntryId);
	}

	@Override
	public StyleBookEntry fetchStyleBookEntryByExternalReferenceCode(
			String externalReferenceCode, long groupId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _styleBookEntryService.
			fetchStyleBookEntryByExternalReferenceCode(
				externalReferenceCode, groupId);
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	@Override
	public String getOSGiServiceIdentifier() {
		return _styleBookEntryService.getOSGiServiceIdentifier();
	}

	@Override
	public java.util.List<StyleBookEntry> getStyleBookEntries(
			long groupId, int start, int end,
			com.liferay.portal.kernel.util.OrderByComparator<StyleBookEntry>
				orderByComparator)
		throws com.liferay.portal.kernel.security.auth.PrincipalException {

		return _styleBookEntryService.getStyleBookEntries(
			groupId, start, end, orderByComparator);
	}

	@Override
	public java.util.List<StyleBookEntry> getStyleBookEntries(
			long groupId, String name, int start, int end,
			com.liferay.portal.kernel.util.OrderByComparator<StyleBookEntry>
				orderByComparator)
		throws com.liferay.portal.kernel.security.auth.PrincipalException {

		return _styleBookEntryService.getStyleBookEntries(
			groupId, name, start, end, orderByComparator);
	}

	@Override
	public int getStyleBookEntriesCount(long groupId)
		throws com.liferay.portal.kernel.security.auth.PrincipalException {

		return _styleBookEntryService.getStyleBookEntriesCount(groupId);
	}

	@Override
	public int getStyleBookEntriesCount(long groupId, String name)
		throws com.liferay.portal.kernel.security.auth.PrincipalException {

		return _styleBookEntryService.getStyleBookEntriesCount(groupId, name);
	}

	@Override
	public StyleBookEntry getStyleBookEntry(long styleBookEntryId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _styleBookEntryService.getStyleBookEntry(styleBookEntryId);
	}

	@Override
	public StyleBookEntry getStyleBookEntryByExternalReferenceCode(
			String externalReferenceCode, long groupId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _styleBookEntryService.getStyleBookEntryByExternalReferenceCode(
			externalReferenceCode, groupId);
	}

	@Override
	public StyleBookEntry publishDraft(long styleBookEntryId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _styleBookEntryService.publishDraft(styleBookEntryId);
	}

	@Override
	public StyleBookEntry updateDefaultStyleBookEntry(
			long styleBookEntryId, boolean defaultStyleBookEntry)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _styleBookEntryService.updateDefaultStyleBookEntry(
			styleBookEntryId, defaultStyleBookEntry);
	}

	@Override
	public StyleBookEntry updateFrontendTokenDefinition(
			long styleBookEntryId, String frontendTokenDefinition)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _styleBookEntryService.updateFrontendTokenDefinition(
			styleBookEntryId, frontendTokenDefinition);
	}

	@Override
	public StyleBookEntry updateFrontendTokensValues(
			long styleBookEntryId, String frontendTokensValues)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _styleBookEntryService.updateFrontendTokensValues(
			styleBookEntryId, frontendTokensValues);
	}

	@Override
	public StyleBookEntry updateName(long styleBookEntryId, String name)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _styleBookEntryService.updateName(styleBookEntryId, name);
	}

	@Override
	public StyleBookEntry updatePreviewFileEntryId(
			long styleBookEntryId, long previewFileEntryId,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _styleBookEntryService.updatePreviewFileEntryId(
			styleBookEntryId, previewFileEntryId, serviceContext);
	}

	@Override
	public StyleBookEntry updateStyleBookEntry(
			long styleBookEntryId, boolean defaultStylebookEntry,
			String frontendTokensValues, String name, String styleBookEntryKey,
			long previewFileEntryId,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _styleBookEntryService.updateStyleBookEntry(
			styleBookEntryId, defaultStylebookEntry, frontendTokensValues, name,
			styleBookEntryKey, previewFileEntryId, serviceContext);
	}

	@Override
	public StyleBookEntry updateStyleBookEntry(
			long styleBookEntryId, String frontendTokensValues, String name,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _styleBookEntryService.updateStyleBookEntry(
			styleBookEntryId, frontendTokensValues, name, serviceContext);
	}

	@Override
	public StyleBookEntryService getWrappedService() {
		return _styleBookEntryService;
	}

	@Override
	public void setWrappedService(StyleBookEntryService styleBookEntryService) {
		_styleBookEntryService = styleBookEntryService;
	}

	private StyleBookEntryService _styleBookEntryService;

}
// LIFERAY-SERVICE-BUILDER-HASH:-311777720