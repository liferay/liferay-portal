/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.page.template.internal.upgrade.v3_3_1;

import com.liferay.layout.validator.LayoutValidator;
import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.jdbc.AutoBatchPreparedStatementUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.LayoutPrototype;
import com.liferay.portal.kernel.model.ModelHintsUtil;
import com.liferay.portal.kernel.service.LayoutPrototypeLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.StringUtil;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

/**
 * @author Mariano Álvaro Sáiz
 */
public class LayoutPageTemplateEntryUpgradeProcess extends UpgradeProcess {

	public LayoutPageTemplateEntryUpgradeProcess(
		LayoutPrototypeLocalService layoutPrototypeLocalService) {

		_layoutPrototypeLocalService = layoutPrototypeLocalService;
	}

	@Override
	protected void doUpgrade() throws Exception {
		_upgradeLayoutPageTemplateEntryNameAndKey();
	}

	private String _generateValidString(String value) {
		StringBundler sb = new StringBundler(value.length());

		for (int i = 0; i < value.length(); i++) {
			char c = value.charAt(i);

			if (LayoutValidator.isBlacklistedChar(c)) {
				sb.append(CharPool.DASH);
			}
			else {
				sb.append(c);
			}
		}

		return sb.toString();
	}

	private String _getUniqueColumnValue(String value, String column) {
		Set<String> uniqueValues = _columnUniqueValues.get(column);

		int maxLength = ModelHintsUtil.getMaxLength(
			LayoutPageTemplateEntryUpgradeProcess.class.getName(), column);

		String currentValue = value;

		for (int i = 1;; i++) {
			if (!uniqueValues.contains(currentValue)) {
				break;
			}

			String suffix = StringPool.DASH + i;

			String prefix = value.substring(
				0, Math.min(maxLength - suffix.length(), value.length()));

			currentValue = prefix + suffix;
		}

		uniqueValues.remove(value);
		uniqueValues.add(currentValue);

		return currentValue;
	}

	private void _loadDistinctKeysAndNames() throws Exception {
		Set<String> names = _columnUniqueValues.get("name");
		Set<String> layoutPageTemplateEntryKeys = _columnUniqueValues.get(
			"layoutPageTemplateEntryKey");

		try (Statement s = connection.createStatement();
			ResultSet resultSet = s.executeQuery(
				"select distinct layoutPageTemplateEntryKey, name from " +
					"LayoutPageTemplateEntry")) {

			while (resultSet.next()) {
				names.add(resultSet.getString("name"));
				layoutPageTemplateEntryKeys.add(
					resultSet.getString("layoutPageTemplateEntryKey"));
			}
		}
	}

	private void _updateLayoutPrototypeName(
			long layoutPrototypeId, String oldName, String newName)
		throws PortalException {

		LayoutPrototype layoutPrototype =
			_layoutPrototypeLocalService.fetchLayoutPrototype(
				layoutPrototypeId);

		if (layoutPrototype == null) {
			return;
		}

		Map<Locale, String> nameMap = layoutPrototype.getNameMap();

		Locale locale = LocaleUtil.fromLanguageId(
			layoutPrototype.getDefaultLanguageId());

		String defaultName = nameMap.get(locale);

		if (!StringUtil.equals(defaultName, oldName)) {
			return;
		}

		nameMap.put(
			locale, StringUtil.replaceFirst(defaultName, oldName, newName));

		_layoutPrototypeLocalService.updateLayoutPrototype(
			layoutPrototypeId, nameMap, layoutPrototype.getDescriptionMap(),
			layoutPrototype.isActive(), new ServiceContext());
	}

	private void _upgradeLayoutPageTemplateEntryNameAndKey() throws Exception {
		_loadDistinctKeysAndNames();

		try (Statement s = connection.createStatement();
			ResultSet resultSet = s.executeQuery(
				"select layoutPageTemplateEntryId, " +
					"layoutPageTemplateEntryKey, layoutPrototypeId, name " +
						"from LayoutPageTemplateEntry");
			PreparedStatement preparedStatement =
				AutoBatchPreparedStatementUtil.autoBatch(
					connection,
					"update LayoutPageTemplateEntry set " +
						"layoutPageTemplateEntryKey = ?, name = ? where " +
							"layoutPageTemplateEntryId = ?")) {

			while (resultSet.next()) {
				String name = resultSet.getString("name");

				if (!LayoutValidator.hasBlacklistedChar(name)) {
					continue;
				}

				long layoutPageTemplateEntryId = resultSet.getLong(
					"layoutPageTemplateEntryId");

				String layoutPageTemplateEntryKey = _generateValidString(
					resultSet.getString("layoutPageTemplateEntryKey"));

				preparedStatement.setString(
					1,
					_getUniqueColumnValue(
						layoutPageTemplateEntryKey,
						"layoutPageTemplateEntryKey"));

				String newName = _generateValidString(name);

				preparedStatement.setString(
					2, _getUniqueColumnValue(newName, "name"));

				preparedStatement.setLong(3, layoutPageTemplateEntryId);

				long layoutPrototypeId = resultSet.getLong("layoutPrototypeId");

				_updateLayoutPrototypeName(layoutPrototypeId, name, newName);

				preparedStatement.addBatch();
			}

			preparedStatement.executeBatch();
		}
	}

	private final Map<String, Set<String>> _columnUniqueValues =
		HashMapBuilder.<String, Set<String>>put(
			"layoutPageTemplateEntryKey", new HashSet<String>()
		).put(
			"name", new HashSet<String>()
		).build();
	private final LayoutPrototypeLocalService _layoutPrototypeLocalService;

}