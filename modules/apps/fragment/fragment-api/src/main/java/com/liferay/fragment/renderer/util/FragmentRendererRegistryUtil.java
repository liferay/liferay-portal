/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.fragment.renderer.util;

import com.liferay.fragment.renderer.FragmentRenderer;
import com.liferay.fragment.renderer.FragmentRendererRegistry;
import com.liferay.portal.kernel.module.service.Snapshot;

import java.util.List;

/**
 * @author Lourdes Fernández Besada
 */
public class FragmentRendererRegistryUtil {

	public static FragmentRenderer getFragmentRenderer(String key) {
		FragmentRendererRegistry fragmentRendererRegistry =
			_fragmentRendererRegistrySnapshot.get();

		return fragmentRendererRegistry.getFragmentRenderer(key);
	}

	public static FragmentRendererRegistry getFragmentRendererRegistry() {
		return _fragmentRendererRegistrySnapshot.get();
	}

	public static List<FragmentRenderer> getFragmentRenderers() {
		FragmentRendererRegistry fragmentRendererRegistry =
			_fragmentRendererRegistrySnapshot.get();

		return fragmentRendererRegistry.getFragmentRenderers();
	}

	public static List<FragmentRenderer> getFragmentRenderers(int type) {
		FragmentRendererRegistry fragmentRendererRegistry =
			_fragmentRendererRegistrySnapshot.get();

		return fragmentRendererRegistry.getFragmentRenderers(type);
	}

	private static final Snapshot<FragmentRendererRegistry>
		_fragmentRendererRegistrySnapshot = new Snapshot<>(
			FragmentRendererRegistryUtil.class, FragmentRendererRegistry.class);

}