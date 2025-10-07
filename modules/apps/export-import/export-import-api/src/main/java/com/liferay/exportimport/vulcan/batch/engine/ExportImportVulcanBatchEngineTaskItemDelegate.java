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

	public interface ExportImportDescriptor {

		public String getItemClassName();

		public default String getItemModelName() {
			return getItemClassName();
		}

		public default String getLabel() {
			return null;
		}

		public default List<String> getNestedFields() {
			return null;
		}

		public default Map<String, Serializable> getParameters(
			PortletDataContext portletDataContext) {

			return null;
		}

		public String getPortletId();

		public Scope getScope();

		public default boolean isActive(PortletDataContext portletDataContext) {
			return true;
		}

	}

	public enum Scope {

		COMPANY, DEPOT, SITE

	}

}