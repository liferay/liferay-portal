/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.batch.planner.rest.resource.v1_0.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.batch.planner.batch.engine.task.TaskItemUtil;
import com.liferay.batch.planner.rest.client.dto.v1_0.AssetLibraryScope;
import com.liferay.batch.planner.rest.client.http.HttpInvoker;
import com.liferay.batch.planner.rest.client.pagination.Page;
import com.liferay.depot.constants.DepotConstants;
import com.liferay.depot.model.DepotEntry;
import com.liferay.depot.service.DepotEntryLocalService;
import com.liferay.object.constants.ObjectDefinitionConstants;
import com.liferay.object.field.util.ObjectFieldUtil;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.test.util.ObjectDefinitionTestUtil;
import com.liferay.portal.kernel.security.auth.PrincipalThreadLocal;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.URLCodec;
import com.liferay.portal.test.rule.Inject;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Matija Petanjek
 */
@RunWith(Arquillian.class)
public class AssetLibraryScopeResourceTest
	extends BaseAssetLibraryScopeResourceTestCase {

	@Override
	@Test
	public void testGetPlanInternalClassNameKeyAssetLibraryScopesPage()
		throws Exception {

		// Internal class name key not found

		String internalClassNameKey = RandomTestUtil.randomString();

		_testGetPlanInternalClassNameKeyAssetLibraryScopesPageNotFound(
			null, internalClassNameKey);
		_testGetPlanInternalClassNameKeyAssetLibraryScopesPageNotFound(
			false, internalClassNameKey);
		_testGetPlanInternalClassNameKeyAssetLibraryScopesPageNotFound(
			true, internalClassNameKey);

		internalClassNameKey = URLCodec.encodeURL(
			TaskItemUtil.getInternalClassNameKey(
				"com.liferay.object.rest.dto.v1_0.ObjectEntry",
				RandomTestUtil.randomString()));

		_testGetPlanInternalClassNameKeyAssetLibraryScopesPageNotFound(
			null, internalClassNameKey);
		_testGetPlanInternalClassNameKeyAssetLibraryScopesPageNotFound(
			false, internalClassNameKey);
		_testGetPlanInternalClassNameKeyAssetLibraryScopesPageNotFound(
			true, internalClassNameKey);

		// Object definition (company scoped)

		ObjectDefinition objectDefinition1 =
			ObjectDefinitionTestUtil.publishObjectDefinition(
				Collections.singletonList(
					ObjectFieldUtil.createObjectField(
						"Text", "String", true, true, null,
						RandomTestUtil.randomString(),
						"a" + RandomTestUtil.randomString(), false)),
				ObjectDefinitionConstants.SCOPE_COMPANY);

		internalClassNameKey = _getInternalClassNameKey(objectDefinition1);

		_testGetPlanInternalClassNameKeyAssetLibraryScopesPageEmpty(
			null, internalClassNameKey);
		_testGetPlanInternalClassNameKeyAssetLibraryScopesPageEmpty(
			false, internalClassNameKey);
		_testGetPlanInternalClassNameKeyAssetLibraryScopesPageEmpty(
			true, internalClassNameKey);

		// Object definition (depot scoped)

		DepotEntry assetLibraryDepotEntry = _addDepotEntry(
			DepotConstants.TYPE_ASSET_LIBRARY);
		DepotEntry spaceDepotEntry = _addDepotEntry(DepotConstants.TYPE_SPACE);

		ObjectDefinition objectDefinition2 =
			ObjectDefinitionTestUtil.publishObjectDefinition(
				Collections.singletonList(
					ObjectFieldUtil.createObjectField(
						"Text", "String", true, true, null,
						RandomTestUtil.randomString(),
						"a" + RandomTestUtil.randomString(), false)),
				ObjectDefinitionConstants.SCOPE_DEPOT);

		internalClassNameKey = _getInternalClassNameKey(objectDefinition2);

		long[] expectedGroupIds = {
			assetLibraryDepotEntry.getGroupId(), spaceDepotEntry.getGroupId()
		};

		_testGetPlanInternalClassNameKeyAssetLibraryScopesPage(
			expectedGroupIds, null, internalClassNameKey);
		_testGetPlanInternalClassNameKeyAssetLibraryScopesPage(
			expectedGroupIds, false, internalClassNameKey);
		_testGetPlanInternalClassNameKeyAssetLibraryScopesPage(
			expectedGroupIds, true, internalClassNameKey);

		// Object definition (site scoped)

		ObjectDefinition objectDefinition3 =
			ObjectDefinitionTestUtil.publishObjectDefinition(
				Collections.singletonList(
					ObjectFieldUtil.createObjectField(
						"Text", "String", true, true, null,
						RandomTestUtil.randomString(),
						"a" + RandomTestUtil.randomString(), false)),
				ObjectDefinitionConstants.SCOPE_SITE);

		internalClassNameKey = _getInternalClassNameKey(objectDefinition3);

		_testGetPlanInternalClassNameKeyAssetLibraryScopesPageEmpty(
			null, internalClassNameKey);
		_testGetPlanInternalClassNameKeyAssetLibraryScopesPageEmpty(
			false, internalClassNameKey);
		_testGetPlanInternalClassNameKeyAssetLibraryScopesPageEmpty(
			true, internalClassNameKey);

		// Service builder entity (site scoped)

		internalClassNameKey =
			"com.liferay.headless.delivery.dto.v1_0.BlogPosting";

		_testGetPlanInternalClassNameKeyAssetLibraryScopesPageEmpty(
			null, internalClassNameKey);
		_testGetPlanInternalClassNameKeyAssetLibraryScopesPageEmpty(
			false, internalClassNameKey);
		_testGetPlanInternalClassNameKeyAssetLibraryScopesPageEmpty(
			true, internalClassNameKey);
	}

	@Override
	protected String[] getAdditionalAssertFieldNames() {
		return new String[] {"label", "value"};
	}

	private DepotEntry _addDepotEntry(int type) throws Exception {
		String name = PrincipalThreadLocal.getName();

		PrincipalThreadLocal.setName(TestPropsValues.getUserId());

		try {
			return _depotEntryLocalService.addDepotEntry(
				HashMapBuilder.put(
					LocaleUtil.getDefault(), RandomTestUtil.randomString()
				).build(),
				HashMapBuilder.put(
					LocaleUtil.getDefault(), RandomTestUtil.randomString()
				).build(),
				type, ServiceContextTestUtil.getServiceContext());
		}
		finally {
			PrincipalThreadLocal.setName(name);
		}
	}

	private String _getInternalClassNameKey(ObjectDefinition objectDefinition) {
		return URLCodec.encodeURL(
			TaskItemUtil.getInternalClassNameKey(
				"com.liferay.object.rest.dto.v1_0.ObjectEntry",
				objectDefinition.getName()));
	}

	private void _testGetPlanInternalClassNameKeyAssetLibraryScopesPage(
			long[] expectedGroupIds, Boolean export,
			String internalClassNameKey)
		throws Exception {

		Page<AssetLibraryScope> assetLibraryScopesPage =
			assetLibraryScopeResource.
				getPlanInternalClassNameKeyAssetLibraryScopesPage(
					internalClassNameKey, export);

		List<AssetLibraryScope> assetLibraryScopes = ListUtil.fromCollection(
			assetLibraryScopesPage.getItems());

		for (long expectedGroupId : expectedGroupIds) {
			Assert.assertTrue(
				ListUtil.exists(
					assetLibraryScopes,
					assetLibraryScope -> Objects.equals(
						assetLibraryScope.getValue(), expectedGroupId)));
		}
	}

	private void _testGetPlanInternalClassNameKeyAssetLibraryScopesPageEmpty(
			Boolean export, String internalClassNameKey)
		throws Exception {

		Page<AssetLibraryScope> assetLibraryScopesPage =
			assetLibraryScopeResource.
				getPlanInternalClassNameKeyAssetLibraryScopesPage(
					internalClassNameKey, export);

		Assert.assertEquals(0, assetLibraryScopesPage.getTotalCount());
	}

	private void _testGetPlanInternalClassNameKeyAssetLibraryScopesPageNotFound(
			Boolean export, String internalClassNameKey)
		throws Exception {

		HttpInvoker.HttpResponse httpResponse =
			assetLibraryScopeResource.
				getPlanInternalClassNameKeyAssetLibraryScopesPageHttpResponse(
					internalClassNameKey, export);

		Assert.assertEquals(404, httpResponse.getStatusCode());
	}

	@Inject
	private DepotEntryLocalService _depotEntryLocalService;

}