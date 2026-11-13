/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.search;

import com.liferay.object.model.ObjectEntry;
import com.liferay.portal.search.spi.model.index.contributor.ModelDocumentContributor;

import java.util.List;

/**
 * @author Stefano Motta
 */
public interface
	RootObjectEntryFolderObjectEntryModelDocumentContributorRegistry {

	public List<ModelDocumentContributor<ObjectEntry>>
		getRootObjectEntryFolderObjectEntryModelDocumentContributors(
			String rootObjectEntryFolderExternalReferenceCode);

}