/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.cmp.site.initializer.internal.fragment.entry.processor.analytics;

import com.liferay.fragment.entry.processor.helper.InfoItemFieldMapped;
import com.liferay.info.item.InfoItemReference;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.model.ObjectEntryFolder;
import com.liferay.object.rest.filter.factory.FilterFactory;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectEntryFolderLocalService;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.petra.sql.dsl.expression.Predicate;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import java.util.Collections;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.Mockito;

/**
 * @author Marcos Martins
 */
public class CMPAnalyticsAttributesContributorTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() {
		ReflectionTestUtil.setFieldValue(
			_cmpAnalyticsAttributesContributor, "_filterFactory",
			_filterFactory);
		ReflectionTestUtil.setFieldValue(
			_cmpAnalyticsAttributesContributor, "_groupLocalService",
			_groupLocalService);
		ReflectionTestUtil.setFieldValue(
			_cmpAnalyticsAttributesContributor, "_objectDefinitionLocalService",
			_objectDefinitionLocalService);
		ReflectionTestUtil.setFieldValue(
			_cmpAnalyticsAttributesContributor,
			"_objectEntryFolderLocalService", _objectEntryFolderLocalService);
		ReflectionTestUtil.setFieldValue(
			_cmpAnalyticsAttributesContributor, "_objectEntryLocalService",
			_objectEntryLocalService);
	}

	@FeatureFlag(enable = false, value = "LPD-58677")
	@Test
	public void testGetAnalyticsAttributesWhenCMPIsDisabled() throws Exception {
		Assert.assertEquals(
			Collections.emptyMap(),
			_cmpAnalyticsAttributesContributor.getAnalyticsAttributes(
				_getInfoItemFieldMapped(), LocaleUtil.US));

		Mockito.verifyNoInteractions(_objectDefinitionLocalService);
	}

	@FeatureFlag("LPD-58677")
	@Test
	public void testGetAnalyticsAttributesWhenObjectEntryIsNotCMSAsset()
		throws Exception {

		Mockito.when(
			_objectDefinitionLocalService.
				fetchObjectDefinitionByExternalReferenceCode(
					Mockito.eq("L_CMP_PROJECT"), Mockito.anyLong())
		).thenReturn(
			Mockito.mock(ObjectDefinition.class)
		);

		Assert.assertEquals(
			Collections.emptyMap(),
			_cmpAnalyticsAttributesContributor.getAnalyticsAttributes(
				_getInfoItemFieldMapped(
					_getObjectEntryFolder(RandomTestUtil.randomString())),
				LocaleUtil.US));

		Mockito.verifyNoInteractions(_groupLocalService);
		Mockito.verifyNoInteractions(_objectEntryLocalService);
	}

	@FeatureFlag("LPD-58677")
	@Test
	public void testGetAnalyticsAttributesWithoutCMPProjectObjectDefinition()
		throws Exception {

		Mockito.when(
			_objectDefinitionLocalService.
				fetchObjectDefinitionByExternalReferenceCode(
					Mockito.eq("L_CMP_PROJECT"), Mockito.anyLong())
		).thenReturn(
			null
		);

		Assert.assertEquals(
			Collections.emptyMap(),
			_cmpAnalyticsAttributesContributor.getAnalyticsAttributes(
				_getInfoItemFieldMapped(), LocaleUtil.US));

		Mockito.verifyNoInteractions(_groupLocalService);
		Mockito.verifyNoInteractions(_objectEntryFolderLocalService);
		Mockito.verifyNoInteractions(_objectEntryLocalService);
	}

	private InfoItemFieldMapped _getInfoItemFieldMapped() {
		return _getInfoItemFieldMapped(null);
	}

	private InfoItemFieldMapped _getInfoItemFieldMapped(
		ObjectEntryFolder objectEntryFolder) {

		ObjectEntry objectEntry = Mockito.mock(ObjectEntry.class);

		Mockito.when(
			objectEntry.getCompanyId()
		).thenReturn(
			RandomTestUtil.randomLong()
		);

		if (objectEntryFolder != null) {
			long objectEntryFolderId = RandomTestUtil.randomLong();

			Mockito.when(
				_objectEntryFolderLocalService.fetchObjectEntryFolder(
					objectEntryFolderId)
			).thenReturn(
				objectEntryFolder
			);

			Mockito.when(
				objectEntry.getObjectEntryFolderId()
			).thenReturn(
				objectEntryFolderId
			);
		}

		return new InfoItemFieldMapped(
			RandomTestUtil.randomString(),
			new InfoItemReference(
				RandomTestUtil.randomString(), RandomTestUtil.randomLong()),
			objectEntry);
	}

	private ObjectEntryFolder _getObjectEntryFolder(
		String externalReferenceCode) {

		ObjectEntryFolder objectEntryFolder = Mockito.mock(
			ObjectEntryFolder.class);

		Mockito.when(
			objectEntryFolder.getExternalReferenceCode()
		).thenReturn(
			externalReferenceCode
		);

		Mockito.when(
			objectEntryFolder.getTreePath()
		).thenReturn(
			StringPool.SLASH + RandomTestUtil.randomLong() + StringPool.SLASH
		);

		return objectEntryFolder;
	}

	private final CMPAnalyticsAttributesContributor
		_cmpAnalyticsAttributesContributor =
			new CMPAnalyticsAttributesContributor();
	private final FilterFactory<Predicate> _filterFactory = Mockito.mock(
		FilterFactory.class);
	private final GroupLocalService _groupLocalService = Mockito.mock(
		GroupLocalService.class);
	private final ObjectDefinitionLocalService _objectDefinitionLocalService =
		Mockito.mock(ObjectDefinitionLocalService.class);
	private final ObjectEntryFolderLocalService _objectEntryFolderLocalService =
		Mockito.mock(ObjectEntryFolderLocalService.class);
	private final ObjectEntryLocalService _objectEntryLocalService =
		Mockito.mock(ObjectEntryLocalService.class);

}