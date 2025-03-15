/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.internal.indexer;

import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.exception.PortalException;
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

		try {
			return persistedModelLocalService.getPersistedModel(classPK);
		}
		catch (PortalException portalException) {
			if (_log.isWarnEnabled()) {
				_log.warn(
					StringBundler.concat(
						"No ", className, " found for class PK ", classPK),
					portalException);
			}

			if (className.equals("com.liferay.object.model.ObjectDefinition") && classPK == Long.getLong("ObjectDefinition.APIApplication.id")) {
				System.out.println("====== BaseModelRetrieverImpl._getPersistedModel() failed to fetch for : " + className + ", classPK: " + classPK);

				portalException.printStackTrace(System.out);
			}

			return null;
		}
	}

	private PersistedModelLocalService _getPersistedModelLocalService(
		String className) {

		PersistedModelLocalService persistedModelLocalService =
			PersistedModelLocalServiceRegistryUtil.
				getPersistedModelLocalService(className);

		if (className.equals("com.liferay.object.model.ObjectDefinition")) {
			System.out.println("==== PersistedModelLocalService lookup for " + className + " --> " + persistedModelLocalService);
		}
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