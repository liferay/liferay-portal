/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.change.tracking.internal.search.spi.model.query.contributor;

import com.liferay.change.tracking.constants.CTConstants;
import com.liferay.change.tracking.internal.CTServiceRegistry;
import com.liferay.change.tracking.model.CTEntry;
import com.liferay.change.tracking.service.CTEntryLocalService;
import com.liferay.change.tracking.spi.search.CTSearchExcludeModelClassPKContributor;
import com.liferay.osgi.service.tracker.collections.list.ServiceTrackerList;
import com.liferay.osgi.service.tracker.collections.list.ServiceTrackerListFactory;
import com.liferay.portal.kernel.change.tracking.CTCollectionThreadLocal;
import com.liferay.portal.kernel.search.BooleanClauseOccur;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.SearchContext;
import com.liferay.portal.kernel.search.filter.BooleanFilter;
import com.liferay.portal.kernel.search.filter.Filter;
import com.liferay.portal.kernel.search.filter.MissingFilter;
import com.liferay.portal.kernel.search.filter.TermsFilter;
import com.liferay.portal.kernel.service.ClassNameLocalService;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.search.model.uid.UIDFactory;
import com.liferay.portal.search.spi.model.query.contributor.ModelPreFilterContributor;
import com.liferay.portal.search.spi.model.registrar.ModelSearchSettings;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Preston Crary
 * @author André de Oliveira
 */
@Component(
	property = {"indexer.class.name=ALL", "indexer.clauses.mandatory=true"},
	service = ModelPreFilterContributor.class
)
public class CTModelPreFilterContributor implements ModelPreFilterContributor {

	@Override
	public void contribute(
		BooleanFilter booleanFilter, ModelSearchSettings modelSearchSettings,
		SearchContext searchContext) {

		String className = modelSearchSettings.getClassName();

		if (!_ctServiceRegistry.hasCTService(className)) {
			return;
		}

		String ctCollectionIdString = GetterUtil.getString(
			searchContext.getAttribute(
				"com.liferay.change.tracking.filter.ctCollectionId"));

		if (Objects.equals(ctCollectionIdString, "ALL")) {
			return;
		}

		BooleanFilter ctBooleanFilter = new BooleanFilter();

		long ctCollectionId = GetterUtil.getLong(
			ctCollectionIdString, CTCollectionThreadLocal.getCTCollectionId());

		if (ctCollectionId == CTConstants.CT_COLLECTION_ID_PRODUCTION) {
			if (!GetterUtil.getBoolean(
					searchContext.getAttribute("relatedClassName"))) {

				ctBooleanFilter.add(
					_CT_COLLECTION_ID_MISSING_FILTER, BooleanClauseOccur.MUST);
			}
		}
		else {
			List<Long> excludeProductionModelClassPKs = new ArrayList<>();

			for (CTEntry ctEntry :
					_ctEntryLocalService.getCTEntries(
						ctCollectionId,
						_classNameLocalService.getClassNameId(className))) {

				int changeType = ctEntry.getChangeType();

				if ((changeType == CTConstants.CT_CHANGE_TYPE_DELETION) ||
					(changeType == CTConstants.CT_CHANGE_TYPE_MODIFICATION)) {

					excludeProductionModelClassPKs.add(
						ctEntry.getModelClassPK());
				}

				for (CTSearchExcludeModelClassPKContributor
						ctSEMCPKContributor : _serviceTrackerList) {

					ctSEMCPKContributor.contribute(
						className, ctEntry.getModelClassPK(),
						excludeProductionModelClassPKs);
				}
			}

			ctBooleanFilter.add(
				_CT_COLLECTION_ID_MISSING_FILTER, BooleanClauseOccur.SHOULD);

			TermsFilter ctCollectionIdTermsFilter = new TermsFilter(
				_CT_COLLECTION_ID);

			ctCollectionIdTermsFilter.addValue(String.valueOf(ctCollectionId));
			ctCollectionIdTermsFilter.addValue(
				String.valueOf(CTConstants.CT_COLLECTION_ID_PRODUCTION));

			ctBooleanFilter.add(
				ctCollectionIdTermsFilter, BooleanClauseOccur.SHOULD);

			if (!excludeProductionModelClassPKs.isEmpty()) {
				TermsFilter uidTermsFilter = new TermsFilter(Field.UID);

				for (Long classPK : excludeProductionModelClassPKs) {
					uidTermsFilter.addValue(
						_uidFactory.getUID(
							className, String.valueOf(classPK),
							CTConstants.CT_COLLECTION_ID_PRODUCTION));
				}

				ctBooleanFilter.add(
					uidTermsFilter, BooleanClauseOccur.MUST_NOT);
			}
		}

		if (ctBooleanFilter.hasClauses()) {
			booleanFilter.add(ctBooleanFilter, BooleanClauseOccur.MUST);
		}
	}

	@Activate
	protected void activate(BundleContext bundleContext) {
		_serviceTrackerList = ServiceTrackerListFactory.open(
			bundleContext, CTSearchExcludeModelClassPKContributor.class);
	}

	@Deactivate
	protected void deactivate() {
		_serviceTrackerList.close();
	}

	private static final String _CT_COLLECTION_ID = "ctCollectionId";

	private static final Filter _CT_COLLECTION_ID_MISSING_FILTER =
		new MissingFilter(_CT_COLLECTION_ID);

	@Reference
	private ClassNameLocalService _classNameLocalService;

	@Reference
	private CTEntryLocalService _ctEntryLocalService;

	@Reference
	private CTServiceRegistry _ctServiceRegistry;

	private ServiceTrackerList<CTSearchExcludeModelClassPKContributor>
		_serviceTrackerList;

	@Reference
	private UIDFactory _uidFactory;

}