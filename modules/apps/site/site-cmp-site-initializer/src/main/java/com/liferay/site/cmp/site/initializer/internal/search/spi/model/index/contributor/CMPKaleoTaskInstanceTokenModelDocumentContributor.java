/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.cmp.site.initializer.internal.search.spi.model.index.contributor;

import com.liferay.object.constants.ObjectDefinitionConstants;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.rest.filter.factory.FilterFactory;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.petra.sql.dsl.expression.Predicate;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.search.Document;
import com.liferay.portal.kernel.service.ClassNameLocalService;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.search.spi.model.index.contributor.ModelDocumentContributor;
import com.liferay.portal.workflow.kaleo.model.KaleoTaskAssignmentInstance;
import com.liferay.portal.workflow.kaleo.model.KaleoTaskInstanceToken;
import com.liferay.site.cmp.site.initializer.internal.util.CMPLinkedObjectEntryUtil;

import java.util.HashSet;
import java.util.Set;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Jhosseph Gonzalez
 */
@Component(
	property = "indexer.class.name=com.liferay.portal.workflow.kaleo.model.KaleoTaskInstanceToken",
	service = ModelDocumentContributor.class
)
public class CMPKaleoTaskInstanceTokenModelDocumentContributor
	implements ModelDocumentContributor<KaleoTaskInstanceToken> {

	@Override
	public void contribute(
		Document document, KaleoTaskInstanceToken kaleoTaskInstanceToken) {

		try {
			_contribute(document, kaleoTaskInstanceToken);
		}
		catch (PortalException portalException) {
			_log.error(portalException);
		}
	}

	private void _contribute(
			Document document, KaleoTaskInstanceToken kaleoTaskInstanceToken)
		throws PortalException {

		ObjectEntry objectEntry = _objectEntryLocalService.fetchObjectEntry(
			kaleoTaskInstanceToken.getClassPK());

		if (objectEntry == null) {
			return;
		}

		long[] cmpTaskObjectEntryIds =
			CMPLinkedObjectEntryUtil.getLinkedObjectEntryIds(
				_filterFactory, _groupLocalService, "L_CMP_TASK_LINK",
				_objectDefinitionLocalService, objectEntry,
				_objectEntryLocalService,
				"r_cmpTaskToCMPTaskLinks_c_cmpTaskId");

		if (cmpTaskObjectEntryIds.length == 0) {
			return;
		}

		document.addKeyword("cmpTaskObjectEntryIds", cmpTaskObjectEntryIds);

		Set<String> cmpAssignTos = new HashSet<>();

		for (KaleoTaskAssignmentInstance kaleoTaskAssignmentInstance :
				kaleoTaskInstanceToken.getKaleoTaskAssignmentInstances()) {

			cmpAssignTos.add(
				StringBundler.concat(
					_classNameLocalService.getClassNameId(
						kaleoTaskAssignmentInstance.getAssigneeClassName()),
					StringPool.UNDERLINE,
					kaleoTaskAssignmentInstance.getAssigneeClassPK()));
		}

		if (!cmpAssignTos.isEmpty()) {
			document.addKeyword(
				"cmpAssignTo", cmpAssignTos.toArray(new String[0]));
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		CMPKaleoTaskInstanceTokenModelDocumentContributor.class);

	@Reference
	private ClassNameLocalService _classNameLocalService;

	@Reference(
		target = "(filter.factory.key=" + ObjectDefinitionConstants.STORAGE_TYPE_DEFAULT + ")"
	)
	private FilterFactory<Predicate> _filterFactory;

	@Reference
	private GroupLocalService _groupLocalService;

	@Reference
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	@Reference
	private ObjectEntryLocalService _objectEntryLocalService;

}