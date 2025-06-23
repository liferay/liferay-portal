/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.delivery.resource.v1_0.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.depot.model.DepotEntry;
import com.liferay.depot.service.DepotEntryLocalServiceUtil;
import com.liferay.dynamic.data.mapping.constants.DDMStructureConstants;
import com.liferay.dynamic.data.mapping.io.DDMFormDeserializer;
import com.liferay.dynamic.data.mapping.io.DDMFormDeserializerDeserializeRequest;
import com.liferay.dynamic.data.mapping.io.DDMFormDeserializerDeserializeResponse;
import com.liferay.dynamic.data.mapping.model.DDMForm;
import com.liferay.dynamic.data.mapping.model.DDMStructure;
import com.liferay.dynamic.data.mapping.storage.StorageType;
import com.liferay.dynamic.data.mapping.test.util.DDMStructureTestHelper;
import com.liferay.headless.delivery.client.dto.v1_0.ContentStructure;
import com.liferay.headless.delivery.client.pagination.Page;
import com.liferay.journal.model.JournalArticle;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.service.GroupLocalServiceUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.test.rule.Inject;

import java.io.InputStream;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Javier Gamarra
 */
@RunWith(Arquillian.class)
public class ContentStructureResourceTest
	extends BaseContentStructureResourceTestCase {

	@Test
	public void testGetSiteContentStructuresPageSearch() throws Exception {
		DDMStructure ddmStructure = _addDDMStructure(
			testGroup, RandomTestUtil.randomString());

		Page<ContentStructure> page =
			contentStructureResource.getSiteContentStructuresPage(
				testGroup.getGroupId(), ddmStructure.getName("en_US"), null,
				null, null, null);

		Assert.assertEquals(1, page.getTotalCount());

		page = contentStructureResource.getSiteContentStructuresPage(
			testGroup.getGroupId(), ddmStructure.getDescription("en_US"), null,
			null, null, null);

		Assert.assertEquals(1, page.getTotalCount());
	}

	@Override
	protected String[] getAdditionalAssertFieldNames() {
		return new String[] {"name"};
	}

	@Override
	protected ContentStructure
			testGetAssetLibraryContentStructurePermissionsPage_addContentStructure()
		throws Exception {

		return testGetContentStructure_addContentStructure();
	}

	@Override
	protected ContentStructure
			testGetAssetLibraryContentStructuresPage_addContentStructure(
				Long assetLibraryId, ContentStructure contentStructure)
		throws Exception {

		DepotEntry depotEntry = DepotEntryLocalServiceUtil.getDepotEntry(
			assetLibraryId);

		return _toContentStructure(
			_addDDMStructure(
				depotEntry.getGroup(), contentStructure.getName()));
	}

	@Override
	protected ContentStructure testGetContentStructure_addContentStructure()
		throws Exception {

		return _toContentStructure(
			_addDDMStructure(testGroup, RandomTestUtil.randomString()));
	}

	@Override
	protected ContentStructure
			testGetContentStructurePermissionsPage_addContentStructure()
		throws Exception {

		return testGetContentStructure_addContentStructure();
	}

	@Override
	protected ContentStructure
			testGetSiteContentStructurePermissionsPage_addContentStructure()
		throws Exception {

		return testGetContentStructure_addContentStructure();
	}

	@Override
	protected ContentStructure
			testGetSiteContentStructuresPage_addContentStructure(
				Long siteId, ContentStructure contentStructure)
		throws Exception {

		return _toContentStructure(
			_addDDMStructure(
				GroupLocalServiceUtil.getGroup(siteId),
				contentStructure.getName()));
	}

	@Override
	protected ContentStructure testGraphQLContentStructure_addContentStructure()
		throws Exception {

		return testGetContentStructure_addContentStructure();
	}

	@Override
	protected ContentStructure
			testPutAssetLibraryContentStructurePermissionsPage_addContentStructure()
		throws Exception {

		return testGetContentStructure_addContentStructure();
	}

	@Override
	protected ContentStructure
			testPutContentStructurePermissionsPage_addContentStructure()
		throws Exception {

		return testGetContentStructure_addContentStructure();
	}

	@Override
	protected ContentStructure
			testPutSiteContentStructurePermissionsPage_addContentStructure()
		throws Exception {

		return testGetContentStructure_addContentStructure();
	}

	private DDMStructure _addDDMStructure(Group group, String name)
		throws Exception {

		DDMStructureTestHelper ddmStructureTestHelper =
			new DDMStructureTestHelper(
				PortalUtil.getClassNameId(JournalArticle.class), group);

		return ddmStructureTestHelper.addStructure(
			PortalUtil.getClassNameId(JournalArticle.class),
			RandomTestUtil.randomString(), name, RandomTestUtil.randomString(),
			_deserialize(_read("test-ddm-structure.json")),
			StorageType.DEFAULT.getValue(), DDMStructureConstants.TYPE_DEFAULT);
	}

	private DDMForm _deserialize(String content) {
		DDMFormDeserializerDeserializeRequest.Builder builder =
			DDMFormDeserializerDeserializeRequest.Builder.newBuilder(content);

		DDMFormDeserializerDeserializeResponse
			ddmFormDeserializerDeserializeResponse =
				_jsonDDMFormDeserializer.deserialize(builder.build());

		return ddmFormDeserializerDeserializeResponse.getDDMForm();
	}

	private String _read(String fileName) throws Exception {
		Class<?> clazz = getClass();

		InputStream inputStream = clazz.getResourceAsStream(
			"dependencies/" + fileName);

		return StringUtil.read(inputStream);
	}

	private ContentStructure _toContentStructure(DDMStructure structure) {
		return new ContentStructure() {
			{
				dateCreated = structure.getCreateDate();
				dateModified = structure.getModifiedDate();
				id = structure.getStructureId();
				name = structure.getName(LocaleUtil.getDefault());
				siteId = structure.getGroupId();
			}
		};
	}

	@Inject(filter = "ddm.form.deserializer.type=json")
	private static DDMFormDeserializer _jsonDDMFormDeserializer;

}