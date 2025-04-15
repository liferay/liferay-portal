/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.oauth2.provider.web.internal.tree.tag;

import com.liferay.oauth2.provider.web.internal.tree.Tree;

import jakarta.servlet.jsp.JspContext;
import jakarta.servlet.jsp.JspException;
import jakarta.servlet.jsp.tagext.JspFragment;
import jakarta.servlet.jsp.tagext.SimpleTagSupport;

import java.io.IOException;

import java.util.Collection;
import java.util.LinkedList;

/**
 * @author Marta Medio
 */
public class TreeTag extends SimpleTagSupport {

	@Override
	public void doTag() throws IOException, JspException {
		JspContext jspContext = getJspContext();

		JspFragment jspFragment = getJspBody();

		jspFragment.invoke(null);

		Object parentNodes = jspContext.getAttribute("parentNodes");

		try {
			jspContext.setAttribute("parentNodes", new LinkedList<>());

			for (Tree<?> tree : _trees) {
				renderTree(tree);
			}
		}
		finally {
			if (parentNodes == null) {
				jspContext.removeAttribute("parentNodes");
			}
			else {
				jspContext.setAttribute("parentNodes", parentNodes);
			}
		}
	}

	public JspFragment getLeafJspFragment() {
		return leafJspFragment;
	}

	public JspFragment getNodeJspFragment() {
		return nodeJspFragment;
	}

	public Collection<Tree<?>> getTrees() {
		return _trees;
	}

	public void setLeafJspFragment(JspFragment leafJspFragment) {
		this.leafJspFragment = leafJspFragment;
	}

	public void setNodeJspFragment(JspFragment nodeJspFragment) {
		this.nodeJspFragment = nodeJspFragment;
	}

	public void setTrees(Collection<Tree<?>> trees) {
		_trees = trees;
	}

	protected void renderTree(Tree<?> tree) throws IOException, JspException {
		JspContext jspContext = getJspContext();

		Object treeObject = jspContext.getAttribute("tree");

		try {
			jspContext.setAttribute("tree", tree);

			if (tree instanceof Tree.Leaf) {
				JspFragment leafJspFragment = getLeafJspFragment();

				leafJspFragment.invoke(null);
			}
			else {
				JspFragment nodeJspFragment = getNodeJspFragment();

				nodeJspFragment.invoke(null);
			}
		}
		finally {
			if (treeObject == null) {
				jspContext.removeAttribute("tree");
			}
			else {
				jspContext.setAttribute("tree", treeObject);
			}
		}
	}

	protected JspFragment leafJspFragment;
	protected JspFragment nodeJspFragment;

	private Collection<Tree<?>> _trees;

}