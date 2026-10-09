/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.tree;

import com.liferay.object.service.ObjectRelationshipLocalService;
import com.liferay.petra.function.UnsafeFunction;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.util.ListUtil;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

/**
 * @author Pedro Leite
 */
public class BaseTreeFactory {

	public BaseTreeFactory(
		ObjectRelationshipLocalService objectRelationshipLocalService) {

		this.objectRelationshipLocalService = objectRelationshipLocalService;
	}

	protected Tree apply(
			long primaryKey,
			UnsafeFunction<Node, List<Node>, PortalException> unsafeFunction)
		throws PortalException {

		Node rootNode = new Node(null, null, primaryKey);

		Queue<Node> queue = new LinkedList<>();

		queue.add(rootNode);

		while (!queue.isEmpty()) {
			Node node = queue.poll();

			List<Node> nodes = unsafeFunction.apply(node);

			if (ListUtil.isEmpty(nodes)) {
				continue;
			}

			node.setChildNodes(nodes);

			queue.addAll(nodes);
		}

		return new Tree(rootNode);
	}

	protected final ObjectRelationshipLocalService
		objectRelationshipLocalService;

}