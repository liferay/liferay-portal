/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.asset.publisher.web.internal.exportimport.portlet.preferences.processor;

import com.liferay.asset.publisher.constants.AssetPublisherPortletKeys;
import com.liferay.exportimport.portlet.preferences.processor.ExportImportPortletPreferencesProcessor;

import org.osgi.service.component.annotations.Component;

/**
 * Provides the implementation of
 * <code>ExportImportPortletPreferencesProcessor</code> (in the
 * <code>com.liferay.exportimport.api</code> module) for the Related Assets
 * portlet. This implementation provides specific export and import capabilities
 * and routines for processing portlet preferences while exporting or importing
 * Related Assets instances.
 *
 * @author Michael Bowerman
 */
@Component(
	property = "jakarta.portlet.name=" + AssetPublisherPortletKeys.RELATED_ASSETS,
	service = ExportImportPortletPreferencesProcessor.class
)
public class RelatedAssetsExportImportPortletPreferencesProcessor
	extends AssetPublisherExportImportPortletPreferencesProcessor {
}