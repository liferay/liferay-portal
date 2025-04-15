/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.web.internal.controller.main;

import com.liferay.osb.faro.constants.FaroUserConstants;
import com.liferay.osb.faro.contacts.model.constants.JSONConstants;
import com.liferay.osb.faro.engine.client.util.OrderByField;
import com.liferay.osb.faro.model.FaroUser;
import com.liferay.osb.faro.service.FaroUserLocalService;
import com.liferay.osb.faro.web.internal.controller.BaseFaroController;
import com.liferay.osb.faro.web.internal.controller.FaroController;
import com.liferay.osb.faro.web.internal.exception.FaroException;
import com.liferay.osb.faro.web.internal.model.display.FaroResultsDisplay;
import com.liferay.osb.faro.web.internal.model.display.contacts.FaroUserDisplay;
import com.liferay.osb.faro.web.internal.param.FaroParam;
import com.liferay.osb.faro.web.internal.util.comparator.FaroUserComparator;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.search.SearchPaginationUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.RoleConstants;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.OrderByComparator;

import jakarta.annotation.security.RolesAllowed;

import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.FormParam;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;

import java.util.Collections;
import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Matthew Kong
 */
@Component(service = {FaroController.class, UserController.class})
@Path("/{groupId}/user")
@Produces(MediaType.APPLICATION_JSON)
public class UserController extends BaseFaroController {

	@Path("/{id}/accept")
	@POST
	@RolesAllowed(RoleConstants.SITE_ADMINISTRATOR)
	public FaroUserDisplay accept(
			@PathParam("groupId") long groupId, @PathParam("id") long id)
		throws PortalException {

		FaroUser faroUser = _faroUserLocalService.getFaroUser(id);

		faroUser.setStatus(FaroUserConstants.STATUS_APPROVED);

		faroUser = _faroUserLocalService.updateFaroUser(faroUser);

		_groupLocalService.addUserGroup(faroUser.getLiveUserId(), groupId);

		return new FaroUserDisplay(faroUser);
	}

	@POST
	@RolesAllowed(RoleConstants.SITE_ADMINISTRATOR)
	public List<FaroUserDisplay> create(
			@PathParam("groupId") long groupId,
			@FormParam("emailAddresses") FaroParam<List<String>>
				emailAddressesFaroParam,
			@FormParam("roleName") String roleName,
			@DefaultValue("true") @FormParam("sendEmail") boolean sendEmail)
		throws Exception {

		validateUpdate(roleName, Collections.emptyList());

		Role role = _roleLocalService.getRole(getCompanyId(), roleName);

		return TransformUtil.transform(
			emailAddressesFaroParam.getValue(),
			emailAddress -> new FaroUserDisplay(
				_faroUserLocalService.addFaroUser(
					getUserId(), groupId, 0, role.getRoleId(), emailAddress,
					FaroUserConstants.STATUS_PENDING, sendEmail)));
	}

	@DELETE
	@RolesAllowed(RoleConstants.SITE_ADMINISTRATOR)
	public List<FaroUserDisplay> delete(
			@PathParam("groupId") long groupId,
			@DefaultValue(JSONConstants.NULL_JSON_ARRAY) @FormParam("ids")
				FaroParam<List<Long>> idsFaroParam)
		throws Exception {

		validateFaroUsers(idsFaroParam.getValue());

		return TransformUtil.transform(
			idsFaroParam.getValue(), id -> delete(groupId, id));
	}

	@DELETE
	@Path("/{id}")
	@RolesAllowed(RoleConstants.SITE_ADMINISTRATOR)
	public FaroUserDisplay delete(
			@PathParam("groupId") long groupId, @PathParam("id") long id)
		throws Exception {

		validateFaroUsers(Collections.singletonList(id));

		return new FaroUserDisplay(_faroUserLocalService.deleteFaroUser(id));
	}

	@GET
	@Path("/current")
	public FaroUserDisplay getCurrentFaroUserDisplay(
			@PathParam("groupId") long groupId)
		throws Exception {

		if (groupId == 0) {
			return new FaroUserDisplay(getUser());
		}

		return new FaroUserDisplay(
			_faroUserLocalService.getFaroUser(groupId, getUserId()));
	}

	@GET
	@Path("/{id}")
	@RolesAllowed(RoleConstants.SITE_MEMBER)
	public FaroUserDisplay getFaroUserDisplay(@PathParam("id") long id)
		throws Exception {

		return new FaroUserDisplay(_faroUserLocalService.getFaroUser(id));
	}

	@Path("/join_request")
	@POST
	public void joinRequest(@PathParam("groupId") long groupId)
		throws PortalException {

		_groupLocalService.getGroup(groupId);

		User user = getUser();

		Role role = _roleLocalService.getRole(
			getCompanyId(), RoleConstants.SITE_MEMBER);

		_faroUserLocalService.addFaroUser(
			user.getUserId(), groupId, user.getUserId(), role.getRoleId(),
			user.getEmailAddress(), FaroUserConstants.STATUS_REQUESTED, true);
	}

	@GET
	@RolesAllowed(RoleConstants.SITE_MEMBER)
	@SuppressWarnings("unchecked")
	public FaroResultsDisplay search(
		@PathParam("groupId") long groupId, @QueryParam("query") String query,
		@DefaultValue(_STATUSES_DEFAULT_VALUE) @QueryParam("statuses") FaroParam
			<List<Integer>> statusesFaroParam,
		@QueryParam("cur") int cur, @QueryParam("delta") int delta,
		@DefaultValue(StringPool.BLANK) @QueryParam("orderByFields") FaroParam
			<List<OrderByField>> orderByFieldsFaroParam) {

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

		return new FaroResultsDisplay(
			TransformUtil.transform(
				_faroUserLocalService.search(
					groupId, query, statuses, startAndEnd[0], startAndEnd[1],
					orderByComparator),
				FaroUserDisplay::new),
			_faroUserLocalService.searchCount(groupId, query, statuses));
	}

	@Path("/search")
	@POST
	@RolesAllowed(RoleConstants.SITE_MEMBER)
	public FaroResultsDisplay searchByForm(
		@PathParam("groupId") long groupId, @FormParam("query") String query,
		@DefaultValue(_STATUSES_DEFAULT_VALUE) @QueryParam("statuses") FaroParam
			<List<Integer>> statusesFaroParam,
		@FormParam("cur") int cur, @FormParam("delta") int delta,
		@DefaultValue(StringPool.BLANK) @FormParam("orderByFields") FaroParam
			<List<OrderByField>> orderByFieldsFaroParam) {

		return search(
			groupId, query, statusesFaroParam, cur, delta,
			orderByFieldsFaroParam);
	}

	@GET
	@Path("/count")
	@RolesAllowed(RoleConstants.SITE_MEMBER)
	public int searchCount(
		@PathParam("groupId") long groupId, @QueryParam("query") String query,
		@DefaultValue(_STATUSES_DEFAULT_VALUE) @QueryParam("statuses") FaroParam
			<List<Integer>> statusesFaroParam) {

		List<Integer> statuses = statusesFaroParam.getValue();

		if (ListUtil.isEmpty(statuses)) {
			throw new FaroException("Invalid statuses values");
		}

		return _faroUserLocalService.searchCount(groupId, query, statuses);
	}

	@PUT
	@RolesAllowed(RoleConstants.SITE_ADMINISTRATOR)
	public List<FaroUserDisplay> update(
			@PathParam("groupId") long groupId,
			@DefaultValue(JSONConstants.NULL_JSON_ARRAY) @FormParam("ids")
				FaroParam<List<Long>> idsFaroParam,
			@FormParam("roleName") String roleName)
		throws Exception {

		validateUpdate(roleName, idsFaroParam.getValue());

		Role role = _roleLocalService.getRole(getCompanyId(), roleName);

		return TransformUtil.transform(
			idsFaroParam.getValue(),
			id -> {
				FaroUser faroUser = _faroUserLocalService.getFaroUser(id);

				faroUser.setRoleId(role.getRoleId());

				return new FaroUserDisplay(
					_faroUserLocalService.updateFaroUser(faroUser));
			});
	}

	@Path("/{id}")
	@PUT
	@RolesAllowed(RoleConstants.SITE_ADMINISTRATOR)
	public FaroUserDisplay update(
			@PathParam("id") long id, @FormParam("roleName") String roleName,
			@FormParam("emailAddress") String emailAddress)
		throws Exception {

		validateUpdate(roleName, Collections.singletonList(id));

		FaroUser faroUser = _faroUserLocalService.getFaroUser(id);

		Role role = _roleLocalService.getRole(getCompanyId(), roleName);

		faroUser.setRoleId(role.getRoleId());

		faroUser.setEmailAddress(emailAddress);

		return new FaroUserDisplay(
			_faroUserLocalService.updateFaroUser(faroUser));
	}

	@Path("/owner")
	@PUT
	@RolesAllowed(StringPool.BLANK)
	public FaroUserDisplay updateOwner(
			@PathParam("groupId") long groupId,
			@FormParam("emailAddress") String emailAddress)
		throws Exception {

		FaroUser faroUser = _faroUserLocalService.getOwnerFaroUser(groupId);

		Role role = _roleLocalService.getRole(
			getCompanyId(), RoleConstants.SITE_ADMINISTRATOR);

		faroUser.setRoleId(role.getRoleId());

		_faroUserLocalService.updateFaroUser(faroUser);

		faroUser = _faroUserLocalService.fetchFaroUser(groupId, emailAddress);

		role = _roleLocalService.getRole(
			getCompanyId(), RoleConstants.SITE_OWNER);

		if (faroUser != null) {
			faroUser.setRoleId(role.getRoleId());

			return new FaroUserDisplay(
				_faroUserLocalService.updateFaroUser(faroUser));
		}

		return new FaroUserDisplay(
			_faroUserLocalService.addFaroUser(
				getUserId(), groupId, 0, role.getRoleId(), emailAddress,
				FaroUserConstants.STATUS_PENDING, false));
	}

	protected void validateFaroUsers(List<Long> ids) throws Exception {
		for (long id : ids) {
			FaroUser faroUser = _faroUserLocalService.fetchFaroUser(id);

			if (faroUser == null) {
				throw new FaroException("No such user with id: " + id);
			}

			Role role = _roleLocalService.getRole(faroUser.getRoleId());

			validateRoleName(role.getName());
		}
	}

	protected void validateLanguageId(String languageId) {
		if (!_language.isAvailableLocale(languageId)) {
			throw new FaroException("Invalid language id: " + languageId);
		}
	}

	protected void validateRoleName(String roleName) {
		if (!roleName.equals(RoleConstants.SITE_ADMINISTRATOR) &&
			!roleName.equals(RoleConstants.SITE_MEMBER)) {

			throw new FaroException("Invalid role name: " + roleName);
		}
	}

	protected void validateUpdate(String roleName, List<Long> ids)
		throws Exception {

		validateFaroUsers(ids);
		validateRoleName(roleName);
	}

	private static final String _STATUSES_DEFAULT_VALUE = "[0, 1]";

	@Reference
	private FaroUserLocalService _faroUserLocalService;

	@Reference
	private GroupLocalService _groupLocalService;

	@Reference
	private Language _language;

	@Reference
	private RoleLocalService _roleLocalService;

}