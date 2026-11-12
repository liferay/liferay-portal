/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.internal.model.listener;

import com.liferay.account.model.AccountEntryOrganizationRel;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectField;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.object.service.ObjectFieldLocalService;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMap;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMapFactory;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery;
import com.liferay.portal.kernel.dao.orm.RestrictionsFactoryUtil;
import com.liferay.portal.kernel.exception.ModelListenerException;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.BaseModelListener;
import com.liferay.portal.kernel.model.ModelListener;
import com.liferay.portal.kernel.transaction.TransactionCallbackUtil;
import com.liferay.portal.search.indexer.IndexerDocumentBuilder;

import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Feliphe Marinho
 * @author Gabriel Albuquerque
 */
@Component(service = ModelListener.class)
public class AccountEntryOrganizationRelModelListener
	extends BaseModelListener<AccountEntryOrganizationRel> {

	@Override
	public void onAfterCreate(
			AccountEntryOrganizationRel accountEntryOrganizationRel)
		throws ModelListenerException {

		TransactionCallbackUtil.registerCommitCallback(
			() -> {
				_reindex(accountEntryOrganizationRel);

				return null;
			});
	}

	@Override
	public void onAfterRemove(
			AccountEntryOrganizationRel accountEntryOrganizationRel)
		throws ModelListenerException {

		TransactionCallbackUtil.registerCommitCallback(
			() -> {
				_reindex(accountEntryOrganizationRel);

				return null;
			});
	}

	@Activate
	protected void activate(BundleContext bundleContext) {
		_serviceTrackerMap = ServiceTrackerMapFactory.openSingleValueMap(
			bundleContext, IndexerDocumentBuilder.class, "indexer.class.name");
	}

	@Deactivate
	protected void deactivate() {
		_serviceTrackerMap.close();
	}

	private void _reindex(
			AccountEntryOrganizationRel accountEntryOrganizationRel)
		throws PortalException {

		for (ObjectDefinition objectDefinition :
				_objectDefinitionLocalService.getObjectDefinitions(true)) {

			if ((objectDefinition.getCompanyId() !=
					accountEntryOrganizationRel.getCompanyId()) ||
				!objectDefinition.isEnableIndexSearch() ||
				objectDefinition.isUnmodifiableSystemObject()) {

				continue;
			}

			ObjectField objectField = _objectFieldLocalService.fetchObjectField(
				objectDefinition.getAccountEntryRestrictedObjectFieldId());

			if (objectField == null) {
				continue;
			}

			IndexableActionableDynamicQuery indexableActionableDynamicQuery =
				_objectEntryLocalService.getIndexableActionableDynamicQuery();

			indexableActionableDynamicQuery.setAddCriteriaMethod(
				dynamicQuery -> {
					dynamicQuery.add(
						RestrictionsFactoryUtil.eq(
							"objectDefinitionId",
							objectDefinition.getObjectDefinitionId()));
					dynamicQuery.add(
						RestrictionsFactoryUtil.eqProperty(
							"objectEntryId", "headObjectEntryId"));
					dynamicQuery.add(
						RestrictionsFactoryUtil.sqlRestriction(
							StringBundler.concat(
								"this_.objectEntryId in (select ",
								objectDefinition.getPKObjectFieldDBColumnName(),
								" from ", objectField.getDBTableName(),
								" where ", objectField.getDBColumnName(), " = ",
								accountEntryOrganizationRel.getAccountEntryId(),
								")")));
				});
			indexableActionableDynamicQuery.setCompanyId(
				accountEntryOrganizationRel.getCompanyId());

			IndexerDocumentBuilder indexerDocumentBuilder =
				_serviceTrackerMap.getService(objectDefinition.getClassName());

			indexableActionableDynamicQuery.setPerformActionMethod(
				indexerDocumentBuilder::getDocument);

			indexableActionableDynamicQuery.performActions();
		}
	}

	@Reference
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	@Reference
	private ObjectEntryLocalService _objectEntryLocalService;

	@Reference
	private ObjectFieldLocalService _objectFieldLocalService;

	private ServiceTrackerMap<String, IndexerDocumentBuilder>
		_serviceTrackerMap;

}