/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.internal.search.spi.model.index.contributor;

import com.liferay.object.model.ObjectEntry;
import com.liferay.object.search.RootObjectEntryFolderObjectEntryModelDocumentContributorRegistry;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMap;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMapFactory;
import com.liferay.portal.search.spi.model.index.contributor.ModelDocumentContributor;

import java.util.Collections;
import java.util.List;

import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;

/**
 * @author Stefano Motta
 */
@Component(
	service = RootObjectEntryFolderObjectEntryModelDocumentContributorRegistry.class
)
public class
	RootObjectEntryFolderObjectEntryModelDocumentContributorRegistryImpl
		implements RootObjectEntryFolderObjectEntryModelDocumentContributorRegistry {

	@Override
	public List<ModelDocumentContributor<ObjectEntry>>
		getRootObjectEntryFolderObjectEntryModelDocumentContributors(
			String rootObjectEntryFolderExternalReferenceCode) {

		List<ModelDocumentContributor<ObjectEntry>> modelDocumentContributors =
			_serviceTrackerMap.getService(
				rootObjectEntryFolderExternalReferenceCode);

		if (modelDocumentContributors == null) {
			return Collections.emptyList();
		}

		return modelDocumentContributors;
	}

	@Activate
	protected void activate(BundleContext bundleContext) {
		_serviceTrackerMap = ServiceTrackerMapFactory.openMultiValueMap(
			bundleContext,
			(Class<ModelDocumentContributor<ObjectEntry>>)
				(Class<?>)ModelDocumentContributor.class,
			"root.object.entry.folder.external.reference.code");
	}

	@Deactivate
	protected void deactivate() {
		_serviceTrackerMap.close();
	}

	private ServiceTrackerMap
		<String, List<ModelDocumentContributor<ObjectEntry>>>
			_serviceTrackerMap;

}