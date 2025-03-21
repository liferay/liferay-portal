/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.fragment.processor;

import com.liferay.asset.kernel.model.AssetRendererFactory;
import com.liferay.fragment.constants.FragmentEntryLinkConstants;
import com.liferay.info.form.InfoForm;
import com.liferay.info.item.InfoItemIdentifier;
import com.liferay.info.item.InfoItemReference;
import com.liferay.portal.kernel.uuid.PortalUUIDUtil;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.Serializable;

import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

/**
 * @author Pavel Savinov
 */
public class DefaultFragmentEntryProcessorContext
	implements FragmentEntryProcessorContext {

	public DefaultFragmentEntryProcessorContext(
		HttpServletRequest httpServletRequest,
		HttpServletResponse httpServletResponse, String mode, Locale locale) {

		_httpServletRequest = httpServletRequest;
		_httpServletResponse = httpServletResponse;
		_mode = mode;
		_locale = locale;

		_fragmentElementId = "fragment-" + PortalUUIDUtil.generate();
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
		return _fragmentElementId;
	}

	@Override
	public HttpServletRequest getHttpServletRequest() {
		return _httpServletRequest;
	}

	@Override
	public HttpServletResponse getHttpServletResponse() {
		return _httpServletResponse;
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
		return _segmentsEntryIds;
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

	public void setFragmentElementId(String fragmentElementId) {
		_fragmentElementId = fragmentElementId;
	}

	public void setInfoForm(InfoForm infoForm) {
		_infoForm = infoForm;
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

	public void setSegmentsEntryIds(long[] segmentsEntryIds) {
		_segmentsEntryIds = segmentsEntryIds;
	}

	private Map<String, Serializable> _attributes = new LinkedHashMap<>();
	private String _fragmentElementId;
	private final HttpServletRequest _httpServletRequest;
	private final HttpServletResponse _httpServletResponse;
	private InfoForm _infoForm;
	private InfoItemReference _infoItemReference;
	private final Locale _locale;
	private final String _mode;
	private long _previewClassNameId;
	private long _previewClassPK;
	private int _previewType = AssetRendererFactory.TYPE_LATEST_APPROVED;
	private String _previewVersion = InfoItemIdentifier.VERSION_LATEST_APPROVED;
	private long[] _segmentsEntryIds = new long[0];

}