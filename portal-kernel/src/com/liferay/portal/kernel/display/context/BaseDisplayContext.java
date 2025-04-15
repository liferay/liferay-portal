/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.display.context;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.UUID;

/**
 * @author Iván Zaera
 */
public abstract class BaseDisplayContext<T extends DisplayContext>
	implements DisplayContext {

	public BaseDisplayContext(
		UUID uuid, T parentDisplayContext,
		HttpServletRequest httpServletRequest,
		HttpServletResponse httpServletResponse) {

		_uuid = uuid;

		this.parentDisplayContext = parentDisplayContext;
		request = httpServletRequest;
		response = httpServletResponse;
	}

	@Override
	public UUID getUuid() {
		return _uuid;
	}

	protected final T parentDisplayContext;
	protected final HttpServletRequest request;
	protected final HttpServletResponse response;

	private final UUID _uuid;

}