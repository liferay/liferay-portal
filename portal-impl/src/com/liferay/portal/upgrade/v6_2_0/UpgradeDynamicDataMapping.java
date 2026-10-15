/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.upgrade.v6_2_0;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.metadata.RawMetadataProcessor;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.LoggingTimer;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.xml.Document;
import com.liferay.portal.kernel.xml.Element;
import com.liferay.portal.kernel.xml.SAXReaderUtil;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.List;

/**
 * @author Juan Fernández
 * @author Marcellus Tavares
 */
public class UpgradeDynamicDataMapping extends UpgradeProcess {

	@Override
	protected void doUpgrade() throws Exception {
		updateSchema();

		updateStructures();
		updateStructuresClassNameId();

		updateTemplates();
	}

	protected void updateMetadataElement(
		Element metadataElement, String[] relocatedMetadadaEntryNames,
		String[] removedMetadataEntryNames) {

		Element parentElement = metadataElement.getParent();

		List<Element> entryElements = metadataElement.elements("entry");

		for (Element entryElement : entryElements) {
			String name = entryElement.attributeValue("name");

			if (ArrayUtil.contains(removedMetadataEntryNames, name)) {
				metadataElement.remove(entryElement);
			}
			else if (ArrayUtil.contains(relocatedMetadadaEntryNames, name)) {
				parentElement.addAttribute(name, entryElement.getText());

				metadataElement.remove(entryElement);
			}
		}
	}

	protected void updateSchema() throws Exception {
		try (LoggingTimer loggingTimer = new LoggingTimer()) {
			alterTableAddColumn("DDMTemplate", "classNameId", "LONG");
			alterColumnName("DDMTemplate", "structureId", "classPK");

			long classNameId = PortalUtil.getClassNameId(
				"com.liferay.portlet.dynamicdatamapping.model.DDMStructure");

			try {
				runSQL("update DDMTemplate set classNameId = " + classNameId);
			}
			catch (Exception exception) {
				if (_log.isWarnEnabled()) {
					_log.warn(exception);
				}
			}
		}
	}

	protected void updateStructure(
			long structureId, String structureKey, String xsd)
		throws Exception {

		try (PreparedStatement preparedStatement = connection.prepareStatement(
				"update DDMStructure set structureKey = ?, xsd = ? where " +
					"structureId = ?")) {

			preparedStatement.setString(1, structureKey);
			preparedStatement.setString(2, xsd);
			preparedStatement.setLong(3, structureId);

			preparedStatement.executeUpdate();
		}
		catch (SQLException sqlException) {
			if (_log.isWarnEnabled()) {
				_log.warn(sqlException);
			}
		}
	}

	protected void updateStructures() throws Exception {
		try (LoggingTimer loggingTimer = new LoggingTimer();

			PreparedStatement preparedStatement = connection.prepareStatement(
				"select structureId, structureKey, xsd from DDMStructure");

			ResultSet resultSet = preparedStatement.executeQuery()) {

			while (resultSet.next()) {
				long structureId = resultSet.getLong("structureId");
				String structureKey = resultSet.getString("structureKey");
				String xsd = resultSet.getString("xsd");

				if (Validator.isNull(structureKey)) {
					structureKey = String.valueOf(System.currentTimeMillis());
				}
				else {
					structureKey = StringUtil.toUpperCase(structureKey.trim());
				}

				updateStructure(
					structureId, structureKey, updateXSD(xsd, structureKey));
			}
		}
	}

	protected void updateStructuresClassNameId() throws Exception {
		try (LoggingTimer loggingTimer = new LoggingTimer();
			PreparedStatement preparedStatement = connection.prepareStatement(
				"update DDMStructure set classNameId = ? where classNameId = " +
					"?")) {

			preparedStatement.setLong(
				1,
				PortalUtil.getClassNameId(
					"com.liferay.portlet.documentlibrary.model." +
						"DLFileEntryMetadata"));
			preparedStatement.setLong(
				2,
				PortalUtil.getClassNameId(
					"com.liferay.portlet.documentlibrary.model.DLFileEntry"));

			preparedStatement.executeUpdate();
		}
		catch (SQLException sqlException) {
			if (_log.isWarnEnabled()) {
				_log.warn(sqlException);
			}
		}
	}

	protected void updateTemplate(
			long templateId, String templateKey, String script)
		throws Exception {

		try (PreparedStatement preparedStatement = connection.prepareStatement(
				"update DDMTemplate set templateKey = ?, script = ? where " +
					"templateId = ?")) {

			preparedStatement.setString(1, templateKey);
			preparedStatement.setString(2, script);
			preparedStatement.setLong(3, templateId);

			preparedStatement.executeUpdate();
		}
	}

	protected void updateTemplates() throws Exception {
		try (LoggingTimer loggingTimer = new LoggingTimer();

			PreparedStatement preparedStatement = connection.prepareStatement(
				"select templateId, templateKey, script from DDMTemplate " +
					"where language = 'xsd'");

			ResultSet resultSet = preparedStatement.executeQuery()) {

			while (resultSet.next()) {
				long templateId = resultSet.getLong("templateId");
				String templateKey = resultSet.getString("templateKey");
				String script = resultSet.getString("script");

				if (Validator.isNull(templateKey)) {
					templateKey = String.valueOf(System.currentTimeMillis());
				}
				else {
					templateKey = StringUtil.toUpperCase(templateKey.trim());
				}

				updateTemplate(
					templateId, templateKey,
					updateXSD(script, StringPool.BLANK));
			}
		}
	}

	protected String updateXSD(String xsd, String structureKey)
		throws Exception {

		Document document = SAXReaderUtil.read(xsd);

		Element rootElement = document.getRootElement();

		List<Element> dynamicElementElements = rootElement.elements(
			"dynamic-element");

		for (Element dynamicElementElement : dynamicElementElements) {
			updateXSDDynamicElement(dynamicElementElement, structureKey);
		}

		return document.formattedString(StringPool.DOUBLE_SPACE);
	}

	protected void updateXSDDynamicElement(
		Element element, String structureKey) {

		Element metadataElement = element.element("meta-data");

		updateMetadataElement(
			metadataElement,
			new String[] {
				"multiple", "name", "readOnly", "repeatable", "required",
				"showLabel", "type", "width"
			},
			new String[] {
				"acceptFiles", "displayChildLabelAsValue", "fieldCssClass",
				"folder"
			});

		if (StringUtil.equalsIgnoreCase(
				structureKey, RawMetadataProcessor.TIKA_RAW_METADATA)) {

			element.addAttribute("indexType", "text");
		}

		List<Element> dynamicElementElements = element.elements(
			"dynamic-element");

		for (Element dynamicElementElement : dynamicElementElements) {
			updateXSDDynamicElement(dynamicElementElement, structureKey);
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		UpgradeDynamicDataMapping.class);

}