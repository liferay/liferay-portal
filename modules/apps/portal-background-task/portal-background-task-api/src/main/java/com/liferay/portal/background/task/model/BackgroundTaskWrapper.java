/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.background.task.model;

import com.liferay.portal.kernel.model.ModelWrapper;
import com.liferay.portal.kernel.model.wrapper.BaseModelWrapper;

import java.io.Serializable;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 * This class is a wrapper for {@link BackgroundTask}.
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @see BackgroundTask
 * @generated
 */
public class BackgroundTaskWrapper
	extends BaseModelWrapper<BackgroundTask>
	implements BackgroundTask, ModelWrapper<BackgroundTask> {

	public BackgroundTaskWrapper(BackgroundTask backgroundTask) {
		super(backgroundTask);
	}

	@Override
	public Map<String, Object> getModelAttributes() {
		Map<String, Object> attributes = new HashMap<String, Object>();

		attributes.put("mvccVersion", getMvccVersion());
		attributes.put("backgroundTaskId", getBackgroundTaskId());
		attributes.put("groupId", getGroupId());
		attributes.put("companyId", getCompanyId());
		attributes.put("userId", getUserId());
		attributes.put("userName", getUserName());
		attributes.put("createDate", getCreateDate());
		attributes.put("modifiedDate", getModifiedDate());
		attributes.put("name", getName());
		attributes.put("servletContextNames", getServletContextNames());
		attributes.put("taskExecutorClassName", getTaskExecutorClassName());
		attributes.put("taskContextMap", getTaskContextMap());
		attributes.put("completed", isCompleted());
		attributes.put("completionDate", getCompletionDate());
		attributes.put("errorStackTrace", getErrorStackTrace());
		attributes.put("status", getStatus());
		attributes.put("statusMessage", getStatusMessage());

		return attributes;
	}

	@Override
	public void setModelAttributes(Map<String, Object> attributes) {
		Long mvccVersion = (Long)attributes.get("mvccVersion");

		if (mvccVersion != null) {
			setMvccVersion(mvccVersion);
		}

		Long backgroundTaskId = (Long)attributes.get("backgroundTaskId");

		if (backgroundTaskId != null) {
			setBackgroundTaskId(backgroundTaskId);
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

		Date createDate = (Date)attributes.get("createDate");

		if (createDate != null) {
			setCreateDate(createDate);
		}

		Date modifiedDate = (Date)attributes.get("modifiedDate");

		if (modifiedDate != null) {
			setModifiedDate(modifiedDate);
		}

		String name = (String)attributes.get("name");

		if (name != null) {
			setName(name);
		}

		String servletContextNames = (String)attributes.get(
			"servletContextNames");

		if (servletContextNames != null) {
			setServletContextNames(servletContextNames);
		}

		String taskExecutorClassName = (String)attributes.get(
			"taskExecutorClassName");

		if (taskExecutorClassName != null) {
			setTaskExecutorClassName(taskExecutorClassName);
		}

		Map<String, Serializable> taskContextMap =
			(Map<String, Serializable>)attributes.get("taskContextMap");

		if (taskContextMap != null) {
			setTaskContextMap(taskContextMap);
		}

		Boolean completed = (Boolean)attributes.get("completed");

		if (completed != null) {
			setCompleted(completed);
		}

		Date completionDate = (Date)attributes.get("completionDate");

		if (completionDate != null) {
			setCompletionDate(completionDate);
		}

		String errorStackTrace = (String)attributes.get("errorStackTrace");

		if (errorStackTrace != null) {
			setErrorStackTrace(errorStackTrace);
		}

		Integer status = (Integer)attributes.get("status");

		if (status != null) {
			setStatus(status);
		}

		String statusMessage = (String)attributes.get("statusMessage");

		if (statusMessage != null) {
			setStatusMessage(statusMessage);
		}
	}

	@Override
	public void addAttachment(long userId, String fileName, java.io.File file)
		throws com.liferay.portal.kernel.exception.PortalException {

		model.addAttachment(userId, fileName, file);
	}

	@Override
	public void addAttachment(
			long userId, String fileName, java.io.InputStream inputStream)
		throws com.liferay.portal.kernel.exception.PortalException {

		model.addAttachment(userId, fileName, inputStream);
	}

	@Override
	public com.liferay.portal.kernel.repository.model.Folder
			addAttachmentsFolder()
		throws com.liferay.portal.kernel.exception.PortalException {

		return model.addAttachmentsFolder();
	}

	@Override
	public BackgroundTask cloneWithOriginalValues() {
		return wrap(model.cloneWithOriginalValues());
	}

	@Override
	public java.util.List<com.liferay.portal.kernel.repository.model.FileEntry>
			getAttachmentsFileEntries()
		throws com.liferay.portal.kernel.exception.PortalException {

		return model.getAttachmentsFileEntries();
	}

	@Override
	public java.util.List<com.liferay.portal.kernel.repository.model.FileEntry>
			getAttachmentsFileEntries(int start, int end)
		throws com.liferay.portal.kernel.exception.PortalException {

		return model.getAttachmentsFileEntries(start, end);
	}

	@Override
	public int getAttachmentsFileEntriesCount()
		throws com.liferay.portal.kernel.exception.PortalException {

		return model.getAttachmentsFileEntriesCount();
	}

	@Override
	public long getAttachmentsFolderId() {
		return model.getAttachmentsFolderId();
	}

	/**
	 * Returns the background task ID of this background task.
	 *
	 * @return the background task ID of this background task
	 */
	@Override
	public long getBackgroundTaskId() {
		return model.getBackgroundTaskId();
	}

	/**
	 * Returns the company ID of this background task.
	 *
	 * @return the company ID of this background task
	 */
	@Override
	public long getCompanyId() {
		return model.getCompanyId();
	}

	/**
	 * Returns the completed of this background task.
	 *
	 * @return the completed of this background task
	 */
	@Override
	public boolean getCompleted() {
		return model.getCompleted();
	}

	/**
	 * Returns the completion date of this background task.
	 *
	 * @return the completion date of this background task
	 */
	@Override
	public Date getCompletionDate() {
		return model.getCompletionDate();
	}

	/**
	 * Returns the create date of this background task.
	 *
	 * @return the create date of this background task
	 */
	@Override
	public Date getCreateDate() {
		return model.getCreateDate();
	}

	/**
	 * Returns the error stack trace of this background task.
	 *
	 * @return the error stack trace of this background task
	 */
	@Override
	public String getErrorStackTrace() {
		return model.getErrorStackTrace();
	}

	/**
	 * Returns the group ID of this background task.
	 *
	 * @return the group ID of this background task
	 */
	@Override
	public long getGroupId() {
		return model.getGroupId();
	}

	/**
	 * Returns the modified date of this background task.
	 *
	 * @return the modified date of this background task
	 */
	@Override
	public Date getModifiedDate() {
		return model.getModifiedDate();
	}

	/**
	 * Returns the mvcc version of this background task.
	 *
	 * @return the mvcc version of this background task
	 */
	@Override
	public long getMvccVersion() {
		return model.getMvccVersion();
	}

	/**
	 * Returns the name of this background task.
	 *
	 * @return the name of this background task
	 */
	@Override
	public String getName() {
		return model.getName();
	}

	/**
	 * Returns the primary key of this background task.
	 *
	 * @return the primary key of this background task
	 */
	@Override
	public long getPrimaryKey() {
		return model.getPrimaryKey();
	}

	/**
	 * Returns the servlet context names of this background task.
	 *
	 * @return the servlet context names of this background task
	 */
	@Override
	public String getServletContextNames() {
		return model.getServletContextNames();
	}

	/**
	 * Returns the status of this background task.
	 *
	 * @return the status of this background task
	 */
	@Override
	public int getStatus() {
		return model.getStatus();
	}

	@Override
	public String getStatusLabel() {
		return model.getStatusLabel();
	}

	/**
	 * Returns the status message of this background task.
	 *
	 * @return the status message of this background task
	 */
	@Override
	public String getStatusMessage() {
		return model.getStatusMessage();
	}

	/**
	 * Returns the task context map of this background task.
	 *
	 * @return the task context map of this background task
	 */
	@Override
	public Map<String, Serializable> getTaskContextMap() {
		return model.getTaskContextMap();
	}

	/**
	 * Returns the task executor class name of this background task.
	 *
	 * @return the task executor class name of this background task
	 */
	@Override
	public String getTaskExecutorClassName() {
		return model.getTaskExecutorClassName();
	}

	/**
	 * Returns the user ID of this background task.
	 *
	 * @return the user ID of this background task
	 */
	@Override
	public long getUserId() {
		return model.getUserId();
	}

	/**
	 * Returns the user name of this background task.
	 *
	 * @return the user name of this background task
	 */
	@Override
	public String getUserName() {
		return model.getUserName();
	}

	/**
	 * Returns the user uuid of this background task.
	 *
	 * @return the user uuid of this background task
	 */
	@Override
	public String getUserUuid() {
		return model.getUserUuid();
	}

	/**
	 * Returns <code>true</code> if this background task is completed.
	 *
	 * @return <code>true</code> if this background task is completed; <code>false</code> otherwise
	 */
	@Override
	public boolean isCompleted() {
		return model.isCompleted();
	}

	@Override
	public boolean isInProgress() {
		return model.isInProgress();
	}

	@Override
	public void persist() {
		model.persist();
	}

	/**
	 * Sets the background task ID of this background task.
	 *
	 * @param backgroundTaskId the background task ID of this background task
	 */
	@Override
	public void setBackgroundTaskId(long backgroundTaskId) {
		model.setBackgroundTaskId(backgroundTaskId);
	}

	/**
	 * Sets the company ID of this background task.
	 *
	 * @param companyId the company ID of this background task
	 */
	@Override
	public void setCompanyId(long companyId) {
		model.setCompanyId(companyId);
	}

	/**
	 * Sets whether this background task is completed.
	 *
	 * @param completed the completed of this background task
	 */
	@Override
	public void setCompleted(boolean completed) {
		model.setCompleted(completed);
	}

	/**
	 * Sets the completion date of this background task.
	 *
	 * @param completionDate the completion date of this background task
	 */
	@Override
	public void setCompletionDate(Date completionDate) {
		model.setCompletionDate(completionDate);
	}

	/**
	 * Sets the create date of this background task.
	 *
	 * @param createDate the create date of this background task
	 */
	@Override
	public void setCreateDate(Date createDate) {
		model.setCreateDate(createDate);
	}

	/**
	 * Sets the error stack trace of this background task.
	 *
	 * @param errorStackTrace the error stack trace of this background task
	 */
	@Override
	public void setErrorStackTrace(String errorStackTrace) {
		model.setErrorStackTrace(errorStackTrace);
	}

	/**
	 * Sets the group ID of this background task.
	 *
	 * @param groupId the group ID of this background task
	 */
	@Override
	public void setGroupId(long groupId) {
		model.setGroupId(groupId);
	}

	/**
	 * Sets the modified date of this background task.
	 *
	 * @param modifiedDate the modified date of this background task
	 */
	@Override
	public void setModifiedDate(Date modifiedDate) {
		model.setModifiedDate(modifiedDate);
	}

	/**
	 * Sets the mvcc version of this background task.
	 *
	 * @param mvccVersion the mvcc version of this background task
	 */
	@Override
	public void setMvccVersion(long mvccVersion) {
		model.setMvccVersion(mvccVersion);
	}

	/**
	 * Sets the name of this background task.
	 *
	 * @param name the name of this background task
	 */
	@Override
	public void setName(String name) {
		model.setName(name);
	}

	/**
	 * Sets the primary key of this background task.
	 *
	 * @param primaryKey the primary key of this background task
	 */
	@Override
	public void setPrimaryKey(long primaryKey) {
		model.setPrimaryKey(primaryKey);
	}

	/**
	 * Sets the servlet context names of this background task.
	 *
	 * @param servletContextNames the servlet context names of this background task
	 */
	@Override
	public void setServletContextNames(String servletContextNames) {
		model.setServletContextNames(servletContextNames);
	}

	/**
	 * Sets the status of this background task.
	 *
	 * @param status the status of this background task
	 */
	@Override
	public void setStatus(int status) {
		model.setStatus(status);
	}

	/**
	 * Sets the status message of this background task.
	 *
	 * @param statusMessage the status message of this background task
	 */
	@Override
	public void setStatusMessage(String statusMessage) {
		model.setStatusMessage(statusMessage);
	}

	/**
	 * Sets the task context map of this background task.
	 *
	 * @param taskContextMap the task context map of this background task
	 */
	@Override
	public void setTaskContextMap(Map<String, Serializable> taskContextMap) {
		model.setTaskContextMap(taskContextMap);
	}

	/**
	 * Sets the task executor class name of this background task.
	 *
	 * @param taskExecutorClassName the task executor class name of this background task
	 */
	@Override
	public void setTaskExecutorClassName(String taskExecutorClassName) {
		model.setTaskExecutorClassName(taskExecutorClassName);
	}

	/**
	 * Sets the user ID of this background task.
	 *
	 * @param userId the user ID of this background task
	 */
	@Override
	public void setUserId(long userId) {
		model.setUserId(userId);
	}

	/**
	 * Sets the user name of this background task.
	 *
	 * @param userName the user name of this background task
	 */
	@Override
	public void setUserName(String userName) {
		model.setUserName(userName);
	}

	/**
	 * Sets the user uuid of this background task.
	 *
	 * @param userUuid the user uuid of this background task
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
	protected BackgroundTaskWrapper wrap(BackgroundTask backgroundTask) {
		return new BackgroundTaskWrapper(backgroundTask);
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:-1367761340