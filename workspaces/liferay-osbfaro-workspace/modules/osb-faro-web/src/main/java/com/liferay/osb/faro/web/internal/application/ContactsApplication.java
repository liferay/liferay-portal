/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.web.internal.application;

import com.liferay.osb.faro.web.internal.constants.FaroConstants;
import com.liferay.osb.faro.web.internal.controller.contacts.AccountFaroController;
import com.liferay.osb.faro.web.internal.controller.contacts.AccountLifecycleFaroController;
import com.liferay.osb.faro.web.internal.controller.contacts.ActivityFaroController;
import com.liferay.osb.faro.web.internal.controller.contacts.ActivityGroupFaroController;
import com.liferay.osb.faro.web.internal.controller.contacts.AssetSummaryCategoryFaroController;
import com.liferay.osb.faro.web.internal.controller.contacts.AssetSummaryFaroController;
import com.liferay.osb.faro.web.internal.controller.contacts.AssetSummaryMimeTypeFaroController;
import com.liferay.osb.faro.web.internal.controller.contacts.AssetSummaryTagFaroController;
import com.liferay.osb.faro.web.internal.controller.contacts.AssetSummaryTypeFaroController;
import com.liferay.osb.faro.web.internal.controller.contacts.AssetSummaryVocabularyFaroController;
import com.liferay.osb.faro.web.internal.controller.contacts.CampaignFaroController;
import com.liferay.osb.faro.web.internal.controller.contacts.ContactsCardFaroController;
import com.liferay.osb.faro.web.internal.controller.contacts.ContactsCardTemplateFaroController;
import com.liferay.osb.faro.web.internal.controller.contacts.ContactsLayoutFaroController;
import com.liferay.osb.faro.web.internal.controller.contacts.ContactsLayoutTemplateFaroController;
import com.liferay.osb.faro.web.internal.controller.contacts.DataSourceFaroController;
import com.liferay.osb.faro.web.internal.controller.contacts.DataSourceMetricsFaroController;
import com.liferay.osb.faro.web.internal.controller.contacts.FieldFaroController;
import com.liferay.osb.faro.web.internal.controller.contacts.FieldMappingFaroController;
import com.liferay.osb.faro.web.internal.controller.contacts.IndividualFaroController;
import com.liferay.osb.faro.web.internal.controller.contacts.IndividualSegmentFaroController;
import com.liferay.osb.faro.web.internal.controller.contacts.InterestFaroController;
import com.liferay.osb.faro.web.internal.controller.contacts.PagesVisitedFaroController;
import com.liferay.osb.faro.web.internal.controller.contacts.SessionFaroController;

import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.core.Application;

import java.util.HashSet;
import java.util.Set;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Matthew Kong
 */
@ApplicationPath("/" + FaroConstants.APPLICATION_CONTACTS)
@Component(property = "jaxrs.application=true", service = Application.class)
public class ContactsApplication extends BaseApplication {

	@Override
	public Set<Object> getControllers() {
		Set<Object> controllers = new HashSet<>();

		controllers.add(_accountFaroController);
		controllers.add(_accountLifecycleFaroController);
		controllers.add(_activityFaroController);
		controllers.add(_activityGroupFaroController);
		controllers.add(_assetSummaryCategoryFaroController);
		controllers.add(_assetSummaryFaroController);
		controllers.add(_assetSummaryMimeTypeFaroController);
		controllers.add(_assetSummaryTagFaroController);
		controllers.add(_assetSummaryTypeFaroController);
		controllers.add(_assetSummaryVocabularyFaroController);
		controllers.add(_campaignFaroController);
		controllers.add(_contactsCardFaroController);
		controllers.add(_contactsCardTemplateFaroController);
		controllers.add(_contactsLayoutFaroController);
		controllers.add(_contactsLayoutTemplateFaroController);
		controllers.add(_dataSourceFaroController);
		controllers.add(_dataSourceMetricsFaroController);
		controllers.add(_fieldFaroController);
		controllers.add(_fieldMappingFaroController);
		controllers.add(_individualFaroController);
		controllers.add(_individualSegmentFaroController);
		controllers.add(_interestFaroController);
		controllers.add(_pagesVisitedFaroController);
		controllers.add(_sessionFaroController);

		return controllers;
	}

	@Reference
	private AccountFaroController _accountFaroController;

	@Reference
	private AccountLifecycleFaroController _accountLifecycleFaroController;

	@Reference
	private ActivityFaroController _activityFaroController;

	@Reference
	private ActivityGroupFaroController _activityGroupFaroController;

	@Reference
	private AssetSummaryCategoryFaroController
		_assetSummaryCategoryFaroController;

	@Reference
	private AssetSummaryFaroController _assetSummaryFaroController;

	@Reference
	private AssetSummaryMimeTypeFaroController
		_assetSummaryMimeTypeFaroController;

	@Reference
	private AssetSummaryTagFaroController _assetSummaryTagFaroController;

	@Reference
	private AssetSummaryTypeFaroController _assetSummaryTypeFaroController;

	@Reference
	private AssetSummaryVocabularyFaroController
		_assetSummaryVocabularyFaroController;

	@Reference
	private CampaignFaroController _campaignFaroController;

	@Reference
	private ContactsCardFaroController _contactsCardFaroController;

	@Reference
	private ContactsCardTemplateFaroController
		_contactsCardTemplateFaroController;

	@Reference
	private ContactsLayoutFaroController _contactsLayoutFaroController;

	@Reference
	private ContactsLayoutTemplateFaroController
		_contactsLayoutTemplateFaroController;

	@Reference
	private DataSourceFaroController _dataSourceFaroController;

	@Reference
	private DataSourceMetricsFaroController _dataSourceMetricsFaroController;

	@Reference
	private FieldFaroController _fieldFaroController;

	@Reference
	private FieldMappingFaroController _fieldMappingFaroController;

	@Reference
	private IndividualFaroController _individualFaroController;

	@Reference
	private IndividualSegmentFaroController _individualSegmentFaroController;

	@Reference
	private InterestFaroController _interestFaroController;

	@Reference
	private PagesVisitedFaroController _pagesVisitedFaroController;

	@Reference
	private SessionFaroController _sessionFaroController;

}