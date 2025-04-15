/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.remote.jaxrs.security.internal.entity;

import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

/**
 * @author Carlos Sierra Andrés
 */
@XmlRootElement(name = "Forbidden")
public class ForbiddenEntity {

	public ForbiddenEntity() {
	}

	public ForbiddenEntity(Exception exception) {
		_exception = exception;
	}

	@XmlElement
	public String getMessage() {
		return _exception.getMessage();
	}

	private Exception _exception;

}