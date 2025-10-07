/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.exportimport.internal.data.handler;

import com.liferay.batch.engine.constants.BatchEngineImportTaskConstants;
import com.liferay.batch.engine.constants.CreateStrategy;
import com.liferay.exportimport.kernel.lar.PortletDataContext;
import com.liferay.exportimport.kernel.lar.PortletDataHandlerKeys;
import com.liferay.exportimport.kernel.lar.UserIdStrategy;
import com.liferay.exportimport.vulcan.batch.engine.ExportImportVulcanBatchEngineTaskItemDelegate;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.petra.string.StringUtil;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.FastDateFormatFactoryUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.Validator;

import java.io.Serializable;

import java.text.Format;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author Vendel Toreki
 * @author Alejandro Tardín
 * @author Petteri Karttunen
 */
public class BatchEnginePortletDataHandlerUtil {

	public static Map<String, Serializable> buildExportParameters(
		ExportImportVulcanBatchEngineTaskItemDelegate.ExportImportDescriptor
			exportImportDescriptor,
		PortletDataContext portletDataContext,
		String siteExternalReferenceCode) {

		return HashMapBuilder.<String, Serializable>put(
			"batchNestedFields",
			() -> {
				List<String> batchNestedFields = new ArrayList<>();

				batchNestedFields.add("customFields.attributeType");

				if (MapUtil.getBoolean(
						portletDataContext.getParameterMap(),
						PortletDataHandlerKeys.PERMISSIONS)) {

					batchNestedFields.add("permissions");
				}

				if (ListUtil.isNotEmpty(
						exportImportDescriptor.getNestedFields())) {

					batchNestedFields.addAll(
						exportImportDescriptor.getNestedFields());
				}

				if (batchNestedFields.isEmpty()) {
					return null;
				}

				return StringUtil.merge(batchNestedFields, StringPool.COMMA);
			}
		).put(
			"filter",
			() -> {
				if ((portletDataContext.getEndDate() == null) &&
					(portletDataContext.getStartDate() == null)) {

					return null;
				}

				StringBundler sb = new StringBundler(5);

				if (portletDataContext.getEndDate() != null) {
					sb.append("dateModified le ");
					sb.append(_format.format(portletDataContext.getEndDate()));
				}

				if (portletDataContext.getStartDate() != null) {
					if (sb.length() > 0) {
						sb.append(" and ");
					}

					sb.append("dateModified ge ");
					sb.append(
						_format.format(portletDataContext.getStartDate()));
				}

				return sb.toString();
			}
		).put(
			"itemClassName", exportImportDescriptor.getItemClassName()
		).put(
			"itemModelName", exportImportDescriptor.getItemModelName()
		).put(
			"siteExternalReferenceCode",
			() -> {
				if (Validator.isNotNull(siteExternalReferenceCode)) {
					return siteExternalReferenceCode;
				}

				return null;
			}
		).put(
			"siteId",
			() -> {
				Map<String, String[]> map =
					portletDataContext.getParameterMap();

				String[] siteIds = GetterUtil.getStringValues(
					map.get("siteId"));

				if (ArrayUtil.isNotEmpty(siteIds)) {
					return siteIds[0];
				}

				return portletDataContext.getScopeGroupId();
			}
		).putAll(
			exportImportDescriptor.getParameters(portletDataContext)
		).build();
	}

	public static Map<String, Serializable> buildImportParameters(
		ExportImportVulcanBatchEngineTaskItemDelegate.ExportImportDescriptor
			exportImportDescriptor,
		PortletDataContext portletDataContext,
		String siteExternalReferenceCode) {

		return HashMapBuilder.<String, Serializable>put(
			"batchRestrictFields",
			() -> {
				if (!MapUtil.getBoolean(
						portletDataContext.getParameterMap(),
						PortletDataHandlerKeys.PERMISSIONS)) {

					return "permissions";
				}

				return null;
			}
		).put(
			"createStrategy", CreateStrategy.UPSERT.getDBOperation()
		).put(
			"importCreatorStrategy",
			() -> {
				if (!UserIdStrategy.CURRENT_USER_ID.equals(
						MapUtil.getString(
							portletDataContext.getParameterMap(),
							PortletDataHandlerKeys.USER_ID_STRATEGY))) {

					return null;
				}

				return BatchEngineImportTaskConstants.
					IMPORT_CREATOR_STRATEGY_KEEP_CREATOR;
			}
		).put(
			"itemClassName", exportImportDescriptor.getItemClassName()
		).put(
			"itemModelName", exportImportDescriptor.getItemModelName()
		).put(
			"siteExternalReferenceCode",
			() -> {
				if (Validator.isNotNull(siteExternalReferenceCode)) {
					return siteExternalReferenceCode;
				}

				return null;
			}
		).put(
			"siteId", portletDataContext.getScopeGroupId()
		).putAll(
			exportImportDescriptor.getParameters(portletDataContext)
		).build();
	}

	private static final Format _format =
		FastDateFormatFactoryUtil.getSimpleDateFormat(
			"yyyy-MM-dd'T'HH:mm:ss'Z'");

}