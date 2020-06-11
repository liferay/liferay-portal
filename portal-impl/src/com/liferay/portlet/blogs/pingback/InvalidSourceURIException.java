/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */

package com.liferay.portlet.blogs.pingback;

import com.liferay.portal.kernel.exception.PortalException;

/**
 * @author Marcellus Tavares
 * @deprecated As of 7.0.0, with no direct replacement
 */
@Deprecated
public class InvalidSourceURIException extends PortalException {

	public InvalidSourceURIException() {
	}

	public InvalidSourceURIException(String msg) {
		super(msg);
	}

	public InvalidSourceURIException(String msg, Throwable cause) {
		super(msg, cause);
	}

	public InvalidSourceURIException(Throwable cause) {
		super(cause);
	}

}