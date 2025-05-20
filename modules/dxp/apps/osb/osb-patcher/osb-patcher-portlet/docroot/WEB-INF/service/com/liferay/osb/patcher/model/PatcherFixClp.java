/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.patcher.model;

import com.liferay.osb.patcher.service.ClpSerializer;
import com.liferay.osb.patcher.service.PatcherFixLocalServiceUtil;

import com.liferay.portal.kernel.bean.AutoEscapeBeanHandler;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.util.ProxyUtil;
import com.liferay.portal.kernel.util.StringBundler;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.model.BaseModel;
import com.liferay.portal.model.impl.BaseModelImpl;
import com.liferay.portal.util.PortalUtil;

import java.io.Serializable;

import java.lang.reflect.Method;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Calvin Keum
 */
public class PatcherFixClp extends BaseModelImpl<PatcherFix>
	implements PatcherFix {
	public PatcherFixClp() {
	}

	@Override
	public Class<?> getModelClass() {
		return PatcherFix.class;
	}

	@Override
	public String getModelClassName() {
		return PatcherFix.class.getName();
	}

	@Override
	public long getPrimaryKey() {
		return _patcherFixId;
	}

	@Override
	public void setPrimaryKey(long primaryKey) {
		setPatcherFixId(primaryKey);
	}

	@Override
	public Serializable getPrimaryKeyObj() {
		return _patcherFixId;
	}

	@Override
	public void setPrimaryKeyObj(Serializable primaryKeyObj) {
		setPrimaryKey(((Long)primaryKeyObj).longValue());
	}

	@Override
	public Map<String, Object> getModelAttributes() {
		Map<String, Object> attributes = new HashMap<String, Object>();

		attributes.put("patcherFixId", getPatcherFixId());
		attributes.put("companyId", getCompanyId());
		attributes.put("userId", getUserId());
		attributes.put("userName", getUserName());
		attributes.put("createDate", getCreateDate());
		attributes.put("modifiedDate", getModifiedDate());
		attributes.put("patcherProductVersionId", getPatcherProductVersionId());
		attributes.put("patcherProjectVersionId", getPatcherProjectVersionId());
		attributes.put("name", getName());
		attributes.put("key", getKey());
		attributes.put("keyVersion", getKeyVersion());
		attributes.put("type", getType());
		attributes.put("latestFix", getLatestFix());
		attributes.put("obsolete", getObsolete());
		attributes.put("committish", getCommittish());
		attributes.put("gitHash", getGitHash());
		attributes.put("gitRemoteURL", getGitRemoteURL());
		attributes.put("dependencies", getDependencies());
		attributes.put("requirements", getRequirements());
		attributes.put("requestKey", getRequestKey());
		attributes.put("jenkinsResults", getJenkinsResults());
		attributes.put("comments", getComments());
		attributes.put("fixPackStatus", getFixPackStatus());
		attributes.put("notified", getNotified());
		attributes.put("productVersion", getProductVersion());
		attributes.put("status", getStatus());
		attributes.put("statusByUserId", getStatusByUserId());
		attributes.put("statusByUserName", getStatusByUserName());
		attributes.put("statusDate", getStatusDate());

		return attributes;
	}

	@Override
	public void setModelAttributes(Map<String, Object> attributes) {
		Long patcherFixId = (Long)attributes.get("patcherFixId");

		if (patcherFixId != null) {
			setPatcherFixId(patcherFixId);
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

		Date createDate = (Date)attributes.get("createDate");

		if (createDate != null) {
			setCreateDate(createDate);
		}

		Date modifiedDate = (Date)attributes.get("modifiedDate");

		if (modifiedDate != null) {
			setModifiedDate(modifiedDate);
		}

		Long patcherProductVersionId = (Long)attributes.get(
				"patcherProductVersionId");

		if (patcherProductVersionId != null) {
			setPatcherProductVersionId(patcherProductVersionId);
		}

		Long patcherProjectVersionId = (Long)attributes.get(
				"patcherProjectVersionId");

		if (patcherProjectVersionId != null) {
			setPatcherProjectVersionId(patcherProjectVersionId);
		}

		String name = (String)attributes.get("name");

		if (name != null) {
			setName(name);
		}

		String key = (String)attributes.get("key");

		if (key != null) {
			setKey(key);
		}

		Double keyVersion = (Double)attributes.get("keyVersion");

		if (keyVersion != null) {
			setKeyVersion(keyVersion);
		}

		Integer type = (Integer)attributes.get("type");

		if (type != null) {
			setType(type);
		}

		Boolean latestFix = (Boolean)attributes.get("latestFix");

		if (latestFix != null) {
			setLatestFix(latestFix);
		}

		Boolean obsolete = (Boolean)attributes.get("obsolete");

		if (obsolete != null) {
			setObsolete(obsolete);
		}

		String committish = (String)attributes.get("committish");

		if (committish != null) {
			setCommittish(committish);
		}

		String gitHash = (String)attributes.get("gitHash");

		if (gitHash != null) {
			setGitHash(gitHash);
		}

		String gitRemoteURL = (String)attributes.get("gitRemoteURL");

		if (gitRemoteURL != null) {
			setGitRemoteURL(gitRemoteURL);
		}

		String dependencies = (String)attributes.get("dependencies");

		if (dependencies != null) {
			setDependencies(dependencies);
		}

		String requirements = (String)attributes.get("requirements");

		if (requirements != null) {
			setRequirements(requirements);
		}

		String requestKey = (String)attributes.get("requestKey");

		if (requestKey != null) {
			setRequestKey(requestKey);
		}

		String jenkinsResults = (String)attributes.get("jenkinsResults");

		if (jenkinsResults != null) {
			setJenkinsResults(jenkinsResults);
		}

		String comments = (String)attributes.get("comments");

		if (comments != null) {
			setComments(comments);
		}

		Integer fixPackStatus = (Integer)attributes.get("fixPackStatus");

		if (fixPackStatus != null) {
			setFixPackStatus(fixPackStatus);
		}

		Boolean notified = (Boolean)attributes.get("notified");

		if (notified != null) {
			setNotified(notified);
		}

		Integer productVersion = (Integer)attributes.get("productVersion");

		if (productVersion != null) {
			setProductVersion(productVersion);
		}

		Integer status = (Integer)attributes.get("status");

		if (status != null) {
			setStatus(status);
		}

		Long statusByUserId = (Long)attributes.get("statusByUserId");

		if (statusByUserId != null) {
			setStatusByUserId(statusByUserId);
		}

		String statusByUserName = (String)attributes.get("statusByUserName");

		if (statusByUserName != null) {
			setStatusByUserName(statusByUserName);
		}

		Date statusDate = (Date)attributes.get("statusDate");

		if (statusDate != null) {
			setStatusDate(statusDate);
		}
	}

	@Override
	public long getPatcherFixId() {
		return _patcherFixId;
	}

	@Override
	public void setPatcherFixId(long patcherFixId) {
		_patcherFixId = patcherFixId;

		if (_patcherFixRemoteModel != null) {
			try {
				Class<?> clazz = _patcherFixRemoteModel.getClass();

				Method method = clazz.getMethod("setPatcherFixId", long.class);

				method.invoke(_patcherFixRemoteModel, patcherFixId);
			}
			catch (Exception e) {
				throw new UnsupportedOperationException(e);
			}
		}
	}

	@Override
	public long getCompanyId() {
		return _companyId;
	}

	@Override
	public void setCompanyId(long companyId) {
		_companyId = companyId;

		if (_patcherFixRemoteModel != null) {
			try {
				Class<?> clazz = _patcherFixRemoteModel.getClass();

				Method method = clazz.getMethod("setCompanyId", long.class);

				method.invoke(_patcherFixRemoteModel, companyId);
			}
			catch (Exception e) {
				throw new UnsupportedOperationException(e);
			}
		}
	}

	@Override
	public long getUserId() {
		return _userId;
	}

	@Override
	public void setUserId(long userId) {
		_userId = userId;

		if (_patcherFixRemoteModel != null) {
			try {
				Class<?> clazz = _patcherFixRemoteModel.getClass();

				Method method = clazz.getMethod("setUserId", long.class);

				method.invoke(_patcherFixRemoteModel, userId);
			}
			catch (Exception e) {
				throw new UnsupportedOperationException(e);
			}
		}
	}

	@Override
	public String getUserUuid() throws SystemException {
		return PortalUtil.getUserValue(getUserId(), "uuid", _userUuid);
	}

	@Override
	public void setUserUuid(String userUuid) {
		_userUuid = userUuid;
	}

	@Override
	public String getUserName() {
		return _userName;
	}

	@Override
	public void setUserName(String userName) {
		_userName = userName;

		if (_patcherFixRemoteModel != null) {
			try {
				Class<?> clazz = _patcherFixRemoteModel.getClass();

				Method method = clazz.getMethod("setUserName", String.class);

				method.invoke(_patcherFixRemoteModel, userName);
			}
			catch (Exception e) {
				throw new UnsupportedOperationException(e);
			}
		}
	}

	@Override
	public Date getCreateDate() {
		return _createDate;
	}

	@Override
	public void setCreateDate(Date createDate) {
		_createDate = createDate;

		if (_patcherFixRemoteModel != null) {
			try {
				Class<?> clazz = _patcherFixRemoteModel.getClass();

				Method method = clazz.getMethod("setCreateDate", Date.class);

				method.invoke(_patcherFixRemoteModel, createDate);
			}
			catch (Exception e) {
				throw new UnsupportedOperationException(e);
			}
		}
	}

	@Override
	public Date getModifiedDate() {
		return _modifiedDate;
	}

	@Override
	public void setModifiedDate(Date modifiedDate) {
		_modifiedDate = modifiedDate;

		if (_patcherFixRemoteModel != null) {
			try {
				Class<?> clazz = _patcherFixRemoteModel.getClass();

				Method method = clazz.getMethod("setModifiedDate", Date.class);

				method.invoke(_patcherFixRemoteModel, modifiedDate);
			}
			catch (Exception e) {
				throw new UnsupportedOperationException(e);
			}
		}
	}

	@Override
	public long getPatcherProductVersionId() {
		return _patcherProductVersionId;
	}

	@Override
	public void setPatcherProductVersionId(long patcherProductVersionId) {
		_patcherProductVersionId = patcherProductVersionId;

		if (_patcherFixRemoteModel != null) {
			try {
				Class<?> clazz = _patcherFixRemoteModel.getClass();

				Method method = clazz.getMethod("setPatcherProductVersionId",
						long.class);

				method.invoke(_patcherFixRemoteModel, patcherProductVersionId);
			}
			catch (Exception e) {
				throw new UnsupportedOperationException(e);
			}
		}
	}

	@Override
	public long getPatcherProjectVersionId() {
		return _patcherProjectVersionId;
	}

	@Override
	public void setPatcherProjectVersionId(long patcherProjectVersionId) {
		_patcherProjectVersionId = patcherProjectVersionId;

		if (_patcherFixRemoteModel != null) {
			try {
				Class<?> clazz = _patcherFixRemoteModel.getClass();

				Method method = clazz.getMethod("setPatcherProjectVersionId",
						long.class);

				method.invoke(_patcherFixRemoteModel, patcherProjectVersionId);
			}
			catch (Exception e) {
				throw new UnsupportedOperationException(e);
			}
		}
	}

	@Override
	public String getName() {
		return _name;
	}

	@Override
	public void setName(String name) {
		_name = name;

		if (_patcherFixRemoteModel != null) {
			try {
				Class<?> clazz = _patcherFixRemoteModel.getClass();

				Method method = clazz.getMethod("setName", String.class);

				method.invoke(_patcherFixRemoteModel, name);
			}
			catch (Exception e) {
				throw new UnsupportedOperationException(e);
			}
		}
	}

	@Override
	public String getKey() {
		return _key;
	}

	@Override
	public void setKey(String key) {
		_key = key;

		if (_patcherFixRemoteModel != null) {
			try {
				Class<?> clazz = _patcherFixRemoteModel.getClass();

				Method method = clazz.getMethod("setKey", String.class);

				method.invoke(_patcherFixRemoteModel, key);
			}
			catch (Exception e) {
				throw new UnsupportedOperationException(e);
			}
		}
	}

	@Override
	public double getKeyVersion() {
		return _keyVersion;
	}

	@Override
	public void setKeyVersion(double keyVersion) {
		_keyVersion = keyVersion;

		if (_patcherFixRemoteModel != null) {
			try {
				Class<?> clazz = _patcherFixRemoteModel.getClass();

				Method method = clazz.getMethod("setKeyVersion", double.class);

				method.invoke(_patcherFixRemoteModel, keyVersion);
			}
			catch (Exception e) {
				throw new UnsupportedOperationException(e);
			}
		}
	}

	@Override
	public int getType() {
		return _type;
	}

	@Override
	public void setType(int type) {
		_type = type;

		if (_patcherFixRemoteModel != null) {
			try {
				Class<?> clazz = _patcherFixRemoteModel.getClass();

				Method method = clazz.getMethod("setType", int.class);

				method.invoke(_patcherFixRemoteModel, type);
			}
			catch (Exception e) {
				throw new UnsupportedOperationException(e);
			}
		}
	}

	@Override
	public boolean getLatestFix() {
		return _latestFix;
	}

	@Override
	public boolean isLatestFix() {
		return _latestFix;
	}

	@Override
	public void setLatestFix(boolean latestFix) {
		_latestFix = latestFix;

		if (_patcherFixRemoteModel != null) {
			try {
				Class<?> clazz = _patcherFixRemoteModel.getClass();

				Method method = clazz.getMethod("setLatestFix", boolean.class);

				method.invoke(_patcherFixRemoteModel, latestFix);
			}
			catch (Exception e) {
				throw new UnsupportedOperationException(e);
			}
		}
	}

	@Override
	public boolean getObsolete() {
		return _obsolete;
	}

	@Override
	public boolean isObsolete() {
		return _obsolete;
	}

	@Override
	public void setObsolete(boolean obsolete) {
		_obsolete = obsolete;

		if (_patcherFixRemoteModel != null) {
			try {
				Class<?> clazz = _patcherFixRemoteModel.getClass();

				Method method = clazz.getMethod("setObsolete", boolean.class);

				method.invoke(_patcherFixRemoteModel, obsolete);
			}
			catch (Exception e) {
				throw new UnsupportedOperationException(e);
			}
		}
	}

	@Override
	public String getCommittish() {
		return _committish;
	}

	@Override
	public void setCommittish(String committish) {
		_committish = committish;

		if (_patcherFixRemoteModel != null) {
			try {
				Class<?> clazz = _patcherFixRemoteModel.getClass();

				Method method = clazz.getMethod("setCommittish", String.class);

				method.invoke(_patcherFixRemoteModel, committish);
			}
			catch (Exception e) {
				throw new UnsupportedOperationException(e);
			}
		}
	}

	@Override
	public String getGitHash() {
		return _gitHash;
	}

	@Override
	public void setGitHash(String gitHash) {
		_gitHash = gitHash;

		if (_patcherFixRemoteModel != null) {
			try {
				Class<?> clazz = _patcherFixRemoteModel.getClass();

				Method method = clazz.getMethod("setGitHash", String.class);

				method.invoke(_patcherFixRemoteModel, gitHash);
			}
			catch (Exception e) {
				throw new UnsupportedOperationException(e);
			}
		}
	}

	@Override
	public String getGitRemoteURL() {
		return _gitRemoteURL;
	}

	@Override
	public void setGitRemoteURL(String gitRemoteURL) {
		_gitRemoteURL = gitRemoteURL;

		if (_patcherFixRemoteModel != null) {
			try {
				Class<?> clazz = _patcherFixRemoteModel.getClass();

				Method method = clazz.getMethod("setGitRemoteURL", String.class);

				method.invoke(_patcherFixRemoteModel, gitRemoteURL);
			}
			catch (Exception e) {
				throw new UnsupportedOperationException(e);
			}
		}
	}

	@Override
	public String getDependencies() {
		return _dependencies;
	}

	@Override
	public void setDependencies(String dependencies) {
		_dependencies = dependencies;

		if (_patcherFixRemoteModel != null) {
			try {
				Class<?> clazz = _patcherFixRemoteModel.getClass();

				Method method = clazz.getMethod("setDependencies", String.class);

				method.invoke(_patcherFixRemoteModel, dependencies);
			}
			catch (Exception e) {
				throw new UnsupportedOperationException(e);
			}
		}
	}

	@Override
	public String getRequirements() {
		return _requirements;
	}

	@Override
	public void setRequirements(String requirements) {
		_requirements = requirements;

		if (_patcherFixRemoteModel != null) {
			try {
				Class<?> clazz = _patcherFixRemoteModel.getClass();

				Method method = clazz.getMethod("setRequirements", String.class);

				method.invoke(_patcherFixRemoteModel, requirements);
			}
			catch (Exception e) {
				throw new UnsupportedOperationException(e);
			}
		}
	}

	@Override
	public String getRequestKey() {
		return _requestKey;
	}

	@Override
	public void setRequestKey(String requestKey) {
		_requestKey = requestKey;

		if (_patcherFixRemoteModel != null) {
			try {
				Class<?> clazz = _patcherFixRemoteModel.getClass();

				Method method = clazz.getMethod("setRequestKey", String.class);

				method.invoke(_patcherFixRemoteModel, requestKey);
			}
			catch (Exception e) {
				throw new UnsupportedOperationException(e);
			}
		}
	}

	@Override
	public String getJenkinsResults() {
		return _jenkinsResults;
	}

	@Override
	public void setJenkinsResults(String jenkinsResults) {
		_jenkinsResults = jenkinsResults;

		if (_patcherFixRemoteModel != null) {
			try {
				Class<?> clazz = _patcherFixRemoteModel.getClass();

				Method method = clazz.getMethod("setJenkinsResults",
						String.class);

				method.invoke(_patcherFixRemoteModel, jenkinsResults);
			}
			catch (Exception e) {
				throw new UnsupportedOperationException(e);
			}
		}
	}

	@Override
	public String getComments() {
		return _comments;
	}

	@Override
	public void setComments(String comments) {
		_comments = comments;

		if (_patcherFixRemoteModel != null) {
			try {
				Class<?> clazz = _patcherFixRemoteModel.getClass();

				Method method = clazz.getMethod("setComments", String.class);

				method.invoke(_patcherFixRemoteModel, comments);
			}
			catch (Exception e) {
				throw new UnsupportedOperationException(e);
			}
		}
	}

	@Override
	public int getFixPackStatus() {
		return _fixPackStatus;
	}

	@Override
	public void setFixPackStatus(int fixPackStatus) {
		_fixPackStatus = fixPackStatus;

		if (_patcherFixRemoteModel != null) {
			try {
				Class<?> clazz = _patcherFixRemoteModel.getClass();

				Method method = clazz.getMethod("setFixPackStatus", int.class);

				method.invoke(_patcherFixRemoteModel, fixPackStatus);
			}
			catch (Exception e) {
				throw new UnsupportedOperationException(e);
			}
		}
	}

	@Override
	public boolean getNotified() {
		return _notified;
	}

	@Override
	public boolean isNotified() {
		return _notified;
	}

	@Override
	public void setNotified(boolean notified) {
		_notified = notified;

		if (_patcherFixRemoteModel != null) {
			try {
				Class<?> clazz = _patcherFixRemoteModel.getClass();

				Method method = clazz.getMethod("setNotified", boolean.class);

				method.invoke(_patcherFixRemoteModel, notified);
			}
			catch (Exception e) {
				throw new UnsupportedOperationException(e);
			}
		}
	}

	@Override
	public int getProductVersion() {
		return _productVersion;
	}

	@Override
	public void setProductVersion(int productVersion) {
		_productVersion = productVersion;

		if (_patcherFixRemoteModel != null) {
			try {
				Class<?> clazz = _patcherFixRemoteModel.getClass();

				Method method = clazz.getMethod("setProductVersion", int.class);

				method.invoke(_patcherFixRemoteModel, productVersion);
			}
			catch (Exception e) {
				throw new UnsupportedOperationException(e);
			}
		}
	}

	@Override
	public int getStatus() {
		return _status;
	}

	@Override
	public void setStatus(int status) {
		_status = status;

		if (_patcherFixRemoteModel != null) {
			try {
				Class<?> clazz = _patcherFixRemoteModel.getClass();

				Method method = clazz.getMethod("setStatus", int.class);

				method.invoke(_patcherFixRemoteModel, status);
			}
			catch (Exception e) {
				throw new UnsupportedOperationException(e);
			}
		}
	}

	@Override
	public long getStatusByUserId() {
		return _statusByUserId;
	}

	@Override
	public void setStatusByUserId(long statusByUserId) {
		_statusByUserId = statusByUserId;

		if (_patcherFixRemoteModel != null) {
			try {
				Class<?> clazz = _patcherFixRemoteModel.getClass();

				Method method = clazz.getMethod("setStatusByUserId", long.class);

				method.invoke(_patcherFixRemoteModel, statusByUserId);
			}
			catch (Exception e) {
				throw new UnsupportedOperationException(e);
			}
		}
	}

	@Override
	public String getStatusByUserUuid() throws SystemException {
		return PortalUtil.getUserValue(getStatusByUserId(), "uuid",
			_statusByUserUuid);
	}

	@Override
	public void setStatusByUserUuid(String statusByUserUuid) {
		_statusByUserUuid = statusByUserUuid;
	}

	@Override
	public String getStatusByUserName() {
		return _statusByUserName;
	}

	@Override
	public void setStatusByUserName(String statusByUserName) {
		_statusByUserName = statusByUserName;

		if (_patcherFixRemoteModel != null) {
			try {
				Class<?> clazz = _patcherFixRemoteModel.getClass();

				Method method = clazz.getMethod("setStatusByUserName",
						String.class);

				method.invoke(_patcherFixRemoteModel, statusByUserName);
			}
			catch (Exception e) {
				throw new UnsupportedOperationException(e);
			}
		}
	}

	@Override
	public Date getStatusDate() {
		return _statusDate;
	}

	@Override
	public void setStatusDate(Date statusDate) {
		_statusDate = statusDate;

		if (_patcherFixRemoteModel != null) {
			try {
				Class<?> clazz = _patcherFixRemoteModel.getClass();

				Method method = clazz.getMethod("setStatusDate", Date.class);

				method.invoke(_patcherFixRemoteModel, statusDate);
			}
			catch (Exception e) {
				throw new UnsupportedOperationException(e);
			}
		}
	}

	/**
	 * @deprecated As of 6.1.0, replaced by {@link #isApproved}
	 */
	@Override
	public boolean getApproved() {
		return isApproved();
	}

	@Override
	public boolean isApproved() {
		if (getStatus() == WorkflowConstants.STATUS_APPROVED) {
			return true;
		}
		else {
			return false;
		}
	}

	@Override
	public boolean isDenied() {
		if (getStatus() == WorkflowConstants.STATUS_DENIED) {
			return true;
		}
		else {
			return false;
		}
	}

	@Override
	public boolean isDraft() {
		if (getStatus() == WorkflowConstants.STATUS_DRAFT) {
			return true;
		}
		else {
			return false;
		}
	}

	@Override
	public boolean isExpired() {
		if (getStatus() == WorkflowConstants.STATUS_EXPIRED) {
			return true;
		}
		else {
			return false;
		}
	}

	@Override
	public boolean isInactive() {
		if (getStatus() == WorkflowConstants.STATUS_INACTIVE) {
			return true;
		}
		else {
			return false;
		}
	}

	@Override
	public boolean isIncomplete() {
		if (getStatus() == WorkflowConstants.STATUS_INCOMPLETE) {
			return true;
		}
		else {
			return false;
		}
	}

	@Override
	public boolean isPending() {
		if (getStatus() == WorkflowConstants.STATUS_PENDING) {
			return true;
		}
		else {
			return false;
		}
	}

	@Override
	public boolean isScheduled() {
		if (getStatus() == WorkflowConstants.STATUS_SCHEDULED) {
			return true;
		}
		else {
			return false;
		}
	}

	public BaseModel<?> getPatcherFixRemoteModel() {
		return _patcherFixRemoteModel;
	}

	public void setPatcherFixRemoteModel(BaseModel<?> patcherFixRemoteModel) {
		_patcherFixRemoteModel = patcherFixRemoteModel;
	}

	public Object invokeOnRemoteModel(String methodName,
		Class<?>[] parameterTypes, Object[] parameterValues)
		throws Exception {
		Object[] remoteParameterValues = new Object[parameterValues.length];

		for (int i = 0; i < parameterValues.length; i++) {
			if (parameterValues[i] != null) {
				remoteParameterValues[i] = ClpSerializer.translateInput(parameterValues[i]);
			}
		}

		Class<?> remoteModelClass = _patcherFixRemoteModel.getClass();

		ClassLoader remoteModelClassLoader = remoteModelClass.getClassLoader();

		Class<?>[] remoteParameterTypes = new Class[parameterTypes.length];

		for (int i = 0; i < parameterTypes.length; i++) {
			if (parameterTypes[i].isPrimitive()) {
				remoteParameterTypes[i] = parameterTypes[i];
			}
			else {
				String parameterTypeName = parameterTypes[i].getName();

				remoteParameterTypes[i] = remoteModelClassLoader.loadClass(parameterTypeName);
			}
		}

		Method method = remoteModelClass.getMethod(methodName,
				remoteParameterTypes);

		Object returnValue = method.invoke(_patcherFixRemoteModel,
				remoteParameterValues);

		if (returnValue != null) {
			returnValue = ClpSerializer.translateOutput(returnValue);
		}

		return returnValue;
	}

	@Override
	public void persist() throws SystemException {
		if (this.isNew()) {
			PatcherFixLocalServiceUtil.addPatcherFix(this);
		}
		else {
			PatcherFixLocalServiceUtil.updatePatcherFix(this);
		}
	}

	@Override
	public PatcherFix toEscapedModel() {
		return (PatcherFix)ProxyUtil.newProxyInstance(PatcherFix.class.getClassLoader(),
			new Class[] { PatcherFix.class }, new AutoEscapeBeanHandler(this));
	}

	@Override
	public Object clone() {
		PatcherFixClp clone = new PatcherFixClp();

		clone.setPatcherFixId(getPatcherFixId());
		clone.setCompanyId(getCompanyId());
		clone.setUserId(getUserId());
		clone.setUserName(getUserName());
		clone.setCreateDate(getCreateDate());
		clone.setModifiedDate(getModifiedDate());
		clone.setPatcherProductVersionId(getPatcherProductVersionId());
		clone.setPatcherProjectVersionId(getPatcherProjectVersionId());
		clone.setName(getName());
		clone.setKey(getKey());
		clone.setKeyVersion(getKeyVersion());
		clone.setType(getType());
		clone.setLatestFix(getLatestFix());
		clone.setObsolete(getObsolete());
		clone.setCommittish(getCommittish());
		clone.setGitHash(getGitHash());
		clone.setGitRemoteURL(getGitRemoteURL());
		clone.setDependencies(getDependencies());
		clone.setRequirements(getRequirements());
		clone.setRequestKey(getRequestKey());
		clone.setJenkinsResults(getJenkinsResults());
		clone.setComments(getComments());
		clone.setFixPackStatus(getFixPackStatus());
		clone.setNotified(getNotified());
		clone.setProductVersion(getProductVersion());
		clone.setStatus(getStatus());
		clone.setStatusByUserId(getStatusByUserId());
		clone.setStatusByUserName(getStatusByUserName());
		clone.setStatusDate(getStatusDate());

		return clone;
	}

	@Override
	public int compareTo(PatcherFix patcherFix) {
		long primaryKey = patcherFix.getPrimaryKey();

		if (getPrimaryKey() < primaryKey) {
			return -1;
		}
		else if (getPrimaryKey() > primaryKey) {
			return 1;
		}
		else {
			return 0;
		}
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}

		if (!(obj instanceof PatcherFixClp)) {
			return false;
		}

		PatcherFixClp patcherFix = (PatcherFixClp)obj;

		long primaryKey = patcherFix.getPrimaryKey();

		if (getPrimaryKey() == primaryKey) {
			return true;
		}
		else {
			return false;
		}
	}

	public Class<?> getClpSerializerClass() {
		return _clpSerializerClass;
	}

	@Override
	public int hashCode() {
		return (int)getPrimaryKey();
	}

	@Override
	public String toString() {
		StringBundler sb = new StringBundler(59);

		sb.append("{patcherFixId=");
		sb.append(getPatcherFixId());
		sb.append(", companyId=");
		sb.append(getCompanyId());
		sb.append(", userId=");
		sb.append(getUserId());
		sb.append(", userName=");
		sb.append(getUserName());
		sb.append(", createDate=");
		sb.append(getCreateDate());
		sb.append(", modifiedDate=");
		sb.append(getModifiedDate());
		sb.append(", patcherProductVersionId=");
		sb.append(getPatcherProductVersionId());
		sb.append(", patcherProjectVersionId=");
		sb.append(getPatcherProjectVersionId());
		sb.append(", name=");
		sb.append(getName());
		sb.append(", key=");
		sb.append(getKey());
		sb.append(", keyVersion=");
		sb.append(getKeyVersion());
		sb.append(", type=");
		sb.append(getType());
		sb.append(", latestFix=");
		sb.append(getLatestFix());
		sb.append(", obsolete=");
		sb.append(getObsolete());
		sb.append(", committish=");
		sb.append(getCommittish());
		sb.append(", gitHash=");
		sb.append(getGitHash());
		sb.append(", gitRemoteURL=");
		sb.append(getGitRemoteURL());
		sb.append(", dependencies=");
		sb.append(getDependencies());
		sb.append(", requirements=");
		sb.append(getRequirements());
		sb.append(", requestKey=");
		sb.append(getRequestKey());
		sb.append(", jenkinsResults=");
		sb.append(getJenkinsResults());
		sb.append(", comments=");
		sb.append(getComments());
		sb.append(", fixPackStatus=");
		sb.append(getFixPackStatus());
		sb.append(", notified=");
		sb.append(getNotified());
		sb.append(", productVersion=");
		sb.append(getProductVersion());
		sb.append(", status=");
		sb.append(getStatus());
		sb.append(", statusByUserId=");
		sb.append(getStatusByUserId());
		sb.append(", statusByUserName=");
		sb.append(getStatusByUserName());
		sb.append(", statusDate=");
		sb.append(getStatusDate());
		sb.append("}");

		return sb.toString();
	}

	@Override
	public String toXmlString() {
		StringBundler sb = new StringBundler(91);

		sb.append("<model><model-name>");
		sb.append("com.liferay.osb.patcher.model.PatcherFix");
		sb.append("</model-name>");

		sb.append(
			"<column><column-name>patcherFixId</column-name><column-value><![CDATA[");
		sb.append(getPatcherFixId());
		sb.append("]]></column-value></column>");
		sb.append(
			"<column><column-name>companyId</column-name><column-value><![CDATA[");
		sb.append(getCompanyId());
		sb.append("]]></column-value></column>");
		sb.append(
			"<column><column-name>userId</column-name><column-value><![CDATA[");
		sb.append(getUserId());
		sb.append("]]></column-value></column>");
		sb.append(
			"<column><column-name>userName</column-name><column-value><![CDATA[");
		sb.append(getUserName());
		sb.append("]]></column-value></column>");
		sb.append(
			"<column><column-name>createDate</column-name><column-value><![CDATA[");
		sb.append(getCreateDate());
		sb.append("]]></column-value></column>");
		sb.append(
			"<column><column-name>modifiedDate</column-name><column-value><![CDATA[");
		sb.append(getModifiedDate());
		sb.append("]]></column-value></column>");
		sb.append(
			"<column><column-name>patcherProductVersionId</column-name><column-value><![CDATA[");
		sb.append(getPatcherProductVersionId());
		sb.append("]]></column-value></column>");
		sb.append(
			"<column><column-name>patcherProjectVersionId</column-name><column-value><![CDATA[");
		sb.append(getPatcherProjectVersionId());
		sb.append("]]></column-value></column>");
		sb.append(
			"<column><column-name>name</column-name><column-value><![CDATA[");
		sb.append(getName());
		sb.append("]]></column-value></column>");
		sb.append(
			"<column><column-name>key</column-name><column-value><![CDATA[");
		sb.append(getKey());
		sb.append("]]></column-value></column>");
		sb.append(
			"<column><column-name>keyVersion</column-name><column-value><![CDATA[");
		sb.append(getKeyVersion());
		sb.append("]]></column-value></column>");
		sb.append(
			"<column><column-name>type</column-name><column-value><![CDATA[");
		sb.append(getType());
		sb.append("]]></column-value></column>");
		sb.append(
			"<column><column-name>latestFix</column-name><column-value><![CDATA[");
		sb.append(getLatestFix());
		sb.append("]]></column-value></column>");
		sb.append(
			"<column><column-name>obsolete</column-name><column-value><![CDATA[");
		sb.append(getObsolete());
		sb.append("]]></column-value></column>");
		sb.append(
			"<column><column-name>committish</column-name><column-value><![CDATA[");
		sb.append(getCommittish());
		sb.append("]]></column-value></column>");
		sb.append(
			"<column><column-name>gitHash</column-name><column-value><![CDATA[");
		sb.append(getGitHash());
		sb.append("]]></column-value></column>");
		sb.append(
			"<column><column-name>gitRemoteURL</column-name><column-value><![CDATA[");
		sb.append(getGitRemoteURL());
		sb.append("]]></column-value></column>");
		sb.append(
			"<column><column-name>dependencies</column-name><column-value><![CDATA[");
		sb.append(getDependencies());
		sb.append("]]></column-value></column>");
		sb.append(
			"<column><column-name>requirements</column-name><column-value><![CDATA[");
		sb.append(getRequirements());
		sb.append("]]></column-value></column>");
		sb.append(
			"<column><column-name>requestKey</column-name><column-value><![CDATA[");
		sb.append(getRequestKey());
		sb.append("]]></column-value></column>");
		sb.append(
			"<column><column-name>jenkinsResults</column-name><column-value><![CDATA[");
		sb.append(getJenkinsResults());
		sb.append("]]></column-value></column>");
		sb.append(
			"<column><column-name>comments</column-name><column-value><![CDATA[");
		sb.append(getComments());
		sb.append("]]></column-value></column>");
		sb.append(
			"<column><column-name>fixPackStatus</column-name><column-value><![CDATA[");
		sb.append(getFixPackStatus());
		sb.append("]]></column-value></column>");
		sb.append(
			"<column><column-name>notified</column-name><column-value><![CDATA[");
		sb.append(getNotified());
		sb.append("]]></column-value></column>");
		sb.append(
			"<column><column-name>productVersion</column-name><column-value><![CDATA[");
		sb.append(getProductVersion());
		sb.append("]]></column-value></column>");
		sb.append(
			"<column><column-name>status</column-name><column-value><![CDATA[");
		sb.append(getStatus());
		sb.append("]]></column-value></column>");
		sb.append(
			"<column><column-name>statusByUserId</column-name><column-value><![CDATA[");
		sb.append(getStatusByUserId());
		sb.append("]]></column-value></column>");
		sb.append(
			"<column><column-name>statusByUserName</column-name><column-value><![CDATA[");
		sb.append(getStatusByUserName());
		sb.append("]]></column-value></column>");
		sb.append(
			"<column><column-name>statusDate</column-name><column-value><![CDATA[");
		sb.append(getStatusDate());
		sb.append("]]></column-value></column>");

		sb.append("</model>");

		return sb.toString();
	}

	private long _patcherFixId;
	private long _companyId;
	private long _userId;
	private String _userUuid;
	private String _userName;
	private Date _createDate;
	private Date _modifiedDate;
	private long _patcherProductVersionId;
	private long _patcherProjectVersionId;
	private String _name;
	private String _key;
	private double _keyVersion;
	private int _type;
	private boolean _latestFix;
	private boolean _obsolete;
	private String _committish;
	private String _gitHash;
	private String _gitRemoteURL;
	private String _dependencies;
	private String _requirements;
	private String _requestKey;
	private String _jenkinsResults;
	private String _comments;
	private int _fixPackStatus;
	private boolean _notified;
	private int _productVersion;
	private int _status;
	private long _statusByUserId;
	private String _statusByUserUuid;
	private String _statusByUserName;
	private Date _statusDate;
	private BaseModel<?> _patcherFixRemoteModel;
	private Class<?> _clpSerializerClass = com.liferay.osb.patcher.service.ClpSerializer.class;
}