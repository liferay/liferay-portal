/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.site.resource.v1_0.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.asset.publisher.constants.AssetPublisherPortletKeys;
import com.liferay.headless.admin.site.client.dto.v1_0.WidgetPageWidgetInstance;
import com.liferay.headless.admin.site.client.problem.Problem;
import com.liferay.layout.test.util.LayoutTestUtil;
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
@FeatureFlag("LPD-35443")
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

		try {
			widgetPageWidgetInstanceResource.deleteSiteSitePageWidgetInstance(
				testGroup.getExternalReferenceCode(),
				_layout.getExternalReferenceCode(), portletId);

			Assert.fail();
		}
		catch (Problem.ProblemException problemException) {
			Problem problem = problemException.getProblem();

			Assert.assertEquals("NOT_FOUND", problem.getStatus());
			Assert.assertNull(problem.getTitle());
		}
	}

	@Override
	@Test
	public void testGetSiteSitePageWidgetInstance() throws Exception {
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

		try {
			widgetPageWidgetInstanceResource.getSiteSitePageWidgetInstance(
				testGroup.getExternalReferenceCode(),
				_layout.getExternalReferenceCode(),
				RandomTestUtil.randomString());

			Assert.fail();
		}
		catch (Problem.ProblemException problemException) {
			Problem problem = problemException.getProblem();

			Assert.assertEquals("NOT_FOUND", problem.getStatus());
			Assert.assertNull(problem.getTitle());
		}
	}

	@Override
	@Test
	public void testPatchSiteSitePageWidgetInstance() throws Exception {
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

		try {
			widgetPageWidgetInstanceResource.patchSiteSitePageWidgetInstance(
				testGroup.getExternalReferenceCode(),
				_layout.getExternalReferenceCode(),
				RandomTestUtil.randomString(),
				randomWidgetPageWidgetInstance());

			Assert.fail();
		}
		catch (Problem.ProblemException problemException) {
			Problem problem = problemException.getProblem();

			Assert.assertEquals("NOT_FOUND", problem.getStatus());
			Assert.assertNull(problem.getTitle());
		}
	}

	@Override
	@Test
	public void testPutSiteSitePageWidgetInstance() throws Exception {
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
			new WidgetPageWidgetInstance();

		String portletName = AssetPublisherPortletKeys.ASSET_PUBLISHER;

		String portletId = PortletIdCodec.encode(portletName);

		widgetPageWidgetInstance.setExternalReferenceCode(portletId);

		widgetPageWidgetInstance.setParentSectionId("column-1");
		widgetPageWidgetInstance.setPosition(_position++);
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

	private Layout _layout;

	@Inject
	private LayoutLocalService _layoutLocalService;

	private int _position;

}