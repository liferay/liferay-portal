/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.journal.internal.upgrade.v4_0_0;

import com.liferay.dynamic.data.mapping.model.DDMStructure;
import com.liferay.dynamic.data.mapping.service.DDMFieldLocalService;
import com.liferay.dynamic.data.mapping.service.DDMStructureLocalService;
import com.liferay.dynamic.data.mapping.storage.DDMFormValues;
import com.liferay.dynamic.data.mapping.util.FieldsToDDMFormValuesConverter;
import com.liferay.journal.model.JournalArticle;
import com.liferay.journal.util.JournalConverter;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.security.xml.SecureXMLFactoryProviderUtil;
import com.liferay.portal.kernel.service.ClassNameLocalService;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.kernel.upgrade.UpgradeProcessFactory;
import com.liferay.portal.kernel.upgrade.UpgradeStep;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.StringUtil;

import java.io.StringReader;
import java.io.StringWriter;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import org.xml.sax.InputSource;

/**
 * @author Preston Crary
 */
public class JournalArticleDDMFieldsUpgradeProcess extends UpgradeProcess {

	public JournalArticleDDMFieldsUpgradeProcess(
		ClassNameLocalService classNameLocalService,
		CompanyLocalService companyLocalService,
		DDMFieldLocalService ddmFieldLocalService,
		DDMStructureLocalService ddmStructureLocalService,
		FieldsToDDMFormValuesConverter fieldsToDDMFormValuesConverter,
		JournalConverter journalConverter, Portal portal) {

		_classNameLocalService = classNameLocalService;
		_companyLocalService = companyLocalService;
		_ddmFieldLocalService = ddmFieldLocalService;
		_ddmStructureLocalService = ddmStructureLocalService;
		_fieldsToDDMFormValuesConverter = fieldsToDDMFormValuesConverter;
		_journalConverter = journalConverter;
		_portal = portal;
	}

	@Override
	protected void doUpgrade() throws Exception {
		_companyLocalService.forEachCompanyId(
			companyId -> {
				long classNameId = _classNameLocalService.getClassNameId(
					JournalArticle.class);

				try (PreparedStatement preparedStatement1 =
						connection.prepareStatement(
							"select id_, groupId, content, DDMStructureKey " +
								"from JournalArticle where companyId = ? and " +
									"ctCollectionId = 0")) {

					preparedStatement1.setLong(1, companyId);

					try (ResultSet resultSet =
							preparedStatement1.executeQuery()) {

						while (resultSet.next()) {
							DDMStructure ddmStructure =
								_ddmStructureLocalService.getStructure(
									_portal.getSiteGroupId(
										resultSet.getLong("groupId")),
									classNameId,
									resultSet.getString("DDMStructureKey"),
									true);

							DDMFormValues ddmFormValues =
								_fieldsToDDMFormValuesConverter.convert(
									ddmStructure,
									_journalConverter.getDDMFields(
										ddmStructure,
										_convertFieldNames(
											resultSet.getString("content"))));

							_ddmFieldLocalService.updateDDMFormValues(
								ddmStructure.getStructureId(),
								resultSet.getLong("id_"), ddmFormValues);
						}
					}
				}
			});
	}

	@Override
	protected UpgradeStep[] getPostUpgradeSteps() {
		return new UpgradeStep[] {
			UpgradeProcessFactory.dropColumns("JournalArticle", "content")
		};
	}

	private String _convertFieldNames(String content) throws Exception {
		TransformerFactory transformerFactory =
			TransformerFactory.newInstance();

		Transformer transformer = transformerFactory.newTransformer();

		Document document =
			SecureXMLFactoryProviderUtil.newDocumentBuilderFactory(
			).newDocumentBuilder(
			).parse(
				new InputSource(new StringReader(content))
			);

		NodeList nodeList = document.getElementsByTagName("dynamic-element");

		for (int i = 0; i < nodeList.getLength(); i++) {
			Node node = nodeList.item(i);

			NamedNodeMap namedNodeMap = node.getAttributes();

			Node instanceIdNode = namedNodeMap.getNamedItem("instance-id");

			if (instanceIdNode != null) {
				instanceIdNode.setTextContent(StringUtil.randomString());
			}

			Node nameNode = namedNodeMap.getNamedItem("name");

			String textContent = nameNode.getTextContent();

			nameNode.setTextContent(
				textContent.replaceAll(StringPool.MINUS, StringPool.BLANK));
		}

		StringWriter stringWriter = new StringWriter();

		transformer.transform(
			new DOMSource(document), new StreamResult(stringWriter));

		return stringWriter.getBuffer(
		).toString();
	}

	private final ClassNameLocalService _classNameLocalService;
	private final CompanyLocalService _companyLocalService;
	private final DDMFieldLocalService _ddmFieldLocalService;
	private final DDMStructureLocalService _ddmStructureLocalService;
	private final FieldsToDDMFormValuesConverter
		_fieldsToDDMFormValuesConverter;
	private final JournalConverter _journalConverter;
	private final Portal _portal;

}