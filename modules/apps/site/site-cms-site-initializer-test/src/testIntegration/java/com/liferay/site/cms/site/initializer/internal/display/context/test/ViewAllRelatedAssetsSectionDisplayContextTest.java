/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.cms.site.initializer.internal.display.context.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.fragment.renderer.FragmentRenderer;
import com.liferay.info.constants.InfoDisplayWebKeys;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.model.ObjectRelationship;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.object.service.ObjectRelationshipLocalService;
import com.liferay.object.test.util.ObjectDefinitionTestUtil;
import com.liferay.object.test.util.ObjectRelationshipTestUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.rule.Sync;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.FeatureFlags;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Collections;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.mock.web.MockHttpServletResponse;

/**
 * @author Larissa Ribeiro
 */
@FeatureFlags(featureFlags = @FeatureFlag("LPD-58677"))
@RunWith(Arquillian.class)
@Sync
public class ViewAllRelatedAssetsSectionDisplayContextTest
	extends BaseDisplayContextTestCase {

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

		_childObjectDefinition =
			ObjectDefinitionTestUtil.publishObjectDefinition();
		_parentObjectDefinition =
			ObjectDefinitionTestUtil.publishObjectDefinition();

		_objectRelationship = ObjectRelationshipTestUtil.addObjectRelationship(
			_objectRelationshipLocalService, _parentObjectDefinition,
			_childObjectDefinition);

		_objectEntry = _objectEntryLocalService.addObjectEntry(
			0, TestPropsValues.getUserId(),
			_parentObjectDefinition.getObjectDefinitionId(), 0, null,
			Collections.emptyMap(), ServiceContextTestUtil.getServiceContext());
	}

	@Test
	public void testGetAdditionalAPIURLParameters() throws Exception {
		_testGetAdditionalAPIURLParametersWithoutRelatedCMPTasks();
		_testGetAdditionalAPIURLParametersWithRelatedCMPTasks();
	}

	@Test
	public void testGetAdditionalProps() throws Exception {
		Map<String, Object> additionalProps = ReflectionTestUtil.invoke(
			_getViewAllRelatedAssetsSectionDisplayContext(
				mockHttpServletRequest),
			"getAdditionalProps", new Class<?>[0]);

		Map<String, Object> breadcrumbProps =
			(Map<String, Object>)additionalProps.get("breadcrumbProps");

		Assert.assertNotNull(breadcrumbProps.get("breadcrumbItems"));
	}

	private Object _getViewAllRelatedAssetsSectionDisplayContext(
			HttpServletRequest httpServletRequest)
		throws Exception {

		httpServletRequest.setAttribute(
			InfoDisplayWebKeys.INFO_ITEM, _objectEntry);
		httpServletRequest.setAttribute(
			"OBJECT_RELATIONSHIP", _objectRelationship);

		_fragmentRenderer.render(
			null, httpServletRequest, new MockHttpServletResponse());

		return httpServletRequest.getAttribute(
			"com.liferay.site.cms.site.initializer.internal.display.context." +
				"ViewAllRelatedAssetsSectionDisplayContext");
	}

	private void _testGetAdditionalAPIURLParametersWithoutRelatedCMPTasks()
		throws Exception {

		String additionalAPIURLParameters = ReflectionTestUtil.invoke(
			_getViewAllRelatedAssetsSectionDisplayContext(
				mockHttpServletRequest),
			"getAdditionalAPIURLParameters", new Class<?>[0]);

		Assert.assertTrue(
			additionalAPIURLParameters,
			additionalAPIURLParameters.contains(
				StringBundler.concat(
					"(cmsSection eq 'contents' or cmsSection eq 'files') and ",
					"cmpProjectObjectEntryIds in (",
					_objectEntry.getObjectEntryId(),
					") and rootDescendantNode eq false")));
	}

	private void _testGetAdditionalAPIURLParametersWithRelatedCMPTasks()
		throws Exception {

		ObjectEntry relatedObjectEntry =
			_objectEntryLocalService.addObjectEntry(
				0, TestPropsValues.getUserId(),
				_childObjectDefinition.getObjectDefinitionId(), 0, null,
				Collections.emptyMap(),
				ServiceContextTestUtil.getServiceContext());

		ObjectRelationshipTestUtil.relateObjectEntries(
			_objectEntry.getObjectEntryId(),
			relatedObjectEntry.getObjectEntryId(), _objectRelationship,
			TestPropsValues.getUserId());

		String additionalAPIURLParameters = ReflectionTestUtil.invoke(
			_getViewAllRelatedAssetsSectionDisplayContext(
				mockHttpServletRequest),
			"getAdditionalAPIURLParameters", new Class<?>[0]);

		Assert.assertTrue(
			additionalAPIURLParameters,
			additionalAPIURLParameters.contains(
				StringBundler.concat(
					"(cmsSection eq 'contents' or cmsSection eq 'files') and ",
					"(cmpProjectObjectEntryIds in (",
					_objectEntry.getObjectEntryId(),
					") or cmpTaskObjectEntryIds in (",
					relatedObjectEntry.getObjectEntryId(),
					")) and rootDescendantNode eq false")));
	}

	@DeleteAfterTestRun
	private ObjectDefinition _childObjectDefinition;

	@Inject(
		filter = "component.name=com.liferay.site.cms.site.initializer.internal.fragment.renderer.ViewAllRelatedAssetsJSPSectionFragmentRenderer"
	)
	private FragmentRenderer _fragmentRenderer;

	private ObjectEntry _objectEntry;

	@Inject
	private ObjectEntryLocalService _objectEntryLocalService;

	private ObjectRelationship _objectRelationship;

	@Inject
	private ObjectRelationshipLocalService _objectRelationshipLocalService;

	@DeleteAfterTestRun
	private ObjectDefinition _parentObjectDefinition;

}