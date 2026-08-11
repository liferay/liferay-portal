/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.exportimport.rest.resource.v1_0.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.exportimport.rest.client.dto.v1_0.PreviewPortletDataHandlerSection;
import com.liferay.exportimport.rest.client.dto.v1_0.PublishPreview;
import com.liferay.exportimport.rest.client.dto.v1_0.PublishProcessRequest;
import com.liferay.layout.test.util.LayoutTestUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.test.log.LogCapture;
import com.liferay.portal.test.log.LoggerTestUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.staging.StagingGroupHelper;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Daniel Raposo
 */
@RunWith(Arquillian.class)
public class PublishPreviewResourceTest
	extends BasePublishPreviewResourceTestCase {

	@Before
	@Override
	public void setUp() throws Exception {
		super.setUp();

		UserTestUtil.setUser(TestPropsValues.getUser());

		GroupTestUtil.enableLocalStaging(testGroup);
	}

	@Override
	@Test
	public void testGetSitePublishPreview() throws Exception {
		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				"com.liferay.portal.vulcan.internal.jaxrs.exception.mapper." +
					"WebApplicationExceptionMapper",
				LoggerTestUtil.WARN)) {

			Group group = GroupTestUtil.addGroup();

			try {
				assertHttpResponseStatusCode(
					400,
					publishPreviewResource.getSitePublishPreviewHttpResponse(
						group.getExternalReferenceCode(), null, null, null));
			}
			finally {
				GroupTestUtil.deleteGroup(group);
			}
		}

		LayoutTestUtil.addTypePortletLayout(
			_stagingGroupHelper.fetchLocalStagingGroup(
				_groupLocalService.fetchGroupByExternalReferenceCode(
					testGroup.getExternalReferenceCode(),
					testCompany.getCompanyId())));

		PublishPreview publishPreview =
			publishPreviewResource.getSitePublishPreview(
				testGroup.getExternalReferenceCode(), null, null, null);

		Assert.assertNotNull(publishPreview.getAdditionCount());

		PreviewPortletDataHandlerSection[] previewPortletDataHandlerSections =
			publishPreview.getPreviewPortletDataHandlerSections();

		Assert.assertTrue(previewPortletDataHandlerSections.length > 0);

		PublishPreview lastPublishDatePublishPreview =
			publishPreviewResource.getSitePublishPreview(
				testGroup.getExternalReferenceCode(),
				String.valueOf(
					PublishProcessRequest.DateRangeType.FROM_LAST_PUBLISH_DATE),
				null, null);

		Assert.assertNotNull(lastPublishDatePublishPreview.getAdditionCount());
	}

	@Inject
	private GroupLocalService _groupLocalService;

	@Inject
	private StagingGroupHelper _stagingGroupHelper;

}