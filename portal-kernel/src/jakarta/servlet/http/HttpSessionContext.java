/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package jakarta.servlet.http;

import java.util.Enumeration;

/**
 * @author Shuyang Zhou
 */
public interface HttpSessionContext {

	public Enumeration<String> getIds();

	public HttpSession getSession(String sessionId);

}