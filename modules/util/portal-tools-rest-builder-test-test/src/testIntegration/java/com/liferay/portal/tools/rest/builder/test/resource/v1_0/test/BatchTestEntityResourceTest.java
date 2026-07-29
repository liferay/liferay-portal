/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.tools.rest.builder.test.resource.v1_0.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.oauth2.provider.scope.ScopeChecker;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.ResourceActionLocalService;
import com.liferay.portal.kernel.service.ResourcePermissionLocalService;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.test.util.HTTPTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.Http;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.tools.rest.builder.test.client.dto.v1_0.BatchTestEntity;
import com.liferay.portal.vulcan.accept.language.AcceptLanguage;
import com.liferay.portal.vulcan.crud.VulcanCRUDItemDelegate;
import com.liferay.portal.vulcan.crud.VulcanCRUDItemDelegateBuilderRegistry;
import com.liferay.portal.vulcan.fields.NestedFieldsContext;
import com.liferay.portal.vulcan.fields.NestedFieldsContextThreadLocal;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

/**
 * @author Alejandro Tardín
 */
@RunWith(Arquillian.class)
public class BatchTestEntityResourceTest
	extends BaseBatchTestEntityResourceTestCase {

	@Override
	@Test
	public void testGetBatchTestEntity() throws Exception {
		super.testGetBatchTestEntity();

		BatchTestEntity postBatchTestEntity =
			batchTestEntityResource.postBatchTestEntity(
				randomBatchTestEntity());

		BatchTestEntity batchTestEntity =
			batchTestEntityResource.getBatchTestEntity(
				postBatchTestEntity.getId());

		Assert.assertNull(batchTestEntity.getEmbeddedNestedField());

		JSONObject batchTestEntityJSONObject = _getBatchTestEntityJSONObject(
			postBatchTestEntity.getId(), "nestedFields=embeddedNestedField");

		Assert.assertEquals(
			postBatchTestEntity.getName(),
			JSONUtil.getValueAsString(
				batchTestEntityJSONObject, "JSONObject/embeddedNestedField",
				"Object/name"));
		Assert.assertNull(
			JSONUtil.getValue(
				batchTestEntityJSONObject, "JSONObject/embeddedNestedField",
				"Object/nestedField2"));

		batchTestEntityJSONObject = _getBatchTestEntityJSONObject(
			postBatchTestEntity.getId(),
			"nestedFields=embeddedNestedField.nestedField2");

		Assert.assertNull(
			JSONUtil.getValue(
				batchTestEntityJSONObject, "JSONObject/embeddedNestedField"));

		batchTestEntityJSONObject = _getBatchTestEntityJSONObject(
			postBatchTestEntity.getId(),
			"nestedFields=embeddedNestedField,embeddedNestedField." +
				"nestedField2");

		Assert.assertEquals(
			postBatchTestEntity.getName(),
			JSONUtil.getValueAsString(
				batchTestEntityJSONObject, "JSONObject/embeddedNestedField",
				"Object/name"));
		Assert.assertNull(
			JSONUtil.getValue(
				batchTestEntityJSONObject, "JSONObject/embeddedNestedField",
				"Object/nestedField2"));

		batchTestEntityJSONObject = _getBatchTestEntityJSONObject(
			postBatchTestEntity.getId(),
			"nestedFields=embeddedNestedField,embeddedNestedField." +
				"nestedField2&nestedFieldsDepth=2");

		Assert.assertEquals(
			postBatchTestEntity.getName(),
			JSONUtil.getValueAsString(
				batchTestEntityJSONObject, "JSONObject/embeddedNestedField",
				"Object/name"));
		Assert.assertEquals(
			"nestedField2-" + postBatchTestEntity.getId(),
			JSONUtil.getValueAsString(
				batchTestEntityJSONObject, "JSONObject/embeddedNestedField",
				"Object/nestedField2"));
	}

	@Override
	@Test
	public void testVulcanCRUDItemDelegateGetItem() throws Exception {
		super.testVulcanCRUDItemDelegateGetItem();

		BatchTestEntity batchTestEntity = randomBatchTestEntity();

		BatchTestEntity postBatchTestEntity =
			batchTestEntityResource.postBatchTestEntity(batchTestEntity);

		VulcanCRUDItemDelegate vulcanCRUDItemDelegate =
			_buildVulcanCRUDItemDelegate();

		com.liferay.portal.tools.rest.builder.test.dto.v1_0.BatchTestEntity
			batchTestEntity1 =
				(com.liferay.portal.tools.rest.builder.test.dto.v1_0.
					BatchTestEntity)vulcanCRUDItemDelegate.fetchItem(
						postBatchTestEntity.getId());

		Assert.assertNull(batchTestEntity1.getNestedField1());
		Assert.assertNull(batchTestEntity1.getNestedField2());

		com.liferay.portal.tools.rest.builder.test.dto.v1_0.BatchTestEntity
			batchTestEntity2 =
				(com.liferay.portal.tools.rest.builder.test.dto.v1_0.
					BatchTestEntity)vulcanCRUDItemDelegate.getItem(
						postBatchTestEntity.getId());

		Assert.assertNull(batchTestEntity2.getNestedField1());
		Assert.assertNull(batchTestEntity2.getNestedField2());

		try (SafeCloseable safeCloseable =
				NestedFieldsContextThreadLocal.
					setNestedFieldsContextWithSafeCloseable(
						new NestedFieldsContext(
							1, Arrays.asList("nestedField1", "nestedField2"),
							"v1.0"))) {

			vulcanCRUDItemDelegate = _buildVulcanCRUDItemDelegate();

			com.liferay.portal.tools.rest.builder.test.dto.v1_0.BatchTestEntity
				batchTestEntity3 =
					(com.liferay.portal.tools.rest.builder.test.dto.v1_0.
						BatchTestEntity)vulcanCRUDItemDelegate.fetchItem(
							postBatchTestEntity.getId());

			Assert.assertEquals(
				batchTestEntity.getNestedField1(),
				batchTestEntity3.getNestedField1());
			Assert.assertEquals(
				"nestedField2-" + postBatchTestEntity.getId(),
				batchTestEntity3.getNestedField2());

			com.liferay.portal.tools.rest.builder.test.dto.v1_0.BatchTestEntity
				batchTestEntity4 =
					(com.liferay.portal.tools.rest.builder.test.dto.v1_0.
						BatchTestEntity)vulcanCRUDItemDelegate.getItem(
							postBatchTestEntity.getId());

			Assert.assertEquals(
				batchTestEntity.getNestedField1(),
				batchTestEntity4.getNestedField1());
			Assert.assertEquals(
				"nestedField2-" + postBatchTestEntity.getId(),
				batchTestEntity4.getNestedField2());
		}
	}

	@Override
	protected BatchTestEntity
			testBatchEngineDeleteImportTask_addBatchTestEntity()
		throws Exception {

		return batchTestEntityResource.postBatchTestEntity(
			randomBatchTestEntity());
	}

	@Override
	protected BatchTestEntity
			testDeleteBatchTestEntityByExternalReferenceCode_addBatchTestEntity()
		throws Exception {

		return batchTestEntityResource.postBatchTestEntity(
			randomBatchTestEntity());
	}

	@Override
	protected BatchTestEntity testGetBatchTestEntitiesPage_addBatchTestEntity(
			BatchTestEntity batchTestEntity)
		throws Exception {

		return batchTestEntityResource.postBatchTestEntity(
			randomBatchTestEntity());
	}

	@Override
	protected BatchTestEntity testGetBatchTestEntity_addBatchTestEntity()
		throws Exception {

		return batchTestEntityResource.postBatchTestEntity(
			randomBatchTestEntity());
	}

	@Override
	protected BatchTestEntity
			testGetBatchTestEntityByExternalReferenceCode_addBatchTestEntity()
		throws Exception {

		return batchTestEntityResource.postBatchTestEntity(
			randomBatchTestEntity());
	}

	@Override
	protected BatchTestEntity testPostBatchTestEntity_addBatchTestEntity(
			BatchTestEntity batchTestEntity)
		throws Exception {

		return batchTestEntityResource.postBatchTestEntity(
			randomBatchTestEntity());
	}

	@Override
	protected BatchTestEntity
			testPutBatchTestEntityByExternalReferenceCode_addBatchTestEntity()
		throws Exception {

		return batchTestEntityResource.postBatchTestEntity(
			randomBatchTestEntity());
	}

	private VulcanCRUDItemDelegate _buildVulcanCRUDItemDelegate()
		throws Exception {

		return _vulcanCRUDItemDelegateBuilderRegistry.builder(
			testCompany,
			"com.liferay.portal.tools.rest.builder.test.dto.v1_0." +
				"BatchTestEntity"
		).acceptLanguage(
			new AcceptLanguage() {

				@Override
				public List<Locale> getLocales() {
					return Arrays.asList(LocaleUtil.getDefault());
				}

				@Override
				public String getPreferredLanguageId() {
					return LocaleUtil.toLanguageId(LocaleUtil.getDefault());
				}

				@Override
				public Locale getPreferredLocale() {
					return LocaleUtil.getDefault();
				}

			}
		).groupLocalService(
			_groupLocalService
		).httpServletRequest(
			new MockHttpServletRequest() {

				@Override
				public StringBuffer getRequestURL() {
					return new StringBuffer(
						StringBundler.concat(
							"http://localhost:",
							PortalUtil.getPortalServerPort(false), "/o/v1.0/",
							RandomTestUtil.randomString(), "/",
							RandomTestUtil.randomString()));
				}

			}
		).httpServletResponse(
			new MockHttpServletResponse()
		).resourceActionLocalService(
			_resourceActionLocalService
		).resourcePermissionLocalService(
			_resourcePermissionLocalService
		).roleLocalService(
			_roleLocalService
		).scopeChecker(
			_scopeChecker
		).uriInfo(
			testVulcanCRUDItemDelegate_getUriInfo()
		).user(
			testVulcanCRUDItemDelegate_getUser()
		).build();
	}

	private JSONObject _getBatchTestEntityJSONObject(
			Long batchTestEntityId, String queryString)
		throws Exception {

		return HTTPTestUtil.invokeToJSONObject(
			null,
			StringBundler.concat(
				"portal-tools-rest-builder-test/v1.0/batch-test-entities/",
				batchTestEntityId, "?", queryString),
			Http.Method.GET);
	}

	@Inject
	private GroupLocalService _groupLocalService;

	@Inject
	private ResourceActionLocalService _resourceActionLocalService;

	@Inject
	private ResourcePermissionLocalService _resourcePermissionLocalService;

	@Inject
	private RoleLocalService _roleLocalService;

	@Inject
	private ScopeChecker _scopeChecker;

	@Inject
	private VulcanCRUDItemDelegateBuilderRegistry
		_vulcanCRUDItemDelegateBuilderRegistry;

}