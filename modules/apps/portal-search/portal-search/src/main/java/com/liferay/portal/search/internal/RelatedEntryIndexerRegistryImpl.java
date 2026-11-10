/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.internal;

import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMap;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMapFactory;
import com.liferay.portal.kernel.search.RelatedEntryIndexer;
import com.liferay.portal.kernel.search.RelatedEntryIndexerRegistry;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;

/**
 * @author Michael C. Han
 */
@Component(service = RelatedEntryIndexerRegistry.class)
public class RelatedEntryIndexerRegistryImpl
	implements RelatedEntryIndexerRegistry {

	@Override
	public List<RelatedEntryIndexer> getRelatedEntryIndexers() {
		List<RelatedEntryIndexer> relatedEntryIndexers = new ArrayList<>();

		Collection<List<RelatedEntryIndexer>> relatedEntryIndexersLists =
			serviceTrackerMap.values();

		for (List<RelatedEntryIndexer> relatedEntryIndexersList :
				relatedEntryIndexersLists) {

			relatedEntryIndexers.addAll(relatedEntryIndexersList);
		}

		return relatedEntryIndexers;
	}

	@Override
	public List<RelatedEntryIndexer> getRelatedEntryIndexers(Class<?> clazz) {
		return serviceTrackerMap.getService(clazz.getName());
	}

	@Override
	public List<RelatedEntryIndexer> getRelatedEntryIndexers(String className) {
		return serviceTrackerMap.getService(className);
	}

	@Activate
	protected void activate(BundleContext bundleContext) {
		serviceTrackerMap = ServiceTrackerMapFactory.openMultiValueMap(
			bundleContext, RelatedEntryIndexer.class,
			"related.entry.indexer.class.name");
	}

	@Deactivate
	protected void deactivate() {
		serviceTrackerMap.close();
	}

	protected ServiceTrackerMap<String, List<RelatedEntryIndexer>>
		serviceTrackerMap;

}