/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

export {
	default as ClientExtension,
	IHTMLElementBuilder,
} from './ClientExtension';

export {default as CodeMirrorKeyboardMessage} from './code_mirror_keyboard_message/CodeMirrorKeyboardMessage';
export {default as FeatureIndicator} from './feature_indicator/FeatureIndicator';

export {default as FieldBase} from './forms/common/FieldBase';
export {RequiredMask} from './forms/common/FieldBase';
export {default as FieldFeedback} from './forms/common/FieldFeedback';

export {default as InputLocalized} from './forms/input/InputLocalized';

export {translationsNormalizer} from './forms/input/InputLocalized';

export {default as useId} from './hooks/useId';
export {default as useSessionState} from './hooks/useSessionState';
export {
	default as LearnMessage,
	ILearnResourceContext,
	LearnResourcesContext,
} from './learn_message/LearnMessage';

export {default as ManagementToolbar} from './management_toolbar/ManagementToolbar';

export {default as openAlertModal} from './modal/commands/openAlertModal';
export {default as openCategorySelectionModal} from './modal/commands/openCategorySelectionModal';
export {default as openConfirmModal} from './modal/commands/openConfirmModal';
export {default as openModal} from './modal/commands/openModal';
export {default as openPortletModal} from './modal/commands/openPortletModal';
export {default as openPortletWindow} from './modal/commands/openPortletWindow';
export {
	default as openSelectionModal,
	OpenSelectionModalSelectedItem,
} from './modal/commands/openSelectionModal';
export {default as openSimpleInputModal} from './modal/commands/openSimpleInputModal';
export {default as openTagSelectionModal} from './modal/commands/openTagSelectionModal';

export {ModalStatus} from './modal/components/Modal';

export {default as MultipleFileUploader} from './multiple_file_uploader/MultipleFileUploader';
export {
	type FileData,
	type UploadRequestCallback,
} from './multiple_file_uploader/types';

export {default as openToast} from './toast/openToast';
export type {OpenToastProps} from './toast/openToast';

export {
	Locale,
	Translations,
} from './translation_manager/TranslationAdminContent';
export {default as TranslationAdminItem} from './translation_manager/TranslationAdminItem';
export {default as TranslationAdminModal} from './translation_manager/TranslationAdminModal';

export {
	default as TranslationAdminSelector,
	TranslationProgress,
} from './translation_manager/TranslationAdminSelector';
export {
	activeLanguageIdsAtom,
	selectedLanguageIdAtom,
} from './translation_manager/state';
export {default as Treeview} from './treeview/Treeview';
