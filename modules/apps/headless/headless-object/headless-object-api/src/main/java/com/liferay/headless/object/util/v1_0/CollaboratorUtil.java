/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.object.util.v1_0;

import com.liferay.headless.object.dto.v1_0.Collaborator;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.NoSuchGroupException;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.UserGroup;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.UserGroupLocalService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.util.OrderByComparatorFactoryUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.vulcan.accept.language.AcceptLanguage;
import com.liferay.portal.vulcan.dto.converter.DTOConverter;
import com.liferay.portal.vulcan.dto.converter.DTOConverterRegistry;
import com.liferay.portal.vulcan.dto.converter.DefaultDTOConverterContext;
import com.liferay.portal.vulcan.pagination.Page;
import com.liferay.portal.vulcan.pagination.Pagination;
import com.liferay.portal.vulcan.util.GroupUtil;
import com.liferay.sharing.model.SharingEntry;
import com.liferay.sharing.security.permission.SharingEntryAction;
import com.liferay.sharing.service.SharingEntryLocalService;
import com.liferay.sharing.service.SharingEntryService;

import jakarta.ws.rs.core.UriInfo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

/**
 * @author Mikel Lorza
 */
public class CollaboratorUtil {

	public static Collaborator addOrUpdateCollaborator(
			AcceptLanguage acceptLanguage, long classNameId, long classPK,
			Collaborator collaborator, long collaboratorId,
			DTOConverter<SharingEntry, Collaborator> dtoConverter,
			DTOConverterRegistry dtoConverterRegistry, long groupId,
			SharingEntryService sharingEntryService, String type,
			UserGroupLocalService userGroupLocalService, UriInfo uriInfo,
			User user, UserLocalService userLocalService)
		throws Exception {

		_validateType(type);

		return toCollaborator(
			acceptLanguage, dtoConverter, dtoConverterRegistry,
			_addOrUpdateSharingEntry(
				classNameId, classPK, collaborator, collaboratorId, groupId,
				sharingEntryService, type, userGroupLocalService,
				userLocalService),
			uriInfo, user);
	}

	public static Page<Collaborator> addOrUpdateCollaborators(
			AcceptLanguage acceptLanguage, long classNameId, long classPK,
			Collaborator[] collaborators,
			DTOConverter<SharingEntry, Collaborator> dtoConverter,
			DTOConverterRegistry dtoConverterRegistry, long groupId,
			SharingEntryService sharingEntryService, UriInfo uriInfo, User user,
			UserGroupLocalService userGroupLocalService,
			UserLocalService userLocalService)
		throws Exception {

		List<SharingEntry> oldSharingEntries =
			sharingEntryService.getSharingEntries(
				classNameId, classPK, groupId, QueryUtil.ALL_POS,
				QueryUtil.ALL_POS, null);

		List<SharingEntry> newSharingEntries = new ArrayList<>();

		List<Long> sharingEntriesIds = new ArrayList<>();

		for (Collaborator collaborator : collaborators) {
			SharingEntry sharingEntry = _addOrUpdateSharingEntry(
				classNameId, classPK, collaborator, collaborator.getId(),
				groupId, sharingEntryService, collaborator.getType(),
				userGroupLocalService, userLocalService);

			newSharingEntries.add(sharingEntry);
			sharingEntriesIds.add(sharingEntry.getSharingEntryId());
		}

		for (SharingEntry sharingEntry : oldSharingEntries) {
			if (!sharingEntriesIds.contains(sharingEntry.getSharingEntryId())) {
				sharingEntryService.deleteSharingEntry(sharingEntry);
			}
		}

		Collections.sort(
			newSharingEntries,
			Comparator.comparing(
				SharingEntry::getCreateDate, Comparator.reverseOrder()
			).thenComparing(
				SharingEntry::getSharingEntryId, Comparator.reverseOrder()
			));

		return Page.of(
			TransformUtil.transform(
				newSharingEntries,
				sharingEntry -> toCollaborator(
					acceptLanguage, dtoConverter, dtoConverterRegistry,
					sharingEntry, uriInfo, user)));
	}

	public static void deleteCollaborator(
			long classNameId, long classPK, Long collaboratorId,
			SharingEntryService sharingEntryService, String type)
		throws Exception {

		_validateType(type);

		if (StringUtil.equals("User", type)) {
			sharingEntryService.deleteSharingEntry(
				0, collaboratorId, classNameId, classPK);
		}
		else {
			sharingEntryService.deleteSharingEntry(
				collaboratorId, 0, classNameId, classPK);
		}
	}

	public static Collaborator getCollaborator(
			AcceptLanguage acceptLanguage, long classNameId, long classPK,
			Long collaboratorId,
			DTOConverter<SharingEntry, Collaborator> dtoConverter,
			DTOConverterRegistry dtoConverterRegistry,
			SharingEntryService sharingEntryService, String type,
			UriInfo uriInfo, User user)
		throws Exception {

		_validateType(type);

		if (StringUtil.equals("User", type)) {
			return toCollaborator(
				acceptLanguage, dtoConverter, dtoConverterRegistry,
				sharingEntryService.getSharingEntry(
					0, collaboratorId, classNameId, classPK),
				uriInfo, user);
		}

		return toCollaborator(
			acceptLanguage, dtoConverter, dtoConverterRegistry,
			sharingEntryService.getSharingEntry(
				collaboratorId, 0, classNameId, classPK),
			uriInfo, user);
	}

	public static Page<Collaborator> getCollaborators(
			AcceptLanguage acceptLanguage, long classNameId, long classPK,
			DTOConverter<SharingEntry, Collaborator> dtoConverter,
			DTOConverterRegistry dtoConverterRegistry, long groupId,
			Pagination pagination,
			SharingEntryLocalService sharingEntryLocalService,
			SharingEntryService sharingEntryService, UriInfo uriInfo, User user)
		throws Exception {

		return Page.of(
			TransformUtil.transform(
				sharingEntryService.getSharingEntries(
					classNameId, classPK, groupId,
					pagination.getStartPosition(), pagination.getEndPosition(),
					OrderByComparatorFactoryUtil.create(
						"SharingEntry", "createDate", false, "sharingEntryId",
						false)),
				sharingEntry -> toCollaborator(
					acceptLanguage, dtoConverter, dtoConverterRegistry,
					sharingEntry, uriInfo, user)),
			pagination,
			sharingEntryLocalService.getSharingEntriesCount(
				classNameId, classPK));
	}

	public static long getGroupId(
			long companyId, GroupLocalService groupLocalService,
			String scopeKey)
		throws Exception {

		Long groupId = GroupUtil.getGroupId(
			companyId, scopeKey, groupLocalService);

		if (groupId != null) {
			return groupId;
		}

		if (Objects.equals(scopeKey, "0")) {
			return 0;
		}

		throw new NoSuchGroupException();
	}

	public static Collaborator toCollaborator(
			AcceptLanguage acceptLanguage,
			DTOConverter<SharingEntry, Collaborator> dtoConverter,
			DTOConverterRegistry dtoConverterRegistry,
			SharingEntry sharingEntry, UriInfo uriInfo, User user)
		throws Exception {

		return dtoConverter.toDTO(
			new DefaultDTOConverterContext(
				acceptLanguage.isAcceptAllLanguages(), new HashMap<>(),
				dtoConverterRegistry, sharingEntry.getSharingEntryId(),
				acceptLanguage.getPreferredLocale(), uriInfo, user),
			sharingEntry);
	}

	private static SharingEntry _addOrUpdateSharingEntry(
			long classNameId, long classPK, Collaborator collaborator,
			long collaboratorId, long groupId,
			SharingEntryService sharingEntryService, String type,
			UserGroupLocalService userGroupLocalService,
			UserLocalService userLocalService)
		throws Exception {

		_validateType(type);

		long toUserGroupId = 0;
		long toUserId = 0;

		if (StringUtil.equals("UserGroup", type)) {
			UserGroup userGroup = userGroupLocalService.getUserGroup(
				collaboratorId);

			toUserGroupId = userGroup.getUserGroupId();
		}
		else {
			User user = userLocalService.getUser(collaboratorId);

			toUserId = user.getUserId();
		}

		boolean shareable = false;

		if (collaborator.getShare() != null) {
			shareable = collaborator.getShare();
		}

		return sharingEntryService.addOrUpdateSharingEntry(
			null, toUserGroupId, toUserId, classNameId, classPK, groupId,
			shareable,
			TransformUtil.transformToList(
				collaborator.getActionIds(),
				SharingEntryAction::parseFromActionId),
			collaborator.getDateExpired(), new ServiceContext());
	}

	private static void _validateType(String type) {
		if (!StringUtil.equals("User", type) &&
			!StringUtil.equals("UserGroup", type)) {

			throw new IllegalArgumentException(
				"Collaborator type must be \"User\" or \"UserGroup\"");
		}
	}

}