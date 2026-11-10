/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.blogs.demo.data.creator.internal;

import com.liferay.blogs.demo.data.creator.BlogsEntryDemoDataCreator;
import com.liferay.blogs.model.BlogsEntry;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.util.StringUtil;

import java.io.IOException;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;

/**
 * @author Sergio González
 */
@Component(
	property = "source=creative-commons",
	service = BlogsEntryDemoDataCreator.class
)
public class CreativeCommonsBlogsEntryDemoDataCreatorImpl
	extends BaseBlogsEntryDemoDataCreator {

	@Override
	public BlogsEntry create(long userId, long groupId)
		throws IOException, PortalException {

		int index = _getNextIndex();

		String title = _getRandomTitle(index);
		String subtitle = _getRandomSubtitle(index);
		String content = _getRandomContent(index);

		return createBlogsEntry(userId, groupId, title, subtitle, content);
	}

	@Activate
	protected void activate() {
		Collections.addAll(
			_availableIndexes, new Integer[] {1, 2, 3, 4, 5, 6, 7, 8, 9, 10});

		Collections.shuffle(_availableIndexes);
	}

	private int _getNextIndex() {
		int index = _atomicInteger.getAndIncrement();

		if (index == (_availableIndexes.size() - 1)) {
			_atomicInteger.set(0);
		}

		return _availableIndexes.get(index);
	}

	private String _getRandomContent(int index) throws IOException {
		Class<?> clazz = getClass();

		String titlePath = StringBundler.concat(
			"com/liferay/blogs/demo/data/creator/internal/dependencies",
			"/creative/commons/content_", index, ".txt");

		return StringUtil.read(clazz.getClassLoader(), titlePath, false);
	}

	private String _getRandomSubtitle(int index) throws IOException {
		Class<?> clazz = getClass();

		String titlePath = StringBundler.concat(
			"com/liferay/blogs/demo/data/creator/internal/dependencies",
			"/creative/commons/subtitle_", index, ".txt");

		return StringUtil.read(clazz.getClassLoader(), titlePath, false);
	}

	private String _getRandomTitle(int index) throws IOException {
		Class<?> clazz = getClass();

		String titlePath = StringBundler.concat(
			"com/liferay/blogs/demo/data/creator/internal/dependencies",
			"/creative/commons/title_", index, ".txt");

		return StringUtil.read(clazz.getClassLoader(), titlePath, false);
	}

	private final AtomicInteger _atomicInteger = new AtomicInteger(0);
	private final List<Integer> _availableIndexes =
		new CopyOnWriteArrayList<>();

}