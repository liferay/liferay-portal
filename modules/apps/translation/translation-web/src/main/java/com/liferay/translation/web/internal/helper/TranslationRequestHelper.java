/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.translation.web.internal.helper;

import com.liferay.info.item.ClassPKInfoItemIdentifier;
import com.liferay.info.item.GroupKeyInfoItemIdentifier;
import com.liferay.info.item.InfoItemServiceRegistry;
import com.liferay.info.item.translator.InfoItemIdentifierTranslator;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.segments.constants.SegmentsExperienceConstants;
import com.liferay.segments.model.SegmentsExperience;
import com.liferay.segments.model.SegmentsExperienceModel;
import com.liferay.segments.service.SegmentsExperienceLocalService;

import jakarta.portlet.PortletRequest;

import jakarta.servlet.http.HttpServletRequest;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author Adolfo Pérez
 */
public class TranslationRequestHelper {

	public TranslationRequestHelper(
		HttpServletRequest httpServletRequest,
		InfoItemServiceRegistry infoItemServiceRegistry,
		SegmentsExperienceLocalService segmentsExperienceLocalService) {

		_httpServletRequest = httpServletRequest;
		_infoItemServiceRegistry = infoItemServiceRegistry;
		_segmentsExperienceLocalService = segmentsExperienceLocalService;
	}

	public TranslationRequestHelper(
		InfoItemServiceRegistry infoItemServiceRegistry,
		PortletRequest portletRequest,
		SegmentsExperienceLocalService segmentsExperienceLocalService) {

		this(
			PortalUtil.getHttpServletRequest(portletRequest),
			infoItemServiceRegistry, segmentsExperienceLocalService);
	}

	public String getClassName(long segmentsExperienceId) {
		SegmentsExperience segmentsExperience =
			_segmentsExperienceLocalService.fetchSegmentsExperience(
				segmentsExperienceId);

		if ((segmentsExperience != null) &&
			!Objects.equals(
				segmentsExperience.getSegmentsExperienceKey(),
				SegmentsExperienceConstants.KEY_DEFAULT)) {

			return SegmentsExperience.class.getName();
		}

		return getModelClassName();
	}

	public String getClassName(long[] segmentsExperienceIds) {
		if (ArrayUtil.isEmpty(segmentsExperienceIds)) {
			return getModelClassName();
		}

		if (_isExportAllSegmentsExperiences(segmentsExperienceIds)) {
			return SegmentsExperience.class.getName();
		}

		if (segmentsExperienceIds.length == 1) {
			SegmentsExperience segmentsExperience =
				_segmentsExperienceLocalService.fetchSegmentsExperience(
					segmentsExperienceIds[0]);

			if ((segmentsExperience == null) ||
				Objects.equals(
					segmentsExperience.getSegmentsExperienceKey(),
					SegmentsExperienceConstants.KEY_DEFAULT)) {

				return getModelClassName();
			}
		}

		return SegmentsExperience.class.getName();
	}

	public long getClassNameId() {
		if (_classNameId != null) {
			return _classNameId;
		}

		_classNameId = ParamUtil.getLong(_httpServletRequest, "classNameId");

		return _classNameId;
	}

	public long getClassPK(long segmentsExperienceId) throws PortalException {
		SegmentsExperience segmentsExperience =
			_segmentsExperienceLocalService.fetchSegmentsExperience(
				segmentsExperienceId);

		if ((segmentsExperience != null) &&
			!Objects.equals(
				segmentsExperience.getSegmentsExperienceKey(),
				SegmentsExperienceConstants.KEY_DEFAULT)) {

			return segmentsExperience.getSegmentsExperienceId();
		}

		return getModelClassPK();
	}

	public long[] getClassPKs(long[] segmentsExperienceIds)
		throws PortalException {

		if (ArrayUtil.isEmpty(segmentsExperienceIds)) {
			return getModelClassPKs();
		}

		if (_isExportAllSegmentsExperiences(segmentsExperienceIds)) {
			return _getSegmentsExperienceIds(getModelClassPKs());
		}

		if (segmentsExperienceIds.length == 1) {
			SegmentsExperience segmentsExperience =
				_segmentsExperienceLocalService.fetchSegmentsExperience(
					segmentsExperienceIds[0]);

			if ((segmentsExperience == null) ||
				Objects.equals(
					segmentsExperience.getSegmentsExperienceKey(),
					SegmentsExperienceConstants.KEY_DEFAULT)) {

				return getModelClassPKs();
			}
		}

		return segmentsExperienceIds;
	}

	public long getGroupId() {
		if (_groupId != null) {
			return _groupId;
		}

		_groupId = ParamUtil.getLong(_httpServletRequest, "groupId");

		return _groupId;
	}

	public String getModelClassName() {
		if (_modelClassName != null) {
			return _modelClassName;
		}

		_modelClassName = PortalUtil.getClassName(getClassNameId());

		return _modelClassName;
	}

	public long getModelClassPK() throws PortalException {
		long[] modelClassPKs = getModelClassPKs();

		if (modelClassPKs.length > 0) {
			return modelClassPKs[0];
		}

		return 0;
	}

	public long[] getModelClassPKs() throws PortalException {
		if (_modelClassPKs != null) {
			return _modelClassPKs;
		}

		_modelClassPKs = ParamUtil.getLongValues(
			_httpServletRequest, "classPK");

		if (_modelClassPKs.length != 0) {
			return _modelClassPKs;
		}

		InfoItemIdentifierTranslator infoItemIdentifierTranslator =
			_infoItemServiceRegistry.getFirstInfoItemService(
				InfoItemIdentifierTranslator.class, getModelClassName());

		String[] keys = ParamUtil.getStringValues(_httpServletRequest, "key");

		long[] modelClassPKs = new long[keys.length];

		for (int i = 0; i < keys.length; i++) {
			ClassPKInfoItemIdentifier classPKInfoItemIdentifier =
				(ClassPKInfoItemIdentifier)
					infoItemIdentifierTranslator.translateInfoItemIdentifier(
						new GroupKeyInfoItemIdentifier(getGroupId(), keys[i]),
						ClassPKInfoItemIdentifier.class);

			modelClassPKs[i] = classPKInfoItemIdentifier.getClassPK();
		}

		_modelClassPKs = modelClassPKs;

		return _modelClassPKs;
	}

	private long[] _getSegmentsExperienceIds(long[] classPKs) {
		List<SegmentsExperience> segmentsExperiences = new ArrayList<>();

		for (long classPK : classPKs) {
			segmentsExperiences.addAll(
				_segmentsExperienceLocalService.getSegmentsExperiences(
					getGroupId(), classPK));
		}

		return ListUtil.toLongArray(
			segmentsExperiences,
			SegmentsExperienceModel::getSegmentsExperienceId);
	}

	private boolean _isExportAllSegmentsExperiences(
		long[] segmentsExperienceIds) {

		if ((segmentsExperienceIds.length == 1) &&
			(segmentsExperienceIds[0] == -1)) {

			return true;
		}

		return false;
	}

	private Long _classNameId;
	private Long _groupId;
	private final HttpServletRequest _httpServletRequest;
	private final InfoItemServiceRegistry _infoItemServiceRegistry;
	private String _modelClassName;
	private long[] _modelClassPKs;
	private final SegmentsExperienceLocalService
		_segmentsExperienceLocalService;

}