/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.exportimport.vulcan.batch.engine;

import com.liferay.exportimport.kernel.lar.PortletDataContext;
import com.liferay.portal.vulcan.batch.engine.VulcanBatchEngineTaskItemDelegate;

import java.io.Serializable;

import java.util.List;
import java.util.Map;

/**
 * @author Alejandro Tardín
 */
public interface ExportImportVulcanBatchEngineTaskItemDelegate<T>
	extends VulcanBatchEngineTaskItemDelegate<T> {

	public ExportImportDescriptor getExportImportDescriptor();

	// -Devcon3- Thanks to the descriptor we obtain necessary data for executing the export/import processes

	public interface ExportImportDescriptor {

		public default String getLabelLanguageKey() {
			return null;
		}

		public String getModelClassName();

		public default String getModelName() {
			return getModelClassName();
		}

		public default List<String> getNestedFields() {
			return null;
		}

		public default Map<String, Serializable> getParameters(
			PortletDataContext portletDataContext) {

			return null;
		}

		public String getPortletId();

		public String getResourceClassName();

		public Scope getScope();

		public default boolean isActive(PortletDataContext portletDataContext) {
			return true;
		}

		public default boolean isApplicableExternalReferenceCode(
			String externalReferenceCode) {

			return true;
		}

	}

	public enum Scope {

		COMPANY, DEPOT, SITE

	}

}