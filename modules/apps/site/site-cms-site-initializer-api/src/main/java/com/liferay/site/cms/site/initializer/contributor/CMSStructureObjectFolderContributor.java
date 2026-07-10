/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.cms.site.initializer.contributor;

import java.util.List;
import java.util.Map;

/**
 * @author Stefano Motta
 */
public interface CMSStructureObjectFolderContributor {

	public String getLabel();

	public String getObjectFolderExternalReferenceCode();

	public Map<String, List<String>> getSystemObjectFieldNames();

}