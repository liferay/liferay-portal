/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.relationship;

import com.liferay.portal.kernel.model.ClassedModel;

import java.util.Collection;

/**
 * @author Máté Thurzó
 *
 * @deprecated As of Cavanaugh (7.4.x), with no direct replacement
 */
@Deprecated
public class RelationshipManagerUtil {

	public static <T extends ClassedModel> Collection<? extends ClassedModel>
		getInboundRelatedModels(Class<T> modelClass, long primKey) {

		return _relationshipManager.getInboundRelatedModels(
			modelClass, primKey);
	}

	public static <T extends ClassedModel> Collection<? extends ClassedModel>
		getInboundRelatedModels(
			Class<T> modelClass, long primKey, Degree degree) {

		return _relationshipManager.getInboundRelatedModels(
			modelClass, primKey, degree);
	}

	public static <T extends ClassedModel> Collection<? extends ClassedModel>
		getOutboundRelatedModels(Class<T> modelClass, long primKey) {

		return _relationshipManager.getOutboundRelatedModels(
			modelClass, primKey);
	}

	public static <T extends ClassedModel> Collection<? extends ClassedModel>
		getOutboundRelatedModels(
			Class<T> modelClass, long primKey, Degree degree) {

		return _relationshipManager.getOutboundRelatedModels(
			modelClass, primKey, degree);
	}

	public static <T extends ClassedModel> Collection<? extends ClassedModel>
		getRelatedModels(Class<T> modelClass, long primKey) {

		return _relationshipManager.getRelatedModels(modelClass, primKey);
	}

	public static <T extends ClassedModel> Collection<? extends ClassedModel>
		getRelatedModels(Class<T> modelClass, long primKey, Degree degree) {

		return _relationshipManager.getRelatedModels(
			modelClass, primKey, degree);
	}

	public static void setRelationshipManager(
		RelationshipManager relationshipManager) {

		if (_relationshipManager != null) {
			relationshipManager = _relationshipManager;

			return;
		}

		_relationshipManager = relationshipManager;
	}

	private static RelationshipManager _relationshipManager;

}