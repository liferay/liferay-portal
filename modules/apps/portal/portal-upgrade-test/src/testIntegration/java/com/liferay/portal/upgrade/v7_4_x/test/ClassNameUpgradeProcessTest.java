/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.upgrade.v7_4_x.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.document.library.kernel.processor.RawMetadataProcessor;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.cache.MultiVMPool;
import com.liferay.portal.kernel.dao.jdbc.DataAccess;
import com.liferay.portal.kernel.dao.orm.EntityCache;
import com.liferay.portal.kernel.instance.PortalInstancePool;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.upgrade.v7_4_x.ClassNameUpgradeProcess;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Marco Galluzzi
 */
@RunWith(Arquillian.class)
public class ClassNameUpgradeProcessTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@BeforeClass
	public static void setUpClass() throws Exception {
		_connection = DataAccess.getConnection();

		_companyId = _addCompany();

		_originalCacheEnabled = ReflectionTestUtil.getAndSetFieldValue(
			PortalInstancePool.class, "_cacheEnabled", false);
	}

	@AfterClass
	public static void tearDownClass() throws Exception {
		ReflectionTestUtil.setFieldValue(
			PortalInstancePool.class, "_cacheEnabled", _originalCacheEnabled);

		_delete("Company");

		DataAccess.cleanUp(_connection);
	}

	@After
	public void tearDown() throws Exception {
		_delete(
			"DLFileEntryMetadata", "DDMField", "DDMStorageLink", "DDMStructure",
			"DDMStructureLayout", "DDMStructureLink", "DDMStructureVersion");
		_deleteClassName();
	}

	@Test
	public void testDeleteAndUpdateDDMStructure() throws Exception {
		long newClassNameId = _getClassNameId(_NEW_CLASS_NAME);

		long newStructureId = _addDDMStructure(newClassNameId);

		long newStructureVersionId = _addDDMStructureRelatedTables(
			newStructureId);

		long oldClassNameId = _addClassName(_OLD_CLASS_NAME);

		long oldStructureId = _addDDMStructure(oldClassNameId);

		_addDLFileEntryMetadata(oldStructureId);

		runUpgrade();

		Assert.assertEquals(0, _getDDMStructureClassNameId(newStructureId));
		Assert.assertEquals(
			newClassNameId, _getDDMStructureClassNameId(oldStructureId));
		Assert.assertEquals(
			0,
			_count(
				"DDMStructureLayout", "structureVersionId",
				newStructureVersionId));
	}

	@Test
	public void testDeleteClassName() throws Exception {
		_addClassName(_OLD_CLASS_NAME);

		runUpgrade();

		Assert.assertEquals(0, _getClassNameId(_OLD_CLASS_NAME));
	}

	@Test
	public void testDeleteDDMStructure() throws Exception {
		long newClassNameId = _getClassNameId(_NEW_CLASS_NAME);

		long newStructureId = _addDDMStructure(newClassNameId);

		long oldClassNameId = _addClassName(_OLD_CLASS_NAME);

		long oldStructureId = _addDDMStructure(oldClassNameId);

		long oldStructureVersionId = _addDDMStructureRelatedTables(
			oldStructureId);

		runUpgrade();

		Assert.assertEquals(
			newClassNameId, _getDDMStructureClassNameId(newStructureId));
		Assert.assertEquals(0, _getDDMStructureClassNameId(oldStructureId));
		Assert.assertEquals(
			0,
			_count(
				"DDMStructureLayout", "structureVersionId",
				oldStructureVersionId));
	}

	@Test
	public void testUpdateClassName() throws Exception {
		long classNameId = _getClassNameId(_NEW_CLASS_NAME);

		Assert.assertNotEquals(0, classNameId);

		_updateClassNameValue(classNameId, _OLD_CLASS_NAME);

		try {
			runUpgrade();

			Assert.assertEquals(
				_NEW_CLASS_NAME, _getClassNameValue(classNameId));
		}
		finally {
			_updateClassNameValue(classNameId, _NEW_CLASS_NAME);
		}
	}

	@Test
	public void testUpdateDDMStructure() throws Exception {
		long classNameId = _addClassName(_OLD_CLASS_NAME);

		long structureId = _addDDMStructure(classNameId);

		runUpgrade();

		Assert.assertEquals(
			_getClassNameId(_NEW_CLASS_NAME),
			_getDDMStructureClassNameId(structureId));
	}

	@Test
	public void testUpdateDDMStructureRelatedTables1() throws Exception {
		long newClassNameId = _getClassNameId(_NEW_CLASS_NAME);

		long newStructureId = _addDDMStructure(newClassNameId);

		_addDLFileEntryMetadata(newStructureId);
		_addDLFileEntryMetadata(newStructureId);

		long newStructureVersionId = _addDDMStructureRelatedTables(
			newStructureId);

		long oldClassNameId = _addClassName(_OLD_CLASS_NAME);

		long oldStructureId = _addDDMStructure(oldClassNameId);

		_addDLFileEntryMetadata(oldStructureId);

		long oldStructureVersionId = _addDDMStructureRelatedTables(
			oldStructureId);

		runUpgrade();

		Assert.assertEquals(
			newClassNameId, _getDDMStructureClassNameId(newStructureId));
		Assert.assertEquals(0, _getDDMStructureClassNameId(oldStructureId));
		Assert.assertEquals(
			12,
			_getDDMStructureRelatedTablesCount(
				newStructureId, newStructureVersionId));
		Assert.assertEquals(
			0,
			_getDDMStructureRelatedTablesCount(
				oldStructureId, oldStructureVersionId));
	}

	@Test
	public void testUpdateDDMStructureRelatedTables2() throws Exception {
		long newClassNameId = _getClassNameId(_NEW_CLASS_NAME);

		long newStructureId = _addDDMStructure(newClassNameId);

		_addDLFileEntryMetadata(newStructureId);

		long newStructureVersionId = _addDDMStructureRelatedTables(
			newStructureId);

		long oldClassNameId = _addClassName(_OLD_CLASS_NAME);

		long oldStructureId = _addDDMStructure(oldClassNameId);

		_addDLFileEntryMetadata(oldStructureId);
		_addDLFileEntryMetadata(oldStructureId);

		long oldStructureVersionId = _addDDMStructureRelatedTables(
			oldStructureId);

		runUpgrade();

		Assert.assertEquals(0, _getDDMStructureClassNameId(newStructureId));
		Assert.assertEquals(
			newClassNameId, _getDDMStructureClassNameId(oldStructureId));
		Assert.assertEquals(
			0,
			_getDDMStructureRelatedTablesCount(
				newStructureId, newStructureVersionId));
		Assert.assertEquals(
			12,
			_getDDMStructureRelatedTablesCount(
				oldStructureId, oldStructureVersionId));
	}

	@Test
	public void testUpdateDDMStructureRelatedTables3() throws Exception {
		long newClassNameId = _getClassNameId(_NEW_CLASS_NAME);

		long newStructureId = _addDDMStructure(newClassNameId);

		_addDLFileEntryMetadata(newStructureId);
		_addDLFileEntryMetadata(newStructureId);

		long fileVersionId = RandomTestUtil.randomLong();

		_addDLFileEntryMetadata(fileVersionId, newStructureId);

		long newStructureVersionId = _addDDMStructureRelatedTables(
			newStructureId);

		long oldClassNameId = _addClassName(_OLD_CLASS_NAME);

		long oldStructureId = _addDDMStructure(oldClassNameId);

		_addDLFileEntryMetadata(oldStructureId);
		_addDLFileEntryMetadata(fileVersionId, oldStructureId);

		long oldStructureVersionId = _addDDMStructureRelatedTables(
			oldStructureId);

		runUpgrade();

		Assert.assertEquals(
			newClassNameId, _getDDMStructureClassNameId(newStructureId));
		Assert.assertEquals(0, _getDDMStructureClassNameId(oldStructureId));
		Assert.assertEquals(
			13,
			_getDDMStructureRelatedTablesCount(
				newStructureId, newStructureVersionId));
		Assert.assertEquals(
			0,
			_getDDMStructureRelatedTablesCount(
				oldStructureId, oldStructureVersionId));
	}

	@Test
	public void testUpdateDDMStructureRelatedTables4() throws Exception {
		long newClassNameId = _getClassNameId(_NEW_CLASS_NAME);

		long newStructureId = _addDDMStructure(newClassNameId);

		_addDLFileEntryMetadata(newStructureId);

		long fileVersionId = RandomTestUtil.randomLong();

		_addDLFileEntryMetadata(fileVersionId, newStructureId);

		long newStructureVersionId = _addDDMStructureRelatedTables(
			newStructureId);

		long oldClassNameId = _addClassName(_OLD_CLASS_NAME);

		long oldStructureId = _addDDMStructure(oldClassNameId);

		_addDLFileEntryMetadata(oldStructureId);
		_addDLFileEntryMetadata(oldStructureId);
		_addDLFileEntryMetadata(fileVersionId, oldStructureId);

		long oldStructureVersionId = _addDDMStructureRelatedTables(
			oldStructureId);

		runUpgrade();

		Assert.assertEquals(0, _getDDMStructureClassNameId(newStructureId));
		Assert.assertEquals(
			newClassNameId, _getDDMStructureClassNameId(oldStructureId));
		Assert.assertEquals(
			0,
			_getDDMStructureRelatedTablesCount(
				newStructureId, newStructureVersionId));
		Assert.assertEquals(
			13,
			_getDDMStructureRelatedTablesCount(
				oldStructureId, oldStructureVersionId));
	}

	protected void runUpgrade() throws Exception {
		UpgradeProcess upgradeProcess = new ClassNameUpgradeProcess();

		upgradeProcess.upgrade();

		_entityCache.clearCache();
		_multiVMPool.clear();
	}

	private static long _addCompany() throws Exception {
		long companyId = RandomTestUtil.nextLong();

		try (PreparedStatement preparedStatement = _connection.prepareStatement(
				"insert into Company (mvccVersion, companyId) values (?, ?)")) {

			preparedStatement.setLong(1, 0);
			preparedStatement.setLong(2, companyId);

			preparedStatement.executeUpdate();
		}

		return companyId;
	}

	private static void _delete(String... tables) throws Exception {
		for (String table : tables) {
			try (PreparedStatement preparedStatement =
					_connection.prepareStatement(
						"delete from " + table + " where companyId = ?")) {

				preparedStatement.setLong(1, _companyId);

				preparedStatement.execute();
			}
		}
	}

	private long _addClassName(String className) throws Exception {
		long classNameId = RandomTestUtil.nextLong();

		try (PreparedStatement preparedStatement = _connection.prepareStatement(
				"insert into ClassName_ (mvccVersion, classNameId, value) " +
					"values (?, ?, ?)")) {

			preparedStatement.setLong(1, 0);
			preparedStatement.setLong(2, classNameId);
			preparedStatement.setString(3, className);

			preparedStatement.executeUpdate();
		}

		return classNameId;
	}

	private long _addDDMStructure(long classNameId) throws Exception {
		long structureId = RandomTestUtil.nextLong();

		try (PreparedStatement preparedStatement = _connection.prepareStatement(
				"insert into DDMStructure (mvccVersion, ctCollectionId, " +
					"uuid_, structureId, companyId, classNameId, " +
						"structureKey) values (?, ?, ?, ?, ?, ?, ?)")) {

			preparedStatement.setLong(1, 0);
			preparedStatement.setLong(2, 0);
			preparedStatement.setString(3, RandomTestUtil.randomString());
			preparedStatement.setLong(4, structureId);
			preparedStatement.setLong(5, _companyId);
			preparedStatement.setLong(6, classNameId);
			preparedStatement.setString(7, RandomTestUtil.randomString());

			preparedStatement.executeUpdate();
		}

		return structureId;
	}

	private long _addDDMStructureRelatedTables(long structureId)
		throws Exception {

		try (PreparedStatement preparedStatement = _connection.prepareStatement(
				"insert into DDMStructureLink (mvccVersion, ctCollectionId, " +
					"structureLinkId, companyId, classPK, structureId) " +
						"values (?, ?, ?, ?, ?, ?)")) {

			preparedStatement.setLong(1, 0);
			preparedStatement.setLong(2, 0);
			preparedStatement.setLong(3, RandomTestUtil.nextLong());
			preparedStatement.setLong(4, _companyId);
			preparedStatement.setLong(5, RandomTestUtil.randomLong());
			preparedStatement.setLong(6, structureId);

			preparedStatement.executeUpdate();
		}

		long structureVersionId = RandomTestUtil.nextLong();

		try (PreparedStatement preparedStatement = _connection.prepareStatement(
				"insert into DDMStructureVersion (mvccVersion, " +
					"ctCollectionId, structureVersionId, companyId, " +
						"structureId) values (?, ?, ?, ?, ?)")) {

			preparedStatement.setLong(1, 0);
			preparedStatement.setLong(2, 0);
			preparedStatement.setLong(3, structureVersionId);
			preparedStatement.setLong(4, _companyId);
			preparedStatement.setLong(5, structureId);

			preparedStatement.executeUpdate();
		}

		try (PreparedStatement preparedStatement = _connection.prepareStatement(
				"insert into DDMStorageLink (mvccVersion, ctCollectionId, " +
					"storageLinkId, companyId, classPK, structureId, " +
						"structureVersionId) values (?, ?, ?, ?, ?, ?, ?)")) {

			preparedStatement.setLong(1, 0);
			preparedStatement.setLong(2, 0);
			preparedStatement.setLong(3, RandomTestUtil.nextLong());
			preparedStatement.setLong(4, _companyId);
			preparedStatement.setLong(5, RandomTestUtil.randomLong());
			preparedStatement.setLong(6, structureId);
			preparedStatement.setLong(7, structureVersionId);

			preparedStatement.executeUpdate();
		}

		try (PreparedStatement preparedStatement = _connection.prepareStatement(
				"insert into DDMField (mvccVersion, ctCollectionId, fieldId, " +
					"companyId, storageId, structureVersionId) values (?, ?, " +
						"?, ?, ?, ?)")) {

			preparedStatement.setLong(1, 0);
			preparedStatement.setLong(2, 0);
			preparedStatement.setLong(3, RandomTestUtil.nextLong());
			preparedStatement.setLong(4, _companyId);
			preparedStatement.setLong(5, RandomTestUtil.randomLong());
			preparedStatement.setLong(6, structureVersionId);

			preparedStatement.executeUpdate();
		}

		try (PreparedStatement preparedStatement = _connection.prepareStatement(
				StringBundler.concat(
					"insert into DDMStructureLayout (mvccVersion, ",
					"ctCollectionId, uuid_, structureLayoutId, companyId, ",
					"structureLayoutKey, structureVersionId) values (?, ?, ?, ",
					"?, ?, ?, ?)"))) {

			preparedStatement.setLong(1, 0);
			preparedStatement.setLong(2, 0);
			preparedStatement.setString(3, RandomTestUtil.randomString());
			preparedStatement.setLong(4, RandomTestUtil.nextLong());
			preparedStatement.setLong(5, _companyId);
			preparedStatement.setString(6, RandomTestUtil.randomString());
			preparedStatement.setLong(7, structureVersionId);

			preparedStatement.executeUpdate();
		}

		return structureVersionId;
	}

	private long _addDLFileEntryMetadata(long structureId) throws Exception {
		return _addDLFileEntryMetadata(
			RandomTestUtil.randomLong(), structureId);
	}

	private long _addDLFileEntryMetadata(long fileVersionId, long structureId)
		throws Exception {

		long fileEntryMetadataId = RandomTestUtil.nextLong();

		try (PreparedStatement preparedStatement = _connection.prepareStatement(
				StringBundler.concat(
					"insert into DLFileEntryMetadata (mvccVersion, ",
					"ctCollectionId, fileEntryMetadataId, companyId, ",
					"DDMStructureId, fileVersionId, externalReferenceCode) ",
					"values (?, ?, ?, ?, ?, ?, ?)"))) {

			preparedStatement.setLong(1, 0);
			preparedStatement.setLong(2, 0);
			preparedStatement.setLong(3, fileEntryMetadataId);
			preparedStatement.setLong(4, _companyId);
			preparedStatement.setLong(5, structureId);
			preparedStatement.setLong(6, fileVersionId);
			preparedStatement.setString(7, RandomTestUtil.randomString());

			preparedStatement.executeUpdate();
		}

		return fileEntryMetadataId;
	}

	private long _count(String table, String column, long structureId)
		throws Exception {

		try (PreparedStatement preparedStatement = _connection.prepareStatement(
				StringBundler.concat(
					"select count(*) as count from ", table, " where ", column,
					" = ?"))) {

			preparedStatement.setLong(1, structureId);

			ResultSet resultSet = preparedStatement.executeQuery();

			if (resultSet.next()) {
				return resultSet.getLong("count");
			}
		}

		return 0;
	}

	private void _deleteClassName() throws Exception {
		try (PreparedStatement preparedStatement = _connection.prepareStatement(
				"delete from ClassName_ where value = ?")) {

			preparedStatement.setString(1, _OLD_CLASS_NAME);

			preparedStatement.execute();
		}
	}

	private long _getClassNameId(String value) throws Exception {
		try (PreparedStatement preparedStatement = _connection.prepareStatement(
				"select classNameId from ClassName_ where value = ?")) {

			preparedStatement.setString(1, value);

			ResultSet resultSet = preparedStatement.executeQuery();

			if (resultSet.next()) {
				return resultSet.getLong("classNameId");
			}
		}

		return 0;
	}

	private String _getClassNameValue(long classNameId) throws Exception {
		try (PreparedStatement preparedStatement = _connection.prepareStatement(
				"select value from ClassName_ where classNameId = ?")) {

			preparedStatement.setLong(1, classNameId);

			ResultSet resultSet = preparedStatement.executeQuery();

			if (resultSet.next()) {
				return resultSet.getString("value");
			}
		}

		return null;
	}

	private long _getDDMStructureClassNameId(long structureId)
		throws Exception {

		try (PreparedStatement preparedStatement = _connection.prepareStatement(
				"select classNameId from DDMStructure where structureId = ?")) {

			preparedStatement.setLong(1, structureId);

			ResultSet resultSet = preparedStatement.executeQuery();

			if (resultSet.next()) {
				return resultSet.getLong("classNameId");
			}
		}

		return 0;
	}

	private long _getDDMStructureRelatedTablesCount(
			long structureId, long structureVersionId)
		throws Exception {

		long dlFileEntryMetadataCount = _count(
			"DLFileEntryMetadata", "DDMStructureId", structureId);
		long ddmFieldCount = _count(
			"DDMField", "structureVersionId", structureVersionId);
		long ddmStorageLinkCount = _count(
			"DDMStorageLink", "structureId", structureId);
		long ddmStructureLayoutCount = _count(
			"DDMStructureLayout", "structureVersionId", structureVersionId);
		long ddmStructureLinkCount = _count(
			"DDMStructureLink", "structureId", structureId);
		long ddmStructureVersionCount = _count(
			"DDMStructureVersion", "structureId", structureId);

		return dlFileEntryMetadataCount + ddmFieldCount + ddmStorageLinkCount +
			ddmStructureLayoutCount + ddmStructureLinkCount +
				ddmStructureVersionCount;
	}

	private void _updateClassNameValue(long classNameId, String value)
		throws Exception {

		try (PreparedStatement preparedStatement = _connection.prepareStatement(
				"update ClassName_ set value = ? where classNameId = ? ")) {

			preparedStatement.setString(1, value);
			preparedStatement.setLong(2, classNameId);

			preparedStatement.executeUpdate();
		}
	}

	private static final String _NEW_CLASS_NAME =
		RawMetadataProcessor.class.getName();

	private static final String _OLD_CLASS_NAME =
		"com.liferay.document.library.kernel.util.RawMetadataProcessor";

	private static long _companyId;
	private static Connection _connection;
	private static boolean _originalCacheEnabled;

	@Inject
	private EntityCache _entityCache;

	@Inject
	private MultiVMPool _multiVMPool;

}