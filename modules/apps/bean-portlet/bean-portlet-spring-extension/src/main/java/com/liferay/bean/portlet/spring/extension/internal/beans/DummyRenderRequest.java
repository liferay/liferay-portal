/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.bean.portlet.spring.extension.internal.beans;

import jakarta.portlet.RenderRequest;

/**
 * @author Neil Griffin
 */
public class DummyRenderRequest
	extends DummyPortletRequest implements RenderRequest {

	public static final RenderRequest INSTANCE = new DummyRenderRequest();

	@Override
	public String getETag() {
		throw new UnsupportedOperationException();
	}

}