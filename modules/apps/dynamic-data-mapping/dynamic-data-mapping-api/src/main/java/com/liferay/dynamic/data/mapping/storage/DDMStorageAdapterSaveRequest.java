/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dynamic.data.mapping.storage;

import com.liferay.dynamic.data.mapping.model.DDMFormInstance;

/**
 * @author Leonardo Barros
 */
public final class DDMStorageAdapterSaveRequest {

	public String getClassName() {
		return _className;
	}

	public DDMFormInstance getDDMFormInstance() {
		return _ddmFormInstance;
	}

	public DDMFormValues getDDMFormValues() {
		return _ddmFormValues;
	}

	public long getGroupId() {
		return _groupId;
	}

	public long getPrimaryKey() {
		return _primaryKey;
	}

	public long getStructureId() {
		return _structureId;
	}

	public long getUserId() {
		return _userId;
	}

	public String getUuid() {
		return _uuid;
	}

	public boolean isInsert() {
		if (_primaryKey == 0) {
			return true;
		}

		return false;
	}

	public static class Builder {

		public static Builder newBuilder(
			long userId, DDMFormValues ddmFormValues) {

			return new Builder(userId, ddmFormValues);
		}

		public DDMStorageAdapterSaveRequest build() {
			return _ddmStorageAdapterSaveRequest;
		}

		public Builder withClassName(String className) {
			_ddmStorageAdapterSaveRequest._className = className;

			return this;
		}

		public Builder withDDMFormInstance(DDMFormInstance ddmFormInstance) {
			_ddmStorageAdapterSaveRequest._ddmFormInstance = ddmFormInstance;

			return this;
		}

		public Builder withGroupId(long groupId) {
			_ddmStorageAdapterSaveRequest._groupId = groupId;

			return this;
		}

		public Builder withPrimaryKey(long primaryKey) {
			_ddmStorageAdapterSaveRequest._primaryKey = primaryKey;

			return this;
		}

		public Builder withStructureId(long structureId) {
			_ddmStorageAdapterSaveRequest._structureId = structureId;

			return this;
		}

		public Builder withUuid(String uuid) {
			_ddmStorageAdapterSaveRequest._uuid = uuid;

			return this;
		}

		private Builder(long userId, DDMFormValues ddmFormValues) {
			_ddmStorageAdapterSaveRequest._userId = userId;
			_ddmStorageAdapterSaveRequest._ddmFormValues = ddmFormValues;
		}

		private final DDMStorageAdapterSaveRequest
			_ddmStorageAdapterSaveRequest = new DDMStorageAdapterSaveRequest();

	}

	private DDMStorageAdapterSaveRequest() {
	}

	private String _className;
	private DDMFormInstance _ddmFormInstance;
	private DDMFormValues _ddmFormValues;
	private long _groupId;
	private long _primaryKey;
	private long _structureId;
	private long _userId;
	private String _uuid;

}