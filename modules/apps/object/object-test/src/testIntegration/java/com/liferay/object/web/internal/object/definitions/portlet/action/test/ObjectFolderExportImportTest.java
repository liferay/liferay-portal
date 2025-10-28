/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.web.internal.object.definitions.portlet.action.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.object.admin.rest.dto.v1_0.ObjectFolder;
import com.liferay.object.admin.rest.dto.v1_0.ObjectFolderItem;
import com.liferay.object.admin.rest.resource.v1_0.ObjectFolderResource;
import com.liferay.object.constants.ObjectFolderConstants;
import com.liferay.object.web.internal.BaseExportImportTestCase;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCResourceCommand;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.FeatureFlags;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.portal.vulcan.pagination.Page;
import com.liferay.portal.vulcan.pagination.Pagination;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Guilherme Sa
 */
@FeatureFlags(
	featureFlags = {@FeatureFlag("LPD-17564"), @FeatureFlag("LPD-32050")}
)
@RunWith(Arquillian.class)
public class ObjectFolderExportImportTest extends BaseExportImportTestCase {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	@Override
	public void setUp() throws Exception {
		super.setUp();

		ObjectFolderResource.Builder builder =
			_objectFolderResourceFactory.create();

		_objectFolderResource = builder.user(
			user
		).build();
	}

	@Test
	public void testExportImportObjectFolder() throws Exception {

		// Import and export an object folder

		testExportImport(
			"test-object-folder-1.json", "test-object-folder-1.json",
			"TESTOBJECTFOLDER1", "TestObjectFolder1");

		_assertObjectFolder(
			0, Collections.emptyList(), "TestObjectFolder1",
			Collections.emptyList());

		testExportImport(
			"test-object-folder-1.nonexistent-object-folder-items.json",
			"test-object-folder-1.json", "TESTOBJECTFOLDER1",
			"TestObjectFolder1");

		_assertObjectFolder(
			0, Collections.emptyList(), "TestObjectFolder1",
			Collections.emptyList());

		// Import and export an object folder that has a linked object
		// definition that does not exist

		testExportImport(
			"test-object-folder-2.json", "test-object-folder-2.json",
			"TESTOBJECTFOLDER2", "TestObjectFolder2");

		_assertObjectFolder(
			2, Collections.singletonList("TESTOBJECTDEFINITION2"),
			"TestObjectFolder2",
			Collections.singletonList("TESTOBJECTDEFINITION1"));
		_assertDefaultObjectFolder(
			Collections.singletonList("TESTOBJECTDEFINITION1"),
			Collections.singletonList("TESTOBJECTDEFINITION2"));

		// Import and export an object folder that moves an object definition
		// between object folders during import

		testExportImport(
			"test-object-folder-3.json", "test-object-folder-3.json",
			"TESTOBJECTFOLDER3", "TestObjectFolder3");

		_assertObjectFolder(
			2, Collections.singletonList("TESTOBJECTDEFINITION2"),
			"TestObjectFolder2",
			Collections.singletonList("TESTOBJECTDEFINITION1"));
		_assertObjectFolder(
			2, Collections.singletonList("TESTOBJECTDEFINITION1"),
			"TestObjectFolder3",
			Collections.singletonList("TESTOBJECTDEFINITION2"));

		// Import and export an object folder with a duplicate external
		// reference code

		testExportImport(
			"test-object-folder-1.json", "test-object-folder-1.json",
			"TESTOBJECTFOLDER3", "TestObjectFolder1");

		_assertObjectFolder(
			2, Collections.singletonList("TESTOBJECTDEFINITION2"),
			"TestObjectFolder2",
			Collections.singletonList("TESTOBJECTDEFINITION1"));
		_assertObjectFolder(
			0, Collections.emptyList(), "TestObjectFolder3",
			Collections.emptyList());
		_assertDefaultObjectFolder(
			Collections.singletonList("TESTOBJECTDEFINITION1"),
			Collections.singletonList("TESTOBJECTDEFINITION2"));
	}

	@Override
	protected ClassLoader getClassLoader() {
		return ObjectFolderExportImportTest.class.getClassLoader();
	}

	@Override
	protected Class<?> getClazz() {
		return getClass();
	}

	@Override
	protected long getId(String name) throws Exception {
		ObjectFolder importedFolder = _getObjectFolder(name);

		return importedFolder.getId();
	}

	@Override
	protected String getIdentifierName() {
		return "objectFolderId";
	}

	@Override
	protected String getJSONName() {
		return "objectFolderJSON";
	}

	@Override
	protected MVCActionCommand getMVCActionCommand() {
		return _mvcActionCommand;
	}

	@Override
	protected MVCResourceCommand getMVCResourceCommand() {
		return _mvcResourceCommand;
	}

	private void _assertDefaultObjectFolder(
			List<String> linkedObjectFolderItemExternalReferenceCodes,
			List<String> unlinkedObjectFolderItemExternalReferenceCodes)
		throws Exception {

		ObjectFolder defaultObjectFolder = _getObjectFolder(
			ObjectFolderConstants.NAME_DEFAULT);

		List<String> defaultObjectFolderItemExternalReferenceCodes =
			TransformUtil.transform(
				ListUtil.fromArray(defaultObjectFolder.getObjectFolderItems()),
				ObjectFolderItem::getObjectDefinitionExternalReferenceCode);

		Assert.assertTrue(
			defaultObjectFolderItemExternalReferenceCodes.containsAll(
				linkedObjectFolderItemExternalReferenceCodes));
		Assert.assertTrue(
			defaultObjectFolderItemExternalReferenceCodes.containsAll(
				unlinkedObjectFolderItemExternalReferenceCodes));

		for (ObjectFolderItem objectFolderItem :
				defaultObjectFolder.getObjectFolderItems()) {

			if (linkedObjectFolderItemExternalReferenceCodes.contains(
					objectFolderItem.
						getObjectDefinitionExternalReferenceCode())) {

				Assert.assertTrue(objectFolderItem.getLinkedObjectDefinition());
			}

			if (unlinkedObjectFolderItemExternalReferenceCodes.contains(
					objectFolderItem.
						getObjectDefinitionExternalReferenceCode())) {

				Assert.assertFalse(
					objectFolderItem.getLinkedObjectDefinition());
			}
		}
	}

	private void _assertObjectFolder(
			long expectedLength,
			List<String> linkedObjectFolderItemExternalReferenceCodes,
			String name,
			List<String> unlinkedObjectFolderItemExternalReferenceCodes)
		throws Exception {

		ObjectFolder objectFolder = _getObjectFolder(name);

		ObjectFolderItem[] objectFolderItems =
			objectFolder.getObjectFolderItems();

		Assert.assertEquals(
			Arrays.toString(objectFolderItems), expectedLength,
			objectFolderItems.length);

		for (ObjectFolderItem objectFolderItem : objectFolderItems) {
			if (objectFolderItem.getLinkedObjectDefinition()) {
				Assert.assertTrue(
					linkedObjectFolderItemExternalReferenceCodes.contains(
						objectFolderItem.
							getObjectDefinitionExternalReferenceCode()));
			}
			else {
				Assert.assertTrue(
					unlinkedObjectFolderItemExternalReferenceCodes.contains(
						objectFolderItem.
							getObjectDefinitionExternalReferenceCode()));
			}
		}
	}

	private ObjectFolder _getObjectFolder(String name) throws Exception {
		Page<ObjectFolder> page = _objectFolderResource.getObjectFoldersPage(
			name, Pagination.of(1, 1));

		List<ObjectFolder> items = (List<ObjectFolder>)page.getItems();

		return items.get(0);
	}

	@Inject
	private JSONFactory _jsonFactory;

	@Inject(
		filter = "mvc.command.name=/object_definitions/import_object_folder"
	)
	private MVCActionCommand _mvcActionCommand;

	@Inject(
		filter = "mvc.command.name=/object_definitions/export_object_folder"
	)
	private MVCResourceCommand _mvcResourceCommand;

	private ObjectFolderResource _objectFolderResource;

	@Inject
	private ObjectFolderResource.Factory _objectFolderResourceFactory;

}