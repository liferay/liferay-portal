/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portlet.internal;

import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.Portlet;

import jakarta.portlet.MimeResponse;
import jakarta.portlet.PortletRequest;
import jakarta.portlet.RenderURL;

import jakarta.servlet.http.HttpServletRequest;

/**
 * @author Neil Griffin
 */
public class RenderURLImpl extends PortletURLImpl implements RenderURL {

	public RenderURLImpl(
		HttpServletRequest httpServletRequest, Portlet portlet, Layout layout,
		String lifecycle, MimeResponse.Copy copy) {

		super(httpServletRequest, portlet, layout, lifecycle, copy);
	}

	public RenderURLImpl(
		PortletRequest portletRequest, Portlet portlet, Layout layout,
		String lifecycle, MimeResponse.Copy copy) {

		super(portletRequest, portlet, layout, lifecycle, copy);
	}

	@Override
	public String getFragmentIdentifier() {
		return _fragmentIdentifier;
	}

	@Override
	public void setFragmentIdentifier(String fragmentIdentifier) {
		_fragmentIdentifier = fragmentIdentifier;
	}

	@Override
	public String toString() {
		String toString = super.toString();

		if (_fragmentIdentifier != null) {
			toString = StringBundler.concat(
				toString, StringPool.POUND, _fragmentIdentifier);
		}

		return toString;
	}

	private String _fragmentIdentifier;

}