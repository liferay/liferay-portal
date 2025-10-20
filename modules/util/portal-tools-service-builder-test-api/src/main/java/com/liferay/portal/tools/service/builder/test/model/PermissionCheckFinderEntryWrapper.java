/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.tools.service.builder.test.model;

import com.liferay.portal.kernel.model.ModelWrapper;
import com.liferay.portal.kernel.model.wrapper.BaseModelWrapper;

import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 * This class is a wrapper for {@link PermissionCheckFinderEntry}.
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @see PermissionCheckFinderEntry
 * @generated
 */
public class PermissionCheckFinderEntryWrapper
	extends BaseModelWrapper<PermissionCheckFinderEntry>
	implements ModelWrapper<PermissionCheckFinderEntry>,
			   PermissionCheckFinderEntry {

	public PermissionCheckFinderEntryWrapper(
		PermissionCheckFinderEntry permissionCheckFinderEntry) {

		super(permissionCheckFinderEntry);
	}

	@Override
	public Map<String, Object> getModelAttributes() {
		Map<String, Object> attributes = new HashMap<String, Object>();

		attributes.put(
			"permissionCheckFinderEntryId", getPermissionCheckFinderEntryId());
		attributes.put("groupId", getGroupId());
		attributes.put("companyId", getCompanyId());
		attributes.put("userId", getUserId());
		attributes.put("integer", getInteger());
		attributes.put("name", getName());
		attributes.put("type", getType());

		return attributes;
	}

	@Override
	public void setModelAttributes(Map<String, Object> attributes) {
		Long permissionCheckFinderEntryId = (Long)attributes.get(
			"permissionCheckFinderEntryId");

		if (permissionCheckFinderEntryId != null) {
			setPermissionCheckFinderEntryId(permissionCheckFinderEntryId);
		}

		Long groupId = (Long)attributes.get("groupId");

		if (groupId != null) {
			setGroupId(groupId);
		}

		Long companyId = (Long)attributes.get("companyId");

		if (companyId != null) {
			setCompanyId(companyId);
		}

		Long userId = (Long)attributes.get("userId");

		if (userId != null) {
			setUserId(userId);
		}

		Integer integer = (Integer)attributes.get("integer");

		if (integer != null) {
			setInteger(integer);
		}

		String name = (String)attributes.get("name");

		if (name != null) {
			setName(name);
		}

		String type = (String)attributes.get("type");

		if (type != null) {
			setType(type);
		}
	}

	@Override
	public PermissionCheckFinderEntry cloneWithOriginalValues() {
		return wrap(model.cloneWithOriginalValues());
	}

	/**
	 * Returns the company ID of this permission check finder entry.
	 *
	 * @return the company ID of this permission check finder entry
	 */
	@Override
	public long getCompanyId() {
		return model.getCompanyId();
	}

	/**
	 * Returns the group ID of this permission check finder entry.
	 *
	 * @return the group ID of this permission check finder entry
	 */
	@Override
	public long getGroupId() {
		return model.getGroupId();
	}

	/**
	 * Returns the integer of this permission check finder entry.
	 *
	 * @return the integer of this permission check finder entry
	 */
	@Override
	public int getInteger() {
		return model.getInteger();
	}

	/**
	 * Returns the name of this permission check finder entry.
	 *
	 * @return the name of this permission check finder entry
	 */
	@Override
	public String getName() {
		return model.getName();
	}

	/**
	 * Returns the permission check finder entry ID of this permission check finder entry.
	 *
	 * @return the permission check finder entry ID of this permission check finder entry
	 */
	@Override
	public long getPermissionCheckFinderEntryId() {
		return model.getPermissionCheckFinderEntryId();
	}

	/**
	 * Returns the primary key of this permission check finder entry.
	 *
	 * @return the primary key of this permission check finder entry
	 */
	@Override
	public long getPrimaryKey() {
		return model.getPrimaryKey();
	}

	/**
	 * Returns the type of this permission check finder entry.
	 *
	 * @return the type of this permission check finder entry
	 */
	@Override
	public String getType() {
		return model.getType();
	}

	/**
	 * Returns the user ID of this permission check finder entry.
	 *
	 * @return the user ID of this permission check finder entry
	 */
	@Override
	public long getUserId() {
		return model.getUserId();
	}

	/**
	 * Returns the user uuid of this permission check finder entry.
	 *
	 * @return the user uuid of this permission check finder entry
	 */
	@Override
	public String getUserUuid() {
		return model.getUserUuid();
	}

	@Override
	public void persist() {
		model.persist();
	}

	/**
	 * Sets the company ID of this permission check finder entry.
	 *
	 * @param companyId the company ID of this permission check finder entry
	 */
	@Override
	public void setCompanyId(long companyId) {
		model.setCompanyId(companyId);
	}

	/**
	 * Sets the group ID of this permission check finder entry.
	 *
	 * @param groupId the group ID of this permission check finder entry
	 */
	@Override
	public void setGroupId(long groupId) {
		model.setGroupId(groupId);
	}

	/**
	 * Sets the integer of this permission check finder entry.
	 *
	 * @param integer the integer of this permission check finder entry
	 */
	@Override
	public void setInteger(int integer) {
		model.setInteger(integer);
	}

	/**
	 * Sets the name of this permission check finder entry.
	 *
	 * @param name the name of this permission check finder entry
	 */
	@Override
	public void setName(String name) {
		model.setName(name);
	}

	/**
	 * Sets the permission check finder entry ID of this permission check finder entry.
	 *
	 * @param permissionCheckFinderEntryId the permission check finder entry ID of this permission check finder entry
	 */
	@Override
	public void setPermissionCheckFinderEntryId(
		long permissionCheckFinderEntryId) {

		model.setPermissionCheckFinderEntryId(permissionCheckFinderEntryId);
	}

	/**
	 * Sets the primary key of this permission check finder entry.
	 *
	 * @param primaryKey the primary key of this permission check finder entry
	 */
	@Override
	public void setPrimaryKey(long primaryKey) {
		model.setPrimaryKey(primaryKey);
	}

	/**
	 * Sets the type of this permission check finder entry.
	 *
	 * @param type the type of this permission check finder entry
	 */
	@Override
	public void setType(String type) {
		model.setType(type);
	}

	/**
	 * Sets the user ID of this permission check finder entry.
	 *
	 * @param userId the user ID of this permission check finder entry
	 */
	@Override
	public void setUserId(long userId) {
		model.setUserId(userId);
	}

	/**
	 * Sets the user uuid of this permission check finder entry.
	 *
	 * @param userUuid the user uuid of this permission check finder entry
	 */
	@Override
	public void setUserUuid(String userUuid) {
		model.setUserUuid(userUuid);
	}

	@Override
	public String toXmlString() {
		return model.toXmlString();
	}

	@Override
	protected PermissionCheckFinderEntryWrapper wrap(
		PermissionCheckFinderEntry permissionCheckFinderEntry) {

		return new PermissionCheckFinderEntryWrapper(
			permissionCheckFinderEntry);
	}

}