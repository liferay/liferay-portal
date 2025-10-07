/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.message.boards.model.impl;

import com.liferay.message.boards.model.MBCategory;
import com.liferay.message.boards.model.MBMessage;
import com.liferay.message.boards.model.MBMessageDisplay;
import com.liferay.message.boards.model.MBThread;
import com.liferay.message.boards.model.MBTreeWalker;
import com.liferay.portal.kernel.util.PropsValues;

import java.util.List;

/**
 * @author Brian Wing Shun Chan
 * @author Shuyang Zhou
 */
public class MBMessageDisplayImpl implements MBMessageDisplay {

	public MBMessageDisplayImpl(
		MBMessage message, MBMessage parentMessage, List<MBMessage> messages,
		MBCategory category, MBThread thread, int discussionMessagesCount) {

		_message = message;
		_parentMessage = parentMessage;
		_category = category;
		_thread = thread;
		_discussionMessagesCount = discussionMessagesCount;

		_treeWalker = new MBTreeWalkerImpl(messages);
	}

	@Override
	public MBCategory getCategory() {
		return _category;
	}

	@Override
	public MBMessage getMessage() {
		return _message;
	}

	@Override
	public MBMessage getParentMessage() {
		return _parentMessage;
	}

	@Override
	public MBThread getThread() {
		return _thread;
	}

	@Override
	public MBTreeWalker getTreeWalker() {
		return _treeWalker;
	}

	@Override
	public boolean isDiscussionMaxComments() {
		if (_message.isDiscussion() &&
			(PropsValues.DISCUSSION_MAX_COMMENTS > 0) &&
			(PropsValues.DISCUSSION_MAX_COMMENTS <= _discussionMessagesCount)) {

			return true;
		}

		return false;
	}

	private final MBCategory _category;
	private final int _discussionMessagesCount;
	private final MBMessage _message;
	private final MBMessage _parentMessage;
	private final MBThread _thread;
	private final MBTreeWalker _treeWalker;

}