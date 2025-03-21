/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.upgrade.v6_2_0;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.dao.orm.common.SQLTransformer;
import com.liferay.portal.kernel.dao.jdbc.AutoBatchPreparedStatementUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.ResourcePermission;
import com.liferay.portal.kernel.portlet.PortletPreferencesFactoryUtil;
import com.liferay.portal.kernel.util.FriendlyURLNormalizerUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HtmlUtil;
import com.liferay.portal.kernel.util.HttpComponentsUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.LoggingTimer;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.StringBundler;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.xml.Attribute;
import com.liferay.portal.kernel.xml.Document;
import com.liferay.portal.kernel.xml.Element;
import com.liferay.portal.kernel.xml.Node;
import com.liferay.portal.kernel.xml.SAXReaderUtil;
import com.liferay.portal.kernel.xml.XPath;
import com.liferay.portal.upgrade.v6_2_0.util.RSSUtil;

import jakarta.portlet.PortletPreferences;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * @author Brian Wing Shun Chan
 * @author Marcellus Tavares
 * @author Juan Fernández
 * @author Bruno Basto
 */
public class UpgradeJournal extends BaseUpgradePortletPreferences {

	protected void addDDMStructure(
			String uuid, long ddmStructureId, long groupId, long companyId,
			long userId, String userName, Timestamp createDate,
			Timestamp modifiedDate, long parentDDMStructureId, long classNameId,
			String ddmStructureKey, String name, String description, String xsd,
			String storageType, int type)
		throws Exception {

		StringBundler sb = new StringBundler(5);

		sb.append("insert into DDMStructure (uuid_, structureId, groupId, ");
		sb.append("companyId, userId, userName, createDate, modifiedDate, ");
		sb.append("parentStructureId, classNameId, structureKey, name, ");
		sb.append("description, xsd, storageType, type_) values (?, ?, ?, ?, ");
		sb.append("?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");

		String sql = sb.toString();

		try (PreparedStatement preparedStatement = connection.prepareStatement(
				sql)) {

			preparedStatement.setString(1, uuid);
			preparedStatement.setLong(2, ddmStructureId);
			preparedStatement.setLong(3, groupId);
			preparedStatement.setLong(4, companyId);
			preparedStatement.setLong(5, userId);
			preparedStatement.setString(6, userName);
			preparedStatement.setTimestamp(7, createDate);
			preparedStatement.setTimestamp(8, modifiedDate);
			preparedStatement.setLong(9, parentDDMStructureId);
			preparedStatement.setLong(10, classNameId);
			preparedStatement.setString(11, ddmStructureKey);
			preparedStatement.setString(12, name);
			preparedStatement.setString(13, description);
			preparedStatement.setString(
				14, getDDMXSD(xsd, getDefaultLocale(companyId)));
			preparedStatement.setString(15, storageType);
			preparedStatement.setInt(16, type);

			preparedStatement.executeUpdate();
		}
		catch (Exception exception) {
			_log.error(
				"Unable to upgrade dynamic data mapping structure with UUID " +
					uuid);

			throw exception;
		}
	}

	protected void addDDMStructure(
			String uuid, long ddmStructureId, long groupId, long companyId,
			long userId, String userName, Timestamp createDate,
			Timestamp modifiedDate, String parentStructureId,
			String ddmStructureKey, String name, String description, String xsd)
		throws Exception {

		long parentDDMStructureId = 0;

		if (Validator.isNotNull(parentStructureId)) {
			parentDDMStructureId = updateStructure(parentStructureId);
		}

		long insertedDDMStructureId = getDDMStructureId(
			groupId, ddmStructureKey, false);

		if (insertedDDMStructureId == 0) {
			addDDMStructure(
				uuid, ddmStructureId, groupId, companyId, userId, userName,
				createDate, modifiedDate, parentDDMStructureId,
				_getClassNameId(
					"com.liferay.portlet.journal.model.JournalArticle"),
				ddmStructureKey, name, description, xsd, "xml",
				_DDM_STRUCTURE_TYPE_DEFAULT);
		}
	}

	protected void addDDMTemplate(
			String uuid, long ddmTemplateId, long groupId, long companyId,
			long userId, String userName, Timestamp createDate,
			Timestamp modifiedDate, long classNameId, long classPK,
			String templateKey, String name, String description, String type,
			String mode, String language, String script, boolean cacheable,
			boolean smallImage, long smallImageId, String smallImageURL)
		throws Exception {

		StringBundler sb = new StringBundler(6);

		sb.append("insert into DDMTemplate (uuid_, templateId, groupId, ");
		sb.append("companyId, userId, userName, createDate, modifiedDate,");
		sb.append("classNameId, classPK , templateKey, name, description,");
		sb.append("type_, mode_, language, script, cacheable, smallImage,");
		sb.append("smallImageId, smallImageURL) values (?, ?, ?, ?, ?, ?,?, ");
		sb.append("?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");

		String sql = sb.toString();

		try (PreparedStatement preparedStatement = connection.prepareStatement(
				sql)) {

			preparedStatement.setString(1, uuid);
			preparedStatement.setLong(2, ddmTemplateId);
			preparedStatement.setLong(3, groupId);
			preparedStatement.setLong(4, companyId);
			preparedStatement.setLong(5, userId);
			preparedStatement.setString(6, userName);
			preparedStatement.setTimestamp(7, createDate);
			preparedStatement.setTimestamp(8, modifiedDate);
			preparedStatement.setLong(9, classNameId);
			preparedStatement.setLong(10, classPK);
			preparedStatement.setString(11, templateKey);
			preparedStatement.setString(12, name);
			preparedStatement.setString(13, description);
			preparedStatement.setString(14, type);
			preparedStatement.setString(15, mode);
			preparedStatement.setString(16, language);
			preparedStatement.setString(17, script);
			preparedStatement.setBoolean(18, cacheable);
			preparedStatement.setBoolean(19, smallImage);
			preparedStatement.setLong(20, smallImageId);
			preparedStatement.setString(21, smallImageURL);

			preparedStatement.executeUpdate();
		}
		catch (Exception exception) {
			_log.error(
				"Unable to upgrade dynamic data mapping template with UUID " +
					uuid);

			throw exception;
		}
	}

	protected void addMetadataEntry(
		Element metadataElement, String name, String value) {

		Element entryElement = metadataElement.addElement("entry");

		entryElement.addAttribute("name", name);
		entryElement.addCDATA(value);
	}

	protected void addResourcePermission(
		PreparedStatement preparedStatement, long companyId, String primKey,
		long roleId) {

		try {
			preparedStatement.setLong(
				1, increment(ResourcePermission.class.getName()));
			preparedStatement.setLong(2, companyId);
			preparedStatement.setString(3, "com.liferay.portlet.journal");
			preparedStatement.setInt(4, ResourceConstants.SCOPE_INDIVIDUAL);
			preparedStatement.setString(5, primKey);
			preparedStatement.setLong(6, roleId);
			preparedStatement.setLong(7, 0);
			preparedStatement.setLong(8, 1);

			preparedStatement.addBatch();
		}
		catch (Exception exception) {
			_log.error("Unable to insert ResourcePermission", exception);
		}
	}

	protected String decodeURL(String url) {
		try {
			return HttpComponentsUtil.decodeURL(url);
		}
		catch (IllegalArgumentException illegalArgumentException) {
			if (_log.isDebugEnabled()) {
				_log.debug(illegalArgumentException);
			}

			return url;
		}
	}

	@Override
	protected void doUpgrade() throws Exception {
		alterColumnName(
			"JournalFeed", "feedType", "feedFormat VARCHAR(75) null");

		setUpStrutureAttributesMappings();

		updateContentSearch();
		updateLinkToLayoutContent();
		updateStructures();
		updateTemplates();
		upgradeURLTitle();

		updateAssetEntryClassTypeId();
		updateJournalResourcePermission();

		super.doUpgrade();
	}

	protected Element fetchMetadataEntry(
		Element parentElement, String attributeName, String attributeValue) {

		StringBundler sb = new StringBundler(5);

		sb.append("entry[@");
		sb.append(attributeName);
		sb.append(StringPool.EQUAL);
		sb.append(HtmlUtil.escapeXPathAttribute(attributeValue));
		sb.append(StringPool.CLOSE_BRACKET);

		XPath xPath = SAXReaderUtil.createXPath(sb.toString());

		return (Element)xPath.selectSingleNode(parentElement);
	}

	protected long getCompanyGroupId(long companyId) throws Exception {
		Long companyGroupId = _companyGroupIds.get(companyId);

		if (companyGroupId != null) {
			return companyGroupId;
		}

		try (PreparedStatement preparedStatement = connection.prepareStatement(
				"select groupId from Group_ where classNameId = ? and " +
					"classPK = ?")) {

			preparedStatement.setLong(
				1, _getClassNameId("com.liferay.portal.model.Company"));
			preparedStatement.setLong(2, companyId);

			try (ResultSet resultSet = preparedStatement.executeQuery()) {
				if (resultSet.next()) {
					companyGroupId = resultSet.getLong("groupId");
				}
				else {
					companyGroupId = 0L;
				}

				_companyGroupIds.put(companyId, companyGroupId);

				return companyGroupId;
			}
		}
	}

	protected long getDDMStructureClassNameId() {
		return _getClassNameId(
			"com.liferay.portlet.dynamicdatamapping.model.DDMStructure");
	}

	protected long getDDMStructureId(
		long groupId, long companyGroupId, String structureId) {

		return getDDMStructureId(groupId, companyGroupId, structureId, true);
	}

	protected long getDDMStructureId(
		long groupId, long companyGroupId, String structureId, boolean warn) {

		if (Validator.isNull(structureId)) {
			return 0;
		}

		Long ddmStructureId = _ddmStructureIds.get(groupId + "#" + structureId);

		if ((ddmStructureId == null) && (companyGroupId != 0)) {
			ddmStructureId = _ddmStructureIds.get(
				companyGroupId + "#" + structureId);
		}

		if (ddmStructureId != null) {
			return ddmStructureId;
		}

		if (warn && _log.isWarnEnabled()) {
			StringBundler sb = new StringBundler(5);

			sb.append("Unable to get the DDM structure ID for group ");
			sb.append(groupId);

			if (companyGroupId != 0) {
				sb.append(" or global group");
			}

			sb.append(" and journal structure ID ");
			sb.append(structureId);

			_log.warn(sb.toString());
		}

		return 0;
	}

	protected long getDDMStructureId(
		long groupId, String structureId, boolean warn) {

		return getDDMStructureId(groupId, 0, structureId, warn);
	}

	protected String getDDMXSD(String journalXSD, Locale defaultLocale)
		throws Exception {

		Document document = SAXReaderUtil.read(journalXSD);

		Element rootElement = document.getRootElement();

		rootElement.addAttribute("available-locales", defaultLocale.toString());
		rootElement.addAttribute("default-locale", defaultLocale.toString());

		List<Element> dynamicElementElements = rootElement.elements(
			"dynamic-element");

		for (Element dynamicElementElement : dynamicElementElements) {
			updateJournalXSDDynamicElement(
				dynamicElementElement, defaultLocale.toString());
		}

		return document.formattedString(StringPool.DOUBLE_SPACE);
	}

	protected Locale getDefaultLocale(long companyId) throws Exception {
		try (PreparedStatement preparedStatement = connection.prepareStatement(
				"select languageId from User_ where companyId = ? and " +
					"defaultUser = ?")) {

			preparedStatement.setLong(1, companyId);
			preparedStatement.setBoolean(2, true);

			try (ResultSet resultSet = preparedStatement.executeQuery()) {
				if (resultSet.next()) {
					String languageId = resultSet.getString("languageId");

					return LocaleUtil.fromLanguageId(languageId);
				}
			}
		}

		return LocaleUtil.getSiteDefault();
	}

	protected long getJournalStructureClassNameId() {
		return _getClassNameId(
			"com.liferay.portlet.journal.model.JournalStructure");
	}

	@Override
	protected String[] getPortletIds() {
		return new String[] {
			"56_INSTANCE_%", "62_INSTANCE_%", "101_INSTANCE_%"
		};
	}

	protected long getRoleId(String roleName) throws Exception {
		try (PreparedStatement preparedStatement = connection.prepareStatement(
				"select roleId from Role_ where name = ?")) {

			preparedStatement.setString(1, roleName);

			try (ResultSet resultSet = preparedStatement.executeQuery()) {
				if (resultSet.next()) {
					return resultSet.getLong("roleId");
				}

				return 0;
			}
		}
	}

	protected void removeAttribute(Element element, String attributeName) {
		Attribute attribute = element.attribute(attributeName);

		if (attribute == null) {
			return;
		}

		element.remove(attribute);
	}

	protected void setUpStrutureAttributesMappings() {
		_ddmDataTypes.put("boolean", "boolean");
		_ddmDataTypes.put("document_library", "document-library");
		_ddmDataTypes.put("image", "image");
		_ddmDataTypes.put("link_to_layout", "link-to-page");
		_ddmDataTypes.put("list", "string");
		_ddmDataTypes.put("multi-list", "string");
		_ddmDataTypes.put("text", "string");
		_ddmDataTypes.put("text_area", "html");
		_ddmDataTypes.put("text_box", "string");

		_ddmMetadataAttributes.put("instructions", "tip");
		_ddmMetadataAttributes.put("label", "label");
		_ddmMetadataAttributes.put("predefinedValue", "predefinedValue");

		_journalTypesToDDMTypes.put("boolean", "checkbox");
		_journalTypesToDDMTypes.put("document_library", "ddm-documentlibrary");
		_journalTypesToDDMTypes.put("image", "ddm-image");
		_journalTypesToDDMTypes.put("image_gallery", "ddm-documentlibrary");
		_journalTypesToDDMTypes.put("link_to_layout", "ddm-link-to-page");
		_journalTypesToDDMTypes.put("list", "select");
		_journalTypesToDDMTypes.put("multi-list", "select");
		_journalTypesToDDMTypes.put("selection_break", "ddm-separator");
		_journalTypesToDDMTypes.put("text", "text");
		_journalTypesToDDMTypes.put("text_area", "ddm-text-html");
		_journalTypesToDDMTypes.put("text_box", "textarea");
	}

	protected void updateAssetEntryClassTypeId() throws Exception {
		try (LoggingTimer loggingTimer = new LoggingTimer();
			PreparedStatement preparedStatement1 = connection.prepareStatement(
				SQLTransformer.transform(
					"select distinct companyId, groupId, resourcePrimKey, " +
						"structureId from JournalArticle where structureId " +
							"!= ''"));
			ResultSet resultSet = preparedStatement1.executeQuery()) {

			long classNameId = _getClassNameId(
				"com.liferay.portlet.journal.model.JournalArticle");

			try (PreparedStatement preparedStatement2 =
					AutoBatchPreparedStatementUtil.concurrentAutoBatch(
						connection,
						"update AssetEntry set classTypeId = ? where " +
							"classNameId = ? AND classPK = ?")) {

				while (resultSet.next()) {
					long groupId = resultSet.getLong("groupId");
					long companyId = resultSet.getLong("companyId");
					long resourcePrimKey = resultSet.getLong("resourcePrimKey");
					String structureId = resultSet.getString("structureId");

					preparedStatement2.setLong(
						1,
						getDDMStructureId(
							groupId, getCompanyGroupId(companyId),
							structureId));

					preparedStatement2.setLong(2, classNameId);
					preparedStatement2.setLong(3, resourcePrimKey);
					preparedStatement2.addBatch();
				}

				preparedStatement2.executeBatch();
			}
		}
	}

	protected void updateContentSearch() throws Exception {
		try (LoggingTimer loggingTimer = new LoggingTimer();
			PreparedStatement preparedStatement = connection.prepareStatement(
				"select groupId, portletId from JournalContentSearch group " +
					"by groupId, portletId having count(groupId) > 1 and " +
						"count(portletId) > 1");
			ResultSet resultSet = preparedStatement.executeQuery()) {

			while (resultSet.next()) {
				long groupId = resultSet.getLong("groupId");
				String portletId = resultSet.getString("portletId");

				updateContentSearch(groupId, portletId);
			}
		}
	}

	protected void updateContentSearch(long groupId, String portletId)
		throws Exception {

		try (PreparedStatement preparedStatement1 = connection.prepareStatement(
				"select preferences from PortletPreferences inner join " +
					"Layout on PortletPreferences.plid = Layout.plid where " +
						"groupId = ? and portletId = ?");
			PreparedStatement preparedStatement2 = connection.prepareStatement(
				"select companyId, privateLayout, layoutId, portletId from " +
					"JournalContentSearch where JournalContentSearch.groupId " +
						"= ? and JournalContentSearch.articleId = ?");
			PreparedStatement preparedStatement3 = connection.prepareStatement(
				"delete from JournalContentSearch where " +
					"JournalContentSearch.groupId = ? and " +
						"JournalContentSearch.articleId = ?");
			PreparedStatement preparedStatement4 = connection.prepareStatement(
				"insert into JournalContentSearch(contentSearchId, " +
					"companyId, groupId, privateLayout, layoutId, portletId, " +
						"articleId) values (?, ?, ?, ?, ?, ?, ?)")) {

			preparedStatement1.setLong(1, groupId);
			preparedStatement1.setString(2, portletId);

			try (ResultSet resultSet1 = preparedStatement1.executeQuery()) {
				while (resultSet1.next()) {
					String xml = resultSet1.getString("preferences");

					PortletPreferences portletPreferences =
						PortletPreferencesFactoryUtil.fromDefaultXML(xml);

					String articleId = portletPreferences.getValue(
						"articleId", null);

					preparedStatement2.setLong(1, groupId);
					preparedStatement2.setString(2, articleId);

					try (ResultSet resultSet2 =
							preparedStatement2.executeQuery()) {

						if (resultSet2.next()) {
							long companyId = resultSet2.getLong("companyId");
							boolean privateLayout = resultSet2.getBoolean(
								"privateLayout");
							long layoutId = resultSet2.getLong("layoutId");
							String journalContentSearchPortletId =
								resultSet2.getString("portletId");

							preparedStatement3.setLong(1, groupId);
							preparedStatement3.setString(2, articleId);

							preparedStatement3.executeUpdate();

							preparedStatement4.setLong(1, increment());
							preparedStatement4.setLong(2, companyId);
							preparedStatement4.setLong(3, groupId);
							preparedStatement4.setBoolean(4, privateLayout);
							preparedStatement4.setLong(5, layoutId);
							preparedStatement4.setString(
								6, journalContentSearchPortletId);
							preparedStatement4.setString(7, articleId);

							preparedStatement4.executeUpdate();
						}
					}
				}
			}
		}
	}

	protected void updateElement(long groupId, Element element) {
		List<Element> dynamicElementElements = element.elements(
			"dynamic-element");

		for (Element dynamicElementElement : dynamicElementElements) {
			updateElement(groupId, dynamicElementElement);
		}

		String type = element.attributeValue("type");

		if (type.equals("link_to_layout")) {
			updateLinkToLayoutElements(groupId, element);
		}
	}

	protected void updateJournalArticleClassNameIdAndClassPK(
			long journalStructureId, Long ddmStructureId)
		throws Exception {

		try (PreparedStatement preparedStatement = connection.prepareStatement(
				"update JournalArticle set classNameId = ?, classPK = ? " +
					"where classNameId = ? and classPK = ?")) {

			preparedStatement.setLong(1, getDDMStructureClassNameId());
			preparedStatement.setLong(2, ddmStructureId);
			preparedStatement.setLong(3, getJournalStructureClassNameId());
			preparedStatement.setLong(4, journalStructureId);

			preparedStatement.execute();
		}
	}

	protected void updateJournalResourcePermission() throws Exception {
		long guestRoleId = getRoleId("Guest");
		long ownerRoleId = getRoleId("Owner");
		long siteMemberRoleId = getRoleId("Site Member");

		StringBundler updateSB = new StringBundler(10);

		updateSB.append("update ResourcePermission set actionIds = actionIds ");
		updateSB.append("+ 1 where name = 'com.liferay.portlet.journal' and ");
		updateSB.append("roleId in (");
		updateSB.append(guestRoleId);
		updateSB.append(",");
		updateSB.append(ownerRoleId);
		updateSB.append(",");
		updateSB.append(siteMemberRoleId);
		updateSB.append(") and ownerId = 0 and MOD(actionIds, 2) = 0 and ");
		updateSB.append("scope = 4");

		runSQL(updateSB.toString());

		StringBundler selectSB = new StringBundler(10);

		selectSB.append("select companyId, primKey, roleId from ");
		selectSB.append("ResourcePermission where name = ");
		selectSB.append("'com.liferay.portlet.journal' and ownerId = 0 and ");
		selectSB.append("scope = 4 and roleId in (");
		selectSB.append(guestRoleId);
		selectSB.append(",");
		selectSB.append(ownerRoleId);
		selectSB.append(",");
		selectSB.append(siteMemberRoleId);
		selectSB.append(") order by companyId, primKey, roleId");

		StringBundler insertSB = new StringBundler(4);

		insertSB.append("insert into ResourcePermission ");
		insertSB.append("(resourcePermissionId, companyId, name, scope, ");
		insertSB.append("primKey, roleId, ownerId, actionIds) values (?, ?, ");
		insertSB.append("?, ?, ?, ?, ?, ?)");

		try (LoggingTimer loggingTimer = new LoggingTimer();
			PreparedStatement preparedStatement1 = connection.prepareStatement(
				selectSB.toString());
			PreparedStatement preparedStatement2 =
				AutoBatchPreparedStatementUtil.autoBatch(
					connection, insertSB.toString());
			ResultSet resultSet = preparedStatement1.executeQuery()) {

			long currentCompanyId = 0;
			String currentPrimKey = null;
			boolean hasGuestResourcePermissions = false;
			boolean hasOwnerResourcePermissions = false;
			boolean hasSiteMemberResourcePermissions = false;

			while (resultSet.next()) {
				long companyId = resultSet.getLong("companyId");
				String primKey = resultSet.getString("primKey");
				long roleId = resultSet.getLong("roleId");

				if ((currentPrimKey != null) &&
					!primKey.equals(currentPrimKey)) {

					if (!hasGuestResourcePermissions) {
						addResourcePermission(
							preparedStatement2, currentCompanyId,
							currentPrimKey, guestRoleId);
					}

					if (!hasOwnerResourcePermissions) {
						addResourcePermission(
							preparedStatement2, currentCompanyId,
							currentPrimKey, ownerRoleId);
					}

					if (!hasSiteMemberResourcePermissions) {
						addResourcePermission(
							preparedStatement2, currentCompanyId,
							currentPrimKey, siteMemberRoleId);
					}

					currentPrimKey = primKey;
					currentCompanyId = companyId;
					hasGuestResourcePermissions = false;
					hasOwnerResourcePermissions = false;
					hasSiteMemberResourcePermissions = false;
				}

				if (currentPrimKey == null) {
					currentCompanyId = companyId;
					currentPrimKey = primKey;
				}

				if (guestRoleId == roleId) {
					hasGuestResourcePermissions = true;
				}
				else if (ownerRoleId == roleId) {
					hasOwnerResourcePermissions = true;
				}
				else if (siteMemberRoleId == roleId) {
					hasSiteMemberResourcePermissions = true;
				}
			}

			if (currentPrimKey != null) {
				if (!hasGuestResourcePermissions) {
					addResourcePermission(
						preparedStatement2, currentCompanyId, currentPrimKey,
						guestRoleId);
				}

				if (!hasOwnerResourcePermissions) {
					addResourcePermission(
						preparedStatement2, currentCompanyId, currentPrimKey,
						ownerRoleId);
				}

				if (!hasSiteMemberResourcePermissions) {
					addResourcePermission(
						preparedStatement2, currentCompanyId, currentPrimKey,
						siteMemberRoleId);
				}
			}

			preparedStatement2.executeBatch();
		}
	}

	protected void updateJournalXSDDynamicElement(
		Element element, String defaultLanguageId) {

		String name = element.attributeValue("name");
		String type = element.attributeValue("type");

		Element metadataElement = element.element("meta-data");

		if (metadataElement == null) {
			metadataElement = element.addElement("meta-data");
		}

		if (type.equals("multi-list")) {
			element.addAttribute("multiple", "true");
		}
		else {
			Element parentElement = element.getParent();

			String parentType = parentElement.attributeValue("type");

			if ((parentType != null) && parentType.equals("select")) {
				metadataElement.addAttribute("locale", defaultLanguageId);

				addMetadataEntry(metadataElement, "label", decodeURL(name));

				removeAttribute(element, "index-type");

				element.addAttribute("name", "option" + StringUtil.randomId());
				element.addAttribute("type", "option");
				element.addAttribute("value", decodeURL(type));

				return;
			}
		}

		String indexType = StringPool.BLANK;

		Attribute indexTypeAttribute = element.attribute("index-type");

		if (indexTypeAttribute != null) {
			indexType = indexTypeAttribute.getValue();

			element.remove(indexTypeAttribute);
		}

		element.remove(element.attribute("type"));

		if (!type.equals("selection_break")) {
			String dataType = _ddmDataTypes.get(type);

			if (dataType == null) {
				dataType = "string";
			}

			element.addAttribute("dataType", dataType);
		}

		element.addAttribute("indexType", indexType);

		String required = "false";

		Element requiredElement = fetchMetadataEntry(
			metadataElement, "name", "required");

		if (requiredElement != null) {
			required = requiredElement.getText();
		}

		element.addAttribute("required", required);

		element.addAttribute("showLabel", "true");

		String newType = _journalTypesToDDMTypes.get(type);

		if (newType == null) {
			newType = type;
		}

		element.addAttribute("type", newType);

		if (newType.startsWith("ddm")) {
			element.addAttribute("fieldNamespace", "ddm");
		}

		metadataElement.addAttribute("locale", defaultLanguageId);

		List<Element> entryElements = metadataElement.elements();

		if (entryElements.isEmpty()) {
			addMetadataEntry(metadataElement, "label", name);
		}
		else {
			for (Element entryElement : entryElements) {
				String oldEntryName = entryElement.attributeValue("name");

				String newEntryName = _ddmMetadataAttributes.get(oldEntryName);

				if (newEntryName == null) {
					metadataElement.remove(entryElement);
				}
				else {
					entryElement.addAttribute("name", newEntryName);
				}
			}
		}

		if (newType.equals("ddm-date") || newType.equals("ddm-decimal") ||
			newType.equals("ddm-integer") ||
			newType.equals("ddm-link-to-page") ||
			newType.equals("ddm-number") || newType.equals("ddm-text-html") ||
			newType.equals("text") || newType.equals("textarea")) {

			element.addAttribute("width", "25");
		}
		else if (newType.equals("ddm-image")) {
			element.addAttribute("fieldNamespace", "ddm");
			element.addAttribute("readOnly", "false");
		}

		element.add(metadataElement.detach());

		List<Element> dynamicElementElements = element.elements(
			"dynamic-element");

		for (Element dynamicElementElement : dynamicElementElements) {
			updateJournalXSDDynamicElement(
				dynamicElementElement, defaultLanguageId);
		}
	}

	protected void updateLinkToLayoutContent() throws Exception {
		try (LoggingTimer loggingTimer = new LoggingTimer();
			PreparedStatement preparedStatement1 = connection.prepareStatement(
				SQLTransformer.transform(
					"select id_, groupId, content from JournalArticle where " +
						"structureId != '' and content like " +
							"'%link_to_layout%'"));
			PreparedStatement preparedStatement2 =
				AutoBatchPreparedStatementUtil.autoBatch(
					connection,
					"update JournalArticle set content = ? where id_ = ?");
			ResultSet resultSet = preparedStatement1.executeQuery()) {

			while (resultSet.next()) {
				long id = resultSet.getLong("id_");
				long groupId = resultSet.getLong("groupId");
				String content = resultSet.getString("content");

				try {
					Document document = SAXReaderUtil.read(content);

					Element rootElement = document.getRootElement();

					for (Element element : rootElement.elements()) {
						updateElement(groupId, element);
					}

					preparedStatement2.setString(1, document.asXML());
					preparedStatement2.setLong(2, id);

					preparedStatement2.addBatch();
				}
				catch (Exception exception) {
					_log.error(
						"Unable to update content for article " + id,
						exception);
				}
			}

			preparedStatement2.executeBatch();
		}
	}

	protected void updateLinkToLayoutElements(long groupId, Element element) {
		Element dynamicContentElement = element.element("dynamic-content");

		Node node = dynamicContentElement.node(0);

		String text = node.getText();

		if (!text.isEmpty() && !text.endsWith(StringPool.AT + groupId)) {
			node.setText(
				dynamicContentElement.getStringValue() + StringPool.AT +
					groupId);
		}
	}

	protected void updatePreferencesClassPKs(
			PortletPreferences portletPreferences, String key)
		throws Exception {

		String[] oldValues = portletPreferences.getValues(key, null);

		if (oldValues == null) {
			return;
		}

		String[] newValues = new String[oldValues.length];

		for (int i = 0; i < oldValues.length; i++) {
			String oldValue = oldValues[i];

			String newValue = oldValue;

			String[] oldPrimaryKeys = StringUtil.split(oldValue);

			for (String oldPrimaryKey : oldPrimaryKeys) {
				if (!Validator.isNumber(oldPrimaryKey)) {
					break;
				}

				Long newPrimaryKey = _ddmStructurePKs.get(
					GetterUtil.getLong(oldPrimaryKey));

				if (Validator.isNotNull(newPrimaryKey)) {
					newValue = StringUtil.replace(
						newValue, oldPrimaryKey, String.valueOf(newPrimaryKey));
				}
			}

			newValues[i] = newValue;
		}

		portletPreferences.setValues(key, newValues);
	}

	protected void updateResourcePermission(
			long companyId, String oldClassName, String newClassName,
			long oldPrimKey, long newPrimKey)
		throws Exception {

		StringBundler sb = new StringBundler(11);

		sb.append("update ResourcePermission set name = '");
		sb.append(newClassName);
		sb.append("', primKey = '");
		sb.append(newPrimKey);
		sb.append("' where companyId = ");
		sb.append(companyId);
		sb.append(" and name = '");
		sb.append(oldClassName);
		sb.append("' and primKey = '");
		sb.append(oldPrimKey);
		sb.append("'");

		runSQL(sb.toString());
	}

	protected long updateStructure(ResultSet resultSet) throws Exception {
		long groupId = resultSet.getLong("groupId");
		String structureId = resultSet.getString("structureId");

		Long ddmStructureId = _ddmStructureIds.get(groupId + "#" + structureId);

		if (ddmStructureId != null) {
			return ddmStructureId;
		}

		ddmStructureId = increment();

		String uuid_ = resultSet.getString("uuid_");
		long companyId = resultSet.getLong("companyId");
		long userId = resultSet.getLong("userId");
		String userName = resultSet.getString("userName");
		Timestamp createDate = resultSet.getTimestamp("createDate");
		Timestamp modifiedDate = resultSet.getTimestamp("modifiedDate");
		String parentStructureId = resultSet.getString("parentStructureId");
		String name = resultSet.getString("name");
		String description = resultSet.getString("description");
		String xsd = resultSet.getString("xsd");

		addDDMStructure(
			uuid_, ddmStructureId, groupId, companyId, userId, userName,
			createDate, modifiedDate, parentStructureId, structureId, name,
			description, xsd);

		long id_ = resultSet.getLong("id_");

		updateJournalArticleClassNameIdAndClassPK(id_, ddmStructureId);

		updateResourcePermission(
			companyId, "com.liferay.portlet.journal.model.JournalStructure",
			"com.liferay.portlet.dynamicdatamapping.model.DDMStructure", id_,
			ddmStructureId);

		_ddmStructureIds.put(groupId + "#" + structureId, ddmStructureId);
		_ddmStructurePKs.put(id_, ddmStructureId);

		return ddmStructureId;
	}

	protected long updateStructure(String structureId) throws Exception {
		try (PreparedStatement preparedStatement = connection.prepareStatement(
				"select * from JournalStructure where structureId = ?")) {

			preparedStatement.setString(1, structureId);

			try (ResultSet resultSet = preparedStatement.executeQuery()) {
				if (resultSet.next()) {
					return updateStructure(resultSet);
				}

				return 0;
			}
		}
		catch (Exception exception) {
			_log.error(
				"Unable to update journal structure with structure ID " +
					structureId);

			throw exception;
		}
	}

	protected void updateStructures() throws Exception {
		try (LoggingTimer loggingTimer = new LoggingTimer();
			PreparedStatement preparedStatement = connection.prepareStatement(
				"select * from JournalStructure");
			ResultSet resultSet = preparedStatement.executeQuery()) {

			while (resultSet.next()) {
				updateStructure(resultSet);
			}

			dropTable("JournalStructure");
		}
	}

	protected void updateTemplates() throws Exception {
		try (LoggingTimer loggingTimer = new LoggingTimer();
			PreparedStatement preparedStatement = connection.prepareStatement(
				"select * from JournalTemplate");
			ResultSet resultSet = preparedStatement.executeQuery()) {

			while (resultSet.next()) {
				String uuid_ = resultSet.getString("uuid_");
				long id_ = resultSet.getLong("id_");
				long groupId = resultSet.getLong("groupId");
				long companyId = resultSet.getLong("companyId");
				long userId = resultSet.getLong("userId");
				String userName = resultSet.getString("userName");
				Timestamp createDate = resultSet.getTimestamp("createDate");
				Timestamp modifiedDate = resultSet.getTimestamp("modifiedDate");
				String templateId = resultSet.getString("templateId");
				String structureId = resultSet.getString("structureId");
				String name = resultSet.getString("name");
				String description = resultSet.getString("description");
				String language = resultSet.getString("langType");
				String script = resultSet.getString("xsl");
				boolean cacheable = resultSet.getBoolean("cacheable");
				boolean smallImage = resultSet.getBoolean("smallImage");
				long smallImageId = resultSet.getLong("smallImageId");
				String smallImageURL = resultSet.getString("smallImageURL");

				long ddmTemplateId = increment();

				long classNameId = getDDMStructureClassNameId();

				long classPK = getDDMStructureId(
					groupId, getCompanyGroupId(companyId), structureId);

				addDDMTemplate(
					uuid_, ddmTemplateId, groupId, companyId, userId, userName,
					createDate, modifiedDate, classNameId, classPK, templateId,
					name, description, _DDM_TEMPLATE_TYPE_DISPLAY,
					_DDM_TEMPLATE_MODE_CREATE, language, script, cacheable,
					smallImage, smallImageId, smallImageURL);

				updateResourcePermission(
					companyId,
					"com.liferay.portlet.journal.model.JournalTemplate",
					"com.liferay.portlet.dynamicdatamapping.model.DDMTemplate",
					id_, ddmTemplateId);
			}

			dropTable("JournalTemplate");
		}
	}

	@Override
	protected String upgradePreferences(
			long companyId, long ownerId, int ownerType, long plid,
			String portletId, String xml)
		throws Exception {

		PortletPreferences portletPreferences =
			PortletPreferencesFactoryUtil.fromXML(
				companyId, ownerId, ownerType, plid, portletId, xml);

		if (portletId.startsWith(_PORTLET_ID_ASSET_PUBLISHER)) {
			updatePreferencesClassPKs(
				portletPreferences,
				"anyClassTypeJournalArticleAssetRendererFactory");
			updatePreferencesClassPKs(portletPreferences, "classTypeIds");
			updatePreferencesClassPKs(
				portletPreferences,
				"classTypeIdsJournalArticleAssetRendererFactory");

			// Moved from com.liferay.portal.upgrade.v6_2_0.
			// UpgradeAssetPublisher to improve performance

			upgradeRss(portletPreferences);
			upgradeScopeIds(portletPreferences);
		}
		else if (portletId.startsWith(_PORTLET_ID_JOURNAL_CONTENT)) {
			String templateId = portletPreferences.getValue(
				"templateId", StringPool.BLANK);

			if (Validator.isNotNull(templateId)) {
				portletPreferences.reset("templateId");

				portletPreferences.setValue("ddmTemplateKey", templateId);
			}
		}
		else if (portletId.startsWith(_PORTLET_ID_JOURNAL_CONTENT_LIST)) {
			String structureId = portletPreferences.getValue(
				"structureId", StringPool.BLANK);

			if (Validator.isNotNull(structureId)) {
				portletPreferences.reset("structureId");

				portletPreferences.setValue("ddmStructureKey", structureId);
			}
		}

		return PortletPreferencesFactoryUtil.toXML(portletPreferences);
	}

	protected void upgradeRss(PortletPreferences portletPreferences)
		throws Exception {

		String rssFormat = GetterUtil.getString(
			portletPreferences.getValue("rssFormat", null));

		if (Validator.isNotNull(rssFormat)) {
			portletPreferences.setValue(
				"rssFeedType",
				RSSUtil.getFeedType(
					RSSUtil.getFormatType(rssFormat),
					RSSUtil.getFormatVersion(rssFormat)));
		}

		portletPreferences.reset("rssFormat");
	}

	protected void upgradeScopeIds(PortletPreferences portletPreferences)
		throws Exception {

		String defaultScope = GetterUtil.getString(
			portletPreferences.getValue("defaultScope", null));

		if (Validator.isNull(defaultScope)) {
			return;
		}

		if (defaultScope.equals("true")) {
			portletPreferences.setValues(
				"scopeIds", new String[] {"Group_default"});
		}
		else if (!defaultScope.equals("false")) {
			portletPreferences.setValues(
				"scopeIds", new String[] {defaultScope});
		}

		portletPreferences.reset("defaultScope");
	}

	protected void upgradeURLTitle() throws Exception {
		_runSQL("create index IX_LPP_41834_UXEC on JournalArticle (urlTitle);");

		int count = 0;

		try (LoggingTimer loggingTimer = new LoggingTimer();
			PreparedStatement preparedStatement1 = connection.prepareStatement(
				"select distinct groupId, articleId, urlTitle from " +
					"JournalArticle");
			ResultSet resultSet = preparedStatement1.executeQuery()) {

			Map<String, String> processedArticleIds = new HashMap<>();

			try (PreparedStatement preparedStatement2 =
					AutoBatchPreparedStatementUtil.autoBatch(
						connection,
						"update JournalArticle set urlTitle = ? where " +
							"urlTitle = ?")) {

				while (resultSet.next()) {
					String urlTitle = GetterUtil.getString(
						resultSet.getString("urlTitle"));

					String normalizedURLTitle =
						FriendlyURLNormalizerUtil.
							normalizeWithPeriodsAndSlashes(urlTitle);

					if (urlTitle.equals(normalizedURLTitle)) {
						continue;
					}

					long groupId = resultSet.getLong("groupId");
					String articleId = resultSet.getString("articleId");

					normalizedURLTitle = _getUniqueUrlTitle(
						groupId, articleId, normalizedURLTitle,
						processedArticleIds);

					preparedStatement2.setString(1, normalizedURLTitle);

					preparedStatement2.setString(2, urlTitle);

					preparedStatement2.addBatch();

					count++;
				}

				preparedStatement2.executeBatch();
			}
		}

		if (_log.isInfoEnabled()) {
			_log.info("Updated " + count + " journal articles");
		}
	}

	private long _getClassNameId(String className) {
		Long classNameId = _classNameIds.get(className);

		if (classNameId == null) {
			classNameId = PortalUtil.getClassNameId(className);

			_classNameIds.put(className, classNameId);
		}

		return classNameId;
	}

	private String _getUniqueUrlTitle(
			long groupId, String articleId, String urlTitle,
			Map<String, String> processedArticleIds)
		throws Exception {

		for (int i = 1;; i++) {
			String key = groupId + StringPool.UNDERLINE + urlTitle;

			String processedArticleId = processedArticleIds.get(key);

			if (((processedArticleId == null) ||
				 processedArticleId.equals(articleId)) &&
				_isValidUrlTitle(groupId, articleId, urlTitle)) {

				processedArticleIds.put(key, articleId);

				return urlTitle;
			}

			String suffix = StringPool.DASH + i;

			String prefix = urlTitle;

			if (urlTitle.length() > suffix.length()) {
				prefix = urlTitle.substring(
					0, urlTitle.length() - suffix.length());
			}

			urlTitle = prefix + suffix;
		}
	}

	private boolean _isValidUrlTitle(
			long groupId, String articleId, String urlTitle)
		throws Exception {

		try (PreparedStatement preparedStatement = connection.prepareStatement(
				"select count(*) from JournalArticle where groupId = ? and " +
					"articleId != ? and urlTitle = ?")) {

			preparedStatement.setLong(1, groupId);
			preparedStatement.setString(2, articleId);
			preparedStatement.setString(3, urlTitle);

			try (ResultSet resultSet = preparedStatement.executeQuery()) {
				while (resultSet.next()) {
					int count = resultSet.getInt(1);

					if (count > 0) {
						return false;
					}
				}

				return true;
			}
		}
	}

	private void _runSQL(String sql) throws Exception {
		try (LoggingTimer loggingTimer = new LoggingTimer(sql)) {
			runSQL(sql);
		}
	}

	private static final int _DDM_STRUCTURE_TYPE_DEFAULT = 0;

	private static final String _DDM_TEMPLATE_MODE_CREATE = "create";

	private static final String _DDM_TEMPLATE_TYPE_DISPLAY = "display";

	private static final String _PORTLET_ID_ASSET_PUBLISHER = "101";

	private static final String _PORTLET_ID_JOURNAL_CONTENT = "56";

	private static final String _PORTLET_ID_JOURNAL_CONTENT_LIST = "62";

	private static final Log _log = LogFactoryUtil.getLog(UpgradeJournal.class);

	private final Map<String, Long> _classNameIds = new HashMap<>();
	private final Map<Long, Long> _companyGroupIds = new HashMap<>();
	private final Map<String, String> _ddmDataTypes = new HashMap<>();
	private final Map<String, String> _ddmMetadataAttributes = new HashMap<>();
	private final Map<String, Long> _ddmStructureIds = new HashMap<>();
	private final Map<Long, Long> _ddmStructurePKs = new HashMap<>();
	private final Map<String, String> _journalTypesToDDMTypes = new HashMap<>();

}