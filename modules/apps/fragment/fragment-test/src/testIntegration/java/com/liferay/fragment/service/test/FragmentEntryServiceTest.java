/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.fragment.service.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.fragment.constants.FragmentConstants;
import com.liferay.fragment.exception.FragmentEntryConfigurationException;
import com.liferay.fragment.exception.FragmentEntryContentException;
import com.liferay.fragment.exception.FragmentEntryNameException;
import com.liferay.fragment.model.FragmentCollection;
import com.liferay.fragment.model.FragmentComposition;
import com.liferay.fragment.model.FragmentEntry;
import com.liferay.fragment.service.FragmentEntryLocalService;
import com.liferay.fragment.service.FragmentEntryService;
import com.liferay.fragment.test.util.FragmentCompositionTestUtil;
import com.liferay.fragment.test.util.FragmentEntryTestUtil;
import com.liferay.fragment.test.util.FragmentTestUtil;
import com.liferay.fragment.util.comparator.FragmentCompositionFragmentEntryModifiedDateComparator;
import com.liferay.fragment.util.comparator.FragmentCompositionFragmentEntryNameComparator;
import com.liferay.fragment.util.comparator.FragmentEntryCreateDateComparator;
import com.liferay.fragment.util.comparator.FragmentEntryNameComparator;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.RoleTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PersistenceTestRule;

import java.sql.Timestamp;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Jürgen Kappler
 */
@RunWith(Arquillian.class)
public class FragmentEntryServiceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(), PersistenceTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();

		_fragmentCollection = FragmentTestUtil.addFragmentCollection(
			_group.getGroupId());

		_updatedFragmentCollection = FragmentTestUtil.addFragmentCollection(
			_group.getGroupId());
	}

	@Test
	public void testAddFragmentEntries() throws Exception {
		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(
				_group.getGroupId(), TestPropsValues.getUserId());

		List<FragmentEntry> originalFragmentEntries =
			_fragmentEntryService.getFragmentEntries(
				_fragmentCollection.getFragmentCollectionId());

		_fragmentEntryService.addFragmentEntry(
			null, _group.getGroupId(),
			_fragmentCollection.getFragmentCollectionId(),
			StringUtil.randomString(), StringUtil.randomString(),
			RandomTestUtil.randomString(), RandomTestUtil.randomString(),
			RandomTestUtil.randomString(), false, "{fieldSets: []}", null, 0,
			false, false, FragmentConstants.TYPE_SECTION, null,
			WorkflowConstants.STATUS_APPROVED, serviceContext);

		_fragmentEntryService.addFragmentEntry(
			null, _group.getGroupId(),
			_fragmentCollection.getFragmentCollectionId(),
			StringUtil.randomString(), StringUtil.randomString(),
			RandomTestUtil.randomString(), RandomTestUtil.randomString(),
			RandomTestUtil.randomString(), false, "{fieldSets: []}", null, 0,
			false, false, FragmentConstants.TYPE_SECTION, null,
			WorkflowConstants.STATUS_APPROVED, serviceContext);

		List<FragmentEntry> actualFragmentEntries =
			_fragmentEntryService.getFragmentEntries(
				_fragmentCollection.getFragmentCollectionId());

		Assert.assertEquals(
			actualFragmentEntries.toString(),
			originalFragmentEntries.size() + 2, actualFragmentEntries.size());
	}

	@Test
	public void testAddFragmentEntry() throws Exception {
		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(
				_group.getGroupId(), TestPropsValues.getUserId());

		String name = RandomTestUtil.randomString();

		FragmentEntry fragmentEntry = _fragmentEntryService.addFragmentEntry(
			null, _group.getGroupId(),
			_fragmentCollection.getFragmentCollectionId(),
			StringUtil.randomString(), name, RandomTestUtil.randomString(),
			RandomTestUtil.randomString(), RandomTestUtil.randomString(), false,
			"{fieldSets: []}", null, 0, false, false,
			FragmentConstants.TYPE_SECTION, null,
			WorkflowConstants.STATUS_APPROVED, serviceContext);

		Assert.assertEquals(name, fragmentEntry.getName());
	}

	@Test(expected = FragmentEntryNameException.class)
	public void testAddFragmentEntryUsingEmptyName() throws Exception {
		_fragmentEntryService.addFragmentEntry(
			null, _group.getGroupId(),
			_fragmentCollection.getFragmentCollectionId(), StringPool.BLANK,
			StringPool.BLANK, StringPool.BLANK, StringPool.BLANK,
			StringPool.BLANK, false, StringPool.BLANK, null, 0, false, false,
			FragmentConstants.TYPE_COMPONENT, null,
			WorkflowConstants.STATUS_APPROVED,
			ServiceContextTestUtil.getServiceContext(
				_group.getGroupId(), TestPropsValues.getUserId()));
	}

	@Test(expected = FragmentEntryConfigurationException.class)
	public void testAddFragmentEntryUsingInvalidConfiguration()
		throws Exception {

		_fragmentEntryService.addFragmentEntry(
			null, _group.getGroupId(),
			_fragmentCollection.getFragmentCollectionId(),
			RandomTestUtil.randomString(), RandomTestUtil.randomString(), null,
			"<div></div>", null, false,
			_read("configuration-invalid-missing-field-sets.json"), null, 0,
			false, false, FragmentConstants.TYPE_SECTION, null,
			WorkflowConstants.STATUS_APPROVED,
			ServiceContextTestUtil.getServiceContext(
				_group.getGroupId(), TestPropsValues.getUserId()));
	}

	@Test
	public void testAddFragmentEntryUsingMixedHTML() throws Exception {
		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(
				_group.getGroupId(), TestPropsValues.getUserId());

		String html = "<div>Text Inside</div> Text Outside";

		FragmentEntry fragmentEntry = _fragmentEntryService.addFragmentEntry(
			null, _group.getGroupId(),
			_fragmentCollection.getFragmentCollectionId(),
			RandomTestUtil.randomString(), RandomTestUtil.randomString(), null,
			html, null, false, StringPool.BLANK, null, 0, false, false,
			FragmentConstants.TYPE_COMPONENT, null,
			WorkflowConstants.STATUS_APPROVED, serviceContext);

		Assert.assertEquals(html, fragmentEntry.getHtml());
	}

	@Test(expected = FragmentEntryContentException.class)
	public void testAddFragmentEntryUsingNullHTML() throws Exception {
		_fragmentEntryService.addFragmentEntry(
			null, _group.getGroupId(),
			_fragmentCollection.getFragmentCollectionId(),
			RandomTestUtil.randomString(), RandomTestUtil.randomString(), null,
			null, null, false, StringPool.BLANK, null, 0, false, false,
			FragmentConstants.TYPE_COMPONENT, null,
			WorkflowConstants.STATUS_APPROVED,
			ServiceContextTestUtil.getServiceContext(
				_group.getGroupId(), TestPropsValues.getUserId()));
	}

	@Test(expected = FragmentEntryNameException.class)
	public void testAddFragmentEntryUsingNullName() throws Exception {
		_fragmentEntryService.addFragmentEntry(
			null, _group.getGroupId(),
			_fragmentCollection.getFragmentCollectionId(),
			RandomTestUtil.randomString(), null, null, StringPool.BLANK, null,
			false, StringPool.BLANK, null, 0, false, false,
			FragmentConstants.TYPE_COMPONENT, null,
			WorkflowConstants.STATUS_APPROVED,
			ServiceContextTestUtil.getServiceContext(
				_group.getGroupId(), TestPropsValues.getUserId()));
	}

	@Test
	public void testAddFragmentEntryUsingPlaintextHTML() throws Exception {
		_fragmentEntryService.addFragmentEntry(
			null, _group.getGroupId(),
			_fragmentCollection.getFragmentCollectionId(),
			RandomTestUtil.randomString(), RandomTestUtil.randomString(), null,
			"Text only fragment", null, false, StringPool.BLANK, null, 0, false,
			false, FragmentConstants.TYPE_COMPONENT, null,
			WorkflowConstants.STATUS_APPROVED,
			ServiceContextTestUtil.getServiceContext(
				_group.getGroupId(), TestPropsValues.getUserId()));
	}

	@Test
	public void testAddFragmentEntryUsingValidConfiguration() throws Exception {
		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(
				_group.getGroupId(), TestPropsValues.getUserId());

		String configuration = _read("configuration-valid-complete.json");

		FragmentEntry fragmentEntry = _fragmentEntryService.addFragmentEntry(
			null, _group.getGroupId(),
			_fragmentCollection.getFragmentCollectionId(),
			RandomTestUtil.randomString(), RandomTestUtil.randomString(), null,
			"<div></div>", null, false, configuration, null, 0, false, false,
			FragmentConstants.TYPE_SECTION, null,
			WorkflowConstants.STATUS_APPROVED, serviceContext);

		Assert.assertEquals(configuration, fragmentEntry.getConfiguration());
	}

	@Test
	public void testAddFragmentEntryWithFragmentEntryKey() throws Exception {
		FragmentEntry fragmentEntry = _fragmentEntryService.addFragmentEntry(
			null, _group.getGroupId(),
			_fragmentCollection.getFragmentCollectionId(), "FRAGMENTENTRYKEY",
			RandomTestUtil.randomString(), null, "<div></div>", null, false,
			"{fieldSets: []}", null, 0, false, false,
			FragmentConstants.TYPE_SECTION, null,
			WorkflowConstants.STATUS_APPROVED,
			ServiceContextTestUtil.getServiceContext(
				_group.getGroupId(), TestPropsValues.getUserId()));

		Assert.assertEquals(
			"fragmententrykey", fragmentEntry.getFragmentEntryKey());
		Assert.assertEquals(
			WorkflowConstants.STATUS_APPROVED, fragmentEntry.getStatus());
	}

	@Test
	public void testAddFragmentEntryWithFragmentEntryKeyAndType()
		throws Exception {

		FragmentEntry fragmentEntry = _fragmentEntryService.addFragmentEntry(
			null, _group.getGroupId(),
			_fragmentCollection.getFragmentCollectionId(), "FRAGMENTENTRYKEY",
			RandomTestUtil.randomString(), null, RandomTestUtil.randomString(),
			null, false, "{fieldSets: []}", null, 0, false, false,
			FragmentConstants.TYPE_COMPONENT, null,
			WorkflowConstants.STATUS_APPROVED,
			ServiceContextTestUtil.getServiceContext(
				_group.getGroupId(), TestPropsValues.getUserId()));

		Assert.assertEquals(
			"fragmententrykey", fragmentEntry.getFragmentEntryKey());
		Assert.assertEquals(
			FragmentConstants.TYPE_COMPONENT, fragmentEntry.getType());
	}

	@Test
	public void testAddFragmentEntryWithHTML() throws Exception {
		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(
				_group.getGroupId(), TestPropsValues.getUserId());

		String html = "<div>Valid HTML</div>";

		FragmentEntry fragmentEntry = _fragmentEntryService.addFragmentEntry(
			null, _group.getGroupId(),
			_fragmentCollection.getFragmentCollectionId(), StringPool.BLANK,
			RandomTestUtil.randomString(), null, html, null, false,
			StringPool.BLANK, null, 0, false, false,
			FragmentConstants.TYPE_COMPONENT, null,
			WorkflowConstants.STATUS_APPROVED, serviceContext);

		Assert.assertEquals(html, fragmentEntry.getHtml());
	}

	@Test
	public void testAddFragmentEntryWithoutAddPermission() throws Exception {
		try {
			UserTestUtil.setUser(
				UserTestUtil.addGroupUser(_group, RoleConstants.SITE_MEMBER));

			_fragmentEntryService.addFragmentEntry(
				RandomTestUtil.randomString(), _group.getGroupId(),
				_fragmentCollection.getFragmentCollectionId(),
				"FRAGMENTENTRYKEYONE", "Fragment Entry One",
				RandomTestUtil.randomString(), RandomTestUtil.randomString(),
				RandomTestUtil.randomString(), false, "{fieldSets: []}", null,
				0, false, false, FragmentConstants.TYPE_COMPONENT, null,
				WorkflowConstants.STATUS_APPROVED,
				ServiceContextTestUtil.getServiceContext(
					_group.getGroupId(), TestPropsValues.getUserId()));

			Assert.fail();
		}
		catch (PrincipalException principalException) {
		}
		finally {
			UserTestUtil.setUser(TestPropsValues.getUser());
		}
	}

	@Test
	public void testAddFragmentEntryWithType() throws Exception {
		FragmentEntry fragmentEntry = _fragmentEntryService.addFragmentEntry(
			null, _group.getGroupId(),
			_fragmentCollection.getFragmentCollectionId(), StringPool.BLANK,
			RandomTestUtil.randomString(), null, RandomTestUtil.randomString(),
			null, false, StringPool.BLANK, null, 0, false, false,
			FragmentConstants.TYPE_COMPONENT, null,
			WorkflowConstants.STATUS_APPROVED,
			ServiceContextTestUtil.getServiceContext(
				_group.getGroupId(), TestPropsValues.getUserId()));

		Assert.assertEquals(
			FragmentConstants.TYPE_COMPONENT, fragmentEntry.getType());
	}

	@Test
	public void testAddFragmentEntryWithTypeAndHTML() throws Exception {
		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(
				_group.getGroupId(), TestPropsValues.getUserId());

		String html = "<div>Valid HTML</div>";

		FragmentEntry fragmentEntry = _fragmentEntryService.addFragmentEntry(
			null, _group.getGroupId(),
			_fragmentCollection.getFragmentCollectionId(), StringPool.BLANK,
			RandomTestUtil.randomString(), null, html, null, false,
			StringPool.BLANK, null, 0, false, false,
			FragmentConstants.TYPE_COMPONENT, null,
			WorkflowConstants.STATUS_APPROVED, serviceContext);

		Assert.assertEquals(html, fragmentEntry.getHtml());
		Assert.assertEquals(
			FragmentConstants.TYPE_COMPONENT, fragmentEntry.getType());
	}

	@Test
	public void testCopyFragmentEntry() throws Exception {
		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(
				_group.getGroupId(), TestPropsValues.getUserId());

		String name = RandomTestUtil.randomString();

		FragmentEntry fragmentEntry = _fragmentEntryService.addFragmentEntry(
			null, _group.getGroupId(),
			_fragmentCollection.getFragmentCollectionId(), StringPool.BLANK,
			name, "div {\ncolor: red\n}", "<div>Test</div>", "alert(\"test\")",
			false, StringPool.BLANK, null, 0, false, false,
			FragmentConstants.TYPE_COMPONENT, null,
			WorkflowConstants.STATUS_APPROVED, serviceContext);

		FragmentEntry copyFragmentEntry =
			_fragmentEntryService.copyFragmentEntry(
				_group.getGroupId(), fragmentEntry.getFragmentEntryId(),
				fragmentEntry.getFragmentCollectionId(), serviceContext);

		_assertCopiedFragment(fragmentEntry, copyFragmentEntry);
	}

	@Test
	public void testCopyFragmentEntryToDifferentCollection() throws Exception {
		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(
				_group.getGroupId(), TestPropsValues.getUserId());

		FragmentCollection targetFragmentCollection =
			FragmentTestUtil.addFragmentCollection(_group.getGroupId());

		String name = RandomTestUtil.randomString();

		FragmentEntry fragmentEntry = _fragmentEntryService.addFragmentEntry(
			null, _group.getGroupId(),
			_fragmentCollection.getFragmentCollectionId(), StringPool.BLANK,
			name, "div {\ncolor: red\n}", "<div>Test</div>", "alert(\"test\")",
			false, StringPool.BLANK, null, 0, false, false,
			FragmentConstants.TYPE_COMPONENT, null,
			WorkflowConstants.STATUS_APPROVED, serviceContext);

		FragmentEntry copyFragmentEntry =
			_fragmentEntryService.copyFragmentEntry(
				_group.getGroupId(), fragmentEntry.getFragmentEntryId(),
				targetFragmentCollection.getFragmentCollectionId(),
				serviceContext);

		_assertCopiedFragment(fragmentEntry, copyFragmentEntry);
	}

	@Test
	public void testDeleteFragmentEntries() throws Exception {
		FragmentEntry fragmentEntry1 = FragmentEntryTestUtil.addFragmentEntry(
			_fragmentCollection.getFragmentCollectionId());

		FragmentEntry fragmentEntry2 = FragmentEntryTestUtil.addFragmentEntry(
			_fragmentCollection.getFragmentCollectionId());

		_fragmentEntryService.deleteFragmentEntries(
			new long[] {
				fragmentEntry1.getFragmentEntryId(),
				fragmentEntry2.getFragmentEntryId()
			});

		Assert.assertNull(
			_fragmentEntryLocalService.fetchFragmentEntry(
				fragmentEntry1.getFragmentEntryId()));

		Assert.assertNull(
			_fragmentEntryLocalService.fetchFragmentEntry(
				fragmentEntry2.getFragmentEntryId()));
	}

	@Test
	public void testDeleteFragmentEntry() throws Exception {
		FragmentEntry fragmentEntry = FragmentEntryTestUtil.addFragmentEntry(
			_fragmentCollection.getFragmentCollectionId());

		_fragmentEntryService.deleteFragmentEntry(
			fragmentEntry.getFragmentEntryId());

		Assert.assertNull(
			_fragmentEntryLocalService.fetchFragmentEntry(
				fragmentEntry.getFragmentEntryId()));
	}

	@Test
	public void testDeleteFragmentEntryByExternalReferenceCode()
		throws Exception {

		FragmentEntry fragmentEntry = _fragmentEntryService.addFragmentEntry(
			RandomTestUtil.randomString(), _group.getGroupId(),
			_fragmentCollection.getFragmentCollectionId(),
			"FRAGMENTENTRYKEYONE", "Fragment Entry One",
			RandomTestUtil.randomString(), RandomTestUtil.randomString(),
			RandomTestUtil.randomString(), false, "{fieldSets: []}", null, 0,
			false, false, FragmentConstants.TYPE_COMPONENT, null,
			WorkflowConstants.STATUS_APPROVED,
			ServiceContextTestUtil.getServiceContext(
				_group.getGroupId(), TestPropsValues.getUserId()));

		_fragmentEntryService.deleteFragmentEntry(
			fragmentEntry.getExternalReferenceCode(),
			fragmentEntry.getGroupId());

		Assert.assertNull(
			_fragmentEntryLocalService.fetchFragmentEntry(
				fragmentEntry.getFragmentEntryId()));
	}

	@Test
	public void testDeleteFragmentEntryByExternalReferenceCodeWithoutDeletePermission()
		throws Exception {

		FragmentEntry fragmentEntry =
			_fragmentEntryLocalService.addFragmentEntry(
				RandomTestUtil.randomString(), TestPropsValues.getUserId(),
				_group.getGroupId(),
				_fragmentCollection.getFragmentCollectionId(),
				"FRAGMENTENTRYKEYONE", "Fragment Entry One",
				RandomTestUtil.randomString(), RandomTestUtil.randomString(),
				RandomTestUtil.randomString(), false, "{fieldSets: []}", null,
				0, false, false, FragmentConstants.TYPE_COMPONENT, null,
				WorkflowConstants.STATUS_APPROVED,
				ServiceContextTestUtil.getServiceContext(
					_group.getGroupId(), TestPropsValues.getUserId()));

		try {
			UserTestUtil.setUser(
				UserTestUtil.addGroupUser(_group, RoleConstants.SITE_MEMBER));

			_fragmentEntryService.deleteFragmentEntry(
				fragmentEntry.getExternalReferenceCode(),
				fragmentEntry.getGroupId());

			Assert.fail();
		}
		catch (PrincipalException principalException) {
		}
		finally {
			UserTestUtil.setUser(TestPropsValues.getUser());
		}
	}

	@Test
	public void testFetchFragmentEntry() throws Exception {
		String name = RandomTestUtil.randomString();

		FragmentEntry fragmentEntry = FragmentEntryTestUtil.addFragmentEntry(
			_fragmentCollection.getFragmentCollectionId(), name);

		FragmentEntry persistedFragmentEntry =
			_fragmentEntryService.fetchFragmentEntry(
				fragmentEntry.getFragmentEntryId());

		Assert.assertEquals(name, persistedFragmentEntry.getName());
	}

	@Test
	public void testGetAnyFragmentCompositionsAndFragmentEntriesCount()
		throws Exception {

		FragmentCompositionTestUtil.addFragmentComposition(
			_fragmentCollection.getFragmentCollectionId(),
			RandomTestUtil.randomString());
		FragmentCompositionTestUtil.addFragmentComposition(
			_fragmentCollection.getFragmentCollectionId(),
			RandomTestUtil.randomString());
		FragmentCompositionTestUtil.addFragmentComposition(
			_fragmentCollection.getFragmentCollectionId(),
			RandomTestUtil.randomString());

		FragmentEntryTestUtil.addFragmentEntry(
			_fragmentCollection.getFragmentCollectionId(),
			RandomTestUtil.randomString());
		FragmentEntryTestUtil.addFragmentEntry(
			_fragmentCollection.getFragmentCollectionId(),
			RandomTestUtil.randomString());

		Assert.assertEquals(
			5,
			_fragmentEntryService.
				getFragmentCompositionsAndFragmentEntriesCount(
					_fragmentCollection.getGroupId(),
					_fragmentCollection.getFragmentCollectionId(),
					WorkflowConstants.STATUS_ANY));
	}

	@Test
	public void testGetApprovedFragmentCompositionsAndFragmentEntriesCount()
		throws Exception {

		FragmentCompositionTestUtil.addFragmentComposition(
			_fragmentCollection.getFragmentCollectionId(),
			RandomTestUtil.randomString());
		FragmentCompositionTestUtil.addFragmentComposition(
			_fragmentCollection.getFragmentCollectionId(),
			RandomTestUtil.randomString());
		FragmentCompositionTestUtil.addFragmentComposition(
			_fragmentCollection.getFragmentCollectionId(),
			RandomTestUtil.randomString());

		FragmentEntryTestUtil.addFragmentEntry(
			_fragmentCollection.getFragmentCollectionId(),
			RandomTestUtil.randomString());
		FragmentEntryTestUtil.addFragmentEntry(
			_fragmentCollection.getFragmentCollectionId(),
			RandomTestUtil.randomString());

		Assert.assertEquals(
			5,
			_fragmentEntryService.
				getFragmentCompositionsAndFragmentEntriesCount(
					_fragmentCollection.getGroupId(),
					_fragmentCollection.getFragmentCollectionId(),
					WorkflowConstants.STATUS_APPROVED));
	}

	@Test
	public void testGetFragmentCompositionsAndFragmentEntries()
		throws Exception {

		String keyword = "text";

		FragmentComposition fragmentComposition1 =
			FragmentCompositionTestUtil.addFragmentComposition(
				_fragmentCollection.getFragmentCollectionId(),
				RandomTestUtil.randomString() + keyword);
		FragmentComposition fragmentComposition2 =
			FragmentCompositionTestUtil.addFragmentComposition(
				_fragmentCollection.getFragmentCollectionId(),
				RandomTestUtil.randomString() + keyword +
					RandomTestUtil.randomString());
		FragmentComposition fragmentComposition3 =
			FragmentCompositionTestUtil.addFragmentComposition(
				_fragmentCollection.getFragmentCollectionId(),
				keyword + RandomTestUtil.randomString());

		FragmentEntry fragmentEntry1 = FragmentEntryTestUtil.addFragmentEntry(
			_fragmentCollection.getFragmentCollectionId(),
			RandomTestUtil.randomString() + keyword);
		FragmentEntry fragmentEntry2 = FragmentEntryTestUtil.addFragmentEntry(
			_fragmentCollection.getFragmentCollectionId(),
			RandomTestUtil.randomString() + keyword +
				RandomTestUtil.randomString());
		FragmentEntry fragmentEntry3 = FragmentEntryTestUtil.addFragmentEntry(
			_fragmentCollection.getFragmentCollectionId(),
			keyword + RandomTestUtil.randomString());

		List<Object> fragmentCompositionsAndFragmentEntries =
			_fragmentEntryService.getFragmentCompositionsAndFragmentEntries(
				_fragmentCollection.getGroupId(),
				_fragmentCollection.getFragmentCollectionId(),
				WorkflowConstants.STATUS_APPROVED, QueryUtil.ALL_POS,
				QueryUtil.ALL_POS, null);

		Assert.assertEquals(
			fragmentCompositionsAndFragmentEntries.toString(), 6,
			fragmentCompositionsAndFragmentEntries.size());

		List<FragmentComposition> fragmentCompositions = new ArrayList<>();
		List<FragmentEntry> fragmentEntries = new ArrayList<>();

		for (Object object : fragmentCompositionsAndFragmentEntries) {
			if (object instanceof FragmentComposition) {
				FragmentComposition fragmentComposition =
					(FragmentComposition)object;

				Assert.assertTrue(
					Objects.equals(
						fragmentComposition.getFragmentCompositionId(),
						fragmentComposition1.getFragmentCompositionId()) ||
					Objects.equals(
						fragmentComposition.getFragmentCompositionId(),
						fragmentComposition2.getFragmentCompositionId()) ||
					Objects.equals(
						fragmentComposition.getFragmentCompositionId(),
						fragmentComposition3.getFragmentCompositionId()));

				fragmentCompositions.add(fragmentComposition);
			}
			else {
				FragmentEntry fragmentEntry = (FragmentEntry)object;

				Assert.assertTrue(
					Objects.equals(
						fragmentEntry.getFragmentEntryId(),
						fragmentEntry1.getFragmentEntryId()) ||
					Objects.equals(
						fragmentEntry.getFragmentEntryId(),
						fragmentEntry2.getFragmentEntryId()) ||
					Objects.equals(
						fragmentEntry.getFragmentEntryId(),
						fragmentEntry3.getFragmentEntryId()));

				fragmentEntries.add(fragmentEntry);
			}
		}

		Assert.assertEquals(
			fragmentCompositions.toString(), 3, fragmentCompositions.size());
		Assert.assertEquals(
			fragmentEntries.toString(), 3, fragmentEntries.size());
	}

	@Test
	public void testGetFragmentCompositionsAndFragmentEntriesCaseInsensitive()
		throws Exception {

		String keyword = RandomTestUtil.randomString();

		FragmentCompositionTestUtil.addFragmentComposition(
			_fragmentCollection.getFragmentCollectionId(),
			RandomTestUtil.randomString());

		FragmentComposition fragmentComposition1 =
			FragmentCompositionTestUtil.addFragmentComposition(
				_fragmentCollection.getFragmentCollectionId(),
				RandomTestUtil.randomString() + keyword);

		FragmentCompositionTestUtil.addFragmentComposition(
			_fragmentCollection.getFragmentCollectionId(),
			RandomTestUtil.randomString());

		FragmentComposition fragmentComposition2 =
			FragmentCompositionTestUtil.addFragmentComposition(
				_fragmentCollection.getFragmentCollectionId(),
				RandomTestUtil.randomString() + keyword +
					RandomTestUtil.randomString());

		FragmentCompositionTestUtil.addFragmentComposition(
			_fragmentCollection.getFragmentCollectionId(),
			RandomTestUtil.randomString());

		FragmentComposition fragmentComposition3 =
			FragmentCompositionTestUtil.addFragmentComposition(
				_fragmentCollection.getFragmentCollectionId(),
				keyword + RandomTestUtil.randomString());

		FragmentCompositionTestUtil.addFragmentComposition(
			_fragmentCollection.getFragmentCollectionId(),
			RandomTestUtil.randomString());

		FragmentEntryTestUtil.addFragmentEntry(
			_fragmentCollection.getFragmentCollectionId(),
			RandomTestUtil.randomString());

		FragmentEntry fragmentEntry1 = FragmentEntryTestUtil.addFragmentEntry(
			_fragmentCollection.getFragmentCollectionId(),
			RandomTestUtil.randomString() + keyword);

		FragmentEntryTestUtil.addFragmentEntry(
			_fragmentCollection.getFragmentCollectionId(),
			RandomTestUtil.randomString());

		FragmentEntry fragmentEntry2 = FragmentEntryTestUtil.addFragmentEntry(
			_fragmentCollection.getFragmentCollectionId(),
			RandomTestUtil.randomString() + keyword +
				RandomTestUtil.randomString());

		FragmentEntryTestUtil.addFragmentEntry(
			_fragmentCollection.getFragmentCollectionId(),
			RandomTestUtil.randomString());

		FragmentEntry fragmentEntry3 = FragmentEntryTestUtil.addFragmentEntry(
			_fragmentCollection.getFragmentCollectionId(),
			keyword + RandomTestUtil.randomString());

		FragmentEntryTestUtil.addFragmentEntry(
			_fragmentCollection.getFragmentCollectionId(),
			RandomTestUtil.randomString());

		List<Object> fragmentCompositionsAndFragmentEntries =
			_fragmentEntryService.getFragmentCompositionsAndFragmentEntries(
				_fragmentCollection.getGroupId(),
				_fragmentCollection.getFragmentCollectionId(), keyword,
				WorkflowConstants.STATUS_APPROVED, QueryUtil.ALL_POS,
				QueryUtil.ALL_POS, null);

		Assert.assertEquals(
			fragmentCompositionsAndFragmentEntries.toString(), 6,
			fragmentCompositionsAndFragmentEntries.size());

		List<FragmentComposition> fragmentCompositions = new ArrayList<>();
		List<FragmentEntry> fragmentEntries = new ArrayList<>();

		for (Object object : fragmentCompositionsAndFragmentEntries) {
			if (object instanceof FragmentComposition) {
				FragmentComposition fragmentComposition =
					(FragmentComposition)object;

				Assert.assertTrue(
					Objects.equals(
						fragmentComposition.getFragmentCompositionId(),
						fragmentComposition1.getFragmentCompositionId()) ||
					Objects.equals(
						fragmentComposition.getFragmentCompositionId(),
						fragmentComposition2.getFragmentCompositionId()) ||
					Objects.equals(
						fragmentComposition.getFragmentCompositionId(),
						fragmentComposition3.getFragmentCompositionId()));

				fragmentCompositions.add(fragmentComposition);
			}
			else {
				FragmentEntry fragmentEntry = (FragmentEntry)object;

				Assert.assertTrue(
					Objects.equals(
						fragmentEntry.getFragmentEntryId(),
						fragmentEntry1.getFragmentEntryId()) ||
					Objects.equals(
						fragmentEntry.getFragmentEntryId(),
						fragmentEntry2.getFragmentEntryId()) ||
					Objects.equals(
						fragmentEntry.getFragmentEntryId(),
						fragmentEntry3.getFragmentEntryId()));

				fragmentEntries.add(fragmentEntry);
			}
		}

		Assert.assertEquals(
			fragmentCompositions.toString(), 3, fragmentCompositions.size());
		Assert.assertEquals(
			fragmentEntries.toString(), 3, fragmentEntries.size());

		List<Object> lowerCaseFragmentCompositionsAndFragmentEntries =
			_fragmentEntryService.getFragmentCompositionsAndFragmentEntries(
				_fragmentCollection.getGroupId(),
				_fragmentCollection.getFragmentCollectionId(),
				StringUtil.toLowerCase(keyword),
				WorkflowConstants.STATUS_APPROVED, QueryUtil.ALL_POS,
				QueryUtil.ALL_POS, null);

		Assert.assertEquals(
			lowerCaseFragmentCompositionsAndFragmentEntries.toString(),
			fragmentCompositionsAndFragmentEntries,
			lowerCaseFragmentCompositionsAndFragmentEntries);

		List<Object> upperCaseFragmentCompositionsAndFragmentEntries =
			_fragmentEntryService.getFragmentCompositionsAndFragmentEntries(
				_fragmentCollection.getGroupId(),
				_fragmentCollection.getFragmentCollectionId(),
				StringUtil.toUpperCase(keyword),
				WorkflowConstants.STATUS_APPROVED, QueryUtil.ALL_POS,
				QueryUtil.ALL_POS, null);

		Assert.assertEquals(
			upperCaseFragmentCompositionsAndFragmentEntries.toString(),
			fragmentCompositionsAndFragmentEntries,
			upperCaseFragmentCompositionsAndFragmentEntries);
	}

	@Test
	public void testGetFragmentCompositionsAndFragmentEntriesCountWithName()
		throws Exception {

		String keyword = "text";

		FragmentCompositionTestUtil.addFragmentComposition(
			_fragmentCollection.getFragmentCollectionId(),
			RandomTestUtil.randomString());
		FragmentCompositionTestUtil.addFragmentComposition(
			_fragmentCollection.getFragmentCollectionId(), keyword);
		FragmentCompositionTestUtil.addFragmentComposition(
			_fragmentCollection.getFragmentCollectionId(),
			RandomTestUtil.randomString());
		FragmentCompositionTestUtil.addFragmentComposition(
			_fragmentCollection.getFragmentCollectionId(),
			RandomTestUtil.randomString() + keyword +
				RandomTestUtil.randomString());
		FragmentCompositionTestUtil.addFragmentComposition(
			_fragmentCollection.getFragmentCollectionId(),
			RandomTestUtil.randomString());
		FragmentCompositionTestUtil.addFragmentComposition(
			_fragmentCollection.getFragmentCollectionId(),
			keyword + RandomTestUtil.randomString());
		FragmentCompositionTestUtil.addFragmentComposition(
			_fragmentCollection.getFragmentCollectionId(),
			RandomTestUtil.randomString());
		FragmentCompositionTestUtil.addFragmentComposition(
			_fragmentCollection.getFragmentCollectionId(),
			RandomTestUtil.randomString());

		FragmentEntryTestUtil.addFragmentEntry(
			_fragmentCollection.getFragmentCollectionId(), keyword);
		FragmentEntryTestUtil.addFragmentEntry(
			_fragmentCollection.getFragmentCollectionId(),
			RandomTestUtil.randomString());
		FragmentEntryTestUtil.addFragmentEntry(
			_fragmentCollection.getFragmentCollectionId(),
			RandomTestUtil.randomString() + keyword +
				RandomTestUtil.randomString());
		FragmentEntryTestUtil.addFragmentEntry(
			_fragmentCollection.getFragmentCollectionId(),
			RandomTestUtil.randomString());
		FragmentEntryTestUtil.addFragmentEntry(
			_fragmentCollection.getFragmentCollectionId(),
			keyword + RandomTestUtil.randomString());
		FragmentEntryTestUtil.addFragmentEntry(
			_fragmentCollection.getFragmentCollectionId(),
			RandomTestUtil.randomString());

		Assert.assertEquals(
			6,
			_fragmentEntryService.
				getFragmentCompositionsAndFragmentEntriesCount(
					_fragmentCollection.getGroupId(),
					_fragmentCollection.getFragmentCollectionId(), keyword,
					WorkflowConstants.STATUS_APPROVED));
	}

	@Test
	public void testGetFragmentCompositionsAndFragmentEntriesOrderByModifiedDate()
		throws Exception {

		FragmentComposition fragmentComposition1 =
			FragmentCompositionTestUtil.addFragmentComposition(
				_fragmentCollection.getFragmentCollectionId(),
				RandomTestUtil.randomString());
		FragmentComposition fragmentComposition2 =
			FragmentCompositionTestUtil.addFragmentComposition(
				_fragmentCollection.getFragmentCollectionId(),
				RandomTestUtil.randomString());

		FragmentEntry fragmentEntry1 = FragmentEntryTestUtil.addFragmentEntry(
			_fragmentCollection.getFragmentCollectionId(),
			RandomTestUtil.randomString());
		FragmentEntry fragmentEntry2 = FragmentEntryTestUtil.addFragmentEntry(
			_fragmentCollection.getFragmentCollectionId(),
			RandomTestUtil.randomString());

		Assert.assertEquals(
			Arrays.asList(
				fragmentComposition1, fragmentComposition2, fragmentEntry1,
				fragmentEntry2),
			_fragmentEntryService.getFragmentCompositionsAndFragmentEntries(
				_fragmentCollection.getGroupId(),
				_fragmentCollection.getFragmentCollectionId(), null,
				WorkflowConstants.STATUS_APPROVED, QueryUtil.ALL_POS,
				QueryUtil.ALL_POS,
				FragmentCompositionFragmentEntryModifiedDateComparator.
					getInstance(true)));
		Assert.assertEquals(
			Arrays.asList(
				fragmentEntry2, fragmentEntry1, fragmentComposition2,
				fragmentComposition1),
			_fragmentEntryService.getFragmentCompositionsAndFragmentEntries(
				_fragmentCollection.getGroupId(),
				_fragmentCollection.getFragmentCollectionId(), null,
				WorkflowConstants.STATUS_APPROVED, QueryUtil.ALL_POS,
				QueryUtil.ALL_POS,
				FragmentCompositionFragmentEntryModifiedDateComparator.
					getInstance(false)));

		fragmentEntry1 = _fragmentEntryService.updateFragmentEntry(
			fragmentEntry1);

		Assert.assertEquals(
			Arrays.asList(
				fragmentComposition1, fragmentComposition2, fragmentEntry2,
				fragmentEntry1),
			_fragmentEntryService.getFragmentCompositionsAndFragmentEntries(
				_fragmentCollection.getGroupId(),
				_fragmentCollection.getFragmentCollectionId(), null,
				WorkflowConstants.STATUS_APPROVED, QueryUtil.ALL_POS,
				QueryUtil.ALL_POS,
				FragmentCompositionFragmentEntryModifiedDateComparator.
					getInstance(true)));
		Assert.assertEquals(
			Arrays.asList(
				fragmentEntry1, fragmentEntry2, fragmentComposition2,
				fragmentComposition1),
			_fragmentEntryService.getFragmentCompositionsAndFragmentEntries(
				_fragmentCollection.getGroupId(),
				_fragmentCollection.getFragmentCollectionId(), null,
				WorkflowConstants.STATUS_APPROVED, QueryUtil.ALL_POS,
				QueryUtil.ALL_POS,
				FragmentCompositionFragmentEntryModifiedDateComparator.
					getInstance(false)));
	}

	@Test
	public void testGetFragmentCompositionsAndFragmentEntriesOrderByName()
		throws Exception {

		FragmentComposition fragmentComposition1 =
			FragmentCompositionTestUtil.addFragmentComposition(
				_fragmentCollection.getFragmentCollectionId(), "Heading");
		FragmentComposition fragmentComposition2 =
			FragmentCompositionTestUtil.addFragmentComposition(
				_fragmentCollection.getFragmentCollectionId(), "Card");

		FragmentEntry fragmentEntry1 = FragmentEntryTestUtil.addFragmentEntry(
			_fragmentCollection.getFragmentCollectionId(), "Button");
		FragmentEntry fragmentEntry2 = FragmentEntryTestUtil.addFragmentEntry(
			_fragmentCollection.getFragmentCollectionId(), "Date");

		Assert.assertEquals(
			Arrays.asList(
				fragmentEntry1, fragmentComposition2, fragmentEntry2,
				fragmentComposition1),
			_fragmentEntryService.getFragmentCompositionsAndFragmentEntries(
				_fragmentCollection.getGroupId(),
				_fragmentCollection.getFragmentCollectionId(), null,
				WorkflowConstants.STATUS_APPROVED, QueryUtil.ALL_POS,
				QueryUtil.ALL_POS,
				FragmentCompositionFragmentEntryNameComparator.getInstance(
					true)));
		Assert.assertEquals(
			Arrays.asList(
				fragmentComposition1, fragmentEntry2, fragmentComposition2,
				fragmentEntry1),
			_fragmentEntryService.getFragmentCompositionsAndFragmentEntries(
				_fragmentCollection.getGroupId(),
				_fragmentCollection.getFragmentCollectionId(), null,
				WorkflowConstants.STATUS_APPROVED, QueryUtil.ALL_POS,
				QueryUtil.ALL_POS,
				FragmentCompositionFragmentEntryNameComparator.getInstance(
					false)));

		fragmentEntry1 = _fragmentEntryService.updateFragmentEntry(
			fragmentEntry1.getFragmentEntryId(), "Dropdown");

		Assert.assertEquals(
			Arrays.asList(
				fragmentComposition2, fragmentEntry2, fragmentEntry1,
				fragmentComposition1),
			_fragmentEntryService.getFragmentCompositionsAndFragmentEntries(
				_fragmentCollection.getGroupId(),
				_fragmentCollection.getFragmentCollectionId(), null,
				WorkflowConstants.STATUS_APPROVED, QueryUtil.ALL_POS,
				QueryUtil.ALL_POS,
				FragmentCompositionFragmentEntryNameComparator.getInstance(
					true)));
		Assert.assertEquals(
			Arrays.asList(
				fragmentComposition1, fragmentEntry1, fragmentEntry2,
				fragmentComposition2),
			_fragmentEntryService.getFragmentCompositionsAndFragmentEntries(
				_fragmentCollection.getGroupId(),
				_fragmentCollection.getFragmentCollectionId(), null,
				WorkflowConstants.STATUS_APPROVED, QueryUtil.ALL_POS,
				QueryUtil.ALL_POS,
				FragmentCompositionFragmentEntryNameComparator.getInstance(
					false)));
	}

	@Test
	public void testGetFragmentCompositionsAndFragmentEntriesWithBlanks()
		throws Exception {

		String keyword = "text with blanks";

		FragmentCompositionTestUtil.addFragmentComposition(
			_fragmentCollection.getFragmentCollectionId(),
			RandomTestUtil.randomString());

		FragmentComposition fragmentComposition1 =
			FragmentCompositionTestUtil.addFragmentComposition(
				_fragmentCollection.getFragmentCollectionId(),
				RandomTestUtil.randomString() + keyword);

		FragmentCompositionTestUtil.addFragmentComposition(
			_fragmentCollection.getFragmentCollectionId(),
			RandomTestUtil.randomString());

		FragmentComposition fragmentComposition2 =
			FragmentCompositionTestUtil.addFragmentComposition(
				_fragmentCollection.getFragmentCollectionId(),
				RandomTestUtil.randomString() + keyword +
					RandomTestUtil.randomString());

		FragmentCompositionTestUtil.addFragmentComposition(
			_fragmentCollection.getFragmentCollectionId(),
			RandomTestUtil.randomString());

		FragmentComposition fragmentComposition3 =
			FragmentCompositionTestUtil.addFragmentComposition(
				_fragmentCollection.getFragmentCollectionId(),
				keyword + RandomTestUtil.randomString());

		FragmentCompositionTestUtil.addFragmentComposition(
			_fragmentCollection.getFragmentCollectionId(),
			RandomTestUtil.randomString());

		FragmentEntryTestUtil.addFragmentEntry(
			_fragmentCollection.getFragmentCollectionId(),
			RandomTestUtil.randomString());

		FragmentEntry fragmentEntry1 = FragmentEntryTestUtil.addFragmentEntry(
			_fragmentCollection.getFragmentCollectionId(),
			RandomTestUtil.randomString() + keyword);

		FragmentEntryTestUtil.addFragmentEntry(
			_fragmentCollection.getFragmentCollectionId(),
			RandomTestUtil.randomString());

		FragmentEntry fragmentEntry2 = FragmentEntryTestUtil.addFragmentEntry(
			_fragmentCollection.getFragmentCollectionId(),
			RandomTestUtil.randomString() + keyword +
				RandomTestUtil.randomString());

		FragmentEntryTestUtil.addFragmentEntry(
			_fragmentCollection.getFragmentCollectionId(),
			RandomTestUtil.randomString());

		FragmentEntry fragmentEntry3 = FragmentEntryTestUtil.addFragmentEntry(
			_fragmentCollection.getFragmentCollectionId(),
			keyword + RandomTestUtil.randomString());

		FragmentEntryTestUtil.addFragmentEntry(
			_fragmentCollection.getFragmentCollectionId(),
			RandomTestUtil.randomString());

		List<Object> fragmentCompositionsAndFragmentEntries =
			_fragmentEntryService.getFragmentCompositionsAndFragmentEntries(
				_fragmentCollection.getGroupId(),
				_fragmentCollection.getFragmentCollectionId(), keyword,
				WorkflowConstants.STATUS_APPROVED, QueryUtil.ALL_POS,
				QueryUtil.ALL_POS, null);

		Assert.assertEquals(
			fragmentCompositionsAndFragmentEntries.toString(), 6,
			fragmentCompositionsAndFragmentEntries.size());

		List<FragmentComposition> fragmentCompositions = new ArrayList<>();
		List<FragmentEntry> fragmentEntries = new ArrayList<>();

		for (Object object : fragmentCompositionsAndFragmentEntries) {
			if (object instanceof FragmentComposition) {
				FragmentComposition fragmentComposition =
					(FragmentComposition)object;

				Assert.assertTrue(
					Objects.equals(
						fragmentComposition.getFragmentCompositionId(),
						fragmentComposition1.getFragmentCompositionId()) ||
					Objects.equals(
						fragmentComposition.getFragmentCompositionId(),
						fragmentComposition2.getFragmentCompositionId()) ||
					Objects.equals(
						fragmentComposition.getFragmentCompositionId(),
						fragmentComposition3.getFragmentCompositionId()));

				fragmentCompositions.add(fragmentComposition);
			}
			else {
				FragmentEntry fragmentEntry = (FragmentEntry)object;

				Assert.assertTrue(
					Objects.equals(
						fragmentEntry.getFragmentEntryId(),
						fragmentEntry1.getFragmentEntryId()) ||
					Objects.equals(
						fragmentEntry.getFragmentEntryId(),
						fragmentEntry2.getFragmentEntryId()) ||
					Objects.equals(
						fragmentEntry.getFragmentEntryId(),
						fragmentEntry3.getFragmentEntryId()));

				fragmentEntries.add(fragmentEntry);
			}
		}

		Assert.assertEquals(
			fragmentCompositions.toString(), 3, fragmentCompositions.size());
		Assert.assertEquals(
			fragmentEntries.toString(), 3, fragmentEntries.size());

		List<Object> lowerCaseFragmentCompositionsAndFragmentEntries =
			_fragmentEntryService.getFragmentCompositionsAndFragmentEntries(
				_fragmentCollection.getGroupId(),
				_fragmentCollection.getFragmentCollectionId(),
				StringUtil.toLowerCase(keyword),
				WorkflowConstants.STATUS_APPROVED, QueryUtil.ALL_POS,
				QueryUtil.ALL_POS, null);

		Assert.assertEquals(
			lowerCaseFragmentCompositionsAndFragmentEntries.toString(),
			fragmentCompositionsAndFragmentEntries,
			lowerCaseFragmentCompositionsAndFragmentEntries);

		List<Object> upperCaseFragmentCompositionsAndFragmentEntries =
			_fragmentEntryService.getFragmentCompositionsAndFragmentEntries(
				_fragmentCollection.getGroupId(),
				_fragmentCollection.getFragmentCollectionId(),
				StringUtil.toUpperCase(keyword),
				WorkflowConstants.STATUS_APPROVED, QueryUtil.ALL_POS,
				QueryUtil.ALL_POS, null);

		Assert.assertEquals(
			upperCaseFragmentCompositionsAndFragmentEntries.toString(),
			fragmentCompositionsAndFragmentEntries,
			upperCaseFragmentCompositionsAndFragmentEntries);
	}

	@Test
	public void testGetFragmentEntriesByKeywordAndStatusOrderByCreateDateComparator()
		throws Exception {

		LocalDateTime localDateTime = LocalDateTime.now();

		FragmentEntryTestUtil.addFragmentEntryByStatus(
			_fragmentCollection.getFragmentCollectionId(), "AD Fragment",
			WorkflowConstants.STATUS_DRAFT, Timestamp.valueOf(localDateTime));

		FragmentEntry fragmentEntry =
			FragmentEntryTestUtil.addFragmentEntryByStatus(
				_fragmentCollection.getFragmentCollectionId(),
				"AC Fragment Entry", WorkflowConstants.STATUS_APPROVED,
				Timestamp.valueOf(localDateTime));

		localDateTime = localDateTime.plus(1, ChronoUnit.SECONDS);

		FragmentEntryTestUtil.addFragmentEntryByStatus(
			_fragmentCollection.getFragmentCollectionId(), "AA Fragment",
			WorkflowConstants.STATUS_APPROVED,
			Timestamp.valueOf(localDateTime));

		localDateTime = localDateTime.plus(1, ChronoUnit.SECONDS);

		FragmentEntryTestUtil.addFragmentEntryByStatus(
			_fragmentCollection.getFragmentCollectionId(), "AB Fragment Entry",
			WorkflowConstants.STATUS_APPROVED,
			Timestamp.valueOf(localDateTime));

		FragmentEntryCreateDateComparator fragmentEntryCreateDateComparatorAsc =
			FragmentEntryCreateDateComparator.getInstance(true);

		List<FragmentEntry> fragmentEntries =
			_fragmentEntryService.getFragmentEntriesByName(
				_group.getGroupId(),
				_fragmentCollection.getFragmentCollectionId(), "Entry",
				QueryUtil.ALL_POS, QueryUtil.ALL_POS,
				fragmentEntryCreateDateComparatorAsc);

		FragmentEntry firstFragmentEntry = fragmentEntries.get(0);

		Assert.assertEquals(
			fragmentEntries.toString(), fragmentEntry.getName(),
			firstFragmentEntry.getName());

		FragmentEntryCreateDateComparator
			fragmentEntryCreateDateComparatorDesc =
				FragmentEntryCreateDateComparator.getInstance(false);

		fragmentEntries = _fragmentEntryService.getFragmentEntriesByName(
			_group.getGroupId(), _fragmentCollection.getFragmentCollectionId(),
			"Entry", QueryUtil.ALL_POS, QueryUtil.ALL_POS,
			fragmentEntryCreateDateComparatorDesc);

		FragmentEntry lastFragmentEntry = fragmentEntries.get(
			fragmentEntries.size() - 1);

		Assert.assertEquals(
			fragmentEntries.toString(), fragmentEntry.getName(),
			lastFragmentEntry.getName());
	}

	@Test
	public void testGetFragmentEntriesByNameAndStatusOrderByNameComparator()
		throws Exception {

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(
				_group.getGroupId(), TestPropsValues.getUserId());

		FragmentEntry fragmentEntry = _fragmentEntryService.addFragmentEntry(
			null, _group.getGroupId(),
			_fragmentCollection.getFragmentCollectionId(),
			RandomTestUtil.randomString(), "AC Fragment Entry",
			RandomTestUtil.randomString(), RandomTestUtil.randomString(),
			RandomTestUtil.randomString(), false, "{fieldSets: []}", null, 0,
			false, false, FragmentConstants.TYPE_SECTION, null,
			WorkflowConstants.STATUS_APPROVED, serviceContext);

		_fragmentEntryService.addFragmentEntry(
			null, _group.getGroupId(),
			_fragmentCollection.getFragmentCollectionId(),
			RandomTestUtil.randomString(), "AA Fragment",
			RandomTestUtil.randomString(), RandomTestUtil.randomString(),
			RandomTestUtil.randomString(), false, "{fieldSets: []}", null, 0,
			false, false, FragmentConstants.TYPE_SECTION, null,
			WorkflowConstants.STATUS_APPROVED, serviceContext);

		_fragmentEntryService.addFragmentEntry(
			null, _group.getGroupId(),
			_fragmentCollection.getFragmentCollectionId(),
			RandomTestUtil.randomString(), "AB Fragment Entry",
			RandomTestUtil.randomString(), RandomTestUtil.randomString(),
			RandomTestUtil.randomString(), false, "{fieldSets: []}", null, 0,
			false, false, FragmentConstants.TYPE_SECTION, null,
			WorkflowConstants.STATUS_DRAFT, serviceContext);

		_fragmentEntryService.addFragmentEntry(
			null, _group.getGroupId(),
			_fragmentCollection.getFragmentCollectionId(),
			RandomTestUtil.randomString(), "AD Fragment Entry",
			RandomTestUtil.randomString(), RandomTestUtil.randomString(),
			RandomTestUtil.randomString(), false, "{fieldSets: []}", null, 0,
			false, false, FragmentConstants.TYPE_SECTION, null,
			WorkflowConstants.STATUS_APPROVED, serviceContext);

		FragmentEntryNameComparator fragmentEntryNameComparatorAsc =
			FragmentEntryNameComparator.getInstance(true);

		List<FragmentEntry> fragmentEntries =
			_fragmentEntryService.getFragmentEntriesByNameAndStatus(
				_group.getGroupId(),
				_fragmentCollection.getFragmentCollectionId(), "Entry",
				WorkflowConstants.STATUS_APPROVED, QueryUtil.ALL_POS,
				QueryUtil.ALL_POS, fragmentEntryNameComparatorAsc);

		FragmentEntry firstFragmentEntry = fragmentEntries.get(0);

		Assert.assertEquals(
			fragmentEntries.toString(), fragmentEntry.getName(),
			firstFragmentEntry.getName());

		FragmentEntryNameComparator fragmentEntryNameComparatorDesc =
			FragmentEntryNameComparator.getInstance(false);

		fragmentEntries =
			_fragmentEntryService.getFragmentEntriesByNameAndStatus(
				_group.getGroupId(),
				_fragmentCollection.getFragmentCollectionId(), "Entry",
				WorkflowConstants.STATUS_APPROVED, QueryUtil.ALL_POS,
				QueryUtil.ALL_POS, fragmentEntryNameComparatorDesc);

		FragmentEntry lastFragmentEntry = fragmentEntries.get(
			fragmentEntries.size() - 1);

		Assert.assertEquals(
			fragmentEntries.toString(), fragmentEntry.getName(),
			lastFragmentEntry.getName());
	}

	@Test
	public void testGetFragmentEntriesByNameOrderByCreateDateComparator()
		throws Exception {

		LocalDateTime localDateTime = LocalDateTime.now();

		FragmentEntry fragmentEntry = FragmentEntryTestUtil.addFragmentEntry(
			_fragmentCollection.getFragmentCollectionId(), "AC Fragment Entry",
			Timestamp.valueOf(localDateTime));

		localDateTime = localDateTime.plus(1, ChronoUnit.SECONDS);

		FragmentEntryTestUtil.addFragmentEntry(
			_fragmentCollection.getFragmentCollectionId(), "AA Fragment",
			Timestamp.valueOf(localDateTime));

		localDateTime = localDateTime.plus(1, ChronoUnit.SECONDS);

		FragmentEntryTestUtil.addFragmentEntry(
			_fragmentCollection.getFragmentCollectionId(), "AB Fragment Entry",
			Timestamp.valueOf(localDateTime));

		FragmentEntryCreateDateComparator fragmentEntryCreateDateComparatorAsc =
			FragmentEntryCreateDateComparator.getInstance(true);

		List<FragmentEntry> fragmentEntries =
			_fragmentEntryService.getFragmentEntriesByName(
				_group.getGroupId(),
				_fragmentCollection.getFragmentCollectionId(), "Entry",
				QueryUtil.ALL_POS, QueryUtil.ALL_POS,
				fragmentEntryCreateDateComparatorAsc);

		FragmentEntry firstFragmentEntry = fragmentEntries.get(0);

		Assert.assertEquals(
			fragmentEntries.toString(), fragmentEntry.getName(),
			firstFragmentEntry.getName());

		FragmentEntryCreateDateComparator
			fragmentEntryCreateDateComparatorDesc =
				FragmentEntryCreateDateComparator.getInstance(false);

		fragmentEntries = _fragmentEntryService.getFragmentEntriesByName(
			_group.getGroupId(), _fragmentCollection.getFragmentCollectionId(),
			"Entry", QueryUtil.ALL_POS, QueryUtil.ALL_POS,
			fragmentEntryCreateDateComparatorDesc);

		FragmentEntry lastFragmentEntry = fragmentEntries.get(
			fragmentEntries.size() - 1);

		Assert.assertEquals(
			fragmentEntries.toString(), fragmentEntry.getName(),
			lastFragmentEntry.getName());
	}

	@Test
	public void testGetFragmentEntriesByNameOrderByNameComparator()
		throws Exception {

		FragmentEntry fragmentEntry = FragmentEntryTestUtil.addFragmentEntry(
			_fragmentCollection.getFragmentCollectionId(), "AB Fragment Entry");

		FragmentEntryTestUtil.addFragmentEntry(
			_fragmentCollection.getFragmentCollectionId(), "AA Fragment");

		FragmentEntryTestUtil.addFragmentEntry(
			_fragmentCollection.getFragmentCollectionId(), "AC Fragment Entry");

		FragmentEntryNameComparator fragmentEntryNameComparatorAsc =
			FragmentEntryNameComparator.getInstance(true);

		List<FragmentEntry> fragmentEntries =
			_fragmentEntryService.getFragmentEntriesByName(
				_group.getGroupId(),
				_fragmentCollection.getFragmentCollectionId(), "Entry",
				QueryUtil.ALL_POS, QueryUtil.ALL_POS,
				fragmentEntryNameComparatorAsc);

		FragmentEntry firstFragmentEntry = fragmentEntries.get(0);

		Assert.assertEquals(
			fragmentEntries.toString(), fragmentEntry.getName(),
			firstFragmentEntry.getName());

		FragmentEntryNameComparator fragmentEntryNameComparatorDesc =
			FragmentEntryNameComparator.getInstance(false);

		fragmentEntries = _fragmentEntryService.getFragmentEntriesByName(
			_group.getGroupId(), _fragmentCollection.getFragmentCollectionId(),
			"Entry", QueryUtil.ALL_POS, QueryUtil.ALL_POS,
			fragmentEntryNameComparatorDesc);

		FragmentEntry lastFragmentEntry = fragmentEntries.get(
			fragmentEntries.size() - 1);

		Assert.assertEquals(
			fragmentEntries.toString(), fragmentEntry.getName(),
			lastFragmentEntry.getName());
	}

	@Test
	public void testGetFragmentEntriesByStatusOrderByCreateDateComparator()
		throws Exception {

		LocalDateTime localDateTime = LocalDateTime.now();

		FragmentEntryTestUtil.addFragmentEntryByStatus(
			_fragmentCollection.getFragmentCollectionId(), "AC Fragment Entry",
			WorkflowConstants.STATUS_DRAFT, Timestamp.valueOf(localDateTime));

		localDateTime = localDateTime.plus(1, ChronoUnit.SECONDS);

		FragmentEntry fragmentEntry =
			FragmentEntryTestUtil.addFragmentEntryByStatus(
				_fragmentCollection.getFragmentCollectionId(),
				"AB Fragment Entry", WorkflowConstants.STATUS_APPROVED,
				Timestamp.valueOf(localDateTime));

		localDateTime = localDateTime.plus(1, ChronoUnit.SECONDS);

		FragmentEntryTestUtil.addFragmentEntryByStatus(
			_fragmentCollection.getFragmentCollectionId(), "AA Fragment Entry",
			WorkflowConstants.STATUS_APPROVED,
			Timestamp.valueOf(localDateTime));

		FragmentEntryCreateDateComparator fragmentEntryCreateDateComparatorAsc =
			FragmentEntryCreateDateComparator.getInstance(true);

		List<FragmentEntry> fragmentEntries =
			_fragmentEntryService.getFragmentEntriesByStatus(
				_group.getGroupId(),
				_fragmentCollection.getFragmentCollectionId(),
				WorkflowConstants.STATUS_APPROVED, QueryUtil.ALL_POS,
				QueryUtil.ALL_POS, fragmentEntryCreateDateComparatorAsc);

		FragmentEntry firstFragmentEntry = fragmentEntries.get(0);

		Assert.assertEquals(
			fragmentEntries.toString(), fragmentEntry.getName(),
			firstFragmentEntry.getName());

		FragmentEntryCreateDateComparator
			fragmentEntryCreateDateComparatorDesc =
				FragmentEntryCreateDateComparator.getInstance(false);

		fragmentEntries = _fragmentEntryService.getFragmentEntriesByStatus(
			_group.getGroupId(), _fragmentCollection.getFragmentCollectionId(),
			WorkflowConstants.STATUS_APPROVED, QueryUtil.ALL_POS,
			QueryUtil.ALL_POS, fragmentEntryCreateDateComparatorDesc);

		FragmentEntry lastFragmentEntry = fragmentEntries.get(
			fragmentEntries.size() - 1);

		Assert.assertEquals(
			fragmentEntries.toString(), fragmentEntry.getName(),
			lastFragmentEntry.getName());
	}

	@Test
	public void testGetFragmentEntriesByStatusOrderByNameComparator()
		throws Exception {

		FragmentEntryTestUtil.addFragmentEntryByStatus(
			_fragmentCollection.getFragmentCollectionId(), "AC Fragment Entry",
			WorkflowConstants.STATUS_DRAFT);

		FragmentEntry fragmentEntry =
			FragmentEntryTestUtil.addFragmentEntryByStatus(
				_fragmentCollection.getFragmentCollectionId(),
				"AB Fragment Entry", WorkflowConstants.STATUS_APPROVED);

		FragmentEntryTestUtil.addFragmentEntryByStatus(
			_fragmentCollection.getFragmentCollectionId(), "AA Fragment",
			WorkflowConstants.STATUS_APPROVED);

		FragmentEntryNameComparator fragmentEntryNameComparatorAsc =
			FragmentEntryNameComparator.getInstance(true);

		List<FragmentEntry> fragmentEntries =
			_fragmentEntryService.getFragmentEntriesByStatus(
				_group.getGroupId(),
				_fragmentCollection.getFragmentCollectionId(),
				WorkflowConstants.STATUS_APPROVED, QueryUtil.ALL_POS,
				QueryUtil.ALL_POS, fragmentEntryNameComparatorAsc);

		FragmentEntry lastFragmentEntry = fragmentEntries.get(
			fragmentEntries.size() - 1);

		Assert.assertEquals(
			fragmentEntries.toString(), fragmentEntry.getName(),
			lastFragmentEntry.getName());

		FragmentEntryNameComparator fragmentEntryNameComparatorDesc =
			FragmentEntryNameComparator.getInstance(false);

		fragmentEntries = _fragmentEntryService.getFragmentEntriesByStatus(
			_group.getGroupId(), _fragmentCollection.getFragmentCollectionId(),
			WorkflowConstants.STATUS_APPROVED, QueryUtil.ALL_POS,
			QueryUtil.ALL_POS, fragmentEntryNameComparatorDesc);

		FragmentEntry firstFragmentEntry = fragmentEntries.get(0);

		Assert.assertEquals(
			fragmentEntries.toString(), fragmentEntry.getName(),
			firstFragmentEntry.getName());
	}

	@Test
	public void testGetFragmentEntriesByType() throws Exception {
		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(
				_group.getGroupId(), TestPropsValues.getUserId());

		List<FragmentEntry> originalFragmentEntries =
			_fragmentEntryService.getFragmentEntriesByTypeAndStatus(
				_group.getGroupId(),
				_fragmentCollection.getFragmentCollectionId(),
				FragmentConstants.TYPE_COMPONENT,
				WorkflowConstants.STATUS_APPROVED);

		_fragmentEntryService.addFragmentEntry(
			null, _group.getGroupId(),
			_fragmentCollection.getFragmentCollectionId(),
			"FRAGMENTENTRYKEYONE", "Fragment Entry One",
			RandomTestUtil.randomString(), RandomTestUtil.randomString(),
			RandomTestUtil.randomString(), false, "{fieldSets: []}", null, 0,
			false, false, FragmentConstants.TYPE_COMPONENT, null,
			WorkflowConstants.STATUS_APPROVED, serviceContext);

		_fragmentEntryService.addFragmentEntry(
			null, _group.getGroupId(),
			_fragmentCollection.getFragmentCollectionId(),
			"FRAGMENTENTRYKEYTWO", "Fragment Entry Two",
			RandomTestUtil.randomString(), RandomTestUtil.randomString(),
			RandomTestUtil.randomString(), false, "{fieldSets: []}", null, 0,
			false, false, FragmentConstants.TYPE_SECTION, null,
			WorkflowConstants.STATUS_APPROVED, serviceContext);

		List<FragmentEntry> actualFragmentEntries =
			_fragmentEntryService.getFragmentEntriesByTypeAndStatus(
				_group.getGroupId(),
				_fragmentCollection.getFragmentCollectionId(),
				FragmentConstants.TYPE_COMPONENT,
				WorkflowConstants.STATUS_APPROVED);

		Assert.assertEquals(
			actualFragmentEntries.toString(),
			originalFragmentEntries.size() + 1, actualFragmentEntries.size());
	}

	@Test
	public void testGetFragmentEntriesByTypeOrderByCreateDateComparator()
		throws Exception {

		LocalDateTime localDateTime = LocalDateTime.now();

		FragmentEntryTestUtil.addFragmentEntryByType(
			_fragmentCollection.getFragmentCollectionId(), "AC Fragment Entry",
			FragmentConstants.TYPE_SECTION, Timestamp.valueOf(localDateTime));

		localDateTime = localDateTime.plus(1, ChronoUnit.SECONDS);

		FragmentEntry fragmentEntry =
			FragmentEntryTestUtil.addFragmentEntryByType(
				_fragmentCollection.getFragmentCollectionId(),
				"AB Fragment Entry", FragmentConstants.TYPE_COMPONENT,
				Timestamp.valueOf(localDateTime));

		localDateTime = localDateTime.plus(1, ChronoUnit.SECONDS);

		FragmentEntryTestUtil.addFragmentEntryByType(
			_fragmentCollection.getFragmentCollectionId(), "AA Fragment Entry",
			FragmentConstants.TYPE_COMPONENT, Timestamp.valueOf(localDateTime));

		FragmentEntryCreateDateComparator fragmentEntryCreateDateComparatorAsc =
			FragmentEntryCreateDateComparator.getInstance(true);

		List<FragmentEntry> fragmentEntries =
			_fragmentEntryService.getFragmentEntriesByType(
				_group.getGroupId(),
				_fragmentCollection.getFragmentCollectionId(),
				FragmentConstants.TYPE_COMPONENT, QueryUtil.ALL_POS,
				QueryUtil.ALL_POS, fragmentEntryCreateDateComparatorAsc);

		FragmentEntry firstFragmentEntry = fragmentEntries.get(0);

		Assert.assertEquals(
			fragmentEntries.toString(), fragmentEntry.getName(),
			firstFragmentEntry.getName());

		FragmentEntryCreateDateComparator
			fragmentEntryCreateDateComparatorDesc =
				FragmentEntryCreateDateComparator.getInstance(false);

		fragmentEntries = _fragmentEntryService.getFragmentEntriesByType(
			_group.getGroupId(), _fragmentCollection.getFragmentCollectionId(),
			FragmentConstants.TYPE_COMPONENT, QueryUtil.ALL_POS,
			QueryUtil.ALL_POS, fragmentEntryCreateDateComparatorDesc);

		FragmentEntry lastFragmentEntry = fragmentEntries.get(
			fragmentEntries.size() - 1);

		Assert.assertEquals(
			fragmentEntries.toString(), fragmentEntry.getName(),
			lastFragmentEntry.getName());
	}

	@Test
	public void testGetFragmentEntriesByTypeOrderByNameComparator()
		throws Exception {

		FragmentEntryTestUtil.addFragmentEntryByType(
			_fragmentCollection.getFragmentCollectionId(), "AC Fragment Entry",
			FragmentConstants.TYPE_SECTION);

		FragmentEntry fragmentEntry =
			FragmentEntryTestUtil.addFragmentEntryByType(
				_fragmentCollection.getFragmentCollectionId(),
				"AB Fragment Entry", FragmentConstants.TYPE_COMPONENT);

		FragmentEntryTestUtil.addFragmentEntryByType(
			_fragmentCollection.getFragmentCollectionId(), "AA Fragment Entry",
			FragmentConstants.TYPE_COMPONENT);

		FragmentEntryNameComparator fragmentEntryNameComparatorAsc =
			FragmentEntryNameComparator.getInstance(true);

		List<FragmentEntry> fragmentEntries =
			_fragmentEntryService.getFragmentEntriesByType(
				_group.getGroupId(),
				_fragmentCollection.getFragmentCollectionId(),
				FragmentConstants.TYPE_COMPONENT, QueryUtil.ALL_POS,
				QueryUtil.ALL_POS, fragmentEntryNameComparatorAsc);

		FragmentEntry lastFragmentEntry = fragmentEntries.get(
			fragmentEntries.size() - 1);

		Assert.assertEquals(
			fragmentEntries.toString(), fragmentEntry.getName(),
			lastFragmentEntry.getName());

		FragmentEntryNameComparator fragmentEntryNameComparatorDesc =
			FragmentEntryNameComparator.getInstance(false);

		fragmentEntries = _fragmentEntryService.getFragmentEntriesByType(
			_group.getGroupId(), _fragmentCollection.getFragmentCollectionId(),
			FragmentConstants.TYPE_COMPONENT, QueryUtil.ALL_POS,
			QueryUtil.ALL_POS, fragmentEntryNameComparatorDesc);

		FragmentEntry firstFragmentEntry = fragmentEntries.get(0);

		Assert.assertEquals(
			fragmentEntries.toString(), fragmentEntry.getName(),
			firstFragmentEntry.getName());
	}

	@Test
	public void testGetFragmentEntriesCount() throws Exception {
		int originalFragmentCollectionsCount =
			_fragmentEntryService.getFragmentEntriesCount(
				_group.getGroupId(),
				_fragmentCollection.getFragmentCollectionId());

		FragmentEntryTestUtil.addFragmentEntry(
			_fragmentCollection.getFragmentCollectionId());

		FragmentEntryTestUtil.addFragmentEntry(
			_fragmentCollection.getFragmentCollectionId());

		int actualFragmentCollectionsCount =
			_fragmentEntryService.getFragmentEntriesCount(
				_group.getGroupId(),
				_fragmentCollection.getFragmentCollectionId());

		Assert.assertEquals(
			originalFragmentCollectionsCount + 2,
			actualFragmentCollectionsCount);
	}

	@Test
	public void testGetFragmentEntriesCountByName() throws Exception {
		int originalFragmentCollectionsCount =
			_fragmentEntryService.getFragmentEntriesCountByName(
				_group.getGroupId(),
				_fragmentCollection.getFragmentCollectionId(),
				"Fragment Entry");

		FragmentEntryTestUtil.addFragmentEntryByType(
			_fragmentCollection.getFragmentCollectionId(), "Fragment Entry One",
			WorkflowConstants.STATUS_APPROVED);

		FragmentEntryTestUtil.addFragmentEntryByType(
			_fragmentCollection.getFragmentCollectionId(), "Fragment Entry Two",
			WorkflowConstants.STATUS_DENIED);

		int actualFragmentCollectionsCount =
			_fragmentEntryService.getFragmentEntriesCountByName(
				_group.getGroupId(),
				_fragmentCollection.getFragmentCollectionId(),
				"Fragment Entry");

		Assert.assertEquals(
			originalFragmentCollectionsCount + 2,
			actualFragmentCollectionsCount);
	}

	@Test
	public void testGetFragmentEntriesCountByNameAndStatus() throws Exception {
		int originalFragmentCollectionsCount =
			_fragmentEntryService.getFragmentEntriesCountByNameAndStatus(
				_group.getGroupId(),
				_fragmentCollection.getFragmentCollectionId(), "Fragment Entry",
				WorkflowConstants.STATUS_APPROVED);

		FragmentEntryTestUtil.addFragmentEntryByStatus(
			_fragmentCollection.getFragmentCollectionId(), "Fragment Entry One",
			WorkflowConstants.STATUS_APPROVED);

		FragmentEntryTestUtil.addFragmentEntryByStatus(
			_fragmentCollection.getFragmentCollectionId(), "Fragment Entry Two",
			WorkflowConstants.STATUS_DENIED);

		int actualFragmentCollectionsCount =
			_fragmentEntryService.getFragmentEntriesCountByNameAndStatus(
				_group.getGroupId(),
				_fragmentCollection.getFragmentCollectionId(), "Fragment Entry",
				WorkflowConstants.STATUS_APPROVED);

		Assert.assertEquals(
			originalFragmentCollectionsCount + 1,
			actualFragmentCollectionsCount);
	}

	@Test
	public void testGetFragmentEntriesCountByStatus() throws Exception {
		int originalApprovedFragmentEntryCount =
			_fragmentEntryService.getFragmentEntriesCountByStatus(
				_group.getGroupId(),
				_fragmentCollection.getFragmentCollectionId(),
				WorkflowConstants.STATUS_APPROVED);

		int originalDraftFragmentEntryCount =
			_fragmentEntryService.getFragmentEntriesCountByStatus(
				_group.getGroupId(),
				_fragmentCollection.getFragmentCollectionId(),
				WorkflowConstants.STATUS_DRAFT);

		FragmentEntryTestUtil.addFragmentEntryByStatus(
			_fragmentCollection.getFragmentCollectionId(),
			WorkflowConstants.STATUS_APPROVED);

		FragmentEntryTestUtil.addFragmentEntryByStatus(
			_fragmentCollection.getFragmentCollectionId(),
			WorkflowConstants.STATUS_APPROVED);

		FragmentEntryTestUtil.addFragmentEntryByStatus(
			_fragmentCollection.getFragmentCollectionId(),
			WorkflowConstants.STATUS_DRAFT);

		List<FragmentEntry> approvedFragmentEntries =
			_fragmentEntryService.getFragmentEntriesByStatus(
				_group.getGroupId(),
				_fragmentCollection.getFragmentCollectionId(),
				WorkflowConstants.STATUS_APPROVED);

		List<FragmentEntry> draftFragmentEntries =
			_fragmentEntryService.getFragmentEntriesByStatus(
				_group.getGroupId(),
				_fragmentCollection.getFragmentCollectionId(),
				WorkflowConstants.STATUS_DRAFT);

		Assert.assertEquals(
			approvedFragmentEntries.toString(),
			originalApprovedFragmentEntryCount + 2,
			approvedFragmentEntries.size());

		Assert.assertEquals(
			draftFragmentEntries.toString(),
			originalDraftFragmentEntryCount + 1, draftFragmentEntries.size());
	}

	@Test
	public void testGetFragmentEntriesCountByType() throws Exception {
		int originalFragmentCollectionsCount =
			_fragmentEntryService.getFragmentEntriesCountByType(
				_group.getGroupId(),
				_fragmentCollection.getFragmentCollectionId(),
				FragmentConstants.TYPE_COMPONENT);

		FragmentEntryTestUtil.addFragmentEntryByType(
			_fragmentCollection.getFragmentCollectionId(),
			FragmentConstants.TYPE_COMPONENT);

		FragmentEntryTestUtil.addFragmentEntryByType(
			_fragmentCollection.getFragmentCollectionId(),
			FragmentConstants.TYPE_SECTION);

		int actualFragmentCollectionsCount =
			_fragmentEntryService.getFragmentEntriesCountByType(
				_group.getGroupId(),
				_fragmentCollection.getFragmentCollectionId(),
				FragmentConstants.TYPE_COMPONENT);

		Assert.assertEquals(
			originalFragmentCollectionsCount + 1,
			actualFragmentCollectionsCount);
	}

	@Test
	public void testGetFragmentEntriesOrderByCreateDateComparator()
		throws Exception {

		LocalDateTime localDateTime = LocalDateTime.now();

		FragmentEntry fragmentEntry = FragmentEntryTestUtil.addFragmentEntry(
			_fragmentCollection.getFragmentCollectionId(), "AB Fragment Entry",
			Timestamp.valueOf(localDateTime));

		localDateTime = localDateTime.plus(1, ChronoUnit.SECONDS);

		FragmentEntryTestUtil.addFragmentEntry(
			_fragmentCollection.getFragmentCollectionId(), "AA Fragment Entry",
			Timestamp.valueOf(localDateTime));

		FragmentEntryCreateDateComparator fragmentEntryCreateDateComparatorAsc =
			FragmentEntryCreateDateComparator.getInstance(true);

		List<FragmentEntry> fragmentEntries =
			_fragmentEntryService.getFragmentEntries(
				_group.getGroupId(),
				_fragmentCollection.getFragmentCollectionId(),
				QueryUtil.ALL_POS, QueryUtil.ALL_POS,
				fragmentEntryCreateDateComparatorAsc);

		FragmentEntry firstFragmentEntry = fragmentEntries.get(0);

		Assert.assertEquals(
			fragmentEntries.toString(), fragmentEntry.getName(),
			firstFragmentEntry.getName());

		FragmentEntryCreateDateComparator
			fragmentEntryCreateDateComparatorDesc =
				FragmentEntryCreateDateComparator.getInstance(false);

		fragmentEntries = _fragmentEntryService.getFragmentEntries(
			_group.getGroupId(), _fragmentCollection.getFragmentCollectionId(),
			QueryUtil.ALL_POS, QueryUtil.ALL_POS,
			fragmentEntryCreateDateComparatorDesc);

		FragmentEntry lastFragmentEntry = fragmentEntries.get(
			fragmentEntries.size() - 1);

		Assert.assertEquals(
			fragmentEntries.toString(), fragmentEntry.getName(),
			lastFragmentEntry.getName());
	}

	@Test
	public void testGetFragmentEntriesOrderByNameComparator() throws Exception {
		FragmentEntry fragmentEntry = FragmentEntryTestUtil.addFragmentEntry(
			_fragmentCollection.getFragmentCollectionId(), "AB Fragment Entry");

		FragmentEntryTestUtil.addFragmentEntry(
			_fragmentCollection.getFragmentCollectionId(), "AA Fragment Entry");

		FragmentEntryNameComparator fragmentEntryNameComparatorAsc =
			FragmentEntryNameComparator.getInstance(true);

		List<FragmentEntry> fragmentEntries =
			_fragmentEntryService.getFragmentEntries(
				_group.getGroupId(),
				_fragmentCollection.getFragmentCollectionId(),
				QueryUtil.ALL_POS, QueryUtil.ALL_POS,
				fragmentEntryNameComparatorAsc);

		FragmentEntry lastFragmentEntry = fragmentEntries.get(
			fragmentEntries.size() - 1);

		Assert.assertEquals(
			fragmentEntries.toString(), fragmentEntry.getName(),
			lastFragmentEntry.getName());

		FragmentEntryNameComparator fragmentEntryNameComparatorDesc =
			FragmentEntryNameComparator.getInstance(false);

		fragmentEntries = _fragmentEntryService.getFragmentEntries(
			_group.getGroupId(), _fragmentCollection.getFragmentCollectionId(),
			QueryUtil.ALL_POS, QueryUtil.ALL_POS,
			fragmentEntryNameComparatorDesc);

		FragmentEntry firstFragmentEntry = fragmentEntries.get(0);

		Assert.assertEquals(
			fragmentEntries.toString(), fragmentEntry.getName(),
			firstFragmentEntry.getName());
	}

	@Test
	public void testGetFragmentEntryByExternalReferenceCode() throws Exception {
		FragmentEntry fragmentEntry = _fragmentEntryService.addFragmentEntry(
			RandomTestUtil.randomString(), _group.getGroupId(),
			_fragmentCollection.getFragmentCollectionId(),
			"FRAGMENTENTRYKEYONE", "Fragment Entry One",
			RandomTestUtil.randomString(), RandomTestUtil.randomString(),
			RandomTestUtil.randomString(), false, "{fieldSets: []}", null, 0,
			false, false, FragmentConstants.TYPE_COMPONENT, null,
			WorkflowConstants.STATUS_APPROVED,
			ServiceContextTestUtil.getServiceContext(
				_group.getGroupId(), TestPropsValues.getUserId()));

		FragmentEntry curFragmentEntry =
			_fragmentEntryService.getFragmentEntryByExternalReferenceCode(
				fragmentEntry.getExternalReferenceCode(),
				fragmentEntry.getGroupId());

		Assert.assertEquals(
			fragmentEntry.getFragmentEntryId(),
			curFragmentEntry.getFragmentEntryId());
	}

	@Test
	public void testGetFragmentEntryByExternalReferenceCodeWithoutViewPermission()
		throws Exception {

		FragmentEntry fragmentEntry = _fragmentEntryService.addFragmentEntry(
			RandomTestUtil.randomString(), _group.getGroupId(),
			_fragmentCollection.getFragmentCollectionId(),
			"FRAGMENTENTRYKEYONE", "Fragment Entry One",
			RandomTestUtil.randomString(), RandomTestUtil.randomString(),
			RandomTestUtil.randomString(), false, "{fieldSets: []}", null, 0,
			false, false, FragmentConstants.TYPE_COMPONENT, null,
			WorkflowConstants.STATUS_APPROVED,
			ServiceContextTestUtil.getServiceContext(
				_group.getGroupId(), TestPropsValues.getUserId()));

		RoleTestUtil.removeResourcePermission(
			RoleConstants.GUEST, FragmentEntry.class.getName(),
			ResourceConstants.SCOPE_INDIVIDUAL,
			String.valueOf(fragmentEntry.getFragmentEntryId()),
			ActionKeys.VIEW);
		RoleTestUtil.removeResourcePermission(
			RoleConstants.SITE_MEMBER, FragmentEntry.class.getName(),
			ResourceConstants.SCOPE_INDIVIDUAL,
			String.valueOf(fragmentEntry.getFragmentEntryId()),
			ActionKeys.VIEW);

		try {
			UserTestUtil.setUser(
				UserTestUtil.addGroupUser(_group, RoleConstants.SITE_MEMBER));

			_fragmentEntryService.getFragmentEntryByExternalReferenceCode(
				fragmentEntry.getExternalReferenceCode(),
				fragmentEntry.getGroupId());

			Assert.fail();
		}
		catch (PrincipalException principalException) {
		}
		finally {
			UserTestUtil.setUser(TestPropsValues.getUser());
		}
	}

	@Test
	public void testMoveFragmentEntry() throws Exception {
		FragmentEntry fragmentEntry = FragmentEntryTestUtil.addFragmentEntry(
			_fragmentCollection.getFragmentCollectionId());

		FragmentCollection targetFragmentCollection =
			FragmentTestUtil.addFragmentCollection(_group.getGroupId());

		fragmentEntry = _fragmentEntryService.moveFragmentEntry(
			fragmentEntry.getFragmentEntryId(),
			targetFragmentCollection.getFragmentCollectionId());

		Assert.assertEquals(
			targetFragmentCollection.getFragmentCollectionId(),
			fragmentEntry.getFragmentCollectionId());
	}

	@Test
	public void testUpdateFragmentCollectionId() throws Exception {
		FragmentEntry fragmentEntry = FragmentEntryTestUtil.addFragmentEntry(
			_fragmentCollection.getFragmentCollectionId());

		fragmentEntry = _fragmentEntryService.updateFragmentEntry(
			fragmentEntry.getFragmentEntryId(),
			_updatedFragmentCollection.getFragmentCollectionId(),
			"Fragment Entry Updated", "div {\ncolor: red;\n}",
			"<div>Updated</div>", "alert(\"test\");", false,
			"{\n\t\"fieldSets\": [\n\t]\n}", fragmentEntry.getIcon(), 1, false,
			fragmentEntry.getTypeOptions(), WorkflowConstants.STATUS_APPROVED);

		Assert.assertEquals(
			fragmentEntry.getFragmentCollectionId(),
			_updatedFragmentCollection.getFragmentCollectionId());
		Assert.assertEquals("Fragment Entry Updated", fragmentEntry.getName());
		Assert.assertEquals("div {\ncolor: red;\n}", fragmentEntry.getCss());
		Assert.assertEquals("<div>Updated</div>", fragmentEntry.getHtml());
		Assert.assertEquals("alert(\"test\");", fragmentEntry.getJs());
		Assert.assertEquals(1, fragmentEntry.getPreviewFileEntryId());
		Assert.assertEquals(
			WorkflowConstants.STATUS_APPROVED, fragmentEntry.getStatus());
	}

	@Test
	public void testUpdateFragmentEntryName() throws Exception {
		FragmentEntry fragmentEntry = FragmentEntryTestUtil.addFragmentEntry(
			_fragmentCollection.getFragmentCollectionId(),
			"Fragment Name Original");

		fragmentEntry = _fragmentEntryService.updateFragmentEntry(
			fragmentEntry.getFragmentEntryId(), "Fragment Name Updated");

		Assert.assertEquals("Fragment Name Updated", fragmentEntry.getName());
	}

	@Test
	public void testUpdateFragmentEntryValues() throws Exception {
		FragmentEntry fragmentEntry = _fragmentEntryService.addFragmentEntry(
			null, _group.getGroupId(),
			_fragmentCollection.getFragmentCollectionId(), "FRAGMENTENTRYKEY",
			"Fragment Entry Original", RandomTestUtil.randomString(),
			"<div>Original</div>", RandomTestUtil.randomString(), false,
			StringPool.BLANK, null, 0, false, false,
			FragmentConstants.TYPE_COMPONENT, null,
			WorkflowConstants.STATUS_DRAFT,
			ServiceContextTestUtil.getServiceContext(
				_group.getGroupId(), TestPropsValues.getUserId()));

		fragmentEntry = _fragmentEntryService.updateFragmentEntry(
			fragmentEntry.getFragmentEntryId(),
			fragmentEntry.getFragmentCollectionId(), "Fragment Entry Updated",
			"div {\ncolor: red;\n}", "<div>Updated</div>", "alert(\"test\");",
			fragmentEntry.isCacheable(), "{\n\t\"fieldSets\": [\n\t]\n}",
			fragmentEntry.getIcon(), fragmentEntry.getPreviewFileEntryId(),
			false, fragmentEntry.getTypeOptions(),
			WorkflowConstants.STATUS_APPROVED);

		Assert.assertEquals("Fragment Entry Updated", fragmentEntry.getName());
		Assert.assertEquals("div {\ncolor: red;\n}", fragmentEntry.getCss());
		Assert.assertEquals("<div>Updated</div>", fragmentEntry.getHtml());
		Assert.assertEquals("alert(\"test\");", fragmentEntry.getJs());
		Assert.assertEquals(
			WorkflowConstants.STATUS_APPROVED, fragmentEntry.getStatus());
	}

	@Test
	public void testUpdateFragmentEntryValuesAndPreviewFileEntryId()
		throws Exception {

		FragmentEntry fragmentEntry = _fragmentEntryService.addFragmentEntry(
			null, _group.getGroupId(),
			_fragmentCollection.getFragmentCollectionId(), "FRAGMENTENTRYKEY",
			"Fragment Entry Original", RandomTestUtil.randomString(),
			"<div>Original</div>", RandomTestUtil.randomString(), false,
			StringPool.BLANK, null, 0, false, false,
			FragmentConstants.TYPE_COMPONENT, null,
			WorkflowConstants.STATUS_DRAFT,
			ServiceContextTestUtil.getServiceContext(
				_group.getGroupId(), TestPropsValues.getUserId()));

		Assert.assertEquals(0, fragmentEntry.getPreviewFileEntryId());

		fragmentEntry = _fragmentEntryService.updateFragmentEntry(
			fragmentEntry.getFragmentEntryId(),
			fragmentEntry.getFragmentCollectionId(), "Fragment Entry Updated",
			"div {\ncolor: red;\n}", "<div>Updated</div>", "alert(\"test\");",
			fragmentEntry.isCacheable(), "{\n\t\"fieldSets\": [\n\t]\n}",
			fragmentEntry.getIcon(), 1, false, fragmentEntry.getTypeOptions(),
			WorkflowConstants.STATUS_APPROVED);

		Assert.assertEquals("Fragment Entry Updated", fragmentEntry.getName());
		Assert.assertEquals("div {\ncolor: red;\n}", fragmentEntry.getCss());
		Assert.assertEquals("<div>Updated</div>", fragmentEntry.getHtml());
		Assert.assertEquals("alert(\"test\");", fragmentEntry.getJs());
		Assert.assertEquals(1, fragmentEntry.getPreviewFileEntryId());
		Assert.assertEquals(
			WorkflowConstants.STATUS_APPROVED, fragmentEntry.getStatus());
	}

	@Test
	public void testUpdatePreviewFileEntryId() throws Exception {
		FragmentEntry fragmentEntry = FragmentEntryTestUtil.addFragmentEntry(
			_fragmentCollection.getFragmentCollectionId());

		long previewFileEntryId = fragmentEntry.getPreviewFileEntryId();

		fragmentEntry = _fragmentEntryService.updateFragmentEntry(
			fragmentEntry.getFragmentEntryId(), previewFileEntryId + 1);

		Assert.assertEquals(
			previewFileEntryId + 1, fragmentEntry.getPreviewFileEntryId());
	}

	private void _assertCopiedFragment(
		FragmentEntry fragmentEntry, FragmentEntry copyFragmentEntry) {

		Assert.assertEquals(
			fragmentEntry.getGroupId(), copyFragmentEntry.getGroupId());

		Assert.assertEquals(
			fragmentEntry.getName() + " (Copy)", copyFragmentEntry.getName());

		Assert.assertEquals(fragmentEntry.getCss(), copyFragmentEntry.getCss());

		Assert.assertEquals(
			fragmentEntry.getHtml(), copyFragmentEntry.getHtml());

		Assert.assertEquals(fragmentEntry.getJs(), copyFragmentEntry.getJs());

		Assert.assertEquals(
			fragmentEntry.getStatus(), copyFragmentEntry.getStatus());

		Assert.assertEquals(
			fragmentEntry.getType(), copyFragmentEntry.getType());
	}

	private String _read(String fileName) throws Exception {
		return new String(
			FileUtil.getBytes(getClass(), "dependencies/" + fileName));
	}

	private FragmentCollection _fragmentCollection;

	@Inject
	private FragmentEntryLocalService _fragmentEntryLocalService;

	@Inject
	private FragmentEntryService _fragmentEntryService;

	@DeleteAfterTestRun
	private Group _group;

	private FragmentCollection _updatedFragmentCollection;

}