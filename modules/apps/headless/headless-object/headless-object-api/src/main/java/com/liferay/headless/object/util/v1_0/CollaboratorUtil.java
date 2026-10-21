/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.object.util.v1_0;

import com.liferay.headless.object.dto.v1_0.Collaborator;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.configuration.module.configuration.ConfigurationProvider;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.NoSuchGroupException;
import com.liferay.portal.kernel.exception.NoSuchModelException;
import com.liferay.portal.kernel.model.Ticket;
import com.liferay.portal.kernel.model.TicketConstants;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.UserGroup;
import com.liferay.portal.kernel.security.auth.GuestOrUserUtil;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.TicketLocalService;
import com.liferay.portal.kernel.service.UserGroupLocalService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.service.permission.UserPermissionUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.OrderByComparatorFactoryUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.vulcan.accept.language.AcceptLanguage;
import com.liferay.portal.vulcan.dto.converter.DTOConverter;
import com.liferay.portal.vulcan.dto.converter.DTOConverterRegistry;
import com.liferay.portal.vulcan.dto.converter.DefaultDTOConverterContext;
import com.liferay.portal.vulcan.pagination.Page;
import com.liferay.portal.vulcan.pagination.Pagination;
import com.liferay.portal.vulcan.util.GroupUtil;
import com.liferay.sharing.configuration.SharingEntryCollaborationEmailConfiguration;
import com.liferay.sharing.exception.DuplicateSharingEntryException;
import com.liferay.sharing.model.SharingEntry;
import com.liferay.sharing.security.permission.SharingEntryAction;
import com.liferay.sharing.service.SharingEntryLocalService;
import com.liferay.sharing.service.SharingEntryService;

import jakarta.servlet.http.HttpServletRequest;

import jakarta.ws.rs.core.UriInfo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * @author Mikel Lorza
 */
public class CollaboratorUtil {

	public static Collaborator addOrUpdateCollaborator(
			AcceptLanguage acceptLanguage, String className, long classNameId,
			long classPK, Collaborator collaborator, long collaboratorId,
			long companyId, ConfigurationProvider configurationProvider,
			DTOConverter<SharingEntry, Collaborator> dtoConverter,
			DTOConverterRegistry dtoConverterRegistry, long groupId,
			HttpServletRequest httpServletRequest,
			SharingEntryService sharingEntryService,
			TicketLocalService ticketLocalService, String type, UriInfo uriInfo,
			User user, UserGroupLocalService userGroupLocalService,
			UserLocalService userLocalService)
		throws Exception {

		_validateType(type);

		if (StringUtil.equals("Email", type)) {
			return addOrUpdateCollaboratorByEmailAddress(
				acceptLanguage, className, classNameId, classPK, collaborator,
				companyId, configurationProvider, dtoConverter,
				dtoConverterRegistry, collaborator.getEmailAddress(), groupId,
				httpServletRequest, sharingEntryService, ticketLocalService,
				uriInfo, user, userGroupLocalService, userLocalService);
		}

		if (StringUtil.equals("User", type) &&
			!_hasViewPermission(userLocalService.getUser(collaboratorId))) {

			throw new NoSuchModelException();
		}

		return toCollaborator(
			acceptLanguage, dtoConverter, dtoConverterRegistry,
			_addOrUpdateSharingEntry(
				classNameId, classPK, collaborator, collaboratorId, groupId,
				httpServletRequest, sharingEntryService, type,
				userGroupLocalService, userLocalService),
			uriInfo, user);
	}

	public static Collaborator addOrUpdateCollaboratorByEmailAddress(
			AcceptLanguage acceptLanguage, String className, long classNameId,
			long classPK, Collaborator collaborator, long companyId,
			ConfigurationProvider configurationProvider,
			DTOConverter<SharingEntry, Collaborator> dtoConverter,
			DTOConverterRegistry dtoConverterRegistry, String emailAddress,
			long groupId, HttpServletRequest httpServletRequest,
			SharingEntryService sharingEntryService,
			TicketLocalService ticketLocalService, UriInfo uriInfo, User user,
			UserGroupLocalService userGroupLocalService,
			UserLocalService userLocalService)
		throws Exception {

		_validateType("Email");
		_validateEmailAddress(emailAddress);
		_validateEmailActionIds(collaborator.getActionIds());

		emailAddress = _normalizeEmailAddress(emailAddress);

		User existingUser = userLocalService.fetchUserByEmailAddress(
			companyId, emailAddress);

		if (existingUser != null) {
			_validateSharingEntry(
				classNameId, classPK, emailAddress, sharingEntryService, user,
				existingUser.getUserId());

			SharingEntry sharingEntry = _addOrUpdateSharingEntry(
				classNameId, classPK, collaborator, existingUser.getUserId(),
				groupId, httpServletRequest, sharingEntryService, "User",
				userGroupLocalService, userLocalService);

			Ticket ticket = _fetchTicketByEmailAddress(
				className, classPK, companyId, emailAddress,
				ticketLocalService);

			if (ticket != null) {
				SharingEntry existingSharingEntry =
					sharingEntryService.fetchSharingEntry(
						ticket.getTicketId(), 0, 0, classNameId, classPK);

				if (existingSharingEntry != null) {
					sharingEntryService.deleteSharingEntry(
						existingSharingEntry);
				}

				ticketLocalService.deleteTicket(ticket.getTicketId());
			}

			return toCollaborator(
				acceptLanguage, dtoConverter, dtoConverterRegistry,
				sharingEntry, uriInfo, user);
		}

		Ticket ticket = _addOrUpdateTicket(
			className, classPK, companyId, emailAddress,
			_getExpirationDate(companyId, configurationProvider),
			_fetchTicketByEmailAddress(
				className, classPK, companyId, emailAddress,
				ticketLocalService),
			ticketLocalService);

		return toCollaborator(
			acceptLanguage, dtoConverter, dtoConverterRegistry,
			_addOrUpdateSharingEntry(
				classNameId, classPK, collaborator, ticket.getTicketId(),
				groupId, httpServletRequest, sharingEntryService, "Email",
				userGroupLocalService, userLocalService),
			uriInfo, user);
	}

	public static Page<Collaborator> addOrUpdateCollaborators(
			AcceptLanguage acceptLanguage, String className, long classNameId,
			long classPK, Collaborator[] collaborators, long companyId,
			ConfigurationProvider configurationProvider,
			DTOConverter<SharingEntry, Collaborator> dtoConverter,
			DTOConverterRegistry dtoConverterRegistry, long groupId,
			HttpServletRequest httpServletRequest,
			SharingEntryService sharingEntryService,
			TicketLocalService ticketLocalService, UriInfo uriInfo, User user,
			UserGroupLocalService userGroupLocalService,
			UserLocalService userLocalService)
		throws Exception {

		Date expirationDate = _getExpirationDate(
			companyId, configurationProvider);
		List<SharingEntry> oldSharingEntries =
			sharingEntryService.getSharingEntries(
				classNameId, classPK, groupId, QueryUtil.ALL_POS,
				QueryUtil.ALL_POS, null);
		List<SharingEntry> newSharingEntries = new ArrayList<>();
		Set<Long> sharingEntryIds = new HashSet<>();
		Set<Long> ticketIds = new HashSet<>();

		for (Collaborator collaborator : collaborators) {
			_validateType(collaborator.getType());

			SharingEntry sharingEntry = null;

			if (StringUtil.equals("Email", collaborator.getType())) {
				_validateEmailAddress(collaborator.getEmailAddress());
				_validateEmailActionIds(collaborator.getActionIds());

				String emailAddress = _normalizeEmailAddress(
					collaborator.getEmailAddress());

				User existingUser = userLocalService.fetchUserByEmailAddress(
					companyId, emailAddress);

				if (existingUser != null) {
					_validateSharingEntry(
						classNameId, classPK, emailAddress, sharingEntryService,
						user, existingUser.getUserId());

					sharingEntry = _addOrUpdateSharingEntry(
						classNameId, classPK, collaborator,
						existingUser.getUserId(), groupId, httpServletRequest,
						sharingEntryService, "User", userGroupLocalService,
						userLocalService);
				}
				else {
					Ticket ticket = _addOrUpdateTicket(
						className, classPK, companyId, emailAddress,
						expirationDate,
						_fetchTicketByEmailAddress(
							className, classPK, companyId, emailAddress,
							ticketLocalService),
						ticketLocalService);

					sharingEntry = _addOrUpdateSharingEntry(
						classNameId, classPK, collaborator,
						ticket.getTicketId(), groupId, httpServletRequest,
						sharingEntryService, collaborator.getType(),
						userGroupLocalService, userLocalService);

					ticketIds.add(ticket.getTicketId());
				}
			}
			else {
				long collaboratorId = GetterUtil.getLong(collaborator.getId());

				if (StringUtil.equals("User", collaborator.getType()) &&
					!_hasViewPermission(
						userLocalService.getUser(collaboratorId))) {

					throw new NoSuchModelException();
				}

				sharingEntry = _addOrUpdateSharingEntry(
					classNameId, classPK, collaborator, collaboratorId, groupId,
					httpServletRequest, sharingEntryService,
					collaborator.getType(), userGroupLocalService,
					userLocalService);
			}

			newSharingEntries.add(sharingEntry);
			sharingEntryIds.add(sharingEntry.getSharingEntryId());
		}

		List<Ticket> tickets = ticketLocalService.getTickets(
			companyId, className, classPK,
			TicketConstants.TYPE_INVITE_COLLABORATOR);

		for (Ticket ticket : tickets) {
			if (ticketIds.contains(ticket.getTicketId())) {
				continue;
			}

			ticketLocalService.deleteTicket(ticket.getTicketId());
		}

		for (SharingEntry sharingEntry : oldSharingEntries) {
			if (sharingEntryIds.contains(sharingEntry.getSharingEntryId())) {
				continue;
			}

			sharingEntryService.deleteSharingEntry(sharingEntry);
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
			long classNameId, long classPK, Long collaboratorId, long companyId,
			SharingEntryService sharingEntryService, String type)
		throws Exception {

		_validateType(type);

		if (StringUtil.equals("Email", type)) {
			throw new IllegalArgumentException(
				"Use deleteCollaboratorByEmailAddress to delete a " +
					"collaborator of type \"Email\"");
		}

		if (StringUtil.equals("User", type)) {
			sharingEntryService.deleteSharingEntry(
				0, 0, collaboratorId, classNameId, classPK);
		}
		else if (StringUtil.equals("UserGroup", type)) {
			sharingEntryService.deleteSharingEntry(
				0, collaboratorId, 0, classNameId, classPK);
		}
	}

	public static void deleteCollaboratorByEmailAddress(
			String className, long classNameId, long classPK, long companyId,
			String emailAddress, SharingEntryService sharingEntryService,
			TicketLocalService ticketLocalService, User user,
			UserLocalService userLocalService)
		throws Exception {

		_validateEmailAddress(emailAddress);

		User toUser = userLocalService.fetchUserByEmailAddress(
			companyId, _normalizeEmailAddress(emailAddress));

		if (toUser != null) {
			SharingEntry sharingEntry = sharingEntryService.fetchSharingEntry(
				0, 0, toUser.getUserId(), classNameId, classPK);

			if (sharingEntry != null) {
				if ((sharingEntry.getUserId() != user.getUserId()) &&
					!_hasViewPermission(toUser)) {

					throw new NoSuchModelException();
				}

				sharingEntryService.deleteSharingEntry(sharingEntry);
			}
		}

		Ticket ticket = _fetchTicketByEmailAddress(
			className, classPK, companyId, emailAddress, ticketLocalService);

		if (ticket == null) {
			return;
		}

		SharingEntry sharingEntry = sharingEntryService.fetchSharingEntry(
			ticket.getTicketId(), 0, 0, classNameId, classPK);

		if (sharingEntry != null) {
			sharingEntryService.deleteSharingEntry(sharingEntry);
		}

		ticketLocalService.deleteTicket(ticket.getTicketId());
	}

	public static Collaborator getCollaborator(
			AcceptLanguage acceptLanguage, long classNameId, long classPK,
			Long collaboratorId, long companyId,
			DTOConverter<SharingEntry, Collaborator> dtoConverter,
			DTOConverterRegistry dtoConverterRegistry,
			SharingEntryService sharingEntryService, String type,
			UriInfo uriInfo, User user)
		throws Exception {

		_validateType(type);

		if (StringUtil.equals("Email", type)) {
			throw new IllegalArgumentException(
				"Use getCollaboratorByEmailAddress to get a collaborator of " +
					"type \"Email\"");
		}

		if (StringUtil.equals("User", type)) {
			return toCollaborator(
				acceptLanguage, dtoConverter, dtoConverterRegistry,
				sharingEntryService.getSharingEntry(
					0, 0, collaboratorId, classNameId, classPK),
				uriInfo, user);
		}

		return toCollaborator(
			acceptLanguage, dtoConverter, dtoConverterRegistry,
			sharingEntryService.getSharingEntry(
				0, collaboratorId, 0, classNameId, classPK),
			uriInfo, user);
	}

	public static Collaborator getCollaboratorByEmailAddress(
			AcceptLanguage acceptLanguage, String className, long classNameId,
			long classPK, long companyId,
			DTOConverter<SharingEntry, Collaborator> dtoConverter,
			DTOConverterRegistry dtoConverterRegistry, String emailAddress,
			SharingEntryService sharingEntryService,
			TicketLocalService ticketLocalService, UriInfo uriInfo, User user,
			UserLocalService userLocalService)
		throws Exception {

		_validateEmailAddress(emailAddress);

		emailAddress = _normalizeEmailAddress(emailAddress);

		User existingUser = userLocalService.fetchUserByEmailAddress(
			companyId, emailAddress);

		if (existingUser != null) {
			SharingEntry sharingEntry = sharingEntryService.fetchSharingEntry(
				0, 0, existingUser.getUserId(), classNameId, classPK);

			if (sharingEntry != null) {
				return toCollaborator(
					acceptLanguage, dtoConverter, dtoConverterRegistry,
					sharingEntry, uriInfo, user);
			}

			throw new NoSuchModelException();
		}

		Ticket ticket = _fetchTicketByEmailAddress(
			className, classPK, companyId, emailAddress, ticketLocalService);

		if (ticket == null) {
			throw new NoSuchModelException();
		}

		SharingEntry sharingEntry = sharingEntryService.fetchSharingEntry(
			ticket.getTicketId(), 0, 0, classNameId, classPK);

		if (sharingEntry == null) {
			throw new NoSuchModelException();
		}

		return toCollaborator(
			acceptLanguage, dtoConverter, dtoConverterRegistry, sharingEntry,
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

		if (StringUtil.equals("0", scopeKey)) {
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
			HttpServletRequest httpServletRequest,
			SharingEntryService sharingEntryService, String type,
			UserGroupLocalService userGroupLocalService,
			UserLocalService userLocalService)
		throws Exception {

		long toTicketId = 0;
		long toUserGroupId = 0;
		long toUserId = 0;

		if (StringUtil.equals("Email", type)) {
			toTicketId = collaboratorId;
		}
		else if (StringUtil.equals("UserGroup", type)) {
			UserGroup userGroup = userGroupLocalService.getUserGroup(
				collaboratorId);

			toUserGroupId = userGroup.getUserGroupId();
		}
		else if (StringUtil.equals("User", type)) {
			User user = userLocalService.getUser(collaboratorId);

			toUserId = user.getUserId();
		}

		boolean shareable = false;

		if (collaborator.getShare() != null) {
			shareable = collaborator.getShare();
		}

		ServiceContext serviceContext = new ServiceContext();

		serviceContext.setRequest(httpServletRequest);

		return sharingEntryService.addOrUpdateSharingEntry(
			null, toTicketId, toUserGroupId, toUserId, classNameId, classPK,
			groupId, shareable,
			TransformUtil.transformToList(
				collaborator.getActionIds(),
				SharingEntryAction::parseFromActionId),
			collaborator.getDateExpired(), serviceContext);
	}

	private static Ticket _addOrUpdateTicket(
		String className, long classPK, long companyId, String emailAddress,
		Date expirationDate, Ticket ticket,
		TicketLocalService ticketLocalService) {

		if (ticket == null) {
			return ticketLocalService.addTicket(
				companyId, className, classPK,
				TicketConstants.TYPE_INVITE_COLLABORATOR, emailAddress, null,
				expirationDate, null);
		}

		ticket.setEmailAddress(emailAddress);
		ticket.setExpirationDate(expirationDate);

		return ticketLocalService.updateTicket(ticket);
	}

	private static Ticket _fetchTicketByEmailAddress(
		String className, long classPK, long companyId, String emailAddress,
		TicketLocalService ticketLocalService) {

		List<Ticket> tickets = ticketLocalService.getTickets(
			companyId, TicketConstants.TYPE_INVITE_COLLABORATOR, emailAddress);

		for (Ticket ticket : tickets) {
			if (StringUtil.equals(className, ticket.getClassName()) &&
				(ticket.getClassPK() == classPK)) {

				return ticket;
			}
		}

		return null;
	}

	private static Date _getExpirationDate(
			long companyId, ConfigurationProvider configurationProvider)
		throws Exception {

		SharingEntryCollaborationEmailConfiguration
			sharingEntryCollaborationEmailConfiguration =
				configurationProvider.getCompanyConfiguration(
					SharingEntryCollaborationEmailConfiguration.class,
					companyId);

		return new Date(
			System.currentTimeMillis() +
				TimeUnit.HOURS.toMillis(
					sharingEntryCollaborationEmailConfiguration.
						invitationToCollaborateTokenExpirationTime()));
	}

	private static boolean _hasViewPermission(User user) throws Exception {
		return UserPermissionUtil.contains(
			GuestOrUserUtil.getPermissionChecker(), user.getUserId(),
			ActionKeys.VIEW);
	}

	private static String _normalizeEmailAddress(String emailAddress) {
		return StringUtil.toLowerCase(StringUtil.trim(emailAddress));
	}

	private static void _validateEmailActionIds(String[] actionIds) {
		if (actionIds == null) {
			throw new IllegalArgumentException(
				"Collaborators of type \"Email\" can only be granted the " +
					"VIEW action");
		}

		for (String actionId : actionIds) {
			if (!StringUtil.equals(
					SharingEntryAction.VIEW.getActionId(), actionId)) {

				throw new IllegalArgumentException(
					"Collaborators of type \"Email\" can only be granted the " +
						"VIEW action");
			}
		}
	}

	private static void _validateEmailAddress(String emailAddress) {
		if (Validator.isNull(emailAddress)) {
			throw new IllegalArgumentException(
				"Collaborator type \"Email\" must have an email address");
		}

		if (!Validator.isEmailAddress(emailAddress)) {
			throw new IllegalArgumentException(
				"Invalid email address: " + emailAddress);
		}
	}

	private static void _validateSharingEntry(
			long classNameId, long classPK, String emailAddress,
			SharingEntryService sharingEntryService, User user, long userId)
		throws Exception {

		SharingEntry sharingEntry = sharingEntryService.fetchSharingEntry(
			0, 0, userId, classNameId, classPK);

		if ((sharingEntry != null) &&
			(sharingEntry.getUserId() != user.getUserId())) {

			throw new DuplicateSharingEntryException(
				StringBundler.concat(
					"A sharing entry already exists for ", emailAddress,
					" with class name ID ", classNameId,
					" and class primary key ", classPK));
		}
	}

	private static void _validateType(String type) {
		if (!StringUtil.equals("Email", type) &&
			!StringUtil.equals("User", type) &&
			!StringUtil.equals("UserGroup", type)) {

			throw new IllegalArgumentException(
				"Collaborator type must be \"Email\", \"User\", or " +
					"\"UserGroup\"");
		}
	}

}