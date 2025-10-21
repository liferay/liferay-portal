/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.dao.orm.hibernate;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.model.impl.BaseModelImpl;

import org.hibernate.property.access.internal.PropertyAccessFieldImpl;
import org.hibernate.property.access.internal.PropertyAccessStrategyFieldImpl;
import org.hibernate.property.access.spi.PropertyAccess;

/**
 * @author Dante Wang
 */
@SuppressWarnings("rawtypes")
public class PrivatePropertyAccessor extends PropertyAccessStrategyFieldImpl {

	@Override
	public PropertyAccess buildPropertyAccess(
		Class containerJavaType, String propertyName, boolean setterRequired) {

		Class<?> superClass = null;

		while ((superClass = containerJavaType.getSuperclass()) !=
					BaseModelImpl.class) {

			containerJavaType = superClass;
		}

		propertyName = StringPool.UNDERLINE.concat(propertyName);

		return new PropertyAccessFieldImpl(
			this, containerJavaType, propertyName);
	}

}