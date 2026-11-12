/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.relationship;

import com.liferay.portal.kernel.model.ClassedModel;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

/**
 * @author Shuyang Zhou
 */
public class RelationshipManagerUtilTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Test
	public void testGetOutboundRelatedModels() {
		AtomicReference<Degree> degreeAtomicReference = new AtomicReference<>();

		RelationshipManagerUtil.setRelationshipManager(
			new RelationshipManager() {

				@Override
				public <T extends ClassedModel>
					Collection<? extends ClassedModel> getInboundRelatedModels(
						Class<T> modelClass, long primKey) {

					throw new UnsupportedOperationException();
				}

				@Override
				public <T extends ClassedModel>
					Collection<? extends ClassedModel> getInboundRelatedModels(
						Class<T> modelClass, long primKey, Degree degree) {

					throw new UnsupportedOperationException();
				}

				@Override
				public <T extends ClassedModel>
					Collection<? extends ClassedModel> getOutboundRelatedModels(
						Class<T> modelClass, long primKey) {

					return Collections.emptyList();
				}

				@Override
				public <T extends ClassedModel>
					Collection<? extends ClassedModel> getOutboundRelatedModels(
						Class<T> modelClass, long primKey, Degree degree) {

					degreeAtomicReference.set(degree);

					return Collections.emptyList();
				}

				@Override
				public <T extends ClassedModel>
					Collection<? extends ClassedModel> getRelatedModels(
						Class<T> modelClass, long primKey) {

					throw new UnsupportedOperationException();
				}

				@Override
				public <T extends ClassedModel>
					Collection<? extends ClassedModel> getRelatedModels(
						Class<T> modelClass, long primKey, Degree degree) {

					throw new UnsupportedOperationException();
				}

			});

		Degree degree = Degree.two();

		RelationshipManagerUtil.getOutboundRelatedModels(
			ClassedModel.class, RandomTestUtil.randomLong(), degree);

		Assert.assertSame(degree, degreeAtomicReference.get());
	}

}