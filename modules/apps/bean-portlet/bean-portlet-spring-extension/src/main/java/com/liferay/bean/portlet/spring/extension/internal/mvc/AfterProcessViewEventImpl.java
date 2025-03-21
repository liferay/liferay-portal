/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.bean.portlet.spring.extension.internal.mvc;

import jakarta.mvc.engine.ViewEngine;
import jakarta.mvc.event.AfterProcessViewEvent;

/**
 * @author Neil Griffin
 */
public class AfterProcessViewEventImpl
	extends BaseProcessViewEventImpl implements AfterProcessViewEvent {

	public AfterProcessViewEventImpl(
		Object eventObject, String view,
		Class<? extends ViewEngine> viewEngine) {

		super(eventObject, view, viewEngine);
	}

}