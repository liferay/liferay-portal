/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.utility.page.kernel;

import com.liferay.layout.utility.page.kernel.request.contributor.StatusLayoutUtilityPageEntryRequestContributor;
import com.liferay.osgi.service.tracker.collections.map.PropertyServiceReferenceComparator;
import com.liferay.osgi.service.tracker.collections.map.PropertyServiceReferenceMapper;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMap;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMapFactory;
import com.liferay.portal.kernel.module.util.SystemBundleUtil;
import com.liferay.portal.kernel.util.Validator;

/**
 * @author Jürgen Kappler
 */
public class StatusLayoutUtilityPageEntryRequestContributorRegistryUtil {

	public static StatusLayoutUtilityPageEntryRequestContributor
		getStatusLayoutUtilityPageEntryRequestContributor(int statusCode) {

		String layoutUtilityPageEntryType =
			LayoutUtilityPageEntryTypeUtil.getStatusLayoutUtilityPageEntryType(
				statusCode);

		if (Validator.isNull(layoutUtilityPageEntryType)) {
			return null;
		}

		return _layoutUtilityPageEntryViewRenderersServiceTrackerMap.getService(
			layoutUtilityPageEntryType);
	}

	private static final ServiceTrackerMap
		<String, StatusLayoutUtilityPageEntryRequestContributor>
			_layoutUtilityPageEntryViewRenderersServiceTrackerMap =
				ServiceTrackerMapFactory.openSingleValueMap(
					SystemBundleUtil.getBundleContext(),
					(Class<StatusLayoutUtilityPageEntryRequestContributor>)
						(Class)
							StatusLayoutUtilityPageEntryRequestContributor.
								class,
					null,
					new PropertyServiceReferenceMapper<>("utility.page.type"),
					new PropertyServiceReferenceComparator<>(
						"service.ranking"));

}