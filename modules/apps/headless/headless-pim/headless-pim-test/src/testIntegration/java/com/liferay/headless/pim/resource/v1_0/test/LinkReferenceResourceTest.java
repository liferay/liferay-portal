/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.pim.resource.v1_0.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.depot.model.DepotEntry;
import com.liferay.headless.pim.client.dto.v1_0.LinkReference;
import com.liferay.headless.pim.client.pagination.Page;
import com.liferay.headless.pim.client.pagination.Pagination;
import com.liferay.object.model.ObjectEntry;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.site.pim.site.initializer.engine.PIMLinkEngine;
import com.liferay.site.pim.site.initializer.link.PIMLinkType;
import com.liferay.site.pim.site.initializer.test.util.PIMBaseSKUTestUtil;
import com.liferay.site.pim.site.initializer.test.util.PIMTestUtil;
import com.liferay.site.pim.site.initializer.test.util.link.TestPIMLinkType;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceRegistration;

/**
 * @author Stefano Motta
 */
@FeatureFlag("LPD-96666")
@RunWith(Arquillian.class)
public class LinkReferenceResourceTest
	extends BaseLinkReferenceResourceTestCase {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	@Override
	public void setUp() throws Exception {
		super.setUp();

		PIMTestUtil.getOrAddGroup();

		Bundle bundle = FrameworkUtil.getBundle(
			LinkReferenceResourceTest.class);

		BundleContext bundleContext = bundle.getBundleContext();

		_serviceRegistration = bundleContext.registerService(
			PIMLinkType.class, new TestPIMLinkType(), null);
	}

	@After
	@Override
	public void tearDown() throws Exception {
		super.tearDown();

		_serviceRegistration.unregister();
	}

	@Override
	@Test
	public void testGetScopeScopeKeyLinksPage() throws Exception {
		DepotEntry depotEntry = PIMTestUtil.addSpaceDepotEntry();

		ObjectEntry sourceObjectEntry =
			PIMBaseSKUTestUtil.addPIMBaseSKUObjectEntry(
				depotEntry.getGroupId());

		Page<LinkReference> page = _getLinkReferencesPage(
			sourceObjectEntry, Pagination.of(1, 20));

		Assert.assertEquals(0, page.getTotalCount());

		ObjectEntry targetObjectEntry1 =
			PIMBaseSKUTestUtil.addPIMBaseSKUObjectEntry(
				depotEntry.getGroupId());
		ObjectEntry targetObjectEntry2 =
			PIMBaseSKUTestUtil.addPIMBaseSKUObjectEntry(
				depotEntry.getGroupId());

		_pimLinkEngine.addPIMLinks(
			sourceObjectEntry,
			Arrays.asList(targetObjectEntry1, targetObjectEntry2), _TYPE);

		page = _getLinkReferencesPage(sourceObjectEntry, Pagination.of(1, 20));

		Assert.assertEquals(2, page.getTotalCount());

		List<LinkReference> linkReferences =
			(List<LinkReference>)page.getItems();

		Assert.assertNull(_getLinkReference(linkReferences, sourceObjectEntry));

		_assertLinkReference(linkReferences, targetObjectEntry1);
		_assertLinkReference(linkReferences, targetObjectEntry2);
	}

	@Override
	@Test
	public void testGetScopeScopeKeyLinksPageWithPagination() throws Exception {
		DepotEntry depotEntry = PIMTestUtil.addSpaceDepotEntry();

		ObjectEntry sourceObjectEntry =
			PIMBaseSKUTestUtil.addPIMBaseSKUObjectEntry(
				depotEntry.getGroupId());

		_pimLinkEngine.addPIMLinks(
			sourceObjectEntry,
			Arrays.asList(
				PIMBaseSKUTestUtil.addPIMBaseSKUObjectEntry(
					depotEntry.getGroupId()),
				PIMBaseSKUTestUtil.addPIMBaseSKUObjectEntry(
					depotEntry.getGroupId()),
				PIMBaseSKUTestUtil.addPIMBaseSKUObjectEntry(
					depotEntry.getGroupId())),
			_TYPE);

		Page<LinkReference> page = _getLinkReferencesPage(
			sourceObjectEntry, Pagination.of(1, 2));

		Assert.assertEquals(3, page.getTotalCount());

		List<LinkReference> linkReferences =
			(List<LinkReference>)page.getItems();

		Assert.assertEquals(
			linkReferences.toString(), 2, linkReferences.size());
	}

	private void _assertLinkReference(
		List<LinkReference> linkReferences, ObjectEntry objectEntry) {

		LinkReference linkReference = _getLinkReference(
			linkReferences, objectEntry);

		Assert.assertEquals("approved", linkReference.getStatus());

		Map<String, Map<String, String>> actions = linkReference.getActions();

		Map<String, String> deleteAction = actions.get("delete");

		String href = deleteAction.get("href");

		Assert.assertTrue(href, href.contains("/links?className="));

		Assert.assertEquals("DELETE", deleteAction.get("method"));
	}

	private LinkReference _getLinkReference(
		List<LinkReference> linkReferences, ObjectEntry objectEntry) {

		for (LinkReference linkReference : linkReferences) {
			if (Objects.equals(
					linkReference.getExternalReferenceCode(),
					objectEntry.getExternalReferenceCode())) {

				return linkReference;
			}
		}

		return null;
	}

	private Page<LinkReference> _getLinkReferencesPage(
			ObjectEntry objectEntry, Pagination pagination)
		throws Exception {

		return linkReferenceResource.getScopeScopeKeyLinksPage(
			String.valueOf(objectEntry.getGroupId()),
			objectEntry.getModelClassName(),
			objectEntry.getExternalReferenceCode(), pagination);
	}

	private static final String _TYPE = "variant";

	@Inject
	private PIMLinkEngine _pimLinkEngine;

	private ServiceRegistration<PIMLinkType> _serviceRegistration;

}