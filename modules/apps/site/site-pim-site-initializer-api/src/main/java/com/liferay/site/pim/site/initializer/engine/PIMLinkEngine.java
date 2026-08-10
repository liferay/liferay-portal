/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.pim.site.initializer.engine;

import com.liferay.object.model.ObjectEntry;
import com.liferay.portal.kernel.exception.PortalException;

import java.util.List;

import org.osgi.annotation.versioning.ProviderType;

/**
 * @author Stefano Motta
 */
@ProviderType
public interface PIMLinkEngine {

	public void addLinks(
			ObjectEntry sourceObjectEntry,
			List<ObjectEntry> targetObjectEntries, String type)
		throws PortalException;

	public void deleteLink(ObjectEntry objectEntry, String type)
		throws PortalException;

}