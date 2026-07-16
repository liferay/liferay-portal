/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.internal.expando;

import com.liferay.expando.kernel.model.ExpandoBridge;
import com.liferay.expando.kernel.model.ExpandoColumn;
import com.liferay.expando.kernel.model.ExpandoColumnConstants;
import com.liferay.expando.kernel.model.ExpandoColumnTable;
import com.liferay.expando.kernel.model.ExpandoTableConstants;
import com.liferay.expando.kernel.model.ExpandoTableTable;
import com.liferay.expando.kernel.model.ExpandoValue;
import com.liferay.expando.kernel.service.ExpandoColumnLocalService;
import com.liferay.expando.kernel.service.ExpandoTableLocalService;
import com.liferay.expando.kernel.service.ExpandoValueLocalService;
import com.liferay.petra.sql.dsl.DSLQueryFactoryUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.BaseModel;
import com.liferay.portal.kernel.model.ClassName;
import com.liferay.portal.kernel.model.ShardedModel;
import com.liferay.portal.kernel.search.Document;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.ReindexCacheThreadLocal;
import com.liferay.portal.kernel.service.ClassNameLocalService;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.search.expando.ExpandoBridgeIndexer;
import com.liferay.portal.search.expando.ExpandoBridgeUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Bryan Engler
 */
@Component(service = ExpandoBridgeIndexer.class)
public class ExpandoBridgeIndexerImpl implements ExpandoBridgeIndexer {

	@Override
	public void addAttributes(Document document, BaseModel<?> baseModel) {
		if (baseModel == null) {
			return;
		}

		try {
			doAddAttributes(document, baseModel);
		}
		catch (SystemException systemException) {
			_log.error(systemException);
		}
	}

	protected void addAttribute(
			Document document, ExpandoColumn expandoColumn,
			Map<Long, ExpandoValue> expandoValues)
		throws PortalException {

		ExpandoValue expandoValue = expandoValues.get(
			expandoColumn.getColumnId());
		boolean hasValue = true;

		if (expandoValue == null) {
			expandoValue = _expandoValueLocalService.createExpandoValue(0);

			expandoValue.setColumnId(expandoColumn.getColumnId());
			expandoValue.setData(expandoColumn.getDefaultData());

			hasValue = false;
		}

		String fieldName = ExpandoBridgeUtil.encodeFieldName(expandoColumn);
		int indexType = ExpandoBridgeUtil.getIndexType(expandoColumn);
		int type = expandoColumn.getType();

		if (type == ExpandoColumnConstants.BOOLEAN) {
			document.addKeyword(fieldName, expandoValue.getBoolean());
		}
		else if (type == ExpandoColumnConstants.BOOLEAN_ARRAY) {
			if (hasValue) {
				document.addKeyword(fieldName, expandoValue.getBooleanArray());
			}
			else {
				document.addKeyword(fieldName, new boolean[0]);
			}
		}
		else if (type == ExpandoColumnConstants.DATE) {
			document.addDate(fieldName, expandoValue.getDate());
		}
		else if (type == ExpandoColumnConstants.DOUBLE) {
			document.addNumberSortable(fieldName, expandoValue.getDouble());
		}
		else if ((type == ExpandoColumnConstants.DOUBLE_ARRAY) && hasValue) {
			Field field = new Field(
				fieldName,
				ArrayUtil.toStringArray(expandoValue.getDoubleArray()));

			field.setNumeric(true);
			field.setNumericClass(Double.class);

			document.add(field);
		}
		else if (type == ExpandoColumnConstants.FLOAT) {
			document.addNumberSortable(fieldName, expandoValue.getFloat());
		}
		else if ((type == ExpandoColumnConstants.FLOAT_ARRAY) && hasValue) {
			Field field = new Field(
				fieldName,
				ArrayUtil.toStringArray(expandoValue.getFloatArray()));

			field.setNumeric(true);
			field.setNumericClass(Float.class);

			document.add(field);
		}
		else if (type == ExpandoColumnConstants.GEOLOCATION) {
			JSONObject jsonObject = expandoValue.getGeolocationJSONObject();

			double latitude = jsonObject.getDouble("latitude");
			double longitude = jsonObject.getDouble("longitude");

			document.addGeoLocation(
				fieldName.concat("_geolocation"), latitude, longitude);
		}
		else if ((type == ExpandoColumnConstants.INTEGER) ||
				 (type == ExpandoColumnConstants.INTEGER_ARRAY)) {

			Field field = new Field(fieldName, "0");

			if (type == ExpandoColumnConstants.INTEGER) {
				field = new Field(
					fieldName, String.valueOf(expandoValue.getInteger()));
			}
			else if (hasValue) {
				field = new Field(
					fieldName,
					ArrayUtil.toStringArray(expandoValue.getIntegerArray()));
			}

			field.setNumeric(true);
			field.setNumericClass(Integer.class);

			document.add(field);
		}
		else if ((type == ExpandoColumnConstants.LONG) ||
				 (type == ExpandoColumnConstants.LONG_ARRAY)) {

			Field field = new Field(fieldName, "0");

			if (type == ExpandoColumnConstants.LONG) {
				field = new Field(
					fieldName, String.valueOf(expandoValue.getLong()));
			}
			else if (hasValue) {
				field = new Field(
					fieldName,
					ArrayUtil.toStringArray(expandoValue.getLongArray()));
			}

			field.setNumeric(true);
			field.setNumericClass(Long.class);

			document.add(field);
		}
		else if (type == ExpandoColumnConstants.NUMBER) {
			document.addKeyword(
				fieldName, String.valueOf(expandoValue.getNumber()));
		}
		else if (type == ExpandoColumnConstants.NUMBER_ARRAY) {
			if (hasValue) {
				document.addKeyword(
					fieldName,
					ArrayUtil.toStringArray(expandoValue.getNumberArray()));
			}
			else {
				document.addKeyword(fieldName, new long[0]);
			}
		}
		else if ((type == ExpandoColumnConstants.SHORT) ||
				 (type == ExpandoColumnConstants.SHORT_ARRAY)) {

			Field field = new Field(fieldName, "0");

			if (type == ExpandoColumnConstants.SHORT) {
				field = new Field(
					fieldName, String.valueOf(expandoValue.getShort()));
			}
			else if (hasValue) {
				field = new Field(
					fieldName,
					ArrayUtil.toStringArray(expandoValue.getShortArray()));
			}

			field.setNumeric(true);
			field.setNumericClass(Short.class);

			document.add(field);
		}
		else if (type == ExpandoColumnConstants.STRING) {
			if (indexType == ExpandoColumnConstants.INDEX_TYPE_KEYWORD) {
				document.addKeyword(fieldName, expandoValue.getString());
			}
			else {
				document.addText(fieldName, expandoValue.getString());
			}
		}
		else if (type == ExpandoColumnConstants.STRING_ARRAY) {
			if (hasValue) {
				if (indexType == ExpandoColumnConstants.INDEX_TYPE_KEYWORD) {
					document.addKeyword(
						fieldName, expandoValue.getStringArray());
				}
				else {
					document.addText(
						fieldName,
						StringUtil.merge(
							expandoValue.getStringArray(), StringPool.SPACE));
				}
			}
			else {
				if (indexType == ExpandoColumnConstants.INDEX_TYPE_KEYWORD) {
					document.addKeyword(fieldName, StringPool.BLANK);
				}
				else {
					document.addText(fieldName, StringPool.BLANK);
				}
			}
		}
		else if (type == ExpandoColumnConstants.STRING_LOCALIZED) {
			if (hasValue) {
				if (indexType == ExpandoColumnConstants.INDEX_TYPE_KEYWORD) {
					document.addLocalizedKeyword(
						fieldName, expandoValue.getStringMap());
				}
				else {
					document.addLocalizedText(
						fieldName, expandoValue.getStringMap());
				}
			}
		}
	}

	protected void doAddAttributes(Document document, BaseModel<?> baseModel) {
		long companyId = 0;

		if (baseModel instanceof ShardedModel shardedModel) {
			companyId = shardedModel.getCompanyId();
		}

		List<ExpandoColumn> indexedColumns = _getIndexedExpandoColumns(
			companyId, baseModel.getModelClassName());

		if (ListUtil.isEmpty(indexedColumns)) {
			return;
		}

		ExpandoBridge expandoBridge = baseModel.getExpandoBridge();

		List<ExpandoValue> expandoValues =
			_expandoValueLocalService.getRowValues(
				expandoBridge.getCompanyId(), expandoBridge.getClassName(),
				ExpandoTableConstants.DEFAULT_TABLE_NAME,
				expandoBridge.getClassPK(), QueryUtil.ALL_POS,
				QueryUtil.ALL_POS);

		Map<Long, ExpandoValue> expandoValuesMap = new HashMap<>();

		for (ExpandoValue expandoValue : expandoValues) {
			expandoValuesMap.put(expandoValue.getColumnId(), expandoValue);
		}

		for (ExpandoColumn expandoColumn : indexedColumns) {
			try {
				addAttribute(document, expandoColumn, expandoValuesMap);
			}
			catch (Exception exception) {
				_log.error("Indexing " + expandoColumn.getName(), exception);
			}
		}
	}

	private List<ExpandoColumn> _getIndexedExpandoColumns(
		long companyId, String className) {

		Map<Long, Map<String, List<ExpandoColumn>>> expandoColumnsMaps =
			ReindexCacheThreadLocal.getGlobalReindexCache(
				() -> -1, ExpandoBridgeIndexerImpl.class.getName(),
				count -> {
					List<ExpandoColumn> expandoColumns = new ArrayList<>();

					for (ExpandoColumn expandoColumn :
							_expandoColumnLocalService.
								<List<ExpandoColumn>>dslQuery(
									DSLQueryFactoryUtil.select(
										ExpandoColumnTable.INSTANCE
									).from(
										ExpandoColumnTable.INSTANCE
									).where(
										ExpandoColumnTable.INSTANCE.
											typeSettings.isNotNull()
									),
									false)) {

						int indexType = ExpandoBridgeUtil.getIndexType(
							expandoColumn);

						if (indexType !=
								ExpandoColumnConstants.INDEX_TYPE_NONE) {

							expandoColumns.add(expandoColumn);
						}
					}

					if (expandoColumns.isEmpty()) {
						return Collections.emptyMap();
					}

					Map<Long, Object[]> tableInfos = new HashMap<>();

					for (Object[] values :
							_expandoTableLocalService.<List<Object[]>>dslQuery(
								DSLQueryFactoryUtil.select(
									ExpandoTableTable.INSTANCE.tableId,
									ExpandoTableTable.INSTANCE.companyId,
									ExpandoTableTable.INSTANCE.classNameId
								).from(
									ExpandoTableTable.INSTANCE
								).where(
									ExpandoTableTable.INSTANCE.name.eq(
										ExpandoTableConstants.
											DEFAULT_TABLE_NAME)
								),
								false)) {

						tableInfos.put((Long)values[0], values);
					}

					Map<Long, Map<String, List<ExpandoColumn>>>
						localExpandoColumnsMaps = new HashMap<>();

					Map<Long, String> classNames = new HashMap<>();

					for (ExpandoColumn expandoColumn : expandoColumns) {
						Object[] tableInfo = tableInfos.get(
							expandoColumn.getTableId());

						if (tableInfo == null) {
							continue;
						}

						String localClassName = classNames.computeIfAbsent(
							(Long)tableInfo[2],
							classNameId -> {
								ClassName classNameObject =
									_classNameLocalService.fetchByClassNameId(
										classNameId);

								if (classNameObject == null) {
									return null;
								}

								return classNameObject.getClassName();
							});

						if (localClassName == null) {
							continue;
						}

						Map<String, List<ExpandoColumn>>
							localExpandoColumnsMap =
								localExpandoColumnsMaps.computeIfAbsent(
									(Long)tableInfo[1], key -> new HashMap<>());

						List<ExpandoColumn> localExpandoColumns =
							localExpandoColumnsMap.computeIfAbsent(
								localClassName, key -> new ArrayList<>());

						localExpandoColumns.add(expandoColumn);
					}

					return localExpandoColumnsMaps;
				});

		if (expandoColumnsMaps == null) {
			List<ExpandoColumn> expandoColumns =
				_expandoColumnLocalService.getDefaultTableColumns(
					companyId, className);

			if (expandoColumns.isEmpty()) {
				return expandoColumns;
			}

			List<ExpandoColumn> indexedExpandoColumns = new ArrayList<>();

			for (ExpandoColumn expandoColumn : expandoColumns) {
				int indexType = ExpandoBridgeUtil.getIndexType(expandoColumn);

				if (indexType != ExpandoColumnConstants.INDEX_TYPE_NONE) {
					indexedExpandoColumns.add(expandoColumn);
				}
			}

			return indexedExpandoColumns;
		}

		Map<String, List<ExpandoColumn>> expandoColumnsMap =
			expandoColumnsMaps.get(companyId);

		if (expandoColumnsMap == null) {
			return null;
		}

		return expandoColumnsMap.get(className);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		ExpandoBridgeIndexerImpl.class);

	@Reference
	private ClassNameLocalService _classNameLocalService;

	@Reference
	private ExpandoColumnLocalService _expandoColumnLocalService;

	@Reference
	private ExpandoTableLocalService _expandoTableLocalService;

	@Reference
	private ExpandoValueLocalService _expandoValueLocalService;

}