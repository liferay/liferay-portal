/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.style.book.service.impl;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.aop.AopService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.security.permission.resource.PortletResourcePermission;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.style.book.constants.StyleBookActionKeys;
import com.liferay.style.book.constants.StyleBookConstants;
import com.liferay.style.book.model.StyleBookEntry;
import com.liferay.style.book.service.base.StyleBookEntryServiceBaseImpl;

import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Eudaldo Alonso
 * @see    StyleBookEntryServiceBaseImpl
 */
@Component(
	property = {
		"json.web.service.context.name=stylebook",
		"json.web.service.context.path=StyleBookEntry"
	},
	service = AopService.class
)
public class StyleBookEntryServiceImpl extends StyleBookEntryServiceBaseImpl {

	@Override
	public StyleBookEntry addStyleBookEntry(
			String externalReferenceCode, long groupId,
			boolean defaultStyleBookEntry, String frontendTokensValues,
			String name, String styleBookEntryKey, String themeId,
			ServiceContext serviceContext)
		throws PortalException {

		_portletResourcePermission.check(
			getPermissionChecker(), groupId,
			StyleBookActionKeys.MANAGE_STYLE_BOOK_ENTRIES);

		return styleBookEntryLocalService.addStyleBookEntry(
			externalReferenceCode, getUserId(), groupId, defaultStyleBookEntry,
			frontendTokensValues, name, styleBookEntryKey, themeId,
			serviceContext);
	}

	@Override
	public StyleBookEntry addStyleBookEntry(
			String externalReferenceCode, long groupId, String name,
			String styleBookEntryKey, String themeId,
			ServiceContext serviceContext)
		throws PortalException {

		return addStyleBookEntry(
			externalReferenceCode, groupId, StringPool.BLANK, name,
			styleBookEntryKey, themeId, serviceContext);
	}

	@Override
	public StyleBookEntry addStyleBookEntry(
			String externalReferenceCode, long groupId,
			String frontendTokensValues, String name, String styleBookEntryKey,
			String themeId, ServiceContext serviceContext)
		throws PortalException {

		return addStyleBookEntry(
			externalReferenceCode, groupId, false, frontendTokensValues, name,
			styleBookEntryKey, themeId, serviceContext);
	}

	@Override
	public StyleBookEntry copyStyleBookEntry(
			long groupId, long sourceStyleBookEntryId,
			ServiceContext serviceContext)
		throws PortalException {

		_portletResourcePermission.check(
			getPermissionChecker(), groupId,
			StyleBookActionKeys.MANAGE_STYLE_BOOK_ENTRIES);

		return styleBookEntryLocalService.copyStyleBookEntry(
			getUserId(), groupId, sourceStyleBookEntryId, serviceContext);
	}

	@Override
	public StyleBookEntry deleteStyleBookEntry(long styleBookEntryId)
		throws PortalException {

		StyleBookEntry styleBookEntry =
			styleBookEntryPersistence.findByPrimaryKey(styleBookEntryId);

		return deleteStyleBookEntry(styleBookEntry);
	}

	@Override
	public StyleBookEntry deleteStyleBookEntry(
			String externalReferenceCode, long groupId)
		throws PortalException {

		StyleBookEntry styleBookEntry =
			styleBookEntryPersistence.fetchByERC_G_Head(
				externalReferenceCode, groupId, true);

		return deleteStyleBookEntry(styleBookEntry);
	}

	@Override
	public StyleBookEntry deleteStyleBookEntry(StyleBookEntry styleBookEntry)
		throws PortalException {

		_portletResourcePermission.check(
			getPermissionChecker(), styleBookEntry.getGroupId(),
			StyleBookActionKeys.MANAGE_STYLE_BOOK_ENTRIES);

		return styleBookEntryLocalService.deleteStyleBookEntry(styleBookEntry);
	}

	@Override
	public StyleBookEntry discardDraftStyleBookEntry(long styleBookEntryId)
		throws PortalException {

		StyleBookEntry styleBookEntry =
			styleBookEntryPersistence.findByPrimaryKey(styleBookEntryId);

		_portletResourcePermission.check(
			getPermissionChecker(), styleBookEntry.getGroupId(),
			StyleBookActionKeys.MANAGE_STYLE_BOOK_ENTRIES);

		return styleBookEntryLocalService.deleteDraft(styleBookEntry);
	}

	@Override
	public StyleBookEntry fetchStyleBookEntryByExternalReferenceCode(
			String externalReferenceCode, long groupId)
		throws PortalException {

		_portletResourcePermission.check(
			getPermissionChecker(), groupId,
			StyleBookActionKeys.MANAGE_STYLE_BOOK_ENTRIES);

		return styleBookEntryLocalService.
			fetchStyleBookEntryByExternalReferenceCode(
				externalReferenceCode, groupId);
	}

	@Override
	public List<StyleBookEntry> getStyleBookEntries(
			long groupId, int start, int end,
			OrderByComparator<StyleBookEntry> orderByComparator)
		throws PrincipalException {

		_portletResourcePermission.check(
			getPermissionChecker(), groupId,
			StyleBookActionKeys.MANAGE_STYLE_BOOK_ENTRIES);

		return styleBookEntryLocalService.getStyleBookEntries(
			groupId, start, end, orderByComparator);
	}

	@Override
	public List<StyleBookEntry> getStyleBookEntries(
			long groupId, String name, int start, int end,
			OrderByComparator<StyleBookEntry> orderByComparator)
		throws PrincipalException {

		_portletResourcePermission.check(
			getPermissionChecker(), groupId,
			StyleBookActionKeys.MANAGE_STYLE_BOOK_ENTRIES);

		return styleBookEntryLocalService.getStyleBookEntries(
			groupId, name, start, end, orderByComparator);
	}

	@Override
	public int getStyleBookEntriesCount(long groupId)
		throws PrincipalException {

		_portletResourcePermission.check(
			getPermissionChecker(), groupId,
			StyleBookActionKeys.MANAGE_STYLE_BOOK_ENTRIES);

		return styleBookEntryPersistence.countByGroupId_Head(groupId, true);
	}

	@Override
	public int getStyleBookEntriesCount(long groupId, String name)
		throws PrincipalException {

		_portletResourcePermission.check(
			getPermissionChecker(), groupId,
			StyleBookActionKeys.MANAGE_STYLE_BOOK_ENTRIES);

		return styleBookEntryLocalService.getStyleBookEntriesCount(
			groupId, name);
	}

	@Override
	public StyleBookEntry getStyleBookEntry(long styleBookEntryId)
		throws PortalException {

		StyleBookEntry styleBookEntry =
			styleBookEntryPersistence.findByPrimaryKey(styleBookEntryId);

		_portletResourcePermission.check(
			getPermissionChecker(), styleBookEntry.getGroupId(),
			StyleBookActionKeys.MANAGE_STYLE_BOOK_ENTRIES);

		return styleBookEntry;
	}

	@Override
	public StyleBookEntry getStyleBookEntryByExternalReferenceCode(
			String externalReferenceCode, long groupId)
		throws PortalException {

		_portletResourcePermission.check(
			getPermissionChecker(), groupId,
			StyleBookActionKeys.MANAGE_STYLE_BOOK_ENTRIES);

		return styleBookEntryLocalService.
			getStyleBookEntryByExternalReferenceCode(
				externalReferenceCode, groupId);
	}

	@Override
	public StyleBookEntry publishDraft(long styleBookEntryId)
		throws PortalException {

		StyleBookEntry styleBookEntry =
			styleBookEntryPersistence.findByPrimaryKey(styleBookEntryId);

		_portletResourcePermission.check(
			getPermissionChecker(), styleBookEntry.getGroupId(),
			StyleBookActionKeys.MANAGE_STYLE_BOOK_ENTRIES);

		return styleBookEntryLocalService.publishDraft(styleBookEntry);
	}

	@Override
	public StyleBookEntry updateDefaultStyleBookEntry(
			long styleBookEntryId, boolean defaultStyleBookEntry)
		throws PortalException {

		StyleBookEntry styleBookEntry =
			styleBookEntryPersistence.findByPrimaryKey(styleBookEntryId);

		_portletResourcePermission.check(
			getPermissionChecker(), styleBookEntry.getGroupId(),
			StyleBookActionKeys.MANAGE_STYLE_BOOK_ENTRIES);

		return styleBookEntryLocalService.updateDefaultStyleBookEntry(
			styleBookEntryId, defaultStyleBookEntry);
	}

	@Override
	public StyleBookEntry updateFrontendTokenDefinition(
			long styleBookEntryId, String frontendTokenDefinition,
			ServiceContext serviceContext)
		throws PortalException {

		StyleBookEntry styleBookEntry =
			styleBookEntryPersistence.findByPrimaryKey(styleBookEntryId);

		_portletResourcePermission.check(
			getPermissionChecker(), styleBookEntry.getGroupId(),
			StyleBookActionKeys.MANAGE_STYLE_BOOK_ENTRIES);

		return styleBookEntryLocalService.updateFrontendTokenDefinition(
			styleBookEntryId, frontendTokenDefinition, serviceContext);
	}

	@Override
	public StyleBookEntry updateFrontendTokensValues(
			long styleBookEntryId, String frontendTokensValues)
		throws PortalException {

		StyleBookEntry styleBookEntry =
			styleBookEntryPersistence.findByPrimaryKey(styleBookEntryId);

		_portletResourcePermission.check(
			getPermissionChecker(), styleBookEntry.getGroupId(),
			StyleBookActionKeys.MANAGE_STYLE_BOOK_ENTRIES);

		return styleBookEntryLocalService.updateFrontendTokensValues(
			styleBookEntryId, frontendTokensValues);
	}

	@Override
	public StyleBookEntry updateName(long styleBookEntryId, String name)
		throws PortalException {

		StyleBookEntry styleBookEntry =
			styleBookEntryPersistence.findByPrimaryKey(styleBookEntryId);

		_portletResourcePermission.check(
			getPermissionChecker(), styleBookEntry.getGroupId(),
			StyleBookActionKeys.MANAGE_STYLE_BOOK_ENTRIES);

		return styleBookEntryLocalService.updateName(styleBookEntryId, name);
	}

	@Override
	public StyleBookEntry updatePreviewFileEntryId(
			long styleBookEntryId, long previewFileEntryId,
			ServiceContext serviceContext)
		throws PortalException {

		StyleBookEntry styleBookEntry =
			styleBookEntryPersistence.findByPrimaryKey(styleBookEntryId);

		_portletResourcePermission.check(
			getPermissionChecker(), styleBookEntry.getGroupId(),
			StyleBookActionKeys.MANAGE_STYLE_BOOK_ENTRIES);

		return styleBookEntryLocalService.updatePreviewFileEntryId(
			styleBookEntryId, previewFileEntryId, serviceContext);
	}

	@Override
	public StyleBookEntry updateStyleBookEntry(
			long styleBookEntryId, boolean defaultStylebookEntry,
			String frontendTokensValues, String name, String styleBookEntryKey,
			long previewFileEntryId, ServiceContext serviceContext)
		throws PortalException {

		StyleBookEntry styleBookEntry =
			styleBookEntryPersistence.findByPrimaryKey(styleBookEntryId);

		_portletResourcePermission.check(
			getPermissionChecker(), styleBookEntry.getGroupId(),
			StyleBookActionKeys.MANAGE_STYLE_BOOK_ENTRIES);

		return styleBookEntryLocalService.updateStyleBookEntry(
			getUserId(), styleBookEntryId, defaultStylebookEntry,
			frontendTokensValues, name, styleBookEntryKey, previewFileEntryId,
			serviceContext);
	}

	@Override
	public StyleBookEntry updateStyleBookEntry(
			long styleBookEntryId, String frontendTokensValues, String name,
			ServiceContext serviceContext)
		throws PortalException {

		StyleBookEntry styleBookEntry =
			styleBookEntryPersistence.findByPrimaryKey(styleBookEntryId);

		_portletResourcePermission.check(
			getPermissionChecker(), styleBookEntry.getGroupId(),
			StyleBookActionKeys.MANAGE_STYLE_BOOK_ENTRIES);

		return styleBookEntryLocalService.updateStyleBookEntry(
			styleBookEntryId, frontendTokensValues, name, serviceContext);
	}

	@Reference(
		target = "(resource.name=" + StyleBookConstants.RESOURCE_NAME + ")"
	)
	private PortletResourcePermission _portletResourcePermission;

}