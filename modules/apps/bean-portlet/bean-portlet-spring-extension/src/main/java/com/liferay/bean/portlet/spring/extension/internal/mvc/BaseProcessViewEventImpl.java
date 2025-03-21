/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.bean.portlet.spring.extension.internal.mvc;

import jakarta.mvc.engine.ViewEngine;

import org.springframework.context.ApplicationEvent;

/**
 * @author Neil Griffin
 */
public class BaseProcessViewEventImpl extends ApplicationEvent {

	public BaseProcessViewEventImpl(
		Object eventObject, String view,
		Class<? extends ViewEngine> viewEngine) {

		super(eventObject);

		_view = view;
		_viewEngine = viewEngine;
	}

	public Class<? extends ViewEngine> getEngine() {
		return _viewEngine;
	}

	public String getView() {
		return _view;
	}

	private final String _view;
	private final Class<? extends ViewEngine> _viewEngine;

}