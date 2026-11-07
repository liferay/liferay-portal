/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.bulk.rest.internal.resource.v1_0;

import com.liferay.bulk.rest.dto.v1_0.DocumentBulkSelection;
import com.liferay.bulk.rest.dto.v1_0.Selection;
import com.liferay.bulk.rest.internal.util.DocumentBulkSelectionUtil;
import com.liferay.bulk.rest.resource.v1_0.SelectionResource;
import com.liferay.bulk.selection.BulkSelection;
import com.liferay.bulk.selection.BulkSelectionFactoryRegistry;
import com.liferay.portal.kernel.change.tracking.CTAware;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ServiceScope;

/**
 * @author Alejandro Tardín
 */
@Component(
	properties = "OSGI-INF/liferay/rest/v1_0/selection.properties",
	scope = ServiceScope.PROTOTYPE, service = SelectionResource.class
)
@CTAware
public class SelectionResourceImpl extends BaseSelectionResourceImpl {

	@Override
	public Selection postBulkSelection(
			DocumentBulkSelection documentBulkSelection)
		throws Exception {

		BulkSelection<?> bulkSelection = DocumentBulkSelectionUtil.create(
			_bulkSelectionFactoryRegistry, documentBulkSelection);

		return new Selection() {
			{
				setSize(bulkSelection::getSize);
			}
		};
	}

	@Reference
	private BulkSelectionFactoryRegistry _bulkSelectionFactoryRegistry;

}