/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.blogs.web.internal.info.item.provider.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.blogs.model.BlogsEntry;
import com.liferay.blogs.test.util.BlogsTestUtil;
import com.liferay.info.field.InfoField;
import com.liferay.info.field.InfoFieldSet;
import com.liferay.info.form.InfoForm;
import com.liferay.info.item.InfoItemServiceRegistry;
import com.liferay.info.item.provider.InfoItemFormProvider;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Eudaldo Alonso
 */
@RunWith(Arquillian.class)
public class BlogsEntryInfoItemFormProviderTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();
	}

	@Test
	@TestInfo({"LPS-106776", "LPS-118979"})
	public void testGetInfoFormInfoFieldSets() throws Exception {
		InfoItemFormProvider<BlogsEntry> infoItemFormProvider =
			(InfoItemFormProvider<BlogsEntry>)
				_infoItemServiceRegistry.getFirstInfoItemService(
					InfoItemFormProvider.class, BlogsEntry.class.getName());

		BlogsEntry blogsEntry = BlogsTestUtil.addEntry(
			TestPropsValues.getUserId(), RandomTestUtil.randomString(),
			RandomTestUtil.randomString(),
			ServiceContextTestUtil.getServiceContext(_group.getGroupId()));

		InfoForm infoForm = infoItemFormProvider.getInfoForm(blogsEntry);

		_assertInfoFields(
			new String[] {"Title", "Author Name", "Author Profile Image"},
			infoForm, "basic-information");
		_assertInfoFields(
			new String[] {"All Categories", "Topic", "Tags"}, infoForm,
			"categorization");
		_assertInfoFields(
			new String[] {
				"Subtitle", "Cover Image", "Cover Image Caption", "Content"
			},
			infoForm, "content");
		_assertInfoFields(
			new String[] {"Description", "Small Image", "Display Date"},
			infoForm, "configuration");
		_assertInfoFields(new String[] {"Default"}, infoForm, "display-page");
	}

	private void _assertInfoFields(
		String[] expectedInfoFieldLabels, InfoForm infoForm, String name) {

		InfoFieldSet infoFieldSet = (InfoFieldSet)infoForm.getInfoFieldSetEntry(
			name);

		List<InfoField<?>> infoFields = infoFieldSet.getAllInfoFields();

		Assert.assertEquals(
			infoFields.toString(), expectedInfoFieldLabels.length,
			infoFields.size());

		for (int i = 0; i < expectedInfoFieldLabels.length; i++) {
			InfoField<?> infoField = infoFields.get(i);

			Assert.assertEquals(
				expectedInfoFieldLabels[i],
				infoField.getLabel(LocaleUtil.getSiteDefault()));
		}
	}

	@DeleteAfterTestRun
	private Group _group;

	@Inject
	private InfoItemServiceRegistry _infoItemServiceRegistry;

}