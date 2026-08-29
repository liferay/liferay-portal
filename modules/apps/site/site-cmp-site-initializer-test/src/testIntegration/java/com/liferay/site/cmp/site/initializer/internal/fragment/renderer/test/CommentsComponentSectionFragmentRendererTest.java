/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.cmp.site.initializer.internal.fragment.renderer.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.fragment.renderer.FragmentRenderer;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.model.GroupConstants;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.FeatureFlags;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import java.util.Map;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Carolina Barbosa
 */
@FeatureFlags(featureFlags = @FeatureFlag("LPD-58677"))
@RunWith(Arquillian.class)
public class CommentsComponentSectionFragmentRendererTest
	extends BaseComponentSectionFragmentRendererTestCase {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Test
	public void testGetProps() throws Exception {
		Map<String, Object> props = getProps();

		Assert.assertEquals(
			_getURL("/add_content_item_comment"), props.get("addCommentURL"));
		Assert.assertEquals(
			_getURL("/delete_content_item_comment"),
			props.get("deleteCommentURL"));
		Assert.assertEquals(
			_getURL("/edit_content_item_comment"), props.get("editCommentURL"));
		Assert.assertEquals(
			_getURL("/get_asset_comments"), props.get("getCommentsURL"));
	}

	@Override
	protected FragmentRenderer getFragmentRenderer() {
		return _fragmentRenderer;
	}

	private String _getURL(String actionId) {
		return StringBundler.concat(
			themeDisplay.getPortalURL(), themeDisplay.getPathMain(),
			GroupConstants.CMS_FRIENDLY_URL, actionId, "?p_l_id=",
			themeDisplay.getPlid(), "&classNameId=",
			_portal.getClassNameId(cmpProjectObjectDefinition.getClassName()),
			"&classPK=", cmpProjectObjectEntry.getObjectEntryId());
	}

	@Inject(
		filter = "component.name=com.liferay.site.cmp.site.initializer.internal.fragment.renderer.CommentsComponentSectionFragmentRenderer"
	)
	private FragmentRenderer _fragmentRenderer;

	@Inject
	private Portal _portal;

}