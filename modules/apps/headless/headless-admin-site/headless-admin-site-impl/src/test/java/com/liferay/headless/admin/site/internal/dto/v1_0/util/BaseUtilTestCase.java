/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.site.internal.dto.v1_0.util;

import com.liferay.info.item.InfoItemDetails;
import com.liferay.info.item.InfoItemIdentifier;
import com.liferay.info.item.InfoItemReference;
import com.liferay.info.item.InfoItemServiceRegistry;
import com.liferay.info.item.provider.InfoItemDetailsProvider;
import com.liferay.info.item.provider.InfoItemObjectProvider;
import com.liferay.portal.json.JSONFactoryImpl;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.GroupConstants;
import com.liferay.portal.kernel.service.GroupLocalServiceUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.PortalUtil;

import org.junit.AfterClass;
import org.junit.BeforeClass;

import org.mockito.MockedStatic;
import org.mockito.Mockito;

/**
 * @author Lourdes Fernández Besada
 */
public abstract class BaseUtilTestCase {

	@BeforeClass
	public static void setUpClassBaseUtilTestCase() {
		JSONFactoryUtil jsonFactoryUtil = new JSONFactoryUtil();

		jsonFactoryUtil.setJSONFactory(new JSONFactoryImpl());

		_mockGroup(
			ITEM_GROUP_EXTERNAL_REFERENCE_CODE, ITEM_GROUP_ID,
			GroupConstants.TYPE_DEPOT);
		_mockGroup(
			SCOPE_EXTERNAL_REFERENCE_CODE, SCOPE_GROUP_ID,
			GroupConstants.TYPE_SITE_OPEN);

		portalUtilMockedStatic.when(
			() -> PortalUtil.getClassNameId(Mockito.anyString())
		).thenReturn(
			CLASS_NAME_ID
		);
	}

	@AfterClass
	public static void tearDownClassBaseUtilTestCase() {
		_groupLocalServiceUtilMockedStatic.close();
		portalUtilMockedStatic.close();
	}

	protected static InfoItemReference mockInfoItemReference()
		throws Exception {

		InfoItemReference infoItemReference = Mockito.mock(
			InfoItemReference.class);

		InfoItemDetailsProvider<Object> infoItemDetailsProvider = Mockito.mock(
			InfoItemDetailsProvider.class);

		InfoItemDetails infoItemDetails = Mockito.mock(InfoItemDetails.class);

		Mockito.when(
			infoItemDetails.getInfoItemReference()
		).thenReturn(
			infoItemReference
		);

		Mockito.when(
			infoItemDetailsProvider.getInfoItemDetails(
				Mockito.eq(SCOPE_GROUP_ID), Mockito.any(Class.class),
				Mockito.any())
		).thenReturn(
			infoItemDetails
		);

		Mockito.when(
			infoItemServiceRegistry.getFirstInfoItemService(
				Mockito.eq(InfoItemDetailsProvider.class), Mockito.any(),
				Mockito.any())
		).thenReturn(
			infoItemDetailsProvider
		);

		InfoItemObjectProvider<Object> infoItemObjectProvider = Mockito.mock(
			InfoItemObjectProvider.class);

		Mockito.when(
			infoItemObjectProvider.getInfoItem(
				Mockito.eq(SCOPE_GROUP_ID),
				Mockito.any(InfoItemIdentifier.class))
		).thenReturn(
			Mockito.mock(Object.class)
		);

		Mockito.when(
			infoItemServiceRegistry.getFirstInfoItemService(
				Mockito.eq(InfoItemObjectProvider.class), Mockito.any(),
				Mockito.any())
		).thenReturn(
			infoItemObjectProvider
		);

		return infoItemReference;
	}

	protected static final long CLASS_NAME_ID = RandomTestUtil.randomLong();

	protected static final long COMPANY_ID = RandomTestUtil.randomLong();

	protected static final String ITEM_GROUP_EXTERNAL_REFERENCE_CODE =
		RandomTestUtil.randomString();

	protected static final long ITEM_GROUP_ID = RandomTestUtil.randomLong();

	protected static final String SCOPE_EXTERNAL_REFERENCE_CODE =
		RandomTestUtil.randomString();

	protected static final long SCOPE_GROUP_ID = RandomTestUtil.randomLong();

	protected static final InfoItemServiceRegistry infoItemServiceRegistry =
		Mockito.mock(InfoItemServiceRegistry.class);
	protected static final MockedStatic<PortalUtil> portalUtilMockedStatic =
		Mockito.mockStatic(PortalUtil.class);

	private static void _mockGroup(
		String externalReferenceCode, long groupId, int type) {

		Group group = Mockito.mock(Group.class);

		Mockito.when(
			group.getExternalReferenceCode()
		).thenReturn(
			externalReferenceCode
		);

		Mockito.when(
			group.getGroupId()
		).thenReturn(
			groupId
		);

		Mockito.when(
			group.getType()
		).thenReturn(
			type
		);

		Mockito.when(
			group.isDepot()
		).thenReturn(
			GroupConstants.TYPE_DEPOT == type
		);

		_groupLocalServiceUtilMockedStatic.when(
			() -> GroupLocalServiceUtil.fetchGroup(groupId)
		).thenReturn(
			group
		);

		_groupLocalServiceUtilMockedStatic.when(
			() -> GroupLocalServiceUtil.fetchGroupByExternalReferenceCode(
				externalReferenceCode, COMPANY_ID)
		).thenReturn(
			group
		);

		_groupLocalServiceUtilMockedStatic.when(
			() -> GroupLocalServiceUtil.getGroup(groupId)
		).thenReturn(
			group
		);

		_groupLocalServiceUtilMockedStatic.when(
			() -> GroupLocalServiceUtil.getGroupByExternalReferenceCode(
				externalReferenceCode, COMPANY_ID)
		).thenReturn(
			group
		);
	}

	private static final MockedStatic<GroupLocalServiceUtil>
		_groupLocalServiceUtilMockedStatic = Mockito.mockStatic(
			GroupLocalServiceUtil.class);

}