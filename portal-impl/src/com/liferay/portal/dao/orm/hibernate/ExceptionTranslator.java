/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.dao.orm.hibernate;

import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.dao.orm.ORMException;
import com.liferay.portal.kernel.dao.orm.ObjectNotFoundException;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONSerializer;
import com.liferay.portal.kernel.model.BaseModel;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;

import org.hibernate.Session;
import org.hibernate.StaleObjectStateException;

/**
 * @author Brian Wing Shun Chan
 */
public class ExceptionTranslator {

	public static ORMException translate(Exception exception) {
		if (exception instanceof org.hibernate.ObjectNotFoundException) {
			return new ObjectNotFoundException(exception);
		}

		return new ORMException(exception);
	}

	public static ORMException translate(
		Exception exception, Session session, Object object) {

		if (exception instanceof StaleObjectStateException) {
			BaseModel<?> baseModel = (BaseModel<?>)object;

			Object currentObject = session.getReference(
				object.getClass(), baseModel.getPrimaryKeyObj());

			BaseModel<?> currentObjectBaseModel = (BaseModel<?>)currentObject;

			currentObject = currentObjectBaseModel.clone();

			object = baseModel.clone();

			JSONSerializer jsonSerializer =
				JSONFactoryUtil.createJSONSerializer();

			PermissionChecker permissionChecker =
				PermissionThreadLocal.getPermissionChecker();

			try {
				PermissionThreadLocal.setPermissionChecker(null);

				return new ORMException(
					StringBundler.concat(
						jsonSerializer.serialize(object),
						" is stale in comparison to ",
						jsonSerializer.serialize(currentObject)),
					exception);
			}
			finally {
				PermissionThreadLocal.setPermissionChecker(permissionChecker);
			}
		}

		return new ORMException(exception);
	}

}