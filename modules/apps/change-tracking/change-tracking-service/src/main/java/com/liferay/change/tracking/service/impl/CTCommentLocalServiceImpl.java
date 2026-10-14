/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.change.tracking.service.impl;

import com.liferay.change.tracking.model.CTCollection;
import com.liferay.change.tracking.model.CTComment;
import com.liferay.change.tracking.service.base.CTCommentLocalServiceBaseImpl;
import com.liferay.change.tracking.service.persistence.CTCollectionPersistence;
import com.liferay.portal.aop.AopService;
import com.liferay.portal.kernel.exception.PortalException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Preston Crary
 */
@Component(
	property = "model.class.name=com.liferay.change.tracking.model.CTComment",
	service = AopService.class
)
public class CTCommentLocalServiceImpl extends CTCommentLocalServiceBaseImpl {

	@Override
	public CTComment addCTComment(
			long userId, long ctCollectionId, long ctEntryId, String value)
		throws PortalException {

		CTCollection ctCollection = _ctCollectionPersistence.findByPrimaryKey(
			ctCollectionId);

		CTComment ctComment = ctCommentPersistence.create(
			counterLocalService.increment(CTComment.class.getName()));

		ctComment.setCompanyId(ctCollection.getCompanyId());
		ctComment.setUserId(userId);
		ctComment.setCtCollectionId(ctCollectionId);
		ctComment.setCtEntryId(ctEntryId);
		ctComment.setValue(value);

		return ctCommentPersistence.update(ctComment);
	}

	@Override
	public CTComment deleteCTComment(long ctCommentId) {
		CTComment ctComment = ctCommentPersistence.fetchByPrimaryKey(
			ctCommentId);

		if (ctComment != null) {
			ctComment = ctCommentPersistence.remove(ctComment);
		}

		return ctComment;
	}

	@Override
	public Map<Long, List<CTComment>> getCTCollectionCTComments(
		long ctCollectionId) {

		Map<Long, List<CTComment>> collectionCommentsMap = new HashMap<>();

		for (CTComment ctComment :
				ctCommentPersistence.findByCtCollectionId(ctCollectionId)) {

			List<CTComment> ctComments = collectionCommentsMap.computeIfAbsent(
				ctComment.getCtEntryId(), key -> new ArrayList<>());

			ctComments.add(ctComment);
		}

		return collectionCommentsMap;
	}

	@Override
	public CTComment updateCTComment(long ctCommentId, String value)
		throws PortalException {

		CTComment ctComment = ctCommentPersistence.findByPrimaryKey(
			ctCommentId);

		ctComment.setValue(value);

		return ctCommentPersistence.update(ctComment);
	}

	@Reference
	private CTCollectionPersistence _ctCollectionPersistence;

}