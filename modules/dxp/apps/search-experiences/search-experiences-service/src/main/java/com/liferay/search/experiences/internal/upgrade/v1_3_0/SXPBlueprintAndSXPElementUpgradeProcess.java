/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.search.experiences.internal.upgrade.v1_3_0;

import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.dao.jdbc.AutoBatchPreparedStatementUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.LocalizationUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.search.experiences.model.impl.SXPElementImpl;
import com.liferay.search.experiences.rest.dto.v1_0.ElementDefinition;
import com.liferay.search.experiences.rest.dto.v1_0.ElementInstance;
import com.liferay.search.experiences.rest.dto.v1_0.SXPElement;
import com.liferay.search.experiences.rest.dto.v1_0.util.ElementInstanceUtil;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

import java.util.Arrays;
import java.util.Map;
import java.util.Objects;

/**
 * @author Wade Cao
 */
public class SXPBlueprintAndSXPElementUpgradeProcess extends UpgradeProcess {

	@Override
	protected void doUpgrade() throws Exception {
		_upgradeSXPElement();

		_upgradeSXPBlueprint();
	}

	private com.liferay.search.experiences.model.SXPElement _fetchSXPElement(
			long sxpElementId)
		throws Exception {

		try (PreparedStatement preparedStatement = connection.prepareStatement(
				"select sxpElementId, externalReferenceCode, readOnly, " +
					"version from SXPElement where sxpElementId = ?")) {

			preparedStatement.setLong(1, sxpElementId);

			try (ResultSet resultSet = preparedStatement.executeQuery()) {
				while (resultSet.next()) {
					com.liferay.search.experiences.model.SXPElement sxpElement =
						new SXPElementImpl();

					sxpElement.setExternalReferenceCode(
						resultSet.getString("externalReferenceCode"));
					sxpElement.setSXPElementId(
						resultSet.getLong("sxpElementId"));
					sxpElement.setReadOnly(resultSet.getBoolean("readOnly"));
					sxpElement.setVersion(resultSet.getString("version"));

					return sxpElement;
				}
			}
		}

		return null;
	}

	private String _getElementInstancesJSON(String elementInstancesJSON)
		throws Exception {

		ElementInstance[] elementInstances =
			ElementInstanceUtil.toElementInstances(elementInstancesJSON);

		if (ArrayUtil.isEmpty(elementInstances)) {
			return "[]";
		}

		for (ElementInstance elementInstance : elementInstances) {
			SXPElement sxpElement = elementInstance.getSxpElement();

			if (sxpElement.getId() == null) {
				continue;
			}

			com.liferay.search.experiences.model.SXPElement
				serviceBuilderSXPElement = _fetchSXPElement(sxpElement.getId());

			if (serviceBuilderSXPElement == null) {
				_log.error(
					"No search experiences element exists with ID " +
						sxpElement.getId());

				continue;
			}

			if (serviceBuilderSXPElement.isReadOnly()) {
				if (Objects.equals(
						serviceBuilderSXPElement.getExternalReferenceCode(),
						"BOOST_CONTENTS_IN_A_CATEGORY_FOR_A_PERIOD_OF_TIME") ||
					Objects.equals(
						serviceBuilderSXPElement.getExternalReferenceCode(),
						"BOOST_CONTENTS_IN_A_CATEGORY_FOR_THE_TIME_OF_DAY") ||
					Objects.equals(
						serviceBuilderSXPElement.getExternalReferenceCode(),
						"LIMIT_SEARCH_TO_CONTENTS_CREATED_WITHIN_A_PERIOD_OF_" +
							"TIME")) {

					ElementDefinition elementDefinition =
						sxpElement.getElementDefinition();

					sxpElement.setElementDefinition(
						() -> ElementDefinition.unsafeToDTO(
							_renameElementDefinitionJSON(
								String.valueOf(elementDefinition))));
				}

				Map<String, String> description_i18n =
					sxpElement.getDescription_i18n();

				description_i18n.put(
					"en-US", _renameDescription(description_i18n.get("en-US")));

				Map<String, String> title_i18n = sxpElement.getTitle_i18n();

				title_i18n.put("en-US", _renameTitle(title_i18n.get("en-US")));
			}

			sxpElement.setExternalReferenceCode(
				serviceBuilderSXPElement::getExternalReferenceCode);
			sxpElement.setVersion(serviceBuilderSXPElement::getVersion);
		}

		return Arrays.toString(elementInstances);
	}

	private String _getExternalReferenceCode(String title) throws Exception {
		String localizedTitle = LocalizationUtil.getLocalization(
			title, LocaleUtil.toLanguageId(LocaleUtil.US));

		return StringUtil.replace(
			StringUtil.toUpperCase(localizedTitle), CharPool.SPACE,
			CharPool.UNDERLINE);
	}

	private String _renameDescription(String description) {
		return StringUtil.replace(
			description,
			"Boost contents in a category for users belonging to a given " +
				"user segment",
			"Boost contents in a category for users belonging to the given " +
				"user segments");
	}

	private String _renameElementDefinitionJSON(String elementDefinitionJSON) {
		return StringUtil.replace(
			elementDefinitionJSON,
			new String[] {
				"Create Date: From", "Create Date: To", "Morning (4am - 12am)"
			},
			new String[] {"Date: From", "Date: To", "Morning (4am - 12pm)"});
	}

	private String _renameTitle(String title) {
		return StringUtil.replace(
			title,
			new String[] {
				"Boost Contents in a Category for a User Segment",
				"Search with the Lucene Syntax"
			},
			new String[] {
				"Boost Contents in a Category for User Segments",
				"Search with Query String Syntax"
			});
	}

	private void _upgradeSXPBlueprint() throws Exception {
		alterTableDropColumn("SXPBlueprint", "key_");

		try (PreparedStatement preparedStatement1 = connection.prepareStatement(
				"select sxpBlueprintId, elementInstancesJSON, version from " +
					"SXPBlueprint");
			PreparedStatement preparedStatement2 =
				AutoBatchPreparedStatementUtil.concurrentAutoBatch(
					connection,
					"update SXPBlueprint set elementInstancesJSON = ?, " +
						"version = ? where sxpBlueprintId = ?")) {

			try (ResultSet resultSet = preparedStatement1.executeQuery()) {
				while (resultSet.next()) {
					preparedStatement2.setString(
						1,
						_getElementInstancesJSON(
							resultSet.getString("elementInstancesJSON")));

					String version = resultSet.getString("version");

					if (Validator.isNull(version)) {
						version = "1.0";
					}

					preparedStatement2.setString(2, version);

					preparedStatement2.setLong(
						3, resultSet.getLong("sxpBlueprintId"));

					preparedStatement2.addBatch();
				}

				preparedStatement2.executeBatch();
			}
		}
	}

	private void _upgradeSXPElement() throws Exception {
		alterTableDropColumn("SXPElement", "key_");

		try (PreparedStatement preparedStatement1 = connection.prepareStatement(
				"select externalReferenceCode, sxpElementId, description, " +
					"elementDefinitionJSON, readOnly, title, version from " +
						"SXPElement");
			PreparedStatement preparedStatement2 =
				AutoBatchPreparedStatementUtil.concurrentAutoBatch(
					connection,
					StringBundler.concat(
						"update SXPElement set externalReferenceCode = ?, ",
						"description = ?, elementDefinitionJSON = ?, title = ",
						"?, version = ? where sxpElementId = ?"))) {

			try (ResultSet resultSet = preparedStatement1.executeQuery()) {
				while (resultSet.next()) {
					String externalReferenceCode = resultSet.getString(
						"externalReferenceCode");
					String description = resultSet.getString("description");
					String elementDefinitionJSON = resultSet.getString(
						"elementDefinitionJSON");
					String title = resultSet.getString("title");

					if (resultSet.getBoolean("readOnly")) {
						title = _renameTitle(title);

						externalReferenceCode = _getExternalReferenceCode(
							title);

						description = _renameDescription(description);
						elementDefinitionJSON = _renameElementDefinitionJSON(
							elementDefinitionJSON);
					}

					preparedStatement2.setString(1, externalReferenceCode);
					preparedStatement2.setString(2, description);
					preparedStatement2.setString(3, elementDefinitionJSON);
					preparedStatement2.setString(4, title);

					String version = resultSet.getString("version");

					if (Validator.isNull(version)) {
						version = "1.0";
					}

					preparedStatement2.setString(5, version);

					preparedStatement2.setLong(
						6, resultSet.getLong("sxpElementId"));
					preparedStatement2.addBatch();
				}

				preparedStatement2.executeBatch();
			}
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		SXPBlueprintAndSXPElementUpgradeProcess.class);

}