/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.admin.rest.resource.v1_0.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.object.admin.rest.client.dto.v1_0.ObjectFolder;
import com.liferay.object.admin.rest.client.dto.v1_0.ObjectFolderItem;
import com.liferay.object.admin.rest.resource.v1_0.test.util.ObjectDefinitionTestUtil;
import com.liferay.object.constants.ObjectFolderConstants;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectFolderLocalService;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.search.Document;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.Hits;
import com.liferay.portal.kernel.search.IndexWriterHelper;
import com.liferay.portal.kernel.search.Indexer;
import com.liferay.portal.kernel.search.IndexerRegistryUtil;
import com.liferay.portal.kernel.search.SearchContext;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.test.rule.Inject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Murilo Stodolni
 */
@RunWith(Arquillian.class)
public class ObjectFolderResourceTest extends BaseObjectFolderResourceTestCase {

	@Ignore
	@Override
	@Test
	public void testGraphQLGetObjectFolder() {
	}

	@Ignore
	@Override
	@Test
	public void testGraphQLGetObjectFolderByExternalReferenceCode() {
	}

	@Ignore
	@Override
	@Test
	public void testGraphQLGetObjectFolderByExternalReferenceCodeNotFound() {
	}

	@Ignore
	@Override
	@Test
	public void testGraphQLGetObjectFolderNotFound() {
	}

	@Ignore
	@Override
	@Test
	public void testGraphQLGetObjectFoldersPage() {
	}

	@Override
	@Test
	public void testPatchObjectFolder() throws Exception {
		super.testPatchObjectFolder();

		ObjectDefinition finalObjectDefinition =
			ObjectDefinitionTestUtil.addCustomObjectDefinition();

		ObjectFolder postObjectFolder = testPostObjectFolder_addObjectFolder(
			new ObjectFolder() {
				{
					setExternalReferenceCode(RandomTestUtil.randomString());
					setLabel(
						Collections.singletonMap(
							"en_US", RandomTestUtil.randomString()));
					setName(
						StringUtil.toLowerCase(RandomTestUtil.randomString()));
					setObjectFolderItems(
						new ObjectFolderItem[] {
							new ObjectFolderItem() {
								{
									setLinkedObjectDefinition(false);
									setObjectDefinitionExternalReferenceCode(
										finalObjectDefinition.
											getExternalReferenceCode());
									setPositionX(0);
									setPositionY(0);
								}
							}
						});
				}
			});

		postObjectFolder.setExternalReferenceCode(
			RandomTestUtil.randomString());

		ObjectFolder patchObjectFolder = objectFolderResource.patchObjectFolder(
			postObjectFolder.getId(), postObjectFolder);

		Assert.assertEquals(
			postObjectFolder.getExternalReferenceCode(),
			patchObjectFolder.getExternalReferenceCode());

		ObjectFolderItem[] objectFolderItems =
			patchObjectFolder.getObjectFolderItems();

		Assert.assertEquals(
			Arrays.toString(objectFolderItems), 1, objectFolderItems.length);

		ObjectFolderItem objectFolderItem = objectFolderItems[0];

		Assert.assertEquals(
			finalObjectDefinition.getExternalReferenceCode(),
			objectFolderItem.getObjectDefinitionExternalReferenceCode());
	}

	@Override
	@Test
	public void testPutObjectFolder() throws Exception {
		super.testPutObjectFolder();

		ObjectDefinition finalObjectDefinition =
			ObjectDefinitionTestUtil.addCustomObjectDefinition();

		ObjectFolder postObjectFolder = testPostObjectFolder_addObjectFolder(
			new ObjectFolder() {
				{
					setExternalReferenceCode(RandomTestUtil.randomString());
					setLabel(
						Collections.singletonMap(
							"en_US", RandomTestUtil.randomString()));
					setName(
						StringUtil.toLowerCase(RandomTestUtil.randomString()));
					setObjectFolderItems(
						new ObjectFolderItem[] {
							new ObjectFolderItem() {
								{
									setLinkedObjectDefinition(false);
									setObjectDefinitionExternalReferenceCode(
										finalObjectDefinition.
											getExternalReferenceCode());
									setPositionX(0);
									setPositionY(0);
								}
							}
						});
				}
			});

		postObjectFolder.setObjectFolderItems(new ObjectFolderItem[0]);

		ObjectFolder putObjectFolder = objectFolderResource.putObjectFolder(
			postObjectFolder.getId(), postObjectFolder);

		ObjectFolderItem[] objectFolderItems =
			putObjectFolder.getObjectFolderItems();

		Assert.assertEquals(
			Arrays.toString(objectFolderItems), 1, objectFolderItems.length);

		Assert.assertEquals(
			finalObjectDefinition.getExternalReferenceCode(),
			objectFolderItems[0].getObjectDefinitionExternalReferenceCode());
	}

	@Override
	@Test
	public void testPutObjectFolderByExternalReferenceCode() throws Exception {
		super.testPutObjectFolderByExternalReferenceCode();

		_testPutObjectFolderByExternalReferenceCodeMovedObjectDefinition();
		_testPutObjectFolderByExternalReferenceCodeUnmovedObjectDefinition();
	}

	@Override
	protected String[] getAdditionalAssertFieldNames() {
		return new String[] {"label"};
	}

	@Override
	protected ObjectFolder randomObjectFolder() throws Exception {
		ObjectFolder objectFolder = super.randomObjectFolder();

		objectFolder.setLabel(
			Collections.singletonMap("en_US", RandomTestUtil.randomString()));

		return objectFolder;
	}

	@Override
	protected ObjectFolder testDeleteObjectFolder_addObjectFolder()
		throws Exception {

		return testPostObjectFolder_addObjectFolder(randomObjectFolder());
	}

	@Override
	protected ObjectFolder testGetObjectFolder_addObjectFolder()
		throws Exception {

		return testPostObjectFolder_addObjectFolder(randomObjectFolder());
	}

	@Override
	protected ObjectFolder
			testGetObjectFolderByExternalReferenceCode_addObjectFolder()
		throws Exception {

		return testPostObjectFolder_addObjectFolder(randomObjectFolder());
	}

	@Override
	protected ObjectFolder testGetObjectFoldersPage_addObjectFolder(
			ObjectFolder objectFolder)
		throws Exception {

		return testPostObjectFolder_addObjectFolder(objectFolder);
	}

	@Override
	protected ObjectFolder testGraphQLObjectFolder_addObjectFolder()
		throws Exception {

		return testPostObjectFolder_addObjectFolder(randomObjectFolder());
	}

	@Override
	protected ObjectFolder testPatchObjectFolder_addObjectFolder()
		throws Exception {

		return testPostObjectFolder_addObjectFolder(randomObjectFolder());
	}

	@Override
	protected ObjectFolder testPostObjectFolder_addObjectFolder(
			ObjectFolder objectFolder)
		throws Exception {

		return objectFolderResource.postObjectFolder(objectFolder);
	}

	@Override
	protected ObjectFolder testPutObjectFolder_addObjectFolder()
		throws Exception {

		return testPostObjectFolder_addObjectFolder(randomObjectFolder());
	}

	@Override
	protected ObjectFolder
			testPutObjectFolderByExternalReferenceCode_addObjectFolder()
		throws Exception {

		return testPostObjectFolder_addObjectFolder(randomObjectFolder());
	}

	@Override
	protected ObjectFolder
			testPutObjectFolderByExternalReferenceCode_createObjectFolder()
		throws Exception {

		return testPostObjectFolder_addObjectFolder(randomObjectFolder());
	}

	private void _assertIndexed(
			boolean expected, ObjectDefinition objectDefinition)
		throws Exception {

		List<Long> objectDefinitionIds = _getIndexedObjectDefinitionIds(
			objectDefinition);

		Assert.assertEquals(
			expected,
			objectDefinitionIds.contains(
				objectDefinition.getObjectDefinitionId()));
	}

	private void _deleteObjectDefinitionDocument(
			ObjectDefinition objectDefinition)
		throws Exception {

		_assertIndexed(true, objectDefinition);

		Indexer<ObjectDefinition> indexer =
			IndexerRegistryUtil.nullSafeGetIndexer(ObjectDefinition.class);

		indexer.delete(objectDefinition);

		_indexWriterHelper.commit(objectDefinition.getCompanyId());

		_assertIndexed(false, objectDefinition);
	}

	private List<Long> _getIndexedObjectDefinitionIds(
			ObjectDefinition objectDefinition)
		throws Exception {

		SearchContext searchContext = new SearchContext();

		searchContext.setAttribute(Field.NAME, objectDefinition.getShortName());
		searchContext.setCompanyId(objectDefinition.getCompanyId());
		searchContext.setEntryClassNames(
			new String[] {ObjectDefinition.class.getName()});
		searchContext.setKeywords(objectDefinition.getShortName());

		Indexer<ObjectDefinition> indexer =
			IndexerRegistryUtil.nullSafeGetIndexer(ObjectDefinition.class);

		Hits hits = indexer.search(searchContext);

		List<Long> objectDefinitionIds = new ArrayList<>();

		for (Document document : hits.getDocs()) {
			objectDefinitionIds.add(
				GetterUtil.getLong(document.get(Field.ENTRY_CLASS_PK)));
		}

		return objectDefinitionIds;
	}

	private void _testPutObjectFolderByExternalReferenceCodeMovedObjectDefinition()
		throws Exception {

		ObjectDefinition objectDefinition =
			ObjectDefinitionTestUtil.addCustomObjectDefinition();

		_indexWriterHelper.commit(objectDefinition.getCompanyId());

		_deleteObjectDefinitionDocument(objectDefinition);

		ObjectFolder objectFolder = testPostObjectFolder_addObjectFolder(
			randomObjectFolder());

		objectFolder.setObjectFolderItems(
			new ObjectFolderItem[] {
				_toObjectFolderItem(
					false, objectDefinition.getExternalReferenceCode(), 0, 0)
			});

		objectFolderResource.putObjectFolderByExternalReferenceCode(
			objectFolder.getExternalReferenceCode(), objectFolder);

		_indexWriterHelper.commit(objectDefinition.getCompanyId());

		_assertIndexed(true, objectDefinition);

		_objectDefinitionLocalService.deleteObjectDefinition(
			objectDefinition.getObjectDefinitionId());

		_objectFolderLocalService.deleteObjectFolder(objectFolder.getId());
	}

	private void _testPutObjectFolderByExternalReferenceCodeUnmovedObjectDefinition()
		throws Exception {

		ObjectDefinition objectDefinition =
			ObjectDefinitionTestUtil.addCustomObjectDefinition();

		_indexWriterHelper.commit(objectDefinition.getCompanyId());

		_deleteObjectDefinitionDocument(objectDefinition);

		ObjectFolder objectFolder =
			objectFolderResource.getObjectFolderByExternalReferenceCode(
				ObjectFolderConstants.EXTERNAL_REFERENCE_CODE_DEFAULT);

		ObjectFolderItem[] objectFolderItems =
			objectFolder.getObjectFolderItems();

		ObjectFolderItem[] newObjectFolderItems =
			new ObjectFolderItem[objectFolderItems.length];

		for (int i = 0; i < objectFolderItems.length; i++) {
			ObjectFolderItem objectFolderItem = objectFolderItems[i];

			newObjectFolderItems[i] = _toObjectFolderItem(
				objectFolderItem.getLinkedObjectDefinition(),
				objectFolderItem.getObjectDefinitionExternalReferenceCode(),
				objectFolderItem.getPositionX(),
				objectFolderItem.getPositionY());
		}

		objectFolder.setObjectFolderItems(newObjectFolderItems);

		objectFolderResource.putObjectFolderByExternalReferenceCode(
			ObjectFolderConstants.EXTERNAL_REFERENCE_CODE_DEFAULT,
			objectFolder);

		_indexWriterHelper.commit(objectDefinition.getCompanyId());

		_assertIndexed(false, objectDefinition);

		_objectDefinitionLocalService.deleteObjectDefinition(
			objectDefinition.getObjectDefinitionId());
	}

	private ObjectFolderItem _toObjectFolderItem(
		Boolean linkedObjectDefinition,
		String objectDefinitionExternalReferenceCode, Integer positionX,
		Integer positionY) {

		ObjectFolderItem objectFolderItem = new ObjectFolderItem();

		objectFolderItem.setLinkedObjectDefinition(linkedObjectDefinition);
		objectFolderItem.setObjectDefinitionExternalReferenceCode(
			objectDefinitionExternalReferenceCode);
		objectFolderItem.setPositionX(positionX);
		objectFolderItem.setPositionY(positionY);

		return objectFolderItem;
	}

	@Inject
	private IndexWriterHelper _indexWriterHelper;

	@Inject
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	@Inject
	private ObjectFolderLocalService _objectFolderLocalService;

}