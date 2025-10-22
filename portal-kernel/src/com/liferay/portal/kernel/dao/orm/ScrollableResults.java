/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.dao.orm;

/**
 * @author Brian Wing Shun Chan
 */
public interface ScrollableResults {

	public boolean first() throws ORMException;

	public Object get() throws ORMException;

	public Object get(int i) throws ORMException;

	public boolean last() throws ORMException;

	public boolean next() throws ORMException;

	public boolean previous() throws ORMException;

	public boolean scroll(int i) throws ORMException;

}