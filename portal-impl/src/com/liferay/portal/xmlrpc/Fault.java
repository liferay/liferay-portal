/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.xmlrpc;

import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.xmlrpc.Response;
import com.liferay.portal.kernel.xmlrpc.XmlRpcException;

/**
 * @author Alexander Chow
 * @author Brian Wing Shun Chan
 */
public class Fault implements Response {

	public Fault(int code, String description) {
		_code = code;
		_description = description;
	}

	public int getCode() {
		return _code;
	}

	@Override
	public String getDescription() {
		return _description;
	}

	@Override
	public String toString() {
		return StringBundler.concat("XML-RPC fault ", _code, " ", _description);
	}

	@Override
	public String toXml() throws XmlRpcException {
		StringBundler sb = new StringBundler(6);

		sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?><methodResponse>");
		sb.append("<fault><value><struct><member><name>faultCode</name>");
		sb.append(XmlRpcUtil.wrapValue(_code));
		sb.append("</member><member><name>faultString</name>");
		sb.append(XmlRpcUtil.wrapValue(_description));
		sb.append("</member></struct></value></fault></methodResponse>");

		return sb.toString();
	}

	private final int _code;
	private final String _description;

}