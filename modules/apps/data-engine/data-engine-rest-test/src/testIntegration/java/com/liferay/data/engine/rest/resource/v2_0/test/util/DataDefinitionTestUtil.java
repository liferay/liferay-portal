/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.data.engine.rest.resource.v2_0.test.util;

import com.liferay.data.engine.rest.client.dto.v2_0.DataDefinition;
import com.liferay.data.engine.rest.client.dto.v2_0.DataDefinitionField;
import com.liferay.data.engine.rest.client.resource.v2_0.DataDefinitionResource;
import com.liferay.data.engine.rest.resource.v2_0.test.util.content.type.TestDataDefinitionContentType;
import com.liferay.dynamic.data.mapping.model.DDMStructure;
import com.liferay.dynamic.data.mapping.storage.StorageType;
import com.liferay.dynamic.data.mapping.test.util.DDMStructureTestHelper;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.StringUtil;

import java.io.InputStream;

import java.util.Map;

/**
 * @author Gabriel Albuquerque
 */
public class DataDefinitionTestUtil {

	public static DDMStructure addDDMStructure(Group group) throws Exception {
		DDMStructureTestHelper ddmStructureTestHelper =
			new DDMStructureTestHelper(
				PortalUtil.getClassNameId(TestDataDefinitionContentType.class),
				group);

		return ddmStructureTestHelper.addStructure(
			PortalUtil.getClassNameId(TestDataDefinitionContentType.class),
			RandomTestUtil.randomString(), RandomTestUtil.randomString(),
			read("test-structured-content-structure.json"),
			StorageType.DEFAULT.getValue());
	}

	public static DataDefinition addDataDefinition(
			DataDefinition dataDefinition, Long groupId)
		throws Exception {

		DataDefinitionResource.Builder builder =
			DataDefinitionResource.builder();

		DataDefinitionResource dataDefinitionResource = builder.authentication(
			"test@liferay.com", "test"
		).locale(
			LocaleUtil.getDefault()
		).build();

		return dataDefinitionResource.postSiteDataDefinitionByContentType(
			groupId, "test", dataDefinition);
	}

	public static DataDefinition addDataDefinition(long groupId)
		throws Exception {

		return addDataDefinition(_randomDataDefinition(groupId), groupId);
	}

	public static DataDefinition addDataDefinitionWithDataLayout(long groupId)
		throws Exception {

		DataDefinition dataDefinition = DataDefinition.toDTO(
			read("data-definition-basic.json"));

		dataDefinition.setSiteId(groupId);

		DataDefinitionResource.Builder builder =
			DataDefinitionResource.builder();

		DataDefinitionResource dataDefinitionResource = builder.authentication(
			"test@liferay.com", "test"
		).locale(
			LocaleUtil.getDefault()
		).build();

		return dataDefinitionResource.postSiteDataDefinitionByContentType(
			groupId, "test", dataDefinition);
	}

	public static DataDefinition addDataDefinitionWithFieldset(long groupId)
		throws Exception {

		DataDefinition dataDefinition = DataDefinition.toDTO(
			read("data-definition-object-structure-with-fieldset.json"));

		DataDefinitionResource.Builder builder =
			DataDefinitionResource.builder();

		DataDefinitionResource dataDefinitionResource = builder.authentication(
			"test@liferay.com", "test"
		).locale(
			LocaleUtil.getDefault()
		).build();

		DataDefinition fieldsetDataDefinition =
			dataDefinitionResource.postSiteDataDefinitionByContentType(
				groupId, "test",
				DataDefinition.toDTO(
					read("data-definition-fieldset-structure.json")));

		for (DataDefinitionField dataDefinitionField :
				dataDefinition.getDataDefinitionFields()) {

			Map<String, Object> customProperties =
				dataDefinitionField.getCustomProperties();

			customProperties.put(
				"ddmStructureId", fieldsetDataDefinition.getId());
		}

		return dataDefinitionResource.postSiteDataDefinitionByContentType(
			groupId, "test", dataDefinition);
	}

	public static String read(String fileName) throws Exception {
		Class<?> clazz = DataDefinitionTestUtil.class;

		InputStream inputStream = clazz.getResourceAsStream(
			"dependencies/" + fileName);

		JSONObject jsonObject = JSONFactoryUtil.createJSONObject(
			StringUtil.read(inputStream));

		return jsonObject.toString();
	}

	private static DataDefinition _randomDataDefinition(long groupId)
		throws Exception {

		DataDefinition dataDefinition = new DataDefinition() {
			{
				availableLanguageIds = new String[] {"en_US", "pt_BR"};
				dataDefinitionFields = new DataDefinitionField[] {
					new DataDefinitionField() {
						{
							description = HashMapBuilder.<String, Object>put(
								"en_US", RandomTestUtil.randomString()
							).build();
							fieldType = "text";
							label = HashMapBuilder.<String, Object>put(
								"en_US", RandomTestUtil.randomString()
							).put(
								"pt_BR", RandomTestUtil.randomString()
							).build();
							name = RandomTestUtil.randomString();
							tip = HashMapBuilder.<String, Object>put(
								"en_US", RandomTestUtil.randomString()
							).put(
								"pt_BR", RandomTestUtil.randomString()
							).build();
						}
					}
				};
				dataDefinitionKey = RandomTestUtil.randomString();
				defaultLanguageId = "en_US";
				siteId = groupId;
				userId = TestPropsValues.getUserId();
			}
		};

		dataDefinition.setDescription(
			HashMapBuilder.<String, Object>put(
				"en_US", RandomTestUtil.randomString()
			).build());
		dataDefinition.setName(
			HashMapBuilder.<String, Object>put(
				"en_US", RandomTestUtil.randomString()
			).build());

		return dataDefinition;
	}

}