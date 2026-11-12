/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.wiki.engine.creole.internal.parser.ast;

/**
 * @author Miguel Pastor
 */
public abstract class URLNode extends ASTNode {

	public URLNode() {
	}

	public URLNode(int token) {
		super(token);
	}

	public URLNode(int token, String link) {
		super(token);

		_link = link;
	}

	public URLNode(String link) {
		_link = link;
	}

	public String getLink() {
		return _link;
	}

	public void setLink(String link) {
		_link = link;
	}

	private String _link;

}