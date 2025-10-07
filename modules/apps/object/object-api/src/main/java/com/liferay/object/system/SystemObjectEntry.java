/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.system;

import com.liferay.expando.kernel.model.ExpandoBridge;
import com.liferay.portal.kernel.model.ExternalReferenceCodeModel;
import com.liferay.portal.kernel.model.GroupedModel;
import com.liferay.portal.kernel.util.GetterUtil;

import java.io.Serializable;

import java.util.Date;
import java.util.Map;

/**
 * @author Carolina Barbosa
 */
public class SystemObjectEntry
	implements ExternalReferenceCodeModel, GroupedModel {

	public SystemObjectEntry(
		long classPK, String externalReferenceCode,
		Map<String, Object> values) {

		_classPK = classPK;
		_externalReferenceCode = externalReferenceCode;
		_values = values;
	}

	public long getClassPK() {
		return _classPK;
	}

	@Override
	public long getCompanyId() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Date getCreateDate() {
		throw new UnsupportedOperationException();
	}

	@Override
	public ExpandoBridge getExpandoBridge() {
		throw new UnsupportedOperationException();
	}

	@Override
	public String getExternalReferenceCode() {
		return _externalReferenceCode;
	}

	@Override
	public long getGroupId() {
		return GetterUtil.getLong(_values.get("groupId"));
	}

	@Override
	public Class<?> getModelClass() {
		throw new UnsupportedOperationException();
	}

	@Override
	public String getModelClassName() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Date getModifiedDate() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Serializable getPrimaryKeyObj() {
		throw new UnsupportedOperationException();
	}

	@Override
	public long getUserId() {
		throw new UnsupportedOperationException();
	}

	@Override
	public String getUserName() {
		throw new UnsupportedOperationException();
	}

	@Override
	public String getUserUuid() {
		throw new UnsupportedOperationException();
	}

	public Map<String, Object> getValues() {
		return _values;
	}

	@Override
	public void setCompanyId(long companyId) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void setCreateDate(Date date) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void setExternalReferenceCode(String externalReferenceCode) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void setGroupId(long groupId) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void setModifiedDate(Date date) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void setPrimaryKeyObj(Serializable primaryKeyObj) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void setUserId(long userId) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void setUserName(String userName) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void setUserUuid(String userUuid) {
		throw new UnsupportedOperationException();
	}

	private final long _classPK;
	private final String _externalReferenceCode;
	private final Map<String, Object> _values;

}