/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.user.client.dto.v1_0;

import com.liferay.headless.admin.user.client.function.UnsafeSupplier;
import com.liferay.headless.admin.user.client.serdes.v1_0.UserGroupSerDes;

import java.io.Serializable;

import java.util.Date;
import java.util.Map;
import java.util.Objects;

import jakarta.annotation.Generated;

/**
 * @author Javier Gamarra
 * @generated
 */
@Generated("")
public class UserGroup implements Cloneable, Serializable {

	public static UserGroup toDTO(String json) {
		return UserGroupSerDes.toDTO(json);
	}

	public Map<String, Map<String, String>> getActions() {
		return actions;
	}

	public void setActions(Map<String, Map<String, String>> actions) {
		this.actions = actions;
	}

	public void setActions(
		UnsafeSupplier<Map<String, Map<String, String>>, Exception>
			actionsUnsafeSupplier) {

		try {
			actions = actionsUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Map<String, Map<String, String>> actions;

	public Creator getCreator() {
		return creator;
	}

	public void setCreator(Creator creator) {
		this.creator = creator;
	}

	public void setCreator(
		UnsafeSupplier<Creator, Exception> creatorUnsafeSupplier) {

		try {
			creator = creatorUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Creator creator;

	public Date getDateCreated() {
		return dateCreated;
	}

	public void setDateCreated(Date dateCreated) {
		this.dateCreated = dateCreated;
	}

	public void setDateCreated(
		UnsafeSupplier<Date, Exception> dateCreatedUnsafeSupplier) {

		try {
			dateCreated = dateCreatedUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Date dateCreated;

	public Date getDateModified() {
		return dateModified;
	}

	public void setDateModified(Date dateModified) {
		this.dateModified = dateModified;
	}

	public void setDateModified(
		UnsafeSupplier<Date, Exception> dateModifiedUnsafeSupplier) {

		try {
			dateModified = dateModifiedUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Date dateModified;

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setDescription(
		UnsafeSupplier<String, Exception> descriptionUnsafeSupplier) {

		try {
			description = descriptionUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String description;

	public String getExternalReferenceCode() {
		return externalReferenceCode;
	}

	public void setExternalReferenceCode(String externalReferenceCode) {
		this.externalReferenceCode = externalReferenceCode;
	}

	public void setExternalReferenceCode(
		UnsafeSupplier<String, Exception> externalReferenceCodeUnsafeSupplier) {

		try {
			externalReferenceCode = externalReferenceCodeUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String externalReferenceCode;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public void setId(UnsafeSupplier<Long, Exception> idUnsafeSupplier) {
		try {
			id = idUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Long id;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setName(UnsafeSupplier<String, Exception> nameUnsafeSupplier) {
		try {
			name = nameUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String name;

	public com.liferay.headless.admin.user.client.permission.Permission[]
		getPermissions() {

		return permissions;
	}

	public void setPermissions(
		com.liferay.headless.admin.user.client.permission.Permission[]
			permissions) {

		this.permissions = permissions;
	}

	public void setPermissions(
		UnsafeSupplier
			<com.liferay.headless.admin.user.client.permission.Permission[],
			 Exception> permissionsUnsafeSupplier) {

		try {
			permissions = permissionsUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected com.liferay.headless.admin.user.client.permission.Permission[]
		permissions;

	public RoleBrief[] getRoleBriefs() {
		return roleBriefs;
	}

	public void setRoleBriefs(RoleBrief[] roleBriefs) {
		this.roleBriefs = roleBriefs;
	}

	public void setRoleBriefs(
		UnsafeSupplier<RoleBrief[], Exception> roleBriefsUnsafeSupplier) {

		try {
			roleBriefs = roleBriefsUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected RoleBrief[] roleBriefs;

	public UserAccountBrief[] getUserAccountBriefs() {
		return userAccountBriefs;
	}

	public void setUserAccountBriefs(UserAccountBrief[] userAccountBriefs) {
		this.userAccountBriefs = userAccountBriefs;
	}

	public void setUserAccountBriefs(
		UnsafeSupplier<UserAccountBrief[], Exception>
			userAccountBriefsUnsafeSupplier) {

		try {
			userAccountBriefs = userAccountBriefsUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected UserAccountBrief[] userAccountBriefs;

	public Integer getUsersCount() {
		return usersCount;
	}

	public void setUsersCount(Integer usersCount) {
		this.usersCount = usersCount;
	}

	public void setUsersCount(
		UnsafeSupplier<Integer, Exception> usersCountUnsafeSupplier) {

		try {
			usersCount = usersCountUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Integer usersCount;

	@Override
	public UserGroup clone() throws CloneNotSupportedException {
		return (UserGroup)super.clone();
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof UserGroup)) {
			return false;
		}

		UserGroup userGroup = (UserGroup)object;

		return Objects.equals(toString(), userGroup.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		return UserGroupSerDes.toJSON(this);
	}

}