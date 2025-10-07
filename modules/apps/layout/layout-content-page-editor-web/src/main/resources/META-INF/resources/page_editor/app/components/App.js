/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {ClayIconSpriteContext} from '@clayui/icon';
import {getControlPanelSpritemap} from '@liferay/frontend-icons-web';
import PropTypes from 'prop-types';
import React from 'react';

import ConvertToPageTemplateModal from '../../plugins/convert_to_page_template_modal/components/ConvertToPageTemplateModal';
import {StyleBookContextProvider} from '../../plugins/page_design_options/hooks/useStyleBook';
import {INIT} from '../actions/types';
import {ClipboardContextProvider} from '../contexts/ClipboardContext';
import {CollectionActiveItemContextProvider} from '../contexts/CollectionActiveItemContext';
import {ControlsProvider} from '../contexts/ControlsContext';
import {DisplayPagePreviewItemContextProvider} from '../contexts/DisplayPagePreviewItemContext';
import {EditableProcessorContextProvider} from '../contexts/EditableProcessorContext';
import {FormValidationContextProvider} from '../contexts/FormValidationContext';
import {GlobalContextProvider} from '../contexts/GlobalContext';
import {
	KeyboardMovementContextProvider,
	useMovementSources,
} from '../contexts/KeyboardMovementContext';
import {LayoutKeyboardContextProvider} from '../contexts/LayoutKeyboardContext';
import {LocalConfigContextProvider} from '../contexts/LocalConfigContext';
import {ObjectDataContextProvider} from '../contexts/ObjectDataContext';
import {PortletContentContextProvider} from '../contexts/PortletContentContext';
import {ShortcutContextProvider} from '../contexts/ShortcutContext';
import {StoreContextProvider} from '../contexts/StoreContext';
import {WidgetsContextProvider} from '../contexts/WidgetsContext';
import AppHooks from '../hooks/app_hooks/index';
import {reducer} from '../reducers/index';
import {DragAndDropContextProvider} from '../utils/drag_and_drop/useDragAndDrop';
import CommonStylesManager from './CommonStylesManager';
import {DisplayPagePreviewItemSelector} from './DisplayPagePreviewItemSelector';
import DragPreviewWrapper from './DragPreviewWrapper';
import FocusManager from './FocusManager';
import ItemConfigurationSidebar from './ItemConfigurationSidebar';
import {LayoutBreadcrumbs} from './LayoutBreadcrumbs';
import LayoutViewport from './LayoutViewport';
import MultiSelectManager from './MultiSelectManager';
import ShortcutManager from './ShortcutManager';
import Sidebar from './Sidebar';
import Toolbar from './Toolbar';
import ExperienceCustomizerModal from './cms/ExperienceCustomizerModal';
import KeyboardMovementManager from './keyboard_movement/KeyboardMovementManager';
import KeyboardMovementPreview from './keyboard_movement/KeyboardMovementPreview';

export default function App({state}) {
	const initialState = reducer(state, {type: INIT});

	return (
		<ClayIconSpriteContext.Provider value={getControlPanelSpritemap()}>
			<StoreContextProvider initialState={initialState} reducer={reducer}>
				<ConvertToPageTemplateModal />

				<ExperienceCustomizerModal />

				<ControlsProvider>
					<CollectionActiveItemContextProvider>
						<DragAndDropContextProvider>
							<EditableProcessorContextProvider>
								<DisplayPagePreviewItemContextProvider>
									<WidgetsContextProvider>
										<AppHooks />

										<DisplayPagePreviewItemSelector dark />

										<DragPreviewWrapper />

										<FocusManager />

										<FormValidationContextProvider>
											<Toolbar />

											<KeyboardMovementContextProvider>
												<ClipboardContextProvider>
													<ShortcutContextProvider>
														<KeyboardManager />

														<KeyboardMovementPreview />

														<PortletContentContextProvider>
															<LocalConfigContextProvider>
																<GlobalContextProvider>
																	<CommonStylesManager />

																	<StyleBookContextProvider>
																		<ObjectDataContextProvider>
																			<Sidebar />

																			<LayoutKeyboardContextProvider>
																				<LayoutViewport />
																			</LayoutKeyboardContextProvider>

																			<LayoutBreadcrumbs />

																			<ItemConfigurationSidebar />
																		</ObjectDataContextProvider>
																	</StyleBookContextProvider>
																</GlobalContextProvider>
															</LocalConfigContextProvider>
														</PortletContentContextProvider>
													</ShortcutContextProvider>
												</ClipboardContextProvider>
											</KeyboardMovementContextProvider>
										</FormValidationContextProvider>
									</WidgetsContextProvider>
								</DisplayPagePreviewItemContextProvider>
							</EditableProcessorContextProvider>
						</DragAndDropContextProvider>
					</CollectionActiveItemContextProvider>
				</ControlsProvider>
			</StoreContextProvider>
		</ClayIconSpriteContext.Provider>
	);
}

App.propTypes = {
	state: PropTypes.object.isRequired,
};

function KeyboardManager() {
	const movementSources = useMovementSources();

	return movementSources.length ? (
		<KeyboardMovementManager />
	) : (
		<>
			<ShortcutManager />
			<MultiSelectManager />
		</>
	);
}
