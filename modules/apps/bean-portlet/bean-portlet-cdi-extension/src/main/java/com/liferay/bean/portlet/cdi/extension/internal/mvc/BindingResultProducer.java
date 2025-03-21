/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.bean.portlet.cdi.extension.internal.mvc;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;

import jakarta.mvc.RedirectScoped;
import jakarta.mvc.binding.BindingResult;

/**
 * @author Neil Griffin
 */
@ApplicationScoped
public class BindingResultProducer {

	@Produces
	@RedirectScoped
	public BindingResult getBindingResult() {
		return new BindingResultImpl();
	}

}