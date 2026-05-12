/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.preview;

import com.liferay.portal.kernel.exception.NoSuchModelException;
import com.liferay.portal.kernel.model.PersistedModel;
import com.liferay.portal.kernel.service.PersistedModelLocalService;
import com.liferay.portal.kernel.service.persistence.BasePersistence;
import com.liferay.portal.kernel.service.persistence.impl.BasePersistenceImpl;

import java.io.Serializable;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author Shuyang Zhou
 */
public class TestPersistedModelLocalService
	implements PersistedModelLocalService {

	public TestPersistedModelLocalService(
		Class<?> modelClass,
		Map<Serializable, ? extends PersistedModel> persistedModels) {

		_modelClass = modelClass;
		_persistedModels = persistedModels;
	}

	@Override
	public PersistedModel deletePersistedModel(PersistedModel persistedModel) {
		throw new UnsupportedOperationException();
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T extends PersistedModel> Map<Serializable, T> fetchPersistedModels(
		Set<Serializable> primaryKeys) {

		Map<Serializable, T> map = new HashMap<>();

		for (Serializable primaryKey : primaryKeys) {
			PersistedModel persistedModel = _persistedModels.get(primaryKey);

			if (persistedModel != null) {
				map.put(primaryKey, (T)persistedModel);
			}
		}

		return map;
	}

	@Override
	@SuppressWarnings({"rawtypes", "unchecked"})
	public BasePersistence<?> getBasePersistence() {
		return new BasePersistenceImpl() {

			@Override
			public Class getModelClass() {
				return _modelClass;
			}

		};
	}

	@Override
	public PersistedModel getPersistedModel(Serializable primaryKeyObj)
		throws NoSuchModelException {

		PersistedModel persistedModel = _persistedModels.get(primaryKeyObj);

		if (persistedModel == null) {
			throw new NoSuchModelException(String.valueOf(primaryKeyObj));
		}

		return persistedModel;
	}

	private final Class<?> _modelClass;
	private final Map<Serializable, ? extends PersistedModel> _persistedModels;

}