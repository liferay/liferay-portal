/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.bean.portlet.cdi.extension.internal.mvc;

import jakarta.enterprise.inject.Produces;

import jakarta.inject.Named;

import jakarta.mvc.Models;
import jakarta.mvc.RedirectScoped;

/**
 * @author Neil Griffin
 */
public class ModelsProducer {

	@Named("models")
	@Produces
	@RedirectScoped
	public Models getModels() {
		return new ModelsImpl();
	}

}