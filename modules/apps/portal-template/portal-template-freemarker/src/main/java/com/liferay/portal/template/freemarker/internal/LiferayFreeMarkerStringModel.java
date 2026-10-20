/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.template.freemarker.internal;

import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.util.StringUtil;

import freemarker.ext.beans.BeansWrapper;
import freemarker.ext.beans.InvalidPropertyException;
import freemarker.ext.beans.StringModel;

import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;

import java.util.Set;

/**
 * @author Marta Medio
 */
public class LiferayFreeMarkerStringModel extends StringModel {

	public LiferayFreeMarkerStringModel(Object object, BeansWrapper wrapper) {
		super(object, wrapper);
	}

	@Override
	public TemplateModel get(String key) throws TemplateModelException {
		if (_restrictedMethodNames == null) {
			throw new InvalidPropertyException(
				StringBundler.concat(
					"Denied access to method or field ", key, " of ",
					object.getClass()));
		}

		String methodOrFieldName = StringUtil.toLowerCase(key);

		for (String restrictedMethodName : _restrictedMethodNames) {
			if (restrictedMethodName.endsWith(methodOrFieldName)) {
				throw new InvalidPropertyException(
					StringBundler.concat(
						"Denied access to method or field ", key, " of ",
						object.getClass()));
			}
		}

		return super.get(key);
	}

	@Override
	public String getAsString() {
		if ((_restrictedMethodNames == null) ||
			_restrictedMethodNames.contains("tostring")) {

			return "Denied access to the toString method in class " +
				object.getClass();
		}

		return object.toString();
	}

	public void setRestrictedMethodNames(Set<String> restrictedMethodNames) {
		_restrictedMethodNames = restrictedMethodNames;
	}

	private Set<String> _restrictedMethodNames;

}