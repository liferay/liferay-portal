/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.model;

import com.liferay.portal.kernel.model.wrapper.BaseModelWrapper;

import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 * This class is a wrapper for {@link LayoutBranch}.
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @see LayoutBranch
 * @deprecated As of Cavanaugh (7.4.x), with no direct replacement
 * @generated
 */
@Deprecated
public class LayoutBranchWrapper
	extends BaseModelWrapper<LayoutBranch>
	implements LayoutBranch, ModelWrapper<LayoutBranch> {

	public LayoutBranchWrapper(LayoutBranch layoutBranch) {
		super(layoutBranch);
	}

	@Override
	public Map<String, Object> getModelAttributes() {
		Map<String, Object> attributes = new HashMap<String, Object>();

		attributes.put("mvccVersion", getMvccVersion());
		attributes.put("layoutBranchId", getLayoutBranchId());
		attributes.put("groupId", getGroupId());
		attributes.put("companyId", getCompanyId());
		attributes.put("userId", getUserId());
		attributes.put("userName", getUserName());
		attributes.put("layoutSetBranchId", getLayoutSetBranchId());
		attributes.put("plid", getPlid());
		attributes.put("name", getName());
		attributes.put("description", getDescription());
		attributes.put("master", isMaster());

		return attributes;
	}

	@Override
	public void setModelAttributes(Map<String, Object> attributes) {
		Long mvccVersion = (Long)attributes.get("mvccVersion");

		if (mvccVersion != null) {
			setMvccVersion(mvccVersion);
		}

		Long layoutBranchId = (Long)attributes.get("layoutBranchId");

		if (layoutBranchId != null) {
			setLayoutBranchId(layoutBranchId);
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

		String userName = (String)attributes.get("userName");

		if (userName != null) {
			setUserName(userName);
		}

		Long layoutSetBranchId = (Long)attributes.get("layoutSetBranchId");

		if (layoutSetBranchId != null) {
			setLayoutSetBranchId(layoutSetBranchId);
		}

		Long plid = (Long)attributes.get("plid");

		if (plid != null) {
			setPlid(plid);
		}

		String name = (String)attributes.get("name");

		if (name != null) {
			setName(name);
		}

		String description = (String)attributes.get("description");

		if (description != null) {
			setDescription(description);
		}

		Boolean master = (Boolean)attributes.get("master");

		if (master != null) {
			setMaster(master);
		}
	}

	@Override
	public LayoutBranch cloneWithOriginalValues() {
		return wrap(model.cloneWithOriginalValues());
	}

	/**
	 * Returns the company ID of this layout branch.
	 *
	 * @return the company ID of this layout branch
	 */
	@Override
	public long getCompanyId() {
		return model.getCompanyId();
	}

	/**
	 * Returns the description of this layout branch.
	 *
	 * @return the description of this layout branch
	 */
	@Override
	public String getDescription() {
		return model.getDescription();
	}

	/**
	 * Returns the group ID of this layout branch.
	 *
	 * @return the group ID of this layout branch
	 */
	@Override
	public long getGroupId() {
		return model.getGroupId();
	}

	/**
	 * Returns the layout branch ID of this layout branch.
	 *
	 * @return the layout branch ID of this layout branch
	 */
	@Override
	public long getLayoutBranchId() {
		return model.getLayoutBranchId();
	}

	/**
	 * Returns the layout set branch ID of this layout branch.
	 *
	 * @return the layout set branch ID of this layout branch
	 */
	@Override
	public long getLayoutSetBranchId() {
		return model.getLayoutSetBranchId();
	}

	/**
	 * Returns the master of this layout branch.
	 *
	 * @return the master of this layout branch
	 */
	@Override
	public boolean getMaster() {
		return model.getMaster();
	}

	/**
	 * Returns the mvcc version of this layout branch.
	 *
	 * @return the mvcc version of this layout branch
	 */
	@Override
	public long getMvccVersion() {
		return model.getMvccVersion();
	}

	/**
	 * Returns the name of this layout branch.
	 *
	 * @return the name of this layout branch
	 */
	@Override
	public String getName() {
		return model.getName();
	}

	/**
	 * Returns the plid of this layout branch.
	 *
	 * @return the plid of this layout branch
	 */
	@Override
	public long getPlid() {
		return model.getPlid();
	}

	/**
	 * Returns the primary key of this layout branch.
	 *
	 * @return the primary key of this layout branch
	 */
	@Override
	public long getPrimaryKey() {
		return model.getPrimaryKey();
	}

	/**
	 * Returns the user ID of this layout branch.
	 *
	 * @return the user ID of this layout branch
	 */
	@Override
	public long getUserId() {
		return model.getUserId();
	}

	/**
	 * Returns the user name of this layout branch.
	 *
	 * @return the user name of this layout branch
	 */
	@Override
	public String getUserName() {
		return model.getUserName();
	}

	/**
	 * Returns the user uuid of this layout branch.
	 *
	 * @return the user uuid of this layout branch
	 */
	@Override
	public String getUserUuid() {
		return model.getUserUuid();
	}

	/**
	 * Returns <code>true</code> if this layout branch is master.
	 *
	 * @return <code>true</code> if this layout branch is master; <code>false</code> otherwise
	 */
	@Override
	public boolean isMaster() {
		return model.isMaster();
	}

	@Override
	public void persist() {
		model.persist();
	}

	/**
	 * Sets the company ID of this layout branch.
	 *
	 * @param companyId the company ID of this layout branch
	 */
	@Override
	public void setCompanyId(long companyId) {
		model.setCompanyId(companyId);
	}

	/**
	 * Sets the description of this layout branch.
	 *
	 * @param description the description of this layout branch
	 */
	@Override
	public void setDescription(String description) {
		model.setDescription(description);
	}

	/**
	 * Sets the group ID of this layout branch.
	 *
	 * @param groupId the group ID of this layout branch
	 */
	@Override
	public void setGroupId(long groupId) {
		model.setGroupId(groupId);
	}

	/**
	 * Sets the layout branch ID of this layout branch.
	 *
	 * @param layoutBranchId the layout branch ID of this layout branch
	 */
	@Override
	public void setLayoutBranchId(long layoutBranchId) {
		model.setLayoutBranchId(layoutBranchId);
	}

	/**
	 * Sets the layout set branch ID of this layout branch.
	 *
	 * @param layoutSetBranchId the layout set branch ID of this layout branch
	 */
	@Override
	public void setLayoutSetBranchId(long layoutSetBranchId) {
		model.setLayoutSetBranchId(layoutSetBranchId);
	}

	/**
	 * Sets whether this layout branch is master.
	 *
	 * @param master the master of this layout branch
	 */
	@Override
	public void setMaster(boolean master) {
		model.setMaster(master);
	}

	/**
	 * Sets the mvcc version of this layout branch.
	 *
	 * @param mvccVersion the mvcc version of this layout branch
	 */
	@Override
	public void setMvccVersion(long mvccVersion) {
		model.setMvccVersion(mvccVersion);
	}

	/**
	 * Sets the name of this layout branch.
	 *
	 * @param name the name of this layout branch
	 */
	@Override
	public void setName(String name) {
		model.setName(name);
	}

	/**
	 * Sets the plid of this layout branch.
	 *
	 * @param plid the plid of this layout branch
	 */
	@Override
	public void setPlid(long plid) {
		model.setPlid(plid);
	}

	/**
	 * Sets the primary key of this layout branch.
	 *
	 * @param primaryKey the primary key of this layout branch
	 */
	@Override
	public void setPrimaryKey(long primaryKey) {
		model.setPrimaryKey(primaryKey);
	}

	/**
	 * Sets the user ID of this layout branch.
	 *
	 * @param userId the user ID of this layout branch
	 */
	@Override
	public void setUserId(long userId) {
		model.setUserId(userId);
	}

	/**
	 * Sets the user name of this layout branch.
	 *
	 * @param userName the user name of this layout branch
	 */
	@Override
	public void setUserName(String userName) {
		model.setUserName(userName);
	}

	/**
	 * Sets the user uuid of this layout branch.
	 *
	 * @param userUuid the user uuid of this layout branch
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
	protected LayoutBranchWrapper wrap(LayoutBranch layoutBranch) {
		return new LayoutBranchWrapper(layoutBranch);
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:1027429418