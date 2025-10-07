/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.list.type.resource.v1_0.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.headless.admin.list.type.client.dto.v1_0.ListTypeDefinition;
import com.liferay.headless.admin.list.type.client.dto.v1_0.ListTypeEntry;
import com.liferay.list.type.service.ListTypeDefinitionLocalService;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.odata.entity.EntityField;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.vulcan.util.LocalizedMapUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Gabriel Albuquerque
 */
@RunWith(Arquillian.class)
public class ListTypeDefinitionResourceTest
	extends BaseListTypeDefinitionResourceTestCase {

	@Override
	@Test
	public void testGetListTypeDefinitionsPageWithSortInteger()
		throws Exception {

		testGetListTypeDefinitionsPageWithSort(
			EntityField.Type.INTEGER,
			(entityField, listTypeDefinition1, listTypeDefinition2) -> {
				if (BeanTestUtil.hasProperty(
						listTypeDefinition1, entityField.getName())) {

					BeanTestUtil.setProperty(
						listTypeDefinition1, entityField.getName(), 0);
				}

				if (BeanTestUtil.hasProperty(
						listTypeDefinition2, entityField.getName())) {

					BeanTestUtil.setProperty(
						listTypeDefinition2, entityField.getName(), 1);
				}
			});
	}

	@Override
	@Test
	public void testPostListTypeDefinition() throws Exception {
		super.testPostListTypeDefinition();

		ListTypeDefinition randomListTypeDefinition =
			randomListTypeDefinition();

		randomListTypeDefinition.setDefaultLanguageId("pt_BR");
		randomListTypeDefinition.setName_i18n(
			Collections.singletonMap("pt_BR", RandomTestUtil.randomString()));
		randomListTypeDefinition.setSystem(true);

		ListTypeDefinition postListTypeDefinition =
			testPostListTypeDefinition_addListTypeDefinition(
				randomListTypeDefinition);

		assertEquals(randomListTypeDefinition, postListTypeDefinition);
		assertValid(postListTypeDefinition);

		randomListTypeDefinition = randomListTypeDefinition();

		randomListTypeDefinition.setName(RandomTestUtil.randomString());
		randomListTypeDefinition.setName_i18n((Map<String, String>)null);

		_assertListTypeDefinitionNameLocalizedMap(
			testPostListTypeDefinition_addListTypeDefinition(
				randomListTypeDefinition));
	}

	@Override
	@Test
	public void testPutListTypeDefinition() throws Exception {
		super.testPutListTypeDefinition();

		ListTypeDefinition listTypeDefinition =
			testPutListTypeDefinition_addListTypeDefinition();

		listTypeDefinition.setName(RandomTestUtil.randomString());
		listTypeDefinition.setName_i18n((Map<String, String>)null);

		_assertListTypeDefinitionNameLocalizedMap(
			listTypeDefinitionResource.putListTypeDefinition(
				listTypeDefinition.getId(), listTypeDefinition));
	}

	@Override
	protected ListTypeDefinition randomListTypeDefinition() throws Exception {
		ListTypeDefinition listTypeDefinition =
			super.randomListTypeDefinition();

		listTypeDefinition.setName_i18n(
			Collections.singletonMap("en-US", RandomTestUtil.randomString()));
		listTypeDefinition.setSystem(false);

		ListTypeEntry listTypeEntry = new ListTypeEntry();

		listTypeEntry.setName_i18n(
			Collections.singletonMap("en-US", RandomTestUtil.randomString()));
		listTypeEntry.setKey(RandomTestUtil.randomString());

		listTypeDefinition.setListTypeEntries(
			new ListTypeEntry[] {listTypeEntry});

		return listTypeDefinition;
	}

	@Override
	protected ListTypeDefinition
			testDeleteListTypeDefinition_addListTypeDefinition()
		throws Exception {

		return _addListTypeDefinition(randomListTypeDefinition());
	}

	@Override
	protected ListTypeDefinition
			testGetListTypeDefinition_addListTypeDefinition()
		throws Exception {

		return _addListTypeDefinition(randomListTypeDefinition());
	}

	@Override
	protected ListTypeDefinition
			testGetListTypeDefinitionByExternalReferenceCode_addListTypeDefinition()
		throws Exception {

		return _addListTypeDefinition(randomListTypeDefinition());
	}

	@Override
	protected ListTypeDefinition
			testGetListTypeDefinitionsPage_addListTypeDefinition(
				ListTypeDefinition listTypeDefinition)
		throws Exception {

		return _addListTypeDefinition(listTypeDefinition);
	}

	@Override
	protected ListTypeDefinition
			testGraphQLListTypeDefinition_addListTypeDefinition()
		throws Exception {

		return _addListTypeDefinition(randomListTypeDefinition());
	}

	@Override
	protected ListTypeDefinition
			testGraphQLListTypeDefinition_addListTypeDefinition(
				ListTypeDefinition listTypeDefinition)
		throws Exception {

		return _addListTypeDefinition(listTypeDefinition);
	}

	@Override
	protected ListTypeDefinition
			testPatchListTypeDefinition_addListTypeDefinition()
		throws Exception {

		return _addListTypeDefinition(randomListTypeDefinition());
	}

	@Override
	protected ListTypeDefinition
			testPostListTypeDefinition_addListTypeDefinition(
				ListTypeDefinition listTypeDefinition)
		throws Exception {

		return _addListTypeDefinition(listTypeDefinition);
	}

	@Override
	protected ListTypeDefinition
			testPutListTypeDefinition_addListTypeDefinition()
		throws Exception {

		return _addListTypeDefinition(randomListTypeDefinition());
	}

	@Override
	protected ListTypeDefinition
			testPutListTypeDefinitionByExternalReferenceCode_addListTypeDefinition()
		throws Exception {

		return _addListTypeDefinition(randomListTypeDefinition());
	}

	@Override
	protected ListTypeDefinition
			testPutListTypeDefinitionByExternalReferenceCode_createListTypeDefinition()
		throws Exception {

		return _addListTypeDefinition(randomListTypeDefinition());
	}

	private ListTypeDefinition _addListTypeDefinition(
			ListTypeDefinition listTypeDefinition)
		throws Exception {

		listTypeDefinition = listTypeDefinitionResource.postListTypeDefinition(
			listTypeDefinition);

		_listTypeDefinitions.add(
			_listTypeDefinitionLocalService.fetchListTypeDefinition(
				listTypeDefinition.getId()));

		return listTypeDefinition;
	}

	private void _assertListTypeDefinitionNameLocalizedMap(
		ListTypeDefinition listTypeDefinition) {

		Map<Locale, String> nameLocalizedMap = LocalizedMapUtil.getLocalizedMap(
			listTypeDefinition.getName_i18n());

		Assert.assertEquals(
			listTypeDefinition.getName(),
			nameLocalizedMap.get(LocaleUtil.getSiteDefault()));
	}

	@Inject
	private ListTypeDefinitionLocalService _listTypeDefinitionLocalService;

	@DeleteAfterTestRun
	private List<com.liferay.list.type.model.ListTypeDefinition>
		_listTypeDefinitions = new ArrayList<>();

}