/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.delivery.resource.v1_0.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.data.engine.rest.resource.v2_0.DataDefinitionResource;
import com.liferay.dynamic.data.mapping.service.DDMStructureLayoutLocalService;
import com.liferay.headless.delivery.client.dto.v1_0.DocumentMetadataSet;
import com.liferay.headless.delivery.client.serdes.v1_0.DataDefinitionFieldSerDes;
import com.liferay.headless.delivery.client.serdes.v1_0.DataLayoutSerDes;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.test.rule.FeatureFlags;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.portal.vulcan.util.GroupUtil;
import com.liferay.portal.vulcan.util.LocalizedMapUtil;

import java.io.InputStream;

import java.util.Locale;

import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.runner.RunWith;

/**
 * @author Javier Gamarra
 */
@FeatureFlags({"LPD-32247", "LPD-34651"})
@RunWith(Arquillian.class)
public class DocumentMetadataSetResourceTest
	extends BaseDocumentMetadataSetResourceTestCase {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Override
	protected DocumentMetadataSet randomDocumentMetadataSet() throws Exception {
		return _randomDocumentMetadataSet(testGroup);
	}

	@Override
	protected DocumentMetadataSet
			testDeleteAssetLibraryDocumentMetadataSetByExternalReferenceCode_addDocumentMetadataSet()
		throws Exception {

		return documentMetadataSetResource.postSiteDocumentMetadataSet(
			testDepotEntry.getGroupId(), randomDocumentMetadataSet());
	}

	@Override
	protected Long
			testDeleteAssetLibraryDocumentMetadataSetByExternalReferenceCode_getAssetLibraryId()
		throws Exception {

		return testDepotEntry.getDepotEntryId();
	}

	@Override
	protected DocumentMetadataSet
			testGetAssetLibraryDocumentMetadataSetByExternalReferenceCode_addDocumentMetadataSet()
		throws Exception {

		return documentMetadataSetResource.postSiteDocumentMetadataSet(
			testDepotEntry.getGroupId(), randomDocumentMetadataSet());
	}

	@Override
	protected Long
			testGetAssetLibraryDocumentMetadataSetByExternalReferenceCode_getAssetLibraryId()
		throws Exception {

		return testDepotEntry.getDepotEntryId();
	}

	@Override
	protected DocumentMetadataSet
			testGraphQLDocumentMetadataSet_addDocumentMetadataSet()
		throws Exception {

		return documentMetadataSetResource.postSiteDocumentMetadataSet(
			testGroup.getGroupId(), randomDocumentMetadataSet());
	}

	@Override
	protected DocumentMetadataSet
			testGraphQLGetAssetLibraryDocumentMetadataSetByExternalReferenceCode_addDocumentMetadataSet()
		throws Exception {

		return documentMetadataSetResource.postSiteDocumentMetadataSet(
			testDepotEntry.getGroupId(),
			_randomDocumentMetadataSet(testDepotEntry.getGroup()));
	}

	@Override
	protected Long
			testGraphQLGetAssetLibraryDocumentMetadataSetByExternalReferenceCode_getAssetLibraryId()
		throws Exception {

		return testDepotEntry.getDepotEntryId();
	}

	@Override
	protected Long
			testPutAssetLibraryDocumentMetadataSetByExternalReferenceCode_getAssetLibraryId()
		throws Exception {

		return testDepotEntry.getDepotEntryId();
	}

	private DocumentMetadataSet _randomDocumentMetadataSet(Group group)
		throws Exception {

		String randomDescription = StringUtil.toLowerCase(
			RandomTestUtil.randomString());
		String randomName = StringUtil.toLowerCase(
			RandomTestUtil.randomString());

		return new DocumentMetadataSet() {
			{
				setActions(() -> null);
				setAssetLibraryKey(() -> GroupUtil.getAssetLibraryKey(group));
				setAvailableLanguages(
					() -> LocaleUtil.toW3cLanguageIds(
						new Locale[] {LocaleUtil.getSiteDefault()}));
				setDataDefinitionFields(
					DataDefinitionFieldSerDes.toDTOs(
						_read("test-ddm-fields.json")));
				setDataLayout(
					DataLayoutSerDes.toDTO(_read("test-data-layout.json")));
				setDateCreated(RandomTestUtil.nextDate());
				setDateModified(RandomTestUtil.nextDate());
				setDescription(() -> randomDescription);
				setDescription_i18n(
					() -> LocalizedMapUtil.getI18nMap(
						HashMapBuilder.put(
							LocaleUtil.getSiteDefault(), randomDescription
						).build()));
				setExternalReferenceCode(
					StringUtil.toLowerCase(RandomTestUtil.randomString()));
				setId(RandomTestUtil.randomLong());
				setName(() -> randomName);
				setName_i18n(
					() -> LocalizedMapUtil.getI18nMap(
						HashMapBuilder.put(
							LocaleUtil.getSiteDefault(), randomName
						).build()));
				setSiteId(group::getGroupId);
			}
		};
	}

	private String _read(String fileName) throws Exception {
		Class<?> clazz = getClass();

		InputStream inputStream = clazz.getResourceAsStream(
			"dependencies/" + fileName);

		return StringUtil.read(inputStream);
	}

	@Inject
	private DataDefinitionResource.Factory _dataDefinitionResourceFactory;

	@Inject
	private DDMStructureLayoutLocalService _ddmStructureLayoutLocalService;

}