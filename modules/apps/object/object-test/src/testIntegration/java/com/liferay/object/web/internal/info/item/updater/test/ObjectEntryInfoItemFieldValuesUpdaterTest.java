/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.web.internal.info.item.updater.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.info.field.InfoField;
import com.liferay.info.field.InfoFieldValue;
import com.liferay.info.field.type.TextInfoFieldType;
import com.liferay.info.item.InfoItemFieldValues;
import com.liferay.info.item.updater.InfoItemFieldValuesUpdater;
import com.liferay.info.localized.InfoLocalizedValue;
import com.liferay.object.field.builder.TextObjectFieldBuilder;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.model.ObjectField;
import com.liferay.object.related.models.test.util.ObjectEntryTestUtil;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.test.util.ObjectDefinitionTestUtil;
import com.liferay.object.web.internal.info.item.BaseObjectEntryInfoItemTestCase;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.test.rule.Inject;

import java.io.Serializable;

import java.util.Collections;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Carolina Barbosa
 */
@RunWith(Arquillian.class)
public class ObjectEntryInfoItemFieldValuesUpdaterTest
	extends BaseObjectEntryInfoItemTestCase {

	@Test
	public void testUpdateFromInfoItemFieldValues() throws Exception {
		String name1 = RandomTestUtil.randomString();
		String name2 = RandomTestUtil.randomString();

		_updateFromInfoItemFieldValues(
			geInfoItemFieldValues(name1, name2), objectDefinition2,
			objectEntry2);

		assertObjectEntryValues(name1, name2);
	}

	@Test
	public void testUpdateFromInfoItemFieldValuesWithLocalizedObjectField()
		throws Exception {

		ObjectDefinition objectDefinition =
			ObjectDefinitionTestUtil.publishObjectDefinition(
				true,
				Collections.singletonList(
					new TextObjectFieldBuilder(
					).labelMap(
						RandomTestUtil.randomLocaleStringMap()
					).localized(
						true
					).name(
						"name"
					).build()));

		String enValue = RandomTestUtil.randomString();

		ObjectEntry objectEntry = ObjectEntryTestUtil.addObjectEntry(
			0, objectDefinition.getObjectDefinitionId(),
			HashMapBuilder.<String, Serializable>put(
				"name_i18n",
				HashMapBuilder.put(
					"en_US", enValue
				).build()
			).build());

		String ptValue = RandomTestUtil.randomString();

		_updateFromInfoItemFieldValues(
			InfoItemFieldValues.builder(
			).infoFieldValue(
				new InfoFieldValue<>(
					InfoField.builder(
					).infoFieldType(
						TextInfoFieldType.INSTANCE
					).namespace(
						ObjectField.class.getSimpleName()
					).name(
						"name"
					).localizable(
						true
					).build(),
					InfoLocalizedValue.builder(
					).value(
						LocaleUtil.BRAZIL, ptValue
					).build())
			).build(),
			objectDefinition, objectEntry);

		Map<String, Serializable> values = objectEntryLocalService.getValues(
			objectEntry.getObjectEntryId());

		Map<String, Object> localizedValues = (Map<String, Object>)values.get(
			"name_i18n");

		Assert.assertEquals(enValue, localizedValues.get("en_US"));
		Assert.assertEquals(ptValue, localizedValues.get("pt_BR"));

		_objectDefinitionLocalService.deleteObjectDefinition(objectDefinition);
	}

	private void _updateFromInfoItemFieldValues(
			InfoItemFieldValues infoItemFieldValues,
			ObjectDefinition objectDefinition, ObjectEntry objectEntry)
		throws Exception {

		InfoItemFieldValuesUpdater<ObjectEntry> infoItemFieldValuesUpdater =
			infoItemServiceRegistry.getFirstInfoItemService(
				InfoItemFieldValuesUpdater.class,
				objectDefinition.getClassName());

		infoItemFieldValuesUpdater.updateFromInfoItemFieldValues(
			objectEntry, infoItemFieldValues);
	}

	@Inject
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

}