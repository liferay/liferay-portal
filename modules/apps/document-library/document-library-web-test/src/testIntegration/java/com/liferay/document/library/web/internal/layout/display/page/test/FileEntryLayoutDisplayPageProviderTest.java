/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.document.library.web.internal.layout.display.page.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.document.library.kernel.model.DLFileEntry;
import com.liferay.document.library.kernel.model.DLFolderConstants;
import com.liferay.document.library.kernel.service.DLAppLocalService;
import com.liferay.friendly.url.configuration.FriendlyURLSeparatorCompanyConfiguration;
import com.liferay.info.item.ERCInfoItemIdentifier;
import com.liferay.info.item.InfoItemReference;
import com.liferay.layout.display.page.LayoutDisplayPageObjectProvider;
import com.liferay.layout.display.page.LayoutDisplayPageProvider;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.configuration.test.util.CompanyConfigurationTemporarySwapper;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.portlet.constants.FriendlyURLResolverConstants;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Adolfo Pérez
 */
@RunWith(Arquillian.class)
public class FileEntryLayoutDisplayPageProviderTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();

		_fileEntry = _dlAppLocalService.addFileEntry(
			null, TestPropsValues.getUserId(), _group.getGroupId(),
			DLFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			RandomTestUtil.randomString(),
			ContentTypes.APPLICATION_OCTET_STREAM,
			RandomTestUtil.randomString(), StringPool.BLANK,
			RandomTestUtil.randomString(), RandomTestUtil.randomString(),
			new byte[0], null, null, null,
			ServiceContextTestUtil.getServiceContext(_group.getGroupId()));

		ServiceContextThreadLocal.pushServiceContext(
			ServiceContextTestUtil.getServiceContext(_group.getGroupId()));
	}

	@After
	public void tearDown() throws Exception {
		ServiceContextThreadLocal.popServiceContext();
	}

	@Test
	public void testGetLayoutDisplayPageObjectProvider() throws Exception {
		LayoutDisplayPageObjectProvider layoutDisplayPageObjectProvider =
			_layoutDisplayPageProvider.getLayoutDisplayPageObjectProvider(
				_fileEntry.getGroupId(),
				new InfoItemReference(
					FileEntry.class.getName(),
					new ERCInfoItemIdentifier(
						_fileEntry.getExternalReferenceCode())));

		Assert.assertEquals(
			_fileEntry, layoutDisplayPageObjectProvider.getDisplayObject());

		Company company = _companyLocalService.getCompany(
			TestPropsValues.getCompanyId());

		Group companyGroup = company.getGroup();

		layoutDisplayPageObjectProvider =
			_layoutDisplayPageProvider.getLayoutDisplayPageObjectProvider(
				companyGroup.getGroupId(),
				new InfoItemReference(
					FileEntry.class.getName(),
					new ERCInfoItemIdentifier(
						_fileEntry.getExternalReferenceCode(),
						_group.getExternalReferenceCode())));

		Assert.assertEquals(
			_fileEntry, layoutDisplayPageObjectProvider.getDisplayObject());

		layoutDisplayPageObjectProvider =
			_layoutDisplayPageProvider.getLayoutDisplayPageObjectProvider(
				companyGroup.getGroupId(),
				new InfoItemReference(
					FileEntry.class.getName(),
					new ERCInfoItemIdentifier(
						_fileEntry.getExternalReferenceCode())));

		Assert.assertNull(layoutDisplayPageObjectProvider);
	}

	@Test
	public void testGetLayoutDisplayPageObjectProviderFileEntryWithInvalidFileEntryId() {
		Assert.assertNull(
			_layoutDisplayPageProvider.getLayoutDisplayPageObjectProvider(
				_fileEntry.getGroupId(),
				String.valueOf(_fileEntry.getFileEntryId() + 1)));
	}

	@Test
	public void testGetLayoutDisplayPageObjectProviderFileEntryWithInvalidUrlTitle() {
		Assert.assertNull(
			_layoutDisplayPageProvider.getLayoutDisplayPageObjectProvider(
				_fileEntry.getGroupId(), RandomTestUtil.randomString()));
	}

	@Test
	public void testGetLayoutDisplayPageObjectProviderFileEntryWithValidFileEntryId() {
		Assert.assertNotNull(
			_layoutDisplayPageProvider.getLayoutDisplayPageObjectProvider(
				_fileEntry.getGroupId(),
				String.valueOf(_fileEntry.getFileEntryId())));
	}

	@Test
	public void testGetLayoutDisplayPageObjectProviderFileEntryWithValidUrlTitle()
		throws Exception {

		Assert.assertNotNull(
			_layoutDisplayPageProvider.getLayoutDisplayPageObjectProvider(
				_fileEntry.getGroupId(), _fileEntry.getTitle()));
	}

	@Test
	public void testGetURLSeparator() {
		Assert.assertEquals(
			FriendlyURLResolverConstants.URL_SEPARATOR_FILE_ENTRY,
			_layoutDisplayPageProvider.getURLSeparator());
	}

	@Test
	public void testGetURLSeparatorWithConfiguredURLSeparator()
		throws Exception {

		String fileEntryFriendlyURLSeparator = "/file-test1/";

		try (CompanyConfigurationTemporarySwapper
				companyConfigurationTemporarySwapper =
					new CompanyConfigurationTemporarySwapper(
						_group.getCompanyId(),
						FriendlyURLSeparatorCompanyConfiguration.class.
							getName(),
						HashMapDictionaryBuilder.<String, Object>put(
							"friendlyURLSeparatorsJSON",
							JSONUtil.put(
								DLFileEntry.class.getName(),
								fileEntryFriendlyURLSeparator)
						).build())) {

			Assert.assertEquals(
				fileEntryFriendlyURLSeparator,
				_layoutDisplayPageProvider.getURLSeparator());
		}
	}

	@Inject
	private CompanyLocalService _companyLocalService;

	@Inject
	private DLAppLocalService _dlAppLocalService;

	private FileEntry _fileEntry;

	@DeleteAfterTestRun
	private Group _group;

	@Inject
	private GroupLocalService _groupLocalService;

	@Inject(
		filter = "component.name=com.liferay.document.library.web.internal.layout.display.page.FileEntryLayoutDisplayPageProvider"
	)
	private LayoutDisplayPageProvider<FileEntry> _layoutDisplayPageProvider;

}