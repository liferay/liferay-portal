/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.page.template.exception;

import com.liferay.layout.page.template.constants.LayoutPageTemplateCollectionTypeConstants;
import com.liferay.portal.kernel.exception.PortalException;

/**
 * @author Brian Wing Shun Chan
 */
public class DuplicateLayoutPageTemplateCollectionException
	extends PortalException {

	public DuplicateLayoutPageTemplateCollectionException() {
	}

	public DuplicateLayoutPageTemplateCollectionException(String msg) {
		super(msg);
	}

	public DuplicateLayoutPageTemplateCollectionException(
		String name, int type) {

		super("Duplicate layout page template collection with name " + name);

		_name = name;
		_type = type;
	}

	public DuplicateLayoutPageTemplateCollectionException(
		String msg, Throwable throwable) {

		super(msg, throwable);
	}

	public DuplicateLayoutPageTemplateCollectionException(Throwable throwable) {
		super(throwable);
	}

	public String getName() {
		return _name;
	}

	public int getType() {
		return _type;
	}

	private String _name;
	private int _type = LayoutPageTemplateCollectionTypeConstants.BASIC;

}