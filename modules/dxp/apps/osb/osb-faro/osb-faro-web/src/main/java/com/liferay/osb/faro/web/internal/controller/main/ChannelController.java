/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.web.internal.controller.main;

import com.liferay.osb.faro.constants.FaroChannelConstants;
import com.liferay.osb.faro.engine.client.model.Channel;
import com.liferay.osb.faro.engine.client.model.Results;
import com.liferay.osb.faro.engine.client.util.OrderByField;
import com.liferay.osb.faro.exception.NoSuchFaroUserException;
import com.liferay.osb.faro.model.FaroChannel;
import com.liferay.osb.faro.model.FaroPreferences;
import com.liferay.osb.faro.model.FaroProject;
import com.liferay.osb.faro.model.FaroUser;
import com.liferay.osb.faro.service.FaroChannelLocalService;
import com.liferay.osb.faro.service.FaroPreferencesLocalService;
import com.liferay.osb.faro.service.FaroUserLocalService;
import com.liferay.osb.faro.web.internal.annotations.PATCH;
import com.liferay.osb.faro.web.internal.controller.BaseFaroController;
import com.liferay.osb.faro.web.internal.controller.FaroController;
import com.liferay.osb.faro.web.internal.exception.FaroException;
import com.liferay.osb.faro.web.internal.exception.FaroValidationException;
import com.liferay.osb.faro.web.internal.model.display.FaroResultsDisplay;
import com.liferay.osb.faro.web.internal.model.display.asah.FaroChannelDisplay;
import com.liferay.osb.faro.web.internal.model.display.contacts.FaroUserDisplay;
import com.liferay.osb.faro.web.internal.model.preferences.WorkspacePreferences;
import com.liferay.osb.faro.web.internal.param.FaroParam;
import com.liferay.osb.faro.web.internal.util.JSONUtil;
import com.liferay.osb.faro.web.internal.util.comparator.FaroChannelComparator;
import com.liferay.osb.faro.web.internal.util.comparator.FaroUserComparator;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.dao.search.SearchPaginationUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.RoleConstants;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.StringUtil;

import jakarta.annotation.security.RolesAllowed;

import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.FormParam;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author André Miranda
 */
@Component(service = {ChannelController.class, FaroController.class})
@Path("/{groupId}/channel")
@Produces(MediaType.APPLICATION_JSON)
public class ChannelController extends BaseFaroController {

	@Path("/{id}/users")
	@POST
	@RolesAllowed(RoleConstants.SITE_ADMINISTRATOR)
	public void addUsers(
			@PathParam("groupId") long groupId, @PathParam("id") String id,
			@FormParam("userIds") FaroParam<List<Long>> userIdsFaroParam)
		throws PortalException {

		_faroChannelLocalService.addUsers(
			getCompanyId(), id, userIdsFaroParam.getValue(), getUserId(),
			groupId);
	}

	@Path("/clear")
	@POST
	@RolesAllowed(RoleConstants.SITE_ADMINISTRATOR)
	public void clear(
			@PathParam("groupId") long groupId,
			@FormParam("ids") FaroParam<List<String>> idsFaroParam)
		throws Exception {

		User user = getUser();

		FaroUser faroUser = _faroUserLocalService.fetchFaroUser(
			groupId, user.getEmailAddress());

		if (faroUser == null) {
			throw new NoSuchFaroUserException(
				StringBundler.concat(
					"No FaroUser exists with the key {groupId=", groupId,
					", emailAddress=", user.getEmailAddress(), "}"));
		}

		contactsEngineClient.clearChannel(
			faroProjectLocalService.getFaroProjectByGroupId(groupId), faroUser,
			idsFaroParam.getValue());
	}

	@POST
	@RolesAllowed(RoleConstants.SITE_ADMINISTRATOR)
	public FaroChannelDisplay create(
			@PathParam("groupId") long groupId, @FormParam("name") String name)
		throws PortalException {

		if (StringUtil.equalsIgnoreCase(name, StringPool.NULL)) {
			throw new FaroValidationException(
				"name",
				getLocalizedMessage(
					"name-cannot-be-a-reserved-word-such-as-null"));
		}

		FaroProject faroProject =
			faroProjectLocalService.getFaroProjectByGroupId(groupId);

		Channel channel = new Channel();

		channel.setName(name);

		channel = contactsEngineClient.addChannel(faroProject, channel);

		FaroChannel faroChannel = _faroChannelLocalService.addFaroChannel(
			getUserId(), channel.getName(), channel.getId(), groupId);

		return new FaroChannelDisplay(faroChannel);
	}

	@DELETE
	@RolesAllowed(RoleConstants.SITE_ADMINISTRATOR)
	public void delete(
			@PathParam("groupId") long groupId,
			@FormParam("ids") FaroParam<List<String>> idsFaroParam)
		throws Exception {

		User user = getUser();

		FaroUser faroUser = _faroUserLocalService.fetchFaroUser(
			groupId, user.getEmailAddress());

		if (faroUser == null) {
			throw new NoSuchFaroUserException(
				StringBundler.concat(
					"No FaroUser exists with the key {groupId=", groupId,
					", emailAddress=", user.getEmailAddress(), "}"));
		}

		contactsEngineClient.deleteChannels(
			faroProjectLocalService.getFaroProjectByGroupId(groupId), faroUser,
			idsFaroParam.getValue());

		for (String id : idsFaroParam.getValue()) {
			_faroChannelLocalService.deleteFaroChannel(id, groupId);

			for (FaroPreferences faroPreferences :
					_faroPreferencesLocalService.getFaroPreferencesByGroupId(
						groupId)) {

				WorkspacePreferences workspacePreferences = JSONUtil.readValue(
					faroPreferences.getPreferences(),
					WorkspacePreferences.class);

				if (workspacePreferences.removeEmailReportPreferences(id)) {
					_faroPreferencesLocalService.savePreferences(
						getUserId(), groupId, faroPreferences.getOwnerId(),
						JSONUtil.writeValueAsString(workspacePreferences));
				}
			}
		}
	}

	@GET
	@Path("/{id}")
	@RolesAllowed(RoleConstants.SITE_MEMBER)
	public FaroChannelDisplay getFaroChannelDisplay(
			@PathParam("groupId") long groupId, @PathParam("id") String id)
		throws PortalException {

		FaroChannel faroChannel = _faroChannelLocalService.getFaroChannel(
			id, groupId);

		return new FaroChannelDisplay(
			contactsEngineClient.getChannel(
				faroProjectLocalService.getFaroProjectByGroupId(groupId),
				faroChannel.getChannelId()),
			faroChannel);
	}

	@GET
	@Path("/{id}/users")
	@RolesAllowed(RoleConstants.SITE_MEMBER)
	public FaroResultsDisplay getUsers(
			@PathParam("groupId") long groupId, @PathParam("id") String id,
			@QueryParam("available") boolean available,
			@QueryParam("query") String query,
			@DefaultValue(_STATUSES_DEFAULT_VALUE) @QueryParam("statuses")
				FaroParam<List<Integer>> statusesFaroParam,
			@QueryParam("cur") int cur, @QueryParam("delta") int delta,
			@DefaultValue(StringPool.BLANK) @QueryParam("orderByFields")
				FaroParam<List<OrderByField>> orderByFieldsFaroParam)
		throws PortalException {

		List<Integer> statuses = statusesFaroParam.getValue();

		if (ListUtil.isEmpty(statuses)) {
			throw new FaroException("Invalid statuses values");
		}

		int[] startAndEnd = SearchPaginationUtil.calculateStartAndEnd(
			cur, delta);

		OrderByComparator<FaroUser> orderByComparator = null;

		if (ListUtil.isNotNull(orderByFieldsFaroParam.getValue())) {
			orderByComparator = new FaroUserComparator(
				orderByFieldsFaroParam.getValue());
		}

		List<FaroUser> faroUsers = _faroChannelLocalService.getFaroUsers(
			id, available, query, statuses, groupId, startAndEnd[0],
			startAndEnd[1], orderByComparator);

		return new FaroResultsDisplay(
			TransformUtil.transform(faroUsers, FaroUserDisplay::new),
			_faroChannelLocalService.getFaroUsersCount(
				id, available, query, statuses, groupId));
	}

	@PATCH
	@Path("/{id}")
	@RolesAllowed(RoleConstants.SITE_ADMINISTRATOR)
	public FaroChannelDisplay patch(
			@PathParam("groupId") long groupId, @PathParam("id") String id,
			@FormParam("name") String name,
			@FormParam("permissionType") Integer permissionType)
		throws PortalException {

		FaroChannel faroChannel = _faroChannelLocalService.getFaroChannel(
			id, groupId);

		if (permissionType != null) {
			if (permissionType == FaroChannelConstants.PERMISSION_ALL_USERS) {
				faroChannel.setPermissionType(
					FaroChannelConstants.PERMISSION_ALL_USERS);
			}
			else if (permissionType ==
						FaroChannelConstants.PERMISSION_SELECT_USERS) {

				faroChannel.setPermissionType(
					FaroChannelConstants.PERMISSION_SELECT_USERS);
			}
			else {
				throw new FaroException(
					"Invalid permission type:" + permissionType);
			}
		}

		if ((name != null) && !name.isEmpty()) {
			FaroProject faroProject =
				faroProjectLocalService.getFaroProjectByGroupId(groupId);

			Channel channel = contactsEngineClient.patchChannel(
				faroProject, id, name);

			faroChannel.setName(channel.getName());
		}

		faroChannel = _faroChannelLocalService.updateFaroChannel(faroChannel);

		return new FaroChannelDisplay(faroChannel);
	}

	@DELETE
	@Path("/{id}/users")
	@RolesAllowed(RoleConstants.SITE_ADMINISTRATOR)
	public void removeUsers(
			@PathParam("groupId") long groupId, @PathParam("id") String id,
			@FormParam("userIds") FaroParam<List<Long>> userIdsFaroParam)
		throws PortalException {

		_faroChannelLocalService.removeUsers(
			id, userIdsFaroParam.getValue(), groupId);
	}

	@GET
	@RolesAllowed(RoleConstants.SITE_MEMBER)
	public FaroResultsDisplay<FaroChannelDisplay> search(
			@PathParam("groupId") long groupId,
			@QueryParam("query") String query, @QueryParam("cur") int cur,
			@QueryParam("delta") int delta,
			@DefaultValue(StringPool.BLANK) @QueryParam("orderByFields")
				FaroParam<List<OrderByField>> orderByFieldsFaroParam)
		throws Exception {

		int[] startAndEnd = null;

		if ((cur == QueryUtil.ALL_POS) && (delta == QueryUtil.ALL_POS)) {
			startAndEnd = new int[] {QueryUtil.ALL_POS, QueryUtil.ALL_POS};
		}
		else {
			startAndEnd = SearchPaginationUtil.calculateStartAndEnd(cur, delta);
		}

		List<OrderByField> orderByFields = orderByFieldsFaroParam.getValue();

		if (orderByFields == null) {
			orderByFields = Collections.singletonList(
				new OrderByField("createTime", "asc"));
		}

		List<FaroChannel> faroChannels = _faroChannelLocalService.search(
			groupId, query, startAndEnd[0], startAndEnd[1],
			new FaroChannelComparator(orderByFields));

		if ((cur == -1) && (delta == -1)) {
			return new FaroResultsDisplay<>(
				TransformUtil.transform(faroChannels, FaroChannelDisplay::new),
				_faroChannelLocalService.searchCount(groupId, query));
		}

		if (faroChannels.isEmpty()) {
			return new FaroResultsDisplay<>();
		}

		Map<String, Channel> channelsById = new HashMap<>();

		Results<Channel> channelsResult = contactsEngineClient.getChannels(
			faroProjectLocalService.getFaroProjectByGroupId(groupId), cur,
			delta, ListUtil.toList(faroChannels, FaroChannel::getChannelId),
			null);

		for (Channel channel : channelsResult.getItems()) {
			channelsById.put(channel.getId(), channel);
		}

		List<FaroChannelDisplay> faroChannelDisplays = new ArrayList<>();

		for (FaroChannel faroChannel : faroChannels) {
			faroChannelDisplays.add(
				new FaroChannelDisplay(
					channelsById.get(faroChannel.getChannelId()), faroChannel));
		}

		return new FaroResultsDisplay<>(
			faroChannelDisplays,
			_faroChannelLocalService.searchCount(groupId, query));
	}

	private static final String _STATUSES_DEFAULT_VALUE = "[0]";

	@Reference
	private FaroChannelLocalService _faroChannelLocalService;

	@Reference
	private FaroPreferencesLocalService _faroPreferencesLocalService;

	@Reference
	private FaroUserLocalService _faroUserLocalService;

}