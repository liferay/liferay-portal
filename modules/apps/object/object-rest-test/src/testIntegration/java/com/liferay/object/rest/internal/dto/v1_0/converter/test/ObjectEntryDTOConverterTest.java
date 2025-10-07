/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.rest.internal.dto.v1_0.converter.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.list.type.entry.util.ListTypeEntryUtil;
import com.liferay.list.type.model.ListTypeDefinition;
import com.liferay.list.type.service.ListTypeDefinitionLocalService;
import com.liferay.object.constants.ObjectDefinitionConstants;
import com.liferay.object.constants.ObjectEntryFolderConstants;
import com.liferay.object.field.builder.PicklistObjectFieldBuilder;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.rest.dto.v1_0.ListEntry;
import com.liferay.object.rest.dto.v1_0.ObjectEntry;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.object.service.ObjectEntryVersionLocalService;
import com.liferay.object.test.util.ObjectDefinitionTestUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.portal.vulcan.dto.converter.DTOConverter;
import com.liferay.portal.vulcan.dto.converter.DTOConverterRegistry;
import com.liferay.portal.vulcan.dto.converter.DefaultDTOConverterContext;
import com.liferay.portal.vulcan.util.LocalizedMapUtil;

import java.io.Serializable;

import java.util.Arrays;
import java.util.Map;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Adolfo Pérez
 */
@RunWith(Arquillian.class)
public class ObjectEntryDTOConverterTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@FeatureFlag("LPD-17564")
	@Test
	public void testToDTO() throws Exception {
		ListTypeDefinition listTypeDefinition =
			_listTypeDefinitionLocalService.addListTypeDefinition(
				null, TestPropsValues.getUserId(),
				RandomTestUtil.randomLocaleStringMap(), false,
				Arrays.asList(
					ListTypeEntryUtil.createListTypeEntry("listTypeEntryKey1"),
					ListTypeEntryUtil.createListTypeEntry(
						"listTypeEntryKey2")));

		_objectDefinition = ObjectDefinitionTestUtil.publishObjectDefinition(
			false, false, true,
			ListUtil.fromArray(
				new PicklistObjectFieldBuilder(
				).labelMap(
					LocalizedMapUtil.getLocalizedMap(
						RandomTestUtil.randomString())
				).listTypeDefinitionId(
					listTypeDefinition.getListTypeDefinitionId()
				).name(
					"picklist"
				).build()),
			ObjectDefinitionConstants.SCOPE_SITE);

		com.liferay.object.model.ObjectEntry serviceBuilderObjectEntry =
			_objectEntryLocalService.addObjectEntry(
				TestPropsValues.getGroupId(), TestPropsValues.getUserId(),
				_objectDefinition.getObjectDefinitionId(),
				ObjectEntryFolderConstants.
					PARENT_OBJECT_ENTRY_FOLDER_ID_DEFAULT,
				null,
				HashMapBuilder.<String, Serializable>put(
					"picklist", "listTypeEntryKey1"
				).build(),
				ServiceContextTestUtil.getServiceContext(
					TestPropsValues.getGroupId()));

		ObjectEntry objectEntry = _toDTO(serviceBuilderObjectEntry);

		Map<String, Object> properties = objectEntry.getProperties();

		ListEntry listEntry = (ListEntry)properties.get("picklist");

		Assert.assertEquals("listTypeEntryKey1", listEntry.getKey());

		Group group = _groupLocalService.getGroup(
			serviceBuilderObjectEntry.getGroupId());

		Assert.assertEquals(
			group.getGroupId(), GetterUtil.getLong(objectEntry.getScopeId()));
		Assert.assertEquals(group.getGroupKey(), objectEntry.getScopeKey());
	}

	private ObjectEntry _toDTO(
			com.liferay.object.model.ObjectEntry serviceBuilderObjectEntry)
		throws Exception {

		DTOConverter<com.liferay.object.model.ObjectEntry, ObjectEntry>
			dtoConverter =
				(DTOConverter
					<com.liferay.object.model.ObjectEntry, ObjectEntry>)
						_dtoConverterRegistry.getDTOConverter(
							com.liferay.object.model.ObjectEntry.class.
								getName());

		DefaultDTOConverterContext dtoConverterContext =
			new DefaultDTOConverterContext(
				_dtoConverterRegistry,
				serviceBuilderObjectEntry.getObjectEntryId(),
				LocaleUtil.getDefault(), null, null);

		dtoConverterContext.setAttribute("objectDefinition", _objectDefinition);
		dtoConverterContext.setAttribute(
			"objectEntryVersion",
			_objectEntryVersionLocalService.getObjectEntryVersion(
				serviceBuilderObjectEntry.getObjectEntryId(), 1));

		return dtoConverter.toDTO(
			dtoConverterContext, serviceBuilderObjectEntry);
	}

	@Inject
	private DTOConverterRegistry _dtoConverterRegistry;

	@Inject
	private GroupLocalService _groupLocalService;

	@Inject
	private ListTypeDefinitionLocalService _listTypeDefinitionLocalService;

	@DeleteAfterTestRun
	private ObjectDefinition _objectDefinition;

	@Inject
	private ObjectEntryLocalService _objectEntryLocalService;

	@Inject
	private ObjectEntryVersionLocalService _objectEntryVersionLocalService;

}