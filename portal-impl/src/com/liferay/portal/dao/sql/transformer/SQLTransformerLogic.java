/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.dao.sql.transformer;

import java.util.function.Function;

import org.hibernate.cfg.Configuration;

/**
 * @author Manuel de la Peña
 * @author Brian Wing Shun Chan
 */
public interface SQLTransformerLogic {

	public Function<String, String>[] getFunctions();

	public void populateSqlFunctions(Configuration configuration);

}