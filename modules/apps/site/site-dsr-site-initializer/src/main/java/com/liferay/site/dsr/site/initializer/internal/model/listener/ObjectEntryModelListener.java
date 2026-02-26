/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.dsr.site.initializer.internal.model.listener;

import com.liferay.layout.util.LayoutServiceContextHelper;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.portal.kernel.exception.ModelListenerException;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.BaseModelListener;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.GroupConstants;
import com.liferay.portal.kernel.model.LayoutSetPrototype;
import com.liferay.portal.kernel.model.ModelListener;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.service.ClassNameLocalService;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.LayoutSetPrototypeLocalService;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.service.UserGroupRoleLocalService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.transaction.TransactionCommitCallbackUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.liveusers.LiveUsers;
import com.liferay.portal.security.permission.PermissionCacheUtil;
import com.liferay.sites.kernel.util.Sites;

import java.io.Serializable;

import java.util.Map;
import java.util.Objects;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Stefano Motta
 */
@Component(service = ModelListener.class)
public class ObjectEntryModelListener extends BaseModelListener<ObjectEntry> {

	@Override
	public void onAfterCreate(ObjectEntry objectEntry)
		throws ModelListenerException {

		try {
			_onAfterCreate(objectEntry);
		}
		catch (Exception exception) {
			throw new ModelListenerException(exception);
		}
	}

	@Override
	public void onAfterRemove(ObjectEntry objectEntry)
		throws ModelListenerException {

		try {
			_onAfterRemove(objectEntry);
		}
		catch (Exception exception) {
			throw new ModelListenerException(exception);
		}
	}

	private String _getFriendlyURL(String friendlyURL) {
		if (Validator.isNotNull(friendlyURL) && !friendlyURL.startsWith("/")) {
			return "/" + friendlyURL;
		}

		return friendlyURL;
	}

	private ServiceContext _getServiceContext(long companyId, long userId)
		throws PortalException {

		ServiceContext serviceContext = new ServiceContext();

		serviceContext.setCompanyId(companyId);
		serviceContext.setUserId(userId);

		ServiceContextThreadLocal.pushServiceContext(serviceContext);

		return serviceContext;
	}

	private void _onAfterCreate(ObjectEntry objectEntry) throws Exception {
		ObjectDefinition objectDefinition = objectEntry.getObjectDefinition();

		if (!Objects.equals(
				objectDefinition.getExternalReferenceCode(), "L_DSR_ROOM")) {

			return;
		}

		Company company = _companyLocalService.getCompany(
			objectEntry.getCompanyId());
		User user = _userLocalService.getUser(objectEntry.getUserId());

		try (AutoCloseable autoCloseable =
				_layoutServiceContextHelper.getServiceContextAutoCloseable(
					company, user)) {

			Map<String, Serializable> values = objectEntry.getValues();

			Group group = _groupLocalService.addGroup(
				null, user.getUserId(), GroupConstants.DEFAULT_PARENT_GROUP_ID,
				objectDefinition.getClassName(), objectEntry.getObjectEntryId(),
				GroupConstants.DEFAULT_LIVE_GROUP_ID,
				HashMapBuilder.put(
					LocaleUtil.getDefault(), StringUtil.randomString()
				).build(),
				null, GroupConstants.TYPE_SITE_RESTRICTED, null, true,
				GroupConstants.DEFAULT_MEMBERSHIP_RESTRICTION,
				_getFriendlyURL(
					GetterUtil.getString(
						values.get("friendlyURL"),
						GetterUtil.getString(values.get("name")))),
				true, false, true,
				_getServiceContext(company.getCompanyId(), user.getUserId()));

			Role role = _roleLocalService.getRole(
				group.getCompanyId(), RoleConstants.SITE_OWNER);

			_userGroupRoleLocalService.addUserGroupRoles(
				user.getUserId(), group.getGroupId(),
				new long[] {role.getRoleId()});

			_userLocalService.addGroupUsers(
				group.getGroupId(), new long[] {user.getUserId()});

			LiveUsers.joinGroup(
				group.getCompanyId(), group.getGroupId(), user.getUserId());

			LayoutSetPrototype layoutSetPrototype =
				_layoutSetPrototypeLocalService.
					getLayoutSetPrototypeByUuidAndCompanyId(
						GetterUtil.getString(
							values.get("siteTemplateKey"),
							"L_DSR_LAYOUT_SET_PROTOTYPE"),
						company.getCompanyId());

			_sites.updateLayoutSetPrototypesLinks(
				group, layoutSetPrototype.getLayoutSetPrototypeId(), 0, true,
				false);

			TransactionCommitCallbackUtil.registerCallback(
				() -> {
					_objectEntryLocalService.partialUpdateObjectEntry(
						objectEntry.getUserId(), objectEntry.getObjectEntryId(),
						objectEntry.getObjectEntryFolderId(),
						HashMapBuilder.<String, Serializable>put(
							"friendlyURL",
							StringUtil.removeFirst(group.getFriendlyURL(), "/")
						).put(
							"siteId", group.getGroupId()
						).build(),
						new ServiceContext());

					return null;
				});
		}
		catch (Exception exception) {

			// LPS-169057

			PermissionCacheUtil.clearCache(objectEntry.getUserId());

			throw exception;
		}
		finally {
			ServiceContextThreadLocal.popServiceContext();
		}
	}

	private void _onAfterRemove(ObjectEntry objectEntry)
		throws PortalException {

		ObjectDefinition objectDefinition = objectEntry.getObjectDefinition();

		if (!Objects.equals(
				objectDefinition.getExternalReferenceCode(), "L_DSR_ROOM")) {

			return;
		}

		Group group = _groupLocalService.fetchGroup(
			objectEntry.getCompanyId(),
			_classNameLocalService.getClassNameId(
				objectDefinition.getClassName()),
			objectEntry.getObjectEntryId());

		if (group != null) {
			_groupLocalService.deleteGroup(group);
		}
	}

	@Reference
	private ClassNameLocalService _classNameLocalService;

	@Reference
	private CompanyLocalService _companyLocalService;

	@Reference
	private GroupLocalService _groupLocalService;

	@Reference
	private LayoutServiceContextHelper _layoutServiceContextHelper;

	@Reference
	private LayoutSetPrototypeLocalService _layoutSetPrototypeLocalService;

	@Reference
	private ObjectEntryLocalService _objectEntryLocalService;

	@Reference
	private RoleLocalService _roleLocalService;

	@Reference
	private Sites _sites;

	@Reference
	private UserGroupRoleLocalService _userGroupRoleLocalService;

	@Reference
	private UserLocalService _userLocalService;

}