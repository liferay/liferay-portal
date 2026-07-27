/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.batch.planner.rest.resource.v1_0.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.batch.planner.batch.engine.task.TaskItemUtil;
import com.liferay.batch.planner.rest.client.dto.v1_0.DepotScope;
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
public class DepotScopeResourceTest extends BaseDepotScopeResourceTestCase {

	@Override
	@Test
	public void testGetPlanInternalClassNameKeyDepotScopesPage()
		throws Exception {

		// Internal class name key not found

		String internalClassNameKey = RandomTestUtil.randomString();

		_testGetPlanInternalClassNameKeyDepotScopesPageNotFound(
			null, internalClassNameKey);
		_testGetPlanInternalClassNameKeyDepotScopesPageNotFound(
			false, internalClassNameKey);
		_testGetPlanInternalClassNameKeyDepotScopesPageNotFound(
			true, internalClassNameKey);

		internalClassNameKey = URLCodec.encodeURL(
			TaskItemUtil.getInternalClassNameKey(
				"com.liferay.object.rest.dto.v1_0.ObjectEntry",
				RandomTestUtil.randomString()));

		_testGetPlanInternalClassNameKeyDepotScopesPageNotFound(
			null, internalClassNameKey);
		_testGetPlanInternalClassNameKeyDepotScopesPageNotFound(
			false, internalClassNameKey);
		_testGetPlanInternalClassNameKeyDepotScopesPageNotFound(
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

		_testGetPlanInternalClassNameKeyDepotScopesPageEmpty(
			null, internalClassNameKey);
		_testGetPlanInternalClassNameKeyDepotScopesPageEmpty(
			false, internalClassNameKey);
		_testGetPlanInternalClassNameKeyDepotScopesPageEmpty(
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

		_testGetPlanInternalClassNameKeyDepotScopesPage(
			spaceDepotEntry.getGroupId(), null, internalClassNameKey,
			assetLibraryDepotEntry.getGroupId());
		_testGetPlanInternalClassNameKeyDepotScopesPage(
			spaceDepotEntry.getGroupId(), false, internalClassNameKey,
			assetLibraryDepotEntry.getGroupId());
		_testGetPlanInternalClassNameKeyDepotScopesPage(
			spaceDepotEntry.getGroupId(), true, internalClassNameKey,
			assetLibraryDepotEntry.getGroupId());

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

		_testGetPlanInternalClassNameKeyDepotScopesPageEmpty(
			null, internalClassNameKey);
		_testGetPlanInternalClassNameKeyDepotScopesPageEmpty(
			false, internalClassNameKey);
		_testGetPlanInternalClassNameKeyDepotScopesPageEmpty(
			true, internalClassNameKey);

		// Service builder entity (site scoped)

		internalClassNameKey =
			"com.liferay.headless.delivery.dto.v1_0.BlogPosting";

		_testGetPlanInternalClassNameKeyDepotScopesPageEmpty(
			null, internalClassNameKey);
		_testGetPlanInternalClassNameKeyDepotScopesPageEmpty(
			false, internalClassNameKey);
		_testGetPlanInternalClassNameKeyDepotScopesPageEmpty(
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

	private void _testGetPlanInternalClassNameKeyDepotScopesPage(
			long expectedGroupId, Boolean export, String internalClassNameKey,
			long unexpectedGroupId)
		throws Exception {

		Page<DepotScope> depotScopesPage =
			depotScopeResource.getPlanInternalClassNameKeyDepotScopesPage(
				internalClassNameKey, export);

		List<DepotScope> depotScopes = ListUtil.fromCollection(
			depotScopesPage.getItems());

		Assert.assertTrue(
			ListUtil.exists(
				depotScopes,
				depotScope -> Objects.equals(
					depotScope.getValue(), expectedGroupId)));
		Assert.assertFalse(
			ListUtil.exists(
				depotScopes,
				depotScope -> Objects.equals(
					depotScope.getValue(), unexpectedGroupId)));
	}

	private void _testGetPlanInternalClassNameKeyDepotScopesPageEmpty(
			Boolean export, String internalClassNameKey)
		throws Exception {

		Page<DepotScope> depotScopesPage =
			depotScopeResource.getPlanInternalClassNameKeyDepotScopesPage(
				internalClassNameKey, export);

		Assert.assertEquals(0, depotScopesPage.getTotalCount());
	}

	private void _testGetPlanInternalClassNameKeyDepotScopesPageNotFound(
			Boolean export, String internalClassNameKey)
		throws Exception {

		HttpInvoker.HttpResponse httpResponse =
			depotScopeResource.
				getPlanInternalClassNameKeyDepotScopesPageHttpResponse(
					internalClassNameKey, export);

		Assert.assertEquals(404, httpResponse.getStatusCode());
	}

	@Inject
	private DepotEntryLocalService _depotEntryLocalService;

}