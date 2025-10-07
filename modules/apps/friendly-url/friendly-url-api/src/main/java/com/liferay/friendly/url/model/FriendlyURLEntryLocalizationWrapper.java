/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.friendly.url.model;

import com.liferay.portal.kernel.model.ModelWrapper;
import com.liferay.portal.kernel.model.wrapper.BaseModelWrapper;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;

/**
 * <p>
 * This class is a wrapper for {@link FriendlyURLEntryLocalization}.
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @see FriendlyURLEntryLocalization
 * @generated
 */
public class FriendlyURLEntryLocalizationWrapper
	extends BaseModelWrapper<FriendlyURLEntryLocalization>
	implements FriendlyURLEntryLocalization,
			   ModelWrapper<FriendlyURLEntryLocalization> {

	public FriendlyURLEntryLocalizationWrapper(
		FriendlyURLEntryLocalization friendlyURLEntryLocalization) {

		super(friendlyURLEntryLocalization);
	}

	@Override
	public Map<String, Object> getModelAttributes() {
		Map<String, Object> attributes = new HashMap<String, Object>();

		attributes.put("mvccVersion", getMvccVersion());
		attributes.put("ctCollectionId", getCtCollectionId());
		attributes.put(
			"friendlyURLEntryLocalizationId",
			getFriendlyURLEntryLocalizationId());
		attributes.put("companyId", getCompanyId());
		attributes.put("friendlyURLEntryId", getFriendlyURLEntryId());
		attributes.put("languageId", getLanguageId());
		attributes.put("groupId", getGroupId());
		attributes.put("classNameId", getClassNameId());
		attributes.put("classPK", getClassPK());
		attributes.put("urlTitle", getUrlTitle());

		return attributes;
	}

	@Override
	public void setModelAttributes(Map<String, Object> attributes) {
		Long mvccVersion = (Long)attributes.get("mvccVersion");

		if (mvccVersion != null) {
			setMvccVersion(mvccVersion);
		}

		Long ctCollectionId = (Long)attributes.get("ctCollectionId");

		if (ctCollectionId != null) {
			setCtCollectionId(ctCollectionId);
		}

		Long friendlyURLEntryLocalizationId = (Long)attributes.get(
			"friendlyURLEntryLocalizationId");

		if (friendlyURLEntryLocalizationId != null) {
			setFriendlyURLEntryLocalizationId(friendlyURLEntryLocalizationId);
		}

		Long companyId = (Long)attributes.get("companyId");

		if (companyId != null) {
			setCompanyId(companyId);
		}

		Long friendlyURLEntryId = (Long)attributes.get("friendlyURLEntryId");

		if (friendlyURLEntryId != null) {
			setFriendlyURLEntryId(friendlyURLEntryId);
		}

		String languageId = (String)attributes.get("languageId");

		if (languageId != null) {
			setLanguageId(languageId);
		}

		Long groupId = (Long)attributes.get("groupId");

		if (groupId != null) {
			setGroupId(groupId);
		}

		Long classNameId = (Long)attributes.get("classNameId");

		if (classNameId != null) {
			setClassNameId(classNameId);
		}

		Long classPK = (Long)attributes.get("classPK");

		if (classPK != null) {
			setClassPK(classPK);
		}

		String urlTitle = (String)attributes.get("urlTitle");

		if (urlTitle != null) {
			setUrlTitle(urlTitle);
		}
	}

	@Override
	public FriendlyURLEntryLocalization cloneWithOriginalValues() {
		return wrap(model.cloneWithOriginalValues());
	}

	/**
	 * Returns the fully qualified class name of this friendly url entry localization.
	 *
	 * @return the fully qualified class name of this friendly url entry localization
	 */
	@Override
	public String getClassName() {
		return model.getClassName();
	}

	/**
	 * Returns the class name ID of this friendly url entry localization.
	 *
	 * @return the class name ID of this friendly url entry localization
	 */
	@Override
	public long getClassNameId() {
		return model.getClassNameId();
	}

	/**
	 * Returns the class pk of this friendly url entry localization.
	 *
	 * @return the class pk of this friendly url entry localization
	 */
	@Override
	public long getClassPK() {
		return model.getClassPK();
	}

	/**
	 * Returns the company ID of this friendly url entry localization.
	 *
	 * @return the company ID of this friendly url entry localization
	 */
	@Override
	public long getCompanyId() {
		return model.getCompanyId();
	}

	/**
	 * Returns the ct collection ID of this friendly url entry localization.
	 *
	 * @return the ct collection ID of this friendly url entry localization
	 */
	@Override
	public long getCtCollectionId() {
		return model.getCtCollectionId();
	}

	/**
	 * Returns the friendly url entry ID of this friendly url entry localization.
	 *
	 * @return the friendly url entry ID of this friendly url entry localization
	 */
	@Override
	public long getFriendlyURLEntryId() {
		return model.getFriendlyURLEntryId();
	}

	/**
	 * Returns the friendly url entry localization ID of this friendly url entry localization.
	 *
	 * @return the friendly url entry localization ID of this friendly url entry localization
	 */
	@Override
	public long getFriendlyURLEntryLocalizationId() {
		return model.getFriendlyURLEntryLocalizationId();
	}

	/**
	 * Returns the group ID of this friendly url entry localization.
	 *
	 * @return the group ID of this friendly url entry localization
	 */
	@Override
	public long getGroupId() {
		return model.getGroupId();
	}

	/**
	 * Returns the language ID of this friendly url entry localization.
	 *
	 * @return the language ID of this friendly url entry localization
	 */
	@Override
	public String getLanguageId() {
		return model.getLanguageId();
	}

	/**
	 * Returns the mvcc version of this friendly url entry localization.
	 *
	 * @return the mvcc version of this friendly url entry localization
	 */
	@Override
	public long getMvccVersion() {
		return model.getMvccVersion();
	}

	/**
	 * Returns the primary key of this friendly url entry localization.
	 *
	 * @return the primary key of this friendly url entry localization
	 */
	@Override
	public long getPrimaryKey() {
		return model.getPrimaryKey();
	}

	/**
	 * Returns the url title of this friendly url entry localization.
	 *
	 * @return the url title of this friendly url entry localization
	 */
	@Override
	public String getUrlTitle() {
		return model.getUrlTitle();
	}

	@Override
	public void setClassName(String className) {
		model.setClassName(className);
	}

	/**
	 * Sets the class name ID of this friendly url entry localization.
	 *
	 * @param classNameId the class name ID of this friendly url entry localization
	 */
	@Override
	public void setClassNameId(long classNameId) {
		model.setClassNameId(classNameId);
	}

	/**
	 * Sets the class pk of this friendly url entry localization.
	 *
	 * @param classPK the class pk of this friendly url entry localization
	 */
	@Override
	public void setClassPK(long classPK) {
		model.setClassPK(classPK);
	}

	/**
	 * Sets the company ID of this friendly url entry localization.
	 *
	 * @param companyId the company ID of this friendly url entry localization
	 */
	@Override
	public void setCompanyId(long companyId) {
		model.setCompanyId(companyId);
	}

	/**
	 * Sets the ct collection ID of this friendly url entry localization.
	 *
	 * @param ctCollectionId the ct collection ID of this friendly url entry localization
	 */
	@Override
	public void setCtCollectionId(long ctCollectionId) {
		model.setCtCollectionId(ctCollectionId);
	}

	/**
	 * Sets the friendly url entry ID of this friendly url entry localization.
	 *
	 * @param friendlyURLEntryId the friendly url entry ID of this friendly url entry localization
	 */
	@Override
	public void setFriendlyURLEntryId(long friendlyURLEntryId) {
		model.setFriendlyURLEntryId(friendlyURLEntryId);
	}

	/**
	 * Sets the friendly url entry localization ID of this friendly url entry localization.
	 *
	 * @param friendlyURLEntryLocalizationId the friendly url entry localization ID of this friendly url entry localization
	 */
	@Override
	public void setFriendlyURLEntryLocalizationId(
		long friendlyURLEntryLocalizationId) {

		model.setFriendlyURLEntryLocalizationId(friendlyURLEntryLocalizationId);
	}

	/**
	 * Sets the group ID of this friendly url entry localization.
	 *
	 * @param groupId the group ID of this friendly url entry localization
	 */
	@Override
	public void setGroupId(long groupId) {
		model.setGroupId(groupId);
	}

	/**
	 * Sets the language ID of this friendly url entry localization.
	 *
	 * @param languageId the language ID of this friendly url entry localization
	 */
	@Override
	public void setLanguageId(String languageId) {
		model.setLanguageId(languageId);
	}

	/**
	 * Sets the mvcc version of this friendly url entry localization.
	 *
	 * @param mvccVersion the mvcc version of this friendly url entry localization
	 */
	@Override
	public void setMvccVersion(long mvccVersion) {
		model.setMvccVersion(mvccVersion);
	}

	/**
	 * Sets the primary key of this friendly url entry localization.
	 *
	 * @param primaryKey the primary key of this friendly url entry localization
	 */
	@Override
	public void setPrimaryKey(long primaryKey) {
		model.setPrimaryKey(primaryKey);
	}

	/**
	 * Sets the url title of this friendly url entry localization.
	 *
	 * @param urlTitle the url title of this friendly url entry localization
	 */
	@Override
	public void setUrlTitle(String urlTitle) {
		model.setUrlTitle(urlTitle);
	}

	@Override
	public String toXmlString() {
		return model.toXmlString();
	}

	@Override
	public Map<String, Function<FriendlyURLEntryLocalization, Object>>
		getAttributeGetterFunctions() {

		return model.getAttributeGetterFunctions();
	}

	@Override
	public Map<String, BiConsumer<FriendlyURLEntryLocalization, Object>>
		getAttributeSetterBiConsumers() {

		return model.getAttributeSetterBiConsumers();
	}

	@Override
	protected FriendlyURLEntryLocalizationWrapper wrap(
		FriendlyURLEntryLocalization friendlyURLEntryLocalization) {

		return new FriendlyURLEntryLocalizationWrapper(
			friendlyURLEntryLocalization);
	}

}