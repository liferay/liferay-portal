/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.site.resource.v1_0.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.asset.publisher.constants.AssetPublisherPortletKeys;
import com.liferay.headless.admin.site.client.dto.v1_0.BasicWidgetPageWidgetInstance;
import com.liferay.headless.admin.site.client.dto.v1_0.WidgetPageWidgetInstance;
import com.liferay.headless.admin.site.client.problem.Problem;
import com.liferay.layout.test.util.LayoutTestUtil;
import com.liferay.petra.function.UnsafeRunnable;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.LayoutTypePortlet;
import com.liferay.portal.kernel.portlet.PortletIdCodec;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.Inject;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Rubén Pulido
 */
@FeatureFlag("LPD-74328")
@RunWith(Arquillian.class)
public class WidgetPageWidgetInstanceResourceTest
	extends BaseWidgetPageWidgetInstanceResourceTestCase {

	@Before
	@Override
	public void setUp() throws Exception {
		super.setUp();

		_layout = LayoutTestUtil.addTypePortletLayout(testGroup.getGroupId());
	}

	@Ignore
	@Override
	@Test
	public void testBatchEngineDeleteImportTask() throws Exception {
		super.testBatchEngineDeleteImportTask();
	}

	@Override
	@Test
	public void testDeleteSiteSitePageWidgetInstance() throws Exception {
		_testDeleteSiteSitePageWidgetInstance();
		_testDeleteSiteSitePageWidgetInstanceWithNonexistentWidgetInstance();
	}

	@Override
	@Test
	public void testGetSiteSitePageWidgetInstance() throws Exception {
		_testGetSiteSitePageWidgetInstance();
		_testGetSiteSitePageWidgetInstanceWithContentPage();
		_testGetSiteSitePageWidgetInstanceWithNonexistentSitePage();
		_testGetSiteSitePageWidgetInstanceWithNonexistentWidgetInstance();
	}

	@Override
	@Test
	public void testPatchSiteSitePageWidgetInstance() throws Exception {
		_testPatchSiteSitePageWidgetInstance();
		_testPatchSiteSitePageWidgetInstanceWithNonexistentParentSectionId();
		_testPatchSiteSitePageWidgetInstanceWithNonexistentWidgetInstance();
	}

	@Override
	@Test
	public void testPostSiteSitePageWidgetInstance() throws Exception {
		_testPostSiteSitePageWidgetInstance();
		_testPostSiteSitePageWidgetInstanceWithNonexistentParentSectionId();
		_testPostSiteSitePageWidgetInstanceWithUnregisteredWidget();
	}

	@Override
	@Test
	public void testPutSiteSitePageWidgetInstance() throws Exception {
		_testPutSiteSitePageWidgetInstance();
		_testPutSiteSitePageWidgetInstanceWithNonexistentParentSectionId();
		_testPutSiteSitePageWidgetInstanceWithUnregisteredWidget();
	}

	@Override
	protected String[] getAdditionalAssertFieldNames() {
		return new String[] {
			"externalReferenceCode", "parentSectionId", "position",
			"widgetInstanceId", "widgetName"
		};
	}

	@Override
	protected WidgetPageWidgetInstance randomWidgetPageWidgetInstance()
		throws Exception {

		WidgetPageWidgetInstance widgetPageWidgetInstance =
			new BasicWidgetPageWidgetInstance();

		String portletName = AssetPublisherPortletKeys.ASSET_PUBLISHER;

		String portletId = PortletIdCodec.encode(portletName);

		widgetPageWidgetInstance.setExternalReferenceCode(portletId);

		widgetPageWidgetInstance.setParentSectionId("column-1");
		widgetPageWidgetInstance.setPosition(_position++);
		widgetPageWidgetInstance.setType(
			WidgetPageWidgetInstance.Type.BASIC_WIDGET_PAGE_WIDGET_INSTANCE);
		widgetPageWidgetInstance.setWidgetInstanceId(
			PortletIdCodec.decodeInstanceId(portletId));
		widgetPageWidgetInstance.setWidgetName(portletName);

		return widgetPageWidgetInstance;
	}

	@Override
	protected String
			testGetSiteSitePageWidgetInstance_getSitePageExternalReferenceCode()
		throws Exception {

		return _layout.getExternalReferenceCode();
	}

	@Override
	protected WidgetPageWidgetInstance
			testGetSiteSitePageWidgetInstancesPage_addWidgetPageWidgetInstance(
				String siteExternalReferenceCode,
				String sitePageExternalReferenceCode,
				WidgetPageWidgetInstance widgetPageWidgetInstance)
		throws Exception {

		return widgetPageWidgetInstanceResource.postSiteSitePageWidgetInstance(
			siteExternalReferenceCode, sitePageExternalReferenceCode,
			widgetPageWidgetInstance);
	}

	@Override
	protected String
			testGetSiteSitePageWidgetInstancesPage_getSitePageExternalReferenceCode()
		throws Exception {

		return _layout.getExternalReferenceCode();
	}

	@Override
	protected WidgetPageWidgetInstance
			testPostSiteSitePageWidgetInstance_addWidgetPageWidgetInstance(
				WidgetPageWidgetInstance widgetPageWidgetInstance)
		throws Exception {

		return widgetPageWidgetInstanceResource.postSiteSitePageWidgetInstance(
			testGroup.getExternalReferenceCode(),
			_layout.getExternalReferenceCode(), widgetPageWidgetInstance);
	}

	private void _assertProblemException(
			String expectedStatus, String expectedTitle,
			UnsafeRunnable<Exception> unsafeRunnable)
		throws Exception {

		try {
			unsafeRunnable.run();

			Assert.fail();
		}
		catch (Problem.ProblemException problemException) {
			Problem problem = problemException.getProblem();

			Assert.assertEquals(expectedStatus, problem.getStatus());
			Assert.assertEquals(expectedTitle, problem.getTitle());
		}
	}

	private void _testDeleteSiteSitePageWidgetInstance() throws Exception {
		WidgetPageWidgetInstance widgetPageWidgetInstance =
			testPostSiteSitePageWidgetInstance_addWidgetPageWidgetInstance(
				randomWidgetPageWidgetInstance());

		_layout = _layoutLocalService.fetchLayout(_layout.getPlid());

		LayoutTypePortlet layoutTypePortlet =
			(LayoutTypePortlet)_layout.getLayoutType();

		String portletId = PortletIdCodec.encode(
			widgetPageWidgetInstance.getWidgetName(),
			widgetPageWidgetInstance.getWidgetInstanceId());

		Assert.assertTrue(layoutTypePortlet.hasPortletId(portletId));

		widgetPageWidgetInstanceResource.deleteSiteSitePageWidgetInstance(
			testGroup.getExternalReferenceCode(),
			_layout.getExternalReferenceCode(), portletId);

		_layout = _layoutLocalService.fetchLayout(_layout.getPlid());

		layoutTypePortlet = (LayoutTypePortlet)_layout.getLayoutType();

		Assert.assertFalse(layoutTypePortlet.hasPortletId(portletId));
	}

	private void _testDeleteSiteSitePageWidgetInstanceWithNonexistentWidgetInstance()
		throws Exception {

		String widgetInstanceExternalReferenceCode =
			RandomTestUtil.randomString();

		_assertProblemException(
			"NOT_FOUND", null,
			() ->
				widgetPageWidgetInstanceResource.
					deleteSiteSitePageWidgetInstance(
						testGroup.getExternalReferenceCode(),
						_layout.getExternalReferenceCode(),
						widgetInstanceExternalReferenceCode));
	}

	private void _testGetSiteSitePageWidgetInstance() throws Exception {
		WidgetPageWidgetInstance postWidgetPageWidgetInstance =
			testPostSiteSitePageWidgetInstance_addWidgetPageWidgetInstance(
				randomWidgetPageWidgetInstance());

		String portletId = PortletIdCodec.encode(
			postWidgetPageWidgetInstance.getWidgetName(),
			postWidgetPageWidgetInstance.getWidgetInstanceId());

		WidgetPageWidgetInstance getWidgetPageWidgetInstance =
			widgetPageWidgetInstanceResource.getSiteSitePageWidgetInstance(
				testGroup.getExternalReferenceCode(),
				_layout.getExternalReferenceCode(), portletId);

		assertEquals(postWidgetPageWidgetInstance, getWidgetPageWidgetInstance);
		assertValid(getWidgetPageWidgetInstance);
	}

	private void _testGetSiteSitePageWidgetInstanceWithContentPage()
		throws Exception {

		Layout layout = LayoutTestUtil.addTypeContentLayout(testGroup);

		_assertProblemException(
			"BAD_REQUEST",
			"The site page with external reference code \"" +
				layout.getExternalReferenceCode() + "\" is not a widget page",
			() ->
				widgetPageWidgetInstanceResource.getSiteSitePageWidgetInstance(
					testGroup.getExternalReferenceCode(),
					layout.getExternalReferenceCode(),
					RandomTestUtil.randomString()));
	}

	private void _testGetSiteSitePageWidgetInstanceWithNonexistentSitePage()
		throws Exception {

		String sitePageExternalReferenceCode = RandomTestUtil.randomString();

		_assertProblemException(
			"NOT_FOUND", null,
			() ->
				widgetPageWidgetInstanceResource.getSiteSitePageWidgetInstance(
					testGroup.getExternalReferenceCode(),
					sitePageExternalReferenceCode,
					RandomTestUtil.randomString()));
	}

	private void _testGetSiteSitePageWidgetInstanceWithNonexistentWidgetInstance()
		throws Exception {

		String widgetInstanceExternalReferenceCode =
			RandomTestUtil.randomString();

		_assertProblemException(
			"NOT_FOUND", null,
			() ->
				widgetPageWidgetInstanceResource.getSiteSitePageWidgetInstance(
					testGroup.getExternalReferenceCode(),
					_layout.getExternalReferenceCode(),
					widgetInstanceExternalReferenceCode));
	}

	private void _testPatchSiteSitePageWidgetInstance() throws Exception {
		WidgetPageWidgetInstance postWidgetPageWidgetInstance =
			testPostSiteSitePageWidgetInstance_addWidgetPageWidgetInstance(
				randomWidgetPageWidgetInstance());

		String portletId = PortletIdCodec.encode(
			postWidgetPageWidgetInstance.getWidgetName(),
			postWidgetPageWidgetInstance.getWidgetInstanceId());

		WidgetPageWidgetInstance patchWidgetPageWidgetInstance =
			widgetPageWidgetInstanceResource.patchSiteSitePageWidgetInstance(
				testGroup.getExternalReferenceCode(),
				_layout.getExternalReferenceCode(), portletId,
				postWidgetPageWidgetInstance);

		assertEquals(
			postWidgetPageWidgetInstance, patchWidgetPageWidgetInstance);
		assertValid(patchWidgetPageWidgetInstance);
	}

	private void _testPatchSiteSitePageWidgetInstanceWithNonexistentParentSectionId()
		throws Exception {

		WidgetPageWidgetInstance widgetPageWidgetInstance =
			testPostSiteSitePageWidgetInstance_addWidgetPageWidgetInstance(
				randomWidgetPageWidgetInstance());

		String portletId = PortletIdCodec.encode(
			widgetPageWidgetInstance.getWidgetName(),
			widgetPageWidgetInstance.getWidgetInstanceId());

		String parentSectionId = RandomTestUtil.randomString();

		widgetPageWidgetInstance.setParentSectionId(parentSectionId);

		_assertProblemException(
			"BAD_REQUEST",
			"The widget page section " + parentSectionId + " does not exist",
			() ->
				widgetPageWidgetInstanceResource.
					patchSiteSitePageWidgetInstance(
						testGroup.getExternalReferenceCode(),
						_layout.getExternalReferenceCode(), portletId,
						widgetPageWidgetInstance));
	}

	private void _testPatchSiteSitePageWidgetInstanceWithNonexistentWidgetInstance()
		throws Exception {

		String widgetInstanceExternalReferenceCode =
			RandomTestUtil.randomString();

		_assertProblemException(
			"NOT_FOUND", null,
			() ->
				widgetPageWidgetInstanceResource.
					patchSiteSitePageWidgetInstance(
						testGroup.getExternalReferenceCode(),
						_layout.getExternalReferenceCode(),
						widgetInstanceExternalReferenceCode,
						randomWidgetPageWidgetInstance()));
	}

	private void _testPostSiteSitePageWidgetInstance() throws Exception {
		WidgetPageWidgetInstance randomWidgetPageWidgetInstance =
			randomWidgetPageWidgetInstance();

		WidgetPageWidgetInstance postWidgetPageWidgetInstance =
			testPostSiteSitePageWidgetInstance_addWidgetPageWidgetInstance(
				randomWidgetPageWidgetInstance);

		assertEquals(
			randomWidgetPageWidgetInstance, postWidgetPageWidgetInstance);
		assertValid(postWidgetPageWidgetInstance);
	}

	private void _testPostSiteSitePageWidgetInstanceWithNonexistentParentSectionId()
		throws Exception {

		WidgetPageWidgetInstance widgetPageWidgetInstance =
			randomWidgetPageWidgetInstance();

		String parentSectionId = RandomTestUtil.randomString();

		widgetPageWidgetInstance.setParentSectionId(parentSectionId);

		_assertProblemException(
			"BAD_REQUEST",
			"The widget page section " + parentSectionId + " does not exist",
			() ->
				widgetPageWidgetInstanceResource.postSiteSitePageWidgetInstance(
					testGroup.getExternalReferenceCode(),
					_layout.getExternalReferenceCode(),
					widgetPageWidgetInstance));
	}

	private void _testPostSiteSitePageWidgetInstanceWithUnregisteredWidget()
		throws Exception {

		String widgetInstanceId = RandomTestUtil.randomString();
		String widgetName = "com_liferay_test_FakePortlet";

		WidgetPageWidgetInstance widgetPageWidgetInstance =
			new BasicWidgetPageWidgetInstance();

		widgetPageWidgetInstance.setParentSectionId("column-1");
		widgetPageWidgetInstance.setPosition(0);
		widgetPageWidgetInstance.setType(
			WidgetPageWidgetInstance.Type.BASIC_WIDGET_PAGE_WIDGET_INSTANCE);
		widgetPageWidgetInstance.setWidgetInstanceId(widgetInstanceId);
		widgetPageWidgetInstance.setWidgetName(widgetName);

		_assertProblemException(
			"BAD_REQUEST",
			StringBundler.concat(
				"The widget ",
				PortletIdCodec.encode(widgetName, widgetInstanceId),
				" could not be added to the site page ",
				_layout.getExternalReferenceCode()),
			() ->
				widgetPageWidgetInstanceResource.postSiteSitePageWidgetInstance(
					testGroup.getExternalReferenceCode(),
					_layout.getExternalReferenceCode(),
					widgetPageWidgetInstance));
	}

	private void _testPutSiteSitePageWidgetInstance() throws Exception {
		WidgetPageWidgetInstance widgetPageWidgetInstance =
			randomWidgetPageWidgetInstance();

		String portletId = PortletIdCodec.encode(
			widgetPageWidgetInstance.getWidgetName(),
			widgetPageWidgetInstance.getWidgetInstanceId());

		WidgetPageWidgetInstance putWidgetPageWidgetInstance =
			widgetPageWidgetInstanceResource.putSiteSitePageWidgetInstance(
				testGroup.getExternalReferenceCode(),
				_layout.getExternalReferenceCode(), portletId,
				widgetPageWidgetInstance);

		assertEquals(widgetPageWidgetInstance, putWidgetPageWidgetInstance);
		assertValid(putWidgetPageWidgetInstance);
	}

	private void _testPutSiteSitePageWidgetInstanceWithNonexistentParentSectionId()
		throws Exception {

		WidgetPageWidgetInstance widgetPageWidgetInstance =
			testPostSiteSitePageWidgetInstance_addWidgetPageWidgetInstance(
				randomWidgetPageWidgetInstance());

		String portletId = PortletIdCodec.encode(
			widgetPageWidgetInstance.getWidgetName(),
			widgetPageWidgetInstance.getWidgetInstanceId());

		String parentSectionId = RandomTestUtil.randomString();

		widgetPageWidgetInstance.setParentSectionId(parentSectionId);

		_assertProblemException(
			"BAD_REQUEST",
			"The widget page section " + parentSectionId + " does not exist",
			() ->
				widgetPageWidgetInstanceResource.putSiteSitePageWidgetInstance(
					testGroup.getExternalReferenceCode(),
					_layout.getExternalReferenceCode(), portletId,
					widgetPageWidgetInstance));
	}

	private void _testPutSiteSitePageWidgetInstanceWithUnregisteredWidget()
		throws Exception {

		WidgetPageWidgetInstance widgetPageWidgetInstance =
			new BasicWidgetPageWidgetInstance();

		widgetPageWidgetInstance.setParentSectionId("column-1");
		widgetPageWidgetInstance.setPosition(0);
		widgetPageWidgetInstance.setType(
			WidgetPageWidgetInstance.Type.BASIC_WIDGET_PAGE_WIDGET_INSTANCE);

		String widgetInstanceId = RandomTestUtil.randomString();

		widgetPageWidgetInstance.setWidgetInstanceId(widgetInstanceId);

		String widgetName = "com_liferay_test_FakePortlet";

		widgetPageWidgetInstance.setWidgetName(widgetName);

		String portletId = PortletIdCodec.encode(widgetName, widgetInstanceId);

		_assertProblemException(
			"BAD_REQUEST",
			StringBundler.concat(
				"The widget ", portletId,
				" could not be added to the site page ",
				_layout.getExternalReferenceCode()),
			() ->
				widgetPageWidgetInstanceResource.putSiteSitePageWidgetInstance(
					testGroup.getExternalReferenceCode(),
					_layout.getExternalReferenceCode(), portletId,
					widgetPageWidgetInstance));
	}

	private Layout _layout;

	@Inject
	private LayoutLocalService _layoutLocalService;

	private int _position;

}