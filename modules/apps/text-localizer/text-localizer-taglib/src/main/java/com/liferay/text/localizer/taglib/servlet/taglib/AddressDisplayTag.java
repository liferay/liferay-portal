/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.text.localizer.taglib.servlet.taglib;

import com.liferay.petra.string.CharPool;
import com.liferay.portal.kernel.model.Address;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.taglib.util.ParamAndPropertyAncestorTagImpl;
import com.liferay.text.localizer.taglib.internal.address.util.AddressTextLocalizerUtil;

import jakarta.servlet.jsp.JspException;
import jakarta.servlet.jsp.JspWriter;
import jakarta.servlet.jsp.tagext.BodyTag;

import java.io.IOException;

/**
 * @author Pei-Jung Lan
 */
public class AddressDisplayTag extends ParamAndPropertyAncestorTagImpl {

	@Override
	public int doEndTag() throws JspException {
		JspWriter writer = pageContext.getOut();

		try {
			writer.write(
				StringUtil.replace(
					_getFormattedAddress(), CharPool.NEW_LINE, "<br />"));
		}
		catch (IOException ioException) {
			throw new JspException(ioException);
		}

		return BodyTag.EVAL_BODY_BUFFERED;
	}

	public void setAddress(Address address) {
		_address = address;
	}

	private String _getFormattedAddress() {
		return AddressTextLocalizerUtil.format(_address);
	}

	private Address _address;

}