/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.fragment.renderer;

import com.liferay.fragment.constants.FragmentEntryLinkConstants;
import com.liferay.fragment.model.FragmentEntryLink;
import com.liferay.info.form.InfoForm;
import com.liferay.info.item.InfoItemReference;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.uuid.PortalUUIDUtil;

import java.io.Serializable;

import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

/**
 * @author Jorge Ferrer
 */
public class DefaultFragmentRendererContext implements FragmentRendererContext {

	public DefaultFragmentRendererContext(FragmentEntryLink fragmentEntryLink) {
		_fragmentEntryLink = fragmentEntryLink;

		_fragmentEntryElementId = "fragment-" + PortalUUIDUtil.generate();
	}

	@Override
	public Serializable getAttribute(String name) {
		return _attributes.get(name);
	}

	@Override
	public Map<String, Serializable> getAttributes() {
		return _attributes;
	}

	@Override
	public InfoItemReference getContextInfoItemReference() {
		return _infoItemReference;
	}

	@Override
	public String getFragmentElementId() {
		return _fragmentEntryElementId;
	}

	@Override
	public FragmentEntryLink getFragmentEntryLink() {
		return _fragmentEntryLink;
	}

	@Override
	public InfoForm getInfoForm() {
		return _infoForm;
	}

	@Override
	public Locale getLocale() {
		return _locale;
	}

	@Override
	public String getMode() {
		return _mode;
	}

	@Override
	public long getPreviewClassNameId() {
		return _previewClassNameId;
	}

	@Override
	public long getPreviewClassPK() {
		return _previewClassPK;
	}

	@Override
	public int getPreviewType() {
		return _previewType;
	}

	@Override
	public String getPreviewVersion() {
		return _previewVersion;
	}

	@Override
	public long[] getSegmentsEntryIds() {
		return _segmentsSegmentsEntryIds;
	}

	@Override
	public boolean isDisablePortletRender() {
		return _disablePortletRender;
	}

	@Override
	public boolean isEditMode() {
		return Objects.equals(getMode(), FragmentEntryLinkConstants.EDIT);
	}

	@Override
	public boolean isIndexMode() {
		return Objects.equals(getMode(), FragmentEntryLinkConstants.INDEX);
	}

	@Override
	public boolean isPreviewMode() {
		return Objects.equals(getMode(), FragmentEntryLinkConstants.PREVIEW);
	}

	@Override
	public boolean isUseCachedContent() {
		return _useCachedContent;
	}

	@Override
	public boolean isViewMode() {
		return Objects.equals(getMode(), FragmentEntryLinkConstants.VIEW);
	}

	public void setAttribute(String name, Serializable value) {
		_attributes.put(name, value);
	}

	public void setAttributes(Map<String, Serializable> attributes) {
		_attributes = attributes;
	}

	public void setContextInfoItemReference(
		InfoItemReference infoItemReference) {

		_infoItemReference = infoItemReference;
	}

	public void setDisablePortletRender(boolean disablePortletRender) {
		_disablePortletRender = disablePortletRender;
	}

	public void setInfoForm(InfoForm infoForm) {
		_infoForm = infoForm;
	}

	public void setLocale(Locale locale) {
		_locale = locale;
	}

	public void setMode(String mode) {
		_mode = mode;
	}

	public void setPreviewClassNameId(long previewClassNameId) {
		_previewClassNameId = previewClassNameId;
	}

	public void setPreviewClassPK(long previewClassPK) {
		_previewClassPK = previewClassPK;
	}

	public void setPreviewType(int previewType) {
		_previewType = previewType;
	}

	public void setPreviewVersion(String previewVersion) {
		_previewVersion = previewVersion;
	}

	public void setSegmentsEntryIds(long[] segmentsSegmentsEntryIds) {
		_segmentsSegmentsEntryIds = segmentsSegmentsEntryIds;
	}

	public void setUseCachedContent(boolean useCachedContent) {
		_useCachedContent = useCachedContent;
	}

	private Map<String, Serializable> _attributes = new LinkedHashMap<>();
	private boolean _disablePortletRender;
	private final String _fragmentEntryElementId;
	private final FragmentEntryLink _fragmentEntryLink;
	private InfoForm _infoForm;
	private InfoItemReference _infoItemReference;
	private Locale _locale = LocaleUtil.getMostRelevantLocale();
	private String _mode = FragmentEntryLinkConstants.VIEW;
	private long _previewClassNameId;
	private long _previewClassPK;
	private int _previewType;
	private String _previewVersion;
	private long[] _segmentsSegmentsEntryIds = new long[0];
	private boolean _useCachedContent = true;

}