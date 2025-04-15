/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.bean.portlet.cdi.extension.internal.scope;

import com.liferay.bean.portlet.extension.ScopedBean;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

import jakarta.enterprise.context.spi.Contextual;
import jakarta.enterprise.context.spi.CreationalContext;

import java.io.Serializable;

/**
 * @author Neil Griffin
 */
public class CDIScopedBean<T> implements ScopedBean<T>, Serializable {

	public CDIScopedBean(
		Contextual<T> bean, CreationalContext<T> creationalContext, String name,
		String scopeName) {

		_bean = bean;
		_creationalContext = creationalContext;
		_name = name;
		_scopeName = scopeName;

		_containerCreatedInstance = bean.create(creationalContext);
	}

	@Override
	public void destroy() {
		if (_log.isDebugEnabled()) {
			_log.debug(
				StringBundler.concat(
					"Destroying @", _scopeName, " bean named ", _name));
		}

		_creationalContext.release();

		_bean.destroy(_containerCreatedInstance, _creationalContext);
	}

	@Override
	public T getContainerCreatedInstance() {
		return _containerCreatedInstance;
	}

	public String getScopeName() {
		return _scopeName;
	}

	private static final Log _log = LogFactoryUtil.getLog(CDIScopedBean.class);

	private static final long serialVersionUID = 2388556996969921221L;

	private final Contextual<T> _bean;
	private final T _containerCreatedInstance;
	private final CreationalContext<T> _creationalContext;
	private final String _name;
	private final String _scopeName;

}