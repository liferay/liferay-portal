/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.upgrade.v7_4_x;

import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.dao.db.DBType;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.upgrade.BaseExternalReferenceCodeUpgradeProcess;
import com.liferay.portal.kernel.upgrade.BaseUuidUpgradeProcess;
import com.liferay.portal.kernel.upgrade.CTModelUpgradeProcess;
import com.liferay.portal.kernel.upgrade.DBColumnSizeUpgradeProcess;
import com.liferay.portal.kernel.upgrade.DeleteDuplicateUniqueFinderRowsUpgradeProcess;
import com.liferay.portal.kernel.upgrade.DummyUpgradeProcess;
import com.liferay.portal.kernel.upgrade.GuestUnsupportedResourcePermissionsUpgradeProcess;
import com.liferay.portal.kernel.upgrade.UpgradeProcessFactory;
import com.liferay.portal.kernel.upgrade.util.UpgradeModulesFactory;
import com.liferay.portal.kernel.upgrade.util.UpgradeVersionTreeMap;
import com.liferay.portal.kernel.version.Version;
import com.liferay.portal.upgrade.util.PortalUpgradeProcessRegistry;
import com.liferay.portal.upgrade.util.UpgradePartitionedControlTable;
import com.liferay.portal.upgrade.v7_4_x.util.AssetTagGroupRelTable;
import com.liferay.portal.upgrade.v7_4_x.util.AssetVocabularyGroupRelTable;
import com.liferay.portal.upgrade.v7_4_x.util.RememberMeTokenTable;

/**
 * @author Pei-Jung Lan
 */
public class PortalUpgradeProcessRegistryImpl
	implements PortalUpgradeProcessRegistry {

	@Override
	public void registerUpgradeProcesses(
		UpgradeVersionTreeMap upgradeVersionTreeMap) {

		upgradeVersionTreeMap.put(
			new Version(9, 0, 0),
			UpgradeProcessFactory.addColumns(
				"Address", "externalReferenceCode VARCHAR(75) null",
				"description STRING null", "latitude DOUBLE",
				"longitude DOUBLE", "name VARCHAR(255) null",
				"validationDate DATE null", "validationStatus INTEGER"),
			UpgradeProcessFactory.alterColumnType(
				"Address", "street1", "VARCHAR(255) null"),
			UpgradeProcessFactory.alterColumnType(
				"Address", "street2", "VARCHAR(255) null"),
			UpgradeProcessFactory.alterColumnType(
				"Address", "street3", "VARCHAR(255) null"));

		upgradeVersionTreeMap.put(
			new Version(9, 0, 1),
			UpgradeModulesFactory.create(
				new String[] {
					"com.liferay.change.tracking.web",
					"com.liferay.document.library.asset.auto.tagger.tensorflow",
					"com.liferay.portal.bundle.blacklist.impl",
					"com.liferay.portal.component.blacklist.impl",
					"com.liferay.portal.search", "com.liferay.template.web"
				},
				new String[][] {
					{"opensocial-portlet", "opensocial-portlet", "OpenSocial"}
				},
				new String[][] {
					{
						"com.liferay.softwarecatalog.service",
						"SCFrameworkVersion"
					}
				}));

		upgradeVersionTreeMap.put(new Version(9, 1, 0), new UpgradeRegion());

		upgradeVersionTreeMap.put(new Version(9, 2, 0), new UpgradeCountry());

		upgradeVersionTreeMap.put(
			new Version(9, 2, 1, "step-1"), new UpgradeListType());

		upgradeVersionTreeMap.put(
			new Version(9, 2, 1),
			UpgradeModulesFactory.create(
				new String[] {"com.liferay.address.impl"}, null));

		upgradeVersionTreeMap.put(
			new Version(10, 0, 0), new UpgradePortletPreferences());

		upgradeVersionTreeMap.put(
			new Version(11, 0, 0), new UpgradeAssetEntry());

		upgradeVersionTreeMap.put(
			new Version(12, 0, 0), new UpgradePortalPreferences());

		upgradeVersionTreeMap.put(
			new Version(12, 0, 1),
			UpgradeProcessFactory.runSQL(
				"update ResourceAction set actionId = 'MANAGE_COUNTRIES' " +
					"where name='90' and actionId = " +
						"'MANAGE_COMMERCE_COUNTRIES'"));

		upgradeVersionTreeMap.put(
			new Version(12, 0, 2), new UpgradeDLFileEntryType());

		upgradeVersionTreeMap.put(
			new Version(12, 1, 0), new UpgradeDLFileEntry());

		upgradeVersionTreeMap.put(
			new Version(12, 1, 1),
			UpgradeProcessFactory.addColumns(
				"DLFileVersion", "expirationDate DATE null",
				"reviewDate DATE null"));

		upgradeVersionTreeMap.put(
			new Version(12, 2, 0), new UpgradeCompanyId());

		upgradeVersionTreeMap.put(
			new Version(12, 2, 1),
			UpgradeProcessFactory.alterColumnType(
				"AssetEntry", "title", "TEXT null"));

		upgradeVersionTreeMap.put(
			new Version(12, 2, 2),
			UpgradeProcessFactory.runSQL(
				StringBundler.concat(
					"update PortalPreferenceValue set namespace = ",
					"'com.liferay.portal.kernel.util.SessionTreeJSClicks' ",
					"where namespace = ",
					"'com.liferay.taglib.ui.util.SessionTreeJSClicks'")));

		upgradeVersionTreeMap.put(new Version(13, 0, 0), new UpgradeAccount());

		upgradeVersionTreeMap.put(new Version(13, 0, 1), new UpgradeLayout());

		upgradeVersionTreeMap.put(
			new Version(13, 1, 0), new UpgradeAssetVocabulary());

		upgradeVersionTreeMap.put(
			new Version(13, 2, 0), new UpgradeAssetCategory());

		upgradeVersionTreeMap.put(
			new Version(13, 3, 0),
			new CTModelUpgradeProcess("Repository", "RepositoryEntry"));

		upgradeVersionTreeMap.put(
			new Version(13, 3, 1),
			UpgradeProcessFactory.runSQL(
				"update Repository set portletId = name where (portletId is " +
					"null or portletId = '') and name = " +
						"'com.liferay.portal.kernel.util.TempFileEntryUtil'"));

		upgradeVersionTreeMap.put(
			new Version(13, 3, 2), new UpgradeMappingTables());

		upgradeVersionTreeMap.put(new Version(13, 3, 3), new UpgradeGroup());

		upgradeVersionTreeMap.put(
			new Version(13, 3, 4), new UpgradeExpandoColumn());

		upgradeVersionTreeMap.put(
			new Version(13, 3, 5),
			UpgradeProcessFactory.alterColumnType(
				"Contact_", "prefixId", "LONG NULL"));

		upgradeVersionTreeMap.put(
			new Version(13, 3, 6),
			UpgradeProcessFactory.alterColumnType(
				"Contact_", "suffixId", "LONG NULL"));

		upgradeVersionTreeMap.put(
			new Version(14, 0, 0), new UpgradeExternalReferenceCode());

		upgradeVersionTreeMap.put(
			new Version(14, 0, 1), new UpgradeFaviconFileEntryIdColumn());

		upgradeVersionTreeMap.put(
			new Version(14, 0, 2), new UpgradeCountryCode());

		upgradeVersionTreeMap.put(
			new Version(15, 0, 0),
			UpgradeProcessFactory.dropTables("OrgGroupRole"));

		upgradeVersionTreeMap.put(
			new Version(16, 0, 0), new DummyUpgradeProcess());

		upgradeVersionTreeMap.put(
			new Version(16, 1, 0),
			new BaseExternalReferenceCodeUpgradeProcess() {

				@Override
				protected String[][] getTableAndPrimaryKeyColumnNames() {
					return new String[][] {{"DLFolder", "folderId"}};
				}

			});

		upgradeVersionTreeMap.put(
			new Version(16, 1, 1),
			UpgradeProcessFactory.runSQL(
				"update Group_ set type_ = 3 where type_ = 4"));

		upgradeVersionTreeMap.put(
			new Version(16, 1, 2),
			UpgradeModulesFactory.create(
				new String[] {
					"com.liferay.analytics.settings.web",
					"com.liferay.client.extension.web",
					"com.liferay.commerce.image.service",
					"com.liferay.commerce.product.content.web",
					"com.liferay.document.library.google.docs",
					"com.liferay.dynamic.data.mapping.form.web",
					"com.liferay.image.impl",
					"com.liferay.journal.image.service",
					"com.liferay.message.boards.moderation",
					"com.liferay.monitoring.web",
					"com.liferay.portal.configuration.persistence.impl",
					"com.liferay.portal.search.elasticsearch.monitoring.web",
					"com.liferay.portal.security.ldap.impl",
					"com.liferay.portal.security.sso.token.impl",
					"com.liferay.push.notifications.web",
					"com.liferay.saml.addon.keep.alive.web",
					"com.liferay.saml.impl", "com.liferay.saml.web",
					"com.liferay.segments.content.targeting.upgrade"
				},
				null));

		upgradeVersionTreeMap.put(
			new Version(17, 0, 0),
			UpgradeProcessFactory.dropColumns("Company", "system_"));

		upgradeVersionTreeMap.put(
			new Version(18, 0, 0),
			UpgradeProcessFactory.alterColumnName(
				"Address", "typeId", "listTypeId LONG"));

		upgradeVersionTreeMap.put(
			new Version(18, 0, 1), new UpgradeVirtualHost());

		upgradeVersionTreeMap.put(
			new Version(19, 0, 0),
			UpgradeProcessFactory.alterColumnName(
				"Phone", "typeId", "listTypeId LONG"));

		upgradeVersionTreeMap.put(
			new Version(20, 0, 0),
			UpgradeProcessFactory.alterColumnName(
				"EmailAddress", "typeId", "listTypeId LONG"));

		upgradeVersionTreeMap.put(
			new Version(21, 0, 0),
			UpgradeProcessFactory.alterColumnName(
				"Website", "typeId", "listTypeId LONG"));

		upgradeVersionTreeMap.put(
			new Version(22, 0, 0),
			UpgradeProcessFactory.alterColumnName(
				"Organization_", "statusId", "statusListTypeId LONG"));

		upgradeVersionTreeMap.put(
			new Version(23, 0, 0),
			UpgradeProcessFactory.alterColumnName(
				"Contact_", "prefixId", "prefixListTypeId LONG"));

		upgradeVersionTreeMap.put(
			new Version(24, 0, 0),
			UpgradeProcessFactory.alterColumnName(
				"Contact_", "suffixId", "suffixListTypeId LONG"));

		upgradeVersionTreeMap.put(
			new Version(25, 0, 0),
			UpgradeProcessFactory.alterColumnName(
				"OrgLabor", "typeId", "listTypeId LONG"));

		upgradeVersionTreeMap.put(
			new Version(25, 0, 1),
			UpgradeProcessFactory.alterColumnType(
				"Role_", "description", "TEXT null"));

		upgradeVersionTreeMap.put(
			new Version(25, 1, 0), new CTModelUpgradeProcess("EmailAddress"));

		upgradeVersionTreeMap.put(
			new Version(25, 1, 1),
			UpgradeModulesFactory.create(
				new String[] {"com.liferay.questions.web"}, null));

		upgradeVersionTreeMap.put(
			new Version(25, 1, 2), new DummyUpgradeProcess());

		upgradeVersionTreeMap.put(
			new Version(25, 2, 0),
			new CTModelUpgradeProcess("LayoutPrototype"));

		upgradeVersionTreeMap.put(
			new Version(25, 3, 0),
			UpgradeProcessFactory.addColumns(
				"DLFileVersion", "storeUUID VARCHAR(255) null"));

		upgradeVersionTreeMap.put(
			new Version(25, 3, 1, "step-1"),
			UpgradeProcessFactory.alterColumnType(
				"UserGroupGroupRole", "userGroupGroupRoleId", "LONG not null"),
			UpgradeProcessFactory.alterColumnType(
				"UserGroupGroupRole", "userGroupId", "LONG null"),
			UpgradeProcessFactory.alterColumnType(
				"UserGroupGroupRole", "groupId", "LONG null"),
			UpgradeProcessFactory.alterColumnType(
				"UserGroupGroupRole", "roleId", "LONG null"),
			//
			UpgradeProcessFactory.alterColumnType(
				"UserGroupRole", "userGroupRoleId", "LONG not null"),
			UpgradeProcessFactory.alterColumnType(
				"UserGroupRole", "userId", "LONG null"),
			UpgradeProcessFactory.alterColumnType(
				"UserGroupRole", "groupId", "LONG null"),
			UpgradeProcessFactory.alterColumnType(
				"UserGroupRole", "roleId", "LONG null"));

		upgradeVersionTreeMap.put(
			new Version(25, 3, 1), new UpgradeUsersUserGroups());

		upgradeVersionTreeMap.put(new Version(26, 0, 0), new UpgradeUserType());

		upgradeVersionTreeMap.put(
			new Version(26, 1, 0),
			UpgradeProcessFactory.addColumns(
				"Company", "indexNameCurrent VARCHAR(75)",
				"indexNameNext VARCHAR(75)"));

		upgradeVersionTreeMap.put(
			new Version(26, 2, 0),
			new CTModelUpgradeProcess("Address", "Phone"));

		upgradeVersionTreeMap.put(
			new Version(26, 3, 0),
			new CTModelUpgradeProcess(
				"AnnouncementsEntry", "AnnouncementsFlag"));

		upgradeVersionTreeMap.put(
			new Version(26, 4, 0),
			new BaseExternalReferenceCodeUpgradeProcess() {

				@Override
				protected String[][] getTableAndPrimaryKeyColumnNames() {
					return new String[][] {{"Group_", "groupId"}};
				}

			});

		upgradeVersionTreeMap.put(
			new Version(26, 5, 0),
			UpgradeModulesFactory.create(
				new String[] {"com.liferay.asset.link.service"}, null));

		upgradeVersionTreeMap.put(
			new Version(27, 0, 0, "step-1"),
			new UpgradePartitionedControlTable("ClassName_"));

		upgradeVersionTreeMap.put(
			new Version(27, 0, 0),
			UpgradeModulesFactory.create(
				new String[] {"com.liferay.comment.web"}, null));

		upgradeVersionTreeMap.put(
			new Version(28, 0, 0),
			new UpgradePartitionedControlTable("ResourceAction"));

		upgradeVersionTreeMap.put(
			new Version(28, 0, 1),
			new GuestUnsupportedResourcePermissionsUpgradeProcess(
				Group.class.getName(), ActionKeys.CONFIGURE_PORTLETS,
				ActionKeys.VIEW_SITE_ADMINISTRATION));

		upgradeVersionTreeMap.put(
			new Version(28, 0, 2),
			UpgradeModulesFactory.create(
				new String[] {"com.liferay.user.associated.data.web"}, null));

		upgradeVersionTreeMap.put(
			new Version(29, 0, 0), new UpgradeListTypeCompanyId());

		upgradeVersionTreeMap.put(
			new Version(29, 1, 0),
			new CTModelUpgradeProcess(
				"Country", "CountryLocalization", "Region",
				"RegionLocalization"));

		upgradeVersionTreeMap.put(
			new Version(29, 1, 1), new DummyUpgradeProcess());

		upgradeVersionTreeMap.put(
			new Version(29, 1, 2), new UpgradeListTypeType());

		upgradeVersionTreeMap.put(
			new Version(29, 2, 0),
			UpgradeProcessFactory.addColumns(
				"DLFileEntry", "displayDate DATE null"),
			UpgradeProcessFactory.addColumns(
				"DLFileVersion", "displayDate DATE null"));

		upgradeVersionTreeMap.put(
			new Version(29, 2, 1),
			UpgradeModulesFactory.create(
				new String[] {"com.liferay.portal.search.tuning.rankings.web"},
				null));

		upgradeVersionTreeMap.put(
			new Version(29, 2, 2),
			UpgradeModulesFactory.create(
				new String[] {"com.liferay.headless.builder.impl"}, null));

		upgradeVersionTreeMap.put(
			new Version(29, 3, 0),
			new CTModelUpgradeProcess("AnnouncementsDelivery"));

		upgradeVersionTreeMap.put(
			new Version(30, 0, 0),
			new UpgradePartitionedControlTable("Counter"));

		upgradeVersionTreeMap.put(
			new Version(30, 0, 1),
			UpgradeProcessFactory.alterColumnType(
				"PortalPreferenceValue", "key_", "VARCHAR(1024) null"));

		upgradeVersionTreeMap.put(new Version(30, 1, 1), new UpgradeRelease());

		upgradeVersionTreeMap.put(
			new Version(31, 0, 0), new UpgradeListTypeAuditFields());

		upgradeVersionTreeMap.put(
			new Version(31, 1, 0),
			UpgradeProcessFactory.alterColumnType(
				"PasswordTracker", "password_", "VARCHAR(255) null"),
			//
			UpgradeProcessFactory.alterColumnType(
				"Ticket", "key_", "VARCHAR(255) null"),
			//
			UpgradeProcessFactory.alterColumnType(
				"User_", "password_", "VARCHAR(255) null"));

		upgradeVersionTreeMap.put(
			new Version(31, 1, 1), new UpgradePortletPreferencesCompanyId());

		upgradeVersionTreeMap.put(
			new Version(31, 2, 0),
			new LayoutExternalReferenceCodeUpgradeProcess());

		upgradeVersionTreeMap.put(
			new Version(31, 3, 0), RememberMeTokenTable.create());

		upgradeVersionTreeMap.put(
			new Version(31, 4, 0),
			new RoleExternalReferenceCodeUpgradeProcess());

		upgradeVersionTreeMap.put(
			new Version(31, 5, 0),
			new BaseExternalReferenceCodeUpgradeProcess() {

				@Override
				protected String[][] getTableAndPrimaryKeyColumnNames() {
					return new String[][] {
						{"DLFileEntryType", "fileEntryTypeId"}
					};
				}

			});

		upgradeVersionTreeMap.put(
			new Version(31, 6, 0),
			new DLFileShortcutExternalReferenceCodeUpgradeProcess());

		upgradeVersionTreeMap.put(
			new Version(31, 7, 0),
			new BaseExternalReferenceCodeUpgradeProcess() {

				@Override
				protected String[][] getTableAndPrimaryKeyColumnNames() {
					return new String[][] {
						{"DLFileEntryMetadata", "fileEntryMetadataId"}
					};
				}

			});

		upgradeVersionTreeMap.put(
			new Version(31, 8, 0),
			new EmailAddressExternalReferenceCodeUpgradeProcess());

		upgradeVersionTreeMap.put(
			new Version(31, 9, 0),
			new BaseExternalReferenceCodeUpgradeProcess() {

				@Override
				protected String[][] getTableAndPrimaryKeyColumnNames() {
					return new String[][] {{"Website", "websiteId"}};
				}

			});

		upgradeVersionTreeMap.put(
			new Version(31, 10, 0),
			new BaseExternalReferenceCodeUpgradeProcess() {

				@Override
				protected String[][] getTableAndPrimaryKeyColumnNames() {
					return new String[][] {{"Repository", "repositoryId"}};
				}

			});

		upgradeVersionTreeMap.put(
			new Version(31, 11, 0),
			new BaseExternalReferenceCodeUpgradeProcess() {

				@Override
				protected String[][] getTableAndPrimaryKeyColumnNames() {
					return new String[][] {{"Phone", "phoneId"}};
				}

			});

		upgradeVersionTreeMap.put(
			new Version(31, 12, 0),
			new BaseExternalReferenceCodeUpgradeProcess() {

				@Override
				protected String[][] getTableAndPrimaryKeyColumnNames() {
					return new String[][] {{"AssetTag", "tagId"}};
				}

			});

		upgradeVersionTreeMap.put(
			new Version(31, 12, 1),
			UpgradeModulesFactory.create(
				new String[] {"com.liferay.feature.flag.web"}, null));

		upgradeVersionTreeMap.put(
			new Version(31, 13, 0),
			new BaseUuidUpgradeProcess() {

				@Override
				protected String[][] getTableAndPrimaryKeyColumnNames() {
					return new String[][] {
						{"WorkflowDefinitionLink", "workflowDefinitionLinkId"}
					};
				}

			});

		upgradeVersionTreeMap.put(
			new Version(31, 14, 0),
			new BaseExternalReferenceCodeUpgradeProcess() {

				@Override
				protected String[][] getTableAndPrimaryKeyColumnNames() {
					return new String[][] {
						{"WorkflowDefinitionLink", "workflowDefinitionLinkId"}
					};
				}

			});

		upgradeVersionTreeMap.put(
			new Version(31, 14, 1),
			UpgradeProcessFactory.dropColumns("Company", "system_"));

		upgradeVersionTreeMap.put(
			new Version(31, 14, 2),
			UpgradeProcessFactory.alterColumnType(
				"AssetVocabulary", "visibilityType", "INTEGER"));

		upgradeVersionTreeMap.put(
			new Version(31, 15, 0),
			UpgradeProcessFactory.addColumns(
				"SystemEvent", "classExternalReferenceCode VARCHAR(75) null"));

		upgradeVersionTreeMap.put(
			new Version(31, 15, 1),
			new DBColumnSizeUpgradeProcess(
				DBType.ORACLE, "number", 30, 20, "DOUBLE"));

		upgradeVersionTreeMap.put(
			new Version(31, 15, 2),
			UpgradeProcessFactory.alterColumnType(
				"SystemEvent", "classExternalReferenceCode",
				"VARCHAR(1000) null"));

		upgradeVersionTreeMap.put(
			new Version(31, 15, 3),
			UpgradeProcessFactory.dropColumns("Contact_", "accountId"));

		upgradeVersionTreeMap.put(
			new Version(31, 16, 0), AssetVocabularyGroupRelTable.create());

		upgradeVersionTreeMap.put(
			new Version(31, 17, 0),
			UpgradeProcessFactory.addColumns("Role_", "status INTEGER"),
			UpgradeProcessFactory.runSQL("update Role_ set status = 0"));

		upgradeVersionTreeMap.put(
			new Version(31, 18, 0), AssetTagGroupRelTable.create());

		upgradeVersionTreeMap.put(
			new Version(31, 19, 0),
			UpgradeProcessFactory.addColumns(
				"Address", "subtype VARCHAR(75) null"));

		upgradeVersionTreeMap.put(
			new Version(32, 0, 0),
			new DeleteDuplicateUniqueFinderRowsUpgradeProcess(
				"PortalPreferences", new String[] {"ownerType", "ownerId"},
				"portalPreferencesId asc"),
			new DeleteDuplicateUniqueFinderRowsUpgradeProcess(
				"PortletItem",
				new String[] {"groupId", "classNameId", "portletId", "name"},
				"portletItemId asc"),
			new DeleteDuplicateUniqueFinderRowsUpgradeProcess(
				"SocialActivitySetting",
				new String[] {"groupId", "classNameId", "activityType", "name"},
				"activitySettingId asc"),
			new DeleteDuplicateUniqueFinderRowsUpgradeProcess(
				"Ticket", new String[] {"key_"}, "ticketId asc"));

		upgradeVersionTreeMap.put(
			new Version(32, 2, 0),
			UpgradeProcessFactory.addColumns("AssetCategory", "status INTEGER"),
			UpgradeProcessFactory.runSQL("update AssetCategory set status = 0"),
			UpgradeProcessFactory.addColumns(
				"AssetVocabulary", "status INTEGER"),
			UpgradeProcessFactory.runSQL(
				"update AssetVocabulary set status = 0"));

		upgradeVersionTreeMap.put(
			new Version(32, 3, 0),
			UpgradeProcessFactory.addColumns("Organization_", "status INTEGER"),
			UpgradeProcessFactory.runSQL(
				"update Organization_ set status = 0"));

		upgradeVersionTreeMap.put(
			new Version(32, 4, 0),
			UpgradeProcessFactory.addColumns("Address", "status INTEGER"),
			UpgradeProcessFactory.runSQL("update Address set status = 0"));

		upgradeVersionTreeMap.put(
			new Version(32, 4, 1), new UpgradePortletPreferenceValueCounter());

		upgradeVersionTreeMap.put(
			new Version(32, 4, 2), new UpgradeServiceComponent());
	}

}