/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.change.tracking.rest.resource.v1_0.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.change.tracking.exception.CTCollectionStatusException;
import com.liferay.change.tracking.model.CTPreferences;
import com.liferay.change.tracking.rest.client.dto.v1_0.CTCollection;
import com.liferay.change.tracking.rest.client.dto.v1_0.Status;
import com.liferay.change.tracking.rest.client.http.HttpInvoker;
import com.liferay.change.tracking.service.CTCollectionLocalService;
import com.liferay.change.tracking.service.CTPreferencesLocalService;
import com.liferay.dynamic.data.mapping.test.util.DDMStructureTestUtil;
import com.liferay.journal.model.JournalArticle;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.change.tracking.CTCollectionThreadLocal;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.service.ClassNameLocalService;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DataGuard;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import jakarta.ws.rs.core.Response;

import java.util.Date;
import java.util.Map;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author David Truong
 */
@DataGuard(scope = DataGuard.Scope.METHOD)
@RunWith(Arquillian.class)
public class CTCollectionResourceTest extends BaseCTCollectionResourceTestCase {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Ignore
	@Override
	@Test
	public void testDeleteCTCollectionBatch() throws Exception {
		super.testDeleteCTCollectionBatch();
	}

	@Override
	@Test
	public void testGetCTCollection() throws Exception {
		super.testGetCTCollection();

		CTCollection ctCollection = ctCollectionResource.postCTCollection(
			randomCTCollection());

		com.liferay.change.tracking.model.CTCollection
			serviceBuilderCTCollection =
				_ctCollectionLocalService.getCTCollection(ctCollection.getId());

		serviceBuilderCTCollection.setStatus(WorkflowConstants.STATUS_EXPIRED);

		_ctCollectionLocalService.updateCTCollection(
			serviceBuilderCTCollection);

		ctCollection = ctCollectionResource.getCTCollection(
			ctCollection.getId());

		Map<String, Map<String, String>> actions = ctCollection.getActions();

		Assert.assertEquals(actions.toString(), 3, actions.size());

		Assert.assertTrue(actions.containsKey("delete"));
		Assert.assertTrue(actions.containsKey("get"));
		Assert.assertTrue(actions.containsKey("reactivate"));
	}

	@Override
	@Test
	public void testGetCTCollectionByExternalReferenceCodeShareLink()
		throws Exception {

		CTCollection ctCollection = ctCollectionResource.postCTCollection(
			randomCTCollection());

		Assert.assertEquals(
			StringPool.BLANK,
			ctCollectionResource.
				getCTCollectionByExternalReferenceCodeShareLink(
					ctCollection.getExternalReferenceCode()));

		com.liferay.change.tracking.model.CTCollection
			serviceBuilderCTCollection =
				_ctCollectionLocalService.getCTCollection(ctCollection.getId());

		serviceBuilderCTCollection.setShareable(true);

		_ctCollectionLocalService.updateCTCollection(
			serviceBuilderCTCollection);

		Assert.assertNotEquals(
			StringPool.BLANK,
			ctCollectionResource.
				getCTCollectionByExternalReferenceCodeShareLink(
					ctCollection.getExternalReferenceCode()));
	}

	@Override
	@Test
	public void testGetCTCollectionShareLink() throws Exception {
		CTCollection ctCollection = ctCollectionResource.postCTCollection(
			randomCTCollection());

		Assert.assertEquals(
			StringPool.BLANK,
			ctCollectionResource.getCTCollectionShareLink(
				ctCollection.getId()));

		com.liferay.change.tracking.model.CTCollection
			serviceBuilderCTCollection =
				_ctCollectionLocalService.getCTCollection(ctCollection.getId());

		serviceBuilderCTCollection.setShareable(true);

		_ctCollectionLocalService.updateCTCollection(
			serviceBuilderCTCollection);

		Assert.assertNotEquals(
			StringPool.BLANK,
			ctCollectionResource.getCTCollectionShareLink(
				ctCollection.getId()));
	}

	@Override
	@Test
	public void testPostCTCollectionByExternalReferenceCodePublish()
		throws Exception {

		CTCollection ctCollection =
			testPostCTCollectionByExternalReferenceCodePublish_addCTCollection();

		try (SafeCloseable safeCloseable =
				CTCollectionThreadLocal.setCTCollectionIdWithSafeCloseable(
					ctCollection.getId())) {

			DDMStructureTestUtil.addStructure(
				TestPropsValues.getGroupId(), JournalArticle.class.getName());
		}

		assertHttpResponseStatusCode(
			Response.Status.NO_CONTENT.getStatusCode(),
			ctCollectionResource.
				postCTCollectionByExternalReferenceCodePublishHttpResponse(
					ctCollection.getExternalReferenceCode()));

		ctCollection =
			ctCollectionResource.getCTCollectionByExternalReferenceCode(
				ctCollection.getExternalReferenceCode());

		Status status = ctCollection.getStatus();

		Assert.assertEquals(
			WorkflowConstants.STATUS_APPROVED, (int)status.getCode());

		_assertHttpResponseProblem(
			CTCollectionStatusException.class,
			ctCollectionResource.
				postCTCollectionByExternalReferenceCodePublishHttpResponse(
					ctCollection.getExternalReferenceCode()));
	}

	@Override
	@Test
	public void testPostCTCollectionByExternalReferenceCodeSchedulePublish()
		throws Exception {

		CTCollection ctCollection =
			testPostCTCollectionByExternalReferenceCodeSchedulePublish_addCTCollection();

		_assertHttpResponseProblem(
			IllegalArgumentException.class,
			ctCollectionResource.
				postCTCollectionByExternalReferenceCodeSchedulePublishHttpResponse(
					ctCollection.getExternalReferenceCode(),
					new Date(
						System.currentTimeMillis() -
							RandomTestUtil.randomInt())));

		assertHttpResponseStatusCode(
			Response.Status.NO_CONTENT.getStatusCode(),
			ctCollectionResource.
				postCTCollectionByExternalReferenceCodeSchedulePublishHttpResponse(
					ctCollection.getExternalReferenceCode(),
					new Date(
						System.currentTimeMillis() +
							RandomTestUtil.randomInt())));

		ctCollection =
			ctCollectionResource.getCTCollectionByExternalReferenceCode(
				ctCollection.getExternalReferenceCode());

		Status status = ctCollection.getStatus();

		Assert.assertEquals(
			WorkflowConstants.STATUS_SCHEDULED, (int)status.getCode());
	}

	@Override
	@Test
	public void testPostCTCollectionCheckout() throws Exception {
		CTCollection ctCollection =
			testPostCTCollectionCheckout_addCTCollection();

		assertHttpResponseStatusCode(
			Response.Status.NO_CONTENT.getStatusCode(),
			ctCollectionResource.postCTCollectionCheckoutHttpResponse(
				ctCollection.getId()));

		CTPreferences ctPreferences =
			_ctPreferencesLocalService.getCTPreferences(
				TestPropsValues.getCompanyId(), TestPropsValues.getUserId());

		Assert.assertEquals(
			ctPreferences.getCtCollectionId(), (long)ctCollection.getId());
	}

	@Override
	@Test
	public void testPostCTCollectionPublish() throws Exception {
		CTCollection ctCollection =
			testPostCTCollectionPublish_addCTCollection();

		try (SafeCloseable safeCloseable =
				CTCollectionThreadLocal.setCTCollectionIdWithSafeCloseable(
					ctCollection.getId())) {

			DDMStructureTestUtil.addStructure(
				TestPropsValues.getGroupId(), JournalArticle.class.getName());
		}

		assertHttpResponseStatusCode(
			Response.Status.NO_CONTENT.getStatusCode(),
			ctCollectionResource.postCTCollectionPublishHttpResponse(
				ctCollection.getId()));

		ctCollection = ctCollectionResource.getCTCollection(
			ctCollection.getId());

		Status status = ctCollection.getStatus();

		Assert.assertEquals(
			WorkflowConstants.STATUS_APPROVED, (int)status.getCode());

		_assertHttpResponseProblem(
			CTCollectionStatusException.class,
			ctCollectionResource.postCTCollectionPublishHttpResponse(
				ctCollection.getId()));
	}

	@Override
	@Test
	public void testPostCTCollectionSchedulePublish() throws Exception {
		CTCollection ctCollection =
			testPostCTCollectionSchedulePublish_addCTCollection();

		_assertHttpResponseProblem(
			IllegalArgumentException.class,
			ctCollectionResource.postCTCollectionSchedulePublishHttpResponse(
				ctCollection.getId(),
				new Date(
					System.currentTimeMillis() - RandomTestUtil.randomInt())));

		assertHttpResponseStatusCode(
			Response.Status.NO_CONTENT.getStatusCode(),
			ctCollectionResource.postCTCollectionSchedulePublishHttpResponse(
				ctCollection.getId(),
				new Date(
					System.currentTimeMillis() + RandomTestUtil.randomInt())));

		ctCollection = ctCollectionResource.getCTCollection(
			ctCollection.getId());

		Status status = ctCollection.getStatus();

		Assert.assertEquals(
			WorkflowConstants.STATUS_SCHEDULED, (int)status.getCode());
	}

	@Override
	protected String[] getAdditionalAssertFieldNames() {
		return new String[] {"description", "name"};
	}

	@Override
	protected String[] getIgnoredEntityFieldNames() {
		return new String[] {
			"dateScheduled", "description", "ownerName", "status"
		};
	}

	@Override
	protected CTCollection testDeleteCTCollection_addCTCollection()
		throws Exception {

		return ctCollectionResource.postCTCollection(randomCTCollection());
	}

	@Override
	protected CTCollection
			testDeleteCTCollectionByExternalReferenceCode_addCTCollection()
		throws Exception {

		return ctCollectionResource.postCTCollection(randomCTCollection());
	}

	@Override
	protected CTCollection testGetCTCollection_addCTCollection()
		throws Exception {

		return ctCollectionResource.postCTCollection(randomCTCollection());
	}

	@Override
	protected CTCollection
			testGetCTCollectionByExternalReferenceCode_addCTCollection()
		throws Exception {

		return ctCollectionResource.postCTCollection(randomCTCollection());
	}

	@Override
	protected CTCollection testGetCTCollectionsPage_addCTCollection(
			CTCollection ctCollection)
		throws Exception {

		return _postCTCollection(ctCollection);
	}

	@Override
	protected CTCollection testGraphQLCTCollection_addCTCollection()
		throws Exception {

		return ctCollectionResource.postCTCollection(randomCTCollection());
	}

	@Override
	protected CTCollection testPatchCTCollection_addCTCollection()
		throws Exception {

		return ctCollectionResource.postCTCollection(randomCTCollection());
	}

	@Override
	protected CTCollection
			testPatchCTCollectionByExternalReferenceCode_addCTCollection()
		throws Exception {

		return ctCollectionResource.postCTCollection(randomCTCollection());
	}

	@Override
	protected CTCollection testPostCTCollection_addCTCollection(
			CTCollection ctCollection)
		throws Exception {

		return ctCollectionResource.postCTCollection(ctCollection);
	}

	@Override
	protected CTCollection
			testPostCTCollectionByExternalReferenceCodePublish_addCTCollection()
		throws Exception {

		return ctCollectionResource.postCTCollection(randomCTCollection());
	}

	@Override
	protected CTCollection
			testPostCTCollectionByExternalReferenceCodeSchedulePublish_addCTCollection()
		throws Exception {

		return ctCollectionResource.postCTCollection(randomCTCollection());
	}

	@Override
	protected CTCollection testPostCTCollectionCheckout_addCTCollection()
		throws Exception {

		return ctCollectionResource.postCTCollection(randomCTCollection());
	}

	@Override
	protected CTCollection testPostCTCollectionPublish_addCTCollection()
		throws Exception {

		return ctCollectionResource.postCTCollection(randomCTCollection());
	}

	@Override
	protected CTCollection testPostCTCollectionSchedulePublish_addCTCollection()
		throws Exception {

		return ctCollectionResource.postCTCollection(randomCTCollection());
	}

	@Override
	protected CTCollection testPutCTCollection_addCTCollection()
		throws Exception {

		return ctCollectionResource.postCTCollection(randomCTCollection());
	}

	private void _assertHttpResponseProblem(
			Class<?> exceptionClass, HttpInvoker.HttpResponse httpResponse)
		throws Exception {

		Assert.assertEquals(
			Response.Status.BAD_REQUEST.getStatusCode(),
			httpResponse.getStatusCode());

		if (exceptionClass != null) {
			JSONObject jsonObject = _jsonFactory.createJSONObject(
				httpResponse.getContent());

			Assert.assertEquals(
				exceptionClass.getSimpleName(), jsonObject.get("type"));
		}
	}

	private CTCollection _postCTCollection(CTCollection ctCollection)
		throws Exception {

		CTCollection postCTCollection = ctCollectionResource.postCTCollection(
			ctCollection);

		com.liferay.change.tracking.model.CTCollection
			serviceBuilderCTCollection =
				_ctCollectionLocalService.getCTCollection(
					postCTCollection.getId());

		serviceBuilderCTCollection.setCreateDate(ctCollection.getDateCreated());
		serviceBuilderCTCollection.setModifiedDate(
			ctCollection.getDateModified());

		serviceBuilderCTCollection =
			_ctCollectionLocalService.updateCTCollection(
				serviceBuilderCTCollection);

		return ctCollectionResource.getCTCollection(
			serviceBuilderCTCollection.getCtCollectionId());
	}

	@Inject
	private ClassNameLocalService _classNameLocalService;

	@Inject
	private CTCollectionLocalService _ctCollectionLocalService;

	@Inject
	private CTPreferencesLocalService _ctPreferencesLocalService;

	@Inject
	private JSONFactory _jsonFactory;

	@Inject
	private LayoutLocalService _layoutLocalService;

}