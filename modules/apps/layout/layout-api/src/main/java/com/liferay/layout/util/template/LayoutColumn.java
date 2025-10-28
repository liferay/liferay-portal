/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.util.template;

import com.liferay.fragment.constants.FragmentConstants;
import com.liferay.fragment.model.FragmentEntryLink;
import com.liferay.fragment.service.FragmentEntryLinkLocalServiceUtil;
import com.liferay.petra.function.UnsafeConsumer;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.LayoutTypePortlet;
import com.liferay.portal.kernel.model.LayoutTypePortletConstants;
import com.liferay.portal.kernel.portlet.PortletIdCodec;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.util.PortletKeys;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.segments.service.SegmentsExperienceLocalServiceUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Eudaldo Alonso
 */
public class LayoutColumn {

	public static LayoutColumn of(
		Layout layout, UnsafeConsumer<LayoutColumn, Exception> unsafeConsumer) {

		LayoutColumn layoutColumn = new LayoutColumn(layout);

		try {
			unsafeConsumer.accept(layoutColumn);
		}
		catch (Exception exception) {
			throw new RuntimeException(exception);
		}

		return layoutColumn;
	}

	public void addAllPortlets() throws PortalException {
		LayoutTypePortlet layoutTypePortlet =
			(LayoutTypePortlet)_layout.getLayoutType();

		for (String portletId : layoutTypePortlet.getPortletIds()) {
			if (!portletId.startsWith(PortletKeys.NESTED_PORTLETS)) {
				_addPortlet(portletId);
			}
		}
	}

	public void addPortlets(String columnId) throws PortalException {
		List<String> portletIds = LayoutTypeSettingsInspectorUtil.getPortletIds(
			_layout.getTypeSettingsProperties(), columnId);

		for (String portletId : portletIds) {
			if (portletId.startsWith(PortletKeys.NESTED_PORTLETS)) {
				for (String portletNestedColumnId :
						_getNestedColumnIds(portletId)) {

					addPortlets(portletNestedColumnId);
				}
			}
			else {
				_addPortlet(portletId);
			}
		}
	}

	public List<Long> getFragmentEntryLinkIds() {
		return _fragmentEntryLinkIds;
	}

	public int getSize() {
		return _size;
	}

	public void setSize(int size) {
		_size = size;
	}

	private LayoutColumn(Layout layout) {
		_layout = layout;
	}

	private void _addPortlet(String portletId) throws PortalException {
		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		FragmentEntryLink fragmentEntryLink =
			FragmentEntryLinkLocalServiceUtil.addFragmentEntryLink(
				null, serviceContext.getUserId(),
				serviceContext.getScopeGroupId(), null, null, null,
				SegmentsExperienceLocalServiceUtil.
					fetchDefaultSegmentsExperienceId(_layout.getPlid()),
				_layout.getPlid(), StringPool.BLANK, StringPool.BLANK,
				StringPool.BLANK, StringPool.BLANK,
				JSONUtil.put(
					"instanceId", PortletIdCodec.decodeInstanceId(portletId)
				).put(
					"portletId", PortletIdCodec.decodePortletName(portletId)
				).toString(),
				StringPool.BLANK, 0, null, FragmentConstants.TYPE_PORTLET,
				serviceContext);

		_fragmentEntryLinkIds.add(fragmentEntryLink.getFragmentEntryLinkId());
	}

	private List<String> _getNestedColumnIds(String portletId) {
		List<String> portletNestedColumnIds = new ArrayList<>();

		String property = _layout.getTypeSettingsProperty(
			LayoutTypePortletConstants.NESTED_COLUMN_IDS, StringPool.BLANK);

		String[] nestedColumnIds = property.split(StringPool.COMMA);

		for (String nestedColumnId : nestedColumnIds) {
			if (Validator.isNotNull(nestedColumnId) &&
				nestedColumnId.startsWith(StringPool.UNDERLINE + portletId)) {

				portletNestedColumnIds.add(nestedColumnId);
			}
		}

		return portletNestedColumnIds;
	}

	private final List<Long> _fragmentEntryLinkIds = new ArrayList<>();
	private final Layout _layout;
	private int _size = 12;

}