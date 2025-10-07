/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.delivery.resource.v1_0.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.depot.model.DepotEntry;
import com.liferay.depot.service.DepotEntryLocalServiceUtil;
import com.liferay.dynamic.data.mapping.constants.DDMStructureConstants;
import com.liferay.dynamic.data.mapping.constants.DDMTemplateConstants;
import com.liferay.dynamic.data.mapping.io.DDMFormDeserializer;
import com.liferay.dynamic.data.mapping.io.DDMFormDeserializerDeserializeRequest;
import com.liferay.dynamic.data.mapping.io.DDMFormDeserializerDeserializeResponse;
import com.liferay.dynamic.data.mapping.model.DDMForm;
import com.liferay.dynamic.data.mapping.model.DDMStructure;
import com.liferay.dynamic.data.mapping.model.DDMTemplate;
import com.liferay.dynamic.data.mapping.service.DDMTemplateLocalServiceUtil;
import com.liferay.dynamic.data.mapping.storage.StorageType;
import com.liferay.dynamic.data.mapping.test.util.DDMStructureTestHelper;
import com.liferay.headless.delivery.client.dto.v1_0.ContentTemplate;
import com.liferay.journal.model.JournalArticle;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.service.GroupLocalServiceUtil;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.template.TemplateConstants;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.odata.entity.EntityField;
import com.liferay.portal.test.rule.Inject;

import java.io.InputStream;

import java.util.Collections;

import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Javier Gamarra
 */
@RunWith(Arquillian.class)
public class ContentTemplateResourceTest
	extends BaseContentTemplateResourceTestCase {

	@Override
	@Test
	public void testGetAssetLibraryContentTemplatesPageWithSortInteger()
		throws Exception {

		testGetAssetLibraryContentTemplatesPageWithSort(
			EntityField.Type.INTEGER,
			(entityField, contentTemplate1, contentTemplate2) -> {
				if (BeanTestUtil.hasProperty(
						contentTemplate1, entityField.getName())) {

					BeanTestUtil.setProperty(
						contentTemplate1, entityField.getName(), 0);
				}

				if (BeanTestUtil.hasProperty(
						contentTemplate2, entityField.getName())) {

					BeanTestUtil.setProperty(
						contentTemplate2, entityField.getName(), 1);
				}
			});
	}

	@Override
	@Test
	public void testGetSiteContentTemplatesPageWithSortInteger()
		throws Exception {

		testGetSiteContentTemplatesPageWithSort(
			EntityField.Type.INTEGER,
			(entityField, contentTemplate1, contentTemplate2) -> {
				if (BeanTestUtil.hasProperty(
						contentTemplate1, entityField.getName())) {

					BeanTestUtil.setProperty(
						contentTemplate1, entityField.getName(), 0);
				}

				if (BeanTestUtil.hasProperty(
						contentTemplate2, entityField.getName())) {

					BeanTestUtil.setProperty(
						contentTemplate2, entityField.getName(), 1);
				}
			});
	}

	@Override
	protected ContentTemplate
			testGetAssetLibraryContentTemplatesPage_addContentTemplate(
				Long assetLibraryId, ContentTemplate contentTemplate)
		throws Exception {

		DepotEntry depotEntry = DepotEntryLocalServiceUtil.getDepotEntry(
			assetLibraryId);

		return _getContentTemplate(depotEntry.getGroup());
	}

	@Override
	protected ContentTemplate testGetSiteContentTemplate_addContentTemplate()
		throws Exception {

		return _getContentTemplate(testGroup);
	}

	@Override
	protected ContentTemplate
			testGetSiteContentTemplatesPage_addContentTemplate(
				Long siteId, ContentTemplate contentTemplate)
		throws Exception {

		return _getContentTemplate(GroupLocalServiceUtil.getGroup(siteId));
	}

	@Override
	protected ContentTemplate
			testGraphQLSiteContentTemplate_addContentTemplate()
		throws Exception {

		return _getContentTemplate(testGroup);
	}

	private DDMForm _deserialize(String content) {
		DDMFormDeserializerDeserializeRequest.Builder builder =
			DDMFormDeserializerDeserializeRequest.Builder.newBuilder(content);

		DDMFormDeserializerDeserializeResponse
			ddmFormDeserializerDeserializeResponse =
				_jsonDDMFormDeserializer.deserialize(builder.build());

		return ddmFormDeserializerDeserializeResponse.getDDMForm();
	}

	private ContentTemplate _getContentTemplate(Group group) throws Exception {
		DDMStructureTestHelper ddmStructureTestHelper =
			new DDMStructureTestHelper(
				PortalUtil.getClassNameId(JournalArticle.class), group);

		DDMStructure ddmStructure = ddmStructureTestHelper.addStructure(
			PortalUtil.getClassNameId(JournalArticle.class),
			RandomTestUtil.randomString(), RandomTestUtil.randomString(),
			_deserialize(_read("test-ddm-structure.json")),
			StorageType.DEFAULT.getValue(), DDMStructureConstants.TYPE_DEFAULT);

		DDMTemplate ddmTemplate = DDMTemplateLocalServiceUtil.addTemplate(
			null, TestPropsValues.getUserId(), group.getGroupId(),
			PortalUtil.getClassNameId(DDMStructure.class),
			ddmStructure.getStructureId(),
			PortalUtil.getClassNameId(JournalArticle.class), null,
			Collections.singletonMap(
				LocaleUtil.getDefault(), RandomTestUtil.randomString()),
			null, DDMTemplateConstants.TEMPLATE_TYPE_DISPLAY, null,
			TemplateConstants.LANG_TYPE_VM, "<div>${MyText.getData()}</div>",
			false, false, null, null, new ServiceContext());

		return new ContentTemplate() {
			{
				dateCreated = ddmTemplate.getCreateDate();
				dateModified = ddmTemplate.getModifiedDate();
				id = ddmTemplate.getTemplateKey();
				name = ddmTemplate.getName(LocaleUtil.getDefault());
				siteId = ddmTemplate.getGroupId();
			}
		};
	}

	private String _read(String fileName) throws Exception {
		Class<?> clazz = getClass();

		InputStream inputStream = clazz.getResourceAsStream(
			"dependencies/" + fileName);

		return StringUtil.read(inputStream);
	}

	@Inject(filter = "ddm.form.deserializer.type=json")
	private static DDMFormDeserializer _jsonDDMFormDeserializer;

}