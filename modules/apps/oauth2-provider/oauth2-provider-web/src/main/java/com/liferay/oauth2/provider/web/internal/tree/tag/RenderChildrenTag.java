/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.oauth2.provider.web.internal.tree.tag;

import com.liferay.oauth2.provider.web.internal.tree.Tree;

import jakarta.servlet.jsp.JspContext;
import jakarta.servlet.jsp.JspException;
import jakarta.servlet.jsp.tagext.JspFragment;
import jakarta.servlet.jsp.tagext.JspTag;

import java.io.IOException;

import java.util.Deque;

/**
 * @author Marta Medio
 */
public class RenderChildrenTag extends TreeTag {

	@Override
	public void doTag() throws IOException, JspException {
		JspContext jspContext = getJspContext();

		Object treeObject = jspContext.getAttribute("tree");

		if (!(treeObject instanceof Tree.Node)) {
			throw new IllegalStateException(
				"Render children must be used inside the node fragment of a " +
					"tree tag");
		}

		Deque<Tree.Node<?>> parentNodes =
			(Deque<Tree.Node<?>>)jspContext.getAttribute("parentNodes");

		Tree.Node<?> node = (Tree.Node<?>)treeObject;

		parentNodes.push(node);

		try {
			for (Tree<?> tree : node.getTrees()) {
				renderTree(tree);
			}
		}
		finally {
			parentNodes.pop();
		}
	}

	@Override
	public JspFragment getLeafJspFragment() {
		if (leafJspFragment != null) {
			return leafJspFragment;
		}

		JspTag jspTag = findAncestorWithClass(this, TreeTag.class);

		if (jspTag instanceof TreeTag) {
			TreeTag treeTag = (TreeTag)jspTag;

			return treeTag.getLeafJspFragment();
		}

		throw new IllegalStateException("Unable to get leaf JSP fragment");
	}

	@Override
	public JspFragment getNodeJspFragment() {
		if (nodeJspFragment != null) {
			return nodeJspFragment;
		}

		JspTag jspTag = findAncestorWithClass(this, TreeTag.class);

		if (jspTag instanceof TreeTag) {
			TreeTag treeTag = (TreeTag)jspTag;

			return treeTag.getNodeJspFragment();
		}

		throw new IllegalStateException("Unable to get node JSP fragment");
	}

	@Override
	public void setLeafJspFragment(JspFragment leafJspFragment) {
		this.leafJspFragment = leafJspFragment;
	}

	@Override
	public void setNodeJspFragment(JspFragment nodeJspFragment) {
		this.nodeJspFragment = nodeJspFragment;
	}

}