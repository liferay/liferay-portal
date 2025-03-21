/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.osgi.web.servlet.jsp.compiler.internal;

import com.liferay.portal.kernel.util.GetterUtil;

import jakarta.servlet.ServletConfig;
import jakarta.servlet.jsp.JspException;
import jakarta.servlet.jsp.tagext.Tag;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.jasper.Constants;
import org.apache.jasper.runtime.TagHandlerPool;

/**
 * @author Shuyang Zhou
 * @author Preston Crary
 * @see    com.liferay.support.tomcat.jasper.runtime.TagHandlerPool
 */
public class JspTagHandlerPool extends TagHandlerPool {

	@Override
	public Tag get(Class<? extends Tag> tagClass) throws JspException {
		Tag tag = _tags.poll();

		if (tag == null) {
			try {
				tag = tagClass.newInstance();
			}
			catch (Exception exception) {
				throw new JspException(exception);
			}
		}
		else {
			_counter.getAndDecrement();
		}

		return tag;
	}

	@Override
	public void release() {
		Tag tag = null;

		while ((tag = _tags.poll()) != null) {
			tag.release();
		}
	}

	@Override
	public void reuse(Tag tag) {
		if (_counter.get() < _maxSize) {
			_counter.getAndIncrement();

			_tags.offer(tag);
		}
		else {
			tag.release();
		}
	}

	@Override
	protected void init(ServletConfig config) {
		_maxSize = GetterUtil.getInteger(
			getOption(config, OPTION_MAXSIZE, null), Constants.MAX_POOL_SIZE);
	}

	private final AtomicInteger _counter = new AtomicInteger();
	private int _maxSize;
	private final Queue<Tag> _tags = new ConcurrentLinkedQueue<>();

}