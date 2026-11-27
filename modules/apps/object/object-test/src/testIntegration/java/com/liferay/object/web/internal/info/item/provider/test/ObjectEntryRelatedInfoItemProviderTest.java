/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.web.internal.info.item.provider.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.info.item.InfoItemServiceRegistry;
import com.liferay.info.item.RelatedInfoItem;
import com.liferay.info.item.provider.RelatedInfoItemProvider;
import com.liferay.object.constants.ObjectRelationshipConstants;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectRelationship;
import com.liferay.object.rest.test.util.ObjectRelationshipTestUtil;
import com.liferay.object.test.util.ObjectDefinitionTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import java.util.List;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Carolina Barbosa
 */
@RunWith(Arquillian.class)
public class ObjectEntryRelatedInfoItemProviderTest {

	@ClassRule
	@Rule
	public static final LiferayIntegrationTestRule liferayIntegrationTestRule =
		new LiferayIntegrationTestRule();

	@FeatureFlag("LPD-108620")
	@Test
	public void testGetRelatedInfoItems() throws Exception {
		_testGetRelatedInfoItems(ObjectRelationshipConstants.TYPE_ONE_TO_MANY);
		_testGetRelatedInfoItems(ObjectRelationshipConstants.TYPE_ONE_TO_ONE);
	}

	private void _testGetRelatedInfoItems(String type) throws Exception {
		ObjectDefinition childObjectDefinition =
			ObjectDefinitionTestUtil.publishObjectDefinition();
		ObjectDefinition parentObjectDefinition =
			ObjectDefinitionTestUtil.publishObjectDefinition();

		ObjectRelationship objectRelationship =
			ObjectRelationshipTestUtil.addObjectRelationship(
				parentObjectDefinition, childObjectDefinition,
				TestPropsValues.getUserId(), type);

		RelatedInfoItemProvider<?> relatedInfoItemProvider =
			_infoItemServiceRegistry.getFirstInfoItemService(
				RelatedInfoItemProvider.class,
				childObjectDefinition.getClassName());

		List<RelatedInfoItem> relatedInfoItems =
			relatedInfoItemProvider.getRelatedInfoItems();

		RelatedInfoItem relatedInfoItem = relatedInfoItems.get(0);

		Assert.assertEquals(
			parentObjectDefinition.getClassName(),
			relatedInfoItem.getClassName());
		Assert.assertEquals(
			objectRelationship.getName(), relatedInfoItem.getName());

		Assert.assertEquals(
			relatedInfoItems.toString(), 1, relatedInfoItems.size());
	}

	@Inject
	private InfoItemServiceRegistry _infoItemServiceRegistry;

}