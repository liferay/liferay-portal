/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.exportimport.vulcan.batch.engine;

import com.liferay.exportimport.kernel.lar.PortletDataContext;
import com.liferay.portal.kernel.model.BaseModel;
import com.liferay.portal.vulcan.batch.engine.VulcanBatchEngineTaskItemDelegate;
import com.liferay.staging.StagingGroupHelper;
import com.liferay.staging.StagingGroupHelperUtil;

import java.io.Serializable;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.Function;

/**
 * @author Alejandro Tardín
 */
public interface ExportImportVulcanBatchEngineTaskItemDelegate<T>
	extends VulcanBatchEngineTaskItemDelegate<T> {

	public ExportImportDescriptor<? extends BaseModel<?>>
		getExportImportDescriptor();

	public interface ExportImportDescriptor<T extends BaseModel<T>> {

		public default Function<T, Boolean> getApplicableModelFunction() {
			return null;
		}

		public default String getDescription(Locale locale) {
			return null;
		}

		public default Function<T, String>
			getJSONTypeInfoPropertyValueFunction() {

			return null;
		}

		public String getKey();

		public String getLabelLanguageKey();

		public Class<T> getModelClass();

		public default String getModelClassName() {
			Class<T> modelClass = getModelClass();

			return modelClass.getName();
		}

		public default List<String> getNestedFields() {
			return null;
		}

		public default Map<String, Serializable> getParameters(
			PortletDataContext portletDataContext) {

			return null;
		}

		public String getPortletId();

		public default int getRank() {
			return 100;
		}

		public default Map<String, String[]> getReferences() {
			return null;
		}

		public Scope getScope();

		public default String getSectionKey() {
			return null;
		}

		public default String getTag(Locale locale) {
			return null;
		}

		public default boolean isActive(PortletDataContext portletDataContext) {
			return true;
		}

		public default boolean isHidden() {
			return false;
		}

		public default boolean isMissingPortletSupported() {
			return false;
		}

		public default boolean isScopeSupported(long companyId, long groupId) {
			Scope scope = getScope();

			StagingGroupHelper stagingGroupHelper =
				StagingGroupHelperUtil.getStagingGroupHelper();

			if (stagingGroupHelper.isCompanyGroup(companyId, groupId)) {
				if (scope == Scope.COMPANY) {
					return true;
				}

				return false;
			}

			if (stagingGroupHelper.isDepotGroup(groupId)) {
				if (scope == Scope.DEPOT) {
					return true;
				}

				return false;
			}

			if (scope == Scope.SITE) {
				return true;
			}

			return false;
		}

		public default boolean isStagingSupported() {
			return false;
		}

	}

	public enum Scope {

		COMPANY, DEPOT, PORTLET, SITE

	}

}