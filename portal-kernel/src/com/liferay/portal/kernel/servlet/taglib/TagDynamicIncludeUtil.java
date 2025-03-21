/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.servlet.taglib;

import com.liferay.osgi.service.tracker.collections.map.ServiceReferenceMapper;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMap;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMapFactory;
import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.module.util.SystemBundleUtil;
import com.liferay.portal.kernel.util.ListUtil;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.Iterator;
import java.util.List;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

/**
 * @author Carlos Sierra Andrés
 * @author Raymond Augé
 */
public class TagDynamicIncludeUtil {

	public static List<TagDynamicInclude> getTagDynamicIncludes(
		String tagClassName, String tagDynamicId, String tagPoint) {

		return _tagDynamicIncludes.getService(
			_getKey(tagClassName, tagDynamicId, tagPoint));
	}

	public static boolean hasTagDynamicInclude(
		String tagClassName, String tagDynamicId, String tagPoint) {

		return ListUtil.isNotEmpty(
			getTagDynamicIncludes(tagClassName, tagDynamicId, tagPoint));
	}

	public static void include(
		HttpServletRequest httpServletRequest,
		HttpServletResponse httpServletResponse, String tagClassName,
		String tagDynamicId, String tagPoint, boolean ascendingPriority) {

		List<TagDynamicInclude> tagDynamicIncludes = getTagDynamicIncludes(
			tagClassName, tagDynamicId, tagPoint);

		if (ListUtil.isEmpty(tagDynamicIncludes)) {
			return;
		}

		Iterator<TagDynamicInclude> iterator = null;

		if (ascendingPriority) {
			iterator = tagDynamicIncludes.iterator();
		}
		else {
			iterator = ListUtil.reverseIterator(tagDynamicIncludes);
		}

		while (iterator.hasNext()) {
			TagDynamicInclude tagDynamicInclude = iterator.next();

			try {
				tagDynamicInclude.include(
					httpServletRequest, httpServletResponse, tagClassName,
					tagDynamicId, tagPoint);
			}
			catch (Exception exception) {
				_log.error(exception);
			}
		}
	}

	private static String _getKey(
		String tagClassName, String tagDynamicId, String tagPoint) {

		return StringBundler.concat(
			tagClassName, CharPool.POUND, tagPoint, CharPool.POUND,
			tagDynamicId);
	}

	private TagDynamicIncludeUtil() {
	}

	private static final Log _log = LogFactoryUtil.getLog(
		TagDynamicIncludeUtil.class);

	private static final BundleContext _bundleContext =
		SystemBundleUtil.getBundleContext();

	private static final ServiceTrackerMap<String, List<TagDynamicInclude>>
		_tagDynamicIncludes = ServiceTrackerMapFactory.openMultiValueMap(
			_bundleContext, TagDynamicInclude.class, null,
			new ServiceReferenceMapper<String, TagDynamicInclude>() {

				@Override
				public void map(
					ServiceReference<TagDynamicInclude> serviceReference,
					final Emitter<String> emitter) {

					TagDynamicInclude tagDynamicInclude =
						_bundleContext.getService(serviceReference);

					try {
						tagDynamicInclude.register(
							new TagDynamicInclude.TagDynamicIncludeRegistry() {

								@Override
								public void register(
									String tagClassName, String tagDynamicId,
									String tagPoint) {

									emitter.emit(
										_getKey(
											tagClassName, tagDynamicId,
											tagPoint));
								}

							});
					}
					finally {
						_bundleContext.ungetService(serviceReference);
					}
				}

			});

}