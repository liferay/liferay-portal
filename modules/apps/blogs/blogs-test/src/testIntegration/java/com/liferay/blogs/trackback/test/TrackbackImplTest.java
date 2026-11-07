/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.blogs.trackback.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.blogs.linkback.LinkbackConsumer;
import com.liferay.blogs.model.BlogsEntry;
import com.liferay.blogs.service.BlogsEntryLocalServiceUtil;
import com.liferay.portal.kernel.comment.CommentManagerUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.service.CompanyLocalServiceUtil;
import com.liferay.portal.kernel.service.IdentityServiceContextFunction;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import java.util.Date;
import java.util.function.Function;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceReference;

/**
 * @author Adolfo Pérez
 */
@RunWith(Arquillian.class)
public class TrackbackImplTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Test
	public void testAddTrackback() throws Exception {
		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext();

		_blogsEntry = BlogsEntryLocalServiceUtil.addEntry(
			TestPropsValues.getUserId(), StringUtil.randomString(),
			StringUtil.randomString(), new Date(), serviceContext);

		IdentityServiceContextFunction serviceContextFunction =
			new IdentityServiceContextFunction(serviceContext);

		CommentManagerUtil.addComment(
			TestPropsValues.getUserId(), TestPropsValues.getGroupId(),
			BlogsEntry.class.getName(), _blogsEntry.getEntryId(),
			StringUtil.randomString(), serviceContextFunction);

		int initialCommentsCount = CommentManagerUtil.getCommentsCount(
			BlogsEntry.class.getName(), _blogsEntry.getEntryId());

		Bundle bundle = FrameworkUtil.getBundle(getClass());

		BundleContext bundleContext = bundle.getBundleContext();

		ServiceReference<?>[] serviceReferences =
			bundleContext.getServiceReferences(
				MVCActionCommand.class.getName(),
				"(mvc.command.name=/blogs/trackback)");

		Object trackback = ReflectionTestUtil.getFieldValue(
			bundleContext.getService(serviceReferences[0]), "_trackback");

		ThemeDisplay themeDisplay = new ThemeDisplay();

		themeDisplay.setCompany(
			CompanyLocalServiceUtil.getCompany(TestPropsValues.getCompanyId()));

		ReflectionTestUtil.invoke(
			trackback, "addTrackback",
			new Class<?>[] {
				BlogsEntry.class, ThemeDisplay.class, String.class,
				String.class, String.class, String.class, Function.class
			},
			_blogsEntry, themeDisplay, StringUtil.randomString(),
			StringUtil.randomString(), StringUtil.randomString(),
			StringUtil.randomString(), serviceContextFunction);

		Assert.assertEquals(
			initialCommentsCount + 1,
			CommentManagerUtil.getCommentsCount(
				BlogsEntry.class.getName(), _blogsEntry.getEntryId()));

		LinkbackConsumer linkbackConsumer = bundleContext.getService(
			bundleContext.getServiceReference(LinkbackConsumer.class));

		linkbackConsumer.verifyNewTrackbacks();
	}

	@DeleteAfterTestRun
	private BlogsEntry _blogsEntry;

}