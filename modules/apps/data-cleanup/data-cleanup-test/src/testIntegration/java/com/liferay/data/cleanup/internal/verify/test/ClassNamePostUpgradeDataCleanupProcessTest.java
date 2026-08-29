/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.data.cleanup.internal.verify.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.blogs.model.BlogsEntry;
import com.liferay.dynamic.data.mapping.kernel.DDMStructure;
import com.liferay.journal.model.JournalArticle;
import com.liferay.message.boards.util.MBUtil;
import com.liferay.object.constants.ObjectDefinitionConstants;
import com.liferay.object.constants.ObjectFieldConstants;
import com.liferay.object.field.util.ObjectFieldUtil;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.test.util.ObjectDefinitionTestUtil;
import com.liferay.petra.concurrent.DCLSingleton;
import com.liferay.petra.function.UnsafeConsumer;
import com.liferay.petra.function.UnsafeRunnable;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.model.ClassName;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.module.util.BundleUtil;
import com.liferay.portal.kernel.module.util.SystemBundleUtil;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.service.ClassNameLocalService;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.test.log.LogCapture;
import com.liferay.portal.test.rule.Inject;

import java.sql.Connection;
import java.sql.PreparedStatement;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.Assert;
import org.junit.Assume;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.osgi.framework.Bundle;

/**
 * @author Luis Ortiz
 */
@RunWith(Arquillian.class)
public class ClassNamePostUpgradeDataCleanupProcessTest
	extends BasePostUpgradeDataCleanupProcessTestCase {

	@Test
	public void testCleanUpBuildsPackageNameBundlesMapForNondefaultCompany()
		throws Exception {

		Assume.assumeFalse(CompanyThreadLocal.isDefaultCompany());

		Bundle bundle = BundleUtil.getBundle(
			SystemBundleUtil.getBundleContext(),
			"com.liferay.data.cleanup.impl");

		DCLSingleton<Map<String, List<Bundle>>> dclSingleton =
			ReflectionTestUtil.getFieldValue(
				bundle.loadClass(getPostUpgradeDataCleanupProcessClassName()),
				"_packageNameBundlesMapDCLSingleton");

		dclSingleton.destroy(null);

		test(
			null,
			() -> {
			},
			() -> {
			});

		Map<String, List<Bundle>> packageNameBundlesMap =
			ReflectionTestUtil.getFieldValue(dclSingleton, "_singleton");

		Assert.assertFalse(packageNameBundlesMap.isEmpty());
	}

	@Test
	public void testFoundLayoutClassNameWithDashIsNotDeleted()
		throws Exception {

		AtomicReference<ClassName> classNameAtomicReference =
			new AtomicReference<>();
		String classNameValue =
			Layout.class.getName() + StringPool.DASH +
				RandomTestUtil.randomString();

		test(
			logCapture -> {
				List<String> messages = logCapture.getMessages();

				Assert.assertTrue(messages.toString(), messages.isEmpty());

				ClassName className = _classNameLocalService.fetchClassName(
					classNameValue);

				Assert.assertEquals(classNameValue, className.getValue());
			},
			() -> {
				if (classNameAtomicReference.get() != null) {
					_classNameLocalService.deleteClassName(
						classNameAtomicReference.get());
				}
			},
			() -> classNameAtomicReference.set(
				_classNameLocalService.addClassName(classNameValue)));
	}

	@Test
	public void testFoundLiferayClassNameWithDashIsNotDeleted()
		throws Exception {

		AtomicReference<ClassName> classNameAtomicReference =
			new AtomicReference<>();
		String classNameValue =
			DDMStructure.class.getName() + StringPool.DASH +
				JournalArticle.class.getName();

		test(
			logCapture -> {
				List<String> messages = logCapture.getMessages();

				Assert.assertTrue(messages.toString(), messages.isEmpty());

				ClassName className = _classNameLocalService.fetchClassName(
					classNameValue);

				Assert.assertEquals(classNameValue, className.getValue());
			},
			() -> {
				if (classNameAtomicReference.get() != null) {
					_classNameLocalService.deleteClassName(
						classNameAtomicReference.get());
				}
			},
			() -> classNameAtomicReference.set(
				_classNameLocalService.addClassName(classNameValue)));
	}

	@Test
	public void testFoundLiferayClassNameWithPoundIsNotDeleted()
		throws Exception {

		AtomicReference<ObjectDefinition> objectDefinitionAtomicReference =
			new AtomicReference<>();

		test(
			logCapture -> {
				List<String> messages = logCapture.getMessages();

				Assert.assertTrue(messages.toString(), messages.isEmpty());

				ObjectDefinition objectDefinition =
					objectDefinitionAtomicReference.get();

				ClassName className = _classNameLocalService.fetchClassName(
					objectDefinition.getClassName());

				Assert.assertEquals(
					objectDefinition.getClassName(), className.getValue());
			},
			() -> {
				ObjectDefinition objectDefinition =
					objectDefinitionAtomicReference.get();

				if (objectDefinition != null) {
					_objectDefinitionLocalService.deleteObjectDefinition(
						objectDefinition);

					ClassName className = _classNameLocalService.fetchClassName(
						objectDefinition.getClassName());

					if (className != null) {
						_classNameLocalService.deleteClassName(className);
					}
				}
			},
			() -> {
				ObjectDefinition objectDefinition =
					ObjectDefinitionTestUtil.addCustomObjectDefinition(
						Collections.singletonList(
							ObjectFieldUtil.createObjectField(
								ObjectFieldConstants.BUSINESS_TYPE_TEXT,
								ObjectFieldConstants.DB_TYPE_STRING, true, true,
								null, "First Name", "firstName", true)));

				objectDefinition =
					_objectDefinitionLocalService.publishCustomObjectDefinition(
						TestPropsValues.getUserId(),
						objectDefinition.getObjectDefinitionId());

				objectDefinitionAtomicReference.set(objectDefinition);
			});
	}

	@Test
	public void testFoundSubscriptionClassNameIsNotDeleted() throws Exception {
		AtomicReference<ClassName> classNameAtomicReference =
			new AtomicReference<>();
		String classNameValue = MBUtil.getSubscriptionClassName(
			BlogsEntry.class.getName());

		test(
			logCapture -> {
				List<String> messages = logCapture.getMessages();

				Assert.assertTrue(messages.toString(), messages.isEmpty());

				ClassName className = _classNameLocalService.fetchClassName(
					classNameValue);

				Assert.assertEquals(classNameValue, className.getValue());
			},
			() -> {
				if (classNameAtomicReference.get() != null) {
					_classNameLocalService.deleteClassName(
						classNameAtomicReference.get());
				}
			},
			() -> classNameAtomicReference.set(
				_classNameLocalService.addClassName(classNameValue)));
	}

	@Test
	public void testNonliferayClassNameIsNotDeleted() throws Exception {
		AtomicReference<ClassName> classNameAtomicReference =
			new AtomicReference<>();
		String classNameValue =
			"com.test.not.liferay." + RandomTestUtil.randomString();

		test(
			logCapture -> {
				List<String> messages = logCapture.getMessages();

				Assert.assertTrue(messages.toString(), messages.isEmpty());

				ClassName className = _classNameLocalService.fetchClassName(
					classNameValue);

				Assert.assertEquals(classNameValue, className.getValue());
			},
			() -> {
				if (classNameAtomicReference.get() != null) {
					_classNameLocalService.deleteClassName(
						classNameAtomicReference.get());
				}
			},
			() -> classNameAtomicReference.set(
				_classNameLocalService.addClassName(classNameValue)));
	}

	@Test
	public void testNotFoundLiferayClassNameUnusedIsDeleted() throws Exception {
		AtomicReference<ClassName> classNameAtomicReference =
			new AtomicReference<>();
		String classNameValue =
			"com.liferay.test." + RandomTestUtil.randomString();

		test(
			logCapture -> {
				List<String> messages = logCapture.getMessages();

				Assert.assertTrue(
					messages.toString(),
					messages.contains(
						StringBundler.concat(
							"Table ", dbInspector.normalizeName("ClassName_"),
							", 1 row deleted because \"", classNameValue,
							"\" is not defined in any deployed module and is ",
							"not in use")));

				ClassName className = _classNameLocalService.fetchClassName(
					classNameValue);

				Assert.assertEquals(StringPool.BLANK, className.getValue());
			},
			() -> {
				if (classNameAtomicReference.get() != null) {
					_classNameLocalService.deleteClassName(
						classNameAtomicReference.get());
				}
			},
			() -> classNameAtomicReference.set(
				_classNameLocalService.addClassName(classNameValue)));
	}

	@Test
	public void testNotFoundLiferayClassNameUsedIsNotDeleted()
		throws Exception {

		AtomicReference<ClassName> classNameAtomicReference =
			new AtomicReference<>();
		String classNameValue =
			"com.liferay.test." + RandomTestUtil.randomString();
		long addressId = RandomTestUtil.nextLong();

		test(
			logCapture -> {
				List<String> messages = logCapture.getMessages();

				Assert.assertTrue(
					messages.toString(),
					messages.contains(
						StringBundler.concat(
							"Class name ", classNameValue,
							" is not defined in any deployed module but is ",
							"referenced in the next tables: ",
							dbInspector.normalizeName(_TABLE_NAME))));

				ClassName className = _classNameLocalService.fetchClassName(
					classNameValue);

				Assert.assertEquals(classNameValue, className.getValue());
			},
			() -> {
				if (classNameAtomicReference.get() != null) {
					_classNameLocalService.deleteClassName(
						classNameAtomicReference.get());
				}

				_deleteFromDatabase(addressId, connection);
			},
			() -> {
				ClassName className = _classNameLocalService.addClassName(
					classNameValue);

				classNameAtomicReference.set(className);

				_insertIntoDatabase(
					addressId, className.getClassNameId(), connection);
			});
	}

	@Test
	public void testNotFoundLiferayClassNameWithDashIsDeleted()
		throws Exception {

		AtomicReference<ClassName> classNameAtomicReference =
			new AtomicReference<>();
		String classNameValue =
			DDMStructure.class.getName() + StringPool.DASH +
				RandomTestUtil.randomString();

		test(
			logCapture -> {
				List<String> messages = logCapture.getMessages();

				Assert.assertTrue(
					messages.toString(),
					messages.contains(
						StringBundler.concat(
							"Table ", dbInspector.normalizeName("ClassName_"),
							", 1 row deleted because \"", classNameValue,
							"\" is not defined in any deployed module and is ",
							"not in use")));

				ClassName className = _classNameLocalService.fetchClassName(
					classNameValue);

				Assert.assertEquals(StringPool.BLANK, className.getValue());
			},
			() -> {
				if (classNameAtomicReference.get() != null) {
					_classNameLocalService.deleteClassName(
						classNameAtomicReference.get());
				}
			},
			() -> classNameAtomicReference.set(
				_classNameLocalService.addClassName(classNameValue)));
	}

	@Test
	public void testNotFoundLiferayClassNameWithPoundUnusedIsDeleted()
		throws Exception {

		AtomicReference<ClassName> classNameAtomicReference =
			new AtomicReference<>();
		String classNameValue =
			ObjectDefinitionConstants.
				CLASS_NAME_PREFIX_CUSTOM_OBJECT_DEFINITION +
					RandomTestUtil.randomString(4);

		test(
			logCapture -> {
				List<String> messages = logCapture.getMessages();

				Assert.assertTrue(
					messages.toString(),
					messages.contains(
						StringBundler.concat(
							"Table ", dbInspector.normalizeName("ClassName_"),
							", 1 row deleted because \"", classNameValue,
							"\" is not defined in any deployed module and is ",
							"not in use")));

				ClassName className = _classNameLocalService.fetchClassName(
					classNameValue);

				Assert.assertEquals(StringPool.BLANK, className.getValue());
			},
			() -> {
				if (classNameAtomicReference.get() != null) {
					_classNameLocalService.deleteClassName(
						classNameAtomicReference.get());
				}
			},
			() -> classNameAtomicReference.set(
				_classNameLocalService.addClassName(classNameValue)));
	}

	@Test
	public void testNotFoundLiferayClassNameWithPoundUsedIsNotDeleted()
		throws Exception {

		long addressId = RandomTestUtil.nextLong();
		AtomicReference<ClassName> classNameAtomicReference =
			new AtomicReference<>();
		String classNameValue =
			ObjectDefinitionConstants.
				CLASS_NAME_PREFIX_CUSTOM_OBJECT_DEFINITION +
					RandomTestUtil.randomString();

		test(
			logCapture -> {
				List<String> messages = logCapture.getMessages();

				Assert.assertTrue(
					messages.toString(),
					messages.contains(
						StringBundler.concat(
							"Class name ", classNameValue,
							" is not defined in any deployed module but is ",
							"referenced in the next tables: ",
							dbInspector.normalizeName(_TABLE_NAME))));

				ClassName className = _classNameLocalService.fetchClassName(
					classNameValue);

				Assert.assertEquals(classNameValue, className.getValue());
			},
			() -> {
				if (classNameAtomicReference.get() != null) {
					_classNameLocalService.deleteClassName(
						classNameAtomicReference.get());
				}

				_deleteFromDatabase(addressId, connection);
			},
			() -> {
				ClassName className = _classNameLocalService.addClassName(
					classNameValue);

				classNameAtomicReference.set(className);

				_insertIntoDatabase(
					addressId, className.getClassNameId(), connection);
			});
	}

	@Test
	public void testNotFoundSubscriptionClassNameUnusedIsDeleted()
		throws Exception {

		AtomicReference<ClassName> classNameAtomicReference =
			new AtomicReference<>();
		String classNameValue = MBUtil.getSubscriptionClassName(
			"com.liferay.test." + RandomTestUtil.randomString());

		test(
			logCapture -> {
				List<String> messages = logCapture.getMessages();

				Assert.assertTrue(
					messages.toString(),
					messages.contains(
						StringBundler.concat(
							"Table ", dbInspector.normalizeName("ClassName_"),
							", 1 row deleted because \"", classNameValue,
							"\" is not defined in any deployed module and is ",
							"not in use")));

				ClassName className = _classNameLocalService.fetchClassName(
					classNameValue);

				Assert.assertEquals(StringPool.BLANK, className.getValue());
			},
			() -> {
				if (classNameAtomicReference.get() != null) {
					_classNameLocalService.deleteClassName(
						classNameAtomicReference.get());
				}
			},
			() -> classNameAtomicReference.set(
				_classNameLocalService.addClassName(classNameValue)));
	}

	@Test
	public void testNotFoundSubscriptionClassNameUsedIsNotDeleted()
		throws Exception {

		long addressId = RandomTestUtil.nextLong();
		AtomicReference<ClassName> classNameAtomicReference =
			new AtomicReference<>();
		String classNameValue = MBUtil.getSubscriptionClassName(
			"com.liferay.test." + RandomTestUtil.randomString());

		test(
			logCapture -> {
				List<String> messages = logCapture.getMessages();

				Assert.assertTrue(
					messages.toString(),
					messages.contains(
						StringBundler.concat(
							"Class name ", classNameValue,
							" is not defined in any deployed module but is ",
							"referenced in the next tables: ",
							dbInspector.normalizeName(_TABLE_NAME))));

				ClassName className = _classNameLocalService.fetchClassName(
					classNameValue);

				Assert.assertEquals(classNameValue, className.getValue());
			},
			() -> {
				if (classNameAtomicReference.get() != null) {
					_classNameLocalService.deleteClassName(
						classNameAtomicReference.get());
				}

				_deleteFromDatabase(addressId, connection);
			},
			() -> {
				ClassName className = _classNameLocalService.addClassName(
					classNameValue);

				classNameAtomicReference.set(className);

				_insertIntoDatabase(
					addressId, className.getClassNameId(), connection);
			});
	}

	@Test
	public void testVerifyDoesNotRunIfModulesNotStarted() throws Exception {
		AtomicReference<Bundle> bundleAtomicReference = new AtomicReference<>();

		test(
			logCapture -> {
				List<String> messages = logCapture.getMessages();

				Assert.assertTrue(
					messages.toString(),
					messages.contains(
						"ClassNamePostUpgradeDataCleanupProcess cannot be " +
							"executed because there are modules with " +
								"unsatisfied references"));
			},
			() -> {
				Bundle bundle = bundleAtomicReference.get();

				if (bundle != null) {
					installBundle(bundle, SystemBundleUtil.getBundleContext());
				}
			},
			() -> bundleAtomicReference.set(
				uninstallBundle(
					SystemBundleUtil.getBundleContext(),
					"com.liferay.dynamic.data.mapping.service")));
	}

	@Override
	protected Object[] getPostUpgradeDataCleanupProcessArguments() {
		return new Object[] {
			_classNameLocalService, _companyLocalService, connection,
			_objectDefinitionLocalService
		};
	}

	@Override
	protected Class<?>[] getPostUpgradeDataCleanupProcessArgumentTypes() {
		return new Class<?>[] {
			ClassNameLocalService.class, CompanyLocalService.class,
			Connection.class, ObjectDefinitionLocalService.class
		};
	}

	@Override
	protected String getPostUpgradeDataCleanupProcessClassName() {
		return "com.liferay.data.cleanup.internal.verify." +
			"ClassNamePostUpgradeDataCleanupProcess";
	}

	@Override
	protected void test(
			UnsafeConsumer<LogCapture, Exception> assertUnsafeConsumer,
			UnsafeRunnable<Exception> cleanUpDataUnsafeRunnable,
			UnsafeRunnable<Exception> initializeDataUnsafeRunnable)
		throws Exception {

		long classNameCount = _classNameLocalService.getClassNamesCount();

		try {
			super.test(
				assertUnsafeConsumer, cleanUpDataUnsafeRunnable,
				initializeDataUnsafeRunnable);
		}
		finally {
			Assert.assertEquals(
				classNameCount, _classNameLocalService.getClassNamesCount());
		}
	}

	private void _deleteFromDatabase(long addressId, Connection connection)
		throws Exception {

		try (PreparedStatement preparedStatement = connection.prepareStatement(
				"delete from " + _TABLE_NAME + "  where addressId = ?")) {

			preparedStatement.setLong(1, addressId);
			preparedStatement.executeUpdate();
		}
	}

	private void _insertIntoDatabase(
			long addressId, long classNameId, Connection connection)
		throws Exception {

		try (PreparedStatement preparedStatement = connection.prepareStatement(
				"insert into Address (mvccVersion, ctCollectionId, " +
					"addressId, classNameId) values (0, 0, ?, ?)")) {

			preparedStatement.setLong(1, addressId);
			preparedStatement.setLong(2, classNameId);

			preparedStatement.executeUpdate();
		}
	}

	private static final String _TABLE_NAME = "Address";

	@Inject
	private ClassNameLocalService _classNameLocalService;

	@Inject
	private CompanyLocalService _companyLocalService;

	@Inject
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

}