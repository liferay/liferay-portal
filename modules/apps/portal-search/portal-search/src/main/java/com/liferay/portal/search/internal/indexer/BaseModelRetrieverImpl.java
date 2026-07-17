/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.internal.indexer;

import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.BaseModel;
import com.liferay.portal.kernel.model.PersistedModel;
import com.liferay.portal.kernel.service.PersistedModelLocalService;
import com.liferay.portal.search.indexer.BaseModelRetriever;
import com.liferay.portal.service.PersistedModelLocalServiceRegistryUtil;

import org.osgi.service.component.annotations.Component;

/**
 * @author Michael C. Han
 */
@Component(service = BaseModelRetriever.class)
public class BaseModelRetrieverImpl implements BaseModelRetriever {

	@Override
	public BaseModel<?> fetchBaseModel(String className, long classPK) {
		PersistedModel persistModel = _getPersistedModel(className, classPK);

		if (persistModel == null) {
			return null;
		}

		if (!(persistModel instanceof BaseModel)) {
			if (_log.isWarnEnabled()) {
				_log.warn(persistModel + " is not a base model");
			}

			return null;
		}

		return (BaseModel<?>)persistModel;
	}

	private PersistedModel _getPersistedModel(String className, long classPK) {
		PersistedModelLocalService persistedModelLocalService =
			_getPersistedModelLocalService(className);

		return persistedModelLocalService.fetchPersistedModel(classPK);
	}

	private PersistedModelLocalService _getPersistedModelLocalService(
		String className) {

		PersistedModelLocalService persistedModelLocalService =
			PersistedModelLocalServiceRegistryUtil.
				getPersistedModelLocalService(className);

		if (persistedModelLocalService == null) {
			throw new SystemException(
				"No persisted model local service found for class " +
					className);
		}

		return persistedModelLocalService;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		BaseModelRetrieverImpl.class);

}