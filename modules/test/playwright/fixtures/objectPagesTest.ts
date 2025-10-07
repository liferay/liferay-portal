/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {test} from '@playwright/test';

import {CommerceCatalogSystemSettingsPage} from '../pages/commerce/commerceCatalogSystemSettingsPage';
import {SystemSettingsPage} from '../pages/configuration-admin-web/SystemSettingsPage';
import {EditObjectDefinitionPage} from '../pages/object-web/EditObjectDefinitionPage';
import {ModalAddObjectDefinitionPage} from '../pages/object-web/ModalAddObjectDefinitionPage';
import {ModalEditObjectFolderPage} from '../pages/object-web/ModalEditObjectFolderPage';
import {ViewObjectDefinitionsPage} from '../pages/object-web/ViewObjectDefinitionsPage';
import {ModelBuilderDiagramPage} from '../pages/object-web/model-builder/ModelBuilderDiagramPage';
import {ModelBuilderLeftSidebarPage} from '../pages/object-web/model-builder/ModelBuilderLeftSidebarPage';
import {ModelBuilderObjectDefinitionNodePage} from '../pages/object-web/model-builder/ModelBuilderObjectDefinitionNodePage';
import {ModelBuilderRightSidebarPage} from '../pages/object-web/model-builder/ModelBuilderRightSidebarPage';
import {EditObjectDetailsPage} from '../pages/object-web/object-details/EditObjectDetailsPage';
import {ViewObjectEntriesPage} from '../pages/object-web/object-entries/ViewObjectEntriesPage';
import {ObjectFieldsPage} from '../pages/object-web/object-fields/ObjectFieldsPage';
import {ObjectLayoutsPage} from '../pages/object-web/object-layout/ObjectLayoutsPage';
import {AddNewObjectRelationshipModalPage} from '../pages/object-web/object-relationship/AddObjectRelationshipModalPage';
import {ObjectRelationshipFormPage} from '../pages/object-web/object-relationship/ObjectRelationshipFormPage';
import {ObjectRelationshipsPage} from '../pages/object-web/object-relationship/ObjectRelationshipsPage';
import {EditObjectValidationPage} from '../pages/object-web/object-validation/EditObjectValidationPage';
import {ModalAddObjectValidationPage} from '../pages/object-web/object-validation/ModalAddObjectValidationPage';
import {ObjectValidationsPage} from '../pages/object-web/object-validation/ObjectValidationsPage';
import {EditObjectViewPage} from '../pages/object-web/object-view/EditObjectViewPage';
import {ObjectDetailsPage} from '../pages/object-web/object-view/ObjectDetailsPage';
import {ObjectViewPage} from '../pages/object-web/object-view/ObjectViewsPage';

const objectPagesTest = test.extend<{
	addNewObjectRelationshipModalPage: AddNewObjectRelationshipModalPage;
	commerceCatalogSystemSettingsPage: CommerceCatalogSystemSettingsPage;
	editObjectDefinitionPage: EditObjectDefinitionPage;
	editObjectDetailsPage: EditObjectDetailsPage;
	editObjectValidationPage: EditObjectValidationPage;
	editObjectViewPage: EditObjectViewPage;
	modalAddObjectDefinitionPage: ModalAddObjectDefinitionPage;
	modalAddObjectValidationPage: ModalAddObjectValidationPage;
	modalEditObjectFolderPage: ModalEditObjectFolderPage;
	modelBuilderDiagramPage: ModelBuilderDiagramPage;
	modelBuilderLeftSidebarPage: ModelBuilderLeftSidebarPage;
	modelBuilderObjectDefinitionNodePage: ModelBuilderObjectDefinitionNodePage;
	modelBuilderRightSidebarPage: ModelBuilderRightSidebarPage;
	objectDetailsPage: ObjectDetailsPage;
	objectFieldsPage: ObjectFieldsPage;
	objectLayoutsPage: ObjectLayoutsPage;
	objectRelationshipFormPage: ObjectRelationshipFormPage;
	objectRelationshipsPage: ObjectRelationshipsPage;
	objectValidationsPage: ObjectValidationsPage;
	objectViewPage: ObjectViewPage;
	systemSettingsPage: SystemSettingsPage;
	viewObjectDefinitionsPage: ViewObjectDefinitionsPage;
	viewObjectEntriesPage: ViewObjectEntriesPage;
}>({
	addNewObjectRelationshipModalPage: async ({page}, use) => {
		await use(new AddNewObjectRelationshipModalPage(page));
	},
	editObjectDefinitionPage: async ({page}, use) => {
		await use(new EditObjectDefinitionPage(page));
	},
	editObjectDetailsPage: async ({page}, use) => {
		await use(new EditObjectDetailsPage(page));
	},
	editObjectValidationPage: async ({page}, use) => {
		await use(new EditObjectValidationPage(page));
	},
	editObjectViewPage: async ({page}, use) => {
		await use(new EditObjectViewPage(page));
	},
	modalAddObjectDefinitionPage: async ({page}, use) => {
		await use(new ModalAddObjectDefinitionPage(page));
	},
	modalAddObjectValidationPage: async ({page}, use) => {
		await use(new ModalAddObjectValidationPage(page));
	},
	modalEditObjectFolderPage: async ({page}, use) => {
		await use(new ModalEditObjectFolderPage(page));
	},
	modelBuilderDiagramPage: async ({page}, use) => {
		await use(new ModelBuilderDiagramPage(page));
	},
	modelBuilderLeftSidebarPage: async ({page}, use) => {
		await use(new ModelBuilderLeftSidebarPage(page));
	},
	modelBuilderObjectDefinitionNodePage: async ({page}, use) => {
		await use(new ModelBuilderObjectDefinitionNodePage(page));
	},
	modelBuilderRightSidebarPage: async ({page}, use) => {
		await use(new ModelBuilderRightSidebarPage(page));
	},
	objectDetailsPage: async ({page}, use) => {
		await use(new ObjectDetailsPage(page));
	},
	objectFieldsPage: async ({page}, use) => {
		await use(new ObjectFieldsPage(page));
	},
	objectLayoutsPage: async ({page}, use) => {
		await use(new ObjectLayoutsPage(page));
	},
	objectRelationshipsPage: async ({page}, use) => {
		await use(new ObjectRelationshipsPage(page));
	},
	objectValidationsPage: async ({page}, use) => {
		await use(new ObjectValidationsPage(page));
	},
	objectViewPage: async ({page}, use) => {
		await use(new ObjectViewPage(page));
	},
	systemSettingsPage: async ({page}, use) => {
		await use(new SystemSettingsPage(page));
	},
	viewObjectDefinitionsPage: async ({page}, use) => {
		await use(new ViewObjectDefinitionsPage(page));
	},
	viewObjectEntriesPage: async ({page}, use) => {
		await use(new ViewObjectEntriesPage(page));
	},
});

export {objectPagesTest};
