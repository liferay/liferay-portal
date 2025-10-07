/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.model;

import com.liferay.portal.kernel.model.wrapper.BaseModelWrapper;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 * This class is a wrapper for {@link Release}.
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @see Release
 * @generated
 */
public class ReleaseWrapper
	extends BaseModelWrapper<Release>
	implements ModelWrapper<Release>, Release {

	public ReleaseWrapper(Release release) {
		super(release);
	}

	@Override
	public Map<String, Object> getModelAttributes() {
		Map<String, Object> attributes = new HashMap<String, Object>();

		attributes.put("mvccVersion", getMvccVersion());
		attributes.put("releaseId", getReleaseId());
		attributes.put("createDate", getCreateDate());
		attributes.put("modifiedDate", getModifiedDate());
		attributes.put("servletContextName", getServletContextName());
		attributes.put("schemaVersion", getSchemaVersion());
		attributes.put("buildNumber", getBuildNumber());
		attributes.put("buildDate", getBuildDate());
		attributes.put("versionDisplayName", getVersionDisplayName());
		attributes.put("verified", isVerified());
		attributes.put("state", getState());
		attributes.put("testString", getTestString());

		return attributes;
	}

	@Override
	public void setModelAttributes(Map<String, Object> attributes) {
		Long mvccVersion = (Long)attributes.get("mvccVersion");

		if (mvccVersion != null) {
			setMvccVersion(mvccVersion);
		}

		Long releaseId = (Long)attributes.get("releaseId");

		if (releaseId != null) {
			setReleaseId(releaseId);
		}

		Date createDate = (Date)attributes.get("createDate");

		if (createDate != null) {
			setCreateDate(createDate);
		}

		Date modifiedDate = (Date)attributes.get("modifiedDate");

		if (modifiedDate != null) {
			setModifiedDate(modifiedDate);
		}

		String servletContextName = (String)attributes.get(
			"servletContextName");

		if (servletContextName != null) {
			setServletContextName(servletContextName);
		}

		String schemaVersion = (String)attributes.get("schemaVersion");

		if (schemaVersion != null) {
			setSchemaVersion(schemaVersion);
		}

		Integer buildNumber = (Integer)attributes.get("buildNumber");

		if (buildNumber != null) {
			setBuildNumber(buildNumber);
		}

		Date buildDate = (Date)attributes.get("buildDate");

		if (buildDate != null) {
			setBuildDate(buildDate);
		}

		String versionDisplayName = (String)attributes.get(
			"versionDisplayName");

		if (versionDisplayName != null) {
			setVersionDisplayName(versionDisplayName);
		}

		Boolean verified = (Boolean)attributes.get("verified");

		if (verified != null) {
			setVerified(verified);
		}

		Integer state = (Integer)attributes.get("state");

		if (state != null) {
			setState(state);
		}

		String testString = (String)attributes.get("testString");

		if (testString != null) {
			setTestString(testString);
		}
	}

	@Override
	public Release cloneWithOriginalValues() {
		return wrap(model.cloneWithOriginalValues());
	}

	/**
	 * Returns the build date of this release.
	 *
	 * @return the build date of this release
	 */
	@Override
	public Date getBuildDate() {
		return model.getBuildDate();
	}

	/**
	 * Returns the build number of this release.
	 *
	 * @return the build number of this release
	 */
	@Override
	public int getBuildNumber() {
		return model.getBuildNumber();
	}

	@Override
	public String getBundleSymbolicName() {
		return model.getBundleSymbolicName();
	}

	/**
	 * Returns the create date of this release.
	 *
	 * @return the create date of this release
	 */
	@Override
	public Date getCreateDate() {
		return model.getCreateDate();
	}

	/**
	 * Returns the modified date of this release.
	 *
	 * @return the modified date of this release
	 */
	@Override
	public Date getModifiedDate() {
		return model.getModifiedDate();
	}

	/**
	 * Returns the mvcc version of this release.
	 *
	 * @return the mvcc version of this release
	 */
	@Override
	public long getMvccVersion() {
		return model.getMvccVersion();
	}

	/**
	 * Returns the primary key of this release.
	 *
	 * @return the primary key of this release
	 */
	@Override
	public long getPrimaryKey() {
		return model.getPrimaryKey();
	}

	/**
	 * Returns the release ID of this release.
	 *
	 * @return the release ID of this release
	 */
	@Override
	public long getReleaseId() {
		return model.getReleaseId();
	}

	/**
	 * Returns the schema version of this release.
	 *
	 * @return the schema version of this release
	 */
	@Override
	public String getSchemaVersion() {
		return model.getSchemaVersion();
	}

	/**
	 * Returns the servlet context name of this release.
	 *
	 * @return the servlet context name of this release
	 */
	@Override
	public String getServletContextName() {
		return model.getServletContextName();
	}

	/**
	 * Returns the state of this release.
	 *
	 * @return the state of this release
	 */
	@Override
	public int getState() {
		return model.getState();
	}

	/**
	 * Returns the test string of this release.
	 *
	 * @return the test string of this release
	 */
	@Override
	public String getTestString() {
		return model.getTestString();
	}

	/**
	 * Returns the verified of this release.
	 *
	 * @return the verified of this release
	 */
	@Override
	public boolean getVerified() {
		return model.getVerified();
	}

	/**
	 * Returns the version display name of this release.
	 *
	 * @return the version display name of this release
	 */
	@Override
	public String getVersionDisplayName() {
		return model.getVersionDisplayName();
	}

	/**
	 * Returns <code>true</code> if this release is verified.
	 *
	 * @return <code>true</code> if this release is verified; <code>false</code> otherwise
	 */
	@Override
	public boolean isVerified() {
		return model.isVerified();
	}

	@Override
	public void persist() {
		model.persist();
	}

	/**
	 * Sets the build date of this release.
	 *
	 * @param buildDate the build date of this release
	 */
	@Override
	public void setBuildDate(Date buildDate) {
		model.setBuildDate(buildDate);
	}

	/**
	 * Sets the build number of this release.
	 *
	 * @param buildNumber the build number of this release
	 */
	@Override
	public void setBuildNumber(int buildNumber) {
		model.setBuildNumber(buildNumber);
	}

	/**
	 * Sets the create date of this release.
	 *
	 * @param createDate the create date of this release
	 */
	@Override
	public void setCreateDate(Date createDate) {
		model.setCreateDate(createDate);
	}

	/**
	 * Sets the modified date of this release.
	 *
	 * @param modifiedDate the modified date of this release
	 */
	@Override
	public void setModifiedDate(Date modifiedDate) {
		model.setModifiedDate(modifiedDate);
	}

	/**
	 * Sets the mvcc version of this release.
	 *
	 * @param mvccVersion the mvcc version of this release
	 */
	@Override
	public void setMvccVersion(long mvccVersion) {
		model.setMvccVersion(mvccVersion);
	}

	/**
	 * Sets the primary key of this release.
	 *
	 * @param primaryKey the primary key of this release
	 */
	@Override
	public void setPrimaryKey(long primaryKey) {
		model.setPrimaryKey(primaryKey);
	}

	/**
	 * Sets the release ID of this release.
	 *
	 * @param releaseId the release ID of this release
	 */
	@Override
	public void setReleaseId(long releaseId) {
		model.setReleaseId(releaseId);
	}

	/**
	 * Sets the schema version of this release.
	 *
	 * @param schemaVersion the schema version of this release
	 */
	@Override
	public void setSchemaVersion(String schemaVersion) {
		model.setSchemaVersion(schemaVersion);
	}

	/**
	 * Sets the servlet context name of this release.
	 *
	 * @param servletContextName the servlet context name of this release
	 */
	@Override
	public void setServletContextName(String servletContextName) {
		model.setServletContextName(servletContextName);
	}

	/**
	 * Sets the state of this release.
	 *
	 * @param state the state of this release
	 */
	@Override
	public void setState(int state) {
		model.setState(state);
	}

	/**
	 * Sets the test string of this release.
	 *
	 * @param testString the test string of this release
	 */
	@Override
	public void setTestString(String testString) {
		model.setTestString(testString);
	}

	/**
	 * Sets whether this release is verified.
	 *
	 * @param verified the verified of this release
	 */
	@Override
	public void setVerified(boolean verified) {
		model.setVerified(verified);
	}

	/**
	 * Sets the version display name of this release.
	 *
	 * @param versionDisplayName the version display name of this release
	 */
	@Override
	public void setVersionDisplayName(String versionDisplayName) {
		model.setVersionDisplayName(versionDisplayName);
	}

	@Override
	public String toXmlString() {
		return model.toXmlString();
	}

	@Override
	protected ReleaseWrapper wrap(Release release) {
		return new ReleaseWrapper(release);
	}

}