/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.fragment.web.internal.frontend.taglib.clay.servlet.taglib;

import com.liferay.fragment.model.FragmentComposition;
import com.liferay.fragment.model.FragmentEntry;
import com.liferay.fragment.web.internal.constants.FragmentTypeConstants;
import com.liferay.frontend.taglib.clay.servlet.taglib.VerticalCard;
import com.liferay.portal.kernel.dao.search.RowChecker;

import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;

import java.util.Objects;

/**
 * @author Jürgen Kappler
 */
public class FragmentEntryVerticalCardFactory {

	public static FragmentEntryVerticalCardFactory getInstance() {
		return _fragmentEntryVerticalCardFactory;
	}

	public VerticalCard getVerticalCard(
		FragmentComposition fragmentComposition, RenderRequest renderRequest,
		RenderResponse renderResponse, RowChecker rowChecker, String type) {

		if (!Objects.equals(type, FragmentTypeConstants.BASIC_FRAGMENT_TYPE)) {
			return null;
		}

		return new BasicFragmentCompositionVerticalCard(
			fragmentComposition, renderRequest, renderResponse, rowChecker);
	}

	public VerticalCard getVerticalCard(
		FragmentEntry fragmentEntry, RenderRequest renderRequest,
		RenderResponse renderResponse, RowChecker rowChecker, String type) {

		if (Objects.equals(type, FragmentTypeConstants.BASIC_FRAGMENT_TYPE)) {
			return new BasicFragmentEntryVerticalCard(
				fragmentEntry, renderRequest, renderResponse, rowChecker);
		}

		if (!Objects.equals(
				type, FragmentTypeConstants.INHERITED_FRAGMENT_TYPE)) {

			return null;
		}

		return new InheritedFragmentEntryVerticalCard(
			fragmentEntry, renderRequest, renderResponse, rowChecker);
	}

	private FragmentEntryVerticalCardFactory() {
	}

	private static final FragmentEntryVerticalCardFactory
		_fragmentEntryVerticalCardFactory =
			new FragmentEntryVerticalCardFactory();

}