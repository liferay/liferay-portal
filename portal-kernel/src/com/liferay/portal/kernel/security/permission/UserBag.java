/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.security.permission;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Organization;
import com.liferay.portal.kernel.model.Role;

import java.io.Serializable;

import java.util.Collection;

import org.osgi.annotation.versioning.ProviderType;

/**
 * @author László Csontos
 * @author Preston Crary
 */
@ProviderType
public interface UserBag extends Serializable {

	public Collection<Group> getGroups() throws PortalException;

	public long[] getRoleIds();

	public Collection<Role> getRoles() throws PortalException;

	public long[] getUserGroupIds();

	public Collection<Group> getUserGroups() throws PortalException;

	public long getUserId();

	public long getUserMVCCVersion();

	public long[] getUserOrgGroupIds();

	public Collection<Group> getUserOrgGroups() throws PortalException;

	public long[] getUserOrgIds();

	public Collection<Organization> getUserOrgs() throws PortalException;

	public Collection<Group> getUserUserGroupGroups() throws PortalException;

	public long[] getUserUserGroupsIds();

	public boolean hasRole(Role role);

	public boolean hasUserGroup(Group group);

	public boolean hasUserOrg(Organization organization);

	public boolean hasUserOrgGroup(Group group);

}