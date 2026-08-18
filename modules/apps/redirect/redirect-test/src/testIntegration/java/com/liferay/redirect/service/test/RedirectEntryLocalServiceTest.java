/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.redirect.service.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.LayoutFriendlyURLException;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.DateUtil;
import com.liferay.portal.kernel.util.FriendlyURLNormalizer;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.redirect.exception.CircularRedirectEntryException;
import com.liferay.redirect.exception.DuplicateRedirectEntrySourceURLException;
import com.liferay.redirect.exception.RequiredRedirectEntrySourceURLException;
import com.liferay.redirect.model.RedirectEntry;
import com.liferay.redirect.model.RedirectNotFoundEntry;
import com.liferay.redirect.service.RedirectEntryLocalService;
import com.liferay.redirect.service.RedirectNotFoundEntryLocalService;

import java.time.Instant;

import java.util.Date;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Alejandro Tardín
 * @author Roberto Díaz
 */
@RunWith(Arquillian.class)
public class RedirectEntryLocalServiceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() throws Exception {
		UserTestUtil.setUser(TestPropsValues.getUser());

		_group = GroupTestUtil.addGroup();
	}

	@Test
	public void testAddRedirectEntryDeletesRedirectNotFoundEntry()
		throws Exception {

		_redirectNotFoundEntry =
			_redirectNotFoundEntryLocalService.addOrUpdateRedirectNotFoundEntry(
				_groupLocalService.getGroup(_group.getGroupId()), _URL_SOURCE);

		_redirectEntry = _redirectEntryLocalService.addRedirectEntry(
			_group.getGroupId(), _URL_DESTINATION, null, false, _URL_SOURCE,
			ServiceContextTestUtil.getServiceContext());

		Assert.assertNull(
			_redirectNotFoundEntryLocalService.fetchRedirectNotFoundEntry(
				_group.getGroupId(), _URL_SOURCE));
	}

	@Test
	public void testAddRedirectEntryDoesNotFixAChainByDestinationURL()
		throws Exception {

		_redirectEntry = _redirectEntryLocalService.addRedirectEntry(
			_group.getGroupId(), _URL_DESTINATION, null, false, _URL_SOURCE,
			ServiceContextTestUtil.getServiceContext());

		_chainedRedirectEntry = _redirectEntryLocalService.addRedirectEntry(
			_group.getGroupId(), _appendGroupBaseURL(_URL_SOURCE), null,
			_URL_GROUP_BASE, false, _URL_ANOTHER_SOURCE, false,
			ServiceContextTestUtil.getServiceContext());

		Assert.assertEquals(
			_friendlyURLNormalizer.normalizeWithEncoding(_URL_ANOTHER_SOURCE),
			_chainedRedirectEntry.getSourceURL());

		Assert.assertEquals(
			_appendGroupBaseURL(_URL_SOURCE),
			_chainedRedirectEntry.getDestinationURL());
	}

	@Test
	public void testAddRedirectEntryDoesNotFixAChainBySourceURL()
		throws Exception {

		_redirectEntry = _redirectEntryLocalService.addRedirectEntry(
			_group.getGroupId(),
			_appendGroupBaseURL(_URL_INTERMEDIATE_DESTINATION), null, false,
			_URL_SOURCE, ServiceContextTestUtil.getServiceContext());

		_chainedRedirectEntry = _redirectEntryLocalService.addRedirectEntry(
			_group.getGroupId(), _URL_FINAL_DESTINATION, null, _URL_GROUP_BASE,
			false, _URL_INTERMEDIATE_DESTINATION, false,
			ServiceContextTestUtil.getServiceContext());

		Assert.assertEquals(
			_friendlyURLNormalizer.normalizeWithEncoding(
				_URL_INTERMEDIATE_DESTINATION),
			_chainedRedirectEntry.getSourceURL());

		Assert.assertEquals(
			_URL_FINAL_DESTINATION, _chainedRedirectEntry.getDestinationURL());

		_redirectEntry = _redirectEntryLocalService.fetchRedirectEntry(
			_redirectEntry.getRedirectEntryId());

		Assert.assertEquals(
			_friendlyURLNormalizer.normalizeWithEncoding(_URL_SOURCE),
			_redirectEntry.getSourceURL());

		Assert.assertEquals(
			_appendGroupBaseURL(_URL_INTERMEDIATE_DESTINATION),
			_redirectEntry.getDestinationURL());
	}

	@Test(expected = DuplicateRedirectEntrySourceURLException.class)
	public void testAddRedirectEntryFailsWhenDuplicateSourceURL()
		throws Exception {

		_redirectEntry = _redirectEntryLocalService.addRedirectEntry(
			_group.getGroupId(), _URL_DESTINATION, null, false, _URL_SOURCE,
			ServiceContextTestUtil.getServiceContext());

		_redirectEntryLocalService.addRedirectEntry(
			_group.getGroupId(), _URL_DESTINATION, null, false, _URL_SOURCE,
			ServiceContextTestUtil.getServiceContext());
	}

	@Test(expected = DuplicateRedirectEntrySourceURLException.class)
	public void testAddRedirectEntryFailsWhenDuplicateSourceURLAndDifferentType()
		throws Exception {

		_redirectEntry = _redirectEntryLocalService.addRedirectEntry(
			_group.getGroupId(), _URL_DESTINATION, null, true, _URL_SOURCE,
			ServiceContextTestUtil.getServiceContext());

		_redirectEntryLocalService.addRedirectEntry(
			_group.getGroupId(), _URL_DESTINATION, null, false, _URL_SOURCE,
			ServiceContextTestUtil.getServiceContext());
	}

	@Test(expected = DuplicateRedirectEntrySourceURLException.class)
	public void testAddRedirectEntryFailsWhenDuplicateSourceURLAndExpirationDate()
		throws Exception {

		_redirectEntry = _redirectEntryLocalService.addRedirectEntry(
			_group.getGroupId(), _URL_DESTINATION, new Date(), true,
			_URL_SOURCE, ServiceContextTestUtil.getServiceContext());

		_redirectEntryLocalService.addRedirectEntry(
			_group.getGroupId(), _URL_DESTINATION, new Date(), false,
			_URL_SOURCE, ServiceContextTestUtil.getServiceContext());
	}

	@Test(expected = DuplicateRedirectEntrySourceURLException.class)
	public void testAddRedirectEntryFailsWhenDuplicateSourceURLDiffersUpperCaseLowerCase()
		throws Exception {

		_redirectEntry = _redirectEntryLocalService.addRedirectEntry(
			_group.getGroupId(), _appendGroupBaseURL(_URL_DESTINATION), null,
			false, StringUtil.toUpperCase(_URL_SOURCE),
			ServiceContextTestUtil.getServiceContext());

		_redirectEntry = _redirectEntryLocalService.addRedirectEntry(
			_group.getGroupId(), _URL_DESTINATION, null, false,
			StringUtil.toLowerCase(_URL_SOURCE),
			ServiceContextTestUtil.getServiceContext());
	}

	@Test(
		expected = CircularRedirectEntryException.MustNotFormALoopWithAnotherRedirectEntry.class
	)
	public void testAddRedirectEntryFailsWhenRedirectLoop() throws Exception {
		_redirectEntry = _redirectEntryLocalService.addRedirectEntry(
			_group.getGroupId(),
			_appendGroupBaseURL(
				_friendlyURLNormalizer.normalizeWithEncoding(_URL_DESTINATION)),
			null, false, _URL_SOURCE,
			ServiceContextTestUtil.getServiceContext());

		_chainedRedirectEntry = _redirectEntryLocalService.addRedirectEntry(
			_group.getGroupId(),
			_appendGroupBaseURL(
				_friendlyURLNormalizer.normalizeWithEncoding(_URL_SOURCE)),
			null, _URL_GROUP_BASE, false, _URL_DESTINATION, false,
			ServiceContextTestUtil.getServiceContext());
	}

	@Test(
		expected = CircularRedirectEntryException.MustNotFormALoopWithAnotherRedirectEntry.class
	)
	public void testAddRedirectEntryFailsWhenRedirectLoopSourceURLDiffersUpperCaseLowerCase()
		throws Exception {

		_redirectEntry = _redirectEntryLocalService.addRedirectEntry(
			_group.getGroupId(), _appendGroupBaseURL(_URL_DESTINATION), null,
			false, StringUtil.toUpperCase(_URL_SOURCE),
			ServiceContextTestUtil.getServiceContext());

		_chainedRedirectEntry = _redirectEntryLocalService.addRedirectEntry(
			_group.getGroupId(),
			_appendGroupBaseURL(StringUtil.toLowerCase(_URL_SOURCE)), null,
			_URL_GROUP_BASE, false, _URL_DESTINATION, false,
			ServiceContextTestUtil.getServiceContext());
	}

	@Test(
		expected = CircularRedirectEntryException.DestinationURLMustNotBeEqualToSourceURL.class
	)
	public void testAddRedirectEntryFailsWhenSameDestinationAndSourceURL()
		throws Exception {

		_redirectEntry = _redirectEntryLocalService.addRedirectEntry(
			_group.getGroupId(), _appendGroupBaseURL(_URL_SOURCE), null,
			_URL_GROUP_BASE, false, _URL_SOURCE, false,
			ServiceContextTestUtil.getServiceContext());
	}

	@Test(expected = LayoutFriendlyURLException.class)
	public void testAddRedirectEntryFailsWhenSourceURLDoubleSlash()
		throws Exception {

		_redirectEntry = _redirectEntryLocalService.addRedirectEntry(
			_group.getGroupId(), _URL_DESTINATION, null, false, "a//a",
			ServiceContextTestUtil.getServiceContext());
	}

	@Test(expected = LayoutFriendlyURLException.class)
	public void testAddRedirectEntryFailsWhenSourceURLEndSlash()
		throws Exception {

		_redirectEntry = _redirectEntryLocalService.addRedirectEntry(
			_group.getGroupId(), _URL_DESTINATION, null, false, "a/",
			ServiceContextTestUtil.getServiceContext());
	}

	@Test(expected = LayoutFriendlyURLException.class)
	public void testAddRedirectEntryFailsWhenSourceURLFriendlyURLMapper()
		throws Exception {

		_redirectEntryLocalService.addRedirectEntry(
			_group.getGroupId(), "http://www.liferay.com", null, false,
			"questions",
			ServiceContextTestUtil.getServiceContext(_group.getGroupId()));
	}

	@Test(expected = LayoutFriendlyURLException.class)
	public void testAddRedirectEntryFailsWhenSourceURLLayoutFriendlyUrlKeywordsProperty()
		throws Exception {

		_redirectEntryLocalService.addRedirectEntry(
			_group.getGroupId(), "http://www.liferay.com", null, false,
			"tunnel-web",
			ServiceContextTestUtil.getServiceContext(_group.getGroupId()));
	}

	@Test(expected = RequiredRedirectEntrySourceURLException.class)
	public void testAddRedirectEntryFailsWhenSourceURLNull() throws Exception {
		_redirectEntryLocalService.addRedirectEntry(
			_group.getGroupId(), "http://www.liferay.com", null, false,
			StringPool.BLANK,
			ServiceContextTestUtil.getServiceContext(_group.getGroupId()));
	}

	@Test(expected = LayoutFriendlyURLException.class)
	public void testAddRedirectEntryFailsWhenSourceURLOneCharacterLong()
		throws Exception {

		_redirectEntryLocalService.addRedirectEntry(
			_group.getGroupId(), "http://www.liferay.com", null, false, "a",
			ServiceContextTestUtil.getServiceContext(_group.getGroupId()));
	}

	@Test(expected = LayoutFriendlyURLException.class)
	public void testAddRedirectEntryFailsWhenSourceURLPortalFriendlyURLSeparator()
		throws Exception {

		_redirectEntryLocalService.addRedirectEntry(
			_group.getGroupId(), "http://www.liferay.com", null, false,
			"-/test",
			ServiceContextTestUtil.getServiceContext(_group.getGroupId()));
	}

	@Test(expected = LayoutFriendlyURLException.class)
	public void testAddRedirectEntryFailsWhenSourceURLStartSlash()
		throws Exception {

		_redirectEntry = _redirectEntryLocalService.addRedirectEntry(
			_group.getGroupId(), _URL_DESTINATION, null, false, "/a",
			ServiceContextTestUtil.getServiceContext());
	}

	@Test(expected = LayoutFriendlyURLException.class)
	public void testAddRedirectEntryFailsWhenSourceURLTooLong()
		throws Exception {

		_redirectEntry = _redirectEntryLocalService.addRedirectEntry(
			_group.getGroupId(), _URL_DESTINATION, null, false,
			StringUtil.randomString(256),
			ServiceContextTestUtil.getServiceContext());
	}

	@Test(expected = LayoutFriendlyURLException.class)
	public void testAddRedirectEntryFailsWhenSourceURLURLanguagePath()
		throws Exception {

		_redirectEntryLocalService.addRedirectEntry(
			_group.getGroupId(), "http://www.liferay.com", null, false,
			"es/test",
			ServiceContextTestUtil.getServiceContext(_group.getGroupId()));
	}

	@Test(expected = LayoutFriendlyURLException.class)
	public void testAddRedirectEntryFailsWhenSourceURLURLSeparator()
		throws Exception {

		_redirectEntryLocalService.addRedirectEntry(
			_group.getGroupId(), "http://www.liferay.com", null, false, "/b/",
			ServiceContextTestUtil.getServiceContext(_group.getGroupId()));
	}

	@Test(
		expected = CircularRedirectEntryException.MustNotFormALoopWithAnotherRedirectEntry.class
	)
	public void testAddRedirectEntryFailsWhenUpdateChainedRedirectEntriesCausesARedirectLoop()
		throws Exception {

		_redirectEntry = _redirectEntryLocalService.addRedirectEntry(
			_group.getGroupId(),
			_appendGroupBaseURL(
				_friendlyURLNormalizer.normalizeWithEncoding(
					_URL_INTERMEDIATE)),
			null, false, _URL_SOURCE,
			ServiceContextTestUtil.getServiceContext());

		_chainedRedirectEntry = _redirectEntryLocalService.addRedirectEntry(
			_group.getGroupId(),
			_appendGroupBaseURL(
				_friendlyURLNormalizer.normalizeWithEncoding(_URL_SOURCE)),
			null, _URL_GROUP_BASE, false, _URL_DESTINATION, false,
			ServiceContextTestUtil.getServiceContext());

		_intermediateRedirectEntry =
			_redirectEntryLocalService.addRedirectEntry(
				_group.getGroupId(),
				_appendGroupBaseURL(
					_friendlyURLNormalizer.normalizeWithEncoding(
						_URL_DESTINATION)),
				null, _URL_GROUP_BASE, false, _URL_INTERMEDIATE, true,
				ServiceContextTestUtil.getServiceContext());
	}

	@Test
	public void testAddRedirectEntryFixesAChainByDestinationURL()
		throws Exception {

		_redirectEntry = _redirectEntryLocalService.addRedirectEntry(
			_group.getGroupId(), _URL_DESTINATION, null, false, _URL_SOURCE,
			ServiceContextTestUtil.getServiceContext());

		_chainedRedirectEntry = _redirectEntryLocalService.addRedirectEntry(
			_group.getGroupId(),
			_appendGroupBaseURL(
				_friendlyURLNormalizer.normalizeWithEncoding(_URL_SOURCE)),
			null, _URL_GROUP_BASE, false, _URL_ANOTHER_SOURCE, true,
			ServiceContextTestUtil.getServiceContext());

		Assert.assertEquals(
			_friendlyURLNormalizer.normalizeWithEncoding(_URL_ANOTHER_SOURCE),
			_chainedRedirectEntry.getSourceURL());

		Assert.assertEquals(
			_URL_DESTINATION, _chainedRedirectEntry.getDestinationURL());
	}

	@Test
	public void testAddRedirectEntryFixesAChainBySourceURL() throws Exception {
		String normalizedIntermediateDestinationURL =
			_friendlyURLNormalizer.normalizeWithEncoding(
				_URL_INTERMEDIATE_DESTINATION);

		_redirectEntry = _redirectEntryLocalService.addRedirectEntry(
			_group.getGroupId(),
			_appendGroupBaseURL(normalizedIntermediateDestinationURL), null,
			false, _URL_SOURCE, ServiceContextTestUtil.getServiceContext());

		_chainedRedirectEntry = _redirectEntryLocalService.addRedirectEntry(
			_group.getGroupId(), _URL_FINAL_DESTINATION, null, _URL_GROUP_BASE,
			false, _URL_INTERMEDIATE_DESTINATION, true,
			ServiceContextTestUtil.getServiceContext());

		Assert.assertEquals(
			normalizedIntermediateDestinationURL,
			_chainedRedirectEntry.getSourceURL());

		Assert.assertEquals(
			_URL_FINAL_DESTINATION, _chainedRedirectEntry.getDestinationURL());

		_redirectEntry = _redirectEntryLocalService.fetchRedirectEntry(
			_redirectEntry.getRedirectEntryId());

		Assert.assertEquals(
			_friendlyURLNormalizer.normalizeWithEncoding(_URL_SOURCE),
			_redirectEntry.getSourceURL());

		Assert.assertEquals(
			_URL_FINAL_DESTINATION, _redirectEntry.getDestinationURL());
	}

	@Test
	public void testAddRedirectEntryNotNormalizedSourceURL() throws Exception {
		_redirectEntry = _redirectEntryLocalService.addRedirectEntry(
			_group.getGroupId(), _URL_DESTINATION, null, false, "attaché",
			ServiceContextTestUtil.getServiceContext());

		Assert.assertEquals(
			_friendlyURLNormalizer.normalizeWithEncoding("attaché"),
			_redirectEntry.getSourceURL());
	}

	@Test
	public void testAddRedirectEntryWithTwoStepRedirectLoop() throws Exception {
		_redirectEntry = _redirectEntryLocalService.addRedirectEntry(
			_group.getGroupId(), _appendGroupBaseURL(_URL_INTERMEDIATE), null,
			false, _URL_SOURCE, ServiceContextTestUtil.getServiceContext());

		_chainedRedirectEntry = _redirectEntryLocalService.addRedirectEntry(
			_group.getGroupId(), _appendGroupBaseURL(_URL_SOURCE), null,
			_URL_GROUP_BASE, false, _URL_DESTINATION, false,
			ServiceContextTestUtil.getServiceContext());

		_intermediateRedirectEntry =
			_redirectEntryLocalService.addRedirectEntry(
				_group.getGroupId(), _appendGroupBaseURL(_URL_DESTINATION),
				null, _URL_GROUP_BASE, false, _URL_INTERMEDIATE, false,
				ServiceContextTestUtil.getServiceContext());

		_redirectEntry = _redirectEntryLocalService.fetchRedirectEntry(
			_redirectEntry.getRedirectEntryId());

		Assert.assertEquals(
			_friendlyURLNormalizer.normalizeWithEncoding(_URL_SOURCE),
			_redirectEntry.getSourceURL());

		Assert.assertEquals(
			_appendGroupBaseURL(_URL_INTERMEDIATE),
			_redirectEntry.getDestinationURL());

		_chainedRedirectEntry = _redirectEntryLocalService.fetchRedirectEntry(
			_chainedRedirectEntry.getRedirectEntryId());

		Assert.assertEquals(
			_friendlyURLNormalizer.normalizeWithEncoding(_URL_DESTINATION),
			_chainedRedirectEntry.getSourceURL());

		Assert.assertEquals(
			_appendGroupBaseURL(_URL_SOURCE),
			_chainedRedirectEntry.getDestinationURL());

		Assert.assertEquals(
			_friendlyURLNormalizer.normalizeWithEncoding(_URL_INTERMEDIATE),
			_intermediateRedirectEntry.getSourceURL());

		Assert.assertEquals(
			_appendGroupBaseURL(_URL_DESTINATION),
			_intermediateRedirectEntry.getDestinationURL());
	}

	@Test
	public void testFetchExpiredRedirectEntry() throws Exception {
		Instant instant = Instant.now();

		_redirectEntry = _redirectEntryLocalService.addRedirectEntry(
			_group.getGroupId(), _URL_DESTINATION,
			Date.from(instant.minusSeconds(3600)), false, _URL_SOURCE,
			ServiceContextTestUtil.getServiceContext());

		Assert.assertNull(
			_redirectEntryLocalService.fetchRedirectEntry(
				_group.getGroupId(), _URL_SOURCE));
	}

	@Test
	public void testFetchNotExpiredRedirectEntry() throws Exception {
		Instant instant = Instant.now();

		_redirectEntry = _redirectEntryLocalService.addRedirectEntry(
			_group.getGroupId(), _URL_DESTINATION,
			Date.from(instant.plusSeconds(3600)), false, _URL_SOURCE,
			ServiceContextTestUtil.getServiceContext());

		Assert.assertEquals(
			_redirectEntry,
			_redirectEntryLocalService.fetchRedirectEntry(
				_group.getGroupId(),
				_friendlyURLNormalizer.normalizeWithEncoding(_URL_SOURCE)));
	}

	@Test
	public void testFetchRedirectEntry() throws Exception {
		_redirectEntry = _redirectEntryLocalService.addRedirectEntry(
			_group.getGroupId(), _URL_DESTINATION, null, false, _URL_SOURCE,
			ServiceContextTestUtil.getServiceContext());

		Assert.assertEquals(
			_redirectEntry,
			_redirectEntryLocalService.fetchRedirectEntry(
				_group.getGroupId(),
				_friendlyURLNormalizer.normalizeWithEncoding(_URL_SOURCE)));
	}

	@Test
	public void testFetchRedirectEntryDoesNotUpdateTheLastOccurrenceDate()
		throws Exception {

		_redirectEntry = _redirectEntryLocalService.addRedirectEntry(
			_group.getGroupId(), _URL_DESTINATION, null, false, _URL_SOURCE,
			ServiceContextTestUtil.getServiceContext());

		Assert.assertNull(_redirectEntry.getLastOccurrenceDate());

		_redirectEntry = _redirectEntryLocalService.fetchRedirectEntry(
			_group.getGroupId(),
			_friendlyURLNormalizer.normalizeWithEncoding(_URL_SOURCE));

		Assert.assertNull(_redirectEntry.getLastOccurrenceDate());
	}

	@Test
	public void testFetchRedirectEntryUpdatesTheLastOccurrenceDate()
		throws Exception {

		_redirectEntry = _redirectEntryLocalService.addRedirectEntry(
			_group.getGroupId(), _URL_DESTINATION, null, false, _URL_SOURCE,
			ServiceContextTestUtil.getServiceContext());

		Assert.assertNull(_redirectEntry.getLastOccurrenceDate());

		_redirectEntry = _redirectEntryLocalService.fetchRedirectEntry(
			_group.getGroupId(),
			_friendlyURLNormalizer.normalizeWithEncoding(_URL_SOURCE), true);

		Date lastOccurrenceDate = _redirectEntry.getLastOccurrenceDate();

		Assert.assertTrue(lastOccurrenceDate.before(DateUtil.newDate()));
	}

	@Test
	public void testFetchRedirectEntryUpdatesTheLastOccurrenceDateOnceADay()
		throws Exception {

		_redirectEntry = _redirectEntryLocalService.addRedirectEntry(
			_group.getGroupId(), _URL_DESTINATION, null, false, _URL_SOURCE,
			ServiceContextTestUtil.getServiceContext());

		Assert.assertNull(_redirectEntry.getLastOccurrenceDate());

		_redirectEntry = _redirectEntryLocalService.fetchRedirectEntry(
			_group.getGroupId(),
			_friendlyURLNormalizer.normalizeWithEncoding(_URL_SOURCE), true);

		Date lastOccurrenceDate = _redirectEntry.getLastOccurrenceDate();

		_redirectEntry = _redirectEntryLocalService.fetchRedirectEntry(
			_group.getGroupId(),
			_friendlyURLNormalizer.normalizeWithEncoding(_URL_SOURCE), true);

		Assert.assertTrue(
			DateUtil.equals(
				lastOccurrenceDate, _redirectEntry.getLastOccurrenceDate()));
	}

	@Test
	public void testUpdateRedirectEntryDoesNotFixAChainByDestinationURL()
		throws Exception {

		_redirectEntry = _redirectEntryLocalService.addRedirectEntry(
			_group.getGroupId(), _URL_DESTINATION, null, false, _URL_SOURCE,
			ServiceContextTestUtil.getServiceContext());

		_chainedRedirectEntry = _redirectEntryLocalService.addRedirectEntry(
			_group.getGroupId(), _appendGroupBaseURL(_URL_SOURCE), null,
			_URL_GROUP_BASE, false, _URL_ANOTHER_SOURCE, false,
			ServiceContextTestUtil.getServiceContext());

		_chainedRedirectEntry = _redirectEntryLocalService.updateRedirectEntry(
			_chainedRedirectEntry.getRedirectEntryId(),
			_appendGroupBaseURL(_URL_SOURCE), null, _URL_GROUP_BASE, false,
			_URL_ANOTHER_SOURCE, false);

		Assert.assertEquals(
			_friendlyURLNormalizer.normalizeWithEncoding(_URL_ANOTHER_SOURCE),
			_chainedRedirectEntry.getSourceURL());

		Assert.assertEquals(
			_appendGroupBaseURL(_URL_SOURCE),
			_chainedRedirectEntry.getDestinationURL());
	}

	@Test
	public void testUpdateRedirectEntryDoesNotFixAChainBySourceURL()
		throws Exception {

		_redirectEntry = _redirectEntryLocalService.addRedirectEntry(
			_group.getGroupId(),
			_appendGroupBaseURL(_URL_INTERMEDIATE_DESTINATION), null, false,
			_URL_SOURCE, ServiceContextTestUtil.getServiceContext());

		_chainedRedirectEntry = _redirectEntryLocalService.addRedirectEntry(
			_group.getGroupId(), _URL_FINAL_DESTINATION, null, _URL_GROUP_BASE,
			false, _URL_INTERMEDIATE_DESTINATION, false,
			ServiceContextTestUtil.getServiceContext());

		_chainedRedirectEntry = _redirectEntryLocalService.updateRedirectEntry(
			_chainedRedirectEntry.getRedirectEntryId(), _URL_FINAL_DESTINATION,
			null, _URL_GROUP_BASE, false, _URL_INTERMEDIATE_DESTINATION, false);

		Assert.assertEquals(
			_friendlyURLNormalizer.normalizeWithEncoding(
				_URL_INTERMEDIATE_DESTINATION),
			_chainedRedirectEntry.getSourceURL());

		Assert.assertEquals(
			_URL_FINAL_DESTINATION, _chainedRedirectEntry.getDestinationURL());

		_redirectEntry = _redirectEntryLocalService.fetchRedirectEntry(
			_redirectEntry.getRedirectEntryId());

		Assert.assertEquals(
			_friendlyURLNormalizer.normalizeWithEncoding(_URL_SOURCE),
			_redirectEntry.getSourceURL());

		Assert.assertEquals(
			_appendGroupBaseURL(_URL_INTERMEDIATE_DESTINATION),
			_redirectEntry.getDestinationURL());
	}

	@Test(
		expected = CircularRedirectEntryException.DestinationURLMustNotBeEqualToSourceURL.class
	)
	public void testUpdateRedirectEntryFailsWhenSameDestinationAndSourceURL()
		throws Exception {

		_redirectEntry = _redirectEntryLocalService.addRedirectEntry(
			_group.getGroupId(), _appendGroupBaseURL(_URL_DESTINATION), null,
			_URL_GROUP_BASE, false, _URL_SOURCE, false,
			ServiceContextTestUtil.getServiceContext());

		Assert.assertEquals(
			_friendlyURLNormalizer.normalizeWithEncoding(_URL_SOURCE),
			_redirectEntry.getSourceURL());
		Assert.assertEquals(
			_appendGroupBaseURL(_URL_DESTINATION),
			_redirectEntry.getDestinationURL());

		_redirectEntry = _redirectEntryLocalService.updateRedirectEntry(
			_redirectEntry.getRedirectEntryId(),
			_appendGroupBaseURL(_URL_SOURCE), null, _URL_GROUP_BASE, false,
			_URL_SOURCE, false);
	}

	@Test
	public void testUpdateRedirectEntryFixesAChainByDestinationURL()
		throws Exception {

		_redirectEntry = _redirectEntryLocalService.addRedirectEntry(
			_group.getGroupId(), _URL_DESTINATION, null, false, _URL_SOURCE,
			ServiceContextTestUtil.getServiceContext());

		String groupSourceURL = _appendGroupBaseURL(
			_friendlyURLNormalizer.normalizeWithEncoding(_URL_SOURCE));

		_chainedRedirectEntry = _redirectEntryLocalService.addRedirectEntry(
			_group.getGroupId(), groupSourceURL, null, _URL_GROUP_BASE, false,
			_URL_ANOTHER_SOURCE, false,
			ServiceContextTestUtil.getServiceContext());

		_chainedRedirectEntry = _redirectEntryLocalService.updateRedirectEntry(
			_chainedRedirectEntry.getRedirectEntryId(), groupSourceURL, null,
			_URL_GROUP_BASE, false, _URL_ANOTHER_SOURCE, true);

		Assert.assertEquals(
			_friendlyURLNormalizer.normalizeWithEncoding(_URL_ANOTHER_SOURCE),
			_chainedRedirectEntry.getSourceURL());

		Assert.assertEquals(
			_URL_DESTINATION, _chainedRedirectEntry.getDestinationURL());
	}

	@Test
	public void testUpdateRedirectEntryFixesAChainBySourceURL()
		throws Exception {

		String normalizedIntermediateDestinationURL =
			_friendlyURLNormalizer.normalizeWithEncoding(
				_URL_INTERMEDIATE_DESTINATION);

		_redirectEntry = _redirectEntryLocalService.addRedirectEntry(
			_group.getGroupId(),
			_appendGroupBaseURL(normalizedIntermediateDestinationURL), null,
			false, _URL_SOURCE, ServiceContextTestUtil.getServiceContext());

		_chainedRedirectEntry = _redirectEntryLocalService.addRedirectEntry(
			_group.getGroupId(), _URL_FINAL_DESTINATION, null, _URL_GROUP_BASE,
			false, _URL_INTERMEDIATE_DESTINATION, false,
			ServiceContextTestUtil.getServiceContext());

		_chainedRedirectEntry = _redirectEntryLocalService.updateRedirectEntry(
			_chainedRedirectEntry.getRedirectEntryId(), _URL_FINAL_DESTINATION,
			null, _URL_GROUP_BASE, false, _URL_INTERMEDIATE_DESTINATION, true);

		Assert.assertEquals(
			normalizedIntermediateDestinationURL,
			_chainedRedirectEntry.getSourceURL());

		Assert.assertEquals(
			_URL_FINAL_DESTINATION, _chainedRedirectEntry.getDestinationURL());

		_redirectEntry = _redirectEntryLocalService.fetchRedirectEntry(
			_redirectEntry.getRedirectEntryId());

		Assert.assertEquals(
			_friendlyURLNormalizer.normalizeWithEncoding(_URL_SOURCE),
			_redirectEntry.getSourceURL());

		Assert.assertEquals(
			_URL_FINAL_DESTINATION, _redirectEntry.getDestinationURL());
	}

	@Test
	public void testUpdateRedirectEntryNormalizedSourceURL() throws Exception {
		_redirectEntry = _redirectEntryLocalService.addRedirectEntry(
			_group.getGroupId(), _URL_DESTINATION, null, false, _URL_SOURCE,
			ServiceContextTestUtil.getServiceContext());

		_redirectEntry = _redirectEntryLocalService.updateRedirectEntry(
			_redirectEntry.getRedirectEntryId(), _URL_DESTINATION, null, false,
			"attaché");

		Assert.assertEquals(
			_friendlyURLNormalizer.normalizeWithEncoding("attaché"),
			_redirectEntry.getSourceURL());
	}

	private String _appendGroupBaseURL(String url) {
		return _URL_GROUP_BASE + StringPool.SLASH + url;
	}

	private static final String _URL_ANOTHER_SOURCE =
		RandomTestUtil.randomString();

	private static final String _URL_DESTINATION =
		RandomTestUtil.randomString();

	private static final String _URL_FINAL_DESTINATION =
		RandomTestUtil.randomString();

	private static final String _URL_GROUP_BASE = RandomTestUtil.randomString();

	private static final String _URL_INTERMEDIATE =
		RandomTestUtil.randomString();

	private static final String _URL_INTERMEDIATE_DESTINATION =
		RandomTestUtil.randomString();

	private static final String _URL_SOURCE = RandomTestUtil.randomString();

	@DeleteAfterTestRun
	private RedirectEntry _chainedRedirectEntry;

	@Inject
	private FriendlyURLNormalizer _friendlyURLNormalizer;

	@DeleteAfterTestRun
	private Group _group;

	@Inject
	private GroupLocalService _groupLocalService;

	@DeleteAfterTestRun
	private RedirectEntry _intermediateRedirectEntry;

	@DeleteAfterTestRun
	private RedirectEntry _redirectEntry;

	@Inject
	private RedirectEntryLocalService _redirectEntryLocalService;

	@DeleteAfterTestRun
	private RedirectNotFoundEntry _redirectNotFoundEntry;

	@Inject
	private RedirectNotFoundEntryLocalService
		_redirectNotFoundEntryLocalService;

}