/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.model;

import com.liferay.portal.kernel.model.ModelWrapper;
import com.liferay.portal.kernel.model.wrapper.BaseModelWrapper;

import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 * This class is a wrapper for {@link SiteSitemapRegenerationEntry}.
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @see SiteSitemapRegenerationEntry
 * @generated
 */
public class SiteSitemapRegenerationEntryWrapper
	extends BaseModelWrapper<SiteSitemapRegenerationEntry>
	implements ModelWrapper<SiteSitemapRegenerationEntry>,
			   SiteSitemapRegenerationEntry {

	public SiteSitemapRegenerationEntryWrapper(
		SiteSitemapRegenerationEntry siteSitemapRegenerationEntry) {

		super(siteSitemapRegenerationEntry);
	}

	@Override
	public Map<String, Object> getModelAttributes() {
		Map<String, Object> attributes = new HashMap<String, Object>();

		attributes.put("mvccVersion", getMvccVersion());
		attributes.put(
			"siteSitemapRegenerationEntryId",
			getSiteSitemapRegenerationEntryId());
		attributes.put("groupId", getGroupId());
		attributes.put("companyId", getCompanyId());
		attributes.put("assetTypeKey", getAssetTypeKey());

		return attributes;
	}

	@Override
	public void setModelAttributes(Map<String, Object> attributes) {
		Long mvccVersion = (Long)attributes.get("mvccVersion");

		if (mvccVersion != null) {
			setMvccVersion(mvccVersion);
		}

		Long siteSitemapRegenerationEntryId = (Long)attributes.get(
			"siteSitemapRegenerationEntryId");

		if (siteSitemapRegenerationEntryId != null) {
			setSiteSitemapRegenerationEntryId(siteSitemapRegenerationEntryId);
		}

		Long groupId = (Long)attributes.get("groupId");

		if (groupId != null) {
			setGroupId(groupId);
		}

		Long companyId = (Long)attributes.get("companyId");

		if (companyId != null) {
			setCompanyId(companyId);
		}

		String assetTypeKey = (String)attributes.get("assetTypeKey");

		if (assetTypeKey != null) {
			setAssetTypeKey(assetTypeKey);
		}
	}

	@Override
	public SiteSitemapRegenerationEntry cloneWithOriginalValues() {
		return wrap(model.cloneWithOriginalValues());
	}

	/**
	 * Returns the asset type key of this site sitemap regeneration entry.
	 *
	 * @return the asset type key of this site sitemap regeneration entry
	 */
	@Override
	public String getAssetTypeKey() {
		return model.getAssetTypeKey();
	}

	/**
	 * Returns the company ID of this site sitemap regeneration entry.
	 *
	 * @return the company ID of this site sitemap regeneration entry
	 */
	@Override
	public long getCompanyId() {
		return model.getCompanyId();
	}

	/**
	 * Returns the group ID of this site sitemap regeneration entry.
	 *
	 * @return the group ID of this site sitemap regeneration entry
	 */
	@Override
	public long getGroupId() {
		return model.getGroupId();
	}

	/**
	 * Returns the mvcc version of this site sitemap regeneration entry.
	 *
	 * @return the mvcc version of this site sitemap regeneration entry
	 */
	@Override
	public long getMvccVersion() {
		return model.getMvccVersion();
	}

	/**
	 * Returns the primary key of this site sitemap regeneration entry.
	 *
	 * @return the primary key of this site sitemap regeneration entry
	 */
	@Override
	public long getPrimaryKey() {
		return model.getPrimaryKey();
	}

	/**
	 * Returns the site sitemap regeneration entry ID of this site sitemap regeneration entry.
	 *
	 * @return the site sitemap regeneration entry ID of this site sitemap regeneration entry
	 */
	@Override
	public long getSiteSitemapRegenerationEntryId() {
		return model.getSiteSitemapRegenerationEntryId();
	}

	@Override
	public void persist() {
		model.persist();
	}

	/**
	 * Sets the asset type key of this site sitemap regeneration entry.
	 *
	 * @param assetTypeKey the asset type key of this site sitemap regeneration entry
	 */
	@Override
	public void setAssetTypeKey(String assetTypeKey) {
		model.setAssetTypeKey(assetTypeKey);
	}

	/**
	 * Sets the company ID of this site sitemap regeneration entry.
	 *
	 * @param companyId the company ID of this site sitemap regeneration entry
	 */
	@Override
	public void setCompanyId(long companyId) {
		model.setCompanyId(companyId);
	}

	/**
	 * Sets the group ID of this site sitemap regeneration entry.
	 *
	 * @param groupId the group ID of this site sitemap regeneration entry
	 */
	@Override
	public void setGroupId(long groupId) {
		model.setGroupId(groupId);
	}

	/**
	 * Sets the mvcc version of this site sitemap regeneration entry.
	 *
	 * @param mvccVersion the mvcc version of this site sitemap regeneration entry
	 */
	@Override
	public void setMvccVersion(long mvccVersion) {
		model.setMvccVersion(mvccVersion);
	}

	/**
	 * Sets the primary key of this site sitemap regeneration entry.
	 *
	 * @param primaryKey the primary key of this site sitemap regeneration entry
	 */
	@Override
	public void setPrimaryKey(long primaryKey) {
		model.setPrimaryKey(primaryKey);
	}

	/**
	 * Sets the site sitemap regeneration entry ID of this site sitemap regeneration entry.
	 *
	 * @param siteSitemapRegenerationEntryId the site sitemap regeneration entry ID of this site sitemap regeneration entry
	 */
	@Override
	public void setSiteSitemapRegenerationEntryId(
		long siteSitemapRegenerationEntryId) {

		model.setSiteSitemapRegenerationEntryId(siteSitemapRegenerationEntryId);
	}

	@Override
	public String toXmlString() {
		return model.toXmlString();
	}

	@Override
	protected SiteSitemapRegenerationEntryWrapper wrap(
		SiteSitemapRegenerationEntry siteSitemapRegenerationEntry) {

		return new SiteSitemapRegenerationEntryWrapper(
			siteSitemapRegenerationEntry);
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:430351872