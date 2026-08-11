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
 * This class is a wrapper for {@link ClobEntry}.
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @see ClobEntry
 * @generated
 */
public class ClobEntryWrapper
	extends BaseModelWrapper<ClobEntry>
	implements ClobEntry, ModelWrapper<ClobEntry> {

	public ClobEntryWrapper(ClobEntry clobEntry) {
		super(clobEntry);
	}

	@Override
	public Map<String, Object> getModelAttributes() {
		Map<String, Object> attributes = new HashMap<String, Object>();

		attributes.put("clobEntryId", getClobEntryId());
		attributes.put("content", getContent());

		return attributes;
	}

	@Override
	public void setModelAttributes(Map<String, Object> attributes) {
		Long clobEntryId = (Long)attributes.get("clobEntryId");

		if (clobEntryId != null) {
			setClobEntryId(clobEntryId);
		}

		String content = (String)attributes.get("content");

		if (content != null) {
			setContent(content);
		}
	}

	@Override
	public ClobEntry cloneWithOriginalValues() {
		return wrap(model.cloneWithOriginalValues());
	}

	/**
	 * Returns the clob entry ID of this clob entry.
	 *
	 * @return the clob entry ID of this clob entry
	 */
	@Override
	public long getClobEntryId() {
		return model.getClobEntryId();
	}

	/**
	 * Returns the content of this clob entry.
	 *
	 * @return the content of this clob entry
	 */
	@Override
	public String getContent() {
		return model.getContent();
	}

	/**
	 * Returns the primary key of this clob entry.
	 *
	 * @return the primary key of this clob entry
	 */
	@Override
	public long getPrimaryKey() {
		return model.getPrimaryKey();
	}

	/**
	 * Sets the clob entry ID of this clob entry.
	 *
	 * @param clobEntryId the clob entry ID of this clob entry
	 */
	@Override
	public void setClobEntryId(long clobEntryId) {
		model.setClobEntryId(clobEntryId);
	}

	/**
	 * Sets the content of this clob entry.
	 *
	 * @param content the content of this clob entry
	 */
	@Override
	public void setContent(String content) {
		model.setContent(content);
	}

	/**
	 * Sets the primary key of this clob entry.
	 *
	 * @param primaryKey the primary key of this clob entry
	 */
	@Override
	public void setPrimaryKey(long primaryKey) {
		model.setPrimaryKey(primaryKey);
	}

	@Override
	public String toXmlString() {
		return model.toXmlString();
	}

	@Override
	protected ClobEntryWrapper wrap(ClobEntry clobEntry) {
		return new ClobEntryWrapper(clobEntry);
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:1959113477