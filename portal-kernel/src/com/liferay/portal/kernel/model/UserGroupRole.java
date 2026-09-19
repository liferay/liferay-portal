/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.model;

import com.liferay.portal.kernel.annotation.ImplementationClassName;
import com.liferay.portal.kernel.util.Accessor;

import org.osgi.annotation.versioning.ProviderType;

/**
 * The extended model interface for the UserGroupRole service. Represents a row in the &quot;UserGroupRole&quot; database table, with each column mapped to a property of this class.
 *
 * @author Brian Wing Shun Chan
 * @see UserGroupRoleModel
 * @generated
 */
@ImplementationClassName("com.liferay.portal.model.impl.UserGroupRoleImpl")
@ProviderType
public interface UserGroupRole extends PersistedModel, UserGroupRoleModel {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this interface directly. Add methods to <code>com.liferay.portal.model.impl.UserGroupRoleImpl</code> and rerun ServiceBuilder to automatically copy the method declarations to this interface.
	 */
	public static final Accessor<UserGroupRole, Long>
		USER_GROUP_ROLE_ID_ACCESSOR = new Accessor<UserGroupRole, Long>() {

			@Override
			public Long get(UserGroupRole userGroupRole) {
				return userGroupRole.getUserGroupRoleId();
			}

			@Override
			public Class<Long> getAttributeClass() {
				return Long.class;
			}

			@Override
			public Class<UserGroupRole> getTypeClass() {
				return UserGroupRole.class;
			}

		};
	public static final Accessor<UserGroupRole, Long> USER_ID_ACCESSOR =
		new Accessor<UserGroupRole, Long>() {

			@Override
			public Long get(UserGroupRole userGroupRole) {
				return userGroupRole.getUserId();
			}

			@Override
			public Class<Long> getAttributeClass() {
				return Long.class;
			}

			@Override
			public Class<UserGroupRole> getTypeClass() {
				return UserGroupRole.class;
			}

		};
	public static final Accessor<UserGroupRole, Long> GROUP_ID_ACCESSOR =
		new Accessor<UserGroupRole, Long>() {

			@Override
			public Long get(UserGroupRole userGroupRole) {
				return userGroupRole.getGroupId();
			}

			@Override
			public Class<Long> getAttributeClass() {
				return Long.class;
			}

			@Override
			public Class<UserGroupRole> getTypeClass() {
				return UserGroupRole.class;
			}

		};
	public static final Accessor<UserGroupRole, Long> ROLE_ID_ACCESSOR =
		new Accessor<UserGroupRole, Long>() {

			@Override
			public Long get(UserGroupRole userGroupRole) {
				return userGroupRole.getRoleId();
			}

			@Override
			public Class<Long> getAttributeClass() {
				return Long.class;
			}

			@Override
			public Class<UserGroupRole> getTypeClass() {
				return UserGroupRole.class;
			}

		};

	@Override
	public boolean equals(Object object);

	public Group getGroup()
		throws com.liferay.portal.kernel.exception.PortalException;

	public Role getRole()
		throws com.liferay.portal.kernel.exception.PortalException;

	public User getUser()
		throws com.liferay.portal.kernel.exception.PortalException;

	public boolean hasOrganizationRole();

	public boolean hasSiteRole();

	public int hashCode();

}
// LIFERAY-SERVICE-BUILDER-HASH:1313566012