/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.preview;

import com.liferay.portal.kernel.model.PersistedModel;
import com.liferay.portal.kernel.model.impl.BaseModelImpl;

import java.io.Serializable;

/**
 * @author Shuyang Zhou
 */
public abstract class BaseTestModel<T extends BaseTestModel<T>>
	extends BaseModelImpl<T> implements PersistedModel {

	@Override
	public Object clone() {
		throw new UnsupportedOperationException();
	}

	@Override
	public T cloneWithOriginalValues() {
		throw new UnsupportedOperationException();
	}

	@Override
	public int compareTo(T t) {
		throw new UnsupportedOperationException();
	}

	@Override
	public String getModelClassName() {
		Class<?> modelClass = getModelClass();

		return modelClass.getName();
	}

	@Override
	public Serializable getPrimaryKeyObj() {
		return primaryKey;
	}

	@Override
	public boolean isEntityCacheEnabled() {
		return false;
	}

	@Override
	public boolean isFinderCacheEnabled() {
		return false;
	}

	@Override
	public void persist() {
		throw new UnsupportedOperationException();
	}

	@Override
	public void setPrimaryKeyObj(Serializable primaryKeyObj) {
		primaryKey = primaryKeyObj;
	}

	protected BaseTestModel(Serializable primaryKey) {
		this.primaryKey = primaryKey;
	}

	protected Serializable primaryKey;

}