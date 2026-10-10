/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.tools.service.builder.test.model;

import com.liferay.portal.kernel.model.ModelWrapper;
import com.liferay.portal.kernel.model.wrapper.BaseModelWrapper;

import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 * This class is a wrapper for {@link FilterPrimaryEntry}.
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @see FilterPrimaryEntry
 * @generated
 */
public class FilterPrimaryEntryWrapper
	extends BaseModelWrapper<FilterPrimaryEntry>
	implements FilterPrimaryEntry, ModelWrapper<FilterPrimaryEntry> {

	public FilterPrimaryEntryWrapper(FilterPrimaryEntry filterPrimaryEntry) {
		super(filterPrimaryEntry);
	}

	@Override
	public Map<String, Object> getModelAttributes() {
		Map<String, Object> attributes = new HashMap<String, Object>();

		attributes.put("filterPrimaryEntryId", getFilterPrimaryEntryId());
		attributes.put("groupId", getGroupId());
		attributes.put("companyId", getCompanyId());
		attributes.put("userId", getUserId());
		attributes.put("resourcePrimKey", getResourcePrimKey());

		return attributes;
	}

	@Override
	public void setModelAttributes(Map<String, Object> attributes) {
		Long filterPrimaryEntryId = (Long)attributes.get(
			"filterPrimaryEntryId");

		if (filterPrimaryEntryId != null) {
			setFilterPrimaryEntryId(filterPrimaryEntryId);
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

		Long resourcePrimKey = (Long)attributes.get("resourcePrimKey");

		if (resourcePrimKey != null) {
			setResourcePrimKey(resourcePrimKey);
		}
	}

	@Override
	public FilterPrimaryEntry cloneWithOriginalValues() {
		return wrap(model.cloneWithOriginalValues());
	}

	/**
	 * Returns the company ID of this filter primary entry.
	 *
	 * @return the company ID of this filter primary entry
	 */
	@Override
	public long getCompanyId() {
		return model.getCompanyId();
	}

	/**
	 * Returns the filter primary entry ID of this filter primary entry.
	 *
	 * @return the filter primary entry ID of this filter primary entry
	 */
	@Override
	public long getFilterPrimaryEntryId() {
		return model.getFilterPrimaryEntryId();
	}

	/**
	 * Returns the group ID of this filter primary entry.
	 *
	 * @return the group ID of this filter primary entry
	 */
	@Override
	public long getGroupId() {
		return model.getGroupId();
	}

	/**
	 * Returns the primary key of this filter primary entry.
	 *
	 * @return the primary key of this filter primary entry
	 */
	@Override
	public long getPrimaryKey() {
		return model.getPrimaryKey();
	}

	/**
	 * Returns the resource prim key of this filter primary entry.
	 *
	 * @return the resource prim key of this filter primary entry
	 */
	@Override
	public long getResourcePrimKey() {
		return model.getResourcePrimKey();
	}

	/**
	 * Returns the user ID of this filter primary entry.
	 *
	 * @return the user ID of this filter primary entry
	 */
	@Override
	public long getUserId() {
		return model.getUserId();
	}

	/**
	 * Returns the user uuid of this filter primary entry.
	 *
	 * @return the user uuid of this filter primary entry
	 */
	@Override
	public String getUserUuid() {
		return model.getUserUuid();
	}

	@Override
	public boolean isResourceMain() {
		return model.isResourceMain();
	}

	@Override
	public void persist() {
		model.persist();
	}

	/**
	 * Sets the company ID of this filter primary entry.
	 *
	 * @param companyId the company ID of this filter primary entry
	 */
	@Override
	public void setCompanyId(long companyId) {
		model.setCompanyId(companyId);
	}

	/**
	 * Sets the filter primary entry ID of this filter primary entry.
	 *
	 * @param filterPrimaryEntryId the filter primary entry ID of this filter primary entry
	 */
	@Override
	public void setFilterPrimaryEntryId(long filterPrimaryEntryId) {
		model.setFilterPrimaryEntryId(filterPrimaryEntryId);
	}

	/**
	 * Sets the group ID of this filter primary entry.
	 *
	 * @param groupId the group ID of this filter primary entry
	 */
	@Override
	public void setGroupId(long groupId) {
		model.setGroupId(groupId);
	}

	/**
	 * Sets the primary key of this filter primary entry.
	 *
	 * @param primaryKey the primary key of this filter primary entry
	 */
	@Override
	public void setPrimaryKey(long primaryKey) {
		model.setPrimaryKey(primaryKey);
	}

	/**
	 * Sets the resource prim key of this filter primary entry.
	 *
	 * @param resourcePrimKey the resource prim key of this filter primary entry
	 */
	@Override
	public void setResourcePrimKey(long resourcePrimKey) {
		model.setResourcePrimKey(resourcePrimKey);
	}

	/**
	 * Sets the user ID of this filter primary entry.
	 *
	 * @param userId the user ID of this filter primary entry
	 */
	@Override
	public void setUserId(long userId) {
		model.setUserId(userId);
	}

	/**
	 * Sets the user uuid of this filter primary entry.
	 *
	 * @param userUuid the user uuid of this filter primary entry
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
	protected FilterPrimaryEntryWrapper wrap(
		FilterPrimaryEntry filterPrimaryEntry) {

		return new FilterPrimaryEntryWrapper(filterPrimaryEntry);
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:-840409080