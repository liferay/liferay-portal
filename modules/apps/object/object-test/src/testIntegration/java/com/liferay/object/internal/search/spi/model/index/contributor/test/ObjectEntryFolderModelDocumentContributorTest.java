/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.internal.search.spi.model.index.contributor.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.object.constants.ObjectEntryFolderConstants;
import com.liferay.object.model.ObjectEntryFolder;
import com.liferay.object.service.ObjectEntryFolderLocalService;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.search.Document;
import com.liferay.portal.kernel.search.DocumentImpl;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.search.spi.model.index.contributor.ModelDocumentContributor;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Mikel Lorza
 */
@RunWith(Arquillian.class)
public class ObjectEntryFolderModelDocumentContributorTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();
	}

	@Test
	public void testContribute() throws Exception {
		_testContribute(
			HashMapBuilder.put(
				"cms_kind", "folder"
			).put(
				"cms_root", "true"
			).put(
				"cms_section", "contents"
			).build(),
			ObjectEntryFolderConstants.EXTERNAL_REFERENCE_CODE_CONTENTS);
		_testContribute(
			HashMapBuilder.put(
				"cms_kind", "folder"
			).put(
				"cms_root", "true"
			).put(
				"cms_section", "files"
			).build(),
			ObjectEntryFolderConstants.EXTERNAL_REFERENCE_CODE_FILES);
		_testContribute(
			HashMapBuilder.put(
				"cms_kind", StringPool.BLANK
			).put(
				"cms_root", StringPool.BLANK
			).put(
				"cms_section", StringPool.BLANK
			).build(),
			null);
	}

	private ObjectEntryFolder _addObjectEntryFolder(
			String externalReferenceCode, long parentObjectEntryFolderId)
		throws Exception {

		return _objectEntryFolderLocalService.addObjectEntryFolder(
			externalReferenceCode, _group.getGroupId(),
			TestPropsValues.getUserId(), parentObjectEntryFolderId,
			RandomTestUtil.randomString(),
			HashMapBuilder.put(
				LocaleUtil.ENGLISH, RandomTestUtil.randomString()
			).build(),
			RandomTestUtil.randomString(), new ServiceContext());
	}

	private void _testContribute(
			Map<String, String> attributes,
			String parentObjectEntryFolderExternalReferenceCode)
		throws Exception {

		ObjectEntryFolder parentObjectEntryFolder = _addObjectEntryFolder(
			parentObjectEntryFolderExternalReferenceCode,
			ObjectEntryFolderConstants.PARENT_OBJECT_ENTRY_FOLDER_ID_DEFAULT);

		ObjectEntryFolder objectEntryFolder = _addObjectEntryFolder(
			null, parentObjectEntryFolder.getObjectEntryFolderId());

		Document document = new DocumentImpl();

		_objectEntryFolderModelDocumentContributor.contribute(
			document, objectEntryFolder);

		Assert.assertEquals(
			objectEntryFolder.getDescription(),
			document.get(Field.DESCRIPTION));
		Assert.assertEquals(
			String.valueOf(objectEntryFolder.getParentObjectEntryFolderId()),
			document.get(Field.FOLDER_ID));
		Assert.assertEquals(
			objectEntryFolder.getName(), document.get(Field.NAME));

		for (Map.Entry<String, String> entry : attributes.entrySet()) {
			Assert.assertEquals(entry.getValue(), document.get(entry.getKey()));
		}
	}

	@DeleteAfterTestRun
	private Group _group;

	@Inject
	private ObjectEntryFolderLocalService _objectEntryFolderLocalService;

	@Inject(
		filter = "component.name=com.liferay.object.internal.search.spi.model.index.contributor.ObjectEntryFolderModelDocumentContributor"
	)
	private ModelDocumentContributor<ObjectEntryFolder>
		_objectEntryFolderModelDocumentContributor;

}