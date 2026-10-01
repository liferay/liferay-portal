/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.internal.notification.term.contributor.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.notification.context.NotificationContext;
import com.liferay.notification.context.NotificationContextBuilder;
import com.liferay.notification.term.evaluator.NotificationTermEvaluator;
import com.liferay.notification.term.evaluator.NotificationTermEvaluatorTracker;
import com.liferay.object.definition.notification.term.util.ObjectDefinitionNotificationTermUtil;
import com.liferay.object.field.builder.LocationObjectFieldBuilder;
import com.liferay.object.field.util.ObjectFieldUtil;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.test.util.ObjectDefinitionTestUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Carolina Barbosa
 */
@FeatureFlag("LPD-11388")
@RunWith(Arquillian.class)
public class ObjectDefinitionNotificationTermEvaluatorTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() throws Exception {
		_objectDefinition = ObjectDefinitionTestUtil.publishObjectDefinition();

		List<NotificationTermEvaluator> notificationTermEvaluators =
			_notificationTermEvaluatorTracker.getNotificationTermEvaluators(
				_objectDefinition.getClassName());

		_notificationTermEvaluator = notificationTermEvaluators.get(0);
	}

	@Test
	public void testEvaluateWithLocationObjectField() throws Exception {
		ObjectFieldUtil.addCustomObjectField(
			new LocationObjectFieldBuilder(
			).labelMap(
				RandomTestUtil.randomLocaleStringMap()
			).name(
				"location"
			).objectDefinitionId(
				_objectDefinition.getObjectDefinitionId()
			).userId(
				TestPropsValues.getUserId()
			).build());

		String address = RandomTestUtil.randomString();

		NotificationContext notificationContext =
			new NotificationContextBuilder(
			).className(
				_objectDefinition.getClassName()
			).termValues(
				HashMapBuilder.<String, Object>put(
					"entryDTO",
					HashMapBuilder.<String, Object>put(
						"properties",
						HashMapBuilder.<String, Object>put(
							"location",
							HashMapBuilder.<String, Object>put(
								"address", address
							).build()
						).build()
					).build()
				).build()
			).build();

		Assert.assertEquals(
			address,
			_notificationTermEvaluator.evaluate(
				NotificationTermEvaluator.Context.CONTENT, notificationContext,
				ObjectDefinitionNotificationTermUtil.getObjectFieldTermName(
					_objectDefinition.getShortName(), "location")));
	}

	private NotificationTermEvaluator _notificationTermEvaluator;

	@Inject
	private NotificationTermEvaluatorTracker _notificationTermEvaluatorTracker;

	private ObjectDefinition _objectDefinition;

}